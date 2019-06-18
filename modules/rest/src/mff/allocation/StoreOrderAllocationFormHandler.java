package mff.allocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.constants.MFFConstants;
import com.mff.email.MFFEmailManager;

import atg.commerce.CommerceException;
import atg.commerce.csr.util.CSRAgentTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.commerce.util.TransactionLockService;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.repository.RepositoryException;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.lockmanager.TimeExceededException;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import oms.allocation.item.AllocationConstants;
import oms.allocation.store.StoreAllocationManager;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.order.OrderLockManager;

/**
 * 
 * @author vsingh
 *
 */
public class StoreOrderAllocationFormHandler extends PurchaseProcessFormHandler{
	
	private String successURL;
	private String errorURL;
	private PipelineManager fulfillmentPipelineManager;
	private StoreAllocationManager storeAllocationManager;
	private String orderId;
	private String signature;
	private String specialInstructions;
	private String comments;
	private String dateOfBirth;
	private StoreAllocationInput[] storeAllocationInput;
	private int commerceItemCount;
	private OMSOrderManager mOrderManager;
	private StoreAllocationResponse allocationResponse = new StoreAllocationResponse();
	private OrderLockManager mOrderLockManager;
	
	// input and output parameters for updateInPickingStatus
	private String storeId;
	private int orderCount;
	private InPickingInput[] inPickingInput;
	
	//input and output parameter for cancelOrder
	private String mOrderIdToCancel;
	private CancelOrderResponse cancelOrderResponse = new CancelOrderResponse();
	private MFFEmailManager mEmailManager;
	private String mCancelOrderReasonCode;
	private boolean mUseTransactionLock;
	
	// Using lock managers to prevent multiple requests
	// for the same order in a given instance
	// if requests are on different instances
	// concurrentupdateexception is to be expected.
	// This flag turns on/off the use of LocalLockManager for locking
	
	private boolean mUseLocalLockManager;
	
	private boolean mUseClientLockManager;
	
	private boolean useThreadForOwner;
	private CSRAgentTools csrAgentTools;
	
	public boolean isUseThreadForOwner() {
		return useThreadForOwner;
	}

	public void setUseThreadForLockName(boolean pUseThreadForOwner) {
		useThreadForOwner = pUseThreadForOwner;
	}

	// Timeout in milliseconds the request should wait for a lock
	// The LocalLockManager timeout does not appear to be kicking in
	// This is done to prevent a request from waiting forever on a lock
	private long lockTimeout;
	
	// ClientLockManager
	ClientLockManager clientLockManager;
	
	public ClientLockManager getClientLockManager() {
		return clientLockManager;
	}

	public void setClientLockManager(ClientLockManager pClientLockManager) {
		clientLockManager = pClientLockManager;
	}

	public long getLockTimeout() {
		return lockTimeout;
	}

	public void setLockTimeout(long pLockTimeout) {
		lockTimeout = pLockTimeout;
	}

	public boolean isUseLocalLockManager() {
		return mUseLocalLockManager;
	}

	public boolean isUseClientLockManager() {
		return mUseClientLockManager;
	}

	public void setUseClientLockManager(boolean pUseClientLockManager) {
		mUseClientLockManager = pUseClientLockManager;
	}

	public void setUseLocalLockManager(boolean pUseLocalLockManager) {
		mUseLocalLockManager = pUseLocalLockManager;
	}

	private Object getLockOwner() {
		Object lockOwner="pps";
		if(isUseThreadForOwner()) {
			lockOwner=Thread.currentThread();
		}
		return lockOwner;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean handleStoreSubmit(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse)
			throws ServletException, IOException
	{
		RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
		String myHandleMethod = "StoreOrderActionFormHandler.handleStoreSubmit";
		if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
		{
			Transaction tr = null;
			try {
				tr = ensureTransaction();

				if(getStoreAllocationInput() == null || getStoreAllocationInput().length == 0){
					addFormException(new DropletException("ArrayInput size is either null or zero."));
					return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
				}

				if(Strings.isNullOrEmpty(getOrderId())){
					addFormException(new DropletException("OrderId is null so cannot continue processing."));
					return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
				}

				if(isUseLocalLockManager()) {
					vlogInfo("Acquiring localLockManager lock for " + getOrderId());
					getConfiguration().getTransactionLockFactory().getServiceInstance().getClientLockManager().acquireWriteLock(getOrderId(), getLockOwner(), getLockTimeout());
					vlogInfo("Acquired localLockManager lock for " + getOrderId());
				} else if (isUseClientLockManager()) {
					vlogInfo("Acquiring ClientLockManager lock for " + getOrderId());
					boolean lockType = getClientLockManager().acquireWriteLock(getOrderId(), getLockOwner(),getLockTimeout());
					//getClientLockManager().acquireWriteLock(getOrderId(), "pps", getLockTimeout());
					vlogInfo("Acquired ClientLockManager lock for " + getOrderId() + " lock type global = " + lockType);
				}
				

				// Get the order ID
				Order lOmsOrder = null;
				try {
					lOmsOrder = getOmsOrderManager().loadOrder(getOrderId());
				} catch (CommerceException e) {
					vlogError (e, "Unable to load order ID: {0}", getOrderId());
					addFormException(new DropletException("Unable to load order " + getOrderId()));
					return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
				}

				MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;
				String orderId = lOrder.getId();

				vlogDebug("handleStoreSubmit - Current Order Id : {0}", orderId);

				synchronized(lOrder) {
					List<String> itemsToShip = new ArrayList<String>();
					List<String> itemsToShipSplit = new ArrayList<String>();
					List<String> itemsToAllocate = new ArrayList<String>();
					List<String> itemsToCancel = new ArrayList<String>();
					List<String> itemsToPickup = new ArrayList<String>();
					List<CommerceItem> ciShippedItems = new ArrayList<CommerceItem>();
					HashMap skuToCommerceItemMap = new HashMap<String,String>();
					HashMap<String,String> itemToTrackingNumberMap = new HashMap<String,String>();
					HashMap<String,String> itemToReasonCodeMap = new HashMap<String,String>();
					boolean isBopisOrder = lOrder.isBopisOrder();
					/*boolean cancelItem = false;

					if(isBopisOrder && lOrder.getCommerceItemCount() != getCommerceItemCount()){
            addFormException(new DropletException("Number of items in request doesn't match the items in order. "
                + "Please make a selection for each item in the order"));
            return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
					}*/

					StoreAllocationInput[] storeInputArr = getStoreAllocationInput();
					for(StoreAllocationInput storeInput : storeInputArr){
						// validate storeInput
						if(Strings.isNullOrEmpty(storeInput.getCommerceItemId())){
							addFormException(new DropletException("CommerceItemId is null so cannot continue processing."));
							return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
						} else if(Strings.isNullOrEmpty(storeInput.getReasonCode())){
							addFormException(new DropletException("ReasonCode is null for commerceItemId" + storeInput.getCommerceItemId() + " cannot continue processing."));
							return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
						} 

						String reasonCode = storeInput.getReasonCode();
						itemToReasonCodeMap.put(storeInput.getCommerceItemId(), reasonCode);

						if(reasonCode.equalsIgnoreCase(MFFConstants.REASON_CODE_SHIPPED)){
							String commerceItemId = storeInput.getCommerceItemId();
							if(getOmsOrderManager().isSplitOrderEnabled()){
                itemsToShip.add(commerceItemId);
              }else{
  							try {
  								MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, commerceItemId);
  								ciShippedItems.add(lCommerceItem);
  								String skuId = lCommerceItem.getCatalogRefId();
  								if(skuToCommerceItemMap != null && skuToCommerceItemMap.get(skuId) != null){
  									itemsToShipSplit.add(commerceItemId);
  								}else{
  									skuToCommerceItemMap.put(skuId, commerceItemId);
  									itemsToShip.add(commerceItemId);
  								}
  							} catch (Exception e) {
  								vlogError(e, "Error getting commerceItemId {0}",commerceItemId);
  							}
              }

							if(!Strings.isNullOrEmpty(storeInput.getTrackingNumber())){
								itemToTrackingNumberMap.put(storeInput.getCommerceItemId(), storeInput.getTrackingNumber());
							}
						} else if(reasonCode.equalsIgnoreCase(MFFConstants.REASON_CODE_READY_FOR_PICKUP)){
							itemsToPickup.add(storeInput.getCommerceItemId());
						}	else if(isBopisOrder){
							itemsToCancel.add(storeInput.getCommerceItemId());
							//cancelItem = true;
						} else{
							itemsToAllocate.add(storeInput.getCommerceItemId());
						}
					}

					vlogDebug("handleStoreSubmit - itemsToShip - {0},itemsToAllocate - {1}, itemsToPickup - {2}, itemsToCancel - {3},itemsToShipSplit - {4}",
							itemsToShip,itemsToAllocate,itemsToPickup,itemsToCancel,itemsToShipSplit);

					// run pipeline process
					Map lPipelineParams = new HashMap();
					lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder);
					lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP,itemToReasonCodeMap);
					lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_SPECIAL_INST,getSpecialInstructions());
					lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_COMMENTS,getComments());

					if(itemsToShip.size() > 0){
						lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP,itemsToShip);
						lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_TRACKING_NO, itemToTrackingNumberMap);
						if(lOrder.isBopisOrder()){
							if(!Strings.isNullOrEmpty(getSignature())){
								lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_SIGNATURE, getSignature());
							}
							if(!Strings.isNullOrEmpty(getDateOfBirth())){
								lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_DATE_OF_BIRTH, getDateOfBirth());
							}
						}
						if(itemsToShipSplit.size() > 0){
							lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_SUPPRESS_SHIPPED_EMAIL, true);
						}
						
						Profile agentProfile = getCsrAgentTools().getCSREnvironmentTools().getEnvironmentTools().getAgentProfile();
						if (agentProfile != null) {
	            lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_AGENT_USER_ID,agentProfile.getPropertyValue("login"));
	          }
					} 
					if(itemsToAllocate.size() > 0){
						lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_DECLINE,itemsToAllocate);
					} 
					if(itemsToPickup.size() > 0){
						lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_PICKUP,itemsToPickup);
					} 
					if(itemsToCancel.size() > 0){
						lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL,itemsToCancel);
						lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC,"Bopis Item rejected by store.");
					}

					if(itemsToShip.size() > 0 || itemsToAllocate.size() > 0 || itemsToPickup.size() > 0 || itemsToCancel.size() > 0){
						runStoreProcesstPipeline(lOrder, lPipelineParams);
					}else{
						vlogInfo ("Skipping running pipeline {0} for order number {1}/{2} and order state {3}", 
								AllocationConstants.PIPELINE_STORE_PROCESS,lOrder.getOrderNumber(),orderId,lOrder.getStateAsString());
					}

					if(itemsToShipSplit.size() > 0){

						for (String lCommerceItemId : itemsToShipSplit) {
							Map lPipelineParamsSplit = new HashMap();
							List<String> itemsToShipNew = new ArrayList<String>();
							itemsToShipNew.add(lCommerceItemId);

							lPipelineParamsSplit.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP,itemsToShipNew);
							lPipelineParamsSplit.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder);
							lPipelineParamsSplit.put(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP,itemToReasonCodeMap);
							lPipelineParamsSplit.put(AllocationConstants.PIPELINE_PARAMETER_SPECIAL_INST,getSpecialInstructions());
							lPipelineParamsSplit.put(AllocationConstants.PIPELINE_PARAMETER_COMMENTS,getComments());
							lPipelineParamsSplit.put(AllocationConstants.PIPELINE_PARAMETER_TRACKING_NO, itemToTrackingNumberMap);
							lPipelineParamsSplit.put(AllocationConstants.PIPELINE_PARAMETER_SUPPRESS_SHIPPED_EMAIL, true);

							runStoreProcesstPipeline(lOrder, lPipelineParamsSplit);
						}
						if(!getFormError()){
							getEmailManager().sendOrderShippedEmail(lOrder, ciShippedItems, itemToTrackingNumberMap);
						}
					}
				} // synchronized

				//If NO form errors are found, redirect to the success URL.
				//If form errors are found, redirect to the error URL.
				return checkFormRedirect (getSuccessURL(), getErrorURL(), pRequest, pResponse);
			} catch (DeadlockException  e) {
				logError("Deadlock waiting to lock " + getOrderId() + " with owner pps" );
				addFormException(new DropletException("This order " + getOrderId() + " is being edited in another session."));
				return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
			} catch (TimeExceededException e) {
				//e.printStackTrace();
				logError("Timed out waiting for lock " + getOrderId() + " with owner pps" );
				addFormException(new DropletException("This order " + getOrderId() + " is being edited in another session."));
				return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
			}
			finally {
				if(isUseLocalLockManager()) {
					try {
						//getConfiguration().getTransactionLockFactory().getServiceInstance().releaseTransactionLock("getOrderId()");
						logInfo("Releasing locallock " + getOrderId() + " with owner " + getLockOwner());
						getConfiguration().getTransactionLockFactory().getServiceInstance().getClientLockManager().releaseWriteLock(getOrderId(), getLockOwner());
						logInfo("Released clientlock " + getOrderId() + " with owner " + getLockOwner());
					} catch (LockManagerException e) {
						e.printStackTrace();
					}
				} else if (isUseClientLockManager()) {
						if(getClientLockManager().hasWriteLock(getOrderId(), getLockOwner())) {
							try {
								logInfo("Releasing clientlock " + getOrderId() + " with owner " + getLockOwner());
								getClientLockManager().releaseWriteLock(getOrderId(), getLockOwner());
								logInfo("Released clientlock " + getOrderId() + " with owner " + getLockOwner());
							} catch (LockManagerException e) {
								e.printStackTrace();
							}
						}
					
				}
				if (tr != null) commitTransaction(tr);
				if (rrm != null)
					rrm.removeRequestEntry(myHandleMethod);
			}
		}
		else {
			return false;
		}
	}
	
	private void runStoreProcesstPipeline(MFFOrderImpl pOrder,Map<String,String> pPipelineParams){
    String pipelineName = AllocationConstants.PIPELINE_STORE_PROCESS;
    vlogInfo ("Calling pipeline: {0} for order number {1}/{2}", pipelineName,pOrder.getOrderNumber(),orderId);
    
    try {
      
      if(getOrderLockManager().lockOrder(orderId, "pps")){
        PipelineResult result = getFulfillmentPipelineManager().runProcess(pipelineName, pPipelineParams);
        
        if(result.hasErrors()){
          Object[] keys = result.getErrorKeys();
          for (int i = 0; i < keys.length; i++) {
           addFormException(new DropletException((String)result.getError(keys[i])));
          }
        }else{
          getAllocationResponse().setOrder(pOrder);
          vlogInfo ("Successfully finished {0} for order number {1}/{2} and order state {3}", pipelineName,pOrder.getOrderNumber(),orderId,pOrder.getStateAsString());
        }
      }else{
        addFormException(new DropletException("Order is locked by another process. Please try again later"));
      }
    
    } catch (RunProcessException ex) {
      if(ex.getSourceException() != null && ex.getSourceException().getCause() != null && ex.getSourceException().getCause().getMessage() != null &&
          ex.getSourceException().getCause().getMessage().equalsIgnoreCase(AllocationConstants.PIPELINE_PARAMETER_INSUFFICIENT_FUNDS_ERROR_MESSAGE_STR)) {
        addFormException(new DropletException(ex.getSourceException().getCause().getMessage()));
      }else {
        addFormException(new DropletException("Error processing Order -" + orderId + ". Please try again later."));
      }
      vlogError(ex, "Error running pipeline {0} for order number {1}",pipelineName,orderId);
    }finally {
      getOrderLockManager().releaseOrder(orderId, "pps");
    }
  }
	
	/**
   * Get the commerce item from the order given the Commerce Item ID.
   * 
   * @param pOrder
   *            ATG Order
   * @param lCommerceItemId
   *            Commerce Item Id
   * @return Commerce item
   * @throws Exception
   */
  private MFFCommerceItemImpl getCommerceItem(Order pOrder, String lCommerceItemId) throws Exception {
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
	
	public boolean handleUpdateInPickingStatus(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "StoreOrderActionFormHandler.handleUpdateInPickingStatus";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        
        if(Strings.isNullOrEmpty(getStoreId())){
          addFormException(new DropletException("StoreId is null so cannot continue processing."));
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
        }
        
        String storeId = getStoreId();
        InPickingInput[] inPickingInpArr =  getInPickingInput();
        
        for(InPickingInput inPicking : inPickingInpArr){
          String orderId = inPicking.getOrderId();
          String tokens[] = StringUtils.splitStringAtCharacter(inPicking.getCommerceItemIds(), '|');
          for(String commerceItemId : tokens){
            try {
              getStoreAllocationManager().updateInPickingFlag(orderId, commerceItemId, storeId);
            } catch (CommerceException | RepositoryException e) {
              vlogError(e, "An exception happened updating InPicking Flag");
            }
          }
        }
        
        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getSuccessURL(), getErrorURL(), pRequest, pResponse);
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
  public boolean handleCancelOrder(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
	  
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "StoreOrderActionFormHandler.handleCancelOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        
        if(Strings.isNullOrEmpty(getOrderIdToCancel())){
          addFormException(new DropletException("OrderIdToCancel is null so cannot continue processing."));
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
        }
        
        // Get the order ID
        Order lOmsOrder = null;
        try {
          lOmsOrder = getOmsOrderManager().loadOrder(getOrderIdToCancel());
        } catch (CommerceException e) {
          vlogError (e, "Unable to load order ID: {0}", getOrderIdToCancel());
          addFormException(new DropletException("Unable to load order " + getOrderIdToCancel()));
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
        }
        
        MFFOrderImpl lOrder = (MFFOrderImpl) lOmsOrder;
        String orderNumber = lOrder.getOrderNumber();
        
        List<CommerceItem> items = lOrder.getCommerceItems();
        List<String> commerceItemIds = new ArrayList<String>();
        HashMap<String,String> itemToReasonCodeMap = new HashMap<String,String>();
        for(CommerceItem item : items){
          MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl)item;
          if(!lCommerceItem.getStateAsString().equalsIgnoreCase(MFFCommerceItemStates.CANCELLED)){
            commerceItemIds.add(item.getId());
            itemToReasonCodeMap.put(item.getId(), getCancelOrderReasonCode());
          }
        }
        
        if(commerceItemIds.size() > 0){
          
          String pipelineName = AllocationConstants.PIPELINE_CANCEL;
          Map lPipelineParams = new HashMap();
          lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, lOrder);
          lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL,commerceItemIds);
          lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC,"Order Cancelled by PPS");
          lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP,itemToReasonCodeMap);
          
          try {
            getFulfillmentPipelineManager().runProcess(AllocationConstants.PIPELINE_CANCEL, lPipelineParams);
            getCancelOrderResponse().setOrder(lOrder);
            vlogInfo ("Successfully finished {0} for order number {1} and order state {2}", pipelineName,orderNumber,lOrder.getStateAsString());
          } catch (RunProcessException ex) {
            addFormException(new DropletException("Error cancelling Order -"  + orderNumber + ". Please try again later."));
            vlogError(ex, "Error running pipeline {0} for order number {1}", AllocationConstants.PIPELINE_CANCEL,orderNumber);      
          }
        }
        
        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getSuccessURL(), getErrorURL(), pRequest, pResponse);
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
	
	@Override
  protected void acquireTransactionLock(DynamoHttpServletRequest pRequest) throws DeadlockException {
    
    if(isUseTransactionLock()){
      try {
        TransactionLockService service = getConfiguration().getTransactionLockFactory().getServiceInstance();
        if (service != null) {
          String uniqueKey = getUniqueKey();
          if (!Strings.isNullOrEmpty(uniqueKey)) {
            vlogInfo("acquireTransactionLock : Acquired Lock using uniqueKey - {0}",uniqueKey);
            pRequest.setAttribute("atg.PurchaseProcessFormHandlerLock", uniqueKey);
            service.acquireTransactionLock(uniqueKey);
          } else {
            service.acquireTransactionLock();
          }
        }
      } catch (NoLockNameException exc) {
        if (isLoggingError()) logError(exc);
      }
    }else{
      super.acquireTransactionLock(pRequest);
    }
  }
	
	private String getUniqueKey(){
	  
	  StringBuffer sb = new StringBuffer();
	  sb.append(System.currentTimeMillis());
	  sb.append("-");
	  sb.append(UUID.randomUUID().toString());
	  
	  return sb.toString();
	}
	
	public String getSuccessURL() {
		return successURL;
	}
	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}
	
	public String getErrorURL() {
		return errorURL;
	}
	public void setErrorURL(String errorURL) {
		this.errorURL = errorURL;
	}
	
	public PipelineManager getFulfillmentPipelineManager() {
		return fulfillmentPipelineManager;
	}
	public void setFulfillmentPipelineManager(
			PipelineManager fulfillmentPipelineManager) {
		this.fulfillmentPipelineManager = fulfillmentPipelineManager;
	}
	
	public StoreAllocationManager getStoreAllocationManager() {
		return storeAllocationManager;
	}
	public void setStoreAllocationManager(StoreAllocationManager pStoreAllocationManager) {
		this.storeAllocationManager = pStoreAllocationManager;
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String pOrderId) {
		this.orderId = pOrderId;
	}
	
	public String getSignature() {
    return signature;
  }
  public void setSignature(String signature) {
    this.signature = signature;
  }
  
  public String getSpecialInstructions() {
    return specialInstructions;
  }
  public void setSpecialInstructions(String pSpecialInstructions) {
    specialInstructions = pSpecialInstructions;
  }
  
  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String pDateOfBirth) {
    dateOfBirth = pDateOfBirth;
  }

  public StoreAllocationInput[] getStoreAllocationInput() {
		return storeAllocationInput;
	}

	public void setStoreAllocationInput(StoreAllocationInput[] pStoreAllocationInput) {
		this.storeAllocationInput = pStoreAllocationInput;
	}

	public int getCommerceItemCount() {
		return commerceItemCount;
	}

	public void setCommerceItemCount(int pCommerceItemCount) {
		if (pCommerceItemCount <= 0) {
			commerceItemCount = 0;
			storeAllocationInput = null;
		} else {
			commerceItemCount = pCommerceItemCount;
			storeAllocationInput = new StoreAllocationInput[commerceItemCount];
			Throwable caught = null;
			try {
				for (int index = 0; index < commerceItemCount; index++) {
					storeAllocationInput[index] = new StoreAllocationInput();
				}
			} catch (Throwable thrown) {
				caught = thrown;
			}
			if (caught != null) {
				if (isLoggingError()) {
					logError(caught);
				}

				// Throw away partially built array.
				storeAllocationInput = null;
			}
		}
		
	}
	
  public void setOmsOrderManager(OMSOrderManager pOrderManager)   {
  	mOrderManager = pOrderManager;
  }

  public OMSOrderManager getOmsOrderManager()   {
  	return mOrderManager;
  }

	public StoreAllocationResponse getAllocationResponse() {
		return allocationResponse;
	}

	public void setAllocationResponse(StoreAllocationResponse allocationResponse) {
		this.allocationResponse = allocationResponse;
	}

  public String getStoreId() {
    return storeId;
  }

  public void setStoreId(String pStoreId) {
    storeId = pStoreId;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int pOrderCount) {
    if (pOrderCount <= 0) {
      orderCount = 0;
      inPickingInput = null;
    } else {
      orderCount = pOrderCount;
      inPickingInput = new InPickingInput[orderCount];
      Throwable caught = null;
      try {
        for (int index = 0; index < orderCount; index++) {
          inPickingInput[index] = new InPickingInput();
        }
      } catch (Throwable thrown) {
        caught = thrown;
      }
      if (caught != null) {
        if (isLoggingError()) {
          logError(caught);
        }

        // Throw away partially built array.
        inPickingInput = null;
      }
    }
  }

  public InPickingInput[] getInPickingInput() {
    return inPickingInput;
  }

  public void setInPickingInput(InPickingInput[] pInPickingInput) {
    inPickingInput = pInPickingInput;
  }

  public String getOrderIdToCancel() {
    return mOrderIdToCancel;
  }

  public void setOrderIdToCancel(String pOrderIdToCancel) {
    mOrderIdToCancel = pOrderIdToCancel;
  }

  public CancelOrderResponse getCancelOrderResponse() {
    return cancelOrderResponse;
  }

  public void setCancelOrderResponse(CancelOrderResponse pCancelOrderResponse) {
    cancelOrderResponse = pCancelOrderResponse;
  }

  public OrderLockManager getOrderLockManager() {
    return mOrderLockManager;
  }

  public void setOrderLockManager(OrderLockManager pOrderLockManager) {
    mOrderLockManager = pOrderLockManager;
  }
  
  public MFFEmailManager getEmailManager() {
    return mEmailManager;
  }

  public void setEmailManager(MFFEmailManager pEmailManager) {
    mEmailManager = pEmailManager;
  }
  
  public String getComments() {
    return comments;
  }

  public void setComments(String pComments) {
    comments = pComments;
  }
  
  public String getCancelOrderReasonCode() {
    return mCancelOrderReasonCode;
  }

  public void setCancelOrderReasonCode(String pCancelOrderReasonCode) {
    this.mCancelOrderReasonCode = pCancelOrderReasonCode;
  }

  public boolean isUseTransactionLock() {
    return mUseTransactionLock;
  }

  public void setUseTransactionLock(boolean pUseTransactionLock) {
    mUseTransactionLock = pUseTransactionLock;
  }

  public CSRAgentTools getCsrAgentTools() {
    return csrAgentTools;
  }

  public void setCsrAgentTools(CSRAgentTools pCsrAgentTools) {
    csrAgentTools = pCsrAgentTools;
  }

}
