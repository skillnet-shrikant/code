package com.mff.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CommerceItemsCummulationDroplet extends DynamoServlet {
  
  /* INPUT PARAMETERS */
  public static final ParameterName PARAM_ORDER = ParameterName.getParameterName("order");
  
  public static final String COMMERCEITEMS_SET = "commerceItemSet";

  /* OPEN PARAMETERS */
  public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
  public static final ParameterName OPARAM_ERROR = ParameterName.getParameterName("error");
  
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pRequest.getObjectParameter(PARAM_ORDER);
    Map<String,CommerceItemWithQuantityVO> lCommerceItemMap = new HashMap<String,CommerceItemWithQuantityVO>();
    for(Object lCommerceItemObj : lOrder.getCommerceItems()) {
      MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) lCommerceItemObj;
      if(lCommerceItemMap.get(lCommerceItem.getCatalogRefId()) == null) {
        lCommerceItemMap.put(lCommerceItem.getCatalogRefId(), new CommerceItemWithQuantityVO(lCommerceItem, lCommerceItem.getQuantity()));
      } else {
        lCommerceItemMap.get(lCommerceItem.getCatalogRefId()).setQuantity(lCommerceItemMap.get(lCommerceItem.getCatalogRefId()).getQuantity() + lCommerceItem.getQuantity());
      }
    }
    pRequest.setParameter(COMMERCEITEMS_SET,lCommerceItemMap.values());
    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }
  
  public class CommerceItemWithQuantityVO{
    
    private MFFCommerceItemImpl mCommerceItem;
    private long mQuantity;
    
    public CommerceItemWithQuantityVO(MFFCommerceItemImpl pCommerceItem, long pQuantity) {
      mCommerceItem = pCommerceItem;
      mQuantity = pQuantity;
    }
    
    public MFFCommerceItemImpl getCommerceItem() {
      return mCommerceItem;
    }
    public void setCommerceItem(MFFCommerceItemImpl pCommerceItem) {
      mCommerceItem = pCommerceItem;
    }
    public long getQuantity() {
      return mQuantity;
    }
    public void setQuantity(long pQuantity) {
      mQuantity = pQuantity;
    }
    
  }
}

