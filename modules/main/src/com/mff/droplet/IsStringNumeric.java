package com.mff.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class IsStringNumeric extends DynamoServlet {
  
  /* INPUT PARAMETERS */
  public static final ParameterName PARAM_STRING = ParameterName.getParameterName("string");
  /* OPEN PARAMETERS */
  public static final ParameterName OPARAM_TRUE = ParameterName.getParameterName("true");
  public static final ParameterName OPARAM_FALSE = ParameterName.getParameterName("false");
  
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    String lString = pRequest.getParameter(PARAM_STRING);
    if(StringUtils.isNotBlank(lString.trim()) && StringUtils.isNumericOnly(lString.trim())) {
      pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);
    }else {
      pRequest.serviceLocalParameter(OPARAM_FALSE, pRequest, pResponse);
    }
  }

  
  
}
