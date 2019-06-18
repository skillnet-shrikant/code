package com.aci.pipeline.processor.extended;


import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.processor.ProcAddSubClientIdInfo;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcAddExtendedSubClientIdInfo extends ProcAddSubClientIdInfo {


	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException, AciPipelineException {
		
		LPTransaction pRequest=pParams.getRequest();
		if(pRequest==null){
			vlogError("No Transaction request object found to process");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			if (isLoggingError()) {
				logError("No order found to process");
			}
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO, true);
		}
		String clientId="000460";
		
		pRequest.setField(FieldMappingConstants.EBT_NAME,clientId+"subclientId");
		pParams.setRequest(pRequest);
	}
	
}
