package com.mff.commerce.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.google.gson.GsonBuilder;

public class PrimaryNavDroplet extends DynamoServlet {

	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_CATEGORY_ID = ParameterName
			.getParameterName("categoryId");
	public static final ParameterName PARAM_CATEGORY = ParameterName
			.getParameterName("category");

	/* OUTPUT PARAMETERS */
	public static final String O_JSON_NAV_NAME = "primaryNavJson";
	public static final String O_DEPARTMENTS_NAV = "departments";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName
			.getParameterName("output");
	public static final ParameterName OPARAM_ERROR = ParameterName
			.getParameterName("error");
	public static final ParameterName OPARAM_EMPTY = ParameterName
			.getParameterName("empty");

	/* MEMBERS */
	private CatalogTools catalogTools;
	private DimensionValueCacheTools cacheTools;
	private String rootCategoryId;

	@SuppressWarnings("unchecked")
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		RepositoryItem category = (RepositoryItem) pRequest
				.getObjectParameter(PARAM_CATEGORY);

		if (category == null) {
			String categoryId = getRootCategoryId();
			if (isLoggingDebug()) {
				vlogDebug("PrimaryNavDroplet for Root Category [{0}]", categoryId);
			}

			if (categoryId == null || categoryId.isEmpty()) {
				vlogWarning("Missing required rootCategoryId in properties file");
				pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest,
						pResponse);
				return;
			}

			// Look up the category
			try {
				category = getCatalogTools().findCategory(categoryId);
				if (isLoggingDebug()) {
					vlogDebug("Found category [{0}].", category);
				}
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError("Exception trying to find category [" + categoryId
							+ "]");
				}
			}
			// no category returned.
			if (category == null) {
				if (isLoggingError()) {
					logError("category not found for id:" + categoryId);
				}
				pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest,
						pResponse);
				return;
			}
		}

		List<RepositoryItem> topLevelCategories = (List<RepositoryItem>) category
				.getPropertyValue("displayOrderedCategories");

		ArrayList<PrimaryNavItem> departments = new ArrayList<PrimaryNavItem>();

		for (RepositoryItem topCat : topLevelCategories) {

			// Make sure it's in the dimval cache
			String visibility = (String)topCat.getPropertyValue("visibility");
			if(visibility == null || visibility.equalsIgnoreCase("All Locations")
					|| visibility.equalsIgnoreCase("Nav Only")) {

				ArrayList<String> rc = new ArrayList<String>(
						Arrays.asList(getRootCategoryId()));

				DimensionValueCacheObject cached = cacheTools.get(
						topCat.getRepositoryId(), rc);
				if (cached != null) {

					// Set Display Name, Type, repository ID, and URL

					PrimaryNavItem dept = new PrimaryNavItem();
					dept.setRepositoryId(topCat.getRepositoryId());
					dept.setNavType("department");
					dept.setDisplayName(topCat.getItemDisplayName());
					dept.setUrl(cached.getUrl());

					// Add Categories Here
					List<RepositoryItem> deptCategories = (List<RepositoryItem>) topCat
							.getPropertyValue("displayOrderedCategories");

					if (deptCategories != null) {
						dept.setSubcategories(getCategories(deptCategories));
					}

					departments.add(dept);
				}
			}
		}

		String json = new GsonBuilder().disableHtmlEscaping().create()
				.toJson(departments);
		pRequest.setParameter(O_JSON_NAV_NAME, json);
		pRequest.setParameter(O_DEPARTMENTS_NAV, departments);
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<PrimaryNavItem> getCategories(
			List<RepositoryItem> p_categories) {

		ArrayList<PrimaryNavItem> categories = new ArrayList<PrimaryNavItem>();

		
		for (RepositoryItem category : p_categories) {

			if (category != null) {

				String visibility = (String)category.getPropertyValue("visibility");
				if(visibility == null || visibility.equalsIgnoreCase("All Locations")
						|| visibility.equalsIgnoreCase("Nav Only")) {


					ArrayList<String> rc = new ArrayList<String>(
							Arrays.asList(getRootCategoryId()));

					DimensionValueCacheObject cached = cacheTools.get(
							category.getRepositoryId(), rc);
					if (cached != null) {

						PrimaryNavItem cat = new PrimaryNavItem();
						cat.setRepositoryId(category.getRepositoryId());
						cat.setNavType("category");
						cat.setDisplayName(category.getItemDisplayName());
						cat.setUrl(cached.getUrl());

						// Add Sub Categories Here

						List<RepositoryItem> categoryChildren = (List<RepositoryItem>) category
								.getPropertyValue("displayOrderedCategories");

						if (categoryChildren != null) {
							cat.setSubcategories(getSubCategories(categoryChildren));
						}

						categories.add(cat);
					}
				}
			}

		}
		// Sorting categories alphabetically by displayName
/*		Collections.sort(categories, new Comparator<PrimaryNavItem>(){
			  public int compare(PrimaryNavItem p1, PrimaryNavItem p2){
			    return p1.getDisplayName().compareTo(p2.getDisplayName());
			  }
			});*/
		return categories;

	}

	private ArrayList<PrimaryNavItem> getSubCategories(
			List<RepositoryItem> p_subCategories) {

		ArrayList<PrimaryNavItem> subCategories = new ArrayList<PrimaryNavItem>();


		for (RepositoryItem subCategory : p_subCategories) {

			if (subCategory != null) {

				String visibility = (String)subCategory.getPropertyValue("visibility");
				if(visibility == null || visibility.equalsIgnoreCase("All Locations")
						|| visibility.equalsIgnoreCase("Nav Only")) {


					ArrayList<String> rc = new ArrayList<String>(
							Arrays.asList(getRootCategoryId()));

					DimensionValueCacheObject cached = cacheTools.get(
							subCategory.getRepositoryId(), rc);
					if (cached != null) {

						PrimaryNavItem scat = new PrimaryNavItem();
						scat.setRepositoryId(subCategory.getRepositoryId());
						scat.setNavType("subcategory");
						scat.setDisplayName(subCategory.getItemDisplayName());
						scat.setUrl(cached.getUrl());
						subCategories.add(scat);
					}
				}
			}
		}
		
		// Sorting subCategories alphabetically by displayName
/*		Collections.sort(subCategories, new Comparator<PrimaryNavItem>(){
			  public int compare(PrimaryNavItem p1, PrimaryNavItem p2){
			    return p1.getDisplayName().compareTo(p2.getDisplayName());
			  }
			});*/
		return subCategories;

	}

	public CatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(CatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public DimensionValueCacheTools getCacheTools() {
		return cacheTools;
	}

	public void setCacheTools(DimensionValueCacheTools cacheTools) {
		this.cacheTools = cacheTools;
	}

	/**
	 * @return the rootCategoryId
	 */
	public String getRootCategoryId() {
		return rootCategoryId;
	}

	/**
	 * @param rootCategoryId
	 *            the rootCategoryId to set
	 */
	public void setRootCategoryId(String rootCategoryId) {
		this.rootCategoryId = rootCategoryId;
	}

}
