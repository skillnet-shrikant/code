<%--
  - File Name: quickViewModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows a user to quickly see product information
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/com/bv/seo/BVSeoDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="productImageRoot" bean="/mff/MFFEnvironment.productImageRoot" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">quickViewModal</jsp:attribute>
		<jsp:body>
			<c:if test="${bvEnabled}">
				<dsp:include otherContext="/bv" page="/productDisplay/common/bv_pdp_qv_script.jsp">
					<dsp:param name="externalId" value="${productId}" />
				</dsp:include>
				<c:set var="contextPath" value="${currentContext}" />
			</c:if>
			<dsp:droplet name="ProductLookup">
				<dsp:param name="id" param="productId"/>
				<dsp:param name="elementName" value="product"/>
				<dsp:oparam name="output">
					<dsp:getvalueof param="product" var="product"/>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:param name="productItem" value="${product}"/>

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

			<div class="quick-view-modal">

				<div class="modal-body">
					<%-- product images --%>
					<div class="product-images-social">
						<dsp:include page="${contextPath}/browse/includes/productImages.jsp">
							<dsp:param name="productId" value="${productId}"/>
							<dsp:param name="productName" param="productItem.description"/>
						</dsp:include>
					</div>

					<%-- product details --%>
					<div class="product-details">
						<h1 class="product-name">
							<dsp:valueof param="productItem.description" valueishtml="true"/>
						</h1>
						<div class="product-brand">
							<a href="${brandUrl}"><dsp:valueof param="productItem.brand" valueishtml="true"/></a>
						</div>
						<c:if test="${bvEnabled}">
							<div class="product-reviews" itemprop="aggregateRating" itemscope itemtype="http://schema.org/AggregateRating">
								<dsp:include otherContext="/bv" page="/productDisplay/ratings/bv_ratings_container.jsp" >
									<dsp:param name="externalId" value="${productId}" />
									<dsp:param name="seoRatings" value="${seoRatings}"/>
									<dsp:param name="pageType" value="quickview" />
								</dsp:include>
							</div>
							<c:set var="contextPath" value="${currentContext}" />
						</c:if>
						<div class="product-number">
							<span class="label">Product #:</span>
							<span><c:out value="${productId}"/></span>
						</div>
						<dsp:getvalueof var="skuId" param="productItem.childSKUs[0].id" scope="request" />
						<c:if test="${skuCount.size gt 1}">
							<c:set var="skuId" value="" scope="request" />
							<c:set var="productSkuClass" value="hide" scope="request" />
						</c:if>
						<div class="product-sku ${productSkuClass}">
							<span class="pipe">|</span>
							<span class="label">SKU:</span>
							<span class="sku-number">${skuId}</span>
						</div>
						<c:if test="${!isGiftCard}">
							<div class="product-price price" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
								<span itemprop="priceCurrency" content="USD" />
								<dsp:include page="${contextPath}/browse/includes/productPrice.jsp" >
									<dsp:param name="productItem" param="productItem"/>
								</dsp:include>
							</div>
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
							<dsp:oparam name="5">
								<%-- 5: in store only (no bopis or ship-to-home) --%>
								<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
									<dsp:param name="inUrl" value="${contextPath}/sitewide/storeLocator.jsp"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="storeLocatorUrl" scope="request" param="secureUrl"/>
										<p class="product-info">
											<span class="orange">Available for Purchase in Store Only</span><br/>
											<span style="font-size:12px">Price and availability may vary by location&nbsp;&nbsp;</span>
											<dsp:a href="${storeLocatorUrl}" iclass="product-in-store">Find a Store</dsp:a>
										</p>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>

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

							<div class="product-info">
								This item requires an FFL shipment address.
								<a href="${contextPath}/static/faq-purchasing-firearms-online">Click here for more information.</a>
							</div>
						</c:if>

						<div class="add-to-cart-actions">
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
							<a href="${productUrl}" class="button primary add-to-cart-submit">View Product</a>
						</div>

						<%-- tabs --%>
						<dsp:include page="${contextPath}/browse/includes/productInfoAccordion.jsp" >
							<dsp:param name="productItem" param="productItem"/>
							<dsp:param name="isFromQuickView" value="true" />
							<dsp:param name="seoReviews" value="${seoReviews}" />
							<dsp:param name="seoRatings" value="${seoRatings}" />
						</dsp:include>

					</div>

				</div>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
