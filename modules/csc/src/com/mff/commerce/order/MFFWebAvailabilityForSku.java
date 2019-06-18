package com.mff.commerce.order;

import java.io.IOException;

import javax.servlet.ServletException;

import com.mff.commerce.inventory.FFInventoryManager;
import com.mff.commerce.inventory.StockLevel;

import atg.commerce.inventory.InventoryException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet returns the online stock level for a sku.
 * 
 */
public class MFFWebAvailabilityForSku extends DynamoServlet {

  // Input Parameters
  private static final ParameterName SKU = ParameterName.getParameterName("skuid");

  // Output Parameters
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");
  public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

  // List of stores and inventory or error messages will be set in the ELEMENTS param
  private static final String ELEMENTS = "elements";
  
  private FFInventoryManager inventoryManager;

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Started MFFWebAvailabilityForSku.service..");

    // Get the skuId from the input parameter
    String skuId = (String) pRequest.getParameter(SKU);
    if (isLoggingDebug()) logDebug("skuId = " + ((skuId != null) ? skuId : "sku id is null"));
    if (null == skuId) {
      pRequest.setParameter(ELEMENTS, "sku id not found");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }

    StockLevel skuAvailability = new StockLevel();
    try 
    {
      skuAvailability = getInventoryManager().querySkuStockLevel(skuId);
    } 
    catch (InventoryException ie) 
    {
      vlogError(ie, "Inventory Exception occurred while getting sku stock level");
      pRequest.setParameter(ELEMENTS, "Inventory Exception occurred while getting sku stock level");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
    
    if (null!=skuAvailability)
    {
      pRequest.setParameter(ELEMENTS, skuAvailability);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
  }

  /**
   * @return the inventoryManager
   */
  public FFInventoryManager getInventoryManager() {
    return inventoryManager;
  }

  /**
   * @param inventoryManager the inventoryManager to set
   */
  public void setInventoryManager(FFInventoryManager inventoryManager) {
    this.inventoryManager = inventoryManager;
  }

}
