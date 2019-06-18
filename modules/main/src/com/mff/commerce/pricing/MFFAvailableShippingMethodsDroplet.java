package com.mff.commerce.pricing;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.AvailableShippingMethodsDroplet;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.calculators.ShippingCostHelper;
import com.mff.commerce.pricing.calculators.ShippingSurchargeByItemCalculator;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;
import com.mff.util.ShippingMethod;
import com.mff.zip.MFFZipcodeHelper;

/**
 * This droplet is extended to get the available ship methods based on the items
 * in that shipping group and shipping address along with the corresponding
 * shipping price.
 * 
 * @author Mastanreddy
 * 
 */
public class MFFAvailableShippingMethodsDroplet extends AvailableShippingMethodsDroplet {

  private String[] mStandardShipRanges;
  private String[] mSecondDayShipRanges;
  private String[] mOverNightShipRanges;
  private String[] mLTLRanges;
  private boolean upChargeAddAmount;
  private Map<String, String> mUpCharges = new HashMap<String, String>();
  private double mStdGiftCardShipAmount;
  private double mSecondDayGiftCardShipAmount;
  private double mOverNightGiftCardShipAmount;
  ShippingSurchargeByItemCalculator mShippingSurchargeBySkuCalculator;
  String mDefaultCurrencyCode;
  PricingTools mPricingTools;
  private MFFZipcodeHelper mZipCodeHelper;
  private Map<String, String> mShippingMethodNamesMap = new HashMap<String, String>();
  private Map<String, String> mShippingMethodNotesMap = new HashMap<String, String>();
  private boolean expressCheckoutFix;
  private ShippingCostHelper shippingCostHelper;


  public ShippingCostHelper getShippingCostHelper() {
	return shippingCostHelper;
}

public void setShippingCostHelper(ShippingCostHelper pShippingCostHelper) {
	shippingCostHelper = pShippingCostHelper;
}

public boolean isExpressCheckoutFix() {
	return expressCheckoutFix;
}

public void setExpressCheckoutFix(boolean pExpressCheckoutFix) {
	expressCheckoutFix = pExpressCheckoutFix;
}

/**
   * @return the upCharges
   */
  public Map<String, String> getUpCharges() {
    return mUpCharges;
  }

  /**
   * @param pUpCharges
   *          the upCharges to set
   */
  public void setUpCharges(Map<String, String> pUpCharges) {
    mUpCharges = pUpCharges;
  }

  private List<String> mSecondDayShipStates;

  /**
   * @return the secondDayShipStates
   */
  public List<String> getSecondDayShipStates() {
    return mSecondDayShipStates;
  }

  /**
   * @param pSecondDayShipStates
   *          the secondDayShipStates to set
   */
  public void setSecondDayShipStates(List<String> pSecondDayShipStates) {
    mSecondDayShipStates = pSecondDayShipStates;
  }

  /**
   * @return the lTLRanges
   */
  public String[] getLTLRanges() {
    return mLTLRanges;
  }

  /**
   * @param pLTLRanges
   *          the lTLRanges to set
   */
  public void setLTLRanges(String[] pLTLRanges) {
    mLTLRanges = pLTLRanges;
  }

  /**
   * @return the standardShipRanges
   */
  public String[] getStandardShipRanges() {
    return mStandardShipRanges;
  }

  /**
   * @param pStandardShipRanges
   *          the standardShipRanges to set
   */
  public void setStandardShipRanges(String[] pStandardShipRanges) {
    mStandardShipRanges = pStandardShipRanges;
  }

  /**
   * @return the secondDayShipRanges
   */
  public String[] getSecondDayShipRanges() {
    return mSecondDayShipRanges;
  }

  /**
   * @param pSecondDayShipRanges
   *          the secondDayShipRanges to set
   */
  public void setSecondDayShipRanges(String[] pSecondDayShipRanges) {
    mSecondDayShipRanges = pSecondDayShipRanges;
  }

  /**
   * @return the overNightShipRanges
   */
  public String[] getOverNightShipRanges() {
    return mOverNightShipRanges;
  }

  /**
   * @param pOverNightShipRanges
   *          the overNightShipRanges to set
   */
  public void setOverNightShipRanges(String[] pOverNightShipRanges) {
    mOverNightShipRanges = pOverNightShipRanges;
  }

  private boolean isUpChargeAddAmount() {
    return upChargeAddAmount;
  }

  public void setUpChargeAddAmount(boolean pUpChargeAddAmount) {
    upChargeAddAmount = pUpChargeAddAmount;
  }

  @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    String perfName = "service";
    PerformanceMonitor.startOperation("AvailableShippingMethodsDroplet", perfName);
    boolean perfCancelled = false;
    try {
      List availableMethods = null;
      Map<String, ShippingMethod> shipmethods = new LinkedHashMap();

      if (getShippingPricingEngine() != null) {
        ShippingGroup shippingGroup = getShippingGroup(pRequest, pResponse);

        Collection pricingModels = getPricingModels(pRequest, pResponse);
        RepositoryItem profile = getProfile(pRequest, pResponse);
        Locale locale = getUserLocale(pRequest, pResponse);
        if (shippingGroup != null) {
        	
        	MFFPricingUtil pricingUtil = new MFFPricingUtil();
          try {
            availableMethods = getShippingPricingEngine().getAvailableMethods(shippingGroup, pricingModels, locale, profile, null);
            Object object = pRequest.getObjectParameter(MFFConstants.ORDER);
            if (object != null) {
              Order order = (Order) object;
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
              if (shippingGroup instanceof HardgoodShippingGroup) {
                Address address = null;
                HardgoodShippingGroup hardgoodShipping = (HardgoodShippingGroup) shippingGroup;
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
            if (object != null) {
              Order order = (Order) object;
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
            boolean isLTLDiscount=false;
            for (Object shipMethod : availableMethods) {
              if (shipMethod.toString().equals(MFFConstants.STANDARD)) {
                amount = getAmount(shippingGroup, getStandardShipRanges(), shipMethod);
                if(gcOnlyOrder)
                  amount=getStdGiftCardShipAmount();
                if(containsGC)
                  amount=amount+getStdGiftCardShipAmount();
              } else if (shipMethod.toString().equals(MFFConstants.SECOND_DAY)) {
                amount = getAmount(shippingGroup, getSecondDayShipRanges(), shipMethod);
                if(gcOnlyOrder)
                  amount=getSecondDayGiftCardShipAmount();
                if(containsGC)
                  amount=amount+getSecondDayGiftCardShipAmount();
              } else if (shipMethod.toString().equals(MFFConstants.OVER_NIGHT)) {
                amount = getAmount(shippingGroup, getOverNightShipRanges(), shipMethod);
                if(gcOnlyOrder)
                  amount=getOverNightGiftCardShipAmount();
                if(containsGC)
                  amount=amount+getOverNightGiftCardShipAmount();
              } else if (shipMethod.toString().equals(MFFConstants.LTL_TRUCK)) {
                  if (object != null) {
                      MFFOrderImpl order = (MFFOrderImpl) object;
                      //if(order.isLTLOrder() && !order.isFreeLTLItemsOnly()) {
                      MFFPricingUtil util = new MFFPricingUtil();
                      if(util.isCalculateLTLShippingCosts((Order)object, shipMethod.toString())) {
                    	  amount = getAmount(shippingGroup, getLTLRanges(), shipMethod);
                      } else {
                    	  amount = getAmount(shippingGroup, getStandardShipRanges(), MFFConstants.STANDARD);
                      }
                      //} else {
                    	//  amount = getAmount(shippingGroup, getStandardShipRanges(), MFFConstants.STANDARD);
                      //}
                  }
                
                if(amount == 0.0) {
                	isLTLDiscount = true;
                }
              }
              //if (shippingMethodsList.contains(shipMethod)) {
                amount = addShippingUpCharges((Order) object, amount, shippingGroup, pricingUtil);
              //}
              ShippingMethod shipMethodObj = new ShippingMethod();
              String shippingAmount = "";
              String shippingMethodKey = shipMethod.toString();
              if(gcOnlyOrder){
            	if (amount == 0.0) {
            		shippingAmount = "FREE";
                }
                else{
                  //format the double as we are loosing the 00's
                  DecimalFormat df = new DecimalFormat("#.00");
                  shippingAmount = df.format(amount);
                }
              } else if (amount > 0.0) {
            	  shippingAmount = Double.toString(amount);
              } else if(amount==0.0 && isLTLDiscount) {
            	  shippingAmount = "FREE";
              } else if (amount==0.0) {
            	  // 2564
            	  // all else fails and amount is 0.0 then its probably from a free item shipping promo
            	  shippingAmount = "FREE";
              }
              
              shipMethodObj.setShippingMethodName(getShippingMethodNamesMap().get(shippingMethodKey));
              shipMethodObj.setShippingMethodNote(getShippingMethodNotesMap().get(shippingMethodKey));
              shipMethodObj.setShippingMethodAmount(shippingAmount);
              shipmethods.put(shippingMethodKey, shipMethodObj);
              
              amount = 0.0;
              
            }
          } catch (PricingException exc) {
            try {
              if (!(perfCancelled)) {
                PerformanceMonitor.cancelOperation("AvailableShippingMethodsDroplet", perfName);
                perfCancelled = true;
              }
            } catch (PerfStackMismatchException psm) {
              if (isLoggingWarning()) {
                logWarning(psm);
              }
            }
            if (isLoggingError()) logError(exc);
          } catch (PropertyNotFoundException e) {
            try {
              if (!(perfCancelled)) {
                PerformanceMonitor.cancelOperation("AvailableShippingMethodsDroplet", perfName);
                perfCancelled = true;
              }
            } catch (PerfStackMismatchException psm) {
              if (isLoggingWarning()) {
                logWarning(psm);
              }
            }
            if (isLoggingError()) logError(e);
          }

        } else {
          vlogError("missingRequiredInputParam");
        }

      }
      
      pRequest.setParameter("availableShippingMethods", shipmethods);
      pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
    } finally {
      try {
        if (!(perfCancelled)) {
          PerformanceMonitor.endOperation("AvailableShippingMethodsDroplet", perfName);
          perfCancelled = true;
        }
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(e);
        }
      }
    }
  }

  protected double addShippingUpCharges(Order pOrder, double pAmount, ShippingGroup pShippingGroup, MFFPricingUtil pPricingUtil) {
    String shipmethod = pShippingGroup.getShippingMethod();
    Boolean saturDayFlag = ((MFFHardgoodShippingGroup) pShippingGroup).isSaturdayDelivery();

    double shipAmount = pAmount;
    
    Boolean isSignatureRequired = false;
    vlogDebug("addShippingUpCharges(): ShipMethod ::{0},Saturday Flag: {1} ", shipmethod, saturDayFlag);
    vlogDebug("addShippingUpCharges(): Amount:{0}", pAmount);
    
    long addtnnalHandlingItemsCount = 0;
    long overSizeItemsCount = 0;

    if (isUpChargeAddAmount()) {
      shipAmount = pAmount;
      CommerceItem commerceItem = null;

      List CIRelationships = ((ShippingGroupImpl) pShippingGroup).getCommerceItemRelationships();
      boolean isLtl = pPricingUtil.isLTLOrderByCiRelationships(CIRelationships);
      boolean calcLTLRates = false;
      if(!isExpressCheckoutFix()) {
    	  calcLTLRates = pPricingUtil.isCalculateLTLShippingCosts(pOrder, pShippingGroup.getShippingMethod());
      } else {
    	  if(pOrder != null) {
    		  calcLTLRates = pPricingUtil.isCalculateLTLShippingCosts(pOrder, pShippingGroup.getShippingMethod());
    	  }
      }
      vlogDebug("addShippingUpCharges(): isLtl: " +isLtl);
      
      if (!isLtl  || (isLtl && !calcLTLRates)){
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
	    		  
/*	    		 
 * 				BZ 3063 - Commented out in lieu of new calculations 
 * 				 if (pPricingUtil.isAdditionalHandling(commerceItem)) {
	    			  if (pPricingUtil.isOverSized(commerceItem)) {
	    				  vlogDebug("addShippingUpCharges(): This is Oversized Item with CommerceItem Id :: " + commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
	    				  overSizeItemsCount = overSizeItemsCount + commerceItem.getQuantity();
	    			  } else {
	    				  vlogDebug("addShippingUpCharges(): This is Additional lHandling Item with CommerceItem Id :: " + commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
	    				  addtnnalHandlingItemsCount = addtnnalHandlingItemsCount + commerceItem.getQuantity();
	    			  }
	    		  }*/

	    		  if (getShippingCostHelper().isAdditionalHandling(commerceItem)) {
	    				  vlogDebug("addShippingUpCharges(): This is Additional lHandling Item with CommerceItem Id :: " + commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
	    				  addtnnalHandlingItemsCount = addtnnalHandlingItemsCount + commerceItem.getQuantity();
	    		  }
    			  if (getShippingCostHelper().isOverSized(commerceItem)) {
    				  vlogDebug("addShippingUpCharges(): This is Oversized Item with CommerceItem Id :: " + commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
    				  overSizeItemsCount = overSizeItemsCount + commerceItem.getQuantity();
    			  }	    		  

	    		  if (pPricingUtil.isSignatureRequired(commerceItem) && !isSignatureRequired) {
	    			  vlogDebug("addShippingUpCharges(): This is Signature Required Item with CommerceItem Id :: " + commerceItem.getId());
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
			vlogDebug("addShippingUpCharges(): LongLite upcharges: " +shipAmount);
		}
      
    	if (overSizeItemsCount > 0) {
			shipAmount = shipAmount +  (Double.valueOf(getUpCharges().get("Oversized")) * overSizeItemsCount);
			vlogDebug("addShippingUpCharges(): OverSized upcharges: " + shipAmount);
		}
      
    	if (saturDayFlag && pPricingUtil.isSaturdayDelivery(pShippingGroup)) {
    		shipAmount = shipAmount + Double.valueOf(getUpCharges().get("Saturday"));
    		vlogDebug("addShippingUpCharges(): SaturdayDelvery upcharges " + getUpCharges().get("Saturday"));
    	}
    	if (isSignatureRequired) {
    		shipAmount = shipAmount + Double.valueOf(getUpCharges().get("SignatureRequired"));
    		vlogDebug("addShippingUpCharges(): Signature Required upcharges " + getUpCharges().get("SignatureRequired"));
    	}
    }
    
    vlogDebug("addShippingUpCharges(): Final shipping amount after applying all surchanges: " + shipAmount);
	shipAmount = getPricingTools().round(shipAmount, getDefaultCurrencyCode());
	vlogDebug("addShippingUpCharges(): rounded shipping amount to: " + shipAmount);

    return shipAmount;
  }

  protected double getAmount(ShippingGroup pShippingGroup, String[] pPriceRanges, Object pShipMethod) throws PricingException, PropertyNotFoundException {
    double subtotal = getRangeComparisonValue(pShippingGroup, pShipMethod);
    String [] ranges = pPriceRanges;
/*    if(pShipMethod.toString().equalsIgnoreCase(MFFConstants.LTL_TRUCK)) {
    	if(subtotal < 150) {
    		subtotal = getRangeComparisonValue(pShippingGroup, MFFConstants.STANDARD);
    		ranges = getStandardShipRanges();
    	}
    }*/
    int length = ranges.length;
    for (int c = 0; c < length; c++) {
      String[] subParts = StringUtils.splitStringAtString(ranges[c], ":");
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
    MFFPricingUtil util = new MFFPricingUtil();
    
    List<CommerceItemRelationship> relationships = pShippingGroup.getCommerceItemRelationships();
    if (relationships != null) {
      int num = relationships.size();
      for (int c = 0; c < num; ++c) {
        CommerceItemRelationship relationship = (CommerceItemRelationship) relationships.get(c);
        if (relationship != null) {
          CommerceItem item = relationship.getCommerceItem();
          if (item != null) {
            if (pShipMethod.toString().equalsIgnoreCase(MFFConstants.LTL_TRUCK) ) {
              Object weightObject = DynamicBeans.getPropertyValue(item.getAuxiliaryData().getCatalogRef(), "weight");
              BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
              if (weightObject != null && !util.isFreeFreightItem(item)) subTotal += (itemQuantity.multiply(BigDecimal.valueOf(((Number) weightObject).doubleValue()))).doubleValue();
            } else {
              ItemPriceInfo info = item.getPriceInfo();
              if (info != null) {
				// 2564 - Ignore items with free item shipping discounts
                //if(!((MFFCommerceItemImpl)item).isGiftCard() && !((MFFCommerceItemImpl)item).isFreeFreightShipping())
            	  if(!skipShipping(item, pShipMethod.toString())) 
            		  subTotal += info.getAmount();
              }
            }
          }
        }
      }
    }

    return subTotal;
  }

	private boolean skipShipping (CommerceItem pItem, String pShipMethod) {
		boolean skipShipping = false;
		
		MFFCommerceItemImpl item = (MFFCommerceItemImpl)pItem;
		// skip shipping if item is a GC
		
		if(item.isGiftCard()) {
			return true;
		}
		
		// if item has freeFreightShipping flag set for the sku, then skip
		MFFPricingUtil util = new MFFPricingUtil();
		if(util.isFreeFreightItemForShipMethod(item, pShipMethod)) {
			return true;
		}
		
/*		// skip if item has qualified for a free ship promo & ship method is STANDARD
		if(item.isFreeShippingPromo() && pShipMethod.equals("Standard")) {
			return true;
		}

		// skip if item has qualified for a free ship promo
		// ship method is one of the configured ship methods in the promo
		if(item.isFreeShippingPromo() && item.getExtendToShipMethods() != null && item.getExtendToShipMethods().contains(pShipMethod)) {
			return true;
		}*/
		
		return skipShipping;
	}
	
  public double getStdGiftCardShipAmount() {
    return mStdGiftCardShipAmount;
  }

  public void setStdGiftCardShipAmount(double pStdGiftCardShipAmount) {
    mStdGiftCardShipAmount = pStdGiftCardShipAmount;
  }

  public double getSecondDayGiftCardShipAmount() {
    return mSecondDayGiftCardShipAmount;
  }

  public void setSecondDayGiftCardShipAmount(double pSecondDayGiftCardShipAmount) {
    mSecondDayGiftCardShipAmount = pSecondDayGiftCardShipAmount;
  }

  public double getOverNightGiftCardShipAmount() {
    return mOverNightGiftCardShipAmount;
  }

  public void setOverNightGiftCardShipAmount(double pOverNightGiftCardShipAmount) {
    mOverNightGiftCardShipAmount = pOverNightGiftCardShipAmount;
  }

  public ShippingSurchargeByItemCalculator getShippingSurchargeBySkuCalculator() {
	return mShippingSurchargeBySkuCalculator;
  }

  public void setShippingSurchargeBySkuCalculator(
		ShippingSurchargeByItemCalculator pShippingSurchargeBySkuCalculator) {
	mShippingSurchargeBySkuCalculator = pShippingSurchargeBySkuCalculator;
  }
  
  public void setPricingTools(PricingTools pPricingTools) {
		this.mPricingTools = pPricingTools;
	}

	public PricingTools getPricingTools() {
		return this.mPricingTools;
	}

	public String getDefaultCurrencyCode() {
		return mDefaultCurrencyCode;
	}

	public void setDefaultCurrencyCode(String pDefaultCurrencyCode) {
		mDefaultCurrencyCode = pDefaultCurrencyCode;
	}

	public MFFZipcodeHelper getZipCodeHelper() {
		return mZipCodeHelper;
	}

	public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
		mZipCodeHelper = pZipCodeHelper;
	}

	public Map<String, String> getShippingMethodNamesMap() {
		return mShippingMethodNamesMap;
	}

	public void setShippingMethodNamesMap(
			Map<String, String> pShippingMethodNamesMap) {
		mShippingMethodNamesMap = pShippingMethodNamesMap;
	}

	public Map<String, String> getShippingMethodNotesMap() {
		return mShippingMethodNotesMap;
	}

	public void setShippingMethodNotesMap(
			Map<String, String> pShippingMethodNotesMap) {
		mShippingMethodNotesMap = pShippingMethodNotesMap;
	}

}
