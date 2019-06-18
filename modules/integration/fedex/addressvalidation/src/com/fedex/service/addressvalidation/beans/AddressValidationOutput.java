package com.fedex.service.addressvalidation.beans;



public class AddressValidationOutput {

	private String mClassification;
	private String mEffectiveStreetLine1;
	private String mEffectiveStreetLine2;
	private String mEffectiveCity;
	private String mEffectiveState;
	private String mEffectiveZipCode;
	private boolean mCountrySupported;
	private boolean mSuiteRequiredButMissing;
	private boolean mInvalidSuiteNumber;
	private boolean mMultipleMatches;
	private boolean mResolved;
	private boolean mDPV;
	private boolean mValidMultiUnit;
	private boolean mPOBox;
	private boolean mMultiUnitBase;
	private boolean mStreetAddressValidated;
	private boolean mRRConversion;
	private boolean mStreetRangeValidated;
	private boolean mPostalCodeValidated;
	private boolean mCityStateValidated;
	


	public boolean isStreetRangeValidated() {
		return mStreetRangeValidated;
	}

	public void setStreetRangeValidated(boolean mStreetRangeValidated) {
		this.mStreetRangeValidated = mStreetRangeValidated;
	}
	
	public boolean isPostalCodeValidated() {
		return mPostalCodeValidated;
	}

	public void setPostalCodeValidated(boolean mPostalCodeValidated) {
		this.mPostalCodeValidated = mPostalCodeValidated;
	}
	
	public boolean isCityStateValidated() {
		return mCityStateValidated;
	}

	public void setCityStateValidated(boolean mCityStateValidated) {
		this.mCityStateValidated = mCityStateValidated;
	}

	public boolean isCountrySupported() {
		return mCountrySupported;
	}

	public void setCountrySupported(boolean mCountrySupported) {
		this.mCountrySupported = mCountrySupported;
	}

	public boolean isSuiteRequiredButMissing() {
		return mSuiteRequiredButMissing;
	}

	public void setSuiteRequiredButMissing(boolean mSuiteRequiredButMissing) {
		this.mSuiteRequiredButMissing = mSuiteRequiredButMissing;
	}

	public boolean isInvalidSuiteNumber() {
		return mInvalidSuiteNumber;
	}

	public void setInvalidSuiteNumber(boolean mInvalidSuiteNumber) {
		this.mInvalidSuiteNumber = mInvalidSuiteNumber;
	}

	public boolean isMultipleMatches() {
		return mMultipleMatches;
	}

	public void setMultipleMatches(boolean mMultipleMatches) {
		this.mMultipleMatches = mMultipleMatches;
	}

	public boolean isResolved() {
		return mResolved;
	}

	public void setResolved(boolean mResolved) {
		this.mResolved = mResolved;
	}

	public boolean isDPV() {
		return mDPV;
	}

	public void setDPV(boolean mDPV) {
		this.mDPV = mDPV;
	}

	public boolean isValidMultiUnit() {
		return mValidMultiUnit;
	}

	public void setValidMultiUnit(boolean mValidMultiUnit) {
		this.mValidMultiUnit = mValidMultiUnit;
	}

	public boolean isPOBox() {
		return mPOBox;
	}

	public void setPOBox(boolean mPOBox) {
		this.mPOBox = mPOBox;
	}

	public boolean isMultiUnitBase() {
		return mMultiUnitBase;
	}

	public void setMultiUnitBase(boolean mMultiUnitBase) {
		this.mMultiUnitBase = mMultiUnitBase;
	}

	public boolean isStreetAddressValidated() {
		return mStreetAddressValidated;
	}

	public void setStreetAddressValidated(boolean mStreetAddressValidated) {
		this.mStreetAddressValidated = mStreetAddressValidated;
	}

	public boolean isRRConversion() {
		return mRRConversion;
	}

	public void setRRConversion(boolean mRRConversion) {
		this.mRRConversion = mRRConversion;
	}

	public String getClassification() {
		return mClassification;
	}

	public void setClassification(String pClassification) {
		this.mClassification = pClassification;
	}

	public String getEffectiveCity() {
		return mEffectiveCity;
	}

	public void setEffectiveCity(String pCity) {
		this.mEffectiveCity = pCity;
	}

	public String getEffectiveState() {
		return mEffectiveState;
	}

	public void setEffectiveState(String pState) {
		this.mEffectiveState = pState;
	}

	public String getEffectiveZipCode() {
		return mEffectiveZipCode;
	}

	public void setEffectiveZipCode(String pZipCode) {
		this.mEffectiveZipCode = pZipCode;
	}

	public String getEffectiveStreetLine1() {
		return mEffectiveStreetLine1;
	}

	public void setEffectiveStreetLine1(String pStreetLine1) {
		this.mEffectiveStreetLine1 = pStreetLine1;
	}

	public String getEffectiveStreetLine2() {
		return mEffectiveStreetLine2;
	}

	public void setEffectiveStreetLine2(String pStreetLine2) {
		this.mEffectiveStreetLine2 = pStreetLine2;
	}

	@Override
	public String toString() {
		return "AddressValidationOutput [Classification=" + mClassification
				+ ", StreetLine1=" + mEffectiveStreetLine1
				+ ", StreetLine2=" + mEffectiveStreetLine2
				+ ", City=" + mEffectiveCity + ", State="
				+ mEffectiveState + ", ZipCode=" + mEffectiveZipCode+"]";
	}
	
}
