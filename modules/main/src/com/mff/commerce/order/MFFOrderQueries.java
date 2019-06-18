package com.mff.commerce.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderQueries;
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.states.OrderStates;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

public class MFFOrderQueries extends OrderQueries {

	/**
	 * This is used to get the LegacyOrdes
	 * @param pProfileId
	 * @return
	 * @throws CommerceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getLegacyOrdersForProfile(String pProfileId) throws CommerceException {
		return ((MFFOrderManager)getOrderManager()).loadLegacyOrders(getLegacyOrderIdsForProfile(pProfileId));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getLegacyOrderIdsForProfile(String pProfileId)throws CommerceException {
		
		if ((pProfileId == null) || (pProfileId.trim().length() == 0)) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidProfileIdParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
		}

		boolean reverse_sort = false;
		RepositoryItem[] items = null;
			try {
				RepositoryView legacyOrderView = ((MFFOrderManager)getOrderManager()).getLegacyOrderRepository().getView("order");
				QueryBuilder orderQueryBuilder = legacyOrderView.getQueryBuilder();
				
				QueryExpression profileIdProperty = orderQueryBuilder.createPropertyQueryExpression(PropertyNameConstants.PROFILEID);
				QueryExpression profileIdValue = orderQueryBuilder.createConstantQueryExpression(pProfileId);
				Query orderQuery = orderQueryBuilder.createComparisonQuery(profileIdProperty, profileIdValue, QueryBuilder.EQUALS);
				items = legacyOrderView.executeQuery(orderQuery);
			} catch (RepositoryException e) {
				throw new CommerceException(e);
			}
			
				
		if (items == null) {
			return new ArrayList(0);
		}

		int size = items.length;
		ArrayList orderIds = new ArrayList(size);
		if (reverse_sort) {
			for (int i = size - 1; i >= 0; --i)
				orderIds.add(items[i].getRepositoryId());
		} else {
			for (int i = 0; i < size; ++i)
				orderIds.add(items[i].getRepositoryId());
		}
		return orderIds;
	}
	
	/**
	 * This is used to get the profile Order details based on submmitedDate
	 * @param String pProfileId,Date pFrom, Date pTo,String pOrderByProperty,boolean pAscending,
			List<String> pOrderProperties,RepositoryView rv, int pStartIndex, int pEndIndex
	 * @return ArrayList (Orders)
	 * @throws CommerceException
	 */
	@SuppressWarnings("rawtypes")
	public List getOrdersForProfileWithinDateRange(String pProfileId, Date pFrom, Date pTo,String pOrderByProperty,boolean pAscending,
			List<String> pOrderProperties,RepositoryView rv, int pStartIndex, int pEndIndex)
		    throws CommerceException
    {
	    if (pProfileId == null) {
	    	throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidProfileIdParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
	    }
	    if (pFrom == null) {
	    	throw new InvalidParameterException();
	    }
	    if ((pTo != null) && 
	      (pFrom.after(pTo))) {
	    	throw new InvalidParameterException();
	    }
	    RepositoryItem[] items;
	    try
	    {
	      Query query=getOrderQueryForProfileWithinDateRange(pProfileId, pFrom, pTo, rv);
	      SortDirectives sds = null;
	      if (pOrderByProperty != null)
	      {
	        sds = new SortDirectives();
	        sds.addDirective(new SortDirective(pOrderByProperty, pAscending?SortDirective.DIR_ASCENDING:SortDirective.DIR_DESCENDING));
	      }
	      items = rv.executeQuery(query,pStartIndex,pEndIndex,sds);
	    }
	    catch (RepositoryException e)
	    {
	      throw new CommerceException(e);
	    }
	    if (items == null) {
	      return new ArrayList(0);
	    }
	    ArrayList<Map> orders = new ArrayList<Map>(items.length);
	    for (int i = 0; i < items.length; i++) {
	      HashMap<String,Object> orderMap=new HashMap<>();
	      for(int j = 0; j < pOrderProperties.size(); j++){
	    	  if(pOrderProperties.get(j).contains(".")){
	    		  String[] props=pOrderProperties.get(j).split("\\.");
	    		  RepositoryItem tmpItem=(RepositoryItem)items[i].getPropertyValue(props[0]);
	    		  if(tmpItem!=null){
	    			  orderMap.put(props[1], tmpItem.getPropertyValue(props[1]));
	    		  }
	    	  }else{
	    		  orderMap.put(pOrderProperties.get(j), items[i].getPropertyValue(pOrderProperties.get(j)));
	    	  }
	      }
	      orders.add(orderMap);
	    }
	    return orders;
	}
	
	/**
	 * Returns Order Count based on the parameters passed.
	 * @param String pProfileId, Date pFrom, Date pTo,RepositoryView orderView
	 * @return int (Order Count)
	 * @throws CommerceException
	 */
	public int getCountForOrdersWithinDateRange(String pProfileId, Date pFrom, Date pTo,RepositoryView orderView)
			throws CommerceException
	{
		int orderCount=0;
		try {
			orderCount = orderView.executeCountQuery(getOrderQueryForProfileWithinDateRange(pProfileId, pFrom, pTo, orderView));
		} catch (RepositoryException e) {
			throw new CommerceException(e);
		}
		return orderCount;
	}
	
	protected Query getOrderQueryForProfileWithinDateRange(String pProfileId,Date pFrom, Date pTo, RepositoryView rv) throws CommerceException, RepositoryException{
		Query[] q = new Query[3];
		QueryBuilder qb = rv.getQueryBuilder();
	    q[0] = getDateRangeQuery(pFrom, pTo, qb);
	  
	    QueryExpression expr1 = qb.createPropertyQueryExpression(PropertyNameConstants.PROFILEID);
	    QueryExpression expr2 = qb.createConstantQueryExpression(pProfileId);
	    q[1] = qb.createComparisonQuery(expr1, expr2, QueryBuilder.EQUALS);
	    QueryExpression orderState1 = qb.createPropertyQueryExpression(PropertyNameConstants.ORDERSTATE);
	    QueryExpression orderState2 = qb.createConstantQueryExpression(StringUtils.toUpperCase(OrderStates.INCOMPLETE));
	    q[2] = qb.createComparisonQuery(orderState1, orderState2, QueryBuilder.NOT_EQUALS);
	   
	    Query query = qb.createAndQuery(q);
	    return query;
	}
		  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Query getDateRangeQuery(Date pFrom, Date pTo,QueryBuilder qb)
		    throws CommerceException
	{
	    if (pFrom == null) {
	      throw new InvalidParameterException();
	    }
	    ArrayList list = new ArrayList(2);
	    QueryExpression expr1;
	    QueryExpression expr2;
	    Query query;
	    try
	    {
	      expr1 = qb.createPropertyQueryExpression(PropertyNameConstants.SUBMITTEDDATE);
	      expr2 = qb.createConstantQueryExpression(pFrom);
	      query = qb.createComparisonQuery(expr1, expr2, QueryBuilder.GREATER_THAN_OR_EQUALS);
	    }
	    catch (RepositoryException e)
	    {
	      throw new CommerceException(e);
	    }
	    list.add(query);
	    if (pTo != null)
	    {
	      try
	      {
	        expr1 = qb.createPropertyQueryExpression(PropertyNameConstants.SUBMITTEDDATE);
	        expr2 = qb.createConstantQueryExpression(pTo);
	        query = qb.createComparisonQuery(expr1, expr2, QueryBuilder.LESS_THAN_OR_EQUALS);
	      }
	      catch (RepositoryException e)
	      {
	        throw new CommerceException(e);
	      }
	      list.add(query);
	    }
	    Query andQuery;
	    try
	    {
	      andQuery = qb.createAndQuery((Query[])list.toArray(new Query[0]));
	    }
	    catch (RepositoryException e)
	    {
	      throw new CommerceException(e);
	    }
	    return andQuery;
	}
}
