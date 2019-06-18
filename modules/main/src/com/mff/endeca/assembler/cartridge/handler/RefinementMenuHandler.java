package com.mff.endeca.assembler.cartridge.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.RefinementMenuConfig;
import com.endeca.infront.cartridge.model.Refinement;
import com.mff.commerce.catalog.MFFCatalogTools;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

public class RefinementMenuHandler extends com.endeca.infront.cartridge.RefinementMenuHandler {

	protected static final String DIMENSION_NAME = "dimensionName";
	protected static final String ALL_LOCATIONS = "All Locations";
	protected static final String LEFT_ONLY = "Left Only";
	
	MFFCatalogTools catalogTools;
	String categoryDimensionName;
	String categoryIdPropertyName;
	String catalogsPropertyname;
	boolean filterCategories;
	String visibilityPropertyName;
	
	public String getVisibilityPropertyName() {
		return visibilityPropertyName;
	}

	public void setVisibilityPropertyName(String pVisibilityPropertyName) {
		visibilityPropertyName = pVisibilityPropertyName;
	}

	public boolean isFilterCategories() {
		return filterCategories;
	}

	public void setFilterCategories(boolean pFilterCategories) {
		filterCategories = pFilterCategories;
	}

	public String getCatalogsPropertyname() {
		return catalogsPropertyname;
	}

	public void setCatalogsPropertyname(String pCatalogsPropertyname) {
		catalogsPropertyname = pCatalogsPropertyname;
	}

	public String getCategoryIdPropertyName() {
		return categoryIdPropertyName;
	}

	public void setCategoryIdPropertyName(String pCategoryIdPropertyName) {
		categoryIdPropertyName = pCategoryIdPropertyName;
	}

	public String getCategoryDimensionName() {
		return categoryDimensionName;
	}

	public void setCategoryDimensionName(String pCategoryDimensionName) {
		categoryDimensionName = pCategoryDimensionName;
	}

	public MFFCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(MFFCatalogTools pCatalogTools) {
		catalogTools = pCatalogTools;
	}

	/**
	 * Removing sitestate from response json
	 * 
	 */
	@Override
	public void preprocess(RefinementMenuConfig refinementMenuConfig)
			throws CartridgeHandlerException {

		setSiteState(null);
		super.preprocess(refinementMenuConfig);
	}

	protected RefinementMenu filterHiddenCategoryFacets(RefinementMenu pCategoryRefinementMenu) 
			throws CartridgeHandlerException {
		List<Refinement> currentRefinements = pCategoryRefinementMenu.getRefinements();
		List<Refinement> validRefinements = new ArrayList<Refinement>();
		for (Refinement refinement : currentRefinements) {
			String categoryId = refinement.getProperties().get(getCategoryIdPropertyName());
	        if (categoryId != null) {
	            RepositoryItem categoryItem = null;
	            
	            try {
	              categoryItem = getCatalogTools().findCategory(categoryId, getCatalogsPropertyname());
	              if (categoryItem != null) {
	            	  String visibility = (String)categoryItem.getPropertyValue(getVisibilityPropertyName());
	                  if(visibility == null || visibility.equalsIgnoreCase(ALL_LOCATIONS)
	                		  || visibility.equalsIgnoreCase(LEFT_ONLY)) {
	                	  validRefinements.add(refinement);  
	                  }
	            	  
	              }	              
	            } 
	            catch (RepositoryException e) {
	            	throw new CartridgeHandlerException(e);
	            }
	        }			
		}
		pCategoryRefinementMenu.setRefinements(validRefinements);
		return pCategoryRefinementMenu;
	}
	
	@Override
	public RefinementMenu process(RefinementMenuConfig refinementMenuConfig)  throws CartridgeHandlerException  {
		RefinementMenu refinementMenu = super.process(refinementMenuConfig);

		if(isFilterCategories()) {
			if(refinementMenu != null) {
				String dimensionName = (String) refinementMenu.get(DIMENSION_NAME);
				if(dimensionName != null) {
					if (getCategoryDimensionName().equals(dimensionName)) {
						refinementMenu = filterHiddenCategoryFacets(refinementMenu);
					}
				}
			}
		}
		
		//Removing Nr param
		//.equalsIgnoreCase("dynRank")
		return removeNrParams(refinementMenu);
	}


/**
   * Removes Nr param from Navigation state of refinements
   *
   * @param refinementMenu
   */
	@SuppressWarnings("rawtypes")
	private RefinementMenu removeNrParams(RefinementMenu refinementMenu)  {
		//Removing Nr from ancestors
		if(null!=refinementMenu && null!=refinementMenu.getRefinements()){
			for (com.endeca.infront.cartridge.model.Refinement refinement : refinementMenu.getRefinements()){
				String pNavState = refinement.getNavigationState();
				String[] navStatePieces = pNavState.split("/N-");
				if(navStatePieces!= null && navStatePieces.length ==2 ){
					List sel = getNavigationState().getFilterState().getNavigationFilters();
					if(navStatePieces[1].indexOf("/Ntt")!= -1){
						navStatePieces[1] = navStatePieces[1].substring(0,navStatePieces[1].indexOf("/Ntt"));
					}else if(navStatePieces[1].indexOf("?")!= -1){
						navStatePieces[1] = navStatePieces[1].substring(0,navStatePieces[1].indexOf("?"));
					}
					if(navStatePieces[1].indexOf("+")!= -1){
						List<String> nState = new ArrayList<String>(Arrays.asList(navStatePieces[1].split("\\+")));
						nState.removeAll(sel);
						refinement.getProperties().put("dimVal", nState.get(0));
					}else if (null != navStatePieces[1]){
						refinement.getProperties().put("dimVal",navStatePieces[1]);
					}
				}
				refinement.setNavigationState(removeRecordFilterParams(refinement.getNavigationState()));
			}
/*			if(sortOption != null && sortOption.equalsIgnoreCase("dynRank"))
				refinementMenu.getRefinements().sort((o1, o2) -> o2.getCount().compareTo(o1.getCount()));
			if(sortOption != null && sortOption.equalsIgnoreCase("static"))
				refinementMenu.getRefinements().sort((o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));*/
		}
		//
		/*		Collections.sort(categories, new Comparator<PrimaryNavItem>(){
		  public int compare(PrimaryNavItem p1, PrimaryNavItem p2){
		    return p1.getDisplayName().compareTo(p2.getDisplayName());
		  }
		});*/
	return refinementMenu;
	}


	public static String removeRecordFilterParams(String navigationState) {
		String recordFilterParam = "";
		if(navigationState.indexOf("Nr=") != -1){
			String nstateParams[] = navigationState.substring(navigationState.indexOf("?")).split("&");
			if(nstateParams.length !=0){
				for(String nstateParam:nstateParams){
					if(nstateParam.indexOf("Nr=") != -1){
						recordFilterParam = nstateParam;
						break;
					}
				}
			}
		}
		if(!recordFilterParam.equals("")){
			if(recordFilterParam.indexOf("?") == 0){
				navigationState = navigationState.replace(recordFilterParam, "?");
			}else{
				navigationState = navigationState.replace("&"+recordFilterParam, "");
			}
		}
		navigationState = navigationState.replace("?&", "?");
		if(navigationState.lastIndexOf("?") == navigationState.length()-1){
			navigationState = navigationState.substring(0,navigationState.length()-1);
		}

		return navigationState;
	}


}
