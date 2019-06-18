<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${contentItem.customPaddingTitleEnabled}">
		<dsp:getvalueof var="customTitleStyle" value='style="padding: ${contentItem.contentTitlePadding}px 0px" ' />
	</c:if>
	<c:if test="${contentItem.customPaddingRowEnabled}">
		<dsp:getvalueof var="customContentStyle" value='style="padding: ${contentItem.contentRowPadding}px 0px" ' />
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
				<c:if test="${not empty contentItem.sectionTitle}">
					<div class="section-title ${mobileHideClass} ${desktopHideClass}" ${customTitleStyle}>
						<h2>${contentItem.sectionTitle}</h2>
					</div>
				</c:if>
				<c:if test="${not empty contentItem.PromoSlots}">
					<div class="section-row ${mobileHideClass} ${desktopHideClass}" ${customContentStyle}>
						<div class="section-content">
							<c:choose>
								<c:when test="${contentItem.promoSlotsPerSlide eq 6}">
									<dsp:getvalueof var="sliderClass" value="promo-slider promo-slider-six"/>
									<dsp:getvalueof var="gridType" value="grid4"/>
								</c:when>
								<c:when test="${contentItem.promoSlotsPerSlide eq 5}">
									<dsp:getvalueof var="sliderClass" value="promo-slider promo-slider-five"/>
									<dsp:getvalueof var="gridType" value="grid3"/>
								</c:when>
								<c:when test="${contentItem.promoSlotsPerSlide eq 4}">
									<dsp:getvalueof var="sliderClass" value="promo-slider promo-slider-four"/>
									<dsp:getvalueof var="gridType" value="grid4"/>
								</c:when>
								<c:when test="${contentItem.promoSlotsPerSlide eq 3}">
									<dsp:getvalueof var="sliderClass" value="promo-slider promo-slider-three"/>
									<dsp:getvalueof var="gridType" value="grid3"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="sliderClass" value="promo-slider"/>
								</c:otherwise>
							</c:choose>
							<div class="${sliderClass}">
								<c:forEach var="promoSlot" items="${contentItem.PromoSlots}">
									<dsp:renderContentItem contentItem="${promoSlot}" />
								</c:forEach>
							</div>
							
						</div>
					</div>
				
				</c:if>
				
					
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>