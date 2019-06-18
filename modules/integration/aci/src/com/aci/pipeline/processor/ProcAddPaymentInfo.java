package com.aci.pipeline.processor;

import java.util.ArrayList;
import java.util.List;

import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcAddPaymentInfo extends AbstractAciProcessor {
	

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddPaymentInfo.runProcess:Called");
		
		Order order = null;
		order=pParams.getOrder();
		
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_INFO, true);
		}
		
		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_INFO, true);
		}
		
		String ccClassType=aciConfiguration.getCreditCardClassType();
		if(ccClassType==null){
			vlogError("Credit Card Class Type is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_INFO, true);
		}
		else {
			ccClassType=ccClassType.trim();
		}
		
		String gcClassType=aciConfiguration.getGiftCardClassType();
		if(gcClassType==null){
			vlogError("Gift Card Class Type is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_PAYMENT_INFO, true);
		}
		else {
			gcClassType=gcClassType.trim();
		}
		
		List<PaymentGroup> paymentGroups=order.getPaymentGroups();
		List<PaymentGroup> creditCardPaymentGroup=new ArrayList<PaymentGroup>();
		List<PaymentGroup> giftCardPaymentGroup=new ArrayList<PaymentGroup>();
		if(paymentGroups!=null) {
			for(PaymentGroup payGroup:paymentGroups){
				vlogDebug("Payment Group being processed is "+payGroup.getPaymentGroupClassType());
				if(payGroup.getPaymentGroupClassType().equalsIgnoreCase(ccClassType)){
					creditCardPaymentGroup.add(payGroup);
					
					pParams.setHasCreditCards(true);
				}
				if(payGroup.getPaymentGroupClassType().equalsIgnoreCase(gcClassType)){
					giftCardPaymentGroup.add(payGroup);
					pParams.setHasGifftCards(true);
				}
			}
			if(creditCardPaymentGroup.size()!=0){
				pParams.setCreditCards(creditCardPaymentGroup);
			}
			if(giftCardPaymentGroup.size()!=0){
				pParams.setGiftCards(giftCardPaymentGroup);
			}
			
		}
		
		pParams.setRequest(request);
		vlogDebug("ProcAddBaseOrderInfo.runProcess:End");
		return SUCCESS;
		
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}
	

}
