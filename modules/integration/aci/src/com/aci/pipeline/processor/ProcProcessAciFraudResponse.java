package com.aci.pipeline.processor;


import atg.core.util.StringUtils;
import atg.repository.RepositoryException;

import java.util.List;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcProcessAciFraudResponse extends AbstractAciProcessor {

	protected static final int EXIT_CHAIN = 0;
	private static final int[] RET_CODES = {SUCCESS, ERROR,EXIT_CHAIN};
	
	private List<String> mStatCdPositiveResponsesList;
	private String mSuspendCode;
	private List<String> mFraudStatCdPositiveResponsesList;
	
	public String getSuspendCode(){
		return mSuspendCode;
	}
	
	public void setSuspendCode(String pSuspendCode){
		mSuspendCode=pSuspendCode;
	}
	
	public List<String> getFraudStatCdPositiveResponsesList(){
		return this.mFraudStatCdPositiveResponsesList;
	}
	
	public void setFraudStatCdPositiveResponsesList(List<String> pFraudStatCdPositiveResponsesList){
		this.mFraudStatCdPositiveResponsesList=pFraudStatCdPositiveResponsesList;
	}
	
	public List<String> getStatCdPositiveResponsesList(){
		return this.mStatCdPositiveResponsesList;
	}
	
	public void setStatCdPositiveResponsesList(List<String> pStatCdPositiveResponsesList){
		this.mStatCdPositiveResponsesList=pStatCdPositiveResponsesList;
	}
	
	@Override
	public int[] getRetCodes() {
		return RET_CODES;
	}
	
	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcProcessAciFraudResponse:runAciProcess:Called");
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_PROCESS_ACI_RESPONSE_FAILED, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_PROCESS_ACI_RESPONSE_FAILED, true);
		}
		logBasicTransactionInfo(request);
		processAciRedResponse(request,pResult);
		vlogDebug("ProcProcessAciFraudResponse:runAciProcess:End");
		return SUCCESS;
	}
	
	private void processAciRedResponse(LPTransaction response,AciPipelineResult pResult){
		
		vlogDebug("ProcProcessAciFraudResponse:processAciRedResponse:Start");
		String pTransactionStatus = response.getField(FieldMappingConstants.STAT_CD);
		
		if(!StringUtils.isBlank(pTransactionStatus) && (getStatCdPositiveResponsesList().contains(pTransactionStatus.toUpperCase().trim()))){
			pResult.setTransactionSuccess(true);
			String pFraudStatus=response.getField(FieldMappingConstants.FRAUD_STAT_CD);
			if(!StringUtils.isBlank(pFraudStatus) && (getFraudStatCdPositiveResponsesList().contains(pFraudStatus.toUpperCase().trim()))){
				pResult.setFraudTransactionSuccess(true);
				pResult.setFraudResult(pFraudStatus.toUpperCase());
			}
			else {
				pResult.setFraudTransactionSuccess(false);
			}
		}
		else {
			pResult.setTransactionSuccess(false);
		}
		
		vlogDebug("ProcProcessAciFraudResponse:processAciRedResponse:End");
			 
	}
	
	private void logBasicTransactionInfo(LPTransaction transaction){
		logInfo("***Aci transaction info***");
		logInfo("ORD_ID: "+transaction.getField(FieldMappingConstants.ORD_ID));
		logInfo("ACT_CD: "+transaction.getField(FieldMappingConstants.ACT_CD));
		logInfo("Status Code: "+transaction.getField(FieldMappingConstants.STAT_CD));
		logInfo("Response Code: "+transaction.getField(FieldMappingConstants.RSP_CD));
		logInfo("Response Code Msg: "+transaction.getField(FieldMappingConstants.RSP_MSG));
		logInfo("Fraud Stat code: "+transaction.getField(FieldMappingConstants.FRAUD_STAT_CD));
		logInfo("Fraud resp code: "+transaction.getField(FieldMappingConstants.FRAUD_RSP_CD));
		logInfo("***Aci transaction end***");
	}
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException {

	}

}
