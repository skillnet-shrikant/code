package com.mff.repository.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import atg.adapter.version.CurrentVersionItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
public class DisplayOrderedCategories extends RepositoryPropertyDescriptor {

	public DisplayOrderedCategories() {
		setPropertyType(Object.class);
	}

	public DisplayOrderedCategories(String pPropertyName)
	{
		super(pPropertyName);
		setPropertyType(Object.class);
	}
	public DisplayOrderedCategories(String pPropertyName, Class pPropertyType, String pShortDescription)
	{
		super(pPropertyName, pPropertyType, pShortDescription);
	    setPropertyType(Object.class);
	}

	@Override
	public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
		//reorderedChildCategories
		//displayOrderedCategories
		ArrayList displayOrderedChildCatgs = new ArrayList();
		ArrayList childCatgsToSort = new ArrayList();
		List reorderedChildCatgs = (List)pItem.getPropertyValue("reorderedChildCategories");
		List fixedChildCatgs = (List)pItem.getPropertyValue("fixedChildCategories");
		
		displayOrderedChildCatgs.addAll(reorderedChildCatgs);
		for(Object fixedChildCatg:fixedChildCatgs) {
			if(!displayOrderedChildCatgs.contains(fixedChildCatg)) {
				childCatgsToSort.add(fixedChildCatg);
			}
		}
		try {Collections.sort(childCatgsToSort, new Comparator(){
			  public int compare(Object p1, Object p2){
				  if(p1 instanceof CurrentVersionItem)
					  return ((String)((CurrentVersionItem)p1).getPropertyValue("displayName")).compareTo((String)((CurrentVersionItem) p2).getPropertyValue("displayName"));
				  else 
					  return ((String)((RepositoryItemImpl)p1).getPropertyValue("displayName")).compareTo((String)((RepositoryItemImpl) p2).getPropertyValue("displayName"));
			  }
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		displayOrderedChildCatgs.addAll(childCatgsToSort);
		return displayOrderedChildCatgs;
	}
}
