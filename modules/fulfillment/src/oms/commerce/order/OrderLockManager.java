/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.order;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.TransactionManager;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

/**
 * The Order Lock Manager class that is responsible for locking and releasing 
 * orders in CSC and OMS
 * 
 * @author KnowledgePath Solutions Inc.
 */
public class OrderLockManager extends GenericService{

	// -------------------------------------------
	// STATIC CONSTANTS
	// -------------------------------------------
	public static final String ITEM_DESC_NAME_LOCKED_ORDER = "lockedOrder";
	public static final String PROP_NAME_LOCKED_ORDER_ID = "orderId";
	public static final String PROP_NAME_LOCKING_LOCK_ID = "lockId";
	public static final String PROP_NAME_LOCKING_SERVER_IP_ADDR = "serverIp";
	public static final String PROP_NAME_LOCK_TIME = "lockTime";
	public static final Object LOCK_ON_ORDER_LOCK_TABLE = new Object();

	// -------------------------------------------
	// PRIVATE VARIABLES
	// -------------------------------------------
	// The IP Address of the specific server in the cluster
	private String appServerIpAddress = "";

	// -------------------------------------------
	// CONFIGURED PROPERTIES
	// -------------------------------------------
	private int orderLockTimeoutMinutes;
	private Repository orderLockRepository;
	private TransactionManager transactionManager;
	private String forceReleaseOrderId;
	
	private boolean mEnabled;
	private boolean mTestMode;
	private long mThreadSleepTime;

	// -------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------
	/**
	 * Gets the application server IP address. This is a parameter attached to
	 * the lock.
	 */
	public OrderLockManager() {
		super();
		orderLockTimeoutMinutes = 15;

		try
		{
			java.net.InetAddress address = java.net.InetAddress.getLocalHost();
			appServerIpAddress = address.getHostAddress();
		}
		catch (java.net.UnknownHostException uhe)
		{
		  logError(uhe);
		}
	}

	/**
	 * Clears any locked orders associated to this server
	 */
	@Override
	public void doStartService() throws ServiceException
	{
		super.doStartService();
		releaseAllOrders();
	}

	/**
	 * Clears any locked orders associated to this server
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			releaseAllOrders();
		}
		finally
		{
			super.finalize();
		}
	}

	// -------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------
	public boolean lockOrder(String orderId, String lockName){
		return lockOrder(orderId, lockName, false);
	}
	
	/**
	 * To lock the specified order for the specified process if the order does 
	 * not have an active lock.
	 * 
	 * <p>To use this method for OMS tasks, send a String value specific to the task 
	 * as the processName parameter
	 * 
	 * @param orderId - The id of the Order to lock
	 * @param lockName - The name of the process or agent that would be locking the order
	 * @return <code>true</code> if the order is locked successfully, <code>false</code> 
	 * if it could not be locked for the process
	 */
	public boolean lockOrder(String orderId, String lockName, boolean useNewTransaction)
	{
		vlogDebug("In lockOrder. Order Id: {0}, Lock name: {1}", orderId, lockName);
		if (isLoggingDebug()){
			vlogDebug("In lockOrder. Order Id: {0}, Lock name: {1}", orderId, lockName);
		}
		
		if(!isEnabled()){
		  vlogInfo("In lockOrder. Lock Order is disabled so returning true");
		  return true;
		}
		if(orderId == null || lockName == null){
			if (isLoggingInfo()){
				vlogInfo("Not locking order. Either order Id or lock name is empty. Order Id: {0}, Lock name: {1}", orderId, lockName);
			}
			return false;
		}			
		
		boolean rollback = true;
		TransactionDemarcation td = null;
		TransactionManager tm = null;

		try
		{
			td = new TransactionDemarcation();
			tm = getTransactionManager();
			if(tm != null)
				td.begin(tm, (useNewTransaction ? TransactionDemarcation.REQUIRES_NEW : TransactionDemarcation.REQUIRED));

			boolean orderLockedSuccessfully = lockOrderMethod(orderId, lockName);

			// No need to commit this transaction if order could not be locked
			rollback = !orderLockedSuccessfully;
			
			if (isLoggingDebug()){
				logDebug("Order lock successful? " + orderLockedSuccessfully);
			}
			return orderLockedSuccessfully;
		}
		catch(TransactionDemarcationException tde)
		{
			vlogWarning(tde, "An exception occurred while trying to lock the order id = {0} lock name = {1}", orderId, lockName);
		}
		finally
		{
			if(tm != null)
			{
				try
				{
					if(td != null)
						td.end(rollback);
				}
				catch(TransactionDemarcationException tde)
				{
				  vlogError(tde,"Error while locking orderId {0}",orderId);
				}
			}
		}

		return false;
	}
	
	public void releaseOrder(String orderId, String lockId)
	{
		releaseOrder(orderId, lockId, false);
	}

	/**
	 * To release the lock on the specified order. The lock will be released 
	 * only by the lock who is locking the order. Use new transaction for 
	 * locking from OMS (either specify in pipeline definition or use the flag)
	 * 
	 * @param orderId - id of the order on which the lock has to be released
	 * @param lockId - id of the agent or process who wants to release the lock
	 */
	public void releaseOrder(String orderId, String lockId, boolean useNewTransaction)
	{
		vlogDebug("In releaseOrder. Order Id: {0}, Lock name: {1}", orderId, lockId);
		if (isLoggingDebug()){
			vlogDebug("In releaseOrder. Order Id: {0}, Lock name: {1}", orderId, lockId);
		}
		if(!isEnabled()){
      vlogInfo("In releaseOrder. Lock Order is disabled so returning");
      return;
    }
		
		if(orderId == null || lockId == null){
			if (isLoggingInfo()){
				vlogInfo("Not releasing order. Either order Id or lock name is empty. Order Id: {0}, Lock name: {1}", orderId, lockId);
			}
			return;
		}
		
		boolean rollback = true;
		TransactionDemarcation td = null;
		TransactionManager tm = null;

		try
		{
			td = new TransactionDemarcation();
			tm = getTransactionManager();
			if(tm != null)
				td.begin(tm, (useNewTransaction ? TransactionDemarcation.REQUIRES_NEW : TransactionDemarcation.REQUIRED));
			
			
			if(isTestMode()){
				logInfo("releasing lock before sleep");
				try {
					Thread.sleep(getThreadSleepTime());
				} catch (InterruptedException e) {
					logError(e);
				}
				logInfo("releasing lock after sleep");
			}
			
			synchronized(LOCK_ON_ORDER_LOCK_TABLE)
			{
				RepositoryItem lockedOrderItem = getLockedOrderItem(orderId);

				if(lockedOrderItem != null && lockedOrderItem.getPropertyValue(PROP_NAME_LOCKING_LOCK_ID).equals(lockId))
				{
					try
					{
						removeLockedOrderItem(orderId);

						if(isLoggingDebug())
							logDebug("The order " + orderId + " has been successfully released by lock " + lockId);
					}
					catch(RepositoryException re)
					{
						vlogError(re,"Exception while releasing locked order {0} for lock {1}",orderId,lockId);
					}
					catch(Exception e)
					{
						vlogError(e, "Exception while releasing locked order {0} for lock {1}",orderId,lockId);
					}
				}
				else
				{
					if(isLoggingDebug())
						logDebug("The order " + orderId + " is either not locked or the lock " + lockId + " is not eligible to release the lock");
				}
			}

			rollback = false;
		}
		catch(TransactionDemarcationException tde)
		{
		  vlogError(tde,"Error while releaseOrder orderId {0}",orderId);
		}
		finally
		{
			if(tm != null)
			{
				try
				{
					if(td != null)
						td.end(rollback);
				}
				catch(TransactionDemarcationException tde)
				{
				  vlogError(tde,"Error while releaseOrder orderId {0}",orderId);
				}
			}
		}
	}

	/**
	 * Release locks on all orders associated with this server
	 */
	public void releaseAllOrders()
	{
		if (isLoggingDebug())
			logDebug("releaseAllOrders() : Start");
		try
		{
			RepositoryView rv = getOrderLockRepository().getView(ITEM_DESC_NAME_LOCKED_ORDER);
			QueryBuilder qb = rv.getQueryBuilder();
			QueryExpression pqe = qb.createPropertyQueryExpression(PROP_NAME_LOCKING_SERVER_IP_ADDR);
			QueryExpression cqe = qb.createConstantQueryExpression(appServerIpAddress);
			Query q = qb.createComparisonQuery(pqe, cqe, QueryBuilder.EQUALS);

			RepositoryItem[] lockedOrdersToRelease = rv.executeQuery(q);
			
			if(lockedOrdersToRelease != null)
			{
				if (isLoggingDebug())
					logDebug("Number of orders to release : " + lockedOrdersToRelease.length);
				
				MutableRepository mutableOrderLockRepository = (MutableRepository)getOrderLockRepository();
				for(RepositoryItem lockedOrder : lockedOrdersToRelease)
					mutableOrderLockRepository.removeItem(lockedOrder.getRepositoryId(), ITEM_DESC_NAME_LOCKED_ORDER);
			}
		}
		catch(RepositoryException re)
		{
		  vlogError(re,"Error inside releaseAllOrders");
		}
		if (isLoggingDebug())
			logDebug("releaseAllOrders() : Ended");
	}

	/**
	 * To display the list of all locked orders
	 * @return the list of locked orders as a String for display
	 */
	public String showAllLockedOrders()
	{
		if (isLoggingDebug())
			logDebug("showAllLockedOrders() : Start");
		
		RepositoryItem[] lockedOrderItems = null;

		try
		{
			RepositoryView rv = getOrderLockRepository().getView(ITEM_DESC_NAME_LOCKED_ORDER);
			QueryBuilder qb = rv.getQueryBuilder();
			QueryExpression pqe = qb.createPropertyQueryExpression(PROP_NAME_LOCKED_ORDER_ID);
			Query q = qb.createNotQuery(qb.createIsNullQuery(pqe));
			lockedOrderItems = rv.executeQuery(q);
		}
		catch(RepositoryException re)
		{
		  vlogError(re,"Error inside showAllLockedOrders");
		}

		StringBuffer lockedOrdersStrBuf = new StringBuffer("{ ");

		if(lockedOrderItems != null)
		{
			if (isLoggingDebug())
				logDebug("Number of locked orders : " + lockedOrderItems.length);
			
			for(RepositoryItem lockedOrder : lockedOrderItems)
				lockedOrdersStrBuf.append("[").append(PROP_NAME_LOCKED_ORDER_ID).append(":").append(lockedOrder.getRepositoryId()).append(", ").append(PROP_NAME_LOCKING_LOCK_ID).append(":").append(lockedOrder.getPropertyValue(PROP_NAME_LOCKING_LOCK_ID)).append(", ").append(PROP_NAME_LOCKING_SERVER_IP_ADDR).append(":").append(lockedOrder.getPropertyValue(PROP_NAME_LOCKING_SERVER_IP_ADDR)).append(", ").append(PROP_NAME_LOCK_TIME).append(":").append(getLockedTimeForDisplay((Date)lockedOrder.getPropertyValue(PROP_NAME_LOCK_TIME))).append("]; ");
		}

		if (isLoggingDebug())
			logDebug("showAllLockedOrders() : End");
		return (lockedOrdersStrBuf.toString() + "}");
	}

	/**
	 * To release the lock on the specified order forcefully.
	 * @param orderId - id of the order on which the lock has to be released
	 */
	public void forceReleaseOrder()
	{
		String orderId = getForceReleaseOrderId();
		if (isLoggingDebug())
			logDebug("forceReleaseOrder() : Start. Order Id : " + orderId);
		
		if(orderId == null)
			return;

		RepositoryItem lockedOrderItem = getLockedOrderItem(orderId);

		if(lockedOrderItem != null)
		{
			try
			{
				removeLockedOrderItem(orderId);

				if(isLoggingDebug())
					logDebug("The order " + orderId + " has been successfully released");
			}
			catch(RepositoryException re)
			{
				vlogError(re,"Exception while force-releasing locked order {0}",orderId);
			}
			catch(Exception e)
			{
				vlogError(e,"Exception while force-releasing locked order {0}",orderId);
			}
		}
		else
		{
			if(isLoggingDebug())
				logDebug("The order " + orderId + " is not locked to release it");
		}
	}

	/**
	 * To find out which agent or process is locking the specified order
	 * @param orderId - id of the locked order
	 * @return lock id if the order is locked, <code>null</code> if the order 
	 * is not locked by any agent or process
	 */
	public String getLockingLockId(String orderId)
	{
		synchronized(LOCK_ON_ORDER_LOCK_TABLE)
		{
			RepositoryItem lockedOrderItem = getLockedOrderItem(orderId);

			return (lockedOrderItem != null ? lockedOrderItem.getPropertyValue(PROP_NAME_LOCKING_LOCK_ID).toString() : null);
		}
	}
	// -------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------
	private boolean lockOrderMethod(String orderId, String agentId)
	{
		if(orderId == null || agentId == null)
			return false;

		try
		{
			synchronized(LOCK_ON_ORDER_LOCK_TABLE)
			{
				RepositoryItem lockedOrderItem = getLockedOrderItem(orderId);

				if(lockedOrderItem == null)
				{
					if(isLoggingDebug())
						logDebug("Order " + orderId + " is not locked so it is being locked by agent " + agentId);

					MutableRepositoryItem mutableOrderLockItem = ((MutableRepository)getOrderLockRepository()).createItem(orderId, ITEM_DESC_NAME_LOCKED_ORDER);
					mutableOrderLockItem.setPropertyValue(PROP_NAME_LOCKING_LOCK_ID, agentId);
					mutableOrderLockItem.setPropertyValue(PROP_NAME_LOCKING_SERVER_IP_ADDR, appServerIpAddress);
					mutableOrderLockItem.setPropertyValue(PROP_NAME_LOCK_TIME, getCurrentTime());
					((MutableRepository)getOrderLockRepository()).addItem(mutableOrderLockItem);

					return true;
				}

				if(lockedOrderItem.getPropertyValue(PROP_NAME_LOCKING_LOCK_ID).equals(agentId) || (System.currentTimeMillis() - ((Date)lockedOrderItem.getPropertyValue(PROP_NAME_LOCK_TIME)).getTime()) > (getOrderLockTimeoutMinutes() * 60 * 1000L))
				{
					if(isLoggingDebug())
						logDebug("Order " + orderId + " is either locked by the same agent already or the lock has timed out for another agent, so its lock is being updated for agent " + agentId);

					MutableRepositoryItem mutableOrderLockItem = ((MutableRepository)getOrderLockRepository()).getItemForUpdate(orderId, ITEM_DESC_NAME_LOCKED_ORDER);
					mutableOrderLockItem.setPropertyValue(PROP_NAME_LOCKING_LOCK_ID, agentId);
					mutableOrderLockItem.setPropertyValue(PROP_NAME_LOCKING_SERVER_IP_ADDR, appServerIpAddress);
					mutableOrderLockItem.setPropertyValue(PROP_NAME_LOCK_TIME, getCurrentTime());
					((MutableRepository)getOrderLockRepository()).updateItem(mutableOrderLockItem);

					return true;
				}
			}
		}
		catch(RepositoryException re)
		{
			vlogError(re,"Exception while locking order {0} for agent {1}",orderId,agentId);
		}
		catch(Exception e)
		{
			vlogError(e,"Exception while locking order {0} for agent {1}",orderId,agentId);
		}

		if(isLoggingDebug())
			logDebug("Could not acquire lock on order " + orderId + " because it is already locked and the lock is active");

		return false;
	}
	
	/**
	 * To get the locked order item from the order lock repository from the 
	 * specified order id
	 * 
	 * @param orderId - id of the order that is locked
	 * @return the {@link RepositoryItem} object that has the following details:<br/>
	 * Locked Order Id, Locking Lock Id, IP address of server in cluster used by the lock & Lock Time
	 */
	private RepositoryItem getLockedOrderItem(String orderId)
	{
		if(orderId == null || orderId.equals(""))
			return null;

		RepositoryItem lockedOrderItem = null;

		try
		{
			lockedOrderItem = getOrderLockRepository().getItem(orderId, ITEM_DESC_NAME_LOCKED_ORDER);
		}
		catch(RepositoryException re)
		{
			vlogError(re,"Error while retrieving order {0} from the database",orderId);
		}
		catch(Exception e)
		{
			vlogError("Error while retrieving order {0} from the database",orderId);
		}

		return lockedOrderItem;
	}

	/**
	 * Removes a lock record from the lock repository
	 * @param orderId Order Id to be removed
	 * @throws RepositoryException
	 */
	private void removeLockedOrderItem(String orderId) throws RepositoryException
	{
		((MutableRepository)getOrderLockRepository()).removeItem(orderId, ITEM_DESC_NAME_LOCKED_ORDER);
	}

	/**
	 * To get the locked time in a readable format
	 * @return The locked time value in 'yyyy-MM-dd HH:mm:ss' format
	 */
	private String getLockedTimeForDisplay(Date lockTime)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(lockTime);
	}
	
	/**
	 * To get the current time for creating or updating a lock
	 * @return the current time as {@link Date} object
	 */
	private Date getCurrentTime()
	{
		return new Date(System.currentTimeMillis());
	}

	// -------------------------------------------
	// GETTERS & SETTERS
	// -------------------------------------------
	/**
	 * @return The time in minutes after which an  acquired lock can expire 
	 * if another lock is requested on the order.
	 */
	public int getOrderLockTimeoutMinutes() {
		return orderLockTimeoutMinutes;
	}

	/**
	 * @param orderLockTimeoutMinutes The time in minutes after which an 
	 * acquired lock can expire if another lock is requested on the order.
	 */
	public void setOrderLockTimeoutMinutes(int orderLockTimeoutMinutes) {
		this.orderLockTimeoutMinutes = orderLockTimeoutMinutes;
	}

	/**
	 * @return The order lock repository
	 */
	public Repository getOrderLockRepository() {
		return orderLockRepository;
	}

	/**
	 * @param orderLockRepository The order lock repository
	 */
	public void setOrderLockRepository(Repository orderLockRepository) {
		this.orderLockRepository = orderLockRepository;
	}

	/**
	 * @return ATG transaction manager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager ATG transaction manager
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return If an order is to be force released, set this property value
	 * and invoke the method forceReleaseOrder()
	 */
	public String getForceReleaseOrderId() {
		return forceReleaseOrderId;
	}

	/**
	 * @param forceReleaseOrderId If an order is to be force released, set this 
	 * property value and invoke the method forceReleaseOrder()
	 */
	public void setForceReleaseOrderId(String forceReleaseOrderId) {
		this.forceReleaseOrderId = forceReleaseOrderId;
	}
	
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
  
  /**
   * 
   * @return
   */
  public boolean isTestMode() {
    return mTestMode;
  }
  
  /**
   * 
   * @param pTestMode
   */
  public void setTestMode(boolean pTestMode) {
    mTestMode = pTestMode;
  }
  
  /**
   * 
   * @return
   */
  public long getThreadSleepTime() {
    return mThreadSleepTime;
  }
  
  /**
   * 
   * @param pThreadSleepTime
   */
  public void setThreadSleepTime(long pThreadSleepTime) {
    mThreadSleepTime = pThreadSleepTime;
  }
}
