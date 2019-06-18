package oms.dashboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.TransactionManager;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.reports.dashboard.OrderDashboardRepoHelper;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;

public class AsychronousOrderDashboard extends GenericService {

	private static ExecutorService pool;
	private int threadPoolSize;
	private boolean asyncEnabled;
	
	private OrderDashboardRepoHelper dashboardHelper;
	private TransactionManager transactionManager;

	public void doStartService() throws ServiceException {
		super.doStartService();

		if (getThreadPoolSize() > 0) {
			pool = Executors.newFixedThreadPool(getThreadPoolSize());
		} else {
			pool = Executors.newFixedThreadPool(3);
		}
	}

	public void doStopService() throws ServiceException {
		super.doStopService();
		if (pool != null) {
			pool.shutdown();
		}
	}

	/**
	 * 
	 * @param lParams
	 * @param lOrder
	 */
	@SuppressWarnings("rawtypes")
	public void processDashboardRequest(Map lParams, Order lOrder, String processName) {
		if (lParams == null || lOrder == null || StringUtils.isBlank(processName)) {
			return;
		}
		if (isAsyncEnabled()) {
			final Map pMap = lParams;
			final Order pOrder = lOrder;
			final long quantity = getAllCommerceItemQty(lOrder);
			final String pProcessName = processName;
			pool.execute(new Runnable() {
				public void run() {
					process(pMap, pOrder, pProcessName,quantity);
				}
			});
		} else {
			final long quantity = getAllCommerceItemQty(lOrder);
			process(lParams, lOrder, processName,quantity);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void process(Map lParams, Order lOrder, String processName, long pOrderQuantity) {
		TransactionDemarcation td = null;
		boolean rollback = true;
		
		if(isLoggingDebug()){
			logDebug("Begin processing dashboard request " + processName);
		}
		try {
			 td = new TransactionDemarcation();
		     td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
		        
			List<ItemAllocation> lItemAllocations = (List<ItemAllocation>) lParams
					.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS);
			List<String> lItemsToShip = (List<String>) lParams
					.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
			List<String> lItemsToCancel = (List<String>) lParams
					.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL);
			List<String> lItemsToDecline = (List<String>) lParams
					.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_DECLINE);

			if (processName.equalsIgnoreCase("ADD-ORDER")) {
				getDashboardHelper().submitOrderToDashboard(lOrder, pOrderQuantity);
			}

			if (processName.equalsIgnoreCase("CANCELLED-ORDER") && lItemsToCancel != null
					&& lItemsToCancel.size() > 0) {
				getDashboardHelper().updateCancelledItemsToDashboard(lOrder, getCommerceItems(lOrder, lItemsToCancel));
			}

			if (processName.equalsIgnoreCase("SHIPPED-ORDER") && lItemsToShip != null && lItemsToShip.size() > 0) {
				getDashboardHelper().updateShippedItemsToDashboard(lOrder, getCommerceItems(lOrder, lItemsToShip));

				MFFOrderImpl mffOrder = (MFFOrderImpl) lOrder;
				if (mffOrder.isBopisOrder()) {
					getDashboardHelper().incrementShippedBopisOrderCount(mffOrder.getBopisStore());
				} else {
					Set<String> stores = getUniqueStoreIds(lItemsToShip, lOrder);
					if (stores != null && stores.size() > 0) {
						for (String storeId : stores) {
							getDashboardHelper().incrementShippedOrderCount(storeId);
						}
					}
				}
			}

			if (processName.equalsIgnoreCase("STORE-ASSIGN-COUNT") && lItemAllocations != null
					&& lItemAllocations.size() > 0) {
				MFFOrderImpl mffOrder = (MFFOrderImpl) lOrder;
				if (mffOrder.isBopisOrder()) {
					getDashboardHelper().incrementAssignedBopisOrderCount(mffOrder.getBopisStore());
				} else {
					Set<String> stores = getUniqueStoreIds(lItemAllocations, lOrder);
					if (stores != null && stores.size() > 0) {
						for (String storeId : stores) {
							getDashboardHelper().incrementAssignedOrderCount(storeId);
						}
					}
				}
			}

			if (processName.equalsIgnoreCase("STORE-DECLINE-COUNT") && lItemsToDecline != null
					&& lItemsToDecline.size() > 0) {
				MFFOrderImpl mffOrder = (MFFOrderImpl) lOrder;
				if (mffOrder.isBopisOrder()) {
					getDashboardHelper().incrementRejectedBopisOrderCount(mffOrder.getBopisStore());
				} else {
					Set<String> stores = getUniqueStoreIds(lItemsToDecline, lOrder);
					if (stores != null && stores.size() > 0) {
						for (String storeId : stores) {
							getDashboardHelper().incrementRejectedOrderCount(storeId);
						}
					}
				}
			}
			rollback = false;
			if(isLoggingDebug()){
				logDebug(" Successfully processed dashboard request " + processName);
			}
		} catch (Exception e) {
			if(isLoggingError()){
				logError(e);
			}
		} finally {
			try {
				td.end(rollback);
			} catch (TransactionDemarcationException tde) {
				vlogError(tde, "TransactionDemarcationException in finally processInvoiceMessage:");
			}
		}

	}

	/**
	 * Gets the unique store ids.
	 *
	 * @param pItems
	 *            the items
	 * @param pOrder
	 *            the order
	 * @return the unique store ids
	 */
	@SuppressWarnings("rawtypes")
	protected Set<String> getUniqueStoreIds(List pItems, Order pOrder) {

		Set<String> storeIds = new HashSet<String>();
		String itemId = null;
		if (pItems != null) {
			ItemAllocation itemAllocated = null;
			for (Object item : pItems) {
				if (item instanceof String) {
					itemId = (String) item;
					MFFCommerceItemImpl mffItem = null;
					try {
						mffItem = getCommerceItem(pOrder, itemId);
					} catch (Exception e) {
						logError(e);
					}
					if (StringUtils.isNotBlank(mffItem.getFulfillmentStore())) {
						storeIds.add(mffItem.getFulfillmentStore());
					}
				} else if (item instanceof ItemAllocation) {
					itemAllocated = (ItemAllocation) item;

					if (StringUtils.isNotBlank(itemAllocated.getFulfillmentStore())) {
						storeIds.add(itemAllocated.getFulfillmentStore());
					}
				}

			}
		}

		return storeIds;
	}

	/**
	 * Gets the bopis store ids.
	 *
	 * @param pOrder
	 *            the order
	 * @return the bopis store ids
	 */
	protected Set<String> getBopisStoreIds(Order pOrder) {

		Set<String> bopisStoreIds = new HashSet<String>();
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		if (lOrder.isBopisOrder()) {
			bopisStoreIds.add(lOrder.getBopisStore());
		}

		return bopisStoreIds;
	}

	/**
	 * Get the commerce item from the order given the Commerce Item ID.
	 *
	 * @param pOrder
	 *            ATG Order
	 * @param lCommerceItemId
	 *            Commerce Item Id
	 * @return Commerce item
	 * @throws Exception
	 *             the exception
	 */
	protected MFFCommerceItemImpl getCommerceItem(Order pOrder, String lCommerceItemId) throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		MFFCommerceItemImpl lCommerceItem;
		try {
			lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lCommerceItemId);
		} catch (CommerceItemNotFoundException ex) {
			String lErrorMessage = String.format("Commerce item not found for decline order number: %s Item: %s",
					lOrder.getId(), lCommerceItemId);
			vlogError(ex, lErrorMessage);
			throw new Exception(lErrorMessage);
		} catch (InvalidParameterException ex) {
			String lErrorMessage = String.format("Commerce item not found for decline order number: %s Item: %s",
					lOrder.getId(), lCommerceItemId);
			vlogError(ex, lErrorMessage);
			throw new Exception(lErrorMessage);
		}
		return lCommerceItem;
	}

	/**
	 * Gets the commerce items.
	 *
	 * @param pOrder
	 *            the order
	 * @param lItemAllocations
	 *            the l item allocations
	 * @return the commerce items
	 */
	protected List<CommerceItem> getCommerceItems(Order pOrder, List<String> lItemAllocations) {
		List<CommerceItem> lCommerceItems = new ArrayList<CommerceItem>();
		for (String itemId : lItemAllocations) {
			try {
				lCommerceItems.add(getCommerceItem(pOrder, itemId));
			} catch (Exception ex) {
				String lErrorMessage = String.format("Commerce item not found for decline order number: %s ",
						pOrder.getId());
				vlogError(ex, lErrorMessage);
			}
		}
		return lCommerceItems;
	}

	/**
	 * Gets the all commerce item qty.
	 *
	 * @param pOrder
	 *            the order
	 * @return the all commerce item qty
	 */
	@SuppressWarnings("unchecked")
	protected Long getAllCommerceItemQty(Order pOrder) {
		long pQuantity = 0;
		List<CommerceItem> items = pOrder.getCommerceItems();
		for (CommerceItem item : items) {
			pQuantity += item.getQuantity();
		}
		return pQuantity;
	}

	public static ExecutorService getPool() {
		return pool;
	}

	public static void setPool(ExecutorService pPool) {
		pool = pPool;
	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int pThreadPoolSize) {
		threadPoolSize = pThreadPoolSize;
	}

	public boolean isAsyncEnabled() {
		return asyncEnabled;
	}

	public void setAsyncEnabled(boolean pAsyncEnabled) {
		asyncEnabled = pAsyncEnabled;
	}

	public OrderDashboardRepoHelper getDashboardHelper() {
		return dashboardHelper;
	}

	public void setDashboardHelper(OrderDashboardRepoHelper pDashboardHelper) {
		dashboardHelper = pDashboardHelper;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager pTransactionManager) {
		transactionManager = pTransactionManager;
	}
}
