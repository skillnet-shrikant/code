package com.aci.commerce.order.processor;

import java.util.HashMap;

import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.aci.constants.AciConstants;
import com.aci.pipeline.exception.AciPipelineException;

/**
 * This processor call ACI service and adds the returned token number to payment group. This will be added to the
 * moveToConfirmation pipeline chain.
 * 
 * @author DMI
 * 
 */
public class ProcTokenizeCreditCardWithACI extends GenericService implements PipelineProcessor {

	private final int SUCCESS = 1;
	
	private OrderManager mOrderManager = null;
	
	@Override
	public int[] getRetCodes() {
		return new int [] {SUCCESS};
	}
	
	
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		
		vlogDebug("Called runProcess().");

		HashMap map = (HashMap) pParam;
		Order order = (Order)map.get("Order");
		 if(order == null)
			 throw new AciPipelineException(AciConstants.ACI_ORDER_NOT_FOUND, true);
		
		vlogDebug("ProcTokenizeCreditCardWithACI: runProcess(): Exiting.");
		return SUCCESS;
	}


	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.mOrderManager = orderManager;
	}
}