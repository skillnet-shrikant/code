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
			<section id="${contentItem.anchorTag}">
			<div class="section-row ${mobileHideClass} ${desktopHideClass}">
				<div class="text-wrapped-img">
					<c:if test="${not empty contentItem.imageURL}" >
						<c:choose>
							<c:when test="${contentItem.imageAlignment == 'right'}">
								<img class="right wrap-right" src="${contentItem.imageURL}" alt="${contentItem.imageAltText}">
							</c:when>
							<c:when test="${contentItem.imageAlignment == 'left'}">
								<img class="left wrap-left" src="${contentItem.imageURL}" alt="${contentItem.imageAltText}">
							</c:when>
							<c:otherwise>
								<img src="${contentItem.imageURL}" alt="${contentItem.imageAltText}">
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:if test="${not empty contentItem.sectionTitle}">
						<div class="small-title">
							<h3>${contentItem.sectionTitle}</h3>
						</div>
					</c:if>
					<c:if test="${not empty contentItem.paraText}" >
						<p>${contentItem.paraText}</p>
					</c:if>
					<c:if test="${not empty contentItem.sectionHref}" >
						<p><a href="${contentItem.sectionHref}">Read more...</a></p>
					</c:if>
				</div>
			</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
