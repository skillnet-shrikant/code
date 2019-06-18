package com.aci.pipeline.processor;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.order.Order;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcValidateAddressFieldAciRequest extends AbstractAciProcessor {

	protected static final int EXIT_CHAIN = 0;
	private static final int[] RET_CODES = {SUCCESS, ERROR,EXIT_CHAIN};
	
	@Override
	public int[] getRetCodes() {
		return RET_CODES;
	}
	
	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcValidateAddressFieldAciRequest:runAciProcess:Started");
				
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ADDRESS_VALIDATEREQUEST_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ADDRESS_VALIDATEREQUEST_INFO, true);
		}
		
		validateAddressFields(request,aciConfiguration,FieldMappingConstants.CUST_ADDR1,FieldMappingConstants.CUST_ADDR2);
		validateAddressFields(request,aciConfiguration,FieldMappingConstants.SHIP_ADDR1,FieldMappingConstants.SHIP_ADDR2);
		pParams.setRequest(request);
		vlogDebug("ProcValidateAddressFieldAciRequest:runAciProcess:End");
		return SUCCESS;
	}
	
	private void validateAddressFields(LPTransaction pRequest,AciConfiguration aciConfiguration,String addressField1,String addressField2){
		vlogDebug("ProcValidateAddressFieldAciRequest:validateAddressFields:Started");
		String address1=pRequest.getField(addressField1);
		String address2=pRequest.getField(addressField2);
		Map<String,String> obtainedAddress=new HashMap<String,String>();
		if(address1!=null&&!address1.trim().isEmpty()){
			obtainedAddress.put("address1",address1);
			vlogDebug("ProcValidateAddressFieldAciRequest:Address1:"+address1);
		}
		if(address2!=null&&!address2.trim().isEmpty()){
			obtainedAddress.put("address2",address2);
			vlogDebug("ProcValidateAddressFieldAciRequest:Address2:"+address2);
		}
		
		Map<String,String> truncatedAddress=AciUtils.breakAddressInfo(obtainedAddress, aciConfiguration.getMaxFieldLengthsMap(),addressField1,addressField2);
		String truncatedAddress1=truncatedAddress.get("address1");
		pRequest.setField(addressField1,truncatedAddress1 );
		String truncatedAddress2=truncatedAddress.get("address2");
		if(truncatedAddress2!=null&&!truncatedAddress2.trim().isEmpty()){
			pRequest.setField(addressField2,truncatedAddress2 );
		}
		vlogDebug("ProcValidateAddressFieldAciRequest:validateAddressFields:End");
	}
	
	
		
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException {

	}

}
