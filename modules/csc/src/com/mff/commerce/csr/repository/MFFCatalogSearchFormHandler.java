package com.mff.commerce.csr.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import com.mff.commerce.csr.environment.MFFCSREnvironmentTools;

import atg.commerce.csr.repository.servlet.CustomCatalogSearchFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;





public class MFFCatalogSearchFormHandler extends CustomCatalogSearchFormHandler {
  
	

	private MFFCSREnvironmentTools  mCsrEnvironmentTools;
	
	public MFFCSREnvironmentTools getCsrEnvironmentTools() {
		return mCsrEnvironmentTools;
	}



	public void setCsrEnvironmentTools(MFFCSREnvironmentTools pCsrEnvironmentTools) {
		mCsrEnvironmentTools = pCsrEnvironmentTools;
	}


	public boolean handleSearch(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
             throws ServletException, IOException
         {
			vlogInfo("MFFCatalogSearchFormHandler : handleSearch: Start");
			boolean overRideSearch=false;
			Map<String,String> modifyProps=new HashMap<String,String>();
			if(getCsrEnvironmentTools()==null){
				return super.handleSearch(pRequest, pResponse);
			}
			Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
			ArrayList<String> propVariations=new ArrayList<String>();
			if(searchVariations==null||searchVariations.isEmpty()){
				overRideSearch=false;
			}
			else {
				
				if(getCsrEnvironmentTools().isUseProductSearchVariation()){
					String[] types=getItemTypes();
					if(types==null){
						overRideSearch=false;
					}
					else if(types.length==0){
						overRideSearch=false;
					}
					else {
						for(String type:types){
							String productDescriptorName=getCsrEnvironmentTools().getProductDescriptorName();
							if( productDescriptorName==null||productDescriptorName.isEmpty()){
								overRideSearch=false;
							}
							else {
									if(type.equalsIgnoreCase(productDescriptorName)){
										propVariations=new ArrayList<String>(searchVariations.keySet());
										Set formPropertiesByName = getPropertyValues().entrySet();
										for(Iterator propTypes = formPropertiesByName.iterator(); propTypes.hasNext();)
						                {
											java.util.Map.Entry pair = (java.util.Map.Entry)propTypes.next();
											String propertyName=(String)pair.getKey();
											String propertyValue=(String)pair.getValue();
											if(propertyValue==null){
												propertyValue="";
											}
											vlogDebug("Property Name:" + propertyName +" -- Property Value:"+propertyValue);
											if(propertyValue!=null&&propVariations.contains(propertyName) && !propertyValue.isEmpty()){
												modifyProps.put(propertyName,propertyValue);
											}
										
											
						                }
										overRideSearch=true;
									}
									else {
										overRideSearch=false;
									}
							}
						}
					}
				}
				else {
					overRideSearch=false;
				}
			}
			if(overRideSearch){
				
				modifyEnteredValue(modifyProps,propVariations);
			}
			
			String skuValue=getSku();
			if(getCsrEnvironmentTools()!=null&&skuValue!=null&&searchVariations!=null && !skuValue.isEmpty()){
				setSku(parsedValue("sku",skuValue));
			}
			
			vlogInfo("MFFCatalogSearchFormHandler : handleSearch: End");
			return super.handleSearch(pRequest, pResponse);
		 	
         }
	
	private void modifyEnteredValue(Map<String,String> propsToModify,ArrayList<String> propVariations){
			vlogInfo("MFFCatalogSearchFormHandler : modifyEnteredValue: Start");
			if(propsToModify!=null && propVariations!=null && !propsToModify.isEmpty() && ! propVariations.isEmpty()){
				Set formPropertiesByName = propsToModify.entrySet();
				for(Iterator propTypes = formPropertiesByName.iterator(); propTypes.hasNext();)
                {
					java.util.Map.Entry pair = (java.util.Map.Entry)propTypes.next();
					String propertyName=(String)pair.getKey();
					String propertyValue=(String)pair.getValue();
					if(propVariations.contains(propertyName)){
						getPropertyValues().put(propertyName, parsedValue(propertyName,propertyValue));
					}
                }
			}
			vlogInfo("MFFCatalogSearchFormHandler : modifyEnteredValue: End");
	}
	
	private String parsedValue(String propName, String currValue){
		vlogInfo("MFFCatalogSearchFormHandler : parsedValue: Start");
		String retValue=currValue;
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		vlogInfo("MFFCatalogSearchFormHandler : propertyModified: "+propName+" : current value: "+currValue);
		if(searchVariations!=null&&!searchVariations.isEmpty()){
			String lengthField=searchVariations.get(propName);
			if(lengthField==null || lengthField.isEmpty()){
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
		vlogInfo("MFFCatalogSearchFormHandler : propertyModified: "+propName+" : current value: "+retValue);
		vlogInfo("MFFCatalogSearchFormHandler : parsedValue: End");
		return retValue;
	}
  
  
  
}
