package com.mff.commerce.order;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.nucleus.naming.ComponentName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.mff.constants.MFFConstants;

public class MFFLegacyOrderLookup extends DynamoServlet {
	
	private MFFOrderManager mOrderManager;
	private boolean mEnableSecurity;

	/**
	 * @return the orderManager
	 */
	public MFFOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(MFFOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * @return the enableSecurity
	 */
	public boolean isEnableSecurity() {
		return mEnableSecurity;
	}

	/**
	 * @param pEnableSecurity the enableSecurity to set
	 */
	public void setEnableSecurity(boolean pEnableSecurity) {
		mEnableSecurity = pEnableSecurity;
	}

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("MFFLegacyOrderLookup :: service () :: START");
		Order order = null;
		
		String orderId = (String) pRequest.getParameter(MFFConstants.ORDER_ID);
		vlogDebug("orderId =  " + orderId);
		try {
			if (getOrderManager().legacyOrderExists(orderId)) {
				order = getOrderManager().loadLegacyOrder(orderId);
			}
		} catch (CommerceException e) {
			vlogError("CommerceException occurred "+e.getMessage());
		}
		if (order == null) {
			pRequest.setParameter("errorMsg", "NoSuchOrder");
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
			return;
		}
		if (isEnableSecurity()) {
				vlogDebug("checking ownership. current user is "
						+ getCurrentProfileId(pRequest) + " order owner is "
						+ order.getProfileId());
			if (!(order.getProfileId().equals(getCurrentProfileId(pRequest)))) {
				pRequest.setParameter("errorMsg", "NoPermissionForOrder");
				pRequest.serviceLocalParameter("error", pRequest, pResponse);
				return;
			}
		}
		pRequest.setParameter("result", order);
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
		vlogDebug("MFFLegacyOrderLookup :: service () :: END");
		return;
	}
	
	protected String getCurrentProfileId(DynamoHttpServletRequest pRequest) {
		Profile profile = (Profile) pRequest.resolveName(ComponentName.getComponentName(MFFConstants.ATG_PROFILE));
		if (profile != null) {
			return profile.getRepositoryId();
		}
		return "-1";
	}
}
