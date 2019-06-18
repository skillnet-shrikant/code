package oms.commerce.processor;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.states.OMSOrderStates;

public class ProcFraudInventoryCheck extends ApplicationLoggingImpl implements PipelineProcessor {

  private final int SUCCESS = 1;

    /**
     * Default Constructor 
     */
    public ProcFraudInventoryCheck() {
        super();
    }    

  public int[] getRetCodes() {
      int[] ret = {SUCCESS};
        return ret;
  }
  
  private FFRepositoryInventoryManager inventoryManager;

  private boolean inventoryEnabled = true;

  /**
   * Run the pipeline process 
   */
  @SuppressWarnings("rawtypes")
  public int runProcess(Object pParam, PipelineResult pResult) 
    throws Exception  {
    vlogDebug ("Begin ProcFraudInventoryCheck");
    
    if (!isInventoryEnabled()) {
      vlogWarning("ProcFraudInventoryCheck is not enabled so skipped processing");
    }
    HashMap map       = (HashMap) pParam;
    MFFOrderImpl lOMSOrder   = (MFFOrderImpl)map.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
    String newOrderState = (String)map.get(OMSOrderConstants.PIPELINE_NEW_ORDER_STATE);
    
    if (lOMSOrder == null)
        throw new InvalidParameterException("Ther OMS Order was not passed to ProcFraudInventoryCheck");
    
    vlogDebug("ProcFraudInventoryCheck : order {0} newOrderState {1}",lOMSOrder.getOrderNumber(),newOrderState);
    
    if(!StringUtils.isEmpty(newOrderState) && newOrderState.equalsIgnoreCase(OMSOrderStates.FRAUD_REJECT)){
 
       List ciList = lOMSOrder.getCommerceItems();
       ListIterator ciIterator = ciList.listIterator();
       while (ciIterator.hasNext()) {
        CommerceItem ci = (CommerceItem) ciIterator.next();
        String skuId = ci.getCatalogRefId();
        long qty = ci.getQuantity();
        try {
          getInventoryManager().increaseStockLevel(skuId, qty);
         }catch (InventoryException e) {
          vlogError(e,"ProcFraudInventoryCheck: Error updatig inventory for order {0} Sku {0}",lOMSOrder.getOrderNumber(),skuId);
        }
      }
    }
     
    vlogDebug ("End ProcFraudInventoryCheck");
    return SUCCESS;
  }
  
  public FFRepositoryInventoryManager getInventoryManager() {
    return inventoryManager;
  }

  public void setInventoryManager(FFRepositoryInventoryManager inventoryManager) {
    this.inventoryManager = inventoryManager;
  }

  /**
   * 
   * @return the inventoryEnabled flag
   */
  public boolean isInventoryEnabled() {
    return inventoryEnabled;
  }

  /**
   * Set the inventoryEnabled. When set to false this processor will suppress
   * the calls to the inventory manager and checks to make sure the inventory
   * is available.
   * 
   * @param inventoryEnabled the inventory flag.
   */
  public void setInventoryEnabled(boolean inventoryEnabled) {
    this.inventoryEnabled = inventoryEnabled;
  }

}
