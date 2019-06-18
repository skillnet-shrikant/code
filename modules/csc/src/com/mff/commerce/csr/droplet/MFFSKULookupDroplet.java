package com.mff.commerce.csr.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.csr.environment.MFFCSREnvironmentTools;

import atg.commerce.csr.catalog.CSRCatalogItemLookupDroplet;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class MFFSKULookupDroplet extends CSRCatalogItemLookupDroplet {

	static final ParameterName ID_PARAM = ParameterName.getParameterName("id");
	private MFFCSREnvironmentTools  mCsrEnvironmentTools;
	private String mSkuPropertyName;
	
	public MFFCSREnvironmentTools getCsrEnvironmentTools() {
		return mCsrEnvironmentTools;
	}


	public void setCsrEnvironmentTools(MFFCSREnvironmentTools pCsrEnvironmentTools) {
		mCsrEnvironmentTools = pCsrEnvironmentTools;
	}
	
	
	public String getSkuPropertyName() {
		return mSkuPropertyName;
	}



	public void setSkuPropertyName(String pSkuPropertyName) {
		mSkuPropertyName = pSkuPropertyName;
	}



	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException
        {
		vlogDebug("MFFSKULookupDroplet : service: Start");
			boolean overRideDroplet=false;
			if(getCsrEnvironmentTools()==null){
				super.service(pRequest, pResponse);
				return;
			}
			Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
			if(searchVariations==null || searchVariations.isEmpty()){
				overRideDroplet=false;
			}
			else {
				if(getCsrEnvironmentTools().isUseProductSearchVariation())
				{
					overRideDroplet=true;
				}
				else {
					overRideDroplet=false;
				}
			}
			if(overRideDroplet){
				String id = pRequest.getParameter(ID_PARAM);
				String skuName=getSkuPropertyName();
				String parsedValue=id;
				if(skuName==null || skuName.isEmpty())
					parsedValue=parsedValue("sku",id);
				else
					parsedValue=parsedValue(skuName,id);
				pRequest.setParameter(ID_PARAM.getName(), parsedValue);
			}
			super.service(pRequest, pResponse);
			vlogDebug("MFFSKULookupDroplet : service: End");
			
      }
	

	private String parsedValue(String propName, String currValue){
		vlogDebug("MFFSKULookupDroplet : parsedValue: Start");
		String retValue=currValue;
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		vlogDebug("MFFSKULookupDroplet : propertyModified: "+propName+" : current value: "+currValue);
		if(searchVariations!=null&&currValue!=null&&!searchVariations.isEmpty()&&!currValue.isEmpty()){
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
		vlogDebug("MFFSKULookupDroplet : propertyModified: "+propName+" : current value: "+retValue);
		vlogDebug("MFFSKULookupDroplet : parsedValue: End");
		return retValue;
	}
}
