package com.mff.commerce.promotion;

import java.util.Map;

import com.mff.commerce.states.MFFOrderStates;

import atg.commerce.order.Order;
import atg.commerce.pricing.GWPInfo;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.promotion.GWPManager;
import atg.commerce.promotion.GWPMultiHashKey;
import atg.commerce.states.StateDefinitions;
import atg.core.util.ResourceUtils;

public class MFFGWPManager extends GWPManager {

  /* (non-Javadoc)
   * @see atg.commerce.promotion.GWPManager#processGWPInfos(java.util.Map, atg.commerce.pricing.PricingContext, java.util.Map)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public boolean processGWPInfos(Map<GWPMultiHashKey, GWPInfo> pGWPInfos, PricingContext pPricingContext, Map pExtraParameters) throws PricingException {
    Order order;
    if(pPricingContext == null)
    {
        String msg = ResourceUtils.getMsgResource("noPricingContext", "atg.commerce.promotion.PromotionResources", sResourceBundle);
        if(isLoggingError())
            logError(msg);
        throw new PricingException(msg);
    }
    order = pPricingContext.getOrder();
    if(order == null)
    {
        String msg = ResourceUtils.getMsgResource("noOrder", "atg.commerce.promotion.PromotionResources", sResourceBundle);
        if(isLoggingError())
            logError(msg);
        throw new PricingException(msg);
    }
    if(order.getState() == StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.IN_REMORSE))
    {
        if(isLoggingDebug())
            logDebug("processGWPInfos disabled for modifiable order in remorse");
        return false;
    }
    
    return super.processGWPInfos(pGWPInfos, pPricingContext, pExtraParameters);
    //return false;
  }
}
