package com.mff.commerce.catalog;

import java.util.ArrayList;

public class PrimaryNavItem {

	private String repositoryId;
	private String navType;
	private String displayName;
	private String url;
	private ArrayList<PrimaryNavItem> subcategories;


	/**
	 * @return the navType
	 */
	public String getNavType() {
		return navType;
	}
	/**
	 * @param navType the navType to set
	 */
	public void setNavType(String navType) {
		this.navType = navType;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the subcategories
	 */
	public ArrayList<PrimaryNavItem> getSubcategories() {
		return subcategories;
	}
	/**
	 * @param subcategories the subcategories to set
	 */
	public void setSubcategories(ArrayList<PrimaryNavItem> subcategories) {
		this.subcategories = subcategories;
	}
	/**
	 * @return the repositoryId
	 */
	public String getRepositoryId() {
		return repositoryId;
	}
	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}



}
