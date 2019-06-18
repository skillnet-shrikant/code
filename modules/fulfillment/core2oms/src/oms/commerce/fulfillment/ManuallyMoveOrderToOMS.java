/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.fulfillment;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.TransactionManager;

import oms.commerce.order.OMSOrderConstants;

import atg.commerce.CommerceException;
import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.SimpleOrderManager;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;

/**
 * A dynamo service that can be used to push orders from CORE to OMS manually.
 * This service can be invoked from the dynamo admin console. An order Id or a
 * array of order Ids can be configured and the method moveOrderIdToOMS() /
 * moveMultipleOrdersToOMS() can be invoked.
 * 
 * <p>
 * This can be used to move the orders manually once orders with status
 * error_to_oms is manually corrected.
 * 
 * @author KnowledgePath Solutions Inc.
 */
public class ManuallyMoveOrderToOMS extends GenericService {

	// ------------------------------------------
	// CONFIGURED PROPERTIES
	// ------------------------------------------
	private SimpleOrderManager orderManager;
	private TransactionManager transactionManager;
	private PipelineManager fulfillmentPipelineManager;
	private String orderId;
	private String[] orders;

	// ------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------
	/**
	 * Moves the order configured in the component property orderId from CORE to
	 * OMS
	 */
	public void moveOrderIdToOMS() {
		moveOrderIdToOMS(getOrderId(), true);
	}

	/**
	 * Moves the orders configured in the component property orders from CORE to
	 * OMS
	 */
	public void moveMultipleOrdersToOMS() {
		for (String ordId : getOrders())
			moveOrderIdToOMS(ordId, true);
	}

	/**
	 * Moves the order configured in the component property orderId from CORE to
	 * OMS. Bypass the inventory hold processor.
	 */
	public void moveOrderIdToOMSSkipInventory() {
		moveOrderIdToOMS(getOrderId(), false);
	}

	/**
	 * Moves the orders configured in the component property orders from CORE to
	 * OMS. Bypass the inventory hold processor.
	 */
	public void moveMultipleOrdersToOMSSkipInventory() {
		for (String ordId : getOrders())
			moveOrderIdToOMS(ordId, false);
	}

	/**
	 * Moves the order in the method argument from CORE to OMS
	 * 
	 * @param ordId
	 *            Order Id to Move to OMS from CORE
	 * @param processInventory
	 *            Should the inventory hold processing be done
	 */
	protected void moveOrderIdToOMS(String ordId, boolean processInventory) {
		if (isLoggingDebug())
			logDebug("Inside moveOrderToOMS with order id : " + ordId);

		if (!StringUtils.isEmpty(ordId)) {
			try {
				vlogDebug("orderId is: {0}", ordId);

				if (getOrderManager().orderExists(ordId)) {
					this.processOrder(ordId, processInventory);
				} else {
					vlogDebug("Sorry the order for the given id doesn't exist");
				}
			} catch (CommerceException ce) {
				vlogError(ce, "CommerceException in moveOrderToOMS");
			}
		} else {
			vlogError("Please add order id to process");
		}
	}

	/**
	 * Calls the handleSaveOrderToOMSManual pipeline chain to move the order
	 * from CORE to OMS manually.
	 * 
	 * @param ordId
	 *            Order Id to be processed
	 * @param processInventory
	 *            Should the inventory processor be invoked
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processOrder(String ordId, boolean processInventory) {
		vlogDebug("Entering processOrder - ordId, processInventory");

		TransactionDemarcation td = new TransactionDemarcation();
		boolean rollback = true;
		try {
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

			// load the order
			OrderImpl order = (OrderImpl) getOrderManager().loadOrder(ordId);
			synchronized (order) {
				// invoke the pipeline
				Map map = new HashMap();
				map.put(PipelineConstants.ORDER, order);
				map.put(OMSOrderConstants.PIPELINE_OMS_PROCESS, "saveToOMS");
				map.put(OMSOrderConstants.PIPELINE_PROCESS_INVENTORY, processInventory);

				logInfo("We are about to manually move core order to OMS whose Id is: " + ordId);

				if (isLoggingDebug())
					logDebug("Calling pipeline for order: " + ordId);

				this.getFulfillmentPipelineManager().runProcess(OMSOrderConstants.PIPELINE_MANUAL_MOVE_OMS_CHAIN, map);

				setOrderId("");
				setOrders(null);
			}

			rollback = false;
		} catch (RunProcessException rpe) {
			logError("RunProcessException in processOrder:", rpe);
		} catch (TransactionDemarcationException tde) {
			logError("TransactionException in processInvoiceMessage:", tde);
		} catch (CommerceException ce) {
			logError("CommerceException in processInvoiceMessage:", ce);
		} catch (Exception e) {
			vlogError(e,"Exception in processInvoiceMessage:");
		} finally {
			try {
				td.end(rollback);
			} catch (TransactionDemarcationException tde) {
				vlogError(tde, "TransactionDemarcationException in finally processInvoiceMessage:");
			}
		}
	}

	// ------------------------------------------
	// GETTERS AND SETTERS
	// ------------------------------------------

	/**
	 * @return Order manager (CORE Order manager)
	 */
	public SimpleOrderManager getOrderManager() {
		return orderManager;
	}

	/**
	 * @param orderManager
	 *            Order manager (CORE Order manager)
	 */
	public void setOrderManager(SimpleOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	/**
	 * @return Transaction manager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager
	 *            Transaction manager
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return Fulfillment Pipeline Manager
	 */
	public PipelineManager getFulfillmentPipelineManager() {
		return fulfillmentPipelineManager;
	}

	/**
	 * @param fulfillmentPipelineManager
	 *            Fulfillment Pipeline Manager
	 */
	public void setFulfillmentPipelineManager(PipelineManager fulfillmentPipelineManager) {
		this.fulfillmentPipelineManager = fulfillmentPipelineManager;
	}

	/**
	 * @return The order id to be manually moved from CORE to OMS. This needs to
	 *         be set before invoking moveOrderIdToOMS() /
	 *         moveOrderIdToOMSSkipInventory()
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId
	 *            The order id to be manually moved from CORE to OMS. This needs
	 *            to be set before invoking moveOrderIdToOMS() /
	 *            moveOrderIdToOMSSkipInventory()
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return The list of order ids to be manually moved from CORE to OMS. This
	 *         needs to be set before invoking moveMultipleOrdersToOMS() /
	 *         moveMultipleOrdersToOMSSkipInventory()
	 */
	public String[] getOrders() {
		return orders;
	}

	/**
	 * @param orders
	 *            The list of order ids to be manually moved from CORE to OMS.
	 *            This needs to be set before invoking moveMultipleOrdersToOMS()
	 *            / moveMultipleOrdersToOMSSkipInventory()
	 */
	public void setOrders(String[] orders) {
		this.orders = orders;
	}
}
