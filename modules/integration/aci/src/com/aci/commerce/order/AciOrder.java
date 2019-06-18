package com.aci.commerce.order;


import com.aci.constants.AciConstants;

import atg.commerce.order.OrderImpl;

public class AciOrder extends OrderImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public AciOrder(){
		super();
	}
	
	public void setBuyerIpAddress(String pIpAddress) {
		setPropertyValue(AciConstants.PROPERTY_ACI_BUYER_IP_ADDRESS, pIpAddress);
	}
	
	public String getBuyerIpAddress() {
		return (String) getPropertyValue(AciConstants.PROPERTY_ACI_BUYER_IP_ADDRESS);
	}
	
	public void setDeviceId(String pDeviceId) {
		setPropertyValue(AciConstants.PROPERTY_ACI_DEVICE_BLACKBOX, pDeviceId);
	}
	
	public String getDeviceId() {
		return (String) getPropertyValue(AciConstants.PROPERTY_ACI_DEVICE_BLACKBOX);
	}
	
	public void setFraudStat(String pFraudStat){
		setPropertyValue(AciConstants.PROPERTY_ACI_FRAUD_STAT, pFraudStat);
	}
	
	public String getFraudStat(){
		return (String) getPropertyValue(AciConstants.PROPERTY_ACI_FRAUD_STAT);
	}
	
	public String getTiebackAgentId(){
		return (String) getPropertyValue(AciConstants.PROPERTY_ACI_TIEBACK_AGENT_ID);
	}
	
	public void setTiebackAgentId(String pAgentId){
		setPropertyValue(AciConstants.PROPERTY_ACI_TIEBACK_AGENT_ID,pAgentId);
	}
	
	

}
