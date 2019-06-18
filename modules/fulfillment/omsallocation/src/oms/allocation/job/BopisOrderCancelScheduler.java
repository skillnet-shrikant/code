/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;
import oms.allocation.item.AllocationConstants;
import oms.commerce.jobs.OMSSingletonScheduleTask;
import oms.commerce.order.OMSOrderManager;

/**
 * This is a scheduled process that will cancel bopis order
 * older than defined number of days.
 *
 * @author KnowledgePath Solutions Inc.
 * 
 */
public class BopisOrderCancelScheduler 
	extends OMSSingletonScheduleTask {
	

	@Override
	protected void performTask() {
		List<String> lOrderIds = null;
		
		// Exit If scheduler is not enabled
		if (!getSchedulerEnabled()) {
			vlogWarning("BopisOrderCancelScheduler is not enabled, skipping process");
			return;
		}

		// Find the orders to allocate
		try {
			lOrderIds = findOrdersToCancel();
		}
		catch (CommerceException ex) {
			vlogError ("Unable to get the list of orders to cancel ... process exiting");
			return;
		} 

		// Run the allocation pipeline for each order
		for (String lOrderId : lOrderIds) {
			runOrderCancellationPipeline (lOrderId);			
		}
	}	
	
	/**
	 * Find the orders that need to be cancelled by looking for orders 
	 * that have been readyForPickup for more than xx days.
	 * 
	 * @return					List of order Ids to be processed
	 * @throws CommerceException
	 */
	private List <String> findOrdersToCancel () 
		throws CommerceException { 
		Connection lConnection 		= null;		
		DataSource lDataSource 		= getDataSource();
		List <String> lResults 		= new Vector <String> ();
		ResultSet lResultSet 		= null;
		int lCount					= 0;
		
		// Replace interval in the SQL with value from properties file
		String lSql = getCanceQuerySql().replaceAll("SQL_INTERVAL", getInterval());
		vlogDebug ("SQL Statement for cancel bopis order is " + lSql);

		try {
			lConnection 			 		= lDataSource.getConnection();
			PreparedStatement lStatement 	= lConnection.prepareStatement(lSql);
			lStatement.setString(1, "SENT_TO_STORE");
			lStatement.setInt(2, 1);
			lResultSet 						= lStatement.executeQuery();
			while (lResultSet.next()) {
				lCount++;
				lResults.add (lResultSet.getString("order_id"));
			}
			vlogDebug ("Total Bopis orders to cancel {0} ",lCount);
		} catch (SQLException ex) {
			vlogError (ex, "Unable to get the orders for cancellation");
			throw new CommerceException ("Unable to get the orders for cancellation", ex);
		}
		finally {
			try {
				lConnection.close();
			} catch (SQLException ex) {
				vlogError (ex, "Unable to close the SQL connection");
				throw new CommerceException ("Unable to close the SQL connection", ex);
			}			
		}
		vlogDebug ("Found {0} orders to cancel", lResults.size());
		return lResults;
	}
	
	/**
	 * Run the order cancellation pipeline.
	 * 
	 * @param pOrderId		ATG Order id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void runOrderCancellationPipeline (String pOrderId) {
		
		List<String> lCancelCommerceItems 	= new ArrayList<String> ();
		
		// Load the order from the repository
		Order lOrder = null;
		try {
			lOrder = getOmsOrderManager().loadOrder(pOrderId);
		} catch (CommerceException e) {
			vlogError (e, "Unable to load order ID: {1}", pOrderId);
		}		
		String lOrderNumber = ((MFFOrderImpl)lOrder).getOrderNumber();
		
		// Get the list of items to cancel
		lCancelCommerceItems = getItemsToCancel (lOrder);

		Map lPipelineParams = new HashMap();
		lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder); 
		lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL, 	lCancelCommerceItems);
		lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC, 		getCancelDescription());
		try {
			vlogInfo ("Calling Order Cancellation pipeline: {0} for order number {1}", AllocationConstants.PIPELINE_BOPIS_CANCEL, lOrderNumber);
			getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_BOPIS_CANCEL, lPipelineParams);
			vlogInfo ("Successfully cancelled order number: {0}", lOrderNumber);
		}
		catch (RunProcessException ex)	{
			vlogError(ex, "Error cancelling order number {0}", lOrderNumber);
		}
	}

	/**
	 * Run the back order cancellation pipeline using the
	 * order ID property.
	 */
	public void runCancellationPipelineForOrderId () { 
		String lOrderId = getOrderId();
		if (lOrderId == null) {
			vlogDebug ("Order Id is null - process exiting");
		}
		runOrderCancellationPipeline (lOrderId);
	}
	
	/**
	 * Get the list of commerce items that need to be cancelled.
	 * 
	 * @param pOrder		ATG Order
	 * @return				List of commerce items to cancel
	 */
	@SuppressWarnings("unchecked")
	protected List<String> getItemsToCancel (Order pOrder) {
		List<String> lCancelCommerceItems = new ArrayList<String> ();
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		
		// Get the commerce items that are SENT_TO_STORE OR READY_FOR_PICKUP
		List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
		for (CommerceItem lCommerceItem : lCommerceItems) {
			MFFCommerceItemImpl lItem = (MFFCommerceItemImpl) lCommerceItem;
			if (lItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.SENT_TO_STORE) ||
			    lItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.READY_FOR_PICKUP)) {
				vlogDebug ("Add item to cancel: Order Number: {0} Item Id: {1}", 
							lOrder.getOrderNumber(),lItem.getId());
				lCancelCommerceItems.add(lItem.getId());
			}
		}
		vlogDebug ("Cancelling {0} items for order {1}", lCancelCommerceItems.size(), ((MFFOrderImpl) pOrder).getOrderNumber());
		return lCancelCommerceItems;
	}
	
	// ***************************************************************
	//    					Getter/Setter Methods 
	// ***************************************************************
	 /** SQL to retrieve the orders for cancellation */
  private String mCanceQuerySql;
	public String getCanceQuerySql() {
    return mCanceQuerySql;
  }
  public void setCanceQuerySql(String pCanceQuerySql) {
    this.mCanceQuerySql = pCanceQuerySql;
  }

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

	/** Cancel Description **/
	String mCancelDescription;
	public String getCancelDescription() {
		return mCancelDescription;
	}
	public void setCancelDescription(String pCancelDescription) {
		this.mCancelDescription = pCancelDescription;
	}
	
}