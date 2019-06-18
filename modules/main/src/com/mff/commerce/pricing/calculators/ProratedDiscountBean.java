package com.mff.commerce.pricing.calculators;

import java.util.List;

import atg.commerce.order.CommerceItem;
import atg.repository.RepositoryItem;

public class ProratedDiscountBean {
  
  private List<CommerceItem> discountedItems;
  private List<CommerceItem> qualifierItems;
  private RepositoryItem promotion;

  public List<CommerceItem> getDiscountedItems() {
    return discountedItems;
  }

  public void setDiscountedItems(List<CommerceItem> discountedItems) {
    this.discountedItems = discountedItems;
  }

  public List<CommerceItem> getQualifierItems() {
    return qualifierItems;
  }

  public void setQualifierItems(List<CommerceItem> qualifierItems) {
    this.qualifierItems = qualifierItems;
  }

  public RepositoryItem getPromotion() {
    return promotion;
  }

  public void setPromotion(RepositoryItem promotion) {
    this.promotion = promotion;
  }
}
