package com.mff.repository.property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class TodayStoreHours extends RepositoryPropertyDescriptor {
  
  /*private static final Map<Integer,String> mDayTypeMap= new HashMap<Integer, String>() {{
    put(101, "Sunday");
    put(102, "Monday");
    put(103, "Tuesday");
    put(104, "Wednesday");
    put(105, "Thursday");
    put(106, "Friday");
    put(107, "Saturday");
  }};*/
  
  @Override
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    List<RepositoryItemImpl> lHolidayStoreHoursList = (List<RepositoryItemImpl>)pItem.getPropertyValue("holidayStoreHoursList");
    List<RepositoryItemImpl> lStandardStoreHoursList = (List<RepositoryItemImpl>)pItem.getPropertyValue("standardStoreHoursList");
    Date lToday = new Date();
    SimpleDateFormat lFormatterDay = new SimpleDateFormat( "EEEEE" );
    SimpleDateFormat lFormatterDate = new SimpleDateFormat( "MM/dd/yy" );
    String lTodayDay = lFormatterDay.format(lToday);
    String lTodayDate = lFormatterDate.format(lToday);
    for(RepositoryItemImpl lHolidayStoreHours : lHolidayStoreHoursList) {
      Date lHolidayDate= (Date) lHolidayStoreHours.getPropertyValue("holidayDate");
      if(lTodayDate.equals(lFormatterDate.format(lHolidayDate))) {
        return lHolidayStoreHours;
      }
    }
    for(RepositoryItemImpl lStandardStoreHours : lStandardStoreHoursList) {
      String lDayType = (String) lStandardStoreHours.getPropertyValue("dayType");
      if(lTodayDay.equals(lDayType)) {
        return lStandardStoreHours;
      }
    }
    
    return null;
  }
  
}
