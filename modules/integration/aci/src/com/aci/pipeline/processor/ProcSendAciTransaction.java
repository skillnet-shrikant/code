package com.aci.pipeline.processor;


import java.text.SimpleDateFormat;
import java.util.Date;

import atg.commerce.order.Order;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.liveprocessor.LPClient.LPClient;
import com.liveprocessor.LPClient.LPEncryptionException;
import com.liveprocessor.LPClient.LPIniFileException;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcSendAciTransaction extends AbstractAciProcessor {
	
	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcSendAciTransaction:runAciProcess:Started");
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_SEND_TO_ACI, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_SEND_TO_ACI, true);
		}
		
		Order pOrder=pParams.getOrder();
		
		//Initialize Client
		LPClient client=aciConfiguration.getClient();
		
		//Process client
		try {
		    request.setField(FieldMappingConstants.PBG_SWITCH_KEY,pParams.getOriginalJournalKey());
				printAciRequest(request,pOrder);
				request.process(client);
				pParams.setRequest(request);
				printAciResponse(request);
				vlogDebug("ProcSendAciTransaction:runAciProcess:End");
			return SUCCESS;
		}
		catch(LPEncryptionException ex){
			vlogError(ex,"Error occurred while processing the aci transaction");
			vlogDebug("ProcSendAciTransaction:runAciProcess:End");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_SEND_TO_ACI, true);
		}
		catch(LPIniFileException ex){
			vlogError(ex,"Error occurred while processing the aci transaction");
			vlogDebug("ProcSendAciTransaction:runAciProcess:End");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_SEND_TO_ACI, true);
		}
				
		
	}
	
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {

	}
	
	private void printAciResponse(LPTransaction response){

		
		SimpleDateFormat sfDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now=new Date();
		if(isLoggingDebug()){
			logDebug("**** Aci basic response params *****");
			logDebug("Status Code: "+response.getField(FieldMappingConstants.STAT_CD));
			logDebug("Response Code: "+response.getField(FieldMappingConstants.RSP_CD));
			logDebug("Transaction ID: "+response.getField(FieldMappingConstants.REQ_ID));
			logDebug("Fraud Stat code: "+response.getField(FieldMappingConstants.FRAUD_STAT_CD));
			logDebug("Fraud resp code: "+response.getField(FieldMappingConstants.FRAUD_RSP_CD));
			logDebug("**** Aci basic response params *****");
		}
		if(isLoggingDebug()){
			logDebug("ProcSendAciTransaction:printAciResponse:Started");
			logDebug("Received Response from Aci gateway:");
			logDebug("Transaction received time: "+sfDate.format(now));
			logDebug("Order ID: "+response.getField(FieldMappingConstants.ORD_ID));
			logDebug("Response Date: "+response.getField(FieldMappingConstants.RSP_DT));
			logDebug("Response Time: "+response.getField(FieldMappingConstants.RSP_TM));
			logDebug("Fraud Desc: "+response.getField(FieldMappingConstants.FRAUD_RSP_DESC));
			logDebug("Fraud use code: "+response.getField(FieldMappingConstants.FRAUD_USE_CD));
			logDebug("Action code: "+response.getField(FieldMappingConstants.ACT_CD));
			logDebug("ProcSendAciTransaction:printAciResponse:End");
		}		
		
	}
	
	private void printAciRequest(LPTransaction request,Order pOrder){

		SimpleDateFormat sfDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now=new Date();
		
		
		if(isLoggingInfo()){
			logInfo("**** Aci basic request params *****");
			if(pOrder!=null){
				logInfo("ATG Order ID: "+pOrder.getId());
			}
			logInfo("ORD_DTM: "+request.getField(FieldMappingConstants.ORD_DTM));
			logInfo("AMT: "+request.getField(FieldMappingConstants.AMT));
			logInfo("ACT_CD: "+request.getField(FieldMappingConstants.ACT_CD));
			logInfo("**** Aci basic request params *****");
		}
		if(isLoggingDebug()){
			logDebug("ProcSendAciTransaction:printAciRequest:Start");
			logDebug("Aci Request:");
			logDebug("Transactio sent time: "+sfDate.format(now));
			logDebug("ACT_CD: "+request.getField(FieldMappingConstants.ACT_CD));
			logDebug("REQ_TYPE_CD: "+request.getField(FieldMappingConstants.REQ_TYPE_CD));
			logDebug("DIV_NUMBER: "+request.getField(FieldMappingConstants.DIV_NUM));
			logDebug("S_KEY_ID: "+request.getField(FieldMappingConstants.S_KEY_ID));
			logDebug("EBT_NAME: "+request.getField(FieldMappingConstants.EBT_NAME));
			logDebug("EBT_SERVICE: "+request.getField(FieldMappingConstants.EBT_SERVICE));
			logDebug("CUST_TYPE_CD: "+request.getField(FieldMappingConstants.CUST_TYPE_CD));
			logDebug("SHIP_TYPE_CD: "+request.getField(FieldMappingConstants.SHIP_TYPE_CD));
			logDebug("ORD_ID: "+request.getField(FieldMappingConstants.ORD_ID));
			logDebug("ORD_DTM: "+request.getField(FieldMappingConstants.ORD_DTM));
			logDebug("AMT: "+request.getField(FieldMappingConstants.AMT));
			logDebug("FRT_AMT: "+request.getField(FieldMappingConstants.FRT_AMT));
			logDebug("SLS_TAX_AMT: "+request.getField(FieldMappingConstants.SLS_TAX_AMT));
			logDebug("EBT_DEVICEPRINT: "+request.getField(FieldMappingConstants.EBT_DEVICEPRINT));
			logDebug("CUST_FNAME: "+request.getField(FieldMappingConstants.CUST_FNAME));
			logDebug("CUST_LNAME: "+request.getField(FieldMappingConstants.CUST_LNAME));
			logDebug("SHIP_FNAME: "+request.getField(FieldMappingConstants.SHIP_FNAME));
			logDebug("SHIP_LNAME: "+request.getField(FieldMappingConstants.SHIP_LNAME));
			logDebug("PBG_SWITCH_KEY: "+request.getField(FieldMappingConstants.PBG_SWITCH_KEY));
			logDebug("ProcSendAciTransaction:printAciRequest:End");
			
		}
		
	}

}
