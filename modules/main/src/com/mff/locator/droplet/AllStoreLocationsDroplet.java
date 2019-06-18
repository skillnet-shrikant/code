package com.mff.locator.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.locator.StoreLocatorTools;

/**
 * 
 * @author DMI
 *
 */
public class AllStoreLocationsDroplet extends DynamoServlet {

	private static final String OUTPUT_OPARAM = "output";
	private static final String EMPTY_OPARAM = "empty";
	private static final String STORES_OUT_PARAM = "allStores";

	private StoreLocatorTools mStoreLocatorTools;

	@SuppressWarnings("rawtypes")
  public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		List allStores = getStoreLocatorTools().findAllStores();

		if (allStores != null && allStores.size() > 0) {
			pRequest.setParameter(STORES_OUT_PARAM, allStores);
			pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);
		} else {

			pRequest.serviceLocalParameter(EMPTY_OPARAM, pRequest, pResponse);
		}
	}

	public StoreLocatorTools getStoreLocatorTools() {
		return mStoreLocatorTools;
	}

	public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
		this.mStoreLocatorTools = pStoreLocatorTools;
	}
}
