<%--
	- File Name: emailPreferences.jsp
	- Author(s): KnowledgePath Solutions
	- Copyright Notice:
	- Description: This is the Wishabi/Flipp weekly ad
	--%>

<dsp:page>

	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="emailpref" />
		<dsp:param name="defaultPageTitle" value="Email Preferences" />
		<dsp:param name="defaultMetaDescription" value="Fleet Farm Email Preferences" />
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
		<jsp:attribute name="pageType">emailPreferences</jsp:attribute>
		<jsp:attribute name="bodyClass">preference-center</jsp:attribute>
		<jsp:body>

			<div class="section-row">
				<div class="section-content">

					<!-- listrak preference center -->
					<div data-ltk-prefcenter="MillsFleetFarm"></div>
					<!-- /listrak preference center -->

				</div>
			</div>

		</jsp:body>
	</layout:default>

</dsp:page>
