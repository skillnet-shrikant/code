<%--
  - File Name: taxExemptions.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page has all the users saved tax exemptions on it. Users can also add a tax
  								exemption from this page.
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/mff/droplet/SortMapByValueToArrayDefaultFirst"/>
	<dsp:importbean bean="/com/mff/userprofiling/droplet/CheckMaxTaxExemptionsDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	
	<dsp:droplet name="CheckMaxTaxExemptionsDroplet">
		<dsp:oparam name="output">
			<dsp:getvalueof var="maxExemptionsReached" vartype="java.lang.Boolean" param="maxExemptionsReached"/>
		</dsp:oparam>
	</dsp:droplet>

	<layout:default>
		<jsp:attribute name="pageTitle">Tax Exemptions</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">taxExemptions</jsp:attribute>
		<jsp:attribute name="bodyClass">taxExemptions</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">Tax Exemptions</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>Tax Exemptions</h1>
			</div>

			<%-- errors --%>
			<div class="error-container">
				<div class="error-messages">

					<c:if test="${maxExemptionsReached}">
						<div class="alert-box info">
							<p>
								<fmt:message key="maxExemptionsReached" />
							</p>
						</div>
					</c:if>

				</div>
			</div>


			<div class="section-row">
				<ul class="account-grid">
				
				<dsp:droplet name="SortMapByValueToArrayDefaultFirst">
					<dsp:param name="map" bean="Profile.taxExemptions"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>

						<c:forEach var="taxExemption" items="${sortedArray}" varStatus="status">
							<dsp:getvalueof var="nickName" value="${taxExemption.key}"/>
							<dsp:setvalue param="taxExemptionVal" value="${taxExemption.value}"/>
							<dsp:getvalueof var="classification" param="taxExemptionVal.classificationName"/>
							<dsp:getvalueof var="taxId" param="taxExemptionVal.taxId"/>
							<dsp:getvalueof var="currentTaxExmpId" param="taxExemptionVal.repositoryId"/>

							<li>
								<div class="card">
									<div class="card-title">${nickName}</div>
									<div class="card-content">
										<p><strong>Classification:</strong> ${classification}</p>
										<p><strong>Tax ID:</strong> ${taxId} </p>
									</div>
									<div class="card-links">
										<dsp:a title="${editAddressTitle}" page="editTaxExemption.jsp">
											<dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
											<dsp:param name="addEditMode" value="edit"/>
											<dsp:param name="nickName" value="${nickName}"/>
											<span>Edit Tax Exemption</span>
										</dsp:a>
										<dsp:a href="${contextPath}/account/ajax/deleteTaxExemptionModal.jsp" class="modal-trigger" data-target="delete-tax-exemption-modal" data-size="small">
											<dsp:param name="nickName" value="${nickName}"/>
											Delete Tax Exemption
										</dsp:a>
									</div>
								</div>
							</li>
						</c:forEach>
					
					</dsp:oparam>
				</dsp:droplet>

					<c:if test="${!maxExemptionsReached}">
						<li>
							<div class="card new-tax-exemption">
								<a href="newTaxExemption.jsp">
									<span class="link-text">
										<span class="icon icon-plus"></span>
										Add New Tax Exemption
									</span>
								</a>
							</div>
						</li>
					</c:if>

				</ul>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
