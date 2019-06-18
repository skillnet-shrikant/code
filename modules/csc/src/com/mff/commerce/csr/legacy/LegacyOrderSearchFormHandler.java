package com.mff.commerce.csr.legacy;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;

import atg.adapter.gsa.query.Builder;
import atg.commerce.csr.environment.CSREnvironmentTools;
import atg.commerce.csr.search.RepositorySearcher;
import atg.commerce.csr.search.SearchAttribute;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlQuery;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.svc.search.RepositoryQueryTableFormHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;

public class LegacyOrderSearchFormHandler extends RepositoryQueryTableFormHandler {

	private CSREnvironmentTools			mCSREnvironmentTools;
	private RepositorySearcher			mRepositorySearcher;
	private int							mSearchableAttributeNum;
	private SearchAttribute				mSearchAttributes[];
	private Query mQuery;

	@Override
	protected Query createSearchQuery(QueryBuilder querybuilder) throws RepositoryException {
		
		if(null!=getQuery()){
			
			return getQuery();
		}
		if (getRepositorySearcher() != null) {
			try {
				return getRepositorySearcher().generateSearchQuery(getSearchAttributes(), getRepository(), 
						getRepositoryView().getViewName(), Locale.US);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		vlogDebug(" mRepositorySearcher is null");
		return null;		
	}

	@Override
	public boolean handleSearch(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, Exception {
		validateAndSetDateInput(pRequest);
		generateProfileSubQuery(pRequest);
		if (getFormError()) {
			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
		}
		vlogDebug("StoreOrderSearchFormHandler : Inside handleSearch");
		
		return super.handleSearch(pRequest, pResponse);
	}
	
	public void setSearchAttributes(SearchAttribute pSearchAttributes[]) {
		mSearchAttributes = pSearchAttributes;
	}

	public SearchAttribute[] getSearchAttributes() {
		return mSearchAttributes;
	}

	public void setSearchAttribute(int pIndex, SearchAttribute pSearchAttribute) {
		mSearchAttributes[pIndex] = pSearchAttribute;
	}

	public SearchAttribute getSearchAttribute(int pIndex) {
		return mSearchAttributes[pIndex];
	}

	public void setSearchableAttributeNum(int pSearchableAttributeNum) {
		mSearchableAttributeNum = pSearchableAttributeNum;
		setSearchAttributes(new SearchAttribute[pSearchableAttributeNum]);
		for (int index = 0; index < pSearchableAttributeNum; index++) {
			SearchAttribute searchAttribute = new SearchAttribute();
			getSearchAttributes()[index] = searchAttribute;
		}
		vlogDebug((new StringBuilder()).append("Set mSearchableAttributes array size = ").append(getSearchAttributes().length).toString());
	}
	
	private void validateAndSetDateInput(DynamoHttpServletRequest pRequest) {

		String startDate = pRequest.getParameter("startDate");
		String endDate = pRequest.getParameter("endDate");
		vlogDebug("validateAndSetDateInput - Start Data : " + startDate);
		vlogDebug("validateAndSetDateInput - End Data : " + endDate);
		try {
			if (!StringUtils.isBlank(startDate)) {
				Date startDateFormatted = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
				startDate = new SimpleDateFormat("M/d/yy").format(startDateFormatted);
				vlogDebug("validateAndSetDateInput - formatted Start Data : " + startDate);

				if (!StringUtils.isBlank(endDate)) {
					Date endDateFormatted = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
					endDate = new SimpleDateFormat("M/d/yy").format(endDateFormatted);
					vlogDebug("validateAndSetDateInput - formatted End Data : " + endDate);

					// check if both end and start date are present, end date is
					// not before start date
					if (!StringUtils.isBlank(startDate) && startDateFormatted.getTime() > endDateFormatted.getTime()) {
						addFormException(new DropletException("End Date cannot be before Start Date"));
						return;
					}
				}
			}

			SearchAttribute startDateAttr = getSearchAttribute(2);
			startDateAttr.setValues(new String[] { startDate });

			SearchAttribute endDateAttr = getSearchAttribute(3);
			endDateAttr.setValues(new String[] { endDate });

		} catch (ParseException e) {
			logError(e);
		}
	}
	
	public void generateProfileSubQuery(DynamoHttpServletRequest pRequest)
			throws RepositoryException {
		String rqlStatement = "";
		String firstName = pRequest.getParameter("firstName");
		String lastName = pRequest.getParameter("lastName");
		String phone = pRequest.getParameter("phone");
		String email = pRequest.getParameter("email");
		if(email.length()>0){
			rqlStatement = "paymentGroups" + " includes item ( "
					+ "email" + " CONTAINS IGNORECASE \""+ email.trim() 
					+ "\" )";
		}
		if(phone.length()>0){
			rqlStatement = "shippingGroups" + " includes item ( "
					+ "phoneNumber" + " CONTAINS IGNORECASE \""+ phone.trim() 
					+ "\" )";
		}
		if (firstName.length() >0 || lastName.length() >0){			
			rqlStatement = "shippingGroups" + " includes item ( ";
			if(firstName.length() >0){
				rqlStatement=rqlStatement+"firstName" + " CONTAINS IGNORECASE \""+ firstName.trim()+ "\"" ;
			}
			if(lastName.length() >0){
				if(rqlStatement.contains("firstName")){
					rqlStatement=rqlStatement+"AND lastName" + " CONTAINS IGNORECASE \""+ lastName.trim()+ "\" ";
				}else{
					rqlStatement=rqlStatement+"lastName" + " CONTAINS IGNORECASE \""+ lastName.trim()+ "\"" ;
				}					
			}
			rqlStatement=rqlStatement+" )";
//			rqlStatement = "shippingGroups" + " includes item ( "
//					+ "firstName" + " CONTAINS IGNORECASE \""+ firstName.trim() 
//					+ "\" )";			
			
		}
		
		if (rqlStatement.length() >0){
			RepositoryView searchView = getRepositorySearcher().getRepositoryView(getRepository(),
					getRepositoryView().getViewName());
			QueryBuilder qb = getRepositorySearcher().getQueryBuilder(searchView);
			
			if (isLoggingDebug()) {
				logDebug("rqlStatement : '" + rqlStatement + "'; ");
			}
			RqlStatement statement = RqlStatement.parseRqlStatement(rqlStatement);
			RqlQuery rqlQuery = statement.getQuery();
			setQuery(rqlQuery.createQuery(qb, searchView, null));
		}	
		
	}
	

	public int getSearchableAttributeNum() {
		return mSearchableAttributeNum;
	}

	public void setRepositorySearcher(RepositorySearcher pRepositorySearcher) {
		mRepositorySearcher = pRepositorySearcher;
	}

	public RepositorySearcher getRepositorySearcher() {
		return mRepositorySearcher;
	}

	public CSREnvironmentTools getCSREnvironmentTools() {
		return mCSREnvironmentTools;
	}

	public void setCSREnvironmentTools(CSREnvironmentTools pCSREnvironmentTools) {
		mCSREnvironmentTools = pCSREnvironmentTools;
	}

	public Query getQuery() {
		return mQuery;
	}

	public void setQuery(Query pQuery) {
		this.mQuery = pQuery;
	}
}
