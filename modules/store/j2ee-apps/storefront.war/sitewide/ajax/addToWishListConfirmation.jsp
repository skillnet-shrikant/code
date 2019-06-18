<%--
  - File Name: addToWishListConfirmationModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows a user to email a product's information to a friend
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="skuId" param="skuId" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="productImageRoot" bean="/mff/MFFEnvironment.productImageRoot" />

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">addToWishListConfirmationModal</jsp:attribute>
		<jsp:body>

			<div class="add-to-wish-list-confirmation-modal">

				<div class="modal-header">
					<h2>ITEM ADDED TO WISH LIST</h2>
				</div>

				<div class="modal-body">

					<%-- modal message --%>
					<p>The following product has been successfully added to your Wish List:</p>

					<dsp:droplet name="ProductLookup">
						<dsp:param name="id" param="productId"/>
						<dsp:param name="filterByCatalog" value="false"/>
						<dsp:param name="filterBySite" value="false"/>
						<dsp:param name="elementName" value="giftProductItem"/>
						<dsp:oparam name="output">
							<dsp:droplet name="SKULookup">
								<dsp:param name="id" param="skuId"/>
								<dsp:param name="filterByCatalog" value="false"/>
								<dsp:param name="filterBySite" value="false"/>
								<dsp:param name="elementName" value="giftSkuItem"/>
								<dsp:oparam name="output">

									<%-- product details --%>
									<div class="product-details">
										<div class="product-brand">
											<dsp:valueof param="giftProductItem.brand"/>
										</div>
										<h1 class="product-name">
											<dsp:getvalueof param="giftProductItem.description" var="productName"/>
											<dsp:getvalueof param="giftProductItem.brand" var="productBrand"/>
											<dsp:getvalueof var="productNameWithoutBrand" value="${fn:substringAfter(productName,productBrand)}" />
											${productNameWithoutBrand}
										</h1>
										<div class="product-number">
											<span class="label">Product #:</span>
											<span><dsp:valueof param="productId"/></span>
										</div>
										<div class="product-sku">
											<span class="pipe">|</span>
											<span class="label">SKU:</span>
											<span><dsp:valueof param="skuId"/></span>
										</div>
										<dsp:droplet name="/com/mff/browse/droplet/IsInactiveProduct">
											<dsp:param name="productId" value="${productId}"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="isInactiveProd" param="isInActiveProduct"/>
												<dsp:getvalueof var="isActiveTeaser" param="isActiveTeaser"/>
											</dsp:oparam>
										</dsp:droplet>
										
										<c:choose>
												<c:when test="${isActiveTeaser}">
													<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
														<dsp:param name="value" param="giftProductItem.teaserPDPMessage"/>
														<dsp:oparam name="true">
															<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
				  												<dsp:param name="queryRQL" value="infoKey=\"EVENT_ITEM_PDP\""/>
				  												<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
				  												<dsp:param name="itemDescriptor" value="infoMessage"/>
				  												<dsp:param name="elementName" value="contentItem"/>
				  												<dsp:oparam name="output">
																	<div class="regular-price">
																		<span itemprop="price" content="content">
																			<dsp:valueof param="contentItem.infoMsg" valueishtml="true"/>
																		</span>
																	</div>
				  												</dsp:oparam>
															</dsp:droplet>
														</dsp:oparam>
														<dsp:oparam name="false">
																<div class="regular-price">
																	<span itemprop="price" content="content">
																		<dsp:valueof param="giftProductItem.teaserPDPMessage" valueishtml="true"/>
																	</span>
																</div>
														</dsp:oparam>												
													</dsp:droplet>
												</c:when>
												<c:otherwise>
													<div class="product-price price" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
														<span itemprop="priceCurrency" content="USD" />
														<dsp:include page="${contextPath}/browse/includes/productPrice.jsp" >
															<dsp:param name="productItem" param="giftProductItem"/>
														</dsp:include>
													</div>
												</c:otherwise>
											</c:choose>										


										<%-- minimum age --%>
										<dsp:droplet name="/atg/dynamo/droplet/Switch">
											<dsp:param name="value" param="giftProductItem.minimumAge"/>
											<dsp:oparam name="18">
												<div class="product-message">
													<span class="icon icon-error"></span> You must be at least 18 years old to purchase this item.
												</div>
											</dsp:oparam>
											<dsp:oparam name="21">
												<div class="product-message">
													<span class="icon icon-error"></span> You must be at least 21 years old to purchase this item
												</div>
											</dsp:oparam>
										</dsp:droplet>

									</div>

									<%-- product image --%>
									<div class="product-image">
										<dsp:droplet name="/com/mff/browse/droplet/ProductImageDroplet">
											<dsp:param name="productId" param="productId" />
											<dsp:param name="imageSize" value="xl" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="defaultImage" param="productImages[0]" />
												<picture>
													<!--[if IE 9]><video style="display: none;"><![endif]-->
													<source srcset="${productImageRoot}/${productId}/m/${defaultImage}" media="(min-width: 768px)">
													<!--[if IE 9]></video><![endif]-->
													<img src="${productImageRoot}/${productId}/x/${defaultImage}" alt="${productName}" />
												</picture>
											</dsp:oparam>
											<dsp:oparam name="empty">
												<picture>
													<!--[if IE 9]><video style="display: none;"><![endif]-->
													<source srcset="${productImageRoot}/unavailable/m.jpg" media="(min-width: 768px)">
													<!--[if IE 9]></video><![endif]-->
													<img src="${productImageRoot}/unavailable/xl.jpg" alt="Image Unavailable" />
												</picture>
											</dsp:oparam>
										</dsp:droplet>
									</div>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
					<a href="${contextPath}/account/wishList.jsp" class="button primary expand">View Wish List</a>
				</div>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
