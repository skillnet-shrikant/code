package com.mff.services.cache;

import atg.deployment.server.DeploymentServer;
import atg.deployment.server.Target;
import atg.nucleus.GenericRMIService;

import java.rmi.RemoteException;
import java.util.List;

public class TargetCacheInvalidatorRemoteImpl extends GenericRMIService implements TargetCacheInvalidator {

    public static final long serialVersionUID = 0x0000112L;

    private TargetCacheInvalidatorHelper mHelper;
    private DeploymentServer deploymentServer = null;

    public DeploymentServer getDeploymentServer() {
        return deploymentServer;
    }

    public void setDeploymentServer(DeploymentServer deploymentServer) {
        this.deploymentServer = deploymentServer;
    }

    public TargetCacheInvalidatorHelper getHelper() {
        return mHelper;
    }

    public void setHelper(TargetCacheInvalidatorHelper pHelper) {
        mHelper = pHelper;
    }

    public TargetCacheInvalidatorRemoteImpl() throws RemoteException
    {
    }

    public void invalidateAllItems( String descriptorUri) throws RemoteException {
        // Invalidate the given descriptor against all targets
        Target targets[] = getAllTargets();
        if ( targets != null) {
            for ( Target target : targets) {
                mHelper.invalidateAllItemsForTarget(target, descriptorUri);
            }
        }
    }

    public void invalidateItems( List<String> uris) throws RemoteException  {
    	logDebug("InvalidateItems called with uris " + uris.size() + " in TCIRemoteImpl");
        // Invalidate the given items against all targets
        Target targets[] = getAllTargets();
        if ( targets != null) {
            for ( Target target : targets) {
                mHelper.invalidateItemsForTarget(target, uris);
            }
        }
    }


    public Target[] getAllTargets() {
        if ( deploymentServer == null) {
            return null;
        }
        return deploymentServer.getTargets();
    }


}
