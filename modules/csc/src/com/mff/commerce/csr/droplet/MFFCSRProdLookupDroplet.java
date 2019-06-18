package com.mff.commerce.csr.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.csr.environment.MFFCSREnvironmentTools;


import atg.nucleus.naming.ParameterName;
import atg.repository.servlet.ItemLookupDroplet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class MFFCSRProdLookupDroplet extends ItemLookupDroplet {

	static final ParameterName ID_PARAM = ParameterName.getParameterName("id");
	private MFFCSREnvironmentTools  mCsrEnvironmentTools;
	private String mIdPropertyName;
	
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
		vlogDebug("MFFCSRProdLookupDroplet : service: Start");
			boolean overRideDroplet=false;
			if(getCsrEnvironmentTools()==null){
				super.service(pRequest, pResponse);
				return;
			}
			Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
			if(searchVariations==null||searchVariations.isEmpty()){
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
				String parsedValue=id;
				String idPropName=getIdPropertyName();
				if(idPropName==null|| idPropName.isEmpty())
					parsedValue=parsedValue("id",id);
				else
					parsedValue=parsedValue(idPropName,id);
				pRequest.setParameter(ID_PARAM.getName(), parsedValue);
			}
			vlogDebug("MFFCSRProdLookupDroplet : service: End");
			super.service(pRequest, pResponse);
			
			
      }
	

	private String parsedValue(String propName, String currValue){
		vlogDebug("MFFCSRProdLookupDroplet : parsedValue: Start");
		String retValue=currValue;
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		vlogDebug("MFFCSRProdLookupDroplet : propertyModified: "+propName+" : current value: "+currValue);
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
		vlogDebug("MFFCSRProdLookupDroplet : propertyModified: "+propName+" : current value: "+retValue);
		vlogDebug("MFFCSRProdLookupDroplet : parsedValue: End");
		return retValue;
	}
}
