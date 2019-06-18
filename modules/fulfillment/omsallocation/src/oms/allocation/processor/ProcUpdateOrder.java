/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;


import java.util.Map;

import oms.allocation.item.AllocationConstants;
import oms.commerce.order.MFFOMSOrderManager;
import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.reports.dashboard.OrderDashboardRepoHelper;

/**
 * This pipeline process will update the states on the shipping group and order.
 * 
 * @author DMI
 * 
 */
public class ProcUpdateOrder extends GenericService implements PipelineProcessor {

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
		// Update order
		getOmsOrderManager().updateOrder(lOrder);

		vlogDebug("Exiting ProcUpdateOrderStates - runProcess");
		return SUCCESS;
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