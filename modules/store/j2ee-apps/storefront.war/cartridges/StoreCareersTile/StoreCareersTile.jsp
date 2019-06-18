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
			<li class="${mobileHideClass} ${desktopHideClass}">
				<div class="text-left">
					<h2><dsp:valueof param="storeItem.pageCareerHeader"/></h2>
					<ul>
						<li>
							<dsp:getvalueof param="storeItem.pageCareerImage" var="careerImg"/>
							<img src="${careerImg}" alt="Careers Center" width="100%"/>
						</li>
						<li>
							<dsp:valueof param="storeItem.pageCareerBody" valueishtml="true"/>
						</li>
					</ul>
				</div>
			</li>
			<hr>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>