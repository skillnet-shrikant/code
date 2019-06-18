package com.mff.commerce.order.purchase;

import atg.core.util.ContactInfo;

public class AddressVerificationVO {
	
	/*
	 * Flag which holds the AVS Call status
	 * If true then we will use the AVS results
	 * If false then it could be a system error at back-end, ignore AVS result and proceed with checkout
	 */
	private boolean mAvsSuccess=false;
	/*
	 * Flag that denotes if AVS Verification was done
	 */
	private boolean mAvsVerified=false;
	/*
	 * Entered Address
	 */
	private ContactInfo mEnteredAddress;
	/*
	 * Suggested Address
	 */
	private ContactInfo mSuggestedAddress;
	/*
	 * Flag to control to display of suggested address for user selection
	 */
	private boolean mDisplaySuggestion=false;
	
	public AddressVerificationVO(){
		
	}
	
	public AddressVerificationVO(boolean pAvsSuccess,boolean pAvsVerified,ContactInfo pEnteredAddress,ContactInfo pSuggestedAddress,boolean pDisplaySuggestion){
		this.mAvsSuccess = pAvsSuccess;
		this.mAvsVerified = pAvsVerified;
		this.mEnteredAddress = pEnteredAddress;
		this.mSuggestedAddress = pSuggestedAddress;
		this.mDisplaySuggestion = pDisplaySuggestion;
	}
	
	/**
	 * @return the mAvsSuccess
	 */
	public boolean isAvsSuccess() {
		return mAvsSuccess;
	}

	/**
	 * @param pAvsSuccess the mAvsSuccess to set
	 */
	public void setAvsSuccess(boolean pAvsSuccess) {
		this.mAvsSuccess = pAvsSuccess;
	}

	/**
	 * @return the mAvsVerified
	 */
	public boolean isAvsVerified() {
		return mAvsVerified;
	}
	/**
	 * @param pAvsVerified the mAvsVerified to set
	 */
	public void setAvsVerified(boolean pAvsVerified) {
		this.mAvsVerified = pAvsVerified;
	}
	/**
	 * @return the mEnteredAddress
	 */
	public ContactInfo getEnteredAddress() {
		return mEnteredAddress;
	}
	/**
	 * @param pEnteredAddress the mEnteredAddress to set
	 */
	public void setEnteredAddress(ContactInfo pEnteredAddress) {
		this.mEnteredAddress = pEnteredAddress;
	}
	/**
	 * @return the mSuggestedAddress
	 */
	public ContactInfo getSuggestedAddress() {
		return mSuggestedAddress;
	}
	/**
	 * @param pSuggestedAddress the mSuggestedAddress to set
	 */
	public void setSuggestedAddress(ContactInfo pSuggestedAddress) {
		this.mSuggestedAddress = pSuggestedAddress;
	}

	/**
	 * @return the mDisplaySuggestion
	 */
	public boolean isDisplaySuggestion() {
		return mDisplaySuggestion;
	}

	/**
	 * @param pDisplaySuggestion the mDisplaySuggestion to set
	 */
	public void setDisplaySuggestion(boolean pDisplaySuggestion) {
		this.mDisplaySuggestion = pDisplaySuggestion;
	}

}
