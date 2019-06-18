package oms.dropship.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;
import mff.task.Task;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.OMSOrderManager;

public class DropShipSalesFileGeneratorTask extends Task {
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doTask() {
    
    List<String> lOrderIds = null;

    // Find the drop ship orders to process
    try {
      lOrderIds = findOrdersToProcess();
    } catch (CommerceException ex) {
      vlogError("Unable to get the list of orders to cancel ... process exiting");
      return;
    }
    
    for (String lOrderId : lOrderIds) {
     
      Order lOrder = null;
      try {
        lOrder = getOmsOrderManager().loadOrder(lOrderId);
        // run pipeline process
        synchronized (lOrder) {
          vlogDebug("Processing orderId {0} and invoking pipeline {1}",lOrderId,getPipelineName());
          Map lPipelineParams = new HashMap();
          lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder);
          getFulfillmentPipelineManager().runProcess(getPipelineName(), lPipelineParams);
          vlogInfo ("Successfully generated salesorder file for orderId : {0}", lOrder.getId());
        }
      } catch (CommerceException e) {
        vlogError (e, "Unable to load order ID: {0}", lOrderId);
      } catch (RunProcessException ex) {
        vlogError(ex, "Error running pipeline {0} for order number {1}",getPipelineName(),lOrder.getId());
      }
      
    }
    
  }

  /**
   * Find the orders that need to be processed by looking for orders that have
   * items in PEDNING_DROP_SHIP_FULFILLEMT state.
   * 
   * @return List of order Ids to be processed
   * @throws CommerceException
   */
  private List<String> findOrdersToProcess() throws CommerceException {
    Connection lConnection = null;
    DataSource lDataSource = getDataSource();
    List<String> lResults = new Vector<String>();
    ResultSet lResultSet = null;
    
    //select distinct order_ref from dcspp_item where state = 'PENDING_DROP_SHIP_FULFILLEMT';
    String lSql = getQuerySql();
    vlogDebug("SQL Statement to get dropship orders is " + lSql);

    try {
      lConnection = lDataSource.getConnection();
      PreparedStatement lStatement = lConnection.prepareStatement(lSql);
      lStatement.setString(1, "PENDING_DROP_SHIP_FULFILLMENT");
      lResultSet = lStatement.executeQuery();
      while (lResultSet.next()) {
        lResults.add(lResultSet.getString("order_ref"));
      }
    } catch (SQLException ex) {
      vlogError(ex, "Unable to get the orders for dropShip file generation");
      throw new CommerceException("Unable to get the orders for dropShip file generation", ex);
    } finally {
      try {
        lConnection.close();
      } catch (SQLException ex) {
        vlogError(ex, "Unable to close the SQL connection");
        throw new CommerceException("Unable to close the SQL connection", ex);
      }
    }
    vlogDebug("Found {0} orders for dropShip file generation", lResults.size());
    return lResults;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void generateFileForOrderId(){
    Order lOrder = null;
    try {
      lOrder = getOmsOrderManager().loadOrder(getOrderId());
      synchronized (lOrder) {
        Map lPipelineParams = new HashMap();
        lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder);
        getFulfillmentPipelineManager().runProcess(getPipelineName(), lPipelineParams);
      }
    } catch (CommerceException e) {
      vlogError (e, "Unable to load order ID: {0}", getOrderId());
    } catch (RunProcessException ex) {
      vlogError(ex, "Error running pipeline {0} for order number {1}",getPipelineName(),lOrder.getId());
    }
  }
  
 
  

  // ***************************************************************
  // Getter/Setter Methods
  // ***************************************************************
  
  private String pipelineName;
  
  public String getPipelineName() {
    return pipelineName;
  }

  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
  }




  private String mQuerySql;
  
  public String getQuerySql() {
    return mQuerySql;
  }

  public void setQuerySql(String pQuerySql) {
    this.mQuerySql = pQuerySql;
  }

  String mOrderId;

  public String getOrderId() {
    return mOrderId;
  }

  public void setOrderId(String pOrderId) {
    this.mOrderId = pOrderId;
  }

  /** SQL Data source for native query execution */
  DataSource mDataSource;

  public DataSource getDataSource() {
    return mDataSource;
  }

  public void setDataSource(DataSource pDataSource) {
    this.mDataSource = pDataSource;
  }

  OMSOrderManager mOrderManager;

  public void setOmsOrderManager(OMSOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public OMSOrderManager getOmsOrderManager() {
    return mOrderManager;
  }

  /** Fulfillment pipeline Manager **/
  private PipelineManager mFulfillmentPipelineManager;

  public PipelineManager getFulfillmentPipelineManager() {
    return mFulfillmentPipelineManager;
  }

  public void setFulfillmentPipelineManager(PipelineManager pFulfillmentPipelineManager) {
    mFulfillmentPipelineManager = pFulfillmentPipelineManager;
  }

}
