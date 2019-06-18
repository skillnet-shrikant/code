<%--
Display the base values for payment group types

This page is displayed when an unknown payment group is found on an order.

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

  <dsp:getvalueof var="paymentGroup" param="paymentGroup"/>
  <dsp:getvalueof var="propertyName" param="propertyName"/>
  <dsp:getvalueof var="displayValue" param="displayValue"/>
  <dsp:getvalueof var="displayHeading" param="displayHeading"/>
  
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  <dsp:importbean
    bean="/atg/commerce/custsvc/order/PaymentGroupStateDescriptions"/>
  
  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>
  
  <c:if test="${propertyName == 'value1'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.type'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <c:out value="${paymentGroup.paymentMethod}"/>
    </c:if>
  </c:if>

  <c:if test="${propertyName == 'value2'}">
    <c:if test="${displayHeading == true}">
      <p>
    </c:if>
    <c:if test="${displayValue == true}">
      <fmt:message key='billingSummary.commerceItem.unknownPaymentType'/>
    </c:if>
  </c:if>
  
  <c:if test="${propertyName == 'value3'}">
    <p>
  </c:if>
  
  <c:if test="${propertyName == 'status'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='billingSummary.commerceItem.header.state'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <dsp:droplet name="PaymentGroupStateDescriptions">
        <dsp:param name="state" value="${paymentGroup.stateAsString}"/>
        <dsp:param name="elementName" value="stateDescription"/>
        <dsp:oparam name="output">
          <dsp:droplet name="IsHighlightedState">
            <dsp:param name="obj" value="${paymentGroup}"/>
            <dsp:oparam name="true">
        <span class="atg_commerce_csr_dataHighlight"> <dsp:valueof
          param="stateDescription"></dsp:valueof></span>
            </dsp:oparam>
            <dsp:oparam name="false">
              <dsp:valueof param="stateDescription"></dsp:valueof>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
  </c:if>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayUnknownPaymentType.jsp#1 $$Change: 946917 $--%>
