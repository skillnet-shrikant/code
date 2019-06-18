package mff.typeahead.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to build URLs to product, npc, and brand pages
 * @author foldenburg
 *
 */
public final class UrlBuilder {

	// Logging objects
	private static final String CLASSNAME = "UrlBuilder";
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

	// Constants
	private static final String PRODUCT_IMAGE_PATH = "/resources/product/image";
	private static final char SLASH = '/';
	private static final int DEFAULT_URL_BUFFER_SIZE = 256;
	private static final Pattern pattern = Pattern.compile("[\\W]+");

	/**
	 * Private constructor
	 * This class should not be instantiated as all public methods should be static
	 */
	private UrlBuilder() { }

	/**
	 * Build a product url
	 * @param itemNumber Item number from Endeca
	 * @param vendorNumber Vendor number
	 * @param modelNumber Model number
	 * @return A String containing the product URL
	 */
	public static String buildProductUrl(String repositoryId, String displayName) {
		final String methodName = "buildProductUrl";
		StringBuilder url = new StringBuilder();
		try {
			url.append("/detail/");
			if( null != displayName){
				Matcher matcher = pattern.matcher(displayName);
				url.append(matcher.replaceAll("-").toLowerCase()).append("/");
			}
			url.append(repositoryId);

		} catch (Exception e) {
			LOGGER.logp(Level.SEVERE, CLASSNAME, methodName,
					"An error occurred while building a URL.",e);
		}
		return url.toString();
	}

	/**
	 * Build a product image URL
	 * @param imageName Image name attribute
	 * @return A String containing the product image URL
	 */
	public static String buildProductImageUrl(String imageName) {
		StringBuilder url = new StringBuilder(PRODUCT_IMAGE_PATH);
		url.append(imageName.substring(0,6));
		url.append(SLASH);
		url.append(imageName);
		url.append("sm.jpg");
		return url.toString();
	}

	/**
	 * Generates an article detail URL from the supplied title and id.
	 * <p>
	 * The format should be /cd_&lt;title&gt;_&lt;id&gt;_&lt;N&gt;_&lt;Ne&gt;
	 * then calls the method to append the queryString.
	 *
	 * @param title Article title
	 * @param anId Article ID
	 * @return String A String containing the article URL
	 */
	public static String generateArticleUrl(String title, String anId){
		StringBuilder urlBuffer =
			new StringBuilder(DEFAULT_URL_BUFFER_SIZE);
		urlBuffer.append("/article");
		urlBuffer.append(cleanString(title));
		urlBuffer.append('_');
		urlBuffer.append(anId);
		urlBuffer.append('_');
		return urlBuffer.toString();
	}

	/**
	 * Wrapper method for buildSearchUrl(String,String)
	 * Uses N=0 (unrefined search)
	 *
	 * @param searchTerms Search terms
	 * @return URL String
	 */
	public static String buildSearchUrl(String searchTerms) {
		return buildSearchUrl(searchTerms, "0");
	}

	/**
	 * Wrapper method for buildTypeaheadDetailUrl(String,String)
	 * Uses N=0 (unrefined search)
	 *
	 * @param searchTerms Search terms
	 * @return URL String
	 */
	public static String buildTypeaheadDetailUrl(String searchTerms) {
		return buildTypeaheadDetailUrl(searchTerms, "0");
	}


	/**
	 * Builds a search URL
	 *
	 * @param searchTerms Search term
	 * @param nValue String nValue to refine to
	 * @return A string containing the Search URL
	 */
	public static String buildSearchUrl(String searchTerms, String nValue) {
		String cleanTerms = cleanString(searchTerms);
		StringBuilder buffer = new StringBuilder();
		buffer.append("/search/");

		buffer.append("_/N-");
		buffer.append(nValue);
		if(!cleanTerms.isEmpty()) {
			buffer.append("?Ntt=");
			buffer.append(cleanTerms);
		}
		return buffer.toString();
	}

	/**
	 * Builds a Typeahead Detail URL
	 *
	 * @param searchTerms Search term
	 * @param nValue String nValue to refine to
	 * @return A string containing the Typeahead Detail URL
	 */
	public static String buildTypeaheadDetailUrl(String searchTerms, String nValue) {
		String cleanTerms = cleanString(searchTerms);
		StringBuilder buffer = new StringBuilder();
		buffer.append("/typeahead/detail/");
		buffer.append(cleanTerms);
		buffer.append(".js");

		return buffer.toString();
	}

	/**
	 * Remove HTML entities (start with '&' followed by 1 or more
	 * non-whitespace (\S+) followed by the first ';' (?;)) or any
	 * non-word characters ([^a-zA-Z0-9\\s]). Finally convert whitespace
	 * (\\s) to '+'.
	 *
	 * @param value Input string
	 * @return Cleaned output string
	 */
	private static String cleanString(String value) {
		String result = "";
		if(value!=null) {
			result = value.replaceAll("&\\S+?;|[^a-zA-Z0-9\\s]", "")
							.replaceAll("\\s+", "+");
		}
		return result;
	}
}
