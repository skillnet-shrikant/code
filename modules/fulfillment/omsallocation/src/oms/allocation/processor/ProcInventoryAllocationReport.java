/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFOrderStates;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;
import oms.commerce.order.MFFOMSOrderManager;

/**
 * 
 * @author Manoj
 * 
 */
public class ProcInventoryAllocationReport extends GenericService implements PipelineProcessor {

	private final static int	SUCCESS	= 1;

	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    vlogDebug("Entering ProcInventoryAllocationReport - runProcess");

    Map lParams = (Map) pPipelineParams;
    Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    List<ItemAllocation> lItemAllocations = (List<ItemAllocation>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS);
    List <String> lItemsToDecline   = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_DECLINE);
    HashMap<String,String> reasonCodeMap = (HashMap<String,String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP);
    List<String> lItemsToCancel = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL);
    HashMap<String,String> itemToPrevStateMap = (HashMap<String,String>) lParams.get(AllocationConstants.ITEM_TO_PREV_STATE_MAP);
    MFFOrderImpl mffOrder = (MFFOrderImpl) lOrder;
    
    boolean isInventoryDeallocate = false;
    List<String> lItemsCancellable = new ArrayList<String>();
    List<MFFCommerceItemImpl> pDeAllocatableItems = new ArrayList<MFFCommerceItemImpl>();

    if(getProcessName().equalsIgnoreCase("CANCELLED") && 
                    !mffOrder.getStateAsString().equalsIgnoreCase(MFFOrderStates.IN_REMORSE) && 
                    lItemsToCancel != null && lItemsToCancel.size() > 0){
        lItemsCancellable.addAll(lItemsToCancel);
    }
    
    if (getProcessName().equalsIgnoreCase("DECLINE") && lItemsToDecline != null) {
      lItemsCancellable.addAll(lItemsToDecline);
    }
   
    if (lItemsCancellable != null && !lItemsCancellable.isEmpty()) {
      isInventoryDeallocate = true;
      for (String itemId : lItemsCancellable) {
        pDeAllocatableItems.add(getCommerceItem(lOrder, itemId));
      }
    }
   
    if(isInventoryDeallocate){
      createInventoryReport(lOrder, pDeAllocatableItems, isInventoryDeallocate,reasonCodeMap,itemToPrevStateMap);
    } else if(lItemAllocations != null){
      createInventoryReport(lOrder, lItemAllocations, isInventoryDeallocate,reasonCodeMap,itemToPrevStateMap);
    }
    
    vlogDebug("Exiting ProcInventoryAllocationReport - runProcess");
    return SUCCESS;
  }
	/*
	 * This method checks if passed store number is a valid integer.
	 */
	
	public boolean isStoreNumValid(String store){
	  if(!StringUtils.isBlank(store)){
	    try{
	      Integer.parseInt(store);
	      return true;
	    }catch(NumberFormatException nfe){
	      logWarning("Invalid store passed, skipping this record for inventory report" + store);
	    }
	  }
	  
	  return false;
	}

  @SuppressWarnings("rawtypes")
  protected void createInventoryReport(Order pOrder, List pItemAllocations, boolean pIsInventoryDeallocate, HashMap<String,String> pReasonCodeMap,
    HashMap<String,String> pItemToPrevStateMap) throws CommerceException {
    
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    vlogDebug ("Add Inventory Allocation Report for Order No: {0} ", lOrder.getId());
    MutableRepositoryItem lMutableRepositoryItem = null;
   
    try {
      MutableRepository mutableRepo = (MutableRepository)getOmsOrderManager().getOmsOrderRepository();
      String skuId = null;
      Integer qty = null;
      String store = null;
      String itemId = null;
     
      for (Object itemAllocated : pItemAllocations) {
        
        if(itemAllocated instanceof ItemAllocation){
          
          ItemAllocation item = (ItemAllocation) itemAllocated;
          store = item.getFulfillmentStore();
          skuId = item.getSkuId();
          qty = (int) item.getQuantity();
          itemId = item.getCommerceItemId();
        } else if (itemAllocated instanceof MFFCommerceItemImpl){
          
          MFFCommerceItemImpl item = (MFFCommerceItemImpl) itemAllocated;
          if(pItemToPrevStateMap != null && pItemToPrevStateMap.size() > 0){
            String prevState = pItemToPrevStateMap.get(item.getId());
            if(!Strings.isNullOrEmpty(prevState) && getCancellableOrderStates() != null && getCancellableOrderStates().contains(prevState)){
              continue;
            }
          }
          store = (item.getFulfillmentStore() == null) ? "-1": item.getFulfillmentStore();
          skuId = item.getCatalogRefId();
          qty = (int) item.getQuantity();
          itemId = item.getId();
        }
        
        if(!isStoreNumValid(store)){
          continue;
        }
        
        lMutableRepositoryItem = mutableRepo.createItem(AllocationConstants.INVENTORY_REPORT_ITEM_DESC);
        
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ALLOCATED_SKUID, skuId);
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ALLOCATED_QUANTITY, qty);

        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_WEB_ORDERNUMBER, 
              lOrder.getPropertyValue(AllocationConstants.PROPERTY_ORDER_NUMBER));
        
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ORDERED_DATETIME, lOrder.getCreationDate());
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ORDERPROCESSED_DATETIME, lOrder.getLastModifiedDate());

        if (lOrder.isBopisOrder()) {
          store = lOrder.getBopisStore();
          lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ALLOCATION_SUBTYPE,
              AllocationConstants.INV_REPORT_ALLOCATION_SUB_TYPE_BOPIS);
        } else {
          
          lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ALLOCATION_SUBTYPE,
              AllocationConstants.INV_REPORT_ALLOCATION_SUB_TYPE_PPS);
        }
        
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ALLOCATED_STORELOCATION, Integer.parseInt(store));
        
        if(pIsInventoryDeallocate){
          lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ALLOCATION_TYPE, 
              AllocationConstants.INV_REPORT_DEALLOCATED);
        } else {
          lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_ALLOCATION_TYPE,
              AllocationConstants.INV_REPORT_ALLOCATED);
        }
        
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_CREATE_DATE, new Date());
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_CREATE_USER, getCreatedByUser());
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_UPDATE_DATE, new Date());
        lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_UPDATE_USER, getCreatedByUser());
        
        if(pReasonCodeMap != null){
          if(!pReasonCodeMap.isEmpty() &&  pReasonCodeMap.keySet().contains(itemId)){
            lMutableRepositoryItem.setPropertyValue(AllocationConstants.INV_REPORT_PROP_REASON_CODE, pReasonCodeMap.get(itemId));
          }
        }
        mutableRepo.addItem(lMutableRepositoryItem);
      }
    } catch (RepositoryException e) {
      String lErrorMessage = String.format("Unable to create inventory allocated record - Order Number: %s ", lOrder.getId());
      vlogError (e, lErrorMessage);
      throw new CommerceException (lErrorMessage, e);
    }
		    
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

	private MFFOMSOrderManager	mOmsOrderManager;
	
	private String createdByUser;
	
	private String mProcessName;
	
	private List<String> mCancellableOrderStates;
	
	public List<String> getCancellableOrderStates() {
    return mCancellableOrderStates;
  }

  public void setCancellableOrderStates(List<String> pCancellableOrderStates) {
    this.mCancellableOrderStates = pCancellableOrderStates;
  }

  public String getProcessName() {
    return mProcessName;
  }

  public void setProcessName(String mProcessName) {
    this.mProcessName = mProcessName;
  }

	public MFFOMSOrderManager getOmsOrderManager() {
		return mOmsOrderManager;
	}

	public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
		this.mOmsOrderManager = pOmsOrderManager;
	}

  public String getCreatedByUser() {
    return createdByUser;
  }

  public void setCreatedByUser(String pCreatedByUser) {
    createdByUser = pCreatedByUser;
  }

}