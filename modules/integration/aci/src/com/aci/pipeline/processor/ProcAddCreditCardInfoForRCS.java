package com.aci.pipeline.processor;

import java.util.ArrayList;
import java.util.List;

import atg.core.util.StringUtils;

import atg.core.util.ContactInfo;
import atg.repository.RepositoryException;


import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;



public class ProcAddCreditCardInfoForRCS extends AbstractAciProcessor {
	

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddCreditCardInfoForToken.runAciProcess:Called");
		
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
		
		AciCreditCardInfo ccBean=pParams.getAciCreditCardInfo();
		if(ccBean==null){
			vlogError("Credit Card Info is Null");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
		
		addCardInformation(request,pParams);
		
		String creditCardExpirationYear=ccBean.getExpirationYear();
		String creditCardExpirationMonth=ccBean.getExpirationMonth();
		if((creditCardExpirationYear!=null&&creditCardExpirationMonth!=null)&& (!creditCardExpirationYear.trim().isEmpty()&&!creditCardExpirationMonth.trim().isEmpty())){
			request.setField(FieldMappingConstants.CARD_EXP_DT,AciUtils.formatCCExpirationDateToRedReadableForm(creditCardExpirationYear, creditCardExpirationMonth));
		}
		else {
			vlogError("Card Expiry date is absent. Please enter the card expiry date");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
		String cardVerificationNumber=ccBean.getSecurityCode();
		if(cardVerificationNumber!=null){
			cardVerificationNumber=cardVerificationNumber.trim();
		}
		if(StringUtils.isEmpty(cardVerificationNumber)){
			vlogInfo("Transaction without card verification number");
		}
		else {
			if(StringUtils.isNumericOnly(cardVerificationNumber)){
				request.setField(FieldMappingConstants.CARD_SEC_CD,cardVerificationNumber);
				request.setField(FieldMappingConstants.CARD_SEC_IND_CD,"1");
			}
			else {
				vlogInfo("Malformed card verification number present");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
			}
			
		}
		pParams.setRequest(request);
		vlogDebug("ProcAddCreditCardInfoForToken.runAciProcess:End");
		return SUCCESS;
	}
	
	private void addCardInformation(LPTransaction request, AciPipelineProcessParam pParams) throws AciPipelineException{
		AciConfiguration configuration=pParams.getAciConfiguration();
		String tokenizeActionCode=configuration.getTokenizeTransactionAction();
		String authActionCode=configuration.getAuthorizeTransactionAction();
		request.setField(FieldMappingConstants.AUTOSTANDALONE_TOKEN,"0");
		if(request.getField(FieldMappingConstants.ACT_CD).equalsIgnoreCase(tokenizeActionCode)){
			AciCreditCardInfo info=pParams.getAciCreditCardInfo();
			String ccNumber=info.getCreditCardNumber();
			if(ccNumber==null||ccNumber.trim().equalsIgnoreCase("")){
				vlogError("Credit Card Number is Null");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
			}
			else {
					request.setField(FieldMappingConstants.ACCT_NUM,ccNumber.trim());
					pParams.setCreditCardBillingAddressPresent(true);
					ContactInfo creditCardContactInfo=(ContactInfo)info.getBillingAddress();
					if(creditCardContactInfo!=null){
						List<ContactInfo> creditCardContactInfos=new ArrayList<ContactInfo>();
						creditCardContactInfos.add(creditCardContactInfo);
						pParams.setCreditCardBillingInfos(creditCardContactInfos);		
					}
			}
			
		}
		else if(request.getField(FieldMappingConstants.ACT_CD).equalsIgnoreCase(authActionCode)){
			AciCreditCardInfo info=pParams.getAciCreditCardInfo();
			String ccNumber=info.getTokenNumber();
			if(ccNumber==null||ccNumber.trim().equalsIgnoreCase("")){
				vlogError("Token Number is Null");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
			}
			else {	
					request.setField(FieldMappingConstants.AMT,AciUtils.convertAmountToRedReadableFormat((info.getAmount()),2));
					request.setField(FieldMappingConstants.TOKEN_ID,ccNumber.trim());
					pParams.setCreditCardBillingAddressPresent(true);
					ContactInfo creditCardContactInfo=(ContactInfo)info.getBillingAddress();
					if(creditCardContactInfo!=null){
						List<ContactInfo> creditCardContactInfos=new ArrayList<ContactInfo>();
						creditCardContactInfos.add(creditCardContactInfo);
						pParams.setCreditCardBillingInfos(creditCardContactInfos);		
					}
			}
			
		}
		
	}
	
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException{
		
	}
	

		
}
