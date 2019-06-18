package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.result.PriceAdjustment;
import atg.commerce.promotion.PromotionTools;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.constants.MFFConstants;

public class LineItemTotalPriceDroplet extends DynamoServlet {
	
	private static final String COMMERCE_ITEM_PARAM = "commerceItem";
	private static final String TOTAL_LINE_ITEM_PRICE = "totalLinePrice";
	private static final String PROMOS_APPLIED_TO_LINE= "lineItemPromos";
	private static final String PRICING_ADJUSTMENT_ITEM_DISCOUNT_DESCR = "Item Discount";
	
	PromotionTools promotionTools;
	
	
	
	public PromotionTools getPromotionTools() {
		return promotionTools;
	}



	public void setPromotionTools(PromotionTools pPromotionTools) {
		promotionTools = pPromotionTools;
	}



	public void service(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		double totalLinePrice = 0.0;
		ArrayList lineItemPromos = new ArrayList();
		
		// Get the input param
		CommerceItem commerceItem = (CommerceItem)pRequest.getObjectParameter(COMMERCE_ITEM_PARAM);
		if (commerceItem!=null){
			ItemPriceInfo priceInfo = (ItemPriceInfo)commerceItem.getPriceInfo();
			
			// 2414 - Display gwp promo message with the qualifier & not with the free item
			if( ((MFFCommerceItemImpl)commerceItem).getGwpPromoId() != null) {
				try {
					if(getPromotionTools().getPromotions() != null) {
						lineItemPromos.add(getPromotionTools().getPromotions().getItem(((MFFCommerceItemImpl)commerceItem).getGwpPromoId(), "promotion"));
					}
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
			
			if (priceInfo!=null){
				long quantity = commerceItem.getQuantity();
				double listPrice = priceInfo.getListPrice();
				double salePrice = priceInfo.getSalePrice();
				double priceToUse = listPrice;
				
				if (salePrice > 0 && salePrice < listPrice) {
					priceToUse = salePrice;
				}

				totalLinePrice = priceToUse * quantity;
				
				ItemPriceInfo itemPriceInfo = commerceItem.getPriceInfo();
				List<PriceAdjustment> adjustments = itemPriceInfo.getAdjustments();
		        

				Iterator adjIter = adjustments.iterator();

			    while (adjIter.hasNext()) {
			      PricingAdjustment pricingAdjustment = (PricingAdjustment) adjIter.next();
			      if (pricingAdjustment == null) {
			        continue;
			      }
			      if (PRICING_ADJUSTMENT_ITEM_DISCOUNT_DESCR.equals(pricingAdjustment.getAdjustmentDescription())) {
			    	  // 2414 - Do not display GWP promo with gwp item. This is displayed on the qualifier
			    	  if(!(Boolean)((MFFCommerceItemImpl)commerceItem).getPropertyValue("gwp")) {
			    		  lineItemPromos.add(pricingAdjustment.getPricingModel());
			    	  }
			    	  
			    	  //2414 - Display the GWP amount instead of its list/sale prices
			    	  if((Boolean)((MFFCommerceItemImpl)commerceItem).getPropertyValue("gwp")) {
			    		  totalLinePrice = priceInfo.getAmount();
			    	  }
					}
			    }
				
				if (isLoggingDebug()) {
					logDebug("CommerceItem:" + commerceItem.getId() + ", listPrice:" + listPrice + ", salePrice:"
							+ salePrice);
					logDebug("priceToUse:" + priceToUse);
					logDebug("totalLinePrice:" + totalLinePrice);
					logDebug("lineItemPromos:" + lineItemPromos);
				}
			}
		} else {
			if (isLoggingWarning()) logWarning("Commerce Item is null, hence could not display price");
		}

		pRequest.setParameter(TOTAL_LINE_ITEM_PRICE, totalLinePrice);
		pRequest.setParameter(PROMOS_APPLIED_TO_LINE, lineItemPromos);
		
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
	}
}
