<%--
 Customer Order History JSON Object Creation
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderData.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dsp:page>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:getvalueof var="formPath" param="fhp"/>
<dsp:importbean var="formHandler" bean="${formPath}"/>



<json:object prettyPrint="${UIConfig.prettyPrintResponses}">

  <json:property name="resultLength" value="${formHandler.totalItemCount}"/>
  <json:property name="currentPage" value="${formHandler.currentPage}"/>
  <json:array name="results" items="${formHandler.searchResults}" var="resultWrapper">
    <dsp:include src="/panels/order/orderDataItem.jsp" otherContext="${CSRConfigurator.contextRoot}">
    <dsp:param name="orderRepositoryItem" value="${resultWrapper.repositoryItem}"/>
    </dsp:include>
  </json:array>

</json:object>
</dsp:layeredBundle>
</dsp:page>

<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderData.jsp#1 $$Change: 946917 $--%>
