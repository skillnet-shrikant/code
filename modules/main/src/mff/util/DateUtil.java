package mff.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Date Utility Class used to convert Strings to Dates and Timestamps
 */
public class DateUtil {
	private static Log			log									= LogFactory.getLog(DateUtil.class);

	public static final String	DATE_PATTERN						= "MM/dd/yyyy";

	public static final String	TIME_PATTERN						= "HH:mm";
	public static final String	TIME_MILLIS_PATTERN					= "HH:mm:ss.S";

	public static final String	DATE_TIME_SEC_PATTERN				= "MM/dd/yyyy HH:mm:ss";

	/* Site Personalization cookie date format */
	public static final String	DATE_TIME_SPCOOKIE_PATTERN			= "yyyy-MM-dd";

	public static final String	NUMERIC_DATE_PATTERN				= "yyyyMMdd";
	public static final String	NUMERIC_TIME_PATTERN				= "HHmm";
	public static final String	NUMERIC_DATE_TIME_SEC_PATTERN		= "yyyyMMddHHmmss";
	public static final String	NUMERIC_DATE_TIME_MILLIS_PATTERN	= "yyyyMMddHHmmssS";



	private DateUtil() {
	}



	/**
	 * Converts a java.util.Date or java.sql.Date to a Calendar object.
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Calendar convertDateToCalendar(Date date) {
		if (date instanceof java.sql.Date)
			return new GregorianCalendar((date.getYear() + 1900), date.getMonth(), date.getDate());
		else
			return new GregorianCalendar((date.getYear() + 1900), date.getMonth(), date.getDate(), date.getHours(),
					date.getMinutes(), date.getSeconds());
	}



	/**
	 * Creates a date with the specified values.
	 * 
	 * The year and month should be the actuals (i.e. should not follow the
	 * java.util.Date year/month convention).
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date createDate(int year, int month, int dayOfMonth) {
		return new Date(year - 1900, month - 1, dayOfMonth);
	}



	/**
	 * Creates a Date based upon the given year, month, day.
	 * 
	 * The month should be base 1.
	 * 
	 * @param year
	 * @param month
	 *            base 1
	 * @param dayOfMonth
	 * @return
	 */
	public static Date createDate(String year, String month, String dayOfMonth) {
		Calendar cal = createCalendar(year, month, dayOfMonth);
		if (cal != null)
			return cal.getTime();
		return null;
	}



	/**
	 * Creates a calendar base upon the given year, month, day.
	 * 
	 * The month should be base 1.
	 * 
	 * @param year
	 * @param month
	 *            base 1
	 * @param dayOfMonth
	 * @return
	 */
	public static Calendar createCalendar(String year, String month, String dayOfMonth) {
		int iYear, iMonth, iDay;
		try {
			iYear = Integer.parseInt(year);
			iMonth = Integer.parseInt(month);
			iDay = Integer.parseInt(dayOfMonth);
		} catch (NumberFormatException e) {
			log.warn("NumberFormatException occurred: " + e.getMessage());
			log.error(e);
			return null;
		}

		return new GregorianCalendar(iYear, (iMonth - 1), iDay);
	}



	/**
	 * This method returns the current date in the format: MM/dd/yyyy
	 * 
	 * @return the current date
	 * @throws ParseException
	 *             when String doesn't match the expected format
	 */
	public static Calendar getToday() throws ParseException {
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat(getDatePattern());

		// This seems like quite a hack (date -> string -> date),
		// but it works ;-)
		String todayAsString = df.format(today);
		Calendar cal = new GregorianCalendar();
		cal.setTime(convertStringToDate(todayAsString));

		return cal;
	}



	public static int getCurrentYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}



	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH);
	}



	@SuppressWarnings("deprecation")
	public static int getYear(Date date) {
		return date.getYear() + 1900;
	}



	/**
	 * Returns a date which is the first day of the year for the specified date.
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getFirstDayOfYear(Date date) {
		return new Date(date.getYear(), 0, 1);
	}



	/**
	 * Returns a date which is the first day of the year for the specified year.
	 * 
	 * @param year
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getFirstDayOfYear(int year) {
		return new Date((year - 1900), 0, 1);
	}



	/**
	 * Returns a date which is the first day of the year for the current year.
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getFirstDayOfYear() {
		return new Date((getCurrentYear() - 1900), 0, 1);
	}



	/**
	 * Returns a date which is the last day of the year for the provided date.
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getLastDayOfYear(Date date) {
		return new Date(date.getYear(), 11, 31);
	}



	/**
	 * Calculates the length of an interval in years.
	 * 
	 * @param date1
	 *            - The start date of the interval
	 * @param date2
	 *            - The end date of the interval
	 */
	@SuppressWarnings("deprecation")
	public static int getDiffInYears(Date date1, Date date2) {
		return date2.getYear() - date1.getYear();
	}



	/**
	 * Returns a date which is the first day of the month for the provided date.
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getFirstDayOfMonth(Date date) {
		return new Date(date.getYear(), date.getMonth(), 1);
	}



	/**
	 * Returns a date which is the last date of the month for the provided date.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = convertDateToCalendar(date);
		int maxDay = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		cal.set(GregorianCalendar.DAY_OF_MONTH, maxDay);
		return cal.getTime();
	}



	/**
	 * Calculates the length of an interval in months.
	 * 
	 * @param date1
	 *            - The start date of the interval
	 * @param date2
	 *            - The end date of the interval
	 */
	@SuppressWarnings("deprecation")
	public static int getDiffInMonths(Date date1, Date date2) {
		int liMonthDiff = 0;

		liMonthDiff += (date2.getYear() - date1.getYear()) * 12;
		liMonthDiff += (date2.getMonth() - date1.getMonth());

		// Adjust for rounding off to the nearest month.
		if (date2.getDate() < date1.getDate()) {
			liMonthDiff--;
		}
		return liMonthDiff;
	}



	/**
	 * Returns a date which is date + additionalMonths.
	 * 
	 * @param date
	 * @param num
	 * @return
	 */
	public static Date addMonths(Date date, int additionalMonths) {
		Calendar cal = convertDateToCalendar(date);
		cal.add(Calendar.MONTH, additionalMonths);
		return cal.getTime();
	}



	/**
	 * Returns a date which is date + additionalDays.
	 * 
	 * @param date
	 * @param num
	 * @return
	 */
	public static Date addDays(Date date, int additionalDays) {
		Calendar cal = convertDateToCalendar(date);
		cal.add(Calendar.DATE, additionalDays);
		return cal.getTime();
	}



	@SuppressWarnings("deprecation")
	public static Date stripTimeFromDate(Date date) {
		return new Date(date.getYear(), date.getMonth(), date.getDate());
	}



	/**
	 * This method generates a string representation of a date's date/time in
	 * the format you specify on input
	 * 
	 * @param aDate
	 *            a date object
	 * @return a formatted string representation of the date
	 */
	public static String getDateTime(Date aDate) {
		return getDateTime(getDateTimePattern(), aDate);
	}



	/**
	 * This method generates a string representation of the curretn date's
	 * date/time in the format you specify on input
	 * 
	 * @return a formatted string representation of the date
	 */
	public static String getDateTimeNow() {
		return getDateTime(getDateTimePattern(), new Date());
	}



	/**
	 * This method generates a string representation of a date's date/time in
	 * the format you specify on input
	 * 
	 * @param aMask
	 *            the date pattern the string is in
	 * @param aDate
	 *            a date object
	 * @return a formatted string representation of the date
	 * 
	 * @see java.text.SimpleDateFormat
	 */
	public static String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null) {
			log.error("aDate is null!");
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}



	/**
	 * Return default datePattern (MM/dd/yyyy)
	 * 
	 * @return a string representing the date pattern on the UI
	 */
	public static String getDatePattern() {
		String defaultDatePattern;
		defaultDatePattern = DATE_PATTERN;

		return defaultDatePattern;
	}



	public static String getDateTimePattern() {
		return DateUtil.getDatePattern() + " " + TIME_MILLIS_PATTERN;
	}



	/**
	 * This method returns the date in the MM/dd/yyyy pattern.
	 * 
	 * @param aDate
	 *            date from database as a string
	 * @return formatted string for the ui
	 */
	public static String getDate(Date aDate) {
		SimpleDateFormat df;
		String returnValue = "";

		if (aDate != null) {
			df = new SimpleDateFormat(getDatePattern());
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}



	/**
	 * This method generates a string representation of a date/time in the
	 * format you specify on input
	 * 
	 * @param aMask
	 *            the date pattern the string is in
	 * @param strDate
	 *            a string representation of a date
	 * @return a converted Date object
	 * @see java.text.SimpleDateFormat
	 * @throws ParseException
	 *             when String doesn't match the expected format
	 */
	public static Date convertStringToDate(String aMask, String strDate) throws ParseException {
		SimpleDateFormat df;
		Date date;
		df = new SimpleDateFormat(aMask);

		if (log.isDebugEnabled()) {
			log.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
		}

		try {
			date = df.parse(strDate);
		} catch (ParseException pe) {
			// log.error("ParseException: " + pe);
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return (date);
	}



	/**
	 * This method returns the current date time in the format: MM/dd/yyyy HH:MM
	 * a
	 * 
	 * @param theTime
	 *            the current time
	 * @return the current date/time
	 */
	public static String getTimeNow(Date theTime) {
		return getDateTime(TIME_PATTERN, theTime);
	}



	/**
	 * Returns the current date/time in the specified format.
	 * 
	 * @param aMask
	 * @return
	 */
	public static String getDateTimeNow(String aMask) {
		return getDateTime(aMask, new Date());
	}



	/**
	 * This method generates a string representation of a date based on the
	 * System Property 'dateFormat' in the format you specify on input
	 * 
	 * @param aDate
	 *            A date to convert
	 * @return a string representation of the date
	 */
	public static String convertDateToString(Date aDate) {
		return getDateTime(getDatePattern(), aDate);
	}



	public static String convertDateTimeToString(Date aDate) {
		return getDateTime(getDateTimePattern(), aDate);
	}



	/**
	 * This method converts a String to a date using the datePattern
	 * 
	 * @param strDate
	 *            the date to convert (in format MM/dd/yyyy)
	 * @return a date object
	 * @throws ParseException
	 *             when String doesn't match the expected format
	 */
	public static Date convertStringToDate(String strDate) throws ParseException {
		Date aDate = null;

		try {
			if (log.isDebugEnabled()) {
				log.debug("converting date with pattern: " + getDatePattern());
			}

			aDate = convertStringToDate(getDatePattern(), strDate);
		} catch (ParseException pe) {
			log.error("Could not convert '" + strDate + "' to a date, throwing exception " + pe);
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return aDate;
	}



	/**************************************************************************
	 * Birth date
	 **************************************************************************/

	/**
	 * Determines if the given birthdate is valid. Accounts for leap years and
	 * future dates.
	 * 
	 * The month should be base 1.
	 * 
	 * @param year
	 * @param month
	 *            base 1
	 * @param dayOfMonth
	 * @return
	 */
	public static boolean isValidBirthdate(String year, String month, String dayOfMonth) {

		int iYear, iMonth, iDay;
		try {
			iYear = Integer.parseInt(year);
			iMonth = Integer.parseInt(month) - 1;
			iDay = Integer.parseInt(dayOfMonth);
		} catch (NumberFormatException e) {
			return false;
		}

		Calendar bday = new GregorianCalendar();
		bday.set(iYear, iMonth, iDay);

		// if the get values don't match the set values then
		// a 'rollover' occurred - meaning invalid date entered by user
		if (bday.get(Calendar.DAY_OF_MONTH) != iDay || bday.get(Calendar.MONTH) != iMonth
				|| bday.get(Calendar.YEAR) != iYear) {
			return false;
		}

		return isValidBirthdate(bday);
	}



	/**
	 * Determines if the given birthdate 'bday' is valid. Accounts for leap
	 * years and future dates.
	 * 
	 * @param bday
	 * @return
	 */
	public static boolean isValidBirthdate(Calendar bday) {

		// Validate.notNull(bday, "bday cannot be null");

		// people from the future aren't allowed
		Calendar now = Calendar.getInstance();
		if (bday.after(now))
			return false;

		return true;
	}



	/**
	 * Determines how many years have passed since the given bday
	 * 
	 * @param bday
	 * @return
	 */
	public static int getAgeInYears(Calendar bday) {

		// Validate.notNull(bday, "bday cannot be null");

		Calendar currentDate = Calendar.getInstance();
		int currentYear = currentDate.get(Calendar.YEAR);
		int bdayYear = bday.get(Calendar.YEAR);

		int yearsOld = currentYear - bdayYear;

		// have they had their bday this year?
		currentDate.set(Calendar.YEAR, bdayYear);
		if (currentDate.before(bday)) {
			yearsOld--;
		}

		return yearsOld;
	}
}