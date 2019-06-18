package com.security.crypto;

import java.io.Serializable;

import com.security.EXTNSecurityConstants;

import atg.core.util.Base64;
import atg.nucleus.GenericService;

/**
 * An abstract class which further defines encryption logic.
 *
 * @author Peter Goetsch (pgoetsch@knowledgepath.com)
 */
public abstract class AbstractEncryptor extends GenericService implements EXTNEncryptor, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private transient boolean mInitialized = false;

	private final transient Object mMonitor = new Object();

	//###########################################
	//		Implemented from EXTNEncryptor
	//###########################################

	/*
	 * An abstract implementation of EXTNEncryptor's encrypt method. The init() method is first called to prep the
	 * encryptor.
	 *
	 * (non-Javadoc)
	 * @see mff.security.crypto.EXTNEncryptor#encrypt(java.lang.String)
	 */
	@Override
	public String encrypt(String pStr) {
		String encryptedStr = null;
		try {
			init();
			encryptedStr = encodeToString(doEncrypt(pStr.getBytes()));
		} catch (EXTNEncryptionException e) {
			if(isLoggingError()) {
				logError(EXTNSecurityConstants.getSecurityResources().getString(EXTNSecurityConstants.ENCRYPTION_PROBLEM), e);
			}
		}
		return encryptedStr;
	}

	/*
	 * An abstract implementation of EXTNEncryptor's decrypt method. The init() method is first called to prep the
	 * encryptor.
	 *
	 * (non-Javadoc)
	 * @see mff.security.crypto.EXTNEncryptor#decrypt(java.lang.String)
	 */
	@Override
	public String decrypt(String pEncryptedString) {
		if(pEncryptedString == null) {
			return null;
		}

		String decryptedStr = null;
		try {
			init();
			decryptedStr = new String(doDecrypt(decodeToByteArray(pEncryptedString)));
		} catch (EXTNEncryptionException e) {
			if(isLoggingError()) {
				logError(EXTNSecurityConstants.getSecurityResources().getString(EXTNSecurityConstants.DECRYPTION_PROBLEM), e);
			}
		}
		return decryptedStr;
	}

	//###########################################
	//		Abstract Declarations
	//###########################################

	/**
	 * Do init.
	 *
	 * @throws EXTNEncryptionException the KP encryption exception
	 */
	protected abstract void doInit() throws EXTNEncryptionException;

	/**
	 * Do encrypt.
	 *
	 * @param pValue the value
	 * @return the byte[]
	 * @throws EXTNEncryptionException the KP encryption exception
	 */
	protected abstract byte[] doEncrypt(byte[] pValue) throws EXTNEncryptionException;

	/**
	 * Do decrypt.
	 *
	 * @param pValue the value
	 * @return the byte[]
	 * @throws EXTNEncryptionException the KP encryption exception
	 */
	protected abstract byte[] doDecrypt(byte[] pValue) throws EXTNEncryptionException;

	//###########################################
	//		Regular Methods
	//###########################################

	/**
	 * Decode to byte array.
	 *
	 * @param pValue the value
	 * @return the byte[]
	 */
	protected byte[] decodeToByteArray(String pValue) {
		return Base64.decodeToByteArray(pValue);
	}

	/**
	 * Encode to string.
	 *
	 * @param pValue the value
	 * @return the string
	 */
	protected String encodeToString(byte[] pValue) {
		return Base64.encodeToString(pValue);
	}

	/**
	 * Call this init method before every cryptograpy operation.
	 *
	 * @throws EXTNEncryptionException the KP encryption exception
	 */
	protected final void init() throws EXTNEncryptionException {
		if(!mInitialized) {
			synchronized (mMonitor) {
				if(!mInitialized) {
					doInit();
					mInitialized = true;
				}
			}
		}
	}
}
