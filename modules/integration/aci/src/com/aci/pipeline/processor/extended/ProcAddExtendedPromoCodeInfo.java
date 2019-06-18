package com.aci.pipeline.processor.extended;





import atg.commerce.order.Order;
import atg.repository.Repository;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.processor.ProcAddPromoCodeInfo;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcAddExtendedPromoCodeInfo extends ProcAddPromoCodeInfo {
	

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		vlogDebug("ProcAddExtendedPromoCodeInfo:addExtendedProperties:Started");
		
		
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PROMOCODE_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PROMOCODE_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PROMOCODE_INFO, true);
		}
		
		Repository orderRepository=pParams.getOrderRepository();
		if(orderRepository==null){
			vlogError("Order Repository request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PROMOCODE_INFO, true);
		}
		
		
		pParams.setRequest(request);
		vlogDebug("ProcAddExtendedPromoCodeInfo:addExtendedProperties:End");
		
	}
	
}
