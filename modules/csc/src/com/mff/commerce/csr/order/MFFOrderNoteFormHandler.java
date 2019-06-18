package com.mff.commerce.csr.order;

import java.io.IOException;

import javax.servlet.ServletException;

import com.google.common.base.Strings;

import atg.commerce.CommerceException;
import atg.commerce.csr.order.OrderNoteFormHandler;
import atg.commerce.order.Order;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.svc.agent.environment.EnvironmentTools;
import oms.commerce.order.OMSOrderManager;

/**
 * 
 * @author vsingh
 *
 */
public class MFFOrderNoteFormHandler extends OrderNoteFormHandler{
  
  private String orderId;
  private String storeNumber;
  
  public boolean handleAddCommentPPS(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws IOException, ServletException{
    
    vlogDebug("Entering MFFOrderNoteFormHandler.handleAddCommentPPS");

    EnvironmentTools envtools = getCSRAgentTools().getCSREnvironmentTools().getEnvironmentTools();
    OMSOrderManager orderMgr = (OMSOrderManager)getOrderManager();
    TransactionDemarcation td = null;
    boolean lRollback = true;
    
    try {
      td = new TransactionDemarcation();
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      
      if(Strings.isNullOrEmpty(getOrderId())){
        addFormException(new DropletException("OrderId cannot be null/empty"));
        return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
      }
      
      if(Strings.isNullOrEmpty(getComment())){
        addFormException(new DropletException("Comment cannot be null/empty"));
        return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
      }
      vlogDebug("Comment :{0}, Store: {1}, Order Id: {2}", getComment(),getStoreNumber(),getOrderId());
      Order lOmsOrder = null;
      try {
        lOmsOrder = orderMgr.loadOrder(getOrderId());
      } catch (CommerceException e) {
        vlogError (e, "Unable to load order ID: {0}", getOrderId());
        addFormException(new DropletException("Unable to load order " + getOrderId()));
        return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
      }
      
      try {
        orderMgr.createOrderComment(lOmsOrder,getComment(), envtools.getAgentProfile().getDataSource(), getStoreNumber());
      } catch (RepositoryException e) {
        addFormException(new DropletException("Unable to add comment to the order " + getOrderId()));
        return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
      }
      
      lRollback = false;
    } catch (TransactionDemarcationException e) {
      vlogError(e, "An exception occurred during adding comment from PPS");
     
    } finally {
      try {
        if (td != null) td.end(lRollback);
      } catch (Exception e) {
        vlogError(e, "A transaction exception occurred while trying to end the transaction");
      }
     
    }

    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String pOrderId) {
    orderId = pOrderId;
  }

public String getStoreNumber() {
	return storeNumber;
}

public void setStoreNumber(String pStoreNumber) {
	storeNumber = pStoreNumber;
}
  

}
