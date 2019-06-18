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
			<div class="section-row ${mobileHideClass} ${desktopHideClass}">
				<c:if test="${not empty contentItem.sectionTitle}">
					<div class="small-title">
						<h3>${contentItem.sectionTitle}</h3>
					</div>
				</c:if>
				<c:if test="${not empty contentItem.imageURL}">
					<div class="single-image-row">
						<c:choose>
							<c:when test="${not empty contentItem.imageHyperlink}">
								<c:choose>
									<c:when test="${not empty contentItem.imageAlignment && contentItem.imageAlignment =='center'}">
										<a href="${contentItem.imageHyperlink}"><img src="${contentItem.imageURL}" alt="${contentItem.imageAltText}" /></a>
									</c:when>
									<c:otherwise>
										<a href="${contentItem.imageHyperlink}"><img class="${contentItem.imageAlignment}" src="${contentItem.imageURL}" alt="${contentItem.imageAltText}" /></a>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${not empty contentItem.imageAlignment && contentItem.imageAlignment =='center'}">
										<img src="${contentItem.imageURL}" alt="${contentItem.imageAltText}" />
									</c:when>
									<c:otherwise>
										<img class="${contentItem.imageAlignment}" src="${contentItem.imageURL}" alt="${contentItem.imageAltText}" />
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						
					</div>
				</c:if>
				
			</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>
	
	
</dsp:page>