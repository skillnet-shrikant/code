package com.security.crypto;

/**
 * An exception representing a problem with encryption/decryption.
 *
 * @author Peter Goetsch (pgoetsch@knowledgepath.com)
 */
public class EXTNEncryptionException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor left here for any possible future additions.
	 *
	 * @param pArg0
	 * @param pArg1
	 */
	public EXTNEncryptionException(String pArg0, Throwable pArg1) {
		super(pArg0, pArg1);
	}

	/**
	 * Constructor left here for any possible future additions.
	 *
	 * @param pArg0
	 */
	public EXTNEncryptionException(String pArg0) {
		super(pArg0);
	}

	/**
	 * Constructor left here for any possible future additions.
	 *
	 * @param pArg0
	 */
	public EXTNEncryptionException(Throwable pArg0) {
		super(pArg0);
	}
}
