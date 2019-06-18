package com.mff.commerce.csr.environment;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.svc.agent.environment.EnvironmentChangeFormHandler;
import atg.svc.agent.environment.EnvironmentException;

import com.mff.commerce.order.MFFCSROrderHolder;

public class MFFChangeOrder extends EnvironmentChangeFormHandler {

  MFFCSROrderHolder csrOrderHolder;
  MFFCSREnvironmentTools csrEnviornmentTools;

  /**
   * To set the order id in the Order Holder which decides which repository to
   * use Core or OMS
   */
  @Override
  protected boolean preChangeEnvironment(DynamoHttpServletRequest request, DynamoHttpServletResponse response) throws ServletException, IOException {
    Order currentOrder = getCsrEnviornmentTools().getOriginalOrder();
    String currentOrderId = currentOrder == null ? "" : currentOrder.getId();
    String newOrderId = (String) getInputParameters().get("newOrderId");

    if (isLoggingDebug()) logDebug("MFFChangeOrder: Current Order is " + currentOrderId + ", New Order is " + newOrderId);

    // Set the order id, that is going to be active, in the Order Holder to know
    // which repository to use - core or oms
    getCsrOrderHolder().setOrderId(newOrderId);
    
    return super.preChangeEnvironment(request, response);
    //return true;
  }

  @Override
  protected boolean postChangeEnvironment(DynamoHttpServletRequest arg0, DynamoHttpServletResponse arg1) throws ServletException, IOException {

    if (!super.postChangeEnvironment(arg0, arg1)) return false;

    if (getFormError()) return true;

    try {
      getCsrEnviornmentTools().loadViewOrderAndProfile(getCsrOrderHolder().getOriginalOrder());
    } catch (EnvironmentException e) {
      throw new ServletException(e);
    }

    return true;
  }

  public MFFCSROrderHolder getCsrOrderHolder() {
    return csrOrderHolder;
  }

  public void setCsrOrderHolder(MFFCSROrderHolder csrOrderHolder) {
    this.csrOrderHolder = csrOrderHolder;
  }

  public MFFCSREnvironmentTools getCsrEnviornmentTools() {
    return csrEnviornmentTools;
  }

  public void setCsrEnviornmentTools(MFFCSREnvironmentTools csrEnviornmentTools) {
    this.csrEnviornmentTools = csrEnviornmentTools;
  }

}
