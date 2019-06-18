<dsp:page>
	<dsp:importbean bean="/com/aci/configuration/AciConfiguration"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="AciConfiguration.enableTieBack"/>
		<dsp:oparam name="false">
			<dsp:droplet name="Redirect">
				<dsp:param name="url" value="/"/>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:droplet name="/com/aci/fraudcheck/droplet/ReDOrderStatusUpdateDroplet">
				<dsp:oparam name="empty">
				</dsp:oparam>
				<dsp:oparam name="error">
				</dsp:oparam>
				<dsp:oparam name="output">
				</dsp:oparam>
			</dsp:droplet>
	
		</dsp:oparam>
	</dsp:droplet>
	
	
	
</dsp:page>