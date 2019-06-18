package oms.commerce.salesaudit.util;

import atg.nucleus.GenericService;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import oms.commerce.salesaudit.exception.SalesAuditException;
import oms.commerce.salesaudit.record.Appeasement;
import oms.commerce.salesaudit.record.AuxiliaryRecord;
import oms.commerce.salesaudit.record.Extract;
import oms.commerce.salesaudit.record.Invoice;
import oms.commerce.salesaudit.record.InvoiceAddress;
import oms.commerce.salesaudit.record.LineCarton;
import oms.commerce.salesaudit.record.LineDiscount;
import oms.commerce.salesaudit.record.LineItem;
import oms.commerce.salesaudit.record.LinePayment;
import oms.commerce.salesaudit.record.LineSummary;
import oms.commerce.salesaudit.record.PaymentSummary;
import oms.commerce.salesaudit.record.ReturnedItem;
import oms.commerce.salesaudit.record.SalesAuditConstants;
import oms.commerce.salesaudit.record.ShippedItem;
import oms.commerce.salesaudit.schema.sale.AddressLineType;
import oms.commerce.salesaudit.schema.sale.AddressType;
import oms.commerce.salesaudit.schema.sale.ContactType;
import oms.commerce.salesaudit.schema.sale.CustomerType;
import oms.commerce.salesaudit.schema.sale.LineDiscountType;
import oms.commerce.salesaudit.schema.sale.OrderAppeasementType;
import oms.commerce.salesaudit.schema.sale.OrderPayment;
import oms.commerce.salesaudit.schema.sale.PersonType;
import oms.commerce.salesaudit.schema.sale.Sales;
import oms.commerce.salesaudit.schema.sale.SalesOrder;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Appeasements;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Discounts;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Shipped.Serials;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Shipped.Tracking;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Payments;
import oms.commerce.salesaudit.schema.sale.SalesOrder.ShippingAmount;
import oms.commerce.salesaudit.schema.sale.TaxAmount;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Auxiliaries;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Auxiliaries.Auxiliary;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Shipped;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Sku;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.RestockingFee;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Shipped.Tracking.Carton;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Summary;
import oms.commerce.salesaudit.schema.sale.SalesOrder.Cartons;
import oms.commerce.salesaudit.schema.sale.Sales.Summary.Payments.Payment;

public class SalesAuditMapper  
  extends GenericService {

  /**
   * Convert the input XML into a prinable string that can be send to 
   * the output file.
   * 
   * @param pSales    Input JAXB XML
   * @return          String representation of XML
   */
  public String convertXMLToString (Sales pSales) {
    StringWriter lStringWriter      = new StringWriter(); 
    BufferedWriter lBufferedWriter  = new BufferedWriter (lStringWriter);
    try {      
      JAXBContext lJAXBContext      = JAXBContext.newInstance(Sales.class);
      Marshaller lMarshaller        = lJAXBContext.createMarshaller();
      lMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);          
      lMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);        
      lMarshaller.marshal(pSales, lBufferedWriter);
      vlogDebug ("Output XML is " + lStringWriter);
  } catch (Exception e) {
      vlogError ("Unable to convert the input XML into a String " + e.getMessage());
      return null;
  }
  return lStringWriter.toString();  
 }
  
  /**
   * Create the XML sales record from the data in the repository.
   * 
   * @return     XML Sales Record
   */
  public Sales createXMLSalesAuditRecord () {
    // Create the Sales Record
    Sales lSales = new Sales ();
    
    // Create the header fields
    copyHeaderFields (lSales);
    
    // Output each of the invoice records
    //SalesOrder lSalesOrder = copySalesRecord (pInvoice);
    //lSales.getOrders().getOrder().add(lSalesOrder);   

    // Output the File Summary
    //copyFileSummary (lSales);
    
    return lSales;
  }
  
  /**
   * Copy the sales record from the repository to the JAXB schema.
   * 
   * @param pInvoice    Invoice (Repository)
   * @return            Sales Order (JAXB)
   */
  public SalesOrder copyOrderRecord (Invoice pInvoice) {
    // Create the Sales Order Object
    SalesOrder lSalesOrder = new SalesOrder();
    
    // Copy the common order fields
    vlogDebug ("+++++ Copy Common Invoice Fields");
    copyCommonOrderFields (lSalesOrder, pInvoice);
    
    // Copy the customer record
    vlogDebug ("+++++ Copy Customer Record");
    CustomerType lCustomerType = copyCustomerRecord (pInvoice);
    lSalesOrder.setCustomer(lCustomerType);
    
    // Copy the Shipping Amount
    vlogDebug ("+++++ Copy Shipping Amount");
    ShippingAmount lShippingAmount = new ShippingAmount();
    lShippingAmount = copyShippingAmount (pInvoice);
    lSalesOrder.setShippingAmount(lShippingAmount);
    
    // Copy Order Auxilliaries
    vlogDebug ("+++++ Copy order auxiliaries");
    Auxiliaries lAuxiliaries = new Auxiliaries();
    lSalesOrder.setAuxiliaries(lAuxiliaries);
    copyOrderAuxiliaries (pInvoice, lAuxiliaries);
    
    // Copy Order Appeasements
    vlogDebug ("+++++ Copy order appeasements");
    //Appeasements lAppeasements = new Appeasements();
    //lSalesOrder.setAppeasements(lAppeasements);
    //lSalesOrder.getAppeasements();
    copyOrderAppeasements (pInvoice, lSalesOrder);
    
    // Copy Order Payments
    vlogDebug ("+++++ Copy order payments");
    Payments lPayments = new Payments ();
    lSalesOrder.setPayments(lPayments);
    copyOrderPayments (pInvoice, lSalesOrder);
    
    // Copy Shipped Lines
    vlogDebug ("+++++ Copy shipped lines");
    Lines lLines      = new Lines();
    lSalesOrder.setLines(lLines);
    copyShippedLines (pInvoice, lSalesOrder);    
    
    // Copy returned Lines
    vlogDebug ("+++++ Copy returned lines");
    copyReturnedLines (pInvoice, lSalesOrder);
    
    // Copy Cartons
    vlogDebug ("+++++ Copy cartons lines");
    //Cartons lCartons = new Cartons ();
    //lSalesOrder.setCartons(lCartons);
    copyCartons (pInvoice, lSalesOrder);
    
    // Copy Line Summary
    vlogDebug ("+++++ Copy order summary");
    Summary lSummary = new Summary ();
    lSalesOrder.setSummary(lSummary);
    copyLineSummary (pInvoice, lSalesOrder);
    
    // Copy Line Summary
    vlogDebug ("+++++ (NEW) ... Add totals to the Summary");
    addToFileSummary (pInvoice);
    
    return lSalesOrder;
  }
  
  /**
   * Create the JAXB XML Sales Record 
   * 
   * @return    JAXB XML Sales Record
   */
  public Sales createSalesRecord () {
    // Create the Sales Record
    Sales lSales = new Sales ();
    
    // Initialize the order totals
    ExtractStatistics lExtractStatistics = new ExtractStatistics();
    this.setExtractStatistics(lExtractStatistics);
    
    return lSales;
  }
  
  /**
   * Copy the common header fields for the extract file.
   * 
   * @param pSales      JAXB Sales Object
   * @param pInvoice    Invoice Repository Item
   */
  public void copyHeaderFields (Sales pSales) {
    //Sales lSales = new Sales ();
    pSales.setSequence(new BigInteger("000001"));    
    pSales.setGenerated(formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    pSales.setReportDate(formatDate(new Date(), "yyyy-MM-dd"));
    pSales.setVersion(SalesAuditConstants.SCHEMA_VERSION);
  }
  
  /**
   * Copy the common order fields from the repository to the JAXB Object.
   * 
   * @param pSalesOrder     JAXB Order object
   * @param pInvoice        Invoice Repository Item
   */
  private void copyCommonOrderFields (SalesOrder pSalesOrder, Invoice pInvoice) {
    vlogDebug ("+++++ Invoice is " + pInvoice.toString());
    pSalesOrder.setNumber         (pInvoice.getOrderNumber());
    pSalesOrder.setDate           (formatDate(new Date(pInvoice.getOrderDate().getTime()), "yyyy-MM-dd HH:mm:ss"));
    pSalesOrder.setSource         (pInvoice.getSource());
    pSalesOrder.setType           (pInvoice.getOrderType());
    pSalesOrder.setBusinessType   (pInvoice.getBusinessType());
    pSalesOrder.setShipVia        (pInvoice.getShipVia());
  }
  
  /**
   * Create the customer record (billing/shipping address) for the given invoice.
   * 
   * @param pInvoice      Invoice Object
   * @return              Customer Type
   */
  private CustomerType copyCustomerRecord (Invoice pInvoice) {
    // Build the Customer record
    CustomerType lCustomerType = new CustomerType();
    lCustomerType.setReference                (pInvoice.getReference());
    lCustomerType.setTaxExemptionCertificate  (pInvoice.getTaxExemptionName());    
    lCustomerType.setTaxExemptionName         (pInvoice.getTaxExemptionName());
    lCustomerType.setTaxExemptionType         (pInvoice.getTaxExemptionType());
    if(isIncludeEmployeeElements())
    	lCustomerType.setFleetFarmId              (pInvoice.getFleetFarmId());

    // Create the Billing Address 
    PersonType lBillingPersonType = createBillingAddress (pInvoice);
    lCustomerType.setBillTo(lBillingPersonType);
    
    // Create the shipping Address 
    PersonType lShippingPersonType = createShippingAddress (pInvoice);
    lCustomerType.setShipTo(lShippingPersonType);
    
    return lCustomerType;
  }
  
  /**
   * Create the billing address for the given invoice.
   * 
   * @param pInvoice      Invoice object
   * @return              Person Type
   */
  public PersonType createBillingAddress (Invoice pInvoice) {
    // Create Person Type
    PersonType lPersonType = new PersonType ();
    
    // Create the Bill to Contact Type
    ContactType lContactType = new ContactType ();
    InvoiceAddress lBillingAddress = pInvoice.getBillingAddress();
    lContactType.setFirstName                 (lBillingAddress.getFirstName());
    lContactType.setMiddleName                (lBillingAddress.getMiddleName());
    lContactType.setLastName                  (lBillingAddress.getLastName());
    lContactType.setCompanyName               (lBillingAddress.getCompanyName());
    lContactType.setEmail                     (lBillingAddress.getEmail());
    lContactType.setHomePhone                 (lBillingAddress.getHomePhone());
    lContactType.setWorkPhone                 (lBillingAddress.getWorkPhone());
    lContactType.setFax                       (lBillingAddress.getFax());
    lContactType.setMobilePhone               (lBillingAddress.getMobilePhone());
    lContactType.setOrganization              (lBillingAddress.getOrganization());
    lPersonType.setContact(lContactType);
    
    // Create the Bill to Address Type 
    AddressType lAddressType = new AddressType ();
    
    // Set Address Lines
    copyAddressToList (lBillingAddress.getAddress1(), 1, lAddressType.getLine());
    copyAddressToList (lBillingAddress.getAddress2(), 2, lAddressType.getLine());
    copyAddressToList (lBillingAddress.getAddress3(), 3, lAddressType.getLine());
    copyAddressToList (lBillingAddress.getAddress4(), 4, lAddressType.getLine());
    
    // Set address other info
    lAddressType.setCompanyName               (lBillingAddress.getCompanyName());
    lAddressType.setCity                      (lBillingAddress.getcity());
    lAddressType.setProvince                  (lBillingAddress.getProvince());
    lAddressType.setProvinceCode              (lBillingAddress.getprovinceCode());
    lAddressType.setPostalCode                (lBillingAddress.getPostalCode());
    lAddressType.setCountryCode               (lBillingAddress.getCountryCode());    
    lPersonType.setAddress(lAddressType);
    
    return lPersonType;
  }
  
  /**
   * Create the shipping address for a given invoice. 
   * @param pInvoice        Invoice Object
   * @return                Person Type
   */
  public PersonType createShippingAddress (Invoice pInvoice) {
    // Create Person Type
    PersonType lPersonType = new PersonType ();
    
    // Create the Bill to Contact Type
    ContactType lContactType = new ContactType ();
    InvoiceAddress lShippingAddress = pInvoice.getShippingAddress();
    lContactType.setFirstName                 (lShippingAddress.getFirstName());
    lContactType.setMiddleName                (lShippingAddress.getMiddleName());
    lContactType.setLastName                  (lShippingAddress.getLastName());
    lContactType.setCompanyName               (lShippingAddress.getCompanyName());
    lContactType.setEmail                     (lShippingAddress.getEmail());
    lContactType.setHomePhone                 (lShippingAddress.getHomePhone());
    lContactType.setWorkPhone                 (lShippingAddress.getWorkPhone());
    lContactType.setFax                       (lShippingAddress.getFax());
    lContactType.setMobilePhone               (lShippingAddress.getMobilePhone());
    lContactType.setOrganization              (lShippingAddress.getOrganization());
    lPersonType.setContact(lContactType);
    
    // Create the Bill to Address Type 
    AddressType lAddressType = new AddressType ();
    
    // Set Address Lines
    copyAddressToList (lShippingAddress.getAddress1(), 1, lAddressType.getLine());
    copyAddressToList (lShippingAddress.getAddress2(), 2, lAddressType.getLine());
    copyAddressToList (lShippingAddress.getAddress3(), 3, lAddressType.getLine());
    copyAddressToList (lShippingAddress.getAddress4(), 4, lAddressType.getLine());
    
    // Set address other info
    lAddressType.setCompanyName               (lShippingAddress.getCompanyName());
    lAddressType.setCity                      (lShippingAddress.getcity());
    lAddressType.setProvince                  (lShippingAddress.getProvince());
    lAddressType.setProvinceCode              (lShippingAddress.getprovinceCode());
    lAddressType.setPostalCode                (lShippingAddress.getPostalCode());
    lAddressType.setCountryCode               (lShippingAddress.getCountryCode());    
    lPersonType.setAddress(lAddressType);
    
    return lPersonType;
  }
  
  /**
   * Copy the address lines from the repository to the JAXB generated 
   * classes.
   * 
   * @param pAddressLine    Address to be added to the list  
   * @param pSequence       Sequence number of 1-4
   * @param pAddressList    List of addresses 
   */
  private void copyAddressToList (String pAddressLine, int pSequence, List<AddressLineType> pAddressList) {
    AddressLineType lAddressLineType = new AddressLineType();
    String lSequenceString = (new Integer (pSequence)).toString();
    lAddressLineType.setSequence(new BigInteger (lSequenceString));
    lAddressLineType.setValue(pAddressLine);
    pAddressList.add(lAddressLineType);
  }
  
  /**
   * Copy the shipping amount from the repository to the JAXB XML schema.
   * 
   * @param pInvoice        Invoice object
   * @return                Shipping Amount
   */
  private ShippingAmount copyShippingAmount (Invoice pInvoice) {
    ShippingAmount lShippingAmount = new ShippingAmount();
    lShippingAmount.setTotal                  (formatBigDecimal (pInvoice.getOrderShipping()));
    
    // Create Tax Amount
    TaxAmount lTaxAmount = new TaxAmount();
    lTaxAmount.setLocal                       (formatBigDecimal (pInvoice.getOrderShippingLocalTax()));
    lTaxAmount.setCounty                      (formatBigDecimal (pInvoice.getOrderShippingCountyTax()));
    lTaxAmount.setState                       (formatBigDecimal (pInvoice.getOrderShippingStateTax()));
    lTaxAmount.setTotal                       (formatBigDecimal (pInvoice.getOrderShippingTotal()));
    lTaxAmount.setShippingTax                 (formatBigDecimal (pInvoice.getOrderShippingTax()));
    lTaxAmount.setExtendedTotal               (formatBigDecimal (pInvoice.getOrderShippingExtendedTotal()));
    lShippingAmount.setTax                    (lTaxAmount);
    return lShippingAmount;
  }
  
  /**
   * Copy the Order auxiliaries from the repository to the JAXB XML schema.
   * 
   * @param pInvoice        Invoice Object
   * @param pAuxiliarys     Order Auxiliaries
   */
  private void copyOrderAuxiliaries (Invoice pInvoice, Auxiliaries pAuxiliarys) {
    List<AuxiliaryRecord> lAuxiliaryRecords = pInvoice.getAuxiliaries();
    for (AuxiliaryRecord lAuxiliaryRecord : lAuxiliaryRecords) {
      Auxiliary lAuxiliary = new Auxiliary ();
      lAuxiliary.setName          (lAuxiliaryRecord.getAuxilliaryName());
      lAuxiliary.setValue         (lAuxiliaryRecord.getAuxilliaryValue());
      pAuxiliarys.getAuxiliary().add(lAuxiliary);
    }    
  }
  
  /**
   * Copy the order Appeasements from the repository to the JAXB schema.
   * 
   * @param pInvoice      Invoice Object
   * @param pSalesOrder   JAXB Sales Order
   */
  private void copyOrderAppeasements (Invoice pInvoice, SalesOrder pSalesOrder) {
    // Create the appeasements entry if there are appeasements to output
    if (pInvoice.getAppeasedItems().size() > 0) {
      Appeasements lAppeasements = new Appeasements();
      pSalesOrder.setAppeasements(lAppeasements);
    }
    List<Appeasement> lAppeasements = pInvoice.getAppeasedItems();
    for (Appeasement lAppeasement : lAppeasements) {
      OrderAppeasementType lOrderAppeasementType = new OrderAppeasementType();
      lOrderAppeasementType.setCode           (lAppeasement.getAppeaseCode());
      //lOrderAppeasementType.setDate         (lAppeasement.getAppeaseDate());
      lOrderAppeasementType.setDate           (null);
      lOrderAppeasementType.setDescription    (lAppeasement.getAppeaseDescription());
      lOrderAppeasementType.setReference      (lAppeasement.getReference());
      lOrderAppeasementType.setValue          (formatBigDecimal (lAppeasement.getAmount()));
      pSalesOrder.getAppeasements().getAppeasement().add (lOrderAppeasementType);
    }
  }
  
  /**
   * Copy the order payments from the repository to the JAXB Schema.
   * 
   * @param pInvoice      Invoice Object
   * @param pSalesOrder   JAXB Sales Order
   */
  private void copyOrderPayments (Invoice pInvoice, SalesOrder pSalesOrder) {
    List<LinePayment> lLinePayments = pInvoice.getPayments();
    for (LinePayment lLinePayment : lLinePayments) {
      // Copy payments
      OrderPayment lOrderPayment = new OrderPayment();
      lOrderPayment.setType(lLinePayment.getPaymentType());
      lOrderPayment.setAmount(formatBigDecimal (lLinePayment.getAmount()));
      lOrderPayment.setTransactionReference(lLinePayment.getTransactionReference());
      lOrderPayment.setDate(formatDate (new Date(lLinePayment.getPaymentDate().getTime()),"yyyy-MM-dd hh:mm:ss"));
      lOrderPayment.setCardNumber(lLinePayment.getCardNumber());
      lOrderPayment.setTokenId(lLinePayment.getTokenId());
      
      // Copy Auxiliaries
      oms.commerce.salesaudit.schema.sale.OrderPayment.Auxiliaries lAuxiliarys = new oms.commerce.salesaudit.schema.sale.OrderPayment.Auxiliaries(); 
      lOrderPayment.setAuxiliaries(lAuxiliarys);
      lAuxiliarys = lOrderPayment.getAuxiliaries();
      copyPaymentAuxiliaries (lLinePayment, lAuxiliarys);
      
      // Add to List
      pSalesOrder.getPayments().getPayment().add(lOrderPayment);
    }
  }
  
  /**
   * Copy the payment auxiliaries from the repository to the JAXB schema.
   * 
   * @param pPayment      Payment Object
   * @param pAuxiliarys   JAXB Payment Auxiliaries
   */
  private void copyPaymentAuxiliaries (LinePayment pLinePayment, oms.commerce.salesaudit.schema.sale.OrderPayment.Auxiliaries pAuxiliarys) {
    List<AuxiliaryRecord> lAuxiliaryRecords = pLinePayment.getAuxiliaries();
    for (AuxiliaryRecord lAuxiliaryRecord : lAuxiliaryRecords) {
      oms.commerce.salesaudit.schema.sale.OrderPayment.Auxiliaries.Auxiliary lAuxiliary = new oms.commerce.salesaudit.schema.sale.OrderPayment.Auxiliaries.Auxiliary ();
      lAuxiliary.setName          (lAuxiliaryRecord.getAuxilliaryName());
      lAuxiliary.setValue         (lAuxiliaryRecord.getAuxilliaryValue());
      pAuxiliarys.getAuxiliary().add(lAuxiliary);
    }    
  }  
  
  /**
   * Copy the shipped items from the repository to the JAXB schema.
   * 
   * @param pInvoice      Invoice Object (Repository)
   * @param lSalesOrder   Sales order (JAXB)
   */
  private void copyShippedLines (Invoice pInvoice, SalesOrder lSalesOrder) {
    List<ShippedItem> lShippedItems = pInvoice.getShippedItems();
    for (ShippedItem lShippedItem : lShippedItems) {
      // Copy Line
      Shipped lShipped = new Shipped();
      lShipped.setLineId                  (new BigInteger (lShippedItem.getExtractLineId().toString()));
      lShipped.setClientLineId            (lShippedItem.getClientLineId());
      
      // Add SKU Information
      Sku lSku = new Sku();
      lSku.setSkucode                     (lShippedItem.getSkucode());
      lSku.setBarcode                     (lShippedItem.getBarcode());
      lSku.setItemNumber                  (lShippedItem.getItemNumber());
      lSku.setColorCode                   (lShippedItem.getColorCode());
      lSku.setSizeCode                    (lShippedItem.getSizeCode());
      lShipped.setSku(lSku);
      
      // Price/Quantity
      lShipped.setQuantity                (new BigInteger(lShippedItem.getQuantity().toString()));
      lShipped.setUnitPrice               (formatBigDecimal (lShippedItem.getUnitPrice()));
      lShipped.setFacilityCd              (lShippedItem.getFacilityCd());
      
      //set gc number to the serial tag if available
      if(lShippedItem.getGiftCardNumber()!=null && !lShippedItem.getGiftCardNumber().isEmpty()){
        Serials serial = new Serials();
        serial.getSerialNumber().add(lShippedItem.getGiftCardNumber());
        lShipped.setSerials(serial);
      }
      
      // Line Discounts     
      copyShippedLineDiscounts            (lShippedItem, lShipped);
      
      // Shipping/Tax
      lShipped.setShipping                (formatBigDecimal (lShippedItem.getShippingAmount()));
      TaxAmount lTaxAmount = new TaxAmount();
      lTaxAmount.setLocal                 (formatBigDecimal (lShippedItem.getLineLocalTax()));
      lTaxAmount.setCounty                (formatBigDecimal (lShippedItem.getLineCountyTax()));
      lTaxAmount.setState                 (formatBigDecimal (lShippedItem.getLineStateTax()));
      lTaxAmount.setTotal                 (formatBigDecimal (lShippedItem.getLineTaxTotal()));
      lTaxAmount.setShippingTax           (formatBigDecimal (lShippedItem.getLineShippingTax()));
      lTaxAmount.setExtendedTotal         (formatBigDecimal (lShippedItem.getLineExtendedTotal()));
      lShipped.setTax                     (lTaxAmount);
      
      // Extended Price
      lShipped.setExtendedPrice           (formatBigDecimal (lShippedItem.getExtendedPrice()));
      
      // Get Auxiliaries
      oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries lAuxiliarys = new oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries();
      lShipped.setAuxiliaries(lAuxiliarys);
      //lAuxiliarys = lShipped.getAuxiliaries();
      copyLineAuxiliaries (lShippedItem, lAuxiliarys);
      
      // Tracking
      Tracking lTracking = new Tracking();
      lShipped.setTracking(lTracking);
      List<Carton> lCartons = lShipped.getTracking().getCarton();
      copyLineCartons (lShippedItem, lCartons);
      
      // Copy to Sales Order
      lSalesOrder.getLines().getShipped().add(lShipped);
     }
    
   }
   
  /**
   * Copy the returned items from the repository to the JAXB schema.
   * 
   * @param pInvoice      Invoice Object (Repository)
   * @param lSalesOrder   Sales order (JAXB)
   */
  private void copyReturnedLines (Invoice pInvoice, SalesOrder lSalesOrder) {
    List<ReturnedItem> lReturnedItems = pInvoice.getReturnedItems();
    for (ReturnedItem lReturnedItem : lReturnedItems) {
      // Copy Line
      Returned lReturned = new Returned();
      lReturned.setLineId                  (new BigInteger (lReturnedItem.getExtractLineId().toString()));
      lReturned.setClientLineId            (lReturnedItem.getClientLineId());
      
      // Add SKU Information
      Sku lSku = new Sku();
      lSku.setSkucode                     (lReturnedItem.getSkucode());
      lSku.setBarcode                     (lReturnedItem.getBarcode());
      lSku.setItemNumber                  (lReturnedItem.getItemNumber());
      lSku.setColorCode                   (lReturnedItem.getColorCode());
      lSku.setSizeCode                    (lReturnedItem.getSizeCode());
      lReturned.setSku(lSku);
      
      // Price/Quantity
      lReturned.setQuantity                (new BigInteger(lReturnedItem.getQuantity().toString()));
      lReturned.setUnitPrice               (formatBigDecimal (lReturnedItem.getUnitPrice()));
      lReturned.setFacilityCd              (lReturnedItem.getFacilityCd());
      
      // Line Discounts     
      copyReturnedLineDiscounts            (lReturnedItem, lReturned);
      
      // Shipping/Tax
      lReturned.setShipping                (formatBigDecimal (lReturnedItem.getShippingAmount()));
      TaxAmount lTaxAmount = new TaxAmount();
      lTaxAmount.setLocal                 (formatBigDecimal (lReturnedItem.getLineLocalTax()));
      lTaxAmount.setCounty                (formatBigDecimal (lReturnedItem.getLineCountyTax()));
      lTaxAmount.setState                 (formatBigDecimal (lReturnedItem.getLineStateTax()));
      lTaxAmount.setTotal                 (formatBigDecimal (lReturnedItem.getLineTaxTotal()));
      lTaxAmount.setShippingTax           (formatBigDecimal (lReturnedItem.getLineShippingTax()));
      lTaxAmount.setExtendedTotal         (formatBigDecimal (lReturnedItem.getLineExtendedTotal()));
      lReturned.setTax                    (lTaxAmount);
      
      // Extended Price
      lReturned.setExtendedPrice           (formatBigDecimal (lReturnedItem.getExtendedPrice()));
      
      // Get Auxiliaries
      oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries lAuxiliarys = new oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries();
      lReturned.setAuxiliaries(lAuxiliarys);
      //oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries lAuxiliarys; 
      //lAuxiliarys = lReturned.getAuxiliaries();
      copyLineAuxiliaries (lReturnedItem, lAuxiliarys);

      // Get return specific fields
      lReturned.setReason                   (lReturnedItem.getReturnReason());
      lReturned.setRma                      (lReturnedItem.getRmaNumber());
      RestockingFee lRestockingFee = new RestockingFee();
      lRestockingFee.setAmount              (formatBigDecimal (lReturnedItem.getReturnedAmount()));
      TaxAmount lRestockTaxAmount = new TaxAmount();
      lRestockTaxAmount.setLocal            (formatBigDecimal (lReturnedItem.getRestockLocalTax()));
      lRestockTaxAmount.setCounty           (formatBigDecimal (lReturnedItem.getRestockCountyTax()));
      lRestockTaxAmount.setState            (formatBigDecimal (lReturnedItem.getRestockStateTax()));
      lRestockTaxAmount.setTotal            (formatBigDecimal (lReturnedItem.getRestockTaxTotal()));
      lRestockTaxAmount.setShippingTax      (formatBigDecimal (lReturnedItem.getRestockShippingTax()));
      lRestockTaxAmount.setExtendedTotal    (formatBigDecimal (lReturnedItem.getRestockExtendedTotal()));
      lRestockingFee.setTax(lRestockTaxAmount);
      lReturned.setRestockingFee(lRestockingFee);
      
      // Copy to Sales Order
      lSalesOrder.getLines().getReturned().add(lReturned);
     }
   }
  
  
   /**
    * Copy the Shipped Line discounts from the repository to the JAXB schema.
    *   
    * @param pLineItem     Shipped item (Repository)
    * @param lShipped         Shipped Item (JAXB)
    */
   private void copyShippedLineDiscounts (LineItem pLineItem, Shipped pShipped) {
     List<LineDiscount> lLineDiscounts = pLineItem.getLineDiscounts();
     if (lLineDiscounts.size() > 0) {
       Discounts lDiscounts = new Discounts();
       pShipped.setDiscounts(lDiscounts);       
     }
     for (LineDiscount lLineDiscount : lLineDiscounts) {
       // Copy Line
       LineDiscountType lLineDiscountType = new LineDiscountType ();
       lLineDiscountType.setType          (lLineDiscount.getDiscountType());
       lLineDiscountType.setCode          (lLineDiscount.getDiscountCode());
       lLineDiscountType.setSource        (lLineDiscount.getSource());
       //As per the bug 2200 increasing the digits after decimal point to 4
       lLineDiscountType.setValue         (formatBigDecimal (lLineDiscount.getAmount(),4));
       pShipped.getDiscounts().getDiscount().add(lLineDiscountType);       
     }      
   }

   /**
    * Copy the Returned Line discounts from the repository to the JAXB schema.
    *   
    * @param pLineItem     Shipped item (Repository)
    * @param lShipped         Shipped Item (JAXB)
    */
   private void copyReturnedLineDiscounts (LineItem pLineItem, Returned pReturned) {
     List<LineDiscount> lLineDiscounts = pLineItem.getLineDiscounts();
     if (lLineDiscounts.size() > 0) { 
       Discounts lDiscounts = new Discounts();
       pReturned.setDiscounts(lDiscounts);
     }
     for (LineDiscount lLineDiscount : lLineDiscounts) {
       // Copy Line
       LineDiscountType lLineDiscountType = new LineDiscountType ();
       lLineDiscountType.setType          (lLineDiscount.getDiscountType());
       lLineDiscountType.setCode          (lLineDiscount.getDiscountCode());
       lLineDiscountType.setSource        (lLineDiscount.getSource());
       lLineDiscountType.setValue         (formatBigDecimal (lLineDiscount.getAmount()));
       pReturned.getDiscounts().getDiscount().add(lLineDiscountType);       
     }      
   }
   
   /**
    * Copy the Shipped Line auxiliaries from the repository to the JAXB schema.
    * 
    * @param pPayment      Payment Object
    * @param pAuxiliarys   JAXB Payment Auxiliaries
    */
   private void copyLineAuxiliaries (LineItem pLineItem, oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries pAuxiliarys) {
     List<AuxiliaryRecord> lAuxiliaryRecords = pLineItem.getAuxiliaries();
     for (AuxiliaryRecord lAuxiliaryRecord : lAuxiliaryRecords) {
       oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries.Auxiliary lAuxiliary = new oms.commerce.salesaudit.schema.sale.SalesOrder.Lines.Returned.Auxiliaries.Auxiliary ();
       lAuxiliary.setName               (lAuxiliaryRecord.getAuxilliaryName());
       lAuxiliary.setValue              (lAuxiliaryRecord.getAuxilliaryValue());
       pAuxiliarys.getAuxiliary().add(lAuxiliary);
     }    
   }  
   
   /**
    * Copy the cartons from the repository to the JAXB schema.
    * 
    * @param pLineItem      Line Item (Repository)
    * @param pCarton        Carton (JAXB)
    */
   private void copyLineCartons (LineItem pLineItem, List<Carton> pCarton) {
     List<LineCarton> lLineCartons = pLineItem.getLineCartons();
     for (LineCarton lLineCarton : lLineCartons) {
       // Copy Line
       Carton lCarton = new Carton ();
       lCarton.setTrackingNumber              (lLineCarton.getTrackingNumber());
       lCarton.setShipVia                     (lLineCarton.getShipVia());
       lCarton.setQuantity                    (new BigInteger(lLineCarton.getQuantity().toString()));
       lCarton.setDeliverConfirmationNumber   (lLineCarton.getDeliverConfirmationNumber());
       lCarton.setCartonNumber                (lLineCarton.getCartonNumber());

       // Add to cartons
       pCarton.add(lCarton);
     }
   }
   
   /**
    * Copy the cartons for the lines from the repository to the JAXB schema.
    *  
    * @param pInvoice         Invoice (Repository)
    * @param lSalesOrder      Sales order (JAXB)
    */
   private void copyCartons (Invoice pInvoice, SalesOrder lSalesOrder) {
     List <oms.commerce.salesaudit.record.Carton> lSACartons = pInvoice.getCartons();
     if (lSACartons != null && lSACartons.size() > 0) {
       Cartons lCartons = new Cartons ();
       lSalesOrder.setCartons(lCartons);
     }

     for (oms.commerce.salesaudit.record.Carton lSACarton : lSACartons) {
       // Copy Line
       oms.commerce.salesaudit.schema.sale.SalesOrder.Cartons.Carton lCarton = new oms.commerce.salesaudit.schema.sale.SalesOrder.Cartons.Carton ();
       lCarton.setTrackingNumber              (lSACarton.getTrackingNumber());
       lCarton.setCartonNumber                (lSACarton.getCartonNumber());
       lCarton.setShipDate                    (formatDate(lSACarton.getShipDate(), "yyyy-MM-dd"));
       lCarton.setShipVia                     (lSACarton.getShipVia());
       lCarton.setFacilityCd                  (lSACarton.getFacilityCd());
       lCarton.setWeight                      (new BigDecimal (lSACarton.getWeight()));
       lCarton.setCartonSize                  (lSACarton.getCartonSize());
       lCarton.setCartonType                  (lSACarton.getCartonType());
       lCarton.setBillOfLading                (lSACarton.getBillOfLading());
       lCarton.setProNum                      (lSACarton.getProNum());
       lCarton.setManifestNumber              (lSACarton.getManifestNumber());
       lCarton.setPickTicket                  (lSACarton.getPickTicket());
       lCarton.setReturnLabelNumber           (lSACarton.getReturnLabelNumber());
       lCarton.setDeliverConfirmationNumber   (lSACarton.getDeliverConfirmationNumber());
       lSalesOrder.getCartons().getCarton().add(lCarton);
     }
   }
   
   /**
    * Copy the lines summary from the repository to the JAXB schema.
    * 
    * @param pInvoice       Invoice (Repository)
    * @param pSalesOrder    Sales Order (JAXB)
    */
   private void copyLineSummary (Invoice pInvoice, SalesOrder pSalesOrder) {
     LineSummary lLineSummary = pInvoice.getLineSummary();
     Summary lSummary = new Summary();
     lSummary.setTransactionTotal             (formatBigDecimal (pInvoice.getLineSummary().getTransactionTotal()));
     lSummary.setTransactionsTaxableTotal     (formatBigDecimal (pInvoice.getLineSummary().getTransactionTaxableTotal()));
     lSummary.setTransactionTaxTotal          (formatBigDecimal (pInvoice.getLineSummary().getTransactionTaxTotal()));
     lSummary.setLineCount                    (new BigInteger (pInvoice.getLineSummary().getLineCount().toString()));
     lSummary.setPaymentTotal                 (formatBigDecimal (pInvoice.getLineSummary().getPaymentTotal()));
     lSummary.setPaymentCount                 (new BigInteger (pInvoice.getLineSummary().getPaymentCount().toString()));
     lSummary.setDiscountTotal                (formatBigDecimal (pInvoice.getLineSummary().getDiscountTotal()));
     lSummary.setDiscountCount                (new BigInteger (pInvoice.getLineSummary().getDiscountCount().toString()));
     lSummary.setGiftcardSoldTotal            (formatBigDecimal (pInvoice.getLineSummary().getGiftcardSoldTotal()));
     lSummary.setGiftcardSoldCount            (new BigInteger (pInvoice.getLineSummary().getGiftcardSoldCount().toString()));
     pSalesOrder.setSummary(lSummary);
   }
  
   /**
    * Create the file summary records for this extract.  This will consist of the
    * following steps:
    *    1.) Add to the summary counts every time a record is added to the Sales Audit file
    *    2.) Persist the summary counts to the Invoice repository
    *    3.) Copy the summary records to the JAXB schema and add to the output XML
   * @throws SalesAuditException 
    */
   public Extract generateFileSummary (Sales pSales, String pFileName) 
       throws SalesAuditException {
     // Create Invoice Summary record
     Extract lExtract = getInvoiceManager().addExtractStatus(getExtractStatistics(), pFileName, SalesAuditConstants.RUN_TYPE_STANDARD);
     
     // Add the summary record to the output XML
     copyExtractSummary (lExtract, pSales);    
     
     return lExtract;
   }
   
   public Extract generateFileSummaryForReRun (Sales pSales, String pFileName) 
       throws SalesAuditException {
     
     // Get the extract record for this file
     Extract lExtract = getInvoiceManager().getExtractRecordForFile (pFileName);
     
     // Add the summary record to the output XML
     if (lExtract == null) {
       String lErrorMessage = String.format("Unable to get extract record for file " + pFileName);
       vlogError (lErrorMessage);
       throw new SalesAuditException (lErrorMessage);
     }
     copyExtractSummary (lExtract, pSales);
     return lExtract;
   }
   
   
   
   /**
    * Add the Invoice record's totals to the file summary so we can add the file 
    * summary at the end of the XML.
    * @param pInvoice     Invoice (Repository)
    */
   private void addToFileSummary (Invoice pInvoice) {
     LineSummary lLineSummary = pInvoice.getLineSummary();
     getExtractStatistics().setTransactionTotal         (getExtractStatistics().getTransactionTotal ()        + lLineSummary.getTransactionTotal());    
     getExtractStatistics().setTransactionTaxableTotal  (getExtractStatistics().getTransactionTaxableTotal () + lLineSummary.getTransactionTaxableTotal());
     getExtractStatistics().setTransactionTaxTotal      (getExtractStatistics().getTransactionTaxTotal ()     + lLineSummary.getTransactionTaxTotal());   
     getExtractStatistics().setTransactionCount         (getExtractStatistics().getTransactionCount()         + 1);
     getExtractStatistics().setLineCount                (getExtractStatistics().getLineCount()                + lLineSummary.getLineCount());     
     getExtractStatistics().setPaymentTotal             (getExtractStatistics().getPaymentTotal ()            + lLineSummary.getPaymentTotal());     
     getExtractStatistics().setPaymentCount             (getExtractStatistics().getPaymentCount ()            + lLineSummary.getPaymentCount());     
     getExtractStatistics().setDiscountTotal            (getExtractStatistics().getDiscountTotal ()           + lLineSummary.getDiscountTotal());
     getExtractStatistics().setDiscountCount            (getExtractStatistics().getDiscountCount ()           + lLineSummary.getDiscountCount());     
     getExtractStatistics().setGiftcardSoldTotal        (lLineSummary.getGiftcardSoldTotal()                  + lLineSummary.getGiftcardSoldTotal());     
     getExtractStatistics().setGiftcardSoldCount        (getExtractStatistics().getGiftcardSoldCount ()       + lLineSummary.getGiftcardSoldCount());
     addPaymentsToSummary (pInvoice);  
   }
   
   /**
    * Add the payments from the passed invoice to the summary so they can be represented 
    * when we create the extract totals.
    * 
    * @param pInvoice           Invoice (Repository)
    */
   private void addPaymentsToSummary (Invoice pInvoice) {
     List<LinePayment> lLinePayments = pInvoice.getPayments();
     for (LinePayment lLinePayment : lLinePayments) {
       String lPaymentType = lLinePayment.getPaymentType();
       ExtractPaymentSummary lExtractPaymentSummary = getPaymentByType (lPaymentType);
       if (lLinePayment.getAmount() < 0.0d) {
         lExtractPaymentSummary.setCreditCount            (lExtractPaymentSummary.getCreditCount() + 1);
         lExtractPaymentSummary.setCreditTotal            (lExtractPaymentSummary.getCreditTotal() + getPositiveValue(lLinePayment.getAmount()));
       }
       else {
         lExtractPaymentSummary.setDebitCount            (lExtractPaymentSummary.getDebitCount() + 1);
         lExtractPaymentSummary.setDebitTotal            (lExtractPaymentSummary.getDebitTotal() + lLinePayment.getAmount());
       }
     }
   }
   
   private double getPositiveValue (double pAmount) {
     if (pAmount > 0.0d) {
       return pAmount;
     } else {
       return -pAmount;
     }
   }

   /**
    * Find the payment for the given card type in the payment summary.
    * 
    * @param pPaymentType       Payment Type
    * @return                   Extract Payment Summary Record
    */
   private ExtractPaymentSummary getPaymentByType (String pPaymentType) {
     List<ExtractPaymentSummary> lExtractPaymentSummarys = getExtractStatistics().getExtractPaymentSummary();
     for (ExtractPaymentSummary lExtractPaymentSummary : lExtractPaymentSummarys) {
       if (lExtractPaymentSummary.getPaymentType().equals(pPaymentType)) {
         return lExtractPaymentSummary;
       }
     }
     // No payment found ... create new one
     ExtractPaymentSummary lExtractPaymentSummary = new ExtractPaymentSummary ();
     lExtractPaymentSummary.setPaymentType(pPaymentType);
     getExtractStatistics().getExtractPaymentSummary().add(lExtractPaymentSummary);
     return lExtractPaymentSummary;
   }
   
   /*
   /**
    * Persist the file summary into the Inventory repository.  
    * 
    * @param pInvoice           Invoice (Repository)
    */
   /*
   private Extract createExtractSummaryRepositoryItems () {
     // Create the Inventory repository items
     Extract lExtract = new Extract();
     //lExtract.setExtractDate            (new Date());
     lExtract.setExtractFileName        ("?????");
     lExtract.setRunType                ("?????");
     
     // Create Summary
     ExtractSummary lExtractSummary = new ExtractSummary ();
     lExtractSummary.setTransactionTotal         (getExtractStatistics().getTransactionTotal        ());    
     lExtractSummary.setTransactionTaxableTotal  (getExtractStatistics().getTransactionTaxableTotal ());
     lExtractSummary.setTransactionTaxTotal      (getExtractStatistics().getTransactionTaxTotal     ());     
     lExtractSummary.setLineCount                (getExtractStatistics().getLineCount               ());     
     lExtractSummary.setPaymentTotal             (getExtractStatistics().getPaymentTotal            ());     
     lExtractSummary.setPaymentCount             (getExtractStatistics().getPaymentCount            ());     
     lExtractSummary.setDiscountTotal            (getExtractStatistics().getDiscountTotal           ());
     lExtractSummary.setDiscountCount            (getExtractStatistics().getDiscountCount           ());     
     lExtractSummary.setGiftcardSoldTotal        (getExtractStatistics().getGiftcardSoldTotal       ());     
     lExtractSummary.setGiftcardSoldCount        (getExtractStatistics().getGiftcardSoldCount       ());
     lExtract.setExtractSummary(lExtractSummary);

     // Create Payments
     List<ExtractPaymentSummary> lExtractPaymentSummarys = getExtractStatistics().getExtractPaymentSummary();
     for (ExtractPaymentSummary lExtractPaymentSummary : lExtractPaymentSummarys) {
       PaymentSummary lPaymentSummary = new PaymentSummary ();
       lPaymentSummary.setPaymentType            (lExtractPaymentSummary.getPaymentType());
       lPaymentSummary.setCreditTotal            (lExtractPaymentSummary.getCreditTotal());
       lPaymentSummary.setCreditCount            (lExtractPaymentSummary.getCreditCount());
       lPaymentSummary.setDebitTotal             (lExtractPaymentSummary.getDebitTotal());
       lPaymentSummary.setDebitCount             (lExtractPaymentSummary.getDebitCount());
       lExtract.getPaymentSummary().add (lPaymentSummary);
     }
     
     return lExtract;
   }
   */
   
   /**
    * Copy the extract status to the JAXB classes
    * 
    * @param pExtract       Extract record (Respository)
    * @param pSales         Sales (JAXB)
    */
   private void copyExtractSummary (Extract pExtract, Sales pSales) {
     oms.commerce.salesaudit.schema.sale.Sales.Summary lSummary = new oms.commerce.salesaudit.schema.sale.Sales.Summary ();
     pSales.setSummary(lSummary);
     pSales.getSummary().setTransactionsTotal         (formatBigDecimal(pExtract.getExtractSummary().getTransactionTotal()));    
     pSales.getSummary().setTransactionsTaxableTotal  (formatBigDecimal(pExtract.getExtractSummary().getTransactionTaxableTotal ()));
     pSales.getSummary().setTransactionsTaxTotal      (formatBigDecimal(pExtract.getExtractSummary().getTransactionTaxTotal     ()));
     pSales.getSummary().setTransactionLinesCount     (new BigInteger(new Long(pExtract.getExtractSummary().getLineCount()).toString()));
     pSales.getSummary().setTransactionsCount         (new BigInteger(new Long(pExtract.getExtractSummary().getTransactionCount()).toString()));
     pSales.getSummary().setPaymentsTotal             (formatBigDecimal (pExtract.getExtractSummary().getPaymentTotal            ()));     
     pSales.getSummary().setPaymentsCount             (new BigInteger(new Long(pExtract.getExtractSummary().getPaymentCount()).toString()));     
     pSales.getSummary().setDiscountsTotal            (formatBigDecimal(pExtract.getExtractSummary().getDiscountTotal           ()));
     pSales.getSummary().setDiscountsCount            (new BigInteger(new Long(pExtract.getExtractSummary().getDiscountCount()).toString()));     
     pSales.getSummary().setGiftcardSoldTotal         (formatBigDecimal (pExtract.getExtractSummary().getGiftcardSoldTotal       ()));     
     pSales.getSummary().setGiftcardSoldCount         (new BigInteger(new Long(pExtract.getExtractSummary().getGiftcardSoldCount()).toString()));     
     copyPaymentSummary (pExtract, pSales);
   }
   
   private void copyPaymentSummary (Extract pExtract, Sales pSales) {
     oms.commerce.salesaudit.schema.sale.Sales.Summary.Payments lPayments = new oms.commerce.salesaudit.schema.sale.Sales.Summary.Payments();
     pSales.getSummary().setPayments(lPayments);
     List<PaymentSummary> lPaymentSummarys = pExtract.getPaymentSummary();
     for (PaymentSummary lPaymentSummary : lPaymentSummarys) {
       Payment lPayment = new Payment();
       lPayment.setType                               (lPaymentSummary.getPaymentType());
       lPayment.setCreditTotal                        (formatBigDecimal (lPaymentSummary.getCreditTotal()));
       lPayment.setCreditCount                        (new BigInteger(lPaymentSummary.getCreditCount().toString()));
       lPayment.setDebitTotal                         (formatBigDecimal (lPaymentSummary.getDebitTotal()));
       lPayment.setDebitCount                         (new BigInteger(lPaymentSummary.getDebitCount().toString()));
       pSales.getSummary().getPayments().getPayment().add(lPayment);
     }
   }
   
   private double roundDouble (double pDouble, int pPrecision) {
     return (new BigDecimal (pDouble).setScale(pPrecision, BigDecimal.ROUND_HALF_UP).doubleValue());
   }
   
   private BigDecimal formatBigDecimal (Double pDoubleValue) {
     return (new BigDecimal (new Double(roundDouble(pDoubleValue.doubleValue(), 2)).toString()));
   }
   
   private BigDecimal formatBigDecimal (Double pDoubleValue, int pPrecision) {
     return (new BigDecimal (new Double(roundDouble(pDoubleValue.doubleValue(), pPrecision)).toString()));
   }
   
   private String formatDate (Date pDate, String pFormat) {
     SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(pFormat);
     return lSimpleDateFormat.format(pDate);
   }
   
   // *********************************************************
   //            Getter/setters
   // *********************************************************
   ExtractStatistics mExtractStatistics;
   public ExtractStatistics getExtractStatistics() {
    return mExtractStatistics;
  }
  public void setExtractStatistics(ExtractStatistics pExtractStatistics) {
    this.mExtractStatistics = pExtractStatistics;
  }
  
  InvoiceManager mInvoiceManager;
  public InvoiceManager getInvoiceManager() {
    return mInvoiceManager;
  }
  public void setInvoiceManager(InvoiceManager pInvoiceManager) {
    this.mInvoiceManager = pInvoiceManager;
  }
  
  boolean includeEmployeeElements;
public boolean isIncludeEmployeeElements() {
	return includeEmployeeElements;
}

public void setIncludeEmployeeElements(boolean pIncludeEmployeeElements) {
	includeEmployeeElements = pIncludeEmployeeElements;
}
  
  
  //String mFileName;
  //public String getFileName() {
  //  return mFileName;
  //}
  //public void setFileName(String pFileName) {
  //  this.mFileName = pFileName;
  //}  
   
}

