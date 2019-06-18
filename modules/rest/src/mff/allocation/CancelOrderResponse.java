package mff.allocation;

import atg.commerce.order.Order;

public class CancelOrderResponse {
  
  private Order mOrder;

  public Order getOrder() {
    return mOrder;
  }

  public void setOrder(Order mOrder) {
    this.mOrder = mOrder;
  }

}
