package oms.dropship.processor;

import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;

import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import mff.dropship.loadAccept.xsd.Detail;
import mff.dropship.loadAccept.xsd.LineItem;
import mff.dropship.loadAccept.xsd.Message;
import mff.dropship.loadAccept.xsd.SalesOrderSuccess;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.MFFOMSOrderManager;

public class ProcProcessDropshipMessages extends GenericService implements PipelineProcessor {

  private final static int  SUCCESS = 1;

  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }
  
  @SuppressWarnings("rawtypes")
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    vlogDebug("Entering ProcProcessDropshipMessages - runProcess");
    
    Map lParams = (Map) pPipelineParams;
    Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    Object message = lParams.get(AllocationConstants.PIPELINE_PARAMETER_DROP_SHIP_MESSAGE);
    
   if(lOrder != null && message != null && message instanceof Message ){
      Message lMessage = (Message)message;
      processAcceptMessage(lMessage,lOrder);
    }
    
    return SUCCESS;
  }
  
  private void processAcceptMessage(Message pMessage,Order pOrder) throws Exception{
    
    vlogDebug("Entering processAcceptMessage for orderId {0}",pOrder.getId());
    
    SalesOrderSuccess success = pMessage.getSalesOrderSuccess();
    Detail details = success.getDetail();
    for(LineItem lLineItem : details.getLineItem()){
      String lineNo = "ci" + lLineItem.getLineNo(); // commerceItemId
      String lineStatus = lLineItem.getLineStatus();
      if(lineStatus.equalsIgnoreCase("ACCEPTED")){
        //get the commerceItem and update the status
        MFFCommerceItemImpl lCommerceItem = getCommerceItem(pOrder, lineNo);
        lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.DROP_SHIP_ACCEPTED));
      }
    }
    
    getOmsOrderManager().updateOrder(pOrder);
  }
  
  /**
   * Get the commerce item from the order given the Commerce Item ID.
   * 
   * @param pOrder
   *            ATG Order
   * @param lCommerceItemId
   *            Commerce Item Id
   * @return Commerce item
   * @throws Exception
   */
  protected MFFCommerceItemImpl getCommerceItem(Order pOrder, String lCommerceItemId) throws Exception {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    MFFCommerceItemImpl lCommerceItem;
    try {
      lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lCommerceItemId);
    } catch (CommerceItemNotFoundException ex) {
      String lErrorMessage = String.format("Commerce item not found for order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Commerce item not found for order number: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    }
    return lCommerceItem;
  }
  
  MFFOMSOrderManager  mOmsOrderManager;

  public MFFOMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
    this.mOmsOrderManager = pOmsOrderManager;
  }

}
