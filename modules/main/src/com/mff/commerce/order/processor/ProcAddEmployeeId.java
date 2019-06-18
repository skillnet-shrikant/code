package com.mff.commerce.order.processor;

import java.util.HashMap;

import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.order.OrderManager;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderTools;

/**
 * The following class copies the employee id to the order.
 * 
 * @author DMI
 * 
 */
public class ProcAddEmployeeId extends GenericService implements PipelineProcessor {

	private final int SUCCESS = 1;
	
	
	@Override
	public int[] getRetCodes() {
		return new int [] {SUCCESS};
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		
		vlogDebug("Called runProcess().");

		HashMap map = (HashMap) pParam;
		MFFOrderImpl order = (MFFOrderImpl) map.get(PipelineConstants.ORDER);
		MutableRepositoryItem lProfile = (MutableRepositoryItem) map.get("Profile");
		String employeeId = (String)lProfile.getPropertyValue("employeeId");
		boolean validated = (boolean)lProfile.getPropertyValue("validated");
		
		if(validated) {
			order.setEmployeeId(employeeId);
			return SUCCESS;
		} 
		vlogDebug("Exiting runProcess");
		return SUCCESS;
	}

}