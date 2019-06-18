package com.mff.commerce.test;

import java.util.List;

import javax.transaction.TransactionManager;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardInfo;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.service.FirstDataService;
import com.mff.commerce.payment.MFFGiftCardManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.ShippingGroup;
import atg.commerce.payment.PaymentManager;
import atg.commerce.pricing.PricingTools;
import atg.core.util.ContactInfo;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineResult;

public class TestCheckOutService extends GenericService  {

  private FirstDataService mFirstDataService;
  
  public FirstDataService getFirstDataService() {
    return mFirstDataService;
  }

  public void setFirstDataService(FirstDataService mFirstDataService) {
    this.mFirstDataService = mFirstDataService;
  }
  
  public void test1Activate()throws Exception {
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().activate(getGiftCardNumber(), 20);//25Denom account#
   
    vlogDebug("Activate--newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );

  }
  
  
  public void test2BalanceInquiry()throws Exception {
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().balanceInquiry(getGiftCardNumber());//NonDenom account#
    vlogDebug("BalanceInquiry-newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );

  }  
  
  public void test3AuthorizationAndDebit()throws Exception {
    //2408
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().authorize(getGiftCardNumber(),5, "EANDummy");
    vlogDebug("Authorization--newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );
    vlogDebug("Authorization--LocalLockId {0} - LockAmount {1} ", testResponse.getLocalLockId(),testResponse.getLockAmount() );
    //2208
    if(testResponse.getLocalLockId() != null){
      MFFGiftCardPaymentStatus testResponse2 = getFirstDataService().debit(getGiftCardNumber(),5, testResponse.getLocalLockId(), "EANDummy");
      vlogDebug("Debit--newBalance {0} - responseCode {1} ", testResponse2.getNewBalance(),testResponse2.getResponseCode() );
      vlogDebug("Debit--LocalLockId {0} - LockAmount {1} ", testResponse2.getLocalLockId(),testResponse2.getLockAmount() );      
    }else{
      System.out.println("NO LOCKID - UNABLE TO DEBIT");
    }

  }
  
  public void testGiftCardManagerBalanceInquiry()throws Exception {
    MFFGiftCardInfo testResponse = this.getGiftCardManager().checkGiftCardBalance(getGiftCardNumber(), "EANDummy");//NonDenom account#
    vlogDebug("BalanceInquiry-newBalance {0} - responseCode {1} ", testResponse.getBalanceAmount(),testResponse.getResponseCode() );
    vlogDebug("BalanceInquiry-gcNum {0} - Ean {1} ", testResponse.getGiftCardNumber(),testResponse.getEan() );

  } 
 
  public void testGiftCardManagerActivation()throws Exception {
    MFFGiftCardInfo testResponse = this.getGiftCardManager().giftCardActivation(getGiftCardNumber(), 30.00);//NonDenom account#
    vlogDebug("Activation-newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );
    vlogDebug("Activation-gcNum {0} - Ean {1} ", testResponse.getGiftCardNumber(),testResponse.getEan() );

  } 

  
  /**
   * *************************************
   * TEST CODE FOR A PROGRAMMATIC CHECKOUT
   * *************************************
   */
  OrderManager mOrderManager;

  String orderId = "o50001";

  // CreditCardService creditCardService;

  TransactionManager transactionManager;

  PaymentManager paymentManager;

  PricingTools pricingTools;

  CommerceItemManager commerceItemManager;

  PaymentGroupManager paymentGroupManager;

  String profileId = "720000";

  String catalogRefId1 = "TMPSKU-4046-57";

  String paymentGroupId = "";

  String billingEmailAddress = "test@test.com";
  // review email address test_review@test.com

  String orgId;

  String merchantId;

  String fingerprintURL;

  Order mOrder = null;

  String productId = "TMPPROD-4046";

  MFFGiftCardManager giftCardManager;
  
  public MFFGiftCardManager getGiftCardManager() {
    return giftCardManager;
  }

  public void setGiftCardManager(MFFGiftCardManager pGiftCardManager) {
    giftCardManager = pGiftCardManager;
  }

  private double mAmount;
  private String mAddress1, mAddress2, mCity, mState, mCountry, mPostalCode,
      mFirstName, mLastName, mEmail, mPaymentMethod, mPhoneNumber, mAccountNumber, mRoutingNumber, mAccountType;
  
  private int requestId;
  
  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  public void setOrderManager(OrderManager orderManager) {
    this.mOrderManager = orderManager;
  }  
 
  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public TransactionManager getTransactionManager() {
    return transactionManager;
  }

  public void setTransactionManager(TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public PaymentManager getPaymentManager() {
    return paymentManager;
  }

  public void setPaymentManager(PaymentManager paymentManager) {
    this.paymentManager = paymentManager;
  }

  public PricingTools getPricingTools() {
    return pricingTools;
  }

  public void setPricingTools(PricingTools pricingTools) {
    this.pricingTools = pricingTools;
  }

  public CommerceItemManager getCommerceItemManager() {
    return commerceItemManager;
  }

  public void setCommerceItemManager(CommerceItemManager commerceItemManager) {
    this.commerceItemManager = commerceItemManager;
  }

  public PaymentGroupManager getPaymentGroupManager() {
    return paymentGroupManager;
  }

  public void setPaymentGroupManager(PaymentGroupManager paymentGroupManager) {
    this.paymentGroupManager = paymentGroupManager;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public String getCatalogRefId1() {
    return catalogRefId1;
  }

  public void setCatalogRefId1(String catalogRefId1) {
    this.catalogRefId1 = catalogRefId1;
  }

  public String getPaymentGroupId() {
    return paymentGroupId;
  }

  public void setPaymentGroupId(String paymentGroupId) {
    this.paymentGroupId = paymentGroupId;
  }
  
  public String giftCardNumber;

  public String getGiftCardNumber() {
    return giftCardNumber;
  }

  public void setGiftCardNumber(String pGiftCardNumber) {
    giftCardNumber = pGiftCardNumber;
  }
 
  public void testActivateGiftCard()throws Exception {
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().activate(getGiftCardNumber(), 30);//25Denom account#
    vlogDebug("Activate--GiftCardNumber {0} - amount {1} ", getGiftCardNumber(),"30");
    vlogDebug("Activate--newBalance {0} - responseCode {1} ", testResponse.getNewBalance(),testResponse.getResponseCode() );

  }

  public void testCreateOrder() {
    try {
      testCreateOrder1();
      testCreateOrder2();
      //testCheckDebit();
    } catch (Exception e) {
      logError(e);
    }
  }
  
  public void testCreateOrder1() throws CommerceException {
    OrderManager om = getOrderManager();
    CommerceItemManager cm = getCommerceItemManager();
    PricingTools pt = getPricingTools();

    // String deviceFPRequest = getFingerprintURL();
    // deviceFPRequest = deviceFPRequest + "?org_id=" + getOrgId() +
    // "&session_id=" + getMerchantId();

    String profileId = "300003";//getProfileId(); // "300000";
    String catalogRefId1 = "005925227";//getCatalogRefId1(); // "0016240608";

    String productId1 = "0000000001160";//getProductId();

    long quantity1 = 1;

    // create the Order and CommerceItems. The Order has a ShippingGroup and
    // PaymentGroup
    // in it when constructed
    Order order = om.createOrder(profileId);
    CommerceItem item1 = cm.createCommerceItem(catalogRefId1, productId1,
        quantity1);
    // CommerceItem item2 = cm.createCommerceItem(catalogRefId2, productId2,
    // quantity2);

    // add the items to the Order, set the return value back to the object
    // because if an
    // item with catalogRefId already existed in the Order, the quantity is
    // incremented rather
    // than adding the new object
    item1 = cm.addItemToOrder(order, item1);
    // get the ShippingGroup and add the items to it
    ShippingGroup sg = (ShippingGroup) order.getShippingGroups().get(0);

    HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup) sg;
    hardgoodShippingGroup.setShippingAddress(constructContactInfo());

    cm.addItemQuantityToShippingGroup(order, item1.getId(), sg.getId(),
        quantity1);

    PaymentGroup payGroup = getPaymentGroupManager().createPaymentGroup(
        "giftCard");

    // String lRequestId = getProfitPointService().generateRequestId();

    if (payGroup instanceof MFFGiftCardPaymentGroup) {
      MFFGiftCardPaymentGroup lCheckPaymentGroup = (MFFGiftCardPaymentGroup) payGroup;

      try {
        MFFGiftCardInfo testGCResponse = this.getGiftCardManager().checkGiftCardBalance(getGiftCardNumber(), "3234334");
        // lProfitPointPayGroup.setRequestId(lRequestId);
        if(testGCResponse.isTransactionSuccess() && testGCResponse.getBalanceAmount() > 0.0){
          lCheckPaymentGroup.setBalanceAmount(testGCResponse.getBalanceAmount());
          lCheckPaymentGroup.setCardNumber(testGCResponse.getGiftCardNumber());
          lCheckPaymentGroup.setEan(testGCResponse.getEan());
          //lCheckPaymentGroup.setBillingAddress(constructContactInfo());          
        }else{
          System.out.println("GIFT CARD RESPONSE CODE: " + testGCResponse.getResponseCode() );
          System.out.println("GIFT CARD RESPONSE CODE: " + testGCResponse.getBalanceAmount() );

        }


        vlogInfo("TestCheckService: Payment group found and values set to \n Amount: "
            + lCheckPaymentGroup.getAmount()
            + " \n Account Number: "
            + lCheckPaymentGroup.getCardNumber());



        getPaymentGroupManager().addPaymentGroupToOrder(order,
            lCheckPaymentGroup);
        om.addRemainingOrderAmountToPaymentGroup(order,
            lCheckPaymentGroup.getId());

        setPaymentGroupId(lCheckPaymentGroup.getId());

        // Compute the prices using pricing tools
        pt.priceOrderTotal(order);
        // order.
        mOrder = order;
        // getOrderManager().updateOrder(order);
      
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }//NonDenom account#
 
    } else {
      logError("Payment Group not created");
    }
  }
 
  public void testCreateOrder2() throws CommerceException {
    // checkout the Order and iterate through the result object displaying
    // errors
    PipelineResult pr = getOrderManager().processOrder(mOrder);
    if (pr.hasErrors()) {
      Object[] keys = pr.getErrorKeys();
      for (int i = 0; i < keys.length; i++)
        System.out.println(pr.getError(keys[i]));
    }
    System.out.println("********************ORDERID:  " + mOrder.getId());
    setOrderId(mOrder.getId());

  }
  
  public void testCheckDebit() throws CommerceException {

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      Order order = null;
      if(mOrder == null){
        order = getOrderManager().loadOrder(getOrderId());
        
      }else{
        order = getOrderManager().loadOrder( mOrder.getId());

      }

      List<PaymentGroup> paymentGroups = order.getPaymentGroups();

      for (int i = 0; i < order.getPaymentGroupCount(); i++) {
        vlogInfo("Payment Group class type: "
            + ((PaymentGroup) order.getPaymentGroups().get(i))
                .getPaymentGroupClassType());
        vlogInfo("Payment Group method: "
            + ((PaymentGroup) order.getPaymentGroups().get(i))
                .getPaymentMethod());
        vlogInfo("Payment Group Id"
            + ((PaymentGroup) order.getPaymentGroups().get(i))
                .getId());
      }

      vlogInfo("Debit called");

      getPaymentManager().debit(order, paymentGroups);
      vlogInfo("Successful Debit and updating order now");

      getOrderManager().updateOrder(order);

    } catch (TransactionDemarcationException e) {
      e.printStackTrace();
    } finally {
      if (getTransactionManager() != null)
        try {
          td.end();
        } catch (TransactionDemarcationException e) {
          e.printStackTrace();
        }
    }
  }
  
  public void testCheckCredit() throws CommerceException {

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      Order order = null;
      if(mOrder == null){
        order = getOrderManager().loadOrder(getOrderId());
        
      }else{
        order = getOrderManager().loadOrder( mOrder.getId());

      }

      List<PaymentGroup> paymentGroups = order.getPaymentGroups();

      for (int i = 0; i < order.getPaymentGroupCount(); i++) {
        vlogInfo("Payment Group class type: "
            + ((PaymentGroup) order.getPaymentGroups().get(i))
                .getPaymentGroupClassType());
        vlogInfo("Payment Group method: "
            + ((PaymentGroup) order.getPaymentGroups().get(i))
                .getPaymentMethod());
        vlogInfo("Payment Group Id"
            + ((PaymentGroup) order.getPaymentGroups().get(i))
                .getId());
      }

      vlogInfo("Debit called");

      getPaymentManager().credit(order, paymentGroups);
      vlogInfo("Successful Credit and updating order now");

      getOrderManager().updateOrder(order);

    } catch (TransactionDemarcationException e) {
      e.printStackTrace();
    } finally {
      if (getTransactionManager() != null)
        try {
          td.end();
        } catch (TransactionDemarcationException e) {
          e.printStackTrace();
        }
    }
  }
  
  
  public ContactInfo constructContactInfo() {
    ContactInfo contactInfo = new ContactInfo();
    contactInfo.setAddress1("1 B St");
    contactInfo.setAddress2("");
    contactInfo.setFirstName("Rick");
    contactInfo.setLastName("Ross");
    contactInfo.setCity("Beverly Hills");
    contactInfo.setCountry("US");
    contactInfo.setState("CA");
    contactInfo.setPhoneNumber("2223332222");
    contactInfo.setPostalCode("90201");
    contactInfo.setEmail("rick@ross.com");
    return contactInfo;
  }  
  
  
}
