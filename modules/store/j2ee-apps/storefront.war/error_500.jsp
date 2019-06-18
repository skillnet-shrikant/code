<%--
  - File Name: error_500.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the 500 error page
  --%>

<layout:default>
	<jsp:attribute name="pageTitle">Error 500</jsp:attribute>
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
				<h1>Internal Server Error</h1>
			</div>
			<div class="error-page">
				<div class="error-page-message">
					<p>
						We're sorry, there seems to be an issue with one of our servers. Our developers are
						working hard to repair it.
					</p>
					<p>
						In the mean time, you can try refreshing the page or <a href="${contextPath}/">head back
						to the homepage</a>. You can also report this problem to our
						<a href="${contextPath}/static/contact-us">customer service</a> team if you're feeling helpful.
					</p>
					<img src="${contextPath}/resources/images/error500.jpg" alt="error" />
				</div>
			</div>
		</section>

	</jsp:body>
</layout:default>
