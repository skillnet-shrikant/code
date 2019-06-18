<%--
	- File Name: weeklyAd.jsp
	- Author(s): KnowledgePath Solutions
	- Copyright Notice:
	- Description: This is the Wishabi/Flipp weekly ad
	--%>

<dsp:page>

	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="weeklyad" />
		<dsp:param name="defaultPageTitle" value="Weekly Ad" />
		<dsp:param name="defaultMetaDescription" value="Fleet Farm Weekly Ad" />
		<dsp:param name="defaultCanonicalURL" value="" />
		<dsp:param name="defaultRobotsIndex" value="index" />
		<dsp:param name="defaultRobotsFollow" value="follow" />
	</dsp:include>	
	<layout:default>
		<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
		<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
		<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">home</jsp:attribute>
		<jsp:attribute name="pageType">weeklyAd</jsp:attribute>
		<jsp:attribute name="bodyClass">weekly-ad</jsp:attribute>
		<jsp:body>

			<%-- title --%>
			<div class="section-title">
				<h1>Weekly Ad</h1>
			</div>

			<div class="section-row">
				<div class="section-content">
					<script src="//weeklyad.fleetfarm.com/hosted_services/js/2.1.1/wishabi.js"></script>
					<!-- Flipp Integration Code for Weekly Ad | Start -->
					<script src="//weeklyad.fleetfarm.com/hosted_services/iframe.js" type="text/javascript"></script>
					<div id="circ_div"></div>
					<script>
						var pageSizing = 'PAGE';
						var minHeight = 600;
						var initialHeight = 1000;
						var extraPadding = 0;
						var queryParameters = 'auto_locate=true&auto_store=true';
						var circ_iframe = new wishabi.hostedservices.iframe.decorate(
							'circ_div',
							'fleetfarm',
							wishabi.hostedservices.iframe.Sizing[pageSizing],
							{
								minHeight: minHeight,
								initialHeight: initialHeight,
								extraPadding: extraPadding,
								queryParameters: queryParameters
							});
							
							
						// Define wishabi.js configuration parameters
						var flippDomain = window.location.protocol + '//weeklyad.fleetfarm.com';
						var flippContainer = document.getElementById('circ_div');
						var flippIframe = flippContainer.getElementsByTagName('iframe')[0];
						
						// Define the Analytics API event handler
						var flippAnalyticsDelegate = {
							handleAnalyticsEvent: function(flippAnalytics, detail) {
							
								// Pass selected event types to Google Analytics
								switch (detail.event_type) {
									case "open":
											// flyer select
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'Select Circular', detail.external_run_name);
											break;
									case "store_selector_open":
											// my store -> change store location
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'My Store', 'Change Local Store');
											break;
									case "category_select":
											// categories -> category 
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'Categories', '');
											break;
									case "discount_filter":
											// discount filter -> usage of slider
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'Discount Slider', '');
											break;
									case "list_add":
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'Add item', detail.item_id || '');
											break;
									case "list_remove":
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'Remove item', detail.item_id);
											break;
									case "item_ttm":
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'Shop Now/Find a store', detail.item_id || '');
											break;
									case "item_open":
											KP.analytics.trackEvent('Weekly Ad', 'Weekly Ad', 'Details', detail.item_name || '');
										break;
								}
							}
						};
						// Register the event handler with the Analytics API
						var flippAnalytics = new wishabi.hostedservices.iframe.Analytics(
							window, flippIframe.contentWindow, flippAnalyticsDelegate, flippDomain
						);
					</script>
					<!-- end Flipp Code -->

				</div>
			</div>

		</jsp:body>
	</layout:default>

</dsp:page>
