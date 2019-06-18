package com.security;

import java.util.Locale;
import java.util.ResourceBundle;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

/**
 * Constants for Security
 * @author
 */
public final class EXTNSecurityConstants {

	/* Resource Bundle */
	private static final String SECURITY_RESOURCE_BUNDLE_NAME = "com.security.EXTNSecurityResources";

	public static ResourceBundle getSecurityResources() {
		return getSecurityResources(ServletUtil.getCurrentRequest());
	}

	public static ResourceBundle getSecurityResources(DynamoHttpServletRequest pRequest) {
	    RequestLocale requestLocale = null;
	    if (pRequest != null) {
	    	requestLocale = pRequest.getRequestLocale();
	    }
	    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
	    return getSecurityResources(currentLocale);
	}

	public static ResourceBundle getSecurityResources(Locale pLocale) {
		return ResourceBundle.getBundle(SECURITY_RESOURCE_BUNDLE_NAME, pLocale);
	}


	// Encryption
	public static final String ENCRYPTION_ILLEGAL_KEY_LENGTH_CFG = "enc.illegalKeyLengthConfigured";
	public static final String ENCRYPTOR = "encryptor";
	public static final String DECRYPTION_PROBLEM = "enc.decryptionProblem";
	public static final String ENCRYPTION_PROBLEM = "enc.encryptionProblem";
	public static final String ENCRYPTION_RESOLVE_PROBLEM = "enc.resolveProblem";
	public static final String ENCRYPTION_AES = "AES";
	public static final String ENCRYPTION_FAILED_TO_INIT_ENC = "enc.failedToInitEncryptor";
	public static final String ENCRYPTION_START_ERROR = "enc.startError";
	public static final String ENCRYPTION_ALG_EXC = "enc.AlgExc";


}
