<%--
 A page fragment that displays when no records are found in the result

 @param contentItemMap - The map of content items
 @param resultsListContentItem - The Endeca result list content item
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayNoResult.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">  
  <div class="results-none">
    <fmt:message key="endeca.noResults"/>
  </div>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayNoResult.jsp#1 $$Change: 946917 $--%>
