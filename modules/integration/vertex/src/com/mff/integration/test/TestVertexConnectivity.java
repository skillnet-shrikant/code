package com.mff.integration.test;

import com.mff.integration.ws.service.vertex.CalculateTaxService;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.core.util.ContactInfo;
import atg.nucleus.GenericService;
import atg.payment.tax.ShippingDestinationImpl;
import atg.payment.tax.TaxRequestInfoImpl;

public class TestVertexConnectivity extends GenericService {

	private String mZipCode;
	private String mOrderId;
	private OrderManager mOrderManager;

	public void estimateTax() throws Exception {
		// Tax calculations based on the postal code
		double estimatedTax = 0;
		TaxRequestInfoImpl pTaxRequestInfo = new TaxRequestInfoImpl();
		ShippingDestinationImpl[] pShippingDestination = new ShippingDestinationImpl[1];
		
		Order pOrder = getOrderManager().loadOrder(getOrderId());
		pTaxRequestInfo.setOrder(pOrder);
		pTaxRequestInfo.setOrderId(getOrderId());

		ContactInfo adress = new ContactInfo();
		adress.setPostalCode(getZipCode());
		ShippingDestinationImpl shippingAddress = new ShippingDestinationImpl();
		shippingAddress.setShippingAddress(adress);
		pShippingDestination[0] = shippingAddress;
		
		pTaxRequestInfo.setShippingDestinations(pShippingDestination);
		
		vlogInfo("Estimating Tax for Order:: {0} and Zip Code:: {1}",getOrderId(),getZipCode());
		try{
			estimatedTax = getTaxService().estimatedTax(pTaxRequestInfo, getZipCode());
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		
		vlogInfo("Estimated Tax: {0}",estimatedTax);

	}
	
	private CalculateTaxService mTaxService;

	public CalculateTaxService getTaxService() {
		return mTaxService;
	}

	public void setTaxService(CalculateTaxService mTaxService) {
		this.mTaxService = mTaxService;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return mZipCode;
	}

	/**
	 * @param pZipCode the zipCode to set
	 */
	public void setZipCode(String pZipCode) {
		mZipCode = pZipCode;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return mOrderId;
	}

	/**
	 * @param pOrderId the orderId to set
	 */
	public void setOrderId(String pOrderId) {
		mOrderId = pOrderId;
	}

	/**
	 * @return the orderManager
	 */
	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}
}