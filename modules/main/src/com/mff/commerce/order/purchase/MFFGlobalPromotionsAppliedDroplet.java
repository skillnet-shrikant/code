package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFCommerceItemImpl;

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

/**
 * The Class MFFGlobalPromotionsAppliedDroplet.
 *
 * @author mmane
 */
public class MFFGlobalPromotionsAppliedDroplet extends DynamoServlet {

	/** The Constant ORDER. */
	private static final ParameterName ORDER = ParameterName
			.getParameterName("order");
	
	/** The Constant OUTPUT. */
	private static final ParameterName OUTPUT = ParameterName
			.getParameterName("output");

	/** The description. */
	private final String DESCRIPTION = "description";
	
	private final String PROMO_DISPLAY_NAME = "displayName";
	
	/** The result. */
	private final String RESULT = "result";
	private final String SHIPPING_PROMOS = "shippingPromos";
	
	private final String PROMO_DISCOUNT_AMOUNT = "globalDiscountAmount";
	private final String SHIP_PROMO_TO_DISCNT_AMNT_MAP= "shippingPromoToDiscMap";
	
	private final String TOTAL_DISCOUNT_AMOUNT = "totalDiscountAmount";
	private final String TOTAL_DISCOUNT_AMOUNT_POSITIVE = "totalSavings";
	
	/** The global. */
	private final String GLOBAL = "global";

	/** The Constant ACTIVE_PROMOTIONS. */
	public static final ParameterName ACTIVE_PROMOTIONS = ParameterName
			.getParameterName("activePromotions");

	/**
	 * Service method of a servlet.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("rawtypes")
  public void service(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) throws ServletException,
			IOException {

		Order order = (Order) request.getLocalParameter(ORDER);
		
		double totalDiscountAmount = 0.0d;
		
		if (order == null) {
			if (isLoggingError()) {
				logError("Order is a required parameter to display global level promotions!");
			}
			return;
		}
		List activePromotions = (List) request
				.getObjectParameter(ACTIVE_PROMOTIONS);
		boolean isGlobalPromoExists = false;
		Map<String, String> globalPromotions = new HashMap<String, String>();
		Map<String, Double> globalPromotionToAdjustments = new HashMap<String, Double>();
		/**
		 * First find out Order level promotion
		 */
		if (order.getPriceInfo() != null) {
			List adjustments = (List) order.getPriceInfo().getAdjustments();
			if (adjustments != null) {
				Iterator adjIter = adjustments.iterator();
				while (adjIter.hasNext()) {
					PricingAdjustment pricingAdjustment = (PricingAdjustment) adjIter
							.next();

					if (pricingAdjustment == null) {
						continue;
					}

					RepositoryItem promotion = pricingAdjustment
							.getPricingModel();

					if (promotion != null) {
						Boolean isGlobal = (Boolean) promotion
								.getPropertyValue(GLOBAL);
						
						if (isGlobal != null && isGlobal.booleanValue() ) {
							isGlobalPromoExists = true;
							
							String promotionName = (String) promotion
                  .getPropertyValue(PROMO_DISPLAY_NAME);
							
							String description = (String) promotion
									.getPropertyValue(DESCRIPTION);

							if (StringUtils.isEmpty(description)) {
								description = promotion.getRepositoryId();
							}
							globalPromotions.put(promotionName,description);
							globalPromotionToAdjustments.put(promotionName, pricingAdjustment.getTotalAdjustment() * -1);
							totalDiscountAmount+=pricingAdjustment.getTotalAdjustment();
						}
					}
				}
			}
		}

		// If that didn't pan out, looks at item-level promotion.
		List commerceItems = (List) order.getCommerceItems();
		if (commerceItems != null) {
			Iterator ciIter = commerceItems.iterator();
			while (ciIter.hasNext()) {
				MFFCommerceItemImpl ci = (MFFCommerceItemImpl) ciIter.next();
				ItemPriceInfo priceInfo = ci.getPriceInfo();

				if (priceInfo != null) {

					List adjustments = (List) priceInfo.getAdjustments();
					if (adjustments != null) {
						Iterator adjIter = adjustments.iterator();

						while (adjIter.hasNext()) {
							PricingAdjustment pricingAdjustment = (PricingAdjustment) adjIter
									.next();
							if (pricingAdjustment == null) {
								continue;
							}

							RepositoryItem promotion = pricingAdjustment
									.getPricingModel();

							if (promotion != null) {
								Boolean isGlobal = (Boolean) promotion
										.getPropertyValue(GLOBAL);
								
								if (isGlobal != null && isGlobal.booleanValue() ) {
									isGlobalPromoExists = true;
									
									String promotionName = (String) promotion
		                  .getPropertyValue(PROMO_DISPLAY_NAME);
									
									String description = (String) promotion
											.getPropertyValue(DESCRIPTION);

									if (StringUtils.isEmpty(description)) {
										description = promotion
												.getRepositoryId();
									}
									globalPromotions.put(promotionName,description);
									globalPromotionToAdjustments.put(promotionName, pricingAdjustment.getTotalAdjustment() * -1);
									totalDiscountAmount+=pricingAdjustment.getTotalAdjustment();
								}
							}
						}
					}
				}
			}
		}
		/**
		 * Remove any promotion already present on the profile to avoid
		 * duplicate display
		 */

		if (activePromotions != null && activePromotions.size() > 0
				&& globalPromotions != null && globalPromotions.size() > 0) {

			Iterator it = activePromotions.iterator();
			while (it.hasNext()) {
				RepositoryItem promoStatusItem = (RepositoryItem) it.next();

				if (promoStatusItem == null)
					continue;

				RepositoryItem promoItem = (RepositoryItem) promoStatusItem
						.getPropertyValue("promotion");

				if (promoItem == null)
					continue;
				Boolean isGlobal = (Boolean) promoItem
						.getPropertyValue(GLOBAL);
				
				if(isGlobal != null && isGlobal.booleanValue() && 
						!globalPromotions.containsKey(promoItem.getRepositoryId())) {
				  
				  String promotionName = (String) promoItem
              .getPropertyValue(PROMO_DISPLAY_NAME);
					String description = (String) promoItem
							.getPropertyValue(DESCRIPTION);

					if (StringUtils.isEmpty(description)) {
						description = promoItem
								.getRepositoryId();
					}
					globalPromotions.put(promotionName,description);
				}
			}
		}
		
		Map<String, String> shippingPromotions = new HashMap<String, String>();
		Map<String, Double> shippingPromotionToAdjustments = new HashMap<String, Double>();

		List<?> sgList = order.getShippingGroups();
	      if (sgList.size() >= 1) {
	        Iterator<?> sgiter = sgList.iterator();
	        while (sgiter.hasNext()) {
	          ShippingGroup sg = (ShippingGroup) sgiter.next();
	          ShippingPriceInfo shipPricingInfo = sg.getPriceInfo();
	          if (shipPricingInfo != null) {
	            List adjustments = (List) shipPricingInfo.getAdjustments();
	            if (adjustments != null) {
	            	Iterator shippingAdjIter = adjustments.iterator();
					while (shippingAdjIter.hasNext()) {
						PricingAdjustment pricingAdjustment = (PricingAdjustment) shippingAdjIter
								.next();

						if (pricingAdjustment == null) {
							continue;
						}

						RepositoryItem promotion = pricingAdjustment.getPricingModel();

						if (promotion != null) {
							Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);
							if (isGlobal != null && isGlobal.booleanValue() ) {
								String promotionName = (String) promotion.getPropertyValue(PROMO_DISPLAY_NAME);
								
								String description = (String) promotion.getPropertyValue(DESCRIPTION);
	
								if (StringUtils.isEmpty(description)) {
									description = promotion.getRepositoryId();
								}
								shippingPromotions.put(promotionName,description);
								shippingPromotionToAdjustments.put(promotionName, pricingAdjustment.getTotalAdjustment() * -1);
								totalDiscountAmount+=pricingAdjustment.getTotalAdjustment();
							}
						}
					}
	            }
	          }
	        }
	      }
		
		vlogDebug("totalDiscountAmount: " + totalDiscountAmount);
		

		/**
		 * If Global promotino exists; they will be added to the OUTPUT
		 * servicelocal param,
		 */
		boolean serviceOutput = false;
		if ( (globalPromotions != null && globalPromotions.size() > 0) || isGlobalPromoExists) {
			vlogDebug("globalPromotions: " + globalPromotions.values());
			vlogDebug("globalPromotionToAdjustments: " + globalPromotionToAdjustments.values());
			request.setParameter(RESULT, globalPromotions);
			request.setParameter(PROMO_DISCOUNT_AMOUNT, globalPromotionToAdjustments);
			request.setParameter(TOTAL_DISCOUNT_AMOUNT, totalDiscountAmount);
			request.setParameter(TOTAL_DISCOUNT_AMOUNT_POSITIVE, totalDiscountAmount * -1);
			serviceOutput = true;
		}
		if ( (shippingPromotions != null && shippingPromotions.size() > 0)) {
			
			vlogDebug("shippingPromotions: " + shippingPromotions.values());
			vlogDebug("shippingPromotionToAdjustments: " + shippingPromotionToAdjustments.values());
			request.setParameter(SHIPPING_PROMOS, shippingPromotions);
			request.setParameter(SHIP_PROMO_TO_DISCNT_AMNT_MAP, shippingPromotionToAdjustments);
			serviceOutput = true;
		}
		// output will be served only if there are some discounts to show.
		if(serviceOutput){
			request.serviceLocalParameter(OUTPUT, request, response);
		}
	}
}
