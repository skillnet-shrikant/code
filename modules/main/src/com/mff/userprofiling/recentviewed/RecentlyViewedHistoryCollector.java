package com.mff.userprofiling.recentviewed;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import atg.core.util.StringUtils;
import atg.dms.patchbay.MessageSink;
import atg.nucleus.GenericService;
import atg.repository.ItemDescriptorImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;
import atg.userprofiling.dms.ViewItemMessage;

import com.mff.constants.MFFConstants;
import com.mff.userprofiling.MFFProfileTools;


/**
 * This MessageSink component listens for JMS 'product' ViewItem messages. It will
 * receive all product information from the ProductBrowsed droplet which fires the 
 * JMS messages.
 * 
 * When the product information is extracted from a JMS message, processing is
 * delegated to the RecentlyViewedTools component which adds the product to the
 * recently viewed list.
 * 
 */
public class RecentlyViewedHistoryCollector extends GenericService implements MessageSink {

  //----------------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------------

  /** 
   * We store the item descriptor here so we can do an 'instanceof' check of the
   * items viewed to see if they are 'products'. 
   */ 
  private ItemDescriptorImpl mProductItemDescriptor = null;
  
  //--------------------------------------------------------
  // property: recentlyViewedTools
  //--------------------------------------------------------
  protected RecentlyViewedTools mRecentlyViewedTools = null;

  /**
   * @return the ProfileTools
   */
  public RecentlyViewedTools getRecentlyViewedTools() {
    return mRecentlyViewedTools;
  }

  /**
   * @param pProfileTools the ProfileTools to set
   */
  public void setRecentlyViewedTools(RecentlyViewedTools pRecentlyViewedTools) {
    mRecentlyViewedTools = pRecentlyViewedTools;
  }
  
  //----------------------------
  // property: MessageType
  //----------------------------
  protected String mMessageType = "atg.dps.ViewItem";

  /**
   * @return The JMS message type
   */
  public String getMessageType() {
    return mMessageType;
  }

  /**
   * @param pMessageType The new JMS message type
   */
  public void setMessageType(String pMessageType) {
    mMessageType = pMessageType;
  }

  //-------------------------------------
  // property: catalogRepository
  //-------------------------------------
  protected Repository mCatalogRepository = null;

  /**
   * @return The catalogRepository.
   */
  public Repository getCatalogRepository() {
    return mCatalogRepository;
  }

  /**
   * @param The catalogRepository.
   */
  public void setCatalogRepository(Repository pCatalogRepository) {
    mCatalogRepository = pCatalogRepository;
  }
  
  //-----------------------------------------------
  // property: profileTools
  //-----------------------------------------------
  protected MFFProfileTools mProfileTools = null;

  /**
   * @return the ProfileTools
   */
  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }
  /**
   * @param pProfileTools the ProfileTools to set
   */
  public void setProfileTools(MFFProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  
  
  //----------------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------------
  /**
   * Receives JMS message and processes it if the message type in the 
   * message is equal to <code>mMessageType</code> and the itemType in 
   * the message is equal to the 'product' item name.
   */
  public void receiveMessage(String pPortName, Message pMessage)
    throws JMSException {
    
    try {
      mProductItemDescriptor = 
        (ItemDescriptorImpl) getCatalogRepository().getItemDescriptor(MFFConstants.PRODUCT);
    } 
    catch (RepositoryException re) {
      if(isLoggingError()){
        logDebug("There was a problem getting the item descriptor for product: " +re);
      }
    }
    catch (ClassCastException cce) {
      // Its alright if the product item descriptor is not an ItemDescriptorImpl,
      // we just won't do the 'instanceof' check.
    }
    
    String messageType = pMessage.getJMSType();
    
    if(messageType.equals(getMessageType())) {
      ObjectMessage oMessage = (ObjectMessage) pMessage;
      ViewItemMessage itemMessage = (ViewItemMessage) oMessage.getObject();
      
      if(isLoggingDebug()){
        logDebug("ViewItemMessageSink receives object = " + itemMessage);
      }
      
      if(itemMessage.getItemType().equals(MFFConstants.PRODUCT) ||
        (mProductItemDescriptor != null && 
         mProductItemDescriptor.isInstance(itemMessage.getItem()))) {
        processMessage(itemMessage);
      }
    }
  }
  
  
  //----------------------------------------------------------------------------------
  /**
   * Process received product data item. 
   * 
   * The product repository item, siteId, and profileId are extracted from the message.  If no profileId is supplied
   * then the profile associated with the current thread is used.  Using these references, the recentlyViewed
   * repository item can be updated.
   * 
   * @param pItemMessage The JMS message to be processed.
   */
  public void processMessage(ViewItemMessage pItemMessage) {

    // Retrieve the product from the message.
    RepositoryItem itemToAdd = pItemMessage.getItem();

    // Retrieve the profile from the message.  If there isn't one, use the current user profile item.
    String profileId = pItemMessage.getProfileId();
    RepositoryItem profile;

    if (StringUtils.isBlank(profileId)) {
      profile = ServletUtil.getCurrentUserProfile();
    }
    else {
      try {
        profile = getProfileTools().getProfileItem(profileId);
      } catch (RepositoryException e) {
        if (isLoggingWarning())
          logWarning("Unable to retrieve profile with ID: "
              + profileId + ".  Using this thread's current profile, instead.");
        profile = ServletUtil.getCurrentUserProfile();
      }
    }
    // Retrieve the site id that the current request is coming from (from the message).
    String currentSiteId = pItemMessage.getSiteId();
    
    try {
      // Add the product to the recentlyViewed item list.
      getRecentlyViewedTools().addProduct(itemToAdd, profile, currentSiteId);
    } catch (RepositoryException re) {
      if (isLoggingError()){
        logError("There was a problem adding product '" + itemToAdd.getRepositoryId() + 
            "' to profile - '" + profile.getRepositoryId() + "'\n" + re);
      }
    }
  }
  
}
