package com.mff.commerce.processor;

import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.email.MFFEmailManager;

import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.order.Order;
import atg.service.pipeline.PipelineResult;
import oms.commerce.processor.EXTNPipelineProcessor;

public class ProcSendAppeasementEmail extends EXTNPipelineProcessor {
  private MFFEmailManager mEmailManager;
  
  @SuppressWarnings("rawtypes")
  @Override
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResult) throws Exception {
    
    Map lParams =(Map) pPipelineParams;
    Appeasement lAppeasement = (Appeasement)lParams.get("Appeasement");
    Order lOrder = (Order)lParams.get("Order");
    
    MFFOrderImpl lOrderImpl = (MFFOrderImpl)lOrder;
    if(lAppeasement != null){
      getEmailManager().sendAppeasementEmail(lOrderImpl,lAppeasement);
    }
    return CONTINUE;
  }

  public MFFEmailManager getEmailManager() {
    return mEmailManager;
  }

  public void setEmailManager(MFFEmailManager pEmailManager) {
    mEmailManager = pEmailManager;
  }

}
