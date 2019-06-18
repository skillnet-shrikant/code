<%--
  - File Name: expressCheckout.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page is where a user can set and update their express checkout settings
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/commerce/pricing/AvailableShippingMethods"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ExpressCheckoutFormHandler"/>

	<%-- Page Variables --%>
	<c:set var="showSaveButton" value="true" scope="request" />

	<layout:default>
		<jsp:attribute name="pageTitle">Express Checkout</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">expressCheckout</jsp:attribute>
		<jsp:attribute name="bodyClass">account expressCheckout</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">Express Checkout</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>Express Checkout</h1>
			</div>

			<div class="section-row">
				<dsp:form id="express-checkout-form" method="post" class="payment-form" data-validate>

					<div class="payment-form-card">
						<div class="field-group">
							<h2>Payment Information</h2>

							<dsp:getvalueof bean="Profile.defaultCreditCard" var="defaultCard"/>
							<c:if test="${not empty defaultCard}">
								<dsp:getvalueof bean="Profile.defaultCreditCard.id" var="defaultCardId"/>
							</c:if>

							<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="Profile.creditCards"/>
								<dsp:param name="elementName" value="creditCard"/>
								<dsp:oparam name="output">
									<dsp:getvalueof param="creditCard.creditCardType" var="creditCardType"/>
									<dsp:getvalueof bean="CreditCardTools.reverseCardCodeMap" var="reverseCardCodeMap"/>
									<dsp:getvalueof param="index" var="index"/>
									<dsp:getvalueof param="creditCard.id" var="cardId"/>

									<div class="radio">
										<label for="saved-card-${index}">
											<c:choose>
												<c:when test="${defaultCardId eq cardId}">
													<dsp:input bean="ProfileFormHandler.defaultCreditCardID" type="radio" value="${cardId}" id="saved-card-${index}" name="saved-card" checked="true"/>
												</c:when>
												<c:otherwise>
													<dsp:input bean="ProfileFormHandler.defaultCreditCardID" type="radio" value="${cardId}" id="saved-card-${index}" name="saved-card" />
												</c:otherwise>
											</c:choose>
											<div class="card">
												<div class="card-title"><dsp:valueof param="key"/></div>
												<div class="card-content">
													<p><dsp:valueof param="creditCard.nameOnCard"/></p>
													<p>
														<c:out value="${reverseCardCodeMap[creditCardType]}"/>
													</p>
													<p>XXXX&nbsp;XXXX&nbsp;XXXX<dsp:valueof param="creditCard.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
													<p><strong>Exp:</strong>&nbsp;<dsp:valueof param="creditCard.expirationMonth"/>/<dsp:valueof param="creditCard.expirationYear"/></p>
												</div>
											</div>
										</label>
									</div>

								</dsp:oparam>
								<dsp:oparam name="empty">
									<c:set var="showSaveButton" value="false" scope="request" />
									<p>
										It looks like you don't have any saved cards in your account. Visit the
										<dsp:a href="${contextPath}/account/newPayment.jsp">New Payment</dsp:a> page to
										add a new payment method to your account.
									</p>
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</div>

					<div class="payment-form-address">

						<%-- shipping method --%>
						<dsp:getvalueof var="shippingMethodNames" bean="AvailableShippingMethods.shippingMethodNamesMap"/>
						<dsp:getvalueof bean="Profile.defaultCarrier" var="defaultCarrier"/>
						<div class="field-group">
							<h2>Shipping Method</h2>
							<dsp:select bean="ProfileFormHandler.defaultCarrier" id="shipping-method" data-validation="required" data-fieldname="Shipping Method">
								<dsp:droplet name="AvailableShippingMethods">
									<dsp:param bean="ExpressCheckoutFormHandler.shippingGroup" name="shippingGroup"/>
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="availableShippingMethods" />
											<dsp:oparam name="outputStart">
												<dsp:option value="">
													Choose Shipping Method
												</dsp:option>
											</dsp:oparam>
											<dsp:oparam name="output">
												<dsp:getvalueof param="key" var="shipMethod"/>
												<c:choose>
													<c:when test="${defaultCarrier eq shipMethod}">
														<dsp:option value="${shipMethod}" selected="true">
															${shippingMethodNames[shipMethod]}
														</dsp:option>
													</c:when>
													<c:otherwise>
														<dsp:option value="${shipMethod}">
															${shippingMethodNames[shipMethod]}
														</dsp:option>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:select>
						</div>

						<%-- shipping address --%>
						<div class="field-group">
							<h2>Shipping Address</h2>
							<dsp:getvalueof bean="Profile.shippingAddress" var="defaultShipAddr"/>
							<c:if test="${not empty defaultShipAddr}">
								<dsp:getvalueof bean="Profile.shippingAddress.id" var="defaultShipAddrId"/>
							</c:if>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="Profile.secondaryAddresses"/>
								<dsp:param name="elementName" value="address"/>
								<dsp:oparam name="output">
									<dsp:getvalueof param="key" var="addressNickName"/>
									<dsp:getvalueof param="index" var="addrIndex"/>
									<dsp:getvalueof param="address.id" var="addressId"/>

									<div class="radio">
										<label for="shipping-address-${addrIndex}">
											<c:choose>
												<c:when test="${addressId eq defaultShipAddrId}">
													<dsp:input bean="ProfileFormHandler.editValue.defaultAddressNickname" type="radio" value="${addressNickName}"
														id="shipping-address-${addrIndex}" name="shipping-address" data-validation="required" data-fieldname="Shipping Address" checked="true"/>
												</c:when>
												<c:otherwise>
													<dsp:input bean="ProfileFormHandler.editValue.defaultAddressNickname" type="radio" value="${addressNickName}"
														id="shipping-address-${addrIndex}" name="shipping-address" data-validation="required" data-fieldname="Shipping Address"/>
												</c:otherwise>
											</c:choose>
											<div class="card">
												<div class="card-title"><dsp:valueof param="key"/></div>
												<div class="card-content">
													<p><dsp:valueof param="address.firstName"/>&nbsp;<dsp:valueof param="address.lastName"/></p>
													<p><dsp:valueof param="address.address1"/></p>
													<p><dsp:valueof param="address.address2"/></p>
													<p><dsp:valueof param="address.city"/>,&nbsp;<dsp:valueof param="address.state"/>&nbsp;<dsp:valueof param="address.postalCode"/></p>
													<p>
														<dsp:getvalueof var="phone" param="address.phoneNumber" />
														<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
													</p>
												</div>
											</div>
										</label>
									</div>

								</dsp:oparam>
								<dsp:oparam name="empty">
									<c:set var="showSaveButton" value="false" scope="request" />
									<p>
										It looks like you don't have any saved addresses in your address book. Visit the
										<dsp:a href="${contextPath}/account/newAddress.jsp">New Address</dsp:a> page to
										add a new address to your address book.
									</p>
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</div>

					<div class="payment-form-button">
						<c:choose>
							<c:when test="${showSaveButton}">
								<dsp:input type="hidden" bean="ProfileFormHandler.expressCheckoutPreferencesSuccessURL" value="${contextPath}/account/json/loginSuccess.jsp" />
								<dsp:input type="hidden" bean="ProfileFormHandler.expressCheckoutPreferencesErrorURL" value="${contextPath}/account/json/profileError.jsp" />
								<dsp:input type="hidden" bean="ProfileFormHandler.setExpressCheckoutPreferences" value="Save Express Checkout" />
								<input id="payment-form-submit" name="payment-form-submit" type="submit" class="button primary" value="Save Express Checkout" />
							</c:when>
							<c:otherwise>
								<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
									<dsp:param name="inUrl" value="${contextPath}/account/account.jsp"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="accountUrl" scope="request" param="secureUrl"/>
										<dsp:a href="${accountUrl}" class="button primary">Back To My Account</dsp:a>
									</dsp:oparam>
								</dsp:droplet>
							</c:otherwise>
						</c:choose>
					</div>

				</dsp:form>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
