package com.mff.integration.ws.service.vertex;

import java.util.List;

public class Tax {

	private double mCityTax;
	private double mCountyTax;
	private double mStateTax;
	private double mTotalTax;
	private List<LineItemTax> mLineItemTax;

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

	public double getStateTax() {
		return mStateTax;
	}

	public void setStateTax(double mStateTax) {
		this.mStateTax = mStateTax;
	}

	public double getTotalTax() {
		return mTotalTax;
	}

	public void setTotalTax(double pTotalTax) {
		this.mTotalTax = pTotalTax;
	}

	public List<LineItemTax> getLineItemTax() {
		return mLineItemTax;
	}

	public void setLineItemTax(List<LineItemTax> pLineItemTax) {
		this.mLineItemTax = pLineItemTax;
	}

}
