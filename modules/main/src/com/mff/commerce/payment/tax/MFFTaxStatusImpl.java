package com.mff.commerce.payment.tax;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.payment.DummyTaxStatus;

public class MFFTaxStatusImpl extends DummyTaxStatus {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Shipping Group **/
	private String mShippingGroupId;

	/** Shipping Tax **/
	private double mShippingTax;

	/** Shipping Tax **/
	private Map<String, Double> mShippingTaxMap = new HashMap<String, Double>();
	
	/** County Tax Info */
	private String mCountyTaxInfo;
	
	public String getCountyTaxInfo() {
    return mCountyTaxInfo;
  }

  public void setCountyTaxInfo(String pCountyTaxInfo) {
    mCountyTaxInfo = pCountyTaxInfo;
  }

	public Map<String, Double> getShippingTaxMap() {
		return mShippingTaxMap;
	}

	public void setShippingTaxMap(Map<String, Double> pShippingTaxMap) {
		this.mShippingTaxMap = pShippingTaxMap;
	}

	/** Map of taxable items **/
	private Map<String, MFFTaxStatusItem> mEXTNTaxStatusItems = new HashMap<String, MFFTaxStatusItem>();

	public String getShippingGroupId() {
		return mShippingGroupId;
	}

	public void setShippingGroupId(String pShippingGroupId) {
		this.mShippingGroupId = pShippingGroupId;
	}

	public Map<String, MFFTaxStatusItem> getEXTNTaxStatusItems() {
		return mEXTNTaxStatusItems;
	}

	public void setEXTNTaxStatusItems(
			Map<String, MFFTaxStatusItem> pEXTNTaxStatusItems) {
		this.mEXTNTaxStatusItems = pEXTNTaxStatusItems;
	}

	public double getShippingTax() {
		return mShippingTax;
	}

	public void setShippingTax(double pShippingTax) {
		this.mShippingTax = pShippingTax;
	}

}
