/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.List;
import java.util.Map;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.core.util.ResourceUtils;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;

/**
 * This pipeline process will update the inventory for the 
 * allocations.
 * 
 * @author DMI
 *
 */
public class ProcUpdateInventory 
	extends GenericService  
	implements PipelineProcessor {

	private final static int SUCCESS 		= 1;
	private final static String INVENTORY_ERROR_KEY = "csc.error.inv.allocation";
	
	public int[] getRetCodes() {
		int[] ret = {SUCCESS};
		return ret;
	}
	
	static final String MY_RESOURCE_NAME = "com.mff.constants.Resources";
	private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess (Object pPipelineParams, PipelineResult pPipelineResults) 
		throws Exception {
		vlogDebug("Entering ProcUpdateInventory - runProcess");
	
		Map lParams 		  = (Map) pPipelineParams;
		Order lOrder 	 	  = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		OrderImpl  lExtnOrder = (OrderImpl) lOrder;
		List<ItemAllocation> lItemAllocations = (List<ItemAllocation>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS);
		
		if(((MFFOrderImpl)lOrder).isBopisOrder()){
		  // this is been done as inventory is already updated when order moved to OMS
		  vlogInfo("Skipping Inventory Update for Bopis Order id = " + lExtnOrder.getId());
		  return SUCCESS;
		}
		
		vlogInfo("Starting Allocation Filter Chain - Order id = " + lExtnOrder.getId());
		
		// We update inventory for the allocated items
		try
		{
		  updateInventory (lOrder, lItemAllocations);
		}
		catch(CommerceException ce)
		{
		  // In case there is an inventory error we stop the pipeline chain
		  pPipelineResults.addError(INVENTORY_ERROR_KEY, ResourceUtils.getMsgResource(INVENTORY_ERROR_KEY, MY_RESOURCE_NAME, sResourceBundle));
		  vlogDebug("Exiting ProcUpdateInventory and stop chain execution with errors - runProcess");
		  return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
		}
		vlogDebug("Exiting ProcUpdateInventory - runProcess");
		return SUCCESS;
	}
	
	/**
	 * Update the inventory for the allocations.
	 * 
	 * @param pOrder				ATG Order
	 * @param pItemAllocations		Item Allocations
	 * @throws CommerceException
	 */
	protected void updateInventory (Order pOrder, List<ItemAllocation> pItemAllocations) 
		throws CommerceException { 
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		for (ItemAllocation lItemAllocation : pItemAllocations) {
			String lCommerceItemId 	= lItemAllocation.getCommerceItemId();
			String lSkuId 			= lItemAllocation.getSkuId();
			String lStoreId 		= lItemAllocation.getFulfillmentStore();
			long lQuantity			= lItemAllocation.getQuantity();
			
			if (lItemAllocation.isGCFulfillment() || lItemAllocation.isForceAllocate() || lItemAllocation.isSplitItem() || lItemAllocation.isDropShipItem()) {
        vlogDebug ("Bypassing store inventory update for item - order: {0} item: {1} Store: {2}", lOrder.getId(), lCommerceItemId, lStoreId);
      }
      else{
        vlogDebug ("Updating store inventory for order: {0} item: {1} SkuId: {2} Store: {3}", lOrder.getId(), lCommerceItemId, lSkuId, lStoreId);
        getInventoryManager().incrementStoreAllocated(lSkuId, lStoreId, lQuantity);
      }
			/*if (lItemAllocation.isBackordered() || lItemAllocation.isGCFulfillment()) {
				vlogDebug ("Bypassing store inventory update for item - order: {0} item: {1} UPC: {2} Store: {3}", lOrder.getOrderNumber(), lCommerceItemId, lUpc, lStoreId);
			}
			else if (lItemAllocation.isStoreFulfillment()) {
				vlogDebug ("Updating store inventory for order: {0} item: {1} UPC: {2} Store: {3}", lOrder.getOrderNumber(), lCommerceItemId, lUpc, lStoreId);
				getInventoryManager().incrementStoreAllocated(lUpc, lStoreId, lQuantity);
			}
			else {
				vlogDebug ("Updating warehouse inventory for order: {0} item: {1} UPC: {2} Store: {3}", lOrder.getOrderNumber(), lCommerceItemId, lUpc, lStoreId);
				getInventoryManager().incrementW1PreAllocated(lUpc, lQuantity);
				getInventoryManager().incrementW1Allocated(lUpc, lQuantity);
			}*/
		}
	}
	
	private FFRepositoryInventoryManager mInventoryManager;
	public FFRepositoryInventoryManager getInventoryManager() {
		return mInventoryManager;
	}
	public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
		this.mInventoryManager = pInventoryManager;
	}
	
}