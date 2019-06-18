package mff.commerce.order;

import java.io.IOException;

import javax.servlet.ServletException;

import oms.allocation.store.StoreAllocationStates;
import mff.rest.MFFRestManager;
import mff.rest.StoreOrderResponse;
import atg.commerce.CommerceException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.google.common.base.Strings;

/**
 * Droplet to get list of orders by storeId
 * 
 * @author vsingh
 *
 */
public class GetOrdersByStoreIdDroplet extends DynamoServlet{
	
	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_STORE_ID = ParameterName.getParameterName("storeId");
	public static final ParameterName PARAM_PAGE_NUM = ParameterName.getParameterName("pageNum");
	public static final ParameterName PARAM_NUMBER_OF_RECORDS_PER_PAGE = ParameterName.getParameterName("numberOfRecordsPerPage");
	public static final ParameterName PARAM_ORDER_STATE = ParameterName.getParameterName("orderState");
	public static final ParameterName PARAM_NUMBER_OF_DAYS = ParameterName.getParameterName("numberOfDays");
	
	/* OUTPUT PARAMETERS */
	public static final String PARAM_STORE_ORDER_RESPONSE = "storeOrders";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_ERROR = ParameterName.getParameterName("error");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");
	
	private MFFRestManager restManager;
	
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		
		String storeId = (String) pRequest.getObjectParameter(PARAM_STORE_ID);
		String pageNumber = pRequest.getParameter(PARAM_PAGE_NUM);
		String numRecordsPerPage = pRequest.getParameter(PARAM_NUMBER_OF_RECORDS_PER_PAGE);
		String orderState = pRequest.getParameter(PARAM_ORDER_STATE);
    String numOfDays = pRequest.getParameter(PARAM_NUMBER_OF_DAYS);
    
		int pageNum = 1;
		int numberOfRecordsPerPage = getRestManager().getNumberofRecordsPerPage();
		String numberOfDays  = "7";
		
		vlogDebug("GetOrdersByStoreIdDroplet : input storeId {0} pageNum {1} numRecordsPerPage {2}",storeId,pageNumber,numRecordsPerPage);
		if(Strings.isNullOrEmpty(storeId)){
			vlogWarning("Missing required storeId parameter : {0}",storeId);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
			return;
		}
		
		if(!Strings.isNullOrEmpty(pageNumber)){
			pageNum = Integer.valueOf(pageNumber);
		}else{
			vlogWarning("pageNumber is null so defaulting to {0}",pageNum);
		}
		
		if(!Strings.isNullOrEmpty(numRecordsPerPage)){
			numberOfRecordsPerPage = Integer.valueOf(numRecordsPerPage);
		}else{
			vlogWarning("numRecordsPerPage is null so defaulting to {0}",numberOfRecordsPerPage);
		}
		
		if(!Strings.isNullOrEmpty(orderState)){
		  vlogWarning("orderState is {0}",orderState);
    }else{
      orderState = StoreAllocationStates.PRE_SHIP;
      vlogWarning("orderState is null so defaulting to PRE_SHIP");
    }
    
    if(!Strings.isNullOrEmpty(numOfDays)){
      numberOfDays = numOfDays;
    }else{
      vlogWarning("numOfDays is null so defaulting to {0}",numberOfDays);
    }
		
		try {
		  StoreOrderResponse storeOrderResponse;
		  if(orderState.equalsIgnoreCase(StoreAllocationStates.SHIPPED)){
		    storeOrderResponse = getRestManager().getStoreOrdersListByStoreId(storeId,StoreAllocationStates.SHIPPED,numberOfDays,pageNum,numberOfRecordsPerPage);
		  }else if(orderState.equalsIgnoreCase(StoreAllocationStates.READY_FOR_PICKUP)){
        storeOrderResponse = getRestManager().getStoreOrdersListByStoreId(storeId,StoreAllocationStates.READY_FOR_PICKUP,null,pageNum,numberOfRecordsPerPage);
      }else{
		    storeOrderResponse = getRestManager().getStoreOrdersListByStoreId(storeId, StoreAllocationStates.PRE_SHIP,null,pageNum, numberOfRecordsPerPage);
		  }
			pRequest.setParameter(PARAM_STORE_ORDER_RESPONSE, storeOrderResponse);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			return;
		} catch (CommerceException e) {
			logError("GetOrdersByStoreIdDroplet : Error getting orders by storeId : " + e);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		
	}

	public MFFRestManager getRestManager() {
		return restManager;
	}

	public void setRestManager(MFFRestManager restManager) {
		this.restManager = restManager;
	}
	
	

}
