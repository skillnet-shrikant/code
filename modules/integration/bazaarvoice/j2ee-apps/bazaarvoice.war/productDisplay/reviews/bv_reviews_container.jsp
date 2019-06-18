<dsp:page>
	<!-- Reviews Container goes below product description -->
	<dsp:getvalueof var="seoReviews" param="seoReviews" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<div id="BVRRContainer">
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" param="seoRatings"/>
			<dsp:oparam name="false">
				<!-- SEO reviews are not empty -->
				<dsp:valueof param="seoReviews" valueishtml="true"/>
			</dsp:oparam>
			<dsp:oparam name="true">
				<!-- SEO reviews are empty -->
			</dsp:oparam>
		</dsp:droplet>
		
	</div>
	<script type="text/javascript">
         $BV.ui('rr', 'show_reviews', {
             doShowContent: function() {
               // If the container is hidden (such as behind a tab), put code here to make it visible 
               //(open the tab).
             }
         });
     </script>
</dsp:page>