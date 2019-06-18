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
			<section>
				<c:if test="${not empty contentItem.PromoBars}">
					<div id="global-promo-section" class="global-promo-section ${mobileHideClass} ${desktopHideClass}">
						<div class="global-promo-section-container">
							<c:choose>
								<c:when test="${fn:length(contentItem.PromoBars) == 1}">
									<dsp:getvalueof var="barClass" value="promo-bar promo-bar-one"/>
								</c:when>
								<c:when test="${fn:length(contentItem.PromoBars) == 2}">
									<dsp:getvalueof var="barClass" value="promo-bar promo-bar-two"/>
								</c:when>
								<c:when test="${fn:length(contentItem.PromoBars) == 3}">
									<dsp:getvalueof var="barClass" value="promo-bar promo-bar-three"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="barClass" value="promo-bar"/>
								</c:otherwise>
							</c:choose>
							<ul class="${barClass}">
								<c:forEach var="promoBar" items="${contentItem.PromoBars}">
									<li><dsp:renderContentItem contentItem="${promoBar}" /></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</c:if>
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>