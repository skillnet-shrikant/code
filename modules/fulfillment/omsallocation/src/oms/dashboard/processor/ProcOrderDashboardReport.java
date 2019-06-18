/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.dashboard.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.reports.dashboard.OrderDashboardRepoHelper;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;
import oms.dashboard.AsychronousOrderDashboard;

/**
 * The Class ProcOrderDashboardReport.
 *
 * @author Manoj
 */
public class ProcOrderDashboardReport extends GenericService implements PipelineProcessor {

	/** The Constant SUCCESS. */
	private final static int SUCCESS = 1;
	private AsychronousOrderDashboard dashboard;

	/**
	 * Gets the ret codes.
	 *
	 * @return the ret codes
	 */
	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	/**
	 * Run process.
	 *
	 * @param pPipelineParams
	 *            the pipeline params
	 * @param pPipelineResults
	 *            the pipeline results
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
		vlogDebug("Entering ProcOrderDashboardReport - runProcess");

		if (!isEnabled()) {
			return SUCCESS;
		}

		Map lParams = (Map) pPipelineParams;
		Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		
		if(isLoggingDebug()){
			logDebug(" Calling Dashboard Async.");
		}
		getDashboard().processDashboardRequest(lParams, lOrder, this.getProcessName());
		
		if(isLoggingDebug()){
			logDebug(" Request submitted for Dashboard Async.");
		}

		vlogDebug("Exiting ProcOrderDashboardReport - runProcess");
		return SUCCESS;
	}

	/** The m process name. */
	private String mProcessName;

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

	/**
	 * Gets the process name.
	 *
	 * @return the process name
	 */
	public String getProcessName() {
		return mProcessName;
	}

	/**
	 * Sets the process name.
	 *
	 * @param mProcessName
	 *            the new process name
	 */
	public void setProcessName(String mProcessName) {
		this.mProcessName = mProcessName;
	}

	public AsychronousOrderDashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(AsychronousOrderDashboard pDashboard) {
		dashboard = pDashboard;
	}
}
