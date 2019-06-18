package com.mff.browse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.constants.MFFConstants;

/**
 * This class is used to get the dynamic attributes from SKU
 * @author DMI
 *
 */
public class MFFDynamicAttributes extends DynamoServlet {
	
	private Repository mRepository;

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return mRepository;
	}

	/**
	 * @param pRepository the repository to set
	 */
	public void setRepository(Repository pRepository) {
		mRepository = pRepository;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("MFFDynamicAttributes :: service :: START");
		String productId = pRequest.getParameter("productId");
		String attributeName = pRequest.getParameter("nameOfAttribute");
		RepositoryItem product = null;
		List<RepositoryItem> skus = null;
		Map<String, String> dynamicAttributes = null;
		Map<String, String> prodDynamicAttributes = null;
		String pickerName = null;
		Map<String, String> attributes = new HashMap<String, String>();
		
		if(!StringUtils.isBlank(productId) && !StringUtils.isBlank(attributeName)){
			try {
				product = getRepository().getItem(productId, "product");
				if(product.getPropertyValue("dynamicAttributes") != null){
					prodDynamicAttributes = (Map<String, String>) product.getPropertyValue("dynamicAttributes");
				}
				if(!prodDynamicAttributes.isEmpty()){
					pickerName = prodDynamicAttributes.get(attributeName);
				}
				skus = (List<RepositoryItem>) product.getPropertyValue("childSKUs");
				for (RepositoryItem sku : skus) {
					if(sku.getPropertyValue("dynamicAttributes") != null){
						dynamicAttributes = (Map<String, String>) sku.getPropertyValue("dynamicAttributes");
					}
					if(dynamicAttributes != null && dynamicAttributes.size() > 0){
						if(!StringUtils.isBlank(dynamicAttributes.get(pickerName))){
							attributes.put(sku.getRepositoryId(), dynamicAttributes.get(pickerName));
						}
					}
				}
				if(attributes.size() > 0 ){
					pRequest.setParameter("attributes", attributes);
					pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
				} else {
					pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
				}
				vlogDebug("MFFDynamicAttributes :: service :: END");
			} catch (RepositoryException e) {
				vlogError("RepositoryException occurred while getting Dynamic attributes ::"+e.getMessage());
			}
		}
	}
}
