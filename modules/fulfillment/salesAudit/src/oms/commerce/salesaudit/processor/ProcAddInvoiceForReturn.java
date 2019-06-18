package oms.commerce.salesaudit.processor;

import java.util.Map;
import atg.commerce.csr.returns.ReturnRequest;
import atg.service.pipeline.PipelineResult;
import oms.commerce.processor.EXTNPipelineProcessor;
import oms.commerce.salesaudit.util.InvoiceManager;

/**
 * This class will add a new invoice to the invoice repository when the customer
 * returns an item.  This processor will be called from the confirmReturnRequest
 * pipeline chain, which is the Commerce Pipeline.
 * 
 * @author jvose
 *
 */
public class ProcAddInvoiceForReturn 
  extends EXTNPipelineProcessor {
	
	boolean skipInvoice;
	
  public boolean isSkipInvoice() {
		return skipInvoice;
	}
	public void setSkipInvoice(boolean pSkipInvoice) {
		skipInvoice = pSkipInvoice;
	}

private static String POS_ORIGIN_OF_RETURN = "pos";
  
  @SuppressWarnings("rawtypes")
  @Override
  public int runProcess(Object pParam, PipelineResult pArg1)  
    throws Exception {
    vlogDebug("ProcAddInvoiceForReturn.runProcess - begin");

    // Get order from the pipeline parameters
    Map lPipelineParams       = (Map) pParam;
    ReturnRequest lReturnRequest = (ReturnRequest) lPipelineParams.get("ReturnRequest");
    
    if (lReturnRequest!= null && lReturnRequest.getOriginOfReturn().equalsIgnoreCase(POS_ORIGIN_OF_RETURN)) {
      vlogInfo("We do not create invoice/sales audit entry for POS returns - Order:{0}, return requestId:{1}",lReturnRequest.getOrder().getId(),lReturnRequest.getRequestId());
    } else {
      // Add new Return invoice
      if(!skipInvoice) {
    	  getInvoiceManager().addReturnsInvoice(lReturnRequest);
      }
    }
    vlogDebug("ProcAddInvoiceForReturn.runProcess - end");
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