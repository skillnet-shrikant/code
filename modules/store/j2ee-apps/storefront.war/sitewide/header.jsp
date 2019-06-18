<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="date" bean="/atg/dynamo/service/CurrentDate"/>

	<!-- header -->
	<header class="desktop-header">

		<%-- masthead --%>
		<div class="header-masthead">
			<dsp:droplet name="InvokeAssembler">
				<dsp:param name="includePath" value=""/>
				<dsp:param name="contentCollection" value="/content/Shared/Global Header"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="headerLogoContent" vartype="com.endeca.infront.assembler.ContentItem" param="contentItem" />
				</dsp:oparam>
			</dsp:droplet>
			<c:choose>
				<c:when test="${not empty headerLogoContent.contents}">
					<c:forEach var="headerElement" items="${headerLogoContent.contents}">
						<dsp:renderContentItem contentItem="${headerElement}" >
							<dsp:param name="mobile" value="false"/>
						</dsp:renderContentItem>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="logo">
						<a data-action="Fleet Farm Logo" href="${contextPath}/">
							<img src="${contextPath}/resources/images/new_logo.jpg" alt="Fleet Farm Logo" />
						</a>
					</div>
				</c:otherwise>
			</c:choose>
			<%-- utility nav --%>
			<div class="utility-nav">
				<nav class="nav-top">
					<ul>
						<li class="utility-nav-item">
							<div class="utility-nav-header">
								<a href="/c/info/call-us">
									<span class="icon icon-phone" aria-hidden="true"></span>
									<span class="sr-only">Call Us at </span>Call Us&nbsp;
								</a>
							</div>
						</li>
						<li class="utility-nav-item">
							<div class="utility-nav-header">
								<a href="${contextPath}/sitewide/storeLocator.jsp" class="store-locator-nav">
									<span class="icon icon-store-locator" aria-hidden="true"></span>
									Store Locator
								</a>
							</div>
						</li>
						<li class="utility-nav-item">
							<div class="utility-nav-header">
								<a href="${contextPath}/sitewide/weeklyAd.jsp">
									<span class="icon icon-weekly-ad" aria-hidden="true"></span>
									Weekly Ad
								</a>
							</div>
						</li>
						<li class="utility-nav-item">
							<div class="utility-nav-header">
								<a href="${contextPath}/account/orderTracking.jsp">
									<span class="icon icon-truck" aria-hidden="true"></span>
									Track Order
								</a>
							</div>
						</li>
						<li class="utility-nav-item">
							<div class="utility-nav-header">
									<a href="/sitewide/giftCardBalance.jsp">
										<span class="icon icon-card" aria-hidden="true"></span>
										Gift Cards
									</a>
								</div>
						</li>
					</ul>
				</nav>

				<nav class="nav-bottom">
					<ul>
						<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
						<%-- home store --%>
						<c:choose>
							<c:when test="${empty homeStore}">
								<%-- empty home store --%>
								<li class="utility-nav-item utility-home-store dropdown" >
									<div class="utility-home-header" aria-controls="home-store">
										<a href="#" class="update-store">
											<span class="icon icon-locator" aria-hidden="true"></span>
											CHOOSE A STORE
										</a>
									</div>
									<div class="utility-home-menu dropdown-menu home-store-menu" id="home-store">
										<div class="store-card">
											<div class="store-location-info">
											</div>
											<div class="card-links">
												<div class="label"><span>SELECT ANOTHER STORE</span></div>
												<div class="update-search-form">
													<dsp:form formid="update-search-form" id="update-search-form" method="post" name="update-search-form" data-validate>
														<dsp:include page="/sitewide/includes/updateStoreSearchForm.jsp" />
													</dsp:form>
												</div>
											</div>
										</div>
									</div>
								</li>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="city" bean="Profile.myHomeStore.city"/>
								<dsp:getvalueof var="state" bean="Profile.myHomeStore.stateAddress"/>
								<dsp:getvalueof var="address1" bean="Profile.myHomeStore.address1"/>
								<dsp:getvalueof var="phoneNumber" bean="Profile.myHomeStore.phoneNumber"/>
								<dsp:getvalueof var="postalCode" bean="Profile.myHomeStore.postalCode"/>
								<dsp:getvalueof var="website" bean="Profile.myHomeStore.website"/>

								<li class="utility-nav-item utility-home-store dropdown">
									<div class="utility-home-header force-left" aria-controls="home-store">
										<a href="#" class="home-store-toggle">
											<span class="icon icon-locator" aria-hidden="true"></span>
											${city}, ${state}
										</a>
									</div>
									<div class="utility-nav-menu dropdown-menu home-store-menu" id="home-store">
										<div class="store-card">
											<div class="store-location-info">
												<div class="card-title">
													<a href="#" class="disabled ">
														<span class="icon icon-locator" aria-hidden="true"></span>
														&nbsp;My Store
													</a>
												</div>
												<div class="card-content">
													<p class="title">${city}, ${state}</p>
													<a href="${website}" alt="view store details" class="view-store">View Store Details</a>
												</div>
												<div class="card-content">
													<p>${address1}</p>
													<p>${city},&nbsp;${state}&nbsp;${postalCode}</p>
													<p>${phoneNumber}</p>
													<a href="${website}" alt="view store details" class="hide-store">View Store Details</a>
												</div>
											</div>
											<div class="card-links">
												<div class="label"><span>SELECT ANOTHER STORE</span></div>
												<div class="update-search-form">
													<dsp:form formid="update-search-form" id="update-search-form" method="post" name="update-search-form" data-validate>
														<dsp:include page="/sitewide/includes/updateStoreSearchForm.jsp" />
													</dsp:form>
												</div>
											</div>
										</div>
									</div>
								</li>
							</c:otherwise>
						</c:choose>
						<%-- guest user --%>
						<li class="utility-nav-item utility-login guest">
							<div class="utility-nav-header">
								<a href="${contextPath}/account/login.jsp">My Account</a>
							</div>
						</li>

						<%-- fully / partially authenticated --%>
						<li class="utility-nav-item utility-login recognized dropdown" data-dropdown>
							<div class="utility-nav-header dropdown-toggle force-left" aria-controls="utility-login">
								<a href="#">Hi&nbsp;<span class="js-username">Customer</span>!</a>
							</div>
							<div class="utility-nav-menu dropdown-menu" id="utility-login">
								<ul class="menu-list">
									<li>
										<a href="${contextPath}/account/account.jsp">My Account</a>
									</li>
									<li>
										<a href="${contextPath}/account/wishList.jsp">Wish List</a>
									</li>
									<li>
										<a href="${contextPath}/account/orderTracking.jsp">Order Status</a>
									</li>
									<li>
										<dsp:form id="sign-out-form" formid="sign-out-form" iclass="sign-out" action="${requestURL}" method="post">
											<dsp:input type="submit" bean="ProfileFormHandler.logout" value="Sign Out" class="sign-out-link" />
										</dsp:form>
									</li>
								</ul>
							</div>
						</li>

						<%-- lisa : make search bar always visible --%>
						<%--
						<li class="utility-nav-item">
							<div class="utility-nav-header">
								<a href="#" id="search-bar-toggle">
									<span class="icon icon-search" aria-hidden="true"></span>
									<span class="sr-only">Keyword Search</span>
								</a>
							</div>
						</li>
						--%>
						<li class="utility-nav-item">
							<div class="utility-nav-header">
								<a href="#" data-action="cart" class="side-cart-toggle">
									<span aria-hidden="true" class="icon icon-cart"></span>
									<span class="cart-count">0</span>
									<span class="sr-only">Cart</span>
								</a>
							</div>
						</li>
					</ul>
				</nav>
			</div>
		</div>
		<%-- primary nav --%>
		<c:import url="/sitewide/nav/primaryNav.jsp"/>
		<c:choose>
			<c:when test="${(contentItem['@type'] =='GardenCenterPage' || contentItem['@type'] == 'PromoPage') && not empty contentItem.HeaderContent }">
				<c:forEach var="headerContent" items="${contentItem.HeaderContent}">
					<dsp:renderContentItem contentItem="${headerContent}" />
				</c:forEach>
			</c:when>
			<c:when test="${not empty contentItem.contents[0].HeaderContent}">
				<c:forEach var="headerContent" items="${contentItem.contents[0].HeaderContent}">
					<dsp:renderContentItem contentItem="${headerContent}" />
				</c:forEach>
			</c:when>
			<c:otherwise>
				<dsp:droplet name="InvokeAssembler">
					<dsp:param name="includePath" value=""/>
					<dsp:param name="contentCollection" value="/content/Shared/Global Banner"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="contentSlotContent" vartype="com.endeca.infront.assembler.ContentItem" param="contentItem" />
					</dsp:oparam>
				</dsp:droplet>
				<c:choose>
					<c:when test="${not empty contentSlotContent.contents}">
						<c:forEach var="contentElement" items="${contentSlotContent.contents}">
							<dsp:renderContentItem contentItem="${contentElement}" />
						</c:forEach>
					</c:when>
					<c:otherwise>
						<%-- global promo bar --%>
							<dsp:droplet name="RQLQueryForEach">
								<dsp:param name="queryRQL" value="contentKey=:contentKey" />
								<dsp:param name="contentKey" value="0000" />
								<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
								<dsp:param name="itemDescriptor" value="mffStaticContent"/>
								<dsp:param name="elementName" value="contentItem"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="startDate" param="contentItem.startDate"/>
									<dsp:getvalueof var="endDate" param="contentItem.endDate"/>
									<c:if test="${(date.timeAsTimestamp < endDate) || (null eq endDate)}">
										<c:if test="${(date.timeAsTimestamp > startDate) || (null eq startDate)}">
											<dsp:getvalueof var="redirectUrl" param="contentItem.redirectUrl"/>
											<div id="global-promo" class="global-promo open">
												<div class="global-promo-container">
													<div class="global-promo-content">
														<div class="global-promo-text">
															<dsp:valueof param="contentItem.displayName" valueishtml="true" /> <a href="${redirectUrl}"><dsp:valueof param="contentItem.urlTitle" valueishtml="true" /></a>
														</div>
														<%-- lisa : remove global promo close button --%>
														<%--<span class="icon icon-close" data-target="#global-promo" aria-hidden="true"></span>--%>
													</div>
												</div>
											</div>
										</c:if>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
					</c:otherwise>
				</c:choose>


			</c:otherwise>
		</c:choose>

	</header>
	<!-- /header -->
	<dsp:form id="home-store-form" formid="home-store-form" action="${requestURL}" method="post">
	 	<dsp:input bean="ProfileFormHandler.homeStoreChosen" name="homestore" type="hidden" id="homestore"/>
	 	<!-- <dsp:input bean="ProfileFormHandler.updateMyHomeStore" type="submit" value="Set Home Store" iclass="hide"/> -->
	 	<dsp:input type="hidden" bean="ProfileFormHandler.updateHomeStoreSuccessURL" value="${contextPath}/sitewide/json/updateMyHomeStoreSuccess.jsp" id="home-store-successUrl"/>
		<dsp:input type="hidden" bean="ProfileFormHandler.updateHomeStoreErrorURL" value="${contextPath}/account/json/profileError.jsp" />
	 	<dsp:input type="hidden" bean="ProfileFormHandler.updateMyHomeStore" id="choose-store" name="choose-store" value="Set Home Store" />
	</dsp:form>
	<%-- google maps 
	<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDV3nXdSNtnvq3h6lKDe5d9NReG9NAwFUQ"></script>--%>
</dsp:page>
