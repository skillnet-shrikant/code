package com.mff.commerce.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.prorate.MFFProrateItemManager;
import com.mff.constants.MFFConstants;
import com.mff.returns.MFFReturnItem;
import com.mff.returns.ReturnInput;

import atg.commerce.CommerceException;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.order.CommerceItem;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
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
public class ProcUpdateProrateItem extends GenericService implements PipelineProcessor {

  private final static int SUCCESS = 1;

  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }

  @SuppressWarnings({ "rawtypes" })
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {

    vlogDebug("Entering ProcUpdateProrateItem - runProcess");

    Map lParams = (Map) pPipelineParams;

    ReturnRequest returnRequest = (ReturnRequest) lParams.get("ReturnRequest");
    // Update prorate items based on the returnItems of the returnRequest
    updateStateOfProrateItems(returnRequest);
    
    /*MFFOrderImpl lOrder = (MFFOrderImpl) returnRequest.getOrder();
    Object retInput =  lParams.get("returnInputArray");
    if(retInput != null && retInput instanceof ReturnInput[]){
      ReturnInput[] returnInput = (ReturnInput[]) retInput;
      
      if(lOrder != null && returnInput != null){
        updateStateOfProrateItems(lOrder,returnInput);
      }else{
        vlogDebug("ProcUpdateProrateItem : skipped Processing update of prorate items");
      }
    }else{
      vlogInfo("ProcUpdateProrateItem : No returnInput array passed so skipped Processing update of prorate items");
    }*/
   

    return SUCCESS;
  }

  protected void updateStateOfProrateItems(MFFOrderImpl pOrder, ReturnInput[] pReturnInputArr) throws 
    NumberFormatException, CommerceException, RepositoryException {
    
    vlogDebug("Inside updateStateOfProrateItems");
    
    for (ReturnInput returnInp : pReturnInputArr) {
      List<String> inputLineNumbers = returnInp.getLinesReturnedList();
      if (inputLineNumbers != null && !inputLineNumbers.isEmpty()) {
        for (String lineNumber : inputLineNumbers) {
          RepositoryItem[] items = getProrateItemManager().getProrateItemByCIAndLineNumber(returnInp.getCommerceItemId(), Double.valueOf(lineNumber));
          if (items != null && items.length > 0) {
            
            String prorateItemState = (String)items[0].getPropertyValue(MFFConstants.PROPERTY_PRORATE_STATE);
            vlogDebug("updateStateOfProrateItems : commerceItemId : {0} lineNumber : {1} prorateItemState : {2}", 
                returnInp.getCommerceItemId(),lineNumber,prorateItemState);
            
            if(!prorateItemState.equalsIgnoreCase(MFFConstants.PRORATE_STATE_RETURN)){
              MutableRepositoryItem lMutableItem = getOmsOrderRepository().getItemForUpdate(items[0].getRepositoryId(), MFFConstants.ITEM_DESC_PRORATE_ITEM);
              lMutableItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_RETURN_DATE, new Date());
              lMutableItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_STATE, MFFConstants.PRORATE_STATE_RETURN);
              getOmsOrderRepository().updateItem(lMutableItem);
            }else{
              vlogInfo("ProrateItem is already returned so skipped updating for commerceItemId : {0} lineNumber : {1}", 
                  returnInp.getCommerceItemId(),lineNumber);
            }
          }else{
            vlogDebug("updateStateOfProrateItems : RepositoryItem doesn't exist for commerceItemId - {0} lineNumber - {1}",
                returnInp.getCommerceItemId(),lineNumber);
          }
        }
      }
    }
  }

  /**
   * updateStateOfProrateItems updates the prorateitems used to calculate refunds to return state
   * @param pReturnRequest the return request
   * @throws NumberFormatException, CommerceException, RepositoryException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void updateStateOfProrateItems(ReturnRequest pReturnRequest) throws 
  NumberFormatException, CommerceException, RepositoryException {

    vlogDebug("Inside updateStateOfProrateItems with ReturnRequest");
    
    List<MFFReturnItem> returnItemList = (List<MFFReturnItem>) pReturnRequest.getReturnItemList();
    for (MFFReturnItem retItem : returnItemList) 
    {
      // Look at the prorate items associated to each returned item in the return request
      if(null!=retItem.getProRatedItemIds())
      {
        for (Iterator iterator = retItem.getProRatedItemIds().iterator(); iterator.hasNext();)
        {
          String proRatedItemId = (String) iterator.next();
          MutableRepositoryItem prorateItem = (MutableRepositoryItem) getOmsOrderRepository().getItem(proRatedItemId, MFFConstants.ITEM_DESC_PRORATE_ITEM);
          if(prorateItem != null){
            String prorateItemStatus = (String) prorateItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_STATE);
            if(!prorateItemStatus.equalsIgnoreCase(MFFConstants.PRORATE_STATE_RETURN)){
              prorateItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_RETURN_DATE, new Date());
              prorateItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_STATE, MFFConstants.PRORATE_STATE_RETURN);
              getOmsOrderRepository().updateItem(prorateItem);
              vlogDebug("ProrateItem updated to returned for commerceItemId : {0} prorateItem id : {1}", 
                  retItem.getCommerceItem().getId(), proRatedItemId);
            }else{
              vlogInfo("ProrateItem is already returned so skipped updating for commerceItemId : {0} prorateItem id : {1}", 
                  retItem.getCommerceItem().getId(), proRatedItemId);
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected List<CommerceItem> getReturnedCommerceItems(ReturnRequest pReturnRequest) {
    List<CommerceItem> lCommerceItems = new ArrayList<CommerceItem>();
    List<MFFReturnItem> returnItemList = (List<MFFReturnItem>) pReturnRequest.getReturnItemList();
    for (MFFReturnItem retItem : returnItemList) {
      lCommerceItems.add(retItem.getCommerceItem());
    }
    return lCommerceItems;
  }

  private OMSOrderManager mOmsOrderManager;

  public OMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(OMSOrderManager pOmsOrderManager) {
    this.mOmsOrderManager = pOmsOrderManager;
  }

  private MutableRepository getOmsOrderRepository() {
    return (MutableRepository) getOmsOrderManager().getOmsOrderRepository();
  }
  
  private MFFProrateItemManager prorateItemManager;
  
  public MFFProrateItemManager getProrateItemManager() {
    return prorateItemManager;
  }

  public void setProrateItemManager(MFFProrateItemManager prorateItemManager) {
    this.prorateItemManager = prorateItemManager;
  }

}
