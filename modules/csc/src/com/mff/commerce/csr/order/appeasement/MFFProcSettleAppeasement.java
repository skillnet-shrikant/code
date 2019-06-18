package com.mff.commerce.csr.order.appeasement;

import java.util.Map;

import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.csr.order.appeasement.processor.ProcSettleAppeasement;
import atg.service.pipeline.PipelineResult;

public class MFFProcSettleAppeasement extends ProcSettleAppeasement {

  @SuppressWarnings("rawtypes")
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
    Map params = (Map) pParam;
    Appeasement appeasement = (Appeasement) params.get("Appeasement");
    
    int result = super.runProcess(pParam, pResult);
    if(result == STOP_CHAIN_EXECUTION_AND_ROLLBACK){
      appeasement.setProcessed(false);
    }
    
    return result;
  }

}
