package com.mff.commerce.order;

import com.mff.util.MFFUtils;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

public class MFFOrderHolder extends OrderHolder {
  

  @Override
  protected Order createInitialOrder(RepositoryItem pProfile) throws CommerceException {
    
    if (pProfile != null && pProfile.getRepositoryId() != null) {
      return super.createInitialOrder(pProfile);
    } else {
      /*
       * Use the logger instead of the component logging to have the ability to
       * turn on logging debug using dynamo admin. Can be turned from the
       * following location in dyn/admin
       * /dyn/admin/nucleus/atg/dynamo/service/logging/ClassLoggingFactory
       */
      ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClass(getClass());
      if (logger != null && logger.isLoggingDebug()) {
        logger.logDebug(MFFUtils.printRequestInfo(ServletUtil.getCurrentRequest()));
      }
      
      return null;
    }
    
  }
  
  

}
