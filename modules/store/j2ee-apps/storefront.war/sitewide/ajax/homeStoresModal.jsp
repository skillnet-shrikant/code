<%--
  - File Name: homeStoresModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows the user to select a home store
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	
	<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
	<dsp:getvalueof var="pageName" param="pageType"/>
	
	<layout:ajax>
		<jsp:attribute name="pageType">bopisModal</jsp:attribute>
		<jsp:body>

			<div class="home-store-modal">
				<c:choose>
					<c:when test="${empty homeStore}">
						<div class="modal-header">
							<h2>CHOOSE MY STORE LOCATION</h2>
						</div>
						<div class="modal-body">
							<div class="store-search-form">
								<dsp:form formid="store-search-form" id="store-search-form" method="post" name="store-search-form" data-validate>
									<dsp:include page="/sitewide/includes/homeStoreSearchForm.jsp" />
								</dsp:form>
							</div>
							<div class="store-results"></div>
						</div>
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="city" bean="Profile.myHomeStore.city"/>
						<dsp:getvalueof var="state" bean="Profile.myHomeStore.stateAddress"/>
						<dsp:getvalueof var="address1" bean="Profile.myHomeStore.address1"/>
						<dsp:getvalueof var="phoneNumber" bean="Profile.myHomeStore.phoneNumber"/>
						<dsp:getvalueof var="meters" bean="Profile.myHomeStore.distance" />
						<dsp:getvalueof var="lat" bean="Profile.myHomeStore.latitude" />
						<dsp:getvalueof var="lng" bean="Profile.myHomeStore.longitude" />
						<dsp:getvalueof var="name" bean="Profile.myHomeStore.name" />
						<dsp:getvalueof var="zip" bean="Profile.myHomeStore.postalCode" />
						<dsp:getvalueof var="redirectUrl" bean="Profile.myHomeStore.website" />
						<fmt:formatNumber var="miles" value="${(meters / 1609.344)}" maxFractionDigits="2" />
						<div class="modal-header">
							<h2>CHANGE MY STORE LOCATION</h2>
						</div>
						
						<div class="modal-body">
							<div class="card">
								<div class="store-location-info">
									<div class="card-content left address">
										<p class="orange title">
											<span class="icon icon-locator orange" aria-hidden="true"></span>
											MY STORE
										</p>
										<p class="title">${city}, ${state}</p>
										<p>${address1}</p>
										<p>${city},&nbsp;${state}</p>
										<p>${phoneNumber}</p>
										<p><strong>Distance:</strong> ${miles}</p>
									</div>
									<div class="card-content left">
										<div class="map-section">
											<div id="storemap"></div>
										</div>
									</div>
									<div class="label"><span>SELECT ANOTHER STORE</span></div>
									<div class="store-search-form">
										<dsp:form formid="store-search-form" id="store-search-form" method="post" name="store-search-form" data-validate>
											<dsp:include page="/sitewide/includes/homeStoreSearchForm.jsp" />
										</dsp:form>
									</div>
									<div class="store-results">
										<c:if test="${pageName ne 'account' and pageName ne 'mobile'}">
											<p class="results-zip-code">Showing results for: <strong><dsp:valueof bean="StoreLocatorFormHandler.postalCode" /></strong> within <strong>100 miles</strong></p>
											<div class="home-results-list scrollbar" id="style-1">
												<ul class="bopis-results-list">
													<dsp:droplet name="ForEach">
														<dsp:param name="array" bean="StoreLocatorFormHandler.results"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="meters" param="element.store.distance" />
															<fmt:formatNumber var="miles" value="${(meters / 1609.344)}" maxFractionDigits="2" />
															<dsp:getvalueof var="eligible" param="element.bopisElgible" vartype="boolean" />
															<li>
																<div class="address">
																	<h3><dsp:valueof param="element.store.city" />, <dsp:valueof param="element.store.stateAddress" /></h3>
																	<p><dsp:valueof param="element.store.address1" /></p>
																	<p><dsp:valueof param="element.store.address2" /></p>
																	<p><dsp:valueof param="element.store.city" />, MN&nbsp;<dsp:valueof param="element.store.postalCode" /></p>
																	<p><dsp:valueof param="element.store.phoneNumber" /></p>
																	<p><strong>Distance:</strong> ${miles}</p>
																</div>
																<div class="actions">
																	<button class="button primary choose-store" data-store-id="<dsp:valueof param="element.store.locationId" />">MAKE THIS MY STORE</button>
																</div>
															</li>
														</dsp:oparam>
													</dsp:droplet>
												</ul>
											</div>
										</c:if>
									</div>
									<div class="hide">										
										<div class="storeAddress">${address1}</div>
										<div class="storeCity">${city}</div>
										<div class="storeState">${state}</div>
										<div class="storeZip">${zip}</div>
										<div class="storePhone">${phoneNumber}</div>
										<div class="storeName">${name}</div>
										<div class="storeUrl">${redirectUrl}</div>
										<div class="storeLat">${lat}</div>
										<div class="storeLng">${lng}</div>
									</div>
								</div>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</jsp:body>
	</layout:ajax>

</dsp:page>
