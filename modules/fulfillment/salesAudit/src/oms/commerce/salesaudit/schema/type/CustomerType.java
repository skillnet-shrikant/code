//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.09 at 08:22:26 AM EST 
//


package oms.commerce.salesaudit.schema.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="reference" type="{http://speedorder.speedfc.com/xml/types}varchar200"/>
 *         &lt;element name="bill_to" type="{http://speedorder.speedfc.com/xml/types}PersonType"/>
 *         &lt;element name="ship_to" type="{http://speedorder.speedfc.com/xml/types}PersonType"/>
 *         &lt;element name="tax_exemption_certificate" type="{http://speedorder.speedfc.com/xml/types}varchar100" minOccurs="0"/>
 *         &lt;element name="tax_exemption_name" type="{http://speedorder.speedfc.com/xml/types}varchar100" minOccurs="0"/>
 *         &lt;element name="tax_exemption_type" type="{http://speedorder.speedfc.com/xml/types}varchar20" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerType", propOrder = {

})
public class CustomerType {

    @XmlElement(required = true)
    protected String reference;
    @XmlElement(name = "bill_to", required = true)
    protected PersonType billTo;
    @XmlElement(name = "ship_to", required = true)
    protected PersonType shipTo;
    @XmlElement(name = "tax_exemption_certificate")
    protected String taxExemptionCertificate;
    @XmlElement(name = "tax_exemption_name")
    protected String taxExemptionName;
    @XmlElement(name = "tax_exemption_type")
    protected String taxExemptionType;

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the billTo property.
     * 
     * @return
     *     possible object is
     *     {@link PersonType }
     *     
     */
    public PersonType getBillTo() {
        return billTo;
    }

    /**
     * Sets the value of the billTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType }
     *     
     */
    public void setBillTo(PersonType value) {
        this.billTo = value;
    }

    /**
     * Gets the value of the shipTo property.
     * 
     * @return
     *     possible object is
     *     {@link PersonType }
     *     
     */
    public PersonType getShipTo() {
        return shipTo;
    }

    /**
     * Sets the value of the shipTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType }
     *     
     */
    public void setShipTo(PersonType value) {
        this.shipTo = value;
    }

    /**
     * Gets the value of the taxExemptionCertificate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxExemptionCertificate() {
        return taxExemptionCertificate;
    }

    /**
     * Sets the value of the taxExemptionCertificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxExemptionCertificate(String value) {
        this.taxExemptionCertificate = value;
    }

    /**
     * Gets the value of the taxExemptionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxExemptionName() {
        return taxExemptionName;
    }

    /**
     * Sets the value of the taxExemptionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxExemptionName(String value) {
        this.taxExemptionName = value;
    }

    /**
     * Gets the value of the taxExemptionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxExemptionType() {
        return taxExemptionType;
    }

    /**
     * Sets the value of the taxExemptionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxExemptionType(String value) {
        this.taxExemptionType = value;
    }

}
