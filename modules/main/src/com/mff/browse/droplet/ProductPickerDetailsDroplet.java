package com.mff.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.pricing.priceLists.PriceListManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.constants.MFFConstants;

/**
 * This class is used to get the Picker Details from the Product as a JSON
 * Structure
 * 
 * @author DMI
 * 
 */
public class ProductPickerDetailsDroplet extends DynamoServlet {

  private static final ParameterName PRODUCT = ParameterName.getParameterName("product");
  private static final String DYNAMIC_ATTRIBUTES = "dynamicAttributes";
  private static final String SKU_ATTRIBUTES = "skuAttributes";
  private static final String CHILD_SKUS = "childSKUs";
  private static final String CATALOG_REF_ID = "catalogRefId";
  private static final String PICKERS = "pickers";
  private static final String PICKER_TYPES = "pickerTypes";
  private static final String PRODUCT_ID = "productId";
  private static final String SKUS = "skus";
  private static final String PICKERS_DATA = "pickersData";
  private static final String CLEARANCE = "clearance";
  private static final String HIDE_PRICE = "hidePrice";
  private static final String VPN = "vpn";
  private static final String MODEL_NUMBER = "modelNumber";

  private PriceListManager priceListManager;

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogDebug("Entered into the service for ProductPickerDetailsDroplet----");
    RepositoryItem productObj = (RepositoryItem) pRequest.getObjectParameter(PRODUCT);
    List<RepositoryItem> skus = (List<RepositoryItem>)pRequest.getObjectParameter(CHILD_SKUS);
    
    HashMap<String, Object> mProductMap = new HashMap<String, Object>();
    Map<String, String> prodDynamicAttributes = null;
    Map<String, String> dynamicAttributes = null;
    HashMap<String, String> pickerTypeMap = new HashMap();
    HashMap<String, Object> mSkuMap = null;
    ArrayList mSkus = new ArrayList();
    if (productObj != null) {
      if (productObj.getPropertyValue(DYNAMIC_ATTRIBUTES) != null) {
        prodDynamicAttributes = (Map<String, String>) productObj.getPropertyValue(DYNAMIC_ATTRIBUTES);
        if (prodDynamicAttributes != null && !prodDynamicAttributes.isEmpty()) {
          Iterator pickerTypeKeysItr = prodDynamicAttributes.keySet().iterator();
          while (pickerTypeKeysItr.hasNext()) {
            String pickerTypeKey = (String) pickerTypeKeysItr.next();
            pickerTypeMap.put(pickerTypeKey, prodDynamicAttributes.get(pickerTypeKey));
          }
          vlogDebug("PickerType List obtained from product : {0} ", productObj.getRepositoryId());
        }
      }
      
      
      //List<RepositoryItem> skus = (List<RepositoryItem>) productObj.getPropertyValue(CHILD_SKUS);
      if (skus != null) {
    	  	boolean isHidePrice = (boolean) productObj.getPropertyValue(HIDE_PRICE);
    	  	vlogDebug("isHidePrice: " + isHidePrice);
	        for (RepositoryItem sku : skus) {
	          if (sku.getPropertyValue(DYNAMIC_ATTRIBUTES) != null) {
	            dynamicAttributes = (Map<String, String>) sku.getPropertyValue(DYNAMIC_ATTRIBUTES);
	            if (dynamicAttributes != null && !dynamicAttributes.isEmpty()) {
	              vlogDebug("dynamicAttributes obtained from sku : {0} ", sku.getRepositoryId());
	              mSkuMap = new HashMap<String, Object>();
	              mSkuMap.put(CATALOG_REF_ID, sku.getRepositoryId());
	              mSkuMap.put(MODEL_NUMBER, sku.getPropertyValue(VPN));
	              Iterator keysItr = dynamicAttributes.keySet().iterator();
	              HashMap pickerMap = new HashMap();
	              while (keysItr.hasNext()) {
	                String attributeKey = (String) keysItr.next();
	                pickerMap.put(attributeKey, dynamicAttributes.get(attributeKey));
	              }
	              vlogDebug("PICKERS obtained from sku : {0} ", sku.getRepositoryId());
	              mSkuMap.put(PICKERS, pickerMap);
	              
	              boolean isClearance = (boolean) sku.getPropertyValue(CLEARANCE);
	              vlogDebug("isClearance: " + isClearance);
	              
	              mSkuMap.put(CLEARANCE, isClearance);
	              mSkuMap.put(HIDE_PRICE, isHidePrice);
	              Map<String, String> skuAttributes = (Map<String, String>) sku.getPropertyValue(SKU_ATTRIBUTES);
	              mSkuMap.put(SKU_ATTRIBUTES, skuAttributes);
	              mSkus.add(mSkuMap);
	            }
	          }
	        }
      }
      mProductMap.put(PRODUCT_ID, productObj.getRepositoryId());
      mProductMap.put(PICKER_TYPES, pickerTypeMap);
      mProductMap.put(SKUS, mSkus);
    }
    // String jsonString = new Gson().toJson(mProductMap);

    if (mProductMap != null && !mProductMap.isEmpty()) {
      pRequest.setParameter(PICKERS_DATA, mProductMap);
      pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
    }
    vlogDebug("Exited from the service ProductPickerDetailsDroplet----");
  }

  public PriceListManager getPriceListManager() {
    return priceListManager;
  }

  public void setPriceListManager(PriceListManager priceListManager) {
    this.priceListManager = priceListManager;
  }
}
