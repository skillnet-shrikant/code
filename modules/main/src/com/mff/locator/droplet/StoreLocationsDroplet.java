package com.mff.locator.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import com.mff.commerce.locations.MFFStoreLocatorFormHandler.BopisStore;
import com.mff.locator.StoreLocatorTools;
import com.mff.zip.MFFZipcodeHelper;

import atg.commerce.locations.Coordinate;
import atg.commerce.locations.GeoLocatorDroplet;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import mff.MFFEnvironment;

public class StoreLocationsDroplet extends GeoLocatorDroplet {
	
	private StoreLocatorTools mStoreLocatorTools;
	private MFFZipcodeHelper mZipCodeHelper;

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		String state = pRequest.getParameter("state");
		String locationID = pRequest.getParameter("id");
		String zipcode = pRequest.getParameter("zipcode");
		String ignoreLocationId = pRequest.getParameter("ignoreLocationId");
		Object homeStore = pRequest.getObjectParameter("homeStore");
		
		if (isLoggingDebug()){
			logDebug("StoreLocationsDroplet called: state: " + state);
			logDebug("StoreLocationsDroplet called: homeStore: " + homeStore);
		}

		List<RepositoryItem> storeLocations = null;

		if (!StringUtils.isEmpty(state)) {
			storeLocations = getStoreLocatorTools().getStoreByState(state);
		} else if (!StringUtils.isEmpty(zipcode)) {
			  storeLocations = findStoresNearestToZipcode(zipcode,pRequest,pResponse);
		} else if (!StringUtils.isEmpty(locationID)) {
			RepositoryItem storeItem = getStoreLocatorTools().getStoreByLocationId(locationID);
			if (storeItem != null) {
				storeLocations = new ArrayList<RepositoryItem>();
				storeLocations.add(storeItem);
			}
			
		} else {
			storeLocations = getStoreLocatorTools().findAllStores();
		}
		if(StringUtils.isNotEmpty(ignoreLocationId) && storeLocations != null) {
			for(RepositoryItem storeRepoItem : storeLocations) {
				if(storeRepoItem.getRepositoryId().equals(ignoreLocationId)) {
					storeLocations.remove(storeRepoItem);
					break;
				}
			}
		}
		if (storeLocations != null && storeLocations.size() > 0) {
			
			if (homeStore != null) {
				while (storeLocations.indexOf(homeStore) > 0) {
					
					int i = storeLocations.indexOf(homeStore);
					Collections.swap(storeLocations, i, i - 1);
				}
			}
			
			if (isLoggingDebug()){
				logDebug("StoreLocationsDroplet called: storeLocations: " + storeLocations);
			}
			pRequest.setParameter("storeLocations", storeLocations);
			pRequest.serviceLocalParameter("output", pRequest, pResponse);
		} else {
			pRequest.serviceLocalParameter("empty", pRequest, pResponse);
		}
	}
	
	private List<RepositoryItem> findStoresNearestToZipcode(String pZipCode,DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) {
	  RepositoryItem lZipCodeRepoItem = getZipCodeHelper().retrieveZipcodeItembyZipcode(pZipCode);
    if(null != lZipCodeRepoItem) {
       Coordinate lZipCoordinate =  new Coordinate((Double)lZipCodeRepoItem.getPropertyValue("latitude"), (Double)lZipCodeRepoItem.getPropertyValue("longitude"));
       vlogDebug("Using a Cordinates of {0} ZipCode {1}.", new Object[] { pZipCode, lZipCodeRepoItem });
       double distance = getDistance();
       String distanceParam = pRequest.getParameter("distance");
       if (distanceParam != null) {
         distance = Double.parseDouble(distanceParam);
  
  
         if (getUnitOfMeasureTools() != null) {
           distance = getUnitOfMeasureTools().convertValue(distance, pRequest.getParameter("unitOfMeasure"));
         }
  
       }
      
       if (((distance > getMaximumDistance()) && (getMaximumDistance() >= 0.0D)) || ((distance <= -1.0D) && (getMaximumDistance() >= 0.0D))) {
         distance = getMaximumDistance();
       }
       vlogDebug("Using a distance of {0} ", new Object[] { Double.valueOf(distance) });
       String siteScope = pRequest.getParameter("siteScope");
       Object siteIdsParam = pRequest.getObjectParameter("siteIds");
       Collection siteIds = null;
       if (siteIdsParam instanceof Collection) {
         siteIds = (Collection)siteIdsParam;
       } else {
         if ((siteScope == null) || (siteScope.isEmpty())) {
           siteScope = getSiteScope();
         }
         siteIds = generateSiteFilter(siteScope);
       }
       if (isLoggingDebug()) {
         logDebug("searchByUserId: site IDs to get = " + siteIds);
       }
       try {
         RepositoryItem[] lStores = getItems(pRequest, lZipCoordinate, distance, siteIds);
         if(null!=lStores) {
           return new ArrayList<RepositoryItem>(Arrays.asList(lStores));
         }
       } catch (RepositoryException e) {
        vlogError("Error finding nearest store {0}", e.getMessage());
       }
    }
	  return null;
  }

  public StoreLocatorTools getStoreLocatorTools() {
		return mStoreLocatorTools;
	}

	public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
		this.mStoreLocatorTools = pStoreLocatorTools;
	}

  public MFFZipcodeHelper getZipCodeHelper() {
    return mZipCodeHelper;
  }

  public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
    mZipCodeHelper = pZipCodeHelper;
  }

}