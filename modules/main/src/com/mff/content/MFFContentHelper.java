package com.mff.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class MFFContentHelper extends GenericService {
	
	private Repository mContentRepository;	
	private String mQueryAllContents;
	private String mQueryAllStaticPageContents;
	private static String CONST_URL_STATIC = "/static/";

	public List<RepositoryItem> getContentsItems (String pItemDesc, String pRql, String pContentKey) {
		
		if (isLoggingDebug()){
			logDebug("getContentsItems(): Called: pRql:" + pRql + " : pItemDesc: " + pItemDesc + "pContentKey: " + pContentKey);
		}
		
		List<RepositoryItem> result = null;
		RepositoryView view;
		try {
			
			view = getContentRepository().getView(pItemDesc);
			
			RqlStatement statement = RqlStatement.parseRqlStatement(pRql);
			
			Object params[] = new Object[1];
			params[0] = pContentKey;
			RepositoryItem [] contentItems = statement.executeQuery (view, params);
			
			if (isLoggingDebug()){
				logDebug("getContentsItems(): contentItems: "+ contentItems );
			}
			
			if (contentItems != null && contentItems.length > 0) {
				result = Arrays.asList(contentItems);
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Exception while getting content items: " + e, e);
			}
		}
		return result;
	}

	public List<String> getAllStaticURLs(){
		List<String> lAllUrlsList=new ArrayList<String>();
		List<RepositoryItem> lStaticContentRepoItems = getContentsItems ("mffStaticContent", getQueryAllStaticPageContents(), null);
		if(lStaticContentRepoItems!=null){
			for(RepositoryItem pStaticContentRepoItem : lStaticContentRepoItems){
				String lPageURL = (String)pStaticContentRepoItem.getPropertyValue("redirectUrl");
				if(StringUtils.isNotEmpty(lPageURL)){
					lAllUrlsList.add(CONST_URL_STATIC + lPageURL);
				}
			}
		}
		return lAllUrlsList;
	}
	
	public String getQueryAllContents() {
		return mQueryAllContents;
	}

	public void setQueryAllContents(String pQueryAllContents) {
		this.mQueryAllContents = pQueryAllContents;
	}

	public Repository getContentRepository() {
		return mContentRepository;
	}

	public void setContentRepository(Repository pContentRepository) {
		this.mContentRepository = pContentRepository;
	}

	public String getQueryAllStaticPageContents() {
		return mQueryAllStaticPageContents;
	}

	public void setQueryAllStaticPageContents(
			String pQueryAllStaticPageContents) {
		mQueryAllStaticPageContents = pQueryAllStaticPageContents;
	}

}