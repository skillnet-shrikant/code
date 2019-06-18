<%--
 This page defines the order summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/returnOrderSummary.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%-- begin returnOrderSummary.jsp --%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<dsp:importbean var="currentStep" bean="/atg/commerce/custsvc/ordersummary/Return"/>
<dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" scope="request"/>
<dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
<c:set var="order" value="${cart.current}" scope="request"/>

<dl class="atg_commerce_csr_orderSummaryGuide">

<c:forEach var="step" items="${currentStep.steps}">
  <c:if test="${ empty step.visibleWhenInStepsList || cfn:contains(step.visibleWhenInStepsList, param.panel)}">
    <dsp:include otherContext="${step.context}" page="${step.page}">
      <dsp:param name="step" value="${step}"/>
    </dsp:include>
  </c:if>
</c:forEach>

</dl>

<c:set var="displayReturnOrAppeasementLinks" value="true" scope="request" />

<c:if test="${param.panel == 'cmcReturnsPS' }">
  <c:set var="displayReturnOrAppeasementLinks" value="false" scope="request" />
  <span id="atg_csc_ordersummary_action">
    <a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.order.returns.cancelReturnRequest();return false;">
      <fmt:message key="orderSummary.cancelReturn"/>
    </a>
  </span>
</c:if>
<c:if test="${param.panel == 'cmcRefundTypePS'}">
  <c:set var="displayReturnOrAppeasementLinks" value="false" scope="request" />
  <span id="atg_csc_ordersummary_action"><a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.order.returns.cancelReturnRequestInRefundPage();return false;"><fmt:message key="orderSummary.cancelReturn"/></a></span>
</c:if>
<c:if test="${param.panel == 'cmcCompleteReturnPS'}">
  <c:set var="displayReturnOrAppeasementLinks" value="false" scope="request" />
  <span id="atg_csc_ordersummary_action"><a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.order.returns.cancelReturnRequestInCompletePage();return false;"><fmt:message key="orderSummary.cancelReturn"/></a></span>
</c:if>

<c:if test="${param.panel == 'cmcAppeasementSummaryPS'}">
  <c:set var="displayReturnOrAppeasementLinks" value="false" scope="request" />
  <span id="atg_csc_ordersummary_action"><a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.order.appeasement.cancelAppeasement();return false;"><fmt:message key="orderSummary.cancelAppeasement"/></a></span>
</c:if>

<c:if test="${param.panel == 'cmcAppeasementsPS'}">
  <c:set var="displayReturnOrAppeasementLinks" value="false" scope="request" />
</c:if>

<%-- Only display the links to start a return or appeasement if we are not already processing one --%>

<c:if test="${displayReturnOrAppeasementLinks}">

  <%-- Display quick links for creating an appeasement or return if none exist --%>
  <c:if test="${orderIsReturnable}">
    <span id="atg_csc_ordersummary_action"><a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.order.returns.initiateReturnProcess({orderId: '${order.id}'});return false;"><fmt:message key="orderSummary.processReturnOrExchange"/></a></span>
  </c:if>

  <%-- Confirm that the order is appeasable. --%>
  <c:if test="${orderIsAppeasable}">
    <dsp:droplet name="HasAccessRight">
      <dsp:param name="accessRight" value="cmcAppeasements"/>
      <dsp:oparam name="accessGranted">
        <%-- Appeasement link --%>
        <span id="atg_csc_ordersummary_action"><a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.order.appeasement.initiateAppeasementProcess({orderId: '${order.id}'});return false;"><fmt:message key="orderSummary.appeaseOrder"/></a></span>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

</c:if>


</dsp:layeredBundle>
</dsp:page>
<%-- end returnOrderSummary.jsp --%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/returnOrderSummary.jsp#2 $$Change: 1179550 $--%>
