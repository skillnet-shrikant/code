package com.mff.sitemap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.sitemap.DynamicSitemapGenerator;
import atg.sitemap.SitemapGeneratorService;
import atg.sitemap.SitemapTools;

public class MFFCategorySiteMapGenerator extends DynamicSitemapGenerator {

	private static final String END_DATE = "endDate";
    private static final String START_DATE = "startDate";
    private static final String FIXED_CHILD_PRODUCTS = "fixedChildProducts";
    private static final String FIXED_CHILD_CATEGORIES = "fixedChildCategories";
    private DimensionValueCacheTools mDimensionValueCacheTools;
	private String mRootCategoryId;

	public DimensionValueCacheTools getDimensionValueCacheTools() {
		return mDimensionValueCacheTools;
	}

	public void setDimensionValueCacheTools(
			DimensionValueCacheTools pDimensionValueCacheTools) {
		mDimensionValueCacheTools = pDimensionValueCacheTools;
	}

	public String getRootCategoryId() {
		return mRootCategoryId;
	}

	public void setRootCategoryId(String pRootCategoryId) {
		mRootCategoryId = pRootCategoryId;
	}

	@SuppressWarnings("unchecked")
	public void generateSitemapUrls(String pItemDescriptorName,
			SitemapGeneratorService pSitemapGeneratorService, String pSiteId) {
	  vlogDebug("Generating Category Sitemap urls - Started");
		try {
			SitemapTools sitemapTools = pSitemapGeneratorService
					.getSitemapTools();
			StringBuilder sb = new StringBuilder();
			sitemapTools.appendSitemapHeader(sb);
			RepositoryItem root = getSourceRepository().getItem(
					getRootCategoryId(), pItemDescriptorName);
			List<RepositoryItem> departments = (List<RepositoryItem>) root
					.getPropertyValue(FIXED_CHILD_CATEGORIES);
			
			vlogDebug("Number of fixedChildCategories of root category:{0} is: {1}",root.getRepositoryId(),departments.size());
			
			List<String> ancestors = new ArrayList<String>();
			ancestors.add(getRootCategoryId());
			for (RepositoryItem department : departments) { // departments
			  vlogDebug("currently working on child category:{0}",department.getRepositoryId());
				DimensionValueCacheObject cacheEntryDept = getDimensionValueCacheTools()
						.get(department.getRepositoryId(), ancestors);
				if (null != cacheEntryDept) {
				  vlogDebug("Found Dimension cache object for sub category:{0} and calling getChildCategoryUrls()",department.getRepositoryId());
					getChildCategoryUrls(pSitemapGeneratorService,
							sitemapTools, sb, ancestors, department,
							cacheEntryDept);
				}
			}
			sitemapTools.appendSitemapFooter(sb);
			sitemapTools.writeSitemap(sb, getSitemapFilePrefix(), 1);
		} catch (RepositoryException e) {
			vlogError(
					e,
					"RepositoryException while accessing category items -- {0}",
					e.getMessage());
		}
		vlogDebug("Generating Category Sitemap urls - Ended");
	}

	private void getChildCategoryUrls(
			SitemapGeneratorService pSitemapGeneratorService,
			SitemapTools sitemapTools, StringBuilder sb,
			List<String> ancestors, RepositoryItem category,
			DimensionValueCacheObject cacheEntryCat) {
	  vlogDebug("getChildCategoryUrls Category - {0}",category.getRepositoryId());
	  boolean isAddToSiteMap = false;
	  if(isItemActive(category)) {
	    vlogDebug("Is active Category - {0} - true",category.getRepositoryId());
	    List<RepositoryItem> subcategories = (List<RepositoryItem>) category.getPropertyValue(FIXED_CHILD_CATEGORIES);
  		if (null != subcategories && !subcategories.isEmpty()) { // sub-categories
  		  isAddToSiteMap=true;
  		  vlogDebug("has child categoies Category - {0} - true",category.getRepositoryId());
  			ancestors.add(category.getRepositoryId());
  			for (RepositoryItem subcategory : subcategories) {
  				DimensionValueCacheObject cacheEntrySubCat = getDimensionValueCacheTools()
  						.get(subcategory.getRepositoryId(), ancestors);
  				// recursive call to generate urls for all childcategories
  				if (null != cacheEntrySubCat) {
  					getChildCategoryUrls(pSitemapGeneratorService,
  							sitemapTools, sb, ancestors, subcategory,
  							cacheEntrySubCat);
  				}
  			}
  		}else {
  		  isAddToSiteMap = hasActiveProducts(category);
  		  vlogDebug("has active child products Category - {0} - "+isAddToSiteMap,category.getRepositoryId());
  		}
		}else {
		  vlogDebug("Is active Category - {0} - false",category.getRepositoryId());
		}
		
		if(isAddToSiteMap) {
		  vlogDebug("adding to sitemap Category - {0} - "+isAddToSiteMap,category.getRepositoryId());
		  String url = SitemapTools.addPrefixToUrl(getUrlPrefix(),
	        cacheEntryCat.getUrl());
	    sb.append(sitemapTools.generateSitemapUrlXml(
	        SitemapTools.escapeURL(url), getChangeFrequency(),
	        getPriority().toString(),
	        pSitemapGeneratorService.isDebugMode()));
		}else {
		  vlogDebug("skipping add to sitemap Category - {0} - "+isAddToSiteMap,category.getRepositoryId());
		}
		
	}

  private boolean hasActiveProducts(RepositoryItem pCategory) {
    List<RepositoryItem> lChildProducts = (List<RepositoryItem>) pCategory.getPropertyValue(FIXED_CHILD_PRODUCTS);
    if(null != lChildProducts && !lChildProducts.isEmpty() ) {
      for(RepositoryItem lProductItem : lChildProducts) {
        if(isItemActive(lProductItem)) {
          return true;
        }
      }
    }
    
    return false;
  }

  private boolean isItemActive(RepositoryItem pRepoItem) {
    Date lToday = new Date();
    if( (pRepoItem.getPropertyValue(START_DATE) == null || ((Date)pRepoItem.getPropertyValue(START_DATE)).before(lToday)) && 
        (pRepoItem.getPropertyValue(END_DATE) == null || ((Date)pRepoItem.getPropertyValue(END_DATE)).after(lToday))
        ){
      return true;
    }
    return false;
  }
}
