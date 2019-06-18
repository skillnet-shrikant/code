<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/billing.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<!-- begin ordersummary/modify/billing.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
<dsp:getvalueof var="returnRequest" bean="/atg/commerce/custsvc/order/ShoppingCart.returnRequest" scope="request"/>
<dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.current" scope="request"/>

<dsp:getvalueof var="step" param="step"/>
<c:set var="displayBillingLink" value="${true}"/>

<c:if test="${! empty returnRequest && returnRequest.exchangeProcess == true}">
  <c:if test="${returnRequest.returnPaymentState eq 'Refund'}">
    <c:set var="displayBillingLink" value="${false}"/>
  </c:if>
</c:if>

  <c:if test="${displayBillingLink}">
  <dt>
    <a  href="#" id="keyboardShortcutBilling" onclick="atgNavigate({ panelStack : 'cmcBillingPS', queryParams: { init : 'true' }});return false;"><fmt:message key="orderSummary.billing"/></a>
  </dt>
  <dd>
      <span id="atg_csc_ordersummary_billingComplete">
      <c:if test="${empty step.completeWhenInStepsList || cfn:contains(step.completeWhenInStepsList, param.panel)}">
        <fmt:message key="orderSummary.complete"/>
      </c:if>
    </span>
  </dd>
  </c:if>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/billing.jsp#1 $$Change: 946917 $--%>
