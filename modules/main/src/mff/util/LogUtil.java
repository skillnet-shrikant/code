package mff.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogUtil {
	
    public static final int LOG_LEVEL_ALL = 0;
    public static final int LOG_LEVEL_TRACE = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_INFO = 3;
    public static final int LOG_LEVEL_WARN = 4;
    public static final int LOG_LEVEL_ERROR = 5;
    public static final int LOG_LEVEL_FATAL = 6;
    public static final int LOG_LEVEL_OFF = 7;
    
    public static final String LOG_LEVEL_TRACE_LOG_PREFIX 		= "TRACE";
    public static final String LOG_LEVEL_DEBUG_LOG_PREFIX		= "DEBUG";
    public static final String LOG_LEVEL_INFO_LOG_PREFIX		= "INFO ";
    public static final String LOG_LEVEL_ERROR_LOG_PREFIX		= "ERROR";
	
	private static Log log = LogFactory.getLog(LogUtil.class);
	
	public static void debug(Object arg0) {
		log.debug(arg0);
	}

	public static void debug(Object arg0, Throwable arg1) {
		log.debug(arg0, arg1);
	}

	public static void error(Object arg0) {
		log.error(arg0);
	}

	public static void error(Object arg0, Throwable arg1) {
		log.error(arg0, arg1);
	}

	public static void fatal(Object arg0) {
		log.fatal(arg0);
	}

	public static void fatal(Object arg0, Throwable arg1) {
		log.fatal(arg0, arg1);
	}

	public static void info(Object arg0) {
		log.info(arg0);
	}

	public static void info(Object arg0, Throwable arg1) {
		log.info(arg0, arg1);
	}

	public static boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public static boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	public static boolean isFatalEnabled() {
		return log.isFatalEnabled();
	}

	public static boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	public static boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	public static boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	public static void trace(Object arg0) {
		log.trace(arg0);
	}

	public static void trace(Object arg0, Throwable arg1) {
		log.trace(arg0, arg1);
	}

	public static void warn(Object arg0) {
		log.warn(arg0);
	}

	public static void warn(Object arg0, Throwable arg1) {
		log.warn(arg0, arg1);
	}
}