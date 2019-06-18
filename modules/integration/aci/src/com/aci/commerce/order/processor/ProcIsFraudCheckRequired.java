package com.aci.commerce.order.processor;


import com.aci.configuration.AciConfiguration;

import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;


public class ProcIsFraudCheckRequired extends GenericService implements PipelineProcessor {

	public static int FRAUD_CHECK_REQUIRED = 1;
	public static int FRAUD_CHECK_NOT_REQUIRED = 2;
	
	private AciConfiguration mAciConfiguration;
	
	public AciConfiguration getAciConfiguration(){
		return mAciConfiguration;
		
	}
	
	public void setAciConfiguration(AciConfiguration pAciConfiguration){
		mAciConfiguration=pAciConfiguration;
	}
	
	
	@Override
	public int[] getRetCodes() {
		return new int[] { FRAUD_CHECK_REQUIRED , FRAUD_CHECK_NOT_REQUIRED};
	}

	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		
		vlogDebug("ProcIsFraudCheckRequired:runProcess:Start");
		boolean isCheckRequired=false;
		isCheckRequired=getAciConfiguration().isEnableFraud();
		vlogDebug("ProcIsFraudCheckRequired:runProcess:End");
		if(isCheckRequired){
			return FRAUD_CHECK_REQUIRED;
		}
		else {
			return FRAUD_CHECK_NOT_REQUIRED;
		}
	}

}
