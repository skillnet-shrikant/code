package com.mff.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
/**
 * This Util helps in retrieving product/sku dynamic attributes and its values
 *  
 * @author DMI
 *
 */
public class MFFDynamicAttributesUtil extends GenericService {

	private Repository mProductRepository;

	/**
	 * @return the repository
	 */
	public Repository getProductRepository() {
		return mProductRepository;
	}

	/**
	 * @param pRepository the repository to set
	 */
	public void setProductRepository(Repository pProductRepository) {
		mProductRepository = pProductRepository;
	}
	
	/**
	 * This method returns unique values of a specific attribute for a product
	 * params: pProductId, pAttributeName
	 * returns : Collection
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection getProductAttributeValues(String pProductId, String pAttributeName) throws RepositoryException{
		Map<String, String> prodDynamicAttributes = null;
		Map<String, String> dynamicAttributes = null;
		String pickerKey = null;
		Set mAttributeValues=new HashSet();
		RepositoryItem product = getProductRepository().getItem(pProductId, "product");
		if(product.getPropertyValue("dynamicAttributes") != null){
			prodDynamicAttributes = (Map<String, String>) product.getPropertyValue("dynamicAttributes");
		}
		if(prodDynamicAttributes!=null && !prodDynamicAttributes.isEmpty()){
			for (Iterator keyItr = prodDynamicAttributes.keySet().iterator(); keyItr.hasNext();) {
				String attributeKey=(String)keyItr.next();
				if(pAttributeName.equalsIgnoreCase(prodDynamicAttributes.get(attributeKey))){
					pickerKey=attributeKey;
				}
			}
		}
		List<RepositoryItem> skus = (List<RepositoryItem>) product.getPropertyValue("childSKUs");
		for (RepositoryItem sku : skus) {
			if(sku.getPropertyValue("dynamicAttributes") != null){
				dynamicAttributes = (Map<String, String>) sku.getPropertyValue("dynamicAttributes");
			}
			if(dynamicAttributes != null && dynamicAttributes.size() > 0 && pickerKey!=null){
				if(!StringUtils.isBlank(dynamicAttributes.get(pickerKey))){
					mAttributeValues.add(dynamicAttributes.get(pickerKey));
				}
			}
		}
		return mAttributeValues;
	}
	
}
