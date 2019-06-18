<%--
 Schedules
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/schedules.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>

<dsp:page xml="true">
<div style="height:100%">
<dsp:getvalueof var="viewOrderId" bean="/atg/commerce/custsvc/order/ViewOrderHolder.current.id"/>

<dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderSupportedForUpdate"/>

<dsp:droplet name="IsOrderSupportedForUpdate">
<dsp:param name="order" bean="/atg/commerce/custsvc/order/ViewOrderHolder.current"/>
<dsp:oparam name="true">

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<ul class="atg_commerce_csr_panelToolBar">
<li class="atg_commerce_csr_last">
<a href="#" title="<fmt:message key="schedules.action.addASchedule"/>" onclick="atg.commerce.csr.order.scheduled.loadOrderForAddSchedule('${viewOrderId}');return false;"><fmt:message key="schedules.action.addASchedule"/></a>
</li>
</ul>
</dsp:layeredBundle>

</dsp:oparam>
</dsp:droplet> 

<dsp:droplet name="/atg/commerce/order/scheduled/ScheduledOrderLookup">
<dsp:param name="templateId" value="${viewOrderId}"/>
<dsp:oparam name="output">
  <dsp:include src="/panels/order/displaySchedulesTable.jsp" otherContext="${CSRConfigurator.contextRoot}">
  <dsp:param name="scheduledOrderItems" param="scheduledOrders"/>
  <dsp:param name="templateOrder" bean="/atg/commerce/custsvc/order/ViewOrderHolder.current"/>
  <dsp:param name="templateOrderId" value="${viewOrderId}"/>
  <dsp:param name="showActions" value="true"/>
  </dsp:include>
</dsp:oparam>
</dsp:droplet>
</div>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/schedules.jsp#1 $$Change: 946917 $--%>
