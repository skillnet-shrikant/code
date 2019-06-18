/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.processor;

import java.math.BigDecimal;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;

/**
 * Base class for all the processors which are part of the pick response processing
 */
abstract public class EXTNPipelineProcessor extends ApplicationLoggingImpl implements
		PipelineProcessor 
{
    protected final int CONTINUE = 1;
    protected final int BREAK = 2;
    protected final int SHIPPED = 3;
    protected final int CANCELLED = 4;
    protected final int RETURN = 5;
    
	/**
     * Default Constructor
     */
    public EXTNPipelineProcessor() {
        super();
    	this.setLoggingIdentifier(this.getClass().getName());
    }
	/* (non-Javadoc)
	 * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
	 */
	public int[] getRetCodes() {
    	int[] ret = {CONTINUE, BREAK, SHIPPED, CANCELLED, RETURN};
        return ret;
	}
	
	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logDebug(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logDebug(String message, Throwable throwable)
	{
		if (isLoggingDebug())
			super.logDebug(message, throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logDebug(java.lang.String)
	 */
	@Override
	public void logDebug(String message)
	{
		if (isLoggingDebug())
			super.logDebug(message);
	}

	@Override
	public void vlogDebug(String pFormat, Object... pArgs) {
		if (isLoggingDebug())
			super.vlogDebug(pFormat, pArgs);
	}
	
	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logDebug(java.lang.Throwable)
	 */
	@Override
	public void logDebug(Throwable throwable)
	{
		if (isLoggingDebug())
			super.logDebug(throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logError(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logError(String message, Throwable throwable)
	{
		if (isLoggingError())
			super.logError(message, throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logError(java.lang.String)
	 */
	@Override
	public void logError(String message)
	{
		if (isLoggingError())
			super.logError(message);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logError(java.lang.Throwable)
	 */
	@Override
	public void logError(Throwable throwable)
	{
		if (isLoggingError())
			super.logError(throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logInfo(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logInfo(String message, Throwable throwable)
	{
		if (isLoggingInfo())
			super.logInfo(message, throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logInfo(java.lang.String)
	 */
	@Override
	public void logInfo(String message)
	{
		if (isLoggingInfo())
			super.logInfo(message);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logInfo(java.lang.Throwable)
	 */
	@Override
	public void logInfo(Throwable throwable)
	{
		if (isLoggingInfo())
			super.logInfo(throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logTrace(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logTrace(String message, Throwable throwable)
	{
		if (isLoggingTrace())
			super.logTrace(message, throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logTrace(java.lang.String)
	 */
	@Override
	public void logTrace(String message)
	{
		if (isLoggingTrace())
			super.logTrace(message);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logTrace(java.lang.Throwable)
	 */
	@Override
	public void logTrace(Throwable throwable)
	{
		if (isLoggingTrace())
			super.logTrace(throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logWarning(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logWarning(String message, Throwable throwable)
	{
		if (isLoggingWarning())
			super.logWarning(message, throwable);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logWarning(java.lang.String)
	 */
	@Override
	public void logWarning(String message)
	{
		if (isLoggingWarning())
			super.logWarning(message);
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#logWarning(java.lang.Throwable)
	 */
	@Override
	public void logWarning(Throwable throwable)
	{
		if (isLoggingWarning())
			super.logWarning(throwable);
	}
	
	/**
	 * Rounds price to 2 places of decimal
	 * @param pNumber
	 * @return
	 */
	public static double roundPrice(double pNumber){
		BigDecimal bd = new BigDecimal(Double.toString(pNumber));
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
}