package com.mff.commerce.order;

import java.io.IOException;

import javax.servlet.ServletException;

import oms.commerce.settlement.SettlementManagerImpl;
import atg.commerce.CommerceException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.firstdata.order.MFFGiftCardPaymentGroup;

/**
 * This droplet returns the amount to use for a refundable gift card
 * 
 */
public class MFFRefundableGiftcardAmount extends DynamoServlet {

  private SettlementManagerImpl settlementManager;
  
  // Input Parameters
  private static final ParameterName PAYGROUP = ParameterName.getParameterName("paygroup");

  // Output Parameters
  public static final ParameterName TRUE = ParameterName.getParameterName("true");
  public static final ParameterName FALSE = ParameterName.getParameterName("false");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");

  // Error messages will be set in the ELEMENTS param
  private static final String ELEMENTS = "elements";

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Started MFFRefundableGiftcardAmount.service..");

    double refundAmount=0.0d;
    // Get the gc payment group from the input parameter
    MFFGiftCardPaymentGroup gcPayGroup = (MFFGiftCardPaymentGroup) pRequest.getObjectParameter(PAYGROUP);
    if (isLoggingDebug()) logDebug("payGroup = " + ((gcPayGroup != null) ? gcPayGroup.getId() : "gc payment group is null"));
    if (null == gcPayGroup) {
      pRequest.setParameter(ELEMENTS, "GC payment group not found, set to error");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
    
    if (null != gcPayGroup) 
    {
      try 
      {
          refundAmount = getSettlementManager().getRefundAmount(gcPayGroup);
          pRequest.setParameter(ELEMENTS, refundAmount);
          pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
          return;
      }
      catch (CommerceException e) {
        vlogError(e, "CommerceException getting the amount for a refundable gift card");
      }
    }
    pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

  /**
   * @return the settlementManager
   */
  public SettlementManagerImpl getSettlementManager() {
    return settlementManager;
  }

  /**
   * @param pSettlementManager the settlementManager to set
   */
  public void setSettlementManager(SettlementManagerImpl pSettlementManager) {
    settlementManager = pSettlementManager;
  }

}
