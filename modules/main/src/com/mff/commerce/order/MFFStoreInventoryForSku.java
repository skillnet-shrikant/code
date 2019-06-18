package com.mff.commerce.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;

import com.mff.commerce.inventory.FFInventoryManager;
import com.mff.commerce.inventory.StoreAvailability;

import atg.commerce.inventory.InventoryException;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet returns the list of stores inventory for a sku.
 * 
 */
public class MFFStoreInventoryForSku extends DynamoServlet {

  // Input Parameters
  private static final ParameterName SKU = ParameterName.getParameterName("skuid");
  private static final ParameterName STORE = ParameterName.getParameterName("storeid");

  // Output Parameters
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");
  public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

  // List of stores and inventory or error messages will be set in the ELEMENTS param
  private static final String ELEMENTS = "elements";
  
  private static final String LOCATION_REPOSITORY_NAME = "location";
  private static final String PROPERTY_STORE_ID = "locationId";
  private FFInventoryManager inventoryManager;
  private Repository locRepository;

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Started MFFStoreInventoryForSku.service..");

    // Get the skuId from the input parameter
    String skuId = (String) pRequest.getParameter(SKU);
    if (isLoggingDebug()) logDebug("skuId = " + ((skuId != null) ? skuId : "sku id is null"));
    if (null == skuId) {
      pRequest.setParameter(ELEMENTS, "sku id not found");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
    
    String storeId = (String) pRequest.getParameter(STORE);
    if (isLoggingDebug()) logDebug("storeId = " + ((storeId != null) ? storeId : "store id is null"));

    List<String> stores = new ArrayList<String>();
    List<StoreAvailability> storeAvailability = new ArrayList<StoreAvailability>();
    
    // 1. Get list of Stores
    if (null == storeId) {
      stores = getStoreIds();
      if (null == stores || stores.size()==0) {
        pRequest.setParameter(ELEMENTS, "stores not found");
        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        return;
      }
    }else{
      stores.add(storeId);
    }
    
    // 2. Call inventory with the sku and stores list
    try 
    {
      storeAvailability = getInventoryManager().queryStoreAvailability(skuId, stores);
    } 
    catch (InventoryException ie) 
    {
      vlogError(ie, "Inventory Exception occurred while getting store availability");
      pRequest.setParameter(ELEMENTS, "Inventory Exception occurred while getting store availability");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }

    if (null!=storeAvailability && storeAvailability.size()>0)
    {
      sortListByStoreId(storeAvailability);
      pRequest.setParameter(ELEMENTS, storeAvailability);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
    else
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
  }
  
  private void sortListByStoreId( List<StoreAvailability> pStoreAvailability){
    
    Collections.sort(pStoreAvailability, new Comparator<StoreAvailability>(){

      @Override
      public int compare(StoreAvailability pObject1, StoreAvailability pObject2) {
        Integer val1 = Integer.parseInt(pObject1.getStoreNo());
        Integer val2 = Integer.parseInt(pObject2.getStoreNo());
        return val1.compareTo(val2);
      }
      
    });
  }

  public List<String> getStoreIds() 
  {
    List<String> stores = new ArrayList<String>();
    RepositoryItem[] storeItems = null;
    try
    {
      RepositoryView view = getLocRepository().getView(LOCATION_REPOSITORY_NAME);
      RqlStatement skuIdRQL = RqlStatement.parseRqlStatement("ALL");
      storeItems = skuIdRQL.executeQuery(view, null);
      
      if(null!=storeItems)
      {
        for (int i = 0; i < storeItems.length; i++) {
          String storeId = (String) storeItems[i].getPropertyValue(PROPERTY_STORE_ID);
          stores.add(storeId);
        }
      }
    }
    catch(RepositoryException rex)
    {
      vlogError(rex, "Repository Exception occurred while getting the stores from location repository");
    }

    return stores;
  }

  /**
   * @return the locRepository
   */
  public Repository getLocRepository() {
    return locRepository;
  }

  /**
   * @param locRepository the locRepository to set
   */
  public void setLocRepository(Repository locRepository) {
    this.locRepository = locRepository;
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
