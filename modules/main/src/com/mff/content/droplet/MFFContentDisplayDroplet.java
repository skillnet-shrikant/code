package com.mff.content.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.content.MFFContentHelper;

public class MFFContentDisplayDroplet extends DynamoServlet {
	
	private static final String OUTPUT_OPARAM = "output";
	private static final String EMPTY_OPARAM = "empty";
	private static final String CONTENTS_OUTPUT = "allContents";
	private MFFContentHelper mContentHelper;
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		if (isLoggingDebug()){
			logDebug("MFFContentsDroplet: Called.");
		}
		
		String itemDesc = pRequest.getParameter("itemDescriptor");
		String rqlQuery = pRequest.getParameter("rqlQuery");
		String contentKey = pRequest.getParameter("contentKey");
		
		List allContents = getContentHelper().getContentsItems(itemDesc, rqlQuery, contentKey);

		if (allContents != null && allContents.size() > 0) {
			
			pRequest.setParameter(CONTENTS_OUTPUT, allContents);
			pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);
		} else {
			if (isLoggingDebug()){
				logDebug("MFFContentsDroplet serivce size empty");
			}
			pRequest.serviceLocalParameter(EMPTY_OPARAM, pRequest, pResponse);
		}
	}

	public MFFContentHelper getContentHelper() {
		return mContentHelper;
	}

	public void setContentHelper(MFFContentHelper pContentHelper) {
		this.mContentHelper = pContentHelper;
	}

}