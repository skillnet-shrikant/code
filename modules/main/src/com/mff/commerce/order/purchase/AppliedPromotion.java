package com.mff.commerce.order.purchase;

/**
 * POJO to hold the applied promotion details through a Coupon This object will
 * be used in the jsps to display the promotions
 * 
 * @author DMI
 *
 */
public class AppliedPromotion {

	private String mPromoId;
	private String mCouponCode;
	private String mDescription;
	private Double mDiscountAmount;
	private String mDiscountType;
	
	// 2564 - Handle free item shipping promos
	private boolean itemShipping;

	public boolean isItemShipping() {
		return itemShipping;
	}

	public void setItemShipping(boolean pItemShipping) {
		itemShipping = pItemShipping;
	}

	private String promoName;
	private String promoShortDesc;

	public String getPromoId() {
		return mPromoId;
	}

	public void setPromoId(String pPromoId) {
		mPromoId = pPromoId;
	}

	public String getCouponCode() {
		return mCouponCode;
	}

	public void setCouponCode(String pCouponCode) {
		mCouponCode = pCouponCode;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String pDescription) {
		mDescription = pDescription;
	}

	public Double getDiscountAmount() {
		return mDiscountAmount;
	}

	public void setDiscountAmount(Double pDiscountAmount) {
		mDiscountAmount = pDiscountAmount;
	}

	public String getDiscountType() {
		return mDiscountType;
	}

	public void setDiscountType(String pDiscountType) {
		mDiscountType = pDiscountType;
	}
	
	public String getPromoName() {
		return promoName;
	}

	public void setPromoName(String pPromoName) {
		promoName = pPromoName;
	}

	public String getPromoShortDesc() {
		return promoShortDesc;
	}

	public void setPromoShortDesc(String pPromoShortDesc) {
		promoShortDesc = pPromoShortDesc;
	}

	@Override
	public String toString() {
		String toString = "[" + getPromoId() + "," + getCouponCode() + ","
				+ getDescription() + "," + getDiscountAmount() + ","
				+ getDiscountType() + "," + getPromoName() + "," + getPromoShortDesc() + "]";
		return toString;
	}
}
