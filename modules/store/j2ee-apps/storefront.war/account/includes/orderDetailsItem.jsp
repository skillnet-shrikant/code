<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
	<dsp:importbean bean="/com/mff/droplet/MFFDynamicAttributesBySkuDroplet"/>
	<dsp:importbean bean="/mff/MFFEnvironment"/>
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />

	<%-- Page Variables --%>
	<dsp:getvalueof param="bopis" var="bopis" />
	<dsp:getvalueof param="bopisClass" var="bopisClass" />
	<dsp:getvalueof param="commerceItem.quantity" var="qty" />
	<dsp:getvalueof param="commerceItem.listPrice" var="listPrice" />
	<dsp:getvalueof param="commerceItem.catalogRefId" var="skuId" />
	<dsp:getvalueof param="commerceItem.productId" var="productId" />
	<dsp:getvalueof param="commerceItem.siteId" var="siteId" />
	<dsp:getvalueof param="isLegacy" var="isLegacy" />
	<dsp:getvalueof param="trackingNumber" var="trackingNumber" />

	<dsp:getvalueof bean="MFFEnvironment.productImageRoot" var="productImageRoot" />

	<dsp:droplet name="/com/mff/droplet/ProductUrlGeneratorDroplet">
		<dsp:param name="productId" value="${productId}"/>
		<dsp:oparam name="output">
			<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
				<dsp:param name="inUrl" param="url"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="productUrl" scope="request" param="secureUrl"/>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="isGWPItem" param="commerceItem.gwp"/>

	<dsp:droplet name="ProductLookup">
		<dsp:param name="id" param="commerceItem.productId"/>
		<dsp:param name="filterByCatalog" value="false"/>
		<dsp:param name="filterBySite" value="false"/>
		<dsp:param name="elementName" value="productItem"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="productItem.description" var="productName" />
			<dsp:getvalueof param="productItem" var="productItem" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:setvalue param="productItem" value="${productItem}" />

	<div class="order-item">
		<div class="item-image order-item-section">
			<a href="${productUrl}">
				<dsp:droplet name="/com/mff/browse/droplet/ProductImageDroplet">
					<dsp:param name="productId" value="${productId}" />
					<dsp:param name="imageSize" value="s" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="defaultImage" param="productImages[0]" />
						<picture>
							<!--[if IE 9]><video style="display: none;"><![endif]-->
							<source srcset="${productImageRoot}/${productId}/xs/${defaultImage}" media="(min-width: 768px)">
							<!--[if IE 9]></video><![endif]-->
							<img src="${productImageRoot}/${productId}/s/${defaultImage}" alt="${productName}" />
						</picture>
					</dsp:oparam>
					<dsp:oparam name="empty">
						<picture>
							<!--[if IE 9]><video style="display: none;"><![endif]-->
							<source srcset="${productImageRoot}/unavailable/xs.jpg" media="(min-width: 768px)">
							<!--[if IE 9]></video><![endif]-->
							<img src="${productImageRoot}/unavailable/s.jpg" alt="Image Unavailable" />
						</picture>
					</dsp:oparam>
				</dsp:droplet>
			</a>
		</div>
		<div class="item-details order-item-section ${bopisClass}">
			<div class="product-name">
				<a href="${productUrl}">${productName}</a>
			</div>
			<div class="price">
				<dsp:droplet name="Compare">
					<dsp:param name="obj1" param="commerceItem.salePrice" />
					<dsp:param name="obj2" param="commerceItem.listPrice" />
					<dsp:oparam name="lessthan">
						<div class="original-price">
							<dsp:valueof param="commerceItem.listPrice" converter="currency"/>
						</div>
						<div class="sale-price">
							<dsp:valueof param="commerceItem.salePrice" converter="currency"/>
						</div>
					</dsp:oparam>
					<dsp:oparam name="default">
						<div class="regular-price">
							<dsp:valueof param="commerceItem.listPrice" converter="currency"/>
						</div>
					</dsp:oparam>
				</dsp:droplet>
			</div>
			<div class="product-sku">
				<span class="label">SKU:</span>
				<span>${skuId}</span>
			</div>
			<div class="product-selections">
				<dsp:droplet name="SKULookup">
					<dsp:param name="id" param="commerceItem.catalogrefId"/>
					<dsp:param name="filterByCatalog" value="false"/>
					<dsp:param name="filterBySite" value="false"/>
					<dsp:param name="elementName" value="skuItem"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="isLTL" param="skuItem.ltl" />
						<dsp:droplet name="MFFDynamicAttributesBySkuDroplet">
							<dsp:param name="product" param="productItem" />
							<dsp:param name="sku" param="skuItem" />
							<dsp:oparam name="output">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="dynAttributes"/>
									<dsp:param name="elementName" value="attributeValue"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="attributeValue" param="attributeValue"/>
										<c:if test="${not empty attributeValue}">
											<div class="variant">
												<span class="label"><dsp:valueof param="key" />:</span>
												<span>${attributeValue}</span>
											</div>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
				<div class="variant">
					<span class="label">Qty:</span>
					<span><dsp:valueof param="commerceItem.quantity"/></span>
				</div>
				<div class="variant item-price">
					<span class="label">Price:</span>
					<dsp:droplet name="Compare">
						<dsp:param name="obj1" param="commerceItem.salePrice" />
						<dsp:param name="obj2" param="commerceItem.listPrice" />
						<dsp:oparam name="lessthan">
							<div class="original-price">
								<dsp:valueof param="commerceItem.listPrice" converter="currency"/>
							</div>
							<div class="sale-price">
								<dsp:valueof param="commerceItem.salePrice" converter="currency"/>
							</div>
						</dsp:oparam>
						<dsp:oparam name="default">
							<div class="regular-price">
								<dsp:valueof param="commerceItem.listPrice" converter="currency"/>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</div>
				<!--  Bazaar voice variant -->
				<div class="variant">
					<c:if test="${bvEnabled}">
						<div class="product-reviews" itemprop="aggregateRating" itemscope itemtype="http://schema.org/AggregateRating">
							<dsp:include otherContext="/bv" page="/productListing/ratings/bv_plp_ratings.jsp">
								<dsp:param name="productId" value="${productId}" />
							</dsp:include>
						</div>
						<c:set var="contextPath" value="${currentContext}" />
					</c:if>
				</div>
			</div>
			<%--2393 Removing the GWP note --%>
			<%--
			<c:if test="${isGWPItem}">
				<a class="gwp-note modal-trigger" href="/checkout/ajax/gwpDetailsModal.jsp" data-target="promo-details-modal" data-size="small">
					Free item discount is reflected in the price of the qualifying item. <span class="icon icon-info"></span>
				</a>
			</c:if>
			 --%>
		</div>

		<c:if test="${not bopis}">
			<div class="item-tracking order-item-section">
				<span class="label">Tracking #:</span>
				<dsp:droplet name="Switch">
					<dsp:param name="value" param="isLegacy"/>
					<dsp:oparam name="true">
						<c:choose>
							<c:when test="${empty trackingNumber}">
								&nbsp;
							</c:when>
							<c:otherwise>
								${trackingNumber}
							</c:otherwise>
						</c:choose>
					</dsp:oparam>
					<dsp:oparam name="false">
						<dsp:getvalueof var="itemTrackingNum" param="commerceItem.trackingNumber"/>
						<c:choose>
							<c:when test="${empty itemTrackingNum}">
								&nbsp;
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${isLTL eq 'true'}">
										${itemTrackingNum}
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="fedExTrackingUrl" bean="MFFEnvironment.fedExTrackingUrl" />
										<ul class="trk-num-list">
											<c:forTokens items="${itemTrackingNum}" delims="|" var="individualTrackingNumber">
												<li><a href="${fedExTrackingUrl}${individualTrackingNumber}" target="_blank" >${individualTrackingNumber}</a></li>
											</c:forTokens>
										</ul>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</dsp:oparam>
				</dsp:droplet>
			</div>
		</c:if>

		<div class="item-price-subtotal order-item-section ${bopisClass}">
			<span class="label">Total:</span>

			<%-- 2414 - Display FREE instead of $0.00 for GWP items --%>
			<dsp:getvalueof var="totalLinePrice" param="commerceItem.lineItemTotal" />
			<span class="item-total">
				<c:choose>
					<c:when test="${totalLinePrice eq 0}">
						FREE
					</c:when>
					<c:otherwise>
						<dsp:valueof param="commerceItem.lineItemTotal" converter="currency"/>
					</c:otherwise>
				</c:choose>
			</span>
		</div>

		<div class="item-action-links order-item-section">
			<a href="${productUrl}" class="button primary add-to-cart">View Item</a>
		</div>

	</div>
</dsp:page>
