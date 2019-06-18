package com.mff.commerce.order;

public class MFFLineItemInput 
{
  
  /**
   * @return the commerceItemId
   */
  public String getCommerceItemId() {
    return commerceItemId;
  }
  /**
   * @param commerceItemId the commerceItemId to set
   */
  public void setCommerceItemId(String commerceItemId) {
    this.commerceItemId = commerceItemId;
  }
  /**
   * @return the giftCardNumber
   */
  public String getGiftCardNumber() {
    return giftCardNumber;
  }
  /**
   * @param giftCardNumber the giftCardNumber to set
   */
  public void setGiftCardNumber(String giftCardNumber) {
    this.giftCardNumber = giftCardNumber;
  }
  /**
   * @return the itemSelected
   */
  public boolean isItemSelected() {
    return itemSelected;
  }
  /**
   * @param itemSelected the itemSelected to set
   */
  public void setItemSelected(boolean itemSelected) {
    this.itemSelected = itemSelected;
  }

  private String commerceItemId;
  private String giftCardNumber;
  private boolean itemSelected;
}
