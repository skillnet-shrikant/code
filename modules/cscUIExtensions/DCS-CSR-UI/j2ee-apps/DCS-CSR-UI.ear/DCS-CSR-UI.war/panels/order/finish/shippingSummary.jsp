<%--
Display details of all the shipping groups in the order. Use the
finishOrderBillingLineItem.jsp to render each line item.

Expected params
currentOrder : The order that the payment group details are retrieved from.

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShippingGroupStateDescriptions"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <dsp:getvalueof var="order" param="currentOrder"/>
    <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>

    <c:if test="${empty isExistingOrderView}">
      <c:set var="isExistingOrderView" value="false"/>
    </c:if>

    <%-- Show Shipping Status if viewing an existing order --%>
    <c:choose>
      <c:when test="${order.shippingGroupCount == 1}">
        <c:forEach items="${order.shippingGroups}"
                   var="shippingGroup" varStatus="shippingGroupIndex">
          <dsp:include src="/include/order/shippingGroupReadView.jsp" otherContext="${CSRConfigurator.contextRoot}">
            <dsp:param name="shippingGroup" value="${shippingGroup}"/>
            <dsp:param name="isExistingOrderView" value="${isExistingOrderView}"/>
          </dsp:include>
        </c:forEach>
      </c:when>
      <c:when test="${order.shippingGroupCount > 1}">
        <c:forEach items="${order.shippingGroups}"
                   var="shippingGroup" varStatus="shippingGroupIndex">
          <fieldset>
            <legend>
              <fmt:message key='shippingSummary.shipment.header'/>

              <c:out value="${shippingGroupIndex.count}"/>
            </legend>

            <dsp:include src="/include/order/shippingGroupReadView.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="shippingGroup" value="${shippingGroup}"/>
              <dsp:param name="isExistingOrderView" value="${isExistingOrderView}"/>
            </dsp:include>
            <dsp:include src="/include/order/displayCommerceItem.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="currentOrder" value="${order}"/>
              <dsp:param name="currentShippingGroup" value="${shippingGroup}"/>
              <dsp:param name="isExistingOrderView" value="${isExistingOrderView}"/>
            </dsp:include>
          </fieldset>
        </c:forEach>
      </c:when>
    </c:choose>
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/shippingSummary.jsp#1 $$Change: 946917 $--%>
