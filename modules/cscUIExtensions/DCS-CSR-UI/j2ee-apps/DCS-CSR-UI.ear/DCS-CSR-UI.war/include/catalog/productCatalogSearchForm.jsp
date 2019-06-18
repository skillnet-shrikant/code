<%--
 This page shows the product catalog results
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productCatalogSearchForm.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/CustomCatalogProductSearch" var="customCatalogProductSearch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/ProductSearch" var="productSearch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
  <dsp:importbean var="defaultPageFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/ProductSearchDefault" /> 
  <dsp:importbean var="extendedPageFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/ProductSearchExtended" /> 
  <c:set var="orderIsModifiable" value="false"/>
  <c:if test="${!empty shoppingCart.originalOrder}">
    <dsp:droplet name="/atg/commerce/custsvc/order/OrderIsModifiable">
      <dsp:param name="order" value="${shoppingCart.originalOrder}"/>
      <dsp:oparam name="true">
        <c:set var="orderIsModifiable" value="true"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
  
  <dsp:tomap bean="/atg/userprofiling/Profile" var="profile"/>
  
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <fmt:message key="catalogBrowse.searchResults.noResults" var="noResultsMessage" />
    <fmt:message key="catalogBrowse.searchResults.productResults" var="productResultsNumberMessage" />
    <fmt:message key="catalogBrowse.searchResults.noSearchPerformed" var="noSearchPerformedMessage" />
    <fmt:message key="text.leftBracket" var="leftBracketMessage" />
    <fmt:message key="text.rightBracket" var="rightBracketMessage"/>

    <c:set var="useCustomCatalogs" value="${CSRConfigurator.customCatalogs}"/>

    <c:set var="productSearchBean" value="ProductSearch"/>
    <c:if test="${useCustomCatalogs}">
      <c:set var="productSearch" value="${customCatalogProductSearch}"/>
      <c:set var="productSearchBean" value="CustomCatalogProductSearch"/>
    </c:if>
    <script type="text/javascript">
      dojo.provide("atg.commerce.csr.catalog.productCatalog");

      atg.commerce.csr.catalog.productCatalogSearchFields = [
        <c:if test="${isMultiSiteEnabled}">
        { name: '<fmt:message key="catalogBrowse.searchResults.site"/>', canSort: false},
        </c:if>
        { name: '<fmt:message key="catalogBrowse.searchResults.productInformation"/>', canSort: true, property: "displayName"},
        { name: '<fmt:message key="catalogBrowse.searchResults.actions"/>', canSort: false}
      ];

      atg.commerce.csr.catalog.productCatalog.searchView =
        { cells: [[
          <c:if test="${isMultiSiteEnabled}">
          { name: '<fmt:message key="catalogBrowse.searchResults.site"/>',width:"10%"},
          </c:if>
          { name: '<fmt:message key="catalogBrowse.searchResults.productInformation"/>',width:"55%"},
          { name: '<fmt:message key="catalogBrowse.searchResults.actions"/>',width:"35%"}
        ]]};


atg.commerce.csr.catalog.productCatalog.searchLayout = [atg.commerce.csr.catalog.productCatalog.searchView];


  atg.commerce.csr.catalog.productCatalogPagedData =
    new atg.data.FormhandlerData(atg.commerce.csr.catalog.productCatalogSearchFields,"${CSRConfigurator.contextRoot}/include/catalog/productCatalogSearchResults.jsp?orderIsModifiable=<c:out value='${orderIsModifiable}'/>&isSearching=true");
  <dsp:getvalueof var="rowsPerPage" bean="${productSearchBean}.maxResultsPerPage"/>
  atg.commerce.csr.catalog.productCatalogPagedData.rowsPerPage = ${rowsPerPage};
  atg.commerce.csr.catalog.productCatalogPagedData.formId = 'searchByProductForm';
  atg.commerce.csr.catalog.productCatalogPagedData.setCurrentPageNumber = function(inRowIndex) {
    var currentPage = Math.floor(inRowIndex / this.rowsPerPage) + 1;
    var form = dojo.byId(this.formId);
    if (form) {
      if (form[this.formCurrentPageField]){
        form[this.formCurrentPageField].value = currentPage;
      }
    }
  };
  atg.commerce.csr.catalog.productCatalogPagedData.rows = function(inRowIndex, inData) {
    for (var i=0, l=inData.results.length; i<l; i++) {
      var newRow = [
        <c:if test="${isMultiSiteEnabled}">
        inData.results[i].siteIcon,
        </c:if>
        '<div class="atg_commerce_csr_productCatalogDetails"><div>' + inData.results[i].image + '</div><ul><li>' + inData.results[i].name + '</li><li>' + inData.results[i].productId  + '</li><li><span style="font-weight:bold"><font color="0000FF">' + inData.results[i].fulfillmentMethod  + '</font></span></li><li><span style="font-weight:bold"><font  color="FF0000">' + inData.results[i].ageRestriction  + '</font></span></li><li>'  + inData.results[i].priceRange + '</li></ul></div>',
        inData.results[i].qty + inData.results[i].actions
      ];
      this.setRow(newRow, inRowIndex + i);
    }
  };

  atg.commerce.csr.catalog.productCatalog.searchRefreshGrid = function () {
    dojo.byId("searchByProductForm").currentResultPageNum.value = 1;
    atg.commerce.csr.catalog.productCatalogPagedData.count = 0;
    dijit.byId("atg_commerce_csr_catalog_productCatalogTable").rowCount = 0;
    atg.commerce.csr.catalog.productCatalogPagedData.clearData();
    dijit.byId("atg_commerce_csr_catalog_productCatalogTable").updateRowCount(0);
    atg.commerce.csr.catalog.productCatalogPagedData.fetchRowCount(
    {
      callback: function(inRowCount) {
        if (inRowCount.resultLength > 0) {
          dojo.style("atg_commerce_csr_catalog_productCatalogTableContainer", "display", "");
          //dojo.style("atg_commerce_csr_catalog_productCatalogTableContainer", "visibility", "visible");
          //dojo.style("atg_commerce_csr_catalog_search_noResults", "display", "none");
          document.getElementById("atg_commerce_csr_catalog_search_noResults").innerHTML = "<c:out value='${productResultsNumberMessage}' /><c:out value='${leftBracketMessage}' />" + inRowCount.resultLength + "<c:out value='${rightBracketMessage}' />";
        } else {
          dojo.style("atg_commerce_csr_catalog_productCatalogTableContainer", "display", "none");
          //dojo.style("atg_commerce_csr_catalog_search_noResults", "display", "");
          
          //CSC-169542
          if(window.catalogInfo){
        	 window.catalogInfo.isCatalogSearching = false ; 
          }
          document.getElementById("atg_commerce_csr_catalog_search_noResults").innerHTML = "<c:out value='${noResultsMessage}' />";
        }
        atg.commerce.csr.catalog.productCatalogPagedData.count = inRowCount.resultLength;
        dijit.byId("atg_commerce_csr_catalog_productCatalogTable").rowCount = inRowCount.resultLength;
        atg.commerce.csr.catalog.productCatalogPagedData.clearData();
        dijit.byId("atg_commerce_csr_catalog_productCatalogTable").updateRowCount(inRowCount.resultLength);
      }
    });
  };

  atg.commerce.csr.catalog.productCatalogAddPagination = function() {
    dijit.byId("atg_commerce_csr_catalog_productCatalogTable").setStructure(atg.commerce.csr.catalog.productCatalog.searchLayout);
    //dojo.style("atg_commerce_csr_catalog_productCatalogTableContainer", "visibility", "hidden");
  }

  atg.commerce.csr.catalog.productCatalogRemovePagination = function() {
    // clean up after ourselves
    atg.commerce.csr.catalog.productCatalogPagedData = undefined;
  }
  </script>
    <script type="text/javscript">
    _container_.onLoadDeferred.addCallback(function () {
        atg.commerce.csr.catalog.productCatalogAddPagination();
        var form = dojo.byId("searchByProductForm");
        if (form && window.catalogInfo) {
          <c:if test="${CSRConfigurator.useSKUId}">
            if (window.catalogInfo.sku) form.sku.value = window.catalogInfo.sku;
          </c:if>
          <c:if test="${CSRConfigurator.useProductId}">
            if (window.catalogInfo.productID) form.productID.value = window.catalogInfo.productID;
          </c:if>
          if (window.catalogInfo.siteSelect) form.siteSelect.selectedIndex = window.catalogInfo.siteSelect;
          if (window.catalogInfo.priceRelation) form.priceRelation.value = window.catalogInfo.priceRelation;
          if (window.catalogInfo.itemPrice) form.itemPrice.value = window.catalogInfo.itemPrice;
          if (window.catalogInfo.searchInput) form.searchInput.value = window.catalogInfo.searchInput;
          
          if (window.catalogInfo.categorySelect) {
            atg.commerce.csr.catalog.selectSite('<fmt:message key="catalogBrowse.findProducts.allCategories"/>', window.catalogInfo.categorySelect);
          } else {
            atg.commerce.csr.catalog.selectSite('<fmt:message key="catalogBrowse.findProducts.allCategories"/>');
          }
          if (window.catalogInfo.isCatalogSearching == true) {
            atg.commerce.csr.catalog.catalogSearch('');
          }
        }
        dojo.connect(_container_, "resize", dijit.byId("atg_commerce_csr_catalog_productCatalogTable"), "update");
        if(window.catalogInfoOnNewProductSearchConnect) {
        	// remove old connection
        	dojo.disconnect(window.catalogInfoOnNewProductSearchConnect);
        }
        if (dojo.byId("globalContextStartCall")){
          window.catalogInfoOnNewProductSearchConnect = dojo.connect(dojo.byId("globalContextStartCall"), "onclick", atg.commerce.csr.catalog.onNewProductSearch);
        }else if(dojo.byId("globalContextEndAndStartCall")){
          window.catalogInfoOnNewProductSearchConnect = dojo.connect(dojo.byId("globalContextEndAndStartCall"), "onclick", atg.commerce.csr.catalog.onNewProductSearch);
        }
        atg.keyboard.registerFormDefaultEnterKey("searchByProductForm", "searchButton");
    });
    _container_.onUnloadDeferred.addCallback(function () {
        atg.keyboard.unRegisterFormDefaultEnterKey("searchByProductForm");
        atg.commerce.csr.catalog.productCatalogRemovePagination();
        dojo.disconnect(_container_, "resize", dijit.byId("atg_commerce_csr_catalog_productCatalogTable"), "update");
    });

      // autofocus attribute does not function correctly in IE and firefox
      // this polyfil can be removed when both bowsers fix this
      dojo.byId('searchButton').focus()
      
    </script>
    <div class="atg_commerce_csr_findProdForm">
    <dsp:form action="#" id="searchByProductForm" formid="searchByProductForm">
      <dsp:input bean="${productSearchBean}.isCatalogBrowsing" name="isCatalogBrowsing" value="false" type="hidden"/>
      <dsp:input bean="${productSearchBean}.navigationPath" name="path" type="hidden"/>
      <dsp:input bean="${productSearchBean}.sortProperty" name="sortProperty" type="hidden"/>
      <dsp:input bean="${productSearchBean}.sortDirection" name="sortDirection" type="hidden"/>
      <%-- the following input will trigger both setCurrentResultPageNum and handleCurrentResultPageNum --%>
      <dsp:input type="hidden" name="currentResultPageNum" priority="-10" bean="${productSearchBean}.currentResultPageNum" beanvalue="${productSearchBean}.currentResultPageNum" />
      <div class="atg-csc-base-table atg-csc-base-spacing-top">
        <c:if test="${not empty defaultPageFragment.URL}">
          <dsp:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
        </c:if>
  
        <c:if test="${not empty extendedPageFragment.URL}">
          <dsp:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
        </c:if>
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"></div>
        <div class="atg-csc-base-table-cell"></div>
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"></div>
        <div class="atg_commerce_csr_formButtons atg-csc-base-table-cell atg-base-table-product-catalog-search-button">
          <input type="button" class="atg_commerce_csr_searchFormButton" id="searchButton" value="<fmt:message key='catalogBrowse.findProducts.search'/>" onclick="atg.commerce.csr.catalog.catalogSearch('<dsp:valueof bean="${productSearchBean}.hierarchicalCategoryId"/>')" autofocus />
          <a href="#" onclick="atg.commerce.csr.catalog.onNewProductSearch();return false;"><fmt:message key="catalogBrowse.findProducts.newSearch"/></a>
        <script>
          // autofocus atttribute does not function correctly in IE and firefox
          // remove this shim when 
          dojo.byId('searchButton').focus()
        </script>
        </div>
      </div>
    </dsp:form>
    </div>
    <div id="atg_commerce_csr_catalog_search_noResults">
        <c:out value="${noSearchPerformedMessage}" />
    </div>
    <div id="atg_commerce_csr_catalog_productCatalogTableContainer" style="height: 300px;display:none;">
      <div id="atg_commerce_csr_catalog_productCatalogTable"
       dojoType="dojox.Grid"
      jbarraud-apps-x model="atg.commerce.csr.catalog.productCatalogPagedData"
       autoHeight="false"
       onMouseOverRow="atg.noop()"
       onRowClick="atg.noop()"
       onCellClick="atg.noop()"
       onCellContextMenu="atg.noop()"
       onkeydown="atg.noop()">
      </div>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productCatalogSearchForm.jsp#2 $$Change: 1179550 $--%>
