package com.mff.repository.property;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class RepositoryIdWithoutZeros extends RepositoryPropertyDescriptor {

  @Override
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    String id = (String) pItem.getPropertyValue("id");
    return String.valueOf(Long.parseLong(id));
  }
}
