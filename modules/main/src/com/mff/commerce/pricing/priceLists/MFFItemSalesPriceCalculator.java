package com.mff.commerce.pricing.priceLists;

import java.util.Locale;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.pricing.MFFItemPriceInfo;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.ItemSalesPriceCalculator;
import atg.repository.RepositoryItem;

public class MFFItemSalesPriceCalculator extends ItemSalesPriceCalculator {

  
  @SuppressWarnings("rawtypes")
  @Override
  public void priceItem(RepositoryItem pPrice, ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
    
    vlogDebug("Entering priceItem : pPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters");
    if(pItem instanceof MFFCommerceItemImpl) {
    	MFFCommerceItemImpl item=(MFFCommerceItemImpl)pItem;
    	item.setFreeFreightShipping(false);
    }
        
    super.priceItem(pPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
    
    if (pPriceQuote.isOnSale()) {
      if (pPriceQuote instanceof MFFItemPriceInfo) {
        MFFItemPriceInfo lPriceInfo = (MFFItemPriceInfo) pPriceQuote;
        lPriceInfo.setSalePricePromoId((String)pPrice.getPropertyValue("promoId"));
        if (isLoggingDebug()) vlogDebug("Price info has been set {0}", lPriceInfo.getSalePricePromoId());
      }
    }
    vlogDebug("Exiting priceItem : pPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters");
  }

}
