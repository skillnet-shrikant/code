<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:choose>
		<c:when test="${contentItem.noOverLayEnabled}">
			<dsp:getvalueof var="backgroundClass" value="${contentItem.overlay} ${contentItem.overlayVerticalAlignment} ${contentItem.overlayHorizontalAlignment} ${contentItem.overlayTextAlignment} no-overlay"/>
			<dsp:getvalueof var="hasOverlay" value="false"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="backgroundClass" value="${contentItem.overlay} ${contentItem.overlayVerticalAlignment} ${contentItem.overlayHorizontalAlignment} ${contentItem.overlayTextAlignment}"/>
			<dsp:getvalueof var="hasOverlay" value="true"/>
		</c:otherwise>
	</c:choose>
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
	
	<c:choose>
		<c:when test="${not empty contentItem.imageH2Text1}">
			<dsp:getvalueof var="promoName" value="${contentItem.imageH2Text1}"/>
		</c:when>
		<c:when test="${not empty contentItem.imageParaText1}">
			<dsp:getvalueof var="promoName" value="${fn:escapeXml(contentItem.imageParaText1)}"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="promoName" value="${contentItem.imageButtonLabel1}"/>
		</c:otherwise>
	</c:choose>
		
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.active}"/>
		<dsp:oparam name="true">
			<li class="promo-tile ${mobileHideClass} ${desktopHideClass}" data-promotionname='${promoName}' data-promotion='${promoName}' data-promotionposition="slot" data-promotionlink="${contentItem.imageButtonHref1}">
				<c:if test="${not empty contentItem.imageButtonHref1}">
					<a href="${contentItem.imageButtonHref1}" id="${contentItem.idTag}" data-overlay="${hasOverlay}">
						<c:choose>
							<c:when test="${not empty contentItem.imageURL1}">
								<img src="${contentItem.imageURL1}" alt="${contentItem.imageAltText1}" />
							</c:when>
							<c:when test="${not empty contentItem.videoId}">
								<div class="vimeo-embed">
								<iframe src="https://player.vimeo.com/video/${contentItem.videoId}?api=1&autoplay=0&playsinline=0&color=fd6316&byline=0&portrait=0&title=0"
							        webkitallowfullscreen
							        mozallowfullscreen
							        allowfullscreen>
							    </iframe>
							    </div>
							</c:when>
						</c:choose> 

						<c:if test="${not empty contentItem.imageH2Text1 || not empty contentItem.imageParaText1 || not empty contentItem.imageButtonLabel1}">
							<div class="promo-tile-content ${backgroundClass}">
								<c:if test="${not empty contentItem.imageH2Text1}">
									<h2>${contentItem.imageH2Text1}</h2>
								</c:if>
								<c:if test="${not empty contentItem.imageParaText1}">
									<p>${contentItem.imageParaText1}</p>
								</c:if>
								<c:if test="${contentItem.buttonCustomWidthEnabled}">
									<dsp:getvalueof var="customStyle" value='style="width: ${contentItem.buttonCustomWidth}%" ' />
								</c:if>
								<c:if test="${not empty contentItem.imageButtonLabel1}">
									<span class="button primary" ${customStyle}>${contentItem.imageButtonLabel1}</span>
								</c:if>
							</div>
						</c:if>
					</a>
				</c:if>

				<c:if test="${empty contentItem.imageButtonHref1}">
					<c:choose>
						<c:when test="${not empty contentItem.imageURL1}">
							<img src="${contentItem.imageURL1}" alt="${contentItem.imageAltText1}" />
						</c:when>
						<c:when test="${not empty contentItem.videoId}">
							<div class="vimeo-embed">
								<iframe src="https://player.vimeo.com/video/${contentItem.videoId}?api=1&autoplay=0&playsinline=0&color=fd6316&byline=0&portrait=0&title=0"
									webkitallowfullscreen
									mozallowfullscreen
									allowfullscreen>
								</iframe>
						    </div>
						</c:when>
					</c:choose>
					<c:if test="${not empty contentItem.imageH2Text1 || not empty contentItem.imageParaText1 || not empty contentItem.imageButtonLabel1}">
						<div data-overlay="${hasOverlay}" class="promo-tile-content ${backgroundClass}">
							<c:if test="${not empty contentItem.imageH2Text1}">
								<h2>${contentItem.imageH2Text1}</h2>
							</c:if>
							<c:if test="${not empty contentItem.imageParaText1}">
								<p>${contentItem.imageParaText1}</p>
							</c:if>
							<c:if test="${contentItem.buttonCustomWidthEnabled}">
								<dsp:getvalueof var="customStyle" value='style="width: ${contentItem.buttonCustomWidth}%" ' />
							</c:if>
							<c:if test="${not empty contentItem.imageButtonLabel1}">
								<span class="button primary" ${customStyle}>${contentItem.imageButtonLabel1}</span>
							</c:if>
						</div>
					</c:if>
				</c:if>
		
			</li>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
