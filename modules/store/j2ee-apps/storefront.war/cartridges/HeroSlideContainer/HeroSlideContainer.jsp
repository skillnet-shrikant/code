<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<!-- hero slider -->
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
			<c:if test="${not empty contentItem.HeroSlides}">
				<section id="${contentItem.anchorTag}">
					<div class="section-row ${mobileHideClass} ${desktopHideClass}" ${customStyle}>
						<div class="section-content">
							<div class="hero-slider">
								<c:forEach var="element" items="${contentItem.HeroSlides}">
									<dsp:renderContentItem contentItem="${element}" />
								</c:forEach>
							</div>
						</div>
					</div>
				</section>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>