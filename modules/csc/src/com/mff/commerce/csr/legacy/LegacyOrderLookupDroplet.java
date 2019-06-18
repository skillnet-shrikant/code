package com.mff.commerce.csr.legacy;

import java.io.IOException;

import javax.servlet.ServletException;

import com.mff.account.order.bean.MFFOrderDetails;
import com.mff.commerce.order.MFFOrderDetailHelper;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class LegacyOrderLookupDroplet extends DynamoServlet{
  
  private MFFOrderDetailHelper mOrderDetailHelper;
  
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("LegacyOrderLookupDroplet :: service () :: START");
      
    String orderId = (String) pRequest.getParameter(MFFConstants.ORDER_ID);
    
    vlogDebug("LegacyOrderLookupDroplet : orderId -  {0}",orderId );
    
    RepositoryItem orderItem;
    MFFOrderDetails mffOrderDetailObj = new MFFOrderDetails();
    try {
      orderItem = getOrderDetailHelper().getOrderItemById(orderId,"true");
    
      if (orderItem!=null) {
        getOrderDetailHelper().fillOrderDetailsByOrderId(orderItem, mffOrderDetailObj);
      }else {
        pRequest.setParameter("errorMsg", "NoSuchOrder");
        pRequest.serviceLocalParameter("error", pRequest, pResponse);
        return;
      }
      
    } catch (CommerceException e) {
      vlogError("Commerce Exception while fetching order: " + e, e);
    } catch (RepositoryException e) {
      vlogError("Repository Exception while fetching order: " + e, e);
    }
    
    pRequest.setParameter("result", mffOrderDetailObj);
    pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
    vlogDebug("LegacyOrderLookupDroplet :: service () :: END");
    return;
  }

  public MFFOrderDetailHelper getOrderDetailHelper() {
    return mOrderDetailHelper;
  }

  public void setOrderDetailHelper(MFFOrderDetailHelper pOrderDetailHelper) {
    mOrderDetailHelper = pOrderDetailHelper;
  }
  
  

}
