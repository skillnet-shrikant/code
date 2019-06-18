package com.aci.pipeline.processor;

import java.util.List;

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



public class ProcAddBillingAddressInfo extends AbstractAciProcessor {

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddBillingAddressInfo:runAciProcess:Called");

		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_BILLING_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_BILLING_INFO, true);
		}
		
		String emailAddress=pParams.getEmailAddress();
		if(!StringUtils.isEmpty(emailAddress)){
			request.setField(FieldMappingConstants.CUST_EMAIL,emailAddress);
			request.setField(FieldMappingConstants.CUST_ID,emailAddress);
		}
		
		List<ContactInfo> creditCardBillingContactInfos= (List<ContactInfo>)pParams.getCreditCardBillingInfos();
		List<ContactInfo> giftCardBillingContactInfos= (List<ContactInfo>)pParams.getGiftCardBillingInfos();
		pParams.setBillingAddressAdded(false);
		
		if(creditCardBillingContactInfos!=null&&creditCardBillingContactInfos.size()!=0&&pParams.getCreditCardBillingAddressPresent()){
			ContactInfo contactInfo=creditCardBillingContactInfos.get(0);
			addBillingAddressToRequest(contactInfo,request,aciConfiguration);
			pParams.setBillingAddressAdded(true);
		}
		else if(giftCardBillingContactInfos!=null&&giftCardBillingContactInfos.size()!=0&&pParams.getGiftCardBillingAddressPresent()){
			ContactInfo contactInfo=giftCardBillingContactInfos.get(0);
			addBillingAddressToRequest(contactInfo,request,aciConfiguration);
			pParams.setBillingAddressAdded(true);
		}
		pParams.setRequest(request);
		vlogDebug("ProcAddBillingAddressInfo:runAciProcess:End");
		return SUCCESS;
	}
	
	/**
	 * This will add billing address information from ATG Order to the RED Order
	 * @param billingAddress
	 * @param pRedOrder
	 */
	protected void addBillingAddressToRequest(ContactInfo billingAddress, LPTransaction pRequest,AciConfiguration pAciConfiguration){
		vlogDebug("ProcAddBillingAddressInfo:addBillingAddressToRequest:Start");	
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
		vlogDebug("ProcAddBillingAddressInfo:addBillingAddressToRequest:End");
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {

	}

}
