package com.mff.commerce.order.purchase;

/**
 * A bean to hold the inventory error messages for commerce items 
 */
public class MFFInventoryMessage {

	private String mCommerceItemId;
	private String mSkuId;
	private String mProductId;
	private String mErrorMessage;
	private boolean mRemoved;
	private Long mCartQty;
	private Long mAvailQty;
		
	public Long getCartQty() {
		return mCartQty;
	}
	public void setCartQty(Long pCartQty) {
		this.mCartQty = pCartQty;
	}
	public Long getAvailQty() {
		return mAvailQty;
	}
	public void setAvailQty(Long pAvailQty) {
		this.mAvailQty = pAvailQty;
	}
	public String getProductId() {
		return mProductId;
	}
	public void setProductId(String pProductId) {
		mProductId = pProductId;
	}
	public String getCommerceItemId() {
		return mCommerceItemId;
	}
	public void setCommerceItemId(String pCommerceItemId) {
		mCommerceItemId = pCommerceItemId;
	}
	public String getSkuId() {
		return mSkuId;
	}
	public void setSkuId(String pSkuId) {
		mSkuId = pSkuId;
	}
	public String getErrorMessage() {
		return mErrorMessage;
	}
	public void setErrorMessage(String pErrorMessage) {
		mErrorMessage = pErrorMessage;
	}
	public boolean isRemoved() {
		return mRemoved;
	}
	public void setRemoved(boolean pRemoved) {
		mRemoved = pRemoved;
	}
	
	
}
