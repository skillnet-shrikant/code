package com.mff.commerce.util;

import atg.core.util.Address;
import atg.core.util.StringUtils;


public class AddressUtil {

	/**
	 * Check if country is international
	 * @param pAddress
	 * @return true if country is international i.e. not usa and not canada
	 */
	public static boolean isInternational(Address pAddress){		
		return (pAddress!=null) && !isNorthAmerica(pAddress.getCountry());
	}

	public static boolean isInternational(String pCountry) {
		return !isNorthAmerica(pCountry);
	}
	
	public static boolean isNorthAmerica(String pCountry){		
		return isUSA(pCountry)||isCanada(pCountry);
	}
	
	public static boolean isUSA(String pCountry){		
		return "USA".equalsIgnoreCase(pCountry) || "US".equalsIgnoreCase(pCountry);
	}
	
	public static boolean isCanada(String pCountry){		
		return "Canada".equalsIgnoreCase(pCountry) || "CA".equalsIgnoreCase(pCountry);
	}

	/**
	 * Check if postal code/zip code is empty
	 * @param pAddress
 	 * @return true if postal code/zip code is empty
	 */
	public static boolean isPostalCodeEmpty(Address pAddress){		
		return (pAddress != null) && StringUtils.isBlank(pAddress.getPostalCode());
	}
	
	/**
	 * Helper method to parse and hyphenate the zip code, at this point the zip code must have be validated
	 * We need to add hyphen if the valid zip has 9 or 10 digits
	 * if zip has 9 digits, just add hyphen after first 5 digits, example valid zip 123456789
	 * if zip has 10 digits, add hypen after first 5 digits and add from the 7th character, example valid zip 12345 6789 
	 * @param zip
	 * @return
	 */
	public static String hyphenateZip(String zip){
		String hyphenatedZip = zip;
		if (zip!=null) {
			if (zip.length() == 9) {
				hyphenatedZip = zip.substring(0, 5) + '-' + zip.substring(5);
			}
			else if (zip.length() == 10) {
				// If the zip is already an hypenated one, just return it.
				if (zip.substring(5, 6).equals("-")) {
					return zip;
				}
				hyphenatedZip = zip.substring(0, 5) + '-' + zip.substring(6);
			}
		}
		return hyphenatedZip;
	}
}
