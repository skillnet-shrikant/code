
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/com/mff/droplet/BopisStoreAvailabilityDroplet"/>
	<json:object>

		<json:property name="success">true</json:property>
		<json:property name="locationId"><dsp:valueof bean="Profile.myHomeStore.locationId" /></json:property>
		<json:property name="address1"><dsp:valueof bean="Profile.myHomeStore.address1" /></json:property>
		<json:property name="city"><dsp:valueof bean="Profile.myHomeStore.city" /></json:property>
		<json:property name="stateAddress"><dsp:valueof bean="Profile.myHomeStore.stateAddress" /></json:property>
		<json:property name="postalCode"><dsp:valueof bean="Profile.myHomeStore.postalCode" /></json:property>
		<json:property name="phoneNumber"><dsp:valueof bean="Profile.myHomeStore.phoneNumber" /></json:property>
		<json:property name="website"><dsp:valueof bean="Profile.myHomeStore.website" /></json:property>

		<dsp:getvalueof var="storeId" param="storeId"/>
		<dsp:getvalueof var="skuId" param="skuId"/>
		<dsp:getvalueof var="productId" param="productId"/>
		<dsp:getvalueof var="bopisOnly" value="false" vartype="Boolean" />
		<dsp:getvalueof var="cartIsEmpty" value="true" vartype="Boolean" />
		<dsp:getvalueof var="isAvailable" value="false" vartype="Boolean" />

		<dsp:getvalueof var="currentOrder" bean="ShoppingCart.current" />
		<c:if test="${currentOrder != null && currentOrder.totalCommerceItemCount > 0}">
			<dsp:getvalueof var="cartIsEmpty" value="false" vartype="Boolean" />
		</c:if>

		<c:if test="${not empty storeId && (not empty skuId || not empty productId)}">
			<dsp:droplet name="BopisStoreAvailabilityDroplet">
				<dsp:param name="productId" param="productId"/>
				<dsp:param name="skuId" param="skuId"/>
				<dsp:param name="quantity" value="1"/>
				<dsp:param name="storeId" param="storeId"/>
				<dsp:oparam name="true">
					<dsp:getvalueof var="isAvailable" value="true" vartype="Boolean" />
					<json:property name="eligible">true</json:property>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="/com/mff/browse/droplet/IsInactiveProduct">
				<dsp:param name="productId" param="productId"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="isInactiveProd" param="isInActiveProduct"/>
					<dsp:getvalueof var="isActiveTeaser" param="isActiveTeaser"/>
				</dsp:oparam>
			</dsp:droplet>			
			
			<c:choose>	
				<c:when test="${isActiveTeaser}">
					<json:property name="isActiveTeaser">hide</json:property>
				</c:when>
				<c:otherwise>
					<json:property name="isActiveTeaser"></json:property>	
				</c:otherwise>
			</c:choose>
			
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

		<c:if test="${isAvailable}">
			<json:property name="available">${isAvailable}</json:property>
		</c:if>
		<c:if test="${bopisOnly}">
			<json:property name="bopisOnly">${bopisOnly}</json:property>
		</c:if>
		<c:if test="${not bopisOnly and not cartIsEmpty}">
			<json:property name="displayShipMyOrderLink">true</json:property>
		</c:if>

	</json:object>

</dsp:page>
