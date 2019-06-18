package com.fedex.service.addressvalidation.test;

import java.rmi.RemoteException;

import com.fedex.service.addressvalidation.FedexAddressValidationService;
import com.fedex.service.addressvalidation.beans.AddressValidationInput;
import com.fedex.service.addressvalidation.beans.AddressValidationOutput;

import atg.core.util.Address;
import atg.nucleus.GenericService;

public class AddressValidationTest extends GenericService {
	
	
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String zipCode;
	
	private FedexAddressValidationService mFedexAddressValidationService; 
	
	public FedexAddressValidationService getAddressValidationService(){
		return mFedexAddressValidationService;
	}
	
	public void setAddressValidationService(FedexAddressValidationService pFedexAddressValidationService){
		mFedexAddressValidationService=pFedexAddressValidationService;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	
	public void testAddressValidation(){
		AddressValidationInput avsInput=new AddressValidationInput();
		
		avsInput.setAddressLine1(getAddressLine1());
		avsInput.setAddressLine2(getAddressLine2());
		avsInput.setCity(getCity());
		avsInput.setState(getState());
		avsInput.setZipCode(getZipCode());
		
		Address inputAddress=new Address();
		inputAddress.setAddress1(avsInput.getAddressLine1().trim().toUpperCase());
		if(avsInput.getAddressLine2()!=null){
			inputAddress.setAddress2(avsInput.getAddressLine2().trim().toUpperCase());
		}
		inputAddress.setCity(avsInput.getCity().trim().toUpperCase());
		inputAddress.setState(avsInput.getState().trim().toUpperCase());
		inputAddress.setPostalCode(avsInput.getZipCode().trim().toUpperCase());
		inputAddress.setCountry("US");
		Address outputAddress=new Address();
		
		System.out.println("Address Entered: ");
		System.out.println(inputAddress.getAddress1().toUpperCase());
		System.out.println(inputAddress.getAddress2().toUpperCase());
		System.out.println(inputAddress.getCity().toUpperCase());
		System.out.println(inputAddress.getState().toUpperCase());
		System.out.println(inputAddress.getPostalCode().toUpperCase());
		System.out.println(inputAddress.getCountry().toUpperCase());
		
		try {
			AddressValidationOutput avsOutput=getAddressValidationService().ValidateAddress(avsInput);
			if(avsOutput.isResolved()){

				outputAddress.setAddress1(avsOutput.getEffectiveStreetLine1().trim().toUpperCase());
				if(avsOutput.getEffectiveStreetLine2()!=null){
					outputAddress.setAddress2(avsOutput.getEffectiveStreetLine2().trim().toUpperCase());
				}
				outputAddress.setCity(avsOutput.getEffectiveCity().trim().toUpperCase());
				outputAddress.setState(avsOutput.getEffectiveState().trim().toUpperCase());
				outputAddress.setPostalCode(avsOutput.getEffectiveZipCode().trim().toUpperCase());
				outputAddress.setCountry("US".toUpperCase());
				
				if(inputAddress.equals(outputAddress)){
					System.out.println("Input address and output address are equal: Use the address entered");
				}
				else {
					System.out.println("Use the suggested address:");
					System.out.println(outputAddress.getAddress1().toUpperCase());
					System.out.println(outputAddress.getAddress2().toUpperCase());
					System.out.println(outputAddress.getCity().toUpperCase());
					System.out.println(outputAddress.getState().toUpperCase());
					System.out.println(outputAddress.getPostalCode().toUpperCase());
					System.out.println(outputAddress.getCountry().toUpperCase());
				}
				
			}
			else {
				System.out.println("Cannot validate the address: Use the address entered");
			}
		} catch (RemoteException e) {
			System.out.println("Cannot validate the address: Use the address entered");
		}
		
		
		
		
	}

}
