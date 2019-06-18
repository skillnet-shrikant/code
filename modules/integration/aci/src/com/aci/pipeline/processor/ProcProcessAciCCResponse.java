package com.aci.pipeline.processor;


import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;

import java.util.List;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.constants.TransactionStatusConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcProcessAciCCResponse extends AbstractAciProcessor {

	protected static final int EXIT_CHAIN = 0;
	private static final int[] RET_CODES = {SUCCESS, ERROR,EXIT_CHAIN};
	
	private List<String> mStatCdPositiveResponsesList;
	private List<String> mFraudStatCdPositiveResponsesList;
	private List<String> mApproveResponseCodeList;
	private List<String> mDeclineResponseCodeList;
	private boolean mTestEIVCCN;

	public boolean isTestEIVCCN() {
    return mTestEIVCCN;
  }

  public void setTestEIVCCN(boolean pTestEIVCCN) {
    mTestEIVCCN = pTestEIVCCN;
  }

  public List<String> getApproveResponseCodeList() {
		return mApproveResponseCodeList;
	}

	public void setApproveResponseCodeList(List<String> pApproveResponseCodeList) {
		mApproveResponseCodeList = pApproveResponseCodeList;
	}

	public List<String> getDeclineResponseCodeList() {
		return mDeclineResponseCodeList;
	}

	public void setDeclineResponseCodeList(List<String> pDeclineResponseCodeList) {
		mDeclineResponseCodeList = pDeclineResponseCodeList;
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
		vlogDebug("ProcProcessAciCCResponse:runAciProcess:Called");
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
		Order pOrder=pParams.getOrder();
		logBasicTransactionInfo(request,pOrder);
		//boolean isEnableCVVVerification=pParams.isEnableCvvVerification();
		//boolean isAvsVerification=aciConfiguration.isEnableAvsCheck();
		captureResponse(request); //Capture the response in the database. If any exception occurs catch the exception and move forward
		String pActionCode=request.getField(FieldMappingConstants.ACT_CD);
		if(!StringUtils.isBlank(pActionCode)){
			if(aciConfiguration.getTokenizeTransactionAction().trim().equalsIgnoreCase(pActionCode.trim())){
				processAciRCSTokenizationResponse(request, pResult);
			}
			else if(aciConfiguration.getAuthorizeTransactionAction().trim().equalsIgnoreCase(pActionCode.trim())) {
				processAciRCSAuthResponse(request, pResult);
			}
			else if(aciConfiguration.getDebitTransactionAction().trim().equalsIgnoreCase(pActionCode.trim())) {
				processAciRCSDebitResponse(request, pResult);
			}
			else if(aciConfiguration.getCreditTransactionAction().trim().equalsIgnoreCase(pActionCode.trim())){
				processAciRCSCreditResponse(request, pResult);
			}
			else if(aciConfiguration.getAuthReversalTransactionAction().trim().equalsIgnoreCase(pActionCode.trim())){
				processAciRCSAuthReversalResponse(request,pResult);
			}
		}
		else {
			vlogDebug("ActionCode for request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_PROCESS_ACI_RESPONSE_FAILED, true);
		}
		
		
		vlogDebug("ProcProcessAciCCResponse:runAciProcess:End");
		return SUCCESS;
	}
	
	private void processAciRCSAuthReversalResponse(LPTransaction response,AciPipelineResult pResult) throws AciPipelineException{

		vlogDebug("ProcProcessAciCCResponse:processAciRCSAuthReversalResponse:Start");
		String pTransactionStatus = response.getField(FieldMappingConstants.STAT_CD);
		String originalAuthDate=response.getField(FieldMappingConstants.RSP_DT);
		String originalAuthTime=response.getField(FieldMappingConstants.RSP_TM);
		if(!StringUtils.isBlank(pTransactionStatus) && (getStatCdPositiveResponsesList().contains(pTransactionStatus.toUpperCase().trim()))){
			String responseCode=response.getField(FieldMappingConstants.RSP_CD);
			if(!StringUtils.isBlank(responseCode) && getApproveResponseCodeList().contains(responseCode.toUpperCase().trim())){
				pResult.setPaymentResponseCode(responseCode);
				pResult.setTransactionSuccess(true);
			}
			else {
				pResult.setTransactionSuccess(true);
			}
			
			if(!StringUtils.isEmpty(originalAuthDate)){
					pResult.setOriginalAciAuthDate(originalAuthDate);
			}
			
			if(!StringUtils.isEmpty(originalAuthTime)){
					pResult.setOriginalAciAuthTime(originalAuthTime);
			}
			
			String authCode=response.getField(FieldMappingConstants.RSP_AUTH_NUM);
			String authDate=response.getField(FieldMappingConstants.RSP_DT);
			
			vlogDebug("Auth date = {0}", authDate);
			String authTime=response.getField(FieldMappingConstants.RSP_TM);
			vlogDebug("Auth time = {0}", authTime);
			String requestId=response.getField(FieldMappingConstants.REQ_ID);
			pResult.setAciReqId(requestId);
			String actCode=response.getField(FieldMappingConstants.ACT_CD);
			pResult.setAuthorizationCode(authCode);
			pResult.setCallType(actCode);
			
		}
		else {
			pResult.setTransactionSuccess(false);
		}

		vlogDebug("ProcProcessAciCCResponse:processAciRCSAuthReversalResponse:End");
			 
	}
	
	private void processAciRCSCreditResponse(LPTransaction response,AciPipelineResult pResult) throws AciPipelineException{
		
		vlogDebug("ProcProcessAciCCResponse:processAciRCSCreditResponse:Start");
		
		
		vlogDebug("ProcProcessAciCCResponse:processAciRCSCreditResponse:End");
			 
	}
	
	private void processAciRCSDebitResponse(LPTransaction response,AciPipelineResult pResult) throws AciPipelineException{
		
		vlogDebug("ProcProcessAciCCResponse:processAciRCSDebitResponse:Start");
		
		
		vlogDebug("ProcProcessAciCCResponse:processAciRCSDebitResponse:End");
			 
	}
	
	private void processAciRCSTokenizationResponse(LPTransaction response,AciPipelineResult pResult) throws AciPipelineException{
		
		vlogDebug("ProcProcessAciCCResponse:processAciRCSTokenizeResponse:Start");
		String pTransactionStatus = response.getField(FieldMappingConstants.STAT_CD);
		if(isTestEIVCCN()) {
      pTransactionStatus="EIVCCN";
    }
		if(!StringUtils.isBlank(pTransactionStatus) && (getStatCdPositiveResponsesList().contains(pTransactionStatus.toUpperCase().trim()))){
			String pTokenNumber=response.getField(FieldMappingConstants.TOKEN_ID);
			if(!StringUtils.isBlank(pTokenNumber)){
				String pCodeType=response.getField(FieldMappingConstants.MOP_TYPE_CD);
				pResult.setTransactionSuccess(true);
				pResult.setTokenNumber(pTokenNumber.trim());
				if(!StringUtils.isBlank(pCodeType)){
					pResult.setMopTypeCd(pCodeType.toUpperCase().trim());
				}
			}
			else {
				vlogDebug("TokenNumber for response object is null.");
				pResult.setTransactionSuccess(false);
				throw new AciPipelineException(FieldMappingConstants.ACI_TOKENIZATION_FAILED, true);
			}
		}
		else {
			pResult.setTransactionSuccess(false);
			pResult.setStatusCode(pTransactionStatus.toUpperCase().trim());
		}
		
		vlogDebug("ProcProcessAciCCResponse:processAciRCSTokenizeResponse:End");
			 
	}
	
	private void parseAuthDataFromResponse(LPTransaction response, AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcProcessAciCCResponse:parseAuthDataFromResponse:Start");
			String authCode=response.getField(FieldMappingConstants.RSP_AUTH_NUM);
			String authDate=response.getField(FieldMappingConstants.RSP_DT);
			
			vlogDebug("Auth date = {0}", authDate);
			String authTime=response.getField(FieldMappingConstants.RSP_TM);
			vlogDebug("Auth time = {0}", authTime);
			String responseMessage=response.getField(FieldMappingConstants.RSP_MSG);
			String requestId=response.getField(FieldMappingConstants.REQ_ID);
			pResult.setAciReqId(requestId);
			//String orderId=response.getField(FieldMappingConstants.ORD_ID);
			String actCode=response.getField(FieldMappingConstants.ACT_CD);
			pResult.setAuthorizationCode(authCode);
			pResult.setCallType(actCode);
			pResult.setPaymentResponseMessage(responseMessage);
		
		vlogDebug("ProcProcessAciCCResponse:parseAuthDataFromResponse:End");
	}
	
	
	private void processAciRCSAuthResponse(LPTransaction response,AciPipelineResult pResult) throws AciPipelineException{
		
		vlogDebug("ProcProcessAciCCResponse:processAciRCSAuthResponse:Start");
		String pTransactionStatus = response.getField(FieldMappingConstants.STAT_CD);
		String pAvsValidationCode=response.getField(FieldMappingConstants.RSP_AVS_CD);		
		String pCvvValidationCode=response.getField(FieldMappingConstants.RSP_SEC_CD);
		String orgJKey=response.getField(FieldMappingConstants.PBG_SWITCH_KEY);
		String originalAuthDate=response.getField(FieldMappingConstants.RSP_DT);
		String originalAuthTime=response.getField(FieldMappingConstants.RSP_TM);
		if(!StringUtils.isBlank(pTransactionStatus) && (getStatCdPositiveResponsesList().contains(pTransactionStatus.toUpperCase().trim()))){
			String responseCode=response.getField(FieldMappingConstants.RSP_CD);
			if(!StringUtils.isBlank(responseCode) && getApproveResponseCodeList().contains(responseCode.toUpperCase().trim())){
				pResult.setPaymentResponseCode(responseCode);
				if(!StringUtils.isEmpty(pCvvValidationCode)){
					pResult.setCvvValidationCode(pCvvValidationCode);
				}
				if(!StringUtils.isEmpty(pAvsValidationCode)){
					pResult.setAddressValidationCode(pAvsValidationCode);
				}
				if(!StringUtils.isEmpty(originalAuthDate)){
					pResult.setOriginalAciAuthDate(originalAuthDate);
				}
				if(!StringUtils.isEmpty(originalAuthTime)){
					pResult.setOriginalAciAuthTime(originalAuthTime);
				}
				pResult.setOriginalJournalKey(orgJKey);
				
				pResult.setTransactionSuccess(true);
				parseAuthDataFromResponse(response,pResult);

			}
			else if(!StringUtils.isBlank(responseCode) && getDeclineResponseCodeList().contains(responseCode.toUpperCase().trim())){
				pResult.setTransactionSuccess(false);
			}
			else {
				pResult.setTransactionSuccess(false);
			}
		}
		else {
			pResult.setTransactionSuccess(false);
		}

		vlogDebug("ProcProcessAciCCResponse:processAciRCSAuthResponse:End");
			 
	}
	
  private void logBasicTransactionInfo(LPTransaction transaction,Order pOrder) {
    
    if (isLoggingInfo()) {
      logInfo("***Aci transaction info***");
      if(pOrder!=null){
			logInfo("ATG Order ID: "+pOrder.getId());
		}
      logInfo("ORD_ID: " + transaction.getField(FieldMappingConstants.ORD_ID));
      logInfo("ACT_CD: " + transaction.getField(FieldMappingConstants.ACT_CD));
      logInfo("Status Code: " + transaction.getField(FieldMappingConstants.STAT_CD));
      logInfo("Response Code: " + transaction.getField(FieldMappingConstants.RSP_CD));
      logInfo("Response Code Msg: " + transaction.getField(FieldMappingConstants.RSP_MSG));
      logInfo("***Aci transaction end***");
    }
  }

	
	private void captureResponse(LPTransaction response){
		
	}
	
	private void processAciRedResponse(LPTransaction response,AciPipelineResult pResult) throws AciPipelineException{
		
		vlogDebug("ProcProcessAciCCResponse:processAciRedResponse:Start");
		String pTransactionStatus = response.getField(FieldMappingConstants.STAT_CD);
		
		if(!StringUtils.isBlank(pTransactionStatus) && (getStatCdPositiveResponsesList().contains(pTransactionStatus.toUpperCase().trim()))){
			if(pTransactionStatus.equalsIgnoreCase(TransactionStatusConstants.TRANSACTION_APPROVE)||pTransactionStatus.equalsIgnoreCase(TransactionStatusConstants.TRANSACTION_SUCCESS)){
				pResult.setTransactionSuccess(true);
				
			}
			else {
				pResult.setTransactionSuccess(false);
			}
		}
		else {
			pResult.setTransactionSuccess(false);
		}
		
		vlogDebug("ProcProcessAciCCResponse:processAciRedResponse:End");
			 
	}
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException {

	}

}
