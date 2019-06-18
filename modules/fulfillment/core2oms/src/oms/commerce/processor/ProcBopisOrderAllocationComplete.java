package oms.commerce.processor;


import atg.service.pipeline.PipelineResult;


public class ProcBopisOrderAllocationComplete extends EXTNPipelineProcessor{

	
	private boolean mEnable;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int runProcess(Object pParam, PipelineResult pArg1) throws Exception {

		return CONTINUE;
	}
	public boolean isEnable() {
		return mEnable;
	}
	public void setEnable(boolean pEnable) {
		mEnable = pEnable;
	}

}
