package com.mff.commerce.pricing.calculators;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.Constants;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.WeightRangeShippingCalculator;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * This calculator is used to get the LTL ship price based on weight
 * @author MastanReddy
 *
 */
public class MFFLTLShippingCalculator extends WeightRangeShippingCalculator {

  MFFPriceRangeShippingCalculator standardCalculator;
  private static final String BOPIS_SHIP_METHOD = "Bopis";

  public MFFPriceRangeShippingCalculator getStandardCalculator() {
    return standardCalculator;
  }

  public void setStandardCalculator(MFFPriceRangeShippingCalculator pStandardCalculator) {
    standardCalculator = pStandardCalculator;
  }
  
  

  /**
	 * This method is used to calculate the shippingPriceInfo
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)throws PricingException {
		
		vlogDebug("priceShippingGroup(): START");
		MFFPricingUtil util = new MFFPricingUtil();
		boolean isBopisOrder = ((MFFOrderImpl)pOrder).isBopisOrder();
		//boolean isLTLOrder = ((MFFOrderImpl)pOrder).isLTLOrder();
		boolean isLTLOrder = util.isLTLOrderByItems(pOrder.getCommerceItems());
		boolean isCalcLTLShipRates = util.isCalculateLTLShippingCosts(pOrder, pShippingGroup.getShippingMethod());
		vlogDebug("Order " + pOrder.getId() + " LTL Order " + isLTLOrder);
		
		if(isBopisOrder){
		   //Confirming we set BOPIS so calculator will not include shipping.
		   pShippingGroup.setShippingMethod(BOPIS_SHIP_METHOD);
		   resetShippingPriceInfo(pPriceQuote);
		   String shipmethod = pShippingGroup.getShippingMethod();
		   vlogDebug("priceShippingGroup(): ShipMethod :: "+shipmethod);
		   return;
		}
		
		// Exit if this is NOT an LTLOrder
		// and is not a mixed cart (a free LTL item with a regular item)
		
/*		if(!isLTLOrder && !((MFFOrderImpl)pOrder).isFreeLTLWithNonLTLItems()) {
			vlogDebug("Order " + pOrder.getId() + " Not calculating LTL charges.");
			return;
		}*/
		if(!isLTLOrder) {
			return;
		}
		
		//MFFPricingUtil util = new MFFPricingUtil();
		List CIRelationships = ((ShippingGroupImpl) pShippingGroup).getCommerceItemRelationships();

		boolean ltlFlag = util.isLTLOrderByCiRelationships(CIRelationships);
		vlogDebug("ltlFlag: " + ltlFlag);
		// skip the calculation part, if item not satisfied LTL conditions.
		if (!ltlFlag) {
			return;
		}
		if (!(haveItemsToShip(pShippingGroup))) {
			resetShippingPriceInfo(pPriceQuote);
		} else {
			
			// it will get the amount based on the weight 
			double amount = getAmount(pOrder, pPriceQuote, pShippingGroup,
					pPricingModel, pLocale, pProfile, pExtraParameters);
			
			String currencyCode = (String) pExtraParameters.get("CurrencyCode");
			priceShippingPriceInfo(amount, pOrder, pShippingGroup, pPriceQuote, currencyCode);
		}
		
		vlogDebug("priceShippingGroup(): END");
	}

	protected double getAmount(Order pOrder,ShippingGroup pShippingGroup, String[] pPriceRanges, Object pShipMethod) throws PricingException, PropertyNotFoundException {
		double subtotal=0.0;
		Object objOrder=null;
		if(pShippingGroup == null) {
			return 0.0;
		}
		try {
			subtotal = getRangeComparisonValue(pShippingGroup,pShipMethod);
		} catch (CommerceException e) {
			vlogError (e, "Unable to get range comparisions", pOrder.getId());
		} 
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
		              if (weightObject != null && !util.isFreeFreightItem(item) &&
		            		  !( ((MFFCommerceItemImpl)item).isFreeShippingPromo() && ((MFFCommerceItemImpl)item).getExtendToShipMethods().contains(MFFConstants.LTL_TRUCK))) {
		            	  subTotal += (itemQuantity.multiply(BigDecimal.valueOf(((Number) weightObject).doubleValue()))).doubleValue();
		              }
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
			if(util.isFreeFreightItem(item)) {
				return true;
			}
			
			// skip if item has qualified for a free ship promo & ship method is STANDARD
			if(item.isFreeShippingPromo() && pShipMethod.equals("Standard")) {
				return true;
			}

			// skip if item has qualified for a free ship promo
			// ship method is one of the configured ship methods in the promo
			if(item.isFreeShippingPromo() && item.getExtendToShipMethods() != null && item.getExtendToShipMethods().contains(pShipMethod)) {
				return true;
			}
			
			return skipShipping;
		}	  
	@Override
	protected double getAmount(Order pOrder, ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup,
			RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
		MFFOrderImpl order = (MFFOrderImpl) pOrder;
		MFFPricingUtil util = new MFFPricingUtil();
		
		if(order.isLTLOrder()) {
			if(util.isCalculateLTLShippingCosts(pOrder, pShippingGroup.getShippingMethod())) {
				return super.getAmount(pOrder, pPriceQuote, pShippingGroup, pPricingModel, pLocale, pProfile, pExtraParameters);
			} else {
				try {
					return getAmount(pOrder,pShippingGroup, getStandardCalculator().getRanges(), MFFConstants.STANDARD);
				} catch (PropertyNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		return 0.0;
	}

	@Override
	protected double getWeight(CommerceItem pCommerceItem) throws PricingException {
		double weight = 0.0D;

		// 2427 - Compute weight only if item is not marked free freight shipping
		MFFPricingUtil util = new MFFPricingUtil();
		if(!util.isFreeFreightItem(pCommerceItem) && 
				!( ((MFFCommerceItemImpl)pCommerceItem).isFreeShippingPromo() && ((MFFCommerceItemImpl)pCommerceItem).getExtendToShipMethods().contains(MFFConstants.LTL_TRUCK))) {
			weight = super.getWeight(pCommerceItem);
		}
		return weight;
	}

	/**
	 * This method is used to get the LTL shipAmount and UpCharge amount.
	 * @param pAmount 
	 * @param pOrder 
	 * @param pShippingGroup 
	 * @param pPriceQuote
	 * @param pCurrencyCode
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	protected void priceShippingPriceInfo(double pAmount, Order pOrder, ShippingGroup pShippingGroup, ShippingPriceInfo pPriceQuote, String pCurrencyCode) {
		
		vlogDebug("priceShippingPriceInfo(): START");
		double shipAmount = pAmount;
		
		String shipmethod = pShippingGroup.getShippingMethod();
		vlogDebug("ShipMethod: "+shipmethod + " ; shipping amount: " + shipAmount);
		
		shipAmount = getPricingTools().round(shipAmount, pCurrencyCode);
		vlogDebug("rounded shipping amount to: " + shipAmount);

		double oldAmount = pPriceQuote.getAmount();
		vlogDebug("shipping old amount: " + oldAmount+" ; Raw Shipping Amoun: " +pPriceQuote.getRawShipping());
		
		pPriceQuote.setAmount(shipAmount);
		pPriceQuote.setRawShipping(shipAmount);

		List adjustments = pPriceQuote.getAdjustments();

		double adjustAmount = pPriceQuote.getAmount() - oldAmount;
		
		pPriceQuote.getAdjustments().add(new PricingAdjustment(
						Constants.SHIPPING_PRICE_ADJUSTMENT_DESCRIPTION, null, getPricingTools().round(adjustAmount, pCurrencyCode), 1L));

		vlogDebug("priceShippingPriceInfo(): END");
	}
}
