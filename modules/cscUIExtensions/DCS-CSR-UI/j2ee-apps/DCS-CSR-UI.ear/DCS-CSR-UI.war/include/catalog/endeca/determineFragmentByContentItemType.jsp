<%--
 A page fragment that displays a content item in the result area. It does this by identify which page fragment to use for the content item. 

 @param contentItemResult - The Endeca search result
 @param contentItemMap - The map of endeca content items by type
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/determineFragmentByContentItemType.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>

<dsp:getvalueof param="contentItem" var="contentItem"/>
<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>

<c:set var="type" value="${contentItem['@type']}"/>
<c:set var="contentItemFragment" value="${endecaConfig.resultContentItemPageFragments[type]}"/>

<c:if test="${empty contentItemFragment}">
  <c:set var="contentItemFragment" value="${endecaConfig.defaultResultContentItemPageFragment}"/>
</c:if>
   
<dsp:include src="${contentItemFragment.URL}" otherContext="${contentItemFragment.servletContext}">
  <dsp:param name="contentItem" value="${contentItem}"/>
  <dsp:param name="contentItemMap" value="${contentItemMap}"/>
</dsp:include>


</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/determineFragmentByContentItemType.jsp#1 $$Change: 946917 $--%>
