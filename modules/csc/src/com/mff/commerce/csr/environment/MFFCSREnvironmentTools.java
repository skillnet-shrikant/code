package com.mff.commerce.csr.environment;

import atg.commerce.CommerceException;
import atg.commerce.csr.environment.CSREnvironmentTools;
import atg.commerce.csr.util.CSRAgentTools;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.svc.agent.environment.EnvironmentChangeDetailConflict;
import atg.svc.agent.environment.EnvironmentChangeState;
import atg.svc.agent.environment.EnvironmentException;

import java.util.Map;

import com.mff.commerce.csr.util.MFFCSRAgentTools;
import com.mff.commerce.order.MFFCSROrderHolder;
import com.mff.commerce.order.MFFOrderManager;

public class MFFCSREnvironmentTools extends CSREnvironmentTools {
	
  private boolean mUseProductSearchVariation;
  private String mProductDescriptorName;
  private Map<String,String> mProductSearchMaxFieldConfig;
  private boolean mSendInvoiceTabOn;

 
  public Map<String, String> getProductSearchMaxFieldConfig() {
	return mProductSearchMaxFieldConfig;
  }

  public void setProductSearchMaxFieldConfig(Map<String, String> pProductSearchMaxFieldConfig) {
	  mProductSearchMaxFieldConfig = pProductSearchMaxFieldConfig;
  }

  public String getProductDescriptorName() {
	return mProductDescriptorName;
  }

  public void setProductDescriptorName(String pProductDescriptorName) {
	mProductDescriptorName = pProductDescriptorName;
  }
  
  public boolean isUseProductSearchVariation() {
	  return mUseProductSearchVariation;
  }

  public void setUseProductSearchVariation(boolean pUseProductSearchVariation) {
	  mUseProductSearchVariation = pUseProductSearchVariation;
  }



  public void changeViewOrder(String pViewOrderId) throws EnvironmentException {
    CSRAgentTools agentTools = getCSRAgentTools();
    String viewOrderId = pViewOrderId;
    if (StringUtils.isBlank(viewOrderId)) return;
    Order order = null;
    try {
      if (((MFFCSRAgentTools) agentTools).getOmsOrderManager().orderExists(viewOrderId)) {
        order = ((MFFCSRAgentTools)agentTools).getOmsOrderManager().loadOrder(viewOrderId);
      } else {
        order = ((MFFOrderManager) getOrderManager()).loadLegacyOrder(viewOrderId);
      }
    } catch (CommerceException e) {
      if (isLoggingError()) logError(e);
      throw new EnvironmentException(e);
    }

    if (order != null) loadViewOrderAndProfile(order);
  }

  @Override
  public void createNewOrder(String pApplicationName) throws EnvironmentException {

    // Set the order id in the Order Holder to null, which will reset the order
    // repository to Core Order Repository
    if (isLoggingDebug()) logDebug("MFFCSREnvironmentTools setting OrderHolder orderId to null");
    ((MFFCSROrderHolder) getOrderHolder()).setOrderId(null);

    super.createNewOrder(pApplicationName);
  }

  public void addChangeOrderDetail(String pOrderId, EnvironmentChangeState pEnvironmentChangeState) throws EnvironmentChangeDetailConflict, EnvironmentException {
    
    if (isLoggingDebug()) logDebug("Inside MFFCSREnvironmentTools addChangeOrderDetail");
    if (getOrderChangeId(pEnvironmentChangeState) != null && getOrderChangeId(pEnvironmentChangeState).equals(pOrderId)) return;
    Order changeOrder;
    try {
      changeOrder = ((MFFCSRAgentTools)getCSRAgentTools()).getOmsOrderManager().loadOrder(pOrderId);
    } catch (CommerceException e) {
      throw new EnvironmentException(e);
    }
    addChangeOrderDetail(changeOrder, pEnvironmentChangeState);
  }

  public boolean isSendInvoiceTabOn() {
	return mSendInvoiceTabOn;
  }

  public void setSendInvoiceTabOn(boolean pSendInvoiceTabOn) {
	mSendInvoiceTabOn = pSendInvoiceTabOn;
  }

}