package com.mff.commerce.order.processor;

import atg.beans.DynamicBeans;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.CommerceItemContainer;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.ProcLoadPriceInfoObjects;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItemDescriptor;

public class MFFProcLoadPriceInfoObjects extends ProcLoadPriceInfoObjects {

	 //-----------------------------------------------
	/**
	 * This method has been overriden to allow for the TaxPriceInfos on the commerceItem to also be 
	 * set at this time. Based on pPropertyName past in this method will load either a taxPriceInfo
	 * dutyTaxPriceInfo or giftWrapTaxPriceInfo.  All of these objects are of type taxPriceInfo.  
	 * 
	 * @param order The order whose item's price info is being loaded
	 * @param ci The commerce item whose price is being loaded
	 * @param mutItem The repository item for the commerce item
	 * @param orderManager The OrderManager that was in the pipeline params
	 * @param invalidateCache If true, then the item's price info repository cache
	 *                        entry is invalidated
	 * @throws Exception
	 **/
	protected void loadItemPriceInfo(Order order, CommerceIdentifier ci,
			MutableRepositoryItem mutItem, OrderManager orderManager, Boolean invalidateCache) throws Exception	{


		if(isLoggingDebug())
			logDebug("In loadItemPriceInfo");  
		//call the super method to do the default stuff
		super.loadItemPriceInfo(order, ci, mutItem, orderManager, invalidateCache);


		//call the new method to load in the taxpriceinfo now as well
		if(isLoggingDebug())
			logDebug("In loadItemPriceInfo - taxPriceInfo");  
		loadItemTaxPriceInfos(order,ci,mutItem, orderManager, invalidateCache, "taxPriceInfoRepositoryItem", getTaxPriceInfoProperty());


	}

	//-----------------------------------------------
	/**
	 * This method loads the TaxPriceInfo objects of a CommerceItem. 
	 * 
	 * @param order The order whose item's price info is being loaded
	 * @param ci The commerce item whose price is being loaded
	 * @param mutItem The repository item for the commerce item
	 * @param orderManager The OrderManager that was in the pipeline params
	 * @param invalidateCache If true, then the item's price info repository cache
	 *                        entry is invalidated
	 * @param pPropertyName the name of the property to load ( the memory object)
	 * @param pPropertyValue the property in the repository that the newly created taxPriceInfo should be set to 
	 * @throws Exception
	 **/
	protected void loadItemTaxPriceInfos(Order order, CommerceIdentifier ci,
			MutableRepositoryItem mutItem, OrderManager orderManager, Boolean invalidateCache, 
			String pPropertyName, String pPropertyValue)
	throws Exception
	{
		if(isLoggingDebug())
			logDebug("In loadItemTaxPriceInfos -- pPropertyName: "+ pPropertyName);

		MutableRepositoryItem piRepItem = (MutableRepositoryItem) mutItem.getPropertyValue(pPropertyValue);
		Object amtInfo = null;

		piRepItem = (MutableRepositoryItem) mutItem.getPropertyValue(pPropertyValue);
		if (piRepItem == null) {
			if (DynamicBeans.getBeanInfo(ci).hasProperty(pPropertyName))
				DynamicBeans.setPropertyValue(ci, pPropertyName, piRepItem);
			DynamicBeans.setPropertyValue(ci, pPropertyValue, amtInfo);
		}
		else {
			RepositoryItemDescriptor desc = piRepItem.getItemDescriptor();
			if (invalidateCache.booleanValue())
				invalidateCache((ItemDescriptorImpl) desc, piRepItem);
			String className = orderManager.getOrderTools().getMappedBeanName(desc.getItemDescriptorName());
			amtInfo = Class.forName(className).newInstance();
			if (DynamicBeans.getBeanInfo(ci).hasProperty(pPropertyName))
				DynamicBeans.setPropertyValue(ci, pPropertyName, piRepItem);
			DynamicBeans.setPropertyValue(ci, pPropertyValue, amtInfo);
			readProperties(order, amtInfo, getLoadProperties(), piRepItem, desc, orderManager);
			loadPricingAdjustments(order, amtInfo, piRepItem, orderManager, invalidateCache);
			loadShippingItemsTaxPriceInfos(order, amtInfo, piRepItem, orderManager, invalidateCache);
		}

		if (ci instanceof ChangedProperties)
			((ChangedProperties) ci).clearChangedProperties();

		/* If the item is a configurable SKU, then load the priceinfo for the subskus */
		if(ci instanceof CommerceItemContainer) {
			if(isLoggingDebug())
				logDebug("item is configurable sku - iterate thru its subskus to load them " + mutItem);
			loadSubSkuPriceInfo(order, ci, mutItem, orderManager, invalidateCache);
		}

	}
	
}

