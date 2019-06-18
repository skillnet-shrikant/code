/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oms.allocation.item.AllocationConstants;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;

public class TestAllocationPipeline 
	extends GenericService {

	private List<String> itemsToShip;
	private List<String> itemsToDecline;
	private List<String> itemsToPickup;
	private List<String> itemsToCancel;
	private	String forceAllocateOrderId;
	private	String forceAllocateStore;
	private String defaultCancelReasonCode;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void runForceAllocationPipeline() throws RunProcessException 
	{
		// Properties ForceAllocateOrderId and forceAllocateStore must be set
		if(null!=getForceAllocateOrderId() && null!=getForceAllocateStore())
		{
			// Get the order
			Order omsOrder = null;
			try 
			{
				omsOrder = getOmsOrderManager().loadOrder(getForceAllocateOrderId());
			} 
			catch (CommerceException e) 
			{
				vlogError (e, "Unable to load order ID: {0}", getForceAllocateOrderId());
				return;
			}
			
			Map lPipelineParams = new HashMap();
			lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, omsOrder);
			lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_FORCED_STORE, getForceAllocateStore());
			try 
			{
				vlogInfo ("Calling allocation pipeline: {0} for order number {1} and store {2}", AllocationConstants.PIPELINE_ALLOCATE_ORDER, getForceAllocateOrderId(), getForceAllocateStore());
				getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_ALLOCATE_ORDER, lPipelineParams);
				vlogInfo ("Successfully allocated order number: {0} to store {1}", getForceAllocateOrderId(), getForceAllocateStore());
			}
			catch (RunProcessException ex)
			{
				vlogError(ex, "Error allocating order number {0}", getForceAllocateOrderId());
			}
		}
	}
	
	
    // **************************************************
	//
    //                 Store Shipment
	//
    // **************************************************
	public void runStoreShipmentPipeline () 
		throws RunProcessException {
		String lOrderId = getStoreShipmentOrderId();
		
		runStoreShipmentPipeline (lOrderId);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void runStoreShipmentPipeline (String pOrderId) 
		throws RunProcessException {
		
		List<String> lItemsToShip = new ArrayList<String> ();
		
		// Get the order ID
		Order lOmsOrder = null;
		try {
			lOmsOrder = getOmsOrderManager().loadOrder(pOrderId);
		} catch (CommerceException e) {
			vlogError (e, "Unable to load order ID: {0}", pOrderId);
		}
		MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;

		// Get the items to ship
		List<MFFCommerceItemImpl> lCommerceItems = lOrder.getCommerceItems();
		for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
			if(getItemsToShip().contains(lCommerceItem.getId())){
				vlogDebug ("Adding Commerce Item Id: {0} for Order Number {1} for shipment", lCommerceItem.getId(), lOrder.getId());
				lItemsToShip.add(lCommerceItem.getId());
			}
		}
		
		Map lPipelineParams = new HashMap();
		lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, lOrder); 
		lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
		//lPipelineParams.put(OMSOrderConstants.PIPELINE_TRACKING_NO, getTrackingNo());
		lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_AGENT_USER_ID,"testComponent");
		try {
			getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);
			vlogInfo ("Shipped order number: {0} Items: {1}", lOrder.getId(), lItemsToShip);
		}
		catch (RunProcessException e)	{
			vlogError(e, "Error shipping order {0} ", lOrder.getId());
		}
	}

    // **************************************************
	//
    //            Store/Warehouse Cancellation
	//
    // **************************************************
	public void runCancelPipeline () 
		throws RunProcessException {
		String lOrderId = getCancelOrderId();
		
		runCancelPipeline (lOrderId);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void runCancelPipeline (String pOrderId) 
		throws RunProcessException {
		
		List<String> lItemsToCancel = new ArrayList<String> ();
		HashMap<String,String> itemToReasonCodeMap = new HashMap<String,String>();
		
		// Get the order ID
		Order lOmsOrder = null;
		try {
			lOmsOrder = getOmsOrderManager().loadOrder(pOrderId);
		} catch (CommerceException e) {
			vlogError (e, "Unable to load order ID: {0}", pOrderId);
		}
		MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;
		
		// Get the items to ship
		List<MFFCommerceItemImpl> lCommerceItems = lOrder.getCommerceItems();
		for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
			vlogDebug ("Adding Commerce Item Id: {0} for Order Number {1} for cancellation", lCommerceItem.getId(), lOrder.getId());
			lItemsToCancel.add(lCommerceItem.getId());
			itemToReasonCodeMap.put(lCommerceItem.getId(), getDefaultCancelReasonCode());
		}

		Map lPipelineParams = new HashMap();
		lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, lOrder); 
		lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_CANCEL, lItemsToCancel);
		lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC, getCancelDescription());
		if(itemToReasonCodeMap != null && itemToReasonCodeMap.size() > 0){
      lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP,itemToReasonCodeMap);
    }
		try {
			getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_CANCEL, lPipelineParams);
			vlogInfo ("Cancelled order number: {0} Items: {1}", lOrder.getId(), lItemsToCancel);
		}
		catch (RunProcessException e)	{
			vlogError(e, "Error cancelling order {0} ", lOrder.getId());
		}
	}
	
	
    // **************************************************
	//
    //            Store decline
	//
    // **************************************************
	public void runDeclinePipeline () 
		throws RunProcessException {
		String lOrderId = getDeclineOrderId();
		
		runDeclinePipeline (lOrderId);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void runDeclinePipeline (String pOrderId) 
		throws RunProcessException {
		
		List<String> lItemsToDecline = new ArrayList<String> ();
		
		// Get the order ID
		Order lOmsOrder = null;
		try {
			lOmsOrder = getOmsOrderManager().loadOrder(pOrderId);
		} catch (CommerceException e) {
			vlogError (e, "Unable to load order ID: {0}", pOrderId);
		}
		MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;
		
		// Get the items to decline
		List<MFFCommerceItemImpl> lCommerceItems = lOrder.getCommerceItems();
		for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
      if(getItemsToDecline().contains(lCommerceItem.getId())){
        vlogDebug ("Adding Commerce Item Id: {0} for Order Number {1} for decline", lCommerceItem.getId(), lOrder.getId());
        lItemsToDecline.add(lCommerceItem.getId());
      }
    }

		Map lPipelineParams = new HashMap();
		lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, lOrder); 
		lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_DECLINE, lItemsToDecline);
		try {
			getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_DECLINE, lPipelineParams);
			vlogInfo ("Declined order number: {0} Items: {1}", lOrder.getId(), lItemsToDecline);
		}
		catch (RunProcessException e)	{
			vlogError(e, "Error declining order {0} ", lOrder.getId());
		}
	}
	
	// **************************************************
//
//            Store Process
//
// **************************************************
	public void runStoreProcessPipeline () 
		throws RunProcessException {
		String lOrderId = getStoreProcessOrderId();
		
		runStoreProcessPipeline (lOrderId);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void runStoreProcessPipeline (String pOrderId) 
		throws RunProcessException {
		
		List<String> lItemsToDecline = new ArrayList<String> ();
		List<String> lItemsToShip = new ArrayList<String> ();
		List<String> lItemsToPickup = new ArrayList<String>();
		List<String> lIemsToCancel = new ArrayList<String>();
		HashMap<String,String> itemToReasonCodeMap = new HashMap<String,String>();
		
		// Get the order ID
		Order lOmsOrder = null;
		try {
			lOmsOrder = getOmsOrderManager().loadOrder(pOrderId);
		} catch (CommerceException e) {
			vlogError (e, "Unable to load order ID: {0}", pOrderId);
		}
		MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;
		
		// Get the items to decline
		List<MFFCommerceItemImpl> lCommerceItems = lOrder.getCommerceItems();
		for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
			if(getItemsToDecline() != null && getItemsToDecline().size() > 0 && getItemsToDecline().contains(lCommerceItem.getId())){
				vlogDebug ("Adding Commerce Item Id - 1 : {0} for Order Number {1} for decline", lCommerceItem.getId(), lOrder.getId());
				lItemsToDecline.add(lCommerceItem.getId());
			}else if(getItemsToShip() != null && getItemsToShip().size() > 0 && getItemsToShip().contains(lCommerceItem.getId())){
				vlogDebug ("Adding Commerce Item Id - 1 : {0} for Order Number {1} for shipment", lCommerceItem.getId(), lOrder.getId());
				lItemsToShip.add(lCommerceItem.getId());
			}else if(getItemsToPickup() != null && getItemsToPickup().size() > 0 && getItemsToPickup().contains(lCommerceItem.getId())){
        vlogDebug ("Adding Commerce Item Id - 1 : {0} for Order Number {1} for pickup", lCommerceItem.getId(), lOrder.getId());
        lItemsToPickup.add(lCommerceItem.getId());
      }else if(getItemsToCancel() != null && getItemsToCancel().size() > 0 && getItemsToCancel().contains(lCommerceItem.getId())){
        vlogDebug ("Adding Commerce Item Id - 1 : {0} for Order Number {1} for cancel", lCommerceItem.getId(), lOrder.getId());
        lIemsToCancel.add(lCommerceItem.getId());
        itemToReasonCodeMap.put(lCommerceItem.getId(), getDefaultCancelReasonCode());
      }
		}

		Map lPipelineParams = new HashMap();
		lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, lOrder); 
		lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_DECLINE, lItemsToDecline);
		lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
		lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_PICKUP, lItemsToPickup);
		lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_CANCEL, lIemsToCancel);
		lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC, getCancelDescription());
		if(itemToReasonCodeMap != null && itemToReasonCodeMap.size() > 0){
		  lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP,itemToReasonCodeMap);
		}
		if(lItemsToShip != null && lItemsToShip.size() > 0){
		  lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_AGENT_USER_ID,"testComponent");
		}
		
		try {
			getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_PROCESS, lPipelineParams);
			vlogInfo ("StoreOrderProcess order number: {0} ItemsToDecline: {1} ItemsToShip : {2} lItemsToPickup : {3} ItemsToCancel : {4}", 
			    lOrder.getId(), lItemsToDecline,lItemsToShip,lItemsToPickup,lIemsToCancel);
		}
		catch (RunProcessException e)	{
			vlogError(e, "Error declining order {0} ", lOrder.getId());
		}
	}
	
 // **************************************************
 //    ReadyForPickup OrderId
 // **************************************************
 public void runReadyForPickUp () 
  throws RunProcessException {
  String lOrderId = getReadyForPickUpOrderId();
  
  runReadyForPickUp (lOrderId);
 }

 @SuppressWarnings({ "rawtypes", "unchecked" })
 protected void runReadyForPickUp (String pOrderId) 
  throws RunProcessException {
  
  List<String> lItemsToPickUp = new ArrayList<String> ();
  
  // Get the order ID
  Order lOmsOrder = null;
  try {
    lOmsOrder = getOmsOrderManager().loadOrder(pOrderId);
  } catch (CommerceException e) {
    vlogError (e, "Unable to load order ID: {0}", pOrderId);
  }
  MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;
  
  // Get the items to pickup
  List<MFFCommerceItemImpl> lCommerceItems = lOrder.getCommerceItems();
  for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
    vlogDebug ("Adding Commerce Item Id: {0} for Order Number {1} for readyForPickup", lCommerceItem.getId(), lOrder.getId());
    lItemsToPickUp.add(lCommerceItem.getId());
  }

  Map lPipelineParams = new HashMap();
  lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, lOrder); 
  lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_PICKUP, lItemsToPickUp);
  try {
    getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_READY_FOR_PICKUP, lPipelineParams);
    vlogInfo ("ReadyForPickup order number: {0} Items: {1}", lOrder.getId(), lItemsToPickUp);
  }
  catch (RunProcessException e) {
    vlogError(e, "Error ReadyForPickup order {0} ", lOrder.getId());
  }
}

	// ******************************************************
	// 					Getter/setters
	// ******************************************************
	/** Store Shipment Order Id **/
	String mStoreShipmentOrderId;
	public String getStoreShipmentOrderId() {
		return mStoreShipmentOrderId;
	}
	public void setStoreShipmentOrderId(String pStoreShipmentOrderId) {
		this.mStoreShipmentOrderId = pStoreShipmentOrderId;
	}

	String mGcShipmentOrderId;
	/**
	 * @return the gcShipmentOrderId
	 */
	public String getGcShipmentOrderId() {
		return mGcShipmentOrderId;
	}

	/**
	 * @param pGcShipmentOrderId the gcShipmentOrderId to set
	 */
	public void setGcShipmentOrderId(String pGcShipmentOrderId) {
		mGcShipmentOrderId = pGcShipmentOrderId;
	}

	String mTrackingNo;
	public String getTrackingNo() {
		return mTrackingNo;
	}
	public void setTrackingNo(String pTrackingNo) {
		this.mTrackingNo = pTrackingNo;
	}

	/** Store Cancel Order Id **/
	String mCancelOrderId;
	public String getCancelOrderId() {
		return mCancelOrderId;
	}
	public void setCancelOrderId(String pCancelOrderId) {
		this.mCancelOrderId = pCancelOrderId;
	}
	
	/** Cancel Description **/
	String mCancelDescription;
	public String getCancelDescription() {
    return mCancelDescription;
  }
  public void setCancelDescription(String pCancelDescription) {
    mCancelDescription = pCancelDescription;
  }

  /** Store Cancel Item Id **/
	String mCancelItemId;
	public String getCancelItemId() {
		return mCancelItemId;
	}
	public void setCancelItemId(String pCancelItemId) {
		this.mCancelItemId = pCancelItemId;
	}

	/** Store Decline Order Id **/
	String mDeclineOrderId;
	public String getDeclineOrderId() {
		return mDeclineOrderId;
	}
	public void setDeclineOrderId(String pDeclineOrderId) {
		this.mDeclineOrderId = pDeclineOrderId;
	}
	
	/** ReadyForPickUp Order Id */
	private String mReadyForPickUpOrderId;
	public String getReadyForPickUpOrderId() {
    return mReadyForPickUpOrderId;
  }
  public void setReadyForPickUpOrderId(String pReadyForPickUpOrderId) {
    mReadyForPickUpOrderId = pReadyForPickUpOrderId;
  }

  /** Store Process Order Id **/
	String mStoreProcessOrderId;
	public String getStoreProcessOrderId() {
		return mStoreProcessOrderId;
	}
	public void setStoreProcessOrderId(String pStoreProcessOrderId) {
		this.mStoreProcessOrderId = pStoreProcessOrderId;
	}
	
	public void runGCShipmentPipeline () 
			throws RunProcessException {
			String lOrderId = getGcShipmentOrderId();
			
			runGCShipmentPipeline (lOrderId);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected void runGCShipmentPipeline (String pOrderId) 
			throws RunProcessException {
			
			List<String> lItemsToShip = new ArrayList<String> ();
			
			// Get the order ID
			Order lOmsOrder = null;
			try {
				lOmsOrder = getOmsOrderManager().loadOrder(pOrderId);
			} catch (CommerceException e) {
				vlogError (e, "Unable to load order ID: {1}", pOrderId);
			}
			MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;

			// Get the items to ship
			List<MFFCommerceItemImpl> lCommerceItems = lOrder.getCommerceItems();
			for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
				vlogDebug ("Adding Commerce Item Id: {0} for Order Number {1} for shipment", lCommerceItem.getId(), lOrder.getId());
				lItemsToShip.add(lCommerceItem.getId());
			}
			
			Map lPipelineParams = new HashMap();
			/*lPipelineParams.put(ReportingConstants.PIPELINE_OMS_ORDER, lOrder); 
			lPipelineParams.put(ReportingConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);*/
			try {
				getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_GC_SHIPMENT, lPipelineParams);
				vlogInfo ("Shipped order number: {0} Items: {1}", lOrder.getId(), lItemsToShip);
			}
			catch (RunProcessException e)	{
				vlogError(e, "Error shipping order {0} ", lOrder.getId());
			}
		}

	
	// ******************************************************
	// 					Other Getter/setters
	// ******************************************************
	OMSOrderManager mOrderManager;
    public void setOmsOrderManager(OMSOrderManager pOrderManager)   {
    	mOrderManager = pOrderManager;
    }

    public OMSOrderManager getOmsOrderManager()   {
    	return mOrderManager;
    }
	
	/**  Fulfillment pipeline Manager **/ 
	private PipelineManager mFulfillmentPipelineManager;
	public PipelineManager getFulfillmentPipelineManager() {
		return mFulfillmentPipelineManager;
	}
	public void setFulfillmentPipelineManager(PipelineManager pFulfillmentPipelineManager) {
		mFulfillmentPipelineManager = pFulfillmentPipelineManager;
	}

	/**
	 * @return the itemsToShip
	 */
	public List<String> getItemsToShip() {
		return itemsToShip;
	}

	/**
	 * @param pItemsToShip the itemsToShip to set
	 */
	public void setItemsToShip(List<String> pItemsToShip) {
		itemsToShip = pItemsToShip;
	}

	public String getForceAllocateOrderId() {
		return forceAllocateOrderId;
	}

	public void setForceAllocateOrderId(String forceAllocateOrderId) {
		this.forceAllocateOrderId = forceAllocateOrderId;
	}

	public String getForceAllocateStore() {
		return forceAllocateStore;
	}

	public void setForceAllocateStore(String forceAllocateStore) {
		this.forceAllocateStore = forceAllocateStore;
	}
	
	public List<String> getItemsToDecline() {
		return itemsToDecline;
	}
	
	public void setItemsToDecline(List<String> itemsToDecline) {
		this.itemsToDecline = itemsToDecline;
	}

  public List<String> getItemsToPickup() {
    return itemsToPickup;
  }

  public void setItemsToPickup(List<String> itemsToPickup) {
    this.itemsToPickup = itemsToPickup;
  }

  public List<String> getItemsToCancel() {
    return itemsToCancel;
  }

  public void setItemsToCancel(List<String> pItemsToCancel) {
    itemsToCancel = pItemsToCancel;
  }


  public String getDefaultCancelReasonCode() {
    return defaultCancelReasonCode;
  }


  public void setDefaultCancelReasonCode(String pDefaultCancelReasonCode) {
    defaultCancelReasonCode = pDefaultCancelReasonCode;
  }
  	
}
