package com.aci.pipeline.processor.extended;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.core.util.ContactInfo;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.processor.ProcAddCommerceItemsInfo;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;



public class ProcAddExtendedCommerceItemsInfo extends ProcAddCommerceItemsInfo {
	
	
	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		vlogDebug("ProcAddExtendedCommerceItemsInfo:addExtendedProperties:Start");
		
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

		LPTransaction pRequest=null;
		pRequest=pParams.getRequest();
		if(pRequest==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_COMMERCEITEM_INFO, true);
		}
		
		String email=pParams.getEmailAddress();
		if(email==null){
			email="";
		}
		else{
			email=email.trim();
		}
		
		Repository productCatalog=getProductCatalog();
		long totalOrderItems=0;
		List<CommerceItem> commerceItems=pParams.getCommerceItems();
		int i=1;
		int itemCount = commerceItems.size();
		Map<String,String> commerceItemNumberCommerceItemMap=new HashMap<String,String>();
		for(CommerceItem commerceItem:commerceItems){
			// Recipient info to be passed to red. Fix for Issue#: 1818
			addRecipientInfo(commerceItem,pRequest,i,email);
			String skuNumber=commerceItem.getCatalogRefId();
			String productNumber=commerceItem.getAuxiliaryData().getProductId();
			RepositoryItem productItem=productCatalog.getItem(productNumber,"product");
			String title=(String)productItem.getPropertyValue("displayName");
			if(title!=null&&!title.trim().isEmpty()){
				pRequest.setField(FieldMappingConstants.ITEM_DESCN+Integer.toString(i),title.trim());
			}
			String category=getCategoryForProduct(productItem);
			pRequest.setField(FieldMappingConstants.ITEM_MAN_PART_NON+Integer.toString(i),category);
			String brandInfo=(String)productItem.getPropertyValue("brand");
			if(brandInfo!=null&&!brandInfo.trim().equalsIgnoreCase("")){
				pRequest.setField(FieldMappingConstants.ITEM_SHIP_NON+Integer.toString(i),brandInfo.trim());
			}
			Long itemQuantity=commerceItem.getQuantity();
			totalOrderItems=totalOrderItems+itemQuantity.longValue();
			Double amountInfo=commerceItem.getPriceInfo().getAmount();
			Double costPerLineItem=amountInfo.doubleValue()/itemQuantity;
			vlogDebug("Cost per line items: " + amountInfo.doubleValue());
			vlogDebug("Cost per each unit in line items: " + costPerLineItem);
			pRequest.setField(FieldMappingConstants.ITEM_PROD_CDN+Integer.toString(i),productNumber);
			pRequest.setField(FieldMappingConstants.ITEM_SKUN+Integer.toString(i),skuNumber);
			pRequest.setField(FieldMappingConstants.ITEM_QUANTITYN+Integer.toString(i), AciUtils.convertAmountToRedReadableFormat((Double)(itemQuantity.doubleValue()),4));
			pRequest.setField(FieldMappingConstants.ITEM_CST_AMTN+Integer.toString(i), 
					AciUtils.convertAmountToRedReadableFormat(costPerLineItem,4));
			pRequest.setField(FieldMappingConstants.ITEM_AMTN+Integer.toString(i), 
					AciUtils.convertAmountToRedReadableFormat(amountInfo,2));
			commerceItemNumberCommerceItemMap.put(commerceItem.getId(),Integer.toString(i));
			i++;
			
		}
		pRequest.setField(FieldMappingConstants.PROD_DEL_CD,"PHY");
		pParams.setCommerceItemNumberCommerceItemMap(commerceItemNumberCommerceItemMap);
		pRequest.setField(FieldMappingConstants.OI_REPEAT, Integer.toString(itemCount));
		pParams.setRequest(pRequest);
		vlogDebug("ProcAddExtendedCommerceItemsInfo:addExtendedProperties:Start");
	}
	
	private String getCategoryForProduct(RepositoryItem product){
		vlogDebug("ProcAddExtendedCommerceItemsInfo:getCategoryForProduct:Start");
		String productCategoryId="TestCategory";
 /**		Set<RepositoryItem> parentCategories=(Set<RepositoryItem>)(product.getPropertyValue("parentCategories"));
		if(parentCategories==null || parentCategories.size()==0){
			productCategoryId= "Web-Product-Root";
		}
		else {
			for(RepositoryItem category:parentCategories){
				String categoryId=(String)category.getPropertyValue("id");
				boolean isActive=(Boolean)category.getPropertyValue("isActive");
				boolean isAvailable=(Boolean)category.getPropertyValue("isAvailable");
				if((categoryId.toLowerCase().contains(("TCA".trim().toLowerCase())))&&isActive&&isAvailable){
					productCategoryId=categoryId;
					break;
				}
			}
		}
	**/
		vlogDebug("ProcAddExtendedCommerceItemsInfo:getCategoryForProduct:End");
		return productCategoryId;
	}
	
	private void addRecipientInfo(CommerceItem commerceItem, LPTransaction pRequest, int itemNumber,String emailAddress){
		// Recipient info to be passed to red. Fix for Issue#: 1818
		String recipFName="";
		String recipLName="";
		String recipMInitial="";
		String recipAddr1="";
		String recipAddr2="";
		String recipCity="";
		String recipState="";
		String recipPostalCode="";
		String recipCountry="";
		String recipPhone="";
		String recipEmail="";
		String recipShipComments="";
		if(emailAddress==null){
			emailAddress="";
		}
		else {
			emailAddress=emailAddress.trim();
		}
		List<ShippingGroupCommerceItemRelationship> commerceItemShippingGroupRelationships=commerceItem.getShippingGroupRelationships();
		for(ShippingGroupCommerceItemRelationship commShipRel:commerceItemShippingGroupRelationships){
			ShippingGroup shipGroup=commShipRel.getShippingGroup();
			if(shipGroup instanceof HardgoodShippingGroup){
				ContactInfo recipientContactInfo=(ContactInfo)((HardgoodShippingGroup)shipGroup).getShippingAddress();
				recipFName= AciUtils.checkForNullOrEmpty(recipientContactInfo.getFirstName());
				recipLName=AciUtils.checkForNullOrEmpty(recipientContactInfo.getLastName());
				recipMInitial=AciUtils.checkForNullOrEmpty(recipientContactInfo.getMiddleName());
				recipAddr1=AciUtils.checkForNullOrEmpty(recipientContactInfo.getAddress1());
				recipAddr2=AciUtils.checkForNullOrEmpty(recipientContactInfo.getAddress2());
				recipCity=AciUtils.checkForNullOrEmpty(recipientContactInfo.getCity());
				recipState=AciUtils.checkForNullOrEmpty(recipientContactInfo.getState());
				recipPostalCode=AciUtils.checkForNullOrEmpty(recipientContactInfo.getPostalCode());
				recipCountry=AciUtils.checkForNullOrEmpty(recipientContactInfo.getCountry());
				recipPhone=AciUtils.checkForNullOrEmpty(recipientContactInfo.getPhoneNumber());
				recipEmail=AciUtils.checkForNullOrEmpty(recipientContactInfo.getEmail());
				recipShipComments= recipAddr1+" "+recipAddr2+"|"+recipPostalCode;
				pRequest.setField(FieldMappingConstants.RECIP_FNAMEN+Integer.toString(itemNumber),recipFName);
				pRequest.setField(FieldMappingConstants.RECIP_LNAMEN+Integer.toString(itemNumber),recipLName);
				pRequest.setField(FieldMappingConstants.RECIP_MNAMEN+Integer.toString(itemNumber),recipMInitial);
				pRequest.setField(FieldMappingConstants.RECIP_ADDR1N+Integer.toString(itemNumber),recipAddr1);
				pRequest.setField(FieldMappingConstants.RECIP_ADDR2N+Integer.toString(itemNumber),recipAddr2);
				pRequest.setField(FieldMappingConstants.RECIP_CITYN+Integer.toString(itemNumber),recipCity);
				pRequest.setField(FieldMappingConstants.RECIP_STPR_CDN+Integer.toString(itemNumber),recipState);
				pRequest.setField(FieldMappingConstants.RECIP_CNTRY_CDN+Integer.toString(itemNumber),recipCountry);
				pRequest.setField(FieldMappingConstants.RECIP_POSTAL_CDN+Integer.toString(itemNumber),recipPostalCode);
				pRequest.setField(FieldMappingConstants.RECIP_PHONEN+Integer.toString(itemNumber),recipPhone);
				if(recipEmail!=null){
					recipEmail=recipEmail.trim();
					if(recipEmail.isEmpty()){
						recipEmail=emailAddress;
					}
				}
				else{
					recipEmail=emailAddress;
				}
				pRequest.setField(FieldMappingConstants.RECIP_EMAILN+Integer.toString(itemNumber),recipEmail);
				pRequest.setField(FieldMappingConstants.ITEM_SHIP_COMTS+Integer.toString(itemNumber),recipShipComments);
			}
			else if (shipGroup instanceof ElectronicShippingGroup){
				// Left to handle this for enhancement
			}
		}
		
		
		
	}
}
