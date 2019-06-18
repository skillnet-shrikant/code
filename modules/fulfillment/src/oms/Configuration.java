/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms;

import atg.nucleus.GenericService;

/**
 * 
 * This class is a convenience class which will contain the major configuration
 * information for the OMS system. It is meant to be a central place for
 * configuration information.
 * 
 * 
 * @author KnowledgePath
 */
public class Configuration extends GenericService {

	private boolean mRemorsePeriodEnabled;

	/**
	 * Returns whether the application moves the order state to a Remorse period
	 * or not when first created in OMS.
	 * 
	 * @return whether this application is configured to use Remorse Period
	 */
	public boolean isRemorsePeriodEnabled() {
		return mRemorsePeriodEnabled;
	}

	/**
	 * Sets whether the application uses Remorse period. When set to false the
	 * Remorse period is skipped and the order is sent to PRE_SENT_WMS.
	 * 
	 * @param remorsePeriodEnabled whether this application will use Remorse Period
	 */
	public void setRemorsePeriodEnabled(boolean remorsePeriodEnabled) {
		this.mRemorsePeriodEnabled = remorsePeriodEnabled;
	}

}
