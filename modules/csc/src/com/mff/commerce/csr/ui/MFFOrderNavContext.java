package com.mff.commerce.csr.ui;

import atg.commerce.csr.order.CSROrderHolder;
import atg.commerce.csr.ui.OrderNavContext;
import atg.core.util.StringUtils;

import com.mff.commerce.order.MFFOrderImpl;

public class MFFOrderNavContext extends OrderNavContext {

  /* (non-Javadoc)
   * @see atg.commerce.csr.ui.OrderNavContext#getLabel()
   */
  @Override
  public String getLabel() {
    CSROrderHolder orderHolder = getCSRAgentTools().getCSREnvironmentTools().getOrderHolder();
    MFFOrderImpl currentOrder = (MFFOrderImpl) orderHolder.getOriginalOrder();
    if(currentOrder == null)
        return "";
    String label = null;
    if(currentOrder.isTransient() && !orderHolder.isCloneEditMode())
    {
        return super.getLabel();
    } else
    {
        label = !StringUtils.isEmpty(currentOrder.getOrderNumber())?currentOrder.getOrderNumber():currentOrder.getId();
        return label;
    }
  }

}
