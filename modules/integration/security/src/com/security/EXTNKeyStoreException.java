package com.security;

/**
 * A custom exception for key store related tasks.
 */
public class EXTNKeyStoreException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor left here for any possible future additions.
	 *
	 * @param pErrorMessage
	 * @param pThrowable
	 */
	public EXTNKeyStoreException(String pErrorMessage, Throwable pThrowable) {
		super(pErrorMessage, pThrowable);
	}

	/**
	 * Constructor left here for any possible future additions.
	 *
	 * @param pErrorMessage
	 */
	public EXTNKeyStoreException(String pErrorMessage) {
		super(pErrorMessage);
	}

	/**
	 * Constructor left here for any possible future additions.
	 *
	 * @param pThrowable
	 */
	public EXTNKeyStoreException(Throwable pThrowable) {
		super(pThrowable);
	}
}
