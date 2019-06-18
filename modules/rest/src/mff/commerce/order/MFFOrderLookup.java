package mff.commerce.order;

import java.io.IOException;

import javax.servlet.ServletException;

import com.google.common.base.Strings;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderLookup;
import atg.commerce.order.OrderUserMessage;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import oms.commerce.order.MFFOMSOrderManager;

public class MFFOrderLookup extends OrderLookup {

  public static final ParameterName ORDER_NUMBER = ParameterName.getParameterName("orderNumber");
  
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)throws ServletException, IOException{
    
    String orderNumber = (String)pRequest.getLocalParameter(ORDER_NUMBER);
    if(!Strings.isNullOrEmpty(orderNumber)){
      vlogDebug("About to search by orderNumber : {0}",orderNumber);
      searchByOrderNumber(pRequest, pResponse, orderNumber);
      return;
    }
    
    vlogDebug("OrderNumber is null/empty so calling super");
    super.service(pRequest, pResponse);
  }

  protected void searchByOrderNumber(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pOrderNumber) 
      throws ServletException, IOException {

    Order order = null;
    
    try {
      String orderId = ((MFFOMSOrderManager)getOrderManager()).getOrderIdByOrderNumber(pOrderNumber);
      if(!Strings.isNullOrEmpty(orderId)){
        order = getOrderManager().loadOrder(orderId);
      }else{
        vlogDebug("Cannot find orderId for orderNumber : {0} ",pOrderNumber);
      }

      if (order == null) {
        String errMsg = OrderUserMessage.format(MSG_NO_SUCH_ORDER, getUserLocale(pRequest, pResponse));
        pRequest.setParameter(ERRORMESSAGE, errMsg);
        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        return;
      }
      
      if (isEnableSecurity()) {
        if (isLoggingDebug()) logDebug((new StringBuilder()).append("checking ownership. current user is ").append(getCurrentProfileId(pRequest)).append(" order owner is ").append(order.getProfileId()).toString());
        if (!order.getProfileId().equals(getCurrentProfileId(pRequest))) {
          String errMsg = OrderUserMessage.format(MSG_NO_PERMISSION_FOR_ORDER, getUserLocale(pRequest, pResponse));
          pRequest.setParameter(ERRORMESSAGE, errMsg);
          pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
          return;
        }
      }
  
      pRequest.setParameter(RESULT, order);
      pRequest.setParameter(COUNT, Integer.valueOf(1));
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      
    } catch (CommerceException e) {
      if (isLoggingError()) logError(e);
      String errMsg = OrderUserMessage.format(MSG_GENERAL_ERROR, getUserLocale(pRequest, pResponse));
      pRequest.setParameter(ERRORMESSAGE, errMsg);
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
    }
    //return;
  }

}
