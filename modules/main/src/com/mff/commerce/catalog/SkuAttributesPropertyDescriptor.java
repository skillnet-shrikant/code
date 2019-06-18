package com.mff.commerce.catalog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.mff.constants.MFFConstants;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class SkuAttributesPropertyDescriptor extends RepositoryPropertyDescriptor{

  private static final long serialVersionUID = 1L;

  
  /**
   * Properties of this type should always be read-only. This is a no-op method.
   */
  public void setPropertyValue(RepositoryItemImpl item, Object value) {
    // this is not a writable property, do nothing.
    return;
  }

  @SuppressWarnings({ "unchecked"})
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
     
    Set<RepositoryItem> parentProducts= (Set<RepositoryItem>) pItem.getPropertyValue(MFFConstants.SKU_PARENT_PRODUCTS);
    Map<String,String> skuDynAttrMap = (Map<String,String>) pItem.getPropertyValue(MFFConstants.PRODUCT_DYNAMIC_ATTRIBUTES);
    HashMap<String,String> skuAttrMap = new HashMap<String,String>();
    //System.out.println("SkuAttributesPropertyDescriptor getPropertyValue : parentProducts - " + parentProducts + "skuDynAttrMap - " + skuDynAttrMap);
    if(parentProducts != null && skuDynAttrMap != null){
        for(RepositoryItem product : parentProducts){
          Map<String,String> prodDynAttrMap = (Map<String,String>)product.getPropertyValue(MFFConstants.SKU_DYNAMIC_ATTRIBUTES);
          if(prodDynAttrMap != null){
            Set<String> prodDynAttrkeys = prodDynAttrMap.keySet(); 
            for(String key : prodDynAttrkeys){
              String prodDynAttValue = (String)prodDynAttrMap.get(key);
              
              if(!Strings.isNullOrEmpty(prodDynAttValue)){
                String skuDynAttrValue = skuDynAttrMap.get(key);
                skuAttrMap.put(prodDynAttValue, skuDynAttrValue);
              }
            }
          }
        }
    }
    
    //System.out.println("SkuAttributesPropertyDescriptor getPropertyValue : skuAttrMap - " + skuAttrMap);
    return skuAttrMap;
  }

}
