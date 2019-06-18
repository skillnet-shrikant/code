<%--
  - File Name: bopisSearchSuccess.jsp
  - Author(s): jjensen
  - Copyright Notice:
  - Description: Creates a json success message after successfully searching BOPIS inventory
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="isAvailable" value="false" vartype="Boolean" />
	<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
	<dsp:getvalueof var="homeStoreId" bean="Profile.myHomeStore.locationId"/>

	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="zip"><dsp:valueof bean="StoreLocatorFormHandler.postalCode" /></json:property>
		<%-- locations --%>
		<json:array name="locations">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="StoreLocatorFormHandler.results"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="meters" param="element.store.distance" />
					<fmt:formatNumber var="miles" value="${(meters / 1609.344)}" maxFractionDigits="2" />
					<dsp:getvalueof var="locationId" param="element.store.locationId" />
					<json:object>
						<json:property name="storeIndex"><dsp:valueof param="count" /></json:property>
						<json:property name="distance">${miles}</json:property>
						<json:property name="locationId"><dsp:valueof param="element.store.locationId" /></json:property>
						<json:property name="address"><dsp:valueof param="element.store.address1" /></json:property>
						<json:property name="address2"><dsp:valueof param="element.store.address2" /></json:property>					
						<json:property name="name"><dsp:valueof param="element.store.name" /></json:property>
						<json:property name="city"><dsp:valueof param="element.store.city" /></json:property>
						<json:property name="state"><dsp:valueof param="element.store.stateAddress" /></json:property>
						<json:property name="zip"><dsp:valueof param="element.store.postalCode" /></json:property>
						<json:property name="phone"><dsp:valueof param="element.store.phoneNumber" /></json:property>
						<json:property name="storeHours"><dsp:valueof param="element.store.storeHours" /></json:property>
						<json:property name="gasMartHours"><dsp:valueof param="element.store.gasMartHours" /></json:property>
						<json:property name="serviceCenterHours"><dsp:valueof param="element.store.serviceCenterHours" /></json:property>
						<json:property name="lat"><dsp:valueof param="element.store.latitude" /></json:property>
						<json:property name="lng"><dsp:valueof param="element.store.longitude" /></json:property>
						<json:property name="articleId"><dsp:valueof param="element.store.storeLandingPage.id" /></json:property>
						<json:property name="redirectUrl"><dsp:valueof param="element.store.website" />?zipcode=<dsp:valueof bean="StoreLocatorFormHandler.postalCode" /></json:property>
						<c:choose>
							<c:when test="${homeStoreId eq locationId}">
								<json:property name="isHomeStore">true</json:property>
							</c:when>
							<c:otherwise>
								<json:property name="isHomeStore"></json:property>
							</c:otherwise>
						</c:choose>
					</json:object>
				</dsp:oparam>
			</dsp:droplet>
		</json:array>
	</json:object>

</dsp:page>
