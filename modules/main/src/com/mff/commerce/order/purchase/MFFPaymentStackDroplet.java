package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.claimable.ClaimableException;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingAdjustment;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.promotion.MffPromotionTools;
import com.mff.constants.MFFConstants;

/**
 * The Class MFFPaymentStackDroplet will return infromation to render the Payment stack.
 *
 * @author DMI
 */

public class MFFPaymentStackDroplet extends DynamoServlet {
	
	private static final ParameterName ORDER = ParameterName.getParameterName("order");
	public static final ParameterName ACTIVE_PROMOTIONS = ParameterName.getParameterName("activePromotions");
	private static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	
	private MffPromotionTools mPromotionTools;

	/**
	 * Service method of a servlet.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("rawtypes")
	public void service(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) throws ServletException,
			IOException {
		
		boolean isGlobalPromoExists = false;
		
		Order order = (Order) request.getLocalParameter(ORDER);
		if (order == null) {
			if (isLoggingError()) {
				logError("Order is a required parameter to display user.");
			}
			return;
		}
		List activePromotions = (List) request.getObjectParameter(ACTIVE_PROMOTIONS);

		List<PricingAdjustment> orderPromos = new ArrayList<PricingAdjustment>();
		List<PricingAdjustment> taxPromos = new ArrayList<PricingAdjustment>();
		List<PricingAdjustment> itemPromos = new ArrayList<PricingAdjustment>();
		List<PricingAdjustment> shippingPromos = new ArrayList<PricingAdjustment>();

		// Passing true to get PricingAdjustments
		getPromotionTools().getOrderPromotions(order, orderPromos, taxPromos,
				itemPromos, shippingPromos, true);
		
		Map<String, String> globalPromotions = new HashMap<String, String>();
		Map<String, Double> globalPromotionToAdjustments = new HashMap<String, Double>();
		Map<String, String> shippingPromotions = new HashMap<String, String>();
		Map<String, Double> shippingPromotionToAdjustments = new HashMap<String, Double>();
		List<AppliedPromotion> orderAppliedPromotions = new ArrayList<AppliedPromotion>();

		try {

			if (orderPromos != null) {
				getPromotionTools().getPromotionFromAdjustment(orderPromos,
						orderAppliedPromotions,
						globalPromotions, globalPromotionToAdjustments,
						shippingPromotions, shippingPromotionToAdjustments, "order");
				isGlobalPromoExists = getPromotionTools().isGlobalPromoExists(orderPromos);
				vlogDebug("isGlobalPromoExists in orderPromos: " + isGlobalPromoExists);
			}

			if (shippingPromos != null) {
				getPromotionTools().getPromotionFromAdjustment(shippingPromos,
						orderAppliedPromotions,
						globalPromotions, globalPromotionToAdjustments,
						shippingPromotions, shippingPromotionToAdjustments, "shipping");
				
				if (!isGlobalPromoExists){
					isGlobalPromoExists = getPromotionTools().isGlobalPromoExists(shippingPromos);
					vlogDebug("isGlobalPromoExists in shippingPromos: " + isGlobalPromoExists);
				}
			}

			if (itemPromos != null) {
				getPromotionTools().getPromotionFromAdjustment(itemPromos,
						orderAppliedPromotions,
						globalPromotions, globalPromotionToAdjustments,
						shippingPromotions, shippingPromotionToAdjustments, "item");
				
				if (!isGlobalPromoExists){
					isGlobalPromoExists = getPromotionTools().isGlobalPromoExists(itemPromos);
					vlogDebug("isGlobalPromoExists in itemPromos: " + isGlobalPromoExists);
				}
				
			}

			getPromotionTools().applyRemainingActivePromotions(activePromotions, orderAppliedPromotions);

		} catch (ClaimableException e) {
			if (isLoggingError()) {
				logError("Some error in fetching coupons applied: ", e);
			}
		}
		
		if (orderAppliedPromotions != null && orderAppliedPromotions.size() > 0) {
			request.setParameter(MFFConstants.COUPON_PROMOS, orderAppliedPromotions);
		}

		/**
		 * If Global promotino exists; they will be added to the OUTPUT
		 * servicelocal param,
		 */
		
		if ( (globalPromotions != null && globalPromotions.size() > 0) || isGlobalPromoExists) {
			vlogDebug("globalPromotions: " + globalPromotions.values());
			vlogDebug("globalPromotionToAdjustments: " + globalPromotionToAdjustments.values());
			request.setParameter(MFFConstants.GLOBAL_PROMOS, globalPromotions);
			request.setParameter(MFFConstants.PROMO_DISCOUNT_AMOUNT, globalPromotionToAdjustments);
		}
		if ( (shippingPromotions != null && shippingPromotions.size() > 0)) {
			
			vlogDebug("shippingPromotions: " + shippingPromotions.values());
			vlogDebug("shippingPromotionToAdjustments: " + shippingPromotionToAdjustments.values());
			request.setParameter(MFFConstants.SHIPPING_PROMOS, shippingPromotions);
			request.setParameter(MFFConstants.SHIP_PROMO_TO_DISCNT_AMNT_MAP, shippingPromotionToAdjustments);
		}
		
		vlogDebug("isGlobalPromoExists: " + isGlobalPromoExists);
		request.setParameter(MFFConstants.IS_GLOBAL_PROMO_EXISTS, isGlobalPromoExists);

		request.serviceLocalParameter(OUTPUT, request, response);
	}

	public MffPromotionTools getPromotionTools() {
		return mPromotionTools;
	}

	public void setPromotionTools(MffPromotionTools pPromotionTools) {
	    mPromotionTools = pPromotionTools;
	}
}
