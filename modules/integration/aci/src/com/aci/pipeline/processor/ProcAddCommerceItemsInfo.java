package com.aci.pipeline.processor;


import java.util.List;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.repository.Repository;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcAddCommerceItemsInfo extends AbstractAciProcessor {

	private Repository mProductCatalog;
	
	public Repository getProductCatalog(){
		return this.mProductCatalog;
	}
	
	public void setProductCatalog(Repository pProductCatalog){
		this.mProductCatalog=pProductCatalog;
	}
	
	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddCommerceItemsInfo:runAciProcess:Start");
		
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_COMMERCEITEM_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_COMMERCEITEM_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_COMMERCEITEM_INFO, true);
		}
		
		List<CommerceItem> commerceItems=order.getCommerceItems();
		pParams.setCommerceItems(commerceItems);
		if (isLoggingDebug()) {
			logDebug("ProcAddBaseOrderInfo.runProcess:End");
		}
		return SUCCESS;
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}

}
