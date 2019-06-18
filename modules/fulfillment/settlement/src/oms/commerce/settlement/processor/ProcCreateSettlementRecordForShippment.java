package oms.commerce.settlement.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.CommerceException;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.MFFOMSOrderManager;
import oms.commerce.processor.EXTNPipelineProcessor;
import oms.commerce.settlement.SettlementManager;

/**
 * The following processor is responsible for creating settlement records when a
 * shipment is processed. The pipeline requires the following parameters set in
 * the pipeline.
 * <ul>
 * <li>omsOrder - the order that is being processed
 * <li>itemsToShip - the list of commerce item ids that is being shipped
 * </ul>
 * 
 * <b> Assumptions: </b>
 * <ul>
 * <li>When a commerce item id is shipped all the quantities are shipped
 * </ul>
 * 
 * @author savula
 *
 */
public class ProcCreateSettlementRecordForShippment extends EXTNPipelineProcessor {

  private MFFOMSOrderManager orderManager;
  
  private SettlementManager settlementManager;
  
  

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    vlogDebug("Entering runProcess : pPipelineParams, pPipelineResults");

    Map lParams = (Map) pPipelineParams;
    MFFOrderImpl lOrder = (MFFOrderImpl) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    
//    HashMap<String, Double> settlementMap = new HashMap<>();
    
    double amountToSettle = 0.0d;

    List<String> lItemsToShip = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
    // List of items to ship is the list of commerce item ids

    if (lItemsToShip == null || lItemsToShip.size() < 1) {
      vlogDebug("ProcCreateProrateItem : No items to ship were found for orderId {0}", lOrder.getId());

    } else {

      Map<String, List<String>> lShippingGroupToItemsShippedMap = getOrderManager().getItemForShipGroups(lOrder, lItemsToShip);
      
      Map<String, Map<String, Double>> lShippingGroupToSettlementMap = new HashMap<String, Map<String, Double>>();
      
      //Loop through each of the shipping groups associated with the items
      for (String lShippingGroupId : lShippingGroupToItemsShippedMap.keySet()) {
        
        HashMap<String, Double> settlementMap = new HashMap<>();
        
        List<String> lItemsToShipForSG = lShippingGroupToItemsShippedMap.get(lShippingGroupId);
        
        for (String lItemToShip : lItemsToShipForSG) {
          amountToSettle = getOrderManager().computeAmountToSettle(lItemToShip, lOrder);
        	  try{
        	    getSettlementManager().createDebitSettlements(lOrder, amountToSettle, settlementMap);
        	  
        	  }catch(CommerceException e) {
        	    if(e.getMessage().equals(AllocationConstants.PIPELINE_PARAMETER_INSUFFICIENT_FUNDS_ERROR_MESSAGE_STR)) {
        	      pPipelineResults.addError(AllocationConstants.PIPELINE_PARAMETER_INSUFFICIENT_FUNDS, AllocationConstants.PIPELINE_PARAMETER_INSUFFICIENT_FUNDS_ERROR_MESSAGE_STR);
        	      throw new RunProcessException(AllocationConstants.PIPELINE_PARAMETER_INSUFFICIENT_FUNDS_ERROR_MESSAGE_STR);
        	    }else {
        	      throw e;
        	    }
        	  }
        }
        
        lShippingGroupToSettlementMap.put(lShippingGroupId, settlementMap);
        
      }
      lParams.put(AllocationConstants.PIPELINE_PARAMETER_SETTLEMENT_MAP, lShippingGroupToSettlementMap);
      
//      for (String lItemToShip : lItemsToShip) {
//        amountToSettle = getOrderManager().computeAmountToSettle(lItemToShip, lOrder);
//        if(amountToSettle > 0){
//          getSettlementManager().createDebitSettlements(lOrder, amountToSettle, settlementMap);
//          lParams.put(AllocationConstants.PIPELINE_PARAMETER_SETTLEMENT_MAP, settlementMap);
//        }
//      }
    }

    vlogDebug("Exiting runProcess : pPipelineParams, pPipelineResults");
    return CONTINUE;
  }

  public MFFOMSOrderManager getOrderManager() {
    return orderManager;
  }

  public void setOrderManager(MFFOMSOrderManager pOrderManager) {
    orderManager = pOrderManager;
  }

  public SettlementManager getSettlementManager() {
    return settlementManager;
  }

  public void setSettlementManager(SettlementManager pSettlementManager) {
    settlementManager = pSettlementManager;
  }
}
