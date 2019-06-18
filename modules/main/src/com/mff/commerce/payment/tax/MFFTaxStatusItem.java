package com.mff.commerce.payment.tax;

public class MFFTaxStatusItem {
	private double mCountyTax;
	private double mCityTax;
	private double mStateTax;
	private double mDistrictTax;
	private String mCommerceItemId;
	private String mSku;
	private double mAmount;
	private double mTax;
	private String mCountyTaxInfo;

	public double getCountyTax() {
		return mCountyTax;
	}

	public void setCountyTax(double mCountyTax) {
		this.mCountyTax = mCountyTax;
	}

	public double getCityTax() {
		return mCityTax;
	}

	public void setCityTax(double mCityTax) {
		this.mCityTax = mCityTax;
	}

	public double getStateTax() {
		return mStateTax;
	}

	public void setStateTax(double mStateTax) {
		this.mStateTax = mStateTax;
	}

	public double getDistrictTax() {
		return mDistrictTax;
	}

	public void setDistrictTax(double mDistrictTax) {
		this.mDistrictTax = mDistrictTax;
	}

	public String getCommerceItemId() {
		return mCommerceItemId;
	}

	public void setCommerceItemId(String pCommerceItemId) {
		this.mCommerceItemId = pCommerceItemId;
	}

	public String getSku() {
		return mSku;
	}

	public void setSku(String pSku) {
		this.mSku = pSku;
	}

	public double getAmount() {
		return mAmount;
	}

	public void setAmount(double pAmount) {
		this.mAmount = pAmount;
	}

	public double getTax() {
		return mTax;
	}

	public void setTax(double pTax) {
		this.mTax = pTax;
	}
	
	public String getCountyTaxInfo() {
    return mCountyTaxInfo;
  }

  public void setCountyTaxInfo(String pCountyTaxInfo) {
    mCountyTaxInfo = pCountyTaxInfo;
  }

}
