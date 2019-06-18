<%--
  - File Name: profile.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the login page for My Account
  --%>

<dsp:page>

	<layout:default>
		<jsp:attribute name="pageTitle">My Profile</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">profile</jsp:attribute>
		<jsp:attribute name="bodyClass">account profile</jsp:attribute>
		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">My Profile</span></li>
				</ul>
			</section>

			<section>
				<div class="section-title">
					<h1>My Profile</h1>
				</div>
				<div class="section-row">
					<div class="profile-form">
						<dsp:form id="profile-form" method="post" data-validate>
							<dsp:include page="/account/includes/profileForm.jsp" flush="true"/>
						</dsp:form>
					</div>
					<div class="profile-links">
						<a href="changeEmail.jsp">Change Email</a>
						<a href="changePassword.jsp">Change Password</a>
						<a href="expressCheckout.jsp">Change Express Checkout Settings</a>
					</div>
				</div>
			</section>

		</jsp:body>
	</layout:default>

</dsp:page>
