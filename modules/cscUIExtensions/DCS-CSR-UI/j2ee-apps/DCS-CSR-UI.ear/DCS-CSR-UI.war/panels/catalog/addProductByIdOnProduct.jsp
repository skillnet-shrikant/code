<%--
 This page encodes the search results as JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/addProductByIdOnProduct.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf"%>

<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"
									var="productLookup" />
	<dsp:importbean bean="/atg/commerce/catalog/SKULookup" />
	<dsp:importbean bean="/atg/dynamo/servlet/RequestLocale" />
	<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup" />
	<dsp:importbean
			bean="/atg/commerce/custsvc/catalog/GetSKUParentProductDroplet" />
	<dsp:importbean var="agentTools"
									bean="/atg/commerce/custsvc/util/CSRAgentTools" />
	<dsp:importbean
			bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet" />
	<dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem" />
	<dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet" />
	<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
	<dsp:importbean bean="/atg/multisite/SiteContext" />
	<dsp:importbean bean="/atg/commerce/catalog/UnitOfMeasureDroplet"/>
	<dsp:getvalueof var="currentSite" bean="SiteContext.site" />
	<dsp:importbean bean="/com/mff/commerce/csr/droplet/ProductSkuIdValidator" />
	<c:if test="${empty isMultisiteEnabled}">
		<c:set var="isMultisiteEnabled" value="${agentTools.multiSiteEnabled}" />
	</c:if>
	<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
		<dsp:getvalueof param="productId" var="productId" />
		<dsp:getvalueof param="skuId" var="skuId" />

		<dsp:droplet name="SharingSitesDroplet">
			<dsp:oparam name="output">
				<dsp:getvalueof var="sites" param="sites" />
			</dsp:oparam>
		</dsp:droplet>

		<csr:getCurrencyCode>
			<c:set var="currencyCode" value="${currencyCode}" scope="request" />
		</csr:getCurrencyCode>

		<c:if test="${!empty productId}">
			
			<dsp:droplet name="ProductSkuIdValidator">
				<dsp:param name="product_id" value="${productId}" />
				<dsp:param name="product_id_present" value="1" />
				<dsp:oparam name="empty">
				</dsp:oparam>
				<dsp:oparam name="no_product_id">
				</dsp:oparam>
				<dsp:oparam name="output">
					<dsp:getvalueof var="newProductId" param="new_product_id"/>
					<c:set var="productId" value="${newProductId}" />
				</dsp:oparam>
			</dsp:droplet>
			
			<dsp:droplet name="ProductLookup">
				<dsp:param bean="RequestLocale.locale" name="repositoryKey" />
				<dsp:param name="id" param="productId" />
				<dsp:param name="sites" value="${sites}" />
				<dsp:param name="elementName" value="product" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="productItem" param="product" />
					<dsp:tomap var="product" param="product" />
					<dsp:getvalueof param="product.childSkus" var="productChildSkus" />
				</dsp:oparam>
			</dsp:droplet>
		</c:if>

		<c:if test="${!empty skuId}">
			<dsp:droplet name="ProductSkuIdValidator">
				<dsp:param name="sku_id" value="${skuId}" />
				<dsp:param name="sku_id_present" value="1" />
				<dsp:oparam name="empty">
				</dsp:oparam>
				<dsp:oparam name="no_sku_id">
				</dsp:oparam>
				<dsp:oparam name="output">
					<dsp:getvalueof var="newSkuId" param="new_sku_id"/>
					<c:set var="skuId" value="${newSkuId}" />
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="SKULookup">
				<dsp:param bean="RequestLocale.locale" name="repositoryKey" />
				<dsp:param name="id" param="skuId" />
				<dsp:param name="sites" value="${sites}" />
				<dsp:param name="elementName" value="sku" />
				<dsp:oparam name="output">
					<dsp:getvalueof param="sku" var="skuItem" />
					<dsp:tomap var="sku" param="sku" />
					<dsp:droplet name="GetSKUParentProductDroplet">
						<dsp:param name="skuId" value="${sku.id}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="productItem" param="product" />
							<dsp:getvalueof param="product" var="product" />
							<dsp:tomap var="product" param="product" />
							<dsp:getvalueof value="${product.childSkus}"
															var="productChildSkus" />
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</c:if>

		<c:if test="${not empty product}">
			<dsp:getvalueof value="${fn:length(productChildSkus)}" var="skuCount" />
			<json:object prettyPrint="${UIConfig.prettyPrintResponses}">
				<json:property name="productId" value="${product.id}" />
				<json:property name="fractionalQuantitiesAllowed" value="${product.fractionalQuantitiesAllowed}" />
				<c:choose>
					<c:when test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 5}">
						<json:property name="skuCount" value="0"/>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${!empty skuId}">
								<json:property name="skuCount" value="1" />
							</c:when>
							<c:otherwise>
								<json:property name="skuCount" value="${skuCount}" />
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				<json:property name="productName" value="${product.displayName}" />
				<c:if test="${isMultisiteEnabled}">
					<dsp:droplet name="SiteIdForCatalogItem">
						<dsp:param name="item" value="${productItem}" />
						<dsp:param name="currentSiteFirst" value="true" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="productSiteId" param="siteId" />
							<dsp:droplet name="GetSiteDroplet">
								<dsp:param name="siteId" value="${productSiteId}" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="site" var="site" />
									<dsp:tomap value="${site}" var="site" />
									<dsp:getvalueof var="siteIconURL" param="site.favicon" />
									<dsp:getvalueof var="siteIconHover" param="site.name" />
									<c:if test="${empty siteIconURL}">
										<c:set var="siteIconURL"
													 value="${CSRConfigurator.defaultSiteIconURL}" />
									</c:if>
								</dsp:oparam>

								<dsp:oparam name="empty">
									<c:if test="${!empty currentSite}">
										<dsp:tomap var="currentSiteMap" value="${currentSite}"/>
										<c:set var="siteIconURL" value="${currentSiteMap.favicon}" />
										<c:set var="siteIconHover" value="${currentSiteMap.name}" />
									</c:if>

									<c:if test="${empty siteIconURL}">
										<c:set var="siteIconURL"
													 value="${CSRConfigurator.defaultSiteIconURL}" />
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
							<json:property name="productSiteId" value="${productSiteId}" />
						</dsp:oparam>
					</dsp:droplet>
				</c:if>
				<c:choose>
					<c:when test="${!empty skuId}">
						<dsp:tomap var="sku" value="${skuItem}" />
						<json:array name="skus">
							<json:object>
								<json:property name="skuId" value="${skuId}" />
								<dsp:droplet name="UnitOfMeasureDroplet">
									<dsp:param name="product" value="${product.id}"/>
									<dsp:param name="sku" value="${skuId}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="isFractional" param="fractional"/>
									</dsp:oparam>
								</dsp:droplet>
								<json:property name="fractionalQuantitiesAllowed" value="${isFractional}" />
								<c:if test="${isMultisiteEnabled}">
									<json:property name="productSiteId" value="${productSiteId}" />
									<json:property name="siteIconURL" value="${siteIconURL}" />
									<json:property name="siteIconHover" value="${siteIconHover}" />
								</c:if>
								<json:property name="displayName"
															 value="${sku.displayName}" />
								<csr:skuPriceDisplay salePrice="displaySalePrice"
																		 listPrice="displayListPrice" product='${productItem}'
																		 sku='${skuItem}' />
								<json:property name="skuPriceEach" value="${displayListPrice}" />
								<json:property name="skuPriceEachFormatted">
									<csr:formatNumber value="${displayListPrice}" type="currency"
																		currencyCode="${currencyCode}" />
								</json:property>

								<c:if test="${displaySalePrice <  displayListPrice }">
									<json:property name="skuDiscountedPrice"
																 value="${displaySalePrice}" />
									<json:property name="skuDiscountedPriceFormatted">
										<csr:formatNumber value="${displaySalePrice}" type="currency"
																			currencyCode="${currencyCode}" />
									</json:property>
								</c:if>

								<c:if test="${empty displaySalePrice || (displaySalePrice ==  displayListPrice) }">
									<json:property name="skuDiscountedPrice" value="" />
									<json:property name="skuDiscountedPriceFormatted" value="" />
								</c:if>

								<dsp:droplet name="InventoryLookup">
									<dsp:param name="itemId" value="${sku.id}" />
									<dsp:param name="useCache" value="true" />
									<dsp:oparam name="output">
										<dsp:droplet name="Switch">
											<dsp:param name="value"
																 param="inventoryInfo.availabilityStatus" />
											<dsp:oparam name="1001">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.outOfStock" />
												</json:property>
												<json:property name="skuStatusCssClass" value="notInStock" />
												<c:set var="inventoryStatus" value="outofstock" />
											</dsp:oparam>
											<dsp:oparam name="1002">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.preorder" />
												</json:property>
												<c:set var="inventoryStatus" value="preorder" />
											</dsp:oparam>
											<dsp:oparam name="1003">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.backorder" />
												</json:property>
												<c:set var="inventoryStatus" value="backorder" />
											</dsp:oparam>
											<dsp:oparam name="1000">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.inStock" />
												</json:property>
												<c:set var="inventoryStatus" value="instock" />
											</dsp:oparam>
											<dsp:oparam name="unset">
												<json:property name="skuStatus" value="" />
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
								<c:choose>
					            	<c:when test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 7}">
										 <json:property name="fulfillmentMethod" value="BOPIS ONLY FULFILLMENT" />
					           		</c:when>  
					           		<c:otherwise>
					           			 <json:property name="fulfillmentMethod" value="" />
					           		</c:otherwise>
					            </c:choose>
								<c:choose>
					            	<c:when test="${not empty product.minimumAge}">
										 <json:property name="ageRestriction" value="YOU MUST BE AT LEAST ${product.minimumAge} YEARS OLD TO PURCHASE THIS ITEM." />
					           		</c:when>  
					           		<c:otherwise>
					           			 <json:property name="ageRestriction" value="" />
					           		</c:otherwise>
					            </c:choose>
							</json:object>
						</json:array>
					</c:when>
					<c:otherwise>
						<json:array name="skus" items="${productChildSkus}" var="skuItem">
							<dsp:tomap var="sku" value="${skuItem}" />
							<json:object>
								<json:property name="skuId" value="${sku.id}" />

								<dsp:droplet name="UnitOfMeasureDroplet">
									<dsp:param name="product" value="${product.id}"/>
									<dsp:param name="sku" value="${sku.id}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="isFractional" param="fractional"/>
									</dsp:oparam>
								</dsp:droplet>

								<json:property name="fractionalQuantitiesAllowed" value="${isFractional}" />
								<c:if test="${isMultisiteEnabled}">
									<json:property name="productSiteId" value="${productSiteId}" />
									<json:property name="siteIconURL" value="${siteIconURL}" />
									<json:property name="siteIconHover" value="${siteIconHover}" />
								</c:if>
								<json:property name="displayName"
															 value="${sku.displayName}" />
								<csr:skuPriceDisplay salePrice="displaySalePrice"
																		 listPrice="displayListPrice" product='${productItem}'
																		 sku='${skuItem}' />
								<json:property name="skuPriceEach" value="${displayListPrice}" />
								<json:property name="skuPriceEachFormatted">
									<csr:formatNumber value="${displayListPrice}" type="currency"
																		currencyCode="${currencyCode}" />
								</json:property>

								<c:if test="${displaySalePrice <  displayListPrice}">
									<json:property name="skuDiscountedPrice"
																 value="${displaySalePrice}" />
									<json:property name="skuDiscountedPriceFormatted">
										<csr:formatNumber value="${displaySalePrice}" type="currency"
																			currencyCode="${currencyCode}" />
									</json:property>
								</c:if>

								<c:if test="${empty displaySalePrice || (displaySalePrice <  displayListPrice) }">
									<json:property name="skuDiscountedPrice" value="" />
									<json:property name="skuDiscountedPriceFormatted" value="" />
								</c:if>

								<dsp:droplet name="InventoryLookup">
									<dsp:param name="itemId" value="${sku.id}" />
									<dsp:param name="useCache" value="true" />
									<dsp:oparam name="output">
										<dsp:droplet name="Switch">
											<dsp:param name="value"
																 param="inventoryInfo.availabilityStatus" />
											<dsp:oparam name="1001">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.outOfStock" />
												</json:property>
												<json:property name="skuStatusCssClass" value="notInStock" />
												<c:set var="inventoryStatus" value="outofstock" />
											</dsp:oparam>
											<dsp:oparam name="1002">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.preorder" />
												</json:property>
												<c:set var="inventoryStatus" value="preorder" />
											</dsp:oparam>
											<dsp:oparam name="1003">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.backorder" />
												</json:property>
												<c:set var="inventoryStatus" value="backorder" />
											</dsp:oparam>
											<dsp:oparam name="1000">
												<json:property name="skuStatus">
													<fmt:message
															key="global.product.availabilityStatus.inStock" />
												</json:property>
												<c:set var="inventoryStatus" value="instock" />
											</dsp:oparam>
											<dsp:oparam name="unset">
												<json:property name="skuStatus" value="" />
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
								<c:choose>
					            	<c:when test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 7}">
										 <json:property name="fulfillmentMethod" value="BOPIS ONLY FULFILLMENT" />
					           		</c:when>  
					           		<c:otherwise>
					           			 <json:property name="fulfillmentMethod" value="" />
					           		</c:otherwise>
					            </c:choose>
								<c:choose>
					            	<c:when test="${not empty product.minimumAge}">
										 <json:property name="ageRestriction" value="YOU MUST BE AT LEAST ${product.minimumAge} YEARS OLD TO PURCHASE THIS ITEM." />
					           		</c:when>  
					           		<c:otherwise>
					           			 <json:property name="ageRestriction" value="" />
					           		</c:otherwise>
					            </c:choose>
							</json:object>
						</json:array>
					</c:otherwise>
				</c:choose>
			</json:object>
		</c:if>
	</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/addProductByIdOnProduct.jsp#2 $$Change: 1179550 $--%>
