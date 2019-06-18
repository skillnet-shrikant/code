package com.mff.commerce.states;

import atg.commerce.states.CommerceItemStates;

public class MFFCommerceItemStates extends CommerceItemStates {

	public static final String SHIPPED="shipped";
	public static final String CANCELLED="cancelled";
	public static final String RETURNED="returned";
	public static final String SENT_TO_STORE="sent_to_store";
	public static final String PENDING_GC_FULFILLMENT="pending_gc_fulfillment";
	public static final String PENDING_GC_ACTIVATION="pending_gc_activation";
	public static final String PENDING_ALLOCATION="pending_allocation";
	public static final String FORCED_ALLOCATION="forced_allocation";
	public static final String READY_FOR_PICKUP="ready_for_pickup";
	public static final String PENDING_DROP_SHIP_FULFILLMENT="pending_drop_ship_fulfillment";
	public static final String PENDING_DROP_SHIP_CONFIRM="pending_drop_ship_confirm";
	public static final String DROP_SHIP_ACCEPTED="drop_ship_accepted";
	public static final String ERROR_GC_ACTIVATION="error_gc_activation";
}
