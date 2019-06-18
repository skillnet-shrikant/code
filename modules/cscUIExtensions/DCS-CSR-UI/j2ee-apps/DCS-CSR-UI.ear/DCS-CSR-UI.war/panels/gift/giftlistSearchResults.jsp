<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistSearchResults.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean var="defaultPageFragment"
    bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistSearchResultsDefault" />
  <dsp:importbean var="extendedPageFragment"
    bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistSearchResultsExtended" />
    <c:if test="${not empty defaultPageFragment.URL}">
      <dsp:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
    </c:if>
    <c:if test="${not empty extendedPageFragment.URL}">
      <dsp:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
    </c:if>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistSearchResults.jsp#1 $$Change: 946917 $--%>