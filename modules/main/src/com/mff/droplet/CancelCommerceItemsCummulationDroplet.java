package com.mff.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class CancelCommerceItemsCummulationDroplet extends CommerceItemsCummulationDroplet {
  
  public static final String CANCELLED_ITEMS_SET = "cancelledItemSet";
  
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pRequest.getObjectParameter(PARAM_ORDER);
    Map<String,CommerceItemWithQuantityVO> lCommerceItemMap = new HashMap<String,CommerceItemWithQuantityVO>();
    Map<String,CommerceItemWithQuantityVO> lCancelledItemMap = new HashMap<String,CommerceItemWithQuantityVO>();
    
    for(Object lCommerceItemObj : lOrder.getCommerceItems()) {
      MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) lCommerceItemObj;
      String lSkuId = lCommerceItem.getCatalogRefId();
      if(lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.CANCELLED)){
        //Map of cancelled items
        if(lCancelledItemMap.get(lSkuId) == null) {
          lCancelledItemMap.put(lSkuId, new CommerceItemWithQuantityVO(lCommerceItem, lCommerceItem.getQuantity()));
        } else {
          lCancelledItemMap.get(lSkuId).setQuantity(lCancelledItemMap.get(lSkuId).getQuantity() + lCommerceItem.getQuantity());
        }
      }else{
        if(lCommerceItemMap.get(lSkuId) == null) {
          lCommerceItemMap.put(lSkuId, new CommerceItemWithQuantityVO(lCommerceItem, lCommerceItem.getQuantity()));
        } else {
          lCommerceItemMap.get(lSkuId).setQuantity(lCommerceItemMap.get(lSkuId).getQuantity() + lCommerceItem.getQuantity());
        }
      }
    }
    pRequest.setParameter(COMMERCEITEMS_SET,lCommerceItemMap.values());
    pRequest.setParameter(CANCELLED_ITEMS_SET,lCancelledItemMap.values());
    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }
  
  
}

