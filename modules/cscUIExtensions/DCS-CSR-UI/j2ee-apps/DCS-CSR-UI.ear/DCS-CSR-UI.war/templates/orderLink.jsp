<%--
 This page defines the order link template
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/templates/orderLink.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:getvalueof var="isScheduledOrders" bean="/atg/commerce/custsvc/util/CSRConfigurator.usingScheduledOrders"/>

<script type="text/javascript">
dojo.provide( "atg.commerce.csr.order.scheduled" );
atg.commerce.csr.order.scheduled.isScheduledOrders = "${isScheduledOrders}";

<%-- Cancel Order popup --%>
dojo.addOnLoad(function () {
  if (!dijit.byId("cancelOrderPopup")) {
    new dojox.Dialog({ id: "cancelOrderPopup",
                       cacheContent: "false",
                       executeScripts: "true",
                       scriptHasHooks: "true",
                        duration: 100,
                       "class": "atg_commerce_csr_popup"});
  }
});
</script>

  <dsp:getvalueof var="ticketId" param="ticketId"/>
  <dsp:getvalueof var="panelId" param="panelId"/>
  <dsp:getvalueof var="panelStackId" param="panelStackId"/>
  <dsp:getvalueof var="otherContext" param="otherContext"/>
  <dsp:getvalueof var="resourceBundle" param="resourceBundle"/>

  <%--Get the current Order from the OrderHolder--%>
  <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.originalOrder"/>    

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <div id="orderLink" class="tabValue">
      <a id="orderLinkAnchor" href="#" onclick="<dsp:include src="/include/order/currentOrderViewAction.jsp" otherContext="${CSRConfigurator.contextRoot}"/>event.cancelBubble=true;return false;" class="globalTicket">
        <dsp:getvalueof var="transient" bean="/atg/commerce/custsvc/order/ShoppingCart.originalOrder.transient"/>
        <c:if test="${transient}">
          <fmt:message key="order.new"/>
        </c:if>
        <c:if test="${not transient}">
          <dsp:getvalueof var="orderId" bean="/atg/commerce/custsvc/order/ShoppingCart.originalOrder.id"/>
          ${orderId}
        </c:if>
      </a>
    </div>

    <c:set var="showSaveButton" value="${false}"/>
    <dsp:droplet name="/atg/commerce/custsvc/order/IsOrderIncomplete">
      <dsp:param name="order" value="${order}"/>
      <dsp:oparam name="true">
        <c:set var="showSaveButton" value="${true}"/>
      </dsp:oparam>
    </dsp:droplet>
    
    <c:set var="showCancelButton" value="${false}"/>
    <dsp:droplet name="/atg/commerce/custsvc/order/scheduled/IsScheduledOrderTemplate">
    <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="false">
      <dsp:droplet name="/atg/commerce/custsvc/order/OrderIsModifiable">
        <dsp:param name="order" value="${order}"/>
        <dsp:oparam name="true">
          <c:set var="showCancelButton" value="${true}"/>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
    </dsp:droplet>


    <c:if test="${(showSaveButton == true) || (showCancelButton == true)}">
      <div class="tabActions">
    </c:if>
    
      <c:if test="${showSaveButton == true}">
        <a id="orderSave" href="#" 
           onclick="atg.commerce.csr.commitOrder('globalCommitOrderForm');event.cancelBubble=true;return false;" 
           class="iconSave" title="<fmt:message key="order.save"/>"
          ><fmt:message key="order.save"/></a>
      </c:if>

      <c:if test="${showCancelButton == true}">
        <%-- Cancel Order source --%>
        <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart" />
        <c:url var="cancelOrderCommerceUrl" context="${CSRConfigurator.contextRoot}" value="/include/order/cancelOrderPopup.jsp">
          <c:param name="orderId" value="${cart.originalOrder.id}" />
          <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}" />
        </c:url>
        <a id="orderCancel" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                popupPaneId: 'cancelOrderPopup',
                url: '${cancelOrderCommerceUrl}',
                onClose: function( args ) {  } });event.cancelBubble=true;return false;" 
           class="iconDiscard" title="<fmt:message key="order.discard"/>"
                ><fmt:message key="order.discard"/>
        </a>
      </c:if>
    
    <c:if test="${(showSaveButton == true) || (showCancelButton == true)}">
      </div>
    </c:if>

  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/templates/orderLink.jsp#1 $$Change: 946917 $--%>
