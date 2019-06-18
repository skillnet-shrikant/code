package com.mff.util;


/**
 * This bean holds shipping method info to be displayed on Shipping methods page.
 * 
 */
public class ShippingMethod {
	
	String mShippingMethodName;
	String mShippingMethodNote;
	String mShippingMethodAmount;
	public String getShippingMethodName() {
		return mShippingMethodName;
	}
	public void setShippingMethodName(String pShippingMethodName) {
		mShippingMethodName = pShippingMethodName;
	}
	public String getShippingMethodNote() {
		return mShippingMethodNote;
	}
	public void setShippingMethodNote(String pShippingMethodNote) {
		mShippingMethodNote = pShippingMethodNote;
	}
	public String getShippingMethodAmount() {
		return mShippingMethodAmount;
	}
	public void setShippingMethodAmount(String pShippingMethodAmount) {
		mShippingMethodAmount = pShippingMethodAmount;
	}
}