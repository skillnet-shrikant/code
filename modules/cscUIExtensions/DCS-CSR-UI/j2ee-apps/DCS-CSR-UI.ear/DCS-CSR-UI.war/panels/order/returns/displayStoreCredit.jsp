<%--
Display the appropriate details for the store credit.

This page is used to store credit card details in returns page.

Expected params
refundMethod : The refund method from which the information is going to be retrieved.

If you want to display any of the values below, pass in a parameters below.
propertyName : required -- This parameter is used to display a particular information about a property
displayValue : optional -- The parameter is used to display the value of the <code>propertyName</code>
displayHeading : optional -- The parameter is used to display the heading of the <code>propertyName</code>

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:getvalueof var="paymentGroup" param="refundMethod.storeCredit"/>
  <dsp:getvalueof var="refundMethod" param="refundMethod"/>
  <dsp:getvalueof var="propertyName" param="propertyName"/>
  <dsp:getvalueof var="displayValue" param="displayValue"/>
  <dsp:getvalueof var="displayHeading" param="displayHeading"/>
  <dsp:getvalueof var="order" param="order"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>


  <dsp:tomap var="storeCredit" value="${paymentGroup}"/>

  <c:if test="${propertyName == 'value1'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.type'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <fmt:message key="newOrderBilling.displayPaymentMethods.storeCredit"/>
      <c:if test="${!empty storeCredit && !empty storeCredit.storeCreditNumber }">
        <fmt:message key="common.hyphen"/>
        &nbsp;
        <c:out value="${storeCredit.storeCreditNumber}"/>
      </c:if>
    </c:if>
  </c:if>

  <c:if test="${propertyName == 'value2'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.amtRemaining'/>
    </c:if>
    <c:if test="${displayValue == true}">
     <dsp:getvalueof var="maxAllowedAmount" param="refundMethod.maximumRefundAmount"/>
     <c:choose>
     <c:when test="${!empty maxAllowedAmount && maxAllowedAmount == -1}">
       <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
         <fmt:message key='returnItems.refundType.storeCredit.noLimit'/>
       </dsp:layeredBundle>
     </c:when>
     <c:when test="${!empty maxAllowedAmount}">
      <csr:formatNumber value="${maxAllowedAmount}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
     </c:when>
     </c:choose>
    </c:if>
  </c:if>

  <c:if test="${propertyName == 'value3'}">
    <c:if test="${displayHeading == true}">
    </c:if>
    <c:if test="${displayValue == true}">
    </c:if>
  </c:if>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/displayStoreCredit.jsp#2 $$Change: 1179550 $--%>
