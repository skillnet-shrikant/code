package com.mff.commerce.csr.ui;

import atg.commerce.csr.ui.OrderCancelNavAction;

import com.mff.commerce.csr.util.MFFCSRAgentTools;
import com.mff.commerce.order.MFFOrderImpl;

/**
 * 
 * @author vsingh
 * 
 */
public class MFFOrderCancelNavAction extends OrderCancelNavAction {

  @Override
  public boolean isEnabled() {

    if (super.isEnabled()) {
      MFFCSRAgentTools agentTools = (MFFCSRAgentTools) getCSRAgentTools();
      MFFOrderImpl currentOrder = (MFFOrderImpl) agentTools.getCSREnvironmentTools().getCurrentOrder();
      // we are checking for the below states, as these are special cases when
      // we don't allow item based cancellation but only order based cancellation. 
      // Once order is allocated, item based cancellation is enabled
      for(String orderState : agentTools.getCancellableOrderStates()){
        if (currentOrder.getStateAsString().equalsIgnoreCase(orderState)) {
          return true;
        }
      }
    }
    
    return false;
  }

}
