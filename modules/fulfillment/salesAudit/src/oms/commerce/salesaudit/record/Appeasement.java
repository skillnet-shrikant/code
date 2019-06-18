package oms.commerce.salesaudit.record;

import java.sql.Timestamp;
import atg.repository.MutableRepositoryItem;

/**
 * This class will represent an appeasement item in the invoice repository, and 
 * will be used to create the Sales Audit extract file.
 * 
 * @author jvose
 *
 */
public class Appeasement 
   extends SalesAuditItem {
  
  /** Default Constructor **/
  public Appeasement () {    
  }
  
  /** Other Constructor **/
  public Appeasement (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }
  
  /**  appeasement - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASEMENT_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASEMENT_ID, pId);
  }
   
  /**  appeasement - appeaseCode   **/
  public String getAppeaseCode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASE_CODE);
  }
  public void  setAppeaseCode(String pAppeaseCode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASE_CODE, pAppeaseCode);
  }
   
  /**  appeasement - appeaseDescription   **/
  public String getAppeaseDescription() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASE_DESCRIPTION);
  }
  public void  setAppeaseDescription(String pAppeaseDescription) {
     setPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASE_DESCRIPTION, pAppeaseDescription);
  }
   
  /**  appeasement - reference   **/
  public String getReference() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_REFERENCE);
  }
  public void  setReference(String pReference) {
     setPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_REFERENCE, pReference);
  }
   
  /**  appeasement - appeaseDate   **/
  public Timestamp getAppeaseDate() {
     return (Timestamp) getPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASE_DATE);
  }
  public void  setAppeaseDate(Timestamp pAppeaseDate) {
     setPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_APPEASE_DATE, pAppeaseDate);
  }
   
  /**  appeasement - amount   **/
  public Double getAmount() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_AMOUNT);
  }
  public void  setAmount(Double pAmount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_APPEASEMENT_AMOUNT, pAmount);
  }
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"           + lLineFeed);
    lStringBuffer.append ("           Order Appeasement      "                            + lLineFeed);
    lStringBuffer.append ("Appeasement ID .................. " + getId()                  + lLineFeed); 
    lStringBuffer.append ("Appeasement Code ................ " + getAppeaseCode()         + lLineFeed); 
    lStringBuffer.append ("Appeasement Description ......... " + getAppeaseDescription()  + lLineFeed); 
    lStringBuffer.append ("Reference ....................... " + getReference()           + lLineFeed); 
    lStringBuffer.append ("Appeasement Date ................ " + getAppeaseDate()         + lLineFeed); 
    lStringBuffer.append ("Amount .......................... " + getAmount()              + lLineFeed); 
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }
  
}
