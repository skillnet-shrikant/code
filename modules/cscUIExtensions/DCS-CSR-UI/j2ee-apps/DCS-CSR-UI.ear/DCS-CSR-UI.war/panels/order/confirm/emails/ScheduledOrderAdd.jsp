<%@ include file="/include/top.jspf"%>

<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<%--
    This is just a sample email template. The whole csc.css is included here.
    It is advisable to use just the necessary css templates to improve the mass email
    performances.
  --%>

<style type="text/css">
<%@ include file="../../../../css/csc.css" %>
</style>

<%/* render email content in the provided customer display locale */%>
<dsp:getvalueof var="localeString" param="confirmationInfo.customerDisplayLocaleString"/>
<c:if test="${!empty localeString}">
  <fmt:setLocale value="${localeString}" scope="request"/>
</c:if>
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
<fmt:message key="confirmOrder.emails.scheduledOrderAdd.orderIsComplete" var="orderIsCompleteMessage"/>
<dsp:setvalue value="${orderIsCompleteMessage}" param="messageSubject"/>
<%--
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="OrderShipped" param="mailingName"/>

<dsp:setvalue paramvalue="confirmationInfo.order" param="order"/>
<dsp:setvalue paramvalue="confirmationInfo.profile" param="profile"/>

<p>
<dsp:getvalueof param="profile.firstName" var="profileFirstName"><fmt:message key="confirmOrder.emails.scheduledOrderUpdate.valuedCustomer"/></dsp:getvalueof>
<dsp:getvalueof param="profile.lastName" var="profileLastName"/>
<fmt:message key="confirmOrder.emails.scheduledOrderAdd.dearCustomer">
  <fmt:param value="${fn:escapeXml(profileFirstName)}"/>
  <fmt:param value="${fn:escapeXml(profileLastName)}"/>
</fmt:message>

<p><fmt:message key="confirmOrder.emails.scheduledOrderAdd.orderProcessed"/>

<p>
<dsp:getvalueof param="order.id" var="orderIdVar"><fmt:message key="confirmOrder.emails.scheduledOrderAdd.contactCustomerCervice"/></dsp:getvalueof>
<fmt:message key="confirmOrder.emails.scheduledOrderAdd.confirmationNumberIs">
  <fmt:param value="${orderIdVar}"/>
</fmt:message>

<hr>

<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include src="/panels/order/confirm/emails/DisplayOrderSummary.jsp" otherContext="${CSRConfigurator.contextRoot}"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include src="/panels/order/confirm/emails/DisplayShippingInfo.jsp" otherContext="${CSRConfigurator.contextRoot}"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include src="/panels/order/confirm/emails/DisplayPaymentInfo.jsp" otherContext="${CSRConfigurator.contextRoot}"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<dsp:getvalueof param="confirmationInfo.extraData.scheduledOrderItem" var="scheduledOrderItemVar"/>
<fmt:message key="confirmOrder.emails.scheduledOrderAdd.orderItemIs">
  <fmt:param value="${scheduledOrderItemVar}"/>
</fmt:message>
<dsp:getvalueof param="confirmationInfo.extraData.schedule" var="scheduleVar"/>
<fmt:message key="confirmOrder.emails.scheduledOrderAdd.scheduleIs">
  <fmt:param value="${scheduleVar}"/>
</fmt:message>
</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/ScheduledOrderAdd.jsp#1 $$Change: 946917 $--%>
