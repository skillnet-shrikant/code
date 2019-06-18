package oms.commerce.order;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItemManager;

public class OMSCommerceItemManager extends CommerceItemManager{
	
	private boolean enableSiteCompatibility;
	
	protected void validateSiteCompatibility(String pExistingSiteId, String pNewSiteId)
             throws CommerceException{
		
        if(isEnableSiteCompatibility()){
        	super.validateSiteCompatibility(pExistingSiteId, pNewSiteId);
        }else{
        	vlogInfo("OMSCommerceItemManager validateSiteCompatibility disabled ");
        }
    }

	public boolean isEnableSiteCompatibility() {
		return enableSiteCompatibility;
	}

	public void setEnableSiteCompatibility(boolean enableSiteCompatibility) {
		this.enableSiteCompatibility = enableSiteCompatibility;
	}
	
	
}
