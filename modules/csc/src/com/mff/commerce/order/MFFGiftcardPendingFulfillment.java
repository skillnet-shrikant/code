package com.mff.commerce.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.commerce.states.MFFPaymentGroupStates;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import oms.commerce.order.OMSOrderManager;

/**
 * This droplet returns the list of gift card items in Pending Fulfillment status
 * or the list of payment groups in refundable gift card pending fulfillment  status.
 * 
 * <p>
 * Example Usage:
 * <p>
 * 
 * <pre>
 *  <dsp:droplet name="GiftcardPendingFulfillment">
 *    <dsp:param name="order" value="${order}"/>
 *    <dsp:param name="itemType" value="true"/>
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
public class MFFGiftcardPendingFulfillment extends DynamoServlet {

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
  private List<String> gwpItemValidQualifyingStates;

  @SuppressWarnings({ "unused", "unchecked" })
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Started MFFGiftcardPendingFulfillment.service..");

    List<MFFCommerceItemImpl> gcInPendingFulfillment = new ArrayList<MFFCommerceItemImpl>();
    List<PaymentGroup> gcPGInPendingFulfillment = new ArrayList<PaymentGroup>();
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
        List<MFFCommerceItemImpl> gwpInPendingFulfillment = new ArrayList<MFFCommerceItemImpl>();
        boolean isGwpValidForActivation = true;
        
        for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
          if (null != lCommerceItem && 
              (lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_GC_FULFILLMENT) ||
              lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.ERROR_GC_ACTIVATION))) {
            
                if(lCommerceItem.isGwp()){
                  gwpInPendingFulfillment.add(lCommerceItem);
                }else{
                  gcInPendingFulfillment.add(lCommerceItem);
                }
          } else if(!getGwpItemValidQualifyingStates().contains(lCommerceItem.getStateAsString())){
            isGwpValidForActivation = false;
          }
        }
        
        if(gwpInPendingFulfillment.size() > 0 && isGwpValidForActivation){
          gcInPendingFulfillment.addAll(gwpInPendingFulfillment);
        }
        if (null!=gcInPendingFulfillment && gcInPendingFulfillment.size()>0)
        {
          pRequest.setParameter(ELEMENTS, gcInPendingFulfillment);
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
            gcPGInPendingFulfillment.add(lPaymentGroup);
          }
        }
        if (null!=gcPGInPendingFulfillment && gcPGInPendingFulfillment.size()>0)
        {
          pRequest.setParameter(ELEMENTS, gcPGInPendingFulfillment);
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

  public List<String> getGwpItemValidQualifyingStates() {
    return gwpItemValidQualifyingStates;
  }

  public void setGwpItemValidQualifyingStates(List<String> pGwpItemValidQualifyingStates) {
    gwpItemValidQualifyingStates = pGwpItemValidQualifyingStates;
  }
  
  
}
