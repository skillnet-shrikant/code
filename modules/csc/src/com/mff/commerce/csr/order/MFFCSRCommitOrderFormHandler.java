package com.mff.commerce.csr.order;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import com.aci.payment.creditcard.AciCreditCard;
import com.google.common.base.Strings;
import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.inventory.StockLevel;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.order.purchase.MFFCheckoutManager;
import com.mff.constants.MFFConstants;
import com.mff.email.MFFEmailManager;

import atg.commerce.CommerceException;
import atg.commerce.csr.order.CSRCommitOrderFormHandler;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.droplet.DropletException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.web.messaging.MessageConstants;
import atg.web.messaging.MessageTools;
import atg.web.messaging.RequestMessage;

public class MFFCSRCommitOrderFormHandler extends CSRCommitOrderFormHandler {

  MFFEmailManager emailManager;
  private FFRepositoryInventoryManager inventoryManager;
  private MFFCheckoutManager mCheckoutManager;
  private MessageTools messageTools;
  
  @Override
  public void preCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    vlogDebug("Entering preCommitOrder : pRequest, pResponse");
    
    List<String> errorMessages = checkForSkuAvailability(pRequest,(MFFOrderImpl)getOrder());
    if (errorMessages != null && errorMessages.size() > 0) {
      for(String errorMsg : errorMessages){
        addFormException(new DropletException(errorMsg));
      }
      return;
    }
    
    super.preCommitOrder(pRequest, pResponse);
  }
  
  @Override
  public void postCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering postCommitOrder : pRequest, pResponse");
    
    RepositoryItem profile = getCSRAgentTools().getCSREnvironmentTools().getEnvironmentTools().getActiveCustomerProfile();
    Order lastOrder = getShoppingCart().getLast();

    // decrement inventory for web/bopis
    getInventoryManager().decrementInventory(lastOrder, getOrder().getId(), getProfile().getRepositoryId());
    
    getEmailManager().sendOrderConfirmationMail((MFFOrderImpl)lastOrder, profile);
    
    if(getCheckoutManager().isSavePaymentMethod()){
      AciCreditCard creditCard = getCreditCardPaymentGroup((MFFOrderImpl)lastOrder);
      if(creditCard != null){
        getCommerceProfileTools().copyCreditCardToProfile(creditCard, getProfile(),getCheckoutManager().getPaymentName());
        getCheckoutManager().resetCheckoutValuesPostCommit();
      }
    }
    
    super.postCommitOrder(pRequest, pResponse);
    vlogDebug("Exiting postCommitOrder : pRequest, pResponse");
  }
  
  public boolean handleSendOrderConfirmationEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException{

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "MFFCSRCommitOrderFormHandler.handleSendOrderConfirmationEmail";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        
        RepositoryItem profile = getCSRAgentTools().getCSREnvironmentTools().getEnvironmentTools().getActiveCustomerProfile();
        String orderId = getShoppingCart().getCurrent().getId();
        
        try {
          // load core order
          Order coreOrder = getOrderManager().loadOrder(orderId);
          getEmailManager().sendOrderConfirmationMail((MFFOrderImpl)coreOrder, profile);
          RequestMessage message = new RequestMessage();
          // success message to agent
          message.setType(MessageConstants.TYPE_CONFIRMATION);
          message.setSummary("Order Confirmation successfully send to " + ((MFFOrderImpl)coreOrder).getContactEmail());
          getMessageTools().addMessage(message);
        } catch (CommerceException ce) {
          logError(ce);
        }
        
        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getCommitOrderUpdatesSuccessURL(), getCommitOrderUpdatesErrorURL(), pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
          if (rrm != null)
              rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  public MFFEmailManager getEmailManager() {
    return emailManager;
  }

  public void setEmailManager(MFFEmailManager pEmailManager) {
    emailManager = pEmailManager;
  }

  @Override
  public void sendConfirmationMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    
    String emailAddress = getConfirmationInfo().getToEmailAddress();
    RepositoryItem profile = getCSRAgentTools().getCSREnvironmentTools().getEnvironmentTools().getActiveCustomerProfile();
    Order lastOrder = getShoppingCart().getLast();
    
    getEmailManager().sendOrderConfirmationMail((MFFOrderImpl)lastOrder, profile, emailAddress);    
    
  }
  
  public AciCreditCard getCreditCardPaymentGroup(MFFOrderImpl currentOrder) {
    
    vlogDebug("Action: getpCreditCardPaymentGroup(currentOrder) for order id {0} and profile id {1}", getOrder().getId(), getProfile().getRepositoryId());
    
    @SuppressWarnings("unchecked")
    List<PaymentGroup> list = currentOrder.getPaymentGroups();

    // if there are no existing payment groups on the order, return null;
    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        PaymentGroup pg = (PaymentGroup) list.get(i);

        if (pg instanceof AciCreditCard) {
          return (AciCreditCard) pg;
        }
      }
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  private List<String> checkForSkuAvailability(DynamoHttpServletRequest pRequest,MFFOrderImpl currentOrder) {

    List<String> errorMessages = new ArrayList<String>();

    // Check for DC Inventory and Check for sku availability flags
    Map<String, StockLevel> skusStockLevel = ((MFFOrderManager) getOrderManager()).getStockLevelForSkusInOrder(currentOrder);
    
    List<MFFCommerceItemImpl> ciList = currentOrder.getCommerceItems();
    ListIterator<MFFCommerceItemImpl> ciIterator = ciList.listIterator();
    while (ciIterator.hasNext()) {
      MFFCommerceItemImpl ci = (MFFCommerceItemImpl) ciIterator.next();
      String skuId = ci.getCatalogRefId();
      long currentQty = ci.getQuantity();
      long availableStockLevel = 0;
      StockLevel stockLevel = skusStockLevel.get(skuId);
      if (stockLevel != null) {
        availableStockLevel = stockLevel.getStockLevel();
      }

      if (availableStockLevel <= 0 || currentQty > availableStockLevel) {
        String resourceMsg = MFFConstants.getEXTNResourcesMessage(pRequest, MFFConstants.MSG_SKU_ID_NOT_AVAILABLE);
        if(!Strings.isNullOrEmpty(resourceMsg)){
          errorMessages.add(MessageFormat.format(resourceMsg, skuId));
        }else{
          errorMessages.add("We are sorry but sku " + skuId + " is not available at this time");
        }
      }
    }
    
    return errorMessages;

  }

  /**
   * @return the inventoryManager
   */
  public FFRepositoryInventoryManager getInventoryManager() {
    return inventoryManager;
  }

  /**
   * @param pInventoryManager the inventoryManager to set
   */
  public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
    inventoryManager = pInventoryManager;
  }
  
  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }
  
  /**
   * @return the messageTools
   */
  public MessageTools getMessageTools() {
    return messageTools;
  }

  /**
   * @param messageTools
   *          the messageTools to set
   */
  public void setMessageTools(MessageTools messageTools) {
    this.messageTools = messageTools;
  }

}
