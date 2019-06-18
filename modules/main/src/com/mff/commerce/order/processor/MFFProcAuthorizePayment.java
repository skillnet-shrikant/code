package com.mff.commerce.order.processor;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.constants.MFFConstants;

import atg.commerce.order.PaymentGroup;
import atg.commerce.order.processor.ProcAuthorizePayment;
import atg.service.pipeline.PipelineResult;

/**
 * The following class has been overriden to support the ability to display
 * custom error message for gift auth failure
 * 
 * @author savula
 *
 */
public class MFFProcAuthorizePayment extends ProcAuthorizePayment {

  @Override
  protected void addErrorToPipelineResult(PaymentGroup pFailedPaymentGroup, String pStatusMessage, PipelineResult pResult, ResourceBundle pBundle) {
    if (pFailedPaymentGroup instanceof MFFGiftCardPaymentGroup) {
      addGiftCardError((MFFGiftCardPaymentGroup) pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
    } else {
      super.addErrorToPipelineResult(pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
    }
  }

  /**
   * The following method checks for a specific error message returned by the
   * authorization processor and generates the appropriate error message to be
   * displayed.
   * 
   * @param pFailedPaymentGroup the gift card payment group
   * @param pStatusMessage the error message stored in the authorization status
   * @param pResult the pipeline result
   * @param pBundle the resource bundle to be used to generate the custom message
   */
  protected void addGiftCardError(MFFGiftCardPaymentGroup pFailedPaymentGroup, String pStatusMessage, PipelineResult pResult, ResourceBundle pBundle) {
    vlogDebug("Entering addGiftCardError : pFailedPaymentGroup, pStatusMessage, pResult, pBundle");
    
    if (pStatusMessage != null && pStatusMessage.equalsIgnoreCase(MFFConstants.GIFT_CARD_INSUFFICIENT_FUNDS)) {
      String lCardNumber = pFailedPaymentGroup.getCardNumber();
      String last4Digits = lCardNumber.substring(lCardNumber.length() - 4);
      
      vlogDebug("The last 4 digits on the gift card number is set to {0}", last4Digits);
      String errorKey = MFFConstants.GIFT_CARD_INSUFFICIENT_FUNDS;
      String errorMessage = MessageFormat.format(pBundle.getString(MFFConstants.GIFT_CARD_INSUFFICIENT_FUNDS), last4Digits);
      
      vlogDebug("The generated error message {0} for the error key {1}", errorMessage, errorKey);
      pResult.addError(errorKey, errorMessage);
    } else {
      addPaymentGroupError(pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
    }
    
    vlogDebug("Exiting addGiftCardError : pFailedPaymentGroup, pStatusMessage, pResult, pBundle");
  }

}
