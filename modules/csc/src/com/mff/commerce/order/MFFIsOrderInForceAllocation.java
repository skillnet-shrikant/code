package com.mff.commerce.order;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import oms.commerce.order.OMSOrderManager;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.states.MFFCommerceItemStates;

/**
 * This droplet is used to check if the order is considered in forced allocate
 * status based on any commerce item in that state.
 * 
 * <p>
 * Example Usage:
 * <p>
 * 
 * <pre>
 *  <dsp:droplet name="IsOrderInForceAllocation">
 *    <dsp:param name="order" value="${order}"/>
 *    <dsp:oparam name="true">
 *      true  
 *    </dsp:oparam>
 *    <dsp:oparam name="false">
 *      false
 *    </dsp:oparam>
 *  </dsp:droplet>
 * </pre>
 * <p>
 * 
 */
public class MFFIsOrderInForceAllocation extends DynamoServlet {

  // Input Parameters
  private static final ParameterName ORDER_ID = ParameterName.getParameterName("orderId");

  // Output Parameters
  public static final ParameterName TRUE = ParameterName.getParameterName("true");
  public static final ParameterName FALSE = ParameterName.getParameterName("false");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");

  // Error messages will be set in the ELEMENTS param
  private static final String ELEMENTS = "elements";
  
  private OMSOrderManager orderManager;

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Started MFFIsOrderInForceAllocation.service..");

    boolean forcedAllocationOrder = false;
    // Get the order from the input parameter
    String orderId = pRequest.getParameter(ORDER_ID);
    if (isLoggingDebug()) logDebug("orderId = " + ((orderId != null) ? orderId : "order id is null"));
    if (null == orderId) {
      pRequest.setParameter(ELEMENTS, "order id not found, set to error");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
    
    Order currentOrder;
    try 
    {
      currentOrder = getOrderManager().loadOrder(orderId);
    } catch (CommerceException e) {
      pRequest.setParameter(ELEMENTS, "order " + orderId + " can not be loaded, set to error");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }

    if (null != currentOrder) {
      // Collect commerce items to be cancelled
      List<CommerceItem> lCommerceItems = currentOrder.getCommerceItems();
      for (CommerceItem lCommerceItem : lCommerceItems) {
        if (null != lCommerceItem && lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.FORCED_ALLOCATION)) {
          // Create item allocations on a forced allocation order
          forcedAllocationOrder = true;
          break;
        }
      }
    }

    if (forcedAllocationOrder)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

  /**
   * @return the orderManager
   */
  public OMSOrderManager getOrderManager() {
    return orderManager;
  }

  /**
   * @param pOrderManager the orderManager to set
   */
  public void setOrderManager(OMSOrderManager pOrderManager) {
    orderManager = pOrderManager;
  }

}
