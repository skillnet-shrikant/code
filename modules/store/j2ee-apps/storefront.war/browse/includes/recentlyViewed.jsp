<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="productImageRoot" bean="/mff/MFFEnvironment.productImageRoot" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="recentlyViewedProducts" bean="Profile.recentlyViewedProducts" />

	<%-- At the time when img error event fires, KP JS namespace is not yet defined, and no error listeners are bound yet, so use inline JS for initial handling of image errors --%>
	<script>
		function productImagesPageOnImgError(img, imgType) {
	 		img.onerror = null; // reset onerror
		 	var lPath  = '${productImageRoot}/unavailable/l.jpg',
				xlPath = '${productImageRoot}/unavailable/xl.jpg',
				thPath = '${productImageRoot}/unavailable/th.jpg';
			if(imgType === 'main'){
				img.parentNode.getElementsByTagName('source')[0].srcset = lPath;
				img.parentNode.getElementsByTagName('source')[1].srcset = xlPath;
				img.src = lPath;
			}else if(imgType === 'thumb'){
				img.parentNode.getElementsByTagName('source')[0].srcset = thPath; // update source's srcset
				img.src = thPath;
			}
		}
	</script>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${recentlyViewedProducts}"/>
		<dsp:oparam name="outputStart">
			<section class="recently-viewed-items">
				<div class="section-title">
					<h2>Recently Viewed</h2>
				</div>
				<div class="section-row">
					<div class="section-content">
						<div class="product-slider recently-viewed">
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:include page="/browse/includes/productTile.jsp">
				<dsp:param name="product" param="element.product" />
				<dsp:param name="gridType" value="grid4" />
			</dsp:include>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
						</div>
					</div>
				</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
