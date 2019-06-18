package com.mff.userprofiling;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.userprofiling.RepositoryProfileItemFinder;

public class MFFRepositoryProfileItemFinder extends RepositoryProfileItemFinder {
	public RepositoryItem[] findByEmployeeInfo(String pSomCard, String pPhone, String pEmpId) {
		try {
			if (this.isLoggingDebug()) {
				this.logDebug("findByEmployeeInfo");
			}
	
			if(pSomCard == null || pPhone == null || pEmpId == null) {
				return null;
			} else {
				RepositoryView view = getProfileRepository().getView(getProfileRepository().getDefaultViewName());
				QueryBuilder qb = view.getQueryBuilder();
				Query query = this.generateEmployeeQuery(pSomCard, pPhone, pEmpId, qb);
				RepositoryItem[] items = view.executeQuery(query);
				return items;
			}
			
		} catch (RepositoryException re) {
			if(isLoggingError()) {
				logError(re);
			}
		}
		return null;
	}
	
	public Query generateEmployeeQuery(String pSomCard, String pPhone, String pEmpId, QueryBuilder pQueryBuilder) throws RepositoryException {
		   if (this.isLoggingDebug()) {
		      this.logDebug("Generating employee query " );
		   }

		   // employeeId
		   QueryExpression empIdPty = pQueryBuilder.createPropertyQueryExpression("employeeId");
		   QueryExpression empIdValue = pQueryBuilder.createConstantQueryExpression(pEmpId);
		   Query empIdQuery = pQueryBuilder.createComparisonQuery(empIdPty, empIdValue, QueryBuilder.EQUALS);
		   
		   //somCard
		   QueryExpression somCardPty = pQueryBuilder.createPropertyQueryExpression("somCard");
		   QueryExpression somCardValue = pQueryBuilder.createConstantQueryExpression(pSomCard);
		   Query somCardQuery = pQueryBuilder.createComparisonQuery(somCardPty, somCardValue, QueryBuilder.EQUALS);
		   
		   //phoneNumber
		   QueryExpression phoneNumberPty = pQueryBuilder.createPropertyQueryExpression("phoneNumber");
		   QueryExpression phoneNumberValue = pQueryBuilder.createConstantQueryExpression(pPhone);
		   Query phoneNumberQuery = pQueryBuilder.createComparisonQuery(phoneNumberPty, phoneNumberValue, QueryBuilder.EQUALS);		   

			Query[] compoundQuery = new Query[]{empIdQuery, somCardQuery,phoneNumberQuery};
			
			Query employeeQuery = pQueryBuilder.createAndQuery(compoundQuery);
			return employeeQuery;		   
	}
}
