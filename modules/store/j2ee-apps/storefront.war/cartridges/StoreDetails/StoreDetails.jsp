
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<section>
		<div class="section-title">
			<h2><dsp:valueof param="storeItem.pageStoreDetailHeader"/></h2>
		</div>
		<div class="section-row">
			<div class="section-content">
				<dsp:valueof param="storeItem.pageStoreDetailBody" valueishtml="true"/>
			</div>
		</div>
	</section>
</dsp:page>