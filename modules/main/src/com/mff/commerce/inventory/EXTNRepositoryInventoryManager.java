package com.mff.commerce.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.sql.DataSource;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.adapter.gsa.GSARepository;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.RepositoryInventoryManager;
import atg.commerce.order.Order;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import mff.MFFEnvironment;

/**
 * 
 * @author KP
 *
 */
public class EXTNRepositoryInventoryManager 
	extends RepositoryInventoryManager implements EXTNInventoryManager {
	
	public static final String	SQL_QUERY_INVENTORY			= "select * from mff_inventory where inventory_id IN (";
	public static final String  SQL_QUERY_STORE_INVENTORY     = "select * from mff_store_inventory where inventory_id IN (";
	private MFFCatalogTools mCatalogTools;
	private MFFEnvironment mEnvironment;

	// Flag determining if we should acquire inventory locks in the preCommitOrder() method of EXTNCommitOrderFormHandler.
	private boolean mAcquireInventoryLocksPreCommitOrder;
	private String insertSQL;
	public static final String RQL_QUERY_DUPLICATE_BACK_IN_STOCK_ITEM = "catalogRefId = ?0 AND emailAddress = ?1 AND productId = ?2";
	private static String DEFAULT_STORE_LOCK_NAME = "extnInventoryManager";
	private String storeInventoryLockSql;
	private String onlineAndStoreInventoryLockSql;

	private boolean lockStoreInventory;
	private boolean turnOnHotfix;
	private boolean enableBopisInventoryFix;

	public boolean isLockStoreInventory() {
		return lockStoreInventory;
	}

	public void setLockStoreInventory(boolean pLockStoreInventory) {
		lockStoreInventory = pLockStoreInventory;
	}

	public boolean isTurnOnHotfix() {
		return turnOnHotfix;
	}

	public void setTurnOnHotfix(boolean pTurnOnHotfix) {
		turnOnHotfix = pTurnOnHotfix;
	}
	
	/**
	 * 
	 * @param pSkuId
	 * @return
	 */
	private Map<Object,Object> queryInventory(String pSkuId){
		
		vlogDebug("Entering queryInventory : pSkuId - {0}",pSkuId);
		List<String> skuIdsList = new ArrayList<String>();
		skuIdsList.add(pSkuId);
		
		List<Map<Object,Object>> skuResultsList = queryInventory(skuIdsList);
		
		return (skuResultsList != null && skuResultsList.size() > 0)? skuResultsList.get(0) : null;
	}
	
	/**
	 * 
	 * @param pSkuIds
	 * @return
	 */
	private ArrayList<Map<Object,Object>> queryInventory(List<String> pSkuIds){
		
		ArrayList<Map<Object,Object>> resultsList = new ArrayList<Map<Object,Object>>();
		
		Connection lConnection = null;
		DataSource lDataSource = ((GSARepository)getRepository()).getDataSource();
		ResultSet lResultSet = null;
		Statement lStatement = null;
	
		//String lSql = buildSqlQuery(pSkuId);
		String inValues = "";
		StringBuffer strBuff = new StringBuffer();
		
		for (String skuId : pSkuIds){
			strBuff.append("'");
			strBuff.append(INVENTORY_PREFIX);
			strBuff.append(skuId);
			strBuff.append("',");
		}
		
		inValues = strBuff.toString();
		
		//	Remove the trailing comma
		inValues = inValues.substring(0, inValues.length()-1);
		StringBuffer queryBuff = new StringBuffer();
		String query = "";
		queryBuff.append(SQL_QUERY_INVENTORY);
		queryBuff.append(inValues);
		queryBuff.append(")");
		query = queryBuff.toString();
		vlogDebug("queryInventory : query is : {0}",query);  
		try {
			lConnection = lDataSource.getConnection();
			lStatement = lConnection.createStatement();
			lResultSet = lStatement.executeQuery(query);
			while (lResultSet.next()) {
				
				HashMap<Object,Object> inventoryMap = new HashMap<Object,Object>();
				inventoryMap.put(PROPERTY_SKU_ID, lResultSet.getString("catalog_ref_id"));
				inventoryMap.put(PROPERTY_AVAILABILITY_STATUS, lResultSet.getString("avail_status"));
				vlogDebug("Stock From Table: {0} ",lResultSet.getLong("stock_level"));
				inventoryMap.put(PROPERTY_STOCK_LEVEL, lResultSet.getLong("stock_level"));
				inventoryMap.put(PROPERTY_SOLD, lResultSet.getLong("sold"));
				inventoryMap.put(PROPERTY_ALLOCATED, lResultSet.getLong("allocated"));
				inventoryMap.put(PROPERTY_SHIPPED, lResultSet.getLong("shipped"));
				resultsList.add(inventoryMap);
			}
		} catch (SQLException ex) {
			vlogError(ex, "Unable to get the results");
		} finally {
			try {
				lConnection.close();
			} catch (SQLException ex) {
				vlogError(ex, "Error closing the SQL Connection used to get the inventory results");
			}
		}
		
		return resultsList;
		
	}
	
	/**
	 * This method will query the database to find availability of all the skus at a particular store
	 * it calculates in stock quantity available for purchase (instock = stocklevel - (allocated + shipped))
	 * It also considers the requested quantity and check if instock is greater than requested quantity
	 * Any sku marked as damaged for a particular store will be excluded
	 * 
	 * @param pSkus
	 * @param pStoreNos
	 * @return
	 */
	 private Map<Object,List<String>> queryStoreInventory(Map<String,Long> pSkus, List<String> pStoreNos){
	    
	   //result set
	    HashMap<Object,List<String>> inventoryMap = new HashMap<Object,List<String>>();
	    
	    //db connection parameters
	    Connection lConnection = null;
	    DataSource lDataSource = ((GSARepository)getRepository()).getDataSource();
	    ResultSet lResultSet = null;
	    Statement lStatement = null;
	  
	    String[] skuIds = new String[pSkus.size()];
	    pSkus.keySet().toArray(skuIds);
	    
	    //construct and get bopis query
	    String bopisQuery = getBopisQuery(skuIds, pStoreNos);
	    
	    try {
	      //create connection
	      lConnection = lDataSource.getConnection();
	      lStatement = lConnection.createStatement();
	      lResultSet = lStatement.executeQuery(bopisQuery);
	      
	      //iterate through the result set
	      while (lResultSet.next()) {
	        //get data values
	        String lSkuId = lResultSet.getString("catalog_ref_id").toString();
	        String lStoreId = lResultSet.getString("store_id").toString();
	        long lStockLevel = lResultSet.getLong("stock_level");
	        long lAllocated = lResultSet.getLong("allocated");
	        long lShipped = lResultSet.getLong("shipped");
	        
	        //calculate the current stock available for purchase.
	        long lInStock = lStockLevel - (lAllocated + lShipped);
	        
	        //get the customer requested quantity from the cart
	        long reqQty = pSkus.get(lSkuId);
	        //if available stock for purchase is greater than requested quantity,
	        //then only add this store to list and perform additional processing
	        if(lInStock >= reqQty){
	          vlogDebug("queryStoreInventory: Requested Quantity: {0} of the sku:{1} is available at store:{2}",reqQty, lSkuId, lStoreId);
	          
	          //check if map already contains the store,
	          //if yes, retrieve the skuList and add this new sku
	          //else, create a new entry in the map
	          if(inventoryMap.get(lStoreId) == null){
	            vlogDebug("queryStoreInventory: Adding sku:{0} to available list of store:{1}",lSkuId, lStoreId);
	            List<String> availSkuList = new ArrayList<String>();
	            availSkuList.add(lSkuId);
	            inventoryMap.put(lStoreId, availSkuList);
	          }else{
	            List<String> availSkuList = inventoryMap.get(lStoreId);
	            availSkuList.add(lSkuId);
	            inventoryMap.put(lStoreId, availSkuList);
	          }
	        }else{
	          vlogDebug("queryStoreInventory: Requested Quantity: {0} of the sku:{1} is not available at store:{2}",reqQty, lSkuId, lStoreId);
	        }
	      }
	    } catch (SQLException ex) {
	      vlogError(ex, "Unable to get the results");
	    } finally {
	      try {
	        lConnection.close();
	      } catch (SQLException ex) {
	        vlogError(ex, "Error closing the SQL Connection used to get the inventory results");
	      }
	    }
	    
	    //print Inventory Map for debugging purposes
	    if(inventoryMap != null){
	      for(Object storeId: inventoryMap.keySet()){
	        if(inventoryMap.get(storeId) != null){
	          String skuList = "";
	          for(String skuId: inventoryMap.get(storeId))
	            skuList+=skuId+",";
	          vlogDebug("storeId:{0}, skuList:{1}",storeId,skuList);
	        }
	      }
	    }
	    return inventoryMap;
	  }
	
	/**
	  * Increments the sold count on an item when an order is submitted.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param 	pQuantity 	- The number of items being purchased.
	  * @Return 1 if successful, -1 if unsuccessful.
	  **/
	  public int purchase(String pSkuId, long pQuantity) throws InventoryException{
		  
		vlogDebug("Inside purchase: " + pQuantity + " of skuId " + pSkuId);
		int iRetValue = INVENTORY_STATUS_SUCCEED;
		TransactionDemarcation td = new TransactionDemarcation();
		String lErrorMessage 		 = "purchase failed for SkuId: %s Qty: %d Error: %s";
		
		try {
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

			// Increase the quantity sold for this sku
			iRetValue = decreaseStockLevel(pSkuId, pQuantity);
		}
		catch (TransactionDemarcationException e) {
			vlogError(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"),e);
			throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"),e);
		}
		finally {
			try {
				td.end();
			}
			catch (TransactionDemarcationException e) {
				vlogError(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"),e);
				throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"),e);
			}
		}
		
		return iRetValue;
	  }

	  /**
	   * Sets the stock level for a given skuId to a number, and reset the sold 
	   * count to zero. 
	   *  
	   * @Param 	pSkuId 	- skuId
	   * @Param 	pNumber - The number of items.
	   * @Return 1 if successful, -1 if unsuccessful
	  **/
	  @Override
	  public int setStockLevel(String pSkuId, long pNumber) throws InventoryException{
			
			vlogDebug("Entering setStockLevel : pSkuId - {0},pNumber - {1}",pSkuId,pNumber);
			
			TransactionDemarcation td = new TransactionDemarcation();
			try {
				td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
				MutableRepositoryItem item = getInventoryItemForUpdate(pSkuId,DEFAULT_LOCATION_ID);
				if(item != null){
					item.setPropertyValue(PROPERTY_SOLD,new Long(0));
					item.setPropertyValue(PROPERTY_STOCK_LEVEL, pNumber);
					updateItem(item);
				}else{
					vlogWarning("setStockLevel : No Inventory record found for sku {0} so skipped processing",pSkuId);
				}
			}
			catch (RepositoryException e) {
				if (isLoggingError()) {
					logError("Could not update the stock level", e);
				}
				throw new InventoryException("Could not update the stock level", e);
			}
			catch (TransactionDemarcationException e) {
				if (isLoggingError()) {
					logError("Could not update the stock level", e);
				}
				throw new InventoryException("Could not update the stock level", e);
			}
			finally {
				try {
					td.end();
				}
				catch (TransactionDemarcationException tde) {
					if (isLoggingError()) {
						logError(tde);
					}
				}
			}
			
			vlogDebug("Exiting setStockLevel");
			
			return INVENTORY_STATUS_SUCCEED;
		
	  }

	 
	  /**
	  * Decrements the sold count on an item when an order is cancelled.  
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param 	pQuantity 	- The number of items being cancelled.
	  * @Return 1 if successful, -1 if unsuccessful.
	  **/
	  public int increaseStockLevel(String pSkuId, long pQuantity) throws InventoryException{
		  	
		  	long lQtySold		 	 = 0;
			long lQtySoldUpdated  	 = 0;		
			String lErrorMessage 		 = "increaseStockLevel failed for SkuId: %s Qty: %d Error: %s";
			
			vlogDebug("Start increaseStockLevel for pSkuId: " + pSkuId + " Qty: " + pQuantity);
			TransactionDemarcation td = new TransactionDemarcation();
			try {
				td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
				MutableRepositoryItem lItem = getInventoryItemForUpdate(pSkuId,DEFAULT_LOCATION_ID);
				
				// Check that we got an inventory record
				if (lItem == null) {
					vlogError(String.format(lErrorMessage, pSkuId, pQuantity, "Not Found"));
					throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Not Found"));
				}
				
				lQtySold = ((Long) lItem.getPropertyValue(PROPERTY_SOLD)).longValue();
				//lInventoryInStock = ((Long) lItem.getPropertyValue(PROPERTY_STOCK_LEVEL)).longValue();
				
				// subtract the number of items been cancelled from sold
				lQtySoldUpdated = lQtySold - pQuantity;
				
				vlogInfo("increaseStockLevel: pSkuId: {0} Qty Sold: {1} Qty Sold Remaining: {2}",pSkuId, lQtySold, lQtySoldUpdated);
				// Update Inventory
				lItem.setPropertyValue(PROPERTY_SOLD, lQtySoldUpdated);
				updateItem(lItem);
			}
			catch (RepositoryException e) {
				vlogError(String.format(lErrorMessage, pSkuId, pQuantity, "Repository Error"),e);
				throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Repository Error"),e);
			}
			catch (TransactionDemarcationException e) {
				vlogError(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"),e);
				throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"),e);
			}
			finally {
				try {
					td.end();
				}
				catch (TransactionDemarcationException tde) {
					logError(tde);
				}
			}
			vlogDebug ("----------------------------------------------------------------------------");
			vlogDebug("Exiting increaseStockLevel ");
			vlogDebug("                  SkuId: " + pSkuId);
			vlogDebug("        	  	  Quantity: " + pQuantity);		
			vlogDebug("           Initial sold: " + lQtySold);
			vlogDebug("           Updated sold: " + lQtySoldUpdated);
			vlogDebug ("----------------------------------------------------------------------------");
			
			return INVENTORY_STATUS_SUCCEED;
	  }

	  
	  /**
	  * Increments the sold count on an item when an order is submitted.  This is equivalent
	  * to a purchase.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param 	pQuantity 	- The number of items being purchased.
	  * @Return 1 if successful, -1 if unsuccessful.
	  **/
	  public int decreaseStockLevel(String pSkuId, long pQuantity) throws InventoryException{
		int returnStatus = INVENTORY_STATUS_SUCCEED;
	  	vlogDebug("Start of decreaseStockLevel.  Will increase Qty Sold for SkuId: " + pSkuId + " by: " + pQuantity);
		TransactionDemarcation td = new TransactionDemarcation();
		try {
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
			MutableRepositoryItem item = getInventoryItemForUpdate(pSkuId,DEFAULT_LOCATION_ID);

			if(item != null){
				//This call seems to always get the live, un-cached, DB value:
				long lQtySold = ((Long) item.getPropertyValue(PROPERTY_SOLD)).longValue();
				long lQtyAllocated = ((Long) item.getPropertyValue(PROPERTY_ALLOCATED)).longValue();
				long lQtyShipped = ((Long) item.getPropertyValue(PROPERTY_SHIPPED)).longValue();
				long lInventoryInStock = ((Long) item.getPropertyValue(PROPERTY_STOCK_LEVEL)).longValue();
				long lAvailableStock = lInventoryInStock - (lQtySold + lQtyAllocated + lQtyShipped); 
				
				//long lInventoryInStock = getInventoryInStock (item);

				if (lAvailableStock < pQuantity) {
					returnStatus = INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
					vlogInfo("INVENTORY OVERSOLD : The inventory for SkuId: {0} has been oversold", pSkuId);
					return returnStatus;
				}
				else {
					logInfo("decreaseStockLevel: SkuId:" + pSkuId + ", Qty Sold is being incremented from:" + lQtySold + " to " + (lQtySold + pQuantity));
				}
				
				//Update the QtySold property regardless
				item.setPropertyValue(PROPERTY_SOLD, lQtySold + pQuantity);
				updateItem(item);
				
				if (isLoggingDebug()) {
					logDebug ("----------------------------------------------------------------------------");
					logDebug ("In decreaseStockLevel for SkuId:" + pSkuId);
					logDebug ("Inventory in Stock  	:" + lInventoryInStock);
					logDebug ("QtySold before   	:" + lQtySold);
					logDebug ("Number sold now  	:" + pQuantity);
					logDebug ("New QtySold total	:" + (lQtySold + pQuantity));
					logDebug ("----------------------------------------------------------------------------");
				}
			}
		}
		catch (RepositoryException e) {
			vlogError("RepositoryException caught in EXTNRepositoryInventoryManager.decreaseStockLevel.  The QtySold for pSkuId:" + pSkuId + " could not be incremented.  Exception:" + e);
			throw new InventoryException("Could not increment Qty Sold for pSkuId:" + pSkuId, e);
		}
		catch (TransactionDemarcationException e) {
			vlogError("TransactionDemarcationException caught in EXTNRepositoryInventoryManager.decreaseStockLevel.  Could not increment QtySold for Sku:" + pSkuId + ".  Exception:", e);
			throw new InventoryException("Could not increment QtySold for Sku:" + pSkuId, e);
		}
		finally {
			try {
				td.end();
			}
			catch (TransactionDemarcationException tde) {
				vlogError("TransactionDemarcationException caught in EXTNRepositoryInventoryManager.decreaseStockLevel.  Could not increment QtySold for Sku:" + pSkuId + ". Exception:" + tde);
			}
		}
		vlogDebug("End of EXTNRepositoryInventoryManager.decreaseStockLevel for SkuId Id:" + pSkuId);
		return returnStatus;
	  }
	
	/**
	 * Query the availability status of the current item.
	 *  
	 * @Param 	pSkuId 	- skuId
	 * @Return Available Status for the item
	 **/
	@Override
	public String querySkuAvailabilityStatus(String pSkuId) throws InventoryException {
		String avStatus = "";
		String lErrorMessage 		 = "querySkuAvailabilityStatus failed for SkuId: %s Error: %s";

		try {
			Map<Object, Object> invMap = queryInventory(pSkuId);
			if(invMap != null && invMap.size() > 0){
				avStatus = (String) invMap.get(PROPERTY_AVAILABILITY_STATUS);
				vlogDebug("querySkuAvailabilityStatus : pSkuId - {0}, Available Status - {1}",pSkuId,avStatus);
			}
		} catch (Exception e) {
			vlogError(String.format(lErrorMessage, pSkuId, "Exception"),e);
			throw new InventoryException (String.format(lErrorMessage, pSkuId, "Exception"),e);
		}
		
		return avStatus;
	}
	
	/**
	  * Query the availability status of the current item.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Return sold count
	  **/
	@Override
	public long querySkuSoldCount(String pSkuId) throws InventoryException {
		long sold = 0;
		String lErrorMessage 		 = "querySkuSoldCount failed for SkuId: %s Error: %s";
		
		Map<Object, Object> invMap;
		try {
			invMap = queryInventory(pSkuId);
			if(invMap != null && invMap.size() > 0){
				sold = (Long) invMap.get(PROPERTY_SOLD);
				vlogDebug("querySkuAvailabilityStatus : pSkuId - {0}, sold - {1}",pSkuId,sold);
			}
		} catch (Exception e) {
			vlogError(String.format(lErrorMessage, pSkuId, "Exception"));
			throw new InventoryException (String.format(lErrorMessage, pSkuId, "Exception"),e);
		}
		
		return sold;
	}
	
	/**
	 * Gets the current stock level for an item from the local ATG inventory 
	 * repository.  This call will:
	 * - subtract out items that have been sold
	 *  
	 * @Param 	pSkuId 	- skuId
	 * @Return Stock level for the item, or null if there is an error
	 **/
	@Override
	public StockLevel querySkuStockLevel(String pSkuId) throws InventoryException {
		
		long invStockLevel = 0; 
		long sold = 0;
		long shipped = 0;
		long allocated = 0;
		String lErrorMessage 		 = "querySkuStockLevel failed for SkuId: %s Error: %s";

		Map<Object, Object> invMap;
		try {
			invMap = queryInventory(pSkuId);
			if(invMap != null && invMap.size() > 0){		
				StockLevel stockLevel = new StockLevel();

				invStockLevel = (Long)invMap.get(PROPERTY_STOCK_LEVEL);
				vlogDebug("SkuID: {0} Stock Level:{1}",pSkuId,invStockLevel);
				
				sold  = (Long)invMap.get(PROPERTY_SOLD);
				shipped  = (Long)invMap.get(PROPERTY_SHIPPED);
				allocated  = (Long)invMap.get(PROPERTY_ALLOCATED);
				
				//rule to calculate the inventory
				//Updated the below LOGIC to fix the issue with NEGATIVE Inventories
				
				long invSoldAllocatedShipped = 0;
				invSoldAllocatedShipped = sold+allocated+shipped;
				
				if(invStockLevel > 0 ) {
					if(invSoldAllocatedShipped >= 0) {
						invStockLevel = invStockLevel - invSoldAllocatedShipped;
					}else {
						invStockLevel = invStockLevel + invSoldAllocatedShipped;
					}
				}else {
					invStockLevel = 0;
				}
				
				//invStockLevel = invStockLevel - (sold+allocated+shipped);
				
				vlogDebug("SkuID: {0} Updated Stock Level :{1}",pSkuId,invStockLevel);
				stockLevel.setSkuId((String)invMap.get(PROPERTY_SKU_ID));
				stockLevel.setStockLevel(invStockLevel);
				stockLevel.setSold((long)invMap.get(PROPERTY_SOLD));
				stockLevel.setShipped((long)invMap.get(PROPERTY_SHIPPED));
				
				vlogDebug("querySkuStockLevel : - {0}",stockLevel.toString());
				
				return stockLevel;
			}
		} catch (Exception e) {
			vlogError(String.format(lErrorMessage, pSkuId, "Exception"));
			throw new InventoryException (String.format(lErrorMessage, pSkuId, "Exception"),e);
		}
		
		return null;
	}
	
	 /**
	  * Gets the current stock level for list of skuIds
	  * 
	  * @param pSkuIds
	  * @return
	  * @throws InventoryException
	  */
	@Override
	public Map<String,StockLevel> querySkusStockLevel(List<String> pSkuIds) throws InventoryException {
		
		Map <String, StockLevel> resultsMap = new HashMap <String, StockLevel> ();
		String lErrorMessage 		 = "querySkusStockLevel failed for SkuIds: %s Error: %s";
		try {
			List<Map<Object, Object>> skuInvMap = queryInventory(pSkuIds);
			 
			//for (int i=0; i < invMap.size(); i++) 
			for (Map<Object, Object> invMap: skuInvMap)
			{
				StockLevel stockLevel = new StockLevel();
				String skuId = (String)invMap.get(PROPERTY_SKU_ID);
				
				long invStockLevel = (Long)invMap.get(PROPERTY_STOCK_LEVEL);
				long sold  = (Long)invMap.get(PROPERTY_SOLD);
				long shipped  = (Long)invMap.get(PROPERTY_SHIPPED);
				long allocated  = (Long)invMap.get(PROPERTY_ALLOCATED);
        
				long invSoldAllocatedShipped = 0;
				invSoldAllocatedShipped = sold+allocated+shipped;
				
				if(invStockLevel > 0 ) {
					if(invSoldAllocatedShipped >= 0) {
						invStockLevel = invStockLevel - invSoldAllocatedShipped;
					}else {
						invStockLevel = invStockLevel + invSoldAllocatedShipped;
					}
				}else {
					invStockLevel = 0;
				}
				
				stockLevel.setSkuId((String)invMap.get(PROPERTY_SKU_ID));
				stockLevel.setStockLevel(invStockLevel);
				
				vlogDebug("querySkusStockLevel : - {0}",stockLevel.toString());
				
				resultsMap.put(skuId,stockLevel);
			}
		} catch (Exception e) {
			vlogError(String.format(lErrorMessage, pSkuIds, "Exception"));
			throw new InventoryException(String.format(lErrorMessage, pSkuIds, "Exception"),e);
		}
		return resultsMap;
	}
	
	/**
	  * Sets the stock availability for a given skuId/store.
	  *  
	  * @Param 	pSkuId 			- skuId
	  * @Param 	pStoreNo 			- The Store Number
	  * @Param	pAvailibilityStatus - SAP Availability Status
	  * @Return 1 if successful, -1 if unsuccessful
	  **/
	@Override
	public int setStoreStockAvailability(String pSkuId, String pStoreNo,String pAvailibilityStatus) throws InventoryException {
		
		vlogDebug("Entering setStoreStockAvailability : pSkuId - {0}, pStoreNo - {1}, pAvailibilityStatus - {2}",pSkuId, pStoreNo, pAvailibilityStatus);
		String lErrorMessage 		 = "setStoreStockAvailability failed for SkuId: %s Store: %s pStatus: %d Error: %s";
		TransactionDemarcation td = new TransactionDemarcation();
		try {
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
			// Update Store Inventory
			MutableRepositoryItem lStoreItem = getStoreInventoryItemForUpdate (pSkuId, pStoreNo);
			if (lStoreItem == null) {
				vlogError("No sku inventory exists for skuId - {0} for store - {1}",pSkuId,pStoreNo);
				throw new InventoryException(String.format(lErrorMessage, pSkuId, pStoreNo, pAvailibilityStatus, "Store SkuId Not Found"));
			}			
			lStoreItem.setPropertyValue(PROPERTY_STORE_STOCK_STATUS,pAvailibilityStatus);
			//getRepository().updateItem(lStoreItem);
			updateStoreInventoryItem(lStoreItem);
		}
		catch (RepositoryException e) {
			vlogError("Could not update the availability status", e);
			throw new InventoryException("Could not update the stock level", e);
		}
		catch (TransactionDemarcationException e) {
			vlogError("Could not update the availability status", e);
			throw new InventoryException("Could not update the availability status", e);
		}
		finally {
			try {
				td.end();
			}
			catch (TransactionDemarcationException tde) {
				if (isLoggingError()) {
					logError(tde);
				}
			}
		}
		
		vlogDebug("Exiting setStoreStockAvailability");
		
		return INVENTORY_STATUS_SUCCEED;
	}
	
	/**
   * Sets the damaged flag for a given skuId/store.
   *  
   * @Param  pSkuId      - skuId
   * @Param  pStoreNo      - The Store Number
   * @Return 1 if successful, -1 if unsuccessful
   **/
 @Override
 public int setStoreDamaged(String pSkuId, String pStoreNo,boolean isDamaged) throws InventoryException {
   
   vlogDebug("Entering setStoreDamaged : pSkuId - {0}, pStoreNo - {1}, pIsDamaged - {2}",pSkuId, pStoreNo, isDamaged);
   String lErrorMessage     = "setStoreDamaged failed for SkuId: %s Store: %s pStatus: %d Error: %s";
   TransactionDemarcation td = new TransactionDemarcation();
   try {
     td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
     // Update Store Inventory
     MutableRepositoryItem lStoreItem = getStoreInventoryItemForUpdate (pSkuId, pStoreNo);
     if (lStoreItem == null) {
       vlogError("No sku inventory exists for skuId - {0} for store - {1}",pSkuId,pStoreNo);
       throw new InventoryException(String.format(lErrorMessage, pSkuId, pStoreNo, isDamaged, "Store SkuId Not Found"));
     }     
     lStoreItem.setPropertyValue(PROPERTY_STORE_DAMAGED,isDamaged);
     //getRepository().updateItem(lStoreItem);
     updateStoreInventoryItem(lStoreItem);
   }
   catch (RepositoryException e) {
     vlogError("Could not update the damaged", e);
     throw new InventoryException("Could not update the damaged", e);
   }
   catch (TransactionDemarcationException e) {
     vlogError("Could not update the damged", e);
     throw new InventoryException("Could not update the damaged", e);
   }
   finally {
     try {
       td.end();
     }
     catch (TransactionDemarcationException tde) {
       if (isLoggingError()) {
         logError(tde);
       }
     }
   }
   
   vlogDebug("Exiting setStoreDamaged");
   
   return INVENTORY_STATUS_SUCCEED;
 }
	
	/**
	 * Get a store inventory record for update.
	 * @param pSkuId SkuId 
	 * @param pStoreId  Store Number
	 * @return MutableRepositoryItem
	 * @throws RepositoryException
	 */
	protected MutableRepositoryItem getStoreInventoryItemForUpdate (String pSkuId, String pStoreId)
		throws RepositoryException   {
		
		String lId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;
		/*if (getRepository() != null) 
			return getRepository().getItemForUpdate(lId, STORE_INVENTORY_ITEM_NAME);
		else
			return null;*/
		
		if(isLockStoreInventory()) {
		    try {
		        lockStoreInventory(lId);
		      }
		      catch(java.sql.SQLException se) {
		        throw new RepositoryException(se);
		      }			
		}



    if (getRepository() != null) 
      return getRepository().getItemForUpdate(lId, STORE_INVENTORY_ITEM_NAME);
    else
      return null;

	}
	
	/**
	  * Get the availability status for a given store.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param  pStoreNo	- A Store number
	  * @Return A Store availability Object (Store number and the store status)
	  **/
	@Override
	public StoreAvailability queryStoreAvailability(String pSkuId,String pStoreNo) throws InventoryException {
		
		vlogDebug("Entering queryStoreAvailability : pSkuId - {0}, pStoreNo - {1}",pSkuId, pStoreNo);
		List<String> storeList = new ArrayList<String>();
		storeList.add(pStoreNo);
		
		List<StoreAvailability> storeAvList = queryStoreAvailability(pSkuId, storeList);
		
		return (storeAvList != null && storeAvList.size() > 0)? storeAvList.get(0) : null;
	}
	
	/**
	  * Get the availability status for a list of stores.
	  *  
	  * @Param 	pSkuId 	- skuId
	  * @Param  pStoreNos	- A list of store numbers for which we need inventory
	  * @Return A list of Store availability (Store number and the store status)
	  **/
	@Override
	public List<StoreAvailability> queryStoreAvailability(String pSkuId,List<String> pStoreNos) throws InventoryException {
		
		vlogDebug("Entering queryStoreAvailability : pSkuId - {0}, pStoreNos - {1}",pSkuId, pStoreNos);
		List<StoreAvailability> storeAvList = new ArrayList<StoreAvailability>();
		String lErrorMessage 		 = "queryStoreAvailability failed for SkuId: %s Error: %s";
		String[] idsArr = new String[pStoreNos.size()];
		for(int i = 0; i < pStoreNos.size(); i++){
			idsArr[i] = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreNos.get(i);
		}
		vlogDebug("IdsArray : - {0}",Arrays.toString(idsArr));	
		try {
			RepositoryItem[] storeItems = getRepository().getItems(idsArr, STORE_INVENTORY_ITEM_NAME);
			
			if(storeItems == null)
			  vlogError("Stores not Found");
			else
			  vlogDebug("Store Inventory Result Set Size:{0}", storeItems.length);
			
			for(RepositoryItem storeItem : storeItems){
				StoreAvailability storeAv = new StoreAvailability();
					storeAv.setSkuId(pSkuId);
					storeAv.setStoreNo((String)storeItem.getPropertyValue(PROPERTY_STORE_ID));
					long storeStockLevel = (long)storeItem.getPropertyValue(PROPERTY_STORE_STOCK_LEVEL);
					long storeAllocated = (long)storeItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
					long storeShipped = (long)storeItem.getPropertyValue(PROPERTY_STORE_SHIPPED);
					long storeInStockForPurchase = storeStockLevel - (storeAllocated + storeShipped);
					
					storeAv.setStoreStockLevel(storeInStockForPurchase);
					//storeAv.setStoreStockLevel((long)storeItem.getPropertyValue(PROPERTY_STORE_STOCK_LEVEL));
					storeAv.setAvailabilityStatus(STOCK_STATUS_AVAILABLE);
				storeAvList.add(storeAv);
				vlogDebug("queryStoreAvailability : - {0}",storeAv);	
			}
		} catch (RepositoryException e) {
			vlogError(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
			throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
		}
		return storeAvList;
	}
	
	/**
   * Get the availability status for a list of stores.
   *  
   * @Param  pSkuId  - skuId
   * @Param  pStoreNos - A list of store numbers for which we need inventory
   * @Return A list of Store availability (Store number and the store status)
   **/
 @Override
 public Map<String,StockLevel> queryStoreAvailability(List<String> pSkuIds,String pStoreNo) throws InventoryException {
   
   vlogDebug("Entering queryStoreAvailability : pSkuIds - {0}, pStoreNo - {1}",pSkuIds, pStoreNo);
   Map <String, StockLevel> resultsMap = new HashMap <String, StockLevel> ();
   String lErrorMessage     = "queryStoreAvailability failed for SkuId: %s Error: %s";
   
   /*if(isEnableBopisInventoryFix()){
     Map <String, StockLevel> invMap = querySkusStockLevel(pSkuIds);
     if(invMap != null && invMap.size() > 0){
       Set<String> lSkuIds = invMap.keySet();
       for(String skuId : lSkuIds){
         StockLevel stockLevel = invMap.get(skuId);
         // this is returning true as remaining stock is greater than 1 so we need to move this MFFOrderManager 
         if(stockLevel != null && stockLevel.getStockLevel() > 0){
           // all good continue
         }else{
           vlogInfo("queryStoreAvailability : Inventory in mff_inventory is 0 for skuId {0}", stockLevel.getSkuId());
           // return inventory map to indicate the main inventory is invalid
           return  invMap;
         }
       }
     } 
   }*/
   
   String[] idsArr = new String[pSkuIds.size()];
   for(int i = 0; i < pSkuIds.size(); i++){
     idsArr[i] = INVENTORY_PREFIX + pSkuIds.get(i) + getEnvironment().getStoreInvItemDelim() + pStoreNo;
   }
   vlogDebug("IdsArray : - {0}",Arrays.toString(idsArr));  
   try {
     RepositoryItem[] storeItems = getRepository().getItems(idsArr, STORE_INVENTORY_ITEM_NAME);
     
     if(storeItems == null)
       vlogError("Stores not Found");
     else
       vlogDebug("Store Inventory Result Set Size:{0}", storeItems.length);
     
     for(RepositoryItem storeItem : storeItems){
       StockLevel storeAv = new StockLevel();
       String skuId = (String)storeItem.getPropertyValue(PROPERTY_SKU_ID);
         storeAv.setSkuId(skuId);
         
         long storeStockLevel = (long)storeItem.getPropertyValue(PROPERTY_STORE_STOCK_LEVEL);
         long storeAllocated = (long)storeItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
         long storeShipped = (long)storeItem.getPropertyValue(PROPERTY_STORE_SHIPPED);
         long storeInStockForPurchase = storeStockLevel - (storeAllocated + storeShipped);
         
         storeAv.setStockLevel(storeInStockForPurchase);
         
         resultsMap.put(skuId,storeAv);
       vlogDebug("queryStoreAvailability : - {0}",storeAv);  
     }
   } catch (RepositoryException e) {
     vlogError(String.format(lErrorMessage, pStoreNo, "Repository Error"),e);
     throw new InventoryException(String.format(lErrorMessage, pStoreNo, "Repository Error"),e);
   }
   return resultsMap;
 }
	/**
	 * get a list of stores
   * Loop through the list of skus
   * get list of skus
   * loop through the list of skus
   * check if all skus are available at store
   * if any of the sku do not have stock available then break the loop
   * and mark the bopisElgible flag to False
   * if bopisElgible flag is set to false for this store, mark it as stock not available.
   * else mark this store as stock available
	 */
 @Override
 public Map<String,Boolean> getBopisInventory(Map<String,Long> pSkus,List<String> pStoreNos) throws InventoryException {
   
   if(pSkus == null || pStoreNos == null){
     vlogError("No Skus or Stores found to query the inventory");
     throw new InventoryException("No Skus or Stores found to query the inventory");
   }
   if(pSkus.size() < 0 || pStoreNos.size() < 0){
     vlogError("No Skus or Stores found to query the inventory");
     throw new InventoryException("No Skus or Stores found to query the inventory");
   }
   
     Map<String,Boolean> storeAvStatus = new HashMap<String,Boolean>();
   
     String[] skuIds = new String[pSkus.size()];
     pSkus.keySet().toArray(skuIds);
     
     Map<Object,List<String>> inventoryMap = queryStoreInventory(pSkus, pStoreNos); 
     
     for(Object storeId : inventoryMap.keySet()){
       List<String> skuList = inventoryMap.get(storeId);
       if(skuList != null && skuIds.length == skuList.size()){
         storeAvStatus.put(storeId.toString(), true);
         vlogDebug("Store no: {0} is elgible for Pickup",storeId);
         
       }else{
           storeAvStatus.put(storeId.toString(), false);
           vlogDebug("One or more skuids are not avilable at Store no: {0} for Pickup",storeId);
       }
     }

     return storeAvStatus;
 }

 protected MutableRepositoryItem getStoreInventoryItemForUpdateNoLock (String pSkuId, String pStoreId)
		 throws RepositoryException   {

	 String lId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;

	 if (getRepository() != null) 
		 return getRepository().getItemForUpdate(lId, STORE_INVENTORY_ITEM_NAME);
	 else
		 return null;

 } 
 protected MutableRepositoryItem getInventoryItemForUpdateNoLock(String pId, String pLocationId)
		 throws RepositoryException
 {

	 RepositoryItem item = getInventoryItem(pId, pLocationId);
	 MutableRepositoryItem mutItem = null;
	 if (item instanceof MutableRepositoryItem)
		 mutItem = (MutableRepositoryItem)item;
	 else  if (item != null)
		 mutItem = getRepository().getItemForUpdate(item.getRepositoryId(), getItemType());
	 else
		 return null;

	 return mutItem;
 }	
	/**
	* This method will be called when a SKU is allocated to a store. This method
	 * decrements the sold count and increments the allocated count from inventory item(mff_inventory table)
	* It also increments allocated count for storeInventory(mff_store_inventory table)
	*/
	public int incrementStoreAllocated (String pSkuId, String pStoreId, long pQty) throws InventoryException{
	  
	  String lErrorMessage      = "incrementStoreAllocated failed for SkuId: %s Error: %s";
	  
	  vlogDebug("Entering incrementStoreAllocated");
	  TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      MutableRepositoryItem lInvItem=null;
      MutableRepositoryItem lStoreItem=null;
      
      if(isTurnOnHotfix()) {
          // This call will be updating both mff_inventory & mff_store_inventory tables
          // Lets lock the single row in mff_inventory table that corresponds to the sku passed in
          // Lets lock the single row in mff_store_inventory table that corresponds to the sku & store passed in
          
          lockOnlineAndStoreInventory(pSkuId,pStoreId);
          
  	    //get mff inventory item and update sold & allocation count
  	    lInvItem = getInventoryItemForUpdateNoLock (pSkuId, DEFAULT_LOCATION_ID);
  	    //get mff store inventory item and update allocation count
        lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);    	  
      } else {
  	    //get mff inventory item and update sold & allocation count
  	    lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
  	    //get mff store inventory item and update allocation count
        lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);    	  
      }

      
      //if store item or inventory item not found return an error message
	    if(lInvItem == null){
	      vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
	      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
	    }else if(lStoreItem == null){
	      vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}", pSkuId, pStoreId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
	    }else if(pQty <=0){
        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
      }else{
    	      long lSoldCount = (long)lInvItem.getPropertyValue(PROPERTY_SOLD);
            long lAllocated = (long)lInvItem.getPropertyValue(PROPERTY_ALLOCATED);
            long lUpdatedSoldCount = lSoldCount - pQty;
            
            lInvItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated + pQty);
            lInvItem.setPropertyValue(PROPERTY_SOLD, lUpdatedSoldCount);
            
            vlogInfo("incrementStoreAllocated: pSkuId: {0} Current Qty Sold: {1} Qty Sold Remaining : {2}",pSkuId, lSoldCount, lUpdatedSoldCount);
            if(lUpdatedSoldCount < 0){
              vlogInfo("INVENTORY OVERSOLD : The inventory for SkuId: {0} has been oversold", pSkuId);
            }
            
            updateItem(lInvItem);
            
            long lStoreAllocatedCount = (long)lStoreItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
            lStoreItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocatedCount+pQty);
            
            vlogInfo("incrementStoreAllocated: pSkuId: {0} pStoreId: {1} Current Store Allocated Qty: {2} Store Allocated Qty Incremented to : {3} ",pSkuId ,pStoreId, lStoreAllocatedCount, (lStoreAllocatedCount+pQty));
            //getRepository().updateItem(lStoreItem);
            updateStoreInventoryItem(lStoreItem);
	    }
    } catch (RepositoryException e) {
      vlogError(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
    }catch (SQLException e) {
        vlogError(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
	} catch (TransactionDemarcationException e) {
      vlogError("Transaction Error while updating the sold & allocated count for skuId: " + pSkuId,e);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
    }finally {
      try {
        td.end();
      }
      catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }
    vlogDebug("Exiting incrementStoreAllocated");
    
	  return INVENTORY_STATUS_SUCCEED;
	}
	 
	/**
	* This method will be called when an allocated store declines to fulfill the item. This method
	* increments the sold count and decrements the allocated count from inventory item(mff_inventory table)
	* It decrements allocated count in for storeInventory (mff_store_inventory table)
	*/
	public int decrementStoreAllocated (String pSkuId, String pStoreId, long pQty) throws InventoryException{
	  
   String lErrorMessage      = "decrementStoreAllocated failed for SkuId: %s Error: %s";
   
   vlogDebug("Entering decrementStoreAllocated");
   TransactionDemarcation td = new TransactionDemarcation();
   try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      MutableRepositoryItem lInvItem=null;
      MutableRepositoryItem lStoreItem=null;
      if(isTurnOnHotfix()) {
          // This call will be updating both mff_inventory & mff_store_inventory tables
          // Lets lock the single row in mff_inventory table that corresponds to the sku passed in
          // Lets lock the single row in mff_store_inventory table that corresponds to the sku & store passed in
          
          lockOnlineAndStoreInventory(pSkuId,pStoreId);
          
  	    //get mff inventory item and update sold & allocation count
  	    lInvItem = getInventoryItemForUpdateNoLock (pSkuId, DEFAULT_LOCATION_ID);
  	    //get mff store inventory item and update allocation count
        lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);    	  
      } else {
  	    //get mff inventory item and update sold & allocation count
  	    lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
  	    //get mff store inventory item and update allocation count
        lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);    	  
      }
      
      //get mff inventory item and update sold & allocation count
      //MutableRepositoryItem lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
      
    //get mff store inventory item and update allocation count
      //MutableRepositoryItem lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);
      
      //if store item or inventory item not found return an error message
      if(lInvItem == null){
        vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
      }else if(lStoreItem == null){
        vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}", pSkuId, pStoreId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
      }else if(pQty <=0){
        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
      }else{
        long lSoldCount = (long)lInvItem.getPropertyValue(PROPERTY_SOLD);
        long lAllocated = (long)lInvItem.getPropertyValue(PROPERTY_ALLOCATED);
        long lUpdatedSoldCount = lSoldCount + pQty;
        
        //decrement allocated & increment the sold count
        lInvItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated - pQty);
        lInvItem.setPropertyValue(PROPERTY_SOLD, lUpdatedSoldCount);
        
        vlogInfo("decrementStoreAllocated: pSkuId: {0} Current Qty Sold: {1} Qty Sold Incremented to : {2}",pSkuId, lSoldCount, lUpdatedSoldCount);
        if(lUpdatedSoldCount < 0){
          vlogInfo("INVENTORY OVERSOLD : The inventory for SkuId: {0} has been oversold", pSkuId);
        }
        
        updateItem(lInvItem);
        
        long lStoreAllocatedCount = (long)lStoreItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
        lStoreItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocatedCount-pQty);
        
        vlogInfo("decrementStoreAllocated: pSkuId: {0} pStoreId: {1} Current Store Allocated Qty: {2} Store Allocated Qty Remaining: {3} ",pSkuId ,pStoreId, lStoreAllocatedCount, (lStoreAllocatedCount-pQty));
        //getRepository().updateItem(lStoreItem);
        updateStoreInventoryItem(lStoreItem);
      }
    } catch (RepositoryException e) {
      vlogError(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
    }catch (SQLException e) {
        vlogError(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
	} catch (TransactionDemarcationException e) {
      vlogError("Transaction Error while updating the sold & allocated count for skuId:{0}", pSkuId);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
    }finally {
      try {
        td.end();
      }
      catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }
    vlogDebug("Exiting decrementStoreAllocated");
    
	  return INVENTORY_STATUS_SUCCEED;
	}
	
	/**
	  * This method will be called when an allocated store for bopis declines to fulfill the item. This method
	  * will decrement the allocated count from inventory item(mff_inventory table)
	  * It decrements allocated count in for storeInventory (mff_store_inventory table)
	  */
	  public int decrementStoreAllocatedForBopis (String pSkuId, String pStoreId, long pQty) throws InventoryException{
	    
	   String lErrorMessage      = "decrementStoreAllocatedForBopis failed for SkuId: %s Error: %s";
	   
	   vlogDebug("Entering decrementStoreAllocatedForBopis");
	   TransactionDemarcation td = new TransactionDemarcation();
	   try {
	      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

	      MutableRepositoryItem lInvItem=null;
	      MutableRepositoryItem lStoreItem=null;
	      if(isTurnOnHotfix()) {
	          // This call will be updating both mff_inventory & mff_store_inventory tables
	          // Lets lock the single row in mff_inventory table that corresponds to the sku passed in
	          // Lets lock the single row in mff_store_inventory table that corresponds to the sku & store passed in
	          
	          lockOnlineAndStoreInventory(pSkuId,pStoreId);
	          
	  	    //get mff inventory item and update sold & allocation count
	  	    lInvItem = getInventoryItemForUpdateNoLock (pSkuId, DEFAULT_LOCATION_ID);
	  	    //get mff store inventory item and update allocation count
	        lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);    	  
	      } else {
	  	    //get mff inventory item and update sold & allocation count
	  	    lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
	  	    //get mff store inventory item and update allocation count
	        lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);    	  
	      }
	      
	      //get mff inventory item and update allocation count
	      //MutableRepositoryItem lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
	      
	    //get mff store inventory item and update allocation count
	      //MutableRepositoryItem lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);
	      
	      //if store item or inventory item not found return an error message
	      if(lInvItem == null){
	        vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
	        throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
	      }else if(lStoreItem == null){
	        vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}", pSkuId, pStoreId);
	        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
	      }else if(pQty <=0){
	        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
	        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
	      }else{
	        long lAllocated = (long)lInvItem.getPropertyValue(PROPERTY_ALLOCATED);
	        
	        //decrement allocated
	        lInvItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated - pQty);
	        
	        vlogInfo("decrementStoreAllocatedForBopis: pSkuId: {0} Current Qty Allocated: {1} Qty Allocated Remaining: {2}",pSkuId, lAllocated, (lAllocated - pQty));
	        
	        updateItem(lInvItem);
	        
	        long lStoreAllocatedCount = (long)lStoreItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
	        lStoreItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocatedCount-pQty);
	        
	        vlogInfo("decrementStoreAllocatedForBopis: pSkuId: {0} pStoreId: {1} Current Store Allocated Qty: {2} Store Allocated Qty Remaining: {3} ",pSkuId ,pStoreId, lStoreAllocatedCount, (lStoreAllocatedCount-pQty));
	        //getRepository().updateItem(lStoreItem);
	        updateStoreInventoryItem(lStoreItem);
	      }
	    } catch (RepositoryException e) {
	      vlogError(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
	      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
	    }catch (SQLException e) {
	        vlogError(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
	        throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
		}catch (TransactionDemarcationException e) {
	      vlogError("Transaction Error while updating the sold & allocated count for skuId:{0}", pSkuId);
	      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
	    }finally {
	      try {
	        td.end();
	      }
	      catch (TransactionDemarcationException tde) {
	        if (isLoggingError()) {
	          logError(tde);
	        }
	      }
	    }
	    vlogDebug("Exiting decrementStoreAllocatedForBopis");
	    
	    return INVENTORY_STATUS_SUCCEED;
	  }
	 
	/**
	* This method will be called when a SKU is shipped by a store. This method
	 * decrements the allocated count and increments the shipped count from inventory item (mff_inventory table)
	* It also decrements the allocated count and increments the shipped count for storeInventory (mff_store_inventory table)
	*/
	public int incrementStoreShipped (String pSkuId, String pStoreId, long pQty) throws InventoryException{
	  
	  String lErrorMessage      = "incrementStoreShipped failed for SkuId: %s Error: %s";
    
	  vlogDebug("Entering incrementStoreShipped");
    
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      MutableRepositoryItem lInvItem=null;
      MutableRepositoryItem lStoreItem=null;
      if(isTurnOnHotfix()) {
          // This call will be updating both mff_inventory & mff_store_inventory tables
          // Lets lock the single row in mff_inventory table that corresponds to the sku passed in
          // Lets lock the single row in mff_store_inventory table that corresponds to the sku & store passed in
          
          lockOnlineAndStoreInventory(pSkuId,pStoreId);
          
  	    //get mff inventory item and update sold & allocation count
  	    lInvItem = getInventoryItemForUpdateNoLock (pSkuId, DEFAULT_LOCATION_ID);
  	    //get mff store inventory item and update allocation count
        lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);    	  
      } else {
  	    //get mff inventory item and update sold & allocation count
  	    lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
  	    //get mff store inventory item and update allocation count
        lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);    	  
      }
      
      //get mff inventory item and update Shipped & allocation count
     // MutableRepositoryItem lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
      //get mff store inventory item and update shipped & allocation count
     // MutableRepositoryItem lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);
      
      //if store item or inventory item not found return an error message
      if(lInvItem == null){
        vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
      }else if(lStoreItem == null){
        vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}",pSkuId,pStoreId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
      }else if(pQty <=0){
        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
      }else{
        long lShipped = (long)lInvItem.getPropertyValue(PROPERTY_SHIPPED);
        long lAllocated = (long)lInvItem.getPropertyValue(PROPERTY_ALLOCATED);
              
        lInvItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated - pQty);
        lInvItem.setPropertyValue(PROPERTY_SHIPPED, lShipped + pQty);
        updateItem(lInvItem);
        
        long lStoreAllocated = (long)lStoreItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
        long lStoreShipped = (long)lStoreItem.getPropertyValue(PROPERTY_STORE_SHIPPED);
        
        lStoreItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocated - pQty);
        lStoreItem.setPropertyValue(PROPERTY_STORE_SHIPPED, lStoreShipped + pQty);
        //getRepository().updateItem(lStoreItem);
        updateStoreInventoryItem(lStoreItem);
      }
    } catch (RepositoryException e) {
      vlogError(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
    }catch (SQLException e) {
        vlogError(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
	}catch (TransactionDemarcationException e) {
      vlogError("Transaction Error while updating the allocated & shipped count for skuId: {0}", pSkuId);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
    }finally {
      try {
        td.end();
      }
      catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }
    
    vlogDebug("Exiting incrementStoreShipped");
    
	  return INVENTORY_STATUS_SUCCEED;
	}
	
  /**
   * This Methods takes skuIds and StoreNos then builds the bopis inventory query
   * @param pSkuIds
   * @param pStoreNos
   * @return
   */
  public String getBopisQuery(String[] pSkuIds, List<String> pStoreNos){
    
    String inValues = "";
    StringBuffer strBuff = new StringBuffer();
    
    for (String skuId : pSkuIds){
      if(!skuId.isEmpty()){
        for(String lStoreId: pStoreNos){
          strBuff.append("'");
          strBuff.append(INVENTORY_PREFIX);
          strBuff.append(skuId);
          strBuff.append(getEnvironment().getStoreInvItemDelim());
          strBuff.append(lStoreId);
          strBuff.append("',");
        }
      }
    }
    
    inValues = strBuff.toString();
    if(inValues.length() > 1)
    //  Remove the trailing comma
    inValues = inValues.substring(0, inValues.length()-1);
    
    StringBuffer queryBuff = new StringBuffer();
    String query = "";
    queryBuff.append(SQL_QUERY_STORE_INVENTORY);
    queryBuff.append(inValues);
    queryBuff.append(") and stock_level > 0 and is_damaged=0");
    query = queryBuff.toString();
    vlogDebug("queryInventory : query is : {0}",query);
    return query;
  }
	
	public boolean isAcquireInventoryLocksPreCommitOrder() {
		return mAcquireInventoryLocksPreCommitOrder;
	}

	public void setAcquireInventoryLocksPreCommitOrder(boolean pAcquireInventoryLocksPreCommitOrder) {
		mAcquireInventoryLocksPreCommitOrder = pAcquireInventoryLocksPreCommitOrder;
	}
	
	public String getInsertSQL() {
		return insertSQL;
	}

	public void setInsertSQL(String insertSQL) {
		this.insertSQL = insertSQL;
	}

	public MFFCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(MFFCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	public MFFEnvironment getEnvironment() {
		return mEnvironment;
	}

	public void setEnvironment(MFFEnvironment pEnvironment) {
		mEnvironment = pEnvironment;
	}

	/**
	 * This method is used to check the existing item for BackInStock
	 * @param pProfileRepository
	 * @param pCatalogRefId
	 * @param pEmailAddress
	 * @param pProductId
	 * @return
	 * @throws RepositoryException 
	 */
	public boolean isBackInStockItemExists(MutableRepository pProfileRepository, String pCatalogRefId,
			String pEmailAddress, String pProductId) throws RepositoryException {
		boolean isExist = false;

		RepositoryView view = pProfileRepository.getView("backInStockNotifyItem");
		Object[] params;
		RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_DUPLICATE_BACK_IN_STOCK_ITEM);;
		params = new Object[] { pCatalogRefId, pEmailAddress, pProductId };

		RepositoryItem[] items = statement.executeQuery(view, params);

		isExist = (items != null) && (items.length > 0);

		return isExist;
	}

	/**
	 * This method is used to create the BackInStockItem for the OOS sku, product and email
	 * @param pProfileRepository
	 * @param pCatalogRefId
	 * @param pEmailAddress
	 * @param pProductId
	 * @param pCurrentSiteId
	 * @throws RepositoryException 
	 */
	public void createBackInStockNotifyItem(
			MutableRepository pProfileRepository, String pCatalogRefId,
			String pEmailAddress, String pProductId, String pCurrentSiteId) throws RepositoryException {
	    
	    MutableRepositoryItem item = pProfileRepository.createItem("backInStockNotifyItem");
	    
	    item.setPropertyValue("catalogRefId", pCatalogRefId);
	    item.setPropertyValue("emailAddress", pEmailAddress);
	    item.setPropertyValue("productId", pProductId);
	    item.setPropertyValue("siteId", pCurrentSiteId);
	    pProfileRepository.addItem(item);
		
	}

  /*
   * purchase inventory for the skus - Increase the sold count
   */
  @SuppressWarnings("rawtypes")
  public void decrementInventory(Order pLastOrder, String pOrderId, String pProfileId) {
    vlogInfo("CommitOrderFormHandler: decrementInventory: decrementInventory for order id ({0}) and profile id ({1})", pOrderId, pProfileId);
    try {
      vlogDebug(" CommitOrderFormHandler: decrementInventory: Will attempt to decrement the inventory for order:" + pOrderId);

      if (pLastOrder == null) {
        vlogError("CommitOrderFormHandler: decrementInventory: unable to decrement the inventory as the order is null!");
      } else {
        List ciList = pLastOrder.getCommerceItems();
        ListIterator ciIterator = ciList.listIterator();
        while (ciIterator.hasNext()) {
          MFFCommerceItemImpl ci = (MFFCommerceItemImpl) ciIterator.next();
          String skuId = ci.getCatalogRefId();
          long qty = ci.getQuantity();
          if(!ci.isGiftCard()){
            int retValue = purchase(skuId, qty);
  
            if (retValue < 1) {
              logInfo("CommitOrderFormHandler: decrementInventory: Insufficient inventory for Sku " + skuId);
            } else {
              if (isLoggingDebug()) {
                logDebug(" CommitOrderFormHandler: Purchased inventory for item " + skuId);
              }
            }
          }else{
            vlogDebug("CommitOrderFormHandler: GC Product, Skipping purchasing inventory for ci:{0}, order:{1}",ci.getId(),pLastOrder.getId());
          }
        }
      }
    } catch (InventoryException e) {
      if (isLoggingError()) {
        logError("An exception was caught while trying to increase QtySold for Order:" + pLastOrder.getId() + ".  The inventory amounts will be off");
      }
    }
  }
  
  protected void lockStoreInventory(String pStoreInventoryItemId) throws java.sql.SQLException {
    if (isLoggingDebug()) logDebug("lockStoreInventory - Getting lock for " + pStoreInventoryItemId);

    int retryCount = 0;
    int mostTries = getMaximumRetriesPerRowLock();
    int retryInterval = getMillisecondDelayBeforeLockRetry();
    SQLException lastException = null;
    String lock = DEFAULT_STORE_LOCK_NAME;

    Connection c = null;
    PreparedStatement ps = null;
    String sql = getStoreInventoryLockSql();

    int rowcount = 0;

    TransactionDemarcation td = new TransactionDemarcation();

    while (retryCount < mostTries) {
      try {
        td.begin(getTransactionManager());
        c = ((atg.adapter.gsa.GSARepository) getRepository()).getDataSource().getConnection();
        ps = c.prepareStatement(sql);
        ps.setString(1, lock);
        ps.setString(2, pStoreInventoryItemId);
       
        rowcount = ps.executeUpdate();
        lastException = null;
        break;
      } catch (TransactionDemarcationException tde) {
        if (isLoggingDebug()) logDebug("lockStoreInventory - This attemp to lock " + pStoreInventoryItemId + " failed: " + tde.getMessage());

        lastException = new SQLException(tde.getLocalizedMessage());
        retryCount++;
      } catch (SQLException s) {
        if (isLoggingDebug()) logDebug("lockStoreInventory - This attemp to lock " + pStoreInventoryItemId + " failed: " + s.getMessage());

        lastException = s;
        retryCount++;
      } finally {
        try {
          td.end();
        } catch (TransactionDemarcationException tde) {
          if (isLoggingError()) logError("lockStoreInventory - This attemp to lock " + pStoreInventoryItemId + " failed: " + tde.getLocalizedMessage());

          lastException = new SQLException(tde.getLocalizedMessage());
          retryCount++;
        }

        try {
          if (ps != null) {
            ps.close();
            ps = null;
          }
        } catch (SQLException e) {
          if (isLoggingError()) logError(e);

          lastException = e;
        }

        try {
          if (c != null) {
            c.close();
            c = null;
          }
        } catch (SQLException e) {
          if (isLoggingError()) logError(e);

          lastException = e;
        }
      }

      if (lastException != null) {
        try {
          Thread.sleep(retryInterval);
        } catch (InterruptedException i) {
          if (isLoggingDebug()) logDebug("lockStoreInventory - Thread was interrupted.", i);
          // just catch this and continue
        }
      }

    }

    if (lastException != null) throw lastException;
  }
  
  protected void lockOnlineAndStoreInventory(String pSkuId, String pStoreId) throws java.sql.SQLException {
	    if (isLoggingDebug()) logDebug("lockOnlineAndStoreInventory - Getting lock for sku " + pSkuId + " and store " + pStoreId);

	    int retryCount = 0;
	    int mostTries = getMaximumRetriesPerRowLock();
	    int retryInterval = getMillisecondDelayBeforeLockRetry();
	    SQLException lastException = null;
	    //String lock = DEFAULT_STORE_LOCK_NAME;

	    Connection c = null;
	    PreparedStatement ps = null;
	    
	    String sql = getOnlineAndStoreInventoryLockSql();
	    //String sql = "select * from mff_inventory mi, mff_store_inventory si where mi.catalog_ref_id=si.catalog_ref_id and mi.catalog_ref_id=? and si.store_id=? for update";

	    int rowcount = 0;
	    
	    

	    TransactionDemarcation td = new TransactionDemarcation();

	    while (retryCount < mostTries) {
	      try {
	        td.begin(getTransactionManager());
	        c = ((atg.adapter.gsa.GSARepository) getRepository()).getDataSource().getConnection();
	        ps = c.prepareStatement(sql);
	        ps.setString(1, pSkuId);
	        ps.setString(2, pStoreId);
	       
	        rowcount = ps.executeUpdate();
	        lastException = null;
	        break;
	      } catch (TransactionDemarcationException tde) {
	        if (isLoggingDebug()) logDebug("lockStoreInventory - This attemp to lock store & online inv for sku " + pSkuId + " and store " + pStoreId + " failed: " + tde.getMessage());

	        lastException = new SQLException(tde.getLocalizedMessage());
	        retryCount++;
	      } catch (SQLException s) {
	        if (isLoggingDebug()) logDebug("lockStoreInventory - This attemp to lock store & online inv for sku " + pSkuId + " and store " + pStoreId + " failed: " + s.getMessage());

	        lastException = s;
	        retryCount++;
	      } finally {
	        try {
	          td.end();
	        } catch (TransactionDemarcationException tde) {
	          if (isLoggingError()) logError("lockStoreInventory - This attemp to lock store & online inv for sku " + pSkuId + " and store " + pStoreId + " failed: " + tde.getLocalizedMessage());

	          lastException = new SQLException(tde.getLocalizedMessage());
	          retryCount++;
	        }

	        try {
	          if (ps != null) {
	            ps.close();
	            ps = null;
	          }
	        } catch (SQLException e) {
	          if (isLoggingError()) logError(e);

	          lastException = e;
	        }

	        try {
	          if (c != null) {
	            c.close();
	            c = null;
	          }
	        } catch (SQLException e) {
	          if (isLoggingError()) logError(e);

	          lastException = e;
	        }
	      }

	      if (lastException != null) {
	        try {
	          Thread.sleep(retryInterval);
	        } catch (InterruptedException i) {
	          if (isLoggingDebug()) logDebug("lockStoreInventory - Thread was interrupted.", i);
	          // just catch this and continue
	        }
	      }

	    }

	    if (lastException != null) throw lastException;
	  }
  
  protected void updateStoreInventoryItem(MutableRepositoryItem pItem) throws RepositoryException{
    
    try {
      MutableRepository rep = getRepository();

      // so when this gets commited, the lock field is not set anymore
      String lockPropertyName = getInventoryRowLockProperty();
      pItem.setPropertyValue(lockPropertyName, null);
      rep.updateItem(pItem);
    }
    catch(RepositoryException r) {
      throw r;
    }
  }

  public String getStoreInventoryLockSql() {
    return storeInventoryLockSql;
  }

  public void setStoreInventoryLockSql(String pStoreInventoryLockSql) {
    storeInventoryLockSql = pStoreInventoryLockSql;
  }
	public String getOnlineAndStoreInventoryLockSql() {
		return onlineAndStoreInventoryLockSql;
	}

	public void setOnlineAndStoreInventoryLockSql(String pOnlineAndStoreInventoryLockSql) {
		onlineAndStoreInventoryLockSql = pOnlineAndStoreInventoryLockSql;
	}

  public boolean isEnableBopisInventoryFix() {
    return enableBopisInventoryFix;
  }

  public void setEnableBopisInventoryFix(boolean pEnableBopisInventoryFix) {
    enableBopisInventoryFix = pEnableBopisInventoryFix;
  }  
}
