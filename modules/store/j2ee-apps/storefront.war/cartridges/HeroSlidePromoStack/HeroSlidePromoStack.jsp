<!-- promo slider with stacked images -->
<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${contentItem.customPaddingRowEnabled}">
		<dsp:getvalueof var="customStyle" value='style="padding: ${contentItem.contentRowPadding}px 0px" ' />
	</c:if>
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
				<div class="section-row ${mobileHideClass} ${desktopHideClass}" ${customStyle}>
					<div class="section-content">
						<div class="hero-slider-promo-stack">
							<c:if test="${not empty contentItem.HeroSlides}">
								<div class="hero-slider hero-slider-with-promo">
									<c:forEach var="element" items="${contentItem.HeroSlides}">
										<dsp:renderContentItem contentItem="${element}" />
									</c:forEach>
								</div>
							</c:if>
							<c:if test="${not empty contentItem.PromoSlots}">
								<c:choose>
									<c:when test="${fn:length(contentItem.PromoSlots) == 1}">
										<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-one"/>
									</c:when>
									<c:when test="${fn:length(contentItem.PromoSlots) == 2}">
										<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-two"/>
									</c:when>
									<c:when test="${fn:length(contentItem.PromoSlots) == 3}">
										<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-three"/>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="gridClass" value="promo-grid"/>
									</c:otherwise>
								</c:choose>
								<ul class="${gridClass}">
									<c:forEach var="promoSlot" items="${contentItem.PromoSlots}">
										<dsp:renderContentItem contentItem="${promoSlot}" />
									</c:forEach>
								</ul>
							</c:if>
						</div>
					</div>
				</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
