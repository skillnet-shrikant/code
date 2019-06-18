package com.mff.droplet;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import com.mff.util.MFFUtils;
import java.io.IOException;
import javax.servlet.ServletException;

public class NavigationUrlGenerator
  extends DynamoServlet
{
	  private static final String REQUESTURI = "requestUri";
	  private static final String PARAMNAME = "paramName";
	  private static final String PARAMVALUE = "paramValue";
	  private static final String URL = "url";
	  private static final String OUTPUT = "output";
  
	  public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	    throws ServletException, IOException {
		    String lParamName = pRequest.getParameter(PARAMNAME);
		    String lParamValue = pRequest.getParameter(PARAMVALUE);
		    String lRequestUri = pRequest.getParameter(REQUESTURI);
		    if (StringUtils.isEmpty(lRequestUri)) {
		    	lRequestUri = pRequest.getRequestURI();
		    }
		    StringBuilder lUrlBuilder = new StringBuilder();
		    if (StringUtils.isEmpty(pRequest.getQueryParameter(lParamName))) {
		    	lUrlBuilder.append(lRequestUri).append("?").append(pRequest.getQueryString()).append("&").append(lParamName).append("=").append(lParamValue);
		    } else {
		    	lUrlBuilder.append(lRequestUri).append("?").append(MFFUtils.removeParamFromQueryString(pRequest.getQueryString(), lParamName)).append("&").append(lParamName).append("=").append(lParamValue);
		    }
		    pRequest.setParameter(URL, lUrlBuilder.toString());
		    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	 }
}
