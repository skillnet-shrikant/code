<%--
 This page defines the product catalog panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/productCatalogBrowse.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:importbean bean="/atg/userprofiling/Profile" var="profile"/>

    <dsp:tomap var="profile" value="${profile}"/>
    <c:set var="currentCatalog" value="${profile.catalog}"/>

    <div class="panelContent" id="___panelContent___">
      <div parseWidgets="false" class="atg_commerce_csr_browseTree">
        <dsp:include src="/include/catalog/navigationTree.jsp" otherContext="${CSRConfigurator.contextRoot}" />
      </div>
      <div class="atg_commerce_csr_coreContent atg_commerce_csr_productBrowse" id="atg_commerce_csr_coreContent">
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
        
        <c:set var="useCustomCatalogs" value="${CSRConfigurator.customCatalogs}"/>

        <c:set var="productSearchBean" value="ProductSearch"/>
        <c:if test="${useCustomCatalogs}">
          <c:set var="productSearch" value="${customCatalogProductSearch}"/>
          <c:set var="productSearchBean" value="CustomCatalogProductSearch"/>
        </c:if>
        <script type="text/javascript">
          dojo.provide("atg.commerce.csr.catalog.productCatalog");
          atg.commerce.csr.catalog.productCatalogSearchSetFormSortProperty = function (columnIndex, sortDesc) {
            var theForm = dojo.byId(atg.commerce.csr.catalog.productCatalogPagedData.formId);
          }

          atg.commerce.csr.catalog.productCatalogSearchFields = [
            { name: '&nbsp;'},
            { name: '<fmt:message key="catalogBrowse.searchResults.productId"/>'},
            { name: '<fmt:message key="catalogBrowse.searchResults.SKU"/>'},
            { name: '<fmt:message key="catalogBrowse.searchResults.name"/>'},
            { name: '<fmt:message key="catalogBrowse.searchResults.priceRange"/>'},
            { name: '<fmt:message key="catalogBrowse.searchResults.qty"/>'},
            { name: '<fmt:message key="catalogBrowse.searchResults.actions"/>'}
          ];

          atg.commerce.csr.catalog.productCatalogSearchProperties = [
            { property: "image" },
            { property: "productId" },
            { property: "sku" },
            { property: "name" },
            { property: "priceRange" },
            { property: "qty" },
            { property: "actions" }
          ];
          atg.commerce.csr.catalog.productCatalog.searchView =
            { cells: [[
              { name: '<fmt:message key="catalogBrowse.searchResults.productInformation"/>',width:"65%"},
              { name: '<fmt:message key="catalogBrowse.searchResults.actions"/>',width:"35%"}
            ]]};


          atg.commerce.csr.catalog.productCatalog.searchLayout = [atg.commerce.csr.catalog.productCatalog.searchView];


            atg.commerce.csr.catalog.productCatalogPagedData =
              new atg.data.FormhandlerData(atg.commerce.csr.catalog.productCatalogSearchFields,"${CSRConfigurator.contextRoot}/include/catalog/productCatalogSearchResults.jsp?orderIsModifiable=<c:out value='${orderIsModifiable}'/>");
            <dsp:getvalueof var="rowsPerPage" bean="${productSearchBean}.maxResultsPerPage"/>
            atg.commerce.csr.catalog.productCatalogPagedData.rowsPerPage = ${rowsPerPage};
            atg.commerce.csr.catalog.productCatalogPagedData.formId = 'selectTreeNode';
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
              for (var i=inRowIndex, l=inData.results.length; i<l; i++) {
                var newRow = [
                  '<div class="atg_commerce_csr_productCatalogDetails"><div>' + inData.results[i].image + '</div><ul><li>' + inData.results[i].name + '</li><li>' + inData.results[i].productId  + '</li><li><span style="font-weight:bold"><font  color="0000FF">' + inData.results[i].fulfillmentMethod  + '</font></span></li><li><span style="font-weight:bold"><font  color="FF0000">' + inData.results[i].ageRestriction  + '</font></span></li><li>'  + inData.results[i].priceRange + '</li></ul></div>',
                  inData.results[i].qty + inData.results[i].actions
                ];
                this.setRow(newRow, i);
              }
            };

            atg.commerce.csr.catalog.productCatalog.searchRefreshGrid = function () {
              dojo.byId("selectTreeNode").currentResultPageNum.value = 1;
              atg.commerce.csr.catalog.productCatalogPagedData.count = 0;
              dijit.byId("atg_commerce_csr_catalog_productCatalogTable").rowCount = 0;
              atg.commerce.csr.catalog.productCatalogPagedData.clearData();
              dijit.byId("atg_commerce_csr_catalog_productCatalogTable").updateRowCount(0);
              var form = document.getElementById("selectTreeNode");
              var selectedCategoryId = null;
              if (form) {
            	  selectedCategoryId = form.hierarchicalCategoryId.value;
              }
              
              atg.commerce.csr.catalog.productCatalogPagedData.fetchRowCount(
              {
                callback: function(inRowCount) {
                  if (inRowCount.resultLength > 0) {
                    dojo.style("atg_commerce_csr_catalog_productCatalogTableContainer", "display", "");
                    dojo.style("atg_commerce_csr_catalog_noResults", "display", "none");
                    dojo.style("atg_commerce_csr_catalog_selectCategory", "display", "none");
                  } else {
                    if (selectedCategoryId) {
                        dojo.style("atg_commerce_csr_catalog_productCatalogTableContainer", "display", "none");
                        dojo.style("atg_commerce_csr_catalog_selectCategory", "display", "none");
                        dojo.style("atg_commerce_csr_catalog_noResults", "display", "");
                    } else {
                        dojo.style("atg_commerce_csr_catalog_productCatalogTableContainer", "display", "none");
                        dojo.style("atg_commerce_csr_catalog_selectCategory", "display", "");
                        dojo.style("atg_commerce_csr_catalog_noResults", "display", "none");
                    }
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
              atg.commerce.csr.catalog.clearTreeStateIfCatalogChanged('<c:out value="${currentCatalog}"/>');
              atg.commerce.csr.catalog.productCatalogAddPagination();
              var form = dojo.byId("selectTreeNode");
              if (form && window.catalogInfo) {
                if (window.catalogInfo.allHierarchicalCategoryId) form.allHierarchicalCategoryId.value = window.catalogInfo.allHierarchicalCategoryId;
                if (window.catalogInfo.hierarchicalCategoryId) form.hierarchicalCategoryId.value = window.catalogInfo.hierarchicalCategoryId;
                <c:if test="${CSRConfigurator.useSKUId}">
                  if (window.catalogInfo.sku) form.sku.value = window.catalogInfo.sku;
                </c:if>
                <c:if test="${CSRConfigurator.useProductId}">
                  if (window.catalogInfo.productID) form.productID.value = window.catalogInfo.productID;
                </c:if>
                if (window.catalogInfo.priceRelation) form.priceRelation.value = window.catalogInfo.priceRelation;
                if (window.catalogInfo.itemPrice) form.itemPrice.value = window.catalogInfo.itemPrice;
                if (window.catalogInfo.searchInput) form.searchInput.value = window.catalogInfo.searchInput;
              }
              dojo.connect(this, "resize", dojo.hitch(dijit.byId("atg_commerce_csr_catalog_productCatalogTable"), "update"));
              
              atg.keyboard.registerFormDefaultEnterKey("selectTreeNode", "searchButton");
          });
          _container_.onUnloadDeferred.addCallback(function () {
              atg.keyboard.unRegisterFormDefaultEnterKey("selectTreeNode");
              atg.commerce.csr.catalog.productCatalogRemovePagination();
              dojo.disconnect(this, "resize", dojo.hitch(dijit.byId("atg_commerce_csr_catalog_productCatalogTable"), "update"));
          });
          </script>
          <div id="atg_commerce_csr_catalog_subCategoriesListContainer">
            <c:if test="${!empty productSearch.hierarchicalCategoryId}">
              <c:import url="/include/catalog/subCategoriesList.jsp">
                <c:param name="_windowid" value="${param['_windowid']}"/>
                <c:param name="categoryId" value="${productSearch.hierarchicalCategoryId}"/>
                <c:param name="path" value="${productSearch.navigationPath}"/>
              </c:import>
            </c:if>
          </div>
          <div id="atg_commerce_csr_catalog_selectCategory">
            <fmt:message key="catalogBrowse.browseResults.selectCategory"/>
          </div>
          <div id="atg_commerce_csr_catalog_noResults" style="display:none;">
            <fmt:message key="catalogBrowse.browseResults.noResults"/>
          </div>
          <div id="atg_commerce_csr_catalog_productCatalogTableContainer" style="height:350px;display:none">
            <div id="atg_commerce_csr_catalog_productCatalogTable"
             dojoType="dojox.Grid"
             model="atg.commerce.csr.catalog.productCatalogPagedData"
             autoHeight="true"
             onMouseOverRow="atg.noop()"
             onRowClick="atg.noop()"
             onCellClick="atg.noop()"
             onCellContextMenu="atg.noop()"
             onkeydown="atg.noop()">
            </div>
          </div>
      </div>
    </div>
  </dsp:layeredBundle>
  <script type="text/javascript">
    dojo.addOnLoad(function () {
      atg.progress.update('cmcCatalogPS');
    });
  </script>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/productCatalogBrowse.jsp#1 $$Change: 946917 $--%>
