/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.store;

public class StoreAllocationStates {

	// Item has been allocated to the store and is awaiting shipping by the store
	public static final String PRE_SHIP = "PRE_SHIP";

	// Store has declined to ship the item.  Item will be re-allocated to another store
	public static final String DECLINE = "DECLINE";

	// Item has been shipped by the store
	public static final String SHIPPED = "SHIPPED";

	// Item has been cancelled by the store
	public static final String CANCELLED = "CANCELLED";
	
	//Item which is readyForPickUp by the user, only used for Bopis orders
	public static final String READY_FOR_PICKUP = "READY_FOR_PICKUP";
}
