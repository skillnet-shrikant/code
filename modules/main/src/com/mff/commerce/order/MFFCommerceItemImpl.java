package com.mff.commerce.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mff.commerce.pricing.MFFTaxPriceInfo;
import com.mff.constants.MFFConstants;

import atg.commerce.order.CommerceItemImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

/**
 * KP Extensions to Commerce item
 *
 * @author KnowledgePath Solutions Inc.
 */
@SuppressWarnings("serial")
public class MFFCommerceItemImpl extends CommerceItemImpl{

	private MFFTaxPriceInfo mTaxPriceInfo = null;
	private MutableRepositoryItem mTaxPriceInfoRepositoryItem = null;
	private boolean freeShippingPromo=false;
	private Set<String> extendToShipMethods;
	private String gwpPromoId;
	private boolean overrideDiscountable=false;

	public boolean isOverrideDiscountable() {
		return overrideDiscountable;
	}

	public void setOverrideDiscountable(boolean pOverrideDiscountable) {
		overrideDiscountable = pOverrideDiscountable;
	}

	public double getGwpValue() {
		if(getPropertyValue("gwpValue") != null)
		return (Double) getPropertyValue("gwpValue");
		else
			return 0.0;
	}

	public void setGwpValue(double pGwpValue) {
		setPropertyValue("gwpValue", pGwpValue);
	}


	public boolean isGwpGiftCardPromo() {
		if(getPropertyValue("gwpGiftCardPromo") != null)
		return (Boolean) getPropertyValue("gwpGiftCardPromo");
		else
			return false;
	}

	public void setGwpGiftCardPromo(boolean pGwpGiftCardPromo) {
		setPropertyValue("gwpGiftCardPromo", pGwpGiftCardPromo);
	}
	
	public double getGwpGiftCardValue() {
		if(getPropertyValue("gwpGiftCardValue") != null){
		return (Double) getPropertyValue("gwpGiftCardValue");
		} else {
			return 0.0;
	}
	}

	public void setGwpGiftCardValue(double pGwpGCValue) {
		setPropertyValue("gwpGiftCardValue", pGwpGCValue);
	}
	
	public String getGwpPromoId() {
		return (String) getPropertyValue("gwpPromoId");
	}

	public void setGwpPromoId(String pGwpPromoId) {
		setPropertyValue("gwpPromoId", pGwpPromoId);
	}

	public boolean isFreeShippingPromo() {
		if(getPropertyValue("isFreeShippingPromo") != null)
		return (Boolean) getPropertyValue("isFreeShippingPromo");
		else
			return false;
	}

	public void setFreeFreightShipping(boolean pFreeFreightShipping) {
		setPropertyValue("isFreeShippingPromo", pFreeFreightShipping);
	}

	public Set getExtendToShipMethods() {
		return extendToShipMethods;
	}

	public void setExtendToShipMethods(Set pExtendToShipMethods) {
		extendToShipMethods = pExtendToShipMethods;
	}

	public MFFTaxPriceInfo getTaxPriceInfo() {
		return mTaxPriceInfo;
	}

	public void setTaxPriceInfo(MFFTaxPriceInfo pTaxPriceInfo) {
		mTaxPriceInfo = pTaxPriceInfo;
	}

	/**
	 * @return Returns the taxPriceInfoRepositoryItem.
	 */
	public MutableRepositoryItem getTaxPriceInfoRepositoryItem() {
		return mTaxPriceInfoRepositoryItem;
	}

	/**
	 * @param pTaxPriceInfoRepositoryItem
	 *            The taxPriceInfoRepositoryItem to set.
	 */
	public void setTaxPriceInfoRepositoryItem(
			MutableRepositoryItem pTaxPriceInfoRepositoryItem) {
		mTaxPriceInfoRepositoryItem = pTaxPriceInfoRepositoryItem;
	}


	public double getShipping() {
		return (Double) getPropertyValue(MFFConstants.SHIPPING);
	}

	public void setShipping(double pShipping) {
		setPropertyValue(MFFConstants.SHIPPING, pShipping);
	}

	public double getShippingDiscount() {
	  return (Double) getPropertyValue(MFFConstants.SHIPPING_DISCOUNT);
  }

  public void setShippingDiscount(double pShippingDiscount) {
    setPropertyValue(MFFConstants.SHIPPING_DISCOUNT, pShippingDiscount);
  }

  public double getShippingTax() {
		return (Double) getPropertyValue(MFFConstants.SHIPPING_TAX);
	}
	public void setShippingTax(double pShippingTax) {
		setPropertyValue(MFFConstants.SHIPPING_TAX, pShippingTax);
	}

	public double getShippingCityTax() {
    return (Double) getPropertyValue(MFFConstants.SHIPPING_CITY_TAX);
  }
  public void setShippingCityTax(double pShippingCityTax) {
    setPropertyValue(MFFConstants.SHIPPING_CITY_TAX, pShippingCityTax);
  }

  public double getShippingStateTax() {
    return (Double) getPropertyValue(MFFConstants.SHIPPING_STATE_TAX);
  }
  public void setShippingStateTax(double pShippingStateTax) {
    setPropertyValue(MFFConstants.SHIPPING_STATE_TAX, pShippingStateTax);
  }

  public double getShippingCountyTax() {
    return (Double) getPropertyValue(MFFConstants.SHIPPING_COUNTY_TAX);
  }
  public void setShippingCountyTax(double pShippingCountyTax) {
    setPropertyValue(MFFConstants.SHIPPING_COUNTY_TAX, pShippingCountyTax);
  }

  public double getShippingDistrictTax() {
    return (Double) getPropertyValue(MFFConstants.SHIPPING_DISTRICT_TAX);
  }
  public void setShippingDistrictTax(double pShippingDistrictTax) {
    setPropertyValue(MFFConstants.SHIPPING_DISTRICT_TAX, pShippingDistrictTax);
  }

  public double getShippingCountryTax() {
    return (Double) getPropertyValue(MFFConstants.SHIPPING_COUNTRY_TAX);
  }
  public void setShippingCountryTax(double pShippingCountryTax) {
    setPropertyValue(MFFConstants.SHIPPING_COUNTRY_TAX, pShippingCountryTax);
  }

	public String getFulfillmentStore() {
		return (String) getPropertyValue(MFFConstants.FULFILLMENT_STORE);
	}
	public void setFulfillmentStore(String pFulfillmentStore) {
		setPropertyValue(MFFConstants.FULFILLMENT_STORE, pFulfillmentStore);
	}

	/**
	 * @return The date the shipment message was received from WMS
	 */
	public Date getShipDate() {
		return (Date) getPropertyValue(MFFConstants.PROPERTY_SHIP_DATE);
	}
	/**
	 * @param shipDate The date the shipment message was received from WMS
	 */
	public void setShipDate(Date shipDate) {
		setPropertyValue(MFFConstants.PROPERTY_SHIP_DATE, shipDate);
	}

	/**
	 * @return The date item returned message was received from WMS
	 */
	public Date getReturnDate() {
		return (Date) getPropertyValue(MFFConstants.PROPERTY_RETURN_DATE);
	}
	/**
	 * @param returnDate The date item returned message was received from WMS
	 */
	public void setReturnDate(Date returnDate) {
		setPropertyValue(MFFConstants.PROPERTY_RETURN_DATE, returnDate);
	}
	/**
	 * @return The date item canceled message was received from WMS
	 */
	public Date getCancelDate() {
		return (Date) getPropertyValue(MFFConstants.PROPERTY_CANCEL_DATE);
	}
	/**
	 * @param cancelDate The date item canceled message was received from WMS
	 */
	public void setCancelDate(Date cancelDate) {
		setPropertyValue(MFFConstants.PROPERTY_CANCEL_DATE, cancelDate);
	}

	/**
	 * @return Shipment tracking Number for the item
	 */
	public String getTrackingNumber() {
		return (String) getPropertyValue(MFFConstants.PROPERTY_TRACKING_NUMBER);
	}

	/**
	 * @param trackingUrl Shipment tracking Number for the item
	 */
	public void setTrackingNumber(String trackingNumber) {
		setPropertyValue(MFFConstants.PROPERTY_TRACKING_NUMBER, trackingNumber);
	}

	@SuppressWarnings("rawtypes")
	public Set getPreviousAllocation() {
		return (Set)getPropertyValue(MFFConstants.PREVIOUS_ALLOCATION);
	}

	@SuppressWarnings("rawtypes")
	public void setPreviousAllocation(Set previousAllocation) {
		setPropertyValue(MFFConstants.PREVIOUS_ALLOCATION, previousAllocation);
	}

	/**
   * @return canceled description
   */
  public String getCancelDescription() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_CANCEL_DESCRIPTION);
  }

  /**
   * @param cancelDescription order canceled description
   */
  public void setCancelDescription(String cancelDescription) {
    setPropertyValue(MFFConstants.PROPERTY_CANCEL_DESCRIPTION, cancelDescription);
  }

  public String getGiftCardNumber() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_GC_NUMBER);
    }

  public void setGiftCardNumber(String pGiftCardNumber) {
    setPropertyValue(MFFConstants.PROPERTY_GC_NUMBER, pGiftCardNumber);
  }

  /**
   *
   * @return Minimum Age
   */
  public Integer getMinimumAge() {
    return (Integer) getPropertyValue(MFFConstants.PROPERTY_MINIMUM_AGE);
  }

  public void setMinimumAge(Integer pMinimumAge) {
    setPropertyValue(MFFConstants.PROPERTY_MINIMUM_AGE, pMinimumAge);
  }

  /**
   *
   * @return FFL Flag
   */
  public Boolean getFFL() {
    return (Boolean) getPropertyValue(MFFConstants.PROPERTY_FFL);
  }

  public void setFFL(Boolean pFFL) {
    setPropertyValue(MFFConstants.PROPERTY_FFL, pFFL);
  }

  public Boolean getDropShip() {
    return (Boolean) getPropertyValue(MFFConstants.PROPERTY_DROP_SHIP);
  }

  public void setDropShip(Boolean pDropShip) {
    setPropertyValue(MFFConstants.PROPERTY_DROP_SHIP, pDropShip);
  }

  /**
   *
   * @return GiftCard Flag
   */
  public boolean isGiftCard() {
    boolean isGiftCard = false;
    if(getPropertyValue(MFFConstants.PROPERTY_GIFTCARD) != null){
      isGiftCard = (Boolean) getPropertyValue(MFFConstants.PROPERTY_GIFTCARD);
    }
    return isGiftCard;
  }

  public void setGiftCard(boolean pGiftCard) {
    setPropertyValue(MFFConstants.PROPERTY_GIFTCARD, pGiftCard);
  }

  /**
   *
   * @return GiftCard Denominations
   */
  public double getGiftCardDenomination() {
    return (Double) getPropertyValue(MFFConstants.PROPERTY_GIFTCARD_DENOMINATION);
  }

  public void setGiftCardDenomination(double pGiftCardDenomination) {
    setPropertyValue(MFFConstants.PROPERTY_GIFTCARD_DENOMINATION, pGiftCardDenomination);
  }

  @SuppressWarnings("rawtypes")
  public Set getReturnItemIds() {
    return (Set)getPropertyValue(MFFConstants.PROPERTY_RETURN_ITEM_IDS);
  }

  @SuppressWarnings("rawtypes")
  public void setReturnItemIds(Set pReturnItemIds) {
    setPropertyValue(MFFConstants.PROPERTY_RETURN_ITEM_IDS, pReturnItemIds);
  }

  private List<RepositoryItem> returnItems;

  public List<RepositoryItem> getReturnItems() {
    return returnItems;
  }

  public void setReturnItems(List<RepositoryItem> pReturnItems) {
    this.returnItems = pReturnItems;
  }

  @SuppressWarnings("rawtypes")
  public boolean isGwp(){

    if(getRepositoryItem() != null){
      Set gwpMarker = (Set)getRepositoryItem().getPropertyValue("gwpMarkers");
      if(gwpMarker != null && gwpMarker.size() > 0){
        return true;
      }
    }
    return false;
  }

  /**
   * @return activation attempts
   */
  public Integer getActivationAttempts() {
    return (Integer) getPropertyValue(MFFConstants.PROPERTY_ACTIVATION_ATTEMPTS);
  }

  public void setActivationAttempts(Integer pActivationAttempts) {
    setPropertyValue(MFFConstants.PROPERTY_ACTIVATION_ATTEMPTS, pActivationAttempts);
  }

  public String getRejectionReasonCodes() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_REJECTION_REASON_CODES);
  }

  public void setRejectionReasonCodes(String pRejectionReasonCodes) {
    setPropertyValue(MFFConstants.PROPERTY_REJECTION_REASON_CODES, pRejectionReasonCodes);
  }

  public String toString(){
	  return this.getClass().getName() + ":\n\t" +
	  super.toString()
         + "\n\tshipping: " + getShipping()
         + "\n\tshippingTax: " + getShippingTax()
         + "\n\tfulfillmentStore: " + getFulfillmentStore();
	}

}
