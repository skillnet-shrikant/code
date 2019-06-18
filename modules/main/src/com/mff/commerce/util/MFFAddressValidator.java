package com.mff.commerce.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mff.constants.MFFConstants;
import com.mff.userprofiling.MFFProfileTools;

import atg.commerce.util.AddressValidatorImpl;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.GenericFormHandler;
import atg.droplet.MFFFormExceptionGenerator;
import atg.servlet.DynamoHttpServletRequest;


public class MFFAddressValidator extends AddressValidatorImpl {
	
	
	private MFFFormExceptionGenerator mFormExceptionGenerator;
	private Properties mErrorCodeToAddressField;
	private Properties mAddressErrorCodeIdentityMap;
	MFFProfileTools mProfileTools;
	private boolean mvalidateAddress1NotPOBox;
	private boolean mvalidateAddress2NotPOBox;
	private boolean mValidateThreePartPhoneNumber;
	private boolean mValidateAddress2;
	private String mPhone;
	private static final int CITY_LENGTH_MAX=25;
	private static final int FIRSTNAME_LENGTH_MAX=15;
	private static final int LASTNAME_LENGTH_MAX=20;
	private static final int ADDR1_LENGTH_MAX=30;
	private static final int ADDR2_LENGTH_MAX=30;
	private static final int PHONE_NUMBER_LENGTH=10;
	private String mShippingMethod;
	private String mStandardShippingMethod;
	public static final String[] notAllowed={"P O BOX","PO BOX","P.O. BOX","P.O.BOX","P.O BOX"};
	public static final String[] allowed={"AFO","APO","FPO"};
	public static final String ARMED_FORCES_AMERICA="AA";
	public static final String ARMED_FORCES_EUROPE="AE";
	public static final String ARMED_FORCES_PACIFIC="AP";
	private static final String OMIT_CHARACTERS_PATTERN=".*[&><].*";
	
	private int minAsciiValue = 32;
	
	private int maxAsciiValue = 127;
	
	private char excludeChars[] = null;
	
	// PO Box validation pattern
	public static final String PO_BOX_PATTERN = "^[P|p]*(OST|ost)*\\.*\\s*[O|o|0]*(ffice|FFICE)*\\.*\\s*[B|b][O|o|0][X|x]\\s*(\\d.)*";
	public static final String ZIP_CODE_PATTERN = "^\\d{5}[- ]?\\d{4}$|^\\d{5}$";
	
	/**
	 * This method checks if addresses contain a PO Box. If yes then only standard shipping is allowed
	 * @param pAddressToCheck
	 * @return
	 */
	public boolean addressPOBoxShipMethodMatch(String pAddressToCheck, String shipMethod)
	{
		
		if(pAddressToCheck == null || pAddressToCheck.isEmpty() || shipMethod == null || shipMethod.isEmpty()){
			// We return true even if empty. false is returned only when an explicit check fails below
			if (isLoggingDebug()) {logDebug("Returning true - address to check and shipmethod were empty...");}
			return true;
		}
		
		//String shipMethod = getShippingMethod(); // Property is set by form handler
		String stdMethod = getStandardShippingMethod(); // Property set by properties file 
		
		for(int i=0;i < notAllowed.length;i++)
		{
			// If the address contains a PO Box. Convert both the to be compared strings to lowercase for an even comparison
			if(pAddressToCheck.toLowerCase().contains(notAllowed[i].toLowerCase()))
			{
				// This was an allowed PO Box but shipping method was either missing or not chosen as Standard
				if(shipMethod == null || shipMethod.isEmpty()  || !shipMethod.equalsIgnoreCase(stdMethod))
				{
					if (isLoggingDebug()) {logDebug("Returning false - allowed PO Box/missing shipping method or not selected as standard...");}
					return false;
				}
				
				// This is an allowed PO Box and shipping method chosen was standard
				if (isLoggingDebug()) {logDebug("Returning true - allowed PO Box & shipping method was standard...");}
				return true;

			}
		}
		
		// This address has no issues with the PO Box
		if (isLoggingDebug()) {logDebug("Returning true - address has no issues with PO Box...");}
		return true;
		
	}
	
	/**
	 * This method checks if addresses contain a PO Box.
	 * @param pAddressToCheck
	 * @return
	 */
	public boolean addressContainsPOBox(String pAddressToCheck) {
		for(int i=0;i < notAllowed.length;i++){
			// If the address contains a PO Box. Convert both the to be compared strings to lowercase for an even comparison
			if(pAddressToCheck.toLowerCase().contains(notAllowed[i].toLowerCase()))	{
				if (isLoggingDebug()) {logDebug("Returning true - Address contain PO Box...");}
				return true;
			}
		}
		
		// This address has no issues with the PO Box
		if (isLoggingDebug()) {logDebug("Returning false - address does not contain PO Box...");}
		return false;
		
	}
	
	/**
	 * This method validates the given address string against a PO Box regular expression
	 * It returns true if it matches with the pattern
	 * @param pAddressToCheck
	 * @return
	 */
	public boolean isItAPoBoxAddress(String pAddressToCheck) {
		vlogDebug("EXTNAddressValidator->isItAPoBoxAddress: Address string: {0} ", new Object[]{pAddressToCheck});
		Pattern poBoxPattern =  Pattern.compile(PO_BOX_PATTERN, Pattern.CASE_INSENSITIVE);
		boolean poBoxAddress = poBoxPattern.matcher(pAddressToCheck).find();
		vlogDebug("EXTNAddressValidator->isItAPoBoxAddress: poBoxAddress {0} ", new Object[]{poBoxAddress});
		return poBoxAddress;
	}
	

	/**
	 * If state chosen is AA/AE/AP then only standard shipping is allowed
	 * @param pAddressToCheck
	 * @return
	 */
	public boolean stateShipMethodMatch(String state, String shipMethod)
	{
		
		if (isLoggingDebug()) {logDebug("State is : " + state + "ShipMethod is :" + shipMethod);}
		if(state == null || state.isEmpty() || shipMethod == null || shipMethod.isEmpty()){
			// We return true even if empty. false is returned only when an explicit check fails below
			if (isLoggingDebug()) {logDebug("Returning true - state and shipmethod were empty...");}
			return true;
		}
		
		String stdMethod = getStandardShippingMethod(); // Property set by properties file 
		
		if(state.equalsIgnoreCase(ARMED_FORCES_AMERICA) || state.equalsIgnoreCase(ARMED_FORCES_EUROPE) || state.equalsIgnoreCase(ARMED_FORCES_PACIFIC))
		{
			if(shipMethod.equalsIgnoreCase(stdMethod))
			{
				if (isLoggingDebug()) {logDebug("Returning true - state was AA/AE/AP & shipping method was standard...");}
				return true;
			}
			
			if (isLoggingDebug()) {logDebug("Returning false - state was AA/AE/AP & shipping method was not standard...");}
			return false;
				
		}
		
		if (isLoggingDebug()) {logDebug("Returning true - state was not AA/AE/AP");}
		return true;
		
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Collection validateAddress(Address pAddress, Locale pLocale)
     {
         ResourceBundle resourceBundle = MFFConstants.getEXTNResources();
         return validateAddress(pAddress, resourceBundle);
     }
	
	/**
	 * @param pAddr
	 * @param pRequest
	 * @param pFormHandler
	 */
	@SuppressWarnings("rawtypes")
	public void validateAddress(Address pAddr, DynamoHttpServletRequest pRequest, GenericFormHandler pFormHandler) {
		
		Map fieldNameToError = validateAddress(pAddr, MFFConstants.getEXTNResources(pRequest), createResourceAndErrorKeyMaps());
		
		if (fieldNameToError != null && !fieldNameToError.isEmpty()) {
			Iterator iter = fieldNameToError.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String errorMsg = (String) fieldNameToError.get(key);
				if(isLoggingDebug())
				{
					logDebug("Key="+key+":Error="+errorMsg);
				}
				getFormExceptionGenerator().generateInlineException(errorMsg, key, pFormHandler, pRequest);
			}
		}
	}
	
	/**
	 * @param pAddr
	 * @param pRequest
	 * @param pFormHandler
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void validateCustomerAddress(Address pAddr, DynamoHttpServletRequest pRequest, GenericFormHandler pFormHandler, String pContainerName) {
		
		Map fieldNameToError = validateAddress(pAddr, MFFConstants.getEXTNResources(pRequest), createResourceAndErrorKeyMaps());
		if (fieldNameToError != null && !fieldNameToError.isEmpty()) {
			
			Iterator iter = fieldNameToError.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				List<String> errorProps = new ArrayList<String>();
				errorProps.add(key);
				String errorMsg = (String) fieldNameToError.get(key);
				if(isLoggingDebug())
				{
					logDebug("Key="+key+":Error="+errorMsg);
				}
				getFormExceptionGenerator().generateInlineException(errorMsg, errorProps, 
						pContainerName, pFormHandler, pRequest);
			}
		}
	}
	
	/**
	 * This method return the Map of propertyName as key and errorMsg as value
	 * 
	 * @param pAddr
	 * @param pRequest
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> validateAddress(Address pAddr, DynamoHttpServletRequest pRequest) {
		
		Map<String,String> fieldNameToError = validateAddress(pAddr, MFFConstants.getEXTNResources(pRequest), createResourceAndErrorKeyMaps());
		
		vlogDebug("validateAddress fieldNameToError : {0}",fieldNameToError);
		return fieldNameToError;
	}
	
	
	/**
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map createResourceAndErrorKeyMaps() {
		Map map = new HashMap();
		map.put("errorMapKey", getErrorCodeToAddressField());
		map.put("resourceMapKey", getAddressErrorCodeIdentityMap());
		return map;
	}
	
	
	
	/* (non-Javadoc)
	 * @see atg.commerce.util.AddressValidatorImpl#validateAddress(atg.core.util.Address, java.util.ResourceBundle, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	protected Map validateAddress(Address pAddr, ResourceBundle pResources, Map resourceAndErrorKeys) {

		if (isLoggingDebug()) {
			logDebug("Start of EXTNAddressValidator.validateAddress...");
			logDebug("Validating Address of Type:" + pAddr.getClass().getName());
		}

		Map returnErrors = new HashMap();
		
		vlogDebug("In EXTNAddress Validator Country: {0}, State: {1}, PostalCode: {2}",pAddr.getCountry(), pAddr.getState(), pAddr.getPostalCode());
		
		//OOTB code looks for zip code		
		//for international, zip code is not required.
		//remove the error for international
		if (AddressUtil.isInternational(pAddr) && 
				returnErrors!=null){
			
			vlogDebug("Removing Postal Code Error");
			if(returnErrors.containsKey("postalCode"))
				returnErrors.remove("postalCode");
			if(returnErrors.containsKey("PostalCodeMissing"))
				returnErrors.remove("PostalCodeMissing");
		}

		// Now Continue with our Validations
		
		// Validate the First Name
		if(getValidateFirstName()){
			String name = pAddr.getFirstName();
			if(name == null || name.isEmpty() || name.length() > FIRSTNAME_LENGTH_MAX || name.matches(OMIT_CHARACTERS_PATTERN)){
				if (isLoggingDebug()) {logDebug("Value for firstname is either null,empty or exceeded maximum characters allowed...");}
				addError (MFFConstants.ENTER_VALID_FIRSTNAME, pResources, resourceAndErrorKeys, returnErrors);
			}
		}
				
		// Validate the last name
		if(getValidateLastName()){
			String name = pAddr.getLastName();
			if(name == null || name.isEmpty() || name.length() > LASTNAME_LENGTH_MAX || name.matches(OMIT_CHARACTERS_PATTERN)){
				if (isLoggingDebug()) {logDebug("Value for secondname is either null,empty or exceeded maximum characters allowed...");}
				addError (MFFConstants.ENTER_VALID_LASTNAME, pResources, resourceAndErrorKeys, returnErrors);
			}
		}		
		// Validate Address1
		String addr1=pAddr.getAddress1();
		if(getValidateAddress1()){
			if (addr1 == null || addr1.isEmpty()) {
				if (isLoggingDebug()) {logDebug("Address1 is empty...");}
				addError (MFFConstants.ENTER_VALID_ADDRESS1, pResources, resourceAndErrorKeys, returnErrors);
			}
			else if(addr1.length() > ADDR1_LENGTH_MAX){
				if (isLoggingDebug()) {logDebug("Address1 exceeded allowed max characters...");}
				addError (MFFConstants.ENTER_VALID_ADDRESS1, pResources, resourceAndErrorKeys, returnErrors);
			}
			else if(!validateAlphaNumericAddressLine(addr1)){
				
				vlogDebug("Address1 is not alpha numeric..");
				addError (MFFConstants.ENTER_VALID_ADDRESS1, pResources, resourceAndErrorKeys, returnErrors);
			}			
			else if (getValidateAddress1NotPOBox()) {
				if (addressContainsPOBox(addr1)) {
					if (isLoggingDebug()) {logDebug("Address1 failed the check...");}
					addError (MFFConstants.MSG_POBOX_SHIP_ADDRESS1, pResources, resourceAndErrorKeys, returnErrors);
				}
			}
			
			
		}
		
		// Validate Address2
		String addr2=pAddr.getAddress2();
		if (isLoggingDebug()) {logDebug("The value of Address2 is ..." + addr2);}
		
		if(isValidateAddress2()){
			if (addr2 == null || addr2.isEmpty()) {
				if (isLoggingDebug()) {logDebug("Address2 is empty...");}
				// If address2 is empty do nothing since address 2 is optional
				//addError (MFFConstants.ENTER_VALID_ADDRESS2, pResources, resourceAndErrorKeys, returnErrors);
			}
			else if(addr2.length() > ADDR2_LENGTH_MAX){
				if (isLoggingDebug()) {logDebug("Address2 is empty...");}
				// If addr2 is present it cannot be greater then 60 characters 
				addError (MFFConstants.ENTER_VALID_ADDRESS2, pResources, resourceAndErrorKeys, returnErrors);
			}
			else if(!validateAlphaNumericAddressLine(addr2)){
					
					vlogDebug("Address1 is not alpha numeric..");
					addError (MFFConstants.ENTER_VALID_ADDRESS2, pResources, resourceAndErrorKeys, returnErrors);
			}			
			else if (getValidateAddress2NotPOBox()) {
				if (addressContainsPOBox(pAddr.getAddress2())) {
					if (isLoggingDebug()) {logDebug("Address2 failed the check...");}
					addError (MFFConstants.MSG_POBOX_SHIP_ADDRESS2, pResources, resourceAndErrorKeys, returnErrors);
				}
			}
			
		}
		
		// Validate the City
		if(getValidateCity()){
			String city = pAddr.getCity();
			if(city == null || city.isEmpty() || city.length() > CITY_LENGTH_MAX){
				if (isLoggingDebug()) {logDebug("Value for city is either null,empty or exceeded maximum characters allowed...");}
				addError (MFFConstants.ENTER_VALID_CITY, pResources, resourceAndErrorKeys, returnErrors);
			}
		}
		
		// Validate State
		if (getValidateState()){
			
			if((pAddr.getState() == null || pAddr.getState().isEmpty()) && (pAddr.getCountry() != null 
					&& (pAddr.getCountry().equalsIgnoreCase("US") || pAddr.getCountry().equalsIgnoreCase("CA")))) {
				if (isLoggingDebug()) {logDebug("State is missing...");}
				addError (MFFConstants.MSG_STATE_MISSING, pResources, resourceAndErrorKeys, returnErrors);
			}
			else if(!stateShipMethodMatch(pAddr.getState(), getShippingMethod())){
				if (isLoggingDebug()) {logDebug("State is armed forces not using the default shipping...");}
				addError (MFFConstants.MSG_ARMED_FORCES_SHIP, pResources, resourceAndErrorKeys, returnErrors);
			}
			
		}

		// Validate the Postal Code for US & Canada
		if (!AddressUtil.isInternational(pAddr) && getValidatePostalCode()) {
			if (AddressUtil.isPostalCodeEmpty(pAddr)) {
				if (isLoggingDebug()) {logDebug("Postal Code is empty...");}
				addError (MFFConstants.MSG_POSTAL_CODE_MISSING, pResources, resourceAndErrorKeys, returnErrors);
			}
			else {
				if (!validatePostalCode(pAddr.getPostalCode())) {
					if (isLoggingDebug()) {logDebug("Postal Code did not pass validation test..");}
					addError (MFFConstants.MSG_POSTAL_CODE_FORMAT_INVALID, pResources, resourceAndErrorKeys, returnErrors);
				}
			}
		}

		// Validate the Phone number
		if (getValidatePhoneNumber()) {
			
			String phone = ((ContactInfo)pAddr).getPhoneNumber();
      
      if(StringUtils.isEmpty(phone))
      {
        if (isLoggingDebug()) 
        {
          logDebug("Phone Number is empty...");
        }
        addError (MFFConstants.MSG_PHONE_NUMBER_MISSING, pResources, resourceAndErrorKeys, returnErrors);
      }
      else
      {			
  			// As a precautionary step convert phone to a purely numeric format by removing non-numeric characters and set it back to the shipping address
  			if (isLoggingDebug()) 
  			{
  			  logDebug("Phone number in original format was..." + phone);
  			}
  			phone = removeNonNumeric(phone);
  			((ContactInfo)pAddr).setPhoneNumber(phone);
  			if (isLoggingDebug()) 
  			{
  			  logDebug("Phone number after modification is..." + phone);
  			}

  			if(pAddr.getCountry() != null && pAddr.getCountry().equalsIgnoreCase("US") && !validatePhone(phone))
  			{
  				if (isLoggingDebug()) {logDebug("Phone Number is invalid...");}
  				addError (MFFConstants.MSG_PHONE_NUMBER_INVALID, pResources, resourceAndErrorKeys, returnErrors);
  			}
      }
			
		}

		return returnErrors;
	}
	
	/**
	 * This method is used to convert the phone numbers in different formats to a purely numeric format of type xxxxxxxxxx
	 * @param str
	 * @return
	 */
	private String removeNonNumeric(String str)
	{
		StringBuffer strBuff = new StringBuffer();
		
	    for (char c : str.toCharArray())
	    {
	        if (Character.isDigit(c)) {strBuff.append(c);}
	    }
	    
	    return strBuff.toString();
	}
	
	/**
	 * Method validates phone number
	 * @param phoneNumber
	 * @return
	 */
	private boolean validatePhone(String phoneNumber){
		
		if(phoneNumber.length() != PHONE_NUMBER_LENGTH || !isNumeric(phoneNumber))
		{
			return false;
		}
		
		if(phoneNumber.startsWith("0")){
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Method determines if a string is numeric
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
		
	/**
	 * Method to validate postal code, valid codes are 99999, 999999999, 99999-9999, 99999 9999
	 * @param pPostalCode
	 * @return
	 */
	private boolean validatePostalCode(String pPostalCode) {
		Pattern pattern = Pattern.compile(ZIP_CODE_PATTERN);
		Matcher matcher = pattern.matcher(pPostalCode);
		return matcher.matches();
	}
	
	private boolean validateAlphaNumericAddressLine(String pAddressLine) {
		boolean alphaNumercCheck = true;
		if (pAddressLine == null || pAddressLine.isEmpty()) {
			alphaNumercCheck = false;
		} else{
			char lAddressLineChar [] = pAddressLine.toCharArray();
			for (char lAddressChar : lAddressLineChar) {
				if (lAddressChar < getMinAsciiValue() || lAddressChar > getMaxAsciiValue()) {
					alphaNumercCheck = false;
					break;
					
				} else {
					if (excludeChars != null && excludeChars.length > 0) {
						for (char excluded : excludeChars) {
							if (lAddressChar == excluded) {
								alphaNumercCheck = false;
							}
						}
					}
					
				}
				
			}
			/**
			if(pAddressLine.matches("^.*[^a-zA-Z0-9 .-#].*$")){
				if (isLoggingDebug()) {logDebug("Returning true - invalid Address Line is not Alpha Numeric");}
				alphaNumercCheck =  true;			
			}else{
				if (isLoggingDebug()) {logDebug("Returning false - valid Address Line is Alpha Numeric");}
				alphaNumercCheck = false;		
			}
			**/
		}
		return alphaNumercCheck;			

		
	}
	

	// GETTERS & SETTERS
	
	public MFFProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(MFFProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

	public boolean getValidateAddress1NotPOBox() {
		return mvalidateAddress1NotPOBox;
	}

	public void setValidateAddress1NotPOBox(boolean pValidateAddress1NotPOBox) {
		mvalidateAddress1NotPOBox = pValidateAddress1NotPOBox;
	}

	public boolean getValidateAddress2NotPOBox() {
		return mvalidateAddress2NotPOBox;
	}

	public void setValidateAddress2NotPOBox(boolean pValidateAddress2NotPOBox) {
		mvalidateAddress2NotPOBox = pValidateAddress2NotPOBox;
	}

	public boolean getValidateThreePartPhoneNumber() {
		return mValidateThreePartPhoneNumber;
	}

	public void setValidateThreePartPhoneNumber(boolean pValidateThreePartPhoneNumber) {
		mValidateThreePartPhoneNumber = pValidateThreePartPhoneNumber;
	}

	public MFFFormExceptionGenerator getFormExceptionGenerator() {
		return mFormExceptionGenerator;
	}

	public void setFormExceptionGenerator(
			MFFFormExceptionGenerator pFormExceptionGenerator) {
		mFormExceptionGenerator = pFormExceptionGenerator;
	}

	public Properties getErrorCodeToAddressField() {
		return mErrorCodeToAddressField;
	}

	public void setErrorCodeToAddressField(Properties pErrorCodeToAddressField) {
		mErrorCodeToAddressField = pErrorCodeToAddressField;
	}

	public Properties getAddressErrorCodeIdentityMap() {
		return mAddressErrorCodeIdentityMap;
	}

	public void setAddressErrorCodeIdentityMap(
			Properties pAddressErrorCodeIdentityMap) {
		mAddressErrorCodeIdentityMap = pAddressErrorCodeIdentityMap;
	}
	
	public boolean isValidateAddress2() {
		return mValidateAddress2;
	}

	public void setValidateAddress2(boolean pValidateAddress2) {
		mValidateAddress2 = pValidateAddress2;
	}
	
	public String getShippingMethod() {
		return mShippingMethod;
	}

	public void setShippingMethod(String pShippingMethod) {
		mShippingMethod = pShippingMethod;
	}
	
	public String getStandardShippingMethod() {
		return mStandardShippingMethod;
	}

	public void setStandardShippingMethod(String pStandardShippingMethod) {
		mStandardShippingMethod = pStandardShippingMethod;
	}
	
	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String pPhone) {
		mPhone = pPhone;
	}
	
	// The below validate address method is the out of box method invoked by the checkout manager. 
	private String mPhoneAreaCode;
	private String mPhonePrefix;
	private String mPhoneSuffix;
	
	public String getPhonePrefix() {return mPhonePrefix;}
	public void setPhonePrefix(String phonePrefix) {mPhonePrefix = phonePrefix;}
	public String getPhoneSuffix() {return mPhoneSuffix;}
	public void setPhoneSuffix(String pPhoneSuffix) {mPhoneSuffix = pPhoneSuffix;}
	public String getPhoneAreaCode() {return mPhoneAreaCode;}
	public void setPhoneAreaCode(String pPhoneAreaCode) {mPhoneAreaCode = pPhoneAreaCode;}
	
	/**
	 * @param pAddr
	 * @param pPhoneAreaCode
	 * @param pPhonePrefix
	 * @param pPhoneSuffix
	 * @param pResources
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Collection validateAddress(Address pAddr, String pPhoneAreaCode, String pPhonePrefix, String pPhoneSuffix, ResourceBundle pResources) 
	{
		if (isLoggingDebug()) {logDebug("Out of Box validate Address method initiated by checkout manager in EXTNAddressValiator ...");}

		// Store the params locally so the overwrite of validateAddress can access the values.
		setPhoneAreaCode(pPhoneAreaCode);
		setPhonePrefix(pPhonePrefix);
		setPhoneSuffix(pPhoneSuffix);

		// Call the above extension to OOTB:
		return validateAddress(pAddr, pResources);
	}
	

	/**
	 * @param pAddr
	 * @param pPhoneAreaCode
	 * @param pPhonePrefix
	 * @param pPhoneSuffix
	 * @param pRequest
	 * @param pFormHandler
	 */
	public void validateAddress(Address pAddr, String pPhoneAreaCode, String pPhonePrefix, String pPhoneSuffix, DynamoHttpServletRequest pRequest, GenericFormHandler pFormHandler) {
		// Store the params locally so the overwrite of validateAddress can access the values.
		if (isLoggingDebug()) {
			logDebug("setting phoneAreaCode to " + pPhoneAreaCode);
		}
		setPhoneAreaCode(pPhoneAreaCode);
		setPhonePrefix(pPhonePrefix);
		setPhoneSuffix(pPhoneSuffix);
		
		validateAddress(pAddr, pRequest, pFormHandler);
	}

	public int getMinAsciiValue() {
		return minAsciiValue;
	}

	public void setMinAsciiValue(int minAsciiValue) {
		this.minAsciiValue = minAsciiValue;
	}

	public int getMaxAsciiValue() {
		return maxAsciiValue;
	}

	public void setMaxAsciiValue(int maxAsciiValue) {
		this.maxAsciiValue = maxAsciiValue;
	}

	public char[] getExcludeChars() {
		return excludeChars;
	}

	public void setExcludeChars(char[] excludeChars) {
		this.excludeChars = excludeChars;
	}

}
