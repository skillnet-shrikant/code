package com.mff.commerce.order;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.nucleus.naming.ComponentName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.mff.account.order.bean.MFFOrderDetails;
import com.mff.constants.MFFConstants;

public class MFFOrderDetailLookup extends DynamoServlet {
	
	private boolean mEnableSecurity;
	private MFFOrderDetailHelper mOrderDetailHelper;

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("MFFOrderDetailLookup :: service () :: START");
			
		String orderId = (String) pRequest.getParameter(MFFConstants.ORDER_ID);
		String isLegacy = (String) pRequest.getParameter("lgc");
		
		vlogDebug("orderId =  " + orderId + " ,isLegacy =  " + isLegacy);
		
		RepositoryItem orderItem;
		MFFOrderDetails mffOrderDetailObj = new MFFOrderDetails();
		try {
			orderItem = getOrderDetailHelper().getOrderItemById(orderId,isLegacy);
		
			if (orderItem!=null) {
				
				if (isEnableSecurity()) {
					String reqProfileId = getCurrentProfileId(pRequest);
					String orderProfileId = getOrderDetailHelper().getProfileIdForOrder(orderItem);
					
					vlogDebug("checking ownership. current user is: "
							+ reqProfileId + " ,order owner is: " + orderProfileId);
					if (!reqProfileId.equals(orderProfileId)) {
						pRequest.setParameter("errorMsg", "NoPermissionForOrder");
						pRequest.serviceLocalParameter("error", pRequest, pResponse);
						return;
					}
				}
				getOrderDetailHelper().fillOrderDetailsByOrderId(orderItem, mffOrderDetailObj);
			}else {
				pRequest.setParameter("errorMsg", "NoSuchOrder");
				pRequest.serviceLocalParameter("error", pRequest, pResponse);
				return;
			}
			
		} catch (CommerceException e) {
			vlogError("Commerce Exception while fetching order: " + e, e);
		} catch (RepositoryException e) {
			vlogError("Repository Exception while fetching order: " + e, e);
		}
		
		pRequest.setParameter("result", mffOrderDetailObj);
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
		vlogDebug("MFFOrderDetailLookup :: service () :: END");
		return;
	}
	
	protected String getCurrentProfileId(DynamoHttpServletRequest pRequest) {
		Profile profile = (Profile) pRequest.resolveName(ComponentName.getComponentName(MFFConstants.ATG_PROFILE));
		if (profile != null) {
			return profile.getRepositoryId();
		}
		return "-1";
	}

	public boolean isEnableSecurity() {
		return mEnableSecurity;
	}
	
	public void setEnableSecurity(boolean pEnableSecurity) {
		mEnableSecurity = pEnableSecurity;
	}

	public MFFOrderDetailHelper getOrderDetailHelper() {
		return mOrderDetailHelper;
	}

	public void setOrderDetailHelper(MFFOrderDetailHelper pOrderDetailHelper) {
		mOrderDetailHelper = pOrderDetailHelper;
	}
}
