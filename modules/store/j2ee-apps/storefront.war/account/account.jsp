<%--
  - File Name: account.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the login page for My Account
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/com/mff/account/order/droplet/MFFOrderHistoryDroplet"/>
	<dsp:importbean bean="/atg/commerce/pricing/AvailableShippingMethods"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="taxExemptions" vartype="java.lang.Object" bean="Profile.taxExemptions"/>

	<layout:default>
		<jsp:attribute name="pageTitle">My Account</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">account</jsp:attribute>
		<jsp:attribute name="bodyClass">account</jsp:attribute>
		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><span class="crumb active">My Account </span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>My Account</h1>
			</div>

			<div class="section-row">
				<ul class="account-grid">

					<%-- my orders --%>
					<li>
						<div class="card">
							<div class="card-title">My Orders</div>
							<div class="card-content">
								<p>
									Track and get details on recent orders.
								</p>
							</div>
							<div class="card-content">
								<p class="title"><a href="${contextPath}/account/orderTracking.jsp">Order Tracking</a></p>
								<dsp:droplet name="MFFOrderHistoryDroplet">
								<dsp:param name="userId" bean="Profile.id"/>
									<dsp:oparam name="empty">
										<p>You have no orders</p>
									</dsp:oparam>
									<dsp:oparam name="output">
										<dsp:droplet name="Range">
											<dsp:param name="array" param="orders"/>
											<dsp:param name="howMany" value="3"/>
											<dsp:param name="elementName" value="order"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="orderNumber" param="order.orderNumber"/>
												<div class="line-item">
													<p class="title">
														<c:choose>
															<c:when test="${not empty orderNumber}">
																${orderNumber}
															</c:when>
															<c:otherwise>
																<dsp:valueof param="order.id"/>
															</c:otherwise>
														</c:choose>
													</p>
													<dsp:a href="${contextPath}/account/orderDetails.jsp">
														View Details
														<dsp:param name="orderId" param="order.id"/>
														<dsp:param name="lgc" param="order.legacyOrder"/>
													</dsp:a>
												</div>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</div>
							<div class="card-links">
								<a href="orderHistory.jsp" class="button primary outline">View All Orders</a>
							</div>
						</div>
					</li>

					<%-- my profile --%>
					<li>
						<div class="card">
							<div class="card-title">My Profile</div>
							<div class="card-content">
								<p><dsp:valueof bean="Profile.firstName"/>&nbsp;<dsp:valueof bean="Profile.lastName"/></p>
								<p class="long-text-wrap"><dsp:valueof bean="Profile.email"/></p>
								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
									<dsp:param name="value" param="Profile.phoneNumber"/>
									<dsp:oparam name="false">
										<p>
											<dsp:getvalueof var="phone" param="Profile.phoneNumber" />
											<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
										</p>
									</dsp:oparam>
								</dsp:droplet>
								<p><dsp:valueof bean="Profile.gender"/></p>
							</div>
							<div class="card-links">
								<dsp:a href="changeEmail.jsp">Change Email</dsp:a>
								<a href="changePassword.jsp">Change Password</a>
								<a href="profile.jsp" class="button primary outline">View My Profile</a>
							</div>
						</div>
					</li>
					
					<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
					
					<%-- my store --%>
					<li class="home-store">
						<div class="card">
							<div class="card-title">My Store</div>

							<c:choose>
								<c:when test="${empty homeStore}">
									<%-- empty home store --%>
									<div class="card-content">
										<p>You have not chosen a home store location.</p>
									</div>
									<div class="card-links">
										<a href="${contextPath}/" class="button primary outline update-store">choose a store</a>
									</div>
								</c:when>
								<c:otherwise>
									<%-- home store was set --%>
										<dsp:getvalueof var="city" bean="Profile.myHomeStore.city"/>
										<dsp:getvalueof var="state" bean="Profile.myHomeStore.stateAddress"/>
										<dsp:getvalueof var="website" bean="Profile.myHomeStore.website" />
										
									<div class="card-content">
										<p class="title">${city}, ${state}</p>
										<p><dsp:valueof bean="Profile.myHomeStore.address1"/></p>
										<p><dsp:valueof bean="Profile.myHomeStore.address2"/></p>
										<p>${city},&nbsp;${state}&nbsp;<dsp:valueof bean="Profile.myHomeStore.postalCode"/></p>
										<p><dsp:valueof bean="Profile.myHomeStore.phoneNumber"/></p>
										<dsp:a href="${website}">View Store Details</dsp:a>
									</div>
									<div class="card-links">
										<a href="#" class="button primary outline update-store">Change my store</a>
									</div>
								</c:otherwise>
							</c:choose>

						</div>
					</li>

					<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist"/>
					<dsp:getvalueof var="items" param="wishlist.giftlistItems"/>
					<dsp:getvalueof var="wishListCount" value="${fn:length(items)}"/>

					<%-- wish list --%>
					<li>
						<div class="card">
							<div class="card-title">Wish List</div>

							<c:choose>
								<c:when test="${empty items}">
									<%-- empty wish list --%>
									<div class="card-content">
										<p>You don't have any items saved in your Wish List.</p>
									</div>
									<div class="card-links">
										<a href="${contextPath}/" class="button primary outline">SHOP NOW</a>
									</div>
								</c:when>
								<c:otherwise>
									<%-- wish list has items --%>
									<div class="card-content">
										<p>
											You have <strong>${wishListCount}</strong> item<c:if test="${wishListCount > 1}">s</c:if>
											saved for later. Share them with your friends and family, print
											a list to shop in-store or buy them now.
										</p>
									</div>
									<div class="card-links">
										<a href="wishList.jsp" class="button primary outline">View Wish List</a>
									</div>
								</c:otherwise>
							</c:choose>

						</div>
					</li>

					<%-- address book --%>
					<li>
						<div class="card">
							<div class="card-title">Address Book</div>
							<div class="card-content">
								<dsp:getvalueof var="defaultAddress" bean="Profile.shippingAddress"/>
								<c:choose>
									<c:when test="${not empty defaultAddress}">
										<p class="title">Default Shipping Address</p>
										<p><dsp:valueof bean="Profile.shippingAddress.address1"/></p>
										<p><dsp:valueof bean="Profile.shippingAddress.address2"/></p>
										<p><dsp:valueof bean="Profile.shippingAddress.city"/>,&nbsp;<dsp:valueof bean="Profile.shippingAddress.state"/>&nbsp;<dsp:valueof bean="Profile.shippingAddress.postalCode"/></p>
									</c:when>
									<c:otherwise>
										<p> No Default Shipping Address.</p>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="card-links">
								<a href="addressBook.jsp" class="button primary outline">View Address Book</a>
							</div>
						</div>
					</li>

					<%-- payment --%>
					<li>
						<div class="card">
							<div class="card-title">Payment</div>
							<div class="card-content">
								<dsp:getvalueof var="defaultCardId" bean="Profile.defaultCreditCard.id"/>

								<c:choose>
									<c:when test="${not empty defaultCardId}">
										<dsp:getvalueof bean="CreditCardTools.reverseCardCodeMap" var="reverseCardCodeMap"/>

										<dsp:droplet name="ForEach">
											<dsp:param name="array" bean="Profile.creditCards"/>
											<dsp:param name="elementName" value="creditCard"/>
											<dsp:oparam name="outputStart">
												<p class="title">Default Payment Method</p>
											</dsp:oparam>
											<dsp:oparam name="output">
												<dsp:getvalueof var="cardId" param="creditCard.id"/>
												<dsp:getvalueof param="creditCard.creditCardType" var="creditCardType"/>
												<c:if test="${cardId eq defaultCardId}">

													<%-- displaying card nick name --%>
													<p><strong>Nickname:</strong>&nbsp;<dsp:valueof param="key"/> </p>
													<%-- displaying card type --%>
													<p><c:out value="${reverseCardCodeMap[creditCardType]}"/></p>
													<p>XXXX&nbsp;XXXX&nbsp;XXXX<dsp:valueof param="creditCard.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
												</c:if>
											</dsp:oparam>
										</dsp:droplet>

								</c:when>
									<c:otherwise>
										<p> No Default Payment Available.</p>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="card-links">
								<%--<a href="paymentInfo.jsp">Update Default Payment Method</a>--%>
								<a href="paymentMethods.jsp" class="button primary outline">View Saved Payments</a>
							</div>
						</div>
					</li>

					<%-- tax exemptions --%>
					<li>
						<div class="card">
							<div class="card-title">Tax Exemptions</div>
							<div class="card-content">
								<c:choose>
									<c:when test="${not empty taxExemptions}">
										<c:forEach var="taxExemption" items="${taxExemptions}" varStatus="status">
											<dsp:getvalueof var="nickName" value="${taxExemption.key}"/>
											<div class="line-item">
												<p class="title">${nickName}</p>
												<dsp:a page="editTaxExemption.jsp">
													<dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
													<dsp:param name="addEditMode" value="edit"/>
													<dsp:param name="nickName" value="${nickName}"/>
													Edit
												</dsp:a>
											</div>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<p> No tax exemptions saved.</p>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="card-links">
								<a href="taxExemptions.jsp" class="button primary outline">View Tax Exemptions</a>
							</div>
						</div>
					</li>

					<%-- express checkout --%>
					<li>
						<div class="card">
							<div class="card-title">Express Checkout</div>
							<dsp:getvalueof bean="Profile.expressCheckout" var="expressCheckout"/>

							<c:choose>
								<c:when test="${expressCheckout eq true}">

									<dsp:getvalueof bean="Profile.shippingAddress" var="shippingAddress"/>
									<div class="card-content">
										<p class="title">Shipping Address</p>
										<p><dsp:valueof bean="Profile.shippingAddress.firstName"/>&nbsp;<dsp:valueof bean="Profile.shippingAddress.lastName"/></p>
										<p><dsp:valueof bean="Profile.shippingAddress.address1"/></p>
										<p><dsp:valueof bean="Profile.shippingAddress.address2"/></p>
										<p><dsp:valueof bean="Profile.shippingAddress.city"/>,&nbsp;<dsp:valueof bean="Profile.shippingAddress.state"/>&nbsp;<dsp:valueof bean="Profile.shippingAddress.postalcode"/></p>
									</div>
									
									<dsp:getvalueof var="shippingMethodNames" bean="AvailableShippingMethods.shippingMethodNamesMap"/>
									<dsp:getvalueof var="defaultCarrier" bean="Profile.defaultCarrier" />

									<div class="card-content">
										<p class="title">Shipping Method</p>
										<p>${shippingMethodNames[defaultCarrier]}</p>
									</div>
									<dsp:getvalueof bean="Profile.defaultCreditCard.creditCardType" var="defaultCardType"/>
									<dsp:getvalueof var="defaultCardId" bean="Profile.defaultCreditCard.id"/>

									<dsp:droplet name="ForEach">
										<dsp:param name="array" bean="Profile.creditCards"/>
										<dsp:param name="elementName" value="creditCard"/>
										<dsp:oparam name="output">
											<dsp:getvalueof param="creditCard.id" var="cardId"/>
											<c:if test="${cardId eq defaultCardId}">
												<div class="card-content">
													<p class="title">Payment Method</p>
													<p><strong>Nickname:</strong> <dsp:valueof param="key"/> </p>
													<p><c:out value="${reverseCardCodeMap[defaultCardType]}"/></p>
													<p>XXXX&nbsp;XXXX&nbsp;XXXX<dsp:valueof bean="Profile.defaultCreditCard.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
												</div>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
								</c:when>
								<c:otherwise>
									<div class="card-content">
										<p>Express Checkout needs to be setup.</p>
									</div>
								</c:otherwise>
							</c:choose>

							<div class="card-links">
								<a href="expressCheckout.jsp" class="button primary outline">Update</a>
							</div>
						</div>
					</li>

				</ul>
			</div>
			
		</jsp:body>

	</layout:default>

</dsp:page>
