package com.firstdata.payment.processor;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardInfo;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class ProcFirstDataPreprocessorPaymentGroup extends GenericService implements PipelineProcessor {

  
  private String mGiftCardInfoClass;



  public static final int SUCCESS = 1;
  public int[] getRetCodes() {
    int retCodes[] = {SUCCESS};
    return retCodes;
  }

  public int runProcess(Object arg0, PipelineResult arg1) throws Exception {
    vlogDebug("ProcFirstDataPreprocessorPaymentGroup: runProcess: MFFGiftCardInfo Object Creation Started");
    
    PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs) arg0;
    // create MFFGiftCardInfo Object
    MFFGiftCardInfo gci = getGiftCardInfo();
    //
    MFFGiftCardPaymentGroup gcPG = (MFFGiftCardPaymentGroup) params.getPaymentGroup();
    //
    addDataToGiftCardInfo(gcPG,params,gci);
    // set MFFGiftCardInfo to Pipeline
    params.setPaymentInfo(gci);
    //
    vlogDebug("ProcFirstDataPreprocessorPaymentGroup: runProcess: MFFGiftCardInfo Object Creation End");

    
    return SUCCESS;
  }

  
  
  /*
   * add data to MFFGiftCardInfo Object
   */
  protected void addDataToGiftCardInfo(MFFGiftCardPaymentGroup pPaymentGroup,
      PaymentManagerPipelineArgs pParams, MFFGiftCardInfo pGiftCardInfo) {

    // Set Data to ProfitPointInfo Object
    pGiftCardInfo.setAmount(pParams.getAmount());
    pGiftCardInfo.setGiftCardNumber(pPaymentGroup.getCardNumber());
    pGiftCardInfo.setEan(pPaymentGroup.getEan());
    pGiftCardInfo.setPaymentId(pPaymentGroup.getId());
    
  }
  
  
  
  /*
   * Factory method to create a new instance of MFFGiftCardInfo class
   */
  protected MFFGiftCardInfo getGiftCardInfo() throws Exception

  {
    vlogDebug(
        "ProcFirstDataPreprocessorPaymentGroup: MFFGiftCardInfo: Creating MFFGiftCardInfo of Class Type: {0}",
        getGiftCardInfoClass());
    MFFGiftCardInfo gci = (MFFGiftCardInfo) Class.forName(this.getGiftCardInfoClass()).newInstance();
    vlogDebug("ProcFirstDataPreprocessorPaymentGroup: MFFGiftCardInfo:  Created MFFGiftCardInfo End");

    return gci;
  }

  public String getGiftCardInfoClass() {
    return mGiftCardInfoClass;
  }

  public void setGiftCardInfoClass(String pGiftCardInfoClass) {
    mGiftCardInfoClass = pGiftCardInfoClass;
  }
  
  
}
