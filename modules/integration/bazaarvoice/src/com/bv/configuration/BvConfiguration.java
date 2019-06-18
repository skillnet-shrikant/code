package com.bv.configuration;

import atg.nucleus.GenericService;


public class BvConfiguration extends GenericService {
	
	private String mClientName;
	private String mDisplayEnvironmentName;
	private String mDisplayjsName;
	private String mBvLoaderjsName;
	private boolean mStaging;
	private String mDisplaySiteId;
	private String mDisplayUrl;
	private String mBvLoaderUrl;
	private String mDisplayStagingUrl;
	private String mBvLoaderStagingUrl;
	private String mLocale;
	private String mDefaultCurrency;
	private String mBvLoaderStagingEnvironmentName;
	private String mBvLoaderProductionEnvironmentName;
	private String mBvLoaderSiteId;
	private String mCloudKey;
	private String mBvRootFolder;
	
	public String getBvRootFolder() {
		return mBvRootFolder;
	}

	public void setBvRootFolder(String pBvRootFolder) {
		mBvRootFolder = pBvRootFolder;
	}

	public String getCloudKey(){
		return mCloudKey;
	}
	
	public void setCloudKey(String pCloudKey){
		mCloudKey=pCloudKey;
	}
	
	public String getBvLoaderSiteId() {
		return mBvLoaderSiteId;
	}

	public void setBvLoaderSiteId(String pBvLoaderSiteId) {
		mBvLoaderSiteId = pBvLoaderSiteId;
	}

	public String getBvLoaderProductionEnvironmentName() {
		return mBvLoaderProductionEnvironmentName;
	}

	public void setBvLoaderProductionEnvironmentName(String pBvLoaderProductionEnvironmentName) {
		mBvLoaderProductionEnvironmentName = pBvLoaderProductionEnvironmentName;
	}

	public String getBvLoaderStagingEnvironmentName() {
		return mBvLoaderStagingEnvironmentName;
	}

	public void setBvLoaderStagingEnvironmentName(String pBvLoaderStagingEnvironmentName) {
		mBvLoaderStagingEnvironmentName = pBvLoaderStagingEnvironmentName;
	}

	public String getBvLoaderjsName() {
		return mBvLoaderjsName;
	}

	public void setBvLoaderjsName(String pBvLoaderjsName) {
		mBvLoaderjsName = pBvLoaderjsName;
	}

	public String getDefaultCurrency() {
		return mDefaultCurrency;
	}

	public void setDefaultCurrency(String pDefaultCurrency) {
		mDefaultCurrency = pDefaultCurrency;
	}

	public String getLocale() {
		return mLocale;
	}

	public void setLocale(String pLocale) {
		mLocale = pLocale;
	}

	public String getDisplayStagingUrl() {
		return mDisplayStagingUrl;
	}

	public void setDisplayStagingUrl(String pDisplayStagingUrl) {
		mDisplayStagingUrl = pDisplayStagingUrl;
	}

	public String getBvLoaderStagingUrl() {
		return mBvLoaderStagingUrl;
	}

	public void setBvLoaderStagingUrl(String pBvLoaderStagingUrl) {
		mBvLoaderStagingUrl = pBvLoaderStagingUrl;
	}

	public String getDisplayUrl() {
		return mDisplayUrl;
	}

	public void setDisplayUrl(String pDisplayUrl) {
		mDisplayUrl = pDisplayUrl;
	}

	public String getBvLoaderUrl() {
		return mBvLoaderUrl;
	}

	public void setBvLoaderUrl(String pBvLoaderUrl) {
		mBvLoaderUrl = pBvLoaderUrl;
	}

	public String getDisplaySiteId(){
		return mDisplaySiteId;
	}
	
	public void setDisplaySiteId(String pDisplaySiteId){
		mDisplaySiteId=pDisplaySiteId;
	}
	
	public boolean isStaging(){
		return mStaging;
	}
	
	public void setStaging(boolean pStaging){
		mStaging=pStaging;
	}
	
	public String getClientName(){
		return mClientName;
	}
	
	public void setClientName(String pClientName){
		mClientName=pClientName;
	}
	
	public String getDisplayEnvironmentName(){
		return mDisplayEnvironmentName;
	}
	
	public void setDisplayEnvironmentName(String pDisplayEnvironmentName){
		mDisplayEnvironmentName=pDisplayEnvironmentName;
	}

	public String getDisplayjsName(){
		return mDisplayjsName;
	}
	
	public void setDisplayjsName(String pDisplayjsName){
		mDisplayjsName=pDisplayjsName;
	}
}
