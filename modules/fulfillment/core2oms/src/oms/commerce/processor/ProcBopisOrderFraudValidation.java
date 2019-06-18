package oms.commerce.processor;

import java.util.Map;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.constants.MFFConstants;

import atg.commerce.order.OrderImpl;
import atg.service.pipeline.PipelineResult;
import oms.commerce.order.OMSOrderConstants;


public class ProcBopisOrderFraudValidation extends EXTNPipelineProcessor{

	private static final int PIPELINE_RESULT_HOLD_ALLOCATION=2;
	
	private boolean mEnable;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int runProcess(Object pParam, PipelineResult pArg1) throws Exception {

		Map pipelineParams = (Map) pParam;
		OrderImpl omsOrder = (OrderImpl) pipelineParams.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
		boolean isBopisOrder = ((MFFOrderImpl)omsOrder).isBopisOrder();
		if(isEnable() && isBopisOrder) {
			String fraudStatus = ((MFFOrderImpl)omsOrder).getFraudStat(); 
			if(!Strings.isNullOrEmpty(fraudStatus) && (fraudStatus.equalsIgnoreCase(MFFConstants.FRAUD_REJECT))){
			  return PIPELINE_RESULT_HOLD_ALLOCATION;
			}else if(!Strings.isNullOrEmpty(fraudStatus) && (fraudStatus.equalsIgnoreCase(MFFConstants.FRAUD_REVIEW))){
			  return PIPELINE_RESULT_HOLD_ALLOCATION;
			}
		}
		return CONTINUE;
	}
	public boolean isEnable() {
		return mEnable;
	}
	public void setEnable(boolean pEnable) {
		mEnable = pEnable;
	}

}
