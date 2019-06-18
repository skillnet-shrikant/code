/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.List;
import java.util.Map;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;
import atg.commerce.order.Order;
import atg.service.pipeline.PipelineResult;

public class ProcSetOrderStates 
	extends ProcUpdateOrderStates {

	private final static int	SUCCESS	= 1;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
		vlogDebug("Entering ProcSetOrderStates - runProcess");

		Map lParams = (Map) pPipelineParams;
		Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		List<ItemAllocation> lItemAllocations = (List<ItemAllocation>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS);

		// Update order states
		updateOrderStates(lOrder, lItemAllocations);

		vlogDebug("Exiting ProcSetOrderStates - runProcess");
		return SUCCESS;
	}
}
