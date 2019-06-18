<%--
 This page defines the order summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/exchangeOrderSummary.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<!-- begin exchangeOrderSummary.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<dsp:importbean var="currentStep" bean="/atg/commerce/custsvc/ordersummary/Exchange"/>

<dl class="atg_commerce_csr_orderSummaryGuide">

<c:forEach var="step" items="${currentStep.steps}">
  <c:if test="${ empty step.visibleWhenInStepsList || cfn:contains(step.visibleWhenInStepsList, param.panel)}">
    <dsp:include otherContext="${step.context}" page="${step.page}">
      <dsp:param name="step" value="${step}"/>
    </dsp:include>
  </c:if>
</c:forEach>

</dl>

<span id="atg_csc_ordersummary_action">
<c:if test="${param.panel == 'cmcReturnsPS'}">
<%@ include file="/include/order/cancelOrderCommon.jspf" %>
<a style="text-decoration: underline;" href="#"
  onclick="atg.commerce.csr.common.showPopupWithReturn({
             popupPaneId: 'cancelOrderPopup',
             title: '<fmt:message key='cancelOrder.popup.header' />',
             url: '${cancelOrderPopupUrl}',
             onClose: function( args ) {  } });
             return false;">
  <fmt:message key="orderSummary.cancelExchange"/>
</a>
</c:if>
<c:if test="${param.panel == 'cmcCatalogPS' }">
  <a style="text-decoration: underline;" href="#" onclick="<dsp:include src="/include/order/currentOrderViewAction.jsp" otherContext="${CSRConfigurator.contextRoot}"/>return false;"><fmt:message key="orderSummary.checkOut"/></a>
</c:if>
<c:if test="${param.panel == 'cmcShoppingCartPS' || param.panel == 'cmcShippingAddressPS' || param.panel == 'cmcShippingMethodPS' || param.panel == 'cmcBillingPS' || param.panel == 'cmcCompleteOrderPS' || param.panel == 'cmcRefundTypePS'}">
  <a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.openPanelStack('cmcCatalogPS');return false;"><fmt:message key="orderSummary.addProducts"/></a>
</c:if>
<c:if test="${param.panel == 'cmcCatalogPS' || param.panel == 'cmcShoppingCartPS' || param.panel == 'cmcShippingAddressPS' || param.panel == 'cmcShippingMethodPS' || param.panel == 'cmcBillingPS' || param.panel == 'cmcCompleteOrderPS' || param.panel == 'cmcRefundTypePS'}">
<%@ include file="/include/order/cancelOrderCommon.jspf" %>
&nbsp;|&nbsp;
<a style="text-decoration: underline;" href="#"
  onclick="atg.commerce.csr.common.showPopupWithReturn({
             popupPaneId: 'cancelOrderPopup',
             title: '<fmt:message key='cancelOrder.popup.header' />',
             url: '${cancelOrderPopupUrl}',
             onClose: function( args ) {  } });
             return false;">
  <fmt:message key="orderSummary.cancelExchange"/>
</a>
</c:if>
</span>
</dsp:layeredBundle>
</dsp:page>
<!-- end exchangeOrderSummary.jsp -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/exchangeOrderSummary.jsp#1 $$Change: 946917 $--%>
