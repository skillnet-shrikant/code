/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.jobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.transaction.TransactionManager;

import oms.commerce.order.OMSOrderManager;
import oms.commerce.states.OMSOrderStates;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.states.StateDefinitions;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

/**
 * Once an order has been submitted to the WMS, it cannot be edited or 
 * cancelled in the CSC. So a remorse period feature is implemented so that 
 * the order does not get submitted to the WMS as soon as it is taken. The 
 * orders copied over to the OMS have a state of IN_REMORSE. 
 * 
 * <p>This scheduled process looks up all orders that have completed the 
 * remorse period since submitted and is in IN_REMORSE status and updates 
 * its status to be PRE_SENT_WMS. 
 * 
 * @author KnowledgePath Solutions Inc.
 *
 */
public class RemorsePeriod extends OMSSingletonScheduleTask{

	OMSOrderManager mOrderManager;
	//OrderLockManager mLockManager;

	public static final String LOCK_NAME = "oms_remorse_period_lock";
	static final String ORDER_VIEW = "order";


	@Override
	protected void performTask() {
		//Get all orders with state "IN_REMORSE" and whose submittedTime < (currentTime  - remorsePeriod).
		RepositoryItem[] items = getRemorseOrders();
		//Change state from IN_REMORSE --> PRE_SENT_WMS of the fetched orders.
		changeOrderStatus(items);
	}

	/**
	 * Get time in yyyy-MM-dd HH:mm:ss zzz format after subtracting remorse 
	 * period (in minutes) from current time.
	 * @param remorsePeriod
	 * @return String in yyyy-MM-dd HH:mm:ss zzz format
	 */
	private String getRemorsePeriodExpirationDate(int remorsePeriod){
		// Remorse period in milliseconds = (remorse period) * (milliseconds in one minute);
		long TIME_IN_MILLI_SECONDS= (remorsePeriod) * 60000;
		Date date = Calendar.getInstance().getTime();
		long t=date.getTime();
		Date today=new Date(t - (TIME_IN_MILLI_SECONDS));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
		String retValue = df.format(today);
		return retValue;
	}

	/**
	 * Get all orders with state "IN_REMORSE" and whose submittedTime < (currentTime  - remorsePeriod).
	 * @return order repository items list
	 */
	private RepositoryItem[] getRemorseOrders(){
		RepositoryItem[] items = null;
		try {

			RepositoryView orderView =  getOrderManager().getOmsOrderRepository().getView(ORDER_VIEW);

			String remorsePeriodExpiryDate = getRemorsePeriodExpirationDate(getOrderManager().getRemorsePeriod());

			if(isLoggingDebug()){
				logDebug("Order Remorse period expired at: " + remorsePeriodExpiryDate);
			}
			//rql query is:  state=?0 and datetime(?1) > submittedDate
			String queryString = "state=\"" + OMSOrderStates.IN_REMORSE.toUpperCase() + "\" and  datetime(\""  + 
					remorsePeriodExpiryDate + "\") > submittedDate";

			if(isLoggingDebug()){
				logDebug("Remorse period query: "  + queryString);
			}

			RqlStatement statement = RqlStatement.parseRqlStatement(queryString);
			items = statement.executeQuery(orderView,new Object[]{});

		} catch (RepositoryException e) {
			logError("Repository Exception: " + e.getMessage());
		} 
		return items;
	}

	/**
	 * Change order status from IN_REMORSE -->PRE_SENT_WMS
	 * @param Orders 
	 */
	private  void changeOrderStatus(RepositoryItem[] items){

		if(items != null && items.length > 0){
			try{   
				if(isLoggingDebug()){
					logDebug("Found " + items.length + " orders with IN_REMORSE state");
				}

				for(RepositoryItem item : items){
					Order order =	getOrderManager().loadOrder(item.getRepositoryId());
					synchronized(order){
						TransactionDemarcation td =new TransactionDemarcation(); 
						try{
							td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
							order.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.PENDING_ALLOCATION));
							getOrderManager().updateOrder(order);
							vlogInfo ("Successfully changed order status from IN_REMORSE to PENDING_ALLOCATION orderId : {0}", order.getId());
/*							if(getLockManager().lockOrder(item.getRepositoryId(), LOCK_NAME)){
								order.setState(StateDefinitions.ORDERSTATES.getStateValue(EXTNOrderStates.PRE_SENT_WMS));
								getLockManager().releaseOrder(item.getRepositoryId(), LOCK_NAME);
								getOrderManager().updateOrder(order);
							}
*/
							} catch (TransactionDemarcationException e) {
							if (isLoggingError()) {
								logError("Could not update order status. Error: " + e.getMessage());
							}
						}
						finally {
							try {
								td.end();
							}
							catch (TransactionDemarcationException tde) {
								if (isLoggingError()) {
									logError(tde);
								}
							}
						}

					}
				}
			}catch (CommerceException e) {
				logError("CommerceException:" + e.getMessage());
			}
		}

	}

	public OMSOrderManager getOrderManager() {
		return mOrderManager;
	}
	public void setOrderManager(OMSOrderManager pOrderManager) {
		this.mOrderManager = pOrderManager;
	}

	/*public OrderLockManager getLockManager() {
		return mLockManager;
	}

	public void setLockManager(OrderLockManager pLockManager) {
		this.mLockManager = pLockManager;
	}*/


	private TransactionManager transactionManager;

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager
	 *            the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}


}