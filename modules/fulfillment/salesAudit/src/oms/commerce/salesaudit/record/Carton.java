package oms.commerce.salesaudit.record;

import java.sql.Timestamp;
import atg.repository.MutableRepositoryItem;

/**
 * This class will represent a shipment carton in the invoice repository.  The carton records 
 * shipment information about a shipped item.
 * 
 * @author jvose
 *
 */
public class Carton 
  extends SalesAuditItem {

  /** Default Constructor **/
  public Carton () {    
  }
  
  /** Other Constructor **/
  public Carton (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }
  
  /**  carton - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_ID, pId);
  }
   
  /**  carton - trackingNumber   **/
  public String getTrackingNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_TRACKING_NUMBER);
  }
  public void  setTrackingNumber(String pTrackingNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_TRACKING_NUMBER, pTrackingNumber);
  }
   
  /**  carton - cartonNumber   **/
  public String getCartonNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_NUMBER);
  }
  public void  setCartonNumber(String pCartonNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_NUMBER, pCartonNumber);
  }
   
  /**  carton - shipDate   **/
  public Timestamp getShipDate() {
     return (Timestamp) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_SHIP_DATE);
  }
  public void  setShipDate(Timestamp pShipDate) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_SHIP_DATE, pShipDate);
  }
   
  /**  carton - shipVia   **/
  public String getShipVia() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_SHIP_VIA);
  }
  public void  setShipVia(String pShipVia) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_SHIP_VIA, pShipVia);
  }
   
  /**  carton - facilityCd   **/
  public String getFacilityCd() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_FACILITY_CD);
  }
  public void  setFacilityCd(String pFacilityCd) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_FACILITY_CD, pFacilityCd);
  }
   
  /**  carton - weight   **/
  public Double getWeight() {
     return (Double) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_WEIGHT);
  }
  public void  setWeight(Double pWeight) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_WEIGHT, pWeight);
  }
   
  /**  carton - cartonSize   **/
  public String getCartonSize() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_SIZE);
  }
  public void  setCartonSize(String pCartonSize) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_SIZE, pCartonSize);
  }
   
  /**  carton - cartonType   **/
  public String getCartonType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_TYPE);
  }
  public void  setCartonType(String pCartonType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_CARTON_TYPE, pCartonType);
  }
   
  /**  carton - billOfLading   **/
  public String getBillOfLading() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_BILL_OF_LADING);
  }
  public void  setBillOfLading(String pBillOfLading) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_BILL_OF_LADING, pBillOfLading);
  }
   
  /**  carton - proNum   **/
  public String getProNum() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_PRO_NUM);
  }
  public void  setProNum(String pProNum) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_PRO_NUM, pProNum);
  }
   
  /**  carton - manifestNumber   **/
  public String getManifestNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_MANIFEST_NUMBER);
  }
  public void  setManifestNumber(String pManifestNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_MANIFEST_NUMBER, pManifestNumber);
  }
   
  /**  carton - pickTicket   **/
  public String getPickTicket() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_PICK_TICKET);
  }
  public void  setPickTicket(String pPickTicket) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_PICK_TICKET, pPickTicket);
  }
   
  /**  carton - returnLabelNumber   **/
  public String getReturnLabelNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_RETURN_LABEL_NUMBER);
  }
  public void  setReturnLabelNumber(String pReturnLabelNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_RETURN_LABEL_NUMBER, pReturnLabelNumber);
  }
   
  /**  carton - deliverConfirmationNumber   **/
  public String getDeliverConfirmationNumber() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_CARTON_DELIVER_CONFIRMATION_NUMBER);
  }
  public void  setDeliverConfirmationNumber(String pDeliverConfirmationNumber) {
     setPropertyValue (SalesAuditConstants.PROPERTY_CARTON_DELIVER_CONFIRMATION_NUMBER, pDeliverConfirmationNumber);
  }
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"                 + lLineFeed);
    lStringBuffer.append ("             Carton Record        "                                  + lLineFeed);
    lStringBuffer.append ("Carton ID ....................... " + getId()                        + lLineFeed);
    lStringBuffer.append ("Tracking Number ................. " + getTrackingNumber()            + lLineFeed); 
    lStringBuffer.append ("Carton Number ................... " + getCartonNumber()              + lLineFeed); 
    lStringBuffer.append ("Ship Date ....................... " + getShipDate()                  + lLineFeed);
    lStringBuffer.append ("Ship Via ........................ " + getShipVia()                   + lLineFeed);
    lStringBuffer.append ("Facility Code ................... " + getFacilityCd()                + lLineFeed);
    lStringBuffer.append ("Weight .......................... " + getWeight()                    + lLineFeed);
    lStringBuffer.append ("Carton Size ..................... " + getCartonSize()                + lLineFeed);
    lStringBuffer.append ("Carton Type ..................... " + getCartonType()                + lLineFeed);
    lStringBuffer.append ("Bill of Lading .................. " + getBillOfLading()              + lLineFeed);
    lStringBuffer.append ("Pro Num ......................... " + getProNum()                    + lLineFeed);
    lStringBuffer.append ("Manifest Number ................. " + getManifestNumber()            + lLineFeed);
    lStringBuffer.append ("Pick Ticket ..................... " + getPickTicket()                + lLineFeed);
    lStringBuffer.append ("Return Label Number ............. " + getReturnLabelNumber()         + lLineFeed);
    lStringBuffer.append ("Delivery Confirmation Number .... " + getDeliverConfirmationNumber() + lLineFeed);
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }
  

}
