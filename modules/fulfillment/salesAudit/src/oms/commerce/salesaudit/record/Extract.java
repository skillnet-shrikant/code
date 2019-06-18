package oms.commerce.salesaudit.record;

import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import atg.repository.MutableRepositoryItem;

/**
 * This class will represent an extract record in the invoice repository.  The extract record
 * records information about each time an extract is sent to Sales Audit.
 * 
 * @author jvose
 *
 */
public class Extract 
  extends SalesAuditItem {

  /** Default Constructor **/
  public Extract () {    
  }
  
  /** Other Constructor **/
  public Extract (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }  
  
  /**  extract - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_ID, pId);
  }
   
  /**  extract - extractDate   **/
  public Timestamp getExtractDate() {
     return (Timestamp) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_DATE);
  }
  public void  setExtractDate(Timestamp pExtractDate) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_DATE, pExtractDate);
  }
   
  /**  extract - extractFileName   **/
  public String getExtractFileName() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_FILE_NAME);
  }
  public void  setExtractFileName(String pExtractFileName) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_FILE_NAME, pExtractFileName);
  }
   
  /**  extract - runType   **/
  public String getRunType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_RUN_TYPE);
  }
  public void  setRunType(String pRunType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_RUN_TYPE, pRunType);
  }

  /**  extract - Extract Summary   **/
  public ExtractSummary getExtractSummary() {
    MutableRepositoryItem lItem =  (MutableRepositoryItem) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_SUMMARY);
    return new ExtractSummary (lItem); 
  }
  public void  setExtractSummary(ExtractSummary pExtractSummary) {
     setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_EXTRACT_SUMMARY, pExtractSummary.getRepositoryItem());
  }
  
 // /**  extract - Payment Summary     **/
 // public List<PaymentSummary> getPaymentSummary  () {
 //    return (List<PaymentSummary>) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_PAYMENT_SUMMARY);
 // }
 // public void  setPaymentSummary  (List<PaymentSummary> pPaymentSummary) {
 //    setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_PAYMENT_SUMMARY, pPaymentSummary);
 // }  
  
  /**  extract - Payment Summary     **/
  @SuppressWarnings("unchecked")
  public List<PaymentSummary> getPaymentSummary  () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_PAYMENT_SUMMARY);
    List <PaymentSummary> lPaymentSummarys = new Vector <PaymentSummary> ();    
    for (Object lItem : lItems) {
      PaymentSummary lPaymentSummary = new PaymentSummary ((MutableRepositoryItem)lItem);
      lPaymentSummarys.add(lPaymentSummary);
    }
    return lPaymentSummarys;
  }
  public void  setPaymentSummary  (List<PaymentSummary> pPaymentSummarys) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    for (PaymentSummary lPaymentSummary : pPaymentSummarys) {
      lItems.add(lPaymentSummary.getRepositoryItem());
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_EXTRACT_PAYMENT_SUMMARY, lItems);
  }  
  
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"                 + lLineFeed);
    lStringBuffer.append ("             Extract Record        "                                 + lLineFeed);
    lStringBuffer.append ("Extract ID ...................... " + getId()                        + lLineFeed);
    lStringBuffer.append ("Extract Date .................... " + getExtractDate()               + lLineFeed); 
    lStringBuffer.append ("File Name ....................... " + getExtractFileName()           + lLineFeed); 
    lStringBuffer.append ("Run Type ........................ " + getRunType()                   + lLineFeed);
    lStringBuffer.append (getExtractSummary().toString()                                        + lLineFeed);
    List <PaymentSummary> lPaymentSummarys = getPaymentSummary();
    for (PaymentSummary lPaymentSummary : lPaymentSummarys) {
      lStringBuffer.append (lPaymentSummary.toString()                                          + lLineFeed);    
    }
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }

  
}
