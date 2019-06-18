package com.mff.commerce.pricing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.commerce.CommerceException;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.Qualifier;
import atg.commerce.pricing.definition.DiscountStructureElem;
import atg.commerce.pricing.definition.MatchingObject;
import atg.commerce.pricing.definition.PricingModelElem;
import atg.repository.RepositoryException;

/**
 * @author Manoj
 */
public class MFFQualifierService extends Qualifier {
	boolean includePartialQualifierFix;
	public boolean isIncludePartialQualifierFix() {
		return includePartialQualifierFix;
	}

	public void setIncludePartialQualifierFix(boolean pIncludePartialQualifierFix) {
		includePartialQualifierFix = pIncludePartialQualifierFix;
	}
	boolean ignoreDiscountableForItemShipping;
	
	public boolean isIgnoreDiscountableForItemShipping() {
		return ignoreDiscountableForItemShipping;
	}

	public void setIgnoreDiscountableForItemShipping(boolean pIgnoreDiscountableForItemShipping) {
		ignoreDiscountableForItemShipping = pIgnoreDiscountableForItemShipping;
	}

	@SuppressWarnings("rawtypes")
	public Object evaluateQualifier(PricingContext pPricingContext, Map pExtraParametersMap,
			List pFilteredQualifierItems) throws PricingException {
		vlogDebug("Promotion " + pPricingContext.getPricingModel().getRepositoryId());

		Object promotionQualified = super.evaluateQualifier(pPricingContext, pExtraParametersMap,
				pFilteredQualifierItems);

		if(promotionQualified == null) {
			vlogDebug("Promotion " + pPricingContext.getPricingModel().getRepositoryId() + " evaluated to NULL. Clearing any gwpPromoId flag set on items");
			Iterator<FilteredCommerceItem> fciIter = pFilteredQualifierItems.iterator();
			while(fciIter.hasNext()) {
				FilteredCommerceItem fci = (FilteredCommerceItem) fciIter.next();
				if(!(fci.getWrappedItem() instanceof atg.commerce.pricing.PricingCommerceItem)) {
					MFFCommerceItemImpl mi = (MFFCommerceItemImpl) fci.getWrappedItem();
					vlogDebug("mi.getGWPPromoId is " + mi.getGwpPromoId());
					if(mi.getGwpPromoId() != null && mi.getGwpPromoId().equalsIgnoreCase(pPricingContext.getPricingModel().getRepositoryId())) {
						vlogDebug("mi.getGWPPromoId is cleared id=" + mi.getId() + " filterItems size " + pFilteredQualifierItems.size() + " promoQualifier is null");
						if(isIncludePartialQualifierFix()) {
							if(fci.getPriceQuote().getQuantityAsQualifier() == 0) {
								mi.setGwpPromoId(null);
							}
						} else {
							mi.setGwpPromoId(null);
						}

					}					
				}
			}			
		}
		// 2414 - Track the gwp item promo if the item is a qualifier for the promo
		// MatchingObject will have the qualifier item in it
		if (promotionQualified instanceof Boolean ) {
			if((Boolean) promotionQualified) {
				vlogDebug("Promotion " + pPricingContext.getPricingModel().getRepositoryId() + " evaluated to true");
			} else {
				vlogDebug("Promotion " + pPricingContext.getPricingModel().getRepositoryId() + " evaluated to false. Clearing any gwpPromoId flag set on items");
    			Iterator<FilteredCommerceItem> fciIter = pFilteredQualifierItems.iterator();
    			while(fciIter.hasNext()) {
    				FilteredCommerceItem fci = (FilteredCommerceItem) fciIter.next();
    				if(!(fci.getWrappedItem() instanceof atg.commerce.pricing.PricingCommerceItem)) {
	    				MFFCommerceItemImpl mi = (MFFCommerceItemImpl) fci.getWrappedItem();
	    				vlogDebug("mi.getGWPPromoId is " + mi.getGwpPromoId());
	    				if(mi.getGwpPromoId() != null && mi.getGwpPromoId().equalsIgnoreCase(pPricingContext.getPricingModel().getRepositoryId())) {
	    					vlogDebug("mi.getGWPPromoId is cleared");
	    					mi.setGwpPromoId(null);
	    				}
    				}
    			}
			}
				
			
		} else if (promotionQualified instanceof ArrayList) {
			PricingModelElem model=null;
			DiscountStructureElem dse = null;
    		try {
				model = (PricingModelElem) getPMDLCache().get(pPricingContext.getPricingModel());
			} catch (Exception e) {
				e.printStackTrace();
			}
    		dse = (DiscountStructureElem)model.getOffer().getSubElements()[0];
    		String calcType = dse.getCalculatorType();

    		if(calcType != null && calcType.equalsIgnoreCase("gwp")) {
    			vlogDebug("Promotion {0} evaluated to true. Clearing previously qualified items", pPricingContext.getPricingModel().getRepositoryId());
    			
    			Iterator<FilteredCommerceItem> fciIter = pFilteredQualifierItems.iterator();
    			while(fciIter.hasNext()) {
    				FilteredCommerceItem fci = (FilteredCommerceItem) fciIter.next();
    				MFFCommerceItemImpl mi = (MFFCommerceItemImpl) fci.getWrappedItem();
    				if(mi.getGwpPromoId() != null && mi.getGwpPromoId().equalsIgnoreCase(pPricingContext.getPricingModel().getRepositoryId())) {
    					mi.setGwpPromoId(null);
    				}
    			}
    			
    			ArrayList ar = (ArrayList) promotionQualified;
    			Iterator<MatchingObject> iter = ar.iterator();
    			while(iter.hasNext()) {
    				MatchingObject mo = iter.next();
    				FilteredCommerceItem fci = (FilteredCommerceItem) mo.getMatchingObject();
    				MFFCommerceItemImpl mi = (MFFCommerceItemImpl) fci.getWrappedItem();
    				vlogDebug("Setting gwpPromoId for item " + mi.getId() + " with value " + pPricingContext.getPricingModel().getRepositoryId());
    				mi.setGwpPromoId(pPricingContext.getPricingModel().getRepositoryId());
    			}
    		}
		}
		return promotionQualified;
	}

	@Override
	protected Object evaluateTarget(PricingContext pPricingContext, Map pExtraParametersMap, List pFilteredTargetItems)
			throws PricingException {
		if(isIgnoreDiscountableForItemShipping()) {
			
			try {
				if(isLoggingDebug()) {
					logDebug("Pricing Model in evaluateTarget is " + pPricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName());
				}
				if(pPricingContext.getPricingModel() != null && pPricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName().equalsIgnoreCase("freeItemShipping")) {
					setItemOveride(true, pFilteredTargetItems);
				} else {
					setItemOveride(false, pFilteredTargetItems);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return super.evaluateTarget(pPricingContext, pExtraParametersMap, pFilteredTargetItems);
	}

	@Override
	protected void filterItemsForTarget(PricingContext pPricingContext, Map pExtraParametersMap,
			Map pDetailsPendingActingAsQualifier, Map pDetailsRangesToReceiveDiscount, List pFilteredTargetItems)
			throws PricingException {
		if(isIgnoreDiscountableForItemShipping()) {
			try {
				if(isLoggingDebug()) {
					logDebug("Pricing Model in filterItemsForTarget is " + pPricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName());
				}
				if(pPricingContext.getPricingModel() != null && pPricingContext.getPricingModel().getItemDescriptor().getItemDescriptorName().equalsIgnoreCase("freeItemShipping")) {
					setItemOveride(true, pFilteredTargetItems);
				} else {
					setItemOveride(false, pFilteredTargetItems);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		super.filterItemsForTarget(pPricingContext, pExtraParametersMap, pDetailsPendingActingAsQualifier,
				pDetailsRangesToReceiveDiscount, pFilteredTargetItems);
	}

	public void setItemOveride(boolean pOverride, List pFilteredTargetItems) {
		if(pFilteredTargetItems != null) {
			Iterator<FilteredCommerceItem> fciIter = pFilteredTargetItems.iterator();
			while(fciIter.hasNext()) {
				FilteredCommerceItem fci = (FilteredCommerceItem) fciIter.next();
				if(!(fci.getWrappedItem() instanceof atg.commerce.pricing.PricingCommerceItem)) {
				MFFCommerceItemImpl mi = (MFFCommerceItemImpl) fci.getWrappedItem();
				mi.setOverrideDiscountable(pOverride);
				}
			}
		} else {
			vlogDebug("FilteredTargetItems is null");
		}
	}
	@Override
	public boolean isDiscountableItem(FilteredCommerceItem pItem) throws CommerceException {
		if(isIgnoreDiscountableForItemShipping()) {
			Object wrappedItem = pItem.getWrappedItem();
			if(wrappedItem != null && wrappedItem instanceof MFFCommerceItemImpl) {
				MFFCommerceItemImpl mi = (MFFCommerceItemImpl) wrappedItem;
				if(mi.isOverrideDiscountable()) {
					pItem.getPriceInfo().setDiscountable(true);
					return true;
				} else {
					return super.isDiscountableItem(pItem);
				}				
			} else {
				return super.isDiscountableItem(pItem);
			}

		} else {
			return super.isDiscountableItem(pItem);
		}
	}
}
