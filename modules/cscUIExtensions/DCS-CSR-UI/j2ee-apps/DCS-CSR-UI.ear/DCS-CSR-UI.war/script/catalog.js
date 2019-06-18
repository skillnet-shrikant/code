dojo.provide("atg.commerce.csr.catalog");
dojo.require("dojo.date.locale");
dojo.require("dojox.i18n.currency");
dojo.require("dojo.currency");
dojo.require("dojo.string");

atg.commerce.csr.catalog =
{
  
  // Adds exchange item to order
  addExchangeItemToOrder : function (SKUId, productId, confirmMessage, siteId, successURL, errorURL) {
    var theForm = dojo.byId("buyForm");
    
    if (theForm) {
      
      if(successURL && successURL != '') {
        theForm.successURL.value=successURL;
      }
            
      if(errorURL && errorURL != '') {
        theForm.errorURL.value=errorURL;
      }
      theForm.quantity.value = 1;
      this._addVerifiedItemToOrder(theForm, SKUId, productId, confirmMessage, siteId);  
    }
  },
  
  // Adds item to order
  addItemToOrder : function (formNumber, SKUId, productId, confirmMessage, siteId, isItemFractional, invalidQuantityMsg) {
    var theForm = dojo.byId("buyForm");

    if (typeof isItemFractional === "undefined") {
     isItemFractional = false;
    }

    if (theForm) {
      var itemQuantity = document.getElementById("itemQuantity" + formNumber);
      // test for fractional units by testing for float
      if (itemQuantity && itemQuantity.value != "") {
        var quantityNumber = dojo.number.parse(itemQuantity.value)
        if (isItemFractional === "true") {
          dojo.debug("addItemToOrder: A fractional quantity was added for SKU: " + SKUId + " quantity: " + quantityNumber);
          theForm.quantityWithFraction.value = quantityNumber;
          theForm.quantity.value = 0;
        } else {
          dojo.debug("addItemToOrder: Quantity added for SKU: " + SKUId + " quantity: " + quantityNumber);
          theForm.quantity.value = quantityNumber;
          theForm.quantityWithFraction.value = 0.0;
        }

        this._addVerifiedItemToOrder(theForm, SKUId, productId, confirmMessage, siteId);
        itemQuantity.value = ""; // clear quantity input field
      }
      else {
        dojo.debug("addItemToOrder: User did not specify a value for the amount to add to the cart");
        atg.commerce.csr.catalog.showError(invalidQuantityMsg);
      }
    }
  },
  
  _addVerifiedItemToOrder : function (theForm, SKUId, productId, confirmMessage, siteId) {
    theForm.catalogRefId.value = SKUId;
    theForm.productId.value = productId;
        
    if (siteId) {
      theForm.siteId.value = siteId;
    }
    theForm["atg.successMessage"].value = confirmMessage;
    var deferred = atgSubmitAction({
      form: theForm
    });
    deferred.addCallback(function() {atg.progress.update('cmcCatalogPS');}); // poke the order summary panel to update  
  },
  
  // OnCloseHandler implementation
  onCloseHandler_impl : function( args ) {
    // bugfix for case, when this handler is called too many times (somewhy)
    if(window.atg_commerce_csr_catalog_OnCloseHandler_ignoreCalls) {
      return;
    }
    window.atg_commerce_csr_catalog_OnCloseHandler_ignoreCalls = true;

    if ( args.result == 'ok' ) {
      var productSkusObj = window.atg_commerce_csr_catalog_OnCloseHandler_productSkusObj;

      var nextRowId = window.atg_commerce_csr_catalog_OnCloseHandler_rowId;
      var nextRowIndex = atg.commerce.csr.catalog.getRowIndexByRowId(nextRowId);

      var skuArray = new Array();
      for ( var ii=0; ii < args.product.skus.length; ++ii ) {
        var qty = dojo.string.trim(args.product.skus[ii].quantity);
        if(qty > 0) {
          productSkusObj.skus[ii].quantity = qty;
          skuArray.push(productSkusObj.skus[ii]);
        }
      }
      atg.commerce.csr.catalog.insertSkusIntoTable(skuArray, nextRowId, nextRowIndex, args.product.id);
      window.atg_commerce_csr_catalog_OnCloseHandler_rowId = null;
      window.atg_commerce_csr_catalog_OnCloseHandler_productSkusObj = null;

    } else {
      atg.commerce.csr.catalog.rememberProductId('');
    }
    // set focus to last productId input
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    if (addProductsByIdTable) {
      var ind = atg.commerce.csr.catalog.getRowIdFromTr(addProductsByIdTable.rows[addProductsByIdTable.rows.length - 1]);
      if (document.getElementById("atg_commerce_csr_catalog_productId" + ind)) {
        document.getElementById("atg_commerce_csr_catalog_productId" + ind).focus();
      }
      if(args.result != 'ok') {
        document.getElementById("atg_commerce_csr_catalog_productId" + ind).value = '';
      }
    }
  },
  
  // Returns true if product entry field should be shown
  isShowProductEntryField : function(){
    return document.getElementById('atg_commerce_csr_catalog_showProductEntryField').value;
  },

  // Returns true if sku entry field should be shown
  isShowSKUEntryField : function(){
    return document.getElementById('atg_commerce_csr_catalog_showSKUEntryField').value;
  },
  
  // Returns true if multisite is enabled
  isMultiSiteEnabled : function() {
    return document.getElementById('atg_commerce_csr_catalog_isMultiSiteEnabled').value === "true";
  },
  
  // Inserts multiple SKU into Add Products by Id table, inserts an empty row if necessary
  insertSkusIntoTable : function(skuArray, nextRowId, nextRowIndex, optDefaultProductId) {
    var toAddNewRow = false;
    var isLastRow = atg.commerce.csr.catalog.isLastRowIndex(nextRowIndex);
    for ( var ii=0; ii < skuArray.length; ++ii ) {
      //if(skuArray[ii].quantity > 0) {
      if(toAddNewRow) {
        nextRowId = atg.commerce.csr.catalog.addProductsByIdAddRow(nextRowIndex);
      } else {
        toAddNewRow = true;
      }
      //productSkusObj.skus[ii].quantity = skuArray[ii].quantity;
      var productId = skuArray[ii].productId ? skuArray[ii].productId : optDefaultProductId;
      var productSiteId = skuArray[ii].productSiteId;
      atg.commerce.csr.catalog.displaySKU(skuArray[ii], nextRowId, productId, productSiteId);
      /*if(atg.commerce.csr.catalog.isShowProductEntryField())*/ document.getElementById('atg_commerce_csr_catalog_productId' + nextRowId).value = productId;
      ++nextRowIndex;
      //}
    }

    if(isLastRow && skuArray.length > 0/* && areAnyRowsAdded */) {
      atg.commerce.csr.catalog.addProductsByIdAddRow();
    }

    //return skuArray.length > 0 ? nextRowId : -1;
  },

  // Returns OnCloseHandler implementation
  getOnCloseHandler : function (productSkusObj, rowId, isFirstRowId) {
    window.atg_commerce_csr_catalog_OnCloseHandler_ignoreCalls = false;
    if(isFirstRowId){
      window.atg_commerce_csr_catalog_OnCloseHandler_rowId = rowId;
    }else{
      var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
      var lastRow = atg.commerce.csr.catalog.getRowIdFromTr(addProductsByIdTable.rows[addProductsByIdTable.rows.length-1]);
      window.atg_commerce_csr_catalog_OnCloseHandler_rowId = lastRow;
    }
    window.atg_commerce_csr_catalog_OnCloseHandler_productSkusObj = productSkusObj;
    return atg.commerce.csr.catalog.onCloseHandler_impl;
  },

  // OnCloseHandler for Edit Line Item popup
  editLineItemOnCloseHandler : function(args, rowId) {
    if ( args.result == 'ok' ) {
      atg.commerce.csr.catalog.asyncLoadAndDisplaySKU("sku", args.sku, rowId);
    }
  },

  // Shows Product Quick View popup panel
  showProductViewPopup : function (productSkusObj, productId, rowId, isFirstRow) {
    var siteId = dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_siteId" + rowId).value);
    if(!siteId){
      siteId = productSkusObj.productSiteId;
    }
    atg.commerce.csr.common.showPopupWithReturn({
      popupPaneId: 'atg_commerce_csr_catalog_productQuickViewPopup',
      url: atg.commerce.csr.catalog.getProductQuickViewURL() + productId + "&siteId=" + siteId,
      onClose: atg.commerce.csr.catalog.getOnCloseHandler(productSkusObj, rowId, isFirstRow),
      title: document.getElementById("atg_commerce_csr_catalog_addProducts").value
    });
  },

  // Shows product View panel
  showProductView : function (productId, skuId, mode) {
    var theForm = document.getElementById("selectTabbedPanel");
    if (theForm) {
      atgSubmitAction({
        form: theForm,
        selectTabbedPanels: ["cmcProductViewP"],
        queryParams: {productId: productId, skuId: skuId}
      });
    }
  },

  // Shows Product View panel in context of product's site
  showProductViewInSiteContext : function (productId, siteId, currentSiteId) {
      if (siteId && siteId != currentSiteId) { // handle site change!
      var theForm = dojo.byId("atg_commerce_csr_productDetailsForm");
      atgSubmitAction({
        form: theForm,
        sync: true,
        queryParams: { productId: productId},
            selectTabbedPanels: ["cmcProductViewP"],
            panelStack : ["cmcCatalogPS", "globalPanels"]
      });
        theForm = document.getElementById("atg_commerce_csr_productDetailsForm");
          if (theForm) {
            atgSubmitAction({
              form: theForm,
              panelStack : ["cmcCatalogPS", "globalPanels"],
          queryParams: { contentHeader : true, siteId: siteId}
            });
      }
    }
      else { // no site change
      var theForm = dojo.byId("atg_commerce_csr_productDetailsForm");
      atgSubmitAction({
        form: theForm,
        queryParams: { productId: productId},
            selectTabbedPanels: ["cmcProductViewP"],
            panelStack : ["cmcCatalogPS", "globalPanels"]
      });
      }
  },
  
  // Shows product Catalog panel
  showProductCatalog : function (categoryId, ancestorsString, isCustomCatalogs) {
    var theForm = document.getElementById("selectTabbedPanel");
    if (theForm) {
      atg.commerce.csr.catalog.setCatalogInfo(true);
      if(categoryId && (ancestorsString != null) && isCustomCatalogs){
        atg.commerce.csr.catalog.setTreeInfo(ancestorsString + '$div$category_div_' + categoryId,  atg.commerce.csr.catalog.createInfo(categoryId, ancestorsString, isCustomCatalogs), "category");
      }
      atgSubmitAction({
        sync: true,
        form: theForm,
        selectTabbedPanels: ["cmcProductCatalogBrowseP"]
      });
    }
  },

  // Clears all search fields
  onNewProductSearch : function () {
    var sku = document.getElementById("sku");
    if (sku) sku.value = "";
    var productID = document.getElementById("productID");
    if (productID) productID.value = "";
    var itemPrice = document.getElementById("itemPrice");
    if (itemPrice) itemPrice.value = "";
    var searchInput = document.getElementById("searchInput");
    if (searchInput) searchInput.value = "";
    var priceRelation = document.getElementById("priceRelation");
    if (priceRelation) priceRelation.selectedIndex = 0;
    var siteSelect = document.getElementById("siteSelect");
    if (siteSelect) siteSelect.selectedIndex = 0;
    var categorySelect = document.getElementById("categorySelect");
    if (categorySelect) {
      categorySelect.selectedIndex = 0;
      categorySelect.disabled = "disabled";
    }
    window.catalogInfo = null;
  },
  
  // Creates info for CatalogInfo
  createInfo : function (categoryId, ancestorsString, isCustomCatalogs) {
    var customCatalogsAssetString = "atgasset:/CustomProductCatalog/category/";
    var standardCatalogsAssetString = "atgasset:/ProductCatalog/category/";
    // pass appropriate parameters
    var info = {};
    if (isCustomCatalogs == 'true') {
      info.URI = customCatalogsAssetString + categoryId;
    } else {
      info.URI = standardCatalogsAssetString + categoryId;
    }
    if (ancestorsString == '') {
      info.path = '$div$category_div_' + categoryId;
    } else {
      info.path = ancestorsString;
    }
    return info;
  },
  
  // Selects appropriate node in the tree when category in subcategories list is clicked
  nodeClicked : function (categoryId, ancestorsString, isCustomCatalogs) {
    var customCatalogsAssetString = "atgasset:/CustomProductCatalog/category/";
    var standardCatalogsAssetString = "atgasset:/ProductCatalog/category/";
    // pass appropriate parameters
    var info = atg.commerce.csr.catalog.createInfo(categoryId, ancestorsString, isCustomCatalogs);

    atg.commerce.csr.catalog.getTree().nodeSelected(ancestorsString + '$div$category_div_' + categoryId, info);

    if (ancestorsString != '') {
      var ancestorCategory = atg.commerce.csr.catalog.getTreeNodeCategoryId(ancestorsString);
      var ancestorInfo = {};
      if (isCustomCatalogs) {
        ancestorInfo.URI = customCatalogsAssetString + ancestorCategory;
      } else {
        ancestorInfo.URI = standardCatalogsAssetString + ancestorCategory;
      }
      ancestorInfo.path = ancestorsString;
      atg.commerce.csr.catalog.getTree().openNode(ancestorsString);
    }
  },

  // Updates Catalog Browse right pane when category is selected in the tree
  nodeSelected : function (path, info) {
    if (atg.commerce.csr.catalog.getTreeInfo().path && atg.commerce.csr.catalog.getTreeInfo().path != path && atg.commerce.csr.catalog.getTreeFrame().getElementById("node_" + atg.commerce.csr.catalog.getTreeInfo().path)) {
      atg.commerce.csr.catalog.getTreeFrame().getElementById("node_" + atg.commerce.csr.catalog.getTreeInfo().path).className = "treeNode";
    }
    var replacedPath = atg.commerce.csr.catalog.getCommaSeparatedTreePath(path);

    // if user selected category
    atg.commerce.csr.catalog.setTreeInfo(path, info, "category");
    var form = document.getElementById("selectTreeNode");
    if (form) {
      form.hierarchicalCategoryId.value = atg.commerce.csr.catalog.getTreeNodeCategoryId(path);
      form.path.value = replacedPath;
      atgSubmitAction({
        form: form,
        sync: true
      });
      //atg.commerce.csr.catalog.reloadCatalogBrowseRightPane(form.hierarchicalCategoryId.value, replacedPath);
      atg.commerce.csr.catalog.reloadSubCategoriesList(form.hierarchicalCategoryId.value, replacedPath);
      atg.commerce.csr.catalog.productCatalogPagedData.formId = "selectTreeNode";
      atg.commerce.csr.catalog.productCatalog.searchRefreshGrid();
    }
    atg.commerce.csr.catalog.setCatalogInfo(true);
  },
  
  // Performs search
  onCatalogBrowseSearch : function (categoryId) {
    var form = document.getElementById("searchByProductForm");
    if (form) {
      atg.commerce.csr.catalog.setCatalogInfo(false, form);
      if (form.itemPrice && form.priceRelation) {
        var itemPrice = form.itemPrice.value;
        if (form.priceRelation.value == " ") {
          form.itemPrice.value = "";
        }
      }
      if (form.productID) form.productID.value = dojo.string.trim(form.productID.value);
      if (form.sku) form.sku.value = dojo.string.trim(form.sku.value);
    }
    // reload search results
    atg.commerce.csr.catalog.productCatalogPagedData.formId = "searchByProductForm";
    atg.commerce.csr.catalog.productCatalog.searchRefreshGrid();

    if (dojo.byId('allHierarchicalCategoryId') && dojo.byId('allHierarchicalCategoryId').checked) {
      // no catalog tree entry should be highlighted after executing search against entire catalog
      var info = atg.commerce.csr.catalog.createInfo("", "");
      info.URI = '';
      atg.commerce.csr.catalog.getTree().nodeSelected("", info);
      atg.commerce.csr.catalog.reloadSubCategoriesList("", "");
    }
  },
  
  catalogSearch : function (categoryId) {
    var form = document.getElementById("searchByProductForm");
      if (form) {
        atg.commerce.csr.catalog.setCatalogInfo(false, form);
        if (form.itemPrice && form.priceRelation) {
          var itemPrice = form.itemPrice.value;
          if (form.priceRelation.value == " ") {
            form.itemPrice.value = "";
          }
        }
        if (form.sitesSelectValue) {
          var siteSelect = document.getElementById('siteSelect');
          if (siteSelect) {
            form.sitesSelectValue.value = siteSelect.options[form.siteSelect.selectedIndex].value;
          }
        }
        if (form.hierarchicalCategoryId) {
          var categorySelect = document.getElementById('categorySelect');
          if (categorySelect) {
            form.hierarchicalCategoryId.value = categorySelect.options[form.categorySelect.selectedIndex].value;
          }
        }
        if (form.productID) form.productID.value = dojo.string.trim(form.productID.value);
        if (form.sku) form.sku.value = dojo.string.trim(form.sku.value);
      }
      // reload search results
      atg.commerce.csr.catalog.productCatalogPagedData.formId = "searchByProductForm";
      atg.commerce.csr.catalog.productCatalog.searchRefreshGrid();
  },

  // Reloads subcategories list
  reloadSubCategoriesList : function (categoryId, path) {
    dojo.xhrGet({
      url: atg.commerce.csr.getContextRoot() + "/include/catalog/subCategoriesList.jsp?categoryId=" + categoryId + "&path=" + path + "&_windowid=" + window.windowId,
      encoding: "utf-8",
      handle: function(response, ioArgs) {
        if(!(response instanceof Error)){
          var value = document.getElementById('atg_commerce_csr_catalog_subCategoriesListContainer');
          value.innerHTML=response;
        }
      },
      mimetype: "text/html"
    });
  },

  // Sets catalog to user
  selectCatalog : function (catalogId) {
    var form = document.getElementById("setCatalogForm");
    if (form) {
      form.catalogId.value = catalogId;
      atgSubmitAction({
        form: form,
        selectTabbedPanels : ["cmcProductCatalogSearchP"],
        sync:true
      });
    }
  },

  searchForCatalogs: function(dateFormat) {
    var incorrectStartDate = dojo.date.locale.format(dojo.date.add(new Date(),"year", 19), {datePattern: dateFormat, selector: "date"});
    var incorrectEndDate = dojo.date.locale.format(new Date(-1, 00, 01), {datePattern: dateFormat, selector: "date"});
    var startDate = document.getElementById('moreCatalogsStartDateInput').value;
    var endDate = document.getElementById('moreCatalogsEndDateInput').value;

    if (startDate != "" && startDate != dateFormat) {
      if (!dojo.date.locale.parse(startDate, {datePattern: dateFormat, selector: "date"})) {
        document.getElementById('moreCatalogsStartDate').value = incorrectStartDate;
      } else {
        document.getElementById('moreCatalogsStartDate').value = startDate;
      }
    } else {
      document.getElementById('moreCatalogsStartDate').value = "";
    }
    if (endDate != "" && endDate != dateFormat) {
      if (!dojo.date.locale.parse(endDate, {datePattern: dateFormat, selector: "date"})) {
        document.getElementById('moreCatalogsEndDate').value = incorrectEndDate;
      } else {
         var tempDateInc = dojo.date.add(dojo.date.locale.parse(endDate, {datePattern: dateFormat, selector: "date"}),"day", 1); 
         document.getElementById('moreCatalogsEndDate').value = dojo.date.locale.format(tempDateInc, {datePattern: dateFormat, selector: "date"});
      }
    } else {
      document.getElementById('moreCatalogsEndDate').value = "";
    }

    atg.commerce.csr.catalog.moreCatalogs.searchRefreshGrid();
  },

  
  selectSite: function (allCategoriesString, selectedCategoryIndex) {
    var siteSelect = document.getElementById('siteSelect');
    if (siteSelect) {
      dojo.xhrGet({
      sync: true,
      url: atg.commerce.csr.getContextRoot() + "/include/catalog/getCategoriesForSite.jsp",
      content: {
        siteId: siteSelect.options[siteSelect.selectedIndex].value,
        _windowid: window.windowId
      },
      encoding: "utf-8",
      handle: function(response, ioArgs) {
        if(!(response instanceof Error)){
          var inObj = atg.commerce.csr.catalog.createObjectFromJSON(response);
          var categorySelect = document.getElementById('categorySelect');
          //removes all options
          categorySelect.length=0;
          //this is not a speedy solution. But we are creating only few options not a lot.
          //concatenated string is the fast option. But select.innerhtml poses problems stated 
          //in the article.
          //http://support.microsoft.com/kb/276228
          //BUG: Internet Explorer Fails to Set the innerHTML Property of the Select Object
          categorySelect.options[0] = new Option(allCategoriesString, '');
          if (siteSelect.selectedIndex == 0 || inObj == null || inObj.categories.length == 0) {
            categorySelect.disabled = 'disabled';
          } else {
            categorySelect.disabled = '';
            var categoriesLength = inObj.categories.length;
            for (i = 0; i < categoriesLength; i++) {
              var category = inObj.categories[i];
              //0 index is the all root categories.
              categorySelect.options[i+1] = new Option(category.name, category.id);
            }
          }
          if (selectedCategoryIndex) {
            categorySelect.selectedIndex = selectedCategoryIndex;
          }
        } else {
          atg.commerce.csr.catalog.showDojoIoBindError(response, ioArgs);
        }
        return response;
      },
      mimetype: "text/plain"
    });
    }
  },
  
  // Creates input field with id fieldId, name fieldName, and optional type fieldType
  createInputField : function (fieldId, fieldName, opt_fieldType) {
    var aField = document.createElement("input");
    if (opt_fieldType) {
      aField.type = opt_fieldType;
    } else {
      aField.type = "text";
    }
    aField.id = fieldId;
    aField.name = fieldName;
    //aField.size = "10";
    return aField;
  },

  // Creates input field with name fieldName, type fieldType
  createInputFieldWithoutId : function (fieldName, fieldType) {
    return atg.commerce.csr.catalog.createInputField("", fieldName, fieldType);
  },

  // Gets id of next row to be added to Add Products by Id pane
  getNextRowId : function() {
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    var usedIds = new Array;
    for(var rowIndex = 0; rowIndex < addProductsByIdTable.rows.length; rowIndex++) {
      usedIds[rowIndex] = atg.commerce.csr.catalog.getRowIdFromTr(addProductsByIdTable.rows[rowIndex]);
    }
    usedIds.sort(function (a, b){return a - b;});
    for(var rowId = 0; rowId < usedIds.length; rowId++) {
      if(rowId<usedIds[rowId]){
        return rowId;
      }
    }    
    return usedIds.length;
  },

  // Gets next row index of Add Products by Id pane
  getNextRowIndex : function() {
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    var rowsCount = addProductsByIdTable.rows.length;
    return rowsCount;
  },

  // Generates new id of TR element of Add Products by Id pane
  generateTrId : function(rowId) {
    return "atg_commerce_csr_catalog_addProductsByIdTr" + rowId;
  },

  // Gets row id of TR element of Add Products by Id pane
  getRowIdFromTr : function(trElement) {
    return parseInt(trElement.id.replace(/^atg_commerce_csr_catalog_addProductsByIdTr/,""));
  },

  // Adds an empty row to Add Products by Id panel
  // additional parameter - 'input' element with id=addToShoppingButton
  addProductsByIdAddRow : function (opt_insertToIndex) {
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    var newRowId = atg.commerce.csr.catalog.getNextRowId();
    var newRowIndex = opt_insertToIndex ? opt_insertToIndex : atg.commerce.csr.catalog.getNextRowIndex();
    addProductsByIdTable.insertRow(newRowIndex);

    // update data needed for storing table in window scope (a mirror object)
    atg.commerce.csr.catalog.getPanelProducts().splice(newRowIndex-1, 0, null);

    var newRow = addProductsByIdTable.rows[newRowIndex];
    if (newRow) {
      newRow.id = atg.commerce.csr.catalog.generateTrId(newRowId);
      var tdCount = 0;
      if (atg.commerce.csr.catalog.isShowProductEntryField() != 'false') {
        var productCell = newRow.insertCell(tdCount);
        productCell.id = "productCell" + newRowId;
        productCell.className = "atg_numberValue";
        var productField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_productId" + newRowId, "atg_commerce_csr_catalog_productId" + newRowId);
        productField.size = "10";
        productField.onblur = function() {atg.commerce.csr.catalog.loadProductByProductIdTabOut(productField, newRowId);}
        productField.onfocus = function() {atg.commerce.csr.catalog.onFocusProductId(productField);}
        productField.onchange = function() {atg.commerce.csr.catalog.onProductRowChanged(newRowId);}
        productCell.appendChild(productField);

        //var warningElement = document.createElement("div");
        //warningElement.id = "atg_commerce_csr_catalog_productNotFoundWarning"+newRowId;
        //productCell.appendChild(warningElement);

        //tdCount += 1;
      } else {
        var productCell = newRow.insertCell(tdCount);
        productCell.id = "productCell" + newRowId;
        productCell.className = "atg_numberValue";
        productCell.style.display = "none";
        var productField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_productId" + newRowId, "atg_commerce_csr_catalog_productId" + newRowId, "hidden");
        productCell.appendChild(productField);
      }
      var warningElement = document.createElement("div");
      warningElement.id = "atg_commerce_csr_catalog_productNotFoundWarning"+newRowId;
      productCell.appendChild(warningElement);

      tdCount += 1;

      if (atg.commerce.csr.catalog.isShowSKUEntryField() != 'false') {
        var skuCell = newRow.insertCell(tdCount);
        skuCell.id = "skuCell" + newRowId;
        skuCell.className = "atg_numberValue";
        var skuField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_skuId" + newRowId, "atg_commerce_csr_catalog_skuId" + newRowId);
        skuField.size = "10";
        skuField.onblur = function() {atg.commerce.csr.catalog.loadProductBySkuIdTabOut(skuField, newRowId);}
        skuField.onfocus = function() {atg.commerce.csr.catalog.onFocusSkuId(skuField);}
        skuField.onchange = function() {atg.commerce.csr.catalog.onProductRowChanged(newRowId);}
        skuCell.appendChild(skuField);

        //var warningElement = document.createElement("div");
        //warningElement.id = "atg_commerce_csr_catalog_skuNotFoundWarning"+newRowId;
        //skuCell.appendChild(warningElement);

        //tdCount += 1;
      } else {
        var skuCell = newRow.insertCell(tdCount);
        skuCell.id = "skuCell" + newRowId;
        skuCell.className = "atg_numberValue";
        skuCell.style.display = "none";
        var skuField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_skuId" + newRowId, "atg_commerce_csr_catalog_skuId" + newRowId);
        skuCell.appendChild(skuField);
      }
      var warningElement = document.createElement("div");
      warningElement.id = "atg_commerce_csr_catalog_skuNotFoundWarning"+newRowId;
      skuCell.appendChild(warningElement);

      tdCount += 1;

      var quantityCell = newRow.insertCell(tdCount);
      quantityCell.id = "quantityCell" + newRowId;
      quantityCell.className = "atg_numberValue";

      var isFractionalField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_isFractional" + newRowId, "atg_commerce_csr_catalog_isFractional" + newRowId, "hidden");

      var quantityField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_qty" + newRowId, "atg_commerce_csr_catalog_qty" + newRowId);
      quantityField.size = document.getElementById('atg_commerce_csr_catalog_quantityInputTagSize').value; 
      quantityField.maxLength = document.getElementById('atg_commerce_csr_catalog_quantityInputTagMaxlength').value;
      quantityField.onchange = function() { atg.commerce.csr.catalog.onProductRowChanged(newRowId);}

      var salePriceField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_salePrice" + newRowId, "atg_commerce_csr_catalog_salePrice" + newRowId, "hidden");
      var siteIdField = atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_siteId" + newRowId, "atg_commerce_csr_catalog_siteId" + newRowId, "hidden");

      quantityCell.appendChild(quantityField);
      quantityCell.appendChild(siteIdField);
      quantityCell.appendChild(salePriceField);
      quantityCell.appendChild(isFractionalField);
      tdCount += 1;

      var nameCell = newRow.insertCell(tdCount);
      nameCell.id = "atg_commerce_csr_catalog_productName" + newRowId;
      tdCount += 1;

      var statusCell = newRow.insertCell(tdCount);
      statusCell.id = "atg_commerce_csr_catalog_skuStatus" + newRowId;
      tdCount += 1;
      
      var priceEachCell = newRow.insertCell(tdCount);
      priceEachCell.id = "atg_commerce_csr_catalog_priceEach" + newRowId;
      priceEachCell.className = "atg_numberValue";
      tdCount += 1;

      var totalPriceCell = newRow.insertCell(tdCount);
      totalPriceCell.id = "atg_commerce_csr_catalog_priceTotal" + newRowId;
      totalPriceCell.className = "atg_numberValue";
      tdCount += 1;

      var editCell = newRow.insertCell(tdCount);
      editCell.id = "atg_commerce_csr_catalog_editCell" + newRowId;
      editCell.className = "atg_iconCell";
      tdCount += 1;

      var deleteCell = newRow.insertCell(tdCount);
      deleteCell.id = "atg_commerce_csr_catalog_deleteCell" + newRowId;
      deleteCell.className = "atg_iconCell";
    }
    var addToShoppingButton = document.getElementById("atg_commerce_csr_catalog_addToShoppingButton");
    if (addToShoppingButton && atg.commerce.csr.catalog.isOrderModifiable()) {
      addToShoppingButton.disabled = false;
    }
    return newRowId;
  },

  // Returns isOrderModifiable value that is stored in a hidden field
  isOrderModifiable : function() {
    var isOrderModifiableHiddenValue = document.getElementById("atg_commerce_csr_catalog_isOrderModifiableHiddenValue");
    return (isOrderModifiableHiddenValue && isOrderModifiableHiddenValue.value != 'false');
  },

  // Sets display name for product
  setDisplayedProductName : function (productId, skuObj, rowId, productSiteId) {
    var productNameElem = document.getElementById("atg_commerce_csr_catalog_productName" + rowId);
    var siteIconStr = "";
    if(atg.commerce.csr.catalog.isMultiSiteEnabled()){
      siteIconStr = '<img src="' + skuObj.siteIconURL + '" title="' + skuObj.siteIconHover + '"/> ';
    }
    productNameElem.innerHTML = siteIconStr + '<a href="#" onclick="atg.commerce.csr.catalog.showAddProductPopup(\'' + productId + '\', \'' + rowId + '\');return false;">' + skuObj.displayName + '</a>';
    if(skuObj.ageRestriction != null && skuObj.ageRestriction != ""){
    	productNameElem.innerHTML = productNameElem.innerHTML + '<br/><span style="font-weight:bold"><font  color="FF0000">'+ skuObj.ageRestriction +'</font></span>';
    }
    if(skuObj.fulfillmentMethod != null && skuObj.fulfillmentMethod != ""){
    	productNameElem.innerHTML = productNameElem.innerHTML + '<br/><span style="font-weight:bold"><font  color="0000FF">'+ skuObj.fulfillmentMethod +'</font></span>';
    }
  },

  // Displays product information
  // extra input to function: reads URL from hidden input field with id "atg_commerce_csr_catalog_productEditLineItem"
  displaySKU : function (skuObj, rowId, productId, productSiteId) {
    // set display name, skuId, Qty, etc
    var skuIdElem = document.getElementById("atg_commerce_csr_catalog_skuId" + rowId);
    var statusElem = document.getElementById("atg_commerce_csr_catalog_skuStatus" + rowId);
    var priceEachElem = document.getElementById("atg_commerce_csr_catalog_priceEach" + rowId);
    var priceTotalElem = document.getElementById("atg_commerce_csr_catalog_priceTotal" + rowId);
    var hiddenSalePriceElem = document.getElementById("atg_commerce_csr_catalog_salePrice" + rowId);
    var editElem = document.getElementById("atg_commerce_csr_catalog_editCell" + rowId);
    var deleteElem = document.getElementById("atg_commerce_csr_catalog_deleteCell" + rowId);
    var siteIdElem = document.getElementById("atg_commerce_csr_catalog_siteId" + rowId);

    // update data needed for storing table in window scope (a mirror object)
    var rowIndex = atg.commerce.csr.catalog.getRowIndexByRowId(rowId);
    skuObj.productId = productId; // needed to restore table from window-scoped data
    atg.commerce.csr.catalog.getPanelProducts().splice(rowIndex-1, 1, skuObj);

    /*if(atg.commerce.csr.catalog.isShowSKUEntryField())*/  skuIdElem.value = skuObj.skuId;

    statusElem.innerHTML = skuObj.skuStatus;
    if(skuObj.skuStatusCssClass) {
      statusElem.className = skuObj.skuStatusCssClass;
    }

    if (skuObj.skuDiscountedPrice && skuObj.skuDiscountedPrice != '' && skuObj.skuDiscountedPrice != skuObj.skuPriceEach) {
      priceEachElem.innerHTML = skuObj.skuDiscountedPriceFormatted + "<br><div class='oldPrice'>" + skuObj.skuPriceEachFormatted + "</div>";
      hiddenSalePriceElem.value = skuObj.skuDiscountedPrice;
    } else {
      priceEachElem.innerHTML = skuObj.skuPriceEachFormatted;
      hiddenSalePriceElem.value = skuObj.skuPriceEach;
    }

    /*if(atg.commerce.csr.catalog.isShowProductEntryField())*/ {
      var productIdElem = document.getElementById("atg_commerce_csr_catalog_productId" + rowId);
      if (productIdElem && productIdElem.value == "" && productId) {
        productIdElem.value = productId;
      }
    }

    if(skuObj.quantity != null) {
      var qtyElem = document.getElementById("atg_commerce_csr_catalog_qty" + rowId);
      qtyElem.value = skuObj.quantity;
    }

    if(skuObj.fractionalQuantitiesAllowed != null) {
      var isFractional = document.getElementById("atg_commerce_csr_catalog_isFractional" + rowId);
      isFractional.value = skuObj.fractionalQuantitiesAllowed;
    }

    if(productSiteId != null && siteIdElem != null) {
      siteIdElem.value = productSiteId;
    }

    atg.commerce.csr.catalog.updateTotalPrice(rowId);

    if(atg.commerce.csr.catalog.isShowSKUEntryField() != 'false') {
      var editAnchor = document.createElement("a");
      editAnchor.href = "#";
      editAnchor.className = "atg_tableIcon atg_propertyEdit";
      //Open the Edit Line Item popup
      var productSkusObj = new Object();
      productSkusObj.skus = new Array(1);
      productSkusObj.skuCount = 1;
      //productSkusObj.productName = ""; // this value does not matter
      productSkusObj.skus[0] = skuObj;
      var editLineItemUrl = document.getElementById("atg_commerce_csr_catalog_productEditLineItem").value;
      var editLineItemHandler = function(args) {
        atg.commerce.csr.catalog.editLineItemOnCloseHandler(args, rowId);
      }
      editAnchor.onclick = function() {atg.commerce.csr.common.showPopupWithReturn({
        popupPaneId: 'editLineItemPopup',
        url: '' + editLineItemUrl.replace("SKUIDPLACEHOLDER", skuObj.skuId) + productId,
        onClose: editLineItemHandler,
        title: document.getElementById("atg_commerce_csr_catalog_editLineItem").value
      });};
      editAnchor.title = document.getElementById("atg_commerce_csr_catalog_editTooltip").value;
      editAnchor.innerHTML = document.getElementById("atg_commerce_csr_catalog_edit").value;
      editElem.innerHTML = "";
      editElem.appendChild(editAnchor);
    }

    var deleteAnchor = document.createElement("a");
    deleteAnchor.href = "#";
    deleteAnchor.className = "atg_tableIcon atg_propertyClear";
    deleteAnchor.onclick = function() {atg.commerce.csr.catalog.deleteProductRowByRowId(rowId);};
    deleteAnchor.title = document.getElementById("atg_commerce_csr_catalog_deleteTooltip").value;
    deleteAnchor.innerHTML = document.getElementById("atg_commerce_csr_catalog_delete").value;
    deleteElem.innerHTML = "";
    deleteElem.appendChild(deleteAnchor);

    atg.commerce.csr.catalog.setDisplayedProductName(productId, skuObj, rowId, productSiteId);
  },

  // Clears product information
  clearProduct : function(rowId, fieldToIgnore) {
    document.getElementById("atg_commerce_csr_catalog_productName" + rowId).innerHTML = ""
    if(fieldToIgnore != "productId") {
      var theField = document.getElementById("atg_commerce_csr_catalog_productId" + rowId);
      if(theField)  theField.value = "";
    }
    if(fieldToIgnore != "skuId") {
      var theField = document.getElementById("atg_commerce_csr_catalog_skuId" + rowId);
      if(theField)  theField.value = "";
    }

    document.getElementById("atg_commerce_csr_catalog_siteId" + rowId).value = "";
    document.getElementById("atg_commerce_csr_catalog_qty" + rowId).value = "";
    document.getElementById("atg_commerce_csr_catalog_skuStatus" + rowId).innerHTML = "";
    document.getElementById("atg_commerce_csr_catalog_priceEach" + rowId).innerHTML = "";
    document.getElementById("atg_commerce_csr_catalog_salePrice" + rowId).value = "";
    document.getElementById("atg_commerce_csr_catalog_priceTotal" + rowId).innerHTML = "";
    document.getElementById("atg_commerce_csr_catalog_editCell" + rowId).innerHTML = "";
    //document.getElementById("atg_commerce_csr_catalog_deleteCell" + rowId).innerHTML = "";

    document.getElementById("atg_commerce_csr_catalog_productNotFoundWarning"+rowId).innerHTML = "";
    document.getElementById("atg_commerce_csr_catalog_skuNotFoundWarning"+rowId).innerHTML = "";

    // update data needed for storing table in window scope (a mirror object)
    var rowIndex = atg.commerce.csr.catalog.getRowIndexByRowId(rowId);
    atg.commerce.csr.catalog.getPanelProducts().splice(rowIndex-1, 1, null);
  },

  // Converts JSON data to JS object
  createObjectFromJSON : function (jsonData) {
    if(jsonData == null)
      return null;
    var inData = jsonData;
    try {
      inData = dojo.string.trim(dojo.string.trim(jsonData).replace(/^[\r\n\s]+/,''));
      inData = inData.replace(/\s*[\r\n]+[\s\r\n]*/g,'\n');
      var inObj = (inData ? eval('(' + inData + ')') : null);
      return inObj;
    } catch(err) {
      var errorMsg = 'convertObjectFromJSON(): error catched: "'+err+'" for JSON:'+inData;
      atg.commerce.csr.catalog.showError(errorMsg);
      return null;
    }
  },

  // Shows JSON error
  showDojoIoBindError : function (response, ioArgs) {
    var errDescr = "";
    var errorMsg = "dojo.xhrGet failed: error="+response;
    atg.commerce.csr.catalog.showError(errorMsg);
  },

  // Shows error in message bar
  showError : function(errorMsg) {
    atg.commerce.csr.common.addMessageInMessagebar ("error", errorMsg);
  },

  // Verifies that product Id or sku Id has changed
  verifyId : function(productOrSkuId) {
    return productOrSkuId != null && productOrSkuId.match(/^[\w\d-]+$/);
  },

  // Returns productJsonURL that is stored in a hidden field
  getReadProductJsonURL : function() {
    return document.getElementById("atg_commerce_csr_catalog_readProductJsonURL").value;
  },

  // Returns productQuickViewURL that is stored in a hidden field
  getProductQuickViewURL : function() {
    return document.getElementById("atg_commerce_csr_catalog_productQuickViewURL").value;
  },

  // Returns products data
  getPanelProducts : function() {
    var products = document.getElementById("atg_commerce_csr_catalog_products").productData;
    if(products == null) {
      document.getElementById("atg_commerce_csr_catalog_products").productData = new Array(1);
      products = document.getElementById("atg_commerce_csr_catalog_products").productData;
      products[0] = null;
    }
    return products;
  },

  // Sets default quantity as 1
  setDefaultQuantity : function(rowId, prevQty) {
    var qtyElem = document.getElementById("atg_commerce_csr_catalog_qty" + rowId);
    if(prevQty == null || dojo.string.trim(prevQty) == '') {
      qtyElem.value = '1';
    }else{
      qtyElem.value = prevQty;
    }
  },
  
  // Updates the Product image/description to correspond to the associated SKU.
  viewSkuDescription : function(skuId, productId, displayName, description, imageURL, panelId) {
      var currSkuId = document.getElementById("atg_commerce_csr_catalog_product_info_currSkuId");
      var infoImage = document.getElementById("atg_commerce_csr_catalog_product_info_image");
      var infoDisplayName = document.getElementById("atg_commerce_csr_catalog_product_info_display_name");
      var infoRepositoryId = document.getElementById("atg_commerce_csr_catalog_product_info_repository_id");
      var infoDescription = document.getElementById("atg_commerce_csr_catalog_product_info_description");
      var infoPrice = document.getElementById("atg_commerce_csr_catalog_product_info_price");
      if(currSkuId.value && currSkuId.value == skuId){
        infoImage.src=document.getElementById("atg_commerce_csr_catalog_product_info_product_image").value;
        infoDisplayName.innerHTML = document.getElementById("atg_commerce_csr_catalog_product_info_product_display_name").value;
        infoRepositoryId.innerHTML = document.getElementById("atg_commerce_csr_catalog_product_info_product_repository_id").value;
        infoDescription.innerHTML = document.getElementById("atg_commerce_csr_catalog_product_info_product_description").value;
        if (infoPrice) {
          infoPrice.innerHTML = document.getElementById("atg_commerce_csr_catalog_product_info_product_price").innerHTML;
        }
        currSkuId.value = "";
      }else{
        if(dojo.string.trim(imageURL) != ""){
          infoImage.src=imageURL;
        }else{
          infoImage.src=document.getElementById("atg_commerce_csr_catalog_product_info_product_image").value;
        }
        infoDisplayName.innerHTML = displayName;
        infoRepositoryId.innerHTML = skuId;
        infoDescription.innerHTML = description;
        if(!panelId||dojo.string.trim(panelId)=="") panelId = "productView";
        var price = document.getElementById(skuId+"-"+productId+"-"+panelId+"-price-td");
        if (price) {
          if (infoPrice) {
            infoPrice.innerHTML = price.innerHTML;
          }
        } else {
          if (infoPrice) {
            infoPrice.innerHTML = "";
          }
        }
        currSkuId.value = skuId;
      }
  },
  
  // Loads and displays product or SKU information; itemType - "product" or "sku"
  asyncLoadAndDisplaySKU : function(itemType, itemId, rowId) {
    var itemIdType = itemType+"Id";
    var prevQty = document.getElementById("atg_commerce_csr_catalog_qty" + rowId).value;
    atg.commerce.csr.catalog.clearProduct(rowId, itemIdType);
    if(atg.commerce.csr.catalog.verifyId(itemId)) {
      dojo.xhrGet({
        sync: true,
        url: atg.commerce.csr.catalog.getReadProductJsonURL() + "&"+itemIdType+"="+itemId,
        encoding: "utf-8",
        handle: function(response, ioArgs){
          if(!(response instanceof Error)){
              var inObj = atg.commerce.csr.catalog.createObjectFromJSON(response);
              if(inObj == null || inObj.skuCount == null) {
                atg.commerce.csr.catalog.showNotFound(itemType, rowId);
              } else {
                if (itemType == "product") { // product
                  if (inObj.skuCount == 1) {
                    atg.commerce.csr.catalog.setDefaultQuantity(rowId,prevQty);
                    atg.commerce.csr.catalog.displaySKU(inObj.skus[0], rowId, inObj.productId, inObj.skus[0].productSiteId);
                    atg.commerce.csr.catalog.rememberSkuId(inObj.skus[0].skuId);
                    atg.commerce.csr.catalog.onProductRowChanged(rowId);
                    document.getElementById("atg_commerce_csr_catalog_qty" + rowId).focus();
                  } else if (inObj.skuCount >= 2) {
                    atg.commerce.csr.catalog.showProductViewPopup(inObj, itemId, rowId, true);
                  }
                } else { // sku
                  if (inObj.skuCount != 1) {
                    var errorMsg = "ERROR: inObj.skuCount should be 1 when loading product by SKU ID";
                    atg.commerce.csr.catalog.showError(errorMsg);
                  } else {
                    atg.commerce.csr.catalog.setDefaultQuantity(rowId,prevQty);
                    atg.commerce.csr.catalog.displaySKU(inObj.skus[0], rowId, inObj.productId, inObj.skus[0].productSiteId);
                    atg.commerce.csr.catalog.rememberProductId(inObj.productId);
                    atg.commerce.csr.catalog.onProductRowChanged(rowId);
                    document.getElementById("atg_commerce_csr_catalog_qty" + rowId).focus();
                  }
                }
              }
          } else {
            atg.commerce.csr.catalog.showDojoIoBindError(response, ioArgs);
          }
          return response;
        },
        mimetype: "text/plain"
      });

    } else {
      atg.commerce.csr.catalog.showNotFound(itemType, rowId);
    }
  },
  
  // Loads product information and displays add product popup
  showAddProductPopup : function(productId, rowId) {
    if(atg.commerce.csr.catalog.verifyId(productId)) {
      dojo.xhrGet({
        sync: true,
        url: atg.commerce.csr.catalog.getReadProductJsonURL() + "&productId="+productId,
        encoding: "utf-8",
        handle: function(response, ioArgs){
          if(!(response instanceof Error)){
              var inObj = atg.commerce.csr.catalog.createObjectFromJSON(response);
              if(inObj == null || inObj.skuCount == null) {
                var errorMsg = "ERROR: can not load product information for productId="+productId;
                atg.commerce.csr.catalog.showError(errorMsg);
              } else {
                atg.commerce.csr.catalog.showProductViewPopup(inObj, productId, rowId, false);
              }
          } else {
            atg.commerce.csr.catalog.showDojoIoBindError(response, ioArgs);
          }
          return response;
        },
        mimetype: "text/plain"
      });

    } 
  },

  // This function is called when user tabs out from Sku ID input field on Add Products by ID panel
  loadProductBySkuIdTabOut : function (field, rowId) {
    var enteredSkuId = field.value;
    if(enteredSkuId != null) {
      enteredSkuId = dojo.string.trim(enteredSkuId);
    }

    var isChanged = enteredSkuId != document.getElementById("atg_commerce_csr_catalog_tmpSkuId").value;
    var shouldFillOtherFields = atg.commerce.csr.catalog.isShowProductEntryField() != 'false' && (isChanged ? false : dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_productId"+rowId).value)=="");
    var toLoad = enteredSkuId != null && dojo.string.trim(enteredSkuId) != "" && (isChanged || shouldFillOtherFields);
    if(toLoad) {
      atg.commerce.csr.catalog.asyncLoadAndDisplaySKU("sku", enteredSkuId, rowId);
    }else{
      if((enteredSkuId == null || dojo.string.trim(enteredSkuId) == "") && isChanged){
        atg.commerce.csr.catalog.deleteProductRowByRowId(rowId)
      }
    }
  },

  // This function is called when user tabs out from Product ID input field on Add Products by ID panel
  loadProductByProductIdTabOut : function (field, rowId) {
    var enteredProductId = field.value;
    if(enteredProductId != null) {
      enteredProductId = dojo.string.trim(enteredProductId);
    }

    var isChanged = enteredProductId != document.getElementById("atg_commerce_csr_catalog_tmpProductId").value;
    var shouldFillOtherFields = atg.commerce.csr.catalog.isShowSKUEntryField() != 'false' && (isChanged ? false : dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_skuId"+rowId).value)=="");
    var toLoad = enteredProductId != null && dojo.string.trim(enteredProductId) != "" && (isChanged || shouldFillOtherFields);

    if(toLoad) {
        atg.commerce.csr.catalog.asyncLoadAndDisplaySKU("product", enteredProductId, rowId);
    }else{
      if((enteredProductId == null || dojo.string.trim(enteredProductId) == "") && isChanged){
        atg.commerce.csr.catalog.deleteProductRowByRowId(rowId)
      }
    }
  },

  // itemType - "product" or "sku"
  showNotFound : function(itemType, rowId) {
    document.getElementById("atg_commerce_csr_catalog_"+itemType+"NotFoundWarning"+rowId).innerHTML = document.getElementById("atg_commerce_csr_catalog_"+itemType+"NotFoundError").value;
  },

  // Returns IDs of filled rows in table
  getFilledRowsId : function () {
    var filledRows = new Array();
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    if(addProductsByIdTable){
      var itemsCount = addProductsByIdTable.rows.length - 2;
      var rowId = 0;
      for (ii=1; ii <= itemsCount; ii++) {
        rowId = atg.commerce.csr.catalog.getRowIdFromTr(addProductsByIdTable.rows[ii]);
        if(atg.commerce.csr.catalog.isRowFilledIn(rowId)){
          filledRows.push(rowId);
        }
      }
    }  
    return filledRows;  
  },
      
  // Adds items from Add Products by Id panel to shopping cart
  addProductsByIdToShoppingCart : function () {
      var theForm = document.getElementById("atg_commerce_csr_catalog_addProductsByIdForm");
      if (theForm) {
        var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
        var filledRowsIds = atg.commerce.csr.catalog.getFilledRowsId(true,true);
        if(filledRowsIds.length>0){
          var skus = new Array();
          for(ii=0;ii<filledRowsIds.length;ii++){
            skus[ii] = new Array();
            skus[ii].siteIdToAdd = dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_siteId" + filledRowsIds[ii]).value);
            skus[ii].productIdToAdd = dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_productId" + filledRowsIds[ii]).value);
            skus[ii].skuIdToAdd = dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_skuId" + filledRowsIds[ii]).value);
            skus[ii].qtyToAdd = dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_qty" + filledRowsIds[ii]).value);
            skus[ii].fractionalQuantitiesAllowed = dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_isFractional" + filledRowsIds[ii]).value);
          }
          document.getElementById("atg_commerce_csr_catalog_dontStore").value = "1";
          atg.commerce.csr.catalog.clearAddProductByIdData();
          atg.commerce.csr.catalog.addProductsToShoppingCart(skus);
        }
      }
  },
      
  // Adds items from Add Products by Id panel to shopping cart
  addProductsToShoppingCartHandler : function (args, siteId,pLandingPanelStacks) {
    if ( args.result == 'ok' ) {
      var product = args.product;
      var skus = new Array();
      for(ii=0;ii<product.skus.length;ii++){
        skus[ii] = new Array();
        skus[ii].siteIdToAdd = dojo.string.trim(siteId);
        skus[ii].productIdToAdd = dojo.string.trim(product.id);
        skus[ii].skuIdToAdd = dojo.string.trim(product.skus[ii].id);
        skus[ii].qtyToAdd = dojo.string.trim(product.skus[ii].quantity);
      }
      atg.commerce.csr.catalog.addProductsToShoppingCart(skus,pLandingPanelStacks);
    }
  },

  addProductsToShoppingCart : function (skus,pLandingPanelStacks) {
        dojo.xhrGet({
          url: atg.commerce.csr.getContextRoot() + "/include/catalog/addToCartForm.jsp?count=" + skus.length + "&_windowid=" + window.windowId,
          encoding: "utf-8",
          handle: function(response, ioArgs) {
            if(!(response instanceof Error)){
              var value = document.getElementById('atg_commerce_csr_catalog_addToCartContainer');
              value.innerHTML=response;
              var formRowId = 1;
              for(ii=0;ii<skus.length;ii++){
                    document.getElementById("atg_commerce_csr_catalog_siteIdToAdd" + formRowId).value = skus[ii].siteIdToAdd;
                    document.getElementById("atg_commerce_csr_catalog_productIdToAdd" + formRowId).value = skus[ii].productIdToAdd;
                    document.getElementById("atg_commerce_csr_catalog_skuIdToAdd" + formRowId).value = skus[ii].skuIdToAdd;

                     if(skus[ii].fractionalQuantitiesAllowed === "true") {
                      document.getElementById("atg_commerce_csr_catalog_qtyWithFractionToAdd" + formRowId).value = skus[ii].qtyToAdd;
                    } else {
                      document.getElementById("atg_commerce_csr_catalog_qtyToAdd" + formRowId).value = skus[ii].qtyToAdd;
                    }

                    formRowId = formRowId + 1;
              }
              var landingPanelStack;
              if(pLandingPanelStacks == null)
                  landingPanelStacks="cmcShoppingCartPS";
              else
                  landingPanelStacks=pLandingPanelStacks;
              atgSubmitAction({
                //form: document.getElementById("addToCartForm"),
                form: document.getElementById("atg_commerce_csr_catalog_addToCartForm"),
                panelStack:[landingPanelStacks]
              });
            }
          },
          mimetype: "text/html"
        });
  },

  // Verifies Qty field
  verifyQty : function(rowId) {
    var qty = document.getElementById("atg_commerce_csr_catalog_qty" + rowId).value;
    return qty && qty != "" && qty.match(/^[+-]?\d+(\.\d+)?$/) && !isNaN(parseFloat(qty)) && parseFloat(qty) > 0;
    return true;
  },

  // Returns true if Add Products by Id row is filled, false otherwise
  isRowFilledIn : function (rowId) {
    // if last row is completed then add a new empty row
    var isRowCompleted = 
        (atg.commerce.csr.catalog.isShowSKUEntryField() == 'false' || dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_skuId" + rowId).value) != "")
        && (atg.commerce.csr.catalog.isShowProductEntryField() == 'false' || dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_productId" + rowId).value) != "")
        && atg.commerce.csr.catalog.verifyQty(rowId);
    return isRowCompleted;
  },

  // Updates total price
  updateTotalPrice : function (rowId) {
    var qtyElem = document.getElementById("atg_commerce_csr_catalog_qty" + rowId);
      var hiddenSalePriceElem = document.getElementById("atg_commerce_csr_catalog_salePrice" + rowId);
      var priceTotalElem = document.getElementById("atg_commerce_csr_catalog_priceTotal" + rowId);
    if (qtyElem && hiddenSalePriceElem && priceTotalElem) {
      if (atg.commerce.csr.catalog.verifyQty(rowId)) {
        var totalPrice = parseFloat(qtyElem.value) * hiddenSalePriceElem.value;
        priceTotalElem.innerHTML = atg.commerce.csr.catalog.formatCurrency(totalPrice);
      }else{
        priceTotalElem.innerHTML = "";
      }
    }
  },

  // This functionalion is called when Add Products by Id row is changed
  onProductRowChanged : function (rowId) {
    atg.commerce.csr.catalog.updateTotalPrice(rowId);

    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    var lastRow = addProductsByIdTable.rows[addProductsByIdTable.rows.length - 1];
    var isLastRow = atg.commerce.csr.catalog.generateTrId(rowId) == lastRow.id;
    if (isLastRow && atg.commerce.csr.catalog.isRowFilledIn(rowId)) {
      var newRowId = atg.commerce.csr.catalog.addProductsByIdAddRow();
      var newRow = document.getElementById(atg.commerce.csr.catalog.generateTrId(newRowId));
      newRow.scrollIntoView(false);
    }
    atg.commerce.csr.catalog.setAddToShoppingButtonDisabled();
  },

  // Stores productId in a hidden field
  rememberProductId : function(productId) {
    document.getElementById("atg_commerce_csr_catalog_tmpProductId").value = dojo.string.trim(productId);
  },

  // Stores skuId in a hidden field
  rememberSkuId : function(skuId) {
    document.getElementById("atg_commerce_csr_catalog_tmpSkuId").value = dojo.string.trim(skuId);
  },

  // This function is called  when product Id field gets focus
  onFocusProductId : function (field) {
    atg.commerce.csr.catalog.rememberProductId(field.value);
  },

  // This function is called when sku Id field gets focus
  onFocusSkuId : function (field) {
    atg.commerce.csr.catalog.rememberSkuId(field.value);
  },

  // Gets row index by row id
  getRowIndexByRowId : function (rowId) {
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');

    var rowTrId = atg.commerce.csr.catalog.generateTrId(rowId);
    for(var rowIndex = 1; rowIndex < addProductsByIdTable.rows.length; ++rowIndex) {
      if(addProductsByIdTable.rows[rowIndex].id == rowTrId) {
        return rowIndex;
      }
    }
    var errorMsg = "ERROR in getRowIndexByRowId(): row id="+rowId+" not found";
    atg.commerce.csr.catalog.showError(errorMsg);
    return -1;
  },

  // Checks if row with rowIndex is the last
  isLastRowIndex : function (rowIndex) {
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    var isLastRow = rowIndex == addProductsByIdTable.rows.length - 1;
    return isLastRow;
  },

  // Deletes row in Add Product by Id ane using row id
  deleteProductRowByRowId : function (rowId) {
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');

    var productCount = addProductsByIdTable.rows.length - 1;
    if (productCount <= 1)  return;

    var rowIndex = atg.commerce.csr.catalog.getRowIndexByRowId(rowId);
    if (rowIndex != -1) {
      var isLastRow = atg.commerce.csr.catalog.isLastRowIndex(rowIndex);
      addProductsByIdTable.deleteRow(rowIndex);

      // update data needed for storing table in window scope (a mirror object)
      atg.commerce.csr.catalog.getPanelProducts().splice(rowIndex-1, 1);

      if (isLastRow) {
        atg.commerce.csr.catalog.addProductsByIdAddRow();
      }
    }
    
    atg.commerce.csr.catalog.setAddToShoppingButtonDisabled();

  },
  
  // Enables or diaebles Add to Cart button
  setAddToShoppingButtonDisabled : function(){
    var filledRowsCount = atg.commerce.csr.catalog.getFilledRowsId().length;
    var addToShoppingButton = document.getElementById("atg_commerce_csr_catalog_addToShoppingButton");
    if (addToShoppingButton) {
      if (filledRowsCount < 1) {
        addToShoppingButton.disabled = "disabled";
      }else{
        if (atg.commerce.csr.catalog.isOrderModifiable()) {
          addToShoppingButton.disabled = false;
        }
      }
    }      
  },

  // Serializes data from Add Products by Id
  getAddProductByIdDataToStore : function() {
    var data = atg.commerce.csr.catalog.getPanelProducts();
    while(data.length>0 && data[data.length-1] == null) {
      data.splice(data.length-1, 1); //delete last 'null' elements
    }
    if(data.length < 1)
      return null;

    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');

    for(var i=0; i < data.length; ++i) {
      var rowIndex = i+1;
      var row = addProductsByIdTable.rows[rowIndex];
      var rowId = atg.commerce.csr.catalog.getRowIdFromTr(row);
      if(data[i] == null) {
        var errorMsg = "ERROR: getAddProductByIdDataToStore: unexpected null element ("+i+") of an array";
        atg.commerce.csr.catalog.showError(errorMsg);
      } else {
        data[i].quantity = document.getElementById("atg_commerce_csr_catalog_qty" + rowId).value;
      }
    }
    return dojo.toJson(data);
  },

  // Stores data from Add Products by Id
  storeAddProductByIdData : function() {
    if(document.getElementById("atg_commerce_csr_catalog_dontStore").value == "1")
      return;

    var data = atg.commerce.csr.catalog.getAddProductByIdDataToStore();
    if(data == null) {
      atg.commerce.csr.catalog.clearAddProductByIdData();
    } else {
      data = data.replace("'","\\'").replace("\n","");
      //data = escape(data);
      var theForm = document.getElementById("atg_commerce_csr_catalog_addProductsByIdWindowScopeForm");
      theForm.atg_commerce_csr_catalog_valueForWindowScope.value = data;
      atgSubmitAction({
        form: theForm
      });
    }
  },

  // Restores Add Product by Id data
  restoreAddProductByIdData : function(dataToRestore) {
    var addProductsByIdTable = document.getElementById('atg_commerce_csr_catalog_addProductsByIdTable');
    if(atg.commerce.csr.catalog.isLastRowIndex(0)) {
      atg.commerce.csr.catalog.addProductsByIdAddRow();
    }
    //var skuArray = dojo.json.evalJSON(dataToRestore);
    var skuArray = dojo.fromJson(dataToRestore);
    //var skuArray = atg.commerce.csr.catalog.createObjectFromJSON(dataToRestore); FIXME!!!
    
    if(skuArray == null){
      return;
    }

    var nextRowId = 1;
    var nextRowIndex = 1;
    atg.commerce.csr.catalog.insertSkusIntoTable(skuArray, nextRowId, nextRowIndex, null);
  },

  // Clears Add Product by Id data
  clearAddProductByIdData : function() {
    var theForm = document.getElementById("atg_commerce_csr_catalog_addProductsByIdWindowScopeForm");
    theForm.atg_commerce_csr_catalog_valueForWindowScope.value = "";
    atgSubmitAction({
      form: theForm
    });
  },

  // Restores tree state while switching tabs
  restoreTreeState : function() {
    var tree = atg.commerce.csr.catalog.getTree();
    // add _windowid param to tree URLs
    tree.populateNodeURL += "&_windowid=" + window.windowId;
    tree.openURL += "&_windowid=" + window.windowId;
    tree.closeURL += "&_windowid=" + window.windowId;
    tree.checkURL += "&_windowid=" + window.windowId;
    tree.clearAllAndCheckURL += "&_windowid=" + window.windowId;
    tree.uncheckURL += "&_windowid=" + window.windowId;
    tree.selectURL += "&_windowid=" + window.windowId;

    if (window.treeInfo) {
      var rootCategoryOpened = false;
      if (window.treeInfo.selectedItemType == "category") {
        if (window.catalogInfo) {
          tree.nodeSelected(window.treeInfo.path, window.treeInfo.info);
        }
      }
      if (window.treeInfo.path) {
        var tempStr = window.treeInfo.path.substring(window.treeInfo.path.indexOf("_div_") + 5);
        var endIndex = tempStr.indexOf("$div$");
        var parentCategory = tempStr.substring(0, endIndex);
        if (parentCategory != "") {
          tree.openNode("$div$category_div_" + parentCategory);
          rootCategoryOpened = true;
        } else if (tempStr != "") {
          tree.openNode("$div$category_div_" + tempStr);
          rootCategoryOpened = true;
        }
      //root category
      } else if (!rootCategoryOpened && window.treeInfo.rootItemExpanded && window.treeInfo.rootCategoryId) {
        tree.openNode("$div$category_div_" + window.treeInfo.rootCategoryId);
      }
    }
    if (window.catalogInfo) {
      atg.commerce.csr.catalog.productCatalogPagedData.formId = "selectTreeNode";
    }
  },

  //Formats currency
  formatCurrency : function(currencyValue) {
    var currencyCode = document.getElementById("atg_commerce_csr_catalog_activeCurrencyCode").value;

    var currencySymbol = document.getElementById("atg_commerce_csr_catalog_currentOrderCurrencySymbol").value;

    var currencyNumberOfDecimalPlaces = document.getElementById("atg_commerce_csr_catalog_activeCurrencyCodeNumberOfDecimalPlaces").value;

    if("" == currencyCode || "" == dojo.string.trim(currencyCode)) {
      atg.commerce.csr.catalog.showError("atg.commerce.csr.catalog.formatCurrency failed: currencyCode="+currencyCode);
      return ""+currencyValue;
    }

    if("" == currencySymbol || "" == dojo.string.trim(currencySymbol)) {
      atg.commerce.csr.catalog.showError("atg.commerce.csr.catalog.formatCurrency failed: currencySymbol="+currencySymbol);
      return ""+currencySymbol;
    }

    if("" == currencyNumberOfDecimalPlaces || "" == dojo.string.trim(currencyNumberOfDecimalPlaces)) {
      currencyNumberOfDecimalPlaces = 2;
    }

    try {
      //var formattedAmount = dojox.i18n.currency.format(pAmount, pCurrencyCode ,pFlags); 
      var formattedAmount = dojo.currency.format(currencyValue, {currency: currencyCode, places:currencyNumberOfDecimalPlaces, round: 0, symbol:currencySymbol});
      return formattedAmount;
    } catch(e) {
      atg.commerce.csr.catalog.showError("dojo.currency.format failed: "+e);
      return ""+currencyValue;
    }
  },
  
  //Returns tree component
  getTree: function() {
    if (!window.frames.treeContainer || !window.frames.treeContainer.tree) {
      var tree = window.frames[1].tree;
    } else {
      var tree = window.frames.treeContainer.tree;
    }
    if (!tree) {
      var framesLength = window.frames.length;
      for (i=0; i < framesLength; i++) {
        var frameObj = window.frames[i];
        if (frameObj && frameObj.tree) {
          tree = frameObj.tree;
          break;
        }
      }
    }
    return tree;
  },

  //Returns frmae that contains tree
  getTreeFrame: function() {
    if (!window.frames.treeContainer || !window.frames.treeContainer.tree) {
      return window.frames[1].document;
    } else {
      return window.frames.treeContainer.document;
    }
  },
  
    // Returns comma-separated path of the tree node
  getCommaSeparatedTreePath : function(path) {
    if (path != null) return path.replace(/\$div\$category_div_/g,",");
  },
  
  // Returns tree node item type by URI
  getTreeNodeItemType : function(uri) {
    if (uri != null) {
      var lastPart = uri.substring(uri.indexOf("/") + 1);
      var ind1 = lastPart.indexOf("/") + 1;
      var ind2 = lastPart.lastIndexOf("/");
      return lastPart.substring(ind1, ind2);
    }
  },
  
  // Returns tree node category id by path
  getTreeNodeCategoryId : function(path) {
    if (path != null) return path.substring(path.lastIndexOf("_div_") + 5);
  },
  
  // Returns catalogInfo component. If it does not exist, creates new one
  getCatalogInfo : function() {
    if (!window.catalogInfo) {
      window.catalogInfo = {};
    }
    return window.catalogInfo;
  },
  
  // Fills catalogInfo component with appropriate search data taken from the form
  setCatalogInfo : function(isCatalogBrowsing, form) {
    var catalogInfo = atg.commerce.csr.catalog.getCatalogInfo();
    if (isCatalogBrowsing) {
      catalogInfo.isCatalogBrowsing = true;
    } else {
      catalogInfo.isCatalogSearching = true;
    }
    if (form) {
      if (form.sku) catalogInfo.sku = form.sku.value;
      if (form.productID) catalogInfo.productID = form.productID.value;
      if (form.searchInput) catalogInfo.searchInput = form.searchInput.value;
      if (form.siteSelect) catalogInfo.siteSelect = form.siteSelect.selectedIndex;
      if (form.categorySelect) catalogInfo.categorySelect = form.categorySelect.selectedIndex;
      if (form.itemPrice) catalogInfo.itemPrice = form.itemPrice.value;
      if (form.priceRelation) catalogInfo.priceRelation = form.priceRelation.value;
    }
  },
  
  // Returns treeInfo component. If it does not exist, creates new one
  getTreeInfo : function() {
    if (!window.treeInfo) {
      window.treeInfo = {};
    }
    return window.treeInfo;
  },
  
  // Sets treeInfo component's values
  setTreeInfo : function(path, info, selectedItemType) {
    atg.commerce.csr.catalog.getTreeInfo().path = path;
    atg.commerce.csr.catalog.getTreeInfo().info = info;
    atg.commerce.csr.catalog.getTreeInfo().selectedItemType = selectedItemType;
  },

  //Clears catalog navigation tree state
  clearTreeState : function() {
    window.catalogInfo = null;
    window.treeInfo = null;
  },

  //clears the catalog navigation tree state if the catalog is changed...
  clearTreeStateIfCatalogChanged : function(catalogId) {
    var catalogInfo = atg.commerce.csr.catalog.getCatalogInfo();
    var currentCatalogId = catalogInfo.currentCatalogId;
    if (currentCatalogId && catalogId && currentCatalogId !== catalogId) {
      atg.commerce.csr.catalog.clearTreeState();
      catalogInfo.currentCatalogId = catalogId;
    } else if (catalogId) {
      catalogInfo.currentCatalogId = catalogId;
    }
  }

};
