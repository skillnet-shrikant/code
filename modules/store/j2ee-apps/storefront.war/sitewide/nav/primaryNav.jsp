<dsp:page>

	<%-- Imports --%>
	<%@ page import="com.mff.commerce.catalog.PrimaryNavItem" %>
	<dsp:importbean bean="/mff/commerce/catalog/PrimaryNavDroplet"/>

	<dsp:droplet name="PrimaryNavDroplet">
		<dsp:getvalueof var="departments" param="departments" />
		<dsp:oparam name="output">

			<!--Primary Nav -->
			<div class="primary-nav" data-primarynav>
				<nav>
					<section class="primary-nav-item department-dropdown">
						<h2 class="primary-nav-button" id="department-nav-button">
							<a href="#" class="nav-link">Shop Departments<span class="icon icon-arrow-down"></span></a>
						</h2>
						<div class="primary-nav-menu" id="department-nav-menu" data-flyoutnav>
							<ul>
								<c:forEach var="department" items="${departments}" varStatus="status">
									<li>
										<h3 class="sub-nav-button">
											<a href="${department.url}" data-parent="Shop Departments" class="nav-link">${department.displayName}<c:if test="${not empty department.subcategories}"><span class="icon icon-arrow-right"></c:if></span></a>
										</h3>
										<c:if test="${not empty department.subcategories}">
											<div class="sub-nav-menu">
												<ul>
													<c:forEach var="category" items="${department.subcategories}" varStatus="status">
														<li>
															<h3 class="sub-nav-title">
																<a href="${category.url}" data-parent="${department.displayName}" class="nav-link">${category.displayName}</a>
															</h3>
															<c:if test="${not empty category.subcategories}">
																<ul class="sub-nav-list">
																	<c:forEach var="subcategory" items="${category.subcategories}" varStatus="status">
																		<li>
																			<a href="${subcategory.url}" data-parent="${category.displayName}" class="nav-link">${subcategory.displayName}</a>
																		</li>
																	</c:forEach>
																</ul>
															</c:if>
														</li>
													</c:forEach>
												</ul>
											</div>
										</c:if>
									</li>
								</c:forEach>
								<%-- SERVICES - should always be the last list item --%>
								<li class="services-nav">
									<h3 class="sub-nav-button">
										<a href="http://www.fleetfarmtires.com/auto-repairs.aspx" data-parent="Shop Departments" class="nav-link" target="_blank">Auto Service</a>
									</h3>
								</li>
								<li>
									<h3 class="sub-nav-button">
										<a href="http://www.fleetfarmtires.com" data-parent="Shop Departments" class="nav-link" target="_blank">Tires</a>
									</h3>
								</li>
								<li>
									<h3 class="sub-nav-button">

										<a href="${contextPath}/sitewide/storeLocator.jsp" data-parent="Shop Departments" class="nav-link">Store Locations</a>

									</h3>
								</li>
								<li>
									<h3 class="sub-nav-button">
										<a href="${contextPath}/sitewide/giftCardBalance.jsp" data-parent="Shop Departments" class="nav-link">Gift Cards</a>
									</h3>
								</li>
							</ul>
						</div>
					</section>
					<!-- the top level search bar  -->
					<dsp:getvalueof var="escapeSpecialChars" bean="/mff/MFFEnvironment.escapeSearchSpecialCharacters" />
					<section class="primary-nav-item search-form">
						<div id="keyword-search" class="keyword-search">
							<div class="keyword-search-bar">
								<form id="search-desktop" class="keyword-search-form" action="${contextPath}/search">
									<label class="sr-only" for="Ntt">keyword search</label>
									<div class="field-group inline-form">
										<span class="icon icon-search" aria-hidden="true"></span>
										<input type="text" name="Ntt" id="Ntt" class="keyword-search-field" placeholder="Start typing to search" autocomplete="off" autocorrect="off" value="" data-escape-special-char="${escapeSpecialChars}" data-typeahead required/>
										<button id="search" class="keyword-search-button button primary" aria-label="search">
											<span class="icon icon-search" aria-hidden="true"></span>
											Search
										</button>
									</div>
								</form>
							</div>
							<div class="typeahead hide" id="typeahead">
								<div class="typeahead-container">
									<div class="typeahead-suggestions"></div>
									<div class="typeahead-details"></div>
								</div>
							</div>
						</div>
					</section>
				</nav>
			</div>
			<!--/Primary Nav -->

		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
