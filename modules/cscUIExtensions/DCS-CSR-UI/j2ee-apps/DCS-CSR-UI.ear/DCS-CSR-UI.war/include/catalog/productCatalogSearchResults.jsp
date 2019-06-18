<%--
 This page encodes the search results as JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productCatalogSearchResults.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
<dsp:importbean bean="/atg/commerce/custsvc/catalog/CustomCatalogProductSearch" var="customCatalogProductSearch"/>
<dsp:importbean bean="/atg/commerce/custsvc/catalog/ProductSearch" var="productSearch"/>
<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
<dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
<dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem"/>
<dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="agentTools"/>
<dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/> 
<dsp:getvalueof var="currentSiteId" bean="Site.id"/>

<dsp:getvalueof param="orderIsModifiable" var="orderIsModifiable"/>
<dsp:getvalueof param="isSearching" var="isSearching"/>

<c:choose>
  <c:when test="${orderIsModifiable}">
    <c:set var="orderIsModifiableDisableAttribute" value=""/>
  </c:when>
  <c:otherwise>
    <c:set var="orderIsModifiableDisableAttribute" value=" disabled='disabled' "/>
  </c:otherwise>
</c:choose>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <c:set var="useCustomCatalogs" value="${CSRConfigurator.customCatalogs}"/>

  <csr:getCurrencyCode>
   <c:set var="currencyCode" value="${currencyCode}" scope="request" />
  </csr:getCurrencyCode> 

  <c:set var="productSearchBean" value="ProductSearch"/>
  <c:if test="${useCustomCatalogs}">
    <c:set var="productSearch" value="${customCatalogProductSearch}"/>
    <c:set var="productSearchBean" value="CustomCatalogProductSearch"/>
  </c:if>
  <dsp:getvalueof bean="${productSearchBean}.isCatalogBrowsing" var="isCatalogBrowsing"/>
  <c:choose>
    <c:when test="${isCatalogBrowsing}">
      <dsp:droplet name="SharingSitesDroplet">
        <dsp:oparam name="output">
          <dsp:getvalueof var="sites" param="sites"/>
        </dsp:oparam>
      </dsp:droplet>

      <dsp:droplet name="CategoryLookup">
        <dsp:param bean="RequestLocale.locale" name="repositoryKey"/>
        <dsp:param name="id" value="${productSearch.hierarchicalCategoryId}"/>
        <dsp:param name="sites" value="${sites}"/>
        <dsp:param name="elementName" value="category"/>
        <dsp:oparam name="output">
          <dsp:tomap param="category" var="category"/>
          <c:if test="${!empty category.childProducts}">
            <c:set var="searchResults" value="${category.childProducts}"/>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
      <c:set var="resultSetSize" value="${fn:length(searchResults)}"/>
    </c:when>
    <c:otherwise>
      <dsp:getvalueof bean="${productSearchBean}.resultSetSize" var="resultSetSize"/>
      <dsp:getvalueof bean="${productSearchBean}.searchResults" var="searchResults"/>
    </c:otherwise>
  </c:choose>
  <json:object prettyPrint="${UIConfig.prettyPrintResponses}" escapeXml="false">
    <c:choose>
      <c:when test="${!empty searchResults}">
        <json:property name="resultLength" value="${resultSetSize}"/>
        <json:array name="results" items="${searchResults}" var="element">
          <dsp:tomap value="${element}" var="product"/>
          <dsp:tomap value="${product.smallImage}" var="smallImage"/>
          <dsp:tomap value="${product.childSKUs}" var="childSKUs"/>
          <c:if test="${empty childSKUs.baseList[1]}">
            <dsp:tomap value="${childSKUs.baseList[0]}" var="singleSKU"/>
          </c:if>
          <json:object>
            <c:choose>
              <c:when test="${isSearching}">
                <json:property name="siteIcon">
                  <dsp:droplet name="SiteIdForCatalogItem">
                    <dsp:param name="item" value="${element}"/>
                    <dsp:getvalueof bean="${productSearchBean}.catalogIdentifierSiteIds" var="productSearchBeanSites"/>
                    <dsp:param name="siteId" value="${(!empty productSearchBeanSites) ? productSearchBeanSites[0] : null}"/>
                    <dsp:param name="currentSiteFirst" value="true"/>
                    <dsp:oparam name="output">
                    <dsp:getvalueof param="siteId" var="siteId"/>
                    <csr:siteIcon siteId="${siteId}" />
                    </dsp:oparam>
                  </dsp:droplet>
                </json:property>
              </c:when>
              <c:otherwise>
                <c:set var="siteId" value="" />
              </c:otherwise>
            </c:choose>
            <c:choose>
              <c:when test="${!empty smallImage.url}">
                <json:property name="image">
                  <a onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');return false;" href="#"><img src="${fn:escapeXml(smallImage.url)}" alt="${fn:escapeXml(product.displayName)}" border="0"  height="60"/></a>
                </json:property>
              </c:when>
              <c:otherwise>
                <c:url context='/agent' value='/images/icon_confirmationLarge.gif' var="defaultImageURL"/>
                <json:property name="image">
                  <a onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');return false;" href="#"><img src="${defaultImageURL}" alt="${fn:escapeXml(product.displayName)}" border="0"  height="60"/></a>
                </json:property>
              </c:otherwise>
            </c:choose>
              
            <json:property name="productId" value="${fn:escapeXml(product.description)}<BR>Online Item #: ${fn:escapeXml(product.id)}"/>

            <c:choose>
              <c:when test="${empty childSKUs.baseList[1]}">
                <json:property name="sku" value="${fn:escapeXml(singleSKU.id)}"/>
              </c:when>
              <c:otherwise>
                <json:property name="sku" value="&nbsp;"/>
              </c:otherwise>
            </c:choose>
            <json:property name="name">
              <a onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');return false;" href="#">${fn:escapeXml(product.displayName)}</a>
            </json:property>
              
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
           
            <c:choose>
              <c:when test="${1 == fn:length(product.childSKUs)}">
                <csr:skuPriceDisplay salePrice="displaySalePrice" listPrice="displayListPrice" product="${element}" sku="${product.childSKUs[0]}"/>
                <c:if test="${displaySalePrice <  displayListPrice}">
                  <json:property name="priceRange">
                    <span class="atg_commerce_csr_common_content_strikethrough"><csr:formatNumber value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/></span>&nbsp;<csr:formatNumber value="${displaySalePrice}" type="currency" currencyCode="${currencyCode}"/>
                  </json:property>
                </c:if>
                <c:if test="${empty displaySalePrice || (displaySalePrice ==  displayListPrice) }">
                  <json:property name="priceRange">
                    <csr:formatNumber value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/>
                  </json:property>
                </c:if>
              </c:when>
              <c:otherwise>
                <csr:priceRange lowPrice="lowestPrice" highPrice="highestPrice" productId='${product.repositoryId}'/>
                <c:choose>
                  <c:when test="${lowestPrice eq highestPrice and empty lowestPrice}">
                    <json:property name="priceRange">
                      <fmt:message key="catalogBrowse.searchResults.noPrice"/>
                    </json:property>
                  </c:when>
                  <c:when test="${lowestPrice eq highestPrice and !empty lowestPrice}">
                    <json:property name="priceRange">
                      <csr:formatNumber value="${lowestPrice}" type="currency" currencyCode="${currencyCode}"/>
                    </json:property>
                  </c:when>
                  <c:otherwise>
                    <json:property name="priceRange">
                      <csr:formatNumber value="${lowestPrice}" type="currency" currencyCode="${currencyCode}"/>${empty rangeSeparator ? "-" : rangeSeparator}<csr:formatNumber value="${highestPrice}" type="currency" currencyCode="${currencyCode}"/>
                    </json:property>
                  </c:otherwise>
                </c:choose>
              </c:otherwise>
            </c:choose>
            <dsp:getvalueof var="giftCardProduct" bean="/atg/commerce/catalog/CatalogTools.giftCardProductId"/>
            <c:choose>
              <c:when test="${empty childSKUs.baseList[1] && product.id != giftCardProduct}">
                <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
                  <dsp:param name="product" value="${product.id}"/>
                  <dsp:param name="sku" value="${singleSKU.id}"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="isFractional" param="fractional"/>
                  </dsp:oparam>
                </dsp:droplet>
                <fmt:message key="catalogBrowse.searchResults.productAddedToOrder.js" var="addToOrderMsg"><fmt:param value="${fn:escapeXml(product.displayName)}"/></fmt:message>
                <c:set var="addToOrderMsg_search" value="&#039;"/>
                <c:set var="addToOrderMsg_replace" value="&#092;&#039;"/>
                <c:set var="addToOrderMsg" value="${fn:replace(addToOrderMsg,addToOrderMsg_search,addToOrderMsg_replace)}"/>
                <fmt:message key="genericRenderer.invalidQuantityMessage" var="addToOrderInvalidQuantityMsg"/>
                <c:set var="addToOrderInvalidQuantityMsg_search" value="&#039;"/>
                <c:set var="addToOrderInvalidQuantityMsg_replace" value="&#092;&#039;"/>
                <c:set var="addToOrderInvalidQuantityMsg" value="${fn:replace(addToOrderInvalidQuantityMsg,addToOrderInvalidQuantityMsg_search,addToOrderInvalidQuantityMsg_replace)}"/>
                <json:property name="qty">
                  <c:set var="maxLength" value="5"/>
                  <c:set var="size" value="5"/>
                  <c:if test="${isFractional eq true}">
                    <c:set var="maxLength" value="9"/>
                  </c:if>
				  <c:choose>
            		<c:when test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 5}">
						Available in select stores only&nbsp;
            		</c:when>
            		<c:otherwise>
                  		<input maxlength="${maxLength}" size="${size}" type="text" onkeyup="if (event.keyCode==13){dojo.byId('itemQuantity${status.count}${fn:escapeXml(product.id)}_button').click();}" id="itemQuantity${status.count}${fn:escapeXml(product.id)}" <c:out value="${orderIsModifiableDisableAttribute}"/>/>
          			</c:otherwise>
          		  </c:choose>
                </json:property>
                <fmt:message key='catalogBrowse.searchResults.buy' var="buyButtonLabel"/>
                <json:property name="actions">
                  <input id="itemQuantity${status.count}${fn:escapeXml(product.id)}_button" type="button" name="submit" value="${fn:escapeXml(buyButtonLabel)}" onclick="atg.commerce.csr.catalog.addItemToOrder('${status.count}${fn:escapeXml(product.id)}', '${fn:escapeXml(singleSKU.id)}', '${fn:escapeXml(product.id)}', '${addToOrderMsg}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(isFractional)}', '${addToOrderInvalidQuantityMsg}');" <c:out value="${orderIsModifiableDisableAttribute}"/>/>
                </json:property>
              </c:when>
              <c:otherwise>
                <json:property name="qty" value="&nbsp;"/>
                <fmt:message key='catalogBrowse.searchResults.view' var="viewButtonLabel"/>
                <json:property name="actions">
                  <input type="button" name="submit" value="${fn:escapeXml(viewButtonLabel)}" onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}')"/>
                </json:property>
              </c:otherwise>
            </c:choose>
          </json:object>
        </json:array>
      </c:when>
      <c:otherwise>
        <json:property name="resultLength" value="${0}"/>
      </c:otherwise>
    </c:choose>
  </json:object>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productCatalogSearchResults.jsp#2 $$Change: 1179550 $--%>
