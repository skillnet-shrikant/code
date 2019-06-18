<%--
	Page Parameters:
		gridType: grid4, grid3, catgrid
		showQuickView: true for category/search results pages
		idImgPrefix: Value entered in cartridge to be used in id attrib of anchor tags of prod images
		idNamePrefix: Value entered in cartridge to be used in id attrib of anchor tags on prod names
--%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/commerce/catalog/CatalogTools" />
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/mff/droplet/MFFDynamicAttributesBySkuDroplet"/>
	
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="gtmEnabled" bean="/mff/MFFEnvironment.gtmEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="productImageRoot" bean="/mff/MFFEnvironment.productImageRoot" />
	<dsp:getvalueof var="product" param="product" />
	<dsp:getvalueof var="productId" param="productId" vartype="java.lang.String" />
	<dsp:getvalueof var="gridType" param="gridType" />
	<dsp:getvalueof var="idImgPrefix" param="idImgPrefix"/>
	<dsp:getvalueof var="idNamePrefix" param="idNamePrefix"/>
	<dsp:getvalueof var="showQuickView" param="showQuickView" />
	<dsp:getvalueof var="searchTerm"  param="Ntt"/>
	<dsp:getvalueof var="giftCardProductId" bean="CatalogTools.giftCardProductId" />
	<dsp:getvalueof var="position" param="position" />
	<dsp:getvalueof var="analyticPageType" param="pageType"/>
	<dsp:getvalueof var="list" param="list" />
	
	<%-- if there is a product, use the product --%>
	<%-- if there is NOT a product, use the product id to find the product --%>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" param="product" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="productId" param="product.id" />
			<dsp:getvalueof var="productName" param="product.description" />
			<dsp:getvalueof var="productBrand" param="product.brand" />
			<dsp:getvalueof var="childSkus" param="product.childSkus" />
			<dsp:getvalueof var="isFreeFreightShipment" param="product.childSkus[0].freeShipping"/>
			<dsp:getvalueof var="teaserPLPMessage" param="product.teaserPLPMessage" />
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:droplet name="/atg/commerce/catalog/ProductLookup">
				<dsp:param name="id" param="productId"/>
				<dsp:param name="elementName" value="product"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="product" param="product" />
					<dsp:getvalueof var="productName" param="product.description" />
					<dsp:getvalueof var="productBrand" param="product.brand" />
					<dsp:getvalueof var="childSkus" param="product.childSkus" />
					<dsp:getvalueof var="childSkuId" param="product.childSkus[0].id" />
					<dsp:getvalueof var="isFreeFreightShipment" param="product.childSkus[0].freeShipping"/>
					<dsp:getvalueof var="parentCategoriesList" param="product.parentCategories" />
					<dsp:getvalueof var="teaserPLPMessage" param="product.teaserPLPMessage" />
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
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
	<dsp:droplet name="/com/mff/browse/droplet/IsInactiveProduct">
	<dsp:param name="productId" value="${productId}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="isInactiveProd" param="isInActiveProduct"/>
			<dsp:getvalueof var="isActiveTeaser" param="isActiveTeaser"/>
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${not empty idImgPrefix}">
		<c:set var="idImgTag">id="${idImgPrefix}-${productId}"</c:set>
	</c:if>
	<c:if test="${not empty idNamePrefix}">
		<c:set var="idNameTag">id="${idNamePrefix}-${productId}"</c:set>
	</c:if>

	<dsp:droplet name="/atg/collections/filter/droplet/StartEndDateFilterDroplet">
		<dsp:param name="collection" value="${childSkus}" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="skus" param="filteredCollection"/>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="productNameTagVal" value="data-prod-name-tag='${fn:trim(fn:escapeXml(fn:replace(productName,productBrand,'')))}'" />
	
	<div class="product-tile">
		<div class="product-image">
			<c:if test="${showQuickView}">
				<a href="${contextPath}/browse/ajax/quickViewModal.jsp?productId=${productId}" class="modal-trigger button primary button-quickview" data-target="quick-view-modal">Quick View</a>
			</c:if>
			<c:choose>
				<c:when test="${not empty param.Ntt}">
					<a href="${productUrl}?Ntt=${param.Ntt}" data-pid="${productId}" data-action="${productUrl}?Ntt=${param.Ntt}">
				</c:when>
				<c:when test="${not empty brandCrumb}">
					<a href="${productUrl}?bc=${pdpCrumb}&brandCrumb=${brandCrumb}&bn=${brandURIName}" data-pid="${productId}" data-action="${productUrl}?bc=${pdpCrumb}&brandCrumb=${brandCrumb}&bn=${brandURIName}">
				</c:when>
				<c:otherwise>
					<a href="${productUrl}?bc=${pdpCrumb}" ${productNameTagVal} ${idImgTag} data-pid="${productId}" data-action="${productUrl}?bc=${pdpCrumb}">
				</c:otherwise>
			</c:choose>
				<dsp:droplet name="/com/mff/browse/droplet/ProductImageDroplet">
					<dsp:param name="productId" value="${productId}" />
					<dsp:param name="imageSize" value="m" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="defaultImage" param="productImages[0]" />
						<c:choose>
							<c:when test="${gridType == 'grid3'}">
								<img src="${productImageRoot}/${productId}/m/${defaultImage}" alt="${productName}" data-pin-nopin="true"/>
							</c:when>
							<c:when test="${gridType == 'catgrid'}">
								<picture>
									<!--[if IE 9]><video style="display: none;"><![endif]-->
									<source srcset="${productImageRoot}/${productId}/l/${defaultImage}" media="(min-width: 980px)">
									<source srcset="${productImageRoot}/${productId}/m/${defaultImage}" media="(min-width: 768px)">
									<!--[if IE 9]></video><![endif]-->
									<img src="${productImageRoot}/${productId}/m/${defaultImage}" alt="${productName}" data-pin-nopin="true" onError="productImagesPageOnImgError(this, 'main');"/>
								</picture>
							</c:when>
							<c:otherwise>
								<%-- grid4 or no gridType parameter --%>
								<picture>
									<!--[if IE 9]><video style="display: none;"><![endif]-->
									<source srcset="${productImageRoot}/${productId}/l/${defaultImage}" media="(min-width: 980px)">
									<source srcset="${productImageRoot}/${productId}/m/${defaultImage}" media="(min-width: 768px)">
									<!--[if IE 9]></video><![endif]-->
									<img src="${productImageRoot}/${productId}/m/${defaultImage}" alt="${productName}" data-pin-nopin="true" onError="productImagesPageOnImgError(this, 'main');"/>
								</picture>
							</c:otherwise>
						</c:choose>
					</dsp:oparam>
					<dsp:oparam name="empty">
						<c:choose>
							<c:when test="${gridType == 'grid3'}">
								<img src="${productImageRoot}/unavailable/m.jpg" alt="Image Unavailable" data-pin-nopin="true"/>
							</c:when>
							<c:when test="${gridType == 'catgrid'}">
								<picture>
									<!--[if IE 9]><video style="display: none;"><![endif]-->
									<source srcset="${productImageRoot}/unavailable/l.jpg" media="(min-width: 980px)">
									<source srcset="${productImageRoot}/unavailable/m.jpg" media="(min-width: 768px)">
									<!--[if IE 9]></video><![endif]-->
									<img src="${productImageRoot}/unavailable/m.jpg" alt="Image Unavailable" data-pin-nopin="true"/>
								</picture>
							</c:when>
							<c:otherwise>
								<%-- grid4 or no gridType parameter --%>
								<picture>
									<!--[if IE 9]><video style="display: none;"><![endif]-->
									<source srcset="${productImageRoot}/unavailable/m.jpg" media="(min-width: 768px)">
									<!--[if IE 9]></video><![endif]-->
									<img src="${productImageRoot}/unavailable/m.jpg" alt="Image Unavailable" data-pin-nopin="true"/>
								</picture>
							</c:otherwise>
						</c:choose>
					</dsp:oparam>
				</dsp:droplet>
			</a>
		</div>
		<div class="product-details-container">

			<div class="product-name">
				<dsp:getvalueof var="productNameWithoutBrand" value="${fn:substringAfter(productName,productBrand)}" />
				<c:choose>
					<c:when test="${not empty param.Ntt}">
						<a href="${productUrl}?Ntt=${param.Ntt}" data-pid="${productId}" data-action="${productUrl}?Ntt=${param.Ntt}">${productNameWithoutBrand}</a>
					</c:when>
					<c:when test="${not empty brandCrumb}">
						<a href="${productUrl}?bc=${pdpCrumb}&brandCrumb=${brandCrumb}&bn=${brandURIName}" data-pid="${productId}" data-action="${productUrl}?bc=${pdpCrumb}&brandCrumb=${brandCrumb}&bn=${brandURIName}">${productNameWithoutBrand}</a>
					</c:when>
					<c:otherwise>
						<a href="${productUrl}?bc=${pdpCrumb}" ${productNameTagVal} ${idNameTag} data-pid="${productId}" data-action="${productUrl}?bc=${pdpCrumb}">${productNameWithoutBrand}</a>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="product-brand">
				${productBrand}
			</div>

			<c:if test="${bvEnabled}">
				<div class="product-reviews" itemprop="aggregateRating" itemscope itemtype="http://schema.org/AggregateRating">
					<dsp:include otherContext="/bv" page="/productListing/ratings/bv_plp_ratings.jsp">
						<dsp:param name="productId" value="${productId}" />
					</dsp:include>
				</div>
				<c:set var="contextPath" value="${currentContext}" />
			</c:if>
			<div class="price" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
				<c:choose>
					<c:when test="${productId != giftCardProductId}">
						<c:choose>
								<c:when test="${isActiveTeaser}">
									<c:choose>
										<c:when test="${empty teaserPLPMessage}">
											<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
  												<dsp:param name="queryRQL" value="infoKey=\"EVENT_ITEM_PLP\""/>
  												<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
  												<dsp:param name="itemDescriptor" value="infoMessage"/>
  												<dsp:oparam name="output">
													<div class="discontinued-map-pricing-label">
															<dsp:valueof param="element.infoMsg" valueishtml="true"/>
													</div>
  												</dsp:oparam>
											</dsp:droplet>
										</c:when>
										<c:otherwise>
												<div class="discontinued-map-pricing-label">
														<c:out value="${teaserPLPMessage}" escapeXml="false"/>
												</div>
										</c:otherwise>
									</c:choose>
								</c:when>

							<c:otherwise>
								<c:if test="${!isInactiveProd}">
									<span itemprop="priceCurrency" content="USD" />
									<dsp:include page="/browse/includes/productPrice.jsp" >
										<dsp:param name="productItem" value="${product}" />
										<dsp:param name="skus" value="${skus}"/>
									</dsp:include>
								</c:if>
							</c:otherwise>
						</c:choose>

					</c:when>
					<c:otherwise> &nbsp; </c:otherwise>
				</c:choose>
			</div>
			<%--BZ: 2427 --%>
			<dsp:droplet name="/com/mff/droplet/MoreStylesAvailableDroplet">
				<dsp:param name="product" value="${product}" />
				<dsp:oparam name="true">
					<div class="more-styles">
						More Styles Available
					</div>
				</dsp:oparam>
			</dsp:droplet>

			<c:if test="${isFreeFreightShipment}">
				<div class="free-freight-shipping-label">
					<span class="icon icon-freight-truck"></span>
					<span>Free Freight Shipping</span>
				</div>
			</c:if>
		</div>
	</div>
	<c:if test="${gtmEnabled and (analyticPageType eq 'category' or analyticPageType eq 'search')}">
		
		<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${parentCategoriesList}"/>
			<dsp:param name="elementName" value="category"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="catName" param="category.displayName" />
			</dsp:oparam>
		</dsp:droplet>
			
		<script class="analytics-script analytics-browse">
			(function () {
				digitalData.products.push({
					"name": "${fn:escapeXml(productNameWithoutBrand)}",
					'id': '${productId}',
					'price': '${listPrice}',
					'brand': '${fn:escapeXml(productBrand)}',
					'category': '${fn:escapeXml(catName)}',
					'variant': [
						<dsp:droplet name="/atg/commerce/catalog/SKULookup">
							<dsp:param name="id" value="${childSkuId}"/>
							<dsp:param name="filterByCatalog" value="false"/>
							<dsp:param name="filterBySite" value="false"/>
							<dsp:param name="elementName" value="skuItem"/>
							<dsp:oparam name="output">
								<dsp:droplet name="MFFDynamicAttributesBySkuDroplet">
									<dsp:param name="product" value="${product}" />
									<dsp:param name="sku" param="skuItem" />
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="dynAttributes"/>
											<dsp:param name="elementName" value="attributeValue"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="key" param="key"/>
												<dsp:getvalueof var="attributeValue" param="attributeValue"/>
												<dsp:getvalueof var="index" param="index" />
												<c:if test="${index > 0}">,</c:if>
												<json:object>
													 <json:property name='${key}'>
														'${fn:escapeXml(attributeValue)}'
													 </json:property>
												 </json:object>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
					],
					'list': '${list}',
					'position': '${position}'
				});
			})();
		</script>
	</c:if>
</dsp:page>
