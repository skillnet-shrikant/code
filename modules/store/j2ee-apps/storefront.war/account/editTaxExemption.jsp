<%--
  - File Name: editTaxExemption.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page is where a user can edit a tax exemption that is already in their account
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

	<layout:default>

		<jsp:attribute name="pageTitle">Edit Tax Exemption</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">taxExemption</jsp:attribute>
		<jsp:attribute name="bodyClass">account taxExemption</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/taxExemptions.jsp">Tax Exemptions</a></li>
					<li><span class="crumb active">Edit Tax Exemption</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>Edit Tax Exemption</h1>
			</div>

			<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="true"/>
			<dsp:setvalue bean="ProfileFormHandler.editTaxExemption" paramvalue="nickName"/>
			<dsp:getvalueof var="preFillValuesVar" value="true" vartype="java.lang.Boolean"/>

			<div class="section-row">
				<div class="required-note">* Required</div>
				<div class="tax-exemption-form">
					<dsp:form id="tax-exemption-form" action="${originatingRequest.requestURI}" method="post" name="taxExmpForm" data-validate>
						<dsp:include page="/account/includes/taxExemptionForm.jsp">
							<dsp:param name="edit" value="true" />
							<dsp:param name="preFillValues" value="${preFillValuesVar}"/>
						</dsp:include>
					</dsp:form>
				</div>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
