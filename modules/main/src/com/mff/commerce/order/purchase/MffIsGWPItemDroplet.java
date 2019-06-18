package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * The Class MffIsGWPItemDroplet.
 *
 * @author manoj_mane
 */
/**
 * @author MANOJ
 *
 */
public class MffIsGWPItemDroplet extends DynamoServlet {

	/** The Constant ORDER. */
	private static final ParameterName COMMERCE_ITEM = ParameterName.getParameterName("commerceItem");

	/** The Constant OUTPUT. */
	private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

	/** The result. */
	private final String RESULT = "result";

	/**
	 * Service method of a servlet.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("rawtypes")
	public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
			throws ServletException, IOException {
		
		vlogDebug("MffIsGWPItemDroplet service : start");
		boolean isGWP = false;

		MFFCommerceItemImpl commerceItem = (MFFCommerceItemImpl) request.getLocalParameter(COMMERCE_ITEM);
		if (commerceItem == null) {
				vlogError("MffIsGWPItemDroplet : commerceItem is a required parameter.");
			return;
		}

		if (commerceItem.getRepositoryItem() != null) {
			Set gwpMarker = (Set) commerceItem.getRepositoryItem().getPropertyValue("gwpMarkers");
			if (gwpMarker != null && gwpMarker.size() > 0) {
				
				vlogDebug("MffIsGWPItemDroplet service :"
						+ " commerceItem with id {0} is gwp item", commerceItem.getId());
				isGWP = true;
			}
		}

		request.setParameter(RESULT, isGWP);
		request.serviceLocalParameter(OUTPUT, request, response);
		
		vlogDebug("MffIsGWPItemDroplet service : exit");
	}

}
