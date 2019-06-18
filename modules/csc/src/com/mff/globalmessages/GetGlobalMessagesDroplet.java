package com.mff.globalmessages;

import java.io.IOException;

import javax.servlet.ServletException;

import com.google.common.base.Strings;

import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class GetGlobalMessagesDroplet extends DynamoServlet {

	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_MSG_DESTINATION = ParameterName.getParameterName("msgDestination");
	
	/* OUTPUT PARAMETERS */
	public static final String PARAM_GLOBAL_MSG_RESPONSE = "globalMessages";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_ERROR = ParameterName.getParameterName("error");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");
	
	GlobalMessagesManager mGlobalMessagesManager;
	
	private String mGetMessagesQuery;
	
	public void setGetMessagesQuery(String pGetMessagesQuery){
		this.mGetMessagesQuery=pGetMessagesQuery;
	}
	
	public String getGetMessagesQuery(){
		return this.mGetMessagesQuery;
	}
	
	public GlobalMessagesManager getGlobalMessagesManager() {
		return mGlobalMessagesManager;
	}


	public void setGlobalMessagesManager(GlobalMessagesManager pGlobalMessagesManager) {
		mGlobalMessagesManager = pGlobalMessagesManager;
	}


	@Override
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes)
			throws ServletException, IOException {
		try {
			String msgDestination = (String) pReq.getObjectParameter(PARAM_MSG_DESTINATION);
			if(Strings.isNullOrEmpty(msgDestination)){
				vlogWarning("Missing required Message Destination parameter");
				pReq.serviceLocalParameter(OPARAM_ERROR, pReq, pRes);
				return;
			}
			vlogDebug("Message Destination {0}", msgDestination);
			MessagesResponse mMessagesResponse=getGlobalMessagesManager().getGlobalMessageAndAlerts(msgDestination,getGetMessagesQuery());
			pReq.setParameter(PARAM_GLOBAL_MSG_RESPONSE, mMessagesResponse);
			if(mMessagesResponse==null || (mMessagesResponse.getAlerts().isEmpty() && mMessagesResponse.getMessages().isEmpty())){
				vlogDebug("No Messages to report");
				pReq.serviceLocalParameter(OPARAM_EMPTY, pReq, pRes);
			}else {
				vlogDebug("Messages are available to report");
				pReq.serviceLocalParameter(OPARAM_OUTPUT, pReq, pRes);
			}
		} catch (RepositoryException e) {
			logError("GetGlobalMessagesDroplet : Error getting messages/ alerts: " + e);
			pReq.serviceLocalParameter(OPARAM_ERROR, pReq, pRes);
		} catch (Exception e) {
			logError("GetGlobalMessagesDroplet : Error getting messages/ alerts : " + e);
			pReq.serviceLocalParameter(OPARAM_ERROR, pReq, pRes);
		}
	}
}
