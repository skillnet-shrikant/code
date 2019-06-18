package com.firstdata.payment;

import atg.payment.PaymentStatusImpl;

public class MFFGiftCardPaymentStatus extends PaymentStatusImpl {

  /**
   * Payment Status Class for holding the status details of Gift Card Transactions.
   * 
   * author: DMI
   */
  private static final long serialVersionUID = 189466668144666684L;

  private String authCode;
  private String localLockId;
  private double newBalance;
  private String responseCode;
  private String callType;
  private double previousBalance;
  private double lockAmount;
  private String responseCodeMsg;
  
  public MFFGiftCardPaymentStatus(String pTransactionId) {
    super();
    this.setTransactionId(pTransactionId);
  }
  
  public MFFGiftCardPaymentStatus () {
    super();
  }
  
  public String getResponseCodeMsg() {
    return responseCodeMsg;
  }

  public void setResponseCodeMsg(String pResponseCodeMsg) {
    responseCodeMsg = pResponseCodeMsg;
  }

  public double getPreviousBalance() {
    return previousBalance;
  }

  public void setPreviousBalance(double previousBalance) {
    this.previousBalance = previousBalance;
  }

  public double getLockAmount() {
    return lockAmount;
  }

  public void setLockAmount(double lockAmount) {
    this.lockAmount = lockAmount;
  }
  
  public String getCallType() {
    return callType;
  }

  public void setCallType(String callType) {
    this.callType = callType;
  }

  public String getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }
  
  public double getNewBalance() {
    return newBalance;
  }

  public void setNewBalance(double newBalance) {
    this.newBalance = newBalance;
  }

  public String getAuthCode() {
    return authCode;
  }

  public void setAuthCode(String authCode) {
    this.authCode = authCode;
  }

  public String getLocalLockId() {
    return localLockId;
  }

  public void setLocalLockId(String localLockId) {
    this.localLockId = localLockId;
  }

}
