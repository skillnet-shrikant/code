package com.mff.userprofiling.droplet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet returns Tax exemption classifications to be displayed on Tax Exemption page.
 * 
 * @author DMI
 */
public class TaxExemptionClassificationsDroplet extends DynamoServlet {

    public static final String OUTPUT_OPARAM = "output";
	
	private Repository mContentRepository;
	private String mQueryByActive;
	private String mItemDesc;
	
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
																	throws ServletException, IOException {
		
		List<RepositoryItem> result = null;
		RepositoryView view;
		
		try {
			
			view = getContentRepository().getView(getItemDesc());
			
			RqlStatement statement = RqlStatement.parseRqlStatement(getQueryByActive());
			
			final Object params[] = new Object[1];
			params[0] = true;
			
			RepositoryItem [] classificationItems = statement.executeQuery (view, params);
			
			if (isLoggingDebug()){
				logDebug("service(): classificationItems: "+ classificationItems );
			}
			
			if (classificationItems != null && classificationItems.length > 0) {
				result = Arrays.asList(classificationItems);
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Exception while getting classification items: " + e, e);
			}
		}

		pRequest.setParameter("classifications", result);
		pRequest.serviceLocalParameter("output", pRequest, pResponse);
	}
	
	
	public Repository getContentRepository() {
		return mContentRepository;
	}

	public void setContentRepository(Repository pContentRepository) {
		this.mContentRepository = pContentRepository;
	}

	public String getQueryByActive() {
		return mQueryByActive;
	}

	public void setQueryByActive(String pQueryByActive) {
		this.mQueryByActive = pQueryByActive;
	}

	public String getItemDesc() {
		return mItemDesc;
	}

	public void setItemDesc(String pItemDesc) {
		this.mItemDesc = pItemDesc;
	}
}