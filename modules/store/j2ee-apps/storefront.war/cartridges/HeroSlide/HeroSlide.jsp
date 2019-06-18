<!-- hero slide -->
<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="small" value="${contentItem.imageURL1}"/>
	<dsp:getvalueof var="medium" value="${contentItem.imageMedium}"/>
	<dsp:getvalueof var="large" value="${contentItem.imageLarge}"/>
	<dsp:getvalueof var="alt" value="${contentItem.imageAltText1}"/>
	<c:choose>
		<c:when test="${contentItem.noOverLayEnabled}">
			<dsp:getvalueof var="backgroundClass" value="${contentItem.overlay} ${contentItem.overlayVerticalAlignment} ${contentItem.overlayHorizontalAlignment} no-overlay"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="backgroundClass" value="${contentItem.overlay} ${contentItem.overlayVerticalAlignment} ${contentItem.overlayHorizontalAlignment}"/>
		</c:otherwise>
	</c:choose>

	<%-- figure out number of images that are set so we know if we need <picture> or <img> --%>
	<c:set var="numImages" value="0" scope="request" />
	<c:set var="smallestImage" value="" scope="request" />
	<c:if test="${not empty large}">
		<c:set var="numImages" value="${numImages+1}" scope="request" />
		<c:set var="smallestImage" value="${large}" scope="request" />
	</c:if>
	<c:if test="${not empty medium}">
		<c:set var="numImages" value="${numImages+1}" scope="request" />
		<c:set var="smallestImage" value="${medium}" scope="request" />
	</c:if>
	<c:if test="${not empty small}">
		<c:set var="numImages" value="${numImages+1}" scope="request" />
		<c:set var="smallestImage" value="${small}" scope="request" />
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
			<div class="hero-slide ${mobileHideClass} ${desktopHideClass}">
				<c:choose>
					<c:when test="${empty contentItem.imageButtonHref1}">
						<%-- (responsive) image --%>
							<c:choose>
								<c:when test="${numImages gt 1}">
									<picture>
										<!--[if IE 9]><video style="display: none;"><![endif]-->
										<c:if test="${not empty large}">
											<source srcset="${large}" media="(min-width: 980px)">
										</c:if>
										<c:if test="${not empty medium and medium ne smallestImage}">
											<source srcset="${medium}" media="(min-width: 768px)">
										</c:if>
										<!--[if IE 9]></video><![endif]-->
										<img srcset="${smallestImage}" src="${smallestImage}" alt="${alt}">
									</picture>
								</c:when>
								<c:otherwise>
									<img src="${smallestImage}" alt="${alt}" />
								</c:otherwise>
							</c:choose>
					
							<%-- text overlay --%>
							<c:if test="${not empty contentItem.imageH2Text1 || not empty contentItem.imageParaText1 || not empty contentItem.imageButtonLabel1}">
								<div class="hero-slider-content ${backgroundClass}">
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
					</c:when>
					<c:otherwise>
						<a href="${contentItem.imageButtonHref1}" id="${contentItem.idTag}">
							<%-- (responsive) image --%>
							<c:choose>
								<c:when test="${numImages gt 1}">
									<picture>
										<!--[if IE 9]><video style="display: none;"><![endif]-->
										<c:if test="${not empty large}">
											<source srcset="${large}" media="(min-width: 980px)">
										</c:if>
										<c:if test="${not empty medium and medium ne smallestImage}">
											<source srcset="${medium}" media="(min-width: 768px)">
										</c:if>
										<!--[if IE 9]></video><![endif]-->
										<img srcset="${smallestImage}" src="${smallestImage}" alt="${alt}">
									</picture>
								</c:when>
								<c:otherwise>
									<img src="${smallestImage}" alt="${alt}" />
								</c:otherwise>
							</c:choose>
					
							<%-- text overlay --%>
							<c:if test="${not empty contentItem.imageH2Text1 || not empty contentItem.imageParaText1 || not empty contentItem.imageButtonLabel1}">
								<div class="hero-slider-content ${backgroundClass}">
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
					</c:otherwise>
				</c:choose>
			</div>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>