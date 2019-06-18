package com.mff.droplet;

import java.util.ArrayList;
import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class MFFPaginationDroplet extends DynamoServlet {

	private int mRecordsPerPage = 5;
	private int mDefaultPageNumber = 1;

	private static final String OUTPUT = "output";
	private static final String RETURN_LIST = "filteredItems";
	
	private static final String PER_PAGE = "perPage";
	private static final String PAGE_NO = "pageNumber";
	private static final String CONST_CURRENT_PAGE = "currentPage";
	
	private static final String CONST_NO_OF_PAGES = "noOfPages";
	private static final String EMPTY = "empty";


	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
					throws javax.servlet.ServletException, java.io.IOException {
		
		vlogDebug("service called.");

		int pageNo = getDefaultPageNumber();
		int perPage = getRecordsPerPage();
				
		List inputList;
		List pagedOutputList = new ArrayList();
		try {
			if(pRequest.getLocalParameter(PAGE_NO) !=null){
				pageNo = Integer.parseInt((String) pRequest.getLocalParameter(PAGE_NO));
			}

			if(pRequest.getLocalParameter(PER_PAGE) !=null){
				perPage = Integer.parseInt((String) pRequest.getLocalParameter(PER_PAGE));
			}
			
		} catch (NumberFormatException e) {
			logError("Issue with pRequest parameters page No" + pageNo + "and per page" + perPage);
		}
		
		inputList = (List) pRequest.getObjectParameter("items");
		
		if(inputList != null && inputList.size() > 0){
			
			int forStart=((pageNo-1)*perPage);
			int forEnd=((pageNo*perPage)-1);

			for(int i=forStart;i<=forEnd;i++){

				if(!(i>=inputList.size())){
					pagedOutputList.add(inputList.get(i));
				}
			}

			long pageCount = (long)Math.ceil((double)inputList.size()/perPage);
			
				vlogDebug("input list size : " + inputList.size());
				vlogDebug("perPage : " + perPage);
				vlogDebug("Page Count : " + pageCount);
			
			pRequest.setParameter(CONST_NO_OF_PAGES, pageCount);
			pRequest.setParameter(RETURN_LIST, pagedOutputList);
			pRequest.setParameter(CONST_CURRENT_PAGE, pageNo);
			pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
		} else {
			pRequest.serviceParameter(EMPTY, pRequest, pResponse);
		}

	}
	
	public int getRecordsPerPage() {
		return mRecordsPerPage;
	}

	public void setRecordsPerPage(int pRecordsPerPage) {
		this.mRecordsPerPage = pRecordsPerPage;
	}

	public int getDefaultPageNumber() {
		return mDefaultPageNumber;
	}

	public void setDefaultPageNumber(int pDefaultPageNumber) {
		this.mDefaultPageNumber = pDefaultPageNumber;
	}

}
