package com.mff.userprofiling;

import java.beans.IntrospectionException;
import java.rmi.RemoteException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderTools;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.states.StateDefinitions;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.i18n.LocaleUtils;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.NumberFormat;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;

import com.fedex.service.addressvalidation.FedexAddressValidationService;
import com.fedex.service.addressvalidation.beans.AddressValidationInput;
import com.fedex.service.addressvalidation.beans.AddressValidationOutput;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.profile.MFFPropertyManager;
import com.mff.userprofiling.util.TaxExemptionInfo;

public class MFFProfileTools extends CommerceProfileTools {
	
	  public static final String ID_PROPERTY_NAME = "ID";
	  public static final String UNKNOWN_STATE_CODE = "unknown";
	  
	  /** Resource bundle name with profile-related resources. */
	  private static final String PROFILE_RESOURCE_BUNDLE_NAME = "atg.commerce.profile.Resources";
	  
	  /** Resource name for default address name. */
	  private static final String ADDRESS_NICKNAME_PREFIX = "addressNicknamePrefix";
	  
	  private String mMaskCharacter = "X";
	  private int mNumUnmasked = 4;
	  private int mGroupingSize = 0;
	  
	  private Repository mContentRepository;
	  
	  //--------------------------------------------------
	  // property: ShippingAddressClassName
	  private String mShippingAddressClassName = "com.mff.userprofiling.MFFContactInfo";

	  /**
	   * @return the String
	   */
	  public String getShippingAddressClassName() {
	    return mShippingAddressClassName;
	  }

	  /**
	   * @param pShippingAddressClassName the String to set
	   */
	  public void setShippingAddressClassName(String pShippingAddressClassName) {
	    mShippingAddressClassName = pShippingAddressClassName;
	  }
	 
	  //--------------------------------------------------
	  // property: BillingAddressClassName
	  private String mBillingAddressClassName = "atg.core.util.ContactInfo";

	  /**
	   * @return the String
	   */
	  public String getBillingAddressClassName() {
	    return mBillingAddressClassName;
	  }

	  /**
	   * @param pBillingAddressClassName the String to set
	   */
	  public void setBillingAddressClassName(String pBillingAddressClassName) {
	    mBillingAddressClassName = pBillingAddressClassName;
	  }

	 
	  //-----------------------------------
	  // property: mCommercePropertyManager
	  /** Provides helper methods when dealing with Profiles */
	  private CommercePropertyManager mCommercePropertyManager;
	  
	  /** @return Gets mCommercePropertyManager   */
	  public CommercePropertyManager getCommercePropertyManager() {
	    return mCommercePropertyManager;
	  }  

	  /** @param pCommercePropertyManager Sets mCommercePropertyManager  */
	  public void setCommercePropertyManager(CommercePropertyManager pCommercePropertyManager) {
	    mCommercePropertyManager = pCommercePropertyManager;
	  }
	
	private String mEmailFormat;
	
	private int mMinPasswordLength;
	
	private int mMaxPasswordLength;
	
	/** Address validationService	   */
	  private FedexAddressValidationService mAddressValidationService;

	 
		/**
	 * @return the addressValidationService
	 */
	public FedexAddressValidationService getAddressValidationService() {
		return mAddressValidationService;
	}

	/**
	 * @param pAddressValidationService the addressValidationService to set
	 */
	public void setAddressValidationService(
			FedexAddressValidationService pAddressValidationService) {
		mAddressValidationService = pAddressValidationService;
	}

		/**
	 * @return the emailFormat
	 */
	public String getEmailFormat() {
		return mEmailFormat;
	}
	
	/**
	 * @param pEmailFormat the emailFormat to set
	 */
	public void setEmailFormat(String pEmailFormat) {
		mEmailFormat = pEmailFormat;
	}

	/**
	 * @return the minPasswordLength
	 */
	public int getMinPasswordLength() {
		return mMinPasswordLength;
	}

	/**
	 * @param pMinPasswordLength the minPasswordLength to set
	 */
	public void setMinPasswordLength(int pMinPasswordLength) {
		mMinPasswordLength = pMinPasswordLength;
	}

	/**
	 * @return the maxPasswordLength
	 */
	public int getMaxPasswordLength() {
		return mMaxPasswordLength;
	}

	/**
	 * @param pMaxPasswordLength the maxPasswordLength to set
	 */
	public void setMaxPasswordLength(int pMaxPasswordLength) {
		mMaxPasswordLength = pMaxPasswordLength;
	}

	/**
	   * Validates an email address for correctness.
	   *
	   * @return boolean true if email address is valid
	   * @param pEmail email address
	   */
	  public boolean validateEmailAddress(String pEmail) {
	    String regularExp = getEmailFormat();
	
	    //Set the email pattern string
	    Pattern p = Pattern.compile(regularExp);
	    //Match the given string with the pattern
	    Matcher m = p.matcher(pEmail);
	    //check whether match is found
	    return m.matches();
	  }
	  
	  public String getDefaultShippingMethod(RepositoryItem pProfile) {
		  MFFPropertyManager spm = (MFFPropertyManager) getPropertyManager();
	    String defaultShippingMethodPropertyName = spm.getDefaultShippingMethodPropertyName();
	    String shippingMethod = (String) pProfile.getPropertyValue(defaultShippingMethodPropertyName);

	    return shippingMethod;
	  }

	  /**
	   * Gets nickname for the given profile's address. 
	   * 
	   * @param pProfile The profile repository item
	   * @param pAddress Address object 
	   * @return nickname for secondary address repository item
	   */
	  public String getProfileAddressName(RepositoryItem pProfile, Address pAddress) {
	    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
	    Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
	    String nickname = null;
	    
	    for (Iterator it = secondaryAddresses.entrySet().iterator(); it.hasNext();){
	      Map.Entry addrEntry = (Map.Entry) it.next();
	      RepositoryItem address = (RepositoryItem)addrEntry.getValue();
	      if (areAddressesEqual(pAddress, address, null)){
	        nickname = (String) addrEntry.getKey();

	        if (isLoggingDebug()) {
	          logDebug("Nickname for secondary address " + pAddress + " found: " + nickname);
	        }
	        break;
	      }
	    }
	    return nickname;
	  }
	  
	  /**
	   * Gets the default shipping address nickname for a user.
	   *
	   * @param pProfile
	   *            the user profile
	   * @return the default shipping address nickname for a user.
	   */
	  public String getDefaultShippingAddressNickname(RepositoryItem pProfile) {
	    RepositoryItem defaultAddress = getDefaultShippingAddress(pProfile);    
	    return defaultAddress != null? getProfileAddressName(pProfile, defaultAddress):null;
	  }

	  /**
	   * Utility method to copy one address repoitory item to another
	   * repository item.
	   *
	   * @param pAddress - source address
	   * @param pNewAddress - target address
	   * @param pAddressIterator - address iterator
	   */
	  public void copyAddress(MutableRepositoryItem pAddress, MutableRepositoryItem pNewAddress, Iterator pAddressIterator) {
	    String propertyName;
	    Object property;

	    while (pAddressIterator.hasNext()) {
	      propertyName = (String) pAddressIterator.next();
	      property = pAddress.getPropertyValue(propertyName);
	      pNewAddress.setPropertyValue(propertyName, property);
	    }
	  }

	  /**
	   * This method creates an address object and sets the property values
	   * to values in the repository item passed in.
	   * @param pItem the repository item
	   * @return address the address object with data from repository
	   * @exception RepositoryException if there was an error when creating the new repository item.
	   */
	  public Address getAddressFromRepositoryItem(RepositoryItem pItem)
	    throws RepositoryException {
	    Address address = new ContactInfo();

	    // update item with values in address
	    try {
	      atg.commerce.order.OrderTools.copyAddress(pItem, address);
	    } catch (CommerceException ce) {
	      Throwable src = ce.getSourceException();

	      if (src instanceof RepositoryException) {
	        throw (RepositoryException) src;
	      } else {
	        throw new RepositoryException(src);
	      }
	    }

	    return address;
	  }
	  
	  
	  /**
		 * This method sets next available payment method as default, if existing default payment method is getting removed.
		 * @param pProfile
		 * @param pRemovingCardName
		 * @param pDefaultCreditCardItem
		 */
		public void setDefaultPaymentMethod(RepositoryItem pProfile, String pRemovingCardName, String pDefaultCardNickName) {
			
			vlogDebug("setDefaultPaymentMethod() : defaultCardNickName: "+ pDefaultCardNickName);

			if ((!StringUtils.isEmpty(pDefaultCardNickName) && pDefaultCardNickName.equalsIgnoreCase(pRemovingCardName))) {
				
				Map remainingCards = getUsersCreditCardMap(pProfile);
				
				if (remainingCards.size() > 0) {
					
					LinkedList sortedCards= sortByValue(remainingCards);
					vlogDebug("setDefaultPaymentMethod() : default card was removed, and there is a card avaialble on profile.");
					
					RepositoryItem availableCard = (RepositoryItem) ((Map.Entry) sortedCards.get(0)).getValue();

					vlogDebug("setDefaultPaymentMethod() : availableCard: " + availableCard);

					if (availableCard != null) {

						String availableCardNickName = getCreditCardNickname(pProfile, availableCard);
						vlogDebug("setDefaultPaymentMethod() : availableCardNickName: " + availableCardNickName);

						try {
							setDefaultCreditCard(pProfile, availableCardNickName);
							
						} catch (RepositoryException e) {
							if (isLoggingError()) {
								logError("setDefaultPaymentMethod() : Repository Exception while setting default card: " + e, e);
							}
						}
					}
				}
			}
		}
		
		/**
		 * This method sets next available address as default, if existing default address is getting removed.
		 * @param pProfile
		 * @param pRemovingAddrName
		 * @param pDefaultAddressItem
		 */
		public boolean setDefaultShippingAddress(RepositoryItem pProfile) {
			
			boolean isDefaultAddressSet = false;
			
			CommercePropertyManager cpmgr = getCommercePropertyManager();
			Map secondaryAddressMap = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
			
			logDebug("setDefaultShippingAddress: secondaryAddressMap: " + secondaryAddressMap);
			vlogDebug("setDefaultShippingAddress() : secondaryAddressMap size: " + secondaryAddressMap.size());
			
			if (secondaryAddressMap.size() > 0) {
				
				LinkedList sortedAddresses = sortByValue(secondaryAddressMap);
				
				vlogDebug("setDefaultShippingAddress: sortedAddresses: " + sortedAddresses);
				vlogDebug("setDefaultShippingAddress() : default address was removed, and there is a address avaialble on profile.");
				
				RepositoryItem availableAddr = (RepositoryItem) ((Map.Entry) sortedAddresses.get(0)).getValue();

				vlogDebug("setDefaultShippingAddress() : availableAddr: " + availableAddr);

				if (availableAddr != null) {

					String availableAddrNickName = getProfileAddressName(pProfile, availableAddr);
					vlogDebug("setDefaultShippingAddress() : availableAddrNickName: " + availableAddrNickName);

					try {
						isDefaultAddressSet = setDefaultShippingAddress(pProfile, availableAddrNickName);
						
					} catch (RepositoryException e) {
						if (isLoggingError()) {
							logError("setDefaultShippingAddress() : Repository Exception while setting default address: " + e, e);
						}
					}
				}
			}
			return isDefaultAddressSet;
		}

	  public boolean setDefaultTaxExemption(RepositoryItem pProfile, String pNickName) throws RepositoryException {
		    
		    Map taxExmpns = (Map) pProfile.getPropertyValue("taxExemptions");
	  	  	RepositoryItem taxExmpItem = (RepositoryItem) taxExmpns.get(pNickName);
	  	  	
		    updateProperty("defaultTaxExemption", taxExmpItem, pProfile);
		    return true;
	  }
	  
	  public boolean setMyHomeStore(RepositoryItem pProfile, RepositoryItem pStoreItem) throws RepositoryException {
	  	  	updateProperty("myHomeStore", pStoreItem, pProfile);
		    return true;
	  }

	  /**
	   * This implementation makes a reference to an existing address (should be located in a <code>secondaryAddresses</code> set)
	   * with the <code>shippingAddress</code> property.
	   * 
	   * @param pProfile profile repository item
	   * @param pAddressName nickname of the address to be set as default
	   * @return true
	   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
	   */
	  @Override
	  public boolean setDefaultShippingAddress(RepositoryItem pProfile, String pAddressName) throws RepositoryException {
	    CommercePropertyManager propertyManager = getCommercePropertyManager();
	    RepositoryItem addressItem = StringUtils.isEmpty(pAddressName) ? null : getProfileAddress(pProfile, pAddressName);
	    updateProperty(propertyManager.getShippingAddressPropertyName(), addressItem, pProfile);
	    return true;
	  }
	  
	  public boolean isDuplicateTaxExmpNickName(RepositoryItem pProfile, String pNewNickName, List<String> pExcludedNames){
		  
		  Map taxExmpns = (Map) pProfile.getPropertyValue("taxExemptions");
		  return checkForDuplicates(taxExmpns, pNewNickName, pExcludedNames);
	  }
	  
	  /**
	   * Overrides CommerceProfileTools.isDuplicateAddressNickName. A case insensitive nickname check.
	   * 
	   * @param pProfile The current user profile
	   * @param pNewNickName An address nickname
	   * @return true if the nickname is duplicated, false otherwise.
	   */
	  @Override
	  public boolean isDuplicateAddressNickName(RepositoryItem pProfile, String pNewNickName){
	    return isDuplicateAddressNickname(pProfile, pNewNickName, null);
	  }
	  
	  /**
	   * A case insensitive duplicate nickname check.
	   * 
	   * @param pProfile The current user profile
	   * @param pNewNickName An address nickname
	   * @param pExcludedNames A list of nicknames that the pNewNickName shouldn't be checked against
	   * @return true if the nickname is duplicated, false otherwise.
	   */
	  public boolean isDuplicateAddressNickname(RepositoryItem pProfile, String pNewNickname,
	                                            List<String> pExcludedNames)  
	  {
	    String secondaryAddressPropertyName =  getCommercePropertyManager().getSecondaryAddressPropertyName();
	    Map secondaryAddressMap = (Map) pProfile.getPropertyValue(secondaryAddressPropertyName);
	    return checkForDuplicates(secondaryAddressMap, pNewNickname, pExcludedNames);
	  }

	  /**
	   * Changes secondary address nickname
	   * 
	   * @param pProfile profile repository item
	   * @param pOldAddressName old secondary address nickname
	   * @param pNewAddressName new secondary address nickname
	   * @throws RepositoryException 
	   */
	  public void changeSecondaryAddressName(RepositoryItem pProfile, String pOldAddressName,
	                                         String pNewAddressName) throws RepositoryException {
	    if (StringUtils.isBlank(pNewAddressName) || pNewAddressName.equals(pOldAddressName)) {
	      return;
	    }

	    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
	    Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
	    RepositoryItem address = getProfileAddress(pProfile, pOldAddressName);
	    if (address != null) {
	      secondaryAddresses.remove(pOldAddressName);
	      secondaryAddresses.put(pNewAddressName, address);
	      updateProperty(cpmgr.getSecondaryAddressPropertyName(), secondaryAddresses, pProfile);
	    }
	  }
	  
	  /**
	   * Checks for duplicates between pKey and the mPropertyMap.keySet().
	   * 
	   * @param mPropertyMap
	   * @param pKey
	   * @param pExcludedKeys Keys to be excluded from the case insensitive check.
	   * @return
	   */
	  protected boolean checkForDuplicates(Map mPropertyMap, String pKey, List<String> pExcludedKeys){
	    //Fetch the keys (ie nick names)
	    Collection secondaryAddressKeys = mPropertyMap.keySet();

	    List<String> profileNames = new ArrayList<String>();
	    Iterator<String> iterator = secondaryAddressKeys.iterator();
	    while (iterator.hasNext()) {
	      profileNames.add(iterator.next());
	    }
	    
	    // Remove the names we want to ignore
	    if(pExcludedKeys != null){
	      profileNames.removeAll(pExcludedKeys);
	    }
	    
	    // Check for duplicates
	    for(String profileNickname : profileNames){
	      if(profileNickname.equalsIgnoreCase(pKey)){
	        return true;
	      }
	    }
	    return false;
	  }
	  
	  /**
	   * This method is used to check if user's password meets the min & max length.
	   *
	   * @param pPassword
	   *            is password string
	   * @return True if password meets min/max requirements False if password
	   *         fails min/max requirements
	   */
	  public boolean isValidPasswordLength(String pPassword) {
	    int passwordLength = pPassword.length();

	    // Check to see if password.length is between min and max values
	    if ((passwordLength >= getMinPasswordLength()) && (passwordLength <= getMaxPasswordLength())) {
	      return true;
	    }
	    return false;
	  }
	  
	  public String getUniqueShippingAddressNickname(Object pAddress, RepositoryItem pProfile, String pNewNickname) {
		    if (StringUtils.isBlank(pNewNickname)) {
		      // Always get resource bundles with Locale, this is essential for internationalization.
		      Object localePropertyValue = pProfile.getPropertyValue(getPropertyManager().getLocalePropertyName());
		      Locale currentUserLocale = LocaleUtils.getCachedLocale(localePropertyValue == null ?
		        Locale.getDefault().toString() : localePropertyValue.toString());
		      // We will construct address name from this draft.
		      ResourceBundle profileResources = LayeredResourceBundle.getBundle(PROFILE_RESOURCE_BUNDLE_NAME, currentUserLocale);
		      pNewNickname = profileResources.getString(ADDRESS_NICKNAME_PREFIX);
		    }
		    return super.getUniqueShippingAddressNickname(pAddress, pProfile, pNewNickname);
		  }
	  
	  public void createTaxExemptions(RepositoryItem pProfile, TaxExemptionInfo pTaxExemptionInfo, Address pAddress)
			    throws RepositoryException {
		    
		    Map taxExemptions = (Map)pProfile.getPropertyValue("taxExemptions");
		    RepositoryItem taxExmpInfoItem = createTaxExemptionInfoItem(pProfile, pTaxExemptionInfo, pAddress);
		    taxExemptions.put(pTaxExemptionInfo.getNickName(), taxExmpInfoItem);
	}
	  
	  public RepositoryItem createTaxExemptionInfoItem(RepositoryItem pProfile, TaxExemptionInfo pTaxExemptionInfo, Address pAddress)
			    throws RepositoryException {
		  
		MutableRepository repository = (MutableRepository)pProfile.getRepository();
		CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
		MutableRepositoryItem taxExmpInfoItem = repository.createItem("taxExemptionInfo");
	   
		if (pTaxExemptionInfo != null) {
			taxExmpInfoItem.setPropertyValue("nickName", pTaxExemptionInfo.getNickName());
			taxExmpInfoItem.setPropertyValue("classificationId", pTaxExemptionInfo.getClassificationId());
			taxExmpInfoItem.setPropertyValue("classificationCode", pTaxExemptionInfo.getClassificationCode());
			taxExmpInfoItem.setPropertyValue("classificationName", pTaxExemptionInfo.getClassificationName());
			taxExmpInfoItem.setPropertyValue("taxId", pTaxExemptionInfo.getTaxId());
			taxExmpInfoItem.setPropertyValue("orgName", pTaxExemptionInfo.getOrgName());
			taxExmpInfoItem.setPropertyValue("businessDesc", pTaxExemptionInfo.getBusinessDesc());
			taxExmpInfoItem.setPropertyValue("merchandise", pTaxExemptionInfo.getMerchandise());
			taxExmpInfoItem.setPropertyValue("taxCity", pTaxExemptionInfo.getTaxCity());
			taxExmpInfoItem.setPropertyValue("taxState", pTaxExemptionInfo.getTaxState());
		}
	    
	    MutableRepositoryItem orgAddressitem = repository.createItem(cpmgr.getContactInfoItemDescriptorName());
	    
	    logDebug("handleAddTaxExemption: orgAddressitem: " + orgAddressitem);
	    logDebug("handleAddTaxExemption: pAddress: " + pAddress);
	    
	    if (pAddress != null) {
	      
	    	try {
	    		OrderTools.copyAddress(pAddress, orgAddressitem);
	    	}
	    	catch (CommerceException ce)
		      {
		        Throwable src = ce.getSourceException();
		        if ((src instanceof RepositoryException)) {
		          throw ((RepositoryException)src);
		        }
		        throw new RepositoryException(src);
		      }
	    	
	      orgAddressitem.setPropertyValue(cpmgr.getAddressOwnerPropertyName(), taxExmpInfoItem.getRepositoryId());
	    }
	
	    RepositoryItem orgAddressitemAdded = repository.addItem(orgAddressitem);
	    logDebug("handleAddTaxExemption: orgAddressitemAdded: " + orgAddressitemAdded);
	
	    taxExmpInfoItem.setPropertyValue("organizationAddress", orgAddressitemAdded);
	    
	    repository.addItem(taxExmpInfoItem);
	
	    return taxExmpInfoItem;
	}
	  
	public String[] getClassificationInfo(String pClassificationId) {
		
		String[] classificationInfo = new String[2];

		RepositoryItem classificationItem = null;
		try {
			classificationItem = getContentRepository().getItem(pClassificationId, "taxExmpClassification");
		} catch (RepositoryException e) {
			logError("RepositoryException while getting classification item: " + e , e);
		}
		if (classificationItem != null) {
			classificationInfo[0] = (String) classificationItem.getPropertyValue("taxExmpCode");
			classificationInfo[1] = (String) classificationItem.getPropertyValue("displayName");
		}

		return classificationInfo;
	}
	  
		  public void updateTaxExemptionInfo(RepositoryItem pProfile, String pNickName, String pNewNickName, TaxExemptionInfo pTaxExemptionInfo, Address pAddress)
				    throws RepositoryException {
			  
			MutableRepository repository = (MutableRepository)pProfile.getRepository();
			
			Map taxExmpns = (Map) pProfile.getPropertyValue("taxExemptions");
	  	  	RepositoryItem existingTaxExmpInfo = (RepositoryItem) taxExmpns.get(pNickName);
	  	  	
			MutableRepositoryItem taxExmpInfoItem = repository.getItemForUpdate(existingTaxExmpInfo.getRepositoryId(), "taxExemptionInfo");
			logDebug("updateTaxExemptionInfo: taxExmpInfoItem: " + taxExmpInfoItem);
			
			if (pTaxExemptionInfo != null) {
				taxExmpInfoItem.setPropertyValue("nickName", pTaxExemptionInfo.getNickName());
				taxExmpInfoItem.setPropertyValue("classificationId", pTaxExemptionInfo.getClassificationId());
				taxExmpInfoItem.setPropertyValue("classificationCode", pTaxExemptionInfo.getClassificationCode());
				taxExmpInfoItem.setPropertyValue("classificationName", pTaxExemptionInfo.getClassificationName());
				taxExmpInfoItem.setPropertyValue("taxId", pTaxExemptionInfo.getTaxId());
				taxExmpInfoItem.setPropertyValue("orgName", pTaxExemptionInfo.getOrgName());
				taxExmpInfoItem.setPropertyValue("businessDesc", pTaxExemptionInfo.getBusinessDesc());
				taxExmpInfoItem.setPropertyValue("merchandise", pTaxExemptionInfo.getMerchandise());
				taxExmpInfoItem.setPropertyValue("taxCity", pTaxExemptionInfo.getTaxCity());
				taxExmpInfoItem.setPropertyValue("taxState", pTaxExemptionInfo.getTaxState());
			}
		    
		    MutableRepositoryItem oldAddressitem = (MutableRepositoryItem) taxExmpInfoItem.getPropertyValue("organizationAddress");
		    
		    logDebug("updateTaxExemptionInfo: orgAddressitem: " + oldAddressitem);
		    logDebug("updateTaxExemptionInfo: pAddress: " + pAddress);
		    
		    if (pAddress != null) {
				updateProfileRepositoryAddress(oldAddressitem, pAddress);
			}
		
		    repository.updateItem(oldAddressitem);
		    repository.updateItem(taxExmpInfoItem);
		    if (!StringUtils.isBlank(pNewNickName) && !pNewNickName.equals(pNickName)) {
		    	taxExmpns.remove(pNickName);
		    	taxExmpns.put(pNewNickName, existingTaxExmpInfo);
		    }
		}
		  
		  /**
		   * This method is used to update the credit card with the different saved address. 
		   * @param pCardToUpdate
		   * @param pProfile
		   * @param pUpdatedCreditCard
		   * @param pNewCreditCardNickname
		 * @param pBillingItem 
		   * @throws InstantiationException
		   * @throws IllegalAccessException
		   * @throws ClassNotFoundException
		   * @throws IntrospectionException
		   * @throws RepositoryException
		   */
		  public void updateProfileCreditCard(RepositoryItem pCardToUpdate, RepositoryItem pProfile, Map pUpdatedCreditCard, 
				  String pNewCreditCardNickname, RepositoryItem pBillingItem)
				  throws InstantiationException, IllegalAccessException, ClassNotFoundException, IntrospectionException, RepositoryException {
			   
			  vlogDebug("MFFProfileTools :: updateProfileCreditCard() :: START");
			  
				MutableRepository repository = (MutableRepository) pProfile.getRepository();
				MutableRepositoryItem oldCard = RepositoryUtils.getMutableRepositoryItem(pCardToUpdate);
				
				String nickname = getCreditCardNickname(pProfile, pCardToUpdate);
				
				CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
				if (pUpdatedCreditCard != null) {
					String[] cardProperties = cpmgr.getShallowCreditCardPropertyNames();

					for (int i = 0; i < cardProperties.length; ++i) {
						Object property = pUpdatedCreditCard.get(cardProperties[i]);
						if (property != null) {
							oldCard.setPropertyValue(cardProperties[i], property);
						}
					}
					oldCard.setPropertyValue(cpmgr.getCreditCardItemDescriptorBillingAddressPropertyName(), pBillingItem);
					repository.updateItem(oldCard);
				}

				vlogDebug("MFFProfileTools :: updateProfileCreditCard() :: END");
				if ((StringUtils.isBlank(pNewCreditCardNickname))
						|| (pNewCreditCardNickname.equalsIgnoreCase(nickname)))
					return;
				changeCreditCardNickname(pProfile, nickname, pNewCreditCardNickname);
			}
		  

		/**
		 * This method is used to validate the address by FedExService and return if any suggested address.
		 * @param pRequest
		 * @param pNewCard
		 * @return
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Map validateAddress(DynamoHttpServletRequest pRequest, Map pNewCard) {
			
			Map resultMap = new HashMap();
			AddressValidationInput avsInput=new AddressValidationInput();
			
			avsInput.setAddressLine1((String)pNewCard.get("address1"));
			avsInput.setAddressLine2((String)pNewCard.get("address2"));
			avsInput.setCity((String)pNewCard.get("city"));
			avsInput.setState((String)pNewCard.get("state"));
			avsInput.setZipCode((String)pNewCard.get("postalCode"));
			
			Address inputAddress=new Address();
			
			inputAddress.setAddress1(((String)pNewCard.get("address1")).trim().toUpperCase());
			if(pNewCard.get("address2") !=null ){
				inputAddress.setAddress2(((String)pNewCard.get("address2")).trim().toUpperCase());
			}
			inputAddress.setCity(((String)pNewCard.get("city")).trim().toUpperCase());
			inputAddress.setState(((String)pNewCard.get("state")).trim().toUpperCase());
			inputAddress.setPostalCode(((String)pNewCard.get("postalCode")).trim().toUpperCase());
			inputAddress.setCountry("US");
			
			ContactInfo outputAddress = new ContactInfo();
			
			AddressValidationOutput avsOutput;
			try {
				avsOutput = getAddressValidationService().ValidateAddress(avsInput);
				if(avsOutput.isResolved()){
					
					outputAddress.setAddress1(avsOutput.getEffectiveStreetLine1().trim().toUpperCase());
					if(avsOutput.getEffectiveStreetLine2()!=null){
						outputAddress.setAddress2(avsOutput.getEffectiveStreetLine2().trim().toUpperCase());
					}
					outputAddress.setCity(avsOutput.getEffectiveCity().trim().toUpperCase());
					outputAddress.setState(avsOutput.getEffectiveState().trim().toUpperCase());
					outputAddress.setPostalCode(avsOutput.getEffectiveZipCode().trim().toUpperCase());
					outputAddress.setCountry("US");
					boolean matchFlag = false;
					if(inputAddress.getAddress1().equalsIgnoreCase(outputAddress.getAddress1()) && 
							inputAddress.getCity().equalsIgnoreCase(outputAddress.getCity()) &&
							inputAddress.getState().equalsIgnoreCase(outputAddress.getState())&&
							inputAddress.getCountry().equalsIgnoreCase(outputAddress.getCountry()) &&
							inputAddress.getPostalCode().equalsIgnoreCase(outputAddress.getPostalCode())){
						matchFlag = true;
					} 
					if(matchFlag && !StringUtils.isBlank(inputAddress.getAddress2()) &&
							inputAddress.getAddress2().equalsIgnoreCase(outputAddress.getAddress2())){
						matchFlag = true;
					} else if(matchFlag && !StringUtils.isBlank(inputAddress.getAddress2()) &&
							!inputAddress.getAddress2().equalsIgnoreCase(outputAddress.getAddress2())){
						matchFlag = false;
					} 
					if(matchFlag){
						resultMap.put("addressMatch","true");
						return resultMap;
					} else {
						resultMap.put("addressMatch","false");
						resultMap.put("enteredAddress", inputAddress);
						resultMap.put("suggestedAddress", outputAddress);
						return resultMap;
					}
				}
			} catch (RemoteException e) {
				vlogError("Exception occurred while executing FedEx Service :: "+e.getMessage());
			} 
			resultMap.put("addressMatch","false");
			resultMap.put("enteredAddress", inputAddress);
			return resultMap;
		}
		
		/**
		 * This method is used to updated the credit card with new billing address
		 * @param pCardToUpdate
		 * @param pProfile
		 * @param pUpdatedCreditCard
		 * @param pNewNickname
		 * @param pBillinrAddressId
		 * @throws RepositoryException
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 * @throws ClassNotFoundException
		 * @throws IntrospectionException
		 */
		@SuppressWarnings("rawtypes")
		public void updateCardWithNewAddress(RepositoryItem pCardToUpdate, Profile pProfile, Map pUpdatedCreditCard, 
				String pNewNickname, String pBillinrAddressId) 
						throws RepositoryException, InstantiationException, IllegalAccessException, ClassNotFoundException, IntrospectionException{
			
			MutableRepository repository = (MutableRepository) pProfile.getRepository();
			MutableRepositoryItem oldCard = RepositoryUtils.getMutableRepositoryItem(pCardToUpdate);
			String nickname = getCreditCardNickname(pProfile, pCardToUpdate);
			CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
			if (pUpdatedCreditCard != null) {
				String[] cardProperties = cpmgr.getShallowCreditCardPropertyNames();

				for (int i = 0; i < cardProperties.length; ++i) {
					Object property = pUpdatedCreditCard.get(cardProperties[i]);
					if (property != null) {
						oldCard.setPropertyValue(cardProperties[i], property);
					}
				}

				oldCard.setPropertyValue(cpmgr.getBillingAddressPropertyName(), getProfileAddressById(pBillinrAddressId));
				repository.updateItem(oldCard);
			}
			
			changeCreditCardNickname(pProfile, nickname, pNewNickname);
		}
		
		public void setForcePasswordReset(RepositoryItem pProfile) throws RepositoryException,
		IntrospectionException, PropertyNotFoundException{
			 vlogDebug("MFFProfileTools :: setForcePasswordReset() :: START");
			  
				MutableRepository repository = (MutableRepository) pProfile.getRepository();
				MutableRepositoryItem pProfileItem = (MutableRepositoryItem)pProfile;
				pProfileItem.setPropertyValue("forcePasswordReset", false);
				repository.updateItem(pProfileItem);
				vlogDebug("MFFProfileTools :: setForcePasswordReset() :: END");			
		}
		
		public void setLastActivityDate(RepositoryItem pProfile) throws RepositoryException,
		IntrospectionException, PropertyNotFoundException{
			 vlogDebug("MFFProfileTools :: setLastActivityDate() :: START");
			  
			 
				MutableRepository repository = (MutableRepository) pProfile.getRepository();
				MutableRepositoryItem pProfileItem = (MutableRepositoryItem)pProfile;
				DynamicBeans.setPropertyValue(pProfileItem, "lastActivity", Calendar.getInstance().getTime());
				vlogDebug("MFFProfileTools :: setLastActivityDate() :: END");			
		}
		
		public String createProfileCreditCardWithExistingAddress(RepositoryItem pProfile,
				Map pNewCreditCard, String pCreditCardNickname,
				RepositoryItem pBillingAddress) throws RepositoryException,
				IntrospectionException, PropertyNotFoundException {
			MutableRepository repository = (MutableRepository) pProfile
					.getRepository();
			CommercePropertyManager propertyManager = (CommercePropertyManager) this
					.getPropertyManager();
						
			String creditCardNickname = pCreditCardNickname;
			if (StringUtils.isBlank(pCreditCardNickname)) {
				creditCardNickname = getUniqueCreditCardNickname(
						pNewCreditCard, (RepositoryItem) pProfile, (String) null);
			}

			MutableRepositoryItem newCreditCard = this.createCreditCardItemWithSavedAddress(pProfile, pBillingAddress);
			copyShallowCreditCardProperties(pNewCreditCard, newCreditCard);
			if (pBillingAddress != null) {
				MutableRepositoryItem billingAddress = (MutableRepositoryItem) newCreditCard
						.getPropertyValue(propertyManager
								.getCreditCardItemDescriptorBillingAddressPropertyName());
				
				if (getDefaultBillingAddress(pProfile) == null) {
					updateProperty(propertyManager.getBillingAddressPropertyName(),
							billingAddress, pProfile);
				}

				repository.updateItem(newCreditCard);
			}

			addCreditCardToUsersMap(pProfile, newCreditCard, creditCardNickname);
			return creditCardNickname;
		}
		
		public MutableRepositoryItem createCreditCardItemWithSavedAddress(RepositoryItem pProfile, RepositoryItem pBillingAddress)
				throws RepositoryException {
			if (pProfile != null) {
				CommercePropertyManager cpmgr = (CommercePropertyManager) this
						.getPropertyManager();
				MutableRepository repository = (MutableRepository) pProfile.getRepository();
				MutableRepositoryItem creditCardRepositoryItem = repository.createItem(cpmgr.getCreditCardItemDescriptorName());
				
				creditCardRepositoryItem.setPropertyValue(cpmgr.getCreditCardItemDescriptorBillingAddressPropertyName(),pBillingAddress);
				
				repository.addItem(creditCardRepositoryItem);
				return creditCardRepositoryItem;
			} else {
				return null;
			}
		}
		
		protected int verifyCreditCardDate( String pExpMonth, String pExpDayOfMonth, String pExpYear) {
			if (pExpMonth != null && pExpMonth.trim().length() != 0
					&& pExpYear != null && pExpYear.trim().length() != 0) {
				Date dateCur = new Date(System.currentTimeMillis());
				SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
				SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("dd");
				SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
				ParsePosition pos = new ParsePosition(0);
				boolean includesDay = false;
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				Date dateExpires;
				if (pExpDayOfMonth != null && pExpDayOfMonth.trim().length() != 0) {
					includesDay = true;
					dateExpires = formatter.parse(pExpMonth.trim() + "/"
							+ pExpDayOfMonth.trim() + "/" + pExpYear.trim(), pos);
				} else {
					dateExpires = formatter.parse(pExpMonth.trim() + "/1/"
							+ pExpYear.trim(), pos);
				}

				return dateExpires == null ? 7 : (!dateCur.after(dateExpires) ? 0
						: (!monthFormat.format(dateExpires).equals(
								monthFormat.format(dateCur))
								|| !yearFormat.format(dateExpires).equals(
										yearFormat.format(dateCur))
								|| includesDay
								&& !dayOfMonthFormat.format(dateExpires).equals(
										dayOfMonthFormat.format(dateCur)) ? 1 : 0));
			} else {
				return 7;
			}
		}
		
		/**
	   * This method validates credit card already present or not, based on last 4
	   * digits and exp month & exp year.
	   * 
	   * @param pCardNumber
	   * @param pProfile
	   * @param expMonthYear
	   * @return
	   */
	  @SuppressWarnings({ "unchecked", "rawtypes" })
	  public boolean isCreditCardExistInProfile(String pCardNumber, RepositoryItem pProfile, String expMonthYear) {
	    MFFPropertyManager cpmgr = (MFFPropertyManager) getCommercePropertyManager();

	    String cardLastFour = pCardNumber.substring(pCardNumber.length() - 4);
	    RepositoryItem primaryCC = (RepositoryItem) pProfile.getPropertyValue(cpmgr.getDefaultCreditCardPropertyName());

	    if (primaryCC != null) {
	      if (isLoggingDebug()) logDebug("Primary Card :" + primaryCC.getRepositoryId());
	    }

	    if (primaryCC != null) {

	      if (isLoggingDebug()) logDebug("inside primary cc IF");

	      String primaryCard = (String) primaryCC.getPropertyValue("creditCardNumber");
	      String expMonth = (String) primaryCC.getPropertyValue("expirationMonth");
	      String expYear = (String) primaryCC.getPropertyValue("expirationYear");
	      String primaryCardLastFour = primaryCard.substring(primaryCard.length() - 4);
	      String expMY = expMonth + expYear;
	      if (cardLastFour.equalsIgnoreCase(primaryCardLastFour) && expMonthYear.equalsIgnoreCase(expMY)) {
	        return true;
	      }
	    }

	    Map secondaryCards = (Map) pProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
	    Collection<RepositoryItem> cards = secondaryCards.values();
	    for (RepositoryItem card : cards) {
	      if (isLoggingDebug()) logDebug("Repository ID : " + card.getRepositoryId());

	      String primaryCard = (String) card.getPropertyValue("creditCardNumber");
	      String expMonth = (String) card.getPropertyValue("expirationMonth");
	      String expYear = (String) card.getPropertyValue("expirationYear");
	      String primaryCardLastFour = primaryCard.substring(primaryCard.length() - 4);
	      String expMY = expMonth + expYear;
	      if (cardLastFour.equalsIgnoreCase(primaryCardLastFour) && expMonthYear.equalsIgnoreCase(expMY)) {
	        return true;
	      }
	    }
	    return false;
	  }
	  
	  public void clearCurrentPromos(RepositoryItem pProfile) throws RepositoryException {
	    // Get the list of active promotions on the order
	    if (isLoggingDebug()) {
	      logDebug("Removing active promotions for profile " + pProfile.getRepositoryId());
	    }
	    MutableRepositoryItem mutProfile = getProfileRepository().getItemForUpdate(pProfile.getRepositoryId(), pProfile.getItemDescriptor().getItemDescriptorName());
	    Collection activePromos = (Collection) mutProfile.getPropertyValue(getPromotionTools().getActivePromotionsProperty());
	    if (activePromos != null) {
	      activePromos.clear();
	    }
	    getProfileRepository().updateItem(mutProfile);
	  }
	  
	  public boolean isHardLoggedIn(RepositoryItem pProfile) {
	    MFFPropertyManager pm = (MFFPropertyManager) getPropertyManager();
	    return getLoginStatus(pProfile) == pm.getLoginStatusRegisteredHardLogin();

	  }
	  
	  public int getLoginStatus(RepositoryItem pProfile) {
	    MFFPropertyManager pm = (MFFPropertyManager) getPropertyManager();
	    int lLoginStatus = pm.getLoginStatusAnonymous();

	    if (!pProfile.isTransient()) {

	      Integer securityStatus = (Integer) pProfile.getPropertyValue(pm.getSecurityStatusPropertyName());
	      if (isLoggingDebug()) vlogDebug("Security status set to {0}", securityStatus);

	      String login = (String) pProfile.getPropertyValue(pm.getLoginPropertyName());
	      String profileId = pProfile.getRepositoryId();

	      if (isLoggingDebug()) vlogDebug("Login {0}, pProfileId {1}", login, profileId);

	      if (securityStatus == pm.getSecurityStatusAnonymous() && login.equals(profileId)) {
	        lLoginStatus = pm.getLoginStatusPersistentAnonymous();
	      }
	      if (securityStatus <= pm.getSecurityStatusCookie() && login.equals(profileId)) {
	        lLoginStatus = pm.getLoginStatusPersistentAnonymousSoftLogin();
	      }
	      if (securityStatus <= pm.getSecurityStatusCookie() && !login.equals(profileId)) {
	        lLoginStatus = pm.getLoginStatusRegisteredSoftLogin();
	      }
	      if (securityStatus >= pm.getSecurityStatusLogin()) {
	        lLoginStatus = pm.getLoginStatusRegisteredHardLogin();
	      }
	    }

	    return lLoginStatus;
	  }
	  
	  public String getLastFourForCreditCard(String pCreditCardNumber) {
		  if (pCreditCardNumber.length() > 4) {
			  pCreditCardNumber = pCreditCardNumber.substring(pCreditCardNumber.length() - 4);
	        }
		  return pCreditCardNumber;
	  }
	  public String maskCreditCard(String pCreditCard) {
		  String creditCardNumber = NumberFormat.formatCreditCardNumber(
					pCreditCard, getMaskCharacter(), getNumUnmasked(), getGroupingSize());
		  
		  return creditCardNumber;
	  }
		
		public boolean isExpDateChanged(RepositoryItem pCardToUpdate,
				Map pEditValue, CommercePropertyManager pCpmgr) {
			
			String expMonthOnProfile = (String)pCardToUpdate.getPropertyValue(pCpmgr.getCreditCardExpirationMonthPropertyName());
			String expYearOnProfile = (String)pCardToUpdate.getPropertyValue(pCpmgr.getCreditCardExpirationYearPropertyName());
			
			vlogDebug("isExpDateChanged(): expMonthOnProfile: " + expMonthOnProfile);
			vlogDebug("isExpDateChanged(): expYearOnProfile: " + expYearOnProfile);
			
			String newExpMonth = (String) pEditValue.get(pCpmgr.getCreditCardExpirationMonthPropertyName());
			String newExpYear = (String) pEditValue.get(pCpmgr.getCreditCardExpirationYearPropertyName());
			
			vlogDebug("isExpDateChanged(): newExpMonth: " + newExpMonth);
			vlogDebug("isExpDateChanged(): newExpYear: " + newExpYear);
			
			if ((!StringUtils.isEmpty(expMonthOnProfile) && !expMonthOnProfile.equalsIgnoreCase(newExpMonth))
					|| (!StringUtils.isEmpty(expYearOnProfile) && !expYearOnProfile.equalsIgnoreCase(newExpYear))) {
				return true;
			}
			
			return false;
			
		}
		
		public boolean isCardTypeChanged(RepositoryItem pCardToUpdate,
				Map pEditValue, CommercePropertyManager pCpmgr){
			
			String cardTypeOnProfile = (String)pCardToUpdate.getPropertyValue(pCpmgr.getCreditCardTypePropertyName());
			vlogDebug("isCardNumberChanged(): cardTypeOnProfile: " + cardTypeOnProfile);
			
			String newCardType = (String) pEditValue.get(pCpmgr.getCreditCardTypePropertyName());
			vlogDebug("isCardNumberChanged(): newCardType: " + newCardType);
			
			if (!StringUtils.isEmpty(cardTypeOnProfile) && !cardTypeOnProfile.equalsIgnoreCase(newCardType)){
				return true;
			}
			
			return false;
		}
		
	public boolean isCardNumberChanged(RepositoryItem pCardToUpdate,
			Map pEditValue, CommercePropertyManager pCpmgr){
		
		String cardNumOnProfile = (String)pCardToUpdate.getPropertyValue(pCpmgr.getCreditCardNumberPropertyName());
        if (cardNumOnProfile.length() > 4) {
        	cardNumOnProfile = cardNumOnProfile.substring(cardNumOnProfile.length() - 4);
        }
		
		String newCardNum = (String) pEditValue.get(pCpmgr.getCreditCardNumberPropertyName());
		if (newCardNum.length() > 4) {
			newCardNum = newCardNum.substring(newCardNum.length() - 4);
        }
		
		if (!StringUtils.isEmpty(cardNumOnProfile) && !cardNumOnProfile.equalsIgnoreCase(newCardNum)){
			return true;
		}
		
		return false;
	}
	
	public boolean isMaskedCardNumber(Map pEditValue, CommercePropertyManager pCpmgr){
		
		String newCardNum = (String) pEditValue.get(pCpmgr.getCreditCardNumberPropertyName());
		if (newCardNum.contains(getMaskCharacter())) {
			return true;
        }
		return false;
	}
	
	private LinkedList sortByValue(Map map) {
		
		LinkedList list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, RepositoryItem>>(){
			
			public int compare( Map.Entry<String, RepositoryItem> o1, Map.Entry<String, RepositoryItem> o2 )
            {
                return (o1.getValue().getRepositoryId()).compareTo( o2.getValue().getRepositoryId());
            }
		});

		return list;
	}
  
	public Repository getContentRepository() {
		return mContentRepository;
	}

	public void setContentRepository(Repository pContentRepository) {
		this.mContentRepository = pContentRepository;
	}

	public String getMaskCharacter() {
		return mMaskCharacter;
	}

	public void setMaskCharacter(String pMaskCharacter) {
		mMaskCharacter = pMaskCharacter;
	}

	public int getNumUnmasked() {
		return mNumUnmasked;
	}

	public void setNumUnmasked(int pNumUnmasked) {
		mNumUnmasked = pNumUnmasked;
	}

	public int getGroupingSize() {
		return mGroupingSize;
	}

	public void setGroupingSize(int pGroupingSize) {
		mGroupingSize = pGroupingSize;
	}

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void loadShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart) throws CommerceException {

    if (isLoggingDebug()) logDebug((new StringBuilder()).append("loading orders for profile ").append(pProfile).toString());

    if (pProfile == null || pShoppingCart == null) return;

    if (!shouldLoadShoppingCarts(pShoppingCart)) return;

    Collection siteIds = getSiteGroupManager().getSharingSiteIds("atg.ShoppingCart");
    int orderStates[] = { StateDefinitions.ORDERSTATES.getStateValue("incomplete") };

    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    RepositoryItem organization = null;
    
    MFFOrderManager orderManager = (MFFOrderManager) getOrderManager();

    try {
      RepositoryItemDescriptor profileDesc = pProfile.getItemDescriptor();
      if (profileDesc != null && profileDesc.hasProperty(cpmgr.getCurrentOrganizationPropertyName())) organization = (RepositoryItem) pProfile.getPropertyValue(cpmgr.getCurrentOrganizationPropertyName());
    } catch (RepositoryException exception) {
      if (isLoggingDebug()) logDebug((new StringBuilder()).append("The ").append(cpmgr.getCurrentOrganizationPropertyName()).append(" property of the user profile could not be determined.").toString());
    }

    String organizationId = organization != null ? organization.getRepositoryId() : null;

    if (isLoggingDebug()) logDebug((new StringBuilder()).append("organizationId = ").append(organizationId).toString());

    ArrayList organizationIdList = new ArrayList();
    organizationIdList.add(organizationId);
    List orders = orderManager.getOrderQueries().getOrdersForProfileInState(pProfile.getRepositoryId(), 0, -1, orderStates, getSortOrdersByProperty(), isAscendingOrder(), siteIds, organizationIdList);
    Collection currentOrders = pShoppingCart.getSaved();

    if (currentOrders != null) {
      if (currentOrders.containsAll(orders)) {
        if (isLoggingDebug()) logDebug("All orders are already in there so we can just return.");
        return;
      }
      try {
        orders.removeAll(currentOrders);
      } catch (UnsupportedOperationException uoe) {
        logError(uoe);
      } catch (ClassCastException cce) {
        logError(cce);
      } catch (IllegalArgumentException iae) {
        logError(iae);
      }
    }

    if (isLoggingDebug()) logDebug((new StringBuilder()).append("Found orders for profile[").append(pProfile.getRepositoryId()).append("]=").append(orders).toString());

    if (orders != null && orders.size() > 0) {
      int order_index = findOrderToLoad(orders);

      if (isLoggingDebug()) logDebug((new StringBuilder()).append("The index returned is: ").append(order_index).toString());
      Order persistentCurrent = null;

      if (order_index >= 0)
        persistentCurrent = (Order) orders.remove(order_index);
      else if (isLoggingDebug()) logDebug("The index returned is not in the list");

      if (persistentCurrent != null) if (pShoppingCart.isCurrentEmpty()) {
        if (isLoggingDebug()) logDebug((new StringBuilder()).append("Current order is empty, so make ").append(persistentCurrent).append(" current").toString());

        pShoppingCart.setCurrent(persistentCurrent);
      } else {
        Order activeCurrent = pShoppingCart.getCurrent();

        if (!orderManager.orderContainsGWPGC(activeCurrent) && isMergeOrders() && (!activeCurrent.getId().equals(persistentCurrent.getId()) || activeCurrent.getLastModifiedTime() < persistentCurrent.getLastModifiedTime()) && getOrderManager().areOrdersMergeableByOrganizationId(activeCurrent, persistentCurrent)) {

          if (isLoggingDebug()) logDebug((new StringBuilder()).append("Merge order ").append(activeCurrent).append(" into order ").append(persistentCurrent).toString());

          synchronized (persistentCurrent) {
            getOrderManager().mergeOrders(activeCurrent, persistentCurrent);
          }

          if (isLoggingDebug()) logDebug((new StringBuilder()).append("Make merged order ").append(persistentCurrent).append(" current").toString());
          pShoppingCart.setCurrent(persistentCurrent);

        } else if (!activeCurrent.getId().equals(persistentCurrent.getId())) {

          if (isLoggingDebug()) logDebug((new StringBuilder()).append("Do not merge, so add ").append(persistentCurrent).append(" to saved list").toString());
          pShoppingCart.getSaved().add(persistentCurrent);
        }
      }

      int size = orders.size();
      if (size > 0) {
        if (isLoggingDebug()) logDebug((new StringBuilder()).append("Add the rest of the orders ").append(orders).append(" to the saved list").toString());
        for (int c = 0; c < size; c++) {
          Order order = (Order) orders.get(c);
          if (isLoggingDebug()) logDebug((new StringBuilder()).append("Adding order ").append(order).append(" to saved list").toString());
          pShoppingCart.getSaved().add(order);
        }

      }
    }
  }
	
}