package com.mff.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import com.mff.commerce.inventory.FFInventoryManager;
import com.mff.commerce.inventory.StockLevel;

import atg.commerce.inventory.InventoryException;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class MoreStylesAvailableDroplet extends DynamoServlet {
  
  /* INPUT PARAMETERS */
  public static final ParameterName PARAM_PRODUCT = ParameterName.getParameterName("product");
  /* OPEN PARAMETERS */
  public static final ParameterName OPARAM_TRUE = ParameterName.getParameterName("true");
  public static final ParameterName OPARAM_FALSE = ParameterName.getParameterName("false");
  
  private FFInventoryManager inventoryManager;
  
  public FFInventoryManager getInventoryManager() {
    return inventoryManager;
  }
 
  public void setInventoryManager(FFInventoryManager inventoryManager) {
    this.inventoryManager = inventoryManager;
  }
  
  @Override
  public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    RepositoryItem lProductRepoItem = (RepositoryItem) pRequest.getObjectParameter(PARAM_PRODUCT);
    List<RepositoryItem> lSkusList = (List<RepositoryItem>) lProductRepoItem.getPropertyValue("childSKUs");
    Set<String> lColorSet=new HashSet<String>();
    try {
      if(lSkusList != null && lSkusList.size() > 1) {
        
        List<String> skuIds = new ArrayList<String>();
        for (RepositoryItem sku : lSkusList) {
            skuIds.add(sku.getRepositoryId());
        }
        Map<String, StockLevel> skusStockLevel = getInventoryManager().querySkusStockLevel(skuIds, true);
        for(RepositoryItem lSkuItem : lSkusList) {
          if(skusStockLevel.get(lSkuItem.getRepositoryId())!=null && skusStockLevel.get(lSkuItem.getRepositoryId()).getStockLevel()>0l) {
            Map<String,String>lSkuAttributes = (Map<String, String>) lSkuItem.getPropertyValue("skuAttributes");
            if(StringUtils.isNotBlank(lSkuAttributes.get("Color"))) {
              lColorSet.add(lSkuAttributes.get("Color"));
            }
          }
        }
      }
      
    } catch (InventoryException e) {
      vlogError(e, "Error quering inventory");
    }
    if(lColorSet.size()>1) {
      pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);
    }else {
      pRequest.serviceLocalParameter(OPARAM_FALSE, pRequest, pResponse);
    }
    
  }

}
