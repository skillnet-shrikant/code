/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import oms.allocation.item.AllocationConstants;
import oms.commerce.jobs.OMSSingletonScheduleTask;
import oms.commerce.order.OMSOrderManager;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFOrderStates;

/**
 * This is a scheduled process that will create an order extract file of both warehouse and
 * store orders for KWI.  The generated file will be sent to KWI via FTP, after which the 
 * the extract status will be set on the commerce item level.
 *
 * @author KnowledgePath Solutions Inc.
 * 
 */
public class AllocationScheduler 
	extends OMSSingletonScheduleTask {

	/** SQL to retrieve the orders for fulfillment */
	String SQL_GET_ORDERS_FOR_ALLOCATION =
	"SELECT ORDER_ID																	" +
	"		FROM  DCSPP_ORDER															" +
	"		WHERE LAST_MODIFIED_DATE > CURRENT_DATE - INTERVAL 'SQL_INTERVAL' DAY AND	" + 
	"		      UPPER(STATE) = UPPER(?)												";	

	@Override
	public void performTask() {
		List<String> lOrderIds = null;
		
		// Exit If scheduler is not enabled
		if (!getSchedulerEnabled()) {
			vlogWarning("AllocationScheduler is not enabled, skipping process");
			return;
		}

		// Find the orders to allocate
		try {
			lOrderIds = findOrdersToAllocate();
		}
		catch (CommerceException ex) {
			vlogError ("Unable to get the list of orders to allocate ... process exiting");
			return;
		} 

		// Run the allocation pipeline for each order
		for (String lOrderId : lOrderIds) {
			runAllocationPipeline (lOrderId);			
		}
	}	
	
	/**
	 * Find the orders that need to be allocated by looking for orders 
	 * that have a status of pending allocation.
	 * 
	 * @return					List of order Ids to be processed
	 * @throws CommerceException
	 */
	private List <String> findOrdersToAllocate () 
		throws CommerceException { 
		Connection lConnection 		= null;		
		DataSource lDataSource 		= getDataSource();
		List <String> lResults 		= new Vector <String> ();
		ResultSet lResultSet 		= null;
		int lCount					= 0;
		
		// Replace interval in the SQL with value from properties file
		String lSql = SQL_GET_ORDERS_FOR_ALLOCATION.replaceAll("SQL_INTERVAL", getInterval());
		vlogDebug ("SQL Statement for allocation is " + lSql);

		try {
			lConnection 			 		= lDataSource.getConnection();
			PreparedStatement lStatement 	= lConnection.prepareStatement(lSql);
			lStatement.setString (1, MFFOrderStates.PENDING_ALLOCATION);
			lResultSet 						= lStatement.executeQuery();
			while (lResultSet.next()) {
				lCount++;
				lResults.add (lResultSet.getString("order_id"));
			}
		} catch (SQLException ex) {
			vlogError (ex, "Unable to get the orders for allocation");
			throw new CommerceException ("Unable to get the orders for allocation", ex);
		}
		finally {
			try {
				lConnection.close();
			} catch (SQLException ex) {
				vlogError (ex, "Unable to close the SQL connection");
				throw new CommerceException ("Unable to close the SQL connection", ex);
			}			
		}
		vlogDebug ("Found {0} orders for allocation", lResults.size());
		return lResults;
	}
	
	/**
	 * Run the allocation procedure for the order Id.
	 * 
	 * @param pOrderId		ATG Order id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void runAllocationPipeline (String pOrderId) { 
		
		Order lOrder = null;
		try {
			lOrder = getOmsOrderManager().loadOrder(pOrderId);
		} catch (CommerceException e) {
			vlogError (e, "Unable to load order ID: {1}", pOrderId);
		}
		
		String lOrderNumber = ((MFFOrderImpl)lOrder).getOrderNumber();

		Map lPipelineParams = new HashMap();
		lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder); 
		try {
			vlogInfo ("Calling allocation pipeline: {0} for order number {1}, orderId {2}", AllocationConstants.PIPELINE_ALLOCATE_ORDER, lOrderNumber,pOrderId);
      PipelineResult pResult = getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_ALLOCATE_ORDER, lPipelineParams);
      if (pResult.hasErrors())
      {
        vlogError("Errors found running allocation for order Id {0}", pOrderId);
        Object[] keys = pResult.getErrorKeys();
        for (int i = 0; i < keys.length; i++)
        {
          vlogError ("Unable to allocate order with error {0}", pResult.getError(keys[i]));
        }
      }
      else
      {
        vlogInfo ("Successfully allocated order number: {0} orderId : {1}", lOrderNumber,pOrderId);
      }
		}
		catch (RunProcessException ex)	{
			vlogError(ex, "Error allocating order number {0} orderId : {1}", lOrderNumber,pOrderId);
		}
	}

	public void runAllocationPipelineForOrderId () { 
		String lOrderId = getOrderId();
		if (lOrderId == null) {
			vlogDebug ("Order Id is null - process exiting");
		}
		runAllocationPipeline (lOrderId);
	}
	
	// ***************************************************************
	//    					Getter/Setter Methods 
	// ***************************************************************
	String mOrderId;
	public String getOrderId() {
		return mOrderId;
	}
	public void setOrderId(String pOrderId) {
		this.mOrderId = pOrderId;
	}

	/** SQL Data source for native query execution  */
	DataSource mDataSource;			
	public DataSource getDataSource() {
		return mDataSource;
	}
	public void setDataSource(DataSource pDataSource) {
		this.mDataSource = pDataSource;
	}		

	String mInterval;
	public String getInterval() {
		return mInterval;
	}
	public void setInterval(String pInterval) {
		this.mInterval = pInterval;
	}
	
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

}