package mff.returns;

import atg.commerce.order.Order;

/**
 * 
 * @author vsingh
 *
 */
public class ReturnResponse {
	
	private Order mOrder;

	public Order getOrder() {
		return mOrder;
	}

	public void setOrder(Order mOrder) {
		this.mOrder = mOrder;
	}

}
