<dsp:page>
	<dsp:getvalueof var="externalId" param="externalId" />
	<c:set var="externalIdString" value="'${externalId}'" />
	<script type="text/javascript">
		    $BV.ui("rr", "show_summary", {
		    	productId: "${externalIdString}"
		    });
	</script>
</dsp:page>