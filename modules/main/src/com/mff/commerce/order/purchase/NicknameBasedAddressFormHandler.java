package com.mff.commerce.order.purchase;

public interface NicknameBasedAddressFormHandler extends AddressFormHandler {
	String getAddressNickname();
	void setAddressNickname(String pNickname);
}
