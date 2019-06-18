package mff.returns;

import java.util.ArrayList;
import java.util.List;

public class ReturnResponseItemPOS {
  
  private String commerceItemId;
  private boolean returnSuccess= false;
  private List<String> linesReturned = new ArrayList<String>();
  
  public String getCommerceItemId() {
    return commerceItemId;
  }
  public void setCommerceItemId(String commerceItemId) {
    this.commerceItemId = commerceItemId;
  }
  public boolean isReturnSuccess() {
    return returnSuccess;
  }
  public void setReturnSuccess(boolean returnSuccess) {
    this.returnSuccess = returnSuccess;
  }
  public List<String> getLinesReturned() {
    return linesReturned;
  }
  public void setLinesReturned(List<String> linesReturned) {
    this.linesReturned = linesReturned;
  }
  
  

}
