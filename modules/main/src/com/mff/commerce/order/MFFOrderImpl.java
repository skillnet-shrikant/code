package com.mff.commerce.order;

import java.util.Date;
import java.util.List;

import com.aci.commerce.order.AciOrder;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.repository.RemovedItemException;
import atg.repository.RepositoryItem;


/**
 * Extension to order class.
 * 
 * @author DMI
 */
public class MFFOrderImpl extends AciOrder {
  
 private static final long serialVersionUID = 1L;

 /**
   * Removes an Address associated with this orders shipping group.
   * 
   * @param pShippingGroupId - The shipping groups id from which to remove the address.
   * 
   * @return A boolean indicating success or failure.
   */
  public boolean removeAddress(String pShippingGroupId){
    
    ShippingGroup orderShippingGroup =  null;
    
    try{
      orderShippingGroup = getShippingGroup(pShippingGroupId);
    }
    catch(Exception e) {
      return false;
    }
    
    // If the shipping group is found, set its Address to an empty address.   
    if(orderShippingGroup instanceof HardgoodShippingGroup) {
      Address emptyAddress = new Address();
      ((HardgoodShippingGroup)orderShippingGroup).setShippingAddress(emptyAddress);
      
      return true;
    }  
    
    return false;
  }
  
  @SuppressWarnings({ "unchecked" })
  public double getGCTotalDenominations(){
    double gcTotal=0.0;
    if(getCommerceItemCount() > 0){
      List<CommerceItem> lItems = getCommerceItems();
      for(CommerceItem lItem: lItems){
        if(((MFFCommerceItemImpl) lItem).isGiftCard()){
          gcTotal=gcTotal+((MFFCommerceItemImpl) lItem).getGiftCardDenomination();
        }
        }
      }
    return gcTotal;
  }
  
  /**
   * This is an helper method to find if this order requires a signature
   * This flag will be used while sending info to fedex
   * 1) If any of the commerce items in the order has value 18/21 then
   *     we mark this order to have signature required
   * 2) If order contains ffl items, we already default the item to 21 if already not set
   * @return
   */
  public boolean isSignatureRequired(){
    
    if(getCommerceItemCount() > 0){
      for(Object ci : getCommerceItems()){
        MFFCommerceItemImpl mffCI = (MFFCommerceItemImpl) ci;
        if(mffCI != null){
          if(mffCI.getMinimumAge() != null){
            int minAge = (int)mffCI.getMinimumAge();
            if(minAge == 18 || minAge == 21){
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  /**
   * Order is an LTL order if it has LTL items by weight 
   * 
   * Note: This method is used to determine if we need to calculate LTL charges 
   * 
   * LTL charges will be calculated only if the order has 1 LTL item without any free shipping discounts on it
   * 
   * @return
   */
  public boolean isLTLOrder(){
	  boolean ltlOrder = false;
	  boolean ltlItem = false;
	  boolean freeFreight = false;
	  if(getCommerceItemCount() > 0){
		  for(Object ci : getCommerceItems()){
			  MFFCommerceItemImpl mffCI = (MFFCommerceItemImpl) ci;
			  if(mffCI != null){
				  MFFPricingUtil util = new MFFPricingUtil();
				  ltlItem = util.isLTLItem(mffCI); // ltlItem by weight
				  if(ltlItem) {
					  return true;
				  }
			  }
		  }
	  }
	  MFFPricingUtil util = new MFFPricingUtil();
	  return util.isLTLOrderByItems(getCommerceItems());
  }
  
  public boolean isLTLShipRates() {
	  boolean isLTLShipRates = false;
	  MFFPricingUtil util = new MFFPricingUtil();
	  if(getShippingGroupCount() > 0) {
		  ShippingGroup sg = (ShippingGroup)getShippingGroups().get(0);
		  isLTLShipRates = util.isCalculateLTLShippingCosts(this, sg.getShippingMethod());  
	  }
	  return isLTLShipRates;
	  
  }
  
  /**
   * Flag to indicate is the order contains only LTL items with
   * either free freight flag or free item ship promo
   * @return
   */
  public boolean isFreeLTLItemsOnly(){
	  boolean ltlOrder = false;
	  boolean ltlItem = false;
	  boolean freeFreight = false;
	  double orderWeight = 0.0;
	  MFFPricingUtil util = new MFFPricingUtil();
	  if(isLTLOrder()) {
		  if(getCommerceItemCount() > 0){
			  for(Object ci : getCommerceItems()){
				  MFFCommerceItemImpl mffCI = (MFFCommerceItemImpl) ci;
				  if(mffCI != null){
					  ltlItem = util.isLTLItem(mffCI); // ltlItem by weight
					  freeFreight = util.isFreeFreightItem(mffCI); // item has free shipping flag or promo
					  if(ltlItem && !freeFreight) {
						  return false;
					  } else {
						  if(!freeFreight)
							  orderWeight += util.getItemWeightByQnty(mffCI);
					  }
				  }
			  }
		  }
		  if(orderWeight > 150) {
			  return false;
		  }
	  }

	  return true;
  }

  /**
   * Flag to indicate is the order contains LTL items with
   * either free freight flag or free item ship promo
   * AND regular non-ltl items.
   * @return
   */  
  public boolean isFreeLTLWithNonLTLItems(){
	  boolean ltlOrder = false;
	  boolean ltlItem = false;
	  boolean freeFreight = false;
	  if(getCommerceItemCount() > 0){
		  for(Object ci : getCommerceItems()){
			  MFFCommerceItemImpl mffCI = (MFFCommerceItemImpl) ci;
			  if(mffCI != null){
				  MFFPricingUtil util = new MFFPricingUtil();
				  ltlItem = util.isLTLItem(mffCI); // ltlItem by weight
				  freeFreight = util.isFreeFreightItem(mffCI); // item has free shipping flag or promo
				  if(!ltlItem) {
					  return true;
				  }
				  
				  
			  }
		  }
	  }
	  return false;
  }
  
  public boolean isEdsPPSOnly(){
	  boolean freeFreight = false;
	  if(getCommerceItemCount() > 0){
		  for(Object ci : getCommerceItems()){
			  MFFCommerceItemImpl mffCI = (MFFCommerceItemImpl) ci;
			  if(mffCI != null){
				  RepositoryItem lSku = (RepositoryItem) mffCI.getAuxiliaryData().getCatalogRef();

				  freeFreight = (Boolean) lSku.getPropertyValue("freeShipping");
				  if(freeFreight) {
					  return true;
				  }
			  }
		  }
	  }
	  return false;
  }  

  // Order is not shippable if it has bopisOnly items.
  // Products with fulfillment method 7
  public boolean isOrderShippable(){
	  if(getCommerceItemCount() > 0){
		  for(Object ci : getCommerceItems()){
			  MFFCommerceItemImpl mffCI = (MFFCommerceItemImpl) ci;
			  if(mffCI != null){
				  RepositoryItem prod = (RepositoryItem) mffCI.getAuxiliaryData().getProductRef();
				  if(prod.getPropertyValue("fulfillmentMethod") != null && (Integer)prod.getPropertyValue("fulfillmentMethod")==7) {
					  return false;
				  }
			  }
		  }
	  }
	  return true;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<String> getCouponCodes() {
    return (List)getPropertyValue(MFFConstants.PROPERTY_COUPON_CODES);
  }
  
  public void setCouponCodes(List<String> pCouponCodes) {
    setPropertyValue(MFFConstants.PROPERTY_COUPON_CODES, pCouponCodes);
  }

	public String getEmployeeId() {
		return (String) getPropertyValue(MFFConstants.PROPERTY_EMPLOYEE_ID);
	}

	public void setEmployeeId(String pEmployeeId) {
		setPropertyValue(MFFConstants.PROPERTY_EMPLOYEE_ID, pEmployeeId);
	}
	
	public String getOrderNumber() {
		return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_NUMBER);
	}

	public void setOrderNumber(String pOrderNumber) {
		setPropertyValue(MFFConstants.PROPERTY_ORDER_NUMBER, pOrderNumber);
	}
	
	public void setIPAddress(String pOrderNumber) {
		setPropertyValue(MFFConstants.PROPERTY_IP_ADDRESS, pOrderNumber);
	}
	
	public String getIPAddress() {
		return (String) getPropertyValue(MFFConstants.PROPERTY_IP_ADDRESS);
	}
	
	public String getTaxExemptionName() {
		return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_TAX_EXMP);
	}

	public void setTaxExemptionName(String pOrderNumber) {
		setPropertyValue(MFFConstants.PROPERTY_ORDER_TAX_EXMP, pOrderNumber);
	}
	
	public boolean isBopisOrder() {
    return (Boolean) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_ORDER);
  }

  public void setBopisOrder(boolean pBopisOrder) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_ORDER, pBopisOrder);
  }

  public String getBopisStore() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_STORE);
  }

  public void setBopisStore(String pBopisStore) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_STORE, pBopisStore);
  }
  
  public String getBopisSignature() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_SIGNATURE);
  }

  public void setBopisSignature(String pBopisSignature) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_SIGNATURE, pBopisSignature);
  }

  public String getBopisPerson() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_PERSON);
  }

  public void setBopisPerson(String pBopisPerson) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_PERSON, pBopisPerson);
  }
  
  public String getBopisEmail() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_EMAIL);
  }

  public void setBopisEmail(String pBopisEmail) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_EMAIL, pBopisEmail);
  }
  
  public String getContactEmail() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_CONTACT_EMAIL);
  }

  public void setContactEmail(String pContactEmail) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_CONTACT_EMAIL, pContactEmail);
  }
  
  public boolean isFFLOrder() {
    return (Boolean) getPropertyValue(MFFConstants.PROPERTY_ORDER_FFL_ORDER);
  }

  public void setFFLOrder(boolean pFFLOrder) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_FFL_ORDER, pFFLOrder);
  }
  
  public String getFFLDealerId() {
    return (String) getPropertyValue(MFFConstants.PROPERTY_ORDER_FFL_DEALER_ID);
  }

  public void setFFLDealerId(String pFFLDealerId) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_FFL_DEALER_ID, pFFLDealerId);
  }
  
  public Date getBopisReminderSentDate() {
    return (Date) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_REMINDER_SENT_DATE);
  }

  public void setBopisReminderSentDate(Date pBopisReminderSentDate) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_REMINDER_SENT_DATE, pBopisReminderSentDate);
  }
  
  public Date getBopisSecondReminderSentDate() {
    return (Date) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_SECOND_REMINDER_SENT_DATE);
  }

  public void setBopisSecondReminderSentDate(Date pBopisSecondReminderSentDate) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_SECOND_REMINDER_SENT_DATE, pBopisSecondReminderSentDate);
  }
  
  public Date getBopisReadyForPickupDate() {
    return (Date) getPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_READY_FOR_PICKUP_DATE);
  }

  public void setBopisReadyForPickupDate(Date pBopisReadyForPickupDate) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_BOPIS_READY_FOR_PICKUP_DATE, pBopisReadyForPickupDate);
  }

  /**
   * @return the pReturnablePPS
   */
  public boolean isReturnablePPS() {
    return (Boolean) getPropertyValue(MFFConstants.PROPERTY_ORDER_RETURNABLE_PPS);
  }

  /**
   * @param pPReturnablePPS the pReturnablePPS to set
   */
  public void setReturnablePPS(boolean pReturnablePPS) {
    setPropertyValue(MFFConstants.PROPERTY_ORDER_RETURNABLE_PPS, pReturnablePPS);
  }
  
  public Date getDateOfBirth() {
    return (Date)getPropertyValue(MFFConstants.PROPERTY_DATE_OF_BIRTH);
  }

  public void setDateOfBirth(Date pDateOfBirth) {
    setPropertyValue(MFFConstants.PROPERTY_DATE_OF_BIRTH, pDateOfBirth);
  }

  public String getAppInstanceName() {
	return (String) getPropertyValue(MFFConstants.PROPERTY_APP_INSTANCE_NAME);
  }
	
  public void setAppInstanceName(String pAppInstanceName) {
	setPropertyValue(MFFConstants.PROPERTY_APP_INSTANCE_NAME, pAppInstanceName);
  }
	
  /**
   * Method that renders the general order information in a readable string format.
   * 
   * @return string representation of the class.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("Order[");

    try {
      sb.append("type:").append(getOrderClassType()).append("; ");
      sb.append("id:").append(getId()).append("; ");
      sb.append("state:").append(getStateAsString()).append("; ");
      sb.append("transient:").append(isTransient()).append("; ");
      sb.append("profileId:").append(getProfileId()).append("; ");
    } 
    catch (RemovedItemException exc) {
      sb.append("removed");
    }

    sb.append("]");

    return sb.toString();
  }
}
