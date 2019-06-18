package com.listrak.service.email;



import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.listrak.service.constants.ListrakConstants;
import com.listrak.service.email.client.SegmentationFieldValue;
import com.listrak.service.email.client.SendMessageRequest;
import com.listrak.service.exception.ListrakException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CreditCard;
import atg.commerce.order.GiftCertificate;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.ContactInfo;
import atg.repository.Repository;
import atg.repository.RepositoryItem;





public class OrderEmailService extends EmailService {

	
	protected Repository mProductRepository;
	protected Repository mStoreRepository;
	private String mSiteHttpServerName;
	private boolean mUseHttps;

	public boolean isUseHttps(){
		return mUseHttps;
	}
	
	public void setUseHttps(boolean pUseHttps){
		mUseHttps=pUseHttps;
	}
	
	/* siteHttpServerName */
	public String getSiteHttpServerName() {
		return mSiteHttpServerName;
	}
	public void setSiteHttpServerName(String pSiteHttpServerName) {
		mSiteHttpServerName = pSiteHttpServerName;
	}
	/**
	 * @return the repository
	 */
	public Repository getProductRepository() {
		return mProductRepository;
	}

	/**
	 * @param pRepository the repository to set
	 */
	public void setProductRepository(Repository pProductRepository) {
		mProductRepository = pProductRepository;
	}
	
	
	public Repository getStoreRepository() {
		return mStoreRepository;
	}

	public void setStoreRepository(Repository pStoreRepository) {
		mStoreRepository = pStoreRepository;
	}

	public void sendWebOrderConfirmationEmail(OrderImpl order,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendWebOrderConfirmationEmail:Start");
		try {
			
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.WEB_ORDER_CONFIRM_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendWebOrderConfirmationEmail:End");
	}
	
	public void sendBopisOrderConfirmationEmail(OrderImpl order, String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendBopisOrderConfirmationEmail:Start");
		try {
			
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_ORDER_CONFIRM_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendBopisOrderConfirmationEmail:End");
	}
	
	public void sendBopisAltOrderConfirmationEmail(OrderImpl order,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendBopisOrderConfirmationEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisAlternateInfoDetails(order, sfvList);
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_ORDER_CONFIRM_ALTEMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendBopisOrderConfirmationEmail:End");
	}
	
	public void sendOrderPickupConfirmationEmail(OrderImpl order, boolean isShowAltPersonDetails,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderPickupConfirmationEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS__PICKUP_CONF);
			if(isShowAltPersonDetails){
				populateBopisAlternateInfoDetails(order, sfvList);
			}
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_ORDER_PICKUP_CONFIRM_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderPickupConfirmationEmail:End");
	}
	
	
	public void sendOrderPickupConfirmationAltEmail(OrderImpl order, String altEmail,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderPickupConfirmationEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(altEmail);
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS__PICKUP_CONF);
			populateBopisAlternateInfoDetails(order, sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_ORDER_PICKUP_CONFIRM_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderPickupConfirmationEmail:End");
	}
	
	
	public void sendOrderReadyPickupCSEmail(OrderImpl order, boolean includeAltPersonDetails,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupCSEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_INI);
			if(includeAltPersonDetails){
				populateBopisAlternateInfoDetails(order, sfvList);
			}
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_READY_TO_PICKUP_CS_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		vlogInfo("OrderEmailService:sendOrderReadyPickupCSEmail:End");
		
	}
	
	public void sendOrderReadyPickupCSAltEmail(OrderImpl order,String altEmail,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupCSAltEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(altEmail);
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_INI);
			populateBopisAlternateInfoDetails(order, sfvList);
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_READY_PICKUP_CS_ALTEMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupCSAltEmail:End");
	}
	
	public void sendOrderReadyPickupOutEmail(OrderImpl order,boolean includeAltPersonDetails,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupOutEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_INI);
			if(includeAltPersonDetails){
				populateBopisAlternateInfoDetails(order, sfvList);
			}
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_READY_PICKUP_OUT_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupOutEmail:End");
	}
	
	public void sendOrderReadyPickupFACEmail(OrderImpl order,boolean includeAltPersonDetails,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupFACEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_INI);
			if(includeAltPersonDetails){
				populateBopisAlternateInfoDetails(order, sfvList);
			}
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_READY_TO_PICKUP_FAC_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupFACEmail:End");
	}
	
	public void sendOrderReadyPickupOutAltEmail(OrderImpl order,String altEmail,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupOutAltEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(altEmail);
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_INI);
			populateBopisAlternateInfoDetails(order, sfvList);
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_READY_PICKUP_OUT_ALTEMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupOutAltEmail:End");
	}

	public void sendOrderReadyPickupFACAltEmail(OrderImpl order,String altEmail,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupFACAltEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(altEmail);
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_INI);
			populateBopisAlternateInfoDetails(order, sfvList);
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.BOPIS_READY_PICKUP_FAC_ALTEMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupFACAltEmail:End");
	}	
	public void sendOrderReadyPickupRemEmail(OrderImpl order, boolean isShowAltPersonDetails,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupRemEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_REM_1);
			if(isShowAltPersonDetails){
				populateBopisAlternateInfoDetails(order, sfvList);
			}
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(getEmailType(order,false,1));
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupRemEmail:End");
	}
	
	public String getEmailType (OrderImpl pOrder, boolean bAlternatePerson, int reminder) {
		String emailType = ListrakConstants.BOPIS_PICKUP_REM_EMAIL;
		List<ShippingGroup> spgs=pOrder.getShippingGroups();		
		boolean isYardEntrance=false;
		boolean isCSDesk=false;
		boolean isFireArmsCounter=false;
		for(ShippingGroup spg:spgs){
			if(spg instanceof HardgoodShippingGroup){
				String pickupInstruction=(String)spg.getSpecialInstructions().get("pickupinstructions");
				if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
					if(pickupInstruction.toLowerCase().contains(("Outside Yard Entrance").toLowerCase())){
						if(reminder==1) {
							emailType = ListrakConstants.BOPIS_PICKUP_YARD_REM_EMAIL;
							if(bAlternatePerson) {
								emailType = ListrakConstants.BOPIS_PICKUP_YARD_REM_ALTEMAIL;
							}
							break;
						} else {
							emailType = ListrakConstants.BOPIS_PICKUP_YARD_REM2_EMAIL;
							if(bAlternatePerson) {
								emailType = ListrakConstants.BOPIS_PICKUP_YARD_REM2_ALTEMAIL;
							}
							break;
						}
						
					}
				}
				if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
					if(pickupInstruction.toLowerCase().contains(("Customer Service Desk").toLowerCase())){
						if(reminder == 1) {
							emailType = ListrakConstants.BOPIS_PICKUP_REM_EMAIL;
							if(bAlternatePerson) {
								emailType = ListrakConstants.BOPIS_PICKUP_REM_ALTEMAIL;
							}
							break;							
						} else {
							emailType = ListrakConstants.BOPIS_PICKUP_REM2_EMAIL;
							if(bAlternatePerson) {
								emailType = ListrakConstants.BOPIS_PICKUP_REM2_ALTEMAIL;
							}
							break;							
						}
					}
				}
				if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
					if(pickupInstruction.toLowerCase().contains(("Firearms Counter").toLowerCase())){
						if(reminder == 1) {
							emailType = ListrakConstants.BOPIS_PICKUP_FAC_REM_EMAIL;
							if(bAlternatePerson) {
								emailType = ListrakConstants.BOPIS_PICKUP_FAC_REM_ALTEMAIL;
							}						
							break;							
						} else {
							emailType = ListrakConstants.BOPIS_PICKUP_FAC_REM2_EMAIL;
							if(bAlternatePerson) {
								emailType = ListrakConstants.BOPIS_PICKUP_FAC_REM2_ALTEMAIL;
							}						
							break;							
						}
					}
				}
			}
			
		}
		logDebug("For order " + pOrder.getId() + " returning emailType " + emailType);
		return emailType;
	}
	
	
	public void sendOrderReadyPickupRem2Email(OrderImpl order,boolean isShowAltPersonDetails,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupRem2Email:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_REM_2);
			if(isShowAltPersonDetails){
				populateBopisAlternateInfoDetails(order, sfvList);
			}
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(getEmailType(order,false,2));
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		vlogInfo("OrderEmailService:sendOrderReadyPickupRem2Email:End");
		
	}
	
	public void sendOrderShippedEmail(OrderImpl order,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderShippedEmail:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateTrackingNumbers(order, sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.ORDER_SHIPPED_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		vlogInfo("OrderEmailService:sendOrderShippedEmail:End");
		
	}
	
	public void sendOrderShippedEmail(OrderImpl order, List<CommerceItem> pItemsShipped,Map<String,String> pTrackingNumberMap,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderShippedEmail:with multiple params:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(getEmail(order));
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList,pItemsShipped,pTrackingNumberMap);
			populatePriceInfoDetails(order, sfvList);
			populateTrackingNumbers(order, sfvList,pItemsShipped,pTrackingNumberMap);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(ListrakConstants.ORDER_SHIPPED_EMAIL);
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderShippedEmail:with multiple params:End");
	}
	
	public void sendOrderReadyPickupRemAltEmail(OrderImpl order,String altEmail,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupRemAltEmails:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(altEmail);
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_REM_1);
			populateBopisAlternateInfoDetails(order, sfvList);
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(getEmailType(order,true,1));
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupRemAltEmails:End");
	}
	
	public void sendOrderReadyPickupRem2AltEmail(OrderImpl order,String altEmail,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:sendOrderReadyPickupRem2AltEmails:Start");
		try {
			ObjectMapper mapper=new ObjectMapper();
			SendMessageRequest mr=new SendMessageRequest();
			mr.setEmailAddress(altEmail);
			List<SegmentationFieldValue> sfvList= new ArrayList<SegmentationFieldValue>();
			populateOrderNumber(order, sfvList);
			populateContactEmail(order, sfvList);
			populatePurchaseDate(order, sfvList);
			populateShippingInfo(order, sfvList);
			populateBillingInfo(order, sfvList,purchaserName);
			populateCartItems(order, sfvList);
			populatePriceInfoDetails(order, sfvList);
			populateBopisStoreInfoDetails(order, sfvList);
			populateBopisPickupDetails(order, sfvList,ListrakConstants.BOPIS_PICKUP_REM_2);
			populateBopisAlternateInfoDetails(order, sfvList);
			populateCustomerDetails(sfvList);
			mr.setSegmentationFieldValues(sfvList);
			String jsonString=mapper.writeValueAsString(mr);
			HashMap<String,String> headers=new HashMap<String,String>();
			String urlExtension=getEmailTypeToCode().get(getEmailType(order,true,2));
			sendEmail(jsonString,headers,urlExtension);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
			
		vlogInfo("OrderEmailService:sendOrderReadyPickupRem2AltEmails:End");
	}
	
	
	protected void populateOrderNumber(OrderImpl order,List<SegmentationFieldValue> sfvList) throws ListrakException{
		vlogInfo("OrderEmailService:populateOrderNumber:Start");
		SegmentationFieldValue sfv= new SegmentationFieldValue();
		if(order!=null){
			RepositoryItem repoItem= order.getRepositoryItem();
			String orderNumber=(String)repoItem.getPropertyValue(ListrakConstants.ORDER_NUMBER_PROPERTY_NAME);
			if(orderNumber!=null){
				sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.ORDER_NUMBER));
				sfv.setValue(orderNumber.trim());
				sfvList.add(sfv);
			}
			else {
				throw new ListrakException(ListrakConstants.ORDER_NUMBER_NULL_EXCEPTION);
			}
		}
		else {
			throw new ListrakException(ListrakConstants.ORDER_NULL_EXCEPTION);
		}
		vlogInfo("OrderEmailService:populateOrderNumber:End");
	}
	
	protected String getEmail(OrderImpl order) throws ListrakException{
		vlogInfo("OrderEmailService:getEmail:Start");
		RepositoryItem repoItem= order.getRepositoryItem();
		String email=(String)repoItem.getPropertyValue(ListrakConstants.CONTACT_EMAIL_PROPERTY_NAME);
		if(email!=null){
			vlogInfo("OrderEmailService:getEmail:End");
			return email;
		}
		else {
			throw new ListrakException(ListrakConstants.EMAIL_EMPTY_EXCEPTION);
		}
		
	}
	
	protected void populateContactEmail(OrderImpl order,List<SegmentationFieldValue> sfvList)throws ListrakException{
		vlogInfo("OrderEmailService:populateContactEmail:Start");
		SegmentationFieldValue sfv= new SegmentationFieldValue();
		if(order!=null){
			String email=getEmail(order);
			if(email!=null && !email.trim().isEmpty()){
				sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.EMAIL));
				sfv.setValue(email.trim());
				sfvList.add(sfv);
			}
			else {
				throw new ListrakException(ListrakConstants.EMAIL_EMPTY_EXCEPTION);
			}
		}
		else {
			throw new ListrakException(ListrakConstants.ORDER_NULL_EXCEPTION);
		}
		vlogInfo("OrderEmailService:populateContactEmail:End");
	}
	
	
	protected void populatePurchaseDate(OrderImpl order,List<SegmentationFieldValue> sfvList) throws ListrakException{
		vlogInfo("OrderEmailService:populatePurchaseDate:Start");
		SegmentationFieldValue sfv= new SegmentationFieldValue();
		if(order!=null){
			RepositoryItem repoItem= order.getRepositoryItem();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
			Timestamp submitted_date=(Timestamp)repoItem.getPropertyValue(ListrakConstants.SUBMITTED_DATE);
			String purchaseDate=sdf.format(submitted_date);
			if(purchaseDate!=null){
				sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.PURCHASE_DATE));
				sfv.setValue(purchaseDate.trim());
				sfvList.add(sfv);
			}
			else {
				throw new ListrakException(ListrakConstants.PURCHASEDATE_NULL_EXCEPTION);
			}
		}
		else {
			throw new ListrakException(ListrakConstants.ORDER_NULL_EXCEPTION);
		}
		vlogInfo("OrderEmailService:populatePurchaseDate:End");
	}
	
	protected void populateShippingInfo(OrderImpl order,List<SegmentationFieldValue> sfvList) throws ListrakException{
		vlogInfo("OrderEmailService:populateShippingInfo:Start");
		if(order!=null){
			List<ShippingGroup> sgs=order.getShippingGroups();
			HardgoodShippingGroup hsg=new HardgoodShippingGroup();
			for(ShippingGroup sg:sgs){
				if(sg instanceof HardgoodShippingGroup){
					hsg=(HardgoodShippingGroup)sg;
					break;
				}
				else {
					vlogDebug("Electronic shipping group found");
					// TODO code for electronic shipping group
				}
			}
			String shippingMethod=hsg.getShippingMethod();
			if(shippingMethod!=null){
				if(shippingMethod.equalsIgnoreCase("BOPIS")){
					shippingMethod="Pickup In Store";
				}
			}
			else {
				shippingMethod="Standard";
			}
			
			if(shippingMethod!=null){
				SegmentationFieldValue sgMethod= new SegmentationFieldValue();
				sgMethod.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_METHOD));
				sgMethod.setValue(shippingMethod);
				sfvList.add(sgMethod);
			}
			populateShippingAddress(sfvList,hsg);

		}
		else {
			throw new ListrakException(ListrakConstants.ORDER_NULL_EXCEPTION);
		}
		vlogInfo("OrderEmailService:populateShippingInfo:End");
	}
	
	protected void populateBillingInfo(OrderImpl order,List<SegmentationFieldValue> sfvList,String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:populateBillingInfo:Start");
		String paymentSection="";
		if(order!=null){
			List<PaymentGroup> pgs=order.getPaymentGroups();
			CreditCard cpg=new CreditCard();
			boolean isOtherPgPresent=false;
			boolean isCCPresent=false;
			PaymentGroup gpg=new GiftCertificate();
			
			for(PaymentGroup pg:pgs){
				if(pg instanceof CreditCard){
					cpg=(CreditCard)pg;
					isCCPresent=true;
				}
				else {
					gpg=pg;
					isOtherPgPresent=true;
				}
			}
			if(isCCPresent){
				String paymentMethod=cpg.getPaymentMethod();
				// Payment method to pass to Listrak
				
				
				String paymentType=cpg.getCreditCardType();
				double paymentAmount=cpg.getAmount();
				String paymentAmountString="$0.00";
				
				
				if(paymentAmount!=0){
					paymentAmountString="$"+formatDoubleValueToTwodecimals(paymentAmount);
				}
				//CreditCardTypeTo pass to Listrak
				if(paymentType!=null && ! paymentType.isEmpty()){
					paymentType=paymentType.substring(0, 1).toUpperCase() + paymentType.substring(1);
					paymentSection=paymentType+"	"+paymentAmountString;
					
				}
				
				String lastFourCardNumber=cpg.getCreditCardNumber();
				//Last four to send to Listrak
				
				populateBillingAddress(sfvList,cpg,purchaserName);
				
				if(isOtherPgPresent){
					String gcMethod=gpg.getPaymentMethod();
					// Payment method to pass to Listrak
					double gcAmount=gpg.getAmount();
					String gcAmountString="$0.00";
					if(gcAmount!=0){
						gcAmountString="$"+formatDoubleValueToTwodecimals(gcAmount);
					}
					
					String cardNumber=(String)((PaymentGroupImpl)gpg).getRepositoryItem().getPropertyValue(ListrakConstants.GIFT_CARD_NUMBER);
					if(cardNumber==null){
						cardNumber="";
					}
					//CardNumber to send to Listrak
					if(gcMethod!=null)
					{	gcMethod=gcMethod.substring(0, 1).toUpperCase() + gcMethod.substring(1);
						paymentSection=paymentSection+"<br />"+gcMethod+"	"+gcAmountString;					
					}
				}
				
				if(paymentMethod!=null&&!paymentMethod.isEmpty()){
					SegmentationFieldValue pMsfv= new SegmentationFieldValue();
					pMsfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.PAYMENT_METHOD));
					pMsfv.setValue(paymentSection);
					sfvList.add(pMsfv);
				}
				
			}
			else if(isOtherPgPresent){
				String paymentMethod=gpg.getPaymentMethod();
				// Payment method to pass to Listrak
				populateBillingAddress(sfvList,null,purchaserName);
				paymentSection="";
				double gcAmount=gpg.getAmount();
				String cardNumber=(String)((PaymentGroupImpl)gpg).getRepositoryItem().getPropertyValue(ListrakConstants.GIFT_CARD_NUMBER);
				//CardNumber to send to Listrak
				String gcAmountString="$0.00";
				if(gcAmount!=0){
					gcAmountString="$"+formatDoubleValueToTwodecimals(gcAmount);
				}
				if(paymentMethod!=null)
				{	paymentMethod=paymentMethod.substring(0, 1).toUpperCase() + paymentMethod.substring(1);
					paymentSection=paymentMethod+"	"+gcAmountString;					
				}
				
				
				if(paymentMethod!=null&&!paymentMethod.isEmpty()){
					SegmentationFieldValue pMsfv= new SegmentationFieldValue();
					pMsfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.PAYMENT_METHOD));
					pMsfv.setValue(paymentSection);
					sfvList.add(pMsfv);
				}
				
				

			}

		}
		else {
			throw new ListrakException(ListrakConstants.ORDER_NULL_EXCEPTION);
		}
		vlogInfo("OrderEmailService:populateBillingInfo:End");
	}
	
	protected void populateBillingAddress(List<SegmentationFieldValue> sfvList, CreditCard cpg, String purchaserName) throws ListrakException{
		vlogInfo("OrderEmailService:populateBillingAddress:Start");
		if(cpg!=null){
				
			if(cpg.getBillingAddress()!=null){
				String fname=cpg.getBillingAddress().getFirstName();
				if(fname!=null){
					SegmentationFieldValue pgFname= new SegmentationFieldValue();
					pgFname.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_FIRST_NAME));
					pgFname.setValue(fname);
					sfvList.add(pgFname);
				}
				else {
					// To do billing first name empty
					vlogDebug("Billing first name is empty");
				}
				String lname=cpg.getBillingAddress().getLastName();
				if(lname!=null){
					SegmentationFieldValue pgLname= new SegmentationFieldValue();
					pgLname.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_LAST_NAME));
					pgLname.setValue(lname);
					sfvList.add(pgLname);
				}
				else {
					// To do billing last name  is empty
					vlogDebug("Billing last name is empty");
				}
				
				String address1=cpg.getBillingAddress().getAddress1();
				if(address1!=null){
					SegmentationFieldValue pgAddr1= new SegmentationFieldValue();
					pgAddr1.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_STREET_ADD1));
					pgAddr1.setValue(address1);
					sfvList.add(pgAddr1);
				}
				else {
					vlogDebug("Billing Address1 is empty");
					// To do billing address1 is empty
				}
				String address2=cpg.getBillingAddress().getAddress2();
				if(address2!=null){
					SegmentationFieldValue pgAddr2= new SegmentationFieldValue();
					pgAddr2.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_STREET_ADD2));
					pgAddr2.setValue(address2);
					sfvList.add(pgAddr2);
				}
				else {
					vlogDebug("Billing Address2 is empty");
					//To do billing address2 is empty
				}
				String billingCity=cpg.getBillingAddress().getCity();
				if(billingCity!=null){
					SegmentationFieldValue pgCity= new SegmentationFieldValue();
					pgCity.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_STREET_CITY));
					pgCity.setValue(billingCity);
					sfvList.add(pgCity);
				}
				else {
					vlogDebug("Billing City is empty");
					// To do billing city is empty
				}
				String billingState=cpg.getBillingAddress().getState();
				if(billingState!=null){
					SegmentationFieldValue pgState= new SegmentationFieldValue();
					pgState.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_STREET_STATE));
					pgState.setValue(billingState);
					sfvList.add(pgState);
				}
				else {
					vlogDebug("Billing State is empty");
					// To do billing state is empty
				}
				String billingPostalCode=cpg.getBillingAddress().getPostalCode();
				if(billingPostalCode!=null){
					SegmentationFieldValue pgZip= new SegmentationFieldValue();
					pgZip.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_STREET_ZIP));
					pgZip.setValue(billingPostalCode);
					sfvList.add(pgZip);
				}
				else {
					vlogDebug("Billing Zipcode is empty");
					// To do billing zipcode is empty
				}
				String billingCountry=cpg.getBillingAddress().getCountry();
				if(billingCountry!=null){
					SegmentationFieldValue pgCountry= new SegmentationFieldValue();
					pgCountry.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_STREET_COUNTRY));
					pgCountry.setValue(billingCountry);
					sfvList.add(pgCountry);
				}
				else {
					vlogDebug("Billing country is empty");
					// To do billing country is empty
				}
				String billingPhone=(String)((ContactInfo)cpg.getBillingAddress()).getPhoneNumber();
				if(billingPhone!=null){
					SegmentationFieldValue billingPhoneSfv= new SegmentationFieldValue();
					billingPhoneSfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_PHONE));
					billingPhoneSfv.setValue(billingPhone);
					sfvList.add(billingPhoneSfv);
				}
				else {
					vlogDebug("Billing phone is empty");
					// To do billing phone is empty
				}
			}
			else {
				String fname=purchaserName;
				if(fname!=null){
					SegmentationFieldValue pgFname= new SegmentationFieldValue();
					pgFname.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_FIRST_NAME));
					pgFname.setValue(fname);
					sfvList.add(pgFname);
				}
				else {
					// To do billing first name empty
					vlogDebug("Billing first name is empty");
				}
			}
		}
		else {
			String fname=purchaserName;
			if(fname!=null){
				SegmentationFieldValue pgFname= new SegmentationFieldValue();
				pgFname.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.BILLING_FIRST_NAME));
				pgFname.setValue(fname);
				sfvList.add(pgFname);
			}
			else {
				// To do billing first name empty
				vlogDebug("Billing first name is empty");
			}
		}
		vlogInfo("OrderEmailService:populateBillingAddress:End");
	}
	
	protected void populateShippingAddress(List<SegmentationFieldValue> sfvList,HardgoodShippingGroup sg){
		vlogInfo("OrderEmailService:populateShippingAddress:Start");
		if(sg!=null){
			if(sg.getShippingAddress()!=null){
				
				String shippingFname=sg.getShippingAddress().getFirstName();
				if(shippingFname!=null){
					SegmentationFieldValue sgFname= new SegmentationFieldValue();
					sgFname.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_FNAME));
					sgFname.setValue(shippingFname);
					sfvList.add(sgFname);
				}
				else {
					vlogDebug("Shipping First Name is empty");
				}
				String shippingLname=sg.getShippingAddress().getLastName();
				if(shippingLname!=null){
					SegmentationFieldValue sgLname= new SegmentationFieldValue();
					sgLname.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_LNAME));
					sgLname.setValue(shippingLname);
					sfvList.add(sgLname);
				}
				else {
					vlogDebug("Shipping last Name is empty");
				}
				
				String shippingAddress1=sg.getShippingAddress().getAddress1();
				if(shippingAddress1!=null){
					SegmentationFieldValue sgAddr1= new SegmentationFieldValue();
					sgAddr1.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_ADD1));
					sgAddr1.setValue(shippingAddress1);
					sfvList.add(sgAddr1);
				}
				else {
					vlogDebug("Shipping Address1 is empty");
				}
				String shippingAddress2=sg.getShippingAddress().getAddress2();
				if(shippingAddress2!=null){
					SegmentationFieldValue sgAddr2= new SegmentationFieldValue();
					sgAddr2.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_ADD2));
					sgAddr2.setValue(shippingAddress2);
					sfvList.add(sgAddr2);
				}
				else {
					vlogDebug("Shipping Address2 is empty");
				}
				String shippingCity=sg.getShippingAddress().getCity();
				if(shippingCity!=null){
					SegmentationFieldValue sgCity= new SegmentationFieldValue();
					sgCity.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_CITY));
					sgCity.setValue(shippingCity);
					sfvList.add(sgCity);
				}
				else {
					vlogDebug("Shipping City is empty");
				}
				String shippingState=sg.getShippingAddress().getState();
				if(shippingState!=null){
					SegmentationFieldValue sgState= new SegmentationFieldValue();
					sgState.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_STATE));
					sgState.setValue(shippingState);
					sfvList.add(sgState);
				}
				else {
					vlogDebug("Shipping State is empty");
				}
				String shippingPostalCode=sg.getShippingAddress().getPostalCode();
				if(shippingPostalCode!=null){
					SegmentationFieldValue sgZip= new SegmentationFieldValue();
					sgZip.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_ZIP));
					sgZip.setValue(shippingPostalCode);
					sfvList.add(sgZip);
				}
				else {
					vlogDebug("Shipping Zipcode is empty");
				}
				String shippingCountry=sg.getShippingAddress().getCountry();
				if(shippingCountry!=null){
					SegmentationFieldValue sgCountry= new SegmentationFieldValue();
					sgCountry.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_STREET_COUNTRY));
					sgCountry.setValue(shippingCountry);
					sfvList.add(sgCountry);
				}
				else {
					vlogDebug("Shipping Countr is empty");
				}
				
			}
		}
		vlogInfo("OrderEmailService:populateShippingAddress:End");
		
	}
	
	protected void populateCartItems(OrderImpl order,List<SegmentationFieldValue> sfvList) throws ListrakException{
		vlogInfo("OrderEmailService:populateCartItems:Start");
		if(order !=null){
			StringBuilder sb=new StringBuilder();
			String cartItemsHeader=ListrakConstants.getResources().getString(ListrakConstants.CART_ITEMS_HEADER_RESOURCE_NAME);
			String cartItemsFooter=	ListrakConstants.getResources().getString(ListrakConstants.CART_ITEMS_FOOTER_RESOURCE_NAME);	
			sb.append(cartItemsHeader);
			List<CommerceItem> ciList=order.getCommerceItems();
			for(CommerceItem ci:ciList){
				long quantity=ci.getQuantity();
				
				ItemPriceInfo priceInfo=ci.getPriceInfo();
				boolean onSale=priceInfo.isOnSale();
				double price=0.0d;
				if(onSale){
					price=priceInfo.getSalePrice();
				}
				else {
					price=priceInfo.getListPrice();
				}
				double totalPrice=price;
				String quantityStr=""+quantity;
				String productId=ci.getAuxiliaryData().getProductId();
				List<String> productDetails=getProductDetails(productId);
				boolean isItemStandard=true;
				if(isItemStandard){
					Object[] msgArgs = new Object[5];
					msgArgs[0]=productDetails.get(2);
					msgArgs[1]=productDetails.get(0);
					msgArgs[2]=productDetails.get(1);
					msgArgs[3]=quantityStr;
					msgArgs[4]="$"+formatDoubleValueToTwodecimals(totalPrice);
					String standardDetails=ListrakConstants.getResources().getString(ListrakConstants.STANDARD_SKU_DETAILS_RESOURCE_NAME);
					String standardTemplate=MessageFormat.format(standardDetails, msgArgs);
					sb.append(standardTemplate);
				}
				else {
					// If not standard items
				}
				
				
			}
			sb.append(cartItemsFooter);
			SegmentationFieldValue cartItems= new SegmentationFieldValue();
			cartItems.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.CART_ITEMS));
			cartItems.setValue(sb.toString().trim());
			sfvList.add(cartItems);
			
		}
		else {
			throw new ListrakException(ListrakConstants.ORDER_NULL_EXCEPTION);
		}
		vlogInfo("OrderEmailService:populateCartItems:End");
	
	}
	
	protected void populateCartItems(OrderImpl order,List<SegmentationFieldValue> sfvList,List<CommerceItem> pItemsShipped,Map<String, String> pTrackingNumberMap) throws ListrakException{

		vlogInfo("OrderEmailService:populateCartItems:with multiple params:Start");
		if(order !=null){
			StringBuilder sb=new StringBuilder();
			String cartItemsHeader=ListrakConstants.getResources().getString(ListrakConstants.CART_ITEMS_HEADER_RESOURCE_NAME);
			String cartItemsFooter=	ListrakConstants.getResources().getString(ListrakConstants.CART_ITEMS_FOOTER_RESOURCE_NAME);	
			sb.append(cartItemsHeader);
			for(CommerceItem ci:pItemsShipped){
				long quantity=ci.getQuantity();
				
				ItemPriceInfo priceInfo=ci.getPriceInfo();
				boolean onSale=priceInfo.isOnSale();
				double price=0.0d;
				if(onSale){
					price=priceInfo.getSalePrice();
				}
				else {
					price=priceInfo.getListPrice();
				}
				double totalPrice=price;
				String quantityStr=""+quantity;
				String productId=ci.getAuxiliaryData().getProductId();
				List<String> productDetails=getProductDetails(productId);
				boolean isItemStandard=true;
				if(isItemStandard){
					Object[] msgArgs = new Object[5];
					msgArgs[0]=productDetails.get(2);
					msgArgs[1]=productDetails.get(0);
					msgArgs[2]=productDetails.get(1);
					msgArgs[3]=quantityStr;
					msgArgs[4]="$"+formatDoubleValueToTwodecimals(totalPrice);
					String standardDetails=ListrakConstants.getResources().getString(ListrakConstants.STANDARD_SKU_DETAILS_RESOURCE_NAME);
					String standardTemplate=MessageFormat.format(standardDetails, msgArgs);
					sb.append(standardTemplate);
				}
				else {
					// is not standard item
				}
				
				
			}
			sb.append(cartItemsFooter);
			SegmentationFieldValue cartItems= new SegmentationFieldValue();
			cartItems.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.CART_ITEMS));
			cartItems.setValue(sb.toString().trim());
			sfvList.add(cartItems);
			
		}
		else {
			throw new ListrakException(ListrakConstants.ORDER_NULL_EXCEPTION);
		}
		vlogInfo("OrderEmailService:populateCartItems:with multiple params:End");
	}
	
	protected List<String> getProductDetails(String productId) throws ListrakException{
		vlogInfo("OrderEmailService:getProductDetails:Start");
		List<String> productDetails=new ArrayList<String>();
		try {
			
			RepositoryItem productItem= getProductRepository().getItem(productId, "product");
			String title=(String)productItem.getPropertyValue(ListrakConstants.PRODUCT_TITLE_PROPERTY_NAME);
			productDetails.add(productId);
			productDetails.add(title);
			String seoDescription=(String)productItem.getPropertyValue(ListrakConstants.PRODUCT_SEODES_PROPERTY_NAME);
			seoDescription=seoDescription.replaceAll(" ","-");
			productDetails.add(seoDescription);
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		vlogInfo("OrderEmailService:getProductDetails:End");
		return productDetails;
		
	}
	
	protected void populatePriceInfoDetails(OrderImpl order,List<SegmentationFieldValue> sfvList,List<CommerceItem> pItemsShipped,Map<String,String> pTrackingNumberMap) throws ListrakException{
		vlogInfo("OrderEmailService:populatePriceInfoDetails:with multiple params:Start");
		try {
			OrderPriceInfo opi=order.getPriceInfo();
			double subTotal=0.00d;
			double taxPrice=0.00d;
			double shippingTotal=0.00d;
			
			double totalPrice=0.00d;
			
			for(CommerceItem item:pItemsShipped){
				double itemSubTotal=item.getPriceInfo().getRawTotalPrice();
				CommerceItemImpl itemImpl=((CommerceItemImpl)item);
				Double shippingPrice=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING)) : new Double(0.00d));

				Double shippingTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_TAX)) : new Double(0.00d));;
				Double shippingCountryTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTRY_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTRY_TAX)) : new Double(0.00d));
				Double shippingStateTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_STATE_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_STATE_TAX)) : new Double(0.00d));
				Double shippingDistrictTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISTRICT_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISTRICT_TAX)) : new Double(0.00d));
				Double shippingCountyTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTY_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTY_TAX)) : new Double(0.00d));
				Double shippingCityTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_CITY_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_CITY_TAX)) : new Double(0.00d));
				Double shippingDiscount=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISCOUNT)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISCOUNT)) : new Double(0.00d));
				double shippingPriceTotal=shippingPrice.doubleValue()+shippingTax.doubleValue()+shippingCountryTax.doubleValue()+shippingStateTax.doubleValue()+shippingDistrictTax.doubleValue()+shippingCountyTax.doubleValue()+shippingCityTax.doubleValue()-shippingDiscount.doubleValue();
				//double shippingPriceTotal=shippingPrice.doubleValue();
				TaxPriceInfo tpiItem=(itemImpl.getPropertyValue(ListrakConstants.ITEM_TAX_PRICE_INFO)!=null ? (((TaxPriceInfo)itemImpl.getPropertyValue(ListrakConstants.ITEM_TAX_PRICE_INFO))) : new TaxPriceInfo());
				double itemTaxTotal=tpiItem.getAmount();
				subTotal+=itemSubTotal;
				taxPrice+=itemTaxTotal;
				shippingTotal+=shippingPriceTotal;
			}
			
			
			totalPrice=subTotal+taxPrice+shippingTotal;
		
			SegmentationFieldValue subTotalFv= new SegmentationFieldValue();
			subTotalFv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SUB_TOTAL));
			subTotalFv.setValue(formatDoubleValueToTwodecimals(subTotal));
			sfvList.add(subTotalFv);
			
			SegmentationFieldValue TaxFv= new SegmentationFieldValue();
			TaxFv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.TAX));
			TaxFv.setValue(formatDoubleValueToTwodecimals(taxPrice));
			sfvList.add(TaxFv);
			
			SegmentationFieldValue ShippingFV= new SegmentationFieldValue();
			ShippingFV.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_COST));
			ShippingFV.setValue(formatDoubleValueToTwodecimals(shippingTotal));
			sfvList.add(ShippingFV);
			
			SegmentationFieldValue TotalFV= new SegmentationFieldValue();
			TotalFV.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.GRAND_TOTAL));
			TotalFV.setValue(formatDoubleValueToTwodecimals(totalPrice));
			sfvList.add(TotalFV);
			
			
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		
		vlogInfo("OrderEmailService:populatePriceInfoDetails:with multiple params:End");
	}
	
	protected void populatePriceInfoDetails(OrderImpl order,List<SegmentationFieldValue> sfvList) throws ListrakException{
		vlogInfo("OrderEmailService:populatePriceInfoDetails:Start");
		try {
			OrderPriceInfo opi=order.getPriceInfo();
			double subTotal=opi.getRawSubtotal();
			double taxPrice=opi.getTax();
			double shippingPrice=opi.getShipping();
			double totalPrice=opi.getTotal();
			SegmentationFieldValue subTotalFv= new SegmentationFieldValue();
			subTotalFv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SUB_TOTAL));
			subTotalFv.setValue(formatDoubleValueToTwodecimals(subTotal));
			sfvList.add(subTotalFv);
			
			SegmentationFieldValue TaxFv= new SegmentationFieldValue();
			TaxFv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.TAX));
			TaxFv.setValue(formatDoubleValueToTwodecimals(taxPrice));
			sfvList.add(TaxFv);
			
			SegmentationFieldValue ShippingFV= new SegmentationFieldValue();
			ShippingFV.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_COST));
			ShippingFV.setValue(formatDoubleValueToTwodecimals(shippingPrice));
			sfvList.add(ShippingFV);
			
			SegmentationFieldValue TotalFV= new SegmentationFieldValue();
			TotalFV.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.GRAND_TOTAL));
			TotalFV.setValue(formatDoubleValueToTwodecimals(totalPrice));
			sfvList.add(TotalFV);
			
			
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		vlogInfo("OrderEmailService:populatePriceInfoDetails:End");
		
	}
	
	protected void populateBopisStoreInfoDetails(OrderImpl order,List<SegmentationFieldValue> sfvList) throws ListrakException{
		vlogInfo("OrderEmailService:populateBopisStoreInfoDetails:Start");
		try {
			
			Boolean isBopisOrder=(Boolean)order.getPropertyValue(ListrakConstants.BOPIS_ORDER);
			if(isBopisOrder){
				String storeId=(String)order.getPropertyValue(ListrakConstants.BOPIS_STORE);
				if(storeId!=null&&!storeId.isEmpty()){
					
					RepositoryItem storeItem=getStoreRepository().getItem(storeId,ListrakConstants.BOPIS_STORE_ITEM_DESCRIPTOR);
					String locationName=(String)storeItem.getPropertyValue(ListrakConstants.BOPIS_STORE_NAME);
					if(locationName!=null&&!locationName.isEmpty()){
						SegmentationFieldValue storeName= new SegmentationFieldValue();
						storeName.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.STORE_NAME));
						storeName.setValue(locationName);
						sfvList.add(storeName);
					}
					String storeAddress1=(String)storeItem.getPropertyValue(ListrakConstants.BOPIS_STORE_ADDRESS1);
					if(storeAddress1!=null&&!storeAddress1.isEmpty()){
						SegmentationFieldValue storeAddress1Sfv= new SegmentationFieldValue();
						storeAddress1Sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.STORE_STREET_ADD1));
						storeAddress1Sfv.setValue(storeAddress1);
						sfvList.add(storeAddress1Sfv);
					}
					String storeCity=(String)storeItem.getPropertyValue(ListrakConstants.BOPIS_STORE_CITY);
					if(storeCity!=null&&!storeCity.isEmpty()){
						SegmentationFieldValue storeCitySfv= new SegmentationFieldValue();
						storeCitySfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.STORE__STREET_CITY));
						storeCitySfv.setValue(storeCity);
						sfvList.add(storeCitySfv);
					}
					String storeState=(String)storeItem.getPropertyValue(ListrakConstants.BOPIS_STORE_STATE);
					if(storeState!=null&&!storeState.isEmpty()){
						SegmentationFieldValue storeStateSfv= new SegmentationFieldValue();
						storeStateSfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.STORE_STREET_STATE));
						storeStateSfv.setValue(storeState);
						sfvList.add(storeStateSfv);
					}
					
					String storeZip=(String)storeItem.getPropertyValue(ListrakConstants.BOPIS_STORE_ZIP);
					if(storeZip!=null&&!storeZip.isEmpty()){
						SegmentationFieldValue storeZipSfv= new SegmentationFieldValue();
						storeZipSfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.STORE_STREET_ZIP));
						storeZipSfv.setValue(storeZip);
						sfvList.add(storeZipSfv);
					}
					
					String storeWebsite=(String)storeItem.getPropertyValue(ListrakConstants.BOPIS_STORE_WEBSITE);
					if(storeWebsite!=null&&!storeWebsite.isEmpty()){
						
						if(isUseHttps()){
							if(storeWebsite.charAt(0)=='/'){
								storeWebsite="https://"+getSiteHttpServerName()+storeWebsite;
							}
							else {
								storeWebsite="https://"+getSiteHttpServerName()+storeWebsite+"/";
							}
						}
						else {
							if(storeWebsite.charAt(0)=='/'){
								storeWebsite="http://"+getSiteHttpServerName()+storeWebsite;
							}
							else {
								storeWebsite="http://"+getSiteHttpServerName()+storeWebsite+"/";
							}
						}
						StringBuilder sb=new StringBuilder();
						Object[] msgArgs = new Object[1];
						msgArgs[0]=storeWebsite;
						String storeWebsiteLink=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_STORE_WEBSITE_LINK);
						String standardTemplate=MessageFormat.format(storeWebsiteLink, msgArgs);
						sb.append(standardTemplate);
						
						SegmentationFieldValue storeLink= new SegmentationFieldValue();
						storeLink.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.STORE_WEBSITE));
						storeLink.setValue(sb.toString().trim());
						sfvList.add(storeLink);
					}
				}
				else {
					// Store Id is null
				}
				
			}
			else {
				//Order is not bopis
			}
			
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		vlogInfo("OrderEmailService:populateBopisStoreInfoDetails:End");
		
	}
	
	protected void populateBopisAlternateInfoDetails(OrderImpl order,List<SegmentationFieldValue> sfvList) throws ListrakException{
		vlogInfo("OrderEmailService:populateBopisAlternateInfoDetails:Start");
		try {
			
			Boolean isBopisOrder=(Boolean)order.getPropertyValue(ListrakConstants.BOPIS_ORDER);
			if(isBopisOrder){
				String altPersonName=(String)order.getPropertyValue(ListrakConstants.BOPIS_PERSON);
				if(altPersonName!=null&&!altPersonName.isEmpty()){
					SegmentationFieldValue altPersonSfv= new SegmentationFieldValue();
					altPersonSfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.ALT_FIRST_NAME));
					altPersonSfv.setValue(altPersonName);
					sfvList.add(altPersonSfv);
				}
				String altEmail=(String)order.getPropertyValue(ListrakConstants.BOPIS_EMAIL);
				if(altEmail!=null&&!altEmail.isEmpty()){
					SegmentationFieldValue altEmailSfv= new SegmentationFieldValue();
					altEmailSfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.ALT_PICKUP_EMAIL));
					altEmailSfv.setValue(altEmail);
					sfvList.add(altEmailSfv);
				}
					
			}
				
			
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		
		vlogInfo("OrderEmailService:populateBopisAlternateInfoDetails:End");
	}
	
	protected void populateBopisPickupDetails(OrderImpl order,List<SegmentationFieldValue> sfvList, int scenarioNumber) throws ListrakException{
		try {
			vlogInfo("OrderEmailService:populateBopisPickupDetails:Start");
			Boolean isBopisOrder=(Boolean)order.getPropertyValue(ListrakConstants.BOPIS_ORDER);
			if(isBopisOrder){
				
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
				Timestamp readyforpick_date=(Timestamp)order.getPropertyValue(ListrakConstants.BOPIS_READY_FOR_PICKUP_DATE);
				String pickDate=sdf.format(readyforpick_date);
				if(pickDate!=null){
					SegmentationFieldValue sfv= new SegmentationFieldValue();
					sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.PICKUP_DATE));
					sfv.setValue(pickDate.trim());
					sfvList.add(sfv);
				}
				
			}
			
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		
		vlogInfo("OrderEmailService:populateBopisPickupDetails:End");
	}
	
	protected void populateTrackingNumbers(OrderImpl order,List<SegmentationFieldValue> sfvList){
		vlogInfo("OrderEmailService:populateTrackingNumbers:Start");
		vlogInfo("OrderEmailService:populateTrackingNumbers:End");
		
		/// TO DO CODE HERE
		
	}
	
	protected void populateTrackingNumbers(OrderImpl order,List<SegmentationFieldValue> sfvList,List<CommerceItem> pItemsShipped,Map<String,String> pTrackingNumberMap) throws ListrakException{
		vlogInfo("OrderEmailService:populateTrackingNumbers:with multiple params: Start");
		try {
			
			
			
			StringBuilder fedexTrackingLinks=new StringBuilder();
			
				String prevTrackingNumber="";
				String currentTrackingNumber="";
				if(pTrackingNumberMap.size()!=0){
					for(String key:pTrackingNumberMap.keySet()){
						String trakNo=pTrackingNumberMap.get(key);
						vlogDebug("Get tracking key: "+key+";Get tracking number:"+trakNo);
						if(!prevTrackingNumber.trim().equalsIgnoreCase(trakNo)){
							currentTrackingNumber=trakNo;
							if(!currentTrackingNumber.isEmpty()){
								StringBuilder sb=new StringBuilder();
								Object[] msgArgs = new Object[2];
								msgArgs[0]=currentTrackingNumber;
								msgArgs[1]=currentTrackingNumber;
								String fedexLink=ListrakConstants.getResources().getString(ListrakConstants.FEDEX_TRACKING_LINK);
								String standardTemplate=MessageFormat.format(fedexLink, msgArgs);
								sb.append(standardTemplate);
								fedexTrackingLinks.append(sb.toString());
							}
							prevTrackingNumber=currentTrackingNumber;
						}
					}
				}
				else {
					
				}
				vlogDebug("Fedex Tracking links "+fedexTrackingLinks.toString());
				if(!(fedexTrackingLinks.toString().isEmpty())){
					SegmentationFieldValue sfv= new SegmentationFieldValue();
					sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.TRACKING_NUMBER));
					sfv.setValue(fedexTrackingLinks.toString());
					sfvList.add(sfv);
				}
				
			
		}
		catch(Exception ex){
			vlogInfo("OrderEmailService:populateTrackingNumbers:with multiple params:Exception occurred ");
			throw new ListrakException(ex);
			
		}
		vlogInfo("OrderEmailService:populateTrackingNumbers:with multiple params: End");
		
	}
	
	protected String formatDoubleValueToTwodecimals(double doubleValue){
		String formattedString="0.00";
		DecimalFormat df = new DecimalFormat("0.00");
		formattedString = df.format(doubleValue);
		return formattedString;
	    
	}
	
	protected void populateCustomerDetails(List<SegmentationFieldValue> sfvList)throws ListrakException{
		vlogInfo("OrderEmailService:populateCustomerDetails:Start");
		if(sfvList!=null){
			SegmentationFieldValue custDetailsSfv= new SegmentationFieldValue();
			StringBuilder custDetails=new StringBuilder();
			String custDetailsHeader=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_CUSTOMER_DETAIL_HEADER);
			custDetails.append(custDetailsHeader);
			String custDetailsFooter=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_CUSTOMER_DETAIL_FOOTER);
			String custOrderNum=populateCustDetailsFieldValue(sfvList,getFiledIdToNameMap().get(ListrakConstants.ORDER_NUMBER));
			String pFname=populateCustDetailsFieldValue(sfvList,getFiledIdToNameMap().get(ListrakConstants.BILLING_FIRST_NAME));
			String pLname=populateCustDetailsFieldValue(sfvList,getFiledIdToNameMap().get(ListrakConstants.BILLING_LAST_NAME));
			String pEmail=populateCustDetailsFieldValue(sfvList,getFiledIdToNameMap().get(ListrakConstants.EMAIL));
			String altName=populateCustDetailsFieldValue(sfvList,getFiledIdToNameMap().get(ListrakConstants.ALT_FIRST_NAME));
			String pickupEmail=populateCustDetailsFieldValue(sfvList,getFiledIdToNameMap().get(ListrakConstants.ALT_PICKUP_EMAIL));
			
			if(!custOrderNum.isEmpty()){
				StringBuilder sb=new StringBuilder();
				Object[] msgArgs = new Object[1];
				msgArgs[0]=custOrderNum;
				String custDetfieldVal=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_CUSTOMER_DETAIL_ONUM);
				String standardTemplate=MessageFormat.format(custDetfieldVal, msgArgs);
				sb.append(standardTemplate);
				custDetails.append(sb.toString());
			}
			if(!pFname.isEmpty()){
					StringBuilder sb=new StringBuilder();
					Object[] msgArgs = new Object[2];
					if(!pLname.isEmpty()){
						msgArgs[0]=pFname;
						msgArgs[1]=pLname;
					}
					else {
						msgArgs[0]=pFname;
						msgArgs[1]="";
					}
				String custDetfieldVal=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_CUSTOMER_DETAIL_PNAME);
				String standardTemplate=MessageFormat.format(custDetfieldVal, msgArgs);
				sb.append(standardTemplate);
				custDetails.append(sb.toString());
			}
			
			if(!pEmail.isEmpty()){
				StringBuilder sb=new StringBuilder();
				Object[] msgArgs = new Object[1];
				msgArgs[0]=pEmail;
				String custDetfieldVal=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_CUSTOMER_DETAIL_PEMAIL);
				String standardTemplate=MessageFormat.format(custDetfieldVal, msgArgs);
				sb.append(standardTemplate);
				custDetails.append(sb.toString());
			}
			if(!altName.isEmpty()){
				StringBuilder sb=new StringBuilder();
				Object[] msgArgs = new Object[2];
				msgArgs[0]=altName;
				msgArgs[1]="";
				String custDetfieldVal=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_CUSTOMER_DETAIL_ALTNAME);
				String standardTemplate=MessageFormat.format(custDetfieldVal, msgArgs);
				sb.append(standardTemplate);
				custDetails.append(sb.toString());
			}
			if(!pickupEmail.isEmpty()){
				StringBuilder sb=new StringBuilder();
				Object[] msgArgs = new Object[1];
				msgArgs[0]=pickupEmail;
				String custDetfieldVal=ListrakConstants.getResources().getString(ListrakConstants.BOPIS_CUSTOMER_DETAIL_ALTEmail);
				String standardTemplate=MessageFormat.format(custDetfieldVal, msgArgs);
				sb.append(standardTemplate);
				custDetails.append(sb.toString());
			}
			custDetails.append(custDetailsFooter);
			vlogDebug("Customer details dynamic html "+custDetails.toString());
			if(!(custDetails.toString().isEmpty())){
				SegmentationFieldValue sfv= new SegmentationFieldValue();
				sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.CUSTOMER_DETAILS));
				sfv.setValue(custDetails.toString());
				sfvList.add(sfv);
			}


		}
		vlogInfo("OrderEmailService:populateCustomerDetails:End");
	}
	
	protected String populateCustDetailsFieldValue(List<SegmentationFieldValue> sfList , String fieldName){
		String result="";
		for(SegmentationFieldValue sf:sfList){
			if(sf.getSegmentationFieldId().equalsIgnoreCase(fieldName)){
				result=sf.getValue();
				break;
			}
		}
		return result;
	}
	
	
}
