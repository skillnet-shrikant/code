package com.aci.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aci.constants.FieldMappingConstants;
import com.liveprocessor.LPClient.LPTransaction;

import atg.commerce.order.Order;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;


public class AciUtils {
	

	public static final String	RES_BUNDLE		= "com.aci.resources.AciMessageResource";
	
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	
	
	public static String getString(String pMessage) {
		return ResourceUtils.getBundle(RES_BUNDLE).getString(pMessage);
	}
	
	public static String getString(String pMessage, Locale pLocale) {
		return ResourceUtils.getBundle(RES_BUNDLE, pLocale).getString(pMessage);
	}
	
	public static String format(String pMessage, Object... pParams) {
		return getFormat(pMessage, null).format(pParams);
	}

	public static String format(String pMessage, Locale pLocale, Object... pParams) {
		return getFormat(pMessage, pLocale).format(pParams);
	}

	private static MessageFormat getFormat(String pKey, Locale pLocale) {
		MessageFormat mf = null;
		if (pLocale != null) {
			mf = new MessageFormat(getString(pKey, pLocale));
		} else {
			mf = new MessageFormat(getString(pKey));
		}
		return mf;
	}
	
	public static void setOrderAmountToRedRequest(Order pOrder,LPTransaction pRequest){
		if(pOrder !=null){
			if(pOrder.getPriceInfo()==null){
				pRequest.setField(FieldMappingConstants.AMT,convertAmountToRedReadableFormat((0.0),2));
			}
			else {
				pRequest.setField(FieldMappingConstants.AMT,convertAmountToRedReadableFormat(pOrder.getPriceInfo().getAmount(),2));
			}
		}
	}
	
	/**
	 * This method will convert all the amounts to RED readable format
	 * @param pAmount
	 * @return
	 */
	public static String convertAmountToRedReadableFormat(Double pAmount,int impliedDecimal){
		if(pAmount==null){
			return convertAmountToRedReadableFormat(0.0,impliedDecimal);
		}
		else {
			    Double roundingDecimal=Math.pow(10, impliedDecimal);
				Double roundedAmount=(pAmount*roundingDecimal);
				String priceAmount = Long.toString(Math.round(roundedAmount));
				return priceAmount;
			}
	}
	
	public static String convertOrderSubmittedTimeToReDReadableForm(Date pDate){
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(pDate);
	}
	
	public static String getTransactionOrganizationCode(String agentId){
		if(agentId!=null&& !agentId.trim().isEmpty()){
			return "T";
		}
		else {
			return "E";
		}
		
	}
	
	public static String truncateToMaxFieldLength(String value, int maxLength){
		String finalString=value;
		if(value!=null){
			if(finalString.length()>maxLength){
				finalString=value.substring(0,maxLength);
			}
		}
		return finalString;
	}
	
	

	
	public static boolean isAddressField(String key){
		if(key.contains("ADDR1")|| key.contains("ADDR2")){
			return true;
		}
		else {
			return false;
		}
	}
	

	
	public static boolean isRepeatableField(String fieldName){
		if(fieldName.trim().contains(FieldMappingConstants.ITEM_QUANTITYN.trim()) || fieldName.trim().contains(FieldMappingConstants.ITEM_AMTN.trim())
			||	fieldName.trim().contains(FieldMappingConstants.ITEM_CST_AMTN.trim()) ||fieldName.trim().contains(FieldMappingConstants.ITEM_DESCN.trim())
			|| fieldName.trim().contains(FieldMappingConstants.ITEM_PROD_CDN.trim()) || fieldName.trim().contains(FieldMappingConstants.ITEM_SKUN.trim())
			|| fieldName.trim().contains(FieldMappingConstants.ITEM_GIFT_MSGN.trim()) || fieldName.trim().contains(FieldMappingConstants.ITEM_SHIP_COMTS.trim())
			|| fieldName.trim().contains(FieldMappingConstants.ITEM_MAN_PART_NON.trim()) || fieldName.trim().contains(FieldMappingConstants.ITEM_SHIP_NON.trim())
		){
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
	
	/**
	 * This will break the address into multiple strings with string max string length for each to be 40 characters including spaces
	 */
	public static Map<String,String> breakAddressInfo(Map<String,String> addresses,Map<String,String> maxFieldLengthConfiguration,String addressField1,String addressField2){
		Map<String,String> addressInfo=new HashMap<String,String>();
		if((addresses!=null&&addresses.size()!=0) && (maxFieldLengthConfiguration!=null && maxFieldLengthConfiguration.size()!=0)){
			String address1=addresses.get("address1");
			Integer address1MaxLength=Integer.parseInt(maxFieldLengthConfiguration.get(addressField1));
			String address2=addresses.get("address2");
			Integer address2MaxLength=Integer.parseInt(maxFieldLengthConfiguration.get(addressField2));
			String wrappedAddress2="";
				if(address1!=null){
					if(address1.length()>address1MaxLength.intValue()) {
						String splitString="";
						String[] lineSplit=address1.split(" ");	
						if(lineSplit.length>1){
							for(String line:lineSplit){
								if((splitString.length()+line.length())>address1MaxLength.intValue()){
									wrappedAddress2=wrappedAddress2+line+" ";
								}
								else {
									splitString=splitString+line+" ";
								}
							}
							if(splitString.contains(" ")){
								splitString=splitString.substring(0,splitString.lastIndexOf(" "));
							}
							if(wrappedAddress2.length()!=0){
								if(wrappedAddress2.contains(" ")){
									wrappedAddress2=wrappedAddress2.substring(0,wrappedAddress2.lastIndexOf(" "));
								}
							}
						}
						else{
							splitString=address1.substring(0,address1MaxLength);
							wrappedAddress2=address1.substring(address1MaxLength,address1.length());
						}
						addressInfo.put("address1",splitString);
					}
					else {
						addressInfo.put("address1",address1);
					}
				}
				if(address2!=null){
					if(wrappedAddress2.length()!=0){
						address2=wrappedAddress2+" "+address2;
					}
					if(address2.length()>address2MaxLength.intValue()) {
						address2=address2.substring(0,address2MaxLength.intValue());
						
					}
					addressInfo.put("address2",address2);
				}
				else {
					if(wrappedAddress2.length()!=0){
						address2=wrappedAddress2;
						addressInfo.put("address2",address2);
					}
				}
		}
		return addressInfo;
	}
	
	/**
	 * This will mask the card number to show first 4 and last 4 digits
	 * @param ccNumber
	 * @return
	 */
	public static String maskCardNumber(String ccNumber){
		StringBuffer ccBuffer=null;
		if(ccNumber!=null){
			ccBuffer=new StringBuffer();
		for(int j =1;j<=ccNumber.length();j++){
			 if(j<=4 || j>ccNumber.length()-4){
				 ccBuffer.append(ccNumber.charAt(j-1));
			 }
			 else{	 
				 ccBuffer.append("X");
			 }
         }
		}
		return ccBuffer.toString();
		
	}
	
	/**
	 * This will convert the credit card expiration date to RED readable format
	 * @param ccExpirationYear
	 * @param ccExpirationMonth
	 * @return
	 */
	public static String formatCCExpirationDateToRedReadableForm(String ccExpirationYear, String ccExpirationMonth){
		String expirationDate="";
		if(ccExpirationMonth!=null){
			if(ccExpirationMonth.trim().length()==1){
				ccExpirationMonth="0"+ccExpirationMonth.trim();
			}
			if (ccExpirationYear != null) {
				if (ccExpirationYear.length() == 4) {
					expirationDate=expirationDate+ccExpirationMonth+ccExpirationYear.substring(2);
				} else if (ccExpirationYear.length() == 2) {
					expirationDate=expirationDate+ccExpirationMonth+ccExpirationYear;
				} 
			}
		}
		return expirationDate;
	}
	
	/**
	 * This will convert phone numbers to RED readable format
	 * @param pPhoneNumber
	 * @return
	 */
	public static String convertPhoneNumberToRedReadableForm(String pPhoneNumber){
		String phoneNumber="";
		if(pPhoneNumber!=null&& !pPhoneNumber.trim().isEmpty()){
			phoneNumber=pPhoneNumber.replace("-", "");
		}
		return phoneNumber;
	}
	

	
	public static String obfuscatedCreditCardNumber(String creditCardNumber){
		String obfuscatedNumber="";
		if(creditCardNumber!=null&&!creditCardNumber.trim().equalsIgnoreCase("")){
			int creditCardNumberLength=creditCardNumber.length();
			String firstSixLetters="";
			String lastFourLetters="";
			String middleNumbers="";
			if(creditCardNumberLength>=14){
				firstSixLetters=creditCardNumber.substring(0,6);
			}
			if(creditCardNumberLength>=4){
				lastFourLetters=creditCardNumber.substring(creditCardNumberLength-4);
			}
			if(creditCardNumberLength>=4){
				int lengthOfFirstSixLetters=firstSixLetters.length();
				int lengthofLastFourLetters=lastFourLetters.length();
				int totalObfuscatedLength=lengthOfFirstSixLetters+lengthofLastFourLetters;
				if(totalObfuscatedLength<creditCardNumberLength){
					int paddedLength=creditCardNumberLength-totalObfuscatedLength;
					for(int i=0;i<paddedLength;i++){
						middleNumbers=middleNumbers+"X";
					}
				}
			}
			else {
				lastFourLetters=creditCardNumber;
			}
			obfuscatedNumber=firstSixLetters+middleNumbers+lastFourLetters;
		}
		return obfuscatedNumber;
	}

	public static String convertPostalCodeToRedReadableForm(String pPostalCode) {
		
		String postalCode = "";
		
		if (!StringUtils.isEmpty(pPostalCode)) {
			String[] postalCodeArray = pPostalCode.split("-");
			
			if (postalCodeArray.length>0){
				postalCode = postalCodeArray[0];
			}
		}
		return postalCode;
	}
	
	public static boolean isIpv4(String ipAddress){
		Pattern VALID_IPV4_PATTERN= Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
		Matcher m1= VALID_IPV4_PATTERN.matcher(ipAddress);
		return m1.matches();
	}
	
	public static String checkForNullOrEmpty(String value){
		if(value==null){
			value="";
		}
		else if(value.isEmpty()){
			value="";
		}
		return value.trim();
	}

}
