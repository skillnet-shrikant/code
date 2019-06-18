package com.mff.account.order.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.pricing.calculators.ShippingCostHelper;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;

public class HasOversizeItemDroplet extends DynamoServlet {

	ShippingCostHelper shippingCostHelper;
	
	public ShippingCostHelper getShippingCostHelper() {
		return shippingCostHelper;
	}

	public void setShippingCostHelper(ShippingCostHelper pShippingCostHelper) {
		shippingCostHelper = pShippingCostHelper;
	}

	/**
	 * This method will check if item has Over size handling fee applicable.
	 *
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		CommerceItem commerceItem = (CommerceItem) pRequest
				.getObjectParameter("commerceItem");

		vlogDebug("HasOversizeItemDroplet: commerceItem: " + commerceItem);

		
		/* Commented for BZ 3063
		
		MFFPricingUtil pricingUtil = new MFFPricingUtil();
		boolean isOverSize = pricingUtil.isOverSized(commerceItem); */
		
		// BZ 3063 changes
		boolean isOverSize = getShippingCostHelper().isOverSized(commerceItem);
		
		vlogDebug("HasOversizeItemDroplet: isOverSize: " + isOverSize);

		pRequest.setParameter("isOverSize", isOverSize);
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
	}

}
