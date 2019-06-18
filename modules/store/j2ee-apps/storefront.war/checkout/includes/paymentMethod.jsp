<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:getvalueof bean="ShoppingCart.current.signatureRequired" var="hasRestrictedItems"/>
	<dsp:getvalueof var="bopis" bean="ShoppingCart.current.bopisOrder"/>

	<%-- section title --%>
	<h3>Credit Card</h3>
	<div class="required-note">* Required</div>

	 <%-- BZ 2505 --%>
	<c:choose>
	  <c:when test="${hasRestrictedItems eq true && bopis eq true}">
		<div class="restricted-message">
			<h4><span class="icon icon-error"></span> Age Restricted Item</h4>
			<p>The name on the card provided below <b><u>must</u></b> match the ID of the purchaser at time of pickup for this order to be released.</p>
		</div>
	  </c:when>
	</c:choose>

	<c:choose>
		<c:when test="${userIsAuthenticated && hasProfileCard}">
			<%-- saved payment methods --%>
			<dsp:include page="/checkout/includes/savedPaymentMethods.jsp" />
		</c:when>
		<c:otherwise>
			<%-- new payment method --%>
			<dsp:input type="hidden" bean="PaymentGroupFormHandler.creditCardId" value="0" id="payment-method-new" name="payment-method" checked="true"/>

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
					<dsp:input type="text" bean="PaymentGroupFormHandler.paymentName" maxlength="42" id="payment-name" name="payment-name" autocapitalize="off" data-validation="required nameField" data-fieldname="Payment Name" placeholder="ex: John's Visa" value=""/>
				</div>
			</c:if>

		</c:otherwise>
	</c:choose>

</dsp:page>
