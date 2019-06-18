package com.aci.pipeline.result;


import atg.service.pipeline.PipelineResultImpl;
public class AciPipelineResult extends PipelineResultImpl {


	private boolean mTransactionSuccess;
	private boolean mFraudTransactionSuccess;
	private String mFraudResult;
	private String mTokenNumber;
	private String mAuthorizationCode;
	private String mPaymentResponseCode;
	private String mPaymentResponseMessage;
	private String mAddressValidationCode;
	private String mCvvValidationCode;
	private String mCallType;
	private String mAciReqId;
	private String mMopTypeCd;
	private String mOriginalJournalKey;
	private String mOriginalAciAuthDate;
	private String mOriginalAciAuthTime;
	private String mStatusCode;
		
	  
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
	
	public String getAciReqId(){
		return mAciReqId;
	}
	
	public void setAciReqId(String pAciReqId){
		mAciReqId=pAciReqId;
	}
	
	public boolean isTransactionSuccess() {
		return mTransactionSuccess;
	}
	public void setTransactionSuccess(boolean pTransactionSuccess) {
		mTransactionSuccess = pTransactionSuccess;
	}
	public boolean isFraudTransactionSuccess() {
		return mFraudTransactionSuccess;
	}
	public void setFraudTransactionSuccess(boolean pFraudTransactionSuccess) {
		mFraudTransactionSuccess = pFraudTransactionSuccess;
	}
	public String getFraudResult() {
		return mFraudResult;
	}
	public void setFraudResult(String pFraudResult) {
		mFraudResult = pFraudResult;
	}
	public String getTokenNumber() {
		return mTokenNumber;
	}
	public void setTokenNumber(String pTokenNumber) {
		mTokenNumber = pTokenNumber;
	}
	public String getAuthorizationCode() {
		return mAuthorizationCode;
	}
	public void setAuthorizationCode(String pAuthorizationCode) {
		mAuthorizationCode = pAuthorizationCode;
	}
	public String getPaymentResponseCode() {
		return mPaymentResponseCode;
	}
	public void setPaymentResponseCode(String pPaymentResponseCode) {
		mPaymentResponseCode = pPaymentResponseCode;
	}
	public String getPaymentResponseMessage() {
		return mPaymentResponseMessage;
	}
	public void setPaymentResponseMessage(String pPaymentResponseMessage) {
		mPaymentResponseMessage = pPaymentResponseMessage;
	}
	public String getAddressValidationCode() {
		return mAddressValidationCode;
	}
	public void setAddressValidationCode(String pAddressValidationCode) {
		mAddressValidationCode = pAddressValidationCode;
	}
	public String getCvvValidationCode() {
		return mCvvValidationCode;
	}
	public void setCvvValidationCode(String pCvvValidationCode) {
		mCvvValidationCode = pCvvValidationCode;
	}
	public String getCallType() {
		return mCallType;
	}
	public void setCallType(String pCallType) {
		mCallType = pCallType;
	}

  public String getStatusCode() {
    return mStatusCode;
  }

  public void setStatusCode(String pStatusCode) {
    mStatusCode = pStatusCode;
  }
	
	
	
}
