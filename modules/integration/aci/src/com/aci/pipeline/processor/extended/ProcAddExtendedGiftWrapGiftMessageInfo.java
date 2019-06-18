package com.aci.pipeline.processor.extended;

import java.util.List;
import java.util.Map;

import atg.commerce.order.Order;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.processor.ProcAddGiftWrapGiftMessageInfo;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcAddExtendedGiftWrapGiftMessageInfo extends ProcAddGiftWrapGiftMessageInfo {
	

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		vlogDebug("ProcAddExtendedGiftWrapGiftMessageInfo:addExtendedProperties:Start");
		
		Order order = null;
		order=pParams.getOrder();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_GIFTWRAP_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_GIFTWRAP_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_GIFTWRAP_INFO, true);
		}
		
		List<RepositoryItem> commerceItemShippingGroupRelationShips=pParams.getCommerceItemShippingGroupRelationShips();
		if(commerceItemShippingGroupRelationShips==null||commerceItemShippingGroupRelationShips.size()==0 ){
			if (isLoggingError()) {
				logError("No Shippinggroup commerceitem relationships found");
			}
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		
		Map<String,String> commerceItemNumberCommerceItemMap=pParams.getCommerceItemNumberCommerceItemMap();
		if(commerceItemShippingGroupRelationShips==null||commerceItemShippingGroupRelationShips.size()==0 ){
			if (isLoggingError()) {
				logError("No CommerceItems found");
			}
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		
		boolean hasGiftWrapping=false;
		String giftMessage="";
		for(RepositoryItem relShipItem:commerceItemShippingGroupRelationShips){
			RepositoryItem commerceItem=(RepositoryItem)relShipItem.getPropertyValue("commerceItem");
			if(commerceItem!=null){
				String commerceItemClassType=(String)commerceItem.getPropertyValue("commerceItemClassType");
				String commerceItemId=commerceItem.getRepositoryId();
				String count=commerceItemNumberCommerceItemMap.get(commerceItemId.trim());
				if(commerceItemClassType!=null&&!commerceItemClassType.trim().isEmpty()){
					if(commerceItemClassType.trim().equalsIgnoreCase("giftCardCommerceItem")){
						String giverMessage=(String)commerceItem.getPropertyValue("giverMessage");
						Double selectedGiftWrapPrice= (Double)relShipItem.getPropertyValue("selectedGiftWrapPrice");
						Double zeroValue=new Double(0.0);
						Boolean isElectronicType=(Boolean)commerceItem.getPropertyValue("isElectronicType");
						if((selectedGiftWrapPrice!=null)&&(selectedGiftWrapPrice.doubleValue()!=zeroValue.doubleValue())){
							hasGiftWrapping=true;
						}
						if(giverMessage!=null&&!giverMessage.trim().equalsIgnoreCase("")){
							request.setField(FieldMappingConstants.ITEM_GIFT_MSGN+count.trim(),giverMessage.trim());
							giftMessage=giverMessage.trim();
						}
						else {
							request.setField(FieldMappingConstants.ITEM_GIFT_MSGN+count.trim(),"NONE");
						}
					}
					if(commerceItemClassType.trim().equalsIgnoreCase("storeCommerceItem")){
						Double selectedGiftWrapPrice= (Double)relShipItem.getPropertyValue("selectedGiftWrapPrice");
						Double zeroValue=new Double(0.0);
						if((selectedGiftWrapPrice!=null)&&(selectedGiftWrapPrice.doubleValue()!=zeroValue.doubleValue())){
							hasGiftWrapping=true;
							RepositoryItem shippingGroup =(RepositoryItem)relShipItem.getPropertyValue("shippingGroup");
							if(shippingGroup!=null){
								String shipperMessageLine1=(String)shippingGroup.getPropertyValue("giftMessageLine1");
								String shipperMessageLine2=(String)shippingGroup.getPropertyValue("giftMessageLine2");
								String shipperMessageLine3=(String)shippingGroup.getPropertyValue("giftMessageLine3");
								String shipperMessageLine4=(String)shippingGroup.getPropertyValue("giftMessageLine4");
								String concatenatedShipMessage="";
								if(shipperMessageLine1!=null&&!shipperMessageLine1.trim().equalsIgnoreCase("")){
									shipperMessageLine1=shipperMessageLine1.trim();
									concatenatedShipMessage=concatenatedShipMessage+shipperMessageLine1+".";
								}
								if(shipperMessageLine2!=null&&!shipperMessageLine2.trim().equalsIgnoreCase("")){
									shipperMessageLine2=shipperMessageLine2.trim();
									concatenatedShipMessage=concatenatedShipMessage+shipperMessageLine2+".";
								}
								if(shipperMessageLine3!=null&&!shipperMessageLine3.trim().equalsIgnoreCase("")){
									shipperMessageLine3=shipperMessageLine3.trim();
									concatenatedShipMessage=concatenatedShipMessage+shipperMessageLine3+".";
								}
								if(shipperMessageLine4!=null&&!shipperMessageLine4.trim().equalsIgnoreCase("")){
									shipperMessageLine4=shipperMessageLine4.trim();
									concatenatedShipMessage=concatenatedShipMessage+shipperMessageLine4+".";
								}
								if(!concatenatedShipMessage.trim().equalsIgnoreCase("")){
									
									request.setField(FieldMappingConstants.ITEM_GIFT_MSGN+count.trim(),concatenatedShipMessage.trim());
									giftMessage=concatenatedShipMessage;
								}
								else {
									request.setField(FieldMappingConstants.ITEM_GIFT_MSGN+count.trim(),"NONE");
								}
							}
						}
						
					}
				}
			}
			
		}
		
		if(hasGiftWrapping){
			request.setField(FieldMappingConstants.GIFT_WRAP,"Y");
		}
		else {
			request.setField(FieldMappingConstants.GIFT_WRAP,"N");
		}
		
		if(!giftMessage.trim().equalsIgnoreCase("")){
			request.setField(FieldMappingConstants.GIFT_MESSAGE,giftMessage);
		}
		else{
			request.setField(FieldMappingConstants.GIFT_MESSAGE,"NONE");
		}
		
		pParams.setRequest(request);
		vlogDebug("ProcAddExtendedGiftWrapGiftMessageInfo:addExtendedProperties:End");
	}
	
}
