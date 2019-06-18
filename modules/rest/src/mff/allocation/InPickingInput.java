package mff.allocation;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class InPickingInput {
  
  private String orderId;
  private String commerceItemIds;
  
  public String getOrderId() {
    return orderId;
  }
  
  public void setOrderId(String pOrderId) {
    orderId = pOrderId;
  }
  
  public String getCommerceItemIds() {
    return commerceItemIds;
  }
  
  public void setCommerceItemIds(String pCommerceItemIds) {
    commerceItemIds = pCommerceItemIds;
  }
  
  @Override
  public String toString() {
      return ToStringBuilder.reflectionToString(this);
  }
  
  

}
