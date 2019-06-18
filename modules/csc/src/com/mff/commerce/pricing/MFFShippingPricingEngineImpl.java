package com.mff.commerce.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingPricingEngineImpl;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.calculators.ShippingCostHelper;
import com.mff.commerce.pricing.calculators.ShippingSurchargeByItemCalculator;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;
import com.mff.zip.MFFZipcodeHelper;

public class MFFShippingPricingEngineImpl extends ShippingPricingEngineImpl {

  private String[] standardShipRanges;
  private String[] secondDayShipRanges;
  private String[] overNightShipRanges;
  private String[] ltlRanges;
  private boolean upChargeAddAmount;
  private Map<String, String> upCharges = new HashMap<String, String>();
  private double stdGiftCardShipAmount;
  private double secondDayGiftCardShipAmount;
  private double overNightGiftCardShipAmount;
  
  private List<String> secondDayShipStates;
  ShippingSurchargeByItemCalculator mShippingSurchargeBySkuCalculator;
  private MFFZipcodeHelper mZipCodeHelper;
  ShippingCostHelper shippingCostHelper;
  
  public ShippingCostHelper getShippingCostHelper() {
	return shippingCostHelper;
}

public void setShippingCostHelper(ShippingCostHelper pShippingCostHelper) {
	shippingCostHelper = pShippingCostHelper;
}

/* (non-Javadoc)
   * @see atg.commerce.pricing.ShippingPricingEngineImpl#getAvailableMethods(atg.commerce.order.ShippingGroup, java.util.Collection, 
   * java.util.Locale, atg.repository.RepositoryItem, java.util.Map)
   */
  @Override
  public List getAvailableMethods(ShippingGroup pShipment, Collection pPricingModels, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
    List availableMethods = super.getAvailableMethods(pShipment, pPricingModels, pLocale, pProfile, pExtraParameters);
    try
    {
      Order order = (Order) getPricingTools().getOrderManager().getOrderForShippingGroup(pShipment);
      if(null!=order)
      {
        MFFPricingUtil pricingUtil = new MFFPricingUtil();
        List<CommerceItem> citems = order.getCommerceItems();
        boolean isLTL = false;
        boolean restrictAir = false;
        if (!citems.isEmpty()) {
          
          isLTL = pricingUtil.isLTLOrderByItems(citems);
          
          if (isLTL) {
            // removing non LTL shipmethods
            availableMethods.remove(MFFConstants.STANDARD);
            availableMethods.remove(MFFConstants.SECOND_DAY);
            availableMethods.remove(MFFConstants.OVER_NIGHT);
          }
          
          for (CommerceItem commerceItem : citems) {
            MFFCommerceItemImpl lMffItem = (MFFCommerceItemImpl) commerceItem;
            RepositoryItem lSku = (RepositoryItem) lMffItem.getAuxiliaryData().getCatalogRef();
            
            restrictAir = (Boolean) lSku.getPropertyValue(MFFConstants.SKU_RESTRICT_AIR);
            if (!isLTL && restrictAir) {
              // removing shipmethods delivered through Air
              availableMethods.remove(MFFConstants.SECOND_DAY);
              availableMethods.remove(MFFConstants.OVER_NIGHT);
              break;
            }
  
            boolean isFFL = lMffItem.getFFL();
            Integer lMinimuAge = lMffItem.getMinimumAge();
            vlogDebug("AvailableShippingMethods: removing Second Day, Over Night shipping methods -> ffl Flag:{0}, lMinimumAge:{1}", isFFL, lMinimuAge);
            if (isFFL || (lMinimuAge != null && lMinimuAge >= 18)) {
              availableMethods.remove(MFFConstants.SECOND_DAY);
              availableMethods.remove(MFFConstants.OVER_NIGHT);
              availableMethods.remove(MFFConstants.LTL_TRUCK);
              break;
            }
          }
          if (!isLTL) {
            availableMethods.remove(MFFConstants.LTL_TRUCK);
          }
        } else {
          vlogWarning("No commerce items found on the order, showing default shipping methods");
          availableMethods.remove(MFFConstants.LTL_TRUCK);
        }
        if (pShipment instanceof HardgoodShippingGroup) {
          Address address = null;
          HardgoodShippingGroup hardgoodShipping = (HardgoodShippingGroup) pShipment;
          address = hardgoodShipping.getShippingAddress();
          // for Alaska and Hawaii Standard shipping not allowed
          if (address != null && !getSecondDayShipStates().isEmpty() && getSecondDayShipStates().contains(address.getState()) && availableMethods.contains(MFFConstants.STANDARD)) {
            availableMethods.remove(MFFConstants.STANDARD);
          }
          //Remove overnight, if zip is in fedex standard overnight unavailable table. 
          if (availableMethods.contains(MFFConstants.OVER_NIGHT)
        		  	&& (address != null && getZipCodeHelper().isZipRestrictedForFedExOvernight(address.getPostalCode()))) {
        	  availableMethods.remove(MFFConstants.OVER_NIGHT);
          }
        }
        
      } else {
        vlogWarning("Order is null, showing default shipping methods");
        availableMethods.remove(MFFConstants.LTL_TRUCK);
      }
      //remove bopis shipping methods from the droplet
      //as we will not be showing it in the front end
      availableMethods.remove(MFFConstants.BOPIS);

      //below code block is to verify if order contains only Giftcard
      //or if it contains other items along with Gfitcard
      boolean gcOnlyOrder = false;
      boolean containsGC = false;
      int gcItemCount = 0;
      if (null!=order) {
        List<CommerceItem> citems = order.getCommerceItems();
        if (!citems.isEmpty()) {
          for (CommerceItem commerceItem : citems) {
            MFFCommerceItemImpl item = (MFFCommerceItemImpl) commerceItem;
            if(item.isGiftCard())
              gcItemCount++;
          }
          if(gcItemCount == citems.size()){
            gcOnlyOrder=true;
          }else if(gcItemCount > 0){
            containsGC=true;
          }
        }
      }

      double amount = 0.0;
      ArrayList<String> shippingMethodsList = new ArrayList<String>();
      shippingMethodsList.add(MFFConstants.STANDARD);
      shippingMethodsList.add(MFFConstants.SECOND_DAY);
      shippingMethodsList.add(MFFConstants.OVER_NIGHT);
      for (Object shipMethod : availableMethods) {
        if (shipMethod.toString().equals(MFFConstants.STANDARD)) {
          amount = getAmount(pShipment, getStandardShipRanges(), shipMethod);
          if(gcOnlyOrder)
            amount=getStdGiftCardShipAmount();
          if(containsGC)
            amount=amount+getStdGiftCardShipAmount();
        } else if (shipMethod.toString().equals(MFFConstants.SECOND_DAY)) {
          amount = getAmount(pShipment, getSecondDayShipRanges(), shipMethod);
          if(gcOnlyOrder)
            amount=getSecondDayGiftCardShipAmount();
          if(containsGC)
            amount=amount+getSecondDayGiftCardShipAmount();
        } else if (shipMethod.toString().equals(MFFConstants.OVER_NIGHT)) {
          amount = getAmount(pShipment, getOverNightShipRanges(), shipMethod);
          if(gcOnlyOrder)
            amount=getOverNightGiftCardShipAmount();
          if(containsGC)
            amount=amount+getOverNightGiftCardShipAmount();
        } else if (shipMethod.toString().equals(MFFConstants.LTL_TRUCK)) {
          //amount = getAmount(pShipment, getLtlRanges(), shipMethod);
          if (order != null) {
              MFFOrderImpl orderImpl = (MFFOrderImpl) order;
              if(orderImpl.isLTLOrder() && !orderImpl.isFreeLTLItemsOnly()) {
            	  amount = getAmount(pShipment, getLtlRanges(), shipMethod);
              } else {
            	  amount = getAmount(pShipment, getStandardShipRanges(), MFFConstants.STANDARD);
              }
          }          
        }
        if (shippingMethodsList.contains(shipMethod)) {
          amount = addShippingUpCharges(amount, pShipment);
        }
      }
      
    } catch (CommerceException | RepositoryException | PropertyNotFoundException e) {
      vlogError(e, "An exception occurred while trying to select the available shipping methods");
    }

    return availableMethods;
  }

  protected double getAmount(ShippingGroup pShippingGroup, String[] pPriceRanges, Object pShipMethod) throws PricingException, PropertyNotFoundException {
    double subtotal = getRangeComparisonValue(pShippingGroup, pShipMethod);
    int length = pPriceRanges.length;
    for (int c = 0; c < length; c++) {
      String[] subParts = StringUtils.splitStringAtString(pPriceRanges[c], ":");
      double lowRange = Double.parseDouble(subParts[0]);
      double highRange = 0.0d;
      if (!subParts[1].equalsIgnoreCase("MAX_VALUE"))
        highRange = Double.parseDouble(subParts[1]);
      else
        highRange = Double.MAX_VALUE;
      if ((subtotal >= lowRange) && (subtotal <= highRange)) {
        return Double.valueOf(subParts[2]);
      }
    }
    return 0.0;
  }

  /**
   * Return the value which should be used as a comparison between the range
   * values, Referred to the OOTB code
   * 
   * @param pShipMethod
   * @return the sum of all the Number values fetched from the configured
   *         property
   * @throws PropertyNotFoundException
   */
  @SuppressWarnings({ "unchecked" })
  protected double getRangeComparisonValue(ShippingGroup pShippingGroup, Object pShipMethod) throws PricingException, PropertyNotFoundException {
    double subTotal = 0.0;
    List<CommerceItemRelationship> relationships = pShippingGroup.getCommerceItemRelationships();
    if (relationships != null) {
      int num = relationships.size();
      for (int c = 0; c < num; ++c) {
        CommerceItemRelationship relationship = (CommerceItemRelationship) relationships.get(c);
        if (relationship != null) {
          CommerceItem item = relationship.getCommerceItem();
          if (item != null) {
            if (pShipMethod.toString().equalsIgnoreCase(MFFConstants.LTL_TRUCK)) {
              Object weightObject = DynamicBeans.getPropertyValue(item.getAuxiliaryData().getCatalogRef(), "weight");
              Object freeShippingObject = DynamicBeans.getPropertyValue(item.getAuxiliaryData().getCatalogRef(), "freeShipping");
              BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
              Boolean freeFreight = (Boolean)freeShippingObject;
              if (weightObject != null && !freeFreight) { 
            	  subTotal += (itemQuantity.multiply(BigDecimal.valueOf(((Number) weightObject).doubleValue()))).doubleValue();
              }
            } else {
              ItemPriceInfo info = item.getPriceInfo();
              if (info != null) {
                if(!((MFFCommerceItemImpl)item).isGiftCard())
                  subTotal += info.getAmount();
              }
            }
          }
        }
      }
    }

    return subTotal;
  }

  protected double addShippingUpCharges(double pAmount, ShippingGroup pShippingGroup) {
    String shipmethod = pShippingGroup.getShippingMethod();
    Boolean saturDayFlag = ((MFFHardgoodShippingGroup) pShippingGroup).isSaturdayDelivery();

    double shipAmount = pAmount;
    MFFPricingUtil util = new MFFPricingUtil();
    long addtnnalHandlingItemsCount = 0;
	long overSizeItemsCount = 0;
    Boolean isSignatureRequired = false;
    vlogDebug("addShippingUpCharges(): ShipMethod ::{0},Saturday Flag: {1} ", shipmethod, saturDayFlag);
    vlogDebug("addShippingUpCharges(): Amount:{0}", pAmount);

    if (isUpChargeAddAmount()) {
      shipAmount = pAmount;
      CommerceItem commerceItem = null;

      List CIRelationships = ((ShippingGroupImpl) pShippingGroup).getCommerceItemRelationships();
      
      boolean isLtl = util.isLTLOrderByCiRelationships(CIRelationships);
      vlogDebug("addShippingUpCharges(): isLtl: " +isLtl);
      
      if(!isLtl){
	      if ((CIRelationships != null) && (CIRelationships.size() > 0)) {
	    	  
	    	  double surchargeAmount = 0.0;
	          try {
	        	  surchargeAmount = getShippingSurchargeBySkuCalculator().getTotalSurcharge(CIRelationships);
	        	  shipAmount = shipAmount + surchargeAmount;
	          	} catch (PricingException e) {
					if (isLoggingError()){
						logError("addShippingUpCharges(): PricingException while determining shipping surcharge by item: " +e, e);
					}
				} catch (PropertyNotFoundException e) {
					if (isLoggingError()){
						logError("addShippingUpCharges(): PropertyNotFoundException while determining shipping surcharge by item: " +e, e);
					}
				}
	          
	        int listSize = CIRelationships.size();
	        
	        for (int i = 0; i < listSize; ++i) {
	          ShippingGroupCommerceItemRelationship currentCommerceItemRelationship = (ShippingGroupCommerceItemRelationship) CIRelationships.get(i);
	          commerceItem = currentCommerceItemRelationship.getCommerceItem();
	          
	          // Commented for BZ 3063
/*	          if(util.isAdditionalHandling(commerceItem)){
					if(util.isOverSized(commerceItem)){
						vlogDebug("addShippingUpCharges(): This is Oversized Item with CommerceItem Id: "+ commerceItem.getId());
						overSizeItemsCount = overSizeItemsCount + commerceItem.getQuantity();
					} else {
						vlogDebug("addShippingUpCharges(): This is Additional Handling Item with CommerceItem Id: "
								+ commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
						addtnnalHandlingItemsCount = addtnnalHandlingItemsCount + commerceItem.getQuantity();
					}
				}*/

	          // BZ 3063 - Oversized charges computed independent of additional handling
	          if(getShippingCostHelper().isAdditionalHandling(commerceItem)){
	        		  vlogDebug("addShippingUpCharges(): This is Additional Handling Item with CommerceItem Id: "
	        				  + commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
	        		  addtnnalHandlingItemsCount = addtnnalHandlingItemsCount + commerceItem.getQuantity();
	          }
				if(getShippingCostHelper().isOverSized(commerceItem)){
					vlogDebug("addShippingUpCharges(): This is Oversized Item with CommerceItem Id: "+ commerceItem.getId());
					overSizeItemsCount = overSizeItemsCount + commerceItem.getQuantity();
				}
				
	            if (util.isSignatureRequired(commerceItem) && !isSignatureRequired) {
	              vlogDebug("addShippingUpCharges(): This is Signature Required Item with CommerceItem Id: " + commerceItem.getId());
	              isSignatureRequired = true;
	            }
	        }
	      }
      }

    }

    // it should execute only for non-LTL items
    if (isUpChargeAddAmount()) {
      if (addtnnalHandlingItemsCount > 0) {
        shipAmount = shipAmount + (Double.valueOf(getUpCharges().get("AdditionalHandling")) * addtnnalHandlingItemsCount );
        vlogDebug("addShippingUpCharges(): LongLite upcharges: " + shipAmount);
      }
      if (overSizeItemsCount > 0) {
        shipAmount = shipAmount +  (Double.valueOf(getUpCharges().get("Oversized")) * overSizeItemsCount);
        vlogDebug("addShippingUpCharges(): OverSized upcharges: " + getUpCharges().get("Oversized"));
      }
      
      if (saturDayFlag && util.isSaturdayDelivery(pShippingGroup)) {
        shipAmount = shipAmount + Double.valueOf(getUpCharges().get("Saturday"));
        vlogDebug("addShippingUpCharges(): SaturdayDelvery upcharges: " + getUpCharges().get("Saturday"));
      }
      if (isSignatureRequired) {
        shipAmount = shipAmount + Double.valueOf(getUpCharges().get("SignatureRequired"));
        vlogDebug("addShippingUpCharges(): Signature Required upcharges: " + getUpCharges().get("SignatureRequired"));
      }
    }

    return shipAmount;
  }

  /**
   * @return the secondDayShipStates
   */
  public List<String> getSecondDayShipStates() {
    return secondDayShipStates;
  }

  /**
   * @param pSecondDayShipStates the secondDayShipStates to set
   */
  public void setSecondDayShipStates(List<String> pSecondDayShipStates) {
    secondDayShipStates = pSecondDayShipStates;
  }

  /**
   * @return the standardShipRanges
   */
  public String[] getStandardShipRanges() {
    return standardShipRanges;
  }

  /**
   * @param pStandardShipRanges the standardShipRanges to set
   */
  public void setStandardShipRanges(String[] pStandardShipRanges) {
    standardShipRanges = pStandardShipRanges;
  }

  /**
   * @return the secondDayShipRanges
   */
  public String[] getSecondDayShipRanges() {
    return secondDayShipRanges;
  }

  /**
   * @param pSecondDayShipRanges the secondDayShipRanges to set
   */
  public void setSecondDayShipRanges(String[] pSecondDayShipRanges) {
    secondDayShipRanges = pSecondDayShipRanges;
  }

  /**
   * @return the overNightShipRanges
   */
  public String[] getOverNightShipRanges() {
    return overNightShipRanges;
  }

  /**
   * @param pOverNightShipRanges the overNightShipRanges to set
   */
  public void setOverNightShipRanges(String[] pOverNightShipRanges) {
    overNightShipRanges = pOverNightShipRanges;
  }

  /**
   * @return the ltlRanges
   */
  public String[] getLtlRanges() {
    return ltlRanges;
  }

  /**
   * @param pLtlRanges the ltlRanges to set
   */
  public void setLtlRanges(String[] pLtlRanges) {
    ltlRanges = pLtlRanges;
  }

  /**
   * @return the upChargeAddAmount
   */
  public boolean isUpChargeAddAmount() {
    return upChargeAddAmount;
  }

  /**
   * @param pUpChargeAddAmount the upChargeAddAmount to set
   */
  public void setUpChargeAddAmount(boolean pUpChargeAddAmount) {
    upChargeAddAmount = pUpChargeAddAmount;
  }

  /**
   * @return the upCharges
   */
  public Map<String, String> getUpCharges() {
    return upCharges;
  }

  /**
   * @param pUpCharges the upCharges to set
   */
  public void setUpCharges(Map<String, String> pUpCharges) {
    upCharges = pUpCharges;
  }

  /**
   * @return the stdGiftCardShipAmount
   */
  public double getStdGiftCardShipAmount() {
    return stdGiftCardShipAmount;
  }

  /**
   * @param pStdGiftCardShipAmount the stdGiftCardShipAmount to set
   */
  public void setStdGiftCardShipAmount(double pStdGiftCardShipAmount) {
    stdGiftCardShipAmount = pStdGiftCardShipAmount;
  }

  /**
   * @return the secondDayGiftCardShipAmount
   */
  public double getSecondDayGiftCardShipAmount() {
    return secondDayGiftCardShipAmount;
  }

  /**
   * @param pSecondDayGiftCardShipAmount the secondDayGiftCardShipAmount to set
   */
  public void setSecondDayGiftCardShipAmount(double pSecondDayGiftCardShipAmount) {
    secondDayGiftCardShipAmount = pSecondDayGiftCardShipAmount;
  }

  /**
   * @return the overNightGiftCardShipAmount
   */
  public double getOverNightGiftCardShipAmount() {
    return overNightGiftCardShipAmount;
  }

  /**
   * @param pOverNightGiftCardShipAmount the overNightGiftCardShipAmount to set
   */
  public void setOverNightGiftCardShipAmount(double pOverNightGiftCardShipAmount) {
    overNightGiftCardShipAmount = pOverNightGiftCardShipAmount;
  }
  
  public ShippingSurchargeByItemCalculator getShippingSurchargeBySkuCalculator() {
		return mShippingSurchargeBySkuCalculator;
	}

	public void setShippingSurchargeBySkuCalculator(
			ShippingSurchargeByItemCalculator pShippingSurchargeBySkuCalculator) {
		mShippingSurchargeBySkuCalculator = pShippingSurchargeBySkuCalculator;
	}
	
	public MFFZipcodeHelper getZipCodeHelper() {
		return mZipCodeHelper;
	}

	public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
		mZipCodeHelper = pZipCodeHelper;
	}

}
