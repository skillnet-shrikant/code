<dsp:page>
	<dsp:importbean bean="/com/bv/configuration/BVConfiguration"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:getvalueof var="stagingEnvironmentName" bean="BVConfiguration.bvLoaderStagingEnvironmentName" />
	<dsp:getvalueof var="productionEnvironmentName" bean="BVConfiguration.bvLoaderProductionEnvironmentName" />
	<dsp:getvalueof var="clientName" bean="BVConfiguration.clientName" />
	<dsp:getvalueof var="jsName" bean="BVConfiguration.bvLoaderjsName" />
	<dsp:getvalueof var="isStaging" bean="BVConfiguration.staging" />
	<dsp:getvalueof var="stagingUrl" bean="BVConfiguration.bvLoaderStagingUrl" />
	<dsp:getvalueof var="productionUrl" bean="BVConfiguration.bvLoaderUrl" />
	<dsp:getvalueof var="locale" bean="BVConfiguration.locale" />
	<dsp:getvalueof var="siteId" bean="BVConfiguration.bvLoaderSiteId" />
	
	<dsp:droplet name="Switch">
		<dsp:param name="value" value="${isStaging}"/>
		<dsp:oparam name="true">
			<script type="text/javascript" src="${stagingUrl}/${clientName}/${siteId}/${stagingEnvironmentName}/${locale}/${jsName}"></script>
		</dsp:oparam>
		<dsp:oparam name="false">
			<script type="text/javascript" src="${productionUrl}/${clientName}/${siteId}/${productionEnvironmentName}/${locale}/${jsName}"></script>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>