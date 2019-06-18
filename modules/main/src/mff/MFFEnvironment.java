package mff;

import java.util.Map;

import atg.nucleus.GenericService;

/**
 * This class stores globalKPconfiguration. Special properties specific to this
 * project should go in here. Note: most of the ATG-required global
 * configuration still remains in the /atg/dynamo/Configuration component.
 */
public class MFFEnvironment extends GenericService {

	private String mSiteHttpServerName;
	private int mSessionTimeoutRefresh;
	private String mProductImageRoot;
	private String mSwatchImageRoot;
	private String mMarketingImageRoot;
	private boolean mBvEnabled;
	private boolean mBvOrderTransactionsEnabled;
	private boolean mGtmEnabled;
	private boolean mListrakEnabled;
	private boolean mIovationEnabled;
	private boolean mSkipAvailabilityCheck;
	private int mStoreInventoryThreshold;
	private int mSiteInventoryThreshold;
	private String mStoreInvItemDelim;
	private double mStorePickUpRadius;
	private int mMaxGiftCardsAllowedPerOrder;
	private boolean mRestrictGCPaymentGCOnlyOrder;
	private String mDefaultSiteId;
	private long mMaxQtyPerItemInOrder;
	private long mMaxItemCountPerOrder;
	private long mMaxTotalQtyPerOrder;
	private boolean mEnableMaxItemCountPerOrder;
	private boolean mEnableMaxTotalQtyPerOrder;
	private String mFedExTrackingUrl;
	private String mGiftCardSkuId;
	private String mLegacyPrefix;
	private String jobAlertsEmailFromAddress;
	private String jobAlertsEmailToAddress;
	private String jobAlertsEmailSubjectSuffix;
	private boolean sendEmailAlerts;
	private String mDefaultReturnStoreID;
	private boolean mDynamicRemarketingTagEnabled;
	private String gardenCenterURL;
	private String gardenCenterText;
	private boolean mEscapeSearchSpecialCharacters;
	private String mReportToEmailAddress;
	private String mReportFromEmailAddress;
	private String mReportEmailSubjectSuffix;
	private boolean enableSetOrderByCommerceIdDebug = true;
	private boolean enable404RedirectsOnPDP = false;
	

	
	public boolean isEnable404RedirectsOnPDP() {
		return enable404RedirectsOnPDP;
	}
	public void setEnable404RedirectsOnPDP(boolean pEnable404RedirectsOnPDP) {
		enable404RedirectsOnPDP = pEnable404RedirectsOnPDP;
	}
	public String getGardenCenterText() {
		return gardenCenterText;
	}
	public void setGardenCenterText(String pGardenCenterText) {
		gardenCenterText = pGardenCenterText;
	}
	public String getGardenCenterURL() {
		return gardenCenterURL;
	}
	public void setGardenCenterURL(String pGardenCenterURL) {
		gardenCenterURL = pGardenCenterURL;
	}
	/* siteHttpServerName */
	public String getSiteHttpServerName() {
		return mSiteHttpServerName;
	}
	public void setSiteHttpServerName(String pSiteHttpServerName) {
		mSiteHttpServerName = pSiteHttpServerName;
	}



	/* sessionTimeoutRefresh */
	public int getSessionTimeoutRefresh() {
		return mSessionTimeoutRefresh;
	}
	public void setSessionTimeoutRefresh(int pSessionTimeoutRefresh) {
		mSessionTimeoutRefresh = pSessionTimeoutRefresh;
	}



	/* productImageRoot */
	public String getProductImageRoot() {
		return mProductImageRoot;
	}
	public void setProductImageRoot(String pProductImageRoot) {
		mProductImageRoot = pProductImageRoot;
	}

	/* swatchImageRoot */
	public String getSwatchImageRoot() {
		return mSwatchImageRoot;
	}
	public void setSwatchImageRoot(String pSwatchImageRoot) {
		mSwatchImageRoot = pSwatchImageRoot;
	}

	/* marketingImageRoot */
	public String getMarketingImageRoot() {
		return mMarketingImageRoot;
	}
	public void setMarketingImageRoot(String pMarketingImageRoot) {
		mMarketingImageRoot = pMarketingImageRoot;
	}

	/* bvEnabled */
	public boolean isBvEnabled() {
		return mBvEnabled;
	}
	public void setBvEnabled(boolean pBvEnabled) {
		mBvEnabled = pBvEnabled;
	}
	
	/* bvOrderTransactionsEnabled */
  public boolean isBvOrderTransactionsEnabled() {
    return mBvOrderTransactionsEnabled;
  }
  public void setBvOrderTransactionsEnabled(boolean pBvOrderTransactionsEnabled) {
    mBvOrderTransactionsEnabled = pBvOrderTransactionsEnabled;
  }

	/* gtmEnabled */
	public boolean isGtmEnabled() {
		return mGtmEnabled;
	}
	public void setGtmEnabled(boolean pGtmEnabled) {
		mGtmEnabled = pGtmEnabled;
	}

	/* listrakEnabled */
	public boolean isListrakEnabled() {
		return mListrakEnabled;
	}
	public void setListrakEnabled(boolean pListrakEnabled) {
		mListrakEnabled = pListrakEnabled;
	}

	/* iovationEnabled */
	public boolean isIovationEnabled() {
		return mIovationEnabled;
	}
	public void setIovationEnabled(boolean pIovationEnabled) {
		mIovationEnabled = pIovationEnabled;
	}

	/* skipAvailabilityCheck */
	public boolean isSkipAvailabilityCheck() {
		return mSkipAvailabilityCheck;
	}
	public void setSkipAvailabilityCheck(boolean pSkipAvailabilityCheck) {
		mSkipAvailabilityCheck = pSkipAvailabilityCheck;
	}

	/* maxQtyPerItemInOrder */
	public long getMaxQtyPerItemInOrder() {
		return mMaxQtyPerItemInOrder;
	}
	public void setMaxQtyPerItemInOrder(long pMaxQtyPerItemInOrder) {
		this.mMaxQtyPerItemInOrder = pMaxQtyPerItemInOrder;
	}

	/* storeInventoryThreshold */
	public int getStoreInventoryThreshold() {
		return mStoreInventoryThreshold;
	}
	public void setStoreInventoryThreshold(int pStoreInventoryThreshold) {
		mStoreInventoryThreshold = pStoreInventoryThreshold;
	}

	/* siteInventoryThreshold */
  public int getSiteInventoryThreshold() {
    return mSiteInventoryThreshold;
  }
  public void setSiteInventoryThreshold(int pSiteInventoryThreshold) {
    mSiteInventoryThreshold = pSiteInventoryThreshold;
  }

	/* storeInvItemDelim */
	public String getStoreInvItemDelim() {
		return mStoreInvItemDelim;
	}
	public void setStoreInvItemDelim(String pStoreInvItemDelim) {
		mStoreInvItemDelim = pStoreInvItemDelim;
	}
  public double getStorePickUpRadius() {
    return mStorePickUpRadius;
  }
  public void setStorePickUpRadius(double pStorePickUpRadius) {
    mStorePickUpRadius = pStorePickUpRadius;
  }
  public int getMaxGiftCardsAllowedPerOrder() {
    return mMaxGiftCardsAllowedPerOrder;
  }
  public void setMaxGiftCardsAllowedPerOrder(int pMaxGiftCardsAllowedPerOrder) {
    mMaxGiftCardsAllowedPerOrder = pMaxGiftCardsAllowedPerOrder;
  }
  
  public String getDefaultSiteId() {
    return mDefaultSiteId;
  }

  public void setDefaultSiteId(String pDefaultSiteId) {
    this.mDefaultSiteId = pDefaultSiteId;
  }
  public long getMaxItemCountPerOrder() {
    return mMaxItemCountPerOrder;
  }
  public void setMaxItemCountPerOrder(long pMaxItemCountPerOrder) {
    mMaxItemCountPerOrder = pMaxItemCountPerOrder;
  }
  public long getMaxTotalQtyPerOrder() {
    return mMaxTotalQtyPerOrder;
  }
  public void setMaxTotalQtyPerOrder(long pMaxTotalQtyPerOrder) {
    mMaxTotalQtyPerOrder = pMaxTotalQtyPerOrder;
  }
  public boolean isEnableMaxItemCountPerOrder() {
    return mEnableMaxItemCountPerOrder;
  }
  public void setEnableMaxItemCountPerOrder(boolean pEnableMaxItemCountPerOrder) {
    mEnableMaxItemCountPerOrder = pEnableMaxItemCountPerOrder;
  }
  public boolean isEnableMaxTotalQtyPerOrder() {
    return mEnableMaxTotalQtyPerOrder;
  }
  public void setEnableMaxTotalQtyPerOrder(boolean pEnableMaxTotalQtyPerOrder) {
    mEnableMaxTotalQtyPerOrder = pEnableMaxTotalQtyPerOrder;
  }
  public boolean isRestrictGCPaymentGCOnlyOrder() {
    return mRestrictGCPaymentGCOnlyOrder;
  }
  public void setRestrictGCPaymentGCOnlyOrder(boolean pRestrictGCPaymentGCOnlyOrder) {
    mRestrictGCPaymentGCOnlyOrder = pRestrictGCPaymentGCOnlyOrder;
  }

	public String getFedExTrackingUrl() {
		return mFedExTrackingUrl;
	}

	public void setFedExTrackingUrl(String pFedExTrackingUrl) {
		mFedExTrackingUrl = pFedExTrackingUrl;
	}
  public String getGiftCardSkuId() {
    return mGiftCardSkuId;
  }
  public void setGiftCardSkuId(String pGiftCardSkuId) {
    mGiftCardSkuId = pGiftCardSkuId;
  }
  
  public String getLegacyPrefix() {
		return mLegacyPrefix;
	}
	public void setLegacyPrefix(String pLegacyPrefix) {
		mLegacyPrefix = pLegacyPrefix;
	}
  public String getJobAlertsEmailFromAddress() {
    return jobAlertsEmailFromAddress;
  }
  public void setJobAlertsEmailFromAddress(String pJobAlertsEmailFromAddress) {
    jobAlertsEmailFromAddress = pJobAlertsEmailFromAddress;
  }
  public String getJobAlertsEmailToAddress() {
    return jobAlertsEmailToAddress;
  }
  public void setJobAlertsEmailToAddress(String pJobAlertsEmailToAddress) {
    jobAlertsEmailToAddress = pJobAlertsEmailToAddress;
  }
  public String getJobAlertsEmailSubjectSuffix() {
    return jobAlertsEmailSubjectSuffix;
  }
  public void setJobAlertsEmailSubjectSuffix(String pJobAlertsEmailSubjectSuffix) {
    jobAlertsEmailSubjectSuffix = pJobAlertsEmailSubjectSuffix;
  }
  public boolean isSendEmailAlerts() {
    return sendEmailAlerts;
  }
  public void setSendEmailAlerts(boolean pSendEmailAlerts) {
    sendEmailAlerts = pSendEmailAlerts;
  }
  
  public String getDefaultReturnStoreID() {
    return mDefaultReturnStoreID;
  }

  public void setDefaultReturnStoreID(String pDefaultReturnStoreID) {
    mDefaultReturnStoreID = pDefaultReturnStoreID;
  }
  
  public boolean isDynamicRemarketingTagEnabled() {
  	return mDynamicRemarketingTagEnabled;
  }
  
  public void setDynamicRemarketingTagEnabled(boolean pDynamicRemarketingTagEnabled) {
  	mDynamicRemarketingTagEnabled = pDynamicRemarketingTagEnabled;
  }
  
  public boolean isEscapeSearchSpecialCharacters() {
    return mEscapeSearchSpecialCharacters;
  }
  
  public void setEscapeSearchSpecialCharacters(boolean pEscapeSearchSpecialCharacters) {
    mEscapeSearchSpecialCharacters = pEscapeSearchSpecialCharacters;
  }
  public String getReportToEmailAddress() {
    return mReportToEmailAddress;
  }
  public void setReportToEmailAddress(String pReportToEmailAddress) {
    mReportToEmailAddress = pReportToEmailAddress;
  }
  public String getReportFromEmailAddress() {
    return mReportFromEmailAddress;
  }
  public void setReportFromEmailAddress(String pReportFromEmailAddress) {
    mReportFromEmailAddress = pReportFromEmailAddress;
  }
  public String getReportEmailSubjectSuffix() {
    return mReportEmailSubjectSuffix;
  }
  public void setReportEmailSubjectSuffix(String pReportEmailSubjectSuffix) {
    mReportEmailSubjectSuffix = pReportEmailSubjectSuffix;
  }
  
  public boolean isEnableSetOrderByCommerceIdDebug() {
    return enableSetOrderByCommerceIdDebug;
  }
  public void setEnableSetOrderByCommerceIdDebug(boolean pEnableSetOrderByCommerceIdDebug) {
    enableSetOrderByCommerceIdDebug = pEnableSetOrderByCommerceIdDebug;
  }
  
  

}
