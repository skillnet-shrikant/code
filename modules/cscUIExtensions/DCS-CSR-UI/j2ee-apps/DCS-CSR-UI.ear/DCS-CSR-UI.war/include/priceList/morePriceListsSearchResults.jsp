<%--
 This page encodes the search results as JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/priceList/morePriceListsSearchResults.jsp#1 $
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

<dsp:importbean bean="/atg/commerce/custsvc/pricing/priceLists/MorePriceListsSearch" var="morePriceListsSearch"/>
<dsp:importbean bean="/atg/commerce/custsvc/environment/ChangeCatalogAndPriceList" var="changeCatalogAndPriceList"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
<dsp:importbean bean="/atg/userprofiling/Profile" var="profile"/>
<dsp:tomap var="profile" value="${profile}"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <c:set var="currentSalePriceListName" value=""/>
  <fmt:message key="menu.priceList.salePriceListLabel" var="salePriceListLabel"/>
  <fmt:message key="menu.priceList.priceListLabel" var="priceListLabel"/>
  <c:set var="currentPriceList" value="${profile.priceList}"/>
  <c:if test="${!empty currentPriceList}">
    <dsp:tomap value="${currentPriceList}" var="currentPriceList"/>
    <c:set var="currentPriceListName" value="${currentPriceList.displayName}"/>
  </c:if>
  <c:set var="currentSalePriceList" value="${profile.salePriceList}"/>
  <c:if test="${CSRConfigurator.usingSalePriceLists && !empty currentSalePriceList}">
    <c:if test="${!empty currentSalePriceList}">
      <dsp:tomap value="${currentSalePriceList}" var="currentSalePriceList"/>
      <c:set var="currentSalePriceListName" value="${currentSalePriceList.displayName}"/>
    </c:if>
  </c:if>
  <c:choose>
    <c:when test="${morePriceListsSearch.formError}">
      {
      resultLength: 0,
       results: []
      }
    </c:when>
    <c:otherwise>
      <c:set var="resultSetSize" value="${morePriceListsSearch.resultSetSize}"/>

      {
      resultLength: ${resultSetSize},
       results: [
        <c:forEach items="${morePriceListsSearch.searchResults}" var="searchResult" varStatus="status">
          <dsp:tomap value="${searchResult}" var="searchResult"/>
          <fmt:message key="global.morePriceLists.priceList" var="priceListSelect"/>
          <fmt:message key="global.morePriceLists.saleList" var="saleListSelect"/>
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

            <c:choose>
              <c:when test="${!empty searchResult.description}">
                description: '<c:out value="${searchResult.description}"/>',
              </c:when>
              <c:otherwise>
                description: '&nbsp;',
              </c:otherwise>
            </c:choose>
            <c:choose>
              <c:when test="${CSRConfigurator.usingSalePriceLists}">
                select: '<a onclick="atg.commerce.csr.pricing.priceLists.selectPriceList(\'setPriceListForm\',\'${fn:escapeXml(searchResult.id)}\');return false;" href="#">${fn:escapeXml(priceListSelect)}</a>  <fmt:message key="text.delimiter"/> <a onclick="atg.commerce.csr.pricing.priceLists.selectPriceList(\'setSalePriceListForm\',\'${fn:escapeXml(searchResult.id)}\');return false;" href="#">${fn:escapeXml(saleListSelect)}</a>'
              </c:when>
              <c:otherwise>
                select: '<a onclick="atg.commerce.csr.pricing.priceLists.selectPriceList(\'setPriceListForm\',\'${fn:escapeXml(searchResult.id)}\');return false;" href="#">${fn:escapeXml(priceListSelect)}</a>'
              </c:otherwise>
            </c:choose>
          <%--select: ['<c:out value="${searchResult.id}"/>', '<fmt:message key="menu.priceList"/><fmt:message key="text.colon"/> <c:out value="${searchResult.displayName}"/>', '<fmt:message key="global.morePriceLists.select"/>']--%>
          }
          <c:if test="${!status.last}">,</c:if>
        </c:forEach>
       ]
      }
    </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/priceList/morePriceListsSearchResults.jsp#1 $$Change: 946917 $--%>
