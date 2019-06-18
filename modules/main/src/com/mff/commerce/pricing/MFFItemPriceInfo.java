package com.mff.commerce.pricing;

import atg.commerce.pricing.ItemPriceInfo;

/**
 * @author Manoj
 */
public class MFFItemPriceInfo extends ItemPriceInfo {

  private static final long serialVersionUID = 1L;

  private double proratedDiscountPrice;
  private String salePriceListId;


  private double discountAmount;
  private double effectivePrice;


  /**
   * The following attribute is used for capturing the promo id associated with
   * a sale price.
   */
  private String salePricePromoId;

  public double getProratedDiscountPrice() {
    return proratedDiscountPrice;
  }

  public void setProratedDiscountPrice(double proratedDiscountPrice) {
    this.proratedDiscountPrice = proratedDiscountPrice;
  }

  public String getSalePriceListId() {
    return salePriceListId;
  }

  public void setSalePriceListId(String salePriceListId) {
    this.salePriceListId = salePriceListId;
  }

  public String getSalePricePromoId() {
    return salePricePromoId;
  }

  public void setSalePricePromoId(String pSalePricePromoId) {
    salePricePromoId = pSalePricePromoId;
  }

  public double getDiscountAmount() {
	  return discountAmount;
  }

  public void setDiscountAmount(double discountAmount) {
	  this.discountAmount = discountAmount;
  }

  public double getEffectivePrice() {
	  return effectivePrice;
  }

  public void setEffectivePrice(double effectivePrice) {
	  this.effectivePrice = effectivePrice;
  }
}
