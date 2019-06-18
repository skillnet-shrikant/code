package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import com.aci.commerce.service.AciService;
import com.aci.payment.creditcard.AciCreditCard;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.pipeline.exception.AciPipelineException;
import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.order.MFFOrderTools;
import com.mff.commerce.payment.MFFGiftCardManager;
import com.mff.commerce.payment.creditcard.MFFExtendableCreditCardTools;
import com.mff.commerce.util.MFFAddressValidator;
import com.mff.constants.MFFConstants;
import com.mff.droplet.InlineFormErrorSupport;
import com.mff.droplet.MFFInlineDropletFormException;
import com.mff.userprofiling.MFFProfile;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.util.MFFUtils;
import com.mff.zip.MFFZipcodeHelper;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.purchase.PaymentGroupFormHandler;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.PropertyManager;
import mff.MFFEnvironment;

public class MFFPaymentGroupFormHandler extends PaymentGroupFormHandler implements InlineFormErrorSupport, NicknameBasedAddressFormHandler {

  private String mCountry = MFFConstants.DEFAULT_COUNTRY;
  private static final String CREDIT_CARD_ID_ZERO = "0";

  private MFFCheckoutManager mCheckoutManager;
  private MFFFormExceptionGenerator mFormExceptionGenerator;
  private MFFAddressValidator mAddressValidator;
  private MFFProfileTools mProfileTools;
  private MFFShippingGroupFormHandler mShippingGroupFormHandler;
  private MFFGiftCardManager mGiftCardManager;
  private AciService mAciService;
  private MFFEnvironment mMffEnvironment;
  private MFFZipcodeHelper mZipCodeHelper;
  private boolean mCityStateZipMatched = true;

  private String mCreditCardType;
  private String mCreditCardNumber;
  private String mCreditCardExpMonth;
  private String mCreditCardExpYear;
  private String mCreditCardCVV;
  private String mNameOnCard;
  private String mTokenId;
  private String mContactEmail;
  private MFFPurchaseProcessHelper mPurchaseProcessHelper;

  private String mGiftCardNumber;
  private String mGiftCardPin;
  private String mGiftCardToRemove;
  // Billing address
  private boolean mSameAddressAsShipping;
  private boolean mSameAddressAsInAddressBook;
  private String mAddressNickname;
  private String mAddressId;
  private String mFirstName = "";
  private String mLastName = "";
  private String mAddress1 = "";
  private String mAddress2 = "";
  private String mCity = "";
  private String mState = "";
  private String mPostalCode = "";
  private String mPhoneNumber;
  private boolean mAddressVerified;
  ContactInfo mBillingAddress;
  private String mPaymentName;
  private boolean mDisplayAvsModal;
  private boolean mSavePaymentMethod;
  private List<String> mCardPropertyList;

  private String mApplyCreditCardSuccessURL;
  private String mApplyCreditCardErrorURL;
  private String mSelectBillingSuccessURL;
  private String mSelectBillingErrorURL;
  private String mAvsBillingSuccessURL;
  private String mAvsBillingErrorURL;
  private String mGiftCardSuccessURL;
  private String mGiftCardErrorURL;
  private String mRemoveGiftCardSuccessURL;
  private String mRemoveGiftCardErrorURL;
  private String mSavePaymentSuccessURL;
  private String mSavePaymentErrorURL;
  private String mMopTypeCode;

  /**
   * Initialize and Validate CreditCard, Billing Address data
   *
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  public boolean preApplyPayments(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    // if there is no contact email set, redirect the user to error page
    if (getContactEmail() == null || getContactEmail().isEmpty()) {
      vlogError("In preApplyPayments: No Contact Email value found, we need this to place an order");
      getFormExceptionGenerator().generateException(MFFConstants.NO_CONTACT_EMAIL_ORDER, true, this, pRequest);
      return checkFormRedirect(getAvsBillingSuccessURL(), getApplyCreditCardErrorURL(), pRequest, pResponse);
    }

    // verify if order requires a credit card
    if (getCheckoutManager().isOrderRequiresCreditCard()) {

      // validate nick name if user opts to save the payment
      if (((MFFProfile) getProfile()).isHardLoggedIn() && isSavePaymentMethod()) {
        if (!validatePaymentName(pRequest, getPaymentName())){
        	return checkFormRedirect(getAvsBillingSuccessURL(), getAvsBillingErrorURL(), pRequest, pResponse);
        }
      }

      // initialize credit card data here
      boolean initSuccess = initCreditCardData(pRequest, pResponse);

      // if there is an issue in initializing the credit card data redirect user
      // to error page
      if (!initSuccess || getFormError()) {
        return checkFormRedirect(getAvsBillingSuccessURL(), getAvsBillingErrorURL(), pRequest, pResponse);
      }

      // BZ 2505 - use card info for pickup person info
      MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();
      if(currentOrder.isBopisOrder() && currentOrder.isSignatureRequired()) {
      	currentOrder.setBopisPerson(getNameOnCard());
      	currentOrder.setBopisEmail(getContactEmail());
      }

      // validate billing address
      boolean valid = validateBillingAddress(pRequest, getBillingAddress());

      // check if there are any errors after validating the billing address
      if (!valid) {
        if (isLoggingError()) {
          vlogError("Found invalid address ({0})  with postalCode ({1}) when processing the billing addess for order ({2})", getBillingAddress(), getBillingAddress().getPostalCode(), getOrder().getId());
        }
        getFormExceptionGenerator().generateException(MFFConstants.BILLING_ADDRESS_MISSING_DATA, true, this, pRequest);
     // BZ 2981 - redirect to error url page
        return checkFormRedirect(getAvsBillingSuccessURL(), getAvsBillingErrorURL(), pRequest, pResponse);
      } else {

    	  if (!getCheckoutManager().isBillingAvsVerified()) {
	    	Map inputAddressParam = new HashMap();
	    	inputAddressParam.put(MFFConstants.ADDRESS_POSTAL_CODE, getBillingAddress().getPostalCode());
	    	inputAddressParam.put(MFFConstants.ADDRESS_STATE, getBillingAddress().getState());
	    	inputAddressParam.put(MFFConstants.ADDRESS_CITY, getBillingAddress().getCity());

	    	if (!getZipCodeHelper().isValidateCityStateZipCombination(inputAddressParam)) {
	    		if (isLoggingDebug()) {
	    			logDebug("Billing Address: Invalid City, State, Postal Code combination.");
	    		}
	    		String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION));
						getFormExceptionGenerator().generateInlineException(resourceMsg,
								 getZipCodeHelper().getCityStateZipErrorField(),
								this, pRequest);
	    		return checkFormRedirect(getAvsBillingSuccessURL(), getAvsBillingErrorURL(), pRequest, pResponse);
	    	}
	    }
    }

      // if billing address is not verified perform AVS check
      if (!getCheckoutManager().isBillingAvsVerified()) {
        boolean displayShippingAvsModal = performBillingAVS();
        if (displayShippingAvsModal) { // Display Shipping AVS Modal
          return checkFormRedirect(getAvsBillingSuccessURL(), getAvsBillingErrorURL(), pRequest, pResponse);
        }
      }

      // validate the card data
      validateCreditCard(pRequest);

      if (getFormError()) {
        return checkFormRedirect(getAvsBillingSuccessURL(), getAvsBillingErrorURL(), pRequest, pResponse);
      }

    }
    return false;
  }

  /**
   *
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleApplyPayments(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogInfo("HandleApplyPayments: for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());

    boolean hardLoggedIn = ((MFFProfile) getProfile()).isHardLoggedIn();

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    Transaction tr = null;
    String methodName = MFFConstants.HANDLE_APPLY_PAYMENTS;

    if ((rrm == null) || (rrm.isUniqueRequestEntry(methodName))) {

      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();
        synchronized (currentOrder) {

          // validate the payments
          preApplyPayments(pRequest, pResponse);

          //if there is any form error found redirect to error page
          if(getFormError()){
            return checkFormRedirect(null, getApplyCreditCardErrorURL(), pRequest, pResponse);
          }

        	 // get ip address from request
            String ipAddress = getPurchaseProcessHelper().getIPAddress(pRequest);

            if (!StringUtils.isEmpty(ipAddress)) {
              // set ip address to order
              ((MFFOrderTools) getOrderManager().getOrderTools()).populateIPAddressToOrder(currentOrder, ipAddress);
            }

            // get device id address from request
            String deviceId = getPurchaseProcessHelper().getDeviceId(pRequest);

            if (!StringUtils.isEmpty(deviceId)) {
              // set ip address to order
              ((MFFOrderTools) getOrderManager().getOrderTools()).populateDeviceIdToOrder(currentOrder, deviceId);
            }

          try {
            if (getCheckoutManager().isOrderRequiresCreditCard()) {
              CreditCard creditCardPaymentGroup = (CreditCard) getCreditCardPaymentGroup(currentOrder);
              if (creditCardPaymentGroup != null) {
                boolean tokenizeCard = false;
                String ccNumber = getCreditCardNumber();

                // no cc number found for any reason, redirect user to error page
                if (StringUtils.isBlank(ccNumber)) {
                  getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_PAYMENT_GROUP_NULL, true, this, pRequest);
                  vlogError("No Creditcard Number in the form for unknown reason redirecing user to error page: orderId:{0}",currentOrder.getId());
                  return checkFormRedirect(null, getAvsBillingErrorURL(), pRequest, pResponse);
                }

                //if new card or user is trying to edit the new card get token from payment group
                if (getCreditCardId().equals(CREDIT_CARD_ID_ZERO)){

                  //get the last 4 digits of the card
                  if (ccNumber.length() > 4)
                    ccNumber = ccNumber.substring(ccNumber.length() - 4);

                  setTokenId(((AciCreditCard)creditCardPaymentGroup).getTokenNumber());
                  String existingCCNum = creditCardPaymentGroup.getCreditCardNumber();


                //tokenize only if new card or edited the new card(card number changed)
                  if(getTokenId() == null || getTokenId().isEmpty() || existingCCNum == null || existingCCNum.isEmpty()){
                    vlogDebug("Card is not tokenized for order:{0}",currentOrder.getId());
                    tokenizeCard=true;
                  }else if(!ccNumber.equalsIgnoreCase(existingCCNum)){
                    vlogDebug("User edited the card tokenize again for order:{0}", currentOrder.getId());
                    tokenizeCard=true;
                  }

                  vlogDebug("Order Id:{0}, tokenizeCard:{1}",currentOrder.getId(),tokenizeCard);

                  //Call ACI service and perform the tokenization
                  if(tokenizeCard){
                    String lTokenNum = null;
                    try {
                      lTokenNum = tokenizeCreditCard(pRequest, pResponse);
                    } catch (AciPipelineException e) {
                      if(MFFConstants.CC_TOKEN_EIVCCN_ERROR_CODE.equalsIgnoreCase(e.getKey())) {
                        getFormExceptionGenerator().generateException(MFFConstants.MSG_CC_TOKEN_EIVCCN_ERROR, true, this, pRequest);
                      }else {
                        getFormExceptionGenerator().generateException(MFFConstants.MSG_CC_TOKEN_ERROR, true, this, pRequest);
                      }

                      return checkFormRedirect(null, getAvsBillingErrorURL(), pRequest, pResponse);
                    }
                    setTokenId(lTokenNum);
                  }
                }

                //for any reason if tokenid is null here throw an error
                if (getTokenId() == null || getTokenId().isEmpty()) {
                  getFormExceptionGenerator().generateException(MFFConstants.MSG_CC_TOKEN_ERROR, true, this, pRequest);
                  return checkFormRedirect(null, getAvsBillingErrorURL(), pRequest, pResponse);
                }else{
                  vlogDebug("Tokenization successful for order id:{0}", currentOrder.getId());
                }

                updatePaymentGroupInfo(creditCardPaymentGroup, getBillingAddress());
              } else {
                getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_PAYMENT_GROUP_NULL, true, this, pRequest);
                return checkFormRedirect(null, getAvsBillingErrorURL(), pRequest, pResponse);
              }
            }
            // set contact email on the order
            currentOrder.setContactEmail(getContactEmail());

            // run payment pipeline
            runPaymentPipeline();

            //update the order
            ((MFFOrderManager) getOrderManager()).updateOrder(currentOrder, methodName);

          } catch (CommerceException e) {
            vlogError(e, "Exception when updating order in handleApplyCreditCard");
            //vlogError("Exception when updating order in handleApplyCreditCard", e);
            getCheckoutManager().setPaymentErrorMessage("Exception when updating order in handleApplyCreditCard");
            getFormExceptionGenerator().generateException(MFFConstants.UPDATE_ORDER_ERROR, true, this, pRequest);
          } catch (RunProcessException e) {
            vlogError("Exception when validating payment groups in handleApplyCreditCard", e);
            getCheckoutManager().setPaymentErrorMessage("Exception when validating payment groups in handleApplyCreditCard");
            getFormExceptionGenerator().generateException(MFFConstants.UPDATE_ORDER_ERROR, true, this, pRequest);
          }

          if (getFormError()) {
            setTokenId(null);
            return checkFormRedirect(null, getAvsBillingErrorURL(), pRequest, pResponse);
          }

          vlogDebug("Registered User:{0} Save the payment method: {1}", hardLoggedIn, isSavePaymentMethod());
          if (hardLoggedIn && isSavePaymentMethod()) {
            if (getCreditCardId().equals(CREDIT_CARD_ID_ZERO)) {
              getCheckoutManager().setSavePaymentMethod(true);
              getCheckoutManager().setPaymentName(getPaymentName());
            } else {
              getCheckoutManager().setSavePaymentMethod(false);
            }
          }
        }
      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }
        if (rrm != null) {
          rrm.removeRequestEntry(methodName);
        }
      }
    }

    if (!getFormError()) {
      getCheckoutManager().authorizeReviewStep();
      getCheckoutManager().setCardAuthorized(true);
    } else {
      getCheckoutManager().authorizePaymentStep();
    }
    setTokenId(null);
    vlogDebug("PaymentGroupFormHandler: handleApplyCreditCard - Exiting");
    return checkFormRedirect(getAvsBillingSuccessURL(), getAvsBillingErrorURL(), pRequest, pResponse);

  }

  /**
   * validate credit card and add form exception if there are any errors
   *
   * @param pRequest
   * @return
   */
  @SuppressWarnings("static-access")
  protected boolean validateCreditCard(DynamoHttpServletRequest pRequest) {
    vlogDebug("PaymentGroupFormHandler: validateCreditCard - Entering");
    if (StringUtils.isBlank(getNameOnCard())) {
      getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_NAME_REQUIRED, true, this, pRequest);
    }
    if (StringUtils.isBlank(getCreditCardType())) {
      getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_TYPE_REQUIRED, true, this, pRequest);
    }

    String cvv = getCreditCardCVV();

    if (StringUtils.isBlank(cvv)) {
      getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_CVV_REQUIRED, true, this, pRequest);
    }
    for (int i = 0; i < cvv.length(); ++i) {
      char c = cvv.charAt(i);
      if ((((c < '0') || (c > '9'))) && (c != ' ')) {
        getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_INVALID_CVV, true, this, pRequest);
      }
    }

    AciCreditCardInfo ccInfo = new AciCreditCardInfo();
    ccInfo.setExpirationYear(getCreditCardExpYear());
    ccInfo.setExpirationMonth(getCreditCardExpMonth());

    String ccNumber = (String) getCreditCardNumber();

    if (ccNumber != null) {
      ccNumber = StringUtils.removeWhiteSpace(ccNumber);
    }

    ccInfo.setCreditCardNumber(ccNumber);
    String cardType=StringUtils.removeWhiteSpace(getCreditCardType());
    ccInfo.setCreditCardType(cardType);

    if (cvv != null) {
      cvv = StringUtils.removeWhiteSpace(cvv);
    }
    ccInfo.setSecurityCode(cvv);

    //if not new card, get the token number and set it to the object
    if (!getCreditCardId().equals(CREDIT_CARD_ID_ZERO)) {
      ccInfo.setTokenNumber(getTokenId());
    }

    // verify card number & card type etc.
    int ccreturn = getCreditCardTools().verifyCreditCard(ccInfo);



    if (ccreturn != getCreditCardTools().SUCCESS) {
      String msg = getCreditCardTools().getStatusCodeMessage(ccreturn);
      addFormException(new DropletFormException(msg, null));
      return false;
    }

    /*
     * The following is to ensure that the credit card type is set to Fleet Rewards VISA if the following criteria is met:
     * the user selected VISA
     * the user entered a Fleet Rewards VISA card number
     */
    boolean hardLoggedIn = ((MFFProfile) getProfile()).isHardLoggedIn();
    if (hardLoggedIn && (getCreditCardId() != null && !getCreditCardId().equals(CREDIT_CARD_ID_ZERO))) {
      vlogDebug("This is a saved card and we will not modify the credit card type");
    } else {
      if (cardType.equalsIgnoreCase("visa")) {
        ccInfo.setCreditCardType("millsVisa");
        if (getCreditCardTools().verifyCreditCard(ccInfo) == getCreditCardTools().SUCCESS) {
          setCreditCardType("millsVisa");
        }
      }
    }

    vlogDebug("PaymentGroupFormHandler: validateCreditCard - Exiting");
    return true;
  }

  public boolean handleApplyGiftCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    final String methodName = MFFConstants.HANDLE_APPLY_GIFT_CARD_METHOD_NAME;

    vlogDebug("PaymentGroupFormHandler: Entering: {0}", methodName);

    if(getMffEnvironment().isRestrictGCPaymentGCOnlyOrder()){
      vlogDebug("PaymentGroupFormHandler: Restricting GC Payment if order contains only GC Products");
      boolean gcProductOnlyOrder = false;
      //verify if order contains only giftcard product, if yes do not let customer pay this order with GiftCard Payment
      if(getOrder() != null){
        if(getOrder().getCommerceItemCount() >0){
          int gcItemCount = 0;

          List items = getOrder().getCommerceItems();
          for(Object item : items){
            MFFCommerceItemImpl mffItem = (MFFCommerceItemImpl) item;
            if(mffItem.isGiftCard())
              gcItemCount++;
          }
          if(gcItemCount == getOrder().getCommerceItemCount()){
            gcProductOnlyOrder=true;
          }
        }
      }

      if(gcProductOnlyOrder){
        getFormExceptionGenerator().generateException(MFFConstants.MSG_GC_PRODUCT_ONLY_ERROR, true, this, pRequest);
        return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
      }
    }
    // If GC Number is missing, add error:
    if (StringUtils.isBlank(getGiftCardNumber())) {
      getFormExceptionGenerator().generateException(MFFConstants.NO_GIFT_CARD_NUMBER, true, this, pRequest);
      return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
    }

    if (StringUtils.isBlank(getGiftCardPin())) {
      getFormExceptionGenerator().generateException(MFFConstants.NO_GIFT_CARD_PIN, true, this, pRequest);
      return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
    }

    if (getFormError()) {
      return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    Transaction tr = null;

    if ((rrm == null) || (rrm.isUniqueRequestEntry(methodName))) {
      try {
        tr = ensureTransaction();

        processGiftCardPayment(pRequest);

      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }
        if (rrm != null) {
          rrm.removeRequestEntry(methodName);
        }
      }
    } else {
      return false;
    }

    vlogDebug("PaymentGroupFormHandler: Exiting: {0}", methodName);
    return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
  }

  private boolean processGiftCardPayment(DynamoHttpServletRequest pRequest) {
    vlogDebug("Start of processGiftCardPayment()");

    String giftCardNumber = getGiftCardNumber();
    String pin = getGiftCardPin();

    // If Pin number was entered, but GC number wasn't, give an error:
    if (giftCardNumber == null || giftCardNumber.isEmpty()) {
      getFormExceptionGenerator().generateException(MFFConstants.NO_GIFT_CARD_NUMBER, true, this, pRequest);
      return false;
    }

    boolean gcCreditEntered = false;

    if (giftCardNumber != null && !giftCardNumber.isEmpty()) {
      gcCreditEntered = true;
    }

    boolean initializeGiftCardStatus = true;

    if (gcCreditEntered) {

      if (isOrderHaveMaxGCS()) {

         Object[] msgArgs = new Object[1];
         msgArgs[0] = getMffEnvironment().getMaxGiftCardsAllowedPerOrder();
         String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_TOO_MANY_GIFT_CARDS));
         String msg = MessageFormat.format(resourceMsg, msgArgs);
         getFormExceptionGenerator().generateException(msg, false, this, pRequest);

        return false;
      }

      initializeGiftCardStatus = initializeGiftCardPayment(giftCardNumber, pin, pRequest);

      setGiftCardNumber(MFFConstants.EMPTY_STRING);
      setGiftCardPin(MFFConstants.EMPTY_STRING);

      if (!initializeGiftCardStatus) return false;
    }

    try {
      getCheckoutManager().updatePaymentGroups(true);
    } catch (CommerceException exc) {
      vlogError("There was an error when updating payment groups for order:" + getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.UPDATE_ORDER_ERROR, true, this, pRequest);
      return false;
    }
    return initializeGiftCardStatus;
  }

  private boolean initializeGiftCardPayment(String pGiftCardNumber, String pGiftCardPin, DynamoHttpServletRequest pRequest) {

    GiftCardPaymentInfo lGiftCardInfo = getGiftCardManager().checkGiftCardBalance(pGiftCardNumber, pGiftCardPin, getOrder().getId());

    /*if (!lGiftCardInfo.getFoundInSystem()) {
      addFormException(new DropletFormException(MFFConstants.getEXTNResources().getString(MFFConstants.INVALID_GIFT_CARD), "", MFFConstants.INVALID_GIFT_CARD));
      return false;
    }*/

    if (!lGiftCardInfo.isTransactionSuccess()) {
      String errorMessage = lGiftCardInfo.getErrorMessage();
      vlogError("Error Message Code while adding giftcard:{0}", errorMessage);
      boolean isMillsMoneyEnabled=false;
      SiteContextManager.getCurrentSite();
      Site curSite = SiteContextManager.getCurrentSite();
      if (curSite != null){
    	  isMillsMoneyEnabled = (Boolean)curSite.getPropertyValue("isEnableMillsMoney");
      }
      if(isMillsMoneyEnabled) {
    	  addFormException(new DropletFormException(MFFConstants.getEXTNResources().getString(MFFConstants.INVALID_MILLS_MONEY), "", MFFConstants.INVALID_MILLS_MONEY));
      } else {
    	  addFormException(new DropletFormException(MFFConstants.getEXTNResources().getString(MFFConstants.INVALID_GIFT_CARD), "", MFFConstants.INVALID_GIFT_CARD));
      }
      return false;
    }

    if (lGiftCardInfo.getAmountOnCard() <= 0) {
      vlogError("Error:{0}", "Zero Balance on Giftcard");
      addFormException(new DropletFormException(MFFConstants.getEXTNResources().getString(MFFConstants.ZERO_BALANCE_GIFT_CARD), "", MFFConstants.ZERO_BALANCE_GIFT_CARD));
      return false;
    }

    if (lGiftCardInfo.getAmountOnCard() > 0) {

      // see if this Gift Card is already in the list of GiftCardPaymentInfos.
      // If so, return a reference to the one already in the list:
      GiftCardPaymentInfo existingEntry = findInGiftCardPaymentInfos(pGiftCardNumber);

      if (existingEntry == null) {
        vlogDebug("New Gift Card");
        getCheckoutManager().getGiftCardPaymentInfos().put(pGiftCardNumber, lGiftCardInfo);
        existingEntry = lGiftCardInfo;
      } else {
        vlogDebug("Existing Gift Card");
        double previousAmountOnCard = existingEntry.getAmountOnCard();
        if (previousAmountOnCard == lGiftCardInfo.getAmountOnCard()) {
          addFormMessage("GiftCard is already on the order");
        } else {
          addFormMessage("Card Updated");
        }

        existingEntry.setAmountOnCard(lGiftCardInfo.getAmountOnCard());
        existingEntry.setFoundInSystem(lGiftCardInfo.getFoundInSystem());
        existingEntry.setDisplayOnPage(lGiftCardInfo.getDisplayOnPage());
        existingEntry.setAmountAssignedToThisOrder(lGiftCardInfo.getAmountAssignedToThisOrder());
        existingEntry.setAmountRemainingToSpend(lGiftCardInfo.getAmountRemainingToSpend());
      }

      Order currentOrder = getOrder();
      synchronized (currentOrder) {
        MFFGiftCardPaymentGroup giftCardPG = getCheckoutManager().fetchGiftCardPaymentGroup(existingEntry.getGiftCardNumber());
        if (giftCardPG == null) {
          try {
            String requstId = getOrder().getId();
            if (requstId != null && requstId.length() > 2) {
              requstId = requstId.substring(1, 5);
            }

            MFFGiftCardPaymentGroup lGiftCard = (MFFGiftCardPaymentGroup) getPaymentGroupManager().createPaymentGroup("giftCard");
            lGiftCard.setCardNumber(pGiftCardNumber);
            lGiftCard.setEan((pGiftCardPin));
            lGiftCard.setLocalLockId((requstId));

            getPaymentGroupManager().addPaymentGroupToOrder(currentOrder, lGiftCard);
            ((MFFOrderManager) getOrderManager()).updateOrder(getOrder(), "Apply Gift Card Payment");
          } catch (CommerceException e) {
            if (isLoggingError()) {
              logError("There is an error while adding a new gift card Payment Group to the Order!!!:" + e);
            }
            addFormException(new DropletFormException(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_GIFTCARD_ADD_ERROR), "", MFFConstants.MSG_GIFTCARD_ADD_ERROR));
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * find if it is an existing gift card
   *
   * @param pGiftCardNum
   * @return
   */
  private GiftCardPaymentInfo findInGiftCardPaymentInfos(String pGiftCardNum) {
    if (getCheckoutManager().getGiftCardPaymentInfos() == null) {
      if (isLoggingError()) {
        logError("GiftCardPaymentInfos shoudln't be null!!!!  Please check on this! Will assign a new value...");
      }
      getCheckoutManager().setGiftCardPaymentInfos(new LinkedHashMap<String, GiftCardPaymentInfo>());
    }

    if (getCheckoutManager().getGiftCardPaymentInfos().containsKey(pGiftCardNum)) {
      return (GiftCardPaymentInfo) getCheckoutManager().getGiftCardPaymentInfos().get(pGiftCardNum);
    } else {
      return null;
    }
  }

  public boolean handleRemoveGiftCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    resetFormExceptions();
    getFormMessages().clear();

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      String giftCardToRemove = getGiftCardToRemove();

      addFormMessage(MFFConstants.getEXTNResources().getString(MFFConstants.GIFT_CARD_SUCCESSFULLY_REMOVED));

      Order currentOrder = getOrder();
      PaymentGroup giftCardPaymentGroupToRemove = getCheckoutManager().fetchGiftCardPaymentGroup(giftCardToRemove);

      if (giftCardPaymentGroupToRemove == null) {
        vlogError("HandleRemoveGiftCard: Gift Card Payment Group is not found on order");
      } else {
        synchronized (currentOrder) {
          getPaymentGroupManager().removeAllRelationshipsFromPaymentGroup(currentOrder, giftCardPaymentGroupToRemove.getId());

          vlogDebug("HandleRemoveGiftCard: remove Gift Card");

          getPaymentGroupManager().removePaymentGroupFromOrder(currentOrder, giftCardPaymentGroupToRemove.getId());
          ((MFFOrderManager) getOrderManager()).updateOrder(getOrder(), "After removal of GC");
        }
      }

      getCheckoutManager().getGiftCardPaymentInfos().remove(giftCardToRemove);

      getCheckoutManager().updatePaymentGroups(true);

    } catch (CommerceException e) {
      if (isLoggingError()) {
        logError(e);
      }
      addFormException(new DropletFormException(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.UPDATE_ORDER_ERROR), "", MFFConstants.UPDATE_ORDER_ERROR));
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }

    return checkFormRedirect(getRemoveGiftCardSuccessURL(), getRemoveGiftCardErrorURL(), pRequest, pResponse);
  }

  /**
   * Initialize Credit Card Data for any of the following scenarios
   * <ul>
   * <li>If Registered User and choose to apply saved card, load data from
   * selected card</li>
   * <li>If Registered User and choose to use new credit card, load data from
   * form fields</li>
   * <li>If Guest User then load data from form fields</li>
   * </ul>
   *
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  public boolean initCreditCardData(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogDebug("PaymentGroupFormHandler: initCreditCardData for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());

    ContactInfo billAddress = null;
    boolean hardLoggedIn = ((MFFProfile) getProfile()).isHardLoggedIn();

    // if user is logged in and choose to apply a saved card
    // then load data from selected card
    if (hardLoggedIn && (getCreditCardId() != null && !getCreditCardId().equals(CREDIT_CARD_ID_ZERO))) {

      vlogDebug("Selected Card Id: {0}", getCreditCardId());

      RepositoryItem selectedCreditCard = getProfileTools().getCreditCardById(getCreditCardId());

      String nickname = getProfileTools().getCreditCardNickname(getProfile(), selectedCreditCard);
      if (nickname == null) {
        // if the nickname was invalid, the user is attempting a hack
        vlogWarning("Caught invalid attempt to access non-owned credit card by user: " + getProfile().getRepositoryId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_INVALID_CC_ACCESS_ERROR, true, this, pRequest);
        return false;
      }

      // load data from the selected card
      loadCreditCardDetailsFromCard(selectedCreditCard);

      //if it is a saved card, there should be a token number on the card otherwise we will not be able to process the payment
      //add form exception and request user to select different card/provide new card
      if(getTokenId() == null || getTokenId().isEmpty()){
        getFormExceptionGenerator().generateException(MFFConstants.MSG_SAVED_CARD_TOKEN_ERROR, true, this, pRequest);
        return false;
      }

      // load billing address from selected card
      billAddress = setBillingAddressFromCard((RepositoryItem) selectedCreditCard.getPropertyValue(((CommercePropertyManager) getProfileTools().getPropertyManager()).getBillingAddressPropertyName()));
      getCheckoutManager().setBillingAvsVerified(true);
    } else {
      // if guest user and user checked isSameAddressAsShipping
      if (getCreditCardId().equals(CREDIT_CARD_ID_ZERO) && isSameAddressAsShipping()) {
        vlogDebug("isSameAddressAshShipping selected");
        billAddress = getCheckoutManager().getShippingAddressInForm();
        getCheckoutManager().setBillingAvsVerified(true);
        vlogDebug("Billing Address: {0}", billAddress);
      } else {
        // guest user and entering billing address
        billAddress = createBillingAddressFromFormFields(pRequest);
        // check if AVS is already performed
        if (isAddressVerified())
          getCheckoutManager().setBillingAvsVerified(true);
        else
          getCheckoutManager().setBillingAvsVerified(false);
      }
    }
    if (this.hasFormError()) {
      return false;
    }

    this.setBillingAddress(billAddress);

    getCheckoutManager().setBillingAddressSameAsShipping(isSameAddressAsShipping());
    getCheckoutManager().setCreditCardId(getCreditCardId());
    getCheckoutManager().setCardAuthorized(false);
    getCheckoutManager().setBillingAddressInForm(billAddress);
    return true;
  }

  /**
   * Perform AVS check on billing Address This method will set Suggested Address
   * to display in the UI Also set displayAVSModal value to decide Modal display
   * in the UI
   *
   * @return
   */
  public boolean performBillingAVS() {

    if (getOrder() != null) vlogInfo("Action - performBillingAVS for order - {0}", getOrder().getId());

    boolean displayAvsModal = false;
    ContactInfo billAddress = this.getBillingAddress();
    AddressVerificationVO avsBillingVO = getCheckoutManager().performAVSCheck(billAddress);

    if (avsBillingVO != null && avsBillingVO.isDisplaySuggestion()) {
      vlogDebug("******** Kick the avs address modal");
      getCheckoutManager().setBillingAvsVO(avsBillingVO);
      displayAvsModal = true;
    } else {
      getCheckoutManager().setBillingAvsVerified(false);
      displayAvsModal = false;
    }
    setDisplayAvsModal(displayAvsModal);
    return displayAvsModal;
  }

  /**
   * run payment pipeline to validate payment groups
   *
   * @throws RunProcessException
   */
  private void runPaymentPipeline() throws RunProcessException {
    vlogInfo("Action: runPaymentPipeline for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());
    vlogDebug("Will call this Validate for Checkout chain:" + getValidatePaymentGroupsChainId());

    @SuppressWarnings("rawtypes")
    Map parameterMap = createRepriceParameterMap();

    // runProcessValidatePaymentGroups(getShoppingCart().getCurrent(),
    // getUserPricingModels(), getUserLocale(), getProfile(), parameterMap);

    PipelineResult result = runProcess("moveToConfirmation", getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), parameterMap);
    processPipelineErrors(result);
  }

  /**
   * get current payment group from the current order
   *
   * @param currentOrder
   * @return
   */
  public CreditCard getCreditCardPaymentGroup(MFFOrderImpl currentOrder) {
    @SuppressWarnings("unchecked")
    List<PaymentGroup> list = currentOrder.getPaymentGroups();

    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        PaymentGroup pg = (PaymentGroup) list.get(i);

        if (pg instanceof CreditCard) {
          return (CreditCard) pg;
        }
      }
    }
    return null;
  }

  /**
   * Set Payment Group values
   *
   * @param pTokenNumber
   * @param pCreditCard
   * @param pBillingAddress
   */
  private void updatePaymentGroupInfo(CreditCard pCreditCard, ContactInfo pBillingAddress) {

    AciCreditCard creditCard = (AciCreditCard) pCreditCard;

    vlogDebug("Copying credit card information to the Payment Group...");
    creditCard.setTokenNumber(getTokenId());
    creditCard.setNameOnCard(getNameOnCard());
    String ccNumber = getCreditCardNumber();
    if (ccNumber.length() > 4) {
      ccNumber = ccNumber.substring(ccNumber.length() - 4);
    }
    if(!StringUtils.isEmpty(getMopTypeCode())){
    	creditCard.setMopTypeCode(getMopTypeCode().trim().toUpperCase());
    }
    creditCard.setCreditCardNumber(ccNumber);
    creditCard.setExpirationMonth(getCreditCardExpMonth());
    creditCard.setExpirationYear(getCreditCardExpYear());
    creditCard.setCardVerificationNumber(getCreditCardCVV());
    String cardType=StringUtils.removeWhiteSpace(getCreditCardType());
    creditCard.setCreditCardType(cardType);
    creditCard.setBillingAddress(pBillingAddress);

  }

  /**
   * This method generates token by calling ACI service
   *
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  private String tokenizeCreditCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, AciPipelineException {
    vlogDebug("tokenizeCreditCard(): Called.");
    String tokenNumber = null;
      AciCreditCardInfo aciCardInfo = generateCreditCardInfo(getBillingAddress());
      getAciService().tokenizeCreditCard(aciCardInfo);
      tokenNumber = aciCardInfo.getTokenNumber();
      String mopType=aciCardInfo.getMopTypeCd();
      if(!StringUtils.isEmpty(mopType))
    	  setMopTypeCode(mopType.trim().toUpperCase());
    return tokenNumber;
  }

  /**
   * Create ACI CreditCardInfo bean
   *
   * @param pBillingAddress
   * @return
   */
  private AciCreditCardInfo generateCreditCardInfo(ContactInfo pBillingAddress) {
    AciCreditCardInfo ccInfo = new AciCreditCardInfo();
    ccInfo.setCreditCardNumber(getCreditCardNumber());
    ccInfo.setSecurityCode(getCreditCardCVV());
    String cardType=StringUtils.removeWhiteSpace(getCreditCardType());;
    ccInfo.setCreditCardType(cardType);
    ccInfo.setExpirationMonth(getCreditCardExpMonth());
    ccInfo.setExpirationYear(getCreditCardExpYear());
    ccInfo.setOnlineOrder(true);
    ccInfo.setBillingAddress(pBillingAddress);
    return ccInfo;
  }

  /**
   * Validate the billing address This method calls OOTB address validation
   * service
   *
   * @param pRequest
   * @param billAddress
   * @return
   */
  private boolean validateBillingAddress(DynamoHttpServletRequest pRequest, ContactInfo billAddress) {
    boolean valid = true;
    getAddressValidator().validateAddress(billAddress, pRequest, this);

    @SuppressWarnings("unchecked")
    List<DropletException> errorList = getFormExceptions();

    vlogDebug("PaymentGroupFormHandler: Error List Size:{0}", errorList.size());

    if (errorList != null && !errorList.isEmpty()) {
      for (int i = 0; i < errorList.size(); i++) {
        vlogDebug("PaymentGroupFormHandler: Error:{0}, : Message:{1}", i, errorList.get(i).getMessage());
        valid = false;
      }
    }
    return valid;
  }

  /**
   * Create the billing address from the form fields
   *
   * @param pRequest
   * @return
   */
  private ContactInfo createBillingAddressFromFormFields(DynamoHttpServletRequest pRequest) {

    ContactInfo billAddress = new ContactInfo();
    billAddress.setFirstName(getFirstName());
    billAddress.setLastName(getLastName());
    billAddress.setAddress1(getAddress1());
    billAddress.setAddress2(getAddress2());
    billAddress.setCity(getCity());
    billAddress.setState(getState());
    billAddress.setPostalCode(getPostalCode());
    billAddress.setCountry(getCountry());
    if (StringUtils.isBlank(billAddress.getCountry())) {
      billAddress.setCountry(MFFConstants.DEFAULT_COUNTRY);
    }
    billAddress.setPhoneNumber(getPhoneNumber());
    boolean valid = validateBillingAddress(pRequest, billAddress);
    if (!valid) {
      getFormExceptionGenerator().generateException(MFFConstants.BILLING_ADDRESS_MISSING_DATA, true, this, pRequest);
    }
    return billAddress;
  }

  /**
   * Load billing address form the selected card
   *
   * @param pAddress
   * @return
   */
  private ContactInfo setBillingAddressFromCard(RepositoryItem pAddress) {
    vlogDebug("PaymentGroupFormHandler: setBillingAddressFromCard - Entering");
    ContactInfo billAddress = null;
    if (pAddress != null) {
      billAddress = new ContactInfo();
      billAddress.setFirstName((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_FIRST_NAME));
      billAddress.setLastName((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_LAST_NAME));
      billAddress.setAddress1((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_ADDRESS1));
      billAddress.setAddress2((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_ADDRESS2));
      billAddress.setCity((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_CITY));
      billAddress.setState((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_STATE));
      billAddress.setPostalCode((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_POSTAL_CODE));
      billAddress.setCountry((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_COUNTRY));
      if (StringUtils.isBlank(billAddress.getCountry())) {
        billAddress.setCountry(MFFConstants.DEFAULT_COUNTRY);
      }
      billAddress.setPhoneNumber((String) pAddress.getPropertyValue(MFFConstants.ADDRESS_PHONE_NUMBER));
      setBillingAddress(billAddress);
    }
    vlogDebug("PaymentGroupFormHandler: setBillingAddressFromCard - Exiting");
    return billAddress;
  }

  /**
   * Load Credit card details from the selected card
   *
   * @param pCreditCard
   */
  private void loadCreditCardDetailsFromCard(RepositoryItem pCreditCard) {

    vlogDebug("PaymentGroupFormHandler: loadCreditCardDetailsFromCard - Entering");
    if (pCreditCard != null) {
      setCreditCardNumber((String) pCreditCard.getPropertyValue(MFFConstants.FIELD_CARD_NUMBER));
      setCreditCardExpMonth((String) pCreditCard.getPropertyValue(MFFConstants.FIELD_CARD_EXP_MONTH));
      setCreditCardExpYear((String) pCreditCard.getPropertyValue(MFFConstants.FIELD_CARD_EXP_YEAR));
      setCreditCardType((String) pCreditCard.getPropertyValue(MFFConstants.FIELD_CARD_TYPE));
      setNameOnCard((String) pCreditCard.getPropertyValue(MFFConstants.FIELD_NAME_ON_CARD));
      setTokenId((String) pCreditCard.getPropertyValue(MFFConstants.FIELD_TOKEN_ID));
      String mopTypeCode=(String) pCreditCard.getPropertyValue(MFFConstants.FIELD_MOP_TYPE_CODE);
      if(!StringUtils.isEmpty(mopTypeCode)){
    	  setMopTypeCode(mopTypeCode.trim().toUpperCase());
      }
    }
    vlogDebug("PaymentGroupFormHandler: loadCreditCardDetailsFromCard - Exiting");
  }

  private boolean validatePaymentName(DynamoHttpServletRequest pRequest, String paymentName) {
    // Validate nickName only for hard logged in users
    if (((MFFProfile) getProfile()).isHardLoggedIn()) {
      // Validate NickName is present
      if (StringUtils.isBlank(paymentName)) {
        getFormExceptionGenerator().generateException(MFFConstants.PAYMENT_NAME_MISSING, true, this, pRequest);
        return false;
      } else {
        if (getProfileTools().isDuplicateCreditCardNickname(getProfile(), paymentName)) {
          getFormExceptionGenerator().generateException(MFFConstants.MSG_DUPLICATE_PAYMENT_NAME, true, this, pRequest);
          return false;
        }
      }
    }
    return true;
  }

  /******************
   * Helper Methods *
   *****************/

  public boolean hasFormError() {
    if (getFormError()) {
      getCheckoutManager().authorizeShippingStep();
      logFormErrors();
      return true;
    }
    return false;
  }

  private void logFormErrors() {
    @SuppressWarnings("unchecked")
    List<DropletException> errorList = getFormExceptions();

    vlogDebug("PaymentGroupFormHandler: Error List Size:{0}", errorList.size());

    if (errorList != null && !errorList.isEmpty()) {
      for (int i = 0; i < errorList.size(); i++) {
        vlogDebug("PaymentGroupFormHandler: Error:{0}, : Message:{1}", i, errorList.get(i).getMessage());
      }
    }
  }

  private boolean isOrderHaveMaxGCS() {
    if (getCheckoutManager().getGiftCardPaymentInfos().size() >= getMffEnvironment().getMaxGiftCardsAllowedPerOrder()) {
      return true;
    }
    return false;
  }

  /**
   * @return the mCreditCardId
   */
  public String getCreditCardId() {
    return mCreditCardId;
  }

  /**
   * @param pCreditCardId
   *          the mCreditCardId to set
   */
  public void setCreditCardId(String pCreditCardId) {
    this.mCreditCardId = pCreditCardId;
  }

  /**
   * @return the mCreditCardType
   */
  public String getCreditCardType() {
    return mCreditCardType;
  }

  /**
   * @param pCreditCardType
   *          the mCreditCardType to set
   */
  public void setCreditCardType(String pCreditCardType) {
    this.mCreditCardType = pCreditCardType;
  }

  /**
   * @return the mCreditCardNumber
   */
  public String getCreditCardNumber() {
    return mCreditCardNumber;
  }

  /**
   * @param pCreditCardNumber
   *          the mCreditCardNumber to set
   */
  public void setCreditCardNumber(String pCreditCardNumber) {
    this.mCreditCardNumber = pCreditCardNumber;
  }

  /**
   * @return the mCreditCardExpMonth
   */
  public String getCreditCardExpMonth() {
    return mCreditCardExpMonth;
  }

  /**
   * @param pCreditCardExpMonth
   *          the mCreditCardExpMonth to set
   */
  public void setCreditCardExpMonth(String pCreditCardExpMonth) {
    this.mCreditCardExpMonth = pCreditCardExpMonth;
  }

  /**
   * @return the mCreditCardExpYear
   */
  public String getCreditCardExpYear() {
    return mCreditCardExpYear;
  }

  /**
   * @param pCreditCardExpYear
   *          the mCreditCardExpYear to set
   */
  public void setCreditCardExpYear(String pCreditCardExpYear) {
    this.mCreditCardExpYear = pCreditCardExpYear;
  }

  /**
   * @return the mCreditCardCVV
   */
  public String getCreditCardCVV() {
    return mCreditCardCVV;
  }

  /**
   * @param pCreditCardCVV
   *          the mCreditCardCVV to set
   */
  public void setCreditCardCVV(String pCreditCardCVV) {
    this.mCreditCardCVV = pCreditCardCVV;
  }

  /**
   *
   * @return
   */
  public String getNameOnCard() {
    return mNameOnCard;
  }

  /**
   *
   * @param mNameOnCard
   */
  public void setNameOnCard(String mNameOnCard) {
    this.mNameOnCard = mNameOnCard;
  }

  /**
   * @return the mTokenId
   */
  public String getTokenId() {
    return mTokenId;
  }

  /**
   * @param pTokenId
   *          the mTokenId to set
   */
  public void setTokenId(String pTokenId) {
    this.mTokenId = pTokenId;
  }

  /**
   * @return the mMopType
   */
  public String getMopTypeCode() {
    return mMopTypeCode;
  }

  /**
   * @param pTokenId
   *          the mTokenId to set
   */
  public void setMopTypeCode(String pMopTypeCode) {
    this.mMopTypeCode = pMopTypeCode;
  }

  /**
   * @return the mSameAddressAsShipping
   */
  public boolean isSameAddressAsShipping() {
    return mSameAddressAsShipping;
  }

  /**
   * @param pSameAddressAsShipping
   *          the mSameAddressAsShipping to set
   */
  public void setSameAddressAsShipping(boolean pSameAddressAsShipping) {
    this.mSameAddressAsShipping = pSameAddressAsShipping;
  }

  /**
   * @return the mSameAddressAsInAddressBook
   */
  public boolean isSameAddressAsInAddressBook() {
    return mSameAddressAsInAddressBook;
  }

  /**
   * @param pSameAddressAsInAddressBook
   *          the mSameAddressAsInAddressBook to set
   */
  public void setSameAddressAsInAddressBook(boolean pSameAddressAsInAddressBook) {
    this.mSameAddressAsInAddressBook = pSameAddressAsInAddressBook;
  }

  /**
   * @return the mAddressNickname
   */
  public String getAddressNickname() {
    return mAddressNickname;
  }

  /**
   * @param pAddressNickname
   *          the mAddressNickname to set
   */
  public void setAddressNickname(String pAddressNickname) {
    this.mAddressNickname = pAddressNickname;
  }

  /**
   * @return the mAddressId
   */
  public String getAddressId() {
    return mAddressId;
  }

  /**
   * @param pAddressId
   *          the mAddressId to set
   */
  public void setAddressId(String pAddressId) {
    this.mAddressId = pAddressId;
  }

  /**
   * @return the mFirstName
   */
  public String getFirstName() {
    return mFirstName;
  }

  /**
   * @param pFirstName
   *          the mFirstName to set
   */
  public void setFirstName(String pFirstName) {
    this.mFirstName = pFirstName;
  }

  /**
   * @return the mLastName
   */
  public String getLastName() {
    return mLastName;
  }

  /**
   * @param pLastName
   *          the mLastName to set
   */
  public void setLastName(String pLastName) {
    this.mLastName = pLastName;
  }

  /**
   * @return the mAddress1
   */
  public String getAddress1() {
    return mAddress1;
  }

  /**
   * @param pAddress1
   *          the mAddress1 to set
   */
  public void setAddress1(String pAddress1) {
    this.mAddress1 = pAddress1;
  }

  /**
   * @return the mAddress2
   */
  public String getAddress2() {
    return mAddress2;
  }

  /**
   * @param pAddress2
   *          the mAddress2 to set
   */
  public void setAddress2(String pAddress2) {
    this.mAddress2 = pAddress2;
  }

  /**
   * @return the mCity
   */
  public String getCity() {
    return mCity;
  }

  /**
   * @param pCity
   *          the mCity to set
   */
  public void setCity(String pCity) {
    this.mCity = pCity;
  }

  /**
   * @return the mState
   */
  public String getState() {
    return mState;
  }

  /**
   * @param pState
   *          the mState to set
   */
  public void setState(String pState) {
    this.mState = pState;
  }

  /**
   * @return the mPostalCode
   */
  public String getPostalCode() {
    return mPostalCode;
  }

  /**
   * @param pPostalCode
   *          the mPostalCode to set
   */
  public void setPostalCode(String pPostalCode) {
    this.mPostalCode = pPostalCode;
  }

  /**
   * @return the mPhoneNumber
   */
  public String getPhoneNumber() {
    return mPhoneNumber;
  }

  /**
   * @param pPhoneNumber
   *          the mPhoneNumber to set
   */
  public void setPhoneNumber(String pPhoneNumber) {
    this.mPhoneNumber = pPhoneNumber;
  }

  /**
   * @return the mCountry
   */
  public String getCountry() {
    return mCountry;
  }

  /**
   * @param pCountry
   *          the mCountry to set
   */
  public void setCountry(String pCountry) {
    this.mCountry = pCountry;
  }

  /**
   * @return the mBillingAddress
   */
  public ContactInfo getBillingAddress() {
    return mBillingAddress;
  }

  /**
   * @param pBillingAddress
   *          the mBillingAddress to set
   */
  public void setBillingAddress(ContactInfo pBillingAddress) {
    this.mBillingAddress = pBillingAddress;
  }

  /**
   * @return the mSavePaymentMethod
   */
  public boolean isSavePaymentMethod() {
    return mSavePaymentMethod;
  }

  /**
   * @param pSavePaymentMethod
   *          the mSavePaymentMethod to set
   */
  public void setSavePaymentMethod(boolean pSavePaymentMethod) {
    this.mSavePaymentMethod = pSavePaymentMethod;
  }

  /**
   * @return the mAddressValidator
   */
  public MFFAddressValidator getAddressValidator() {
    return mAddressValidator;
  }

  /**
   * @param pAddressValidator
   *          the mAddressValidator to set
   */
  public void setAddressValidator(MFFAddressValidator pAddressValidator) {
    this.mAddressValidator = pAddressValidator;
  }

  /**
   * @return the mCheckoutManager
   */
  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  /**
   * @param pCheckoutManager
   *          the mCheckoutManager to set
   */
  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    this.mCheckoutManager = pCheckoutManager;
  }

  /**
   * @return the mProfileTools
   */
  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * @param mProfileTools
   *          the mProfileTools to set
   */
  public void setProfileTools(MFFProfileTools mProfileTools) {
    this.mProfileTools = mProfileTools;
  }

  /**
   * @return the mApplyCreditCardSuccessURL
   */
  public String getApplyCreditCardSuccessURL() {
    return mApplyCreditCardSuccessURL;
  }

  /**
   * @param pApplyCreditCardSuccessURL
   *          the mApplyCreditCardSuccessURL to set
   */
  public void setApplyCreditCardSuccessURL(String pApplyCreditCardSuccessURL) {
    this.mApplyCreditCardSuccessURL = pApplyCreditCardSuccessURL;
  }

  /**
   * @return the mApplyCreditCardErrorURL
   */
  public String getApplyCreditCardErrorURL() {
    return mApplyCreditCardErrorURL;
  }

  /**
   * @param pApplyCreditCardErrorURL
   *          the mApplyCreditCardErrorURL to set
   */
  public void setApplyCreditCardErrorURL(String pApplyCreditCardErrorURL) {
    this.mApplyCreditCardErrorURL = pApplyCreditCardErrorURL;
  }

  /**
   * @return the mSelectBillingSuccessURL
   */
  public String getSelectBillingSuccessURL() {
    return mSelectBillingSuccessURL;
  }

  /**
   * @param pSelectBillingSuccessURL
   *          the mSelectBillingSuccessURL to set
   */
  public void setSelectBillingSuccessURL(String pSelectBillingSuccessURL) {
    this.mSelectBillingSuccessURL = pSelectBillingSuccessURL;
  }

  /**
   * @return the mSelectBillingErrorURL
   */
  public String getSelectBillingErrorURL() {
    return mSelectBillingErrorURL;
  }

  /**
   * @param pSelectBillingErrorURL
   *          the mSelectBillingErrorURL to set
   */
  public void setSelectBillingErrorURL(String pSelectBillingErrorURL) {
    this.mSelectBillingErrorURL = pSelectBillingErrorURL;
  }

  /**
   * @return the mDisplayAvsModal
   */
  public boolean isDisplayAvsModal() {
    return mDisplayAvsModal;
  }

  /**
   * @param pDisplayAvsModal
   *          the mDisplayAvsModal to set
   */
  public void setDisplayAvsModal(boolean pDisplayAvsModal) {
    this.mDisplayAvsModal = pDisplayAvsModal;
  }

  /**
   * @return the mSavePaymentSuccessURL
   */
  public String getSavePaymentSuccessURL() {
    return mSavePaymentSuccessURL;
  }

  /**
   * @param pSavePaymentSuccessURL
   *          the mSavePaymentSuccessURL to set
   */
  public void setSavePaymentSuccessURL(String pSavePaymentSuccessURL) {
    this.mSavePaymentSuccessURL = pSavePaymentSuccessURL;
  }

  /**
   * @return the mSavePaymentErrorURL
   */
  public String getSavePaymentErrorURL() {
    return mSavePaymentErrorURL;
  }

  /**
   * @param pSavePaymentErrorURL
   *          the mSavePaymentErrorURL to set
   */
  public void setSavePaymentErrorURL(String pSavePaymentErrorURL) {
    this.mSavePaymentErrorURL = pSavePaymentErrorURL;
  }

  // Credit Card
  private String mCreditCardId;

  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator) {
    mFormExceptionGenerator = pFormExceptionGenerator;
  }

	public List<MFFInlineDropletFormException> getFormFieldExceptions() {
		return getFormExceptionGenerator().getFormFieldExceptions(
				getFormExceptions());
	}

	public List<DropletException> getNonFormFieldExceptions() {
		return getFormExceptionGenerator().getNonFormFieldExceptions(
				getFormExceptions());
	}

  public MFFShippingGroupFormHandler getShippingGroupFormHandler() {
    return mShippingGroupFormHandler;
  }

  public void setShippingGroupFormHandler(MFFShippingGroupFormHandler pShippingGroupFormHandler) {
    mShippingGroupFormHandler = pShippingGroupFormHandler;
  }

  public String getAvsBillingSuccessURL() {
    return mAvsBillingSuccessURL;
  }

  public void setAvsBillingSuccessURL(String pAvsBillingSuccessURL) {
    mAvsBillingSuccessURL = pAvsBillingSuccessURL;
  }

  public String getAvsBillingErrorURL() {
    return mAvsBillingErrorURL;
  }

  public void setAvsBillingErrorURL(String pAvsBillingErrorURL) {
    mAvsBillingErrorURL = pAvsBillingErrorURL;
  }

  public String getContactEmail() {
    return mContactEmail;
  }

  public void setContactEmail(String pContactEmail) {
    mContactEmail = pContactEmail;
  }

  public boolean isAddressVerified() {
    return mAddressVerified;
  }

  public void setAddressVerified(boolean pAddressVerified) {
    mAddressVerified = pAddressVerified;
  }

  public MFFGiftCardManager getGiftCardManager() {
    return mGiftCardManager;
  }

  public void setGiftCardManager(MFFGiftCardManager pGiftCardManager) {
    mGiftCardManager = pGiftCardManager;
  }

  public String getGiftCardNumber() {
    return mGiftCardNumber;
  }

  public void setGiftCardNumber(String pGiftCardNumber) {
    mGiftCardNumber = pGiftCardNumber;
  }

  public String getGiftCardPin() {
    return mGiftCardPin;
  }

  public void setGiftCardPin(String pGiftCardPin) {
    mGiftCardPin = pGiftCardPin;
  }

  public String getGiftCardSuccessURL() {
    return mGiftCardSuccessURL;
  }

  public void setGiftCardSuccessURL(String pGiftCardSuccessURL) {
    mGiftCardSuccessURL = pGiftCardSuccessURL;
  }

  public String getGiftCardErrorURL() {
    return mGiftCardErrorURL;
  }

  public void setGiftCardErrorURL(String pGiftCardErrorURL) {
    mGiftCardErrorURL = pGiftCardErrorURL;
  }

  /**
   * Utility method to retrieve the PropertyManager.
   *
   * @return property manager
   */
  protected PropertyManager getPropertyManager() {
    return getProfileTools().getPropertyManager();
  }

  private MFFExtendableCreditCardTools mCreditCardTools;

  /**
   * @return the creditCardTools
   */
  public MFFExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * @param pCreditCardTools
   *          the creditCardTools to set
   */
  public void setCreditCardTools(MFFExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }

  public List<String> getCardPropertyList() {
    return mCardPropertyList;
  }

  public void setCardPropertyList(List<String> pCardPropertyList) {
    mCardPropertyList = pCardPropertyList;
  }

  public String getPaymentName() {
    return mPaymentName;
  }

  public void setPaymentName(String pPaymentName) {
    mPaymentName = pPaymentName;
  }

  ArrayList<String> mFormMessages = null;

  public boolean getFormMessage() {
    return mFormMessages != null && !mFormMessages.isEmpty();
  }

  public ArrayList<String> getFormMessages() {
    if (mFormMessages == null) {
      mFormMessages = new ArrayList<String>();
    }
    return mFormMessages;
  }

  public void addFormMessage(String pNewMessage) {
    if (mFormMessages == null) {
      mFormMessages = new ArrayList<String>();
    }
    mFormMessages.add(pNewMessage);
  }

  public String getRemoveGiftCardSuccessURL() {
    return mRemoveGiftCardSuccessURL;
  }

  public void setRemoveGiftCardSuccessURL(String pRemoveGiftCardSuccessURL) {
    mRemoveGiftCardSuccessURL = pRemoveGiftCardSuccessURL;
  }

  public String getRemoveGiftCardErrorURL() {
    return mRemoveGiftCardErrorURL;
  }

  public void setRemoveGiftCardErrorURL(String pRemoveGiftCardErrorURL) {
    mRemoveGiftCardErrorURL = pRemoveGiftCardErrorURL;
  }

  public String getGiftCardToRemove() {
    return mGiftCardToRemove;
  }

  public void setGiftCardToRemove(String pGiftCardToRemove) {
    mGiftCardToRemove = pGiftCardToRemove;
  }

  public AciService getAciService() {
    return mAciService;
  }

  public void setAciService(AciService pAciService) {
    mAciService = pAciService;
  }

  public MFFEnvironment getMffEnvironment() {
    return mMffEnvironment;
  }

  public void setMffEnvironment(MFFEnvironment pMffEnvironment) {
    mMffEnvironment = pMffEnvironment;
  }

  public MFFPurchaseProcessHelper getPurchaseProcessHelper() {
    return mPurchaseProcessHelper;
  }

  public void setPurchaseProcessHelper(MFFPurchaseProcessHelper pPurchaseProcessHelper) {
    this.mPurchaseProcessHelper = pPurchaseProcessHelper;
  }

  public MFFZipcodeHelper getZipCodeHelper() {
	return mZipCodeHelper;
  }

  public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
	mZipCodeHelper = pZipCodeHelper;
  }

  public boolean isCityStateZipMatched() {
	return mCityStateZipMatched;
  }

  public void setCityStateZipMatched(boolean pCityStateZipMatched) {
	mCityStateZipMatched = pCityStateZipMatched;
  }
}
