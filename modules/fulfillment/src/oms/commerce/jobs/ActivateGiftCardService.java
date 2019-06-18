package oms.commerce.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.states.StateDefinitions;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;

import com.firstdata.payment.MFFGiftCardInfo;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.payment.MFFGiftCardManager;
import com.mff.commerce.pricing.MFFItemPriceInfo;
import com.mff.commerce.states.MFFCommerceItemStates;

public class ActivateGiftCardService extends OMSSingletonScheduleTask {

  private MFFGiftCardManager giftCardManager;
  private PipelineManager mFulfillmentPipelineManager;
  private DataSource dataSource; // SQL Data source for native query execution
  private String querySQL;
  private int maxActivationAttempts=2;
  
  /* (non-Javadoc)
   * @see oms.commerce.jobs.OMSSingletonScheduleTask#performTask()
   */
  @Override
  protected void performTask() {
    List<String> lOrderIds = null;

    try {
      // 1. Find the orders w/ GC pending activation
      lOrderIds = findOrdersGCPendingActivation();
    } catch (CommerceException ex) {
      vlogError("Unable to get the list of orders with gc items in pending activation");
      return;
    }
    
    for (String lOrderId : lOrderIds) 
    {
      Order lOrder = null;
      try {
        lOrder = getOmsOrderManager().loadOrder(lOrderId);
        vlogDebug ("Order with gc in pending activation {0}", lOrderId);
        synchronized (lOrder) {
          // 2. Get the commerce items pending activation
          List<MFFCommerceItemImpl> cisPendingActivation = findCIsPendingActivation(lOrder);
          List<String> orderItemsToShip = new ArrayList<String> ();
          boolean updateOrder=false;

          // 3. Activate GCs in order
          for (MFFCommerceItemImpl giftCardItem : cisPendingActivation)
          {
            vlogDebug ("Processing gift card item id {0}", giftCardItem.getId());
            updateOrder=true;
            MFFGiftCardInfo giftCardInfo = activateGiftCard(giftCardItem);
            if(null==giftCardItem.getActivationAttempts())
            {
              giftCardItem.setActivationAttempts(Integer.valueOf(1));
            }
            else
            {
              giftCardItem.setActivationAttempts(Integer.valueOf(giftCardItem.getActivationAttempts().intValue() + 1));
            }
            
            // 4. Validate giftCardInfo and add item to list of items activated
            if(null==giftCardInfo || !giftCardInfo.isTransactionSuccess())
            {
              vlogDebug("GC NOT activated - Order Number {0}, GC Number {1}", lOrderId, giftCardItem.getGiftCardNumber());
              if(giftCardItem.getActivationAttempts().intValue()>=getMaxActivationAttempts())
              {
                // Set Commerce item state to ERROR_GC_ACTIVATION
                giftCardItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.ERROR_GC_ACTIVATION));
              }
            }
            else
            {
              // isTransactionSuccess true
              orderItemsToShip.add(giftCardItem.getId());
              vlogDebug("GC activated - Order Number {0}, GC Number {1}", lOrderId, giftCardItem.getGiftCardNumber());
            }
          }
          
          // update from activationAttempts or set ci state in case of error
          if(updateOrder)
          {
            getOmsOrderManager().updateOrder(lOrder);
          }

          // 5. Send validated GCs in order to pipeline to update state to shipped
          if(null!=orderItemsToShip && orderItemsToShip.size()>0)
          {
            Map pipelineParams = new HashMap();
            pipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, lOrder);
            pipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, orderItemsToShip);
            getFulfillmentPipelineManager().runProcess("handleStoreShipment", pipelineParams);
          }
        }
      } catch (CommerceException e) {
        vlogError (e, "Unable to load order ID: {0}", lOrderId);
      } catch (RunProcessException e) {
        vlogError (e, "Error sending GC items to pipeline for order {0}", lOrderId);
      }
    }
  }

  /**
   * This method finds the orders with GCs that need to be activated.
   * The commerce items we look for are in PENDING_GC_ACTIVATION state.
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
    vlogDebug("SQL Statement to get GCs to activate is " + lSql);

    try {
      lConnection = lDataSource.getConnection();
      PreparedStatement lStatement = lConnection.prepareStatement(lSql);
      lStatement.setString(1, "PENDING_GC_ACTIVATION");
      lResultSet = lStatement.executeQuery();
      logDebug("lStatement: " + lStatement.toString());
      while (lResultSet.next()) {
        lResults.add(lResultSet.getString("order_ref"));
      }
    } catch (SQLException ex) {
      vlogError(ex, "Unable to get the orders with GCs for activation");
      throw new CommerceException("Unable to get the orders with GCs for activation", ex);
    } finally {
      try {
        lConnection.close();
      } catch (SQLException ex) {
        vlogError(ex, "Unable to close the SQL connection");
        throw new CommerceException("Unable to close the SQL connection", ex);
      }
    }
    vlogDebug("Found {0} orders with gc in pending activation state", lResults.size());
    return lResults;
  }

  private List<MFFCommerceItemImpl> findCIsPendingActivation(Order pOrder)
  {
    List<MFFCommerceItemImpl> cisPendingActivation = new ArrayList<MFFCommerceItemImpl>();
    for (MFFCommerceItemImpl commerceItem : (List<MFFCommerceItemImpl>)pOrder.getCommerceItems()) {
      if (null != commerceItem && commerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_GC_ACTIVATION)) {
        // Add item in pending activation state
        cisPendingActivation.add(commerceItem);
      }
    }
    return cisPendingActivation;
  }
  
  private MFFGiftCardInfo activateGiftCard(MFFCommerceItemImpl pCommerceItem)
  {
    MFFGiftCardInfo giftCardInfo = null;
    if(null!=pCommerceItem.getGiftCardNumber())
    {
    	double gcActivationAmount = 0.0;
      // Gift Card Activation
    	if(pCommerceItem.getPriceInfo().getAmount() > 0 ) {
    		gcActivationAmount = pCommerceItem.getPriceInfo().getAmount();
    	} else {
    		//gcActivationAmount = pCommerceItem.getGwpGiftCardValue();
    		gcActivationAmount = ((MFFItemPriceInfo)pCommerceItem.getPriceInfo()).getEffectivePrice();
/*			Iterator adjIter = pCommerceItem.getPriceInfo().getAdjustments().iterator();
			while(adjIter.hasNext()) {
				PricingAdjustment adj = (PricingAdjustment) adjIter.next();
				if(adj.getPricingModel()!= null) {
					//return item;
					gcActivationAmount += -1*adj.getTotalAdjustment();
				}
			}	*/	
    	}
    		
    	giftCardInfo = getGiftCardManager().giftCardActivation(pCommerceItem.getGiftCardNumber(), gcActivationAmount);
      vlogDebug("Activation Result - GC Number {0}, TransactionSuccess {1}", pCommerceItem.getGiftCardNumber(), giftCardInfo.isTransactionSuccess());
      vlogDebug("Activation Result - ResponseCode {0}, ResponseCodeMsg {1}, ErrorMessage {2}", giftCardInfo.getResponseCode(), giftCardInfo.getResponseCodeMsg(), giftCardInfo.getErrorMessage());
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

}
