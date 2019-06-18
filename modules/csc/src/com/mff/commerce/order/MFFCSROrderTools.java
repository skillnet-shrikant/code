package com.mff.commerce.order;

import atg.commerce.csr.environment.CSREnvironmentTools;
import atg.repository.Repository;
import atg.repository.RepositoryItem;

public class MFFCSROrderTools extends MFFOrderTools {

  public Repository getOrderRepository() {
    Repository orderRepository = super.getOrderRepository();

    try {
      CSREnvironmentTools csrEnvTools = getCsrEnvironmentTools();
      if (csrEnvTools != null) {
        MFFCSROrderHolder orderHolder = (MFFCSROrderHolder) csrEnvTools.getOrderHolder();
        if (orderHolder != null) {
          String orderId = orderHolder.getOrderId();
          if (isLoggingDebug()) logDebug("MFFCSROrderTools.getOrderRepository - orderId: " + orderId);

          if (orderId != null) {
            Repository omsOrderRepository = getOmsOrderRepository();
            if (omsOrderRepository != null) {
              RepositoryItem orderObject = omsOrderRepository.getItem(orderId, "order");
              if (orderObject != null) {
                orderRepository = omsOrderRepository;
                if (isLoggingDebug()) logDebug("MFFCSROrderTools.getOrderRepository - Using OMS Repository");
              }
            }
          }

        }
      }
    } catch (Exception re) {
      vlogError(re,"Error while getting order repository");
    }

    return orderRepository;
  }

  Repository omsOrderRepository;

  public Repository getOmsOrderRepository() {
    return omsOrderRepository;
  }

  public void setOmsOrderRepository(Repository omsOrderRepository) {
    this.omsOrderRepository = omsOrderRepository;
  }

  CSREnvironmentTools csrEnvironmentTools;

  public CSREnvironmentTools getCsrEnvironmentTools() {
    return csrEnvironmentTools;
  }

  public void setCsrEnvironmentTools(CSREnvironmentTools csrEnvironmentTools) {
    this.csrEnvironmentTools = csrEnvironmentTools;
  }
}
