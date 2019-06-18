package com.mff.commerce.order.purchase;

public enum CheckoutStep implements Comparable<CheckoutStep> {
  SHIPPING(1, "shipping"),
  PAYMENT(2, "payment"),
  ACCOUNT(3, "account"),
  REVIEW(4, "review"),
  CONFIRM(5, "confirm");
  
  private CheckoutStep(int pCode, String pName) {
    mCode = pCode;
    mName = pName;
  }
  
  public static final CheckoutStep getDefaultStep() {
    return SHIPPING;
  }
  
  public boolean before(CheckoutStep other) {
    return getCode() < other.getCode();
  }
  
  public boolean after(CheckoutStep other) {
    return getCode() > other.getCode();
  }
  
  private int mCode;
  private String mName;
  
  public int getCode() {
    return mCode;
  }
  public void setCode(int pCode) {
    mCode = pCode;
  }
  public String getName() {
    return mName;
  }
  public void setName(String pName) {
    mName = pName;
  }
  
  public String toString() {
    return getName();
  }
}
