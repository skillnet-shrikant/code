package com.aci.commerce.test;

import com.aci.commerce.service.AciService;
import com.aci.payment.creditcard.AciCreditCardInfo;

import atg.core.util.ContactInfo;

import atg.nucleus.GenericService;

public class AciTestClass extends GenericService {
	
		private AciService aciService;
		private String ccNumber;
		private String ccExpirationMonth;
		private String ccExpirationYear;
		private String ccCardType;
		private String CVV;
		private String nameOnCard;
		private boolean useCvv;
		private String tokenNumber;
		private String mAuthCode;
	    private String mMopTypeCd;
	    private String mOriginalReqId;
	    private String mOriginalJournalKey;
	    private double mAuthReversalAmount;
	    private String mOriginalAciAuthDate;
	    private String mOriginalAciAuthTime;
	    private boolean mUseReqId;
		private boolean mUseOriginalAuthDateTime;  
		  
	    public boolean isUseOriginalAuthDateTime() {
			return mUseOriginalAuthDateTime;
		}

		public void setUseOriginalAuthDateTime(boolean pUseOriginalAuthDateTime) {
			mUseOriginalAuthDateTime = pUseOriginalAuthDateTime;
		}

		public boolean isUseReqId() {
			return mUseReqId;
		}

		public void setUseReqId(boolean pUseReqId) {
			mUseReqId = pUseReqId;
		}

		public String getOriginalAciAuthDate() {
			return mOriginalAciAuthDate;
		}

		public void setOriginalAciAuthDate(String pOriginalAciAuthDate) {
			mOriginalAciAuthDate = pOriginalAciAuthDate;
		}

		public String getOriginalAciAuthTime() {
			return mOriginalAciAuthTime;
		}

		public void setOriginalAciAuthTime(String pOriginalAciAuthTime) {
			mOriginalAciAuthTime = pOriginalAciAuthTime;
		}

		public double getAuthReversalAmount(){
			  return mAuthReversalAmount;
		  }
		  
		  public void setAuthReversalAmount(double pAuthReversalAmount){
			  mAuthReversalAmount=pAuthReversalAmount;
		  }
		  
		  public String getOriginalReqId() {
			return mOriginalReqId;
		}

		public void setOriginalReqId(String pOriginalReqId) {
			mOriginalReqId = pOriginalReqId;
		}

		public String getOriginalJournalKey() {
			return mOriginalJournalKey;
		}

		public void setOriginalJournalKey(String pOriginalJournalKey) {
			mOriginalJournalKey = pOriginalJournalKey;
		}

		public String getMopTypeCd(){
			  return mMopTypeCd;
		  }
		  
		  public void setMopTypeCd(String pMopTypeCd){
			  mMopTypeCd=pMopTypeCd;
		  }
		  
		  public String getAuthCode(){
			  return mAuthCode;
		  }
		  
		  public void setAuthCode(String pAuthCode){
			  mAuthCode=pAuthCode;
		  }

		
		public String getTokenNumber(){
			return tokenNumber;
		}
		
		public void setTokenNumber(String pTokenNumber){
			tokenNumber=pTokenNumber;
		}
		
		public boolean isUseCvv(){
			return useCvv;
		}
		
		public void setUseCvv(boolean pUseCvv){
			useCvv=pUseCvv;
		}
	
	  	public AciService getAciService() {
		    return aciService;
		  }

		  public void setAciService(AciService pAciService) {
		    aciService = pAciService;
		  }

		  public String getCcNumber() {
		    return ccNumber;
		  }

		  public void setCcNumber(String pCcNumber) {
		    ccNumber = pCcNumber;
		  }

		  public String getCcExpirationMonth() {
		    return ccExpirationMonth;
		  }

		  public void setCcExpirationMonth(String pCcExpirationMonth) {
		    ccExpirationMonth = pCcExpirationMonth;
		  }

		  public String getCcExpirationYear() {
		    return ccExpirationYear;
		  }

		  public void setCcExpirationYear(String pCcExpirationYear) {
		    ccExpirationYear = pCcExpirationYear;
		  }

		  public String getCcCardType() {
		    return ccCardType;
		  }

		  public void setCcCardType(String pCcCardType) {
		    ccCardType = pCcCardType;
		  }

		  public String getCVV() {
		    return CVV;
		  }

		  public void setCVV(String pCVV) {
		    CVV = pCVV;
		  }

		  public String getNameOnCard() {
		    return nameOnCard;
		  }

		  public void setNameOnCard(String pNameOnCard) {
		    nameOnCard = pNameOnCard;
		  }
	
   public void testAciTokenization(){
	   AciCreditCardInfo ccInfo=getCreditCardinfo(constructContactInfo());
	   try{
		   getAciService().tokenizeCreditCard(ccInfo);
		   setTokenNumber(ccInfo.getTokenNumber());
	   }
	   catch(Exception ex){
		   vlogInfo("Exception occurred",ex);
	   }
	}
		  
	public void testAciAuthorization(){
	   AciCreditCardInfo ccInfo=getCreditCardinfoForAuth(constructContactInfo());
	   try{
		  
		   if(isUseCvv()){
			   ccInfo.setSecurityCode(getCVV());
		   }
		   getAciService().authorizeCreditCard(ccInfo);
	   }
	   catch(Exception ex){
		   vlogInfo("Exception occurred",ex);
	   }
	}
	
	protected ContactInfo constructContactInfo() {
	    ContactInfo contactInfo = new ContactInfo();
	    contactInfo.setAddress1("1 B St");
	    contactInfo.setAddress2("");
	    contactInfo.setFirstName("Rick");
	    contactInfo.setLastName("Ross");
	    contactInfo.setCity("Beverly Hills");
	    contactInfo.setCountry("US");
	    contactInfo.setState("CA");
	    contactInfo.setPhoneNumber("2223332222");
	    contactInfo.setPostalCode("90201");
	    contactInfo.setEmail("rick@ross.com");
	    return contactInfo;
	  }
	
	private AciCreditCardInfo getCreditCardinfo(ContactInfo pBillingAddress){
		 	AciCreditCardInfo ccInfo = new AciCreditCardInfo();
		    ccInfo.setCreditCardNumber(getCcNumber());
		    if(isUseCvv()){
		    	ccInfo.setSecurityCode(getCVV());
		    }
		    ccInfo.setCreditCardType(getCcCardType().toLowerCase());
		    ccInfo.setExpirationMonth(getCcExpirationMonth());
		    ccInfo.setExpirationYear(getCcExpirationYear());
		    ccInfo.setOnlineOrder(true);
		    ccInfo.setBillingAddress(pBillingAddress);
		    return ccInfo;
	}
	
	private AciCreditCardInfo getCreditCardinfoForAuth(ContactInfo pBillingAddress){
	 	AciCreditCardInfo ccInfo = new AciCreditCardInfo();
	    ccInfo.setTokenNumber(getTokenNumber());
	    if(isUseCvv()){
	    	ccInfo.setSecurityCode(getCVV());
	    }
	    ccInfo.setCreditCardType(getCcCardType().toLowerCase());
	    ccInfo.setExpirationMonth(getCcExpirationMonth());
	    ccInfo.setExpirationYear(getCcExpirationYear());
	    ccInfo.setOnlineOrder(true);
	    ccInfo.setBillingAddress(pBillingAddress);
	    ccInfo.setAmount(26.20);
	    return ccInfo;
	}
	
	 public void testAuthReversals() throws Exception {

		  AciCreditCardInfo aciCreditCardInfo = new AciCreditCardInfo();
		  
		  aciCreditCardInfo.setOnlineOrder(true);
		  aciCreditCardInfo.setAuthorizationNumber(getAuthCode());
		  aciCreditCardInfo.setAuthReversalAmount(getAuthReversalAmount());
		  aciCreditCardInfo.setOriginalJournalKey(getOriginalJournalKey());
		  aciCreditCardInfo.setMopTypeCd(getMopTypeCd());
		  AciService aciService=getAciService();
		  if(aciService.getAciConfiguration().isUseReqIdForAuthReversal()){
				 aciCreditCardInfo.setOriginalReqId(getOriginalReqId());
				 if(aciService.getAciConfiguration().isUseOriginalAuthDateTime()){
					 aciCreditCardInfo.setOriginalAuthDate(getOriginalAciAuthDate());
					 aciCreditCardInfo.setOriginalAuthTime(getOriginalAciAuthTime());
				 }
		  }
		  aciService.reversePreviousAuthorization(aciCreditCardInfo);
	  }

}
