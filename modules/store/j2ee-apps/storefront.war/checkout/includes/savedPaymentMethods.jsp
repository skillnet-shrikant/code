<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager" />
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="reverseCardCodeMap" bean="CreditCardTools.reverseCardCodeMap"/>
	<dsp:getvalueof var="creditCardId" bean="CheckoutManager.creditCardId" />
	<dsp:getvalueof var="defaultCreditCardId" bean="CheckoutManager.creditCardId" />
	<c:if test="${empty defaultCreditCardId}">
		<dsp:getvalueof var="defaultCreditCardId" bean="Profile.defaultCreditCard.repositoryId" />
	</c:if>

	<dsp:droplet name="MapToArrayDefaultFirst">
		<dsp:param name="map" bean="Profile.creditCards" />
		<dsp:param name="defaultId" value="${defaultCreditCardId}" />
		<dsp:oparam name="output">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="sortedArray"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="count" param="count"/>
					<dsp:getvalueof var="size" param="size"/>
					<dsp:getvalueof var="defaultCardId" value="${defaultCreditCardId}" />
					<dsp:getvalueof var="creditCardType" param="element.value.creditCardType"/>
					<dsp:getvalueof var="cardId" param="element.value.id"/>
					<c:set var="checked" value="false" />
					<c:if test="${defaultCardId == cardId or (creditCardId != '0' and count==1)}">
						<c:set var="checked" value="true" />
					</c:if>

					<%-- radio button --%>
					<div class="radio">
						<label for="payment-method-${count}">
							<dsp:input type="radio" id="payment-method-${count}" name="payment-method" bean="PaymentGroupFormHandler.creditCardId" value="${cardId}" checked="${checked}" />
							<div class="card">
								<div class="card-title">
									<dsp:valueof param="element.key"/>
								</div>
								<div class="card-content">
									<p><dsp:valueof param="element.value.nameOnCard"/></p>
									<p><c:out value="${reverseCardCodeMap[creditCardType]}"/></p>
									<p>XXXX&nbsp;XXXX&nbsp;XXXX<dsp:valueof param="element.value.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
									<p><strong>Exp:</strong>&nbsp;<dsp:valueof param="element.value.expirationMonth"/>/<dsp:valueof param="element.value.expirationYear"/></p>
									<div class="saved-card-cvv">
										<label for="cvv-${count}">* CVV <a href="${contextPath}/checkout/ajax/cvvModal.jsp" class="modal-trigger" data-target="cvv-modal" data-size="small"><span class="icon icon-info"></span></a></label>
										<input type="tel" id="cvv-${count}" name="cvv-${count}" class="cvv-input" autocapitalize="off" data-validation="required numeric minlength" data-fieldname="CVV" placeholder="CVV" min-length="3" maxlength="4" value=""/>
									</div>
								</div>
							</div>
						</label>
					</div>

				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<div class="radio payment-method-new">
		<label for="payment-method-new">
			<dsp:input type="radio" bean="PaymentGroupFormHandler.creditCardId" value="0" id="payment-method-new" name="payment-method" />
			<span class="label">New Credit Card</span>
			<div class="new-payment-method">

				<%-- new payment method form --%>
				<picture>
					<!--[if IE 9]><video style="display: none;"><![endif]-->
					<source srcset="${contextPath}/resources/images/credit-cards-large.jpg" media="(min-width: 768px)">
					<!--[if IE 9]></video><![endif]-->
					<img src="${contextPath}/resources/images/credit-cards.jpg" class="cc-images" alt="credit cards accepted" />
				</picture>
				<dsp:include page="/checkout/includes/paymentMethodForm.jsp" />

				<%-- billing address --%>
				<dsp:include page="/checkout/includes/billingAddress.jsp" />

				<c:if test="${userIsAuthenticated}">
					<%-- save payment method to account --%>
					<div class="field-group">
						<div class="checkbox">
							<label for="save-payment-method">
								<dsp:input type="checkbox" bean="PaymentGroupFormHandler.savePaymentMethod" id="save-payment-method" name="save-payment-method" checked="false" /> Save Payment Method
							</label>
						</div>
					</div>
					<div class="field-group payment-name">
						<label for="payment-name">* Payment Name</label>
						<dsp:input id="payment-name" name="payment-name" bean="PaymentGroupFormHandler.paymentName" maxlength="42" type="text" autocapitalize="off" data-validation="required nameField" data-fieldname="Payment Name" placeholder="ex: John's Visa" value=""/>
					</div>
				</c:if>

			</div>
		</label>
	</div>

</dsp:page>
