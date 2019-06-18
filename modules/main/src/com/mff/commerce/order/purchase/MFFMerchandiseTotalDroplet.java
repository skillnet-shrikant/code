package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingTools;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.constants.MFFConstants;

public class MFFMerchandiseTotalDroplet extends DynamoServlet {

	private static final ParameterName ORDER = ParameterName.getParameterName("order");
	private static final String MERCHANDISE_TOTAL = "merchandiseTotal";
	private static final String ROUNDED_MERCHANDISE_TOTAL = "roundedMerchandiseTotal";
	private final String TOTAL_DISCOUNT_AMOUNT_POSITIVE = "totalSavings";
	
	public void service(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		Order order = (Order) pRequest.getLocalParameter(ORDER);
		String ignoreSavings = (String) pRequest.getParameter("ignoreSavings");
		String roundMerchTotal = (String) pRequest.getParameter("roundMerchTotal");
		
		if (order == null) {
			if (isLoggingError()) {
				logError("Order is a required parameter to display merchandise total.");
			}
			return;
		}
		
		double merchandiseTotal = 0.0d;
		double roundedMerchandiseTotal= 0.0d;
		double totalDiscountAmount = 0.0d;
		double totalLinePrice = 0.0d;
		
		List commerceItems = (List) order.getCommerceItems();
		
		if (commerceItems != null && commerceItems.size() > 0) {
			
	        Iterator ciIter = commerceItems.iterator();
			while (ciIter.hasNext()) {
	        	CommerceItem commerceItem = (CommerceItem) ciIter.next();
	        	
	        	if (commerceItem != null) {
	    			ItemPriceInfo priceInfo = (ItemPriceInfo)commerceItem.getPriceInfo();
	    			
					if (priceInfo != null) {
	    				long quantity = commerceItem.getQuantity();
	    				double listPrice = priceInfo.getListPrice();
	    				double salePrice = priceInfo.getSalePrice();
	    				double priceToUse = listPrice;
	    				
	    				if (salePrice > 0 && salePrice < listPrice) {
	    					priceToUse = salePrice;
	    				}
	    				totalLinePrice = priceToUse * quantity;
	    				// 2414 - GWP item price to be displayed
	    				if((Boolean)((MFFCommerceItemImpl)commerceItem).getPropertyValue("gwp")) {
	    					totalLinePrice = priceInfo.getAmount();
	    				}	    				
	    				merchandiseTotal = merchandiseTotal + totalLinePrice;
	    				
    					vlogDebug("CommerceItem:" + commerceItem.getId() + ", listPrice:" + listPrice + ", salePrice:"
    							+ salePrice);
    					vlogDebug("priceToUse:" + priceToUse);
    					vlogDebug("totalLinePrice:" + totalLinePrice);
	    			}
	    		}
	        }
	      }

		
		vlogDebug("merchandiseTotal:" + merchandiseTotal);
		
		if ("yes".equalsIgnoreCase(roundMerchTotal) && merchandiseTotal > 0) {
			roundedMerchandiseTotal= PricingTools.getPricingTools().round(merchandiseTotal);
		}
		
		if (!"yes".equalsIgnoreCase(ignoreSavings)) {
		
			// adding order level promos total.
			if (order.getPriceInfo() != null) {
				List adjustments = (List) order.getPriceInfo().getAdjustments();
				if (adjustments != null) {
					Iterator adjIter = adjustments.iterator();
					while (adjIter.hasNext()) {
						PricingAdjustment pricingAdjustment = (PricingAdjustment) adjIter.next();
	
						if (pricingAdjustment == null) {
							continue;
						}
	
						RepositoryItem promotion = pricingAdjustment.getPricingModel();
	
						if (promotion != null) {
							totalDiscountAmount+=pricingAdjustment.getTotalAdjustment();
						}
					}
				}
			}
	
			// adding item level promos total.
			
			if (commerceItems != null) {
				Iterator ciIter = commerceItems.iterator();
				while (ciIter.hasNext()) {
					MFFCommerceItemImpl ci = (MFFCommerceItemImpl) ciIter.next();
					// 2414 - Do not factor GWP promotions in the pay stack. The free items are already shown in the cart
					if(!(Boolean)ci.getPropertyValue("gwp")) {
						ItemPriceInfo priceInfo = ci.getPriceInfo();

						if (priceInfo != null) {

							List adjustments = (List) priceInfo.getAdjustments();
							if (adjustments != null) {
								Iterator adjIter = adjustments.iterator();

								while (adjIter.hasNext()) {
									PricingAdjustment pricingAdj = (PricingAdjustment) adjIter.next();
									if (pricingAdj == null) {
										continue;
									}

									RepositoryItem promotion = pricingAdj.getPricingModel();

									if (promotion != null) {
										totalDiscountAmount+=pricingAdj.getTotalAdjustment();
									}
								}
							}
						}
					}
				}
			}
			
			vlogDebug("totalDiscountAmount: " + totalDiscountAmount);
			if (totalDiscountAmount == 0) {
				pRequest.setParameter(TOTAL_DISCOUNT_AMOUNT_POSITIVE, Double.valueOf("-0.0") * -1);
			} else {
				pRequest.setParameter(TOTAL_DISCOUNT_AMOUNT_POSITIVE, totalDiscountAmount * -1);
			}
		}
		
		pRequest.setParameter(ROUNDED_MERCHANDISE_TOTAL, roundedMerchandiseTotal);
		pRequest.setParameter(MERCHANDISE_TOTAL, merchandiseTotal);
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
	}
}
