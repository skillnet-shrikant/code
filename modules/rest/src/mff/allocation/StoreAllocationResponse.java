package mff.allocation;

import atg.commerce.order.Order;

/**
 * 
 * @author vsingh
 *
 */
public class StoreAllocationResponse {
	
	private Order mOrder;

	public Order getOrder() {
		return mOrder;
	}

	public void setOrder(Order mOrder) {
		this.mOrder = mOrder;
	}

}
