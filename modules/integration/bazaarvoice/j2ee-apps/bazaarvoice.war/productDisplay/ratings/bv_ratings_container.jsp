<dsp:page>
	<dsp:getvalueof var="externalId" param="externalId" />
	<dsp:getvalueof var="pageType" param="pageType" />
	<dsp:getvalueof var="seoRatings" param="seoRatings" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<div id="BVRRSummaryContainer">
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" param="seoRatings"/>
			<dsp:oparam name="false">
				<!-- SEO ratings are not empty -->
				<dsp:valueof param="seoRatings" valueishtml="true"/>
			</dsp:oparam>
			<dsp:oparam name="true">
				<!-- SEO ratings are empty -->
			</dsp:oparam>
		</dsp:droplet>
		
	</div>
	<c:if test="${pageType eq 'quickview'}">
		<script type="text/javascript">
			$BV.ui("rr", "show_reviews", {
			productId: "${externalId}",
			});
		</script>
	</c:if>
</dsp:page>