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
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="currentOrder" bean="ShoppingCart.current" />
	<dsp:getvalueof var="cartIsEmpty" value="true" vartype="Boolean" />
	<dsp:getvalueof var="isAvailable" value="false" vartype="Boolean" />
	<dsp:getvalueof var="displayShipMyOrderLink" value="false" vartype="Boolean" />
	<dsp:getvalueof var="distanceInMeters" bean="/mff/MFFEnvironment.storePickUpRadius" />
	<fmt:formatNumber var="distanceInMiles" value="${(distanceInMeters / 1609.344)}" maxFractionDigits="2" />

	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="fromProduct"><dsp:valueof bean="StoreLocatorFormHandler.fromProduct" /></json:property>
		<json:property name="productId"><dsp:valueof param="productId" /></json:property>
		<json:property name="skuId"><dsp:valueof bean="StoreLocatorFormHandler.catalogRefId" /></json:property>
		<json:property name="quantity"><dsp:valueof bean="StoreLocatorFormHandler.quantity" /></json:property>
		<json:property name="searchRadius">${distanceInMiles}</json:property>
		<json:property name="zip"><dsp:valueof bean="StoreLocatorFormHandler.postalCode" /></json:property>
		<json:property name="editMode"><dsp:valueof bean="StoreLocatorFormHandler.editMode" /></json:property>
		<json:property name="removalCommerceIds"><dsp:valueof bean="StoreLocatorFormHandler.removalCommerceIds" /></json:property>

		<%-- stores --%>
		<json:array name="stores">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="StoreLocatorFormHandler.results"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="meters" param="element.store.distance" />
					<dsp:getvalueof var="eligible" param="element.bopisElgible" vartype="boolean" />
					<fmt:formatNumber var="miles" value="${(meters / 1609.344)}" maxFractionDigits="2" />
					<json:object>
						<c:if test="${eligible}">
							<dsp:getvalueof var="isAvailable" value="true" vartype="Boolean" />
							<json:property name="eligible">${eligible}</json:property>
						</c:if>
						<json:property name="distance">${miles}</json:property>
						<json:property name="locationId"><dsp:valueof param="element.store.locationId" /></json:property>
						<json:property name="address1"><dsp:valueof param="element.store.address1" /></json:property>
						<json:property name="address2"><dsp:valueof param="element.store.address2" /></json:property>
						<json:property name="city"><dsp:valueof param="element.store.city" /></json:property>
						<json:property name="stateAddress"><dsp:valueof param="element.store.stateAddress" /></json:property>
						<json:property name="postalCode"><dsp:valueof param="element.store.postalCode" /></json:property>
						<json:property name="phoneNumber"><dsp:valueof param="element.store.phoneNumber" /></json:property>
					</json:object>
				</dsp:oparam>
			</dsp:droplet>
		</json:array>

		<c:if test="${currentOrder != null && currentOrder.totalCommerceItemCount > 0}">
			<dsp:getvalueof var="cartIsEmpty" value="false" vartype="Boolean" />
		</c:if>

		<c:if test="${productId}">
			<dsp:droplet name="/atg/commerce/catalog/ProductLookup">
				<dsp:param name="id" value="${productId}"/>
				<dsp:param name="elementName" value="product"/>
				<dsp:oparam name="output">
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="product.fulfillmentMethod" />
						<%-- 7: product is bopis only --%>
						<dsp:oparam name="7">
							<dsp:getvalueof var="bopisOnly" value="true" vartype="Boolean" />
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</c:if>

		<c:if test="${not bopisOnly and not cartIsEmpty}">
			<dsp:getvalueof var="displayShipMyOrderLink" value="true" vartype="Boolean" />
		</c:if>

		<c:if test="${isAvailable}">
			<json:property name="available">${isAvailable}</json:property>
		</c:if>
		<c:if test="${bopisOnly}">
			<json:property name="bopisOnly">${bopisOnly}</json:property>
		</c:if>
		<c:if test="${displayShipMyOrderLink}">
			<json:property name="displayShipMyOrderLink">${displayShipMyOrderLink}</json:property>
		</c:if>

	</json:object>

</dsp:page>
