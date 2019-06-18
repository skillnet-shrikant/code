package com.aci.payment.creditcard;


import java.util.Date;

import atg.payment.creditcard.CreditCardStatusImpl;

public class AciCreditCardStatus extends CreditCardStatusImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * <property column-name="auth_code" data-type="string" name="authCode" />
			<property column-name="response_code" data-type="string" name="responseCode" />
			<property column-name="response_code_msg" data-type="string" name="responseCodeMsg" />
			<property column-name="cvv_verification_code" data-type="string" name="cvvVerificationCode" />
			<property column-name="call_type" data-type="string" name="callType" />
	 * 
	 * 
	 */
	
	private String mAuthCode;
	private String mResponseCode;
	private String mResponseCodeMsg;
	private String mCvvVerificationCode;
	private String mCallType;
	private String businessDate;
	//
  private String storeNumber = ""; 
  private String terminalNumber = ""; 
  private String transactionNumber = ""; 
  private String tenderSequence = ""; 
  private String recordIdentifier = ""; 
  private String keySequence = ""; 
  private String journalKey = "";
  private String operatorNumber = "";
  private String transactionDate = ""; 
  private String processingIdentifier = "";
  private String tenderAmount = "";
  private String transactionType = "";
  private String invoiceNumber = "";
  private String tenderType = "";
  private String accountNumber = "";
  private String expirationDate = "";
  private String authorizationCode = "";
  private String authorizationDate = "";
  private String authorizationTime = "";
  private String mAuthorizationSource;
  private String posEntryMode = "";
  private String currencyCode = "";
  private String userData = "";
  private String orderNumber = "";
  private String pointofSaleConditionCode = "";
  private String pointofSaleTerminalType = "";
  private String pointofSaleTerminalEntryCapability = "";
  private String reAuthProcessIndicator = "";
  private String retryCounter = "";
  private String cardStartDate = "";
  private String cardIssueNumber = "";
  private String transactionBillingMethodIndicator = "";
  private String mccCode = "";
  private String cashBackAmount = "";
  private String localTime = "";
  private String networkID = "";
  private String retrievalReferenceNum = "";
  private String ebtVoucherNumber = "";
  private String productDeliveryMethod = "";
  private String accountNumberLength = "";
  private String sequenceOfCard = "";
  private String cardLevelResults = "";
  private String authorizationTrackNumber = "";
  private String authorizerBankNumber = "";
  private String marketSpecificIndicator = "";
  private String encryptedStringToken = "";
  private String approvedCashBackAmount = "";
  private String partialApprovalIndicator = "";
  private String authorizedNetworkID = "";
  private String feeProgramIndicator = "";
  private String checkMICRData = "";
  private String micrLineFormatCode = "";
  private String accountType = "";
  private String originalJournalKey = "";
  private double originalAuthorizationAmount;
  private String salesTax = "";
  private String netAuthorizedAmount = "";
  private String merchantOrderNumber = "";
  private String ecommerceIndicator = "";
  private String mOriginalAciAuthDate;
  private String mOriginalAciAuthTime;
  private String mOriginalReqId;
  
  public String getOriginalReqId(){
	  return mOriginalReqId;
  }
  
  public void setOriginalReqId(String pOriginalReqId){
	  mOriginalReqId=pOriginalReqId;
  }
	
  
  public String getOriginalAciAuthDate(){
	  return mOriginalAciAuthDate;
  }
	
  public void setOriginalAciAuthDate(String pOriginalAciAuthDate){
	  mOriginalAciAuthDate=pOriginalAciAuthDate;
  }
  
  public String getOriginalAciAuthTime(){
	  return mOriginalAciAuthTime;
  }
	
  public void setOriginalAciAuthTime(String pOriginalAciAuthTime){
	  mOriginalAciAuthTime=pOriginalAciAuthTime;
  }
  
  public String getBusinessDate() {
    return businessDate;
  }


  public void setBusinessDate(String pBusinessDate) {
    businessDate = pBusinessDate;
  }


  public String getStoreNumber() {
    return storeNumber;
  }


  public void setStoreNumber(String pStoreNumber) {
    storeNumber = pStoreNumber;
  }


  public String getTerminalNumber() {
    return terminalNumber;
  }


  public void setTerminalNumber(String pTerminalNumber) {
    terminalNumber = pTerminalNumber;
  }


  public String getTransactionNumber() {
    return transactionNumber;
  }


  public void setTransactionNumber(String pTransactionNumber) {
    transactionNumber = pTransactionNumber;
  }


  public String getTenderSequence() {
    return tenderSequence;
  }


  public void setTenderSequence(String pTenderSequence) {
    tenderSequence = pTenderSequence;
  }


  public String getRecordIdentifier() {
    return recordIdentifier;
  }


  public void setRecordIdentifier(String pRecordIdentifier) {
    recordIdentifier = pRecordIdentifier;
  }


  public String getKeySequence() {
    return keySequence;
  }


  public void setKeySequence(String pKeySequence) {
    keySequence = pKeySequence;
  }


  public String getJournalKey() {
    return journalKey;
  }


  public void setJournalKey(String pJournalKey) {
    journalKey = pJournalKey;
  }


  public String getOperatorNumber() {
    return operatorNumber;
  }


  public void setOperatorNumber(String pOperatorNumber) {
    operatorNumber = pOperatorNumber;
  }


  public String getTransactionDate() {
    return transactionDate;
  }


  public void setTransactionDate(String pTransactionDate) {
    transactionDate = pTransactionDate;
  }


  public String getProcessingIdentifier() {
    return processingIdentifier;
  }


  public void setProcessingIdentifier(String pProcessingIdentifier) {
    processingIdentifier = pProcessingIdentifier;
  }


  public String getTenderAmount() {
    return tenderAmount;
  }


  public void setTenderAmount(String pTenderAmount) {
    tenderAmount = pTenderAmount;
  }


  public String getTransactionType() {
    return transactionType;
  }


  public void setTransactionType(String pTransactionType) {
    transactionType = pTransactionType;
  }


  public String getInvoiceNumber() {
    return invoiceNumber;
  }


  public void setInvoiceNumber(String pInvoiceNumber) {
    invoiceNumber = pInvoiceNumber;
  }


  public String getTenderType() {
    return tenderType;
  }


  public void setTenderType(String pTenderType) {
    tenderType = pTenderType;
  }


  public String getAccountNumber() {
    return accountNumber;
  }


  public void setAccountNumber(String pAccountNumber) {
    accountNumber = pAccountNumber;
  }


  public String getExpirationDate() {
    return expirationDate;
  }


  public void setExpirationDate(String pExpirationDate) {
    expirationDate = pExpirationDate;
  }


  public String getAuthorizationCode() {
    return authorizationCode;
  }


  public void setAuthorizationCode(String pAuthorizationCode) {
    authorizationCode = pAuthorizationCode;
  }


  public String getAuthorizationDate() {
    return authorizationDate;
  }


  public void setAuthorizationDate(String pAuthorizationDate) {
    authorizationDate = pAuthorizationDate;
  }


  public String getAuthorizationTime() {
    return authorizationTime;
  }


  public void setAuthorizationTime(String pAuthorizationTime) {
    authorizationTime = pAuthorizationTime;
  }


  public String getAuthorizationSource() {
    return mAuthorizationSource;
  }


  public void setAuthorizationSource(String pAuthorizationSource) {
	  mAuthorizationSource = pAuthorizationSource;
  }


  public String getPosEntryMode() {
    return posEntryMode;
  }


  public void setPosEntryMode(String pPosEntryMode) {
    posEntryMode = pPosEntryMode;
  }


  public String getCurrencyCode() {
    return currencyCode;
  }


  public void setCurrencyCode(String pCurrencyCode) {
    currencyCode = pCurrencyCode;
  }


  public String getUserData() {
    return userData;
  }


  public void setUserData(String pUserData) {
    userData = pUserData;
  }


  public String getOrderNumber() {
    return orderNumber;
  }


  public void setOrderNumber(String pOrderNumber) {
    orderNumber = pOrderNumber;
  }


  public String getPointofSaleConditionCode() {
    return pointofSaleConditionCode;
  }


  public void setPointofSaleConditionCode(String pPointofSaleConditionCode) {
    pointofSaleConditionCode = pPointofSaleConditionCode;
  }


  public String getPointofSaleTerminalType() {
    return pointofSaleTerminalType;
  }


  public void setPointofSaleTerminalType(String pPointofSaleTerminalType) {
    pointofSaleTerminalType = pPointofSaleTerminalType;
  }


  public String getPointofSaleTerminalEntryCapability() {
    return pointofSaleTerminalEntryCapability;
  }


  public void setPointofSaleTerminalEntryCapability(String pPointofSaleTerminalEntryCapability) {
    pointofSaleTerminalEntryCapability = pPointofSaleTerminalEntryCapability;
  }


  public String getReAuthProcessIndicator() {
    return reAuthProcessIndicator;
  }


  public void setReAuthProcessIndicator(String pReAuthProcessIndicator) {
    reAuthProcessIndicator = pReAuthProcessIndicator;
  }


  public String getRetryCounter() {
    return retryCounter;
  }


  public void setRetryCounter(String pRetryCounter) {
    retryCounter = pRetryCounter;
  }


  public String getCardStartDate() {
    return cardStartDate;
  }


  public void setCardStartDate(String pCardStartDate) {
    cardStartDate = pCardStartDate;
  }


  public String getCardIssueNumber() {
    return cardIssueNumber;
  }


  public void setCardIssueNumber(String pCardIssueNumber) {
    cardIssueNumber = pCardIssueNumber;
  }


  public String getTransactionBillingMethodIndicator() {
    return transactionBillingMethodIndicator;
  }


  public void setTransactionBillingMethodIndicator(String pTransactionBillingMethodIndicator) {
    transactionBillingMethodIndicator = pTransactionBillingMethodIndicator;
  }


  public String getMccCode() {
    return mccCode;
  }


  public void setMccCode(String pMccCode) {
    mccCode = pMccCode;
  }


  public String getCashBackAmount() {
    return cashBackAmount;
  }


  public void setCashBackAmount(String pCashBackAmount) {
    cashBackAmount = pCashBackAmount;
  }


  public String getLocalTime() {
    return localTime;
  }


  public void setLocalTime(String pLocalTime) {
    localTime = pLocalTime;
  }


  public String getNetworkID() {
    return networkID;
  }


  public void setNetworkID(String pNetworkID) {
    networkID = pNetworkID;
  }


  public String getRetrievalReferenceNum() {
    return retrievalReferenceNum;
  }


  public void setRetrievalReferenceNum(String pRetrievalReferenceNum) {
    retrievalReferenceNum = pRetrievalReferenceNum;
  }


  public String getEbtVoucherNumber() {
    return ebtVoucherNumber;
  }


  public void setEbtVoucherNumber(String pEbtVoucherNumber) {
    ebtVoucherNumber = pEbtVoucherNumber;
  }


  public String getProductDeliveryMethod() {
    return productDeliveryMethod;
  }


  public void setProductDeliveryMethod(String pProductDeliveryMethod) {
    productDeliveryMethod = pProductDeliveryMethod;
  }


  public String getAccountNumberLength() {
    return accountNumberLength;
  }


  public void setAccountNumberLength(String pAccountNumberLength) {
    accountNumberLength = pAccountNumberLength;
  }


  public String getSequenceOfCard() {
    return sequenceOfCard;
  }


  public void setSequenceOfCard(String pSequenceOfCard) {
    sequenceOfCard = pSequenceOfCard;
  }


  public String getCardLevelResults() {
    return cardLevelResults;
  }


  public void setCardLevelResults(String pCardLevelResults) {
    cardLevelResults = pCardLevelResults;
  }


  public String getAuthorizationTrackNumber() {
    return authorizationTrackNumber;
  }


  public void setAuthorizationTrackNumber(String pAuthorizationTrackNumber) {
    authorizationTrackNumber = pAuthorizationTrackNumber;
  }


  public String getAuthorizerBankNumber() {
    return authorizerBankNumber;
  }


  public void setAuthorizerBankNumber(String pAuthorizerBankNumber) {
    authorizerBankNumber = pAuthorizerBankNumber;
  }


  public String getMarketSpecificIndicator() {
    return marketSpecificIndicator;
  }


  public void setMarketSpecificIndicator(String pMarketSpecificIndicator) {
    marketSpecificIndicator = pMarketSpecificIndicator;
  }


  public String getEncryptedStringToken() {
    return encryptedStringToken;
  }


  public void setEncryptedStringToken(String pEncryptedStringToken) {
    encryptedStringToken = pEncryptedStringToken;
  }


  public String getApprovedCashBackAmount() {
    return approvedCashBackAmount;
  }


  public void setApprovedCashBackAmount(String pApprovedCashBackAmount) {
    approvedCashBackAmount = pApprovedCashBackAmount;
  }


  public String getPartialApprovalIndicator() {
    return partialApprovalIndicator;
  }


  public void setPartialApprovalIndicator(String pPartialApprovalIndicator) {
    partialApprovalIndicator = pPartialApprovalIndicator;
  }


  public String getAuthorizedNetworkID() {
    return authorizedNetworkID;
  }


  public void setAuthorizedNetworkID(String pAuthorizedNetworkID) {
    authorizedNetworkID = pAuthorizedNetworkID;
  }


  public String getFeeProgramIndicator() {
    return feeProgramIndicator;
  }


  public void setFeeProgramIndicator(String pFeeProgramIndicator) {
    feeProgramIndicator = pFeeProgramIndicator;
  }


  public String getCheckMICRData() {
    return checkMICRData;
  }


  public void setCheckMICRData(String pCheckMICRData) {
    checkMICRData = pCheckMICRData;
  }


  public String getMicrLineFormatCode() {
    return micrLineFormatCode;
  }


  public void setMicrLineFormatCode(String pMicrLineFormatCode) {
    micrLineFormatCode = pMicrLineFormatCode;
  }


  public String getAccountType() {
    return accountType;
  }


  public void setAccountType(String pAccountType) {
    accountType = pAccountType;
  }


  public String getOriginalJournalKey() {
    return originalJournalKey;
  }


  public void setOriginalJournalKey(String pOriginalJournalKey) {
    originalJournalKey = pOriginalJournalKey;
  }


  public double getOriginalAuthorizationAmount() {
    return originalAuthorizationAmount;
  }


  public void setOriginalAuthorizationAmount(double pOriginalAuthorizationAmount) {
    originalAuthorizationAmount = pOriginalAuthorizationAmount;
  }


  public String getSalesTax() {
    return salesTax;
  }


  public void setSalesTax(String pSalesTax) {
    salesTax = pSalesTax;
  }


  public String getNetAuthorizedAmount() {
    return netAuthorizedAmount;
  }


  public void setNetAuthorizedAmount(String pNetAuthorizedAmount) {
    netAuthorizedAmount = pNetAuthorizedAmount;
  }


  public String getMerchantOrderNumber() {
    return merchantOrderNumber;
  }


  public void setMerchantOrderNumber(String pMerchantOrderNumber) {
    merchantOrderNumber = pMerchantOrderNumber;
  }


  public String getEcommerceIndicator() {
    return ecommerceIndicator;
  }


  public void setEcommerceIndicator(String pEcommerceIndicator) {
    ecommerceIndicator = pEcommerceIndicator;
  }
	
	
	
	 public AciCreditCardStatus(String pTransactionId, double pAmount, boolean pTransactionSuccess, String pErrorMessage, Date pTransactionTimestamp, Date pAuthorizationExpiration)
	 {
        super(pTransactionId,pAmount, pTransactionSuccess,pErrorMessage,pTransactionTimestamp, pAuthorizationExpiration);
        
    }
	
	
	public String getResponseCode() {
		return mResponseCode;
	}

	public void setResponseCode(String pResponseCode) {
		mResponseCode = pResponseCode;
	}

	public String getResponseCodeMsg() {
		return mResponseCodeMsg;
	}

	public void setResponseCodeMsg(String pResponseCodeMsg) {
		mResponseCodeMsg = pResponseCodeMsg;
	}

	public String getCvvVerificationCode() {
		return mCvvVerificationCode;
	}

	public void setCvvVerificationCode(String pCvvVerificationCode) {
		mCvvVerificationCode = pCvvVerificationCode;
	}

	public String getCallType() {
		return mCallType;
	}

	public void setCallType(String pCallType) {
		mCallType = pCallType;
	}

	public AciCreditCardStatus() {
		super();
	}
	
	public String getAuthCode() {
		return mAuthCode;
	}

	public void setAuthCode(String pAuthCode) {
		mAuthCode=pAuthCode;
	}
	
	
}