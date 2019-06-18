/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderImpl;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;

public class ProcSplitCommerceItems extends ApplicationLoggingImpl implements
		PipelineProcessor 
{
	
	/**
     * Default Constructor
     */
    public ProcSplitCommerceItems() {
        super();
    }
    private final int SUCCESS = 1;
    /*
     * ATG OOTB fulfillment ResourceBundle
     */
    static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";
    private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
    
    // property: LoggingIdentifier
    String mLoggingIdentifier = "ProcSplitCommerceItems";
    private OMSOrderManager omsOrderManager;
    private boolean enabled = false;
    
    /**
     * 
     * @return enabled
     */
    public boolean isEnabled() {
		return enabled;
	}
    
    /**
     * 
     * @param enabled
     */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
     * Sets property LoggingIdentifier
     **/
    public void setLoggingIdentifier(String pLoggingIdentifier) {
      mLoggingIdentifier = pLoggingIdentifier;
    }

    /**
     * Returns property LoggingIdentifier
     **/
    public String getLoggingIdentifier() {
      return mLoggingIdentifier;
    }
    
	/**
	 * @return the omsOrderManager
	 */
	public OMSOrderManager getOmsOrderManager() {
		return omsOrderManager;
	}

	/**
	 * @param omsOrderManager the omsOrderManager to set
	 */
	public void setOmsOrderManager(OMSOrderManager omsOrderManager) {
		this.omsOrderManager = omsOrderManager;
	}

	/* (non-Javadoc)
	 * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
	 */
	public int[] getRetCodes() {
    	int[] ret = {SUCCESS};
        return ret;
	}
	
	/* (non-Javadoc)
	 * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object, atg.service.pipeline.PipelineResult)
	 */
	@SuppressWarnings("rawtypes")
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception 
	{
		if(isEnabled()){
			if(isLoggingDebug()){
				logDebug("inside ProcSplitCommerceItems");
			}
			HashMap map = (HashMap) pParam;
			OrderImpl pOrder = (OrderImpl) map.get(PipelineConstants.ORDER);
			OrderImpl pOMSOrder = (OrderImpl)map.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
		    if (pOrder == null)
		    {
		       throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
		    }
		    if (pOMSOrder == null)
		    {
		       throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
		    }
		    
		    if(pOMSOrder.getTotalCommerceItemCount() == pOMSOrder.getCommerceItemCount())
		    {
		    	if(isLoggingDebug()){
		    		logDebug("TotalCommerceItemCount is::::"+pOMSOrder.getTotalCommerceItemCount());
		    		logDebug("Thre is no need to split commerceItems");
		    	}
		    }
		    else if(isGiftCardInOrder(pOMSOrder))
		    {
		    	if(isLoggingDebug()){
		    		logDebug("There are gift card items with multiple quantity so split them");
		    		logDebug("OMSOrder with Id:"+pOMSOrder.getId()+" is about to split commerceItems");
		    	}
		    	getOmsOrderManager().splitCommerceItems(pOMSOrder);
		    }
		}else{
			vlogInfo("ProcSplitCommerceItems : Skipped processing as enabled set to false");
		}
	    return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
  private boolean isGiftCardInOrder(OrderImpl pOrder) {
	  
	  List<CommerceItem> items = pOrder.getCommerceItems();

    Iterator<CommerceItem> itemsIter = items.iterator();

    while (itemsIter.hasNext()) {
      CommerceItemImpl lCommerceItem = (CommerceItemImpl) itemsIter.next();
      if(getOmsOrderManager().isGiftCardItem(lCommerceItem) && 
          lCommerceItem.getQuantity() > 1){
        return true;
      }
    }
    return false;
   }
	
}