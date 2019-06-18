<%-- FIXME: Add page comments

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/claimClaimables.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler"/>
  <dsp:importbean var="urlDroplet" bean="/atg/svc/droplet/FrameworkUrlDroplet"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <div>
      <dsp:droplet name="FrameworkUrlDroplet">
        <dsp:param name="panelStacks" value="cmcBillingPS"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="successURL" bean="FrameworkUrlDroplet.url"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:form id="csrBillingClaimableForm" onsubmit="return false" formid="csrBillingClaimableForm">
        <ul class="atg_dataForm atg_commerce_csr_creditClaimForm">
          <li class="atg_svc_billingClaimCode">
            <span class="atg_commerce_csr_fieldTitle">
              <label>
                <fmt:message key="newOrderBilling.claimClaimables.title"/>
              </label>
            </span>
            <fmt:message var="claimCodeRequired" key="newOrderBilling.claimClaimables.required"/>
            <dsp:input bean="PaymentGroupFormHandler.claimCode" type="text" size="25" maxlength="40"
                       name="billingClaimCode">
              <dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox"/>
              <dsp:tagAttribute name="required" value="true"/>
              <dsp:tagAttribute name="trim" value="true"/>
              <dsp:tagAttribute name="promptMessage" value="${claimCodeRequired}"/>
            </dsp:input>
          </li>
          <li>
            <%-- Before cliaming the claimables, save the user input in the page such as amount.--%>

            <c:set var="fnString"
                   value="atg.commerce.csr.order.billing.saveUserInputAndClaimClaimables();return false;"></c:set>
            <input type="button"
                   name="billingClaimSubmit"
                   onclick="<c:out value='${fnString}'/>"
                   dojoType="atg.widget.validation.SubmitButton"
                   value="<fmt:message key='newOrderBilling.claimClaimables.button.claim' />"/>
          </li>
        </ul>
        <dsp:input bean="PaymentGroupFormHandler.claimItemSuccessURL" type="hidden" value="${successURL }"/>
        <dsp:input bean="PaymentGroupFormHandler.claimItemErrorURL" type="hidden" value="${successURL }"/>
        <dsp:input bean="PaymentGroupFormHandler.claimItem" type="hidden" value="" priority="-10"/>
        <script type="text/javascript">
          _container_.onLoadDeferred.addCallback(function() {
            atg.keyboard.registerDefaultEnterKey({form:"csrBillingClaimableForm", name:"billingClaimCode"},
              dijit.byNode(dojo.byId("csrBillingClaimableForm")["billingClaimSubmit"]), "buttonClick");
          });
          _container_.onUnloadDeferred.addCallback(function() {
            atg.keyboard.unRegisterDefaultEnterKey({form:"csrBillingClaimableForm",name:"billingClaimCode"});
          });

          atg.commerce.csr.order.billing.saveUserInputAndClaimClaimables = function() {
            var returnValue = atg.commerce.csr.order.billing.saveUserInput();
            if (returnValue == true) {
              atg.commerce.csr.order.billing.claimClaimables();
            }
            ;
          }

        </script>
      </dsp:form>
    </div>
    
    <div>
      <dsp:droplet name="FrameworkUrlDroplet">
        <dsp:param name="panelStacks" value="cmcBillingPS"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="successURL" bean="FrameworkUrlDroplet.url"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:form id="csrShippingAdjustmentForm" onsubmit="return false" formid="csrShippingAdjustmentForm">
        <ul class="atg_dataForm atg_commerce_csr_creditClaimForm">
          <li class="atg_svc_billingClaimCode">
            <span class="atg_commerce_csr_fieldTitle">
              <label>
                Shipping Amount : 
              </label>
            </span>
            
            <dsp:input bean="CartModifierFormHandler.shippingAdjustmentAmount" type="text" size="10" maxlength="10" name="shippingAdjustmentAmount">
            </dsp:input>
          </li>
          <li>
            <input type="button" name="shippingAdjustmentButton" id="shippingAdjustmentButton" name="submitShippingAdjustment" onclick="submitShippingAdjustment"
                   dojoType="atg.widget.validation.SubmitButton" value="Apply"/>
          </li>
        </ul>
        <dsp:input bean="CartModifierFormHandler.shippingAdjustmentSuccessURL" type="hidden" value="${successURL }"/>
        <dsp:input bean="CartModifierFormHandler.shippingAdjustmentErrorURL" type="hidden" value="${successURL }"/>
        <dsp:input bean="CartModifierFormHandler.shippingAdjustment" type="hidden" value="" priority="-10"/>
        <script type="text/javascript">
			function submitShippingAdjustment() {
				atgSubmitAction({
					form : dojo.byId("csrShippingAdjustmentForm")
				});
			}
		</script>
      </dsp:form>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/claimClaimables.jsp#1 $$Change: 946917 $--%>
