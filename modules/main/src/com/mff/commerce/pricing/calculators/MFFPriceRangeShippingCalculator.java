package com.mff.commerce.pricing.calculators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.pricing.util.MFFPricingUtil;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PriceRangeShippingCalculator;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;

/**
 * This calculator is used to get the shipping prices based on the shipping group subtotal
 * @author MastanReddy
 *
 */
public class MFFPriceRangeShippingCalculator extends PriceRangeShippingCalculator {
	
	private double mGiftCardShipAmount;
	private List<String> mSecondDayShipStates = new ArrayList<String>();
	private double mAlaskaShipAmount;

	/**
	 * @return the alaskaShipAmount
	 */
	public double getAlaskaShipAmount() {
		return mAlaskaShipAmount;
	}


	/**
	 * @param pAlaskaShipAmount the alaskaShipAmount to set
	 */
	public void setAlaskaShipAmount(double pAlaskaShipAmount) {
		mAlaskaShipAmount = pAlaskaShipAmount;
	}


	/**
	 * @return the secondDayShipStates
	 */
	public List<String> getSecondDayShipStates() {
		return mSecondDayShipStates;
	}


	/**
	 * @param pSecondDayShipStates the secondDayShipStates to set
	 */
	public void setSecondDayShipStates(List<String> pSecondDayShipStates) {
		mSecondDayShipStates = pSecondDayShipStates;
	}


	/**
	 * @return the giftCardShipAmount
	 */
	public double getGiftCardShipAmount() {
		return mGiftCardShipAmount;
	}


	/**
	 * @param pGiftCardShipAmount the giftCardShipAmount to set
	 */
	public void setGiftCardShipAmount(double pGiftCardShipAmount) {
		mGiftCardShipAmount = pGiftCardShipAmount;
	}


	/**
	 * This method is used to get the subtotal of the commerceItems except GiftCard
	 */
	@SuppressWarnings("rawtypes")
	protected double getSubTotal(Order pOrder, ShippingGroup pShippingGroup) {
		
		String perfName = "getSubTotal";
		PerformanceMonitor.startOperation("PriceRangeShippingCalculator", perfName);
		boolean perfCancelled = false;
		try {
			double subTotal = 0.0D;
			MFFPricingUtil util = new MFFPricingUtil();

			List relationships = pShippingGroup.getCommerceItemRelationships();
			if (relationships != null) {
				int num = relationships.size();
				ItemPriceInfo info = null;
				for (int c = 0; c < num; ++c) {
					CommerceItemRelationship relationship = (CommerceItemRelationship) relationships.get(c);
					
					if (relationship != null) {
						CommerceItem item = relationship.getCommerceItem();
						
						if (item != null) {
						  boolean isGC = ((MFFCommerceItemImpl)item).isGiftCard();
						  boolean isFreeFreightPromo = ((MFFCommerceItemImpl)item).isFreeShippingPromo();
						  boolean isFreeFreightShipping = util.isFreeFreightItem(item);
						  
							// get the subTotal of shipGroup items except GiftCard items
							if(item instanceof CommerceItem){
								info = item.getPriceInfo();
								if (info != null && !skipShipping(item)) {
									subTotal += info.getAmount();
								}
							}
							
							/* else if(item instanceof LTLCommerceItem){
								info = item.getPriceInfo();
								if (info != null) {
									subTotal += info.getAmount();
								}
							}*/
						}
					}
				}
			}
			return subTotal;
		} finally {
			try {
				if (!(perfCancelled)) {
					PerformanceMonitor.endOperation("PriceRangeShippingCalculator", perfName);
					perfCancelled = true;
				}
			} catch (PerfStackMismatchException e) {
				if (isLoggingWarning())
					logWarning(e);
			}
		}
	}
	
	private boolean skipShipping (CommerceItem pItem) {
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
		if(item.isFreeShippingPromo() && getShippingMethod().equals("Standard")) {
			return true;
		}

		// skip if item has qualified for a free ship promo
		// ship method is one of the configured ship methods in the promo
		if(item.isFreeShippingPromo() && item.getExtendToShipMethods() != null && item.getExtendToShipMethods().contains(getShippingMethod())) {
			return true;
		}
		
		return skipShipping;
	}
	
	@SuppressWarnings("rawtypes")
	protected double getAmount(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
		
		double amount = 0.0;
		HardgoodShippingGroup hardgoodShip = null;
		String shipmentState = null;
		
		if(pShippingGroup instanceof HardgoodShippingGroup){
			hardgoodShip = (HardgoodShippingGroup) pShippingGroup;
		}
		if(hardgoodShip != null){
			if(hardgoodShip.getShippingAddress() != null){
				shipmentState = hardgoodShip.getShippingAddress().getState();
				vlogDebug("Shipment state :: " + shipmentState);
			}
		}
		// get the range amount from the super method
		amount = super.getAmount(pOrder, pPriceQuote, pShippingGroup, pPricingModel, pLocale, pProfile, pExtraParameters);
		
		// check for Alaska/Hawaii states
		if(!StringUtils.isBlank(shipmentState) && !getSecondDayShipStates().isEmpty() &&
				getSecondDayShipStates().contains(shipmentState)){
			amount = amount + getAlaskaShipAmount();
		} 
		vlogDebug("Shipping amount :: " + amount);
		
		// check for GiftCardItems
		List relationships = pShippingGroup.getCommerceItemRelationships();
		if (relationships != null) {
			int num = relationships.size();
			CommerceItem item = null;
			int gcItemCount = 0;
			for (int c = 0; c < num; ++c) {
				CommerceItemRelationship relationship = (CommerceItemRelationship) relationships.get(c);
				
				if (relationship != null) {
					item = relationship.getCommerceItem();
					if (item != null) {
					if(((MFFCommerceItemImpl)item).isGiftCard()){
					    gcItemCount++;
						}
					}
				}
			}
			//if giftcard only order, then apply giftcard shipping charges only
			//if order contains gifcard & other items add giftcard shipping charges to existing charges
			if(num >0 && gcItemCount == num){
			  amount = getGiftCardShipAmount();
			}else if(gcItemCount >0){
			  amount = amount + getGiftCardShipAmount();  
			}
		}
		return amount;
	}
}
