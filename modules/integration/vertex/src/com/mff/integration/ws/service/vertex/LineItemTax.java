package com.mff.integration.ws.service.vertex;

public class LineItemTax {

	private String mLineItemId;
	private double mCityTax;
	private double mCountyTax;
	private double mDistrictTax = 0.0D;
	private double mStateTax;
	private double mTotalTax;
	private String mLineItemInfo;
	private String mCountyTaxInfo;

	public String getLineItemId() {
		return mLineItemId;
	}

	public void setLineItemId(String mLineItemId) {
		this.mLineItemId = mLineItemId;
	}

	public double getCityTax() {
		return mCityTax;
	}

	public void setCityTax(double mCityTax) {
		this.mCityTax = mCityTax;
	}

	public double getCountyTax() {
		return mCountyTax;
	}

	public void setCountyTax(double mCountyTax) {
		this.mCountyTax = mCountyTax;
	}
	
	public double getDistrictTax() {
		return mDistrictTax;
	}

	public void setDistrictTax(double mDistrictTax) {
		this.mDistrictTax = mDistrictTax;
	}

	public double getStateTax() {
		return mStateTax;
	}

	public void setStateTax(double pStateTax) {
		this.mStateTax = pStateTax;
	}

	public double getTotalTax() {
		return mTotalTax;
	}

	public void setTotalTax(double mTotalTax) {
		this.mTotalTax = mTotalTax;
	}

	public String getLineItemInfo() {
		return mLineItemInfo;
	}

	public void setLineItemInfo(String mLineItemInfo) {
		this.mLineItemInfo = mLineItemInfo;
	}

  public String getCountyTaxInfo() {
    return mCountyTaxInfo;
  }

  public void setCountyTaxInfo(String pCountyTaxInfo) {
    mCountyTaxInfo = pCountyTaxInfo;
  }
	
}
