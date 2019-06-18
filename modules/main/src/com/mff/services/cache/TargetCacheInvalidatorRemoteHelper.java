package com.mff.services.cache;

import atg.nucleus.GenericService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class TargetCacheInvalidatorRemoteHelper extends GenericService {

    private String mRemoteInvalidatorRmiUrl;

    public String getRemoteInvalidatorRmiUrl() {
        return mRemoteInvalidatorRmiUrl;
    }

    public void setRemoteInvalidatorRmiUrl(String pRemoteInvalidatorRmiUrl) {
        mRemoteInvalidatorRmiUrl = pRemoteInvalidatorRmiUrl;
    }

    public TargetCacheInvalidator getRemoteInvalidator( String pUri) throws MalformedURLException, RemoteException, NotBoundException {
        // Get the URI of the invalidator service
        TargetCacheInvalidator service = null;
        String xportURI = pUri;
        service = (TargetCacheInvalidator) Naming.lookup(xportURI);
        // And look it up
/*        try {
            if ( isLoggingDebug()) {
                logDebug("Looking up TCI at URI "+xportURI);
            }
            service = (TargetCacheInvalidator) Naming.lookup(xportURI);
        }
        catch ( NotBoundException e) {
            // The agent doesn't have this service.  Probably a PWA.
            if ( isLoggingWarning()) {
                logWarning("The TargetCacheInvalidator could not be found on the running JBoss instance at URL " + xportURI, e);
            }
        }
        catch ( Exception e) {
            if ( isLoggingError()) {
                logError("Problem looking up remote cache invalidator for URI "+xportURI, e);
            }
        }*/
        return service;
    }

    public void invalidateAllItems( String descriptorUri) throws MalformedURLException, RemoteException, NotBoundException {
        // Tell the remote invalidator to invalidate all items of the given type...
        if ( isLoggingDebug()) {
            logDebug("Attempting to invalidate all items of type "+descriptorUri);
        }

        TargetCacheInvalidator tci = getRemoteInvalidator(getRemoteInvalidatorRmiUrl());
        if ( tci != null) {
            if ( isLoggingDebug()) {
                logDebug("Attempting to call invalidation method "+tci);
            }
            try {
                tci.invalidateAllItems(descriptorUri);
            }
            catch ( RemoteException e) {
                if ( isLoggingError()) {
                    logError("Remote exception throws by cache invalidator", e);
                }
            }
        }
    }

    public void invalidateItems( List<String> uris) throws RemoteException, MalformedURLException, NotBoundException {
        // Tell the remote invalidator to invalidate the given list of items
        if ( isLoggingDebug()) {
            logDebug("Attempting to invalidate "+uris.size()+" items");
        }

        // ... and call the invalidator service for each available agent
        TargetCacheInvalidator tci = getRemoteInvalidator(getRemoteInvalidatorRmiUrl());
        if(tci != null)
        	logDebug(tci.getClass().getName());
        else
        	logDebug("TCI is null");
        
        if ( tci != null) {
            if ( isLoggingDebug()) {
                logDebug("Attempting to call invalidation method "+tci);
            }
            tci.invalidateItems(uris);
/*            try {
                tci.invalidateItems(uris);
            }
            catch ( RemoteException e) {
                if ( isLoggingError()) {
                    logError("Remote exception throws by cache invalidator", e);
                }
            }*/
        }
    }

}
