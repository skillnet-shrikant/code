<%--
  - File Name: addressBook.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page has all the users saved addresses on it. Users can also add an address
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/mff/droplet/SortMapByValueToArrayDefaultFirst"/>

	<%-- Page Variables --%>
	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof var="defaultAddress" bean="Profile.shippingAddress"/>

	<layout:default>

		<jsp:attribute name="pageTitle">Address Book</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">addressBook</jsp:attribute>
		<jsp:attribute name="bodyClass">addressBook</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">Address Book</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>Address Book</h1>
			</div>

			<div class="section-row">
				<ul class="account-grid">

					<dsp:droplet name="SortMapByValueToArrayDefaultFirst">
						<dsp:param name="defaultId" value="${defaultAddress.repositoryId}"/>
						<dsp:param name="map" bean="Profile.secondaryAddresses"/>
						<dsp:oparam name="output">
				  		<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>

							<c:forEach var="shippingAddress" items="${sortedArray}" varStatus="status">
								<dsp:setvalue param="shippingAddress" value="${shippingAddress}"/>

								<li>
									<div class="card">
										<div class="card-title"><dsp:valueof param="shippingAddress.key"/></div>
										<div class="card-content">
											<c:choose>
												<c:when test="${status.count == 1 and not empty defaultAddress}">
													<p class="default-address">Default Address</p>
												</c:when>
												<c:otherwise>
													<dsp:getvalueof var="showMakeDefault" value="true" vartype="java.lang.Boolean"/>
												</c:otherwise>
											</c:choose>
											<p><dsp:valueof param="shippingAddress.value.firstName"/>&nbsp;<dsp:valueof param="shippingAddress.value.lastName"/></p>
											<dsp:getvalueof var="attention" param="shippingAddress.value.attention"/>
											<c:if test="${!empty attention}"><p><span class="label">Attention:</span>${attention}</p></c:if>
											<p><dsp:valueof param="shippingAddress.value.address1"/></p>
											<p><dsp:valueof param="shippingAddress.value.address2"/></p>
											<p><dsp:valueof param="shippingAddress.value.city"/>,&nbsp;<dsp:valueof param="shippingAddress.value.state"/>&nbsp;<dsp:valueof param="shippingAddress.value.postalCode"/></p>
											<p>
												<dsp:getvalueof var="phone" param="shippingAddress.value.phoneNumber" />
												<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
											</p>
										</div>
										<div class="card-links">
											<dsp:a page="editAddress.jsp">
												<dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
												<dsp:param name="nickName" value="${shippingAddress.key}"/>
												Edit Address
											</dsp:a>
											<dsp:a href="${contextPath}/account/ajax/deleteAddressModal.jsp" class="modal-trigger" data-target="delete-address-modal" data-size="small">
												<dsp:param name="nickName" value="${shippingAddress.key}"/>
												Delete Address
											</dsp:a>
											<c:if test="${showMakeDefault == true}">
												<dsp:a bean="ProfileFormHandler.defaultShippingAddress" href="${requestURL}" value="${shippingAddress.key}">
													Make Default Address
												</dsp:a>
											</c:if>
										</div>
									</div>
								</li>

				  		</c:forEach>

						</dsp:oparam>
					</dsp:droplet>

					<li>
						<div class="card new-address">
							<dsp:a href="newAddress.jsp">
								<span class="link-text">
									<span class="icon icon-plus"></span>
									Add New Address
								</span>
							</dsp:a>
						</div>
					</li>
				</ul>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
