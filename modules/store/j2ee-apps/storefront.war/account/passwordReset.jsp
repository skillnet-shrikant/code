<%--
  - File Name: forgotPassword.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the page where users can recover a forgotton password
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="successParam" param="success"/>

	<layout:default>
		<jsp:attribute name="pageTitle">Password Reset</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">passwordReset</jsp:attribute>
		<jsp:attribute name="bodyClass">account passwordReset</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/profile.jsp">My Profile</a></li>
					<li><span class="crumb active">Password Reset</span></li>
				</ul>
			</section>

			<section>
				<div class="section-title">
					<h1>Password Reset</h1>
				</div>
				<div class="section-row">
					<div class="password-reset-form">
						<div class="password-reset-errors">
							<dsp:include page="/sitewide/includes/errors/formErrors.jsp" >
								<dsp:param name="formhandler" bean="ForgotPasswordHandler"/>
							</dsp:include>
						</div>
						<div class="password-reset-message">
							<p>
								Don't worry! Just enter your email address below, and we'll send you a temporary
								password instantly. Once you've received it, we recommend visiting My Account to
								create a new password you'll remember easily.
							</p>
						</div>
						<dsp:form id="password-reset-form" method="post" formid="password-reset-form" data-validate>
							<jsp:include page="/account/includes/passwordResetForm.jsp" />
						</dsp:form>
					</div>
				</div>
			</section>

		</jsp:body>

	</layout:default>

</dsp:page>
