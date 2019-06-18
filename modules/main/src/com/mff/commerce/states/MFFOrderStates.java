package com.mff.commerce.states;

import atg.commerce.states.OrderStates;

public class MFFOrderStates extends OrderStates {

	/**
	 * Order state names pertinent to Core
	 */
	public static final String SENT_TO_OMS = "sent_to_oms";
	public static final String ERROR_TO_OMS="error_to_oms";
	
	/**
	 * Order state names pertinent to OMS
	 */
	public static final String IN_REMORSE = "in_remorse";
	public static final String FRAUD_REVIEW = "fraud_review";
	public static final String FRAUD_REJECT = "fraud_reject";
	
	//public static final String PROCESSING_STORE = "processing_store";
	public static final String SENT_TO_STORE = "sent_to_store";
	
	public static final String FORCED_ALLOCATION = "forced_allocation";
	
	public static final String PARTIALLY_SHIPPED = "partially_shipped";
	public static final String SHIPPED = "shipped";
	
	public static final String FRAUD_CANCEL = "fraud_cancel";
	public static final String CANCELLED = "cancelled";
	
	public static final String SYSTEM_HOLD = "system_hold";
	public static final String REMORSE_PERIOD="remorse_period";
	
	public static final String PENDING_ALLOCATION="pending_allocation";
}
