<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/return/refundType.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<!-- begin ordersummary/return/refundType.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
<dsp:getvalueof var="step" param="step"/>
<dsp:getvalueof var="returnRequest" bean="/atg/commerce/custsvc/order/ShoppingCart.returnRequest" scope="request"/>
<dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.current" scope="request"/>

<c:set var="displayReturnTypeLink" value="${true}"/>

<c:if test="${!empty returnRequest && returnRequest.exchangeProcess == true}">
  <c:set var="balance" value="${order.priceInfo.total - returnRequest.totalRefundAmount}"/>
  <c:if test="${returnRequest.returnPaymentState eq 'Due' || returnRequest.returnPaymentState eq 'None'}">
    <c:set var="displayReturnTypeLink" value="${false}"/>
  </c:if>
</c:if>
 <c:if test="${displayReturnTypeLink}">
  <dt>
    <a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.openPanelStack('cmcRefundTypePS');return false;"><fmt:message key="orderSummary.refundType"/></a>
  </dt>
  <dd>
    <span id="atg_csc_ordersummary_refundComplete">
      <c:if test="${empty step.completeWhenInStepsList || cfn:contains(step.completeWhenInStepsList, param.panel)}">
        <fmt:message key="orderSummary.complete"/>
      </c:if>
    </span>
  </dd>
  </c:if>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/return/refundType.jsp#1 $$Change: 946917 $--%>
