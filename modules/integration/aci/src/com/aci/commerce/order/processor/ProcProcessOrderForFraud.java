package com.aci.commerce.order.processor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.aci.commerce.order.AciOrder;
import com.aci.commerce.service.AciService;
import com.aci.constants.AciConstants;
import com.aci.pipeline.exception.AciPipelineException;

import atg.commerce.order.OrderManager;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class ProcProcessOrderForFraud extends GenericService implements PipelineProcessor {

    private final int SUCCESS = 1;
   
    private String mLoggingIdentifier;
    private AciService mFraudService;
    
    public AciService getFraudService(){
    	return mFraudService;
    }
    
    public void setFraudService(AciService pFraudService){
    	mFraudService=pFraudService;
    }
    
	public ProcProcessOrderForFraud()
    {
		mLoggingIdentifier = "ProcProcessOrderForFraud";
    }
	
    public void setLoggingIdentifier(String pLoggingIdentifier)
    {
    	mLoggingIdentifier = pLoggingIdentifier;
    }

    public String getLoggingIdentifier()
    {
    	return mLoggingIdentifier;
    }
	
	@Override
	public int[] getRetCodes() {
		int ret[] = {SUCCESS};
		return ret;
		
	}

	@Override
	public int runProcess(Object pParam, PipelineResult result) throws Exception {
		vlogDebug("ProcProcessOrderForFraud:runProcess:Start");
		try{
			HashMap map = (HashMap)pParam;
			AciOrder aciOrder = (AciOrder)map.get(AciConstants.ACI_PIPELINE_ORDER_PARAM_NAME);
			if(aciOrder == null)
				throw new AciPipelineException(AciConstants.ACI_ORDER_NOT_FOUND, true);
			synchronized(aciOrder){
				OrderManager orderManager = (OrderManager)map.get("OrderManager");
				if(orderManager!=null){
					String fraudStat=getFraudService().screenForFraud(aciOrder);
					if(!StringUtils.isEmpty(fraudStat)){
						aciOrder.setFraudStat(fraudStat);
						orderManager.updateOrder(aciOrder);
					}
					else {
						vlogWarning("ProcProcessOrderForFraud:runProcess:Fraud status obtained is empty");
						vlogDebug("ProcProcessOrderForFraud:runProcess:End");
						return SUCCESS;
					}
				}
				else {
					vlogWarning("ProcProcessOrderForFraud:runProcess:Order manager is empty no fraud check performed");
					vlogDebug("ProcProcessOrderForFraud:runProcess:End");
					return SUCCESS;
				}
				vlogDebug("ProcProcessOrderForFraud:runProcess:End");
				return SUCCESS;
			}
		}
		catch(Exception ex){
			vlogError(ex,"ProcProcessOrderForFraud:runProcess:Order Update errored out : End");
			return SUCCESS;
		}
	}

}
