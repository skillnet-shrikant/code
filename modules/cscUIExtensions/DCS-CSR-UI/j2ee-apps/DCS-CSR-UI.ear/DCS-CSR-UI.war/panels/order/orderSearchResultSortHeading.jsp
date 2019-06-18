<%--
 Displays a single field heading

 resourceBundle - The resource file
 resourceKey - The key to that maps to the resource string
 fieldName - The name of the field to be sorted

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderSearchResultSortHeading.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>

<dsp:page xml="true">

  <dsp:getvalueof var="resourceBundle" param="resourceBundle" />
  <dsp:getvalueof var="resourceKey" param="resourceKey" />
  <dsp:getvalueof var="fieldName" param="fieldName" />
  <dsp:importbean var="orderFormHandler" bean="/atg/commerce/custsvc/order/OrderSearchTreeQueryFormHandler"/>
  <dsp:getvalueof var="orderSearchResponse" value="${orderFormHandler.searchResponse}"/>
  <dsp:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:layeredBundle basename="${resourceBundle}">
  <c:choose>
  
    <c:when test="${fn:toLowerCase(orderSearchResponse.docSortProp) eq fn:toLowerCase(fieldName)}">
  
    <a href="#" id="atg_service_customer_search${fieldName}Sort" onclick="atg.commerce.csr.order.handleSort('${fieldName}');return false;"><fmt:message key="${resourceKey}"/></a>
    
    <c:choose>
    
      <c:when test="${orderSearchResponse.docSortOrder eq 'ascending'}">
      
        <img src="${UIConfig.contextRoot}/images/sortArrowUp.gif">
      
      </c:when>
      
      <c:otherwise>
      
        <img src="${UIConfig.contextRoot}/images/sortArrowDown.gif">
      
      </c:otherwise>
    
    </c:choose>

    </c:when>
    
    <c:otherwise>
    
    <a  href="#" id="atg_commerce_csr_order_search${fieldName}Sort" onclick="atg.commerce.csr.order.handleSort('${fieldName}');return false;"><fmt:message key="${resourceKey}"/></a>

    
    </c:otherwise>
    
  </c:choose>

  </dsp:layeredBundle>
    
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderSearchResultSortHeading.jsp#1 $$Change: 946917 $--%>