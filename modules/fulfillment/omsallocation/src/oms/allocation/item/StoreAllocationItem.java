package oms.allocation.item;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class StoreAllocationItem {
	
	private String orderId;
	private String orderNumber;
	private String shippingMethod;
	private String state;
	private String commerceItemId;
	private String storeId;
	private String skuId;
	private long quantity;
	private Date allocationDate;
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
	private boolean bopisOrder;
	private boolean inPicking;
	private Date orderSubmittedDate;
	private String pickUpInstructions;
	
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
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCommerceItemId() {
		return commerceItemId;
	}
	public void setCommerceItemId(String commerceItemId) {
		this.commerceItemId = commerceItemId;
	}
	
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public Date getAllocationDate() {
		return allocationDate;
	}
	public void setAllocationDate(Date allocationDate) {
		this.allocationDate = allocationDate;
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
  
  @Override
  public String toString() {
      return ToStringBuilder.reflectionToString(this);
  }
	
	

}
