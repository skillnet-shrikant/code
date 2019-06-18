/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.core.util.ContactInfo;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import oms.allocation.item.AllocationConstants;
import oms.allocation.item.StoreAllocation;
import oms.allocation.item.StoreAllocationItem;

/**
 * The fulfillment history  Manager will handle all of the updates to 
 * the fulfillment history repository item.  The fulfillment history
 * tracks the disposition of all items that are fulfilled by a store.
 *  The 
 * The API will handle the following events:
 * 
 * 		1.) addLineItem 				- Add a new store fulfillment item
 * 		2.) declineLineItem 			- Decline a store fulfillment item 
 * 		3.) shipLineItem 				- Ship a store fulfillment item
 * 		4.) countFulfillmentAttempts	- Count the number of store declines for an item
 *  
 * @author DMI
 *
 */
public class StoreAllocationManager 
	extends GenericService {
	
	// ********************************************************************
	//
	//						Fulfillment History
	//
	// ********************************************************************
	
	/**
	 * Add a new store fulfillment record for a given order and line item.
	 * 
	 * @param pOrder				ATG Order
	 * @param pCommerceItemId		Commerce Item ID for item to be fulfilled
	 * @throws CommerceException
	 */
	public void addLineItem (Order pOrder, String pCommerceItemId) 
		throws CommerceException {		
		vlogDebug ("Begin addLineItem");
		MFFOrderImpl lOrder 				= (MFFOrderImpl) pOrder;
		MFFCommerceItemImpl lCommerceItem 	= (MFFCommerceItemImpl) lOrder.getCommerceItem(pCommerceItemId);
		HardgoodShippingGroup shipGroup		= getOrderShippingGroup(pOrder);
		
		if(Strings.isNullOrEmpty(lCommerceItem.getFulfillmentStore())){
		  vlogInfo ("Skipped creating storeAllocation as fulfillment store is null for Order No: {0} Item: {1}", lOrder.getId(), pCommerceItemId);
		  return;
		}
		
		vlogDebug ("Add Fulfillment history for Order No: {0} Item: {1}", lOrder.getId(), pCommerceItemId);
		MutableRepositoryItem lMutableRepositoryItem = null;
		try {
			lMutableRepositoryItem = getOmsOrderRepository().createItem (AllocationConstants.ITEM_STORE_ALLOCATION);
		} catch (RepositoryException e) {
			String lErrorMessage = String.format("Unable to create fulfillment history record - Order Number: %s Commerce Item Id: %s", lOrder.getId(), pCommerceItemId);
			vlogError (e, lErrorMessage);
			throw new CommerceException (lErrorMessage, e);
		}
		StoreAllocation lFulfillmentHistory = new StoreAllocation (lMutableRepositoryItem);
		ContactInfo shipAddress = (ContactInfo)shipGroup.getShippingAddress();
		
		lFulfillmentHistory.setAllocationDate	(new Date());
		lFulfillmentHistory.setChanged			(true);
		lFulfillmentHistory.setCommerceItemId	(pCommerceItemId);
		lFulfillmentHistory.setOrderDate		(lOrder.getSubmittedDate());
		lFulfillmentHistory.setOrderId			(lOrder.getId());
		lFulfillmentHistory.setOrderNumber		(lOrder.getOrderNumber());
		if(lOrder.isBopisOrder()){
		  lFulfillmentHistory.setFirstName(lOrder.getBopisPerson()); 
		}else{
		  lFulfillmentHistory.setFirstName		(shipAddress.getFirstName());
		  lFulfillmentHistory.setLastName  		(shipAddress.getLastName());
		}
		lFulfillmentHistory.setAddress1(shipAddress.getAddress1());
		lFulfillmentHistory.setAddress2(shipAddress.getAddress2());
		lFulfillmentHistory.setCity(shipAddress.getCity());
		lFulfillmentHistory.setShipState(shipAddress.getState());
		lFulfillmentHistory.setPostalCode(shipAddress.getPostalCode());
		lFulfillmentHistory.setCounty(shipAddress.getCounty());
		lFulfillmentHistory.setCountry(shipAddress.getCountry());
		lFulfillmentHistory.setPhoneNumber(shipAddress.getPhoneNumber());
		lFulfillmentHistory.setContactEmail(lOrder.getContactEmail());
		
		lFulfillmentHistory.setShippingMethod	(shipGroup.getShippingMethod());
		lFulfillmentHistory.setQuantity			(lCommerceItem.getQuantity());
		lFulfillmentHistory.setShippedDate		(null);
		lFulfillmentHistory.setState			(StoreAllocationStates.PRE_SHIP);
		lFulfillmentHistory.setStateDetail		(null);
		lFulfillmentHistory.setStoreId			(lCommerceItem.getFulfillmentStore());
		
		lFulfillmentHistory.setSkuId			(lCommerceItem.getCatalogRefId());
		lFulfillmentHistory.setBopisOrder(lOrder.isBopisOrder());
		lFulfillmentHistory.setInPicking(false);
		
		// Show formatted record
		vlogDebug (lFulfillmentHistory.toString());
		
		// Add item to repository
		try {
			getOmsOrderRepository().addItem(lFulfillmentHistory.getRepositoryItem());
		} catch (RepositoryException e) {			
			String lErrorMessage = String.format("Unable to add fulfillment history record - Order Number: %s Commerce Item Id: %s", lOrder.getId(), pCommerceItemId);
			vlogError (e, lErrorMessage);
			vlogError (lFulfillmentHistory.toString());
			throw new CommerceException (lErrorMessage, e);
		}
		vlogDebug ("End addLineItem");
	}
	
	/**
	 * Marks the current allocation for this line item as declined so
	 * that it can be re-allocated to another store.  Items to be declined
	 * must be in the PRE-SHIP state.
	 * 
	 * @param pOrder			ATG Order
	 * @param pCommerceItemId	Commerce Item Id
	 * @throws RepositoryException 
	 */
	public void declineLineItem (Order pOrder, String pCommerceItemId) 		
		throws CommerceException, RepositoryException {		
		vlogDebug ("Begin declineLineItem");
		MFFOrderImpl lOrder 				= (MFFOrderImpl) pOrder;
		vlogDebug ("Decline order number: {0} Commerce Item: {1}", lOrder.getId(), pCommerceItemId);
		
		// Get the item from the repository
		RepositoryItem lItem 	= getPreShipRecord (pCommerceItemId);
		String lItemId 			= lItem.getRepositoryId();
		
	    MutableRepositoryItem lMutableItem 		= getOmsOrderRepository().getItemForUpdate(lItemId, AllocationConstants.ITEM_STORE_ALLOCATION);
	    StoreAllocation lFulfillmentHistory 	= new StoreAllocation(lMutableItem);
	    lFulfillmentHistory.setState(StoreAllocationStates.DECLINE);
	    lFulfillmentHistory.setDeclineDate(new Date());
	    getOmsOrderRepository().updateItem(lMutableItem);
		vlogDebug ("End declineLineItem");
	}
	
	/**
	 * Marks the current allocation for this line item as shipped.

	 * @param pOrder				ATG Order
	 * @param pCommerceItemId		Commerce item id
	 * @throws CommerceException
	 * @throws RepositoryException
	 */
	public void shipLineItem (Order pOrder, String pCommerceItemId) 		
		throws CommerceException, RepositoryException {		
		vlogDebug ("Begin shipLineItem");
		MFFOrderImpl lOrder 				= (MFFOrderImpl) pOrder;
		vlogDebug ("Ship order number: {0} Commerce Item: {1}", lOrder.getId(), pCommerceItemId);
		
		// Get the item from the repository
		RepositoryItem lItem  = null;
		if(lOrder.isBopisOrder()){
		  lItem   = getReadyForPickUpRecord(pCommerceItemId);
		}else{
		  lItem 	= getPreShipRecord (pCommerceItemId);
		}
		if(lItem != null){
		  String lItemId 			= lItem.getRepositoryId();
			
	    MutableRepositoryItem lMutableItem 		= getOmsOrderRepository().getItemForUpdate(lItemId, AllocationConstants.ITEM_STORE_ALLOCATION);
	    StoreAllocation lFulfillmentHistory 	= new StoreAllocation(lMutableItem);
	    lFulfillmentHistory.setState(StoreAllocationStates.SHIPPED);
	    lFulfillmentHistory.setShippedDate(new Date());
	    getOmsOrderRepository().updateItem(lMutableItem);
		}
		vlogDebug ("End shipLineItem");
	}

	/**
	 * Cancel the current allocation for this line item.
	 * 
	 * @param pOrder				ATG Order
	 * @param pCommerceItemId		Commerce item id
	 * @throws CommerceException
	 * @throws RepositoryException
	 */
  public void cancelLineItem(Order pOrder, String pCommerceItemId) throws CommerceException, RepositoryException {
    vlogDebug("Begin cancelLineItem");
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    vlogDebug("Cancel order number: {0} Commerce Item: {1}", lOrder.getId(), pCommerceItemId);

    // Get the item from the repository
    RepositoryItem[] lItems = null;
    if(lOrder.isBopisOrder()){
      lItems = getAllocationByCIAndStatus(pCommerceItemId, StoreAllocationStates.READY_FOR_PICKUP);
    }
    
    if(lItems == null){
      lItems = getAllocationByCIAndStatus(pCommerceItemId, StoreAllocationStates.PRE_SHIP);
    }
    
    if (lItems != null && lItems.length > 0) {
      String lItemId = lItems[0].getRepositoryId();
      MutableRepositoryItem lMutableItem = getOmsOrderRepository().getItemForUpdate(lItemId, AllocationConstants.ITEM_STORE_ALLOCATION);
      StoreAllocation lFulfillmentHistory = new StoreAllocation(lMutableItem);
      lFulfillmentHistory.setState(StoreAllocationStates.CANCELLED);
      getOmsOrderRepository().updateItem(lMutableItem);
    } else {
      vlogDebug("No PreShip record found for commerce item {0}. So continue processing", pCommerceItemId);
    }
  }
  
  /**
   * Marks the current allocation for this line item as readyForPickup.
   * 
   * @param pOrder        ATG Order
   * @param pCommerceItemId   Commerce item id
   * @throws CommerceException
   * @throws RepositoryException
   */
  public void readyForPickLineItem(Order pOrder, String pCommerceItemId,String pPickUpInstructions) throws CommerceException, RepositoryException {
    vlogDebug("Begin readyForPickLineItem");
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    vlogDebug("PickUp order number: {0} Commerce Item: {1}", lOrder.getId(), pCommerceItemId);

    // Get the item from the repository
    RepositoryItem[] lItems = null;
    lItems = getAllocationByCIAndStatus(pCommerceItemId, StoreAllocationStates.PRE_SHIP);
    if (lItems != null && lItems.length > 0) {
      String lItemId = lItems[0].getRepositoryId();
      MutableRepositoryItem lMutableItem = getOmsOrderRepository().getItemForUpdate(lItemId, AllocationConstants.ITEM_STORE_ALLOCATION);
      StoreAllocation lFulfillmentHistory = new StoreAllocation(lMutableItem);
      lFulfillmentHistory.setState(StoreAllocationStates.READY_FOR_PICKUP);
      lFulfillmentHistory.setReadyForPickupDate(new Date());
      if(!Strings.isNullOrEmpty(pPickUpInstructions)){
        lFulfillmentHistory.setPickUpInstructions(pPickUpInstructions);
      }
      getOmsOrderRepository().updateItem(lMutableItem);
    } else {
      vlogDebug("No PreShip record found for commerce item {0}. So continue processing", pCommerceItemId);
    }
  }
  
  /**
   * Marks the current allocation inPicking flag to true for the lineItem.
   * 
   * @param pOrder        ATG Order
   * @param pCommerceItemId   Commerce item id
   * @throws CommerceException
   * @throws RepositoryException
   */
  public void updateInPickingFlag(String pOrderId, String pCommerceItemId, String pStoreId) throws CommerceException, RepositoryException {
    vlogDebug("Inside updateInPickingFlag order number: {0} Commerce Item: {1}, StoreId; {2}", pOrderId, pCommerceItemId, pStoreId);

    // Get the item from the repository
    RepositoryItem[] lItems = null;
    lItems = getAllocationByCIAndStatus(pCommerceItemId, StoreAllocationStates.PRE_SHIP);
    if (lItems != null && lItems.length > 0) {
      String lItemId = lItems[0].getRepositoryId();
      MutableRepositoryItem lMutableItem = getOmsOrderRepository().getItemForUpdate(lItemId, AllocationConstants.ITEM_STORE_ALLOCATION);
      String storeId = (String)lMutableItem.getPropertyValue(AllocationConstants.PROPERTY_STORE_ID);
      vlogDebug("Inside updateInPickingFlag item Allocated StoreId: {0}", storeId);
      if(!Strings.isNullOrEmpty(storeId) && storeId.equalsIgnoreCase(pStoreId)){
        StoreAllocation lFulfillmentHistory = new StoreAllocation(lMutableItem);
        
        lFulfillmentHistory.setInPicking(true);
        lFulfillmentHistory.setInPickingDate(new Date());
        getOmsOrderRepository().updateItem(lMutableItem);
      }else{
        vlogWarning("Cannot update inPicking flag as storeId is null/not valid for order number: {0} Commerce Item: {1}, StoreId; {2}", 
            pOrderId, pCommerceItemId, pStoreId);
      }
    } else {
      vlogDebug("No PreShip record found for commerce item {0}. So continue processing", pCommerceItemId);
    }
  }
  
  
	/**
	 * Get the pre-ship record so we can change the status.
	 * 
	 * @param pCommerceItemId		Commerce item Id
	 * @return
	 * @throws CommerceException
	 */
	protected RepositoryItem getPreShipRecord (String pCommerceItemId) 
		throws CommerceException {
		RepositoryItem [] lItems 		= null;
		lItems = getAllocationByCIAndStatus (pCommerceItemId, StoreAllocationStates.PRE_SHIP);
		if (lItems == null || lItems.length < 1) {
			String lErrorMessage = String.format("Error locating Commerce Item Id: %s State: %s", pCommerceItemId, StoreAllocationStates.PRE_SHIP);
			vlogError (lErrorMessage);
			throw new CommerceException (lErrorMessage);
		}
		if (lItems.length > 1) {
			String lErrorMessage = String.format("More than one record found for Commerce Item Id: %s State: %s", pCommerceItemId, StoreAllocationStates.PRE_SHIP);
			vlogError (lErrorMessage);
			throw new CommerceException (lErrorMessage);
		}
		return lItems[0];		
	}
	
	/**
   * Get the readyForPickUp record so we can change the status.
   * 
   * @param pCommerceItemId   Commerce item Id
   * @return
   * @throws CommerceException
   */
  protected RepositoryItem getReadyForPickUpRecord (String pCommerceItemId) 
    throws CommerceException {
    RepositoryItem [] lItems    = null;
    lItems = getAllocationByCIAndStatus (pCommerceItemId, StoreAllocationStates.READY_FOR_PICKUP);
    if (lItems == null || lItems.length < 1) {
      String lErrorMessage = String.format("Error locating Commerce Item Id: %s State: %s", pCommerceItemId, StoreAllocationStates.READY_FOR_PICKUP);
      vlogError (lErrorMessage);
      throw new CommerceException (lErrorMessage);
    }
    if (lItems.length > 1) {
      String lErrorMessage = String.format("More than one record found for Commerce Item Id: %s State: %s", pCommerceItemId, StoreAllocationStates.READY_FOR_PICKUP);
      vlogError (lErrorMessage);
      throw new CommerceException (lErrorMessage);
    }
    return lItems[0];   
  }
	
	/**
	 * Get the current item by item ID and status.
	 * 
	 * @param pCommerceItemId		Commerce item id
	 * @param pState				Item Status
	 * @return
	 * @throws CommerceException
	 */
	protected RepositoryItem[]  getAllocationByCIAndStatus (String pCommerceItemId, String pState) 
		throws CommerceException {
		RepositoryItem [] lItems 		= null;
		try {
			// Get the repository item for this order number
			Object [] lParams 			= new String[2];
			lParams[0] 					= pCommerceItemId;
			lParams[1] 					= pState;
			RepositoryView lView 		= getOmsOrderRepository().getView(AllocationConstants.ITEM_STORE_ALLOCATION);
			RqlStatement lStatement 	= RqlStatement.parseRqlStatement("commerceItemId EQUALS ?0 AND STATE EQUALS ?1");
			lItems 						= lStatement.executeQuery(lView, lParams);
		}
		catch (RepositoryException e) {
			String lErrorMessage = String.format("getFulfillmentByCommerceItemId - Error locating Commerce Item Id: %s State: %s", pCommerceItemId, pState);
			vlogError (e, lErrorMessage);
			throw new CommerceException (lErrorMessage, e);
		}
		return lItems;
	}
	
	/**
	 * Return the number of allocation attempts for a 
	 * given line item.
	 * 
	 * @param pCommerceItemId		Commerce item id
	 * @return
	 * @throws CommerceException
	 */
	public long countAllocationAttempts (String pCommerceItemId) 
		throws CommerceException {
		RepositoryItem [] lItems 		= null;
		lItems = getAllocationByCIAndStatus (pCommerceItemId, StoreAllocationStates.DECLINE);
		if(lItems != null){
			return lItems.length;
		}
		
		return 0;
	}
	
	/**
	 * Determine if the maximum number of allocation attempt has been reached  
	 * 
	 * @param pCommerceItemId		Commerce Item Id
	 * @return						true - Threshold exceeded 
	 * 								false - Threshold not exceeded 
	 * @throws CommerceException
	 */
	public boolean isMaxAllocationReached(String pCommerceItemId) 
		throws CommerceException {
		if (countAllocationAttempts (pCommerceItemId) == getMaxAllocationCount()) 
			return true;
		else 
			return false;
	}
	
	/**
	 * Get the allocation status by store and status.
	 * 
	 * @param pStoreId		Store id
	 * @param pState		State
	 * @return
	 * @throws CommerceException
	 */
	public RepositoryItem[]  getAllocationByStoreAndStatus (String pStoreId, String pState) throws CommerceException {
		
		RepositoryItem [] lItems 		= null;
		try {
			// Get the repository item for store and status
			Object [] lParams 			= new String[2];
			lParams[0] 					= pStoreId;
			lParams[1] 					= pState;
			RepositoryView lView 		= getOmsOrderRepository().getView(AllocationConstants.ITEM_STORE_ALLOCATION);
			RqlStatement lStatement 	= RqlStatement.parseRqlStatement("storeId EQUALS ?0 AND STATE EQUALS ?1");
			lItems 						= lStatement.executeQuery(lView, lParams);
		}
		catch (RepositoryException e) {
			String lErrorMessage = String.format("getAllocationByStoreAndStatus - Error locating Commerce Item Id: %s State: %s", pStoreId, pState);
			vlogError (e, lErrorMessage);
			throw new CommerceException (lErrorMessage, e);
		}
		return lItems;
	}
	
	/**
	 * Gets the shippingMethod of the Order
	 * 
	 * @param pOrder
	 * @return
	 */
	private HardgoodShippingGroup getOrderShippingGroup(Order pOrder){
		MFFOrderImpl lOrder = (MFFOrderImpl)pOrder;
		HardgoodShippingGroup hsg = null;
		
		if(lOrder.getShippingGroups() != null && lOrder.getShippingGroups().size() > 0){
			ShippingGroup sg = (ShippingGroup)lOrder.getShippingGroups().get(0);
			if(sg instanceof HardgoodShippingGroup){
				hsg = (HardgoodShippingGroup)sg;
			}
		}
		return hsg;
	}
	
	public List<StoreAllocationItem> getAllocationByStoreAndStatusSql (String pStoreId, String pState, int lowEnd, int highEnd) throws CommerceException {
		
		// query the message table
	    DataSource ds = getDataSource();
	    PreparedStatement ps = null;
	    List<StoreAllocationItem> storeAllocationList = new ArrayList<StoreAllocationItem>();
	    
		if (getDataSource() == null) {
		  if (isLoggingDebug()) {
		    logDebug("no configured datasource. aborting");
		  }
		  return storeAllocationList;
		}
	   
	    if (isLoggingDebug()) {
	      logDebug("querying SQL datasource " + dataSource.toString() + " with SQL: " + getQuerySQL());
		}
		
		try (Connection conn = ds.getConnection()) {
		  ps = prepareQueryStatement(conn,pStoreId,pState,lowEnd,highEnd);
		  ResultSet rs = ps.executeQuery();
		  
		  try {
				while(rs.next()){
					
					StoreAllocationItem lStoreAllocation = new StoreAllocationItem ();
					
					lStoreAllocation.setCommerceItemId	(rs.getString("commerce_item_id"));
					lStoreAllocation.setOrderId			(rs.getString("order_id"));
					lStoreAllocation.setOrderNumber      (rs.getString("order_number"));
					lStoreAllocation.setShippingMethod	(rs.getString("shipping_method"));
					lStoreAllocation.setQuantity		(rs.getLong("quantity"));
					lStoreAllocation.setSkuId			(rs.getString("sku_id"));
					lStoreAllocation.setAllocationDate(rs.getTimestamp("allocation_date"));
					lStoreAllocation.setFirstName(rs.getString("first_name"));
					lStoreAllocation.setLastName(rs.getString("last_name"));
					lStoreAllocation.setAddress1(rs.getString("address1"));
					lStoreAllocation.setAddress2(rs.getString("address2"));
					lStoreAllocation.setCity(rs.getString("city"));
					lStoreAllocation.setShipState(rs.getString("ship_state"));
					lStoreAllocation.setPostalCode(rs.getString("postal_code"));
					lStoreAllocation.setCounty(rs.getString("county"));
					lStoreAllocation.setCountry(rs.getString("country"));
					lStoreAllocation.setPhoneNumber(rs.getString("phone_number"));
					lStoreAllocation.setContactEmail(rs.getString("contact_email"));
					lStoreAllocation.setState			(pState);
					lStoreAllocation.setStoreId			(pStoreId);
					lStoreAllocation.setBopisOrder(rs.getBoolean("bopis_order"));
					lStoreAllocation.setInPicking(rs.getBoolean("in_picking"));
					lStoreAllocation.setOrderSubmittedDate(rs.getTimestamp("order_date"));
					lStoreAllocation.setPickUpInstructions(rs.getString("pick_up_instructions"));
					
					storeAllocationList.add(lStoreAllocation);
				}
			} catch (SQLException e) {
				vlogError(e,"Error in getAllocationByStoreAndStatusSql");
			}
		
		} catch (SQLException ex) {
			String errorMessage = "getAllocationByStoreAndStatusSql - Unable to get records";
			vlogError(ex, errorMessage);
		}
		
		return storeAllocationList;
	}

	public int getPreShippedItemsByStoreId(String pStoreId){
		return getTotalAllocatedRecordsByStoreId(pStoreId, StoreAllocationStates.PRE_SHIP);
	}
	
	public int getReadyForPickupItemsByStoreId(String pStoreId){
    return getTotalAllocatedRecordsByStoreId(pStoreId, StoreAllocationStates.READY_FOR_PICKUP);
  }
	
	private int getTotalAllocatedRecordsByStoreId(String pStoreId, String pState){
		int count = 0;
		
		if (getDataSource() == null) {
		  if (isLoggingDebug()) {
		    logDebug("no configured datasource. aborting");
		  }
		  return count;
		}
		
		// query the message table
	    DataSource ds = getDataSource();
	    PreparedStatement ps = null;
	   
	    if (isLoggingDebug()) {
	      logDebug("querying SQL datasource " + dataSource.toString() + " with SQL: " + getCountSQL());
		}
		
		try (Connection conn = ds.getConnection()) {
		  ps = prepareCountStatement(conn,pStoreId,pState);
		  ResultSet rs = ps.executeQuery();
		  
		  rs.next();
		  count = rs.getInt("count");
		
		} catch (SQLException ex) {
			String errorMessage = "getTotalAllocatedRecordsByStoreId - Unable to get records";
			vlogError(ex, errorMessage);
		
		}
		  
		return count;
	}
	
	protected PreparedStatement prepareCountStatement(Connection conn,String pStoreId, String pState) throws SQLException {
		
		//select count(distinct order_id) AS count from mff_store_allocation where state=? AND store_id=?
		PreparedStatement ps = conn.prepareStatement(getCountSQL());
		ps.setString(1, pState);
		ps.setString(2, pStoreId);
		return ps;
	}	
	
	protected PreparedStatement prepareQueryStatement(Connection conn,String pStoreId, String pState,int lowEnd, int highEnd) throws SQLException {
		
		//select order_id,commerce_item_id,store_id,sku_id,quantity,order_date,allocation_date,decline_date,ship_date,shipping_method from 
		//(select dense_rank() over (order by order_id) pagination,mff_store_allocation.* from mff_store_allocation where state=? AND store_id=?) where pagination >? and pagination <=?
		
		PreparedStatement ps = conn.prepareStatement(getQuerySQL());
		ps.setString(1, pState);
		ps.setString(2, pStoreId);
		ps.setInt(3, lowEnd);
		ps.setInt(4, highEnd);
		return ps;
	}
	
	protected PreparedStatement prepareShippedCountStatement(Connection conn,String pStoreId, String pState,String pNumberOfDays) throws SQLException {
    
    //select count(distinct order_id) AS count from mff_store_allocation where state=? AND store_id=?
    String lSql = getShippedCountSQL().replaceAll("SQL_INTERVAL", pNumberOfDays);
    vlogDebug ("SQL Statement for ShippedCountStatement is " + lSql);
    PreparedStatement ps = conn.prepareStatement(lSql);
    ps.setString(1, pState);
    ps.setString(2, pStoreId);
    return ps;
  } 
	
	protected PreparedStatement prepareShippedQueryStatement(Connection conn,String pStoreId, String pState,String pNumberOfDays, int lowEnd, int highEnd) throws SQLException {
    
	  String lSql = getShippedQuerySQL().replaceAll("SQL_INTERVAL", pNumberOfDays);
    vlogDebug ("SQL Statement for ShippedQueryStatement is " + lSql);
    PreparedStatement ps = conn.prepareStatement(lSql);
    ps.setString(1, pState);
    ps.setString(2, pStoreId);
    ps.setInt(3, lowEnd);
    ps.setInt(4, highEnd);
    return ps;
  }
	
	public List<StoreAllocationItem> getShippedAllocations (String pStoreId, String pState, String pNumberOfDays,int lowEnd, int highEnd) throws CommerceException {
    
    // query the message table
      DataSource ds = getDataSource();
      PreparedStatement ps = null;
      List<StoreAllocationItem> storeAllocationList = new ArrayList<StoreAllocationItem>();
      
    if (getDataSource() == null) {
      if (isLoggingDebug()) {
        logDebug("no configured datasource. aborting");
      }
      return storeAllocationList;
    }
     
      if (isLoggingDebug()) {
        logDebug("querying SQL datasource " + dataSource.toString() + " with SQL: " + getQuerySQL());
    }
    
    try (Connection conn = ds.getConnection()) {
      ps = prepareShippedQueryStatement(conn, pStoreId, pState, pNumberOfDays, lowEnd, highEnd);
      ResultSet rs = ps.executeQuery();
      
      try {
        while(rs.next()){
          
          StoreAllocationItem lStoreAllocation = new StoreAllocationItem ();
          
          lStoreAllocation.setCommerceItemId  (rs.getString("commerce_item_id"));
          lStoreAllocation.setOrderId     (rs.getString("order_id"));
          lStoreAllocation.setOrderNumber      (rs.getString("order_number"));
          lStoreAllocation.setShippingMethod  (rs.getString("shipping_method"));
          lStoreAllocation.setQuantity    (rs.getLong("quantity"));
          lStoreAllocation.setSkuId     (rs.getString("sku_id"));
          lStoreAllocation.setAllocationDate(rs.getTimestamp("allocation_date"));
          lStoreAllocation.setFirstName(rs.getString("first_name"));
          lStoreAllocation.setLastName(rs.getString("last_name"));
          lStoreAllocation.setAddress1(rs.getString("address1"));
          lStoreAllocation.setAddress2(rs.getString("address2"));
          lStoreAllocation.setCity(rs.getString("city"));
          lStoreAllocation.setShipState(rs.getString("ship_state"));
          lStoreAllocation.setPostalCode(rs.getString("postal_code"));
          lStoreAllocation.setCounty(rs.getString("county"));
          lStoreAllocation.setCountry(rs.getString("country"));
          lStoreAllocation.setPhoneNumber(rs.getString("phone_number"));
          lStoreAllocation.setContactEmail(rs.getString("contact_email"));
          lStoreAllocation.setState     (pState);
          lStoreAllocation.setStoreId     (pStoreId);
          lStoreAllocation.setBopisOrder(rs.getBoolean("bopis_order"));
          lStoreAllocation.setInPicking(rs.getBoolean("in_picking"));
          lStoreAllocation.setOrderSubmittedDate(rs.getTimestamp("order_date"));
          
          storeAllocationList.add(lStoreAllocation);
        }
      } catch (SQLException e) {
        vlogError(e,"Error in getShippedAllocations");
      }
    
    } catch (SQLException ex) {
      String errorMessage = "getShippedAllocations - Unable to get records";
      vlogError(ex, errorMessage);
    }
    
    return storeAllocationList;
  }
	
	public int getTotalShippedRecordsByStoreId(String pStoreId, String pState,String pNumberOfDays){
    int count = 0;
    
    if (getDataSource() == null) {
      if (isLoggingDebug()) {
        logDebug("no configured datasource. aborting");
      }
      return count;
    }
    
    // query the message table
    DataSource ds = getDataSource();
    PreparedStatement ps = null;
   
    if (isLoggingDebug()) {
      logDebug("querying SQL datasource " + dataSource.toString() + " with SQL: " + getShippedCountSQL());
    }
    
    try (Connection conn = ds.getConnection()) {
      ps = prepareShippedCountStatement(conn,pStoreId,pState,pNumberOfDays);
      ResultSet rs = ps.executeQuery();
      
      rs.next();
      count = rs.getInt("count");
    
    } catch (SQLException ex) {
      String errorMessage = "getShippedRecordsByStoreId - Unable to get records";
      vlogError(ex, errorMessage);
    
    }
      
    return count;
  }
	
	public int getFulfillmentStoreCountByOrderId(String pOrderId){
    int count = 0;
    
    if (getDataSource() == null) {
      if (isLoggingDebug()) {
        logDebug("no configured datasource. aborting");
      }
      return count;
    }
    
    // query the message table
      DataSource ds = getDataSource();
      PreparedStatement ps = null;
     
      if (isLoggingDebug()) {
        logDebug("querying SQL datasource " + dataSource.toString() + " with SQL: " + getFulfillmentStoreCountSql());
    }
    
    try (Connection conn = ds.getConnection()) {
      ps = conn.prepareStatement(getFulfillmentStoreCountSql());
      ps.setString(1, pOrderId);
      //ps = prepareCountStatement(conn,pStoreId,pState);
      ResultSet rs = ps.executeQuery();
      
      rs.next();
      count = rs.getInt("count");
    
    } catch (SQLException ex) {
      String errorMessage = "getTotalAllocatedRecordsByStoreId - Unable to get records";
      vlogError(ex, errorMessage);
    
    }
      
    return count;
  }
	
	
	// ***************************************************************
	// **  					Getters/Setters
	// ***************************************************************
	/** Reporting Repository **/
	MutableRepository mOmsOrderRepository;
	public MutableRepository getOmsOrderRepository() {
		return mOmsOrderRepository;
	}
	public void setOmsOrderRepository(MutableRepository pOmsOrderRepository) {
		this.mOmsOrderRepository = pOmsOrderRepository;
	}

	/** Transaction Manager **/
	private TransactionManager mTransactionManager;
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}
	public void setTransactionManager(TransactionManager pTransactionManager) {
		this.mTransactionManager = pTransactionManager;
	}
	
	/** Max Allocation Count */
	private int maxAllocationCount = 3;
	public int getMaxAllocationCount() {
		return maxAllocationCount;
	}
	
	/** OMSRepository Datasource */
	private DataSource dataSource;
	public void setMaxAllocationCount(int maxAllocationCount) {
		this.maxAllocationCount = maxAllocationCount;
	}
	
	public DataSource getDataSource() {
	   return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
	   this.dataSource = dataSource;
	}
	 
	/** Sql Query to get records by store */
	private String querySQL;
	public String getQuerySQL() {
	   return querySQL;
	}
	
	public void setQuerySQL(String querySQL) {
	   this.querySQL = querySQL;
	}
	 
	/** Sql Count Query to get total records by store */
	private String countSQL;
	public String getCountSQL() {
		return countSQL;
	}

	public void setCountSQL(String countSQL) {
		this.countSQL = countSQL;
	}
	
	private String shippedCountSQL;
  public String getShippedCountSQL() {
    return shippedCountSQL;
  }

  public void setShippedCountSQL(String shippedCountSQL) {
    this.shippedCountSQL = shippedCountSQL;
  }
	
  private String shippedQuerySQL;
  public String getShippedQuerySQL() {
    return shippedQuerySQL;
  }

  public void setShippedQuerySQL(String shippedQuerySQL) {
    this.shippedQuerySQL = shippedQuerySQL;
  }
  
  private String fulfillmentStoreCountSql;
  public String getFulfillmentStoreCountSql() {
    return fulfillmentStoreCountSql;
  }

  public void setFulfillmentStoreCountSql(String pFulfillmentStoreCountSql) {
    fulfillmentStoreCountSql = pFulfillmentStoreCountSql;
  }
  
  
	
}