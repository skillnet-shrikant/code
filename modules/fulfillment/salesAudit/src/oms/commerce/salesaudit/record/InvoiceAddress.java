package oms.commerce.salesaudit.record;

import atg.repository.MutableRepositoryItem;

/** 
 * This class represents a billing/shipping address in the invoice repository.  
 * 
 * @author jvose
 *
 */
public class InvoiceAddress 
  extends SalesAuditItem {

  /** Default Constructor **/
  public InvoiceAddress () {    
  }
  
  /** Other Constructor **/
  public InvoiceAddress (MutableRepositoryItem pItem) {
    this.setRepositoryItem(pItem);
  }  
  
  /**  invoiceAddress - id   **/
  public String getId() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_ID);
  }
  public void  setId(String pId) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_ID, pId);
  }
   
  /**  invoiceAddress - type   **/
  public String getType() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_TYPE);
  }
  public void  setType(String pType) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_TYPE, pType);
  }
   
  /**  invoiceAddress - firstName   **/
  public String getFirstName() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_FIRST_NAME);
  }
  public void  setFirstName(String pFirstName) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_FIRST_NAME, pFirstName);
  }
   
  /**  invoiceAddress - middleName   **/
  public String getMiddleName() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_MIDDLE_NAME);
  }
  public void  setMiddleName(String pMiddleName) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_MIDDLE_NAME, pMiddleName);
  }
   
  /**  invoiceAddress - lastName   **/
  public String getLastName() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_LAST_NAME);
  }
  public void  setLastName(String pLastName) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_LAST_NAME, pLastName);
  }
   
  /**  invoiceAddress - organization   **/
  public String getOrganization() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ORGANIZATION);
  }
  public void  setOrganization(String pOrganization) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ORGANIZATION, pOrganization);
  }
   
  /**  invoiceAddress - companyName   **/
  public String getCompanyName() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_COMPANY_NAME);
  }
  public void  setCompanyName(String pCompanyName) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_COMPANY_NAME, pCompanyName);
  }
   
  /**  invoiceAddress - homePhone   **/
  public String getHomePhone() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_HOME_PHONE);
  }
  public void  setHomePhone(String pHomePhone) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_HOME_PHONE, pHomePhone);
  }
   
  /**  invoiceAddress - workPhone   **/
  public String getWorkPhone() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_WORK_PHONE);
  }
  public void  setWorkPhone(String pWorkPhone) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_WORK_PHONE, pWorkPhone);
  }
   
  /**  invoiceAddress - mobilePhone   **/
  public String getMobilePhone() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_MOBILE_PHONE);
  }
  public void  setMobilePhone(String pMobilePhone) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_MOBILE_PHONE, pMobilePhone);
  }
   
  /**  invoiceAddress - fax   **/
  public String getFax() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_FAX);
  }
  public void  setFax(String pFax) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_FAX, pFax);
  }
   
  /**  invoiceAddress - email   **/
  public String getEmail() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_EMAIL);
  }
  public void  setEmail(String pEmail) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_EMAIL, pEmail);
  }
   
  /**  invoiceAddress - address1   **/
  public String getAddress1() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_1);
  }
  public void  setAddress1(String pAddress1) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_1, pAddress1);
  }
   
  /**  invoiceAddress - address2   **/
  public String getAddress2() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_2);
  }
  public void  setAddress2(String pAddress2) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_2, pAddress2);
  }
   
  /**  invoiceAddress - address3   **/
  public String getAddress3() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_3);
  }
  public void  setAddress3(String pAddress3) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_3, pAddress3);
  }
   
  /**  invoiceAddress - address4   **/
  public String getAddress4() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_4);
  }
  public void  setAddress4(String pAddress4) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_ADDRESS_4, pAddress4);
  }
   
  /**  invoiceAddress - city   **/
  public String getcity() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_CITY);
  }
  public void  setcity(String pcity) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_CITY, pcity);
  }
   
  /**  invoiceAddress - provinceCode   **/
  public String getprovinceCode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_PROVINCE_CODE);
  }
  public void  setprovinceCode(String pprovinceCode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_PROVINCE_CODE, pprovinceCode);
  }
   
  /**  invoiceAddress - province   **/
  public String getProvince() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_PROVINCE);
  }
  public void  setProvince(String pProvince) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_PROVINCE, pProvince);
  }
   
  /**  invoiceAddress - postalCode   **/
  public String getPostalCode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_POSTAL_CODE);
  }
  public void  setPostalCode(String pPostalCode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_POSTAL_CODE, pPostalCode);
  }
   
  /**  invoiceAddress - countryCode   **/
  public String getCountryCode() {
     return (String) getPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_COUNTRY_CODE);
  }
  public void  setCountryCode(String pCountryCode) {
     setPropertyValue (SalesAuditConstants.PROPERTY_INVOICE_ADDRESS_COUNTRY_CODE, pCountryCode);
  }
  
  
  /**
   * Returns a string representation of the object.
   */
  public String toString () {
    String lLineFeed = System.getProperty("line.separator");
    StringBuffer lStringBuffer = new StringBuffer();
    lStringBuffer.append (lLineFeed);
    lStringBuffer.append ("***************************************************"                 + lLineFeed);
    lStringBuffer.append ("             Invoice Address Record        "                         + lLineFeed);
    lStringBuffer.append ("Address ID ...................... " + getId()                        + lLineFeed);
    lStringBuffer.append ("Type ............................ " + getType()                      + lLineFeed); 
    lStringBuffer.append ("First Name ...................... " + getFirstName()                 + lLineFeed); 
    lStringBuffer.append ("Middle Name ..................... " + getMiddleName()                + lLineFeed);
    lStringBuffer.append ("last Name ....................... " + getLastName()                  + lLineFeed);
    lStringBuffer.append ("Organization .................... " + getOrganization()              + lLineFeed);
    lStringBuffer.append ("Company Name .................... " + getCompanyName()               + lLineFeed);
    lStringBuffer.append ("Home Phone ...................... " + getHomePhone()                 + lLineFeed);
    lStringBuffer.append ("Work Phone ...................... " + getWorkPhone()                 + lLineFeed);
    lStringBuffer.append ("Mobile Phone .................... " + getMobilePhone()               + lLineFeed);
    lStringBuffer.append ("Fax ............................. " + getFax()                       + lLineFeed);
    lStringBuffer.append ("Email ........................... " + getEmail()                     + lLineFeed);
    lStringBuffer.append ("Address 1 ....................... " + getAddress1()                  + lLineFeed);
    lStringBuffer.append ("Address 2 ....................... " + getAddress2()                  + lLineFeed);
    lStringBuffer.append ("Address 3 ....................... " + getAddress3()                  + lLineFeed);
    lStringBuffer.append ("Address 4 ....................... " + getAddress4()                  + lLineFeed);
    lStringBuffer.append ("City ............................ " + getcity()                      + lLineFeed);
    lStringBuffer.append ("Province Code ................... " + getprovinceCode()              + lLineFeed);
    lStringBuffer.append ("Province ........................ " + getProvince()                  + lLineFeed);
    lStringBuffer.append ("Postal Code ..................... " + getPostalCode()                + lLineFeed);
    lStringBuffer.append ("Country Code .................... " + getCountryCode()               + lLineFeed);
    lStringBuffer.append ("***************************************************");
    return lStringBuffer.toString();
  }  
  
}
