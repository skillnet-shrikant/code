<%--
 A page fragment that displays a collection of objects in a content item as a table with each row containing an object. A title is also display,
 if configured. 

 @param resultsListContentItem - The  content item containing the collection

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayCollectionWithTitle.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>

<dsp:getvalueof param="contentItem" var="contentItem"/>
<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>


<dsp:include src="/include/catalog/endeca/displayCollectionAsList.jsp">
<dsp:param name="contentItem" value="${contentItem}"/>
<dsp:param name="contentItemMap" value="${contentItemMap}"/>
<dsp:param name="showTitle" value="${true}"/>
</dsp:include>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayCollectionWithTitle.jsp#1 $$Change: 946917 $--%>
