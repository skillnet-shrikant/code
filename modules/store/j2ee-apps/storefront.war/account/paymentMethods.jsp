<%--
  - File Name: paymentMethods.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page displays all the users saved payment methods. Users can also save a new
  							 payment method here.
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/com/mff/droplet/SortMapByValueToArrayDefaultFirst"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

	<layout:default>
		<jsp:attribute name="pageTitle">Payment Methods</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">paymentMethods</jsp:attribute>
		<jsp:attribute name="bodyClass">paymentMethods</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">Payment Methods</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>Payment</h1>
			</div>

			<div class="section-row">
				<ul class="account-grid">

				<dsp:getvalueof var="defaultCreditCard" bean="Profile.defaultCreditCard"/>

				<dsp:droplet name="SortMapByValueToArrayDefaultFirst">
					<dsp:param name="defaultId" value="${defaultCreditCard.repositoryId}"/>
					<dsp:param name="map" bean="Profile.creditCards"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>

							<c:forEach var="creditCard" items="${sortedArray}" varStatus="status">
								<dsp:setvalue param="creditCard" value="${creditCard}"/>

								<dsp:getvalueof param="creditCard.value.creditCardType" var="creditCardType"/>
								<dsp:getvalueof bean="CreditCardTools.reverseCardCodeMap" var="reverseCardCodeMap"/>

								<li>
									<div class="card">
										<div class="card-title">
											<dsp:valueof param="creditCard.key"/>
										</div>
										<div class="card-content">
											<c:choose>
												<c:when test="${status.count == 1 and not empty defaultCreditCard}">
													<p class="default-address">Default Payment Method</p>
												</c:when>
												<c:otherwise>
													<dsp:getvalueof var="showMakeDefault" value="true" vartype="java.lang.Boolean"/>
												</c:otherwise>
											</c:choose>
											<p><dsp:valueof param="creditCard.value.nameOnCard"/></p>
											<p><c:out value="${reverseCardCodeMap[creditCardType]}"/></p>
											<p>XXXX&nbsp;XXXX&nbsp;XXXX<dsp:valueof param="creditCard.value.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
											<p><strong>Exp:</strong>&nbsp;<dsp:valueof param="creditCard.value.expirationMonth"/>/<dsp:valueof param="creditCard.value.expirationYear"/></p>
										</div>
										<div class="card-links">
											<dsp:a href="${contextPath}/account/editPayment.jsp">
												Edit Payment Method
												<dsp:param name="nickName" param="creditCard.key"/>
												<dsp:param name="editMode" value="true"/>
											</dsp:a>
											<dsp:a href="${contextPath}/account/ajax/deletePaymentModal.jsp" class="modal-trigger" data-target="delete-payment-modal" data-size="small">
												Delete Payment Method
												<dsp:param name="nickName" param="creditCard.key"/>
											</dsp:a>
											<c:if test="${showMakeDefault == true}">
												<dsp:a bean="ProfileFormHandler.defaultCard" href="${requestURL}" value="${creditCard.key}">
													Make Default Payment Method
												</dsp:a>
											</c:if>
										</div>
									</div>
								</li>
							</c:forEach>
						</dsp:oparam>
					</dsp:droplet>

					<li>
						<div class="card new-payment">
							<dsp:a href="${contextPath}/account/newPayment.jsp">
								<span class="link-text">
									<span class="icon icon-plus"></span>
									Add New Payment Method
								</span>
							</dsp:a>
						</div>
					</li>

				</ul>
			</div>

		</jsp:body>

	</layout:default>

</dsp:page>
