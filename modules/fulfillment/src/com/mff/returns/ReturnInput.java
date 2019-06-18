package com.mff.returns;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author vsingh
 *
 */
public class ReturnInput {
  
  private String commerceItemId;
  private String quantityReturned;
  private String returnReason;
  private boolean returnShipping;
  private List<String> linesReturnedList;
  private String linesReturned;
  private String comments;
  private String originOfReturn;
  private String amountReturned;
  private Set<String> proRatedItemIds;
  
  public String getCommerceItemId() {
    return commerceItemId;
  }
  public void setCommerceItemId(String commerceItemId) {
    this.commerceItemId = commerceItemId;
  }
  
  public String getQuantityReturned() {
    return quantityReturned;
  }
  public void setQuantityReturned(String quantityReturned) {
    this.quantityReturned = quantityReturned;
  }
  
  public String getReturnReason() {
    return returnReason;
  }
  public void setReturnReason(String returnReason) {
    this.returnReason = returnReason;
  }
  
  public boolean isReturnShipping() {
    return returnShipping;
  }
  public void setReturnShipping(boolean returnShipping) {
    this.returnShipping = returnShipping;
  }
  
  public String getLinesReturned() {
    return linesReturned;
  }
  public void setLinesReturned(String linesReturned) {
    this.linesReturned = linesReturned;
  }
  
  public List<String> getLinesReturnedList() {
    return linesReturnedList;
  }
  public void setLinesReturnedList(List<String> linesReturnedList) {
    this.linesReturnedList = linesReturnedList;
  }
  
  public String getComments() {
    return comments;
  }
  public void setComments(String comments) {
    this.comments = comments;
  }
  
  public String getOriginOfReturn() {
    return originOfReturn;
  }
  public void setOriginOfReturn(String originOfReturn) {
    this.originOfReturn = originOfReturn;
  }
  
  public String getAmountReturned() {
    return amountReturned;
  }
  public void setAmountReturned(String amountReturned) {
    this.amountReturned = amountReturned;
  }
  
  public Set<String> getProRatedItemIds() {
    return proRatedItemIds;
  }
  public void setProRatedItemIds(Set<String> proRatedItemIds) {
    this.proRatedItemIds = proRatedItemIds;
  }
  
  @Override
  public String toString() {
      return ToStringBuilder.reflectionToString(this);
  }

}
