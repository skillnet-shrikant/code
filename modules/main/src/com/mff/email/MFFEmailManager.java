package com.mff.email;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.aci.payment.creditcard.AciCreditCard;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.constants.MFFConstants;
import com.mff.locator.StoreLocatorTools;
import com.mff.userprofiling.MFFProfileTools;

import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.PaymentGroup;
import atg.commerce.states.StateDefinitions;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

public class MFFEmailManager extends GenericService {

  protected MFFProfileTools mProfileTools;
  protected StoreLocatorTools mStoreLocatorTools;
  protected boolean mSendEmailInSeparateThread;
  protected boolean mPersistEmails;
  protected String mDefaultMessageFrom;
  protected String mDefaultMessageSubject;
  protected String mFFLCCEmail;
  protected String mPartialCancellationSubject;
  protected String mFullCancellationSubject;
  protected TemplateEmailInfoImpl mDefaultEmailTemplate;
  protected TemplateEmailSender mTemplateEmailSender;
  protected String mTestOrderId;
  protected TemplateEmailInfoImpl mAccountCreationEmailTemplate;
  protected TemplateEmailInfoImpl mEmailUpdateEmailTemplate;
  protected TemplateEmailInfoImpl mPasswordUpdateEmailTemplate;
  protected TemplateEmailInfoImpl mEmailAFriendEmailTemplate;
  protected TemplateEmailInfoImpl mShareWishListEmailTemplate;
  protected TemplateEmailInfoImpl mBackinStockEmailTemplate;
  protected TemplateEmailInfoImpl mOrderConfirmationEmailTemplate;
  protected TemplateEmailInfoImpl mOrderCancellationEmailTemplate;
  protected TemplateEmailInfoImpl mBopisOrderConfirmationEmailTemplate;
  protected TemplateEmailInfoImpl mBopisReadyForPickupEmailTemplate;
  protected TemplateEmailInfoImpl mBopisReminderToPickupEmailTemplate;
  protected TemplateEmailInfoImpl mBopisOrderPickupConfirmationEmailTemplate;
  protected TemplateEmailInfoImpl mBopisEmailToAlternateEmailTemplate;
  protected TemplateEmailInfoImpl mBopisCancellationEmailTemplate;
  protected TemplateEmailInfoImpl mOrderShippedEmailTemplate;
  protected TemplateEmailInfoImpl mFFLConfirmationEmailTemplate;
  protected TemplateEmailInfoImpl mContactUsEmailTemplate;
  protected TemplateEmailInfoImpl mPasswordResetEmailTemplate;
  protected TemplateEmailInfoImpl mReturnRecievedEmailTemplate;
  protected TemplateEmailInfoImpl mAppeasementEmailTemplate;
  protected TemplateEmailInfoImpl mInvoiceEmailTemplate;
  protected Map<String,String> mContactUsStoreDropdownName;
	

	public Map<String, String> getContactUsStoreDropdownName() {
		return mContactUsStoreDropdownName;
	}
	public void setContactUsStoreDropdownName(Map<String, String> pContactUsStoreDropdownName) {
		mContactUsStoreDropdownName = pContactUsStoreDropdownName;
	}
  
  public TemplateEmailInfoImpl getAppeasementEmailTemplate() {
     return mAppeasementEmailTemplate;
  }

  public void setAppeasementEmailTemplate(TemplateEmailInfoImpl pAppeasementEmailTemplate) {
    mAppeasementEmailTemplate = pAppeasementEmailTemplate;
  }
    
  protected String defaultContactUsEmailTo;
  protected Map<String, String> contactUsEmailMap = new HashMap<String, String>();

  /**
   * This method is used to send the email to customer on account creation
   *
   * @param pUser
   * @param pLocale
   * @return
   */
  public boolean sendAccountCreation(RepositoryItem pUser, Locale pLocale) {
    boolean result = false;
    if (pUser != null) {
      String email = (String) pUser.getPropertyValue(MFFConstants.EMAIL);
      if (!StringUtils.isBlank(email)) {
        try {
          Map<String, Object> templateParameters = new HashMap<String, Object>();
          templateParameters.put("username", (new StringBuilder()).append(pUser.getPropertyValue(MFFConstants.FIRST_NAME)).append(" ").append(pUser.getPropertyValue(MFFConstants.LAST_NAME)).toString());

          vlogDebug(email);
          TemplateEmailInfoImpl template = getAccountCreationEmailTemplate();
          if (null != template) {
            TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
            template.copyPropertiesTo(templateEmail);
            templateEmail.setTemplateParameters(templateParameters);
            Object[] recepients = { email };

            getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
            result = true;
          } else {
            vlogDebug("Template Email Info is null, please check components configuration.");
          }

        } catch (TemplateEmailException e) {
          vlogError("TemplateEmailException occurred while sending account creationn email :: " + e.getMessage());
        }
      }
    } else {
      vlogDebug("No user found.");
    }

    return result;
  }

  /**
   * This method is used to send the email to customer on email updation
   *
   * @param pUser
   * @param pLocale
   * @return
   */
  public boolean sendEmailUpdateEmail(RepositoryItem pUser, Locale pLocale) {
    boolean result = false;
    if (pUser != null) {
      String email = (String) pUser.getPropertyValue(MFFConstants.EMAIL);
      if (!StringUtils.isBlank(email)) {
        try {
          Map<String, Object> templateParameters = new HashMap<String, Object>();
          templateParameters.put("username", (new StringBuilder()).append(pUser.getPropertyValue(MFFConstants.FIRST_NAME)).append(" ").append(pUser.getPropertyValue(MFFConstants.LAST_NAME)).toString());
          vlogDebug(email);

          TemplateEmailInfoImpl template = getEmailUpdateEmailTemplate();
          if (null != template) {
            TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
            template.copyPropertiesTo(templateEmail);
            templateEmail.setTemplateParameters(templateParameters);
            Object[] recepients = { email };

            getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
            result = true;
          } else {
            vlogDebug("Template Email Info is null, please check components configuration.");
          }

        } catch (TemplateEmailException e) {
          vlogError("TemplateEmailException occurred while sending account creationn email :: " + e.getMessage());
        }
      }
    } else {
      vlogDebug("No user found.");
    }

    return result;
  }

  /**
   * This method is used to send the email to customer on email updation
   *
   * @param pUser
   * @param pLocale
   * @return
   */
  public boolean sendPasswordUpdateEmail(RepositoryItem pUser, Locale pLocale) {
    boolean result = false;
    if (pUser != null) {
      String email = (String) pUser.getPropertyValue(MFFConstants.EMAIL);
      if (!StringUtils.isBlank(email)) {
        try {
          Map<String, Object> templateParameters = new HashMap<String, Object>();
          templateParameters.put("username", pUser.getPropertyValue(MFFConstants.FIRST_NAME));
          vlogDebug(email);

          TemplateEmailInfoImpl template = getPasswordUpdateEmailTemplate();
          if (null != template) {
            TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
            template.copyPropertiesTo(templateEmail);
            templateEmail.setTemplateParameters(templateParameters);
            Object[] recepients = { email };

            getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
            result = true;
          } else {
            vlogDebug("Template Email Info is null, please check components configuration.");
          }

        } catch (TemplateEmailException e) {
          vlogError("TemplateEmailException occurred while sending account creationn email :: " + e.getMessage());
        }
      }
    } else {
      vlogDebug("No user found.");
    }

    return result;
  }

  public void sendAFriendEmail(Map pEmailData) {
    vlogDebug("MFFEmailManager :: sendAFriendEmail :: START");
    TemplateEmailInfoImpl template = getEmailAFriendEmailTemplate();
    if (null != template) {
      TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
      template.setMessageSubject((String) pEmailData.get("yourName") + " Has Shared a Product with You from FleetFarm.com");
      template.copyPropertiesTo(templateEmail);
      templateEmail.setTemplateParameters(pEmailData);
      Object[] recepients = { pEmailData.get("friendEmail") };
      templateEmail.setMessageFrom((String) pEmailData.get("yourEmail"));

      try {
        getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
      } catch (TemplateEmailException e) {
        vlogError("TemplateEmailException occurrend while sending Email a Friend PDP :: " + e.getMessage());
      }
    }
    vlogDebug("MFFEmailManager :: sendAFriendEmail :: END");
  }

  public void sendShareWishlistEmail(Map<String, Object> pEmailData) {

    TemplateEmailInfoImpl template = getShareWishListEmailTemplate();
    if (null != template) {
      TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
      template.setMessageSubject((String) pEmailData.get("yourName") + " Has Shared a Wish List with You");
      template.copyPropertiesTo(templateEmail);
      templateEmail.setTemplateParameters(pEmailData);
      Object[] recepients = { pEmailData.get("friendEmail") };
      templateEmail.setMessageFrom((String) pEmailData.get("yourEmail"));

      try {
        getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), true);
      } catch (TemplateEmailException e) {

        if (isLoggingError()) {
          vlogError("sendShareWishlistEmail(): Exception during sending email to share a wishlist: " + e, e);
        }
      }
    }
  }

  public void sendBackInstockNotification(Map pEmailData, Object[] pReceipents) {
    if (pEmailData != null) {
      TemplateEmailInfoImpl template = getBackinStockEmailTemplate();
      if (null != template) {
        TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
        template.copyPropertiesTo(templateEmail);
        templateEmail.setTemplateParameters(pEmailData);
        try {
          getTemplateEmailSender().sendEmailMessage(templateEmail, pReceipents, isSendEmailInSeparateThread(), true);
        } catch (TemplateEmailException e) {
          vlogError("sendBackInstockNotification(): Exception during sending email to BackInStock item: " + e.getMessage());
        }
      }
    }

  }

  public void sendOrderConfirmationMail(MFFOrderImpl pOrder, RepositoryItem pProfile, String pEmailAddress) {

    vlogDebug("Entered into sendOrderConfirmationMail...");
    if (pProfile != null) {
      try {
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        templateParameters.put("profile", pProfile);
        templateParameters.put("order", pOrder);
        vlogDebug(pEmailAddress);
        TemplateEmailInfoImpl template = null;
        vlogDebug("lOrder.isFFLOrder()  -> {0}", pOrder.isFFLOrder());
        List purchaserNameFields = new ArrayList();
        fillPurchaserNameFields(pProfile, pOrder, purchaserNameFields);
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
        
        if (pOrder.isBopisOrder()) {
          templateParameters.put("store", fetchStoreFromOrder(pOrder));
          template = getBopisOrderConfirmationEmailTemplate();
        } else if (pOrder.isFFLOrder()) {
        	template = getFflConfirmationEmailTemplate();
        	vlogDebug("FFL Email Template  -> {0}", template);
        } else {
        	template = getOrderConfirmationEmailTemplate();
        }
        
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          if(pOrder.isFFLOrder()){
            if(StringUtils.isNotBlank(getFFLCCEmail())){
              Object[] recepients = { pEmailAddress,getFFLCCEmail() };
              getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
            }
            else{
             Object[] recepients = { pEmailAddress };
             getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
            }
          }else{
            Object[] recepients = { pEmailAddress };
            getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
          }
        } else {
          vlogDebug("Template Email Info is null, please check components configuration.");
        }

      } catch (TemplateEmailException e) {
        vlogError(e, "TemplateEmailException occurred while sending order confirmation email :: " + e.getMessage());
      }

    } else {
      vlogDebug("Profile is null");
    }

  }

  public void sendOrderConfirmationMail(MFFOrderImpl pOrder, RepositoryItem pProfile) {
    vlogDebug("Entered into sendOrderConfirmationMail...");
    if (pProfile != null) {
      String email = pOrder.getContactEmail();
      if (!StringUtils.isBlank(email)) {
        sendOrderConfirmationMail(pOrder, pProfile, email);
      } else {
        vlogDebug("email is blank");
      }
    } else {
      vlogDebug("Profile is null");
    }
  }

  public void sendOrderConfirmationMail(MFFOrderImpl pOrder, Profile pProfile) {

    sendOrderConfirmationMail(pOrder, pProfile.getDataSource());
  }

  public void sendCancelItemsEmail(MFFOrderImpl pOrder, List<String> pCancelItemsIds,HashMap<String,String> pReasonCodeMap) {
	  
	  String email = pOrder.getContactEmail();
	  String reasonCode = "";
	  
	  if (!StringUtils.isBlank(email)) {
		
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
     
        for (String lCommerceItemId : pCancelItemsIds) {
          if(pReasonCodeMap != null && pReasonCodeMap.get(lCommerceItemId) != null){
            reasonCode = pReasonCodeMap.get(lCommerceItemId);
            break;
          }
        }
        
        vlogDebug("sendCancelItemsEmail : CancelReasonCode - {0}, email - {1}",reasonCode,email);
        templateParameters.put("profile", profile);
        templateParameters.put("order", pOrder);
        templateParameters.put("cancelledItems", pCancelItemsIds);
        templateParameters.put("reasonCode", reasonCode);

        TemplateEmailInfoImpl template = null;
        List<String> purchaseName = new ArrayList<String>();
        fillPurchaserNameFields(profile, pOrder, purchaseName);
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaseName.get(0));
        if (pOrder.isBopisOrder()) {
          template = getBopisCancellationEmailTemplate();
          templateParameters.put("store", fetchStoreFromOrder(pOrder));
          templateParameters.put(MFFConstants.CONST_BOPIS_SALUTATION, purchaseName.get(0));
        } else {
          template = getOrderCancellationEmailTemplate();
        }
        
        if (null != template) {
          String cancellationType = getCancellationType(pOrder);
          vlogDebug("cancellationType: " + cancellationType);
          templateParameters.put("cancelType", cancellationType);
          template.setMessageSubject(getMessageSubjectType(cancellationType,pOrder.getOrderNumber()));
          /*if (MFFConstants.CONST_ORDER_CANCEL_PARTIAL.equalsIgnoreCase(cancellationType)){
            template.setMessageSubject("Order#" + pOrder.getOrderNumber() + ":" + getPartialCancellationSubject());
          } else {
            template.setMessageSubject("Order#" + pOrder.getOrderNumber() + ":" + getFullCancellationSubject());
          }*/
          
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.copyPropertiesTo(templateEmail);
          
          templateEmail.setTemplateParameters(templateParameters);
          
          Object[] recepientPurchase = { email };
        	try {
        	  getTemplateEmailSender().sendEmailMessage(templateEmail, recepientPurchase, isSendEmailInSeparateThread(), isPersistEmails());
    			} catch (TemplateEmailException e1) {
    				vlogError(e1, "TemplateEmailException occurred while sending order cancellation email to purchaser.");
    			}
            
        } else {
          vlogDebug("Template Email Info is null, please check components configuration.");
        }
        
        if (pOrder.isBopisOrder()) {
  		  sendBopisCancelItemsEmailToAlternate(profile, pOrder, pCancelItemsIds, purchaseName);
  	  	}
      
    } else {
      vlogDebug("email is blank");
    }

  }

  public void sendBopisCancelItemsEmailToAlternate(RepositoryItem pProfile, MFFOrderImpl pOrder, List<String> pCancelItemsIds, List purchaseName) {
	  
	  String purchaseEmail = pOrder.getContactEmail();
	  String alternateEmail = pOrder.getBopisEmail();
	  
	  if (!StringUtils.isBlank(alternateEmail) && !alternateEmail.equalsIgnoreCase(purchaseEmail)) {
		
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        
        templateParameters.put("profile", pProfile);
        templateParameters.put("order", pOrder);
        templateParameters.put("cancelledItems", pCancelItemsIds);
        
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaseName.get(0));
        templateParameters.put(MFFConstants.CONST_BOPIS_SALUTATION, pOrder.getBopisPerson());
        
        TemplateEmailInfoImpl template = getBopisCancellationEmailTemplate();
        templateParameters.put("store", fetchStoreFromOrder(pOrder));
        
        
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.copyPropertiesTo(templateEmail);
          
          templateEmail.setTemplateParameters(templateParameters);
          
          Object[] recepientAlternate = { alternateEmail };
          try {
			getTemplateEmailSender().sendEmailMessage(templateEmail, recepientAlternate, isSendEmailInSeparateThread(), isPersistEmails());
		} catch (TemplateEmailException e1) {
			vlogError(e1, "TemplateEmailException occurred while sending order cancellation email to alternate.");
		}
            
        } else {
          vlogDebug("sendBopisCancelItemsEmailToAlternate(): Template Email Info is null, please check components configuration.");
        }

      
    } else {
      vlogDebug("sendBopisCancelItemsEmailToAlternate(): alternateEmail is blank");
    }

  }

  public void sendOrderShippedEmail(MFFOrderImpl pOrder, List<CommerceItem> pItemsToShip,HashMap<String, String> pTrackingNumberMap) {
    String email = pOrder.getContactEmail();
    if (!StringUtils.isBlank(email)) {
      try {
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        RepositoryItem profile = fetchProfileFromOrder(pOrder);
        templateParameters.put("profile", profile);
        templateParameters.put("order", pOrder);
        templateParameters.put("itemsToShip", pItemsToShip);
        templateParameters.put("trackingNumberMap", pTrackingNumberMap);
        List purchaserNameFields = new ArrayList();
        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
        
        vlogDebug(email);
        vlogDebug("trackingNumberMap --- {0}", pTrackingNumberMap);
        TemplateEmailInfoImpl template = getOrderShippedEmailTemplate();
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.setMessageSubject("Order #" +pOrder.getOrderNumber() +": Your Order Has Shipped");
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          Object[] recepients = { email };

          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
        } else {
          vlogDebug("Template Email Info is null, please check components configuration.");
        }

      } catch (TemplateEmailException e) {
        vlogError("TemplateEmailException occurred while sending order shipped email :: " + e.getMessage());
      }
    } else {
      vlogDebug("email is blank");
    }
  }

  public void sendReadyForPickUpEmail(MFFOrderImpl pOrder) {
    String email = pOrder.getContactEmail();
    if (!StringUtils.isBlank(email)) {
      try {
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        
        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
        
        templateParameters.put("profile", profile);
        templateParameters.put("store", fetchStoreFromOrder(pOrder));
        templateParameters.put("order", pOrder);
        vlogDebug(email);
        
        List purchaserNameFields = new ArrayList();
        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
        
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
        
        TemplateEmailInfoImpl template = getBopisReadyForPickupEmailTemplate();
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          Object[] recepients = { email };

          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
        } else {
          vlogDebug("Template Email Info is null, please check components configuration.");
        }

      } catch (TemplateEmailException e) {
        vlogError("TemplateEmailException occurred while sending order ready for pickup email :: " + e.getMessage());
      }
    } else {
      vlogDebug("email is blank");
    }
  }

  public void sendReadyForPickUpAlternateEmail(MFFOrderImpl pOrder) {
    String email = pOrder.getBopisEmail();
    if (!StringUtils.isBlank(email) && !pOrder.getContactEmail().equalsIgnoreCase(email)) {
      try {
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
        
        templateParameters.put("profile", profile);
        templateParameters.put("store", fetchStoreFromOrder(pOrder));
        templateParameters.put("order", pOrder);
        vlogDebug(email);
        
        List purchaserNameFields = new ArrayList();
        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
        
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));

        TemplateEmailInfoImpl template = getBopisEmailToAlternateEmailTemplate();
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          Object[] recepients = { email };

          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
        } else {
          vlogDebug("Template Email Info is null, please check components configuration.");
        }

      } catch (TemplateEmailException e) {
        vlogError("TemplateEmailException occurred while sending order alternate pickup email :: " + e.getMessage());
      }
    } else {
      vlogDebug("email is blank or contact email & bopis email are same");
    }
  }
  
	public void sendBopisReminderEmail(MFFOrderImpl pOrder) {

		sendBopisReminderEmailToPurchaser(pOrder);
		sendBopisReminderToAlternateEmail(pOrder);
	}

	protected void sendBopisReminderEmailToPurchaser(MFFOrderImpl pOrder) {
		
		String purchaserEmail = pOrder.getContactEmail();
	    vlogDebug("sendBopisReminderEmailToPurchaser(): purchaserEmail: " + purchaserEmail);
	    
	    if (!StringUtils.isBlank(purchaserEmail)) {
		    Map<String, Object> templateParameters = new HashMap<String, Object>();
	        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
	        templateParameters.put("profile", profile);
	        templateParameters.put("store", fetchStoreFromOrder(pOrder));
	        templateParameters.put("order", pOrder);
	        List purchaserNameFields = new ArrayList();
	        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
	        
	        templateParameters.put(MFFConstants.CONST_BOPIS_SALUTATION, purchaserNameFields.get(0));
	        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
	        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
	                
	        TemplateEmailInfoImpl template = getBopisReminderToPickupEmailTemplate();
	        if (null != template) {
	          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
	          template.copyPropertiesTo(templateEmail);
	          
	          templateEmail.setTemplateParameters(templateParameters);
	          Object[] recepientPurchaser = { purchaserEmail };

				try {
					getTemplateEmailSender().sendEmailMessage(templateEmail,
							recepientPurchaser, isSendEmailInSeparateThread(), isPersistEmails());
				} catch (TemplateEmailException e) {
					vlogError("sendBopisReminderEmailToPurchaser(): TemplateEmailException occurred while sending Reminder to purchaser: " + e, e);
				}
	        } else {
		          vlogDebug("sendBopisReminderEmailToPurchaser(): Template Email Info is null, please check components configuration.");
	        }
	    } else {
		      vlogDebug("sendBopisReminderEmailToPurchaser(): purchaserEmail is blank");
		}
	}
	
	protected void sendBopisReminderToAlternateEmail(MFFOrderImpl pOrder) {
		
		String purchaserEmail = pOrder.getContactEmail();
	    String alternateEmail = pOrder.getBopisEmail();
	    vlogDebug("sendBopisReminderToAlternateEmail(): purchaserEmail: " + purchaserEmail);
	    vlogDebug("sendBopisReminderToAlternateEmail(): alternateEmail: " + alternateEmail);
	    
	    if (!StringUtils.isBlank(alternateEmail) && !alternateEmail.equalsIgnoreCase(purchaserEmail)) {
		    Map<String, Object> templateParameters = new HashMap<String, Object>();
	        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
	        templateParameters.put("profile", profile);
	        templateParameters.put("store", fetchStoreFromOrder(pOrder));
	        templateParameters.put("order", pOrder);
	        List purchaserNameFields = new ArrayList();
	        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
	        
	        templateParameters.put(MFFConstants.CONST_BOPIS_SALUTATION, pOrder.getBopisPerson());
	        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
	        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
	                
	        TemplateEmailInfoImpl template = getBopisReminderToPickupEmailTemplate();
	        if (null != template) {
	          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
	          template.copyPropertiesTo(templateEmail);
	          
	          templateEmail.setTemplateParameters(templateParameters);
	          Object[] recepientAlternate = { alternateEmail };
	          try {

	  				getTemplateEmailSender().sendEmailMessage(templateEmail, recepientAlternate, isSendEmailInSeparateThread(), isPersistEmails());

		  		} catch (TemplateEmailException e) {
		  			vlogError("sendBopisReminderToAlternateEmail(): TemplateEmailException occurred while sending Reminder to alternate: " + e, e);
		  		}
	          
	        } else {
	          vlogDebug("sendBopisReminderToAlternateEmail(): Template Email Info is null, please check components configuration.");
	        }
	    	
	   } else {
		      vlogDebug("sendBopisReminderToAlternateEmail(): alternateEmail is blank");
		}
		
	}

  public void sendResetPasswordMail(String pEmail, String pResetToken) {
    vlogDebug("email for sending reset password -- {0}", pEmail);
    if (!StringUtils.isBlank(pEmail)) {
      try {
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        templateParameters.put("profile", fetchProfileFromEmail(pEmail));
        templateParameters.put("resetToken", pResetToken);
        vlogDebug(pEmail);
        TemplateEmailInfoImpl template = getPasswordResetEmailTemplate();
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          Object[] recepients = { pEmail };

          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
        } else {
          vlogError("Template Email Info is null, please check components configuration.");
        }

      } catch (TemplateEmailException e) {
        vlogError("TemplateEmailException occurred while sending order alternate pickup email :: " + e.getMessage());
      }
    } else {
      vlogError("email is blank");
    }
  }

  public void sendOrderPickUpConfirmationEmail(MFFOrderImpl pOrder) {
    String email = pOrder.getContactEmail();
    if (!StringUtils.isBlank(email)) {
      try {
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
        templateParameters.put("profile", profile);
        templateParameters.put("order", pOrder);
        List purchaserNameFields = new ArrayList();
        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
        
        vlogDebug(email);
        TemplateEmailInfoImpl template = getBopisOrderPickupConfirmationEmailTemplate();
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.setMessageSubject("Your Fleet Farm Order #" +pOrder.getOrderNumber() +" Has Been Picked Up!");
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          Object[] recepients = { email };
          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
        } else {
          vlogDebug("Template Email Info is null, please check components configuration.");
        }

      } catch (TemplateEmailException e) {
        vlogError("TemplateEmailException occurred while sending order cancellation email :: " + e.getMessage());
      }
    } else {
      vlogDebug("email is blank");
    }
  }

  protected RepositoryItem fetchProfileFromEmail(String pEmail) {
    return getProfileTools().getItemFromEmail(pEmail);
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

  protected RepositoryItem fetchStoreFromOrder(MFFOrderImpl pOrder) {
    RepositoryItem lStoreItem = null;
    try {
      lStoreItem = getStoreLocatorTools().getStoreLocatorRepository().getItem(pOrder.getBopisStore(), "store");
    } catch (RepositoryException e) {
      vlogError("RepositoryException occurred while fetching store for order[" + pOrder.getId() + "] :: " + e.getMessage());
    }
    return lStoreItem;
  }
  
  @SuppressWarnings("unchecked")
	protected String getCancellationType(MFFOrderImpl pOrder) {
		boolean hasCancelled = false;
		boolean hasNonCancelled = false;

		List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
		for (CommerceItem lCommerceItem : lCommerceItems) {
			if (null != lCommerceItem) {
				if (lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.CANCELLED)) {
					hasCancelled = true;
				} else {
					hasNonCancelled = true;
				}
			}
		}
		
		vlogDebug("hasCancelled: " + hasCancelled);
		vlogDebug("hasNonCancelled: " + hasNonCancelled);
		
		if (hasCancelled && hasNonCancelled) {
			return MFFConstants.CONST_ORDER_CANCEL_PARTIAL;
		} else if (hasCancelled && !hasNonCancelled) {
			return MFFConstants.CONST_ORDER_CANCEL_FULL;
		} else {
			return MFFConstants.CONST_ORDER_CANCEL_INVALID;
		}
		
	}
  
  protected String getMessageSubjectType(String pCancellationType,String pOrderNumber){
    StringBuffer sb = new StringBuffer();
    sb.append("Order #");
    sb.append(pOrderNumber);
    sb.append(": ");
    if (MFFConstants.CONST_ORDER_CANCEL_PARTIAL.equalsIgnoreCase(pCancellationType)){
      sb.append(getPartialCancellationSubject());
    } else {
      sb.append(getFullCancellationSubject());
    }
    
    return sb.toString();
  }

  public void sendContactUsEmail(String pToAddr, String pFromAddr, Map<String, Object> pTemplateParameters, String pSubect) {
    String email = pToAddr;
    if (!StringUtils.isBlank(email)) {
      try {
        vlogDebug(email);
        TemplateEmailInfoImpl template = getContactUsEmailTemplate();
        
        if (null != template) {
          template.setMessageFrom(pFromAddr);
          template.setMessageSubject(pSubect);
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(pTemplateParameters);
          Object[] recepients = { email };
          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
        } else {
          vlogDebug("Template Email Info is null, please check components configuration.");
        }
      } catch (TemplateEmailException e) {
        logError("TemplateEmailException occurred while sending account creationn email :: " + e.getMessage());
      }
    } else {
      vlogDebug("email is blank");
    }
  }

  public void sendReturnConfirmationEmail(ReturnRequest pReturnRequest) {
    MFFOrderImpl lOrder;
    if(pReturnRequest.getOriginatingOrder()!=null) {
      lOrder = (MFFOrderImpl) pReturnRequest.getOriginatingOrder();
    }else {
      lOrder = (MFFOrderImpl) pReturnRequest.getOrder();
    }
    String email = lOrder.getContactEmail();
    if (StringUtils.isNotBlank(email)) {
      try {
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(lOrder);
        templateParameters.put("profile", profile);
        templateParameters.put("order", lOrder);
        templateParameters.put("returnRequest", pReturnRequest);
        
        List purchaserNameFields = new ArrayList();
        fillPurchaserNameFields(profile, lOrder, purchaserNameFields);
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
        
        vlogDebug("sendReturnConfirmationEmail(): email: " + email);
        TemplateEmailInfoImpl template = getReturnRecievedEmailTemplate();
        if (null != template) {
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          template.setMessageSubject("Order #" +lOrder.getOrderNumber() +": Your Refund Has Been Processed");
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          Object[] recepients = { email };
          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
        } else {
          vlogDebug("sendReturnConfirmationEmail(): Template Email Info is null, please check components configuration.");
        }

      } catch (TemplateEmailException e) {
        vlogError("TemplateEmailException occurred while sending order return confirmation email :: " + e.getMessage());
      }
    } else {
      vlogDebug("sendReturnConfirmationEmail(): email is blank");
    }

  }
  
  public void sendAppeasementEmail(MFFOrderImpl pOrder,Appeasement pAppeasement ){
    
    String email = pOrder.getContactEmail();
    
    if (!StringUtils.isBlank(email)) {
      try{
        Map<String, Object> templateParameters = new HashMap<String, Object>();
        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
        
        templateParameters.put("profile", profile);
        templateParameters.put("order", pOrder);
        templateParameters.put("appeasement", pAppeasement);
        vlogDebug("Contact Email : {0}",email);
        
        List purchaserNameFields = new ArrayList();
        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
        
        TemplateEmailInfoImpl template = getAppeasementEmailTemplate();
        if (template != null) {
          
          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
          //template.setMessageSubject("Order #" +lOrder.getOrderNumber() +": Your Refund Has Been Processed");
          template.copyPropertiesTo(templateEmail);
          templateEmail.setTemplateParameters(templateParameters);
          Object[] recepients = { email };
          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
          
        } else {
          vlogDebug("sendAppeasementEmail: Template Email Info is null, please check components configuration.");
        }
      }catch (TemplateEmailException e) {
        logError("TemplateEmailException occurred while sending appeasement email : " + e.getMessage());
      }
     
    
    } else {
      vlogDebug("sendAppeasementEmail: email is blank");
    }
  }
  
  public void sendAppeasementEmail(MFFOrderImpl pOrder,Appeasement pAppeasement,String overRideEmailAddress ){
	    
	    String email = pOrder.getContactEmail();
	    
	    if(!StringUtils.isBlank(overRideEmailAddress)){
	    	email=overRideEmailAddress;
	    }
	    if (!StringUtils.isBlank(email)) {
	      try{
	        Map<String, Object> templateParameters = new HashMap<String, Object>();
	        RepositoryItem profile = (RepositoryItem) fetchProfileFromOrder(pOrder);
	        
	        templateParameters.put("profile", profile);
	        templateParameters.put("order", pOrder);
	        templateParameters.put("appeasement", pAppeasement);
	        vlogDebug("Contact Email : {0}",email);
	        
	        List purchaserNameFields = new ArrayList();
	        fillPurchaserNameFields(profile, pOrder, purchaserNameFields);
	        templateParameters.put(MFFConstants.CONST_PURCHASER_F_NAME, purchaserNameFields.get(0));
	        templateParameters.put(MFFConstants.CONST_PURCHASER_L_NAME, purchaserNameFields.get(1));
	        
	        TemplateEmailInfoImpl template = getAppeasementEmailTemplate();
	        if (template != null) {
	          
	          TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
	          //template.setMessageSubject("Order #" +lOrder.getOrderNumber() +": Your Refund Has Been Processed");
	          template.copyPropertiesTo(templateEmail);
	          templateEmail.setTemplateParameters(templateParameters);
	          Object[] recepients = { email };
	          getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
	          
	        } else {
	          vlogDebug("sendAppeasementEmail: Template Email Info is null, please check components configuration.");
	        }
	      }catch (TemplateEmailException e) {
	        logError("TemplateEmailException occurred while sending appeasement email : " + e.getMessage());
	      }
	     
	    
	    } else {
	      vlogDebug("sendAppeasementEmail: email is blank");
	    }
	  }
  
  public void sendInvoiceEmail(String pOrderNumber, File[] pFiles, String pEmailAddress ){
    try{
      TemplateEmailInfoImpl template = getInvoiceEmailTemplate();
      if (template != null) {
        
        TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
        template.copyPropertiesTo(templateEmail);
        templateEmail.setMessageAttachments(pFiles);
        templateEmail.setMessageSubject(template.getMessageSubject() +" " +pOrderNumber);
        Object[] recepients = { pEmailAddress };
        getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, false, isPersistEmails());
        
      } else {
        vlogDebug("sendInvoiceEmail: Template Email Info is null, please check components configuration.");
      }
    }catch (TemplateEmailException e) {
      logError("TemplateEmailException occurred while sending Invoice email : " + e.getMessage());
    }
  }
  
	protected void fillPurchaserNameFields (RepositoryItem pProfile, MFFOrderImpl pOrder, List pPurchaserName) {
		
		String firstName = null;

		// 2505 - Display card holder name in all emails
		if(pOrder != null & pOrder.isSignatureRequired()) {
			List paymentGroups = pOrder.getPaymentGroups();

			PaymentGroup pg = null;
			String nameOnCard=null;
			for (int i = 0; i < paymentGroups.size(); i++) {
				pg = (PaymentGroup) paymentGroups.get(i);
				if (pg instanceof CreditCard) {
					vlogDebug("fillPurchaserNameFields(): Found credit card pg.");
					nameOnCard=((AciCreditCard) pg).getNameOnCard();
					if (StringUtils.isNotBlank(nameOnCard)) {
						pPurchaserName.add(0, nameOnCard);
						pPurchaserName.add(1, "");
						return;
					}
				}
			}			
		}
		
		if (pProfile != null) {
			firstName = (String) pProfile.getPropertyValue("firstName");
			if (StringUtils.isNotBlank(firstName)) {
				vlogDebug("fillPurchaserNameFields(): Using name from profile.");
				pPurchaserName.add(0, firstName);
				pPurchaserName.add(1, (String) pProfile.getPropertyValue("lastName"));
				return;
			}
		}

		List paymentGroups = pOrder.getPaymentGroups();

		PaymentGroup pg = null;
		for (int i = 0; i < paymentGroups.size(); i++) {
			pg = (PaymentGroup) paymentGroups.get(i);
			if (pg instanceof CreditCard) {
				vlogDebug("fillPurchaserNameFields(): Found credit card pg.");
				Address billingAddr = ((CreditCard) pg).getBillingAddress();
				if (billingAddr!=null){
					firstName = billingAddr.getFirstName();
					if (StringUtils.isNotBlank(firstName)) {
						vlogDebug("fillPurchaserNameFields(): Using name from billing address.");
						pPurchaserName.add(0, firstName);
						pPurchaserName.add(1, billingAddr.getLastName());
						return;
					}
				}
			}
		}
		
		//This case occurs for GC Only Guest BOPIS order.
		if (pOrder.isBopisOrder()) {
			vlogDebug("fillPurchaserNameFields(): GC Only payment BOPIS Guest order, Hence using Bopis Pickup person name.");
			pPurchaserName.add(0, pOrder.getBopisPerson());
			pPurchaserName.add(1, "");
			return;
		}
		
		List shippingGroups = pOrder.getShippingGroups();
		for (Object shippingGroup : shippingGroups) {
          if(shippingGroup instanceof HardgoodShippingGroup){
            Address shippingAddress = ((HardgoodShippingGroup)shippingGroup).getShippingAddress();
            if (shippingAddress!=null){
				firstName = shippingAddress.getFirstName();
				if (StringUtils.isNotBlank(firstName)) {
					vlogDebug("fillPurchaserNameFields(): Using name from shipping address.");
					pPurchaserName.add(0, firstName);
					pPurchaserName.add(1, shippingAddress.getLastName());
					return;
				}
			}
          }
        }
	}
	
	
	
	public void sendBopisReminder2Email(MFFOrderImpl pOrder) {
	  sendBopisReminderEmail(pOrder);
	}
  /**
   * @return the sendEmailInSeparateThread
   */
  public boolean isSendEmailInSeparateThread() {
    return mSendEmailInSeparateThread;
  }

  /**
   * @param pSendEmailInSeparateThread
   *          the sendEmailInSeparateThread to set
   */
  public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
    mSendEmailInSeparateThread = pSendEmailInSeparateThread;
  }

  /**
   * @return the persistEmails
   */
  public boolean isPersistEmails() {
    return mPersistEmails;
  }

  /**
   * @param pPersistEmails
   *          the persistEmails to set
   */
  public void setPersistEmails(boolean pPersistEmails) {
    mPersistEmails = pPersistEmails;
  }

  /**
   * @return the defaultMessageFrom
   */
  public String getDefaultMessageFrom() {
    return mDefaultMessageFrom;
  }

  /**
   * @param pDefaultMessageFrom
   *          the defaultMessageFrom to set
   */
  public void setDefaultMessageFrom(String pDefaultMessageFrom) {
    mDefaultMessageFrom = pDefaultMessageFrom;
  }

  public String getFFLCCEmail() {
    return mFFLCCEmail;
  }

  public void setFFLCCEmail(String pFFLCCEmail) {
    mFFLCCEmail = pFFLCCEmail;
  }

  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }

  public void setProfileTools(MFFProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  public StoreLocatorTools getStoreLocatorTools() {
    return mStoreLocatorTools;
  }

  public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
    mStoreLocatorTools = pStoreLocatorTools;
  }

  /**
   * @return the defaultMessageSubject
   */
  public String getDefaultMessageSubject() {
    return mDefaultMessageSubject;
  }

  /**
   * @param pDefaultMessageSubject
   *          the defaultMessageSubject to set
   */
  public void setDefaultMessageSubject(String pDefaultMessageSubject) {
    mDefaultMessageSubject = pDefaultMessageSubject;
  }

  /**
   * @return the defaultEmailTemplate
   */
  public TemplateEmailInfoImpl getDefaultEmailTemplate() {
    return mDefaultEmailTemplate;
  }

  /**
   * @param pDefaultEmailTemplate
   *          the defaultEmailTemplate to set
   */
  public void setDefaultEmailTemplate(TemplateEmailInfoImpl pDefaultEmailTemplate) {
    mDefaultEmailTemplate = pDefaultEmailTemplate;
  }

  /**
   * @return the templateEmailSender
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }

  /**
   * @param pTemplateEmailSender
   *          the templateEmailSender to set
   */
  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    mTemplateEmailSender = pTemplateEmailSender;
  }

  /**
   * @return the accountCreationEmailTemplate
   */
  public TemplateEmailInfoImpl getAccountCreationEmailTemplate() {
    return mAccountCreationEmailTemplate;
  }

  /**
   * @param pAccountCreationEmailTemplate
   *          the accountCreationEmailTemplate to set
   */
  public void setAccountCreationEmailTemplate(TemplateEmailInfoImpl pAccountCreationEmailTemplate) {
    mAccountCreationEmailTemplate = pAccountCreationEmailTemplate;
  }

  /**
   * @return the emailAFriendEmailTemplate
   */
  public TemplateEmailInfoImpl getEmailAFriendEmailTemplate() {
    return mEmailAFriendEmailTemplate;
  }

  /**
   * @param pEmailAFriendEmailTemplate
   *          the emailAFriendEmailTemplate to set
   */
  public void setEmailAFriendEmailTemplate(TemplateEmailInfoImpl pEmailAFriendEmailTemplate) {
    mEmailAFriendEmailTemplate = pEmailAFriendEmailTemplate;
  }

  public TemplateEmailInfoImpl getShareWishListEmailTemplate() {
    return mShareWishListEmailTemplate;
  }

  public void setShareWishListEmailTemplate(TemplateEmailInfoImpl pShareWishListEmailTemplate) {
    this.mShareWishListEmailTemplate = pShareWishListEmailTemplate;
  }

  /**
   * @return the backinStockEmailTemplate
   */
  public TemplateEmailInfoImpl getBackinStockEmailTemplate() {
    return mBackinStockEmailTemplate;
  }

  /**
   * @param pBackinStockEmailTemplate
   *          the backinStockEmailTemplate to set
   */
  public void setBackinStockEmailTemplate(TemplateEmailInfoImpl pBackinStockEmailTemplate) {
    mBackinStockEmailTemplate = pBackinStockEmailTemplate;
  }

  public TemplateEmailInfoImpl getEmailUpdateEmailTemplate() {
    return mEmailUpdateEmailTemplate;
  }

  public void setEmailUpdateEmailTemplate(TemplateEmailInfoImpl pEmailUpdateEmailTemplate) {
    mEmailUpdateEmailTemplate = pEmailUpdateEmailTemplate;
  }

  public TemplateEmailInfoImpl getPasswordUpdateEmailTemplate() {
    return mPasswordUpdateEmailTemplate;
  }

  public void setPasswordUpdateEmailTemplate(TemplateEmailInfoImpl pPasswordUpdateEmailTemplate) {
    mPasswordUpdateEmailTemplate = pPasswordUpdateEmailTemplate;
  }

  public TemplateEmailInfoImpl getOrderConfirmationEmailTemplate() {
    return mOrderConfirmationEmailTemplate;
  }

  public void setOrderConfirmationEmailTemplate(TemplateEmailInfoImpl pOrderConfirmationEmailTemplate) {
    mOrderConfirmationEmailTemplate = pOrderConfirmationEmailTemplate;
  }

  public TemplateEmailInfoImpl getOrderCancellationEmailTemplate() {
    return mOrderCancellationEmailTemplate;
  }

  public void setOrderCancellationEmailTemplate(TemplateEmailInfoImpl pOrderCancellationEmailTemplate) {
    mOrderCancellationEmailTemplate = pOrderCancellationEmailTemplate;
  }

  public TemplateEmailInfoImpl getBopisOrderConfirmationEmailTemplate() {
    return mBopisOrderConfirmationEmailTemplate;
  }

  public void setBopisOrderConfirmationEmailTemplate(TemplateEmailInfoImpl pBopisOrderConfirmationEmailTemplate) {
    mBopisOrderConfirmationEmailTemplate = pBopisOrderConfirmationEmailTemplate;
  }

  public TemplateEmailInfoImpl getBopisReadyForPickupEmailTemplate() {
    return mBopisReadyForPickupEmailTemplate;
  }

  public void setBopisReadyForPickupEmailTemplate(TemplateEmailInfoImpl pBopisReadyForPickupEmailTemplate) {
    mBopisReadyForPickupEmailTemplate = pBopisReadyForPickupEmailTemplate;
  }

  public TemplateEmailInfoImpl getBopisReminderToPickupEmailTemplate() {
    return mBopisReminderToPickupEmailTemplate;
  }

  public void setBopisReminderToPickupEmailTemplate(TemplateEmailInfoImpl pBopisReminderToPickupEmailTemplate) {
    mBopisReminderToPickupEmailTemplate = pBopisReminderToPickupEmailTemplate;
  }

  public TemplateEmailInfoImpl getBopisOrderPickupConfirmationEmailTemplate() {
    return mBopisOrderPickupConfirmationEmailTemplate;
  }

  public void setBopisOrderPickupConfirmationEmailTemplate(TemplateEmailInfoImpl pBopisOrderPickupConfirmationEmailTemplate) {
    mBopisOrderPickupConfirmationEmailTemplate = pBopisOrderPickupConfirmationEmailTemplate;
  }

  public TemplateEmailInfoImpl getBopisEmailToAlternateEmailTemplate() {
    return mBopisEmailToAlternateEmailTemplate;
  }

  public void setBopisEmailToAlternateEmailTemplate(TemplateEmailInfoImpl pBopisEmailToAlternateEmailTemplate) {
    mBopisEmailToAlternateEmailTemplate = pBopisEmailToAlternateEmailTemplate;
  }

  public TemplateEmailInfoImpl getBopisCancellationEmailTemplate() {
    return mBopisCancellationEmailTemplate;
  }

  public void setBopisCancellationEmailTemplate(TemplateEmailInfoImpl pBopisCancellationEmailTemplate) {
    mBopisCancellationEmailTemplate = pBopisCancellationEmailTemplate;
  }

  public TemplateEmailInfoImpl getOrderShippedEmailTemplate() {
    return mOrderShippedEmailTemplate;
  }

  public void setOrderShippedEmailTemplate(TemplateEmailInfoImpl pOrderShippedEmailTemplate) {
    mOrderShippedEmailTemplate = pOrderShippedEmailTemplate;
  }

  public TemplateEmailInfoImpl getFflConfirmationEmailTemplate() {
    return mFFLConfirmationEmailTemplate;
  }

  public void setFflConfirmationEmailTemplate(TemplateEmailInfoImpl pFFLConfirmationEmailTemplate) {
    mFFLConfirmationEmailTemplate = pFFLConfirmationEmailTemplate;
  }

  public TemplateEmailInfoImpl getContactUsEmailTemplate() {
    return mContactUsEmailTemplate;
  }

  public void setContactUsEmailTemplate(TemplateEmailInfoImpl pContactUsEmailTemplate) {
    mContactUsEmailTemplate = pContactUsEmailTemplate;
  }

  public TemplateEmailInfoImpl getPasswordResetEmailTemplate() {
    return mPasswordResetEmailTemplate;
  }

  public void setPasswordResetEmailTemplate(TemplateEmailInfoImpl pPasswordResetEmailTemplate) {
    mPasswordResetEmailTemplate = pPasswordResetEmailTemplate;
  }

  public String getDefaultContactUsEmailTo() {
    return defaultContactUsEmailTo;
  }

  public void setDefaultContactUsEmailTo(String pDefaultContactUsEmailTo) {
    defaultContactUsEmailTo = pDefaultContactUsEmailTo;
  }

  public TemplateEmailInfoImpl getReturnRecievedEmailTemplate() {
    return mReturnRecievedEmailTemplate;
  }

  public void setReturnRecievedEmailTemplate(TemplateEmailInfoImpl pReturnRecievedEmailTemplate) {
    mReturnRecievedEmailTemplate = pReturnRecievedEmailTemplate;
  }

  public TemplateEmailInfoImpl getInvoiceEmailTemplate() {
    return mInvoiceEmailTemplate;
  }

  public void setInvoiceEmailTemplate(TemplateEmailInfoImpl pInvoiceEmailTemplate) {
    mInvoiceEmailTemplate = pInvoiceEmailTemplate;
  }
  public Map<String, String> getContactUsEmailMap() {
    return contactUsEmailMap;
  }

  public void setContactUsEmailMap(Map<String, String> pContactUsEmailMap) {
    contactUsEmailMap = pContactUsEmailMap;
  }

  public String getTestOrderId() {
 	return mTestOrderId;
  }

  public void setTestOrderId(String pTestOrderId) {
	mTestOrderId = pTestOrderId;
  }

  public String getPartialCancellationSubject() {
	return mPartialCancellationSubject;
  }

  public void setPartialCancellationSubject(String pPartialCancellationSubject) {
	  mPartialCancellationSubject = pPartialCancellationSubject;
  }

  public String getFullCancellationSubject() {
	  return mFullCancellationSubject;
  }

  public void setFullCancellationSubject(String pFullCancellationSubject) {
	  mFullCancellationSubject = pFullCancellationSubject;
  }
}
