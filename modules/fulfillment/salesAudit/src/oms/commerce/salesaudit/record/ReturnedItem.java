package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class represents a returned item in teh invoice repository.  The returned item is a type of 
 * line item.  
 * 
 * @author jvose
 *
 */
public class ReturnedItem 
  extends LineItem {
  
  /** Default Constructor **/
  public ReturnedItem () {    
  }
  
  /** Other Constructor **/
  public ReturnedItem (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }

  /**  returnedItem - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_LINE_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_LINE_ID, pId);
  }
   
  /**  returnedItem - lineNumber   **/
  public Long getLineNumber() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_LINE_NUMBER);
  }
  public void  setLineNumber(Long pLineNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_LINE_NUMBER, pLineNumber);
  }
   
  /**  returnedItem - returnReason   **/
  public String getReturnReason() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RETURN_REASON);
  }
  public void  setReturnReason(String pReturnReason) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RETURN_REASON, pReturnReason);
  }
   
  /**  returnedItem - rmaNumber   **/
  public String getRmaNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RMA_NUMBER);
  }
  public void  setRmaNumber(String pRmaNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RMA_NUMBER, pRmaNumber);
  }
   
  /**  returnedItem - returnedAmount   **/
  public Double getReturnedAmount() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RETURNED_AMOUNT);
  }
  public void  setReturnedAmount(Double pReturnedAmount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RETURNED_AMOUNT, pReturnedAmount);
  }
   
  /**  returnedItem - restockLocalTax   **/
  public Double getRestockLocalTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_LOCAL_TAX);
  }
  public void  setRestockLocalTax(Double pRestockLocalTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_LOCAL_TAX, pRestockLocalTax);
  }
   
  /**  returnedItem - restockCountyTax   **/
  public Double getRestockCountyTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_COUNTY_TAX);
  }
  public void  setRestockCountyTax(Double pRestockCountyTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_COUNTY_TAX, pRestockCountyTax);
  }
   
  /**  returnedItem - restockStateTax   **/
  public Double getRestockStateTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_STATE_TAX);
  }
  public void  setRestockStateTax(Double pRestockStateTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_STATE_TAX, pRestockStateTax);
  }
   
  /**  returnedItem - restockTaxTotal   **/
  public Double getRestockTaxTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_TAX_TOTAL);
  }
  public void  setRestockTaxTotal(Double pRestockTaxTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_TAX_TOTAL, pRestockTaxTotal);
  }
   
  /**  returnedItem - restockShippingTax   **/
  public Double getRestockShippingTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_SHIPPING_TAX);
  }
  public void  setRestockShippingTax(Double pRestockShippingTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_SHIPPING_TAX, pRestockShippingTax);
  }
   
  /**  returnedItem - restockExtendedTotal   **/
  public Double getRestockExtendedTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_EXTENDED_TOTAL);
  }
  public void  setRestockExtendedTotal(Double pRestockExtendedTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_RETURNED_ITEM_RESTOCK_EXTENDED_TOTAL, pRestockExtendedTotal);
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
    lStringBuffer.append ("Return Reason ................... " + getReturnReason()            + lLineFeed);
    lStringBuffer.append ("RMA ............................. " + getRmaNumber()               + lLineFeed);
    lStringBuffer.append ("Returned Amount ................. " + getReturnedAmount()          + lLineFeed);
    lStringBuffer.append ("Re-stock County Tax.............. " + getRestockLocalTax()         + lLineFeed);
    lStringBuffer.append ("Re-stock Couty Tax .............. " + getRestockCountyTax()        + lLineFeed);
    lStringBuffer.append ("Re-stock State Tax .............. " + getRestockStateTax()         + lLineFeed);
    lStringBuffer.append ("Re-stock Tax Total .............. " + getRestockTaxTotal()         + lLineFeed);
    lStringBuffer.append ("Re-stock Shipping Tax ........... " + getRestockShippingTax()      + lLineFeed);
    lStringBuffer.append ("Re-stock Extended Total ......... " + getRestockExtendedTotal()    + lLineFeed);
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
