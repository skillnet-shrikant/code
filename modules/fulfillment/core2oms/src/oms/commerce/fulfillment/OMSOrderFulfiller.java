/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.fulfillment;

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import oms.commerce.fulfillment.OMSPipelineConstants;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.states.OMSOrderStates;
import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.fulfillment.OrderFulfiller;
import atg.commerce.fulfillment.SubmitOrder;
import atg.commerce.messaging.CommerceMessage;
import atg.commerce.order.Order;
import atg.commerce.states.StateDefinitions;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.service.pipeline.RunProcessException;


public class OMSOrderFulfiller extends OrderFulfiller{

	private CatalogTools catalogTools;

	private boolean mEnabled;


	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return mEnabled;
	}

	/**
	 * @param pEnabled the enabled to set
	 */
	public void setEnabled(boolean pEnabled) {
		mEnabled = pEnabled;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void handleSubmitOrder(String pPortName, ObjectMessage pMessage)
	throws JMSException {


		// If the component is not enabled do not do anything.
		if (!isEnabled()) {
			vlogDebug("Ignoring the message");
			return;
		}
			

		if (isLoggingDebug())
			logDebug("Handling a SubmitOrder message");		
		// the input params to the chain
		HashMap map = new HashMap(10);
		map.put(OMSPipelineConstants.MESSAGE, pMessage);
		map.put(OMSPipelineConstants.ORDERFULFILLER, this);
		map.put(OMSPipelineConstants.CATALOG_TOOLS, getCatalogTools());
		map.put(OMSPipelineConstants.PIPELINE_OMS_PROCESS, OMSOrderConstants.SAVE_TO_OMS);

		// Call the pipeline chain in a new transaction
		String orderId = null;
		TransactionDemarcation td = new TransactionDemarcation();
		boolean rollback = true;
		try {

			vlogDebug("Saving Orders to OMS only");

			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
			String chainToRun = (String) getChainToRunMap().get("saveOrderToOMSChain");
			getFulfillmentPipelineManager().runProcess(chainToRun, map);
			rollback = false;
			if(isLoggingDebug()){
				logDebug("saveOrderToOMSChain completed without exceptions");
			}
		} catch (RunProcessException e) {
			Throwable p = e.getSourceException();
			vlogError(e, "There was runprocessexception occurred while processing the following order {0}", orderId);
			if (isLoggingError()){
				logError("RunProcessException inhandleSubmitOrder:",p);
			}	

			// If an exception was thrown in the submit order pipeline, capture the order id
			// A non-null order id is used to identify a pipeline exception outside the transaction
			CommerceMessage commerceMessage = (CommerceMessage) pMessage.getObject();
			if(commerceMessage instanceof SubmitOrder)
			{
				orderId=((SubmitOrder) commerceMessage).getOrderId();
			}

			if (p instanceof JMSException) {
				throw (JMSException) p;
			}
		}catch (TransactionDemarcationException tde) {
			if (isLoggingError()) {
				logError("TransactionDemarcationException calling the pipeline process " + tde);
			}
		}
		finally {
			try {
				td.end(rollback);
			}
			catch (TransactionDemarcationException tde) {
				if (isLoggingError()) {
					logError("An exception was caught in the finally clause while setting order status to error_to_oms." + tde);
				}
			}
		}


		if (orderId != null){
			// change the order state to error_to_oms if there is any exception while moving order to oms
			if (isLoggingError()){
				logError("There was an error while moving an order to OMS");
			}
			try
			{
				Order order=getOrderManager().loadOrder(orderId);
				synchronized(order)
				{
					order.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.ERROR_TO_OMS));
					getOrderManager().updateOrder(order);
				}
				if (isLoggingError()){
					logError("The order with id:"+orderId+" was failed while moving to OMS so its state is changed to error_to_oms");
				}
			}
			catch(CommerceException ce){	
				if (isLoggingError()){
					logError("CommerceException while changing order state to error_to_oms::",ce);
				}
			}			
		}
		
		if (isLoggingDebug())
			logDebug("End : Handling a SubmitOrder message");
	}
	
	public CatalogTools getCatalogTools() {
		return catalogTools;
	}
	public void setCatalogTools(CatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
