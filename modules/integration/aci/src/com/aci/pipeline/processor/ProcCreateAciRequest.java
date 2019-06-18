package com.aci.pipeline.processor;

import atg.repository.RepositoryException;
import atg.service.idgen.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcCreateAciRequest extends AbstractAciProcessor {
	
	private IdGenerator mIdGenerator;
	private String mIdGeneratorSpaceName;
	
	public String getIdGeneratorSpaceName(){
		return mIdGeneratorSpaceName;
	}
	
	public void setIdGeneratorSpaceName(String pIdGeneratorSpaceName){
		mIdGeneratorSpaceName=pIdGeneratorSpaceName;
	}
	
	public IdGenerator getIdGenerator(){
		return mIdGenerator;
	}
	
	public void setIdGenerator(IdGenerator pIdGenerator){
		mIdGenerator=pIdGenerator;
	}
	
	private String getAciOrderId() throws AciPipelineException {
		try {
			vlogDebug("ProcCreateAciRequest:getAciOrderId:Start");
			String nextOrderNumber = getIdGenerator().generateStringId(getIdGeneratorSpaceName());
			String numericValue=nextOrderNumber.substring(3);
			String changeValue=numericValue;
			if(numericValue.length()<6){
				for(int i=0;i<6-numericValue.length();i++){
					changeValue=0+changeValue;
				}
			}
			if(numericValue.length()>6){
				changeValue=numericValue.substring(numericValue.length()-6);
			}
			SimpleDateFormat sdfDate = new SimpleDateFormat("ddMMyy");//dd/MM/yyyy
	    	Date now = new Date();
	    	String strDate = sdfDate.format(now);
	    	changeValue=strDate+changeValue;
	    	vlogDebug("ProcCreateAciRequest:getAciOrderId:End");
	    	return changeValue;

		}
		catch(Exception ex){
			vlogError("ProcCreateAciRequest:getAciOrderId:Error");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
	}
	
	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcCreateAciRequest:runAciProcess:Start");
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
		
		String transactionAction =pParams.getTransactionAction();
		if(transactionAction==null){
			vlogError("ACI Transaction action is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
		
		String merchantId =pParams.getDivNum();
		if(merchantId==null){
			vlogError("ACI Merchant ID is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
		
		String currencyCode =pParams.getCurrencyCode();
		if(currencyCode==null){
			vlogError("ACI Currency Code is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
		
		String subClientId =pParams.getSubclientId();
		if(subClientId==null){
			vlogError("ACI Subclient Id is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
		
		String reqType =pParams.getReqType();
		if(reqType==null){
			vlogError("Req Type is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
		
		String mopType=pParams.getMopType();
		
		
		if(mopType==null){
			vlogError("Mop Type is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED, true);
		}
		
		LPTransaction request=new LPTransaction();
		// GENERAL INFORMATION. These values remain same for all transaction unless update by RED
		request.setField(FieldMappingConstants.ACT_CD,transactionAction);
		request.setField(FieldMappingConstants.DIV_NUM,merchantId);
		request.setField(FieldMappingConstants.S_KEY_ID,aciConfiguration.getS_Key_Id());
		request.setField(FieldMappingConstants.CURR_CD,currencyCode);
		request.setField(FieldMappingConstants.ebWEBSITE,aciConfiguration.getEbWebsite());
		String clientId=aciConfiguration.getClientId();
		request.setField(FieldMappingConstants.EBT_NAME,clientId+subClientId);
		request.setField(FieldMappingConstants.REQ_TYPE_CD, reqType);
		request.setField(FieldMappingConstants.MOP_TYPE_CD, mopType);
		if(!pParams.isAuthReversalRequest()){
			request.setField(FieldMappingConstants.ORD_DTM,AciUtils.convertOrderSubmittedTimeToReDReadableForm(new Date()));
		}
		request.setField(FieldMappingConstants.ORD_TZ, aciConfiguration.getDefaultTimeZone());
		request.setField(FieldMappingConstants.ORD_ID,getAciOrderId());
		pParams.setRequest(request);		
		vlogDebug("ProcCreateACIRequest:runAciProcess:End");
		return SUCCESS;
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}

}
