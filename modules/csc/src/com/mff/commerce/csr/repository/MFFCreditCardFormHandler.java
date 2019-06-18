package com.mff.commerce.csr.repository;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.aci.commerce.service.AciService;
import com.aci.constants.AciConstants;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.pipeline.exception.AciPipelineException;
import com.mff.commerce.payment.creditcard.MFFExtendableCreditCardTools;
import com.mff.constants.MFFConstants;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.util.MFFUtils;
import com.mff.zip.MFFZipcodeHelper;

import atg.commerce.CommerceException;
import atg.commerce.csr.repository.CreditCardFormHandler;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.repository.RepositoryException;
import atg.repository.servlet.RepositoryFormHashtable;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.address.AddressMetaInfo;

public class MFFCreditCardFormHandler extends CreditCardFormHandler {
  
  protected static final String PROP_NAME_BILLING_ADDRESS = "BILLINGADDRESS";
  protected static final String PROP_NAME_CREDIT_CARD_NUMBER = "CREDITCARDNUMBER";
  
  private AciService aciService;
  private MFFExtendableCreditCardTools mCreditCardTools;
  private MFFZipcodeHelper mZipCodeHelper;
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void tokenizeCard(Dictionary creditCardFormValues) throws CommerceException {

    vlogDebug("Entering tokenizeCard : creditCardFormValues, pIsNewCard");
    MFFProfileTools lProfileTools = (MFFProfileTools) getCustomerProfile().getProfileTools();
    
    Address billingAddress = null;
    
    RepositoryFormHashtable billAddrHash = (RepositoryFormHashtable) getValueProperty(PROP_NAME_BILLING_ADDRESS);
    
    if (billAddrHash != null && billAddrHash.getRepositoryItem() != null) {
      vlogDebug("An existing address was selected");
      try {
        billingAddress = lProfileTools.getAddressFromRepositoryItem(billAddrHash.getRepositoryItem());
      } catch (RepositoryException e) {
        throw new CommerceException(String.format("An error occurred while trying to construct the billing address from the following repository item {0}", billAddrHash.getRepositoryItem().getRepositoryId()), e);
      }
    }
    
    if (billingAddress == null) {
      vlogDebug("Billing address was entered by the user");
      AddressMetaInfo newAddress = this.getNewAddressMetaInfo();
      billingAddress = newAddress.getAddress();
    }

    AciCreditCardInfo cardInfo = generateCreditCardInfo(creditCardFormValues, billingAddress);
    int ccreturn = getCreditCardTools().verifyCreditCard(cardInfo);

    if (ccreturn != ExtendableCreditCardTools.SUCCESS) {
      String msg = getCreditCardTools().getStatusCodeMessage(ccreturn);
      addFormException(new DropletFormException(msg, null));
      return;
    }
    
    String cardType = (String) creditCardFormValues.get("creditCardType");
    if (cardType.equalsIgnoreCase("visa")) {
      cardInfo.setCreditCardType("millsVisa");
      if (getCreditCardTools().verifyCreditCard(cardInfo) == ExtendableCreditCardTools.SUCCESS) {
        getValue().put("creditCardType", "millsVisa");
      }else{
        cardInfo.setCreditCardType(cardType);
      }
    }
    
    try {
      getAciService().tokenizeCreditCard(cardInfo);
      String tokenNumber = cardInfo.getTokenNumber();
      if (StringUtils.isBlank(tokenNumber)) {
        throw new CommerceException("Tokenization failed");
      } else {
        setValueProperty(AciConstants.PROPERTY_CC_TOKEN_NUMBER, tokenNumber);
        setValueProperty(AciConstants.PROPERTY_ACI_MOP_TYPE_CODE, cardInfo.getMopTypeCd());

        String ccNumber = (String) getValueProperty(PROP_NAME_CREDIT_CARD_NUMBER);
        ccNumber = lProfileTools.getLastFourForCreditCard(ccNumber);
        
        setValueProperty(PROP_NAME_CREDIT_CARD_NUMBER, ccNumber);
      }
    } catch (AciPipelineException e) {
      vlogDebug(e, "Error occurred while trying to save the credit card information for the following customer {0}", creditCardFormValues.get("nameOnCard"));
      throw new CommerceException(e);
    }

    vlogDebug("Exiting tokenizeCard : creditCardFormValues, pIsNewCard");

  }
  
  @Override
  public boolean handleCreate(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    try {
      tokenizeCard(getValue());
    } catch (CommerceException e) {
      addFormException (new DropletException("An error occurred while saving the credit card information"));
    }
    return super.handleCreate(pRequest, pResponse);
  }
  
  protected void preCreateItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    if(getCreateNewAddress()){
      Map<String, Object> newAddress = new HashMap<String, Object>();
      Address billingAddress = getNewAddressMetaInfo().getAddress();
      newAddress.put("address1", billingAddress.getAddress1());
      newAddress.put("address2", billingAddress.getAddress2());
      newAddress.put("city", billingAddress.getCity());
      newAddress.put("state", billingAddress.getState());
      newAddress.put("postalCode", billingAddress.getPostalCode());
      newAddress.put("country", billingAddress.getCountry());
      if (!getZipCodeHelper().isValidateCityStateZipCombination(newAddress)) {
        vlogDebug("preCreateItem : Billing Address: Invalid City, State, Postal Code combination.");
        
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION));
        addFormException(new DropletException(resourceMsg));
        return;
      }
    }
    
    super.preCreateItem(pRequest, pResponse);
   
  }

  @Override
  public boolean handleUpdate(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    try {
      tokenizeCard(getValue());
    } catch (CommerceException e) {
      addFormException (new DropletException("An error occurred while saving the credit card information"));
    }
    return super.handleUpdate(pRequest, pResponse);
  }

  @SuppressWarnings("rawtypes")
  private AciCreditCardInfo generateCreditCardInfo(Dictionary pInputCard, Address pBillingAddress) {
    vlogDebug("Entering generateCreditCardInfo : pInputCard, pBillingAddress");
    AciCreditCardInfo ccInfo = new AciCreditCardInfo();
    
    ccInfo.setCreditCardNumber((String) pInputCard.get("creditCardNumber"));
    ccInfo.setSecurityCode(((String) pInputCard.get("cvv")));
    ccInfo.setCreditCardType((String) pInputCard.get("creditCardType"));
    ccInfo.setExpirationMonth((String)pInputCard.get("expirationMonth"));
    ccInfo.setExpirationYear((String)pInputCard.get("expirationYear"));
    ccInfo.setOnlineOrder(true);
    ccInfo.setBillingAddress(pBillingAddress);
    
    vlogDebug("Exiting generateCreditCardInfo : pInputCard, pBillingAddress");
    return ccInfo;
  }
  public AciService getAciService() {
    return aciService;
  }
  public void setAciService(AciService pAciService) {
    aciService = pAciService;
  }
  
  /**
   * @return the creditCardTools
   */
  public MFFExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * @param pCreditCardTools the creditCardTools to set
   */
  public void setCreditCardTools(MFFExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }
  
  public MFFZipcodeHelper getZipCodeHelper() {
    return mZipCodeHelper;
  }

  public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
    mZipCodeHelper = pZipCodeHelper;
  }
  
}
