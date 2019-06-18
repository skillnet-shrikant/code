package com.mff.droplet;

import java.io.IOException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import javax.servlet.ServletException;

/**
 * This Droplet does an InstanceOf Check & places into the Request
 *
 */
public class InstanceOf extends DynamoServlet 
{
	//------------------------------------------
	// PRIVATE VARIABLES
	// ------------------------------------------
	private static final ParameterName FALSE = ParameterName.getParameterName("false");
	private static final ParameterName ERROR = ParameterName.getParameterName("error");
	private static final ParameterName OBJECT = ParameterName.getParameterName("object");
	private static final ParameterName KLASS = ParameterName.getParameterName("klass");
	private static final ParameterName TRUE = ParameterName.getParameterName("true");
	
	/**
	 * Service method
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException 
	{
	
		Object obj = pRequest.getObjectParameter(OBJECT);
		
		if (obj == null) {
			if (isLoggingError()) {
				logError("object was null");
			}
			pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
			return;
		}
		
		Object klass = pRequest.getObjectParameter(KLASS);
		if (klass == null) {
			if (isLoggingError()) {
				logError("klass was null");
			}
			pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
			return;
		}
		
		Class<?> classObj = null;
		if (klass instanceof Class) {
			classObj = (Class<?>) klass;
		} else if (klass instanceof String) {
			try {
				classObj = Class.forName((String) klass);
			} catch (ClassNotFoundException e) {
				if (isLoggingError()) {
					logError(e);
				}
				pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
				return;
			}
		}
		
		if (classObj == null) {
			if (isLoggingError()) {
				logError("failed to find class based on param: " + klass);
			}
			pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
			return;
		}
		
		
		ParameterName toRender = classObj.isInstance(obj) ? TRUE : FALSE;
		
		//Set the variable toRender with the value of TRUE or FALSE into the Request
		pRequest.serviceLocalParameter(toRender, pRequest, pResponse);
	}
}
