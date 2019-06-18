/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;

import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.states.OMSOrderStates;
import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.promotion.GWPMarkerManager;
import atg.commerce.states.StateDefinitions;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor gets the submitted order from the orderRepository, clones it
 * and stores in the omsOrderRepository.
 * 
 * @author KnowledgePath Solutions Inc.
 */
public class ProcAddToOMSOrderRepository extends ApplicationLoggingImpl implements PipelineProcessor {

	/**
	 * Default Constructor
	 */
	public ProcAddToOMSOrderRepository() {
		super();
	}

	/*
	 * ATG OOTB fulfillment ResourceBundle
	 */
	static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";
	private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

	private final int SUCCESS = 1;
	private OMSOrderManager omsOrderManager;
	private SimpleOrderManager orderManager;
	private Repository orderRepository;


	
	private GWPMarkerManager gwpMarkerManager;
	
	// property: LoggingIdentifier
	String mLoggingIdentifier = "ProcAddToOMSOrderRepository";

	/**
	 * Sets property LoggingIdentifier
	 **/
	public void setLoggingIdentifier(String pLoggingIdentifier) {
		mLoggingIdentifier = pLoggingIdentifier;
	}

	/**
	 * Returns property LoggingIdentifier
	 **/
	public String getLoggingIdentifier() {
		return mLoggingIdentifier;
	}

	/**
	 * @return the omsOrderManager
	 */
	public OMSOrderManager getOmsOrderManager() {
		return omsOrderManager;
	}

	/**
	 * @param omsOrderManager
	 *            the omsOrderManager to set
	 */
	public void setOmsOrderManager(OMSOrderManager omsOrderManager) {
		this.omsOrderManager = omsOrderManager;
	}

	/**
	 * @return the orderManager
	 */
	public SimpleOrderManager getOrderManager() {
		return orderManager;
	}

	/**
	 * @param orderManager
	 *            the orderManager to set
	 */
	public void setOrderManager(SimpleOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	/**
	 * @return the orderRepository
	 */
	public Repository getOrderRepository() {
		return orderRepository;
	}

	/**
	 * @param orderRepository
	 *            the orderRepository to set
	 */
	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
	 */
	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object,
	 * atg.service.pipeline.PipelineResult)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		HashMap map = (HashMap) pParam;
		Order pOrder = (Order) map.get(PipelineConstants.ORDER);
		if (isLoggingDebug()) {
			logDebug("Order with Id:" + pOrder.getId() + " is being Added to OMS Order Repository");
		}
		if (pOrder == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
		}
		MutableRepository omsOrderRepository = (MutableRepository) getOmsOrderManager().getOrderTools().getOrderRepository();
		RepositoryItem originalItem = getOrderRepository().getItem(pOrder.getId(), "order");
		if (isLoggingDebug()) {
			logDebug("cloning item");
		}
		RepositoryItem clonedItem = RepositoryUtils.cloneItem(originalItem, true, null, getOmsOrderManager().getOrderExclusionsMap(), omsOrderRepository, originalItem.getRepositoryId());
		if (isLoggingDebug()) {
			logDebug("clonedItem's id::" + clonedItem.getRepositoryId());
			logDebug("loading order");
		}
		Order clonedOrder = getOmsOrderManager().loadOrder(clonedItem.getRepositoryId());
		if (isLoggingDebug()) {
			logDebug("adding order to OMSRepository");
		}
		updateGWPMarkers(clonedOrder);
		getOmsOrderManager().addOrder(clonedOrder);
		// copy attributes manually which were not not cloned
		// forexample pricing adjustments
		performRemManualCopying(pOrder, clonedOrder);

		// update oms order
		getOmsOrderManager().updateOrder(clonedOrder);

		// change the core order's state
		synchronized (pOrder) {
			pOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.SENT_TO_OMS));
			// update the core order
			getOrderManager().updateOrder(pOrder);
		}

		// Pass on OMS order in the pipeline
		map.put(OMSOrderConstants.PIPELINE_OMS_ORDER, clonedOrder);

		return SUCCESS;
	}

	/**
	 * This function iterates through all the adjusments in the standard order
	 * and copies pricing model to the adjustments in oms order which we were
	 * not able to clone.
	 * 
	 * @param srcOrder
	 * @param destOrder
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void performRemManualCopying(Order srcOrder, Order destOrder) throws Exception {
		if (isLoggingDebug()) {
			logDebug("inside performRemManualCopying");
		}
		// copy the manual adjustment total which is not copied in ATG OOTB
		// cloning.
		srcOrder.getPriceInfo().setManualAdjustmentTotal(destOrder.getPriceInfo().getManualAdjustmentTotal());
		// copy order's priceInfo adjustments's pricingModels
		copyMissingAdjs(srcOrder.getPriceInfo().getAdjustments(), destOrder.getPriceInfo().getAdjustments());
		// copy order's taxPriceInfo adjustment's pricingModels
		copyMissingAdjs(srcOrder.getTaxPriceInfo().getAdjustments(), destOrder.getTaxPriceInfo().getAdjustments());
		if (isLoggingDebug()) {
			logDebug(" Now copying Item's adjustments");
		}
		List srcItems = srcOrder.getCommerceItems();
		List destItems = destOrder.getCommerceItems();
		Iterator it = srcItems.iterator();
		int i = 0;
		while (it.hasNext()) {
			CommerceItemImpl srcItem = (CommerceItemImpl) it.next();
			CommerceItemImpl destItem = (CommerceItemImpl) destItems.get(i);
			
			copyMissingAdjs(srcItem.getPriceInfo().getAdjustments(), destItem.getPriceInfo().getAdjustments());
			//computeGwpProperties(srcOrder, destOrder,srcItem,destItem);

			i++;
		}
		if (isLoggingDebug()) {
			logDebug("copying shippingGroup's adjustments");
		}
		List srcShipGroups = srcOrder.getShippingGroups();
		List destShipGroups = destOrder.getShippingGroups();
		Iterator iter = srcShipGroups.iterator();
		int x = 0;
		while (iter.hasNext()) {
			ShippingGroup srcGroup = (ShippingGroup) iter.next();
			ShippingGroup destGroup = (ShippingGroup) destShipGroups.get(x);
			copyMissingAdjs(srcGroup.getPriceInfo().getAdjustments(), destGroup.getPriceInfo().getAdjustments());
			x++;
		}
	}

	public void computeGwpProperties(Order pSrcOrder, Order pDestOrder, CommerceItemImpl pSrcItem, CommerceItemImpl pDestItem) {
		MFFCommerceItemImpl srcItem = (MFFCommerceItemImpl)pSrcItem;

	}

	/**
	 * This function copy the pricing model from one adjustment to another
	 * 
	 * @param srcAdjs
	 * @param destAdjs
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void copyMissingAdjs(List srcAdjs, List destAdjs) throws Exception {
		if (isLoggingDebug()) {
			logDebug("inside copyingModel");
			logDebug("size of srcAdjs is:::" + srcAdjs.size());
			logDebug("size of destAdjs is:::" + srcAdjs.size());
		}
		Iterator iter = srcAdjs.iterator();
		int i = 0;
		while (iter.hasNext()) {
			PricingAdjustment srcAdjust = (PricingAdjustment) iter.next();
			PricingAdjustment destAdjust = (PricingAdjustment) destAdjs.get(i);
			if (srcAdjust.getAdjustmentDescription().equals(destAdjust.getAdjustmentDescription())) {
				if (isLoggingDebug()) {
					logDebug("adjustment description is the same so copy the pricingModel");
					logDebug("And the description is:::" + srcAdjust.getAdjustmentDescription());
				}
				destAdjust.setPricingModel(srcAdjust.getPricingModel());
				destAdjust.setManualPricingAdjustment(srcAdjust.getManualPricingAdjustment());
			}
			i++;
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void updateGWPMarkers (Order destOrder) {
		vlogDebug("Entering updateGWPMarkers - destOrder");
		List items = destOrder.getCommerceItems();
		Iterator itemIterator = items.iterator();
		while (itemIterator.hasNext()) {
			CommerceItemImpl itemImpl = (CommerceItemImpl) itemIterator.next();
			boolean isGWP = (boolean) itemImpl.getPropertyValue(getGwpMarkerManager().getGwpProperty());
			vlogDebug("GWP flage set to {0}", isGWP);
			
			if (isGWP) {
				updateGWPMarkers(itemImpl);
			}
		}
		vlogDebug("Exiting updateGWPMarkers - destOrder");
	}

	@SuppressWarnings("rawtypes")
	protected void updateGWPMarkers(CommerceItemImpl pDestItem) {
		vlogDebug("Entering updateGWPMarkers - destItem");
		
		//The gwp marker on the dest item should be updated with the new commerce item
		Collection markers = (Collection) pDestItem.getPropertyValue(getGwpMarkerManager().getItemMarkerProperty()); 
		
		if (markers != null) {
			Iterator markerIterator = markers.iterator();
			while (markerIterator.hasNext()) {
				Object markerItem = (Object) markerIterator.next();
				if (markerItem instanceof MutableRepositoryItem) {
					MutableRepositoryItem repositoryItem = (MutableRepositoryItem) markerItem;
					repositoryItem.setPropertyValue("owner", pDestItem.getId());
				} else {
					vlogWarning("Could not cast the marker to repository item to update the commerce item. Dest commerce item id set to {0}", pDestItem.getId());
				}
			}
		} else {
			vlogWarning("Could not determine the markers for the commerce item {0}", pDestItem.getId());
		}
		vlogDebug("Exiting updateGWPMarkers - destItem");
	}



	public GWPMarkerManager getGwpMarkerManager() {
		return gwpMarkerManager;
	}

	public void setGwpMarkerManager(GWPMarkerManager gwpMarkerManager) {
		this.gwpMarkerManager = gwpMarkerManager;
	}

}