<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	
	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>
	
	<c:set var="gcHeaderText">Gift Cards</c:set>
	<c:set var="gcPayLabel">Gift Cards</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Card / Bonus Bucks</c:set>
		<c:set var="gcPayLabel">Gift Cards / Bonus Bucks</c:set>
	</c:if>
		
	<dsp:getvalueof var="isExpressCheckout" bean="CheckoutManager.expressCheckout" vartype="boolean" />

	<%-- Page Parameters --%>
	<dsp:param bean="ShoppingCart.current.contactEmail" name="email"/>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" bean="ShoppingCart.current.paymentGroups"/>
		<dsp:param name="elementName" value="paymentGroup"/>
		<dsp:oparam name="outputStart">
			<div class="gift-card-review">
				<h3>${gcHeaderText}</h3>
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
			<c:if test="${paymentClassType == 'giftCard'}">
				<dsp:getvalueof var="giftCardId" param="paymentGroup.cardNumber"/>
				<div class="applied-gift-card">
					<div class="gift-card-number-applied">${giftCardId}</div>
					<div class="gift-card-amount-applied amount-${giftCardId}"><dsp:valueof param="paymentGroup.amount" converter="currency" /></div>
				</div>
			</c:if>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			</div>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" bean="ShoppingCart.current.paymentGroups"/>
		<dsp:param name="elementName" value="paymentGroup"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
				<c:if test="${paymentClassType == 'creditCard'}">
					<dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
					<dsp:getvalueof var="reverseCardCodeMap" bean="CreditCardTools.reverseCardCodeMap"/>
					<div class="credit-card-review">
						<h3>Credit Card</h3>
						<p><dsp:valueof param="paymentGroup.nameOnCard"/></p>
						<p><c:out value="${reverseCardCodeMap[creditCardType]}"/></p>
						<p>XXXX&nbsp;XXXX&nbsp;XXXX<dsp:valueof param="paymentGroup.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
						<p><strong>Exp:</strong>&nbsp;<dsp:valueof param="paymentGroup.expirationMonth"/>/<dsp:valueof param="paymentGroup.expirationYear"/></p>
						<c:if test="${isExpressCheckout}">
							<label for="cvv">* CVV <a href="${contextPath}/checkout/ajax/cvvModal.jsp" class="modal-trigger" data-target="cvv-modal" data-size="small"><span class="icon icon-info"></span></a></label>
							<dsp:input type="tel" bean="CommitOrderFormHandler.cardVerificationNum" name="cvv" id="cvv" value=""/>
						</c:if>
					</div>
					<div class="billing-address-review">
						<h3>Billing Address</h3>
						<p><dsp:valueof param="paymentGroup.billingAddress.firstName" />&nbsp;<dsp:valueof param="paymentGroup.billingAddress.lastName" /></p>
						<p><dsp:valueof param="paymentGroup.billingAddress.address1" /></p>
						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" param="paymentGroup.billingAddress.address2"/>
							<dsp:oparam name="false">
								<p><dsp:valueof param="paymentGroup.billingAddress.address2" /></p>
							</dsp:oparam>
						</dsp:droplet>
						<p><dsp:valueof param="paymentGroup.billingAddress.city" />,&nbsp;<dsp:valueof param="paymentGroup.billingAddress.state" />&nbsp;<dsp:valueof param="paymentGroup.billingAddress.postalCode" /></p>
						<p>
							<dsp:getvalueof var="phone" param="paymentGroup.billingAddress.phoneNumber" />
							<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
						</p>
					</div>
				</c:if>
		</dsp:oparam>
	</dsp:droplet>

	<div class="email-address-review">
		<h3>Email Address</h3>
		<p><dsp:valueof param="email"/></p>
	</div>

</dsp:page>
