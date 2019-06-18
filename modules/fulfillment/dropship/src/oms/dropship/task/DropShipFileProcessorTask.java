package oms.dropship.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;
import mff.task.Task;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.OMSOrderManager;
import oms.util.fileprocessor.OMSFileProcessorUtil;

public abstract class DropShipFileProcessorTask extends Task{

  @Override
  public void doTask() {
    
    // get files to process
    File[] lFiles = getFileProcessorUtil().getFilesToProcess();
    
    if (lFiles.length < 1) {
      vlogInfo ("No files were found to process");
      return;     
    }
    
    for (File lFile : lFiles) {
       try {
         Object pMessage = getMessageToProcess(lFile);
         processMessage(pMessage);
        // move file to done directory
        getFileProcessorUtil().moveToDoneDirectory(lFile);
       } catch (Exception e) {
         vlogError(e,"Error processing fileName {0}", lFile.getName());
       }
    }
  }
  
  protected abstract Object getMessageToProcess(File pFile) throws Exception;
  
  protected abstract String getOrderNumberFromMessage(Object pMessage);
  
  @SuppressWarnings({ "rawtypes"})
  private void processMessage(Object pMessage) throws Exception{
    
    String orderNumber = getOrderNumberFromMessage(pMessage);
    
    if(Strings.isNullOrEmpty(orderNumber)){
      throw new Exception("Unable to get orderNumber from the message");
    }
    
    vlogDebug("Processing orderNumber {0} and invoking pipeline {1}",orderNumber,getPipelineName());
   
    // Get the order ID
    Order lOmsOrder = null;
    try {
      String orderId = getOmsOrderManager().getOrderIdByOrderNumber(orderNumber);
      lOmsOrder = getOmsOrderManager().loadOrder(orderId);
    } catch (CommerceException e) {
      String lErrorMessage = String.format("Unable to load for orderNumber: %s", orderNumber);
      vlogError(e, lErrorMessage);
      throw new Exception(lErrorMessage);
    }
    
    if(lOmsOrder != null){
      String pipelineName = getPipelineName();
      Map lPipelineParams = createPipelineMap(lOmsOrder, pMessage);
      try {
        getFulfillmentPipelineManager().runProcess(pipelineName, lPipelineParams);
      } catch (RunProcessException ex) {
        String lErrorMessage = String.format("Error running pipeline %s for order id %s", pipelineName, lOmsOrder.getId());
        vlogError(ex, lErrorMessage);
        throw new Exception(lErrorMessage);
      }
    }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected Map createPipelineMap(Order pOrder,Object pMessage)throws CommerceException{
    Map lPipelineParams = new HashMap();
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ORDER, pOrder);
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_DROP_SHIP_MESSAGE, pMessage);

    return lPipelineParams;
  }
  
  private String pipelineName;
  
  public String getPipelineName() {
    return pipelineName;
  }

  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
  }

  private PipelineManager fulfillmentPipelineManager;
  
  public PipelineManager getFulfillmentPipelineManager() {
    return fulfillmentPipelineManager;
  }
  public void setFulfillmentPipelineManager(
      PipelineManager fulfillmentPipelineManager) {
    this.fulfillmentPipelineManager = fulfillmentPipelineManager;
  }
  
  private OMSFileProcessorUtil fileProcessorUtil;

  public OMSFileProcessorUtil getFileProcessorUtil() {
    return fileProcessorUtil;
  }

  public void setFileProcessorUtil(OMSFileProcessorUtil fileProcessorUtil) {
    this.fileProcessorUtil = fileProcessorUtil;
  }
  
  private OMSOrderManager mOmsOrderManager;

  public OMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(OMSOrderManager pOmsOrderManager) {
    this.mOmsOrderManager = pOmsOrderManager;
  }
  
}
