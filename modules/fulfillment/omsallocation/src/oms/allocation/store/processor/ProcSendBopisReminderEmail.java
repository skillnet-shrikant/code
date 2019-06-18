package oms.allocation.store.processor;

import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.email.MFFEmailManager;

import atg.commerce.order.InvalidParameterException;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;

public class ProcSendBopisReminderEmail extends GenericService implements PipelineProcessor {

  private final static int SUCCESS      = 1;
  
  public int[] getRetCodes() {
    int[] ret = {SUCCESS};
    return ret;
  }
  
  @SuppressWarnings({"rawtypes" })
  public int runProcess (Object pPipelineParams, PipelineResult pPipelineResults) 
    throws Exception {
    vlogDebug("Entering ProcSendBopisReminderEmail - runProcess");
    
    if (isSuppressEmail()) {
      vlogDebug ("Email send has been suppressed");
      return SUCCESS;
    }
    
    Map lParams             = (Map) pPipelineParams;
    MFFOrderImpl lOrder     = (MFFOrderImpl) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    boolean isFirstReminder = (boolean)lParams.get(AllocationConstants.PIPELINE_PARAMETER_IS_FIRST_REMINDER);
    
    if (lOrder == null) 
      throw new InvalidParameterException("Ther Order was not passed to ProcSendBopisReminderEmail");
    
    // send Reminder using Email Manager
    try {
      if(isFirstReminder) {
        getEmailManager().sendBopisReminderEmail(lOrder);
      } else {
        getEmailManager().sendBopisReminder2Email(lOrder);
      }
    } catch (Exception e) {
      vlogError(e,"Error while sending bopis reminder email for order {0}",lOrder.getOrderNumber());
      // stop chain execution
      return STOP_CHAIN_EXECUTION;
    }
    
    
    vlogDebug("Exiting ProcSendBopisReminderEmail - runProcess");
    return SUCCESS;
  }
  
  private MFFEmailManager mEmailManager;
  
  public MFFEmailManager getEmailManager() {
    return mEmailManager;
  }

  public void setEmailManager(MFFEmailManager pEmailManager) {
    mEmailManager = pEmailManager;
  }
  
  boolean mSuppressEmail;

  public boolean isSuppressEmail() {
    return mSuppressEmail;
  }

  public void setSuppressEmail(boolean pSuppressEmail) {
    this.mSuppressEmail = pSuppressEmail;
  }

}
