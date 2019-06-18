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
				<div class="text-with-title-row">
					<c:if test="${not empty contentItem.sectionTitle}">
						<h3>${contentItem.sectionTitle}</h3>
					</c:if>
					<c:if test="${not empty contentItem.paraText}" >
						<p>${contentItem.paraText}</p>
					</c:if>
				</div>
			</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>