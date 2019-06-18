<%--
  - File Name: changePassword.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the page where users can change their password
  --%>

<dsp:page>

	<layout:default>
		<jsp:attribute name="pageTitle">Change Password</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">changePassword</jsp:attribute>
		<jsp:attribute name="bodyClass">account changePassword</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/profile.jsp">My Profile</a></li>
					<li><span class="crumb active">Change Password</span></li>
				</ul>
			</section>

			<section>
				<div class="section-title">
					<h1>Change Password</h1>
				</div>
				<div class="section-row">
					<div class="change-password-form">
						<dsp:form id="change-password-form" method="post" data-validate>
							<dsp:include page="/account/includes/changePasswordForm.jsp" flush="true"/>
						</dsp:form>
					</div>
				</div>
			</section>

		</jsp:body>

	</layout:default>

</dsp:page>
