<%--
This page defines the return items panel

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/refundType.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">

<dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupContainerService"/>
<dsp:importbean bean="/atg/commerce/custsvc/profile/AddressHolder"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
<dsp:importbean bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>

<script type="text/javascript">
dojo.require("atg.widget.validation.CurrencyTextboxEx");
</script>

<script type="text/javascript">
  _container_.onLoadDeferred.addCallback(
    function() {
      dojo.subscribe('/atg/commerce/csr/order/PaymentBalance',
        function (event)
        {
          atg.commerce.csr.order.billing.paymentBalanceEventListener(event);
        }
        );
  });
</script>


<dsp:getvalueof var="userOrder" bean="ShoppingCart.current"/>
<dsp:getvalueof var="returnRequest" bean="ShoppingCart.returnRequest"/>
<c:set var="currencyCode" value="${returnRequest.order.priceInfo.currencyCode }"/>

<dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
  <dsp:param name="currencyCode" value="${currencyCode}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
  </dsp:oparam>
</dsp:droplet>

<script type="text/javascript">
  atg.commerce.csr.order.billing.initializePaymentContainer("<c:out value='${returnRequest.totalRefundAmount - returnRequest.replacementOrder.priceInfo.total}'/>", "<c:out value='${currencyCode}'/>", { "locale" : "<dsp:valueof bean='AgentUIConfiguration.javaScriptFormattingLocale' />", "currencySymbol" : "<dsp:valueof bean='CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale' />", "places" : "<c:out value='${currencyDecimalPlaces}' />"});
</script>

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
<dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
  <dsp:oparam name="true">
    <dsp:getvalueof var="processName" param="returnRequest.processName"/>
    <c:choose>
      <c:when test="${processName == 'Return'}">
        <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCompleteReturnPS"/>
      </c:when>
      <c:otherwise>
        <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCompleteOrderPS"/>
      </c:otherwise>
    </c:choose>
  </dsp:oparam>
</dsp:droplet>
<svc-ui:frameworkUrl var="errorURL" panelStacks="cmcRefundTypePS"/>
<svc-ui:frameworkUrl var="cancelReturnRequestSuccessURL" panelStacks="cmcExistingOrderPS,globalPanels"/>

<div class="atg_svc_content atg_commerce_csr_content">
<dsp:form id="csrApplyRefunds" formid="csrApplyRefunds" method="post">

<dsp:input bean="ReturnFormHandler.applyRefundsSuccessURL" value="${successURL}" type="hidden"/>
<dsp:input bean="ReturnFormHandler.applyRefundsErrorURL" value="${errorURL}" type="hidden"/>
<dsp:input name="handleApplyRefunds" bean="ReturnFormHandler.applyRefunds" type="hidden" priority="-10" value=""/>

<dsp:input bean="ReturnFormHandler.cancelReturnRequestSuccessURL" value="${cancelReturnRequestSuccessURL}"
           type="hidden"/>
<dsp:input bean="ReturnFormHandler.cancelReturnRequestErrorURL" value="${errorURL}" type="hidden"/>
<dsp:input name="handleCancelReturnRequest" bean="ReturnFormHandler.cancelReturnRequest" type="hidden" priority="-10"
           value=""/>

<%-- We need to reset for the edit credit card page. This resets the addresses in the address holder. --%>
<dsp:setvalue bean="AddressHolder.resetAddresses" value="true"/>


<p>
  <fmt:message key="returnItems.refundType.header"/>
</p>
<%--This variable is to keep track of the payment type that is being served. We need to display the
the headings for the each payment type, then serve the payment options of that type. This variable is
used to keep track of the previous type is served and if the type changes, then we need to display the heading
for the new type. --%>

<c:set var="previousPGType" value=""/>

<!-- loop through all of the refund methods -->
<dsp:droplet name="ForEach">
  <dsp:param bean="ReturnFormHandler.modifiableRefundMethodList" name="array"/>
  <dsp:param name="elementName" value="refundMethod"/>
  <dsp:param name="sortProperties" value="refundType"/>

  <!-- Render the refund method -->
  <dsp:oparam name="output">
    <dsp:getvalueof var="refundMethod" param="refundMethod"/>
    <dsp:getvalueof var="index" param="index"/>
    <dsp:getvalueof var="pgType" value="${refundMethod.refundType}"/>

    <%--
    If the payment option amount to be restricted for a maximum payment amount,
    then the maxAllowedAmount should be passed in and payment option amount could
    be assigned to the maximum amount.
    --%>
    <%--
    The index is used as the unique identifier to hold the refund type information.
    The same index is used as the widgetId for the input amount field.
    --%>

    <dsp:getvalueof var="maxAllowedAmount" param="refundMethod.maximumRefundAmount" vartype="java.lang.String"/>
    <c:set var="rmWidgetId" value="refundMethod_${index}"/>
    
    <c:choose>
    <c:when test="${!empty maxAllowedAmount && maxAllowedAmount != '-1.0' }">
      <script type="text/javascript">
        _container_.onLoadDeferred.addCallback(function() {
          atg.commerce.csr.order.billing.addPaymentMethod({
            paymentGroupId: '${rmWidgetId}',
            amount: '${refundMethod.amount}',
            maxAllowedAmount:'${maxAllowedAmount}'
          });
        });
      </script>
    </c:when>
    <c:otherwise>
      <script type="text/javascript">
        _container_.onLoadDeferred.addCallback(function() {
          atg.commerce.csr.order.billing.addPaymentMethod({
            paymentGroupId: '${rmWidgetId}',
            amount: '${refundMethod.amount}'
          });
        });
      </script>
    </c:otherwise>
    </c:choose>
    <dsp:getvalueof var="pgTypeConfig" bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}"/>
    
      <%--If there is no previous patyment type or if it is a new payment type, display the headings. --%>
      <c:choose>
        <c:when test="${empty previousPGType}">
          <table class="atg_dataTable atg_commerce_csr_innerTable">
            <thead>
            <th class="atg_commerce_csr_shortData">
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayHeading" value="${true }"/>
                <dsp:param name="order" value="${returnRequest.order}"/>
              </dsp:include>
            </th>
            <th>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value2"/>
                <dsp:param name="displayHeading" value="${true }"/>
                <dsp:param name="order" value="${returnRequest.order}"/>
              </dsp:include>
            </th>
            <th>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value3"/>
                <dsp:param name="displayHeading" value="${true }"/>
                <dsp:param name="order" value="${returnRequest.order}"/>
              </dsp:include>
            </th>
            <th class="atg_numberValue atg_commerce_csr_abbrData">
              <fmt:message key="returnItems.refundType.table.header.amountOrgChrg.title"/>
            </th>
            <th class="atg_numberValue atg_commerce_csr_abbrData">
              <fmt:message key="returnItems.refundType.table.header.amountCredited.title"/>
            </th>
            <th
              class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_billingAmount atg_commerce_csr_returnAmount">
              <fmt:message key="returnItems.refundType.table.header.amount.title"/>
            </th>
            <th></th>
            </thead>
        </c:when>
        <c:when test="${previousPGType != pgType}">
          </table>
          <table class="atg_dataTable atg_commerce_csr_innerTable">
            <thead>
            <th class="atg_commerce_csr_shortData">
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayHeading" value="${true }"/>
                <dsp:param name="order" value="${returnRequest.order}"/>
              </dsp:include>
            </th>
            <th>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value2"/>
                <dsp:param name="displayHeading" value="${true }"/>
                <dsp:param name="order" value="${returnRequest.order}"/>
              </dsp:include>
            </th>
            <th>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value3"/>
                <dsp:param name="displayHeading" value="${true }"/>
                <dsp:param name="order" value="${returnRequest.order}"/>
              </dsp:include>
            </th>
            <th class="atg_numberValue atg_commerce_csr_abbrData">
              <fmt:message key="returnItems.refundType.table.header.amountOrgChrg.title"/>
            </th>
            <th class="atg_numberValue atg_commerce_csr_abbrData">
              <fmt:message key="returnItems.refundType.table.header.amountCredited.title"/>
            </th>
            <th
              class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_billingAmount atg_commerce_csr_returnAmount">
              <fmt:message key="returnItems.refundType.table.header.amount.title"/>
            </th>
            <th></th>
            </thead>
        </c:when>
      </c:choose>
      <c:set var="previousPGType" value="${pgType}"/>
      <tr class="atg_commerce_csr_billingGroup">
        <td>
          <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                       otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
            <dsp:param name="refundMethod" value="${refundMethod}"/>
            <dsp:param name="propertyName" value="value1"/>
            <dsp:param name="displayValue" value="${true }"/>
            <dsp:param name="order" value="${returnRequest.order }"/>
          </dsp:include>
        </td>
        <td>
          <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                       otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
            <dsp:param name="refundMethod" value="${refundMethod}"/>
            <dsp:param name="propertyName" value="value2"/>
            <dsp:param name="displayValue" value="${true }"/>
            <dsp:param name="order" value="${returnRequest.order }"/>
          </dsp:include>
        </td>
        <td>
          <ul class="atg_svc_shipAddress">
          <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                       otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
            <dsp:param name="refundMethod" value="${refundMethod}"/>
            <dsp:param name="propertyName" value="value3"/>
            <dsp:param name="displayValue" value="${true }"/>
            <dsp:param name="order" value="${returnRequest.order }"/>
          </dsp:include>
         </ul>
        </td>
        <dsp:include src="/panels/order/returns/displayPmtCenterFrag.jsp"
                      otherContext="${CSRConfigurator.contextRoot}">
           <dsp:param name="refundMethodIndex" param="index"/>
           <dsp:param name="refundMethod" param="refundMethod"/>
           <dsp:param name="widgetId" value="${rmWidgetId}"/>
        </dsp:include>
        <td class="atg_iconCell">
          <dsp:include src="/panels/order/returns/displayPmtEndFrag.jsp" otherContext="${CSRConfigurator.contextRoot}">
           <dsp:param name="refundMethod" param="refundMethod"/>
           <dsp:param name="refundMethodIndex" param="index"/>
          </dsp:include>
         </td>
      </tr>
  </dsp:oparam>
  <dsp:oparam name="outputEnd">
    </table>
  </dsp:oparam>
  <dsp:oparam name="outputEmpty">
  <table>
  <tr><td>EMPTY REFUND METHODS(/td></tr>
  </table>
</dsp:oparam>
</dsp:droplet>
<!-- ForEach refundMethod -->
</dsp:form>
<div>
  <csr:displayReturnSummary returnRequest="${returnRequest}" isDisplayBalanceDue="${true}"/>
  <script type="text/javascript">
    _container_.onLoadDeferred.addCallback(function()
    {
      atg.commerce.csr.order.billing.assignBalance();
      atg.commerce.csr.order.billing.firePaymentBalanceDojoEvent();
    });
  </script>

  <div class="atg_commerce_csr_billingFooter">
    <csr:displayReturnPanelsCancelButton
      cancelIconOnclickURL="atg.commerce.csr.order.returns.cancelReturnRequestInRefundPage();"
      cancelActionErrorURL="${errorURL}"
      order="${userOrder}"/>
    <input id="checkoutFooterNextButton" type="button"
           onclick="atg.commerce.csr.order.returns.applyRefunds();"
           value="<fmt:message key="common.next.title" />"
    dojoType="atg.widget.validation.SubmitButton"/>
  </div>
</div>
</div>
</dsp:layeredBundle>
<script type="text/javascript">
  atg.progress.update('cmcRefundTypePS');
</script>
<script type="text/javascript">
  if (!dijit.byId("editPaymentOptionFloatingPane")) {
    new dojox.Dialog({ id: "editPaymentOptionFloatingPane",
      cacheContent: "false",
      executeScripts: "true",
      scriptHasHooks: "true",
      duration: 100,
      "class": "atg_commerce_csr_popup"});
  }
</script>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/refundType.jsp#2 $$Change: 1179550 $--%>
