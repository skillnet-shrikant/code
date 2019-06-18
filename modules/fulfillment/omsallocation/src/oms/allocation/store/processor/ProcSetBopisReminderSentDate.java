package oms.allocation.store.processor;

import java.util.Date;
import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.order.InvalidParameterException;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.OMSOrderManager;

public class ProcSetBopisReminderSentDate extends GenericService implements PipelineProcessor {

  private final static int SUCCESS      = 1;
  
  public int[] getRetCodes() {
    int[] ret = {SUCCESS};
    return ret;
  }
  
  @SuppressWarnings({"rawtypes" })
  public int runProcess (Object pPipelineParams, PipelineResult pPipelineResults) 
    throws Exception {
    vlogDebug("Entering ProcUpdateBopisReminderSentDate - runProcess");
    
    Map lParams             = (Map) pPipelineParams;
    MFFOrderImpl lOrder     = (MFFOrderImpl) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    boolean isFirstReminder = (boolean)lParams.get(AllocationConstants.PIPELINE_PARAMETER_IS_FIRST_REMINDER);
    
    if (lOrder == null) 
      throw new InvalidParameterException("Ther Order was not passed to ProcUpdateBopisReminderSentDate");
    if(isFirstReminder) {
      lOrder.setBopisReminderSentDate(new Date());
    } else {
      lOrder.setBopisSecondReminderSentDate(new Date());
    }
    getOmsOrderManager().updateOrder(lOrder);
    
    vlogDebug("Exiting ProcUpdateBopisReminderSentDate - runProcess");
    return SUCCESS;
  }
  
  private OMSOrderManager mOmsOrderManager;

  public OMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(OMSOrderManager pOmsOrderManager) {
    mOmsOrderManager = pOmsOrderManager;
  }
  

}
