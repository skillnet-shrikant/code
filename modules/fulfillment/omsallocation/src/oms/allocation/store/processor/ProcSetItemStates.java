/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.store.processor;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.MFFItemPriceInfo;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.commerce.states.MFFShippingGroupStates;
import com.mff.locator.StoreLocatorTools;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.states.StateDefinitions;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import mff.util.DateUtil;
import oms.allocation.item.AllocationConstants;
import oms.allocation.store.StoreAllocationManager;
import oms.commerce.order.MFFOMSOrderManager;

/**
 * This pipeline process will update the states on the shipping group and
 * commerce item for items that have been shipped, cancelled or declined. The
 * process will also update the inventory for each of the affect items in the
 * order.
 * 
 * @author DMI
 * 
 */
public class ProcSetItemStates extends GenericService implements PipelineProcessor {

	private final static int	SUCCESS	= 1;
	private StoreAllocationManager storeAllocationManager; // StoreAllocationManager
	private StoreLocatorTools storeLocatorTools;
	
	
	public StoreLocatorTools getStoreLocatorTools() {
		return storeLocatorTools;
	}

	public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
		storeLocatorTools = pStoreLocatorTools;
	}

	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
		vlogDebug("Entering ProcSetItemStates - runProcess");

		Map lParams = (Map) pPipelineParams;
		Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		List<String> lItemsToShip = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
		List<String> lItemsToCancel = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL);
		List<String> lItemsToDecline = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_DECLINE);
		List<String> lItemsToPickup = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_PICKUP);
		HashMap<String,String> trackingNumberMap = (HashMap<String,String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_TRACKING_NO);
	  HashMap<String,String> reasonCodeMap = (HashMap<String,String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP);
	  String signature = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_SIGNATURE);
	  String dateOfBirth = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_DATE_OF_BIRTH);
		//String cancelReasonCode = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_CANCEL_REASON_CODE);
		String cancelDescription = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC);
		String pickUpInstructions = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_SPECIAL_INST);

		vlogDebug("Process Name : {0}", getProcessName());
		// Process Shipments
		if (getProcessName().equalsIgnoreCase("SHIPPED"))
			processShipments(lOrder, lItemsToShip, trackingNumberMap,signature,dateOfBirth);

		// Process Cancels
		if (getProcessName().equalsIgnoreCase("CANCELLED"))
			processCancels(lOrder, lItemsToCancel, cancelDescription,reasonCodeMap,lParams);

		// Process Declines
		if (getProcessName().equalsIgnoreCase("DECLINE"))
			processDeclines(lOrder, lItemsToDecline,reasonCodeMap);
		
		// Process ReadyForPickUp
		if (getProcessName().equalsIgnoreCase("PICKUP"))
      processReadyForPickup(lOrder, lItemsToPickup,lParams);

    if (getProcessName().equalsIgnoreCase("PENDINGACTIVATION"))
      processPendingActivation(lOrder, lItemsToShip);
		
		// Set state on shipping groups
		setShippingGroupStates(lOrder,pickUpInstructions);

		// Update order
		getOmsOrderManager().updateOrder(lOrder);

		vlogDebug("Exiting ProcSetItemStates - runProcess");
		return SUCCESS;
	}

	/**
	 * Process the shipment messages by incrementing the ship count for the
	 * inventory and setting the status of the commerce item.
	 * 
	 * @param pOrder
	 *            ATG Order
	 * @param pItemsToShip
	 *            List of commerce item IDs to ship
	 * @throws Exception
	 */
	protected void processShipments(Order pOrder, List<String> pItemsToShip, HashMap<String,String> pTrackingNumberMap,String pSignature,String pDateOfBirth) throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		vlogDebug("Begin process the items to ship for order number {0}", lOrder.getId());

		// Bail if there is nothing to process
		if (pItemsToShip == null || pItemsToShip.size() < 1) {
			vlogDebug("No items to ship were found for order number {0}", lOrder.getId());
			return;
		}
		
		if (lOrder.isBopisOrder()){
		  if(!Strings.isNullOrEmpty(pSignature)){
		    lOrder.setBopisSignature(pSignature);
		    lOrder.setBopisReadyForPickupDate(new Date());
		  }
		  
		  if(!Strings.isNullOrEmpty(pDateOfBirth)){
		    try{
		      Date dob = DateUtil.convertStringToDate("MM/dd/yyyy",pDateOfBirth);
		      lOrder.setDateOfBirth(dob);
		    }catch(ParseException e){
		      logWarning("Cannot set date of birth for order - " + lOrder.getOrderNumber());
		    }
		  }
    }

		for (String lCommerceItemId : pItemsToShip) {
			MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, lCommerceItemId);
			
			boolean isGiftCardItem = getOmsOrderManager().isGiftCardItem(lCommerceItem);
      //Only update inventory for non gift card items
      if(!isGiftCardItem){
       
       // skip inventory update for force allocated items
       if(!lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.FORCED_ALLOCATION)  && 
           !lCommerceItem.getDropShip()){
         // Increment the store shipped in the inventory table
    		 try {
    		   
    		    String lSkuId = lCommerceItem.getCatalogRefId();
    			  long lQuantity = lCommerceItem.getQuantity();
    				String lFulfillmentStore = lCommerceItem.getFulfillmentStore();
    				
    				if(!getStoreLocatorTools().isBOPISOnlyStore(lFulfillmentStore)) {
    					getInventoryManager().incrementStoreShipped(lSkuId, lFulfillmentStore, lQuantity);
    				} else {
    					getInventoryManager().incrementBopisOnlyStoreShipped(lSkuId, lFulfillmentStore, lQuantity, true);
    				}
    				
    			} catch (InventoryException ex) {
    				String lErrorMessage = String.format("Unable to update the inventory for ship order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
    				vlogError(ex, lErrorMessage);
    				throw new Exception(lErrorMessage);
    			}
       }
  		 
       // Set Commerce item state
       lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.SHIPPED));
       lCommerceItem.setShipDate(new Date());
      
       if (pTrackingNumberMap != null && pTrackingNumberMap.get(lCommerceItemId) != null)
         lCommerceItem.setTrackingNumber((String)pTrackingNumberMap.get(lCommerceItemId));
       }
      
       if(isGiftCardItem){
        
          boolean isGwp = lCommerceItem.isGwp();
          // Commerce item is a gift card
          if (lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_GC_ACTIVATION) || isGwp)
          {
            lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.SHIPPED));
            lCommerceItem.setShipDate(new Date());
          }
       }
		}
		vlogDebug("End processing the items to ship for order number {0}", lOrder.getId());
	}

	/**
	 * Process the cancel messages by decrementing the allocation count for the
	 * inventory and setting the status of the commerce item.
	 * 
	 * @param pOrder
	 *            ATG Order
	 * @param pItemsToCancel
	 *            List of commerce item IDs to cancel
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
  protected void processCancels(Order pOrder, List<String> pItemsToCancel, String cancelDescription,HashMap<String,String> pReasonCodeMap,Map lParams) throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		vlogDebug("Begin process the items to cancel for order number {0}", lOrder.getId());
		double totalPriceOfItemsToBeCancelled=0;
		HashMap<String,String> itemToPrevStateMap = new HashMap<String,String>();
		// Bail if there is nothing to process
		if (pItemsToCancel == null || pItemsToCancel.size() < 1) {
			vlogDebug("No items to cancel were found for order number {0}", lOrder.getId());
			return;
		}
		else {
			lParams.put(AllocationConstants.IS_PARTIAL_CANCELLATION, true);
		}
			
		for (String lCommerceItemId : pItemsToCancel) {
			MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, lCommerceItemId);
			
			if(!getOmsOrderManager().isGiftCardItem(lCommerceItem)){
  			// Decrement the store allocated in the inventory table
  			try {
  			  String lSkuId = lCommerceItem.getCatalogRefId();
  				long lQuantity = lCommerceItem.getQuantity();
  				String lFulfillmentStore = lCommerceItem.getFulfillmentStore();
  				
  				if(lOrder.isBopisOrder() || 
  				      (lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.SENT_TO_STORE) && !Strings.isNullOrEmpty(lFulfillmentStore))){
  				  getInventoryManager().decrementStoreAllocatedForBopis(lSkuId, lFulfillmentStore, lQuantity);
  				}else{
  				  getInventoryManager().increaseStockLevel(lSkuId, lQuantity); 
  				}
  				
  				// Call to handle damaged reasonCode
          if (pReasonCodeMap != null && pReasonCodeMap.get(lCommerceItemId) != null){
            String reasonCode = (String)pReasonCodeMap.get(lCommerceItemId);
            if(getOmsOrderManager().getDamagedReasonCodes().contains(reasonCode)){
            	try {
            		getInventoryManager().setStoreDamaged(lSkuId, lFulfillmentStore, true);
            	} catch (InventoryException ie) {
    				String lErrorMessage = String.format("Unable to update the inventory for cancel order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
      				vlogError(ie, lErrorMessage);
            	}
            }
          }
  				
  			} catch (InventoryException ex) {
  				String lErrorMessage = String.format("Unable to update the inventory for cancel order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
  				vlogError(ex, lErrorMessage);
  				throw new Exception(lErrorMessage);
  			}
			}
			// create map of itemId with commerceItemState
			itemToPrevStateMap.put(lCommerceItemId, lCommerceItem.getStateAsString());
			
			// Set Commerce item state
			lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.CANCELLED));
			lCommerceItem.setCancelDate(new Date());
			if (!StringUtils.isBlank(cancelDescription)) {
			  StringBuffer sb = new StringBuffer();
			  sb.append(cancelDescription);
			  if (pReasonCodeMap != null && pReasonCodeMap.get(lCommerceItemId) != null){
			    sb.append(" Reason Code - ");
			    sb.append(pReasonCodeMap.get(lCommerceItemId));
			  }
				lCommerceItem.setCancelDescription(sb.toString());
			}
			double shippingPrice=lCommerceItem.getShipping();
			double shippingTax=lCommerceItem.getShippingTax();
			TaxPriceInfo itemTaxPriceInfo=lCommerceItem.getTaxPriceInfo();
			double itemTax = itemTaxPriceInfo != null ? itemTaxPriceInfo.getAmount() : 0.0d;
			ItemPriceInfo itemPriceInfo = lCommerceItem.getPriceInfo();
			double itemRawTotal = itemPriceInfo.getAmount();
			if(((MFFItemPriceInfo)itemPriceInfo).getEffectivePrice() > 0.0) {
				itemRawTotal = ((MFFItemPriceInfo)itemPriceInfo).getEffectivePrice();
			}
			double orderDiscountShare=itemPriceInfo != null ? itemPriceInfo.getOrderDiscountShare() : 0.0d;
			double totalItemPrice = shippingPrice+shippingTax+itemTax+itemRawTotal-orderDiscountShare;
			totalPriceOfItemsToBeCancelled = totalPriceOfItemsToBeCancelled+totalItemPrice;
		}
		lParams.put(AllocationConstants.TOTAL_CANCELLATION_PRICE,totalPriceOfItemsToBeCancelled);
		lParams.put(AllocationConstants.ITEM_TO_PREV_STATE_MAP,itemToPrevStateMap);
		vlogDebug("End processing the items to cancel for order number {0}", lOrder.getId());
	}

	/**
	 * Process the decline messages by decrementing the allocation count for the
	 * inventory and setting the status of the commerce item.
	 * 
	 * @param pOrder
	 *            ATG Order
	 * @param pItemsToDecline
	 *            List of commerce item IDs to decline
	 * @throws Exception
	 */
	protected void processDeclines(Order pOrder, List<String> pItemsToDecline,HashMap<String,String> pReasonCodeMap) throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		vlogDebug("Begin process the items to decline for order number {0}", lOrder.getId());

		// Bail if there is nothing to process
		if (pItemsToDecline == null || pItemsToDecline.size() < 1) {
			vlogDebug("No items to decline were found for order number {0}", lOrder.getId());
			return;
		}

		for (String lCommerceItemId : pItemsToDecline) {
			MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, lCommerceItemId);
			
			if(!getOmsOrderManager().isGiftCardItem(lCommerceItem)){
  			// Decrement the store allocated in the inventory table
  			try {
  				String lSkuId = lCommerceItem.getCatalogRefId();
  				long lQuantity = lCommerceItem.getQuantity();
  				String lFulfillmentStore = lCommerceItem.getFulfillmentStore();
  				
  				getInventoryManager().decrementStoreAllocated(lSkuId, lFulfillmentStore, lQuantity);
  				
  				// Call to handle damaged reasonCode
  				if (pReasonCodeMap != null && pReasonCodeMap.get(lCommerceItemId) != null){
    				  String reasonCode = (String)pReasonCodeMap.get(lCommerceItemId);
              if(getOmsOrderManager().getDamagedReasonCodes().contains(reasonCode)){
            	  try {
            		  getInventoryManager().setStoreDamaged(lSkuId, lFulfillmentStore, true);
            	  } catch (InventoryException ie) {
        				String lErrorMessage = String.format("Unable to update the inventory for cancel order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
          				vlogError(ie, lErrorMessage);
            	  }
              }
              StringBuffer sb = new StringBuffer();
              if(!Strings.isNullOrEmpty(lCommerceItem.getRejectionReasonCodes()) ){
                sb.append(lCommerceItem.getRejectionReasonCodes());
                sb.append(";");
                sb.append(lFulfillmentStore);
                sb.append("-");
                sb.append(reasonCode);
                lCommerceItem.setRejectionReasonCodes(sb.toString());
              }else{
                sb.append(lFulfillmentStore);
                sb.append("-");
                sb.append(reasonCode);
                lCommerceItem.setRejectionReasonCodes(sb.toString());
              }
  	      }
  			} catch (InventoryException ex) {
  				String lErrorMessage = String.format("Unable to update the inventory for cancel order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
  				vlogError(ex, lErrorMessage);
  				throw new Exception(lErrorMessage);
  			}
			}
			// Set Commerce item state
			
			if(lCommerceItem.getPreviousAllocation().size()>=getStoreAllocationManager().getMaxAllocationCount())
			{
				lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.FORCED_ALLOCATION));
				vlogDebug("Reached max allocations of {0}, item set to forced allocation ", getStoreAllocationManager().getMaxAllocationCount());
			}
			else
			{
				lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_ALLOCATION));
				vlogDebug("Number of previous allocations {0}", lCommerceItem.getPreviousAllocation().size());
			}
		}
		vlogDebug("End processing the items to cancel for order number {0}", lOrder.getId());
	}
	
	/**
   * Process the readyForPickup messages by setting the status of the commerce item.
   * 
   * @param pOrder
   *            ATG Order
   * @param pItemsToPickup
   *            List of commerce item IDs to pickup
   * @throws Exception
   */
	@SuppressWarnings("rawtypes")
  protected void processReadyForPickup(Order pOrder, List<String> pItemsToPickup,Map lParams) throws Exception {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    vlogDebug("Begin process the items to pickup for order number {0}", lOrder.getId());
    
    // Bail if it's not BOPIS order
    if (!lOrder.isBopisOrder()) {
      vlogDebug("Not a bopis order {0} so skip processing", lOrder.getId());
      return;
    }
    
    // Bail if there is nothing to process
    if (pItemsToPickup == null || pItemsToPickup.size() < 1) {
      vlogDebug("No items to pick were found for order number {0}", lOrder.getId());
      return;
    }

    for (String lCommerceItemId : pItemsToPickup) {
      MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, lCommerceItemId);
      lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.READY_FOR_PICKUP));
    }
    
    String comments = (String) lParams.get(AllocationConstants.PIPELINE_PARAMETER_COMMENTS);
    if(!Strings.isNullOrEmpty(comments)){
      getOmsOrderManager().createOrderComment(pOrder,comments);
    }
    
    lOrder.setBopisReadyForPickupDate(new Date());
    vlogDebug("End processing the items for pickup for order number {0}", lOrder.getId());
  }

  /**
   * Process the gift cards in pending fulfillment by 
   * setting the status of the commerce item.
   * 
   * @param pOrder
   *            ATG Order
   * @param pItemsToShip
   *            List of commerce item IDs
   * @throws Exception
   */
  protected void processPendingActivation(Order pOrder, List<String> pItemsToShip) throws Exception {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    vlogDebug("Begin process the items to pending activation for order number {0}", lOrder.getId());

    // Bail if there is nothing to process
    if (pItemsToShip == null || pItemsToShip.size() < 1) {
      vlogDebug("No items to change to pending activation were found for order number {0}", lOrder.getId());
      return;
    }

    for (String lCommerceItemId : pItemsToShip) {
      MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, lCommerceItemId);
      
      boolean isGiftCardItem = getOmsOrderManager().isGiftCardItem(lCommerceItem);
      //Only update inventory for non gift card items
      if(isGiftCardItem){
        // Commerce item is a gift card
        if (lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_GC_FULFILLMENT) ||
            lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.ERROR_GC_ACTIVATION))
        {
          lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_GC_ACTIVATION));
          lCommerceItem.setShipDate(new Date());
        }
      }
    }
    vlogDebug("End processing the items to pending activation for order number {0}", lOrder.getId());
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
	 */
	protected MFFCommerceItemImpl getCommerceItem(Order pOrder, String lCommerceItemId) throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		MFFCommerceItemImpl lCommerceItem;
		try {
			lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lCommerceItemId);
		} catch (CommerceItemNotFoundException ex) {
			String lErrorMessage = String.format("Commerce item not found for decline order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
			vlogError(ex, lErrorMessage);
			throw new Exception(lErrorMessage);
		} catch (InvalidParameterException ex) {
			String lErrorMessage = String.format("Commerce item not found for decline order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
			vlogError(ex, lErrorMessage);
			throw new Exception(lErrorMessage);
		}
		return lCommerceItem;
	}

	/**
	 * Set the state for the shipping groups based on the state of the
	 * underlying commerce items.
	 * 
	 * @param pOrder
	 *            ATG Order
	 */
	@SuppressWarnings("unchecked")
	protected void setShippingGroupStates(Order pOrder,String pPickUpInstructions) {
	  MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
		List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
		for (ShippingGroup lShippingGroup : lShippingGroups) {
			boolean lShipped = false;
			boolean lCancelled = false;
			boolean lOther = false;
			String trackingNumber = "";
			List<CommerceItemRelationship> lRelationships = lShippingGroup.getCommerceItemRelationships();
			for (CommerceItemRelationship lRelationship : lRelationships) {
				MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) lRelationship.getCommerceItem();
				if (lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.SHIPPED)){
					lShipped = true;
					trackingNumber = lCommerceItem.getTrackingNumber();
				}else if (lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.CANCELLED)){
					lCancelled = true;
				}else{
					lOther = true;
				}
			}
			if (lShipped && !lOther) {
				lShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.SHIPPED));
				lShippingGroup.setActualShipDate(new Date());
				((MFFHardgoodShippingGroup)lShippingGroup).setTrackingNumber(trackingNumber);
			} else if (lCancelled && !lOther){
				lShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.CANCELLED));
			}
			
			if(lOrder.isBopisOrder() && !Strings.isNullOrEmpty(pPickUpInstructions) && 
			    getProcessName().equalsIgnoreCase("PICKUP")){

			  ShippingGroupImpl sgImpl = ((ShippingGroupImpl)lShippingGroup);
			  HashMap<String,Object> specialInstructions =  new HashMap<String,Object>();
			  if(sgImpl.getSpecialInstructions() != null && sgImpl.getSpecialInstructions().size() > 0){
			    Set<String> keys = sgImpl.getSpecialInstructions().keySet();
			    for(String mapKey : keys){
			      Object value = sgImpl.getSpecialInstructions().get(mapKey);
			      specialInstructions.put(mapKey,value);
			    }
          specialInstructions.put("pickupinstructions", pPickUpInstructions);
          sgImpl.setSpecialInstructions(specialInstructions);
			  }else{
	        specialInstructions.put("pickupinstructions", pPickUpInstructions);
	        sgImpl.setSpecialInstructions(specialInstructions);
			  }
			}
		}
	}

	MFFOMSOrderManager	mOmsOrderManager;

	public MFFOMSOrderManager getOmsOrderManager() {
		return mOmsOrderManager;
	}

	public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
		this.mOmsOrderManager = pOmsOrderManager;
	}

	private FFRepositoryInventoryManager	mInventoryManager;

	public FFRepositoryInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
		this.mInventoryManager = pInventoryManager;
	}

	String	mProcessName;

	public String getProcessName() {
		return mProcessName;
	}

	public void setProcessName(String mProcessName) {
		this.mProcessName = mProcessName;
	}

	public StoreAllocationManager getStoreAllocationManager() {
		return storeAllocationManager;
	}

	public void setStoreAllocationManager(StoreAllocationManager storeAllocationManager) {
		this.storeAllocationManager = storeAllocationManager;
	}

}