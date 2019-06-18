package com.mff.repository.property;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class SeoDescription extends RepositoryPropertyDescriptor {

  @Override
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    String productDisplayName = (String) pItem.getPropertyValue("description");
    String productBrandName = (String) pItem.getPropertyValue("brand");
    if (null != productDisplayName) {
      if(null!=productBrandName && !productDisplayName.toLowerCase().startsWith(productBrandName.toLowerCase())) {
       return productBrandName.toLowerCase().trim() +" " + productDisplayName.toLowerCase().trim();
      }else {
        return productDisplayName.toLowerCase().trim();
      }
    }
    return null;
  }
}
