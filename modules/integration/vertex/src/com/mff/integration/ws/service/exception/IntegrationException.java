package com.mff.integration.ws.service.exception;

/**
 * <p>
 * The exception thrown by all of the Web Services. 
 * 
 */
public class IntegrationException extends Exception {
	
	private static final long serialVersionUID = 1L;		// Version 
	private int mErrorId;									// Error ID associated with this exception
	private boolean mTemporaryError;						// Indicates if the error is temporary
	
	/**
	 * Default Constructor
	 */
	public IntegrationException () {
		super();
	}
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * @param pMessage
	 * 		Exception description 
	 */
	public IntegrationException (String pMessage) {
		super (pMessage);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * @param pMessage
	 * 		Exception Text
	 * @param pCause
	 * 		The cause of the exception	
	 */
	public IntegrationException (String pMessage, Exception pCause) {
		super (pMessage, pCause);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 * @param pCause
	 * 		The cause of the exception
	 */
	public IntegrationException (Exception pCause) {
		super (pCause);
	}
	
	/**
	 * Retrieves the error Id associated with this exception.
	 * @return
	 * 		Error ID from the Web Service call
	 */
	public int getErrorId() {
		return mErrorId;
	}

	/**
	 * Sets the error id associated with this exception.
	 * @param pErrorId
	 * 		Error ID from the Web Service call
	 */
	public void setErrorId(int pErrorId) {
		this.mErrorId = pErrorId;
	}

	/**
	 * Returns boolean flag to indicate if this is a temporary error.
	 * @return
	 * 		Boolean flag to indicate if this is a temporary error
	 */
	public boolean isTemporaryError() {
		return mTemporaryError;
	}

	/**
	 * Sets a boolean flag to indicate if the error is temporary.
	 * @param pTemporaryError
	 * 		Boolean flag to indicate if this is a temporary error
	 */
	public void setTemporaryError(boolean pTemporaryError) {
		this.mTemporaryError = pTemporaryError;
	}

	public String toString() {
		String retValue = super.toString();
		if (getCause() != null) {
			retValue += " (cause: " + getCause() + ")";
		}
		return retValue;
	}
}
