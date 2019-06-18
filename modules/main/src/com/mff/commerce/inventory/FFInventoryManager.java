package com.mff.commerce.inventory;

import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;

public interface FFInventoryManager extends InventoryManager{
  
  // Inventory Fields
  public static final String PROPERTY_SKU_ID              = "catalogRefId";
  public static final String PROPERTY_AVAILABILITY_STATUS = "availabilityStatus";
  public static final String PROPERTY_STOCK_LEVEL         = "stockLevel";
  public static final String PROPERTY_SOLD                = "sold";
  public static final String PROPERTY_ALLOCATED           = "allocated";
  public static final String PROPERTY_SHIPPED             = "shipped";

  // Store Inventory fields
  public static final String PROPERTY_STORE_INV_ID        = "inventoryId";
  public static final String PROPERTY_STORE_ID            = "storeId";
  public static final String PROPERTY_STORE_STOCK_STATUS  = "status";
  public static final String PROPERTY_STORE_STOCK_LEVEL   = "stockLevel";
  public static final String PROPERTY_STORE_ALLOCATED     = "storeAllocated";
  public static final String PROPERTY_STORE_SHIPPED       = "storeShipped";
  public static final String PROPERTY_STORE_DAMAGED       = "isDamaged";

  // Repository Item Name
  public static final String ITEM_DESC_STORE_INVENTORY              = "storeInventory";
  public static final String ITEM_DESC_INVENTORY_TRANSACTION        = "inventoryTransaction";
  public static final String ITEM_DESC_STORE_INVENTORY_TRANSACTION  = "storeInventoryTransaction";


  // Inventory prefix
  public static final String INVENTORY_PREFIX               = "inv-";

  // Default Location Id
  public static final String DEFAULT_LOCATION_ID            = "4000";

  // Stock Availability Constants
  public static final String STOCK_STATUS_AVAILABLE         = "In Stock";
  public static final String STOCK_STATUS_OUT_OF_STOCK      = "Out of Stock";
  
  /**
   * Gets the current stock level for an item from the local ATG inventory 
   * repository.  This call will:
   * - subtract out items that have been sold
   * - Check sale/site block flags 
   *  
   * @Param  pSkuId  - skuId
   * @Return The curent stock level for the item, or -1 if there is an error
   **/
   public abstract StockLevel querySkuStockLevel(String pSkuId) throws InventoryException;
   
   /**
    * Gets the current stock level for list of skuIds
    * @param pSkuIds
    * @return
    * @throws InventoryException
    */
   public Map<String,StockLevel> querySkusStockLevel(List<String> pSkuIds, boolean bIncludeBopisOnlyChecks) throws InventoryException;
   
   
   /**
    * Get the availability status for a list of stores.  The availability status is 
    * a number between 0 and 9.
    *  
    * @Param  pSkuId  - skuId
      @Param  pStoreNos - A list of store numbers for which we need inventory
    * @Return A list of Store availability (Store number and the store status)
    **/
    public abstract List <StoreAvailability> queryStoreAvailability(String pSkuId, List<String> pStoreNos) throws InventoryException;
    
   /**
    * Get the availability status for a given store.  The availability status is 
    * a number between 0 and 9.
    *  
    * @Param  pSkuId  - skuId
      @Param  pStoreNo  - A Store number
    * @Return A Store availability Object (Store number and the store status)
    **/
   // public abstract StoreAvailability queryStoreAvailability(String pSkuId, String pStoreNo) throws InventoryException;
    
    Map<String, Boolean> getBopisInventory(Map<String, Long> pSkus, List<String> pStoreNos) throws InventoryException;
   
    int setStoreDamaged(String pSkuId, String pStoreNo, boolean pIsDamaged) throws InventoryException;
    
    Map<String,StockLevel> queryStoreAvailability(List<String> pSkuIds, String pStoreNo) throws InventoryException;
    
    public int incrementStoreShipped (String pSkuId, String pStoreId, long pQty) throws InventoryException;

}
