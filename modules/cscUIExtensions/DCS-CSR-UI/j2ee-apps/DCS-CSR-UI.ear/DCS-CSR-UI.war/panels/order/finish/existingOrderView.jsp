<%--
This file gets the order from the orderId parameter and displays
summary information. The following summaries are displayed:

Shopping Cart - a summary of all the items in the cart
Shipping - a summary of the Shipping Groups and their items
Billing - a summary of they Payment Groups

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">


  <dsp:importbean var="urlDroplet" bean="/atg/svc/droplet/FrameworkUrlDroplet" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="userShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ViewOrderHolder" var="viewOrder"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete"/>
  <dsp:importbean bean="/atg/commerce/custsvc/appeasement/OrderIsAppeasableDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/scheduled/IsScheduledOrderTemplate"/>
  <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsReturnable"/>
  <dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  

  <%--Get the current Order from the ViewOrderHolder--%>
  <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ViewOrderHolder.current"/>

  <%--#########  Existing Order View panel start  #########--%>

  <ul class="atg_commerce_csr_panelToolBar">
    <li class="atg_commerce_csr_return">
      <dsp:include src="/include/order/returnPreviousPage.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="order" value="${order}"/>
      </dsp:include>
    </li>

  <dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/commerce/custsvc/util/CSRConfigurator.usingScheduledOrders" name="value"/>
  <dsp:oparam name="true">

  
    <dsp:droplet name="IsScheduledOrderTemplate">
    <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="true">
    
      <%-- Initialize the originating page --%>    
      <dsp:setvalue bean="OriginatingPage.pageName" value="scheduledOrderView"/>
      <dsp:setvalue bean="OriginatingPage.orderId" value="${order.id}"/>
        
      <svc-ui:frameworkUrl var="addNoteSuccessErrorURL" panelStacks="cmcScheduledOrderPS"/>
      <c:set var="addNotePSToRefresh" value="cmcScheduledOrderPS" />    
      <li class="atg_commerce_csr_last">
        <dsp:include src="/include/order/submitNow.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="order" value="${order}"/>
        </dsp:include>
      </li>
    </dsp:oparam>
    <dsp:oparam name="false">
    
      <%-- Initialize the originating page --%>      
      <dsp:setvalue bean="OriginatingPage.pageName" value="orderView"/>
      <dsp:setvalue bean="OriginatingPage.orderId" value="${order.id}"/>

      <svc-ui:frameworkUrl var="addNoteSuccessErrorURL" panelStacks="cmcExistingOrderPS"/>
      <c:set var="addNotePSToRefresh" value="cmcExistingOrderPS" />
    </dsp:oparam>        
    </dsp:droplet>
  </dsp:oparam>
  </dsp:droplet>
  
  <%-- <li class="atg_commerce_csr_last">
    <dsp:include src="/include/order/copyOrder.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="order" value="${order}"/>
    </dsp:include>
  </li> --%>
  
  
  <dsp:droplet name="HasAccessRight">
    <dsp:param name="accessRight" value="cmcApprovals"/>
  <dsp:oparam name="accessGranted">
    <dsp:include src="/include/order/approveButtons.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="order" value="${order}"/>
    </dsp:include>
    </dsp:oparam>
    <dsp:oparam name="accessDenied">
    </dsp:oparam>
  </dsp:droplet>
  
  </ul>

  <div class="atg_commerce_csr_coreExistingOrderView">
    <dsp:include src="/include/order/intrinsicAttributes.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="currentOrder" value="${order}"/>
    </dsp:include>

    <%-- ######################### Shopping Cart #################### --%>

    <div id="atg_commerce_csr_order_shoppingCart_subPanel" class="atg_commerce_csr_subPanel atg_commerce_csr_shoppingCart">
    <div class="atg_commerce_csr_subPanelHeader">
    <ul class="atg_commerce_csr_panelToolBar">
    <li class="atg_commerce_csr_header"><h4><fmt:message key='finishOrder.shoppingCartSummary.header'/></h4></li>
    <%-- Only display Edit link if the order can be modified --%>
    <%-- Order cart modifications disabled --%>
    <dsp:droplet name="OrderIsModifiable">
      <dsp:param name="order" value="${order}"/>
      <dsp:oparam name="true">
          <%--<li class="atg_commerce_csr_last">
            <a href="#"
                onclick="atg.commerce.csr.order.finish.editExistingOrder('atg_commerce_csr_finish_editExistingOrderCartForm','${order.id}')">
                <fmt:message key='common.shoppingCart.edit'/>
            </a>
          </li>--%>
      </dsp:oparam>
      <dsp:oparam name="false">
      </dsp:oparam>
    </dsp:droplet>
        <%-- Confirm that the order is appeasable. --%>
        <dsp:droplet name="OrderIsAppeasableDroplet">
          <dsp:param name="orderId" value="${order.id}"/>
          <dsp:oparam name="true">
            <%-- Finally, only display the Appeasements link
                if the agent has permissions --%>
            <dsp:droplet name="HasAccessRight">
              <dsp:param name="accessRight" value="cmcAppeasements"/>
              <dsp:oparam name="accessGranted">
                <%-- Appeasement link --%>
                <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
                  <li class="atg_commerce_csr_last">
                    <a id="createAppeasementProcess" href="#" onclick="atg.commerce.csr.order.appeasement.initiateAppeasementProcess({orderId: '${order.id}'});return false;">
                    <fmt:message key='appeasement.create.label' /></a>
                  </li>
                </dsp:layeredBundle>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
        <dsp:droplet name="OrderIsReturnable">
          <dsp:param name="order" value="${order}"/>
          <dsp:oparam name="true">
            <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
              <li class="atg_commerce_csr_last">
                <a id="createReturnExchange" href="#" onclick="atg.commerce.csr.order.returns.initiateReturnProcess({orderId: '${order.id}'});return false;">
                <fmt:message key='returns.create.returnOrExchange' /></a>
              </li>
            </dsp:layeredBundle>
          </dsp:oparam>
        </dsp:droplet>
    </ul>
    </div>
    <div class="atg_svc_content atg_commerce_csr_content">
    <dsp:include src="/panels/order/finish/shoppingCartSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="currentOrder" value="${order}"/>
      <dsp:param name="isExistingOrderView" value="${true}"/>
    </dsp:include>
    </div>
    </div>

    <%-- ######################### Shipping ######################### --%>
    <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ShippingAddressTable">
      <jsp:attribute name="setPageData">
      </jsp:attribute>
      <jsp:body>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="currentOrder" value="${order}"/>
        </dsp:include>
      </jsp:body>
    </csr:renderer>
    <%-- ######################### Billing ########################## --%>

  <div id="atg_commerce_csr_order_billingSummary_subPanel" class="atg_commerce_csr_subPanel">
    <div class="atg_commerce_csr_subPanelHeader" >
      <ul class="atg_commerce_csr_panelToolBar">
        <li class="atg_commerce_csr_header">
        <h4><fmt:message key='finishOrder.billingSummary.header' /> </h4></li>
        <%-- Only display Edit link if the order can be modified --%>
        <dsp:droplet name="OrderIsModifiable">
          <dsp:param name="order" value="${order}"/>
          <dsp:oparam name="true">
            <%-- Order can be modified but if it's incomplete, don't diplay Billing edit link --%>
            <dsp:droplet name="IsOrderIncomplete">
              <dsp:oparam name="false">
                  <li class="atg_commerce_csr_last">
                    <a href="#"
                        onclick="atg.commerce.csr.order.finish.editExistingOrder('atg_commerce_csr_finish_editExistingOrderBillingForm','${order.id}')">
                        <fmt:message key='common.billing.edit'/>
                    </a>
                  </li>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </ul>
    </div>

      <dsp:include src="/panels/order/finish/billingSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="currentOrder" value="${order}"/>
        <dsp:param name="isExistingOrderView" value="${true}"/>
      </dsp:include>
    </div>

    <%-- Display Order Notes --%>
    <c:choose>
      <c:when test="${userShoppingCart.originalOrder.id == viewOrder.current.id}">
        <c:set var="mode" value="edit"/>
      </c:when>
      <c:otherwise>
        <c:set var="mode" value="view"/>
      </c:otherwise>
    </c:choose>
    
    <dsp:include src="/include/order/note/notes.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="mode" value="${mode}"/>
      <dsp:param name="successURL" value="${addNoteSuccessErrorURL}" />
      <dsp:param name="psToRefresh" value="${addNotePSToRefresh}" />
      <dsp:param name="order" value="${order}"/>
      <dsp:param name="isExistingOrderView" value="${true}"/>
    </dsp:include>

  </div>
  <%--#########  Existing Order View panel end  ###########--%>

 

  </dsp:layeredBundle>
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/existingOrderView.jsp#2 $$Change: 1179550 $--%>
