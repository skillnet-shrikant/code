<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean var="paymentGroupFormHandler" bean="/atg/commerce/custsvc/order/PaymentGroupFormHandler"/>
  <dsp:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>
  <dsp:importbean var="inStorePaymentInitializer" bean="/atg/commerce/custsvc/order/purchase/InStorePaymentInitializer"/>
  
  <dsp:getvalueof var="paymentGroup" param="paymentGroup"/>
  <dsp:getvalueof var="index" param="index"/>
  <dsp:getvalueof var="userOrder" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>


  <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
    <dsp:param name="currencyCode" value="${userOrder.priceInfo.currencyCode}"/>
    <dsp:oparam name="output">
       <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <c:set var="paymentOptionExpired" value="false"/>
    <dsp:getvalueof var="pgType" param="paymentGroup.paymentGroupClassType"/>
    <c:choose>
      <c:when test="${pgType == 'creditCard'}">
        <dsp:droplet name="/atg/commerce/custsvc/order/CreditCardIsExpired">
          <dsp:param name="creditCard" value="${paymentGroup }"/>
          <dsp:oparam name="true">
            <c:set var="paymentOptionExpired" value="true"/>
            <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function() {
                atg.commerce.csr.order.billing.disableExpiredCreditCardControls({
                  paymentWidgetId : 'csrBillingForm_${paymentGroup.id}',
                  cvv : 'csrBillingForm_${paymentGroup.id}CVV'
                });
              });
            </script>
          </dsp:oparam>
        </dsp:droplet>
        <td class="atg_numberValue atg_messaging_requiredIndicator atg_commerce_csr_securityCode"
            id="${paymentGroup.id}CVVAlert">
          <dsp:input id="${paymentGroup.id}CVV"
                     bean="PaymentGroupFormHandler.currentList[param:index].creditCardVerificationNumber"
                     type="text" size="4" maxlength="4">
            <dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox"/>
          </dsp:input>
        </td>
      </c:when>
      <c:when test="${pgType == 'inStorePayment'}">
        <td class="atg_numberValue"></td>
      </c:when>
      <c:otherwise>
        <td class="atg_numberValue"></td>
      </c:otherwise>
    </c:choose>

    <td class="atg_numberValue atg_messaging_requiredIndicator atg_commerce_csr_billingAmount"
        id="${paymentGroup.id}Alert">
        <c:choose>
          <c:when test="${pgType == 'inStorePayment'}">
            <div>
              <c:if test="${inStorePaymentInitializer.allowInStorePaymentWhenOtherShippingGroupTypesExist && paymentGroupFormHandler.currentList[index].amount == 0}">
                <c:set target="${paymentGroupFormHandler.currentList[index]}" property="amount" value="${userOrder.priceInfo.total}"/>
              </c:if>

              <csr:formatNumber var="currentAmount" type="currency" currencyCode="${userOrder.priceInfo.currencyCode}" value="${paymentGroupFormHandler.currentList[index].amount}"/>

              <input type="checkbox" id="${paymentGroup.id}_checkbox" onchange="atg.commerce.csr.order.billing.checkInStorePaymentCheckbox(this, '${paymentGroup.id}', '${paymentGroup.id}_text', '${paymentGroupFormHandler.currentList[index].amount}', '${currentAmount}');"
              onclick="atg.commerce.csr.order.billing.checkInStorePaymentCheckbox(this, '${paymentGroup.id}', '${paymentGroup.id}_text', '${paymentGroupFormHandler.currentList[index].amount}', '${currentAmount}');" />
              <img src="${CSRConfigurator.contextRoot}/images/icons/icon_applyRemainder.gif" style="cursor:pointer"
               title="<fmt:message key="newOrderBilling.displayPaymentMethods.link.applyRemainder.title" />"
                onclick="atg.commerce.csr.order.billing.applyRemainder
                (
                {
                pmtWidget: dijit.byId('${paymentGroup.id}')
                }
                );"/>
              <span id="${paymentGroup.id}_text">
                0
              </span>
              &nbsp;
              <span id="ea_csc_instore_pickup_billing_logic"></span>
            </div>
            <dsp:input bean="PaymentGroupFormHandler.currentList[param:index].amount"
                       type="hidden" id="${paymentGroup.id}"
                       size="6" maxlength="20">
              <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx"/>
              <dsp:tagAttribute name="required" value="true"/>
              <dsp:tagAttribute name="currency" value="${userOrder.priceInfo.currencyCode}"/>
              <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
              <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
              <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
            </dsp:input>
            <dsp:input bean="PaymentGroupFormHandler.currentList[param:index].relationshipType"
                       type="hidden" id="${paymentGroup.id}_relationshipType"
                       size="6" maxlength="20">
            </dsp:input>
          </c:when>
          <c:otherwise>
            <c:if test="${!paymentOptionExpired}">
              <img src="${CSRConfigurator.contextRoot}/images/icons/icon_applyRemainder.gif" style="cursor:pointer"
                   title="<fmt:message key="newOrderBilling.displayPaymentMethods.link.applyRemainder.title" />"
              onclick="atg.commerce.csr.order.billing.applyRemainder
              (
              {
              pmtWidget: dijit.byId('${paymentGroup.id}')
              }
              );"/>
            </c:if>
            <dsp:input bean="PaymentGroupFormHandler.currentList[param:index].amount"
                       type="text" id="${paymentGroup.id}"
                       size="6" maxlength="20"
                       onkeyup="atg.commerce.csr.order.billing.recalculatePaymentBalance({pmtWidget: this});">
              <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx"/>
              <dsp:tagAttribute name="required" value="true"/>
              <dsp:tagAttribute name="currency" value="${userOrder.priceInfo.currencyCode}"/>
              <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
              <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
              <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
            </dsp:input>
          </c:otherwise>
        </c:choose>
    </td>

  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/displayPmtCenterFrag.jsp#2 $$Change: 1179550 $--%>