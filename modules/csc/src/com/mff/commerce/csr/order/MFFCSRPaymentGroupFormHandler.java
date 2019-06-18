package com.mff.commerce.csr.order;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.aci.commerce.service.AciService;
import com.aci.payment.creditcard.AciCreditCard;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.pipeline.exception.AciPipelineException;
import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardInfo;
import com.google.common.base.Strings;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderTools;
import com.mff.commerce.payment.MFFGiftCardManager;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.commerce.csr.order.CSRPaymentGroupFormHandler;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.purchase.CommerceIdentifierPaymentInfo;
import atg.commerce.order.purchase.CommerceIdentifierPaymentInfoContainer;
import atg.commerce.order.purchase.PaymentGroupMapContainer;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.web.messaging.MessageConstants;

/**
 *
 */
public class MFFCSRPaymentGroupFormHandler extends CSRPaymentGroupFormHandler {

  private AciService aciService;
  private MFFGiftCardManager giftCardManager;
  private String giftCardSuccessURL;
  private String giftCardErrorURL;
  private String giftCardNumber;
  private String giftCardAccessNumber;
  private String creditCardMopType;

  private String contactEmail;

  /*
   * (non-Javadoc)
   * 
   * @see
   * atg.commerce.csr.order.CSRPaymentGroupFormHandler#preApplyPaymentGroups(atg
   * .servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
   */
  @Override
  public void preApplyPaymentGroups(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    super.preApplyPaymentGroups(pRequest, pResponse);

    // Validate number of credit cards used as payment method
    if (isOverMaxCreditCards()) {
      addFormException(new DropletException("Only 1 Credit Card is accepted at this time", "Only 1 Credit Card is accepted at this time"));
      return;
    }

    // Validate credit cards include verification number
    if (!isCreditCardWithVerNumber()) {
      addFormException(new DropletException("Please add the credit card verification number CCV", "Please add the credit card verification number CCV"));
      return;
    }


    // Update the current order's email address
    if (StringUtils.isBlank(getContactEmail())) {
      addFormException(new DropletException("Please enter a valid email address"));
    }
    
    MFFOrderImpl order = (MFFOrderImpl) getOrder();
    order.setContactEmail(getContactEmail());
    setDefaultCountryCode();

  }
  
  @SuppressWarnings("unchecked")
  protected void setDefaultCountryCode () {
    vlogDebug("Entering setDefaultCountryCode : ");
    //setting the default country if not present
    PaymentGroupMapContainer pgmc = getPaymentGroupMapContainer();
    Map<String, Object> lPaymentGroupMap = pgmc.getPaymentGroupMap();
    
    for (String lPaymentGroupName : lPaymentGroupMap.keySet()) {
      vlogDebug("Payment group name set to {0}", lPaymentGroupName);
      PaymentGroup lPaymentGroup = (PaymentGroup) lPaymentGroupMap.get(lPaymentGroupName);
      if (lPaymentGroup instanceof AciCreditCard) {
        AciCreditCard lCreditCard = (AciCreditCard) lPaymentGroup;
        Address lBillingAddress = lCreditCard.getBillingAddress();
        if (lBillingAddress != null && StringUtils.isBlank(lBillingAddress.getCountry())) {
          vlogDebug("Setting the default billing address");
          lBillingAddress.setCountry(MFFConstants.DEFAULT_COUNTRY);
        }
      }
    }
    vlogDebug("Exiting setDefaultCountryCode : ");
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void setTokenNumber() {
    
    vlogDebug("Entering setTokenNumber : ");
    RepositoryItem activeCustomer = getCSREnvironmentTools().getEnvironmentTools().getActiveCustomerProfile();
    Map<String, Object> paymentGroupMapOb = (Map<String, Object>) getPaymentGroupMapContainer().getPaymentGroupMap();
    Map savedCreditCards = (Map) activeCustomer.getPropertyValue("creditCards");
    
    if (savedCreditCards != null && savedCreditCards.size() > 0) {
      
      if (paymentGroupMapOb != null && paymentGroupMapOb.size() > 0) {
        
        for (String key : paymentGroupMapOb.keySet()) {
          
          RepositoryItem savedCreditCard = (RepositoryItem) savedCreditCards.get(key);
          if (savedCreditCard != null) {
            
            AciCreditCard creditCardPaymentGroup = (AciCreditCard) paymentGroupMapOb.get(key);
            
            creditCardPaymentGroup.setTokenNumber((String)savedCreditCard.getPropertyValue("tokenNumber"));
            creditCardPaymentGroup.setMopTypeCode((String)savedCreditCard.getPropertyValue("mopTypeCode"));
            
            vlogDebug("Found a saved credit card that matches what was entered {0}", savedCreditCard.getRepositoryId()) ;
          }
          
        } //end of the for loop
      
      } else {
        vlogWarning ("There were no payment groups created to work on");
      } //end of paymentGroupObj if block
    } else { //end of savedPaymentGroups
      vlogDebug("No credit cards stored in the profile");
    }
    vlogDebug("Exiting setTokenNumber : ");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * atg.commerce.csr.order.CSRPaymentGroupFormHandler#postApplyPaymentGroups(
   * atg.servlet.DynamoHttpServletRequest,
   * atg.servlet.DynamoHttpServletResponse)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void postApplyPaymentGroups(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    List<PaymentGroup> payGroupList = getOrder().getPaymentGroups();

    for (PaymentGroup lPaymentGroup : payGroupList) {

      // if credit card used as payment method update token
      if (null!=lPaymentGroup && lPaymentGroup instanceof AciCreditCard && lPaymentGroup.getAmount()>0.0d) {

        AciCreditCard creditCard = (AciCreditCard) lPaymentGroup;
        if (StringUtils.isBlank(creditCard.getTokenNumber()) || (
            !Strings.isNullOrEmpty(creditCard.getCreditCardNumber()) && creditCard.getCreditCardNumber().length() > 4)) {
          String lTokenNum = tokenizeCreditCard(creditCard);
  
          if (lTokenNum == null || lTokenNum.isEmpty()) {
            addFormException(new DropletException("Token Error", "Token Error"));
            return;
          }
  
          // Set the token number
          ((AciCreditCard) creditCard).setTokenNumber(lTokenNum);
  
          if (!StringUtils.isEmpty(getCreditCardMopType())) {
            ((AciCreditCard) creditCard).setMopTypeCode(getCreditCardMopType().trim().toUpperCase());
          }
  
          // Update the credit card number with the last 4 digits
          String cardNumber = creditCard.getCreditCardNumber();
          if (cardNumber.length() > 4) {
            cardNumber = cardNumber.substring(cardNumber.length() - 4);
          }
          creditCard.setCreditCardNumber(cardNumber);
        }
      }
    }
    super.postApplyPaymentGroups(pRequest, pResponse);
  }

  @SuppressWarnings("unchecked")
  public boolean isCreditCardWithVerNumber() {
    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    List<CommerceIdentifierPaymentInfo> commerceIdentifierPaymentInfos = container.getAllCommerceIdentifierPaymentInfos();
    for (CommerceIdentifierPaymentInfo cipi : commerceIdentifierPaymentInfos) {
      if (null != cipi && null != cipi.getPaymentMethod() && cipi.getAmount() > 0.0d && StringUtils.isEmpty(cipi.getCreditCardVerificationNumber())) {
        PaymentGroup payGroup = getPaymentGroup(cipi.getPaymentMethod());
        if (null != payGroup && payGroup instanceof CreditCard) {
          return false;
        }
      }
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  public boolean isOverMaxCreditCards() {
    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    List<CommerceIdentifierPaymentInfo> commerceIdentifierPaymentInfos = container.getAllCommerceIdentifierPaymentInfos();
    int numCreditCards = 0;
    for (CommerceIdentifierPaymentInfo cipi : commerceIdentifierPaymentInfos) {
      if (null != cipi && null != cipi.getPaymentMethod() && cipi.getAmount() > 0.0d && cipi.getPaymentMethod().startsWith("CreditCard")) {
        ++numCreditCards;
        if (numCreditCards > 1) {
          return true;
        }
      }
    }
    return false;
  }

  private String tokenizeCreditCard(CreditCard pCreditCard) throws ServletException, IOException {
    String tokenNumber = null;
    try {
      AciCreditCardInfo aciCardInfo = generateCreditCardInfo(pCreditCard);
      getAciService().tokenizeCreditCard(aciCardInfo);
      tokenNumber = aciCardInfo.getTokenNumber();
      String ccMopType = aciCardInfo.getMopTypeCd();
      if (!StringUtils.isEmpty(ccMopType)) {
        setCreditCardMopType(ccMopType);
      }
    } catch (AciPipelineException e) {
      vlogError("AciPipelineException during tokenizing card: ", e);
    }
    return tokenNumber;
  }

  private AciCreditCardInfo generateCreditCardInfo(CreditCard pCreditCard) {

    AciCreditCardInfo ccInfo = new AciCreditCardInfo();
    ccInfo.setCreditCardNumber(pCreditCard.getCreditCardNumber());
    ccInfo.setSecurityCode(pCreditCard.getCardVerificationNumber());
    ccInfo.setCreditCardType(pCreditCard.getCreditCardType());
    ccInfo.setExpirationMonth(pCreditCard.getExpirationMonth());
    ccInfo.setExpirationYear(pCreditCard.getExpirationYear());
    ccInfo.setOnlineOrder(true);
    ccInfo.setBillingAddress(pCreditCard.getBillingAddress());

    return ccInfo;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public boolean handleClaimGiftCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
    String myHandleMethod = "MFFCSRPaymentGroupFormHandler.handleClaimGiftCard";
    vlogDebug("Entering {0}", myHandleMethod);
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    if (rrm == null || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      try {
        Order order = getOrder();
        synchronized (order) {
          // Validate input data (gift card, ean number) similar to client side
          // validation

          // Validate gift card has not been added to order
          if (isGiftCardInOrder(order, getGiftCardNumber())) {
            addFormException(new DropletException("Gift Card " + getGiftCardNumber() + " already added to this order", "Gift Card " + getGiftCardNumber() + " already added to this order"));
            return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
          }

          // Balance Inquiry
          MFFGiftCardInfo giftCardInfo = getGiftCardManager().checkGiftCardBalance(getGiftCardNumber(),getGiftCardAccessNumber());
          
          // Validate response of balance inquiry (null, TransactionSuccess)
          if (null == giftCardInfo || !giftCardInfo.isTransactionSuccess()) {
            if (null != giftCardInfo) {
              vlogDebug("Balance Inquiry Result - GC Number {0}, TransactionSuccess {1} , BalanceAmount {2}", giftCardInfo.getGiftCardNumber(), giftCardInfo.isTransactionSuccess(), giftCardInfo.getBalanceAmount());
              vlogDebug("Balance Inquiry Result - ResponseCode {0}, ResponseCodeMsg {1} , ErrorMessage {2}", giftCardInfo.getResponseCode(), giftCardInfo.getResponseCodeMsg(), giftCardInfo.getErrorMessage());
            }
            addFormException(new DropletException("Error getting balance on Gift Card " + getGiftCardNumber(), "Error getting balance on Gift Card " + getGiftCardNumber()));
            return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
          }
          
          vlogDebug("Balance Inquiry Result - GC Number {0}, TransactionSuccess {1} , BalanceAmount {2}", giftCardInfo.getGiftCardNumber(), giftCardInfo.isTransactionSuccess(), giftCardInfo.getBalanceAmount());

          // Add the balance to a new payment group
          MFFGiftCardPaymentGroup gc = (MFFGiftCardPaymentGroup) getPaymentGroupManager().createPaymentGroup(MFFOrderTools.GIFT_CARD_PAYMENT_TYPE);
          
          
          if (giftCardInfo.getBalanceAmount() > 0.0d) {
            // gift cardnumber
            gc.setCardNumber(giftCardInfo.getGiftCardNumber());
            // ean number
            gc.setEan(getGiftCardAccessNumber());
            gc.setCurrencyCode(giftCardInfo.getCurrencyCode());
            gc.setBalanceAmount(giftCardInfo.getBalanceAmount());
          
          } else {
            addFormException(new DropletException("Gift Card has no balance", "Gift Card has no balance"));
            return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
          }
          
          double gcBal = giftCardInfo.getBalanceAmount();
          
          double amountRemaining = remainingAmountForNewGiftCard (order, gc, gcBal);
          /**
          getPaymentGroupMapContainer().addPaymentGroup(getGiftCardNumber(), gc);
          
          CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
          List commerceIdentifierPaymentInfos = container.getAllCommerceIdentifierPaymentInfos();
          CommerceIdentifierPaymentInfo cipi = (CommerceIdentifierPaymentInfo)commerceIdentifierPaymentInfos.get(0);
          
          vlogDebug("Zeroth commerce identifier {0}", cipi);
          
          double currentAmount = cipi.getAmount();
          
          int type = RelationshipTypes.stringToType(cipi.getRelationshipType());
          boolean remainingType = false;
          
          if (type == RelationshipTypes.ORDERAMOUNTREMAINING) {
            remainingType = true;
          }
          
          cipi.setSplitPaymentMethod(getGiftCardNumber());
          
          
          if (remainingType) {
            CommerceIdentifierPaymentInfo newCipi = createSpecificPaymentInfo(cipi);
            newCipi.setCommerceIdentifier(cipi.getCommerceIdentifier());
            newCipi.setRelationshipType(cipi.getAmountType());
            
            newCipi.setPaymentMethod(getGiftCardNumber());
            newCipi.setAmount(amountRemaining);
            container.addCommerceIdentifierPaymentInfo(cipi.getCommerceIdentifier().getId(), newCipi);
            //cipi.setSplitAmount(amountRemaining);
            
          } else if (currentAmount > amountRemaining) {
            if (isLoggingDebug()) logDebug((new StringBuilder()).append("splitting by amountRemaining = ").append(amountRemaining).toString());
            splitCommerceIdentifierPaymentInfoByAmount(cipi, amountRemaining);
          } else {
            if (isLoggingDebug()) logDebug((new StringBuilder()).append("splitting by currentAmount = ").append(currentAmount).toString());
            splitCommerceIdentifierPaymentInfoByAmount(cipi, currentAmount);
          }
          **/
          getPaymentGroupMapContainer().addPaymentGroup(getGiftCardNumber(), gc);
          updateCommerceIdentifierPaymentInfo(gc, amountRemaining);
          
          getMessageTools().addMessage(String.format("Gift card balance was set to %s", gc.getBalanceAmount()), MessageConstants.TYPE_INFORMATION);
          applyPaymentGroups(pRequest, pResponse);
 

          if (!getPaymentGroupManager().isPaymentGroupInOrder(order, gc.getId())) {
            vlogDebug("Adding the following payment group to the order {0}", gc.getId());
            getPaymentGroupManager().addPaymentGroupToOrder(order, gc);
          }
          try {
            getPaymentGroupManager().recalculatePaymentGroupAmounts(order);
            //getPaymentGroupManager().removeEmptyPaymentGroups(order);
            getOrderManager().updateOrder(order);
          } catch (CommerceException ce) {
            processException(ce, "errorUpdatingOrderAfterAddingGiftCard", pRequest, pResponse);
          }                   
          return checkFormRedirect(getGiftCardSuccessURL(), getGiftCardErrorURL(), pRequest, pResponse);
        }
      } finally {
        if (rrm != null) rrm.removeRequestEntry(myHandleMethod);
      }
    }

    return true;
  }
  
  /**
   * The following method returns the payment info that needs to be used to for
   * splitting.
   * 
   * @return
   */
  @SuppressWarnings({ "unchecked" })
  protected void updateCommerceIdentifierPaymentInfo(MFFGiftCardPaymentGroup giftCardPaymentGroup, double maxAllowableAmtToBeChargedOnGiftCard) {
    
    vlogDebug("Entering updateCommerceIdentifierPaymentInfo : giftCardPaymentGroup, maxAllowableAmtToBeChargedOnGiftCard");
    
    vlogDebug("gift card payment group balance amt = {0} maxAllowableAmtToBeChargedOnGiftCard {1}", giftCardPaymentGroup.getBalanceAmount(), maxAllowableAmtToBeChargedOnGiftCard);

    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    List<CommerceIdentifierPaymentInfo> commerceIdentifierPaymentInfos = (List<CommerceIdentifierPaymentInfo>) container.getAllCommerceIdentifierPaymentInfos();
    
    CommerceIdentifierPaymentInfo cipi = null;
    boolean foundRemainingAmountType = false;
    
    for (CommerceIdentifierPaymentInfo tempCipi : commerceIdentifierPaymentInfos) {
      vlogDebug("Iterating through {0}", tempCipi);
      
      int type = RelationshipTypes.stringToType(tempCipi.getRelationshipType());
      
      if (type == RelationshipTypes.ORDERAMOUNTREMAINING) {
        vlogDebug("Type set to order amount remaining");
        cipi = tempCipi;
        foundRemainingAmountType = true;
        break;
      }
      
    }
    
    if (!foundRemainingAmountType) {
      vlogDebug("We could not find any commerce identifier that is of the type remaining type");
      cipi = commerceIdentifierPaymentInfos.get(0);
    }
    
    vlogDebug("After iterating to cipi set to {0} and foundRemainingAmountType to {1}", cipi, foundRemainingAmountType);
    
    cipi.setSplitPaymentMethod(giftCardNumber);
    
    if (!foundRemainingAmountType) {
      vlogDebug("Creating a new payment info for the gift card");
      CommerceIdentifierPaymentInfo newCipi = createSpecificPaymentInfo(cipi);
      newCipi.setCommerceIdentifier(cipi.getCommerceIdentifier());
      newCipi.setRelationshipType(cipi.getAmountType());
      newCipi.setPaymentMethod(getGiftCardNumber());
      newCipi.setAmount(maxAllowableAmtToBeChargedOnGiftCard);
      container.addCommerceIdentifierPaymentInfo(cipi.getCommerceIdentifier().getId(), newCipi);
    } else {
      double amountCurrentlyAllocated = cipi.getAmount();
      if (amountCurrentlyAllocated > maxAllowableAmtToBeChargedOnGiftCard) {
        vlogDebug("Deducting the amount by {0}", maxAllowableAmtToBeChargedOnGiftCard);
        splitCommerceIdentifierPaymentInfoByAmount(cipi, maxAllowableAmtToBeChargedOnGiftCard);
      } else {
        vlogDebug("Moving the entire amount to gift card amount = {0}", amountCurrentlyAllocated);
        splitCommerceIdentifierPaymentInfoByAmount(cipi, amountCurrentlyAllocated);
      }
    }
    
    vlogDebug("Exiting updateCommerceIdentifierPaymentInfo : giftCardPaymentGroup, maxAllowableAmtToBeChargedOnGiftCard");
  }

  @SuppressWarnings("unchecked")
  protected boolean isGiftCardInOrder(Order pOrder, String pGiftCardNumber) {
    List<PaymentGroup> PaymentGroups = pOrder.getPaymentGroups();
    Iterator<PaymentGroup> lIter = PaymentGroups.iterator();
    while (lIter.hasNext()) {
      PaymentGroup lPayGroup = (PaymentGroup) lIter.next();
      if (lPayGroup.getPaymentGroupClassType().equals("giftCard")) {
        MFFGiftCardPaymentGroup gc = (MFFGiftCardPaymentGroup) lPayGroup;
        if (gc.getCardNumber().equals(pGiftCardNumber)) return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  protected double remainingAmountForNewGiftCard(Order pOrder, PaymentGroup pGiftCard, double pGiftCardBalance) {
    double ordertotal = pOrder.getPriceInfo().getTotal();
    double amtCharged = 0.0;
    List<PaymentGroup> PaymentGroups = pOrder.getPaymentGroups();
    Iterator<PaymentGroup> lIter = PaymentGroups.iterator();
    while (lIter.hasNext()) {
      PaymentGroup lPayGroup = (PaymentGroup) lIter.next();
      if (lPayGroup.getPaymentGroupClassType().equals("giftCard")) {
        amtCharged += pGiftCard.getAmount();
      }
    }
    double totalRemaining = ordertotal - amtCharged;
    
    vlogDebug("order total = {0}, amt charged = {1}, total remaining = {2}, ", ordertotal, amtCharged, totalRemaining);
    
    if (totalRemaining > pGiftCardBalance) {
      return pGiftCardBalance;
    } else {
      return totalRemaining;
    }
  }

  /**
   * @return the aciService
   */
  public AciService getAciService() {
    return aciService;
  }

  /**
   * @param pAciService
   *          the aciService to set
   */
  public void setAciService(AciService pAciService) {
    aciService = pAciService;
  }

  /**
   * @return the giftCardSuccessURL
   */
  public String getGiftCardSuccessURL() {
    return giftCardSuccessURL;
  }

  /**
   * @param giftCardSuccessURL
   *          the giftCardSuccessURL to set
   */
  public void setGiftCardSuccessURL(String giftCardSuccessURL) {
    this.giftCardSuccessURL = giftCardSuccessURL;
  }

  /**
   * @return the giftCardErrorURL
   */
  public String getGiftCardErrorURL() {
    return giftCardErrorURL;
  }

  /**
   * @param giftCardErrorURL
   *          the giftCardErrorURL to set
   */
  public void setGiftCardErrorURL(String giftCardErrorURL) {
    this.giftCardErrorURL = giftCardErrorURL;
  }

  /**
   * @return the giftCardNumber
   */
  public String getGiftCardNumber() {
    return giftCardNumber;
  }

  /**
   * @param giftCardNumber
   *          the giftCardNumber to set
   */
  public void setGiftCardNumber(String giftCardNumber) {
    this.giftCardNumber = giftCardNumber;
  }

  /**
   * @return the giftCardAccessNumber
   */
  public String getGiftCardAccessNumber() {
    return giftCardAccessNumber;
  }

  /**
   * @param giftCardAccessNumber
   *          the giftCardAccessNumber to set
   */
  public void setGiftCardAccessNumber(String giftCardAccessNumber) {
    this.giftCardAccessNumber = giftCardAccessNumber;
  }

  /**
   * @return the giftCardManager
   */
  public MFFGiftCardManager getGiftCardManager() {
    return giftCardManager;
  }

  /**
   * @param giftCardManager
   *          the giftCardManager to set
   */
  public void setGiftCardManager(MFFGiftCardManager giftCardManager) {
    this.giftCardManager = giftCardManager;
  }

  private String getCreditCardMopType() {
    return creditCardMopType;
  }

  private void setCreditCardMopType(String pCreditCardMopType) {
    creditCardMopType = pCreditCardMopType;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String pContactEmail) {
    contactEmail = pContactEmail;
  }

}
