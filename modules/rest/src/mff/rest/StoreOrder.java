package mff.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author vsingh
 *
 */
public class StoreOrder {
	
	private String orderId;
	private String orderNumber;
	private String shippingMethod;
	private boolean bopisOrder;
	private boolean inPicking;
	private List<StoreOrderItem> items = new ArrayList<StoreOrderItem>();
	private String firstName;
	private String lastName;
	private String address1;
	private String address2;
	private String city;
	private String shipState;
	private String postalCode;
	private String county;
	private String country;
	private String phoneNumber;
	private String contactEmail;
	private Date orderSubmittedDate;
	private String pickUpInstructions;
	private boolean fulfillmentSplit;
	private String orderAge;
	private boolean showStoreItemLocation;
	
	public String getOrderAge() {
		return orderAge;
	}

	public void setOrderAge(String pOrderAge) {
		orderAge = pOrderAge;
	}

	public String getOrderId() {
		return orderId;
	}
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public String getShippingMethod() {
		return shippingMethod;
	}
	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}
		
	public boolean isBopisOrder() {
		return bopisOrder;
	}
	
	public void setBopisOrder(boolean bopisOrder) {
		this.bopisOrder = bopisOrder;
	}
	
	public boolean isInPicking() {
		return inPicking;
	}
	
	public void setInPicking(boolean pInPicking) {
		inPicking = pInPicking;
	}
	
	public List<StoreOrderItem> getItems() {
		return items;
	}
	
	public void setItems(List<StoreOrderItem> items) {
		this.items = items;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
		
	public String getAddress1() {
		return address1;
	}
	
	public void setAddress1(String pAddress1) {
		address1 = pAddress1;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public void setAddress2(String pAddress2) {
		address2 = pAddress2;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String pCity) {
		city = pCity;
	}
	
	public String getShipState() {
		return shipState;
	}
	
	public void setShipState(String pShipState) {
		shipState = pShipState;
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(String pPostalCode) {
		postalCode = pPostalCode;
	}
	
	public String getCounty() {
		return county;
	}
	
	public void setCounty(String pCounty) {
		county = pCounty;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String pCountry) {
		country = pCountry;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String pPhoneNumber) {
		phoneNumber = pPhoneNumber;
	}
		
	public String getContactEmail() {
		return contactEmail;
	}
	
	public void setContactEmail(String pContactEmail) {
		contactEmail = pContactEmail;
	}
	
	public Date getOrderSubmittedDate() {
		return orderSubmittedDate;
	}
	
	public void setOrderSubmittedDate(Date pOrderSubmittedDate) {
		orderSubmittedDate = pOrderSubmittedDate;
	}
	
	public String getPickUpInstructions() {
		return pickUpInstructions;
	}
	
	public void setPickUpInstructions(String pPickUpInstructions) {
		pickUpInstructions = pPickUpInstructions;
	}
	
	public boolean isFulfillmentSplit() {
		return fulfillmentSplit;
	}
	
	public void setFulfillmentSplit(boolean pFulfillmentSplit) {
		fulfillmentSplit = pFulfillmentSplit;
	}
	
	public boolean isShowStoreItemLocation() {
		return showStoreItemLocation;
	}

	public void setShowStoreItemLocation(boolean pShowStoreItemLocation) {
		showStoreItemLocation = pShowStoreItemLocation;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	
}
