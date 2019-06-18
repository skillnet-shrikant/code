package com.mff.account.order.bean;

import java.util.ArrayList;
import java.util.Date;

import atg.core.util.ContactInfo;

public class MFFOrderDetails {

	private boolean mBopisOrder;
	private String mBopisStore;
	private String mBopisPerson;
	private String mBopisEmail;
	private String mContactEmail;
	private String mAttention;
	
	private Date mSubmittedDate;

	private String mState;

	private String mOrderNumber;

	private String mId;
	private boolean mLegacyOrder;

	private double mAmount;
	private double mRawSubtotal;
	private double mOrderTotal;
	private double mMerchandiseTotal;
	private double mDiscountAmount;
	private double mOrderChargeAmount;
	private double mGiftCardPaymentTotal;
	private double orderSubTotalLessGiftCard;

	private double mTax;
	private double mShipping;

	boolean mDiscounted;

	private ContactInfo mShippingInfo;
	private String mTrackingNumber;
	private ArrayList<MFFOrderBillingInfo> mPaymentGroups;

	private ArrayList<MFFOrderItemInfo> mCommerceItems;

	private String mShippingMethod;

	private boolean mIsSaturdayDelivery;

	public Date getSubmittedDate() {
		return mSubmittedDate;
	}

	public void setSubmittedDate(Date pSubmittedDate) {
		this.mSubmittedDate = pSubmittedDate;
	}

	public ContactInfo getShippingInfo() {
		return this.mShippingInfo;
	}

	public void setShippingInfo(ContactInfo pShippingInfo) {
		this.mShippingInfo = pShippingInfo;
	}

	public String getOrderNumber() {
		return mOrderNumber;
	}

	public void setOrderNumber(String pOrderNumber) {
		mOrderNumber = pOrderNumber;
	}

	public double getOrderTotal() {
		return mOrderTotal;
	}

	public void setOrderTotal(double pOrderTotal) {
		mOrderTotal = pOrderTotal;
	}

	public double getTax() {
		return mTax;
	}

	public void setTax(double pTax) {
		mTax = pTax;
	}

	public double getShipping() {
		return mShipping;
	}

	public void setShipping(double pShipping) {
		mShipping = pShipping;
	}

	public String getShippingMethod() {
		return mShippingMethod;
	}

	public void setShippingMethod(String pShippingMethod) {
		this.mShippingMethod = pShippingMethod;
	}

	public String getBopisPerson() {
		return mBopisPerson;
	}

	public void setBopisPerson(String pBopisPerson) {
		mBopisPerson = pBopisPerson;
	}

	public String getBopisStore() {
		return mBopisStore;
	}

	public void setBopisStore(String pBopisStore) {
		mBopisStore = pBopisStore;
	}

	public String getBopisEmail() {
		return mBopisEmail;
	}

	public void setBopisEmail(String pBopisEmail) {
		mBopisEmail = pBopisEmail;
	}
	
	public String getContactEmail() {
    return mContactEmail;
  }

  public void setContactEmail(String pContactEmail) {
    mContactEmail = pContactEmail;
  }

  public double getRawSubtotal() {
		return mRawSubtotal;
	}

	public void setRawSubtotal(double pRawSubtotal) {
		mRawSubtotal = pRawSubtotal;
	}

	public String getState() {
		return mState;
	}

	public void setState(String pState) {
		mState = pState;
	}

	public String getId() {
		return mId;
	}

	public void setId(String pId) {
		mId = pId;
	}

	public boolean isIsSaturdayDelivery() {
		return mIsSaturdayDelivery;
	}

	public void setIsSaturdayDelivery(boolean pIsSaturdayDelivery) {
		mIsSaturdayDelivery = pIsSaturdayDelivery;
	}

	public double getAmount() {
		return mAmount;
	}

	public void setAmount(double pAmount) {
		mAmount = pAmount;
	}

	public ArrayList<MFFOrderItemInfo> getCommerceItems() {
		return mCommerceItems;
	}

	public void setCommerceItems(ArrayList<MFFOrderItemInfo> pCommerceItems) {
		mCommerceItems = pCommerceItems;
	}

	public boolean isBopisOrder() {
		return mBopisOrder;
	}

	public void setBopisOrder(boolean pBopisOrder) {
		mBopisOrder = pBopisOrder;
	}

	public String getTrackingNumber() {
		return mTrackingNumber;
	}

	public void setTrackingNumber(String pTrackingNumber) {
		mTrackingNumber = pTrackingNumber;
	}

	public double getDiscountAmount() {
		return mDiscountAmount;
	}

	public void setDiscountAmount(double pDiscountAmount) {
		mDiscountAmount = pDiscountAmount;
	}

	public double getOrderChargeAmount() {
		return mOrderChargeAmount;
	}

	public void setOrderChargeAmount(double pOrderChargeAmount) {
		mOrderChargeAmount = pOrderChargeAmount;
	}

	public double getGiftCardPaymentTotal() {
		return mGiftCardPaymentTotal;
	}

	public void setGiftCardPaymentTotal(double pGiftCardPaymentTotal) {
		mGiftCardPaymentTotal = pGiftCardPaymentTotal;
	}

	public boolean isLegacyOrder() {
		return mLegacyOrder;
	}

	public void setLegacyOrder(boolean pLegacyOrder) {
		mLegacyOrder = pLegacyOrder;
	}

	public ArrayList<MFFOrderBillingInfo> getPaymentGroups() {
		return mPaymentGroups;
	}

	public void setPaymentGroups(ArrayList<MFFOrderBillingInfo> pPaymentGroups) {
		mPaymentGroups = pPaymentGroups;
	}

	public void setDiscounted(boolean pDiscounted) {
		this.mDiscounted = pDiscounted;
	}

	public boolean isDiscounted() {
		return this.mDiscounted;
	}

	public double getOrderSubTotalLessGiftCard() {
		return orderSubTotalLessGiftCard;
	}

	public void setOrderSubTotalLessGiftCard(double pOrderSubTotalLessGiftCard) {
		orderSubTotalLessGiftCard = pOrderSubTotalLessGiftCard;
	}

	public double getMerchandiseTotal() {
		return mMerchandiseTotal;
	}

	public void setMerchandiseTotal(double pMerchandiseTotal) {
		mMerchandiseTotal = pMerchandiseTotal;
	}

	public String getAttention() {
		return mAttention;
	}

	public void setAttention(String pAttention) {
		mAttention = pAttention;
	}

}
