package com.security;

import java.io.Serializable;
import java.security.Key;

import atg.crypto.KeyManagerException;
import atg.crypto.KeyStoreKeyManager;

/**
 * This class is an implementation of ATG's KeyStoreKeyManager.  It handles the retrieval of  keys from a keystore on
 * the file system.
 */
public class EXTNLocalKeyStore extends KeyStoreKeyManager implements Serializable, EXTNKeyStore {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Key retrieveKey(String pAlias) throws EXTNKeyStoreException {
		try {
			return super.getKey(pAlias, getPassword().toCharArray());
		} catch(KeyManagerException e) {
			throw new EXTNKeyStoreException(e);
		}
	}
}
