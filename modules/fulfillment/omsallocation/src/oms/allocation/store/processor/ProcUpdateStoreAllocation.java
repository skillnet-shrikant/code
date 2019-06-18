/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.store.processor;

import java.util.List;
import java.util.Map;

import oms.allocation.item.AllocationConstants;
import oms.allocation.store.StoreAllocationManager;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;

/**
 * This pipeline process will update the states on the shipping 
 * group and commerce item for items that have been shipped, cancelled
 * or declined.  The process will also update the inventory for each 
 * of the affect items in the order.
 * 
 * @author DMI
 *
 */
public class ProcUpdateStoreAllocation 
	extends GenericService  
	implements PipelineProcessor {

	private final static int SUCCESS 			= 1;
	
	public int[] getRetCodes() {
		int[] ret = {SUCCESS};
		return ret;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess (Object pPipelineParams, PipelineResult pPipelineResults) 
		throws Exception {
		vlogDebug("Entering ProcUpdateStoreAllocation - runProcess");
	
		Map lParams 		  			= (Map) pPipelineParams;
		Order lOrder 	 	  			= (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		List <String> lItemsToShip 		= (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
		List <String> lItemsToCancel 	= (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL);
		List <String> lItemsToDecline 	= (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_DECLINE);
		List <String> lItemsToPickup = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_PICKUP);
		
		// Process Shipments 
		if(getProcessName().equalsIgnoreCase("SHIPPED"))
			processShipments (lOrder, lItemsToShip);
		
		// Process Cancels 
		if(getProcessName().equalsIgnoreCase("CANCELLED"))
			processCancels (lOrder, lItemsToCancel);
		
		// Process Declines
		if(getProcessName().equalsIgnoreCase("DECLINE"))
			processDeclines (lOrder, lItemsToDecline);
		
	// Process ReadyForPickUp
    if (getProcessName().equalsIgnoreCase("PICKUP"))
      processReadyForPickup(lOrder, lItemsToPickup,lParams);
		
		vlogDebug("Exiting ProcUpdateStoreAllocation - runProcess");
		return SUCCESS;
	}
	
	/**
	 * Process the shipment messages by marking the store allocation record
	 * as shipped.
	 *
	 * @param pOrder			ATG Order
	 * @param pItemsToShip		List of commerce item IDs to ship
	 * @throws Exception		
	 */
	protected void processShipments (Order pOrder, List<String> pItemsToShip) 
		throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;	
		vlogDebug ("Begin process the items to ship for order number {0}",  lOrder.getId());
		
		// Bail if there is nothing to process
		if (pItemsToShip == null || pItemsToShip.size() < 1) {
			vlogDebug ("No items to ship were found for order number {0}",  lOrder.getId());
			return;
		}
		
		for (String lCommerceItemId : pItemsToShip) {
			vlogDebug ("Marking item {0} for order number: {1} as shipped",  lCommerceItemId, lOrder.getId());
			getStoreAllocationManager().shipLineItem(pOrder, lCommerceItemId);
		}	
		vlogDebug ("End processing the items to ship for order number {0}",  lOrder.getId());
	}

	/**
	 * Process the shipment messages by marking the store allocation record
	 * as cancelled.
	 *
	 * @param pOrder			ATG Order
	 * @param pItemsToCancel	List of commerce item IDs to cancel
	 * @throws Exception		
	 */
	protected void processCancels (Order pOrder, List<String> pItemsToCancel) 
		throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;	
		vlogDebug ("Begin process the items to cancel for order number {0}",  lOrder.getId());
		
		// Bail if there is nothing to process
		if (pItemsToCancel == null || pItemsToCancel.size() < 1) {
			vlogDebug ("No items to cancel were found for order number {0}",  lOrder.getId());
			return;
		}
		
		for (String lCommerceItemId : pItemsToCancel) {
			MFFCommerceItemImpl lCommerceItem = getCommerceItem (lOrder, lCommerceItemId);
			if(!StringUtils.isBlank(lCommerceItem.getFulfillmentStore()) && 
					!lCommerceItem.getFulfillmentStore().equalsIgnoreCase(AllocationConstants.WAREHOUSE_STORE) &&
					!lCommerceItem.getFulfillmentStore().equalsIgnoreCase(AllocationConstants.BACKORDER_STORE)){
				vlogDebug ("Marking item {0} for order number: {1} as cancelled",  lCommerceItemId, lOrder.getId());
				getStoreAllocationManager().cancelLineItem(pOrder, lCommerceItemId);
			}
		}	
		vlogDebug ("End processing the items to cancel for order number {0}",  lOrder.getId());
	}
	

	/**
	 * Process the readyForPickUp messages by marking the store allocation record
	 * as readyForPickUp.
	 *
	 * @param pOrder			ATG Order
	 * @param pItemsToPickup		List of commerce item IDs to mark for readyForPickUp
	 * @throws Exception		
	 */
	protected void processReadyForPickup (Order pOrder,  List<String> pItemsToPickup,Map lParams) 
		throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;	
		String pickUpInstructions = "";
		vlogDebug ("Begin process the items to readyForPickUp for order number {0}",  lOrder.getId());
		
		// Bail if it's not BOPIS order
    if (!lOrder.isBopisOrder()) {
      vlogDebug("Not a bopis order {0} so skip processing", lOrder.getId());
      return;
    }
		
		// Bail if there is nothing to process
		if (pItemsToPickup == null || pItemsToPickup.size() < 1) {
			vlogDebug ("No items to pick were found for order number {0}",  lOrder.getId());
			return;
		}
		
		if(lParams.get(AllocationConstants.PIPELINE_PARAMETER_SPECIAL_INST) != null){
		  pickUpInstructions = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_SPECIAL_INST);
		}
		
		for (String lCommerceItemId : pItemsToPickup) {
			vlogDebug ("Marking item {0} for order number: {1} as readyForPickUp",  lCommerceItemId, lOrder.getId());
			getStoreAllocationManager().readyForPickLineItem(pOrder, lCommerceItemId,pickUpInstructions);
		}	
		vlogDebug("End processing the items for pickup for order number {0}", lOrder.getId());
	}
	
	/**
  * Process the decline messages by marking the store allocation record
  * as declined.
  *
  * @param pOrder      ATG Order
  * @param pItemsToDecline   List of commerce item IDs to ship
  * @throws Exception    
  */
 protected void processDeclines (Order pOrder, List<String> pItemsToDecline) 
   throws Exception {
   MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;  
   vlogDebug ("Begin process the items to decline for order number {0}",  lOrder.getId());
   
   // Bail if there is nothing to process
   if (pItemsToDecline == null || pItemsToDecline.size() < 1) {
     vlogDebug ("No items to decline were found for order number {0}",  lOrder.getId());
     return;
   }
   
   for (String lCommerceItemId : pItemsToDecline) {
     vlogDebug ("Marking item {0} for order number: {1} as declined",  lCommerceItemId, lOrder.getId());
     getStoreAllocationManager().declineLineItem(pOrder, lCommerceItemId);
   } 
   vlogDebug ("End processing the items to decline for order number {0}",  lOrder.getId());
 }
	
	/**
	 * Get the commerce item from the order given the Commerce 
	 * Item ID.
	 *  
	 * @param pOrder				ATG Order
	 * @param lCommerceItemId		Commerce Item Id
	 * @return						Commerce item
	 * @throws Exception
	 */
	protected MFFCommerceItemImpl getCommerceItem (Order pOrder, String lCommerceItemId) 
		throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;	
		MFFCommerceItemImpl lCommerceItem;
		try {
			lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lCommerceItemId);
		} catch (CommerceItemNotFoundException ex) {
			String lErrorMessage = String.format ("Commerce item not found for decline order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
			vlogError (ex, lErrorMessage);
			throw new Exception (lErrorMessage);
		} catch (InvalidParameterException ex) {
			String lErrorMessage = String.format ("Commerce item not found for decline order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
			vlogError (ex, lErrorMessage);
			throw new Exception (lErrorMessage);
		}
		return lCommerceItem;
	}
	
	StoreAllocationManager mStoreAllocationManager;
	public StoreAllocationManager getStoreAllocationManager() {
		return mStoreAllocationManager;
	}
	public void setStoreAllocationManager(StoreAllocationManager pStoreAllocationManager) {
		this.mStoreAllocationManager = pStoreAllocationManager;
	}	
	
	String mProcessName;

	public String getProcessName() {
		return mProcessName;
	}
	public void setProcessName(String mProcessName) {
		this.mProcessName = mProcessName;
	}
	
}