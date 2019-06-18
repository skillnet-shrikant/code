package com.mff.commerce.order.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.mff.commerce.order.MFFOrderImpl;

public class ProcRemoveZeroAmountPaymentGroups extends GenericService implements PipelineProcessor {

	private final int SUCCESS = 1;
	
	private OrderManager mOrderManager = null;
	
	@Override
	public int[] getRetCodes() {
		return new int [] {SUCCESS};
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		
		vlogDebug("Called ProcRemoveZeroAmountPaymentGroupsrunProcess().");

		HashMap map = (HashMap) pParam;
		MFFOrderImpl order = (MFFOrderImpl) map.get(PipelineConstants.ORDER);
		OrderManager orderManager = (OrderManager)map.get("OrderManager");
		
    if(order.getPaymentGroupCount() <= 1)
      return SUCCESS;
  
      List paymentGroups = order.getPaymentGroups();
      ArrayList removalList = new ArrayList(paymentGroups.size());
      Iterator iter = paymentGroups.iterator();
      do
      {
        if(!iter.hasNext())
          break;
        PaymentGroup group = (PaymentGroup)iter.next();
        if(group.getAmount() == 0.0d)
          removalList.add(group.getId());
      } while(true);
      
      if(removalList.size()>0)
      {
        for(iter = removalList.iterator(); iter.hasNext(); 
            orderManager.getPaymentGroupManager().removePaymentGroupFromOrder(order, (String)iter.next()));
      }
      vlogDebug("Exiting runProcess");
		return SUCCESS;
	}

	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.mOrderManager = orderManager;
	}
}