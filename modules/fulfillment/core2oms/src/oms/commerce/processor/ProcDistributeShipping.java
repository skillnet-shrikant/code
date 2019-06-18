/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.processor;

import java.util.HashMap;

import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderImpl;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class ProcDistributeShipping extends ApplicationLoggingImpl implements PipelineProcessor {

	private final int SUCCESS = 1;

    /**
     * Default Constructor 
     */
    public ProcDistributeShipping() {
        super();
    }    

	public int[] getRetCodes() {
    	int[] ret = {SUCCESS};
        return ret;
	}

	/**
	 * Run the pipeline process 
	 */
	@SuppressWarnings("rawtypes")
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception	{
		
		vlogDebug ("Begin ProcDistributeShipping");

		/*OrderImpl lOMSOrder 	= (OrderImpl)map.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
		String lAllocationType 	= (String) map.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATION_TYPE);
		String lOrderNumber 	= lOrder.getOrderNumber();*/
		HashMap map 			= (HashMap) pParam;
		OrderImpl lOMSOrder 	= (OrderImpl) map.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
		
		if (lOMSOrder == null)
		    throw new InvalidParameterException("Ther OMS Order was not passed to ProcDistributeShipping"); 
		 
		OrderImpl lOrder 	= (OrderImpl) lOMSOrder;
		String lOrderNumber 	= lOrder.getId(); 
	    
   		vlogDebug("Initial order allocation for order {0} - Call OMS Order Manager to split shipping/tax for order", lOrderNumber);
    	getOmsOrderManager().splitShippingCharge(lOMSOrder);
		vlogDebug ("End ProcDistributeShipping");
	    return SUCCESS;
	}

	/** Order Manager **/
	private OMSOrderManager omsOrderManager;
	public OMSOrderManager getOmsOrderManager() {
		return omsOrderManager;
	}

	public void setOmsOrderManager(OMSOrderManager omsOrderManager) {
		this.omsOrderManager = omsOrderManager;
	}

	/** Logging Identifier **/
	String mLoggingIdentifier = "ProcDistributeShipping";
    public void setLoggingIdentifier(String pLoggingIdentifier) {
      mLoggingIdentifier = pLoggingIdentifier;
    }
    public String getLoggingIdentifier() {
      return mLoggingIdentifier;
    }
	
}