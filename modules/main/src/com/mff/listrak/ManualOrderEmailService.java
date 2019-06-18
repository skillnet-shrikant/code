package com.mff.listrak;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderManager;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;


import com.mff.listrak.MFFListrakEmailManager;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.commerce.order.MFFCommerceItemImpl;

public class ManualOrderEmailService extends GenericService {

	
	private OrderManager mOrderManager;
	private MFFListrakEmailManager mEmailManager;
	private String mOrderId;
	private boolean mEnable;
	private MFFProfileTools mProfileTools;
	
	public MFFProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(MFFProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

	
	public MFFListrakEmailManager getEmailManager() {
		return mEmailManager;
	}

	public void setEmailManager(MFFListrakEmailManager pEmailManager) {
		mEmailManager = pEmailManager;
	}

	protected RepositoryItem defaultUserProfile()
	{
		try {
			if(isLoggingDebug())
				logDebug("Looking for default user profile");
			RepositoryItem profile;
			profile = null;
			DynamoHttpServletRequest req;
			if((req = ServletUtil.getCurrentRequest()) != null)
				profile = (RepositoryItem)req.resolveName("/atg/userprofiling/Profile");
			return profile;
		}
		catch(Exception ex){
			if(isLoggingError())
				logError(ex);
			return null;
		}
    }
	
	public OrderManager getOrderManager() {
		return mOrderManager;
	}


	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}


	public String getOrderId() {
		return mOrderId;
	}


	public void setOrderId(String pOrderId) {
		mOrderId = pOrderId;
	}


	public boolean isEnable() {
		return mEnable;
	}


	public void setEnable(boolean pEnable) {
		mEnable = pEnable;
	}


	public void sendWebOrderConfirmationEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				RepositoryItem profileItem=fetchProfileFromOrder(order);
				if(profileItem==null){
					profileItem=defaultUserProfile();
				}
				getEmailManager().sendOrderConfirmationMail(order, profileItem);
				
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void sendBopisConfirmationEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				RepositoryItem profileItem=fetchProfileFromOrder(order);
				if(profileItem==null){
					profileItem=defaultUserProfile();
				}
				getEmailManager().sendOrderConfirmationMail(order, defaultUserProfile());
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void sendBopisAltConfirmationEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				RepositoryItem profileItem=fetchProfileFromOrder(order);
				if(profileItem==null){
					profileItem=defaultUserProfile();
				}
				getEmailManager().sendOrderConfirmationMail(order, defaultUserProfile());
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	
	public void sendOrderReadyForPickEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				getEmailManager().sendReadyForPickUpEmail(order);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void sendReadyForPickUpAlternateEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				getEmailManager().sendReadyForPickUpAlternateEmail(order);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	

	
	public void sendBopisReminderEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				getEmailManager().sendBopisReminderEmail(order);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void sendOrderPickUpConfirmationEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				getEmailManager().sendOrderPickUpConfirmationEmail(order);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void sendBopisReminder2Email(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				getEmailManager().sendBopisReminder2Email(order);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void sendOrderShippedEmail(){
		try{
			if(isEnable()){
				MFFOrderImpl order=(MFFOrderImpl)getOrderManager().loadOrder(getOrderId());
				List<CommerceItem> shippedCommerceItems=new ArrayList<CommerceItem>();
				HashMap<String,String> trakingNumberMap=new HashMap<String,String>();
				for(int i=0;i<order.getCommerceItems().size();i++){
					MFFCommerceItemImpl item=(MFFCommerceItemImpl)order.getCommerceItems().get(i);
					if(null != item && item.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.SHIPPED)) {
						String itemId=item.getId();
						String trackingNumber=item.getTrackingNumber();
						shippedCommerceItems.add(item);
						trakingNumberMap.put(itemId,trackingNumber);
					}
				}
				if(!shippedCommerceItems.isEmpty())
					getEmailManager().sendOrderShippedEmail(order, shippedCommerceItems, trakingNumberMap);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
   protected RepositoryItem fetchProfileFromOrder(MFFOrderImpl pOrder) {
	    RepositoryItem lProfile = null;
	    try {
	      lProfile = getProfileTools().getProfileItem(pOrder.getProfileId());
	    } catch (RepositoryException e) {
	      vlogError("RepositoryException occurred while fetching profile for order[" + pOrder.getId() + "]:: " + e.getMessage());
	    }
	    return lProfile;
	  }
	
}
