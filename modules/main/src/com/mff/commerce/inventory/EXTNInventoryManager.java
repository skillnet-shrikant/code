package com.mff.commerce.inventory;


import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import com.mff.commerce.inventory.StockLevel;
import com.mff.commerce.inventory.StoreAvailability;

/**
 * 
 * @author KP
 *
 */
public interface EXTNInventoryManager 
	extends InventoryManager {

	// Inventory Fields
	public static final String PROPERTY_SKU_ID 					= "catalogRefId";
	public static final String PROPERTY_AVAILABILITY_STATUS		= "availabilityStatus";
	public static final String PROPERTY_STOCK_LEVEL				= "stockLevel";
	public static final String PROPERTY_SOLD 					= "sold";
	public static final String PROPERTY_ALLOCATED				= "allocated";
	public static final String PROPERTY_SHIPPED					= "shipped";
	
	// Store Inventory fields
	public static final String PROPERTY_STORE_INV_ID			= "inventoryId";
	public static final String PROPERTY_STORE_ID 				= "storeId";
	public static final String PROPERTY_STORE_STOCK_STATUS		= "status";
	public static final String PROPERTY_STORE_STOCK_LEVEL		= "stockLevel";
	public static final String PROPERTY_STORE_ALLOCATED    = "storeAllocated";
	public static final String PROPERTY_STORE_SHIPPED    = "storeShipped";
	public static final String PROPERTY_STORE_DAMAGED    = "isDamaged";
	
	

	// Repository Item Name
	public static final String STORE_INVENTORY_ITEM_NAME		= "storeInventory";
	
	// Inventory prefix
	public static final String INVENTORY_PREFIX					= "inv-"; 
	
	//Default Location Id
	public static final String DEFAULT_LOCATION_ID = "4000";
	
	// Stock Availability Constants
	public static final String STOCK_STATUS_AVAILABLE			= "In Stock"; 
	public static final String STOCK_STATUS_LIMITED_AVAILABLE	= "Limited Stock"; 
	public static final String STOCK_STATUS_OUT_OF_STOCK		= "Out of Stock"; 
	public static final String STOCK_STATUS_NOT_CARRIED			= "Not Carried"; 

	/**
	  * Increments the sold count on an item when an order is submitted.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param 	pQuantity 	- The number of items being purchased.
	  * @Return 1 if successful, -1 if unsuccessful.
	  **/
	  public abstract int purchase(String pSkuId, long pQuantity) throws InventoryException;

	 
	  /**
	  * Sets the stock level for a given skuId to a number, and reset the sold 
	  * count to zero. This is called anytime we reset the inventory
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param 	pQuantity 	- The number of items being purchased.
	  * @Return 1 if successful, -1 if unsuccessful
	  **/
	  public abstract int setStockLevel(String pSkuId, long pQuantity) throws InventoryException;

	 
	  /**
	  * Decrements the sold count on an item when an order is cancelled.  
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param 	pQuantity 	- The number of items being purchased.
	  * @Return 1 if successful, -1 if unsuccessful.
	  **/
	  public abstract int increaseStockLevel(String pSkuId, long pQuantity) throws InventoryException;

	  
	  /**
	  * Increments the sold count on an item when an order is submitted.  This is equivalent
	  * to a purchase.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param 	pQuantity 	- The number of items being purchased.
	  * @Return 1 if successful, -1 if unsuccessful.
	  **/
	  public abstract int decreaseStockLevel(String pSkuId, long pQuantity) throws InventoryException;
	  
	  /**
	  * Query the availability status of the current item.  This will return the Available Status 
	  * which will indicate if the item has stock, limited stock or no stock.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Return Available Status for the item
	  **/
	  public abstract String querySkuAvailabilityStatus(String pSkuId) throws InventoryException;
	  
	  /**
	   * Query the sold count of the current item
	   * 
	   * @param pSkuId
	   * @return
	   * @throws InventoryException
	   */
	  public long querySkuSoldCount(String pSkuId) throws InventoryException;
	  /**
	  * Gets the current stock level for an item from the local ATG inventory 
	  * repository.  This call will:
	  * - subtract out items that have been sold
	  * - Check sale/site block flags 
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Return The curent stock level for the item, or -1 if there is an error
	  **/
	  public abstract StockLevel querySkuStockLevel(String pSkuId) throws InventoryException;
	  
	  /**
	   * Gets the current stock level for list of skuIds
	   * @param pSkuIds
	   * @return
	   * @throws InventoryException
	   */
	  public Map<String,StockLevel> querySkusStockLevel(List<String> pSkuIds) throws InventoryException;
	  
	  /**
	  * Sets the stock availability for a given skuId/store.
	  *  
	  * @Param 	pSkuId 			- skuId
	  * @Param 	pStoreNo 			- The Store Number
	  * @Param	pAvailibilityStatus - SAP Availability Status
	  * @Return 1 if successful, -1 if unsuccessful
	  **/
	  public abstract int setStoreStockAvailability(String pSkuId, String pStoreNo, String pAvailibilityStatus) throws InventoryException;
	  
	  
	  /**
	  * Get the availability status for a given store.  The availability status is 
	  * a number between 0 and 9.
	  *  
	  * @Param 	pSkuId 	- skuId
	    @Param  pStoreNo	- A Store number
	  * @Return A Store availability Object (Store number and the store status)
	  **/
	  public abstract StoreAvailability queryStoreAvailability(String pSkuId, String pStoreNo) throws InventoryException;
	  
	  
	  /**
	  * Get the availability status for a list of stores.  The availability status is 
	  * a number between 0 and 9.
	  *  
	  * @Param 	pSkuId 	- skuId
	    @Param  pStoreNos	- A list of store numbers for which we need inventory
	  * @Return A list of Store availability (Store number and the store status)
	  **/
	  public abstract List <StoreAvailability> queryStoreAvailability(String pSkuId, List<String> pStoreNos) throws InventoryException;
	  
	  /**
	   * This method will be called when an allocated store declines to fulfill the item. This method
	   * increments the sold count and decrements the allocated count from inventory item(mff_inventory table)
	   * It decrements allocated count in for storeInventory (mff_store_inventory table)
	   */
	  public abstract int decrementStoreAllocated (String pSkuId, String pStoreId, long pQty) throws InventoryException;
	   
	  /**
      * This method will be called when an allocated store for bopis declines to fulfill the item. This method
      * will decrement the allocated count from inventory item(mff_inventory table)
      * It decrements allocated count in for storeInventory (mff_store_inventory table)
      */
    public abstract int decrementStoreAllocatedForBopis (String pSkuId, String pStoreId, long pQty) throws InventoryException;

    Map<String, Boolean> getBopisInventory(Map<String, Long> pSkus, List<String> pStoreNos) throws InventoryException;


    int setStoreDamaged(String pSkuId, String pStoreNo, boolean pIsDamaged) throws InventoryException;


    Map<String,StockLevel> queryStoreAvailability(List<String> pSkuIds, String pStoreNo) throws InventoryException;

}
