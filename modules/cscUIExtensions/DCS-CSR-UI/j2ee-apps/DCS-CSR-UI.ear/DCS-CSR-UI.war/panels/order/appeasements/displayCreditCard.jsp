<%--
Display the appropriate details for the credit card.

This page is used to display credit card details in returns page.

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

  <dsp:getvalueof var="paymentGroup" param="refundMethod.creditCard"/>
  <dsp:getvalueof var="propertyName" param="propertyName"/>
  <dsp:getvalueof var="displayValue" param="displayValue"/>
  <dsp:getvalueof var="displayHeading" param="displayHeading"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

  <c:if test="${propertyName == 'value1'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.type'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <csr:displayCreditCardType creditCard="${paymentGroup}"/>
    </c:if>
  </c:if>


  <c:if test="${propertyName == 'value2'}">
    <c:set var="paymentOptionExpired" value="false"/>

    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.expirationDate'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <dsp:droplet name="/atg/commerce/custsvc/order/CreditCardIsExpired">
        <dsp:param name="creditCard" value="${paymentGroup }"/>
        <dsp:oparam name="true">
          <c:set var="paymentOptionExpired" value="true"/>
        </dsp:oparam>
      </dsp:droplet>
  <span
    class="${(!paymentOptionExpired) ? '' : 'atg_commerce_csr_common_error'}">
    <c:if test="${!empty paymentGroup && !empty paymentGroup.expirationMonth }">
      <c:out value="${paymentGroup.expirationMonth}"/>
    </c:if> 
    <c:if test="${!empty paymentGroup && !empty paymentGroup.expirationYear }">
    /
    <c:out value="${paymentGroup.expirationYear}"/>
    </c:if> 
    </span>
   </c:if>
  </c:if>

  <c:if test="${propertyName == 'value3'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.billingAddress'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <c:if test="${!empty paymentGroup && !empty paymentGroup.billingAddress }">
        <dsp:include src="/include/addresses/addressView.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="address" value="${paymentGroup.billingAddress}"/>
          <dsp:param name="heading" value="${heading}"/>
        </dsp:include>
      </c:if>
    </c:if>
  </c:if>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/displayCreditCard.jsp#1 $$Change: 1179550 $--%>
