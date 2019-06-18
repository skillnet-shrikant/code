package com.aci.pipeline.processor;

import atg.commerce.order.Order;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcAddPricingInfo extends AbstractAciProcessor {
	


	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddPricingInfo:runAciProcess:Started");
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PRICE_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PRICE_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PRICE_INFO, true);
		}
	
		
		
		double orderTotal=pParams.getOrderSubTotal()+pParams.getAdditionalShippingPrice();
		double taxTotal=pParams.getTaxTotal();
		double totalPaymentGroupPrice=pParams.getTotalPaymentGroupPrice();
		double shippingPrice=pParams.getShippingPrice()+pParams.getAdditionalShippingPrice();
		
		vlogDebug("Order Total is : "+orderTotal);
		vlogDebug("Tax Total is : "+taxTotal);
		vlogDebug("Paymentgroup total is: "+totalPaymentGroupPrice);
		vlogDebug("Shipping Total is: " + shippingPrice);
		
		request.setField(FieldMappingConstants.AMT,AciUtils.convertAmountToRedReadableFormat((orderTotal),2));
		request.setField(FieldMappingConstants.SLS_TAX_AMT,AciUtils.convertAmountToRedReadableFormat((taxTotal),2));
		request.setField(FieldMappingConstants.FRT_AMT,AciUtils.convertAmountToRedReadableFormat((shippingPrice),2));
		
		vlogDebug("ProcAddPricingInfo.runProcess:End");
		return SUCCESS;
		
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}
		
}
