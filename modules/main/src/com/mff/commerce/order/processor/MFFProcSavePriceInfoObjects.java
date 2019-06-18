package com.mff.commerce.order.processor;

import java.beans.IntrospectionException;

import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.ProcSavePriceInfoObjects;
import atg.core.util.ResourceUtils;
import atg.repository.ConcurrentUpdateException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;

public class MFFProcSavePriceInfoObjects  extends ProcSavePriceInfoObjects {
	
	static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

	/** Resource Bundle **/
	private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

	  //-------------------------------------
	
	//-----------------------------------------------
		/**
		 * This method has been overriden to allow for the TaxPriceInfos on the commerceItem to also be 
		 * created at this time. Based on pPropertyName past in this method will create either a taxPriceInfo
		 * or a dutyTaxPriceInfo.  Both are of type taxPriceInfo.  
		 *
		 * @param order The order being saved
		 * @param cItem The commerce item object
		 * @param cItemRepItem The repository item corresponding to the commerce item
		 * @param mutRep The repository where the order is being saved
		 * @param orderManager The OrderManager from the pipeline params
		 * @throws RepositoryException
		 * @throws IntrospectionException
		 * @throws PropertyNotFoundException
		 * @throws CommerceException
		 **/
		protected void saveItemPriceInfo(Order order, CommerceItem cItem, MutableRepositoryItem cItemRepItem,
				MutableRepository mutRep, OrderManager orderManager)
		throws RepositoryException, IntrospectionException, PropertyNotFoundException,
		CommerceException
		{
			if(isLoggingDebug())
				logDebug("In saveItemPriceInfo");

			//  call the super method to do the regular stuff
			super.saveItemPriceInfo(order, cItem, cItemRepItem, mutRep, orderManager);

			if(isLoggingDebug())
				logDebug("In saveItemPriceInfo -- taxPriceInfo");

			//called this one first as the updateItem call is made in the default saveItemPriceInfo method
			saveItemTaxPriceInfo(order,cItem, cItemRepItem,mutRep, orderManager, "taxPriceInfoRepositoryItem", getTaxPriceInfoProperty());


		}
		
//		-----------------------------------------------
		/**
		 * This method creates a taxPriceInfo objects and saves it on the commerceItem passed in.
		 * 
		 * This method is called by default saveItemPriceInfo().
		 *
		 * @param order The order being saved
		 * @param cItem
		 * @param ciRepItem The repository item corresponding to the order
		 * @param mutRep The repository where the order is being saved
		 * @param orderManager The OrderManager from the pipeline params
		 * @param pPropertyName the property to associate the newly created taxPriceInfo too (this is the memory object)
		 * @param pPropertyValue the property in the repository that the newly created taxPriceInfo should be set to
		 * @throws RepositoryException
		 * @throws IntrospectionException
		 * @throws PropertyNotFoundException
		 * @throws CommerceException
		 **/
		protected void saveItemTaxPriceInfo(Order order,CommerceItem cItem, MutableRepositoryItem ciRepItem, MutableRepository mutRep, OrderManager orderManager, String pPropertyName, String pPropertyValue)throws RepositoryException, IntrospectionException, PropertyNotFoundException, CommerceException {
			if(isLoggingDebug())
				logDebug("In saveItemTaxPriceInfo -- pPropertyName: "+ pPropertyName);

			MutableRepositoryItem tpiRepItem = null;
			boolean hasProperty = false;

			if (DynamicBeans.getBeanInfo(cItem).hasProperty(pPropertyName)) {
				hasProperty = true;
				tpiRepItem = (MutableRepositoryItem) DynamicBeans.getPropertyValue(cItem, pPropertyName);
			}

			if (tpiRepItem == null) {
				tpiRepItem = (MutableRepositoryItem) ciRepItem.getPropertyValue(pPropertyValue);
				if (hasProperty)
					DynamicBeans.setPropertyValue(cItem, pPropertyName, tpiRepItem);
			}

			if(pPropertyValue.equals("taxPriceInfo")){
				if(isLoggingDebug())
					logDebug("should be item, pPropertyValue: "+ pPropertyValue+ " hasProperty: "+ hasProperty);
				tpiRepItem = savePriceInfo(order, cItem, tpiRepItem, (hasProperty ? pPropertyName : null),
						((MFFCommerceItemImpl)cItem).getTaxPriceInfo(), mutRep, orderManager);                      
			}

			ciRepItem.setPropertyValue(pPropertyValue, tpiRepItem);

			try {
				mutRep.updateItem(ciRepItem);
			}
			catch (ConcurrentUpdateException e) {
				String[] msgArgs = { order.getId(), ciRepItem.getItemDescriptor().getItemDescriptorName() };
				throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
						MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
			}

			if (cItem instanceof ChangedProperties) {
				ChangedProperties cp = (ChangedProperties) cItem;
				cp.clearChangedProperties();
				cp.setSaveAllProperties(false);
			}
		}

}
