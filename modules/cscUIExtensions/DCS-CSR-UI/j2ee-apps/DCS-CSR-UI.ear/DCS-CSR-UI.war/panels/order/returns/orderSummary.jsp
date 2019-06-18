<%--
 This page defines the order summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/orderSummary.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="userShoppingCart"/>

  <div class="atg_commerce_csr_corePanelData">
  <div class="atg_commerce_csr_coreCompleteOrderViewPanel">

  <!-- Display Promotions -->
  <dsp:include src="/include/order/promotionsSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
    <dsp:param name="order" value="${userShoppingCart.returnRequest.order}" />
  </dsp:include>

  <!-- Display Pricing Summary -->
  <csr:displayOrderSummary order="${userShoppingCart.returnRequest.order}" />

  </div>
  </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/orderSummary.jsp#1 $$Change: 946917 $--%>
