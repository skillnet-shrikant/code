package com.mff.commerce.profile;

import atg.commerce.profile.CommercePropertyManager;

public class MFFPropertyManager extends CommercePropertyManager {
	
	/**
	   * Back in stock notify item descriptor name.
	   */
	  String mBackInStockNotifyItemDescriptorName = "backInStockNotifyItem";

	  /**
	   * Bisn sku id property name.
	   */
	  String mBisnSkuIdPropertyName = "catalogRefId";

	  /**
	   * Bisn product id property name.
	   */
	  String mBisnProductIdPropertyName = "productId";

	  /**
	   * Bisn e-mail property name.
	   */
	  String mBisnEmailPropertyName = "emailAddress";

	  /**
	   * Date of birth property name.
	   */
	  String mDateOfBirthPropertyName = "dateOfBirth";

	  /**
	   * E-mail recipient item descriptor name.
	   */
	  protected String mEmailRecipientItemDescriptorName;

	  /**
	   * E-mail recipient property name.
	   */
	  protected String mEmailRecipientPropertyName;

	  /**
	   * Source code property name.
	   */
	  protected String mSourceCodePropertyName;
	  
	  /**
	   * Source code property name.
	   */
	  protected String mUserIdPropertyName;

	  /**
	   * Date of the last purchase.
	   */
	  protected String mLastPurchaseDate = "lastPurchaseDate";

	  /**
	   * Bought items constant.
	   */
	  protected String mItemsBought = "itemsBought";

	  /**
	   * Number of orders constant. 
	   */
	  protected String mNumberOfOrders = "numberOfOrders";

	  
	  /** 'receivePromoEmail' property name */
	  private String mReceivePromoEmailPropertyName = "receivePromoEmail";

	  private String mGiftlistsPropertyName = "giftlists";
	  

	  /** Employee related props **/
	  protected String mSomCardPropertyName = "somCard";
	  protected String mEmployeeIdPropertyName = "employeeId";
	  private String mValidatedPropertyName = "validated";
	  private String mEmployeePropertyName = "employee";

	  
	  public String getEmployeeIdPropertyName() {
		return mEmployeeIdPropertyName;
	}

	public void setEmployeeIdPropertyName(String pEmployeeIdPropertyName) {
		mEmployeeIdPropertyName = pEmployeeIdPropertyName;
	}

	public String getValidatedPropertyName() {
		return mValidatedPropertyName;
	}

	public void setValidatedPropertyName(String pValidatedPropertyName) {
		mValidatedPropertyName = pValidatedPropertyName;
	}

	public String getEmployeePropertyName() {
		return mEmployeePropertyName;
	}

	public void setEmployeePropertyName(String pEmployeePropertyName) {
		mEmployeePropertyName = pEmployeePropertyName;
	}

	/**
	   * @return the mSomCardPropertyName
	   */
	  public String getSomCardPropertyName() {
	    return mSomCardPropertyName;
	  }

	  /**
	   * @param pSomCardPropertyName the somCardPropertyName to set
	   */
	  public void setSomCardPropertyName(String pSomCardPropertyName) {
		  mSomCardPropertyName = pSomCardPropertyName;
	  }

	  
	  /**
	   * @return the mGiftlistsPropertyName
	   */
	  public String getGiftlistsPropertyName() {
	    return mGiftlistsPropertyName;
	  }

	  /**
	   * @param pGiftlistsPropertyName the giftlistsPropertyName to set
	   */
	  public void setGiftlistsPropertyName(String pGiftlistsPropertyName) {
	    mGiftlistsPropertyName = pGiftlistsPropertyName;
	  }
	  
	  /**
	   * @return the mReceivePromoEmailPropertyName
	   */
	  public String getReceivePromoEmailPropertyName() {
	    return mReceivePromoEmailPropertyName;
	  }

	  /**
	   *  @param pReceivePromoEmailPropertyName the receivePromoEmailPropertyName to set
	   */
	  public void setReceivePromoEmailPropertyName(
	      String pReceivePromoEmailPropertyName) {
	    mReceivePromoEmailPropertyName = pReceivePromoEmailPropertyName;
	  }

	  private String mWishlistPropertyName = "wishlist";
	  
	  /**
	   * @return the mWishlistPropertyName
	   */
	  public String getWishlistPropertyName()
	  {
	    return mWishlistPropertyName;
	  }
	  
	  /**
	   * @param pWishlistPropertyName the wishlistPropertyName to set
	   */
	  public void setWishlistPropertyName(String pWishlistPropertyName)
	  {
	    mWishlistPropertyName = pWishlistPropertyName;
	  }

	  /**
	  * @param pDateOfBirthPropertyName - date of birth property name.
	  */
	  public void setDateOfBirthPropertyName(String pDateOfBirthPropertyName) {
	    mDateOfBirthPropertyName = pDateOfBirthPropertyName;
	  }

	  /**
	   * @return date of birth property name.
	   */
	  public String getDateOfBirthPropertyName() {
	    return mDateOfBirthPropertyName;
	  }

	  /**
	   * @param pEmailRecipientItemDescriptorName - e-mail recipient item
	   * descriptor name.
	   */
	  public void setEmailRecipientItemDescriptorName(String pEmailRecipientItemDescriptorName) {
	    mEmailRecipientItemDescriptorName = pEmailRecipientItemDescriptorName;
	  }

	  /**
	   * @return mEmailRecipientItemDescriptorName - e-mail recipient
	   * item descriptor name.
	   */
	  public String getEmailRecipientItemDescriptorName() {
	    return mEmailRecipientItemDescriptorName;
	  }

	  /**
	   * @param pEmailRecipientPropertyName - e-mail recipient
	   * property name.
	   */
	  public void setEmailRecipientPropertyName(String pEmailRecipientPropertyName) {
	    mEmailRecipientPropertyName = pEmailRecipientPropertyName;
	  }

	  /**
	   * @return mEmailRecipientPropertyName - e-mail recipient property name.
	   */
	  public String getEmailRecipientPropertyName() {
	    return mEmailRecipientPropertyName;
	  }

	  /**
	   * @return source code property name.
	   */
	  public String getSourceCodePropertyName() {
	    return mSourceCodePropertyName;
	  }

	  /**
	   * @param pSourceCodePropertyName - source code
	   * property name.
	   */
	  public void setSourceCodePropertyName(String pSourceCodePropertyName) {
	    mSourceCodePropertyName = pSourceCodePropertyName;
	  }
	  
	  /**
	   * @return user id property name.
	   */
	  public String getUserIdPropertyName() {
	    return mUserIdPropertyName;
	  }

	  /**
	   * @param pUserIdPropertyName - user id
	   * property name.
	   */
	  public void setUserIdPropertyName(String pUserIdPropertyName) {
	    mUserIdPropertyName = pUserIdPropertyName;
	  }

	  /**
	   * @return backInStockNotifyItem - back in stock
	   * notify item.
	   */
	  public String getBackInStockNotifyItemDescriptorName() {
	    return mBackInStockNotifyItemDescriptorName;
	  }

	  /**
	   * @param pBackInStockNotifyItemDescriptorName -
	   * back in stock notify item descriptor name.
	   */
	  public void setBackInStockNotifyItemDescriptorName(String pBackInStockNotifyItemDescriptorName) {
	    mBackInStockNotifyItemDescriptorName = pBackInStockNotifyItemDescriptorName;
	  }

	  /**
	   * @return bisn e-mail property name.
	   */
	  public String getBisnEmailPropertyName() {
	    return mBisnEmailPropertyName;
	  }

	  /**
	   * @param pBisnEmailPropertyName - bisn e-mail
	   * property name.
	   */
	  public void setBisnEmailPropertyName(String pBisnEmailPropertyName) {
	    mBisnEmailPropertyName = pBisnEmailPropertyName;
	  }

	  /**
	   * @return bisn sku id property name.
	   */
	  public String getBisnSkuIdPropertyName() {
	    return mBisnSkuIdPropertyName;
	  }

	  /**
	   * @param pBisnSkuIdPropertyName - bisn
	   * sku id property name.
	   */
	  public void setBisnSkuIdPropertyName(String pBisnSkuIdPropertyName) {
	    mBisnSkuIdPropertyName = pBisnSkuIdPropertyName;
	  }

	  /**
	   * @return bisn product id property name.
	   */
	  public String getBisnProductIdPropertyName() {
	    return mBisnProductIdPropertyName;
	  }

	  /**
	   * @param pBisnProductIdPropertyName - bisn
	   * property product id property name.
	   */
	  public void setBisnProductIdPropertyName(String pBisnProductIdPropertyName) {
	    mBisnProductIdPropertyName = pBisnProductIdPropertyName;
	  }

	  /**
	   * @return items bought.
	   */
	  public String getItemsBought() {
	    return mItemsBought;
	  }

	  /**
	   * @param pItemsBought - items bought.
	   */
	  public void setItemsBought(String pItemsBought) {
	    mItemsBought = pItemsBought;
	  }

	  /**
	   * @return last purchase date.
	   */
	  public String getLastPurchaseDate() {
	    return mLastPurchaseDate;
	  }

	  /**
	   * @param pLastPurchaseDate - the date of last purchase.
	   */
	  public void setLastPurchaseDate(String pLastPurchaseDate) {
	    mLastPurchaseDate = pLastPurchaseDate;
	  }

	  /**
	   * @return number of orders.
	   */
	  public String getNumberOfOrders() {
	    return mNumberOfOrders;
	  }

	  /**
	   * @param pNumberOfOrders - number of orders.
	   */
	  public void setNumberOfOrders(String pNumberOfOrders) {
	    mNumberOfOrders = pNumberOfOrders;
	  }
	  
	  protected String mNewCreditCard = "newCreditCard";

	  /**
	   * @return the mNewCreditCard
	   */
	  public String getNewCreditCard() {
	    return mNewCreditCard;
	  }

	  /**
	   * @param pNewCreditCard the newCreditCard to set
	   */
	  public void setNewCreditCard(String pNewCreditCard) {
	    mNewCreditCard = pNewCreditCard;
	  }
	  
	  protected String mGenderPropertyName = "gender";
	  
	  /**
	   * @return the mGenderPropertyName
	   */
	  public String getGenderPropertyName() {
	    return mGenderPropertyName;
	  }

	  /**
	   * @param pGenderPropertyName the genderPropertyName to set
	   */
	  public void setGenderPropertyName(String pGenderPropertyName) {
	    mGenderPropertyName = pGenderPropertyName;
	  }

	  protected String mRefferalSourcePropertyName = "referralSource";
	  
	  /**
	   * @return the mRefferalSourcePropertyName
	   */
	  public String getRefferalSourcePropertyName() {
	    return mRefferalSourcePropertyName;
	  }

	  /**
	   * @param pRefferalSourcePropertyName the refferalSourcePropertyName to set
	   */
	  public void setRefferalSourcePropertyName(String pRefferalSourcePropertyName) {
	    mRefferalSourcePropertyName = pRefferalSourcePropertyName;
	  }

	  protected String mRecentlyViewedProductsPropertyName = "recentlyViewedProducts";

	  /**
	   * @return The recentlyViewedProducts property name.
	   */
	  public String getRecentlyViewedProductsPropertyName() {
	    return mRecentlyViewedProductsPropertyName;
	  }

	  /**
	   * @param pRecentlyViewedProductsPropertyName The recentlyViewedProducts property name.
	   */
	  public void setRecentlyViewedProductsPropertyName(String pRecentlyViewedProductsPropertyName) {
	    mRecentlyViewedProductsPropertyName = pRecentlyViewedProductsPropertyName;
	  }
	  
	  protected String mRecentlyViewedProductItemDescriptorName = "recentlyViewedProduct";

	  /**
	   * @return The recentlyViewedProduct item descriptor name.
	   */
	  public String getRecentlyViewedProductItemDescriptorName() {
	    return mRecentlyViewedProductItemDescriptorName;
	  }

	  /**
	   * @param pRecentlyViewedProductItemDescriptorName The recentlyViewedProduct item descriptor name.
	   */
	  public void setRecentlyViewedProductItemDescriptorName(String pRecentlyViewedProductItemDescriptorName) {
	    mRecentlyViewedProductItemDescriptorName = pRecentlyViewedProductItemDescriptorName;
	  }
	  
	  protected String mProductPropertyName = "product";

	  /**
	   * @return The name of property name of recentlyViewedProduct 'product'.
	   */
	  public String getProductPropertyName() {
	    return mProductPropertyName;
	  }
	  /**
	   * @param pProductPropertyName The name of property name of recentlyViewedProduct 'product'.
	   */
	  public void setProductPropertyName(String pProductPropertyName) {
	    mProductPropertyName = pProductPropertyName;
	  }

	  protected String mSiteIdPropertyName = "siteId";
	  
	  /**
	   * @return The property name of recentlyViewedProduct 'siteId'.
	   */
	  public String getSiteIdPropertyName() {
	    return mSiteIdPropertyName;
	  }

	  /**
	   * @param pSiteIdPropertyName The property name of recentlyViewedProduct 'siteId'.
	   */
	  public void setSiteIdPropertyName(String pSiteIdPropertyName) {
	    mSiteIdPropertyName = pSiteIdPropertyName;
	  }

	  protected String mTimeStampPropertyName = "timestamp";
	  
	  /**
	   * @return The descriptor name of recentlyViewedProduct 'timeStamp'.
	   */
	  public String getTimeStampPropertyName() {
	    return mTimeStampPropertyName;
	  }
	  /**
	   * @param pTimeStampPropertyName The descriptor name of recentlyViewedProduct 'timeStamp'.
	   */
	  public void setTimeStampPropertyName(String pTimeStampPropertyName) {
	    mTimeStampPropertyName = pTimeStampPropertyName;
	  }
	  
	  public int getLoginStatusRegisteredHardLogin() {
      return mLoginStatusRegisteredHardLogin;
    }

    public void setLoginStatusRegisteredHardLogin(int pLoginStatusRegisteredHardLogin) {
      mLoginStatusRegisteredHardLogin = pLoginStatusRegisteredHardLogin;
    }
    
    public int getLoginStatusAnonymous() {
      return mLoginStatusAnonymous;
    }

    public void setLoginStatusAnonymous(int pLoginStatusAnonymous) {
      mLoginStatusAnonymous = pLoginStatusAnonymous;
    }

    public int getLoginStatusPersistentAnonymous() {
      return mLoginStatusPersistentAnonymous;
    }

    public void setLoginStatusPersistentAnonymous(int pLoginStatusPersistentAnonymous) {
      mLoginStatusPersistentAnonymous = pLoginStatusPersistentAnonymous;
    }

    public int getLoginStatusPersistentAnonymousSoftLogin() {
      return mLoginStatusPersistentAnonymousSoftLogin;
    }

    public void setLoginStatusPersistentAnonymousSoftLogin(int pLoginStatusPersistentAnonymousSoftLogin) {
      mLoginStatusPersistentAnonymousSoftLogin = pLoginStatusPersistentAnonymousSoftLogin;
    }

    public int getLoginStatusRegisteredSoftLogin() {
      return mLoginStatusRegisteredSoftLogin;
    }

    public void setLoginStatusRegisteredSoftLogin(int pLoginStatusRegisteredSoftLogin) {
      mLoginStatusRegisteredSoftLogin = pLoginStatusRegisteredSoftLogin;
    }

    private int mLoginStatusAnonymous;
    private int mLoginStatusPersistentAnonymous;
    private int mLoginStatusPersistentAnonymousSoftLogin;
    private int mLoginStatusRegisteredSoftLogin;
    private int mLoginStatusRegisteredHardLogin;
}
