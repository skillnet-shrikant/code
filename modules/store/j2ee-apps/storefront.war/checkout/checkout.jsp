<%--
	- File Name: checkout.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Main checkout page view
	- Parameters:
	-
	--%>
<dsp:page>

	<%-- Authentication Check --%>
	<%@include file="/checkout/fragments/secureRedirect.jspf"%>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="bopis" bean="ShoppingCart.current.bopisOrder"/>
	<dsp:getvalueof var="bopisStore" bean="ShoppingCart.current.bopisStore"/>
	<dsp:getvalueof var="fflOrder" bean="ShoppingCart.current.fflOrder" vartype="boolean" />
	<dsp:getvalueof var="isExpressCheckout" bean="CheckoutManager.expressCheckout" vartype="boolean" />
	<c:set var="progressShipping" value="in-progress" scope="request" />
	<c:set var="progressPayment" value="" scope="request" />
	<c:set var="progressReview" value="" scope="request" />
	<c:set var="styleShipping" value="" scope="request" />
	<c:set var="styleReview" value="" scope="request" />
	<c:if test="${isExpressCheckout}">
		<c:set var="progressShipping" value="complete" scope="request" />
		<c:set var="progressPayment" value="complete" scope="request" />
		<c:set var="progressReview" value="in-progress" scope="request" />
		<c:set var="styleShipping" value='style="display:none;"' scope="request" />
		<c:set var="styleReview" value='style="display:block;"' scope="request" />
	</c:if>

	<%-- is user authenticated --%>
	<dsp:droplet name="Switch">
		<dsp:param bean="Profile.hardLoggedIn" name="value"/>
		<dsp:oparam name="true">
			<c:set var="userIsAuthenticated" value="true" scope="request" />
		</dsp:oparam>
		<dsp:oparam name="false">
			<c:set var="userIsAuthenticated" value="false" scope="request" />
		</dsp:oparam>
	</dsp:droplet>

	<%-- Check if user has any shipping addresses saved --%>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value"  bean="Profile.secondaryAddresses"/>
		<dsp:oparam name="true">
			<c:set var="hasProfileAddress" value="false" scope="request" />
		</dsp:oparam>
		<dsp:oparam name="false">
			<c:set var="hasProfileAddress" value="true" scope="request" />
		</dsp:oparam>
	</dsp:droplet>

	<%-- Check if user has any credit card --%>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value"  bean="Profile.creditCards"/>
		<dsp:oparam name="true">
			<c:set var="hasProfileCard" value="false" scope="request" />
		</dsp:oparam>
		<dsp:oparam name="false">
			<c:set var="hasProfileCard" value="true" scope="request" />
		</dsp:oparam>
	</dsp:droplet>

	<layout:checkout>
		<jsp:attribute name="pageTitle">Checkout Page</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="pageType">checkout</jsp:attribute>
		<jsp:attribute name="bodyClass">checkout</jsp:attribute>
		<jsp:body>

			<div class="section-title">
				<h1>Checkout</h1>
			</div>

			<c:set var="shippingStepTitle" value="Shipping" scope="request" />
			<c:if test="${bopis}">
				<c:set var="shippingStepTitle" value="Store Pick-Up" scope="request" />
			</c:if>

			<%-- checkout progress bar --%>
			<div class="checkout-progress">
				<div class="checkout-progress-steps">
					<div class="checkout-progress-shipping ${progressShipping}">
						<span>1</span>${shippingStepTitle}
					</div>
					<div class="checkout-progress-payment ${progressPayment}">
						<span>2</span>Payment
					</div>
					<div class="checkout-progress-review ${progressReview}">
						<span>3</span>Review
					</div>
				</div>
			</div>

			<div class="checkout-columns">

				<%-- checkout steps --%>
				<div class="checkout-left-column">

					<%-- shipping step --%>
					<div class="checkout-shipping ${progressShipping}" id="checkout-shipping" ${styleShipping}>
						<c:choose>
							<c:when test="${bopis}">

								<div class="checkout-title">
									<h2>Store Pick-Up Information</h2>
								</div>

								<%-- bopis checkout forms and panels --%>
								<div class="bopis-checkout">
									<dsp:form formid="bopis-person-form" id="bopis-person-form" name="bopis-person-form" method="POST" data-validate>
										<%-- bopis review panel --%>
										<dsp:include page="/checkout/includes/bopisReview.jsp">
											<dsp:param name="storeId" value="${bopisStore}"/>
											<dsp:param name="firstStep" value="true"/>
										</dsp:include>

										<%-- bopis pickup person form --%>
										<div class="bopis-person-form">
											<dsp:include page="/checkout/includes/bopisPerson.jsp" />
										</div>

										<%-- save person picking up order and continue --%>
										<div class="field-group save-and-continue">
											<dsp:input type="hidden" bean="ShippingGroupFormHandler.bopisShippingSuccessURL" value="${contextPath}/checkout/json/bopisShippingSuccess.jsp" />
											<dsp:input type="hidden" bean="ShippingGroupFormHandler.bopisShippingErrorURL" value="${contextPath}/checkout/json/bopisShippingError.jsp" />
											<dsp:input id="bopis-person-submit" name="bopis-person-submit" bean="ShippingGroupFormHandler.applyBopis" type="submit" iclass="button primary expand disabled bopis-continue" value="Save and Continue" />
										</div>
									</dsp:form>
								</div>

							<%-- hidden form for switching to shipMyOrder instead of store pickup--%>
							<dsp:form formid="ship-my-order-form" id="ship-my-order-form" name="ship-my-order-form" action="${contextPath}/checkout/checkout.jsp" method="post">
								<dsp:input type="hidden" bean="ShippingGroupFormHandler.shipMyOrderSuccessURL" value="${contextPath}/sitewide/json/shipMyOrderSuccess.jsp" />
								<dsp:input type="hidden" bean="ShippingGroupFormHandler.shipMyOrderErrorURL" value="${contextPath}/sitewide/json/shipMyOrderErrorCheckout.jsp" />
								<dsp:input type="hidden" bean="ShippingGroupFormHandler.shipMyOrder" id="ship-my-order" name="ship-my-order" value="submit" />
							</dsp:form>

							</c:when>
							<c:otherwise>
								<div class="checkout-title">
									<h2>Shipping Information</h2><a href="#" class="edit-shipping-address">edit</a>
								</div>

								<%-- shipping review panel --%>
								<div class="shipping-address-review-panel">
									<div class="shipping-address-review"></div>
									<div class="shipping-method-review"></div>
								</div>

								<%-- shipping address form --%>
								<div class="shipping-address">

									<%-- shipping errors --%>
									<div class="shipping-error-messages">
										<dsp:include page="/sitewide/includes/errors/formErrors.jsp" >
											<dsp:param name="formhandler" bean="ShippingGroupFormHandler"/>
										</dsp:include>
									</div>

									<dsp:form id="shipping-address-form" formid="shipping-address-form" class="shipping-address-form" method="POST" data-validate>
										<%-- set hidden input for bopis flag --%>
										<div class="shipping-address-section">
											<c:choose>
												<%-- ffl dealer info --%>
												<c:when test="${fflOrder}">
													<%@ include file="/checkout/includes/fflDealerInfo.jsp"%>
												</c:when>
												<c:otherwise>
													<%-- shipping address --%>
													<%@ include file="/checkout/includes/shippingAddress.jsp"%>
												</c:otherwise>
											</c:choose>
										</div>
										<%-- save shipping info and continue --%>
										<div class="field-group save-and-continue">
											<dsp:input id="ship-address-error-url" name="ship-address-error-url" type="hidden" bean="ShippingGroupFormHandler.avsShippingErrorURL" value="${contextPath}/checkout/json/avsShippingAddressError.jsp"/>
											<dsp:input id="ship-address-success-url" name="ship-address-success-url" type="hidden" bean="ShippingGroupFormHandler.avsShippingSuccessURL" value="${contextPath}/checkout/json/avsShippingAddressSuccess.jsp"/>
											<dsp:input id="address-submit" name="address-submit" type="submit" bean="ShippingGroupFormHandler.applyShippingGroups" iclass="button primary expand disabled shipping-address-continue" value="Save and Continue" />
										</div>
									</dsp:form>
								</div>

								<%-- shipping method --%>
								<div class="shipping-method">
									<dsp:form id="shipping-method-form" formid="shipping-method-form" class="shipping-method-form" method="POST" data-validate>

										<div class="shipping-method-fields">
											<dsp:include page="/checkout/includes/shippingMethod.jsp" />
										</div>

										<%-- save shipping method and continue --%>
										<div class="field-group save-and-continue">
											<dsp:input id="ship-method-error-url" name="ship-method-error-url" type="hidden" bean="ShippingGroupFormHandler.shipMethodErrorURL" value="${contextPath}/checkout/json/shipMethodError.jsp"/>
											<dsp:input id="ship-method-success-url" name="ship-method-success-url" type="hidden" bean="ShippingGroupFormHandler.shipMethodSuccessURL" value="${contextPath}/checkout/json/shipMethodSuccess.jsp"/>
											<dsp:input id="method-submit" name="method-submit" type="submit" bean="ShippingGroupFormHandler.applyShippingMethod" iclass="button primary expand disabled shipping-method-continue" value="Save and Continue" />
										</div>

									</dsp:form>
								</div>

							</c:otherwise>
						</c:choose>

					</div>

					<dsp:droplet name="/atg/commerce/order/purchase/InitializePaymentDroplet">
					<c:choose>
					<c:when test="${isExpressCheckout}">
						<dsp:param name="newCard" value="false"/>
						<dsp:param name="reprice" value="false"/>
					</c:when>
					<c:otherwise>
						<dsp:param name="newCard" value="true"/>
						<dsp:param name="reprice" value="true"/>
						<dsp:param name="repriceOperation" value="ORDER_TOTAL"/>
					</c:otherwise>
					</c:choose>
						<dsp:oparam name="success">
							<%-- billing step --%>
							<div class="checkout-payment ${progressPayment}" id="checkout-payment">
								<div class="checkout-title">
									<h2>Payment Information</h2>
								</div>

								<%-- gift card --%>
								<dsp:droplet name="/atg/dynamo/droplet/Switch">
									<dsp:param name="value" bean="CheckoutManager.orderRequiresCreditCard"/>
									<dsp:oparam name="true">
										<c:set var="gcInfoStyle" value="" />
									</dsp:oparam>
									<dsp:oparam name="false">
										<c:set var="gcInfoStyle" value="style='display:none'" />
									</dsp:oparam>
								</dsp:droplet>

								<div class="gift-card-info" id="checkout-gift-card" ${gcInfoStyle}>
									<dsp:include page="/checkout/includes/giftCard.jsp" />
								</div>
								<div class="gift-card-review-panel">
									<c:set var="hasGiftCards" value="false" scope="request" />
									<dsp:droplet name="ForEach">
										<dsp:param name="array" bean="ShoppingCart.current.paymentGroups"/>
										<dsp:param name="elementName" value="paymentGroup"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
											<c:if test="${paymentClassType == 'giftCard'}">
												<c:set var="hasGiftCards" value="true" scope="request" />
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
									<c:if test="${hasGiftCards}">
										<dsp:include page="/checkout/includes/giftCardReview.jsp" />
									</c:if>
								</div>

								<%-- payment methods --%>
								<div class="payment-info">

									<div class="payment-error-messages">
									<dsp:include page="/sitewide/includes/errors/formErrors.jsp" >
										<dsp:param name="formhandler" bean="PaymentGroupFormHandler"/>
									</dsp:include>
									</div>

									<dsp:form id="payment-info-form" formid="payment-info-form" class="payment-info-form" method="POST" data-validate>
										<%-- hide credit card section if gift cards cover order --%>
										<dsp:droplet name="Switch">
											<dsp:param name="value" bean="CheckoutManager.orderRequiresCreditCard"/>
											<dsp:oparam name="false">
												<c:set var="paymentInfoStyle" value='style="display:none"' scope="request" />
											</dsp:oparam>
											<dsp:oparam name="true">
												<c:set var="paymentInfoStyle" value="" scope="request" />
											</dsp:oparam>
										</dsp:droplet>

										<%-- credit cards --%>
										<div class="payment-method-section" ${paymentInfoStyle}>
											<dsp:include page="/checkout/includes/paymentMethod.jsp" />
										</div>

										<%-- email address --%>
										<dsp:include page="/checkout/includes/emailAddress.jsp" />

										<%-- save payment info and continue --%>
										<div class="field-group save-and-continue">
											<input value="" type="hidden" id="captureRedShieldDeviceId" name="captureRedShieldDeviceId"/>
											<dsp:input id="billing-address-error-url" name="billing-address-error-url" type="hidden" bean="PaymentGroupFormHandler.avsBillingErrorURL" value="${contextPath}/checkout/json/avsBillingError.jsp"/>
											<dsp:input id="billing-address-success-url" name="billing-address-success-url" type="hidden" bean="PaymentGroupFormHandler.avsBillingSuccessURL" value="${contextPath}/checkout/json/avsBillingSuccess.jsp"/>
											<dsp:input id="payment-submit" name="payment-submit" type="submit" class="button primary payment-continue disabled" bean="PaymentGroupFormHandler.applyPayments" value="Save and Continue" />
										</div>
									</dsp:form>
								</div>

							</div>
						</dsp:oparam>
						<dsp:oparam name="error">
						</dsp:oparam>
						<dsp:oparam name="invalidAccess">
							<dsp:droplet name="Redirect">
								<dsp:param name="url" value="/checkout/checkout.jsp"/>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>

					<%-- review / place order step --%>
					<div class="checkout-review ${progressReview}" id="checkout-review" ${styleReview}>

						<dsp:form id="submitOrderForm" name="review-panel-form" formid="review-panel-form" action="orderConfirmation.jsp" method="post">
							<dsp:input type="hidden" bean="CommitOrderFormHandler.commitOrderSuccessURL" value="${contextPath}/checkout/json/submitOrderSuccess.jsp" />
							<dsp:input type="hidden" bean="CommitOrderFormHandler.commitOrderErrorURL" value="${contextPath}/checkout/json/submitOrderError.jsp" />
							<dsp:input type="hidden" bean="CommitOrderFormHandler.commitOrderInventoryErrorURL" value="${contextPath}/checkout/json/submitOrderInventoryError.jsp" />
							<dsp:input type="hidden" bean="CommitOrderFormHandler.commitOrderLoginErrorURL" value="${contextPath}/checkout/json/submitOrderLoginError.jsp" />

							<%-- shipping --%>
							<c:choose>
								<c:when test="${bopis}">
									<div class="checkout-title">
										<h2>Store Pick-Up Information</h2><a href="#" class="edit-shipping">edit</a>
									</div>
									<%-- bopis review panel --%>
									<div class="bopis-checkout-review">
										<dsp:include page="/checkout/includes/bopisReview.jsp">
											<dsp:param name="storeId" value="${bopisStore}"/>
											<dsp:param name="firstStep" value="false"/>
										</dsp:include>
									</div>
								</c:when>
								<c:otherwise>
									<div class="checkout-title">
										<h2>Shipping</h2><a href="#" class="edit-shipping">edit</a>
									</div>
									<%-- shipping review panel --%>
									<div class="shipping-review-panel">
										<div class="shipping-address-review">
											<c:choose>
												<%-- ffl dealer info --%>
												<c:when test="${fflOrder}">
													<dsp:include page="/checkout/includes/fflDealerInfoReview.jsp"/>
												</c:when>
												<c:otherwise>
													<dsp:include page="/checkout/includes/shippingAddressReview.jsp" />
												</c:otherwise>
											</c:choose>
										</div>
										<div class="shipping-method-review">
											<dsp:include page="/checkout/includes/shippingMethodReview.jsp" />
										</div>
									</div>
								</c:otherwise>
							</c:choose>

							<%-- payment review panel --%>
							<div class="checkout-title">
								<h2>Payment</h2><a href="#" class="edit-payment">edit</a>
							</div>
							<div class="payment-info-review-panel">
								<dsp:include page="/checkout/includes/paymentMethodReview.jsp" />
							</div>
							<div class="checkout-title">
								<h2>Review & Place Order</h2>
							</div>
							<div class="order-review">
								<p>
									<fmt:message key="checkout.review" />
								</p>
								<dsp:input type="submit" bean="CommitOrderFormHandler.commitOrder" id="commit-order" value="Place Order" class="button primary place-order-btn disabled"/>
							</div>

						</dsp:form>
					</div>

				</div>

				<%-- order summary --%>
				<div class="checkout-right-column">
					<div class="order-summary">

						<div class="checkout-title">
							<h2>Order Summary</h2>
						</div>

						<%-- order items --%>
						<div id="order-items-container">
							<dsp:include page="/checkout/includes/cartItems.jsp" />
							<%-- 
							<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="ShoppingCart.current.commerceItems"/>
								<dsp:param name="elementName" value="commerceItem"/>
								<dsp:param name="sortProperties" value="+minimumAge"/>
								<dsp:oparam name="output">
									<div class="order-items">
										<%@ include file="fragments/checkoutOrderItem.jspf"%>
									</div>
								</dsp:oparam>
							</dsp:droplet>
							--%>
						</div>

						<%-- promo form / financial stack --%>
						<div class="promo-and-totals">
							<div class="promo-code-container">
								<dsp:include page="/checkout/includes/promoCode.jsp">
									<dsp:param name="isCheckout" value="true" />
								</dsp:include>
							</div>
							<div class="totals-container">
								<dsp:include page="/checkout/includes/totals.jsp">
									<dsp:param name="isCheckout" value="true"/>
								</dsp:include>
							</div>
						</div>
						<div class="checkout-review">
							<button id="fake-commit-order" class="button primary place-order-btn disabled expand hide-for-medium-up">Place Order</button>
						</div>

					</div>
				</div>

			</div>

		</jsp:body>
	</layout:checkout>

</dsp:page>
