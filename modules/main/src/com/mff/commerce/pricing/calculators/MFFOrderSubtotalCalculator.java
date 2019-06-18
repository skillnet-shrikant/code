package com.mff.commerce.pricing.calculators;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.purchase.GiftCardPaymentInfo;
import com.mff.commerce.pricing.MFFOrderPriceInfo;

import atg.commerce.order.Order;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.OrderSubtotalCalculator;
import atg.commerce.pricing.PricingException;
import atg.repository.RepositoryItem;

/**
 * The Class MFFOrderSubtotalCalculator.
 *
 * @author MANOJ
 */
public class MFFOrderSubtotalCalculator extends OrderSubtotalCalculator {

  /**
   * Price order.
   *
   * @param pOrderPriceInfo
   *          the order price info
   * @param pOrder
   *          the order
   * @param pRepositoryItem
   *          the repository item
   * @param pLocale
   *          the locale
   * @param pRepositoryItem2
   *          the repository item2
   * @param paramMap
   *          the param map
   * @throws PricingException
   *           the pricing exception
   */
  @Override
  public void priceOrder(OrderPriceInfo pOrderPriceInfo, Order pOrder, RepositoryItem pRepositoryItem, Locale pLocale, RepositoryItem pRepositoryItem2, Map paramMap) throws PricingException {
    List items = pOrder.getCommerceItems();
    Double giftCardItemSubTotal = 0.0;
    Double orderSubTotalLessGiftCard = 0.0;
    for (Object item : items) {
      if (item instanceof MFFCommerceItemImpl) {
        MFFCommerceItemImpl mffCi = (MFFCommerceItemImpl) item;
        if (mffCi.isGiftCard()) {
          giftCardItemSubTotal += mffCi.getPriceInfo().getAmount();
        }
      }
    }
    
    Double giftCardPaymentAmount = 0.0;
    if(pOrder.getPaymentGroupCount() > 0){
      List paymentGroups = pOrder.getPaymentGroups();
      for(Object paymentGroup: paymentGroups){
        if(paymentGroup instanceof MFFGiftCardPaymentGroup){
          giftCardPaymentAmount+=((MFFGiftCardPaymentGroup)paymentGroup).getAmount();
        }
      }
    }

    if (pOrder.getPriceInfo() != null) {
      orderSubTotalLessGiftCard = pOrder.getPriceInfo().getRawSubtotal() - giftCardItemSubTotal;

      if (isLoggingDebug()) {
        logDebug("MFFOrderSubtotalCalculator --->" + "orderSubTotal :" + pOrder.getPriceInfo().getRawSubtotal());
        logDebug("MFFOrderSubtotalCalculator --->" + "giftCardItemSubTotal :" + giftCardItemSubTotal);
        logDebug("MFFOrderSubtotalCalculator --->" + "orderSubTotalLessGiftCard :" + orderSubTotalLessGiftCard);
        logDebug("MFFOrderSubtotalCalculator --->" + "giftcardPaymentTotal :" + giftCardPaymentAmount);
        
      }

      MFFOrderPriceInfo mffOrderPriceInfo = (MFFOrderPriceInfo) pOrderPriceInfo;
      mffOrderPriceInfo.setOrderSubTotalLessGiftCard(orderSubTotalLessGiftCard);
      mffOrderPriceInfo.setGiftCardPaymentTotal(giftCardPaymentAmount);
    }

    

    super.priceOrder(pOrderPriceInfo, pOrder, pRepositoryItem, pLocale, pRepositoryItem2, paramMap);
  }

}
