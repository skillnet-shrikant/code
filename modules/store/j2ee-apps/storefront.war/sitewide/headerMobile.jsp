<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="date" bean="/atg/dynamo/service/CurrentDate"/>

	<!-- mobile header -->
	<header class="mobile-header accordion" role="tablist" aria-multiselectable="false" data-accordion>

		<%-- masthead --%>
		<div class="masthead-mobile">

			<%-- off-canvas toggle --%>
			<a href="#" class="masthead-mobile-link masthead-mobile-link-menu off-canvas-toggle">
				<span class="icon icon-menu" aria-hidden="true"></span>
				<span class="sr-only">Menu</span>
			</a>

			<%-- search bar toggle --%>
			<div class="left masthead-mobile-link masthead-mobile-link-search accordion-title" role="tab" aria-expanded="false" aria-controls="mobile-search-dropdown" id="mobile-search-icon">
				<span class="icon icon-search" aria-hidden="true"></span>
				<span class="sr-only">Search</span>
			</div>

			<%-- side cart toggle --%>
			<div class="right masthead-mobile-link masthead-mobile-link-cart side-cart-toggle">
				<span class="icon icon-cart" aria-hidden="true"></span>
				<span class="cart-count">0</span>
				<span class="sr-only">Cart</span>
			</div>
			<%--
			<div class="right masthead-mobile-link masthead-mobile-link-locator accordion-title" role="tab" aria-expanded="false" aria-controls="mobile-locator-dropdown" id="mobile-locator-icon">
				<span class="icon icon-locator large" aria-hidden="true"></span>
				<span class="sr-only">Stores</span>
			</div>
			--%>

			<%-- logo --%>
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
							<dsp:param name="mobile" value="true"/>
						</dsp:renderContentItem>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="masthead-logo">
						<a href="${contextPath}/">
							<img src="${assetPath}/images/new_logo.png" alt="Fleet Farm">
						</a>
					</div>
				</c:otherwise>
			</c:choose>

		</div>

		<%-- search bar --%>
		<div class="accordion-container search-container">
			<div class="accordion-body" aria-labelledby="mobile-search-icon" role="tabpanel" id="mobile-search-dropdown">
				<div class="keyword-search">
					<div class="mobile-keyword-search-bar">
						<form id="search-mobile" class="keyword-search-form" action="${contextPath}/search" novalidate>
							<label class="sr-only" for="Ntt-mobile">keyword search</label>
							<div class="field-group inline-form">
								<div class="search-field">
									<input type="text" name="Ntt" id="Ntt-mobile" class="keyword-search-field" placeholder="Start typing to search" autocomplete="off" autocorrect="off" value="" required/>
									<button id="mobile-search" class="keyword-search-button button primary" aria-label="search">
										<span class="icon icon-search" aria-hidden="true"></span>
									</button>
									<div class="typeahead hide"></div>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<%-- global promo bar --%>
		<%-- added condition to drive global promos from xm 2816 --%>
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
	<dsp:form id="home-store-form" formid="home-store-form" action="${requestURL}" method="post">
 	<dsp:input bean="ProfileFormHandler.homeStoreChosen" name="homestore" type="hidden" id="homestore"/>
 	<!-- <dsp:input bean="ProfileFormHandler.updateMyHomeStore" type="submit" value="Set Home Store" iclass="hide"/> -->
 	<dsp:input type="hidden" bean="ProfileFormHandler.updateHomeStoreSuccessURL" value="${contextPath}/sitewide/json/updateMyHomeStoreSuccess.jsp" />
	<dsp:input type="hidden" bean="ProfileFormHandler.updateHomeStoreErrorURL" value="${contextPath}/account/json/profileError.jsp" />
 	<dsp:input type="hidden" bean="ProfileFormHandler.updateMyHomeStore" id="choose-store" name="choose-store" value="Set Home Store" />
</dsp:form>

	<c:import url="/sitewide/nav/offcanvasNav.jsp"/>
	<!-- /mobile header -->

</dsp:page>
