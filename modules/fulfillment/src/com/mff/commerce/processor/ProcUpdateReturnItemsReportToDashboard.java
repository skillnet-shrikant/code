package com.mff.commerce.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mff.returns.MFFReturnItem;
import com.reports.dashboard.OrderDashboardRepoHelper;

import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.order.CommerceItem;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * The Class ProcUpdateReturnItemsReportToDashboard.
 *
 * @author mmane
 */
public class ProcUpdateReturnItemsReportToDashboard extends GenericService implements PipelineProcessor {

	/** The Constant SUCCESS. */
	private final static int SUCCESS = 1;

	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	@SuppressWarnings({ "rawtypes" })
	public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {

		vlogDebug("Entering ProcUpdateReturnItemsReportToDashboard - runProcess");

		if(!isEnabled()){
			return SUCCESS;
		}
		Map lParams = (Map) pPipelineParams;

		ReturnRequest returnRequest = (ReturnRequest) lParams.get("ReturnRequest");
		
		try{
			getDashboardHelper().updateReturnedItemsToDashboard(returnRequest.getOrder(),
					getReturnedCommerceItems(returnRequest));
		} catch(Exception ex){
			vlogError("Some exception in updating the dashboard", ex);
			return SUCCESS;
		}
		
		return SUCCESS;
	}

	/**
	 * Gets the returned commerce items.
	 *
	 * @param pReturnRequest the return request
	 * @return the returned commerce items
	 */
	@SuppressWarnings("unchecked")
	protected List<CommerceItem> getReturnedCommerceItems(ReturnRequest pReturnRequest) {
		List<CommerceItem> lCommerceItems = new ArrayList<CommerceItem>();
		List<MFFReturnItem> returnItemList = (List<MFFReturnItem>) pReturnRequest.getReturnItemList();
		for (MFFReturnItem retItem : returnItemList) {
			lCommerceItems.add(retItem.getCommerceItem());
		}
		return lCommerceItems;
	}

	/** The m enabled. */
	boolean mEnabled;

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return mEnabled;
	}

	/**
	 * Sets the enabled.
	 *
	 * @param pEnabled
	 *            the new enabled
	 */
	public void setEnabled(boolean pEnabled) {
		mEnabled = pEnabled;
	}

	/** The dashboard helper. */
	private OrderDashboardRepoHelper dashboardHelper;

	/**
	 * Gets the dashboard helper.
	 *
	 * @return the dashboard helper
	 */
	public OrderDashboardRepoHelper getDashboardHelper() {
		return dashboardHelper;
	}

	/**
	 * Sets the dashboard helper.
	 *
	 * @param pDashboardHelper
	 *            the new dashboard helper
	 */
	public void setDashboardHelper(OrderDashboardRepoHelper pDashboardHelper) {
		dashboardHelper = pDashboardHelper;
	}

}
