<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" param="mobile"/>
		<dsp:oparam name="true">
			<div class="masthead-logo">
				<a href="${contextPath}/">
					<img src="${contentItem.imageMobileURL}" alt="${contentItem.imageAltText}"/>
				</a>
			</div>
		</dsp:oparam>
		<dsp:oparam name="false">
			<div class="logo">
				<a href="${contextPath}/">
					<img src="${contentItem.imageDesktopURL}" alt="${contentItem.imageAltText}" />
				</a>
			</div>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>