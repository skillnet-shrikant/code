/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.List;
import java.util.Map;

import oms.allocation.item.AllocationConstants;
import oms.allocation.store.StoreAllocationManager;
import oms.commerce.order.ItemAllocation;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.mff.commerce.order.MFFOrderImpl;

/**
 * This pipeline process will write the store allocations for 
 * the CSC.
 * 
 * @author DMI
 *
 */
public class ProcCreateStoreAllocations 
	extends GenericService  
	implements PipelineProcessor {

	private final static int SUCCESS 		= 1;
	
	public int[] getRetCodes() {
		int[] ret = {SUCCESS};
		return ret;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess (Object pPipelineParams, PipelineResult pPipelineResults) 
		throws Exception {
		vlogDebug("Entering ProcCreateStoreAllocations - runProcess");
	
		Map lParams 		  = (Map) pPipelineParams;
		Order lOrder 	 	  = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		//OrderImpl  lExtnOrder = (OrderImpl) lOrder;
		List<ItemAllocation> lItemAllocations = (List<ItemAllocation>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS);
		
		// Create Shipping groups for Allocations
		addStoreAllocations (lOrder, lItemAllocations);
		
		vlogDebug("Exiting ProcCreateStoreAllocations - runProcess");
		return SUCCESS;
	}
	
	/**
	 * Add the store allocations to the Store Allocation repository.
	 * 
	 * @param pOrder				ATG Order
	 * @param pItemAllocations		Item Allocations
	 * @throws CommerceException
	 */
	protected void addStoreAllocations (Order pOrder, List<ItemAllocation> pItemAllocations) 
		throws CommerceException { 
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		for (ItemAllocation lItemAllocation : pItemAllocations) {
		  
		  String lCommerceItemId = lItemAllocation.getCommerceItemId();
		  
	    if(lItemAllocation.isSplitItem() || lItemAllocation.isForceAllocate()){
	      logInfo("addStoreAllocations : Skipped Processing ItemAllocation as it's marked for split/forceAllocation  Order No:" + lOrder.getId() + " Item: " + lCommerceItemId);
        continue;
      }
			
			vlogDebug ("Add Store Allocation for Order: {0} item: {1}", lOrder.getId(), lCommerceItemId);
			getStoreAllocationManager().addLineItem (pOrder, lCommerceItemId);
		}
	}
	
	StoreAllocationManager mStoreAllocationManager;
	public StoreAllocationManager getStoreAllocationManager() {
		return mStoreAllocationManager;
	}
	public void setStoreAllocationManager(StoreAllocationManager pStoreAllocationManager) {
		this.mStoreAllocationManager = pStoreAllocationManager;
	}
}