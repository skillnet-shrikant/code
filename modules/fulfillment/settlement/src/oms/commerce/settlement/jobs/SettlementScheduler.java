package oms.commerce.settlement.jobs;

import oms.commerce.jobs.OMSSingletonScheduleTask;
import oms.commerce.settlement.SettlementManager;

/**
 * The following method will be used to kick off the settlement process that
 * runs on a nightly basis.
 * 
 * @author savula
 *
 */
public class SettlementScheduler extends OMSSingletonScheduleTask {

  private SettlementManager settlementManager;
  
  @Override
  protected void performTask() {
    if (getSchedulerEnabled()) {
      vlogInfo("SettlementScheduler begin");
      
      getSettlementManager().processSettlementRecords();
      
      vlogInfo("SettlementScheduler end");
    }
  }

  public SettlementManager getSettlementManager() {
    return settlementManager;
  }

  public void setSettlementManager(SettlementManager pSettlementManager) {
    settlementManager = pSettlementManager;
  }

}
