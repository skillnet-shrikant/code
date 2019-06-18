package com.aci.pipeline.processor;

import java.util.ArrayList;
import java.util.List;

import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcAddShippingInfo extends AbstractAciProcessor {

	protected static final int SUCCESS_PROC_HARDGOOD_SHIPPING=3;
	protected static final int SUCCESS_PROC_ELECTRONIC_SHIPPING=4;
	protected static final int SUCCESS_PROC_MULTIPLE_SHIPPING=5;
	protected static final int SUCCESS_PROC_NO_HARDGOD_SHIPPING_AND_ELECTRONIC_SHIPPING=6;
	private static final int[] RET_CODES = {SUCCESS, ERROR,SUCCESS_PROC_HARDGOOD_SHIPPING,SUCCESS_PROC_ELECTRONIC_SHIPPING,SUCCESS_PROC_MULTIPLE_SHIPPING,SUCCESS_PROC_NO_HARDGOD_SHIPPING_AND_ELECTRONIC_SHIPPING};
	
	@Override
	public int[] getRetCodes() {
		return RET_CODES;
	}

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddShippingInfo.runAciProcess:Start");
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_SHIPPING_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_SHIPPING_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_SHIPPING_INFO, true);
		}
		
		
		
		List<ShippingGroup> shippingGroups=order.getShippingGroups();
		List<HardgoodShippingGroup> hgShippingGroups=new ArrayList<HardgoodShippingGroup>();
		List<ElectronicShippingGroup> egShippingGroups=new ArrayList<ElectronicShippingGroup>();
		if(shippingGroups!=null) {
			for(ShippingGroup shipGroup:shippingGroups){
				if(shipGroup instanceof HardgoodShippingGroup) {
					hgShippingGroups.add((HardgoodShippingGroup)shipGroup);
				}
				if(shipGroup instanceof ElectronicShippingGroup){
					egShippingGroups.add((ElectronicShippingGroup)shipGroup);
				}
			}
		}
		pParams.setRequest(request);		
		if(hgShippingGroups.size()>=1&&egShippingGroups.size()>=1) {
			pParams.setHardgoodShippingGroupInfos(hgShippingGroups);
			pParams.setElectronicShippingGroupInfos(egShippingGroups);
			pParams.setHasBothHardgoodAndElectronicgood(true);
			vlogDebug("ProcAddShippingInfo.runAciProcess:End");
			return SUCCESS_PROC_MULTIPLE_SHIPPING;
		}
		else if(hgShippingGroups.size()>=1&&egShippingGroups.size()<=0) {
			pParams.setHardgoodShippingGroupInfos(hgShippingGroups);
			vlogDebug("ProcAddShippingInfo.runAciProcess:End");
			return SUCCESS_PROC_HARDGOOD_SHIPPING;
		}
		else if(hgShippingGroups.size()<=0&&egShippingGroups.size()==1) {
			pParams.setElectronicShippingGroupInfos(egShippingGroups);
			vlogDebug("ProcAddShippingInfo.runAciProcess:End");
			return SUCCESS_PROC_ELECTRONIC_SHIPPING;
		}
		else if(hgShippingGroups.size()<=0&&egShippingGroups.size()>1){
			pParams.setElectronicShippingGroupInfos(egShippingGroups);
			pParams.setHasAllElectronicgoods(true);
			vlogDebug("ProcAddShippingInfo.runAciProcess:End");
			return SUCCESS_PROC_MULTIPLE_SHIPPING;
		}
		else
			vlogDebug("ProcAddShippingInfo.runAciProcess:End");
			return SUCCESS_PROC_NO_HARDGOD_SHIPPING_AND_ELECTRONIC_SHIPPING;
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}

}
