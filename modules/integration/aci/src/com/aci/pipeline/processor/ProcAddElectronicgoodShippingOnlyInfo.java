package com.aci.pipeline.processor;


import java.util.List;

import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.repository.RepositoryException;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcAddElectronicgoodShippingOnlyInfo extends AbstractAciProcessor {

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddElectronicgoodShippingOnlyInfo:runAciProcess:Start");
		
		Order order = null;
		order=pParams.getOrder();
		
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ELECTRONIC_SHIPPING_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ELECTRONIC_SHIPPING_INFO, true);
		}
		
		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ELECTRONIC_SHIPPING_INFO, true);
		}
		
		List<ElectronicShippingGroup> egShippingGroups=(List<ElectronicShippingGroup>) pParams.getElectronicShippingGroupInfos();
		if(egShippingGroups !=null && egShippingGroups.size()!=0){
			ElectronicShippingGroup eg=egShippingGroups.get(0);
			ShippingPriceInfo shippingPriceInfo=eg.getPriceInfo();
			double shippingPrice=shippingPriceInfo.getAmount();
			double paramsShippingPrice=pParams.getShippingPrice();
			double totalShippingPrice=shippingPrice+paramsShippingPrice;
			double paramsAdditionalShippingPrice=pParams.getAdditionalShippingPrice();
			double additionalShippingPrice=0.0;
			pParams.setShippingPrice(totalShippingPrice);
			List<PricingAdjustment> adjustments=shippingPriceInfo.getAdjustments();
			for(PricingAdjustment adjustment:adjustments){
				String adjustmentDescription=adjustment.getAdjustmentDescription();
				if(!StringUtils.isBlank(adjustmentDescription) && (adjustmentDescription.trim().startsWith(FieldMappingConstants.ADDITIONAL_SHIPPING_PRICE_DESCRIPTION))){
					additionalShippingPrice=adjustment.getAdjustment();
					break;
				}
			}
			double totalAdditionalShippingPrice=paramsAdditionalShippingPrice+additionalShippingPrice;
			pParams.setAdditionalShippingPrice(totalAdditionalShippingPrice);
			if(request.getField(FieldMappingConstants.CUST_TYPE_CD)!=null &&!(request.getField(FieldMappingConstants.CUST_TYPE_CD).trim().isEmpty())){
				request.setField(FieldMappingConstants.SHIP_EMAIL, (eg.getEmailAddress()));
			}
		}
		pParams.setRequest(request);
		
		vlogDebug("ProcAddElectronicgoodShippingOnlyInfo:runAciProcess:End");
		
		return SUCCESS;
	}
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}

}
