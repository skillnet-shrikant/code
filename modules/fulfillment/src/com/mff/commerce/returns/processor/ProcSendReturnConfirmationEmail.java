/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package com.mff.commerce.returns.processor;

import java.util.Map;

import com.mff.constants.MFFConstants;
import com.mff.email.MFFEmailManager;

import atg.commerce.csr.returns.ReturnRequest;
import atg.service.pipeline.PipelineResult;
import oms.commerce.processor.EXTNPipelineProcessor;

public class ProcSendReturnConfirmationEmail extends EXTNPipelineProcessor {
	private MFFEmailManager mEmailManager;
	
	@SuppressWarnings("rawtypes")
	@Override
	public int runProcess(Object pPipelineParams, PipelineResult pPipelineResult) throws Exception {
		
		Map lParams =(Map) pPipelineParams;
		//MFFReturnManager returnManager = (MFFReturnManager) lParams.get("ReturnManager");
		ReturnRequest returnRequest = (ReturnRequest) lParams.get("ReturnRequest");
	
		if(returnRequest != null && !returnRequest.getOriginOfReturn().equalsIgnoreCase(MFFConstants.POS_ORIGIN_OF_RETURN)){
		  getEmailManager().sendReturnConfirmationEmail(returnRequest);
		}
		return CONTINUE;
	}

	public MFFEmailManager getEmailManager() {
		return mEmailManager;
	}

	public void setEmailManager(MFFEmailManager pEmailManager) {
		mEmailManager = pEmailManager;
	}

}
