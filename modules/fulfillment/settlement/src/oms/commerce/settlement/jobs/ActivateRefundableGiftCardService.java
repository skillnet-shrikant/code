package oms.commerce.settlement.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.sql.DataSource;

import oms.commerce.jobs.OMSSingletonScheduleTask;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.settlement.SettlementManager;
import oms.commerce.settlement.SettlementManagerImpl;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroupImpl;
import atg.commerce.states.StateDefinitions;
import atg.service.pipeline.PipelineManager;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardInfo;
import com.mff.commerce.payment.MFFGiftCardManager;
import com.mff.commerce.states.MFFPaymentGroupStates;

public class ActivateRefundableGiftCardService extends OMSSingletonScheduleTask {

  private MFFGiftCardManager giftCardManager;
  private PipelineManager mFulfillmentPipelineManager;
  private DataSource dataSource; // SQL Data source for native query execution
  private String querySQL;
  private int maxActivationAttempts=2;
  private SettlementManager settlementManager;
  
  /* (non-Javadoc)
   * @see oms.commerce.jobs.OMSSingletonScheduleTask#performTask()
   */
  @Override
  protected void performTask() {
    List<String> lOrderIds = null;

    try {
      // 1. Find the orders w/ refundable GC pending activation
      lOrderIds = findOrdersGCPendingActivation();
    } catch (CommerceException ex) {
      vlogError("Unable to get the list of orders with gc pay groups in pending activation");
      return;
    }
    
    for (String lOrderId : lOrderIds) 
    {
      Order lOrder = null;
      try {
        lOrder = getOmsOrderManager().loadOrder(lOrderId);
        vlogDebug ("Order with refundable gc in pending activation {0}", lOrderId);
        synchronized (lOrder) {
          // 2. Get the payment groups pending activation
          List<MFFGiftCardPaymentGroup> pgsPendingActivation = findPGsPendingActivation(lOrder);
          boolean updateOrder=false;

          // 3. Activate GCs in order
          for (MFFGiftCardPaymentGroup giftCardPayGroup : pgsPendingActivation)
          {
            vlogDebug ("Processing gift card pay group id {0}", giftCardPayGroup.getId());
            updateOrder=true;
            MFFGiftCardInfo giftCardInfo = activateGiftCard(giftCardPayGroup);
            if(null==giftCardPayGroup.getActivationAttempts())
            {
              giftCardPayGroup.setActivationAttempts(Integer.valueOf(1));
            }
            else
            {
              giftCardPayGroup.setActivationAttempts(Integer.valueOf(giftCardPayGroup.getActivationAttempts().intValue() + 1));
            }
            
            // 4. Validate giftCardInfo and add item to list of items activated
            if(null==giftCardInfo || !giftCardInfo.isTransactionSuccess())
            {
              vlogDebug("GC NOT activated - Order Number {0}, Paygroup GC Number {1}", lOrderId, giftCardPayGroup.getCardNumber());
              if(giftCardPayGroup.getActivationAttempts().intValue()>=getMaxActivationAttempts())
              {
                // Set Pay group to error state
                giftCardPayGroup.setState(StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.ERROR_GC_ACTIVATION));
              }
            }
            else
            {
              // isTransactionSuccess true
              updateOrder=true;
              // Sets the activated paygroup GC to settled
              giftCardPayGroup.setState(StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.SETTLED));
              vlogDebug("GC activated - Order Number {0}, GC Number {1}", lOrderId, giftCardPayGroup.getCardNumber());
            }
          }

          // update from activationAttempts or set ci state in case of error
          if(updateOrder)
          {
            getOmsOrderManager().updateOrder(lOrder);
          }

        }
      } catch (CommerceException e) {
        vlogError (e, "Unable to load order ID: {0}", lOrderId);
      }
    }
  }

  /**
   * This method finds the orders with Refundable GCs that need to be activated.
   * The payment group items we look for are in TO_BE_ACTIVATED state.
   * 
   * @return List of order Ids to be processed
   * @throws CommerceException
   */
  private List<String> findOrdersGCPendingActivation() throws CommerceException {
    Connection lConnection = null;
    DataSource lDataSource = getDataSource();
    List<String> lResults = new Vector<String>();
    ResultSet lResultSet = null;
    
    String lSql = getQuerySQL();
    vlogDebug("SQL Statement to get Refundable GCs to activate is " + lSql);

    try {
      lConnection = lDataSource.getConnection();
      PreparedStatement lStatement = lConnection.prepareStatement(lSql);
      lStatement.setString(1, "TO_BE_ACTIVATED");
      lResultSet = lStatement.executeQuery();
      logDebug("lStatement: " + lStatement.toString());
      while (lResultSet.next()) {
        lResults.add(lResultSet.getString("order_ref"));
      }
    } catch (SQLException ex) {
      vlogError(ex, "Unable to get the orders with refundable GCs for activation");
      throw new CommerceException("Unable to get the orders with refundable GCs for activation", ex);
    } finally {
      try {
        lConnection.close();
      } catch (SQLException ex) {
        vlogError(ex, "Unable to close the SQL connection");
        throw new CommerceException("Unable to close the SQL connection", ex);
      }
    }
    vlogDebug("Found {0} orders with refundable gc in pending activation state", lResults.size());
    return lResults;
  }

  private List<MFFGiftCardPaymentGroup> findPGsPendingActivation(Order pOrder)
  {
    List<MFFGiftCardPaymentGroup> pgsPendingActivation = new ArrayList<MFFGiftCardPaymentGroup>();
    for (PaymentGroupImpl payGroup : (List<PaymentGroupImpl>)pOrder.getPaymentGroups()) {
      if (null != payGroup && payGroup instanceof MFFGiftCardPaymentGroup && payGroup.getState() == StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.TO_BE_ACTIVATED)) {
        // Add item in pending activation state
        pgsPendingActivation.add((MFFGiftCardPaymentGroup)payGroup);
      }
    }
    return pgsPendingActivation;
  }
  
  private MFFGiftCardInfo activateGiftCard(MFFGiftCardPaymentGroup pPayGroup)
  {
    MFFGiftCardInfo giftCardInfo = null;
    if(null!=pPayGroup.getCardNumber())
    {
      try 
      {
        double refundAmount = ((SettlementManagerImpl)getSettlementManager()).getRefundAmount(pPayGroup);
        giftCardInfo = getGiftCardManager().giftCardActivation(pPayGroup.getCardNumber(), refundAmount);
        vlogDebug("Activation Result - GC Number {0}, TransactionSuccess {1}", giftCardInfo.getGiftCardNumber(), giftCardInfo.isTransactionSuccess());
        vlogDebug("Activation Result - ResponseCode {0}, ResponseCodeMsg {1}, ErrorMessage {2}", giftCardInfo.getResponseCode(), giftCardInfo.getResponseCodeMsg(), giftCardInfo.getErrorMessage());
      } catch (CommerceException e) {
        vlogError(e, "A commerce exception occurred while trying to activate refundable gift card");
      }
    }
    return giftCardInfo;
  }

  /**
   * @return the querySQL
   */
  public String getQuerySQL() {
    return querySQL;
  }

  /**
   * @param querySQL the querySQL to set
   */
  public void setQuerySQL(String querySQL) {
    this.querySQL = querySQL;
  }

  /**
   * @return the dataSource
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * @param dataSource the dataSource to set
   */
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  OMSOrderManager mOrderManager;

  public void setOmsOrderManager(OMSOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public OMSOrderManager getOmsOrderManager() {
    return mOrderManager;
  }

  public PipelineManager getFulfillmentPipelineManager() {
    return mFulfillmentPipelineManager;
  }

  public void setFulfillmentPipelineManager(PipelineManager pFulfillmentPipelineManager) {
    mFulfillmentPipelineManager = pFulfillmentPipelineManager;
  }

  /**
   * @return the giftCardManager
   */
  public MFFGiftCardManager getGiftCardManager() {
    return giftCardManager;
  }

  /**
   * @param giftCardManager the giftCardManager to set
   */
  public void setGiftCardManager(MFFGiftCardManager giftCardManager) {
    this.giftCardManager = giftCardManager;
  }

  /**
   * @return the maxActivationAttempts
   */
  public int getMaxActivationAttempts() {
    return maxActivationAttempts;
  }

  /**
   * @param maxActivationAttempts the maxActivationAttempts to set
   */
  public void setMaxActivationAttempts(int maxActivationAttempts) {
    this.maxActivationAttempts = maxActivationAttempts;
  }

  /**
   * @return the settlementManager
   */
  public SettlementManager getSettlementManager() {
    return settlementManager;
  }

  /**
   * @param pSettlementManager the settlementManager to set
   */
  public void setSettlementManager(SettlementManager pSettlementManager) {
    settlementManager = pSettlementManager;
  }

}
