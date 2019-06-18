package oms.commerce.salesaudit.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.transaction.TransactionManager;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.google.common.base.Strings;
import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFGiftCardRefundMethod;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.MFFItemPriceInfo;
import com.mff.commerce.returns.MFFReturnManager;
import com.mff.constants.MFFConstants;
import com.mff.returns.MFFReturnItem;
import com.mff.userprofiling.MFFProfileTools;

import atg.commerce.CommerceException;
import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.csr.returns.CreditCardRefundMethod;
import atg.commerce.csr.returns.RefundMethod;
import atg.commerce.csr.returns.ReturnItem;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.CreditCardRefund;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupNotFoundException;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.Refund;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.payment.PaymentStatus;
//import atg.payment.creditcard.CreditCardStatus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.Profile;
import oms.commerce.order.MFFOMSOrderManager;
import oms.commerce.salesaudit.exception.SalesAuditException;
import oms.commerce.salesaudit.record.AuxiliaryRecord;
import oms.commerce.salesaudit.record.Carton;
import oms.commerce.salesaudit.record.Extract;
import oms.commerce.salesaudit.record.ExtractSummary;
import oms.commerce.salesaudit.record.Invoice;
import oms.commerce.salesaudit.record.InvoiceAddress;
import oms.commerce.salesaudit.record.LineCarton;
import oms.commerce.salesaudit.record.LineDiscount;
import oms.commerce.salesaudit.record.LinePayment;
import oms.commerce.salesaudit.record.LineSummary;
import oms.commerce.salesaudit.record.PaymentSummary;
import oms.commerce.salesaudit.record.ReturnedItem;
import oms.commerce.salesaudit.record.SalesAuditConstants;
import oms.commerce.salesaudit.record.ShippedItem;

/**
 * This class will write the invoice records for shipments, returns and appeasements
 * to the invoice repository.  It is also used to identify the records to be extracted
 * and to record the extract statistics in the invoice repository.
 *
 * @author jvose
 *
 */
public class InvoiceManager
  extends GenericService {

  private String mDefaultReturnStoreID;

  private String defaultReturnShippingReasonCode;

  private Map<String, String> returnReasonCodes;

  private String defaultReturnReasonCode;

  private MFFReturnManager returnManager;

  private boolean processPriceOverrides;

  // ***********************************************************************************
  //
  //
  //                          Add Shipment Invoice Routines
  //
  //
  // ***********************************************************************************

  public boolean isProcessPriceOverrides() {
	return processPriceOverrides;
}

public void setProcessPriceOverrides(boolean pProcessPriceOverrides) {
	processPriceOverrides = pProcessPriceOverrides;
}

/**
   * Add an invoice record to the invoice repository when an item has been
   * shipped by the store.
   *
   * @param pOrder        ATG Order Object
   * @param pItemsToShip  List of Commerce items to ship
   * @throws SalesAuditException
   */
  public void addShipmentInvoice (Order pOrder, List<String> pItemsToShip, Map <String, Map<String, Double>> pShippingGroupToSettlementMap)
      throws SalesAuditException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();

    vlogInfo ("+++++ Begin - Add a new shipment invoice for order {0}/{1}", lOrderNumber, lOrderId);

    boolean lRollback = true;
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      // Create a transaction
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Create an extract statistics record
      ExtractStatistics lExtractStatistics = new ExtractStatistics();

      // Get a list of the commerce items for a given shipment group
      Hashtable <String, List<String>> lShippingGroupItem = getOmsOrderManager().getItemForShipGroups (pOrder, pItemsToShip);
      // Output an Invoice record for each shipment group
      Set<String> lShippingGroupIds = lShippingGroupItem.keySet();
      for(String lShippingGroupId : lShippingGroupIds) {
        vlogDebug ("***** Now processing order {0}/{1} - Shipping group {2}", lOrderNumber, lOrderId, lShippingGroupId);

        // Create Shipping Address
        ContactInfo lShippingAddress = (ContactInfo) getShippingAddressForOrder (lOrder, lShippingGroupId);
        InvoiceAddress lInvoiceShippingAddress = createShippingAddress (lOrder, lShippingGroupId, lShippingAddress);

        // Create Billing Address
        ContactInfo lBillingAddress = (ContactInfo) getBillingAddressForOrder (lOrder, lShippingGroupId);
        InvoiceAddress lInvoiceBillingAddress = createBillingAddress  (lOrder, lShippingGroupId, lBillingAddress);

        // Create Shipped Lines
        List<ShippedItem> lShippedItems = createShippedLines (pOrder, lShippingGroupId, lShippingGroupItem, lExtractStatistics);

        ShippedItem lShipmentShippedItem = createShipmentShippedItem(lOrder, pItemsToShip, lShippedItems,lShippingGroupId);

        //add the shipping charges ship line item
        lShippedItems.add(lShipmentShippedItem);

        // Create Invoice
        Invoice lInvoice = createInvoice ((MFFOrderImpl) pOrder, lShippingGroupId, pItemsToShip, lExtractStatistics, lInvoiceShippingAddress,  lInvoiceBillingAddress, lShippedItems, pShippingGroupToSettlementMap.get(lShippingGroupId));
        vlogDebug (lInvoice.toString());
      }
      lRollback = false;
    } catch (CommerceException ce) {
      String lErrorMessage = String.format("Commerce exception was thrown trying to add the shipment invoice for order %s/%s", lOrderNumber, lOrderId);
      vlogError(ce, lErrorMessage);
      throw new SalesAuditException(ce, lErrorMessage);
    } catch (TransactionDemarcationException ex) {
      String lErrorMessage = String.format("Unable to add shipment invoice (1) for order %s/%s", lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    } catch (SalesAuditException ex) {
      String lErrorMessage = String.format("Unable to add shipment invoice (2) for order %s/%s", lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    finally {
      try {
        td.end (lRollback);
      }
      catch (TransactionDemarcationException ex) {
        String lErrorMessage = String.format("Unable to add shipment invoice (3) for order %s/%s", lOrderNumber, lOrderId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogInfo ("+++++ End - Add a new shipment invoice for order {0}/{1}", lOrderNumber, lOrderId);
  }

  public void addShipmentInvoiceForSplitOrder (Order pOrder, List<String> pItemsToShip, Map <String, Map<String, Double>> pShippingGroupToSettlementMap)
      throws SalesAuditException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();

    vlogInfo ("addShipmentInvoiceForSplitOrder : +++++ Begin - Add a new shipment invoice for order {0}/{1}", lOrderNumber, lOrderId);

    boolean lRollback = true;
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      // Create a transaction
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Create an extract statistics record
      ExtractStatistics lExtractStatistics = new ExtractStatistics();

      // Get a list of the commerce items for a given shipment group
      Hashtable <String, List<String>> lShippingGroupItem = getOmsOrderManager().getItemForShipGroupsForSplitOrder (pOrder, pItemsToShip);
      // Output an Invoice record for each shipment group
      Set<String> lShippingGroupIds = lShippingGroupItem.keySet();
      for(String lShippingGroupId : lShippingGroupIds) {
        vlogDebug ("addShipmentInvoiceForSplitOrder : ***** Now processing order {0}/{1} - Shipping group {2}", lOrderNumber, lOrderId, lShippingGroupId);

        // Create Shipping Address
        ContactInfo lShippingAddress = (ContactInfo) getShippingAddressForOrder (lOrder, lShippingGroupId);
        InvoiceAddress lInvoiceShippingAddress = createShippingAddress (lOrder, lShippingGroupId, lShippingAddress);

        // Create Billing Address
        ContactInfo lBillingAddress = (ContactInfo) getBillingAddressForOrder (lOrder, lShippingGroupId);
        InvoiceAddress lInvoiceBillingAddress = createBillingAddress  (lOrder, lShippingGroupId, lBillingAddress);

        // Create Shipped Lines
        List<ShippedItem> lShippedItems = createShippedLinesForSplitOrder(pOrder, lShippingGroupId, lShippingGroupItem, lExtractStatistics);

        ShippedItem lShipmentShippedItem = createShipmentShippedItemForSplitOrder(lOrder, pItemsToShip, lShippedItems,lShippingGroupId);

        //add the shipping charges ship line item
        lShippedItems.add(lShipmentShippedItem);

        // Create Invoice
        Invoice lInvoice = createInvoiceForSplitOrder ((MFFOrderImpl) pOrder, lShippingGroupId, pItemsToShip, lExtractStatistics, lInvoiceShippingAddress,  lInvoiceBillingAddress, lShippedItems, pShippingGroupToSettlementMap);
        vlogDebug (lInvoice.toString());
      }
      lRollback = false;
    } catch (CommerceException ce) {
      String lErrorMessage = String.format("Commerce exception was thrown trying to add the shipment invoice for order %s/%s", lOrderNumber, lOrderId);
      vlogError(ce, lErrorMessage);
      throw new SalesAuditException(ce, lErrorMessage);
    } catch (TransactionDemarcationException ex) {
      String lErrorMessage = String.format("Unable to add shipment invoice (1) for order %s/%s", lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    } catch (SalesAuditException ex) {
      String lErrorMessage = String.format("Unable to add shipment invoice (2) for order %s/%s", lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    finally {
      try {
        td.end (lRollback);
      }
      catch (TransactionDemarcationException ex) {
        String lErrorMessage = String.format("Unable to add shipment invoice (3) for order %s/%s", lOrderNumber, lOrderId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogInfo ("addShipmentInvoiceForSplitOrder : +++++ End - Add a new shipment invoice for order {0}/{1}", lOrderNumber, lOrderId);
  }

  // ***********************************************************************************
  //
  //                          Add Shipment Add Item Methods
  //
  // ***********************************************************************************

  /**
   * Create an Invoice item and all of the components that are dependent on the
   * invoice.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @return                    Invoice Record
   * @throws SalesAuditException
   */
  protected Invoice createInvoice (MFFOrderImpl pOrder, String pShippingGroupId, List<String> pItemsToShip, ExtractStatistics pExtractStatistics,
      InvoiceAddress pInvoiceShippingAddress, InvoiceAddress pInvoiceBillingAddress, List<ShippedItem> pShippedItems, Map <String, Double> pPaymentGroups)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin createShippingAddress for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);

    // Create Invoice
    Invoice lInvoice = createInvoiceItem (pOrder, pShippingGroupId, pItemsToShip, pExtractStatistics, pInvoiceShippingAddress, pInvoiceBillingAddress, pShippedItems, pPaymentGroups);

    vlogDebug ("+++++ End createShippingAddress for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);
    return lInvoice;
  }

  protected Invoice createInvoiceForSplitOrder (MFFOrderImpl pOrder, String pShippingGroupId, List<String> pItemsToShip, ExtractStatistics pExtractStatistics,
      InvoiceAddress pInvoiceShippingAddress, InvoiceAddress pInvoiceBillingAddress, List<ShippedItem> pShippedItems, Map <String, Map<String, Double>> pShippingGroupToSettlementMap)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin createInvoiceForSplitOrder for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);

    // Create Invoice
    Invoice lInvoice = createInvoiceItemForSplitOrder (pOrder, pShippingGroupId, pItemsToShip, pExtractStatistics, pInvoiceShippingAddress, pInvoiceBillingAddress, pShippedItems, pShippingGroupToSettlementMap);

    vlogDebug ("+++++ End createInvoiceForSplitOrder for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);
    return lInvoice;
  }

  protected Date getShipDateForShippment (MFFHardgoodShippingGroup pShippingGroup, MFFOrderImpl pOrder, List<String> pItemsToShip) throws SalesAuditException {
    Date shipDate = null;
    if (pShippingGroup.getActualShipDate() != null) {
      shipDate = pShippingGroup.getActualShipDate();
    } else {
      for (String lItemId : pItemsToShip) {
        MFFCommerceItemImpl lCommerceItem = getCommerceItem(pOrder, lItemId);
        if (lCommerceItem != null) {
          shipDate = lCommerceItem.getShipDate();
        }
        if (shipDate != null) break;
      }

    }
    if (shipDate == null) {
      vlogWarning("Ship date for order number {0} order id {1} could not be found using the current time", pOrder.getOrderNumber(), pOrder.getId());
      shipDate = new Date();
    }
    return shipDate;
  }

  /**
   * Create an Invoice item.
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @return                    Invoice Record
   * @throws SalesAuditException
   */
  protected Invoice createInvoiceItem (MFFOrderImpl pOrder, String pShippingGroupId, List<String> pItemsToShip, ExtractStatistics pExtractStatistics, InvoiceAddress pInvoiceShippingAddress, InvoiceAddress pInvoiceBillingAddress, List<ShippedItem> pShippedItems, Map <String, Double> pPaymentGroups)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Create Invoice Item for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);

    // Get the commerce item
    MFFHardgoodShippingGroup lMFFHardgoodShippingGroup = (MFFHardgoodShippingGroup) getShippingGroup (pOrder, pShippingGroupId);
    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_INVOICE);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Shipping Address for order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    Invoice lInvoice = new Invoice (lMutableRepositoryItem);
    lInvoice.setOrderNumber               (pOrder.getOrderNumber());

    lInvoice.setOrderDate                 (new Timestamp ((getShipDateForShippment(lMFFHardgoodShippingGroup, pOrder, pItemsToShip)).getTime()));
    lInvoice.setSource                    (SalesAuditConstants.ORDER_SOURCE);
    lInvoice.setOrderType                 (SalesAuditConstants.ORDER_TYPE);
    lInvoice.setBusinessType              (SalesAuditConstants.BUSINESS_TYPE);
    lInvoice.setLoyaltyIdentifier         (null);
    lInvoice.setCustomerPurchaseOrder     (null);
    lInvoice.setReference                 (lInvoice.getId());
    lInvoice.setFleetFarmId               (pOrder.getEmployeeId());

    // Tax Exemption
    Hashtable <String, String> lTaxExemptionHash = getTaxExemptionCertificate (pOrder);
    lInvoice.setTaxExemptionCertificate   (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CERT));
    lInvoice.setTaxExemptionName          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_NAME));
    lInvoice.setTaxExemptionType          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CODE));

    // Tax
    Hashtable <String, Double> lOrderTax = getShippingAndTaxes (pOrder, pItemsToShip, pShippingGroupId);
    lInvoice.setOrderShipping             (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setOrderShippingLocalTax     (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
    lInvoice.setOrderShippingCountyTax    (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
    lInvoice.setOrderShippingStateTax     (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
    lInvoice.setOrderShippingTotal        (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingTax          (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingExtendedTotal(lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setShipVia                   (getShipViaFromMethod(lMFFHardgoodShippingGroup.getShippingMethod()));
    lInvoice.setStatus                    (SalesAuditConstants.EXTRACT_STATUS_CREATED);
    lInvoice.setLastExtractDate           (null);
    lInvoice.setExtract                   (null);
    lInvoice.setShippingAddress           (pInvoiceShippingAddress);
    lInvoice.setBillingAddress            (pInvoiceBillingAddress);
    lInvoice.setShippedItems              (pShippedItems);

    // Create order auxiliaries
    List<AuxiliaryRecord> lAuxiliaryRecords = createOrderAuxiliarys (pOrder);
    lInvoice.setAuxiliaries(lAuxiliaryRecords);

    // Create Payments
    List<LinePayment> lLinePayments = createLinePayments (pOrder, pShippingGroupId, pItemsToShip, pExtractStatistics, pPaymentGroups);
    lInvoice.setPayments(lLinePayments);

    // Create Cartons
    List<Carton> lCartons = createCartons (pOrder, pShippingGroupId, pItemsToShip);
    lInvoice.setCartons(lCartons);

    // Create Summary
    LineSummary lLineSummary = createLineSummary (pOrder, pShippingGroupId, pExtractStatistics);
    lInvoice.setLineSummary(lLineSummary);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lInvoice.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Shipping Address for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Create Invoice Item for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);
    return lInvoice;
  }

  protected Invoice createInvoiceItemForSplitOrder (MFFOrderImpl pOrder, String pShippingGroupId, List<String> pItemsToShip, ExtractStatistics pExtractStatistics, InvoiceAddress pInvoiceShippingAddress, InvoiceAddress pInvoiceBillingAddress,
      List<ShippedItem> pShippedItems, Map <String, Map<String, Double>> pShippingGroupToSettlementMap)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Create Invoice Item for Split Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);

    // Get the commerce item
    MFFHardgoodShippingGroup lMFFHardgoodShippingGroup = (MFFHardgoodShippingGroup) getShippingGroup (pOrder, pShippingGroupId);
    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_INVOICE);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Shipping Address for order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    Invoice lInvoice = new Invoice (lMutableRepositoryItem);
    lInvoice.setOrderNumber               (pOrder.getOrderNumber());

    lInvoice.setOrderDate                 (new Timestamp ((getShipDateForShippment(lMFFHardgoodShippingGroup, pOrder, pItemsToShip)).getTime()));
    lInvoice.setSource                    (SalesAuditConstants.ORDER_SOURCE);
    lInvoice.setOrderType                 (SalesAuditConstants.ORDER_TYPE);
    lInvoice.setBusinessType              (SalesAuditConstants.BUSINESS_TYPE);
    lInvoice.setLoyaltyIdentifier         (null);
    lInvoice.setCustomerPurchaseOrder     (null);
    lInvoice.setReference                 (lInvoice.getId());
    lInvoice.setFleetFarmId               (pOrder.getEmployeeId());

    // Tax Exemption
    Hashtable <String, String> lTaxExemptionHash = getTaxExemptionCertificate (pOrder);
    lInvoice.setTaxExemptionCertificate   (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CERT));
    lInvoice.setTaxExemptionName          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_NAME));
    lInvoice.setTaxExemptionType          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CODE));

    // Tax
    Hashtable <String, Double> lOrderTax = getShippingAndTaxesForSplitOrder(pOrder, pItemsToShip, pShippingGroupId);
    lInvoice.setOrderShipping             (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setOrderShippingLocalTax     (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
    lInvoice.setOrderShippingCountyTax    (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
    lInvoice.setOrderShippingStateTax     (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
    lInvoice.setOrderShippingTotal        (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingTax          (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingExtendedTotal(lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setShipVia                   (getShipViaFromMethod(lMFFHardgoodShippingGroup.getShippingMethod()));
    lInvoice.setStatus                    (SalesAuditConstants.EXTRACT_STATUS_CREATED);
    lInvoice.setLastExtractDate           (null);
    lInvoice.setExtract                   (null);
    lInvoice.setShippingAddress           (pInvoiceShippingAddress);
    lInvoice.setBillingAddress            (pInvoiceBillingAddress);
    lInvoice.setShippedItems              (pShippedItems);

    // Create order auxiliaries
    List<AuxiliaryRecord> lAuxiliaryRecords = createOrderAuxiliarys (pOrder);
    lInvoice.setAuxiliaries(lAuxiliaryRecords);

    // Create Payments
    List<LinePayment> lLinePayments = createLinePaymentsForSplitOrder (pOrder, pShippingGroupId, pItemsToShip, pExtractStatistics, pShippingGroupToSettlementMap);
    lInvoice.setPayments(lLinePayments);

    // Create Cartons
    List<Carton> lCartons = createCartons (pOrder, pShippingGroupId, pItemsToShip);
    lInvoice.setCartons(lCartons);

    // Create Summary
    LineSummary lLineSummary = createLineSummary (pOrder, pShippingGroupId, pExtractStatistics);
    lInvoice.setLineSummary(lLineSummary);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lInvoice.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Shipping Address for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Create Invoice Item for Split Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);
    return lInvoice;
  }

  /**
   * Create the order auxiliary records.
   *
   * @param pOrder          ATG Order
   * @return                List of Order auxiliary records
   * @throws SalesAuditException
   */
  protected List<AuxiliaryRecord> createOrderAuxiliarys (MFFOrderImpl pOrder)
      throws SalesAuditException {

    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Create Order Auxiliaries for Order : {0}/{1}", lOrderNumber, lOrderId);

    AuxiliaryRecord lAuxiliaryRecord = null;
    List<AuxiliaryRecord> lAuxiliaryRecords = new Vector <AuxiliaryRecord> ();

    // Sub-market
    vlogDebug ("+++++ Create Sub-Market Order Auxiliaries for Order : {0}/{1}", lOrderNumber, lOrderId);
    String lSubMarket = SalesAuditConstants.NO_MARKET_CODE;
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_ORDER, SalesAuditConstants.AUXILIARY_TYPE_ORDER_SUB_MARKET, lSubMarket);
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    // Customer Id
    vlogDebug ("+++++ Create Customer ID Order Auxiliaries for Order : {0}/{1}", lOrderNumber, lOrderId);
    String lCustomerId = pOrder.getProfileId();
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_ORDER, SalesAuditConstants.AUXILIARY_TYPE_ORDER_CUSTOMER_ID, lCustomerId);
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    // Base Sales Tax Rate
    vlogDebug ("+++++ Create Base Tax Rate Order Auxiliaries for Order : {0}/{1}", lOrderNumber, lOrderId);
    String lBaseTaxRate = getBaseSalesTax(pOrder);
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_ORDER, SalesAuditConstants.AUXILIARY_TYPE_ORDER_BASE_SALES_TAX_RATE, lBaseTaxRate);
    lAuxiliaryRecords.add(lAuxiliaryRecord);
    vlogDebug ("+++++ End create Order Auxiliaries for Order : {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId);
    return lAuxiliaryRecords;
  }

  /**
   * Find the base sales tax rate for this order.
   *
   * @param pOrder        ATG Order
   * @return              Sales Tax rate as String
   */
  @SuppressWarnings("unchecked")
  protected String getBaseSalesTax (MFFOrderImpl pOrder) {
    List <MFFCommerceItemImpl> lCommerceItems = pOrder.getCommerceItems();
    boolean lRateFound = false;
    double lBaseTaxRate = 0.00;
    for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
      if (lRateFound) continue;
      TaxPriceInfo lTaxPriceInfo = lCommerceItem.getTaxPriceInfo();
      double lTaxAmount  = lTaxPriceInfo != null ? lTaxPriceInfo.getAmount() : 0.0d;
      double lLineAmount = lCommerceItem.getPriceInfo().getAmount();
      if (lTaxAmount > 0 && lLineAmount > 0) {
        lBaseTaxRate  = lTaxAmount/lLineAmount * 100;
        lRateFound    = true;
      }
    }
    return formatBigDecimal (lBaseTaxRate).toString();
  }

  /**
   * Create the line payments for the shipped lines items.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @param pItemsToShip        Items to Ship
   * @return                    List of line payments
   * @throws SalesAuditException
   */
  @SuppressWarnings("unchecked")
  protected List<LinePayment> createLinePayments (MFFOrderImpl pOrder, String pShippingGroupId, List<String> pItemsToShip,
      ExtractStatistics pExtractStatistics,
      Map <String, Double> pPaymentGroups)
      throws SalesAuditException {
    String lOrderNumber     = pOrder.getOrderNumber();
    String lOrderId         = pOrder.getId();
    vlogDebug ("+++++ Begin Create Line Payments for Order : {0}/{1}", lOrderNumber, lOrderId);

    List <LinePayment> lLinePayments = new Vector <LinePayment> ();

    // Get Shipping Group
    MFFHardgoodShippingGroup lMFFHardgoodShippingGroup = (MFFHardgoodShippingGroup) getShippingGroup (pOrder, pShippingGroupId);
    Date lShipDate            = getShipDateForShippment(lMFFHardgoodShippingGroup, pOrder, pItemsToShip);
    Timestamp lShipTimestamp  = new Timestamp (lShipDate.getTime());

    // Loop through all the items and get payment group(s) for items
    for (Map.Entry<String, Double> lPaymentAmount : pPaymentGroups.entrySet()) {
      String lPaymentGroupId          = lPaymentAmount.getKey();
      double lPaymentGroupAmount      = lPaymentAmount.getValue().doubleValue();
      PaymentGroup lPaymentGroup      = getPaymentGroup (pOrder, lPaymentGroupId);

      MutableRepositoryItem lMutableRepositoryItem = null;
      try {
        lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_PAYMENT);
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to add line payment for order %s/%s", lOrderNumber, lOrderId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
      LinePayment lLinePayment = new LinePayment (lMutableRepositoryItem);
      if (lPaymentGroup instanceof CreditCard) {
        CreditCard lCreditCardPaymentGroup = (CreditCard) lPaymentGroup;
        lLinePayment.setAmount                  (lPaymentGroupAmount);
        lLinePayment.setCardNumber              ("************" + lCreditCardPaymentGroup.getCreditCardNumber());
        lLinePayment.setCardReference           (lCreditCardPaymentGroup.getCreditCardNumber());
        lLinePayment.setPaymentDate             (lShipTimestamp);
        lLinePayment.setPaymentType             (getMillsPaymentMethod (lCreditCardPaymentGroup.getCreditCardType()));
        lLinePayment.setTokenId                 ((String) (lCreditCardPaymentGroup.getPropertyValue("tokenNumber")));

        /*
         * Instead of credit card status we are using payment status due to bug
         * 2354. When an authorization fails we are storing it as PaymentStatus.
         * This will be fixed as part of 2354. But existing orders needs to be
         * handled as well.
         */
        List<PaymentStatus> lCreditCardStatuses = lCreditCardPaymentGroup.getAuthorizationStatus();
        for (PaymentStatus lCreditCardStatus : lCreditCardStatuses) {
          if (lCreditCardStatus.getTransactionSuccess()) lLinePayment.setTransactionReference(lCreditCardStatus.getTransactionId());
        }


        List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (pOrder, lPaymentGroup, "CreditCard");
        lLinePayment.setAuxiliaries(lAuxiliaryRecords);
        pExtractStatistics.setPaymentCount      (pExtractStatistics.getPaymentCount() + 1);
        pExtractStatistics.setPaymentTotal      (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
      }
      if (lPaymentGroup instanceof MFFGiftCardPaymentGroup) {
        MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) lPaymentGroup;
        lLinePayment.setAmount                (lPaymentGroupAmount);
        //As per the bug 2110, sending unmasked giftcard number in the sales audit
        lLinePayment.setCardNumber            (lGiftCardPaymentGroup.getCardNumber());
        lLinePayment.setCardReference         (getCardReferenceNumber(lGiftCardPaymentGroup.getCardNumber()));
        lLinePayment.setPaymentDate           (lShipTimestamp);
        lLinePayment.setPaymentType           (SalesAuditConstants.GIFT_CARD_PAYMENT_TYPE);
        lLinePayment.setTokenId               (null);
        lLinePayment.setTransactionReference  (null);
        List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (pOrder, lPaymentGroup, "GiftCard");
        lLinePayment.setAuxiliaries(lAuxiliaryRecords);
        pExtractStatistics.setPaymentCount    (pExtractStatistics.getPaymentCount() + 1);
        pExtractStatistics.setPaymentTotal    (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
      }
      try {
        getInvoiceRepository().addItem(lLinePayment.getRepositoryItem());
        lLinePayments.add(lLinePayment);
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to add Line payment for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogDebug ("+++++ End Create Line Payments for Order : {0}/{1}", lOrderNumber, lOrderId);
    return lLinePayments;
  }

  @SuppressWarnings("unchecked")
  protected List<LinePayment> createLinePaymentsForSplitOrder (MFFOrderImpl pOrder, String pShippingGroupId, List<String> pItemsToShip,
      ExtractStatistics pExtractStatistics,
      Map <String, Map<String, Double>> pShippingGroupToSettlementMap)
      throws SalesAuditException {

    String lOrderNumber     = pOrder.getOrderNumber();
    String lOrderId         = pOrder.getId();
    vlogDebug ("+++++ Begin Create Line Payments for Split Order : {0}/{1}", lOrderNumber, lOrderId);

    List <LinePayment> lLinePayments = new Vector <LinePayment> ();

    // Get Shipping Group
    MFFHardgoodShippingGroup lMFFHardgoodShippingGroup = (MFFHardgoodShippingGroup) getShippingGroup (pOrder, pShippingGroupId);
    Date lShipDate            = getShipDateForShippment(lMFFHardgoodShippingGroup, pOrder, pItemsToShip);
    Timestamp lShipTimestamp  = new Timestamp (lShipDate.getTime());
    Hashtable<String, List<String>> lShippingGroupItem;

    try {
      lShippingGroupItem = getOmsOrderManager().getItemForShipGroups (pOrder, pItemsToShip);
    } catch (CommerceException e) {
      String lErrorMsg = String.format("Could not get the shipping groups for the items that are being shipped for %s/%s", pOrder.getId(), pOrder.getOrderNumber());
      vlogError(e, lErrorMsg);
      throw new SalesAuditException(e, lErrorMsg);
    }

    Set<String> lShippingGroupIds = lShippingGroupItem.keySet();

    HashMap<String,LinePayment> pgGroupToLineItemMap = new HashMap<String,LinePayment>();

    // Loop through all the items and get payment group(s) for items
    for(String lShippingGroupId : lShippingGroupIds) {
      Map <String, Double> pPaymentGroups = pShippingGroupToSettlementMap.get(lShippingGroupId);

      for (Map.Entry<String, Double> lPaymentAmount : pPaymentGroups.entrySet()) {

        String lPaymentGroupId          = lPaymentAmount.getKey();
        double lPaymentGroupAmount      = lPaymentAmount.getValue().doubleValue();
        PaymentGroup lPaymentGroup      = getPaymentGroup (pOrder, lPaymentGroupId);

        if(pgGroupToLineItemMap != null && pgGroupToLineItemMap.get(lPaymentGroupId) != null){
          LinePayment lPayment = pgGroupToLineItemMap.get(lPaymentGroupId);
          lPayment.setAmount(lPayment.getAmount() +  lPaymentGroupAmount);
          pExtractStatistics.setPaymentTotal      (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
          try {
            getInvoiceRepository().updateItem(lPayment.getRepositoryItem());
            continue;
          } catch (RepositoryException ex) {
            String lErrorMessage = String.format("Unable to update line payment for split order %s/%s", lOrderNumber, lOrderId);
            vlogError (ex, lErrorMessage);
            throw new SalesAuditException (ex, lErrorMessage);
          }
        }

        MutableRepositoryItem lMutableRepositoryItem = null;
        try {
          lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_PAYMENT);
        } catch (RepositoryException ex) {
          String lErrorMessage = String.format("Unable to add line payment for split order %s/%s", lOrderNumber, lOrderId);
          vlogError (ex, lErrorMessage);
          throw new SalesAuditException (ex, lErrorMessage);
        }

        LinePayment lLinePayment = new LinePayment (lMutableRepositoryItem);
        if (lPaymentGroup instanceof CreditCard) {
          CreditCard lCreditCardPaymentGroup = (CreditCard) lPaymentGroup;
          lLinePayment.setAmount                  (lPaymentGroupAmount);
          lLinePayment.setCardNumber              ("************" + lCreditCardPaymentGroup.getCreditCardNumber());
          lLinePayment.setCardReference           (lCreditCardPaymentGroup.getCreditCardNumber());
          lLinePayment.setPaymentDate             (lShipTimestamp);
          lLinePayment.setPaymentType             (getMillsPaymentMethod (lCreditCardPaymentGroup.getCreditCardType()));
          lLinePayment.setTokenId                 ((String) (lCreditCardPaymentGroup.getPropertyValue("tokenNumber")));

          /*
           * Instead of credit card status we are using payment status due to bug
           * 2354. When an authorization fails we are storing it as PaymentStatus.
           * This will be fixed as part of 2354. But existing orders needs to be
           * handled as well.
           */
          List<PaymentStatus> lCreditCardStatuses = lCreditCardPaymentGroup.getAuthorizationStatus();
          for (PaymentStatus lCreditCardStatus : lCreditCardStatuses) {
            if (lCreditCardStatus.getTransactionSuccess()) lLinePayment.setTransactionReference(lCreditCardStatus.getTransactionId());
          }


          List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (pOrder, lPaymentGroup, "CreditCard");
          lLinePayment.setAuxiliaries(lAuxiliaryRecords);
          pExtractStatistics.setPaymentCount      (pExtractStatistics.getPaymentCount() + 1);
          pExtractStatistics.setPaymentTotal      (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
        }
        if (lPaymentGroup instanceof MFFGiftCardPaymentGroup) {
          MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) lPaymentGroup;
          lLinePayment.setAmount                (lPaymentGroupAmount);
          //As per the bug 2110, sending unmasked giftcard number in the sales audit
          lLinePayment.setCardNumber            (lGiftCardPaymentGroup.getCardNumber());
          lLinePayment.setCardReference         (getCardReferenceNumber(lGiftCardPaymentGroup.getCardNumber()));
          lLinePayment.setPaymentDate           (lShipTimestamp);
          lLinePayment.setPaymentType           (SalesAuditConstants.GIFT_CARD_PAYMENT_TYPE);
          lLinePayment.setTokenId               (null);
          lLinePayment.setTransactionReference  (null);
          List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (pOrder, lPaymentGroup, "GiftCard");
          lLinePayment.setAuxiliaries(lAuxiliaryRecords);
          pExtractStatistics.setPaymentCount    (pExtractStatistics.getPaymentCount() + 1);
          pExtractStatistics.setPaymentTotal    (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
        }
        try {
          getInvoiceRepository().addItem(lLinePayment.getRepositoryItem());
          lLinePayments.add(lLinePayment);
          pgGroupToLineItemMap.put(lPaymentGroupId, lLinePayment);
        } catch (RepositoryException ex) {
          String lErrorMessage = String.format("Unable to add Line payment for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
          vlogError (ex, lErrorMessage);
          throw new SalesAuditException (ex, lErrorMessage);
        }
      }
    }
    vlogDebug ("+++++ End Create Line Payments for Split Order : {0}/{1}", lOrderNumber, lOrderId);
    return lLinePayments;
  }

  /**
   * Convert the ATG credit card type to a Fleet Farm Card type.
   *
   * @param pPaymentMethod      ATG Payment method
   * @return
   */
  protected String getMillsPaymentMethod (String pPaymentMethod) {
    String lMillsPaymentType = "VS";
    if (pPaymentMethod.equals("visa"))
      lMillsPaymentType = "VS";
    else if (pPaymentMethod.equals("masterCard"))
      lMillsPaymentType = "MC";
    else if (pPaymentMethod.equals("americanExpress"))
      lMillsPaymentType = "AX";
    else if (pPaymentMethod.equals("discover"))
      lMillsPaymentType = "DS";
    else if (pPaymentMethod.equals("millsCredit"))
      lMillsPaymentType = "PL";
    else if (pPaymentMethod.equals("millsVisa"))
      lMillsPaymentType = "VSC";
    else
      vlogError ("Unable to get a Fleet Farm Payment method for ATG payment methods {0}", pPaymentMethod);
    return lMillsPaymentType;
  }

  @SuppressWarnings("unchecked")
  protected List<AuxiliaryRecord> createPaymentAuxiliarys (MFFOrderImpl pOrder, PaymentGroup pPaymentGroup, String pPaymentType)
      throws SalesAuditException {

    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();

    String lPaymentGroupId = pPaymentGroup == null ? "NULL" : pPaymentGroup.getId();
    vlogDebug ("+++++ Begin Create Payment Auxiliaries for Order : {0}/{1}, payment Group id = {2}, payment type = {3}", lOrderNumber, lOrderId, lPaymentGroupId, pPaymentType);

    AuxiliaryRecord lAuxiliaryRecord = null;
    List<AuxiliaryRecord> lAuxiliaryRecords = new Vector <AuxiliaryRecord> ();

    // Expiration Date
    vlogDebug ("+++++ Create Expiration Date Payment Auxiliaries for Order : {0}/{1}", lOrderNumber, lOrderId);
    String lExpirationDate = "";
    if (pPaymentType.equals("CreditCard")) {
      vlogDebug("Payment type set to credit card");
      CreditCard lCreditCardPaymentGroup = (CreditCard) pPaymentGroup;
      String lExpireMonth = lCreditCardPaymentGroup.getExpirationMonth();
      String lExpireyear  = lCreditCardPaymentGroup.getExpirationYear();
      lExpirationDate = lExpireyear + "-" + lExpireMonth + "-01";
      lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_PAYMENT, SalesAuditConstants.EXPIRATION_DATE, lExpirationDate);
      lAuxiliaryRecords.add(lAuxiliaryRecord);
      vlogDebug("Added an auxilary record {0}", lAuxiliaryRecord.getId());
    }

    // Authorization Number
    vlogDebug ("+++++ Create Authorization Number Payment Auxiliaries for Order : {0}/{1}", lOrderNumber, lOrderId);
    String lAuthorizationNumber = null;
    if (pPaymentType.equals("CreditCard")) {
      CreditCard lCreditCardPaymentGroup = (CreditCard) pPaymentGroup;

      List <PaymentStatus> lCreditCardStatuses = lCreditCardPaymentGroup.getAuthorizationStatus();
      for (PaymentStatus lCreditCardStatus : lCreditCardStatuses) {
        if (lCreditCardStatus.getTransactionSuccess())
          lAuthorizationNumber = lCreditCardStatus.getTransactionId();
          vlogDebug("Authorization number for transaction success {0}", lAuthorizationNumber);
      }
    }
    if (pPaymentType.equals("GiftCard")) {

      MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) pPaymentGroup;
      vlogDebug("gift card payment group id set to {0}", lGiftCardPaymentGroup.getId());
      List<MFFGiftCardPaymentStatus> lMFFGiftCardPaymentStatuses = lGiftCardPaymentGroup.getAuthorizationStatus();
      if (lMFFGiftCardPaymentStatuses != null) {
        for (MFFGiftCardPaymentStatus lMFFGiftCardPaymentStatus : lMFFGiftCardPaymentStatuses) {
          if (lMFFGiftCardPaymentStatus.getTransactionSuccess())
            lAuthorizationNumber = lMFFGiftCardPaymentStatus.getAuthCode();
            vlogDebug("Authorization number for GC set to {0}", lAuthorizationNumber);
        }
      }
    }

    if (lAuthorizationNumber != null) {
      lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_PAYMENT, SalesAuditConstants.AUTHORIZATION_NUMBER, lAuthorizationNumber);
      lAuxiliaryRecords.add(lAuxiliaryRecord);
    } else {
      vlogDebug("Auxillary record not available");
    }

    // Settlement Number
    vlogDebug ("+++++ Create Settlement Number Payment Auxiliaries for Order : {0}/{1}", lOrderNumber, lOrderId);

    String lSettlementId = "";

    if (pPaymentType.equals("CreditCard")) {

      CreditCard lCreditCardPaymentGroup = (CreditCard) pPaymentGroup;
      vlogDebug("Credit card payment group id set to {0}", lCreditCardPaymentGroup.getId());
      List <RepositoryItem> lSettlements = (List<RepositoryItem>) lCreditCardPaymentGroup.getPropertyValue("settlements");

      if (lSettlements != null) vlogDebug("settlement size set to {0}", lSettlements.size());

      if (lSettlements != null && lSettlements.size() > 0) {
        lSettlementId = (String) lSettlements.get(0).getPropertyValue("id");
        vlogDebug("CC Settlement id set to {0}", lSettlementId);
      }

    }
    if (pPaymentType.equals("GiftCard")) {

      MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) pPaymentGroup;
      vlogDebug("Processing gift card group {0}", lGiftCardPaymentGroup.getId());
      List <RepositoryItem> lSettlements = (List<RepositoryItem>) lGiftCardPaymentGroup.getPropertyValue("settlements");

      if (lSettlements != null) vlogDebug("settlement size set to {0}", lSettlements.size());

      if (lSettlements != null && lSettlements.size() > 0) {
        lSettlementId = (String) lSettlements.get(0).getPropertyValue("id");
        vlogDebug("GC Settlement id set to {0}", lSettlementId);
      }
    }
    vlogDebug("Settlement id set to {0}", lSettlementId);

    if(!Strings.isNullOrEmpty(lSettlementId)){
      lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_PAYMENT, SalesAuditConstants.SETTLEMENT_NUMBER, lSettlementId);
      lAuxiliaryRecords.add(lAuxiliaryRecord);
    }
    vlogDebug ("+++++ End create Order Auxiliaries for Order : {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId);
    return lAuxiliaryRecords;
  }

  protected PaymentGroup getPaymentGroup (MFFOrderImpl pOrder, String pPaymentGroupId)
      throws SalesAuditException {
    String lOrderNumber         = pOrder.getOrderNumber();
    String lOrderId             = pOrder.getId();
    PaymentGroup lPaymentGroup  = null;

    try {
      lPaymentGroup = pOrder.getPaymentGroup(pPaymentGroupId);
    } catch (PaymentGroupNotFoundException ex) {
      String lErrorMessage = String.format("Unable to get the payment group (1) for order %s/%s", lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Unable to get the payment group (2) for order %s/%s", lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    return lPaymentGroup;
  }

  /**
   * Create the cartons for the current order.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping group Id
   * @param pItemsToShip        List of Items to ship
   * @return                    List of cartons for the order
   * @throws SalesAuditException
   */
  protected List<Carton> createCartons (MFFOrderImpl pOrder, String pShippingGroupId, List<String> pItemsToShip)
      throws SalesAuditException {
    String lOrderNumber     = pOrder.getOrderNumber();
    String lOrderId         = pOrder.getId();
    vlogDebug ("+++++ Begin Create Line Cartons for Order : {0}/{1}", lOrderNumber, lOrderId);

    Hashtable <String, Carton> lCartonHash = new Hashtable <String, Carton> ();
    List <Carton> lCartons = new Vector <Carton> ();

    // Get Shipping Group
    MFFHardgoodShippingGroup lMFFHardgoodShippingGroup = (MFFHardgoodShippingGroup) getShippingGroup (pOrder, pShippingGroupId);
    Date lShipDate            = getShipDateForShippment(lMFFHardgoodShippingGroup, pOrder, pItemsToShip);
    String lShipVia           = getShipViaFromMethod(lMFFHardgoodShippingGroup.getShippingMethod());

    // Loop through all the items and get payment group(s) for items
    for (String lItemToShip : pItemsToShip) {
      MFFCommerceItemImpl lCommerceItem     = getCommerceItem (pOrder, lItemToShip);

      //2326 - Sending first tracking num from the list
      String lTrackingNumber = lCommerceItem.getTrackingNumber();
      if (lTrackingNumber == null) {
       vlogDebug ("No tracking number found for order %s/%s", lOrderNumber, lOrderId);
       lTrackingNumber = SalesAuditConstants.DEFAULT_TRACKING_NUMBER;
      }else{
        String lTrackingNos[] = lTrackingNumber.split("|");
        if(lTrackingNos.length > 0 && !StringUtils.isEmpty(lTrackingNos[0]))
          lTrackingNumber=lTrackingNos[0];
        else
          lTrackingNumber = SalesAuditConstants.DEFAULT_TRACKING_NUMBER;
      }
      vlogDebug("Tracking Number:{0}, item:{1}", lTrackingNumber,lCommerceItem.getId());

      if (! lCartonHash.contains(lTrackingNumber)) {
          MutableRepositoryItem lMutableRepositoryItem = null;
          try {
            lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_CARTON);
          }
          catch (RepositoryException ex) {
            String lErrorMessage = String.format("Unable to add carton for order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
            vlogError (ex, lErrorMessage);
            throw new SalesAuditException (ex, lErrorMessage);
          }
          Carton lCarton = new Carton(lMutableRepositoryItem);
          lCartonHash.put(lTrackingNumber, lCarton);
      }
      // Set values
      Carton lCarton      = lCartonHash.get(lTrackingNumber);
      lCarton.setTrackingNumber                 (lTrackingNumber);
      lCarton.setCartonNumber                   (lCarton.getId());
      lCarton.setShipDate                       (new Timestamp (lShipDate.getTime()));
      lCarton.setShipVia                        (lShipVia);
      lCarton.setFacilityCd                     (lCommerceItem.getFulfillmentStore());
      lCarton.setWeight                         (0.00);
      lCarton.setCartonSize                     (null);
      lCarton.setCartonType                     (null);
      lCarton.setBillOfLading                   (null);
      lCarton.setProNum                         (null);
      lCarton.setManifestNumber                 (lCarton.getId());
      lCarton.setPickTicket                     (null);
      lCarton.setReturnLabelNumber              (null);
      lCarton.setDeliverConfirmationNumber      (null);
    }
    // Convert Hash into a List and add to repository
    Set<String> lKeys = lCartonHash.keySet();
    for(String lKey: lKeys) {
      Carton lCarton = lCartonHash.get(lKey);
      lCartons.add(lCarton);
      // Add item to repository
      try {
        getInvoiceRepository().addItem(lCarton.getRepositoryItem());
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to add carton for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogDebug ("+++++ End Create Line Cartons for Order : {0}/{1}", lOrderNumber, lOrderId);
    return lCartons;
  }

  /**
   *  Create the summary record for this order
   *  transaction_total           = Total for this order
   *  transactions_taxable_total  = Taxable total
   *  transaction_tax_total       = Tax for this order
   *  line_count                  = Number of lines
   *  payment_total               = Payment total
   *  payment_count               = Number of payments used
   *  discount_total              = Discount total
   *  discount_count              = Number of discounts
   *  giftcard_sold_total         = Gift Cards sold
   *  giftcard_sold_count         = Gift Cards count
   *
   * @param pOrder
   * @param pShippingGroupId
   * @param pItemsToShip
   * @return
   * @throws SalesAuditException
   */
  protected LineSummary createLineSummary (MFFOrderImpl pOrder, String pShippingGroupId, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {
    String lOrderNumber     = pOrder.getOrderNumber();
    String lOrderId         = pOrder.getId();
    vlogDebug ("+++++ Begin create line summary for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_SUMMARY);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line summary for order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    LineSummary lLineSummary = new LineSummary (lMutableRepositoryItem);


    lLineSummary.setTransactionTotal          (pExtractStatistics.getTransactionTotal());


    lLineSummary.setTransactionTaxableTotal   (pExtractStatistics.getTransactionTaxableTotal());

    lLineSummary.setTransactionTaxTotal       (pExtractStatistics.getTransactionTaxTotal());
    lLineSummary.setLineCount                 (pExtractStatistics.getLineCount());

    lLineSummary.setPaymentTotal              (pExtractStatistics.getPaymentTotal());

    lLineSummary.setPaymentCount              (pExtractStatistics.getPaymentCount());
    lLineSummary.setDiscountTotal             (pExtractStatistics.getDiscountTotal());
    lLineSummary.setDiscountCount             (pExtractStatistics.getDiscountCount());
    lLineSummary.setGiftcardSoldTotal         (pExtractStatistics.getGiftcardSoldTotal());
    lLineSummary.setGiftcardSoldCount         (pExtractStatistics.getGiftcardSoldCount());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lLineSummary.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line summary for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End create line summary for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);
    return lLineSummary;
  }


  /**
   * Create a Shipping Address record using the shipping group for the shipped items.
   *
   * @param pOrderId              Order Id
   * @param pOrderNumber          Order Number
   * @param pShippingGroup        Shipping group for items being fulfilled
   * @return                      Invoice Address repository item
   * @throws SalesAuditException
   */
  protected InvoiceAddress createShippingAddress (MFFOrderImpl pOrder, String pShippingGroupId, ContactInfo pAddress)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin createShippingAddress for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_INVOICE_ADDRESS);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Shipping Address for order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    InvoiceAddress lInvoiceAddress = new InvoiceAddress (lMutableRepositoryItem);
    lInvoiceAddress.setType                   (SalesAuditConstants.ADDRESS_TYPE_SHIPPING);
    lInvoiceAddress.setFirstName              (pAddress.getFirstName());
    lInvoiceAddress.setMiddleName             (pAddress.getMiddleName());
    lInvoiceAddress.setLastName               (pAddress.getLastName());
    lInvoiceAddress.setOrganization           (pAddress.getCompanyName());
    lInvoiceAddress.setCompanyName            (pAddress.getCompanyName());
    lInvoiceAddress.setHomePhone              (pAddress.getPhoneNumber());
    lInvoiceAddress.setWorkPhone              (null);
    lInvoiceAddress.setMobilePhone            (null);
    lInvoiceAddress.setFax                    (pAddress.getFaxNumber());
    lInvoiceAddress.setEmail                  (pOrder.getContactEmail());
    lInvoiceAddress.setAddress1               (pAddress.getAddress1());
    lInvoiceAddress.setAddress2               (pAddress.getAddress2());
    lInvoiceAddress.setAddress3               (pAddress.getAddress3());
    lInvoiceAddress.setAddress4               (null);
    lInvoiceAddress.setcity                   (pAddress.getCity());
    lInvoiceAddress.setprovinceCode           (pAddress.getState());
    lInvoiceAddress.setProvince               (null);
    lInvoiceAddress.setPostalCode             (pAddress.getPostalCode());
    lInvoiceAddress.setCountryCode            (pAddress.getCountry());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lInvoiceAddress.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Shipping Address for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End createShippingAddress for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);
    return lInvoiceAddress;
  }



  /**
   * Create a billing address item for the given order/shipping group.
   *
   * @param pOrder                ATG Order
   * @param pShippingGroupId      Shipping Group Id
   * @param pAddress              Address
   * @return                      Billing address
   * @throws SalesAuditException
   */
  protected InvoiceAddress createBillingAddress (MFFOrderImpl pOrder, String pShippingGroupId, ContactInfo pAddress)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin createBillingAddress for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_INVOICE_ADDRESS);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Billing Address for order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    InvoiceAddress lInvoiceAddress = new InvoiceAddress (lMutableRepositoryItem);
    lInvoiceAddress.setType                   (SalesAuditConstants.ADDRESS_TYPE_BILLING);
    lInvoiceAddress.setFirstName              (pAddress.getFirstName());
    lInvoiceAddress.setMiddleName             (pAddress.getMiddleName());
    lInvoiceAddress.setLastName               (pAddress.getLastName());
    lInvoiceAddress.setOrganization           (pAddress.getCompanyName());
    lInvoiceAddress.setCompanyName            (pAddress.getCompanyName());
    lInvoiceAddress.setHomePhone              (pAddress.getPhoneNumber());
    lInvoiceAddress.setWorkPhone              (null);
    lInvoiceAddress.setMobilePhone            (null);
    lInvoiceAddress.setFax                    (pAddress.getFaxNumber());
    //lInvoiceAddress.setEmail                  (pAddress.getEmail());
    lInvoiceAddress.setEmail                  (pOrder.getContactEmail());
    lInvoiceAddress.setAddress1               (pAddress.getAddress1());
    lInvoiceAddress.setAddress2               (pAddress.getAddress2());
    lInvoiceAddress.setAddress3               (pAddress.getAddress3());
    lInvoiceAddress.setAddress4               (null);
    lInvoiceAddress.setcity                   (pAddress.getCity());
    lInvoiceAddress.setprovinceCode           (pAddress.getState());
    lInvoiceAddress.setProvince               (null);
    lInvoiceAddress.setPostalCode             (pAddress.getPostalCode());
    lInvoiceAddress.setCountryCode            (pAddress.getCountry());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lInvoiceAddress.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Billing Address for Order %s/%s ShipGroup: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End createBillingAddress for Order: {0}/{1} ShipGroup: {2}", lOrderNumber, lOrderId, pShippingGroupId);
    return lInvoiceAddress;
  }

  /**
   * Create the shipped lines for the list of commerce items in a given shipping group.
   *
   * @param pOrder                ATG Order
   * @param pShippingGroupId      Shipping Group ID
   * @param pShippingGroupItem    List of commerce items for the shipping group
   * @return
   * @throws SalesAuditException
   */
  public List<ShippedItem> createShippedLines (Order pOrder, String pShippingGroupId, Hashtable <String, List<String>> pShippingGroupItem, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {
    // List of Shipped Items
    List <ShippedItem> lShippedItems = new Vector <ShippedItem> ();

    List<String> lItemsToShip = pShippingGroupItem.get(pShippingGroupId);

    // Calculate the shipping discounts for the lines
    //Commenting out this line as part of 2125 since we
    //don't need shipping discount to be created as part of shipped line item
    //Hashtable <String, Double> lShippingDiscountHash = getShippingLineDiscount (pOrder, pShippingGroupId, lItemsToShip);

    for (String lItemToShip : lItemsToShip) {
      MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
      ShippedItem lShippedItem = createShippedItem (lOrder, pShippingGroupId, lItemToShip, pExtractStatistics);
      lShippedItems.add(lShippedItem);
    }
    return lShippedItems;
  }

  public List<ShippedItem> createShippedLinesForSplitOrder (Order pOrder, String pShippingGroupId, Hashtable <String, List<String>> pShippingGroupItem, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {
    // List of Shipped Items
    List <ShippedItem> lShippedItems = new Vector <ShippedItem> ();

    List<String> lItemsToShip = pShippingGroupItem.get(pShippingGroupId);

    // Calculate the shipping discounts for the lines
    //Commenting out this line as part of 2125 since we
    //don't need shipping discount to be created as part of shipped line item
    //Hashtable <String, Double> lShippingDiscountHash = getShippingLineDiscount (pOrder, pShippingGroupId, lItemsToShip);
    Hashtable <String, ShippedItem> skuToShippedItem = new Hashtable<String, ShippedItem>();

    for (String lItemToShip : lItemsToShip) {
      MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
      MFFCommerceItemImpl lCommerceItem = getCommerceItem (pOrder, lItemToShip);
      String skuId = lCommerceItem.getCatalogRefId();
      if(skuToShippedItem.containsKey(skuId)){
       // do an update here
        updateShippedItem(lOrder, pShippingGroupId, lItemToShip, pExtractStatistics, skuToShippedItem.get(skuId));
      }else{
        ShippedItem lShippedItem = createShippedItem (lOrder, pShippingGroupId, lItemToShip, pExtractStatistics);
        lShippedItems.add(lShippedItem);
        skuToShippedItem.put(skuId, lShippedItem);
      }
    }
    return lShippedItems;
  }

  /**
   * Create a shipped item for a given order/commerce item.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @param pItemToShip         Commerce item to be shipped
   * @return                    ShippedItem
   * @throws SalesAuditException
   */
  protected ShippedItem createShippedItem (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin ShippedItem for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);

    // Get the commerce item
    MFFCommerceItemImpl lCommerceItem = getCommerceItem (pOrder, pItemToShip);
    String lProductId = (String) lCommerceItem.getPropertyValue("productId");

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_SHIPPED_ITEM);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add shipped item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    ShippedItem lShippedItem = new ShippedItem (lMutableRepositoryItem);
    lShippedItem.setExtractLineId             (new Long(lShippedItem.getId()));
    lShippedItem.setClientLineId              (lCommerceItem.getId());

    //if gwp giftcard sku, replace it with the original giftcard sku
    if(!pOrder.isBopisOrder() && lCommerceItem.isGwp() && getGwpGCSkuIds().contains(lCommerceItem.getCatalogRefId())){
      lShippedItem.setSkucode                   (getGwpGCReplacementSkuId());
      lShippedItem.setBarcode                   (getGwpGCReplacementSkuId());
      lShippedItem.setItemNumber                (getGwpGCReplacementSkuId());
    }else{
      lShippedItem.setSkucode                   (lCommerceItem.getCatalogRefId());
      lShippedItem.setBarcode                   (lCommerceItem.getCatalogRefId());
      lShippedItem.setItemNumber                (lCommerceItem.getCatalogRefId());
    }
    String lColorCode = getDynamicProperty (lCommerceItem.getCatalogRefId(), lProductId, "Color");
    if (lColorCode == null)
      lColorCode = "N1";
    lShippedItem.setColorCode                 (lColorCode);
    String lSizeCode = getDynamicProperty (lCommerceItem.getCatalogRefId(), lProductId, "Size");
    if (lSizeCode == null)
      lSizeCode = "N1";
    lShippedItem.setSizeCode                  (lSizeCode);
    lShippedItem.setQuantity                  (lCommerceItem.getQuantity());
    lShippedItem.setUnitPrice                 (lCommerceItem.getPriceInfo().getListPrice());
    lShippedItem.setFacilityCd                (lCommerceItem.getFulfillmentStore());
    lShippedItem.setShippingAmount            (lCommerceItem.getShipping());

    //double giftValue=0.0;
    // if the item is a qualifier
    // reduce gwp item value
    MFFItemPriceInfo priceInfo = (MFFItemPriceInfo)lCommerceItem.getPriceInfo();

    if(lCommerceItem.getGwpPromoId() != null) {
    	// Need a way to handle existing orders in the system before changes related to 2414 is deployed
    	lShippedItem.setExtendedPrice             (priceInfo.getEffectivePrice() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    } else if((Boolean)lCommerceItem.getPropertyValue("gwp")) {
    	// if this is the gwp item
    	lShippedItem.setExtendedPrice             (priceInfo.getEffectivePrice() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    } else {
    	// in all other cases use just the amount
    	lShippedItem.setExtendedPrice             (lCommerceItem.getPriceInfo().getAmount() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    }


    // Get the Tax for the line
    Hashtable <String, Double> lTaxValues = getShippingAndTaxes (lCommerceItem);
    lShippedItem.setLineLocalTax              (lTaxValues.get(SalesAuditConstants.LINE_LOCAL_TAX));
    lShippedItem.setLineCountyTax             (lTaxValues.get(SalesAuditConstants.LINE_COUNTY_TAX));
    lShippedItem.setLineTaxTotal              (lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));
    lShippedItem.setLineStateTax              (lTaxValues.get(SalesAuditConstants.LINE_STATE_TAX));
    lShippedItem.setLineShippingTax           (lTaxValues.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lShippedItem.setLineExtendedTotal         (lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) - lCommerceItem.getPriceInfo().getOrderDiscountShare());

    // Need a way to handle older orders in the system
    if(priceInfo.getEffectivePrice() > 0) {

    } else {
    	if((Boolean)lCommerceItem.getPropertyValue("gwp")) {
    		// for gift items... we need to send the acutal gift value to ReSA
    		if(lCommerceItem.isGiftCard()) {
    			lShippedItem.setLineExtendedTotal         (lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) + lCommerceItem.getGwpGiftCardValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    		} else {
    			lShippedItem.setLineExtendedTotal         (lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) + lCommerceItem.getGwpValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    		}
    	} else {
    		// for all other items, we deduct the gift value from the price
    		// for items that are not qualifiers for a gwp Promo, the gwpValue would be zero
    		// for gwp qualifier items, gwpValue would be non-zero & will be deducted
    		lShippedItem.setLineExtendedTotal         (lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) - lCommerceItem.getGwpValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    	}
    }





    lShippedItem.setLineNumber                (new Long(lShippedItem.getId()));

  //set giftcard number
    if(lCommerceItem.isGiftCard())
      lShippedItem.setGiftCardNumber(lCommerceItem.getGiftCardNumber());

    // Update Statistics
    pExtractStatistics.setLineCount(pExtractStatistics.getLineCount() + 1);
    pExtractStatistics.setTransactionTotal(pExtractStatistics.getTransactionTotal() + lCommerceItem.getPriceInfo().getRawTotalPrice());
    pExtractStatistics.setTransactionTaxTotal(pExtractStatistics.getTransactionTaxTotal() + lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));
    if (lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL) > 0)
     pExtractStatistics.setTransactionTaxableTotal(pExtractStatistics.getTransactionTaxableTotal() + lCommerceItem.getPriceInfo().getRawTotalPrice());
    if (getOmsOrderManager().isGiftCardItem(lCommerceItem)) {
      pExtractStatistics.setGiftcardSoldCount(pExtractStatistics.getGiftcardSoldCount() + lCommerceItem.getQuantity());
      pExtractStatistics.setGiftcardSoldTotal(pExtractStatistics.getGiftcardSoldTotal() + lCommerceItem.getPriceInfo().getAmount()+lCommerceItem.getGwpGiftCardValue());
    }

    // Create the Line Discounts for this item
    List<LineDiscount> lLineDiscounts = createLineDiscounts (pOrder, pShippingGroupId, pItemToShip, null);
    lShippedItem.setLineDiscounts(lLineDiscounts);

    // Create Shipping Line Discounts
    //commenting out this to address 2125.
    //We no longer should be sending the shipping discount as part of the the shipped discount
    /**
    if (pShippingDiscountHash.get(pItemToShip) != null) {
      double lShippingLineDiscountAmount = pShippingDiscountHash.get(pItemToShip).doubleValue();
      if (lShippingLineDiscountAmount > 0) {
        LineDiscount lShippingLineDiscount = createShippingLineDiscount (pOrder, pShippingGroupId, pItemToShip, lShippingLineDiscountAmount);
        if (lShippingLineDiscount != null) {
          lLineDiscounts.add(lShippingLineDiscount);
          lShippedItem.setLineDiscounts(lLineDiscounts);
          lShippedItem.setLineExtendedTotal(lCommerceItem.getShipping());
          lShippedItem.setExtendedPrice(lCommerceItem.getShipping());
        }
      }
    }
    */

    // Add to totals
    for (LineDiscount lLineDiscount : lLineDiscounts) {
      pExtractStatistics.setDiscountCount             (pExtractStatistics.getDiscountCount() + 1);
      pExtractStatistics.setDiscountTotal             (roundPrice(pExtractStatistics.getDiscountTotal() + (lLineDiscount.getAmount()*lShippedItem.getQuantity())));
      pExtractStatistics.setTransactionTotal          (roundPrice(pExtractStatistics.getTransactionTotal() - (lLineDiscount.getAmount()*lShippedItem.getQuantity())));
      if (pExtractStatistics.getTransactionTaxableTotal() > 0) {
        pExtractStatistics.setTransactionTaxableTotal (roundPrice((pExtractStatistics.getTransactionTaxableTotal() - (lLineDiscount.getAmount()*lShippedItem.getQuantity()))));
      }
    }

    // Create the line cartons for this item
    List<LineCarton> lLineCartons = createLineCartons (pOrder, pShippingGroupId, pItemToShip);
    lShippedItem.setLineCartons(lLineCartons);

    // Create Auxillaries
    List <AuxiliaryRecord> lAuxiliaryRecords = createLineAuxiliarys (pOrder, pShippingGroupId, pItemToShip);
    lShippedItem.setAuxiliaries(lAuxiliaryRecords);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lShippedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add shipped item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End ShippedItem for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    return lShippedItem;
  }

  protected ShippedItem updateShippedItem (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip, ExtractStatistics pExtractStatistics,ShippedItem pShippedItem)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin ShippedItem for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);

    // Get the commerce item
    MFFCommerceItemImpl lCommerceItem = getCommerceItem (pOrder, pItemToShip);
    /*String lProductId = (String) lCommerceItem.getPropertyValue("productId");

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().getItemForUpdate(pShippedItem.getRepositoryItem().getRepositoryId(), SalesAuditConstants.ITEM_SHIPPED_ITEM);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add shipped item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    ShippedItem lShippedItem = new ShippedItem (lMutableRepositoryItem);*/
    ShippedItem lShippedItem = pShippedItem;

    //lShippedItem.setExtractLineId             (new Long(lShippedItem.getId()));
    //lShippedItem.setClientLineId              (lCommerceItem.getId());

    //if gwp giftcard sku, replace it with the original giftcard sku
    /*if(!pOrder.isBopisOrder() && lCommerceItem.isGwp() && getGwpGCSkuIds().contains(lCommerceItem.getCatalogRefId())){
      lShippedItem.setSkucode                   (getGwpGCReplacementSkuId());
      lShippedItem.setBarcode                   (getGwpGCReplacementSkuId());
      lShippedItem.setItemNumber                (getGwpGCReplacementSkuId());
    }else{
      lShippedItem.setSkucode                   (lCommerceItem.getCatalogRefId());
      lShippedItem.setBarcode                   (lCommerceItem.getCatalogRefId());
      lShippedItem.setItemNumber                (lCommerceItem.getCatalogRefId());
    }

    String lColorCode = getDynamicProperty (lCommerceItem.getCatalogRefId(), lProductId, "Color");
    if (lColorCode == null)
      lColorCode = "N1";
    lShippedItem.setColorCode                 (lColorCode);
    String lSizeCode = getDynamicProperty (lCommerceItem.getCatalogRefId(), lProductId, "Size");
    if (lSizeCode == null)
      lSizeCode = "N1";

    lShippedItem.setSizeCode                  (lSizeCode);
    lShippedItem.setUnitPrice                 (lCommerceItem.getPriceInfo().getListPrice());
    lShippedItem.setFacilityCd                (lCommerceItem.getFulfillmentStore());*/

    lShippedItem.setQuantity                  (lShippedItem.getQuantity() + lCommerceItem.getQuantity());
    lShippedItem.setShippingAmount            (lShippedItem.getShippingAmount() + lCommerceItem.getShipping());

    //double giftValue=0.0;
    // if the item is a qualifier
    // reduce gwp item value

    MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo)lCommerceItem.getPriceInfo();

    if(itemPriceInfo.getEffectivePrice() > 0) {

    	if((Boolean)lCommerceItem.getPropertyValue("gwp")) {
    		lShippedItem.setExtendedPrice             (lShippedItem.getExtendedPrice() + itemPriceInfo.getEffectivePrice() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    	} else {
    		lShippedItem.setExtendedPrice             (lShippedItem.getExtendedPrice() + lCommerceItem.getPriceInfo().getAmount() - itemPriceInfo.getDiscountAmount() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    	}
    } else {
    	if(lCommerceItem.getGwpPromoId() != null) {
    		lShippedItem.setExtendedPrice             (lShippedItem.getExtendedPrice() + lCommerceItem.getPriceInfo().getAmount() - lCommerceItem.getGwpValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    	} else if((Boolean)lCommerceItem.getPropertyValue("gwp")) {
    		// if this is the gwp item
    		// use the GWP value instead
    		if(lCommerceItem.isGiftCard()) {
    			lShippedItem.setExtendedPrice             (lShippedItem.getExtendedPrice() + lCommerceItem.getGwpGiftCardValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    		} else {
    			lShippedItem.setExtendedPrice             (lShippedItem.getExtendedPrice() + lCommerceItem.getGwpValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    		}

    	} else {
    		// in all other cases use just the amount
    		lShippedItem.setExtendedPrice             (lShippedItem.getExtendedPrice() + lCommerceItem.getPriceInfo().getAmount() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    	}
    }



/*    if(lCommerceItem.getGwpPromoId() != null) {
      giftValue = getOmsOrderManager().getGiftValueForPromo(pOrder, lCommerceItem.getGwpPromoId(),false);
      lShippedItem.setExtendedPrice             (lCommerceItem.getPriceInfo().getAmount() - giftValue - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    } else if((Boolean)lCommerceItem.getPropertyValue("gwp")) {
      Iterator adjIter = lCommerceItem.getPriceInfo().getAdjustments().iterator();
      while(adjIter.hasNext()) {
        PricingAdjustment adj = (PricingAdjustment)adjIter.next();
        if(adj.getAdjustmentDescription().equalsIgnoreCase("Item Discount")) {
          giftValue += -1 * adj.getTotalAdjustment();
        }
      }
      lShippedItem.setExtendedPrice             (giftValue - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    }else {
      lShippedItem.setExtendedPrice             (lCommerceItem.getPriceInfo().getAmount() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    }*/

    // Get the Tax for the line
    Hashtable <String, Double> lTaxValues = getShippingAndTaxes (lCommerceItem);
    lShippedItem.setLineLocalTax              (lShippedItem.getLineLocalTax()  + lTaxValues.get(SalesAuditConstants.LINE_LOCAL_TAX));
    lShippedItem.setLineCountyTax             (lShippedItem.getLineCountyTax() + lTaxValues.get(SalesAuditConstants.LINE_COUNTY_TAX));
    lShippedItem.setLineStateTax              (lShippedItem.getLineStateTax() + lTaxValues.get(SalesAuditConstants.LINE_STATE_TAX));
    lShippedItem.setLineTaxTotal              (lShippedItem.getLineTaxTotal() + lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));
    lShippedItem.setLineShippingTax           (lShippedItem.getLineShippingTax() + lTaxValues.get(SalesAuditConstants.LINE_SHIPPING_TAX));

    MFFItemPriceInfo priceInfo = (MFFItemPriceInfo)lCommerceItem.getPriceInfo();
    // Need a way to handle existing orders in the system
    if(priceInfo.getEffectivePrice() > 0) {
    	lShippedItem.setLineExtendedTotal         (lShippedItem.getLineExtendedTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    } else {
    	if((Boolean)lCommerceItem.getPropertyValue("gwp")) {
    		// for gift items... we need to send the acutal gift value to ReSA
    		if(lCommerceItem.isGiftCard()) {
    			lShippedItem.setLineExtendedTotal         (lShippedItem.getLineExtendedTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) + lCommerceItem.getGwpGiftCardValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    		} else {
    			lShippedItem.setLineExtendedTotal         (lShippedItem.getLineExtendedTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) + lCommerceItem.getGwpValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    		}
    	} else {
    		// for all other items, we deduct the gift value from the price
    		// for items that are not qualifiers for a gwp Promo, the gwpValue would be zero
    		// for gwp qualifier items, gwpValue would be non-zero & will be deducted
    		lShippedItem.setLineExtendedTotal         (lShippedItem.getLineExtendedTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL) - lCommerceItem.getGwpValue() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
    	}
    }



    //lShippedItem.setLineNumber                (new Long(lShippedItem.getId()));

    //set giftcard number
    if(lCommerceItem.isGiftCard())
      lShippedItem.setGiftCardNumber(lCommerceItem.getGiftCardNumber());

    // Update Statistics
    //pExtractStatistics.setLineCount(pExtractStatistics.getLineCount() + 1);
    pExtractStatistics.setTransactionTotal(pExtractStatistics.getTransactionTotal() + lCommerceItem.getPriceInfo().getRawTotalPrice());
    pExtractStatistics.setTransactionTaxTotal(pExtractStatistics.getTransactionTaxTotal() + lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));
    if (lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL) > 0)
     pExtractStatistics.setTransactionTaxableTotal(pExtractStatistics.getTransactionTaxableTotal() + lCommerceItem.getPriceInfo().getRawTotalPrice());
    if (getOmsOrderManager().isGiftCardItem(lCommerceItem)) {
      pExtractStatistics.setGiftcardSoldCount(pExtractStatistics.getGiftcardSoldCount() + lCommerceItem.getQuantity());
      pExtractStatistics.setGiftcardSoldTotal(pExtractStatistics.getGiftcardSoldTotal() + lCommerceItem.getPriceInfo().getAmount());
    }

    // Create the Line Discounts for this item
    List<LineDiscount> lLineDiscounts = createLineDiscounts (pOrder, pShippingGroupId, pItemToShip, null);
    lShippedItem.setLineDiscounts(lLineDiscounts);

    // Add to totals
    for (LineDiscount lLineDiscount : lLineDiscounts) {
      pExtractStatistics.setDiscountTotal             (roundPrice(pExtractStatistics.getDiscountTotal() + (lLineDiscount.getAmount())));
      pExtractStatistics.setTransactionTotal          (roundPrice(pExtractStatistics.getTransactionTotal() - (lLineDiscount.getAmount())));
      if (pExtractStatistics.getTransactionTaxableTotal() > 0) {
        pExtractStatistics.setTransactionTaxableTotal (roundPrice((pExtractStatistics.getTransactionTaxableTotal() - (lLineDiscount.getAmount()))));
      }
    }

    // Create the line cartons for this item
    //List<LineCarton> lLineCartons = createLineCartons (pOrder, pShippingGroupId, pItemToShip);
    //lShippedItem.setLineCartons(lLineCartons);
    for (LineCarton lLineCarton : pShippedItem.getLineCartons()){
      lLineCarton.setQuantity                   (lLineCarton.getQuantity() + lCommerceItem.getQuantity());

      try {
        getInvoiceRepository().updateItem(lLineCarton.getRepositoryItem());
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to update carton line item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }

    }

    // Create Auxillaries
    //List <AuxiliaryRecord> lAuxiliaryRecords = createLineAuxiliarys (pOrder, pShippingGroupId, pItemToShip);
    //lShippedItem.setAuxiliaries(lAuxiliaryRecords);

    // Add item to repository
    try {
      getInvoiceRepository().updateItem(lShippedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to update shipped item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End ShippedItem for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    return lShippedItem;
  }


  protected List<LineDiscount> createPriceOverrideDiscounts (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip, ReturnItem pReturnItem)
	      throws SalesAuditException {
	  String lOrderNumber   = pOrder.getOrderNumber();
	  String lOrderId       = pOrder.getId();
	  double lPriceOverride = 0.0;
	  long lQty             = 1;

	  List <LineDiscount> lLineDiscounts = new Vector <LineDiscount> ();

	  vlogDebug ("+++++ Begin Price Override Line Discount for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);

	  // Get the commerce item
	  MFFCommerceItemImpl lCommerceItem = getCommerceItem (pOrder, pItemToShip);
	  MFFItemPriceInfo lItemPriceInfo = (MFFItemPriceInfo) lCommerceItem.getPriceInfo();
	  int lNumberOfOverrides = 0;
	  long lQtyAdjusted = 0;


	  //lQty = lCommerceItem.getQuantity();

	  List <PricingAdjustment> lPricingAdjustments = lItemPriceInfo.getAdjustments();
	  for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
		  if (lPricingAdjustment.getAdjustmentDescription() != null && lPricingAdjustment.getAdjustmentDescription().equals("Agent price override")) {
			  lPriceOverride += lPricingAdjustment.getTotalAdjustment();
			  lQtyAdjusted += lPricingAdjustment.getQuantityAdjusted();
			  lNumberOfOverrides ++;
		  }
	  }


	  if(lPriceOverride != 0.0 && lNumberOfOverrides > 0) {
		  lPriceOverride = roundDiscount(lPriceOverride/(lQtyAdjusted/lNumberOfOverrides));
		  LineDiscount lLineDiscount  = createPriceOverrideDiscount (pOrder, -1*lPriceOverride, pReturnItem);
		  lLineDiscounts.add(lLineDiscount);
	  }


	  vlogDebug ("+++++ End Price Override Line Discount for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
	  return lLineDiscounts;
  }

  protected LineDiscount createPriceOverrideDiscount (MFFOrderImpl pOrder,double pPriceOveride, ReturnItem pReturnItem) throws SalesAuditException {
	  String lOrderNumber = pOrder.getOrderNumber();
	  String lOrderId     = pOrder.getId();
	  vlogDebug ("+++++ Begin Price Overide Line Discount for Order: {0}/{1} ", lOrderNumber, lOrderId);


	  MutableRepositoryItem lMutableRepositoryItem = null;
	  try {
		  lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_DISCOUNT);
	  } catch (RepositoryException ex) {
		  String lErrorMessage = String.format("Unable to add line discount for order %s/%s ", lOrderNumber, lOrderId);
		  vlogError (ex, lErrorMessage);
		  throw new SalesAuditException (ex, lErrorMessage);
	  }
	  LineDiscount lLineDiscount = new LineDiscount (lMutableRepositoryItem);

	  lLineDiscount.setAmount             (pPriceOveride);
	  lLineDiscount.setDiscountCode       ("default");
	  lLineDiscount.setSource             (SalesAuditConstants.DISCOUNT_SOURCE);
	  lLineDiscount.setDiscountType       (SalesAuditConstants.DISCOUNT_TYPE);

	  // Add item to repository
	  try {
		  getInvoiceRepository().addItem(lLineDiscount.getRepositoryItem());
	  } catch (RepositoryException ex) {
		  String lErrorMessage = String.format("Unable to add line discount for order %s/%s ", lOrderNumber, lOrderId);
		  vlogError (ex, lErrorMessage);
		  throw new SalesAuditException (ex, lErrorMessage);
	  }
	  vlogDebug ("+++++ End Price Overide Line Discount for Order: {0}/{1} ", lOrderNumber, lOrderId);
	  return lLineDiscount;
  }

  /**
   * Create the line discounts for the given order
   *
   * @param pOrder            ATG Order
   * @param pShippingGroupId  Shipping Group Id
   * @param pItemToShip       Commerce Item ID
   * @return                  List of Line Discounts
   * @throws SalesAuditException
   */
  @SuppressWarnings("unchecked")
  protected List<LineDiscount> createLineDiscounts (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip, ReturnItem pReturnItem)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();



    List <LineDiscount> lLineDiscounts = new Vector <LineDiscount> ();

    vlogDebug ("+++++ Begin Line Discount for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);

    // Get the commerce item
    MFFCommerceItemImpl lCommerceItem = getCommerceItem (pOrder, pItemToShip);
    MFFItemPriceInfo lItemPriceInfo = (MFFItemPriceInfo) lCommerceItem.getPriceInfo();

    // Get Order Discounts
    if (lItemPriceInfo.getOrderDiscountShare() > 0) {
      double lOrderDiscountShare = lItemPriceInfo.getOrderDiscountShare();
      LineDiscount lLineDiscount  = createLineDiscount (pOrder, pShippingGroupId, pItemToShip, null, lOrderDiscountShare,lCommerceItem, pReturnItem);
      lLineDiscounts.add(lLineDiscount);
    }

    // check if there are any item discounts
    if(lItemPriceInfo.getDiscountAmount() > 0) {
    	// this must either be a gwp qualifier
    	// or a gwp item. We need a discount record
    	// only for a gwp qualifier or a gwp item that is NOT a gift card
    	// gwp items that are gift cards do not have a discount record
    	// because their gift value is distributed only between the qualifiers

	    /**
	     * Example:
	     * Get any 3 items in Leaf Blower category get $10 GC.
	     * Here 3 items are qualifiers to receive the $10 GC
	     * Each of the 3 items will need a lineDiscount record
	     * The value of the $10 is split between the three items
	     * item.priceInfo.discountAmount will have the right split discount value
	     * and only the 3 qualifiers get the discount record
	     *
	     * Example:
	     * Get 1 X Get Y Free where Y is a non-gift card sku
	     * The value of Y (the discount) is split between both
	     * X & Y (the qualifier and the gift)
	     * Both will get a discount record for the share of discount
	     * they received
	     *
	     */
	    if(lCommerceItem.getGwpPromoId() != null) {
	        LineDiscount lLineDiscount  = createLineDiscount(pOrder, pItemToShip, lCommerceItem, pReturnItem);
	        lLineDiscounts.add(lLineDiscount);
	    }
    }
    if(lItemPriceInfo.getEffectivePrice() > 0) {
	    // Check if there are any Item Discounts on this item
	    // Here we deal only with non GWP promotions
	    // Buy 2 Jeans Get 1 Free is an example of an Item Discount that is not a GWP
	    // We create lineDiscount entries for those
	    List <PricingAdjustment> lPricingAdjustments = lItemPriceInfo.getAdjustments();

	    if(isProcessPriceOverrides()) {
		    for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
		    	if (lPricingAdjustment.getTotalAdjustment() < 0 &&
		    			!lPricingAdjustment.getAdjustmentDescription().equals("Sale price") &&
		    			!lPricingAdjustment.getAdjustmentDescription().equalsIgnoreCase("Agent price override")) {
		    		if(!(Boolean)lCommerceItem.getPropertyValue("gwp")) {
		    			LineDiscount lLineDiscount  = createLineDiscount (pOrder, pItemToShip, getItemDiscountCode(lPricingAdjustment),lCommerceItem, pReturnItem);
		    			lLineDiscounts.add(lLineDiscount);
		    		}
		    	}
		    }
	    } else {
		    for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
		    	if (lPricingAdjustment.getTotalAdjustment() < 0 &&
		    			!lPricingAdjustment.getAdjustmentDescription().equals("Sale price")) {
		    		if(!(Boolean)lCommerceItem.getPropertyValue("gwp")) {
		    			LineDiscount lLineDiscount  = createLineDiscount (pOrder, pItemToShip, getItemDiscountCode(lPricingAdjustment),lCommerceItem, pReturnItem);
		    			lLineDiscounts.add(lLineDiscount);
		    		}
		    	}
		    }
	    }

	    /**
	     * if the item is a gwp, then it might have a share of the discount as well
	     * add its share of discount
	     */
    } else {
        List <PricingAdjustment> lPricingAdjustments = lItemPriceInfo.getAdjustments();
        if(isProcessPriceOverrides()) {
        	for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
        		if (lPricingAdjustment.getTotalAdjustment() < 0 &&
        				!lPricingAdjustment.getAdjustmentDescription().equals("Sale price") &&
        				!lPricingAdjustment.getAdjustmentDescription().equalsIgnoreCase("Agent price override")) {
        			//if(!(Boolean)lCommerceItem.getPropertyValue("gwp") && !lPricingAdjustment.getAdjustmentDescription().equals("Item Discount")) {
        			if(!(Boolean)lCommerceItem.getPropertyValue("gwp")) {
        				LineDiscount lLineDiscount  = createLineDiscount (pOrder, pShippingGroupId, pItemToShip, lPricingAdjustment, 0.00,lCommerceItem, pReturnItem);
        				lLineDiscounts.add(lLineDiscount);
        			}
        		}
        	}
        } else {
        	for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
        		if (lPricingAdjustment.getTotalAdjustment() < 0 &&
        				!lPricingAdjustment.getAdjustmentDescription().equals("Sale price")) {
        			//if(!(Boolean)lCommerceItem.getPropertyValue("gwp") && !lPricingAdjustment.getAdjustmentDescription().equals("Item Discount")) {
        			if(!(Boolean)lCommerceItem.getPropertyValue("gwp")) {
        				LineDiscount lLineDiscount  = createLineDiscount (pOrder, pShippingGroupId, pItemToShip, lPricingAdjustment, 0.00,lCommerceItem, pReturnItem);
        				lLineDiscounts.add(lLineDiscount);
        			}
        		}
        	}
        }


        // if item is a qualifier for a GWP promo
    	// we need the lineDiscount on the qualifier but not on the actual gift item
        if(lCommerceItem.getGwpPromoId() != null) {
        	ArrayList <MFFCommerceItemImpl> giftItems = new ArrayList<MFFCommerceItemImpl>();
        	MFFCommerceItemImpl giftItem=null;
        	MFFItemPriceInfo lGiftItemPriceInfo=null;

        	// get list of commerceItems for which this item is a qualifier
        	giftItems = getOmsOrderManager().getGiftItemsForPromo(pOrder, lCommerceItem.getGwpPromoId());

        	Iterator<MFFCommerceItemImpl> giftIter = giftItems.iterator();
        	while(giftIter.hasNext()) {
        		giftItem = (MFFCommerceItemImpl)giftIter.next();
        		lGiftItemPriceInfo = (MFFItemPriceInfo) giftItem.getPriceInfo();
        	    lPricingAdjustments = lGiftItemPriceInfo.getAdjustments();

        	    // find the adjustment with the promo id set for this item
        	    for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
        	      if (lPricingAdjustment.getPricingModel() != null && lPricingAdjustment.getPricingModel().getRepositoryId().equalsIgnoreCase(lCommerceItem.getGwpPromoId())) {
        	        LineDiscount lLineDiscount  = createLineDiscount (pOrder, pShippingGroupId, pItemToShip, lPricingAdjustment, 0.00,lCommerceItem,pReturnItem);
        	        lLineDiscounts.add(lLineDiscount);
        	      }
        	    }
        	}

        }
    }

    // If there is a manual override
    // the overridden price is the only discount that will be sent
    if(isProcessPriceOverrides()) {
    	vlogDebug("Handling Price Overrides turned on");
    	if(lItemPriceInfo.getFinalReasonCode() != null && lItemPriceInfo.getFinalReasonCode().equalsIgnoreCase("manuallyApplied")) {
    		vlogDebug("Item {0} has a manual price override ", pItemToShip);
    		List <LineDiscount> lPriceOverrideDiscounts = createPriceOverrideDiscounts(pOrder, pShippingGroupId, pItemToShip, pReturnItem);
    		lLineDiscounts.addAll(lPriceOverrideDiscounts);
    	}
    }
    /**
     * if the item has a discount & not gwpPromoId, then its some sort of
     * salePrice adjustment
     */

    // Create salePrice discount records
    if (lItemPriceInfo.isOnSale()) {
    // Create Price Mark downs
      double lListPrice     = lItemPriceInfo.getListPrice();
      double lSalePrice     = lItemPriceInfo.getSalePrice();
      double lMarkdown      = roundPrice(lListPrice - lSalePrice);
      String lMarkdownCode  = lItemPriceInfo.getSalePricePromoId();

      if (lMarkdownCode == null)
        lMarkdownCode = "DEF";
      if (lMarkdown > 0) {
        LineDiscount lLineDiscount  = createLineMarkdown (pOrder, pShippingGroupId, pItemToShip, lMarkdown, lMarkdownCode);
        lLineDiscounts.add(lLineDiscount);
      }
    }

    vlogDebug ("+++++ End Line Discount for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    return lLineDiscounts;
  }

  protected LineDiscount createLineDiscount (MFFOrderImpl pOrder, String pItemToShip, MFFCommerceItemImpl pCommerceItem, ReturnItem pReturnItem)
      throws SalesAuditException {
	    String lOrderNumber = pOrder.getOrderNumber();
	    String lOrderId     = pOrder.getId();
	    MFFItemPriceInfo priceInfo = (MFFItemPriceInfo)pCommerceItem.getPriceInfo();

	    vlogDebug ("+++++ Begin Line Discount for Order: {0}/{1} Item: {2}", lOrderNumber, lOrderId, pItemToShip);

	    MutableRepositoryItem lMutableRepositoryItem = null;
	    try {
	      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_DISCOUNT);
	    } catch (RepositoryException ex) {
	      String lErrorMessage = String.format("Unable to add line discount for order %s/%s Item: %s", lOrderNumber, lOrderId, pItemToShip);
	      vlogError (ex, lErrorMessage);
	      throw new SalesAuditException (ex, lErrorMessage);
	    }
	    LineDiscount lLineDiscount = new LineDiscount (lMutableRepositoryItem);

	    long giftShare=pCommerceItem.getQuantity(); //2
	    double lDiscountAmount = priceInfo.getDiscountAmount();
	    if(pReturnItem != null) {
	    	giftShare = pCommerceItem.getQuantity() + pCommerceItem.getReturnedQuantity();
	    }


	    if(giftShare > 1) {
	    	if(pReturnItem == null) {
	    		vlogInfo("pReturnItem is null. In shipping flow. lAdjustmentAmount {0} giftShare {1} ", lDiscountAmount, giftShare);
	    		lDiscountAmount = roundDiscount(lDiscountAmount/giftShare);
	    		vlogInfo("New Pricing Adjustment Share split for order:{0} and commerce item:{1}, qty:{2} is :{3}",pOrder.getId(),pCommerceItem.getId(),giftShare, lDiscountAmount);
	    	} else {

	    		Set<String> lReturnedItemIds = ((MFFReturnItem)pReturnItem).getProRatedItemIds();
	    		lDiscountAmount = 0.0;
	    		if(lReturnedItemIds != null){
	    			for (String lReturnedItemId : lReturnedItemIds) {
	    				RepositoryItem lRepositoryItem = getProrateItemById (lReturnedItemId);
	    				lDiscountAmount += (Double)lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_GWP_AMOUNT);
	    			}
	    			lDiscountAmount = roundDiscount(lDiscountAmount/pReturnItem.getQuantityReceived());
	    		}
	    	}
	    }

	    lLineDiscount.setAmount             (lDiscountAmount);
	    lLineDiscount.setDiscountCode       (pCommerceItem.getGwpPromoId());
	    lLineDiscount.setSource             (SalesAuditConstants.DISCOUNT_SOURCE);
	    lLineDiscount.setDiscountType       (SalesAuditConstants.DISCOUNT_TYPE);

	    // Add item to repository
	    try {
	      getInvoiceRepository().addItem(lLineDiscount.getRepositoryItem());
	    } catch (RepositoryException ex) {
	      String lErrorMessage = String.format("Unable to add line discount for order %s/%s Item: %s", lOrderNumber, lOrderId, pItemToShip);
	      vlogError (ex, lErrorMessage);
	      throw new SalesAuditException (ex, lErrorMessage);
	    }
	    vlogDebug ("+++++ End Line Discount for Order: {0}/{1} Item: {2}", lOrderNumber, lOrderId, pItemToShip);

	    return lLineDiscount;
  }

  /**
   * This is for item discount promos (non GWP kind)
   *
   * @param pOrder
   * @param pItemToShip
   * @param discount
   * @param promoId
   * @param pCommerceItem
   * @param pReturnItem
   * @return
   * @throws SalesAuditException
   */
  protected LineDiscount createLineDiscount (MFFOrderImpl pOrder, String pItemToShip, String promoId,
		  MFFCommerceItemImpl pCommerceItem, ReturnItem pReturnItem)
      throws SalesAuditException {
	    String lOrderNumber = pOrder.getOrderNumber();
	    String lOrderId     = pOrder.getId();
	    MFFItemPriceInfo priceInfo = (MFFItemPriceInfo)pCommerceItem.getPriceInfo();
	    vlogDebug ("+++++ Begin Line Discount for Order: {0}/{1} Item: {2}", lOrderNumber, lOrderId, pItemToShip);

	    MutableRepositoryItem lMutableRepositoryItem = null;
	    try {
	      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_DISCOUNT);
	    } catch (RepositoryException ex) {
	      String lErrorMessage = String.format("Unable to add line discount for order %s/%s Item: %s", lOrderNumber, lOrderId, pItemToShip);
	      vlogError (ex, lErrorMessage);
	      throw new SalesAuditException (ex, lErrorMessage);
	    }
	    LineDiscount lLineDiscount = new LineDiscount (lMutableRepositoryItem);
	    long giftShare=pCommerceItem.getQuantity(); //2
	    double lDiscountAmount = priceInfo.getDiscountAmount();
	    if(pReturnItem != null) {
	    	giftShare = pCommerceItem.getQuantity() + pCommerceItem.getReturnedQuantity();
	    }


	    if(giftShare > 1) {
	    	if(pReturnItem == null) {
	    		vlogInfo("pReturnItem is null. In shipping flow. lAdjustmentAmount {0} giftShare {1} ", lDiscountAmount, giftShare);
	    		lDiscountAmount = roundDiscount(lDiscountAmount/giftShare);
	    		vlogInfo("New Pricing Adjustment Share split for order:{0} and commerce item:{1}, qty:{2} is :{3}",pOrder.getId(),pCommerceItem.getId(),giftShare, lDiscountAmount);
	    	} else {

	    		Set<String> lReturnedItemIds = ((MFFReturnItem)pReturnItem).getProRatedItemIds();
	    		lDiscountAmount = 0.0;
	    		if(lReturnedItemIds != null){
	    			for (String lReturnedItemId : lReturnedItemIds) {
	    				RepositoryItem lRepositoryItem = getProrateItemById (lReturnedItemId);
	    				lDiscountAmount += (Double)lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_GWP_AMOUNT);
	    			}
	    			lDiscountAmount = roundDiscount(lDiscountAmount/pReturnItem.getQuantityReceived());
	    		}
	    	}
	    }
	    lLineDiscount.setAmount             (lDiscountAmount);
	    lLineDiscount.setDiscountCode       (promoId);
	    lLineDiscount.setSource             (SalesAuditConstants.DISCOUNT_SOURCE);
	    lLineDiscount.setDiscountType       (SalesAuditConstants.DISCOUNT_TYPE);

	    // Add item to repository
	    try {
	      getInvoiceRepository().addItem(lLineDiscount.getRepositoryItem());
	    } catch (RepositoryException ex) {
	      String lErrorMessage = String.format("Unable to add line discount for order %s/%s Item: %s", lOrderNumber, lOrderId, pItemToShip);
	      vlogError (ex, lErrorMessage);
	      throw new SalesAuditException (ex, lErrorMessage);
	    }
	    vlogDebug ("+++++ End Line Discount for Order: {0}/{1} Item: {2}", lOrderNumber, lOrderId, pItemToShip);

	    return lLineDiscount;
  }
  /**
   * Create a line discount for a given discount/commerce item
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @param pItemToShip         Commerce Item to ship
   * @param pPricingAdjustment  Pricing adjustment
   * @return                    Line Discount
   * @throws SalesAuditException
   */
  protected LineDiscount createLineDiscount (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip, PricingAdjustment pPricingAdjustment, double pOrderDiscountShare,
		  MFFCommerceItemImpl pCommerceItem, ReturnItem pReturnItem)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Line Discount for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);


    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_DISCOUNT);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line discount for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    LineDiscount lLineDiscount = new LineDiscount (lMutableRepositoryItem);

    if (pPricingAdjustment != null) {
      Double lAdjustmentAmount = pPricingAdjustment.getTotalAdjustment(); // 169.98
      //ArrayList <MFFCommerceItemImpl> qualifiers = new ArrayList<MFFCommerceItemImpl>();
      double proratedAdjustmentAmount = lAdjustmentAmount;
      long giftShare=pCommerceItem.getQuantity(); //2
      if(pReturnItem != null) {
    	  giftShare = pCommerceItem.getQuantity() + pCommerceItem.getReturnedQuantity();
      }


      if(giftShare > 1) {
    	  if(pReturnItem == null) {
    		  vlogInfo("pReturnItem is null. In shipping flow. lAdjustmentAmount {0} giftShare {1} ", lAdjustmentAmount, giftShare);
    		  proratedAdjustmentAmount = roundDiscount(lAdjustmentAmount/giftShare);
    		  vlogInfo("New Pricing Adjustment Share split for order:{0} and commerce item:{1}, qty:{2} is :{3}",pOrder.getId(),pCommerceItem.getId(),giftShare, proratedAdjustmentAmount);
    	  } else {

    		  Set<String> lReturnedItemIds = ((MFFReturnItem)pReturnItem).getProRatedItemIds();

    		  if(lReturnedItemIds != null){
    			  for (String lReturnedItemId : lReturnedItemIds) {
    				  RepositoryItem lRepositoryItem = getProrateItemById (lReturnedItemId);
    				  proratedAdjustmentAmount += (Double)lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_GWP_AMOUNT);
    			  }
    			  proratedAdjustmentAmount = roundDiscount(proratedAdjustmentAmount/pReturnItem.getQuantityReceived());
    		  }
    	  }
      }
      lLineDiscount.setAmount             (proratedAdjustmentAmount * -1);
      lLineDiscount.setDiscountCode       (getItemDiscountCode(pPricingAdjustment));
      lLineDiscount.setSource             (SalesAuditConstants.DISCOUNT_SOURCE);
      lLineDiscount.setDiscountType       (SalesAuditConstants.DISCOUNT_TYPE);
    }
    else {

      if (pCommerceItem.getQuantity() > 1) {
        pOrderDiscountShare = roundDiscount(pOrderDiscountShare / pCommerceItem.getQuantity());
        vlogInfo("New Order Discount Share split for order:{0} and commerce item:{1}, qty:{2} is :{3}", pOrder.getId(), pCommerceItem.getId(), pCommerceItem.getQuantity(), pOrderDiscountShare);
      }
      lLineDiscount.setAmount             (pOrderDiscountShare);
      
      // Emp Discounts code
      if(hasEmployeeDiscount(pOrder)) {
    	  lLineDiscount.setDiscountType       (SalesAuditConstants.EMP_DISCOUNT_TYPE);
      } else {
    	  lLineDiscount.setDiscountType       (SalesAuditConstants.ORDER_DISCOUNT_TYPE);  
      }
      lLineDiscount.setDiscountCode       (SalesAuditConstants.DISCOUNT_CODE);
      lLineDiscount.setSource             (SalesAuditConstants.DISCOUNT_SOURCE);
      
    }

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lLineDiscount.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line discount for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Line Discount for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    return lLineDiscount;
  }
  
  protected boolean hasEmployeeDiscount(Order pOrder) {
	  boolean hasEmpDiscount = false;
	  String lCode;
	  List <PricingAdjustment> lPricingAdjustments = pOrder.getPriceInfo().getAdjustments();
	  for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
		  if (lPricingAdjustment.getAdjustmentDescription() != null && lPricingAdjustment.getAdjustmentDescription().equals("Order Discount")) {
			    RepositoryItem lPriceModel    = lPricingAdjustment.getPricingModel();
			    if (lPriceModel != null) {
			      lCode      = (String) lPriceModel.getPropertyValue("id");
			      if(lCode != null && lCode.toLowerCase().startsWith("emp")) {
			    	  return true;
			      }
			    }
		  }
	  }	  
	  return hasEmpDiscount;
  }

  /**
   * Create a markdown discount line for promotional markdowns.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group ID
   * @param pItemToShip         Commerce Item to ship
   * @param pMarkdownAmount     Markdown amount
   * @param pMarkdownCode       Markdown code
   * @return                    Line Discount
   * @throws SalesAuditException
   */
  protected LineDiscount createLineMarkdown (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip, double pMarkdownAmount, String pMarkdownCode)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Line Markdown for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_DISCOUNT);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line discount for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    LineDiscount lLineDiscount = new LineDiscount (lMutableRepositoryItem);
    lLineDiscount.setAmount             (pMarkdownAmount);
    lLineDiscount.setDiscountCode       (pMarkdownCode);
    lLineDiscount.setSource             (SalesAuditConstants.DISCOUNT_SOURCE);
    lLineDiscount.setDiscountType       (SalesAuditConstants.DISCOUNT_TYPE);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lLineDiscount.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line Markdown for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Line Markdown for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    return lLineDiscount;
  }



  /**
   * The following method is used to determine the line discounts applied towards the shipping charges
   * @param pOrder the order associated with shipment
   * @param pItemsToShip the list of commerce item ids that are being shipped
   * @return a consolidated line discount which is sum of all the shipping charges
   * @throws SalesAuditException when the creation of the item discount fails
   */
  protected LineDiscount createShippingLineDisount(MFFOrderImpl pOrder, List<String> pItemsToShip) throws SalesAuditException {
    vlogDebug("Entering createShippingLineDisount : pOrder, pShippingGroupId, pItemsToShip");
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    MutableRepositoryItem lMutableRepositoryItem = null;
    LineDiscount lLineDiscount = null;

    double lDiscountAmount = 0.0d;

    //First loop through the items and determine the total
    //shipping discount that was applied towards the item
    for (String lItemToShip : pItemsToShip) {
      MFFCommerceItemImpl lCommerceItemImpl = getCommerceItem(pOrder, lItemToShip);
      lDiscountAmount = lDiscountAmount + lCommerceItemImpl.getShippingDiscount();
    }


    vlogDebug("Total shipping discounts set to {0}", lDiscountAmount);
    if (lDiscountAmount > 0.0) {

      try {
        lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_DISCOUNT);
      } catch (RepositoryException e) {
        String lErrorMessage = String.format("Unable to add Shipping line discount for order %s/%s Items: %s", lOrderNumber, lOrderId, pItemsToShip);
        vlogError(e, lErrorMessage);
        throw new SalesAuditException(e, lErrorMessage);

      }

      lLineDiscount = new LineDiscount(lMutableRepositoryItem);

      lLineDiscount.setAmount(roundDiscount(lDiscountAmount));
      lLineDiscount.setDiscountCode(SalesAuditConstants.SHIP_DISCOUNT_CODE);
      lLineDiscount.setSource(SalesAuditConstants.SHIP_DISCOUNT_SOURCE);
      lLineDiscount.setDiscountType(SalesAuditConstants.SHIP_DISCOUNT_TYPE);

      try {
        getInvoiceRepository().addItem(lMutableRepositoryItem);
      } catch (RepositoryException e) {
        String lErrorMessage = String.format("Unable to add line discount for order number: %s order id: %s items %s", lOrderNumber, lOrderId, pItemsToShip);
        vlogError(e, lErrorMessage);
        throw new SalesAuditException(lErrorMessage, e);
      }
    }
    vlogDebug("Exiting createShippingLineDisount : pOrder, pShippingGroupId, pItemsToShip");
    return lLineDiscount;
  }
  /**
   * Create the line cartons for the given commerce item
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping group Id
   * @param pItemToShip         Commerce Item Id
   * @return                    List of Cartons
   * @throws SalesAuditException
   */
  protected List<LineCarton> createLineCartons (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip)
      throws SalesAuditException {
    List <LineCarton> lLineCartons = new Vector <LineCarton> ();
    LineCarton lLineCarton =  createLineCarton (pOrder, pShippingGroupId, pItemToShip);
    lLineCartons.add(lLineCarton);
    return lLineCartons;
  }

  /**
   * Create the line carton for the given commerce item
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping group Id
   * @param pItemToShip         Commerce Item Id
   * @return                    List of Cartons
   * @throws SalesAuditException
   */
  protected LineCarton createLineCarton (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Line Cartons for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);

    // Get the commerce item
    MFFCommerceItemImpl lCommerceItem = getCommerceItem (pOrder, pItemToShip);
    MFFHardgoodShippingGroup lMFFHardgoodShippingGroup = (MFFHardgoodShippingGroup) getShippingGroup (pOrder, pShippingGroupId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_CARTON);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line carton for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    String lTrackingNumber = lCommerceItem.getTrackingNumber();
    if (lTrackingNumber == null) {
      vlogInfo ("No tracking number found for order %s/%s", lOrderNumber, lOrderId);
      lTrackingNumber = SalesAuditConstants.DEFAULT_TRACKING_NUMBER;
     }
    LineCarton lLineCarton = new LineCarton (lMutableRepositoryItem);
    lLineCarton.setTrackingNumber             (lTrackingNumber);
    lLineCarton.setShipVia                    (getShipViaFromMethod(lMFFHardgoodShippingGroup.getShippingMethod()));
    lLineCarton.setQuantity                   (lCommerceItem.getQuantity());
    lLineCarton.setDeliverConfirmationNumber  (null);
    lLineCarton.setSerialNumber               (null);
    lLineCarton.setCartonNumber               (lLineCarton.getId());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lLineCarton.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line carton for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Line carton for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    return lLineCarton;
  }

  protected LineCarton createLineCarton (MFFOrderImpl pOrder, String pShippingGroupId)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Line Cartons for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId);

    MFFHardgoodShippingGroup lMFFHardgoodShippingGroup = (MFFHardgoodShippingGroup) getShippingGroup (pOrder, pShippingGroupId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_LINE_CARTON);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line carton for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    String lTrackingNumber = SalesAuditConstants.DEFAULT_TRACKING_NUMBER;
    long defaultQty = 1;
    LineCarton lLineCarton = new LineCarton (lMutableRepositoryItem);
    lLineCarton.setTrackingNumber             (lTrackingNumber);
    lLineCarton.setShipVia                    (getShipViaFromMethod(lMFFHardgoodShippingGroup.getShippingMethod()));
    lLineCarton.setQuantity                   (defaultQty);
    lLineCarton.setDeliverConfirmationNumber  (null);
    lLineCarton.setSerialNumber               (null);
    lLineCarton.setCartonNumber               (lLineCarton.getId());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lLineCarton.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add line carton for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Line carton for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId);
    return lLineCarton;
  }

  /**
   * Get a list of Line Auxiliarys for a shipped line.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @param pItemToShip         Commerce Item
   * @return                    List of Auxiliaries for the line
   * @throws SalesAuditException
   */
  protected List<AuxiliaryRecord> createLineAuxiliarys (MFFOrderImpl pOrder, String pShippingGroupId, String pItemToShip)
      throws SalesAuditException {

    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Create Line Auxiliary Records for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);

    // Get the commerce item
    MFFCommerceItemImpl lCommerceItem = getCommerceItem (pOrder, pItemToShip);
    String lProductId = (String) lCommerceItem.getPropertyValue("productId");
    vlogDebug ("***** SKU is 5s product Id is %s ", pItemToShip, lProductId);

    AuxiliaryRecord lAuxiliaryRecord = null;
    List<AuxiliaryRecord> lAuxiliaryRecords = new Vector <AuxiliaryRecord> ();

    // Taxable
    vlogDebug ("+++++ Create Taxable Auxiliary Record for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    String lTaxable = "false";
    if (lCommerceItem.getTaxPriceInfo() != null && lCommerceItem.getTaxPriceInfo().getAmount() > 0)
      lTaxable = "true";
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_TAXABLE, lTaxable);
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    // Brand
    vlogDebug ("+++++ Create Brand Auxiliary Record for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    String lBrand = "N/A";
    lBrand= getProductProperty (lProductId, "brand");
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_BRAND, lBrand);
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    // Gift Certificate
    vlogDebug ("+++++ Create GC Auxiliary Record for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    String lGiftCertificate = "false";
    boolean isGiftCardItem = getOmsOrderManager().isGiftCardItem(lCommerceItem);
    lGiftCertificate = new Boolean (isGiftCardItem).toString();
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_GIFT_CERT, lGiftCertificate);
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    // Cost of the Gift Certificate
    vlogDebug ("+++++ Create Cost Auxiliary Record for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    String lCost = "0.00";
    if (isGiftCardItem)
      lCost = new Double(lCommerceItem.getPriceInfo().getAmount()).toString();
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_COST, lCost);
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    vlogDebug ("+++++ Create Line Auxiliary Records for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pItemToShip);
    return lAuxiliaryRecords;
  }


  /**
   * Get a list of Line Auxiliarys for a shipped line.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @param pItemToShip         Commerce Item
   * @return                    List of Auxiliaries for the line
   * @throws SalesAuditException
   */
  protected List<AuxiliaryRecord> createLineAuxiliarys(MFFOrderImpl pOrder, String pTaxable)
      throws SalesAuditException {

    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Create Line Auxiliary Records for Order: {0}/{1}", lOrderNumber, lOrderId);

    AuxiliaryRecord lAuxiliaryRecord = null;
    List<AuxiliaryRecord> lAuxiliaryRecords = new Vector <AuxiliaryRecord> ();

    // Taxable
    vlogDebug ("+++++ Create Taxable Auxiliary Record for Order: {0}/{1}", lOrderNumber, lOrderId);

    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_TAXABLE, pTaxable);
    lAuxiliaryRecords.add(lAuxiliaryRecord);
    // Brand
    vlogDebug ("+++++ Create Brand Auxiliary Record for Order: {0}/{1}", lOrderNumber, lOrderId);
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_BRAND, " ");
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    // Gift Certificate
    vlogDebug ("+++++ Create GC Auxiliary Record for Order: {0}/{1}", lOrderNumber, lOrderId);
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_GIFT_CERT, "false");
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    // Cost of the Gift Certificate
    vlogDebug ("+++++ Create Cost Auxiliary Record for Order: {0}/{1}", lOrderNumber, lOrderId);
    lAuxiliaryRecord = createAuxiliary (pOrder, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT, SalesAuditConstants.AUXILIARY_TYPE_SHIPMENT_COST, "0.00");
    lAuxiliaryRecords.add(lAuxiliaryRecord);

    vlogDebug ("+++++ Create Line Auxiliary Records for Order: {0}/{1}", lOrderNumber, lOrderId);
    return lAuxiliaryRecords;
  }

  /**
   * Create a Auxiliary Record for a given type, name and value
   *
   * @param pOrder            ATG Order
   * @param pType             Auxiliary Type
   * @param pAuxiliaryName    Auxiliary Name
   * @param pAuxiliaryValue   Auxiliary Value
   * @return                  Auxiliary Record
   * @throws SalesAuditException
   */
  protected AuxiliaryRecord createAuxiliary (MFFOrderImpl pOrder, String pType, String pAuxiliaryName, String pAuxiliaryValue)
      throws SalesAuditException {
    String lOrderNumber = pOrder.getOrderNumber();
    String lOrderId     = pOrder.getId();
    vlogDebug ("+++++ Begin Line Auxiliary for Order: {0}/{1} Type: {2} Name: {3} Value: {4}", lOrderNumber, lOrderId, pType, pAuxiliaryName, pAuxiliaryValue);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_ANCILLIARY);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add auxiliary for order %s/%s Type: %s Name: %s Value %s", lOrderNumber, lOrderId, pType, pAuxiliaryName, pAuxiliaryValue);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    AuxiliaryRecord lAncilliary = new AuxiliaryRecord(lMutableRepositoryItem);
    lAncilliary.setType                       (pType);
    lAncilliary.setAuxilliaryName             (pAuxiliaryName);
    lAncilliary.setAuxilliaryValue            (pAuxiliaryValue);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lAncilliary.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add auxiliary for order %s/%s Type: %s Name: %s Value %s", lOrderNumber, lOrderId, pType, pAuxiliaryName, pAuxiliaryValue);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Line Auxiliary for Order: {0}/{1} Type: {2} Name: {3} Value: {4}", lOrderNumber, lOrderId, pType, pAuxiliaryName, pAuxiliaryValue);
    return lAncilliary;
  }


  /**
   * Get the list of payment groups that are funding this commerce item.  This method is
   * no longer needed, as we are passed the payment groups/amounts in the shipment call.
   *
   * @param pOrder            ATG Order
   * @param lCommerceItemId   Commerce Item ID
   * @return                  A list of the payment groups for the item
   */
  protected List<PaymentGroup> getPaymentGroupsForItem (Order pOrder, String lCommerceItemId) {
    // The relationships do not yet exist.  Return all payment groups for the order at this time.

    return null;
  }

  /**
   * Get the shipping address for a given order.
   *
   * @param pOrder                ATG Order
   * @param pShippingGroupId      Shipping group Id
   * @return                      Address Object
   * @throws SalesAuditException
   */
  protected Address getShippingAddressForOrder (Order pOrder, String pShippingGroupId)
      throws SalesAuditException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();

    ShippingGroup lShippingGroup;
    try {
      lShippingGroup = pOrder.getShippingGroup(pShippingGroupId);
    } catch (ShippingGroupNotFoundException ex) {
      String lErrorMessage = String.format("Shipping Group %s for Order %s/%s is not found", pShippingGroupId, lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Shipping Group %s for Order %s/%s - Invalid Parameter", pShippingGroupId, lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (lErrorMessage);
    }
    if (!(lShippingGroup instanceof HardgoodShippingGroup)) {
      String lErrorMessage = String.format("Shipping Group %s for Order %s/%s is not the right type", pShippingGroupId, lOrderNumber, lOrderId);
      vlogError (lErrorMessage);
      throw new SalesAuditException (lErrorMessage);
    }
    Address lAddress = ((HardgoodShippingGroup) lShippingGroup).getShippingAddress();
    return lAddress;
  }

  /**
   * Get the billing address for the order by extracting the address from the payment groups.  As
   * gift cards do not have an address, we will use the shipping address.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group ID
   * @return                    Billing Address
   * @throws SalesAuditException
   */
  @SuppressWarnings("unchecked")
  protected Address getBillingAddressForOrder (Order pOrder, String pShippingGroupId)
      throws SalesAuditException {
    ShippingGroup lShippingGroup = getShippingGroup (pOrder, pShippingGroupId);

    Address lBillingAddress   = null;
    Address lShippingAddress  = null;

    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();

    // Pull Shipping Address as default
    if (lShippingGroup instanceof HardgoodShippingGroup) {
      lShippingAddress = ((HardgoodShippingGroup) lShippingGroup).getShippingAddress();
    }
	List<PaymentGroup> paymentGroups = pOrder.getPaymentGroups();
	PaymentGroup pg = null;
	 Address lPayBillingAddress   = null;
	for (int i = 0; i < paymentGroups.size(); i++) {
		pg = (PaymentGroup) paymentGroups.get(i);
		if (pg instanceof CreditCard) {
			lPayBillingAddress = ((CreditCard) pg).getBillingAddress();
		}
	}
   
    List<PaymentGroupRelationship> lPaymentGroupRelationships = lShippingGroup.getPaymentGroupRelationships();
    for (PaymentGroupRelationship lPaymentGroupRelationship : lPaymentGroupRelationships) {
      PaymentGroup lPaymentGroup = lPaymentGroupRelationship.getPaymentGroup();
      if (lPaymentGroup instanceof CreditCard) {
        Address lAddress = ((CreditCard) lPaymentGroup).getBillingAddress();
        if (lAddress != null)
          lBillingAddress = lAddress;
      }
      if (lPaymentGroup instanceof MFFGiftCardPaymentGroup) {
        vlogDebug ("Gift card found for order {0}/{1} - No address available", lOrderNumber, lOrderId);
      }
    }
    
    if (lBillingAddress == null)
    	if(lPayBillingAddress==null) {
    		return lShippingAddress;
    	}else {
    		return lPayBillingAddress;
    	}
    else
      return lBillingAddress;
  }

  /**
   * Get the commerce item object for a given commerce item ID.
   *
   * @param pOrder                ATG Order
   * @param pCommerceItemId       Commerce Item ID
   * @return                      Commerce Item
   * @throws SalesAuditException
   */
  protected MFFCommerceItemImpl getCommerceItem(Order pOrder, String pCommerceItemId)
      throws SalesAuditException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();

    MFFCommerceItemImpl lCommerceItem;
    try {
      lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(pCommerceItemId);
    } catch (CommerceItemNotFoundException ex) {
      String lErrorMessage = String.format("Commerce item ID %s not found for Order %s/%s - Item not found", pCommerceItemId, lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Commerce item ID %s not found for Order %s/%s - Invalid parameter", pCommerceItemId, lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (lErrorMessage);
    }
    return lCommerceItem;
  }

  /**
   * Get the shipping group for a given shipping group Id.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping Group Id
   * @return                    Shipping Group Object
   * @throws SalesAuditException
   */
  protected ShippingGroup getShippingGroup (Order pOrder, String pShippingGroupId)
      throws SalesAuditException {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();

    ShippingGroup lShippingGroup;
    try {
      lShippingGroup = (ShippingGroup) pOrder.getShippingGroup(pShippingGroupId);
    } catch (ShippingGroupNotFoundException ex) {
      String lErrorMessage = String.format("Shipping Group ID %s not found for Order %s/%s - Item not found", pShippingGroupId, lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Shipping Group ID %s not found for Order %s/%s - Invalid parameter", pShippingGroupId, lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (lErrorMessage);
    }
    return lShippingGroup;
  }

  protected String getItemDiscountCode (PricingAdjustment pPricingAdjustment) {
    String lCode          = "default";

    /** Return the promotion name and ID **/
    RepositoryItem lPriceModel    = pPricingAdjustment.getPricingModel();
    if (lPriceModel != null)
      lCode      = (String) lPriceModel.getPropertyValue("id");

    if (StringUtils.isNotBlank(lCode) && (lCode.endsWith("_a") || lCode.endsWith("_b"))) {
      lCode = lCode.substring(0, lCode.length()-2);
    }

    if (lCode.length() > 10) {
      vlogWarning("Promotion code is more than 10 characters in length {0}", lCode);
      lCode = lCode.substring(0, 10);
      vlogWarning("New promotion code set to {0}", lCode);
    }
    return lCode;
  }


  /**
   * Get the value of a SKU property given the SKU Id.
   *
   * @param pSkuId        SKU Id
   * @param pProperty     Property Name
   * @return              Property Value
   */
  protected String getSkuProperty(String pSkuId, String pProperty) {
    RepositoryItem lSkuItem = null;
    try {
      lSkuItem = getCatalogTools().findSKU(pSkuId);
    } catch (RepositoryException ex) {
      vlogError ("Unable to get {0} for SKU: {1}", pProperty, pSkuId);
      return null;
    }
    if (lSkuItem.getPropertyValue(pProperty) != null)
       return (String) lSkuItem.getPropertyValue(pProperty);
    else
      return " ";
  }

  /**
   * Get a property for a given product
   *
   * @param pProductId    Product ID
   * @param pProperty     Property Name
   * @return              Property Value
   */
  protected String getProductProperty(String pProductId, String pProperty) {
    RepositoryItem lProductItem = null;
    try {
      lProductItem = getCatalogTools().findProduct(pProductId);
    } catch (RepositoryException ex) {
      vlogError ("Unable to get {0} for Product: {1}", pProperty, pProductId);
      return null;
    }
    if (lProductItem.getPropertyValue(pProperty) != null)
       return (String) lProductItem.getPropertyValue(pProperty);
    else
      return " ";
  }

  /**
   * Get the Tax for a given item in the order.
   *
   * @param pCommerceItem       Commerce Item
   * @return                    Hash table of tax values
   */
  protected Hashtable <String, Double> getShippingAndTaxes (MFFCommerceItemImpl pCommerceItem) {
    Hashtable <String, Double> lLineTax = new Hashtable <String, Double> ();

    // Item Tax
    double lLineLocalTax          = 0.00;
    double lLineCountyTax         = 0.00;
    double lLineStateTax          = 0.00;
    double lLineShipping          = pCommerceItem.getShipping();
    double lLineShippingLocalTax  = pCommerceItem.getShippingCityTax();
    double lLineShippingCountyTax = pCommerceItem.getShippingCountyTax();
    double lLineShippingStateTax  = pCommerceItem.getShippingStateTax();
    double lLineTotalTax          = 0.00;
    double lLineShippingTax       = pCommerceItem.getShippingTax();
    double lLineExtendedTotal     = pCommerceItem.getShippingTax();
    double lItemPrice             = 0.00;

    TaxPriceInfo lTaxPriceInfo = pCommerceItem.getTaxPriceInfo();
    if(lTaxPriceInfo != null){
      lLineLocalTax      = roundPrice (lLineLocalTax      + lTaxPriceInfo.getCityTax());
      lLineCountyTax     = roundPrice (lLineCountyTax     + lTaxPriceInfo.getCountyTax());
      lLineStateTax      = roundPrice (lLineStateTax      + lTaxPriceInfo.getStateTax());
      lLineTotalTax      = roundPrice(lLineTotalTax      + lTaxPriceInfo.getAmount());
    }


    MFFItemPriceInfo lItemPriceInfo = (MFFItemPriceInfo) pCommerceItem.getPriceInfo();

    // Need to way to handle existing orders in the system
    if(lItemPriceInfo.getEffectivePrice() > 0) {
        lItemPrice = lItemPriceInfo.getAmount();
        if(pCommerceItem.isGwp()) {
        	lItemPrice = lItemPriceInfo.getEffectivePrice();
        } else {
        	lItemPrice = lItemPrice - lItemPriceInfo.getDiscountAmount();
        }
        lLineExtendedTotal = roundPrice (lItemPrice + lLineTotalTax);
    } else {
    	lLineExtendedTotal = roundPrice (pCommerceItem.getPriceInfo().getAmount() + lLineTotalTax);
    }

    // Copy values to the hash table
    lLineTax.put(SalesAuditConstants.LINE_LOCAL_TAX,            lLineLocalTax);
    lLineTax.put(SalesAuditConstants.LINE_COUNTY_TAX,           lLineCountyTax);
    lLineTax.put(SalesAuditConstants.LINE_STATE_TAX,            lLineStateTax);
    lLineTax.put(SalesAuditConstants.LINE_TAX_TOTAL,            lLineTotalTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING,             lLineShipping);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,   lLineShippingLocalTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX,  lLineShippingCountyTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,   lLineShippingStateTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_TAX,         lLineShippingTax);
    lLineTax.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,       lLineExtendedTotal);
    return lLineTax;
  }

  protected Hashtable <String, Double> getShippingAndTaxes (MFFOrderImpl pOrder, List<String> pItemsToShip, String pShippingGroupId)
      throws SalesAuditException {

    Hashtable <String, Double> lShippingAndTaxes = new Hashtable <String, Double> ();
    Hashtable <String, List<String>> lShippingGroupItem = null;

    lShippingAndTaxes.put(SalesAuditConstants.LINE_LOCAL_TAX,           0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_COUNTY_TAX,          0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_STATE_TAX,           0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING,            0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,  0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX, 0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,  0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_TAX_TOTAL,           0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_TAX,        0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,      0.00D);


    try {
      lShippingGroupItem = getOmsOrderManager().getItemForShipGroups (pOrder, pItemsToShip);
    } catch (CommerceException e) {
      String lErrorMsg = String.format("Could not get the shipping groups for the items that are being shipped for %s/%s", pOrder.getId(), pOrder.getOrderNumber());
      vlogError(e, lErrorMsg);
      throw new SalesAuditException(e, lErrorMsg);
    }

    // Get the list of commerce items for the shipping group
    List<String> lCommerceItemIds = lShippingGroupItem.get(pShippingGroupId);

    for (String lCommerceItemId : lCommerceItemIds) {

      MFFCommerceItemImpl lCommerceItem                = getCommerceItem(pOrder, lCommerceItemId);
      Hashtable <String, Double> lLineShippingAndTaxes = getShippingAndTaxes (lCommerceItem);

      lShippingAndTaxes.put(SalesAuditConstants.LINE_LOCAL_TAX,           lShippingAndTaxes.get(SalesAuditConstants.LINE_LOCAL_TAX)             + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_LOCAL_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_COUNTY_TAX,          lShippingAndTaxes.get(SalesAuditConstants.LINE_COUNTY_TAX)            + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_COUNTY_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_STATE_TAX,           lShippingAndTaxes.get(SalesAuditConstants.LINE_STATE_TAX)             + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_STATE_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING,            lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING)              + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,  lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX)    + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX, lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX)   + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,  lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX)    + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_TAX_TOTAL,           lShippingAndTaxes.get(SalesAuditConstants.LINE_TAX_TOTAL)             + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_TAX_TOTAL));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_TAX,        lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_TAX)          + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,      lShippingAndTaxes.get(SalesAuditConstants.LINE_EXTENDED_TOTAL)        + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    }

    Set<String> lKeys = lShippingAndTaxes.keySet();
    for(String lKey: lKeys) {
      lShippingAndTaxes.put(lKey, roundPrice (lShippingAndTaxes.get(lKey)));
    }
    return lShippingAndTaxes;
  }

  protected Hashtable <String, Double> getShippingAndTaxesForSplitOrder (MFFOrderImpl pOrder, List<String> pItemsToShip, String pShippingGroupId)
      throws SalesAuditException {

    Hashtable <String, Double> lShippingAndTaxes = new Hashtable <String, Double> ();
    //Hashtable <String, List<String>> lShippingGroupItem = null;

    lShippingAndTaxes.put(SalesAuditConstants.LINE_LOCAL_TAX,           0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_COUNTY_TAX,          0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_STATE_TAX,           0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING,            0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,  0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX, 0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,  0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_TAX_TOTAL,           0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_TAX,        0.00D);
    lShippingAndTaxes.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,      0.00D);


    /*try {
      lShippingGroupItem = getOmsOrderManager().getItemForShipGroups (pOrder, pItemsToShip);
    } catch (CommerceException e) {
      String lErrorMsg = String.format("Could not get the shipping groups for the items that are being shipped for %s/%s", pOrder.getId(), pOrder.getOrderNumber());
      vlogError(e, lErrorMsg);
      throw new SalesAuditException(e, lErrorMsg);
    }

    // Get the list of commerce items for the shipping group
    List<String> lCommerceItemIds = lShippingGroupItem.get(pShippingGroupId);*/

    for (String lCommerceItemId : pItemsToShip) {

      MFFCommerceItemImpl lCommerceItem                = getCommerceItem(pOrder, lCommerceItemId);
      Hashtable <String, Double> lLineShippingAndTaxes = getShippingAndTaxes (lCommerceItem);

      lShippingAndTaxes.put(SalesAuditConstants.LINE_LOCAL_TAX,           lShippingAndTaxes.get(SalesAuditConstants.LINE_LOCAL_TAX)             + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_LOCAL_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_COUNTY_TAX,          lShippingAndTaxes.get(SalesAuditConstants.LINE_COUNTY_TAX)            + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_COUNTY_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_STATE_TAX,           lShippingAndTaxes.get(SalesAuditConstants.LINE_STATE_TAX)             + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_STATE_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING,            lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING)              + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,  lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX)    + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX, lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX)   + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,  lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX)    + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_TAX_TOTAL,           lShippingAndTaxes.get(SalesAuditConstants.LINE_TAX_TOTAL)             + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_TAX_TOTAL));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_SHIPPING_TAX,        lShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_TAX)          + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_SHIPPING_TAX));
      lShippingAndTaxes.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,      lShippingAndTaxes.get(SalesAuditConstants.LINE_EXTENDED_TOTAL)        + lLineShippingAndTaxes.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    }

    Set<String> lKeys = lShippingAndTaxes.keySet();
    for(String lKey: lKeys) {
      lShippingAndTaxes.put(lKey, roundPrice (lShippingAndTaxes.get(lKey)));
    }
    return lShippingAndTaxes;
  }

  /**
   * Get the tax Exemption Certificate for the order
   *
   * @param pOrder          ATG Order
   * @return                Hash of Tax Exemption Values
   */
  protected Hashtable <String, String> getTaxExemptionCertificate (MFFOrderImpl pOrder) {
    String lTaxExemptionCode = null;
    MFFOrderImpl lOrder      = (MFFOrderImpl) pOrder;
    String lOrderNumber      = lOrder.getOrderNumber();
    String lOrderId          = lOrder.getId();
    vlogDebug ("Get Tax Exemption data for order {0}/{1}", lOrderNumber, lOrderId);

    // Setup the Hash with default values
    Hashtable <String, String> lTaxExemptionHash = new Hashtable <String, String> ();
    lTaxExemptionHash.put(SalesAuditConstants.TAX_EXEMPTION_CODE, "");
    lTaxExemptionHash.put(SalesAuditConstants.TAX_EXEMPTION_CERT, "");
    lTaxExemptionHash.put(SalesAuditConstants.TAX_EXEMPTION_NAME, "");

    // Return if the order does not have a Tax exemption code
    lTaxExemptionCode = pOrder.getTaxExemptionName();
    if (lTaxExemptionCode == null) {
      vlogDebug ("Tax Exemption code is null - returning default values");
      return lTaxExemptionHash;
    }

    // Get the tax classification info from the profile
    getTaxExemptionFromProfile (lTaxExemptionHash, pOrder.getProfileId(), lTaxExemptionCode);

    return lTaxExemptionHash;
  }

  /**
   * Pull the tax exemption data from the users profile
   *
   * @param pTaxExemptionHash     Hash of tax exemption Values
   * @param pProfileId            Profile ID
   * @param pExemptionCode        Tax Exemption code
   */
  @SuppressWarnings("unchecked")
  protected void getTaxExemptionFromProfile (Hashtable <String, String> pTaxExemptionHash, String pProfileId, String pExemptionCode) {
    // Load the profile for this user
    Profile lProfile = new Profile();
    getProfileTools().locateUserFromId(pProfileId, lProfile);

    // Find the exempt code
    Map <String, RepositoryItem> lTaxExemptions = (Map <String, RepositoryItem>) lProfile.getPropertyValue("taxExemptions");
    for (Map.Entry<String, RepositoryItem> lEntry : lTaxExemptions.entrySet())  {
      String lExemptionCode = (String) lEntry.getValue().getPropertyValue("classificationCode");
      if (lExemptionCode.equals(pExemptionCode)) {
        pTaxExemptionHash.put(SalesAuditConstants.TAX_EXEMPTION_CODE, (String) lEntry.getValue().getPropertyValue("classificationCode"));
        pTaxExemptionHash.put(SalesAuditConstants.TAX_EXEMPTION_CERT, (String) lEntry.getValue().getPropertyValue("taxId"));
        pTaxExemptionHash.put(SalesAuditConstants.TAX_EXEMPTION_NAME, (String) lEntry.getValue().getPropertyValue("classificationName"));
      }
    }
  }

  /**
   * Get a dynamic property for a given product/SKU
   *
   * @param pSkuId              SKU Id
   * @param pProductId          Product Id
   * @param pDynamicProperty    Dynamic property name
   * @return                    Dynamic property value
   */
  @SuppressWarnings("unchecked")
  protected String getDynamicProperty (String pSkuId, String pProductId, String pDynamicProperty) {
    // Get the list of dynamic attributes for the product
    RepositoryItem lProductItem = null;
    try {
      lProductItem = getCatalogTools().findProduct(pProductId);
    } catch (RepositoryException ex) {
      vlogError ("Unable to get dynamic Attributes for Product: {1}", pProductId);
      return null;
    }

    // Check to see if the Dynamic attributes contain color <0=Size, 1=Color>
    Map <Object, Object> lDynamicAttributes = (Map<Object, Object>) lProductItem.getPropertyValue("dynamicAttributes");
    if (lDynamicAttributes == null) {
      vlogDebug ("No Dynamic properties values found for product %s", pProductId);
      return null;
    }
    String lKey = null;
    for (Map.Entry<Object, Object> lEntry : lDynamicAttributes.entrySet())  {
      String lValue = (String) lEntry.getValue();
      if (lValue.equals (pDynamicProperty))
        lKey = (String) lEntry.getKey();
    }

    // Get the dynamic attributes for the SKU
    RepositoryItem lSkuItem = null;
    try {
      lSkuItem = getCatalogTools().findSKU(pSkuId);
    } catch (RepositoryException ex) {
      vlogError ("Unable to get dynamicAttributes for SKU: {1}", pSkuId);
      return null;
    }

    // Get color value
    lDynamicAttributes = (Map<Object, Object>) lSkuItem.getPropertyValue("dynamicAttributes");
    if (lDynamicAttributes == null) {
      vlogDebug ("No Dynamic properties values found for SKU %s", pSkuId);
      return null;
    }
    String lPropertyValue = null;
    for (Map.Entry<Object, Object> lEntry : lDynamicAttributes.entrySet())  {
      String lValue = (String) lEntry.getKey();
      if (lValue.equals (lKey))
        lPropertyValue = (String) lEntry.getValue();
    }

    //trim if the color code size is more than 10
    if(pDynamicProperty!=null && (pDynamicProperty.equalsIgnoreCase("Color") || pDynamicProperty.equalsIgnoreCase("Size"))){
      if(lPropertyValue!=null && lPropertyValue.length() > 10){
        lPropertyValue = lPropertyValue.substring(0,10);
      }
    }

    return lPropertyValue;
  }

  /**
   * Get the shipping line discounts.
   *
   * @param pOrder              ATG Order
   * @param pShippingGroupId    Shipping group ID
   * @param pItemsToShip        Items to ship
   * @return                    Hash of shipping Discounts
   * @throws SalesAuditException
   */
  protected Hashtable <String, Double> getShippingLineDiscount (Order pOrder, String pShippingGroupId, List<String> pItemsToShip)
      throws SalesAuditException {

    Hashtable <String, Double> lShippingDiscountHash = new Hashtable <String, Double> ();

    if (pItemsToShip != null && pItemsToShip.size() > 0) {
      for (String lCommerceItemId : pItemsToShip) {

        MFFCommerceItemImpl lCommerceItemImpl = getCommerceItem(pOrder, lCommerceItemId);
        double shippingDiscount = lCommerceItemImpl.getShippingDiscount();
        lShippingDiscountHash.put(lCommerceItemId, shippingDiscount);
      }
    }
    return lShippingDiscountHash;
  }

  protected static String padRight(String s, int n) {
    return String.format("%1$-" + n + "s", s);
  }

  /**
   * Get the card reference number (last 4) from the credit card.
   *
   * @param pCardNumber       Credit card number
   * @return                  last 4 of credit card
   */
  protected String getCardReferenceNumber (String pCardNumber) {
    String lLastFour = pCardNumber.substring(pCardNumber.length() - 4);
    return lLastFour;
  }

  /**
   * Get the masked credit card number given the full credit card.
   *
   * @param pCardNumber     Credit card number
   * @return                masked card
   */
  protected String getMaskedCardNumber (String pCardNumber) {
    StringBuffer lStringBuffer = new StringBuffer();
    String lLastFour = pCardNumber.substring(pCardNumber.length() - 4);
    for (int i=0; i < pCardNumber.length() - 4; i++) {
      lStringBuffer.append("*");
    }
    lStringBuffer.append(lLastFour);
    return lStringBuffer.toString();
  }


  // ***********************************************************************************
  //
  //
  //                          Add Return Invoice Routines
  //
  //
  // ***********************************************************************************
  //public void addReturnInvoice (MFFOrderImpl pOrder) {
  //  vlogDebug ("Add a return invoice");
  //}


  /**
   * Create invoice records for a given return request.
   *
   * @param pReturnRequest      Return Request
   * @throws SalesAuditException
   */
  public void addReturnsInvoice (ReturnRequest pReturnRequest)
      throws SalesAuditException {

    MFFOrderImpl lOrder   = (MFFOrderImpl) pReturnRequest.getOrder();
    String lOrderNumber   = lOrder.getOrderNumber();
    String lOrderId       = lOrder.getId();
    String lRMA           = pReturnRequest.getRequestId();

    vlogInfo ("+++++ Begin - Add a new return invoice for order {0}/{1} RMAL: {2}", lOrderNumber, lOrderId, lRMA);

    boolean lRollback = true;
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      // Create a transaction
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Create an extract statistics record
      ExtractStatistics lExtractStatistics = new ExtractStatistics();

      // Get first shipping group for this order to get addresses
      ShippingGroup lShippingGroup  = (ShippingGroup) pReturnRequest.getOrder().getShippingGroups().get(0);
      String lShippingGroupId       = lShippingGroup.getId();

      // Create Shipping Address
      ContactInfo lShippingAddress = (ContactInfo) getShippingAddressForOrder (lOrder, lShippingGroupId);
      InvoiceAddress lInvoiceShippingAddress = createShippingAddress (lOrder, lShippingGroupId, lShippingAddress);

      // Create Billing Address
      ContactInfo lBillingAddress = (ContactInfo) getBillingAddressForOrder (lOrder, lShippingGroupId);
      InvoiceAddress lInvoiceBillingAddress = createBillingAddress  (lOrder, lShippingGroupId, lBillingAddress);

      // Create Returned Lines
      List<ReturnedItem> lReturnedItems = createReturnedLines (pReturnRequest, lExtractStatistics);



      // Create Invoice
      Invoice lInvoice = createReturnInvoiceItem (pReturnRequest, lExtractStatistics, lInvoiceShippingAddress,  lInvoiceBillingAddress, lReturnedItems);
      vlogDebug (lInvoice.toString());
      lRollback = false;
    }
    catch (TransactionDemarcationException ex) {
      String lErrorMessage = String.format("Unable to add return invoice (1) for order %s/%s RMA: %s", lOrderNumber, lOrderId, lRMA);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    catch (SalesAuditException ex) {
      String lErrorMessage = String.format("Unable to add return invoice (2) for order %s/%s RMA: %s", lOrderNumber, lOrderId, lRMA);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    finally {
      try {
        td.end (lRollback);
      }
      catch (TransactionDemarcationException ex) {
        String lErrorMessage = String.format("Unable to add return invoice (3) for order %s/%s RMA: %s", lOrderNumber, lOrderId, lRMA);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogInfo ("+++++ End - Add a new return invoice for order {0}/{1} RMA: {2}", lOrderNumber, lOrderId, lRMA);
  }


  /**
   * Create the returned items for this return request.
   *
   * @param pReturnRequest        Return Request
   * @param pExtractStatistics    Return Statistics
   * @return                      List of returned items
   * @throws SalesAuditException
   */
  @SuppressWarnings("unchecked")
  protected List<ReturnedItem> createReturnedLines (ReturnRequest pReturnRequest, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {

    // Create list of returned items
    List <ReturnedItem> lReturnedItems = new Vector <ReturnedItem> ();

    // Get list of returned items
    List<ReturnItem> lReturnItems = pReturnRequest.getReturnItemList();

    Hashtable <String, ReturnedItem> skuToReturnItem = new Hashtable<String, ReturnedItem>();
    // Loop through all of the items to be returned
    for (ReturnItem lReturnItem : lReturnItems) {
      MFFOrderImpl lOrder               = (MFFOrderImpl) pReturnRequest.getOrder();
      MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) lReturnItem.getCommerceItem();
      List<ShippingGroupRelationship> lShippingGroupRelationships = lCommerceItem.getShippingGroupRelationships();
      String lShippingGroupId           = lShippingGroupRelationships.get(0).getShippingGroup().getId();
      String lRMA                       = pReturnRequest.getRequestId();
      String skuId = lCommerceItem.getCatalogRefId();
      if(skuToReturnItem.containsKey(skuId) && getOmsOrderManager().isSplitOrderEnabled()){
       // do an update here
        updateReturnedItem(lOrder, lShippingGroupId, lRMA, lReturnItem, pExtractStatistics,skuToReturnItem.get(skuId));
      }else{
        ReturnedItem lReturnedItem        = createReturnedItem (lOrder, lShippingGroupId, lRMA, lReturnItem, pExtractStatistics);
        lReturnedItems.add(lReturnedItem);
        skuToReturnItem.put(skuId, lReturnedItem);
      }
    }
    MFFOrderImpl lOrder = (MFFOrderImpl)pReturnRequest.getOrder();
    ReturnedItem lItem = createShipmentReturnedItem(lOrder,lReturnedItems,lReturnItems);

    lReturnedItems.add(lItem);
    //negate the values that need to be negatted.
    updateSummaryStatsForCredit(pExtractStatistics);
    return lReturnedItems;
  }

  private void updateSummaryStatsForCredit (ExtractStatistics pExtractStatistics) {

    vlogDebug("Entering updateSummaryStatsForCredit : pExtractStatistics");
    pExtractStatistics.setTransactionTotal          (getNegativeValue(pExtractStatistics.getTransactionTotal()));
    pExtractStatistics.setTransactionTaxableTotal   (getNegativeValue(pExtractStatistics.getTransactionTaxableTotal()));
    pExtractStatistics.setTransactionTaxTotal       (getNegativeValue(pExtractStatistics.getTransactionTaxTotal()));
    pExtractStatistics.setPaymentTotal              (getNegativeValue(pExtractStatistics.getPaymentTotal()));
    vlogDebug("Exiting updateSummaryStatsForCredit : pExtractStatistics");
  }

  private String getStoreId(MFFReturnItem pReturnItem) {

    String lStoreId           = pReturnItem.getStoreId();
    //Store id is appended with 0 and we might have to strip this out.
    //and when the store id is set to null. We have to set it to 100
    if (StringUtils.isNotBlank(lStoreId)) {
      lStoreId = org.apache.commons.lang3.StringUtils.stripStart(lStoreId, "0");
    } else {
      vlogWarning("Store id was not for a return id {0}", pReturnItem.getId());
      lStoreId = getDefaultReturnStoreID();
    }
    return lStoreId;
  }


  /**
   * Create a returned item
   *
   * @param pOrder                ATG Order
   * @param pShippingGroupId      Shipping group Id
   * @param pReturnItem           Returned Item
   * @param pShippingDiscountHash Shipping Discounts
   * @param pExtractStatistics    Extract statistics for this return
   * @return
   * @throws SalesAuditException
   */
  protected ReturnedItem createReturnedItem (MFFOrderImpl pOrder, String pShippingGroupId, String pRMA, ReturnItem pReturnItem, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {

    vlogDebug("Entering createReturnedItem : pOrder, pShippingGroupId, pRMA, pReturnItem, pExtractStatistics");

    String lOrderNumber       = pOrder.getOrderNumber();
    String lOrderId           = pOrder.getId();
    String lCommerceItemId    = pReturnItem.getCommerceItem().getId();
    MFFReturnItem lReturnItem = (MFFReturnItem) pReturnItem;
    String lStoreId           = getStoreId(lReturnItem);



    vlogDebug ("+++++ Begin Return Item for Order: {0}/{1} ShipGroup: {2} Item: {3} store id {4}", lOrderNumber, lOrderId, pShippingGroupId, lCommerceItemId, lStoreId);

    // Get the commerce item
    MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pReturnItem.getCommerceItem();
    String lProductId                 = (String) lCommerceItem.getPropertyValue("productId");


    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_RETURNED_ITEM);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add returned item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, lCommerceItem.getId());
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }

    ReturnedItem lReturnedItem = new ReturnedItem (lMutableRepositoryItem);
    lReturnedItem.setExtractLineId             (new Long(lReturnedItem.getId()));
    lReturnedItem.setClientLineId              (lCommerceItem.getId());

    if(!pOrder.isBopisOrder() && lCommerceItem.isGwp() && getGwpGCSkuIds().contains(lCommerceItem.getCatalogRefId())){
      lReturnedItem.setSkucode                   (getGwpGCReplacementSkuId());
      lReturnedItem.setBarcode                   (getGwpGCReplacementSkuId());
      lReturnedItem.setItemNumber                (getGwpGCReplacementSkuId());
    }else{
      lReturnedItem.setSkucode                   (lCommerceItem.getCatalogRefId());
      lReturnedItem.setBarcode                   (lCommerceItem.getCatalogRefId());
      lReturnedItem.setItemNumber                (lCommerceItem.getCatalogRefId());
    }
    String lColorCode = getDynamicProperty (lCommerceItem.getCatalogRefId(), lProductId, "Color");
    if (lColorCode == null)
      lColorCode = "N1";
    lReturnedItem.setColorCode                 (lColorCode);
    String lSizeCode = getDynamicProperty (lCommerceItem.getCatalogRefId(), lProductId, "Size");
    if (lSizeCode == null)
      lSizeCode = "N1";
    lReturnedItem.setSizeCode                  (lSizeCode);
    lReturnedItem.setQuantity                  (pReturnItem.getQuantityReceived());
    lReturnedItem.setUnitPrice                 (lCommerceItem.getPriceInfo().getListPrice());
    lReturnedItem.setFacilityCd(lStoreId);


    // Get the Tax for the line
    Hashtable <String, Double> lTaxValues = getTaxForReturnedLine (pReturnItem);
    lReturnedItem.setLineLocalTax              (lTaxValues.get(SalesAuditConstants.LINE_LOCAL_TAX));
    lReturnedItem.setLineCountyTax             (lTaxValues.get(SalesAuditConstants.LINE_COUNTY_TAX));
    lReturnedItem.setLineStateTax              (lTaxValues.get(SalesAuditConstants.LINE_STATE_TAX));
    lReturnedItem.setLineTaxTotal              (lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));

    lReturnedItem.setLineExtendedTotal         (lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    lReturnedItem.setExtendedPrice             (lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    lReturnedItem.setLineNumber                (new Long(lReturnedItem.getId()));

    if (getReturnManager().isReturnItemEligibleForShippingRefund(lReturnItem)) {
      lReturnedItem.setShippingAmount            (lTaxValues.get(SalesAuditConstants.LINE_SHIPPING));
      lReturnedItem.setLineShippingTax           (lTaxValues.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    } else {
      lReturnedItem.setShippingAmount            (0.00D);
      lReturnedItem.setLineShippingTax           (0.00D);
    }

    // Set re-stock fees
    lReturnedItem.setRestockCountyTax          (0.00D);
    lReturnedItem.setRestockExtendedTotal      (0.00D);
    lReturnedItem.setRestockLocalTax           (0.00D);
    lReturnedItem.setRestockShippingTax        (0.00D);
    lReturnedItem.setRestockStateTax           (0.00D);
    lReturnedItem.setRestockTaxTotal           (0.00D);
    double lRefundAmount = roundPrice (pReturnItem.getRefundAmount() + pReturnItem.getActualTaxRefundShare());
    lReturnedItem.setReturnedAmount            (lRefundAmount);
    lReturnedItem.setReturnReason              (getSalesAuditReturnReason(pReturnItem.getReturnReason()));
    lReturnedItem.setRmaNumber                 (pRMA);

    // Update Statistics
    pExtractStatistics.setLineCount(pExtractStatistics.getLineCount() + 1);
    pExtractStatistics.setTransactionTotal(pExtractStatistics.getTransactionTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    pExtractStatistics.setTransactionTaxTotal(pExtractStatistics.getTransactionTaxTotal() + lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));
    if (lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL) > 0)
     pExtractStatistics.setTransactionTaxableTotal(pExtractStatistics.getTransactionTaxableTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    if (getOmsOrderManager().isGiftCardItem(lCommerceItem)) {
      pExtractStatistics.setGiftcardSoldCount(pExtractStatistics.getGiftcardSoldCount() + pReturnItem.getQuantityReceived());
      pExtractStatistics.setGiftcardSoldTotal(pExtractStatistics.getGiftcardSoldTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    }



    // Create the Line Discounts for this item
    List<LineDiscount> lLineDiscounts = createLineDiscounts (pOrder, pShippingGroupId, lCommerceItemId, pReturnItem);
    lReturnedItem.setLineDiscounts(lLineDiscounts);

    // Add discounts to totals
    for (LineDiscount lLineDiscount : lLineDiscounts) {
      pExtractStatistics.setDiscountCount             (pExtractStatistics.getDiscountCount() + 1);
      pExtractStatistics.setDiscountTotal             (roundPrice(pExtractStatistics.getDiscountTotal() + (lLineDiscount.getAmount()*lReturnedItem.getQuantity())));
      pExtractStatistics.setTransactionTotal          (roundPrice(pExtractStatistics.getTransactionTotal()));
      if (pExtractStatistics.getTransactionTaxableTotal() > 0) {
        pExtractStatistics.setTransactionTaxableTotal (roundPrice((pExtractStatistics.getTransactionTaxableTotal())));
      }
    }


    // Create Auxillaries (Done)
    List <AuxiliaryRecord> lAuxiliaryRecords = createLineAuxiliarys (pOrder, pShippingGroupId, lCommerceItem.getId());
    lReturnedItem.setAuxiliaries(lAuxiliaryRecords);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lReturnedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add shipped item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, lCommerceItem.getId());
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Return Item for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pReturnItem.getCommerceItem().getId());
    vlogDebug("Exiting createReturnedItem : pOrder, pShippingGroupId, pRMA, pReturnItem, pExtractStatistics");
    return lReturnedItem;
  }

  protected ReturnedItem updateReturnedItem (MFFOrderImpl pOrder, String pShippingGroupId, String pRMA, ReturnItem pReturnItem, ExtractStatistics pExtractStatistics, ReturnedItem pReturnedItem)
      throws SalesAuditException {

    vlogDebug("Entering updateReturnedItem : pOrder, pShippingGroupId, pRMA, pReturnItem, pExtractStatistics");

    String lOrderNumber       = pOrder.getOrderNumber();
    String lOrderId           = pOrder.getId();
    String lCommerceItemId    = pReturnItem.getCommerceItem().getId();
    MFFReturnItem lReturnItem = (MFFReturnItem) pReturnItem;
    String lStoreId           = getStoreId(lReturnItem);



    vlogDebug ("+++++ Begin Return Item for Order: {0}/{1} ShipGroup: {2} Item: {3} store id {4}", lOrderNumber, lOrderId, pShippingGroupId, lCommerceItemId, lStoreId);

    // Get the commerce item
    MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pReturnItem.getCommerceItem();
    ReturnedItem lReturnedItem = pReturnedItem;

    lReturnedItem.setQuantity                  (lReturnedItem.getQuantity() + pReturnItem.getQuantityReceived());
    // Get the Tax for the line
    Hashtable <String, Double> lTaxValues = getTaxForReturnedLine (pReturnItem);
    lReturnedItem.setLineLocalTax              (lReturnedItem.getLineLocalTax() + lTaxValues.get(SalesAuditConstants.LINE_LOCAL_TAX));
    lReturnedItem.setLineCountyTax             (lReturnedItem.getLineCountyTax() + lTaxValues.get(SalesAuditConstants.LINE_COUNTY_TAX));
    lReturnedItem.setLineStateTax              (lReturnedItem.getLineStateTax() + lTaxValues.get(SalesAuditConstants.LINE_STATE_TAX));
    lReturnedItem.setLineTaxTotal              (lReturnedItem.getLineTaxTotal() + lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));

    lReturnedItem.setLineExtendedTotal         (lReturnedItem.getLineExtendedTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    lReturnedItem.setExtendedPrice             (lReturnedItem.getExtendedPrice() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    //lReturnedItem.setLineNumber                (new Long(lReturnedItem.getId()));

    if (getReturnManager().isReturnItemEligibleForShippingRefund(lReturnItem)) {
      lReturnedItem.setShippingAmount            (lReturnedItem.getShippingAmount() + lTaxValues.get(SalesAuditConstants.LINE_SHIPPING));
      lReturnedItem.setLineShippingTax           (lReturnedItem.getLineShippingTax() + lTaxValues.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    } else {
      lReturnedItem.setShippingAmount            (0.00D);
      lReturnedItem.setLineShippingTax           (0.00D);
    }

    // Set re-stock fees
    lReturnedItem.setRestockCountyTax          (0.00D);
    lReturnedItem.setRestockExtendedTotal      (0.00D);
    lReturnedItem.setRestockLocalTax           (0.00D);
    lReturnedItem.setRestockShippingTax        (0.00D);
    lReturnedItem.setRestockStateTax           (0.00D);
    lReturnedItem.setRestockTaxTotal           (0.00D);
    double lRefundAmount = roundPrice (lReturnedItem.getReturnedAmount() + pReturnItem.getRefundAmount() + pReturnItem.getActualTaxRefundShare());
    lReturnedItem.setReturnedAmount            (lRefundAmount);
    //lReturnedItem.setReturnReason              (getSalesAuditReturnReason(pReturnItem.getReturnReason()));
    //lReturnedItem.setRmaNumber                 (pRMA);

    // Update Statistics
    //pExtractStatistics.setLineCount(pExtractStatistics.getLineCount() + 1);
    pExtractStatistics.setTransactionTotal(pExtractStatistics.getTransactionTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    pExtractStatistics.setTransactionTaxTotal(pExtractStatistics.getTransactionTaxTotal() + lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL));
    if (lTaxValues.get(SalesAuditConstants.LINE_TAX_TOTAL) > 0)
     pExtractStatistics.setTransactionTaxableTotal(pExtractStatistics.getTransactionTaxableTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    if (getOmsOrderManager().isGiftCardItem(lCommerceItem)) {
      pExtractStatistics.setGiftcardSoldCount(pExtractStatistics.getGiftcardSoldCount() + pReturnItem.getQuantityReceived());
      pExtractStatistics.setGiftcardSoldTotal(pExtractStatistics.getGiftcardSoldTotal() + lTaxValues.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
    }



    // Create the Line Discounts for this item
    List<LineDiscount> lLineDiscounts = createLineDiscounts (pOrder, pShippingGroupId, lCommerceItemId, pReturnItem);
    lReturnedItem.setLineDiscounts(lLineDiscounts);

    // Add discounts to totals
    for (LineDiscount lLineDiscount : lLineDiscounts) {
      //pExtractStatistics.setDiscountCount             (pExtractStatistics.getDiscountCount() + 1);
      pExtractStatistics.setDiscountTotal             (roundPrice(pExtractStatistics.getDiscountTotal() + (lLineDiscount.getAmount()*lReturnedItem.getQuantity())));
      pExtractStatistics.setTransactionTotal          (roundPrice(pExtractStatistics.getTransactionTotal()));
      if (pExtractStatistics.getTransactionTaxableTotal() > 0) {
        pExtractStatistics.setTransactionTaxableTotal (roundPrice((pExtractStatistics.getTransactionTaxableTotal())));
      }
    }


    // Create Auxillaries (Done)
    /*List <AuxiliaryRecord> lAuxiliaryRecords = createLineAuxiliarys (pOrder, pShippingGroupId, lCommerceItem.getId());
    lReturnedItem.setAuxiliaries(lAuxiliaryRecords);*/

    // Update item to repository
    try {
      getInvoiceRepository().updateItem(lReturnedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to update return item for order %s/%s ShipGroup: %s Item: %s", lOrderNumber, lOrderId, pShippingGroupId, lCommerceItem.getId());
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Return Item for Order: {0}/{1} ShipGroup: {2} Item: {3}", lOrderNumber, lOrderId, pShippingGroupId, pReturnItem.getCommerceItem().getId());
    vlogDebug("Exiting updateReturnedItem : pOrder, pShippingGroupId, pRMA, pReturnItem, pExtractStatistics");
    return lReturnedItem;
  }


  private double getNegativeValue(double pAmount) {
    if (pAmount < 0.0d) {
      return pAmount;
    } else {
      return -pAmount;
    }
  }

  /**
   * Create a returned item
   *
   * @param pOrder                ATG Order
   * @param pShippingGroupId      Shipping group Id
   * @param pReturnItem           Returned Item
   * @param pShippingDiscountHash Shipping Discounts
   * @param pExtractStatistics    Extract statistics for this return
   * @return
   * @throws SalesAuditException
   */
  protected ReturnedItem createShipmentReturnedItem (MFFOrderImpl pOrder, List<ReturnedItem> pReturnedItems, List<ReturnItem> pReturnItems)
      throws SalesAuditException {
    String lOrderId = pOrder.getId();
    String lOrderNumber = pOrder.getOrderNumber();
    String lStoreId = null;
    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_RETURNED_ITEM);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add returned item for order %s/%s ShipGroup: %s Item: %s");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }

    lStoreId = pReturnedItems.get(0).getFacilityCd();


    //get the total of shipping charges of the items returned
    Double totalReturnShipAmount =0.0;
    Double totalShipTax = 0.0;
    for(ReturnedItem lItem : pReturnedItems){
      totalReturnShipAmount+=lItem.getShippingAmount();
      totalShipTax+=lItem.getLineShippingTax();

    }

    String lTaxable = "false";
    Hashtable <String, Double> lShippingTaxesForReturnedItems = getShippingAndTaxesForReturnedItems(pReturnItems);
    if(lShippingTaxesForReturnedItems != null){
      Double tax = (Double)lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_TAX);
      if(tax > 0.00){
        lTaxable="true";
      }
    }

    ReturnedItem lReturnedItem = new ReturnedItem (lMutableRepositoryItem);
    lReturnedItem.setExtractLineId             (new Long(lReturnedItem.getId()));
    lReturnedItem.setClientLineId              (lReturnedItem.getId());
    lReturnedItem.setSkucode                   (getShippingSku());
    lReturnedItem.setBarcode                   (getShippingSku());
    lReturnedItem.setItemNumber                (getShippingSku());
    lReturnedItem.setColorCode                 ("N1");
    lReturnedItem.setSizeCode                  ("N1");
    long qty = 1;
    lReturnedItem.setQuantity                  (qty);
    lReturnedItem.setUnitPrice                 (totalReturnShipAmount);
    lReturnedItem.setFacilityCd                (lStoreId);

    // Get the Shipping Tax for the line
    lReturnedItem.setLineLocalTax              (lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
    lReturnedItem.setLineCountyTax             (lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
    lReturnedItem.setLineStateTax              (lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
    lReturnedItem.setLineTaxTotal              (lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_TAX));

    lReturnedItem.setLineShippingTax           (lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_TAX));

    double lShippingTotal = lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_TAX) + totalReturnShipAmount;
    lShippingTotal        = roundPrice(lShippingTotal);

    lReturnedItem.setLineExtendedTotal         (lShippingTotal);
    lReturnedItem.setExtendedPrice             (lShippingTotal);
    lReturnedItem.setLineNumber                (new Long(lReturnedItem.getId()));
    lReturnedItem.setShippingAmount            (lShippingTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING));

    // Set re-stock fees
    lReturnedItem.setRestockCountyTax          (0.00D);
    lReturnedItem.setRestockExtendedTotal      (0.00D);
    lReturnedItem.setRestockLocalTax           (0.00D);
    lReturnedItem.setRestockShippingTax        (0.00D);
    lReturnedItem.setRestockStateTax           (0.00D);
    lReturnedItem.setRestockTaxTotal           (0.00D);
    lReturnedItem.setReturnedAmount            (0.00D);
    lReturnedItem.setReturnReason(getDefaultReturnShippingReasonCode());


    // Create Auxiliaries (Done)
    List <AuxiliaryRecord> lAuxiliaryRecords = createLineAuxiliarys (pOrder,lTaxable);
    lReturnedItem.setAuxiliaries(lAuxiliaryRecords);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lReturnedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add return shipped item for order %s/%s", lOrderNumber, lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Return Item for Order: {0}/{1}", lOrderNumber, lOrderId);
    return lReturnedItem;
  }

  /**
   * Convert the ATG return reason code in a Sales Audit reason code.
   *
   * @param pReturnReasonCode         ATG Return Reason code
   * @return                          Sales Audit reason code
   */
  protected String getSalesAuditReturnReason (String pReturnReasonCode) {
    String lSAReasonCode = null;

    if (StringUtils.isNotBlank(pReturnReasonCode) && getReturnReasonCodes().containsKey(pReturnReasonCode)) {

      lSAReasonCode = getReturnReasonCodes().get(pReturnReasonCode);
      vlogDebug("Return reason code set to {0}", lSAReasonCode);

    } else {
      lSAReasonCode = getDefaultReturnReasonCode();
      vlogWarning("Could not find the return reason code mapping for {0} setting it to the default return reason code of {1}", pReturnReasonCode, lSAReasonCode);
    }

    return lSAReasonCode;
  }


  /**
   * Get the tax for all of the returned lines.
   *
   * @param pReturnItems          List of Returned Items
   * @return                      Hash table of taxes
   * @throws SalesAuditException
   */
  protected Hashtable <String, Double> getShippingAndTaxesForReturnedItems (List<ReturnItem> pReturnItems)
      throws SalesAuditException {

    Hashtable <String, Double> lCumulativeCharges = new Hashtable <String, Double> ();
    lCumulativeCharges.put(SalesAuditConstants.LINE_LOCAL_TAX,           0.00D);
    lCumulativeCharges.put(SalesAuditConstants.LINE_COUNTY_TAX,          0.00D);
    lCumulativeCharges.put(SalesAuditConstants.LINE_STATE_TAX,           0.00D);

    lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING,            0.00D);
    lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,  0.00D);
    lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX, 0.00D);
    lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,  0.00D);

    lCumulativeCharges.put(SalesAuditConstants.LINE_TAX_TOTAL,           0.00D);

    lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_TAX,        0.00D);

    lCumulativeCharges.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,      0.00D);
    lCumulativeCharges.put(SalesAuditConstants.LINE_DISCOUNT,            0.00D);

    for (ReturnItem lReturnItem : pReturnItems) {
      Hashtable <String, Double> lLineTax = getTaxForReturnedLine (lReturnItem);
      lCumulativeCharges.put(SalesAuditConstants.LINE_LOCAL_TAX,           lCumulativeCharges.get(SalesAuditConstants.LINE_LOCAL_TAX)             + lLineTax.get(SalesAuditConstants.LINE_LOCAL_TAX));
      lCumulativeCharges.put(SalesAuditConstants.LINE_COUNTY_TAX,          lCumulativeCharges.get(SalesAuditConstants.LINE_COUNTY_TAX)            + lLineTax.get(SalesAuditConstants.LINE_COUNTY_TAX));
      lCumulativeCharges.put(SalesAuditConstants.LINE_STATE_TAX,           lCumulativeCharges.get(SalesAuditConstants.LINE_STATE_TAX)             + lLineTax.get(SalesAuditConstants.LINE_STATE_TAX));

      if (getReturnManager().isReturnItemEligibleForShippingRefund(lReturnItem)) {
        lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING,            lCumulativeCharges.get(SalesAuditConstants.LINE_SHIPPING)              + lLineTax.get(SalesAuditConstants.LINE_SHIPPING));
        lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,  lCumulativeCharges.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX)    + lLineTax.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
        lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX, lCumulativeCharges.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX)   + lLineTax.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
        lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,  lCumulativeCharges.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX)    + lLineTax.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
        lCumulativeCharges.put(SalesAuditConstants.LINE_SHIPPING_TAX,        lCumulativeCharges.get(SalesAuditConstants.LINE_SHIPPING_TAX)          + lLineTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
      }

      lCumulativeCharges.put(SalesAuditConstants.LINE_TAX_TOTAL,           lCumulativeCharges.get(SalesAuditConstants.LINE_TAX_TOTAL)             + lLineTax.get(SalesAuditConstants.LINE_TAX_TOTAL));
      lCumulativeCharges.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,      lCumulativeCharges.get(SalesAuditConstants.LINE_EXTENDED_TOTAL)        + lLineTax.get(SalesAuditConstants.LINE_EXTENDED_TOTAL));
      lCumulativeCharges.put(SalesAuditConstants.LINE_DISCOUNT,            lCumulativeCharges.get(SalesAuditConstants.LINE_DISCOUNT)              + lLineTax.get(SalesAuditConstants.LINE_DISCOUNT));
    }

    Set<String> lKeys = lCumulativeCharges.keySet();
    for(String lKey: lKeys) {
      //lGroupTax.put(lKey, roundDouble (lGroupTax.get(lKey), 2));
      lCumulativeCharges.put(lKey, roundPrice (lCumulativeCharges.get(lKey)));
    }
    return lCumulativeCharges;
  }

  /**
   * Get the tax for a returned line.
   *
   * @param pReturnItem     Returned Item
   * @return                List of taxes for this line
   * @throws SalesAuditException
   */
  protected Hashtable <String, Double> getTaxForReturnedLine (ReturnItem pReturnItem)
      throws SalesAuditException {
    Hashtable <String, Double> lLineTax = new Hashtable <String, Double> ();

    // Item Tax
    double lLineLocalTax          = 0.00;
    double lLineCountyTax         = 0.00;
    double lLineStateTax          = 0.00;
    double lLineShipping          = 0.00;
    double lLineShippingLocalTax  = 0.00;
    double lLineShippingCountyTax = 0.00;
    double lLineShippingStateTax  = 0.00;
    double lLineTotalTax          = 0.00;
    double lLineShippingTax       = 0.00;
    double lLineExtendedTotal     = 0.00;
    double lLineDiscount          = 0.00;

    // Get the list of prorated items for this returned Item
    // MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pReturnItem.getCommerceItem();
    // List<String> lReturnedItemIds = new ArrayList <String> (lCommerceItem.getReturnItemIds());

    Set<String> lReturnedItemIds = ((MFFReturnItem)pReturnItem).getProRatedItemIds();

    if(lReturnedItemIds != null){
      for (String lReturnedItemId : lReturnedItemIds) {
        RepositoryItem lRepositoryItem = getProrateItemById (lReturnedItemId);
        double lLocalTax            = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_CITY_TAX);
        double lStateTax            = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_STATE_TAX);
        double lCountyTax           = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_COUNTY_TAX);
        double lShippingLocalTax    = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_CITY_TAX);
        double lShippingStateTax    = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_STATE_TAX);
        double lShippingCountyTax   = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_COUNTY_TAX);
        double lShipping            = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING);
        double lItemAmount          = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_TOTAL_WITHOUT_SHIPPING);
        double lSalePrice           = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_SALE_PRICE);
        double lUnitPrice           = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_UNIT_PRICE);
        double lOrderDiscountShare  = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_ORDER_DISCOUNT_SHARE);

        lLineLocalTax               = roundPrice (lLineLocalTax              + lLocalTax);
        lLineCountyTax              = roundPrice (lLineCountyTax             + lCountyTax);
        lLineStateTax               = roundPrice (lLineStateTax              + lStateTax);
        lLineShippingLocalTax       = roundPrice (lLineShippingLocalTax      + lShippingLocalTax);
        lLineShippingCountyTax      = roundPrice (lLineShippingCountyTax     + lShippingCountyTax);
        lLineShippingStateTax       = roundPrice (lLineShippingStateTax      + lShippingStateTax);
        lLineShipping               = roundPrice (lLineShipping              + lShipping);
        lLineTotalTax               = roundPrice (lLineTotalTax              + lLocalTax + lStateTax + lCountyTax);
        lLineShippingTax            = roundPrice (lLineShippingTax           + lShippingLocalTax + lShippingCountyTax + lShippingStateTax);
        lLineExtendedTotal          = roundPrice (lLineExtendedTotal         + lItemAmount);
        lLineDiscount               = roundPrice (lSalePrice - lUnitPrice - lOrderDiscountShare);

      }
    }

    // Copy values to the hash table
    lLineTax.put(SalesAuditConstants.LINE_LOCAL_TAX,            lLineLocalTax);
    lLineTax.put(SalesAuditConstants.LINE_COUNTY_TAX,           lLineCountyTax);
    lLineTax.put(SalesAuditConstants.LINE_STATE_TAX,            lLineStateTax);
    lLineTax.put(SalesAuditConstants.LINE_TAX_TOTAL,            lLineTotalTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING,             lLineShipping);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,   lLineShippingLocalTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX,  lLineShippingCountyTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,   lLineShippingStateTax);
    lLineTax.put(SalesAuditConstants.LINE_SHIPPING_TAX,         lLineShippingTax);
    lLineTax.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,       lLineExtendedTotal);
    lLineTax.put(SalesAuditConstants.LINE_DISCOUNT,             lLineDiscount);
    return lLineTax;
  }

  /**
   * Get the protrated item using the prorated item Id
   *
   * @param pId       Prorated Item id
   * @return          Repository Item for prorated item
   *
   * @throws SalesAuditException
   */
  protected RepositoryItem getProrateItemById(String pId)
    throws SalesAuditException {

    vlogDebug("Begin - Get proratred item for {0}", pId);
    RepositoryItem[] lItems = null;
    RepositoryItem lItem    = null;
    try {
      Object[] lParams        = new Object[2];
      lParams[0]              = pId;
      RepositoryView lView    = getOmsOrderManager().getOmsOrderRepository().getView(MFFConstants.ITEM_DESC_PRORATE_ITEM);
      RqlStatement lStatement = RqlStatement.parseRqlStatement("Id EQUALS ?0");
      lItems                  = lStatement.executeQuery(lView, lParams);
      if (lItems != null && lItems.length > 0)
        lItem = lItems[0];
    } catch (RepositoryException e) {
      String lErrorMessage = String.format("getProrateItemById - Error locating Item Id: %s", pId);
      vlogError(e, lErrorMessage);
      throw new SalesAuditException(lErrorMessage, e);
    }
    return lItem;
  }

  /**
   * Create an invoice record for a given return request.
   *
   * @param pReturnRequest            Return Request
   * @param pExtractStatistics        Extract statistics for this return
   * @param pInvoiceShippingAddress   Shipping Address
   * @param pInvoiceBillingAddress    Billing Address
   * @param pReturnedItems            List of returned items
   * @return                          Invoice record
   * @throws SalesAuditException
   */
  @SuppressWarnings("unchecked")
  protected Invoice createReturnInvoiceItem (ReturnRequest pReturnRequest, ExtractStatistics pExtractStatistics,
      InvoiceAddress pInvoiceShippingAddress, InvoiceAddress pInvoiceBillingAddress, List<ReturnedItem> pReturnedItems)
      throws SalesAuditException {

    MFFOrderImpl lOrder     = (MFFOrderImpl) pReturnRequest.getOrder();
    String lOrderNumber     = lOrder.getOrderNumber();
    String lOrderId         = lOrder.getId();
    String lRMA             = pReturnRequest.getRequestId();
    String lShippingMethod  = "Standard";

    vlogDebug ("+++++ Begin createReturnInvoice for Order: {0}/{1} RMA: {2}", lOrderNumber, lOrderId, lRMA);

    // Get the first shipping group
    ShippingGroup lShippingGroup  = (ShippingGroup) pReturnRequest.getOrder().getShippingGroups().get(0);
    if (lShippingGroup != null)
      lShippingMethod = lShippingGroup.getShippingMethod();

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_INVOICE);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice Shipping Address for order %s/%s RMA: %s", lOrderNumber, lOrderId, lRMA);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    Invoice lInvoice = new Invoice (lMutableRepositoryItem);
    lInvoice.setOrderNumber               (lOrderNumber);
    if(pReturnRequest.getAuthorizationDate() != null){
      lInvoice.setOrderDate                 (new Timestamp (pReturnRequest.getAuthorizationDate().getTime()));
    }else{
      lInvoice.setOrderDate                 (new Timestamp (System.currentTimeMillis()));
    }
    lInvoice.setSource                    (SalesAuditConstants.ORDER_SOURCE);
    lInvoice.setOrderType                 (SalesAuditConstants.ORDER_TYPE);
    lInvoice.setBusinessType              (SalesAuditConstants.BUSINESS_TYPE);
    lInvoice.setLoyaltyIdentifier         (null);
    lInvoice.setCustomerPurchaseOrder     (null);
    //lInvoice.setReference                 (lOrderId);
    lInvoice.setReference                 (lInvoice.getId());
    lInvoice.setFleetFarmId               (lOrder.getEmployeeId());

    // Tax Exemption
    Hashtable <String, String> lTaxExemptionHash = getTaxExemptionCertificate (lOrder);
    lInvoice.setTaxExemptionCertificate   (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CERT));
    lInvoice.setTaxExemptionName          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_NAME));
    lInvoice.setTaxExemptionType          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CODE));

    // Tax
    List<ReturnItem> lReturnItems = pReturnRequest.getReturnItemList();
    Hashtable <String, Double> lShippingAndTaxesForReturnedItems = getShippingAndTaxesForReturnedItems (lReturnItems);
    lInvoice.setOrderShipping             (lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setOrderShippingLocalTax     (lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
    lInvoice.setOrderShippingCountyTax    (lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
    lInvoice.setOrderShippingStateTax     (lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
    lInvoice.setOrderShippingTotal        (lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingTax          (lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingExtendedTotal(lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lShippingAndTaxesForReturnedItems.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setShipVia                   (getShipViaFromMethod(lShippingMethod));
    lInvoice.setStatus                    (SalesAuditConstants.EXTRACT_STATUS_CREATED);
    lInvoice.setLastExtractDate           (null);
    lInvoice.setExtract                   (null);
    lInvoice.setShippingAddress           (pInvoiceShippingAddress);
    lInvoice.setBillingAddress            (pInvoiceBillingAddress);
    lInvoice.setReturnedItems             (pReturnedItems);

    // Create order auxiliaries
    List<AuxiliaryRecord> lAuxiliaryRecords = createOrderAuxiliarys (lOrder);
    lInvoice.setAuxiliaries(lAuxiliaryRecords);

    // Create Payments
    List<LinePayment> lLinePayments = createReturnPayments (pReturnRequest, pExtractStatistics);
    lInvoice.setPayments(lLinePayments);

    // Create Summary
    LineSummary lLineSummary = createLineSummary (lOrder, null, pExtractStatistics);
    lInvoice.setLineSummary(lLineSummary);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lInvoice.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Return Invoice Shipping Address for Order %s/%s RMA: %s", lOrderNumber, lOrderId, lRMA);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Create Return Invoice Item for Order: {0}/{1} RMA: {2}", lOrderNumber, lOrderId, lRMA);
    return lInvoice;
  }

  /**
   * Create the payments for the shipped items.
   *
   * @param pReturnRequest          Return Request
   * @param pExtractStatistics      Extract Statistics
   * @return
   * @throws SalesAuditException
   */
  @SuppressWarnings("unchecked")
  protected List<LinePayment> createReturnPayments (ReturnRequest pReturnRequest, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {

    List <LinePayment> lLinePayments = new Vector <LinePayment> ();
    MFFOrderImpl lOrder = (MFFOrderImpl) pReturnRequest.getOrder();
    String lOrderNumber     = lOrder.getOrderNumber();
    String lOrderId         = lOrder.getId();
    String lRMA             = pReturnRequest.getRequestId();

    vlogDebug ("+++++ Begin Create Return Line Payments for Order : {0}/{1} RMA {2}", lOrderNumber, lOrderId, lRMA);
    List<ReturnItem> lReturnItems       = pReturnRequest.getReturnItemList();
    MFFCommerceItemImpl lCommerceItem   = (MFFCommerceItemImpl) lReturnItems.get(0).getCommerceItem();
    Timestamp lShipTimestamp            = new Timestamp (lCommerceItem.getShipDate().getTime());

    // Loop through all the items and get payment group(s) for this return
    List<RefundMethod> lRefundMethods = pReturnRequest.getRefundMethodList();
    for (RefundMethod lRefundMethod : lRefundMethods) {
      double lPaymentGroupAmount = getNegativeValue(lRefundMethod.getAmount());
      MutableRepositoryItem lMutableRepositoryItem = null;
      try {
        lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_PAYMENT);
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to add line payment for order %s/%s", lOrderNumber, lOrderId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
      LinePayment lLinePayment = new LinePayment (lMutableRepositoryItem);
      if (lRefundMethod instanceof CreditCardRefundMethod) {
        CreditCardRefundMethod lCreditCardRefundMethod = (CreditCardRefundMethod) lRefundMethod;
        CreditCard lCreditCardPaymentGroup  = lCreditCardRefundMethod.getCreditCard();
        lLinePayment.setAmount                  (lPaymentGroupAmount);
        lLinePayment.setCardNumber              ("************" + lCreditCardPaymentGroup.getCreditCardNumber());
        lLinePayment.setCardReference           (lCreditCardPaymentGroup.getCreditCardNumber());
        lLinePayment.setPaymentDate             (lShipTimestamp);
        lLinePayment.setPaymentType             (getMillsPaymentMethod(lCreditCardPaymentGroup.getCreditCardType()));
        lLinePayment.setTokenId                 ((String) (lCreditCardPaymentGroup.getPropertyValue("tokenNumber")));
        List <PaymentStatus> lCreditCardStatuses = lCreditCardPaymentGroup.getAuthorizationStatus();
        for (PaymentStatus lCreditCardStatus : lCreditCardStatuses) {
          if (lCreditCardStatus.getTransactionSuccess())
          lLinePayment.setTransactionReference  (lCreditCardStatus.getTransactionId());
        }
        List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (lOrder, lCreditCardPaymentGroup, "CreditCard");
        lLinePayment.setAuxiliaries(lAuxiliaryRecords);
        pExtractStatistics.setPaymentCount      (pExtractStatistics.getPaymentCount() + 1);
        pExtractStatistics.setPaymentTotal      (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
      }
      if (lRefundMethod instanceof MFFGiftCardRefundMethod) {
        MFFGiftCardRefundMethod lGiftCardRefundMethod = (MFFGiftCardRefundMethod) lRefundMethod;
        MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) lGiftCardRefundMethod.getPaymentGroup();
        lLinePayment.setAmount                (lPaymentGroupAmount);
        // Card number, card reference and token Id and reference will be null for GC returns
        lLinePayment.setCardNumber            (null);
        lLinePayment.setCardReference         (null);
        lLinePayment.setPaymentDate           (lShipTimestamp);
        lLinePayment.setPaymentType           (SalesAuditConstants.GIFT_CARD_PAYMENT_TYPE);
        lLinePayment.setTokenId               (null);
        lLinePayment.setTransactionReference  (null);
        List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (lOrder, lGiftCardPaymentGroup, "GiftCard");
        lLinePayment.setAuxiliaries(lAuxiliaryRecords);
        pExtractStatistics.setPaymentCount    (pExtractStatistics.getPaymentCount() + 1);
        pExtractStatistics.setPaymentTotal    (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
      }

      updateSummaryStatsForCredit(pExtractStatistics);
      try {
        getInvoiceRepository().addItem(lLinePayment.getRepositoryItem());
        lLinePayments.add(lLinePayment);
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to add Return payment for Order %s/%s RMA: %s", lOrderNumber, lOrderId, lRMA);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogDebug ("+++++ End Create Return Line Payments for Order : {0}/{1} RMA: {2}", lOrderNumber, lOrderId, lRMA);
    return lLinePayments;
  }

  // ***********************************************************************************
  //
  //
  //                          Add Appeasement Invoice Routines
  //
  //
  // ***********************************************************************************
  //public void addAppeasementInvoice (MFFOrderImpl pOrder) {
  //  vlogDebug ("Add an appeasement invoice to order");
  //}

  /**
   * Create an invoice record for an appeasement.
   *
   * @param pAppeasement            Appeasement Request
   * @throws SalesAuditException
   */
  public void addAppeasementInvoice (Appeasement pAppeasement)
      throws SalesAuditException {

    String lOrderId       = pAppeasement.getOrderId();
    MFFOrderImpl lOrder;
    try {
      lOrder = (MFFOrderImpl) getOmsOrderManager().loadOrder(lOrderId);
    } catch (CommerceException ex) {
      String lErrorMessage = String.format("Unable to load order Id: %s", lOrderId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    String lOrderNumber   = lOrder.getOrderNumber();
    String lAppeasementId = pAppeasement.getAppeasementId();

    vlogInfo ("+++++ Begin - Add a new appeasement for order {0}/{1} Appeasement Id: {2}", lOrderNumber, lOrderId, lAppeasementId);

    boolean lRollback = true;
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      // Create a transaction
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Create an extract statistics record
      ExtractStatistics lExtractStatistics = new ExtractStatistics();

      // Get first shipping group for this order to get addresses
      ShippingGroup lShippingGroup  = (ShippingGroup) lOrder.getShippingGroups().get(0);
      String lShippingGroupId       = lShippingGroup.getId();

      // Create Shipping Address
      ContactInfo lShippingAddress = (ContactInfo) getShippingAddressForOrder (lOrder, lShippingGroupId);
      InvoiceAddress lInvoiceShippingAddress = createShippingAddress (lOrder, lShippingGroupId, lShippingAddress);

      // Create Billing Address
      ContactInfo lBillingAddress = (ContactInfo) getBillingAddressForOrder (lOrder, lShippingGroupId);
      InvoiceAddress lInvoiceBillingAddress = createBillingAddress  (lOrder, lShippingGroupId, lBillingAddress);

      // Create Appeasement
      //List<oms.commerce.salesaudit.record.Appeasement> lAppeasements = createAppeasementItem (lOrder, pAppeasement);

      // Create Appeasement Shipped Lines
      List<ShippedItem> lShippedItems = createAppeasementShippedItem (lOrder,  pAppeasement, lExtractStatistics);

      // Create Invoice
      Invoice lInvoice = createAppeasementInvoiceItem (pAppeasement, lOrder, lShippedItems, lExtractStatistics, lInvoiceShippingAddress,  lInvoiceBillingAddress);
      vlogDebug (lInvoice.toString());
      lRollback = false;
    }
    catch (TransactionDemarcationException ex) {
      String lErrorMessage = String.format("Unable to add return invoice (1) for order %s/%s Appeasement: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    catch (SalesAuditException ex) {
      String lErrorMessage = String.format("Unable to add return invoice (2) for order %s/%s Appeasement: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    finally {
      try {
        td.end (lRollback);
      }
      catch (TransactionDemarcationException ex) {
        String lErrorMessage = String.format("Unable to add return invoice (3) for order %s/%s Appeasement: %s", lOrderNumber, lOrderId, lAppeasementId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogInfo ("+++++ End - Add a new return invoice for order {0}/{1} Appeasement: {2}", lOrderNumber, lOrderId, lAppeasementId);
  }


  /**
   * Create a shipped item for each of the appeasements.
   *
   * @param pOrder              ATG Order
   * @param pAppeasement        Appeasement
   * @param lSkuCode            The SKU for this appeasement
   * @param pExtractStatistics  Statistics for this order
   * @return                    Shipped item
   * @throws SalesAuditException
   */
  protected List<ShippedItem> createAppeasementShippedItem (MFFOrderImpl pOrder, Appeasement pAppeasement, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {
    String lAppeasementId     = pAppeasement.getAppeasementId();
    double lAppeasementAmout  = pAppeasement.getAppeasementAmount();
    String lOrderNumber       = pOrder.getOrderNumber();
    String lOrderId           = pOrder.getId();
    String lSkuCode           = null;
    List<ShippedItem> lShippedItems = new ArrayList <ShippedItem> ();
    vlogDebug ("+++++ Begin Appeasement Shipped Item for Order: {0}/{1} Apopeasement: {2}", lOrderNumber, lOrderId, lAppeasementId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_SHIPPED_ITEM);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Appeaasement shipped item for order %s/%s Appeasement: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    // Get the SKU for this type of Appeasement
    String lAppeasementType = pAppeasement.getAppeasementType();
    if (lAppeasementType.equals("shipping"))
      lSkuCode =  getShippingAppeasementSku();
    else if (lAppeasementType.equals("items"))
      lSkuCode =  getItemsAppeasementSku();
    else if (lAppeasementType.equals("taxes"))
      lSkuCode =  getTaxesAppeasementSku();
    else
      lSkuCode =  getAppreciationAppeasementSku();

    // Get first commerce item to get fulfillment store
    String lFacilityCode = "0000";
    if (pOrder.getCommerceItemCount() > 0) {
      MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItems().get(0);
      lFacilityCode = lCommerceItem.getFulfillmentStore();
      if (lFacilityCode == null)
        lFacilityCode = "0000";
    }

    ShippedItem lShippedItem = new ShippedItem (lMutableRepositoryItem);
    lShippedItem.setExtractLineId             (new Long(lShippedItem.getId()));
    lShippedItem.setClientLineId              (lAppeasementId);
    lShippedItem.setSkucode                   (lSkuCode);
    lShippedItem.setBarcode                   (lSkuCode);
    lShippedItem.setItemNumber                (lSkuCode);
    lShippedItem.setColorCode                 ("N1");
    lShippedItem.setSizeCode                  ("N1");
    lShippedItem.setQuantity                  (-1L);
    lShippedItem.setUnitPrice                 (-lAppeasementAmout);
    lShippedItem.setFacilityCd                (lFacilityCode);
    lShippedItem.setShippingAmount            (0.00D);

    // Get the Tax for the line
    lShippedItem.setLineLocalTax              (0.00D);
    lShippedItem.setLineCountyTax             (0.00D);
    lShippedItem.setLineStateTax              (0.00D);
    lShippedItem.setLineTaxTotal              (0.00D);
    lShippedItem.setLineShippingTax           (0.00D);
    lShippedItem.setLineExtendedTotal         (-lAppeasementAmout);
    lShippedItem.setExtendedPrice             (-lAppeasementAmout);
    lShippedItem.setLineNumber                (new Long(lShippedItem.getId()));

    // Update Statistics
    pExtractStatistics.setLineCount(pExtractStatistics.getLineCount() + 1);
    pExtractStatistics.setTransactionTotal(pExtractStatistics.getTransactionTotal() + lAppeasementAmout);
    pExtractStatistics.setTransactionTaxTotal(pExtractStatistics.getTransactionTaxTotal() + 0.00D);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lShippedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add shipped item for order %s/%s Appeasement: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End ShippedItem for Order: {0}/{1} Appeasement: {2}", lOrderNumber, lOrderId, lAppeasementId);
    lShippedItems.add(lShippedItem);
    return lShippedItems;
  }


  /**
   * Create a shipped item for each of the appeasements.
   *
   * @param pOrder              ATG Order
   * @param pAppeasement        Appeasement
   * @param lSkuCode            The SKU for this appeasement
   * @param pExtractStatistics  Statistics for this order
   * @return                    Shipped item
   * @throws SalesAuditException
   */
  protected ShippedItem createShipmentShippedItem (MFFOrderImpl pOrder, List<String> pItemsToShip,List<ShippedItem> pShippedItems,String pShippingGroupId)
      throws SalesAuditException {
    String lOrderNumber       = pOrder.getOrderNumber();
    String lOrderId           = pOrder.getId();
    String lSkuCode           = getShippingSku();
    vlogDebug ("+++++ Begin Shipment Shipped Item for Order: {0}/{1} shipping group id: {2}", lOrderNumber, lOrderId,pShippingGroupId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_SHIPPED_ITEM);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Appeaasement shipped item for order %s/%s ShippingGroupId: %s", lOrderNumber, lOrderId,pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }

    Hashtable <String, Double> lOrderTax = getShippingAndTaxes(pOrder, pItemsToShip, pShippingGroupId);

    //As per bug 2346, now getting fulfillment store from itemsToShip
    String lFacilityCode = "0000";
    if (pOrder.getCommerceItemCount() > 0) {
      @SuppressWarnings("unchecked")
      List<MFFCommerceItemImpl> items = (List<MFFCommerceItemImpl>)pOrder.getCommerceItems();
      for(MFFCommerceItemImpl item : items){
        if(pItemsToShip.contains(item.getId())){
          if(item.getFulfillmentStore() != null){
            lFacilityCode = item.getFulfillmentStore();
            vlogDebug ("Shipment Shipped Item for Order: {0}/{1} shipping group id: {2}, Fulfillment Store:{3}",lOrderNumber, lOrderId,pShippingGroupId,lFacilityCode);
            break;
          }
        }
      }
    }

    ShippedItem lShippedItem = new ShippedItem (lMutableRepositoryItem);
    lShippedItem.setExtractLineId             (new Long(lShippedItem.getId()));

    lShippedItem.setClientLineId              (pShippingGroupId);
    lShippedItem.setSkucode                   (lSkuCode);
    lShippedItem.setBarcode                   (lSkuCode);
    lShippedItem.setItemNumber                (lSkuCode);
    lShippedItem.setColorCode                 ("N1");
    lShippedItem.setSizeCode                  ("N1");
    lShippedItem.setQuantity                  (1L);
    lShippedItem.setUnitPrice                 (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lShippedItem.setFacilityCd                (lFacilityCode);
    lShippedItem.setShippingAmount            (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));

    // Get the Tax for the line
    lShippedItem.setLineLocalTax              (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
    lShippedItem.setLineCountyTax             (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
    lShippedItem.setLineStateTax              (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
    lShippedItem.setLineTaxTotal              (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lShippedItem.setLineShippingTax           (0.00D);
    lShippedItem.setLineExtendedTotal         (roundPrice(lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lOrderTax.get(SalesAuditConstants.LINE_SHIPPING)));
    lShippedItem.setExtendedPrice             (roundPrice(lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lOrderTax.get(SalesAuditConstants.LINE_SHIPPING)));
    lShippedItem.setLineNumber                (new Long(lShippedItem.getId()));

    LineDiscount lDiscount = createShippingLineDisount(pOrder, pItemsToShip);
    if (lDiscount != null) {
      List<LineDiscount> lDiscounts = new ArrayList<LineDiscount>();
      lDiscounts.add(lDiscount);
      lShippedItem.setLineDiscounts(lDiscounts);

      //Increase the unit price by the amount that is discounted
      double lUnitPrice = lShippedItem.getUnitPrice();
      lUnitPrice = lUnitPrice + lDiscount.getAmount();

      lShippedItem.setUnitPrice(roundPrice(lUnitPrice));

    }

    LineCarton lLineCarton = createLineCarton(pOrder, pShippingGroupId);
    List<LineCarton> lLineCartons = new ArrayList<LineCarton>();
    lLineCartons.add(lLineCarton);

    String lTaxable = "false";
    if(lOrderTax != null){
      Double tax = (Double)lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX);
      if(tax > 0.00){
        lTaxable="true";
      }
    }

    List<AuxiliaryRecord> lLineAuxiliarys = createLineAuxiliarys(pOrder,lTaxable);

    lShippedItem.setLineCartons(lLineCartons);
    lShippedItem.setAuxiliaries(lLineAuxiliarys);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lShippedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add shipped item for order %s/%s ShippingGroupId: %s", lOrderNumber, lOrderId,pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End ShippedItem for Order: {0}/{1} Appeasement: {2}", lOrderNumber, lOrderId,pShippingGroupId);
    return lShippedItem;
  }

  protected ShippedItem createShipmentShippedItemForSplitOrder (MFFOrderImpl pOrder, List<String> pItemsToShip,List<ShippedItem> pShippedItems,String pShippingGroupId)
      throws SalesAuditException {
    String lOrderNumber       = pOrder.getOrderNumber();
    String lOrderId           = pOrder.getId();
    String lSkuCode           = getShippingSku();
    vlogDebug ("+++++ Begin Shipment Shipped Item for Order: {0}/{1} shipping group id: {2}", lOrderNumber, lOrderId,pShippingGroupId);

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_SHIPPED_ITEM);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Appeaasement shipped item for order %s/%s ShippingGroupId: %s", lOrderNumber, lOrderId,pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }

    Hashtable <String, Double> lOrderTax = getShippingAndTaxesForSplitOrder(pOrder, pItemsToShip, pShippingGroupId);

    //As per bug 2346, now getting fulfillment store from itemsToShip
    String lFacilityCode = "0000";
    if (pOrder.getCommerceItemCount() > 0) {
      @SuppressWarnings("unchecked")
      List<MFFCommerceItemImpl> items = (List<MFFCommerceItemImpl>)pOrder.getCommerceItems();
      for(MFFCommerceItemImpl item : items){
        if(pItemsToShip.contains(item.getId())){
          if(item.getFulfillmentStore() != null){
            lFacilityCode = item.getFulfillmentStore();
            vlogDebug ("Shipment Shipped Item for Order: {0}/{1} shipping group id: {2}, Fulfillment Store:{3}",lOrderNumber, lOrderId,pShippingGroupId,lFacilityCode);
            break;
          }
        }
      }
    }

    ShippedItem lShippedItem = new ShippedItem (lMutableRepositoryItem);
    lShippedItem.setExtractLineId             (new Long(lShippedItem.getId()));

    lShippedItem.setClientLineId              (pShippingGroupId);
    lShippedItem.setSkucode                   (lSkuCode);
    lShippedItem.setBarcode                   (lSkuCode);
    lShippedItem.setItemNumber                (lSkuCode);
    lShippedItem.setColorCode                 ("N1");
    lShippedItem.setSizeCode                  ("N1");
    lShippedItem.setQuantity                  (1L);
    lShippedItem.setUnitPrice                 (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lShippedItem.setFacilityCd                (lFacilityCode);
    lShippedItem.setShippingAmount            (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));

    // Get the Tax for the line
    lShippedItem.setLineLocalTax              (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
    lShippedItem.setLineCountyTax             (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
    lShippedItem.setLineStateTax              (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
    lShippedItem.setLineTaxTotal              (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lShippedItem.setLineShippingTax           (0.00D);
    lShippedItem.setLineExtendedTotal         (roundPrice(lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lOrderTax.get(SalesAuditConstants.LINE_SHIPPING)));
    lShippedItem.setExtendedPrice             (roundPrice(lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lOrderTax.get(SalesAuditConstants.LINE_SHIPPING)));
    lShippedItem.setLineNumber                (new Long(lShippedItem.getId()));

    LineDiscount lDiscount = createShippingLineDisount(pOrder, pItemsToShip);
    if (lDiscount != null) {
      List<LineDiscount> lDiscounts = new ArrayList<LineDiscount>();
      lDiscounts.add(lDiscount);
      lShippedItem.setLineDiscounts(lDiscounts);

      //Increase the unit price by the amount that is discounted
      double lUnitPrice = lShippedItem.getUnitPrice();
      lUnitPrice = lUnitPrice + lDiscount.getAmount();

      lShippedItem.setUnitPrice(roundPrice(lUnitPrice));

    }

    LineCarton lLineCarton = createLineCarton(pOrder, pShippingGroupId);
    List<LineCarton> lLineCartons = new ArrayList<LineCarton>();
    lLineCartons.add(lLineCarton);

    String lTaxable = "false";
    if(lOrderTax != null){
      Double tax = (Double)lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX);
      if(tax > 0.00){
        lTaxable="true";
      }
    }

    List<AuxiliaryRecord> lLineAuxiliarys = createLineAuxiliarys(pOrder,lTaxable);

    lShippedItem.setLineCartons(lLineCartons);
    lShippedItem.setAuxiliaries(lLineAuxiliarys);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lShippedItem.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add shipped item for order %s/%s ShippingGroupId: %s", lOrderNumber, lOrderId,pShippingGroupId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End ShippedItem for Order: {0}/{1} Appeasement: {2}", lOrderNumber, lOrderId,pShippingGroupId);
    return lShippedItem;
  }

 /**
  * Create an appeasement record using the CSC appeasement.
  *
  * @param lOrder           ATG Order
  * @param pAppeasement     CSC Appeasement
  * @return                 Appeasement Record
  * @throws SalesAuditException
  */
  protected List<oms.commerce.salesaudit.record.Appeasement> createAppeasementItem (MFFOrderImpl lOrder, Appeasement pAppeasement)
    throws SalesAuditException {

    String lAppeasementId  = pAppeasement.getAppeasementId();
    String lOrderNumber    = lOrder.getOrderNumber();
    String lOrderId        = lOrder.getId();
    List<oms.commerce.salesaudit.record.Appeasement> lAppeasements = new ArrayList <oms.commerce.salesaudit.record.Appeasement> ();

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_APPEASEMENT);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add appeasement item for order %s/%s Appeasement Id: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    oms.commerce.salesaudit.record.Appeasement lAppeasement = null;
    lAppeasement = new oms.commerce.salesaudit.record.Appeasement (lMutableRepositoryItem);
    lAppeasement.setAmount                (pAppeasement.getAppeasementAmount());
    lAppeasement.setAppeaseCode           (getSalesAuditAppeasementReason(pAppeasement.getReasonCode()));
    lAppeasement.setAppeaseDate           (new Timestamp (pAppeasement.getCreationDate().getTime()));
    lAppeasement.setAppeaseDescription    (getAppeasementReasonCodeDescription(pAppeasement.getReasonCode()));
    lAppeasement.setReference             (lOrderNumber);
    lAppeasements.add(lAppeasement);

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lAppeasement.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Appeasement item for Order %s/%s Appeasement Id: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    return lAppeasements;
  }

  /**
   * Convert the ATGF reason code in a Sales Audit reason code.
   *
   * @param pAppeasementReasonCode    ATG Reason code
   * @return                          Sales Audit reason code
   */
  protected String getSalesAuditAppeasementReason (String pAppeasementReasonCode) {
    String lSAReasonCode = "1";
    if (pAppeasementReasonCode.equals("didNotLikeItem"))
      lSAReasonCode   = "1";
    else if (pAppeasementReasonCode.equals("goodwillGesture"))
        lSAReasonCode = "2";
    else if (pAppeasementReasonCode.equals("incorrectItem"))
        lSAReasonCode = "3";
    else if (pAppeasementReasonCode.equals("itemArrivedDamaged"))
        lSAReasonCode = "4";
    else if (pAppeasementReasonCode.equals("itemArrivedLate"))
        lSAReasonCode = "5";
    else if (pAppeasementReasonCode.equals("orderArrivedDamaged"))
        lSAReasonCode = "6";
    else if (pAppeasementReasonCode.equals("orderArrivedLate"))
        lSAReasonCode = "7";
    else if (pAppeasementReasonCode.equals("productComplaint"))
        lSAReasonCode = "8";
    return lSAReasonCode;
  }

  /**
   * Get the description for the reason code.
   *
   * @param pReasonCode     Appeasement Reason
   * @return                Appeasement description
   * @throws SalesAuditException
   */
  protected String getAppeasementReasonCodeDescription (String pReasonCode)
      throws SalesAuditException {
    vlogDebug("Begin - Get appeasement description for {0}", pReasonCode);

    String lDescription     = "No Description";
    RepositoryItem[] lItems = null;
    RepositoryItem lItem    = null;
    try {
      Object[] lParams        = new Object[2];
      lParams[0]              = pReasonCode;
      RepositoryView lView    = getOmsOrderManager().getOmsOrderRepository().getView("appeasementReasons");
      RqlStatement lStatement = RqlStatement.parseRqlStatement("description EQUALS ?0");
      lItems                  = lStatement.executeQuery(lView, lParams);
      if (lItems != null && lItems.length > 0) {
        lItem = lItems[0];
        lDescription = (String) lItem.getPropertyValue("readableDescription");
      }
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("getorder appeasement reason - Error locating reason code: %s", pReasonCode);
      vlogError(ex, lErrorMessage);
      throw new SalesAuditException(lErrorMessage, ex);
    }
    return lDescription;
  }

  /**
   * Get the tax for all of the appeased lines.
   *
   * @param pAppeasement          CSC Appeasement
   * @param pOrder                ATG Order
   * @return                      Hash table of taxes
   * @throws SalesAuditException
   */
  protected Hashtable <String, Double> getTaxForAppeasedLines (Appeasement pAppeasement, MFFOrderImpl pOrder)
      throws SalesAuditException {

    Hashtable <String, Double> lGroupTax = new Hashtable <String, Double> ();
    lGroupTax.put(SalesAuditConstants.LINE_LOCAL_TAX,           0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_COUNTY_TAX,          0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_STATE_TAX,           0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_SHIPPING,            0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX,  0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX, 0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_SHIPPING_STATE_TAX,  0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_TAX_TOTAL,           0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_SHIPPING_TAX,        0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,      0.00D);
    lGroupTax.put(SalesAuditConstants.LINE_DISCOUNT,            0.00D);

    double lAmount  = pAppeasement.getAppeasementAmount();
    String lType    = pAppeasement.getAppeasementType();
    if (lType.equals("shipping"))
      lGroupTax.put(SalesAuditConstants.LINE_SHIPPING,           lAmount);
    else if (lType.equals("tax"))
      lGroupTax.put(SalesAuditConstants.LINE_TAX_TOTAL,          lAmount);
    else
      lGroupTax.put(SalesAuditConstants.LINE_EXTENDED_TOTAL,     lAmount);

    Set<String> lKeys = lGroupTax.keySet();
    for(String lKey: lKeys) {
      //lGroupTax.put(lKey, roundDouble (lGroupTax.get(lKey), 2));
      lGroupTax.put(lKey, roundPrice(lGroupTax.get(lKey)));
    }
    return lGroupTax;
  }


  /**
   * Create an appeasement invoice item.
   *
   * @param pAppeasement              CSC Appeasement
   * @param pOrder                    ATG Order
   * @param pAppeasementRecords       Invoice Appeasement records
   * @param pExtractStatistics        Extract statistics
   * @param pInvoiceShippingAddress   Shipping address record
   * @param pInvoiceBillingAddress    Billing Address record
   * @return                          Invoice record
   * @throws SalesAuditException
   */
  protected Invoice createAppeasementInvoiceItem (Appeasement pAppeasement, MFFOrderImpl pOrder, List<ShippedItem> pShippedItems,
      ExtractStatistics pExtractStatistics,
      InvoiceAddress pInvoiceShippingAddress, InvoiceAddress pInvoiceBillingAddress)
      throws SalesAuditException {

    String lOrderNumber     = pOrder.getOrderNumber();
    String lOrderId         = pOrder.getId();
    String lAppeasementId   = pAppeasement.getAppeasementId();
    String lShippingMethod  = "Standard";

    vlogDebug ("+++++ Begin createApppeasementInvoice for Order: {0}/{1} Appeasement Id: {2}", lOrderNumber, lOrderId, lAppeasementId);

    // Get the first shipping group
    ShippingGroup lShippingGroup  = (ShippingGroup) pOrder.getShippingGroups().get(0);
    if (lShippingGroup != null)
      lShippingMethod = lShippingGroup.getShippingMethod();

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_INVOICE);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Invoice for order %s/%s Appeasement Id: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    Invoice lInvoice = new Invoice (lMutableRepositoryItem);
    lInvoice.setOrderNumber               (lOrderNumber);
    if(pAppeasement != null && pAppeasement.getCreationDate() != null) {
    	lInvoice.setOrderDate                 (new Timestamp (pAppeasement.getCreationDate().getTime()));
    } else {
    	lInvoice.setOrderDate                 (new Timestamp (System.currentTimeMillis()));
    }
    lInvoice.setSource                    (SalesAuditConstants.ORDER_SOURCE);
    lInvoice.setOrderType                 (SalesAuditConstants.ORDER_TYPE);
    lInvoice.setBusinessType              (SalesAuditConstants.BUSINESS_TYPE);
    lInvoice.setLoyaltyIdentifier         (null);
    lInvoice.setCustomerPurchaseOrder     (null);
    lInvoice.setReference                 (lInvoice.getId());
    lInvoice.setFleetFarmId               (pOrder.getEmployeeId());

    // Tax Exemption
    Hashtable <String, String> lTaxExemptionHash = getTaxExemptionCertificate (pOrder);
    lInvoice.setTaxExemptionCertificate   (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CERT));
    lInvoice.setTaxExemptionName          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_NAME));
    lInvoice.setTaxExemptionType          (lTaxExemptionHash.get(SalesAuditConstants.TAX_EXEMPTION_CODE));

    // Tax
    Hashtable <String, Double> lOrderTax = getTaxForAppeasedLines (pAppeasement, pOrder);
    lInvoice.setOrderShipping             (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setOrderShippingLocalTax     (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_LOCAL_TAX));
    lInvoice.setOrderShippingCountyTax    (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_COUNTY_TAX));
    lInvoice.setOrderShippingStateTax     (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_STATE_TAX));
    lInvoice.setOrderShippingTotal        (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingTax          (lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX));
    lInvoice.setOrderShippingExtendedTotal(lOrderTax.get(SalesAuditConstants.LINE_SHIPPING_TAX) + lOrderTax.get(SalesAuditConstants.LINE_SHIPPING));
    lInvoice.setShipVia                   (getShipViaFromMethod(lShippingMethod));
    lInvoice.setStatus                    (SalesAuditConstants.EXTRACT_STATUS_CREATED);
    lInvoice.setLastExtractDate           (null);
    lInvoice.setExtract                   (null);
    lInvoice.setShippingAddress           (pInvoiceShippingAddress);
    lInvoice.setBillingAddress            (pInvoiceBillingAddress);
    lInvoice.setShippedItems              (pShippedItems);

    // Create order auxiliaries
    List<AuxiliaryRecord> lAuxiliaryRecords = createOrderAuxiliarys (pOrder);
    lInvoice.setAuxiliaries(lAuxiliaryRecords);

    // Create Payments
    List<LinePayment> lLinePayments = createAppeasementPayments (pAppeasement, pOrder, pExtractStatistics);
    lInvoice.setPayments(lLinePayments);

    // Create Summary
    LineSummary lLineSummary = createLineSummary (pOrder, null, pExtractStatistics);
    lInvoice.setLineSummary(lLineSummary);

    updateSummaryStatsForCredit(pExtractStatistics);
    //vlogDebug (lInvoice.toString());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lInvoice.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add Appeasement Invoice for Order %s/%s Appeasement Id: %s", lOrderNumber, lOrderId, lAppeasementId);
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End Create Appeasement Invoice Item for Order: {0}/{1} Appeasement: {2}", lOrderNumber, lOrderId, lAppeasementId);
    return lInvoice;
  }

  /**
   * Create payments for the appeased requests.
   *
   * @param pReturnRequest        Appeasement Request
   * @param pExtractStatistics    Line Statistics
   * @return                      List of payments
   * @throws SalesAuditException
   */
  @SuppressWarnings("unchecked")
  protected List<LinePayment> createAppeasementPayments (Appeasement pAppeasement, MFFOrderImpl pOrder, ExtractStatistics pExtractStatistics)
      throws SalesAuditException {

    List <LinePayment> lLinePayments = new Vector <LinePayment> ();
    String lOrderNumber     = pOrder.getOrderNumber();
    String lOrderId         = pOrder.getId();
    String lAppeasementId   = pAppeasement.getAppeasementId();

    vlogDebug ("+++++ Begin Create Appeasement Line Payments for Order : {0}/{1} Appeasement Id: {2}", lOrderNumber, lOrderId, lAppeasementId);
    Timestamp lShipTimestamp            = new Timestamp (pAppeasement.getCreationDate().getTime());

    // Loop through all the items and get payment group(s) for this return
    List<Refund> lRefunds = pAppeasement.getRefundList();
    for (Refund lRefund : lRefunds) {
      double lPaymentGroupAmount = getNegativeValue(lRefund.getAmount());
      MutableRepositoryItem lMutableRepositoryItem = null;
      try {
        lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_PAYMENT);
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to add line payment for order %s/%s", lOrderNumber, lOrderId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
      LinePayment lLinePayment = new LinePayment (lMutableRepositoryItem);
      if (lRefund.getRefundType().equals("creditCard")) {
        CreditCardRefund lCreditCardRefund = (CreditCardRefund) lRefund;
        CreditCard lCreditCard = lCreditCardRefund.getCreditCard();
        lLinePayment.setAmount                  (lPaymentGroupAmount);
        lLinePayment.setCardNumber              ("************" + lCreditCard.getCreditCardNumber());
        lLinePayment.setCardReference           (lCreditCard.getCreditCardNumber());
        lLinePayment.setPaymentDate             (lShipTimestamp);
        lLinePayment.setPaymentType             (getMillsPaymentMethod(lCreditCard.getCreditCardType()));
        lLinePayment.setTokenId                 ((String) (lCreditCard.getPropertyValue("tokenNumber")));

        List <PaymentStatus> lCreditCardStatuses = lCreditCard.getAuthorizationStatus();
        for (PaymentStatus lCreditCardStatus : lCreditCardStatuses) {
          if (lCreditCardStatus.getTransactionSuccess())
            lLinePayment.setTransactionReference  (lCreditCardStatus.getTransactionId());
        }

        List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (pOrder, lCreditCard, "CreditCard");
        lLinePayment.setAuxiliaries(lAuxiliaryRecords);
        pExtractStatistics.setPaymentCount      (pExtractStatistics.getPaymentCount() + 1);
        pExtractStatistics.setPaymentTotal      (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
      }
      if (lRefund.getRefundType().equals("giftCard")) {
        MFFGiftCardRefundMethod lGiftCardRefundMethod = (MFFGiftCardRefundMethod) lRefund;
        MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) lGiftCardRefundMethod.getPaymentGroup();
        lLinePayment.setAmount                (lPaymentGroupAmount);
        // Card number, card reference and token Id and reference will be null for GC returns
        lLinePayment.setCardNumber            (null);
        lLinePayment.setCardReference         (null);
        lLinePayment.setPaymentDate           (lShipTimestamp);
        lLinePayment.setPaymentType           (SalesAuditConstants.GIFT_CARD_PAYMENT_TYPE);
        lLinePayment.setTokenId               (null);
        lLinePayment.setTransactionReference  (null);
        if (lGiftCardPaymentGroup != null) {
          List<AuxiliaryRecord> lAuxiliaryRecords = createPaymentAuxiliarys (pOrder, lGiftCardPaymentGroup, "GiftCard");
          lLinePayment.setAuxiliaries(lAuxiliaryRecords);
        }
        pExtractStatistics.setPaymentCount    (pExtractStatistics.getPaymentCount() + 1);
        pExtractStatistics.setPaymentTotal    (pExtractStatistics.getPaymentTotal() + lPaymentGroupAmount);
      }
      updateSummaryStatsForCredit(pExtractStatistics);
      try {
        getInvoiceRepository().addItem(lLinePayment.getRepositoryItem());
        lLinePayments.add(lLinePayment);
      } catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to add Appeasement payment for Order %s/%s RMA: %s", lOrderNumber, lOrderId, lAppeasementId);
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogDebug ("+++++ End Create Appeasement Line Payments for Order : {0}/{1} RMA: {2}", lOrderNumber, lOrderId, lAppeasementId);
    return lLinePayments;
  }


  // ***********************************************************************************
  //
  //
  //                          Get a list of invoice records
  //
  //
  // ***********************************************************************************
  //public List <RepositoryItem> getInvoiceRecord () {
  //  vlogDebug ("Get Invoice Records");
  //  return null;
  //}


  /**
   * Get the list of invoices that are ready for processing.
   *
   * @return        Array of Invoice Repository items
   * @throws SalesAuditException
   */
  public RepositoryItem [] getInvoicesToProcess (String pRunType, String pFileName)
    throws SalesAuditException {
    vlogDebug ("Begin - Pull Invoices for the Sales Audit feed");
    String lQuery = null;
    RepositoryItem [] lInvoiceItems     = null;
    if (pRunType.equals(SalesAuditConstants.RUN_TYPE_STANDARD)) {
      lQuery = "status EQUALS \"created\"";
      vlogDebug ("Pulling invoices for a standard run - Query: " + lQuery);
    }
    else {
      lQuery = "status EQUALS \"extracted\" AND extract.extractFileName EQUALS " + "\"" + pFileName + "\"";
      vlogDebug ("Pulling invoices for a re-run - Query: " + lQuery);
    }
    try {
      Object [] lParams         = new String[1];
      lParams[0]                = SalesAuditConstants.EXTRACT_TO_SALES_AUDIT;
      RepositoryView lView      = getInvoiceRepository().getView(SalesAuditConstants.ITEM_INVOICE);
      RqlStatement lStatement   = RqlStatement.parseRqlStatement(lQuery);
      lInvoiceItems             = lStatement.executeQuery(lView, lParams);
      if (lInvoiceItems != null)
        vlogDebug ("Found " + lInvoiceItems.length + " Invoice items for extraction");
    }
    catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to get list of invoices");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("End - Pull Invoices for the Sales Audit feed");
    return lInvoiceItems;
  }


  /**
   * Get the extract record for the given file name.  This is used during a re-run
   * of a given feed.
   *
   * @param pFileName           Extract file Name
   * @return                    Extract record
   * @throws SalesAuditException
   */
  public Extract getExtractRecordForFile (String pFileName)
      throws SalesAuditException {
      vlogDebug ("Begin - Get Extract record for filename");
      String lQuery = null;
      Extract lExtract = null;
      RepositoryItem [] lExtracts     = null;
      lQuery = "extractFileName EQUALS " + "\"" + pFileName + "\"";
      vlogDebug ("Pulling extract record for file run " + pFileName);
      try {
        Object [] lParams         = new String[1];
        RepositoryView lView      = getInvoiceRepository().getView(SalesAuditConstants.ITEM_EXTRACT);
        RqlStatement lStatement   = RqlStatement.parseRqlStatement(lQuery);
        lExtracts                 = lStatement.executeQuery(lView, lParams);
        vlogDebug ("Found " + lExtracts.length + " extract item for file name");
      }
      catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to get extract record");
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
      vlogDebug ("Begin - Get Extract record for filename");
      if (lExtracts != null && lExtracts.length == 1) {
        lExtract = new Extract ((MutableRepositoryItem) lExtracts[0]);
      }
      return lExtract;
    }


  // ***********************************************************************************
  //
  //
  //                          Update status of invoice records
  //
  //
  // ***********************************************************************************

  /**
   * Update the status of the invoice records to show they have been extracted.
   *
   * @param pInvoices         List of Invoice repository items
   * @param pTimestamp        Timestamp of current run
   * @param pExtract          Extract record for this run
   * @throws SalesAuditException
   */
  public void updateExtractStatus (RepositoryItem [] pInvoices, Timestamp pTimestamp, Extract pExtract)
      throws SalesAuditException {
    vlogDebug ("+++++ Begin - Update the Extract Status on the invoice records");

    boolean lRollback         = true;
    TransactionDemarcation td = new TransactionDemarcation();
    Invoice lInvoice          = null;
    try {
      // Create a transaction
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      for (int i=0; i< pInvoices.length; i++) {
        lInvoice = new Invoice ((MutableRepositoryItem) pInvoices[i]);
        vlogDebug ("Before update of Invoice");
        vlogDebug (lInvoice.toString());

        // Set filename and timestamp
        lInvoice.setLastExtractDate(pTimestamp);
        lInvoice.setStatus (SalesAuditConstants.EXTRACT_STATUS_EXTRACTED);
        lInvoice.setExtract(pExtract);

        vlogDebug ("After update of invoice");
        vlogDebug (lInvoice.toString());
        getInvoiceRepository().updateItem(lInvoice.getRepositoryItem());
      }
      lRollback = false;
    }
    catch (TransactionDemarcationException ex) {
      String lErrorMessage = String.format("updateExtractStatus (1) - Unable to update invoice # %s");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    catch (RepositoryException ex) {
      String lErrorMessage = String.format("updateExtractStatus (2) - Unable to update invoice # %s", lInvoice.getId());
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    finally {
      try {
        td.end (lRollback);
      }
      catch (TransactionDemarcationException ex) {
        String lErrorMessage = String.format("updateExtractStatus (3) - Unable to update invoice # %s", lInvoice.getId());
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogDebug ("+++++ End - Update the Extract Status on the invoice records");
  }

  /**
   * Update the invoice item to error if there is an issue extracting the item to the
   * sales audit file.
   *
   * @param pInvoice            Invoice number
   * @param pTimestamp          Extract time
   * @throws SalesAuditException
   */
  public void updateInvoiceStatusToError (Invoice pInvoice, Timestamp pTimestamp)
      throws SalesAuditException {
    vlogDebug ("+++++ Begin - Update the Extract Status to error on the invoice record");

    boolean lRollback         = true;
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      // Create a transaction
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      vlogDebug ("Before update of Invoice");
      vlogDebug (pInvoice.toString());

      // Set filename and timestamp
      pInvoice.setLastExtractDate(pTimestamp);
      pInvoice.setStatus (SalesAuditConstants.EXTRACT_STATUS_ERROR);

      vlogDebug ("After update of invoice");
      vlogDebug (pInvoice.toString());
      getInvoiceRepository().updateItem(pInvoice.getRepositoryItem());
      lRollback = false;
    }
    catch (TransactionDemarcationException ex) {
      String lErrorMessage = String.format("updateInvoiceStatusToError (1) - Unable to update invoice # %s", pInvoice.getId());
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    catch (RepositoryException ex) {
      String lErrorMessage = String.format("updateInvoiceStatusToError (2) - Unable to update invoice # %s", pInvoice.getId());
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    finally {
      try {
        td.end (lRollback);
      }
      catch (TransactionDemarcationException ex) {
        String lErrorMessage = String.format("updateInvoiceStatusToError (3) - Unable to update invoice # %s", pInvoice.getId());
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    vlogDebug ("+++++ End - Update the Extract Status to error on the invoice record");
  }

  // ***********************************************************************************
  //
  //
  //                          Add Extract Status Routines
  //
  //
  // ***********************************************************************************

  /**
   * Create the extract status record for the current run.
   *
   * @param pExtractStatistics        Statistics for the current run
   * @param pFileName                 Filename for the current run
   * @param pRunType                  Type of run
   * @return                          Extract record
   * @throws SalesAuditException
   */
  public Extract addExtractStatus (ExtractStatistics pExtractStatistics, String pFileName, String pRunType)
      throws SalesAuditException {
    vlogDebug ("Add Extract Summary and Payments");

    boolean lRollback         = true;
    TransactionDemarcation td = new TransactionDemarcation();
    Extract lExtract          = null;
    try {
      // Create a transaction
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Create Extract Summary
      ExtractSummary lExtractSummary = createExtractSummary (pExtractStatistics);

      // Create Payment Summary
      List<PaymentSummary> lPaymentSummarys = new Vector <PaymentSummary> ();
      for (ExtractPaymentSummary lExtractPaymentSummary : pExtractStatistics.getExtractPaymentSummary()) {
        PaymentSummary lPaymentSummary = createPaymentSummary (lExtractPaymentSummary);
        lPaymentSummarys.add(lPaymentSummary);
      }
      // Create Extract
      lExtract = createExtract (lExtractSummary, lPaymentSummarys, pFileName, pRunType);

      lRollback = false;
    }
    catch (TransactionDemarcationException ex) {
      String lErrorMessage = String.format("addExtractStatus (1) - Unable to add extract status");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    finally {
      try {
        td.end (lRollback);
      }
      catch (TransactionDemarcationException ex) {
        String lErrorMessage = String.format("addExtractStatus (2) - Unable to add extract status");
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
    }
    return lExtract;
  }

  /**
   * Create the extract record for the current run.
   *
   * @param pExtractSummary       Extract Summary
   * @param pPaymentSummarys      List of Payments for the file
   * @param pFileName             Name of the extract file
   * @param pRunType              Type of run
   * @return
   * @throws SalesAuditException
   */
  protected Extract createExtract (ExtractSummary pExtractSummary, List<PaymentSummary> pPaymentSummarys, String pFileName, String pRunType)
      throws SalesAuditException {
    vlogDebug ("+++++ Begin - create extract");

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_EXTRACT);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add extract");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    Extract lExtract = new Extract(lMutableRepositoryItem);
    lExtract.setExtractDate                 (new Timestamp(new Date().getTime()));
    lExtract.setExtractFileName             (pFileName);
    lExtract.setRunType                     (pRunType);
    lExtract.setExtractSummary              (pExtractSummary);
    lExtract.setPaymentSummary              (pPaymentSummarys);
    vlogDebug   (lExtract.toString());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lExtract.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add extract summary");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End - create extract");
    return lExtract;
  }

  /**
   * Create the extract summary for the current run.
   *
   * @param pExtractStatistics      Extract statistics
   * @return                        Extract Summary
   * @throws SalesAuditException
   */
  protected ExtractSummary createExtractSummary (ExtractStatistics pExtractStatistics)
      throws SalesAuditException {
    vlogDebug ("+++++ Begin - create extract summary");

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_EXTRACT_SUMMARY);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add extract summary");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    ExtractSummary lExtractSummary = new ExtractSummary(lMutableRepositoryItem);
    lExtractSummary.setTransactionTotal         (pExtractStatistics.getTransactionTotal());
    lExtractSummary.setTransactionTaxableTotal  (pExtractStatistics.getTransactionTaxableTotal());
    lExtractSummary.setTransactionTaxTotal      (pExtractStatistics.getTransactionTaxTotal());
    lExtractSummary.setLineCount                (pExtractStatistics.getLineCount());
    lExtractSummary.setTransactionCount         (pExtractStatistics.getTransactionCount());
    lExtractSummary.setPaymentTotal             (pExtractStatistics.getPaymentTotal());
    lExtractSummary.setPaymentCount             (pExtractStatistics.getPaymentCount());
    lExtractSummary.setDiscountTotal            (pExtractStatistics.getDiscountTotal());
    lExtractSummary.setDiscountCount            (pExtractStatistics.getDiscountCount());
    lExtractSummary.setGiftcardSoldTotal        (pExtractStatistics.getGiftcardSoldTotal());
    lExtractSummary.setGiftcardSoldCount        (pExtractStatistics.getGiftcardSoldCount());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lExtractSummary.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add extract summary");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    vlogDebug ("+++++ End - create extract summary");
    return lExtractSummary;
  }

  /**
   * Create the payment summary for the current run.
   *
   * @param pExtractPaymentSummary      Payment summary
   * @return                            Payment summary record
   * @throws SalesAuditException
   */
  protected PaymentSummary createPaymentSummary (ExtractPaymentSummary pExtractPaymentSummary)
      throws SalesAuditException {
    vlogDebug ("+++++ Begin - create payment summary");

    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getInvoiceRepository().createItem (SalesAuditConstants.ITEM_PAYMENT_SUMMARY);
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add payment summary");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }
    PaymentSummary lPaymentSummary = new PaymentSummary (lMutableRepositoryItem);
    lPaymentSummary.setPaymentType            (pExtractPaymentSummary.getPaymentType());
    lPaymentSummary.setCreditTotal            (pExtractPaymentSummary.getCreditTotal());
    lPaymentSummary.setCreditCount            (pExtractPaymentSummary.getCreditCount());
    lPaymentSummary.setDebitTotal             (pExtractPaymentSummary.getDebitTotal());
    lPaymentSummary.setDebitCount             (pExtractPaymentSummary.getDebitCount());

    // Add item to repository
    try {
      getInvoiceRepository().addItem(lPaymentSummary.getRepositoryItem());
    } catch (RepositoryException ex) {
      String lErrorMessage = String.format("Unable to add extract summary");
      vlogError (ex, lErrorMessage);
      throw new SalesAuditException (ex, lErrorMessage);
    }

    vlogDebug ("+++++ End - create payment summary");
    return lPaymentSummary;
  }

  /**
   * Round a double up.
   *
   * @param pDouble       Double value
   * @param pPrecision    Rounding Precision
   * @return              Rounded double
   */
  protected double roundDouble (double pDouble, int pPrecision) {
    return (new BigDecimal (pDouble).setScale(pPrecision, BigDecimal.ROUND_HALF_UP).doubleValue());
  }

  protected double roundDiscount(double pDouble) {
    return roundDouble(pDouble, 4);
  }

  protected double roundPrice (double pDouble) {
    return roundDouble(pDouble, 2);
  }
  /**
   * Format a double into a Big Decimal
   * @param pDoubleValue        Double value
   * @return                    Big Decimal
   */
  protected BigDecimal formatBigDecimal (Double pDoubleValue) {
    return (new BigDecimal (new Double(roundDouble(pDoubleValue.doubleValue(), 2)).toString()));
  }

  protected String getShipViaFromMethod (String pShippingMethod) {
    vlogDebug ("***** Get Shipping Via for {0} from method", pShippingMethod);
    String lShipVia = "RGND";
    if (pShippingMethod.equals("Standard"))
      lShipVia = "RGND";
    else if (pShippingMethod.equals("SecondDay"))
      lShipVia = "RSEC";
    else if (pShippingMethod.equalsIgnoreCase("Overnight"))
      lShipVia = "ROVR";
    vlogDebug ("***** Shipping Via is {0} for method {1}", lShipVia, pShippingMethod);
    return lShipVia;
  }

  // *********************************************************
  //                  Getter/setters
  // *********************************************************
  MutableRepository mInvoiceRepository;
  public MutableRepository getInvoiceRepository() {
    return mInvoiceRepository;
  }
  public void setInvoiceRepository(MutableRepository pInvoiceRepository) {
    this.mInvoiceRepository = pInvoiceRepository;
  }

  MFFCatalogTools mCatalogTools;
  public MFFCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  public void setCatalogTools(MFFCatalogTools pCatalogTools) {
    this.mCatalogTools = pCatalogTools;
  }

  MFFOMSOrderManager  mOmsOrderManager;
  public MFFOMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;  }

  public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
    this.mOmsOrderManager = pOmsOrderManager;
  }

  TransactionManager mTransactionManager;
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }
  public void setTransactionManager(TransactionManager pTransactionManager) {
    this.mTransactionManager = pTransactionManager;
  }

  MFFProfileTools mProfileTools;
  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }
  public void setProfileTools(MFFProfileTools pProfileTools) {
    this.mProfileTools = pProfileTools;
  }

  String mShippingAppeasementSku;
  public String getShippingAppeasementSku() {
    return mShippingAppeasementSku;
  }
  public void setShippingAppeasementSku(String pShippingAppeasementSku) {
    this.mShippingAppeasementSku = pShippingAppeasementSku;
  }

  String mShippingSku;
  public String getShippingSku() {
    return mShippingSku;
  }

  public void setShippingSku(String pShippingSku) {
    mShippingSku = pShippingSku;
  }

  String mItemsAppeasementSku;
  public String getItemsAppeasementSku() {
    return mItemsAppeasementSku;
  }
  public void setItemsAppeasementSku(String pItemsAppeasementSku) {
    this.mItemsAppeasementSku = pItemsAppeasementSku;
  }

  String mTaxesAppeasementSku;
  public String getTaxesAppeasementSku() {
    return mTaxesAppeasementSku;
  }
  public void setTaxesAppeasementSku(String pTaxesAppeasementSku) {
    this.mTaxesAppeasementSku = pTaxesAppeasementSku;
  }

  String mAppreciationAppeasementSku;
  public String getAppreciationAppeasementSku() {
    return mAppreciationAppeasementSku;
  }

  public void setAppreciationAppeasementSku(String pAppreciationAppeasementSku) {
    this.mAppreciationAppeasementSku = pAppreciationAppeasementSku;
  }

  List<String> mGwpGCSkuIds;
  public List<String> getGwpGCSkuIds() {
    return mGwpGCSkuIds;
  }

  public void setGwpGCSkuIds(List<String> pGwpGCSkuIds) {
    mGwpGCSkuIds = pGwpGCSkuIds;
  }

  String mGwpGCReplacementSkuId;
  public String getGwpGCReplacementSkuId() {
    return mGwpGCReplacementSkuId;
  }

  public void setGwpGCReplacementSkuId(String pGwpGCReplacementSkuId) {
    mGwpGCReplacementSkuId = pGwpGCReplacementSkuId;
  }

  public String getDefaultReturnStoreID() {
    return mDefaultReturnStoreID;
  }

  public void setDefaultReturnStoreID(String pDefaultReturnStoreID) {
    mDefaultReturnStoreID = pDefaultReturnStoreID;
  }

  public String getDefaultReturnShippingReasonCode() {
    return defaultReturnShippingReasonCode;
  }

  public void setDefaultReturnShippingReasonCode(String pDefaultReturnShippingReasonCode) {
    defaultReturnShippingReasonCode = pDefaultReturnShippingReasonCode;
  }

  public Map<String, String> getReturnReasonCodes() {
    return returnReasonCodes;
  }

  public void setReturnReasonCodes(Map<String, String> pReturnReasonCodes) {
    returnReasonCodes = pReturnReasonCodes;
  }

  public String getDefaultReturnReasonCode() {
    return defaultReturnReasonCode;
  }

  public void setDefaultReturnReasonCode(String pDefaultReturnReasonCode) {
    defaultReturnReasonCode = pDefaultReturnReasonCode;
  }

  public MFFReturnManager getReturnManager() {
    return returnManager;
  }

  public void setReturnManager(MFFReturnManager pReturnManager) {
    returnManager = pReturnManager;
  }

}
