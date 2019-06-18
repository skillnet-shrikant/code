package com.mff.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimensionSearchResultGroup;
import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.UrlENEQuery;

public class BrandUrlGeneratorDroplet extends DynamoServlet {

	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_BRAND_NAME = ParameterName.getParameterName("brandName");

	/* OUTPUT PARAMETERS */
	public static final String BRAND_URL = "url";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");

	/* CONSTANTS */
	private static final String ENCODING_UTF8 = "UTF-8";
	private static final String DIMSEARCH_QUERYSTRING = "D=";
	private static final String DX_VALUE = "mode matchall rel exact";

	private String urlPrefix;
	private String brandDimId;
	private String mdexHost;
	private int mdexPort;


	@Override
	public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
		throws ServletException, IOException {

		String brandName = (String) pRequest.getObjectParameter(PARAM_BRAND_NAME);
		String brandDimVal = null;

		if (brandName == null || brandName.isEmpty()) {
			if (isLoggingWarning()) {
				logWarning("Missing required productId parameter in request: "+pRequest.getRequestURI());
			}
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}


		try {
			if (isLoggingDebug()) {
				vlogDebug("Looking up brand [{0}].", brandName);
			}
			brandDimVal = getbrandNVal(brandName);
			if (isLoggingDebug()) {
				vlogDebug("Found brand dim val [{0}].", brandDimVal);
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception trying to find brand [" + brandName + "]");
			}
		}
		// no brand found.
		if (brandDimVal == null) {
			if (isLoggingWarning()) {
				logWarning("Brand Dim Val not found with Name [" + brandName + "]");
			}
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}


		/* Brand URL */
		StringBuffer url = new StringBuffer();
		url.append(getUrlPrefix());
		url.append("/").append(cleanString(brandName));
		url.append("/_/N-" + brandDimVal);
		pRequest.setParameter(BRAND_URL, url.toString());
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

	}

	@SuppressWarnings("unchecked")
	public String getbrandNVal(String brandName)
			throws Exception {

		String brandNVal = null;

		StringBuilder queryString = new StringBuilder()
			.append(DIMSEARCH_QUERYSTRING)
			.append(brandName);

		UrlENEQuery query;
		try {
			query = new UrlENEQuery(queryString.toString(),ENCODING_UTF8);
			query.setDx(DX_VALUE);
			query.setDk("1");
			query.setDi(getBrandDimId());
			query.setDimSearchCompound(false);

			ENEQueryResults eneResults = performQuery(query,
					getMdexHost(),
					getMdexPort());

			if (eneResults.containsDimSearch()) {

				for (DimensionSearchResultGroup dimGroup : (List<DimensionSearchResultGroup>)
						eneResults.getDimensionSearch().getResults()) {
					for (DimLocationList dimLocList : (List<DimLocationList>) dimGroup) {
						for (DimLocation dimLoc : (List<DimLocation>) dimLocList) {

							DimVal dVal = dimLoc.getDimValue();

							String Di = Long.toString(dVal.getDimensionId());

							if (dVal.getName().equals(brandName)) {

								brandNVal = Long.toString(dVal.getId());

								if (isLoggingDebug()) {
									vlogDebug("DimensionID: " + Di + " Brand Dim Val: " + brandNVal +"[{0}].", brandNVal);
								}

								}
							}
						}
					}
				}


		} catch(ENEQueryException e) {
			throw new Exception(e);
		}


		return brandNVal;

	}

	private ENEQueryResults performQuery(ENEQuery query, String mdexHost, int mdexPort) throws Exception {
		try {
			ENEConnection conn = new HttpENEConnection(mdexHost, mdexPort);
			return conn.query(query);
		} catch (ENEQueryException e)
		{
			// Throw a wrapped exception rather than an API-specific one
			throw new Exception(e);
		}
	}

	private static String cleanString(String value) {
		String result = "";
		if(value!=null) {
			result = value.toLowerCase().replaceAll("&\\S+?;|[^a-zA-Z0-9\\s]", "")
							.replaceAll("\\s+", "-");
		}
		return result;
	}


	/* GETTERS/SETTERS */

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	/**
	 * @return the mdexHost
	 */
	public String getMdexHost() {
		return mdexHost;
	}

	/**
	 * @param pMdexHost the mdexHost to set
	 */
	public void setMdexHost(String pMdexHost) {
		mdexHost = pMdexHost;
	}

	/**
	 * @return the mdexPort
	 */
	public int getMdexPort() {
		return mdexPort;
	}

	/**
	 * @param pMdexPort the mdexPort to set
	 */
	public void setMdexPort(int pMdexPort) {
		mdexPort = pMdexPort;
	}

	/**
	 * @return the brandDimId
	 */
	public String getBrandDimId() {
		return brandDimId;
	}

	/**
	 * @param pBrandDimId the brandDimId to set
	 */
	public void setBrandDimId(String pBrandDimId) {
		brandDimId = pBrandDimId;
	}



}
