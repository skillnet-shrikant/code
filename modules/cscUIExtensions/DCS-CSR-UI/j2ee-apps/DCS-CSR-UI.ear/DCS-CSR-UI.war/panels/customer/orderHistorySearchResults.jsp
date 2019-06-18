<%--
 Customer Order History Search Results Panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/orderHistorySearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
  <dsp:setvalue bean="OriginatingPage.pageName" value="customerInformation"/>
    
  <dsp:importbean var="gridConfig" bean="/atg/commerce/custsvc/ui/tables/order/OrderHistoryGrid"/>
  <dsp:include src="${gridConfig.gridPage.URL}" otherContext="${gridConfig.gridPage.servletContext}">
    <dsp:param name="gridConfig" value="${gridConfig}"/>
  </dsp:include>
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/orderHistorySearchResults.jsp#1 $$Change: 946917 $--%>