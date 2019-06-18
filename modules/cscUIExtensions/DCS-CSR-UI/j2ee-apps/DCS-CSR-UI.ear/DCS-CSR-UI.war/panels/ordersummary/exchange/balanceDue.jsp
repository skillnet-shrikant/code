<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/exchange/balanceDue.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<!-- begin ordersummary/exchange/balanceDue.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <c:set var="balance" value="${order.priceInfo.total - returnRequest.totalRefundAmount}"/>
  <dt>
  <c:if test="${balance < 0.0}">
    <em><fmt:message key="orderSummary.refundDue"/></em>
  </c:if>
  <c:if test="${balance >= 0.0}">
    <em><fmt:message key="orderSummary.paymentDue"/></em>
  </c:if>
  </dt>
  <dd>
  <csr:getCurrencyCode order="${order}">
   <c:set var="currencyCode" value="${currencyCode}" scope="request" />
  </csr:getCurrencyCode> 
  
    <span id="atg_csc_ordersummary_orderTotalAmount" class="${ balance >= 0.0 ? 'atg_csc_positiveBalance' : 'atg_csc_negativeBalance' }">
      <csr:formatNumber value="${balance}" type="currency" currencyCode="${currencyCode}"/>
    </span>
  </dd>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/exchange/balanceDue.jsp#2 $$Change: 1179550 $--%>
