/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.item;

public class AllocationConstants {
	
	// Item Types
	public static final String ITEM_STORE_ALLOCATION				= "storeAllocation"; 
	public static final String ITEM_GC_ALLOCATION					= "GCAllocation";
	
	// Order Properties
	public static final String PROPERTY_ID 							= "id";
	public static final String PROPERTY_ORDER_ID 					= "orderId";
	public static final String PROPERTY_ORDER_NUMBER 				= "orderNumber";
	public static final String PROPERTY_COMMERCE_ITEM_ID 			= "commerceItemId";
	public static final String PROPERTY_SKU_ID 						= "skuId";
	public static final String PROPERTY_STORE_ID 					= "storeId";
	//public static final String PROPERTY_UPC 						= "upc";
	public static final String PROPERTY_QUANTITY 					= "quantity";
	public static final String PROPERTY_ORDER_DATE 					= "orderDate";
	public static final String PROPERTY_ALLOCATION_DATE 			= "allocationDate";
	public static final String PROPERTY_SHIPPED_DATE 				= "shipDate";
	public static final String PROPERTY_DECLINE_DATE 				= "declineDate";
	public static final String PROPERTY_STATE 						= "State";
	public static final String PROPERTY_STATE_DETAIL				= "StateDetail";
	public static final String PROPERTY_GIFT_CARD_NO				= "giftCardNo";
	public static final String PROPERTY_GIFT_CARD_PIN				= "giftCardPin";
	public static final String PROPERTY_GIFT_CARD_AMOUNT			= "giftCardAmount";
	public static final String PROPERTY_SHIPPING_METHOD				= "shippingMethod";
	public static final String PROPERTY_FIRST_NAME					= "firstName";
	public static final String PROPERTY_LAST_NAME					 = "lastName";
	public static final String PROPERTY_ADDRESS1           = "address1";
	public static final String PROPERTY_ADDRESS2           = "address2";
	public static final String PROPERTY_CITY               = "city";
	public static final String PROPERTY_SHIP_STATE         = "shipState";
	public static final String PROPERTY_COUNTY             = "county";
	public static final String PROPERTY_COUNTRY            = "country";
	public static final String PROPERTY_POSTAL_CODE        = "postalCode";
	public static final String PROPERTY_PHONE_NUMBER          = "phoneNumber";
	public static final String PROPERTY_CONTACT_EMAIL         = "contactEmail";
	public static final String PROPERTY_BOPIS_ORDER           = "bopisOrder";
	public static final String PROPERTY_IN_PICKING           = "inPicking";
	public static final String PROPERTY_PICK_UP_INTRUCTIONS  = "pickUpInstructions";
	public static final String PROPERTY_IN_PICKING_DATE 	 = "inPickingDate";
	public static final String PROPERTY_READYFOR_PICKUP_DATE = "readyForPickupDate";
	
	//GC Allocation properties
	public static final String PROPERTY_EGIFT_CARD_FLAG             = "eGiftCardFlag";
	
	// Pipelines
	public static final String PIPELINE_ALLOCATE_ORDER 				= "handleAllocateOrder";
	public static final String PIPELINE_STORE_SHIPMENT 				= "handleStoreShipment";
	public static final String PIPELINE_CANCEL 						    = "handleCancel";
	public static final String PIPELINE_BOPIS_CANCEL          = "handleBopisCancel";
	public static final String PIPELINE_BOPIS_REMINDER_EMAIL  = "handleBopisReminderEmail";
	public static final String PIPELINE_STORE_READY_FOR_PICKUP= "handleStoreReadyForPickUp";
	public static final String PIPELINE_STORE_DECLINE 				= "handleStoreDecline";
	public static final String PIPELINE_STORE_PROCESS 				= "handleStoreProcess";
	public static final String PIPELINE_GC_SHIPMENT 				  = "handleGCShipment";
	public static final String PIPELINE_RETURN_STORE_CREDIT   = "handleReturnStoreCreditShipment";
	
	// Pipeline Parameters
	public static final String PIPELINE_PARAMETER_ORDER   			= "omsOrder";
	public static final String PIPELINE_PARAMETER_ALLOCATIONS 		= "allocations";
	public static final String PIPELINE_PARAMETER_ALLOCATION_TYPE 	= "allocationType";
	public static final String PIPELINE_PARAMETER_ITEMS_TO_CANCEL   = "itemsToCancel";

	public static final String PIPELINE_PARAMETER_ITEMS_TO_SHIP     = "itemsToShip";
	public static final String PIPELINE_PARAMETER_ITEMS_TO_DECLINE  = "itemsToDecline";
	public static final String PIPELINE_PARAMETER_ITEMS_TO_PICKUP   = "itemsToPickup";
	public static final String PIPELINE_PARAMETER_TRACKING_NO  		  = "trackingNo";
	public static final String PIPELINE_PARAMETER_REASON_CODE_MAP   = "reasonCodeMap";
	public static final String PIPELINE_PARAMETER_CANCEL_REASON_CODE= "cancelReasonCode";
	public static final String PIPELINE_PARAMETER_CANCEL_DESC		= "cancelDescription";
	public static final String PIPELINE_PARAMETER_SPECIAL_INST    = "specialInstructions";
	public static final String PIPELINE_PARAMETER_GIFTCARD_DETAILS	= "giftCardDetails";
	public static final String PIPELINE_PARAMETER_GIFTCARD_PROCESS	= "giftCardProcess";
	public static final String PIPELINE_PARAMETER_GC_ALLOCATION     = "giftCardAllocation";
	public static final String PIPELINE_PARAMETER_GC_ALLOCATION_MGR = "giftCardAllocationManager";
	public static final String PIPELINE_PARAMETER_RETURN_REQUEST_ID = "returnRequestId";
	public static final String PIPELINE_PARAMETER_FORCED_STORE   	= "forcedStore";
	public static final String PIPELINE_PARAMETER_FORCED_ALLOCATED_ITEMS    = "forceAllocatedItems";
	public static final String PIPELINE_PARAMETER_SIGNATURE       = "signature";
	public static final String PIPELINE_PARAMETER_DATE_OF_BIRTH   = "dateOfBirth";
	public static final String PIPELINE_PARAMETER_DROP_SHIP_MESSAGE = "message";
	public static final String PIPELINE_PARAMETER_SUPPRESS_SHIPPED_EMAIL = "suppressShippedEmail";
	public static final String PIPELINE_PARAMETER_COMMENTS          = "comments";
	public static final String PIPELINE_PARAMETER_INSUFFICIENT_FUNDS = "inSufficientFunds";
	public static final String PIPELINE_PARAMETER_INSUFFICIENT_FUNDS_ERROR_MESSAGE = "inSufficientFundsMessage";
	public static final String PIPELINE_PARAMETER_INSUFFICIENT_FUNDS_ERROR_MESSAGE_STR = "Insufficient Funds - Please Contact the Call Center";
	public static final String PIPELINE_PARAMETER_AGENT_USER_ID    = "agentUserId";
	public static final String PIPELINE_PARAMETER_IS_FIRST_REMINDER    = "isFirstReminder";
	
	//pipeline constant for settlement map. Contains a mapping between the payment group and the amount
	public static final String PIPELINE_PARAMETER_SETTLEMENT_MAP = "settlementMap";
	
	// Allocation Type
	public static final String INITIAL_ALLOCATION_TYPE 				= "initial";
	public static final String SUBSEQUENT_ALLOCATION_TYPE 			= "subsequent";
	
	// Warehouse Store
	public final static String WAREHOUSE_STORE						= "197975";
	public final static String BACKORDER_STORE						= "999999";
	
	public static final String INVENTORY_REPORT_ITEM_DESC = "inventoryReport";
  public static final String INV_REPORT_PROP_WEB_ORDERNUMBER = "webOrderNumber";
  public static final String INV_REPORT_PROP_ALLOCATED_SKUID = "allocatedSkuId";
  public static final String INV_REPORT_PROP_ORDERED_DATETIME = "orderedDateTime";
  public static final String INV_REPORT_PROP_ORDERPROCESSED_DATETIME = "orderProcessedDateTime";
  public static final String INV_REPORT_PROP_ALLOCATED_STORELOCATION = "allocatedStoreLocation";
  public static final String INV_REPORT_PROP_ALLOCATION_TYPE = "allocationType";
  public static final String INV_REPORT_PROP_ALLOCATION_SUBTYPE = "allocationSubType";
  public static final String INV_REPORT_PROP_ALLOCATED_QUANTITY = "allocatedQuantity";
  public static final String INV_REPORT_PROP_CREATE_USER = "transactionCreatedUser";
  public static final String INV_REPORT_PROP_CREATE_DATE = "transactionCreatedDateTime";
  public static final String INV_REPORT_PROP_UPDATE_USER = "transactionLastUpdatedUser";
  public static final String INV_REPORT_PROP_UPDATE_DATE = "transactionLastUpdatedDateTime";
  public static final String INV_REPORT_ALLOCATION_SUB_TYPE_BOPIS = "BOPIS";
  public static final String INV_REPORT_ALLOCATION_SUB_TYPE_PPS = "PPS";
  public static final String INV_REPORT_ALLOCATED = "Allocated";
  public static final String INV_REPORT_DEALLOCATED = "Deallocated";
  public static final String INV_REPORT_PROP_REASON_CODE = "reasonCode";
  public static final String IS_PARTIAL_CANCELLATION="isPartialCancellation";
  public static final String TOTAL_CANCELLATION_PRICE="totalCancellationPrice";
  public static final String ITEM_TO_PREV_STATE_MAP="itemToPrevStateMap";
}
