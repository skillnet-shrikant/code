package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class will represent an auxiliary record in the invoice repository.  Auxiliary records 
 * record additional information about an order, payment method or item.
 * 
 * @author jvose
 *
 */
public class AuxiliaryRecord 
  extends SalesAuditItem {

  /** Default Constructor **/
  public AuxiliaryRecord () {    
  }
  
  /** Other Constructor **/
  public AuxiliaryRecord (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }
  
  /**  auxilliary - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_ID, pId);
  }
   
  /**  auxilliary - type   **/
  public String getType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_TYPE);
  }
  public void  setType(String pType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_TYPE, pType);
  }
   
  /**  auxilliary - auxilliaryName   **/
  public String getAuxilliaryName() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_NAME);
  }
  public void  setAuxilliaryName(String pAuxilliaryName) {
     setPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_NAME, pAuxilliaryName);
  }
   
  /**  auxilliary - auxilliaryValue   **/
  public String getAuxilliaryValue() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_VALUE);
  }
  public void  setAuxilliaryValue(String pAuxilliaryValue) {
     setPropertyValue (SalesAuditConstants.PROPERTY_ANCILLIARY_AUXILLIARY_VALUE, pAuxilliaryValue);
  }
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"           + lLineFeed);
    lStringBuffer.append ("           Auxilliary Record      "                            + lLineFeed);
    lStringBuffer.append ("Auxiliary ID .................... " + getId()                  + lLineFeed); 
    lStringBuffer.append ("Type ............................ " + getType()                + lLineFeed); 
    lStringBuffer.append ("Name ............................ " + getAuxilliaryName()      + lLineFeed); 
    lStringBuffer.append ("Value ........................... " + getAuxilliaryValue()     + lLineFeed); 
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }
  
}
