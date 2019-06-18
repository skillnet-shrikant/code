package com.mff.droplet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import com.mff.constants.MFFConstants;
import com.mff.util.MFFUtils;
import com.mff.commerce.catalog.MFFCatalogTools;

import javax.servlet.ServletException;
import java.io.IOException;

public class ProductUrlGeneratorDroplet extends DynamoServlet {

	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_PRODUCT_ID = ParameterName.getParameterName("productId");

	/* OUTPUT PARAMETERS */
	public static final String PRODUCT_URL = "url";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");

	private Pattern pattern;
	private String urlPrefix;
	private MFFCatalogTools mCatalogTools;

	@Override
	public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
		throws ServletException, IOException {

		String productId = (String) pRequest.getObjectParameter(PARAM_PRODUCT_ID);

		if (productId == null || productId.isEmpty()) {
			if (isLoggingWarning()) {
				logWarning("Missing required productId parameter in request: "+pRequest.getRequestURI());
			}
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}

		RepositoryItem product = null;
		// Look up the product
		try {
			if (isLoggingDebug()) {
				vlogDebug("Looking up product [{0}].", productId);
			}
			product = getCatalogTools().findProduct(productId);
			if (isLoggingDebug()) {
				vlogDebug("Found product [{0}].", product);
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Exception trying to find product [" + productId + "]");
			}
		}
		// no product returned.
		if (product == null) {
			if (isLoggingWarning()) {
				logWarning("Product not found with repository Id [" + productId + "]");
			}
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}
		pRequest.setParameter("product", product);

		/* Product URL */
		StringBuffer url = new StringBuffer();
		String productDisplayName = (String) product.getPropertyValue("seoDescription");
//		String productBrandName = (String) product.getPropertyValue("brand");
//		String productDisplayName = (String) product.getPropertyValue("seoDisplayName");
		url.append(getUrlPrefix());
		if (null != productDisplayName) {
			url.append(format(productDisplayName.toLowerCase()));
		}
		url.append("/").append(productId);
		pRequest.setParameter(PRODUCT_URL, url.toString());
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

	}

	public String format(String pString) {
		Matcher matcher = getPattern().matcher(pString);
		return matcher.replaceAll("-");
	}

	/* GETTERS/SETTERS */

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	public Pattern getPattern() { return pattern; }

	public void setPattern(Pattern pPattern) {
		pattern = pPattern;
	}

	public MFFCatalogTools getCatalogTools() { return mCatalogTools; }

	public void setCatalogTools(MFFCatalogTools pCatalogTools) { mCatalogTools = pCatalogTools; }

}
