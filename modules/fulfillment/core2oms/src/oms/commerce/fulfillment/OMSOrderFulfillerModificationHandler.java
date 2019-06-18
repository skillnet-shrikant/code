/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.fulfillment;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import atg.commerce.fulfillment.OrderFulfillerModificationHandler;

/**
 * This class is to override the OrderFulfiller to either process the 
 * message or not process a message based on the enable and disable 
 * flag.
 * 
 * @author KnowledgePath Solutions Inc.
 *
 */
public class OMSOrderFulfillerModificationHandler extends
OrderFulfillerModificationHandler {
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

	/* (non-Javadoc)
	 * @see atg.commerce.fulfillment.OrderFulfillerModificationHandler#handleModifyOrder(java.lang.String, javax.jms.ObjectMessage)
	 */
	@Override
	public void handleModifyOrder(String pPortName, ObjectMessage pMessage)
	throws JMSException {
		if (!isEnabled())
			return;
		super.handleModifyOrder(pPortName, pMessage);
	}

	/* (non-Javadoc)
	 * @see atg.commerce.fulfillment.OrderFulfillerModificationHandler#handleModifyOrderNotification(java.lang.String, javax.jms.ObjectMessage)
	 */
	@Override
	public void handleModifyOrderNotification(String pPortName,
			ObjectMessage pMessage) throws JMSException {
		if (!isEnabled())
			return;
		super.handleModifyOrderNotification(pPortName, pMessage);
	}


}
