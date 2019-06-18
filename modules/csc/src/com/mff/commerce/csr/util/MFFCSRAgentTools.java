package com.mff.commerce.csr.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import atg.commerce.csr.util.CSRAgentTools;
import atg.commerce.order.OrderTools;
import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;
import atg.svc.agent.environment.EnvironmentTools;
import atg.ticketing.TicketingException;
import oms.commerce.order.OMSOrderManager;

public class MFFCSRAgentTools extends CSRAgentTools {

  private OMSOrderManager mOmsOrderManager;
  private List<String> mCancellableOrderStates;
  private List<String> mCommerceItemSearchableStates;
  private List<String> mShippingMethodSearchableStates;
  private LinkedHashMap<String,String> bopisCancelReasonCodes;
  private LinkedHashMap<String,String> ppsCancelReasonCodes;
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public boolean associateTicketAndOrder(String pOrderId, MutableRepositoryItem pTicket) throws TicketingException {
    if (StringUtils.isEmpty(pOrderId)) return false;

    MutableRepositoryItem mutableTicketItem = null;

    try {
      EnvironmentTools envTools = getCSREnvironmentTools().getEnvironmentTools();
      OrderTools orderTools = getOrderManager().getOrderTools();

      if (pTicket != null)
        mutableTicketItem = pTicket;
      else
        mutableTicketItem = RepositoryUtils.getMutableRepositoryItem(envTools.getActiveTicket());

      RepositoryItem orderToAdd = orderTools.getOrderRepository().getItem(pOrderId, orderTools.getOrderItemDescriptorName());

      if (mutableTicketItem != null && orderToAdd != null && !orderToAdd.isTransient()) {
        Set existingOrders = (Set) mutableTicketItem.getPropertyValue(getTicketOrdersPropertyName());
        List<String> existingOrdersIds = new ArrayList<String>();

        if (existingOrders != null) {
          Iterator iter = existingOrders.iterator();
          while (iter.hasNext())
            existingOrdersIds.add(((String) iter.next()));

          if (existingOrdersIds.indexOf(orderToAdd.getRepositoryId()) == -1) existingOrders.add(orderToAdd.getRepositoryId());
        } else {
          existingOrders = new HashSet();
          existingOrders.add(orderToAdd.getRepositoryId());
        }

        envTools.getTicketingManager().getTicketingRepository().updateItem(mutableTicketItem);

        return true;
      }
    } catch (RepositoryException re) {
      throw new TicketingException(re);
    }

    return false;
  }
  
  public OMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(OMSOrderManager mOmsOrderManager) {
    this.mOmsOrderManager = mOmsOrderManager;
  }

  public List<String> getCancellableOrderStates() {
    return mCancellableOrderStates;
  }

  public void setCancellableOrderStates(List<String> pCancellableOrderStates) {
    this.mCancellableOrderStates = pCancellableOrderStates;
  }

  public List<String> getCommerceItemSearchableStates() {
    return mCommerceItemSearchableStates;
  }

  public void setCommerceItemSearchableStates(List<String> pCommerceItemSearchableStates) {
    this.mCommerceItemSearchableStates = pCommerceItemSearchableStates;
  }

  public List<String> getShippingMethodSearchableStates() {
    return mShippingMethodSearchableStates;
  }

  public void setShippingMethodSearchableStates(List<String> pShippingMethodSearchableStates) {
    this.mShippingMethodSearchableStates = pShippingMethodSearchableStates;
  }
  
  public LinkedHashMap<String, String> getBopisCancelReasonCodes() {
    return bopisCancelReasonCodes;
  }

  public void setBopisCancelReasonCodes(LinkedHashMap<String, String> bopisCancelReasonCodes) {
    this.bopisCancelReasonCodes = bopisCancelReasonCodes;
  }

	public LinkedHashMap<String, String> getPpsCancelReasonCodes() {
		return ppsCancelReasonCodes;
	}

	public void setPpsCancelReasonCodes(LinkedHashMap<String, String> ppsCancelReasonCodes) {
		this.ppsCancelReasonCodes = ppsCancelReasonCodes;
	}
}
