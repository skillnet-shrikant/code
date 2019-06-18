package com.mff.account.order.bean;

public class MFFOrderBillingInfo {

	private String mFirstName;

	private String mLastName;

	private String mNameOnCard;

	private String mPaymentMethod;

	private String mCardNumber;

	private String mGiftCertificateNumber;

	private String mCreditCardType;

	private String mCreditCardNumber;

	private String mExpirationMonth;

	private String mExpirationYear;

	private double mAmount;

	private String mPaymentGroupClassType;

	public String getPaymentMethod() {
		return mPaymentMethod;
	}

	public void setPaymentMethod(String pPaymentMethod) {
		mPaymentMethod = pPaymentMethod;
	}

	public String getExpirationMonth() {
		return mExpirationMonth;
	}

	public void setExpirationMonth(String pExpirationMonth) {
		mExpirationMonth = pExpirationMonth;
	}

	public String getExpirationYear() {
		return mExpirationYear;
	}

	public void setExpirationYear(String pExpirationYear) {
		mExpirationYear = pExpirationYear;
	}

	public String getCreditCardType() {
		return mCreditCardType;
	}

	public void setCreditCardType(String pCreditCardType) {
		mCreditCardType = pCreditCardType;
	}

	public String getCreditCardNumber() {
		return mCreditCardNumber;
	}

	public void setCreditCardNumber(String pCreditCardNumber) {
		mCreditCardNumber = pCreditCardNumber;
	}

	public double getAmount() {
		return mAmount;
	}

	public void setAmount(double pAmount) {
		mAmount = pAmount;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String pFirstName) {
		mFirstName = pFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String pLastName) {
		mLastName = pLastName;
	}

	public String getNameOnCard() {
		return mNameOnCard;
	}

	public void setNameOnCard(String pNameOnCard) {
		mNameOnCard = pNameOnCard;
	}

	public String getPaymentGroupClassType() {
		return mPaymentGroupClassType;
	}

	public void setPaymentGroupClassType(String pPaymentGroupClassType) {
		mPaymentGroupClassType = pPaymentGroupClassType;
	}

	public String getCardNumber() {
		return mCardNumber;
	}

	public void setCardNumber(String pCardNumber) {
		mCardNumber = pCardNumber;
	}

	public String getGiftCertificateNumber() {
		return mGiftCertificateNumber;
	}

	public void setGiftCertificateNumber(String pGiftCertificateNumber) {
		mGiftCertificateNumber = pGiftCertificateNumber;
	}

}
