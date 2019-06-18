/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.commerce.states.MFFShippingGroupStates;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.states.StateDefinitions;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.nucleus.GenericService;
import atg.repository.NamedQueryView;
import atg.repository.ParameterSupportView;
import atg.repository.Query;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import mff.MFFEnvironment;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;
import oms.commerce.order.MFFOMSOrderManager;

/**
 * This pipeline process will determine which store will fulfill each of the
 * items within a given order.
 * 
 * @author DMI
 * 
 */
public class ProcAllocateItems extends GenericService implements PipelineProcessor {

	public static final String	SQL_EXTRACT_LOGS	= "select status, log_message from mff_allocation_log order by log_ts";

	private final static int	SUCCESS				= 1;
	public final static String	SP_NAME				= "f_get_order_allocation";
  public boolean enableProcDebug;
  private String mGiftCardFulfillmentStore;
  
	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
		vlogDebug("Entering ProcAllocateItems - runProcess");

		Map lParams = (Map) pPipelineParams;
		Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		MFFOrderImpl lExtnOrder = (MFFOrderImpl) lOrder;
		vlogDebug("Starting Allocation Filter Chain - Order id = " + lExtnOrder.getId());
		String forcedStore = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_FORCED_STORE);
		vlogDebug("Forced store in pipeline params: " + forcedStore);
		List<String> forceAllocatedItems = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_FORCED_ALLOCATED_ITEMS);
		
		// Get type of allocation
		String lAllocationType = getTypeOfAllocation(lOrder);
		lParams.put(AllocationConstants.PIPELINE_PARAMETER_ALLOCATION_TYPE, lAllocationType);

		// Get allocations from Stored procedure
		List<ItemAllocation> lItemAllocations = new ArrayList<ItemAllocation>();
		String lOrderNumber = lOrder.getId();
		// Get bopis order flag
		boolean isBopisOrder = lExtnOrder.isBopisOrder();
		synchronized (this) 
		{
		  if(null!=lOrder && null!=forcedStore)
			{
				// Create item allocations on a forced store without the ora procedure
			  vlogDebug("Force Allocation order id: {0}", lExtnOrder.getId());
				lItemAllocations = createForceStoreItemAllocation(lExtnOrder, forcedStore,forceAllocatedItems);
			}else if(isBopisOrder){
			  vlogDebug("Bopis order id: {0}", lExtnOrder.getId());
			  String bopisStore = lExtnOrder.getBopisStore();
			  lItemAllocations = createStoreItemAllocation(lExtnOrder, bopisStore);
			} else if(lOrder.getTotalCommerceItemCount() >= getOrderQuantityThreshold()){
        vlogDebug("OrderQuantity Threshold Reached. Force Allocating order id: {0}", lExtnOrder.getId());
        lItemAllocations = createForceItemAllocation(lExtnOrder);
      }
			else
			{
				// Run stored procedure
				lItemAllocations = runAllocationProcedureViaRepository(lOrderNumber);
			}
			
			// Spool procedure output to the logs
		  if(isEnableProcDebug())
		    logProcedureOutput();

			// Add Allocations to the pipeline for downstream processing
			lParams.put(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS, lItemAllocations);

			// Propagate list of items to ship
			//List<String> lItemsToShip = getItemsToShip(lItemAllocations);
			//lParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP, lItemsToShip);

			// Create Shipping groups for Allocations
			createOrderAllocations(lAllocationType, lOrder, lItemAllocations);
		}
		
		vlogInfo("Successfully allocated the following order : id = {0}, number = {1}", lOrder.getId(), lExtnOrder.getOrderNumber());
		vlogDebug("Exiting ProcAllocateItems - runProcess");
		return SUCCESS;
	}

	/**
	 * Determine if this order is going through an initial allocation or if this
	 * is a subsequent allocation. Initial allocations run the shipping
	 * distribution and pay relationship creation.
	 * 
	 * We know it's an initial implementation if the shipping group has a status
	 * of INITIAL.
	 * 
	 * @param pOrder
	 *            ATG Order
	 */
	@SuppressWarnings("unchecked")
	protected String getTypeOfAllocation(Order pOrder) {
		boolean lInitialFound = false;

		// Get the shipping groups for this order
		List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
		for (ShippingGroup lShippingGroup : lShippingGroups) {

			if (lShippingGroup instanceof MFFHardgoodShippingGroup) {
				MFFHardgoodShippingGroup lHGShippingGroup = (MFFHardgoodShippingGroup) lShippingGroup;
				if (lHGShippingGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.INITIAL))
					lInitialFound = true;
			} else if(lShippingGroup instanceof ElectronicShippingGroup){
				ElectronicShippingGroup lEShippingGroup = (ElectronicShippingGroup) lShippingGroup;
				if (lEShippingGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.INITIAL))
					lInitialFound = true;
			}
		}
		if (lInitialFound)
			return AllocationConstants.INITIAL_ALLOCATION_TYPE;
		else
			return AllocationConstants.SUBSEQUENT_ALLOCATION_TYPE;
	}

	/**
	 * Create the required shipping groups for the defined order allocations.
	 * 
	 * @param pItemAllocations
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws CommerceException
	 */
	protected void createOrderAllocations(String pAllocationType, Order pOrder, List<ItemAllocation> pItemAllocations) throws CommerceException, InstantiationException, IllegalAccessException {

		boolean lInitialAllocation = false;

		if (pAllocationType.equalsIgnoreCase(AllocationConstants.INITIAL_ALLOCATION_TYPE))
			lInitialAllocation = true;

		// call order manager to allocate items
		getOmsOrderManager().allocateCommerceItems(lInitialAllocation, pOrder, pItemAllocations);
		
		
	}

	/**
	 * run the allocation procedure and stream the results back using the
	 * repository.
	 * 
	 * @param pOrderNumber
	 *            Order Number
	 * @return List of Item Allocations for this order
	 */
	private List<ItemAllocation> runAllocationProcedureViaRepository(String pOrderNumber) {
		RepositoryItem[] lItems = null;
		List<ItemAllocation> lItemAllocations = new ArrayList<ItemAllocation>();
		try {
			RepositoryView lOrderView = getOmsOrderManager().getOmsOrderRepository().getView("orderItem");
			if ((lOrderView instanceof NamedQueryView)) {
				NamedQueryView lView = (NamedQueryView) lOrderView;
				Query lNamedQuery = lView.getNamedQuery("orderAllocation");
				if ((lOrderView instanceof ParameterSupportView)) {
					lItems = ((ParameterSupportView) lOrderView).executeQuery(lNamedQuery, new Object[] { pOrderNumber });
				}
			}
		} catch (RepositoryException e) {
			vlogError(e, "Repository Exception: ");
		}
		vlogDebug("Total allocation items found: " + (lItems == null ? 0 : lItems.length));
		for (int i = 0; i < lItems.length; i++) {
			ItemAllocation lItemAllocation = new ItemAllocation();
			lItemAllocation.setOrderId((String) lItems[i].getPropertyValue("orderId"));
			lItemAllocation.setCommerceItemId((String) lItems[i].getPropertyValue("commerceItemId"));
			lItemAllocation.setQuantity((Long) lItems[i].getPropertyValue("quantity"));
			lItemAllocation.setSkuId((String) lItems[i].getPropertyValue("skuId"));
			lItemAllocation.setFulfillmentStore((String) lItems[i].getPropertyValue("fulfillmentStore"));
			lItemAllocation.setGCFulfillment((Boolean) lItems[i].getPropertyValue("giftCard"));
			lItemAllocation.setForceAllocate((Boolean) lItems[i].getPropertyValue("forceAllocate"));
			lItemAllocation.setSplitItem((Boolean) lItems[i].getPropertyValue("splitItem"));
			lItemAllocation.setDropShipItem((Boolean) lItems[i].getPropertyValue("dropShipItem"));
			vlogDebug(lItemAllocation.toString());
			lItemAllocations.add(lItemAllocation);
		}
		
		return lItemAllocations;
	}
	
	@SuppressWarnings("unchecked")
  private List<ItemAllocation> createForceStoreItemAllocation(MFFOrderImpl pOrder, String pStore,List<String> pItemToAllocate)
	  {
	    List<ItemAllocation> lItemAllocations = new ArrayList<ItemAllocation>();
	    boolean isBopisOrder = pOrder.isBopisOrder();
	    
	    if(pItemToAllocate != null && pItemToAllocate.size() > 0){
	      for(String commerceItemId : pItemToAllocate){
	        try {
            MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl)pOrder.getCommerceItem(commerceItemId);
            if(lCommerceItem != null){
              ItemAllocation lItemAllocation = new ItemAllocation();
              lItemAllocation.setOrderId(pOrder.getId());
              lItemAllocation.setCommerceItemId(lCommerceItem.getId());
              lItemAllocation.setQuantity(lCommerceItem.getQuantity());
              lItemAllocation.setSkuId(lCommerceItem.getCatalogRefId());
              lItemAllocation.setFulfillmentStore(pStore);
              vlogDebug(lItemAllocation.toString());
              lItemAllocations.add(lItemAllocation);
            }
          } catch (CommerceItemNotFoundException | InvalidParameterException e) {
            logError(e);
          } 
	      }
	    }else{
  	    // Find commerce items in forced allocation and create an item allocation
  	    List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
  	    for (CommerceItem lCommerceItem : lCommerceItems)
  	    {
  	      if(lCommerceItem.getState()==StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.FORCED_ALLOCATION) ||
  	          isBopisOrder)
  	      {
  	        ItemAllocation lItemAllocation = new ItemAllocation();
  	        lItemAllocation.setOrderId(pOrder.getId());
  	        lItemAllocation.setCommerceItemId(lCommerceItem.getId());
  	        lItemAllocation.setQuantity(lCommerceItem.getQuantity());
  	        lItemAllocation.setSkuId(lCommerceItem.getCatalogRefId());
  	        lItemAllocation.setFulfillmentStore(pStore);
  	        vlogDebug(lItemAllocation.toString());
  	        lItemAllocations.add(lItemAllocation);
  	      }
  	    }
	    }
	    vlogDebug("Total items to allocate: " + (lItemAllocations == null ? 0 : lItemAllocations.size()));
	    
	    return lItemAllocations;
	  }

	/**
	 * Create the allocation to the specific store for the items in forced allocate state or 
	 * for bopis order items.
	 *  
	 * @param pOrder the order number
	 * @param pStore the store to allocate the items to
	 * @return List of item allocations for this order
	 */
	@SuppressWarnings("unchecked")
  private List<ItemAllocation> createStoreItemAllocation(MFFOrderImpl pOrder, String pStore)
	{
		List<ItemAllocation> lItemAllocations = new ArrayList<ItemAllocation>();
		boolean isBopisOrder = pOrder.isBopisOrder();
		
		// Find commerce items in forced allocation and create an item allocation
		List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
		for (CommerceItem lCommerceItem : lCommerceItems)
		{
			if(lCommerceItem.getState()==StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.FORCED_ALLOCATION) ||
			    isBopisOrder)
			{
				ItemAllocation lItemAllocation = new ItemAllocation();
				lItemAllocation.setOrderId(pOrder.getId());
				lItemAllocation.setCommerceItemId(lCommerceItem.getId());
				lItemAllocation.setQuantity(lCommerceItem.getQuantity());
				lItemAllocation.setSkuId(lCommerceItem.getCatalogRefId());
				lItemAllocation.setFulfillmentStore(pStore);
				vlogDebug(lItemAllocation.toString());
				lItemAllocations.add(lItemAllocation);
			}
		}
		vlogDebug("Total items to allocate: " + (lItemAllocations == null ? 0 : lItemAllocations.size()));
		
		return lItemAllocations;
	}
	
  @SuppressWarnings("unchecked")
  private List<ItemAllocation> createForceItemAllocation(MFFOrderImpl pOrder)
  {
    List<ItemAllocation> lItemAllocations = new ArrayList<ItemAllocation>();
    
    List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
    for (CommerceItem lCommerceItem : lCommerceItems)
    {
        MFFCommerceItemImpl lCommerceItemImpl = (MFFCommerceItemImpl)lCommerceItem;
        if(lCommerceItemImpl.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.PENDING_ALLOCATION) ||
            lCommerceItemImpl.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.INITIAL)){
          ItemAllocation lItemAllocation = new ItemAllocation();
          lItemAllocation.setOrderId(pOrder.getId());
          lItemAllocation.setCommerceItemId(lCommerceItem.getId());
          lItemAllocation.setQuantity(lCommerceItem.getQuantity());
          lItemAllocation.setSkuId(lCommerceItem.getCatalogRefId());
          if(getOmsOrderManager().isGiftCardItem(lCommerceItemImpl))
          {
           lItemAllocation.setGCFulfillment(true); 
           lItemAllocation.setFulfillmentStore(getGiftCardFulfillmentStore());
          }else if(lCommerceItemImpl.getDropShip()){
            lItemAllocation.setDropShipItem(true);
          }else{
            lItemAllocation.setForceAllocate(true);
          }
          vlogDebug(lItemAllocation.toString());
          lItemAllocations.add(lItemAllocation);
        }
    }
    vlogDebug("Total items to allocate: " + (lItemAllocations == null ? 0 : lItemAllocations.size()));
    
    return lItemAllocations;
  }
	
	/**
	 * Retrieve the logs from the stored procedure so they can be written to the
	 * ATG log.
	 * 
	 * @return
	 * @throws KWIUtilException
	 */
	public void logProcedureOutput() {
		Connection lConnection = null;
		DataSource lDataSource = getDataSource();
		ResultSet lResultSet = null;
		PreparedStatement lStatement = null;

		// SQL to find the extract orders
		String lSql = SQL_EXTRACT_LOGS;
		vlogDebug("SQL Statement to extract logs is  " + lSql);

		try {
			lConnection = lDataSource.getConnection();
			lStatement = lConnection.prepareStatement(lSql);
			lResultSet = lStatement.executeQuery();
			while (lResultSet.next()) {
				String lLogMessage = lResultSet.getString("log_message");
				String lStatus = lResultSet.getString("status");
				if (lStatus.equals("DEBUG"))
					vlogDebug(lLogMessage);
				else
					vlogError(lLogMessage);
			}
		} catch (SQLException ex) {
			vlogError(ex, "Unable to get the results from the stored procedure");
		} finally {
			try {
				lConnection.close();
			} catch (SQLException ex) {
				vlogError(ex, "Error closing the SQL Connection used to get the log results");
			}
		}
	}

	/**
	 * Run the stored procedure using the order number from the properties file.
	 * 
	 */
	public void runStoreprocedure() {
		runAllocationProcedureViaRepository(getOrderNumber());
	}

	/**
	 * Propagate the list of items to ship for reporting.
	 * 
	 * @param pItemAllocations
	 *            Item Allocations
	 * @return
	 */
	/*protected List<String> getItemsToShip(List<ItemAllocation> pItemAllocations) {
		List<String> lItemsToShip = new ArrayList<String>();
		for (ItemAllocation lItemAllocation : pItemAllocations) {
			if (lItemAllocation.isStoreFulfillment() || lItemAllocation.isWarehouseFulfillment() || lItemAllocation.isGCFulfillment())
				lItemsToShip.add(lItemAllocation.getCommerceItemId());
		}
		return lItemsToShip;
	}*/
	
	
  private long getOrderQuantityThreshold(){
    String lErrorMessage     = "getOrderQuantityThreshold failed for SiteId: %s Error: %s";
    long orderQuantityThreshold = 0;
    Site curSite = SiteContextManager.getCurrentSite();
    if (curSite != null){
      orderQuantityThreshold = (Long)curSite.getPropertyValue(MFFConstants.PROPERTY_ORDER_QUANTITY_THRESHOLD);
    }else{
      try {
        RepositoryItem siteItem = getSiteManager().getSite(getEnvironment().getDefaultSiteId());
        orderQuantityThreshold = (Long)siteItem.getPropertyValue(MFFConstants.PROPERTY_ORDER_QUANTITY_THRESHOLD);
      } catch (RepositoryException re) {
        vlogError(re,String.format(lErrorMessage, getEnvironment().getDefaultSiteId(),"Repository Error"));
      }
    }
    
    vlogDebug("Inside getOrderQuantityThreshold - returning orderQuantityThreshold as : {0}",orderQuantityThreshold);
    return orderQuantityThreshold;
  }
  
  private MFFEnvironment mEnvironment;
  
  public MFFEnvironment getEnvironment() {
    return mEnvironment;
  }

  public void setEnvironment(MFFEnvironment pEnvironment) {
    mEnvironment = pEnvironment;
  }

  private SiteManager mSiteManager;
  
  public SiteManager getSiteManager() {
    return mSiteManager;
  }

  public void setSiteManager(SiteManager pSiteManager) {
    this.mSiteManager = pSiteManager;
  }
  
	/** Data Source **/
	private DataSource	mDataSource;

	public DataSource getDataSource() {
		return mDataSource;
	}

	public void setDataSource(DataSource pDataSource) {
		mDataSource = pDataSource;
	}

	MFFOMSOrderManager	mOmsOrderManager;

	public MFFOMSOrderManager getOmsOrderManager() {
		return mOmsOrderManager;
	}

	public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
		this.mOmsOrderManager = pOmsOrderManager;
	}

	String	mOrderNumber	= "4000401";

	public String getOrderNumber() {
		return mOrderNumber;
	}

	public void setOrderNumber(String pOrderNumber) {
		this.mOrderNumber = pOrderNumber;
	}

  public boolean isEnableProcDebug() {
    return enableProcDebug;
  }

  public void setEnableProcDebug(boolean pEnableProcDebug) {
    enableProcDebug = pEnableProcDebug;
  }

  public String getGiftCardFulfillmentStore() {
    return mGiftCardFulfillmentStore;
  }

  public void setGiftCardFulfillmentStore(String pGiftCardFulfillmentStore) {
    mGiftCardFulfillmentStore = pGiftCardFulfillmentStore;
  }

}