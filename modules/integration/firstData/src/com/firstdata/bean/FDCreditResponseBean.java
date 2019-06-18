package com.firstdata.bean;

import com.firstdata.util.FirstDataField;

public class FDCreditResponseBean implements FirstDataBean {

  @FirstDataField(key="11")
  private String systemTraceNumber;
  
  @FirstDataField(key="38")
  private String authCode;
  
  @FirstDataField(key="39")
  private String responseCode;
  
  @FirstDataField(key="42")
  private String merchantAndTerminalId;
  
  @FirstDataField(key="44")
  private String alternateMerchantNumber;
  
  @FirstDataField(key="70")
  private String cardNumber;
  
  @FirstDataField(key="75")
  private String previousBalance;
  
  @FirstDataField(key="76")
  private String newBalance;
  
  @FirstDataField(key="78")
  private String lockAmount;
  
  @FirstDataField(key="A0")
  private String expirationDate;
  
  @FirstDataField(key="B0")
  private String cardClass;
  
  @FirstDataField(key="C0")
  private String localCurrency;
  
  @FirstDataField(key="F3")
  private String merchangeKeyId;
  
  @FirstDataField(key="F6")
  private String originalTransactionRequest;

  public String getSystemTraceNumber() {
    return systemTraceNumber;
  }

  public void setSystemTraceNumber(String systemTraceNumber) {
    this.systemTraceNumber = systemTraceNumber;
  }

  public String getAuthCode() {
    return authCode;
  }

  public void setAuthCode(String authCode) {
    this.authCode = authCode;
  }

  public String getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }

  public String getMerchantAndTerminalId() {
    return merchantAndTerminalId;
  }

  public void setMerchantAndTerminalId(String merchantAndTerminalId) {
    this.merchantAndTerminalId = merchantAndTerminalId;
  }

  public String getAlternateMerchantNumber() {
    return alternateMerchantNumber;
  }

  public void setAlternateMerchantNumber(String alternateMerchantNumber) {
    this.alternateMerchantNumber = alternateMerchantNumber;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getPreviousBalance() {
    return previousBalance;
  }

  public void setPreviousBalance(String previousBalance) {
    this.previousBalance = previousBalance;
  }

  public String getNewBalance() {
    return newBalance;
  }

  public void setNewBalance(String newBalance) {
    this.newBalance = newBalance;
  }

  public String getLockAmount() {
    return lockAmount;
  }

  public void setLockAmount(String lockAmount) {
    this.lockAmount = lockAmount;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getCardClass() {
    return cardClass;
  }

  public void setCardClass(String cardClass) {
    this.cardClass = cardClass;
  }

  public String getLocalCurrency() {
    return localCurrency;
  }

  public void setLocalCurrency(String localCurrency) {
    this.localCurrency = localCurrency;
  }

  public String getMerchangeKeyId() {
    return merchangeKeyId;
  }

  public void setMerchangeKeyId(String merchangeKeyId) {
    this.merchangeKeyId = merchangeKeyId;
  }

  public String getOriginalTransactionRequest() {
    return originalTransactionRequest;
  }

  public void setOriginalTransactionRequest(String originalTransactionRequest) {
    this.originalTransactionRequest = originalTransactionRequest;
  }
}
