<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	 <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
	<dsp:importbean bean="/com/mff/droplet/MFFDynamicAttributesBySkuDroplet"/>
	<dsp:importbean bean="/com/mff/commerce/order/purchase/LineItemTotalPriceDroplet"/>

	<dsp:getvalueof var="analyticPageType" param="analyticPageType" />
	<dsp:param name="order" bean="ShoppingCart.current" />

	<%-- track checkout steps --%>
	<c:choose>
		<c:when  test="${analyticPageType == 'cart'}">
			digitalData.order = {
				"products" : []
			};
		</c:when>	
		<c:otherwise>
			<dsp:getvalueof var="checkoutStep" value="1" />
			<dsp:getvalueof var="isBopisOrder" param="order.bopisOrder" />
			<c:choose>
				<c:when test="${isBopisOrder}">
					<dsp:getvalueof var="checkoutOption" value="Store Pick-Up" />
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="checkoutOption" value="Shipping" />
				</c:otherwise>
			</c:choose>
			
			digitalData.ecommerce = {
				checkout : []
			}
	
			digitalData.ecommerce.checkout = {
				"actionField" : {'step': '${checkoutStep}', 'option': '${checkoutOption}'},
				"products" : []
			};
		</c:otherwise>
	</c:choose>
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="order.commerceItems"/>
		<dsp:param name="elementName" value="commerceItem"/>
		<dsp:oparam name="output">

			<dsp:droplet name="/atg/dynamo/droplet/Compare">
				<dsp:param name="obj1" param="commerceItem.priceInfo.salePrice" />
				<dsp:param name="obj2" param="commerceItem.priceInfo.listPrice" />
				<dsp:oparam name="lessthan">
					<dsp:getvalueof var="actualPrice" param="commerceItem.priceInfo.salePrice" scope="request" />
				</dsp:oparam>
				<dsp:oparam name="default">
					<dsp:getvalueof var="actualPrice" param="commerceItem.priceInfo.listPrice" scope="request" />
				</dsp:oparam>
			</dsp:droplet>
			
			<dsp:getvalueof var="index" param="index" />
			<dsp:getvalueof var="productName" param="commerceItem.auxiliaryData.productRef.description" />
			<dsp:getvalueof var="productId" param="commerceItem.auxiliaryData.productId" />
			<dsp:getvalueof var="productBrand" param="commerceItem.auxiliaryData.productRef.brand" />
			<dsp:getvalueof var="productItem" param="commerceItem.auxiliaryData.productRef" />
			<dsp:getvalueof var="skuItem" param="commerceItem.auxiliaryData.catalogRef" />
			
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="commerceItem.auxiliaryData.productRef.parentCategories"/>
				<dsp:param name="elementName" value="category"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="catName" param="category.displayName" />
				</dsp:oparam>
			</dsp:droplet>
			var productVariant =	<json:object>
				<json:array name="variant">
					<dsp:droplet name="MFFDynamicAttributesBySkuDroplet">
						<dsp:param name="product" param="commerceItem.auxiliaryData.productRef" />
						<dsp:param name="sku" param="commerceItem.auxiliaryData.catalogRef" />
						<dsp:oparam name="output">
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="dynAttributes"/>
								<dsp:param name="elementName" value="attributeValue"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="key" param="key"/>
									<dsp:getvalueof var="attributeValue" param="attributeValue"/>
									<json:object>
										 <json:property name='${key}'>
											${attributeValue}
										 </json:property>
									 </json:object>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</json:array>
			</json:object>
			
			<dsp:droplet name="LineItemTotalPriceDroplet">
				<dsp:param name="commerceItem" param="commerceItem"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="totalLinePrice" param="totalLinePrice"/>
					<dsp:getvalueof var="itemPromos" param="lineItemPromos"/>
				</dsp:oparam>
			</dsp:droplet>
	
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" value="${itemPromos}"/>
				<dsp:param name="elementName" value="promoItem"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="promoDisplayName" param="promoItem.displayName" idtype="java.lang.String" />
					<dsp:getvalueof var="shortDescription" param="promoItem.shortDescription" idtype="java.lang.String" />
				</dsp:oparam>
			</dsp:droplet>
			<c:choose>
				<c:when  test="${analyticPageType == 'cart'}">
					digitalData.order.products.push({
						'name':'${fn:escapeXml(productName)}',
						'id': '${productId}',
						'price': <c:out value="${actualPrice}" />,
						'brand':'${fn:escapeXml(productBrand)}',
						'category': '${fn:escapeXml(catName)}',
						'variant': [],
						'quantity': <dsp:valueof param="commerceItem.quantity"/>
					});
					
					if (productVariant) {
						digitalData.order.products[${index}].variant = productVariant.variant;
					}
				</c:when>
				<c:otherwise>
					digitalData.ecommerce.checkout.products.push({
						'name':'${fn:escapeXml(productName)}',
						'id': '${productId}',
						'price': <c:out value="${actualPrice}" />,
						'brand':'${fn:escapeXml(productBrand)}',
						'category': '${fn:escapeXml(catName)}',
						'variant': [],
						'sku': '<dsp:valueof param="commerceItem.catalogrefId" />',
						'quantity': <dsp:valueof param="commerceItem.quantity"/>,
						'coupon': '${promoDisplayName}'
					});
					if (productVariant) {
						digitalData.ecommerce.checkout.products[${index}].variant = productVariant.variant;
					}
				</c:otherwise>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>
	<c:if  test="${analyticPageType == 'checkout'}">
		digitalData.events.push({
			event: 'checkout',
			ecommerce: digitalData.ecommerce
		});
	</c:if>
	
</dsp:page>