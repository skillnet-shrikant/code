package com.mff.commerce.order.processor;

import java.util.HashMap;

import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.order.OrderManager;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderTools;

/**
 * The following class adds the an order number. This will be added to the
 * processOrder pipeline chain.
 * 
 * @author DMI
 * 
 */
public class ProcAddOrderNumber extends GenericService implements PipelineProcessor {

	private final int SUCCESS = 1;
	
	private OrderManager mOrderManager = null;
	
	@Override
	public int[] getRetCodes() {
		return new int [] {SUCCESS};
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		
		vlogDebug("Called runProcess().");

		HashMap map = (HashMap) pParam;
		MFFOrderImpl order = (MFFOrderImpl) map.get(PipelineConstants.ORDER);
		MFFOrderTools orderTools = (MFFOrderTools) getOrderManager().getOrderTools();
		
		String orderNumber = order.getOrderNumber();
		String orderType = orderTools.getOrderType(order);
		
		boolean orderTypeToNumberPrefixMismatch = orderTools.isCurrentOrderNumPrefixMatchesType(orderType, orderNumber);
		vlogDebug("runProcess(): orderTypeToNumberPrefixMismatch: " + orderTypeToNumberPrefixMismatch);
		
		if (StringUtils.isBlank(orderNumber) || orderTypeToNumberPrefixMismatch) {
			order.setOrderNumber(orderTools.getNextOrderNumber(orderType));
			vlogDebug ("Generating order number for the order id = {0} and setting the order number {1}", order.getId(), order.getOrderNumber());
		} else {
			vlogDebug("Order number already set to {0}", order.getOrderNumber());
			vlogInfo("Order number:  order id = {0} order number {1}", order.getId(), order.getOrderNumber());
		}
		
		//adding app instance name
		String instanceName=orderTools.getAppInstanceName();
		if(instanceName!=null&&!instanceName.isEmpty()){
			order.setAppInstanceName(instanceName);
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