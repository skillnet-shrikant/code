<%--
The Popup that is displayed when an agent clicks the Cancel order button.
This page is used for normal order and exchange orders.
For exchange order, this page provides different options for the user and based on those
options user's can complete their desired actions.


@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/cancelOrderPopup.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="../top.jspf"%>
<c:catch var="exception">

<dsp:page xml="true">
<dsp:importbean bean="/atg/commerce/custsvc/order/CancelOrderFormHandler"/>
<dsp:importbean var="frameworkImpl" bean="/atg/svc/framework/Framework"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<script>
var popupPane = dijit.byId( 'cancelOrderPopup' );
popupPane.titleNode.innerHTML = "<fmt:message key='cancelOrder.popup.header' />";
</script>


  <%--Get the current original Order from the OrderHolder--%>
  <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.originalOrder"/>    
  
  <c:set var="isSubmitted" value="${false}"/>
  <dsp:droplet name="/atg/commerce/custsvc/order/IsOrderSubmitted">
    <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="true">
      <c:set var="isSubmitted" value="${true}"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <c:choose>
  <c:when test="${framework.frameworkInstance.currentTabId eq 'commerceTab'}">
    <svc-ui:frameworkUrl var="cancelSuccessURL" panelStacks="cmcExistingOrderPS,globalPanels"/>
  </c:when>
  <c:otherwise>
    <svc-ui:frameworkUrl var="cancelSuccessURL" panelStacks="globalPanels"/>
  </c:otherwise>
  </c:choose>

  <svc-ui:frameworkUrl var="cancelErrorURL"/>
  
  <!-- these two urls are the success urls used for canceling exchange only and both exchange and return respectively -->
  <svc-ui:frameworkUrl var="refundMethodsPageURL" panelStacks="cmcRefundTypePS"/>
  <svc-ui:frameworkUrl var="viewExistingOrderPageURL" panelStacks="cmcExistingOrderPS"/>

      <%-- Cancel Order Form --%>
      <dsp:form
        id="atg_commerce_csr_finishOrderCancelForm"
        formid="atg_commerce_csr_finishOrderCancelForm">
        <dsp:input type="hidden" priority="-10" value=""
          name="csrCancelOrderHandler"
          bean="CancelOrderFormHandler.cancelOrder" />
        <dsp:input type="hidden" value="${order.id}"
          bean="CancelOrderFormHandler.orderIdToCancel" />
        <dsp:input type="hidden" value="${cancelErrorURL }"
          bean="CancelOrderFormHandler.cancelOrderErrorURL" />
        <dsp:input type="hidden" value="${cancelSuccessURL }"
          bean="CancelOrderFormHandler.cancelOrderSuccessURL" />

        <%-- Returns and Exchange related properties --%>
        <dsp:input type="hidden" priority="-10" value=""
          name="csrCancelExchangeOrderHandler"
          bean="CancelOrderFormHandler.cancelExchangeOrder" />
        <dsp:input type="hidden" value="${cancelSuccessURL }"
          bean="CancelOrderFormHandler.cancelExchangeOrderSuccessURL" />
        <dsp:input type="hidden" value="${cancelErrorURL }"
          bean="CancelOrderFormHandler.cancelExchangeOrderErrorURL" />
        <dsp:input type="hidden" value="${refundMethodsPageURL }"
          bean="CancelOrderFormHandler.refundMethodsPageURL" />
        <dsp:input type="hidden" value="${viewExistingOrderPageURL }"
          bean="CancelOrderFormHandler.viewExistingOrderPageURL" />

        <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
          <dsp:oparam name="true">

          <%-- Always display the highlight validation messages. --%>

         <script type="text/javascript">
         _container_.onLoadDeferred.addCallback(function(){
          atg.commerce.csr.order.finish.resizeCancelOrderWindow();
           });
         </script>

            <div class="atg_commerce_csr_popupPanel_Cancel_Order">

            <div class="panelHeader"><span class="title">
            <fmt:message key='cancelExchangeOrder.popup.cancelQuestion.header' />
            </span></div>
            <dsp:getvalueof var="desiredOperation" bean="CancelOrderFormHandler.desiredExchangeOrderCancelOperation"/>
            <ul class="confirmCancel">
              <li>
                <c:if test="${empty desiredOperation}">
                  <dsp:input name="desiredOption"
                    bean="CancelOrderFormHandler.desiredExchangeOrderCancelOperation"
                    type="radio" value="cancelExchangeOnly" checked="true"/>
                </c:if>
                <c:if test="${!empty desiredOperation}">
                  <dsp:input name="desiredOption"
                    bean="CancelOrderFormHandler.desiredExchangeOrderCancelOperation"
                    type="radio" value="cancelExchangeOnly"/>
                </c:if>
                  <fmt:message key='cancelExchangeOrder.popup.cancelQuestion1' />
                </li>
              <li><dsp:input name="desiredOption"
                bean="CancelOrderFormHandler.desiredExchangeOrderCancelOperation"
                type="radio" value="cancelExchangeReturn" />
                <fmt:message key='cancelExchangeOrder.popup.cancelQuestion2' />
                </li>
            </ul>
            <div class="atg_commerce_csr_panelFooter">
            <%-- OK button --%>
            <input value="<fmt:message key='common.yes'/>" type="button"
              name="cancelExchangeOrderOK"
              onclick="atg.commerce.csr.order.finish.cancelExchangeOrder();" />
            &nbsp;
            <%-- Cancel button --%>
            <input type="button"
              name="cancelExchangeOrderCancel"
              value="<fmt:message key='common.no'/>"
              onclick="atg.commerce.csr.order.finish.hideCancelOrderPrompt();" />
            </div>
            </div>
          </dsp:oparam>
      <dsp:oparam name="false">
			<c:set var="beanName">CSRAgentTools.ppsCancelReasonCodes</c:set>
			<c:if test="${order.bopisOrder}">
				<c:set var="beanName">CSRAgentTools.bopisCancelReasonCodes</c:set>
			</c:if>			
			Select Cancellation Reason1 : 
			<dsp:select bean="CancelOrderFormHandler.cancelOrderReasonCode" id="cancelReasonCode" name="cancelReasonCode">
				 <dsp:droplet name="ForEach">
	        		<dsp:param name="array" bean="${beanName}"/>
	        		 <dsp:param name="elementName" value="reasonCodes" />
	        			<dsp:oparam name="output">
	        				<dsp:getvalueof var="key" param="key" />
	        				<dsp:getvalueof var="reasonCode" param="reasonCodes" />
	        				<dsp:option value="${key}"><c:out value="${reasonCode}" /></dsp:option>
	        			</dsp:oparam>
	        	 </dsp:droplet>
        	</dsp:select>
        	
            <br />
            <c:choose>
            <c:when test="${isSubmitted}">
              <fmt:message key='cancelOrder.popup.unfulfilled' />
            </c:when>
            <c:otherwise>
              <fmt:message key='cancelOrder.popup.delete' />
            </c:otherwise>
            </c:choose>
            <br />
            <fmt:message key='cancelOrder.popup.cancelQuestion' />
            <br />

            <div class="atg_commerce_csr_panelFooter"><%-- OK button --%>
            <input value="<fmt:message key='common.yes'/>" type="button"
              name="cancelOrderOK"
              onClick="atg.commerce.csr.order.finish.cancelOrder();return false;">
            &nbsp; <%-- Cancel button --%> <input type="button"
              name="cancelOrderCancel" value="<fmt:message key='common.no'/>"
              onClick="atg.commerce.csr.order.finish.hideCancelOrderPrompt(); return false;" />
            </div>
      </dsp:oparam>
      </dsp:droplet>
  </dsp:form>
      <%-- End Cancel Order Form --%>
    </dsp:layeredBundle>
    </dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/cancelOrderPopup.jsp#1 $$Change: 946917 $--%>
