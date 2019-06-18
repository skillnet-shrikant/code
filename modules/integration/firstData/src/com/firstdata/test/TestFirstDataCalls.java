package com.firstdata.test;

import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.service.FirstDataService;

import atg.nucleus.GenericService;

public class TestFirstDataCalls extends GenericService  {

  private FirstDataService mFirstDataService;
  private String mEan;
  private double mAmount;
  
  public void setAmount(double pAmount){
	  mAmount=pAmount;
  }
  
  public double getAmount(){
	  return mAmount;
  }
	public String getEan() {
		return mEan;
	}

	public void setEan(String pEan) {
		mEan = pEan;
	}

public FirstDataService getFirstDataService() {
    return mFirstDataService;
  }

  public String assignMerchKey;
  

  public String getAssignMerchKey() {
    return assignMerchKey;
  }

  public void setAssignMerchKey(String pAssignMerchKey) {
    assignMerchKey = pAssignMerchKey;
  }

  public String giftCardNumber;

  public String getGiftCardNumber() {
    return giftCardNumber;
  }

  public void setGiftCardNumber(String pGiftCardNumber) {
    giftCardNumber = pGiftCardNumber;
  }
  public void setFirstDataService(FirstDataService mFirstDataService) {
    this.mFirstDataService = mFirstDataService;
  }
  
  public void testActivateGiftCard()throws Exception {
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().activate(getGiftCardNumber(), 30);//25Denom account#
    vlogDebug("Activate--GiftCardNumber {0} - amount {1} ", getGiftCardNumber(),"30");
    vlogDebug("Activate--newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );

  }
  
  
  public void testBalanceInquiry()throws Exception {
	String number=getGiftCardNumber();
	String ean=getEan();
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().balanceInquiry(number,ean);//NonDenom account#
    vlogDebug("BalanceInquiry-newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );

  }  
  
 
  public void test3AuthorizationAndDebit()throws Exception {
    //2408
	String number=getGiftCardNumber();
	String ean=getEan();
	double amount=getAmount();
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().authorize(number,amount, ean);
    vlogDebug("Authorization--newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );
    vlogDebug("Authorization--LocalLockId {0} - LockAmount {1} ", testResponse.getLocalLockId(),testResponse.getLockAmount() );
    //2208
    if(testResponse.getLocalLockId() != null){
      MFFGiftCardPaymentStatus testResponse2 = getFirstDataService().debit(getGiftCardNumber(),amount, testResponse.getLocalLockId(), "EANDummy");
      vlogDebug("Debit--newBalance {0} - responseCode {1} ", testResponse2.getNewBalance(),testResponse2.getResponseCode() );
      vlogDebug("Debit--LocalLockId {0} - LockAmount {1} ", testResponse2.getLocalLockId(),testResponse2.getLockAmount() );      
    }else{
      System.out.println("NO LOCKID - UNABLE TO DEBIT");
    }

  }
  
  public void testAuthorizationEanEncrypted()throws Exception {
    //2408
	String number=getGiftCardNumber();
	String ean=getEan();
	double amount=getAmount();
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().authorize(number,amount, ean);
    vlogDebug("Authorization--newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );
    vlogDebug("Authorization--LocalLockId {0} - LockAmount {1} ", testResponse.getLocalLockId(),testResponse.getLockAmount() );

  }
  
  public void testAssignMerchWorkingKey()throws Exception {
    //2010
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().assignMerchWorkingKey(getAssignMerchKey());
    vlogDebug("AssignMerchWorkingKey-- responseCode {0} ", testResponse.getResponseCode() );

  }


  
  
}
