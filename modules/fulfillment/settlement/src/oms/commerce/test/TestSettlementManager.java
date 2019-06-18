package oms.commerce.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;

import com.aci.commerce.service.AciService;
import com.aci.payment.creditcard.AciCreditCard;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.pipeline.exception.AciPipelineException;
import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardInfo;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.service.FirstDataService;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderTools;
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
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.states.StateDefinitions;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import oms.allocation.item.AllocationConstants;
import oms.allocation.test.TestAllocationPipeline;
import oms.commerce.fulfillment.ManuallyMoveOrderToOMS;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.payment.MFFPaymentManager;
import oms.commerce.settlement.SettlementManagerImpl;
import oms.commerce.states.OMSOrderStates;

public class TestSettlementManager extends GenericService {

  private FirstDataService mFirstDataService;

  /**
   * ************************************* TEST CODE FOR A PROGRAMMATIC CHECKOUT
   * *************************************
   */
  OrderManager mOrderManager;

  ManuallyMoveOrderToOMS manuallyMoveOrderToOMS;

  String orderId = "o480001";

  // CreditCardService creditCardService;

  TransactionManager transactionManager;

  PaymentManager paymentManager;

  PricingTools pricingTools;

  CommerceItemManager commerceItemManager;

  PaymentGroupManager paymentGroupManager;

  String profileId = "330013";

  // String catalogRefId1 = "005925227";
  // String catalogRefId1 = "007010739";
  String catalogRefId1 = "100598414";

  // String productId = "0000000001160";
  // String productId = "0000000006006";
  String productId1 = "0000000227765";

  // private String secondProductId = "0000000044345";
  private String secondProductId = "0000000034443";
  // private String secondSkuId = "003927084";
  private String secondSkuId = "005888193";

  String paymentGroupId = "";

  String billingEmailAddress = "test@test.com";
  // review email address test_review@test.com

  String orgId;

  String merchantId;

  String fingerprintURL;

  Order mOrder = null;

  OMSOrderManager omsOrderManager;

  // String giftCardNumber = "7777091578837875";
  String giftCardNumber = "7777091578670086";

  MFFGiftCardManager giftCardManager;

  double amount = 100;

  private String forceAllocateStore = "400";

  private PipelineManager omsPipelineManager;

  SettlementManagerImpl settlementManager;

  private double creditAmount = 1.0;

  private String ean = "63600715";

  private AciService aciService;

  private String gwpSkuId = "004990552";

  private String ccNumber = "4895390000000013";
  private String ccExpirationMonth = "12";
  private String ccExpirationYear = "2020";
  private String ccCardType = "visa";
  private String CVV = "398";

  private String nameOnCard = "Pavan K Ventrapragada";

  private double multiplePaymentGroupsGiftCardAmount = 20;
  
  private TestAllocationPipeline testAllocationPipeline;

  private String storeId = "1900";
  
  private int multipleQuantity = 4;
  
  
  public double getMultiplePaymentGroupsGiftCardAmount() {
    return multiplePaymentGroupsGiftCardAmount;
  }

  public void setMultiplePaymentGroupsGiftCardAmount(double pMultiplePaymentGroupsGiftCardAmount) {
    multiplePaymentGroupsGiftCardAmount = pMultiplePaymentGroupsGiftCardAmount;
  }

  public double balanceInquiryWithEAN() throws Exception {
    // MFFGiftCardPaymentStatus testResponse =
    // getFirstDataService().balanceInquiry("7777091578618339", "11111111");//
    // NonDenom

    MFFGiftCardPaymentStatus testResponse = getFirstDataService().balanceInquiry(giftCardNumber, getEan());
    vlogInfo("BalanceInquiry-newBalance {0} - responseCode {1} ", testResponse.getNewBalance(), testResponse.getResponseCode());
    return testResponse.getNewBalance();
  }

  public double balanceInquiryWithoutEAN() throws Exception {
    // NonDenom account#
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().balanceInquiry(giftCardNumber);
    vlogInfo("BalanceInquiry-newBalance {0} - responseCode {1} ", testResponse.getNewBalance(), testResponse.getResponseCode());
    return testResponse.getNewBalance();
  }

  public void activateGiftCard() throws Exception {
    // NonDenom account#
    MFFGiftCardInfo testResponse = this.getGiftCardManager().giftCardActivation(giftCardNumber, amount);
    vlogInfo("Activation-newBalance {0} - responseCode {1} ", testResponse.getNewBalance(), testResponse.getResponseCode());
    vlogInfo("Activation-gcNum {0} - Ean {1} ", testResponse.getGiftCardNumber(), testResponse.getEan());

  }

  private boolean testAuthDecline = true;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void testSplitShipmentsForGiftCard() {
    try {
      createOrderWithGiftCard(2);
      submitOrder();

      Order omsOrder = null;
      while (omsOrder == null) {

        try {
          // sleep for 3 seconds
          Thread.sleep(3 * 1000);

          omsOrder = getOmsOrderManager().loadOrder(orderId);

        } catch (Throwable t) {
          vlogDebug(t, "load order failed");
        }
      }
      forceAllocateOrder(omsOrder);

      // ship the first item
      List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
      List<String> lItemsToShip = new ArrayList<String>();

      lItemsToShip.add(lCommerceItems.get(0).getId());

      Map lPipelineParams = new HashMap();
      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      // to test the reauth of a decline at the time of 2nd shipment
      if (testAuthDecline) getFirstDataService().setTestAuthorizationDeclines(true);

      // Settle the shipment
      getSettlementManager().processSettlementRecords();

      getFirstDataService().setTestAuthorizationDeclines(false);

      omsOrder = getOmsOrderManager().loadOrder(orderId);

      ((MFFPaymentManager) getPaymentManager()).validateAndReauthorizePayments(omsOrder);
      // If you hve partial settlmetn will try to authorize again.
      // Ship the second item
      lItemsToShip = new ArrayList<String>();
      lItemsToShip.add(lCommerceItems.get(1).getId());

      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      getSettlementManager().processSettlementRecords();

    } catch (Exception e) {
      logError(e);
    }

  }

  public void testMultipleShipmentsForGiftCard() throws Throwable {
    try {
      createOrderWithGiftCard(2);
      submitOrder();
      allocateAndShipOrder();
      getSettlementManager().processSettlementRecords();

    } catch (Exception e) {
      logError(e);
    }
  }

  public void testGiftCardCredit() throws Throwable {

    TransactionDemarcation td = null;
    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager());
      createOrderWithGiftCard(1);
      submitOrder();
    } finally {
      td.end();
    }
    allocateAndShipOrder();
    vlogInfo("Shipped the order");
    Order omsOrder = null;

    td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager());

      omsOrder = getOmsOrderManager().loadOrder(orderId);

      // debiting 1 dollar
      getSettlementManager().createCreditSettlements(omsOrder, getCreditAmount(), null);
      getSettlementManager().createCreditSettlements(omsOrder, getCreditAmount(), null);

      getOmsOrderManager().updateOrder(omsOrder);
    } finally {
      td.end();
    }

    vlogInfo("Successfully created a debit settlement");

  }

  public void testCreateOrderWithGiftCard() throws Throwable {
    try {
      createOrderWithGiftCard(1);
      submitOrder();
      allocateAndShipOrder();
      getSettlementManager().processSettlementRecords();
    } catch (Throwable t) {
      vlogError(t, "an error occurred");
      throw t;
    }
  }

  public void testCreateOrderWithCreditCardAndGiftCard() throws Throwable {

    TransactionDemarcation td = null;
    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager());

      createOrderWithCreditAndGiftCard(2);

      submitOrder();

    } finally {
      td.end();
    }
    try {
      td = new TransactionDemarcation();
      allocateAndShipOrder();
      getSettlementManager().processSettlementRecords();
    } catch (Throwable t) {

      vlogError(t, "an error occurred");
      throw t;
    } finally {
      td.end();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void shipCommerceItems(ArrayList<String> pCommerceItems) throws Exception {
    TransactionDemarcation td = null;
    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager());
      Order order = getOmsOrderManager().loadOrder(orderId);

      Map lPipelineParams = new HashMap();
      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, order);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, pCommerceItems);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);
      getOmsOrderManager().updateOrder(order);

    } finally {
      td.end();
    }
  }
  
  @SuppressWarnings({ "unchecked" })
  public void allocateAndShipOrder() throws Throwable {
    Order omsOrder = null;
    int count = 0;
    TransactionDemarcation td = null;
    while (omsOrder == null && count < 10) {

      try {
        // sleep for 3 seconds
        Thread.sleep(3 * 1000);

        omsOrder = getOmsOrderManager().loadOrder(orderId);
        ++count;
      } catch (Throwable t) {
        vlogDebug(t, "load order failed");
      }
    }
    ArrayList<String> commerceItemIds = new ArrayList<String>();
    List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
    for (MFFCommerceItemImpl item : lCommerceItems) {
      commerceItemIds.add(item.getId());
    }

    // allocate the order
    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager());
      allocateOrder(omsOrder);
    } finally {
      td.end();
    }
    shipCommerceItems(commerceItemIds);

  }
  
  
  
  @SuppressWarnings("unchecked")
  public void testSplitShipmentsForForceAllocation() {
    try {
      
      createOrderWithCreditCard(2);
      submitOrder();
      Order omsOrder = getOmsOrder();
      //forceAllocate("1800");
      forceAllocateOrder();
      
      List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
      
      ArrayList<String> itemsToDecline = new ArrayList<String> ();
      
      itemsToDecline.add(lCommerceItems.get(0).getId());
      
      getTestAllocationPipeline().setDeclineOrderId(orderId);
      getTestAllocationPipeline().setItemsToDecline(itemsToDecline);
      getTestAllocationPipeline().runDeclinePipeline();
      
      forceAllocateOrder();
      shipOrder();
    
    } catch (Throwable t) {
      vlogError(t, "An error occurred");
    }
  }
  
  protected void forceAllocate(String pStoreId) throws Throwable {
    
    getTestAllocationPipeline().setForceAllocateOrderId(orderId);
    getTestAllocationPipeline().setForceAllocateStore(pStoreId);
    getTestAllocationPipeline().runForceAllocationPipeline();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void testSplitShipmentsForCreditCard() throws Throwable {
    try {
      createOrderWithCreditCard(2);
      submitOrder();


      allocateOrder();
      Order omsOrder = getOmsOrder();

      // ship the first item
      List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
      List<String> lItemsToShip = new ArrayList<String>();

      lItemsToShip.add(lCommerceItems.get(0).getId());

      Map lPipelineParams = new HashMap();
      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      // to test the reauth of a decline at the time of 2nd shipment
      if (testAuthDecline) this.getAciService().setTestAuthorizationDeclines(true);

      // Settle the shipment - first item is shipped and the first auth is used.
      // second auth will fail when it trys to reauth b/c we force it to fail
      
      getSettlementManager().processSettlementRecords(); 

      getAciService().setTestAuthorizationDeclines(false);
      omsOrder = getOmsOrderManager().loadOrder(orderId);

      ((MFFPaymentManager) getPaymentManager()).validateAndReauthorizePayments(omsOrder);
      // If you hve partial settlmetn will try to authorize again.
      // Ship the second item
      lItemsToShip = new ArrayList<String>();
      lItemsToShip.add(lCommerceItems.get(1).getId());

      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      getSettlementManager().processSettlementRecords();
      // make sur ethis settles against

    } catch (Exception e) {
      logError(e);
    }

  }

  public void testCreateOrderWithCreditCard() throws Throwable {
    try {
    createOrderWithCreditCard(1);
    submitOrder();
    allocateAndShipOrder();
    getSettlementManager().processSettlementRecords();
    } catch (Throwable t) {
      vlogError(t, "Error occurred");
    }
  }
  
  public void testCreateOrderWithMultipleQuantities () throws Throwable {
    try {
      createCartWithSameSku(getMultipleQuantity());
      addCreditCardPayment();
      submitOrder();
      allocateAndShipOrder();
      getSettlementManager().processSettlementRecord();
    } catch (Throwable t) {
      vlogError(t, "error");
    }
  }
  
  public void testDeclinesOnSettlments () throws Throwable {
    try {
      createCartWithSameSku(getMultipleQuantity());
      addCreditCardPayment();
      submitOrder();
      allocateAndShipOrder();
      getAciService().setTestAuthorizationDeclines(true);
      getSettlementManager().processSettlementRecord();
    } catch (Throwable t) {
      vlogError(t, "error");
    }    
  }
  
  protected void addCreditCardPayment() throws CommerceException, AciPipelineException {
    getPricingTools().priceOrderTotal(mOrder);
    PaymentGroup pg = getCreditCard();
    getOrderManager().addRemainingOrderAmountToPaymentGroup(mOrder, pg.getId());
    getPricingTools().priceOrderTotal(mOrder);
    
  }
  


  public void moveOrderToOMS() throws Throwable {
    if (StringUtils.isNotBlank(orderId)) {
      getManuallyMoveOrderToOMS().setOrderId(orderId);
      getManuallyMoveOrderToOMS().moveOrderIdToOMS();
    }
  }

  public void forceAllocateOrder() throws Throwable {
    Order omsOrder = null;
    int count = 0;
    TransactionDemarcation td = null;
    omsOrder = getOmsOrder();
    // allocate the order
    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager());
      forceAllocateOrder(omsOrder);
    } finally {
      td.end();
    }
  }
  
  protected Order getOmsOrder() throws Throwable {
    Order omsOrder = null;
    int count = 0;
    while (omsOrder == null && count < 10) {

      try {
        // sleep for 3 seconds
        Thread.sleep(3 * 1000);

        omsOrder = getOmsOrderManager().loadOrder(orderId);
        ++count;
      } catch (Throwable t) {
        vlogDebug(t, "load order failed");
      }
    }
    if (omsOrder == null) throw new CommerceException ("Could not load the order id " + orderId);
    return omsOrder;
  }

  @SuppressWarnings("unchecked")
  public void shipOrder() throws Throwable {
    Order omsOrder = null;
    int count = 0;
    while (omsOrder == null && count < 10) {

      try {
        // sleep for 3 seconds
        Thread.sleep(3 * 1000);

        omsOrder = getOmsOrderManager().loadOrder(orderId);
        ++count;
      } catch (Throwable t) {
        vlogDebug(t, "load order failed");
      }
    }
    ArrayList<String> commerceItemIds = new ArrayList<String>();

    List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
    for (MFFCommerceItemImpl item : lCommerceItems) {
      commerceItemIds.add(item.getId());
    }
    shipCommerceItems(commerceItemIds);
  }

  public void testSalesAudit() {
    try {
      testCreateOrderWithCreditCard();
    } catch (Throwable e) {
      vlogError(e, "An error occurred while testing");
    }
  }
  
  
  protected void createCartWithSameSku(int pQuantity) throws CommerceException {
    
    OrderManager om = getOrderManager();
    CommerceItemManager cm = getCommerceItemManager();
    String profileId = getProfileId();
    String catalogRefId1 = getCatalogRefId1();
    String productId1 = getProductId1();

    mOrder = om.createOrder(profileId);
    CommerceItem item1 = cm.createCommerceItem(catalogRefId1, productId1, pQuantity);
    item1 = cm.addItemToOrder(mOrder, item1);
    // get the ShippingGroup and add the items to it
    ShippingGroup sg = (ShippingGroup) mOrder.getShippingGroups().get(0);

    HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup) sg;
    hardgoodShippingGroup.setShippingAddress(constructContactInfo());

    cm.addItemQuantityToShippingGroup(mOrder, item1.getId(), sg.getId(), pQuantity);
  }

  protected void createCart(int pQuantity) throws CommerceException {
    OrderManager om = getOrderManager();
    CommerceItemManager cm = getCommerceItemManager();
    String profileId = getProfileId();
    String catalogRefId1 = getCatalogRefId1();
    String productId1 = getProductId1();

    mOrder = om.createOrder(profileId);
    CommerceItem item1 = cm.createCommerceItem(catalogRefId1, productId1, 1);
    item1 = cm.addItemToOrder(mOrder, item1);
    CommerceItem item2 = null;
    if (pQuantity > 1) {
      item2 = cm.createCommerceItem(getSecondSkuId(), getSecondProductId(), 1);
      cm.addItemToOrder(mOrder, item2);
    }
    // get the ShippingGroup and add the items to it
    ShippingGroup sg = (ShippingGroup) mOrder.getShippingGroups().get(0);

    HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup) sg;
    hardgoodShippingGroup.setShippingAddress(constructContactInfo());

    cm.addItemQuantityToShippingGroup(mOrder, item1.getId(), sg.getId(), 1);

    if (pQuantity > 1) {
      cm.addItemQuantityToShippingGroup(mOrder, item2.getId(), sg.getId(), 1);
    }
  }

  protected void createOrderWithCreditCard(int quantity) throws CommerceException, AciPipelineException {
    createCart(quantity);
    addCreditCardPayment();
  }
  


  protected AciCreditCard getCreditCard() throws AciPipelineException, CommerceException {
    PaymentGroup pg = (PaymentGroup) mOrder.getPaymentGroups().get(0);
    AciCreditCard creditCard;

    if (pg instanceof AciCreditCard) {
      creditCard = (AciCreditCard) pg;
    } else {
      creditCard = (AciCreditCard) getPaymentGroupManager().createPaymentGroup();
    }
    AciCreditCardInfo ccInfo = getCreditCardInfo();
    creditCard = (AciCreditCard) pg;
    creditCard.setTokenNumber(ccInfo.getTokenNumber());
    creditCard.setNameOnCard(getNameOnCard());
    creditCard.setMopTypeCode(ccInfo.getMopTypeCd());
    String ccNumber = ccInfo.getCreditCardNumber();
    if (ccNumber.length() > 4) {
      ccNumber = ccNumber.substring(ccNumber.length() - 4);
    }
    creditCard.setCreditCardNumber(ccNumber);
    creditCard.setExpirationMonth(ccInfo.getExpirationMonth());
    creditCard.setExpirationYear(ccInfo.getExpirationYear());
    creditCard.setCreditCardType(getCcCardType());
    creditCard.setBillingAddress(ccInfo.getBillingAddress());
    creditCard.setCardVerificationNumber(getCVV());
    return creditCard;
  }

  public AciCreditCardInfo getCreditCardInfo() throws AciPipelineException {
    AciCreditCardInfo aciCardInfo = generateCreditCardInfo(constructContactInfo());
    getAciService().tokenizeCreditCard(aciCardInfo);
    return aciCardInfo;

  }

  private AciCreditCardInfo generateCreditCardInfo(ContactInfo pBillingAddress) {
    AciCreditCardInfo ccInfo = new AciCreditCardInfo();
    ccInfo.setCreditCardNumber(getCcNumber());
    ccInfo.setSecurityCode(getCVV());
    ccInfo.setCreditCardType(getCcCardType());
    ccInfo.setExpirationMonth(getCcExpirationMonth());
    ccInfo.setExpirationYear(getCcExpirationYear());
    ccInfo.setOnlineOrder(true);
    ccInfo.setBillingAddress(pBillingAddress);
    return ccInfo;
  }

  protected void createOrderWithGiftCard(int quantity) throws CommerceException {

    createCart(quantity);

    PaymentGroup payGroup = getPaymentGroupManager().createPaymentGroup(MFFOrderTools.GIFT_CARD_PAYMENT_TYPE);

    if (payGroup instanceof MFFGiftCardPaymentGroup) {
      MFFGiftCardPaymentGroup lGiftCardPaymentGroup = getGiftCard();

      getPaymentGroupManager().addPaymentGroupToOrder(mOrder, lGiftCardPaymentGroup);

      getOrderManager().addRemainingOrderAmountToPaymentGroup(mOrder, lGiftCardPaymentGroup.getId());

      setPaymentGroupId(lGiftCardPaymentGroup.getId());

      // Compute the prices using pricing tools
      getPricingTools().priceOrderTotal(mOrder);

    } else {
      logError("Payment Group not created");
    }
  }

  protected MFFGiftCardPaymentGroup getGiftCard() throws CommerceException {
    PaymentGroup payGroup = getPaymentGroupManager().createPaymentGroup(MFFOrderTools.GIFT_CARD_PAYMENT_TYPE);
    MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) payGroup;
    MFFGiftCardInfo testGCResponse = this.getGiftCardManager().checkGiftCardBalance(giftCardNumber, getEan());
    if (testGCResponse.isTransactionSuccess()) {
      lGiftCardPaymentGroup.setBalanceAmount(testGCResponse.getBalanceAmount());
      lGiftCardPaymentGroup.setCardNumber(giftCardNumber);
      lGiftCardPaymentGroup.setEan(getEan());
    } else {
      vlogInfo("GIFT CARD RESPONSE CODE: " + testGCResponse.getResponseCode());
    }
    return lGiftCardPaymentGroup;
  }

  protected void createOrderWithCreditAndGiftCard(int quantity) throws CommerceException, AciPipelineException {

    createCart(quantity);
    getPricingTools().priceOrderTotal(mOrder);

    MFFGiftCardPaymentGroup giftCard = getGiftCard();
    AciCreditCard creditCard = getCreditCard();
    
    //getPaymentGroupManager().removeAllPaymentGroupsFromOrder(mOrder);
    giftCard.setAmount(getMultiplePaymentGroupsGiftCardAmount());
    getPaymentGroupManager().addPaymentGroupToOrder(mOrder, giftCard);
    //getPaymentGroupManager().addPaymentGroupToOrder(mOrder, creditCard);

    if (giftCard.getBalanceAmount() > 10) {
      getOrderManager().addOrderAmountToPaymentGroup(mOrder, giftCard.getId(), getMultiplePaymentGroupsGiftCardAmount());
      getOrderManager().addRemainingOrderAmountToPaymentGroup(mOrder, creditCard.getId());
      getPaymentGroupManager().recalculatePaymentGroupAmounts(mOrder);
      getOrderManager().updateOrder(mOrder);
    } else {
      throw new CommerceException("Gift card balance insufficient");
    }
  }

  protected void submitOrder() throws CommerceException {
    // checkout the Order and iterate through the result object displaying
    // errors
    PipelineResult pr = getOrderManager().processOrder(mOrder);
    if (pr.hasErrors()) {
      Object[] keys = pr.getErrorKeys();
      for (int i = 0; i < keys.length; i++)
        vlogInfo("Error key" + pr.getError(keys[i]));
    }

    setOrderId(mOrder.getId());

  }

  protected ContactInfo constructContactInfo() {
    ContactInfo contactInfo = new ContactInfo();
    contactInfo.setAddress1("1300 S. Lynndale Drive");
    contactInfo.setAddress2("");
    contactInfo.setFirstName("John");
    contactInfo.setLastName("Doe");
    contactInfo.setCity("Appleton");
    contactInfo.setCountry("US");
    contactInfo.setState("WI");
    contactInfo.setPhoneNumber("7039446292");
    contactInfo.setPostalCode("54914");
    contactInfo.setEmail("johndoe@fleetfarm.com");
    return contactInfo;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void test1ShipmentsForCreditCardAndReturnFull() {
    // REMOVE AFTER TEST
    // o1660001
    try {
      createOrderWithCreditCard(2);
      submitOrder();
      System.out.println("ORDER ID: " + orderId);
      Order omsOrder = null;
      while (omsOrder == null) {

        try {
          // sleep for 3 seconds
          Thread.sleep(3 * 1000);

          omsOrder = getOmsOrderManager().loadOrder(orderId);

        } catch (Throwable t) {
          vlogDebug(t, "load order failed");
        }
      }
      forceAllocateOrder(omsOrder);

      // ship both items.
      List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
      List<String> lItemsToShip = new ArrayList<String>();

      lItemsToShip.add(lCommerceItems.get(0).getId());
      lItemsToShip.add(lCommerceItems.get(1).getId());

      Map lPipelineParams = new HashMap();
      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      // Settle the shipment - both items
      getSettlementManager().processSettlementRecords(); // EXECUTED. ALso
                                                         // revalidate it

      // REFUND CREDIT.
      omsOrder = getOmsOrderManager().loadOrder(orderId);
      // getSettlementManager().processSettlementRecords(); //EXECUTED. ALso
      // revalidate it
      List<PaymentGroup> paymentGroups = omsOrder.getPaymentGroups();
      getPaymentManager().credit(omsOrder, paymentGroups);
      vlogInfo("Successful Credit and updating order now");
      getOmsOrderManager().updateOrder(omsOrder);

    } catch (Exception e) {
      logError(e);
    }

  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void test2SplitShipmentsForCreditCardAndReturnFull() {
    // REMOVE AFTER TEST
    // o1660006
    try {
      createOrderWithCreditCard(2);
      submitOrder();

      Order omsOrder = null;
      while (omsOrder == null) {

        try {
          // sleep for 3 seconds
          Thread.sleep(3 * 1000);

          omsOrder = getOmsOrderManager().loadOrder(orderId);

        } catch (Throwable t) {
          vlogDebug(t, "load order failed");
        }
      }
      forceAllocateOrder(omsOrder);

      // ship the first item
      List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
      List<String> lItemsToShip = new ArrayList<String>();

      lItemsToShip.add(lCommerceItems.get(0).getId());

      Map lPipelineParams = new HashMap();
      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      // to test the reauth of a decline at the time of 2nd shipment
      if (testAuthDecline) this.getAciService().setTestAuthorizationDeclines(true);

      // Settle the shipment - first item is shipped and the first auth is used.
      // second auth will fail when it trys to reauth b/c we force it to fail
      getSettlementManager().processSettlementRecords(); // EXECUTED. ALso
                                                         // revalidate it

      getAciService().setTestAuthorizationDeclines(false); // Now set the auth
                                                           // to suceeded and
                                                           // let the return try
                                                           // again.

      omsOrder = getOmsOrderManager().loadOrder(orderId);

      ((MFFPaymentManager) getPaymentManager()).validateAndReauthorizePayments(omsOrder);// REVALIDTEDS
                                                                                         // PAYMENT
                                                                                         // auth
                                                                                         // before
                                                                                         // settling.//
      // If you hve partial settlmetn will try to authorize again.
      // Ship the second item
      lItemsToShip = new ArrayList<String>();
      lItemsToShip.add(lCommerceItems.get(1).getId());

      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      getSettlementManager().processSettlementRecords();
      // make sur ethis settles against

      // REFUND CREDIT.
      omsOrder = getOmsOrderManager().loadOrder(orderId);
      // getSettlementManager().processSettlementRecords(); //EXECUTED. ALso
      // revalidate it
      List<PaymentGroup> paymentGroups = omsOrder.getPaymentGroups();
      System.out.println("CREDIT ORDER ID: " + orderId);
      // getPaymentManager().credit(omsOrder, paymentGroups);
      // debit for 7 and a debit for 22
      getPaymentManager().credit(omsOrder, paymentGroups);
      // credit for 7 and a credit for 22
      vlogInfo("Successful Credit and updating order now");
      getOmsOrderManager().updateOrder(omsOrder);

    } catch (Exception e) {
      logError(e);
    }

  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void test3SplitShipmentsForCreditCardAndReturnPartial() {
    // REMOVE AFTER TEST
    // o1660008
    try {
      createOrderWithCreditCard(2);
      submitOrder();

      Order omsOrder = null;
      while (omsOrder == null) {

        try {
          // sleep for 3 seconds
          Thread.sleep(3 * 1000);

          omsOrder = getOmsOrderManager().loadOrder(orderId);

        } catch (Throwable t) {
          vlogDebug(t, "load order failed");
        }
      }
      forceAllocateOrder(omsOrder);

      // ship the first item
      List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
      List<String> lItemsToShip = new ArrayList<String>();

      lItemsToShip.add(lCommerceItems.get(0).getId());

      Map lPipelineParams = new HashMap();
      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      // to test the reauth of a decline at the time of 2nd shipment
      if (testAuthDecline) this.getAciService().setTestAuthorizationDeclines(true);

      // Settle the shipment - first item is shipped and the first auth is used.
      // second auth will fail when it trys to reauth b/c we force it to fail
      getSettlementManager().processSettlementRecords(); // EXECUTED. ALso
                                                         // revalidate it

      getAciService().setTestAuthorizationDeclines(false); // Now set the auth
                                                           // to suceeded and
                                                           // let the return try
                                                           // again.

      omsOrder = getOmsOrderManager().loadOrder(orderId);

      ((MFFPaymentManager) getPaymentManager()).validateAndReauthorizePayments(omsOrder);// REVALIDTEDS
                                                                                         // PAYMENT
                                                                                         // auth
                                                                                         // before
                                                                                         // settling.//
      // If you hve partial settlmetn will try to authorize again.
      // Ship the second item
      lItemsToShip = new ArrayList<String>();
      lItemsToShip.add(lCommerceItems.get(1).getId());

      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);

      getSettlementManager().processSettlementRecords();
      // make sur ethis settles against

      // REFUND CREDIT.
      omsOrder = getOmsOrderManager().loadOrder(orderId);
      // getSettlementManager().processSettlementRecords(); //EXECUTED. ALso
      // revalidate it
      List<PaymentGroup> paymentGroups = omsOrder.getPaymentGroups();
      System.out.println("CREDIT ORDER ID: " + orderId);
      // getPaymentManager().credit(omsOrder, paymentGroups);
      // debit for 7 and a debit for 22
      getPaymentManager().credit(omsOrder, paymentGroups.get(0), 25.00);
      // credit for 7 and a credit for 18 = 25
      vlogInfo("Successful Credit and updating order now");
      getOmsOrderManager().updateOrder(omsOrder);

    } catch (Exception e) {
      logError(e);
    }

  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void testDebitSettlement() throws CommerceException, RunProcessException, TransactionDemarcationException {
    // remorse period
    updateOrderStatus();

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager());
      // allocate order
      Order omsOrder = getOmsOrderManager().loadOrder(orderId);

      forceAllocateOrder(omsOrder);

      // Now run the handle store shipment
      omsOrder = getOmsOrderManager().loadOrder(orderId);
      List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
      List<String> lItemsToShip = new ArrayList<String>();
      for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
        lItemsToShip.add(lCommerceItem.getId());

      }

      Map lPipelineParams = new HashMap();
      lPipelineParams = new HashMap();
      lPipelineParams.put(OMSOrderConstants.PIPELINE_OMS_ORDER, omsOrder);
      lPipelineParams.put(OMSOrderConstants.PIPELINE_ITEMS_TO_SHIP, lItemsToShip);
      getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_STORE_SHIPMENT, lPipelineParams);
    } finally {
      td.end();
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void forceAllocateOrder(Order pOrder) throws RunProcessException {
    getTestAllocationPipeline().setForceAllocateOrderId(getOrderId());
    getTestAllocationPipeline().setForceAllocateStore(getStoreId());
    getTestAllocationPipeline().runForceAllocationPipeline();
  }
  
  public void allocateOrder() throws Throwable {
    Order omsOrder = null;
    TransactionDemarcation td = null;
    omsOrder = getOmsOrder();
    // allocate the order
    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager());
      allocateOrder(omsOrder);
    } finally {
      td.end();
    }
  }
  

  @SuppressWarnings("unchecked")
  protected void allocateOrder(Order pOrder) throws RunProcessException {
    Map lPipelineParams = new HashMap();
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, pOrder);
    getOmsPipelineManager().runProcess(AllocationConstants.PIPELINE_ALLOCATE_ORDER, lPipelineParams);
  }  

  public void testGiftCardRefundsToBeActivated() throws RepositoryException, CommerceException, TransactionDemarcationException {

    RepositoryItem[] unsettledCards = getSettlementManager().getGiftCardRefundsToBeActivated(0, 10);
    for (RepositoryItem card : unsettledCards) {
      vlogInfo("payment group id = {0}", card);
      String pgId = card.getRepositoryId();
      String orderId = ((RepositoryItem) card.getPropertyValue("order")).getRepositoryId();

      TransactionDemarcation td = new TransactionDemarcation();
      try {
        td.begin(getTransactionManager());

        Order order = getOmsOrderManager().loadOrder(orderId);
        MFFGiftCardPaymentGroup pg = (MFFGiftCardPaymentGroup) order.getPaymentGroup(pgId);
        vlogInfo("Amount to be activated = {0}", getSettlementManager().getRefundAmount(pg));

        getSettlementManager().settleGiftCardRefund(pg, "1111111111111111111");
        getOmsOrderManager().updateOrder(order);

      } finally {
        td.end();
      }

    }

  }
  
  

 

  protected void updateOrderStatus() throws CommerceException, TransactionDemarcationException {

    TransactionDemarcation td = null;

    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager());
      Order lOMSOrder = getOmsOrderManager().loadOrder(orderId);
      lOMSOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.PENDING_ALLOCATION));
      getOmsOrderManager().updateOrder(lOMSOrder);
    } finally {
      if (td != null) td.end();
    }

  }

  public FirstDataService getFirstDataService() {
    return mFirstDataService;
  }

  public void setFirstDataService(FirstDataService mFirstDataService) {
    this.mFirstDataService = mFirstDataService;
  }

  public void test1Activate() throws Exception {
    MFFGiftCardPaymentStatus testResponse = getFirstDataService().activate(giftCardNumber, 100);// 25Denom
                                                                                                // account#

    vlogInfo("Activate--newBalance {0} - responseCode {1} ", testResponse.getNewBalance(), testResponse.getResponseCode());

  }

  public MFFGiftCardManager getGiftCardManager() {
    return giftCardManager;
  }

  public void setGiftCardManager(MFFGiftCardManager pGiftCardManager) {
    giftCardManager = pGiftCardManager;
  }

  public String getProductId1() {
    return productId1;
  }

  public void setProductId1(String productId) {
    this.productId1 = productId;
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

  public String getGiftCardNumber() {
    return giftCardNumber;
  }

  public void setGiftCardNumber(String pGiftCardNumber) {
    giftCardNumber = pGiftCardNumber;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double pAmount) {
    amount = pAmount;
  }

  public OMSOrderManager getOmsOrderManager() {
    return omsOrderManager;
  }

  public void setOmsOrderManager(OMSOrderManager pOmsOrderManager) {
    omsOrderManager = pOmsOrderManager;
  }

  public String getForceAllocateStore() {
    return forceAllocateStore;
  }

  public void setForceAllocateStore(String pForceAllocateStore) {
    forceAllocateStore = pForceAllocateStore;
  }

  public PipelineManager getOmsPipelineManager() {
    return omsPipelineManager;
  }

  public void setOmsPipelineManager(PipelineManager pOmsPipelineManager) {
    omsPipelineManager = pOmsPipelineManager;
  }

  public String getSecondProductId() {
    return secondProductId;
  }

  public void setSecondProductId(String pSecondProductId) {
    secondProductId = pSecondProductId;
  }

  public String getSecondSkuId() {
    return secondSkuId;
  }

  public void setSecondSkuId(String pSecondSkuId) {
    secondSkuId = pSecondSkuId;
  }

  public SettlementManagerImpl getSettlementManager() {
    return settlementManager;
  }

  public void setSettlementManager(SettlementManagerImpl pSettlementManager) {
    settlementManager = pSettlementManager;
  }

  public boolean isTestAuthDecline() {
    return testAuthDecline;
  }

  public void setTestAuthDecline(boolean pTestAuthDecline) {
    testAuthDecline = pTestAuthDecline;
  }

  public double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(double pCreditAmount) {
    creditAmount = pCreditAmount;
  }

  public String getEan() {
    return ean;
  }

  public void setEan(String pEan) {
    ean = pEan;
  }

  public AciService getAciService() {
    return aciService;
  }

  public void setAciService(AciService pAciService) {
    aciService = pAciService;
  }

  public String getCcNumber() {
    return ccNumber;
  }

  public void setCcNumber(String pCcNumber) {
    ccNumber = pCcNumber;
  }

  public String getCcExpirationMonth() {
    return ccExpirationMonth;
  }

  public void setCcExpirationMonth(String pCcExpirationMonth) {
    ccExpirationMonth = pCcExpirationMonth;
  }

  public String getCcExpirationYear() {
    return ccExpirationYear;
  }

  public void setCcExpirationYear(String pCcExpirationYear) {
    ccExpirationYear = pCcExpirationYear;
  }

  public String getCcCardType() {
    return ccCardType;
  }

  public void setCcCardType(String pCcCardType) {
    ccCardType = pCcCardType;
  }

  public String getCVV() {
    return CVV;
  }

  public void setCVV(String pCVV) {
    CVV = pCVV;
  }

  public String getNameOnCard() {
    return nameOnCard;
  }

  public void setNameOnCard(String pNameOnCard) {
    nameOnCard = pNameOnCard;
  }

  public String getGwpSkuId() {
    return gwpSkuId;
  }

  public void setGwpSkuId(String pGwpSkuId) {
    gwpSkuId = pGwpSkuId;
  }

  public ManuallyMoveOrderToOMS getManuallyMoveOrderToOMS() {
    return manuallyMoveOrderToOMS;
  }

  public void setManuallyMoveOrderToOMS(ManuallyMoveOrderToOMS pManuallyMoveOrderToOMS) {
    manuallyMoveOrderToOMS = pManuallyMoveOrderToOMS;
  }

  public TestAllocationPipeline getTestAllocationPipeline() {
    return testAllocationPipeline;
  }

  public void setTestAllocationPipeline(TestAllocationPipeline pTestAllocationPipeline) {
    testAllocationPipeline = pTestAllocationPipeline;
  }

  public String getStoreId() {
    return storeId;
  }

  public void setStoreId(String pStoreId) {
    storeId = pStoreId;
  }

  public int getMultipleQuantity() {
    return multipleQuantity;
  }

  public void setMultipleQuantity(int pMultipleQuantity) {
    multipleQuantity = pMultipleQuantity;
  }



}
