package com.mff.commerce.csr.order;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.order.purchase.MFFCheckoutManager;
import com.mff.constants.MFFConstants;
import com.mff.util.MFFUtils;
import com.mff.zip.MFFZipcodeHelper;

import atg.commerce.csr.order.CSRCreateCreditCardFormHandler;
import atg.commerce.order.CreditCard;
import atg.commerce.order.purchase.PaymentGroupMapContainer;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class MFFCSRCreateCreditCardFormHandler extends CSRCreateCreditCardFormHandler{
  
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  static final String MSG_ERROR_IN_VALIDATION = "errorInValidation";
  
  private MFFCheckoutManager mCheckoutManager;
  private MFFZipcodeHelper mZipCodeHelper;
  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }
  
  public void createCreditCard(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) {

    CreditCard creditCard = getCreditCard();
    PaymentGroupMapContainer container = getContainer();
    String name = getCreditCardName();

    if (StringUtils.isEmpty(name)
        && isGenerateNickname()) {
      name = getCommerceProfileTools().getUniqueCreditCardNickname(creditCard, getProfile(),null);
      setCreditCardName(name);
    }

    try {
      if (isValidateCreditCard()) {
        validateCreditCard(getCreditCard(),pRequest,pResponse);
      }
    }
    catch (ServletException se) {
      try {
        String msg = formatUserMessage(MSG_ERROR_IN_VALIDATION, pRequest, pResponse);
        String propertyPath = generatePropertyPath("creditCard");
        addFormException(new DropletFormException(msg, se, propertyPath, MSG_ERROR_IN_VALIDATION));
      }
      catch (Exception exception) { // exceptions thrown by exception-handling
        if (isLoggingError()) logError(exception);
      }
    }
    catch (IOException ioe) {
      try {
        String msg = formatUserMessage(MSG_ERROR_IN_VALIDATION, pRequest, pResponse);
        String propertyPath = generatePropertyPath("creditCard");
        addFormException(new DropletFormException(msg, ioe, propertyPath, MSG_ERROR_IN_VALIDATION));
      }
      catch (Exception exception) { // exceptions thrown by exception-handling
        if (isLoggingError()) logError(exception);
      }
    }

    if (getFormError()) {
      return;
    }

    // add to Container
    if (isAddToContainer()) {
      if (isAssignNewCreditCardAsDefault()) {
        container.setDefaultPaymentGroupName(name);
      }
      container.addPaymentGroup(name, creditCard);
    }

    // copy to Profile
    if (isCopyToProfile()) {
      //getCommerceProfileTools().copyCreditCardToProfile(creditCard, getProfile(),name);
      getCheckoutManager().setSavePaymentMethod(true);
      getCheckoutManager().setPaymentName(name);
    }
  }
  
  @Override
  public void preCreateCreditCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    if(!isUseExistingAddress()){
      Map<String, Object> newAddress = new HashMap<String, Object>();
      Address billingAddress = getCreditCard().getBillingAddress();
      newAddress.put("address1", billingAddress.getAddress1());
      newAddress.put("address2", billingAddress.getAddress2());
      newAddress.put("city", billingAddress.getCity());
      newAddress.put("state", billingAddress.getState());
      newAddress.put("postalCode", billingAddress.getPostalCode());
      newAddress.put("country", billingAddress.getCountry());
      if (!getZipCodeHelper().isValidateCityStateZipCombination(newAddress)) {
        vlogDebug("preCreateCreditCard : Billing Address: Invalid City, State, Postal Code combination.");
        
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION));
        addFormException(new DropletException(resourceMsg));
        return;
      }
    }
    
    super.preCreateCreditCard(pRequest, pResponse);
  }
  
  public MFFZipcodeHelper getZipCodeHelper() {
    return mZipCodeHelper;
  }

  public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
    mZipCodeHelper = pZipCodeHelper;
  }

}
