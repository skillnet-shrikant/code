<%--
This page defines the promotions browser pricing area
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/pricingPreview.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>
  <dsp:getvalueof var="agentProfile" bean="/atg/userprofiling/Profile"/>
  <dsp:getvalueof var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>
  <dsp:getvalueof var="baseHolder" bean="/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.baseHolder"/>
  <dsp:getvalueof var="walletHolder" bean="/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.walletHolder"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <csr:displayOrderSummary order="${order}" isShowHeader="${true}"/>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/pricingPreview.jsp#1 $$Change: 946917 $--%>