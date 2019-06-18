package com.security.crypto;

import java.security.Provider;
import java.security.Security;

import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import com.security.EXTNSecurityConstants;

import atg.crypto.KeyStoreKeyManager;
import com.security.EXTNKeyManager;

/**
 * This class is an AES encryptor/decryptor.
 *
 * @author Peter Goetsch (pgoetsch@knowledgepath.com)
 */
public class AESEncryptor extends AbstractEncryptor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private EXTNKeyManager mKeyManager;

	private KeyStoreKeyManager mKeyStore;

	private List<String> mSecurityProviders = null;

	private transient Cipher mEncryptCypher = null;

	private transient Cipher mDecryptCypher = null;

	private String mAesKeyAlias;

	/*
	 * Implementation of the abstract doEncrypt method.  Encrypts the passed in value.
	 *
	 * (non-Javadoc)
	 * @see mff.security.crypto.AbstractEncryptor#doEncrypt(byte[])
	 */
	@Override
	protected byte[] doEncrypt(byte[] pValue) throws EXTNEncryptionException {
		if(isLoggingDebug()) {
			logDebug("Entering AESEncryptor.doEncrypt");
		}
        byte[] encValue = null;
		try {
			encValue = mEncryptCypher.doFinal(pValue);
		} catch (Exception e) {
			if(isLoggingDebug()) {
				vlogDebug("Found an exception ({0}) while encrypting, rewrapping and throwing up the stack.", e);
			}
			throw new EXTNEncryptionException(e);
		}
		return encValue;
	}

	/*
	 * Implementation of the abstract doDecrypt method.  Decrypts the passed in value.
	 *
	 * (non-Javadoc)
	 * @see mff.security.crypto.AbstractEncryptor#doDecrypt(byte[])
	 */
	@Override
	protected byte[] doDecrypt(byte[] pValue) throws EXTNEncryptionException {
		if(isLoggingDebug()) {
			logDebug("Entering AESEncryptor.doDecrypt");
		}
		byte[] decryptedValue = null;
	     try {
	    	 decryptedValue = mDecryptCypher.doFinal(pValue);
		} catch (Exception e) {
			if(isLoggingDebug()) {
				vlogDebug("Found an exception ({0}) while decrypting, rewrapping and throwing up the stack.", e);
			}
			throw new EXTNEncryptionException(e);
		}
		return decryptedValue;
	}

	/*
	 * Implementation of the abstract doInit method.  Initializes the AES encryption.
	 *
	 * (non-Javadoc)
	 * @see mff.security.crypto.AbstractEncryptor#doInit()
	 */
	protected final void doInit() throws EXTNEncryptionException {
		if(isLoggingDebug()) {
			logDebug("Entering AESEncryptor.doInit");
		}
		SecretKey key = null;

		try {
			addSecurityProviders();

			getKeyStore().loadKeyStore();

			key = (SecretKey) getKeyManager().retrieveKey(getAesKeyAlias());

			mEncryptCypher = Cipher.getInstance(EXTNSecurityConstants.ENCRYPTION_AES);
			mEncryptCypher.init(Cipher.ENCRYPT_MODE, key);

			mDecryptCypher = Cipher.getInstance(EXTNSecurityConstants.ENCRYPTION_AES);
			mDecryptCypher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			if(isLoggingDebug()) {
				vlogDebug("Found an exception ({0}) while initializing, rewrapping and throwing up the stack.", e);
			}
			throw new EXTNEncryptionException(e);
		}
	}

	/**
	 * This method adds new security provider
	 * at the top of the list of existing providers.
	 */
	protected void addSecurityProviders() {
		if(isLoggingDebug()) {
			logDebug("Entering AESEncryptor.addSecurityProviders");
		}
		int position = 1;
		List<String> securityProviders = getSecurityProviders();

		if(securityProviders != null) {
			Iterator<String> securityProvidersIter = securityProviders.iterator();

			String securityProvider = null ;
			while(securityProvidersIter.hasNext()) {
				try {
					securityProvider = (String) securityProvidersIter.next();
					@SuppressWarnings("rawtypes")
					Class providerClass = Class.forName(securityProvider);
					Provider provider = (Provider) providerClass.newInstance();
					Security.insertProviderAt(provider, position);
					position++;
				} catch (InstantiationException e) {
					if (isLoggingWarning()) {
						logWarning("Unable to add provider: " + securityProvider + ". Proceeding with default settings.");
					}
				} catch (IllegalAccessException e) {
					if(isLoggingWarning()) {
						logWarning("Unable to add provider: " + securityProvider + ". Proceeding with default settings.");
					}
				} catch (ClassNotFoundException e) {
					if(isLoggingWarning()) {
						logWarning("Unable to add provider: " + securityProvider + ". Proceeding with default settings.");
					}
				}
			}
		}
	}

	public List<String> getSecurityProviders() {
		return mSecurityProviders;
	}

	public void setSecurityProviders(List<String> pSecurityProviders) {
		mSecurityProviders = pSecurityProviders;
	}

	public KeyStoreKeyManager getKeyStore() {
		return mKeyStore;
	}

	public void setKeyStore(KeyStoreKeyManager pKeyStore) {
		mKeyStore = pKeyStore;
	}

	public EXTNKeyManager getKeyManager() {
		return mKeyManager;
	}

	public void setKeyManager(EXTNKeyManager pKeyManager) {
		mKeyManager = pKeyManager;
	}

	public String getAesKeyAlias() {
		return mAesKeyAlias;
	}

	public void setAesKeyAlias(String pAesKeyAlias) {
		mAesKeyAlias = pAesKeyAlias;
	}
}
