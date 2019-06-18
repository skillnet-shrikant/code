package com.mff.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class MergeLists extends DynamoServlet {
	 /* INPUT PARAMETERS */
	  public static final ParameterName LIST_ONE = ParameterName.getParameterName("list1");
	  public static final ParameterName LIST_TWO = ParameterName.getParameterName("list2");
	  /*OUTPUT PARAMETERS */
	  public static final String FINAL_LIST ="finalList";
	  /* OPEN PARAMETERS */
	  public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	  
	  @Override
	  public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	    throws ServletException, IOException {
		  List pListOne = (List) pRequest.getObjectParameter(LIST_ONE);
		  List pListTwo = (List) pRequest.getObjectParameter(LIST_TWO);
		  if(pListOne==null || pListOne.isEmpty()) {
			  pListOne=pListTwo;
		  }else if(pListTwo==null || pListTwo.isEmpty()) {
			  //nothing to do 
		  }else {
			  pListOne.addAll(pListTwo);
		  }
		  pRequest.setParameter(FINAL_LIST, pListOne);
		  pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		  
	  }

}
