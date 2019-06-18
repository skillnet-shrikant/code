<%--
  - File Name: editPayment.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page is where a user can edit a payment method already in their account
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

	<layout:default>
		<jsp:attribute name="pageTitle">Edit Payment</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">payment</jsp:attribute>
		<jsp:attribute name="bodyClass">account editPayment</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/paymentMethods.jsp">Payment Methods</a></li>
					<li><span class="crumb active">Edit Payment</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>Edit Payment Method</h1>
			</div>

			<dsp:getvalueof param="editMode" var="editMode"/>
			<dsp:getvalueof param="nickName" var="nickName"/>
			<c:set var="editCard" value=""/>
			<c:set var="editCardName" value=""/>
			<c:set var="billingAddress" value=""/>

			<c:if test="${ editMode eq 'true' && not empty nickName}">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" bean="Profile.creditCards"/>
					<dsp:param name="elementName" value="creditCard"/>
					<dsp:oparam name="output">
						<dsp:getvalueof param="key" var="cardNickName"/>
						<dsp:getvalueof param="creditCard" var="creditCard"/>
						<c:if test="${cardNickName eq nickName}">
							<c:set var="editCard" value="${creditCard}"/>
							<c:set var="editCardName" value="${cardNickName}"/>
							<dsp:getvalueof param="creditCard.billingAddress.id" var="billingAddressId"/>
							<%-- <c:set var="billingAddress" value="${creditCardBilling}"/> --%>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>

			<div class="section-row">
				<dsp:form id="payment-form" method="post" class="payment-form" data-validate>

					<div class="payment-form-card">
						<h2>Payment Information</h2>
						<dsp:include page="/account/includes/paymentMethodForm.jsp" flush="true" >
							<dsp:param name="editCard" value="${editCard}"/>
							<dsp:param name="editCardName" value="${editCardName}"/>
						</dsp:include>
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
									<dsp:getvalueof param="address.id" var="addressId"/>
									<dsp:getvalueof param="key" var="nickname"/>
									<div class="radio">
										<label for="${nickname}">

											<c:choose>
												<c:when test="${billingAddressId eq addressId}">
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
							</dsp:droplet>
							<%-- get the saved address from profile end--%>

							<div class="radio new-payment-address">
								<label for="new-payment-address">
									<dsp:input bean="ProfileFormHandler.billAddrValue.addrNickname" type="radio" value="new-payment-address" id="new-payment-address" name="payment-address"/>
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
						<dsp:input bean="ProfileFormHandler.updateCardErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp" />
						<dsp:input bean="ProfileFormHandler.updateCardSuccessURL" type="hidden" value="${contextPath}/account/json/avsPaymentSuccess.jsp" />
						<dsp:input bean="ProfileFormHandler.updateCard" type="submit" id="payment-form-submit" name="payment-form-submit" iclass="button primary" value="Update Payment Method" />
					</div>

				</dsp:form>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
