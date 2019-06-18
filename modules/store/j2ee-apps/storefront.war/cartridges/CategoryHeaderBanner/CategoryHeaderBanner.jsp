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
			<%-- managed content slot --%>
			<section id="${contentItem.anchorTag}">
			<div class="managed-content-slot ${mobileHideClass} ${desktopHideClass}">
				<c:if test="${not empty contentItem.imageURL1}">
					<div class="managed-content-image">
						<c:choose>
							<c:when test="${not empty contentItem.imageLink1}">
								<a href="${contentItem.imageLink1}" id="${contentItem.idTag}">
									<img src="${contentItem.imageURL1}" alt="${contentItem.imageAltText1}" />
								</a>
							</c:when>
							<c:otherwise>
								<img src="${contentItem.imageURL1}" alt="${contentItem.imageAltText1}" />
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
				<c:if test="${not empty contentItem.imageH2Text1 || not empty contentItem.imageParaText1 || not empty contentItem.imageButtonLabel1}">
					<div class="managed-content">
						<c:if test="${not empty contentItem.imageH2Text1}">
							<h1>${contentItem.imageH2Text1}</h1>
						</c:if>
						<c:if test="${not empty contentItem.imageParaText1}">
							<p>
								${contentItem.imageParaText1}
							</p>
						</c:if>
					</div>
				</c:if>
			</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
