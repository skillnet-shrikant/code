<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="storeId" param="storeItem.locationId"/>
	<dsp:getvalueof var="storeZip" param="storeItem.postalCode"/>
	<c:choose>
		<c:when test="${fn:length(contentItem.StorePageTiles) == 1}">
			<dsp:getvalueof var="gridClass" value="store-grid-one store-grid"/>
		</c:when>
		<c:when test="${fn:length(contentItem.StorePageTiles) == 2}">
			<dsp:getvalueof var="gridClass" value="store-grid-two store-grid"/>
		</c:when>
		<c:when test="${fn:length(contentItem.StorePageTiles) >= 3}">
			<dsp:getvalueof var="gridClass" value="store-grid-three store-grid"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="gridClass" value="store-grid"/>
		</c:otherwise>
	</c:choose>
	
	<section>
		<div class="section-row">
			<div class="section-content">
				<ul class="${gridClass}">
					<c:forEach var="storePageTile" items="${contentItem.StorePageTiles}">
						<dsp:renderContentItem contentItem="${storePageTile}" />
					</c:forEach>
					
				</ul>
			</div>
		</div>
	</section>
	
	<%-- product data json --%>
	<script>
		var storeId=${storeId};
		var KP_STORES = KP_STORES || {};
		KP_STORES = <dsp:include src="/sitewide/json/storeLocationJson.jsp?zipcode=${storeZip}&noOfStores=4" />;
	</script>
	
</dsp:page>