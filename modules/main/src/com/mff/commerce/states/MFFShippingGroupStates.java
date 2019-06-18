package com.mff.commerce.states;

import atg.commerce.states.ShippingGroupStates;

public class MFFShippingGroupStates extends ShippingGroupStates {

	public static final String CANCELLED="cancelled";
	public static final String SHIPPED="shipped";
	public static final String SENT_TO_STORE="sent_to_store";
	public static final String REALLOCATE="reallocate";
	public static final String FORCED_ALLOCATION="forced_allocation";
	public static final String PENDING_GC_FULFILLMENT="pending_gc_fulfillment";
	public static final String PENDING_DROP_SHIP_FULFILLMENT="pending_drop_ship_fulfillment";
}
