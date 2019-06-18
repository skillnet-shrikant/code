package oms.commerce.salesaudit.job;

import oms.commerce.jobs.OMSSingletonScheduleTask;
import oms.commerce.salesaudit.exception.SalesAuditException;
import oms.commerce.salesaudit.task.SalesAuditExtract;

/**
 * This is a scheduled process that will run the Sales Audit extract task
 * to create the flat file extract for Oracle Retail Sales Audit.
 *
 * @author DMI Inc.
 * 
 */
public class SalesAuditScheduler 
  extends OMSSingletonScheduleTask {

  @Override
  protected void performTask() {
    vlogInfo("Start SalesAuditScheduler ...");
    
    // Exit If scheduler is not enabled
    if (!getSchedulerEnabled()) {
      vlogWarning("SalesAuditScheduler is not enabled, Sales Audit task will not run");
      return;
    }
    
    // Run Sales Audit extract    
    try {
      runSalesAuditExtract ();
    } catch (SalesAuditException ex) {
      vlogError ("Sales Audit Extract failed with Error " + ex.getMessage());
      return;
    }

    vlogInfo("End SalesAuditScheduler ...");
  } 
  
  /**
   * Run the sales audit extract task to create the feed file for the 
   * Oracle Retail Sales Audit system.
   * @throws SalesAuditException 
   */
  protected void runSalesAuditExtract () 
      throws SalesAuditException {
    vlogDebug("Start Sales Audit Extract ...");
    getSalesAuditExtract().performTask();
    vlogDebug("End Sales Audit Extract ...");
  }

  // ***************************************************************
  //              Getter/Setter Methods 
  // ***************************************************************
  SalesAuditExtract mSalesAuditExtract;
  public SalesAuditExtract getSalesAuditExtract() {
    return mSalesAuditExtract;
  }
  public void setSalesAuditExtract(SalesAuditExtract pSalesAuditExtract) {
    this.mSalesAuditExtract = pSalesAuditExtract;
  }  
  
  
}