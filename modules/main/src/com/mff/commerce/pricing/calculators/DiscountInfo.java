package com.mff.commerce.pricing.calculators;

import java.util.List;

import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.commerce.order.CommerceItem;

public class DiscountInfo {
  
  private MFFCommerceItemImpl giftItem;
  private List<MFFCommerceItemImpl> qualifierItems;
  private double totalQualifierAmount;
  private double discountAmount;
  
  public MFFCommerceItemImpl getGiftItem() {
    return giftItem;
  }

  public void setGiftItem(MFFCommerceItemImpl pGiftItem) {
    this.giftItem = pGiftItem;
  }

  public List<MFFCommerceItemImpl> getQualifierItems() {
    return qualifierItems;
  }

  public void setQualifierItems(List<MFFCommerceItemImpl> qualifierItems) {
    this.qualifierItems = qualifierItems;
  }
  
  public void setDiscountAmount (double pDiscountAmount) {
	  this.discountAmount = pDiscountAmount;
  }
  
  public double getDiscountAmount() {
	  return discountAmount;
  }

  public void setTotalQualifierAmount (double pTotalQualifierAmount) {
	  totalQualifierAmount = pTotalQualifierAmount;
  }
  
  public double getTotalQualifierAmount () {
	  return totalQualifierAmount;
  }

}
