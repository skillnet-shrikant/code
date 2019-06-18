package com.mff.commerce.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.constants.MFFConstants;

import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.commerce.order.OMSOrderManager;

/**
 * 
 * @author vsingh
 *
 */
public class ProcSetReturnItems extends ApplicationLoggingImpl implements PipelineProcessor {

  private final int SUCCESS = 1;
  
  /**
   * Default Constructor 
   */
  public ProcSetReturnItems() {
      super();
  }  
  
  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);

    // check for null parameters
    if (order == null) 
      throw new InvalidParameterException("Ther Order was not passed to ProcSetReturnItems");

    if (order.getCommerceItemCount() == 0) 
      return SUCCESS;
    
    loadReturnItems(order);
    return SUCCESS;
  }
  
  @SuppressWarnings({"unchecked" })
  private void loadReturnItems(Order pOrder) throws RepositoryException{
    MFFOrderImpl lOrder = (MFFOrderImpl)pOrder;
    List<CommerceItem> items = lOrder.getCommerceItems();
    
    for(CommerceItem commerceItem : items){
      MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) commerceItem;
    
      List<RepositoryItem> returnItems = new ArrayList<RepositoryItem>();
      if (lCommerceItem.getReturnItemIds() != null && lCommerceItem.getReturnItemIds().size() > 0) {
        Set<String> returnItemIds = lCommerceItem.getReturnItemIds();
        for(String returnRepId: returnItemIds){
          RepositoryItem prorateItem = getOmsOrderRepository().getItem(returnRepId,MFFConstants.ITEM_DESC_PRORATE_ITEM);
          if(prorateItem != null){
            returnItems.add(prorateItem);
          }
        }
      }
      lCommerceItem.setReturnItems(returnItems);
    }
  }
  
  private OMSOrderManager  mOmsOrderManager;

  public OMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(OMSOrderManager pOmsOrderManager) {
    this.mOmsOrderManager = pOmsOrderManager;
  }
  
  private MutableRepository getOmsOrderRepository(){
    return (MutableRepository)getOmsOrderManager().getOmsOrderRepository();
  }

}
