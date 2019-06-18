package com.aci.pipeline.processor;


import atg.repository.RepositoryException;

import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;

public class ProcAddPromoCodeInfo extends AbstractAciProcessor {

	@Override
	protected int runAciProcess(AciPipelineProcessParam pParams,
			AciPipelineResult pResult) throws AciPipelineException {
		vlogDebug("ProcAddPromoCodeInfo:runAciProcess:Started");
		vlogDebug("ProcAddPromoCodeInfo:runAciProcess:End");
		return SUCCESS;
	}

	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException,AciPipelineException {
		
	}

}
