package com.mff.commerce.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.adapter.gsa.ChangeAwareList;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.csr.returns.ReturnException;
import atg.commerce.pricing.PricingTools;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.mff.account.order.bean.MFFOrderBillingInfo;
import com.mff.account.order.bean.MFFOrderDetails;
import com.mff.account.order.bean.MFFOrderItemInfo;
import com.mff.constants.MFFConstants;

public class MFFOrderDetailHelper extends GenericService {
	
	public Repository mLegacyOrderRepository = null;
	public Repository mOmsOrderRepository = null;
	public Repository mReturnRepository;
	private MFFOrderManager mOrderManager;
	private ArrayList mOrderPrefixes;
	private String[] mOrderProperties = null;
	private String[] mShippingGroupProperties = null;
	private String[] mCreditCardProperties = null;
	private String[] mGiftCardProperties = null;
	private String[] mGiftCertProperties = null;
	private String[] mCommerceItemProperties = null;
	private String mQueryByOrderNumber;

	public RepositoryItem getOrderItemById(String pOrderNumber,
			String pIsLegacy) throws CommerceException, RepositoryException {

		RepositoryItem orderItem = null;
		
		if (StringUtils.isNotBlank(pIsLegacy)) {
			if (Boolean.parseBoolean(pIsLegacy)) {
				orderItem = getLegacyOrderRepository().getItem(pOrderNumber, MFFConstants.ORDER);
			} else {
				orderItem = getOmsOrderRepository().getItem(pOrderNumber, MFFConstants.ORDER);
			}
		} else {
			
			orderItem = getOmsOrderRepository().getItem(pOrderNumber, MFFConstants.ORDER);
			
			if (orderItem == null){
				orderItem = getLegacyOrderRepository().getItem(pOrderNumber, MFFConstants.ORDER);
			}
		}

		return orderItem;
	}
	
	public RepositoryItem getOrderItemForTracking(String pOrderNumber, boolean pIsRegularOrder)
			throws CommerceException, RepositoryException {
		
		vlogDebug ("getOrderItemForTracking(): isRegularOrder: " + pIsRegularOrder);
		
		RepositoryItem orderItem = null;
		if (pIsRegularOrder){
			orderItem = this.getOrderIdByOrderNumber(pOrderNumber);
		} else {
			orderItem = getLegacyOrderRepository().getItem(pOrderNumber, MFFConstants.ORDER);
		}
		
		vlogDebug ("getOrderItemForTracking(): orderItem: " + orderItem);
		
		return orderItem;
	}
	
	public RepositoryItem getOrderIdByOrderNumber(String pOrderNumber) throws RepositoryException {
		
		RepositoryItem orderItem = null;
		try {
			RepositoryView orderView = getOmsOrderRepository().getView(MFFConstants.ORDER);
			RqlStatement statement = RqlStatement.parseRqlStatement(getQueryByOrderNumber());
			
			Object params[] = new Object[1];
			params[0] = pOrderNumber;
			
			RepositoryItem[] orderItems = statement.executeQuery(orderView, params);
			vlogDebug("getOrderIdByOrderNumber(): orderItems: " + orderItems);

			if (orderItems != null && orderItems.length > 0) {
				orderItem = orderItems[0];
			}

		} catch (RepositoryException re) {
			if (isLoggingError()) {
				logError("Exception while getting stores: " + re, re);
			}
		}
		return orderItem;
	  }

	public String getProfileIdForOrder(RepositoryItem pOrderItem)
			throws CommerceException, RepositoryException {

		return (String) pOrderItem.getPropertyValue("profileId");
	}

	public void fillOrderDetailsByOrderId(RepositoryItem pOrderItem,
			MFFOrderDetails pMffOrderDetailObj) throws CommerceException,
			RepositoryException {

		fillOrderHeaderDetails(pOrderItem, pMffOrderDetailObj);
		fillShippingDetails(pOrderItem, pMffOrderDetailObj);
		fillBillingDetails(pOrderItem, pMffOrderDetailObj);
		fillCommerceItemDetails(pOrderItem, pMffOrderDetailObj);

	}

	private void fillCommerceItemDetails(RepositoryItem pOrderItem,
			MFFOrderDetails pMffOrderDetailObj) {

		List commerceItems = (List) pOrderItem.getPropertyValue(MFFConstants.PROP_COMMERCE_ITEMS);
		
		String[] ciProperties = this.getCommerceItemProperties();
		
		Iterator iter = commerceItems.iterator();
		ArrayList<MFFOrderItemInfo> commerceItemInfos = new ArrayList<MFFOrderItemInfo>();
		
		while (iter.hasNext()) {
			
			MutableRepositoryItem commerceItem = (MutableRepositoryItem) iter.next();
			MFFOrderItemInfo commerceItemInfo = new MFFOrderItemInfo();
			
			for (int j = 0; j < ciProperties.length; j++) {
				
				vlogDebug("fillCommerceItemDetails(): " + ciProperties[j]);
				
				if (ciProperties[j].contains(".")) {
					
					String[] props = ciProperties[j].split("\\.");
					int propLength = props.length;
	
					RepositoryItem tmpItem = (RepositoryItem) commerceItem.getPropertyValue(props[0]);
	
					vlogDebug("fillCommerceItemDetails(): Prop name: " + props[propLength - 1]);
	
					if (tmpItem != null) {
						Object value = tmpItem.getPropertyValue(props[propLength - 1]);
	
						try {
	
							vlogDebug("fillCommerceItemDetails(): load property[" + props[propLength - 1] + " : " + value);
	
							DynamicBeans.setPropertyValue(commerceItemInfo, props[propLength - 1], value);
	
						} catch (PropertyNotFoundException e) {
							logWarning("fillCommerceItemDetails(): property not found for : " + props[propLength - 1]);
						}
					}
				} else {
					Object value = commerceItem.getPropertyValue(ciProperties[j]);
					try {
	
						vlogDebug("fillCommerceItemDetails(): load property[" + ciProperties[j] + " : " + value);
						DynamicBeans.setPropertyValue(commerceItemInfo, ciProperties[j], value);
					} catch (PropertyNotFoundException e) {
						logWarning("fillCommerceItemDetails(): property not found for : " + ciProperties[j]);
					}
				}
			}
			
			double totalLinePrice = 0.0;
			double listPrice = 0.0;
			double salePrice = 0.0;
			
			Long quantity = (Long)commerceItem.getPropertyValue("quantity");
			RepositoryItem priceInfo = (RepositoryItem)commerceItem.getPropertyValue("priceInfo");
			
			if (priceInfo != null) {
				Double listPriceObj = (Double)priceInfo.getPropertyValue("listPrice");
				Double salePriceObj = (Double)priceInfo.getPropertyValue("salePrice");
				
				if (listPriceObj!=null){
					listPrice = listPriceObj.doubleValue();
				}
				if (salePriceObj!=null){
					salePrice = salePriceObj.doubleValue();
				}
			}
			double priceToUse = listPrice;
			
			if (salePrice > 0 && salePrice < listPrice) {
				priceToUse = salePrice;
			}
			totalLinePrice = priceToUse * quantity;
			// 2414 - Price display changes
			if(commerceItem.getPropertyValue("gwp")!= null && (Boolean)commerceItem.getPropertyValue("gwp")) {
				totalLinePrice = (Double)priceInfo.getPropertyValue("amount");
			}
			commerceItemInfo.setLineItemTotal(totalLinePrice);
			commerceItemInfos.add(commerceItemInfo);
		}
		
		pMffOrderDetailObj.setCommerceItems(commerceItemInfos);
	}

	private void fillBillingDetails(RepositoryItem pOrderItem, MFFOrderDetails pMffOrderDetailObj) {
		
		List paymentGroups = (List) pOrderItem.getPropertyValue("paymentGroups");
		ArrayList<MFFOrderBillingInfo> pgInfos = new ArrayList<MFFOrderBillingInfo>();
		for (Iterator iter = paymentGroups.iterator(); iter.hasNext(); ) {
			
			RepositoryItem paymentGroupItem = (RepositoryItem) iter.next();
		
			String paymentMethod = (String)paymentGroupItem.getPropertyValue("paymentMethod");
			vlogDebug("fillBillingDetails(): paymentMethod: " + paymentMethod);
			
			String[] paymentGroupProperties = null;
			if ("creditCard".equals(paymentMethod)) {
				paymentGroupProperties = this.getCreditCardProperties();
			} else if ("giftCard".equals(paymentMethod)) {
				paymentGroupProperties = this.getGiftCardProperties();
			} else if ("giftCertificate".equals(paymentMethod)) {
				paymentGroupProperties = this.getGiftCertProperties();
			}
			
			MFFOrderBillingInfo billingInfoItem = new MFFOrderBillingInfo();
	
			if (paymentGroupProperties != null) {
				for (int j = 0; j < paymentGroupProperties.length; j++) {

					vlogDebug("fillBillingDetails(): "
							+ paymentGroupProperties[j]);

					Object value = paymentGroupItem
							.getPropertyValue(paymentGroupProperties[j]);
					try {

						vlogDebug("fillBillingDetails(): load property["
								+ paymentGroupProperties[j] + " : " + value);

						DynamicBeans.setPropertyValue(billingInfoItem,
								paymentGroupProperties[j], value);
					} catch (PropertyNotFoundException e) {
						logWarning("fillBillingDetails(): property not found for : "
								+ paymentGroupProperties[j]);
					}
				}
				pgInfos.add(billingInfoItem);
			}
		}

		pMffOrderDetailObj.setPaymentGroups(pgInfos);
	}

	private void fillShippingDetails(RepositoryItem pOrderItem,
			MFFOrderDetails pMffOrderDetailObj) {

		String[] shipGroupProperties = this.getShippingGroupProperties();

		ChangeAwareList shippingGrpsList = (ChangeAwareList) pOrderItem
				.getPropertyValue("shippingGroups");
		RepositoryItem shippingGroupItem = (RepositoryItem) shippingGrpsList
				.get(0);
		ContactInfo contactInfoItem = new ContactInfo();

		for (int j = 0; j < shipGroupProperties.length; j++) {

			vlogDebug("fillShippingDetails(): " + shipGroupProperties[j]);
			Object value = null;
			if (shipGroupProperties[j].equalsIgnoreCase("state")){
				value = shippingGroupItem.getPropertyValue("stateAddress");
			} else {
				value = shippingGroupItem.getPropertyValue(shipGroupProperties[j]);
			}
			
			try {

				vlogDebug("fillShippingDetails(): load property["
						+ shipGroupProperties[j] + " : " + value);

				DynamicBeans.setPropertyValue(contactInfoItem,
						shipGroupProperties[j], value);
			} catch (PropertyNotFoundException e) {
				logWarning("fillShippingDetails(): property not found for : "
						+ shipGroupProperties[j]);
			}
		}
		
		Map<String, String> specialInstructions = (Map<String, String>) shippingGroupItem
				.getPropertyValue("specialInstructions");
		
		if(specialInstructions != null && specialInstructions.size() > 0){
			String attention = specialInstructions.get("instructions");
			if (StringUtils.isNotBlank(attention)){
				pMffOrderDetailObj.setAttention(attention);
			}
		}
		
		String shippingMethod = (String) shippingGroupItem.getPropertyValue("shippingMethod");
		vlogDebug("fillShippingDetails(): shippingMethod: " + shippingMethod);
		String trackingNumber = (String) shippingGroupItem.getPropertyValue("trackingNumber");
		vlogDebug("fillShippingDetails(): trackingNumber: " + trackingNumber);
		boolean isSaturdayDelivery = (boolean) shippingGroupItem.getPropertyValue("isSaturdayDelivery");
		vlogDebug("fillShippingDetails(): isSaturdayDelivery: " + isSaturdayDelivery);
		
		pMffOrderDetailObj.setShippingMethod(shippingMethod);
		pMffOrderDetailObj.setIsSaturdayDelivery(isSaturdayDelivery);
		pMffOrderDetailObj.setTrackingNumber(trackingNumber);
		pMffOrderDetailObj.setShippingInfo(contactInfoItem);

	}

	@SuppressWarnings("unchecked")
	public void fillOrderHeaderDetails(RepositoryItem pOrderItem,
			MFFOrderDetails pMffOrderDetailObj) throws CommerceException,
			RepositoryException {

		String[] orderProperties = this.getOrderProperties();

		for (int j = 0; j < orderProperties.length; j++) {
			vlogDebug(orderProperties[j]);
			if (orderProperties[j].contains(".")) {
				
				String[] props = orderProperties[j].split("\\.");
				int propLength = props.length;

				RepositoryItem tmpItem = (RepositoryItem) pOrderItem
						.getPropertyValue(props[0]);

				vlogDebug("fillOrderHeaderDetails(): Prop name: " + props[propLength - 1]);

				if (tmpItem != null) {
					Object value = tmpItem
							.getPropertyValue(props[propLength - 1]);

					try {

						vlogDebug("fillOrderHeaderDetails(): load property[" + props[propLength - 1] + " : " + value);

						DynamicBeans.setPropertyValue(pMffOrderDetailObj,
								props[propLength - 1], value);

					} catch (PropertyNotFoundException e) {
						logWarning("fillOrderHeaderDetails(): property not found for : " + props[propLength - 1]);
					}
				}
			} else {
				Object value = pOrderItem.getPropertyValue(orderProperties[j]);
				try {

					vlogDebug("fillOrderHeaderDetails(): load property[" + orderProperties[j] + " : " + value);

					DynamicBeans.setPropertyValue(pMffOrderDetailObj,
							orderProperties[j], value);
				} catch (PropertyNotFoundException e) {
					logWarning("fillOrderHeaderDetails(): property not found for : " + orderProperties[j]);
				}
			}
		}

		double amount = (double) ((RepositoryItem)pOrderItem.getPropertyValue("priceInfo")).getPropertyValue("amount");
		double shipping = (double) ((RepositoryItem)pOrderItem.getPropertyValue("priceInfo")).getPropertyValue("shipping");
		double tax = (double) ((RepositoryItem)pOrderItem.getPropertyValue("priceInfo")).getPropertyValue("tax");
		
		double orderTotal= PricingTools.getPricingTools().round(amount + shipping + tax);
		
		vlogDebug("fillOrderHeaderDetails(): orderTotal: " + orderTotal);
		
		/*double rawSubtotal = (double) ((RepositoryItem)pOrderItem.getPropertyValue("priceInfo")).getPropertyValue("rawSubtotal");
		double manualAdjustmentTotal = (double) ((RepositoryItem)pOrderItem.getPropertyValue("priceInfo")).getPropertyValue("manualAdjustmentTotal");*/
		
		double discountAmount = getOrderManager().getTotalDiscountAmount(pOrderItem);
		vlogDebug("fillOrderHeaderDetails(): discountAmount: " + discountAmount);
		
		if (discountAmount == 0) {
			pMffOrderDetailObj.setDiscountAmount(Double.valueOf("-0.0") * -1);
		} else {
			pMffOrderDetailObj.setDiscountAmount(discountAmount * -1);
		}
		
		
		pMffOrderDetailObj.setOrderTotal(orderTotal);
		pMffOrderDetailObj.setMerchandiseTotal(getMerchandiseTotal(pOrderItem));
		pMffOrderDetailObj.setGiftCardPaymentTotal(getGiftCardPaymentTotal(pOrderItem));
		
		double orderChargeAmount = orderTotal - getGiftCardPaymentTotal(pOrderItem);
		vlogDebug("fillOrderHeaderDetails(): orderChargeAmount: " + orderChargeAmount);
		pMffOrderDetailObj.setOrderChargeAmount(orderChargeAmount);
	}
	
	/**
	 * This method gives Order total without any discounts, tax and shipping charge. 
	 * @param pOrderItem
	 * @return double
	 */
	private double getMerchandiseTotal(RepositoryItem pOrderItem) {

		double merchandiseTotal = 0.0d;
		double totalLinePrice = 0.0d;

		List commerceItems = (List) pOrderItem.getPropertyValue(MFFConstants.PROP_COMMERCE_ITEMS);

		if (commerceItems != null && commerceItems.size() > 0) {
			
			Iterator ciIter = commerceItems.iterator();
			while (ciIter.hasNext()) {
				RepositoryItem commerceItem = (RepositoryItem) ciIter.next();
				
				if (commerceItem != null) {
					Long quantity = (Long)commerceItem.getPropertyValue("quantity");
					RepositoryItem priceInfo = (RepositoryItem)commerceItem.getPropertyValue("priceInfo");
					
					double listPrice = 0.0;
					double salePrice = 0.0;
						
					if (priceInfo != null) {
						Double listPriceObj = (Double)priceInfo.getPropertyValue("listPrice");
						Double salePriceObj = (Double)priceInfo.getPropertyValue("salePrice");
						
						if (listPriceObj!=null){
							listPrice = listPriceObj.doubleValue();
						}
						if (salePriceObj!=null){
							salePrice = salePriceObj.doubleValue();
						}
					}

					double priceToUse = listPrice;
						
					if (salePrice > 0 && salePrice < listPrice) {
						priceToUse = salePrice;
					}
					totalLinePrice = priceToUse * quantity;
					// 2414 - Price display changes
					if((Boolean)commerceItem.getPropertyValue("gwp")) {
						totalLinePrice = (Double)priceInfo.getPropertyValue("amount");
					}
					merchandiseTotal = merchandiseTotal + totalLinePrice;
						
					if (isLoggingDebug()) {
						logDebug("CommerceItem:" + commerceItem.getRepositoryId() + ", listPrice:" + listPrice + ", salePrice:"
								+ salePrice);
						logDebug("priceToUse:" + priceToUse);
						logDebug("totalLinePrice:" + totalLinePrice);
						
					}
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("merchandiseTotal:" + merchandiseTotal);
		}
		return merchandiseTotal;
	}
	
	private double getGiftCardPaymentTotal(RepositoryItem pOrderItem) {

		List paymentGrpsList = (List) pOrderItem.getPropertyValue("paymentGroups");
		
		Double giftCardPaymentAmount = 0.0;
		
		for (Object paymentGroupItem : paymentGrpsList) {
			RepositoryItem pgItem = (RepositoryItem)paymentGroupItem;
			String paymentMethod = (String)pgItem.getPropertyValue("paymentMethod");
			vlogDebug("fillShippingDetails(): paymentMethod: " + paymentMethod);
				if ("giftCertificate".equals(paymentMethod) || "giftCard".equals(paymentMethod)){
					giftCardPaymentAmount += (double)pgItem.getPropertyValue("amount");;
			}
		}
		return giftCardPaymentAmount;
	}
	
	public List getReturnRequestsByOrderId(String pOrderId) throws ReturnException {
		
		if (StringUtils.isEmpty(pOrderId)) {
			return null;
		} else {

			try {
				RepositoryView re = getReturnRepository().getItemDescriptor("returnRequest").getRepositoryView();
				QueryBuilder msg1 = re.getQueryBuilder();
				QueryExpression propertyExpression = msg1.createPropertyQueryExpression("orderId");
				QueryExpression constantExpression = msg1.createConstantQueryExpression(pOrderId);
				Query query = msg1.createComparisonQuery(propertyExpression, constantExpression, 4);
				
				RepositoryItem[] returnRequests = re.executeQuery(query);
				if (returnRequests == null) {
					if (this.isLoggingDebug()) {
						this.logDebug("getReturnRequestsByOrderId: no return requests found");
					}

					return null;
				} else {
					return Arrays.asList(returnRequests);
				}
			} catch (RepositoryException re) {
				vlogError(re, "RepositoryException while looking up returns.");
			}
		}
		return null;
	}

	public Repository getOmsOrderRepository() {
		return mOmsOrderRepository;
	}

	public void setOmsOrderRepository(Repository pOmsOrderRepository) {
		mOmsOrderRepository = pOmsOrderRepository;
	}

	public String[] getOrderProperties() {
		return this.mOrderProperties;
	}

	public void setOrderProperties(String[] pOrderProperties) {
		this.mOrderProperties = pOrderProperties;
	}

	public Repository getLegacyOrderRepository() {
		return mLegacyOrderRepository;
	}

	public void setLegacyOrderRepository(Repository pLegacyOrderRepository) {
		mLegacyOrderRepository = pLegacyOrderRepository;
	}

	public String[] getShippingGroupProperties() {
		return mShippingGroupProperties;
	}

	public void setShippingGroupProperties(String[] pShippingGroupProperties) {
		mShippingGroupProperties = pShippingGroupProperties;
	}

	public String[] getCommerceItemProperties() {
		return mCommerceItemProperties;
	}

	public void setCommerceItemProperties(String[] pCommerceItemProperties) {
		mCommerceItemProperties = pCommerceItemProperties;
	}

	public String[] getCreditCardProperties() {
		return mCreditCardProperties;
	}

	public void setCreditCardProperties(String[] pCreditCardProperties) {
		mCreditCardProperties = pCreditCardProperties;
	}

	public String[] getGiftCardProperties() {
		return mGiftCardProperties;
	}

	public void setGiftCardProperties(String[] pGiftCardProperties) {
		mGiftCardProperties = pGiftCardProperties;
	}

	public String[] getGiftCertProperties() {
		return mGiftCertProperties;
	}

	public void setGiftCertProperties(String[] pGiftCertProperties) {
		mGiftCertProperties = pGiftCertProperties;
	}

	public MFFOrderManager getOrderManager() {
		return mOrderManager;
	}

	public void setOrderManager(MFFOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	public ArrayList getOrderPrefixes() {
		return mOrderPrefixes;
	}

	public void setOrderPrefixes(ArrayList pOrderPrefixes) {
		mOrderPrefixes = pOrderPrefixes;
	}

	public String getQueryByOrderNumber() {
		return mQueryByOrderNumber;
	}

	public void setQueryByOrderNumber(String pQueryByOrderNumber) {
		mQueryByOrderNumber = pQueryByOrderNumber;
	}

	public Repository getReturnRepository() {
		return mReturnRepository;
	}

	public void setReturnRepository(Repository pReturnRepository) {
		mReturnRepository = pReturnRepository;
	}
}
