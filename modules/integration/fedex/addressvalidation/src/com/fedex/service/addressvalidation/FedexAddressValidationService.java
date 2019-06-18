package com.fedex.service.addressvalidation;

import java.rmi.RemoteException;
import java.util.Calendar;


import com.fedex.addressvalidation.AddressValidationServiceStub;
import com.fedex.service.FedexService;
import com.fedex.service.addressvalidation.beans.AddressValidationInput;
import com.fedex.service.addressvalidation.beans.AddressValidationOutput;
import com.fedex.service.addressvalidation.constants.FedexAddressValidationConstants;
import com.fedex.configuration.FedexConfiguration;
import com.fedex.ws.addressvalidation.v4.Address;
import com.fedex.ws.addressvalidation.v4.AddressToValidate;
import com.fedex.ws.addressvalidation.v4.AddressValidationReply;
import com.fedex.ws.addressvalidation.v4.AddressValidationReplyDocument;
import com.fedex.ws.addressvalidation.v4.AddressValidationRequest;
import com.fedex.ws.addressvalidation.v4.AddressValidationRequestDocument;
import com.fedex.ws.addressvalidation.v4.AddressValidationResult;
import com.fedex.ws.addressvalidation.v4.ClientDetail;
import com.fedex.ws.addressvalidation.v4.VersionId;
import com.fedex.ws.addressvalidation.v4.WebAuthenticationCredential;
import com.fedex.ws.addressvalidation.v4.WebAuthenticationDetail;


public class FedexAddressValidationService extends FedexService {
	
	private static final String COUNTRY_CODE = "US";

	private String mFedExEndPointURL;
	private FedexConfiguration mFedexConfiguration;
	
	/**
	 * @param pAddressValidationInput
	 * @return AddressValidationOutput
	 * @throws RemoteException
	 * 
	 * This method makes the call to the API and converts the reply document to a return object.
	 */
	public AddressValidationOutput ValidateAddress(
			AddressValidationInput pAddressValidationInput)
			throws RemoteException {

		if (isLoggingDebug()) {
			logDebug(getClass().getName() + " :: "+ "ValidateAddress(): Start");
		}

		AddressValidationOutput mAddressValidationOutput = null;
		if (null == pAddressValidationInput) {
			return mAddressValidationOutput;
		}

		AddressValidationRequest mAddressValidationRequest = AddressValidationRequest.Factory
				.newInstance();
		mAddressValidationRequest
				.setWebAuthenticationDetail(getWebAuthenticationDetail());
		mAddressValidationRequest.setClientDetail(getClientDetail());
		mAddressValidationRequest.setVersion(getVersionId());
		mAddressValidationRequest.setInEffectAsOfTimestamp(getTimeStamp());
		

		Address mAddress = Address.Factory.newInstance();
		mAddress.setCity(pAddressValidationInput.getCity());
		mAddress.setCountryCode(COUNTRY_CODE);
		mAddress.setPostalCode(pAddressValidationInput.getZipCode());
		mAddress.setStateOrProvinceCode(pAddressValidationInput.getState());
		String[] streetLineArray = new String[2];
		streetLineArray[0] = pAddressValidationInput.getAddressLine1();
		streetLineArray[1] = pAddressValidationInput.getAddressLine2();
		mAddress.setStreetLinesArray(streetLineArray);

		AddressToValidate mAddressToValidate = AddressToValidate.Factory
				.newInstance();
		mAddressToValidate.setAddress(mAddress);
		
		AddressToValidate[] mAddressToValidationArray = new AddressToValidate[1];
		mAddressToValidationArray[0] = mAddressToValidate;

		mAddressValidationRequest
				.setAddressesToValidateArray(mAddressToValidationArray);

		AddressValidationRequestDocument mAddressValidationRequestDocument = AddressValidationRequestDocument.Factory
				.newInstance();
		mAddressValidationRequestDocument
				.setAddressValidationRequest(mAddressValidationRequest);
		
		if (isLoggingDebug()) {
			logDebug(mAddressValidationRequestDocument.toString());
		}

	
	AddressValidationServiceStub mAddressValidationServiceStub = new AddressValidationServiceStub(
				getFedExEndPointURL());
		AddressValidationReplyDocument mAddressValidationReplyDocument = mAddressValidationServiceStub
				.addressValidation(mAddressValidationRequestDocument);

		mAddressValidationOutput = getAddressValidationOutput(mAddressValidationReplyDocument);

		if (isLoggingDebug()) {
			logDebug(getClass().getName() + " :: "+ "ValidateAddress():"+ " Addressvalidation Output Object"+ mAddressValidationOutput.toString());
			logDebug(getClass().getName() + " :: "+ "ValidateAddress(): End");
		}

		return mAddressValidationOutput;

	}

	private WebAuthenticationDetail getWebAuthenticationDetail() {
		WebAuthenticationDetail mWebAuthenticationDetail = WebAuthenticationDetail.Factory
				.newInstance();
		WebAuthenticationCredential mWebAuthenticationCredential = WebAuthenticationCredential.Factory
				.newInstance();
		mWebAuthenticationCredential.setKey(getFedexConfiguration().getApiAccessKey());
		mWebAuthenticationCredential
				.setPassword(getFedexConfiguration().getApiAccessPassword());
		mWebAuthenticationDetail
				.setUserCredential(mWebAuthenticationCredential);
		return mWebAuthenticationDetail;
	}

	private ClientDetail getClientDetail() {
		ClientDetail mClientDetail = ClientDetail.Factory.newInstance();
		mClientDetail.setAccountNumber(getFedexConfiguration().getApiAccessAccountNumber());
		mClientDetail.setMeterNumber(getFedexConfiguration().getApiAccessMeterNumber());
		return mClientDetail;
	}

	private VersionId getVersionId() {
		VersionId mVersionId = VersionId.Factory.newInstance();
		mVersionId.setServiceId(getApiVersionServiceId());
		mVersionId.setIntermediate(getApiVersionIntermediate());
		mVersionId.setMajor(getApiVersionMajor());
		mVersionId.setMinor(getApiVersionMinor());
		return mVersionId;
	}


	private Calendar getTimeStamp(){
		return Calendar.getInstance();
	}


	private AddressValidationOutput getAddressValidationOutput(
			AddressValidationReplyDocument mAddressValidationReplyDocument) {
		
		if (isLoggingDebug()) {
			logDebug(getClass().getName() + " :: "+ "getAddressValidationOutput(): Start");
			logDebug(mAddressValidationReplyDocument.toString());
		}
		AddressValidationOutput mAddressValidationOutput = new AddressValidationOutput();
		AddressValidationReply mAddresValidationReply = mAddressValidationReplyDocument
				.getAddressValidationReply();
	
		if(null != mAddresValidationReply && "SUCCESS".equalsIgnoreCase(mAddresValidationReply.getHighestSeverity().toString())
				&&mAddresValidationReply.getAddressResultsArray().length>0){
			
			if (isLoggingDebug()) {
				logDebug(getClass().getName() +  "::" + "getAddressValidationOutput(): AddressHighestSeverity Success");
			}
			
			
			AddressValidationResult mAddressValidationNode = mAddresValidationReply.getAddressResultsArray()[0];

			mAddressValidationOutput.setClassification(mAddressValidationNode.getClassification().toString());
			mAddressValidationOutput.setEffectiveCity(mAddressValidationNode.getEffectiveAddress().getCity());
			mAddressValidationOutput.setEffectiveState(mAddressValidationNode.getEffectiveAddress().getStateOrProvinceCode());
			mAddressValidationOutput.setEffectiveZipCode(mAddressValidationNode.getEffectiveAddress().getPostalCode());

			String[] streetLineArrayOutput = mAddressValidationNode.getEffectiveAddress().getStreetLinesArray();
			if (streetLineArrayOutput.length > 0) {
				mAddressValidationOutput.setEffectiveStreetLine1(streetLineArrayOutput[0]);
				String addressline2 = null;
				if(streetLineArrayOutput.length > 1){
					addressline2 = streetLineArrayOutput[1];
					mAddressValidationOutput.setEffectiveStreetLine2(addressline2);
				}
			}
			int attributesArray=mAddressValidationNode.getAttributesArray().length;
			for(int i=0;i<attributesArray;i++){
				String name=mAddressValidationNode.getAttributesArray(i).getName();
				Boolean value=new Boolean(mAddressValidationNode.getAttributesArray(i).getValue());
				if(FedexAddressValidationConstants.COUNTRY_SUPPORTED.equalsIgnoreCase(name)){
					mAddressValidationOutput.setCountrySupported(value);
				}
				else if(FedexAddressValidationConstants.MULTIPLE_MATCHES.equalsIgnoreCase(name)){
					mAddressValidationOutput.setMultipleMatches(value);
				}
				else if(FedexAddressValidationConstants.RESOLVED.equalsIgnoreCase(name)){
					mAddressValidationOutput.setResolved(value);
				}
				else if(FedexAddressValidationConstants.STREED_ADDRESS_VALIDATED.equalsIgnoreCase(name)){
					mAddressValidationOutput.setStreetAddressValidated(value);
				}
				else if(FedexAddressValidationConstants.POSTAL_CODE_VALIDATED.equalsIgnoreCase(name)){
					mAddressValidationOutput.setPostalCodeValidated(value);
				}
				else if(FedexAddressValidationConstants.STREET_RANGE_VALIDATED.equalsIgnoreCase(name)){
					mAddressValidationOutput.setStreetRangeValidated(value);
				}
				else if(FedexAddressValidationConstants.CITY_STATE_VALIDATED.equalsIgnoreCase(name)){
					mAddressValidationOutput.setCityStateValidated(value);
				}

			}
		}

		return mAddressValidationOutput;
	}

	public String getFedExEndPointURL() {
		return mFedExEndPointURL;
	}

	public void setFedExEndPointURL(String mFedExEndPointURL) {
		this.mFedExEndPointURL = mFedExEndPointURL;
	}
	
	public FedexConfiguration getFedexConfiguration(){
		return mFedexConfiguration;
	}
	
	public void setFedexConfiguration(FedexConfiguration pFedexConfiguration){
		this.mFedexConfiguration=pFedexConfiguration;
	}
	
	
	

}
