<%--
 This page allows the user to specify the various amounts that are to be refunded
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItems.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">

<dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
<dsp:getvalueof var="returnObject" bean="ShoppingCart.returnRequest"/>

<ul class="atg_commerce_csr_panelToolBar">
  <li class="atg_commerce_csr_return">
    <a href="#"  onclick="atg.commerce.csr.order.viewExistingOrder('${returnObject.order.id}','${returnObject.order.stateAsString}');return false;"><fmt:message key="returnItems.orderDetails.link" /></a>
  </li>
</ul>

<div class="atg_commerce_csr_corePanelData">
  <dsp:include src="/panels/order/returns/returnItemsShippingGroup.jsp" otherContext="${CSRConfigurator.contextRoot}"/>
</div>
</dsp:layeredBundle>

  <script type="text/javascript">
    atg.progress.update('cmcReturnsPS');
  </script>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItems.jsp#1 $$Change: 946917 $--%>
