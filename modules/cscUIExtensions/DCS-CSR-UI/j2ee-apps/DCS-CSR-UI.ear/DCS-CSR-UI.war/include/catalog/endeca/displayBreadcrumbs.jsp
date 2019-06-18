<%--
 A page fragment that displays the endeca breadcrumbs

 @param contentItemResult - The Endeca search result
 @param contentItemMap - The map of endeca content items by type
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayBreadcrumbs.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">


<dsp:getvalueof param="contentItemResult" var="contentItemResult"/>
<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>

<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean var="refinementCrumbDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/RefinementCrumb"/>
<dsp:importbean var="searchCrumbDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/SearchCrumb"/>
<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>

<c:set var="breadcrumbsList" value="${contentItemMap[endecaConfig.breadcrumbContentItemType]}"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<!-- display all search crumbs first -->
<c:forEach items="${breadcrumbsList}" var="breadCrumb">
  
  
  <c:set var="searchCrumbs" value="${breadCrumb['searchCrumbs']}"/>
    <c:forEach items="${searchCrumbs}" var="searchCrumb">
      <li class="refinement">
        <dsp:include src="${searchCrumbDisplayFragment.URL}" otherContext="${searchCrumbDisplayFragment.servletContext}">
          <dsp:param name="searchCrumb" value="${searchCrumb}"/>
          <dsp:param name="breadCrumb" value="${breadCrumb}"/>
          <dsp:param name="contentItemMap" value="${contentItemMap}"/>
          <dsp:param name="contentItemResult" value="${contentItemResult}"/>
        </dsp:include>
      </li>
    </c:forEach>
    
    
</c:forEach>

<!-- display all refinement crumbs -->
<c:forEach items="${breadcrumbsList}" var="breadCrumb">
  <c:set var="refinementCrumbs" value="${breadCrumb['refinementCrumbs']}"/>

  <c:forEach items="${refinementCrumbs}" var="refinementCrumb">
    <dsp:include src="${refinementCrumbDisplayFragment.URL}" otherContext="${refinementCrumbDisplayFragment.servletContext}">
      <dsp:param name="refinementCrumb" value="${refinementCrumb}"/>
      <dsp:param name="breadCrumb" value="${breadCrumb}"/>
      <dsp:param name="contentItemMap" value="${contentItemMap}"/>
      <dsp:param name="contentItemResult" value="${contentItemResult}"/>
    </dsp:include>
  </c:forEach>
</c:forEach>


<c:if test="${not empty searchCrumbs or not empty refinementCrumbs}">
  <li class="refinement right">
    <dsp:droplet name="ContentRequestURLDroplet">
      <dsp:param name="contentPath" value="/browse"/> <%-- TODO: put here url from configuration instead of hardcoded '/browse' value --%>
      <dsp:oparam name="output">
        <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
        <a style="white-space:nowrap;" href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;" title="Clear all breadcrumbs crumbs"><fmt:message key="endeca.clearAll"/></a>
      </dsp:oparam>
    </dsp:droplet>
  </li>
</c:if>

 </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayBreadcrumbs.jsp#1 $$Change: 946917 $--%>
