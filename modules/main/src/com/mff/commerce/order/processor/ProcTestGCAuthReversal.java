 package com.mff.commerce.order.processor;

/*
 * Caution: 1) Use this Processor for Testing Purpose ONLY
 *          2) Make sure to set TestVoidTransaction property of this component to false all environments
 *            (Production, Staging etc..), if you do not want to test
 * Purpose of this class is to Provide ability to test Void Transaction
 * This class will throw an exception which causes pipeline to breaks
 * We catch this exception and perform a test voidTransaction
 */
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * @author DMI
 * 
 */
public class ProcTestGCAuthReversal extends ApplicationLoggingImpl implements
		PipelineProcessor {

	private static final int SUCCESS = 1;

	protected int mRetCodes[] = { SUCCESS };

	private boolean mTestVoidTransaction = true;

	@Override
	public int[] getRetCodes() {
		return mRetCodes;
	}

	@Override
	public int runProcess(Object pArg0, PipelineResult pArg1) throws Exception {

		if (getTestVoidTransaction())
			throw new Exception("Testing the void transaction");

		return SUCCESS;
	}

	/**
	 * @return the testVoidTransaction
	 */
	public boolean getTestVoidTransaction() {
		return mTestVoidTransaction;
	}

	/**
	 * @param testVoidTransaction
	 *            the testVoidTransaction to set
	 */
	public void setTestVoidTransaction(boolean testVoidTransaction) {
		mTestVoidTransaction = testVoidTransaction;
	}

}
