<%--
This file gets the order that exists in ShoppingCart.current and displays
summary information. The following summaries are displayed:

Shopping Cart - a summary of all the items in the cart
Shipping - a summary of the Shipping Groups and their items
Billing - a summary of they Payment Groups

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrder.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:importbean var="shippingGroupFormHandler" bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler" />

  <dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
  <dsp:setvalue bean="OriginatingPage.pageName" value=""/>

  <dsp:getvalueof var="returnRequest" bean="/atg/commerce/custsvc/order/ShoppingCart.returnRequest" scope="request"/>
  <dsp:getvalueof var="userOrder"  bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

      <%--#########  New Order - Complete Order View panel start  #########--%>

      <%--Submit and scheduled order Buttons
      These buttons and forms are repeated in the same page and the form repetition is causing the bug 152772.
      In order to avoid this problem, the form repetition is avoided by passing the includeForm flag.
      --%>
      <dsp:droplet name="/atg/commerce/custsvc/order/IsOrderIncomplete">
      <dsp:oparam name="true">
        <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
        <dsp:oparam name="false">
          <dsp:droplet name="/atg/commerce/custsvc/order/IsOrderAdjustedDroplet">
          <dsp:param name="order" value="${userOrder}"/>
          <dsp:oparam name="true">
            <dsp:droplet name="/atg/web/messaging/AddMessage">
            <dsp:param name="bundleName" value="atg.commerce.order.purchase.UserMessages"/>
            <dsp:param name="key" value="noScheduleOrder"/>
            <dsp:param name="messageType" value="information"/>
            </dsp:droplet>
          </dsp:oparam>
          </dsp:droplet>
          <dsp:droplet name="/atg/commerce/gifts/GiftShippingGroups">
          <dsp:param name="order" value="${userOrder}"/>
          <dsp:oparam name="true">
            <dsp:droplet name="/atg/web/messaging/AddMessage">
            <dsp:param name="bundleName" value="atg.commerce.csr.order.WebAppResources"/>
            <dsp:param name="key" value="finishOrder.noScheduleDueToGiftsInOrder"/>
            <dsp:param name="messageType" value="information"/>
            </dsp:droplet>
          </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
      </dsp:droplet>
    
      
      
      <div class="">
        <dsp:include src="/panels/order/finish/finishOrderButtons.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="currentOrder" value="${userOrder}" />
          <dsp:param name="includeForm" value="${true}" />
        </dsp:include>
      </div>

      <%-- ######################### Shopping Cart #################### --%>
      <div id="atg_commerce_csr_orderReview_shoppingCart_subPanel" class="atg_commerce_csr_subPanel atg_commerce_csr_shoppingCart">
      <div class="atg_commerce_csr_subPanelHeader">
      <ul class="atg_commerce_csr_panelToolBar">
      <li class="atg_commerce_csr_header"><h4><fmt:message key='finishOrder.shoppingCartSummary.header' /></h4></li>
        <li class="atg_commerce_csr_last"><a
          href="#" onclick="atgNavigate({
                    panelStack : 'cmcShoppingCartPS'});return false;">
        <fmt:message key='common.shoppingCart.edit' /> </a></li>
      </ul>
      </div>
      <div class="atg_svc_content atg_commerce_csr_content">
      <dsp:include
        page="shoppingCartSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="currentOrder" value="${userOrder}" />
      </dsp:include>
      </div>
      </div>

      <%-- ######################### Shipping ######################### --%>
      <div id="atg_commerce_csr_orderReview_shippingSummary_subPanel" class="atg_commerce_csr_subPanel">
        <div class="atg_commerce_csr_subPanelHeader">
          <ul class="atg_commerce_csr_panelToolBar">
            <li class="atg_commerce_csr_header">
              <h4><fmt:message key='finishOrder.shippingSummary.header' /></h4>
            </li>
             <c:choose>
             <c:when test="${shippingGroupFormHandler.nonGiftHardgoodShippingGroupCount > 0}">
              <li class="atg_commerce_csr_last"><a href="#"
                onClick="atg.commerce.csr.openPanelStack('cmcShippingMethodPS');return false;"><fmt:message
                key='common.shippingMethod.edit' /></a></li>
            <li>
            <a href="#"
            onClick="atgNavigate({ panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});return false;"><fmt:message
            key='common.shippingAddress.edit' /></a>
            </li>
            </c:when>
             <c:when test="${shippingGroupFormHandler.nonGiftHardgoodShippingGroupCount == 0}">
            <li class="atg_commerce_csr_last">
              <a href="#"
              onClick="atgNavigate({ panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});return false;"><fmt:message
              key='common.shippingAddress.edit' /></a>
              </li>
             </c:when>
             </c:choose>
          </ul>
        </div>
        <dsp:include src="/panels/order/finish/shippingSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="currentOrder" value="${userOrder}" />
        </dsp:include>
      </div>

      <%-- Here we need to find out whether the order is an exchange order or normal order. If the current order is an
      an exchange order, based on amount due, we need to display different information.
      If the amount is to be refunded, display the refund type information. If the refund is not enough to cover the cost,
      billing information to be displayed. If the difference is a wash, we do not need to display any information.

      If the current order is not an exchange order, display the billing information.
      --%>
      <c:set var="exchangeProcess" value="${false }"/>
      <c:if test="${! empty returnRequest && returnRequest.exchangeProcess == true}">
        <c:set var="exchangeProcess" value="${true }"/>
      </c:if>

      <c:if test="${!exchangeProcess}">

      <%-- ######################### Billing ########################## --%>
      <div id="atg_commerce_csr_orderReview_billingSummary_subPanel" class="atg_commerce_csr_subPanel">
        <div class="atg_commerce_csr_subPanelHeader" >
          <ul class="atg_commerce_csr_panelToolBar">
            <li class="atg_commerce_csr_header">
            <h4><fmt:message key='finishOrder.billingSummary.header' /> </h4></li>
            <li class="atg_commerce_csr_last">
            <a href="#" onclick="atgNavigate({
                    panelStack : 'cmcBillingPS',
                    queryParams : { 'init': 'true' }});
                    return false;">
            <fmt:message key='common.billing.edit' /> </a>
            </li>
          </ul>
        </div>

        <dsp:include src="/panels/order/finish/billingSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="currentOrder" value="${userOrder}" />
        </dsp:include>
        </div>
       </c:if>
      <c:if test="${exchangeProcess}">
        <c:choose>
          <c:when test="${returnRequest.returnPaymentState == 'Due'}">
            <%-- ######################### Billing ########################## --%>
            <div class="atg_commerce_csr_subPanel">
              <div class="atg_commerce_csr_subPanelHeader" >
                <ul class="atg_commerce_csr_panelToolBar">
                  <li class="atg_commerce_csr_header">
                  <h4><fmt:message key='finishOrder.billingSummary.header' /> </h4></li>
                  <li class="atg_commerce_csr_last">
                  <a href="#" onclick="atgNavigate({
                          panelStack : 'cmcBillingPS',
                          queryParams : { 'init': 'true' }});
                          return false;">
                  <fmt:message key='common.billing.edit' /> </a>
                  </li>
                </ul>
              </div>

              <dsp:include src="/panels/order/finish/billingSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="currentOrder" value="${userOrder}" />
              </dsp:include>
              </div>
          </c:when>
          <c:when test="${returnRequest.returnPaymentState == 'Refund'}">
          <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
           <%-- ######################### Refund Types #################### --%>
            <div class="atg_commerce_csr_subPanel">
              <div class="atg_commerce_csr_subPanelHeader">
                <ul class="atg_commerce_csr_panelToolBar">
                  <li class="atg_commerce_csr_header">
                    <h4><fmt:message key='finishReturn.refundTypes.table.header'/></h4>
                  </li>
                  <li class="atg_commerce_csr_last">
                      <a href="#" onClick="atgNavigate({panelStack:'cmcRefundTypePS'});return false;">
                          <fmt:message key='common.edit'/> <fmt:message key='finishReturn.refundTypes.table.header'/>
                        </a>
                    </li>
                  </ul>
              </div>
              <dsp:include src="/panels/order/returns/finishRefundSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="returnRequest" value="${returnRequest}"/>
              </dsp:include>
            </div>
        </dsp:layeredBundle>
          </c:when>
        </c:choose>
      </c:if>

      <%-- Display Order Notes --%> <svc-ui:frameworkUrl
        var="addNoteSuccessErrorURL" panelStacks="cmcCompleteOrderPS"/>
      <dsp:include src="/include/order/note/notes.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="mode" value="edit" />
        <dsp:param name="successURL" value="${addNoteSuccessErrorURL}" />
        <dsp:param name="psToRefresh" value="cmcCompleteOrderPS" />
        <dsp:param name="order" value="${userOrder}" />
      </dsp:include>
      <%--Submit and Scheduled order Buttons--%>
      <div id="atg_commerce_csr_orderReview_footerButtons" class="atg_commerce_csr_orderViewFooter">

      <c:if test="${!exchangeProcess}">
        <a class="atg_commerce_csr_return" href="#" onclick="atgNavigate({
                    panelStack : 'cmcBillingPS',
                    queryParams : { 'init': 'true' }});
                    return false;">
            <fmt:message key='common.returnToBilling' /> </a>
      </c:if>
      <c:if test="${exchangeProcess}">
         <c:choose>
          <c:when test="${balance > 0}">
            <a class="atg_commerce_csr_return" href="#" onclick="atgNavigate({
                    panelStack : 'cmcBillingPS',
                    queryParams : { 'init': 'true' }});
                    return false;">
            <fmt:message key='common.returnToBilling' /> </a>
          </c:when>
          <c:when test="${balance < 0}">
            <a class="atg_commerce_csr_return" href="#" onclick="atgNavigate({
                    panelStack : 'cmcRefundTypePS'});
                    return false;">
            <fmt:message key='common.returnToRefundType' /> </a>
          </c:when>
          <c:when test="${balance == 0}">
             <c:choose>
             <c:when test="${shippingGroupFormHandler.nonGiftHardgoodShippingGroupCount > 0}">
                <a class="atg_commerce_csr_return" href="#" onclick="atgNavigate({
                        panelStack : 'cmcShippingMethodPS'});
                        return false;">
                <fmt:message key='common.returnToShippingMethod' /> </a>
             </c:when>
             <c:when test="${shippingGroupFormHandler.nonGiftHardgoodShippingGroupCount == 0}">
                <a class="atg_commerce_csr_return" href="#" onclick="atgNavigate({
                        panelStack : 'cmcShippingAddressPS'});
                        return false;">
                <fmt:message key='common.returnToShippingAddress' /> </a>
             </c:when>
             </c:choose>
          </c:when>
         </c:choose>
      </c:if>

      <%--Submit and scheduled order Buttons
      These buttons and forms are repeated in the same page and the form repetition is causing the bug 152772.
      In order to avoid this problem, the form repetition is avoided by passing the includeForm flag.
      The top of this jsp, includes the form. Passing in includeForm=false to avoid form repetition.
      --%>

      <dsp:include
        page="finishOrderButtons.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="currentOrder" value="${userOrder}" />
        <dsp:param name="includeForm" value="${false}" />
      </dsp:include>
      </div>
      </div>
      <%--#########  New Order - Complete Order View panel end  ###########--%>

  <script type="text/javascript">
    atg.progress.update('cmcCompleteOrderPS');
  </script>
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrder.jsp#1 $$Change: 946917 $--%>
