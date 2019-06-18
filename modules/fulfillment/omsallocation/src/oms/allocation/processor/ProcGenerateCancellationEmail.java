/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.HashMap;
import java.util.List;

import oms.allocation.item.AllocationConstants;
import oms.commerce.order.MFFOMSOrderManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.mff.commerce.order.MFFOrderImpl;

public class ProcGenerateCancellationEmail 
	extends ApplicationLoggingImpl 
	implements PipelineProcessor {

	private final int SUCCESS = 1;

    /**
     * Default Constructor 
     */
    public ProcGenerateCancellationEmail() {
        super();
    }    

	public int[] getRetCodes() {
    	int[] ret = {SUCCESS};
        return ret;
	}

	/**
	 * Run the pipeline process 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int runProcess(Object pParam, PipelineResult pResult) 
		throws Exception	{
		vlogDebug ("Begin ProcGenerateCancellationEmail");

		HashMap map 			= (HashMap) pParam;
		OrderImpl lOMSOrder 	= (OrderImpl) map.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
	    if (lOMSOrder == null)
		       throw new InvalidParameterException("The OMS Order was not passed to ProcGenerateCancellationEmail");	    
   		List <CommerceItem> lCommerceItems = (List<CommerceItem>)map.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL);
	    if (lCommerceItems == null)
		       throw new InvalidParameterException("The Items to cancel was not passed to ProcGenerateCancellationEmail");
		MFFOrderImpl lOrder 	= (MFFOrderImpl) lOMSOrder; 
		String lOrderNumber 	= lOrder.getId(); 		

   		vlogDebug("Generate cancellation email for order number: {0}", lOrderNumber);
   		generateCancellationEmail (lOrder, lCommerceItems);
		vlogDebug ("End ProcGenerateCancellationEmail");
	    return SUCCESS;
	}
	
	protected void generateCancellationEmail (Order pOrder, List<CommerceItem> pCommerceItems) {
   		vlogDebug("Begin Generate cancellation email for order number: {0}", ((MFFOrderImpl) pOrder).getId());
   		// Add details here
   		vlogDebug("End Generate cancellation email for order number: {0}", ((MFFOrderImpl) pOrder).getId());
	}
	
	MFFOMSOrderManager mOmsOrderManager;
	public MFFOMSOrderManager getOmsOrderManager() {
		return mOmsOrderManager;
	}
	public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
		this.mOmsOrderManager = pOmsOrderManager;
	}	
	
}
