<%-- FIXME: Add page comments

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/choosePaymentOptions.jsp#2 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupFormHandler"/>
  <dsp:importbean var="shippingGroupFormHandler" bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupDroplet"/>
  <dsp:importbean bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>
  
  <dsp:getvalueof var="paymentGroups" param="paymentGroups"/>
  <dsp:getvalueof var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>

  <dsp:getvalueof var="userOrder" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

  <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
  <c:set var="order" value="${cart.current}" />
  
    <script type="text/javascript">
      dojo.require("dojox.Dialog");
      dojo.require("atg.widget.validation.CurrencyTextboxEx");
    </script>

    <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
      <dsp:param name="currencyCode" value="${userOrder.priceInfo.currencyCode}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
        </dsp:oparam>
    </dsp:droplet>

    <script type="text/javascript">
    _container_.onLoadDeferred.addCallback(function() {
        dojo.subscribe("/atg/commerce/csr/order/PaymentBalance",
          function (event) {
            atg.commerce.csr.order.billing.paymentBalanceEventListener(event);
          });
      });
      atg.commerce.csr.order.billing.initializePaymentContainer("<c:out value='${userOrder.priceInfo.total}'/>", "<c:out value='${userOrder.priceInfo.currencyCode}'/>", { "locale" : "<dsp:valueof bean='AgentUIConfiguration.javaScriptFormattingLocale' />", "currencySymbol" : "<dsp:valueof bean='CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale' />", "places" : "<c:out value='${currencyDecimalPlaces}' />"});
      atg.commerce.csr.order.billing.initializeCreditCardTypeDataContainer();
    </script>

    <dsp:droplet name="ForEach">
      <dsp:param name="array" bean="/atg/commerce/payment/CreditCardTools.cardCodesMap"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="cardType" param="key"/>
        <dsp:getvalueof var="cardCode" param="element"/>
        <script type="text/javascript">
          atg.commerce.csr.order.billing.addCreditCardTypeData("<c:out value='${cardType}'/>", "<c:out value='${cardCode}'/>");
        </script>
      </dsp:oparam>
    </dsp:droplet>

    <svc-ui:frameworkUrl var="successURL" panelStacks="cmcCompleteOrderPS,globalPanels"/>
    <svc-ui:frameworkUrl var="errorURL"/>

    <c:set var="csrBillingFormName" value="csrBillingForm"/>

    <dsp:form id="${csrBillingFormName}" formid="csrBillingForm">
	
	<ul class="atg_dataForm mff_commerce_csr_email">
	 
	 <%--
	<ul class="atg_dataForm atg_commerce_csr_creditClaimForm">
	 --%> 
		<li class="atg_svc_billingClaimCode">
		<span class="atg_commerce_csr_fieldTitle"><label>Contact Email:</label></span>
		</li>
		<li>
		<c:choose>
			<c:when test="${customerProfile.email != null}">
				<c:set var="email" value="${customerProfile.email}"/>
			</c:when>
			<c:otherwise>
				<c:set var="email" value="${userOrder.contactEmail}"/>
			</c:otherwise>
		</c:choose>

		<dsp:input bean="PaymentGroupFormHandler.contactEmail" value="${email}" type="text">
			<dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
			<dsp:tagAttribute name="required" value="true"/>
			<dsp:tagAttribute name="regExp" value="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,})+$"/>
			<dsp:tagAttribute name="trim" value="true"/>
			<dsp:tagAttribute name="invalidMessage" value="invalid email address"/>
			<dsp:tagAttribute name="class" value="email"/>
			<dsp:tagAttribute name="maxlength" value="255"/>
		</dsp:input>
		</li>  
	</ul>
      
      
    <%--This sets the order level payment options. --%>
    <dsp:setvalue bean="PaymentGroupFormHandler.listId"
                  paramvalue="order.id"/>
    <dsp:input bean="PaymentGroupFormHandler.listId"
               beanvalue="PaymentGroupFormHandler.listId" priority="5"
               type="hidden"/>

        <dsp:include src="/panels/order/billing/displayPaymentOptions.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="displayPaymentOptions" param="displayPaymentOptions"/>
        <dsp:param name="paymentGroups" param="paymentGroups"/>
        <dsp:param name="paymentGroupList" bean="PaymentGroupFormHandler.currentList" />
        </dsp:include>

      <dsp:input type="hidden" priority="-10" value="" name="csrHandleApplyPaymentGroups"
                 bean="PaymentGroupFormHandler.applyPaymentGroups"/>

      <dsp:input type="hidden" value="${errorURL}" name="errorURL"
                 bean="PaymentGroupFormHandler.applyPaymentGroupsErrorURL"/>

      <dsp:input type="hidden" value="${successURL}" name="successURL"
                 bean="PaymentGroupFormHandler.applyPaymentGroupsSuccessURL"/>

      <dsp:input type="hidden" priority="-10" value=""
                 name="csrPaymentGroupsPreserveUserInputOnServerSide"
                 bean="PaymentGroupFormHandler.preserveUserInputOnServerSide"/>
                 

      <dsp:input type="hidden" value="false" name="persistOrder" bean="PaymentGroupFormHandler.persistOrder"/>
      <div style="display:none">
        <span class="atg_messaging_requiredIndicator"
              id="orderTotalUIValidatorAlert">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <input type="hidden" name="orderTotalUIValidator"
               dojoType="dijit.form.ValidationTextBox"
               validIf="function () { return atg.commerce.csr.order.billing.isZeroBalance();}"
               inlineIndicator="orderTotalUIValidatorAlert"/>
      </div>

    </dsp:form>
    
    <div class="atg_commerce_csr_billingSummary">
      <div class="atg_commerce_csr_orderModifications">
      <dsp:include src="/include/order/promotionsSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="order" value="${userOrder}"/>
      </dsp:include>
      </div>
      <csr:displayOrderSummary
        order="${userOrder}"
        isShowHeader="false"
        isDisplayBalanceDue="${true}"
        />
      <div class="atg_commerce_csr_billingFooter">
        <fmt:message var="returnToShippingMethodLabel" key="common.returnToShippingMethod"/>
        <fmt:message var="returnToShippingAddressLabel" key="common.returnToShippingAddress"/>
        <fmt:message var="continueToOrderReviewLabel" key="newOrderBilling.continueToOrderReview"/>

        <c:if test="${shippingGroupFormHandler.nonGiftHardgoodShippingGroupCount > 0}">
          <c:set var="goBackStack" value="cmcShippingMethodPS"/>
          <c:set var="goBackLabel" value="${returnToShippingMethodLabel}"/>
        </c:if>
        <c:if test="${shippingGroupFormHandler.nonGiftHardgoodShippingGroupCount == 0}">
          <c:set var="goBackStack" value="cmcShippingAddressPS"/>
          <c:set var="goBackLabel" value="${returnToShippingAddressLabel}"/>
        </c:if>

        <dsp:include src="/include/order/checkoutFooter.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="goBackLabel" value="${goBackLabel}"/>
          <dsp:param name="goBackStack" value="${goBackStack}"/>
          <dsp:param name="nextButtonOnClick"
                     value="atg.commerce.csr.order.billing.applyPaymentGroups({form:'csrBillingForm'});return false;"/>
          <dsp:param name="nextButtonLabel" value="${continueToOrderReviewLabel}"/>
          <dsp:param name="nextButtonFormId" value="${csrBillingFormName}"/>
        </dsp:include>
      </div>
    </div>

    <script type="text/javascript">
      _container_.onLoadDeferred.addCallback(function() {
        atg.commerce.csr.order.billing.assignBalance();
        atg.commerce.csr.order.billing.firePaymentBalanceDojoEvent();
        atg.commerce.csr.order.billing.csrBillingFormValidate();
        atg.service.form.watchInputs('${csrBillingFormName}', atg.commerce.csr.order.billing.csrBillingFormValidate);

        var theButton = document.getElementById("checkoutFooterNextButton");
        if (theButton != null) {
          theButton.focus();
        }
      });
      _container_.onUnloadDeferred.addCallback(function () {
        atg.service.form.unWatchInputs('${csrBillingFormName}');
      });
    </script>
    <script type="text/javascript">
      if (!dijit.byId("editPaymentOptionFloatingPane")) {
        new dojox.Dialog({ id:"editPaymentOptionFloatingPane",
          cacheContent: "false",
          executeScripts:"true",
          scriptHasHooks:"true",
          duration: 100,
          "class":"atg_commerce_csr_popup"});
      }
    </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- end of newOrderBilling.jsp --%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/choosePaymentOptions.jsp#2 $$Change: 1179550 $--%>
