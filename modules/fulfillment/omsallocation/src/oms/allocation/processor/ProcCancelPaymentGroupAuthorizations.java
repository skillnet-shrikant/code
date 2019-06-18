package oms.allocation.processor;

import java.util.List;
import java.util.Map;

import com.aci.payment.creditcard.AciCreditCard;
import com.mff.commerce.states.MFFOrderStates;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.states.StateDefinitions;
import atg.payment.PaymentStatus;
import atg.payment.creditcard.CreditCardStatus;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.payment.MFFPaymentManager;
import oms.commerce.processor.EXTNPipelineProcessor;

public class ProcCancelPaymentGroupAuthorizations extends EXTNPipelineProcessor {

  MFFPaymentManager paymentManager;

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public int runProcess(Object pParams, PipelineResult pResult) throws Exception {
    vlogDebug("Entering runProcess : pParams, pResult");
    Map lParams = (Map) pParams;
    Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    Boolean isPartialCancellation=(Boolean) lParams.get(AllocationConstants.IS_PARTIAL_CANCELLATION);
    boolean isPartial=false;
    
    if(!isEnabled()){
      vlogInfo("Skipping ProcCancelPaymentGroupAuthorizations as enabled flag is set to false");
      return CONTINUE;
    }
    
    if(isPartialCancellation!=null){
    	isPartial=true;
    }
    // check to see if the order is in a cancelled state

    if (lOrder.getState() == StateDefinitions.ORDERSTATES.getStateValue(MFFOrderStates.CANCELLED)) {
      try {

        List<PaymentGroup> lPayments = lOrder.getPaymentGroups();
        for (PaymentGroup lPayment : lPayments) {
          if(lPayment.getAmountAuthorized() > 0){
            getPaymentManager().reverseAuthorization(lPayment,lPayment.getAmountAuthorized(),false);
          }
        }
      } catch (CommerceException e) {
        vlogWarning(e, "Could not reverse the payment authorization for a cancelation order id: {0}", lOrder.getId());
      }
    }
    else if(isPartial) {
    	vlogDebug("ProcCancelPaymentGroupAuthorizations: Partial Cancellation:");
    	Double cancellationAmount=(Double)lParams.get(AllocationConstants.TOTAL_CANCELLATION_PRICE);
    	double amountToReverse=cancellationAmount.doubleValue();
    	if(amountToReverse !=0.0d){
    		List<PaymentGroup> lPayments = lOrder.getPaymentGroups();
    		for (PaymentGroup lPayment : lPayments) {
    			if(lPayment instanceof AciCreditCard){
    			  if(lPayment.getAmountAuthorized() > 0){
    				double lPaymentAmount=lPayment.getAmount();
    				  double lAlreadyDebitedAmount=0.0d;
    			      for (PaymentStatus debitStatus : (List<PaymentStatus>) lPayment.getDebitStatus()) {
    			          if (debitStatus.getTransactionSuccess()) {
    			        	  lAlreadyDebitedAmount = lAlreadyDebitedAmount + debitStatus.getAmount();
    			          } else {
    			            // the debit was not successful so ignoring it
    			            vlogDebug("Payment group {0} ignoring debit since it was not successful");
    			          }
    			        }
    				double remainingAmount=lPaymentAmount-lAlreadyDebitedAmount-amountToReverse;
    				try {
    					if(remainingAmount>0.0d){
	    					CreditCardStatus status=getPaymentManager().reAuthorize(lOrder, lPayment, remainingAmount);
	    					if(status.getTransactionSuccess()){
	    						getPaymentManager().reverseAuthorization(lPayment,amountToReverse,true);
	    						getPaymentManager().postProcessReAuthorizeCancellation(lPayment, status, remainingAmount,amountToReverse);
	    					}
	    					else {
	    						throw new CommerceException("Cannot validate payment at this time. Please try again later");
	    					}
    					}
    					else {
	    							getPaymentManager().reverseAuthorization(lPayment,amountToReverse,true);
	    					  		double amountToSet=getPaymentManager().getPricingTools().round(remainingAmount+lAlreadyDebitedAmount);
	    					  		lPayment.setAmount(amountToSet);
									int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("settled");
									lPayment.setState(state);
    							
    					}
    				}
    				catch(Exception ex){
    					throw new CommerceException("Cannot validate payment at this time. Please try again later",ex);
    				}
    			    
    			  }
    			}
    	     }
    	}
    }
    
    vlogDebug("Exiting runProcess : pParams, pResult");
    return CONTINUE;
  }
  
  private boolean enabled = true;
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean pEnabled) {
    enabled = pEnabled;
  }

  public MFFPaymentManager getPaymentManager() {
    return paymentManager;
  }

  public void setPaymentManager(MFFPaymentManager pPaymentManager) {
    paymentManager = pPaymentManager;
  }

}
