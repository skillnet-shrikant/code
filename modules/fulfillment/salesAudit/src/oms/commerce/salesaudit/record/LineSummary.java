package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class represents a line summary record in the invoice repository.  The line summary records the totals for 
 * a shipment, return or appeasement.
 * 
 * @author jvose
 *
 */
public class LineSummary 
  extends SalesAuditItem {

  /** Default Constructor **/
  public LineSummary () {    
  }
  
  /** Other Constructor **/
  public LineSummary (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }
  
  /**  lineSummary - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_LINE_SUMMARY_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_LINE_SUMMARY_ID, pId);
  }
   
  /**  lineSummary - transactionTotal   **/
  public Double getTransactionTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_TRANSACTION_TOTAL);
  }
  public void  setTransactionTotal(Double pTransactionTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_TRANSACTION_TOTAL, pTransactionTotal);
  }
   
  /**  lineSummary - transactionTaxableTotal   **/
  public Double getTransactionTaxableTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_TRANSACTION_TAXABLE_TOTAL);
  }
  public void  setTransactionTaxableTotal(Double pTransactionTaxableTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_TRANSACTION_TAXABLE_TOTAL, pTransactionTaxableTotal);
  }
   
  /**  lineSummary - transactionTaxTotal   **/
  public Double getTransactionTaxTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_TRANSACTION_TAX_TOTAL);
  }
  public void  setTransactionTaxTotal(Double pTransactionTaxTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_TRANSACTION_TAX_TOTAL, pTransactionTaxTotal);
  }
   
  /**  lineSummary - lineCount   **/
  public Long getLineCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_LINE_COUNT);
  }
  public void  setLineCount(Long pLineCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_LINE_COUNT, pLineCount);
  }
   
  /**  lineSummary - paymentTotal   **/
  public Double getPaymentTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_PAYMENT_TOTAL);
  }
  public void  setPaymentTotal(Double pPaymentTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_PAYMENT_TOTAL, pPaymentTotal);
  }

  /**  lineSummary - paymentCount   **/
  public Long getPaymentCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_PAYMENT_COUNT);
  }
  public void  setPaymentCount(Long pPaymentCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_PAYMENT_COUNT, pPaymentCount);
  }
  
  /**  lineSummary - discountTotal   **/
  public Double getDiscountTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_DISCOUNT_TOTAL);
  }
  public void  setDiscountTotal(Double pDiscountTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_DISCOUNT_TOTAL, pDiscountTotal);
  }
   
  /**  lineSummary - discountCount   **/
  public Long getDiscountCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_DISCOUNT_COUNT);
  }
  public void  setDiscountCount(Long pDiscountCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_DISCOUNT_COUNT, pDiscountCount);
  }
   
  /**  lineSummary - giftcardSoldTotal   **/
  public Double getGiftcardSoldTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_GIFTCARD_SOLD_TOTAL);
  }
  public void  setGiftcardSoldTotal(Double pGiftcardSoldTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_GIFTCARD_SOLD_TOTAL, pGiftcardSoldTotal);
  }
   
  /**  lineSummary - giftcardSoldCount   **/
  public Long getGiftcardSoldCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_GIFTCARD_SOLD_COUNT);
  }
  public void  setGiftcardSoldCount(Long pGiftcardSoldCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_SUMMARY_GIFTCARD_SOLD_COUNT, pGiftcardSoldCount);
  }

  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"               + lLineFeed);
    lStringBuffer.append ("             Line Summary Record  "                                + lLineFeed);
    lStringBuffer.append ("Line Summary ID ............... " + getId()                        + lLineFeed);
    lStringBuffer.append ("Transaction Total ............. " + getTransactionTotal()          + lLineFeed);
    lStringBuffer.append ("Transaction Taxable Total ..... " + getTransactionTaxableTotal()   + lLineFeed); 
    lStringBuffer.append ("Transaction Tax Total ......... " + getTransactionTaxTotal()       + lLineFeed);
    lStringBuffer.append ("Line Count .................... " + getLineCount()                 + lLineFeed);
    lStringBuffer.append ("Payment Total ................. " + getPaymentTotal()              + lLineFeed);
    lStringBuffer.append ("Payment Count ................. " + getPaymentCount()              + lLineFeed);
    lStringBuffer.append ("Discount Total ................ " + getDiscountTotal()             + lLineFeed);    
    lStringBuffer.append ("Discount Count ................ " + getDiscountCount()             + lLineFeed);
    lStringBuffer.append ("Gift Card Sold Total .......... " + getGiftcardSoldTotal()         + lLineFeed);
    lStringBuffer.append ("Gift Card Sold Count .......... " + getGiftcardSoldCount()         + lLineFeed);
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }       
    
}
