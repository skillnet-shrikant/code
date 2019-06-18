<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:set var="contextPath"  value="${pageContext.servletContext.contextPath}" scope="request" />
	<dsp:getvalueof var="storeItem" param="storeItem"/>
	<dsp:getvalueof var="storeId" param="storeItem.locationId"/>
	<%-- breadcrumbs --%>
	<section class="breadcrumbs">
		<ul aria-label="breadcrumbs" role="navigation">
			<li><a href="${contextPath}/" class="crumb">Home</a></li>
			<li><a href="${contextPath}/sitewide/storeLocator.jsp" class="crumb">Store Locator</a></li>
			<li><span class="crumb active"><dsp:valueof  param="storeItem.pageStoreName"/></span></li>
		</ul>
	</section>
	
	<dsp:getvalueof var="homeStoreId" vartype="java.lang.Object" bean="Profile.myHomeStore.locationId"/>
	<dsp:getvalueof var="storeName" param="storeItem.pageStoreName"/>
	<dsp:getvalueof var="isComingSoon" param="storeItem.pageComingSoonEnabled"/>
	
	<c:if test="${not empty storeName}">
		<section>
			<div class="section-row">
				<div class="section-content">
					<div class="two-column-container" itemscope itemtype="http://schema.org/LocalBusiness">
						<div class="two-column-left">
							<div class="home-store-section hide-on-mobile">
								<c:choose>
									<c:when test="${isComingSoon}">
										<button class="button expand primary">buy online pick up in store</button>
									</c:when>
									<c:when test="${homeStoreId eq storeId}">
										<button class="button expand primary-dark"><span class="icon icon-locator"></span>&nbsp;MY STORE</button>
									</c:when>
									<c:otherwise>
										<button class="button expand primary make-this-store" data-store-id="${storeId}">MAKE THIS MY PICK UP STORE</button>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="store-details-nav-list">
								<div id="${storeId}" class="store-location-details">
									<div class="store_${storeId}" >
										<span class="hide" itemprop="name">Fleet Farm</span>
										<span itemprop="image" class="hide"><dsp:valueof param="storeItem.storeLandingPage.mainImage.url"/></span>
										<strong class="store-title"><dsp:valueof  param="storeItem.pageStoreName"/></strong>
									</div>
									<div class="address" itemprop="address" itemscope itemtype="http://schema.org/PostalAddress">
										<span itemprop="streetAddress" class="hide"><dsp:valueof param="storeItem.address1"/><dsp:valueof param="storeItem.address2"/></span>
										<span class="hide" itemprop="addressLocality"><dsp:valueof param="storeItem.city"/></span>
										<span class="hide" itemprop="addressRegion"><dsp:valueof param="storeItem.stateAddress"/></span>
										<span class="hide" itemprop="postalCode"><dsp:valueof param="storeItem.postalCode"/></span>
										<span itemprop="telephone" class="hide"><dsp:valueof param="storeItem.phoneNumber"/></span>
										<div class="">
											<dsp:valueof  param="storeItem.pageStoreAddress"/>
											<dsp:getvalueof var="pageStoreAddress" param="storeItem.pageStoreAddress"/>
										</div>
										<strong>
											<dsp:valueof param="storeItem.pagePhoneNumber"/>
										</strong><br>
									</div>
								</div>
								<dsp:getvalueof param="storeItem.todayStoreHours.isClosed" var="isClosed"/>
								<div class="store-today-info">
									<c:choose>
										<c:when test="${isComingSoon}">
											<span class="button primary">Location coming soon!</span>
										</c:when>
										<c:when test="${isClosed}">
											<span class="button primary"><dsp:valueof  param="storeItem.todayStoreHours.dayType"/></span>
											<span>Closed</span>
										</c:when>
										<c:otherwise>
											<span class="button primary"><dsp:valueof  param="storeItem.todayStoreHours.dayType"/></span>
											<span class="store-time"><dsp:valueof  param="storeItem.todayStoreHours.openingTime" date="h:mmaa"/>&nbsp;-&nbsp;<dsp:valueof  param="storeItem.todayStoreHours.closingTime" date="h:mmaa"/></span>
										</c:otherwise>
									</c:choose>
								</div>
								<div class="actions">
									<dsp:getvalueof var="storeLatitude" param="storeItem.latitude" />
									<dsp:getvalueof var="storeLongitude" param="storeItem.longitude" />
									<dsp:getvalueof var="mapUrl" value="https://www.google.com/maps/dir//Fleet+Farm,+${pageStoreAddress},+USA/@${storeLatitude},${storeLongitude},14z/" />
									<a class="button tertiary" target="_blank" href="${fn:replace(mapUrl, ' ', '+') }">directions</a>
									<c:choose>
										<c:when test="${isComingSoon}">
											<a class="button dark call-to-mobile disabled" href="">call</a>
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="storePhoneNumber" param="storeItem.phoneNumber" />
											<c:set var="storePhoneNumberWs">${storePhoneNumber.replaceAll("[^0-9]", "")}</c:set>
											<a class="button dark call-to-mobile" id="phone" href="tel:+1-${fn:substring(storePhoneNumberWs,0,3)}-${fn:substring(storePhoneNumberWs,3,6)}-${fn:substring(storePhoneNumberWs,6,10)}">call</a>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="home-store-section show-on-mobile">
								<c:choose>
									<c:when test="${isComingSoon}">
										<button class="button expand primary">buy online pick up in store</button>
									</c:when>
									<c:when test="${homeStoreId eq storeId}">
										<button class="button expand primary-dark"><span class="icon icon-locator"></span>&nbsp;MY STORE</button>
									</c:when>
									<c:otherwise>
										<button class="button expand primary make-this-store" data-store-id="${storeId}">MAKE THIS MY PICK UP STORE</button>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="two-column-right">
							<div class="map-container">
								<div id="map"></div>
							</div>
						</div>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="storeItem.standardStoreHoursList" />
							<dsp:param name="elementName" value="storeHours" />
							<dsp:oparam name="output">
								<meta itemprop="openingHours" content='<dsp:valueof param="storeHours.dayType"/>&nbsp;<dsp:valueof param="storeHours.openingTime" date="HH:mm"/>-<dsp:valueof param="storeHours.closingTime" date="HH:mm"/>'/>
							</dsp:oparam>
						</dsp:droplet>
					</div>
				</div>
			</div>
		</section>
			
	</c:if>
	
</dsp:page>