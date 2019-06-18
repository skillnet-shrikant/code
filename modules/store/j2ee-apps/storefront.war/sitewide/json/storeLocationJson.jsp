<%--
  - File Name: storeLocationJson.jsp
  - Author(s):
  - Copyright Notice:
  - Description: Creates a json object with details for all store locations
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/mff/locator/droplet/StoreLocationsDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Page Variables --%>
	<dsp:getvalueof param="state" var="state" />
	<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
	<dsp:getvalueof var="homeStoreId" bean="Profile.myHomeStore.locationId"/>
	<dsp:getvalueof param="noOfStores" var="noOfStores" />
	
	<json:object>
		<json:array name="locations">

			<dsp:droplet name="StoreLocationsDroplet">
				<dsp:param name="state" value="${state}"/>
				<dsp:param name="homeStore" bean="Profile.myHomeStore"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="storeLocations" param="storeLocations" />
					<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${storeLocations}" />
						<dsp:param name="elementName" value="store" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="count"  param="count" />
							<c:if test="${empty noOfStores || count le noOfStores }">
								<dsp:getvalueof var="meters" param="store.distance" />
								<dsp:getvalueof var="isComingSoon" param="store.pageComingSoonEnabled" />
								<fmt:formatNumber var="miles" value="${(meters / 1609.344)}" maxFractionDigits="2" />
								<dsp:getvalueof var="locationId" param="store.locationId" />
								<json:object>
									<json:property name="storeIndex"><dsp:valueof param="count" /></json:property>
									<json:property name="distance">${miles}</json:property>
									<json:property name="name"><dsp:valueof param="store.name" /></json:property>
									<json:property name="address"><dsp:valueof param="store.address1" /></json:property>
									<json:property name="city"><dsp:valueof param="store.city" /></json:property>
									<json:property name="state"><dsp:valueof param="store.stateAddress" /></json:property>
									<dsp:getvalueof var="storeStateCode" param="store.stateAddress" />
									<dsp:droplet name="/atg/commerce/util/StateListDroplet">
										<dsp:param name="userLocale" bean="/atg/dynamo/servlet/RequestLocale.locale" />
										<dsp:param name="countryCode" value="US" />
										<dsp:oparam name="output">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array" param="states" />
												<dsp:param name="elementName" value="state" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="stateCode" param="state.code" />
													<c:if test="${stateCode eq storeStateCode }">
														<json:property name="stateFullName"><dsp:valueof param="state.displayName" /></json:property>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>
									<json:property name="zip"><dsp:valueof param="store.postalCode" /></json:property>
									<json:property name="phone"><dsp:valueof param="store.phoneNumber" /></json:property>
									<json:property name="gasMartHours"><dsp:valueof param="store.gasMartHours" /></json:property>
									<json:property name="serviceCenterHours"><dsp:valueof param="store.serviceCenterHours" /></json:property>
									<json:property name="lat"><dsp:valueof param="store.latitude" /></json:property>
									<json:property name="lng"><dsp:valueof param="store.longitude" /></json:property>
									<json:property name="locationId"><dsp:valueof param="store.locationId" /></json:property>
									<json:property name="articleId"><dsp:valueof param="store.storeLandingPage.id" /></json:property>
									<json:property name="redirectUrl"><dsp:valueof param="store.website" /></json:property>
									<dsp:getvalueof param="store.todayStoreHours.isClosed" var="isClosed"/>
									<c:choose>
										<c:when test="${homeStoreId eq locationId}">
											<json:property name="isHomeStore">true</json:property>
										</c:when>
										<c:otherwise>
											<json:property name="isHomeStore"></json:property>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${isComingSoon}">
											<json:property name="storeClosingTime">Location coming soon!</json:property>
											<json:property name="isComingSoon"></json:property>
										</c:when>
										<c:when test="${isClosed}">
											<json:property name="storeClosingTime">Closed</json:property>
											<json:property name="isComingSoon">false</json:property>
										</c:when>
										<c:otherwise>
											<json:property name="storeClosingTime">Open until <dsp:valueof param="store.todayStoreHours.closingTime" date="haa"/></json:property>
											<json:property name="isComingSoon">false</json:property>
										</c:otherwise>
									</c:choose>
								</json:object>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>

		</json:array>
	</json:object>

</dsp:page>
