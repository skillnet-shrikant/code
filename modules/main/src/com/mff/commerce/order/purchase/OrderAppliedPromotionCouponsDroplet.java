package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableTools;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.order.MFFCommerceItemImpl;

/**
 * The Class OrderAppliedPromotionCouponsDroplet.
 *
 * @author manoj_mane
 */
/**
 * @author MANOJ_2
 *
 */
public class OrderAppliedPromotionCouponsDroplet extends DynamoServlet {

  /** The Constant ORDER. */
  private static final ParameterName ORDER = ParameterName.getParameterName("order");

  /** The Constant OUTPUT. */
  private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

  /** The description. */
  private final String DESCRIPTION = "description";
  private final String PROMO_SHORT_DESC = "shortDescription";
  private final String PROMO_DISP_NAME = "displayName";

  /** The result. */
  private final String RESULT = "result";

  /** The coupon discount. */
  private final String COUPON_DISCOUNT = "couponDiscount";

  private final String SHOW_OUTPUT = "showOutput";

  /** The global. */
  private final String GLOBAL = "global";

  /** The Constant ACTIVE_PROMOTIONS. */
  public static final ParameterName ACTIVE_PROMOTIONS = ParameterName.getParameterName("activePromotions");

  /** The Constant PROMOTION. */
  public static final String PROMOTION = "promotion";

  /** The m claimable tools. */
  private ClaimableTools mClaimableTools;

  /**
   * Service method of a servlet.
   *
   * @param request
   *          the request
   * @param response
   *          the response
   * @throws ServletException
   *           the servlet exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("rawtypes")
  public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response) throws ServletException, IOException {
    double itemDiscountAmount = 0.0d;
    double orderDiscountAmount = 0.0d;
    double shippingDiscountAmount = 0.0d;

    Order order = (Order) request.getLocalParameter(ORDER);
    if (order == null) {
      if (isLoggingError()) {
        logError("Order is a required parameter to display user ");
      }
      return;
    }
    List activePromotions = (List) request.getObjectParameter(ACTIVE_PROMOTIONS);

    List<AppliedPromotion> orderAppliedPromotions = new ArrayList<AppliedPromotion>();

    try {
      /**
       * First find out Order level promotion
       */
      if (order.getPriceInfo() != null) {
        List adjustments = (List) order.getPriceInfo().getAdjustments();
        if (adjustments != null) {
          getPromotionFromAdjustment(adjustments, orderAppliedPromotions, "order");
        }
      }

      List<?> sgList = order.getShippingGroups();
      if (sgList.size() >= 1) {
        Iterator<?> sgiter = sgList.iterator();
        while (sgiter.hasNext()) {
          ShippingGroup sg = (ShippingGroup) sgiter.next();
          ShippingPriceInfo shipPricingInfo = sg.getPriceInfo();
          if (shipPricingInfo != null) {
            List adjustments = (List) shipPricingInfo.getAdjustments();
            if (adjustments != null) {
              getPromotionFromAdjustment(adjustments, orderAppliedPromotions, "shipping");
            }
          }
        }
      }

      List commerceItems = (List) order.getCommerceItems();
      if (commerceItems != null) {
        Iterator ciIter = commerceItems.iterator();
        while (ciIter.hasNext()) {
          MFFCommerceItemImpl ci = (MFFCommerceItemImpl) ciIter.next();
          ItemPriceInfo priceInfo = ci.getPriceInfo();

          if (priceInfo != null) {

            List adjustments = (List) priceInfo.getAdjustments();
            if (adjustments != null) {
              getPromotionFromAdjustment(adjustments, orderAppliedPromotions, "item");
            }
          }
        }
      }
      applyRemainingActivePromotions(activePromotions,orderAppliedPromotions);
      itemDiscountAmount = calculateCouponItemDiscountAdjustments(commerceItems);
      orderDiscountAmount = calculateCouponOrderDiscountAdjustments(order);
      shippingDiscountAmount = calculateCouponShippingDiscountAdjustments(sgList);
    } catch (ClaimableException e) {
      if (isLoggingError()) {
        logError("Some error in fetching coupons applied.", e);
      }
    }
       
    boolean showOutput = false;

    double totalCouponDiscountAmount = 0.0d;
    
    // 2564 - Handle free item shipping promos
    boolean hasItemShipDiscount = false;
    
    if (orderAppliedPromotions != null && orderAppliedPromotions.size() > 0) {
      Iterator itr = orderAppliedPromotions.iterator();
      while(itr.hasNext()){
        AppliedPromotion promo = (AppliedPromotion) itr.next();
        if(promo != null){
        	// Flag free item shipping promos
        	if(promo.isItemShipping()) {
        		hasItemShipDiscount=true;
        	}
          if( promo.getDiscountType().equalsIgnoreCase("item") && itemDiscountAmount > 0.0d){
            promo.setDiscountAmount(itemDiscountAmount);
            totalCouponDiscountAmount+=itemDiscountAmount;
          }else if( promo.getDiscountType().equalsIgnoreCase("shipping") && shippingDiscountAmount > 0.0d){
            promo.setDiscountAmount(shippingDiscountAmount);
            totalCouponDiscountAmount+=shippingDiscountAmount;
          }else if( promo.getDiscountType().equalsIgnoreCase("order") && orderDiscountAmount > 0.0d){
            promo.setDiscountAmount(orderDiscountAmount);
            totalCouponDiscountAmount+=orderDiscountAmount;
          }
        }
      }
      request.setParameter(RESULT, orderAppliedPromotions);
    }
    request.setParameter(COUPON_DISCOUNT, totalCouponDiscountAmount);
    request.setParameter(SHOW_OUTPUT, showOutput);
    // 2564 - Free item shipping promos will not have a $ discount
    // returning this flag to adjust the display on UI
    request.setParameter("hasItemShipDiscount", hasItemShipDiscount);
    request.serviceLocalParameter(OUTPUT, request, response);
  }

  /**
   * Gets the promotion from adjustment.
   *
   * @param adjustments
   *          the adjustments
   * @param orderAppliedPromotions
   *          the order applied promotions
   * @return the promotion from adjustment
   * @throws ClaimableException
   *           the claimable exception
   */
  @SuppressWarnings("rawtypes")
  private void getPromotionFromAdjustment(List<?> adjustments, List<AppliedPromotion> orderAppliedPromotions, String discountType) throws ClaimableException {

    RepositoryItem[] couponItems = null;
    Iterator adjIter = adjustments.iterator();

    while (adjIter.hasNext()) {
      PricingAdjustment pricingAdjustment = (PricingAdjustment) adjIter.next();
      if (pricingAdjustment == null) {
        continue;
      }

      RepositoryItem promotion = pricingAdjustment.getPricingModel();

      if (promotion != null) {
        Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);
        if (isGlobal != null && isGlobal.booleanValue()) continue;

        String description = (String) promotion.getPropertyValue(DESCRIPTION);

        if (StringUtils.isEmpty(description)) {
          description = promotion.getRepositoryId();
        }
        String promoName = (String) promotion.getPropertyValue(PROMO_DISP_NAME);
        if (StringUtils.isEmpty(promoName)) {
        	promoName = description;
        }
        String promoShortDesc = (String) promotion.getPropertyValue(PROMO_SHORT_DESC);
        if (StringUtils.isEmpty(promoShortDesc)) {
        	promoShortDesc = description;
        }
        
        couponItems = getClaimableTools().getCouponsForPromotion(promotion.getRepositoryId());

        AppliedPromotion promo = new AppliedPromotion();
        if (couponItems != null) {
          promo.setCouponCode(couponItems[0].getRepositoryId());
        } else {
          promo.setCouponCode(promotion.getRepositoryId());
        }
        promo.setDescription(description);
        promo.setPromoName(promoName);
        promo.setPromoShortDesc(promoShortDesc);
        promo.setPromoId(promotion.getRepositoryId());
        promo.setDiscountType(discountType);
        // 2564 - Free item shipping promos are really item discounts when they are setup
        // but treated as shipping discount in the UI
        if(discountType.equalsIgnoreCase("item") && pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase("Item Free Shipping Discount")) {
        	promo.setItemShipping(true);
        }
        if(couponItems != null) {
        	if(!isCouponExists(orderAppliedPromotions, couponItems[0].getRepositoryId())) {
        		orderAppliedPromotions.add(promo);
        	}
        } else {
        	orderAppliedPromotions.add(promo);
        }
      }
    }

  }

  /**
   * <p>
   * Method to handle calculation of Item Discount by coupon.
   *
   * @param commerceItems
   *          the commerce items
   * @return The discount amount by coupon id.
   */
  @SuppressWarnings("rawtypes")
  public double calculateCouponItemDiscountAdjustments(List commerceItems) {
    double discountAdjustment = 0.0d;
    if (commerceItems != null && commerceItems.size() > 0) {
      Iterator iter = commerceItems.iterator();
      while (iter.hasNext()) {
        MFFCommerceItemImpl pItem = (MFFCommerceItemImpl) iter.next();
        if (pItem.getPriceInfo() != null) {
          List adjustments = pItem.getPriceInfo().getAdjustments();
          if (adjustments != null && adjustments.size() > 0) {
            discountAdjustment = getDiscountedAmount(adjustments);
          }
        }
      }
    }
    return discountAdjustment;
  }

  /**
   * Calculate coupon shipping discount adjustments.
   *
   * @param sgList
   *          the sg list
   * @return the double
   */
  @SuppressWarnings("rawtypes")
  public double calculateCouponShippingDiscountAdjustments(List<?> sgList) {
    double discountAdjustment = 0.0d;
    if (sgList.size() >= 1) {
      Iterator<?> sgiter = sgList.iterator();
      while (sgiter.hasNext()) {
        ShippingGroup sg = (ShippingGroup) sgiter.next();
        ShippingPriceInfo shipPricingInfo = sg.getPriceInfo();
        if (shipPricingInfo != null) {
          List adjustments = shipPricingInfo.getAdjustments();
          if (adjustments != null && adjustments.size() > 0) {
            discountAdjustment = getDiscountedAmount(adjustments);
          }
        }
      }
    }
    return discountAdjustment;
  }

  /**
   * <p>
   * Method to handle calculation of Item Discount by coupon.
   *
   * @param order
   *          the order
   * @return The discount amount by coupon id.
   */
  @SuppressWarnings("rawtypes")
  public double calculateCouponOrderDiscountAdjustments(Order order) {
    double discountAdjustment = 0.0d;

    if (order != null) {
      List orderAdjustments = order.getPriceInfo().getAdjustments();
      if (orderAdjustments != null && orderAdjustments.size() > 0) {
        discountAdjustment = getDiscountedAmount(orderAdjustments);
      }
    }
    return discountAdjustment;
  }

  /**
   * Gets the discounted amount.
   *
   * @param adjustments
   *          the adjustments
   * @return the discounted amount
   */
  @SuppressWarnings("rawtypes")
  public double getDiscountedAmount(List<?> adjustments) {
    double discountAdjustment = 0.0;
    if (adjustments != null) {
      Iterator itAdj = adjustments.iterator();
      PricingAdjustment adj = null;
      RepositoryItem promotion = null;

      while (itAdj.hasNext()) {
        adj = (PricingAdjustment) itAdj.next();
        promotion = adj.getPricingModel();

        if (promotion != null) {

          Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);
          if (isGlobal != null && isGlobal.booleanValue()) {
            continue;
          }

          double adjustedAmount = adj.getTotalAdjustment();
          if (adjustedAmount > 0.0) {
            discountAdjustment += adjustedAmount;
          } else {
            discountAdjustment += (adjustedAmount * -1);
          }

        }
      }
    }
    return discountAdjustment;
  }

  /**
   * In case order does have any qualified items to be applied to the cart but
   * yet user has applied promotion, make sure promotion is picked up from
   * activePromotions and displayed to the user.
   *
   * @param activePromotions
   *          the active promotions
   * @param orderAppliedPromotions
   *          the order applied promotions
   * @return the map
   * @throws ClaimableException
   *           the claimable exception
   */
  @SuppressWarnings("rawtypes")
  private List<AppliedPromotion> applyRemainingActivePromotions(List activePromotions, List<AppliedPromotion> orderAppliedPromotions) throws ClaimableException {

    RepositoryItem[] couponItems = null;
    if (orderAppliedPromotions == null || orderAppliedPromotions.size() == 0) {
      if (orderAppliedPromotions == null) {
        orderAppliedPromotions = new ArrayList<AppliedPromotion>();
      }

      if (activePromotions != null) {

        List<RepositoryItem> userPromotions = new ArrayList<RepositoryItem>();
        RepositoryItem promotionStatus = null;
        RepositoryItem promotion = null;
        for (int i = 0; i < activePromotions.size(); i++) {
          promotionStatus = (RepositoryItem) activePromotions.get(i);

          if (promotionStatus != null && promotionStatus.getPropertyValue(PROMOTION) != null) {
            promotion = (RepositoryItem) promotionStatus.getPropertyValue(PROMOTION);
            Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);
            if (isGlobal != null && isGlobal.booleanValue()) {

            } else {
              userPromotions.add(promotion);
            }

          }
        }
        if (userPromotions.size() > 0) {
          String description = null;
          for (RepositoryItem pPromotion : userPromotions) {
            if (pPromotion != null) {

              if (pPromotion.getPropertyValue(DESCRIPTION) != null) {
                description = (String) pPromotion.getPropertyValue(DESCRIPTION);
              }

              if (StringUtils.isEmpty(description)) {
                description = pPromotion.getRepositoryId();
              }
              
              String promoName = (String) promotion.getPropertyValue(PROMO_DISP_NAME);
              if (StringUtils.isEmpty(promoName)) {
              	promoName = description;
              }
              String promoShortDesc = (String) promotion.getPropertyValue(PROMO_SHORT_DESC);
              if (StringUtils.isEmpty(promoShortDesc)) {
              	promoShortDesc = description;
              }
              
              couponItems = getClaimableTools().getCouponsForPromotion(pPromotion.getRepositoryId());
              if (couponItems != null && !isCouponExists(orderAppliedPromotions, couponItems[0].getRepositoryId())) {
                AppliedPromotion promo = new AppliedPromotion();
                promo.setCouponCode(couponItems[0].getRepositoryId());
                promo.setPromoName(promoName);
                promo.setPromoShortDesc(promoShortDesc);
                promo.setDescription(description);
                promo.setPromoId(promotion.getRepositoryId());
                promo.setDiscountType("");
                orderAppliedPromotions.add(promo);
              }

            }
          }
        }
      }
    }
    return orderAppliedPromotions;
  }
  
  private boolean isCouponExists (List<AppliedPromotion> orderAppliedPromotions, String couponCode) {
	  boolean couponCodeExists = false;
	  Iterator<AppliedPromotion> iter = orderAppliedPromotions.iterator();
	  AppliedPromotion promo=null;
	  while(iter.hasNext()) {
		  promo=iter.next();
		  if(promo!= null & promo.getCouponCode() != null && promo.getCouponCode().equalsIgnoreCase(couponCode)) {
			  couponCodeExists=true;
			  break;
		  }
	  }
	  return couponCodeExists;
  }

  /**
   * Sets the claimable tools.
   *
   * @param pClaimableTools
   *          the new claimable tools
   */
  public void setClaimableTools(ClaimableTools pClaimableTools) {
    mClaimableTools = pClaimableTools;
  }

  /**
   * Gets the claimable tools.
   *
   * @return the claimable tools
   */
  public ClaimableTools getClaimableTools() {
    return mClaimableTools;
  }
}
