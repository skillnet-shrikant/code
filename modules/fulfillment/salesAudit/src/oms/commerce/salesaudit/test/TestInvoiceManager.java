package oms.commerce.salesaudit.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.salesaudit.exception.SalesAuditException;
import oms.commerce.salesaudit.util.InvoiceManager;
import atg.commerce.CommerceException;
import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.csr.appeasement.AppeasementTools;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.csr.returns.ReturnTools;
import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

/**
 * This class can be used to test the shipment, return and appeasement 
 * invoice manager calls.
 * 
 * @author jvose
 *
 */
public class TestInvoiceManager 
  extends GenericService {

  /**
   * Test an order shipment call.  You need to specify the orderId,
   * list of items to ship and the map of payment groups/amounts.
   */
  public void testOrderShipment() {
    vlogDebug ("Test Order Shipment ...");
    String lOrderId           = getShipOrderId();
    List<String> lItemsToShip = getShipItems();
    Map <String, Map<String, Double>> lMap = new HashMap <String, Map<String, Double>> ();
    Map<String, Double> lSettlementMap = new HashMap<String, Double>();
    
    int i = 0;
    for (String lPaymentGroup : getShipPaymentGroups()) {
      Double lDouble = new Double (getShipPaymentGroupAmounts().get(i));
      //Double lDouble = 283.75D;
      lSettlementMap.put(lPaymentGroup, lDouble);
      i++;
    }
    testOrderShipment (lOrderId, lItemsToShip, lMap);      
  }
  
  /**
   * Test order shipment.
   * 
   * @param pOrderId        Order Id
   * @param pItemsToShip    List of Commerce Item ID's to ship
   * @param pPaymentGroups  Map of payment groups/amounts
   */
  public void testOrderShipment(String pOrderId, List<String> pItemsToShip, Map<String, Map<String, Double>> pPaymentGroups) {
    vlogDebug ("Test Order Shipment ...");
    Order lOrder = null;
    try {
      lOrder = getOmsOrderManager().loadOrder(pOrderId);
    } catch (CommerceException e) {
      vlogError (e, "Unable to load order ID: {1}", pOrderId);
    }
    try {
      getInvoiceManager().addShipmentInvoice(lOrder, pItemsToShip, pPaymentGroups);
    } catch (SalesAuditException ex) {
      vlogError (ex, "Unable to add a new reporting order");
    }
  }
  
  /**
   * Test the order return call.  You need to spcify the Return Request ID for this
   * method to work.
   */
  public void testOrderReturn() {
    vlogDebug ("Test Order Return for return ID: " + getReturnRequestId() + " using return ID");
    try {
      ReturnRequest lReturnRequest = getReturnTools().getReturnRequest(getReturnRequestId());
      getInvoiceManager().addReturnsInvoice(lReturnRequest);
    } catch (RepositoryException ex) {
      vlogError (ex, "Unable to load the return request for " + getReturnRequestId());    
    } catch (CommerceException ex) {
      vlogError (ex, "Unable to load the return request for " + getReturnRequestId());    
    } catch (SalesAuditException ex) {
      vlogError (ex, "Unable to process the return request for " + getReturnRequestId());
    } 
  }

  /**
   * Test the order appeasement call.  You need to specify the appeasement ID in 
   * ordcer for this call to work.
   */
  public void testOrderAppeasement() {
    vlogDebug ("Test Order Appeasement for appeasement ID: " + getAppeasementId());
    Appeasement lAppeasement;
    try {
      lAppeasement = getOrderAppeasement(getAppeasementId());
      this.getInvoiceManager().addAppeasementInvoice(lAppeasement);
    } catch (SalesAuditException e) {
      vlogError("Unable to add appeasement invoice for ID: " + getAppeasementId());
    }
  }
  
  /**
   * Get an order appeasement given the appeasement ID.
   * 
   * @param pAppeasementId    Appeasement ID
   * @return                  Appeasement 
   * @throws SalesAuditException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Appeasement getOrderAppeasement(String pAppeasementId) 
      throws SalesAuditException {
      
      vlogDebug("Begin - Get order appeasement for {0}", pAppeasementId);
      RepositoryItem[] lItems = null;
      RepositoryItem lItem    = null;
      try {
        Object[] lParams        = new Object[2];
        lParams[0]              = pAppeasementId;
        RepositoryView lView    = getOmsOrderManager().getOmsOrderRepository().getView("appeasement");
        RqlStatement lStatement = RqlStatement.parseRqlStatement("appeasementId EQUALS ?0");
        lItems                  = lStatement.executeQuery(lView, lParams);
        if (lItems != null && lItems.length > 0) 
          lItem = lItems[0];
      } catch (RepositoryException e) {
        String lErrorMessage = String.format("getorder appeasement - Error locating Item Id: %s", pAppeasementId);
        vlogError(e, lErrorMessage);
        throw new SalesAuditException(lErrorMessage, e);
      }    
      Appeasement lAppeasement = null;
      try {
        lAppeasement = getAppeasementTools().loadAppeasement(lItem);
      } catch (CommerceException ex) {
        vlogError (ex, "Unable to load the appeasement");
      } catch (RepositoryException ex) {
        vlogError (ex, "Unable to load the appeasement");
      }      
      return lAppeasement;
    }

  // *********************************************************
  //            Getter/setters
  // *********************************************************
  InvoiceManager mInvoiceManager;
  public InvoiceManager getInvoiceManager() {
    return mInvoiceManager;
  }
  public void setInvoiceManager(InvoiceManager pInvoiceManager) {
    this.mInvoiceManager = pInvoiceManager;
  }

  OMSOrderManager mOrderManager;
  public void setOmsOrderManager(OMSOrderManager pOrderManager)   {
    mOrderManager = pOrderManager;
  }
  public OMSOrderManager getOmsOrderManager()   {
    return mOrderManager;
  }
  
  ReturnTools mReturnTools;
  public ReturnTools getReturnTools() {
    return mReturnTools;
  }
  public void setReturnTools(ReturnTools pReturnTools) {
    mReturnTools = pReturnTools;
  }
  
  AppeasementTools mAppeasementTools;
  public AppeasementTools getAppeasementTools() {
    return mAppeasementTools;
  }
  public void setAppeasementTools(AppeasementTools pAppeasementTools) {
    this.mAppeasementTools = pAppeasementTools;
  }

  // *********************************************************
  //
  //              Shipping Getter/Setters
  //
  // *********************************************************
  String mShipOrderId;
  public String getShipOrderId() {
    return mShipOrderId;
  }
  public void setShipOrderId(String pShipOrderId) {
    this.mShipOrderId = pShipOrderId;
  }
  
  List<String> mShipItems;
  public List<String> getShipItems() {
    return mShipItems;
  }
  public void setShipItems(List<String> pShipItems) {
    this.mShipItems = pShipItems;
  }
  
  List<String> mShipPaymentGroups;
  public List<String> getShipPaymentGroups() {
    return mShipPaymentGroups;
  }
  public void setShipPaymentGroups(List<String> pShipPaymentGroups) {
    this.mShipPaymentGroups = pShipPaymentGroups;
  }
  
  List<String> mShipPaymentGroupAmounts;
  public List<String> getShipPaymentGroupAmounts() {
    return mShipPaymentGroupAmounts;
  }
  public void setShipPaymentGroupAmounts(List<String> pShipPaymentGroupAmounts) {
    this.mShipPaymentGroupAmounts = pShipPaymentGroupAmounts;
  }

  // *********************************************************
  //
  //              Returns Getter/Setters
  //
  // *********************************************************
  String mReturnRequestId;
  public String getReturnRequestId() {
    return mReturnRequestId;
  }
  public void setReturnRequestId(String pReturnRequestId) {
    this.mReturnRequestId = pReturnRequestId;
  }  
  
  String mReturnOrderId;
  public String getReturnOrderId() {
    return mReturnOrderId;
  }
  public void setReturnOrderId(String pReturnOrderId) {
    this.mReturnOrderId = pReturnOrderId;
  }
  
  // *********************************************************
  //
  //              Appeasement Getter/Setters
  //
  // *********************************************************
  String mAppeasementId;
  public String getAppeasementId() {
    return mAppeasementId;
  }
  public void setAppeasementId(String pAppeasementId) {
    this.mAppeasementId = pAppeasementId;
  }    
  
}
