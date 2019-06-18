package mff.typeahead.util;

import mff.typeahead.beans.ErrorBean;

/**
 * A class for Application-wide constants
 * 
 * @author foldenburg
 *
 */
public final class Constants {

	/** 
	 * Private Constructor
	 */
	private Constants() { } 
	
	public static final int NS_PER_MS = 1000000;
	public static final int MS_PER_SEC = 1000;
	public static final int DEFAULT_MAX_RESULTS = 10;
	public static final int DEFAULT_MAX_LENGTH = 30;
	
	// Error Types
	public static final ErrorBean ERR_INVALID_TERM_LENGTH = new ErrorBean("400","Invalid search term length");
	public static final ErrorBean ERR_BACKEND_SERVICE = new ErrorBean("500","An error occurred while querying a backend service");
	public static final ErrorBean ERR_CONFIG = new ErrorBean("501","Service Configuration Error");
}
