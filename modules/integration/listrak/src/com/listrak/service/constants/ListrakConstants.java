package com.listrak.service.constants;

import java.util.Locale;
import java.util.ResourceBundle;

import com.listrak.service.constants.ListrakConstants;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

public final class ListrakConstants {
	
	/* Resource Bundle name*/
	private static final String EXTN_RESOURCE_BUNDLE_NAME = "com.listrak.service.constants.Resources";
	
	public static ResourceBundle getResources() {
		return getResources(ServletUtil.getCurrentRequest());
	}

	
	public static ResourceBundle getResources(DynamoHttpServletRequest pRequest) {
	    RequestLocale requestLocale = null;
	    if (pRequest != null) {
	    	requestLocale = pRequest.getRequestLocale();
	    }
	    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
	    return getResources(currentLocale);
	}
	
	public static ResourceBundle getResources(Locale pLocale) {
		return ResourceBundle.getBundle(EXTN_RESOURCE_BUNDLE_NAME, pLocale);
	}

	
	// Email Constants
	public static String ORDER_NUMBER="orderNumber";
	public static String STORE_NAME="storeName";
	public static String STORE_WEBSITE="storeWebsite";
	public static String BILLING_FIRST_NAME="billingFirstName";
	public static String BILLING_LAST_NAME="billingLastName";
	public static String EMAIL="email";
	public static String PURCHASE_DATE="purchaseDate";
	public static String SHIPPING_METHOD="shippingMethod";
	public static String CART_ITEMS="cartItems";
	public static String SUB_TOTAL="subTotal";
	public static String SHIPPING_COST="shippingCost";
	public static String TAX="tax";
	public static String GRAND_TOTAL="grandTotal";
	public static String ALT_FIRST_NAME="alternateFirstName";
	public static String ALT_LAST_NAME="alternateLastName";
	public static String ALT_PICKUP_EMAIL="alternatePickupEmail";
	public static String BILLING_STREET_ADD1="billingStreetAddress1";
	public static String BILLING_STREET_ADD2="billingStreetAddress2";
	public static String BILLING_STREET_CITY="billingCity";
	public static String BILLING_STREET_STATE="billingState";
	public static String BILLING_STREET_ZIP="billingZip";
	public static String BILLING_STREET_COUNTRY="billingCountry";
	public static String SHIPPING_STREET_FNAME="shippingFirstName";
	public static String SHIPPING_STREET_LNAME="shippingLastName";
	public static String SHIPPING_STREET_ADD1="shippingStreetAddress1";
	public static String SHIPPING_STREET_ADD2="shippingStreetAddress2";
	public static String SHIPPING_STREET_CITY="shippingCity";
	public static String SHIPPING_STREET_STATE="shippingState";
	public static String SHIPPING_STREET_ZIP="shippingZip";
	public static String SHIPPING_STREET_COUNTRY="shippingCountry";
	public static String PICKUP_DATE="pickupDate";
	public static String STORE_STREET_ADD1="storeStreetAdress1";
	public static String STORE__STREET_CITY="storeCity";
	public static String STORE_STREET_STATE="storeState";
	public static String STORE_STREET_ZIP="storeZip";
	public static String PAYMENT_METHOD="paymentMethod";
	public static String TRACKING_NUMBER="trackingNumber";
	public static String BILLING_PHONE="billingPhone";
	public static String CUSTOMER_DETAILS="customerDetails";

	
	// EmailTypeMessageConstants
	public static String WEB_ORDER_CONFIRM_EMAIL="webOrderConfirmationEmail";
	public static String BOPIS_ORDER_CONFIRM_EMAIL="bopisOrderConfirmationEmail";
	public static String BOPIS_ORDER_CONFIRM_ALTEMAIL="bopisOrderConfirmationAltEmail";
	public static String BOPIS_ORDER_PICKUP_CONFIRM_EMAIL="bopisOrderPickupConfirmedEmail";
	public static String BOPIS_READY_TO_PICKUP_CS_EMAIL="bopisReadyToPickupCSEmail";
	public static String BOPIS_READY_PICKUP_CS_ALTEMAIL="bopisReadyToPickupCSAltEmail";
	public static String BOPIS_READY_PICKUP_OUT_EMAIL="bopisReadyToPickupOutEmail";
	public static String BOPIS_READY_PICKUP_OUT_ALTEMAIL="bopisReadyToPickupOutAltEmail";
	
	public static String BOPIS_PICKUP_REM_EMAIL="bopisPickupReadyReminderEmail";
	public static String BOPIS_PICKUP_REM_ALTEMAIL="bopisPickupReadyReminderAltEmail";
	
	public static String BOPIS_PICKUP_REM2_EMAIL="bopisPickupReadyReminder2Email";
	public static String BOPIS_PICKUP_REM2_ALTEMAIL="bopisPickupReadyReminder2AltEmail";
	
	public static String BOPIS_PICKUP_YARD_REM_EMAIL="bopisPickupReadyYardReminderEmail";
	public static String BOPIS_PICKUP_YARD_REM_ALTEMAIL="bopisPickupReadyYardReminderAltEmail";
	public static String BOPIS_PICKUP_YARD_REM2_EMAIL="bopisPickupReadyYardReminder2Email";
	public static String BOPIS_PICKUP_YARD_REM2_ALTEMAIL="bopisPickupReadyYardReminder2AltEmail";	
	
	public static String ORDER_SHIPPED_EMAIL="orderShippedEmail";

	public static String BOPIS_READY_TO_PICKUP_FAC_EMAIL="bopisReadyToPickupFACEmail";
	public static String BOPIS_READY_PICKUP_FAC_ALTEMAIL="bopisReadyToPickupFACAltEmail";
	public static String BOPIS_PICKUP_FAC_REM_EMAIL="bopisPickupFacReadyReminderEmail";
	public static String BOPIS_PICKUP_FAC_REM_ALTEMAIL="bopisPickupFacReadyReminderAltEmail";
	public static String BOPIS_PICKUP_FAC_REM2_EMAIL="bopisPickupFacReadyReminder2Email";
	public static String BOPIS_PICKUP_FAC_REM2_ALTEMAIL="bopisPickupFacReadyReminder2AltEmail";	
	//Request HEaders Param Name
	public static String AUTHORIZATION="Authorization";
	
	// Order Repository Constants
	public static String ORDER_NUMBER_PROPERTY_NAME="orderNumber";
	public static String CONTACT_EMAIL_PROPERTY_NAME="contactEmail";
	public static String SUBMITTED_DATE="submittedDate";
	public static String GIFT_CARD_NUMBER="cardNumber";
	public static String BOPIS_STORE="bopisStore";
	public static String BOPIS_STORE_ITEM_DESCRIPTOR="store";
	public static String BOPIS_ORDER="bopisOrder";
	public static String BOPIS_PERSON="bopisPerson";
	public static String BOPIS_EMAIL="bopisEmail";
	public static String BOPIS_READY_FOR_PICKUP_DATE="bopisReadyForPickupDate";
	public static String BOPIS_REMINDER_SENT_DATE="bopisReminderSentDate";
	public static String BOPIS_STORE_WEBSITE="website";
	public static String ITEM_SHIPPING="shipping";
	public static String ITEM_SHIPPING_TAX="shippingTax";
	public static String ITEM_SHIPPING_CITY_TAX="shippingCityTax";
	public static String ITEM_SHIPPING_COUNTY_TAX="shippingCountyTax";
	public static String ITEM_SHIPPING_STATE_TAX="shippingStateTax";
	public static String ITEM_SHIPPING_DISTRICT_TAX="shippingDistrictTax";
	public static String ITEM_SHIPPING_COUNTRY_TAX="shippingCountryTax";
	public static String ITEM_SHIPPING_DISCOUNT="shippingDiscount";
	public static String ITEM_TAX_PRICE_INFO="taxPriceInfo";
	public static String TAX_AMOUNT="amount";
	
	//LocationRepository Propety Names
	public static String BOPIS_STORE_NAME="name";
	public static String BOPIS_STORE_ADDRESS1="address1";
	public static String BOPIS_STORE_CITY="city";
	public static String BOPIS_STORE_STATE="stateAddress";
	public static String BOPIS_STORE_ZIP="postalCode";
	
	//Catalog Constants
	public static String PRODUCT_TITLE_PROPERTY_NAME="description";
	public static String PRODUCT_SEODES_PROPERTY_NAME="seoDescription";
	public static String PRODUCT_DISPLAYNAME_PROPERTY_NAME="displayName";
	
	//Other Constants
	public static String CART_ITEMS_HEADER_RESOURCE_NAME="cartItemsHeader";
	public static String CART_ITEMS_FOOTER_RESOURCE_NAME="cartItemsFooter";
	public static String STANDARD_SKU_DETAILS_RESOURCE_NAME="standardSkuDetails";
	public static String BOPIS_STORE_WEBSITE_LINK="storeWebsiteLink";
	public static int BOPIS_PICKUP_INI=0;
	public static int BOPIS_PICKUP_REM_1=1;
	public static int BOPIS_PICKUP_REM_2=2;
	public static int BOPIS__PICKUP_CONF=3;
	public static String FEDEX_TRACKING_LINK="fedexTrackingLink";
	public static String FEDEX_TRACKING_HEADER="fedexTrackingLinkHeader";
	public static String FEDEX_TRACKING_FOOTER="fedexTrackingLinkFooter";
	public static String BOPIS_CUSTOMER_DETAIL_HEADER="customerDetailHeader";
	public static String BOPIS_CUSTOMER_DETAIL_ONUM="customerDetailONum";
	public static String BOPIS_CUSTOMER_DETAIL_PNAME="customerDetailPName";
	public static String BOPIS_CUSTOMER_DETAIL_PEMAIL="customerDetailPEmail";
	public static String BOPIS_CUSTOMER_DETAIL_ALTNAME="customerDetailAltName";
	public static String BOPIS_CUSTOMER_DETAIL_ALTEmail="customerDetailAltEmail";
	public static String BOPIS_CUSTOMER_DETAIL_FOOTER="customerDetailFooter";
	
	
	//Exception Constants
	public static String ORDER_NULL_EXCEPTION="orderNull";
	public static String ORDER_NUMBER_NULL_EXCEPTION="orderNumberNull";
	public static String EMAIL_EMPTY_EXCEPTION="emailEmpty";
	public static String PURCHASEDATE_NULL_EXCEPTION="purchaseDateNull";
	public static String BILLINGADDRESS_NULL_EXCEPTION="billingAddressNull";
	

}
