<%@ include file="/include/top.jspf"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/ConfirmationInfo"  var="confirmationInfo"/>

<style type="text/css">
<%@ include file="../../../../css/csc.css" %>
</style>

<%/* render email content in the provided customer display locale */%>
<dsp:getvalueof var="localeString" param="confirmationInfo.customerDisplayLocaleString"/>
<c:if test="${!empty localeString}">
  <fmt:setLocale value="${localeString}" scope="request"/>
</c:if>

<dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">

<dsp:setvalue paramvalue="confirmationInfo.order" param="order"/>
<dsp:setvalue paramvalue="confirmationInfo.profile" param="profile"/>
<dsp:setvalue paramvalue="confirmationInfo.extraData.appeasement" param="appeasement"/>
<dsp:setvalue paramvalue="confirmationInfo.extraData.currencyCode" param="currencyCode"/>

  <p/>
  <dsp:getvalueof param="profile.firstName" var="profileFirstName"/>
  <dsp:getvalueof param="profile.lastName" var="profileLastName"/>
  <fmt:message key="email.appeasement.confirmation.valuedCustomer">
    <fmt:param value="${fn:escapeXml(profileFirstName)}"/>
    <fmt:param value="${fn:escapeXml(profileLastName)}"/>
  </fmt:message>
  <br/>

  <dsp:getvalueof param="order.id" var="orderId"/>
  <fmt:message key="email.appeasement.approval.orderId">
    <fmt:param value="${orderId}"/>
  </fmt:message>
  <br/>

  <dsp:getvalueof param="appeasement.appeasementId" var="appeasementId"/>
  <fmt:message key="email.appeasement.approval.appeasementId">
    <fmt:param value="${appeasementId}"/>
  </fmt:message>
  <br/>

  <dsp:getvalueof param="appeasement.appeasementAmount" var="appeasementAmount"/>
  <dsp:getvalueof param="currencyCode" var="currencyCodeVar"/>
  <csr:formatNumber value="${appeasementAmount}" type="currency" currencyCode="${currencyCodeVar}" var="formattedAppeasementAmount"/>
  <fmt:message key="email.appeasement.approval.appeasementAmount">
    <fmt:param value="${formattedAppeasementAmount}"/>
  </fmt:message>

  <br/>

  <fmt:message key="email.appeasement.approval.rejected.rejectMessage">
  </fmt:message>

</dsp:layeredBundle>

</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/AppeasementApprovalRejected.jsp#1 $$Change: 1179550 $--%>