<%--
 A page fragment that displays a collection of objects in a content item 

 @param resultsListContentItem - The  content item containing the collection

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayCollectionAsList.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:importbean bean="/atg/commerce/custsvc/catalog/endeca/ContentItemResultsDroplet"/>
<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean var="resultContentTitle" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/DisplayResultContentTitle"/>


<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">


<dsp:getvalueof param="showTitle" var="showTitle"/>
<dsp:getvalueof param="contentItem" var="contentItem"/>
<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>


<dsp:droplet name="ContentItemResultsDroplet">
  <dsp:param name="contentItem" value="${contentItem}" />
  <dsp:oparam name="outputStart">
  
	<c:if test="${showTitle}">
		<dsp:include src="${resultContentTitle.URL}" otherContext="${resultContentTitle.servletContext}">
		<dsp:param name="contentItem" value="${contentItem}"/>
		<dsp:param name="contentItemMap" value="${contentItemMap}"/>
		</dsp:include>
	</c:if>

    <div class="table"> 
  </dsp:oparam>
  <dsp:oparam name="outputEnd">
    </div > 
  </dsp:oparam>
  <dsp:oparam name="output">
    <dsp:getvalueof var="resultObject" param="element"/>
    <dsp:getvalueof var="renderingPageFragment" param="renderingPageFragment"/>
    <dsp:getvalueof var="objectType" param="objectType"/>
  
     <div class="row">
      <dsp:include src="${renderingPageFragment.URL}" otherContext="${renderingPageFragment.servletContext}">
      <dsp:param name="resultObject" value="${resultObject}"/>
      <dsp:param name="contentItem" value="${resultsListContentItem}"/>
      <dsp:param name="objectType" value="${objectType}"/>
      </dsp:include>
    </div> <%-- end row div --%>
      
  </dsp:oparam>
  </dsp:droplet>
</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayCollectionAsList.jsp#1 $$Change: 946917 $--%>
