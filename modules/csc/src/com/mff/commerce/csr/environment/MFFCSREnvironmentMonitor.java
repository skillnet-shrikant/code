package com.mff.commerce.csr.environment;

import atg.commerce.csr.environment.CSREnvironmentMonitor;
import atg.svc.agent.environment.EnvironmentException;

import com.mff.commerce.order.MFFCSROrderHolder;

public class MFFCSREnvironmentMonitor extends CSREnvironmentMonitor {

  @Override
  public void initializeNewOrderInCart() throws EnvironmentException {

    MFFCSROrderHolder holder = (MFFCSROrderHolder) getCSREnvironmentTools().getOrderHolder();
    if (isLoggingDebug()) logDebug("MFFCSREnvironmentMonitor:Clear OrderHolder OrderId");
    holder.setOrderId(null);
    super.initializeNewOrderInCart();
  }

}
