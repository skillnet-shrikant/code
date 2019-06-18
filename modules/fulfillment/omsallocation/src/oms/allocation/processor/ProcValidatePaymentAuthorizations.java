/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.List;
import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.payment.MFFPaymentManager;

public class ProcValidatePaymentAuthorizations extends GenericService implements PipelineProcessor {

  private final static int  SUCCESS = 1;

  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }
	
	private boolean enableValidatePaymentAuthorizations = true;
	
	private MFFPaymentManager paymentManager;
	
	private OMSOrderManager orderManager;

	@SuppressWarnings({ "rawtypes", "unchecked"})
	@Override
	public int runProcess(Object pParams, PipelineResult pPipelineResult) throws Exception {
		vlogDebug("ProcValidatePaymentAuthorizations : Entering runProcess");
		
		Map lParams = (Map) pParams;
		if (isEnableValidatePaymentAuthorizations()) {
			
			Order lOrder = (Order) lParams.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
			List<String> lItemsToShip = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
			String orderNumber = ((MFFOrderImpl)lOrder).getOrderNumber();
			
			vlogDebug("ProcValidatePaymentAuthorizations : orderNumber {0} lItemsToShip : {1}", orderNumber,lItemsToShip);
			
			if(lItemsToShip != null && lItemsToShip.size() > 0){
			  boolean paymentAuthorizationValid = getPaymentManager().validateAndReauthorizePayments(lOrder);
  			vlogDebug("ProcValidatePaymentAuthorizations : orderNumber {0} paymentAuthorizationValid: {1}", orderNumber,paymentAuthorizationValid);
  			if (!paymentAuthorizationValid) {
  				vlogInfo("ProcValidatePaymentAuthorizations : Payment re-authorization failed for orderNumber {0}",orderNumber);
  				pPipelineResult.addError("reauthFailure", "Payment authorization failed for orderNumber " + orderNumber);
  				return STOP_CHAIN_EXECUTION_AND_COMMIT;
  			}
			}
		} 
		
		return SUCCESS;
	}

	public boolean isEnableValidatePaymentAuthorizations() {
		return enableValidatePaymentAuthorizations;
	}

	public void setEnableValidatePaymentAuthorizations(boolean enableValidatePaymentAuthorizations) {
		this.enableValidatePaymentAuthorizations = enableValidatePaymentAuthorizations;
	}

	public MFFPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(MFFPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public OMSOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(OMSOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
