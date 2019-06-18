package com.mff.commerce.csr.order;

import atg.nucleus.GenericService;

public class RefundGCLookupResponse extends GenericService{
  
  private String mOrderNumber;
  private String mOrderId;
  
  public String getOrderNumber() {
    return mOrderNumber;
  }
  public void setOrderNumber(String pOrderNumber) {
    mOrderNumber = pOrderNumber;
  }
  public String getOrderId() {
    return mOrderId;
  }
  public void setOrderId(String pOrderId) {
    mOrderId = pOrderId;
  }
  
  

}
