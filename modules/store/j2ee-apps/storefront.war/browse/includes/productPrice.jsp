<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceListManager"/>
	<dsp:importbean bean="/atg/commerce/pricing/PriceRangeDroplet"/>
	
	<dsp:getvalueof var="isHidePrice" param="productItem.hidePrice"/>
	
	<c:choose>
	<c:when test="${isHidePrice eq 'true'}">
		<div class="discontinued-map-pricing-label">
			<span>Add to cart for price&nbsp;</span>
			<a class="view-details modal-trigger" href="/browse/ajax/addToCartToSeePriceModal.jsp" data-target="discontinued-item-policy-modal" data-size="small">
				details
			</a>
		</div>
	</c:when>
	<c:otherwise>
	
		<dsp:getvalueof var="skus" param="skus"/>
		
		<c:choose>
			<c:when test="${not empty skus}">
				<dsp:getvalueof var="sku" value="${skus[0]}"/>
				<dsp:getvalueof var="isClearance" param="skus[0].clearance" scope="request"/>
			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="sku" param="productItem.childSKUs[0]"/>
				<dsp:getvalueof var="isClearance" param="productItem.childSKUs[0].clearance" scope="request"/>
			</c:otherwise>
		</c:choose>
	
		<%-- 
			There are some requests, maybe from bots, that throw pricing errors in the logs.
			Adding this additional check here to suppress those errors
		 --%>
		<c:if test="${not empty skus or not empty sku}">
			<dsp:droplet name="PriceRangeDroplet">
				<dsp:param name="productId" param="productItem.repositoryId"/>
				<dsp:param name="priceList" bean="PriceListManager.defaultPriceListId"/>
				<dsp:param name="salePriceList" bean="PriceListManager.defaultSalePriceListId"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="highestPrice" param="highestPrice"/>
					<dsp:getvalueof var="lowestPrice" param="lowestPrice"/>
					<c:choose>
						<c:when test="${highestPrice ne lowestPrice}">
							<%-- if there's a price range, display it. when a user selects a sku, the actual price will be displayed --%>
							<div class="regular-price">
								<span itemprop="price" content="${lowestPrice}"><dsp:valueof param="lowestPrice" converter="currency"/></span> - <span itemprop="price" content="${highestPrice}"><dsp:valueof param="highestPrice" converter="currency"/></span>
							</div>
							<%-- Below two lines were added to pass range prices to gtm adwords tagging. Bugzilla issue# 2543. --%>
							<dsp:getvalueof var="listPrice" value="${highestPrice}" scope="request"/>
							<dsp:getvalueof var="salePrice" value="${lowestPrice}" scope="request"/>
						</c:when>
						<c:otherwise>
							<%-- there's not a price range. we need to figure out if it's a regular or sale price. --%>
							<%-- since there's no price range, the first sku should have the price of all the skus --%>
							<dsp:droplet name="PriceDroplet">
								<dsp:param name="sku" value="${sku}"/>
								<dsp:param name="priceList" bean="PriceListManager.defaultPriceListId" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="price.listPrice" var="listPrice" scope="request"/>
								</dsp:oparam>
							</dsp:droplet>
							<dsp:droplet name="PriceDroplet">
								<dsp:param name="sku" value="${sku}"/>
								<dsp:param name="priceList" bean="PriceListManager.defaultSalePriceListId" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="price.listPrice" var="salePrice" scope="request"/>
								</dsp:oparam>
							</dsp:droplet>
							<dsp:param name="salePrice" value="${salePrice}" />
							<dsp:param name="listPrice" value="${listPrice}" />
		
							<c:if test="${not empty listPrice}">
								<%-- there is a list price --%>
								<c:choose>
									<c:when test="${not empty salePrice}">
										<%-- there is a sale price --%>
										<c:choose>
											<c:when test="${listPrice ne salePrice}">
												<%-- list and sale price are different, display sale --%>
												<div class="original-price">
													<span itemprop="price" content="${listPrice}"><dsp:valueof param="listPrice" converter="currency" /></span>
												</div>
												<div class="sale-price">
													<span itemprop="price" content="${salePrice}">
														<dsp:valueof param="salePrice" converter="currency" />&nbsp;
														<c:choose>
															<c:when test="${isClearance eq 'true'}">
																<dsp:getvalueof var="showClearanceModal" param="showClearanceModal"/>
																CLEARANCE
															</c:when>
															<c:otherwise>
																SALE
															</c:otherwise>
														</c:choose>
													</span>
												</div>
												<c:if test="${showClearanceModal eq 'true'}">
													<div class="discontinued-policy-label"><span>Clearance Item Policy</span>&nbsp;<a class="view-details modal-trigger" href="/browse/ajax/discontinuedItemPolicyModal.jsp" data-target="discontinued-item-policy-modal" data-size="small"><span class="icon icon-info"></span></a></div>
												</c:if>
											</c:when>
											<c:otherwise>
												<%-- list and sale price are equal, display regular --%>
												<div class="regular-price">
													<span itemprop="price" content="${listPrice}"><dsp:valueof param="listPrice" converter="currency" /></span>
												</div>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<%-- there is only list price, display regular --%>
										<div class="regular-price">
											<span itemprop="price" content="${listPrice}"><dsp:valueof param="listPrice" converter="currency" /></span>
										</div>
									</c:otherwise>
								</c:choose>
							</c:if>
		
						</c:otherwise>
					</c:choose>
				</dsp:oparam>
			</dsp:droplet>
		</c:if>
	
		</c:otherwise>
	</c:choose>
</dsp:page>
