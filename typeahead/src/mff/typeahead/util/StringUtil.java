package mff.typeahead.util;

import com.endeca.soleng.urlformatter.seo.LowerCaseStringFormatter;
import com.endeca.soleng.urlformatter.seo.RegexStringFormatter;
import com.endeca.soleng.urlformatter.seo.SeoDimLocationFormatter;
import com.endeca.soleng.urlformatter.seo.SeoNavStateFormatter;
import com.endeca.soleng.urlformatter.seo.SeoUrlFormatter;
import com.endeca.soleng.urlformatter.seo.StringFormatter;
import com.endeca.soleng.urlformatter.seo.StringFormatterChain;
import com.endeca.soleng.urlformatter.seo.UrlParamEncoder;

/**
 * Contains String manipulation methods and constants
 * @author foldenburg
 */
public final class StringUtil {
	private static SeoUrlFormatter seoUrlFormatter ;
	/**
	 * Private Constructor
	 */
	private StringUtil() { }

	/**
	 * Replaces key special characters with spaces, ignores other non-alphanumeric characters,
	 * and squeezes whitespace.
	 *
	 * @param inputString Input string
	 * @return Sanitized string
	 */
	public static String sanitize(String inputString) {
		if(inputString == null) {
			return new String();
		}
		return inputString.toLowerCase()
				.replaceAll("[#%&*'\"/\\+\\-\\.]"," ")
				.replaceAll("[^a-z0-9 ]", "")
				.replaceAll("\\s+"," ").trim();
	}

	public static void initSeoUrlFormatter(){
		//Initialize the SEOUrlFormatter
		if(null == seoUrlFormatter){
			seoUrlFormatter = new SeoUrlFormatter();
			seoUrlFormatter.setPathSeparatorToken("_");
			seoUrlFormatter.setPathKeyValueSeparator('-');
			String[] pathParams = new String[1];
			pathParams[0] = "N";
			seoUrlFormatter.setPathParamKeys(pathParams);

			UrlParamEncoder[] pUrlParamEncoders = new UrlParamEncoder[1];
			pUrlParamEncoders[0] = new UrlParamEncoder();
			pUrlParamEncoders[0].setParamKey("N");
			seoUrlFormatter.setUrlParamEncoders(pUrlParamEncoders);

			SeoNavStateFormatter pNavStateFormatter = new SeoNavStateFormatter();
			pNavStateFormatter.setUseDimensionNameAsKey(true);
			seoUrlFormatter.setNavStateFormatter(pNavStateFormatter);

			SeoDimLocationFormatter[] pDimLocationFormatters = new SeoDimLocationFormatter[1];
			pDimLocationFormatters[0] = new SeoDimLocationFormatter();
			pDimLocationFormatters[0].setKey("product.category");
			pDimLocationFormatters[0].setAppendRoot(false);
			pDimLocationFormatters[0].setAppendAncestors(false);
			pDimLocationFormatters[0].setAppendDescriptor(true);
			pDimLocationFormatters[0].setSeparator('/');
			StringFormatterChain chain = new StringFormatterChain();
			RegexStringFormatter pDimValStringFormatter = new RegexStringFormatter();
			pDimValStringFormatter.setPattern("[\\W]+");
			pDimValStringFormatter.setReplacement("-");
			pDimValStringFormatter.setReplaceAll(true);
			LowerCaseStringFormatter lowerCaseFormatter = new LowerCaseStringFormatter();
			StringFormatter[] formatters = new StringFormatter[2];
			formatters[0] = pDimValStringFormatter;
			formatters[1] = lowerCaseFormatter;
			chain.setStringFormatters(formatters);
			pDimLocationFormatters[0].setDimValStringFormatter(chain);
			pNavStateFormatter.setDimLocationFormatters(pDimLocationFormatters);
		}
	}

	public static SeoUrlFormatter getSeoUrlFormatter(){
		return seoUrlFormatter;
	}

	public static String cleanString(String value) {
		String result = "";
		if(value!=null) {
			result = value.toLowerCase().replaceAll("&\\S+?;|[^a-zA-Z0-9\\s]", "")
							.replaceAll("\\s+", "-");
		}
		return result;
	}



}
