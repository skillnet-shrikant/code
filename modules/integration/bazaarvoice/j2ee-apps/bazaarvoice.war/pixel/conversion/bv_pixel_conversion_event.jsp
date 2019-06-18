<dsp:page>
	<dsp:getvalueof param="type" var="type" />
	<dsp:getvalueof param="label" var="label" />
	<dsp:getvalueof param="scriptValue" var="scriptValue" />
	<script type="text/javascript">
		BV.pixel.trackConversion({
			"type":"${type}"
			"label":"${label}"
			"value":"${scriptValue}"
		
		});
	</script>
	
</dsp:page>