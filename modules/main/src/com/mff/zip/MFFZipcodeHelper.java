package com.mff.zip;

import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.mff.constants.MFFConstants;

public class MFFZipcodeHelper extends GenericService {

	private Repository mZipcodeRepository;	
	private String mZipcodeQuery;
	private String mZipcodeItemDescriptorName;
	private String mCityStateZipErrorField;
	private String mFedExOvernightRestricted;
	private boolean includeVariants;
	private boolean includeUnacceptableCities;
	

	/**
	 * getter setters for 
	 * zipcoderepository
	 * zipcodequery
	 * zipcodeItemdesciptorName
	 **/
	public boolean isIncludeVariants() {
		return includeVariants;
	}
	public void setIncludeVariants(boolean pIncludeVariants) {
		includeVariants = pIncludeVariants;
	}
	public boolean isIncludeUnacceptableCities() {
		return includeUnacceptableCities;
	}
	public void setIncludeUnacceptableCities(boolean pIncludeUnacceptableCities) {
		includeUnacceptableCities = pIncludeUnacceptableCities;
	}
	public Repository getZipcodeRepository() {
		return mZipcodeRepository;
	}
	public void setZipcodeRepository(Repository pZipcodeRepository) {
		mZipcodeRepository = pZipcodeRepository;
	}
	public String getZipcodeQuery() {
		return mZipcodeQuery;
	}
	public void setZipcodeQuery(String pZipcodeQuery) {
		mZipcodeQuery = pZipcodeQuery;
	}
	
	public String getZipcodeItemDescriptorName() {
		return mZipcodeItemDescriptorName;
	}
	public void setZipcodeItemDescriptorName(String pZipcodeItemDescriptorName) {
		mZipcodeItemDescriptorName = pZipcodeItemDescriptorName;
	}

	/**
	 * retrieves the Zipcode RepositoryItem with zipcode value
	 * @param Zipcode String
	 * @return Zipcode RepositoryItem
	 */
	public RepositoryItem retrieveZipcodeItembyZipcode(String pZipcode){
		
		try {
			RepositoryView lRepositoryView = getZipcodeRepository().getView(getZipcodeItemDescriptorName());
			RqlStatement lRqlStatement = RqlStatement.parseRqlStatement(getZipcodeQuery());
			Object params[] = new Object[]{pZipcode};
			RepositoryItem [] lZipcodeItems = lRqlStatement.executeQuery (lRepositoryView, params);
			if(lZipcodeItems!=null && lZipcodeItems.length>0){
				return lZipcodeItems[0];
			}
		} catch (RepositoryException e) {
			vlogError(e.getMessage());
		}
		return null;
	}
	
	/**
	 * Validates the city and state with Zipcode item.
	 * @param Map pInputAddress
	 * @return boolean matched
	 */
	public boolean isValidateCityStateZipCombination(Map pInputAddress) {
		
		String inputPostalCode = (String) pInputAddress.get(MFFConstants.ADDRESS_POSTAL_CODE);
		if (inputPostalCode.contains(MFFConstants.HYPHEN )){
			String[] zipParts = inputPostalCode.split(MFFConstants.HYPHEN );
			inputPostalCode = zipParts[0];
		}
		
		boolean matched = false;
		RepositoryItem zipItem = retrieveZipcodeItembyZipcode(inputPostalCode);
		
		if (zipItem != null) {
			String city = (String)zipItem.getPropertyValue(MFFConstants.ADDRESS_CITY);
			String state = (String)zipItem.getPropertyValue(MFFConstants.ADDRESS_STATE);
			
			if (!StringUtils.isBlank(city) && !StringUtils.isBlank(state)){
				String inputCity = (String) pInputAddress.get(MFFConstants.ADDRESS_CITY);
				String inputState = (String) pInputAddress.get(MFFConstants.ADDRESS_STATE);
				
				if (city.equalsIgnoreCase(inputCity) && state.equalsIgnoreCase(inputState)){
					matched = true;
				}
				if(!matched && isIncludeVariants()) {
					// acceptable
					String acceptableCities = (String)zipItem.getPropertyValue(MFFConstants.ACCEPTABLE_CITIES);
					if(acceptableCities != null) {
						String[] arrAcceptableCities = acceptableCities.split(",");
						for (String acceptableCity: arrAcceptableCities) {           
							if (acceptableCity.trim().equalsIgnoreCase(inputCity) && state.equalsIgnoreCase(inputState)){
								matched = true;
								break;
							}
						}
					}
					if(!matched && isIncludeUnacceptableCities()) {
						String unacceptableCities = (String)zipItem.getPropertyValue(MFFConstants.UNACCEPTABLE_CITIES);
						if(unacceptableCities != null) {
							String[] arrUnacceptableCities = unacceptableCities.split(",");
							for (String unacceptableCity: arrUnacceptableCities) {           
								if (unacceptableCity.trim().equalsIgnoreCase(inputCity) && state.equalsIgnoreCase(inputState)){
									matched = true;
									break;
								}
							}				
						}
					}
				}
			}
		}
		return matched;
	}
	
	/**
	 * Validates if the Zipcode is in Fedex overnight restricted data.
	 * @param String pZipCode
	 * @return boolean matched
	 */
	public boolean isZipRestrictedForFedExOvernight(String pInputZipCode) {
		vlogDebug("isZipRestrictedForFedExOvernight(): pInputZipCode: " + pInputZipCode);
		
		if(StringUtils.isBlank(pInputZipCode)){
			return false;
		}
		
		String inputZipCode = pInputZipCode;
		if (pInputZipCode.contains(MFFConstants.HYPHEN )){
			String[] zipParts = pInputZipCode.split(MFFConstants.HYPHEN );
			inputZipCode = zipParts[0];
		}
		
		RepositoryItem zipItem = null;
		try {
			zipItem = getZipcodeRepository().getItem(inputZipCode, getFedExOvernightRestricted());
		} catch (RepositoryException e) {
			if (isLoggingError()){
				logError("RepositoryException while looking up zipcode: " + e, e);
			}
		}
		
		if (zipItem != null) {
			return true;
		}
		return false;
	}
	
	public String getCityStateZipErrorField() {
		return mCityStateZipErrorField;
	}
	public void setCityStateZipErrorField(String pCityStateZipErrorField) {
		mCityStateZipErrorField = pCityStateZipErrorField;
	}
	
	public String getFedExOvernightRestricted() {
		return mFedExOvernightRestricted;
	}
	public void setFedExOvernightRestricted(String pFedExOvernightRestricted) {
		mFedExOvernightRestricted = pFedExOvernightRestricted;
	}
}
