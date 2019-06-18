<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistViewPurchaseMode.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean
    bean="/atg/commerce/custsvc/ui/tables/gift/giftlist/GiftlistPurchaseResultsTable"
    var="giftlistTableConfig" />
    <dsp:importbean
    bean="/atg/commerce/custsvc/gifts/GiftlistUIState" var="GiftlistUIState" />
  <dsp:include src="${giftlistTableConfig.tablePage.URL}"
    otherContext="${giftlistTableConfig.tablePage.servletContext}">
    <dsp:param name="tableConfig" value="${giftlistTableConfig}" />
    <dsp:param name="giftlistId" value="${GiftlistUIState.giftlistId}" />
  </dsp:include>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistViewPurchaseMode.jsp#1 $$Change: 946917 $--%>