<%--
  - File Name: changePassword.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the page where users can change their password
  --%>

<dsp:page>

	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="changepassword" />
		<dsp:param name="defaultPageTitle" value="Change Password" />
		<dsp:param name="defaultMetaDescription" value="Fleet Farm Change Password" />
		<dsp:param name="defaultCanonicalURL" value="" />
		<dsp:param name="defaultRobotsIndex" value="index" />
		<dsp:param name="defaultRobotsFollow" value="follow" />
	</dsp:include>
	
	<layout:default>
		<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
		<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
		<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">home</jsp:attribute>
		<jsp:attribute name="pageType">changePassword</jsp:attribute>
		<jsp:attribute name="bodyClass">change-password</jsp:attribute>
	
		<jsp:body>

			<section>
				<div class="section-title">
					<h1>Change Password</h1>
				</div>
				<div class="section-row">
					<div class="change-password-form">
						<dsp:form id="change-password-form" method="post" data-validate>
							<dsp:droplet name="/com/mff/droplet/PasswordResetTokenValidatorDroplet">
								<dsp:oparam name="true">
									<dsp:include page="/sitewide/includes/changePasswordForm.jsp" flush="true">
										<dsp:param name="email" param="email" />
									</dsp:include>
								</dsp:oparam>
								<dsp:oparam name="false">
									In valid Link or Link has been expired
								</dsp:oparam>
							</dsp:droplet>
						</dsp:form>
					</div>
				</div>
			</section>

		</jsp:body>

	</layout:default>

</dsp:page>
