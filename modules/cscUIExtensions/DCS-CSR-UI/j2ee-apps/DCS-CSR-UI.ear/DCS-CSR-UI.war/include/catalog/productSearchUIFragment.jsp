<%--
 This UI fragment defines the Product Search Panel that is shipped with the product, 
 but may be replaced by a customer at their discretion. Customers need simply point the configuration file
 to the new JSP snippet for this to take effect.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productSearchUIFragment.jsp#2 $
 @updated $DateTime: 2015/02/26 10:47:28 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/ProductSearch" var="productSearch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/CustomCatalogProductSearch" var="customCatalogProductSearch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/GetCatalogDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean var="currentSite" bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

    <c:set var="useCustomCatalogs" value="${CSRConfigurator.customCatalogs}"/>
    <c:set var="productSearchBean" value="ProductSearch"/>
    <c:if test="${useCustomCatalogs}">
      <c:set var="productSearch" value="${customCatalogProductSearch}"/>
      <c:set var="productSearchBean" value="CustomCatalogProductSearch"/>
    </c:if>
    
    <div class="atg-csc-base-table-row">
      <dsp:droplet name="SharingSitesDroplet">
        <dsp:oparam name="output">
          <dsp:getvalueof var="sites" param="sites"/>
            <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label">
              <label for="sku">
                <fmt:message key="catalogBrowse.findProducts.site"/>
                <fmt:message key="text.colon"/>
              </label>
            </span>
            <div class="atg-csc-base-table-cell">
              <dsp:input id="sitesSelectValue" name="sitesSelectValue" bean="${productSearchBean}.catalogIdentifierSiteIds" type="hidden" converter="array" value=""/>
              <c:choose>
                <c:when test ="${envTools.siteAccessControlOn == 'true' }">
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="sites"/>
                    <dsp:param name="sortProperties" value="+cscDisplayPriority, +name"/>
                    <dsp:setvalue param="site" paramvalue="element"/>
                    
                    <dsp:oparam name="empty">
                    
                    </dsp:oparam>
                    
                    <dsp:oparam name="outputStart">
                      <select id="siteSelect" class="atg-base-table-product-catalog-search-input" onchange="atg.commerce.csr.catalog.selectSite('<fmt:message key="catalogBrowse.findProducts.allCategories"/>')">
                      <option value="${sites}">
                        <fmt:message key="catalogBrowse.findProducts.allSites"/>
                      </option>
                    </dsp:oparam>
                    
                    <dsp:oparam name="output">
                      <dsp:getvalueof param="site.id" var="siteId"/>
                      <dsp:droplet name="IsSiteAccessibleDroplet">
                        <dsp:param name="siteId" value="${siteId}"/>
                        <dsp:oparam name="true">
                          <option value="${siteId}">
                            <dsp:valueof param="site.name"/>
                          </option>
                        </dsp:oparam>
                       </dsp:droplet>
                    </dsp:oparam>
                    
                    <dsp:oparam name="outputEnd">
                      </select>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:when>
                <c:otherwise>
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="sites"/>
                    <dsp:param name="sortProperties" value="+cscDisplayPriority, +name"/>
                    <dsp:setvalue param="site" paramvalue="element"/>
                  
                      <dsp:oparam name="empty">
                  
                      </dsp:oparam>
                  
                      <dsp:oparam name="outputStart">
                        <select id="siteSelect" class="atg-base-table-product-catalog-search-input" onchange="atg.commerce.csr.catalog.selectSite('<fmt:message key="catalogBrowse.findProducts.allCategories"/>')">
                        <option value="${sites}">
                          <fmt:message key="catalogBrowse.findProducts.allSites"/>
                        </option>
                      </dsp:oparam>
                  
                      <dsp:oparam name="output">
                        <dsp:getvalueof param="site.id" var="siteId"/>
                        <option value="${siteId}">
                          <dsp:valueof param="site.name"/>
                        </option>
                      </dsp:oparam>
                  
                      <dsp:oparam name="outputEnd">
                        </select>
                      </dsp:oparam>
                  </dsp:droplet>
                </c:otherwise>
              </c:choose>
            </div>
        </dsp:oparam>
      </dsp:droplet>


      <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label">
        <label for="sku">
          <fmt:message key="catalogBrowse.findProducts.category"/>
          <fmt:message key="text.colon"/>
        </label>
      </span>
      <div class="atg-csc-base-table-cell">
        <dsp:input type="hidden" bean="${productSearchBean}.hierarchicalCategoryId" value="" name="hierarchicalCategoryId" id="hierarchicalCategoryId" />
        <select id="categorySelect" class="atg-base-table-product-catalog-search-input">
          <option value="">
            <fmt:message key="catalogBrowse.findProducts.allCategories"/>
          </option>
          <c:if test="${!isMultiSiteEnabled || empty currentSite.id}">
            <dsp:droplet name="GetCatalogDroplet">
              <dsp:setvalue param="catalog" paramvalue="element"/>
              <dsp:oparam name="output">
                <dsp:getvalueof param="catalog.rootCategories" var="rootCategories"/>
                <c:forEach items="${rootCategories}" var="category">
                  <dsp:tomap var="category" value="${category}" />
                  <option value="${category.id}">
                    ${category.displayName}
                  </option>
                </c:forEach>
              </dsp:oparam>
              <dsp:oparam name="empty">
              </dsp:oparam>
            </dsp:droplet>
          </c:if>
        </select>
      </div>
    </div>
    <!-- <div style="width: 100px;">&nbsp;</div> -->
    <div class="atg_commerce_csr_productId atg-csc-base-table-row">
      <c:if test="${CSRConfigurator.useProductId}">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label">
          <label for="productID">
            <fmt:message key="catalogBrowse.findProducts.productId"/>
            <fmt:message key="text.colon"/>
          </label>
        </span>
        <div class="atg-csc-base-table-cell">
          <dsp:input bean="${productSearchBean}.propertyValues.id"
                     id="productID"
                     name="productID"
                     type="text"
                     iclass="atg_commerce_csr_formTextField atg-base-table-product-catalog-search-input"
                     maxlength="25"
                     size="25"/>
        </div>
      </c:if>
      <c:if test="${CSRConfigurator.useSKUId}">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label">
          <label for="sku">
            <fmt:message key="catalogBrowse.findProducts.sku"/>
            <fmt:message key="text.colon"/>
          </label>
        </span>
        <div class="atg-csc-base-table-cell">
          <dsp:input bean="${productSearchBean}.sku"
                     id="sku"
                     name="sku"
                     type="text"
                     iclass="atg_commerce_csr_formTextField atg-base-table-product-catalog-search-input"
                     maxlength="25"
                     size="25"/>
        </div>
      </c:if>
    </div>
    <c:choose>
      <c:when test="${!CSRConfigurator.usingPriceLists}">
        <div class="atg-csc-base-table-row">
          <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label">
            <label for="itemPrice">
              <fmt:message key="catalogBrowse.findProducts.itemPrice"/>
              <fmt:message key="text.colon"/>
            </label>
          </span>
          <div class="atg-csc-base-table-cell">
            <dsp:getvalueof bean="${productSearchBean}.priceRelation" var="lastPriceRelation"/>
            <dsp:select bean="${productSearchBean}.priceRelation" id="priceRelation" name="priceRelation">
              <dsp:option value=" "> </dsp:option>
              <dsp:option value="=" selected="${lastPriceRelation == '=' ? true : false}">
                <fmt:message key="catalogBrowse.findProducts.priceRange.equal"/>
              </dsp:option>
              <dsp:option value="<" selected="${lastPriceRelation == '<' ? true : false}">
                <fmt:message key="catalogBrowse.findProducts.priceRange.lt"/>
              </dsp:option>
              <dsp:option value=">" selected="${lastPriceRelation == '>' ? true : false}">
                <fmt:message key="catalogBrowse.findProducts.priceRange.gt"/>
              </dsp:option>
              <dsp:option value="<=" selected="${lastPriceRelation == '<=' ? true : false}">
                <fmt:message key="catalogBrowse.findProducts.priceRange.ltOrEqual"/>
              </dsp:option>
              <dsp:option value=">=" selected="${lastPriceRelation == '>=' ? true : false}">
                <fmt:message key="catalogBrowse.findProducts.priceRange.gtOrEqual"/>
              </dsp:option>
            </dsp:select>
            <dsp:input bean="${productSearchBean}.price"
                        id="itemPrice"
                        name="itemPrice"
                        type="text"
                        iclass="atg_commerce_csr_formTextField atg-base-table-product-catalog-search-input"
                        maxlength="10"
                        size="10"/>
          </div>
          <span id="ea_csc_product_item_price"></span>
        </div>
      </c:when>
      <c:otherwise>
        <!-- <dt class="emptydt" style="width:450px;height:1px;"></dt> -->
      </c:otherwise>
    </c:choose>
        <!-- <div style="width: 100px;">&nbsp;</div> -->
    <div class="atg_commerce_csr_searchDescription atg-csc-base-table-row">
      <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label">
        <label for="description">
          <fmt:message key="catalogBrowse.findProducts.description"/>
          <fmt:message key="text.colon"/>
        </label>
      </span>
      <div class="atg-csc-base-table-cell">
        <dsp:input bean="${productSearchBean}.searchInput"
                     id="searchInput"
                     name="searchInput"
                     type="text"
                     iclass="atg_commerce_csr_formTextField atg-base-table-product-catalog-search-input atg-base-table-product-catalog-search-length"
                     maxlength="25"
                     size="25"/>
      </div>
    </div>
  </dsp:layeredBundle>
  <script type="text/javascript">
    _container_.onLoadDeferred.addCallback(function () {
      if (document.getElementById("categorySelect") && document.getElementById("siteSelect") && document.getElementById("siteSelect").selectedIndex == 0) {
        document.getElementById("categorySelect").disabled = "disabled";
      }
    });
  </script> 
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productSearchUIFragment.jsp#2 $$Change: 953229 $--%>
