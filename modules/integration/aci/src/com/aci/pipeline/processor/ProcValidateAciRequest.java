package com.aci.pipeline.processor;




import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.Order;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcValidateAciRequest extends AbstractAciProcessor {

	protected static final int EXIT_CHAIN = 0;
	private static final int[] RET_CODES = {SUCCESS, ERROR,EXIT_CHAIN};
	
	@Override
	public int[] getRetCodes() {
		return RET_CODES;
	}
	
	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcValidateAciRequest:runAciProcess:Started");
				
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_VALIDATEREQUEST_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_VALIDATEREQUEST_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_VALIDATEREQUEST_INFO, true);
		}
		
		Map<String,String> maxLengthFields=aciConfiguration.getMaxFieldLengthsMap();
		Map<String,String> commerceItemsMap=pParams.getCommerceItemNumberCommerceItemMap();
		int sizeOfMap=commerceItemsMap.size();
		if(maxLengthFields!=null&&maxLengthFields.size()!=0){
			Set<String> keys=maxLengthFields.keySet();
			if(keys!=null&&keys.size()!=0){
				Iterator keyIterator=keys.iterator();
				while(keyIterator.hasNext()){
					String currentKey=(String)keyIterator.next();
					if(AciUtils.isRepeatableField(currentKey)){
						validateRepeatableField(request,currentKey,sizeOfMap,maxLengthFields);
					}
					else {
						if(!AciUtils.isAddressField(currentKey)){
							String fieldValue=request.getField(currentKey);
							String maxLength=maxLengthFields.get(currentKey);
							truncateFields(request,currentKey,fieldValue,maxLength);
						}
						
					}
				}
			}
		}
		pParams.setRequest(request);
		vlogDebug("ProcValidateAciRequest:runAciProcess:End");
		return SUCCESS;
	}
	
	private void truncateFields(LPTransaction pRequest,String key, String fieldValue, String maxLength){
		vlogDebug("ProcValidateAciRequest:truncateFields:Started");
		vlogDebug("ProcValidateAciRequest:key:"+key);
		vlogDebug("ProcValidateAciRequest:Value:"+fieldValue);
		vlogDebug("ProcValidateAciRequest:MaxLength:"+maxLength);
		String truncatedValue="";
		if(fieldValue!=null&&!fieldValue.trim().isEmpty()){
			if(maxLength!=null&&!maxLength.isEmpty()){
				truncatedValue=AciUtils.truncateToMaxFieldLength(fieldValue, Integer.parseInt(maxLength));
			}
			else {
				truncatedValue=fieldValue;
			}
			pRequest.setField(key,truncatedValue);
		}
		vlogDebug("ProcValidateAciRequest:truncateFields:End");
	}

	private void validateRepeatableField(LPTransaction pRequest,String key,int sizeOfMap,Map<String,String> maxFieldLengthMap){
		vlogDebug("ProcValidateAciRequest:validateRepeatableField:Started");
		
		if(sizeOfMap==0){
			String fieldValue=pRequest.getField((key+"1").trim());
			String maxLength=maxFieldLengthMap.get(key);
			vlogDebug("ProcValidateAciRequest:key:"+(key+"1"));
			vlogDebug("ProcValidateAciRequest:Value:"+fieldValue);
			vlogDebug("ProcValidateAciRequest:MaxLength:"+maxLength);
			truncateFields(pRequest,key+"1",fieldValue,maxLength);
		}
		else {
			for(int i=1;i<=sizeOfMap;i++){
				String fieldValue=pRequest.getField(key+i);
				String maxLength=maxFieldLengthMap.get(key);
				vlogDebug("ProcValidateAciRequest:key:"+(key+i));
				vlogDebug("ProcValidateAciRequest:Value:"+fieldValue);
				vlogDebug("ProcValidateAciRequest:MaxLength:"+maxLength);
				truncateFields(pRequest,key+i,fieldValue,maxLength);
			}
		}
		vlogDebug("ProcValidateAciRequest:validateRepeatableField:End");
	}
	
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException {

	}

}
