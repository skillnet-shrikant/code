package oms.commerce.salesaudit.record;

/**
 * This class is a set of the contstants used through out the Sales Audit 
 * processing.
 * 
 * @author jvose
 *
 */
public class SalesAuditConstants {
  
  // Other Constants
  public static final String ORDER_SOURCE           = "MFF";
  public static final String ORDER_TYPE             = "Web";
  public static final String BUSINESS_TYPE          = "WEB";
  public static final String EXTRACT_TO_SALES_AUDIT = "created";
  
  // Item Types
  public static final String ITEM_INVOICE           = "invoice";
  public static final String ITEM_INVOICE_ADDRESS   = "invoiceAddress";
  public static final String ITEM_APPEASEMENT       = "appeasement";
  public static final String ITEM_ANCILLIARY        = "auxilliary";
  public static final String ITEM_CARTON            = "carton";
  public static final String ITEM_LINE_CARTON       = "lineCarton";
  public static final String ITEM_LINE_DISCOUNT     = "lineDiscount";
  public static final String ITEM_PAYMENT           = "payment";
  public static final String ITEM_LINE_ITEM         = "lineItem";
  public static final String ITEM_SHIPPED_ITEM      = "shippedItem";
  public static final String ITEM_RETURNED_ITEM     = "returnedItem";
  public static final String ITEM_LINE_SUMMARY      = "lineSummary";
  public static final String ITEM_EXTRACT           = "extract";
  public static final String ITEM_EXTRACT_SUMMARY   = "extractSummary";
  public static final String ITEM_PAYMENT_SUMMARY   = "paymentSummary";  
  
  // Invoice Properties
  public static final String PROPERTY_INVOICE_INVOICE_ID                    = "id";
  public static final String PROPERTY_INVOICE_ORDER_NUMBER                  = "orderNumber";
  public static final String PROPERTY_INVOICE_FLEET_FARM_ID                 = "fleetFarmId";
  public static final String PROPERTY_INVOICE_ORDER_DATE                    = "orderDate";
  public static final String PROPERTY_INVOICE_SOURCE                        = "source";
  public static final String PROPERTY_INVOICE_ORDER_TYPE                    = "orderType";
  public static final String PROPERTY_INVOICE_BUSINESS_TYPE                 = "businessType";
  public static final String PROPERTY_INVOICE_LOYALTY_IDENTIFIER            = "loyaltyIdentifier";
  public static final String PROPERTY_INVOICE_CUSTOMER_PURCHASE_ORDER       = "customerPurchaseOrder";
  public static final String PROPERTY_INVOICE_REFERENCE                     = "reference";
  public static final String PROPERTY_INVOICE_TAX_EXEMPTION_CERTIFICATE     = "taxExemptionCertificate";
  public static final String PROPERTY_INVOICE_TAX_EXEMPTION_NAME            = "taxExemptionName";
  public static final String PROPERTY_INVOICE_TAX_EXEMPTION_TYPE            = "taxExemptionType";
  public static final String PROPERTY_INVOICE_ORDER_SHIPPING                = "orderShipping";
  public static final String PROPERTY_INVOICE_ORDER_SHIPPING_LOCAL_TAX      = "orderShippingLocalTax";
  public static final String PROPERTY_INVOICE_ORDER_SHIPPING_COUNTY_TAX     = "orderShippingCountyTax";
  public static final String PROPERTY_INVOICE_ORDER_SHIPPING_STATE_TAX      = "orderShippingStateTax";
  public static final String PROPERTY_INVOICE_ORDER_SHIPPING_TOTAL          = "orderShippingTotal";
  public static final String PROPERTY_INVOICE_ORDER_SHIPPING_TAX            = "orderShippingTax";
  public static final String PROPERTY_INVOICE_ORDER_SHIPPING_EXTENDED_TOTAL = "orderShippingExtendedTotal";   
  public static final String PROPERTY_INVOICE_SHIP_VIA                      = "shipVia"; 
  public static final String PROPERTY_INVOICE_STATUS                        = "status";
  public static final String PROPERTY_INVOICE_LAST_EXTRACT_DATE             = "lastExtractDate";
  public static final String PROPERTY_INVOICE_EXTRACT                       = "extract";
  public static final String PROPERTY_INVOICE_SHIPPING_ADDRESS              = "shippingAddress";
  public static final String PROPERTY_INVOICE_BILLING_ADDRESS               = "billingAddress"; 
  public static final String PROPERTY_INVOICE_LINE_SUMMARY                  = "lineSummary";
  public static final String PROPERTY_INVOICE_SHIPPED_ITEMS                 = "shippedItems";
  public static final String PROPERTY_INVOICE_RETURNED_ITEMS                = "returnedItems";
  public static final String PROPERTY_INVOICE_APPEASED_ITEMS                = "appeasedItems";
  public static final String PROPERTY_INVOICE_CARTONS                       = "cartons";
  public static final String PROPERTY_INVOICE_PAYMENTS                      = "payments";
  public static final String PROPERTY_INVOICE_AUXILLIARYS                   = "auxilliarys"; 
  
  // Invoice Address Properties
  public static final String PROPERTY_INVOICE_ADDRESS_ADDRESS_ID            = "id";
  public static final String PROPERTY_INVOICE_ADDRESS_ADDRESS_TYPE          = "type";
  public static final String PROPERTY_INVOICE_ADDRESS_FIRST_NAME            = "firstName";
  public static final String PROPERTY_INVOICE_ADDRESS_MIDDLE_NAME           = "middleName";
  public static final String PROPERTY_INVOICE_ADDRESS_LAST_NAME             = "lastName";
  public static final String PROPERTY_INVOICE_ADDRESS_ORGANIZATION          = "organization";
  public static final String PROPERTY_INVOICE_ADDRESS_COMPANY_NAME          = "companyName";
  public static final String PROPERTY_INVOICE_ADDRESS_HOME_PHONE            = "homePhone";
  public static final String PROPERTY_INVOICE_ADDRESS_WORK_PHONE            = "workPhone";
  public static final String PROPERTY_INVOICE_ADDRESS_MOBILE_PHONE          = "mobilePhone";
  public static final String PROPERTY_INVOICE_ADDRESS_FAX                   = "fax";
  public static final String PROPERTY_INVOICE_ADDRESS_EMAIL                 = "email";
  public static final String PROPERTY_INVOICE_ADDRESS_ADDRESS_1             = "address1";
  public static final String PROPERTY_INVOICE_ADDRESS_ADDRESS_2             = "address2";
  public static final String PROPERTY_INVOICE_ADDRESS_ADDRESS_3             = "address3";
  public static final String PROPERTY_INVOICE_ADDRESS_ADDRESS_4             = "address4";
  public static final String PROPERTY_INVOICE_ADDRESS_CITY                  = "city";
  public static final String PROPERTY_INVOICE_ADDRESS_PROVINCE_CODE         = "provinceCode";
  public static final String PROPERTY_INVOICE_ADDRESS_PROVINCE              = "province";
  public static final String PROPERTY_INVOICE_ADDRESS_POSTAL_CODE           = "postalCode";
  public static final String PROPERTY_INVOICE_ADDRESS_COUNTRY_CODE          = "countryCode";

  // Appeasement Properties
  public static final String PROPERTY_APPEASEMENT_APPEASEMENT_ID            = "id";
  public static final String PROPERTY_APPEASEMENT_APPEASE_CODE              = "appeaseCode";
  public static final String PROPERTY_APPEASEMENT_APPEASE_DESCRIPTION       = "appeaseDescription";
  public static final String PROPERTY_APPEASEMENT_REFERENCE                 = "reference";
  public static final String PROPERTY_APPEASEMENT_APPEASE_DATE              = "appeaseDate";
  public static final String PROPERTY_APPEASEMENT_AMOUNT                    = "amount";
  
  // Ancilliary Properties
  public static final String PROPERTY_ANCILLIARY_AUXILLIARY_ID              = "id";
  public static final String PROPERTY_ANCILLIARY_AUXILLIARY_TYPE            = "type";
  public static final String PROPERTY_ANCILLIARY_AUXILLIARY_NAME            = "auxilliaryName";
  public static final String PROPERTY_ANCILLIARY_AUXILLIARY_VALUE           = "auxilliaryValue";
  
  // Carton Properties
  public static final String PROPERTY_CARTON_CARTON_ID                      = "id";
  public static final String PROPERTY_CARTON_TRACKING_NUMBER                = "trackingNumber";
  public static final String PROPERTY_CARTON_CARTON_NUMBER                  = "cartonNumber";
  public static final String PROPERTY_CARTON_SHIP_DATE                      = "shipDate";
  public static final String PROPERTY_CARTON_SHIP_VIA                       = "shipVia";
  public static final String PROPERTY_CARTON_FACILITY_CD                    = "facilityCd";
  public static final String PROPERTY_CARTON_WEIGHT                         = "weight";
  public static final String PROPERTY_CARTON_CARTON_SIZE                    = "cartonSize";
  public static final String PROPERTY_CARTON_CARTON_TYPE                    = "cartonType";
  public static final String PROPERTY_CARTON_BILL_OF_LADING                 = "billOfLading";
  public static final String PROPERTY_CARTON_PRO_NUM                        = "proNum";
  public static final String PROPERTY_CARTON_MANIFEST_NUMBER                = "manifestNumber";
  public static final String PROPERTY_CARTON_PICK_TICKET                    = "pickTicket";
  public static final String PROPERTY_CARTON_RETURN_LABEL_NUMBER            = "returnLabelNumber";
  public static final String PROPERTY_CARTON_DELIVER_CONFIRMATION_NUMBER    = "deliverConfirmationNumber";
  
  // Line Carton Properties
  public static final String PROPERTY_LINE_CARTON_LINE_CARTON_ID            = "id";
  public static final String PROPERTY_LINE_CARTON_TRACKING_NUMBER           = "trackingNumber";
  public static final String PROPERTY_LINE_CARTON_SHIP_VIA                  = "shipVia";
  public static final String PROPERTY_LINE_CARTON_QUANTITY                  = "quantity";
  public static final String PROPERTY_LINE_CARTON_DELIVER_CONFIRMATION_NUMBER = "deliverConfirmationNumber";
  public static final String PROPERTY_LINE_CARTON_SERIAL_NUMBER             = "serialNumber";
  public static final String PROPERTY_LINE_CARTON_CARTON_NUMBER             = "cartonNumber";
  
  // Line Discount
  public static final String PROPERTY_LINE_DISCOUNT_LINE_DISCOUNT_ID        = "id";
  public static final String PROPERTY_LINE_DISCOUNT_DISCOUNT_TYPE           = "discountType";
  public static final String PROPERTY_LINE_DISCOUNT_DISCOUNT_CODE           = "discountCode";
  public static final String PROPERTY_LINE_DISCOUNT_SOURCE                  = "source";
  public static final String PROPERTY_LINE_DISCOUNT_AMOUNT                  = "amount";
  
  // Payment
  public static final String PROPERTY_PAYMENT_PAYMENT_ID                    = "id";
  public static final String PROPERTY_PAYMENT_PAYMENT_TYPE                  = "paymentType";
  public static final String PROPERTY_PAYMENT_AMOUNT                        = "amount";
  public static final String PROPERTY_PAYMENT_TRANSACTION_REFERENCE         = "transactionReference";
  public static final String PROPERTY_PAYMENT_PAYMENT_DATE                  = "paymentDate";
  public static final String PROPERTY_PAYMENT_CARD_REFERENCE                = "cardReference";
  public static final String PROPERTY_PAYMENT_CARD_NUMBER                   = "cardNumber";
  public static final String PROPERTY_PAYMENT_TOKEN_ID                      = "tokenId";  
  public static final String PROPERTY_PAYMENT_AUXILLIARYS                   = "auxilliarys";
  
  // Line Item
  public static final String PROPERTY_LINE_ITEM_LINE_ID                     = "id";
  public static final String PROPERTY_LINE_ITEM_EXTRACT_LINE_ID             = "extractLineId";
  public static final String PROPERTY_LINE_ITEM_CLIENT_LINE_ID              = "clientLineId";
  public static final String PROPERTY_LINE_ITEM_SKUCODE                     = "skucode";
  public static final String PROPERTY_LINE_ITEM_BARCODE                     = "barcode";
  public static final String PROPERTY_LINE_ITEM_ITEM_NUMBER                 = "itemNumber";
  public static final String PROPERTY_LINE_ITEM_COLOR_CODE                  = "colorCode";
  public static final String PROPERTY_LINE_ITEM_SIZE_CODE                   = "sizeCode";
  public static final String PROPERTY_LINE_ITEM_QUANTITY                    = "quantity";
  public static final String PROPERTY_LINE_ITEM_UNIT_PRICE                  = "unitPrice";
  public static final String PROPERTY_LINE_ITEM_FACILITY_CD                 = "facilityCd";
  public static final String PROPERTY_LINE_ITEM_SHIPPING_AMOUNT             = "shippingAmount";
  public static final String PROPERTY_LINE_ITEM_LINE_LOCAL_TAX              = "lineLocalTax";
  public static final String PROPERTY_LINE_ITEM_LINE_COUNTY_TAX             = "lineCountyTax";
  public static final String PROPERTY_LINE_ITEM_LINE_STATE_TAX              = "lineStateTax";
  public static final String PROPERTY_LINE_ITEM_LINE_TAX_TOTAL              = "lineTaxTotal";
  public static final String PROPERTY_LINE_ITEM_LINE_SHIPPING_TAX           = "lineShippingTax";
  public static final String PROPERTY_LINE_ITEM_LINE_EXTENDED_TOTAL         = "lineExtendedTotal";
  public static final String PROPERTY_LINE_ITEM_EXTENDED_PRICE              = "extendedPrice";
  public static final String PROPERTY_LINE_ITEM_LINE_NUMBER                 = "lineNumber";
  public static final String PROPERTY_LINE_ITEM_RETURN_REASON               = "returnReason";
  public static final String PROPERTY_LINE_ITEM_RMA_NUMBER                  = "rmaNumber";
  public static final String PROPERTY_LINE_ITEM_RETURNED_AMOUNT             = "returnedAmount";
  public static final String PROPERTY_LINE_ITEM_RESTOCK_LOCAL_TAX           = "restockLocalTax";
  public static final String PROPERTY_LINE_ITEM_RESTOCK_COUNTY_TAX          = "restockCountyTax";
  public static final String PROPERTY_LINE_ITEM_RESTOCK_STATE_TAX           = "restockStateTax";
  public static final String PROPERTY_LINE_ITEM_RESTOCK_TAX_TOTAL           = "restockTaxTotal";
  public static final String PROPERTY_LINE_ITEM_RESTOCK_SHIPPING_TAX        = "restockShippingTax";
  public static final String PROPERTY_LINE_ITEM_RESTOCK_EXTENDED_TOTAL      = "restockExtendedTotal";
  public static final String PROPERTY_LINE_ITEM_LINE_DISCOUNTS              = "lineDiscounts";
  public static final String PROPERTY_LINE_ITEM_LINE_CARTONS                = "lineCartons";
  public static final String PROPERTY_LINE_ITEM_AUXILLIARYS                 = "auxilliarys";
  
  // Shipped Line
  public static final String PROPERTY_SHIPPED_ITEM_LINE_ID                  = "id";
  public static final String PROPERTY_SHIPPED_ITEM_LINE_NUMBER              = "lineNumber";
  public static final String PROPERTY_SHIPPED_ITEM_GIFTCARD_NUMBER          = "giftCardNumber";
  
  // Returned Line
  public static final String PROPERTY_RETURNED_ITEM_LINE_ID                 = "id";
  public static final String PROPERTY_RETURNED_ITEM_LINE_NUMBER             = "lineNumber";
  public static final String PROPERTY_RETURNED_ITEM_RETURN_REASON           = "returnReason";
  public static final String PROPERTY_RETURNED_ITEM_RMA_NUMBER              = "rmaNumber";
  public static final String PROPERTY_RETURNED_ITEM_RETURNED_AMOUNT         = "returnedAmount";
  public static final String PROPERTY_RETURNED_ITEM_RESTOCK_LOCAL_TAX       = "restockLocalTax";
  public static final String PROPERTY_RETURNED_ITEM_RESTOCK_COUNTY_TAX      = "restockCountyTax";
  public static final String PROPERTY_RETURNED_ITEM_RESTOCK_STATE_TAX       = "restockStateTax";
  public static final String PROPERTY_RETURNED_ITEM_RESTOCK_TAX_TOTAL       = "restockTaxTotal";
  public static final String PROPERTY_RETURNED_ITEM_RESTOCK_SHIPPING_TAX    = "restockShippingTax";
  public static final String PROPERTY_RETURNED_ITEM_RESTOCK_EXTENDED_TOTAL  = "restockExtendedTotal";
  
  // Line Summary
  public static final String PROPERTY_LINE_SUMMARY_LINE_SUMMARY_ID          = "id";
  public static final String PROPERTY_LINE_SUMMARY_TRANSACTION_TOTAL        = "transactionTotal";
  public static final String PROPERTY_LINE_SUMMARY_TRANSACTION_TAXABLE_TOTAL = "transactionTaxableTotal";
  public static final String PROPERTY_LINE_SUMMARY_TRANSACTION_TAX_TOTAL    = "transactionTaxTotal";
  public static final String PROPERTY_LINE_SUMMARY_LINE_COUNT               = "lineCount";
  public static final String PROPERTY_LINE_SUMMARY_PAYMENT_TOTAL            = "paymentTotal";
  public static final String PROPERTY_LINE_SUMMARY_PAYMENT_COUNT            = "paymentCount";
  public static final String PROPERTY_LINE_SUMMARY_DISCOUNT_TOTAL           = "discountTotal";
  public static final String PROPERTY_LINE_SUMMARY_DISCOUNT_COUNT           = "discountCount";
  public static final String PROPERTY_LINE_SUMMARY_GIFTCARD_SOLD_TOTAL      = "giftcardSoldTotal";
  public static final String PROPERTY_LINE_SUMMARY_GIFTCARD_SOLD_COUNT      = "giftcardSoldCount";
  
  // Extract
  public static final String PROPERTY_EXTRACT_EXTRACT_ID                    = "id";
  public static final String PROPERTY_EXTRACT_EXTRACT_DATE                  = "extractDate";
  public static final String PROPERTY_EXTRACT_EXTRACT_FILE_NAME             = "extractFileName";
  public static final String PROPERTY_EXTRACT_RUN_TYPE                      = "runType";  
  public static final String PROPERTY_EXTRACT_PAYMENT_SUMMARY               = "paymentSummary";
  public static final String PROPERTY_EXTRACT_EXTRACT_SUMMARY               = "extractSummary"; 
  
  // Extract Summary
  public static final String PROPERTY_EXTRACT_SUMMARY_EXTRACT_SUMMARY_ID    = "id";
  public static final String PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TOTAL     = "transactionTotal";
  public static final String PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TAXABLE_TOTAL = "transactionTaxableTotal";
  public static final String PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TAX_TOTAL = "transactionTaxTotal";
  public static final String PROPERTY_EXTRACT_SUMMARY_TRANS_COUNT           = "transactionCount";
  public static final String PROPERTY_EXTRACT_SUMMARY_LINE_COUNT            = "lineCount";
  public static final String PROPERTY_EXTRACT_SUMMARY_PAYMENT_TOTAL         = "paymentTotal";
  public static final String PROPERTY_EXTRACT_SUMMARY_PAYMENT_COUNT         = "paymentCount";
  public static final String PROPERTY_EXTRACT_SUMMARY_DISCOUNT_TOTAL        = "discountTotal";
  public static final String PROPERTY_EXTRACT_SUMMARY_DISCOUNT_COUNT        = "discountCount";
  public static final String PROPERTY_EXTRACT_SUMMARY_GIFTCARD_SOLD_TOTAL   = "giftcardSoldTotal";
  public static final String PROPERTY_EXTRACT_SUMMARY_GIFTCARD_SOLD_COUNT   = "giftcardSoldCount";
  
  // Summary Payment
  public static final String PROPERTY_PAYMENT_SUMMARY_EXTRACT_PAYMENT_ID    = "id";
  public static final String PROPERTY_PAYMENT_SUMMARY_PAYMENT_TYPE          = "paymentType";
  public static final String PROPERTY_PAYMENT_SUMMARY_CREDIT_TOTAL          = "creditTotal";
  public static final String PROPERTY_PAYMENT_SUMMARY_CREDIT_COUNT          = "creditCount";
  public static final String PROPERTY_PAYMENT_SUMMARY_DEBIT_TOTAL           = "debitTotal";
  public static final String PROPERTY_PAYMENT_SUMMARY_DEBIT_COUNT           = "debitCount";

  // Tax Constants
  public static final String LINE_LOCAL_TAX                                 = "localTax";
  public static final String LINE_COUNTY_TAX                                = "countyTax";
  public static final String LINE_STATE_TAX                                 = "stateTax";
  public static final String LINE_SHIPPING                                  = "shipping";
  public static final String LINE_SHIPPING_LOCAL_TAX                        = "shippingLocalTax";
  public static final String LINE_SHIPPING_COUNTY_TAX                       = "shippingCountyTax";
  public static final String LINE_SHIPPING_STATE_TAX                        = "shippingStateTax";
  public static final String LINE_TAX_TOTAL                                 = "lineTaxTotal";
  public static final String LINE_SHIPPING_TAX                              = "lineShippingTax";
  public static final String LINE_EXTENDED_TOTAL                            = "lineExtendedTotal";
  public static final String LINE_DISCOUNT                                  = "lineDiscount";

  public static final String DISCOUNT_TYPE                                  = "DISC";
  public static final String OVERRIDE_DISCOUNT_TYPE                         = "OVR";
  public static final String DISCOUNT_SOURCE                                = "WEB";
  public static final String DISCOUNT_CODE                                  = "ORD";
  public static final String ORDER_DISCOUNT_TYPE                            = "ORD";
  public static final String EMP_DISCOUNT_TYPE                              = "EMPDSC";
  
  public static final String SHIP_DISCOUNT_TYPE                             = "DISC";
  public static final String SHIP_DISCOUNT_SOURCE                           = "WEB";
  public static final String SHIP_DISCOUNT_CODE                             = "SHP";
  
  public static final String AUXILIARY_TYPE_ORDER                           = "order";
  public static final String AUXILIARY_TYPE_PAYMENT                         = "payment";
  public static final String AUXILIARY_TYPE_RETURN                          = "return";
  public static final String AUXILIARY_TYPE_SHIPMENT                        = "shipment";
  
  public static final String AUXILIARY_TYPE_SHIPMENT_TAXABLE                = "taxable";
  public static final String AUXILIARY_TYPE_SHIPMENT_BRAND                  = "brand";
  public static final String AUXILIARY_TYPE_SHIPMENT_GIFT_CERT              = "gift_cert";
  public static final String AUXILIARY_TYPE_SHIPMENT_COST                   = "cost";
  public static final String AUXILIARY_TYPE_ORDER_SUB_MARKET                = "Sub-Market";
  public static final String AUXILIARY_TYPE_ORDER_CUSTOMER_ID               = "Customer-ID";
  public static final String AUXILIARY_TYPE_ORDER_BASE_SALES_TAX_RATE       = "base_sales_tax_rate";
  
  public static final String EXTRACT_STATUS_CREATED                         = "created";
  public static final String EXTRACT_STATUS_EXTRACTED                       = "extracted";
  public static final String EXTRACT_STATUS_ERROR                           = "error";
  
  public static final String NO_MARKET_CODE                                 = "No market code in Sara";
  
  public static final String GIFT_CARD_PAYMENT_TYPE                         = "GC";
  
  public static final String RUN_TYPE_STANDARD                              = "standard";
  public static final String RUN_TYPE_RERUN                                 = "re-run";
  
  public static final String ADDRESS_TYPE_SHIPPING                          = "shipping";
  public static final String ADDRESS_TYPE_BILLING                           = "billing";
  
  public static final String SCHEMA_VERSION                                 = "1.2";
  
  public static final String DEFAULT_TRACKING_NUMBER                        = "PENDING";
  public static final String DEFAULT_SHIP_VIA                               = "RGND";
  
  // Tax Exemption Values
  public static final String TAX_EXEMPTION_CODE                              = "taxExemptionCode";
  public static final String TAX_EXEMPTION_CERT                              = "taxExemptionCert";
  public static final String TAX_EXEMPTION_NAME                              = "taxExemptionName";
  
  // Payment Auxillaries
  public static final String EXPIRATION_DATE                                 = "expiration_date";
  public static final String AUTHORIZATION_NUMBER                            = "auth_num";
  public static final String SETTLEMENT_NUMBER                               = "settlement_num";
  
  //public static final String INVOICE_STATUS_CREATED                         = "created";
  //public static final String EXTRACT_STATUS_EXTRACTED                       = "extracted";
  //public static final String EXTRACT_STATUS_ERROR                           = "error";
}