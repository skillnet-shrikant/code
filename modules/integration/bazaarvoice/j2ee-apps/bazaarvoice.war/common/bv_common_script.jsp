<dsp:page>
	<dsp:importbean bean="/com/bv/configuration/BVConfiguration"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:getvalueof var="environmentName" bean="BVConfiguration.displayEnvironmentName" />
	<dsp:getvalueof var="clientName" bean="BVConfiguration.clientName" />
	<dsp:getvalueof var="jsName" bean="BVConfiguration.displayjsName" />
	<dsp:getvalueof var="isStaging" bean="BVConfiguration.staging" />
	<dsp:getvalueof var="displayStagingUrl" bean="BVConfiguration.displayStagingUrl" />
	<dsp:getvalueof var="displayUrl" bean="BVConfiguration.displayUrl" />
	<dsp:getvalueof var="locale" bean="BVConfiguration.locale" />
	<dsp:getvalueof var="siteId" bean="BVConfiguration.displaySiteId"/>
	
	<dsp:droplet name="Switch">
		<dsp:param name="value" value="${isStaging}"/>
		<dsp:oparam name="true">
			<script type="text/javascript" src="${displayStagingUrl}/${environmentName}/static/${clientName}/${siteId}/${locale}/${jsName}"></script>
		</dsp:oparam>
		<dsp:oparam name="false">
			<script type="text/javascript" src="${displayUrl}/${clientName}/${siteId}/${locale}/${jsName}"></script>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>