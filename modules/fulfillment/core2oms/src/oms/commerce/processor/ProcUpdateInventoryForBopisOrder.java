package oms.commerce.processor;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.locator.StoreLocatorTools;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.states.OMSOrderStates;

public class ProcUpdateInventoryForBopisOrder extends ApplicationLoggingImpl implements PipelineProcessor {

  private final int SUCCESS = 1;
  private StoreLocatorTools storeLocatorTools;
  
  public StoreLocatorTools getStoreLocatorTools() {
	return storeLocatorTools;
}

public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
	storeLocatorTools = pStoreLocatorTools;
}

/**
   * Default Constructor 
   */
  public ProcUpdateInventoryForBopisOrder() {
      super();
  }    

  public int[] getRetCodes() {
      int[] ret = {SUCCESS};
        return ret;
  }
  
  private FFRepositoryInventoryManager inventoryManager;

  private boolean enabled = true;

  /**
   * Run the pipeline process 
   */
  @SuppressWarnings("rawtypes")
  public int runProcess(Object pParam, PipelineResult pResult) 
    throws Exception  {
    vlogDebug ("Begin ProcUpdateInventoryForBopisOrder");
    
    if (!isEnabled()) {
      vlogWarning("ProcUpdateInventoryForBopisOrder is not enabled so skipped processing");
    }
    
    HashMap map       = (HashMap) pParam;
    MFFOrderImpl lOMSOrder   = (MFFOrderImpl)map.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
    String newOrderState = (String)map.get(OMSOrderConstants.PIPELINE_NEW_ORDER_STATE);
    boolean fraudOrder = newOrderState.equalsIgnoreCase(OMSOrderStates.FRAUD_REJECT) ? true : false;
    
    if (lOMSOrder == null)
        throw new InvalidParameterException("Ther OMS Order was not passed to ProcFraudInventoryCheck");
    
    vlogDebug("ProcUpdateInventoryForBopisOrder : order {0} newOrderState {1}",lOMSOrder.getOrderNumber(),newOrderState);
    
    if(lOMSOrder.isBopisOrder() && !fraudOrder){
 
       List ciList = lOMSOrder.getCommerceItems();
       ListIterator ciIterator = ciList.listIterator();
       while (ciIterator.hasNext()) {
        CommerceItem ci = (CommerceItem) ciIterator.next();
        String skuId = ci.getCatalogRefId();
        long qty = ci.getQuantity();
        try {
        	if(!getStoreLocatorTools().isBOPISOnlyStore(lOMSOrder.getBopisStore())) {
        		getInventoryManager().incrementStoreAllocated(skuId, lOMSOrder.getBopisStore(), qty);
        	} else {
        		getInventoryManager().incrementBopisOnlyStoreAllocated(skuId, lOMSOrder.getBopisStore(), qty, true);
        	}
          
          //
         }catch (InventoryException e) {
          vlogError(e,"ProcUpdateInventoryForBopisOrder: Error updatig inventory for order {0} Sku {0}",lOMSOrder.getOrderNumber(),skuId);
        }
      }
    }
     
    vlogDebug ("End ProcUpdateInventoryForBopisOrder");
    return SUCCESS;
  }
  
  public FFRepositoryInventoryManager getInventoryManager() {
    return inventoryManager;
  }

  public void setInventoryManager(FFRepositoryInventoryManager inventoryManager) {
    this.inventoryManager = inventoryManager;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean pEnabled) {
    enabled = pEnabled;
  }

}
