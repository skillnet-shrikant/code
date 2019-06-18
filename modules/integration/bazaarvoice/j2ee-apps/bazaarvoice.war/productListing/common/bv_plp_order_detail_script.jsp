<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:getvalueof var="commerceItems" param="commerceItems" />
	
	<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${commerceItems}"/>
			<dsp:param name="elementName" value="commerceItem"/>
			<dsp:oparam name="outputStart">
				<script type="text/javascript">
					$BV.ui( 'rr', 'inline_ratings', {
					productIds:[
			</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:getvalueof var="commerceItem" param="commerceItem" />
				<dsp:getvalueof var="size" param="size" />
				<dsp:getvalueof var="count" param="count" />
					'${commerceItem.productId}'
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