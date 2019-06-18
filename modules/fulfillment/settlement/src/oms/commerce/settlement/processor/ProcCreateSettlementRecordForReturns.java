package oms.commerce.settlement.processor;

import atg.service.pipeline.PipelineResult;
import oms.commerce.processor.EXTNPipelineProcessor;

/**
 * The following processor is responsible for creating settlement records when a
 * shipment is processed. The pipeline requires the following parameters set in
 * the pipeline.
 * <ul>
 * <li>omsOrder - the order that is being processed
 * <li>itemsToShip - the list of commerce item ids that is being shipped
 * </ul>
 * 
 * <b> Assumptions: </b>
 * <ul>
 * <li>When a commerce item id is shipped all the quantities are shipped
 * </ul>
 * 
 * @author savula
 *
 */
public class ProcCreateSettlementRecordForReturns extends EXTNPipelineProcessor {

  @Override
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    
    return CONTINUE;
  }

}
