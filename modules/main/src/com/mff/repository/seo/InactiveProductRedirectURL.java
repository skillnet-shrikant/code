package com.mff.repository.seo;

import com.mff.redirects.PDPRedirectTools;

import atg.repository.Repository;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.service.webappregistry.WebApp;
import atg.servlet.DynamoHttpServletRequest;

public class InactiveProductRedirectURL extends IndirectUrlTemplate {

	boolean enable404Redirects;
	PDPRedirectTools redirectTools;
	
	public PDPRedirectTools getRedirectTools() {
		return redirectTools;
	}

	public void setRedirectTools(PDPRedirectTools pRedirectTools) {
		redirectTools = pRedirectTools;
	}



	public boolean isEnable404Redirects() {
		return enable404Redirects;
	}

	public void setEnable404Redirects(boolean pEnable404Redirects) {
		enable404Redirects = pEnable404Redirects;
	}


	@Override
	public String getForwardUrl(DynamoHttpServletRequest pRequest, String pIndirectUrl, WebApp pDefaultWebApp,
			Repository pDefaultRepository) throws ItemLinkException {
		
		logInfo("Incoming requestURI is " + pRequest.getRequestURI());
		logInfo("Setting useUrlRedirect to false");
		
		setUseUrlRedirect(false);
		String url = super.getForwardUrl(pRequest, pIndirectUrl, pDefaultWebApp, pDefaultRepository);
		logInfo("Default redirect url is " + url);
		
		
		if(isEnable404Redirects()) {
			logInfo("Custom 404 redirects are configured");
			String redirectURL = getRedirectTools().getRedirectURL(pRequest);
			if(redirectURL!=null) {
				setUseUrlRedirect(true);
				url = redirectURL;
			}

		} else {
			logInfo("Custom 404 redirects disabled");
		}
		logInfo("New code is forwarding to " + url + " - Redirect mode is " + isUseUrlRedirect());
		return url;
	}
	
}