<%@ include file="/include/top.jspf"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<style type="text/css">
<%@ include file="../../../../css/csc.css" %>
</style>

<%/* render email content in the provided customer display locale */%>
<dsp:getvalueof var="localeString" param="confirmationInfo.customerDisplayLocaleString"/>
<c:if test="${!empty localeString}">
  <fmt:setLocale value="${localeString}" scope="request"/>
</c:if>

<dsp:layeredBundle basename="atg.commerce.csr.approvals.order.WebAppResources">

<dsp:setvalue paramvalue="confirmationInfo.order" param="order"/>
<dsp:setvalue paramvalue="confirmationInfo.profile" param="profile"/>
<dsp:getvalueof var="currencyCode" param="order.priceInfo.currencyCode"/>

<p>
<dsp:getvalueof param="profile.firstName" var="profileFirstName"/>
<dsp:getvalueof param="profile.lastName" var="profileLastName"/>
<fmt:message key="approvals.emails.dearCustomer">
  <fmt:param value="${fn:escapeXml(profileFirstName)}"/>
  <fmt:param value="${fn:escapeXml(profileLastName)}"/>
</fmt:message>

<p>

<fmt:message key="approvals.emails.orderNotFulfilled"/>

<p>
<dsp:getvalueof param="order.id" var="orderId"/>
<fmt:message key="approvals.emails.contactCustomerServices">
  <fmt:param value="${fn:escapeXml(orderId)}"/>
</fmt:message>

<table cellspacing=2 cellpadding=0 border=0>

  <tr>
    <td><fmt:message key="approvals.emails.quantity"/></td>
    <td><fmt:message key="approvals.emails.product"/></td>
    <td><fmt:message key="approvals.emails.sku"/></td>
    <td><fmt:message key="approvals.emails.priceEach"/></td>
    <td><fmt:message key="approvals.emails.salePrice"/></td>
    <td><fmt:message key="approvals.emails.totalPrice"/></td>
  </tr>
  
  
  <dsp:tomap var="orderMap" param="order" recursive="true"/>
  
  <dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.commerceItems"/>
  <dsp:param name="elementName" value="item"/>
    <dsp:oparam name="output">
    <dsp:tomap var="itemMap" param="item" recursive="true"/>
            
      <tr>
          <td><web-ui:formatNumber value="${itemMap.quantity}"/></td>
          <dsp:tomap var="sku" value="${itemMap.auxiliaryData.catalogRef}"/>
          <td>${fn:escapeXml(sku.displayName)}</td>
          <td>${fn:escapeXml(sku.id)}</td>
          <td><csr:formatNumber value="${itemMap.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}"/></td>
          <td><csr:formatNumber value="${itemMap.salePrice}" type="currency" currencyCode="${currencyCode}"/></td>
          <td><csr:formatNumber value="${itemMap.priceInfo.listPrice*itemMap.quantity}" type="currency" currencyCode="${currencyCode}"/></td>
      </tr>
  
    </dsp:oparam>
              
  </dsp:droplet> 

</table>

<dsp:tomap var="priceInfoMap" param="order.priceInfo" recursive="true"/>

<table cellspacing=2 cellpadding=0 border=0>

  <tr>
    <td><fmt:message key="approvals.emails.subtotal"/></td>
    <td><csr:formatNumber value="${priceInfoMap.rawSubtotal}" type="currency" currencyCode="${currencyCode}"/></td>
  </tr>
  <tr>
    <td><fmt:message key="approvals.emails.orderDiscount"/></td>
    <td><csr:formatNumber value="${priceInfoMap.discountAmount}" type="currency" currencyCode="${currencyCode}"/></td>
  </tr>
  <tr>
    <td><fmt:message key="approvals.emails.adjustment"/></td>
    <td><csr:formatNumber value="${priceInfoMap.manualAdjustmentTotal}" type="currency" currencyCode="${currencyCode}"/></td>
  </tr>
  <tr>
    <td><fmt:message key="approvals.emails.shipping"/></td>
    <td><csr:formatNumber value="${priceInfoMap.shipping}" type="currency" currencyCode="${currencyCode}"/></td>
  </tr>
  <tr>
    <td><fmt:message key="approvals.emails.tax"/></td>
    <td><csr:formatNumber value="${priceInfoMap.tax}" type="currency" currencyCode="${currencyCode}"/></td>
  </tr>
  <tr>
    <td><strong><fmt:message key="approvals.emails.orderTotal"/></strong></td>
    <td><strong><csr:formatNumber value="${priceInfoMap.total}" type="currency" currencyCode="${currencyCode}"/></strong></td>
  </tr>
</table>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/approvalRejectedEmailTemplate.jsp#2 $$Change: 1179550 $--%>
