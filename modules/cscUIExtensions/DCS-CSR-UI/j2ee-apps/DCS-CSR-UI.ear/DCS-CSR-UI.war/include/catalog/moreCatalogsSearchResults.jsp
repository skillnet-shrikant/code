<%--
 This page encodes the search results as JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/moreCatalogsSearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ page errorPage="/error.jsp" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csr" tagdir="/WEB-INF/tags"  %>

<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"%>
<dsp:page>

<dsp:importbean bean="/atg/commerce/custsvc/catalog/MoreCatalogsSearch" var="moreCatalogsSearch"/>
<dsp:importbean bean="/atg/commerce/custsvc/environment/ChangeCatalogAndPriceList" var="changeCatalogAndPriceList"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <c:set var="catalogDisplayName">
    <fmt:message key="menu.catalog"/><fmt:message key="text.colon"/>
  </c:set>
  <c:choose>
    <c:when test="${moreCatalogsSearch.formError}">
      {
      resultLength: 0,
       results: []
      }
    </c:when>
    <c:otherwise>
      <c:set var="resultSetSize" value="${moreCatalogsSearch.resultSetSize}"/>

      {
      resultLength: ${resultSetSize},
       results: [
        <c:forEach items="${moreCatalogsSearch.searchResults}" var="searchResult" varStatus="status">
          <dsp:tomap value="${searchResult}" var="searchResult"/>
          <fmt:message key="global.moreCatalogs.select" var="selectLabel"/>
          {
            displayName: '<c:out value="${searchResult.displayName}"/>',
            <c:choose>
              <c:when test="${!empty searchResult.lastModifiedDate}">
                creationDate: '<web-ui:formatDate value="${searchResult.creationDate}" type="date" dateStyle="short"/>',
              </c:when>
              <c:otherwise>
                creationDate: '&nbsp;',
              </c:otherwise>
            </c:choose>
            <c:set var="isPrompted" value="${((changeCatalogAndPriceList.doWarnings && changeCatalogAndPriceList.environmentChangeState.warnings) || changeCatalogAndPriceList.showDispositionPrompt) && !empty changeCatalogAndPriceList.confirmURL}"/>
            select: '<a onclick="atg.commerce.csr.catalog.selectCatalog(\'${fn:escapeXml(searchResult.id)}\'); return false;" href="#">${fn:escapeXml(selectLabel)}</a>'
            <%--select: ['<c:out value="${searchResult.id}"/>', '<fmt:message key="menu.catalog"/><fmt:message key="text.colon"/> <c:out value="${searchResult.displayName}"/>', '<fmt:message key="global.moreCatalogs.select"/>']--%>
          }
          <c:if test="${!status.last}">,</c:if>
        </c:forEach>
       ]
      }
  </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/moreCatalogsSearchResults.jsp#1 $$Change: 946917 $--%>
