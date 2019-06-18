package manual.order;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CreditCard;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.Relationship;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.userprofiling.ProfileTools;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.order.MFFOrderTools;
import com.mff.userprofiling.MFFProfileTools;

public class ManualBillingInfoProcessor extends GenericService {
	
	protected CatalogTools mCatalogTools;
	PipelineManager mPipelineManager;
	protected String mMoveToConfirmationChainId;
	
	/**
	 * This method Initialize and setup Credit Card Payment group for the order,
	 * if store credit is used, then all amount from the order deducted by the
	 * store credit, then by credit card.
	 * @throws CommerceException 
	 * @throws InvalidParameterException 
	 */
	@SuppressWarnings("rawtypes")
	public void setupCreditCardPaymentGroupsForOrder(MFFOrderImpl pOrder,
			RepositoryItem pProfileItem, MFFOrderManager mOrderManager) throws InvalidParameterException, CommerceException {
			
		boolean exists = false;
	    List paymentRelationships = pOrder.getPaymentGroupRelationships();
	    
	    for(Object paymentRelationship : paymentRelationships) {
	      if( ((Relationship)paymentRelationship).getRelationshipType() == RelationshipTypes.ORDERAMOUNTREMAINING ) {
	        exists = true;
	        break;
	      }
	    }

	    if(!exists) {
	      addOrderAmountRemainingToCreditPaymentGroup(pOrder, mOrderManager);
	    }
	    
		//addCreditCardDetails(getCreditCard(pOrder, mOrderManager), false, "NEW",pProfileItem, Locale.getDefault());
	
		addCreditCardAuthorizationNumber(pOrder,"123", mOrderManager);
	}
	
	public void addCreditCardAuthorizationNumber(Order pOrder,
			String pCreditCardVerificationNumber, MFFOrderManager mOrderManager) {

		setCreditCardVerificationNumber(pOrder, pCreditCardVerificationNumber);
	}
	
	public void setCreditCardVerificationNumber(Order pOrder, String pVerificationNumber) {
	    CreditCard creditCard = getCreditCard(pOrder);
	    creditCard.setCardVerificationNumber(pVerificationNumber);
	  }
	
	@SuppressWarnings("rawtypes")
	public CreditCard getCreditCard(Order pOrder) {
	    if (pOrder == null) {
	      if (isLoggingDebug()) {
	        logDebug("Null order passed to getCreditCard method.");
	      }

	      return null;
	    }

	    List paymentGroups = pOrder.getPaymentGroups();

	    if (paymentGroups == null) {
	      if (isLoggingDebug()) {
	        logDebug("Order has null list of payment groups.");
	      }

	      return null;
	    }

	    int numPayGroups = paymentGroups.size();

	    if (numPayGroups == 0) {
	      if (isLoggingWarning()) {
	        logWarning("No payment group on this order!");
	      }

	      return null;
	    }

	    PaymentGroup pg = null;

	    // We are only supporting a single credit card payment group. Return the first one we get.
	    for (int i = 0; i < numPayGroups; i++) {
	      pg = (PaymentGroup) paymentGroups.get(i);

	      if (pg instanceof CreditCard) {
	        return (CreditCard) pg;
	      }
	    }

	    return null;
	  }
	
	public void addOrderAmountRemainingToCreditPaymentGroup(Order pOrder, MFFOrderManager mOrderManager)
		    throws CommerceException,InvalidParameterException {

		    CreditCard creditCard = getCreditCard(pOrder);
		    double orderRemainingAmount = getOrderRemaningAmount(pOrder,mOrderManager);
		    if( orderRemainingAmount <= 0 ) {
		      try {

		    	  mOrderManager.removeRemainingOrderAmountFromPaymentGroup(pOrder, creditCard.getId());
		      } catch (RelationshipNotFoundException exc) {
		        if (isLoggingDebug()) {
		          logDebug("Credit Card RelationShip not found:");
		        }
		       return;
		      }
		    } else {
		      try {
		        
		        creditCard.getOrderRelationship();

		      } catch (RelationshipNotFoundException exc) {

		    	  mOrderManager.addRemainingOrderAmountToPaymentGroup(pOrder, creditCard.getId());

		      } catch (InvalidParameterException exc) {

		        throw exc;
		      }
		    }
		  }
	
	
	
	public double getOrderRemaningAmount(Order pOrder,MFFOrderManager mOrderManager) throws CommerceException {

	    Order order = pOrder;

	    PricingTools pricingTools = mOrderManager.getOrderTools().getProfileTools().getPricingTools();
	    double usedStoreCreditAmount = 0;
	    double orderTotal = order.getPriceInfo().getTotal();
	    double orderReminingAmount;
	    
	    orderReminingAmount = pricingTools.round(orderTotal - usedStoreCreditAmount);

	    if (isLoggingDebug()) {
	      logDebug("Total Amount of StoreCredits PaymentGroup = " + usedStoreCreditAmount);
	      logDebug("Order Remaning Amount = " + orderReminingAmount);
	    }

	    return orderReminingAmount;
	  }

	/**
	 * Run the pipeline.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public void runProcessMoveToConfirmation(MFFOrderImpl pOrder,
			RepositoryItem pProfileItem,MFFOrderManager mOrderManager) throws RunProcessException {

		HashMap params = new HashMap(11);

		params.put(PipelineConstants.CATALOGTOOLS, getCatalogTools());
		params.put(PipelineConstants.INVENTORYMANAGER, mOrderManager
				.getOrderTools().getInventoryManager());

		PipelineResult result = runProcess(getMoveToConfirmationChainId(),
				pOrder, null, Locale.getDefault(), pProfileItem,
				params, mOrderManager);

		//processPipelineErrors(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected PipelineResult runProcess(String pChainId, Order pOrder,
			PricingModelHolder pPricingModels, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters,
			MFFOrderManager mOrderManager) throws RunProcessException {
		
	        if(pChainId == null)
	        {
	            if(isLoggingDebug())
	                logDebug("runProcess skipped because chain ID is null");
	            return null;
	        }
	        Map params = new HashMap();
	        
	        try
	        {
	            OrderTools orderTools = mOrderManager.getOrderTools();
	            params.put("Order", pOrder);
	            params.put("PricingModels", pPricingModels);
	            params.put("Locale", pLocale);
	            params.put("Profile", pProfile);
	            params.put("OrderManager", mOrderManager);
	            params.put("CatalogTools", orderTools.getCatalogTools());
	            params.put("InventoryManager", orderTools.getInventoryManager());
	            params.put("ExtraParameters", pExtraParameters);
	            return runProcess(pChainId, params);
	        }
	        catch(RunProcessException exc)
	        {
	            throw exc;
	        }
	    }

	    @SuppressWarnings("rawtypes")
		protected PipelineResult runProcess(String pChainId, Map pParameters)
	        throws RunProcessException
	    {
	        if(isLoggingDebug())
	            logDebug((new StringBuilder()).append("runProcess called with chain ID = ").append(pChainId).toString());
	        if(pChainId == null)
	            return null;
	        try
	        {
	            return getPipelineManager().runProcess(pChainId, pParameters);
	        }
	        catch(RunProcessException exc) {
	        	throw exc;
	        }
	    }

	/**
	 * Move to confirmation using new billing address and new credit card info.
	 * @throws CommerceException 
	 * @throws InvalidParameterException 
	 */
	public void processBillingWithNewAddressAndNewCard(MFFOrderImpl pOrder,
			RepositoryItem pProfileItem, MFFOrderManager mOrderManager)
			throws InvalidParameterException, CommerceException {

		synchronized (pOrder) {

			preBillingWithNewAddressAndNewCard(pOrder, pProfileItem,
					mOrderManager);
			billingWithNewAddressAndNewCard(pOrder, pProfileItem, mOrderManager);
		}

		postBillingWithNewAddressAndNewCard(pOrder, pProfileItem, mOrderManager);

	}

	/**
	 * Setup credit card payment group and validate user input
	 * @throws CommerceException 
	 * @throws InvalidParameterException 
	 */
	protected void preBillingWithNewAddressAndNewCard(MFFOrderImpl pOrder, RepositoryItem pProfileItem,
								MFFOrderManager mOrderManager) throws InvalidParameterException, CommerceException {

		fillCreditCardFieldsWithUserInput(pOrder, mOrderManager);

		setupCreditCardPaymentGroupsForOrder(pOrder, pProfileItem, mOrderManager);

		// addCreditCardBillingAddress(pOrder, pProfileItem, mOrderManager);
	}

	/**
	 * Prefill credit card details from the fields user input
	 * 
	 * @param pUsingNewAddress
	 *            if new address should be used as credit card billing address
	 */
	private void fillCreditCardFieldsWithUserInput(MFFOrderImpl pOrder,
			MFFOrderManager mOrderManager) {

		CreditCard card = (CreditCard) getCreditCard(pOrder);

		card.setCreditCardNumber("1111");
		card.setCreditCardType("visa");

		card.setExpirationMonth("12");
		card.setExpirationYear("19");

		ContactInfo creditCardBillingAddress = new ContactInfo();

		creditCardBillingAddress.setFirstName("billing first name");
		creditCardBillingAddress.setLastName("billing last name");

		creditCardBillingAddress.setAddress1("3035 W Wisconsin Ave");
		creditCardBillingAddress.setAddress2("");
		creditCardBillingAddress.setAddress3("");
		creditCardBillingAddress.setCity("Appleton");

		creditCardBillingAddress.setState("WI");
		creditCardBillingAddress.setPostalCode("54914");

		creditCardBillingAddress.setCountry("US");
		creditCardBillingAddress.setPhoneNumber("920-734-8231");

		card.setBillingAddress(creditCardBillingAddress);
	}
	
	/**
	 * Run 'move to confirmation' pipeline chain and update order.
	 */
	protected void billingWithNewAddressAndNewCard(MFFOrderImpl pOrder,RepositoryItem pProfileItem, MFFOrderManager mOrderManager) {
		
		try {
			//runProcessMoveToConfirmation(pOrder,pProfileItem,mOrderManager);
		} catch (Exception exc) {
			if (isLoggingError()) {
				logError("Error in billingWithNewAddressAndNewCard: ", exc);
			}
		}
	}

	/**
	 * Add credit card to user profile, update checkout level.
	 */
	protected void postBillingWithNewAddressAndNewCard(MFFOrderImpl pOrder, RepositoryItem pProfileItem, MFFOrderManager mOrderManager) {

		// Below code is from addCreditCardToProfile();
		CreditCard card = getCreditCard(pOrder);
		//saveCreditCardToProfile(pOrder, pProfileItem,"conversion_card", mOrderManager);
		
		saveBillingAddressToProfile(pOrder, pProfileItem, mOrderManager);

		// Make sure user has a default billing address. Use this one
		// if current billingAddress is empty.
		saveDefaultBillingAddress(pProfileItem, card, mOrderManager);
	}
	
	/**
	   * This method saves the Billing Address to the profile.
	   * @param pOrder the oder
	   * @param pProfile the profile
	   */
	  @SuppressWarnings("rawtypes")
	public void saveBillingAddressToProfile(Order pOrder, RepositoryItem pProfile, MFFOrderManager mOrderManager) {
		  
		// Save the billing info as a possible shipping address
	    MFFOrderTools orderTools = (MFFOrderTools) mOrderManager.getOrderTools();
	    MFFProfileTools profileTools = (MFFProfileTools) orderTools.getProfileTools();
	    CreditCard card = getCreditCard(pOrder);

	    CommercePropertyManager cpmgr = (CommercePropertyManager) profileTools.getPropertyManager();
		Map secondaryAddressMap = (Map)pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
		
		String billingAddressLine1 = card.getBillingAddress().getAddress1();
		logDebug(" saveBillingAddressToProfile(): addressLine1 : " + billingAddressLine1);
		  
		if (!secondaryAddressMap.containsKey(billingAddressLine1)) {
			
			logDebug(" saveBillingAddressToProfile(): NO address in profile matching with " +
					"billing addressLine1, hence saving address to profile with addressLine1: " + billingAddressLine1);
			
			String billingAddressNickname = profileTools.getUniqueShippingAddressNickname(card.getBillingAddress(), pProfile, billingAddressLine1);

			logDebug(" saveBillingAddressToProfile(): billingAddressNickname: " + billingAddressNickname);

			//orderTools.saveAddressToAddressBook(pProfile, card.getBillingAddress(), billingAddressNickname);
			
		} else {
			logDebug(" saveBillingAddressToProfile(): Billing address existing in profile with " +
					"addressLine1: " + billingAddressLine1 + " , hence not saving this address to profile.");
		}
	  }
	
	public void saveDefaultBillingAddress(RepositoryItem pProfile, CreditCard pCreditCard, MFFOrderManager mOrderManager) {
	    MFFOrderTools orderTools = (MFFOrderTools) mOrderManager.getOrderTools();
	    MFFProfileTools profileTools = (MFFProfileTools) orderTools.getProfileTools();
	    CommercePropertyManager pm = (CommercePropertyManager) profileTools.getPropertyManager();

	    if (pProfile.getPropertyValue(pm.getBillingAddressPropertyName()) == null) {
	      if (pCreditCard != null) {
	        if (isLoggingDebug()) {
	          logDebug("Saving default billing address.");
	        }

	        this.saveAddressToDefaultBilling(pCreditCard.getBillingAddress(), pProfile,mOrderManager);
	      }
	    }
	  }
	
	public boolean applyPaymentGroupToOrder(MFFOrderImpl pOrder,
			RepositoryItem pProfileItem, MFFOrderManager mOrderManager,
			TransactionManager transactionManager) throws CommerceException {

		Transaction transaction = null;

		try {

			transaction = transactionManager.getTransaction();
			if (transaction == null) {
				transactionManager.begin();
			}

			CreditCard cc = this.getCreditCard(pOrder);
			logDebug("applyPaymentGroupToOrder: Credit card: " + cc);
			mOrderManager.addRemainingOrderAmountToPaymentGroup(pOrder, cc.getId());

			pOrder.updateVersion();
			mOrderManager.updateOrder(pOrder);

		} catch (NotSupportedException e) {

		} catch (SystemException e) {

		} finally {
			if (transactionManager != null) {
				try {
					transactionManager.commit();
				} catch (SecurityException e) {

				} catch (IllegalStateException e) {

				} catch (RollbackException e) {

				} catch (HeuristicMixedException e) {

				} catch (HeuristicRollbackException e) {

				} catch (SystemException e) {

				}
			}
		}

		return true;
	}
		
	
	@SuppressWarnings("static-access")
	public boolean saveAddressToDefaultBilling(Address pAddress, RepositoryItem pProfile, MFFOrderManager mOrderManager) {
	    MutableRepositoryItem address = null;
	    MutableRepository profileRepository = (MutableRepository) pProfile.getRepository();
	    
	    ProfileTools profileTools = (ProfileTools)mOrderManager.getOrderTools().getProfileTools();
	    CommercePropertyManager pm = (CommercePropertyManager) profileTools.getPropertyManager();

	    // Create a new address and store it in address book.
	    try {
	      // Create an Address item.
	      address = profileRepository.createItem(pm.getContactInfoItemDescriptorName());
	      MFFOrderTools orderTools = (MFFOrderTools) mOrderManager.getOrderTools();
	      orderTools.copyAddress(pAddress, address);
	      profileRepository.addItem(address);
	      
	      // Update the profile with the billing address.
	      profileTools.updateProperty(pm.getBillingAddressPropertyName(), address, pProfile);
	    } 
	    catch (RepositoryException re) {
	      if (isLoggingError()) {
	        logError("Error creating new address object: ", re);
	      }
	      return false;
	    } 
	    catch (CommerceException ce) {
	      if (isLoggingError()) {
	        logError("Error copying address to address book: ", ce);
	      }
	      return false;
	    }

	    return true;
	  }
	
	/**
	   * Verify, whether Order Relationship exist in the given Payment Group or not.
	   * @param pPaymentGroup
	   * @return true if relation ship found else return false
	   */
	  public boolean isPaymentGroupOrderRelationShipExist(PaymentGroup pPaymentGroup) {
	    try {


	      pPaymentGroup.getOrderRelationship();
	    } catch (RelationshipNotFoundException rnfexc) {
	      if (isLoggingDebug()) {
	        logDebug("BillingProcessHelper: No Relationship Found for credit card" +
	                " is paying with a new Credit card.");
	        logDebug(rnfexc);
	      }

	      return false;

	    }  catch (InvalidParameterException exc) {

	      if (isLoggingDebug()) {
	        logDebug("BillingProcessHelper: "+ exc);
	      }

	      return false;
	    }
	    return true;
	  }

	public String getMoveToConfirmationChainId() {
		return mMoveToConfirmationChainId;
	}

	public void setMoveToConfirmationChainId(String pMoveToConfirmationChainId) {
		mMoveToConfirmationChainId = pMoveToConfirmationChainId;
	}

	public CatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(CatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	public void setPipelineManager(PipelineManager pPipelineManager) {
		mPipelineManager = pPipelineManager;
	}

	public PipelineManager getPipelineManager() {
		return mPipelineManager;
	}
}