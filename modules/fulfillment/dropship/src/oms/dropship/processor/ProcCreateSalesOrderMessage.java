package oms.dropship.processor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.RepositoryContactInfo;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import mff.dropship.salesOrder.xsd.CustomerInformation;
import mff.dropship.salesOrder.xsd.Detail;
import mff.dropship.salesOrder.xsd.Header;
import mff.dropship.salesOrder.xsd.LineItem;
import mff.dropship.salesOrder.xsd.Message;
import mff.dropship.salesOrder.xsd.MessageHeader;
import mff.dropship.salesOrder.xsd.ObjectFactory;
import mff.dropship.salesOrder.xsd.OrderHeader;
import mff.dropship.salesOrder.xsd.SalesOrderSubmission;
import mff.dropship.salesOrder.xsd.ShipmentInformation;
import mff.dropship.salesOrder.xsd.TransactionInfo;
import oms.allocation.item.AllocationConstants;

public class ProcCreateSalesOrderMessage extends GenericService implements PipelineProcessor {

  private final static int  SUCCESS = 1;

  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    vlogDebug("Entering ProcCreateSalesOrderMessage - runProcess");
    
    Map lParams = (Map) pPipelineParams;
    Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    if(lOrder != null){
      Message message = createJaxbObj(lOrder);
      lParams.put(AllocationConstants.PIPELINE_PARAMETER_DROP_SHIP_MESSAGE, message);
    }
    
    return SUCCESS;
  }
  
  private Message createJaxbObj(Order pOrder){
    vlogDebug("Entering ProcCreateSalesOrderMessage - createJaxbObj for orderId {0}",pOrder.getId());
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    
    ObjectFactory objFactory = new ObjectFactory();
    Message message = objFactory.createMessage();
    message.setMessageHeader(createMessageHeader(objFactory, lOrder));
    message.getSalesOrderSubmission().add(createSalesOrderSubmission(objFactory, lOrder));
    message.setTransactionInfo(createTransactionInfo(objFactory));
    
    return message;
    
  }
  
  private MessageHeader createMessageHeader(ObjectFactory objFactory,Order pOrder){
    
    MessageHeader messageHeader = objFactory.createMessageHeader();
    
    messageHeader.setMessageId(BigInteger.valueOf(System.currentTimeMillis()));// use currentTime
    messageHeader.setTransactionName(getTransactionName());
    messageHeader.setPartnerName(getPartnerName());
    messageHeader.setSourceUrl(getSourceUrl());
    messageHeader.setCreateTimestamp(formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
    messageHeader.setResponseRequest(BigInteger.ONE);
    return messageHeader;
  }
  
  private SalesOrderSubmission createSalesOrderSubmission(ObjectFactory objFactory,Order pOrder){
    
    SalesOrderSubmission salesOrderSubmission = objFactory.createSalesOrderSubmission();
    salesOrderSubmission.setHeader(createHeader(objFactory, pOrder));
    salesOrderSubmission.setDetail(createDetail(objFactory, pOrder));
    return salesOrderSubmission;
  }
  
  private Header createHeader(ObjectFactory objFactory,Order pOrder){
    
    Header header = objFactory.createHeader();
    header.setCustomerId(pOrder.getProfileId());
    CreditCard creditCard = getCreditCardPaymentGroup(pOrder);
    if (creditCard != null && creditCard.getBillingAddress() != null) {
      header.setCustomerInformation(createCustomerInformation(objFactory,pOrder,creditCard));
    }
    HardgoodShippingGroup shippingGroup = getHardgoodShippingGroup(pOrder);
    if (shippingGroup != null && shippingGroup.getShippingAddress() != null) {
      header.setShipmentInformation(createShipmentInformation(objFactory,pOrder,shippingGroup));
    }
    header.setOrderHeader(createOrderHeader(objFactory, pOrder));
    
    return header;
  }
  
  private CustomerInformation createCustomerInformation(ObjectFactory objFactory,Order pOrder,CreditCard creditCard){
    
    RepositoryContactInfo billAddress = (RepositoryContactInfo) (creditCard.getBillingAddress());
    CustomerInformation customerInformation = objFactory.createCustomerInformation();
    customerInformation.setCustomerFirstName(billAddress.getFirstName());
    customerInformation.setCustomerLastName(billAddress.getLastName());
    customerInformation.setCustomerAddress1(billAddress.getAddress1());
    if(!Strings.isNullOrEmpty(billAddress.getAddress1())){
      customerInformation.setCustomerAddress2(billAddress.getAddress2());
    }
    customerInformation.setCustomerCity(billAddress.getCity());
    customerInformation.setCustomerState(billAddress.getState());
    customerInformation.setCustomerPostCode(billAddress.getPostalCode());
    customerInformation.setCustomerCountryCode(billAddress.getCountry());
    if(!Strings.isNullOrEmpty(billAddress.getPhoneNumber())){
      customerInformation.setCustomerPhone1(billAddress.getPhoneNumber());
    }
    return customerInformation;
  }
  
  private ShipmentInformation createShipmentInformation(ObjectFactory objFactory,Order pOrder,HardgoodShippingGroup pShippingGroup){
    ShipmentInformation shipmentInformation = objFactory.createShipmentInformation();
    
    RepositoryContactInfo shippingAddress = (RepositoryContactInfo) (pShippingGroup.getShippingAddress());
    shipmentInformation.setShipFirstName(shippingAddress.getFirstName());
    shipmentInformation.setShipLastName(shippingAddress.getLastName());
    shipmentInformation.setShipAddress1(shippingAddress.getAddress1());
    if(!Strings.isNullOrEmpty(shippingAddress.getAddress2())){
      shipmentInformation.setShipAddress2(shippingAddress.getAddress2());
    }
    shipmentInformation.setShipCity(shippingAddress.getCity());
    shipmentInformation.setShipState(shippingAddress.getState());
    shipmentInformation.setShipPostCode(shippingAddress.getPostalCode());
    shipmentInformation.setShipCountryCode(shippingAddress.getCountry());
    if(!Strings.isNullOrEmpty(shippingAddress.getPhoneNumber())){
      shipmentInformation.setShipPhone1(shippingAddress.getPhoneNumber());
    }
    shipmentInformation.setShipVia(pShippingGroup.getShippingMethod());
    shipmentInformation.setShipRequestDate(formatDate(pOrder.getSubmittedDate(),"YYYYMMdd"));
    return shipmentInformation;
  }
  
  private OrderHeader createOrderHeader(ObjectFactory objFactory,Order pOrder){
    MFFOrderImpl lOrderImpl = (MFFOrderImpl) pOrder;
    OrderHeader orderHeader = objFactory.createOrderHeader();
    orderHeader.setCustomerOrderNumber(lOrderImpl.getOrderNumber());
    orderHeader.setCustomerOrderDate(formatDate(lOrderImpl.getSubmittedDate(),"YYYYMMdd"));
    OrderPriceInfo lPriceInfo = lOrderImpl.getPriceInfo();
    if(lPriceInfo != null){
      orderHeader.setOrderSubTotal(BigDecimal.valueOf(lPriceInfo.getRawSubtotal()));
      orderHeader.setOrderDiscount(BigDecimal.valueOf(lPriceInfo.getDiscountAmount()));
      orderHeader.setOrderTotalNet(BigDecimal.valueOf(lPriceInfo.getTotal()));
      orderHeader.setOrderTax1(BigDecimal.valueOf(lPriceInfo.getTax()));
      orderHeader.setOrderShipmentCharge(BigDecimal.valueOf(lPriceInfo.getShipping()));
    }
    orderHeader.setOrderType("MFF");
    return orderHeader;
  }
  
  @SuppressWarnings("unchecked")
  private Detail createDetail(ObjectFactory objFactory,Order pOrder){
    Detail detail =  objFactory.createDetail();
    
    List<CommerceItem> ciList = pOrder.getCommerceItems();
    for(CommerceItem ciItem : ciList){
      MFFCommerceItemImpl lItem = (MFFCommerceItemImpl) ciItem;
      if(lItem.getStateAsString().equalsIgnoreCase(
          MFFCommerceItemStates.PENDING_DROP_SHIP_FULFILLMENT)){
        
        detail.getLineItem().add(createLineItem(objFactory, pOrder, lItem));
      }
    }
    
    return detail;
  }
  
  private LineItem createLineItem(ObjectFactory objFactory,Order pOrder, MFFCommerceItemImpl pItem){
    LineItem lineItem = objFactory.createLineItem();
    ItemPriceInfo priceInfo = pItem.getPriceInfo();
    
    String lineNo = pItem.getId().replace("ci","");
    lineItem.setLineNo(lineNo);
    lineItem.setItemCode(pItem.getCatalogRefId());
    lineItem.setQuantity(BigDecimal.valueOf(pItem.getQuantity()));
    lineItem.setUnitOfMeasure("EA");
    lineItem.setFacilityCd(getFacilityCode());
    lineItem.setBasePrice(BigDecimal.valueOf(priceInfo.getAmount()));
    
    return lineItem;
    
  }
  
  private TransactionInfo createTransactionInfo(ObjectFactory objFactory){
    TransactionInfo transactionInfo = objFactory.createTransactionInfo();
    transactionInfo.setEventID(getEventId());
    return transactionInfo;
  }
  
  private String formatDate(Date pDate, String pFormat) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(pFormat);
    String formattedDate = dateFormat.format(pDate);
    return formattedDate;
  }
  
  @SuppressWarnings("unchecked")
  private CreditCard getCreditCardPaymentGroup(Order pOrder) {

    if (pOrder.getPaymentGroups() != null && pOrder.getPaymentGroups().size() > 0) {
      List<PaymentGroup> pgGroups = pOrder.getPaymentGroups();
      for(PaymentGroup pg : pgGroups){
        if (pg instanceof CreditCard) {
          return (CreditCard) pg;
        }
      }
    }

    return null;
  }
  
  private HardgoodShippingGroup getHardgoodShippingGroup(Order pOrder) {

    if (pOrder.getShippingGroups() != null && pOrder.getShippingGroups().size() > 0) {
      ShippingGroup sg = (ShippingGroup) pOrder.getShippingGroups().get(0);
      if (sg instanceof HardgoodShippingGroup) {
        return (HardgoodShippingGroup) sg;
      }
    }

    return null;
  }
  
  private String facilityCode; // MFF4001
  private String eventId; //MFFOrder
  private String transactionName; //sales-order-submission
  private String partnerName;  //("dmi");
  private String sourceUrl; //http://www.dminc.com

  public String getFacilityCode() {
    return facilityCode;
  }

  public void setFacilityCode(String facilityCode) {
    this.facilityCode = facilityCode;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getTransactionName() {
    return transactionName;
  }

  public void setTransactionName(String transactionName) {
    this.transactionName = transactionName;
  }

  public String getPartnerName() {
    return partnerName;
  }

  public void setPartnerName(String partnerName) {
    this.partnerName = partnerName;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }
  
  

}
