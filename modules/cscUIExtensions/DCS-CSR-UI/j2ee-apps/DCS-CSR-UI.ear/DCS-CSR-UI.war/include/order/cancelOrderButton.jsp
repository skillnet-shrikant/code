<%--
Render a Cancel Button that is used to cancel the order. This page
contains the button and the form for cancelling an order.

Expected params
orderId : The id of the order to cancel.
successUrl: The success url for the cancel order form.
errorUrl: The error url for the cancel order form.

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="../top.jspf"%>
<c:catch var="exception">

<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  
  <%@ include file="cancelOrderCommon.jspf" %>

  <%-- Cancel button --%>
  <input id="checkoutFooterCancelButton" type="button" value="<fmt:message key='common.cancel'/>"
    onclick="atg.commerce.csr.common.showPopupWithReturn({
                  popupPaneId: 'cancelOrderPopup',
                  title: '<fmt:message key='cancelOrder.popup.header' />',
                  url: '${cancelOrderPopupUrl}',
                  onClose: function( args ) {  } })"
    ${(empty cart.originalOrder.id) ? "disabled" : ""} />

  </dsp:layeredBundle>
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/cancelOrderButton.jsp#1 $$Change: 946917 $--%>
