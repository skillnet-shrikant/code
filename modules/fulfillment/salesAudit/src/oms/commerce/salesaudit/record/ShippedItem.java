package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class represents a shipped item in the invoice repository.
 * 
 * @author jvose
 *
 */
public class ShippedItem 
  extends LineItem {
  
  /** Default Constructor **/
  public ShippedItem () {    
  }
  
  /** Other Constructor **/
  public ShippedItem (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }

  /**  shippedItem - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_SHIPPED_ITEM_LINE_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_SHIPPED_ITEM_LINE_ID, pId);
  }
   
  /**  shippedItem - lineNumber   **/
  public Long getLineNumber() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_SHIPPED_ITEM_LINE_NUMBER);
  }
  public void  setLineNumber(Long pLineNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_SHIPPED_ITEM_LINE_NUMBER, pLineNumber);
  }
  
  /**  shippedItem - giftCardNumber   **/
  public String getGiftCardNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_SHIPPED_ITEM_GIFTCARD_NUMBER);
  }
  public void  setGiftCardNumber(String pSerialNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_SHIPPED_ITEM_GIFTCARD_NUMBER, pSerialNumber);
  }
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"               + lLineFeed);
    lStringBuffer.append ("             Line Item Record     "                                + lLineFeed);
    lStringBuffer.append ("Line Discount ID ................ " + getId()                      + lLineFeed);
    lStringBuffer.append ("Extract Line ID ................. " + getExtractLineId()           + lLineFeed);
    lStringBuffer.append ("Client Line ID .................. " + getClientLineId()            + lLineFeed); 
    lStringBuffer.append ("SKU Code ........................ " + getSkucode()                 + lLineFeed);
    lStringBuffer.append ("Bar Code ........................ " + getBarcode()                 + lLineFeed);
    lStringBuffer.append ("Item Number ..................... " + getItemNumber()              + lLineFeed);
    lStringBuffer.append ("Color Code ...................... " + getColorCode()               + lLineFeed);
    lStringBuffer.append ("Size Code ....................... " + getSizeCode()                + lLineFeed);
    lStringBuffer.append ("Quantity ........................ " + getQuantity()                + lLineFeed);
    lStringBuffer.append ("Unit Price ...................... " + getUnitPrice()               + lLineFeed);
    lStringBuffer.append ("Facility Code ................... " + getFacilityCd()              + lLineFeed);
    lStringBuffer.append ("Shipping Amount ................. " + getShippingAmount()          + lLineFeed);
    lStringBuffer.append ("Local Tax ....................... " + getLineLocalTax()            + lLineFeed);
    lStringBuffer.append ("County Tax ...................... " + getLineCountyTax()           + lLineFeed);
    lStringBuffer.append ("State Tax ....................... " + getLineStateTax()            + lLineFeed);
    lStringBuffer.append ("Tax Total ....................... " + getLineTaxTotal()            + lLineFeed);
    lStringBuffer.append ("Shipping Tax .................... " + getLineShippingTax()         + lLineFeed);
    lStringBuffer.append ("Extended Total .................. " + getLineExtendedTotal()       + lLineFeed);
    lStringBuffer.append ("Extended Price .................. " + getExtendedPrice()           + lLineFeed);
    lStringBuffer.append ("Line Number ..................... " + getLineNumber()              + lLineFeed);
    for (LineDiscount lLineDiscount : getLineDiscounts()) {
      lStringBuffer.append (lLineDiscount.toString()                                          + lLineFeed);
    }
    for (LineCarton lLineCarton : getLineCartons()) {
      lStringBuffer.append (lLineCarton.toString()                                            + lLineFeed);
    }
    for (AuxiliaryRecord lAuxiliaryRecord : getAuxiliaries()) {
      lStringBuffer.append (lAuxiliaryRecord.toString()                                       + lLineFeed);
    }
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }       


}
