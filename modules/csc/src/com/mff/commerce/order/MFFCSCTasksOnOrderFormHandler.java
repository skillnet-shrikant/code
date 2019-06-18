package com.mff.commerce.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.google.common.base.Strings;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.commerce.states.MFFPaymentGroupStates;

import atg.commerce.CommerceException;
import atg.commerce.csr.environment.CSREnvironmentTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.states.StateDefinitions;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.access.SiteAccessException;
import atg.repository.ItemDescriptorImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryImpl;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.svc.agent.environment.EnvironmentException;

import atg.web.messaging.MessageConstants;
import atg.web.messaging.MessageTools;
import atg.web.messaging.RequestMessage;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;
import oms.commerce.order.MFFOMSOrderManager;
import oms.commerce.order.OMSOrderManager;

/**
 * MFFCSCTasksOnOrderFormHandler includes different tasks agents perform on
 * orders. It sends the order in forced allocation status to the allocation
 * pipeline with the store that has been pre-selected. It also allows the agent
 * to cancel the items in force allocation status. This class includes handler
 * methods to manage fulfilling gift cards, including adding a gift card number,
 * and setting the gift card item in pending activation.
 */
public class MFFCSCTasksOnOrderFormHandler extends GenericFormHandler {

  private static final String REQUEST_ENTRY_FULFILL_GIFTCARD = "MFFCSCTasksOnOrderFormHandler.handleFulfillGiftCard";
  private static final String REQUEST_ENTRY_REFUND_GIFTCARD = "MFFCSCTasksOnOrderFormHandler.handleFulfillRefundGiftCard";
  private static final String REQUEST_ENTRY_FORCE_ALLOCATION = "MFFCSCTasksOnOrderFormHandler.handleForceAllocation";
  private static final String REQUEST_ENTRY_FORCE_CANCEL_ITEMS = "MFFCSCTasksOnOrderFormHandler.handleCancelForceAllocItems";
  private static final String REQUEST_ENTRY_REMORSE_CANCEL_ITEMS = "MFFCSCTasksOnOrderFormHandler.handleCancelItems";
  private static final String REQUEST_ENTRY_CHANGE_RETURNABLE_FLAG = "MFFCSCTasksOnOrderFormHandler.handleChangeReturnableFlag";

  private TransactionManager transactionManager;
  private RepeatingRequestMonitor repeatingRequestMonitor;
  private MessageTools messageTools;
  private OMSOrderManager omsOrderManager;
  private PipelineManager fulfillmentPipelineManager;
  private Repository omsOrderRepository;

  private String tasksOnOrderSuccessUrl;
  private String tasksOnOrderErrorUrl;
  private List<String> cancelItemsId = new ArrayList<String>();

  private String storeCode; // store code to allocate the order
  private String orderId; // order id
  private boolean activationCheckbox;
  private boolean returnableFlag;
  private int inputSize, inputSizeGC, inputSizeRGC;
  private String cancelOrderReasonCode;
  private String splitCommerceItemId;
  private String submitAction;
  private CSREnvironmentTools envTools;

  private MFFLineItemInput[] lineItemInput;
  private MFFLineItemInput[] lineItemInputGC;
  private MFFLineItemInput[] lineItemInputRGC;

  public boolean handleFulfillGiftCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSCTasksOnOrderFormHandler.handleFulfillGiftCard");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    RequestMessage message = new RequestMessage();
    boolean rollbackTransaction = true;
    boolean updateRequired = false;
    boolean itemSelected = false;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_FULFILL_GIFTCARD)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

        if (null != getOrderId()) {
          invalidateOrderLocalCache(getOrderId());
          Order omsOrder = getOmsOrderManager().loadOrder(getOrderId());
          if (activationCheckbox) {
            // Action: Set ready for Activation
            // a. Collect commerce items to be activated
            List<String> activateItemsId = new ArrayList<String>();
            for (int i = 0; i < getLineItemInputGC().length; i++) {
              MFFLineItemInput lineInput2 = getLineItemInputGC()[i];
              if (lineInput2.isItemSelected() && !StringUtils.isEmpty(lineInput2.getGiftCardNumber())) {
                activateItemsId.add(lineInput2.getCommerceItemId());
              }
            }
            // b. Send list of commerce items to be Pending Activation
            if (null != activateItemsId && activateItemsId.size() > 0) {
              Map lPipelineParams = new HashMap();
              lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, omsOrder);
              lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP, activateItemsId);
              try {
                vlogInfo("Calling handleStoreShipment pipeline for order id {0}", getOrderId());
                getFulfillmentPipelineManager().runProcess("handleGiftCardFulfillActivation", lPipelineParams);
                vlogInfo("Successfully setting items to pending activation for order id: {0}", getOrderId());
              } catch (RunProcessException ex) {
                vlogError(ex, "Error cancelling items for order id {0}", getOrderId());
              }
            } else {
              // Validation error message to agent
              message.setType(MessageConstants.TYPE_ERROR);
              message.setSummary("Error setting Gif Card(s) activation. Please select the item checkbox and make sure there is a gift card number");
              getMessageTools().addMessage(message);
              return false;
            }
          } else {
            // Action: Update GC Number
            synchronized (omsOrder) {
              List<String> selectedGCNumbers = new ArrayList<String>();
              for (int i = 0; i < getLineItemInputGC().length; i++) {
                MFFLineItemInput lineInput2 = getLineItemInputGC()[i];
                if (lineInput2.isItemSelected()) {
                  itemSelected = true;
                  MFFCommerceItemImpl gcCi = (MFFCommerceItemImpl) omsOrder.getCommerceItem(lineInput2.getCommerceItemId());
                  if (null != gcCi.getGiftCardNumber() && gcCi.getGiftCardNumber().equals(lineInput2.getGiftCardNumber())) {
                    // update not needed
                    break;
                  }
                  if (!StringUtils.isNumericOnly(lineInput2.getGiftCardNumber()) || 
                      (lineInput2.getGiftCardNumber().length() != 13 && lineInput2.getGiftCardNumber().length() != 16 && lineInput2.getGiftCardNumber().length() != 19)) {
                    // Validation error message to agent
                    message.setType(MessageConstants.TYPE_ERROR);
                    message.setSummary("Error updating Gif Card(s). Please use 13, 16, or 19 numeric digits (i.e. 1234567890123456)");
                    getMessageTools().addMessage(message);
                    return false;
                  }
                  // Validate no duplicated GC numbers in the same submission
                  if(selectedGCNumbers.contains(lineInput2.getGiftCardNumber()))
                  {
                    // Validation error message to agent
                    message.setType(MessageConstants.TYPE_ERROR);
                    message.setSummary("Error updating Gif Card(s). Duplicated gift card number found in submission");
                    getMessageTools().addMessage(message);
                    return false;
                  }
                  // Validate no duplicated GC numbers in the same order
                  if(isGCNumberinOrderCI(omsOrder, lineInput2.getGiftCardNumber()))
                  {
                    // Validation error message to agent
                    message.setType(MessageConstants.TYPE_ERROR);
                    message.setSummary("Error updating Gif Card(s). Gift card number already used in this order");
                    getMessageTools().addMessage(message);
                    return false;
                  }
                  selectedGCNumbers.add(lineInput2.getGiftCardNumber());
                  // Update the item's gift card number
                  gcCi.setGiftCardNumber(lineInput2.getGiftCardNumber());
                  updateRequired = true;
                }
              }
              if (!itemSelected) {
                // Validation error message to agent - item not selected
                message.setType(MessageConstants.TYPE_ERROR);
                message.setSummary("Gift card number not updated, please select the item checkbox first");
                getMessageTools().addMessage(message);
                return false;
              }
              if (updateRequired) {
                // Update Order
                getOmsOrderManager().updateOrder(omsOrder);
                // success message to agent
                message.setType(MessageConstants.TYPE_CONFIRMATION);
                message.setSummary("Gift Card(s) number updated successfully");
                getMessageTools().addMessage(message);
              }
            }
          }
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | CommerceException e) {
        // error message to be displayed for the agent
        vlogError(e, "An exception occurred during gift card fulfillment");
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error during gift card fulfillment");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_FULFILL_GIFTCARD);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
  }

  private void invalidateOrderLocalCache(String pOrderId)
  {
    RepositoryImpl repository = (RepositoryImpl)getOmsOrderRepository();
    ItemDescriptorImpl itemDescriptor;
    try {
      itemDescriptor = (ItemDescriptorImpl)repository.getItemDescriptor("order");
      itemDescriptor.removeItemFromCache(pOrderId);
    } catch (RepositoryException e) {
      vlogError(e, "A repository exception occurred while trying to remove order item from cache");
    }    
  }
  
  public boolean isGCNumberinOrderCI(Order pOrder, String pGiftCardNumber) 
  {
    List<MFFCommerceItemImpl> commerceItems = pOrder.getCommerceItems();
    for (MFFCommerceItemImpl lCommerceItem : commerceItems) {
      if (null!=lCommerceItem.getGiftCardNumber() && lCommerceItem.getGiftCardNumber().equals(pGiftCardNumber)) return true;
    }
    return false;
  }
  
  public boolean isGCNumberinOrderPG(Order pOrder, String pGiftCardNumber) 
  {
    List<PaymentGroup> paymentGroups = pOrder.getPaymentGroups();
    for (PaymentGroup lPaymentGroup : paymentGroups) {
      if(lPaymentGroup.getPaymentGroupClassType().equals("giftCard")) {
        MFFGiftCardPaymentGroup gc = (MFFGiftCardPaymentGroup) lPaymentGroup;
        if (null!=gc.getCardNumber() && gc.getCardNumber().equals(pGiftCardNumber)) return true;
      }
    }
    return false;
  }
  
  public boolean handleFulfillRefundGiftCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSCTasksOnOrderFormHandler.handleFulfillRefundGiftCard");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    RequestMessage message = new RequestMessage();
    boolean rollbackTransaction = true;
    boolean updateRequired = false;
    boolean itemSelected = false;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_REFUND_GIFTCARD)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

        if (null != getOrderId()) {
          Order omsOrder = getOmsOrderManager().loadOrder(getOrderId());
          if (activationCheckbox) {
            // Action: Set ready for Activation
            // a. Collect commerce items to be activated
            List<String> activateItemsId = new ArrayList<String>();
            for (int i = 0; i < getLineItemInputRGC().length; i++) {
              MFFLineItemInput lineInput2 = getLineItemInputRGC()[i];
              if (lineInput2.isItemSelected() && !StringUtils.isEmpty(lineInput2.getGiftCardNumber())) {
                activateItemsId.add(lineInput2.getCommerceItemId());
              }
            }
            // b. update to be Pending Activation
            if (null != activateItemsId && activateItemsId.size() > 0) {
              for (String lpaygroupId : activateItemsId) {
                PaymentGroup gcPaygroup = omsOrder.getPaymentGroup(lpaygroupId);
                gcPaygroup.setState(StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.TO_BE_ACTIVATED));
                updateRequired=true;
              }
              if (updateRequired) {
                // Update Order
                getOmsOrderManager().updateOrder(omsOrder);
                // success message to agent
                message.setType(MessageConstants.TYPE_CONFIRMATION);
                message.setSummary("Gift Card(s) sucessfully in pending activation");
                getMessageTools().addMessage(message);
              }
            } else {
              // Validation error message to agent
              message.setType(MessageConstants.TYPE_ERROR);
              message.setSummary("Error setting Refundable Gif Card(s) activation. Please select the item checkbox and make sure there is a gift card number");
              getMessageTools().addMessage(message);
              return false;
            }
          } else {
            // Action: Update GC Number
            synchronized (omsOrder) {
              List<String> selectedGCNumbers = new ArrayList<String>();
              for (int i = 0; i < getLineItemInputRGC().length; i++) {
                MFFLineItemInput lineInput2 = getLineItemInputRGC()[i];
                if (lineInput2.isItemSelected()) {
                  itemSelected = true;
                  MFFGiftCardPaymentGroup gcPg = (MFFGiftCardPaymentGroup) omsOrder.getPaymentGroup(lineInput2.getCommerceItemId());
                  if (null != gcPg.getCardNumber() && gcPg.getCardNumber().equals(lineInput2.getGiftCardNumber())) {
                    // update not needed
                    break;
                  }
                  if (!StringUtils.isNumericOnly(lineInput2.getGiftCardNumber()) || 
                      (lineInput2.getGiftCardNumber().length() != 13 && lineInput2.getGiftCardNumber().length() != 16 && lineInput2.getGiftCardNumber().length() != 19)) {
                    // Validation error message to agent
                    message.setType(MessageConstants.TYPE_ERROR);
                    message.setSummary("Error updating Gif Card(s). Please use 13, 16, or 19 numeric digits (i.e. 1234567890123456)");
                    getMessageTools().addMessage(message);
                    return false;
                  }
                  // Validate no duplicated GC numbers in the same submission
                  if(selectedGCNumbers.contains(lineInput2.getGiftCardNumber()))
                  {
                    // Validation error message to agent
                    message.setType(MessageConstants.TYPE_ERROR);
                    message.setSummary("Error updating Gif Card(s). Duplicated gift card number found in submission");
                    getMessageTools().addMessage(message);
                    return false;
                  }
                  // Validate no duplicated GC numbers in the same order
                  if(isGCNumberinOrderPG(omsOrder, lineInput2.getGiftCardNumber()))
                  {
                    // Validation error message to agent
                    message.setType(MessageConstants.TYPE_ERROR);
                    message.setSummary("Error updating Gif Card(s). Gift card number already used in this order");
                    getMessageTools().addMessage(message);
                    return false;
                  }
                  selectedGCNumbers.add(lineInput2.getGiftCardNumber());
                  // Update the item's gift card number
                  gcPg.setCardNumber(lineInput2.getGiftCardNumber());
                  updateRequired = true;
                }
              }
              if (!itemSelected) {
                // Validation error message to agent - item not selected
                message.setType(MessageConstants.TYPE_ERROR);
                message.setSummary("Gift card number not updated, please select the item checkbox first");
                getMessageTools().addMessage(message);
                return false;
              }
              if (updateRequired) {
                // Update Order
                getOmsOrderManager().updateOrder(omsOrder);
                // success message to agent
                message.setType(MessageConstants.TYPE_CONFIRMATION);
                message.setSummary("Gift Card(s) number updated successfully");
                getMessageTools().addMessage(message);
              }
            }
          }
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | CommerceException e) {
        // error message to be displayed for the agent
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error during gift card fulfillment");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_REFUND_GIFTCARD);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
  }
  
  /**
   * handleForceAllocateOrder is the handler method called to send the order to
   * the allocation pipeline using the pre-selected store code. The store code
   * will be set in the pipeline params to avoid the allocation ora proc.
   *
   * @param pRequest
   *          the request object
   * @param pResponse
   *          the response object
   * @return indicates whether to continue the processing after the handler is
   *         finished
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleForceAllocateOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSCTasksOnOrderFormHandler.handleForceAllocateOrder");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_FORCE_ALLOCATION)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        if (!StringUtils.isEmpty(getOrderId()) && !StringUtils.isEmpty(getStoreCode())) {
          runForceAllocation();
          setLineItemInput(null);
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | RunProcessException e) {
        // error message to be displayed for the agent
        RequestMessage message = new RequestMessage();
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error running force allocation");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_FORCE_ALLOCATION);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void runForceAllocation() throws RunProcessException {
    Order omsOrder = null;
    try {
      omsOrder = getOmsOrderManager().loadOrder(getOrderId());
    } catch (CommerceException e) {
      vlogError(e, "Unable to load order ID: {0}", getOrderId());
      return;
    }
    
    List<String> itemsToAllocate = new ArrayList<String>();
    for (int i = 0; i < getLineItemInput().length; i++) {
      if (getLineItemInput()[i].isItemSelected()) {
        MFFCommerceItemImpl lCommerceItem;
        try {
          lCommerceItem = getCommerceItem(omsOrder, getLineItemInput()[i].getCommerceItemId());
          if(lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.FORCED_ALLOCATION)){
            itemsToAllocate.add(getLineItemInput()[i].getCommerceItemId());
          }
        } catch (Exception e) {
          logError(e);
        }
      }
    }
    Map lPipelineParams = new HashMap();
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, omsOrder);
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_FORCED_STORE, getStoreCode());
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_FORCED_ALLOCATED_ITEMS, itemsToAllocate);
    try {
      vlogInfo("Calling allocation pipeline for order id {0} and store {1}", getOrderId(), getStoreCode());
      PipelineResult pResult = getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_ALLOCATE_ORDER, lPipelineParams);
      if (pResult.hasErrors()) {
        vlogError("Errors found running force allocation for order id {0} and store {1}", getOrderId(), getStoreCode());
        Object[] keys = pResult.getErrorKeys();
        RequestMessage message = new RequestMessage();
        for (int i = 0; i < keys.length; i++) {
          message.setType(MessageConstants.TYPE_ERROR);
          message.setSummary((String) pResult.getError(keys[i]));
          getMessageTools().addMessage(message);
        }
      } else {
        vlogInfo("Successfully allocated order id: {0} to store {1}", getOrderId(), getStoreCode());
      }
    } catch (RunProcessException ex) {
      vlogError(ex, "Error allocating order id {0}", getOrderId());
    }
  }
  
  protected MFFCommerceItemImpl getCommerceItem(Order pOrder, String lCommerceItemId) throws Exception {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    MFFCommerceItemImpl lCommerceItem;
    try {
      lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lCommerceItemId);
    } catch (CommerceItemNotFoundException ex) {
      String lErrorMessage = String.format("Commerce item not found for decline order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Commerce item not found for decline order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    }
    return lCommerceItem;
  }

  /**
   * handleCancelForceAllocItems is the handler method called to collect and
   * cancel the commerce items in forced allocation status by using the
   * cancellation pipeline.
   *
   * @param pRequest
   *          the request object
   * @param pResponse
   *          the response object
   * @return indicates whether to continue the processing after the handler is
   *         finished
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleCancelForceAllocItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSCTasksOnOrderFormHandler.handleCancelForceAllocItems");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_FORCE_CANCEL_ITEMS)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        if (!StringUtils.isEmpty(getOrderId())) {
          runCancelItems(getOrderId());
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | RunProcessException e) {
        // error message to be displayed for the agent
        RequestMessage message = new RequestMessage();
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error running cancel items in forced allocation");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_FORCE_CANCEL_ITEMS);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
  }

  protected void runCancelItems(String pOrderId) throws RunProcessException {
    Order omsOrder = null;
    try {
      omsOrder = getOmsOrderManager().loadOrder(pOrderId);
    } catch (CommerceException e) {
      vlogError(e, "Unable to load order ID: {0}", pOrderId);
      return;
    }

    if (null != omsOrder) {
      // Collect commerce items to be cancelled
      List<CommerceItem> lCommerceItems = omsOrder.getCommerceItems();
      for (CommerceItem lCommerceItem : lCommerceItems) {
        if (null != lCommerceItem && lCommerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.FORCED_ALLOCATION)) {
          // Create item allocations on a forced allocation order
          getCancelItemsId().add(lCommerceItem.getId());
        }
      }

      Map lPipelineParams = new HashMap();
      lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, omsOrder);
      lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL, getCancelItemsId());
      lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC,"Item in force allocation cancelled by agent.");

      try {
        vlogInfo("Calling cancel items pipeline for order id {0}", pOrderId);
        getFulfillmentPipelineManager().runProcess("handleCancel", lPipelineParams);
        vlogInfo("Successfully cancel forced allocation items for order id: {0}", pOrderId);
      } catch (RunProcessException ex) {
        vlogError(ex, "Error cancelling items for order id {0}", pOrderId);
      }
    } else {
      vlogError("Error found with order ID: {0}", pOrderId);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public boolean handleCancelItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSCTasksOnOrderFormHandler.handleCancelItem");
    
    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    RequestMessage message = new RequestMessage();
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_REMORSE_CANCEL_ITEMS)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

        if (null != getOrderId()) {
          Order omsOrder = getOmsOrderManager().loadOrder(getOrderId());
          HashMap<String,String> itemToReasonCodeMap = new HashMap<String,String>();
            // a. Collect commerce item ids to be cancelled
            for (int i = 0; i < getLineItemInput().length; i++) {
              if (getLineItemInput()[i].isItemSelected()) {
                cancelItemsId.add(getLineItemInput()[i].getCommerceItemId());
                if(!Strings.isNullOrEmpty(getCancelOrderReasonCode())){
                  itemToReasonCodeMap.put(getLineItemInput()[i].getCommerceItemId(), getCancelOrderReasonCode());
                }
              }
            }
            // b. send to handle cancel pipeline
            Map lPipelineParams = new HashMap();
            lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, omsOrder);
            lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL, getCancelItemsId());
            lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC,"Item cancelled by agent.");
            lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP,itemToReasonCodeMap);
            try {
              vlogInfo("Calling cancel items pipeline for order id {0}", getOrderId());
              getFulfillmentPipelineManager().runProcess("handleCancel", lPipelineParams);
              setLineItemInput(null);
              vlogInfo("Successfully cancel items for order id: {0}", getOrderId());
            } catch (RunProcessException ex) {
              vlogError(ex, "Error cancelling items for order id {0}", getOrderId());
            }
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | CommerceException e) {
        // error message to be displayed for the agent
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error cancelling items");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_REMORSE_CANCEL_ITEMS);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
  }
  
  public boolean handleChangeReturnableFlag(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSCTasksOnOrderFormHandler.handleChangeReturnableFlag");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_CHANGE_RETURNABLE_FLAG)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        if (!StringUtils.isEmpty(getOrderId())) {
          MFFOrderImpl omsOrder = (MFFOrderImpl) getOmsOrderManager().loadOrder(getOrderId());
          if(omsOrder.isReturnablePPS()!=isReturnableFlag())
          {
            vlogDebug("Returnable in PPS flag to change from {0} to {1} for order id {2}",omsOrder.isReturnablePPS(),isReturnableFlag(),omsOrder.getId());
            synchronized (omsOrder) {
              omsOrder.setReturnablePPS(isReturnableFlag());
              getOmsOrderManager().updateOrder(omsOrder);
            }
          }
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | CommerceException e) {
        // error message to be displayed for the agent
        RequestMessage message = new RequestMessage();
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error running change returnable flag");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_CHANGE_RETURNABLE_FLAG);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
  }
  
  public boolean handleForceAllocateAndCancelItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    vlogDebug("handleForceAllocateAndCancelItem : submitAction - {0}",getSubmitAction());
    if(!Strings.isNullOrEmpty(getSubmitAction()) && getSubmitAction().equalsIgnoreCase("forceAllocation")){
      return handleForceAllocateOrder(pRequest, pResponse);
    }
    
    return handleCancelItem(pRequest, pResponse);
  }
  
  public boolean handleSplitItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSCTasksOnOrderFormHandler.handleSplitItems");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry("splititems")) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        if (!StringUtils.isEmpty(getOrderId())) {
          vlogDebug("handleSplitItems : Split CommerceItem Id - {0}",getSplitCommerceItemId());
          
          if(Strings.isNullOrEmpty(getSplitCommerceItemId())){
            addFormException(new DropletException("CommerceItemId is null/empty"));
            checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
          }
          
          MFFOrderImpl omsOrder = (MFFOrderImpl) getOmsOrderManager().loadOrder(getOrderId());
          
          synchronized (omsOrder) {
            List<ItemAllocation> lItemAllocations = new ArrayList<ItemAllocation>();
            
            if(!Strings.isNullOrEmpty(getSplitCommerceItemId())){
              MFFCommerceItemImpl ci = (MFFCommerceItemImpl) omsOrder.getCommerceItem(getSplitCommerceItemId());
             
              ItemAllocation lItemAllocation = new ItemAllocation();
              lItemAllocation.setOrderId(getOrderId());
              lItemAllocation.setCommerceItemId(ci.getId());
              lItemAllocation.setQuantity(ci.getQuantity());
              lItemAllocation.setSkuId(ci.getCatalogRefId());
              lItemAllocation.setSplitItem(true);
              vlogDebug(lItemAllocation.toString());
              lItemAllocations.add(lItemAllocation);
                
              ((MFFOMSOrderManager)getOmsOrderManager()).splitCommerceItemsForAllocation(omsOrder,lItemAllocations,MFFCommerceItemStates.FORCED_ALLOCATION);
              getOmsOrderManager().updateOrder(omsOrder);
              getEnvTools().changeViewOrder(getOrderId());
            }
          }
          
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | CommerceException e) {
        // error message to be displayed for the agent
        RequestMessage message = new RequestMessage();
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error running change returnable flag");
        getMessageTools().addMessage(message);
      } catch (EnvironmentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (SiteAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_CHANGE_RETURNABLE_FLAG);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getTasksOnOrderSuccessUrl(), getTasksOnOrderErrorUrl(), pRequest, pResponse);
  }
  
  protected void setTransactionToRollbackOnly() throws SystemException {
    TransactionManager tm = getTransactionManager();
    if (tm != null) {
      tm.setRollbackOnly();
    }
  }

  /**
   * @return the transactionManager
   */
  public TransactionManager getTransactionManager() {
    return transactionManager;
  }

  /**
   * @param transactionManager
   *          the transactionManager to set
   */
  public void setTransactionManager(TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * @return the repeatingRequestMonitor
   */
  public RepeatingRequestMonitor getRepeatingRequestMonitor() {
    return repeatingRequestMonitor;
  }

  /**
   * @param repeatingRequestMonitor
   *          the repeatingRequestMonitor to set
   */
  public void setRepeatingRequestMonitor(RepeatingRequestMonitor repeatingRequestMonitor) {
    this.repeatingRequestMonitor = repeatingRequestMonitor;
  }

  /**
   * @return the tasksOnOrderSuccessUrl
   */
  public String getTasksOnOrderSuccessUrl() {
    return tasksOnOrderSuccessUrl;
  }

  /**
   * @param tasksOnOrderSuccessUrl
   *          the tasksOnOrderSuccessUrl to set
   */
  public void setTasksOnOrderSuccessUrl(String tasksOnOrderSuccessUrl) {
    this.tasksOnOrderSuccessUrl = tasksOnOrderSuccessUrl;
  }

  /**
   * @return the tasksOnOrderErrorUrl
   */
  public String getTasksOnOrderErrorUrl() {
    return tasksOnOrderErrorUrl;
  }

  /**
   * @param tasksOnOrderErrorUrl
   *          the tasksOnOrderErrorUrl to set
   */
  public void setTasksOnOrderErrorUrl(String tasksOnOrderErrorUrl) {
    this.tasksOnOrderErrorUrl = tasksOnOrderErrorUrl;
  }

  public String getStoreCode() {
    return storeCode;
  }

  public void setStoreCode(String storeCode) {
    this.storeCode = storeCode;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
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

  public OMSOrderManager getOmsOrderManager() {
    return omsOrderManager;
  }

  public void setOmsOrderManager(OMSOrderManager omsOrderManager) {
    this.omsOrderManager = omsOrderManager;
  }

  public PipelineManager getFulfillmentPipelineManager() {
    return fulfillmentPipelineManager;
  }

  public void setFulfillmentPipelineManager(PipelineManager fulfillmentPipelineManager) {
    this.fulfillmentPipelineManager = fulfillmentPipelineManager;
  }

  public List<String> getCancelItemsId() {
    return cancelItemsId;
  }

  public void setCancelItemsId(List<String> cancelItemsId) {
    this.cancelItemsId = cancelItemsId;
  }

  /**
   * @return the lineItemInput
   */
  public MFFLineItemInput[] getLineItemInput() {
    if (lineItemInput == null) {
      setLineItemInput(new MFFLineItemInput[getInputSize()]);
      for (int index = 0; index < getInputSize(); index++) {
        MFFLineItemInput input = new MFFLineItemInput();
        lineItemInput[index] = input;
      }
    }
    return lineItemInput;
  }

  /**
   * @param lineItemInput
   *          the lineItemInput to set
   */
  public void setLineItemInput(MFFLineItemInput[] lineItemInput) {
    this.lineItemInput = lineItemInput;
  }

  /**
   * @return the lineItemInputGC
   */
  public MFFLineItemInput[] getLineItemInputGC() {
    if (lineItemInputGC == null) {
      setLineItemInputGC(new MFFLineItemInput[getInputSizeGC()]);
      for (int index = 0; index < getInputSizeGC(); index++) {
        MFFLineItemInput input = new MFFLineItemInput();
        lineItemInputGC[index] = input;
      }
    }
    return lineItemInputGC;
  }

  /**
   * @param pLineItemInputGC the lineItemInputGC to set
   */
  public void setLineItemInputGC(MFFLineItemInput[] pLineItemInputGC) {
    lineItemInputGC = pLineItemInputGC;
  }
  
  /**
   * @return the lineItemInputGC
   */
  public MFFLineItemInput[] getLineItemInputRGC() {
    if (lineItemInputRGC == null) {
      setLineItemInputRGC(new MFFLineItemInput[getInputSizeRGC()]);
      for (int index = 0; index < getInputSizeRGC(); index++) {
        MFFLineItemInput input = new MFFLineItemInput();
        lineItemInputRGC[index] = input;
      }
    }
    return lineItemInputRGC;
  }

  /**
   * @param pLineItemInputGC the lineItemInputGC to set
   */
  public void setLineItemInputRGC(MFFLineItemInput[] pLineItemInputRGC) {
    lineItemInputRGC = pLineItemInputRGC;
  }

  /**
   * @return the inputSizeRGC
   */
  public int getInputSizeRGC() {
    return inputSizeRGC;
  }

  /**
   * @param pInputSizeRGC the inputSizeRGC to set
   */
  public void setInputSizeRGC(int pInputSizeRGC) {
    inputSizeRGC = pInputSizeRGC;
  }

  /**
   * @return the activationCheckbox
   */
  public boolean isActivationCheckbox() {
    return activationCheckbox;
  }

  /**
   * @param activationCheckbox
   *          the activationCheckbox to set
   */
  public void setActivationCheckbox(boolean activationCheckbox) {
    this.activationCheckbox = activationCheckbox;
  }

  /**
   * @return the inputSize
   */
  public int getInputSize() {
    return inputSize;
  }

  /**
   * @param inputSize the inputSize to set
   */
  public void setInputSize(int inputSize) {
    this.inputSize = inputSize;
  }

  /**
   * @return the inputSizeGC
   */
  public int getInputSizeGC() {
    return inputSizeGC;
  }

  /**
   * @param pInputSizeGC the inputSizeGC to set
   */
  public void setInputSizeGC(int pInputSizeGC) {
    inputSizeGC = pInputSizeGC;
  }

  /**
   * @return the returnableFlag
   */
  public boolean isReturnableFlag() {
    return returnableFlag;
  }

  /**
   * @param pReturnableFlag the returnableFlag to set
   */
  public void setReturnableFlag(boolean pReturnableFlag) {
    returnableFlag = pReturnableFlag;
  }

  /**
   * @return the omsOrderRepository
   */
  public Repository getOmsOrderRepository() {
    return omsOrderRepository;
  }

  /**
   * @param pOmsOrderRepository the omsOrderRepository to set
   */
  public void setOmsOrderRepository(Repository pOmsOrderRepository) {
    omsOrderRepository = pOmsOrderRepository;
  }

  public String getCancelOrderReasonCode() {
    return cancelOrderReasonCode;
  }

  public void setCancelOrderReasonCode(String pCancelOrderReasonCode) {
    cancelOrderReasonCode = pCancelOrderReasonCode;
  }

  public String getSplitCommerceItemId() {
    return splitCommerceItemId;
  }

  public void setSplitCommerceItemId(String pSplitCommerceItemId) {
    splitCommerceItemId = pSplitCommerceItemId;
  }

  public CSREnvironmentTools getEnvTools() {
    return envTools;
  }

  public void setEnvTools(CSREnvironmentTools pEnvTools) {
    envTools = pEnvTools;
  }

  public String getSubmitAction() {
    return submitAction;
  }

  public void setSubmitAction(String pSubmitAction) {
    submitAction = pSubmitAction;
  }

}
