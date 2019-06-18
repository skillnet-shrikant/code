package com.mff.commerce.order;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.states.MFFShippingGroupStates;

/**
 * This droplet returns the address to be used in the label during gift card fulfillment process.
 * 
 */
public class MFFGiftcardShipAddressForLabel extends DynamoServlet {

  // Input Parameters
  private static final ParameterName ITEM = ParameterName.getParameterName("item");
  private static final ParameterName PAYGROUPTYPE = ParameterName.getParameterName("payGroupType");
  private static final ParameterName ORDER = ParameterName.getParameterName("order");
  
  // Output Parameters
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");

  // Lines for the label will be set in the lines param
  private static final String LINE1 = "line1";
  private static final String LINE2 = "line2";
  private static final String LINE3 = "line3";

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Started MFFGiftcardShipAddressForLabel.service..");

    // Whether this is a refundable gift card in which case we do not have a relationship with shipping group
    Boolean payGroupType = Boolean.parseBoolean((String)pRequest.getParameter(PAYGROUPTYPE));
    if (null == payGroupType) 
    {
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }

    HardgoodShippingGroup shipGroup = null;
    if(payGroupType.booleanValue())
    {
      // find sg
      Order currentOrder = (Order) pRequest.getObjectParameter(ORDER);
      if (isLoggingDebug()) logDebug("orderId = " + ((currentOrder != null) ? currentOrder.getId() : "order is null"));
      if (null == currentOrder) {
        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        return;
      }
      
      // Get the Shipping Group containing the shipping address for the gift card
      List<ShippingGroup> lShippingGroups = currentOrder.getShippingGroups();
      for (ShippingGroup lShippingGroup : lShippingGroups) {
        if (null != lShippingGroup && lShippingGroup.getState() == StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.SHIPPED)) {
          shipGroup = (HardgoodShippingGroup)lShippingGroup;
          break;
        }
      }
    }
    else
    {
      // Get the commerce item from the input parameter
      CommerceItem ci = (CommerceItem) pRequest.getObjectParameter(ITEM);
      if (isLoggingDebug()) logDebug("commerce item id = " + ((ci != null) ? ci.getId() : "commerce item is null"));
      if (null == ci) {
        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        return;
      }
      // Get the Shipping Group containing the shipping address for the gift card
      if (null != ci) {
        List<ShippingGroupRelationship> sgrs = ci.getShippingGroupRelationships();
        for (ShippingGroupRelationship sgr : sgrs) {
          if (null != sgr) {
            shipGroup = (HardgoodShippingGroup)sgr.getShippingGroup();
            break;
          }
        }
      }
    }

    if (null!=shipGroup)
    {
      // Format every line to be displayed in the generic shipping label during gift card fulfillment
      String line2 = (null!=shipGroup.getShippingAddress().getAddress2())?shipGroup.getShippingAddress().getAddress1() + " " + shipGroup.getShippingAddress().getAddress2():shipGroup.getShippingAddress().getAddress1();
      pRequest.setParameter(LINE1, shipGroup.getShippingAddress().getFirstName().toUpperCase() + " " + shipGroup.getShippingAddress().getLastName().toUpperCase());
      pRequest.setParameter(LINE2, line2.toUpperCase());
      pRequest.setParameter(LINE3, shipGroup.getShippingAddress().getCity().toUpperCase() + " " + shipGroup.getShippingAddress().getState().toUpperCase() + " " + shipGroup.getShippingAddress().getPostalCode());
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
    else
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
  }
}
