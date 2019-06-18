<%--
 A page fragment that displays product image, name, ID, description
 and price range. Used on the ProductView panel.

 This page requires either a productId or a commerceItemId parameter

 @param productId - The ID of the product to display
 @param siteId - The ID of the site
 @param commerceItemId - The commerce item ID of the item in the current order
 @param hidePrice - parameter to decide whether to display price or not.
                    if this parameter is provided and it is true, then price 
                    will not be displayed. 

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/productInformation.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/userprofiling/Profile" var="profile"/>
    <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet" />
    <dsp:importbean bean="/com/mff/browse/droplet/ProductImageDroplet"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/com/mff/commerce/csr/droplet/ProductSkuIdValidator" />
	
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
	  <dsp:getvalueof var="productImageRoot" bean="MFFEnvironment.productImageRoot"/>
	  <dsp:getvalueof var="siteHttpServerName" bean="MFFEnvironment.siteHttpServerName"/>
      <dsp:getvalueof var="productId" param="productId"/>
      <dsp:getvalueof var="commerceItemId" param="commerceItemId"/>
      <dsp:getvalueof var="hidePrice" param="hidePrice"/>
      <dsp:getvalueof var="siteId" param="siteId"/>
	  
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
      
    <dsp:importbean bean="/atg/dynamo/droplet/multisite/SiteContextDroplet"/>
    <c:set var="validSiteId" value="${false}"/>
    <%-- if the siteId is null or site is deleted, then use the empty site context. --%> 
    <c:if test="${not empty siteId}">
     <dsp:droplet name="GetSiteDroplet">
      <dsp:param name="siteId" value="${siteId}" />
      <dsp:oparam name="output">
       <c:set var="validSiteId" value="${true}"/>
      </dsp:oparam>
      </dsp:droplet>    
    </c:if> 
    
    <dsp:droplet name="SiteContextDroplet">
      <dsp:param name="siteId" param="siteId" />
      <dsp:param name="emptySite" value="${!validSiteId}" />
      <dsp:oparam name="output">
        <csr:getProduct productId="${productId}" commerceItemId="${commerceItemId}">
          <div class="atg_commerce_csr_productViev">
            <c:if test="${not empty product}">
              <dsp:tomap var="productMap" value="${product}"/>
              <dsp:tomap var="image" value="${productMap.smallImage}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_currSkuId" value=""/>
              <!--<dsp:img src="${image.url}" id="atg_commerce_csr_catalog_product_info_image"
                iclass="atg_commerce_csr_ProductViewImg" />
              	<dsp:img src="http://www.fleetfarm.com/products/images/small/${productId}.jpg" id="atg_commerce_csr_catalog_product_info_image"
                iclass="atg_commerce_csr_ProductViewImg" /> -->
                
	            <dsp:droplet name="ProductImageDroplet">
					<dsp:param name="productId" value="${productId}" />
					<dsp:oparam name="output">
					<%-- main image --%>
					<dsp:getvalueof var="defaultImage" param="productImages[0]" />
					<dsp:img src="https://${siteHttpServerName}${productImageRoot}/${productId}/l/${defaultImage}" id="atg_commerce_csr_catalog_product_info_image"
	                iclass="atg_commerce_csr_ProductViewImg" />
						
				 	</dsp:oparam>
				</dsp:droplet>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_image" value="${image.url}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_display_name" value="${fn:escapeXml(productMap.displayName)}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_description" value="${fn:escapeXml(productMap.description)}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_repository_id" value="${fn:escapeXml(productMap.repositoryId)}"/>
              <c:if test="${hidePrice != true}">
              <c:set var="productPriceRange">
                <dsp:include src="/include/catalog/displayProductPriceRange.jsp" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="productToPrice" value="${product}"/>
                </dsp:include>
              </c:set>
              <div style="display:none" id="atg_commerce_csr_catalog_product_info_product_price">${productPriceRange}</div>
              </c:if>              
              <dl class="atg_commerce_csr_catalog_product_info_layout">
                <dt id="atg_commerce_csr_catalog_product_info_display_name" class="atg_commerce_csr_productVievTitle">
                  ${fn:escapeXml(productMap.description)}
                </dt>
                <dd id="atg_commerce_csr_catalog_product_info_repository_id" class="atg_commerce_csr_productVievID">
                  Online Item #: ${fn:escapeXml(productMap.repositoryId)}
                </dd>
                 <c:if test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 7}">
				 <dd id="atg_commerce_csr_catalog_product_info_description" class="atg_commerce_csr_productVievDesc">
                	<span style="font-weight:bold"><font  color="0000FF">BOPIS ONLY FULFILLMENT</font></span>
                 </dd>
                </c:if>
                <c:if test="${not empty product.minimumAge}">
				 <dd id="atg_commerce_csr_catalog_product_info_description" class="atg_commerce_csr_productVievDesc">
                	<span style="font-weight:bold"><font  color="FF0000">YOU MUST BE AT LEAST ${product.minimumAge} YEARS OLD TO PURCHASE THIS ITEM.</font></span>
                 </dd>
                </c:if>
                <dd id="atg_commerce_csr_catalog_product_info_description" class="atg_commerce_csr_productVievDesc">
                	<c:set var="splitSellingPoints" value="${fn:split(product.sellingPoints,'^;')}"/>
					<c:forEach items="${splitSellingPoints}" var="sellingPoint">
						- ${fn:escapeXml(sellingPoint)}<BR>
					</c:forEach>
                </dd>
                <c:if test="${hidePrice != true}">
                 <dd id="atg_commerce_csr_catalog_product_info_price" class="atg_commerce_csr_productVievPrice">
                   ${productPriceRange}<BR>
                 </dd>
                </c:if>
                <c:if test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 5}">
				 <dd id="atg_commerce_csr_catalog_product_info_description" class="atg_commerce_csr_productVievDesc">
                	Available in select stores only
                 </dd>
                </c:if>
                <c:if test="${not empty product.madeInUsa && product.madeInUsa == 'true'}">
				 <dd id="atg_commerce_csr_catalog_product_info_description" class="atg_commerce_csr_productVievDesc">
                	<dsp:img src="/DCS-CSR/images/mff/MadeInUSAFlag.jpg"/>
                 </dd>
                </c:if>
              </dl>
            </c:if>
          </div>
        </csr:getProduct>
      </dsp:oparam>
      
      <dsp:oparam name="error">
        <csr:getProduct productId="${productId}" commerceItemId="${commerceItemId}">
          <div class="atg_commerce_csr_productViev">
            <c:if test="${not empty product}">
              <dsp:tomap var="productMap" value="${product}"/>
              <dsp:tomap var="image" value="${productMap.smallImage}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_currSkuId" value=""/>
              <dsp:img src="${image.url}" id="atg_commerce_csr_catalog_product_info_image"
                iclass="atg_commerce_csr_ProductViewImg" />
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_image" value="${image.url}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_display_name" value="${fn:escapeXml(productMap.displayName)}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_description" value="${fn:escapeXml(productMap.description)}"/>
              <input type="hidden" id="atg_commerce_csr_catalog_product_info_product_repository_id" value="${fn:escapeXml(productMap.repositoryId)}"/>
              <c:if test="${hidePrice != true}">
              <c:set var="productPriceRange">
                <dsp:include src="/include/catalog/displayProductPriceRange.jsp" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="productToPrice" value="${product}"/>
                </dsp:include>
              </c:set>
              <div style="display:none" id="atg_commerce_csr_catalog_product_info_product_price">${productPriceRange}</div>
              </c:if>              
              <dl class="atg_commerce_csr_catalog_product_info_layout">
                <dt id="atg_commerce_csr_catalog_product_info_description" class="atg_commerce_csr_productVievDesc">
                  ${fn:escapeXml(productMap.description)}
                </dt>
                <dd id="atg_commerce_csr_catalog_product_info_repository_id" class="atg_commerce_csr_productVievID">
                  ${fn:escapeXml("Online Item #: " + productMap.repositoryId)}
                </dd>
                <dd id="atg_commerce_csr_catalog_product_info_display_name" class="atg_commerce_csr_productVievTitle">
					<c:set var="splitSellingPoints" value="${fn:split(product.sellingPoints,'^;')}"/>
					<c:forEach items="${splitSellingPoints}" var="sellingPoint">
						- ${fn:escapeXml(sellingPoint)}<BR>
					</c:forEach>
                </dd>
                <c:if test="${hidePrice != true}">
                 <dd id="atg_commerce_csr_catalog_product_info_price" class="atg_commerce_csr_productVievPrice">
                   ${productPriceRange}
                 </dd>
                </c:if>
              </dl>
            </c:if>
          </div>
        </csr:getProduct>
      </dsp:oparam>      
      
    </dsp:droplet>
      
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <% 
    Exception ee = (Exception) pageContext.getAttribute("exception"); 
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/productInformation.jsp#1 $$Change: 946917 $--%>
