package com.aci.pipeline.processor;



import atg.repository.RepositoryException;

import java.util.Date;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;



public class ProcAddCreditCardInfoForRCSAuthReversal extends AbstractAciProcessor {
	

	
	
	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddCreditCardInfoForRCSAuthReversal.runAciProcess:Called");
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO_FOR_RCS_AUTH_REVERSAL, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO_FOR_RCS_AUTH_REVERSAL, true);
		}
	
		Double amountToReverse=pParams.getAmountToReverse();
		if(amountToReverse==0){
			vlogError("Amount to reverse is 0");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO_FOR_RCS_AUTH_REVERSAL, true);
		}
		
		String authReversalTransactionAction=aciConfiguration.getAuthReversalTransactionAction();
		if(authReversalTransactionAction==null || authReversalTransactionAction.trim().isEmpty()){
			authReversalTransactionAction=FieldMappingConstants.AUTH_REVERSAL_TRANSACTION_ACTION_CODE;
		}
		
		if(aciConfiguration.isUseReqIdForAuthReversal()){
			
			String originalReqId= pParams.getOriginalReqId();
			if(originalReqId==null){
				vlogError("Original Request Id is null");
				throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_CREDITCARD_ONLY_INFO_FOR_RCS_AUTH_REVERSAL, true);
			}
			request.setField(FieldMappingConstants.ORIG_REQ_ID,originalReqId);
		}
		request.setField(FieldMappingConstants.ACT_CD,authReversalTransactionAction.trim());
		request.setField(FieldMappingConstants.AMT,AciUtils.convertAmountToRedReadableFormat((amountToReverse),2));
		pParams.setRequest(request);
		vlogDebug("ProcAddCreditCardInfoForRCSAuthReversal.runAciProcess:End");
		return SUCCESS;
	}
		
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException{
		
	}
	

		
}
