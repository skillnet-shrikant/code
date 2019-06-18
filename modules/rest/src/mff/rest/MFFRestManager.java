package mff.rest;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.Period;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.planogram.util.GetPlanogramInfoUtil;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import mff.MFFException;
import oms.allocation.item.StoreAllocationItem;
import oms.allocation.store.StoreAllocationManager;
import oms.allocation.store.StoreAllocationStates;

/**
 *
 * @author vsingh
 *
 */
public class MFFRestManager extends GenericService{
	
	private StoreAllocationManager mStoreAllocationManager;
	private GetPlanogramInfoUtil mGetPlanogramInfoUtil;
	private MFFCatalogTools mCatalogTools;
	private int numberofRecordsPerPage = 10;
	
	/**
	 * Looks up allocation records based on storeId and creates response object
	 * 
	 * @param pStoreId
	 * @param pRequestedPageNumber
	 * @return
	 * @throws CommerceException
	 */
	public StoreOrderResponse getStoreOrdersListByStoreId(String pStoreId, String pState, String pNumberOfDays, int pRequestedPageNumber, int pNumberOfRecordsPerPage) throws CommerceException{
		
		StoreOrderResponse storeOrdersList = new StoreOrderResponse();
		
		List<StoreOrder> storeOrders = new ArrayList<StoreOrder>();
		HashMap<String,StoreOrder> orderIdToStoreMap = new HashMap<String,StoreOrder>();
		HashMap<String,Boolean> orderIdToInPickingMap = new HashMap<String,Boolean>();
		BigDecimal totalPages = BigDecimal.ZERO;
		int numberOfRecordsPerPage = pNumberOfRecordsPerPage;
		int lowEnd = 0;
		int highEnd = 0;
		int currentPage = pRequestedPageNumber;
		//int totalRowsFromResultSet = 0;
		
	  int totalRecords = 0;
	  if(pState.equalsIgnoreCase(StoreAllocationStates.PRE_SHIP)){
	    totalRecords = getStoreAllocationManager().getPreShippedItemsByStoreId(pStoreId);
    }else  if(pState.equalsIgnoreCase(StoreAllocationStates.READY_FOR_PICKUP)){
      totalRecords = getStoreAllocationManager().getReadyForPickupItemsByStoreId(pStoreId);
    }else if(pState.equalsIgnoreCase(StoreAllocationStates.SHIPPED)){
      totalRecords = getStoreAllocationManager().getTotalShippedRecordsByStoreId(pStoreId, pState, pNumberOfDays);
    }
		
		vlogDebug("MFFRestManager : getStoreOrdersListByStoreId - pState {0} totalRecords {1}",pState,totalRecords);
		
		if(totalRecords > 0){
			totalPages = (new BigDecimal(totalRecords)).divide((new BigDecimal(numberOfRecordsPerPage)), 0,BigDecimal.ROUND_UP);
		
			vlogDebug("MFFRestManager : getStoreOrdersListByStoreId - totalPages {0}",totalPages);
			
			if(pRequestedPageNumber == 1 || pRequestedPageNumber > totalPages.intValue()){
				highEnd = numberOfRecordsPerPage;
				currentPage = 1;
			} else	if(pRequestedPageNumber <= totalPages.intValue()){
				highEnd =  (pRequestedPageNumber * numberOfRecordsPerPage);
				lowEnd = highEnd - numberOfRecordsPerPage;
			}
			
			vlogDebug("MFFRestManager : getStoreOrdersListByStoreId - storeId {0}, lowEnd {1}, highEnd {2}",pStoreId,lowEnd,highEnd);
			List<StoreAllocationItem> storeAllocations = new ArrayList<StoreAllocationItem>();
			if(pState.equalsIgnoreCase(StoreAllocationStates.PRE_SHIP) || pState.equalsIgnoreCase(StoreAllocationStates.READY_FOR_PICKUP)){
			  storeAllocations = getStoreAllocationManager().getAllocationByStoreAndStatusSql(pStoreId, pState, lowEnd, highEnd);
			}else if(pState.equalsIgnoreCase(StoreAllocationStates.SHIPPED)){
			  storeAllocations = getStoreAllocationManager().getShippedAllocations(pStoreId, pState, pNumberOfDays, lowEnd, highEnd);
			}
			
			for(StoreAllocationItem storeAllocation : storeAllocations){
				StoreOrder storeOrder = null;
				String orderId = storeAllocation.getOrderId();
				boolean fulfillmentSplitOrder = isFulfillmentSplit(orderId);
				
				if(orderIdToStoreMap != null && orderIdToStoreMap.get(orderId) != null){
					storeOrder = orderIdToStoreMap.get(orderId);
					StoreOrderItem storeOrderItem = createStoreOrderItem(storeAllocation);
          storeOrder.getItems().add(storeOrderItem);
          if(orderIdToInPickingMap != null && orderIdToInPickingMap.get(orderId) != null){
            Boolean inPicking = orderIdToInPickingMap.get(orderId);
            // if inPicking is false, don't do anything
            if(inPicking){
              orderIdToInPickingMap.put(orderId, storeOrderItem.isInPicking());
              storeOrder.setInPicking(storeOrderItem.isInPicking());
            }
          }else{
            orderIdToInPickingMap.put(orderId, storeOrderItem.isInPicking());
            storeOrder.setInPicking(storeOrderItem.isInPicking());
          }
          	if(getGetPlanogramInfoUtil()!=null){
          		storeOrder.setShowStoreItemLocation(getGetPlanogramInfoUtil().isShowItemLocationInfo());
          	}
				}else{
					storeOrder = new StoreOrder();
					storeOrder.setFulfillmentSplit(fulfillmentSplitOrder);
					storeOrder.setOrderId(storeAllocation.getOrderId());
					storeOrder.setOrderNumber(storeAllocation.getOrderNumber());
					storeOrder.setShippingMethod(storeAllocation.getShippingMethod());
					storeOrder.setBopisOrder(storeAllocation.isBopisOrder());
					storeOrder.setFirstName(storeAllocation.getFirstName());
					storeOrder.setLastName(storeAllocation.getLastName());
					storeOrder.setAddress1(storeAllocation.getAddress1());
					storeOrder.setAddress2(storeAllocation.getAddress2());
					storeOrder.setCity(storeAllocation.getCity());
					storeOrder.setShipState(storeAllocation.getShipState());
					storeOrder.setPostalCode(storeAllocation.getPostalCode());
					storeOrder.setCountry(storeAllocation.getCountry());
					storeOrder.setCounty(storeAllocation.getCounty());
					storeOrder.setPhoneNumber(storeAllocation.getPhoneNumber());
					storeOrder.setContactEmail(storeAllocation.getContactEmail());
					storeOrder.setOrderSubmittedDate(storeAllocation.getOrderSubmittedDate());
					storeOrder.setOrderAge(getOrderAge(storeAllocation.getOrderId(),storeAllocation.getOrderSubmittedDate()));
					storeOrder.setPickUpInstructions(storeAllocation.getPickUpInstructions());
					StoreOrderItem storeOrderItem = createStoreOrderItem(storeAllocation);
					storeOrder.getItems().add(storeOrderItem);
					if(getGetPlanogramInfoUtil()!=null){
		          		storeOrder.setShowStoreItemLocation(getGetPlanogramInfoUtil().isShowItemLocationInfo());
		          	}
					storeOrders.add(storeOrder);
					if(orderIdToInPickingMap != null && orderIdToInPickingMap.get(orderId) != null){
            Boolean inPicking = orderIdToInPickingMap.get(orderId);
            // if inPicking is false, don't do anything
            if(inPicking){
              orderIdToInPickingMap.put(orderId, storeOrderItem.isInPicking());
              storeOrder.setInPicking(storeOrderItem.isInPicking());
            }
          }else{
            orderIdToInPickingMap.put(orderId, storeOrderItem.isInPicking());
            storeOrder.setInPicking(storeOrderItem.isInPicking());
          }
					
				}
				
				//totalRowsFromResultSet++;
				orderIdToStoreMap.put(orderId, storeOrder);
			}
		
		}
			
		storeOrdersList.setTotalNumbeOfPages(totalPages.intValue());
		storeOrdersList.setNumberofRecordsPerPage(getNumberofRecordsPerPage());
		storeOrdersList.setTotalRecords(totalRecords);
		storeOrdersList.setCurrentPageNumber(currentPage);
		storeOrdersList.setRequestPageNumber(pRequestedPageNumber);
		
		storeOrdersList.setStoreOrder(storeOrders);
			
		
		
		return storeOrdersList;
	}
	
	private String getOrderAge(String pOrderId, Date pOrderSubmittedDate){
	  
	  StringBuffer sb = new StringBuffer();
	  SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	  Date currentDate = new Date();
	  vlogDebug("getOrderAge : orderId - {0} Current Date - {1} Order Date - {2}",pOrderId, formatter.format(currentDate),pOrderSubmittedDate);
	  
    
	  Period p = new Period(pOrderSubmittedDate.getTime(),currentDate.getTime());
	  int years=p.getYears();
	  int months=p.getMonths();
	  int days=(p.getWeeks()*7)+p.getDays();
	  int hours=p.getHours();
	  int minutes=p.getMinutes();
	  int seconds=p.getSeconds();
	  
	  if(years>0){
		  sb.append(years);
		  sb.append(" Years ");
		  if(months>0){
			  sb.append(months);
			  sb.append(" Months ");
		  }
		  vlogDebug("Order Age - {0} -- Years calculator",sb.toString());
		  return sb.toString();
	  }
	  else if(months>0){
		  sb.append(months);
		  sb.append(" Months ");
		  if(days>0){
			  sb.append(days);
			  sb.append(" Days ");
		  }
		  vlogDebug("Order Age - {0} -- Months calculator",sb.toString());
		  return sb.toString();
	  }
	  else if(days>0){
		  if(days>2){
			  sb.append(days);
			  sb.append(" Days ");
			  vlogDebug("Order Age - {0} -- Days calculator for days >2",sb.toString());
			  return sb.toString();
		  }

		  else {
			  int secondsToMinutes=seconds/60;
			  int currentMinutes=secondsToMinutes+minutes;
			  int minutesToHours=currentMinutes/60;
			  int reminderMinutes=currentMinutes%60;
			  int currentHours=hours+minutesToHours;
			  int daysToHours=days*24;
			  int displayHours=currentHours+daysToHours;
			  if(displayHours>48){
				  int converHoursToDays=displayHours/24;
				  int reminderHours=displayHours%24;
				  sb.append(converHoursToDays);
				  sb.append(" Days ");
				  vlogDebug("Order Age - {0} -- Days calculator for hours >48",sb.toString());
				  return sb.toString();
			  }
			  else {
				  sb.append(displayHours);
				  sb.append(" Hours ");
				  if(reminderMinutes>0){
					  sb.append(reminderMinutes);
					  sb.append(" Minutes ");
				  }
				  vlogDebug("Order Age - {0} -- Days calculator for hours < 48",sb.toString());
				  return sb.toString();
			  }
		  }
	  }
	  
	  else if(hours>0){
		  int secondsToMinutes=seconds/60;
		  int currentMinutes=secondsToMinutes+minutes;
		  int minutesToHours=currentMinutes/60;
		  int reminderMinutes=currentMinutes%60;
		  int currentHours=hours+minutesToHours;
		  int daysToHours=days*24;
		  int displayHours=currentHours+daysToHours;
		  if(displayHours>48){
			  int converHoursToDays=displayHours/24;
			  int reminderHours=displayHours%24;
			  sb.append(converHoursToDays);
			  sb.append(" Days ");
			  vlogDebug("Order Age - {0} -- Hours calculator for hours >48",sb.toString());
			  return sb.toString();
		  }
		  else {
			  sb.append(displayHours);
			  sb.append(" Hours ");
			  if(reminderMinutes>0){
				  sb.append(reminderMinutes);
				  sb.append(" Minutes ");
			  }
			  vlogDebug("Order Age - {0} -- Hours calculator for hours <48",sb.toString());
			  return sb.toString();
		  }
	  }
	  else if(minutes>0){
		  int secondsToMinutes=seconds/60;
		  int currentMinutes=secondsToMinutes+minutes;
		  int minutesToHours=currentMinutes/60;
		  int reminderMinutes=currentMinutes%60;
		  int displayHours=hours+minutesToHours;
		  if(displayHours>1){
			  sb.append(displayHours);
			  sb.append(" Hours ");
			  if(reminderMinutes>0){
				  sb.append(reminderMinutes);
				  sb.append(" Minutes ");
			  }
			  vlogDebug("Order Age - {0} -- Minutes calculator for hours >1",sb.toString());
			  return sb.toString();
		  }
		  else {
			  sb.append(reminderMinutes);
			  sb.append(" Minutes ");
			  vlogDebug("Order Age - {0} -- Minutes calculator for hours <1",sb.toString());
			  return sb.toString();
		  }
	  }
	  else {
		  return "< 1 Minute";
	  }
	
}
	
	/**
	 * Creates StoreOrderItem
	 * 
	 * @param pStoreAllocationItem
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
  private StoreOrderItem createStoreOrderItem(StoreAllocationItem pStoreAllocation ){
		
		StoreOrderItem storeOrderItem = new StoreOrderItem();
		String storeId=pStoreAllocation.getStoreId();
		String skuId=pStoreAllocation.getSkuId();
		storeOrderItem.setStoreId(pStoreAllocation.getStoreId());
		storeOrderItem.setSkuId(pStoreAllocation.getSkuId());
		storeOrderItem.setQuantity(pStoreAllocation.getQuantity());
		storeOrderItem.setCommerceItemId(pStoreAllocation.getCommerceItemId());
		storeOrderItem.setAllocationDate(pStoreAllocation.getAllocationDate());
		storeOrderItem.setInPicking(pStoreAllocation.isInPicking());
		try {
      RepositoryItem sku = getCatalogTools().findSKU(pStoreAllocation.getSkuId());
      if(sku != null){
        Map<String,String> skuAttributes = ((Map<String,String>)sku.getPropertyValue(MFFConstants.SKU_SKU_ATTRIBUTES));
        if(skuAttributes != null && skuAttributes.size() > 0){
          for (Map.Entry<String, String> lSkuAtrrMap : skuAttributes.entrySet()) {
            storeOrderItem.getSkuAttributes().put(lSkuAtrrMap.getKey(), lSkuAtrrMap.getValue());
          }
        }
        storeOrderItem.setUpcs((String)sku.getPropertyValue(MFFConstants.SKU_UPCS));
        storeOrderItem.setVpn((String)sku.getPropertyValue(MFFConstants.SKU_VPN));
        storeOrderItem.setPpsMessage((String)sku.getPropertyValue(MFFConstants.SKU_PPS_MESSAGE));
        RepositoryItem product = getCatalogTools().getParentProductOfSku(sku);
        if(product != null){
          String description = (String)product.getPropertyValue("description");
          storeOrderItem.setProductDescription(description);
        }
    	if(getGetPlanogramInfoUtil()!=null) {
    		 try {
    			 String planoGramLocationInfo=getGetPlanogramInfoUtil().getPlanogramLocationInfo(skuId, storeId);
    			 storeOrderItem.setPlanoGramInfo(planoGramLocationInfo);
    		 }catch(MFFException ex){
    	        	vlogError(ex,"Error adding planogram info for sku {0}",pStoreAllocation.getSkuId());
    	     }
    	}
    }
 
      
      
     
    } catch (RepositoryException e) {
      vlogError(e,"Error looking up sku {0}",pStoreAllocation.getSkuId());
    }

		
		
		return storeOrderItem;
		
	}
	
	
	
	private boolean isFulfillmentSplit(String pOrderId){
	  
	  int count = getStoreAllocationManager().getFulfillmentStoreCountByOrderId(pOrderId);
	  if(count > 1){
	    return true;
	  }
	  
	  return false;
	}
	
	/**
	 * Create list of StoreOrder from allocation items
	 * 
	 * @param pStoreId
	 * @return
	 * @throws CommerceException
	 */
	/*public List<StoreOrder> getOrdersByStoreId(String pStoreId) throws CommerceException{
		
		List<StoreOrder> storeOrdersList = new ArrayList<>();
		HashMap<String,StoreOrder> orderIdToStoreMap = new HashMap<String,StoreOrder>();
		
		RepositoryItem[] items = getStoreAllocationManager().
				getAllocationByStoreAndStatus(pStoreId, StoreAllocationStates.PRE_SHIP);
		
		if(items != null){
			for(RepositoryItem item : items){
				StoreOrder storeOrder = null;
				String orderId = (String)item.getPropertyValue(AllocationConstants.PROPERTY_ORDER_ID);
				if(orderIdToStoreMap != null && orderIdToStoreMap.get(orderId) != null){
					storeOrder = orderIdToStoreMap.get(orderId);
					storeOrder.getItems().add(createStoreOrderItem(item));
				}else{
					storeOrder = new StoreOrder();
					storeOrder.setOrderNumber(orderId);
					storeOrder.setShippingMethod((String) item.getPropertyValue(AllocationConstants.PROPERTY_SHIPPING_METHOD));
					storeOrder.getItems().add(createStoreOrderItem(item));
					
					storeOrdersList.add(storeOrder);
				}
				
				orderIdToStoreMap.put(orderId, storeOrder);
			}
			
		}
		
		return storeOrdersList;
	}*/
	
	public StoreAllocationManager getStoreAllocationManager() {
		return mStoreAllocationManager;
	}

	public void setStoreAllocationManager(StoreAllocationManager pStoreAllocationManager) {
		this.mStoreAllocationManager = pStoreAllocationManager;
	}

	public MFFCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(MFFCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	public int getNumberofRecordsPerPage() {
		return numberofRecordsPerPage;
	}

	public void setNumberofRecordsPerPage(int numberofRecordsPerPage) {
		this.numberofRecordsPerPage = numberofRecordsPerPage;
	}

	public GetPlanogramInfoUtil getGetPlanogramInfoUtil() {
		return mGetPlanogramInfoUtil;
	}

	public void setGetPlanogramInfoUtil(GetPlanogramInfoUtil pGetPlanogramInfoUtil) {
		mGetPlanogramInfoUtil = pGetPlanogramInfoUtil;
	}
	
	
}
