package com.mff.commerce.pricing.calculators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.pricing.MFFOrderPriceInfo;

import atg.commerce.order.Order;
import atg.commerce.pricing.OrderDiscountCalculator;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.repository.RepositoryItem;

/**
 * The Class MFFOrderDiscountCalculator.
 *
 * @author MANOJ
 */
public class MFFOrderDiscountCalculator extends OrderDiscountCalculator {


  /**
   * Price order.
   *
   * @param pPriceQuote the price quote
   * @param pOrder the order
   * @param pPricingModel the pricing model
   * @param pLocale the locale
   * @param pProfile the profile
   * @param pExtraParameters the extra parameters
   * @throws PricingException the pricing exception
   */
  @Override
  public void priceOrder(OrderPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
    prePriceOrder(pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
    super.priceOrder(pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
  }

  /**
   * Pre price order.
   *
   * @param pPriceQuote the price quote
   * @param pOrder the order
   * @param pPricingModel the pricing model
   * @param pLocale the locale
   * @param pProfile the profile
   * @param pExtraParameters the extra parameters
   * @throws PricingException the pricing exception
   */
  private void prePriceOrder(OrderPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
    Integer type = (Integer) pPricingModel.getPropertyValue("type");
    if (type != null && type.intValue() == 10) {
      calculateAdjusterForPercentageOffPromo(pPriceQuote, pPricingModel, pExtraParameters, pOrder);
    } else if (type != null && type.intValue() == 11) {
      calculateAdjusterForAmountOffByTierPromo(pPriceQuote, pPricingModel, pExtraParameters, pOrder);
    }
  }

  /**
   * Calculate adjuster for amount off by tier promo.
   *
   * @param pPriceQuote the price quote
   * @param pPricingModel the pricing model
   * @param pExtraParameters the extra parameters
   * @param pOrder the order
   * @throws PricingException the pricing exception
   */
  private void calculateAdjusterForAmountOffByTierPromo(OrderPriceInfo pPriceQuote, RepositoryItem pPricingModel, Map pExtraParameters, Order pOrder) throws PricingException {
    double tierDiscount = super.getAdjuster(pPricingModel, pExtraParameters);

  }

  /**
   * Calculate adjuster for percentage off promo.
   *
   * @param pPriceQuote the price quote
   * @param pPricingModel the pricing model
   * @param pExtraParameters the extra parameters
   * @param pOrder the order
   * @throws PricingException the pricing exception
   */
  private void calculateAdjusterForPercentageOffPromo(OrderPriceInfo pPriceQuote, RepositoryItem pPricingModel, Map pExtraParameters, Order pOrder) throws PricingException {
    double adjuster = super.getAdjuster(pPricingModel, pExtraParameters);
    double giftCardLessSubtotal = ((MFFOrderPriceInfo) pPriceQuote).getOrderSubTotalLessGiftCard();
 
    vlogDebug(" Gift Card Less Subtotal " + giftCardLessSubtotal);
    double discountOnSubtotal = giftCardLessSubtotal * adjuster;
      
    vlogDebug(" Discount on Subtotal " + discountOnSubtotal);
    // Back calculate discount% on orderAmount
    adjuster = discountOnSubtotal / (pPriceQuote.getAmount());
    if (Double.isNaN(adjuster) || Double.isInfinite(adjuster)) { // Safety net
      adjuster = 0.0d;
    }
    vlogDebug(" New Adjuster " + adjuster);
    pExtraParameters.put("adjuster", adjuster);
  }

 
  /**
   * Gets the adjuster.
   *
   * @param pPricingModel the pricing model
   * @param pExtraParameters the extra parameters
   * @return the adjuster
   * @throws PricingException the pricing exception
   */
  protected double getAdjuster(RepositoryItem pPricingModel, Map pExtraParameters) throws PricingException {

    Double adjuster = 0d;
    if (pPricingModel != null) {
      if (pExtraParameters.get("adjuster") != null) {
        vlogDebug("Adjuster value is: {0} for promotion: {1}", adjuster, pPricingModel.getItemDisplayName());
        adjuster = (Double) pExtraParameters.get("adjuster");
      } else
        adjuster = super.getAdjuster(pPricingModel, pExtraParameters);
    }

    return adjuster;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected List getItemsToReceiveDiscountShare(Order pOrder) {
    List items = super.getItemsToReceiveDiscountShare(pOrder);
    List itemsListModified = new ArrayList(items);

    if (items == null) {
      return null;
    }

    Iterator itemsListIterator = itemsListModified.iterator();
    while (itemsListIterator.hasNext()) {
      MFFCommerceItemImpl item = (MFFCommerceItemImpl) itemsListIterator.next();
        
        if(item.isGiftCard()){
          itemsListIterator.remove();
        }
      
    }
    return itemsListModified;
  }
}
