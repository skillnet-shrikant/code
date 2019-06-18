/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.item;

import java.util.Date;

import atg.repository.MutableRepositoryItem;

public class StoreAllocation 
	extends AllocationItem {

	/** Default Constructor **/
	public StoreAllocation () {
	}
	
	/** Other Constructor **/
	public StoreAllocation (MutableRepositoryItem pItem) {
		this.setRepositoryItem(pItem);
	}

	/** Order Id **/
	public String getOrderId() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_ORDER_ID);
	}
	public void setOrderId(String pOrderId) {
		setPropertyValue (AllocationConstants.PROPERTY_ORDER_ID, pOrderId);
	}

	/** Order Number **/
	public String getOrderNumber() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_ORDER_NUMBER);
	}
	public void setOrderNumber(String pOrderNumber) {
		setPropertyValue (AllocationConstants.PROPERTY_ORDER_NUMBER, pOrderNumber);
	}
	
	/** Shipping Method */
	public String getShippingMethod() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_SHIPPING_METHOD);
	}

	public void setShippingMethod(String pShippingMethod) {
		setPropertyValue (AllocationConstants.PROPERTY_SHIPPING_METHOD, pShippingMethod);
	}
	
	/** ShipTo FirstName */
	public String getFirstName() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_FIRST_NAME);
	}

	public void setFirstName(String pFirstName) {
		setPropertyValue (AllocationConstants.PROPERTY_FIRST_NAME, pFirstName);
	}
	
	/** ShipTo LastName */
	public String getLastName() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_LAST_NAME);
	}

	public void setLastName(String pLastName) {
		setPropertyValue (AllocationConstants.PROPERTY_LAST_NAME, pLastName);
	}
	
	/** CommerceItemId **/
	public String getCommerceItemId() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_COMMERCE_ITEM_ID);
	}
	public void setCommerceItemId(String pCommerceItemId) {
		setPropertyValue (AllocationConstants.PROPERTY_COMMERCE_ITEM_ID, pCommerceItemId);
	}

	/** Store Id **/
	public String getStoreId() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_STORE_ID);
	}
	public void setStoreId(String pStoreId) {
		setPropertyValue (AllocationConstants.PROPERTY_STORE_ID, pStoreId);
	}
	
	/** UPC **/
	public String getSkuId() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_SKU_ID);
	}
	public void setSkuId(String pSkuId) {
		setPropertyValue (AllocationConstants.PROPERTY_SKU_ID, pSkuId);
	}
	
	/** Quantity **/
	public long getQuantity() {
		return (Long) getPropertyValue (AllocationConstants.PROPERTY_QUANTITY);
	}
	public void setQuantity(long pQuantity) {
		setPropertyValue (AllocationConstants.PROPERTY_QUANTITY, pQuantity);
	}
	
	/** Order Date **/
	public Date getOrderDate() {
		return (Date) getPropertyValue (AllocationConstants.PROPERTY_ORDER_DATE);
	}
	public void setOrderDate(Date pOrderDate) {
		setPropertyValue (AllocationConstants.PROPERTY_ORDER_DATE, pOrderDate);
	}

	/** Allocation Date **/
	public Date getAllocationDate() {
		return (Date) getPropertyValue (AllocationConstants.PROPERTY_ALLOCATION_DATE);
	}
	public void setAllocationDate(Date pAllocationDate) {
		setPropertyValue (AllocationConstants.PROPERTY_ALLOCATION_DATE, pAllocationDate);
	}

	/** Shipped Date **/
	public Date getShippedDate() {
		return (Date) getPropertyValue (AllocationConstants.PROPERTY_SHIPPED_DATE);
	}
	public void setShippedDate(Date pShippedDate) {
		setPropertyValue (AllocationConstants.PROPERTY_SHIPPED_DATE, pShippedDate);
	}

	/** Decline Date **/
	public Date getDeclineDate() {
		return (Date) getPropertyValue (AllocationConstants.PROPERTY_DECLINE_DATE);
	}
	public void setDeclineDate(Date pDeclineDate) {
		setPropertyValue (AllocationConstants.PROPERTY_DECLINE_DATE, pDeclineDate);
	}
	
	/** State **/
	public String getState() {
		return (String) getPropertyValue (AllocationConstants.PROPERTY_STATE);
	}
	public void setState(String pState) {
		setPropertyValue (AllocationConstants.PROPERTY_STATE, pState);
	}
	
	/** State Detail **/
	public Date getStateDetail() {
		return (Date) getPropertyValue (AllocationConstants.PROPERTY_STATE_DETAIL);
	}
	public void setStateDetail(String pStateDetail) {
		setPropertyValue (AllocationConstants.PROPERTY_STATE_DETAIL, pStateDetail);
	}
	
	/** Bopis Order Flag*/	
	public boolean isBopisOrder() {
	  return (Boolean) getPropertyValue(AllocationConstants.PROPERTY_BOPIS_ORDER);
  }

  public void setBopisOrder(boolean pBopisOrder) {
    setPropertyValue(AllocationConstants.PROPERTY_BOPIS_ORDER, pBopisOrder);
  }
  
  /** In Picking Flag */
  public boolean isInPicking() {
    return (Boolean) getPropertyValue(AllocationConstants.PROPERTY_IN_PICKING);
  }

  public void setInPicking(boolean pInPicking) {
    setPropertyValue(AllocationConstants.PROPERTY_IN_PICKING, pInPicking);
  }
  
  public String getAddress1() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_ADDRESS1);
  }
  
  public void setAddress1(String pAddress1) {
    setPropertyValue(AllocationConstants.PROPERTY_ADDRESS1, pAddress1);
  }
  
  public String getAddress2() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_ADDRESS2);
  }
  
  public void setAddress2(String pAddress2) {
    setPropertyValue(AllocationConstants.PROPERTY_ADDRESS2, pAddress2);
  }
  
  public String getCity() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_CITY);
  }
  
  public void setCity(String pCity) {
    setPropertyValue(AllocationConstants.PROPERTY_CITY, pCity);
  }
  
  public String getShipState() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_SHIP_STATE);
  }

  public void setShipState(String pShipState) {
    setPropertyValue(AllocationConstants.PROPERTY_SHIP_STATE, pShipState);
  }

  public String getCounty() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_COUNTY);
  }
  
  public void setCounty(String pCounty) {
    setPropertyValue(AllocationConstants.PROPERTY_COUNTY, pCounty);
  }
  
  public String getPostalCode() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_POSTAL_CODE);
  }
  
  public void setPostalCode(String pPostalCode) {
    setPropertyValue(AllocationConstants.PROPERTY_POSTAL_CODE, pPostalCode);
  }
  
  public String getCountry() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_COUNTRY);
  }
  
  public void setCountry(String pCountry) {
    setPropertyValue(AllocationConstants.PROPERTY_COUNTRY, pCountry);
  }
  
  public String getPhoneNumber() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_PHONE_NUMBER);
  }
  
  public void setPhoneNumber(String pPhoneNumber) {
    setPropertyValue(AllocationConstants.PROPERTY_PHONE_NUMBER, pPhoneNumber);
  }
  
  public String getContactEmail() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_CONTACT_EMAIL);
  }
  
  public void setContactEmail(String pContactEmail) {
    setPropertyValue(AllocationConstants.PROPERTY_CONTACT_EMAIL, pContactEmail);
  }
  
  public String getPickUpInstructions() {
    return (String) getPropertyValue (AllocationConstants.PROPERTY_PICK_UP_INTRUCTIONS);
  }

  public void setPickUpInstructions(String pPickUpInstructions) {
    setPropertyValue(AllocationConstants.PROPERTY_PICK_UP_INTRUCTIONS, pPickUpInstructions);
  }

  /** Shipped Date **/
  public Date getInPickingDate() {
	return (Date) getPropertyValue (AllocationConstants.PROPERTY_IN_PICKING_DATE);
  }
  public void setInPickingDate(Date pInPickingDate) {
	setPropertyValue (AllocationConstants.PROPERTY_IN_PICKING_DATE, pInPickingDate);	
  }
	
  /** Shipped Date **/
  public Date getReadyForPickupDate() {
	 return (Date) getPropertyValue (AllocationConstants.PROPERTY_READYFOR_PICKUP_DATE);
  }
  public void setReadyForPickupDate(Date pReadyForPickupDate) {
	setPropertyValue (AllocationConstants.PROPERTY_READYFOR_PICKUP_DATE, pReadyForPickupDate);	
  }
  
  /**
	 * Returns a string representation of the object.
	 */
	public String toString () {
		String lLineFeed = System.getProperty("line.separator");
		StringBuffer lStringBuffer = new StringBuffer();
		lStringBuffer.append (lLineFeed);
		lStringBuffer.append ("***************************************************" 		+ lLineFeed);
		lStringBuffer.append ("        Fulfillment History       "                  		+ lLineFeed);
		lStringBuffer.append ("Order Id ........................ " + getOrderId() 			+ lLineFeed);
		lStringBuffer.append ("Order Number .................... " + getOrderNumber() 		+ lLineFeed);
		lStringBuffer.append ("Commerce Item Id ................ " + getCommerceItemId() 	+ lLineFeed);
		lStringBuffer.append ("ShippingMethod .................. " + getShippingMethod() 	+ lLineFeed);
		lStringBuffer.append ("FirstName ......................."  + getFirstName()		 	+ lLineFeed);
		lStringBuffer.append ("LastName ........................"  + getLastName()		 	+ lLineFeed);
		lStringBuffer.append ("Address1 ........................"  + getLastName()      + lLineFeed);
		lStringBuffer.append ("Address2 ........................"  + getLastName()      + lLineFeed);
		lStringBuffer.append ("City     ........................"  + getCity()          + lLineFeed);
		lStringBuffer.append ("State    ........................"  + getState()     + lLineFeed);
		lStringBuffer.append ("PostalCode ......................"  + getPostalCode()      + lLineFeed);
		lStringBuffer.append ("County .........................."  + getCountry()      + lLineFeed);
		lStringBuffer.append ("Country ........................."  + getCountry()      + lLineFeed);
		lStringBuffer.append ("PhoneNumber ....................."  + getPhoneNumber()      + lLineFeed);
		lStringBuffer.append ("ContactEmail ...................."  + getContactEmail()      + lLineFeed);
		lStringBuffer.append ("ShippingMethod .................. " + getShippingMethod() 	+ lLineFeed);
		lStringBuffer.append ("Store Id ........................ " + getStoreId() 			+ lLineFeed);
		lStringBuffer.append ("Quantity ........................ " + getQuantity() 			+ lLineFeed);
		lStringBuffer.append ("Order Date ...................... " + getOrderDate() 		+ lLineFeed);
		lStringBuffer.append ("Allocation Date ................. " + getAllocationDate() 	+ lLineFeed);
		lStringBuffer.append ("State ........................... " + getState() 			+ lLineFeed);
		lStringBuffer.append ("State Detail .................... " + getStateDetail() 		+ lLineFeed);
		lStringBuffer.append ("Bopis Order ..................... " + isBopisOrder()     + lLineFeed);
		lStringBuffer.append ("In Picking  ..................... " + isInPicking()     + lLineFeed);
		lStringBuffer.append ("PickUp Instructions  ............ " + getPickUpInstructions()     + lLineFeed);
		lStringBuffer.append ("In Picking Date............ " + getInPickingDate()     + lLineFeed);
		lStringBuffer.append ("Ready For Pickup Date............ " + getReadyForPickupDate()     + lLineFeed);
		lStringBuffer.append ("***************************************************");
		return lStringBuffer.toString();
	}
}