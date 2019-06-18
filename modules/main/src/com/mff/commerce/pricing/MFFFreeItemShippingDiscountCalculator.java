package com.mff.commerce.pricing;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;

import atg.commerce.order.Order;
import atg.commerce.pricing.CalculatorInfo;
import atg.commerce.pricing.CalculatorInfoProvider;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.ItemDiscountCalculator;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.QualifiedItem;
import atg.repository.RepositoryItem;

public class MFFFreeItemShippingDiscountCalculator extends ItemDiscountCalculator implements CalculatorInfoProvider {
	protected Collection findQualifyingItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, RepositoryItem pProfile, Locale pLocale, Order pOrder, Map pExtraParameters)
			throws PricingException
	{
		Collection qualifyingObjects = super.findQualifyingItems(pPriceQuotes, pItems, pPricingModel, pProfile, pLocale, pOrder, pExtraParameters);
		if(qualifyingObjects !=null && qualifyingObjects.size() > 0){
			Iterator iter =  qualifyingObjects.iterator();
			QualifiedItem qItem = null;
			String calcType;
			String discType;
			MFFCommerceItemImpl item=null;
			while(iter.hasNext()) {
				qItem = (QualifiedItem)iter.next();
				item = (MFFCommerceItemImpl) ((FilteredCommerceItem)qItem.getItem()).getWrappedItem();
				calcType=qItem.getDiscount().getCalculatorType();
				discType=qItem.getDiscount().getDiscountType();

				Iterator adjIter = ((FilteredCommerceItem)qItem.getItem()).getPriceQuote().getAdjustments().iterator();
				PricingAdjustment adj=null;
				
				while(adjIter.hasNext()) {
					adj = (PricingAdjustment)adjIter.next();
					if (adj!= null && adj.getAdjustmentDescription().equalsIgnoreCase("Item Free Shipping Discount")) {
						adjIter.remove();
					}
				}
				if (calcType != null && calcType.equalsIgnoreCase("freeItemShipping") && discType != null && discType.equalsIgnoreCase("freeItemShipping")) {
					item = (MFFCommerceItemImpl) ((FilteredCommerceItem)qItem.getItem()).getWrappedItem();
					item.setFreeFreightShipping(true);
					item.setExtendToShipMethods((Set<String>)pPricingModel.getPropertyValue("extendToShipMethods"));
					MFFHardgoodShippingGroup sg = (MFFHardgoodShippingGroup) pOrder.getShippingGroups().get(0);
					String shipMethod = sg.getShippingMethod();
					
					if(shipMethod != null) {
						
						// check if it is part of extend ship methods
						if(shipMethod.equalsIgnoreCase("Standard") || item.getExtendToShipMethods().contains(shipMethod)) {
							PricingAdjustment adjustment = null;
							adjustment = new PricingAdjustment("Item Free Shipping Discount", pPricingModel, 0, item.getQuantity());
							item.getPriceInfo().getAdjustments().add(adjustment);
							((FilteredCommerceItem)qItem.getItem()).getPriceQuote().getAdjustments().add(adjustment);							
						}						
					}
					//((FilteredCommerceItem)qItem.getItem()).getPriceQuote().setDiscounted(true);
				} else {
					item.setFreeFreightShipping(false);
				}
			}

		}
		return qualifyingObjects;
	}

	@Override
	public void priceItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Order pOrder, Map pExtraParameters) throws PricingException {
		// TODO Auto-generated method stub
		findQualifyingItems(pPriceQuotes, pItems, pPricingModel,  pProfile, pLocale,pOrder, pExtraParameters);
	}

	@Override
	protected int getDiscountType(String pDiscountType) {
		// TODO Auto-generated method stub
		return super.getDiscountType(pDiscountType);
	}

	@Override
	protected String getDiscountType(RepositoryItem pPricingModel, Map pExtraParameters) throws PricingException {
		// TODO Auto-generated method stub
		return super.getDiscountType(pPricingModel, pExtraParameters);
	}

	@Override
	public CalculatorInfo getCalculatorInfo() {
		CalculatorInfo calcInfo = new CalculatorInfo("freeItemShipping");
		calcInfo.setDiscountTypes(new String[] { "freeItemShipping"});
		return calcInfo;
	}

}
