package com.mff.locator;

import java.util.Arrays;
import java.util.List;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class StoreLocatorTools extends GenericService {
	
	private String ITEMDESC_STORE_LOCATOR = "location";
	private String PTY_IS_BOPIS_ONLY = "isBOPISOnly";
	private Repository mStoreLocatorRepository;
	
	private String mQueryStoreByState;
	private String mQueryAllStores;
	
	public boolean isBOPISOnlyStore (String pStoreId) {
		if(pStoreId == null || pStoreId.trim().equalsIgnoreCase("")) {
			return false;
		}
		RepositoryItem storeItem = getStoreByLocationId(pStoreId);
		if(storeItem != null) {
			return (Boolean)storeItem.getPropertyValue(PTY_IS_BOPIS_ONLY);
		}
		return false;
	}

	public List<RepositoryItem> getStoreByState(String pState) {
		
		if (isLoggingDebug()){
			logDebug("getStoreByState(): Invoked.");
		}

		List<RepositoryItem> storeLocations = null;

		try {
			RepositoryView lStoreLocatorView = getStoreLocatorRepository().getView(ITEMDESC_STORE_LOCATOR);
			RqlStatement statement = RqlStatement.parseRqlStatement(getQueryStoreByState());
			
			Object params[] = new Object[1];
			params[0] = pState;
			
			RepositoryItem[] storeLocatorItems = statement.executeQuery(lStoreLocatorView, params);
			
			if (isLoggingDebug()){
				logDebug("getStoreByState(): storeLocatorItems: " + storeLocatorItems.length);
			}

			if (storeLocatorItems != null && storeLocatorItems.length > 0) {
				
				if (isLoggingDebug()) {
					for (int i=0; i<storeLocatorItems.length;i++){
						logDebug("getStoreByState(): storeLocatorItem: "+ i + " : " + storeLocatorItems[i]);
					}
				}

				storeLocations = Arrays.asList(storeLocatorItems);

			}
		} catch (RepositoryException re) {
			if (isLoggingError()){
				logError("Exception while getting stores: " + re, re);
			}
		}

		return storeLocations;
	}
	
	public RepositoryItem getStoreByLocationId (String pLocationId) {
		
		if (isLoggingDebug()){
			logDebug("getStoreByLocationId(): Invoked with pLocationId: " + pLocationId);
		}
		RepositoryItem storeItem = null;
		try {
			
			storeItem = getStoreLocatorRepository().getItem(pLocationId, ITEMDESC_STORE_LOCATOR);
			
			if (isLoggingDebug()) {
				logDebug("getStoreByLocationId(): storeItem: " + storeItem);
			}
			
		} catch (RepositoryException re) {
			if (isLoggingError()){
				logError("Exception while getting store by location id: " + re, re);
			}
		}

		return storeItem;
	}
	
	public List<RepositoryItem> findAllStores () {
		List<RepositoryItem> result = null;
		RepositoryView view;
		try {
			view = getStoreLocatorRepository().getView(ITEMDESC_STORE_LOCATOR);
			RqlStatement statement = RqlStatement.parseRqlStatement(getQueryAllStores());
			Object params[] = new Object[0];
			RepositoryItem [] stores = statement.executeQuery (view, params);
			if (stores != null && stores.length > 0) {
				result = Arrays.asList(stores);
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Exception while getting all stores: " + e, e);
			}
		}
		return result;
	}

	public Repository getStoreLocatorRepository() {
		return mStoreLocatorRepository;
	}

	public void setStoreLocatorRepository(Repository pStoreLocatorRepository) {
		this.mStoreLocatorRepository = pStoreLocatorRepository;
	}

	public String getQueryStoreByState() {
		return mQueryStoreByState;
	}

	public void setQueryStoreByState(String pQueryStoreByState) {
		this.mQueryStoreByState = pQueryStoreByState;
	}

	public String getQueryAllStores() {
		return mQueryAllStores;
	}

	public void setQueryAllStores(String pQueryAllStores) {
		this.mQueryAllStores = pQueryAllStores;
	}
}