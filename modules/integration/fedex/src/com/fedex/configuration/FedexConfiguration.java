package com.fedex.configuration;

import atg.nucleus.GenericService;

public class FedexConfiguration extends GenericService {

	private String mApiAccessKey;
	private String mApiAccessPassword;
	private String mApiAccessAccountNumber;
	private String mApiAccessMeterNumber;
	

	
	
	public String getApiAccessKey() {
		return mApiAccessKey;
	}
	public void setApiAccessKey(String pApiAccessKey) {
		this.mApiAccessKey = pApiAccessKey;
	}
	public String getApiAccessPassword() {
		return mApiAccessPassword;
	}
	public void setApiAccessPassword(String pApiAccessPassword) {
		this.mApiAccessPassword = pApiAccessPassword;
	}
	public String getApiAccessAccountNumber() {
		return mApiAccessAccountNumber;
	}
	public void setApiAccessAccountNumber(String pApiAccessAccountNumber) {
		this.mApiAccessAccountNumber = pApiAccessAccountNumber;
	}
	public String getApiAccessMeterNumber() {
		return mApiAccessMeterNumber;
	}
	public void setApiAccessMeterNumber(String pApiAccessMeterNumber) {
		this.mApiAccessMeterNumber = pApiAccessMeterNumber;
	}
	
	
	
}
