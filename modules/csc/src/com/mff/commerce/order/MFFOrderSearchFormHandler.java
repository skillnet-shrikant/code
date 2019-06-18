package com.mff.commerce.order;

import java.util.Map;

import com.mff.commerce.csr.environment.MFFCSREnvironmentTools;

import atg.commerce.csr.search.textsearch.OrderSearchFormHandler;
import atg.textsearch.client.*;

/**
 * 
 * @author vsingh
 *
 */
public class MFFOrderSearchFormHandler extends OrderSearchFormHandler{
  
  private String commerceItemStateProperty;
  private String shippingMethodProperty;
  private String orderNumberProperty;
  private MFFCSREnvironmentTools  mCsrEnvironmentTools; 
  private String mSkuPropertyName;
  
  
  public String getSkuPropertyName() {
		return mSkuPropertyName;
  }
  
  public void setSkuPropertyName(String pSkuPropertyName) {
		mSkuPropertyName = pSkuPropertyName;
  }

  
  public MFFCSREnvironmentTools getCsrEnvironmentTools() {
		return mCsrEnvironmentTools;
  }

  public void setCsrEnvironmentTools(MFFCSREnvironmentTools pCsrEnvironmentTools) {
		mCsrEnvironmentTools = pCsrEnvironmentTools;
  }
  
  public String getShippingMethodProperty() {
    return shippingMethodProperty;
  }

  public void setShippingMethodProperty(String shippingMethodProperty) {
    this.shippingMethodProperty = shippingMethodProperty;
  }

  public String getCommerceItemStateProperty() {
    return commerceItemStateProperty;
  }

  public void setCommerceItemStateProperty(String commerceItemStateProperty) {
    this.commerceItemStateProperty = commerceItemStateProperty;
  }

  public String getOrderNumberProperty() {
    return orderNumberProperty;
  }

  public void setOrderNumberProperty(String pOrderNumberProperty) {
    orderNumberProperty = pOrderNumberProperty;
  }
  
  @Override
  protected SearchRequest beforeSearch(SearchRequest pRequest, SearchSession pSession) {
	   vlogDebug("MFFOrderSearchFormHandler : beforeSearch: Start");
		boolean isModifySkuField=false;
		
		if(getCsrEnvironmentTools()==null){
			return super.beforeSearch(pRequest, pSession);
		}
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		if( searchVariations==null||searchVariations.isEmpty()){
			isModifySkuField=false;
		}
		else {
			if(getCsrEnvironmentTools().isUseProductSearchVariation())
			{
				isModifySkuField=true;
			}
			else {
				isModifySkuField=false;
			}
		}
		if(isModifySkuField){
			vlogDebug("MFFOrderSearchFormHandler : beforeSearch: isModifySkuFieldBlock Start");
			Field fields[] = pRequest.getFields();
			if(fields!=null){
				vlogDebug("MFFOrderSearchFormHandler : beforeSearch: Fields Present");
				Field skuField=fields[5];
				if(skuField!=null){
					vlogDebug("MFFOrderSearchFormHandler : beforeSearch: SkuFields Present");
					String fieldValue=skuField.getValue();
					if(fieldValue!=null&&!fieldValue.isEmpty()){
						String parsedValue=fieldValue;
						String skuName=getSkuPropertyName();
						if(skuName==null|| skuName.isEmpty())
							parsedValue=parsedValue("sku",fieldValue);
						else
							parsedValue=parsedValue(skuName,fieldValue);
						fields[5].setValue(parsedValue);
						vlogDebug("MFFOrderSearchFormHandler : beforeSearch: SkuField Modified");
					}
				}
			}
			vlogDebug("MFFOrderSearchFormHandler : beforeSearch: isModifySkuFieldBlock End");
		}
		vlogDebug("MFFOrderSearchFormHandler : beforeSearch: End");
		return super.beforeSearch(pRequest, pSession);

	  
  }
  
  private String parsedValue(String propName, String currValue){
		vlogDebug("MFFOrderSearchFormHandler : parsedValue: Start");
		String retValue=currValue;
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		vlogDebug("MFFOrderSearchFormHandler : propertyModified: "+propName+" : current value: "+currValue);
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
		vlogDebug("MFFOrderSearchFormHandler : propertyModified: "+propName+" : current value: "+retValue);
		vlogDebug("MFFOrderSearchFormHandler : parsedValue: End");
		return retValue;
	}
  
  

}
