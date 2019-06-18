<%--
 This page defines the order summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/orderSummary.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<!-- begin orderSummary.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" scope="request"/>
<dsp:importbean var="agentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools" scope="request"/>

<c:set var="order" value="${cart.current}" scope="request"/>

<dsp:droplet name="/atg/commerce/custsvc/order/IsOrderSubmitted">
  <dsp:param name="order" value="${order}"/>
  <dsp:oparam name="true">
    <c:set var="orderIsSubmitted" value="true" scope="request"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <c:set var="orderIsSubmitted" value="false" scope="request"/>
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/atg/commerce/custsvc/order/OrderIsModifiable">
  <dsp:param name="order" value="${order}"/>
  <dsp:oparam name="true">
    <c:set var="orderIsModifiable" value="true" scope="request" />
  </dsp:oparam>
  <dsp:oparam name="false">
    <c:set var="orderIsModifiable" value="false" scope="request" />
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/atg/commerce/custsvc/order/OrderIsReturnable">
  <dsp:param name="order" value="${order}"/>
  <dsp:oparam name="true">
    <c:set var="orderIsReturnable" value="true" scope="request" />
  </dsp:oparam>
  <dsp:oparam name="false">
    <c:set var="orderIsReturnable" value="false" scope="request" />
  </dsp:oparam>
</dsp:droplet>

<%--
<dsp:droplet name="/atg/commerce/custsvc/appeasement/OrderIsAppeasableDroplet">
  <dsp:param name="orderId" value="${order.id}"/>
  <dsp:oparam name="true">
    <c:set var="orderIsAppeasable" value="true" scope="request" />
  </dsp:oparam>
  <dsp:oparam name="false">
    <c:set var="orderIsAppeasable" value="false" scope="request" />
  </dsp:oparam>
</dsp:droplet>
 --%>
 
<c:set var="orderPendingAppeasement" value="false" scope="request" />

<dsp:droplet name="/atg/commerce/custsvc/appeasement/IsAppeasementPendingApprovalDroplet">
  <dsp:param name="orderId" value="${order.id}"/>
  <dsp:oparam name="true">
    <c:set var="orderPendingAppeasement" value="true" scope="request" />
  </dsp:oparam>
  <dsp:oparam name="false">
    <c:set var="orderPendingAppeasement" value="false" scope="request" />
  </dsp:oparam>
</dsp:droplet>

<c:set var="returnRequest" value="${cart.returnRequest}" scope="request" />
<c:set var="appeasement" value="${cart.appeasement}" scope="request" />

<dsp:droplet name="/atg/commerce/custsvc/order/IsOrderIncomplete">
                <dsp:param name="order" value="${order}"/>
                <dsp:oparam name="true">
                  <c:set var="orderIsIncomplete" value="true" scope="request" />
                </dsp:oparam>
                <dsp:oparam name="false">
                  <c:set var="orderIsIncomplete" value="false" scope="request" />
                </dsp:oparam>
              </dsp:droplet>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/commerce/custsvc/util/CSRConfigurator.usingScheduledOrders" name="value"/>
  <dsp:oparam name="true">
    <dsp:droplet name="/atg/commerce/custsvc/order/scheduled/IsScheduledOrderTemplate">
      <dsp:param name="order" value="${order}"/>
      <dsp:oparam name="true">
        <c:set var="orderIsTemplate" value="true" scope="request"/>
      </dsp:oparam>
      <dsp:oparam name="false">
        <c:set var="orderIsTemplate" value="false" scope="request"/>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>


<dsp:importbean var="start" bean="/atg/commerce/custsvc/ordersummary/Start"/>

<dsp:include otherContext="${start.context}" page="${start.page}"/>

</dsp:page>
<!-- end orderSummary.jsp -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/orderSummary.jsp#2 $$Change: 1179550 $--%>
