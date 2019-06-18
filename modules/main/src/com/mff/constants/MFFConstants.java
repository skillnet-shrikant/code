package com.mff.constants;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.mff.util.MFFUtils;

/**
 * Constants for KP
 */
public final class MFFConstants {

	/* Resource Bundle name*/
	private static final String EXTN_RESOURCE_BUNDLE_NAME = "com.mff.constants.Resources";

	public static ResourceBundle getEXTNResources() {
		return getEXTNResources(ServletUtil.getCurrentRequest());
	}

	public static ResourceBundle getEXTNResources(DynamoHttpServletRequest pRequest) {
	    RequestLocale requestLocale = null;
	    if (pRequest != null) {
	    	requestLocale = pRequest.getRequestLocale();
	    }
	    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
	    return getEXTNResources(currentLocale);
	}

	public static String getEXTNResourcesMessage(DynamoHttpServletRequest pRequest, String pKey) {
	    RequestLocale requestLocale = null;
	    if (pRequest != null) {
	    	requestLocale = pRequest.getRequestLocale();
	    }
	    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
	    ResourceBundle resourceBundle = getEXTNResources(currentLocale);
	    String message = "Missing Message in ResourceBundle For Key:" + pKey;
	    try{
	      message = MFFUtils.splitMessageFromKey(resourceBundle.getString(pKey));
	    } catch (MissingResourceException mre) {
	    	mre.printStackTrace();
	    }
	    return message;
	}

	public static ResourceBundle getEXTNResources(Locale pLocale) {
		return ResourceBundle.getBundle(EXTN_RESOURCE_BUNDLE_NAME, pLocale);
	}

	/*Forgot Password */
	public static final String MSG_ERROR_PROFILE_NOTFOUND = "noProfileFoundError";

	/* Product Constants*/
	public static final String PRODUCT_ITEM_DESCRIPTOR="product";
	public static final String PROPERTY_PRODUCT_NUM_IMAGES="numImages";

	/* General Constants */
	public static final String SOURCE_IP_ADDRESS_HEADER_NAME = "True-Client-IP";
	public static final String GUEST_SUFFIX = "_guest";

	// Order repository
	public static final String PROPERTY_SHIPPING_ITEMS_TAX_PRICES_INFOS = "shippingItemsTaxPriceInfos";
	public static final String SHIPPING = "shipping";
	public static final String SHIPPING_DISCOUNT = "shippingDiscount";
	public static final String SHIPPING_TAX = "shippingTax";
	public static final String SHIPPING_CITY_TAX = "shippingCityTax";
	public static final String SHIPPING_COUNTY_TAX = "shippingCountyTax";
	public static final String SHIPPING_STATE_TAX = "shippingStateTax";
	public static final String SHIPPING_DISTRICT_TAX = "shippingDistrictTax";
	public static final String SHIPPING_COUNTRY_TAX = "shippingCountryTax";
	public static final String FULFILLMENT_STORE = "fulfillmentStore";
	public static final String PROPERTY_SATURDAY_DELIVERY = "isSaturdayDelivery";
	public static final String PROPERTY_SHIP_DATE = "shipDate";
	public static final String PROPERTY_RETURN_DATE = "returnDate";
	public static final String PROPERTY_CANCEL_DATE = "cancelDate";
	public static final String PREVIOUS_ALLOCATION = "previousAllocation";
	public static final String PROPERTY_TRACKING_NUMBER = "trackingNumber";
	public static final String PROPERTY_CANCEL_DESCRIPTION = "cancelDescription";
  public static final String PROPERTY_GC_NUMBER = "giftCardNumber"; // commerce item property
  public static final String PROPERTY_MINIMUM_AGE = "minimumAge";
  public static final String PROPERTY_FFL = "ffl";
  public static final String PROPERTY_DROP_SHIP = "dropShip";
  public static final String PROPERTY_GIFTCARD = "giftCard";
  public static final String PROPERTY_GWP = "gwp";
  public static final String PROPERTY_GIFTCARD_DENOMINATION = "giftCardDenomination";
  public static final String PROPERTY_RETURN_ITEM_IDS = "returnItemIds";
  public static final String PROPERTY_ACTIVATION_ATTEMPTS = "activationAttempts";
  public static final String PROPERTY_REJECTION_REASON_CODES = "rejectionReasonCodes";
  public static final String PROPERTY_BV_REVIEWS="bvReviews";

  public static final String ITEM_DESC_PRORATE_ITEM = "prorateItem";
  public static final String PROPERTY_PRORATE_LINE_NUMBER = "lineNumber";
  public static final String PROPERTY_PRORATE_COMMERCE_ITEM_ID = "commerceItemId";
  public static final String PROPERTY_PRORATE_ORDER_ID = "orderId";
  public static final String PROPERTY_PRORATE_UNIT_PRICE = "prorateUnitPrice";
  public static final String PROPERTY_PRORATE_LIST_PRICE = "listPrice";
  public static final String PROPERTY_PRORATE_SALE_PRICE = "salePrice";
  public static final String PROPERTY_PRORATE_SHIPPING = "prorateShipping";
  public static final String PROPERTY_PRORATE_SHIPPING_TAX = "prorateShippingTax";
  public static final String PROPERTY_PRORATE_SHIPPING_CITY_TAX = "prorateShippingCityTax";
  public static final String PROPERTY_PRORATE_SHIPPING_COUNTY_TAX = "prorateShippingCountyTax";
  public static final String PROPERTY_PRORATE_SHIPPING_STATE_TAX = "prorateShippingStateTax";
  public static final String PROPERTY_PRORATE_SHIPPING_DISTRICT_TAX = "prorateShippingDistrictTax";
  public static final String PROPERTY_PRORATE_SHIPPING_COUNTRY_TAX = "prorateShippingCountryTax";
  public static final String PROPERTY_PRORATE_TAX = "prorateTax";
  public static final String PROPERTY_PRORATE_ITEM_CITY_TAX = "prorateItemCityTax";
  public static final String PROPERTY_PRORATE_ITEM_COUNTY_TAX = "prorateItemCountyTax";
  public static final String PROPERTY_PRORATE_ITEM_STATE_TAX = "prorateItemStateTax";
  public static final String PROPERTY_PRORATE_ITEM_DISTRICT_TAX = "prorateItemDistrictTax";
  public static final String PROPERTY_PRORATE_ITEM_COUNTRY_TAX = "prorateItemCountryTax";
  public static final String PROPERTY_PRORATE_ORDER_DISCOUNT_SHARE = "prorateOrderDiscountShare";
  public static final String PROPERTY_PRORATE_DISCOUNT_PRICE = "proratedDiscountPrice";
  public static final String PROPERTY_PRORATE_GWP_AMOUNT = "proratedGwpAmount";
  public static final String PROPERTY_PRORATE_TOTAL = "prorateTotal";
  public static final String PROPERTY_PRORATE_TOTAL_WITHOUT_SHIPPING = "prorateTotalWithoutShipping";
  public static final String PROPERTY_PRORATE_STATE = "state";
  public static final String PROPERTY_PRORATE_QUANTITY = "quantity";
  public static final String PROPERTY_PRORATE_CREATION_DATE = "creationDate";
  public static final String PROPERTY_PRORATE_RETURN_DATE = "returnDate";
  public static final String PRORATE_STATE_INITIAL = "initial";
  public static final String PRORATE_STATE_RETURN = "return";
  
  public static final String ITEM_DESC_AGENT_ACTION_LOG = "agentActionLog";
  public static final String PROPERTY_ACTION_LOG_AGENT_ID = "agentId";
  public static final String PROPERTY_ACTION_LOG_ORDER_NUMBER = "orderNumber";
  public static final String PROPERTY_ACTION_LOG_ACTION = "action";

  public static final String REASON_CODE_SHIPPED = "shipped";
  public static final String REASON_CODE_READY_FOR_PICKUP  = "readyForPickUp";
  public static final String REASON_CODE_DAMAGED  = "damaged";
  public static final String REASON_CODE_NO_STOCK  = "noStock";
  public static final String REASON_CODE_OTHER_SHOP_WORN  = "otherShopWorn";
  public static final String REASON_CODE_OTHER_BOX_CRUSHED  = "otherBoxCrushed";
  public static final String REASON_CODE_OTHER_DISPLAY  = "otherDisplay";

	public static final String UTF_8 = "UTF-8";
	public static final String HYPHEN = "-";
	public static final String QUESTION_MARK = "?";
	public static final String QUESTION_MARK_ENCODED = "%3F";
	public static final String QUESTION_MARK_ALIAS = "-QM";
	public static final String SINGLE_SPACE = " ";
	public static final String EMPTY_STRING = "";
	public static final String HTTP = "http";
	public static final String HTTP_X_REQUESTED_WITH = "X-REQUESTED-WITH";
	public static final String STATICPAGE = "staticPage";
	public static final String PAGE_NAME = "pageName";
	public static final String JSESSIONID = "JSESSIONID";
	public static final String AMOUNT = "amount";
	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String ATG_PROFILE = "/atg/userprofiling/Profile";
	public static final String PROP_PROFILE_ID = "profileId";
	public static final String ORDER_ID = "orderId";
	public static final String SUBMITTED_DATE = "submittedDate";
	public static final String ORDER_TOTAL = "orderTotal";
	public static final String ORDER_STATUS = "orderStatus";
	public static final String ORDER = "order";
	public static final String ORDERS = "orders";
	public static final String PROPERTY_ORDER_BOPIS_ORDER = "bopisOrder";
  public static final String PROPERTY_ORDER_BOPIS_STORE = "bopisStore";
  public static final String PROPERTY_ORDER_BOPIS_SIGNATURE = "bopisSignature";
  public static final String PROPERTY_ORDER_BOPIS_PERSON="bopisPerson";
  public static final String PROPERTY_ORDER_BOPIS_EMAIL="bopisEmail";
  public static final String PROPERTY_ORDER_CONTACT_EMAIL="contactEmail";
  public static final String PROPERTY_ORDER_FFL_ORDER = "fflOrder";
  public static final String PROPERTY_ORDER_FFL_DEALER_ID = "fflDealerId";
  public static final String PROPERTY_ORDER_BOPIS_REMINDER_SENT_DATE = "bopisReminderSentDate";
  public static final String PROPERTY_ORDER_BOPIS_SECOND_REMINDER_SENT_DATE = "bopisSecondReminderSentDate";
  public static final String PROPERTY_ORDER_BOPIS_READY_FOR_PICKUP_DATE = "bopisReadyForPickupDate";
  public static final String PROPERTY_COUPON_CODES = "couponCodes";
  public static final String PROPERTY_ORDER_RETURNABLE_PPS = "returnablePPS";
  public static final String PROPERTY_DATE_OF_BIRTH = "dateOfBirth";
  
	public static final String EMPTY = "empty";
	public static final String OUTPUT = "output";
	public static final String OUTPUT_START = "outputStart";

	public static final int ZERO = 0;
	public static final int ONE = 1;

	public static final String MSG_TRACK_ORDER_ERR_MSG = "track.order.error.msg";
	public static final String MSG_INVALID_ORDER_NUMBER = "track.order.orderNumber";

	public static final String FREE_SHIPPING = "freeShipping";
	
	public static final String SKU_LENGTH = "skuLength";
	public static final String WIDTH = "width";
	public static final String WEIGHT = "weight";
	public static final String SKU_DEPTH = "skuDepth";
	// This is 3:30PM
	public static final String SHIPPING_CUT_OFF_TIME = "15:30";

	public static final String STANDARD = "Standard";
	public static final String SECOND_DAY = "Second Day";
	public static final String OVER_NIGHT = "Overnight";
	public static final String LTL_TRUCK = "LTL-Truck";
	public static final String BOPIS = "Bopis";

	//Cart
	public static final String MSG_PRODUCT_MISSING="cart.productMissing";
	public static final String MSG_SKU_MISSING="cart.skuMissing";
	public static final String MSG_QTY_NOT_AVAILABLE="cart.quantityNotAvailable";
	public static final String MSG_OVER_QTY_LIMIT="cart.overQtyLimit";
	public static final String MSG_ORDER_NOT_SHIPPABLE="cart.exceedsPerSkuThresholds";
	public static final String MSG_ADD_QTY_CART_NOT_AVAILABLE="cart.cartQuantityNotAvailable";
	public static final String MSG_QTY_NOT_AVAILABLE_FOR_PROD="cart.quantityNotAvailableProduct";
	public static final String MSG_PRODUCT_NOT_AVAILABLE="cart.skuNotAvailable";
	public static final String MSG_SKU_ID_NOT_AVAILABLE="cart.skuIdNotAvailable";
	public static final String MSG_PRODUCT_INSTORE_ONLY="cart.productInStoreOnly";
	public static final String MSG_PRODUCT_BOPIS_ONLY="cart.productBopisOnly";
	public static final String MSG_FFL_BOPIS_ERROR="cart.fflBopisError";
	public static final String MSG_GC_BOPIS_ERROR="cart.gcBopisError";
	public static final String PRODUCT_NO_LONGER_AVAILABLE = "product.availabilityMessage.noLongerAvailable";
	public static final String PRODUCT_OUT_OF_STOCK = "product.availabilityMessage.outOfStock";
	public static final String MSG_STORE_POSTAL_CODE_MISSING="product.store.zipcodeMissing";
	public static final String MSG_TAX_EXEMPTION_ERROR="cart.taxExemptionError";
	public static final String MSG_QTY_LMT_REACHED = "cart.maxQuantityreached";
	public static final String MSG_ITEM_COUNT_LMT_REACHED="cart.maxItemCountPerOrderReached";
	public static final String MSG_REDUCED_MESSAGE = "cart.reducedQuantity";
	public static final String MSG_REMOVED_MESSAGE = "cart.itemRemoved";
	public static final String MSG_NOT_AVAILABLE_MESSAGE = "cart.itemNotavailable";
	public static final String MSG_CART_MERGED = "cart.merged";
	public static final String MSG_REDUCED_ORDER = "order.confirm.quantity.changed";
	public static final String MSG_REMOVED_ORDER = "order.confirm.item.removed";
	public static final String MSG_MODIFIED_ORDER_QTY = "order.confirm.quantity.adjusted";
	public static final String MSG_QTY_MISSING="cart.qtyMissing";
	public static final String MSG_ADD_QTY_ERROR="cart.addQuantityError";
	public static final String MSG_COOKIE_DISABLED="cart.cookieDisabled";
	public static final String MSG_CART_NO_ITEMS="cart.noItemsInTheCart";
	public static final String MSG_INVALID_ZIP_CODE="bopis.invalidZipCode";
	public static final String MSG_NO_STORES_FOUND="bopis.noStoresFound";
	public static final String MSG_BOPIS_NO_STORE="bopisStoreNotSelected";
	public static final String MSG_NO_ORDER_TO_MODIFY="noOrderToModify";
	public static final String MSG_BOPIS_ORDER_UPDATE_ERROR="unableToUpdateBopisInfo";
	public static final String MSG_GC_MAXDENOMINATION_ERROR="cart.giftCardMaxDenominationError";
	public static final String MSG_GC_MAXVALUE_ERROR="cart.giftCardMaxValueError";
	public static final String MSG_GC_INVALID_DENOMINATION="cart.giftCardInvalidDenomination";
	public static final String MSG_FFL_ORDER_UPDATE_ERROR="unableToUpdateFFLInfo";
	public static final String FIELD_MISSING_ERROR = "parentProducts";
	public static final String MSG_EXPRESS_CHECKOUT_ERROR="cart.expressCheckoutError";

	//Catalog Constants
	public static final String SKU_AGE_RESTRICTED="ageRestriction";
	public static final String PRODUCT_FULFILLMENT_METHOD="fulfillmentMethod";
	public static final String PRODUCT_START_DATE="startDate";
	public static final String PRODUCT_END_DATE="endDate";
	public static final String PRODUCT_TEASER_START_DATE="teaserStartDate";
  public static final String PRODUCT_TEASER_END_DATE="teaserEndDate";
	public static final int PRODUCT_INSTORE_ONLY=5;
	public static final int PRODUCT_BOPIS_ONLY=7;
	public static final String PRODUCT_FFL="ffl";
	public static final String SKU_EDS="eds";
	public static final String PRODUCT_GIFTCARD = "giftCard";
	public static final String PRODUCT = "product";
	public static final String PRODUCT_ANCESTOR_CATEGORIES = "ancestorCategories";
	public static final String PRODUCT_COMPUTED_CATALOGS = "computedCatalogs";
	public static final String PRODUCT_DYNAMIC_ATTRIBUTES = "dynamicAttributes";
	public static final String SKU_LTL = "ltl";
	public static final String SKU_LIMIT_PER_ORDER = "limitPerOrder";
	public static final String SKU_BOPIS_LIMIT_PER_ORDER = "bopisLimitPerOrder";
	public static final String SKU_RESTRICT_AIR = "restrictAir";
	public static final String SKU_PPS_MSG_IDS = "ppsMsgIds";
	public static final String SKU_PARENT_PRODUCTS = "parentProducts";
	public static final String SKU_DYNAMIC_ATTRIBUTES = "dynamicAttributes";
	public static final String SKU_SKU_ATTRIBUTES = "skuAttributes";
	public static final String SKU_VPN = "vpn";
	public static final String SKU_PPS_MESSAGE = "ppsMessage";
	public static final String SKU_UPCS = "upcs";
	
	public static final String CATALOG_PARENT_CATEGORY = "parentCategoriesForCatalog";
	public static final String PARENT_CATEGORY = "parentCategory";
	public static final String PARENT_CATEGORIES = "parentCategories";
	public static final String FIXED_PARENT_CATEGORIES = "fixedParentCategories";
	public static final String CHILD_CATEGORIES = "childCategories";
	public static final String ROOT_CATEGORY_REPO_ID="rootCategory";
	public static final String PARENT_PRODUCTS = "parentProducts";
	public static final String RESTRICTED_LOC = "restrictedLocations";
	public static final String RESTRICTED_LOC_SEPERATOR = "\\^";

	//inventory
	public static final String PROPERTY_ORDER_QUANTITY_THRESHOLD = "orderQuantiyThreshold";

	//Tax exemption
	public static final String CONST_MAX_EXEMPTIONS_REACHED = "tax.max.exemptions.reached";

	public static final String EMAIL = "email";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String MSG_CREATE_ACC = "email.account.creation";
	
	//Contact Us email params
	public static final String FLD_EMAIL = "contact-us-email";
	public static final String PARAM_EMAIL = "contactUsEmail";

	//wish list related properties
	public static final String FRIEND_EMAIL = "friendEmail";
	public static final String YOUR_EMAIL = "yourEmail";
	public static final String YOUR_NAME = "yourName";
	public static final String MESSAGE = "message";
	public static final String GIFTLIST_ID = "giftListId";
	public static final String SITE_ID = "siteId";
	public static final String GIFTLIST = "giftList";

	// CSC
	public static final String MSG_SUGGESTED_ADDRESS = "csc.suggested.address";
	public static final String MSG_FFL_ORDER = "csc.fflorder.msg";

	// Email Friend
	public static final String PRODUCT_ID_MISSING = "email.friend.productId.missing";
	public static final String FRIEND_EMAIL_MISSING = "email.friend.friendEmail.missing";
	public static final String YOUR_EMAIL_MISSING = "email.friend.yourEmail.missing";
	public static final String YOUR_NAME_MISSING = "email.friend.yourName.missing";
	public static final String MESSAGE_MISSING = "email.friend.message.missing";
	public static final String INVALID_YOUR_EMAIL = "email.friend.invalid.your.email";
	public static final String INVALID_FRIEND_EMAIL = "email.friend.invalid.friend.email";
	public static final String MISSING_REQUIRED_FIELDS = "missingRequiredFields";

	//Checkout Constants
  public static final String DEFAULT_COUNTRY = "US";
  public static final String CHECKOUT_LOGIN_REDIRECT_MSG = "checkout.login.redirect.msg";

  //Shipping Billing
   public static final String BILLING_ADDRESS_MISSING_DATA = "billing.address.missing.data";
   public static final String SHIPPING_FORM_ERRORS = "shippingFormErrors";
   public static final String SHIPPING_ADDRESS_NICKNAME_MISSING="shipAddressNickNameMissing";
   public static final String PAYMENT_NAME_MISSING="paymentNameMissing";
   public static final String MSG_DUPLICATE_PAYMENT_NAME = "duplicatePaymentName";
   public static final String SHIPPING_GROUP_ACCESS_ERRORS = "shippingGroupAccessError";
   public static final String SHIPPING_METHOD_MISSING_ERROR = "shippingMethodMissingError";
   public static final String SHIPPING_METHOD_SATURDAY_ERROR = "shippingMethodSatDayError";
   public static final String SHIPPING_GROUP_ERROR = "shippingGroupError";
   public static final String CREDIT_CARD_PAYMENT_GROUP_NULL = "creditCardPaymentGroupNull";
   public static final String PAYMENT_FORM_ERRORS = "paymentFormErrors";
   public static final String CREDIT_CARD_NUMBER_REQUIRED = "creditCardPaymentGroupNull";
   public static final String CREDIT_CARD_TYPE_REQUIRED = "creditCardTypeRequired";
   public static final String CREDIT_CARD_CVV_REQUIRED = "creditCardCVVRequired";
   public static final String CREDIT_CARD_MONTH_REQUIRED = "creditCardMonthRequired";
   public static final String CREDIT_CARD_YEAR_REQUIRED = "creditCardYearRequired";
   public static final String CREDIT_CARD_NAME_REQUIRED = "creditCardNameRequired";
   public static final String CREDIT_CARD_INVALID_CVV = "creditCardInvalidCVV";
   public static final String BILLING_ADDR_FIELDS_PREFIX = "billing-";
   public static final String MSG_NICKNAME_MAX_LEN = "error.maxlength.nickname";

// For RepeatingRequestMonitor or PerformanceMonitor:
  public static final String HANDLE_APPLY_SHIPPING_GROUPS_METHOD_NAME = "ShippingGroupFormHandler.handleApplyShippingGroups";
  public static final String HANDLE_APPLY_SHIPPING_ENTERED_ADDRESS_METHOD_NAME = "ShippingGroupFormHandler.handleApplyEnteredAddress";
  public static final String HANDLE_APPLY_SHIPPING_SUGGESTED_ADDRESS_METHOD_NAME = "ShippingGroupFormHandler.handleApplySuggestedAddress";
  public static final String HANDLE_APPLY_SHIPPING_BOPIS_METHOD_NAME = "ShippingGroupFormHandler.handleApplyBopis";
  public static final String HANDLE_APPLY_PAYMENTS_METHOD_NAME = "PaymentGroupFormHandler.handleApplyPayments";
  public static final String HANDLE_APPLY_BILLING_ADDRESS = "PaymentGroupFormHandler.handleApplyBillingAddress";
  public static final String HANDLE_APPLY_BILLING_ENTERED_ADDRESS_METHOD_NAME = "PaymentGroupFormHandler.handleApplyEnteredAddress";
  public static final String HANDLE_APPLY_BILLING_SUGGESTED_ADDRESS_METHOD_NAME = "PaymentGroupFormHandler.handleApplySuggestedAddress";
  public static final String HANDLE_APPLY_EDIT_SHIPPING_ADDRESS_METHOD_NAME = "ShippingGroupFormHandler.handleEditShippingAddress";
  public static final String HANDLE_APPLY_PAYMENTS = "PaymentGroupFormHandler.handleApplyPayments";
  public static final String SET_RESET_CREDITCARD = "PaymentGroupFormHandler.setResetCreditCard";
  public static final String HANDLE_APPLY_GIFT_CARD_METHOD_NAME = "PaymentGroupFormHandler.handleApplyGiftCard";
  public static final String PRICE_SHIPPING_METHOD_DROPLET = "PriceShippingMethodDroplet";
  public static final String DEFAULT_SHIPPING_METHOD = "ECON";
  public static final String HANDLE_COMMIT_ORDER_METHOD_NAME = "CommitOrderFormHandler.handleCommitOrder";

//Property Names
  public static final String GIFT_RECEIPT = "giftReceipt";
  public static final String GIFT_MESSAGE = "giftMessage";
  public static final String SAME_AS_SHIPPING="sameAsShipping";
  public static final String UPDATE_ORDER_ERROR = "updateOrderError";
  public static final String KWI_EXTRACT_FLAG = "kwiExtractFlag";
  public static final String EEMS_EXTRACT_FLAG = "eemsExtractFlag";
  public static final String TRANSACTION_EXTRACT_FLAG = "transactionExtractFlag";
  public static final String NOTIFICATION_EXTRACT_FLAG = "notificationExtractFlag";
  public static final String CANCEL_ITEM_REASON_CODE = "cancelItemReasonCode";
  public static final String WAREHOUSE_CANCEL = "warehouseCancel";
  public static final String NO_CONTACT_EMAIL_ORDER = "noContactEmailError";
  public static final String ZERO_BALANCE_GIFT_CARD = "zeroBalanceGiftCardError";
  public static final String INVALID_GIFT_CARD = "invalidGiftCardError";
  public static final String INVALID_MILLS_MONEY = "invalidMillsMoneyError";
  public static final String NO_GIFT_CARD_NUMBER = "noGiftCardNumberError";
  public static final String NO_GIFT_CARD_PIN = "noGiftCardPinError";
  public static final String MSG_CAPTCHA_REQUIRED = "captchaRequiredError";
  public static final String MSG_CAPTCHA_VALIDATION_ERROR = "captchaValidationError";
  public static final String MSG_GC_PRODUCT_ONLY_ERROR = "gcProductOnlyOrderError";
  public static final String GIFT_CARD_SUCCESSFULLY_REMOVED = "giftCardRemoved";
  public static final String MSG_TOO_MANY_GIFT_CARDS = "tooManyGiftCards";
  public static final String MSG_GIFTCARD_ADD_ERROR = "unableToAddGiftCardError";
  public static final String MSG_GIFTCARD_INSUFFIENT_FUNDS="insufficientFunds";
  public static final String MSG_GIFTCARD_INVALID_EAN="invalidEan";
  
  public static final String MSG_CC_TOKEN_ERROR = "unableToTokenizeTheCard";
  public static final String MSG_INVALID_CC_ACCESS_ERROR = "invalidAccessofCreditCard";
  public static final String MSG_SAVED_CARD_TOKEN_ERROR = "invalidSavedCard";

//Address Validation
 public static final String ADDRESS_FIRST_NAME="firstName";
 public static final String ADDRESS_LAST_NAME="lastName";
 public static final String ADDRESS_ADDRESS1="address1";
 public static final String ADDRESS_ADDRESS2="address2";
 public static final String ACCEPTABLE_CITIES="acceptableCities";
 public static final String UNACCEPTABLE_CITIES="unacceptableCities";
 public static final String ADDRESS_CITY="city";
 public static final String ADDRESS_STATE="state";
 public static final String ADDRESS_STATE_ADDRESS="stateAddress"; //in a ShippingGroup the State (address) property is: stateAddress, not state
 public static final String ADDRESS_POSTAL_CODE="postalCode";
 public static final String ADDRESS_COUNTRY="country";
 public static final String ADDRESS_PHONE_NUMBER = "phoneNumber";
 public static final String ADDRESS_PHONE_AREA_CODE="phoneAreaCode";
 public static final String ADDRESS_PHONE_PREFIX="phonePrefix";
 public static final String ADDRESS_PHONE_SUFFIX="phoneSuffix";
 public static final String MSG_POSTAL_CODE_INVALID = "PostalCodeInvalid";
 public static final String MSG_POSTAL_CODE_FORMAT_INVALID="PostalCodeFormatInvalid";
 public static final String MSG_PHONE_MISSING = "PhoneNumberMissing";
 public static final String MSG_PHONE_INVALID="PhoneNumberInvalid";
 public static final String MSG_POBOX_NOT_ALLOWED="POBoxNotAllowed";
 public static final String MSG_POBOX_SHIP_ADDRESS1="MsgPOBoxShipAddress1";
 public static final String MSG_POBOX_SHIP_ADDRESS2="MsgPOBoxShipAddress2";
 public static final String MSG_STATE_MISSING = "StateMissing";
 public static final String MSG_POSTAL_CODE_MISSING = "PostalCodeMissing";
 public static final String SHIPPING_METHOD="shippingMethod";
 public static final String ENTER_VALID_CITY="EnterValidCity";
 public static final String ENTER_VALID_FIRSTNAME="EnterValidFirstName";
 public static final String ENTER_VALID_LASTNAME="EnterValidLastName";
 public static final String ENTER_VALID_ADDRESS1="EnterValidAddress1";
 public static final String ENTER_VALID_ADDRESS2="EnterValidAddress2";
 public static final String MSG_ARMED_FORCES_SHIP = "MsgArmedForcesShip";
 public static final String MSG_PHONE_NUMBER_MISSING="PhoneNumberMissing";
 public static final String MSG_PHONE_NUMBER_INVALID="MsgPhoneNumberInvalid";
 public static final String ENTER_ZIP_CITY_CODE="enterCorrectCityZipState";
 public static final String PROBLEM_PERSISTS="problemPersists";
 public static final String ERROR_COMMIT_ORDER="errorCommitOrder";
 public static final String ERROR_COMMIT_ORDER_KEY="reviewError";
 public static final String FAILURE_CC_AUTH="creditCardAuthFailure";
 public static final String USER_ENTERED_ADDR_NICKNAME = "userEnteredAddrNickname";
 public static final String DEFAULT_ADDRESS_NICKNAME = "primaryAddressNickname";
 public static final String MSG_DUPLICATE_NICKNAME = "duplicateNickname";
 public static final String MSG_DUPLICATE_NICKNAMEADDRESS = "duplicateNickNameAddress";
 public static final String ADDRESS_NICKNAME="address_nickname";
 public static final String SHIPPING_BIG_S = "Shipping";
 
 //Gift card processing. Used to display a custom message 
 //on the front end on submission of the order
 public static final String GIFT_CARD_INSUFFICIENT_FUNDS = "FailedGiftCardAuthNSF";
 
 
 public static final String ADDRESS = "address";
 public static final String COMMENTS = "comments";
 public static final String SUBJECT = "subject";
 public static final String STORE_NAME="storeName";
 public static final String SHIPPING_ADDRESS_MISSING_DATA = "shippingAddressDataMissing";
 public static final String SHIPPING_ADDRESS_MISSING_FFL = "fflDealerIdMissing";
 public static final String SHIPPING_ADDRESS_RESTRICTED_FFL = "shippingStateFFLRestricted";
 public static final String SHIPPING_ADDRESS_RESTRICTED_LTL = "shippingStateLTLRestricted";
 public static final String SHIPPING_ADDRESS_RESTRICTED = "shippingAddressRestricted";
 public static final String SHIPPING_ADDRESS_APO_FPO_RESTRICTED = "shippingAPOFPORestricted";

 public static final String ADDRESS_NOT_FOUND = "addressNotFound";
//Shipping Address
 public static final String PROPERTY_ADDRESS_TYPE = "addressType";
 public static final String PROPERTY_AVS_ADDRESS_VERIFIED = "avsVerified";
 public static final String PROPERTY_AVS_ADDRESS_CHOSEN = "avsAddressChosen";
 public static final String PROPERTY_NICKNAME = "nickName";

//CreditCard - Profile
 public static final String FIELD_CARD_TYPE = "creditCardType";
 public static final String FIELD_CARD_NUMBER = "creditCardNumber";
 public static final String FIELD_CARD_EXP_MONTH = "expirationMonth";
 public static final String FIELD_CARD_EXP_YEAR = "expirationYear";
 public static final String FIELD_NAME_ON_CARD= "nameOnCard";
 public static final String FIELD_TOKEN_ID = "tokenNumber";
 public static final String FIELD_MOP_TYPE_CODE="mopTypeCode";
 public static final String FIELD_TOKEN_EXP_DATE = "tokenExpirationDate";
 public static final String FIELD_LAST_AUTH_DATE = "lastAuthorizationDate";
 public static final String FIELD_TOKEN_PROC_ID = "tokenProcId";
 public static final String CVV = "cvv";
 public static final String MSG_DUPLICATE_CARD = "duplicateCard";
 public static final String CREDIT_CARD_IS_PRIMARY = "isPrimary";
 public static final String CREDIT_CARD_NICKNAME = "creditcard-nickname";
 public static final String PROPERTY_LAST_UPDATE = "lastUpdate";
 public static final String FIELD_NICKNAME = "nickname";
 public static final String PROPERTY_CREATION_TIME = "creationTime";

 public static final String PROPERTY_CC_TOKEN_ID = "tokenId";
 public static final String PROPERTY_CC_TOKEN_EXPIRATION_DATE = "tokenExpirationDate";
 public static final String PROPERTY_CC_LAST_AUTHORIZATION_DATE = "lastAuthorizationDate";
 public static final String PROPERTY_CC_NAME_ON_CARD = "nameOnCard";
 public static final String PROPERTY_CC_TOKEN_PROC_ID = "tokenProcId";
 public static final String PROPERTY_CC_DEVICE_BLACKBOX = "deviceBlackbox";
 public static final String PROPERTY_CC_LAST_ORDER_ID = "lastOrderId";
 public static final String PROPERTY_CC_LAST_ORDER_DATE = "lastOrderDate";

 public static final String PROPERTY_ORDER_NUMBER = "orderNumber";
 public static final String PROPERTY_EMPLOYEE_ID = "employeeId";
 public static final String PROPERTY_IP_ADDRESS = "buyerIpAddress";
 public static final String PROPERTY_APP_INSTANCE_NAME="appInstanceName";

 public static final String MSG_EXP_CHECKOUT_INCOMPLETE = "expressCheckoutIncomplete";

 public static final String ORDER_TYPE_WEB = "web";
 public static final String ORDER_TYPE_CSC = "csc";
 public static final String ORDER_TYPE_BOPIS = "bopis";

 public static final String ORDER_SOURCE_CONTACT_CENTER = "contactCenter";
 public static final String ORDER_SOURCE_DEFAULT = "default";

 public static final String MSG_DUPLICATE_CARD_NICKNAME = "errorDuplicateCardNickname";
 public static final String MSG_DUPLICATE_TAX_EXMP_NICKNAME = "duplicateTaxExmpNickname";
 public static final String PROPERTY_ORDER_TAX_EXMP = "taxExemptionCode";
 public static final String PROPERTY_TAX_EXEMP_CODE = "classificationCode";
 public static final String PROPERTY_TAX_EXEMPTIONS = "taxExemptions";
 
 public static final String MSG_RESET_TOKEN_EMPTY = "resetTokenEmpty";
 public static final String MSG_LEGACY_FORCE_PWD_RESET = "forceResetPwd";
 public static final String MSG_ADDRESS_ON_CARD = "addressOnCard";
 public static final String MSG_INVALID_CITY_STATE_ZIP_COMBINATION = "invalidCityStateZipCombination";

 //MFFContentRepository
 public static final String ITEM_DESC_PPS_MESSAGE = "ppsMessage";
 public static final String PROPERTY_PPS_MESSAGE_TEXT = "text";

 //FRAUD
 public static final String FRAUD_REVIEW = "FRAUD_REVIEW";
 public static final String FRAUD_REJECT = "FRAUD_REJECT";
 
 //POS
 public static String POS_ORIGIN_OF_RETURN = "pos";
 
 //email related
 public static String CONST_PURCHASER_F_NAME = "purchaserFirstName";
 public static String CONST_PURCHASER_L_NAME = "purchaserLastName";
 public static String CONST_BOPIS_SALUTATION = "bopisSalutationName";
 
 public static String CONST_ORDER_CANCEL_PARTIAL = "partial";
 public static String CONST_ORDER_CANCEL_FULL = "full";
 public static String CONST_ORDER_CANCEL_INVALID= "invalid";

 public static String PROP_COMMERCE_ITEMS = "commerceItems";
 public static String PROP_PRICE_INFO = "priceInfo";
 public static String PROP_ADJUSTMENTS = "adjustments";
 public static String PROP_PRICING_MODEL = "pricingModel";
 public static String PROP_TOTAL_ADJUSTMENT = "totalAdjustment";
 public static String PROP_SHIPPING_GROUPS = "shippingGroups";
 
 public static String DESCRIPTION = "description";
 public static String PROMO_SHORT_DESC = "shortDescription";
 public static String PROMO_DISP_NAME = "displayName";
 public static String GLOBAL = "global";
 public static String PROMOTION = "promotion";
 public static String SHIPPING_PROMOS = "shippingPromos";
	
 public static String PROMO_DISCOUNT_AMOUNT = "globalDiscountAmount";
 public static String SHIP_PROMO_TO_DISCNT_AMNT_MAP= "shippingPromoToDiscMap";
	
 public static String TOTAL_DISCOUNT_AMOUNT = "totalDiscountAmount";
 public static String TOTAL_DISCOUNT_AMOUNT_POSITIVE = "totalSavings";
 public static String COUPON_PROMOS = "orderAppliedPromotions";
 public static String GLOBAL_PROMOS = "globalPromotions";
 public static String IS_GLOBAL_PROMO_EXISTS = "isGlobalPromoExists";
 public static String MSG_CC_TOKEN_EIVCCN_ERROR ="EIVCCN_ERROR_MESSAGE";
 public static String CC_TOKEN_EIVCCN_ERROR_CODE ="EIVCCN";
 
}