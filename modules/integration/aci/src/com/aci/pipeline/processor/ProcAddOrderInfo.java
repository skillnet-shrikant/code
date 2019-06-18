package com.aci.pipeline.processor;

import atg.commerce.order.Order;
import atg.repository.Repository;
import atg.repository.RepositoryException;


import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcAddOrderInfo extends AbstractAciProcessor {
	
	private Repository mProfileRepository;
	private Repository mOrderRepository;
	private String mEmailAddressPropertyName;
	
	
	public String getEmailAddressPropertyName(){
		return mEmailAddressPropertyName;
	}
	
	public void setEmailAddressPropertyName(String pEmailAddressPropertyName){
		mEmailAddressPropertyName=pEmailAddressPropertyName;
	}
	
	public Repository getProfileRepository(){
		return mProfileRepository;
	}
	
	public void setProfileRepository(Repository pProfileRepository){
		this.mProfileRepository=pProfileRepository;
	}
	
	public Repository getOrderRepository(){
		return mOrderRepository;
	}
	
	public void setOrderRepository(Repository pOrderRepository){
		this.mOrderRepository=pOrderRepository;
	}
	
	

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		
		vlogDebug("ProcAddOrderInfo:runProcess:Start");
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		request.setField(FieldMappingConstants.CUSTOM_ATG_ORDER_ID,order.getId());
		
		
		// Set Order Amount to Red request object
		if(order.getPriceInfo()==null){
			double orderTotal=0.0;
			pParams.setOrderSubTotal(orderTotal);
		}
		else {
			double orderTotal=order.getPriceInfo().getTotal();
			pParams.setOrderSubTotal(orderTotal);
		}
		if(order.getTaxPriceInfo()==null){
			double taxTotal=0.0;
			pParams.setTaxTotal(taxTotal);
		}
		else {
			double taxTotal=order.getTaxPriceInfo().getAmount();
			pParams.setTaxTotal(taxTotal);
		}
		pParams.setAdditionalShippingPrice(0.0);
		pParams.setTotalPaymentGroupPrice(0.0);
		pParams.setRequest(request);
		vlogDebug("ProcAddOrderInfo:runProcess:End");
		return SUCCESS;
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}
		
}
