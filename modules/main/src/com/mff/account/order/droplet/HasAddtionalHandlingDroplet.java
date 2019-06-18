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

public class HasAddtionalHandlingDroplet extends DynamoServlet {
	
	ShippingCostHelper shippingCostHelper;

	public ShippingCostHelper getShippingCostHelper() {
		return shippingCostHelper;
	}

	public void setShippingCostHelper(ShippingCostHelper pShippingCostHelper) {
		shippingCostHelper = pShippingCostHelper;
	}

	/**
	 * This method will check if item has additional handling fee applicable.
	 *
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		CommerceItem commerceItem = (CommerceItem) pRequest
				.getObjectParameter("commerceItem");

		vlogDebug("HasAddtionalHandlingDroplet: commerceItem: " + commerceItem);

		//BZ 3063 changes
		//MFFPricingUtil pricingUtil = new MFFPricingUtil();
		boolean isLongLight = false;
		
		if(!getShippingCostHelper().isOverSized(commerceItem)) {
			isLongLight = getShippingCostHelper().isAdditionalHandling(commerceItem);
		}

		vlogDebug("HasAddtionalHandlingDroplet: isLongLight: " + isLongLight);

		pRequest.setParameter("isLongLight", isLongLight);
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
	}

}
