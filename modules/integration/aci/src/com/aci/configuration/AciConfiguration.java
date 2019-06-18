package com.aci.configuration;

import com.liveprocessor.LPClient.LPClient;
import com.liveprocessor.LPClient.LPIniFileException;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import java.util.List;
import java.util.Map;

public class AciConfiguration extends GenericService {
	
	
	private LPClient mLPClient;
	
	// Configurable properties to be set in the property file
	private String mAciConfigurationPath;
	private boolean mUseSSL;
	private String mDiv_Num_Fraud_Only;
	private String mDiv_Num_CC;
	private String mS_Key_Id;
	private String mEbWebsite;
	private String mClientId;
	private Map<String,String> mCountryValueMap;
	private Map<String,String> mMaxFieldLengthsMap;
	private String mDefaultCurrencyCode;
	private Map<String,String> mCardTypeCodeValueMap;
	private String mDefaultTimeZone;
	private String mFraudSubClientId;
	private String mCreditCardSubClientId;
	private boolean mEnableTieBack;
	private String mCreditCardClassType;
	private String mGiftCardClassType;
	private String mTokenizeTransactionAction;
	private String mAuthorizeTransactionAction;
	private String mFraudTransactionAction;
	private String mCreditTransactionAction;
	private String mDebitTransactionAction;
	private String mAuthReversalTransactionAction;
	private String mFraudScreenPipelineChainName;
	private String mTokenizePipelineChainName;
	private String mAuthorizePipelineChainName;
	private String mDebitTransactionChainName;
	private String mCreditTransactionChainName;
	private String mAuthReversalPipelineChainName;
	private String mCscOrderReqType;
	private String mOnlineOrderReqType;
	private String mDefaultMopType;
	private boolean mUseAvsCodeForFraud;
	private boolean mUseCvvCodeForFraud;
	private Map<String,String> mCardTypeAciCodeMap;
	private Map<String,String> mFraudStateToOrderStateMap;
	private boolean mUseAuthReversal;
	private boolean mUseOriginalAuthDateTime;
	private String mPaymentSource;
	private List<String> mReversalSupportedCards;
	
	
	//
	private String mStoreNumber;
	private String mTerminalNumber;
	private String mTransNumber;
	private String mTenderSequence;
	private String mRecordIdentifier;
	private String mKeySequence;
	private String mProcessingIdentifier;
	private Map<String,String> mTransType;
	private String mTenderType;
	private String mPosEntryMode;
	private String mCurrencyCode;
	private String mPosSaleConditionCode;
	private String mPosTerminalType;
	private String mPosTerminalEntryCap;
	private String mMccCode;
	private String mAccountNumLength;
	private boolean mEnableFraud;
	private String mAuthSource;
	private boolean mGiftCardHasBillingAddress;
	private Map<String, String> mFraudStatCdMap;
	private Map<String,String> mCardCodesMap;
	private boolean mEnableCVVCheck;
	private boolean mEnableAvsCheck;
	
	private String mCvvVerificationDefaultValue;
	private String mAvsVerificationDefaultValue;
	private List<String> mCvvVerificationSuccessList;
	private List<String> mCvvVerificationFailureList;
	private List<String> mCvvVerificationNotCarriedList;
	private List<String> mAvsVerificationSuccessList;
	private List<String> mAvsVerificationFailureList;
	private List<String> mAvsVerificationNotCarriedList;
	
	private boolean mIp4only;
	
	private boolean useReqIdForAuthReversal;
	private int mAuthExpiryThreshold;
	private int mDebitExpiryThreshold;
	private int mCreditExpiryThreshold;
	
	public int getDebitExpiryThreshold() {
		return mDebitExpiryThreshold;
	}

	public void setDebitExpiryThreshold(int pDebitExpiryThreshold) {
		mDebitExpiryThreshold = pDebitExpiryThreshold;
	}

	public int getCreditExpiryThreshold() {
		return mCreditExpiryThreshold;
	}

	public void setCreditExpiryThreshold(int pCreditExpiryThreshold) {
		mCreditExpiryThreshold = pCreditExpiryThreshold;
	}

	public int getAuthExpiryThreshold() {
		return mAuthExpiryThreshold;
	}

	public void setAuthExpiryThreshold(int pAuthExpiryThreshold) {
		mAuthExpiryThreshold = pAuthExpiryThreshold;
	}

	public List<String> getReversalSupportedCards(){
		return mReversalSupportedCards;
	}
	
	public void setReversalSupportedCards(List<String> pReversalSupportedCards){
		mReversalSupportedCards=pReversalSupportedCards;
	}
	
	public String getPaymentSource(){
		return mPaymentSource;
	}
	
	public void setPaymentSource(String pPaymentSource){
		mPaymentSource=pPaymentSource;
	}
	
	public boolean isUseReqIdForAuthReversal(){
		return useReqIdForAuthReversal;
	}
	
	public void setUseReqIdForAuthReversal(boolean pUseReqIdForAuthReversal){
		useReqIdForAuthReversal=pUseReqIdForAuthReversal;
	}
	
	
	public boolean isUseAuthReversal(){
	  return mUseAuthReversal;
	}
	  
	public void setUseAuthReversal(boolean pUseAuthReversal){
		mUseAuthReversal=pUseAuthReversal;
	 }
		 
	
	public boolean isIp4only(){
	  return mIp4only;
	}
	  
	public void setIp4only(boolean pIp4only){
		mIp4only=pIp4only;
	 }
	  
	public List<String> getAvsVerificationSuccessList() {
		return mAvsVerificationSuccessList;
	}

	public void setAvsVerificationSuccessList(List<String> pAvsVerificationSuccessList) {
		mAvsVerificationSuccessList = pAvsVerificationSuccessList;
	}

	public List<String> getAvsVerificationFailureList() {
		return mAvsVerificationFailureList;
	}

	public void setAvsVerificationFailureList(List<String> pAvsVerificationFailureList) {
		mAvsVerificationFailureList = pAvsVerificationFailureList;
	}

	public List<String> getAvsVerificationNotCarriedList() {
		return mAvsVerificationNotCarriedList;
	}

	public void setAvsVerificationNotCarriedList(List<String> pAvsVerificationNotCarriedList) {
		mAvsVerificationNotCarriedList = pAvsVerificationNotCarriedList;
	}
	
	public List<String> getCvvVerificationSuccessList() {
		return mCvvVerificationSuccessList;
	}

	public void setCvvVerificationSuccessList(List<String> pCvvVerificationSuccessList) {
		mCvvVerificationSuccessList = pCvvVerificationSuccessList;
	}

	public List<String> getCvvVerificationFailureList() {
		return mCvvVerificationFailureList;
	}

	public void setCvvVerificationFailureList(List<String> pCvvVerificationFailureList) {
		mCvvVerificationFailureList = pCvvVerificationFailureList;
	}

	public List<String> getCvvVerificationNotCarriedList() {
		return mCvvVerificationNotCarriedList;
	}

	public void setCvvVerificationNotCarriedList(List<String> pCvvVerificationNotCarriedList) {
		mCvvVerificationNotCarriedList = pCvvVerificationNotCarriedList;
	}
	

	public String getAvsVerificationDefaultValue() {
		return mAvsVerificationDefaultValue;
	}

	public void setAvsVerificationDefaultValue(String pAvsVerificationDefaultValue) {
		mAvsVerificationDefaultValue = pAvsVerificationDefaultValue;
	}

	public String getCvvVerificationDefaultValue() {
		return mCvvVerificationDefaultValue;
	}

	public void setCvvVerificationDefaultValue(String pCvvVerificationDefaultValue) {
		mCvvVerificationDefaultValue = pCvvVerificationDefaultValue;
	}

	
	public boolean isEnableAvsCheck() {
		return mEnableAvsCheck;
	}

	public void setEnableAvsCheck(boolean pEnableAvsCheck) {
		mEnableAvsCheck = pEnableAvsCheck;
	}

	public boolean isEnableCVVCheck() {
		return mEnableCVVCheck;
	}

	public void setEnableCVVCheck(boolean pEnableCVVCheck) {
		mEnableCVVCheck = pEnableCVVCheck;
	}

	
	public Map<String, String> getFraudStateToOrderStateMap() {
		return mFraudStateToOrderStateMap;
	}

	public void setFraudStateToOrderStateMap(Map<String, String> pFraudStateToOrderStateMap) {
		mFraudStateToOrderStateMap = pFraudStateToOrderStateMap;
	}

	public Map<String,String> getCardCodesMap(){
		return mCardCodesMap;
	}
	  
	public void setCardCodesMap(Map<String,String> pCardCodesMap){
		mCardCodesMap=pCardCodesMap;
	}
	
	public Map<String, String> getFraudStatCdMap() {
	    return mFraudStatCdMap;
	}

	public void setFraudStatCdMap(Map<String, String> pFraudStatCdMap) {
	    mFraudStatCdMap = pFraudStatCdMap;
	}
	
	public boolean isGiftCardHasBillingAddress() {
		return mGiftCardHasBillingAddress;
	}

	public void setGiftCardHasBillingAddress(boolean pGiftCardHasBillingAddress) {
		mGiftCardHasBillingAddress = pGiftCardHasBillingAddress;
	}

	public String getAuthSource() {
    return mAuthSource;
  }

  public void setAuthSource(String pAuthSource) {
    mAuthSource = pAuthSource;
  }

  public boolean isEnableFraud(){
	return mEnableFraud;
  }
	
	public void setEnableFraud(boolean pEnableFraud){
		mEnableFraud=pEnableFraud;
	}



	  
	  public String getStoreNumber() {
	    return mStoreNumber;
	  }

	  public void setStoreNumber(String pStoreNumber) {
	    mStoreNumber = pStoreNumber;
	  }

	  public String getTerminalNumber() {
	    return mTerminalNumber;
	  }

	  public void setTerminalNumber(String pTerminalNumber) {
	    mTerminalNumber = pTerminalNumber;
	  }

	  public String getTransNumber() {
	    return mTransNumber;
	  }

	  public void setTransNumber(String pTransNumber) {
	    mTransNumber = pTransNumber;
	  }

	  public String getTenderSequence() {
	    return mTenderSequence;
	  }

	  public void setTenderSequence(String pTenderSequence) {
	    mTenderSequence = pTenderSequence;
	  }

	  public String getRecordIdentifier() {
	    return mRecordIdentifier;
	  }

	  public void setRecordIdentifier(String pRecordIdentifier) {
	    mRecordIdentifier = pRecordIdentifier;
	  }

	  public String getKeySequence() {
	    return mKeySequence;
	  }

	  public void setKeySequence(String pKeySequence) {
	    mKeySequence = pKeySequence;
	  }

	  public String getProcessingIdentifier() {
	    return mProcessingIdentifier;
	  }

	  public void setProcessingIdentifier(String pProcessingIdentifier) {
	    mProcessingIdentifier = pProcessingIdentifier;
	  }

	  public Map<String, String> getTransType() {
	    return mTransType;
	  }

	  public void setTransType(Map<String, String> pTransType) {
	    mTransType = pTransType;
	  }

	  public String getTenderType() {
	    return mTenderType;
	  }

	  public void setTenderType(String pTenderType) {
	    mTenderType = pTenderType;
	  }

	  public String getPosEntryMode() {
	    return mPosEntryMode;
	  }

	  public void setPosEntryMode(String pPosEntryMode) {
	    mPosEntryMode = pPosEntryMode;
	  }

	  public String getCurrencyCode() {
	    return mCurrencyCode;
	  }

	  public void setCurrencyCode(String pCurrencyCode) {
	    mCurrencyCode = pCurrencyCode;
	  }

	  public String getPosSaleConditionCode() {
	    return mPosSaleConditionCode;
	  }

	  public void setPosSaleConditionCode(String pPosSaleConditionCode) {
	    mPosSaleConditionCode = pPosSaleConditionCode;
	  }

	  public String getPosTerminalType() {
	    return mPosTerminalType;
	  }

	  public void setPosTerminalType(String pPosTerminalType) {
	    mPosTerminalType = pPosTerminalType;
	  }

	  public String getPosTerminalEntryCap() {
	    return mPosTerminalEntryCap;
	  }

	  public void setPosTerminalEntryCap(String pPosTerminalEntryCap) {
	    mPosTerminalEntryCap = pPosTerminalEntryCap;
	  }

	  public String getMccCode() {
	    return mMccCode;
	  }

	  public void setMccCode(String pMccCode) {
	    mMccCode = pMccCode;
	  }

	  public String getAccountNumLength() {
	    return mAccountNumLength;
	  }

	  public void setAccountNumLength(String pAccountNumLength) {
	    mAccountNumLength = pAccountNumLength;
	  }
	
	
	
	public Map<String, String> getCardTypeAciCodeMap() {
		return mCardTypeAciCodeMap;
	}

	public void setCardTypeAciCodeMap(Map<String, String> pCardTypeAciCodeMap) {
		mCardTypeAciCodeMap = pCardTypeAciCodeMap;
	}

	public boolean isUseAvsCodeForFraud() {
		return mUseAvsCodeForFraud;
	}

	public void setUseAvsCodeForFraud(boolean pUseAvsCodeForFraud) {
		mUseAvsCodeForFraud = pUseAvsCodeForFraud;
	}

	public boolean isUseCvvCodeForFraud() {
		return mUseCvvCodeForFraud;
	}

	public void setUseCvvCodeForFraud(boolean pUseCvvCodeForFraud) {
		mUseCvvCodeForFraud = pUseCvvCodeForFraud;
	}

	public String getDefaultMopType(){
		return mDefaultMopType;
	}
	
	public void setDefaultMopType(String pDefaultMopType){
		mDefaultMopType=pDefaultMopType;
	}
	
	public String getFraudScreenPipelineChainName(){
		return mFraudScreenPipelineChainName;
	}
	
	public void setFraudScreenPipelineChainName(String pFraudScreenPipelineChainName){
		mFraudScreenPipelineChainName=pFraudScreenPipelineChainName;
	}
	
	public String getOnlineOrderReqType(){
		return mOnlineOrderReqType;
	}
	
	public void setOnlineOrderReqType(String pOnlineOrderReqType){
		mOnlineOrderReqType=pOnlineOrderReqType;
	}
	
	public String getCscOrderReqType(){
		return mCscOrderReqType;
	}
	
	public void setCscOrderReqType(String pCscOrderReqType){
		mCscOrderReqType=pCscOrderReqType;
	}
	
	public LPClient getLPClient() {
		return mLPClient;
	}

	public void setLPClient(LPClient pLPClient) {
		mLPClient = pLPClient;
	}

	public String getTokenizeTransactionAction() {
		return mTokenizeTransactionAction;
	}

	public void setTokenizeTransactionAction(String pTokenizeTransactionAction) {
		mTokenizeTransactionAction = pTokenizeTransactionAction;
	}

	public String getAuthorizeTransactionAction() {
		return mAuthorizeTransactionAction;
	}

	public void setAuthorizeTransactionAction(String pAuthorizeTransactionAction) {
		mAuthorizeTransactionAction = pAuthorizeTransactionAction;
	}

	public String getCreditTransactionAction() {
		return mCreditTransactionAction;
	}

	public void setCreditTransactionAction(String pCreditTransactionAction) {
		mCreditTransactionAction = pCreditTransactionAction;
	}

	public String getDebitTransactionAction() {
		return mDebitTransactionAction;
	}

	public void setDebitTransactionAction(String pDebitTransactionAction) {
		mDebitTransactionAction = pDebitTransactionAction;
	}
	
	public String getFraudTransactionAction() {
		return mFraudTransactionAction;
	}

	public void setFraudTransactionAction(String pFraudTransactionAction) {
		mFraudTransactionAction = pFraudTransactionAction;
	}
	
	public String getAuthReversalTransactionAction() {
		return mAuthReversalTransactionAction;
	}

	public void setAuthReversalTransactionAction(String pAuthReversalTransactionAction) {
		mAuthReversalTransactionAction = pAuthReversalTransactionAction;
	}

	public String getTokenizePipelineChainName() {
		return mTokenizePipelineChainName;
	}

	public void setTokenizePipelineChainName(String pTokenizePipelineChainName) {
		mTokenizePipelineChainName = pTokenizePipelineChainName;
	}

	public String getAuthorizePipelineChainName() {
		return mAuthorizePipelineChainName;
	}

	public void setAuthorizePipelineChainName(String pAuthorizePipelineChainName) {
		mAuthorizePipelineChainName = pAuthorizePipelineChainName;
	}

	public String getDebitTransactionChainName() {
		return mDebitTransactionChainName;
	}

	public void setDebitTransactionChainName(String pDebitTransactionChainName) {
		mDebitTransactionChainName = pDebitTransactionChainName;
	}

	public String getCreditTransactionChainName() {
		return mCreditTransactionChainName;
	}

	public void setCreditTransactionChainName(String pCreditTransactionChainName) {
		mCreditTransactionChainName = pCreditTransactionChainName;
	}

	public String getCreditCardClassType(){
		return mCreditCardClassType;
	}
	
	public void setCreditCardClassType(String pCreditCardClassType){
		mCreditCardClassType=pCreditCardClassType;
	}
	
	
	public void setGiftCardClassType(String pGiftCardClassType){
		mGiftCardClassType=pGiftCardClassType;
	}
	
	public String getGiftCardClassType(){
		return mGiftCardClassType;
	}
	
	
	public boolean isEnableTieBack(){
		return mEnableTieBack;
	}
	
	public void setEnableTieBack(boolean pEnableTieBack){
		mEnableTieBack=pEnableTieBack;
	}
	
	public String getFraudSubClientId(){
		return mFraudSubClientId;
	}
	
	public void setFraudSubClientId(String pFraudSubClientId){
		mFraudSubClientId=pFraudSubClientId;
	}
	
	public String getCreditCardSubClientId(){
		return mCreditCardSubClientId;
	}
	
	public void setCreditCardSubClientId(String pCreditCardSubClientId){
		mCreditCardSubClientId=pCreditCardSubClientId;
	}
	
	public String getDefaultTimeZone(){
		return mDefaultTimeZone;
	}
	
	public void setDefaultTimeZone(String pDefaultTimeZone){
		mDefaultTimeZone=pDefaultTimeZone;
	}
	
	public Map<String,String> getCardTypeCodeValueMap(){
		return mCardTypeCodeValueMap;
	}
	
	public void setCardTypeCodeValueMap(Map<String,String> pCardTypeCodeValueMap){
		mCardTypeCodeValueMap=pCardTypeCodeValueMap;
	}
	
	public String getDefaultCurrencyCode(){
		return this.mDefaultCurrencyCode;
	}
	
	public void setDefaultCurrencyCode(String pDefaultCurrencyCode){
		this.mDefaultCurrencyCode=pDefaultCurrencyCode;
	}
	
	public Map<String,String> getMaxFieldLengthsMap(){
		return this.mMaxFieldLengthsMap;
	}
	
	public void setMaxFieldLengthsMap(Map<String,String> pMaxFieldLengthsMap){
		this.mMaxFieldLengthsMap=pMaxFieldLengthsMap;
	}
	
	public String getClientId() {
		return mClientId;
	}

	public void setClientId(String pClientId) {
		this.mClientId = pClientId;
	}

	public String getAciConfigurationPath(){
		return this.mAciConfigurationPath;
	}
	
	public void setAciConfigurationPath(String pAciConfigurationPath){
		this.mAciConfigurationPath=pAciConfigurationPath;
	}
	
	public boolean getUseSSL(){
		return this.mUseSSL;
	}
	
	public void setUseSSL(boolean pUseSSL){
		this.mUseSSL=pUseSSL;
	}
		

	public String getDiv_Num_Fraud_Only() {
		return mDiv_Num_Fraud_Only;
	}

	public void setDiv_Num_Fraud_Only(String pDiv_Num_Fraud_Only) {
		this.mDiv_Num_Fraud_Only = pDiv_Num_Fraud_Only;
	}
	
	public String getDiv_Num_CC() {
		return mDiv_Num_CC;
	}

	public void setDiv_Num_CC(String pDiv_Num_CC) {
		this.mDiv_Num_CC = pDiv_Num_CC;
	}

	public String getS_Key_Id() {
		return mS_Key_Id;
	}

	public void setS_Key_Id(String pS_Key_Id) {
		this.mS_Key_Id = pS_Key_Id;
	}
	
	public String getEbWebsite (){
		return this.mEbWebsite;
	}
	
	public void setEbWebsite(String pEbWebsite){
		this.mEbWebsite=pEbWebsite;
	}
	
	
	public Map<String,String> getCountryValueMap(){
		return this.mCountryValueMap;
	}
	
	public void setCountryValueMap(Map<String,String> pCountryValueMap){
		this.mCountryValueMap=pCountryValueMap;
	}
	
	public LPClient getClient(){
		return mLPClient;
	}
	
	public String getAuthReversalPipelineChainName() {
		return mAuthReversalPipelineChainName;
	}

	public void setAuthReversalPipelineChainName(String pAuthReversalPipelineChainName) {
		mAuthReversalPipelineChainName = pAuthReversalPipelineChainName;
	}

	public boolean isUseOriginalAuthDateTime() {
		return mUseOriginalAuthDateTime;
	}

	public void setUseOriginalAuthDateTime(boolean pUseOriginalAuthDateTime) {
		mUseOriginalAuthDateTime = pUseOriginalAuthDateTime;
	}

	
	public void doStartService()
	        throws ServiceException
    {
		LPClient.addProviders();
		initializeClient();
    }
	
	
	private void initializeClient(){
		if(isLoggingInfo()) {
			logInfo("****************************************************");
			logInfo("AciConfigurationService: Inside Initialize client");
			logInfo("****************************************************");
		}
		try{
			if(getUseSSL()){
				if(isLoggingInfo()){
					logInfo("Create LPClient in secure mode");
				}
				mLPClient=new LPClient(getAciConfigurationPath(),true);
			}
			else {
				if(isLoggingInfo()){
					logInfo("Create LPClient in clear mode");
				}
				mLPClient= new LPClient(getAciConfigurationPath(),false);
			}
		}
		catch(LPIniFileException ex){
			if(isLoggingInfo()) {
				logInfo("****************************************************");
				logInfo("AciConfigurationService: Initialize Client LPIniFileException");
				logInfo("****************************************************");
			}
			vlogError(ex,"AciConfigurationService: Initialize Client LPIniFileException: Path of file: "+getAciConfigurationPath()+"/lp52.ini: Exception: ");
			mLPClient=null;
		}
	}
	
	
	
	

}
