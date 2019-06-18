package com.firstdata.payment;

import java.io.Serializable;

import atg.commerce.order.Order;

public class MFFGiftCardInfo implements Serializable {

  /**
   * Payment Info Class for holding the details of Gift Card Transactions.
   * 
   * author: DMI
   */
  private static final long serialVersionUID = -8664008938742788294L;

  private Order mOrder;
  private double mBalanceAmount;
  private double mNewBalance;
  private String mGiftCardNumber;
  private String mCurrencyCode;
  private String mLocalLockId;
  private String mEan;
  private String mErrorMessage;
  private String mResponseCode;
  private String mResponseCodeMsg;
  private boolean mTransactionSuccess;
  
  private String paymentId;
  

  /**
   * The amount that is being debited or authed
   */
  private double amount;

  public double getNewBalance() {
    return mNewBalance;
  }
  public void setNewBalance(double pNewBalance) {
    mNewBalance = pNewBalance;
  }
  
  public boolean isTransactionSuccess() {
    return mTransactionSuccess;
  }
  public void setTransactionSuccess(boolean mTransactionSuccess) {
    this.mTransactionSuccess = mTransactionSuccess;
  }
  public String getEan() {
    return mEan;
  }
  public void setEan(String mEan) {
    this.mEan = mEan;
  }
  public Order getOrder() {
    return mOrder;
  }
  public void setOrder(Order mOrder) {
    this.mOrder = mOrder;
  }
  public double getBalanceAmount() {
    return mBalanceAmount;
  }
  public void setBalanceAmount(double mBalanceAmount) {
    this.mBalanceAmount = mBalanceAmount;
  }

  public String getGiftCardNumber() {
    return mGiftCardNumber;
  }
  public void setGiftCardNumber(String mGiftCardNumber) {
    this.mGiftCardNumber = mGiftCardNumber;
  }
  public String getCurrencyCode() {
    return mCurrencyCode;
  }
  public void setCurrencyCode(String mCurrencyCode) {
    this.mCurrencyCode = mCurrencyCode;
  }
  public String getLocalLockId() {
    return mLocalLockId;
  }
  public void setLocalLockId(String mLocalLockId) {
    this.mLocalLockId = mLocalLockId;
  }
 
  public String getErrorMessage() {
    return mErrorMessage;
  }

  public void setErrorMessage(String pErrorMessage) {
    mErrorMessage = pErrorMessage;
  } 
  
  public String getResponseCode() {
    return mResponseCode;
  }

  public void setResponseCode(String responseCode) {
    this.mResponseCode = responseCode;
  }  

  public String getResponseCodeMsg() {
    return mResponseCodeMsg;
  }
  public void setResponseCodeMsg(String pResponseCodeMsg) {
    mResponseCodeMsg = pResponseCodeMsg;
  }
  public double getAmount() {
    return amount;
  }
  public void setAmount(double pAmount) {
    amount = pAmount;
  }
  public String getPaymentId() {
    return paymentId;
  }
  public void setPaymentId(String pPaymentId) {
    paymentId = pPaymentId;
  }
  
}
