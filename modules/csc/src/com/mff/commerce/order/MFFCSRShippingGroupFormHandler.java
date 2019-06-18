package com.mff.commerce.order;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;

import com.mff.constants.MFFConstants;

import atg.commerce.csr.order.CSRShippingGroupFormHandler;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class MFFCSRShippingGroupFormHandler extends CSRShippingGroupFormHandler{
  
  
  @Override
  public void postApplyShippingGroups(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    vlogDebug("MFFCSRShippingGroupFormHandler - Inside postApplyShippingGroups");
    ShippingGroup sg = (ShippingGroup)getOrder().getShippingGroups().get(0);
    if(sg != null && sg instanceof HardgoodShippingGroup){
      ContactInfo addr = (ContactInfo)((HardgoodShippingGroup)sg).getShippingAddress();
      boolean restrictedShipping = ((MFFOrderTools)getOrderManager().getOrderTools()).restrictedShippingLocation(getOrder(),addr);
      if (restrictedShipping) {
        addFormException(new DropletException(MFFConstants.getEXTNResourcesMessage(pRequest, MFFConstants.SHIPPING_ADDRESS_RESTRICTED)));
        return;
      }
    }
    
    super.postApplyShippingGroups(pRequest, pResponse);
      
  }

  @SuppressWarnings("unchecked")
  @Override
  public void preApplyShippingGroups(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering preApplyShippingGroups : pRequest, pResponse");
    ShippingGroupMapContainer sgc = getShippingGroupMapContainer();
    
    if (sgc != null) {
      
      Set<String> shippingGroupNames = sgc.getShippingGroupNames();
      
      for (String shippingGroupName : shippingGroupNames) {
        
        vlogDebug("Shipping group name set to {0}", shippingGroupName);
        
        ShippingGroup lShippingGroup = sgc.getShippingGroup(shippingGroupName);
        
        if (lShippingGroup instanceof HardgoodShippingGroup) {
          
          HardgoodShippingGroup lHardgoodShippingGroup = (HardgoodShippingGroup) lShippingGroup;
          Address lAddress = lHardgoodShippingGroup.getShippingAddress();
          
          if (lAddress != null && StringUtils.isBlank(lAddress.getCountry())) {
            
            vlogDebug("Setting the country to default country of US");
            
            lAddress.setCountry(MFFConstants.DEFAULT_COUNTRY);
            
          } else {
            if (lAddress == null) 
                vlogDebug("Address not set");
            else
               vlogDebug("Country is not blank {0}", lAddress.getCountry());
            
          }
        } else {
          vlogDebug("Shipping group is not of the type hardgood");
        }
      }
      
    }
    
    super.preApplyShippingGroups(pRequest, pResponse);
    vlogDebug("Exiting preApplyShippingGroups : pRequest, pResponse");
  }
  
  
  
}
