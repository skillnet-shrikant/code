package com.mff.commerce.pricing;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.Order;
import atg.commerce.pricing.GWPPriceCalculator;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.QualifiedItem;
import atg.repository.RepositoryItem;

public class MFFGWPPriceCalculator extends GWPPriceCalculator {

  @SuppressWarnings("rawtypes")
  @Override
  protected void priceQualifyingItem(QualifiedItem pQualifiedItem, List pPriceQuotes, List pItems, RepositoryItem pPricingModel, RepositoryItem pProfile, Locale pLocale, Order pOrder, Map pExtraParameters) throws PricingException {
	  super.priceQualifyingItem(pQualifiedItem, pPriceQuotes, pItems, pPricingModel, pProfile, pLocale, pOrder, pExtraParameters);
  }  
}
