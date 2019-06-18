package com.mff.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;



public class RemoveSessionIdDroplet extends DynamoServlet {

	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_URL = ParameterName.getParameterName("inputUrl");

	/* OUTPUT PARAMETERS */
	public static final String finalURL = "url";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");




	@Override
	public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
		throws ServletException, IOException {

		String encodedCanonicalUrl = (String) pRequest.getObjectParameter(PARAM_URL);
		String finalUrl = pRequest.getRequestURI();
		
		
		if (encodedCanonicalUrl == null || encodedCanonicalUrl.isEmpty()) {
			if (isLoggingWarning()) {
				logWarning("Missing required param url parameter in request: "+pRequest.getRequestURI());
			}
			pRequest.setParameter(finalURL, finalUrl.toString());
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}
		else {
			String urlParams="";
			String actualUrl="";
			String urls="";
			vlogDebug("The input url is :"+encodedCanonicalUrl);
			if(encodedCanonicalUrl.toLowerCase().contains("jsessionid")){
				if(encodedCanonicalUrl.contains("?")){
					String[] urlSplit=encodedCanonicalUrl.split("?");
					if(urlSplit.length!=1){
						urlParams=urlSplit[1];
						urls=urlSplit[0];
						if(urlParams.toLowerCase().contains("jsessionid")){
							String[] paramsSplitWithSessionId=urlParams.split(";jsessionid");
							urlParams=paramsSplitWithSessionId[0];
							actualUrl=urls+"?"+urlParams;
						}
						else if(urls.toLowerCase().contains("jsessionid")){
							String[] urlWithSessionId=urls.split(";jsessionid");
							urls=urlWithSessionId[0];
							actualUrl=urls+"?urlParams";
						}
					}
					
				}
				else {
					String[] urlWithSessionId=encodedCanonicalUrl.split(";jsessionid");
					urls=urlWithSessionId[0];
					actualUrl=urls;
					
				}
			}
			else {
				actualUrl=encodedCanonicalUrl;
			}
			
			vlogDebug("Actual url is :"+actualUrl);
			if(!actualUrl.isEmpty())
				finalUrl=actualUrl;
			
			pRequest.setParameter(finalURL, finalUrl.toString());
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

		}
		
	}

}
