package com.mff.commerce.order;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.google.common.base.Strings;
import com.mff.constants.MFFConstants;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.util.MFFUtils;
import com.mff.zip.MFFZipcodeHelper;

import atg.commerce.csr.order.CSRUpdateHardgoodShippingGroupFormHandler;
import atg.commerce.order.HardgoodShippingGroup;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.web.messaging.MessageTools;

public class MFFCSRUpdateHardgoodShippingGroupFormHandler extends CSRUpdateHardgoodShippingGroupFormHandler {

  private MFFPrevAddressRequest mPrevRequest;
  private String selectSuggestedAddress = "no";
  private MFFZipcodeHelper mZipCodeHelper;
  private MessageTools messageTools;
  
  @SuppressWarnings({ "rawtypes" })
  @Override
  public void validateShippingGroup() {
    super.validateShippingGroup();

    if (!getFormError()) {

      DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
      HardgoodShippingGroup shippingGroup = getWorkingHardgoodShippingGroup();
      Address shipAddress = shippingGroup.getShippingAddress();
      
      vlogDebug("Country: {0}, State: {1}, PostalCode: {2}", shipAddress.getCountry(), shipAddress.getState(), shipAddress.getPostalCode());
      Map<String, Object> newAddress = new HashMap<String, Object>();
      newAddress.put("address1", shipAddress.getAddress1());
      newAddress.put("address2", shipAddress.getAddress2());
      newAddress.put("city", shipAddress.getCity());
      newAddress.put("state", shipAddress.getState());
      newAddress.put("postalCode", shipAddress.getPostalCode());
      newAddress.put("country", shipAddress.getCountry());
      
      if (!getZipCodeHelper().isValidateCityStateZipCombination(newAddress)) {
        vlogDebug("Shipping Address: Invalid City, State, Postal Code combination.");
        
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(request).getString(MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION));
        addFormException(new DropletException(resourceMsg));
        return;
      }
      
      if (!Strings.isNullOrEmpty(getSelectSuggestedAddress())) {
        if (getSelectSuggestedAddress().equalsIgnoreCase("yes")) {
          // copy address to ship address
          shippingGroup.getShippingAddress().setAddress1(getPrevRequest().getSuggestedAddress().getAddress1());
          shippingGroup.getShippingAddress().setAddress2(getPrevRequest().getSuggestedAddress().getAddress2());
          shippingGroup.getShippingAddress().setCity(getPrevRequest().getSuggestedAddress().getCity());
          shippingGroup.getShippingAddress().setState(getPrevRequest().getSuggestedAddress().getState());
          shippingGroup.getShippingAddress().setPostalCode(getPrevRequest().getSuggestedAddress().getPostalCode());
          shippingGroup.getShippingAddress().setCountry(getPrevRequest().getSuggestedAddress().getCountry());

          return;
        } else if (getSelectSuggestedAddress().equalsIgnoreCase("no") && getPrevRequest().getPreviousAddress() != null && getPrevRequest().getSuggestedAddress() != null) {
          vlogDebug("CSR is using the previous entered address");
          return;
        }
      }
      
      Map addressInfo = ((MFFProfileTools) getCSRAgentTools().getProfileTools()).validateAddress(request, newAddress);

      if (addressInfo.get("addressMatch") != null && addressInfo.get("addressMatch").equals("true")) {
        vlogDebug("addressMatch is true so skip suggested address");
      } else if (addressInfo.get("addressMatch") != null && addressInfo.get("addressMatch").equals("false") && 
          addressInfo.get("suggestedAddress") == null) {
        // addressMatch is true so skip suggested address
        vlogDebug("addressMatch is false but suggested address is null so skip suggested address");
      } else {
        vlogDebug("addressMatch is false so show suggested address");
        getPrevRequest().setPreviousAddress((ContactInfo) shipAddress);
        getPrevRequest().setAddressType("shippingAddress");
        getPrevRequest().setSuggestedAddress((Address) addressInfo.get("suggestedAddress"));
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_SUGGESTED_ADDRESS));
        addFormException(new DropletException(resourceMsg));
      }
    }

  }

  @SuppressWarnings("rawtypes")
  @Override
  public void postUpdateHardgoodShippingGroup(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    super.postUpdateHardgoodShippingGroup(pRequest, pResponse);

    if (!getFormError()) {
      getPrevRequest().clearAddress();
      // clear the exception messages
      Collection messgaeItems = getMessageTools().getUserMessagingSlotComponent().getCurrentItems();
      if(messgaeItems != null && messgaeItems.size() > 0){
        getMessageTools().getUserMessagingSlotComponent().removeAll(messgaeItems);
      }
    }
  }

  public MFFPrevAddressRequest getPrevRequest() {
    return mPrevRequest;
  }

  public void setPrevRequest(MFFPrevAddressRequest mPrevRequest) {
    this.mPrevRequest = mPrevRequest;
  }

  public String getSelectSuggestedAddress() {
    return selectSuggestedAddress;
  }

  public void setSelectSuggestedAddress(String selectSuggestedAddress) {
    this.selectSuggestedAddress = selectSuggestedAddress;
  }
  
  public MFFZipcodeHelper getZipCodeHelper() {
    return mZipCodeHelper;
  }

  public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
    mZipCodeHelper = pZipCodeHelper;
  }
  
  public MessageTools getMessageTools() {
    return messageTools;
  }

  public void setMessageTools(MessageTools messageTools) {
    this.messageTools = messageTools;
  }


}
