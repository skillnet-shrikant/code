<%--
  - File Name: bopisStoresModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows the user to select a home store
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore.locationId"/>
	<dsp:getvalueof var="skuId" param="skuId"/>
	<dsp:getvalueof var="pageName" param="pageType"/>
	<dsp:getvalueof var="bopisStore" bean="ShoppingCart.current.bopisStore"/>
	<c:if test="${empty bopisStore}">
		<dsp:getvalueof var="bopisStore" param="bopisStore"/>
	</c:if>
	<layout:ajax>
		<jsp:attribute name="pageType">bopisModal</jsp:attribute>
		<jsp:body>
			<c:choose>
				<c:when test="${not empty homeStore}">
					<dsp:getvalueof var="bopisStore" vartype="java.lang.Object" bean="Profile.myHomeStore.locationId"/>
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
					<dsp:getvalueof var="postalCode" bean="Profile.myHomeStore.postalCode" scope="request"/>
				</c:when>
				<c:otherwise>
					<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
						<dsp:param name="id" value="${bopisStore}"/>
						<dsp:param name="elementName" value="store"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="postalCode" param="store.postalCode" scope="request"/>
							<dsp:getvalueof var="city" param="store.city"/>
							<dsp:getvalueof var="state" param="store.stateAddress"/>
							<dsp:getvalueof var="address1" param="store.address1"/>
							<dsp:getvalueof var="phoneNumber" param="store.phoneNumber"/>
							<dsp:getvalueof var="meters" param="store.distance" />
							<dsp:getvalueof var="lat" param="store.latitude" />
							<dsp:getvalueof var="lng" param="store.longitude" />
							<dsp:getvalueof var="name" param="store.name" />
							<dsp:getvalueof var="zip" param="store.postalCode" />
							<dsp:getvalueof var="redirectUrl" param="store.website" />
							<fmt:formatNumber var="miles" value="${(meters / 1609.344)}" maxFractionDigits="2" />
						</dsp:oparam>
					</dsp:droplet>
				</c:otherwise>
			</c:choose>
			<dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
				<dsp:param name="repository" value="/atg/commerce/inventory/InventoryRepository"/>
				<dsp:param name="itemDescriptor" value="storeInventory"/>
				<dsp:param name="queryRQL" value="storeId=:whatStore and catalogRefId=:whatSku"/>
				<dsp:param name="whatStore" value="${bopisStore}"/>
				<dsp:param name="whatSku" value="${skuId}"/>
				<dsp:param name="howMany" value="1"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="lastModified" param="element.lastUpdateDate"/>
					<c:if test="${empty lastModified}">
						<dsp:getvalueof var="lastModified" param="element.creationDate"/>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			<div class="bopis-store-modal">
				<div class="modal-header">
					<h2 class="bopis-title"><span class="icon icon-locator" aria-hidden="true"></span> My Store</h2>
				</div>
				<div class="modal-body">
					<div class="card">
						<div class="store-location-info">
							<div class="bopis-store-details">
								<div class="card-content">
									<p class="">${city}, ${state}</p>
									<a href="${redirectUrl}" alt="View Store Details" class="view-store">View Store Details</a>
								</div>
								<div class="card-content">
									<p>${address1}</p>
									<p>${city},&nbsp;${state}&nbsp;${zip}</p>
									<p>${phoneNumber}</p>
								</div>
								<div class="label"><span>Select Another Store</span></div>
							</div>
							<div class="bopis-store-search-form">
								<dsp:form formid="bopis-search-form" id="bopis-store-search-form" method="post" name="bopis-search-form" data-validate>
									<dsp:include page="/browse/includes/bopisSearchForm.jsp" />
								</dsp:form>
							</div>
							<div class="bopis-results"></div>
							<div class="item-inventory-details">
								<p class="reserve-msg hide">
									To reserve this item, place a Store Pickup order by selecting
									"Choose This Store" from an available store location listed above.
								</p>
								<p>
									Inventory is accurate as of
									<dsp:valueof value="${lastModified}" date="MMMM dd, yyyy hh:mm:ss aa zzz"/>.
									We do our best to update item availability as inventory changes: however,
									there may be slight difference in availability compared to what is listed online.
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</jsp:body>
	</layout:ajax>
</dsp:page>
