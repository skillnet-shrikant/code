package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import com.aci.payment.creditcard.AciCreditCard;
import com.fedex.service.addressvalidation.FedexAddressValidationService;
import com.fedex.service.addressvalidation.beans.AddressValidationInput;
import com.fedex.service.addressvalidation.beans.AddressValidationOutput;
import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.pricing.MFFOrderPriceInfo;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

public class MFFCheckoutManager extends GenericService {
	
	private String mCartSuccessMessage;
	private String mCartErrorMessage;
	private String mPaymentErrorMessage;
	private String mLoginErrorMessage;
	Map<String, String> mCouponMessages;
	private CheckoutStep mCheckoutStepAuthorized = CheckoutStep.getDefaultStep();
	private OrderHolder mShoppingCart;
	private String mShippingMethod;
	private HardgoodShippingGroup mShippingGroup = null;
	private String mShippingAddressId;
	private String mShippingAddressNickName;
	private boolean mSaveShippingAddress=false;
	private boolean mBillingAddressSameAsShipping;
	private ContactInfo mShippingAddressInForm;
	private String mCreditCardId;
	private boolean mCardAuthorized=false;
	private ContactInfo mBillingAddressInForm;
	private boolean mSavePaymentMethod=false;
	private FedexAddressValidationService mAvsService;
	private boolean mShippingAvsVerified;
	private AddressVerificationVO mShippingAvsVO;
	private AddressVerificationVO mBillingAvsVO;
	private boolean mBillingAvsVerified;
	private RepositoryItem mSelectedBillingAddress;
	private String mPaymentName;
	private String mBopisStore;
	private boolean mBopis;
	private String mCartMessage;
	private boolean mExpressCheckout;
	
	private int mCardAttempt;
	
	private PaymentGroupManager mPaymentGroupManager;
	private MFFOrderManager mOrderManager;
	private PricingTools mPricingTools;
	private double mAmountRequiredForCreditCard = -1.0;
	private CreditCard mCreditCardPaymentGroup = null;
	private PricingModelHolder mUserPricingModels;
	
	private boolean mTestGiftCardCall;
  private double mTestGiftCardAmount;
  
	public void resetCheckoutValues(){
	  vlogDebug("Resetting Checkout Values");
		this.mShippingMethod="";
		this.mShippingAddressId="";
		this.mShippingAddressNickName="";
		this.mSaveShippingAddress=false;
		this.mBillingAddressSameAsShipping=false;
		this.mShippingAddressInForm=null;
		this.mSavePaymentMethod=false;
		this.mCreditCardId="";
		this.mCardAuthorized=false;
		this.mBillingAddressInForm=null;
		this.mExpressCheckout=false;
		this.mCardAttempt=0;
		this.mCheckoutStepAuthorized=CheckoutStep.getDefaultStep();
		resetAvsValues();
		
		//wipe off payment info
		this.mGiftCardPaymentInfos=null;
		try {
      updatePaymentGroups(false);
      isOrderRequiresCreditCard();
    } catch (MFFOrderUpdateException e) {
      vlogError(e,"There is an error while resettting checkout values for order:{0}",getOrder().getId());
    }
	}
	
	public void resetCheckoutValuesPostCommit(){
    vlogDebug("Resetting Checkout Values post commit");
    this.mShippingMethod="";
    this.mShippingAddressId="";
    this.mShippingAddressNickName="";
    this.mSaveShippingAddress=false;
    this.mBillingAddressSameAsShipping=false;
    this.mShippingAddressInForm=null;
    this.mSavePaymentMethod=false;
    this.mCreditCardId="";
    this.mCardAuthorized=false;
    this.mBillingAddressInForm=null;
    this.mExpressCheckout=false;
    this.mCardAttempt=0;
    resetAvsValues();
    //wipe off giftcards
    this.mGiftCardPaymentInfos=null;
  }
	
	public void resetAvsValues(){
		this.mShippingAvsVerified=false;
		this.setShippingAvsVO(null);
		this.mBillingAvsVerified=false;
		this.setBillingAvsVO(null);
	}
	
	public void resetBillingValues(){
    this.mBillingAddressSameAsShipping=false;
    this.mCreditCardId="";
    this.mCardAuthorized=false;
    this.mBillingAddressInForm=null;
  }
	
	@SuppressWarnings("static-access")
  public boolean isSaturdayDelivery(ShippingGroup pShippingGroup, String shipMethod) {
    boolean returnFlag = false;
    
    if(pShippingGroup != null){
      
      Calendar currentTime = Calendar.getInstance();
      int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);
      
      String cutOffTime = MFFConstants.SHIPPING_CUT_OFF_TIME;
          
      String[] parts = cutOffTime.split(":");
      Calendar configureTime = Calendar.getInstance();
      configureTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
      configureTime.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            
        
        if(shipMethod.equalsIgnoreCase(MFFConstants.SECOND_DAY) && dayofWeek == currentTime.WEDNESDAY && currentTime.after(configureTime)){
          // Wednesday after 3:30PM
          returnFlag = true;
        } else if(shipMethod.equalsIgnoreCase(MFFConstants.SECOND_DAY) && dayofWeek == currentTime.THURSDAY && currentTime.before(configureTime)){
          // Thursday before 3:30PM
          returnFlag = true;
        } else if(shipMethod.equalsIgnoreCase(MFFConstants.OVER_NIGHT) && dayofWeek == currentTime.THURSDAY && currentTime.after(configureTime)){
          // Thursday after 3:30PM
          returnFlag = true;
        } else if(shipMethod.equalsIgnoreCase(MFFConstants.OVER_NIGHT) && dayofWeek == currentTime.FRIDAY && currentTime.before(configureTime)){
          // Friday before 3:30PM
          returnFlag = true;
        }
    }
    return returnFlag;
  }
	
  public AddressVerificationVO performAVSCheck(ContactInfo pEnteredAddress) {
    
    if(pEnteredAddress == null)
      return null;
    
    AddressVerificationVO addressVO;
    
    AddressValidationInput avsInput=new AddressValidationInput();
    
    avsInput.setAddressLine1(pEnteredAddress.getAddress1());
    avsInput.setAddressLine2(pEnteredAddress.getAddress2());
    avsInput.setCity(pEnteredAddress.getCity());
    avsInput.setState(pEnteredAddress.getState());
    avsInput.setZipCode(pEnteredAddress.getPostalCode());
    
    Address inputAddress=new Address();
    
    inputAddress.setAddress1(pEnteredAddress.getAddress1().trim().toUpperCase());
    if(pEnteredAddress.getAddress2() !=null ){
      inputAddress.setAddress2(pEnteredAddress.getAddress2().trim().toUpperCase());
    }
    inputAddress.setCity(pEnteredAddress.getCity().trim().toUpperCase());
    inputAddress.setState(pEnteredAddress.getState().trim().toUpperCase());
    inputAddress.setPostalCode(pEnteredAddress.getPostalCode().trim().toUpperCase());
    inputAddress.setCountry("US");
    
    ContactInfo suggestedAddress = new ContactInfo();
    
    AddressValidationOutput avsOutput;
    try {
      avsOutput = getAvsService().ValidateAddress(avsInput);
      if(avsOutput.isResolved()){
        
        suggestedAddress.setAddress1(avsOutput.getEffectiveStreetLine1().trim().toUpperCase());
        if(avsOutput.getEffectiveStreetLine2()!=null){
          suggestedAddress.setAddress2(avsOutput.getEffectiveStreetLine2().trim().toUpperCase());
        }
        suggestedAddress.setCity(avsOutput.getEffectiveCity().trim().toUpperCase());
        suggestedAddress.setState(avsOutput.getEffectiveState().trim().toUpperCase());
        suggestedAddress.setPostalCode(avsOutput.getEffectiveZipCode().trim().toUpperCase());
        suggestedAddress.setCountry("US");
        boolean matchFlag = false;
        if(inputAddress.getAddress1().equalsIgnoreCase(suggestedAddress.getAddress1()) && 
            inputAddress.getCity().equalsIgnoreCase(suggestedAddress.getCity()) &&
            inputAddress.getState().equalsIgnoreCase(suggestedAddress.getState())&&
            inputAddress.getCountry().equalsIgnoreCase(suggestedAddress.getCountry()) &&
            inputAddress.getPostalCode().equalsIgnoreCase(suggestedAddress.getPostalCode())){
          matchFlag = true;
        } 
        if(matchFlag && !StringUtils.isBlank(inputAddress.getAddress2()) &&
            inputAddress.getAddress2().equalsIgnoreCase(suggestedAddress.getAddress2())){
          matchFlag = true;
        } else if(matchFlag && !StringUtils.isBlank(inputAddress.getAddress2()) &&
            !inputAddress.getAddress2().equalsIgnoreCase(suggestedAddress.getAddress2())){
          matchFlag = false;
        } 
        if(matchFlag){
            suggestedAddress.setFirstName(pEnteredAddress.getFirstName());
            suggestedAddress.setLastName(pEnteredAddress.getLastName());
            addressVO = new AddressVerificationVO(true,true,pEnteredAddress,suggestedAddress,false);
        } else {
            suggestedAddress.setFirstName(pEnteredAddress.getFirstName());
            suggestedAddress.setLastName(pEnteredAddress.getLastName());
            addressVO = new AddressVerificationVO(true,true,pEnteredAddress,suggestedAddress,true);
        }
      }else{
          vlogError("Unable to validate the address ");
          addressVO = new AddressVerificationVO(false,false,pEnteredAddress,null,true);
      }
    } catch (RemoteException e) {
      vlogError("Exception occurred while executing FedEx Service :: "+e.getMessage());
      // AVS Call Status Failed
      if (isLoggingError()) logError ("AVSCallStatus returned as false, Ignoring the display of AVS Modal");
        addressVO = new AddressVerificationVO(false,false,pEnteredAddress,null,false);
    } 
    return addressVO;
  }
  
  public boolean updatePaymentGroups(boolean isShipping) throws MFFOrderUpdateException {
    vlogDebug("UpdatePaymentGroups - Entering");
    boolean lCCRequired = true;

    synchronized (getOrder()) {

      double totalPriceOfOrder = getTotalOrderPrice();
      double totalAmountUnaccountedFor = totalPriceOfOrder;

      double totalAmountForCreditCard = updateGiftCardPaymentGroups(totalAmountUnaccountedFor);

      lCCRequired = updateCreditCardPaymentGroup(totalAmountForCreditCard);

      if (lCCRequired) {
        if (!isShipping) {
          authorizePaymentStep();
        } else {
          authorizeShippingStep();
        }
      }

      // reorder payment groups if we have gift cards
      if (getOrder().getPaymentGroups().size() > 1) {
        sortPaymentGroups();
      }

    }
    vlogDebug("UpdatePaymentGroups - Exiting");
    return lCCRequired;
  }
  
  private double updateGiftCardPaymentGroups(double pTotalAmountUnaccountedFor) throws MFFOrderUpdateException {

    int numGiftCardPaymentGroups = 0;
    List<GiftCardPaymentInfo> giftCardPaymentInfos = new ArrayList<GiftCardPaymentInfo>(getGiftCardPaymentInfos().values());

    // sort the payment info objects in this order:
    //Collections.sort(giftCardPaymentInfos, mGiftCardPaymentInfoComparator);

    PaymentGroup currentGiftCardPaymentGroup = null;
    double amountPutOnThisGiftCard = 0.0;
    Order currentOrder = getOrder();

    // Update OrderPriceInfo.GiftCardTotal
    if (currentOrder == null || currentOrder.getPriceInfo() == null) {
        vlogError("Cannot update the OrderPriceInfo.GiftCardTotal because Order is null, or Order.OrderPriceInfo is null!");
    } else {
        vlogDebug("OrderPriceInfo.GiftCardTotal being reset to 0.0");
      ((MFFOrderPriceInfo) currentOrder.getPriceInfo()).setGiftCardPaymentTotal(0.0);
    }

    // synchronized (currentOrder) {

    // cycle thru GiftCardPaymentInfos, and for each associated GiftCard Payment Group, wipe out amounts, and remove relationships:
    // Returns the number of payment groups it found:
    numGiftCardPaymentGroups = clearOutAmountsAndRelationshipsInGiftCardPaymentGroups(currentOrder);

    // If there are some Gift Cards on the order:
    if (numGiftCardPaymentGroups > 0) {

      for (GiftCardPaymentInfo currentGiftCardPaymentInfo : giftCardPaymentInfos) {
        currentGiftCardPaymentGroup = fetchGiftCardPaymentGroup(currentGiftCardPaymentInfo.getGiftCardNumber());

        vlogDebug("Processing the next GiftCardPaymentInfo: {0}", currentGiftCardPaymentInfo);

        // If FoundInSystem is false, that means we weren't able to get a Balance Inquiry for the Gift Card:
        if (!currentGiftCardPaymentInfo.isTransactionSuccess()) {
           vlogDebug("Skipping over current Gift Card because its FoundInSystem is FALSE.  Was not able to successful Balance Inquiry: {0}", currentGiftCardPaymentInfo);
          continue; // move on to the next credit payment
        }

        // if the order is accounted for, no need to check the rest of the Gift Card Payments...stop looping thru them.
        // For these Gift Cards, the Payment Group will remain on the order, with an Amount of 0, and no relationship will exist for them.
        if (pTotalAmountUnaccountedFor == 0.0) {
          vlogDebug("The order is accounted for.  No need to proceed...");
          break; // stop looping thru the GiftCardPaymentInfos
        }

        // Most GiftCardPaymentInfos have a GiftCard payment group associated with it. This one does not.....
        if (currentGiftCardPaymentGroup == null) {
           vlogDebug("This Gift Card does NOT have a Payment Group - skipping over it...");
        } else {
          // Update the Gift Card Payment group with new amounts.
          amountPutOnThisGiftCard = updateGiftCard(currentOrder, currentGiftCardPaymentGroup, currentGiftCardPaymentInfo, pTotalAmountUnaccountedFor);
          vlogDebug("Amount Put on This Card is :{0}", amountPutOnThisGiftCard);
          pTotalAmountUnaccountedFor -= amountPutOnThisGiftCard;
          vlogDebug("After calling updateGiftCard, totalAmountUnaccountedFor=" + pTotalAmountUnaccountedFor);
          if (pTotalAmountUnaccountedFor == 0.0) {
            break;
          }
        }
      }

      if (pTotalAmountUnaccountedFor > 0.0) {
        vlogDebug("We have cycled thru the Gift Cards, and the order isn't accounted for.  Have to assign the rest to a Credit Card payment group...");
        setAmountRequiredForCreditCard(pTotalAmountUnaccountedFor);
      }
      else {
        setAmountRequiredForCreditCard(0.0);
      }

    }
    try {
      getOrderManager().updateOrder(getOrder(), "After updating gift card amounts/relationships");

    } catch (Exception exc) {
      if (isLoggingError()) {
        logError("4-Exception caught in updateGiftCardPaymentGroups when updatingOrder after updating Gift Cards:" + exc + ", For order:"
            + currentOrder.getId());
      }
      throw new MFFOrderUpdateException("An error was encountered while updating your order.  Please try again.", exc);
    }
    return pTotalAmountUnaccountedFor;
  }
  
  @SuppressWarnings("unchecked")
  public HardgoodShippingGroup getShippingGroup() {

    if (mShippingGroup == null) {
      Order currentOrder = getOrder();
      List<ShippingGroup> shippingGroups = ((List<ShippingGroup>) currentOrder.getShippingGroups());
      if (shippingGroups != null) {
        if (shippingGroups.isEmpty()) {
          return null;
        }

        if (shippingGroups.size() > 1) {
          Iterator<?> sgiter = shippingGroups.iterator();
          while (sgiter.hasNext()) {
            ShippingGroup sg = (ShippingGroup) sgiter.next();
            
            if (sg instanceof HardgoodShippingGroup) {
              mShippingGroup = (HardgoodShippingGroup) sg;
              return mShippingGroup;
            }
          }
        }

        if (shippingGroups.size() == 1) {
          if (shippingGroups.get(0) instanceof HardgoodShippingGroup) {
            mShippingGroup = (HardgoodShippingGroup) shippingGroups.get(0);
            return mShippingGroup;
          }
        }
      }
    } else {
      return mShippingGroup;
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  private void sortPaymentGroups() throws MFFOrderUpdateException {
    List<PaymentGroup> oldPaymentGroups = getOrder().getPaymentGroups();

    // put credit card first
    List<PaymentGroup> newPaymentGroups = new ArrayList<PaymentGroup>();
    for (PaymentGroup oldPaymentGroup : oldPaymentGroups) {
      if (oldPaymentGroup instanceof AciCreditCard) {
        newPaymentGroups.add(oldPaymentGroup);
      }
    }

    // put all gift cards in a new List and sort it
    List<PaymentGroup> newGiftCardPaymentGroups = new ArrayList<PaymentGroup>();
    for (PaymentGroup oldPaymentGroup : oldPaymentGroups) {
      if (oldPaymentGroup instanceof MFFGiftCardPaymentGroup) {
        newGiftCardPaymentGroups.add(oldPaymentGroup);
      }
    }
    Collections.sort(newGiftCardPaymentGroups, mPaymentGroupComparator);
    newPaymentGroups.addAll(newGiftCardPaymentGroups);

    // update the Order's payment groups List
    try {
      getOrder().removeAllPaymentGroups();
      for (PaymentGroup newPaymentGroup : newPaymentGroups) {
        getOrder().addPaymentGroup(newPaymentGroup);
      }
      getOrderManager().updateOrder(getOrder(), "After sorting payment groups");
    }
    catch (Exception exc) {
      vlogError("Exception caught in updatePaymentGroups when updatingOrder after sorting Gift Cards: " + exc + ", order: "
            + getOrder().getId());
      throw new MFFOrderUpdateException("An error was encountered while updating your order. Please try again. ", exc);
    }
  }

  
  private double updateGiftCard(Order pCurrentOrder, PaymentGroup pCurrentGiftCardPaymentGroup, GiftCardPaymentInfo pCurrentGiftCardPaymentInfo,
      double pTotalAmountUnaccountedFor) throws MFFOrderUpdateException {

    vlogDebug("Start of updateGiftCard - for Gift Card Payment Group:" + pCurrentGiftCardPaymentGroup.getId() + ", amountUnaccountedForYet:"
          + pTotalAmountUnaccountedFor);

    double amountAvailableOnThisCard = 0.0;
    double amountPutOnThisCard = 0.0;

    vlogDebug("This GiftCardPaymentInfo has a Payment Group: " + pCurrentGiftCardPaymentGroup.getId());

    amountAvailableOnThisCard = pCurrentGiftCardPaymentInfo.getAmountOnCard();
    vlogDebug("Amount Available on this card:{0}", amountAvailableOnThisCard);
    
    if (pTotalAmountUnaccountedFor <= amountAvailableOnThisCard) {
      giftCardHasMoreThanEnough(pCurrentOrder, pCurrentGiftCardPaymentGroup, pCurrentGiftCardPaymentInfo, pTotalAmountUnaccountedFor,
          amountAvailableOnThisCard);
      amountPutOnThisCard = pTotalAmountUnaccountedFor;
    } else {
      giftCardUseWholeAmount(pCurrentOrder, pCurrentGiftCardPaymentGroup, pCurrentGiftCardPaymentInfo, pTotalAmountUnaccountedFor, amountAvailableOnThisCard);
      amountPutOnThisCard = amountAvailableOnThisCard;
    }
    
    return amountPutOnThisCard;
  }
  
  private void giftCardHasMoreThanEnough(Order pCurrentOrder, PaymentGroup pCurrentGiftCardPaymentGroup,
      GiftCardPaymentInfo pCurrentGiftCardPaymentInfo, double pTotalAmountUnaccountedFor, double pAmountAvailableOnThisCard)
  throws MFFOrderUpdateException {
    vlogDebug("GiftCardHasMoreThanEnough - Entering");
    double amountPutOnThisCard = pTotalAmountUnaccountedFor;

    // set the amount we need to use on the Payment Group:
    pCurrentGiftCardPaymentGroup.setAmount(amountPutOnThisCard);
    if (pCurrentOrder == null || pCurrentOrder.getPriceInfo() == null) {
      if (isLoggingError()) {
        logError("Cannot update the OrderPriceInfo.GiftCardTotal because Order is null, or Order.OrderPriceInfo is null!");
      }
    } else {
      double newAmount = ((MFFOrderPriceInfo) pCurrentOrder.getPriceInfo()).getGiftCardPaymentTotal();
      newAmount += amountPutOnThisCard;
      ((MFFOrderPriceInfo) pCurrentOrder.getPriceInfo()).setGiftCardPaymentTotal(newAmount);
    }
    vlogDebug("GiftCardHasMoreThanEnough: Amount to put on this card:{0}",amountPutOnThisCard);
    
    try {
      getOrderManager().addOrderAmountToPaymentGroup(pCurrentOrder, pCurrentGiftCardPaymentGroup.getId(), amountPutOnThisCard);
    }
    catch (Exception exc) {
      if (isLoggingError()) {
        logError("Exception caught when trying to ADD an Order Amount relationship for Gift Card Payment Group:" + exc + ", For order:"
            + pCurrentOrder.getId() + ", Gift Card Payment Group:" + pCurrentGiftCardPaymentGroup.getId());
      }
      throw new MFFOrderUpdateException("An error was encountered while updating your order.  Please try again.", exc);

    }
    pCurrentGiftCardPaymentInfo.setAmountAssignedToThisOrder(amountPutOnThisCard);
    pCurrentGiftCardPaymentInfo.setAmountRemainingToSpend(pAmountAvailableOnThisCard - amountPutOnThisCard);
    
    vlogDebug("Amount Assigned To This Order:{0}, Amount Remaining To Spend:{1}", pCurrentGiftCardPaymentInfo.getAmountAssignedToThisOrder(),pCurrentGiftCardPaymentInfo.getAmountRemainingToSpend());
    
    vlogDebug("GiftCardHasMoreThanEnough - Entering");
  }

  /** This method is called when you want to assign this Gift Card to this order, and this Gift Card doesn't have enough balance to cover the rest of the order.
   */
  private void giftCardUseWholeAmount(Order pCurrentOrder, PaymentGroup pCurrentGiftCardPaymentGroup, GiftCardPaymentInfo pCurrentGiftCardPaymentInfo,
      double pTotalAmountUnaccountedFor, double pAmountAvailableOnThisCard) throws MFFOrderUpdateException {
    vlogDebug("giftCardUseWholeAmount - Entering");
    double amountPutOnThisCard = pAmountAvailableOnThisCard;

    pCurrentGiftCardPaymentGroup.setAmount(amountPutOnThisCard);
    if (pCurrentOrder == null || pCurrentOrder.getPriceInfo() == null) {
      if (isLoggingError()) {
        logError("Cannot update the OrderPriceInfo.GiftCardTotal because Order is null, or Order.OrderPriceInfo is null!");
      }
    } else {
      double newAmount = ((MFFOrderPriceInfo) pCurrentOrder.getPriceInfo()).getGiftCardPaymentTotal();
      newAmount += amountPutOnThisCard;
      ((MFFOrderPriceInfo) pCurrentOrder.getPriceInfo()).setGiftCardPaymentTotal(newAmount);
    }
    
    // create an Order Amount relationship for the Gift Card Payment Group.
    try {
      getOrderManager().addOrderAmountToPaymentGroup(pCurrentOrder, pCurrentGiftCardPaymentGroup.getId(), amountPutOnThisCard);
    } catch (Exception exc) {
      if (isLoggingError()) {
        logError("Exception caught when trying to ADD an Order Amount relationship for Gift Card Payment Group:" + exc + ", For order:"
            + pCurrentOrder.getId() + ", Gift Card Payment Group:" + pCurrentGiftCardPaymentGroup.getId());
      }
      throw new MFFOrderUpdateException("An error was encountered while updating your order.  Please try again.", exc);
    }

    pCurrentGiftCardPaymentInfo.setAmountAssignedToThisOrder(amountPutOnThisCard);
    pCurrentGiftCardPaymentInfo.setAmountRemainingToSpend(0.0);

    pTotalAmountUnaccountedFor -= amountPutOnThisCard;
    
    vlogDebug("giftCardUseWholeAmount - Exiting");
  }


  private boolean updateCreditCardPaymentGroup(double pTotalAmountUnaccountedFor) throws MFFOrderUpdateException {
    vlogDebug("UpdateCreditCardPaymentGroup - Entering");
    Order currentOrder = getOrder();
    PaymentGroup currentCreditCardPaymentGroup = getCreditCardPaymentGroup();

    synchronized (currentOrder) {

      if (getCreditCardPaymentGroup() != null && getOrder().getPaymentGroupCount() == 0) {
        setCreditCardPaymentGroup(null);
      }

      if (pTotalAmountUnaccountedFor > 0.0) {

        if (currentCreditCardPaymentGroup == null) {
          createCreditCardPaymentGroupAndFillIn(currentOrder, pTotalAmountUnaccountedFor);
        } else {
          updateCreditCardPaymentGroupWithNewAmounts(currentOrder, currentCreditCardPaymentGroup, pTotalAmountUnaccountedFor);

        }
        vlogDebug("UpdateCreditCardPaymentGroup - Exiting");
        return true;
      } else {
        if (currentCreditCardPaymentGroup == null) {
          if (isLoggingDebug()) {
            logDebug("We don't need a credit card payment group and we don't have one already.  No need to create or delete anything! :-)");

          }
        } else {
          removeCreditCardPaymentGroupNoLongerNeeded(currentOrder, currentCreditCardPaymentGroup);
        }
        setAmountRequiredForCreditCard(0.0);
        vlogDebug("UpdateCreditCardPaymentGroup - Exiting");
        return false;
      }

    }
  }

  private void createCreditCardPaymentGroupAndFillIn(Order pCurrentOrder, double pTotalAmountUnaccountedFor) throws MFFOrderUpdateException {
    vlogDebug("CreateCreditCardPaymentGroupAndFillIn - Entering");
    try {
      CreditCard newCC = (CreditCard) getPaymentGroupManager().createPaymentGroup("creditCard");

      setCreditCardPaymentGroup(newCC);
      newCC.setAmount(pTotalAmountUnaccountedFor);

      setAmountRequiredForCreditCard(pTotalAmountUnaccountedFor);
      getPaymentGroupManager().addPaymentGroupToOrder(pCurrentOrder, newCC);
      getOrderManager().addRemainingOrderAmountToPaymentGroup(getOrder(), newCC.getId());
      getOrderManager().updateOrder(getOrder(), "After creating a CC Payment Group and assigned amount");

    }
    catch (Exception exc) {
      if (isLoggingError()) {
        logError("Errors encountered with creating CC payment group and remaining relationship:" + exc);
      }
      throw new MFFOrderUpdateException("An error was encountered while updating your order.  Please try again.", exc);
    }
    vlogDebug("CreateCreditCardPaymentGroupAndFillIn - Exiting");
  }

  private void updateCreditCardPaymentGroupWithNewAmounts(Order pCurrentOrder, PaymentGroup pCurrentCreditCardPaymentGroup,
      double pTotalAmountUnaccountedFor) throws MFFOrderUpdateException {
    vlogDebug("UpdateCreditCardPaymentGroupWithNewAmounts - Entering");
    if (pCurrentCreditCardPaymentGroup.getAmount() == pTotalAmountUnaccountedFor) {
      setAmountRequiredForCreditCard(pTotalAmountUnaccountedFor);
    } else {
      // update the variable that tells the user the amount we'll have to charge to their credit card.
      setAmountRequiredForCreditCard(pTotalAmountUnaccountedFor);
      try {
        pCurrentCreditCardPaymentGroup.setAmount(pTotalAmountUnaccountedFor);
        getOrderManager().addRemainingOrderAmountToPaymentGroup(pCurrentOrder, pCurrentCreditCardPaymentGroup.getId());
        getOrderManager().updateOrder(getOrder(), "After modifying amount on existing Credit Card");

      } catch (Exception exc) {
        throw new MFFOrderUpdateException("An error was encountered while updating your order.  Please try again.", exc);
      }
    }
    vlogDebug("UpdateCreditCardPaymentGroupWithNewAmounts - Exiting");
  }

  public void removeCreditCardPaymentGroupNoLongerNeeded(Order pCurrentOrder, PaymentGroup pCurrentCreditCardPaymentGroup)
  throws MFFOrderUpdateException {

    try {
      getPaymentGroupManager().removeAllRelationshipsFromPaymentGroup(pCurrentOrder, pCurrentCreditCardPaymentGroup.getId());
      getPaymentGroupManager().removePaymentGroupFromOrder(pCurrentOrder, pCurrentCreditCardPaymentGroup.getId());

      getOrderManager().updateOrder(getOrder(), "After removing the Credit Card we no longer need");

      setCreditCardPaymentGroup(null);

      setAmountRequiredForCreditCard(0.0);

    } catch (Exception exc) {
      if (isLoggingError()) {
        logError("Unable to delete the credit card payment group.  What is going on???:" + exc);
      }
      throw new MFFOrderUpdateException("An error was encountered while updating your order.  Please try again.", exc);
    }
  }

  
  private PaymentGroupComparator mPaymentGroupComparator = new PaymentGroupComparator();

  class GiftCardPaymentInfoComparator implements Comparator<GiftCardPaymentInfo> {    
    public int compare(GiftCardPaymentInfo pGcpi1, GiftCardPaymentInfo pGcpi2) {
      
      if (pGcpi1.getGiftCardNumber() == pGcpi2.getGiftCardNumber()) {
        return 0;
      }

      if (pGcpi1.getAmountOnCard() == pGcpi2.getAmountOnCard()) {
        return 0;
      }
      if (pGcpi1.getAmountOnCard() > pGcpi2.getAmountOnCard()) {
        return 1;
      }
      return -1;
    }
  }

  class PaymentGroupComparator implements Comparator<PaymentGroup> {    
    public int compare(PaymentGroup pPg1, PaymentGroup pPg2) {
      MFFGiftCardPaymentGroup gc1 = (MFFGiftCardPaymentGroup)pPg1;
      MFFGiftCardPaymentGroup gc2 = (MFFGiftCardPaymentGroup)pPg2;
      
      if (gc1.getCardNumber() == gc2.getCardNumber()) {
        return 0;
      }
      
      if (pPg1.getAmount() == pPg2.getAmount()) {
        return 0;
      }
      if (pPg1.getAmount() > pPg2.getAmount()) {
        return 1;
      }
      return -1;
    }
  }

  private int clearOutAmountsAndRelationshipsInGiftCardPaymentGroups(Order pCurrentOrder) {

    Collection<GiftCardPaymentInfo> giftCardPaymentInfos = getGiftCardPaymentInfos().values();
    GiftCardPaymentInfo currentGiftCardPaymentInfo = null;
    PaymentGroup currentGiftCardPaymentGroup = null;
    int numGiftCardPaymentGroups = 0;

    for (Iterator<GiftCardPaymentInfo> i$ = giftCardPaymentInfos.iterator(); i$.hasNext();) {
      currentGiftCardPaymentInfo = (GiftCardPaymentInfo) i$.next();
      currentGiftCardPaymentGroup = fetchGiftCardPaymentGroup(currentGiftCardPaymentInfo.getGiftCardNumber());
      if (currentGiftCardPaymentGroup != null) {
        numGiftCardPaymentGroups++;

        currentGiftCardPaymentGroup.setAmount(0.0); // reset all to 0:
        try {
          getPaymentGroupManager().removeAllRelationshipsFromPaymentGroup(pCurrentOrder, currentGiftCardPaymentGroup.getId());
        }
        catch (CommerceException exc) {
          if (isLoggingError()) {
            logError("Exception caught when trying to REMOVE an Order Amount relationship from a Gift Card Payment Group:" + exc + ", For order:"
                + pCurrentOrder.getId());
          }
        }
      }
    }
    return numGiftCardPaymentGroups;
  }
  
  public double getTotalOrderPrice() {
    if (getOrder() == null) {
      return 0.0;
    } else {
      if (getOrder().getPriceInfo() == null) {
        return 0.0;
      } else {
        return getPricingTools().round(getOrder().getPriceInfo().getTotal());
      }
    }
  }

  @SuppressWarnings("rawtypes")
  public MFFGiftCardPaymentGroup fetchGiftCardPaymentGroup(String pGiftCardNumber) {
    if (StringUtils.isBlank(pGiftCardNumber)) {
      return null;
    }
    List paymentGroups = getOrder().getPaymentGroups();
    if (paymentGroups != null) {
      Iterator iter = paymentGroups.iterator();
      while (iter.hasNext()) {
        Object pg = iter.next();
        if (pg instanceof MFFGiftCardPaymentGroup) {
          MFFGiftCardPaymentGroup giftCard = (MFFGiftCardPaymentGroup) pg;
          if (pGiftCardNumber.equals(giftCard.getCardNumber())) {
            return giftCard;
          }
        }
      }
    }
    return null;
  }
  
  LinkedHashMap<String, GiftCardPaymentInfo> mGiftCardPaymentInfos = null;

  public void setGiftCardPaymentInfos(LinkedHashMap<String, GiftCardPaymentInfo> pNewList) {
    mGiftCardPaymentInfos = pNewList;
  }
  
  @SuppressWarnings("unchecked")
  public LinkedHashMap<String, GiftCardPaymentInfo> getGiftCardPaymentInfos() {
    if (mGiftCardPaymentInfos == null) {

      mGiftCardPaymentInfos = new LinkedHashMap<String, GiftCardPaymentInfo>(); // never return a null

      List<PaymentGroup> paymentGroupList = (List<PaymentGroup>) getOrder().getPaymentGroups();
      List<String> removalPgIds = new ArrayList<String>();
      
      if (paymentGroupList != null) {
        
        for (int i = 0; i < paymentGroupList.size(); i++) {
          PaymentGroup pg = (PaymentGroup) paymentGroupList.get(i);
          if (pg instanceof MFFGiftCardPaymentGroup)
            removalPgIds.add(pg.getId());
        }

        if(removalPgIds.size() > 0){
          // Go thru all Payment Groups - if Gift Card, remove:
          for (int i = 0; i < removalPgIds.size(); i++) {
              // Before making any changes to the Order or Payment Group, synchronize on the Order:
              synchronized (getOrder()) {
                try {
                  getPaymentGroupManager().removeAllRelationshipsFromPaymentGroup(getOrder(), removalPgIds.get(i));
                  getPaymentGroupManager().removePaymentGroupFromOrder(getOrder(), removalPgIds.get(i));
                  // update the order:
                  getOrderManager().updateOrder(getOrder(), "Remove GC Payment Group from Previous Session");
                }
                catch (Exception exc) {
                  if (isLoggingError()) {
                    logError("Exception caught while removing a Gift Card from order:" + exc);
                  }
                }
              }
  
          }
        }
      }
    }

    return mGiftCardPaymentInfos;

  }
  
  public void clearOutCreditCardData() throws MFFOrderUpdateException {

    synchronized (getOrder()) {
    	if(((MFFOrderImpl)getOrder()).getStateAsString().trim().equalsIgnoreCase("INCOMPLETE")){
		      try {
		        CreditCard ccpg = getCreditCardPaymentGroup();
		        if (ccpg != null) {
		          clearStoredCreditCardValues(ccpg);
		        }
		        setCreditCardPaymentGroup(null);
		      }
		      catch (Exception exc) {
		        if (isLoggingError()) {
		          logError("There was a problem in clearOutCreditCardData:" + exc);
		        }
		        throw new MFFOrderUpdateException("An error was encountered while updating your order.  Please try again.", exc);
		      }
		    }
    	}
  }
  
  private void clearStoredCreditCardValues(CreditCard ccpg) {
    ccpg.setCreditCardNumber(null);
    ccpg.setCreditCardType(null);
    ccpg.setExpirationMonth(null);
    ccpg.setExpirationYear(null);
    ccpg.setCardVerificationNumber(null);
    ccpg.setAmount(0.0);
    ((AciCreditCard)ccpg).setTokenNumber(null);
    ((AciCreditCard)ccpg).setNameOnCard(null);
    ((AciCreditCard)ccpg).setMopTypeCode(null);
    ContactInfo billAddr = (ContactInfo) ccpg.getBillingAddress();
    if (billAddr != null) {
      billAddr.setFirstName(null);
      billAddr.setLastName(null);
      billAddr.setAddress1(null);
      billAddr.setAddress2(null);
      billAddr.setCity(null);
      billAddr.setState(null);
      billAddr.setCountry(null);
      billAddr.setPhoneNumber(null);
      billAddr.setPostalCode(null);
      ccpg.setBillingAddress(billAddr);
    }
  }
  
  public boolean repriceOrder(String pPricingOperation, Locale pUserLocale) throws ServletException, IOException {

    try {
      PricingModelHolder pricingModelHolder = getUserPricingModels();
      if (pricingModelHolder != null) {
        pricingModelHolder.initializePricingModels();

        getPricingTools().performPricingOperation(pPricingOperation, getOrder(), pricingModelHolder, pUserLocale, pricingModelHolder.getProfile(), null);
        return true;
      }
    }
    catch (PricingException exc) {
      if (isLoggingError()) {
        logError("An exception was caught in CheckoutManager.  Exc:" + exc);
      }
      return false;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public List<MFFGiftCardPaymentGroup> getGiftCardPaymentGroups() {

    List<PaymentGroup> paymentGroups = ((List<PaymentGroup>) getOrder().getPaymentGroups());
    if (paymentGroups == null) {
      return null;
    }

    List<MFFGiftCardPaymentGroup> giftCardPaymentGroups = new ArrayList<MFFGiftCardPaymentGroup>();
    Iterator<PaymentGroup> payGroupIter = paymentGroups.iterator();
    while (payGroupIter.hasNext()) {
      PaymentGroup paymentGroup = (PaymentGroup) payGroupIter.next();
      vlogDebug("PaymentGroup.PaymentMethod:" + paymentGroup.getPaymentMethod());
      if (paymentGroup instanceof MFFGiftCardPaymentGroup) {
        vlogDebug("Adding this PaymentGroup to giftCardPaymentGroupList:" + paymentGroup);
        giftCardPaymentGroups.add((MFFGiftCardPaymentGroup) paymentGroup);
      }
    }
    return giftCardPaymentGroups;
  }
  
  public boolean isOrderTotalCovered() {
    return (getAmountRequiredForCreditCard() <= 0.0);
  }
  
  public boolean isOrderRequiresCreditCard() {
    return !isOrderTotalCovered();
  }
  
  public double getTotalAmountAppliedToGiftCards() {
    if (getGiftCardPaymentInfos() == null) {
      return 0.0d;
    }
    double runningTotal = 0.0d;
    for (GiftCardPaymentInfo info : getGiftCardPaymentInfos().values()) {
      runningTotal += info.getAmountAssignedToThisOrder();
    }
    
    return getPricingTools().round(runningTotal);
  }

  public void incrementFailureCardAttempt() {
    setCardAttempt(getCardAttempt() + 1);
  }
  
  public boolean isOrderHasGiftCards() {
    List<MFFGiftCardPaymentGroup> gcGroups = getGiftCardPaymentGroups();
    if (gcGroups != null && !gcGroups.isEmpty()) {
      return true;
    }
    return false;
  }
  
  @SuppressWarnings("unchecked")
  public CreditCard getCreditCardPaymentGroup() {
    if(mCreditCardPaymentGroup != null){
      if(getOrder().getPaymentGroups() != null){
        if(!getOrder().getPaymentGroups().contains(mCreditCardPaymentGroup))
          mCreditCardPaymentGroup = null;
      }     
    }

    if (mCreditCardPaymentGroup == null) {
      List<PaymentGroup> list = getOrder().getPaymentGroups();

      if (list != null) {
        for (int i = 0; i < list.size(); i++) {
          PaymentGroup pg = (PaymentGroup) list.get(i);

          if (pg instanceof CreditCard) {
            mCreditCardPaymentGroup = (CreditCard) pg;
            break;
          }
        }
      }
    }

    return mCreditCardPaymentGroup;
  }


  public boolean isOrderHasCreditCard() {
    if (getCreditCardPaymentGroup() != null) {
      return true;
    }
    return false;
  }

	/**
	 * @return the mCartSuccessMessage
	 */
	public String getCartSuccessMessage() {
		return mCartSuccessMessage;
	}

	/**
	 * @param pCartSuccessMessage the mCartSuccessMessage to set
	 */
	public void setCartSuccessMessage(String pCartSuccessMessage) {
		this.mCartSuccessMessage = pCartSuccessMessage;
	}

	/**
	 * @return the mCartErrorMessage
	 */
	public String getCartErrorMessage() {
		return mCartErrorMessage;
	}

	/**
	 * @param pCartErrorMessage the mCartErrorMessage to set
	 */
	public void setCartErrorMessage(String pCartErrorMessage) {
		this.mCartErrorMessage = pCartErrorMessage;
	}
	
	/**
	 * @return the mShippingMethod
	 */
	public String getShippingMethod() {
		return mShippingMethod;
	}

	/**
	 * @param pShippingMethod the mShippingMethod to set
	 */
	public void setShippingMethod(String pShippingMethod) {
		this.mShippingMethod = pShippingMethod;
	}

	/**
	 * @return the mShoppingCart
	 */
	public OrderHolder getShoppingCart() {
		return mShoppingCart;
	}

	/**
	 * @param pShoppingCart the mShoppingCart to set
	 */
	public void setShoppingCart(OrderHolder pShoppingCart) {
		this.mShoppingCart = pShoppingCart;
	}
	
	public Order getOrder() {
		return getShoppingCart().getCurrent();
	}

	/**
	 * @return the mShippingAddressId
	 */
	public String getShippingAddressId() {
			return mShippingAddressId;
	}

	/**
	 * @param pShippingAddressId the mShippingAddressId to set
	 */
	public void setShippingAddressId(String pShippingAddressId) {
		this.mShippingAddressId = pShippingAddressId;
	}

	/**
	 * @return the mShippingAddressNickName
	 */
	public String getShippingAddressNickName() {
		return mShippingAddressNickName;
	}

	/**
	 * @param pShippingAddressNickName the mShippingAddressNickName to set
	 */
	public void setShippingAddressNickName(String pShippingAddressNickName) {
		this.mShippingAddressNickName = pShippingAddressNickName;
	}

	/**
	 * @return the mSaveShippingAddress
	 */
	public boolean isSaveShippingAddress() {
		return mSaveShippingAddress;
	}

	/**
	 * @param pSaveShippingAddress the mSaveShippingAddress to set
	 */
	public void setSaveShippingAddress(boolean pSaveShippingAddress) {
		this.mSaveShippingAddress = pSaveShippingAddress;
	}

	/**
	 * @return the mBillingAddressSameAsShipping
	 */
	public boolean isBillingAddressSameAsShipping() {
		return mBillingAddressSameAsShipping;
	}

	/**
	 * @param pBillingAddressSameAsShipping the mBillingAddressSameAsShipping to set
	 */
	public void setBillingAddressSameAsShipping(boolean pBillingAddressSameAsShipping) {
		this.mBillingAddressSameAsShipping = pBillingAddressSameAsShipping;
	}

	/**
	 * @return the mShippingAddressInForm
	 */
	public ContactInfo getShippingAddressInForm() {
		return mShippingAddressInForm;
	}

	/**
	 * @param pShippingAddressInForm the mShippingAddressInForm to set
	 */
	public void setShippingAddressInForm(ContactInfo pShippingAddressInForm) {
		this.mShippingAddressInForm = pShippingAddressInForm;
	}

	/**
	 * @return the mSavePaymentMethod
	 */
	public boolean isSavePaymentMethod() {
		return mSavePaymentMethod;
	}

	/**
	 * @param pSavePayment the mSavePayment to set
	 */
	public void setSavePaymentMethod(boolean pSavePaymentMethod) {
		this.mSavePaymentMethod = pSavePaymentMethod;
	}

	/**
	 * @return the mCreditCardId
	 */
	public String getCreditCardId() {
		return mCreditCardId;
	}

	/**
	 * @param pCreditCardId the mCreditCardId to set
	 */
	public void setCreditCardId(String pCreditCardId) {
		this.mCreditCardId = pCreditCardId;
	}

	/**
	 * @return the mBillingAddressInForm
	 */
	public ContactInfo getBillingAddressInForm() {
		return mBillingAddressInForm;
	}

	/**
	 * @param pBillingAddressInForm the mBillingAddressInForm to set
	 */
	public void setBillingAddressInForm(ContactInfo pBillingAddressInForm) {
		this.mBillingAddressInForm = pBillingAddressInForm;
	}

	/**
	 * @return the mShippingAvsVerified
	 */
	public boolean isShippingAvsVerified() {
		return mShippingAvsVerified;
	}

	/**
	 * @param pShippingAvsVerified the mShippingAvsVerified to set
	 */
	public void setShippingAvsVerified(boolean pShippingAvsVerified) {
		this.mShippingAvsVerified = pShippingAvsVerified;
	}

	/**
	 * @return the mBillingAvsVerified
	 */
	public boolean isBillingAvsVerified() {
		return mBillingAvsVerified;
	}

	/**
	 * @param pBillingAvsVerified the mBillingAvsVerified to set
	 */
	public void setBillingAvsVerified(boolean pBillingAvsVerified) {
		this.mBillingAvsVerified = pBillingAvsVerified;
	}

	/**
	 * @return the mSelectedBillingAddress
	 */
	public RepositoryItem getSelectedBillingAddress() {
		return mSelectedBillingAddress;
	}

	/**
	 * @param mSelectedBillingAddress the mSelectedBillingAddress to set
	 */
	public void setSelectedBillingAddress(RepositoryItem pSelectedBillingAddress) {
		this.mSelectedBillingAddress = pSelectedBillingAddress;
	}


	/**
	 * @return the mCouponMessages
	 */
	public Map<String, String> getCouponMessages() {
		return mCouponMessages;
	}

	/**
	 * @param pCouponMessages the mCouponMessages to set
	 */
	public void setCouponMessages(Map<String, String> pCouponMessages) {
		if (mCouponMessages == null) mCouponMessages = new HashMap<String, String>();
		this.mCouponMessages = pCouponMessages;
	}

   public void setClearCouponMessages(boolean mClear){
	   getCouponMessages().clear();
   }

	/**
	 * @return the mCardAuthorized
	 */
	public boolean isCardAuthorized() {
		return mCardAuthorized;
	}

	/**
	 * @param pCardAuthorized the mCardAuthorized to set
	 */
	public void setCardAuthorized(boolean pCardAuthorized) {
		this.mCardAuthorized = pCardAuthorized;
	}

	/**
	 * @return the mPaymentErrorMessage
	 */
	public String getPaymentErrorMessage() {
		return mPaymentErrorMessage;
	}

	/**
	 * @param pPaymentErrorMessage the mPaymentErrorMessage to set
	 */
	public void setPaymentErrorMessage(String pPaymentErrorMessage) {
		this.mPaymentErrorMessage = pPaymentErrorMessage;
	}

	/**
	 * 
	 * @return
	 */
	public String getLoginErrorMessage() {
		return mLoginErrorMessage;
	}

	/**
	 * 
	 * @param pLoginErrorMessage
	 */
	public void setLoginErrorMessage(String pLoginErrorMessage) {
		this.mLoginErrorMessage = pLoginErrorMessage;
	}

  public FedexAddressValidationService getAvsService() {
    return mAvsService;
  }

  public void setAvsService(FedexAddressValidationService pAvsService) {
    mAvsService = pAvsService;
  }

  public AddressVerificationVO getShippingAvsVO() {
    return mShippingAvsVO;
  }

  public void setShippingAvsVO(AddressVerificationVO pShippingAvsVO) {
    mShippingAvsVO = pShippingAvsVO;
  }

  public AddressVerificationVO getBillingAvsVO() {
    return mBillingAvsVO;
  }

  public void setBillingAvsVO(AddressVerificationVO pBillingAvsVO) {
    mBillingAvsVO = pBillingAvsVO;
  }

  public boolean isBopis() {
    return ((MFFOrderImpl)getOrder()).isBopisOrder();
  }

  public void setBopis(boolean pBopis) {
    mBopis = pBopis;
  }

  public String getBopisStore() {
    return ((MFFOrderImpl)getOrder()).getBopisStore();
  }

  public void setBopisStore(String pBopisStore) {
    mBopisStore = pBopisStore;
  }

  public int getCardAttempt() {
    return mCardAttempt;
  }

  public void setCardAttempt(int pCardAttempt) {
    mCardAttempt = pCardAttempt;
  }

  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }

  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  public MFFOrderManager getOrderManager() {
    return mOrderManager;
  }

  public void setOrderManager(MFFOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  public double getAmountRequiredForCreditCard() {
    return getPricingTools().round(getTotalOrderPrice() - getTotalAmountAppliedToGiftCards());
  }

  public void setAmountRequiredForCreditCard(double pAmountRequiredForCreditCard) {
    mAmountRequiredForCreditCard = pAmountRequiredForCreditCard;
  }


  public void setCreditCardPaymentGroup(CreditCard pCreditCardPaymentGroup) {
    mCreditCardPaymentGroup = pCreditCardPaymentGroup;
  }
  
  public CheckoutStep getCheckoutStepAuthorized() {
    return mCheckoutStepAuthorized;
  }

  public void setCheckoutStepAuthorized(CheckoutStep pNewVal) {
    mCheckoutStepAuthorized = pNewVal;
  }

  public void resetCheckoutStepAuthorized() {
    mCheckoutStepAuthorized = CheckoutStep.getDefaultStep();
  }

  public void authorizeShippingStep() {
    mCheckoutStepAuthorized = CheckoutStep.SHIPPING;
  }

  public void authorizePaymentStep() {
    mCheckoutStepAuthorized = CheckoutStep.PAYMENT;
  }

  public void authorizeAccountStep() { mCheckoutStepAuthorized = CheckoutStep.ACCOUNT; }

  public void authorizeReviewStep() {
    mCheckoutStepAuthorized = CheckoutStep.REVIEW;
  }

  public void authorizeConfirmStep() {
    mCheckoutStepAuthorized = CheckoutStep.CONFIRM;
  }

  public boolean isAuthorizedForShipping() {
    if (mCheckoutStepAuthorized == CheckoutStep.SHIPPING) {
      return true;
    }
    return false;
  }

  public boolean isAuthorizedForPayment() {
    if (mCheckoutStepAuthorized == CheckoutStep.PAYMENT) {
      return true;
    }
    return false;
  }

  public boolean isAuthorizedForAccount() {
    if (mCheckoutStepAuthorized == CheckoutStep.ACCOUNT) {
      return true;
    }
    return false;
  }

  public boolean isAuthorizedForReview() {
    if (mCheckoutStepAuthorized == CheckoutStep.REVIEW) {
      return true;
    }
    return false;
  }

  public boolean isAuthorizedForConfirmation() {
    if (mCheckoutStepAuthorized == CheckoutStep.CONFIRM) {
      return true;
    }
    return false;
  }
  
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  public boolean isTestGiftCardCall() {
    return mTestGiftCardCall;
  }

  public void setTestGiftCardCall(boolean pTestGiftCardCall) {
    mTestGiftCardCall = pTestGiftCardCall;
  }

  public double getTestGiftCardAmount() {
    return mTestGiftCardAmount;
  }

  public void setTestGiftCardAmount(double pTestGiftCardAmount) {
    mTestGiftCardAmount = pTestGiftCardAmount;
  }

  public String getPaymentName() {
    return mPaymentName;
  }

  public void setPaymentName(String pPaymentName) {
    mPaymentName = pPaymentName;
  }

  public String getCartMessage() {
    return mCartMessage;
  }

  public void setCartMessage(String pCartMessage) {
    mCartMessage = pCartMessage;
  }

  public boolean isExpressCheckout() {
    return mExpressCheckout;
  }

  public void setExpressCheckout(boolean pExpressCheckout) {
    mExpressCheckout = pExpressCheckout;
  }
}
