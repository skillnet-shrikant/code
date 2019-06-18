<%--
Approval JSON Object Creation
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/approvalsDataModel.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dsp:page>

  <dsp:getvalueof var="gridConfigPath" param="gp" scope="request"/>
  
  <dsp:importbean var="gridConfig" bean="${gridConfigPath}"/>
  
  <dsp:importbean var="formHandler" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalRepositoryQueryFormHandler"/>
  
  <dsp:importbean var="agentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools" />
  
  
  
  
{
  "resultLength":${formHandler.totalItemCount},
  "currentPage":${formHandler.currentPage},
  "results":[<c:forEach var="approvalItem" items="${formHandler.searchResults}" varStatus="status">
  
  <dsp:tomap var="approvalItemMap" value="${approvalItem}"/>
    
  
  <dsp:droplet name="/atg/targeting/RepositoryLookup">
    <dsp:param bean="/atg/userprofiling/InternalProfileRepository"
    name="repository"/>
    <dsp:param name="elementName" value="agentProfile"/>
    <dsp:param name="id" value="${approvalItemMap.agentId}"/>
    <dsp:oparam name="output">
      <dsp:tomap var="agentItemMap" param="agentProfile"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:droplet name="/atg/targeting/RepositoryLookup">
    <dsp:param bean="/atg/multisite/SiteRepository"
    name="repository"/>
    <dsp:param name="elementName" value="siteConfig"/>
    <dsp:param name="id" value="${approvalItemMap.siteId}"/>
    <dsp:oparam name="output">
      <dsp:tomap var="siteConfigMap" param="siteConfig"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:droplet name="/atg/targeting/RepositoryLookup">
    <dsp:param bean="/atg/commerce/order/OrderRepository"
    name="repository"/>
    <dsp:param name="elementName" value="orderItem"/>
    <dsp:param name="id" value="${approvalItemMap.orderId}"/>
    <dsp:oparam name="output">
      <dsp:tomap var="order" param="orderItem" recursive="true"/>

      <dsp:droplet name="/atg/commerce/custsvc/order/GetOrderProfile">
      <dsp:param name="orderItem" param="orderItem"/>
      <dsp:oparam name="output">
        <dsp:tomap var="profileItemMap" param="orderProfile"/>
      </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>

  <c:choose>
	<c:when test="${not empty order}">
		<c:choose>
			<c:when test="${not empty order.priceInfo.currencyCode}">
    <c:set var="currencyCode" value="${order.priceInfo.currencyCode}" />
			</c:when>
			<c:otherwise>
     <c:set var="currencyCode" value="${agentTools.activeCustomerCurrencyCode}" />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="currencyCode"	value="${agentTools.activeCustomerCurrencyCode}" />
	</c:otherwise>
</c:choose>
          
  ${status.index > 0 ? ',' : ''}
    {
      <c:set var="isComma" value="${false}"/><c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus">
        <c:if test="${!empty fn:trim(column.field) && !empty fn:trim(column.dataRendererPage)}">${isComma ? ',' : '' }
          <dsp:include src="${fn:trim(column.dataRendererPage.URL)}" otherContext="${fn:trim(column.dataRendererPage.servletContext)}">
            <dsp:param name="field" value="${fn:trim(column.field)}"/>
            <dsp:param name="colIndex" value="${colStatus.index}"/>
            <dsp:param name="approvalItemMap" value="${approvalItemMap}"/>
            <dsp:param name="repositoryId" value="${approvalItem.repositoryId}"/>
            <dsp:param name="profileItemMap" value="${profileItemMap}"/>
            <dsp:param name="agentItemMap" value="${agentItemMap}"/>
            <dsp:param name="siteConfigMap" value="${siteConfigMap}"/>
            <dsp:param name="currencyCode" value="${currencyCode}"/>
          </dsp:include><c:set var="isComma" value="${true}"/>
        </c:if>
      </c:forEach><c:remove var="isComma"/>
    }</c:forEach>
  ]}
</dsp:page>

<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/approvalsDataModel.jsp#1 $$Change: 946917 $--%>
