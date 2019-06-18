package com.security;

import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import atg.nucleus.GenericService;

import atg.nucleus.ServiceException;

import com.security.EXTNSecurityConstants;

/**
 * This is the component that all calling applications use to retrieve keys.  It creates a unified interface for calling
 * classes to use because they don't have to know anything about how or where the keys are stored, they just have to
 * know the alias.
 *
 * DO NOT log the key values, only the aliases should be in the logs.
 */
public class EXTNKeyManager extends GenericService implements Serializable {

	private static final long serialVersionUID = 11L;

	private EXTNKeyStore mKeyStore;

	private Map<String, Key> mKeyCache = new HashMap<String, Key>();

	private String[] mKeyAliasesToLoadOnStartup;

	/**
	 * This is the method calling classes use to retrieve their keys.  A key alias is passed in.  If the key cache is
	 * empty, then the mKeyStore reaches out to retrieve the key.
	 *
	 * @param pAlias - the alias name of the key to retrieve.
	 * @return the key
	 */
	public Key retrieveKey(String pAlias) throws EXTNKeyStoreException {
		if(StringUtils.isBlank(pAlias)) {
			if(isLoggingError()) {
				logError("Blank pAlias parameter passed into SecurityKeyManager.");
			}
			return null;
		}

		if(retriveCachedKey(pAlias) == null) {
			cacheKey(getKeyStore().retrieveKey(pAlias), pAlias);
			return retrieveKey(pAlias);
		} else {
			if(isLoggingDebug()) {
				Key key = retriveCachedKey(pAlias);
				vlogDebug("Key for alias [{0}] successfuly loaded. Key Algorithm is {1}.", pAlias, key.getAlgorithm());
			}
			return retriveCachedKey(pAlias);
		}
	}

	/**
	 * Retrieves the key from the cache if it exists.
	 *
	 * @param pAlias - the alias they the key is keyed by.
	 * @return the cached key
	 */
	private Key retriveCachedKey(String pAlias) {
		if(getKeyCache().containsKey(pAlias)) {
			return getKeyCache().get(pAlias);
		} else {
			if(isLoggingDebug()) {
				vlogDebug("Cache did NOT contain the value for key [{0}].", pAlias);
			}
			return null;
		}
	}

	/**
	 * Places pKey in the cache using the key pAlias.  Overwrites old key values.
	 *
	 * @param pKey - the key
	 * @param pAlias - the alias to use in storing the key
	 */
	private void cacheKey(Key pKey, String pAlias) {
		getKeyCache().put(pAlias, pKey);
		if(isLoggingDebug()) {
			vlogDebug("Added the [{0}] alias into the cache.  The current key set is: {1}", pAlias, getKeyCache().keySet().toString());
		}
	}

	/**
	 * Utility method to clear the key cache.
	 *
	 * Need to test if this works from dyn/admin.
	 */
	public void clearCache() {
		getKeyCache().clear();
	}

	/*
	 * Cycles through the mKeyAliasesToLoadOnStartup array and fetches and loads each one into the cache.
	 */
	@Override
	public void doStartService() throws ServiceException {
		if(isLoggingInfo()) {
			logInfo("Starting up SecurityKeyManager.");
			if(getKeyAliasesToLoadOnStartup().length > 0) {
				vlogDebug("--Preloading the key cache with these keys: {0}.", ArrayUtils.toString(getKeyAliasesToLoadOnStartup()));
			}
		}

		if(getKeyAliasesToLoadOnStartup().length > 0) {
			for(String alias : getKeyAliasesToLoadOnStartup()) {
				try {
					retrieveKey(alias);
				} catch(EXTNKeyStoreException e) {
					if(isLoggingError()) {
						logError("--Could not retrieve the key for alias [" + alias + "].", e);
					}
				}
			}
		}

		try {
			int maxKeyLength = javax.crypto.Cipher.getMaxAllowedKeyLength(EXTNSecurityConstants.ENCRYPTION_AES);

			// if this is 128, then the JCE security patch is not in
			if(maxKeyLength <= 128 && isLoggingWarning()) {
				logWarning("--The max encryption key length for AES is only 128 bits.  Has the JCE security patch been installed?");
			}

			if(isLoggingDebug()) {
				vlogDebug("--Max Encryption Key Length is {0} bits.", maxKeyLength);
			}

		} catch(NoSuchAlgorithmException e) {
			if(isLoggingError()) {
				logError("Error while checking the max key length: ", e);
			}
		}

//		if(isLog())  {
			vlogDebug("SecurityKeyManager successfully started.");
//		}
	}

	public EXTNKeyStore getKeyStore() {
		return mKeyStore;
	}

	public Map<String, Key> getKeyCache() {
		return mKeyCache;
	}

	public void setKeyCache(Map<String, Key> pKeyCache) {
		mKeyCache = pKeyCache;
	}

	public void setKeyStore(EXTNKeyStore pKeyStore) {
		mKeyStore = pKeyStore;
	}

	public String[] getKeyAliasesToLoadOnStartup() {
		return mKeyAliasesToLoadOnStartup;
	}

	public void setKeyAliasesToLoadOnStartup(String[] pKeyAliasesToLoadOnStartup) {
		mKeyAliasesToLoadOnStartup = pKeyAliasesToLoadOnStartup;
	}
}
