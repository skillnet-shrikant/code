package oms.commerce.salesaudit.record;

import java.util.List;
import java.util.Vector;
import atg.repository.MutableRepositoryItem;

/**
 * This class represents a line item in the invoice repository.  The line item can be either 
 * a returned or shipped line.
 * 
 * @author jvose
 *
 */
public class LineItem 
  extends SalesAuditItem {

  /** Default Constructor **/
  public LineItem () {    
  }
  
  /** Other Constructor **/
  public LineItem (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }
  

  /**  lineItem - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_ID, pId);
  }
   
  /**  lineItem - extractLineId   **/
  public Long getExtractLineId() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_EXTRACT_LINE_ID);
  }
  public void  setExtractLineId(Long pExtractLineId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_EXTRACT_LINE_ID, pExtractLineId);
  }
   
  /**  lineItem - clientLineId   **/
  public String getClientLineId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_CLIENT_LINE_ID);
  }
  public void  setClientLineId(String pClientLineId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_CLIENT_LINE_ID, pClientLineId);
  }
   
  /**  lineItem - skucode   **/
  public String getSkucode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_SKUCODE);
  }
  public void  setSkucode(String pSkucode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_SKUCODE, pSkucode);
  }
   
  /**  lineItem - barcode   **/
  public String getBarcode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_BARCODE);
  }
  public void  setBarcode(String pBarcode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_BARCODE, pBarcode);
  }
   
  /**  lineItem - itemNumber   **/
  public String getItemNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_ITEM_NUMBER);
  }
  public void  setItemNumber(String pItemNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_ITEM_NUMBER, pItemNumber);
  }
   
  /**  lineItem - colorCode   **/
  public String getColorCode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_COLOR_CODE);
  }
  public void  setColorCode(String pColorCode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_COLOR_CODE, pColorCode);
  }
   
  /**  lineItem - sizeCode   **/
  public String getSizeCode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_SIZE_CODE);
  }
  public void  setSizeCode(String pSizeCode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_SIZE_CODE, pSizeCode);
  }
   
  /**  lineItem - quantity   **/
  public Long getQuantity() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_QUANTITY);
  }
  public void  setQuantity(Long pQuantity) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_QUANTITY, pQuantity);
  }
   
  /**  lineItem - unitPrice   **/
  public Double getUnitPrice() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_UNIT_PRICE);
  }
  public void  setUnitPrice(Double pUnitPrice) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_UNIT_PRICE, pUnitPrice);
  }
   
  /**  lineItem - facilityCd   **/
  public String getFacilityCd() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_FACILITY_CD);
  }
  public void  setFacilityCd(String pFacilityCd) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_FACILITY_CD, pFacilityCd);
  }
   
  /**  lineItem - shippingAmount   **/
  public Double getShippingAmount() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_SHIPPING_AMOUNT);
  }
  public void  setShippingAmount(Double pShippingAmount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_SHIPPING_AMOUNT, pShippingAmount);
  }
   
  /**  lineItem - lineLocalTax   **/
  public Double getLineLocalTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_LOCAL_TAX);
  }
  public void  setLineLocalTax(Double pLineLocalTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_LOCAL_TAX, pLineLocalTax);
  }
   
  /**  lineItem - lineCountyTax   **/
  public Double getLineCountyTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_COUNTY_TAX);
  }
  public void  setLineCountyTax(Double pLineCountyTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_COUNTY_TAX, pLineCountyTax);
  }
   
  /**  lineItem - lineStateTax   **/
  public Double getLineStateTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_STATE_TAX);
  }
  public void  setLineStateTax(Double pLineStateTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_STATE_TAX, pLineStateTax);
  }
   
  /**  lineItem - lineTaxTotal   **/
  public Double getLineTaxTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_TAX_TOTAL);
  }
  public void  setLineTaxTotal(Double pLineTaxTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_TAX_TOTAL, pLineTaxTotal);
  }
   
  /**  lineItem - lineShippingTax   **/
  public Double getLineShippingTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_SHIPPING_TAX);
  }
  public void  setLineShippingTax(Double pLineShippingTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_SHIPPING_TAX, pLineShippingTax);
  }
   
  /**  lineItem - lineExtendedTotal   **/
  public Double getLineExtendedTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_EXTENDED_TOTAL);
  }
  public void  setLineExtendedTotal(Double pLineExtendedTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_EXTENDED_TOTAL, pLineExtendedTotal);
  }
   
  /**  lineItem - extendedPrice   **/
  public Double getExtendedPrice() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_EXTENDED_PRICE);
  }
  public void  setExtendedPrice(Double pExtendedPrice) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_EXTENDED_PRICE, pExtendedPrice);
  }
   
  /**  lineItem - lineNumber   **/
  public Long getLineNumber() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_NUMBER);
  }
  public void  setLineNumber(Long pLineNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_NUMBER, pLineNumber);
  }
   
  /**  lineItem - returnReason   **/
  public String getReturnReason() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RETURN_REASON);
  }
  public void  setReturnReason(String pReturnReason) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RETURN_REASON, pReturnReason);
  }
   
  /**  lineItem - rmaNumber   **/
  public String getRmaNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RMA_NUMBER);
  }
  public void  setRmaNumber(String pRmaNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RMA_NUMBER, pRmaNumber);
  }
   
  /**  lineItem - returnedAmount   **/
  public Double getReturnedAmount() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RETURNED_AMOUNT);
  }
  public void  setReturnedAmount(Double pReturnedAmount) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RETURNED_AMOUNT, pReturnedAmount);
  }
   
  /**  lineItem - restockLocalTax   **/
  public Double getRestockLocalTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_LOCAL_TAX);
  }
  public void  setRestockLocalTax(Double pRestockLocalTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_LOCAL_TAX, pRestockLocalTax);
  }
   
  /**  lineItem - restockCountyTax   **/
  public Double getRestockCountyTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_COUNTY_TAX);
  }
  public void  setRestockCountyTax(Double pRestockCountyTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_COUNTY_TAX, pRestockCountyTax);
  }
   
  /**  lineItem - restockStateTax   **/
  public Double getRestockStateTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_STATE_TAX);
  }
  public void  setRestockStateTax(Double pRestockStateTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_STATE_TAX, pRestockStateTax);
  }
   
  /**  lineItem - restockTaxTotal   **/
  public Double getRestockTaxTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_TAX_TOTAL);
  }
  public void  setRestockTaxTotal(Double pRestockTaxTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_TAX_TOTAL, pRestockTaxTotal);
  }
   
  /**  lineItem - restockShippingTax   **/
  public Double getRestockShippingTax() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_SHIPPING_TAX);
  }
  public void  setRestockShippingTax(Double pRestockShippingTax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_SHIPPING_TAX, pRestockShippingTax);
  }
   
  /**  lineItem - restockExtendedTotal   **/
  public Double getRestockExtendedTotal() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_EXTENDED_TOTAL);
  }
  public void  setRestockExtendedTotal(Double pRestockExtendedTotal) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_RESTOCK_EXTENDED_TOTAL, pRestockExtendedTotal);
  }

  /**  lineItem - Line Discounts    **/
  @SuppressWarnings("unchecked")
  public List<LineDiscount> getLineDiscounts () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_DISCOUNTS);
    List <LineDiscount> lLineDiscounts = new Vector <LineDiscount> ();    
    for (Object lItem : lItems) {
      LineDiscount lLineDiscount = new LineDiscount ((MutableRepositoryItem)lItem);
      lLineDiscounts.add(lLineDiscount);
    }
    return lLineDiscounts;
  }
  public void  setLineDiscounts (List<LineDiscount> pLineDiscounts) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    
    if (pLineDiscounts != null) { 
      for (LineDiscount lLineDiscount : pLineDiscounts) {
        lItems.add(lLineDiscount.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_DISCOUNTS, lItems);
  }    
  
  /**  lineItem - Line Cartons    **/
  @SuppressWarnings("unchecked")
  public List<LineCarton> getLineCartons () {
    List<Object> lItems = (List<Object>) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_CARTONS);
    List <LineCarton> lLineCartons = new Vector <LineCarton> ();    
    for (Object lItem : lItems) {
      LineCarton lLineCarton = new LineCarton ((MutableRepositoryItem)lItem);
      lLineCartons.add(lLineCarton);
    }
    return lLineCartons;
  }
  public void  setLineCartons (List<LineCarton> pLineCartons) {
    List<MutableRepositoryItem> lItems = new Vector <MutableRepositoryItem> ();
    if (pLineCartons != null) {
      for (LineCarton lLineCarton : pLineCartons) {
        lItems.add(lLineCarton.getRepositoryItem());
      }
    }
    setPropertyValue (SalesAuditConstants.PROPERTY_LINE_ITEM_LINE_CARTONS, lItems);
  }    
 
  /**  lineItem - Auxiliaries    **/
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
    
    if (pAuxiliaries != null) {
      for (AuxiliaryRecord lAuxiliaryRecord : pAuxiliaries) {
        lItems.add(lAuxiliaryRecord.getRepositoryItem());
      }
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
