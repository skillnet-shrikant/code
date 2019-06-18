package com.security;

import java.util.Map;

import atg.security.PasswordNotInPreviousNRule;

/** Override OOTB class to not perform check if key found in map.  We don't
 * want this rule in all cases, notably if soft logged in but checking
 * out as guest and creating a new account.  See Bug 1008.
 *
 */
public class EXTNPasswordNotInPreviousNRule extends PasswordNotInPreviousNRule {
	public static final String SKIP_CHECK_KEY = "skipCheck";

	@SuppressWarnings("rawtypes")
	@Override
	public boolean checkRule(String password, Map map) {
		if (map != null) {
			Object skipCheck = map.get(SKIP_CHECK_KEY);

			if (skipCheck != null) {
				if (isLoggingDebug()) {
					logDebug("Found skipCheck param: " + skipCheck);
				}
				if (skipCheck instanceof String) {
					if (Boolean.valueOf((String) skipCheck)) {
						if (isLoggingDebug()) {
							logDebug("Skipping check based on String");
						}
						return true;
					}
				} else if (skipCheck instanceof Boolean) {
					if ((Boolean) skipCheck) {
						if (isLoggingDebug()) {
							logDebug("Skipping check based on Boolean");
						}
						return true;
					}
				} else {
					if (isLoggingWarning()) {
						logWarning("Found unexpected object for " + SKIP_CHECK_KEY + ": " + skipCheck);
					}
				}
			}
		}

		return super.checkRule(password, map);
	}
}
