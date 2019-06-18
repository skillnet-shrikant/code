package oms.allocation.store.processor;

import java.util.Map;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.constants.MFFConstants;

import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.MFFOMSOrderManager;

/**
 * 
 * @author vsingh
 *
 */
public class ProcCreateAgentLog extends GenericService implements PipelineProcessor{
  
  private final static int  SUCCESS = 1;

  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }
  
  @SuppressWarnings({"rawtypes" })
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    
    vlogDebug("Entering ProcCreateAgentLog - runProcess");
    
    if(!isEnabled()){
      vlogInfo("Skipping ProcCreateAgentLog as enabled flag is set to false");
      return SUCCESS;
    }
    
    Map lParams         = (Map)   pPipelineParams;
    MFFOrderImpl lOrder = (MFFOrderImpl) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    String agentUserId  = (String)lParams.get(AllocationConstants.PIPELINE_PARAMETER_AGENT_USER_ID);
    
    if (Strings.isNullOrEmpty(agentUserId)) {
      agentUserId = "testAgent";
    }
    
    createAgentLogItem(lOrder.getOrderNumber(), agentUserId, "shipped");
    
    return SUCCESS;
  }
  
  private void createAgentLogItem(String pOrderNumber, String pAgentUserId, String pActionName){
    
    MutableRepositoryItem lMutableRepositoryItem = null;
    try {
      lMutableRepositoryItem = getOmsOrderRepository().createItem (MFFConstants.ITEM_DESC_AGENT_ACTION_LOG);
      
      lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_ACTION_LOG_AGENT_ID,pAgentUserId);
      lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_ACTION_LOG_ORDER_NUMBER,pOrderNumber);
      lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_ACTION_LOG_ACTION,pActionName);
      
      getOmsOrderRepository().addItem(lMutableRepositoryItem);
      
    } catch (Exception e) {     
      String lErrorMessage = String.format("Unable to create AgentLogItem - Order Number: %s ", pOrderNumber);
      vlogError (e, lErrorMessage);
    }
  }
  
  private MFFOMSOrderManager  mOmsOrderManager;
  
  private boolean enabled = true;
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean pEnabled) {
    enabled = pEnabled;
  }

  public MFFOMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
    this.mOmsOrderManager = pOmsOrderManager;
  }
  
  private MutableRepository getOmsOrderRepository(){
    return (MutableRepository)getOmsOrderManager().getOmsOrderRepository();
  }
  
  
  
 
  

}
