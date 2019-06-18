package com.mff.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.constants.MFFConstants;

/**
 * This droplet will return dynamic attributes for an input SKU.
 * @author DMI
 *
 */
public class MFFDynamicAttributesBySkuDroplet extends DynamoServlet {
	
	private static final ParameterName SKU = ParameterName.getParameterName("sku");
	private static final ParameterName PRODUCT = ParameterName.getParameterName("product");
	
	private Repository mRepository;
	
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		vlogDebug("service: Called.");
		
		RepositoryItem skuObj = (RepositoryItem) pRequest.getObjectParameter(SKU);
		RepositoryItem productObj = (RepositoryItem) pRequest.getObjectParameter(PRODUCT);
		 
		Map<String, String> outputDynAttributes = new HashMap<String, String>();
		Map<String, String> prodDynamicAttributes= null;
		
		if (productObj!=null){
			prodDynamicAttributes = (Map<String, String>) productObj.getPropertyValue("dynamicAttributes");
		}
		
		if(prodDynamicAttributes != null && prodDynamicAttributes.size() > 0){
			
			Map<String, String> skuDynamicAttributes = null;
			
			if (skuObj!=null){
				skuDynamicAttributes = (Map<String, String>) skuObj.getPropertyValue("dynamicAttributes");
			}
			
			if (skuDynamicAttributes != null && skuDynamicAttributes.size() > 0) {
			 Set<String> keys = prodDynamicAttributes.keySet();
			 vlogDebug("service: keys: " + keys);
		        for(String key: keys){
		        	outputDynAttributes.put(prodDynamicAttributes.get(key), skuDynamicAttributes.get(key));
		        }
			}
		}
		
		vlogDebug("service: dynAttributes: " + outputDynAttributes);

		if (outputDynAttributes.size() > 0) {
			pRequest.setParameter("dynAttributes", outputDynAttributes);
			pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
		} else {
			pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
		}
		
	}
	
	public Repository getRepository() {
		return mRepository;
	}
	
	public void setRepository(Repository pRepository) {
		mRepository = pRepository;
	}
}