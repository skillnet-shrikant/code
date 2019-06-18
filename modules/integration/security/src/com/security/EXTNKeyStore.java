package com.security;

import java.security.Key;

/**
 * An interface that all key stores must implement that defines key retrieval tasks.
 */
public interface EXTNKeyStore {

	/**
	 * This method gets the key based on the passed in alias.
	 *
	 * @param pAlias - the alias for the key to retrieve
	 * @return the secret key
	 * @throws EXTNKeyStoreException
	 */
	public Key retrieveKey(String pAlias) throws EXTNKeyStoreException;
}
