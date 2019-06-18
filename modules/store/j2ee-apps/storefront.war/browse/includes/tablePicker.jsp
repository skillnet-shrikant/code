<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
	<dsp:importbean bean="/com/mff/browse/droplet/ProductPickerDetailsDroplet"/>
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceListManager"/>
	<dsp:importbean bean="/atg/commerce/pricing/PriceRangeDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/com/mff/droplet/BopisStoreAvailabilityDroplet"/>

	<dsp:getvalueof var="isEdit" param="isEdit"/>
	<dsp:getvalueof var="skus" param="skus"/>
	<dsp:getvalueof var="orderIsShippable" bean="ShoppingCart.current.orderShippable" />
	<dsp:getvalueof var="bopisStore" bean="ShoppingCart.current.bopisStore"/>
	<dsp:getvalueof var="bopis" bean="ShoppingCart.current.bopisOrder"/>
	<dsp:getvalueof var="bopisAvail" param="bopisAvail" />
	<dsp:getvalueof var="inStoreOnly" param="inStoreOnly" />
	<dsp:getvalueof var="bopisOnly" param="bopisOnly" />
	<dsp:getvalueof var="cartIsEmpty" param="cartIsEmpty" />
	<dsp:getvalueof var="prevQnty" param="prevQnty" />
	<dsp:getvalueof var="prevSku" param="prevSku" />
	<dsp:getvalueof var="itemOutOfStock" param="itemOutOfStock" />
	<dsp:getvalueof var="bopisOnlyAvailable" param="bopisOnlyAvailable" />
	<dsp:getvalueof var="edsPPSOnly" param="edsPPSOnly" />
	<dsp:getvalueof var="displayEDSMessage" param="displayEDSMessage" />
	<dsp:getvalueof var="edsMessageHeadline" param="edsMessageHeadline" />
	<dsp:getvalueof var="bopisItemsOnly" param="bopisItemsOnly" />
	<dsp:getvalueof var="isItemRemovalRequired" param="isItemRemovalRequired" />
	<dsp:getvalueof var="stockLevelForSkusOfProduct" param="stockLevelForSkusOfProduct" />
	<dsp:getvalueof var="isActiveTeaser" param="isActiveTeaser" />

	<jsp:useBean id="commerceItemSkuQuantityMap" class="java.util.HashMap" scope="request" />

	<dsp:getvalueof var="isHidePrice" param="productItem.hidePrice"/>

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
	<c:if test="${itemOutOfStock and bopisOnlyAvailable}">
		<c:set var="bopisChecked" value="checked=''" scope="request" />
	</c:if>	
	<dsp:droplet name="ProductPickerDetailsDroplet">
		<dsp:param name="product" param="product"/>
		<dsp:param name="childSKUs" param="skus"/>
		<dsp:oparam name="output">

			<dsp:getvalueof var="productId" param="product.id" />

			<%-- table picker --%>
				<%-- Iterating through pickersData --%>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="pickersData"/>
					<dsp:oparam name="output">

						<dsp:droplet name="Switch">
							<dsp:param name="value" param="key"/>
							<dsp:oparam name="pickerTypes">
								<div class="table-picker-title">
									<span><dsp:valueof param="productItem.description" valueishtml="true"/></span>
								</div>
							</dsp:oparam>
							<dsp:oparam name="skus">
								<div>
									<%-- Iterating through skus --%>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="element"/>
										<dsp:param name="elementName" value="sku"/>
										<dsp:oparam name="outputStart">
										 	<dsp:input bean="CartModifierFormHandler.addItemCount" priority="9" paramvalue="size" type="hidden"/>
										 </dsp:oparam>
										<dsp:oparam name="output">
											<dsp:getvalueof var="skuIndex" param="index" />
											<dsp:getvalueof var="catalogRefId" param="sku.catalogRefId" scope="request"/>
											<dsp:getvalueof var="modelNumber" param="sku.modelNumber" scope="request" />
											<div class="table-picker-details">
												<div class="table-header">
													<p>SKU&nbsp;#:&nbsp;${catalogRefId}<c:if test="${not empty modelNumber}">&nbsp;|&nbsp;MODEL&nbsp;#:&nbsp;${modelNumber}</c:if>
														<span>Item <dsp:valueof param="count"/> of  <dsp:valueof param="size"/> </span>
													</p>
												</div>

												<div class="table table-details">
													<div class="product-sku-info" >
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="sku.skuAttributes"/>
															<dsp:param name="elementName" value="skuAttribute"/>
															<dsp:oparam name="output">
																<div>
																	<p><span><dsp:valueof param="key"/></span><span><dsp:valueof param="skuAttribute"/></span></p>
																</div>
															</dsp:oparam>
														</dsp:droplet>
														<%-- get list and sale prices --%>
														<dsp:droplet name="PriceDroplet">
															<dsp:param name="sku" value="${skus[skuIndex]}" />
															<dsp:param name="priceList" bean="PriceListManager.defaultPriceListId" />
															<dsp:oparam name="output">
																<dsp:getvalueof param="price.listPrice" var="listPrice" />
															</dsp:oparam>
														</dsp:droplet>
														<dsp:droplet name="PriceDroplet">
															<dsp:param name="sku" value="${skus[skuIndex]}" />
															<dsp:param name="priceList" bean="PriceListManager.defaultSalePriceListId" />
															<dsp:oparam name="output">
																<dsp:getvalueof param="price.listPrice" var="salePrice" />
															</dsp:oparam>
														</dsp:droplet>
														<dsp:param name="salePrice" value="${salePrice}" />
														<dsp:param name="listPrice" value="${listPrice}" />
														<c:choose>
															<c:when test="${isHidePrice eq 'true'}">
																<div class="hide-pricing-details-label">
																	<a class="view-details modal-trigger" href="/browse/ajax/addToCartToSeePriceModal.jsp" data-target="discontinued-item-policy-modal" data-size="small">
																		Click for Price
																	</a>
																</div>
															</c:when>
															<c:otherwise>
																<c:if test="${not empty listPrice}">
																	<%-- there is a list price --%>
																	<c:choose>
																		<c:when test="${not empty salePrice}">
																			<%-- there is a sale price --%>
																			<c:choose>
																				<c:when test="${listPrice ne salePrice}">
																					<dsp:getvalueof var="isClearance" param="sku.clearance" scope="request"/>

																					<%-- list and sale price are different, display sale --%>
																					<div class="original-price"><span>Price</span><dsp:valueof param="listPrice" converter="currency" />&nbsp;
																						<p class="sale-price"><dsp:valueof param="salePrice" converter="currency" />&nbsp;
																							<c:choose>
																								<c:when test="${isClearance eq 'true'}">
																									CLEARANCE
																									<a class="view-details modal-trigger" href="/browse/ajax/discontinuedItemPolicyModal.jsp" data-target="discontinued-item-policy-modal" data-size="small"><span class="icon icon-info"></span></a>
																								</c:when>
																								<c:otherwise>
																									SALE
																								</c:otherwise>
																							</c:choose>
																						</p>
																					</div>
																				</c:when>
																				<c:otherwise>
																					<%-- list and sale price are equal, display regular --%>
																					<div class="regular-price"><p><span>Price</span><span><dsp:valueof param="listPrice" converter="currency" /></span></p></div>
																				</c:otherwise>
																			</c:choose>
																		</c:when>
																		<c:otherwise>
																			<%-- there is only list price, display regular --%>
																			<div class="regular-price"><p><span>Price</span><span><dsp:valueof param="listPrice" converter="currency" /></span></p></div>
																		</c:otherwise>
																	</c:choose>
																</c:if>
															</c:otherwise>
														</c:choose>
													</div>
													<div class="product-qty-details">
														<c:choose>
															<c:when test="${isEdit}">
																<dsp:droplet name="ForEach">
																	<dsp:param name="array" bean="ShoppingCart.current.commerceItems" />
																	<dsp:param name="elementName" value="currentCommerceItem"/>
																	<dsp:oparam name="empty">
															  		</dsp:oparam>
															  		<dsp:oparam name="output">
															  			<dsp:getvalueof var="currentCommerceItemSku" param="currentCommerceItem.catalogrefId" />
															  			<dsp:droplet name="Compare">
															  				<dsp:param name="obj1" value="${currentCommerceItemSku}"/>
															  				<dsp:param name="obj2" value="${catalogRefId}"/>
															  				<dsp:oparam name="equal">
															  					<dsp:getvalueof var="currentCommerceItemQty" param="currentCommerceItem.quantity" />
																  				<c:set target="${commerceItemSkuQuantityMap}" property="${currentCommerceItemSku}" value="${currentCommerceItemQty}"/>
															  				</dsp:oparam>
															  			</dsp:droplet>
															  		</dsp:oparam>
																</dsp:droplet>

																<dsp:droplet name="IsEmpty">
																	<dsp:param name="value" value="${commerceItemSkuQuantityMap[catalogRefId]}" />
																	<dsp:oparam name="true">
																		<%-- <dsp:input bean="CartModifierFormHandler.items[${skuIndex}].quantity" size="4" type="tel" class="table-qty" id="qty-${skuIndex}" name="qty-${skuIndex}" priority="9" value="0"/> --%>
																		<dsp:getvalueof var="maxQty" bean="MFFEnvironment.maxQtyPerItemInOrder" />
																		<div class="product-qty">
																			<label for="quantity">Quantity</label>
																			<div class="change-quantity" data-min="0" data-max="${maxQty}" data-quantify>
																				<div class="quantity-group">
																					<div class="minus-icon inactive">
																						<span class="icon icon-minus"></span>
																					</div>
																					<div class="current-quantity">
																						<c:choose>
																							<c:when test="${isEdit and (catalogRefId eq prevSku)}">
																								<dsp:input bean="CartModifierFormHandler.quantity" type="text" id="quantity" priority="9" class="counter table-qty" value="${prevQnty}"/>
																							</c:when>
																							<c:otherwise>
																								<dsp:input bean="CartModifierFormHandler.quantity" type="text" id="quantity" priority="9" class="counter table-qty" value="0"/>
																							</c:otherwise>
																						</c:choose>
																					</div>
																					<div class="plus-icon">
																						<span class="icon icon-plus"></span>
																					</div>
																				</div>
																			</div>
																		</div>
																		<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].catalogRefId" value="${catalogRefId}" id="catalogRefIds-${skuIndex}" name="catalogRefIds-${skuIndex}" priority="9" class="table-sku-id" type="hidden"/>
																		<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].productId" value="${productId}" id="productId" name="productId-${skuIndex}" priority="9" class="table-product-id" type="hidden"/>
																	</dsp:oparam>
																	<dsp:oparam name="false">
																		<dsp:getvalueof var="maxQty" bean="MFFEnvironment.maxQtyPerItemInOrder" />
																		<div class="product-qty">
																			<label for="quantity">Quantity</label>
																			<div class="change-quantity" data-min="0" data-max="${maxQty}" data-quantify>
																				<div class="quantity-group">
																					<div class="minus-icon inactive">
																						<span class="icon icon-minus"></span>
																					</div>
																					<div class="current-quantity">
																						<c:choose>
																							<c:when test="${isEdit and (catalogRefId eq prevSku)}">
																								<dsp:input bean="CartModifierFormHandler.quantity" type="text" id="quantity" priority="9" class="counter table-qty" value="${prevQnty}"/>
																							</c:when>
																							<c:otherwise>
																								<dsp:input bean="CartModifierFormHandler.quantity" type="text" id="quantity" priority="9" class="counter table-qty" value="0"/>
																							</c:otherwise>
																						</c:choose>
																					</div>
																					<div class="plus-icon">
																						<span class="icon icon-plus"></span>
																					</div>
																				</div>
																			</div>
																		</div>
																		<%-- <dsp:input bean="CartModifierFormHandler.items[${skuIndex}].quantity" size="4" type="tel" class="table-qty" id="qty-${skuIndex}" name="qty-${skuIndex}" priority="9" value="${commerceItemSkuQuantityMap[catalogRefId]}"/> --%>
																		<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].catalogRefId" value="${catalogRefId}" id="catalogRefIds-${skuIndex}" name="catalogRefIds-${skuIndex}" priority="9" class="table-sku-id" type="hidden"/>
																		<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].productId" value="${productId}" id="productId" name="productId-${skuIndex}" priority="9" class="table-product-id" type="hidden"/>
																	</dsp:oparam>
																</dsp:droplet>
															</c:when>

															<c:otherwise>
																<dsp:getvalueof param="searchSKU" var="searchSKU" />
																<c:choose>
																	<c:when test="${catalogRefId == searchSKU}">
																		<c:set var="skuQtyDefault1" value="1" />
																	</c:when>
																	<c:otherwise>
																		<c:set var="skuQtyDefault1" value="0" />
																	</c:otherwise>
																</c:choose>
																<!--  <c:if test="${stockLevelForSkusOfProduct[catalogRefId].stockLevel == 0 && !inStoreOnly}">
																	<div class="alert-box error">
																		Currently out of stock
																	</div>
																</c:if>-->
																<dsp:getvalueof var="maxQty" bean="MFFEnvironment.maxQtyPerItemInOrder" />
																<div class="product-qty">
																	<label for="quantity">Quantity</label>
																	<div class="change-quantity" data-min="0" data-max="${maxQty}" data-quantify>
																		<div class="quantity-group">
																			<div class="minus-icon inactive">
																				<span class="icon icon-minus"></span>
																			</div>
																			<div class="current-quantity">
																				<c:choose>
																					<c:when test="${isEdit}">
																						<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].quantity" type="text" id="quantity" priority="9" class="counter table-qty" value="${prevQnty}" />
																					</c:when>
																					<c:otherwise>
																						<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].quantity" type="text" id="quantity" priority="9" class="counter table-qty" value="0"/>
																					</c:otherwise>
																				</c:choose>
																			</div>
																			<div class="plus-icon">
																				<span class="icon icon-plus"></span>
																			</div>
																		</div>
																	</div>
																</div>
																<%-- <dsp:input bean="CartModifierFormHandler.items[${skuIndex}].quantity" size="4" type="tel" class="table-qty" id="qty-${skuIndex}" name="qty-${skuIndex}" priority="9" value="${skuQtyDefault1}"/> --%>
																<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].catalogRefId" value="${catalogRefId}" id="catalogRefIds-${skuIndex}" name="catalogRefIds-${skuIndex}" priority="9" class="table-sku-id" type="hidden"/>
																<dsp:input bean="CartModifierFormHandler.items[${skuIndex}].productId" value="${productId}" id="productId" name="productId-${skuIndex}" priority="9" class="table-product-id" type="hidden"/>
															</c:otherwise>
														</c:choose>
													</div>
												<div class="product-inventory">
													<div class="add-to-cart-actions">
														<c:if test="${stockLevelForSkusOfProduct[catalogRefId].stockLevel == 0}">
															<div class="alert-box error">
																Currently out of stock
															</div>
														</c:if>
														<%-- ship / BOPIS radio buttons --%>
														<c:if test="${not (stockLevelForSkusOfProduct[catalogRefId].stockLevel == 0 or inStoreOnly)}">
															<c:choose>
																<c:when test="${bopisOnly}">
																	<div class="radio radio-primary ship-to-home ${hideShippingClass}">
																		<input type="radio" value="shipping-order" id="shipping-order${skuIndex}" name="order-type${skuIndex}" disabled>
																		<label for="shipping-order${skuIndex}">
																			Ship To Home
																			<span class="in-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
																		</label>
																	</div>
																</c:when>
																<c:otherwise>
																	<div class="radio radio-primary ship-to-home ${hideShippingClass}">
																		<input type="radio" value="shipping-order" id="shipping-order${skuIndex}" name="order-type${skuIndex}" ${shippingChecked}>
																		<label for="shipping-order${skuIndex}">
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
															<c:if test="${not isGiftCard and not isFFLItem and not (isEdit and !bopis) and not edsPPSOnly}">
																<div class="radio radio-primary ${hideBopisClass}">
																	<input type="radio" value="bopis-order" class="bopis-order" id="bopis-order${skuIndex}" name="order-type${skuIndex}" ${bopisChecked} />
																	<label for="bopis-order${skuIndex}">
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
															<c:set var="isAvailable" value="true" scope="request" />
															<div id="bopis-location-info${skuIndex}" class="bopis-location-info ${hideBopisClass}">
																<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
																<c:if test="${empty bopisStore}">
																	<c:choose>
																		<c:when test="${not empty param.gStoreId}">
																			<dsp:getvalueof var="bopisStore" value="${param.gStoreId}" scope="request"/>
																		</c:when>
																		<c:when test="${not empty homeStore}">
																			<dsp:getvalueof var="bopisStore" bean="Profile.myHomeStore.locationId" scope="request"/>
																		</c:when>
																	</c:choose>
																</c:if>
																<c:choose>
																	<c:when test="${empty bopisStore and empty homeStore and not inStoreOnly and not edsPPSOnly}">
																		<c:choose>
																			<c:when test="${empty param.gStoreId}">
																				<div class="bopis-section no-store ${hideBopisClass}">
																					<a href="#" class="select-store">Select Store</a>
																					to see item availability
																				</div>
																			</c:when>
																			<c:otherwise>
																				<dsp:getvalueof var="bopisStore" value="${param.gStoreId}" scope="request"/>
																				<div class="bopis-location-info ${hideBopisClass}">
																					<div class="bopis-section">
																						<div class="bopis-store-content " itemprop="offers" itemscope itemtype="http://schema.org/Offer">
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
																	<c:otherwise>
																		<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
																			<dsp:param name="id" value="${bopisStore}"/>
																			<dsp:param name="elementName" value="store"/>
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="postalCode" param="store.postalCode" scope="request"/>
																				<div class="bopis-section ${hideBopisClass}" >
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
																									<dsp:param name="skuId" value="${catalogRefId}"/>
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
																										<span class="bopis-store-unavailable">Not Available<span class="icon icon-unavailable"></span></span>
																										<span>at</span>
																										<a class="bopis-store-info update-bopis-store underlined-link" href="#">
																											<dsp:valueof param="store.city"/>,
																											<dsp:valueof param="store.stateAddress"/>
																										</a>
																									</dsp:oparam>
																								</dsp:droplet>
																							</div>
																						</c:when>
																						<c:otherwise>
																							<c:set var="displaySplitOrderMsg" value="true" />
																						</c:otherwise>
																					</c:choose>
																					<%-- 2402 - Hide these links during the edit flow. --%>
																					<c:if test="${not (isEdit and bopis)}">
																						<div class="card-links ${hideBopisClass}">
																							<c:if test="${not edsPPSOnly}">
																								<a href="#" class="disabled change-store">Change Store</a>
																							</c:if>
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
																						</div>
																					</c:if>
																				</div>
																			</dsp:oparam>
																		</dsp:droplet>
																	</c:otherwise>
																</c:choose>
															</div>
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
															<c:when test="${bopis and not bopisAvail and not inStoreOnly}">
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
																		<span class="error">
																			Sorry, this item isn't available at your current pick-up location. To find
																			an available pick-up location, select the Change Store link below.
																		</span>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<dsp:getvalueof value="${stockLevelForSkusOfProduct[catalogRefId].stockLevel}" var="stockLevel"/>
																<c:choose>
																	<%-- bug 2338: Hide out-of-stock notifications for in-store only products --%>
																	<c:when test="${stockLevel eq 0 && !inStoreOnly}">
																		<div class="radio radio-primary ship-to-home ${hideShippingClass }">
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
																		<a href="#" class="button primary disabled add-to-cart-submit disable-add-to-cart ${hideAddToCart}">Add To Cart</a>
																	</c:when>
																	<%--2427 --%>
																	<c:when test="${edsPPSOnly && bopis}">
																		<a href="#" class="${hideAddToCart} button primary disabled add-to-cart-submit <c:if test='${not isAvailable || stockLevel eq 0}'>disable-add-to-cart</c:if>">Add To Cart</a>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${isEdit}">
																				<c:choose>
																					<c:when test="${pickerType == 'TABLE'}">
																						<a href="#" class="button primary add-to-cart-submit <c:if test='${not isAvailable || stockLevel eq 0}'>disable-add-to-cart</c:if>">Update Cart</a>
																					</c:when>
																					<c:otherwise>
																						<a href="#" class="button primary update-cart-submit <c:if test='${not isAvailable || stockLevel eq 0}'>disable-add-to-cart</c:if>">Update Cart</a>
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:when test="${isFFLItem and not fflOrder}">
																				<%-- add FFL items to non-FFL order  --%>
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
																			<c:otherwise>
																				<a href="#" class="button primary disabled add-to-cart-submit ${hideAddToCart} <c:if test='${(not isAvailable && not empty bopisChecked) || stockLevel eq 0}'>disable-add-to-cart</c:if>">Add To Cart</a>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>

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

																<dsp:droplet name="Switch">
																	<dsp:param bean="Profile.hardLoggedIn" name="value"/>
																	<dsp:oparam name="true">
																		<div class="wishlist-section <c:if test='${inStoreOnly}'>wish-list-wrap</c:if>"><a href="#" class="add-to-wish-list disabled underlined-link">Add to Wish List</a></div>
																	</dsp:oparam>
																	<dsp:oparam name="false">
																		<div class="wishlist-section <c:if test='${inStoreOnly}'>wish-list-wrap</c:if>">
																			<dsp:a href="#" class="login-modal-trigger disabled underlined-link">
																				Add to Wish List
																			</dsp:a>
																		</div>
																	</dsp:oparam>
																</dsp:droplet>

																<%-- <c:if test="${itemOutOfStock && !inStoreOnly}">
																	<dsp:getvalueof var="skuIdEmail" param="productItem.childSKUs[0].id" scope="request" />
																	<div class="back-in-stock">
																		<a href="${contextPath}/browse/ajax/backInStockModal.jsp?productId=${productId}&skuId=${skuIdEmail}" class="underlined-link disabled back-in-stock-modal-trigger modal-trigger" data-target="back-in-stock-modal" data-size="small">Email Me When Back In Stock</a><br>
																	</div>
																</c:if> --%>

															</c:otherwise>
														</c:choose>
													</div>
												</div>
											</div>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
