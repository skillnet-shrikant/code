<%--
 A page fragment that displays the results area of the display. It renders the alternate result content items as well as the ResultList content item

 @param contentItemMap - The map of content items
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean var="noResultDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/NoResult"/>

<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<c:set var="isAlternateContent" value="${false}"/>
<c:set var="isResultList" value="${false}"/>

<%-- get the list of content items defined by the result list content item type in configuration --%>
<c:set var="resultsList" value="${contentItemMap[endecaConfig.resultsListContentItemType]}"/>
<c:if test="${!empty resultsList}">
  <dsp:getvalueof value="${resultsList[0]}" var="resultList"/>
  
  <c:if test="${resultList['totalNumRecs'] > 0}">
    <dsp:include src="/include/catalog/endeca/determineFragmentByContentItemType.jsp">
    <dsp:param name="contentItem" value="${resultList}"/>
    <dsp:param name="contentItemMap" value="${contentItemMap}"/>
    </dsp:include>
    <c:set var="isResultList" value="${true}"/>
  </c:if>
 
</c:if>


<%-- this loops over the alternate content item types and renders their Collections --%>
<c:forEach items="${endecaConfig.alternateResultContentItemTypes}" var="type">
  <c:if test="${!empty contentItemMap[type]}">
    
    <dsp:include src="/include/catalog/endeca/determineFragmentByContentItemType.jsp">
    <dsp:param name="contentItem" value="${contentItemMap[type][0]}"/>
    <dsp:param name="contentItemMap" value="${contentItemMap}"/>
    </dsp:include>
    <c:set var="isAlternateContent" value="${true}"/>
  </c:if>
</c:forEach>


<%-- display no results when no alternate content and no result list --%>
<c:if test="${!isAlternateContent && !isResultList}">
  
  <dsp:include src="${noResultDisplayFragment.URL}" otherContext="${noResultDisplayFragment.servletContext}">
  <dsp:param name="contentItemMap" value="${contentItemMap}"/>
  <dsp:param name="resultsListContentItem" value="${resultList}"/>
  </dsp:include>
</c:if>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResults.jsp#1 $$Change: 946917 $--%>
