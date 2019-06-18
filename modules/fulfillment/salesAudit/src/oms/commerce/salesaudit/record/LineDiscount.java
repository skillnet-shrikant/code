package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class represents a line discount in the invoice repository.  This is any discount that is 
 * at the line level, or prorated to the line.
 * 
 * @author jvose
 *
 */
public class LineDiscount 
  extends SalesAuditItem {

  /** Default Constructor **/
  public LineDiscount () {    
  }
  
  /** Other Constructor **/
  public LineDiscount (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }
  
  /**  lineDiscount - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_LINE_DISCOUNT_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_LINE_DISCOUNT_ID, pId);
  }
   
  /**  lineDiscount - discountType   **/
  public String getDiscountType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_DISCOUNT_TYPE);
  }
  public void  setDiscountType(String pDiscountType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_DISCOUNT_TYPE, pDiscountType);
  }
   
  /**  lineDiscount - discountCode   **/
  public String getDiscountCode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_DISCOUNT_CODE);
  }
  public void  setDiscountCode(String pDiscountCode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_DISCOUNT_CODE, pDiscountCode);
  }
   
  /**  lineDiscount - source   **/
  public String getSource() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_SOURCE);
  }
  public void  setSource(String pSource) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_SOURCE, pSource);
  }
   
  /**  lineDiscount - amount   **/
  public Double getAmount() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_AMOUNT);
  }
  public void  setAmount(Double pAmount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_DISCOUNT_AMOUNT, pAmount);
  }  

  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"       + lLineFeed);
    lStringBuffer.append ("             Line Discount Record      "                   + lLineFeed);
    lStringBuffer.append ("Line Discount ID ................ " + getId()              + lLineFeed);
    lStringBuffer.append ("Discount Type ................... " + getDiscountType()    + lLineFeed);
    lStringBuffer.append ("Discount Code ................... " + getDiscountCode()    + lLineFeed); 
    lStringBuffer.append ("Source .......................... " + getSource()          + lLineFeed);
    lStringBuffer.append ("Amount .......................... " + getAmount()          + lLineFeed);
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }      
  
}
