package com.fedex.service;

import atg.nucleus.GenericService;

public class FedexService extends GenericService {
	
	private String mApiVersionServiceId;
	private int mApiVersionMajor;
	private int mApiVersionIntermediate;
	private int mApiVersionMinor;
	
	public String getApiVersionServiceId() {
		return mApiVersionServiceId;
	}
	public void setApiVersionServiceId(String pApiVersionServiceId) {
		this.mApiVersionServiceId = pApiVersionServiceId;
	}
	public int getApiVersionMajor() {
		return mApiVersionMajor;
	}
	public void setApiVersionMajor(int pApiVersionMajor) {
		this.mApiVersionMajor = pApiVersionMajor;
	}
	public int getApiVersionIntermediate() {
		return mApiVersionIntermediate;
	}
	public void setApiVersionIntermediate(int pApiVersionIntermediate) {
		this.mApiVersionIntermediate = pApiVersionIntermediate;
	}
	public int getApiVersionMinor() {
		return mApiVersionMinor;
	}
	public void setApiVersionMinor(int pApiVersionMinor) {
		this.mApiVersionMinor = pApiVersionMinor;
	}

}
