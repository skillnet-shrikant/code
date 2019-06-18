<%--
 A page fragment that displays the ResultsList ContentItem. 

 @param contentItem - The ResultList content item
 @param contentItemMap - The Map of all content items in the result

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultListContentItem.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:importbean var="resultsPaging" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/ResultsPaging"/>
<dsp:importbean var="sortOptionsDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/ResultsSortOptions"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">


<dsp:getvalueof param="contentItem" var="resultList"/>
<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>
  <div class="results-header">

    <%-- having this if test prevents this from showing up when the resultListMap does not have a totalNumRecs property --%>
    <c:if test="${resultList['totalNumRecs'] > 0}">
      <div class="items">
        <span><c:out value="${resultList.totalNumRecs}"/></span>&nbsp;<fmt:message key="endeca.numberOfItems"/>
      </div>
    </c:if>

    <%-- having this if test prevents the sort options fragment from showing up when the resultListMap does not have a sortOptions property --%>
    <c:if test="${! empty resultList['sortOptions']}">
      <div class="sort">
        <dsp:include src="${sortOptionsDisplayFragment.URL}" otherContext="${sortOptionsDisplayFragment.servletContext}">
        <dsp:param name="contentItemMap" value="${contentItemMap}"/>
        <dsp:param name="resultsListContentItem" value="${resultList}"/>
        </dsp:include>
      </div>
    </c:if>

    <%-- having this if test prevents the page control fragment from showing up when the resultListMap does not have the necessary  properties  --%>
    <c:if test="${! empty resultList['firstRecNum'] && ! empty resultList['totalNumRecs']&& ! empty resultList['recsPerPage']}">
      <div class="pagination">
         <dsp:include src="${resultsPaging.URL}" otherContext="${resultsPaging.servletContext}">
         <dsp:param name="contentItemMap" value="${contentItemMap}"/>
         <dsp:param name="resultsListContentItem" value="${resultList}"/>
         </dsp:include>
      </div>
    </c:if>

  </div>
<dsp:include src="/include/catalog/endeca/displayCollectionAsList.jsp">
<dsp:param name="contentItemMap" value="${contentItemMap}"/>
<dsp:param name="contentItem" value="${resultList}"/>
</dsp:include>
<%--
--%>
  
  <%-- bottom pagination --%>
  <div class="results-header">
    <%-- having this if test prevents the page control fragment from showing up when the resultListMap does not have the necessary  properties  --%>
    <c:if test="${! empty resultList['firstRecNum'] && ! empty resultList['totalNumRecs']&& ! empty resultList['recsPerPage']}">
      <div class="pagination">
         <dsp:include src="${resultsPaging.URL}" otherContext="${resultsPaging.servletContext}">
         <dsp:param name="contentItemMap" value="${contentItemMap}"/>
         <dsp:param name="resultsListContentItem" value="${resultList}"/>
         </dsp:include>
      </div>
    </c:if>
  </div>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultListContentItem.jsp#1 $$Change: 946917 $--%>
