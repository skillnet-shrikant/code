<%--
Display the appropriate details for the electronic shipping group.

This page is shared across the entire CSC application. This is used wherever an electronic address
is displayed.

There is some tweaking needed in the single shipping page. The flags are used to handle these
special cases.

This page is more specifically used in shipping address selection, order review,
order view, email and return items display pages.

Expected params
shippingGroup : The shipping group from which the information is going to be retrieved.

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
  <dsp:importbean
    bean="/atg/commerce/custsvc/order/ShippingGroupStateDescriptions"/>

  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
  <dsp:getvalueof var="propertyName" param="propertyName"/>
  <dsp:getvalueof var="displayValue" param="displayValue"/>
  <dsp:getvalueof var="displayHeading" param="displayHeading"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>
  <c:if test="${propertyName == 'value1'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='shippingSummary.electronicAddress.header'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <li>
        <c:out value="${shippingGroup.emailAddress}"/>
      </li>
    </c:if>
  </c:if>

  <c:if test="${propertyName == 'selectOptionText'}">
    <c:if test="${displayValue == true}">
      ${fn:escapeXml (shippingGroup.emailAddress)}
    </c:if>
  </c:if>


  <c:if test="${propertyName == 'value2'}">
    <c:if test="${displayHeading == true}">
    </c:if>
    <c:if test="${displayValue == true}">
    </c:if>
  </c:if>
  <c:if test="${propertyName == 'status'}">
    <c:if test="${displayHeading == true}">
      <fmt:message key='shippingSummary.shippingStatus.header'/>
    </c:if>
    <c:if test="${displayValue == true}">
      <dsp:droplet name="ShippingGroupStateDescriptions">
        <dsp:param name="state" value="${shippingGroup.stateAsString}"/>
        <dsp:param name="elementName" value="stateDescription"/>
        <dsp:oparam name="output">
          <dsp:droplet name="IsHighlightedState">
            <dsp:param name="obj" value="${shippingGroup}"/>
            <dsp:oparam name="true">
        <span class="atg_commerce_csr_dataHighlight"><dsp:valueof
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayElectronicShippingGroup.jsp#1 $$Change: 946917 $--%>
