<%--
 This page defines the order search results panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/searchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <dsp:importbean bean="/atg/commerce/custsvc/order/OrderSearchUIConfiguration" var="tableConfig"/>
    <dsp:importbean var="formHandler"
      bean="/atg/commerce/custsvc/order/OrderSearchTreeQueryFormHandler" />
    <dsp:importbean var="agentSearchRequestTracker" bean="/atg/commerce/custsvc/order/AgentOrderSearchRequestTracker" />
    <dsp:getvalueof var="searchResponse" value="${formHandler.searchResponse}" />
      
    <dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
    <dsp:setvalue bean="OriginatingPage.pageName" value="orderSearch"/>
    
    <div class="atg_commerce_csr_content">
    
    <c:if test="${null == searchResponse}">
      <c:set var="searchResponse" value="${agentSearchRequestTracker.searchResponse}" /> 
      <dsp:setvalue bean="/atg/commerce/custsvc/order/OrderSearchTreeQueryFormHandler.searchResponse" value="${searchResponse}"/>
    </c:if>
    
    <dsp:getvalueof var="items" value="${searchResponse.items}" />
    
    <c:choose>

      <c:when test="${null != searchResponse}">
      <svc-agent:arrayListSize array="items" param="size" />
        <c:choose>
          <c:when test="${size != 0}">

            <%-- Include Paging Controls JSP --%>
            <dsp:include
              src="${CSRConfigurator.contextRoot}/panels/order/searchResultsPaging.jsp">
              <dsp:param name="formHandler" value="${formHandler}" />
              <dsp:param name="searchResponse"
                value="${searchResponse}" />
            </dsp:include>

            <dsp:include src="${tableConfig.tablePage.URL}"
              otherContext="${tableConfig.tablePage.servletContext}">
              <dsp:param name="tableConfig" value="${tableConfig}" />
              <dsp:param name="searchResponse"
                value="${searchResponse}" />
            </dsp:include>
          </c:when>

          <c:otherwise>
            <fmt:message key='no-matching-orders' />
          </c:otherwise>
        </c:choose>

      </c:when>

      <c:otherwise>
        <fmt:message key='no-search' />
      </c:otherwise>

    </c:choose></div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/searchResults.jsp#1 $$Change: 946917 $--%>
