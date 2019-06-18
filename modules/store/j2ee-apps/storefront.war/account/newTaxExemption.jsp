<%--
  - File Name: newTaxExemption.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page is where a user can add a tax exemption to their account
  --%>

<dsp:page>

	<layout:default>

		<jsp:attribute name="pageTitle">New Tax Exemption</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">taxExemption</jsp:attribute>
		<jsp:attribute name="bodyClass">account newTaxExemption</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/taxExemptions.jsp">Tax Exemptions</a></li>
					<li><span class="crumb active">New Tax Exemption</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>New Tax Exemption</h1>
			</div>

			<div class="section-row">
				<div class="required-note">* Required</div>
				<div class="tax-exemption-form">
					<dsp:form id="tax-exemption-form" method="post" data-validate>
						<jsp:include page="/account/includes/taxExemptionForm.jsp" />
					</dsp:form>
				</div>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
