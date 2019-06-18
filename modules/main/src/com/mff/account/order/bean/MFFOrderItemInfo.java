package com.mff.account.order.bean;

public class MFFOrderItemInfo {

	private String mProductDisplayName;

	private String mProductId;

	private String mCatalogRefId;

	private long mQuantity;

	private String mSiteId;

	private double mListPrice;

	private double mSalePrice;

	double mAmount;
	
	double mLineItemTotal;
	
	private boolean mGwp;

	private String mTrackingNumber;

	public String getProductDisplayName() {
		return mProductDisplayName;
	}

	public void setProductDisplayName(String pProductDisplayName) {
		mProductDisplayName = pProductDisplayName;
	}

	public String getProductId() {
		return mProductId;
	}

	public void setProductId(String pProductId) {
		mProductId = pProductId;
	}

	public double getListPrice() {
		return mListPrice;
	}

	public void setListPrice(double pListPrice) {
		mListPrice = pListPrice;
	}

	public double getSalePrice() {
		return mSalePrice;
	}

	public void setSalePrice(double pSalePrice) {
		mSalePrice = pSalePrice;
	}

	public String getTrackingNumber() {
		return mTrackingNumber;
	}

	public void setTrackingNumber(String pTrackingNumber) {
		mTrackingNumber = pTrackingNumber;
	}

	public String getCatalogRefId() {
		return mCatalogRefId;
	}

	public void setCatalogRefId(String pCatalogRefId) {
		mCatalogRefId = pCatalogRefId;
	}

	public void setAmount(double pAmount) {
		this.mAmount = pAmount;
	}

	public double getAmount() {
		return this.mAmount;
	}

	public long getQuantity() {
		return mQuantity;
	}

	public void setQuantity(long pQuantity) {
		mQuantity = pQuantity;
	}

	public String getSiteId() {
		return mSiteId;
	}

	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}

  public boolean isGwp() {
    return mGwp;
  }

  public void setGwp(boolean pGwp) {
    mGwp = pGwp;
  }

	public double getLineItemTotal() {
		return mLineItemTotal;
	}
	
	public void setLineItemTotal(double pLineItemTotal) {
		mLineItemTotal = pLineItemTotal;
	}

}
