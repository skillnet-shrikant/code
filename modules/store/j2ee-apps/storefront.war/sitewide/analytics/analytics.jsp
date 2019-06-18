<dsp:page>
	<%-- analytics page loaded at body start --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />
	<dsp:importbean bean="/com/mff/droplet/MFFDynamicAttributesBySkuDroplet"/>
	
	<c:set var="title" value="${param.title}" />
	<c:set var="analyticPageType" value="${param.analyticPageType}" />
	<dsp:getvalueof var="productId" param="id" />

	<script class="analytics-script">
	<%-- digitalData: This is the basic structure of the digitalData object. It should appear on every page. If a new object needs to be added, be sure to create it first before you tryto populate it.--%>
	var digitalData = {
		"page" : {
		},
		"user" : {
		},
		"events" : []
	};

	(function () {
		digitalData.page = {
			<%-- tags common to all pages --%>
			<c:if test="${!empty analyticPageType}">
				"pageType" : "<c:out value='${analyticPageType}' />",
			</c:if>
			"pageName" : document.title
		};


		<%-- Product Detail Views --%>
		<c:if test="${analyticPageType eq 'product'}">
			digitalData.page.productID = "${productId}";
			if (!digitalData.products) {
				digitalData.products = [];
			}
							
			<dsp:droplet name="/atg/commerce/catalog/ProductLookup">
			<dsp:param name="id" value="${productId}" />
			<dsp:param name="elementName" value="product" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="product" param="product" />
				<dsp:getvalueof param="productItem.description" var="productName"/>
				<dsp:getvalueof param="productItem.brand" var="productBrand"/>
				<dsp:getvalueof var="productNameWithoutBrand" value="${fn:substringAfter(productName,productBrand)}" />
				
				<c:choose>
				<c:when test="${not empty param.Ntt}">
					<dsp:droplet name="ForEach">
					<dsp:param name="array" param="product.parentCategories"/>
					<dsp:param name="elementName" value="category"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="catName" param="category.displayName" />
					</dsp:oparam>
					</dsp:droplet>
				</c:when>
				<c:otherwise>
					<c:set var="crumbs" value="${fn:split(param.bc, '|')}" />
					<c:set var="parentCatId" value="${crumbs[fn:length(crumbs) - 1]}" />
					<c:if test="${!empty parentCatId}">
						<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
						<dsp:param name="id" value="${parentCatId}"/>
						<dsp:param name="elementName" value="category"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="catName" param="category.displayName" />
						</dsp:oparam>
						</dsp:droplet>
					</c:if>
				</c:otherwise>
				</c:choose>
				<dsp:droplet name="/atg/commerce/catalog/SKULookup">
					<dsp:param name="id" param="product.childSKUs[0].id"/>
					<dsp:param name="filterByCatalog" value="false"/>
					<dsp:param name="filterBySite" value="false"/>
					<dsp:param name="elementName" value="skuItem"/>
					<dsp:oparam name="output">
						var productVariant =	<json:object>
							<json:array name="variant">
								<dsp:droplet name="MFFDynamicAttributesBySkuDroplet">
									<dsp:param name="product" param="product" />
									<dsp:param name="sku" param="skuItem" />
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="dynAttributes"/>
											<dsp:param name="elementName" value="attributeValue"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="key" param="key"/>
												<dsp:getvalueof var="attributeValue" param="attributeValue"/>
												<json:object>
													 <json:property name='${key}'>
														'${fn:escapeXml(attributeValue)}'
													 </json:property>
												 </json:object>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</json:array>
						</json:object>
					</dsp:oparam>
				</dsp:droplet>
					
				digitalData.products.push({
					'name':'${fn:escapeXml(productNameWithoutBrand)}',
					'id': '${productId}',
					'price': '',
					'brand':'${fn:escapeXml(productBrand)}',
					'category': '${fn:escapeXml(catName)}',
					'variant': []
				});
				
				
				if (productVariant) {
					digitalData.products[0].variant = productVariant.variant;
				}
		  </dsp:oparam>
			</dsp:droplet> 
		</c:if>
		
		<%-- Order Checkout pages. --%>
		<c:if test="${analyticPageType eq 'checkout' || analyticPageType eq 'cart'}">
				<dsp:include src="analytics_order.jsp">
					<dsp:param name="analyticPageType" value="${analyticPageType}" />
				</dsp:include>
		</c:if>
	  })();
	</script>
</dsp:page>