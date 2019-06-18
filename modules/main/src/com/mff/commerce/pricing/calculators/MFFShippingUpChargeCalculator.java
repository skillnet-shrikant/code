package com.mff.commerce.pricing.calculators;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.Constants;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingCalculatorImpl;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.repository.RepositoryItem;

import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.util.MFFPricingUtil;

/**
 * This Calculator is used to get the UpCharge for regular shipping groups
 * @author MastanReddy
 *
 */
public class MFFShippingUpChargeCalculator extends ShippingCalculatorImpl {
	
	private Map<String, String> mUpCharges = new HashMap<String, String>();
	private static final String BOPIS_SHIP_METHOD = "Bopis";
	  ShippingCostHelper shippingCostHelper;
	  
	  public ShippingCostHelper getShippingCostHelper() {
		return shippingCostHelper;
	}

	public void setShippingCostHelper(ShippingCostHelper pShippingCostHelper) {
		shippingCostHelper = pShippingCostHelper;
	}
	/**
	 * @return the upCharges
	 */
	public Map<String, String> getUpCharges() {
		return mUpCharges;
	}

	/**
	 * @param pUpCharges the upCharges to set
	 */
	public void setUpCharges(Map<String, String> pUpCharges) {
		mUpCharges = pUpCharges;
	}
	

	/**
	 * This method is used to calculate the shippingPriceInfo
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)throws PricingException {
		
		vlogDebug("priceShippingGroup(): START");
		boolean isBopisOrder = ((MFFOrderImpl)pOrder).isBopisOrder();
		
		if(isBopisOrder){
		   //Confirming we set BOPIS so calculator will not include shipping.
		   pShippingGroup.setShippingMethod(BOPIS_SHIP_METHOD);
		   resetShippingPriceInfo(pPriceQuote);
		   String shipmethod = pShippingGroup.getShippingMethod();
		   vlogDebug("priceShippingGroup(): ShipMethod :: "+shipmethod);
		   return;
		}
		String shipmethod = pShippingGroup.getShippingMethod();
		vlogDebug("priceShippingGroup(): ShipMethod :: "+shipmethod);
		
		/*// ship method is any of the below then only need to calculate the UpCharges, other wise skip the calculation
		if(!(shipmethod.equalsIgnoreCase(MFFConstants.STANDARD) || shipmethod.equalsIgnoreCase(MFFConstants.SECOND_DAY) 
				|| shipmethod.equalsIgnoreCase(MFFConstants.OVER_NIGHT))){
			vlogInfo("Skipping priceShippingGroup() method excution");
			return;
		}*/
		
		if (!(haveItemsToShip(pShippingGroup))) {
			resetShippingPriceInfo(pPriceQuote);
		} else {
			boolean doPricing = false;
			vlogDebug("priceShippingGroup(): isBopisOrder: " + isBopisOrder);
			if (!isBopisOrder) {
				doPricing = performPricing(pShippingGroup);
			}
			vlogDebug("priceShippingGroup(): doPricing: " + doPricing);
			if (doPricing) {
				
				double amount = getAmount(pOrder, pPriceQuote, pShippingGroup,
						pPricingModel, pLocale, pProfile, pExtraParameters);
				
				String currencyCode = (String) pExtraParameters.get("CurrencyCode");
				
				priceShippingPriceInfo(pOrder, amount, pShippingGroup, pPriceQuote, currencyCode);
			}
		}
		vlogDebug("priceShippingGroup(): END");
	}
	
	/**
	 * This method is used to add the UpCharge amount to shipping amount.
	 * @param pAmount 
	 * @param pShippingGroup 
	 * @param pPriceQuote
	 * @param pCurrencyCode
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	protected void priceShippingPriceInfo(Order pOrder, double pAmount, ShippingGroup pShippingGroup, ShippingPriceInfo pPriceQuote, String pCurrencyCode) {
		
		vlogDebug("priceShippingPriceInfo(): START");
		double shipAmount = pAmount;
		MFFPricingUtil util = new MFFPricingUtil();
		Boolean isSignatureRequired = false;
		long commerceItemQty = 0;
		
		long addtnnalHandlingItemsCount = 0;
		long overSizeItemsCount = 0;
		
		String shipmethod = pShippingGroup.getShippingMethod();
		Boolean saturDayFlag = ((MFFHardgoodShippingGroup)pShippingGroup).isSaturdayDelivery();
		
		vlogDebug("priceShippingPriceInfo()ShipMethod ::{0},Saturday Flag: {1} ",shipmethod, saturDayFlag);
		
		vlogDebug("priceShippingPriceInfo(): Input Shipping amount in this calculator:{0}",pAmount);
		
		vlogDebug("priceShippingPriceInfo() isAddAmount: " +isAddAmount());
		
		if (isAddAmount()){
			
			shipAmount = pPriceQuote.getAmount();
			CommerceItem commerceItem = null;
			
			List CIRelationships = ((ShippingGroupImpl) pShippingGroup).getCommerceItemRelationships();
			boolean isLtl = util.isLTLOrderByCiRelationships(CIRelationships);
			boolean calcLTLRates = util.isCalculateLTLShippingCosts(pOrder, pShippingGroup.getShippingMethod());
			vlogDebug("priceShippingPriceInfo(): isLtl: " +isLtl);
			
			if(!isLtl || (isLtl && !calcLTLRates)){
				if ((CIRelationships != null) && (CIRelationships.size() > 0)) {
					int listSize = CIRelationships.size();
					
					for (int i = 0; i < listSize; ++i) {
						ShippingGroupCommerceItemRelationship currentCommerceItemRelationship = 
							(ShippingGroupCommerceItemRelationship)CIRelationships.get(i);
						commerceItem = currentCommerceItemRelationship.getCommerceItem();
						
						// BZ 3063 - Commenting the below out
						
/*						if(util.isAdditionalHandling(commerceItem)){
							if(util.isOverSized(commerceItem)){
		  						vlogDebug("This is Oversized Item with CommerceItem Id :: "+ commerceItem.getId());
		  						overSizeItemsCount = overSizeItemsCount + commerceItem.getQuantity();
							} else {
								vlogDebug("This is Additional Handling Item with CommerceItem Id :: "
										+ commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
								addtnnalHandlingItemsCount = addtnnalHandlingItemsCount + commerceItem.getQuantity();
		  					}
						}*/
						if(getShippingCostHelper().isAdditionalHandling(commerceItem)){
								vlogDebug("This is Additional Handling Item with CommerceItem Id :: "
										+ commerceItem.getId() + " , with Qty: " + commerceItem.getQuantity());
								addtnnalHandlingItemsCount = addtnnalHandlingItemsCount + commerceItem.getQuantity();
						}
						
						if(getShippingCostHelper().isOverSized(commerceItem)){
							vlogDebug("This is Oversized Item with CommerceItem Id :: "+ commerceItem.getId());
							overSizeItemsCount = overSizeItemsCount + commerceItem.getQuantity();
						}
						
			  			if(util.isSignatureRequired(commerceItem) && !isSignatureRequired){
			              vlogDebug("This is Signature Required Item with CommerceItem Id :: "+ commerceItem.getId());
			              isSignatureRequired = true;
			            }
					}
				}
			}
		}
		
		// it should execute only for non-LTL items
		if (isAddAmount()){
			if (addtnnalHandlingItemsCount > 0) {
				shipAmount = shipAmount + (Double.valueOf(getUpCharges().get("AdditionalHandling")) * addtnnalHandlingItemsCount );
				vlogDebug("LongLite upcharges: " +shipAmount);
			}
			if (overSizeItemsCount > 0) {
				shipAmount = shipAmount +  (Double.valueOf(getUpCharges().get("Oversized")) * overSizeItemsCount);
				vlogDebug("OverSized upcharges: " + shipAmount);
			}
			if (saturDayFlag && util.isSaturdayDelivery(pShippingGroup)) {
				shipAmount = shipAmount +  Double.valueOf(getUpCharges().get("Saturday"));
				vlogDebug("SaturdayDelvery upcharges: " + shipAmount);
			}
			if (isSignatureRequired) {
				shipAmount = shipAmount + Double.valueOf(getUpCharges().get("SignatureRequired"));
				vlogDebug("Signature Required upcharges: " + shipAmount);
			}
		}
		
		//shipAmount = shipAmount + pPriceQuote.getAmount();
		
		vlogDebug("Final shipping amount after applying all surchanges: " + shipAmount);
		shipAmount = getPricingTools().round(shipAmount, pCurrencyCode);
		vlogDebug("rounded shipping amount to: " + shipAmount);

		double oldAmount = pPriceQuote.getAmount();
		vlogDebug("shipping old amount=" + oldAmount);
		
		pPriceQuote.setAmount(shipAmount);
		pPriceQuote.setRawShipping(shipAmount);

		List adjustments = pPriceQuote.getAdjustments();

		double adjustAmount = pPriceQuote.getAmount() - oldAmount;
		
		pPriceQuote.getAdjustments().add(new PricingAdjustment(
						Constants.SHIPPING_PRICE_ADJUSTMENT_DESCRIPTION, null, getPricingTools().round(adjustAmount, pCurrencyCode), 1L));

		vlogDebug("priceShippingPriceInfo() : END");
	}
}
