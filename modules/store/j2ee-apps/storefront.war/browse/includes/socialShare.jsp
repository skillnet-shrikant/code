
<c:choose>
	<c:when test="${!empty encodedSeoCanonicalURL}">
		<c:set var="shareUrl" value="${encodedSeoCanonicalURL}" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="shareUrl" value="https://www.fleetfarm.com/detail/p/${productId}" scope="request" />
	</c:otherwise>
</c:choose>


<%-- social media --%>
<div class="social-icons">

	<%-- pinterest --%>
	<script async defer src="//assets.pinterest.com/js/pinit.js"></script>
	<a href="https://www.pinterest.com/pin/create/button/">
		<img src="//assets.pinterest.com/images/pidgets/pinit_fg_en_rect_gray_20.png" />
	</a>

	<%-- twitter --%>
	<a href="https://twitter.com/share" class="twitter-share-button" data-url="${shareUrl}" data-show-count="false"></a>
	<script>
		if (!window.twttr) {
			window.twttr = (function (d,s,id) {
				var t, js, fjs = d.getElementsByTagName(s)[0];
				if (d.getElementById(id)) return; js=d.createElement(s); js.id=id;
				js.src="//platform.twitter.com/widgets.js"; fjs.parentNode.insertBefore(js, fjs);
				return window.twttr || (t = { _e: [], ready: function(f){ t._e.push(f) } });
			}(document, "script", "twitter-wjs"));
			// Define our custom event handlers
			function clickEventToAnalytics (intentEvent) {
				if (!intentEvent) return;
				KP.analytics.trackEvent('Product Detail', 'Product Detail', 'twitter', window.location.href);
			}
			
			// Wait for the asynchronous resources to load
			window.twttr.ready(function (twttr) {
				// Now bind our custom events
				twttr.events.bind('click', clickEventToAnalytics);
			});
		} else {
			if (twttr.widgets) {
				twttr.widgets.load();
			}
		}
	</script>
			

	<%-- facebook --%>
	<div class="facebook-icons">
		<div id="fb-root"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.8";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));
		
			  window.fbAsyncInit = function() {
				FB.Event.subscribe('edge.create', function (response) {
					if (response && response.post_id) {
						KP.analytics.trackEvent('Product Detail', 'Product Detail', 'FB Share', window.location.href);
					}else if (response) {
						KP.analytics.trackEvent('Product Detail', 'Product Detail', 'FB Like', window.location.href);
					}
				});
			}
		
		</script>
		<div class="fb-like" data-href="${shareUrl}" data-layout="button_count" data-action="like" data-size="small" data-show-faces="false" data-share="true"></div>
	</div>

	<div class="site-functions">
		<dsp:a href="${contextPath}/browse/ajax/emailProductModal.jsp" class="modal-trigger" data-target="email-product-modal"><span class="icon icon-email"></span>
			<dsp:param name="productId" value="${productId}"/>
		</dsp:a>
		<a href="#" class="print"><span class="icon icon-print"></span></a>
	</div>

</div>
