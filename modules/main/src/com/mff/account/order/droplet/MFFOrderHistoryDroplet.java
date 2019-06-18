
package com.mff.account.order.droplet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import mff.MFFEnvironment;

import org.apache.commons.lang3.StringUtils;

import atg.commerce.CommerceException;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderQueries;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.order.MFFOrderQueries;
import com.mff.constants.MFFConstants;

/**
 * This Class used for Order History Functionality
 * @author : DMI
 */
public class MFFOrderHistoryDroplet extends DynamoServlet {
	
	private OrderManager mOrderManager;
	private int numOfMonthsForOrderHistory;
	public Repository mOmsOrderRepository = null;
	private List<String> mOrderProperties;
	private int mPerpage;
	private Profile mProfile;
	private MFFEnvironment mEnvironment;
	
	/**
	 * @return the perpage
	 */
	public int getPerpage() {
		return mPerpage;
	}

	/**
	 * @param pPerpage the perpage to set
	 */
	public void setPerpage(int pPerpage) {
		mPerpage = pPerpage;
	}
	
	/**
	 * @return the orderManager
	 */
	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * THis method is used to get the submitted orders of the user
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		vlogDebug("MFFOrderHistoryDroplet: service(): START ");
		String profileId=(String)pRequest.getParameter("userId");
		
		vlogDebug("ProfileId to get the orders of the user: "+profileId);
		String profileIdForLegacyRepository = getProfileIdForLegacyOrders(profileId);
	
		OrderQueries orderQueries = getOrderManager().getOrderQueries();
		int mStartIndex=0;
		int mEndIndex=0;
		int currentPage=0;
		String lCurrentPage=(String)pRequest.getParameter("currentPage");
		vlogDebug("currentPage1: " + lCurrentPage);
		
		
		if(!StringUtils.isEmpty(lCurrentPage) && StringUtils.isNumeric(lCurrentPage)){
			currentPage=Integer.parseInt(lCurrentPage);
			mStartIndex=(currentPage-1)*getPerpage();
		}else{
			mStartIndex=0;
			currentPage=1;
		}
		vlogDebug("currentPage2: " + lCurrentPage);
		vlogDebug("mStartIndex: " + mStartIndex);
		
		int mLegacyStartIndex=0;
		int mLegacyEndIndex=0;
		// Get orders based on profileId
		if (profileId != null) {
			try {
				List<Map> coreOrders=null;
				List<Map> legacyOrders=null;
				Calendar lCal=new GregorianCalendar();
				Date lToday=lCal.getTime();
				lCal.add(Calendar.MONTH, getNumOfMonthsForOrderHistory()*-1);
				
				RepositoryView orderView = getOmsOrderRepository().getView("order");
				RepositoryView legacyOrderView = ((MFFOrderManager)getOrderManager()).getLegacyOrderRepository().getView("order");
				
				int orderCount= ((MFFOrderQueries)orderQueries).getCountForOrdersWithinDateRange(profileId, lCal.getTime(), lToday, orderView);
				vlogDebug("Order Count {0}", orderCount);
				
				int legacyOrderCount = 0;
				if (!StringUtils.isBlank(profileIdForLegacyRepository)){
					
					legacyOrderCount = ((MFFOrderQueries) orderQueries)
							.getCountForOrdersWithinDateRange(profileIdForLegacyRepository,
									lCal.getTime(), lToday, legacyOrderView);
				}
				vlogDebug("Legacy Order Count {0}", legacyOrderCount);
				
				if (mStartIndex <= orderCount) {
					
					int nextSetSize = mStartIndex+getPerpage();
					vlogDebug("nextSetSize: " + nextSetSize);
					
					if(nextSetSize > orderCount){
						mLegacyEndIndex=nextSetSize-orderCount;
						
						mLegacyStartIndex=0;
						if(!(legacyOrderCount>0 && legacyOrderCount>mLegacyEndIndex)){
							mLegacyEndIndex=legacyOrderCount;
						}
						mEndIndex=orderCount;
					}else{
						mEndIndex=nextSetSize;
					}
					
					vlogDebug("StartIndex {0} and EndIndex: {1}", mStartIndex,mEndIndex);
					coreOrders = ((MFFOrderQueries)orderQueries).getOrdersForProfileWithinDateRange(profileId, lCal.getTime(), lToday, "submittedDate", false, getOrderProperties(), orderView, mStartIndex, mEndIndex);
				}else{
					vlogDebug("case: legacy orders.");
					if(mStartIndex>orderCount && mStartIndex<(orderCount+legacyOrderCount)){
						vlogDebug("Determining legacy start and end index.");
						mLegacyStartIndex=mStartIndex-orderCount;
						mLegacyEndIndex=mLegacyStartIndex+getPerpage();
					}
				}
				
				vlogDebug("mLegacyStartIndex {0} and mLegacyEndIndex: {1}", mLegacyStartIndex,mLegacyEndIndex);
				
				if (!StringUtils.isBlank(profileIdForLegacyRepository)){
					if (mLegacyStartIndex >= 0 && mLegacyEndIndex > 0) {
						legacyOrders=((MFFOrderQueries)orderQueries).getOrdersForProfileWithinDateRange(profileIdForLegacyRepository, lCal.getTime(), lToday, "submittedDate", false, getOrderProperties(), legacyOrderView, mLegacyStartIndex, mLegacyEndIndex);
					}
				}
				if(coreOrders!=null){
					if(legacyOrders!=null){
						coreOrders.addAll(legacyOrders);
					}
				}else{
					coreOrders=legacyOrders;
				}
				int numberOfPages = 1;
				int balanceItems = 0;
				if(coreOrders!=null && !coreOrders.isEmpty()){
					vlogDebug("Order Size :{0}", coreOrders.size());
					int totalOrders=orderCount+legacyOrderCount;
					if(totalOrders > getPerpage()){
						numberOfPages = totalOrders/getPerpage();
						balanceItems = totalOrders % getPerpage();
						if(balanceItems > 0 && balanceItems < getPerpage()){
							numberOfPages = numberOfPages +1;
						}
					}
					pRequest.setParameter("numberOfPages", numberOfPages);
					pRequest.setParameter(MFFConstants.ORDERS, coreOrders);
					pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
					
				} else {
					vlogDebug("No Orders found for this user :: "+profileId);
					pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
				}
			} catch (CommerceException e) {
				vlogError("CommerceException occurred :: "+e);
			} catch (RepositoryException e) {
				vlogError("RepositoryException occurred :: "+e.getMessage());
			}

		} else {
			vlogDebug("User is not logged-in to get the orders :: ");
		}
		vlogDebug("MFFOrderHistoryDroplet :: service() :: END ");
	}

	/**
	 * @param profileId
	 * @return
	 */
	private String getProfileIdForLegacyOrders(String profileId) {
		String profileIdForLegacyRepository = null;
		if (StringUtils.isNotBlank(profileId)){
			if(profileId.startsWith(getEnvironment().getLegacyPrefix())){
				vlogDebug("User is Legacy.");
				String [] profileIdValues = profileId.split(getEnvironment().getLegacyPrefix());
				profileIdForLegacyRepository = profileIdValues[1];
				vlogDebug("ProfileId to get orders from legacy: "+profileIdForLegacyRepository);
			} else {
				profileIdForLegacyRepository = profileId;
			}
		}
		return profileIdForLegacyRepository;
	}

	public int getNumOfMonthsForOrderHistory() {
		return numOfMonthsForOrderHistory;
	}

	public void setNumOfMonthsForOrderHistory(int numOfMonthsForOrderHistory) {
		this.numOfMonthsForOrderHistory = numOfMonthsForOrderHistory;
	}

	public List<String> getOrderProperties() {
		return mOrderProperties;
	}

	public void setOrderProperties(List<String> mOrderProperties) {
		this.mOrderProperties = mOrderProperties;
	}

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile mProfile) {
		this.mProfile = mProfile;
	}
	
	public Repository getOmsOrderRepository() {
		return mOmsOrderRepository;
	}

	public void setOmsOrderRepository(Repository pOmsOrderRepository) {
		mOmsOrderRepository = pOmsOrderRepository;
	}

	public MFFEnvironment getEnvironment() {
		return mEnvironment;
	}

	public void setEnvironment(MFFEnvironment pEnvironment) {
		mEnvironment = pEnvironment;
	}

}
