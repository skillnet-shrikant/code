package oms.commerce.salesaudit.record;

import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

/**
 * This class represents an invoice record in the invoice repository.  
 * 
 * @author jvose
 *
 */
public class Invoice 
    extends SalesAuditItem {
  
  /** Default Constructor **/
  public Invoice () {    
  }
  
  /** Other Constructor **/
  public Invoice (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }  
  
  /**  invoice - id    **/
  public String getId () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_INVOICE_ID);
  }
  public void  setId (String pId ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_INVOICE_ID, pId );
  }

  /**  invoice - fleetFarmId    **/
  public String getFleetFarmId () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_FLEET_FARM_ID);
  }
  public void  setFleetFarmId (String pFleetFarmId ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_FLEET_FARM_ID, pFleetFarmId );
  }
  
  /**  invoice - orderNumber    **/
  public String getOrderNumber () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_NUMBER);
  }
  public void  setOrderNumber (String pOrderNumber ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_NUMBER, pOrderNumber );
  }
   
  /**  invoice - orderDate    **/
  public Timestamp getOrderDate () {
     return (Timestamp) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_DATE);
  }
  public void  setOrderDate (Timestamp pOrderDate ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_DATE, pOrderDate );
  }
   
  /**  invoice - source    **/
  public String getSource () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SOURCE);
  }
  public void  setSource (String pSource ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SOURCE, pSource );
  }
   
  /**  invoice - orderType    **/
  public String getOrderType () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_TYPE);
  }
  public void  setOrderType (String pOrderType ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_TYPE, pOrderType );
  }
   
  /**  invoice - businessType    **/
  public String getBusinessType () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_BUSINESS_TYPE);
  }
  public void  setBusinessType (String pBusinessType ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_BUSINESS_TYPE, pBusinessType );
  }
   
  /**  invoice - loyaltyIdentifier    **/
  public String getLoyaltyIdentifier () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_LOYALTY_IDENTIFIER);
  }
  public void  setLoyaltyIdentifier (String pLoyaltyIdentifier ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_LOYALTY_IDENTIFIER, pLoyaltyIdentifier );
  }
   
  /**  invoice - customerPurchaseOrder    **/
  public String getCustomerPurchaseOrder () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_CUSTOMER_PURCHASE_ORDER);
  }
  public void  setCustomerPurchaseOrder (String pCustomerPurchaseOrder ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_CUSTOMER_PURCHASE_ORDER, pCustomerPurchaseOrder );
  }
   
  /**  invoice - reference    **/
  public String getReference () {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_REFERENCE);
  }
  public void  setReference (String pReference ) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_REFERENCE, pReference );
  }
   
  /**  invoice - taxExemptionCertificate   **/
  public String getTaxExemptionCertificate() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_TAX_EXEMPTION_CERTIFICATE);
  }
  public void  setTaxExemptionCertificate(String pTaxExemptionCertificate) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_TAX_EXEMPTION_CERTIFICATE, pTaxExemptionCertificate);
  }
   
  /**  invoice - taxExemptionName   **/
  public String getTaxExemptionName() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_TAX_EXEMPTION_NAME);
  }
  public void  setTaxExemptionName(String pTaxExemptionName) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_TAX_EXEMPTION_NAME, pTaxExemptionName);
  }
   
  /**  invoice - taxExemptionType   **/
  public String getTaxExemptionType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_TAX_EXEMPTION_TYPE);
  }
  public void  setTaxExemptionType(String pTaxExemptionType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_TAX_EXEMPTION_TYPE, pTaxExemptionType);
  }
   
  /**  invoice - orderShipping   **/
  public Double getOrderShipping() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING);
  }
  public void  setOrderShipping(Double pOrderShipping) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING, pOrderShipping);
  }
   
  /**  invoice - orderShippingLocalTax   **/
  public Double getOrderShippingLocalTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_LOCAL_TAX);
  }
  public void  setOrderShippingLocalTax(Double pOrderShippingLocalTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_LOCAL_TAX, pOrderShippingLocalTax);
  }
   
  /**  invoice - orderShippingCountyTax   **/
  public Double getOrderShippingCountyTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_COUNTY_TAX);
  }
  public void  setOrderShippingCountyTax(Double pOrderShippingCountyTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_COUNTY_TAX, pOrderShippingCountyTax);
  }
   
  /**  invoice - orderShippingStateTax   **/
  public Double getOrderShippingStateTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_STATE_TAX);
  }
  public void  setOrderShippingStateTax(Double pOrderShippingStateTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_STATE_TAX, pOrderShippingStateTax);
  }
   
  /**  invoice - orderShippingTotal   **/
  public Double getOrderShippingTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_TOTAL);
  }
  public void  setOrderShippingTotal(Double pOrderShippingTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_TOTAL, pOrderShippingTotal);
  }
   
  /**  invoice - orderShippingTax   **/
  public Double getOrderShippingTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_TAX);
  }
  public void  setOrderShippingTax(Double pOrderShippingTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_TAX, pOrderShippingTax);
  }
   
  /**  invoice - orderShippingExtendedTotal   **/
  public Double getOrderShippingExtendedTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_EXTENDED_TOTAL);
  }
  public void  setOrderShippingExtendedTotal(Double pOrderShippingExtendedTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ORDER_SHIPPING_EXTENDED_TOTAL, pOrderShippingExtendedTotal);
  }
   
  /**  invoice - shipVia   **/
  public String getShipVia() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SHIP_VIA);
  }
  public void  setShipVia(String pShipVia) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SHIP_VIA, pShipVia);
  }
   
  /**  invoice - status   **/
  public String getStatus() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_STATUS);
  }
  public void  setStatus(String pStatus) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_STATUS, pStatus);
  }
   
  /**  invoice - lastExtractDate   **/
  public Timestamp getLastExtractDate() {
     return (Timestamp) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_LAST_EXTRACT_DATE);
  }
  public void  setLastExtractDate(Timestamp pLastExtractDate) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_LAST_EXTRACT_DATE, pLastExtractDate);
  }
   
  /**  invoice - extract    **/
  public Extract getExtract () {
    MutableRepositoryItem lItem =  (MutableRepositoryItem) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_EXTRACT);
    return new Extract (lItem);
  }
  public void  setExtract (Extract pExtract ) {
    RepositoryItem lRepositoryItem = null;
    if (pExtract != null)
      lRepositoryItem = pExtract.getRepositoryItem(); 
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_EXTRACT, lRepositoryItem);
  }
   
  /**  invoice - shippingAddress     **/
  public InvoiceAddress getShippingAddress  () {
    MutableRepositoryItem lItem =  (MutableRepositoryItem) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SHIPPING_ADDRESS);
    return new InvoiceAddress(lItem);
  }
  public void setShippingAddress  (InvoiceAddress pShippingAddress) {
    RepositoryItem lRepositoryItem = null;
    if (pShippingAddress != null)
      lRepositoryItem = pShippingAddress.getRepositoryItem(); 
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SHIPPING_ADDRESS, lRepositoryItem);
  }
   
  /**  invoice - billingAddress      **/
  public InvoiceAddress getBillingAddress   () {
    MutableRepositoryItem lItem =  (MutableRepositoryItem) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_BILLING_ADDRESS);
    return new InvoiceAddress (lItem); 
  }
  public void  setBillingAddress   (InvoiceAddress pBillingAddress) {
    RepositoryItem lRepositoryItem = null;
    if (pBillingAddress != null)
      lRepositoryItem = pBillingAddress.getRepositoryItem(); 
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_BILLING_ADDRESS, lRepositoryItem);
  }
   
  /**  invoice - lineSummary     **/
  public LineSummary getLineSummary () {
    MutableRepositoryItem lItem =  (MutableRepositoryItem) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_LINE_SUMMARY);
    return new LineSummary (lItem); 
  }
  public void  setLineSummary  (LineSummary pLineSummary  ) {
    RepositoryItem lRepositoryItem = null;
    if (pLineSummary != null)
      lRepositoryItem = pLineSummary.getRepositoryItem(); 
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_LINE_SUMMARY, lRepositoryItem);
  }
  
  /**  invoice - Shipped Items     **/
  @SuppressWarnings("unchecked")
  public List<ShippedItem> getShippedItems  () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SHIPPED_ITEMS);
    List <ShippedItem> lShippedItems = new Vector <ShippedItem> ();    
    for (Object lItem : lItems) {
      ShippedItem lShippedItem = new ShippedItem ((MutableRepositoryItem)lItem);
      lShippedItems.add(lShippedItem);
    }
    return lShippedItems;
  }
  public void  setShippedItems  (List<ShippedItem> pShippedItems) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    if (pShippedItems != null) {
      for (ShippedItem lShippedItem : pShippedItems) {
        lItems.add(lShippedItem.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_SHIPPED_ITEMS, lItems);
  }
  
  /**  invoice - Returned Items     **/
  @SuppressWarnings("unchecked")
  public List<ReturnedItem> getReturnedItems () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_RETURNED_ITEMS);
    List <ReturnedItem> lReturnedItems = new Vector <ReturnedItem> ();    
    for (Object lItem : lItems) {
      ReturnedItem lReturnedItem = new ReturnedItem ((MutableRepositoryItem)lItem);
      lReturnedItems.add(lReturnedItem);
    }
    return lReturnedItems;
  }
  public void  setReturnedItems  (List<ReturnedItem> pReturnedItems) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    if (pReturnedItems != null) {
      for (ReturnedItem lReturnedItem : pReturnedItems) {
        lItems.add(lReturnedItem.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_RETURNED_ITEMS, lItems);
  }
  
  /**  invoice - Appeased Items     **/
  @SuppressWarnings("unchecked")
  public List<Appeasement> getAppeasedItems () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_APPEASED_ITEMS);
    List <Appeasement> lAppeasedItems = new Vector <Appeasement> ();    
    for (Object lItem : lItems) {
      Appeasement lAppeasedItem = new Appeasement ((MutableRepositoryItem)lItem);
      lAppeasedItems.add(lAppeasedItem);
    }
    return lAppeasedItems;
  }
  public void  setAppeasedItems  (List<Appeasement> pAppeasedItems) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    if (pAppeasedItems != null) {
      for (Appeasement lAppeasedItem : pAppeasedItems) {
        lItems.add(lAppeasedItem.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_APPEASED_ITEMS, lItems);
  }
  
  /**  invoice - Cartons    **/
  @SuppressWarnings("unchecked")
  public List<Carton> getCartons () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_CARTONS);
    List <Carton> lCartons = new Vector <Carton> ();    
    for (Object lItem : lItems) {
      Carton lCarton = new Carton ((MutableRepositoryItem)lItem);
      lCartons.add(lCarton);
    }
    return lCartons;
  }
  public void  setCartons (List<Carton> pCartons) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    if (pCartons != null) {
      for (Carton lCarton : pCartons) {
        lItems.add(lCarton.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_CARTONS, lItems);
  }    
  

  /**  invoice - Payments    **/
  @SuppressWarnings("unchecked")
  public List<LinePayment> getPayments () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_PAYMENTS);
    List <LinePayment> lLinePayments = new Vector <LinePayment> ();    
    for (Object lItem : lItems) {
      LinePayment lLinePayment = new LinePayment ((MutableRepositoryItem)lItem);
      lLinePayments.add(lLinePayment);
    }
    return lLinePayments;
  }
  public void  setPayments (List<LinePayment> pLinePayments) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    if (pLinePayments != null) {
      for (LinePayment lLinePayment : pLinePayments) {
        lItems.add(lLinePayment.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_PAYMENTS, lItems);
  }      
  

  /**  invoice - Auxiliaries    **/
  @SuppressWarnings("unchecked")
  public List<AuxiliaryRecord> getAuxiliaries () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_AUXILLIARYS);
    List <AuxiliaryRecord> lLinePayments = new Vector <AuxiliaryRecord> ();    
    for (Object lItem : lItems) {
      AuxiliaryRecord lAuxiliaryRecord = new AuxiliaryRecord ((MutableRepositoryItem)lItem);
      lLinePayments.add(lAuxiliaryRecord);
    }
    return lLinePayments;
  }
  public void  setAuxiliaries (List<AuxiliaryRecord> pAuxiliaries) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    if (pAuxiliaries != null) {
      for (AuxiliaryRecord lAuxiliaryRecord : pAuxiliaries) {
        lItems.add(lAuxiliaryRecord.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_AUXILLIARYS, lItems);
  }      
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"                   + lLineFeed);
    lStringBuffer.append ("                 Invoice Record                    "                   + lLineFeed);
    lStringBuffer.append ("Invoice ID ...................... " + getId()                          + lLineFeed);
    lStringBuffer.append ("Order Number .................... " + getOrderNumber()                 + lLineFeed); 
    lStringBuffer.append ("Order Date ...................... " + getOrderDate()                   + lLineFeed); 
    lStringBuffer.append ("Source .......................... " + getSource()                      + lLineFeed);
    lStringBuffer.append ("Order Type ...................... " + getOrderType()                   + lLineFeed);
    lStringBuffer.append ("Business Type ................... " + getBusinessType()                + lLineFeed);
    lStringBuffer.append ("Loyalty Identifier .............. " + getLoyaltyIdentifier()           + lLineFeed);
    lStringBuffer.append ("Purchase Order .................. " + getCustomerPurchaseOrder()       + lLineFeed);
    lStringBuffer.append ("Reference ....................... " + getReference()                   + lLineFeed);
    lStringBuffer.append ("Tax Exemption Certificate ....... " + getTaxExemptionCertificate()     + lLineFeed);
    lStringBuffer.append ("Tax Exemption Name .............. " + getTaxExemptionName()            + lLineFeed);
    lStringBuffer.append ("Tax Exemption Type .............. " + getTaxExemptionType()            + lLineFeed);
    lStringBuffer.append ("Order Shipping .................. " + getOrderShipping()               + lLineFeed);
    lStringBuffer.append ("Shipping Local Tax .............. " + getOrderShippingLocalTax()       + lLineFeed);
    lStringBuffer.append ("Shipping County Tax ............. " + getOrderShippingCountyTax()      + lLineFeed);
    lStringBuffer.append ("Shipping State Tax .............. " + getOrderShippingStateTax()       + lLineFeed);
    lStringBuffer.append ("Shipping Total .................. " + getOrderShippingTotal()          + lLineFeed);
    lStringBuffer.append ("Shipping Tax .................... " + getOrderShippingTax()            + lLineFeed);
    lStringBuffer.append ("Shipping Extended Total ......... " + getOrderShippingExtendedTotal()  + lLineFeed);
    lStringBuffer.append ("Ship Via ........................ " + getShipVia()                     + lLineFeed);
    lStringBuffer.append ("Status .......................... " + getStatus()                      + lLineFeed);    
    lStringBuffer.append ("Last Extract Date ............... " + getLastExtractDate()             + lLineFeed);
    //lStringBuffer.append ("Extract ......................... " + getExtract()                     + lLineFeed);
    lStringBuffer.append ("Shipping Address ................ " + getShippingAddress().toString()  + lLineFeed);
    lStringBuffer.append ("Billing Address ................. " + getBillingAddress().toString()   + lLineFeed);
    lStringBuffer.append ("Line Summary .................... " + getLineSummary().toString()      + lLineFeed);    
    for (ShippedItem lShippedItem : getShippedItems()) {
      lStringBuffer.append (lShippedItem.toString()                                               + lLineFeed);      
    }
    for (ReturnedItem lReturnedItem : getReturnedItems()) {
      lStringBuffer.append (lReturnedItem.toString()                                              + lLineFeed);      
    }
    for (Appeasement lAppeasement : getAppeasedItems()) {
      lStringBuffer.append (lAppeasement.toString()                                               + lLineFeed);
    }
    for (Carton lCarton : getCartons()) {
      lStringBuffer.append (lCarton.toString()                                                    + lLineFeed);
    }
    for (LinePayment lLinePayment : getPayments()) {
      lStringBuffer.append (lLinePayment.toString()                                               + lLineFeed);
    }
    for (AuxiliaryRecord lAuxiliaryRecord : getAuxiliaries()) {
      lStringBuffer.append (lAuxiliaryRecord.toString()                                           + lLineFeed);
    }
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }  
  
}