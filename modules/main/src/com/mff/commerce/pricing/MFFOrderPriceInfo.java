package com.mff.commerce.pricing;

import atg.commerce.pricing.OrderPriceInfo;

public class MFFOrderPriceInfo extends OrderPriceInfo {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private double orderSubTotalLessGiftCard;
  //total of gcs used as payment
  private double mGiftCardPaymentTotal = 0;
  
  private double orderChargeAmount = 0;
  
  public void setOrderChargeAmount(double pOrderChargeAmount) {
	orderChargeAmount = pOrderChargeAmount;
}

public double getOrderSubTotalLessGiftCard() {
    return orderSubTotalLessGiftCard;
  }

  public void setOrderSubTotalLessGiftCard(double orderSubTotalLessGiftCard) {
    this.orderSubTotalLessGiftCard = orderSubTotalLessGiftCard;
  }

  public double getGiftCardPaymentTotal() {
    return mGiftCardPaymentTotal;
  }

  public void setGiftCardPaymentTotal(double pGiftCardPaymentTotal) {
    mGiftCardPaymentTotal = pGiftCardPaymentTotal;
  }
  
  public double getOrderChargeAmount(){
    return getTotal() - getGiftCardPaymentTotal();
  }

}
