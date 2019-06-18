<%--
  - File Name: newPayment.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page is where a user can add a payment method to their account
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

	<layout:default>
		<jsp:attribute name="pageTitle">New Payment</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">payment</jsp:attribute>
		<jsp:attribute name="bodyClass">account newPayment</jsp:attribute>
		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/paymentMethods.jsp">Payment Methods</a></li>
					<li><span class="crumb active">New Payment</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>New Payment Method</h1>
			</div>

			<div class="section-row">
				<dsp:form id="payment-form" method="post" class="payment-form" data-validate>

					<div class="payment-form-card">
						<h2>Payment Information</h2>
						<dsp:include page="/account/includes/paymentMethodForm.jsp" flush="true" />
					</div>

					<div class="payment-form-address">
						<div class="field-group">
							<h2>Choose Billing Address</h2>
							<div class="required-note">* Required</div>

						<%-- get the saved address from profile start--%>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="Profile.secondaryAddresses"/>
								<dsp:param name="elementName" value="address"/>
								<dsp:oparam name="output">
									<dsp:getvalueof param="key" var="nickname"/>
									<dsp:getvalueof param="count" var="count"/>
									<div class="radio">
										<label for="${nickname}">
											<c:choose>
												<c:when test="${count == 1}">
													<dsp:input bean="ProfileFormHandler.billAddrValue.addrNickname" type="radio" value="${nickname}" id="${nickname}" name="payment-address" checked="true"/>
												</c:when>
												<c:otherwise>
													<dsp:input bean="ProfileFormHandler.billAddrValue.addrNickname" type="radio" value="${nickname}" id="${nickname}" name="payment-address"/>
												</c:otherwise>
											</c:choose>
											<div class="card">
												<div class="card-title">${nickname}</div>
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
									<c:set var="newAddress" value="true"/>
								</dsp:oparam>
							</dsp:droplet>
							<%-- get the saved address from profile end--%>

							<div class="radio new-payment-address">
								<label for="new-payment-address">
									<c:choose>
										<c:when test="${newAddress eq 'true'}">
											<dsp:input bean="ProfileFormHandler.billAddrValue.addrNickname" type="radio" value="new-payment-address" id="new-payment-address" name="payment-address" checked="true"/>
										</c:when>
										<c:otherwise>
											<dsp:input bean="ProfileFormHandler.billAddrValue.addrNickname" type="radio" value="new-payment-address" id="new-payment-address" name="payment-address"/>
										</c:otherwise>
									</c:choose>
									<span>New Address</span>
									<div class="new-payment-address-form">
										<dsp:include page="/account/includes/addressForm.jsp">
											<dsp:param name="isPaymentForm" value="true" />
										</dsp:include>
									</div>
								</label>
							</div>
						</div>
					</div>

					<div class="payment-form-button">
						<dsp:input bean="ProfileFormHandler.createCardErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp" />
						<dsp:input bean="ProfileFormHandler.createCardSuccessURL" type="hidden" value="${contextPath}/account/json/avsPaymentSuccess.jsp" />
						<dsp:input bean="ProfileFormHandler.createNewCreditCard" type="submit" id="payment-form-submit" name="payment-form-submit" iclass="button primary" value="Save Payment Method" />
					</div>

				</dsp:form>
			</div>

		</jsp:body>
	</layout:default>

</dsp:page>
