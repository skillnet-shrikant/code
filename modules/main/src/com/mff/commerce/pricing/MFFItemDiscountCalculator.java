package com.mff.commerce.pricing;

import atg.adapter.gsa.GSAItemDescriptor;
import atg.beans.DynamicBeans;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemDiscountCalculator;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.definition.DiscountStructure;
import atg.commerce.pricing.definition.MatchingObject;
import atg.commerce.promotion.GWPManager;
import atg.commerce.promotion.GWPMarkerManager;
import atg.commerce.promotion.PromotionAnalysisManager;
import atg.commerce.promotion.PromotionAnalysisManager.ItemDetails;
import atg.commerce.promotion.PromotionAnalysisModelHolder;
import atg.commerce.promotion.PromotionConstants;
import atg.commerce.promotion.PromotionException;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.Range;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.service.cache.AbstractCache;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.web.messaging.UserMessage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

public class MFFItemDiscountCalculator extends ItemDiscountCalculator {
  protected Collection findQualifyingItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, RepositoryItem pProfile, Locale pLocale, Order pOrder, Map pExtraParameters)
      throws PricingException
    {
     Collection qualifyingObjects = super.findQualifyingItems(pPriceQuotes, pItems, pPricingModel, pProfile, pLocale, pOrder, pExtraParameters);
     if(qualifyingObjects !=null && qualifyingObjects.size() > 0){
      Iterator iter =  qualifyingObjects.iterator();
     
     }
     
     return qualifyingObjects;
    }

  @Override
  public void priceItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale,
      RepositoryItem pProfile, Order pOrder, Map pExtraParameters) throws PricingException {
    // TODO Auto-generated method stub
    super.priceItems(pPriceQuotes, pItems, pPricingModel, pLocale, pProfile, pOrder, pExtraParameters);
  }
}
