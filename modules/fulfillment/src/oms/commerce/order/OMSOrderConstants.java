/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */


/**
 * 
 */
package oms.commerce.order;

public class OMSOrderConstants
{

	/**
	 * Default Constructor
	 */
	public OMSOrderConstants() {
		super();
	}

	// fulfillmentPipeline Constants
	public static final String PIPELINE_OMS_ORDER = "omsOrder";
	public static final String PIPELINE_ORDER_ID = "orderId";
	public static final String PIPELINE_SHIPGROUP_ID = "shipGroupId";
	public static final String PIPELINE_OMS_PROCESS = "omsProcess";
	public static final String PIPELINE_PROCESS_INVENTORY = "ProcessInv";
	public static final String PIPELINE_PROCESS_SHIPPING_GROUP = "shippingGroup";
	public static final String PIPELINE_READY_PICKS = "readyPicks";
	public static final String PIPELINE_SEND_PICKS = "sendPicks";
	public static final String PIPELINE_INVOICE_DATA = "invoiceData";
	public static final String PIPELINE_GENERATE_INVOICE = "generateInvoice";
	public static final String PIPELINE_CANCELLED_ITEMS = "cancelledItems";
	public static final String PIPELINE_XML_MSG = "xmlMsg";
	public static final String PIPELINE_XML_ID = "xmlId";
	public static final String PIPELINE_LOCK_AGENT = "lockAgent";
	public static final String PIPELINE_LOCK_SUCCESS = "lockSuccess";
	public static final String PIPELINE_PICK_STATUS_ID = "pickStatusId";
	public static final String PIPELINE_PICK_STATUS = "pickStatus";
	public static final String PIPELINE_PICK_STATUS_INFO = "pickStatusInfo";
	public static final String PIPELINE_PICK_TYPE = "pickType";
	public static final String PIPELINE_PICK_CI = "pickCommerceItems";
	public static final String PIPELINE_PICK_CANCELLED_CI = "pickCancelledCommerceItems";
	public static final String PIPELINE_REPROCESS = "reprocess";
	public static final String PIPELINE_SCHEDULER_ACTIVATED = "schedulerActivated";
	public static final String PIPELINE_APPEASEMENT_EMAIL = "sendAppeasementEmail";
	public static final String PIPELINE_DONATION_SHIPS = "donationShips";
	public static final String PIPELINE_EGIFTCARD_SHIPS = "eGiftcardShips";
	public static final String PIPELINE_PICK_FIRST_SHIPMENT = "isFirstShipment";
	public static final String PIPELINE_NEW_ORDER_STATE = "newOrderState";
	public static final String PIPELINE_PICK_NUMBER = "pickNumber";
	public static final String PIPELINE_SHIP_NOTIFICATIONS = "shipNotifications";
	public static final String PIPELINE_ITEMS_TO_SHIP = "itemsToShip";
	public static final String PIPELINE_ITEMS_TO_CANCEL = "itemsToCancel";
	public static final String PIPELINE_ITEMS_TO_DECLINE = "itemsToDecline";
	public static final String PIPELINE_ITEMS_TO_PICKUP   = "itemsToPickup";
	public static final String PIPELINE_PROFITPOINT_CANCELLATION_REFUND="profitPointRefundCancellation";
	
	public static final String PIPELINE_APPEASEMENT_AMOUNT = "appeasementAmount";
	public static final String PIPELINE_APPEASEMENT_TYPE = "appeasementType";
	public static final String PIPELINE_APPEASEMENT_NOTE = "appeasementNote";
	public static final String PIPELINE_APPEASEMENT_REASON_CODE = "appeasementReasonCode";
	public static final String PIPELINE_APPEASEMENT_AGENT_ID = "appeasementAgentId";
	public static final String PIPELINE_APPEASEMENT_OPERATION = "appeasementOperation";
	public static final String PIPELINE_APPEASEMENT_ALLOCATION = "allocation";
	public static final String PIPELINE_APPEASEMENT_AGENT_NAME = "appeasementAgentName";
	
	public static final String APPEASEMENT_PROFIT_POINT_REFUND_SHARE = "profitPointRefund";
	
	
	public static final String PIPELINE_APPEASEMENT_ADJUSTMENT = "appeasementAdjustment";
	public static final String PIPELINE_PICK_AUTHORIZATION_CHECK = "pickAuthorizationCheck";
	

	// pipeline names
	public static final String PIPELINE_PROCESS_INVOICE_CHAIN = "handleProcessInvoiceMessage";
	public static final String PIPELINE_MANUAL_MOVE_OMS_CHAIN = "handleSaveOrderToOMSManual";
	public static final String PIPELINE_PROCESS_PICKRESPONSE_CHAIN = "handleProcessPickResponse";
	public static final String PIPELINE_PENDING_APPEASEMENTS_CHAIN = "handleOrdersPendingAppeasement";

	public static final String PIPELINE_PROCESS_EXCHANGE_INVOICE_CHAIN = "handleProcessExchangeInvoiceMessage";
	public static final String PIPELINE_PROCESS_DONATION_INVOICE_CHAIN = "handleProcessDonationsOrder";
	public static final String PIPELINE_PROCESS_EGIFTCARD_INVOICE_CHAIN = "handleProcessEgiftCardsOrder";

	// oms processes
	public static final String SAVE_TO_OMS ="saveToOMS";
	public static final String PROCESS_PICKS="processPicks";
	public static final String PROCESS_INVOICE="processInvoice";
	public static final String PROCESS_RESEND_PICKS="processResendPicks";
	public static final String PROCESS_ERROR_PICKS="processErrorPicks";
	public static final String PROCESS_SAVE_OMS="saveToOMS";
	//pick number id space name
	public static final String PICK_ID_SPACE="pick_number";
	//invoice short shipped
	public static final String PICK_SUFFIX_SHORTSHIPPED="WBO";

	public static final String AGETNT_PRICE_ADJUS_DESC = "Agent price override";

	//Profile
	public static final String PROFILE_LOYALITY_ID ="loyalityId";

	//Reason Codes
	public static final String CR_REASON_CODE="(CR) Customer Requested";
	public static final String FC_REASON_CODE="(FC) Fraud Cancel";
	public static final String FTC_REASON_CODE="(FT) FTC";
	public static final String UN_REASON_CODE="(UN) Item Unavailable";

	//inventory movement
	public final static String SHIP_MOVEMENT_INV_TRANSACTION="I";

	// pick statuses
	public static final String PICK_PROCESSING_STATUS = "PROCESSING";
	public static final String PICK_PENDING_STATUS = "PENDING";
	public static final String PICK_ERROR_STATUS = "ERROR";
	public static final String PICK_SUCCESS_STATUS = "SUCCESS";

	// pick types
	public static final String PICK_TYPE_SHIPPED = "SHIPPED";
	public static final String PICK_TYPE_CANCELLED = "CANCELLED";
	public static final String PICK_TYPE_RETURNED = "RETURNED";	

	//Appeasement related.
	public static final String PROP_APPEASEMENT_STATUS_INITIAL = "INITIAL";
	public static final String PROP_APPEASEMENT_STATUS_ERROR = "ERROR";
	public static final String PROP_APPEASEMENT_STATUS_SENT = "SENT";
	public static final String PROP_APPEASEMENT_STATE="state";
	public static final String PROP_APPEASEMENT_ORDER_NUMBER = "appeasementOrderNumber";
	public static final String ITEM_APPEASEMENT_VIEWNAME = "appeasementRecord";
	public static final String PIPELINE_APPEASMENT_TO_WMS = "handleSendAppeasementToWMS";
	public static final String PIPELINE_APPEASEMENT_ITEM = "appeasement";
	public static final String APPEASMENT_TYPE_REVERSE_APPEASEMENT =  "Reverse Appeasement";
	
	public final static String ADJUSTMENT_TYPE_PRICE					= "priceAdjustment";
	public final static String ADJUSTMENT_TYPE_SHIPPING					= "shippingAdjustment";
	public final static String ADJUSTMENT_TYPE_TAX						= "taxAdjustment";

	//Commerce Item related
	public static final String ITEM_PROPERTY_PROMO_AMOUNT_MAP = "promoAmountMap";
}