package com.mff.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;

import atg.commerce.inventory.InventoryException;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class BopisStoreAvailabilityDroplet extends DynamoServlet {
  
  /* INPUT PARAMETERS */
  public static final ParameterName PARAM_SKU_ID = ParameterName.getParameterName("skuId");
  public static final ParameterName PARAM_PRODUCT_ID = ParameterName.getParameterName("productId");
  public static final ParameterName PARAM_SKU_QUANTITY = ParameterName.getParameterName("quantity");
  public static final ParameterName PARAM_STORE_ID = ParameterName.getParameterName("storeId");


  /* OPEN PARAMETERS */
  public static final ParameterName OPARAM_TRUE = ParameterName.getParameterName("true");
  public static final ParameterName OPARAM_FALSE = ParameterName.getParameterName("false");
  public static final ParameterName OPARAM_ERROR = ParameterName.getParameterName("error");
  
  
  private FFRepositoryInventoryManager mInventoryManager;
  

  public FFRepositoryInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }
  
  @Override
  public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    try {
      String lSkuId = pRequest.getParameter(PARAM_SKU_ID);
      long lQuantity = Long.valueOf(pRequest.getParameter(PARAM_SKU_QUANTITY));
      Map<String,Long> lSkusWithQty = new HashMap<String,Long>();
      String lStoreId = pRequest.getParameter(PARAM_STORE_ID);
      List<String> lStoreIdsList = new ArrayList<String>();
      lStoreIdsList.add(lStoreId);
      if(StringUtils.isNotBlank(lSkuId)) {
    	  lSkusWithQty.put(lSkuId, lQuantity);
	      Map<String, Boolean> lStoresBopisAvlStatus = getInventoryManager().getBopisInventory(lSkusWithQty, lStoreIdsList);
	      if(lStoresBopisAvlStatus.get(lStoreId)!=null && lStoresBopisAvlStatus.get(lStoreId)) {
	        pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);
	      }else {
	        pRequest.serviceLocalParameter(OPARAM_FALSE, pRequest, pResponse);
	      }
      }else {
    	  boolean isInvAvailable = false;
    	  String lProductId = pRequest.getParameter(PARAM_PRODUCT_ID);
    	  RepositoryItem lProductItem = getInventoryManager().getCatalogTools().getCatalogRepository().getItem(lProductId, "product");
    	  if(lProductItem != null) {
	    	  List<RepositoryItem> lSkusList = (List<RepositoryItem>)lProductItem.getPropertyValue("childSKUs");
	    	  for(RepositoryItem lSkuItem : lSkusList) {
	    		  lSkusWithQty = new HashMap<String,Long>();
	    		  lSkusWithQty.put(lSkuItem.getRepositoryId(), lQuantity);
	    		  Map<String, Boolean> lStoresBopisAvlStatus = getInventoryManager().getBopisInventory(lSkusWithQty, lStoreIdsList);
	    		  if(lStoresBopisAvlStatus.get(lStoreId)!=null && lStoresBopisAvlStatus.get(lStoreId)) {
	    			  isInvAvailable = true;
	    			  break;
	    		  }
	    	  }
    	  }
    	  if(isInvAvailable) {
    		  pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);
    	  }else {
    		  pRequest.serviceLocalParameter(OPARAM_FALSE, pRequest, pResponse);
    	  }
    	  
      }
    } catch (InventoryException e) {
    	 vlogError(e, "Unable to get the results Inventory exception "+e.getMessage());
    	 pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
    } catch (Exception e) {
    	 vlogError(e, "Unable to get the results exception "+e.getMessage());
    	 pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
    } 
    
  }

}
