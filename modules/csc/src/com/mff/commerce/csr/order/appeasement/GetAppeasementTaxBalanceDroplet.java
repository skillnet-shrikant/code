package com.mff.commerce.csr.order.appeasement;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.csr.order.appeasement.AppeasementUserMessage;
import atg.core.util.ResourceUtils;
import atg.nucleus.naming.ParameterName;
import atg.service.dynamo.LangLicense;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class GetAppeasementTaxBalanceDroplet extends DynamoServlet {
  protected static ResourceBundle sResourceBundle = ResourceUtils.getBundle(AppeasementUserMessage.RESOURCE_BUNDLE, LangLicense.getLicensedDefault());
  
  private static final ParameterName ELEMENT_NAME = ParameterName.getParameterName("elementName");
  private static final ParameterName ORDER_ID = ParameterName.getParameterName("orderId");
  
  private static final String ELEMENT = "element";
  private static final String ERROR = "error";
  private static final String OUTPUT_OPARAM = "output";
  private static final String ERROR_MESSAGE = "errorMsg";
  private MFFAppeasementManager mAppeasementManager;
  
  /**
   * @return the appeasementManager
   */
  public MFFAppeasementManager getAppeasementManager() {
    return mAppeasementManager;
  }

  /**
   * @param pAppeasementManager the appeasementManager to set
   */
  public void setAppeasementManager(MFFAppeasementManager pAppeasementManager) {
    mAppeasementManager = pAppeasementManager;
  }

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering service : pRequest, pResponse");
    String orderId = pRequest.getParameter(ORDER_ID);
    
    double taxTotal = 0.0D;
    
    try {
      taxTotal = getAppeasementManager().calculateRefundAdjustedTotal(orderId, MFFAppeasementManager.APPEASEMENT_TAXES_TYPE);
    } catch (Exception e) {
      if (isLoggingError()) logError(e);
      String msg = ResourceUtils.getMsgResource("unableToFindShippingBalance", AppeasementUserMessage.RESOURCE_BUNDLE, sResourceBundle);
      pRequest.setParameter(ERROR_MESSAGE, msg);
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
    String elementName = pRequest.getParameter(ELEMENT_NAME);
    if (elementName == null) elementName = ELEMENT;
    
    pRequest.setParameter(elementName, Double.valueOf(taxTotal));
    
    pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);
    vlogDebug("Exiting service : pRequest, pResponse");
  }
}
