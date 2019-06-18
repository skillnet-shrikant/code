package com.mff.commerce.order.processor;

import java.util.HashMap;

import javax.transaction.TransactionManager;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.order.MFFOrderManager.UpdateCartForInventoryResult;

import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.PipelineConstants;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This process Iterate through all the commerce items in the order and check
 * its inventory with the real time available inventory if the item is out of
 * inventory we want to get back to the cart and display the inventory message.
 * 
 */
public class ValidateInventoryForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {

  /** The Constant SUCCESS. */
  private static final int SUCCESS = 1;
  private final int getRetCode[] = { SUCCESS };
  private static final String PERFORM_MONITOR_NAME = "ValidateInventoryForCheckout";
  private MFFOrderManager mOrderManager;
  private TransactionManager mTransactionManager;

  /** Property : inventory manger. */
  private FFRepositoryInventoryManager mInventoryManager;

  /**
   * This method is used for the purpose of to get ReturnCodes.
   * 
   * @return RetCode.
   */
  public int[] getRetCodes() {
    return getRetCode;
  }

  /**
   * This method is used for the purpose of validating realtime Inventory.
   * 
   * @param pParam
   *          the Object
   * @param pResult
   *          the response
   * @return SUCCESS, if successful.
   * @throws Exception
   *           the Exception.
   * 
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws InvalidParameterException {
    String perfMonFunction = "runProcess";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfMonFunction);
    boolean exception = false;

    TransactionDemarcation td = new TransactionDemarcation();
    boolean rollback = true;

    try {
      @SuppressWarnings("rawtypes")
      HashMap map = (HashMap) pParam;
      MFFOrderImpl order = (MFFOrderImpl) map.get(PipelineConstants.ORDER);
      if (order == null) {
        throw new InvalidParameterException("Invalid null pipeline parameter: " + PipelineConstants.ORDER);
      }
      vlogInfo("Action - ValidateInventoryForCheckout:runProcess for order - {0}", order.getId());

      // set transaction required here
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Check for inventory and sku availability
      UpdateCartForInventoryResult result = getOrderManager().updateCartForInventory(order);
      if (result != null) {
        if (result.getMessageMap() != null) {
          pResult.addError("inventoryError", result.getMessageMap());
          rollback=false;
          return STOP_CHAIN_EXECUTION_AND_COMMIT;
        }
      }
      rollback = false;
    } catch (TransactionDemarcationException e) {
      vlogError(e, "Exception block in ValidateInventoryForCheckout");
    } catch (RepositoryException e) {
      vlogError(e, "Exception block in ValidateInventoryForCheckout");
    } catch (CommerceException e) {
      vlogError(e, "Exception block in ValidateInventoryForCheckout");
    } finally {
      if (!exception) PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfMonFunction);
      try {
        td.end(rollback);
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }

    return SUCCESS;
  }

  /**
   * @return the inventoryManager
   */
  public FFRepositoryInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager
   *          the inventoryManager to set
   */
  public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  public MFFOrderManager getOrderManager() {
    return mOrderManager;
  }

  public void setOrderManager(MFFOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }
}
