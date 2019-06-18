package com.mff.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.csr.returns.ReturnItem;
import atg.commerce.csr.returns.ReturnRequest;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class ReturnCommerceItemsCummulationDroplet extends CommerceItemsCummulationDroplet {
  
  public static final ParameterName PARAM_RETURN_REQUEST = ParameterName.getParameterName("returnRequest");
  
  @SuppressWarnings({ "unchecked", "unused" })
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pRequest.getObjectParameter(PARAM_ORDER);
    ReturnRequest lReturnRequest =  (ReturnRequest) pRequest.getObjectParameter(PARAM_RETURN_REQUEST);
    Map<String,CommerceItemWithQuantityVO> lCommerceItemMap = new HashMap<String,CommerceItemWithQuantityVO>();
    
    vlogDebug("ReturnCommerceItemsCummulationDroplet : returnItemCount - {0}", lReturnRequest.getReturnItemCount());
    
    if(lReturnRequest != null && lReturnRequest.getReturnItemList() != null && lReturnRequest.getReturnItemList().size() > 0){
      // Get list of returned items
      List<ReturnItem> lReturnItems = lReturnRequest.getReturnItemList();
      
      for (ReturnItem lReturnItem : lReturnItems) {
        MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) lReturnItem.getCommerceItem();
        String lSkuId = lCommerceItem.getCatalogRefId();
        if(lCommerceItemMap.get(lSkuId) == null) {
          lCommerceItemMap.put(lSkuId, new CommerceItemWithQuantityVO(lCommerceItem, lReturnItem.getQuantityToReturn()));
        } else {
          lCommerceItemMap.get(lSkuId).setQuantity(lCommerceItemMap.get(lSkuId).getQuantity() + lReturnItem.getQuantityToReturn());
        }
      }
      
      pRequest.setParameter(COMMERCEITEMS_SET,lCommerceItemMap.values());
      pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
      
    }else{
      super.service(pRequest, pResponse);
    }
  }
  
}

