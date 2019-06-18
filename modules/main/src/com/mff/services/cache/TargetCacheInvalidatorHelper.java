package com.mff.services.cache;

import java.util.List;
import atg.adapter.gsa.GSARepository;
import atg.deployment.server.Target;
import atg.deployment.server.AgentRef;
import atg.deployment.common.DeploymentException;
import atg.deployment.server.DeploymentServer;
import atg.nucleus.GenericService;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class TargetCacheInvalidatorHelper extends GenericService {

	private DeploymentServer deploymentServer = null;
	
	public DeploymentServer getDeploymentServer() {
		return deploymentServer;
	}

	public void setDeploymentServer(DeploymentServer deploymentServer) {
		this.deploymentServer = deploymentServer;
	}

	public void invalidateAllItemsForRepository( GSARepository repository, String descriptorUri) {
		// Get a Target for this repository
		Target target = getTargetFromRepository( repository);
		if ( target == null) {
			if ( isLoggingWarning()) {
				logWarning("Can't invalidate items for repository "+repository.getAbsoluteName()+" : no target");
			}
			return;
		}

		// Invalidate
		invalidateAllItemsForTarget( target, descriptorUri);
	}
	
	public void invalidateItemsForRepository( GSARepository repository, List<String> uris) {
		if ( uris == null || uris.size() == 0) {
			if ( isLoggingDebug())
				logDebug("No items to perform remote invalidation on");
			return;
		}
		
		// Get a Target for this repository
		Target target = getTargetFromRepository( repository);
		if ( target == null) {
			if ( isLoggingWarning()) {
				logWarning("Can't invalidate items for repository "+repository.getAbsoluteName()+" : no target");
			}
			return;
		}
		
		// Invalidate
		invalidateItemsForTarget( target, uris);
	}

	public void invalidateAllItemsForTargetName( String name, String descriptorUri) {
		// Get a Target for this name
		Target target = getTargetFromName( name);
		if ( target == null) {
			if ( isLoggingWarning()) {
				logWarning("Can't invalidate items for target "+name+" : no target found");
			}
			return;
		}

		// Invalidate
		invalidateAllItemsForTarget( target, descriptorUri);
	}

	public void invalidateItemsForTargetName( String name, List<String> uris) {
		// Get a Target for this name
		Target target = getTargetFromName( name);
		if ( target == null) {
			if ( isLoggingWarning()) {
				logWarning("Can't invalidate items for target "+name+" : no target found");
			}
			return;
		}
		
		// Invalidate
		invalidateItemsForTarget( target, uris);
	}
	
	public void invalidateAllItemsForTarget( Target target, String descriptorUri) {
		// Get the agents for the given target...
		AgentRef[] agents = target.getAgents();
		if ( isLoggingDebug()) {
			logDebug("Attempting to invalidate all items of type "+descriptorUri+" for target "+target.getName());
		}
		
		// ... and call the invalidator service for each available agent
		for ( AgentRef agent : agents) {
			if ( isAgentAvailable(agent)) {
				TargetCacheInvalidator tci = getInvalidatorForAgent(agent);
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
		}
	}
	
	public void invalidateItemsForTarget( Target target, List<String> uris) {
		// Get the agents for the given target...
		AgentRef[] agents = target.getAgents();
		if ( isLoggingDebug()) {
			logDebug("Attempting to invalidate "+uris.size()+" items at target "+target.getName());
		}
		
		// ... and call the invalidator service for each available agent
		for ( AgentRef agent : agents) {
			if ( isAgentAvailable(agent)) {
				TargetCacheInvalidator tci = getInvalidatorForAgent(agent);
				if ( tci != null) {
					if ( isLoggingDebug()) {
						logDebug("Attempting to call invalidation method "+tci);
					}
					try {
						tci.invalidateItems(uris);
					}
					catch ( RemoteException e) {
						if ( isLoggingError()) {
							logError("Remote exception throws by cache invalidator", e);
						}
					}
				}
			}
		}		
	}

	private boolean isAgentAvailable( AgentRef agent) {
		// Get the agent status first...  if it's 4 or 5, the agent is probably down
		int agentStat = agent.getState();
		if ( agentStat == 4 || agentStat == 5) {
			return false;
		}
		return true;
	}
	
	private String getTransportUriFromAgent( AgentRef agent) {
		// Get the transport URI of the agent to determine the path to the RMI server
		String uri = null;
		try {
			String agentURI = agent.getTransportURI();
			
			// Get the RMI host and port...  everything between the 2nd and 3rd "/"
			String parts[] = agentURI.split("/");
			String rmiHost = parts[2];
			if ( isLoggingDebug()) {
				logDebug("The RMI host:port is "+rmiHost);
			}
			
			uri = "rmi://" + rmiHost + "/mff/services/cache/TargetCacheInvalidator";
		}
		catch ( DeploymentException e) {
			if ( isLoggingError()) {
				logError("Couldn't get a transport URI for agent "+agent.getName());
			}
		}
		return uri;
	}
	
	public TargetCacheInvalidator getInvalidatorForAgent( AgentRef agent) {
		// Get the URI of the invalidator service
		TargetCacheInvalidator service = null;
		String xportURI = getTransportUriFromAgent( agent);
		
		// And look it up
		try {
			if ( isLoggingDebug()) {
				logDebug("Looking up TCI at URI "+xportURI);
			}
			service = (TargetCacheInvalidator)Naming.lookup(xportURI);
		}
		catch ( NotBoundException e) {
			// The agent doesn't have this service.  Probably a PWA.
			if ( isLoggingWarning()) {
				logWarning("The agent "+agent+" has no TargetCacheInvalidator registered.  Probably a publishing web agent.", e);
			}
		}
		catch ( Exception e) {
			if ( isLoggingError()) {
				logError("Problem looking up remote cache invalidator for URI "+xportURI, e);
			}
		}
		return service;
	}
	
	public Target getTargetFromRepository( GSARepository repository) {
		// Given a repository, the target is determined by looking at the repository name suffix, so...
		//
		//  _production = Production
		//  _staging = Staging
		if ( repository != null) {
			String repName = repository.getAbsoluteName();
			if ( repName.endsWith("_production")) {
				return getTargetFromName("Production");
			}
			else if ( repName.endsWith("_staging")) {
				return getTargetFromName("Staging");
			}
		}
		return null;
	}

	public Target getTargetFromName( String name) {
		// Given a target name, get the corresponding Target object for the target, if it exists
		if ( deploymentServer == null) {
			return null;
		}
		return deploymentServer.getTargetByName(name);
	}

	public void testRmiCall() {
		invalidateAllItemsForTargetName( "Production", "atgrep:/ProductCatalog/category");
	}
}
