package com.firstdata.util;

import java.beans.Statement;
import java.lang.reflect.Field;
import java.util.HashMap;

import atg.nucleus.GenericService;

import com.firstdata.bean.FirstDataBean;
/**
 * This utility class used for First Data gift card utility methods
 * 
 * @author DMI
 *
 */
public class FirstDataUtil extends GenericService {

  /* This method returns Java Bean after parsing the giftCard response
   * 
   * params : pResponseString (Response String), pDelimeter (Field Delimeter), pFirstDataBean (Bean class)
   * returns : FirstDataBean (java bean)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public FirstDataBean parseResponse(String pResponseString, String pDelimeter, String pFirstDataBean) throws Exception {

    if (pResponseString != null && pDelimeter != null && pResponseString.contains(pDelimeter)) {

      String[] resArr = pResponseString.split(pDelimeter);
      HashMap map = new HashMap();
      for (int i = 0; i < resArr.length; i++) {
        if (resArr[i].length() > 2) {
          map.put(resArr[i].substring(0, 2), resArr[i].substring(2, resArr[i].length()));
        }
      }
      FirstDataBean beanObj = (FirstDataBean) Class.forName(pFirstDataBean).newInstance();
      for (Field field : beanObj.getClass().getDeclaredFields()) {
        char[] fieldChar = field.getName().toCharArray();
        fieldChar[0] = Character.toUpperCase(fieldChar[0]);
        FirstDataField fieldAnnotation = field.getAnnotation(FirstDataField.class);
        if (fieldAnnotation != null && fieldAnnotation.key() != null && map.get(fieldAnnotation.key()) != null) {
          Statement fieldSetter = new Statement(beanObj, "set" + (new String(fieldChar)), new Object[] { map.get(fieldAnnotation.key()) });
          fieldSetter.execute();
        }
      }

      return beanObj;
    } else {
      return null;
    }

  }
  
}
