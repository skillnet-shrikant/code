package com.mff.commerce.pricing.calculators;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.Constants;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.WeightRangeShippingCalculator;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.util.MFFPricingUtil;

/**
 * This calculator determine shipping surcharge by item and add to shipping price.
 * @author DMI
 *
 */
public class ShippingSurchargeByItemCalculator extends WeightRangeShippingCalculator {
	
	String mQntyRangeSeperator;
	private static final String BOPIS_SHIP_METHOD = "Bopis";
	
	/**
	 * This method is used to calculate the shippingPriceInfo
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)throws PricingException {
		
		vlogDebug("priceShippingGroup(): START");
		
		boolean isBopisOrder = ((MFFOrderImpl)pOrder).isBopisOrder();
		
		if(isBopisOrder){
		   //Confirming we set BOPIS so calculator will not include shipping.
		   pShippingGroup.setShippingMethod(BOPIS_SHIP_METHOD);
		   return;
		}
		
		if (!(haveItemsToShip(pShippingGroup))) {
			resetShippingPriceInfo(pPriceQuote);
		} else {
			
			MFFPricingUtil util = new MFFPricingUtil();
			List<CommerceItemRelationship> ciRelationships = ((ShippingGroupImpl) pShippingGroup).getCommerceItemRelationships();
			boolean isLtl = util.isLTLOrderByCiRelationships(ciRelationships);
			vlogDebug("priceShippingPriceInfo(): isLtl: " +isLtl);
			
			// it should execute only for non-LTL items
			if (isLtl){
				return;
			}
				
			double surchargeAmount = 0.0;
			try {
				surchargeAmount = this.getTotalSurcharge(ciRelationships);
			} catch (PropertyNotFoundException e) {
				if (isLoggingError()){
					logError("priceShippingGroup(): PropertyNotFoundException while determining shipping surcharge by item: " +e, e);
				}
			}
			vlogDebug("priceShippingGroup: amount: " + surchargeAmount);
			
			String currencyCode = (String) pExtraParameters.get("CurrencyCode");
			vlogDebug("priceShippingGroup: currencyCode: " + currencyCode);
			
			priceShippingPriceInfo(surchargeAmount, ciRelationships, pPriceQuote, currencyCode);
			
		}
		vlogDebug("priceShippingGroup(): END");
	}
	
	protected double getSurchargeByQntyRange(double pItemQnty, String[] pPriceRanges) throws PricingException {
	    
	    int length = pPriceRanges.length;
	    for (int c = 0; c < length; c++) {
	      String[] subParts = StringUtils.splitStringAtString(pPriceRanges[c], ":");
	      double lowRange = Double.parseDouble(subParts[0]);
	      double highRange = 0.0d;
	      if (!subParts[1].equalsIgnoreCase("MAX_VALUE")){
	        highRange = Double.parseDouble(subParts[1]);
	      } else {
	        highRange = Double.MAX_VALUE;
	      }
	      if ((pItemQnty >= lowRange) && (pItemQnty <= highRange)) {
	        return Double.valueOf(subParts[2]);
	      }
	    }
	    return 0.0;
	  }
	
	/**
	   * Return the total surcharge to be added, based on each item quantity.
	   * 
	   * @param pCIRelationships
	   * @return the sum of all the Number values fetched from shippingSurchargeQntyRange property of sku.  
	   * @throws PropertyNotFoundException
	   */
	  @SuppressWarnings({ "unchecked" })
	  public double getTotalSurcharge(List<CommerceItemRelationship> pCIRelationships) throws PricingException, PropertyNotFoundException {
	    double totalSurchareAmount = 0.0;
	    
	    if (pCIRelationships != null) {
	      int num = pCIRelationships.size();
	      for (int c = 0; c < num; ++c) {
	        CommerceItemRelationship relationship = (CommerceItemRelationship) pCIRelationships.get(c);
	        if (relationship != null) {
	          CommerceItem item = relationship.getCommerceItem();
	          
	          if (item != null) {
	        	  double surchargeByItem = getSurchargePerItem(item);
	              totalSurchareAmount = totalSurchareAmount + surchargeByItem;
	          }
	        }
	      }
	    }

	    return totalSurchareAmount;
	  }

	/**
	 * @param item
	 * @return
	 * @throws PropertyNotFoundException
	 * @throws PricingException
	 */
	public double getSurchargePerItem(CommerceItem item)
			throws PropertyNotFoundException, PricingException {

		String qntyRangeStringForSku = (String) DynamicBeans.getPropertyValue(
				item.getAuxiliaryData().getCatalogRef(),"shippingSurchargeQntyRange");

		if (StringUtils.isBlank(qntyRangeStringForSku)) {
			return 0.0;
		}

		String[] qntyRanges = qntyRangeStringForSku.split(getQntyRangeSeperator());
		double itemQuantity = item.getQuantity();
		double surchargeByItem = getSurchargeByQntyRange(itemQuantity, qntyRanges);
		
		return surchargeByItem;
	}
	
  	/**
	 * This method is used to add the UpCharge amount to shipping amount.
	 * @param pAmount 
	 * @param pShippingGroup 
	 * @param pPriceQuote
	 * @param pCurrencyCode
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	protected void priceShippingPriceInfo(double pSurchargeAmount,
		List pCIRelationships, ShippingPriceInfo pPriceQuote, String pCurrencyCode) {

		vlogDebug("priceShippingPriceInfo(): START");
		double shipAmount = pSurchargeAmount;
		MFFPricingUtil util = new MFFPricingUtil();
	
		vlogDebug("priceShippingPriceInfo() isAddAmount: " + isAddAmount());
	
		if (isAddAmount()) {
			shipAmount = pPriceQuote.getAmount();
			vlogDebug("priceShippingPriceInfo(): Input Shipping amount in this calculator: " + shipAmount);
		}
	
		if (isAddAmount()) {
			shipAmount = shipAmount + pSurchargeAmount;
		}
	
		vlogDebug("Final shipping amount after applying surchanges by item qunatity: "+ shipAmount);
		
		shipAmount = getPricingTools().round(shipAmount, pCurrencyCode);
		vlogDebug("rounded shipping amount to: " + shipAmount);
	
		double oldAmount = pPriceQuote.getAmount();
		vlogDebug("shipping old amount=" + oldAmount);
	
		pPriceQuote.setAmount(shipAmount);
		pPriceQuote.setRawShipping(shipAmount);
	
		List adjustments = pPriceQuote.getAdjustments();
	
		double adjustAmount = pPriceQuote.getAmount() - oldAmount;
	
		pPriceQuote.getAdjustments().add(
				new PricingAdjustment(
						Constants.SHIPPING_PRICE_ADJUSTMENT_DESCRIPTION, null,
						getPricingTools().round(adjustAmount, pCurrencyCode),
						1L));
	
		vlogDebug("priceShippingPriceInfo() : END");
	}

	public String getQntyRangeSeperator() {
		return mQntyRangeSeperator;
	}

	public void setQntyRangeSeperator(String pQntyRangeSeperator) {
		mQntyRangeSeperator = pQntyRangeSeperator;
	}
}
