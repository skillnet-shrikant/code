package com.mff.services.cache;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TargetCacheInvalidator extends Remote
{
	public void invalidateAllItems( String descriptorUri) throws RemoteException;
	public void invalidateItems( List<String> uris) throws RemoteException;
}
