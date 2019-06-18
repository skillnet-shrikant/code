package com.aci.constants;

public class FieldMappingConstants extends AciConstants {
	
	/*Request Fields*/
	
	// Configuration fields
	public static String DIV_NUM="DIV_NUM"; // API Merchant ID
	public static String S_KEY_ID="S_KEY_ID"; // API SSL Key ID
	public static String EBT_NAME="EBT_Name"; // Client ID
	public static String EBT_SERVICE="EBT_Service"; // ReDShieldServiceID
	
	//Transaction fields
	public static String ACT_CD="ACT_CD"; // Transaction Action
	public static String REQ_TYPE_CD="REQ_TYPE_CD"; // Transaction Request Type
	public static String ORD_ID="ORD_ID"; // Transaction ID -- Unique for each transaction
	public static String ORD_DTM="ORD_DTM"; // Transaction Date and Time
	public static String ORD_TZ="ORD_TZ"; // Transaction Time Zone
	public static String AMT="AMT"; // Full Authorization Amount
	public static String CURR_CD="CURR_CD"; // Currency Code
	public static String MOP_TYPE_CD="MOP_TYPE_CD"; // Method of Payment
	public static String ACCT_NUM="ACCT_NUM"; // Card Number
	public static String CARD_EXP_DT="CARD_EXP_DT" ; // Card Expiration Date
	public static final String FRT_AMT="FRT_AMT"; // Freight amount
	public static final String SLS_TAX_AMT="SLS_TAX_AMT"; // Sales tax amount
	
	
	// Payment Processinng Fields
	public static String CARD_SEC_CD="CARD_SEC_CD"; //Card Security Code;
	public static String CARD_SEC_IND_CD="CARD_SEC_IND_CD"; // CVV Code presence indicator
	public static String PROD_DEL_CD="PROD_DEL_CD"; // Product Delivery Code
	public static String AUTOSTANDALONE_TOKEN="AUTOSTANDALONE_TOKEN";//Tokenize credit card flag
	
	//Customer -- Billing Data Fields
	public static String CUST_TYPE_CD="CUST_TYPE_CD"; // Customer Details Present Flag
	public static String CUST_ID="CUST_ID"; // Customer ID / Account Number / Email
	public static String CUST_FNAME="CUST_FNAME"; // Customer First Name
	public static String CUST_MNAME="CUST_MNAME"; // Customer Middle Name
	public static String CUST_LNAME="CUST_LNAME"; // Customer Last Name
	public static String CUST_ADDR1="CUST_ADDR1"; // Customer Address 1
	public static String CUST_ADDR2="CUST_ADDR2"; // Customer Address 2
	public static String CUST_ADDR3="CUST_ADDR3"; // Customer Address 3
	public static String CUST_CITY="CUST_CITY"; // Customer City
	public static String CUST_STPR_CD="CUST_STPR_CD"; // Customer State
	public static String CUST_POSTAL_CD="CUST_POSTAL_CD"; // Customer Postal Code
	public static String CUST_CNTRY_CD="CUST_CNTRY_CD"; // Customer Country
	public static String CUST_HOME_PHONE="CUST_HOME_PHONE"; // Customer Home Phone
	public static String CUST_WORK_PHONE="CUST_WORK_PHONE"; // Customer Work Phone
	public static String CUST_EMAIL="CUST_EMAIL"; // Customer Email
	public static String CUST_IP_ADDR="CUST_IP_ADDR"; // Customer Ip Address
	public static String CUST_BIRTH_DT="CUST_BIRTH_DT"; // Customer Date of Birth
	
	//Customer -- Extended Data Fields
	public static String EBT_PREVCUST="EBT_PREVCUST"; // Previous Customer Flag
	public static String EBT_TOF="EBT_TOF"; // Time on file for the card number
	
	//Customer -- Shipping Data Fields
	public static String SHIP_TYPE_CD="SHIP_TYPE_CD"; // Shipping Details Present Flag
	public static String SHIP_FNAME="SHIP_FNAME"; // Shipping First Name
	public static String SHIP_MNAME="SHIP_MNAME"; // Shipping Middle Initial
	public static String SHIP_LNAME="SHIP_LNAME"; // Shipping Last Name
	public static String SHIP_ADDR1="SHIP_ADDR1"; // Shipping Address 1
	public static String SHIP_ADDR2="SHIP_ADDR2"; // Shipping Address 2
	public static String SHIP_CITY="SHIP_CITY"; // Shipping City
	public static String SHIP_STPR_CD="SHIP_STPR_CD"; // Shipping State Code
	public static String SHIP_POSTAL_CD="SHIP_POSTAL_CD"; // Shipping Postal Code
	public static String SHIP_CNTRY_CD="SHIP_CNTRY_CD"; // Shipping Country
	public static String SHIP_HOME_PHONE="SHIP_HOME_PHONE"; // Shipping Home Phone
	public static String SHIP_FAX_PHONE="SHIP_FAX_PHONE"; // Shipping Fax Phone
	public static String SHIP_EMAIL="SHIP_EMAIL"; // Shipping Email
	public static String ebSHIPCOMMENTS="ebSHIPCOMMENTS"; // Shipping Comments SHIP_ADDR1|SHIP_ADDR2|SHIP_POSTAL_CD
	public static String SHIP_MTHD_CD="SHIP_MTHD_CD"; // Shipping Method used
	public static String EBT_SHIPNO="EBT_SHIPNO"; // Shipping Id
	
	// Gift Message and Gift Wrapping
	public static final String GIFT_MESSAGE="ebGIFTMESSAGE";
	public static final String GIFT_WRAP="EBT_WRAPPED";
	
	//Additional Data Fields
	public static String ebWEBSITE="ebWEBSITE"; // Transaction Source Website URL
	
	// Device Fingerprint
	public static String EBT_DEVICEPRINT="EBT_DEVICEPRINT"; // Device ID collected from Iovation Blackbox
	
	/*Repeatable Data fields*/
	
	//Item Data Fields
	public static String OI_REPEAT="OI_REPEAT"; // Number of Line Items
	public static String ITEM_QUANTITYN="ITEM_QTY"; // Number of quantity for each corresponding line item
	public static String ITEM_SKUN="ITEM_COMM_CD"; // Item SKU number for each corresponding line item
	public static String ITEM_PROD_CDN="ITEM_PROD_CD"; // Item Product Code for each corresponding line item
	public static String ITEM_MAN_PART_NON="ITEM_MAN_PART_NO"; // Item Manufacture Code for each corresponding line item
	public static String ITEM_DESCN="ITEM_DESC"; // Item Description for each corresponding line item
	public static String ITEM_CST_AMTN="ITEM_CST_AMT"; // Item Unit Price for each corresponding line item
	public static String ITEM_AMTN="ITEM_AMT"; // Item Unit price for each corresponding line item
	public static String ITEM_SHIP_NON="ITEM_SHIP_NO"; // Item Shipping Tracking Number
	public static String ITEM_SHIP_MTHDN="ITEM_SHIP_MTHD"; // Item Shipping Method
	public static String ITEM_GIFT_MSGN="ITEM_GIFT_MSG"; // Item Gift message
	public static String ITEM_SHIP_COMTS="ITEM_SHIP_COMMENTS"; // Item Shipping Comments
	
	//Recipent Data Fields
	public static final String RECIP_FNAMEN="RECIP_FNAME"; // Each recipient First Name
	public static final String RECIP_LNAMEN="RECIP_LNAME"; // Each Recipient Last Name
	public static final String RECIP_MNAMEN="RECIP_MNAME"; // Each Recipient Middle Initial
	public static final String RECIP_EMAILN="RECIP_EMAIL"; // Each Recipient Email
	public static final String RECIP_PHONEN="RECIP_PHONE"; // Each Recipient Phone
	public static final String RECIP_ADDR1N="RECIP_ADDR1"; // Each Recipient Address1
	public static final String RECIP_ADDR2N="RECIP_ADDR2"; // Each Recipient Address2
	public static final String RECIP_CITYN="RECIP_CITY"; // Each Recipient City;
	public static final String RECIP_STPR_CDN="RECIP_STPR_CD"; // Each Recipient State
	public static final String RECIP_CNTRY_CDN="RECIP_CNTRY_CD"; // Each Recipient Country Code
	public static final String RECIP_POSTAL_CDN="RECIP_POSTAL_CD"; // Each Recipient Postal Code
	
	//Custom Data Fields
	public static final String CUSTOM_BILLING_INFO="EBT_USER_DATA1"; // Custom data field to capture billing information BilingAddress|Zipcode
	public static final String CUSTOM_PAYMENT_TYPE="EBT_USER_DATA11";
	public static final String CUSTOM_PAYMENT_TYPE_LIST="EBT_USER_DATA12";
	public static final String CUSTOM_PAYMENT_TYPE_AMT_LIST="EBT_USER_DATA13";
	public static final String CUSTOM_ATG_ORDER_ID="EBT_USER_DATA14";
	
	/* Linked Transaction Request fields */
	public static final String ORIG_REQ_ID="ORIG_REQ_ID"; // Original Request ID
	public static final String ORIG_DATE_FIELD="ORIG_RSP_DT"; // Original Date
	public static final String ORIG_TIME_FIELD="ORIG_RSP_TM"; // Original Time
	
	/*Response Fields */
	
	//Transaction Response Fields//
	public static String REQ_ID="REQ_ID"; // Unique ReD Request ID
	public static String STAT_CD="STAT_CD"; // Transaction Status code
	
	//Payment Response Fields
	public static String RSP_AUTH_NUM="RSP_AUTH_NUM"; // Authorization Number
	public static String RSP_CD="RSP_CD"; // Acquirer Response Code
	public static String RSP_DT="RSP_DT"; // Response Date
	public static String RSP_TM="RSP_TM"; // Response Time
	public static String RSP_MSG="RSP_MSG"; // Response Message
	public static String RSP_AVS_CD="RSP_AVS_CD"; // AVS Response Code 
	public static String RSP_SEC_CD="RSP_SEC_CD"; // CVV Response Code
	public static String TOKEN_ID="TOKEN_ID"; // Token ID
	
	//Fraud Screening Response Fields
	public static String FRAUD_STAT_CD="FRAUD_STAT_CD"; // ReD Shield Fraud Status Code
	public static String FRAUD_RSP_CD="FRAUD_RSP_CD"; // ReD Shield Fraud Response Code
	public static String FRAUD_RSP_DESC="FRAUD_RSP_DESC"; // ReD Shield Response Description
	public static String FRAUD_REC_ID="FRAUD_REC_ID"; // ReD Shield Transaction ID
	public static String FRAUD_NEURAL="FRAUD_NEURAL"; // ReD Shield Neural Score
	public static String FRAUD_RCF="FRAUD_RCF"; // ReD Shield Rule Category Flag
	public static final String FRAUD_USE_CD="FRAUD_USE_CD";
	
	//Real Time Rules Response Fields
	public static String RE_STAT_CD="RE_STAT_CD"; // RTR Status Code
	public static String RE_RSP_CD="RE_RSP_CD"; // RTR Response Code
	public static String RE_RULE_ID="RE_RULE_ID"; // RTR Rule ID
	public static String RE_MSG="RE_MSG"; // RTR Rule Description
	
	//JOURNAL KEY
  public static String ORGINAL_J_KEY="ORIGINAL_JOURNAL_KEY"; // ORIGINAL_JOURNAL_KEY
  public static String PBG_SWITCH_KEY="PBG_SWITCH_KEY"; // PBG_SWITCH_KEY	
  
	
	
	
	

	
	
	
	

}
