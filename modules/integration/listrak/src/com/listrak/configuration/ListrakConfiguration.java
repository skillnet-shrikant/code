package com.listrak.configuration;

import atg.nucleus.GenericService;

public class ListrakConfiguration extends GenericService {

	private String mClientId;
	private String mClientSecret;
	private int mDaysAfterPurchaseToHoldBopis;
	
	public int getDaysAfterPurchaseToHoldBopis() {
		return mDaysAfterPurchaseToHoldBopis;
	}
	public void setDaysAfterPurchaseToHoldBopis(int pDaysAfterPurchaseToHoldBopis) {
		mDaysAfterPurchaseToHoldBopis = pDaysAfterPurchaseToHoldBopis;
	}
	public String getClientId() {
		return mClientId;
	}
	public void setClientId(String pClientId) {
		this.mClientId = pClientId;
	}
	public String getClientSecret() {
		return mClientSecret;
	}
	public void setClientSecret(String pClientSecret) {
		this.mClientSecret = pClientSecret;
	}
}
