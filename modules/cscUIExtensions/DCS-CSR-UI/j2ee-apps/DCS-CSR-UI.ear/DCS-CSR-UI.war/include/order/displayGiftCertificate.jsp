<%--
Display the appropriate details for the gift certificate.

This page is shared across the entire CSC application. This page is used wherever a payment
group information is getting displayed.

This page is more specifically used in billing, order review,
order view and email pages.

Expected params
paymentGroup : The payment group from which the information is going to be retrieved.

If you want to display any of the values below, pass in a parameters below.
propertyName : required -- This parameter is used to display a particular information about a property
displayValue : optional -- The parameter is used to display the value of the <code>propertyName</code>
displayHeading : optional -- The parameter is used to display the heading of the <code>propertyName</code>

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:getvalueof var="paymentGroup" param="paymentGroup"/>
  <dsp:getvalueof var="propertyName" param="propertyName"/>
  <dsp:getvalueof var="displayValue" param="displayValue"/>
  <dsp:getvalueof var="displayHeading" param="displayHeading"/>
  <dsp:getvalueof var="order" param="order"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

  <c:if test="${propertyName == 'value1'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.type'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <fmt:message
        key="newOrderBilling.displayPaymentMethods.giftCertificate"/>
      <c:if
        test="${!empty paymentGroup && !empty paymentGroup.giftCertificateNumber }">
        <fmt:message key="common.hyphen"/>
        &nbsp;
        <c:out value="${paymentGroup.giftCertificateNumber}"/>
      </c:if>
    </c:if>
  </c:if>

  <c:if test="${propertyName == 'value2'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.amtRemaining'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <dsp:droplet
        name="/atg/commerce/custsvc/order/PaymentGroupRemainingAmount">
        <dsp:param name="paymentGroup" value="${paymentGroup}"/>
        <dsp:param name="order" value="${order}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="remainingAmount" param="remainingAmount"/>
        </dsp:oparam>
      </dsp:droplet>
      <csr:formatNumber value="${remainingAmount}" type="currency"
                        currencyCode="${order.priceInfo.currencyCode}"/>
    </c:if>
  </c:if>

  <c:if test="${propertyName == 'value3'}">
    <c:if test="${displayHeading == true}">
    </c:if>
    <c:if test="${displayValue == true}">
    </c:if>
  </c:if>

  <c:if test="${propertyName == 'status'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.state'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <fmt:message key="common.notApplicable"/>
    </c:if>
  </c:if>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayGiftCertificate.jsp#2 $$Change: 1179550 $--%>
