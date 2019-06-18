<%-- MFF set a maximum of 5 alt images in Rapid Prototype Review #2 on 7/19/2016 --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/mff/browse/droplet/ProductImageDroplet"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="productName" param="productName" />
	<dsp:getvalueof var="productVideos" param="productVideos" />
	<dsp:getvalueof var="productImageRoot" bean="MFFEnvironment.productImageRoot"/>

	<%-- At the time when img error event fires, KP JS namespace is not yet defined, and no error listeners are bound yet, so use inline JS for initial handling of image errors --%>
	<script>
		function productImagesPageOnImgError(img, imgType){
			img.onerror = null; // reset onerror
			var lPath  = '${productImageRoot}/unavailable/l.jpg';
			var xlPath = '${productImageRoot}/unavailable/xl.jpg';
			var thPath = '${productImageRoot}/unavailable/th.jpg';
			if (imgType === 'main') {
				img.parentNode.getElementsByTagName('source')[0].srcset = lPath;
				img.parentNode.getElementsByTagName('source')[1].srcset = xlPath;
				img.src = lPath;
			} else if(imgType === 'thumb') {
				img.parentNode.getElementsByTagName('source')[0].srcset = thPath; // update source's srcset
				img.src = thPath;
			}
		}
	</script>

	<%-- image viewer --%>
	<div class="product-image-viewer">

		<dsp:droplet name="ProductImageDroplet">
			<dsp:param name="productId" param="productId" />
			<dsp:oparam name="output">
				<%-- main image --%>
				<dsp:getvalueof var="productImages" param="productImages" />
				<dsp:getvalueof var="defaultImage" value="${productImages[0]}" />
				<dsp:getvalueof var="productImageLength" value="${fn:length(productImages)}" />
				<div class="viewer-main" tabindex="0">
					<picture>
						<!--[if IE 9]><video style="display: none;"><![endif]-->
						<source id="ml-main-image" srcset="${productImageRoot}/${productId}/l/${defaultImage}" media="(min-width: 768px)">
						<source id="s-main-image" srcset="${productImageRoot}/${productId}/x/${defaultImage}" media="(min-width: 490px)">
						<!--[if IE 9]></video><![endif]-->
						<img class="viewer-main-image" src="${productImageRoot}/${productId}/l/${defaultImage}" data-image-name="${defaultImage}" data-id="${productId}" itemprop="image" alt="${productName}" onError="productImagesPageOnImgError(this, 'main');"/>
					</picture>
				</div>

				<%-- thumbnail images --%>
				<div class="viewer-thumbnails">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${productImages}" />
						<dsp:param name="elementName" value="image" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="index" param="index" />
							<dsp:getvalueof var="image" param="image" />
							<%-- active class for first thumbnail --%>
							<c:set var="activeClass" value="" scope="request" />
							<c:if test="${index == 0}">
								<c:set var="activeClass" value="active" scope="request" />
							</c:if>
							<%-- using responsive images so no image loads on small --%>
							<div class="viewer-thumb ${activeClass}">
								<picture>
									<!--[if IE 9]><video style="display: none;"><![endif]-->
									<source class="th-image" srcset="${productImageRoot}/${productId}/th/${image}" media="(min-width: 768px)">
									<!--[if IE 9]></video><![endif]-->
									<img class="viewer-thumb-image" src="${productImageRoot}/${productId}/x/${image}" data-image-name="${image}" data-id="${productId}" alt="${productName}" onError="productImagesPageOnImgError(this, 'thumb');"/>
								</picture>
							</div>
							<%-- insert videos after second image (or the final image, if less than two exist) --%>
							<c:if test="${index == 1 || (index == productImageLength - 1 && productImageLength <= 2)}">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="productVideos"/>
									<dsp:param name="elementName" value="video"/>
									<dsp:oparam name="output">
										<div class="viewer-thumb">
											<dsp:getvalueof var="video" param="video" />
											<div class="vimeo-thumb vimeo-modal" data-vimeo-id="${video}" data-vimeo-defer></div>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>

				</div>
			</dsp:oparam>

			<dsp:oparam name="empty">
				<div class="viewer-main" tabindex="0">
					<picture>
						<!--[if IE 9]><video style="display: none;"><![endif]-->
						<source id="ml-main-image" srcset="${productImageRoot}/unavailable/l.jpg" media="(min-width: 768px)">
						<source id="s-main-image" srcset="${productImageRoot}/unavailable/xl.jpg" media="(min-width: 490px)">
						<!--[if IE 9]></video><![endif]-->
						<img class="viewer-main-image" src="${productImageRoot}/unavailable/l.jpg" alt="Image Unavailable">
					</picture>
				</div>
				<div class="viewer-thumbnails">
					<div class="viewer-thumb active">
						<picture>
							<!--[if IE 9]><video style="display: none;"><![endif]-->
							<source class="th-image" srcset="${productImageRoot}/unavailable/th.jpg" media="(min-width: 768px)">
							<!--[if IE 9]></video><![endif]-->
							<img class="viewer-thumb-image" src="${productImageRoot}/unavailable/th.jpg" alt="Image Unavailable" />
						</picture>
					</div>
					<%-- product videos --%>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="productVideos"/>
						<dsp:param name="elementName" value="video"/>
						<dsp:oparam name="output">
							<div class="viewer-thumb">
								<dsp:getvalueof var="video" param="video" />
								<div class="vimeo-thumb vimeo-modal" data-vimeo-id="${video}" data-vimeo-defer></div>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</dsp:oparam>

		</dsp:droplet>
	</div>
</dsp:page>
