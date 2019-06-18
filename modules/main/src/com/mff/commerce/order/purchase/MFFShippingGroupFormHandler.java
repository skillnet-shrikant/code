package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderTools;
import atg.commerce.order.purchase.ShippingGroupFormHandler;
import atg.commerce.pricing.PricingTools;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.order.MFFOrderTools;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.commerce.util.MFFAddressValidator;
import com.mff.constants.MFFConstants;
import com.mff.droplet.InlineFormErrorSupport;
import com.mff.droplet.MFFInlineDropletFormException;
import com.mff.locator.StoreLocatorTools;
import com.mff.userprofiling.MFFContactInfo;
import com.mff.userprofiling.MFFProfile;
import com.mff.userprofiling.MFFProfileFormHandler;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.util.MFFUtils;
import com.mff.zip.MFFZipcodeHelper;

public class MFFShippingGroupFormHandler extends ShippingGroupFormHandler implements InlineFormErrorSupport {

  private MFFCheckoutManager mCheckoutManager;
  private MFFFormExceptionGenerator mFormExceptionGenerator;
  private MFFProfileTools mProfileTools;
  private HardgoodShippingGroup mShippingGroup = null;
  private MFFProfileFormHandler mProfileFormHandler;
  private PricingTools mPricingTools;
  private MFFAddressValidator mAddressValidator;
  private StoreLocatorTools mLocationTools;
  private MFFZipcodeHelper mZipCodeHelper;
  private boolean mCityStateZipMatched = true;

  private String mShippingMethod;
  private String mAddressNickname;
  private String mFirstName = "";
  private String mLastName = "";
  private String mAddress1 = "";
  private String mAddress2 = "";
  private String mCity = "";
  private String mState = "";
  private String mPostalCode = "";
  private String mPhoneNumber;
  private String mCountry = MFFConstants.DEFAULT_COUNTRY;
  private String mPickUpPersonName;
  private String mPickUpPersonEmail;
  private String mSpecialInstructions;
  private boolean mSaturdayDelivery;
  private String mCompanyName;
  private String mFflEmail;
  private String mFflContactName;
  private String mFflDealerFFNumber;

  private boolean mAddressVerified;
  private boolean mFflAddressVerified;
  private ContactInfo mShippingAddress;
  private boolean mAddressType;
  private String mAddressId;
  private boolean mSaveShippingAddress;
  private boolean mAddressEditMode;
  private String mOldAddressNickname;

  private String mGuestCheckoutSuccessURL;
  private String mGuestCheckoutErrorURL;

  private String mAvsShippingSuccessURL;
  private String mAvsShippingErrorURL;
  private String mShipMethodSuccessURL;
  private String mShipMethodErrorURL;
  private String mShipMyOrderSuccessURL;
  private String mShipMyOrderErrorURL;
  private String mBopisShippingSuccessURL;
  private String mBopisShippingErrorURL;

  private static final String ADDRESS_ID_ZERO = "0";

  private boolean mDisplayAvsModal;

  /**
   * Helper method to initialize shipping address
   * <ul>
   * <li>If Bopis Order, then sets the store address as shipping address
   * (guest/registered user)</li>
   * <li>If FFL Order, then Generate the shipping address from FFL shipping
   * Address form (guest/registered user)</li>
   * <li>If Registered user, create the shipping address from selected
   * Address</li>
   * <li>If Registered user, but choose to create new address then create the
   * shipping address from form fields</li>
   * <li>If guest User, create the shipping address from form fields</li>
   * </ul>
   * 
   * @param pRequest
   * @return
   */
  public boolean initShippingAddress(DynamoHttpServletRequest pRequest) {

    boolean hardLoggedIn = ((MFFProfile) getProfile()).isHardLoggedIn();
    boolean isFFLOrder = ((MFFOrderImpl) getOrder()).isFFLOrder();

    if (isLoggingDebug()) {
      logDebug("Inside handleApplyShippingGroups");
    }
    ContactInfo shipAddress = null;

    if (!getCheckoutManager().isBopis()) {
      // if FFL order, create address from FFL shipping address form
      if (isFFLOrder) {
        shipAddress = createFFLShippingAddressFromFormFields(pRequest);
        setAddressId(ADDRESS_ID_ZERO);
        // check if this address is already AVS verified,
        // if not set the checkout AVSVerified to false, so that AVS will be
        // performed.
        if (isFflAddressVerified())
          getCheckoutManager().setShippingAvsVerified(true);
        else
          getCheckoutManager().setShippingAvsVerified(false);

      } else {
        // if registered user & choose to apply selected address,
        // load address from the selected and set the AVS verified to true to
        // skip AVS
        if (hardLoggedIn && !getAddressId().equals(ADDRESS_ID_ZERO)) {
          if (isLoggingDebug()) logDebug("This is an existing address in profile, fetch the address from the addressId:" + getAddressId());
          shipAddress = createShippingAddressFromSelected();
          getCheckoutManager().setShippingAvsVerified(true);
        } else {
          // if user choose to create a new address as registered or guest user,
          // then create address form fields
          shipAddress = createShippingAddressFromFormFields(pRequest);
          // check if AVS is already performed
          if (isAddressVerified())
            getCheckoutManager().setShippingAvsVerified(true);
          else
            getCheckoutManager().setShippingAvsVerified(false);
        }
      }
    } else {
      // if bopis order then create shipping address from store
      // also set the AVS verified to true to skip AVS verification
      shipAddress = createShippingAddressFromStore();
      getCheckoutManager().setShippingAvsVerified(true);
    }

    // if there are any errors while initializing the address return false
    if (this.hasFormError()) {
      return false;
    }

    vlogDebug("ShippingAddress :{0}", shipAddress);

    this.setShippingAddress(shipAddress);

    getCheckoutManager().setShippingMethod(getShippingMethod());
    getCheckoutManager().setShippingAddressId(getAddressId());
    getCheckoutManager().setShippingAddressInForm(shipAddress);
    getCheckoutManager().setSaveShippingAddress(isSaveShippingAddress());
    getCheckoutManager().setShippingAddressNickName(getAddressNickname());

    return true;
  }

  /**
   * Helper method to Validate Shipping Method form Input
   * 
   * @param pRequest
   */
  private void validateShippingMethod(DynamoHttpServletRequest pRequest) {

    if (getOrder() == null) {
      getFormExceptionGenerator().generateException(MFFConstants.MSG_NO_ORDER_TO_MODIFY, true, this, pRequest);
      return;
    }
    if (getShippingMethod() == null || getShippingMethod().equalsIgnoreCase("")) {
      getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_METHOD_MISSING_ERROR, true, this, pRequest);
      return;
    }
    if (getShippingGroup((MFFOrderImpl) getOrder()) == null) {
      getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_GROUP_ERROR, true, this, pRequest);
      return;
    }

    // if Saturday delivery is checked and if order is not eligible for Saturday
    // delivery throw an error
    if (isSaturdayDelivery()) {
      if (!getCheckoutManager().isSaturdayDelivery(getShippingGroup((MFFOrderImpl) getOrder()), getShippingMethod())) {
        getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_METHOD_SATURDAY_ERROR, true, this, pRequest);
        return;
      }
    }
  }

  /**
   * Extend OOTB apply Shipping groups method to customize the validations &
   * processing
   */
  @Override
  public boolean handleApplyShippingGroups(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogInfo("ShippingGroupFormHandler: handleApplyShippingGroups for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());

    processShippingGroup(pRequest, pResponse, MFFConstants.HANDLE_APPLY_SHIPPING_GROUPS_METHOD_NAME);

    return checkFormRedirect(getAvsShippingSuccessURL(), getAvsShippingErrorURL(), pRequest, pResponse);
  }

  /**
   * This handle method will be called to update the pick up person information
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleApplyBopis(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogInfo("Action: handleApplyBopis for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());

    if (!((MFFOrderImpl) getOrder()).isBopisOrder()) {
      vlogError("It is not a bopis order, please check");
      getFormExceptionGenerator().generateException("There is an error while applying shipping info", true, this, pRequest);
      return checkFormRedirect(null, getBopisShippingErrorURL(), pRequest, pResponse);
    }
    if (((MFFOrderImpl) getOrder()).getBopisStore().isEmpty()) {
      vlogError("No Bopis store found on the order, can not proceed further");
      getFormExceptionGenerator().generateException("There is an error while applying shipping info", true, this, pRequest);
      return checkFormRedirect(null, getBopisShippingErrorURL(), pRequest, pResponse);
    }

    processBopis(pRequest, pResponse, MFFConstants.HANDLE_APPLY_SHIPPING_BOPIS_METHOD_NAME);

    return checkFormRedirect(getBopisShippingSuccessURL(), getBopisShippingErrorURL(), pRequest, pResponse);
  }

  /**
   * Handle Method to Apply Shipping Method
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleApplyShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogInfo("Action: handleApplyShippingMethod for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());

    validateShippingMethod(pRequest);
    if (this.hasFormError()) {
      return checkFormRedirect(null, getShipMethodErrorURL(), pRequest, pResponse);
    }
    processShippingMethod(pRequest, pResponse, "handleApplyShippingMethod");
    
    return checkFormRedirect(getShipMethodSuccessURL(), getShipMethodErrorURL(), pRequest, pResponse);
  }

  /**
   * This method helps to set the shipping method on the Order
   * 
   * @param pRequest
   * @param pResponse
   * @param pMethodName
   * @return
   * @throws ServletException
   * @throws IOException
   */
  private boolean processShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pMethodName) throws ServletException, IOException {
    if (isLoggingInfo()) {
      vlogInfo("Action: processShippingMethod for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());
    }
    boolean success = true;
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    Transaction tr = null;

    if ((rrm == null) || (rrm.isUniqueRequestEntry(pMethodName))) {
      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();
        synchronized (currentOrder) {

          try {
            HardgoodShippingGroup shippingGroup = getShippingGroup(currentOrder);
            if (shippingGroup != null) {
              vlogDebug("ShippingGroup::" + shippingGroup);
              vlogDebug("ShippingMethod::" + getShippingMethod());
              MFFHardgoodShippingGroup shipGroup = (MFFHardgoodShippingGroup) shippingGroup;
              shipGroup.setShippingMethod(getShippingMethod());
              shipGroup.setSaturdayDelivery(isSaturdayDelivery());
              // set the shipping method in session
              getCheckoutManager().setShippingMethod(getShippingMethod());
              // Update the Order
              ((MFFOrderManager) getOrderManager()).updateOrder(currentOrder, pMethodName);

            } else {
              if (isLoggingError()) {
                logError("Cannot store ShippingAddress and ShippingMethod because ShippingGroup value is null.");
              }
              return false;
            }
            
            // run shipping group validation pipeline
            if (isValidateShippingGroups()) {
              vlogDebug("MFFShippingGroupFormHandler.validateShippingGroups set to true, so calling pipeline processor...");
              runProcessValidateShippingGroups(currentOrder, getUserPricingModels(), getLocale(), getProfile(), null);
              // Update the Order
              ((MFFOrderManager) getOrderManager()).updateOrder(currentOrder);
            } else {
              vlogDebug("MFFShippingGroupFormHandler.validateShippingGroups set to false, so pipeline processor not called...");
            }
            
            getCheckoutManager().repriceOrder("ORDER_TOTAL", getProfileTools().getUserLocale(pRequest, pResponse));
            
            try {
              getCheckoutManager().updatePaymentGroups(true);
              getCheckoutManager().isOrderRequiresCreditCard();
            } catch (MFFOrderUpdateException exc) {
              logError(exc);
            }

          } catch (CommerceException e) {
            getCheckoutManager().authorizeShippingStep();
            success = false;
            if (isLoggingError()) {
              logError(e);
            }
          } catch (RunProcessException e) {
            success = false;
            if (isLoggingError()) {
              logError(e);
            }
          }
        }
      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }
        if (rrm != null) {
          rrm.removeRequestEntry(pMethodName);
        }
      }
    }
    
    if(getFormError()){
      getCheckoutManager().authorizeShippingStep();
    }else{
      getCheckoutManager().authorizePaymentStep();
    }
    
    return success;
  }

  /**
   * This method processes the Bopis and sets the pick up person info
   * 
   * @param pRequest
   * @param pResponse
   * @param pMethodName
   * @return
   * @throws ServletException
   * @throws IOException
   */
  private boolean processBopis(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,

      String pMethodName) throws ServletException, IOException {
    vlogInfo("Action: processBopis for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());

    boolean success = true;
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    Transaction tr = null;

    if ((rrm == null) || (rrm.isUniqueRequestEntry(pMethodName))) {
      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();
        synchronized (currentOrder) {
          try {
            // TODO: apply the rules here, like skipping address validation etc
            // for bopis
            processShippingGroup(pRequest, pResponse, MFFConstants.HANDLE_APPLY_SHIPPING_GROUPS_METHOD_NAME);

            // BZ 2505 - Contact info will not be captured if cart
            // has ageRestricted items
            if (!currentOrder.isSignatureRequired() && !getPickUpPersonName().isEmpty() && !getPickUpPersonEmail().isEmpty()) {
              if (isLoggingDebug()) {
                logDebug("PickUP Person Name::" + getPickUpPersonName());
                logDebug("Pick Up Person Email::" + getPickUpPersonEmail());
              }
              
              if(getPickUpPersonName().contains("-")){
            	  String[] arr=getPickUpPersonName().split("-");
            	  if(arr.length==2){
            		  String firstName=arr[0];
                	  String lastName=arr[1];
                	  if(!firstName.isEmpty()){
                		  if(!lastName.isEmpty()){
                			  String personName=firstName.trim()+" "+lastName.trim();
                			  currentOrder.setBopisPerson(personName);
                		  }
                		  else {
                			  String personName=firstName.trim();
                			  currentOrder.setBopisPerson(personName);
                		  }
                	  }
            	  }
            	  else if(arr.length==1) {
            		  String firstName=arr[0];
            		  if(!firstName.isEmpty()){
            			  String personName=firstName.trim();
            			  currentOrder.setBopisPerson(personName);
            		  }
            		  else {
            			  currentOrder.setBopisPerson(getPickUpPersonName().trim());
            		  }
            	  }  
              }
              else {
            	  currentOrder.setBopisPerson(getPickUpPersonName().trim());
              }
              currentOrder.setBopisEmail(getPickUpPersonEmail());

              // Update the Order
              ((MFFOrderManager) getOrderManager()).updateOrder(currentOrder, pMethodName);

            } else {
              if (isLoggingError()) {
                logError("Cannot store ShippingAddress and ShippingMethod because ShippingGroup value is null.");
              }
              return false;
            }
          } catch (CommerceException e) {
            getCheckoutManager().authorizeShippingStep();
            success = false;
            if (isLoggingError()) {
              logError(e);
            }
          }
        }
      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }
        if (rrm != null) {
          rrm.removeRequestEntry(pMethodName);
        }
      }
    }
    
    if(getFormError()){
      getCheckoutManager().authorizeShippingStep();
    }else{
      getCheckoutManager().authorizePaymentStep();
    }
    
    return success;
  }

  /**
   * Validates Shipping Group and Add it to Order
   * 
   * @param pRequest
   * @param pResponse
   * @param pMethodName
   * @return
   * @throws ServletException
   * @throws IOException
   */
  private boolean processShippingGroup(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pMethodName) throws ServletException, IOException {

    vlogInfo("ShippingGroupFormHandler: processShippingGroup for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());
    
    MFFOrderImpl order = (MFFOrderImpl) getOrder();

    boolean lFFLOrder = order.isFFLOrder();

    // make sure FFL Dealer Number is provided
    if (lFFLOrder) {
      if (getFflDealerFFNumber() == null || getFflDealerFFNumber().isEmpty()) {
        vlogError("FFL Dealer Id is not provided for the order:{0}", getOrder().getId());
        getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_MISSING_FFL, true, this, pRequest);
        return false;
      }
    }

    // initialize shipping group
    initShippingAddress(pRequest);

    // validate shipping address
    boolean valid = validateShippingAddress(pRequest, getShippingAddress());
    if (!valid) {
      if (isLoggingError()) {
        vlogError("Found invalid address ({0}) with postalCode ({1}) when processing the shipping addess for order ({2})", getShippingAddress(), getShippingAddress().getPostalCode(), getOrder().getId());
      }
      getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_MISSING_DATA, true, this, pRequest);
      return false;
    } else {
		if (!getCheckoutManager().isShippingAvsVerified()) {
	    	Map inputAddressParam = new HashMap();
	    	inputAddressParam.put(MFFConstants.ADDRESS_POSTAL_CODE, getShippingAddress().getPostalCode());
	    	inputAddressParam.put(MFFConstants.ADDRESS_STATE, getShippingAddress().getState());
	    	inputAddressParam.put(MFFConstants.ADDRESS_CITY, getShippingAddress().getCity());
	    	
	    	if (!getZipCodeHelper().isValidateCityStateZipCombination(inputAddressParam)) {
	    		if (isLoggingDebug()) {
	    			logDebug("Shipping Address: Invalid City, State, Postal Code combination.");
	    		}
	    		String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION));
	    		getFormExceptionGenerator().generateInlineException(resourceMsg, getZipCodeHelper().getCityStateZipErrorField(), this, pRequest);
	    		return false;
	    	}
	    }
    }
    
    vlogDebug("processShippingGroup(): state: " + getState());
    MFFOrderTools orderTools = (MFFOrderTools) getOrderManager().getOrderTools();
    if(getState().equalsIgnoreCase("AA") || getState().equalsIgnoreCase("AE") || getState().equalsIgnoreCase("AP")) {
        
        if(!orderTools.isOrderWeightValidForAPOFPO(order) || !orderTools.isItemDimensionsValidForAPOFPO(order)) {
            
            vlogError("We can not ship this order to APO or FPO: order:{0}", getOrder().getId());
            getFormExceptionGenerator().generateException("shippingAPOFPORestricted", true, this, pRequest);
            return false;
        }
    }

    // if FFL order check if state is AK or HI, we currently do not ship ffl
    // items to AK and HI
    MFFPricingUtil util = new MFFPricingUtil();
    boolean isLTL = util.isLTLOrderByItems(order.getCommerceItems());
	if (lFFLOrder || isLTL) {
      if (getState().equalsIgnoreCase("Alaska") || getState().equalsIgnoreCase("AK") || getState().equalsIgnoreCase("Hawaii") || getState().equalsIgnoreCase("HI")) {
    	  
    	  if (lFFLOrder){
    		  vlogError("We can not ship an FFL order to Alaska & Hawai order:{0}", getOrder().getId());
    		  getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_RESTRICTED_FFL, true, this, pRequest);
    	  } else if (isLTL){
    		  vlogError("We can not ship an LTL order to Alaska & Hawai order:{0}", getOrder().getId());
    		  getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_RESTRICTED_LTL, true, this, pRequest);
    	  }
        return false;
      }
    }

    // validate nick name if user opts to save the address
    if (((MFFProfile) getProfile()).isHardLoggedIn() && isSaveShippingAddress()) {
      if (!validateNickname(pRequest, getAddressNickname())) return false;
    }

    // if shipping state is in the list of restricted_loc of sku then throw an
    // error
    boolean restrictedShipping = ((MFFOrderTools) getOrderManager().getOrderTools()).restrictedShippingLocation(getOrder(), getShippingAddress());
    vlogDebug("ShippingGroupFormHandler: processShippingGroup: retrictedShipping:{0}", restrictedShipping);
    if (restrictedShipping) {
      getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_RESTRICTED, true, this, pRequest);
      return false;
    }

    // if any errors found at this stage, redirect to error page
    if (getFormError()) return checkFormRedirect(getAvsShippingSuccessURL(), getAvsShippingErrorURL(), pRequest, pResponse);

    vlogDebug("Shipping Address Validation Completed");
    boolean success = true;
    boolean lBopisOrder = ((MFFOrderImpl) getOrder()).isBopisOrder();

    // perform avs only if it is not performed yet
    if (!getCheckoutManager().isShippingAvsVerified()) {
      boolean displayShippingAvsModal = performShippingAVS();
      if (displayShippingAvsModal) { // Display Shipping AVS Modal
        return checkFormRedirect(getAvsShippingSuccessURL(), getAvsShippingErrorURL(), pRequest, pResponse);
      }
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    Transaction tr = null;

    if ((rrm == null) || (rrm.isUniqueRequestEntry(pMethodName))) {
      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();
        synchronized (currentOrder) {
          try {
            HardgoodShippingGroup shippingGroup = getShippingGroup(currentOrder);

            vlogDebug("ShippingGroup::{0}", shippingGroup);

            if (shippingGroup != null) {
              vlogDebug("ShippingGroup Id:{0}", shippingGroup.getId());
              if (isLoggingDebug()) logDebug("ShippingGroup::" + shippingGroup);
              vlogInfo("firstName:{0}", getShippingAddress().getFirstName());
              // set shipping address
              shippingGroup.setShippingAddress(getShippingAddress());
              // set ffl dealer id, can be null for normal order
              // will not be null for FFLOrder as we validate the input prior to
              // this step
              currentOrder.setFFLDealerId(getFflDealerFFNumber());

              // if Bopis order set/default the shipping method to Bopis
              // as we do not have shippingMethod step for BopisOrders
              if (lBopisOrder) {
                shippingGroup.setShippingMethod("Bopis");
              }
              // set any special instructions if available
              Map<String, String> pSpecialInstructions = new HashMap<String, String>();
              if(StringUtils.isNotEmpty(getSpecialInstructions())){
                pSpecialInstructions.put("instructions", getSpecialInstructions());
              }
              shippingGroup.setSpecialInstructions(pSpecialInstructions);
            } else {
              if (isLoggingError()) {
                logError("Cannot store ShippingAddress and ShippingMethod because ShippingGroup value is null.");
              }
              return false;
            }

            ((MFFOrderManager) getOrderManager()).updateOrder(currentOrder, pMethodName);

          } catch (CommerceException e) {
            success = false;
            if (isLoggingError()) {
              logError(e);
            }
          }
        }

        if(getFormError()){
          // authorize the shipping step
          getCheckoutManager().authorizeShippingStep();
        }

        boolean saveAddress = isSaveShippingAddress();
        vlogDebug("Should we save Address:" + saveAddress);
        vlogDebug("Is the user hard logged in:" + ((MFFProfile) getProfile()).isHardLoggedIn());
        if (((MFFProfile) getProfile()).isHardLoggedIn() && saveAddress) {
          getCheckoutManager().setSaveShippingAddress(true);
        } else {
          getCheckoutManager().setSaveShippingAddress(false);
        }

      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }
        if (rrm != null) {
          rrm.removeRequestEntry(pMethodName);
        }
      }

    }
    return success;
  }
  
  
  

  /**
   * This method called when user choose to shipMyOrderInstead on PDP and Cart
   * pages We will remove any instore only items from the order and reset all
   * Bopis Info on the order
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleShipMyOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogInfo("Action: handleShipMyOrder for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());

    try {
      ((MFFOrderManager) getOrderManager()).shipMyOrderInstead(getOrder());
    } catch (CommerceException e) {
      vlogError("There is an error while updating the order with bopis information");
      getFormExceptionGenerator().generateException(MFFConstants.MSG_BOPIS_ORDER_UPDATE_ERROR, true, this, pRequest);
      return checkFormRedirect(null, getShipMyOrderErrorURL(), pRequest, pResponse);
    }

    return checkFormRedirect(getShipMyOrderSuccessURL(), getShipMyOrderErrorURL(), pRequest, pResponse);
  }
  
  /**
   * get shipping group
   * @return
   */
  public HardgoodShippingGroup getShippingGroup() {
    
    vlogDebug("Action: getShippingGroup for order id ({0}) and profile id ({1})",getOrder().getId(),getProfile().getRepositoryId());
    
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    if (mShippingGroup == null) {
      OrderHolder sc = getShoppingCart();
      Order currOrder = null;

      if (sc == null) {
        if (isLoggingError()) {
          logError("Why is Shopping Cart null??");
        }
        return null;
      }

      else {
        currOrder = getShoppingCart().getCurrent();
        if (currOrder == null) {
          if (isLoggingError()) {
            logError("Why is Current order null??  Can't proceed...");
          }
          getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_FORM_ERRORS,true,this,request);
          return null;
        }
        else {
          if (isLoggingDebug()) {
            logDebug("currOrder:" + currOrder);
          }
        }
      }

      if (currOrder != null) {
        int sgCount = currOrder.getShippingGroupCount();
        if (sgCount == 0) {
          if (isLoggingError()) {
            logError("Error in MFFShippingGroupFormHandler - no shippingGroup!...");
          }
          getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_FORM_ERRORS,true,this,request);
        }
        else if (sgCount > 1) {
          if (isLoggingError()) {
            logError("Error in MFFShippingGroupFormHandler - too many shippingGroups!...");
          }
          getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_FORM_ERRORS,true,this,request);
        }
        else if (sgCount == 1) {
          mShippingGroup = (HardgoodShippingGroup) currOrder.getShippingGroups().get(0);
        }
      }
    }
    return mShippingGroup;
  }
  
  /**
   * Helper Method to perform AVS on Shipping Address
   * 
   * @return
   */
  public boolean performShippingAVS() {
    Order order = getCheckoutManager().getOrder();
    if (isLoggingInfo()) vlogInfo("Action - performShippingAVS for order - {0}", order.getId());

    boolean displayAvsModal = false;
    ContactInfo shipAddress = this.getShippingAddress();
    AddressVerificationVO avsShippingVO = getCheckoutManager().performAVSCheck(shipAddress);
    if (avsShippingVO != null && avsShippingVO.isDisplaySuggestion()) {
      if (isLoggingDebug()) logDebug("******** Kick the avs address modal");
      getCheckoutManager().setShippingAvsVO(avsShippingVO);
      displayAvsModal = true;
    } else {
      getCheckoutManager().setShippingAvsVerified(avsShippingVO.isAvsVerified());
      displayAvsModal = false;
    }
    setDisplayAvsModal(displayAvsModal);
    return displayAvsModal;
  }

  /**
   * Helper method to create shipping address from form fields (request)
   * 
   * @param pRequest
   * @return
   */
  private ContactInfo createShippingAddressFromFormFields(DynamoHttpServletRequest pRequest) {

    MFFContactInfo shipAddress = new MFFContactInfo();
    shipAddress.setFirstName(getFirstName());
    shipAddress.setLastName(getLastName());
    shipAddress.setAttention(getSpecialInstructions());
    shipAddress.setAddress1(getAddress1());
    shipAddress.setAddress2(getAddress2());
    shipAddress.setCity(getCity());
    shipAddress.setState(getState());
    shipAddress.setPostalCode(getPostalCode());
    shipAddress.setCountry(getCountry());
    shipAddress.setPhoneNumber(getPhoneNumber());
    //BZ-2981
    /*boolean valid = validateShippingAddress(pRequest, shipAddress);
    if (!valid) {
      getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_MISSING_DATA, true, this, pRequest);
    }*/
    setShippingAddress(shipAddress);
    return shipAddress;
  }

  /**
   * Helper method to create shipping address from ffl shipping address form
   * 
   * @param pRequest
   * @return
   */
  private ContactInfo createFFLShippingAddressFromFormFields(DynamoHttpServletRequest pRequest) {

	MFFContactInfo shipAddress = new MFFContactInfo();
    shipAddress.setCompanyName(getCompanyName());
    shipAddress.setFirstName(getFflContactName());
    shipAddress.setLastName(getFflContactName());
    shipAddress.setAttention(getSpecialInstructions());
    shipAddress.setAddress1(getAddress1());
    shipAddress.setAddress2(getAddress2());
    shipAddress.setCity(getCity());
    shipAddress.setState(getState());
    shipAddress.setPostalCode(getPostalCode());
    shipAddress.setCountry(getCountry());
    shipAddress.setPhoneNumber(getPhoneNumber());
    shipAddress.setEmail(getFflEmail());
    boolean valid = validateShippingAddress(pRequest, shipAddress);
    if (!valid) {
      getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_MISSING_DATA, true, this, pRequest);
    }
    setShippingAddress(shipAddress);
    return shipAddress;
  }

  /**
   * Create shipping address from selected address (registered user)
   * 
   * @return
   */
  private ContactInfo createShippingAddressFromSelected() {

	MFFContactInfo shipAddress = new MFFContactInfo();
    RepositoryItem repositoryAddress = getProfileTools().getProfileAddressById(getAddressId());

    if (repositoryAddress == null) {
      if (isLoggingWarning()) {
        logWarning("no address found with requested address Id: " + getAddressId());
      }
    } else {
      try {

        OrderTools.copyAddress(repositoryAddress, shipAddress);
        if (isLoggingWarning()) {
          logDebug("Country is **** : " + shipAddress.getCountry());
        }
        if (shipAddress.getCountry() == null) {
          shipAddress.setCountry(MFFConstants.DEFAULT_COUNTRY);
        }
        setShippingAddressSelectedInFormHandler(shipAddress);
      } catch (CommerceException e) {
        if (isLoggingError()) {
          logError("Error getting address from profile. ", e);
        }
      }
    }

    return shipAddress;
  }

  /**
   * Helper method to create shipping address form store address (for Bopis)
   * 
   * @return
   */
  private ContactInfo createShippingAddressFromStore() {

    ContactInfo lStoreAddress = new ContactInfo();
    RepositoryItem storeItem = getLocationTools().getStoreByLocationId(getCheckoutManager().getBopisStore());

    if (storeItem == null) {
      if (isLoggingWarning()) {
        logWarning("no store found with requested store Id: " + getCheckoutManager().getBopisStore());
      }
    } else {
      try {

        OrderTools.copyAddress(storeItem, lStoreAddress);
        String storeName = storeItem.getPropertyValue("city").toString();
        String state = storeItem.getPropertyValue("stateAddress").toString();

        lStoreAddress.setFirstName(storeName);
        lStoreAddress.setLastName(storeName);
        lStoreAddress.setState(state);

        if (isLoggingWarning()) {
          logDebug("Country is **** : " + lStoreAddress.getCountry());
        }
        // setShippingAddressSelectedInFormHandler(lStoreAddress);
      } catch (CommerceException e) {
        if (isLoggingError()) {
          logError("Error getting address of the store ", e);
        }
      }
    }
    return lStoreAddress;
  }

  /**
   * Helper method to set initialized shipping address to the form
   * 
   * @param shipAddress
   */
  private void setShippingAddressSelectedInFormHandler(MFFContactInfo shipAddress) {
    this.setFirstName(shipAddress.getFirstName());
    this.setLastName(shipAddress.getLastName());
    this.setSpecialInstructions(shipAddress.getAttention());
    this.setAddress1(shipAddress.getAddress1());
    this.setAddress2(shipAddress.getAddress2());
    this.setCity(shipAddress.getCity());
    this.setState(shipAddress.getState());
    this.setPostalCode(shipAddress.getPostalCode());
    this.setCountry(shipAddress.getCountry());
    this.setPhoneNumber(shipAddress.getPhoneNumber());
  }

  /**
   * Helper method to validate the nick name (for registered user if choose to
   * save address)
   * 
   * @param pRequest
   * @param nickname
   * @return
   */
  private boolean validateNickname(DynamoHttpServletRequest pRequest, String nickname) {
    // Validate nickName only for hard logged in users
    if (((MFFProfile) getProfile()).isHardLoggedIn()) {
      // Validate NickName is present
      if (StringUtils.isBlank(nickname)) {
        getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_ADDRESS_NICKNAME_MISSING, true, this, pRequest);
        return false;
      } else {
        if (getProfileTools().isDuplicateAddressNickName(getProfile(), nickname)) {
          getFormExceptionGenerator().generateException(MFFConstants.MSG_DUPLICATE_NICKNAMEADDRESS, true, this, pRequest);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Helper method to perform OOTB address validation on the shipping address
   * 
   * @param pRequest
   * @param shipAddress
   * @return
   */
  private boolean validateShippingAddress(DynamoHttpServletRequest pRequest, ContactInfo shipAddress) {
    boolean valid = true;
    getAddressValidator().setShippingMethod(getShippingMethod());

    getAddressValidator().validateAddress(shipAddress, pRequest, this);
    
    if(isSaveShippingAddress()) {
    	if(getAddressNickname() != null && getAddressNickname().length() > 42) {
    		//addFormException(new DropletException("Address Name should be less than 42 characters"));
    		getFormExceptionGenerator().generateInlineException(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_NICKNAME_MAX_LEN), "address-name", this, pRequest);
    		//getFormExceptionGenerator().generateException("Address Name should be less than 42 characters", this);
    	}
    }

    @SuppressWarnings("unchecked")
    List<DropletException> errorList = getFormExceptions();

    vlogDebug("ShippingGroupFormHandler: Error List Size:{0}", errorList.size());

    if (errorList != null && !errorList.isEmpty()) {
      for (int i = 0; i < errorList.size(); i++) {
        vlogDebug("ShippingGroupFormHandler: Error:{0}, : Message:{1}", i, errorList.get(i).getMessage());
        // For reg user, if there is form field error add it to the main error as well
        if(((MFFProfile)getProfile()).isHardLoggedIn() && !getAddressId().equals(ADDRESS_ID_ZERO)){
          DropletException dException = errorList.get(i);
          if(dException instanceof MFFInlineDropletFormException)
            getFormExceptionGenerator().generateException(dException.getMessage(), this);
        }
        valid = false;
      }
    }
    return valid;
  }

  /**
   * Helper method to return shipping group object from the current order
   * @param currentOrder
   * @return
   */
  public HardgoodShippingGroup getShippingGroup(MFFOrderImpl currentOrder) {
    vlogDebug("Action: getShippingGroup(currentOrder) for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());
    
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    if (mShippingGroup == null) {
      if (currentOrder != null) {
        int sgCount = currentOrder.getShippingGroupCount();
        if (sgCount == 0) {
          if (isLoggingError()) {
            logError("Error in MFFShippingGroupFormHandler - no shippingGroup!...");
          }
          getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_FORM_ERRORS, true, this, request);
        } else if (sgCount > 1) {
          if (isLoggingError()) {
            logError("Error in MFFShippingGroupFormHandler - too many shippingGroups!...");
          }
          getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_FORM_ERRORS, true, this, request);
        } else if (sgCount == 1) {
          mShippingGroup = (HardgoodShippingGroup) currentOrder.getShippingGroups().get(0);
        }
      } else {
        if (isLoggingError()) {
          logError("Why is Current order null??  Can't proceed...");
        }
        getFormExceptionGenerator().generateException(MFFConstants.SHIPPING_FORM_ERRORS, true, this, request);
        return null;
      }
    }
    return mShippingGroup;
  }
  
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

    vlogDebug("ShippingGroupFormHandler: Error List Size:{0}", errorList.size());

    if (errorList != null && !errorList.isEmpty()) {
      for (int i = 0; i < errorList.size(); i++) {
        vlogDebug("ShippingGroupFormHandler: Error:{0}, : Message:{1}", i, errorList.get(i).getMessage());
      }
    }
  }

  /******************************/
  /***** Setters and Getters ******/
  /*******************************/

  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }

  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }

  public void setProfileTools(MFFProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  public MFFProfileFormHandler getProfileFormHandler() {
    return mProfileFormHandler;
  }

  public void setProfileFormHandler(MFFProfileFormHandler pProfileFormHandler) {
    mProfileFormHandler = pProfileFormHandler;
  }

  public void setShippingGroup(HardgoodShippingGroup pShippingGroup) {
    mShippingGroup = pShippingGroup;
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
   * Returns the current FirstName value for Address.
   */
  public String getFirstName() {
    return mFirstName;
  }

  /**
   * Sets the FirstName value on Shipping Address.
   */
  public void setFirstName(String pNewVal) {
    mFirstName = pNewVal;
  }

  /**
   * Returns the current LastName value for Address.
   */
  public String getLastName() {
    return mLastName;
  }

  /**
   * Sets the LastName value on Shipping Address.
   */
  public void setLastName(String pNewVal) {
    mLastName = pNewVal;
  }

  /**
   * Returns the current Address1 value for Address.
   */
  public String getAddress1() {
    return mAddress1;
  }

  /**
   * Sets the Address1 value on Shipping Address.
   */
  public void setAddress1(String pNewVal) {
    mAddress1 = pNewVal;
  }

  /**
   * Returns the current Address2 value for Address.
   */
  public String getAddress2() {
    return mAddress2;
  }

  /**
   * Sets the Address2 value on Shipping Address.
   */
  public void setAddress2(String pNewVal) {
    mAddress2 = pNewVal;
  }

  /**
   * Returns the current City value for Address.
   */
  public String getCity() {
    return mCity;
  }

  /**
   * Sets the City value on Shipping Address.
   */
  public void setCity(String pNewVal) {
    mCity = pNewVal;
  }

  /**
   * Returns the current State value for Address.
   */
  public String getState() {
    return mState;
  }

  /**
   * Sets the State value on Shipping Address.
   */
  public void setState(String pNewVal) {
    mState = pNewVal;
  }

  /**
   * Returns the current PostalCode value for Address.
   */
  public String getPostalCode() {
    return mPostalCode;
  }

  /**
   * Sets the PostalCode value on Shipping Address.
   */
  public void setPostalCode(String pNewVal) {
    mPostalCode = pNewVal;
  }

  /**
   * @return the mAddressType
   */
  public boolean isAddressType() {
    return mAddressType;
  }

  /**
   * @param pAddressType
   *          the mAddressType to set
   */
  public void setAddressType(boolean pAddressType) {
    this.mAddressType = pAddressType;
  }

  /**
   *
   * @return
   */
  public String getAddressId() {
    return mAddressId;
  }

  /**
   * @param pSelectedShippingAddress
   */
  public void setAddressId(String pAddressId) {
    mAddressId = pAddressId;
  }

  /**
   * @return
   */
  public boolean isSaveShippingAddress() {
    return mSaveShippingAddress;
  }

  /**
   * @param pSaveShippingAddress
   */
  public void setSaveShippingAddress(boolean pSaveShippingAddress) {
    mSaveShippingAddress = pSaveShippingAddress;
  }

  /**
   * @return
   */
  public boolean isAddressEditMode() {
    return mAddressEditMode;
  }

  /**
   * @param pAddressEditMode
   */
  public void setAddressEditMode(boolean pAddressEditMode) {
    mAddressEditMode = pAddressEditMode;
  }

  /**
   * Returns the current PhoneNumber value for Address.
   */
  public String getPhoneNumber() {
    return mPhoneNumber;
  }

  public void setPhoneNumber(String pPhoneNumber) {
    if (pPhoneNumber != null) {
      mPhoneNumber = pPhoneNumber.replaceAll("[^0-9]*", MFFConstants.EMPTY_STRING);
    }
  }

  /**
   * Returns the current Country value for Address.
   */
  public String getCountry() {
    return mCountry;
  }

  /**
   * Sets the Country value on Shipping Address.
   */
  public void setCountry(String pNewVal) {
    mCountry = pNewVal;
  }

  /**
   * Returns the shipping method for Address.
   */
  public String getShippingMethod() {
    return mShippingMethod;
  }

  /**
   * @param pShippingMethod
   */
  public void setShippingMethod(String pShippingMethod) {
    mShippingMethod = pShippingMethod;
  }

  /**
   * @return the mPricingTools
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  /**
   * @param mPricingTools
   *          the mPricingTools to set
   */
  public void setPricingTools(PricingTools pPricingTools) {
    this.mPricingTools = pPricingTools;
  }

  public String getGuestCheckoutSuccessURL() {
    return mGuestCheckoutSuccessURL;
  }

  public void setGuestCheckoutSuccessURL(String pGuestCheckoutSuccessURL) {
    mGuestCheckoutSuccessURL = pGuestCheckoutSuccessURL;
  }

  public String getGuestCheckoutErrorURL() {
    return mGuestCheckoutErrorURL;
  }

  public void setGuestCheckoutErrorURL(String pGuestCheckoutErrorURL) {
    mGuestCheckoutErrorURL = pGuestCheckoutErrorURL;
  }

  /**
   * @return the mOldAddressNickname
   */
  public String getOldAddressNickname() {
    return mOldAddressNickname;
  }

  /**
   * @param pOldAddressNickname
   *          the mOldAddressNickname to set
   */
  public void setOldAddressNickname(String pOldAddressNickname) {
    this.mOldAddressNickname = pOldAddressNickname;
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
   * @return the mShippingAddress
   */
  public ContactInfo getShippingAddress() {
    return mShippingAddress;
  }

  /**
   * @param pShippingAddress
   *          the mShippingAddress to set
   */
  public void setShippingAddress(ContactInfo pShippingAddress) {
    this.mShippingAddress = pShippingAddress;
  }

	public List<MFFInlineDropletFormException> getFormFieldExceptions() {
		return getFormExceptionGenerator().getFormFieldExceptions(
				getFormExceptions());
	}

	public List<DropletException> getNonFormFieldExceptions() {
		return getFormExceptionGenerator().getNonFormFieldExceptions(
				getFormExceptions());
	}

  public MFFAddressValidator getAddressValidator() {
    return mAddressValidator;
  }

  public void setAddressValidator(MFFAddressValidator pAddressValidator) {
    mAddressValidator = pAddressValidator;
  }

  public String getAvsShippingSuccessURL() {
    return mAvsShippingSuccessURL;
  }

  public void setAvsShippingSuccessURL(String pAvsShippingSuccessURL) {
    mAvsShippingSuccessURL = pAvsShippingSuccessURL;
  }

  public String getAvsShippingErrorURL() {
    return mAvsShippingErrorURL;
  }

  public void setAvsShippingErrorURL(String pAvsShippingErrorURL) {
    mAvsShippingErrorURL = pAvsShippingErrorURL;
  }

  public String getShipMethodSuccessURL() {
    return mShipMethodSuccessURL;
  }

  public void setShipMethodSuccessURL(String pShipMethodSuccessURL) {
    mShipMethodSuccessURL = pShipMethodSuccessURL;
  }

  public String getShipMethodErrorURL() {
    return mShipMethodErrorURL;
  }

  public void setShipMethodErrorURL(String pShipMethodErrorURL) {
    mShipMethodErrorURL = pShipMethodErrorURL;
  }

  public String getShipMyOrderSuccessURL() {
    return mShipMyOrderSuccessURL;
  }

  public void setShipMyOrderSuccessURL(String pShipMyOrderSuccessURL) {
    mShipMyOrderSuccessURL = pShipMyOrderSuccessURL;
  }

  public String getShipMyOrderErrorURL() {
    return mShipMyOrderErrorURL;
  }

  public void setShipMyOrderErrorURL(String pShipMyOrderErrorURL) {
    mShipMyOrderErrorURL = pShipMyOrderErrorURL;
  }

  public String getPickUpPersonName() {
    return mPickUpPersonName;
  }

  public void setPickUpPersonName(String pPickUpPersonName) {
    mPickUpPersonName = pPickUpPersonName;
  }

  public String getPickUpPersonEmail() {
    return mPickUpPersonEmail;
  }

  public void setPickUpPersonEmail(String pPickUpPersonEmail) {
    mPickUpPersonEmail = pPickUpPersonEmail;
  }

  public String getBopisShippingSuccessURL() {
    return mBopisShippingSuccessURL;
  }

  public void setBopisShippingSuccessURL(String pBopisShippingSuccessURL) {
    mBopisShippingSuccessURL = pBopisShippingSuccessURL;
  }

  public String getBopisShippingErrorURL() {
    return mBopisShippingErrorURL;
  }

  public void setBopisShippingErrorURL(String pBopisShippingErrorURL) {
    mBopisShippingErrorURL = pBopisShippingErrorURL;
  }

  public StoreLocatorTools getLocationTools() {
    return mLocationTools;
  }

  public void setLocationTools(StoreLocatorTools pLocationTools) {
    mLocationTools = pLocationTools;
  }

  @Override
  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  @Override
  public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator) {
    mFormExceptionGenerator = pFormExceptionGenerator;
  }

  public boolean isAddressVerified() {
    return mAddressVerified;
  }

  public void setAddressVerified(boolean pAddressVerified) {
    mAddressVerified = pAddressVerified;
  }

  public String getSpecialInstructions() {
    return mSpecialInstructions;
  }

  public void setSpecialInstructions(String pSpecialInstructions) {
    mSpecialInstructions = pSpecialInstructions;
  }

  public boolean isSaturdayDelivery() {
    return mSaturdayDelivery;
  }

  public void setSaturdayDelivery(boolean pSaturdayDelivery) {
    mSaturdayDelivery = pSaturdayDelivery;
  }

  public String getCompanyName() {
    return mCompanyName;
  }

  public void setCompanyName(String pCompanyName) {
    mCompanyName = pCompanyName;
  }

  public String getFflEmail() {
    return mFflEmail;
  }

  public void setFflEmail(String pFflEmail) {
    mFflEmail = pFflEmail;
  }

  public String getFflContactName() {
    return mFflContactName;
  }

  public void setFflContactName(String pFflContactName) {
    mFflContactName = pFflContactName;
  }

  public boolean isFflAddressVerified() {
    return mFflAddressVerified;
  }

  public void setFflAddressVerified(boolean pFflAddressVerified) {
    mFflAddressVerified = pFflAddressVerified;
  }

  public String getFflDealerFFNumber() {
    return mFflDealerFFNumber;
  }

  public void setFflDealerFFNumber(String pFflDealerFFNumber) {
    mFflDealerFFNumber = pFflDealerFFNumber;
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
