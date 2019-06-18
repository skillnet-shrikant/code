package com.mff.endeca.assembler.cartridge.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.Breadcrumbs;
import com.endeca.infront.cartridge.BreadcrumbsConfig;
import com.endeca.infront.cartridge.BreadcrumbsHandler;

public class BreadCrumbsHandler extends BreadcrumbsHandler{

	/**
	 * Removing sitestate from response json
	 */
	@Override
	public void preprocess(BreadcrumbsConfig breadcrumbsConfig)
			throws CartridgeHandlerException {

		setSiteState(null);
		super.preprocess(breadcrumbsConfig);
	}

	@Override
	public Breadcrumbs process(BreadcrumbsConfig breadcrumbsConfig)
			throws CartridgeHandlerException {
		// Removing Nr param
		return removeNrParams(super.process(breadcrumbsConfig));
	}

/**
   * Removes Nr param from Navigation state of ancestors
   *
   * @param breadcrumbs
   */
	private Breadcrumbs removeNrParams(Breadcrumbs breadcrumbs)  {
		//Removing Nr from ancestors
		if(null!=breadcrumbs && null != breadcrumbs.getRefinementCrumbs()){
			for (com.endeca.infront.cartridge.model.RefinementBreadcrumb refinementBreadcrumb : breadcrumbs.getRefinementCrumbs()){
				for (com.endeca.infront.cartridge.model.Ancestor ancestor : refinementBreadcrumb.getAncestors()){
					ancestor.setNavigationState(removeRecordFilterParams(ancestor.getNavigationState()));
				}
				List<String>navStatePieces = new ArrayList<String>(getNavigationState().getFilterState().getNavigationFilters());
				String removeNState = refinementBreadcrumb.getRemoveAction().getNavigationState();
				if(navStatePieces!= null && navStatePieces.size() !=0 ){
					if(removeNState.indexOf("N-") != -1){
						removeNState = removeNState.split("/N-")[1];
						if(removeNState.indexOf("/Ntt")!= -1){
							removeNState= removeNState.substring(0,removeNState.indexOf("/Ntt"));
						}else if(removeNState.indexOf("?")!= -1){
							removeNState = removeNState.substring(0,removeNState.indexOf("?"));
						}
						if(removeNState.indexOf("+")!= -1){
							List<String> nState = new ArrayList<String>(Arrays.asList(removeNState.split("\\+")));
							navStatePieces.removeAll(nState);
							if(navStatePieces!=null && navStatePieces.size()!=0){
								refinementBreadcrumb.getProperties().put("dimVal", navStatePieces.get(0));
							}
						}else if(null != removeNState){
							navStatePieces.removeAll(new ArrayList<String>(Arrays.asList(removeNState)));
							if(navStatePieces!=null && navStatePieces.size()!=0){
								refinementBreadcrumb.getProperties().put("dimVal", navStatePieces.get(0));
							}
						}
					}else{
						refinementBreadcrumb.getProperties().put("dimVal", navStatePieces.get(0));
					}
				}
				refinementBreadcrumb.getRemoveAction().setNavigationState(removeRecordFilterParams(refinementBreadcrumb.getRemoveAction().getNavigationState()));
			}
		}

	return breadcrumbs;
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
