package com.reports.dashboard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.common.base.Strings;
import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.adapter.gsa.GSARepository;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;
import atg.repository.RepositoryView;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdSpace;

public class OrderDashboardRepoHelper extends GenericService {

	OrderManager mOrderManager;

	private MutableRepository mDashboardRepository;

	private String mTestOrderId;
	private String mTestStoreId;

	public static final String DASHBOARD_ITEM = "reportitem";
	public static final String PERFORMANCE_STATS_IEM = "storePerformanceStats";
	public static final String IDSPACE_STORE_PERF_STATS = "storePerfStats";
	public static final String TIMESTAMP_FORMAT = "yyMMddHHmmssSSS";
	public MutableRepository getDashboardRepository() {
		return mDashboardRepository;
	}

	public void setDashboardRepository(MutableRepository pDashboardRepository) {
		this.mDashboardRepository = pDashboardRepository;
	}

	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	private void addItemToDashboard(DashboardItem mDashboardItem) {

		// Add item to repository
		try {
			getDashboardRepository().addItem(mDashboardItem.getRepositoryItem());
		} catch (RepositoryException e) {
			String lErrorMessage = String.format("Unable to add item to dashboard - Order Number: %s order item: %s",
					mDashboardItem.getOrderId(), mDashboardItem);
			vlogError(e, lErrorMessage);
		}
		vlogDebug("End addLineItem");
	}

	public void updateItemToDashboard(DashboardItem mDashboardItem) {
		vlogDebug("updating dashboard item");
		vlogDebug("updating the  order number: {0} ", mDashboardItem.getOrderId());

		try {
			String orderId = mDashboardItem.getOrderId();
			MutableRepositoryItem lMutableItem = getDashboardRepository().getItemForUpdate(orderId, DASHBOARD_ITEM);
			mDashboardItem.setRepositoryItem(lMutableItem);
			getDashboardRepository().updateItem(lMutableItem);
		} catch (RepositoryException e) {
			String lErrorMessage = String.format("Unable to update item to dashboard - Order Number: %s ",
					mDashboardItem.getOrderId());
			vlogError(e, lErrorMessage);
		}
		vlogDebug("End updating order");

	}

	private MutableRepositoryItem addItemToPerformanceStats(PerformanceStatsItem pPerformanceStatsItem) {
		MutableRepositoryItem addedItem = null;
		// Add item to repository
		try {
			getDashboardRepository().addItem(pPerformanceStatsItem.getRepositoryItem());
			addedItem = pPerformanceStatsItem.getRepositoryItem();
		} catch (RepositoryException e) {
			String lErrorMessage = String.format(
					"Unable to add item to Performance Stats - Store Number: %s performance stats item: %s",
					pPerformanceStatsItem.getStoreId(), pPerformanceStatsItem);
			vlogError(e, lErrorMessage);
		}
		vlogDebug("End addLineItem");
		return addedItem;
	}

	public void updateItemToPerformanceStats(PerformanceStatsItem pPerformanceStatsItem) {
		vlogDebug("updating Performace Stats Item");
		vlogDebug("updating the  Store record: {0} ", pPerformanceStatsItem.getStoreId());

		try {
			String storeId = pPerformanceStatsItem.getStoreId();
			MutableRepositoryItem lMutableItem = getDashboardRepository().getItemForUpdate(storeId,
					PERFORMANCE_STATS_IEM);
			// lMutableItem.setPropertyValue(DashboardConstants.SUBMITTED_DATE,
			// new Date());
			lMutableItem.setPropertyValue(DashboardConstants.ORDERS_ASSIGNED_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.ORDERS_ASSIGNED_COUNT));
			lMutableItem.setPropertyValue(DashboardConstants.BOPIS_ORDERS_ASSIGNED_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.BOPIS_ORDERS_ASSIGNED_COUNT));
			lMutableItem.setPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT));
			lMutableItem.setPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT));
			lMutableItem.setPropertyValue(DashboardConstants.SHIPPED_ORDER_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.SHIPPED_ORDER_COUNT));
			lMutableItem.setPropertyValue(DashboardConstants.SHIPPED_BOPIS_ORDER_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.SHIPPED_BOPIS_ORDER_COUNT));
			lMutableItem.setPropertyValue(DashboardConstants.REJECTED_ORDER_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.REJECTED_ORDER_COUNT));
			lMutableItem.setPropertyValue(DashboardConstants.REJECTED_BOPIS_ORDER_COUNT,
					pPerformanceStatsItem.getPropertyValue(DashboardConstants.REJECTED_BOPIS_ORDER_COUNT));
			getDashboardRepository().updateItem(lMutableItem);
		} catch (RepositoryException e) {
			String lErrorMessage = String.format("Unable to update item to Performance Stats - Store Number: %s ",
					pPerformanceStatsItem.getStoreId());
			vlogError(e, lErrorMessage);
		}
		vlogDebug("End updating order");

	}

	// **** Helper calls for Dash board item****

	/**
	 * This method will be called on order submission to OMS
	 * 
	 * @param pOrder
	 * @param pQuantity
	 */
	public void submitOrderToDashboard(Order pOrder, Long pQuantity) throws RepositoryException {
		MutableRepositoryItem dMutableRepositoryItem = null;

		dMutableRepositoryItem = getDashboardRepository().createItem(DASHBOARD_ITEM);
		DashboardItem dDashboardItem = new DashboardItem();
		dDashboardItem.setRepositoryItem(dMutableRepositoryItem);
		dDashboardItem.setOrderId(pOrder.getId());
		dDashboardItem.setPropertyValue(DashboardConstants.QUANTITY, pQuantity);
		dDashboardItem.setPropertyValue(DashboardConstants.STATUS, "SUBMITTED");
		dDashboardItem.setPropertyValue(DashboardConstants.AMOUNT, pOrder.getPriceInfo().getAmount());
		// **********
		// Gift Amount needs to be confirmed
		// **********
		dDashboardItem.setPropertyValue(DashboardConstants.DISOCOUNT_AMOUNT, pOrder.getPriceInfo().getDiscountAmount());
		dDashboardItem.setPropertyValue(DashboardConstants.SHIPPING_AMOUNT, pOrder.getPriceInfo().getShipping());
		dDashboardItem.setPropertyValue(DashboardConstants.TAX_AMOUNT, pOrder.getPriceInfo().getTax());
		dDashboardItem.setPropertyValue(DashboardConstants.SUBMITTED_DATE, pOrder.getSubmittedDate());
		dDashboardItem.setPropertyValue(DashboardConstants.MODIFIED_DATE, pOrder.getLastModifiedDate());

		addItemToDashboard(dDashboardItem);

	}

	public void updateShippedItemsToDashboard(Order pOrder, List<CommerceItem> pShippedComItems)
			throws RepositoryException {

		String orderId = pOrder.getId();
		double shippedAmount = 0.0d;
		double giftCardAmount = 0.0d;
		long quantity = 0;
		MFFCatalogTools catalogTools = (MFFCatalogTools)getOrderManager().getCatalogTools();
		MutableRepositoryItem lMutableItem = getDashboardRepository().getItemForUpdate(orderId, DASHBOARD_ITEM);
		
		if (null != lMutableItem) {
		  shippedAmount = (Double)lMutableItem.getPropertyValue(DashboardConstants.SHIPPED_AMOUNT);
	    giftCardAmount = (Double)lMutableItem.getPropertyValue(DashboardConstants.GIFT_AMOUNT);
	    quantity = (Long)lMutableItem.getPropertyValue(DashboardConstants.SHIPPED_QUANTITY);
			for (CommerceItem lCommerceItem : pShippedComItems) {
			  String productId = ((MFFCommerceItemImpl)lCommerceItem).getProductId();
			  if(!Strings.isNullOrEmpty(productId) && catalogTools.isGCProduct(productId)){
			    giftCardAmount = giftCardAmount + lCommerceItem.getPriceInfo().getAmount();
			  }else{
			    shippedAmount = shippedAmount + lCommerceItem.getPriceInfo().getAmount(); 
			  }
			  quantity = quantity + lCommerceItem.getQuantity();
			}
			lMutableItem.setPropertyValue(DashboardConstants.GIFT_AMOUNT, giftCardAmount);
			lMutableItem.setPropertyValue(DashboardConstants.SHIPPED_AMOUNT, shippedAmount);
			lMutableItem.setPropertyValue(DashboardConstants.SHIPPED_QUANTITY, quantity);
			lMutableItem.setPropertyValue(DashboardConstants.STATUS, "SHIPPED");
			// getDashboardRepository().updateItem(lMutableItem);
		}

	}

	public void updateCancelledItemsToDashboard(Order pOrder, List<CommerceItem> pCanceledComItems)
			throws RepositoryException {

		String orderId = pOrder.getId();
		double amount = 0.0;
		long pQuantity = 0;
		MutableRepositoryItem lMutableItem = getDashboardRepository().getItemForUpdate(orderId, DASHBOARD_ITEM);
		if (null != lMutableItem) {
			for (CommerceItem lCommerceItem : pCanceledComItems) {
				amount = amount + lCommerceItem.getPriceInfo().getAmount();
				pQuantity = pQuantity + lCommerceItem.getQuantity();
			}
			lMutableItem.setPropertyValue(DashboardConstants.CANCELED_AMOUNT, amount);
			lMutableItem.setPropertyValue(DashboardConstants.CANCELED_QUANTITY, pQuantity);
			lMutableItem.setPropertyValue(DashboardConstants.STATUS, "CANCELED");
			// getDashboardRepository().updateItem(lMutableItem);
		}

	}

	public void updateReturnedItemsToDashboard(Order pOrder, List<CommerceItem> pReturnedComItems)
			throws RepositoryException {

		String orderId = pOrder.getId();
		double amount = 0.0;
		long pQuantity = 0;
		MutableRepositoryItem lMutableItem = getDashboardRepository().getItemForUpdate(orderId, DASHBOARD_ITEM);
		if (null != lMutableItem) {
			for (CommerceItem lCommerceItem : pReturnedComItems) {
				amount = amount + lCommerceItem.getPriceInfo().getAmount();
				pQuantity = pQuantity + lCommerceItem.getQuantity();
			}
			lMutableItem.setPropertyValue(DashboardConstants.RETURNED_AMOUNT, amount);
			lMutableItem.setPropertyValue(DashboardConstants.RETURNED_QUANTITY, pQuantity);
			// lMutableItem.setPropertyValue(DashboardConstants.STATUS,
			// "CANCELED");
			// getDashboardRepository().updateItem(lMutableItem);
		}

	}

	// **** Helper calls for Performance item****

	public void incrementAssignedOrderCount(String storeId) throws RepositoryException {
		Date currentDate = getCurrentDate();

		MutableRepositoryItem[] storeInfo = (MutableRepositoryItem[]) getStoreInfo(storeId, currentDate);
		if (null != storeInfo && storeInfo.length > 0) {
			Long assignedOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.ORDERS_ASSIGNED_COUNT)
					+ 1;
			Long openOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT) + 1;
			storeInfo[0].setPropertyValue(DashboardConstants.ORDERS_ASSIGNED_COUNT, assignedOrderCount);
			storeInfo[0].setPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT, openOrderCount);
			getDashboardRepository().updateItem(storeInfo[0]);
		}

	}

	public void incrementAssignedBopisOrderCount(String storeId) throws RepositoryException {
		Date currentDate = getCurrentDate();

		MutableRepositoryItem[] storeInfo = (MutableRepositoryItem[]) getStoreInfo(storeId, currentDate);
		if (null != storeInfo && storeInfo.length > 0) {
			Long assignedBopisOrderCount = (Long) storeInfo[0]
					.getPropertyValue(DashboardConstants.BOPIS_ORDERS_ASSIGNED_COUNT) + 1;
			Long openBopisOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT)
					+ 1;
			storeInfo[0].setPropertyValue(DashboardConstants.BOPIS_ORDERS_ASSIGNED_COUNT, assignedBopisOrderCount);
			storeInfo[0].setPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT, openBopisOrderCount);
			getDashboardRepository().updateItem(storeInfo[0]);
		}

	}

	public void incrementShippedOrderCount(String storeId) throws RepositoryException {
		Date currentDate = getCurrentDate();

		MutableRepositoryItem[] storeInfo = (MutableRepositoryItem[]) getStoreInfo(storeId, currentDate);
		if (null != storeInfo && storeInfo.length > 0) {
			Long shippedOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.SHIPPED_ORDER_COUNT) + 1;
			Long openOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT);
			if (openOrderCount > 0) {
				openOrderCount = openOrderCount - 1;
			}
			storeInfo[0].setPropertyValue(DashboardConstants.SHIPPED_ORDER_COUNT, shippedOrderCount);
			storeInfo[0].setPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT, openOrderCount);
			getDashboardRepository().updateItem(storeInfo[0]);
		}

	}

	public void incrementShippedBopisOrderCount(String storeId) throws RepositoryException {
		Date currentDate = getCurrentDate();

		MutableRepositoryItem[] storeInfo = (MutableRepositoryItem[]) getStoreInfo(storeId, currentDate);
		if (null != storeInfo && storeInfo.length > 0) {
			Long shippedBopisOrderCount = (Long) storeInfo[0]
					.getPropertyValue(DashboardConstants.SHIPPED_BOPIS_ORDER_COUNT) + 1;
			Long openBopisOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT);
			if (openBopisOrderCount > 0) {
				openBopisOrderCount = openBopisOrderCount - 1;
			}
			storeInfo[0].setPropertyValue(DashboardConstants.SHIPPED_BOPIS_ORDER_COUNT, shippedBopisOrderCount);
			storeInfo[0].setPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT, openBopisOrderCount);
			getDashboardRepository().updateItem(storeInfo[0]);
		}

	}

	public void incrementRejectedOrderCount(String storeId) throws RepositoryException {
		Date currentDate = getCurrentDate();

		MutableRepositoryItem[] storeInfo = (MutableRepositoryItem[]) getStoreInfo(storeId, currentDate);
		if (null != storeInfo && storeInfo.length > 0) {
			Date storeDate = (Date) storeInfo[0].getPropertyValue(DashboardConstants.SUBMITTED_DATE);
			if (storeDate.equals(currentDate)) {
				Long returnOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.REJECTED_ORDER_COUNT)
						+ 1;
				Long openOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT);
				if (openOrderCount > 0) {
					openOrderCount = openOrderCount - 1;
				}
				storeInfo[0].setPropertyValue(DashboardConstants.REJECTED_ORDER_COUNT, returnOrderCount);
				storeInfo[0].setPropertyValue(DashboardConstants.OPEN_ORDERS_COUNT, openOrderCount);
				getDashboardRepository().updateItem(storeInfo[0]);
			}
		}

	}

	public void incrementRejectedBopisOrderCount(String storeId) throws RepositoryException {
		Date currentDate = getCurrentDate();

		MutableRepositoryItem[] storeInfo = (MutableRepositoryItem[]) getStoreInfo(storeId, currentDate);
		if (null != storeInfo && storeInfo.length > 0) {
			Long shippedBopisOrderCount = (Long) storeInfo[0]
					.getPropertyValue(DashboardConstants.REJECTED_BOPIS_ORDER_COUNT) + 1;
			Long openBopisOrderCount = (Long) storeInfo[0].getPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT);
			if (openBopisOrderCount > 0) {
				openBopisOrderCount = openBopisOrderCount - 1;
			}
			storeInfo[0].setPropertyValue(DashboardConstants.REJECTED_BOPIS_ORDER_COUNT, shippedBopisOrderCount);
			storeInfo[0].setPropertyValue(DashboardConstants.BOPIS_OPEN_ORDERS_COUNT, openBopisOrderCount);
			getDashboardRepository().updateItem(storeInfo[0]);
		}

	}

	private MutableRepositoryItem addStoreItemtoPerformance(String pStoreId, Date pCurrentDate)
			throws RepositoryException {
		MutableRepositoryItem dMutableRepositoryItem = null;
		MutableRepositoryItem addedRepositoryItem = null;

		dMutableRepositoryItem = getDashboardRepository().createItem(getUniqueId(), PERFORMANCE_STATS_IEM);
		PerformanceStatsItem dPerformanceStatsItem = new PerformanceStatsItem();
		dPerformanceStatsItem.setRepositoryItem(dMutableRepositoryItem);
		dPerformanceStatsItem.setStoreId(pStoreId);
		dPerformanceStatsItem.setPropertyValue(DashboardConstants.SUBMITTED_DATE, pCurrentDate);
		addedRepositoryItem = addItemToPerformanceStats(dPerformanceStatsItem);

		return addedRepositoryItem;
	}

	private synchronized String getUniqueId() {
		String id = null;

		try {

			GSARepository gsaRepository = (GSARepository) getDashboardRepository();
			IdGenerator idGen = gsaRepository.getIdGenerator();
			String idSpace = null;
			IdSpace objIdSpace = idGen.getIdSpace(IDSPACE_STORE_PERF_STATS);

			if (objIdSpace != null) {
				idSpace = objIdSpace.getName();
				StringBuffer sb = new StringBuffer();
				id = idGen.generateStringId(idSpace);
				sb.append(id);
				sb.append("-");
				String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
				sb.append(timeStamp);
				id = sb.toString();
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		if (id == null) {
			return new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
		}
		return id;
	}

	public MutableRepositoryItem[] getStoreInfo(String storeId, Date currentDate) throws RepositoryException {
		vlogDebug("MFFProrateItemManager Inside getProrateItemByCIAndLineNumber");
		RepositoryItem[] lItems = null;
		MutableRepositoryItem[] mItems = null;
		if (StringUtils.isNotBlank(storeId)) {

			RepositoryView rv = getDashboardRepository().getView(PERFORMANCE_STATS_IEM);
			Query[] q = new Query[2];
			QueryBuilder qb = rv.getQueryBuilder();
			QueryExpression expr1 = qb.createPropertyQueryExpression(DashboardConstants.SUBMITTED_DATE);
			QueryExpression expr2 = qb.createConstantQueryExpression(currentDate);
			q[0] = qb.createComparisonQuery(expr1, expr2, QueryBuilder.EQUALS);
			QueryExpression storeVal1 = qb.createPropertyQueryExpression("storeId");
			QueryExpression storeVal2 = qb.createConstantQueryExpression(storeId);
			q[1] = qb.createComparisonQuery(storeVal1, storeVal2, QueryBuilder.EQUALS);
			Query query = qb.createAndQuery(q);
			lItems = rv.executeQuery(query);
			if (null == lItems || lItems.length < 0) {
				mItems = new MutableRepositoryItem[1];
				mItems[0] = addStoreItemtoPerformance(storeId, currentDate);
			} else {
				mItems = new MutableRepositoryItem[lItems.length];
				int i = 0;
				for (RepositoryItem item : lItems) {
					mItems[i] = RepositoryUtils.getMutableRepositoryItem(item);
					i++;
				}
			}

		}
		return mItems;
	}

	public Date getCurrentDate() {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date today = new Date();
			return formatter.parse(formatter.format(today));
		} catch (ParseException e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		return null;
	}

	public Date getFormattedStoreDate(Date pStoreDate) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return formatter.parse(formatter.format(pStoreDate));
		} catch (ParseException e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		return null;
	}

	private Order getOrderById(String pOrderId) throws CommerceException {
		return getOrderManager().loadOrder(pOrderId);
	}
	// ***************** Test Methods ***************//

	public void testSubmitLoadItems() throws CommerceException {

		try {
			Order order = getOrderById(getTestOrderId());
			submitOrderToDashboard(order, (long) 2);

			// updateCanceledItemsToDashboard(order, order.getCommerceItems());

		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testShippedItems() throws CommerceException {
		try {
			Order order = getOrderById(getTestOrderId());
			// submitOrderToDashboard(order, 2);
			updateShippedItemsToDashboard(order, order.getCommerceItems());
			// updateCanceledItemsToDashboard(order, order.getCommerceItems());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testCanceledItems() throws CommerceException {
		try {
			Order order = getOrderById(getTestOrderId());
			// submitOrderToDashboard(order, 2);
			// updateShippedItemsToDashboard(order, order.getCommerceItems());
			updateCancelledItemsToDashboard(order, order.getCommerceItems());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testReturnedItems() throws CommerceException {
		try {
			Order order = getOrderById(getTestOrderId());
			// submitOrderToDashboard(order, 2);
			// updateShippedItemsToDashboard(order, order.getCommerceItems());
			updateReturnedItemsToDashboard(order, order.getCommerceItems());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testSTPAssignOrdertoStore() throws CommerceException {
		try {
			incrementAssignedOrderCount(getTestStoreId());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testSTPBopisOrdertoStore() throws CommerceException {
		try {
			incrementAssignedBopisOrderCount(getTestStoreId());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testSTPShipOrdertoStore() throws CommerceException {
		try {
			incrementShippedOrderCount(getTestStoreId());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testSTPShipBopisOrdertoStore() throws CommerceException {
		try {
			incrementShippedBopisOrderCount(getTestStoreId());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public void testSTPrejectOrdertoStore() throws CommerceException {
		try {
			incrementRejectedOrderCount(getTestStoreId());
			incrementRejectedBopisOrderCount(getTestStoreId());
		} catch (Exception e) {
			String lErrorMessage = String.format("Unable to create repository item");
			vlogError(e, lErrorMessage);
		}

	}

	public String getTestOrderId() {
		return mTestOrderId;
	}

	public void setTestOrderId(String pTestOrderId) {
		mTestOrderId = pTestOrderId;
	}

	public String getTestStoreId() {
		return mTestStoreId;
	}

	public void setTestStoreId(String pTestStoreId) {
		mTestStoreId = pTestStoreId;
	}

}
