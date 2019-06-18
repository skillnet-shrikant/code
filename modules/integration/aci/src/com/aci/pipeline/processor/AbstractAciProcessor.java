package com.aci.pipeline.processor;


import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;

import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public abstract class AbstractAciProcessor extends GenericService implements PipelineProcessor {


	protected static final int SUCCESS = 1;
	protected static final int ERROR = 2;
	private static final int[] RET_CODES = {SUCCESS, ERROR};
	
	@Override
	public int[] getRetCodes() {
		return RET_CODES;
	}

	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws AciPipelineException,RepositoryException {
		if (pParam == null || !(pParam instanceof AciPipelineProcessParam) || pResult == null) {
			throw new AciPipelineException("Pipeline parameters not set or incorrectly set");
		}
			vlogDebug("AbstractAciProcessor:runProcess:Start");
			AciPipelineResult result=(AciPipelineResult)pResult;
			int feedback= runAciProcess((AciPipelineProcessParam)pParam,result);
			addExtendedProperties((AciPipelineProcessParam)pParam);
			pResult=result;
			vlogDebug("AbstractAciProcessor:runProcess:End");
			return feedback;
	}
	
	
	protected abstract int runAciProcess(AciPipelineProcessParam pParams, AciPipelineResult pResult) throws AciPipelineException;
	
	protected abstract void addExtendedProperties(AciPipelineProcessParam pParams) throws AciPipelineException,RepositoryException;

}
