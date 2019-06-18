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

import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;
import oms.allocation.item.AllocationConstants;
import oms.commerce.jobs.OMSSingletonScheduleTask;
import oms.commerce.order.OMSOrderManager;

/**
 * This is a scheduled process that will send reminder emails
 * for bopis orders older than defined number of days.
 *
 * @author KnowledgePath Solutions Inc.
 * 
 */
public class BopisOrderReminderScheduler 
	extends OMSSingletonScheduleTask {
	

	@Override
	protected void performTask() {
		processFirstReminderEmails();
		processSecondReminderEmails();
	}

  private void processFirstReminderEmails() {
    vlogDebug ("processFirstReminderEmails -- start ");
    List<String> lOrderIds = null;
		
    boolean isFirstReminder = true;
		// Exit If scheduler is not enabled
		if (!getSchedulerEnabled()) {
			vlogWarning("BopisOrderReminderScheduler is not enabled, skipping process");
			return;
		}

		// Find the orders to allocate
		try {
			lOrderIds = findOrdersToProcess(isFirstReminder);
		}
		catch (CommerceException ex) {
			vlogError ("Unable to get the list of orders to sending emails ... process exiting");
			return;
		} 

		// Run the allocation pipeline for each order
		for (String lOrderId : lOrderIds) {
		  runBopisReminderPipeline (lOrderId,isFirstReminder);			
		}
		vlogDebug ("processFirstReminderEmails -- end ");
  }	
  
  private void processSecondReminderEmails() {
    vlogDebug ("processSecondReminderEmails -- start ");
    List<String> lOrderIds = null;
    
    boolean isFirstReminder = false;
    // Exit If scheduler is not enabled
    if (!getSchedulerEnabled()) {
      vlogWarning("BopisOrderReminderScheduler is not enabled, skipping process");
      return;
    }

    // Find the orders to allocate
    try {
      lOrderIds = findOrdersToProcess(isFirstReminder);
    }
    catch (CommerceException ex) {
      vlogError ("Unable to get the list of orders to sending emails ... process exiting");
      return;
    } 

    // Run the allocation pipeline for each order
    for (String lOrderId : lOrderIds) {
      runBopisReminderPipeline (lOrderId,isFirstReminder);      
    }
    vlogDebug ("processSecondReminderEmails -- end ");
  } 
	
	/**
	 * Find the orders that need to be sent reminder email by 
	 * looking for orders that have been readyForPickup for more than xx days.
	 * 
	 * @return					List of order Ids to be processed
	 * @throws CommerceException
	 */
	private List <String> findOrdersToProcess (boolean isFirstReminder) 
		throws CommerceException { 
		Connection lConnection 		= null;		
		DataSource lDataSource 		= getDataSource();
		List <String> lResults 		= new Vector <String> ();
		ResultSet lResultSet 		= null;
		int lCount					= 0;
		String lSql;
		if(isFirstReminder) {
  		// Replace interval in the SQL with value from properties file
  		lSql = getQuerySql().replaceAll("SQL_INTERVAL", getInterval());
  		vlogDebug ("SQL Statement for sending readyToPickUp reminder emails for bopis order is " + lSql);
		} else {
		  // Replace interval in the SQL with value from properties file
	    lSql = getQuerySecondReminderSql().replaceAll("SQL_INTERVAL", getSecondInterval());
	    vlogDebug ("SQL Statement for sending readyToPickUp reminder emails for bopis order is " + lSql);
		}
		try {
			lConnection 			 		= lDataSource.getConnection();
			PreparedStatement lStatement 	= lConnection.prepareStatement(lSql);
			lStatement.setString(1, "READY_FOR_PICKUP");
			lStatement.setInt(2, 1);
			lResultSet 						= lStatement.executeQuery();
			while (lResultSet.next()) {
				lCount++;
				lResults.add (lResultSet.getString("order_ref"));
			}
			vlogDebug ("Total Bopis orders for sending readyToPickUp reminder emails {0} ",lCount);
		} catch (SQLException ex) {
			vlogError (ex, "Unable to get the orders for readyToPickUp");
			throw new CommerceException ("Unable to get the orders for readyToPickUp", ex);
		}
		finally {
			try {
				lConnection.close();
			} catch (SQLException ex) {
				vlogError (ex, "Unable to close the SQL connection");
				throw new CommerceException ("Unable to close the SQL connection", ex);
			}			
		}
		vlogDebug ("Found {0} orders to process for bopis readyToPickUp reminder emails", lResults.size());
		return lResults;
	}
	
	/**
   * Run the bopisReminder pipeline.
   * 
   * @param pOrderId    ATG Order id
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void runBopisReminderPipeline (String pOrderId,boolean isFirstReminder) {
    
    // Load the order from the repository
    Order lOrder = null;
    try {
      lOrder = getOmsOrderManager().loadOrder(pOrderId);
    } catch (CommerceException e) {
      vlogError (e, "Unable to load order ID: {0}", pOrderId);
    }   
    if(lOrder != null){
      String lOrderNumber = ((MFFOrderImpl)lOrder).getOrderNumber();
      
      Map lPipelineParams = new HashMap();
      lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder);
      lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_IS_FIRST_REMINDER, isFirstReminder); 
      
      try {
        vlogInfo ("Calling handleBopisReminderEmail pipeline: {0} for order number {1}", AllocationConstants.PIPELINE_BOPIS_REMINDER_EMAIL, lOrderNumber);
        getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_BOPIS_REMINDER_EMAIL, lPipelineParams);
        vlogInfo ("Successfully executed handleBopisReminderEmail order number: {0}", lOrderNumber);
      }
      catch (RunProcessException ex)  {
        vlogError(ex, "Error sending bopis reminder email for order number {0}", lOrderNumber);
      }
    }
  }
	
	// ***************************************************************
	//    					Getter/Setter Methods 
	// ***************************************************************
	 /** SQL to retrieve the orders for cancellation */
  private String mQuerySql;
  public String getQuerySql() {
    return mQuerySql;
  }
  public void setQuerySql(String pQuerySql) {
    mQuerySql = pQuerySql;
  }
  
  private String mQuerySecondReminderSql;
  public String getQuerySecondReminderSql() {
    return mQuerySecondReminderSql;
  }
  public void setQuerySecondReminderSql(String pQuerySecondReminderSql) {
    mQuerySecondReminderSql = pQuerySecondReminderSql;
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
	
	String mSecondInterval;
  public String getSecondInterval() {
    return mSecondInterval;
  }
  public void setSecondInterval(String pSecondInterval) {
    this.mSecondInterval = pSecondInterval;
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