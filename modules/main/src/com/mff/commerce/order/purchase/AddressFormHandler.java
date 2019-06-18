package com.mff.commerce.order.purchase;

public interface AddressFormHandler {
	// constants for selecting AVS vs manual input
	public static final String SELECTED_ADDRESS_ENTERED = "entered";
	public static final String SELECTED_ADDRESS_SUGGESTED = "suggested";
	
	String getFirstName();
	String getLastName();
	String getAddress1();
	String getAddress2();
	String getCity();
	String getState();
	String getPostalCode();
	String getCountry();
	String getPhoneNumber();
	boolean getFormError();
	
	void setFirstName(String pFirstName);
	void setLastName(String pLastName);
	void setAddress1(String pAddress1);
	void setAddress2(String pAddress2);
	void setCity(String pCity);
	void setState(String pState);
	void setPostalCode(String pPostalCode);
	void setCountry(String pCountry);
	void setPhoneNumber(String pPhoneNumber);
}
