package com.mff.util;

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.mff.constants.MFFConstants;
import com.google.common.base.Strings;

/**
 * This class contains helpful utility methods packaged as static methods
 * 
 */
public class MFFUtils {

	public static final String LINE_FEED = System.getProperty("line.separator");

	public static <T> List<T> safe(List<T> other) {
		return other == null ? Collections.EMPTY_LIST : other;
	}

	public static String safe(String other) {
		return other == null ? "" : other;
	}

	/**
	 * @param pRequest
	 * @return
	 */
	public static boolean isAjaxRequest(DynamoHttpServletRequest pRequest) {
		String requestedWithHeader = pRequest.getHeader("X-REQUESTED-WITH");
		return (requestedWithHeader != null && requestedWithHeader.length() > 0);
	}

	/**
	 * @return
	 */
	public static boolean isAjaxRequest() {
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		String requestedWithHeader = request
				.getHeader(MFFConstants.HTTP_X_REQUESTED_WITH);
		return (requestedWithHeader != null && requestedWithHeader.length() > 0);
	}

	/**
	 * Generate a human-readable string displaying the time interval between
	 * startTime and now.
	 * 
	 * @param pStartTime
	 * @return
	 */
	public static String getTimeDifference(Date pStartTime) {
		Date endTime = new Date();
		long millis = endTime.getTime() - pStartTime.getTime();
		long hours = millis / 3600000;
		long remainder = millis - hours * 3600000;
		long mins = remainder / 60000;
		remainder = remainder - mins * 60000;
		long sec = remainder / 1000;
		String retValue = (hours > 0 ? hours + " hrs " : "") + mins + " mins "
				+ sec + " sec";
		return retValue;
	}

	/**
	 * Checks if a List contains duplicates by making it into a Set (sets don't
	 * allow duplicates) and comparing the Set's resultant size with the initial
	 * Lists's size.
	 * 
	 * @param pList
	 * @return true if the list contains duplicates, false if not.
	 */
	public static boolean listContainsDuplicates(List<String> pList) {
		Set<String> set = new HashSet<String>(pList);
		if (set.size() < pList.size()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotNull(Object pObj) {
		return pObj != null;
	}

	public static boolean isNull(Object pObj) {
		return pObj == null;
	}

	public static String getLine() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("line.separator"));
		sb.append(Thread.currentThread().getStackTrace()[2].getClassName());
		sb.append(":");
		sb.append(Thread.currentThread().getStackTrace()[2].getLineNumber());
		sb.append(System.getProperty("line.separator"));
		sb.append(Thread.currentThread().getStackTrace()[3].getClassName());
		sb.append(":");
		sb.append(Thread.currentThread().getStackTrace()[3].getLineNumber());
		sb.append(System.getProperty("line.separator"));
		sb.append(Thread.currentThread().getStackTrace()[4].getClassName());
		sb.append(":");
		sb.append(Thread.currentThread().getStackTrace()[4].getLineNumber());
		sb.append(System.getProperty("line.separator"));
		return sb.toString();

	}

	@SuppressWarnings("rawtypes")
	public static String printRequestInfo(DynamoHttpServletRequest pRequest) {
		String retValue = null;

		String lLineFeed = System.getProperty("line.separator");
		StringBuffer lStringBuffer = new StringBuffer();

		lStringBuffer
				.append("***************************************************"
						+ lLineFeed);
		lStringBuffer.append("-------------- Location ---------------"
				+ lLineFeed);
		lStringBuffer.append("Stacktrace : " + getLine() + lLineFeed);
		
		
		lStringBuffer.append("-------------- Request Details ---------------"
				+ lLineFeed);

		lStringBuffer.append("Session Id : " + pRequest.getSession().getId()
				+ lLineFeed);
		
		lStringBuffer.append("Request URI : ");
		lStringBuffer.append(pRequest.getRequestURIWithQueryString());
		lStringBuffer.append(lLineFeed);
		
		Enumeration headerNames = pRequest.getHeaderNames();
		lStringBuffer.append("------ Header Details ------ " + lLineFeed);
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();

			Enumeration headerValues = pRequest.getHeaders(headerName);

			while (headerValues.hasMoreElements()) {
				lStringBuffer.append(headerName);
				lStringBuffer.append(" : " + headerValues.nextElement()
						+ lLineFeed);
			}
		}

		lStringBuffer.append("------ Parameter Details ------ " + lLineFeed);
		Enumeration requestParameters = pRequest.getParameterNames();
		while (requestParameters.hasMoreElements()) {

			String requestParamName = (String) requestParameters.nextElement();
			if (requestParamName != null
					&& !requestParamName.toLowerCase().contains("creditcard")
					&& !requestParamName.toLowerCase().contains("giftcard")) {
				lStringBuffer.append(requestParamName);
				lStringBuffer.append(" : ");
				lStringBuffer.append(pRequest.getParameter(requestParamName));
			} else {
				lStringBuffer.append(requestParamName);
				lStringBuffer.append(" : ");
				lStringBuffer.append(" SUPPRESSED ");
			}
			lStringBuffer.append(lLineFeed);
		}

		lStringBuffer.append("------ Cookie Details ------ " + lLineFeed);
		Enumeration cookieParams = pRequest.getCookieParameterNames();
		// loop through cookie elements
		while (cookieParams.hasMoreElements()) {
			String cookieName = (String) cookieParams.nextElement();
			Enumeration cookieValues = pRequest.getHeaders(cookieName);
			lStringBuffer.append(cookieName);
			while (cookieValues.hasMoreElements()) {
				lStringBuffer.append(" : " + cookieValues.nextElement()
						+ lLineFeed);
			}
		}
		lStringBuffer.append(lLineFeed
				+ "***************************************************"
				+ lLineFeed);
		retValue = lStringBuffer.toString();
		return retValue;

	}

	/**
	 * Gets the IP from request
	 * 
	 * @return
	 */
	public static String getSourceIpAddress() {

		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

		return getSourceIpAddress(request);
	}

	/**
	 * Gets the IP from request
	 * 
	 * @param pRequest
	 * @return
	 */
	public static String getSourceIpAddress(DynamoHttpServletRequest pRequest) {

		String retValue = null;

		retValue = pRequest
				.getHeader(MFFConstants.SOURCE_IP_ADDRESS_HEADER_NAME);

		if (Strings.isNullOrEmpty(retValue)) {
			// vlogDebug("client IP not found in source IP Address using the remote address");
			retValue = pRequest.getRemoteAddr();
		}

		return retValue;
	}

	public static final String MESSAGE_SEPERATOR = "\\|";

	/**
	 * The purpose of this method is to split the message from key, for ex:
	 * SM0005: Invalid credit card
	 */
	public static String splitMessageFromKey(String pMessage) {
		String msg = pMessage;
		if (StringUtils.isNotBlank(pMessage)) {
			String[] msgArr = pMessage.split(MESSAGE_SEPERATOR);
			// initialize to the fist part, which is the key is most cases
			msg = msgArr[0];
			if (msgArr.length > 1) {
				msg = msgArr[1];
			}
		}
		return msg;
	}

	public static String logErrorFields(List<String> errorFields,
			List<String> errorValues) {
		StringBuilder sb = new StringBuilder();
		sb.append(LINE_FEED);
		sb.append("##################################");
		sb.append("Error Fields:");
		sb.append(LINE_FEED);

		if (errorFields != null) {
			for (String errorField : errorFields) {
				sb.append(errorField);
				sb.append(LINE_FEED);
			}
		}

		sb.append("Error values:");
		sb.append(LINE_FEED);
		if (errorValues != null) {
			for (String errorValue : errorValues) {
				sb.append(errorValue);
				sb.append(LINE_FEED);
			}
		}
		sb.append("##################################");
		sb.append(LINE_FEED);
		return sb.toString();
	}

	public static String removeParamFromQueryString(String pQueryString,
			String pParamName) {
		StringBuilder lQueryStringBuilder = new StringBuilder();
		String[] lQueryParamPairs = pQueryString.split("&");
		for (String lQueryParamPair : lQueryParamPairs) {
			if (!lQueryParamPair.startsWith(pParamName + "=")) {
				if (lQueryStringBuilder.length() != 0) {
					lQueryStringBuilder.append("&");
				}
				lQueryStringBuilder.append(lQueryParamPair);
			}
		}
		return lQueryStringBuilder.toString();
	}
}
