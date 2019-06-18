package com.aci.payment.creditcard;


import atg.payment.creditcard.GenericCreditCardInfo;

/**
 * Simple bean-y extends of GenericCreditCardInfo.  Surprised there is not one OOTB.
 *
 */
public class AciCreditCardAuthReversalInfo extends GenericCreditCardInfo {
	
	private static final long serialVersionUID = 28347896L;
	private String mTokenNumber;
	private String mNameOnCard;
	private String mAuthorizationNumber;
	private String mTransactionNumber;
	private boolean mOnlineOrder;
	private String mMopTypeCd;
	private String mOriginalReqId;
	private String mOriginalJournalKey;
	private double mAuthReversalAmount;
	

	
	//------------------------------------------
	// GETTERS & SETTERS
	// ------------------------------------------
	
	public String getOriginalReqId(){
		return mOriginalReqId;
	}
	
	public void setOriginalReqId(String pOriginalReqId){
		mOriginalReqId=pOriginalReqId;
	}
	
	public String getOriginalJournalKey(){
		return mOriginalJournalKey;
	}
	
	public void setOriginalJournalKey(String pOriginalJournalKey){
		mOriginalJournalKey=pOriginalJournalKey;
	}
	
	public double getAuthReversalAmount(){
		return mAuthReversalAmount;
	}
	
	public void setAuthReversalAmount(double pAuthReversalAmount){
		mAuthReversalAmount=pAuthReversalAmount;
	}
	
	
	
	public String getNameOnCard(){
		return mNameOnCard;
	}
	
	public void setNameOnCard(String pNameOnCard){
		mNameOnCard=pNameOnCard;
	}
	
	public boolean isOnlineOrder(){
		return mOnlineOrder;
	}
	
	public void setOnlineOrder(boolean pOnlineOrder){
		mOnlineOrder=pOnlineOrder;
	}
	public String getTokenNumber() {
		return mTokenNumber;
	}
	public void setTokenNumber(String pTokenNumber) {
		this.mTokenNumber = pTokenNumber;
	}
	public String getAuthorizationNumber() {
		return mAuthorizationNumber;
	}
	public void setAuthorizationNumber(String pAuthorizationNumber) {
		this.mAuthorizationNumber = pAuthorizationNumber;
	}
	public String getTransactionNumber() {
		return mTransactionNumber;
	}
	public void setTransactionNumber(String pTransactionNumber) {
		this.mTransactionNumber = pTransactionNumber;
	}
	public String getMopTypeCd(){
		return mMopTypeCd;
	}
	public void setMopTypeCd(String pMopTypeCd){
		mMopTypeCd=pMopTypeCd;
	}
	
}