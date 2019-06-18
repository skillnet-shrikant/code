package mff.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringUtil {

    public static final String LINE_SEPERATOR = 			System.getProperty("line.separator");
    public static final String CSV_DELIM =					",";

    public static final char[] STANDARD_CHARS = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
        'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        };

    private StringUtil() { }

    /**************************************************************************
     *  Standard Utils
     **************************************************************************/

    public static boolean isEqual(String str1, String str2) {
        if (str1 != null && str2 != null) {
            return str1.equals(str2);
        }
        return false;
    }

    public static boolean isEqualIgnoreCase(String str1, String str2) {
        if (str1 != null && str2 != null) {
            return str1.equalsIgnoreCase(str2);
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static boolean isNumber(String number) {
        if (number == null || number.trim().length() == 0)
            return false;
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String number) {
        if (number == null || number.trim().length() == 0)
            return false;
        try {
        	for (int i=0;i<number.length()-1;i++){
        		Integer.parseInt(number.substring(i,i+1));
        	}            
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static boolean booleanValue(String booleanString) {
        if (!StringUtil.isEmpty(booleanString)) {
            return new Boolean(booleanString).booleanValue();
        }
        return false;
    }

    /**
     * Returns a string where only the specified 'charsToKeep'
     * remain from the original input string.
     *
     * @param input
     * @param charsToKeep
     * @return
     */
    public static String cleanString(String input, String charsToKeep) {
        StringBuffer buffer = new StringBuffer(input.length());
        for(int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if(charsToKeep.indexOf(ch) > -1) {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    /**
     * Returns a string where the characters in 'charsToRemove'
     * have been removed from the original input string.
     *
     * @param input
     * @param charsToKeep
     * @return
     */
    public static String cleanFromString(String input, String charsToRemove) {
        StringBuffer buffer = new StringBuffer(input.length());
        for(int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if(charsToRemove.indexOf(ch) > -1)
                continue;
            buffer.append(ch);
        }
        return buffer.toString();
    }

    /**
     * Returns a List with each entry of 'vals' decorated (prefixed and suffixed)
     * by the given 'decorator'.
     *
     * @param vals
     * @param decorator
     * @return
     */
    public static List<String> decorateStrings(List<String> vals, String decorator) {
        List<String> out = new ArrayList<String>(vals.size());
        for (int i = 0; i < vals.size(); i++) {
            out.add(i, decorator + vals.get(i) + decorator);
        }
        return out;
    }

    /**
     * Returns a single string with each entry in 'vals' separated by the the
     * given 'delimiter'.
     *
     * @param vals
     * @param delimiter
     * @return
     */
    public static String genDelimitedString(List<String> vals, String delimiter) {
        return StringUtil.genDelimitedString(vals.toArray(), delimiter);
    }

    /**
     * Returns a single string with each entry in 'vals' separated by the the
     * given 'delimiter'.
     *
     * @param vals
     * @param delimiter
     * @return
     */
    public static String genDelimitedString(Set<String> vals, String delimiter) {
        return StringUtil.genDelimitedString(vals.toArray(), delimiter);
    }

    /**
     * Returns a single string with each entry in 'vals' separated by the the
     * given 'delimiter'.
     *
     * @param vals
     * @param delimiter
     * @return
     */
    public static String genDelimitedString(Object[] vals, String delimiter) {
        StringBuilder output = new StringBuilder();
        for (Object curr : vals) {
            if (output.length() > 0)
                output.append(delimiter);
            output.append(curr.toString());
        }
        return output.toString();
    }

    /**
     * For originalString, will escape each occurrence of the characters within
     * charsToEscape with escapeString.
     *
     * For example:
     * originalString 	= "This test's sample string?"
     * charsToEscape 	= "'?"
     * escapeString		= "'"
     *
     * Return value 	= "This test''s sample string'?"
     *
     * @param originalString
     * @param charsToEscape
     * @param escapeString
     * @return
     */
    public static String escapeString(String originalString, String charsToEscape, String escapeString) {

        if (StringUtil.isEmpty(originalString) || StringUtil.isEmpty(charsToEscape) || StringUtil.isEmpty(escapeString))
            return originalString;

        StringBuilder output = new StringBuilder(originalString.length());
        for (int i = 0; i < originalString.length(); i++) {
            char currChar = originalString.charAt(i);
            if (charsToEscape.indexOf(currChar) > -1)
                output.append(escapeString);
            output.append(currChar);
        }

        return output.toString();
    }

    /**
     * Splits the given input String into an array of segments
     * which are at most 'length' long.
     *
     * For example, an 81 char string will return an array
     * of length 9. The last indexes value would be a string
     * of length 1 and whose value is the last character of the
     * input string.
     *
     * @param input
     * @param length must be greater than zero
     * @return
     */
    public static String[] splitByLength(String input, int length) {

        if (length < 1)
            throw new IllegalArgumentException("length must be greater than zero");
        if (StringUtil.isEmpty(input))
            return new String[0];

        int segments = input.length() / length;
        if (segments * length < input.length())
            segments++;

        String[] result = new String[segments];

        int resultIndex = 0;
        int startIndex = 0;
        int stopIndex = input.length() > length ? length : input.length();
        while (stopIndex <= input.length()) {

            result[resultIndex] = input.substring(startIndex, stopIndex);

            if (stopIndex == input.length())
                break;

            resultIndex++;
            startIndex = stopIndex;
            stopIndex = input.length() > stopIndex + length ? stopIndex + length : input.length();
        }

        return result;
    }

    public static String defaultWhenNull(String value, String defValue) {
        return (value == null) ? defValue : value;
    }
    public static String defaultWhenEmpty(String value, String defValue) {
        return isEmpty(value) ? defValue : value;
    }

    /**************************************************************************
     *  Phone Numbers
     **************************************************************************/
    public static String formatPhoneNumber(String phoneNumber) {
        if (StringUtil.isEmpty(phoneNumber))
            return phoneNumber;
        String result = cleanString(phoneNumber, "0123456789");
        result = (result.length() > 10) ? result.substring(0, 10) : result;
        if (result.length() == 10) {
            result = "(" + result.substring(0, 3) + ") " + result.substring(3,6) + "-" + result.substring(6, result.length());
        } else if (result.length() == 7) {
            result = result.substring(3,6) + "-" + result.substring(6, result.length());
        }

        return result;
    }


}