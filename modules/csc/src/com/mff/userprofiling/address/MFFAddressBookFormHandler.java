package com.mff.userprofiling.address;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFPrevAddressRequest;
import com.mff.constants.MFFConstants;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.util.MFFUtils;
import com.mff.zip.MFFZipcodeHelper;

import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.address.AddressBookException;
import atg.userprofiling.address.AddressBookFormHandler;

public class MFFAddressBookFormHandler extends AddressBookFormHandler {

  boolean mValidateAddress;
  private MFFPrevAddressRequest mPrevRequest;
  private String selectSuggestedAddress = "no";
  protected MFFProfileTools mMFFProfileTools;
  private MFFZipcodeHelper mZipCodeHelper;
  
  @Override
  public boolean handleAddAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    logDebug("Entered into handleAddAddress---MFFAddressBookFormHandler");
    try {
      validateShippingGroup(pRequest);
    } catch (AddressBookException e) {
      logError(e);
    }
    if (getFormError()) return true;
    if (!getFormError()) {
      getPrevRequest().clearAddress();
    }
    return super.handleAddAddress(pRequest, pResponse);
  }
  
  @SuppressWarnings("rawtypes")
  private void validateShippingGroup(DynamoHttpServletRequest pRequest) throws AddressBookException {
    if (!getFormError()) {

      DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
      Address shipAddress = getAddressMetaInfo().getAddress();
      
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
          getAddressMetaInfo().getAddress().setAddress1(getPrevRequest().getSuggestedAddress().getAddress1());
          getAddressMetaInfo().getAddress().setAddress2(getPrevRequest().getSuggestedAddress().getAddress2());
          getAddressMetaInfo().getAddress().setCity(getPrevRequest().getSuggestedAddress().getCity());
          getAddressMetaInfo().getAddress().setState(getPrevRequest().getSuggestedAddress().getState());
          getAddressMetaInfo().getAddress().setPostalCode(getPrevRequest().getSuggestedAddress().getPostalCode());
          getAddressMetaInfo().getAddress().setCountry(getPrevRequest().getSuggestedAddress().getCountry());

          return;
        } else if (getSelectSuggestedAddress().equalsIgnoreCase("no") && getPrevRequest().getPreviousAddress() != null && getPrevRequest().getSuggestedAddress() != null) {
          logDebug("CSR is using the previous entered address");
          return;
        }
      }
     
      Map addressInfo = ((MFFProfileTools) getProfileTools()).validateAddress(request, newAddress);

      if (addressInfo.get("addressMatch") != null && addressInfo.get("addressMatch").equals("true")) {
        // addressMatch is true so skip suggested address
        vlogDebug("addressMatch is true so skip suggested address");
      } else if (addressInfo.get("addressMatch") != null && addressInfo.get("addressMatch").equals("false") && 
          addressInfo.get("suggestedAddress") == null) {
        // addressMatch is false throw user error message for correction.
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.ADDRESS_NOT_FOUND));
        addFormException(new DropletException(resourceMsg));
        vlogDebug("addressMatch is false but suggested address is null so skip suggested address");
      }else {
        vlogDebug("addressMatch is false so show suggested address");
        getPrevRequest().setPreviousAddress((ContactInfo) shipAddress);
        getPrevRequest().setAddressType("shippingAddress");
        getPrevRequest().setSuggestedAddress((Address) addressInfo.get("suggestedAddress"));
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_SUGGESTED_ADDRESS));
        addFormException(new DropletException(resourceMsg));
      }
    }
  }

  @Override
  public boolean handleUpdateAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    logDebug("Entered into handleUpdateAddress---MFFAddressBookFormHandler");
    try {
      validateShippingGroup(pRequest);
    } catch (AddressBookException e) {
      logError(e);
    }
    if (getFormError()) return true;
    if (!getFormError()) {
      getPrevRequest().clearAddress();
    }
    return super.handleUpdateAddress(pRequest, pResponse);
  }
  
  public boolean isValidateAddress() {
    return this.mValidateAddress;
  }

  public void setValidateAddress(boolean pValidateAddress) {
    this.mValidateAddress = pValidateAddress;
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
  

  public void setProfileTools(MFFProfileTools pMFFProfileTools) {
    this.mMFFProfileTools = pMFFProfileTools;
  }

  public MFFProfileTools getProfileTools() {
    return this.mMFFProfileTools;
  }
  
  public MFFZipcodeHelper getZipCodeHelper() {
    return mZipCodeHelper;
  }

  public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
    mZipCodeHelper = pZipCodeHelper;
  }
  
}
