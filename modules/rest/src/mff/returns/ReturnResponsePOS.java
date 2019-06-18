package mff.returns;

import java.util.ArrayList;
import java.util.List;

public class ReturnResponsePOS {
  
  private String orderNumber;
  private List<ReturnResponseItemPOS> returnItems = new ArrayList<ReturnResponseItemPOS>();
  
  public String getOrderNumber() {
    return orderNumber;
  }
  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }
  
  public List<ReturnResponseItemPOS> getReturnItems() {
    return returnItems;
  }
  public void setReturnItems(List<ReturnResponseItemPOS> returnItems) {
    this.returnItems = returnItems;
  }
  
  

}
