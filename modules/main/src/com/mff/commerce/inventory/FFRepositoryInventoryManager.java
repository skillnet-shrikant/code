package com.mff.commerce.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.locator.StoreLocatorTools;

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

public class FFRepositoryInventoryManager extends RepositoryInventoryManager implements FFInventoryManager{
  
  public static final String SQL_QUERY_INVENTORY_1 = "select a.inventory_id,a.catalog_ref_id,a.avail_status,a.stock_level,b.sold,b.allocated,b.shipped from ff_inventory a, ff_inventory_transaction b where a.inventory_id IN (";
  public static final String SQL_QUERY_INVENTORY_2 = ") AND a.inventory_id=b.inventory_id";

  public static final String SQL_BOPIS_QUERY_INVENTORY_1 = "select a.inventory_id, a.catalog_ref_id, 1000 avail_status, a.stock_level, 0 sold, b.allocated, b.shipped shipped from ff_store_inventory a, ff_store_inv_transaction b where a.inventory_id in (";
  public static final String SQL_BOPIS_QUERY_INVENTORY_2 = ") AND a.inventory_id=b.inventory_id AND a.is_damaged=0";
  
  public static final String BOPIS_ONLY_SQL_QUERY_INVENTORY_1 = "select 'inv-' || a.catalog_ref_id inventory_id, a.catalog_ref_id, 1000 avail_status, sum(a.stock_level) stock_level,0 sold,sum(b.allocated) allocated,sum(b.shipped) shipped from ff_store_inventory a, ff_store_inv_transaction b, atg_cata.mff_location ml where a.catalog_ref_id in (";
  public static final String BOPIS_ONLY_SQL_QUERY_INVENTORY_2 = ") AND a.inventory_id=b.inventory_id AND a.is_damaged=0 and ml.is_bopis_only=1 and ml.location_id=a.store_id group by 'inv-' || a.catalog_ref_id , a.catalog_ref_id";

  
  //public static final String SQL_QUERY_STORE_INVENTORY = "select * from ff_store_inventory where inventory_id IN (";
  public static final String SQL_QUERY_STORE_INVENTORY_1 = "select a.catalog_ref_id,a.store_id,a.stock_level,b.allocated,b.shipped from ff_store_inventory a, ff_store_inv_transaction b where a.inventory_id IN (";
  public static final String SQL_QUERY_STORE_INVENTORY_2 = ") AND a.inventory_id=b.inventory_id AND a.stock_level > 0 AND a.is_damaged=0";
 
  private MFFCatalogTools mCatalogTools;
  private MFFEnvironment  mEnvironment;

  // Flag determining if we should acquire inventory locks in the
  // preCommitOrder() method of EXTNCommitOrderFormHandler.
  private boolean mAcquireInventoryLocksPreCommitOrder;
  //private String insertSQL;
  public static final String RQL_QUERY_DUPLICATE_BACK_IN_STOCK_ITEM = "catalogRefId = ?0 AND emailAddress = ?1 AND productId = ?2";
  private static String DEFAULT_STORE_LOCK_NAME = "ffInventoryManager";
  private String storeInventoryLockSql;
  private String storeInvTransactionLockSql;
  private String onlineAndStoreInventoryLockSql;

  private boolean lockStoreInventory;
  private boolean lockStoreInvTransaction;
  private boolean turnOnHotfix;
  // Used in MFFOrdeManager.getStockLevelForSkusInOrder
  private boolean enableBopisInventoryFix;
  private boolean lockInventoryTransaction;
  private String lockInventoryTransactionSql;
  private StoreLocatorTools storeLocatorTools;
  
  public StoreLocatorTools getStoreLocatorTools() {
	  return storeLocatorTools;
  }
  public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
	  storeLocatorTools = pStoreLocatorTools;
  }
/**
   * 
   * @param pSkuIds
   * @return
   */
  private ArrayList<Map<Object, Object>> queryInventory(List<String> pSkuIds) {

    ArrayList<Map<Object, Object>> resultsList = new ArrayList<Map<Object, Object>>();

    Connection lConnection = null;
    DataSource lDataSource = ((GSARepository) getRepository()).getDataSource();
    ResultSet lResultSet = null;
    Statement lStatement = null;

    // String lSql = buildSqlQuery(pSkuId);
    String inValues = "";
    StringBuffer strBuff = new StringBuffer();

    for (String skuId : pSkuIds) {
      strBuff.append("'");
      strBuff.append(INVENTORY_PREFIX);
      strBuff.append(skuId);
      strBuff.append("',");
    }

    inValues = strBuff.toString();

    // Remove the trailing comma
    inValues = inValues.substring(0, inValues.length() - 1);
    StringBuffer queryBuff = new StringBuffer();
    String query = "";
    queryBuff.append(SQL_QUERY_INVENTORY_1);
    queryBuff.append(inValues);
    queryBuff.append(SQL_QUERY_INVENTORY_2);
    query = queryBuff.toString();
    vlogDebug("queryInventory : query is : {0}", query);
    try {
      lConnection = lDataSource.getConnection();
      lStatement = lConnection.createStatement();
      lResultSet = lStatement.executeQuery(query);
      while (lResultSet.next()) {

        HashMap<Object, Object> inventoryMap = new HashMap<Object, Object>();
        inventoryMap.put(PROPERTY_SKU_ID, lResultSet.getString("catalog_ref_id"));
        inventoryMap.put(PROPERTY_AVAILABILITY_STATUS, lResultSet.getString("avail_status"));
        
        vlogDebug("Stock From Table: {0} ", lResultSet.getLong("stock_level"));
        
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
  private ArrayList<Map<Object, Object>> queryStoreInventory(List<String> pSkuIds, String pStoreNumber) {
	    ArrayList<Map<Object, Object>> resultsList = new ArrayList<Map<Object, Object>>();

	    Connection lConnection = null;
	    DataSource lDataSource = ((GSARepository) getRepository()).getDataSource();
	    ResultSet lResultSet = null;
	    Statement lStatement = null;

	    // String lSql = buildSqlQuery(pSkuId);
	    String inValues = "";
	    StringBuffer strBuff = new StringBuffer();

	    for (String skuId : pSkuIds) {
	      strBuff.append("'");
	      strBuff.append(INVENTORY_PREFIX);
	      strBuff.append(skuId);
	      strBuff.append("-");
	      strBuff.append(pStoreNumber);
	      strBuff.append("',");
	    }

	    inValues = strBuff.toString();

	    // Remove the trailing comma
	    inValues = inValues.substring(0, inValues.length() - 1);
	    StringBuffer queryBuff = new StringBuffer();
	    String query = "";
	    queryBuff.append(SQL_BOPIS_QUERY_INVENTORY_1);
	    queryBuff.append(inValues);
	    queryBuff.append(SQL_BOPIS_QUERY_INVENTORY_2);
	    query = queryBuff.toString();
	    vlogDebug("queryStoreInventory : query is : {0}", query);
	    try {
	      lConnection = lDataSource.getConnection();
	      lStatement = lConnection.createStatement();
	      lResultSet = lStatement.executeQuery(query);
	      while (lResultSet.next()) {

	        HashMap<Object, Object> inventoryMap = new HashMap<Object, Object>();
	        inventoryMap.put(PROPERTY_SKU_ID, lResultSet.getString("catalog_ref_id"));
	        inventoryMap.put(PROPERTY_AVAILABILITY_STATUS, lResultSet.getString("avail_status"));
	        
	        vlogDebug("Stock From Table: {0} ", lResultSet.getLong("stock_level"));
	        
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
  private ArrayList<Map<Object, Object>> queryBopisOnlyInventory(List<String> pSkuIds) {

	    ArrayList<Map<Object, Object>> resultsList = new ArrayList<Map<Object, Object>>();

	    Connection lConnection = null;
	    DataSource lDataSource = ((GSARepository) getRepository()).getDataSource();
	    ResultSet lResultSet = null;
	    Statement lStatement = null;

	    // String lSql = buildSqlQuery(pSkuId);
	    String inValues = "";
	    StringBuffer strBuff = new StringBuffer();

	    for (String skuId : pSkuIds) {
	      strBuff.append("'");
	      //strBuff.append(INVENTORY_PREFIX);
	      strBuff.append(skuId);
	      strBuff.append("',");
	    }

	    inValues = strBuff.toString();

	    // Remove the trailing comma
	    inValues = inValues.substring(0, inValues.length() - 1);
	    StringBuffer queryBuff = new StringBuffer();
	    String query = "";
	    queryBuff.append(BOPIS_ONLY_SQL_QUERY_INVENTORY_1);
	    queryBuff.append(inValues);
	    queryBuff.append(BOPIS_ONLY_SQL_QUERY_INVENTORY_2);
	    query = queryBuff.toString();
	    vlogDebug("queryBopisOnlyInventory : query is : {0}", query);
	    try {
	      lConnection = lDataSource.getConnection();
	      lStatement = lConnection.createStatement();
	      lResultSet = lStatement.executeQuery(query);
	      while (lResultSet.next()) {

	        HashMap<Object, Object> inventoryMap = new HashMap<Object, Object>();
	        inventoryMap.put(PROPERTY_SKU_ID, lResultSet.getString("catalog_ref_id"));
	        inventoryMap.put(PROPERTY_AVAILABILITY_STATUS, lResultSet.getString("avail_status"));
	        
	        vlogDebug("Stock From Table: {0} ", lResultSet.getLong("stock_level"));
	        
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
   * This method will query the database to find availability of all the skus at
   * a particular store it calculates in stock quantity available for purchase
   * (instock = stocklevel - (allocated + shipped)) It also considers the
   * requested quantity and check if instock is greater than requested quantity
   * Any sku marked as damaged for a particular store will be excluded
   * 
   * @param pSkus
   * @param pStoreNos
   * @return
   */
  private Map<Object, List<String>> queryStoreInventory(Map<String, Long> pSkus, List<String> pStoreNos) {

    // result set
    HashMap<Object, List<String>> inventoryMap = new HashMap<Object, List<String>>();

    // db connection parameters
    Connection lConnection = null;
    DataSource lDataSource = ((GSARepository) getRepository()).getDataSource();
    ResultSet lResultSet = null;
    Statement lStatement = null;

    String[] skuIds = new String[pSkus.size()];
    pSkus.keySet().toArray(skuIds);

    // construct and get bopis query
    String bopisQuery = getBopisQuery(skuIds, pStoreNos);

    try {
      // create connection
      lConnection = lDataSource.getConnection();
      lStatement = lConnection.createStatement();
      lResultSet = lStatement.executeQuery(bopisQuery);

      // iterate through the result set
      while (lResultSet.next()) {
        // get data values
        String lSkuId = lResultSet.getString("catalog_ref_id").toString();
        String lStoreId = lResultSet.getString("store_id").toString();
        long lStockLevel = lResultSet.getLong("stock_level");
        long lAllocated = lResultSet.getLong("allocated");
        long lShipped = lResultSet.getLong("shipped");

        // calculate the current stock available for purchase.
        long lInStock = lStockLevel - (lAllocated + lShipped);

        // get the customer requested quantity from the cart
        long reqQty = pSkus.get(lSkuId);
        // if available stock for purchase is greater than requested quantity,
        // then only add this store to list and perform additional processing
        if (lInStock >= reqQty) {
          vlogDebug("queryStoreInventory: Requested Quantity: {0} of the sku:{1} is available at store:{2}", reqQty, lSkuId, lStoreId);

          // check if map already contains the store,
          // if yes, retrieve the skuList and add this new sku
          // else, create a new entry in the map
          if (inventoryMap.get(lStoreId) == null) {
            vlogDebug("queryStoreInventory: Adding sku:{0} to available list of store:{1}", lSkuId, lStoreId);
            List<String> availSkuList = new ArrayList<String>();
            availSkuList.add(lSkuId);
            inventoryMap.put(lStoreId, availSkuList);
          } else {
            List<String> availSkuList = inventoryMap.get(lStoreId);
            availSkuList.add(lSkuId);
            inventoryMap.put(lStoreId, availSkuList);
          }
        } else {
          vlogDebug("queryStoreInventory: Requested Quantity: {0} of the sku:{1} is not available at store:{2}", reqQty, lSkuId, lStoreId);
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

    // print Inventory Map for debugging purposes
    if (inventoryMap != null) {
      for (Object storeId : inventoryMap.keySet()) {
        if (inventoryMap.get(storeId) != null) {
          String skuList = "";
          for (String skuId : inventoryMap.get(storeId))
            skuList += skuId + ",";
          vlogDebug("storeId:{0}, skuList:{1}", storeId, skuList);
        }
      }
    }
    return inventoryMap;
  }
  
  /**
   * This Methods takes skuIds and StoreNos then builds the bopis inventory query
   * @param pSkuIds
   * @param pStoreNos
   * @return
   */
  public String getBopisQuery(String[] pSkuIds, List<String> pStoreNos) {

    String inValues = "";
    StringBuffer strBuff = new StringBuffer();

    for (String skuId : pSkuIds) {
      if (!skuId.isEmpty()) {
        for (String lStoreId : pStoreNos) {
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
    if (inValues.length() > 1)
      // Remove the trailing comma
      inValues = inValues.substring(0, inValues.length() - 1);

    StringBuffer queryBuff = new StringBuffer();
    String query = "";
    queryBuff.append(SQL_QUERY_STORE_INVENTORY_1);
    queryBuff.append(inValues);
    //queryBuff.append(") and stock_level > 0 and is_damaged=0");
    queryBuff.append(SQL_QUERY_STORE_INVENTORY_2);
    query = queryBuff.toString();
    vlogDebug("queryInventory : query is : {0}", query);
    return query;
  }

  /**
   * 
   * @param pSkuId
   * @return
   */
  private Map<Object, Object> queryInventory(String pSkuId) {

    vlogDebug("Entering queryInventory : pSkuId - {0}", pSkuId);
    List<String> skuIdsList = new ArrayList<String>();
    skuIdsList.add(pSkuId);

    List<Map<Object, Object>> skuResultsList = queryInventory(skuIdsList);

    return (skuResultsList != null && skuResultsList.size() > 0) ? skuResultsList.get(0) : null;
  }
  
  private Map<Object, Object> queryStoreInventory(String pSkuId, String pStoreNumber) {

	    vlogDebug("Entering queryStoreInventory : pSkuId - {0} pStoreNumber - {1}", pSkuId, pStoreNumber);
	    List<String> skuIdsList = new ArrayList<String>();
	    skuIdsList.add(pSkuId);

	    List<Map<Object, Object>> skuResultsList = queryStoreInventory(skuIdsList, pStoreNumber);

	    return (skuResultsList != null && skuResultsList.size() > 0) ? skuResultsList.get(0) : null;
  }  
  /**
   * Gets the current stock level for an item from the local ATG inventory 
   * repository.  This call will:
   * - subtract out items that have been sold
   *  
   * @Param   pSkuId  - skuId
   * @Return Stock level for the item, or null if there is an error
   **/
  @Override
  public StockLevel querySkuStockLevel(String pSkuId) throws InventoryException {

    long invStockLevel = 0;
    long sold = 0;
    long shipped = 0;
    long allocated = 0;
    String lErrorMessage = "querySkuStockLevel failed for SkuId: %s Error: %s";

    Map<Object, Object> invMap;
    try {
      invMap = queryInventory(pSkuId);
      if (invMap != null && invMap.size() > 0) {
        StockLevel stockLevel = new StockLevel();

        invStockLevel = (Long) invMap.get(PROPERTY_STOCK_LEVEL);
        vlogDebug("SkuID: {0} Stock Level:{1}", pSkuId, invStockLevel);

        sold = (Long) invMap.get(PROPERTY_SOLD);
        shipped = (Long) invMap.get(PROPERTY_SHIPPED);
        allocated = (Long) invMap.get(PROPERTY_ALLOCATED);

        // rule to calculate the inventory
        // Updated the below LOGIC to fix the issue with NEGATIVE Inventories

        long invSoldAllocatedShipped = 0;
        invSoldAllocatedShipped = sold + allocated + shipped;

        if (invStockLevel > 0) {
          if (invSoldAllocatedShipped >= 0) {
            invStockLevel = invStockLevel - invSoldAllocatedShipped;
          } else {
            invStockLevel = invStockLevel + invSoldAllocatedShipped;
          }
        } else {
          invStockLevel = 0;
        }
        
/*        if(bIncludeBopisOnlyChecks) {
        	if(invStockLevel <=0) {
        		stockLevel.setStoreAvailability(false);
        		List<String> skuIds = new ArrayList<String>();
        		skuIds.add(pSkuId);
        		List<Map<Object, Object>> bopisOnlySkuInvMap = queryBopisOnlyInventory(skuIds);
        		for (Map<Object, Object> bopisOnlyInvMap : bopisOnlySkuInvMap) {
        			long bopisInvStockLevel = (Long) bopisOnlyInvMap.get(PROPERTY_STOCK_LEVEL);
        			long bopisSold = (Long) bopisOnlyInvMap.get(PROPERTY_SOLD);
        			long bopisShipped = (Long) bopisOnlyInvMap.get(PROPERTY_SHIPPED);
        			long bopisAllocated = (Long) bopisOnlyInvMap.get(PROPERTY_ALLOCATED);

        			//long bopisInvStockLevel = 0;
        			long bopisInvSoldAllocatedShipped = bopisSold + bopisAllocated + bopisShipped;
        			invStockLevel = bopisInvStockLevel - bopisInvSoldAllocatedShipped;
        			stockLevel.setStoreAvailability(true);            	
        		}
        	}
        }*/
        // invStockLevel = invStockLevel - (sold+allocated+shipped);

        vlogDebug("SkuID: {0} Updated Stock Level :{1}", pSkuId, invStockLevel);
        stockLevel.setSkuId((String) invMap.get(PROPERTY_SKU_ID));
        stockLevel.setStockLevel(invStockLevel);
        stockLevel.setSold((long) invMap.get(PROPERTY_SOLD));
        stockLevel.setShipped((long) invMap.get(PROPERTY_SHIPPED));

        vlogDebug("querySkuStockLevel : - {0}", stockLevel.toString());

        return stockLevel;
      }
    } catch (Exception e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "Exception"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Exception"), e);
    }

    return null;
  }
  
  public StockLevel queryStoreSkuStockLevel(String pSkuId, String pBopisStore) throws InventoryException {

	    long invStockLevel = 0;
	    long sold = 0;
	    long shipped = 0;
	    long allocated = 0;
	    String lErrorMessage = "querySkuStoreStockLevel failed for SkuId: %s Error: %s";

	    Map<Object, Object> invMap;
	    try {
	      invMap = queryStoreInventory(pSkuId, pBopisStore);
	      if (invMap != null && invMap.size() > 0) {
	        StockLevel stockLevel = new StockLevel();

	        invStockLevel = (Long) invMap.get(PROPERTY_STOCK_LEVEL);
	        vlogDebug("SkuID: {0} Stock Level:{1}", pSkuId, invStockLevel);

	        sold = (Long) invMap.get(PROPERTY_SOLD);
	        shipped = (Long) invMap.get(PROPERTY_SHIPPED);
	        allocated = (Long) invMap.get(PROPERTY_ALLOCATED);

	        // rule to calculate the inventory
	        // Updated the below LOGIC to fix the issue with NEGATIVE Inventories

	        long invSoldAllocatedShipped = 0;
	        invSoldAllocatedShipped = sold + allocated + shipped;

	        if (invStockLevel > 0) {
	          if (invSoldAllocatedShipped >= 0) {
	            invStockLevel = invStockLevel - invSoldAllocatedShipped;
	          } else {
	            invStockLevel = invStockLevel + invSoldAllocatedShipped;
	          }
	        } else {
	          invStockLevel = 0;
	        }

	        // invStockLevel = invStockLevel - (sold+allocated+shipped);

	        vlogDebug("SkuID: {0} Updated Stock Level :{1}", pSkuId, invStockLevel);
	        stockLevel.setSkuId((String) invMap.get(PROPERTY_SKU_ID));
	        stockLevel.setStockLevel(invStockLevel);
	        stockLevel.setSold((long) invMap.get(PROPERTY_SOLD));
	        stockLevel.setShipped((long) invMap.get(PROPERTY_SHIPPED));

	        vlogDebug("querySkuStoreStockLevel : - {0}", stockLevel.toString());

	        return stockLevel;
	      }
	    } catch (Exception e) {
	      vlogError(e,String.format(lErrorMessage, pSkuId, "Exception"));
	      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Exception"), e);
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
  public Map<String, StockLevel> querySkusStockLevel(List<String> pSkuIds, boolean bIncludeBopisOnlyChecks) throws InventoryException {

    Map<String, StockLevel> resultsMap = new HashMap<String, StockLevel>();
    String lErrorMessage = "querySkusStockLevel failed for SkuIds: %s Error: %s";
    try {
      List<Map<Object, Object>> skuInvMap = queryInventory(pSkuIds);

      // for (int i=0; i < invMap.size(); i++)
      for (Map<Object, Object> invMap : skuInvMap) {
        StockLevel stockLevel = new StockLevel();
        String skuId = (String) invMap.get(PROPERTY_SKU_ID);

        long invStockLevel = (Long) invMap.get(PROPERTY_STOCK_LEVEL);
        long sold = (Long) invMap.get(PROPERTY_SOLD);
        long shipped = (Long) invMap.get(PROPERTY_SHIPPED);
        long allocated = (Long) invMap.get(PROPERTY_ALLOCATED);

        long invSoldAllocatedShipped = 0;
        invSoldAllocatedShipped = sold + allocated + shipped;

        if (invStockLevel > 0) {
          if (invSoldAllocatedShipped >= 0) {
            invStockLevel = invStockLevel - invSoldAllocatedShipped;
          } else {
            invStockLevel = invStockLevel + invSoldAllocatedShipped;
          }
        } else {
          invStockLevel = 0;
        }
        
        if(bIncludeBopisOnlyChecks) {
        	if(invStockLevel <=0) {
        		stockLevel.setStoreAvailability(false);
        		List<String> skuIds = new ArrayList<String>();
        		skuIds.add(skuId);
        		List<Map<Object, Object>> bopisOnlySkuInvMap = queryBopisOnlyInventory(skuIds);
        		for (Map<Object, Object> bopisOnlyInvMap : bopisOnlySkuInvMap) {
        			long bopisInvStockLevel = (Long) bopisOnlyInvMap.get(PROPERTY_STOCK_LEVEL);
        			long bopisSold = (Long) bopisOnlyInvMap.get(PROPERTY_SOLD);
        			long bopisShipped = (Long) bopisOnlyInvMap.get(PROPERTY_SHIPPED);
        			long bopisAllocated = (Long) bopisOnlyInvMap.get(PROPERTY_ALLOCATED);

        			//long bopisInvStockLevel = 0;
        			long bopisInvSoldAllocatedShipped = bopisSold + bopisAllocated + bopisShipped;
        			invStockLevel = bopisInvStockLevel - bopisInvSoldAllocatedShipped;
        			if(invStockLevel >0) {
        				stockLevel.setStoreAvailability(true);
        			}
        			            	
        		}
        	}
        }

        stockLevel.setSkuId((String) invMap.get(PROPERTY_SKU_ID));
        stockLevel.setStockLevel(invStockLevel);

        vlogDebug("querySkusStockLevel : - {0}", stockLevel.toString());

        resultsMap.put(skuId, stockLevel);
      }
    } catch (Exception e) {
      vlogError(String.format(lErrorMessage, pSkuIds, "Exception"));
      throw new InventoryException(String.format(lErrorMessage, pSkuIds, "Exception"), e);
    }
    return resultsMap;
  }
  
  /**
   * Purchase inventory for the skus - Increase the sold count
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
  
  /**
   * Increments the sold count on an item when an order is submitted.
   *  
   * @Param  pSkuId  - skuId
   * @Param  pQuantity   - The number of items being purchased.
   * @Return 1 if successful, -1 if unsuccessful.
   **/
  public int purchase(String pSkuId, long pQuantity) throws InventoryException {

    vlogDebug("Inside purchase: " + pQuantity + " of skuId " + pSkuId);
    int iRetValue = INVENTORY_STATUS_SUCCEED;
    TransactionDemarcation td = new TransactionDemarcation();
    String lErrorMessage = "purchase failed for SkuId: %s Qty: %d Error: %s";

    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Increase the quantity sold for this sku
      iRetValue = decreaseStockLevel(pSkuId, pQuantity);
    } catch (TransactionDemarcationException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"), e);
    } finally {
      try {
        td.end();
      } catch (TransactionDemarcationException e) {
        vlogError(e,String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"));
        throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Transaction Error"), e);
      }
    }

    return iRetValue;
  }
  
  /**
   * Increments the sold count on an item when an order is submitted.  This is equivalent
   * to a purchase.
   *  
   * @Param  pSkuId  - skuId
   * @Param  pQuantity   - The number of items being purchased.
   * @Return 1 if successful, -1 if unsuccessful.
   **/
  @Override
  public int decreaseStockLevel(String pSkuId, long pQuantity) throws InventoryException {
    int returnStatus = INVENTORY_STATUS_SUCCEED;
    vlogDebug("Start of decreaseStockLevel.  Will increase Qty Sold for SkuId: " + pSkuId + " by: " + pQuantity);
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      //MutableRepositoryItem item = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
      RepositoryItem item = getInventoryItem(pSkuId, DEFAULT_LOCATION_ID);
      
      MutableRepositoryItem itemTransaction = getInventoryTransactionItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);

      if (item != null && itemTransaction != null) {
        // This call seems to always get the live, un-cached, DB value:
        long lQtySold = ((Long) itemTransaction.getPropertyValue(PROPERTY_SOLD)).longValue();
        long lQtyAllocated = ((Long) itemTransaction.getPropertyValue(PROPERTY_ALLOCATED)).longValue();
        long lQtyShipped = ((Long) itemTransaction.getPropertyValue(PROPERTY_SHIPPED)).longValue();
        long lInventoryInStock = ((Long) item.getPropertyValue(PROPERTY_STOCK_LEVEL)).longValue();
        long lAvailableStock = lInventoryInStock - (lQtySold + lQtyAllocated + lQtyShipped);

        // long lInventoryInStock = getInventoryInStock (item);

        if (lAvailableStock < pQuantity) {
                	returnStatus = INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
                vlogInfo("INVENTORY OVERSOLD : The inventory for SkuId: {0} has been oversold", pSkuId);
                return returnStatus;
        } else {
          logInfo("decreaseStockLevel: SkuId:" + pSkuId + ", Qty Sold is being incremented from:" + lQtySold + " to " + (lQtySold + pQuantity));
        }

        // Update the QtySold property regardless
        itemTransaction.setPropertyValue(PROPERTY_SOLD, lQtySold + pQuantity);
        getRepository().updateItem(itemTransaction);

        if (isLoggingDebug()) {
          logDebug("----------------------------------------------------------------------------");
          logDebug("In decreaseStockLevel for SkuId:" + pSkuId);
          logDebug("Inventory in Stock   :" + lInventoryInStock);
          logDebug("QtySold before     :" + lQtySold);
          logDebug("Number sold now    :" + pQuantity);
          logDebug("New QtySold total  :" + (lQtySold + pQuantity));
          logDebug("----------------------------------------------------------------------------");
        }
      }
    } catch (RepositoryException e) {
      vlogError(e,"RepositoryException caught in FFRepositoryInventoryManager.decreaseStockLevel.  The QtySold for pSkuId:" + pSkuId + " could not be incremented.");
      throw new InventoryException("Could not increment Qty Sold for pSkuId:" + pSkuId, e);
    } catch (TransactionDemarcationException e) {
      vlogError(e,"TransactionDemarcationException caught in FFRepositoryInventoryManager.decreaseStockLevel.  Could not increment QtySold for Sku:" + pSkuId);
      throw new InventoryException("Could not increment QtySold for Sku:" + pSkuId, e);
    } finally {
      try {
        td.end();
      } catch (TransactionDemarcationException tde) {
        vlogError(tde,"TransactionDemarcationException caught in FFRepositoryInventoryManager.decreaseStockLevel.  Could not increment QtySold for Sku:" + pSkuId + ".");
      }
    }
    vlogDebug("End of FFRepositoryInventoryManager.decreaseStockLevel for SkuId Id:" + pSkuId);
    return returnStatus;
  }
  
  /**
   * Decrements the sold count on an item when an order is cancelled.  
   *  
   * @Param  pSkuId  - skuId
   * @Param  pQuantity   - The number of items being cancelled.
   * @Return 1 if successful, -1 if unsuccessful.
   **/
   public int increaseStockLevel(String pSkuId, long pQuantity) throws InventoryException{
	   
	   if(pSkuId != null && pSkuId.startsWith("FRAUD")) {
		   String[] fraudSku = pSkuId.split("-");
		   String skuId=null;
		   String storeId=null;
		   if(fraudSku.length == 3) {
			   storeId=fraudSku[1];
			   skuId=fraudSku[2];
		   }
		   
		   return fraudRejectStoreAllocated(skuId, storeId, pQuantity);
	   }
     long lQtySold      = 0;
     long lQtySoldUpdated     = 0;   
     String lErrorMessage     = "increaseStockLevel failed for SkuId: %s Qty: %d Error: %s";
     
     vlogDebug("Start increaseStockLevel for pSkuId: " + pSkuId + " Qty: " + pQuantity);
     TransactionDemarcation td = new TransactionDemarcation();
     try {
       td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
       MutableRepositoryItem itemTransaction = getInventoryTransactionItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
       // Check that we got an inventory record
       if (itemTransaction == null) {
         vlogError(String.format(lErrorMessage, pSkuId, pQuantity, "Not Found"));
         throw new InventoryException(String.format(lErrorMessage, pSkuId, pQuantity, "Not Found"));
       }
       
       lQtySold = ((Long) itemTransaction.getPropertyValue(PROPERTY_SOLD)).longValue();
       
       // subtract the number of items been cancelled from sold
       lQtySoldUpdated = lQtySold - pQuantity;
       
       vlogInfo("increaseStockLevel: pSkuId: {0} Qty Sold: {1} Qty Sold Remaining: {2}",pSkuId, lQtySold, lQtySoldUpdated);
       // Update Inventory
       itemTransaction.setPropertyValue(PROPERTY_SOLD, lQtySoldUpdated);
       getRepository().updateItem(itemTransaction);
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
     vlogDebug("               Quantity: " + pQuantity);   
     vlogDebug("           Initial sold: " + lQtySold);
     vlogDebug("           Updated sold: " + lQtySoldUpdated);
     vlogDebug ("----------------------------------------------------------------------------");
     
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
  public int setStoreDamaged(String pSkuId, String pStoreNo, boolean isDamaged) throws InventoryException {

    vlogDebug("Entering setStoreDamaged : pSkuId - {0}, pStoreNo - {1}, pIsDamaged - {2}", pSkuId, pStoreNo, isDamaged);
    String lErrorMessage = "setStoreDamaged failed for SkuId: %s Store: %s pStatus: %d Error: %s";
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      // Update Store Inventory
      MutableRepositoryItem lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreNo);
      if (lStoreItem == null) {
        vlogError("No sku inventory exists for skuId - {0} for store - {1}", pSkuId, pStoreNo);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, pStoreNo, isDamaged, "Store SkuId Not Found"));
      }
      lStoreItem.setPropertyValue(PROPERTY_STORE_DAMAGED, isDamaged);
      // getRepository().updateItem(lStoreItem);
      updateStoreInventoryItem(lStoreItem);
    } catch (RepositoryException e) {
      vlogError(e,"Could not update the damaged");
      throw new InventoryException("Could not update the damaged", e);
    } catch (TransactionDemarcationException e) {
      vlogError(e,"Could not update the damged");
      throw new InventoryException("Could not update the damaged", e);
    } finally {
      try {
        td.end();
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }

    vlogDebug("Exiting setStoreDamaged");

    return INVENTORY_STATUS_SUCCEED;
  }
  
  /**
   * Get the availability status for a list of stores.
   *  
   * @Param  pSkuId  - skuId
   * @Param  pStoreNos - A list of store numbers for which we need inventory
   * @Return A list of Store availability (Store number and the store status)
   **/
  @Override
  public List<StoreAvailability> queryStoreAvailability(String pSkuId, List<String> pStoreNos) throws InventoryException {

    vlogDebug("Entering queryStoreAvailability : pSkuId - {0}, pStoreNos - {1}", pSkuId, pStoreNos);
    List<StoreAvailability> storeAvList = new ArrayList<StoreAvailability>();
    String lErrorMessage = "queryStoreAvailability failed for SkuId: %s Error: %s";
    String[] idsArr = new String[pStoreNos.size()];
    for (int i = 0; i < pStoreNos.size(); i++) {
      idsArr[i] = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreNos.get(i);
    }
    vlogDebug("IdsArray : - {0}", Arrays.toString(idsArr));
    try {
      RepositoryItem[] storeItems = getRepository().getItems(idsArr, ITEM_DESC_STORE_INVENTORY);

      if (storeItems == null)
        vlogError("Stores not Found");
      else
        vlogDebug("Store Inventory Result Set Size:{0}", storeItems.length);

      for (RepositoryItem storeItem : storeItems) {
        StoreAvailability storeAv = new StoreAvailability();
        storeAv.setSkuId(pSkuId);
        storeAv.setStoreNo((String) storeItem.getPropertyValue(PROPERTY_STORE_ID));
        long storeStockLevel = (long) storeItem.getPropertyValue(PROPERTY_STORE_STOCK_LEVEL);
        RepositoryItem storeTransactionItem = getRepository().getItem(storeItem.getRepositoryId(), "storeInventoryTransaction");
        long storeInStockForPurchase = 0;
        if (storeTransactionItem != null) {
          long storeAllocated = (long) storeTransactionItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
          long storeShipped = (long) storeTransactionItem.getPropertyValue(PROPERTY_STORE_SHIPPED);
          storeInStockForPurchase = storeStockLevel - (storeAllocated + storeShipped);
        }

        storeAv.setStoreStockLevel(storeInStockForPurchase);
        // storeAv.setStoreStockLevel((long)storeItem.getPropertyValue(PROPERTY_STORE_STOCK_LEVEL));
        storeAv.setAvailabilityStatus(STOCK_STATUS_AVAILABLE);
        storeAvList.add(storeAv);
        vlogDebug("queryStoreAvailability : - {0}", storeAv);
      }
    } catch (RepositoryException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "Repository Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
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
  public Map<String, StockLevel> queryStoreAvailability(List<String> pSkuIds, String pStoreNo) throws InventoryException {

    vlogDebug("Entering queryStoreAvailability : pSkuIds - {0}, pStoreNo - {1}", pSkuIds, pStoreNo);
    Map<String, StockLevel> resultsMap = new HashMap<String, StockLevel>();
    String lErrorMessage = "queryStoreAvailability failed for SkuId: %s Error: %s";

    String[] idsArr = new String[pSkuIds.size()];
    for (int i = 0; i < pSkuIds.size(); i++) {
      idsArr[i] = INVENTORY_PREFIX + pSkuIds.get(i) + getEnvironment().getStoreInvItemDelim() + pStoreNo;
    }
    vlogDebug("IdsArray : - {0}", Arrays.toString(idsArr));
    try {
      RepositoryItem[] storeItems = getRepository().getItems(idsArr, ITEM_DESC_STORE_INVENTORY);

      if (storeItems == null)
        vlogError("Stores not Found");
      else
        vlogDebug("Store Inventory Result Set Size:{0}", storeItems.length);

      for (RepositoryItem storeItem : storeItems) {
        StockLevel storeAv = new StockLevel();
        String skuId = (String) storeItem.getPropertyValue(PROPERTY_SKU_ID);
        storeAv.setSkuId(skuId);

        long storeStockLevel = (long) storeItem.getPropertyValue(PROPERTY_STORE_STOCK_LEVEL);
        
        long storeInStockForPurchase = 0;
        RepositoryItem storeTransactionItem = getRepository().getItem(storeItem.getRepositoryId(), ITEM_DESC_STORE_INVENTORY_TRANSACTION);
        if (storeTransactionItem != null) {
          long storeAllocated = (long) storeTransactionItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
          long storeShipped = (long) storeTransactionItem.getPropertyValue(PROPERTY_STORE_SHIPPED);
          storeInStockForPurchase = storeStockLevel - (storeAllocated + storeShipped);
        }

        storeAv.setStockLevel(storeInStockForPurchase);

        resultsMap.put(skuId, storeAv);
        vlogDebug("queryStoreAvailability : - {0}", storeAv);
      }
    } catch (RepositoryException e) {
      vlogError(e,String.format(lErrorMessage, pStoreNo, "Repository Error"));
      throw new InventoryException(String.format(lErrorMessage, pStoreNo, "Repository Error"), e);
    }
    return resultsMap;
  }
  //public int incrementBopisOnlyStoreAllocated(String pSkuId, String pStoreId, long pQty, boolean bUpdateOnlyStoreTables) throws InventoryException {
  public int incrementStoreShipped (String pSkuId, String pStoreId, long pQty) throws InventoryException{
	return incrementBopisOnlyStoreShipped(pSkuId, pStoreId, pQty, false);  
  }
  /**
   * This method will be called when a SKU is shipped by a store. This method
   * decrements the allocated count and increments the shipped count from inventory item 
   * It also decrements the allocated count and increments the shipped count for storeInventory
   */
  public int incrementBopisOnlyStoreShipped (String pSkuId, String pStoreId, long pQty, boolean bUpdateOnlyStoreTables) throws InventoryException{
   
   String lErrorMessage      = "incrementStoreShipped failed for SkuId: %s Error: %s";
   
   vlogDebug("Entering incrementStoreShipped");
   
   TransactionDemarcation td = new TransactionDemarcation();
   try {
     td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

     MutableRepositoryItem lInvTransactionItem=null;
     MutableRepositoryItem lStoreTransactionItem=null;
     
     if(isTurnOnHotfix()) {
         // This call will be updating both mff_inventory & mff_store_inventory tables
         // Lets lock the single row in mff_inventory table that corresponds to the sku passed in
         // Lets lock the single row in mff_store_inventory table that corresponds to the sku & store passed in
       String lId = INVENTORY_PREFIX + pSkuId;
       String lStoreId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;
       
       lockOnlineAndStoreInventory(lId,lStoreId);
         
       //get mff inventory item and update sold & allocation count
       //lInvItem = getInventoryItemForUpdateNoLock (pSkuId, DEFAULT_LOCATION_ID);
       //get mff store inventory item and update allocation count
       //lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);
       lInvTransactionItem = getInventoryTransactionItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);  
         
       lStoreTransactionItem = getStoreInventoryTransactionItemForUpdateNoLock(pSkuId, pStoreId);
     } else {
       //get mff inventory item and update sold & allocation count
       //lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
       //get mff store inventory item and update allocation count
       //lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);
       
       lInvTransactionItem = getInventoryTransactionItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
       
       lStoreTransactionItem = getStoreInventoryTransactionItemForUpdate(pSkuId, pStoreId);
     }
     
     //if store item or inventory item not found return an error message
     if(lInvTransactionItem == null){
       vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
       throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
     }else if(lStoreTransactionItem == null){
       vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}",pSkuId,pStoreId);
       throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
     }else if(pQty <=0){
       vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
       throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
     }else{
    	 if(!bUpdateOnlyStoreTables) {
    		 long lShipped = (long)lInvTransactionItem.getPropertyValue(PROPERTY_SHIPPED);
    		 long lAllocated = (long)lInvTransactionItem.getPropertyValue(PROPERTY_ALLOCATED);

    		 lInvTransactionItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated - pQty);
    		 lInvTransactionItem.setPropertyValue(PROPERTY_SHIPPED, lShipped + pQty);
    		 getRepository().updateItem(lInvTransactionItem);
    	 }
       
       long lStoreAllocated = (long)lStoreTransactionItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
       long lStoreShipped = (long)lStoreTransactionItem.getPropertyValue(PROPERTY_STORE_SHIPPED);
       
       lStoreTransactionItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocated - pQty);
       lStoreTransactionItem.setPropertyValue(PROPERTY_STORE_SHIPPED, lStoreShipped + pQty);
       //getRepository().updateItem(lStoreItem);
       //updateStoreInventoryItem(lStoreItem);
       getRepository().updateItem(lStoreTransactionItem);
     }
   } catch (RepositoryException e) {
     vlogError(e,String.format(lErrorMessage, pSkuId, "Repository Error"));
     throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"),e);
   }catch (SQLException e) {
       vlogError(e,String.format(lErrorMessage, pSkuId, "SQL Error"));
       throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"),e);
   }catch (TransactionDemarcationException e) {
     vlogError(e,"Transaction Error while updating the allocated & shipped count for skuId: {0}", pSkuId);
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
  
  public int incrementStoreAllocated(String pSkuId, String pStoreId, long pQty) throws InventoryException {
	  return incrementBopisOnlyStoreAllocated (pSkuId, pStoreId, pQty, false);
  }
  
  /**
   * This method will be called when a SKU is allocated to a store. This method
    * decrements the sold count and increments the allocated count from inventory item(mff_inventory table)
   * It also increments allocated count for storeInventory(mff_store_inventory table)
   */
  public int incrementBopisOnlyStoreAllocated(String pSkuId, String pStoreId, long pQty, boolean bUpdateOnlyStoreTables) throws InventoryException {

    String lErrorMessage = "incrementStoreAllocated failed for SkuId: %s Error: %s";

    vlogDebug("Entering incrementStoreAllocated");
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      MutableRepositoryItem lInvTransactionItem = null;
      MutableRepositoryItem lStoreTransactionItem = null;

      if (isTurnOnHotfix()) {
        // This call will be updating both mff_inventory & mff_store_inventory
        // tables
        // Lets lock the single row in mff_inventory table that corresponds to
        // the sku passed in
        // Lets lock the single row in mff_store_inventory table that
        // corresponds to the sku & store passed in

        String lId = INVENTORY_PREFIX + pSkuId;
        String lStoreId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;

        lockOnlineAndStoreInventory(lId, lStoreId);

        // get mff inventory item and update sold & allocation count
        // lInvItem = getInventoryItemForUpdateNoLock (pSkuId,
        // DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        // lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);

        lInvTransactionItem = getInventoryTransactionItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);

        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdateNoLock(pSkuId, pStoreId);
      } else {
        // get mff inventory item and update sold & allocation count
        // lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        // lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);

        lInvTransactionItem = getInventoryTransactionItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);

        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdate(pSkuId, pStoreId);
      }

      // if store item or inventory item not found return an error message
      if (lInvTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
      } else if (lStoreTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}", pSkuId, pStoreId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
      } else if (pQty <= 0) {
        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
      } else {
        long lSoldCount = (long) lInvTransactionItem.getPropertyValue(PROPERTY_SOLD);
        long lAllocated = (long) lInvTransactionItem.getPropertyValue(PROPERTY_ALLOCATED);
        long lUpdatedSoldCount = lSoldCount - pQty;

        if(!bUpdateOnlyStoreTables) {
        	lInvTransactionItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated + pQty);
        	lInvTransactionItem.setPropertyValue(PROPERTY_SOLD, lUpdatedSoldCount);

        	vlogInfo("incrementStoreAllocated: pSkuId: {0} Current Qty Sold: {1} Qty Sold Remaining : {2}", pSkuId, lSoldCount, lUpdatedSoldCount);
        	if (lUpdatedSoldCount < 0) {
        		vlogInfo("INVENTORY OVERSOLD : The inventory for SkuId: {0} has been oversold", pSkuId);
        	}

        	getRepository().updateItem(lInvTransactionItem);
        } else {
        	vlogInfo("incrementStoreAllocated: Skipped InvTransaction updates for store: {0} pSkuId: {1}", pStoreId, pSkuId);
        }

        long lStoreAllocatedCount = (long) lStoreTransactionItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
        lStoreTransactionItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocatedCount + pQty);

        vlogInfo("incrementStoreAllocated: pSkuId: {0} pStoreId: {1} Current Store Allocated Qty: {2} Store Allocated Qty Incremented to : {3} ", pSkuId, pStoreId, lStoreAllocatedCount, (lStoreAllocatedCount + pQty));
        // getRepository().updateItem(lStoreItem);
        getRepository().updateItem(lStoreTransactionItem);
      }
    } catch (RepositoryException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "Repository Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } catch (SQLException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "SQL Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"), e);
    } catch (TransactionDemarcationException e) {
      vlogError(e,"Transaction Error while updating the sold & allocated count for skuId: " + pSkuId);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } finally {
      try {
        td.end();
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }
    vlogDebug("Exiting incrementStoreAllocated");

    return INVENTORY_STATUS_SUCCEED;
  }

  /**
   * This method will be called when an allocated BOPIS order is rejected during fraud review
   * item. This method decrements the allocated
   * count from inventory transaction item. It decrements allocated
   * count in for store inventory transaction.
   * 
   * The sold count will not be modified as the sale is no longer valid since
   * the order is rejected after fraud review
   */
  public int fraudRejectStoreAllocated(String pSkuId, String pStoreId, long pQty) throws InventoryException {

    String lErrorMessage = "fraudRejectStoreAllocated failed for SkuId: %s Error: %s";

    vlogDebug("Entering fraudRejectStoreAllocated");
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      
      MutableRepositoryItem lInvTransactionItem=null;
      MutableRepositoryItem lStoreTransactionItem=null;
      
      if (isTurnOnHotfix()) {
        // This call will be updating both mff_inventory & mff_store_inventory
        // tables
        // Lets lock the single row in mff_inventory table that corresponds to
        // the sku passed in
        // Lets lock the single row in mff_store_inventory table that
        // corresponds to the sku & store passed in

        lockOnlineAndStoreInventory(pSkuId, pStoreId);

        // get mff inventory item and update sold & allocation count
        //lInvItem = getInventoryItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        //lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);
        
        lInvTransactionItem = getInventoryTransactionItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);  
        
        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdateNoLock(pSkuId, pStoreId);
      } else {
        // get mff inventory item and update sold & allocation count
        //lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        //lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);
        
        lInvTransactionItem = getInventoryTransactionItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
        
        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdate(pSkuId, pStoreId);
      }

      // if store item or inventory item not found return an error message
      if (lInvTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
      } else if (lStoreTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}", pSkuId, pStoreId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
      } else if (pQty <= 0) {
        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
      } else {
        
        //long lSoldCount = (long) lInvTransactionItem.getPropertyValue(PROPERTY_SOLD);
        long lAllocated = (long) lInvTransactionItem.getPropertyValue(PROPERTY_ALLOCATED);
        //long lUpdatedSoldCount = lSoldCount + pQty;

        // decrement allocated & increment the sold count
        lInvTransactionItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated - pQty);
        //lInvTransactionItem.setPropertyValue(PROPERTY_SOLD, lUpdatedSoldCount);

        vlogInfo("fraudRejectStoreAllocated: pSkuId: {0} Current Qty Allocated: {1} Qty Allocated decremented to : {2}", pSkuId, lAllocated, (lAllocated - pQty));
/*        if (lUpdatedSoldCount < 0) {
          vlogInfo("INVENTORY OVERSOLD : The inventory for SkuId: {0} has been oversold", pSkuId);
        }*/

        //updateItem(lInvItem);
        getRepository().updateItem(lInvTransactionItem);

        long lStoreAllocatedCount = (long) lStoreTransactionItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
        lStoreTransactionItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocatedCount - pQty);

        vlogInfo("fraudRejectStoreAllocated: pSkuId: {0} pStoreId: {1} Current Store Allocated Qty: {2} Store Allocated Qty Remaining: {3} ", pSkuId, pStoreId, lStoreAllocatedCount, (lStoreAllocatedCount - pQty));
        //updateStoreInventoryItem(lStoreItem);
        getRepository().updateItem(lStoreTransactionItem);
      }
    } catch (RepositoryException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "Repository Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } catch (SQLException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "SQL Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"), e);
    } catch (TransactionDemarcationException e) {
      vlogError(e,"Transaction Error while updating the sold & allocated count for skuId:{0}", pSkuId);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } finally {
      try {
        td.end();
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }
    vlogDebug("Exiting fraudRejectStoreAllocated");

    return INVENTORY_STATUS_SUCCEED;
  }  
  /**
   * This method will be called when an allocated store declines to fulfill the
   * item. This method increments the sold count and decrements the allocated
   * count from inventory transaction item. It decrements allocated
   * count in for store inventory transaction
   */
  public int decrementStoreAllocated(String pSkuId, String pStoreId, long pQty) throws InventoryException {

    String lErrorMessage = "decrementStoreAllocated failed for SkuId: %s Error: %s";

    vlogDebug("Entering decrementStoreAllocated");
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      
      MutableRepositoryItem lInvTransactionItem=null;
      MutableRepositoryItem lStoreTransactionItem=null;
      
      if (isTurnOnHotfix()) {
        // This call will be updating both mff_inventory & mff_store_inventory
        // tables
        // Lets lock the single row in mff_inventory table that corresponds to
        // the sku passed in
        // Lets lock the single row in mff_store_inventory table that
        // corresponds to the sku & store passed in

        lockOnlineAndStoreInventory(pSkuId, pStoreId);

        // get mff inventory item and update sold & allocation count
        //lInvItem = getInventoryItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        //lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);
        
        lInvTransactionItem = getInventoryTransactionItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);  
        
        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdateNoLock(pSkuId, pStoreId);
      } else {
        // get mff inventory item and update sold & allocation count
        //lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        //lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);
        
        lInvTransactionItem = getInventoryTransactionItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
        
        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdate(pSkuId, pStoreId);
      }

      // if store item or inventory item not found return an error message
      if (lInvTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
      } else if (lStoreTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}", pSkuId, pStoreId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
      } else if (pQty <= 0) {
        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
      } else {
        
        long lSoldCount = (long) lInvTransactionItem.getPropertyValue(PROPERTY_SOLD);
        long lAllocated = (long) lInvTransactionItem.getPropertyValue(PROPERTY_ALLOCATED);
        long lUpdatedSoldCount = lSoldCount + pQty;

        // decrement allocated & increment the sold count
        lInvTransactionItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated - pQty);
        lInvTransactionItem.setPropertyValue(PROPERTY_SOLD, lUpdatedSoldCount);

        vlogInfo("decrementStoreAllocated: pSkuId: {0} Current Qty Sold: {1} Qty Sold Incremented to : {2}", pSkuId, lSoldCount, lUpdatedSoldCount);
        if (lUpdatedSoldCount < 0) {
          vlogInfo("INVENTORY OVERSOLD : The inventory for SkuId: {0} has been oversold", pSkuId);
        }

        //updateItem(lInvItem);
        getRepository().updateItem(lInvTransactionItem);

        long lStoreAllocatedCount = (long) lStoreTransactionItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
        lStoreTransactionItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocatedCount - pQty);

        vlogInfo("decrementStoreAllocated: pSkuId: {0} pStoreId: {1} Current Store Allocated Qty: {2} Store Allocated Qty Remaining: {3} ", pSkuId, pStoreId, lStoreAllocatedCount, (lStoreAllocatedCount - pQty));
        //updateStoreInventoryItem(lStoreItem);
        getRepository().updateItem(lStoreTransactionItem);
      }
    } catch (RepositoryException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "Repository Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } catch (SQLException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "SQL Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"), e);
    } catch (TransactionDemarcationException e) {
      vlogError(e,"Transaction Error while updating the sold & allocated count for skuId:{0}", pSkuId);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } finally {
      try {
        td.end();
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }
    vlogDebug("Exiting decrementStoreAllocated");

    return INVENTORY_STATUS_SUCCEED;
  }

  /**
   * This method will be called when an allocated store for bopis declines to
   * fulfill the item. This method will decrement the allocated count from
   * inventory item(mff_inventory table) It decrements allocated count in for
   * storeInventory (mff_store_inventory table)
   */
  public int decrementStoreAllocatedForBopis(String pSkuId, String pStoreId, long pQty) throws InventoryException {

    String lErrorMessage = "decrementStoreAllocatedForBopis failed for SkuId: %s Error: %s";

    vlogDebug("Entering decrementStoreAllocatedForBopis");
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      MutableRepositoryItem lInvTransactionItem=null;
      MutableRepositoryItem lStoreTransactionItem=null;
      if (isTurnOnHotfix()) {
        // This call will be updating both mff_inventory & mff_store_inventory
        // tables
        // Lets lock the single row in mff_inventory table that corresponds to
        // the sku passed in
        // Lets lock the single row in mff_store_inventory table that
        // corresponds to the sku & store passed in

        lockOnlineAndStoreInventory(pSkuId, pStoreId);

        // get mff inventory item and update sold & allocation count
        //lInvItem = getInventoryItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        //lStoreItem = getStoreInventoryItemForUpdateNoLock(pSkuId, pStoreId);
        
        lInvTransactionItem = getInventoryTransactionItemForUpdateNoLock(pSkuId, DEFAULT_LOCATION_ID);  
        
        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdateNoLock(pSkuId, pStoreId);
      } else {
        // get mff inventory item and update sold & allocation count
        //lInvItem = getInventoryItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
        // get mff store inventory item and update allocation count
        //lStoreItem = getStoreInventoryItemForUpdate(pSkuId, pStoreId);
        
        lInvTransactionItem = getInventoryTransactionItemForUpdate(pSkuId, DEFAULT_LOCATION_ID);
        
        lStoreTransactionItem = getStoreInventoryTransactionItemForUpdate(pSkuId, pStoreId);
      }

      // get mff inventory item and update allocation count
      // MutableRepositoryItem lInvItem = getInventoryItemForUpdate(pSkuId,
      // DEFAULT_LOCATION_ID);

      // get mff store inventory item and update allocation count
      // MutableRepositoryItem lStoreItem =
      // getStoreInventoryItemForUpdate(pSkuId, pStoreId);

      // if store item or inventory item not found return an error message
      if (lInvTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "Inventory Item not found"));
      } else if (lStoreTransactionItem == null) {
        vlogError("Inventory Item not found for the SKU Id: {0} and StoreId: {1}", pSkuId, pStoreId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, " Store Inventory Item not found"));
      } else if (pQty <= 0) {
        vlogError("Proivde Valid quantity to increment or decrement for the SKU Id: {0}", pSkuId);
        throw new InventoryException(String.format(lErrorMessage, pSkuId, "provide valid quantity"));
      } else {
        
        long lAllocated = (long) lInvTransactionItem.getPropertyValue(PROPERTY_ALLOCATED);

        // decrement allocated
        lInvTransactionItem.setPropertyValue(PROPERTY_ALLOCATED, lAllocated - pQty);

        vlogInfo("decrementStoreAllocatedForBopis: pSkuId: {0} Current Qty Allocated: {1} Qty Allocated Remaining: {2}", pSkuId, lAllocated, (lAllocated - pQty));

        //updateItem(lInvItem);
        getRepository().updateItem(lInvTransactionItem);

        long lStoreAllocatedCount = (long) lStoreTransactionItem.getPropertyValue(PROPERTY_STORE_ALLOCATED);
        lStoreTransactionItem.setPropertyValue(PROPERTY_STORE_ALLOCATED, lStoreAllocatedCount - pQty);

        vlogInfo("decrementStoreAllocatedForBopis: pSkuId: {0} pStoreId: {1} Current Store Allocated Qty: {2} Store Allocated Qty Remaining: {3} ", pSkuId, pStoreId, lStoreAllocatedCount, (lStoreAllocatedCount - pQty));
        //updateStoreInventoryItem(lStoreItem);
        getRepository().updateItem(lStoreTransactionItem);
      }
    } catch (RepositoryException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "Repository Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } catch (SQLException e) {
      vlogError(e,String.format(lErrorMessage, pSkuId, "SQL Error"));
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "SQL Error"), e);
    } catch (TransactionDemarcationException e) {
      vlogError(e,"Transaction Error while updating the sold & allocated count for skuId:{0}", pSkuId);
      throw new InventoryException(String.format(lErrorMessage, pSkuId, "Repository Error"), e);
    } finally {
      try {
        td.end();
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }
    vlogDebug("Exiting decrementStoreAllocatedForBopis");

    return INVENTORY_STATUS_SUCCEED;
  }
  
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
    RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_DUPLICATE_BACK_IN_STOCK_ITEM);
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
  
  protected MutableRepositoryItem getInventoryTransactionItemForUpdate(String pSkuId, String pLocationId) throws RepositoryException {

    String lId = INVENTORY_PREFIX + pSkuId;

    if (isLockInventoryTransaction()) {
      try {
        lockInventoryTransaction(lId);
      } catch (java.sql.SQLException se) {
        throw new RepositoryException(se);
      }
    }

    if (getRepository() != null)
      return getRepository().getItemForUpdate(lId, ITEM_DESC_INVENTORY_TRANSACTION);
    else
      return null;

  }

  protected MutableRepositoryItem getInventoryTransactionItemForUpdateNoLock(String pSkuId, String pLocationId) throws RepositoryException {

    String lId = INVENTORY_PREFIX + pSkuId;

    if (getRepository() != null)
      return getRepository().getItemForUpdate(lId, ITEM_DESC_INVENTORY_TRANSACTION);
    else
      return null;

  }

  protected MutableRepositoryItem getInventoryItemForUpdateNoLock(String pId, String pLocationId) throws RepositoryException {

    RepositoryItem item = getInventoryItem(pId, pLocationId);
    MutableRepositoryItem mutItem = null;
    if (item instanceof MutableRepositoryItem)
      mutItem = (MutableRepositoryItem) item;
    else if (item != null)
      mutItem = getRepository().getItemForUpdate(item.getRepositoryId(), getItemType());
    else
      return null;

    return mutItem;
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
    
    /*String lId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;
    if (getRepository() != null) 
      return getRepository().getItemForUpdate(lId, STORE_INVENTORY_ITEM_NAME);
    else
    return null;*/
    
    StringBuffer sb = new StringBuffer();
    sb.append(INVENTORY_PREFIX);
    sb.append(pSkuId);
    sb.append(getEnvironment().getStoreInvItemDelim());
    sb.append(pStoreId);
    
    String lId = sb.toString();
    
    if(isLockStoreInventory()) {
        try {
            lockStoreInventory(lId);
          }
          catch(java.sql.SQLException se) {
            throw new RepositoryException(se);
          }     
    }
    
    if (getRepository() != null) 
      return getRepository().getItemForUpdate(lId, ITEM_DESC_STORE_INVENTORY);
    else
      return null;

  }
  
  protected RepositoryItem getStoreInventoryItem (String pSkuId, String pStoreId)
		    throws RepositoryException   {
		    
		    /*String lId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;
		    if (getRepository() != null) 
		      return getRepository().getItemForUpdate(lId, STORE_INVENTORY_ITEM_NAME);
		    else
		    return null;*/
		    
		    StringBuffer sb = new StringBuffer();
		    sb.append(INVENTORY_PREFIX);
		    sb.append(pSkuId);
		    sb.append(getEnvironment().getStoreInvItemDelim());
		    sb.append(pStoreId);
		    
		    String lId = sb.toString();

		    if (getRepository() != null) 
		      return getRepository().getItem(lId, ITEM_DESC_STORE_INVENTORY);
		    else
		      return null;

		  }  
  protected MutableRepositoryItem getStoreInventoryTransactionItemForUpdate (String pSkuId, String pStoreId)
      throws RepositoryException   {
      
    /*
       String lId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;
       if (getRepository() != null) 
         return getRepository().getItemForUpdate(lId,STORE_INVENTORY_ITEM_NAME); 
       else 
         return null;
     */

    StringBuffer sb = new StringBuffer();
    sb.append(INVENTORY_PREFIX);
    sb.append(pSkuId);
    sb.append(getEnvironment().getStoreInvItemDelim());
    sb.append(pStoreId);

    String lId = sb.toString();

    if (isLockStoreInvTransaction()) {
      try {
        lockStoreInventoryTransaction(lId);
      } catch (java.sql.SQLException se) {
        throw new RepositoryException(se);
      }
    }

    if (getRepository() != null)
      return getRepository().getItemForUpdate(lId, ITEM_DESC_STORE_INVENTORY_TRANSACTION);
    else
      return null;

  }
  
  protected RepositoryItem getStoreInventoryTransactionItem (String pSkuId, String pStoreId)
	      throws RepositoryException   {
	      
	    /*
	       String lId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;
	       if (getRepository() != null) 
	         return getRepository().getItemForUpdate(lId,STORE_INVENTORY_ITEM_NAME); 
	       else 
	         return null;
	     */

	    StringBuffer sb = new StringBuffer();
	    sb.append(INVENTORY_PREFIX);
	    sb.append(pSkuId);
	    sb.append(getEnvironment().getStoreInvItemDelim());
	    sb.append(pStoreId);

	    String lId = sb.toString();

	    if (getRepository() != null)
	      return getRepository().getItem(lId, ITEM_DESC_STORE_INVENTORY_TRANSACTION);
	    else
	      return null;

	  }  
  
  protected MutableRepositoryItem getStoreInventoryTransactionItemForUpdateNoLock (String pSkuId, String pStoreId)
       throws RepositoryException   {

    // String lId = INVENTORY_PREFIX + pSkuId + getEnvironment().getStoreInvItemDelim() + pStoreId;
    StringBuffer sb = new StringBuffer();
    sb.append(INVENTORY_PREFIX);
    sb.append(pSkuId);
    sb.append(getEnvironment().getStoreInvItemDelim());
    sb.append(pStoreId);

    String lId = sb.toString();

    if (getRepository() != null)
      return getRepository().getItemForUpdate(lId, ITEM_DESC_STORE_INVENTORY_TRANSACTION);
    else
      return null;

   }
  
  @SuppressWarnings("unused")
  protected void lockInventoryTransaction(String pInventoryTransactionItemId) throws java.sql.SQLException {
    if (isLoggingDebug()) logDebug("lockInventoryTransaction - Getting lock for " + pInventoryTransactionItemId);

    int retryCount = 0;
    int mostTries = getMaximumRetriesPerRowLock();
    int retryInterval = getMillisecondDelayBeforeLockRetry();
    SQLException lastException = null;
    String lock = DEFAULT_STORE_LOCK_NAME;

    Connection c = null;
    PreparedStatement ps = null;
    String sql = getLockInventoryTransactionSql();

    int rowcount = 0;

    TransactionDemarcation td = new TransactionDemarcation();

    while (retryCount < mostTries) {
      try {
        td.begin(getTransactionManager());
        c = ((atg.adapter.gsa.GSARepository) getRepository()).getDataSource().getConnection();
        ps = c.prepareStatement(sql);
        ps.setString(1, lock);
        ps.setString(2, pInventoryTransactionItemId);

        rowcount = ps.executeUpdate();
        lastException = null;
        break;
      } catch (TransactionDemarcationException tde) {
        if (isLoggingDebug()) logDebug("lockInventoryTransaction - This attemp to lock " + pInventoryTransactionItemId + " failed: " + tde.getMessage());

        lastException = new SQLException(tde.getLocalizedMessage());
        retryCount++;
      } catch (SQLException s) {
        if (isLoggingDebug()) logDebug("lockInventoryTransaction - This attemp to lock " + pInventoryTransactionItemId + " failed: " + s.getMessage());

        lastException = s;
        retryCount++;
      } finally {
        try {
          td.end();
        } catch (TransactionDemarcationException tde) {
          if (isLoggingError()) logError("lockInventoryTransaction - This attemp to lock " + pInventoryTransactionItemId + " failed: " + tde.getLocalizedMessage());

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
          if (isLoggingDebug()) logDebug("lockInventoryTransaction - Thread was interrupted.", i);
          // just catch this and continue
        }
      }

    }

    if (lastException != null) throw lastException;
  }

  @SuppressWarnings("unused")
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
        //ps.setString(1, lock);
        ps.setString(1, pStoreInventoryItemId);

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

  @SuppressWarnings("unused")
  protected void lockStoreInventoryTransaction(String pStoreInventoryItemId) throws java.sql.SQLException {
    if (isLoggingDebug()) logDebug("lockStoreInventoryTransaction - Getting lock for " + pStoreInventoryItemId);

    int retryCount = 0;
    int mostTries = getMaximumRetriesPerRowLock();
    int retryInterval = getMillisecondDelayBeforeLockRetry();
    SQLException lastException = null;
    String lock = DEFAULT_STORE_LOCK_NAME;

    Connection c = null;
    PreparedStatement ps = null;
    String sql = getStoreInvTransactionLockSql();

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
        if (isLoggingDebug()) logDebug("lockStoreInventoryTransaction - This attemp to lock " + pStoreInventoryItemId + " failed: " + tde.getMessage());

        lastException = new SQLException(tde.getLocalizedMessage());
        retryCount++;
      } catch (SQLException s) {
        if (isLoggingDebug()) logDebug("lockStoreInventoryTransaction - This attemp to lock " + pStoreInventoryItemId + " failed: " + s.getMessage());

        lastException = s;
        retryCount++;
      } finally {
        try {
          td.end();
        } catch (TransactionDemarcationException tde) {
          if (isLoggingError()) logError("lockStoreInventoryTransaction - This attemp to lock " + pStoreInventoryItemId + " failed: " + tde.getLocalizedMessage());

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
          if (isLoggingDebug()) logDebug("lockStoreInventoryTransaction - Thread was interrupted.", i);
          // just catch this and continue
        }
      }

    }

    if (lastException != null) throw lastException;
  }

  @SuppressWarnings("unused")
  protected void lockOnlineAndStoreInventory(String pInventoryId, String pStoreInventoryId) throws java.sql.SQLException {
    if (isLoggingDebug()) logDebug("lockOnlineAndStoreInventory - Getting lock for sku " + pInventoryId + " and store " + pStoreInventoryId);

    int retryCount = 0;
    int mostTries = getMaximumRetriesPerRowLock();
    int retryInterval = getMillisecondDelayBeforeLockRetry();
    SQLException lastException = null;
    // String lock = DEFAULT_STORE_LOCK_NAME;

    Connection c = null;
    PreparedStatement ps = null;

    String sql = getOnlineAndStoreInventoryLockSql();
    // String sql = "select * from mff_inventory mi, mff_store_inventory si
    // where mi.catalog_ref_id=si.catalog_ref_id and mi.catalog_ref_id=? and
    // si.store_id=? for update";

    int rowcount = 0;

    TransactionDemarcation td = new TransactionDemarcation();

    while (retryCount < mostTries) {
      try {
        td.begin(getTransactionManager());
        c = ((atg.adapter.gsa.GSARepository) getRepository()).getDataSource().getConnection();
        ps = c.prepareStatement(sql);
        ps.setString(1, pInventoryId);
        ps.setString(2, pStoreInventoryId);

        rowcount = ps.executeUpdate();
        lastException = null;
        break;
      } catch (TransactionDemarcationException tde) {
        if (isLoggingDebug()) logDebug("lockOnlineAndStoreInventory - This attemp to lock store & online inv for sku " + pInventoryId + " and store " + pStoreInventoryId + " failed: " + tde.getMessage());

        lastException = new SQLException(tde.getLocalizedMessage());
        retryCount++;
      } catch (SQLException s) {
        if (isLoggingDebug()) logDebug("lockOnlineAndStoreInventory - This attemp to lock store & online inv for sku " + pInventoryId + " and store " + pStoreInventoryId + " failed: " + s.getMessage());

        lastException = s;
        retryCount++;
      } finally {
        try {
          td.end();
        } catch (TransactionDemarcationException tde) {
          if (isLoggingError()) logError("lockOnlineAndStoreInventory - This attemp to lock store & online inv for sku " + pInventoryId + " and store " + pStoreInventoryId + " failed: " + tde.getLocalizedMessage());

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
          if (isLoggingDebug()) logDebug("lockOnlineAndStoreInventory - Thread was interrupted.", i);
          // just catch this and continue
        }
      }

    }

    if (lastException != null) throw lastException;
  }

  protected void updateStoreInventoryItem(MutableRepositoryItem pItem) throws RepositoryException {

    try {
      MutableRepository rep = getRepository();

      // so when this gets commited, the lock field is not set anymore
      String lockPropertyName = getInventoryRowLockProperty();
      pItem.setPropertyValue(lockPropertyName, null);
      rep.updateItem(pItem);
    } catch (RepositoryException r) {
      throw r;
    }
  }

  public void acquireTransactionInventoryLocks(List pItemIds, String pLocationId) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Acquiring locks");

    SortedSet ids = new TreeSet();
    int i = 0;
    List skus = new ArrayList(pItemIds);

    while(i<skus.size()) {
      String id = (String) skus.get(i);

      if(isBundle(id)) {
        skus.addAll(getBundledIds(id));
      }
      else {
        ids.add(id);
      }
      i++;
    }

    Iterator sortedIter = ids.iterator();

    while(sortedIter.hasNext()) {
      String id = (String) sortedIter.next();
      try {
    	  lockInventoryTransaction(INVENTORY_PREFIX +id);
      }
      catch(SQLException s) {
        throw new InventoryException(s);
      }
    }
  }

  public boolean isAcquireInventoryLocksPreCommitOrder() {
    return mAcquireInventoryLocksPreCommitOrder;
  }

  public void setAcquireInventoryLocksPreCommitOrder(boolean pAcquireInventoryLocksPreCommitOrder) {
    mAcquireInventoryLocksPreCommitOrder = pAcquireInventoryLocksPreCommitOrder;
  }
  
  public String getStoreInventoryLockSql() {
    return storeInventoryLockSql;
  }

  public void setStoreInventoryLockSql(String pStoreInventoryLockSql) {
    storeInventoryLockSql = pStoreInventoryLockSql;
  }
  
  public String getStoreInvTransactionLockSql() {
    return storeInvTransactionLockSql;
  }

  public void setStoreInvTransactionLockSql(String pStoreInvTransactionLockSql) {
    storeInvTransactionLockSql = pStoreInvTransactionLockSql;
  }

  public String getOnlineAndStoreInventoryLockSql() {
    return onlineAndStoreInventoryLockSql;
  }

  public void setOnlineAndStoreInventoryLockSql(String pOnlineAndStoreInventoryLockSql) {
    onlineAndStoreInventoryLockSql = pOnlineAndStoreInventoryLockSql;
  }
  
  public String getLockInventoryTransactionSql() {
    return lockInventoryTransactionSql;
  }

  public void setLockInventoryTransactionSql(String pLockInventoryTransactionSql) {
    lockInventoryTransactionSql = pLockInventoryTransactionSql;
  }

  public boolean isLockInventoryTransaction() {
    return lockInventoryTransaction;
  }

  public void setLockInventoryTransaction(boolean pLockInventoryTransaction) {
    lockInventoryTransaction = pLockInventoryTransaction;
  }

  public boolean isLockStoreInventory() {
    return lockStoreInventory;
  }

  public void setLockStoreInventory(boolean pLockStoreInventory) {
    lockStoreInventory = pLockStoreInventory;
  }
  
  public boolean isLockStoreInvTransaction() {
    return lockStoreInvTransaction;
  }

  public void setLockStoreInvTransaction(boolean pLockStoreInvTransaction) {
    lockStoreInvTransaction = pLockStoreInvTransaction;
  }

  public boolean isTurnOnHotfix() {
    return turnOnHotfix;
  }

  public void setTurnOnHotfix(boolean pTurnOnHotfix) {
    turnOnHotfix = pTurnOnHotfix;
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
  
  public boolean isEnableBopisInventoryFix() {
    return enableBopisInventoryFix;
  }

  public void setEnableBopisInventoryFix(boolean pEnableBopisInventoryFix) {
    enableBopisInventoryFix = pEnableBopisInventoryFix;
  }  

}
