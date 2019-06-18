<%--
  - File Name: login.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the login page for Checkout
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>

	<dsp:droplet name="Switch">
		<dsp:param bean="Profile.hardLoggedIn" name="value"/>
		<dsp:oparam name="false">

			<layout:default>
				<jsp:attribute name="pageTitle">Login</jsp:attribute>
				<jsp:attribute name="metaDescription"></jsp:attribute>
				<jsp:attribute name="metaKeywords"></jsp:attribute>
				<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
				<jsp:attribute name="seoRobots"></jsp:attribute>
				<jsp:attribute name="lastModified"></jsp:attribute>
				<jsp:attribute name="section">checkout</jsp:attribute>
				<jsp:attribute name="pageType">login</jsp:attribute>
				<jsp:attribute name="bodyClass">checkout login</jsp:attribute>
				<jsp:body>

					<%-- breadcrumbs --%>
					<section class="breadcrumbs">
						<ul aria-label="breadcrumbs" role="navigation">
							<li><a href="${contextPath}/" class="crumb">Home</a></li>
							<li><span class="crumb active">Sign In</span></li>
						</ul>
					</section>

					<div class="section-title">
						<h1>Sign In</h1>
					</div>

					<div class="section-row">
						<div class="login-form">
							<%-- login form --%>
							<h2>Sign In</h2>
							<dsp:form id="login-form" method="post" formid="login-form" action="${contextPath}/account/login.jsp" data-validate>
								<dsp:include page="/checkout/includes/loginForm.jsp">
									<dsp:param name="fromCheckout" value="true"/>
								</dsp:include>
							</dsp:form>
						</div>

						<div class="guest-checkout-form">
							<dsp:droplet name="Switch">
								<dsp:param bean="Profile.softLoggedInRegistered" name="value"/>
								<dsp:oparam name="false">
									<%-- show guest checkout for soft-logged in users --%>
									<h2>Guest Checkout</h2>
									<p>
										Checkout as a guest by clicking below. You can register for faster checkout later.
									</p>
									<form id="guest-checkout-form">
										<div class="field-group">
											<a href="${contextPath}/checkout/checkout.jsp" class="button primary">Guest Checkout</a>
										</div>
									</form>
								</dsp:oparam>
								<dsp:oparam name="true">
									<%-- show soft-logged in users a log out button --%>
									<h2>You're Almost There</h2>
									<p>
										Your cart is saved to this account, but we still need to verify your identity. Please log
										in to your account with this email address, or log out using the button below. If you
										choose to log out, you'll loose all the items in your cart.
									</p>
									<dsp:form id="sign-out-form-checkout" formid="sign-out-form-checkout" iclass="sign-out" action="${requestURL}" method="post">
										<dsp:input type="submit" bean="ProfileFormHandler.logout" value="Sign Out" class="button primary sign-out-link" />
									</dsp:form>
								</dsp:oparam>
							</dsp:droplet>
						</div>

					</div>

				</jsp:body>
			</layout:default>

		</dsp:oparam>
		<dsp:oparam name="true">
			<%-- if logged in redirect to checkout page --%>
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" value="${contextPath}/checkout/checkout.jsp"/>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
