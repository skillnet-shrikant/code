<%--
  - File Name: changeEmail.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the page where users can change their email address
  --%>

<dsp:page>

	<layout:default>
		<jsp:attribute name="pageTitle">Change Email</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">changeEmail</jsp:attribute>
		<jsp:attribute name="bodyClass">account changeEmail</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/profile.jsp">My Profile</a></li>
					<li><span class="crumb active">Change Email</span></li>
				</ul>
			</section>

			<section>
				<div class="section-title">
					<h1>Change Email</h1>
					<p>
						Your email address is used both for contact and to log into fleetfarm.com. After
						changing your email address, you will need to log in with your new email address.
					</p>
				</div>
				<div class="section-row">
					<div class="change-email-form">
						<dsp:form id="change-email-form" method="post" data-validate>
							<dsp:include page="/account/includes/changeEmailForm.jsp" flush="true"/>
						</dsp:form>
					</div>
				</div>
			</section>

		</jsp:body>

	</layout:default>

</dsp:page>
