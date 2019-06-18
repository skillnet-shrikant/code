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
	<c:if test="${contentItem.customPaddingTitleEnabled}">
		<dsp:getvalueof var="customTitleStyle" value='style="padding: ${contentItem.contentTitlePadding}px 0px" ' />
	</c:if>
	<c:if test="${contentItem.customPaddingRowEnabled}">
		<dsp:getvalueof var="customContentStyle" value='style="padding: ${contentItem.contentRowPadding}px 0px" ' />
	</c:if>
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
								<c:when test="${fn:length(contentItem.PromoSlots) == 1}">
									<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-one"/>
								</c:when>
								<c:when test="${fn:length(contentItem.PromoSlots) == 2}">
									<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-two"/>
								</c:when>
								<c:when test="${fn:length(contentItem.PromoSlots) == 3}">
									<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-three"/>
								</c:when>
								<c:when test="${fn:length(contentItem.PromoSlots) == 4}">
									<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-four"/>
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
							
						</div>
					</div>
				
				</c:if>
				
					
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>