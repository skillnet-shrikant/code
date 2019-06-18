package com.aci.payment.creditcard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.payment.creditcard.CreditCardTools;

public class AciCreditCardTools extends CreditCardTools {
	
	private Map<String, String> mReverseCardCodeMap = new HashMap<String, String>();
	
	private List<String> mSupportedCreditCardTypes;
	
	private Map<String, String> mCCTypesMap;

	public Map<String, String> getCCTypesMap() {
		return mCCTypesMap;
	}

	public void setCCTypesMap(Map<String, String> pCCTypesMap) {
		mCCTypesMap = pCCTypesMap;
	}

	public List<String> getSupportedCreditCardTypes() {
		return mSupportedCreditCardTypes;
	}

	public void setSupportedCreditCardTypes(
			List<String> pSupportedCreditCardTypes) {
		mSupportedCreditCardTypes = pSupportedCreditCardTypes;
	}

	/**
	 * @return the reverseCardCodeMap
	 */
	public Map<String, String> getReverseCardCodeMap() {
		return mReverseCardCodeMap;
	}

	/**
	 * @param pReverseCardCodeMap the reverseCardCodeMap to set
	 */
	public void setReverseCardCodeMap(Map<String, String> pReverseCardCodeMap) {
		mReverseCardCodeMap = pReverseCardCodeMap;
	}
	

}
