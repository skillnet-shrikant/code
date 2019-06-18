<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>

	<dsp:getvalueof bean="ShoppingCart.current.signatureRequired" var="hasRestrictedItems"/>
	<dsp:getvalueof var="bopis" bean="ShoppingCart.current.bopisOrder"/>
	
	<c:set var="gcHeaderText">Gift Card </c:set>
	<c:set var="gcInputText">Gift Card Number</c:set>
	<c:set var="gcLinkText">+ add gift card</c:set>
	<c:set var="gcClassName">add-gift-card</c:set>
	<c:set var="gcRestrictedText">Unfortunately, Gift Cards are not eligible forms of payment for this item.</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Card / Bonus Bucks</c:set>
		<c:set var="gcInputText">Card Number</c:set>
		<c:set var="gcLinkText">+ add</c:set>
		<c:set var="gcRestrictedText">Unfortunately, Gift Cards/Bonus Bucks are not eligible forms of payment for this item.</c:set>
	</c:if>

 	<%-- BZ 2505 - Using a different class to hide the GC form --%>
	<c:choose>
	  <c:when test="${hasRestrictedItems eq true && bopis eq true}">
	  	<c:set var="gcClassName">gift-card-restricted</c:set>
	  </c:when>
	</c:choose>

	<h3>${gcHeaderText}</h3><a href="#" class="${gcClassName}">${gcLinkText}</a>
	<c:if test="${hasRestrictedItems eq true && bopis eq true}">
		<div id="gift-card-restricted-text" class="restricted-message">
			<h4><span class="icon icon-error"></span> Age Restricted Item</h4>
			<p>
				Item(s) in your order have an age restriction for purchase. This order will require the
				purchaser to be present at time of pick up.
			</p>
			<p>
				${gcRestrictedText}
			</p>
		</div>
	</c:if>
	<dsp:form id="gift-card-form" class="gift-card-form" action="${contextPath}/checkout/checkout.jsp" method="POST"  formid="gift-card-form"  data-validate>
		<div class="field-group">
			<label for="gift-card-number">${gcInputText}</label>
			<dsp:input bean="PaymentGroupFormHandler.giftCardNumber" id="gift-card-number" name="gift-card-number" type="tel" />
		</div>
		<div class="field-group">
			<div class="field">
				<label for="gc-pin">
					Access Number
					<a href="${contextPath}/checkout/ajax/giftCardInfoModal.jsp" class="modal-trigger" data-target="gift-card-info-modal" data-size="small"><span class="icon icon-info"></span></a>
				</label>
				<dsp:input bean="PaymentGroupFormHandler.giftCardPin" type="tel" id="gc-pin" name="gc-pin" class="gc-pin" maxlength="8" />
			</div>
			<div class="field">
				<%-- need this for proper alignment --%>
				<label for="gift-card-submit">&nbsp;</label>
				<dsp:input bean="PaymentGroupFormHandler.giftCardSuccessURL" type="hidden" id="gift-card-success-url" name="gift-card-success-url" value="${contextPath}/checkout/json/updateCartItemSuccess.jsp?isCheckout=true"/>
				<dsp:input bean="PaymentGroupFormHandler.giftCardErrorURL" type="hidden" id="gift-card-error-url" name="gift-card-error-url" value="${contextPath}/checkout/json/giftCardError.jsp"/>
				<dsp:input bean="PaymentGroupFormHandler.applyGiftCard" type="submit" id="gift-card-submit" name="gift-card-submit" iclass="button primary gift-card-submit" value="Apply" />
			</div>
		</div>
	</dsp:form>

	<%-- Remove Gift Card Form --%>
	<dsp:form name="giftCardRemovalForm" action="${contextPath}/checkout/checkout.jsp" method="post" id="giftCardRemovalForm" formid="giftCardRemovalForm">
		<dsp:input type="hidden" bean="PaymentGroupFormHandler.giftCardToRemove" id="giftCardId" value=""/>
		<dsp:input type="hidden" bean="PaymentGroupFormHandler.removeGiftCardSuccessURL" value="${contextPath}/checkout/json/updateCartItemSuccess.jsp?isCheckout=true" />
		<dsp:input type="hidden" bean="PaymentGroupFormHandler.removeGiftCardErrorURL" value="${contextPath}/checkout/json/giftCardError.jsp" />
		<dsp:input type="hidden" bean="PaymentGroupFormHandler.removeGiftCard" id="removeGiftCardSubmit" name="removeGiftCardSubmit" value="submit" />
	</dsp:form>

</dsp:page>
