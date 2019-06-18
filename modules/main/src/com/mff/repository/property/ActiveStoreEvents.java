package com.mff.repository.property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import mff.util.DateUtil;

public class ActiveStoreEvents extends RepositoryPropertyDescriptor {

  @Override
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    Date lToday = new Date();
    List<RepositoryItemImpl> storeEventsList = (List<RepositoryItemImpl>)pItem.getPropertyValue("storeEvents");
    ArrayList<RepositoryItemImpl> lActiveStoreEventsList= new ArrayList<RepositoryItemImpl>();
    if(null!=storeEventsList) {
      for(RepositoryItemImpl lStoreEvent : storeEventsList) {
        Date lEventStartTime = (Date) lStoreEvent.getPropertyValue("eventStartTime");
        Date lEventEndTime = (Date) lStoreEvent.getPropertyValue("eventEndTime");
        Date lEventDisplayStartTime = (Date) lStoreEvent.getPropertyValue("eventDisplayStartTime");
        if(lToday.before(lEventEndTime)) {
          if(lEventDisplayStartTime==null) {
            if(lEventStartTime.before(DateUtil.addDays(lToday, 90))) {
              lActiveStoreEventsList.add(lStoreEvent);
            }
          }else {
            if(lToday.after(lEventDisplayStartTime)) {
              lActiveStoreEventsList.add(lStoreEvent);
            }
          }
        }
      }
    }
    return lActiveStoreEventsList;
  }
}
