package com.mff.commerce.gifts;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistFormHandler;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.InvalidGiftQuantityException;
import atg.commerce.order.CommerceItem;
import atg.core.util.CaseInsensitiveHashtable;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.PropertyManager;

import com.mff.constants.MFFConstants;
import com.mff.email.MFFEmailManager;

/**
 * This class provide extensions to atg OOTB GiftlistFormHandler.
 *
 */
public class MFFGiftlistFormHandler extends GiftlistFormHandler {

	protected PropertyManager mProfilePropertyManager;
	protected String mRemoveItemsFromGiftlistSuccessURL;
	protected String mRemoveItemsFromGiftlistErrorURL;
	protected String mShareWishlistSuccessURL;
	protected String mShareWishlistErrorURL;
	private CaseInsensitiveHashtable mValue = new CaseInsensitiveHashtable();
	private MFFEmailManager mEmailManager;
	private static final String VALID_EMAIL_REGEXP = "^[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$";

  /**
   * Operation called just before items are removed from a a gift list.
   * 
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public void preRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  
  } 
  
  /**
   * Operation called just after items are removed from a a gift list.
   * 
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public void postRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
  } 
  
  /**
   * HandleRemoveItemsFromGiftlist is called when the user hits the "delete" button on the wish list 
   * page. This handler removes the specified gift Ids from the specified gift list.
   *
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   *
   * @return true if successful, false otherwise.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   * @exception CommerceException if there was an error with Commerce.
   */
  public boolean handleRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                               DynamoHttpServletResponse pResponse)
                                            		   	throws ServletException, IOException, CommerceException {	
	  
	  	String giftlistId = pRequest.getParameter("giftlistId");
	  	String removeGiftitemId = pRequest.getParameter("removeGiftitemId");
	  	
	  	logDebug ("handleRemoveItemsFromGiftlist(): Called: giftListId: "+ giftlistId);
	  	logDebug ("handleRemoveItemsFromGiftlist(): Called: removeGiftitemIds: "+ removeGiftitemId);
	  
		setGiftlistId(giftlistId);
		String giftListItems[] = new String[1];
		giftListItems[0] = removeGiftitemId;
		
		setRemoveGiftitemIds(giftListItems);
	  
	    try {
	      // If any form errors found, redirect to error URL:
	      if (!checkFormRedirect(null, getRemoveItemsFromGiftlistErrorURL(), pRequest, pResponse)) {
	        return false;
	      }
	
	      preRemoveItemsFromGiftlist(pRequest, pResponse);
	      
	      if (validateGiftlistId(pRequest, pResponse)) {
	    	  logDebug ("handleRemoveItemsFromGiftlist(): removing giftlist item.");
	        removeItemsFromGiftlist(pRequest, pResponse);
	
	      }
	
	      postRemoveItemsFromGiftlist(pRequest, pResponse);
	    }
	    catch (CommerceException oce) {
	      processException(oce, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
	    }

	    return checkFormRedirect(getRemoveItemsFromGiftlistSuccessURL(), 
                             getRemoveItemsFromGiftlistErrorURL(), 
                             pRequest, pResponse);
  	}
  
  public boolean handleShareWishlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		
	  	logDebug("handleShareWishlist(): Called.");
	  
		String friendEmail = (String) getValue().get(MFFConstants.FRIEND_EMAIL);
		String yourEmail = (String) getValue().get(MFFConstants.YOUR_EMAIL);
		String yourName = (String) getValue().get(MFFConstants.YOUR_NAME);
		String message = (String) getValue().get(MFFConstants.MESSAGE);
		String giftlistId = getGiftlistId();
		
		if (isLoggingDebug()){
			logDebug("friendEmail: " + friendEmail);
			logDebug("yourEmail: " + yourEmail);
			logDebug("yourName: " + yourName);
			logDebug("Message: " + message);
			logDebug("GiftlistId: " + giftlistId);
		}
		
		if (StringUtils.isEmpty(friendEmail) || StringUtils.isEmpty(yourEmail) || StringUtils.isEmpty(yourName)) {
			addFormException(new DropletException(MFFConstants.MISSING_REQUIRED_FIELDS));
		}
		
		if (!isEmailValid(friendEmail)) {
			addFormException(new DropletException(MFFConstants.INVALID_FRIEND_EMAIL));
		}
		
		if (!isEmailValid(yourEmail)) {
			addFormException(new DropletException(MFFConstants.INVALID_YOUR_EMAIL));
		}
			
		if(!getFormError()) {
			try {
				RepositoryItem giftList = null;
				if (!StringUtils.isEmpty(giftlistId)) {
					giftList = getGiftlistRepository().getItem(giftlistId, "gift-list");
				}
				
				logDebug("Giftlist: " + giftList);

				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put(MFFConstants.SITE_ID, SiteContextManager.getCurrentSiteId());
				map.put(MFFConstants.FRIEND_EMAIL, friendEmail);
				map.put(MFFConstants.YOUR_EMAIL, yourEmail);
				map.put(MFFConstants.YOUR_NAME, yourName);
				map.put(MFFConstants.MESSAGE, message);
				map.put(MFFConstants.GIFTLIST, giftList);
				getEmailManager().sendShareWishlistEmail(map);
				
				getValue().put(MFFConstants.FRIEND_EMAIL, "");
				getValue().put(MFFConstants.YOUR_EMAIL, "");
				getValue().put(MFFConstants.YOUR_NAME, "");
				getValue().put(MFFConstants.MESSAGE, "");
				
			} catch (RepositoryException e) {
				if (isLoggingError()){
					logError("handleShareWishlist(): Exception during sending email to share a wishlist: "+ e, e);
				}
			}
		}
		
		//return false;
		return checkFormRedirect(getShareWishlistSuccessURL(), getShareWishlistErrorURL(), pRequest, pResponse);
	}

   /**
   * Removes the given items to the selected gift list.
   *
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   * @exception CommerceException if there was an error with Commerce.
   */
  public void removeItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    
    GiftlistManager mgr = getGiftlistManager();
    String pGiftlistId = getGiftlistId();
    String[] items = getRemoveGiftitemIds();

    if (items == null) {
      return;
    }

    try {
      for (int i = 0; i < items.length; i++) {
        String id = items[i];
        mgr.removeItemFromGiftlist(pGiftlistId, id);
      }
    } 
    catch (RepositoryException ex) {
      processException(ex, MSG_ERROR_UPDATING_GIFTLIST_ITEMS, pRequest, pResponse);
    }
  }
  
  public boolean isEmailValid(String email) {
		return email.matches(VALID_EMAIL_REGEXP);
	}

  /**
   * Will update the quantity of the commerceItem passed in. If quantity moved to gift list from 
   * cart equals or is greater than that in cart, it will remove the item from the cart. 
   * Otherwise, it will decrease the number by quantity passed in.
   * 
   * @param pItem the commerce item to update.
   * @param pQuantity the number moved to gift list.
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @throws InvalidGiftQuantityException - if pQuantity <= 0.
   * 
   * @exception IOException if there was an error with servlet io.
   * @exception ServletException if there was an error with Servlet.
   */
  @Override
  protected void updateOrder(CommerceItem pItem, long pQuantity, 
                             DynamoHttpServletRequest pRequest, 
                             DynamoHttpServletResponse pResponse)
    throws InvalidGiftQuantityException, IOException, ServletException {
    
    super.updateOrder(pItem, pItem.getQuantity(), pRequest, pResponse);
  }
  
	public void setProfilePropertyManager(
			PropertyManager pProfilePropertyManager) {
		mProfilePropertyManager = pProfilePropertyManager;
	}

	public PropertyManager getProfilePropertyManager() {
		return mProfilePropertyManager;
	}

	public void setRemoveItemsFromGiftlistSuccessURL(
			String pRemoveItemsFromGiftlistSuccessURL) {
		mRemoveItemsFromGiftlistSuccessURL = pRemoveItemsFromGiftlistSuccessURL;
	}

	public String getRemoveItemsFromGiftlistSuccessURL() {
		return mRemoveItemsFromGiftlistSuccessURL;
	}

	public void setRemoveItemsFromGiftlistErrorURL(
			String pRemoveItemsFromGiftlistErrorURL) {
		mRemoveItemsFromGiftlistErrorURL = pRemoveItemsFromGiftlistErrorURL;
	}

	public String getRemoveItemsFromGiftlistErrorURL() {
		return mRemoveItemsFromGiftlistErrorURL;
	}
	
	public Dictionary getValue() {
		return mValue;
	}
	
	public MFFEmailManager getEmailManager() {
		return mEmailManager;
	}
	
	public void setEmailManager(MFFEmailManager pEmailManager) {
		mEmailManager = pEmailManager;
	}

	public String getShareWishlistSuccessURL() {
		return mShareWishlistSuccessURL;
	}

	public void setShareWishlistSuccessURL(String pShareWishlistSuccessURL) {
		this.mShareWishlistSuccessURL = pShareWishlistSuccessURL;
	}

	public String getShareWishlistErrorURL() {
		return mShareWishlistErrorURL;
	}

	public void setShareWishlistErrorURL(String pShareWishlistErrorURL) {
		this.mShareWishlistErrorURL = pShareWishlistErrorURL;
	}
 
}