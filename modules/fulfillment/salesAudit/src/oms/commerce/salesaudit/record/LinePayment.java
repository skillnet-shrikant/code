package oms.commerce.salesaudit.record;

import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import atg.repository.MutableRepositoryItem;

/**
 * This class represents the payment method that was used for a shipment, return or appeasement in the 
 * invoice repository.  
 * 
 * @author jvose
 *
 */
public class LinePayment 
  extends SalesAuditItem {

  /** Default Constructor **/
  public LinePayment () {    
  }
  
  /** Other Constructor **/
  public LinePayment (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }

  /**  payment - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_PAYMENT_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_PAYMENT_ID, pId);
  }
   
  /**  payment - paymentType   **/
  public String getPaymentType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_PAYMENT_TYPE);
  }
  public void  setPaymentType(String pPaymentType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_PAYMENT_TYPE, pPaymentType);
  }
   
  /**  payment - amount   **/
  public Double getAmount() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_AMOUNT);
  }
  public void  setAmount(Double pAmount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_AMOUNT, pAmount);
  }
   
  /**  payment - transactionReference   **/
  public String getTransactionReference() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_TRANSACTION_REFERENCE);
  }
  public void  setTransactionReference(String pTransactionReference) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_TRANSACTION_REFERENCE, pTransactionReference);
  }
   
  /**  payment - paymentDate   **/
  public Timestamp getPaymentDate() {
     return (Timestamp) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_PAYMENT_DATE);
  }
  public void  setPaymentDate(Timestamp pPaymentDate) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_PAYMENT_DATE, pPaymentDate);
  }
   
  /**  payment - cardReference   **/
  public String getCardReference() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_CARD_REFERENCE);
  }
  public void  setCardReference(String pCardReference) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_CARD_REFERENCE, pCardReference);
  }
   
  /**  payment - cardNumber   **/
  public String getCardNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_CARD_NUMBER);
  }
  public void  setCardNumber(String pCardNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_CARD_NUMBER, pCardNumber);
  }
   
  /**  payment - tokenId   **/
  public String getTokenId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_TOKEN_ID);
  }
  public void  setTokenId(String pTokenId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_PAYMENT_TOKEN_ID, pTokenId);
  }
  
  /**  payment - Auxiliaries    **/
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
    for (AuxiliaryRecord lAuxiliaryRecord : pAuxiliaries) {
      lItems.add(lAuxiliaryRecord.getRepositoryItem());
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
    lStringBuffer.append ("***************************************************"           + lLineFeed);
    lStringBuffer.append ("             Line Payment Record  "                            + lLineFeed);
    lStringBuffer.append ("Line Payment ID ................ " + getId()                   + lLineFeed);
    lStringBuffer.append ("Payment Type .................. " + getPaymentType()           + lLineFeed);
    lStringBuffer.append ("Amount ........................ " + getAmount()                + lLineFeed); 
    lStringBuffer.append ("Transaction Reference ......... " + getTransactionReference()  + lLineFeed);
    lStringBuffer.append ("Payment Date .................. " + getPaymentDate()           + lLineFeed);
    lStringBuffer.append ("Card Reference ................ " + getCardReference()         + lLineFeed);
    lStringBuffer.append ("Card Number ................... " + getCardNumber()            + lLineFeed);
    lStringBuffer.append ("Token ID ...................... " + getTokenId()               + lLineFeed);
    for (AuxiliaryRecord lAuxiliaryRecord : getAuxiliaries()) {
      lStringBuffer.append (lAuxiliaryRecord.toString()                                   + lLineFeed);
    }
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }       
  

}
