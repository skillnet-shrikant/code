package mff;

import java.util.Locale;
import java.util.ResourceBundle;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

/**
 * Constants for KP
 * @author
 */
public final class EXTNConstants {

	/* Resource Bundle */
	private static final String EXTN_RESOURCE_BUNDLE_NAME = "mff.Resources";

	public static ResourceBundle getEXTNResources() {
		return getEXTNResources(ServletUtil.getCurrentRequest());
	}

	public static ResourceBundle getEXTNResources(DynamoHttpServletRequest pRequest) {
	    RequestLocale requestLocale = null;
	    if (pRequest != null) {
	    	requestLocale = pRequest.getRequestLocale();
	    }
	    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
	    return getEXTNResources(currentLocale);
	}

	public static ResourceBundle getEXTNResources(Locale pLocale) {
		return ResourceBundle.getBundle(EXTN_RESOURCE_BUNDLE_NAME, pLocale);
	}

	/* General Constants */
	public static final String UTF_8 = "UTF-8";
	public static final String HYPHEN = "-";
	public static final String QUESTION_MARK = "?";
	public static final String QUESTION_MARK_ENCODED = "%3F";
	public static final String QUESTION_MARK_ALIAS = "-QM";
	public static final String SINGLE_SPACE = " ";
	public static final String EMPTY_STRING = "";
	public static final String HTTP = "http";
	public static final String HTTP_X_REQUESTED_WITH = "X-REQUESTED-WITH";
	public static final String STATICPAGE = "staticPage";
	public static final String PAGE_NAME = "pageName";

	//Common
	public static final String PROPERTY_START_DATE = "startDate";
	public static final String PROPERTY_END_DATE = "endDate";

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
