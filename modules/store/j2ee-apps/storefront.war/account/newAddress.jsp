<%--
  - File Name: newAddress.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page is where a user can add an address to their address book
  --%>

<dsp:page>

	<layout:default>

		<jsp:attribute name="pageTitle">New Address</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">address</jsp:attribute>
		<jsp:attribute name="bodyClass">account newAddress</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/addressBook.jsp">Address Book</a></li>
					<li><span class="crumb active">New Address</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>New Address</h1>
			</div>

			<div class="section-row">
				<div class="required-note">* Required</div>
				<div class="address-form">
					<dsp:form action="${pageContext.request.requestURI}" method="post" id="address-form" data-validate>
						<dsp:include page="/account/includes/addressForm.jsp" />
					</dsp:form>
				</div>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
