package com.mff.commerce.order;

import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.nucleus.GenericService;

public class MFFPrevAddressRequest extends GenericService {

  private Address previousAddress;
  private Address suggestedAddress;
  private String mAddressType;
  private String mPhoneNumber;
  
	public Address getPreviousAddress() {
    return previousAddress;
  }

  public void setPreviousAddress(Address previousAddress) {
    this.previousAddress = previousAddress;
    if(previousAddress != null){
      setPhoneNumber(((ContactInfo)previousAddress).getPhoneNumber());
    }
  }

  public Address getSuggestedAddress() {
    return suggestedAddress;
  }

  public void setSuggestedAddress(Address suggestedAddress) {
    this.suggestedAddress = suggestedAddress;
  }

  /*
	 * clear address fields for this object
	 */
	public void clearAddress()
	{
	  setSuggestedAddress(null);
	  setPreviousAddress(null);
		setAddressType(null);
		setPhoneNumber(null);
	}
	
	/**
   * @return the phoneNumber
   */
  public String getPhoneNumber() {
    return mPhoneNumber;
  }

  /**
   * @param pPhoneNumber the phoneNumber to set
   */
  public void setPhoneNumber(String pPhoneNumber) {
    mPhoneNumber = pPhoneNumber;
  }

	/**
	 * @return the addressType
	 */
	public String getAddressType() {
		return mAddressType;
	}

	/**
	 * @param pAddressType the addressType to set
	 */
	public void setAddressType(String pAddressType) {
		mAddressType = pAddressType;
	}

}
