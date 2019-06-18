package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/**
 * This class represents a carton at the line level in the invoice repository.  The line carton 
 * is the shipment information for a given shipment line.
 * 
 * @author jvose
 *
 */
public class LineCarton 
  extends SalesAuditItem {

  /** Default Constructor **/
  public LineCarton () {    
  }
  
  /** Other Constructor **/
  public LineCarton (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }
  
  /**  lineCarton - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_LINE_CARTON_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_LINE_CARTON_ID, pId);
  }
   
  /**  lineCarton - trackingNumber   **/
  public String getTrackingNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_TRACKING_NUMBER);
  }
  public void  setTrackingNumber(String pTrackingNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_TRACKING_NUMBER, pTrackingNumber);
  }
   
  /**  lineCarton - shipVia   **/
  public String getShipVia() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_SHIP_VIA);
  }
  public void  setShipVia(String pShipVia) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_SHIP_VIA, pShipVia);
  }
   
  /**  lineCarton - quantity   **/
  public Long getQuantity() {
     return (Long) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_QUANTITY);
  }
  public void  setQuantity(Long pQuantity) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_QUANTITY, pQuantity);
  }
   
  /**  lineCarton - deliverConfirmationNumber   **/
  public String getDeliverConfirmationNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_DELIVER_CONFIRMATION_NUMBER);
  }
  public void  setDeliverConfirmationNumber(String pDeliverConfirmationNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_DELIVER_CONFIRMATION_NUMBER, pDeliverConfirmationNumber);
  }
   
  /**  lineCarton - serialNumber   **/
  public String getSerialNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_SERIAL_NUMBER);
  }
  public void  setSerialNumber(String pSerialNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_SERIAL_NUMBER, pSerialNumber);
  }
   
  /**  lineCarton - cartonNumber   **/
  public String getCartonNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_CARTON_NUMBER);
  }
  public void  setCartonNumber(String pCartonNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_LINE_CARTON_CARTON_NUMBER, pCartonNumber);
  }

  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"                 + lLineFeed);
    lStringBuffer.append ("             Line Carton Record        "                             + lLineFeed);
    lStringBuffer.append ("Carton ID ....................... " + getId()                        + lLineFeed);
    lStringBuffer.append ("Tracking Number ................. " + getTrackingNumber()            + lLineFeed); 
    lStringBuffer.append ("Ship Via ........................ " + getShipVia()                   + lLineFeed); 
    lStringBuffer.append ("Quantity ........................ " + getQuantity()                  + lLineFeed);
    lStringBuffer.append ("Delviery Confirmation Number .... " + getDeliverConfirmationNumber() + lLineFeed);
    lStringBuffer.append ("Carton Number ................... " + getCartonNumber()              + lLineFeed);
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }    
  
}
