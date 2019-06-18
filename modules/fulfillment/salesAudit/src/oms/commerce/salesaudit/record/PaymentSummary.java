package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class represents the summary of all payments used in a given Sales Audit extract.  
 * 
 * @author jvose
 *
 */
public class PaymentSummary 
  extends SalesAuditItem {

  /** Default Constructor **/
  public PaymentSummary () {    
  }
  
  /** Other Constructor **/
  public PaymentSummary (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }  
  
  /**  paymentSummary - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_EXTRACT_PAYMENT_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_EXTRACT_PAYMENT_ID, pId);
  }
   
  /**  paymentSummary - paymentType   **/
  public String getPaymentType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_PAYMENT_TYPE);
  }
  public void  setPaymentType(String pPaymentType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_PAYMENT_TYPE, pPaymentType);
  }
   
  /**  paymentSummary - creditTotal   **/
  public Double getCreditTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_CREDIT_TOTAL);
  }
  public void  setCreditTotal(Double pCreditTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_CREDIT_TOTAL, pCreditTotal);
  }
   
  /**  paymentSummary - creditCount   **/
  public Long getCreditCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_CREDIT_COUNT);
  }
  public void  setCreditCount(Long pCreditCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_CREDIT_COUNT, pCreditCount);
  }
   
  /**  paymentSummary - debitTotal   **/
  public Double getDebitTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_DEBIT_TOTAL);
  }
  public void  setDebitTotal(Double pDebitTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_DEBIT_TOTAL, pDebitTotal);
  }
   
  /**  paymentSummary - debitCount   **/
  public Long getDebitCount() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_DEBIT_COUNT);
  }
  public void  setDebitCount(Long pDebitCount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_SUMMARY_DEBIT_COUNT, pDebitCount);
  }
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************" + lLineFeed);
    lStringBuffer.append ("             Payment Summary Record  "               + lLineFeed);
    lStringBuffer.append ("Line Summary ID ............... " + getId()          + lLineFeed);
    lStringBuffer.append ("Payment Type .................. " + getPaymentType() + lLineFeed);
    lStringBuffer.append ("Credit Total .................. " + getCreditTotal() + lLineFeed); 
    lStringBuffer.append ("Credit Count .................. " + getCreditCount() + lLineFeed);
    lStringBuffer.append ("Debit Total ................... " + getDebitTotal()  + lLineFeed);
    lStringBuffer.append ("Debit Count ................... " + getDebitCount()  + lLineFeed);    
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }    

}
