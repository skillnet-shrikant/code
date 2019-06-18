package com.mff.commerce.inventory;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.mff.email.MFFEmailManager;

import atg.commerce.fulfillment.UpdateInventory;
import atg.dms.patchbay.MessageSink;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.ProfileTools;


/**
 * This class will be used to notify users when an item is back in stock. It
 * will subscribe to a queue to listen for "UpdateInventory" messages which are
 * only fired when an item goes from Out of Stock to In Stock.
 *
 * @author DMI
 */
public class MFFBackInStockNotificationService extends GenericService implements MessageSink {
	
	private MFFEmailManager mEmailManager;

	/**
	 * @return the emailManager
	 */
	public MFFEmailManager getEmailManager() {
		return mEmailManager;
	}
	/**
	 * @param pEmailManager the emailManager to set
	 */
	public void setEmailManager(MFFEmailManager pEmailManager) {
		mEmailManager = pEmailManager;
	}

/**
   * RQL query to find items.
   */
  protected static final String RQL_QUERY_FIND_BISN_ITEMS = "catalogRefId = ?0";

  /**
   * Repository.
   */
  protected Repository mProfileRepository;

  /**
   * Profile Tools
   */
  protected ProfileTools mProfileTools;
  /**
   * Sets the property ProfileTools.
   * @beaninfo expert: true
   * description: the ProfileTools used to manipulate the profile
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  /**
   * @return The value of the property ProfileTools.
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }
  
  /**
   * property: catalogRepository
   */
  private MutableRepository mCatalogRepository = null;
  
  /**
   * @return the value of the catalogRepository field.
   */
  public MutableRepository getCatalogRepository() {
    return mCatalogRepository;
  }

  /**
   * @param pCatalogRepository -
   *          the value of the catalogRepository: field.
   */
  public void setCatalogRepository(MutableRepository pCatalogRepository) {
    mCatalogRepository = pCatalogRepository;
  }

  /**
   * Initialize service in this method.
   */
  public void doStartService() {
  }

  /**
   * The method called when a message is delivered.
   *
   * @param pPortName - the message port
   * @param pMessage - the JMS message being received
   * @throws JMSException if message error occurs
   *
   */
  public void receiveMessage(String pPortName, Message pMessage)
    throws JMSException {
    String messageType = pMessage.getJMSType();

    vlogDebug("Received message of type " + messageType + "  " + pMessage);

    if (messageType.equals(UpdateInventory.TYPE)) {
      if (pMessage instanceof ObjectMessage) {
        UpdateInventory message = (UpdateInventory) ((ObjectMessage) pMessage).getObject();
        sendBackInStockNotifications(message);
      }
    }
  }

  /**
   * Notify users when an item is back in stock.
   *
   * @param pMessage - message to send
   */
  protected void sendBackInStockNotifications(UpdateInventory pMessage) {
    String[] skuIds = pMessage.getItemIds();

    try {
      for (int i = 0; i < skuIds.length; i++) {
        String skuId = skuIds[i];

        RepositoryItem[] items = retrieveBackInStockNotifyItems(skuId);

        if (items != null) {
          sendEmail(items);
          deleteItemsFromRepository(items);
        }
      }
    } catch (Exception ex) {
        vlogError("There was a problem in sending back in stock notifications.", ex);
    }
  }

  /**
   * Helper method to delete repository items.
   *
   * @param pItems - items to delete
   * @throws RepositoryException if repository error occurs
   */
  protected void deleteItemsFromRepository(RepositoryItem[] pItems) throws RepositoryException {
    MutableRepository repository = (MutableRepository) getProfileRepository();

    for (int i = 0; i < pItems.length; i++) {
      repository.removeItem(pItems[i].getRepositoryId(), "backInStockNotifyItem");
    }
  }

  /**
   * This method is used to do the actual email sending.
   *
   * @param pItems
   * @throws 
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
protected void sendEmail(RepositoryItem[] pItems) {

    for (int i = 0; i < pItems.length; i++) {
      String skuId = (String) pItems[i].getPropertyValue("catalogRefId");
      String productId = (String) pItems[i].getPropertyValue("productId");
      
      // Apply vaildators to the item and send email in case the item pass validation
      if (validateItem(skuId, productId)) {
        Object[] receipents = { pItems[i].getPropertyValue("emailAddress") };
      
        Map params = new HashMap();
        params.put("skuId", skuId);
        params.put("productId", productId);
        params.put("firstName", retrieveUserFirstNameByEmail((String) pItems[i].getPropertyValue("emailAddress")));
        getEmailManager().sendBackInstockNotification(params, receipents);
        
        vlogDebug("Done sending back in stock notification emails.");
      }
      else {
          vlogDebug("No back in stock notification emails will be send for item with sku id : " +  skuId + " and product id : " + productId + 
              "because it doen't pass validation");
      }
      
    } 
  }
  
  
  /**
   * retrieve appropriate back in stock repository items.
   *
   * @param pEmail - user email 
   * @return String firstName
   */
  protected String retrieveUserFirstNameByEmail(String pEmail){
	  RepositoryItem[] lUserItems = getProfileTools().getProfileItemFinder().findByEmail(pEmail, null);
	  if(lUserItems!=null && lUserItems.length>0){
		  return (String) lUserItems[0].getPropertyValue("firstName");
	  }
	return "";
  }

  /**
   * Apply mValidators to items with passed in sku id and product id. 
   * 
   * @param pSkuId the items sku id
   * @param pProductId the items product id
   * @return
   */
  protected boolean validateItem(String pSkuId, String pProductId) {
	RepositoryItem sku = null;
	RepositoryItem product = null;
    try {
      MutableRepository rep = getCatalogRepository();
      sku = rep.getItem(pSkuId, "sku");
      product = rep.getItem(pProductId, "product");     
    } catch (RepositoryException e) {
        vlogDebug("Can't get item with sku id: " + pSkuId + " and product id: " + pProductId);
      // No validation could be done
      return false;
    }   
    if(sku != null && product != null){
    	return true;
    }
    return false;
  }

  
  /**
   * Perform the query to retrieve appropriate back in stock repository items.
   *
   * @param pSkuId - sku ids
   * @return repository items
   * @throws RepositoryException if repository error occurs
   */
  protected RepositoryItem[] retrieveBackInStockNotifyItems(String pSkuId)
    throws RepositoryException {
    Repository repository = getProfileRepository();
    RepositoryView view = repository.getView("backInStockNotifyItem");

    Object[] params = new Object[] { pSkuId };

    RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_FIND_BISN_ITEMS);

    RepositoryItem[] items = statement.executeQuery(view, params);

    return items;
  }


  /**
   * @return profile repository.
   */
  public Repository getProfileRepository() {
    return mProfileRepository;
  }

  /**
   * @param repository - profile repository.
   */
  public void setProfileRepository(Repository repository) {
    mProfileRepository = repository;
  }

}
