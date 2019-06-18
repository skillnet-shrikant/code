<%@ include file="/include/top.jspf"%>

<dsp:page>

 <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
 <dsp:importbean bean="/atg/commerce/custsvc/order/ConfirmationInfo"  var="confirmationInfo"/>
 <dsp:importbean bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler"/>
 
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

  <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">

    <dsp:setvalue paramvalue="confirmationInfo.extraData.appeasement" param="appeasement"/>
    <dsp:setvalue paramvalue="confirmationInfo.order" param="order"/>
    <dsp:setvalue paramvalue="confirmationInfo.profile" param="profile"/>
    	  
	  <p/>
    <dsp:getvalueof param="profile.firstName" var="profileFirstName"/>
    <dsp:getvalueof param="profile.lastName" var="profileLastName"/>
    <fmt:message key="email.appeasement.pending.valuedCustomer">
      <fmt:param value="${fn:escapeXml(profileFirstName)}"/>
      <fmt:param value="${fn:escapeXml(profileLastName)}"/>
    </fmt:message>

    <br/>
    <dsp:getvalueof param="order.id" var="orderId"/>
    <p/>
    <fmt:message key="email.appeasement.pending.reviewDetails">
      <fmt:param value="${orderId}"/>
    </fmt:message>

    <table>
      <tr valign=top>
        <td>
          <dsp:getvalueof param="appeasement.appeasementId" var="appeasementId"/>
          <fmt:message key="email.appeasement.pending.authorizationNumber"/>
        </td>
        <td>${fn:escapeXml(appeasementId)}</td>
      </tr>
      <tr>
        <dsp:getvalueof param="appeasement.appeasementAmount" var="appeasementAmount"/>
        <dsp:getvalueof param="appeasement.originatingOrder.priceInfo.currencyCode" var="currencyCode"/>
        <td><fmt:message key="email.appeasement.pending.amount"/></td>
        <td><csr:formatNumber value="${fn:escapeXml(appeasementAmount)}" type="currency" currencyCode="${currencyCode}"/></td>
      </tr>
      <tr>
        <dsp:getvalueof param="appeasement.reasonCode" var="reasonCode"/>
        <td><fmt:message key="email.appeasement.pending.reason"/></td>
        <td>
          <dsp:layeredBundle basename="atg.commerce.csr.appeasement.AppeasementReasonMessages">
            <fmt:message key="${reasonCode}"/>
          </dsp:layeredBundle>
        </td>
      </tr>
      <tr></tr>
      <tr></tr>
    </table>
    <fmt:message key="email.appeasement.pending.message"/>
  </dsp:layeredBundle>

</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/AppeasementPending.jsp#1 $$Change: 1179550 $--%>