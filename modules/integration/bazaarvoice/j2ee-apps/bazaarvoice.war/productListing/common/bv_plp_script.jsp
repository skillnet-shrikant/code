<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:getvalueof var="records" param="records" />
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${records}"/>
		<dsp:param name="elementName" value="record"/>
		<dsp:oparam name="outputStart">
			<script type="text/javascript">
				$BV.ui( 'rr', 'inline_ratings', {
				productIds:[
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:getvalueof var="record" param="record" />
			<dsp:getvalueof var="size" param="size" />
			<dsp:getvalueof var="count" param="count" />
				'${record.attributes['product.repositoryId']}'
			<c:if test="${size> count}">
				,
			</c:if>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
				],
				containerPrefix:'BVRRInlineRating'
				});
			</script>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>