<%--
  - File Name: login.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the login page for My Account
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>

	<%-- Authentication Check --%>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param bean="Profile.hardLoggedIn" name="value"/>
		<dsp:oparam name="false">

			<layout:default>
				<jsp:attribute name="pageTitle">Login</jsp:attribute>
				<jsp:attribute name="metaDescription"></jsp:attribute>
				<jsp:attribute name="metaKeywords"></jsp:attribute>
				<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
				<jsp:attribute name="seoRobots"></jsp:attribute>
				<jsp:attribute name="lastModified"></jsp:attribute>
				<jsp:attribute name="section">account</jsp:attribute>
				<jsp:attribute name="pageType">login</jsp:attribute>
				<jsp:attribute name="bodyClass">account login</jsp:attribute>
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
							<h2>Sign In</h2>
							<dsp:form id="login-form" method="post" formid="login-form" action="${contextPath}/account/login.jsp" data-validate>
								<dsp:include page="/account/includes/loginForm.jsp" />
							</dsp:form>
						</div>
						<div class="register-form">
							<h2>Create Account</h2>
							<dsp:form id="register-form" method="post" formid="register-form" action="${contextPath}/account/login.jsp" data-validate>
								<dsp:include page="/account/includes/registerForm.jsp" />
							</dsp:form>
						</div>
					</div>

				</jsp:body>

			</layout:default>

		</dsp:oparam>
		<dsp:oparam name="true">
			<%-- if logged in redirect to my account page --%>
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" value="${contextPath}/account/account.jsp"/>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
