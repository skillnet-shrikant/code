package com.mff.commerce.csr.returns;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.csr.returns.ReturnTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.Order;
import atg.commerce.order.PropertyNameConstants;
import atg.repository.RepositoryItem;

public class MFFReturnTools extends ReturnTools{
  
  private List<String> returnableItemStates;
  public static final String GIFT_CARD_REFUND_TYPE = "giftCard";
  
  @Override
  public boolean isItemReturnable(CommerceItem pCommerceItem){
    
    if(pCommerceItem instanceof CommerceItemImpl){
      if(getReturnableItemStates() != null && 
          getReturnableItemStates().contains(((CommerceItemImpl)pCommerceItem).getStateAsString())){
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public String getItemReturnableState(RepositoryItem pCommerceItem){
    String state = (String)pCommerceItem.getPropertyValue(PropertyNameConstants.STATE);
    if(!Strings.isNullOrEmpty(state)){
      if(getReturnableItemStates() != null &&  getReturnableItemStates().contains(state)){
        return "ITEM_RETURNABLE";
      }
    }
    
    return "ITEM_NON_RETURNABLE";
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public Map addWorkingOrderPricingParams(ReturnRequest pReturnRequest, Order pWorkingOrder, Map pExtraParameterMap){
    Map params = addDisableGWPProcessParameter(pExtraParameterMap);
    
    return super.addWorkingOrderPricingParams(pReturnRequest, pWorkingOrder, params);
  }

  public List<String> getReturnableItemStates() {
    return returnableItemStates;
  }

  public void setReturnableItemStates(List<String> returnableItemStates) {
    this.returnableItemStates = returnableItemStates;
  }
  
  
    

}
