package com.googleadwords.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;


import atg.commerce.catalog.CatalogTools;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * An extension of the ATG SingletonSchedulableService. This is used
 * as the Schedulable class for all batch jobs. It defines two extension properties:
 * 
 * <p><code>tasks</code>: An array of tasks that will be executed by the scheduled job.
 * <p><code>enable</code>: Setting to control if the job should execute. 
 * @author KnowledgePath Inc.
 */
public class TestCategory extends GenericService {

	private String categoryId;
	private CatalogTools catalogTools;
	private String categoryPath;
	
	public String getCategoryPath() {
		return categoryPath;
	}
	public void setCategoryPath(String pCategoryPath) {
		categoryPath = pCategoryPath;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String pCategoryId) {
		categoryId = pCategoryId;
	}
	public CatalogTools getCatalogTools() {
		return catalogTools;
	}
	public void setCatalogTools(CatalogTools pCatalogTools) {
		catalogTools = pCatalogTools;
	}
	
	
	public void calculatePath() throws RepositoryException{
		RepositoryItem category=getCatalogTools().findCategory(getCategoryId());
		String displayName=(String)category.getPropertyValue("displayName");
		Set<RepositoryItem> parentCategories=(Set<RepositoryItem>)category.getPropertyValue("fixedParentCategories");
		ArrayList<String> displayNames=new ArrayList<String>();
		displayNames.add(displayName);
		displayNames=calculateCategoryPath(category,displayNames);
		String str="";
		for(int i=displayNames.size()-1;i>=0;i--){
			if(i==0){
				str=str+displayNames.get(i);
			}
			else {
				str=str+displayNames.get(i)+" > ";
			}
		}
		setCategoryPath(str);
	}
	
	
	
	
	private ArrayList<String> calculateCategoryPath(RepositoryItem category,ArrayList<String> names){
		Set<RepositoryItem> parentCategories=(Set<RepositoryItem>)category.getPropertyValue("fixedParentCategories");
		if(parentCategories ==null || parentCategories.size()==0){
			names=names;
		}
		else{
			for(RepositoryItem item:parentCategories){
				if(!isCategoryActive(item)){
					continue;
				}
				else {
					String displayName=(String)item.getPropertyValue("displayName");
					if(displayName.toLowerCase().contains("root category")){
						displayName="Home";
					}
					names.add(displayName);
					names=calculateCategoryPath(item,names);
					break;
				}
			}
		}
		return names;
	}
	
	 public boolean isCategoryActive(RepositoryItem lProductItem){
	      if(lProductItem!=null){
	        Date endDate = (Date) lProductItem.getPropertyValue("endDate");
	        Date startDate = (Date) lProductItem.getPropertyValue("startDate");
	        Date curDate = Calendar.getInstance().getTime();
	        if (startDate == null) {
	          return false;
	        }
	        else if ( endDate == null) {
	          // Return true if the current date is after start date
	          return (curDate.compareTo(startDate) >= 0);
	        }
	        else {
	          // Return true if current date is between start and end dates, inclusively
	          return ((curDate.compareTo(startDate) >= 0) && (curDate.compareTo(endDate) <= 0));
	        }
	      }
	      return false;
	    }
	
}

