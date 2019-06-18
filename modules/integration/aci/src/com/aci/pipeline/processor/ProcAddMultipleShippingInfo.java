package com.aci.pipeline.processor;


import java.util.List;


import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;

import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;


public class ProcAddMultipleShippingInfo extends AbstractAciProcessor {

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:Started");
		
		Order order = null;
		order=pParams.getOrder();
		
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_HARDGOOD_SHIPPING_INFO, true);
		}
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_HARDGOOD_SHIPPING_INFO, true);
		}
		
		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_HARDGOOD_SHIPPING_INFO, true);
		}
		
		boolean hasBothHardgoodAndElectronicShipping=pParams.getHasBothHardgoodAndElectronicgood();
		boolean hasAllElectronicShipping=pParams.getHasAllElectronicgoods();
		if(hasBothHardgoodAndElectronicShipping){
			vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:hasBothHardgoodAndElectronicShipping");
			List<HardgoodShippingGroup> hgShippingGroups=pParams.getHardgoodShippingGroupInfos();
			if(hgShippingGroups!=null&&hgShippingGroups.size()==1){
				HardgoodShippingGroup hgShippingGroup=hgShippingGroups.get(0);
				ShippingPriceInfo shippingPriceInfo=hgShippingGroup.getPriceInfo();
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
				request.setField(FieldMappingConstants.EBT_SHIPNO, hgShippingGroup.getId());
				ContactInfo address=(ContactInfo)hgShippingGroup.getShippingAddress();
				boolean hasBillingAddress=pParams.isBillingAddressAdded();
				if(!hasBillingAddress){
					addBillingAddressToRequest(address,request,aciConfiguration);
				}
				if(request.getField(FieldMappingConstants.CUST_TYPE_CD)!=null &&!(request.getField(FieldMappingConstants.CUST_TYPE_CD).trim().isEmpty())){
					request.setField(FieldMappingConstants.SHIP_TYPE_CD, "S");
					request.setField(FieldMappingConstants.SHIP_FNAME, address.getFirstName());
					request.setField(FieldMappingConstants.SHIP_LNAME, address.getLastName());
					StringBuffer ShipmentComment= new StringBuffer();
					String address1=address.getAddress1();
					String address2=address.getAddress2();
					request.setField(FieldMappingConstants.SHIP_ADDR1,address1 );
					ShipmentComment.append(address.getAddress1());
					if(address2!=null && !(address2.trim().isEmpty())){
						request.setField(FieldMappingConstants.SHIP_ADDR2, address2);
					}
					if(!StringUtils.isBlank(address2)){
						ShipmentComment.append(" ");
						ShipmentComment.append(address.getAddress2());
					}
					if(address.getPostalCode()!=null){
						ShipmentComment.append("|");
						ShipmentComment.append(address.getPostalCode());
					}if(ShipmentComment.toString().length()>0){
						request.setField(FieldMappingConstants.ebSHIPCOMMENTS,ShipmentComment.toString());
					}
					request.setField(FieldMappingConstants.SHIP_CITY,address.getCity());
					request.setField(FieldMappingConstants.SHIP_STPR_CD,address.getState());
					request.setField(FieldMappingConstants.SHIP_CNTRY_CD,aciConfiguration.getCountryValueMap().get(address.getCountry()));
					request.setField(FieldMappingConstants.SHIP_POSTAL_CD,address.getPostalCode());
					request.setField(FieldMappingConstants.SHIP_HOME_PHONE,AciUtils.convertPhoneNumberToRedReadableForm(address.getPhoneNumber()));
					request.setField(FieldMappingConstants.SHIP_EMAIL, (((request.getField(FieldMappingConstants.CUST_EMAIL)!=null)&&!(request.getField(FieldMappingConstants.CUST_EMAIL).trim().isEmpty()))?request.getField(FieldMappingConstants.CUST_EMAIL):""));
				}
				
				String shippingMethod = (String)hgShippingGroup.getShippingMethod();
				if("Standard".equalsIgnoreCase(shippingMethod)){
					request.setField(FieldMappingConstants.SHIP_MTHD_CD,"C");
				}else if("Express".equalsIgnoreCase(shippingMethod)){
					// T for Two business days
					request.setField(FieldMappingConstants.SHIP_MTHD_CD,"T");
				}else if("Overnight".equalsIgnoreCase(shippingMethod)){
					request.setField(FieldMappingConstants.SHIP_MTHD_CD,"N");
				}
				else {
					request.setField(FieldMappingConstants.SHIP_MTHD_CD,"O");
				}
				
			}
			
			List<ElectronicShippingGroup> egShippingGroups=pParams.getElectronicShippingGroupInfos();
			if(egShippingGroups!=null&&egShippingGroups.size()!=0){
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
			vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:hasBothHardgoodAndElectronicShipping End");
			vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:End");
			return SUCCESS;
		}
		if(hasAllElectronicShipping){
			vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:hasAllElectronicShipping");
			List<ElectronicShippingGroup> egShippingGroups=pParams.getElectronicShippingGroupInfos();
			if(egShippingGroups!=null&&egShippingGroups.size()!=0){
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
			vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:hasAllElectronicShipping");
			vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:End");
			return SUCCESS;
		}
		vlogDebug("ProcAddMultiplePaymentInfo:runAciProcess:End");
		return SUCCESS;
		
	}
	
	protected void addBillingAddressToRequest(ContactInfo billingAddress, LPTransaction pRequest,AciConfiguration pAciConfiguration){
		vlogDebug("ProcAddMultiplePaymentInfo:addBillingAddressToRequest:Start");	
		if(billingAddress!=null){
				pRequest.setField(FieldMappingConstants.CUST_TYPE_CD,"B");
				StringBuffer billingData = new StringBuffer();
				pRequest.setField(FieldMappingConstants.CUST_FNAME,billingAddress.getFirstName());
				pRequest.setField(FieldMappingConstants.CUST_LNAME,billingAddress.getLastName());
				
				String email=pRequest.getField(FieldMappingConstants.CUST_EMAIL);
				if(StringUtils.isBlank(email)){
					if(!StringUtils.isBlank(billingAddress.getEmail())){
						pRequest.setField(FieldMappingConstants.CUST_EMAIL,billingAddress.getEmail());
						pRequest.setField(FieldMappingConstants.CUST_ID,billingAddress.getEmail());
					}
				}
				pRequest.setField(FieldMappingConstants.CUST_ADDR1,billingAddress.getAddress1().trim());
				billingData.append(billingAddress.getAddress1());
				if(billingAddress.getAddress2()!=null && !(billingAddress.getAddress2().trim().isEmpty())){
					pRequest.setField(FieldMappingConstants.CUST_ADDR2,billingAddress.getAddress2().trim());
				}
				if(!StringUtils.isBlank(billingAddress.getAddress2())){
					billingData.append(" ");
					billingData.append(billingAddress.getAddress2());
				}
				if(!StringUtils.isBlank(billingAddress.getPostalCode())){
					billingData.append("|");
					String postalCodeWithOutHyphen =  AciUtils.convertPostalCodeToRedReadableForm(billingAddress.getPostalCode());
					billingData.append(postalCodeWithOutHyphen);
					pRequest.setField(FieldMappingConstants.CUST_POSTAL_CD,postalCodeWithOutHyphen);
				}
				if(billingData!=null && billingData.length()>0){
					pRequest.setField(FieldMappingConstants.CUSTOM_BILLING_INFO,billingData.toString());
				}
				pRequest.setField(FieldMappingConstants.CUST_HOME_PHONE,AciUtils.convertPhoneNumberToRedReadableForm(billingAddress.getPhoneNumber()));
				pRequest.setField(FieldMappingConstants.CUST_CITY,billingAddress.getCity());
				pRequest.setField(FieldMappingConstants.CUST_STPR_CD,billingAddress.getState());
				pRequest.setField(FieldMappingConstants.CUST_CNTRY_CD,pAciConfiguration.getCountryValueMap().get(billingAddress.getCountry()));    	
			}
		vlogDebug("ProcAddMultiplePaymentInfo:addBillingAddressToRequest:End");
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}

}
