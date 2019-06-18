package com.mff.commerce.order.purchase;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Vector;

import javax.servlet.ServletException;

import com.aci.payment.creditcard.AciCreditCard;
import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.service.FirstDataService;
import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.inventory.StockLevel;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.constants.MFFConstants;
import com.mff.email.MFFEmailManager;
import com.mff.locator.StoreLocatorTools;
import com.mff.userprofiling.MFFProfile;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.util.MFFUtils;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.purchase.CommitOrderFormHandler;
import atg.commerce.payment.PaymentManager;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userprofiling.address.AddressTools;

/**
 * Extension to ootb CommitOrderFormHandler.
 *
 * @author DMI
 * 
 */
public class MFFCommitOrderFormHandler extends CommitOrderFormHandler {

  MFFPurchaseProcessHelper mPurchaseProcessHelper;
  private MFFOrderManager mOrderManager;
  private MFFCheckoutManager mCheckoutManager;
  private PaymentManager mPaymentManager;
  private MFFProfileTools mProfileTools;
  private MFFFormExceptionGenerator mFormExceptionGenerator;
  private FFRepositoryInventoryManager mInventoryManager;
  private MFFEmailManager mEmailManager;
  private String mCardVerificationNum;
  private FirstDataService mFirstDataService;

  private String mCommitOrderInventoryErrorURL;
  private String mCommitOrderLoginErrorURL;

  public static final String FAIL_CC_AUTH = "FailedCreditCardAuth";
  private static final String PERFORM_MONITOR_NAME = "CommitOrderFormHandler";
  public static final int MAX_FAILED_AUTH_ATTEMPTS = 3;

  private StoreLocatorTools storeLocatorTools;
  
  public StoreLocatorTools getStoreLocatorTools() {
	  return storeLocatorTools;
  }
  public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
	  storeLocatorTools = pStoreLocatorTools;
  }
  
  @SuppressWarnings("unchecked")
  public void preCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogDebug("preCommitOrder: Called.");

    if (getCheckoutManager().isExpressCheckout()) {

      String cvv = getCardVerificationNum();

      if (cvv == null || cvv.isEmpty()) {
        vlogError("CommitOrderFormHandler: CVV is not provided for ExpressCheckout");
        getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_CVV_REQUIRED, true, this, pRequest);
        return;
      }
      for (int i = 0; i < cvv.length(); ++i) {
        char c = cvv.charAt(i);
        if ((((c < '0') || (c > '9'))) && (c != ' ')) {
          vlogError("CommitOrderFormHandler: invalid CVV entered in ExpressCheckout");
          getFormExceptionGenerator().generateException(MFFConstants.CREDIT_CARD_INVALID_CVV, true, this, pRequest);
          return;
        }
      }
    }

    MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();

    ((MFFOrderManager) getOrderManager()).displayOrderVersionInfo(currentOrder, "Checking order version before making changes to the order on preCommitOrder");

    if (currentOrder.getPriceInfo() == null) {
      try {
        runRepriceOrder(pRequest);
      } catch (RunProcessException e) {
        if (isLoggingError()) {
          logError(e);
        }
      }
    }


    if (getCheckoutManager().isOrderRequiresCreditCard() && !isPaymentGroupValid(currentOrder)) {
      String msg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.FAILURE_CC_AUTH));
      getCheckoutManager().setPaymentErrorMessage(msg);
      // Generating a form exception should skip further processing of
      // handleCommitOrder
      getFormExceptionGenerator().generateException(MFFConstants.FAILURE_CC_AUTH, true, this, pRequest);
      return;
    }

    // Validate if the contact email is not missing in the order at this stage.
    if (StringUtils.isNotBlank(currentOrder.getContactEmail())){
      vlogDebug("Action: Valid order with contact email for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());
    } else {
      // Send the user back to the login page to try again
      setCommitOrderErrorURL(getCommitOrderLoginErrorURL());
      vlogWarning("There was an issue with validating for guest flag and contact email for order id({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());
      try {
        getCheckoutManager().setLoginErrorMessage(MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.CHECKOUT_LOGIN_REDIRECT_MSG)));
        // Generating a form exception should skip further processing of
        // handleCommitOrder
        getFormExceptionGenerator().generateException(MFFConstants.CHECKOUT_LOGIN_REDIRECT_MSG, true, this, pRequest);
      } catch (MissingResourceException e) {
        getCheckoutManager().setLoginErrorMessage(MFFConstants.CHECKOUT_LOGIN_REDIRECT_MSG);
        getFormExceptionGenerator().generateException(MFFConstants.CHECKOUT_LOGIN_REDIRECT_MSG, false, this, pRequest);
        if (isLoggingError()) {
          logError(e);
        }
      }
      return;
    }

    logInfo("Acquiring inventory locks for " + getOrder().getId());
    // Acquire inventory locks
    if (getInventoryManager().isAcquireInventoryLocksPreCommitOrder()) {

      vlogDebug("Acquiring inventory locks for order:{0} ", getOrder().getId());

      List<String> skuIds = new ArrayList<String>();
      Iterator<CommerceItem> ciIter = getOrder().getCommerceItems().iterator();
      while (ciIter.hasNext()) {
        CommerceItem ci = (CommerceItem) ciIter.next();
        skuIds.add(ci.getCatalogRefId());
      }

      try {
    	  getInventoryManager().acquireTransactionInventoryLocks(skuIds,FFRepositoryInventoryManager.DEFAULT_LOCATION_ID);
      } catch (InventoryException e) {
        if (isLoggingError()) {
          logError("Was unable to acquire inventory locks for order " + getOrder().getId(), e);
        }
      }
    } else {
      if (isLoggingDebug()) {
        logDebug("Not acquiring inventory locks");
      }
      
    }
    logInfo("Validating inventory for " + getOrder().getId());
    // check for the product display rules before submitting the order, reject
    // the order submission and take back to the cart in case of an issue
    if (!checkForSkuAvailability(currentOrder)) {
      setCommitOrderErrorURL(getCommitOrderInventoryErrorURL());
      // Generating a form exception should skip further processing of
      // handleCommitOrder
      getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_NOT_AVAILABLE, true, this, pRequest);
      return;
    }
  }

  /**
   * A simple wrapper to add the performance monitor
   */
  @Override
  public boolean handleCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String methodName = MFFConstants.HANDLE_COMMIT_ORDER_METHOD_NAME;

    vlogDebug("Begin handleCommitOrder");
    vlogInfo("Action: handleCommitOrder for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());

    // if any form errors found here redirect user to error page
    if (getFormError()) {
      return checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse);
    }

    String perfMonFunction = "handleCommitOrder";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfMonFunction);

    if ((rrm == null) || (rrm.isUniqueRequestEntry(methodName))) {

      // if express checkout get the CVV and update it on the payment group
      if (getCheckoutManager().isExpressCheckout()) {
        if (getCheckoutManager().getCreditCardPaymentGroup() != null) {
          MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();
          synchronized (currentOrder) {
            try {
              AciCreditCard lCreditCard = (AciCreditCard) getCheckoutManager().getCreditCardPaymentGroup();

              lCreditCard.setCardVerficationNumber(getCardVerificationNum());

              vlogDebug("Updating the Order:{0} with CVV for Express Checkout", currentOrder.getId());
              // update the order
              ((MFFOrderManager) getOrderManager()).updateOrder(currentOrder, "CommitOrder applyCVV for ExpressCheckout");
            } catch (CommerceException e) {
              vlogError("Exception when updating order in handleApplyCreditCard", e);
              getCheckoutManager().setPaymentErrorMessage("Exception when updating order in handleApplyCreditCard");
              getFormExceptionGenerator().generateException(MFFConstants.UPDATE_ORDER_ERROR, true, this, pRequest);
              PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME);
              return checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse);
            }
          }
        } else {
          vlogWarning("why credit card number is null here for OrderID:{0}: as credit card required for this order", getOrder().getId());
        }
      }

      try {
        // if customer tried 3 auth failure attempts, we will block user from
        // placing the order
        if (getCheckoutManager().getCardAttempt() >= MAX_FAILED_AUTH_ATTEMPTS) {
          getFormExceptionGenerator().generateException(MFFConstants.FAILURE_CC_AUTH, true, this, pRequest);
          PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME);
          return checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse);
        }

        super.handleCommitOrder(pRequest, pResponse);

        if (getFormError()) {
          Vector vtr = this.getFormExceptions();
          String key = null;
          for (int i = 0; i < vtr.size(); i++) {
            key = ((DropletException) vtr.get(i)).getErrorCode();
            if (key != null && key.contains(FAIL_CC_AUTH)) {
              getCheckoutManager().incrementFailureCardAttempt();
            }

            String errorMessage = ((DropletException) vtr.get(i)).getMessage();
            
            if (errorMessage != null) {
              
              this.getFormExceptions().clear();
              if (key.equals(MFFConstants.GIFT_CARD_INSUFFICIENT_FUNDS)) {
                getFormExceptionGenerator().generateException(errorMessage, this);
              } else {              
                getFormExceptionGenerator().generateException(MFFConstants.ERROR_COMMIT_ORDER, true, this, pRequest);
              }
              PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME);
              
              return checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse);
            }
            vlogError("handleCommitOrder. Form error. Order {0}. Error: {1}", getOrder().getId(), errorMessage);
          }
        }
      } finally {
        vlogDebug("End handleCommitOrder");

        // if there is any error while placing the order, 
        //then loop through the giftcard payment groups
        // check if the giftcard is already authorized,
        // if so, perform a credit operation on giftcard payment group to refund
        // the funds as the order was not submitted successfully.
        if (getFormError()) {
          List<MFFGiftCardPaymentGroup> giftCardPGs = getCheckoutManager().getGiftCardPaymentGroups();
          if (giftCardPGs != null && !giftCardPGs.isEmpty()) {
            vlogDebug("Found giftcard payment groups on the Order:{0}, need to reverse the authorization, if any",getOrder().getId());
            for (MFFGiftCardPaymentGroup giftCardPg : giftCardPGs) {
              List<MFFGiftCardPaymentStatus> pPayStatus = giftCardPg.getAuthorizationStatus();
              for (MFFGiftCardPaymentStatus giftCardStatus : pPayStatus) {
                // if authorization transaction success we need to reverse the authorization
                if (giftCardStatus != null && giftCardStatus.getTransactionSuccess()) {
                  vlogInfo("CommitOrderFormHandler: reverse the authorization on Order:{0} " + "with GiftCardPaymentGroup:{1}", getOrder().getId(), giftCardPg.getId());
                  try {
                    getFirstDataService().debit(giftCardPg.getCardNumber(), 0.0d, giftCardStatus.getLocalLockId(), giftCardPg.getEan());
                  } catch (Exception e) {
                    vlogError(e.getMessage(),"CommitOrderFormHandler: Unable to reverse the authorization on Order:{0} " + "with GiftCardPaymentGroup:{1}", getOrder().getId(), giftCardPg.getId());
                  }
                  
                }
              }
            }
          }
        }

        if (rrm != null) {
          rrm.removeRequestEntry(methodName);
        }

        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfMonFunction);
      }
    }

    return checkFormRedirect(getCommitOrderSuccessURL(), getCommitOrderErrorURL(), pRequest, pResponse);
  }

  private void runRepriceOrder(DynamoHttpServletRequest pRequest) throws RunProcessException {
    vlogWarning("Action: Price info is null for order id ({0}) and profile id ({1}) hence repricing", getOrder().getId(), getProfile().getRepositoryId());
    RequestLocale requestLocale = pRequest.getRequestLocale();
    Locale locale = null;
    if (requestLocale != null) {
      locale = requestLocale.getLocale();
    }

    getPurchaseProcessHelper().runProcessRepriceOrder(getPricingOp(), getOrder(), getUserPricingModels(), locale, getProfile(), null, null);
  }

  private void savePaymentInfoToProfile(AciCreditCard pCreditCardPaymentGroup, String pNickName) {

    vlogInfo("Action: savePaymentMethod for order id {0} and profile id {1}", getShoppingCart().getLast().getId(), getProfile().getRepositoryId());

    CommercePropertyManager cpmgr = (CommercePropertyManager) getProfileTools().getPropertyManager();

    if (pCreditCardPaymentGroup != null) {

      if (getProfileTools().isCreditCardExistInProfile(pCreditCardPaymentGroup.getCreditCardNumber(), getProfile(), pCreditCardPaymentGroup.getExpirationMonth() + pCreditCardPaymentGroup.getExpirationYear())) {
        vlogWarning("Card Exist on the profile:{0} with same data, not saving payment", getProfile().getRepositoryId());
        return;
      }
      vlogDebug("Saving Payment for profile:{0}", getProfile().getRepositoryId());
      Address ccBillAddr = pCreditCardPaymentGroup.getBillingAddress();
      try {
        MutableRepository mutRep = getProfileTools().getProfileRepository();
        MutableRepositoryItem newAddress = mutRep.createItem(cpmgr.getContactInfoItemDescriptorName());
        AddressTools.copyAddress(ccBillAddr, newAddress);
        newAddress.setPropertyValue(cpmgr.getAddressOwnerPropertyName(), getProfile().getRepositoryId());
        mutRep.addItem(newAddress);

        String ccNumber = pCreditCardPaymentGroup.getCreditCardNumber();

        // only save last 4 digits if card has length more than 4
        if (ccNumber.length() > 4) {
          ccNumber = pCreditCardPaymentGroup.getCreditCardNumber().substring(ccNumber.length() - 4);
        }

        RepositoryItem billingAddress = newAddress;
        MutableRepositoryItem newCard = mutRep.createItem(cpmgr.getCreditCardItemDescriptorName());
        newCard.setPropertyValue(cpmgr.getCreditCardNumberPropertyName(), ccNumber);
        newCard.setPropertyValue(cpmgr.getCreditCardTypePropertyName(), pCreditCardPaymentGroup.getCreditCardType());
        newCard.setPropertyValue(cpmgr.getCreditCardExpirationMonthPropertyName(), pCreditCardPaymentGroup.getExpirationMonth());
        newCard.setPropertyValue(cpmgr.getCreditCardExpirationYearPropertyName(), pCreditCardPaymentGroup.getExpirationYear());
        newCard.setPropertyValue(MFFConstants.FIELD_NAME_ON_CARD, pCreditCardPaymentGroup.getNameOnCard());
        newCard.setPropertyValue(MFFConstants.FIELD_TOKEN_ID, pCreditCardPaymentGroup.getTokenNumber());
        String mopType = pCreditCardPaymentGroup.getMopTypeCode();
        if (!StringUtils.isEmpty(mopType)) {
          newCard.setPropertyValue(MFFConstants.FIELD_MOP_TYPE_CODE, mopType);
        }
        newCard.setPropertyValue(cpmgr.getCreditCardBillingAddressPropertyName(), billingAddress);
        mutRep.addItem(newCard);

        MutableRepositoryItem mutProfile = mutRep.getItemForUpdate(getProfile().getRepositoryId(), getProfile().getItemDescriptor().getItemDescriptorName());
        
        @SuppressWarnings("unchecked")
        Map<String, RepositoryItem> creditCards = (Map<String, RepositoryItem>) mutProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
        vlogDebug("Payment Name:{0}", pNickName);
        if (StringUtils.isBlank(pNickName)) {
          pNickName = ((MFFProfileTools) getProfileTools()).getUniqueShippingAddressNickname(billingAddress, getProfile(), null);
        }
        creditCards.put(pNickName, newCard);

        // make it default credit card if there is no default credit card setup
        RepositoryItem currentDefaultCard = (RepositoryItem) getProfile().getPropertyValue(cpmgr.getDefaultCreditCardPropertyName());
        if (currentDefaultCard == null) {
          mutProfile.setPropertyValue(cpmgr.getDerivedDefaultCreditCardPropertyName(), newCard);
        }

        mutRep.updateItem(mutProfile);

        vlogDebug("New payment method saved in the profile:{0}", getProfile().getRepositoryId());

      } catch (RepositoryException e) {
        if (isLoggingError()) {
          logError(e);
        }
      } catch (IntrospectionException e) {
        if (isLoggingError()) {
          logError(e);
        }
      }
    } else {
      if (isLoggingWarning()) {
        logWarning("No CC Payment Group found.  Will NOT save a new payment method to the profile.");
      }
    }
  }

  public AciCreditCard getCreditCardPaymentGroup(MFFOrderImpl currentOrder) {
    
    vlogDebug("Action: getpCreditCardPaymentGroup(currentOrder) for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());
    
    @SuppressWarnings("unchecked")
    List<PaymentGroup> list = currentOrder.getPaymentGroups();

    // if there are no existing payment groups on the order, return null;
    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        PaymentGroup pg = (PaymentGroup) list.get(i);

        if (pg instanceof AciCreditCard) {
          return (AciCreditCard) pg;
        }
      }
    }
    return null;
  }

  @Override
  public void postCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (isLoggingDebug()) logDebug("Inside Post Commit Order");

    if (!getFormError()) {
      if (isLoggingDebug()) {
        logDebug("There are no FormExceptions found by CommitOrderFormHandler, we can proceed to Confirmation page...");
      }

      getCheckoutManager().authorizeConfirmStep();
      
      MFFOrderImpl lastOrder = (MFFOrderImpl) getShoppingCart().getLast();
      MFFProfile lProfile = (MFFProfile) getProfile();

      // save address to profile
      MFFHardgoodShippingGroup lLastSG = (MFFHardgoodShippingGroup) lastOrder.getShippingGroups().get(0);
      Map<String, String> specialInstructions = (Map<String, String>) lLastSG
				.getPropertyValue("specialInstructions");
      String lAttention = "";
      if(specialInstructions != null && specialInstructions.size() > 0){
    	  lAttention = specialInstructions.get("instructions");
      }

      // save only if order is not ffl & bopis & registered and user have
      // choosen to save the address
      if (lProfile.isHardLoggedIn() && getCheckoutManager().isSaveShippingAddress() && !lastOrder.isFFLOrder() && !lastOrder.isBopisOrder()) {
    	ContactInfo lAddress = (ContactInfo) lLastSG.getShippingAddress();
        String lAddressName = getCheckoutManager().getShippingAddressNickName();

        saveShippingAddressToProfile(lAddress, lAddressName, lAttention);
      }

      // save card to profile
      if (lProfile.isHardLoggedIn() && getCheckoutManager().isSavePaymentMethod()) {
        AciCreditCard creditCard = getCreditCardPaymentGroup(lastOrder);
        String lPaymentName = getCheckoutManager().getPaymentName();

        if (creditCard == null) {
          vlogDebug("Credit Card is Null, may be user payed with gift card, no need to save payment method");
        } else {
          savePaymentInfoToProfile(creditCard, lPaymentName);
        }
      }

      // clear current promos applied to the profile.
      try {
        getProfileTools().clearCurrentPromos(getProfile());
      } catch (RepositoryException e) {
        if (isLoggingError()) {
          logError(e);
        }
      }

      
      //if(((MFFOrderImpl)lastOrder).isBopisOrder() && getStoreLocatorTools().isBOPISOnlyStore(((MFFOrderImpl)lastOrder).getBopisStore())) {
    	  
      //} else {
    	  
    	// decrement inventory for web/bopis only if the order is not FRAUD_REJECT
    	  
    	  if(!lastOrder.getFraudStat().equalsIgnoreCase("FRAUD_REJECT")) {
    	      if(((MFFOrderImpl)lastOrder).isBopisOrder() && getStoreLocatorTools().isBOPISOnlyStore(((MFFOrderImpl)lastOrder).getBopisStore())) {
        	  
    	      } else {
    	    	  getInventoryManager().decrementInventory(lastOrder, getOrder().getId(), getProfile().getRepositoryId());  
    	      }

    		  if(lastOrder.isBopisOrder()) {
    			  List ciList = lastOrder.getCommerceItems();
    			  ListIterator ciIterator = ciList.listIterator();
    			  while (ciIterator.hasNext()) {
    				  CommerceItem ci = (CommerceItem) ciIterator.next();
    				  String skuId = ci.getCatalogRefId();
    				  long qty = ci.getQuantity();
    				  try {
    					  if(!getStoreLocatorTools().isBOPISOnlyStore(lastOrder.getBopisStore())) {
    						  getInventoryManager().incrementStoreAllocated(skuId, lastOrder.getBopisStore(), qty);
    					  } else {
    						  getInventoryManager().incrementBopisOnlyStoreAllocated(skuId, lastOrder.getBopisStore(), qty, true);
    					  }

    					  //
    				  }catch (InventoryException e) {
    					  vlogError(e,"ProcUpdateInventoryForBopisOrder: Error updatig inventory for order {0} Sku {0}",lastOrder.getOrderNumber(),skuId);
    				  }    			  
    			  }

    		  }
    	  } 
      //}
      

      getEmailManager().sendOrderConfirmationMail(lastOrder, lProfile);
      // getCheckoutManager().authorizeConfirmationStep();
      getCheckoutManager().resetCheckoutValuesPostCommit();
    }
  }

  @SuppressWarnings("rawtypes")
  private boolean saveShippingAddressToProfile(ContactInfo pAddress, String pNickName, String pAttention) {
    vlogDebug("SaveShippingAddressToProfile - Entering.");
    vlogInfo("Action: saveShippingAddressToProfile for order id {0} and profile id {1}", getShoppingCart().getLast().getId(), getProfile().getRepositoryId());
    boolean success = true;
    vlogDebug("About to save a new address in the profile with nickname:" + pNickName);
    try {
      String addressId = getProfileTools().createProfileRepositorySecondaryAddress(getProfile(), pNickName, pAddress);

      CommercePropertyManager cpmgr = (CommercePropertyManager) getProfileTools().getPropertyManager();
      Map secondaryAddresses = (Map) getProfile().getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
      RepositoryItem address = (RepositoryItem) secondaryAddresses.get(pNickName);
      MutableRepository mutRep = getProfileTools().getProfileRepository();

      MutableRepositoryItem mutAddress = mutRep.getItemForUpdate(address.getRepositoryId(), address.getItemDescriptor().getItemDescriptorName());
      mutAddress.setPropertyValue("attention", pAttention);
      mutRep.updateItem(mutAddress);
      RepositoryItem currentPrimaryAddress = (RepositoryItem) getProfile().getPropertyValue(cpmgr.getShippingAddressPropertyName());
      if (currentPrimaryAddress == null) {
        vlogDebug("The user doesn't have a Primary Shipping Address, so will make this new address Primary by default...");

        MutableRepositoryItem mutProfile = mutRep.getItemForUpdate(getProfile().getRepositoryId(), getProfile().getItemDescriptor().getItemDescriptorName());

        mutProfile.setPropertyValue(cpmgr.getShippingAddressPropertyName(), address);
        mutRep.updateItem(mutProfile);
      }

      logDebug("A new address was created in the Profile.  Nickname:" + pNickName + ", and assigned ID:" + addressId);
    } catch (RepositoryException e) {
      success = false;
      if (isLoggingError()) {
        logError("Error saving New Address to Profile in MFFShippingGroupFormHandler:", e);
      }
    }
    vlogDebug("SaveShippingAddressToProfile - Exiting");
    return success;

  }

  /**
   * Method to check for sku availability
   */
  private boolean checkForSkuAvailability(MFFOrderImpl currentOrder) {

    vlogDebug("Action: checkForSkuAvailability for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());

    // Check for DC Inventory and Check for sku availability flags
    Map<String, StockLevel> skusStockLevel = ((MFFOrderManager) getOrderManager()).getStockLevelForSkusInOrder(currentOrder);
    @SuppressWarnings("unchecked")
    List<MFFCommerceItemImpl> ciList = currentOrder.getCommerceItems();
    ListIterator<MFFCommerceItemImpl> ciIterator = ciList.listIterator();
    while (ciIterator.hasNext()) {
      MFFCommerceItemImpl ci = (MFFCommerceItemImpl) ciIterator.next();
      String skuId = ci.getCatalogRefId();
      long currentQty = ci.getQuantity();
      long availableStockLevel = 0;
      StockLevel stockLevel = skusStockLevel.get(skuId);
      if (stockLevel != null) {
        availableStockLevel = stockLevel.getStockLevel();
      }

      if (availableStockLevel <= 0 || currentQty > availableStockLevel) {
        return false;
      }
    }
    return true;

  }

  /**
   * Doing a minimum validation on the payment group, check if the credit card
   * has a valid token
   * 
   * @return
   */
  private boolean isPaymentGroupValid(MFFOrderImpl currentOrder) {
    
    vlogDebug("Action: isPaymentGroupValid for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());
    
    if (getCreditCardPaymentGroup(currentOrder) != null) {
      // TODO: implement this once ACI integration is completed
      AciCreditCard pCreditCardPaymentGroup = (AciCreditCard) getCreditCardPaymentGroup(currentOrder);
      if (pCreditCardPaymentGroup.getTokenNumber() == null || StringUtils.isBlank(pCreditCardPaymentGroup.getTokenNumber())) {
        return false;
      }
    } else {
      return false;
    }
    return true;
  }

  private PricingModelHolder mUserPricingModels;
  private String mPricingOp;

  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    this.mUserPricingModels = pUserPricingModels;
  }

  public String getPricingOp() {
    return mPricingOp;
  }

  public void setPricingOp(String pPricingOp) {
    this.mPricingOp = pPricingOp;
  }

  public MFFPurchaseProcessHelper getPurchaseProcessHelper() {
    return mPurchaseProcessHelper;
  }

  public void setPurchaseProcessHelper(MFFPurchaseProcessHelper pPurchaseProcessHelper) {
    this.mPurchaseProcessHelper = pPurchaseProcessHelper;
  }

  public MFFOrderManager getOrderManager() {
    return mOrderManager;
  }

  public void setOrderManager(MFFOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }

  public PaymentManager getPaymentManager() {
    return mPaymentManager;
  }

  public void setPaymentManager(PaymentManager pPaymentManager) {
    mPaymentManager = pPaymentManager;
  }

  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }

  public void setProfileTools(MFFProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * @return the mFormExceptionGenerator
   */
  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  /**
   * @param pFormExceptionGenerator
   *          the mFormExceptionGenerator to set
   */
  public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator) {
    this.mFormExceptionGenerator = pFormExceptionGenerator;
  }

  public FFRepositoryInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * @return the mCommitOrderInventoryErrorURL
   */
  public String getCommitOrderInventoryErrorURL() {
    return mCommitOrderInventoryErrorURL;
  }

  /**
   * @param pCommitOrderInventoryErrorURL
   *          the mCommitOrderInventoryErrorURL to set
   */
  public void setCommitOrderInventoryErrorURL(String pCommitOrderInventoryErrorURL) {
    this.mCommitOrderInventoryErrorURL = pCommitOrderInventoryErrorURL;
  }

  public String getCommitOrderLoginErrorURL() {
    return mCommitOrderLoginErrorURL;
  }

  public void setCommitOrderLoginErrorURL(String pCommitOrderLoginErrorURL) {
    mCommitOrderLoginErrorURL = pCommitOrderLoginErrorURL;
  }

  public MFFEmailManager getEmailManager() {
    return mEmailManager;
  }

  public void setEmailManager(MFFEmailManager pEmailManager) {
    mEmailManager = pEmailManager;
  }

  public String getCardVerificationNum() {
    return mCardVerificationNum;
  }

  public void setCardVerificationNum(String pCardVerificationNum) {
    mCardVerificationNum = pCardVerificationNum;
  }

  public FirstDataService getFirstDataService() {
    return mFirstDataService;
  }

  public void setFirstDataService(FirstDataService pFirstDataService) {
    mFirstDataService = pFirstDataService;
  }

}