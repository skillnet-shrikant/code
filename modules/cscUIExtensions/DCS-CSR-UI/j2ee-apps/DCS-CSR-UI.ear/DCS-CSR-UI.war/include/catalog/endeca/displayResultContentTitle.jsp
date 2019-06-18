<%--
 A page fragment that displays a collection of objects in a content item 

 @param contentItem - The  content item containing the collection

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultContentTitle.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean var="displayEndecaResourcedValueFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/DisplayEndecaResourcedValue"/>

<dsp:getvalueof param="contentItem" var="contentItem"/>
<div class="results-title">

  <c:set var="titleMap" value="${endecaConfig.resultContentItemTitlePropertyNames}"/>
  <c:set var="titlePropertyName" value="${titleMap[contentItem['@type']]}"/>
  <dsp:include src="${displayEndecaResourcedValueFragment.URL}" otherContext="${displayEndecaResourcedValueFragment.servletContext}">
   <dsp:param name="propertyName" value="${titlePropertyName}"/>
   <dsp:param name="contentItem" value="${contentItem}"/>
  </dsp:include>
</div>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultContentTitle.jsp#1 $$Change: 946917 $--%>
