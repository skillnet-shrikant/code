package com.mff.userprofiling.util;

/**
 * Bean to hold Tax exemption info.
 * 
 * @author DMI
 */
public class TaxExemptionInfo {
	
	private String mNickName = null;
	private String mClassificationId= null;
	private String mClassificationCode= null;
	private String mClassificationName= null;
	private String mTaxId = null;
	private String mOrgName = null;
	private String mBusinessDesc = null;
	private String mMerchandise = null;
	private String mTaxCity = null;
	private String mTaxState = null;
	
	public String getNickName() {
		return mNickName;
	}
	public void setNickName(String pNickName) {
		this.mNickName = pNickName;
	}
	public String getClassificationId() {
		return mClassificationId;
	}
	public void setClassificationId(String pClassification) {
		this.mClassificationId = pClassification;
	}
	public String getTaxId() {
		return mTaxId;
	}
	public void setTaxId(String pTaxId) {
		this.mTaxId = pTaxId;
	}
	public String getOrgName() {
		return mOrgName;
	}
	public void setOrgName(String pOrgName) {
		this.mOrgName = pOrgName;
	}
	public String getBusinessDesc() {
		return mBusinessDesc;
	}
	public void setBusinessDesc(String pBusinessDesc) {
		this.mBusinessDesc = pBusinessDesc;
	}
	public String getMerchandise() {
		return mMerchandise;
	}
	public void setMerchandise(String pMerchandise) {
		this.mMerchandise = pMerchandise;
	}
	public String getTaxCity() {
		return mTaxCity;
	}
	public void setTaxCity(String pTaxCity) {
		this.mTaxCity = pTaxCity;
	}
	public String getTaxState() {
		return mTaxState;
	}
	public void setTaxState(String pTaxState) {
		this.mTaxState = pTaxState;
	}
	public String getClassificationName() {
		return mClassificationName;
	}
	public void setClassificationName(String pClassificationName) {
		mClassificationName = pClassificationName;
	}
	public String getClassificationCode() {
		return mClassificationCode;
	}
	public void setClassificationCode(String pClassificationCode) {
		mClassificationCode = pClassificationCode;
	}
}