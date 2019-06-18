<%-- This page is used to add gift card as payment option. --%>
<%@  include file="/include/top.jspf"%> 
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupFormHandler" />
  <dsp:importbean var="urlDroplet" bean="/atg/svc/droplet/FrameworkUrlDroplet" />
  <fmt:bundle basename="atg.commerce.csr.order.WebAppResources">
  
  <div>
	<dsp:droplet name="FrameworkUrlDroplet">
		<dsp:param name="panelStacks" value="cmcBillingPS" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="successURL" bean="FrameworkUrlDroplet.url" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="userOrder"  bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>
	<c:set var="checkOMSOrderState" value="${userOrder.stateAsString}"/>
	<c:if test="${checkOMSOrderState != 'SHIPPED'}">
		<dsp:form id="csrBillingAddGiftCardForm" onsubmit="return false" formid="csrBillingAddGiftCardForm">
			<ul class="atg_dataForm atg_commerce_csr_creditClaimForm" >
			<li>
				<span class="atg_commerce_csr_fieldTitle">
				<label>
					Gift Card number
				</label>
				</span>
				<fmt:message var="gcNumberRequired" key="newOrderBilling.gcNumber.required"/>
				<dsp:input bean="PaymentGroupFormHandler.giftCardNumber" type="text" size="20" maxlength="16" id="giftCardNumber">
					<dsp:tagAttribute name="tabindex" value="1"/>
					<dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox" /> 
					<dsp:tagAttribute name="required" value="true" />
					<dsp:tagAttribute name="regExp" value="^([0-9]{13}|[0-9]{16}|[0-9]{19})$"/>
					<dsp:tagAttribute name="trim" value="true" />
					<dsp:tagAttribute name="promptMessage" value="${gcNumberRequired}" />
				</dsp:input>
			</li>
			<li>
				<span class="atg_commerce_csr_fieldTitle">
				<label>
					Access number
				</label>
				</span>
				<fmt:message var="gcAccessNumberRequired" key="newOrderBilling.gcAccessNumber.required"/>
				<dsp:input bean="PaymentGroupFormHandler.giftCardAccessNumber" type="text" size="10" maxlength="8" id="accessNumber">
					<dsp:tagAttribute name="tabindex" value="2"/>
					<dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox" /> 
					<dsp:tagAttribute name="required" value="true" />
					<dsp:tagAttribute name="regExp" value="^([0-9]){1,8}$"/>
					<dsp:tagAttribute name="trim" value="true" />
					<dsp:tagAttribute name="promptMessage" value="${gcAccessNumberRequired}" />
				</dsp:input>
			</li>
			<li>
				<c:set var="fnString" value="atg.commerce.csr.order.billing.saveUserInputAndCallback({mode :'claimClaimables'});"></c:set>
				<input type="button" name="billingGiftCardSubmit" value="Add" onclick="<c:out value='${fnString}'/>" dojoType="atg.widget.validation.SubmitButton"/>
				<c:set var="orderType"><dsp:valueof bean="/atg/commerce/custsvc/order/ShoppingCart.current.orderClassType"/></c:set> 
				<input type="hidden" name="orderType" value="${orderType}" id="orderType"/>
			</li>
			</ul>
	<script type="text/javascript">		
	  atg.commerce.csr.order.billing.saveUserInputAndCallback = function (pParams) {
		  atgSubmitAction({form:dojo.byId("csrBillingAddGiftCardForm")});
	   };
        </script>

        <dsp:input bean="PaymentGroupFormHandler.giftCardSuccessURL" type="hidden" value="${successURL }" />
        <dsp:input bean="PaymentGroupFormHandler.giftCardErrorURL" type="hidden" value="${successURL }" />
        <dsp:input bean="PaymentGroupFormHandler.claimItemSuccessURL" type="hidden" value="${successURL }" />
        <dsp:input bean="PaymentGroupFormHandler.claimItemErrorURL" type="hidden" value="${successURL }" />
        <dsp:input bean="PaymentGroupFormHandler.claimGiftCard" type="hidden" value="" priority="-10" />

        <script type="text/javascript">
          dojo.addOnLoad(function() {
            atg.keyboard.registerDefaultEnterKey({form:"csrBillingAddGiftCardForm",name:"billingClaimCode"}, 
              "csrBillingAddGiftCardForm_billingGiftCardSubmit", "buttonClick");
          });
        </script>
      </dsp:form>
</c:if>	  

	</div>

  </fmt:bundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
