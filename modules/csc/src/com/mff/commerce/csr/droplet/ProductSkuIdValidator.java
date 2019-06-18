package com.mff.commerce.csr.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.csr.environment.MFFCSREnvironmentTools;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class ProductSkuIdValidator extends DynamoServlet {

	/* INPUT PARAMETERS */
	static final ParameterName PRODUCT_ID_PARAM = ParameterName.getParameterName("product_id");
	static final ParameterName SKU_ID_PARAM = ParameterName.getParameterName("sku_id");
	static final ParameterName PRODUCT_ID__PRESENT_PARAM = ParameterName.getParameterName("product_id_present");
	static final ParameterName SKU_ID__PRESENT_PARAM = ParameterName.getParameterName("sku_id_present");
	
	
	/* OUTPUT PARAMETERS */
	public static final String new_product_id = "new_product_id";
	public static final String new_sku_id = "new_sku_id";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");
	public static final ParameterName OPARAM_NO_PRODUCT_ID = ParameterName.getParameterName("no_product_id");
	public static final ParameterName OPARAM_NO_SKU_ID = ParameterName.getParameterName("no_sku_id");
	
	private MFFCSREnvironmentTools  mCsrEnvironmentTools;
	private String mIdPropertyName;
	private String mSkuPropertyName;

	
	
	public String getSkuPropertyName() {
		return mSkuPropertyName;
	}



	public void setSkuPropertyName(String pSkuPropertyName) {
		mSkuPropertyName = pSkuPropertyName;
	}



	public String getIdPropertyName() {
		return mIdPropertyName;
	}



	public void setIdPropertyName(String pIdPropertyName) {
		mIdPropertyName = pIdPropertyName;
	}




	public MFFCSREnvironmentTools getCsrEnvironmentTools() {
		return mCsrEnvironmentTools;
	}



	public void setCsrEnvironmentTools(MFFCSREnvironmentTools pCsrEnvironmentTools) {
		mCsrEnvironmentTools = pCsrEnvironmentTools;
	}
	
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException
        {
			vlogDebug("ProductSkuIdValidator : service: Start");
			String product_id=pRequest.getParameter(PRODUCT_ID_PARAM);
			String product_id_present=pRequest.getParameter(PRODUCT_ID__PRESENT_PARAM);
			String sku_id=pRequest.getParameter(SKU_ID_PARAM);
			String sku_id_present=pRequest.getParameter(SKU_ID__PRESENT_PARAM);
			if(getCsrEnvironmentTools()==null){
				pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
				vlogDebug("ProductSkuIdValidator : CSR Environment Tools component value is null : service: End");
				return;
			}
			else {
				if(getCsrEnvironmentTools().isUseProductSearchVariation()){
					Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
					if(searchVariations!=null && !searchVariations.isEmpty()){
						String idPropName=getIdPropertyName();
						String skuPropName=getSkuPropertyName();
						if(product_id_present!=null && !product_id_present.isEmpty()){
							if(product_id_present.trim().equalsIgnoreCase("1")){
								if(product_id!=null&& !product_id.isEmpty()){
									if(idPropName!=null && !idPropName.isEmpty()){
										String new_pid=parsedValue("id",product_id);
										pRequest.setParameter(new_product_id, new_pid);
										pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
										vlogDebug("ProductSkuIdValidator : Id property name is not set but product id value is validated : service : End");
									}
									else {
										String new_pid=parsedValue(idPropName,product_id);
										pRequest.setParameter(new_product_id, new_pid);
										pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
										vlogDebug("ProductSkuIdValidator : Id property name is set and product id value is validated : service");
									}
								}
								else {
									pRequest.serviceLocalParameter(OPARAM_NO_PRODUCT_ID, pRequest, pResponse);
									vlogDebug("ProductSkuIdValidator : Product id input value is not present : service");
								}
							}
							else {
								
								pRequest.serviceLocalParameter(OPARAM_NO_PRODUCT_ID, pRequest, pResponse);
								vlogDebug("ProductSkuIdValidator : Product id variation is present but value is not 1 : service");
							}
						}
						else {
							pRequest.serviceLocalParameter(OPARAM_NO_PRODUCT_ID, pRequest, pResponse);
							vlogDebug("ProductSkuIdValidator : Product id variation is not present : service");
						}
						if(sku_id_present!=null && !sku_id_present.isEmpty()){
							if(sku_id_present.trim().equalsIgnoreCase("1")){
								if(sku_id!=null&& !sku_id.isEmpty()){
									if(skuPropName!=null && !skuPropName.isEmpty()){
										String new_skuid=parsedValue("sku",sku_id);
										pRequest.setParameter(new_sku_id, new_skuid);
										pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
										vlogDebug("ProductSkuIdValidator : Sku property name is not set but sku id value is validated : service");
									}
									else {
										String new_skuid=parsedValue(skuPropName,sku_id);
										pRequest.setParameter(new_sku_id, new_skuid);
										pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
										vlogDebug("ProductSkuIdValidator : Sku property name is set and sku id value is validated : service");
									}
								}
								else {
									pRequest.serviceLocalParameter(OPARAM_NO_SKU_ID, pRequest, pResponse);
									vlogDebug("ProductSkuIdValidator : sku id input value is not present : service");
								}
							}
							else {
								pRequest.serviceLocalParameter(OPARAM_NO_SKU_ID, pRequest, pResponse);
								vlogDebug("ProductSkuIdValidator : sku id variation is present but value is not 1 : service");
							}
						}
						else {
							pRequest.serviceLocalParameter(OPARAM_NO_SKU_ID, pRequest, pResponse);
							vlogDebug("ProductSkuIdValidator : sku id variation is not present : service");
						}
					}
					else {
						pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
						vlogDebug("ProductSkuIdValidator : Search Variation length configs map is empty : service: End");
						return;
					}
				}
				else {
					pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
					vlogDebug("ProductSkuIdValidator : Search Variation are not used : service: End");
					return;
				}
			}
			
			vlogDebug("ProductSkuIdValidator : service: End");
			
      }
	

	private String parsedValue(String propName, String currValue){
		vlogDebug("ProductSkuIdValidator : parsedValue: Start");
		String retValue=currValue;
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		vlogDebug("ProductSkuIdValidator : propertyModified: "+propName+" : current value: "+currValue);
		if(searchVariations!=null&&currValue!=null&&!searchVariations.isEmpty() && !currValue.isEmpty()){
			String lengthField=searchVariations.get(propName);
			if(lengthField==null||lengthField.isEmpty()){
				lengthField="0";
			}
			int validLength=Integer.parseInt(lengthField);
			if(validLength!=0){
				
				int valLength=validLength;
				int currLength=currValue.length();
				if(valLength>currLength){
					int zerosToAppend=valLength-currLength;
					String zeros="";
					for(int i=0;i<zerosToAppend;i++){
						zeros+="0";
					}
					retValue=zeros+currValue;
				}
				
			}
			
		}
		vlogDebug("ProductSkuIdValidator : propertyModified: "+propName+" : current value: "+retValue);
		vlogDebug("ProductSkuIdValidator : parsedValue: End");
		return retValue;
	}
}
