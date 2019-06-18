package oms.commerce.salesaudit.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;

import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.processor.EXTNPipelineProcessor;
import oms.commerce.salesaudit.util.InvoiceManager;

/**
 * This processor will add a new invoice to the invoice repository when the store 
 * marks the item as shipped.  This processor will be called from the handleStoreShipment
 * pipeline chain, which is the OMS Pipeline.
 * 
 * @author jvose
 *
 */
public class ProcAddInvoiceForShipping 
  extends EXTNPipelineProcessor {
	
	boolean skipInvoice;

  public boolean isSkipInvoice() {
		return skipInvoice;
	}

	public void setSkipInvoice(boolean pSkipInvoice) {
		skipInvoice = pSkipInvoice;
	}

@SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public int runProcess(Object pParam, PipelineResult pArg1)  
    throws Exception {
    vlogDebug("ProcAddInvoiceForShipping.runProcess - begin");

    // Get order from the pipeline parameters
    Map lPipelineParams                     = (Map) pParam;
    MFFOrderImpl lOrder                     = (MFFOrderImpl) lPipelineParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    List<String> lItemsToShip               = (List<String>) lPipelineParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
    Map<String, Map<String, Double>> lSettlementMap  = (Map<String, Map<String, Double>>) lPipelineParams.get(AllocationConstants.PIPELINE_PARAMETER_SETTLEMENT_MAP);
    boolean splitOrderEnabled = getInvoiceManager().getOmsOrderManager().isSplitOrderEnabled();
    
    // Add new shipment invoice
    if(lItemsToShip != null && lItemsToShip.size() > 0) {
      if(isSplitOrder(lOrder) && splitOrderEnabled){
        getInvoiceManager().addShipmentInvoiceForSplitOrder(lOrder, lItemsToShip, lSettlementMap);
      }else{
    	  if(!skipInvoice) {
    		  getInvoiceManager().addShipmentInvoice(lOrder, lItemsToShip, lSettlementMap);
    	  }
      }
    }
    
    vlogDebug("ProcAddInvoiceForShipping.runProcess - end");
    return CONTINUE;
  }
  
  private boolean isSplitOrder(MFFOrderImpl pOrderImpl){
    
    if(pOrderImpl.getCommerceItemCount() > 0){
      HashMap<String,String> skuToCommerceItemMap = new HashMap<String,String>();
      for(Object ci : pOrderImpl.getCommerceItems()){
        MFFCommerceItemImpl mffCI = (MFFCommerceItemImpl) ci;
        String skuId = mffCI.getCatalogRefId();
        if(mffCI != null && skuToCommerceItemMap != null){
          if(skuToCommerceItemMap.get(skuId) != null && !getInvoiceManager().getOmsOrderManager().isGiftCardItem(mffCI)){
            return true;
          }else{
            skuToCommerceItemMap.put(skuId, mffCI.getId());
          }
        }
      }
    }
    return false;
  }
  
  // *********************************************************
  //            Getter/setters
  // *********************************************************
  InvoiceManager mInvoiceManager;
  public InvoiceManager getInvoiceManager() {
    return mInvoiceManager;
  }
  public void setInvoiceManager(InvoiceManager pInvoiceManager) {
    this.mInvoiceManager = pInvoiceManager;
  }
    
}