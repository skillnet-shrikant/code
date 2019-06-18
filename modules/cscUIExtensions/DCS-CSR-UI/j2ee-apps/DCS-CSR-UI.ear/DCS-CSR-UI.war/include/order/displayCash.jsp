<%--
Display the appropriate details for the credit card.

This page is shared across the entire CSC application. This page is used wherever a payment
group information is getting displayed.

This page is more specifically used in billing, returns, order review,
order view and email pages.

Expected params
paymentGroup : The payment group from which the information is going to be retrieved.

If you want to display any of the values below, pass in a parameters below.
propertyName : required -- This parameter is used to display a particular information about a property
displayValue : optional -- The parameter is used to display the value of the <code>propertyName</code>
displayHeading : optional -- The parameter is used to display the heading of the <code>propertyName</code>

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  <dsp:importbean var="paymentGroupFormHandler" bean="/atg/commerce/custsvc/order/PaymentGroupFormHandler"/>
  <dsp:getvalueof var="userOrder" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupStateDescriptions"/>

  <dsp:getvalueof var="paymentGroup" param="paymentGroup"/>
  <dsp:getvalueof var="propertyName" param="propertyName"/>
  <dsp:getvalueof var="displayValue" param="displayValue"/>
  <dsp:getvalueof var="displayHeading" param="displayHeading"/>
  <dsp:getvalueof var="index" param="index"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

  <c:if test="${propertyName == 'value1'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.type'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <fmt:message key='billingSummary.cashPayment.type'/>
    </c:if>
  </c:if>


</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayCash.jsp#1 $$Change: 946917 $--%>
