package com.mff.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class EndecaResultPagination extends DynamoServlet {
	
	private static final String OUTPUT = "output";
	private static final String ERROR = "error";
	private static final String FIRSTRECNUM = "firstRecNum";
	private static final String LASTRECNUM = "lastRecNum";
	private static final String RECSPERPAGE = "recsPerPage";
	private static final String TOTALNUMRECS = "totalNumRecs";
	private static final String TOTALPAGES = "totalPages";
	private static final String CURRENTPAGE = "currentPage";
	private static final String MESSAGE = "message";
	private static final String BEGININDEX = "beginIndex";
	private static final String ENDINDEX = "endIndex";
	
	
	public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		String lLastRecNum = pRequest.getParameter(LASTRECNUM);
		String lTotalNumRecs = pRequest.getParameter(TOTALNUMRECS);
		String lRecsPerPage = pRequest.getParameter(RECSPERPAGE);
		if(StringUtils.isEmpty(lLastRecNum) || StringUtils.isEmpty(lLastRecNum) || StringUtils.isEmpty(lLastRecNum)){
			pRequest.setParameter(MESSAGE, "required parameters are empty");
			pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
			return;
		}
		try{
			Integer lLastRecNumInt = Integer.valueOf(lLastRecNum);
			Integer lTotalNumRecsInt = Integer.valueOf(lTotalNumRecs);
			Integer lRecsPerPageInt = Integer.valueOf(lRecsPerPage);
			
			Integer lCurrentPage = 0;
			Integer lTotalPages = 0;
			Integer lBeginIndex = 0;
			Integer lEndIndex = 0;
			
			Integer lTemp = lLastRecNumInt/lRecsPerPageInt;
			if( lTemp * lRecsPerPageInt < lLastRecNumInt ){
				lCurrentPage = lTemp + 1;
			}else{
				lCurrentPage = lTemp;
			}
			
			lTemp = lTotalNumRecsInt/lRecsPerPageInt;
			if( lTemp * lRecsPerPageInt < lTotalNumRecsInt ){
				lTotalPages = lTemp + 1;
			}else{
				lTotalPages = lTemp;
			}
			
			if(lTotalPages <=5){
				lBeginIndex=1;
				lEndIndex=lTotalPages;
			}else{
				if(lCurrentPage-2 <= 1 ){
					lBeginIndex=1;
					lEndIndex=5;
				}else if(lCurrentPage+2 >= lTotalPages){
					lEndIndex=lTotalPages;
					lBeginIndex=lTotalPages-4;
				}else{
					lBeginIndex=lCurrentPage-2;
					lEndIndex=lCurrentPage+2;
				}
			}
			
			pRequest.setParameter(ENDINDEX, lEndIndex);
			pRequest.setParameter(BEGININDEX, lBeginIndex);
			pRequest.setParameter(TOTALPAGES, lTotalPages);
			pRequest.setParameter(CURRENTPAGE, lCurrentPage);
			pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
		}catch (NumberFormatException pNumberFormatException){
			pRequest.setParameter(MESSAGE, pNumberFormatException.getMessage());
			pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
			return;
		}
		
	} 
	 

}
