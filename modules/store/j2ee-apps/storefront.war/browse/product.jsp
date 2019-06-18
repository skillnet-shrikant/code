<%--
	- File Name: product.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Individual product item page
	- Parameters:
	--%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/commerce/catalog/ProductBrowsed"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>
	<dsp:importbean bean="/mff/commerce/catalog/PrimaryNavDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/com/bv/seo/BVSeoDroplet"/>
	<dsp:importbean bean="/com/mff/droplet/BopisStoreAvailabilityDroplet"/>
	<dsp:importbean bean="/com/mff/redirects/GetPDPRedirectURLDroplet" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="productId" param="id" />
	<dsp:getvalueof var="addFromWishList" param="addFromWishList"/>
	<dsp:getvalueof var="bopisOrder" bean="ShoppingCart.current.bopisOrder"/>
	<dsp:getvalueof var="bopisStore" bean="ShoppingCart.current.bopisStore"/>
	<dsp:getvalueof var="fflOrder" bean="ShoppingCart.current.fflOrder"/>
	<dsp:getvalueof var="edsPPSOnlyOrder" bean="ShoppingCart.current.edsPPSOnly"/>
	<dsp:getvalueof var="orderIsShippable" bean="ShoppingCart.current.orderShippable" />

	<%-- is cart currently empty --%>
	<dsp:getvalueof var="currentOrder" bean="ShoppingCart.current" />
	<dsp:getvalueof var="cartIsEmpty" value="true" vartype="Boolean" />
	<c:if test="${currentOrder != null && currentOrder.totalCommerceItemCount > 0}">
		<dsp:getvalueof var="cartIsEmpty" value="false" vartype="Boolean" />
	</c:if>

	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="enable404RedirectsOnPDP" bean="MFFEnvironment.enable404RedirectsOnPDP"/>
	<dsp:getvalueof var="giftCardProductId" bean="/atg/commerce/catalog/CatalogTools.giftCardProductId" />
	<dsp:getvalueof var="isGiftCard" value="${productId eq giftCardProductId}"/>
	<c:if test="${isGiftCard}"><c:set var="gcClass" value="gift-card" /></c:if>
	<dsp:getvalueof var="rootCategoryId" bean="PrimaryNavDroplet.rootCategoryId"/>
	<dsp:getvalueof var="searchTerm" param="Ntt"/>
	<dsp:getvalueof var="isEdit" param="edit" />
	<dsp:getvalueof var="itemId" bean="CartModifierFormHandler.editValue.commerceItemId" />
	<dsp:getvalueof var="prevQnty" bean="CartModifierFormHandler.editValue.quantity" />
	<dsp:getvalueof var="previousQnty" bean="CartModifierFormHandler.editValue.previousQnty" />
	<c:if test="${empty previousQnty}">
		<dsp:getvalueof var="previousQnty" bean="CartModifierFormHandler.quantity" />
	</c:if>

	<dsp:getvalueof var="prevSku" bean="CartModifierFormHandler.editValue.skuId" />
	<dsp:getvalueof var="currentContext" value="${contextPath}" />

	<%--
		Fix for long stacktraces when an invalid productId is passed
		Happens when sitemap is stale or users land on the PDP from an old
		search result
	--%>
	<dsp:getvalueof var="prodExists" value="false" vartype="Boolean" />
	<dsp:droplet name="ProductLookup">
		<dsp:param name="id" value="${productId}"/>
		<dsp:param name="elementName" value="product"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="product" var="product"/>
			<dsp:getvalueof var="prodExists" value="true" vartype="Boolean" />
			<dsp:getvalueof var="prdChildSkus" param="product.childSkus" />
			
			<dsp:droplet name="/com/mff/browse/droplet/IsInactiveProduct">
			<dsp:param name="productId" value="${productId}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="isInactiveProd" param="isInActiveProduct"/>
				<dsp:getvalueof var="isActiveTeaser" param="isActiveTeaser"/>
				<c:if test="${isInactiveProd && !isActiveTeaser}">
					<c:choose>
						<c:when test="${enable404RedirectsOnPDP}">
							<dsp:droplet name="GetPDPRedirectURLDroplet">
								<dsp:param name="id" value="${productId}"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="redirectURL" param="redirectURL"/>
									<dsp:droplet name="/atg/dynamo/droplet/Redirect">
										<dsp:param name="url" value="${redirectURL}"/>
										<dsp:param name="responseCode" value="301"/>
									</dsp:droplet>
								</dsp:oparam>
								<dsp:oparam name="empty">
									<dsp:droplet name="/atg/dynamo/droplet/Redirect">
										<dsp:param name="url" value="/error_404.jsp"/>
										
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
								<dsp:droplet name="/atg/dynamo/droplet/Redirect">
									<dsp:param name="url" value="/error_404.jsp"/>
									
								</dsp:droplet>
						</c:otherwise>
					</c:choose>
				</c:if>
			</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="empty">
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" value="/error_404.jsp"/>
				
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<c:choose>	
		<c:when test="${isActiveTeaser}">
			<c:set var="hideShippingClass" value="hide" />
			<c:set var="hideBopisClass" value="hide" />
			<c:set var="hideAddToCart" value=" hide " />
		</c:when>
		<c:otherwise>
			<c:set var="hideShippingClass" value="" />
			<c:set var="hideBopisClass" value="" />
			<c:set var="hideAddToCart" value="" />		
		</c:otherwise>
	</c:choose>

	<%--
		Additional check for presence of skus. This is to control the errors in the logs.
		These are most likely bots hitting expired/invalid products
	 --%>
	<c:if test="${empty prdChildSkus}">
		<c:choose>
			<c:when test="${enable404RedirectsOnPDP}">
				<dsp:droplet name="GetPDPRedirectURLDroplet">
					<dsp:param name="id" value="${productId}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="redirectURL" param="redirectURL"/>
						<dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" value="${redirectURL}"/>
							<dsp:param name="responseCode" value="301"/>
						</dsp:droplet>
					</dsp:oparam>
					<dsp:oparam name="empty">
						<dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" value="/error_404.jsp"/>
							
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
			</c:when>
			<c:otherwise>
					<dsp:droplet name="/atg/dynamo/droplet/Redirect">
						<dsp:param name="url" value="/error_404.jsp"/>
						
					</dsp:droplet>
			</c:otherwise>
		</c:choose>
	</c:if>
	<%-- Render the rest of the PDP only when we find a product & it has SKUs. Else redirect to 404 page --%>
	<c:if test="${prodExists and (!isInactiveProd || isActiveTeaser)  and not empty prdChildSkus}">
		<c:if test="${bvEnabled}">
			<dsp:include otherContext="/bv" page="/common/bv_common_script.jsp" />
			<dsp:include otherContext="/bv" page="/productDisplay/common/bv_pdp_script.jsp">
				<dsp:param name="externalId" value="${productId}" />
			</dsp:include>
			<c:set var="contextPath" value="${currentContext}" />
		</c:if>
		<dsp:param name="productItem" value="${product}"/>
		<dsp:droplet name="/atg/collections/filter/droplet/StartEndDateFilterDroplet">
			<dsp:param name="collection" param="productItem.childSkus" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="skus" param="filteredCollection"/>
			</dsp:oparam>
		</dsp:droplet>

		<dsp:test var="skuCount" value="${skus}"/>

		<%--BZ: 2427 --%>
		<dsp:getvalueof var="isFreeFreightShipment" param="productItem.childSkus[0].freeShipping"/>

		<dsp:getvalueof var="pickerType" param="productItem.pickerTemplate"/>
		<c:if test="${pickerType == 'TABLE'}">
			<c:set var="tablePickerClass" value="table-picker" scope="request" />
		</c:if>
		<dsp:getvalueof var="isFFLItem" param="productItem.ffl" />

		<%-- is order bopis only --%>
		<dsp:droplet name="/com/mff/commerce/order/purchase/IsItemRemovalRequired">
			<dsp:oparam name="output">
				<dsp:getvalueof var="bopisItemsOnly" param="bopisItemsOnly" scope="request"/>
				<dsp:getvalueof var="isItemRemovalRequired" param="isItemRemovalRequired" scope="request"/>
			</dsp:oparam>
		</dsp:droplet>

		<dsp:droplet name="ProductBrowsed">
			<dsp:param name="eventobject" param="productItem"/>
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

		<dsp:getvalueof var="brandName" param="productItem.brand"/>
		<dsp:droplet name="/com/mff/droplet/BrandUrlGeneratorDroplet">
			<dsp:param name="brandName" value="${brandName}"/>
			<dsp:oparam name="output">
				<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
					<dsp:param name="inUrl" param="url"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="brandUrl" scope="request" param="secureUrl"/>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>

		<c:if test="${bvEnabled}">
			<dsp:droplet name="BVSeoDroplet">
				<dsp:param name="productId" value="${productId}" />
				<dsp:oparam name="empty">
					<c:set var="seoReviews" value=""/>
					<c:set var="seoRatings" value=""/>
				</dsp:oparam>
				<dsp:oparam name="output">
					<dsp:getvalueof var="seoReviews" param="reviews" />
					<dsp:getvalueof var="seoRatings" param="ratings" />
				</dsp:oparam>
			</dsp:droplet>
		</c:if>

		<dsp:tomap param="productItem.parentCategory" var="parentCategory" scope="request" />
		<dsp:getvalueof var="categoryDisplayName" value="${parentCategory.displayName}" />

		<%-- Fetch SEOTags --%>
		<c:set var="defaultPageTitle" scope="request">
			<dsp:valueof param="productItem.description" valueishtml="true"/><c:if test="${not empty brandName}"> by ${brandName}</c:if> at Fleet Farm
		</c:set>
		<c:set var="defaultMetaDescription" scope="request">
			Find the<dsp:valueof param="productItem.description" valueishtml="true"/><c:if test="${not empty brandName}"> by ${brandName}</c:if> at Fleet Farm. We have low prices and a great selection on all ${categoryDisplayName}.
		</c:set>

		<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
			<dsp:param name="key" value="${productId}" />
			<dsp:param name="defaultPageTitle" value="${defaultPageTitle}" />
			<dsp:param name="defaultMetaDescription" value="${defaultMetaDescription}" />
			<dsp:param name="defaultCanonicalURL" value="${productUrl}" />
			<dsp:param name="defaultRobotsIndex" value="index" />
			<dsp:param name="defaultRobotsFollow" value="follow" />
		</dsp:include>

		<layout:default>
			<%-- display title & desc from request vars set in seoTags.jsp --%>
			<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
			<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
			<jsp:attribute name="metaKeywords"></jsp:attribute>
			<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
			<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
			<jsp:attribute name="lastModified"></jsp:attribute>
			<jsp:attribute name="section">browse</jsp:attribute>
			<jsp:attribute name="pageType">product</jsp:attribute>
			<jsp:attribute name="productId">${productId}</jsp:attribute>
			<jsp:attribute name="bodyClass">browse product ${gcClass}</jsp:attribute>

			<jsp:body>

				<%-- breadcrumbs --%>
				<section class="breadcrumbs">
					<ul aria-label="breadcrumbs" role="navigation">
						<li><a href="${contextPath}/" class="crumb">Home</a></li>
						<c:choose>
							<c:when test="${not empty param.Ntt}">
								<li><a href="/search?Ntt=${param.Ntt}" class="crumb">Search : ${param.Ntt}</a></li>
							</c:when>
							<c:when test="${not empty param.brandCrumb}">
							<%-- from brand page --%>
							<li><a href="/brand/${param.bn}/_/N-${param.brandCrumb}" class="crumb">Brand: ${brandName}</a></li>
								<c:set var="crumbs" value="${fn:split(param.bc, '|')}" />
								<c:forEach var="crumb" items="${crumbs}">
									<c:if test="${crumb ne rootCategoryId}">
										<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
											<dsp:param name="id" value="${crumb}"/>
											<dsp:param name="elementName" value="category"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="catName" param="category.displayName" />
												<dsp:droplet name="DimensionValueCacheDroplet">
													<dsp:param name="repositoryId" value="${crumb}"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="categoryCacheEntry" param="dimensionValueCacheEntry" />
													</dsp:oparam>
												</dsp:droplet>
												<c:set var="catCacheURI" value="${fn:substringBefore(categoryCacheEntry, '/_/')}" />
												<c:set var="catPath" value="${fn:substringAfter(catCacheURI, '/category')}" />
										<li><a href="/brand/${param.bn}${catPath}/_/N-${param.brandCrumb}+${categoryCacheEntry.dimvalId}" class="crumb"><dsp:valueof param="category.displayName"/></a></li>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:set var="crumbs" value="${fn:split(param.bc, '|')}" />
								<c:forEach var="crumb" items="${crumbs}">
									<c:if test="${crumb ne rootCategoryId}">
										<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
											<dsp:param name="id" value="${crumb}"/>
											<dsp:param name="elementName" value="category"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="catName" param="category.displayName" />
												<dsp:droplet name="DimensionValueCacheDroplet">
													<dsp:param name="repositoryId" value="${crumb}"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="categoryCacheEntry" param="dimensionValueCacheEntry" />
													</dsp:oparam>
												</dsp:droplet>
												<li><a href="${categoryCacheEntry.url}" class="crumb"><dsp:valueof param="category.displayName"/></a></li>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</c:forEach>
							</c:otherwise>
						</c:choose>
						<li>
							<span class="crumb active">
								<dsp:valueof param="productItem.description" valueishtml="true"/>
							</span>
						</li>
					</ul>
				</section>
				<section class="product-page" id="product-${productId}" itemscope itemtype="http://schema.org/Product">


					<%-- Include the instoreonly logic here --%>
					
					
					<%-- in store only --%>
						<dsp:droplet name="Switch">
							<dsp:param name="value" param="productItem.fulfillmentMethod" />
							<dsp:getvalueof var="bopisOnly" value="false" vartype="Boolean" />
							<dsp:getvalueof var="edsPPSOnly" value="false" vartype="Boolean" />
							<dsp:getvalueof var="inStoreOnly" value="false" vartype="Boolean" />
							<dsp:getvalueof var="displayEDSMessage" value="false" vartype="Boolean" />
							<dsp:getvalueof var="fulfillmentMethod" param="productItem.fulfillmentMethod" />
							<%-- 3: EDS + BOPIS --%>
							<dsp:oparam name="3">
								<dsp:getvalueof var="displayEDSMessage" value="true" vartype="Boolean" />
							</dsp:oparam>
							<%-- 5: In-Store Only --%>
							<dsp:oparam name="5">
								<dsp:getvalueof var="inStoreOnly" value="true" vartype="Boolean" />
							</dsp:oparam>
							<%-- 7: BOPIS Only (no ship-to-home) --%>
							<dsp:oparam name="7">
								<dsp:getvalueof var="bopisOnly" value="true" vartype="Boolean" />
							</dsp:oparam>
							<%-- 8: EDS-PPS Only --%>
							<dsp:oparam name="8">
								<dsp:getvalueof var="edsPPSOnly" value="true" vartype="Boolean" />
								<dsp:getvalueof var="displayEDSMessage" value="true" vartype="Boolean" />
							</dsp:oparam>
						</dsp:droplet>
					
					
					<%-- product details --%>
					<div class="product-details">
						<c:if test="${not empty brandName}">
							<div class="product-brand">
								<a href="${brandUrl}">
									<span itemprop="brand"><dsp:valueof param="productItem.brand" valueishtml="true"/></span>
									<span class="orange">&gt;</span>
								</a>
							</div>
						</c:if>

						<h1 class="product-name" itemprop="name">
							<dsp:getvalueof param="productItem.description" var="productName"/>
							<dsp:getvalueof param="productItem.brand" var="productBrand"/>
							<dsp:getvalueof var="productNameWithoutBrand" value="${fn:substringAfter(productName,productBrand)}" />
							${productNameWithoutBrand}
						</h1>
						<c:if test="${bvEnabled}">
							<div class="product-reviews" itemprop="aggregateRating" itemscope itemtype="http://schema.org/AggregateRating">
								<dsp:include otherContext="/bv" page="/productDisplay/ratings/bv_ratings_container.jsp">
									<dsp:param name="seoRatings" value="${seoRatings}"/>
								</dsp:include>
							</div>
							<c:set var="contextPath" value="${currentContext}" />
						</c:if>

						<div class="product-number">
							<span class="label">Product #:</span>
							<span itemprop="productID"><c:out value="${productId}"/></span>
						</div>
						<dsp:getvalueof var="skuId" param="productItem.childSKUs[0].id" scope="request" />
						<dsp:getvalueof var="modelNumber" param="productItem.childSKUs[0].vpn" scope="request" />
						<dsp:getvalueof var="defaultSKU" value="${skuId}" scope="request" />
						<dsp:droplet name="/com/mff/droplet/MFFInventoryDroplet">
							<dsp:param name="prodId" value="${productId}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof param="element" var="itemOutOfStock"/>
								<dsp:getvalueof param="bopisOnlyAvailable" var="bopisOnlyAvailable"/>
								<dsp:getvalueof param="stockLevelForSkusOfProduct" var="stockLevelForSkusOfProduct"/>
							</dsp:oparam>
						</dsp:droplet>
						<dsp:droplet name="IsEmpty">
							<dsp:param name="value" param="productItem.childSKUs[0].dynamicAttributes"/>
							<dsp:oparam name="false">
								<c:set var="skuId" value="" scope="request" />
								<c:set var="modelNumber" value="" scope="request" />
								<c:set var="productSkuClass" value="hide" scope="request" />
							</dsp:oparam>
						</dsp:droplet>
						<c:if test="${skuCount.size gt 1}">
							<c:set var="skuId" value="" scope="request" />
							<c:set var="productSkuClass" value="hide" scope="request" />
							<c:set var="modelNumber" value="" scope="request" />
						</c:if>
						<div class="product-sku ${productSkuClass}">
							<span class="pipe">|</span>
							<span class="label">SKU:</span>
							<span class="sku-number" itemprop="sku">${skuId}</span>
						</div>
						<c:if test="${empty modelNumber}">
							<c:set var="modelNumberClass" value="hide" scope="request" />
						</c:if>
						<div class="product-sku-model ${modelNumberClass}">
							<span class="pipe">|</span>
							<span class="label">Model #:</span>
							<span class="model-number" >${modelNumber}</span>
						</div>
						<c:if test="${!isGiftCard}">
							
								<c:choose>
										<c:when test="${isActiveTeaser}">
											<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
												<dsp:param name="value" param="productItem.teaserPDPMessage"/>
												<dsp:oparam name="true">
													<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
		  												<dsp:param name="queryRQL" value="infoKey=\"EVENT_ITEM_PDP\""/>
		  												<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
		  												<dsp:param name="itemDescriptor" value="infoMessage"/>
		  												<dsp:oparam name="output">
															<div class="regular-price">
																<span itemprop="price" content="content">
																	<dsp:valueof param="element.infoMsg" valueishtml="true"/>
																</span>
															</div>
		  												</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
												<dsp:oparam name="false">
														<div class="regular-price">
															<span itemprop="price" content="content">
																<dsp:valueof param="productItem.teaserPDPMessage" valueishtml="true"/>
															</span>
														</div>
												</dsp:oparam>												
											</dsp:droplet>
										</c:when>
									<c:otherwise>
									 	<c:if test="${!isInactiveProd}">
											<div class="product-price price" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
												<c:choose>
													<c:when test="${(itemOutOfStock or inStoreOnly or bopisOnly)}">
														<link itemprop="availability" href="http://schema.org/OutOfStock" />
													</c:when>
													<c:otherwise>
														<link itemprop="availability" href="http://schema.org/InStock" />
														<link itemprop="availableDeliveryMethod" href="http://schema.org/ParcelService" />
														<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction" />
													</c:otherwise>
												</c:choose>
												<link itemprop="itemCondition" href="http://schema.org/NewCondition" />
												<meta itemprop="priceCurrency" content="USD" />
												<dsp:include page="includes/productPrice.jsp">
													<dsp:param name="productItem" param="productItem"/>
													<dsp:param name="skus" value="${skus}"/>
													<dsp:param name="showClearanceModal" value="true"/>
												</dsp:include>
											</div>
										</c:if>
									</c:otherwise>
								</c:choose>
						</c:if>

						<%-- minimum age --%>
						<dsp:droplet name="Switch">
							<dsp:param name="value" param="productItem.minimumAge"/>
							<dsp:oparam name="18">
								<p class="product-message">
									<span class="icon icon-error"></span> You must be at least 18 years old to purchase this item.
								</p>
							</dsp:oparam>
							<dsp:oparam name="21">
								<p class="product-message">
									<span class="icon icon-error"></span> You must be at least 21 years old to purchase this item
								</p>
							</dsp:oparam>
						</dsp:droplet>

						<%-- in store only --%>
						<dsp:droplet name="Switch">
							<dsp:param name="value" param="productItem.fulfillmentMethod" />
							<dsp:getvalueof var="bopisOnly" value="false" vartype="Boolean" />
							<dsp:getvalueof var="edsPPSOnly" value="false" vartype="Boolean" />
							<dsp:getvalueof var="inStoreOnly" value="false" vartype="Boolean" />
							<dsp:getvalueof var="displayEDSMessage" value="false" vartype="Boolean" />
							<dsp:getvalueof var="fulfillmentMethod" param="productItem.fulfillmentMethod" />
							<%-- 3: EDS + BOPIS --%>
							<dsp:oparam name="3">
								<dsp:getvalueof var="displayEDSMessage" value="true" vartype="Boolean" />
							</dsp:oparam>
							<%-- 5: In-Store Only --%>
							<dsp:oparam name="5">
								<dsp:getvalueof var="inStoreOnly" value="true" vartype="Boolean" />
								<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
									<dsp:param name="inUrl" value="${contextPath}/sitewide/storeLocator.jsp"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="storeLocatorUrl" scope="request" param="secureUrl"/>
										<p class="product-info alert-box callout text-left">
											<span class="orange">Available for Purchase in Store Only</span><br/>
											<span style="font-size:12px">Price and availability may vary by location</span>
											<dsp:a href="${storeLocatorUrl}" iclass="product-in-store">Find a Store</dsp:a>
										</p>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
							<%-- 7: BOPIS Only (no ship-to-home) --%>
							<dsp:oparam name="7">
								<dsp:getvalueof var="bopisOnly" value="true" vartype="Boolean" />
							</dsp:oparam>
							<%-- 8: EDS-PPS Only --%>
							<dsp:oparam name="8">
								<dsp:getvalueof var="edsPPSOnly" value="true" vartype="Boolean" />
								<dsp:getvalueof var="displayEDSMessage" value="true" vartype="Boolean" />
							</dsp:oparam>
						</dsp:droplet>

						<dsp:getvalueof var="edsMessageHeadline" value="This item ships directly from our supplier"/>
						<dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
							<dsp:param name="queryRQL" value="contentKey=:contentKey AND displayName=:displayName"/>
							<dsp:param name="contentKey" value="4444"/>
							<dsp:param name="displayName" value="ShipToHomeMessage"/>
							<dsp:param name="howMany" value="1"/>
							<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
							<dsp:param name="itemDescriptor" value="mffStaticContent"/>
							<dsp:param name="elementName" value="contentItem"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="edsMessageHeadline" param="contentItem.contentSections[0].headline"/>
								<div id="eds-info-modal" class="hide" aria-hidden="true">
									<div class="modal-header">
										<h2>Supplier Information</h2>
									</div>
									<div class="modal-body">
										<dsp:valueof param="contentItem.contentSections[0].body" valueishtml="true"/>
									</div>
								</div>
							</dsp:oparam>
						</dsp:droplet>

						<%-- rendered on the page for informational purposes only --%>
						<input type="hidden" name="bopisOnly" value="${bopisOnly}" />
						<input type="hidden" name="edsPPSOnly" value="${edsPPSOnly}" />
						<input type="hidden" name="inStoreOnly" value="${inStoreOnly}" />
						<input type="hidden" name="fulfillmentMethod" value="${fulfillmentMethod}" />

						<%-- ffl --%>
						<c:if test="${isFFLItem}">
							<%-- minimum age --%>
							<%--
							The following IsEmpty droplet covers the case where the minimumAge parameter is not
							set, but the item is an FFL item. This should never happen, but just in case...

							According to the Adult Signature Required Document...
							R-5: "When the FFL message displays on the PDP, either one of the following two messages
							must also display on the PDP (depending on how the item is flagged for adult signature):
								-	You must be at least 18 years old to purchase this item.
								-	You must be at least 21 years old to purchase this item.
							R-1: "If a product is flagged as FFL, but Signature Required field is neither SR18
										or ASR21, the item will default to FFL and ASR21.
							--%>
							<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
								<dsp:param name="value" param="productItem.minimumAge"/>
								<dsp:oparam name="true">
									<p class="product-message">
										<span class="icon icon-error"></span> You must be at least 21 years old to purchase this item.
									</p>
								</dsp:oparam>
							</dsp:droplet>
							<c:if test="${not inStoreOnly}">
								<div class="product-info">
									This item requires an FFL shipment address.
									<a href="${contextPath}/static/faq-purchasing-firearms-online">Click here for more information.</a>
								</div>
							</c:if>
						</c:if>

						<%-- BZ-2666 --%>
						<c:if test="${pickerType == 'TABLE'}">
							<div class="product-table-picker-select">
								<a href="#${tablePickerClass}" class="button primary">select item model</a>
							</div>

							<dsp:droplet name="IsEmpty">
								<dsp:param name="value" param="productItem.splMsg"/>
								<dsp:oparam name="false">
									<div class="product-special-msg-info">
										<div class="card">
											<div class="card-title">
												<span class="icon icon-info"></span>
												<dsp:valueof param="productItem.splMsgTitle" valueishtml="true"/>
											</div>
											<div class="card-content">
												<p><dsp:valueof param="productItem.splMsg" valueishtml="true"/></p>
											</div>
										</div>
									</div>
								</dsp:oparam>
							</dsp:droplet>
						</c:if>

					</div>

					<%-- product images --%>
					<div class="product-images-social">

						<%-- image viewer --%>
						<dsp:include page="includes/productImages.jsp">
							<dsp:param name="productId" value="${productId}"/>
							<dsp:param name="productName" param="productItem.description"/>
							<dsp:param name="productVideos" param="productItem.videos"/>
						</dsp:include>

						<%-- social sharing --%>
						<dsp:include page="includes/socialShare.jsp" />

					</div>
					
					
					<div class="product-pickers ${tablePickerClass}" id="${tablePickerClass}">
						<c:if test="${!isInactiveProd}">
						<dsp:form formid="add-to-cart-form" id="add-to-cart-form" name="add-to-cart-form" class="add-to-cart-form" action="product.jsp" method="post">

							<%-- gift card amount --%>
							<c:if test="${isGiftCard}">
								<div class="gift-card-price">
									<label for="gift-card-amount">Select a value:</label>
									<div class="gift-card-buttons">
										<button class="button secondary gift-card-button" data-amount="10">$10</button>
										<button class="button secondary gift-card-button" data-amount="25">$25</button>
										<button class="button secondary gift-card-button" data-amount="50">$50</button>
										<button class="button secondary gift-card-button" data-amount="100">$100</button>
									</div>
									<label for="gift-card-amount">or enter amount:</label>
									<div class="gift-card-input">
										<span class="gift-card-sign">$</span><dsp:input type="tel" bean="CartModifierFormHandler.value.giftCardDenomination" id="gift-card-amount" iclass="gift-card-amount" />
									</div>
									<div class="gift-card-note">(Value from $2.00 - $500.00)</div>
								</div>
							</c:if>

							<c:if test="${pickerType ne 'TABLE' and itemOutOfStock and not inStoreOnly and not bopisOnlyAvailable}">
								<div class="alert-box error">
									Currently out of stock
								</div>
							</c:if>

							<%-- quantity --%>
							<c:if test="${pickerType ne 'TABLE' and not inStoreOnly}">
								<dsp:getvalueof var="maxQty" bean="MFFEnvironment.maxQtyPerItemInOrder" />
								<div class="product-qty">
									<label for="quantity">Quantity</label>
									<div class="change-quantity" data-min="1" data-max="${maxQty}" data-quantify>
										<div class="quantity-group">
											<div class="minus-icon inactive">
												<span class="icon icon-minus"></span>
											</div>
											<div class="current-quantity">
												<c:choose>
													<c:when test="${isEdit}">
														<dsp:input bean="CartModifierFormHandler.quantity" type="text" id="quantity" priority="9" class="counter" value="${prevQnty}" />
													</c:when>
													<c:otherwise>
														<dsp:input bean="CartModifierFormHandler.quantity" type="text" id="quantity" priority="9" class="counter" value="1" />
													</c:otherwise>
												</c:choose>
											</div>
											<div class="plus-icon">
												<span class="icon icon-plus"></span>
											</div>
										</div>
									</div>
								</div>
							</c:if>

							<%-- should shipping or bopis radio be checked? --%>
							<c:set var="shippingChecked" value="checked=''" scope="request" />
							<c:set var="bopisChecked" value="" scope="request" />
							<c:set var="hideShippingClass" value="" scope="request" />
							<c:set var="hideBopisClass" value="hide" scope="request" />
							
							<c:if test="${itemOutOfStock and bopisOnlyAvailable}">
								<c:set var="bopisChecked" value="checked=''" scope="request" />
							</c:if>
							
							<c:if test="${not isFFLItem and (bopisOrder or bopisOnly)}">
								<c:set var="shippingChecked" value="" scope="request" />
								<c:set var="bopisChecked" value="checked=''" scope="request" />
								<c:set var="hideShippingClass" value="hide" scope="request" />
								<c:set var="hideBopisClass" value="" scope="request" />
							</c:if>
							<c:if test="${not empty param.gStoreId}">
								<c:set var="shippingChecked" value="" scope="request" />
								<c:set var="bopisChecked" value="checked=''" scope="request" />
								<c:set var="hideShippingClass" value="" scope="request" />
								<c:set var="hideBopisClass" value="" scope="request" />
							</c:if>

							<%-- inventory check --%>
							<c:if test="${bopisOrder}">
								<!-- BOPIS inventory check -->
								<c:set var="bopisAvail" value="true"/>
								<dsp:droplet name="/com/mff/commerce/order/MFFStoreInventoryForSku">
									<dsp:param name="skuid" value="${skuId}"/>
									<dsp:param name="storeid" value="${bopisStore}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof param="elements" var="availability"/>
										<%-- Iterate items --%>
										<c:forEach items="${availability}" var="storeAvail" varStatus="storeAvailIndex">
											<c:if test="${storeAvail.storeStockLevel le 0}">
												<c:set var="bopisAvail" value="false"/>
											</c:if>
										</c:forEach>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>

							<%-- pickers generated dynamically --%>
							<div class="product-form-pickers">
								<c:choose>
									<c:when test="${pickerType == 'TABLE'}">
										<dsp:input bean="CartModifierFormHandler.prodType" name="tablePicker" id="tablePicker" value="table" type="hidden" priority="9" />
										<dsp:include page="includes/tablePicker.jsp">
											<dsp:param name="product" param="productItem"/>
											<dsp:param name="skus" value="${skus}"/>
											<dsp:param name="stockLevelForSkusOfProduct" value="${stockLevelForSkusOfProduct}"/>
											<dsp:param name="isEdit" value="${isEdit}" />
											<dsp:param name="searchSKU" value="${searchTerm}" />
											<dsp:param name="bopisAvail" value="${bopisAvail}" />
											<dsp:param name="inStoreOnly" value="${inStoreOnly}" />
											<dsp:param name="bopisOnly" value="${bopisOnly}" />
											<dsp:param name="cartIsEmpty" value="${cartIsEmpty}" />
											<dsp:param name="prevQnty" value="${prevQnty}" />
											<dsp:param name="prevSku" value="${prevSku}" />
											<dsp:param name="itemOutOfStock" value="${itemOutOfStock}" />
											<dsp:param name="bopisOnlyAvailable" value="${bopisOnlyAvailable}" />
											<dsp:param name="edsPPSOnly" value="${edsPPSOnly}" />
											<dsp:param name="displayEDSMessage" value="${displayEDSMessage}" />
											<dsp:param name="edsMessageHeadline" value="${edsMessageHeadline}" />
											<dsp:param name="bopisItemsOnly" value="${bopisItemsOnly}" />
											<dsp:param name="isItemRemovalRequired" value="${isItemRemovalRequired}" />
											<dsp:param name="stockLevelForSkusOfProduct" value="${stockLevelForSkusOfProduct}" />
											<dsp:param name="isActiveTeaser" value="${isActiveTeaser}" />
										</dsp:include>
									</c:when>
									<c:otherwise>
										<dsp:input bean="CartModifierFormHandler.productId" name="productId" id="productId" value="${productId}" priority="9" type="hidden" />
										<dsp:input bean="CartModifierFormHandler.catalogRefIds" name="catalogRefIds" id="catalogRefIds" iclass="input-selected-sku" value="${skuId}" priority="9" type="hidden" />
									</c:otherwise>
								</c:choose>
							</div>

							<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" name="addItemToOrderErrorURL" value="${contextPath}/checkout/json/cartError.jsp" type="hidden" />
							<dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" name="addItemToOrderSuccessURL" value="${contextPath}/checkout/json/cartSuccess.jsp" type="hidden" />
							<dsp:input bean="CartModifierFormHandler.addFromWishList" value="${addFromWishList}" type="hidden"/>

							<c:if test="${pickerType ne 'TABLE'}">
								<div class="add-to-cart-actions">

									<%-- Ship To Home / BOPIS radio buttons --%>
									<c:if test="${not ((itemOutOfStock and !bopisOnlyAvailable) or inStoreOnly)}">
										<c:choose>
											<c:when test="${bopisOnly or (itemOutOfStock and bopisOnlyAvailable)}">
												<div class="radio radio-primary ship-to-home ${hideShippingClass}">
													<input type="radio" value="shipping-order" id="shipping-order" name="order-type" disabled />
													<label for="shipping-order">
														Ship To Home
														<span class="in-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
													</label>
												</div>
											</c:when>
											<c:otherwise>
												<div class="radio radio-primary ship-to-home ${hideShippingClass}">
													<input type="radio" value="shipping-order" id="shipping-order" name="order-type" ${shippingChecked} />
													<label for="shipping-order">
														Ship To Home
														<span class="in-store-available">Available<span class="icon icon-available"></span></span>
													</label>
													<c:if test="${displayEDSMessage}">
														<p class="eds-message">
															<span><c:out value="${edsMessageHeadline}"/></span>
															<a href="#" class="reveal-eds-modal">
																<span class="icon icon-info"></span>
															</a>
														</p>
													</c:if>
												</div>
											</c:otherwise>
										</c:choose>
										<%-- gift cards are not available for BOPIS --%>
										<%-- ffl items cannot be BOPIS --%>
										<%-- 2402 - During edit flow, do not let user change the fulfillment method --%>
										<%--BZ: 2427 Do not show bopis option if item qualifies for eds-pps-only items --%>
										<c:if test="${not isGiftCard and not isFFLItem and not (isEdit and !bopisOrder) and not edsPPSOnly}">
											<div class="radio radio-primary ${hideBopisClass}">
												<input type="radio" value="bopis-order" id="bopis-order" class="bopis-order" name="order-type" ${bopisChecked} />
												<label for="bopis-order">
													Pick Up In Store 
												</label>
											</div>
										</c:if>
										<c:if test="${edsPPSOnly and !isEdit}">
											<div class="radio radio-primary ${hideBopisClass}">
												<input type="radio" value="bopis-order" id="bopis-order" class="bopis-order" name="order-type" disabled />
												<label for="bopis-order">
													Pick Up In Store 
													<span class="in-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
												</label>
											</div>
										</c:if>
										<c:choose>
											<c:when test="${isEdit}">
												<c:if test="${pickerType == 'TABLE'}">
													<dsp:input type="hidden" id="add-to-cart-submit" bean="CartModifierFormHandler.removeAndAddItemToOrder" priority="-21" value="UpdateCart" />
													<dsp:input bean="CartModifierFormHandler.addActionType" name="addActionType" id="addActionType" value="editTablePicker" type="hidden" />
													<dsp:input bean="CartModifierFormHandler.editMode" id="update-item-edit-mode" name="update-item-edit-mode" value="true" type="hidden" />
													<dsp:input type="hidden" bean="CartModifierFormHandler.removeAndAddItemToOrderSuccessURL" id="successUrl" value="${contextPath}/checkout/json/cartEditSuccess.jsp" />
													<dsp:input type="hidden" bean="CartModifierFormHandler.removeAndAddItemToOrderErrorURL" id="errorUrl" value="${contextPath}/checkout/json/cartEditError.jsp" />
												</c:if>
											</c:when>
											<c:otherwise>
												<dsp:input type="hidden" id="add-to-cart-submit" bean="CartModifierFormHandler.addItemToOrder" priority="-21" value="Add to cart" />
											</c:otherwise>
										</c:choose>
									</c:if>

									<c:choose>
										<c:when test="${bopisOrder and not bopisAvail and not inStoreOnly and not (itemOutOfStock and !bopisOnlyAvailable)}">
											<c:choose>
												<c:when test="${isGiftCard}">
													<span class="error">
														Gift cards are not available for pickup at store. Please
														<a href="#" class="ship-my-order">Ship Your Order Instead</a> if you'd like to
														add a Gift Card to your order.
													</span>
												</c:when>
												<c:when test="${isFFLItem}">
													<span class="error">
														FFL items are not available for pickup at store. Please
														<a href="#" class="ship-my-order">Ship Your Order Instead</a> if you'd like to
														add an FFL item to your order.
													</span>
												</c:when>
												<c:otherwise>
													<c:if test="${empty bopisStore}">
														<dsp:getvalueof var="bopisStore" bean="Profile.myHomeStore.locationId" scope="request"/>
													</c:if>
													<div class="bopis-location-info">
														<div class="bopis-section ${hideBopisClass}">
															<div class="bopis-store-content" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
																<c:choose>
																	<c:when test="${not empty salePrice}">
																		<meta itemprop="price" content="${salePrice}"/>
																	</c:when>
																	<c:otherwise>
																		<meta itemprop="price" content="${listPrice}"/>
																	</c:otherwise>
																</c:choose>
																<meta itemprop="priceCurrency" content="USD"/>
																<link itemprop="itemCondition" href="http://schema.org/NewCondition"/>
																<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
																	<dsp:param name="id" value="${bopisStore}"/>
																	<dsp:param name="elementName" value="store"/>
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="postalCode" param="store.postalCode" scope="request"/>
																		<dsp:droplet name="BopisStoreAvailabilityDroplet">
																			<dsp:param name="productId" param="productItem.id"/>
																			<dsp:param name="skuId" value="${skuId}"/>
																			<dsp:param name="quantity" value="1"/>
																			<dsp:param name="storeId" value="${bopisStore}"/>
																			<dsp:oparam name="true">
																				<c:set var="isAvailable" value="true" scope="request" />
																				<link itemprop="availability" href="http://schema.org/InStock"/>
																				<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																				<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																				<c:if test="${empty param.gStoreId}">
																					<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																						<meta itemprop="name" content="Fleet Farm"/>
																						<meta itemprop="branchCode" content="${bopisStore}"/>
																					</span>
																				</c:if>
																				<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																					<meta itemprop="value" content="0"/>
																				</span>
																				<span class="bopis-store-available ${hideBopisClass}">Available<span class="icon icon-available"></span></span>
																				<span class="${hideBopisClass}">at</span>
																				<a class="bopis-store-info update-bopis-store underlined-link ${hideBopisClass}" href="#">
																					<dsp:valueof param="store.city"/>,
																					<dsp:valueof param="store.stateAddress"/>
																				</a>
																			</dsp:oparam>
																			<dsp:oparam name="false">
																				<c:set var="isAvailable" value="false" scope="request" />
																				<link itemprop="availability" href="http://schema.org/OutOfStock"/>
																				<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																				<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																				<c:if test="${empty param.gStoreId}">
																					<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																						<meta itemprop="name" content="Fleet Farm"/>
																						<meta itemprop="branchCode" content="${bopisStore}"/>
																					</span>
																				</c:if>
																				<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																					<meta itemprop="value" content="0"/>
																				</span>
																				<span class="bopis-store-unavailable ${hideBopisClass}">Not Available<span class="icon icon-unavailable ${hideBopisClass}"></span></span>
																				<span class="${hideBopisClass}">at</span>
																				<a class="bopis-store-info update-bopis-store underlined-link ${hideBopisClass}" href="#">
																					<dsp:valueof param="store.city"/>,
																					<dsp:valueof param="store.stateAddress"/>
																				</a>
																			</dsp:oparam>
																			<dsp:oparam name="error">
																				in error
																			</dsp:oparam>
																		</dsp:droplet>
																		<%-- 2402 - Hide these links during the edit flow. --%>
																		<c:if test="${not (isEdit and bopisOrder)}">
																			<div class="card-links ${hideBopisClass}">
																				<a href="#" class="disabled change-store">Change Store</a>
																				<c:if test="${not (bopisItemsOnly and bopisOnly)}">
																					 <c:choose>
																						 <c:when test="${isItemRemovalRequired}">
																							 <span class="seperator">&nbsp;|&nbsp;</span>
																							 <a href="/checkout/ajax/autoRemoveItemModal.jsp" class="auto-remove-item-trigger modal-trigger" data-target="auto-remove-item-modal" data-size="small">Ship My Order Instead</a>
																						 </c:when>
																						 <c:when test="${not bopisOnly and not cartIsEmpty}">
																							 <span class="seperator">&nbsp;|&nbsp;</span>
																							 <a href="#" class="disabled ship-my-order">Ship My Order Instead</a>
																						</c:when>
																					</c:choose>
																				</c:if>
																			</div>
																		</c:if>
																	</dsp:oparam>
																</dsp:droplet>
															</div>
														</div>
													</div>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
											<c:set var="isAvailable" value="true" scope="request" />
											
											<c:if test="${not ((itemOutOfStock and !bopisOnlyAvailable) or inStoreOnly)}">
												<div class="bopis-location-info ${hideBopisClass}">
													<c:choose>
														<c:when test="${bopisOrder and not inStoreOnly}">
															<c:if test="${empty bopisStore}">
																<dsp:getvalueof var="bopisStore" bean="Profile.myHomeStore.locationId" scope="request"/>
															</c:if>
															<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
																<dsp:param name="id" value="${bopisStore}"/>
																<dsp:param name="elementName" value="store"/>
																<dsp:oparam name="output">
																	<dsp:getvalueof var="postalCode" param="store.postalCode" scope="request"/>
																	<div class="bopis-section ${hideBopisClass}">
																		<c:choose>
																			<c:when test="${not edsPPSOnly}">
																				<div class="bopis-store-content" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
																					<c:choose>
																						<c:when test="${not empty salePrice}">
																							<meta itemprop="price" content="${salePrice}"/>
																						</c:when>
																						<c:otherwise>
																							<meta itemprop="price" content="${listPrice}"/>
																						</c:otherwise>
																					</c:choose>
																					<meta itemprop="priceCurrency" content="USD"/>
																					<link itemprop="itemCondition" href="http://schema.org/NewCondition"/>
																					<dsp:droplet name="BopisStoreAvailabilityDroplet">
																						<dsp:param name="productId" param="productItem.id"/>
																						<dsp:param name="skuId" value="${skuId}"/>
																						<dsp:param name="quantity" value="1"/>
																						<dsp:param name="storeId" value="${bopisStore}"/>
																						<dsp:oparam name="true">
																							<c:set var="isAvailable" value="true" scope="request" />
																							<link itemprop="availability" href="http://schema.org/InStock"/>
																							<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																							<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																							<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																								<meta itemprop="branchCode" content="${bopisStore}"/>
																							</span>
																							<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																								<meta itemprop="value" content="0"/>
																							</span>
																							<span class="bopis-store-available">Available<span class="icon icon-available"></span></span>
																							<span>at</span>
																							<a class="bopis-store-info update-bopis-store underlined-link" href="#">
																								<dsp:valueof param="store.city"/>,
																								<dsp:valueof param="store.stateAddress"/>
																							</a>
																						</dsp:oparam>
																						<dsp:oparam name="false">
																							<c:set var="isAvailable" value="false" scope="request" />
																							<link itemprop="availability" href="http://schema.org/OutOfStock"/>
																							<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																							<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																							<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																								<meta itemprop="name" content="Fleet Farm"/>
																								<meta itemprop="branchCode" content="${bopisStore}"/>
																							</span>
																							<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																								<meta itemprop="value" content="0"/>
																							</span>
																							<span class="bopis-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
																							<span>at</span>
																							<a class="bopis-store-info update-bopis-store underlined-link" href="#">
																								<dsp:valueof param="store.city"/>,
																								<dsp:valueof param="store.stateAddress"/>
																							</a>
																						</dsp:oparam>
																						<dsp:oparam name="error">
																							in error
																						</dsp:oparam>
																					</dsp:droplet>
																				</div>
																			</c:when>
																			<c:otherwise>
																				<c:set var="displaySplitOrderMsg" value="true" />
																			</c:otherwise>
																		</c:choose>
																		<%-- 2402 - Hide these links during the edit flow. --%>
																		<c:if test="${not (isEdit and bopisOrder)}">
																			<div class="card-links ${hideBopisClass}">
																				<c:if test="${not edsPPSOnly}">
																					<a href="#" class="disabled change-store">Change Store</a>
																				</c:if>
																				<c:if test="${not (bopisItemsOnly and bopisOnly)}">
																					<%--
																						Requested in 2505. Show ship my order modal consistently
																						on PDP, cart & checkout. Changes to this section should also be made
																						on cart & checkout
																					--%>
																					<c:choose>
																						<c:when test="${isItemRemovalRequired}">
																							<c:if test="${not edsPPSOnly}">
																								<span class="seperator">&nbsp;|&nbsp;</span>
																							</c:if>
																							<a href="/checkout/ajax/autoRemoveItemModal.jsp" class="auto-remove-item-trigger modal-trigger" data-target="auto-remove-item-modal" data-size="small">Ship My Order Instead</a>
																						</c:when>
																						<c:otherwise>
																							<c:if test="${not cartIsEmpty}">
																								<c:if test="${not edsPPSOnly}">
																									<span class="seperator">&nbsp;|&nbsp;</span>
																								</c:if>
																								<a href="#" class="disabled ship-my-order">Ship My Order Instead</a>
																							</c:if>
																						</c:otherwise>
																					</c:choose>
																				</c:if>
																			</div>
																		</c:if>
																	</div>
																</dsp:oparam>
															</dsp:droplet>
														</c:when>
														<c:when test="${not empty homeStore and not inStoreOnly and not edsPPSOnly}">
															<dsp:getvalueof var="postalCode" bean="Profile.myHomeStore.postalCode" scope="request"/>
															<dsp:getvalueof var="city" bean="Profile.myHomeStore.city" scope="request"/>
															<dsp:getvalueof var="state" bean="Profile.myHomeStore.stateAddress" scope="request"/>
															<dsp:getvalueof var="bopisStore" bean="Profile.myHomeStore.locationId" scope="request"/>
															<div class="bopis-section ${hideBopisClass}">
																<div class="bopis-store-content" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
																	<c:choose>
																		<c:when test="${not empty salePrice}">
																			<meta itemprop="price" content="${salePrice}"/>
																		</c:when>
																		<c:otherwise>
																			<meta itemprop="price" content="${listPrice}"/>
																		</c:otherwise>
																	</c:choose>
																	<meta itemprop="priceCurrency" content="USD"/>
																	<link itemprop="itemCondition" href="http://schema.org/NewCondition"/>
																	<dsp:droplet name="BopisStoreAvailabilityDroplet">
																		<dsp:param name="productId" param="productItem.id"/>
																		<dsp:param name="skuId" value="${skuId}"/>
																		<dsp:param name="quantity" value="1"/>
																		<dsp:param name="storeId" value="${bopisStore}"/>
																		<dsp:oparam name="true">
																			<c:set var="isAvailable" value="true" scope="request" />
																			<link itemprop="availability" href="http://schema.org/InStock"/>
																			<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																			<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																			<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																				<meta itemprop="name" content="Fleet Farm"/>
																				<meta itemprop="branchCode" content="${bopisStore}"/>
																			</span>
																			<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																				<meta itemprop="value" content="0"/>
																			</span>
																			<span class="bopis-store-available">Available<span class="icon icon-available"></span></span>
																			<span>at</span>
																			<a class="bopis-store-info update-bopis-store underlined-link" href="#">
																				${city}, ${state}
																			</a>
																		</dsp:oparam>
																		<dsp:oparam name="false">
																			<c:set var="isAvailable" value="false" scope="request" />
																			<link itemprop="availability" href="http://schema.org/OutOfStock"/>
																			<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																			<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																			<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																				<meta itemprop="name" content="Fleet Farm"/>
																				<meta itemprop="branchCode" content="${bopisStore}"/>
																			</span>
																			<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																				<meta itemprop="value" content="0"/>
																			</span>
																			<span class="bopis-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
																			<span>at</span>
																			<a class="bopis-store-info update-bopis-store underlined-link" href="#">
																				${city}, ${state}
																			</a>
																		</dsp:oparam>
																		<dsp:oparam name="error">
																			in error
																		</dsp:oparam>
																	</dsp:droplet>
																</div>
																<%-- 2402 - Hide these links during the edit flow. --%>
																<c:if test="${not (isEdit and bopisOrder)}">
																	<div class="card-links ${hideBopisClass}">
																		<c:if test="${not edsPPSOnly}">
																			<a href="#" class="disabled change-store">Change Store</a>
																		</c:if>
																		<c:if test="${not (bopisItemsOnly and bopisOnly)}">
																			<%--
																				Requested in 2505. Show ship my order modal consistently
																				on PDP, cart & checkout. Changes to this section should also be made
																				on cart & checkout
																			--%>
																			<c:choose>
																				<c:when test="${isItemRemovalRequired}">
																					<c:if test="${not edsPPSOnly}">
																						<span class="seperator">&nbsp;|&nbsp;</span>
																					</c:if>
																					<a href="/checkout/ajax/autoRemoveItemModal.jsp" class="auto-remove-item-trigger modal-trigger" data-target="auto-remove-item-modal" data-size="small">Ship My Order Instead</a>
																				</c:when>
																				<c:otherwise>
																					<c:if test="${not bopisOnly and not cartIsEmpty}">
																						<c:if test="${not edsPPSOnly}">
																							<span class="seperator">&nbsp;|&nbsp;</span>
																						</c:if>
																						<a href="#" class="disabled ship-my-order">Ship My Order Instead</a>
																					</c:if>
																				</c:otherwise>
																			</c:choose>
																		</c:if>
																	</div>
																</c:if>
															</div>
														</c:when>
														<c:when test="${empty bopisStore and not inStoreOnly and not edsPPSOnly}">
															<c:choose>
																<c:when test="${empty param.gStoreId}">
																<c:if test="${!isGiftCard}">
																	<div class="bopis-section no-store ${hideBopisClass}">
																		<a href="#" class="select-store">Select Store</a>
																		to see item availability
																	</div>
																</c:if>	
																</c:when>
																<c:otherwise>
																	<c:if test="${empty bopisStore}">
																		<dsp:getvalueof var="bopisStore" value="${param.gStoreId}" scope="request"/>
																	</c:if>
																	<div class="bopis-location-info">
																		<div class="bopis-section ${hideBopisClass}">
																			<div class="bopis-store-content" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
																				<c:choose>
																					<c:when test="${not empty salePrice}">
																						<meta itemprop="price" content="${salePrice}"/>
																					</c:when>
																					<c:otherwise>
																						<meta itemprop="price" content="${listPrice}"/>
																					</c:otherwise>
																				</c:choose>
																				<meta itemprop="priceCurrency" content="USD"/>
																				<link itemprop="itemCondition" href="http://schema.org/NewCondition"/>
																				<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
																					<dsp:param name="id" value="${bopisStore}"/>
																					<dsp:param name="elementName" value="store"/>
																					<dsp:oparam name="output">
																						<dsp:getvalueof var="postalCode" param="store.postalCode" scope="request"/>
																						<dsp:droplet name="BopisStoreAvailabilityDroplet">
																							<dsp:param name="productId" param="productItem.id"/>
																							<dsp:param name="skuId" value="${skuId}"/>
																							<dsp:param name="quantity" value="1"/>
																							<dsp:param name="storeId" value="${bopisStore}"/>
																							<dsp:oparam name="true">
																								<c:set var="isAvailable" value="true" scope="request"/>
																								<link itemprop="availability" href="http://schema.org/InStock"/>
																								<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																								<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																								<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																									<meta itemprop="name" content="Fleet Farm"/>
																									<meta itemprop="branchCode" content="${bopisStore}"/>
																								</span>
																								<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																									<meta itemprop="value" content="0"/>
																								</span>
																								<span class="bopis-store-available">Available<span class="icon icon-available"></span></span>
																								<span>at</span>
																								<a class="bopis-store-info update-bopis-store underlined-link" href="#" itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																									<dsp:valueof param="store.city"/>,
																									<dsp:valueof param="store.stateAddress"/>
																								</a>
																							</dsp:oparam>
																							<dsp:oparam name="false">
																								<c:set var="isAvailable" value="false" scope="request"/>
																								<link itemprop="availability" href="http://schema.org/OutOfStock"/>
																								<link itemprop="potentialAction" itemscope itemtype="http://schema.org/BuyAction"/>
																								<link itemprop="availableDeliveryMethod" href="http://schema.org/OnSitePickup"/>
																								<span itemprop="availableAtOrFrom" itemscope itemtype="http://schema.org/Place">
																									<meta itemprop="name" content="Fleet Farm"/>
																									<meta itemprop="branchCode" content="${bopisStore}"/>
																								</span>
																								<span itemprop="deliveryLeadTime" itemscope itemtype="http://schema.org/QuantitativeValue">
																									<meta itemprop="value" content="0"/>
																								</span>
																								<span class="bopis-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
																								<span>at</span>
																								<a class="bopis-store-info update-bopis-store underlined-link" href="#">
																									<dsp:valueof param="store.city"/>,
																									<dsp:valueof param="store.stateAddress"/>
																								</a>
																							</dsp:oparam>
																						</dsp:droplet>
																						<c:if test="${not (isEdit and bopisOrder)}">
																							<div class="card-links ${hideBopisClass}">
																								<a href="#" class="disabled change-store">Change Store</a>
																								<c:if test="${not (bopisItemsOnly and bopisOnly)}">
																									<c:if test="${not bopisOnly and not cartIsEmpty}">
																										<span class="seperator">&nbsp;|&nbsp;</span>
																										<a href="#" class="disabled ship-my-order">Ship My Order Instead</a>
																									</c:if>
																								</c:if>
																							</div>
																						</c:if>
																					</dsp:oparam>
																				</dsp:droplet>
																			</div>
																		</div>
																	</div>
																</c:otherwise>
															</c:choose>
														</c:when>
													</c:choose>
												</div>
											</c:if>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<%-- bug 2338: Hide out-of-stock notifications for in-store only products --%>
										<c:when test="${itemOutOfStock && !bopisOnlyAvailable && !inStoreOnly}">
											<div class="radio radio-primary ship-to-home ${hideShippingClass}">
												<input type="radio" value="shipping-order" id="shipping-order" name="order-type" disabled />
												<label for="shipping-order">
													Ship To Home
													<span class="in-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
												</label>
											</div>
											<div class="radio radio-primary ship-to-home ${hideBopisClass}">
												<input type="radio" value="bopis-order" id="bopis-order" class="bopis-order" name="order-type" disabled />
												<label for="bopis-order">
													Pick Up In Store 
													<span class="in-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
												</label>
											</div>
											<a href="#" class="button primary disabled add-to-cart-submit ${hideAddToCart}">Add To Cart</a>		
										</c:when>
										<c:when test="${inStoreOnly}">
											<%-- do not display add to cart button for in store only items --%>
											<%--<a href="#" class="button primary disabled add-to-cart-submit-disabled">Add To Cart</a>--%>
										</c:when>
										<%--2427 --%>
										<c:when test="${edsPPSOnly && bopisOrder}">
											<a href="#" class="button primary disabled add-to-cart-submit ${hideAddToCart}">Add To Cart</a>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${isEdit}">
													<c:choose>
														<c:when test="${pickerType == 'TABLE'}">
															<a href="#" class="button primary add-to-cart-submit">Update Cart</a>
														</c:when>
														<c:otherwise>
															<a href="#" class="button primary update-cart-submit">Update Cart</a>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:when test="${isFFLItem and not fflOrder}">
													<%-- add FFL items to non-FFL order --%>
													<dsp:a href="${contextPath}/browse/ajax/fflModal.jsp" class="button primary disabled ffl-modal-trigger modal-trigger ${hideAddToCart}" data-target="ffl-modal" data-size="small">
														Add To Cart
													</dsp:a>
												</c:when>
												<c:when test="${fflOrder and not isFFLItem}">
													<%-- add non-FFL items to FFL order --%>
													<dsp:a href="${contextPath}/browse/ajax/notFFLModal.jsp" class="button primary disabled ffl-modal-trigger modal-trigger ${hideAddToCart}" data-target="not-ffl-modal" data-size="small">
														Add To Cart
													</dsp:a>
												</c:when>
												<c:when test="${not bopisOrder}">
													<a href="#" class="button primary disabled add-to-cart-submit ${hideAddToCart}">Add To Cart</a>
												</c:when>
												<c:otherwise>
													<a href="#" class="button primary disabled add-to-cart-submit ${hideAddToCart}<c:if test='${not isAvailable}'>disable-add-to-cart</c:if>">Add To Cart</a>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
									<dsp:droplet name="Switch">
										<dsp:param bean="Profile.hardLoggedIn" name="value"/>
										<dsp:oparam name="true">
											<div class="wishlist-section <c:if test='${inStoreOnly}'>wish-list-wrap</c:if>">
												<a href="#" class="add-to-wish-list disabled underlined-link">
													Add to Wish List
												</a>
											</div>
										</dsp:oparam>
										<dsp:oparam name="false">
											<div class="wishlist-section <c:if test='${inStoreOnly}'>wish-list-wrap</c:if>">
												<dsp:a href="#" class="login-modal-trigger disabled underlined-link">
													Add to Wish List
												</dsp:a>
											</div>
										</dsp:oparam>
									</dsp:droplet>
									<%--BZ-2705 --%>
									<%-- <c:if test="${itemOutOfStock && !inStoreOnly}">
										<dsp:getvalueof var="skuIdEmail" param="productItem.childSKUs[0].id" scope="request" />
										<div class="back-in-stock">
											<a href="${contextPath}/browse/ajax/backInStockModal.jsp?productId=${productId}&skuId=${skuIdEmail}" class="underlined-link disabled back-in-stock-modal-trigger modal-trigger" data-target="back-in-stock-modal" data-size="small">Email Me When Back In Stock</a>
										</div>
									</c:if> --%>
								</div>
							</c:if>

							<c:forEach var="childSku" items="${skus}">
								<dsp:param name="childSku" value="${childSku}"/>
								<dsp:getvalueof var="childSkuId" param="childSku.id"/>
								<dsp:getvalueof var="childSkuVpn" param="childSku.vpn"/>
								<c:if test = "${not empty searchTerm && (fn:endsWith(childSkuId, searchTerm)|| (not empty childSkuVpn && fn:endsWith(childSkuVpn, searchTerm)))}">
									 <dsp:getvalueof var="prevSku" param="childSku.id" />
								</c:if>
								<dsp:getvalueof var="shippingSurchargeQntyRange" param="childSku.shippingSurchargeQntyRange"/>
								<c:if test="${not empty shippingSurchargeQntyRange && !bopisOrder}">
									<div class="addln-shipping-surcharge-label">
										<span>Additional shipping charges may apply</span>
										<a class="view-details modal-trigger" href="/browse/ajax/shippingSurchargeModal.jsp" data-target="discontinued-item-policy-modal" data-size="small">
											<span class="icon icon-info"></span>
										</a>
									</div>
								</c:if>
							</c:forEach>

							<%--BZ: 2427 --%>
							<c:if test="${isFreeFreightShipment}">
								<div class="free-freight-shipping-label">
									<span class="icon icon-freight-truck"></span>
									<span>Free freight shipping on this item</span>
									<a class="view-details modal-trigger" href="/browse/ajax/freeFreightShippingModal.jsp" data-target="free-freight-shipping-modal" data-size="small">
										<span class="icon icon-info"></span>
									</a>
								</div>
							</c:if>

							<c:if test="${displaySplitOrderMsg}">
								<dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
									<dsp:param name="queryRQL" value="contentKey=:contentKey AND displayName=:displayName"/>
									<dsp:param name="contentKey" value="4444"/>
									<dsp:param name="displayName" value="PickupInStoreMessage"/>
									<dsp:param name="howMany" value="1"/>
									<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
									<dsp:param name="itemDescriptor" value="mffStaticContent"/>
									<dsp:param name="elementName" value="contentItem"/>
									<dsp:oparam name="output">
										<div class="split-order-msg">
											<h3>
												<span class="icon icon-info"></span>
												<span><dsp:valueof param="contentItem.contentSections[0].headline"/></span>
											</h3>
											<div>
												<c:choose>
													<c:when test="${not orderIsShippable}">
														<p>
															<%-- TODO: this should also probably be its own dynamic text field --%>
															We're sorry, but unfortunately this item cannot be fulfilled
															for pick-up in store, and you have an item in your cart which cannot
															be shipped. As we are currently unable to fulfill split orders,
															please remove the item from your cart to proceed adding this item.
														</p>
													</c:when>
													<c:otherwise>
														<dsp:valueof param="contentItem.contentSections[0].body" valueishtml="true"/>
													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>

							<c:if test="${pickerType ne 'TABLE'}">
								<dsp:droplet name="IsEmpty">
									<dsp:param name="value" param="productItem.splMsg"/>
									<dsp:oparam name="false">
										<div class="product-special-msg-info">
											<div class="card">
												<div class="card-title">
													<span class="icon icon-info"></span>
													<dsp:valueof param="productItem.splMsgTitle" valueishtml="true"/>
												</div>
												<div class="card-content">
													<p><dsp:valueof param="productItem.splMsg" valueishtml="true"/></p>
												</div>
											</div>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>

						</dsp:form>
						</c:if>
						
						<%-- hidden bopis search form --%>
						<dsp:form formid="bopis-inventory-form" id="bopis-inventory-form" method="post" name="bopis-inventory-form" data-validate>
							<input type="hidden" id="bopis-order-inventory" value="${bopisOnly}" />
							<c:choose>
								<c:when test="${edsPPSOnlyOrder}">
									<input type="hidden" id="eds-pps-only-inventory" value="true" />
								</c:when>
								<c:otherwise>
									<input type="hidden" id="eds-pps-only-inventory" value="false" />
								</c:otherwise>
							</c:choose>
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.quantity" id="bopis-quantity-inventory" name="bopis-quantity-inventory" value="0"/>
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.fromProduct" id="bopis-from-product-inventory" name="bopis-from-product-inventory" value="true"/>
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.productId" id="bopis-product-id-inventory" name="bopis-product-id-inventory" value="${productId}"/>
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.catalogRefId" id="bopis-sku-id-inventory" name="bopis-sku-id-inventory" value="${skuId}"/>
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.distance" id="bopis-distance-inventory" name="bopis-distance-inventory" value="80467.2"/>
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.postalCode" id="bopis-zip-inventory" name="bopis-zip-inventory" value="${postalCode}" />
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.editMode" id="bopis-edit-mode-inventory" name="bopis-edit-mode-inventory" value="${isEdit}" />
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.removalCommerceIds" id="bopis-removalCommerceIds-inventory" name="bopis-removalCommerceIds-inventory" value="${itemId}" />
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.errorURL" value="${contextPath}/browse/json/bopisSearchError.jsp" />
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.successURL" value="${contextPath}/browse/json/bopisSearchSuccess.jsp" />
							<dsp:input type="hidden" bean="StoreLocatorFormHandler.locateItems" id="bopis-search-submit-inventory" name="bopis-search-submit-inventory" class="button primary" value="Find Stores" />
						</dsp:form>

						<%-- hidden bopis submit form --%>
						<dsp:form formid="select-bopis-store" id="select-bopis-store" method="post" name="select-bopis-store">
							<input type="hidden" id="bopis-change-store" value="false" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.bopisStore" id="bopis-store-id" name="bopis-store-id" value="${bopisStore}" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.quantity" id="bopis-quantity" name="bopis-quantity" value="0" priority="9" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.fromProduct" id="bopis-from-product" name="bopis-from-product" value="true" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.productId" id="bopis-product-id" name="bopis-product-id" value="${productId}" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.catalogRefIds" id="bopis-sku-id" name="bopis-sku-id" value="${skuId}" priority="9" />
							<c:if test="${isEdit}">
								<dsp:input type="hidden" bean="CartModifierFormHandler.removalCommerceIds" name="bopis-removalIds" id="bopis-removalIds" value="${itemId}" priority="10"/>
								<dsp:input bean="CartModifierFormHandler.editMode" id="bopis-edit-mode" name="bopis-edit-mode" value="${isEdit}" type="hidden" priority="11"/>
							</c:if>
							<dsp:input type="hidden" bean="CartModifierFormHandler.chooseStoreSuccessURL" value="${contextPath}/checkout/json/cartSuccess.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.chooseStoreErrorURL" value="${contextPath}/checkout/json/cartError.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.chooseBopisStore" id="choose-this-store" name="choose-this-store" value="Submit" />
						</dsp:form>

						<%-- hidden form for switching to shipped order instead of bopis --%>
						<dsp:form formid="ship-my-order-form" id="ship-my-order-form" name="ship-my-order-form" action="${contextPath}/browse/product.jsp" method="post">
							<dsp:input type="hidden" bean="CartModifierFormHandler.fromProduct" name="fromProduct" value="true" priority="8"/>
							<dsp:input type="hidden" bean="CartModifierFormHandler.shipMyOrderSuccessURL" value="${contextPath}/sitewide/json/shipMyOrderSuccess.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.shipMyOrderErrorURL" value="${contextPath}/sitewide/json/shipMyOrderError.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.shipMyOrder" id="ship-my-order" name="ship-my-order" value="submit" />
						</dsp:form>

						<%-- hidden form for adding an item to wish list--%>
						<dsp:form name="addItemToWishList" action="${contextPath}/browse/product.jsp" method="post" id="addItemToWishList" formid="addItemToWishList">
							<dsp:input bean="CartModifierFormHandler.productId" name="productId" value="${productId}" type="hidden"/>
							<dsp:input bean="CartModifierFormHandler.catalogRefIds" name="catalogRefIds" iclass="input-selected-sku" id="wish-list-sku" value="${skuId}" priority="9" type="hidden" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.wishListId" name="wishListId" id="wishListId" beanvalue="Profile.wishlist.id" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.addItemToGiftlistSuccessURL" value="${contextPath}/checkout/json/wishListSuccess.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.addItemToGiftlistErrorURL" value="${contextPath}/checkout/json/wishListError.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.addItemToWishlist" id="addItemToWishList" name="addItemToWishList" value="submit" />
						</dsp:form>

						<%-- Edit Cart form, this form will be submitted when user redirected to PDP when user clicks on edit option on cart--%>
						<dsp:form name="updateItem" action="${contextPath}/browse/product.jsp" method="post" id="updateItem" formid="updateItem">
							<dsp:input bean="CartModifierFormHandler.productId" name="productId" value="${productId}" type="hidden" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.fromProduct" name="fromProduct" value="true" priority="8"/>
							<dsp:input type="hidden" bean="CartModifierFormHandler.bopisStore" id="update-bopis-store-id" name="update-bopis-store-id" value="${bopisStore}" />
							<dsp:input bean="CartModifierFormHandler.quantity" id="editQuantity" name="editQuantity" value="0" type="hidden" />
							<dsp:input bean="CartModifierFormHandler.editvalue.previousQnty" id="prevQuantity" name="prevQuantity" value="${prevQnty}" type="hidden" />
							<dsp:input bean="CartModifierFormHandler.editMode" id="update-item-edit-mode" name="update-item-edit-mode" value="true" type="hidden" />
							<dsp:input bean="CartModifierFormHandler.catalogRefIds" name="catalogRefIds" iclass="input-selected-sku" id="updateItemSku" value="${skuId}" priority="9" type="hidden" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.removalCommerceIds" name="removalIds" id="removalIds" value="${itemId}" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.removeAndAddItemToOrderSuccessURL" id="successUrl" value="${contextPath}/checkout/json/cartEditSuccess.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.removeAndAddItemToOrderErrorURL" id="errorUrl" value="${contextPath}/checkout/json/cartEditError.jsp" />
							<dsp:input type="hidden" bean="CartModifierFormHandler.removeAndAddItemToOrder" id="updateCart" name="updateCart" value="UpdateCart" />
						</dsp:form>

					</div>

					<%-- tabs --%>
					<dsp:include page="includes/productInfoAccordion.jsp" >
						<dsp:param name="productItem" param="productItem"/>
						<dsp:param name="seoReviews" value="${seoReviews}" />
						<dsp:param name="seoRatings" value="${seoRatings}" />
					</dsp:include>

				</section>

				<%-- TODO: Phase 2 - recommendations --%>
				<%--<c:import url="/browse/includes/crossSells.jsp" />--%>

				<dsp:param name="gtmProdListPrice" value="${listPrice}" />
				<dsp:param name="gtmProdSalePrice" value="${salePrice}" />
				<dsp:param name="filteredSkus" value="${skus}" />

				<%-- recently viewed --%>
				<dsp:include page="/browse/includes/recentlyViewed.jsp" />

				<%-- product data json --%>
				<script>
					var KP_PRODUCT = KP_PRODUCT || {};
					KP_PRODUCT["${productId}"] = <dsp:include src="/browse/json/productData.jsp"><dsp:param name="product" value="${product}"/><dsp:param name="skus" value="${skus}"/><dsp:param name="prevSku" value="${prevSku}"/><dsp:param name="stockLevelForSkusOfProduct" value="${stockLevelForSkusOfProduct}"/><dsp:param name="productOutOfStock" value="${itemOutOfStock}"/><dsp:param name="bopisOnlyAvailable" value="${bopisOnlyAvailable}"/></dsp:include>;
				</script>

			</jsp:body>
		</layout:default>
	</c:if>
</dsp:page>
