package oms.commerce.processor;

import java.util.HashMap;
import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.order.OrderImpl;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import oms.commerce.order.OMSOrderConstants;

public class ProcBopisOrderAllocation extends EXTNPipelineProcessor{

	private static final String PIPELINE_PARAMETER_ORDER ="omsOrder";
	public static final String PIPELINE_ALLOCATE_ORDER 	 = "handleAllocateOrder";
	
	private boolean mEnable;
	
	/**  Fulfillment pipeline Manager **/ 
	private PipelineManager mFulfillmentPipelineManager;
	public PipelineManager getFulfillmentPipelineManager() {
		return mFulfillmentPipelineManager;
	}
	public void setFulfillmentPipelineManager(PipelineManager pFulfillmentPipelineManager) {
		mFulfillmentPipelineManager = pFulfillmentPipelineManager;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int runProcess(Object pParam, PipelineResult pArg1) throws Exception {

		Map pipelineParams = (Map) pParam;
		OrderImpl omsOrder = (OrderImpl) pipelineParams.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
		boolean isBopisOrder = ((MFFOrderImpl)omsOrder).isBopisOrder();
		if(isEnable() && isBopisOrder) {
			String lOrderNumber = ((MFFOrderImpl)omsOrder).getOrderNumber();
			String mOrderId=omsOrder.getId();
			Map lPipelineParams = new HashMap();
			lPipelineParams.put(PIPELINE_PARAMETER_ORDER, omsOrder); 
			try {
				vlogInfo ("Calling allocation pipeline: {0} for order number {1}, orderId {2}", PIPELINE_ALLOCATE_ORDER, lOrderNumber,mOrderId);
				PipelineResult pResult = getFulfillmentPipelineManager().runProcess(PIPELINE_ALLOCATE_ORDER, lPipelineParams);
				if (pResult.hasErrors())
				{
					vlogError("Errors found running allocation for order Id {0}", mOrderId);
					Object[] keys = pResult.getErrorKeys();
			        for (int i = 0; i < keys.length; i++)
			        {
			          vlogError ("Unable to allocate order with error {0}", pResult.getError(keys[i]));
			        }
				}
				else
				{
					vlogInfo ("Successfully allocated order number: {0} orderId : {1}", lOrderNumber,mOrderId);
				}
			}
			catch (RunProcessException ex)	{
				vlogError(ex, "Error allocating order number {0} orderId : {1}", lOrderNumber,mOrderId);
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
