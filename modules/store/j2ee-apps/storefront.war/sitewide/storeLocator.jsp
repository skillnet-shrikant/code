<%--
	- File Name: storeLocator.jsp
	- Author(s): KnowledgePath Solutions
	- Copyright Notice:
	- Description: This is the store locator
	--%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/mff/locator/droplet/StoreLocationsDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/mff/content/droplet/MFFContentDisplayDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/mff/MFFEnvironment"/>
	<dsp:getvalueof bean="MFFEnvironment.storePickUpRadius" var="distance"/>

	<%-- Page Variables --%>
	<dsp:getvalueof param="state" var="state" />

	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="storelocator" />
		<dsp:param name="defaultPageTitle" value="Store locator" />
		<dsp:param name="defaultMetaDescription" value="Fleet Farm Store Locator" />
		<dsp:param name="defaultCanonicalURL" value="" />
		<dsp:param name="defaultRobotsIndex" value="index" />
		<dsp:param name="defaultRobotsFollow" value="follow" />
	</dsp:include>	

	<layout:default>
		<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
		<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
		<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">home</jsp:attribute>
		<jsp:attribute name="pageType">storeLocator</jsp:attribute>
		<jsp:attribute name="bodyClass">store-locator</jsp:attribute>
		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><span class="crumb active">Store Locator</span></li>
				</ul>
			</section>
			<dsp:getvalueof var="zipcode" param="zipcode"/>
			<%-- title --%>
			<%-- <div class="section-title">
				<h1>Store Locator</h1>
			</div> --%>
		
			<div class="two-column-container">
				<div class="mobile-store-view">
					<a href="#map" alt="view map" class="button primary expand">map view</a>
				</div> <!-- /mobile-refinements -->

				<div class="store-locator-left card">
					<dsp:form id="store-details-list-form" formid="store-details-list-form" action="" method="post">
						<dsp:input type="hidden" bean="StoreLocatorFormHandler.distance" id="bopis-distance-modal" name="bopis-distance-modal" value="${distance}"/>
						<dsp:input type="text" bean="StoreLocatorFormHandler.postalCode" class="store-locator-zip" id="store-locator-zip" name="bopis-zip-modal" data-fieldname="Zip Code" placeholder="Zip Code, City, State, or Store#" value="${zipcode}"/>
						<dsp:input type="hidden" bean="StoreLocatorFormHandler.errorURL" value="${contextPath}/browse/json/bopisSearchError.jsp" />
						<dsp:input type="hidden" bean="StoreLocatorFormHandler.successURL" value="${contextPath}/sitewide/json/storeLocationSuccess.jsp" />
						<dsp:input type="hidden" bean="StoreLocatorFormHandler.findStores" id="bopis-search-submit-modal" name="bopis-search-submit-modal" class="button primary" value="Find Stores" ></dsp:input>
					</dsp:form>
					<button id="search" class="store-search-button button primary" aria-label="search">
						<span class="icon icon-search" aria-hidden="true"></span>
					</button>
					<div class="store-location-results"></div>
				</div>

				<div class="store-locator-right">
					<div class="map-container">
						<div id="map"></div>
					</div>
				</div>

				<%-- <div class="store-locator-right">
					<dsp:droplet name="MFFContentDisplayDroplet">
						<dsp:param name="rqlQuery" value="contentKey =? 0" />
						<dsp:param name="contentKey" value="3333" />
						<dsp:param name="itemDescriptor" value="mffStaticContent"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="allContents" param="allContents"/>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${allContents}"/>
								<dsp:param name="elementName" value="content"/>
								<dsp:oparam name="output">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="content.contentSections"/>
										<dsp:param name="elementName" value="content"/>
										<dsp:oparam name="output">
											<dsp:valueof param="content.body" valueishtml="true"/>
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</div> --%>

			</div>

			<%-- product data json --%>
			<script>
				var KP_STORES = KP_STORES || {};
				KP_STORES = <dsp:include src="/sitewide/json/storeLocationJson.jsp" />;
			</script>

			<%-- google maps 
			<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDV3nXdSNtnvq3h6lKDe5d9NReG9NAwFUQ"></script> --%>
			
		</jsp:body>
	</layout:default>

</dsp:page>
