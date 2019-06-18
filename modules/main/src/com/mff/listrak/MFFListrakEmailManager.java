package com.mff.listrak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.listrak.service.email.OrderEmailService;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.email.MFFEmailManager;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import mff.MFFEnvironment;

public class MFFListrakEmailManager extends MFFEmailManager {

  private static final String IS_ENABLE_LISTRAK_PROP_NAME = "isEnableListrak";
  
	private OrderEmailService mOrderEmailService;
	private MFFEnvironment mEnvironment;
	private SiteManager mSiteManager;

	public SiteManager getSiteManager() {
		return mSiteManager;
	}

	public OrderEmailService getOrderEmailService() {
    return mOrderEmailService;
  }


  public void setOrderEmailService(OrderEmailService pOrderEmailService) {
    mOrderEmailService = pOrderEmailService;
  }

	public void setSiteManager(SiteManager pSiteManager) {
		mSiteManager = pSiteManager;
	}

	public MFFEnvironment getEnvironment() {
		return mEnvironment;
	}

	public void setEnvironment(MFFEnvironment pEnvironment) {
		mEnvironment = pEnvironment;
	}
	
	private boolean isListrakEnabled() {
		Site lCurrentSite = SiteContextManager.getCurrentSite();
	    if(null!=lCurrentSite) {
	    	vlogDebug("Current Site [{0}] and enable listark [{1}]",lCurrentSite,lCurrentSite.getPropertyValue(IS_ENABLE_LISTRAK_PROP_NAME));
	    	return ((Boolean)lCurrentSite.getPropertyValue(IS_ENABLE_LISTRAK_PROP_NAME)).booleanValue();
	    }
	    else{
	      vlogDebug("Current Site is null from SiteContextManager");
	      try {
          RepositoryItem lSiteItem = getSiteManager().getSite(getEnvironment().getDefaultSiteId());
          if(lSiteItem!=null) {
            vlogDebug("Current Site [{0}] from RepositoryItem and is listark enable [{1}]",lSiteItem,lSiteItem.getPropertyValue(IS_ENABLE_LISTRAK_PROP_NAME));
              return ((Boolean)lSiteItem.getPropertyValue(IS_ENABLE_LISTRAK_PROP_NAME)).booleanValue();
          } else {
            vlogDebug("Current Site is null from RepositoryItem");
            return false;
          } 
          
        } catch (RepositoryException e) {
          vlogError(e,String.format(getEnvironment().getDefaultSiteId(),"Repository Error"));
          return false;
        }
	    }
	}

	@Override
	public void sendOrderConfirmationMail(MFFOrderImpl pOrder, RepositoryItem pProfile, String pEmailAddress){
		try {	
			if(isListrakEnabled()){ // Put the toggle switch logic here
				List<String> purchaserNameFields = new ArrayList();
		        fillPurchaserNameFields(pProfile, pOrder, purchaserNameFields);
		        String purchaserFirstName=purchaserNameFields.get(0);
					if(pOrder.isBopisOrder()){
						String contactEmail=pOrder.getContactEmail();
						String altEmail=pOrder.getBopisEmail();
						
						if(contactEmail!=null && ! contactEmail.isEmpty()){
							if(altEmail!=null && ! altEmail.isEmpty()){
								if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
									getOrderEmailService().sendBopisAltOrderConfirmationEmail(pOrder,purchaserFirstName);
								}
								else {
									getOrderEmailService().sendBopisOrderConfirmationEmail(pOrder,purchaserFirstName);
								}
							}
							else {
								getOrderEmailService().sendBopisOrderConfirmationEmail(pOrder,purchaserFirstName);
							}
						}
						else {
							getOrderEmailService().sendBopisOrderConfirmationEmail(pOrder,purchaserFirstName);
						}
						
						
					}
					else if(pOrder.isFFLOrder()){
						super.sendOrderConfirmationMail(pOrder, pProfile, pEmailAddress);
					}
					else {
						getOrderEmailService().sendWebOrderConfirmationEmail(pOrder,purchaserFirstName);
					}
				}
				else {
					super.sendOrderConfirmationMail(pOrder, pProfile, pEmailAddress);
				}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	

	
	@Override
	public void sendOrderShippedEmail(MFFOrderImpl pOrder, List<CommerceItem> pItemsToShip,HashMap<String, String> pTrackingNumberMap){
		try {
			if(isListrakEnabled()){
				RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
				List<String> purchaserNameFields = new ArrayList();
		        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
		        String purchaserFirstName=purchaserNameFields.get(0);
				getOrderEmailService().sendOrderShippedEmail(pOrder,pItemsToShip,pTrackingNumberMap,purchaserFirstName);
			}
			else {
				super.sendOrderShippedEmail(pOrder, pItemsToShip, pTrackingNumberMap);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void sendReadyForPickUpEmail(MFFOrderImpl pOrder){
		try {
			if(isListrakEnabled()){
				RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
				List<String> purchaserNameFields = new ArrayList();
		        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
		        String purchaserFirstName=purchaserNameFields.get(0);
				List<ShippingGroup> spgs=pOrder.getShippingGroups();
				boolean isYardEntrance=false;
				boolean isCSDesk=false;
				boolean isFireArmsCounter=false;
				for(ShippingGroup spg:spgs){
					if(spg instanceof HardgoodShippingGroup){
						String pickupInstruction=(String)spg.getSpecialInstructions().get("pickupinstructions");
						if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
							if(pickupInstruction.toLowerCase().contains(("Outside Yard Entrance").toLowerCase())){
								isYardEntrance=true;
								break;
							}
						}
						if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
							if(pickupInstruction.toLowerCase().contains(("Customer Service Desk").toLowerCase())){
								isCSDesk=true;
								break;
							}
						}
						if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
							if(pickupInstruction.toLowerCase().contains(("Firearms Counter").toLowerCase())){
								isFireArmsCounter=true;
								break;
							}
						}						
					}
					
				}
				if(isCSDesk){
					String contactEmail=pOrder.getContactEmail();
					String altEmail=pOrder.getBopisEmail();
					if(contactEmail!=null && ! contactEmail.isEmpty()){
						if(altEmail!=null && ! altEmail.isEmpty()){
							if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
								getOrderEmailService().sendOrderReadyPickupCSEmail(pOrder,true,purchaserFirstName);
							}
							else {
								getOrderEmailService().sendOrderReadyPickupCSEmail(pOrder,false,purchaserFirstName);
							}
						}
						else {
							getOrderEmailService().sendOrderReadyPickupCSEmail(pOrder,false,purchaserFirstName);
						}
					}
					else {
						getOrderEmailService().sendOrderReadyPickupCSEmail(pOrder,false,purchaserFirstName);
					}
						
				}
				else if(isYardEntrance){
					
					String contactEmail=pOrder.getContactEmail();
					String altEmail=pOrder.getBopisEmail();
					if(contactEmail!=null && ! contactEmail.isEmpty()){
						if(altEmail!=null && ! altEmail.isEmpty()){
							if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
								getOrderEmailService().sendOrderReadyPickupOutEmail(pOrder,true,purchaserFirstName);
							}
							else {
								getOrderEmailService().sendOrderReadyPickupOutEmail(pOrder,false,purchaserFirstName);
							}
						}
						else {
							getOrderEmailService().sendOrderReadyPickupOutEmail(pOrder,false,purchaserFirstName);
						}
					}
					else {
						getOrderEmailService().sendOrderReadyPickupOutEmail(pOrder,false,purchaserFirstName);
					}
				}
				else{
					
					String contactEmail=pOrder.getContactEmail();
					String altEmail=pOrder.getBopisEmail();
					if(contactEmail!=null && ! contactEmail.isEmpty()){
						if(altEmail!=null && ! altEmail.isEmpty()){
							if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
								getOrderEmailService().sendOrderReadyPickupFACEmail(pOrder,true,purchaserFirstName);
							}
							else {
								getOrderEmailService().sendOrderReadyPickupFACEmail(pOrder,false,purchaserFirstName);
							}
						}
						else {
							getOrderEmailService().sendOrderReadyPickupFACEmail(pOrder,false,purchaserFirstName);
						}
					}
					else {
						getOrderEmailService().sendOrderReadyPickupFACEmail(pOrder,false,purchaserFirstName);
					}
				}

				
			}
			else {
				super.sendReadyForPickUpEmail(pOrder);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void sendReadyForPickUpAlternateEmail(MFFOrderImpl pOrder) {
		try {
			
			if(isListrakEnabled()){
				String contactEmail=pOrder.getContactEmail();
				String altEmail=pOrder.getBopisEmail();
				if (!StringUtils.isBlank(altEmail) && !pOrder.getContactEmail().equalsIgnoreCase(altEmail)) {
					RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
					List<String> purchaserNameFields = new ArrayList();
			        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
			        String purchaserFirstName=purchaserNameFields.get(0);
					List<ShippingGroup> spgs=pOrder.getShippingGroups();
					boolean isYardEntrance=false;
					boolean isCSDesk=false;
					boolean isFireArmsCounter=false;					
					for(ShippingGroup spg:spgs){
						if(spg instanceof HardgoodShippingGroup){
							String pickupInstruction=(String)spg.getSpecialInstructions().get("pickupinstructions");
							if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
								if(pickupInstruction.toLowerCase().contains(("Outside Yard Entrance").toLowerCase())){
									isYardEntrance=true;
									break;
								}
							}
							if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
								if(pickupInstruction.toLowerCase().contains(("Customer Service Desk").toLowerCase())){
									isCSDesk=true;
									break;
								}
							}
							if(pickupInstruction!=null && !pickupInstruction.isEmpty()){
								if(pickupInstruction.toLowerCase().contains(("Firearms Counter").toLowerCase())){
									isFireArmsCounter=true;
									break;
								}
							}
						}
						
						
					}
					if(isCSDesk){
						
						if(contactEmail!=null && ! contactEmail.isEmpty()){
							if(altEmail!=null && ! altEmail.isEmpty()){
								if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
									getOrderEmailService().sendOrderReadyPickupCSAltEmail(pOrder,altEmail,purchaserFirstName);
								}
								else {
									getOrderEmailService().sendOrderReadyPickupCSEmail(pOrder,false,purchaserFirstName);
								}
							}
							else {
								getOrderEmailService().sendOrderReadyPickupCSEmail(pOrder,false,purchaserFirstName);
							}
						}
						else {
							getOrderEmailService().sendOrderReadyPickupCSEmail(pOrder,false,purchaserFirstName);
						}
						
						
					}
					else if(isYardEntrance){
						if(contactEmail!=null && ! contactEmail.isEmpty()){
							if(altEmail!=null && ! altEmail.isEmpty()){
								if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
									getOrderEmailService().sendOrderReadyPickupOutAltEmail(pOrder,altEmail,purchaserFirstName);
								}
								else {
									getOrderEmailService().sendOrderReadyPickupOutEmail(pOrder,false,purchaserFirstName);
								}
							}
							else {
								getOrderEmailService().sendOrderReadyPickupOutEmail(pOrder,false,purchaserFirstName);
							}
						}
						else {
							getOrderEmailService().sendOrderReadyPickupOutEmail(pOrder,false,purchaserFirstName);
						}
					}
					else {
						if(contactEmail!=null && ! contactEmail.isEmpty()){
							if(altEmail!=null && ! altEmail.isEmpty()){
								if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
									getOrderEmailService().sendOrderReadyPickupFACAltEmail(pOrder,altEmail,purchaserFirstName);
								}
								else {
									getOrderEmailService().sendOrderReadyPickupFACEmail(pOrder,false,purchaserFirstName);
								}
							}
							else {
								getOrderEmailService().sendOrderReadyPickupFACEmail(pOrder,false,purchaserFirstName);
							}
						}
						else {
							getOrderEmailService().sendOrderReadyPickupFACEmail(pOrder,false,purchaserFirstName);
						}						
					}
				}
			}
			else {
				super.sendReadyForPickUpAlternateEmail(pOrder);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void sendBopisReminderEmail(MFFOrderImpl pOrder){
		try {
			if(isListrakEnabled()){
				RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
				List<String> purchaserNameFields = new ArrayList();
		        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
		        String purchaserFirstName=purchaserNameFields.get(0);
				String contactEmail=pOrder.getContactEmail();
				String altEmail=pOrder.getBopisEmail();
				if(contactEmail!=null && ! contactEmail.isEmpty()){
					if(altEmail!=null && ! altEmail.isEmpty()){
						if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
							getOrderEmailService().sendOrderReadyPickupRemEmail(pOrder,true,purchaserFirstName);
							getOrderEmailService().sendOrderReadyPickupRemAltEmail(pOrder,altEmail,purchaserFirstName);
						}
						else {
							getOrderEmailService().sendOrderReadyPickupRemEmail(pOrder,false,purchaserFirstName);
						}
					}
					else {
						getOrderEmailService().sendOrderReadyPickupRemEmail(pOrder,false,purchaserFirstName);
					}
				}
				else {
					getOrderEmailService().sendOrderReadyPickupRemEmail(pOrder,false,purchaserFirstName);
				}
			}
			else {
				super.sendBopisReminderEmail(pOrder);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void sendOrderPickUpConfirmationEmail(MFFOrderImpl pOrder){
		try{
			if(isListrakEnabled()){
				RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
				List<String> purchaserNameFields = new ArrayList();
		        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
		        String purchaserFirstName=purchaserNameFields.get(0);
				String contactEmail=pOrder.getContactEmail();
				String altEmail=pOrder.getBopisEmail();
				if(contactEmail!=null && ! contactEmail.isEmpty()){
					if(altEmail!=null && ! altEmail.isEmpty()){
						if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
							getOrderEmailService().sendOrderPickupConfirmationEmail(pOrder,true,purchaserFirstName);
							//getOrderEmailService().sendOrderPickupConfirmationAltEmail(pOrder,altEmail,purchaserFirstName);
						}
						else {
							getOrderEmailService().sendOrderPickupConfirmationEmail(pOrder,false,purchaserFirstName);
						}
					}
					else {
						getOrderEmailService().sendOrderPickupConfirmationEmail(pOrder,false,purchaserFirstName);
					}
				}
				else {
					getOrderEmailService().sendOrderPickupConfirmationEmail(pOrder,false,purchaserFirstName);
				}
			}
			else {
				super.sendOrderPickUpConfirmationEmail(pOrder);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void sendBopisReminder2Email(MFFOrderImpl pOrder){
		try {
			if(isListrakEnabled()){
				RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
				List<String> purchaserNameFields = new ArrayList();
		        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
		        String purchaserFirstName=purchaserNameFields.get(0);
				String contactEmail=pOrder.getContactEmail();
				String altEmail=pOrder.getBopisEmail();
				if(contactEmail!=null && ! contactEmail.isEmpty()){
					if(altEmail!=null && ! altEmail.isEmpty()){
						if(!altEmail.trim().equalsIgnoreCase(contactEmail.trim())){
							getOrderEmailService().sendOrderReadyPickupRem2Email(pOrder,true,purchaserFirstName);
							getOrderEmailService().sendOrderReadyPickupRem2AltEmail(pOrder,altEmail,purchaserFirstName);
						}
						else {
							getOrderEmailService().sendOrderReadyPickupRem2Email(pOrder,false,purchaserFirstName);
						}
					}
					else {
						getOrderEmailService().sendOrderReadyPickupRem2Email(pOrder,false,purchaserFirstName);
					}
				}
				else {
					getOrderEmailService().sendOrderReadyPickupRem2Email(pOrder,false,purchaserFirstName);
				}
			}
			else {
				super.sendBopisReminder2Email(pOrder);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
