package com.aci.pipeline.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.core.util.ContactInfo;
import atg.repository.Repository;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.payment.creditcard.AciCreditCard;
import com.aci.payment.creditcard.AciCreditCardStatus;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;



public class ProcAddCreditCardInfoForFraud extends AbstractAciProcessor {
	
	private Repository mOrderRepository;
	private String mCreditCardDescriptorName;
	private static String defaultAddressValidationCode="Y";
	private static String defaultCVVValidationCode="Y";
	
	public String getCreditCardDescriptorName(){
		return mCreditCardDescriptorName;
	}
	
	public void setCreditCardDescriptorName(String pCreditCardDescriptorName){
		mCreditCardDescriptorName=pCreditCardDescriptorName;
	}
	
	public Repository getOrderRepository(){
		return mOrderRepository;
	}
	
	public void setOrderRepository(Repository pOrderRepository){
		this.mOrderRepository=pOrderRepository;
	}
	
	

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddCreditCardInfoForFraud:runAciProcess:Called");
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
		
		boolean hasCreditCards=pParams.isHasCreditCards();
		
		if(hasCreditCards){
			
			List<PaymentGroup> creditCards=null;
			creditCards=pParams.getCreditCards();
			if(creditCards==null){
				vlogError("No Credit card object found to process");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
			}
					
			AciCreditCard creditCard=(AciCreditCard)creditCards.get(0);
			double totalAmount=creditCard.getAmount();
			if(totalAmount!=0){
				pParams.setTotalPaymentGroupPrice(totalAmount);
			}
			else {
				pParams.setTotalPaymentGroupPrice(0.0);
			}
			
			String tokenNumber=creditCard.getTokenNumber();
			if(tokenNumber==null||tokenNumber.trim().equalsIgnoreCase("")){
				vlogError("Credit Card Token is Null");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
			}
			else {
				request.setField(FieldMappingConstants.ACCT_NUM,tokenNumber.trim());
				pParams.setCreditCardBillingAddressPresent(true);
				ContactInfo creditCardContactInfo=(ContactInfo)creditCard.getBillingAddress();
				if(creditCardContactInfo!=null){
					List<ContactInfo> creditCardContactInfos=new ArrayList<ContactInfo>();
					creditCardContactInfos.add(creditCardContactInfo);
					pParams.setCreditCardBillingInfos(creditCardContactInfos);		
				}
			}
				
			String creditCardExpirationYear=creditCard.getExpirationYear();
			String creditCardExpirationMonth=creditCard.getExpirationMonth();
			
			
			if((creditCardExpirationYear!=null&&creditCardExpirationMonth!=null)&& (!creditCardExpirationYear.trim().isEmpty()&&!creditCardExpirationMonth.trim().isEmpty())){
				request.setField(FieldMappingConstants.CARD_EXP_DT,AciUtils.formatCCExpirationDateToRedReadableForm(creditCardExpirationYear, creditCardExpirationMonth));
			}
			else {
				vlogError("Card Expiry date is absent. Please enter the card expiry date");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
			}
			
			boolean useAvsCode=aciConfiguration.isUseAvsCodeForFraud();
			boolean useCvvCode=aciConfiguration.isUseCvvCodeForFraud();
			addCreditCardStatusInfo(creditCard,request,useAvsCode,useCvvCode);
			String creditCardType=creditCard.getCreditCardType();
			String cardTypeCode=aciConfiguration.getCardTypeAciCodeMap().get(creditCardType);
			request.setField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE,cardTypeCode);
			request.setField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE_LIST,(""+cardTypeCode.charAt(0)).toUpperCase());
			request.setField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE_AMT_LIST,(""+totalAmount));
		}
		pParams.setRequest(request);
		vlogDebug("ProcAddCreditCardInfoForFraud:runAciProcess:End");
		return SUCCESS;
	}
	
	
	private void addCreditCardStatusInfo(AciCreditCard creditCard, LPTransaction request,boolean useAvsCodeForFraud, boolean useCvvCodeForFraud){
		vlogDebug("ProcAddCreditCardInfoForFraud:addCreditCardStatusInfo:Start");
		
		AciCreditCardStatus ccAuthStatus=(AciCreditCardStatus)creditCard.getAuthorizationStatus().get(0);
		boolean isAuthSuccess=ccAuthStatus.getTransactionSuccess();
		vlogDebug("ProcAddCreditCardInfoForFraud:addCreditCardStatusInfo:Is auth successful:"+ccAuthStatus);
		request.setField(FieldMappingConstants.CARD_SEC_IND_CD,"1");
		if(isAuthSuccess){
			request.setField(FieldMappingConstants.RSP_CD,"00");
		}
		else{
			String authErrorMessage=ccAuthStatus.getErrorMessage();
			if(authErrorMessage!=null&&!authErrorMessage.trim().isEmpty()){
				request.setField(FieldMappingConstants.RSP_CD,"96");
			}
			else {
				request.setField(FieldMappingConstants.RSP_CD,"05");
			}
		}
		
		if(useAvsCodeForFraud){
			String avsCode=ccAuthStatus.getAvsCode();
			if(!StringUtils.isEmpty(avsCode)){
				request.setField(FieldMappingConstants.RSP_AVS_CD,avsCode);
			}
			else {
				request.setField(FieldMappingConstants.RSP_AVS_CD,defaultAddressValidationCode);
			}
		}
		else {
			request.setField(FieldMappingConstants.RSP_AVS_CD,defaultAddressValidationCode);
		}
		if(useCvvCodeForFraud){
			String cvvCode=ccAuthStatus.getCvvVerificationCode();
			if(!StringUtils.isEmpty(cvvCode)){
				request.setField(FieldMappingConstants.RSP_SEC_CD,cvvCode);
			}
			else {
				request.setField(FieldMappingConstants.RSP_SEC_CD,defaultCVVValidationCode);
			}
		}
		else {
			request.setField(FieldMappingConstants.RSP_SEC_CD,defaultCVVValidationCode);
		}
		
		vlogDebug("ProcAddCreditCardInfoForFraud:addCreditCardStatusInfo:End");
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException{
		
	}
	

		
}
