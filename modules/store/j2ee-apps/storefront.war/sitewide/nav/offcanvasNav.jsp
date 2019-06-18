<dsp:page>

	<%-- Imports --%>
	<%@ page import="com.mff.commerce.catalog.PrimaryNavItem" %>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/mff/commerce/catalog/PrimaryNavDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Page Variables --%>
	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>

	<dsp:droplet name="PrimaryNavDroplet">
		<dsp:oparam name="output">
			<dsp:getvalueof var="departments" param="departments" />

			<!-- off-canvas menu -->
			<aside class="off-canvas-menu">

				<%-- greeting --%>
				<ul class="off-canvas-list greeting">
					<li class="utility-login recognized">
						Hi&nbsp;<span class="js-username">Customer</span>!
					</li>
				</ul>

				<%-- account --%>
				<ul class="off-canvas-list">
					<li>
						<a data-tag-header="Home" href="${contextPath}/"><span class="icon icon-home" aria-hidden="true"></span> Home</a>
					</li>
					<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
					<li class="utility-home-store">
						<c:choose>
							<c:when test="${empty homeStore}">
								<div class="utility-home-header" aria-controls="home-store">
									<a href="#" data-tag-header="CHOOSE A STORE" class="update-store">
										<span class="icon icon-locator" aria-hidden="true"></span>
										CHOOSE A STORE
									</a>
								</div>
								<div class="utility-nav-menu home-store-menu" id="home-store">
									<div class="store-card">
										<div class="store-location-info">
											<div class="card-title">
												<a href="#" class="disabled ">
													<span class="icon icon-locator" aria-hidden="true"></span>
													My Store
												</a>
											</div>
											<div class="card-content">
												<p>${address1}</p>
												<p>${city},&nbsp;${state}&nbsp;${postalCode}</p>
												<p>${phoneNumber}</p>
												<a href="${website}" alt="view store details">View Store Details</a>
											</div>
										</div>
										<div class="card-links">										
											<div class="store-search-form">
												<div class="store-search-form">
													<a href="#" class="button primary update-store">CHANGE MY STORE</a>
												</div>
											</div>
										</div>
									</div>	
								</div>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="city" bean="Profile.myHomeStore.city"/>
								<dsp:getvalueof var="state" bean="Profile.myHomeStore.stateAddress"/>
								<dsp:getvalueof var="address1" bean="Profile.myHomeStore.address1"/>
								<dsp:getvalueof var="postalCode" bean="Profile.myHomeStore.postalCode"/>
								<dsp:getvalueof var="phoneNumber" bean="Profile.myHomeStore.phoneNumber"/>
								<dsp:getvalueof var="website" bean="Profile.myHomeStore.website"/>
								<div class="utility-home-header force-left" aria-controls="home-store">
									<a href="#" class="home-store-toggle">
										<span class="icon icon-locator" aria-hidden="true"></span>
										${city}, ${state}
									</a>
								</div>
								<div class="utility-nav-menu home-store-menu" id="home-store">
										<div class="store-card">
											<div class="store-location-info">
												<div class="card-title">
													<a href="#" class="disabled ">
														<span class="icon icon-locator" aria-hidden="true"></span>
														My Store
													</a>
												</div>
												<div class="card-content">
													<p>${address1}</p>
													<p>${city},&nbsp;${state}&nbsp;${postalCode}</p>
													<p>${phoneNumber}</p>
													<a href="${website}" alt="view store details">View Store Details</a>
												</div>
											</div>
											<div class="card-links">										
												<div class="store-search-form">
													<div class="store-search-form">
														<a href="#" class="button primary update-store">CHANGE MY STORE</a>
														<!-- <dsp:form formid="store-search-form" id="store-search-form" method="post" name="store-search-form" data-validate>
															<dsp:include page="/sitewide/includes/updateStoreSearchForm.jsp" />
														</dsp:form> -->
													</div>
												</div>
											</div>
										</div>	
									</div>
							</c:otherwise>
						</c:choose>
					</li>
					<%-- guest user --%>
					<li class="utility-login guest">
						<a data-tag-header="My Account" href="${contextPath}/account/login.jsp">
							<span class="icon icon-profile" aria-hidden="true"></span> My Account
						</a>
					</li>

					<%-- fully / partially authenticated --%>
					<li class="utility-login recognized">
						<a href="${contextPath}/account/account.jsp">
							<span class="icon icon-profile" aria-hidden="true"></span> My Account
						</a>
					</li>
					<li class="utility-login recognized">
						<a href="${contextPath}/account/wishList.jsp">
							<span class="icon icon-wish-list" aria-hidden="true"></span> Wish List
						</a>
					</li>
					<li class="utility-login recognized">
						<a href="${contextPath}/account/orderTracking.jsp">
							<span class="icon icon-truck" aria-hidden="true"></span>Track Order
						</a>
					</li>
					<li class="utility-login recognized">
						<dsp:form id="sign-out-form-mobile" formid="sign-out-form-mobile" iclass="sign-out" action="${requestURL}" method="post">
							<dsp:input type="submit" bean="ProfileFormHandler.logout" value="Sign Out" class="sign-out-link" />
						</dsp:form>
					</li>
				</ul>

				<%-- categories --%>
				<ul class="off-canvas-list accordion" role="tablist" aria-multiselectable="false" data-accordion>
					<c:forEach var="department" items="${departments}" varStatus="deptStatus">
						<%-- unique name for accoridion title/container ids --%>
						<c:set var="deptSplit" value="${fn:split(department.url, '_')}" />
						<c:set var="deptNValue" value="${fn:replace(deptSplit[1], '/N-', '')}" />

						<c:choose>
							<c:when test="${not empty department.subcategories}">
								<li class="has-submenu accordion-container">
									<a href="${department.url}" class="accordion-title" role="tab" aria-expanded="false" aria-controls="cat-menu-${deptNValue}" id="cat-title-${deptNValue}">
										${department.displayName} <span class="icon icon-arrow-down white" aria-hidden="true"></span>
									</a>
									<div class="accordion-body" aria-labelledby="cat-title-${deptNValue}" role="tabpanel" id="cat-menu-${deptNValue}">
										<ul aria-hidden="true" class="accordion" role="tablist" aria-multiselectable="false" data-accordion>
											<li>
												<a data-parent-mobile="${department.displayName}" href="${department.url}">Shop All ${department.displayName}</a>
											</li>
											<c:forEach var="category" items="${department.subcategories}" varStatus="categoryStatus">
												<%-- unique name for accoridion title/container ids --%>
												<c:set var="catSplit" value="${fn:split(category.url, '_')}" />
												<c:set var="catNValue" value="${fn:replace(catSplit[1], '/N-', '')}" />
												<c:choose>
													<c:when test="${not empty category.subcategories}">
														<li class="has-submenu accordion-container">
															<a href="${category.url}" class="accordion-title" role="tab" aria-expanded="false" aria-controls="sub-menu-${catName[1]}-${catNValue}" id="sub-title-${catName[1]}-${catNValue}">
																${category.displayName} <span class="icon icon-arrow-down white" aria-hidden="true"></span>
															</a>
															<div class="accordion-body" aria-labelledby="sub-title-${catName[1]}-${catNValue}" role="tabpanel" id="sub-menu-${catName[1]}-${catNValue}">
																<ul>
																	<li>
																		<a data-parent-mobile="${category.displayName}" href="${category.url}">Shop All ${category.displayName}</a>
																	</li>
																	<c:forEach var="subcat" items="${category.subcategories}" varStatus="subcatStatus">
																		<li>
																			<a data-parent-mobile="${category.displayName}" href="${subcat.url}">${subcat.displayName}</a>
																		</li>
																	</c:forEach>
																</ul>
															</div>
														</li>
													</c:when>
													<c:otherwise>
														<li class="accordion-container">
															<a data-parent-mobile="${department.displayName}" href="${category.url}" class="accordion-title" id="sub-title-${catName[1]}-${catNValue}">
																${category.displayName}
															</a>
														</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</ul>
									</div>
								</li>
							</c:when>
							<c:otherwise>
								<li class="accordion-container">
									<a href="${department.url}" class="accordion-title" id="cat-title-${deptNValue}">
										${department.displayName}
									</a>
								</li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>

				<%-- utility nav items --%>
				<ul class="off-canvas-list">
					<li>
						<a href="/c/info/call-us">
							<span class="icon icon-phone" aria-hidden="true"></span>
							<span class="sr-only">Call Us at </span>Call Us&nbsp;
						</a>
					</li>
					<li>
						<a href="${contextPath}/sitewide/storeLocator.jsp" data-tag-header="Store Locator" class="store-locator-nav">
							<span class="icon icon-store-locator" aria-hidden="true"></span>
							Store Locator
						</a>
					</li>
					<li>
						<a data-tag-header="Weekly Ad" href="${contextPath}/sitewide/weeklyAd.jsp">
							<span class="icon icon-weekly-ad" aria-hidden="true"></span>
							Weekly Ad
						</a>
					</li>
					<li>
						<a data-tag-header="Track Order" href="${contextPath}/account/orderTracking.jsp">
							<span class="icon icon-truck" aria-hidden="true"></span>
							Track Order
						</a>
					</li>
					<li>
						<a href="${contextPath}/sitewide/giftCardBalance.jsp">
							<span class="icon icon-card" aria-hidden="true"></span>
							Gift Cards
						</a>
					</li>
				</ul>

				<%-- close button --%>
				<span class="icon icon-menu exit-off-canvas"></span>

			</aside>
			<!-- /off-canvas menu -->

			</dsp:oparam>
	</dsp:droplet>
		<%-- google maps --%>
	<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDV3nXdSNtnvq3h6lKDe5d9NReG9NAwFUQ"></script>
</dsp:page>
