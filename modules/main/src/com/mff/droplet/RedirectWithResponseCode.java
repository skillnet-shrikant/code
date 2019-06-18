package com.mff.droplet;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.droplet.Redirect;
import atg.service.dynamo.LangLicense;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class RedirectWithResponseCode extends Redirect {
	private static final String URL = "url";
	private static final String RESPONSE_CODE = "responseCode";

	static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

	private static ResourceBundle sResourceBundle = ResourceBundle.getBundle(MY_RESOURCE_NAME, LangLicense.getLicensedDefault());
	
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		super.service(pRequest, pResponse);
		int responseStatusCode = 0;
		if(pRequest.getParameter(RESPONSE_CODE) != null) {
			responseStatusCode = Integer.parseInt(pRequest.getParameter(RESPONSE_CODE));
			pResponse.setStatus(responseStatusCode);
		}
	}
	
}