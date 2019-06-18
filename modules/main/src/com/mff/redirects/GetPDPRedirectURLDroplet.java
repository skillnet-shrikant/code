package com.mff.redirects;

import java.io.IOException;

import javax.servlet.ServletException;

import com.mff.constants.MFFConstants;

import atg.droplet.Redirect;
import atg.nucleus.GenericService;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class GetPDPRedirectURLDroplet extends DynamoServlet {

	PDPRedirectTools redirectTools;

	public PDPRedirectTools getRedirectTools() {
		return redirectTools;
	}

	public void setRedirectTools(PDPRedirectTools pRedirectTools) {
		redirectTools = pRedirectTools;
	}

	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_PRODUCT_ID = ParameterName.getParameterName("productId");
	
	/* output parameter */
	public static final String PARAM_REDIRECT_URL = "redirectURL";


	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");
	public static final ParameterName OPARAM_ERROR = ParameterName.getParameterName("error");

	@Override
	public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		
		String productId = pRequest.getParameter(PARAM_PRODUCT_ID);
		String redirectURL = null;
		
		if(productId != null) {
			redirectURL = getRedirectTools().getRedirectURL(productId);
		} else {
			redirectURL = getRedirectTools().getRedirectURL(pRequest);
		}
		
		
		if(redirectURL != null) {
			pRequest.setParameter(PARAM_REDIRECT_URL, redirectURL);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} else {
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
		}
		
	}
}