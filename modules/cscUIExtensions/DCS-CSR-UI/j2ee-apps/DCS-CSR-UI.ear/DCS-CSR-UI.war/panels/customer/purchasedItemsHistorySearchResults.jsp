<%--
 Customer Returnable Items Search Results Panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/purchasedItemsHistorySearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/ui/tables/order/PurchasedItemsGrid" var="gridConfig"/>
  <dsp:include src="${gridConfig.gridPage.URL}" otherContext="${gridConfig.gridPage.servletContext}">
    <dsp:param name="gridConfig" value="${gridConfig}"/>
  </dsp:include>
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/purchasedItemsHistorySearchResults.jsp#1 $$Change: 946917 $--%>