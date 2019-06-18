<%--
  - File Name: error_404.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the 404 error page
  --%>

<layout:default>
	<jsp:attribute name="pageTitle">Error 404</jsp:attribute>
	<jsp:attribute name="metaDescription"></jsp:attribute>
	<jsp:attribute name="metaKeywords"></jsp:attribute>
	<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
	<jsp:attribute name="seoRobots"></jsp:attribute>
	<jsp:attribute name="lastModified"></jsp:attribute>
	<jsp:attribute name="section">home</jsp:attribute>
	<jsp:attribute name="pageType">error</jsp:attribute>
	<jsp:attribute name="bodyClass">error</jsp:attribute>
	<jsp:body>

		<section>
			<div class="section-title">
				<h1>Nothing to See Here...</h1>
			</div>
			<div class="error-page">
				<div class="error-page-message">
					<p>
						Ooops! It seems that the page you're looking for doesn't exist.
					</p>
					<p>
						You can <a href="${contextPath}/">click here</a> to return to the homepage, report this
						problem to our <a href="${contextPath}/static/contact-us">customer service</a> team, or check your
						spelling.
					</p>
					<img src="${contextPath}/resources/images/error404.jpg" alt="error" />
				</div>
			</div>
		</section>

	</jsp:body>
</layout:default>
