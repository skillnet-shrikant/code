package com.mff.commerce.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.csr.returns.ReturnException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class MFFReturnStatusDroplet extends DynamoServlet {

	public static final ParameterName ORDER_ID = ParameterName.getParameterName("orderId");
	public static final ParameterName RESULT_NAME = ParameterName.getParameterName("resultName");
	public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName ERROR = ParameterName.getParameterName("error");
	public static final String RESULT = "result";

	private MFFOrderDetailHelper mOrderDetailHelper;

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (this.isLoggingDebug()) {
			this.logDebug("Entered service routine.");
		}

		String orderId = pRequest.getParameter(ORDER_ID);
		String resultParameter;
		if (orderId != null && orderId.trim().length() != 0) {
			if (this.isLoggingDebug()) {
				this.logDebug("orderId=" + orderId);
			}

			resultParameter = pRequest.getParameter(RESULT_NAME);
			if (resultParameter == null) {
				resultParameter = "result";
			}

			if (this.isLoggingDebug()) {
				this.logDebug("resultParameter=" + resultParameter);
			}

			try {
				ArrayList returnRequests = new ArrayList();
				List requestsByOrderId = getOrderDetailHelper().getReturnRequestsByOrderId(orderId);
				if (requestsByOrderId != null && requestsByOrderId.size() > 0) {
					returnRequests.addAll(requestsByOrderId);
				}

				if (this.isLoggingDebug()) {
					this.logDebug("returnRequests List has "
							+ Integer.valueOf(returnRequests.size())
							+ " record(s) in it.");
				}

				pRequest.setParameter(resultParameter, returnRequests);
				pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
			} catch (ReturnException e) {
				if (this.isLoggingError()) {
					this.logError("RepositoryException occurred during getting return request: "
									+ e, e);
				}

				pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
			}

		}
	}
	
	public MFFOrderDetailHelper getOrderDetailHelper() {
		return mOrderDetailHelper;
	}

	public void setOrderDetailHelper(MFFOrderDetailHelper pOrderDetailHelper) {
		mOrderDetailHelper = pOrderDetailHelper;
	}
}
