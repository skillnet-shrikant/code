package com.mff.commerce.order;

import atg.commerce.csr.order.CSROrderHolder;

/**
 * MFFCSROrderHolder extends the OOTB CSROrderHolder. We override the order
 * class type for reporting, promotions and other purposes. Adding the orderId
 * to use when evaluating the repository name.
 */
public class MFFCSROrderHolder extends CSROrderHolder {
  private static final String CSC_ORDER_TYPE = "cscOrder"; // CSC order class
  private String orderId;

  @Override
  public String getOrderType() {
    return CSC_ORDER_TYPE;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

}
