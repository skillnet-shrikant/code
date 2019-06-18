package com.mff.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class MFFStoreDetailIndirectUrlTemplate extends IndirectUrlTemplate {
	
	public static final String ITEMDESC_LOCATION = "location";
	public static final String RQL_STATEMENT = "website EQUALS IGNORECASE ?0";
	
	public static final String CONST_DEPT_IDENTIFIER = "department";
	public static final String CONST_ARTICLE_ITEM = "articleItem";
	public static final String CONST_ARTICLE_ID = "articleItemId";
	public static final String CONST_LOCATION_ID = "locationId";
	
	public static final String CONST_URL_SEPERATOR = "/";
	public int mVisitStoresPosition = 1;
	public int mStoreNamePosition = 2;
	
	private String mUrlPattern;
	private Repository mLocationRepository;
	
	/**
	 * Method overrides IndirectUrlTemplate.getForwardUrl method to create direct urls using params in 
	 * indirect url.
	 *
	 */
	public String getForwardUrl(DynamoHttpServletRequest pRequest, String pIndirectUrl, WebApp pDefaultWebApp, Repository pDefaultRepository)
			throws ItemLinkException {
		
		String uri = pRequest.getRequestURI();
		
		String jumpLink = getJumpLink(pRequest, uri);
		vlogDebug("getForwardUrl(): jumpLink: " + jumpLink);
	
		return jumpLink;
	}
	
	@SuppressWarnings("rawtypes")
	protected String getJumpLink(DynamoHttpServletRequest pRequest, String uri) {

		String result = null;

		try {

			Map storeInfoMap = findPage(uri);
			vlogDebug("getJumpLink(): storeInfoMap: " + storeInfoMap.keySet());
			vlogDebug("getJumpLink(): storeInfoMap: " + storeInfoMap.values());
			
			result = getForwardUrlTemplateFormat();
				
			pRequest.setAttribute(CONST_LOCATION_ID, storeInfoMap.get(CONST_LOCATION_ID));
			pRequest.setAttribute(CONST_ARTICLE_ID, storeInfoMap.get(CONST_ARTICLE_ID));
			pRequest.setAttribute(CONST_ARTICLE_ITEM, storeInfoMap.get(CONST_ARTICLE_ITEM));

		} catch (Exception e) {
			logError(e);
		}
		vlogDebug("getJumpLink(): forward template: " + result);
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map findPage(String uri) {
		
		vlogDebug("findPage: uri: " + uri);
		RepositoryItem result = null;
		RepositoryItem articleItem = null;
		Repository repository = getLocationRepository();
		Map storeInfo = new HashMap();
		
		boolean deptExits = StringUtils.containsIgnoreCase(uri, CONST_DEPT_IDENTIFIER);
		vlogDebug ("findPage: deptExits: " + deptExits);
		
		String[] pageUrlDepts = null;
		
		if (deptExits){
			
			StringBuffer urlStrBuff = new StringBuffer();
			pageUrlDepts =uri.split(CONST_URL_SEPERATOR);
			
			vlogDebug ("findPage: pageUrlDepts: " + pageUrlDepts);
			
			urlStrBuff.append(CONST_URL_SEPERATOR);
			urlStrBuff.append(pageUrlDepts[getVisitStoresPosition()]);
			urlStrBuff.append(CONST_URL_SEPERATOR);
			urlStrBuff.append(pageUrlDepts[getStoreNamePosition()]);
			
			uri = urlStrBuff.toString();
		}
		
		vlogDebug ("findPage: deptExits: uri: " + uri);

		if (null != repository ) {
			try {
				RepositoryView view = repository.getView(ITEMDESC_LOCATION);
				RqlStatement statement = null;
				statement = RqlStatement.parseRqlStatement(RQL_STATEMENT);
				Object[] params = new Object[1];
				
				params[0] = uri;

				RepositoryItem[] items = statement.executeQuery(view, params);
				String locationId = null;
				
				if ((items != null) && (items.length > 0)) {
					vlogDebug ("findPage: items: " + items.length);
					
					for (RepositoryItem item : items) {
						
						result = item;
						locationId = (String) result.getPropertyValue("locationId");
						
						vlogDebug("findPage: locationId : " + locationId);
						storeInfo.put(CONST_LOCATION_ID, locationId);
						
						articleItem = (RepositoryItem) item.getPropertyValue("storeLandingPage");
						break;
					}
				}
				
				vlogDebug ("findPage: store item : " + result);
				
				if (result != null) {
					
						if (deptExits){
					
						String deptName = pageUrlDepts[pageUrlDepts.length - 1];
						vlogDebug ("findPage: deptName: " + deptName);
							
						List depts = (List) result.getPropertyValue("relatedArticles");
						
						if ((depts != null) && (depts.size() > 0)) {
							for (Object dept : depts) {
								RepositoryItem department = (RepositoryItem)dept;
								if (department!=null && !StringUtils.isEmpty(deptName)){
									String itemDeptName = (String) department.getPropertyValue("name");
									if (deptName.equalsIgnoreCase(itemDeptName)){
										articleItem = department;
										storeInfo.put(CONST_ARTICLE_ID, department.getRepositoryId());
										vlogDebug ("findPage: department item : " + result);
										break;
									}
								}
							}
						}
						
					} else {
						RepositoryItem landingPage = (RepositoryItem) result.getPropertyValue("storeLandingPage");
						if (landingPage!=null){
							String landingPageId = (String) landingPage.getPropertyValue("id");
							vlogDebug ("findPage: landingPageId : " + landingPageId);
							storeInfo.put(CONST_ARTICLE_ID, landingPageId);
						}
					}
				}
				storeInfo.put(CONST_ARTICLE_ITEM, articleItem);
				
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(e);
				}
			}
		}
		
		return storeInfo;
	}

	public String getUrlPattern() {
		return mUrlPattern;
	}

	public void setUrlPattern(String pUrlPattern) {
		this.mUrlPattern = pUrlPattern;
	}

	public Repository getLocationRepository() {
		return mLocationRepository;
	}

	public void setLocationRepository(Repository pLocationRepository) {
		mLocationRepository = pLocationRepository;
	}

	public int getVisitStoresPosition() {
		return mVisitStoresPosition;
	}

	public void setVisitStoresPosition(int pVisitStoresPosition) {
		mVisitStoresPosition = pVisitStoresPosition;
	}

	public int getStoreNamePosition() {
		return mStoreNamePosition;
	}

	public void setStoreNamePosition(int pStoreNamePosition) {
		mStoreNamePosition = pStoreNamePosition;
	}

}