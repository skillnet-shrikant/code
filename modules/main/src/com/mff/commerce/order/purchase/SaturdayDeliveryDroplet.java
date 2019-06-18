package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.Calendar;
import javax.servlet.ServletException;

import com.mff.constants.MFFConstants;

import atg.commerce.order.ShippingGroup;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class SaturdayDeliveryDroplet extends DynamoServlet {
	
	private static final String SHIPPING_GROUP_PARAM = "shippingGroup";
	private static final String SHIPPING_METHOD_PARAM = "shippingMethod";
	private static final String IS_SAT_DELIVERY = "isSatDayDelivery";
	
	public void service(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	  
	  ShippingGroup shippingGroup = (ShippingGroup) pRequest.getObjectParameter(SHIPPING_GROUP_PARAM);
	  String shippingMethod = (String) pRequest.getObjectParameter(SHIPPING_METHOD_PARAM);
	 
	  if(shippingGroup == null){
	    vlogWarning("Shipping group should not be NULL at this stage");
	    pRequest.setParameter(IS_SAT_DELIVERY, false);
	  }else{
	    
	    if(shippingMethod == null || shippingMethod.isEmpty()){
	      shippingMethod = shippingGroup.getShippingMethod();
	      vlogDebug("SaturdayDeliveryDroplet: Shipping Method not passed, shipping method from order is:{0}", shippingMethod);
	    }else
	      vlogDebug("SaturdayDeliveryDroplet: Shipping Method passed in: {0}", shippingMethod);
	    
	    boolean isSaturdayDelivery = false;
	    
	    //if shipping method is passed verify if saturday delivery is available for that shipping method
	    //otherwise just verify the staurday delivery eligibility based on the time
	    if(shippingMethod != null && !shippingMethod.isEmpty())
	      isSaturdayDelivery = isSaturdayDelivery(shippingMethod);
	    else
	      isSaturdayDelivery = isSaturdayDelivery();
	    
	    vlogDebug("SaturdayDeliveryDroplet: isSaturdayDelivery:{0}", isSaturdayDelivery);
	    pRequest.setParameter(IS_SAT_DELIVERY, isSaturdayDelivery);
	  }
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
	}
	
	public boolean isSaturdayDelivery() {
    boolean returnFlag = false;
    
      Calendar currentTime = Calendar.getInstance();
      int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);
      
      String cutOffTime = MFFConstants.SHIPPING_CUT_OFF_TIME;
          
          String[] parts = cutOffTime.split(":");
      Calendar configureTime = Calendar.getInstance();
      configureTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
      configureTime.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            
        if(dayofWeek == currentTime.WEDNESDAY && currentTime.after(configureTime)){
          // Wednesday after 3:30PM
          returnFlag = true;
        } else if(dayofWeek == currentTime.THURSDAY && currentTime.before(configureTime)){
          // Thursday before 3:30PM
          returnFlag = true;
        } else if(dayofWeek == currentTime.THURSDAY && currentTime.after(configureTime)){
          // Thursday after 3:30PM
          returnFlag = true;
        } else if(dayofWeek == currentTime.FRIDAY && currentTime.before(configureTime)){
          // Friday before 3:30PM
          returnFlag = true;
        }
        
    return returnFlag;
  }
	
	 public boolean isSaturdayDelivery(String pShippingMethod) {
	    boolean returnFlag = false;
	    
	      Calendar currentTime = Calendar.getInstance();
	      int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);
	      
	      String cutOffTime = MFFConstants.SHIPPING_CUT_OFF_TIME;
	          
	      String[] parts = cutOffTime.split(":");
	      Calendar configureTime = Calendar.getInstance();
	      configureTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
	      configureTime.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
	        
	        if(pShippingMethod.equalsIgnoreCase(MFFConstants.SECOND_DAY) && dayofWeek == currentTime.WEDNESDAY && currentTime.after(configureTime)){
	          // Wednesday after 3:30PM
	          returnFlag = true;
	        } else if(pShippingMethod.equalsIgnoreCase(MFFConstants.SECOND_DAY) && dayofWeek == currentTime.THURSDAY && currentTime.before(configureTime)){
	          // Thursday before 3:30PM
	          returnFlag = true;
	        } else if(pShippingMethod.equalsIgnoreCase(MFFConstants.OVER_NIGHT) && dayofWeek == currentTime.THURSDAY && currentTime.after(configureTime)){
	          // Thursday after 3:30PM
	          returnFlag = true;
	        } else if(pShippingMethod.equalsIgnoreCase(MFFConstants.OVER_NIGHT) && dayofWeek == currentTime.FRIDAY && currentTime.before(configureTime)){
	          // Friday before 3:30PM
	          returnFlag = true;
	        }

	    return returnFlag;
	  }

}
