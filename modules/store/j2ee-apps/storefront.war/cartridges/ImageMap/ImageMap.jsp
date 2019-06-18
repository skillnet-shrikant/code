<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
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
			<section id="${contentItem.anchorTag}">
				<c:if test="${not empty contentItem.sectionTitle}">
					<div class="section-title ${mobileHideClass} ${desktopHideClass}">
						<h2>${contentItem.sectionTitle}</h2>
					</div>
				</c:if>
				
				<div class="section-row ${mobileHideClass} ${desktopHideClass}">
					<div class="section-content">
						<img src="${contentItem.imageUrl}" alt="${contentItem.altText}" usemap="#${contentItem.mapName}"/>
						<map name="${contentItem.mapName}">
							<c:forEach var="map" items="${contentItem.Maps}">
								<area shape="${map.shape}" coords="${map.coords}" href="${map.href}" id="${map.linkId}" alt="${map.altText}">	
							</c:forEach>
						</map>
					</div>
				</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>