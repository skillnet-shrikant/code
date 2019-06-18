package com.mff.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Abstract class that provides support for generating JSON Payloads using Google's Gson library.
 * 
 * Subclasses are primarily responsible for overriding the abstract createObjectForRender method.
 * They may also override createGsonBuilder to modify the way JSON is rendered.
 *
 */
public abstract class GsonSupportDroplet extends DynamoServlet 
{

	//------------------------------------------
	// PRIVATE VARIABLES
	// ------------------------------------------
	private static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	private static final ParameterName EMPTY = ParameterName.getParameterName("empty");
	private static final ParameterName ERROR = ParameterName.getParameterName("error");
	private static final ParameterName PRETTY_PRINT = ParameterName.getParameterName("prettyPrint");
	private static final String RAW_OBJECT = "rawObject";
	private boolean mPrettyPrint = false;
	private String mDefaultParamName = "jsonObject";
	
	//------------------------------------------
	// PROTECTED VARIABLES
	// ------------------------------------------
	protected static final ParameterName JSON_PARAM_NAME = ParameterName.getParameterName("jsonParamName");
	
	
	//------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------

	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException 
	 {
	
		 Object objectToRender = null;
		
		try 
		{
			objectToRender = createObjectForRender(pRequest);
		} 
		catch (IllegalArgumentException e) 
		{
			if (isLoggingError()) 
			{
				logError(e);
			}
			
			pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
			return;
		}
		
		if (objectToRender == null) 
		{
			if (isLoggingDebug()) 
			{
				logDebug("object to render came back null, servicing empty oparam");
			}
			
			pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
			return;
		}
		
		// Render the object as JSON
		Gson gson = createGson(pRequest);
		
		pRequest.setParameter(getParamName(pRequest), gson.toJson(objectToRender));
		pRequest.setParameter(RAW_OBJECT, objectToRender);
		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}
	
	//------------------------------------------
	// PRIVATE METHODS
	// ------------------------------------------
	
	/**
	 * @param pRequest
	 * @return
	 */
	private final boolean determinePrettyPrint(DynamoHttpServletRequest pRequest) 
	{
		Object objPrettyPrint = pRequest.getLocalParameter(PRETTY_PRINT);
	
		if (objPrettyPrint instanceof String) 
		{
			return Boolean.valueOf((String) objPrettyPrint);
		}
		return isPrettyPrint();
	}
	
	
	/**
	 * Create the GSON instance, based on createGsonBuilder().
	 * There should be no need for subclasses to override this, and hence
	 * it is private and final.
	 * 
	 * @param pRequest
	 * @return
	 */
	private final Gson createGson(DynamoHttpServletRequest pRequest) 
	{
		GsonBuilder builder = createGsonBuilder(pRequest);
		return builder.create();
	}

	/**
	 * Returns the parameter name to set the rendered object to.
	 * @param pRequest
	 * @return
	 */
	private final String getParamName(DynamoHttpServletRequest pRequest) {
		String paramName = pRequest.getParameter(JSON_PARAM_NAME);
		if (!StringUtils.isBlank(paramName)) {
			return paramName;
		}
		return getDefaultParamName();
	}
	
	//------------------------------------------
	// PROTECTED METHODS
	// ------------------------------------------
	
	/**
	 * Create a GsonBuilder used to create Gson instances.  Subclasses can override this
	 * and augment the default behavior by calling super and then modifying the builder,
	 * or replace it entirely.
	 * 
	 * @param pRequest
	 * @return
	 */
	protected GsonBuilder createGsonBuilder(DynamoHttpServletRequest pRequest) 
	{
		GsonBuilder builder = new GsonBuilder();
		
		if (determinePrettyPrint(pRequest)) {
			builder.setPrettyPrinting();
		}
		
		return builder;
	}

	/**
	 * Creates the object for rendering as JSON.  Subclasses should implement business logic here.  If anything
	 * is wrong with the request, for example a required parameter is missing, subclasses should throw IllegalArgumentException,
	 * which will be logged and the error oparam rendered.
	 * If a subclass returns null from this method, the empty oparam will be rendered.
	 * 
	 * @param pRequest
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected abstract Object createObjectForRender(DynamoHttpServletRequest pRequest) throws IllegalArgumentException;

	//------------------------------------------
	// GETTERS & SETTERS
	// ------------------------------------------
	
	public String getDefaultParamName() {
		return mDefaultParamName;
	}

	public void setDefaultParamName(String pDefaultParamName) {
		mDefaultParamName = pDefaultParamName;
	}
	
	
	public boolean isPrettyPrint() {
		return mPrettyPrint;
	}

	
	public void setPrettyPrint(boolean pPrettyPrint) {
		mPrettyPrint = pPrettyPrint;
	}
	
}
