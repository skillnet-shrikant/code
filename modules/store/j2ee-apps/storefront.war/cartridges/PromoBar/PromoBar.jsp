<!-- promo bar: used in the home page header-->
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
	<c:if test="${not empty contentItem.idTag}">
		<c:set var="idAttrib">id="${contentItem.idTag}"</c:set>
	</c:if>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.active}"/>
		<dsp:oparam name="true">	
			<div id="global-promo" class="global-promo open ${mobileHideClass} ${desktopHideClass}" data-promotionname="${contentItem.promoText}" data-promotion="${contentItem.promoText}" data-promotionposition="slot" data-promotionlink="${contentItem.linkHref}">
				<div class="global-promo-container">
					<div class="global-promo-content">
						<div class="global-promo-text">
							${contentItem.promoText} <a href="${contentItem.linkHref}" ${idAttrib}>${contentItem.linkText}</a>
						</div>
					</div>
				</div>
			</div>
		</dsp:oparam>
	</dsp:droplet>	
</dsp:page>
