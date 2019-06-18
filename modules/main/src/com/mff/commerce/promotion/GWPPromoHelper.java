package com.mff.commerce.promotion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingAdjustment;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

public class GWPPromoHelper extends GenericService{

	/**
	 * Computes non-gwp item discounts
	 * 
	 * @param pItem
	 * @return
	 */
	public double getNonGWPDiscountValue (MFFCommerceItemImpl pItem) {
	    double discountValue=0.0;
	    
		if(!(Boolean)pItem.getPropertyValue("gwp")) {
	    	// get discount amount
			List adjustments = pItem.getPriceInfo().getAdjustments();
			for (Object adj : adjustments) {
				PricingAdjustment adjustment = null;
				if (adj instanceof PricingAdjustment) {
					adjustment = (PricingAdjustment) adj;
					if(adjustment.getAdjustmentDescription().equalsIgnoreCase("Item Discount")) {
						discountValue += -1*adjustment.getTotalAdjustment();
					}
				}
			}    	
	    }
		return discountValue;
	}
	public ArrayList<MFFCommerceItemImpl> getGWPItems(Order pOrder) {
		ArrayList<MFFCommerceItemImpl> gwpItems = new ArrayList<MFFCommerceItemImpl>();
		
		Iterator itemIter = pOrder.getCommerceItems().iterator();
		
		while(itemIter.hasNext()) {
			MFFCommerceItemImpl item = (MFFCommerceItemImpl)itemIter.next();
			
			if(item.isGwp()) {
				gwpItems.add(item);
			}
			
		}
		return gwpItems;
	}
	
	/*
	 * Returns a list of commerce items that are qualifiers
	 * for promotionId passed in
	 * 
	 */
	public ArrayList<MFFCommerceItemImpl> getQualifiersForGWP(Order pOrder, String pPromoId, boolean bAllQualifiers) {
		ArrayList<MFFCommerceItemImpl> qualifierItems = new ArrayList<MFFCommerceItemImpl>();
		ArrayList<MFFCommerceItemImpl> allItems = new ArrayList<MFFCommerceItemImpl>();
		
		Iterator itemIter = pOrder.getCommerceItems().iterator();
		
		while(itemIter.hasNext()) {
			MFFCommerceItemImpl item = (MFFCommerceItemImpl)itemIter.next();
			
			// we need to evaluate only items that are not GWPs
			
			if(!item.isGwp() && item.getGwpPromoId() != null && item.getGwpPromoId().equalsIgnoreCase(pPromoId)) {
				qualifierItems.add(item);
			}
			if(!item.isGwp()) {
				allItems.add(item);
			}
			
		}

/*		if(bAllQualifiers || qualifierItems.isEmpty()) {
			return allItems;
		} else {
			return qualifierItems;
		}*/
		vlogInfo("boolean flag is {0}", bAllQualifiers);
		if(!qualifierItems.isEmpty() && !bAllQualifiers){
			vlogInfo("Returning just the qualifiers Qualifier size = {0} boolean flag is {1}", qualifierItems.size(), bAllQualifiers);
			return qualifierItems;
		} else {
			vlogInfo("Returning ALL qualifiers Qualifier size = {0} boolean flag is {1}", allItems.size(), bAllQualifiers);
			return allItems;
		}

	}
	
	public double getGiftValue(CommerceItemImpl pGiftItem) {
		double giftValue = 0.0;
		
		// loop thru the item's adjustments to find the giftValue
		// The adjustment should have an "Item Discount" adjustment
		// The totalAdjustment value will be the giftValue
		
		List<PricingAdjustment> adjustments = pGiftItem.getPriceInfo().getAdjustments();
		
		for (PricingAdjustment adj : adjustments) {
			
			if(adj != null && adj.getAdjustmentDescription() != null && adj.getAdjustmentDescription().equalsIgnoreCase("Item Discount")) {
				giftValue += adj.getTotalAdjustment();
			}

		}
		return -1 * giftValue;
	}
	
	public String getGiftPromoId (CommerceItemImpl pGiftItem) {
		String promoId = null;

		List<PricingAdjustment> adjustments = pGiftItem.getPriceInfo().getAdjustments();
		
		for (PricingAdjustment adj : adjustments) {
			
			if(adj != null && adj.getAdjustmentDescription() != null && adj.getAdjustmentDescription().equalsIgnoreCase("Item Discount")) {
				if (adj.getPricingModel() != null) {
					return adj.getPricingModel().getRepositoryId();
				}
			}

		}
		return promoId;
	}
	
	public RepositoryItem getGiftPromo (CommerceItemImpl pGiftItem) {
		RepositoryItem promo = null;

		List<PricingAdjustment> adjustments = pGiftItem.getPriceInfo().getAdjustments();
		
		for (PricingAdjustment adj : adjustments) {
			
			if(adj != null && adj.getAdjustmentDescription() != null && adj.getAdjustmentDescription().equalsIgnoreCase("Item Discount")) {
				if (adj.getPricingModel() != null) {
					return adj.getPricingModel();
				}
			}

		}
		return promo;
	}	
	
	public HashMap<Integer, Double> prorateOnQuantity(double totalAmount, double pQuantityToSplit) {

		vlogDebug("prorateOnQuantity : totalAmount - {0},pQuantityToSplit - {1} ", totalAmount, pQuantityToSplit);

		if (Double.isNaN(totalAmount)) {
			totalAmount = 0.0d;
		}

		HashMap<Integer, Double> proratedAmounts = new HashMap<Integer, Double>();
		BigDecimal runningAmount = new BigDecimal(totalAmount);
		BigDecimal quantity = new BigDecimal(pQuantityToSplit);

		if (pQuantityToSplit == 1) {
			proratedAmounts.put(1, totalAmount);
			return proratedAmounts;
		}

		for (int i = 1; i <= pQuantityToSplit; i++) {

			BigDecimal price = runningAmount.divide(quantity, 2, RoundingMode.HALF_UP);
			proratedAmounts.put(i, price.doubleValue());

			runningAmount = runningAmount.subtract(price);
			quantity = quantity.subtract(BigDecimal.ONE);

		}

		vlogDebug("prorateOnQuantity : proratedAmounts - {0}", proratedAmounts);
		return proratedAmounts;
	}	
}
