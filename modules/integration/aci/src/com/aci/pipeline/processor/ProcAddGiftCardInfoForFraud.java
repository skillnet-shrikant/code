package com.aci.pipeline.processor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.core.util.ContactInfo;
import atg.payment.PaymentStatusImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcAddGiftCardInfoForFraud extends AbstractAciProcessor {
	
	private Repository mOrderRepository;
	private String mGiftCardDescriptorName;
	private String mGiftCardNumberPropertyName;
	private static String defaultAddressValidationCode="Y";
	private static String defaultCVVValidationCode="Y";

	public String getGiftCardNumberPropertyName() {
		return mGiftCardNumberPropertyName;
	}

	public void setGiftCardNumberPropertyName(String pGiftCardNumberPropertyName) {
		mGiftCardNumberPropertyName = pGiftCardNumberPropertyName;
	}

	public String getGiftCardDescriptorName(){
		return mGiftCardDescriptorName;
	}
	
	public void setGiftCardDescriptorName(String pGiftCardDescriptorName){
		mGiftCardDescriptorName=pGiftCardDescriptorName;
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
		vlogDebug("ProcAddGiftCardInfoForFraud:runAciProcess:Called");
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_GIFTCARD_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_GIFTCARD_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_GIFTCARD_INFO, true);
		}
		
		boolean hasCreditCards=pParams.isHasCreditCards();
		boolean hasGiftCards=pParams.isHasGifftCards();
		boolean hasGCBillingAddress=aciConfiguration.isGiftCardHasBillingAddress();
		
		if(hasCreditCards && hasGiftCards){
			List<PaymentGroup> giftCards=pParams.getGiftCards();
			if(giftCards==null){
				vlogError("No Gift Cards objects found to process");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_GIFTCARD_INFO, true);
			}
			addGiftCardInfo(giftCards,request,pParams,false,hasGCBillingAddress);
		}
		else if(!hasCreditCards&&hasGiftCards){
			List<PaymentGroup> giftCards=pParams.getGiftCards();
			if(giftCards==null){
				vlogError("No Gift Cards objects found to process");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_GIFTCARD_INFO, true);
			}
			addGiftCardInfo(giftCards,request,pParams,true,hasGCBillingAddress);
		}
		else if(hasCreditCards && !hasGiftCards){
			
		}
		pParams.setRequest(request);
		vlogDebug("ProcAddGiftCardInfoForFraud:runAciProcess:End");
		return SUCCESS;
	}
	
	private void addGiftCardInfo(List<PaymentGroup> giftCards, LPTransaction request,AciPipelineProcessParam pParams,boolean primaryPaymentGroup,boolean hasBillingAddress) throws AciPipelineException{
		String cardType="";
		String cardTypeList="";
		String cardAmountList="";
		if(primaryPaymentGroup){
			PaymentGroup firstCard=giftCards.get(0);
			double totalAmount=firstCard.getAmount();
			if(totalAmount!=0){
				pParams.setTotalPaymentGroupPrice(totalAmount);
			}
			else {
				pParams.setTotalPaymentGroupPrice(0.0);
			}
			String gcPgId=firstCard.getId();
			RepositoryItem gcItem=getGiftCard(gcPgId);
			String gcNumber=getGiftCardNumber(gcItem);
			if(gcNumber!=null&&!gcNumber.trim().isEmpty()){
				request.setField(FieldMappingConstants.ACCT_NUM,gcNumber.trim());
				int calendarYear=Calendar.getInstance().get(Calendar.YEAR);
				int calendarMonth=Calendar.getInstance().get(Calendar.MONTH);
				String modifiedCalendarYear=Integer.toString(calendarYear+1);
				String modifiedCalendarMonth=Integer.toString(calendarMonth+1);
				request.setField(FieldMappingConstants.CARD_EXP_DT,AciUtils.formatCCExpirationDateToRedReadableForm(modifiedCalendarYear,modifiedCalendarMonth));
				if(hasBillingAddress){
					pParams.setGiftCardBillingAddressPresent(true);
					ContactInfo gcContactInfo=getGiftCardBillingAddress(gcItem);
					List<ContactInfo> gcContactInfos=new ArrayList<ContactInfo>();
					gcContactInfos.add(gcContactInfo);
					pParams.setGiftCardBillingInfos(gcContactInfos);
				}
				else {
					pParams.setGiftCardBillingAddressPresent(false);
				}
				addGiftCardStatusInfo(firstCard,request);
				cardType="G";
			}
		}
		else {
			cardType=request.getField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE);
			cardAmountList=request.getField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE_AMT_LIST)+"|";
			cardTypeList=request.getField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE_LIST)+"|";
		}
		
		for(PaymentGroup pg:giftCards){
			cardTypeList=cardTypeList+"G"+"|";
			cardAmountList=cardAmountList+""+pg.getAmount()+"|";
		}
		cardTypeList=cardTypeList.substring(0,cardTypeList.lastIndexOf("|"));
		cardAmountList=cardAmountList.substring(0,cardAmountList.lastIndexOf("|"));
		request.setField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE, cardType);
		request.setField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE_LIST, cardTypeList);
		request.setField(FieldMappingConstants.CUSTOM_PAYMENT_TYPE_AMT_LIST, cardAmountList);
	}
	
	private void addGiftCardStatusInfo(PaymentGroup giftCard, LPTransaction request){
		vlogDebug("ProcAddGiftCardInfoForFraud:addGiftCardStatusInfo:Start");
		
		PaymentStatusImpl pgStatus=(PaymentStatusImpl)giftCard.getAuthorizationStatus().get(0);
		boolean isAuthSuccess=pgStatus.getTransactionSuccess();
		vlogDebug("ProcAddGiftCardInfoForFraud:addGiftCardStatusInfo:Is auth successful:"+isAuthSuccess);
		if(isAuthSuccess){
			request.setField(FieldMappingConstants.RSP_CD,"00");
		}
		else{
			request.setField(FieldMappingConstants.RSP_CD,"96");
		}
		request.setField(FieldMappingConstants.CARD_SEC_IND_CD,"1");
		request.setField(FieldMappingConstants.RSP_AVS_CD,defaultAddressValidationCode);
		request.setField(FieldMappingConstants.RSP_SEC_CD,defaultCVVValidationCode);
		
		
		
		vlogDebug("ProcAddGiftCardInfoForFraud:addGiftCardStatusInfo:End");
	}

	protected RepositoryItem getGiftCard(String payGroupId)throws AciPipelineException{
		vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCard:Start");
		try {
			RepositoryItem giftCard=getOrderRepository().getItem(payGroupId,getGiftCardDescriptorName());
			vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCard:End");
			return giftCard;
		}
		catch(Exception ex){
			vlogError("Error retrieving gift card. Gift Card  is required for transaction");
			vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCard:End");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
	}
	
	protected String getGiftCardNumber(RepositoryItem giftCard) throws AciPipelineException{
		vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCardNumber:Start");
		try {
			String gcNumber=(String)giftCard.getPropertyValue(getGiftCardNumberPropertyName());
			vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCardNumber:End");
			return gcNumber;
		}
		catch(Exception ex){
			vlogError("Error retrieving gift card number. Gift Card Number is required for transaction");
			vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCardNumber:End");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
	}
	
	protected ContactInfo getGiftCardBillingAddress(RepositoryItem giftCard) throws AciPipelineException{
		vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCardBillingAddressr:Start");
		try {
			ContactInfo contactInfo=new ContactInfo();
			String firstName = (String)giftCard.getPropertyValue("firstName");
			String middleName= (String)giftCard.getPropertyValue("middleName");
			String lastName=(String)giftCard.getPropertyValue("lastName");
			String address1=(String)giftCard.getPropertyValue("address1");
			String address2=(String)giftCard.getPropertyValue("address2");
			String address3=(String)giftCard.getPropertyValue("address3");
			String city=(String)giftCard.getPropertyValue("city");
			String state=(String)giftCard.getPropertyValue("stateAddress");
			String country=(String)giftCard.getPropertyValue("country");
			String phoneNumber=(String)giftCard.getPropertyValue("phoneNumber");
			String email=(String)giftCard.getPropertyValue("email");
			String postalCode=(String)giftCard.getPropertyValue("postalCode");
			contactInfo.setFirstName(firstName);
			contactInfo.setLastName(lastName);
			contactInfo.setMiddleName(middleName);
			contactInfo.setAddress1(address1);
			contactInfo.setAddress2(address2);
			contactInfo.setAddress3(address3);
			contactInfo.setCity(city);
			contactInfo.setState(state);
			contactInfo.setCountry(country);
			contactInfo.setPostalCode(postalCode);
			contactInfo.setPhoneNumber(phoneNumber);
			contactInfo.setEmail(email);
			vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCardBillingAddressr:End");
			return contactInfo;
			
		}
		catch(Exception ex){
			vlogError("Gift Card billing address retrieval error. Billing address is required for transaction");
			vlogDebug("ProcAddGiftCardInfoForFraud:getGiftCardBillingAddressr:End");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
	}
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}
		
}
