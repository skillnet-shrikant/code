package com.mff.commerce.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import oms.commerce.order.OMSOrderManager;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.states.MFFPaymentGroupStates;

/**
 * This droplet returns the list of gift card items to be listed in the packing slip
 * or the list of payment groups in refundable gift cards.
 * 
 */
public class MFFGiftcardPackingSlip extends DynamoServlet {

  // Input Parameters
  private static final ParameterName ORDERID = ParameterName.getParameterName("orderid");
  private static final ParameterName ITEMTYPE = ParameterName.getParameterName("itemType");

  // Output Parameters
  public static final ParameterName TRUE = ParameterName.getParameterName("true");
  public static final ParameterName FALSE = ParameterName.getParameterName("false");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");

  // Error messages will be set in the ELEMENTS param
  private static final String ELEMENTS = "elements";
  private static final String ORDEROUT = "orderout";
  
  private OMSOrderManager omsOrderManager;
  private List<String> excludedStates;

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Started MFFGiftcardPendingFulfillment.service..");

    List<CommerceItem> gcInCommerceItems = new ArrayList<CommerceItem>();
    List<PaymentGroup> gcInPaymentGroups = new ArrayList<PaymentGroup>();
    // Get the orderid from the input parameter
    String currentOrderId = (String) pRequest.getParameter(ORDERID);
    if (isLoggingDebug()) logDebug("currentOrderId = " + ((currentOrderId != null) ? currentOrderId : "order id is null"));
    if (null == currentOrderId) {
      pRequest.setParameter(ELEMENTS, "order not found, set to error");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }

    // Whether item type or paygroup type
    Boolean itemType = Boolean.parseBoolean((String)pRequest.getParameter(ITEMTYPE));
    if (null == itemType) {
      pRequest.setParameter(ELEMENTS, "itemType not found");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }

    try 
    {
      Order omsOrder = getOmsOrderManager().loadOrder(currentOrderId);
      if (null != omsOrder && itemType.booleanValue()) 
      {
        // Collect commerce items pending GC fulfillment
        List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
        for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
          if (null != lCommerceItem && getOmsOrderManager().isGiftCardItem(lCommerceItem) && !getExcludedStates().contains(lCommerceItem.getState())) 
          {
            gcInCommerceItems.add(lCommerceItem);
          }
        }
        if (null!=gcInCommerceItems && gcInCommerceItems.size()>0)
        {
          pRequest.setParameter(ELEMENTS, gcInCommerceItems);
          pRequest.setParameter(ORDEROUT, omsOrder);
          pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
          return;
        }
      }
      else
      {
        // itemType false means payment group for refundable gift card
        List<PaymentGroup> lpayGroups = omsOrder.getPaymentGroups();
        for (PaymentGroup lPaymentGroup : lpayGroups) {
          if (null != lPaymentGroup && 
              (lPaymentGroup.getState() == StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.TO_BE_FULFILLED) ||
              lPaymentGroup.getState() == StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.ERROR_GC_ACTIVATION))) {
            gcInPaymentGroups.add(lPaymentGroup);
          }
        }
        if (null!=gcInPaymentGroups && gcInPaymentGroups.size()>0)
        {
          pRequest.setParameter(ELEMENTS, gcInPaymentGroups);
          pRequest.setParameter(ORDEROUT, omsOrder);
          pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
          return;
        }
      }    
    } catch (CommerceException e) {
      vlogError(e, "A CommerceException occurred while trying to load the order");
    }

    pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

  /**
   * @return the omsOrderManager
   */
  public OMSOrderManager getOmsOrderManager() {
    return omsOrderManager;
  }

  /**
   * @param pOmsOrderManager the omsOrderManager to set
   */
  public void setOmsOrderManager(OMSOrderManager pOmsOrderManager) {
    omsOrderManager = pOmsOrderManager;
  }

  /**
   * @return the excludedStates
   */
  public List<String> getExcludedStates() {
    return excludedStates;
  }

  /**
   * @param pExcludedStates the excludedStates to set
   */
  public void setExcludedStates(List<String> pExcludedStates) {
    excludedStates = pExcludedStates;
  }

}
