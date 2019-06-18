package com.mff.commerce.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.inventory.FFInventoryManager;
import com.mff.commerce.inventory.StockLevel;

import atg.commerce.inventory.InventoryException;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet returns the inventory for a sku.
 * 
 */
public class MFFInventoryDroplet extends DynamoServlet {

  // Input Parameters
  private static final ParameterName PRODUCT_ID = ParameterName.getParameterName("prodId");

  // Output Parameters
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");
  public static final ParameterName EMPTY = ParameterName.getParameterName("empty");
  private static final String STOCKLEVEL_FOR_SKUS_OF_PRODUCT = "stockLevelForSkusOfProduct";

  private static final String ELEMENT = "element";

  private FFInventoryManager inventoryManager;
  private MFFCatalogTools mCatalogTools;

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Started MFFInventoryDroplet.service..");
    // Get the skuId from the input parameter
    String prodId = ((String) pRequest.getParameter(PRODUCT_ID));
    if (isLoggingDebug()) logDebug("prodId = " + ((prodId != null) ? prodId : "prodId is null"));
    if (StringUtils.isBlank(prodId)) {
      pRequest.setParameter(ELEMENT, "Product id not found");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
    prodId = prodId.trim();
    boolean isGCProd = false;
    isGCProd = getCatalogTools().isGCProduct(prodId);

    // skip inventory check
    if (isGCProd) {
      pRequest.setParameter(ELEMENT, false);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      return;
    }

    Map<String, StockLevel> skusStockLevel = new HashMap<String, StockLevel>();
    List<RepositoryItem> skus = new ArrayList<RepositoryItem>();
    List<String> skuIds = new ArrayList<String>();
    boolean isAnySkuInStock = false;
    boolean skuAvailableForBopisOnly = false;
    try {
      // get product
      RepositoryItem lProdItem = getCatalogTools().findProduct(prodId);

      if (lProdItem == null) {
        pRequest.setParameter(ELEMENT, false);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        vlogError("Product with Id :{0} not found in the catalog", prodId);
        return;
      }

      // get child skus of the product
      skus = (List<RepositoryItem>) lProdItem.getPropertyValue("childSkus");

      if (skus == null) {
        pRequest.setParameter(ELEMENT, false);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        return;
      }

      for (RepositoryItem sku : skus) {
        if (sku != null) {
          skuIds.add(sku.getRepositoryId());
        } else {
          vlogWarning("One of the chiuld skus of product:{0} is not found in catalog", prodId);
        }
      }

      // 2. Call inventory for child skus of the product
      skusStockLevel = getInventoryManager().querySkusStockLevel(skuIds, true);

      // check if at least one of the sku of the product is in stock,
      // if yes then set the availability to true
      if (skusStockLevel != null) {
        for (String skuId : skusStockLevel.keySet()) {
          StockLevel stockLevel = skusStockLevel.get(skuId);
          if (stockLevel.getStockLevel() > 0 && !stockLevel.isStoreAvailability()) {
            vlogDebug("sku is in stock, marking product:{0} is available", prodId);
            isAnySkuInStock = true;
            skuAvailableForBopisOnly = false;
            break;
          } else if(stockLevel.isStoreAvailability()) {
        	  skuAvailableForBopisOnly = true;
          }
        }
      }

    } catch (RepositoryException e) {
      vlogError(e, "There is an issue while finding childSkus of product:{0}", prodId);
      pRequest.setParameter(ELEMENT, "There is an error occurred while getting availability");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      
      return;
    } catch (InventoryException ie) {
      vlogError(ie, "Inventory Exception occurred while getting availability of product:{0}", prodId);
      pRequest.setParameter(ELEMENT, "There is an error occurred while getting availability");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }

    pRequest.setParameter(STOCKLEVEL_FOR_SKUS_OF_PRODUCT, skusStockLevel);
    //set item out of stock (inverse of isAnySkuInStock)
    pRequest.setParameter(ELEMENT, !isAnySkuInStock);
    pRequest.setParameter("bopisOnlyAvailable", skuAvailableForBopisOnly);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }

  /**
   * @return the inventoryManager
   */
  public FFInventoryManager getInventoryManager() {
    return inventoryManager;
  }

  /**
   * @param inventoryManager
   *          the inventoryManager to set
   */
  public void setInventoryManager(FFInventoryManager inventoryManager) {
    this.inventoryManager = inventoryManager;
  }

  public MFFCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  public void setCatalogTools(MFFCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

}
