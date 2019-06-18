package com.mff.commerce.order;

import atg.commerce.order.HardgoodShippingGroup;

import com.mff.constants.MFFConstants;

/**
* @author:DMI
*  
* @see atg.commerce.HardgoodShippingGroup.HardgoodShippingGroup
*/
public class MFFHardgoodShippingGroup extends HardgoodShippingGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MFFHardgoodShippingGroup() {
		super();
	}


	public String getFulfillmentStore() {
		return (String) getPropertyValue(MFFConstants.FULFILLMENT_STORE);
	}

	public void setFulfillmentStore (String pFulfillmentStore) {
		setPropertyValue(MFFConstants.FULFILLMENT_STORE, pFulfillmentStore);
	}	

	public boolean isSaturdayDelivery() {
    return (Boolean) getPropertyValue(MFFConstants.PROPERTY_SATURDAY_DELIVERY);
  }

  public void setSaturdayDelivery(boolean pSaturdayDelivery) {
    setPropertyValue(MFFConstants.PROPERTY_SATURDAY_DELIVERY, pSaturdayDelivery);
  }
  
	public String toString()
	{
	  return this.getClass().getName() + ":\n\t" +
	  super.toString(); 
	 // 	   + "\n\tGiftReceipt: " + getGiftReceipt()
	 //	   + "\n\tGiftMessage: " + getGiftMessage()
	 // 	   + "\n\tFulfillmentStore: " + getFulfillmentStore()
	 //      ;
	}	
}