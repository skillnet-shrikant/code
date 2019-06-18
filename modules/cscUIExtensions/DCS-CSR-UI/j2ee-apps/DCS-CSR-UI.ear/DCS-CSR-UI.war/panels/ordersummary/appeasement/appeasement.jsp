<%--
  This page displays the appeasement amount to be credited back to the customer. 
  The amount is displayed as a negative.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/appeasement/appeasement.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<!-- begin ordersummary/appeasement/appeasement.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
<dsp:getvalueof var="step" param="step"/>
<c:if test="${!empty appeasement}">
  <dt>
    <em><fmt:message key="orderSummary.appeasementCredit"/></em>
  </dt>
  <dd>
    <span id="atg_csc_ordersummary_appeasementCredit">
      <csr:formatNumber value="${-appeasement.appeasementAmount}" type="currency" currencyCode="${appeasement.originatingOrder.priceInfo.currencyCode}"/>
    </span>
  </dd>
 </c:if>
</dsp:layeredBundle>
</dsp:page>
<!-- end ordersummary/appeasement/appeasement.jsp -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/appeasement/appeasement.jsp#1 $$Change: 1179550 $--%>
