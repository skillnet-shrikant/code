package com.mff.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;

import atg.commerce.order.CommerceItem;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class ShippedCommerceItemsCummulationDroplet extends CommerceItemsCummulationDroplet {
  
  public static final ParameterName PARAM_ITEM_IDS = ParameterName.getParameterName("itemsToShip");
  
  @SuppressWarnings("unchecked")
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pRequest.getObjectParameter(PARAM_ORDER);
    List<CommerceItem> itemsToShip =  (List<CommerceItem>) pRequest.getObjectParameter(PARAM_ITEM_IDS);
    Map<String,CommerceItemWithQuantityVO> lCommerceItemMap = new HashMap<String,CommerceItemWithQuantityVO>();
    vlogDebug("ShippedCommerceItemsCummulationDroplet : itemsToShip - {0}", itemsToShip);
    
    if(itemsToShip != null && itemsToShip.size() > 0){
      for(CommerceItem lCommerceItemObj : itemsToShip) {
        MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) lCommerceItemObj;
        String lSkuId = lCommerceItem.getCatalogRefId();
        if(lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.SHIPPED)){
          if(lCommerceItemMap.get(lSkuId) == null) {
            lCommerceItemMap.put(lSkuId, new CommerceItemWithQuantityVO(lCommerceItem, lCommerceItem.getQuantity()));
          } else {
            lCommerceItemMap.get(lSkuId).setQuantity(lCommerceItemMap.get(lSkuId).getQuantity() + lCommerceItem.getQuantity());
          }
        }
      }
      
    }else{
      for(Object lCommerceItemObj : lOrder.getCommerceItems()) {
        MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) lCommerceItemObj;
        String lSkuId = lCommerceItem.getCatalogRefId();
        if(lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.SHIPPED)){
          if(lCommerceItemMap.get(lSkuId) == null) {
            lCommerceItemMap.put(lSkuId, new CommerceItemWithQuantityVO(lCommerceItem, lCommerceItem.getQuantity()));
          } else {
            lCommerceItemMap.get(lSkuId).setQuantity(lCommerceItemMap.get(lSkuId).getQuantity() + lCommerceItem.getQuantity());
          }
        }
      }
    }
    pRequest.setParameter(COMMERCEITEMS_SET,lCommerceItemMap.values());
    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }
  
}

