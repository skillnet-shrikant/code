package com.security;

import java.security.Key;
import atg.nucleus.GenericService;

/**
 * This class is a keystore implementation that hooks into KP's internal keystore.
 * Implementation details are TBD.
 */
public class EXTNRemoteKeyStore extends GenericService implements EXTNKeyStore {


	@Override
	public Key retrieveKey(String pAlias) throws EXTNKeyStoreException {
		return null;
	}
}
