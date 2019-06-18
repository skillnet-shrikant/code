/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.List;
import java.util.Map;

import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;
import oms.commerce.order.MFFOMSOrderManager;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.commerce.states.MFFOrderStates;
import com.mff.commerce.states.MFFShippingGroupStates;
import com.reports.dashboard.OrderDashboardRepoHelper;

/**
 * This pipeline process will update the states on the shipping group and order.
 * 
 * @author DMI
 * 
 */
public class ProcUpdateOrderStates extends GenericService implements PipelineProcessor {

	private final static int	SUCCESS	= 1;

	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
		vlogDebug("Entering ProcUpdateOrderStates - runProcess");

		Map lParams = (Map) pPipelineParams;
		Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		List<ItemAllocation> lItemAllocations = (List<ItemAllocation>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS);

		// Update order states
		updateOrderStates(lOrder, lItemAllocations);

		// Update order
		getOmsOrderManager().updateOrder(lOrder);

		vlogDebug("Exiting ProcUpdateOrderStates - runProcess");
		return SUCCESS;
	}

	/**
	 * Update the order states in the shipping group and order.
	 * 
	 * @param pOrder
	 *            ATG Order
	 * @param pItemAllocations
	 *            Item Allocations
	 * @throws CommerceException
	 */
	@SuppressWarnings("unchecked")
	protected void updateOrderStates(Order pOrder, List<ItemAllocation> pItemAllocations) throws CommerceException {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;

		boolean lStorefulfillment 		= false;
		boolean lShipped 				= false;
		boolean lCancelled 				= false;
		boolean lPendingAllocation 		= false;
		boolean lForcedAllocation 		= false;
		boolean lGCfulfillment      = false;
		
		// Loop through the commerce items and see if any are pending allocation
		List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
		for (CommerceItem lCommerceItem : lCommerceItems) {
			MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl) lCommerceItem;
			if (lEXTNCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.PENDING_ALLOCATION))
				lPendingAllocation = true;
			if (lEXTNCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.FORCED_ALLOCATION))
				lForcedAllocation = true;
		}

		// Loop through all shipping groups and get states
		List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
		for (ShippingGroup lShippingGroup : lShippingGroups) {
				MFFHardgoodShippingGroup lSGShippingGroup = (MFFHardgoodShippingGroup) lShippingGroup;
				if (lSGShippingGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.SENT_TO_STORE) ||
				    lSGShippingGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.PENDING_DROP_SHIP_FULFILLMENT))
					lStorefulfillment = true;
				else if (lSGShippingGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.PENDING_GC_FULFILLMENT))
          lGCfulfillment = true;
				else if (lSGShippingGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.SHIPPED))
					lShipped = true;
				else if (lSGShippingGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.CANCELLED))
					lCancelled = true;
				else
					vlogWarning("In determinate state for shipping group id = {0} and state = {1}", lSGShippingGroup.getId(), lSGShippingGroup.getStateAsString());
		}
		
		//The following determines the order state based on the flags set above
		if (lPendingAllocation&&lForcedAllocation)
			lOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.PENDING_ALLOCATION));
		else if (lPendingAllocation)
			lOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.PENDING_ALLOCATION));
		else if (lShipped && !(lStorefulfillment || lGCfulfillment || lForcedAllocation))
			lOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.SHIPPED));
		else if (lShipped)
			lOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.PARTIALLY_SHIPPED));
		else if (lStorefulfillment || lGCfulfillment)
			lOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.SENT_TO_STORE));
	  else if (lForcedAllocation)
      lOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.FORCED_ALLOCATION));
		else if (lCancelled)
			lOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.CANCELLED));
	}

	protected ItemAllocation findItemAllocation(String pCommerceItemId, List<ItemAllocation> pItemAllocations) {
		long lCtr = pItemAllocations.size();
		for (int i = 0; i < lCtr; i++) {
			if (pItemAllocations.get(i).getCommerceItemId().equalsIgnoreCase(pCommerceItemId))
				return pItemAllocations.get(i);
		}
		return null;
	}

	MFFOMSOrderManager	mOmsOrderManager;
	
	private OrderDashboardRepoHelper dashboardHelper;

	public MFFOMSOrderManager getOmsOrderManager() {
		return mOmsOrderManager;
	}

	public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
		this.mOmsOrderManager = pOmsOrderManager;
	}

  public OrderDashboardRepoHelper getDashboardHelper() {
    return dashboardHelper;
  }

  public void setDashboardHelper(OrderDashboardRepoHelper pDashboardHelper) {
    dashboardHelper = pDashboardHelper;
  }

}