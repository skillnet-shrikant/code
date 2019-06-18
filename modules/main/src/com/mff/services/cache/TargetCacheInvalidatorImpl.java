package com.mff.services.cache;

import atg.adapter.gsa.GSAItem;
import atg.adapter.secure.GenericSecuredRepository;
import atg.nucleus.GenericRMIService;
import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.GSAItemDescriptor;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.service.asset.AssetResolver;
import atg.service.asset.AssetUtils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TargetCacheInvalidatorImpl extends GenericRMIService implements TargetCacheInvalidator {

	public static final long serialVersionUID = 0x0000111L;
	
	private GSARepository productCatalog = null;
	private GSARepository priceLists = null;

	public GSARepository getProductCatalog() {
		return productCatalog;
	}

	public void setProductCatalog(GSARepository productCatalog) {
		this.productCatalog = productCatalog;
	}

	public GSARepository getPriceLists() {
		return priceLists;
	}

	public void setPriceLists(GSARepository priceLists) {
		this.priceLists = priceLists;
	}

	public TargetCacheInvalidatorImpl() throws RemoteException
	{
	}

	public void invalidateAllItems( String descriptorUri) throws RemoteException {
		if ( isLoggingDebug()) {
			logDebug("Invalidating all of "+descriptorUri);
		}

		// Parse out the descriptor name
		String repositoryItemURI = descriptorUri.substring( "atgrep:/".length(), descriptorUri.length());
		String parts[] = repositoryItemURI.split("/");
		String repName = parts[0];
		String descName = parts[1];
		try {
			if ( isLoggingDebug()) {
				logDebug("Attempting to invalidate all items of type "+descName+" in repository "+repName);
			}
			GSAItemDescriptor desc = getItemDescriptor( repName, descName);
			if ( desc != null) {
				desc.invalidateCaches();
			}
		}
		catch ( RepositoryException e) {
			if ( isLoggingError()) {
				logError("Problem invalidating caches for URI "+descriptorUri, e);
			}
		}
	}
	
	public void invalidateItems( List<String> uris) throws RemoteException {
		logDebug("InvalidateItems called with uris " + uris.size() + " in TCIImpl");
		if ( isLoggingDebug()) {
			logDebug("Invalidating "+uris.size()+" items");
		}

		// Parse out the descriptor and repository name
		for ( String uri : uris) {
			String repositoryItemURI = uri.substring( "atgrep:/".length(), uri.length());
			String parts[] = repositoryItemURI.split("/");
			String repName = parts[0];
			String descName = parts[1];
			String id = parts[2];
			try {
				GSAItemDescriptor desc = getItemDescriptor( repName, descName);
				if ( desc != null) {
					if (isLoggingDebug()) {
						logDebug("Invalidating item of type " + desc + " with ID " + id + " from repository " + repName);
					}
					desc.invalidateCachedItem(id);
					desc.removeItemFromCache(id);
				}
			}
			catch ( RepositoryException e) {
				if ( isLoggingError()) {
					logError("Problem invalidating caches for URI "+uri, e);
				}
			}
		}
	}

	private AssetResolver mAssetResolver = null;
	private AssetResolver getAssetResolver() {
		if ( mAssetResolver == null) {
			mAssetResolver = AssetUtils.getAssetResolver();
		}
		return mAssetResolver;
	}

	private GSAItemDescriptor getItemDescriptor( String pRepositoryName, String pDescriptorName) throws RepositoryException {
		AssetResolver ar = getAssetResolver();
		GSAItemDescriptor desc = null;
		if ( ar == null) {
			if ( isLoggingError()) {
				logError("Could not find AssetResolver");
			}
		}
		else {
			// Get the repository and figure out what it is
			Object rep = ar.getAssetSourceComponent(pRepositoryName);
			if (rep == null) {
				if (isLoggingError()) {
					logError("Could not resolve repository named " + pRepositoryName);
				}
			} else {
				if (rep instanceof GSARepository) {
					GSARepository gsa = (GSARepository) rep;
					desc = (GSAItemDescriptor) gsa.getItemDescriptor(pDescriptorName);
				}
				else if (rep instanceof GenericSecuredRepository) {
					GenericSecuredRepository gsr = (GenericSecuredRepository) rep;
					GSARepository gsa = (GSARepository)gsr.getWrappedRepository();
					desc = (GSAItemDescriptor) gsa.getItemDescriptor(pDescriptorName);
				}
				else {
					if ( isLoggingError()) {
						logError("Unable to invalidate caches of repository of type "+rep.getClass().getName());
					}
				}
			}
		}
		return desc;
	}

	public void testInvalidator() {
		try {
			invalidateAllItems("atgrep:/ProductCatalog/category");

			ArrayList<String> test = new ArrayList<String>();
			test.add("atgrep:/PriceLists/price/12345");
			test.add("atgrep:/SiteRepository/siteConfiguration/12345");
			test.add("atgrep:/ProductCatalog/product/12345");
			invalidateItems(test);
		}
		catch ( Exception e) {
			if ( isLoggingError()) {
				logError(e);
			}
		}
	}

}
