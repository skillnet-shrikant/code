<%--
 This page defines the paging controls and paging functionality for the customer search results
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/searchResultsPaging.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>


<dspel:page>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <dspel:importbean bean="/atg/dynamo/droplet/For" />
    <dspel:importbean bean="/atg/dynamo/droplet/Switch" />
    <dspel:importbean bean="/Constants" />
    <dspel:importbean var="agentUIConfig"
      bean="/atg/svc/agent/ui/AgentUIConfiguration" />
    <dspel:importbean
      bean="/atg/svc/agent/ui/formhandlers/CustomerSearchTreeQueryFormHandler"
      scope="request" />
    <dspel:importbean var="customerProfileSearchUIConfiguration" bean="/atg/svc/agent/ui/CustomerProfileSearchUIConfiguration"/>
    <dspel:getvalueof param="formHandler" var="formHandler" />
    <dspel:getvalueof param="searchResponse" var="searchResponse" />
    <dspel:getvalueof var="items" value="${searchResponse.items}" />

    <svc-agent:arrayListSize array="items" param="size"/>

    <div  class="atg_pagination atg_tablePagination atg_topPagination">
    <c:if test="${ ! empty searchResponse }">
      <div class="atg_resultTotal">
      <c:choose>
        <c:when test="${searchResponse.timedOut}">
          <h2>The request timed out after <c:out
            value="${searchResponse.totalResponseTimeMs}" /> milliseconds</h2>
        </c:when>
        <c:when test="${searchResponse.groupCount>=0 and size>0}">
          <fmt:message key='customer.results.paging.matching'>
            <fmt:param>
              <c:out
                value="${searchResponse.pageNum*searchResponse.pageSize+1}" />
            </fmt:param>
            <fmt:param>
              <c:out
                value="${searchResponse.pageNum*searchResponse.pageSize+size}" />
            </fmt:param>
            <fmt:param>
              <c:out value="${searchResponse.groupCount}" />
            </fmt:param>
          </fmt:message>
        </c:when>
      </c:choose>
      </div>
    </c:if>

    <c:choose>
      <c:when test="${searchResponse.groupCount>customerProfileSearchUIConfiguration.rowsPerPage}">
		    <div class="atg_pagingControls">
		    <dspel:droplet name="Switch">
		      <dspel:param bean="CustomerSearchTreeQueryFormHandler.pagesAvailable"
		        name="value" />
		            <%-- Display Message if no pages of results exist --%> 
		      <dspel:oparam name="0">
		        <div class="atg_resultTotal" id="${progressNodeId}"><fmt:message
		          key='no-matching-customers' /></div>
		      </dspel:oparam>
		     <dspel:oparam name="1">
		        <%-- If only 1 page of results available, do not display any paging controls --%>
		      </dspel:oparam>
		      <dspel:oparam name="default"> 
		      
		            <%-- First Button paging controls --%> 
		            <c:choose>
		              <c:when test="${searchResponse.pageNum == 0}">
		                <a class="atg_icon atg_jumpToFirst atg_inactive" title="<fmt:message key='customer.results.paging.tooltip.first'/>"></a>
		                </c:when>
		              <c:otherwise>
		                <a class="atg_icon atg_jumpToFirst" href="#" title="<fmt:message key='customer.results.paging.tooltip.first'/>" onclick="return goToPage('1');"></a>
		              </c:otherwise>
		            </c:choose>
		            <%-- End First Button paging controls --%>   
		                  
		            <%-- Previous Button paging controls --%> 
		            <c:choose>
		              <c:when test="${searchResponse.pageNum == 0}">
		                <a class="atg_icon atg_pageBack atg_inactive" title="<fmt:message key='customer.results.paging.tooltip.previous'/>"></a>
		                </c:when>
		              <c:otherwise>
		                <a class="atg_icon atg_pageBack" href="#" title="<fmt:message key='customer.results.paging.tooltip.previous'/>" onclick="return goToPage('${searchResponse.pageNum}');"></a>
		              </c:otherwise>
		            </c:choose>
		            <%-- End Previous Button paging controls --%>          
		
		            <%-- Display Previous Page Number --%> 
		            <c:choose>
		              <c:when test="${searchResponse.pageNum != 0}">   
		                <a class="atg_commerce_csr_pagingLink" href="#" onclick="return goToPage('${searchResponse.pageNum}');"><c:out value="${searchResponse.pageNum}" />&nbsp;</a>             
		              </c:when>
		            </c:choose>
		            <%-- End Display Previous Page Number --%>              
		
		            <%-- Display Current Page --%>            
		            <span><c:out value="${searchResponse.pageNum+1}" />&nbsp;</span>
		            <%-- End Display Current Page --%>  
		            
		            <%-- Display Next Page Number --%> 
		            <c:choose>    
		             <c:when test="${searchResponse.pageNum+1 < formHandler.pagesAvailable}">
		                    <a class="atg_commerce_csr_pagingLink" href="#" onclick="return goToPage('${searchResponse.pageNum +2 }');"> 
		                  <c:out value="${searchResponse.pageNum+2}" /></a>
		             </c:when>               
		            </c:choose>
		            <%-- End Display Next Page Number --%> 
		            
		            <%-- Next Button paging controls --%>
		            <c:choose>
		              <c:when test="${searchResponse.pageNum+1 == formHandler.pagesAvailable}">
		                <a class="atg_icon atg_pageForward atg_inactive" href="#" title="<fmt:message key='customer.results.paging.tooltip.next'/>"></a>
		              </c:when>
		              <c:when test="${searchResponse.pageNum == 0 and searchResponse.groupCount == 0}">
		                <a class="atg_icon atg_pageForward atg_inactive" href="#" title="<fmt:message key='customer.results.paging.tooltip.next'/>"></a>
		              </c:when>
		              <c:when test="${searchResponse.pageNum == howmany}">
		                <a class="atg_icon atg_pageForward atg_inactive" href="#" title="<fmt:message key='customer.results.paging.tooltip.next'/>"></a>
		              </c:when>   
		              <c:otherwise>
		                <a class="atg_icon atg_pageForward" href="#" title="<fmt:message key='customer.results.paging.tooltip.next'/>"
		                  onclick="return goToPage('${searchResponse.pageNum +2 }');"></a>
		              </c:otherwise>                      
		            </c:choose>
		            <%-- End Next Button paging controls --%>
		      </dspel:oparam>
		    </dspel:droplet>
        </div>
		    </c:when>
	    </c:choose>
    </div>
  </dspel:layeredBundle>
</dspel:page> 
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/searchResultsPaging.jsp#1 $$Change: 946917 $--%>