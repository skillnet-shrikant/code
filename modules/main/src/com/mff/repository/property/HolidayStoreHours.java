package com.mff.repository.property;

import java.util.List;
import java.util.ArrayList;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class HolidayStoreHours extends RepositoryPropertyDescriptor {

  @Override
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    List<RepositoryItemImpl> storeHoursList = (List<RepositoryItemImpl>)pItem.getPropertyValue("storeHoursList");
    ArrayList<RepositoryItemImpl> lHolidayStoreHours = new ArrayList<RepositoryItemImpl>();
    if(null!=storeHoursList) {
      for(RepositoryItemImpl lStoreHours : storeHoursList) {
        String lDaytype = (String) lStoreHours.getPropertyValue("dayType");
        if(lDaytype.equals("Holiday")) {
          lHolidayStoreHours.add(lStoreHours);
        }
      }
    }
    return lHolidayStoreHours;
  }
}
