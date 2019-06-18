/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.store.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.email.MFFEmailManager;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;

/**
 * This pipeline process will update the states on the shipping 
 * group and commerce item for items that have been shipped, cancelled
 * or declined.  The process will also update the inventory for each 
 * of the affect items in the order.
 * 
 * @author DMI
 *
 */
public class ProcSendOrderStatusEmail 
	extends GenericService implements PipelineProcessor {

	private final static int SUCCESS 			= 1;
	
	public int[] getRetCodes() {
		int[] ret = {SUCCESS};
		return ret;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int runProcess (Object pPipelineParams, PipelineResult pPipelineResults) 
		throws Exception {
		vlogDebug("Entering ProcSendOrderStatusEmail - runProcess");
	
		Map lParams 		  			= (Map) pPipelineParams;
		Order lOrder 	 	  			= (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		List <String> lItemsToShip 		= (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
		List <String> lItemsToCancel 	= (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL);
		List<String> lItemsToPickup   = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_PICKUP);
		
		HashMap<String,String> trackingNumberMap = (HashMap<String,String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_TRACKING_NO);
		HashMap<String,String> reasonCodeMap = (HashMap<String,String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_REASON_CODE_MAP);
		
		boolean suppressShippedEmail         = false;
		if(lParams.get(AllocationConstants.PIPELINE_PARAMETER_SUPPRESS_SHIPPED_EMAIL) != null){
		  suppressShippedEmail = (Boolean)lParams.get(AllocationConstants.PIPELINE_PARAMETER_SUPPRESS_SHIPPED_EMAIL);
		}
		if (isSuppressEmail() ) {
			vlogDebug ("Email send has been suppressed");
		    return SUCCESS;
		}
		    
		// Send Shipment Email
		if(getProcessName().equalsIgnoreCase("SHIPPED")){
		  if(suppressShippedEmail){
		    vlogDebug ("Shipped Email send has been suppressed");
		  }else{
		    processShipments (lOrder, lItemsToShip, trackingNumberMap);
		  }
		}
		
		// Send Cancel Email
		if(getProcessName().equalsIgnoreCase("CANCELLED"))
			processCancels (lOrder, lItemsToCancel,reasonCodeMap);
		
		// Send ReadyForPickUp Email
    if (getProcessName().equalsIgnoreCase("PICKUP"))
      processReadyForPickup(lOrder, lItemsToPickup);
		
		vlogDebug("Exiting ProcSendOrderStatusEmail - runProcess");
		return SUCCESS;
	}
	
	/**
	 * Send the ship confirmation message.
	 *
	 * @param pOrder			ATG Order
	 * @param pItemsToShip		List of commerce item IDs to ship
	 * @throws Exception		
	 */
	protected void processShipments (Order pOrder, List<String> pItemsToShip, HashMap<String,String> pTrackingNumberMap) 
		throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;	
		vlogDebug ("Begin process the ship confirmation for order number {0}",  lOrder.getId());

		// Bail if there is nothing to process
		if (pItemsToShip == null || pItemsToShip.size() < 1) {
			vlogDebug ("No items to ship were found for order number {0}",  lOrder.getId());
			return;
		}
		
		//Call to EmailManager to send shipment Email
		if(!isSuppressEmail()){
		  if(lOrder.isBopisOrder()){
		    // send bopisOrderPickUpConfirmation Email
			  getEmailManager().sendOrderPickUpConfirmationEmail(lOrder);
		  }else{
		    List<CommerceItem> ciItems = new ArrayList<CommerceItem>();
		    for (String lCommerceItemId : pItemsToShip) {
		      MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, lCommerceItemId);
		      ciItems.add(lCommerceItem);
		    }
		    getEmailManager().sendOrderShippedEmail(lOrder, ciItems, pTrackingNumberMap);
		  }
		}

		vlogDebug ("End processing the ship confirmation for order number {0}",  lOrder.getId());
	}

	/**
	 * Send the cancellation email
	 * 
	 * @param pOrder			ATG Order
	 * @param pItemsToCancel	List of commerce item IDs to cancel
	 * @throws Exception		
	 */
	protected void processCancels (Order pOrder, List<String> pItemsToCancel,HashMap<String,String> pReasonCodeMap) 
		throws Exception {
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;	
		vlogDebug ("Begin process the cancellation email for order number {0}",  lOrder.getId());
		
		// Bail if there is nothing to process
		if (pItemsToCancel == null || pItemsToCancel.size() < 1) {
			vlogDebug ("No items to cancel were found for order number {0}",  lOrder.getId());
			return;
		}
		
		//Call to EmailManager to send cancellation Email
		if(!isSuppressEmail()){
			getEmailManager().sendCancelItemsEmail(lOrder, pItemsToCancel,pReasonCodeMap);		
		}
		vlogDebug ("End processing the cancellation email for order number {0}",  lOrder.getId());
	}
	
	/**
	 * Send the readyForPickUp Email
	 * 
	 * @param pOrder
	 * @param pItemsToPickup
	 * @throws Exception
	 */
	protected void processReadyForPickup(Order pOrder, List<String> pItemsToPickup) 
	     throws Exception {
	   
		  MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;  
		  vlogDebug ("Begin process the readyForPickup email for order number {0}",  lOrder.getId());
		   
		   // Bail if there is nothing to process
	    if (pItemsToPickup == null || pItemsToPickup.size() < 1) {
	      vlogDebug ("No items to pickup were found for order number {0}",  lOrder.getId());
	      return;
	    }
	    
	    //Call to EmailManager to send readyForPickup Email
	    if(!isSuppressEmail()){
			getEmailManager().sendReadyForPickUpEmail(lOrder);
			getEmailManager().sendReadyForPickUpAlternateEmail(lOrder);
			
		}
	    vlogDebug ("End processing the readyForPickup email for order number {0}",  lOrder.getId());
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
  protected MFFCommerceItemImpl getCommerceItem(Order pOrder, String lCommerceItemId) throws Exception {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    MFFCommerceItemImpl lCommerceItem;
    try {
      lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lCommerceItemId);
    } catch (CommerceItemNotFoundException ex) {
      String lErrorMessage = String.format("Commerce item not found for order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Commerce item not found for order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    }
    return lCommerceItem;
  }

	private MFFEmailManager mEmailManager;
	
	public MFFEmailManager getEmailManager() {
    return mEmailManager;
  }

  public void setEmailManager(MFFEmailManager pEmailManager) {
    mEmailManager = pEmailManager;
  }
	
	boolean mSuppressEmail;

	public boolean isSuppressEmail() {
		return mSuppressEmail;
	}

	public void setSuppressEmail(boolean pSuppressEmail) {
		this.mSuppressEmail = pSuppressEmail;
	}
	
	String mProcessName;

	public String getProcessName() {
		return mProcessName;
	}

	public void setProcessName(String mProcessName) {
		this.mProcessName = mProcessName;
	}
	
}
