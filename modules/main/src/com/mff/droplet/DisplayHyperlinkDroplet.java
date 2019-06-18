package com.mff.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class DisplayHyperlinkDroplet extends DynamoServlet {

  /* INPUT PARAMETERS */
  public static final ParameterName PARAM_PRODUCT_ID = ParameterName.getParameterName("productId");
  public static final ParameterName PARAM_SKU_ID = ParameterName.getParameterName("skuId");
  /* OPEN PARAMETERS */
  public static final ParameterName OPARAM_TRUE = ParameterName.getParameterName("true");
  public static final ParameterName OPARAM_FALSE = ParameterName.getParameterName("false");
  
  List <String> mRestrictedProductIdsList;
  List <String> mRestrictedSkuIdsList;
  
  
  public List<String> getRestrictedProductIdsList() {
    return mRestrictedProductIdsList;
  }

  public void setRestrictedProductIdsList(List<String> pRestrictedProductIdsList) {
    mRestrictedProductIdsList = pRestrictedProductIdsList;
  }

  public List<String> getRestrictedSkuIdsList() {
    return mRestrictedSkuIdsList;
  }

  public void setRestrictedSkuIdsList(List<String> pRestrictedSkuIdsList) {
    mRestrictedSkuIdsList = pRestrictedSkuIdsList;
  }

  
  
  @Override
  public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    String lProductId = pRequest.getParameter(PARAM_PRODUCT_ID);
    String lSkuId = pRequest.getParameter(PARAM_SKU_ID);
    vlogDebug("Product Id [{0}] & Sku Id [{1}]", lProductId,lSkuId);
    if(getRestrictedProductIdsList().contains(lProductId) && getRestrictedSkuIdsList().contains(lSkuId)) {
      pRequest.serviceLocalParameter(OPARAM_FALSE, pRequest, pResponse);
    }else {
      pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);
    }
  }
}
