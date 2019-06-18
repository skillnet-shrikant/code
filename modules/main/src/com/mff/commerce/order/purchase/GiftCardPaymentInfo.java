package com.mff.commerce.order.purchase;

import atg.commerce.order.PaymentGroup;

public class GiftCardPaymentInfo {
	private String mGiftCardType = "";
	private String mGiftCardNumber = "";
	private String mGiftCardPin = "";
	private double mAmountOnCard = 0.0;
	private double mAmountAssignedToThisOrder = 0.0;
	private double mAmountRemainingToSpend = 0.0;
	private boolean mFoundInSystem = false;
	private boolean mDisplayOnPage = false;
	private PaymentGroup mPaymentGroup = null;
	private String mErrorMessage;
	private boolean mTransactionSuccess=false;

	public void setGiftCardType(String pNewVal) {
		mGiftCardType = pNewVal;
	}

	public String getGiftCardType() {
		return mGiftCardType;
	}

	public void setGiftCardNumber(String pNewVal) {
		mGiftCardNumber = pNewVal;
	}

	public String getGiftCardNumber() {
		return mGiftCardNumber;
	}

	public void setGiftCardPin(String pNewVal) {
		mGiftCardPin = pNewVal;
	}

	public String getGiftCardPin() {
		return mGiftCardPin;
	}

	public void setAmountOnCard(double pNewVal) {
		mAmountOnCard = pNewVal;
	}

	public double getAmountOnCard() {
		return mAmountOnCard;
	}

	public void setAmountAssignedToThisOrder(double pNewVal) {
		mAmountAssignedToThisOrder = pNewVal;
	}

	public double getAmountAssignedToThisOrder() {
		return mAmountAssignedToThisOrder;
	}

	public void setAmountRemainingToSpend(double pNewVal) {
		mAmountRemainingToSpend = pNewVal;
	}

	public double getAmountRemainingToSpend() {
		return mAmountRemainingToSpend;
	}

	public void setFoundInSystem(boolean pNewVal) {
		mFoundInSystem = pNewVal;
	}

	public boolean getFoundInSystem() {
		return mFoundInSystem;
	}

	public void setDisplayOnPage(boolean pNewVal) {
		mDisplayOnPage = pNewVal;
	}

	public boolean getDisplayOnPage() {
		return mDisplayOnPage;
	}

	public void setPaymentGroup(PaymentGroup pNewVal) {
		mPaymentGroup = pNewVal;
	}

	public PaymentGroup getPaymentGroup() {
		return mPaymentGroup;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public void setErrorMessage(String pErrorMessage) {
		mErrorMessage = pErrorMessage;
	}

  public boolean isTransactionSuccess() {
    return mTransactionSuccess;
  }

  public void setTransactionSuccess(boolean pTransactionSuccess) {
    mTransactionSuccess = pTransactionSuccess;
  }
}
