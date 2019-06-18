<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
	<dsp:importbean bean="/atg/commerce/catalog/SKULookup" />
	<dsp:importbean bean="/com/mff/droplet/MFFDynamicAttributesBySkuDroplet" />
	<dsp:importbean bean="/com/mff/account/order/droplet/HasAddtionalHandlingDroplet"/>
	<dsp:importbean bean="/com/mff/account/order/droplet/HasOversizeItemDroplet"/>
	<dsp:importbean bean="/com/mff/commerce/order/purchase/LineItemTotalPriceDroplet"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

	<%-- Page Variables --%>
	<dsp:getvalueof id="itemId" idtype="java.lang.String" param="commerceItem.catalogrefId" />
	<dsp:getvalueof id="ciId" idtype="java.lang.String" param="commerceItem.id" />
	<dsp:getvalueof id="quantity" idtype="java.lang.String" param="commerceItem.quantity" />
	<dsp:getvalueof var="stockLevel" value="${stockLevelForSkusInOrder[itemId].stockLevel}" />
	<dsp:getvalueof var="maxQty" bean="MFFEnvironment.maxQtyPerItemInOrder" />
	<dsp:getvalueof var="productId" param="commerceItem.auxiliaryData.productId" />
	<dsp:getvalueof var="productName" param="commerceItem.auxiliaryData.productRef.description" />
	<dsp:getvalueof var="productImageRoot" bean="MFFEnvironment.productImageRoot" />
	<dsp:getvalueof var="isClearance" value="false" scope="request"/>
	<dsp:getvalueof var="isSalePrice" value="false" scope="request"/>
	<dsp:getvalueof var="hasPromoMsgs" value="false" scope="request"/>
	<dsp:getvalueof var="ltlShipRates" bean="ShoppingCart.current.ltlShipRates" />

	<%-- vars added to handle 2414. See comments in cartItem.jspf--%>
	<dsp:getvalueof var="qtyToDisplay" param="displayQty" />
	<dsp:getvalueof var="freeGift" param="freeGift" />
	<dsp:getvalueof var="justGift" param="justGift" />

	<c:if test="${empty qtyToDisplay}">
		<c:set var="qtyToDisplay" value="${quantity}" />
	</c:if>
	<c:if test="${freeGift and not justGift}">
		<c:set var="ciId" value="${ciId}-FREE" />
	</c:if>
	<%-- for performance, we don't want to trigger an automatic quantity change (that will happen on "proceed to cart"). --%>
	<c:choose>
		<c:when test="${stockLevel < quantity}">
			<%-- if stock level is less than current qty, set maxQty to current qty so the user can't increase qty, but also the automatic quantity change doesn't happen --%>
			<dsp:getvalueof var="maxQty" value="${quantity}" />
		</c:when>
		<c:otherwise>
			<%-- if current stock level is greater than current qty but less than the default max quantity, set maxQty to the current stock level --%>
			<c:if test="${stockLevel < maxQty}">
				<dsp:getvalueof var="maxQty" value="${stockLevel}" />
			</c:if>
		</c:otherwise>
	</c:choose>

	<dsp:droplet name="LineItemTotalPriceDroplet">
		<dsp:param name="commerceItem" param="commerceItem"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="totalLinePrice" param="totalLinePrice"/>
			<dsp:getvalueof var="itemPromos" param="lineItemPromos"/>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:droplet name="/com/mff/commerce/order/purchase/IsGWPItemDroplet">
		<dsp:param name="commerceItem" param="commerceItem"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="isGWPItem" param="result" scope="request" />
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

	<div class="order-item" data-ciid="${ciId}" data-prodId="${productId}" >
	<%-- BZ 2414 - Removed this per request in the bug.
		<c:if test="${freeGift}">
			<dsp:getvalueof var="promoId" param="pricingModel.id" />
			<span class="free-gift">This free item has been added to your cart.
				<a class="view-details modal-trigger" href="/checkout/ajax/gwpModal.jsp?p=${promoId}" data-target="free-gift-modal" data-size="small">
					<span class="icon icon-info"></span>
				</a>
			</span>
		</c:if>
	 --%>
	 	<dsp:droplet name="/com/mff/droplet/DisplayHyperlinkDroplet">
			<dsp:param name="productId" param="commerceItem.auxiliaryData.productId" />
			<dsp:param name="skuId" param="commerceItem.catalogrefId" />
			<dsp:oparam name="true">
				<dsp:getvalueof var="displayHyperlink" value="true" vartype="java.lang.Boolean"/>
			</dsp:oparam>
			<dsp:oparam name="false">
				<dsp:getvalueof var="displayHyperlink" value="false" vartype="java.lang.Boolean"/>
			</dsp:oparam>
		</dsp:droplet>
		<div class="item-image order-item-section">
			<c:choose>
				<c:when test="${displayHyperlink}">
					<a href="${productUrl}">
						<dsp:droplet name="/com/mff/browse/droplet/ProductImageDroplet">
							<dsp:param name="productId" param="commerceItem.auxiliaryData.productId" />
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
				</c:when>
				<c:otherwise>
					<dsp:droplet name="/com/mff/browse/droplet/ProductImageDroplet">
							<dsp:param name="productId" param="commerceItem.auxiliaryData.productId" />
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
				</c:otherwise>
			</c:choose>
		</div>
		<div class="item-details order-item-section">
			<div class="product-name">
				<c:choose>
					<c:when test="${displayHyperlink}">
						<a href="${productUrl}">${productName}</a>
					</c:when>
					<c:otherwise>
						${productName}
					</c:otherwise>
				</c:choose>
			</div>
			<div class="price">
				<dsp:droplet name="Compare">
					<dsp:param name="obj1" param="commerceItem.priceInfo.salePrice" />
					<dsp:param name="obj2" param="commerceItem.priceInfo.listPrice" />
					<dsp:oparam name="lessthan">
						<dsp:getvalueof var="ciSalePrice" param="commerceItem.priceInfo.salePrice"/>
						<c:if test="${ciSalePrice gt 0}">
							<dsp:getvalueof var="isSalePrice" value="true" scope="request"/>
						</c:if>
						<div class="original-price">
							<dsp:valueof param="commerceItem.priceInfo.listPrice" converter="currency"/>
						</div>
						<div class="sale-price">
							<dsp:valueof param="commerceItem.priceInfo.salePrice" converter="currency"/>
						</div>
					</dsp:oparam>
					<dsp:oparam name="default">
						<div class="regular-price">
							<dsp:valueof param="commerceItem.priceInfo.listPrice" converter="currency"/>
						</div>
					</dsp:oparam>
				</dsp:droplet>
			</div>
			<div class="product-sku">
				<span class="label">SKU:</span>
				<span>${itemId}</span>
			</div>
			<div class="product-selections">

				<dsp:droplet name="ProductLookup">
					<dsp:param name="id" param="commerceItem.auxiliaryData.productId"/>
					<dsp:param name="filterByCatalog" value="false"/>
					<dsp:param name="filterBySite" value="false"/>
					<dsp:param name="elementName" value="productItem"/>
					<dsp:oparam name="output">

						<dsp:droplet name="SKULookup">
							<dsp:param name="id" param="commerceItem.catalogrefId"/>
							<dsp:param name="filterByCatalog" value="false"/>
							<dsp:param name="filterBySite" value="false"/>
							<dsp:param name="elementName" value="skuItem"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="isClearance" param="skuItem.clearance" scope="request"/>
								<%-- LTL is highest priority, hence no need check for additonal handling or oversize --%>
								<c:set var="calcAddlnHandling" value="false" />
								<c:if test="${ltlShipRates eq 'false'}">
									<%-- long and light --%>

									<%-- BZ3063 - Oversized rates will apply independent of addln handling --%>
									<dsp:droplet name="HasOversizeItemDroplet">
										<dsp:param name="commerceItem" param="commerceItem"/>
										<dsp:oparam name="output">
											<dsp:droplet name="Switch">
												<dsp:param name="value" param="isOverSize"/>
												<dsp:oparam name="true">
													<c:set var="isOversize" value="${isOversize + quantity}" scope="request" />
												</dsp:oparam>
												<dsp:oparam name="false">
													<dsp:droplet name="HasAddtionalHandlingDroplet">
														<dsp:param name="commerceItem" param="commerceItem"/>
														<dsp:oparam name="output">
															<dsp:droplet name="Switch">
																<dsp:param name="value" param="isLongLight"/>
																<dsp:oparam name="true">
																	<c:set var="longLight" value="${longLight + quantity}" scope="request" />
																</dsp:oparam>
															</dsp:droplet>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>

								</c:if>

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

					</dsp:oparam>
				</dsp:droplet>

			</div>

			<%-- minimum age --%>
			<dsp:droplet name="Switch">
				<dsp:param name="value" param="commerceItem.minimumAge"/>
				<dsp:oparam name="18">
					<c:set var="signatureRequired" value="1" scope="request" />
					<div class="product-message">
						<span class="icon icon-error"></span> You must be at least 18 years old to purchase this item.
					</div>
				</dsp:oparam>
				<dsp:oparam name="21">
					<c:set var="signatureRequired" value="1" scope="request" />
					<div class="product-message">
						<span class="icon icon-error"></span> You must be at least 21 years old to purchase this item
					</div>
				</dsp:oparam>
			</dsp:droplet>

			<%-- item actions --%>
			<div class="item-actions <c:if test='${isGWPItem and (freeGift or justGift)}'> hide </c:if>">
				<dsp:getvalueof var="variants" param="commerceItem.auxiliaryData.productRef.dynamicAttributes" />
				<c:if test="${not empty variants}">
					<div class="item-action">
						<dsp:a bean="CartModifierFormHandler.editCartItem" href="${productUrl}?edit=true" value="${ciId}">
							Edit
						</dsp:a>
					</div>
				</c:if>
				<div class="item-action">
					<a href="ajax/removeItemModal.jsp?ciId=${ciId}" class="modal-trigger" data-target="remove-item-modal" data-size="small">Remove</a>
				</div>
				<dsp:droplet name="/atg/dynamo/droplet/Switch">
					<dsp:param bean="Profile.hardLoggedIn" name="value"/>
					<dsp:oparam name="true">
						<div class="item-action">
							<a href="#" class="item-save" data-ciid="${ciId}">Move to Wish List</a>
						</div>
					</dsp:oparam>
				</dsp:droplet>

			</div>
			<c:choose>
				<c:when test="${isGWPItem and (freeGift or justGift)}">
					<%-- gwp message --%>
					<%--2393 Removing the GWP note --%>
					<%--
					<a class="gwp-note modal-trigger" href="/checkout/ajax/gwpDetailsModal.jsp" data-target="promo-details-modal" data-size="small">
						Free item discount is reflected in the price of the qualifying item. <span class="icon icon-info"></span>
					</a>
					 --%>
				</c:when>
				<c:otherwise>

				</c:otherwise>
			</c:choose>
			<br/>

		<%--BZ: 2427 --%>
		<dsp:getvalueof var="isFreeFreightShipment" param="commerceItem.auxiliaryData.catalogRef.freeShipping"/>
		<c:if test="${isFreeFreightShipment}">
			<div class="free-freight-shipping-label">
				<span class="icon icon-freight-truck"></span>
				<span>Free freight shipping on this item</span>
				<a class="view-details modal-trigger" href="/browse/ajax/freeFreightShippingModal.jsp" data-target="free-freight-shipping-modal" data-size="small">
					<span class="icon icon-info"></span>
				</a>
			</div>
		</c:if>
		<div class="promo-line-item-msg hide-for-small">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" value="${itemPromos}"/>
					<dsp:param name="elementName" value="promoItem"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="promoDisplayName" param="promoItem.displayName" idtype="java.lang.String" />
						<dsp:getvalueof var="shortDescription" param="promoItem.shortDescription" idtype="java.lang.String" />
						<dsp:getvalueof var="description" param="promoItem.description" idtype="java.lang.String" />
						<c:if test="${not empty shortDescription}">
							<dsp:getvalueof var="hasPromoMsgs" value="true" scope="request"/>

							<span class="icon icon-check"></span><span class="promo-line-item-desc">${shortDescription}</span>&nbsp;discount applied
							<c:url value="${contextPath}/checkout/ajax/lineItemPromoDetailsModal.jsp" var="url">
								<c:param name="p" value="${promoDisplayName}" />
								<c:param name="d" value="${description}" />
							</c:url>

							<a class="view-details modal-trigger" href="${url}" data-target="promo-details-modal" data-size="small"><span class="icon icon-info"></span></a>

						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</div>
			<c:if test="${isClearance eq 'true' && isSalePrice eq 'true'}">
				<div class="discontinued-policy-label hide-for-small"><span>Clearance Item Policy</span>&nbsp;<a class="view-details modal-trigger" href="/browse/ajax/discontinuedItemPolicyModal.jsp" data-target="discontinued-item-policy-modal" data-size="small"><span class="icon icon-info"></span></a></div>
			</c:if>

			<dsp:droplet name="IsEmpty">
				<dsp:param name="value" param="commerceItem.auxiliaryData.productRef.splMsg"/>
				<dsp:oparam name="false">
					<div class="product-special-msg-info hide-for-small">
						<span class="title"><dsp:valueof param="commerceItem.auxiliaryData.productRef.splMsgTitle" valueishtml="true"/></span>&nbsp;
						<dsp:a page="${contextPath}/sitewide/ajax/productSpecialMsgModal.jsp" iclass="view-details modal-trigger" data-target="product-special-msg-modal" data-size="small">
							<dsp:param name="productId" param="commerceItem.auxiliaryData.productRef.id"/>
							<span class="icon icon-info"></span>
						</dsp:a>
					</div>
				</dsp:oparam>
			</dsp:droplet>

		</div>

		<%-- 2414: Do not display price/qty for gifts
		<c:if test="${not freeGift}">
		--%>
			<div class="item-price-unit order-item-section <c:if test='${isGWPItem and freeGift}'> hide </c:if>">
				<div class="price">
					<dsp:droplet name="Compare">
						<dsp:param name="obj1" param="commerceItem.priceInfo.salePrice" />
						<dsp:param name="obj2" param="commerceItem.priceInfo.listPrice" />
						<dsp:oparam name="lessthan">
							<div class="original-price">
								<dsp:valueof param="commerceItem.priceInfo.listPrice" converter="currency"/>
							</div>
							<div class="sale-price">
								<dsp:valueof param="commerceItem.priceInfo.salePrice" converter="currency"/>

								<br/>
								<c:if test="${isSalePrice eq 'true'}">
									<c:choose>
										<c:when test="${isClearance eq 'true'}">
											CLEARANCE
										</c:when>
										<c:otherwise>
											SALE
										</c:otherwise>
									</c:choose>
								</c:if>
							</div>
						</dsp:oparam>
						<dsp:oparam name="default">
							<div class="regular-price">
								<dsp:valueof param="commerceItem.priceInfo.listPrice" converter="currency"/>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</div>

			<div class="item-quantity order-item-section <c:if test='${isGWPItem}'>gwp</c:if> <c:if test='${isGWPItem and freeGift}'> hide </c:if>">
				<span class="label">Qty:</span>
				<c:choose>
					<c:when test="${isGWPItem and freeGift}">
						<%--<span class="gwp-quantity">${qtyToDisplay}</span>
						<input id="quantityUpdate" name="${ciId}-display" type="hidden" value="${quantity}" />

						 2414: quantityUpdate holds the value to display & totalQty is used in form submissions
						<input id="totalQuantity" name="${ciId}" type="hidden" value="${qtyToDisplay}" />
						--%>
						<%-- 2414 - data-free To track free gift quantity --%>
						<div class="change-quantity" data-max="${maxQty}" data-free="${quantity-qtyToDisplay}" data-quantify>
							<div class="quantity-group">
								<div class="minus-icon inactive">
									<span class="icon icon-minus"></span>
								</div>
								<div class="current-quantity">
									<input id="quantityUpdate" name="${ciId}-display" type="text" class="counter" value="${qtyToDisplay}" />
									<input id="totalQuantity" name="${ciId}" type="hidden" value="${qtyToDisplay}" />
								</div>
								<div class="plus-icon">
									<span class="icon icon-plus"></span>
								</div>
							</div>
						</div>
						<div class="update-qty" id="updateQty-${ciId}">
							<a href="#" class="updateCartQty"><fmt:message key="common.update"/></a>
						</div>
					</c:when>
					<c:otherwise>
						<%-- 2414 - data-free To track free gift quantity --%>
						<div class="change-quantity" data-max="${maxQty}" data-free="${quantity-qtyToDisplay}" data-quantify>
							<div class="quantity-group">
								<div class="minus-icon inactive">
									<span class="icon icon-minus"></span>
								</div>
								<div class="current-quantity">
									<input id="quantityUpdate" name="${ciId}-display" type="text" class="counter" value="${qtyToDisplay}" />
									<input id="totalQuantity" name="${ciId}" type="hidden" value="${qtyToDisplay}" />
									<input id="freeQuantity" name="${ciId}-freeQty" type="hidden" value="${quantity-qtyToDisplay}" />
								</div>
								<div class="plus-icon">
									<span class="icon icon-plus"></span>
								</div>
							</div>
						</div>
						<div class="update-qty" id="updateQty-${ciId}">
							<a href="#" class="updateCartQty"><fmt:message key="common.update"/></a>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			<%--
		</c:if>
		--%>
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" param="commerceItem.auxiliaryData.productRef.splMsg"/>
			<dsp:oparam name="false">
				<div class="product-special-msg-info hide-for-medium-up">
					<span class="title"><dsp:valueof param="commerceItem.auxiliaryData.productRef.splMsgTitle" valueishtml="true"/></span>&nbsp;
					<dsp:a page="${contextPath}/sitewide/ajax/productSpecialMsgModal.jsp" iclass="view-details modal-trigger" data-target="product-special-msg-modal" data-size="small">
						<dsp:param name="productId" param="commerceItem.auxiliaryData.productRef.id"/>
						<span class="icon icon-info"></span>
					</dsp:a>
				</div>
			</dsp:oparam>
		</dsp:droplet>
		<div class="item-price-subtotal order-item-section <c:if test='${isGWPItem}'>gwp</c:if>">
			<span class="label">Total:</span>
			<c:choose>
				<c:when test="${not freeGift}">
					<span class="item-total"><dsp:valueof value="${totalLinePrice}" converter="currency"/></span>
				</c:when>
				<c:otherwise>
					<span class="item-total free">FREE</span>
				</c:otherwise>
			</c:choose>
		</div>

	</div>

</dsp:page>