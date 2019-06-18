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
			<c:if test="${not empty contentItem.imageURL}">
				<section id="${contentItem.anchorTag}">
				<div class="managed-content-slot ${mobileHideClass} ${desktopHideClass}">
					<div class="managed-content-image">
						<c:choose>
							<c:when test="${not empty contentItem.link && not empty contentItem.link.path }">
								<a href="${contentItem.link.path}"><img src="${contentItem.imageURL}" /></a>
							</c:when>
							<c:otherwise>
								<img src="${contentItem.imageURL}" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				</section>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>