package com.mff.commerce.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.google.common.base.Strings;

import oms.allocation.item.AllocationConstants;
import oms.allocation.store.StoreAllocationManager;
import oms.commerce.order.OMSOrderManager;
import atg.commerce.csr.order.CSRCancelOrderFormHandler;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.droplet.DropletException;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * MFFCancelOrderFormHandler extends the OOTB CSRCancelOrderFormHandler to cancel the order and commerce Items.
 *
 */

public class MFFCancelOrderFormHandler extends CSRCancelOrderFormHandler {
  
	private OMSOrderManager mOrderManager = null;
	private PipelineManager mFulfillmentPipelineManager;
	private StoreAllocationManager mStoreAllocationManager;
	private String mCancelOrderReasonCode;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void cancelOrder(Order pOrder, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
             throws ServletException, IOException{
		
		vlogDebug("MFFCancelOrderFormHandler : Inside cancelOrder");
		
		MFFOrderImpl order = (MFFOrderImpl)pOrder;
		String orderNumber = order.getId();
		
		List<CommerceItem> items = order.getCommerceItems();
		List<String> commerceItemIds = new ArrayList<String>();
		HashMap<String,String> itemToReasonCodeMap = new HashMap<String,String>();
		for(CommerceItem item : items){
			commerceItemIds.add(item.getId());
			if(!Strings.isNullOrEmpty(getCancelOrderReasonCode())){
        itemToReasonCodeMap.put(item.getId(), getCancelOrderReasonCode());
      }
		}
		
		if(commerceItemIds.size() > 0){
			
			Map lPipelineParams = new HashMap();
			lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, pOrder);
			lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL,commerceItemIds);
			lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC,"Order Cancelled inside remorse period");
			lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP,itemToReasonCodeMap);
			
			try {
				getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_CANCEL, lPipelineParams);
			} catch (RunProcessException ex) {
				addFormException(new DropletException("Error cancelling Order -"  + orderNumber + ". Please try again later."));
				vlogError(ex, "Error running pipeline {0} for order number {1}", AllocationConstants.PIPELINE_CANCEL,orderNumber);			
			}
		}
  }
	
	public StoreAllocationManager getStoreAllocationManager() {
		return mStoreAllocationManager;
	}

	public void setStoreAllocationManager(StoreAllocationManager pStoreAllocationManager) {
		this.mStoreAllocationManager = pStoreAllocationManager;
	}
	
	public PipelineManager getFulfillmentPipelineManager() {
		return mFulfillmentPipelineManager;
	}
	
	public void setFulfillmentPipelineManager(PipelineManager pFulfillmentPipelineManager) {
		mFulfillmentPipelineManager = pFulfillmentPipelineManager;
	}	
	
	public void setOmsOrderManager(OMSOrderManager pOrderManager){
		mOrderManager = pOrderManager;
	}

	public OMSOrderManager getOmsOrderManager(){
		return this.mOrderManager;
	}

	public String getCancelOrderReasonCode() {
    return mCancelOrderReasonCode;
  }

  public void setCancelOrderReasonCode(String pCancelOrderReasonCode) {
    this.mCancelOrderReasonCode = pCancelOrderReasonCode;
  }	
}