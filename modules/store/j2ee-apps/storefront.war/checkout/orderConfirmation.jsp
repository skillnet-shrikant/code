<%--
	- File Name: orderConfimation.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Order confirmation page - last page in the order flow
	- Parameters:
	-
	--%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/atg/multisite/Site" />
	
	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>
	
	<c:set var="gcHeaderText">Gift Card</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Card / Bonus Bucks</c:set>
	</c:if>
	
	<%-- Page Parameters --%>
	<dsp:param name="shippingGroup" bean="ShoppingCart.last.shippingGroups[0]" />
	<dsp:param name="order" bean="ShoppingCart.last" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="bvOrderTransactionsEnabled" bean="MFFEnvironment.bvOrderTransactionsEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />

	<%-- Authentication Check --%>
	<dsp:droplet name="IsEmpty">
		<dsp:param param="order" name="value"/>
		<dsp:oparam name="true">
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" value="${contextPath}/"/>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="false">

			<%-- Page Variables --%>
			<dsp:getvalueof var="bopis" bean="ShoppingCart.last.bopisOrder"/>
			<dsp:getvalueof var="bopisStore" bean="ShoppingCart.last.bopisStore"/>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param bean="Profile.hardLoggedIn" name="value"/>
				<dsp:oparam name="true">
					<c:set var="userIsAuthenticated" value="true" scope="request" />
				</dsp:oparam>
				<dsp:oparam name="false">
					<c:set var="userIsAuthenticated" value="false" scope="request" />
				</dsp:oparam>
			</dsp:droplet>

			<layout:default>
				<jsp:attribute name="pageTitle">Order Confirmation</jsp:attribute>
				<jsp:attribute name="metaDescription"></jsp:attribute>
				<jsp:attribute name="metaKeywords"></jsp:attribute>
				<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
				<jsp:attribute name="seoRobots"></jsp:attribute>
				<jsp:attribute name="lastModified"></jsp:attribute>
				<jsp:attribute name="section">checkout</jsp:attribute>
				<jsp:attribute name="pageType">orderConfirmation</jsp:attribute>
				<jsp:attribute name="bodyClass">checkout orderConfirmation</jsp:attribute>
				<jsp:body>

					<!-- Bazaar voice display common script -->
					<c:if test="${bvEnabled}">
						<dsp:include otherContext="/bv" page="/common/bv_common_script.jsp" />
							<c:set var="contextPath" value="${currentContext}" />
					</c:if>

					<!-- BazaarVoice inline script for product Ids -->
					<c:if test="${bvEnabled}">
						<dsp:include otherContext="/bv" page="/productListing/common/bv_plp_order_detail_script.jsp">
							<dsp:param name="commerceItems" param="order.commerceItems" />
						</dsp:include>
						<c:set var="contextPath" value="${currentContext}" />
					</c:if>

					<section>
						<div class="section-title">
							<h1>Order Confirmation</h1>
							<div class="title-buttons">
								<a href="#" class="print"><span class="icon icon-print"></span></a>
							</div>
						</div>

						<%-- errors --%>
						<div class="error-container">
							<div class="error-messages"></div>
						</div>



						<%-- confirmation message --%>
						<div class="confirmation-message-container">
							<div class="confirmation-messages <c:if test='${userIsAuthenticated}'>user-confirmation-messages</c:if>">
								<h2>Thank you for your order</h2>
								<p>
									Your order number is <strong class="orange"><dsp:valueof param="order.orderNumber"/></strong>
								</p>
								<p>
									You will receive an email confirmation shortly to
									<strong><dsp:valueof param="order.contactEmail"/></strong>.
									<c:if test="${!bopis}">
										Upon shipment of your order, you will receive a shipment confirmation to this address.
									</c:if>
								</p>
								<p>
									Note: If your email provider uses a spam filter, please add customerservice@fleetfarm.com to your address book to ensure you receive emails about your order status.
									<a href="/static/faq-placing-order#order-confirmation-email" class="orderInfo-faq-link" data-target="orderInfo-faq-modal" alt="more info faq">More Info</a>
								</p>
							</div>

							<c:if test="${not userIsAuthenticated}">
								<div class="confirmation-create-account">
									<h2>Create Account</h2>
									<div class="create-account-message">
										<p>
											Save your information for faster checkout next time. You can also track open
											orders, create a Wish List, and manage your communication preferences.
										</p>
										<button class="button primary create-account-button">Create Account</button>
									</div>
									<div class="create-account-form">
										<dsp:form id="register-form" method="post" formid="register-form" action="${contextPath}/checkout/orderConfirmation.jsp" data-validate>
											<dsp:include page="includes/registerForm.jsp" />
										</dsp:form>
									</div>
								</div>
							</c:if>
						</div>

						<%-- shipping/billing information header --%>
						<div class="shipping-billing-info">
							<div class="shipped-to">
								<c:choose>
									<c:when test="${bopis}">
										<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
											<dsp:param name="id" value="${bopisStore}"/>
											<dsp:param name="elementName" value="store"/>
											<dsp:oparam name="output">
												<div class="shipping-billing-header">
													Pick-Up Location
												</div>
												<div class="shipped-to-info">
													<p class="title"><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/></p>
													<p><dsp:valueof param="store.address1"/></p>
													<p><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/>&nbsp;<dsp:valueof param="store.postalCode"/></p>
													<p><dsp:valueof param="store.phoneNumber" /></p>
												</div>
											</dsp:oparam>
										</dsp:droplet>
									</c:when>
									<c:otherwise>
										<div class="shipping-billing-header">
											Shipped To
										</div>
										<div class="shipped-to-info">
											<p><dsp:valueof param="shippingGroup.shippingAddress.firstName"/>&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.lastName"/></p>
											
											<dsp:droplet name="IsEmpty">
												<dsp:param name="value" param="shippingGroup.specialInstructions"/>
												<dsp:oparam name="false">
													<p><span class="label">Attention:</span><dsp:valueof param="shippingGroup.specialInstructions.instructions"/></p>
												</dsp:oparam>
											</dsp:droplet>
												
											<p><dsp:valueof param="shippingGroup.shippingAddress.address1"/></p>
											<dsp:droplet name="IsEmpty">
												<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
												<dsp:oparam name="false">
													<p><dsp:valueof param="shippingGroup.shippingAddress.address2" /></p>
												</dsp:oparam>
											</dsp:droplet>
											<p><dsp:valueof param="shippingGroup.shippingAddress.city"/>,&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.state"/>&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.postalCode"/></p>
											<p>
												<dsp:getvalueof var="phone" param="shippingGroup.shippingAddress.phoneNumber" />
												<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
											</p>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="shipping-method">
								<c:choose>
									<c:when test="${bopis}">
										<div class="shipping-billing-header">
											Selected Pick-Up Person
										</div>
										<div class="shipped-to-info">
											<p><strong>Name:</strong> <dsp:valueof param="order.bopisPerson"/></p>
											<p><strong>Email:</strong> <dsp:valueof param="order.bopisEmail"/></p>
										</div>
									</c:when>
									<c:otherwise>
										<div class="shipping-billing-header">
											Shipping Method
										</div>
										<div class="shipping-method-info">
											<dsp:valueof param="shippingGroup.shippingMethod" />
											<dsp:droplet name="/atg/dynamo/droplet/Switch">
												<dsp:param name="value" param="shippingGroup.saturdayDelivery"/>
												<dsp:oparam name="true">
													<p>Saturday Delivery</p>
												</dsp:oparam>
											</dsp:droplet>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="billed-to">
								<div class="shipping-billing-header">
									Payment Method
								</div>
								<div class="billed-to-info">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" bean="ShoppingCart.last.paymentGroups"/>
										<dsp:param name="elementName" value="paymentGroup"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
											<c:choose>
												<c:when test="${paymentClassType == 'creditCard'}">

												<dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
												<dsp:getvalueof var="reverseCardCodeMap" bean="CreditCardTools.reverseCardCodeMap"/>

													<dsp:getvalueof var="billingName" param="paymentGroup.nameOnCard" scope="request" />
													<div class="billed-to-info-block">
														<h3>Credit Card</h3>
														<p>${billingName}</p>
														<p><c:out value="${reverseCardCodeMap[creditCardType]}"/></p>
														<p>XXXX&nbsp;XXXX&nbsp;XXXX<dsp:valueof param="paymentGroup.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
														<p><strong>Exp:</strong>&nbsp;<dsp:valueof param="paymentGroup.expirationMonth"/>/<dsp:valueof param="paymentGroup.expirationYear"/></p>
														<p><strong>Amount:</strong> <dsp:valueof param="paymentGroup.amount" converter="currency" /></p>
													</div>
												</c:when>
												<c:when test="${paymentClassType == 'giftCard'}">
													<div class="billed-to-info-block">
														<h3>${gcHeaderText}</h3>
														<p><dsp:valueof param="paymentGroup.cardNumber"/></p>
														<p><strong>Amount:</strong> <dsp:valueof param="paymentGroup.amount" converter="currency" /></p>
													</div>
												</c:when>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>
								</div>
							</div>
						</div>

						<%-- cart header --%>
						<div class="order-items-header">
							<div class="order-items-header-detail">Item Details</div>
							<div class="order-items-header-price">Price</div>
							<div class="order-items-header-total">Total</div>
						</div>

						<%-- cart items --%>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="order.commerceItems"/>
							<dsp:param name="elementName" value="commerceItem"/>
							<dsp:param name="sortProperties" value="+minimumAge"/>
							<dsp:oparam name="output">
								<%-- cart items --%>
								<div class="order-items order-details-items">
									<%@ include file="fragments/confirmationOrderItem.jspf"%>
								</div>
							</dsp:oparam>
						</dsp:droplet>

						<%-- promo form / financial stack --%>
						<div class="promo-and-totals">
							<div class="totals-container">
								<dsp:include page="/checkout/includes/totals.jsp">
									<dsp:param name="isOrderConfirmation" value="true"/>
								</dsp:include>
							</div>
						</div>

					</section>

					<%-- listrak: order confirmation --%>
					<%@ include file="/sitewide/third_party/listrak_confirmation.jspf" %>

					<%-- google tag manager: order confirmation --%>
					<%@ include file="/sitewide/third_party/gtm_confirmation.jspf" %>
					
					<c:if test="${bvEnabled and bvOrderTransactionsEnabled}">
						<!--I can see this part</h1> -->
						<dsp:include otherContext="/bv" page="/pixel/common/bv_pixel_common_script.jsp" />
						<dsp:include otherContext="/bv" page="/pixel/transaction/bv_pixel_transaction_event_order_confirmation.jsp" />
						<c:set var="contextPath" value="${currentContext}" />
					</c:if>

				</jsp:body>
			</layout:default>

		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
