<%--
 This page defines the order results panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/encodeSearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page>

  <dsp:importbean var="orderSearchFormHandler" bean="/atg/commerce/custsvc/order/AdvOrderSearch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup"/>

  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
  <json:object prettyPrint="${UIConfig.prettyPrintResponses}">
    <json:property name="resultLength" value="${orderSearchFormHandler.resultSetSize}"/>
    <json:property name="currentPage" value="${orderSearchFormHandler.currentResultPageNum}"/>
    <json:array name="results" items="${orderSearchFormHandler.searchResults}" var="orderItem">
      <dsp:include src="/panels/order/orderDataItem.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="orderRepositoryItem" value="${orderItem}"/>
      </dsp:include>
    </json:array>
  </json:object>
  </dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/encodeSearchResults.jsp#1 $$Change: 946917 $--%>
