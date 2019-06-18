package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class will represent a extract summary record in the invoice repository.  The extract summary
 * is a summarization of all the payment methods used in the Sales Ausdit feed.
 * @author jvose
 *
 */
public class ExtractSummary 
  extends SalesAuditItem {

  /** Default Constructor **/
  public ExtractSummary () {    
  }
  
  /** Other Constructor **/
  public ExtractSummary (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }  
  
  /**  extractSummary - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_EXTRACT_SUMMARY_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_EXTRACT_SUMMARY_ID, pId);
  }
   
  /**  extractSummary - transactionTotal   **/
  public Double getTransactionTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TOTAL);
  }
  public void  setTransactionTotal(Double pTransactionTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TOTAL, pTransactionTotal);
  }
   
  /**  extractSummary - transactionTaxableTotal   **/
  public Double getTransactionTaxableTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TAXABLE_TOTAL);
  }
  public void  setTransactionTaxableTotal(Double pTransactionTaxableTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TAXABLE_TOTAL, pTransactionTaxableTotal);
  }
   
  /**  extractSummary - transactionTaxTotal   **/
  public Double getTransactionTaxTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TAX_TOTAL);
  }
  public void  setTransactionTaxTotal(Double pTransactionTaxTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANSACTION_TAX_TOTAL, pTransactionTaxTotal);
  }
   
  /**  extractSummary - lineCount   **/
  public Long getLineCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_LINE_COUNT);
  }
  public void  setLineCount(Long pLineCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_LINE_COUNT, pLineCount);
  }

  /**  extractSummary - transactionCount   **/
  public Long getTransactionCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANS_COUNT);
  }
  public void  setTransactionCount(Long pTransactionCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_TRANS_COUNT, pTransactionCount);
  }  
  
  /**  extractSummary - paymentTotal   **/
  public Double getPaymentTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_PAYMENT_TOTAL);
  }
  public void  setPaymentTotal(Double pPaymentTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_PAYMENT_TOTAL, pPaymentTotal);
  }

  /**  extractSummary - paymentCount   **/
  public Long getPaymentCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_PAYMENT_COUNT);
  }
  public void  setPaymentCount (Long pPaymentCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_PAYMENT_COUNT, pPaymentCount);
  }
  
  /**  extractSummary - discountTotal   **/
  public Double getDiscountTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_DISCOUNT_TOTAL);
  }
  public void  setDiscountTotal(Double pDiscountTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_DISCOUNT_TOTAL, pDiscountTotal);
  }
   
  /**  extractSummary - discountCount   **/
  public Long getDiscountCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_DISCOUNT_COUNT);
  }
  public void  setDiscountCount(Long pDiscountCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_DISCOUNT_COUNT, pDiscountCount);
  }
   
  /**  extractSummary - giftcardSoldTotal   **/
  public Double getGiftcardSoldTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_GIFTCARD_SOLD_TOTAL);
  }
  public void  setGiftcardSoldTotal(Double pGiftcardSoldTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_GIFTCARD_SOLD_TOTAL, pGiftcardSoldTotal);
  }
   
  /**  extractSummary - giftcardSoldCount   **/
  public Long getGiftcardSoldCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_GIFTCARD_SOLD_COUNT);
  }
  public void  setGiftcardSoldCount(Long pGiftcardSoldCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_SUMMARY_GIFTCARD_SOLD_COUNT, pGiftcardSoldCount);
  }

  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"                 + lLineFeed);
    lStringBuffer.append ("             Extract Summary Record                "                 + lLineFeed);
    lStringBuffer.append ("Extract Summary ID .............. " + getId()                        + lLineFeed);    
    lStringBuffer.append ("Transaction Total ............... " + getTransactionTotal()          + lLineFeed); 
    lStringBuffer.append ("Transaction Taxable Total ....... " + getTransactionTaxableTotal()   + lLineFeed); 
    lStringBuffer.append ("Traqnsaction Tax Total .......... " + getTransactionTaxTotal()       + lLineFeed);
    lStringBuffer.append ("Line Count ...................... " + getLineCount()                 + lLineFeed);
    lStringBuffer.append ("Payment Total ................... " + getPaymentTotal()              + lLineFeed);
    lStringBuffer.append ("Payment Count ................... " + getPaymentCount()              + lLineFeed);
    lStringBuffer.append ("Discount Total .................. " + getDiscountTotal()             + lLineFeed);
    lStringBuffer.append ("Discount Count .................. " + getDiscountCount()             + lLineFeed);
    lStringBuffer.append ("Gift Card Total ................. " + getGiftcardSoldTotal()         + lLineFeed);
    lStringBuffer.append ("Gift Card Count ................. " + getGiftcardSoldCount()         + lLineFeed);    
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }  
}
