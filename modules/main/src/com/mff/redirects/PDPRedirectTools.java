package com.mff.redirects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mff.commerce.catalog.MFFCatalogTools;

import atg.adapter.gsa.GSARepository;
import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.nucleus.GenericService;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;

public class PDPRedirectTools extends GenericService {
	
	
	String canonicalURLRegEx;
	MFFCatalogTools catalogTools;
	GSARepository inactiveProductRedirectsRepo;
	DimensionValueCacheTools cacheTools;
	String pdpURLPrefix;

	public String getPdpURLPrefix() {
		return pdpURLPrefix;
	}

	public void setPdpURLPrefix(String pPdpURLPrefix) {
		pdpURLPrefix = pPdpURLPrefix;
	}

	public DimensionValueCacheTools getCacheTools() {
		return cacheTools;
	}

	public void setCacheTools(DimensionValueCacheTools pCacheTools) {
		cacheTools = pCacheTools;
	}
	
	public GSARepository getInactiveProductRedirectsRepo() {
		return inactiveProductRedirectsRepo;
	}

	public void setInactiveProductRedirectsRepo(GSARepository pInactiveProductRedirectsRepo) {
		inactiveProductRedirectsRepo = pInactiveProductRedirectsRepo;
	}	
	
	public MFFCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(MFFCatalogTools pCatalogTools) {
		catalogTools = pCatalogTools;
	}

	public String getCanonicalURLRegEx() {
		return canonicalURLRegEx;
	}

	public void setCanonicalURLRegEx(String pCanonicalURLRegEx) {
		canonicalURLRegEx = pCanonicalURLRegEx;
	}

	/**
	 * Example: /detail/franklin-2nd-skinz-grey-navy-batting-glove/0000000296937
	 * 
	 * @param pUrl - The assumption is that URL will be the canonical URL of the product
	 * @return product id
	 */
	public String getProductIdFromCanonicalURL (String pUrl) {
		
		logInfo("getProductIdFromCanonicalURL pUrl is " + pUrl);
		
		String productId = null;
		
		if(pUrl.matches(getCanonicalURLRegEx())) {
			
			logInfo("url matches canonical url pattern");
			String [] urlParts = pUrl.split("/");
			
			if(urlParts != null && urlParts.length == 3) {
				productId = urlParts[2];
				logInfo("returning product id " + productId);
			}
		} 

		return productId;
	}
	
	/*
	 * /browse/product.jsp?id=0000000296937
	 * 
	 */
	public String getProductIdFromQueryString (String pUrl) {
		return null;
	}
	
	public String getProductIdFromRequest(DynamoHttpServletRequest pRequest) {
		logInfo("getProductIdFromRequest requestURI " + pRequest.getRequestURI());
		String productId = pRequest.getParameter("id");
		
		if(productId != null) {
			logInfo("Query param id found. Returning prod id from query param " + productId);
			return productId;
		}
		return getProductIdFromCanonicalURL(pRequest.getRequestURI());
	}
	
	public String getConfiguredRedirectURL (RepositoryItem pRedirectItem) throws RepositoryException {
		String redirectURL = null;
		if(pRedirectItem == null) {
			return redirectURL;
		}
		String redirectProductId = (String)pRedirectItem.getPropertyValue("redirectProductId");
		logInfo("configured redirect prod id = " + redirectProductId);
		RepositoryItem redirectProduct = getProduct(redirectProductId);
		String seoDesc = (String)redirectProduct.getPropertyValue("seoDescription");
		logInfo("seoDesc on redirect product = " + seoDesc);
		seoDesc = seoDesc.replaceAll("/", " ");
		logInfo("seoDesc after replaceAll = " + seoDesc);
		redirectURL = getPdpURLPrefix() + seoDesc + "/" + redirectProductId;
		logInfo("Return redirectURL " + redirectURL);
		return redirectURL;
		
	}
	public String getRedirectURL (String productId) {
		String redirectURL = null;
		
		if(productId == null) {
			logError("ProductID is invalid - " + productId);
			return redirectURL;
		}
		RepositoryItem product=null;
		
		try {
			product = getProduct(productId);
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(product == null) {
			logError("Unable to find product");
			return redirectURL;
		}
		
		List<RepositoryItem> childSkus = (List<RepositoryItem>) product.getPropertyValue("childSkus");
		
		if(childSkus == null || childSkus.isEmpty()) {
			logInfo("Product " + productId + " has no skus. It will require redirects");
		}
		
		boolean isActive = getCatalogTools().isProductActive(productId);
		boolean isTeaser = getCatalogTools().isProductTeaserActive(productId);
		
		logInfo("IsActive = " + isActive + " isTeaser = " + isTeaser);
		
		if( (!isActive && !isTeaser) ||
				childSkus == null || childSkus.isEmpty()) {
			logInfo("productId " + productId + " requires a redirect");
			
			try {
				// is a redirect configured in the BCC
				RepositoryItem redirectItem = getConfiguredRedirect(productId);
				
				if(redirectItem != null) {
					logInfo("Redirect configured in BCC with id " + redirectItem.getRepositoryId());
					redirectURL = getConfiguredRedirectURL(redirectItem);
					return redirectURL;
					
				} else {
					logInfo("Redirect not configured. Looking for intelligent redirect");
					redirectURL = getCategoryRedirect(getProduct(productId));
					return redirectURL;
					
				}
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		
		
		return redirectURL;
	}
	
	public String getRedirectURL (DynamoHttpServletRequest pRequest) {
		
		String redirectURL = null;
		String productId = getProductIdFromRequest(pRequest);
		logInfo("getRedirectURL productId is " + productId);

		return getRedirectURL(productId);
	}

	public RepositoryItem getConfiguredRedirect (String pProductId) throws RepositoryException {
		RepositoryView redirectView = getInactiveProductRedirectsRepo().getView("redirect");
		QueryBuilder redirectQueryBuilder = redirectView.getQueryBuilder();

		QueryExpression productIdProperty = redirectQueryBuilder.createPropertyQueryExpression("productId");
		QueryExpression productIdValue = redirectQueryBuilder.createConstantQueryExpression(pProductId);

		QueryExpression isInactiveProperty = redirectQueryBuilder.createPropertyQueryExpression("isInactive");
		QueryExpression isInactiveValue = redirectQueryBuilder.createConstantQueryExpression(Boolean.FALSE);

		
		Query prodIdQuery = redirectQueryBuilder.createComparisonQuery(productIdProperty, productIdValue, QueryBuilder.EQUALS);
		Query activeRedirectQuery = redirectQueryBuilder.createComparisonQuery(isInactiveProperty, isInactiveValue, QueryBuilder.EQUALS);
		
	    Query[] subQueries = { prodIdQuery, activeRedirectQuery };
	    Query activeProdIdRedirectQuery = redirectQueryBuilder.createAndQuery(subQueries);
	    
		RepositoryItem[] redirects = redirectView.executeQuery(activeProdIdRedirectQuery);
		
		if(redirects != null) {
			return redirects[0];
		} else {
			return null;
		}
	}	
	
	public RepositoryItem getProduct(String pProductId) throws RepositoryException {
		RepositoryItem prodItem = getCatalogTools().getCatalogRepository().getItem(pProductId, "product");
		return prodItem;
	}
	
	public RepositoryItem getCategory(String pCategoryId) {
		return null;
	}
	public boolean isCategoryVisible(RepositoryItem pCategory) {
		boolean bVisible = false;
		if(pCategory == null) {
			return bVisible;
		}
		String visibility = (String)pCategory.getPropertyValue("visibility");
		if(visibility == null || visibility.equalsIgnoreCase("All Locations")
				|| visibility.equalsIgnoreCase("Nav Only")) {
			bVisible = true;
		}
		return bVisible;
	}
	public String getCategoryRedirect (RepositoryItem pProduct) {
		
		String categoryLandingURL = null;
		
		if(pProduct == null) {
			return categoryLandingURL;
		}
		DimensionValueCacheObject cachedCatgDimensionValue = null;
		
		// is immediate parent category active
		RepositoryItem immediateParentCategory = (RepositoryItem)pProduct.getPropertyValue("parentCategory");
		
		if(immediateParentCategory != null) {
			logInfo("Immediate parent is defined catId = " + immediateParentCategory.getRepositoryId());
			if(isCategoryVisible(immediateParentCategory)) {
				logInfo("Immediate Parent is visible");
				cachedCatgDimensionValue = getCacheTools().get(immediateParentCategory.getRepositoryId(),null);
				if(cachedCatgDimensionValue != null) {
					categoryLandingURL = cachedCatgDimensionValue.getUrl();
					logInfo("Found cached category url " +categoryLandingURL);
				} else {
					logError("Error getting cached category");
				}
			} else {
				logInfo("Checking ancestorCategories");
				LinkedList<RepositoryItem> ancestors = getCatalogTools().getAncestors(pProduct);
				RepositoryItem ancestorCategory = null;
				
				Iterator<RepositoryItem> itr = ancestors.descendingIterator();
				while (itr.hasNext()) {
					ancestorCategory = itr.next();
					logInfo("Ancestor " + ancestorCategory.getRepositoryId());
					if(ancestorCategory.getRepositoryId().equalsIgnoreCase("00000")) {
						continue;
					}
					if(isCategoryVisible(ancestorCategory)) {
						logInfo("category is visible");
						cachedCatgDimensionValue = getCacheTools().get(ancestorCategory.getRepositoryId(),null);
						if(cachedCatgDimensionValue != null) {
							categoryLandingURL = cachedCatgDimensionValue.getUrl();
							logInfo("Found cached category url " + categoryLandingURL + " Exiting");
							break;
						} else {
							logError("Error getting cached category");
						}
					} else {
						logInfo("Category is NOT visible. Continuing to next ancestor");
					}
				}
			}
		} else {
			logInfo("Category hierarchy not properly set");
		}
		logInfo("Returning catLandingURL " + categoryLandingURL);
		return categoryLandingURL;
	}
}