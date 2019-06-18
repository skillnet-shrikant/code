package com.mff.servlet;

import org.apache.commons.lang3.StringUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.service.webappregistry.WebApp;
import atg.servlet.DynamoHttpServletRequest;

public class MFFStaticIndirectUrlTemplate extends IndirectUrlTemplate {
	
	public static final String ITEMDESC_MFF_STATIC_CONTENT = "mffStaticContent";
	public static final String RQL_STATEMENT = "redirectUrl EQUALS IGNORECASE ?0";
	public static final String MFF_PAGE_CONTENT = "mffPageContent";
	public static final String CONST_URL_SEPERATOR = "/";
	private String mCareersPattern;
	private String mGiftCardsPattern;
	private String mCareersUrlTemplateFormat;
	private String mGiftCardsUrlTemplateFormat;
	
	private Repository mContentRepository;
	
	/**
	 * Method overrides IndirectUrlTemplate.getForwardUrl method to create direct urls using params in 
	 * indirect url.
	 *
	 */
	public String getForwardUrl(DynamoHttpServletRequest pRequest, String pIndirectUrl, WebApp pDefaultWebApp, Repository pDefaultRepository)
			throws ItemLinkException {
		
		String uri = pRequest.getRequestURI();
		
		String jumpLink = getJumpLink(pRequest, uri);
		vlogDebug("service: jumpLink: " + jumpLink);
	
		return jumpLink;
	}
	
	protected String getJumpLink(DynamoHttpServletRequest pRequest, String uri) {

		String result = null;
		
		try {
			
			boolean isCareers = false;
			boolean isGiftCards = false;
			
			if(StringUtils.containsIgnoreCase(uri, getCareersPattern())){
				vlogDebug("findPage: isCareers: " + isCareers);
				isCareers = true;
			} else if(StringUtils.containsIgnoreCase(uri, getGiftCardsPattern())){
				vlogDebug("findPage: isGiftCards: " + isGiftCards);
				isGiftCards = true;
			}
			
			String[] pageUrlElements =uri.split(CONST_URL_SEPERATOR);

			String redirectUrl = pageUrlElements[pageUrlElements.length - 1];
			vlogDebug("findPage: redirectUrl: " + redirectUrl);

			RepositoryItem itemPage = findPage(redirectUrl);

			if (null != itemPage) {
				
				if (isCareers){
					result = getCareersUrlTemplateFormat();
				} else if (isGiftCards){
					result = getGiftCardsUrlTemplateFormat();
				} else {
					result = getForwardUrlTemplateFormat();
				}
				
				pRequest.setAttribute(MFF_PAGE_CONTENT, itemPage);
			}

		} catch (Exception e) {
			logError(e);
		}
		vlogDebug("getJumpLink: result: " + result);
		return result;
	}
	
	private RepositoryItem findPage(String pRedirectUrl) {
		
		RepositoryItem result = null;
		Repository repository = getContentRepository();

		if (null != repository ) {
			try {
				RepositoryView view = repository.getView(ITEMDESC_MFF_STATIC_CONTENT);
				RqlStatement statement = null;
				statement = RqlStatement.parseRqlStatement(RQL_STATEMENT);
				Object[] params = new Object[1];
				params[0] = pRedirectUrl;

				RepositoryItem[] items = statement.executeQuery(view, params);
				
				vlogDebug ("findPage: items: " + items);
				
				if ((items != null) && (items.length > 0)) {
					for (RepositoryItem item : items) {
						result = item;
						break;
					}
				}
				
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(e);
				}
			}
		}
		return result;
	}

	public Repository getContentRepository() {
		return mContentRepository;
	}

	public void setContentRepository(Repository pContentRepository) {
		this.mContentRepository = pContentRepository;
	}

	public String getCareersPattern() {
		return mCareersPattern;
	}

	public void setCareersPattern(String pCareersPattern) {
		mCareersPattern = pCareersPattern;
	}

	public String getCareersUrlTemplateFormat() {
		return mCareersUrlTemplateFormat;
	}

	public void setCareersUrlTemplateFormat(String pCareersUrlTemplateFormat) {
		mCareersUrlTemplateFormat = pCareersUrlTemplateFormat;
	}

	public String getGiftCardsUrlTemplateFormat() {
		return mGiftCardsUrlTemplateFormat;
	}

	public void setGiftCardsUrlTemplateFormat(String pGiftCardsUrlTemplateFormat) {
		mGiftCardsUrlTemplateFormat = pGiftCardsUrlTemplateFormat;
	}

	public String getGiftCardsPattern() {
		return mGiftCardsPattern;
	}

	public void setGiftCardsPattern(String pGiftCardsPattern) {
		mGiftCardsPattern = pGiftCardsPattern;
	}

}