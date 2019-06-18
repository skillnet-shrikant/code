<%--
 This page defines the order summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modifyOrderSummary.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<!-- begin modifyOrderSummary.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<dsp:importbean var="currentStep" bean="/atg/commerce/custsvc/ordersummary/ModifyOrder"/>

<dl class="atg_commerce_csr_orderSummaryGuide">
<c:forEach var="step" items="${currentStep.steps}">
  <c:if test="${ empty step.visibleWhenInStepsList || cfn:contains(step.visibleWhenInStepsList, param.panel)}">
    <dsp:include otherContext="${step.context}" page="${step.page}">
      <dsp:param name="step" value="${step}"/>
    </dsp:include>
  </c:if>
</c:forEach>

</dl>

<%-- default view --%>
<c:if test="${!(param.panel =='cmcShoppingCartPS' || param.panel == 'cmcCompleteOrderPS' || param.panel == 'cmcShippingAddressPS' || param.panel == 'cmcShippingMethodPS' || param.panel == 'cmcBillingPS')}">
  <span id="atg_csc_ordersummary_action" class="atg_commerce_csr_orderSummaryAction"><a  href="#" onclick="<dsp:include src="/include/order/currentOrderViewAction.jsp" otherContext="${CSRConfigurator.contextRoot}"/>event.cancelBubble=true;return false;"><fmt:message key="orderSummary.checkOut"/></a></span>
</c:if>

<%-- when on shopping cart, shipping address, shipping method, billing, review pages --%>
<c:if test="${(param.panel == 'cmcShoppingCartPS' || param.panel == 'cmcCompleteOrderPS' || param.panel == 'cmcShippingAddressPS' || param.panel == 'cmcShippingMethodPS' || param.panel == 'cmcBillingPS')}">
  <span id="atg_csc_ordersummary_action" class="atg_commerce_csr_orderSummaryAction"><a  href="#" onclick="atg.commerce.csr.openPanelStack('cmcCatalogPS');return false;"><fmt:message key="orderSummary.addProducts"/></a></span>
</c:if>

</dsp:layeredBundle>
</dsp:page>
<!-- end modifyOrderSummary.jsp -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modifyOrderSummary.jsp#1 $$Change: 946917 $--%>
