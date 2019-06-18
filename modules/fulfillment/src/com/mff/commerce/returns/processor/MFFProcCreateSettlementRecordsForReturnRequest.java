package com.mff.commerce.returns.processor;

import java.util.Map;

import com.mff.commerce.returns.MFFReturnManager;

import atg.commerce.csr.returns.ReturnRequest;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class MFFProcCreateSettlementRecordsForReturnRequest extends GenericService implements PipelineProcessor {

  private int SUCCESS = 1;
  protected int[] retCodes = {SUCCESS};
  
  @Override
  public int[] getRetCodes() {
    return retCodes;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
    Map lParams = (Map)pParam;
    MFFReturnManager returnManager = (MFFReturnManager) lParams.get("ReturnManager");
    ReturnRequest returnRequest = (ReturnRequest) lParams.get("ReturnRequest");
    returnManager.createSettlements(returnRequest);
    return SUCCESS;
  }

}
