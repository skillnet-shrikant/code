<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.mobileHide}"/>
		<dsp:oparam name="true">
			<dsp:getvalueof var="mobileHideClass" value="mobile-hide "/>
		</dsp:oparam>
		<dsp:oparam name="false">
			<dsp:getvalueof var="mobileHideClass" value=""/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.desktopHide}"/>
		<dsp:oparam name="true">
			<dsp:getvalueof var="desktopHideClass" value="desktop-hide "/>
		</dsp:oparam>
		<dsp:oparam name="false">
			<dsp:getvalueof var="desktopHideClass" value=""/>
		</dsp:oparam>
	</dsp:droplet>
	
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.active}"/>
		<dsp:oparam name="true">
			<dsp:getvalueof var="nearByLocation" param="storeItem.pageNearbyLocationsHeader"/>
			<dsp:getvalueof var="storeId" param="storeItem.locationId"/>
							
			<li class="${mobileHideClass} ${desktopHideClass}">
				<div class="text-left">
					<h2><dsp:valueof param="storeItem.pageNearbyLocationsHeader"/></h2>
					<ul class="store-location-list">
		
					</ul>
					<div class="actions">
						<dsp:a iclass="button dark" href="/sitewide/storeLocator.jsp" data-store-id="${storeId}">find more nearby stores
							<dsp:param name="zipcode" param="storeItem.postalCode"/>
						</dsp:a>
					</div>
				</div>
			</li>
			<hr>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>