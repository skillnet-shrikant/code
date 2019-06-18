package com.mff.account.order.droplet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import atg.beans.PropertyNotFoundException;
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.PricingException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.pricing.calculators.ShippingSurchargeByItemCalculator;
import com.mff.constants.MFFConstants;

public class HasShippingSurchargeBySkuDroplet extends DynamoServlet {
	
	public static final ParameterName ITEMS = ParameterName.getParameterName("items");
	ShippingSurchargeByItemCalculator mShippingSurchargeBySkuCalculator;

	/**
	 * This method will check if cart has items with shipping surchage.
	 *
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		vlogDebug("HasShippingSurchargeBySkuDroplet - Start.");
		
		Object items = pRequest.getObjectParameter(ITEMS);

		vlogDebug("items: " + items);
		double totalSurchareAmount = 0.0;
	    
	    if(items!=null && (items instanceof Collection)){
	    	List<CommerceItem> commerceItems=(List<CommerceItem>)items;
	      
		      for (int i = 0; i < commerceItems.size(); i++) {
		      	CommerceItem item = commerceItems.get(i);
		      	if (item != null) {
	        	  double surchargeByItem = 0.0;
				try {
					surchargeByItem = getShippingSurchargeBySkuCalculator().getSurchargePerItem(item);
				} catch (PricingException e) {
					if (isLoggingError()){
						logError("service(): PricingException while determining shipping surcharge by item: " +e, e);
					}
				} catch (PropertyNotFoundException e) {
					if (isLoggingError()){
						logError("service(): PropertyNotFoundException while determining shipping surcharge by item: " +e, e);
					}
				}
				
	            totalSurchareAmount = totalSurchareAmount + surchargeByItem;
	            
		      	}	
		      }
	    	}
		
		vlogDebug("service():totalSurcharge: " + totalSurchareAmount);
	    
		if (totalSurchareAmount > 0) {
			vlogDebug("service():hasSurcharge true.");
			pRequest.setParameter("hasSurcharge", true);
			pRequest.setParameter("totalSurcharge", totalSurchareAmount);
			pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
	    } else {
	    	pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
	    }
	    	vlogDebug("HasShippingSurchargeBySkuDroplet - Exitting.");
	  }

	public ShippingSurchargeByItemCalculator getShippingSurchargeBySkuCalculator() {
		return mShippingSurchargeBySkuCalculator;
	}

	public void setShippingSurchargeBySkuCalculator(
			ShippingSurchargeByItemCalculator pShippingSurchargeBySkuCalculator) {
		mShippingSurchargeBySkuCalculator = pShippingSurchargeBySkuCalculator;
	}

}
