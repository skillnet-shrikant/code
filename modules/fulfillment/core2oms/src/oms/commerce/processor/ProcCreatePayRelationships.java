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

public class ProcCreatePayRelationships extends ApplicationLoggingImpl implements PipelineProcessor {

	private final int SUCCESS = 1;

    /**
     * Default Constructor 
     */
    public ProcCreatePayRelationships() {
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
	public int runProcess(Object pParam, PipelineResult pResult) 
		throws Exception	{
		vlogDebug ("Begin ProcCreatePayRelationships");

		HashMap map 			= (HashMap) pParam;
		OrderImpl lOMSOrder 	= (OrderImpl)map.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
		//String lAllocationType 	= (String) map.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATION_TYPE);	
		 if (lOMSOrder == null)
		       throw new InvalidParameterException("Ther OMS Order was not passed to ProcCreatePayRelationships");
		 
		OrderImpl lOrder 	= (OrderImpl) lOMSOrder; 
		String lOrderNumber 	= lOrder.getId(); 
	    
	    // Bypass Pay relationships if this is not an initial allocation
	    //if (!lAllocationType.equalsIgnoreCase(AllocationConstants.INITIAL_ALLOCATION_TYPE)) {
	    //	vlogDebug("Order number {0} has already been allocated and does not need to create pay relationships", lOrderNumber);
	    //	return SUCCESS;
	    //}

   		vlogDebug("Call OMS Order Manager to create pay relationships for order {0}", lOrderNumber);
    	getOmsOrderManager().addPayShipRelationships (lOMSOrder);
		vlogDebug ("End ProcCreatePayRelationships");
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
	String mLoggingIdentifier = "ProcCreatePayRelationships";
    public void setLoggingIdentifier(String pLoggingIdentifier) {
      mLoggingIdentifier = pLoggingIdentifier;
    }
    public String getLoggingIdentifier() {
      return mLoggingIdentifier;
    }
	
}
