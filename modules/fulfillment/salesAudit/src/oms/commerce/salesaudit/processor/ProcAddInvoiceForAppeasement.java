package oms.commerce.salesaudit.processor;

import java.util.List;
import java.util.Map;
import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.csr.appeasement.Appeasement;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.processor.EXTNPipelineProcessor;
import oms.commerce.salesaudit.util.InvoiceManager;

/**
 * This class will add a new invoice to the invoice repository when the store 
 * marks the item as shipped.  This is executed at the end of the submitAppeasement
 * pipeline chain. 
 * 
 * @author jvose
 *
 */
public class ProcAddInvoiceForAppeasement 
  extends EXTNPipelineProcessor {

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public int runProcess(Object pParam, PipelineResult pArg1)  
    throws Exception {
    vlogDebug("ProcAddInvoiceForAppeasement.runProcess - begin");

    // Get order from the pipeline parameters
    Map lPipelineParams       = (Map) pParam;
    Appeasement lAppeasement = (Appeasement)lPipelineParams.get("Appeasement");
    
    // Add new appeasement invoice
    getInvoiceManager().addAppeasementInvoice(lAppeasement);
    
    vlogDebug("ProcAddInvoiceForAppeasement.runProcess - end");
    return CONTINUE;
  }
  
  // *********************************************************
  //            Getter/setters
  // *********************************************************
  InvoiceManager mInvoiceManager;
  public InvoiceManager getInvoiceManager() {
    return mInvoiceManager;
  }
  public void setInvoiceManager(InvoiceManager pInvoiceManager) {
    this.mInvoiceManager = pInvoiceManager;
  }  
  
}