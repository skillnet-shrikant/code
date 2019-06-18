dojo.provide( "atg.commerce.csr.cart" );

atg.commerce.csr.cart =
{
  deleteCartItem : function( commerceItemId )
  {
    dojo.byId( "removeCommerceId" ).value = commerceItemId;
    atgSubmitAction({
        form : document.getElementById("deleteItemForm"),
        panelStack : "cmcShoppingCartPS" });
  },

  deleteManualAdjustment : function( adjustmentId, adjustmentReason )
  {
    dojo.byId( "dmaId" ).value = adjustmentId;
    dojo.byId( "dmaReason" ).value = adjustmentReason;
    atgSubmitAction({
        form : document.getElementById("dma"),
        panelStack : "cmcShoppingCartPS" });
  },

  submitNextAction : function ()
  {
    atg.commerce.csr.common.setPropertyOnItems( ['modifyOrderSubmitter'],
      "disabled", true );
    atg.commerce.csr.common.setPropertyOnItems( ['moveToPurchaseInfoSubmitter'],
      "disabled", false );

    atgSubmitAction({
        queryParams : { 'init' : 'true' },
        form : document.getElementById('itemsForm')});
  },



  updatePrice : function ()
  {
    atg.commerce.csr.common.setPropertyOnItems( ['moveToPurchaseInfoSubmitter'],
      "disabled", true );
    atg.commerce.csr.common.setPropertyOnItems( ['modifyOrderSubmitter'],
      "disabled", false );

    atgSubmitAction({
      panelStack : ["cmcShoppingCartPS", "globalPanels"],
      form : document.getElementById('itemsForm'),
      sync: true});
  },


  saveAction : function ()
  {
    atg.commerce.csr.common.setPropertyOnItems( ['moveToPurchaseInfoSubmitter'],
      "disabled", true );
    atg.commerce.csr.common.setPropertyOnItems( ['modifyOrderSubmitter'],
      "disabled", false );

    atgSubmitAction({
      panelStack : ["cmcShoppingCartPS", "globalPanels"],
      form : document.getElementById('itemsForm')});
  },

  // 
  skuChangePopupOkSelected : function( mode, url, childNodeId, radioGroupName, popupId )
  {
    var form = atg.commerce.csr.common.getEnclosingForm( childNodeId );
    
    if ( "apply" == mode ) {
      atg.commerce.csr.common.submitPopup( url, form,
        atg.commerce.csr.common.getEnclosingPopup(childNodeId));
      
    }
    else if ( "return" == mode ) {
      form = dojo.byId('productSkuForm-editSKU-return');
      var checked = atg.commerce.csr.common.getCheckedItem(
        form[radioGroupName]);
      if ( checked != "" )
        checked = checked.value;
      atg.commerce.csr.common.hidePopupWithReturn( popupId,
        { result: 'ok', sku: checked });
    }
  },
  
  adjustmentAmountChanged : function( inputValuePattern ) {
    var amount = dojo.byId("amountTxt").value;
    dojo.byId("adjustmentSubmitButton").disabled = ! amount.match(inputValuePattern);
  },
  
  deleteCartItemByRelationalshipId : function( relationalshipId )
  {
    dojo.byId( "removeRelationshipId" ).value = relationalshipId;
    atgSubmitAction({
        form : document.getElementById("deleteItemByRelationshipIdForm"),
        panelStack : "cmcShoppingCartPS" });
  },
  
  setOrderByRelationshipIdForm : function()
  {
	var finalPrice;
	var finalPriceId;
    var form = document.getElementById("setOrderByRelationshipIdForm");
    var inputs = dojo.query('.ciRelationship');
    var submit = false;
    var setOrderByRelationshipIdFormInnerDiv = document.getElementById("setOrderByRelationshipIdFormInnerDiv");
    if (!setOrderByRelationshipIdFormInnerDiv) {
      var setOrderByRelationshipIdFormInnerDiv = document.createElement("div");
      setOrderByRelationshipIdFormInnerDiv.id = "setOrderByRelationshipIdFormInnerDiv";
      form.appendChild(setOrderByRelationshipIdFormInnerDiv);
    } else {
      setOrderByRelationshipIdFormInnerDiv.innerHTML = '';
    }
    for (i = 0; i < inputs.length; i++) {
      var oldQuantity = dojo.byId(inputs[i].id + "_quantity");
      if (inputs[i].value != 0) {
        if (oldQuantity && oldQuantity.value != inputs[i].value) {
          var node = atg.commerce.csr.catalog.createInputFieldWithoutId(inputs[i].id, "hidden");
          node.value = inputs[i].value;
          submit = true;
          setOrderByRelationshipIdFormInnerDiv.appendChild(node);
        }
      } else {
        atg.commerce.csr.cart.deleteCartItemByRelationalshipId(inputs[i].id);
      }
      finalPriceId = "IPO:" + inputs[i].name ;
      finalPrice = document.getElementById(finalPriceId);
      if(finalPrice)
      	setOrderByRelationshipIdFormInnerDiv.appendChild(finalPrice);
    }
    atgSubmitAction({
        form : form,
        panelStack : "cmcShoppingCartPS" });
  }
};
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
                    var errorMsg = "sku not available for adding to cart";
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
// code to allow for only loading this file once
dojo.provide("atg.commerce.csr");

// register app-specific panels with progress updater
if (dojo.isArray(atg.progress.panels) && dojo.indexOf(atg.progress.panels, "orderSummaryPanel") < 0) {
	atg.progress.panels.push("orderSummaryPanel");
}

//this function has been deprecated and should no longer be used by anything in CSC. 
//it has been left here for backward compatibility
atg.commerce.csr.openOrder = function() {
  atgSubmitAction({url:atg.commerce.csr.getContextRoot()+"/include/orderIsModifiable.jsp",
                   queryParams:{frameworkContext:"/agent"},
                   tab:atg.service.framework.changeTab("commerceTab"),
                   form:"transformForm"});  
};
atg.commerce.csr.createOrder = function() {
  var deferred = atg.progress.update('cmcCustomerSearchPS','createNewOrder');
  deferred.addCallback(function () {
    atgSubmitAction({
      panelStack:["cmcCatalogPS","globalPanels"],
      form:dojo.byId("envNewOrderForm"),
      selectTabbedPanels : ["cmcProductCatalogSearchP"],
      queryParams: { "contentHeader" : true }
    });
  });
};
atg.commerce.csr.commitOrder = function(theFormId) {
  var theForm = document.getElementById(theFormId);
  atgSubmitAction({
    form: theForm,
    panelStack: ["globalPanels"]
  });
};

/**
 * atg.commerce.csr.openPanelStackWithTabbedPanel
 * 
 * Loads a panel stack and lands the agent on the supplied tabbed panel.
 * 
 * @param {String}
 *          panelStack the panelStack Id.
 * @param {String}
 *          tabbedPanel the tabbed panel Id.
 */
atg.commerce.csr.openPanelStackWithTabbedPanel  = function(panelStack, tabbedPanel) {
  return atgSubmitAction({"panelStack":[panelStack],
    "selectTabbedPanels":[tabbedPanel],"form":dojo.byId('transformForm')});
};

/**
 * atg.commerce.csr.openPanelStackWithTabbedPanel
 * 
 * Loads a panel stack and lands the agent on the supplied tabbed panel.
 * 
 * @param {String}
 *          panelStack the panelStack Id.
 * @param {String}
 *          tabbedPanel the tabbed panel Id.
 * @param {String}
 *          tab the tab Id.
 */
atg.commerce.csr.openPanelStackWithTabbedPanel  = function(panelStack, tabbedPanel, tab) {
  return atgSubmitAction({"panelStack":[panelStack],
    "selectTabbedPanels":[tabbedPanel],
    "tab" : atg.service.framework.changeTab(tab),
    "form":dojo.byId('transformForm')});
};

atg.commerce.csr.openPanelStackWithTab  = function(panelStack, tab) {
  return atgSubmitAction({"panelStack":[panelStack],
    "tab" : atg.service.framework.changeTab(tab),
    "form":dojo.byId('transformForm')});
};


atg.commerce.csr.openPanelStack = function(ps) {
  // Include cmcHelpfulPanels in case we're on preferences, which has different side panels
  return atgSubmitAction({"panelStack":[ps,"cmcHelpfulPanels"],"form":dojo.byId('transformForm')});
};
atg.commerce.csr.openPanelStackWithForm = function(ps, f) {
  // Include cmcHelpfulPanels in case we're on preferences, which has different side panels
  return atgSubmitAction({"panelStack":[ps,"cmcHelpfulPanels"],"form":dojo.byId(f)});
};

atg.commerce.csr.openUrl = function(url, frameworkContext) {
  atgSubmitAction({"url":url,
                   "queryParams":{"frameworkContext":frameworkContext},
                   "form":"transformForm"});
};
atg.commerce.csr.initDojo = function() {
  dojo.require("dijit.Menu");
  // framework links widget
  dojo.registerModulePath("framework", atg.commerce.csr.getContextRoot() + "/script/widget");
  dojo.require("framework.FrameworkLink");
};
  /**
   *
   * Sets context root var
   *
   */
  atg.commerce.csr.setContextRoot = function (pContextRoot) {
    window.top.contextRoot = pContextRoot;
  };
  
  /**
   *
   * Gets context root var
   *
   */
  atg.commerce.csr.getContextRoot = function () {
    return window.top.contextRoot;
  };

dojo.addOnLoad(atg.commerce.csr.initDojo);
dojo.provide( "atg.commerce.csr.common" );

//  Will set all checkboxes in the checkboxGroup to be selected or unselected
//  depending on the state of the groupController checkbox.
atg.commerce.csr.common.selectAll = function( groupController, checkboxGroup )
{
  var len;
  var ii=0;
  if ( checkboxGroup ) {
    len = checkboxGroup.length;
    if ( len === undefined ) {
      checkboxGroup.checked = groupController.checked;
      return;
    }
    for( ii=0; ii < len; ii++ ) {
      checkboxGroup[ii].checked = groupController.checked;
    }
  }
};

// Get the checked radio button in the specified radio group
atg.commerce.csr.common.getCheckedItem = function( radioGroup )
{
  if ( !radioGroup ) {
    return "";
  }

  var len = radioGroup.length;
  if ( len === undefined ) {
    if ( radioGroup.checked ) {
      return radioGroup;
    }
    else {
      return "";
    }
  }

  for( var ii=0; ii < len; ii++ ) {
    if ( radioGroup[ii].checked ) {
      return radioGroup[ii];
    }
  }
  return "";
};

// Set the radio button in the specified group, with the specified
// value to checked.
atg.commerce.csr.common.setCheckedItem = function( radioGroup, radioValue )
{
  var len;
  var ii=0;
  if ( radioGroup ) {
    len = radioGroup.length;
    if ( len === undefined ) {
      radioGroup.checked = (radioGroup.value == "checked");
      return;
    }
    for( ii=0; ii < len; ii++ ) {
      radioGroup[ii].checked = false;
      if ( radioGroup[ii].value == radioValue.toString() ) {
        radioGroup[ii].checked = true;
      }
    }
  }
};

// If the DOM element to which ID refers has
// the specified value, set its value to newValue
atg.commerce.csr.common.setIfValue = function( id, value, newValue )
{
  var elem = document.getElementById( id );
  if ( elem.value == value ) {
    elem.value = newValue;
  }
};

  // Set specified property on DOM objects, identified by 'ids'
  // to specified value. The 'ids' parameter may be a single
  // scalar ID value, or an array of IDs.
atg.commerce.csr.common.setPropertyOnItems = function( ids, property, value )
{
  var ii;
  if ( ! dojo.isArray(ids) ) {
    ids = [ ids ];
  }

  if ( ids ) {
    for( ii=0; ii < ids.length; ii++ ) {
      var element = null;
      if (dojo.isObject(ids[ii]) && ids[ii]["form"] && ids[ii]["name"]) {
        // some nodes are form inputs with a form and name, but no DOM ID
        element = document.getElementById(ids[ii]["form"])[ids[ii]["name"]];
      }
      else {
        element = dijit.byId(ids[ii]);
        if (!element) {
          element = dojo.byId(ids[ii]);
        }
      }
      if (element) element[ property ] = value;
    }
  }
};

  // Enable DOM elements identified by enableIds, disable
  // elements identified by disableIds. Both parameters
  // may be arrays of DOM ID strings or a scaler ID string
atg.commerce.csr.common.enableDisable = function ( enableIds, disableIds )
{
  atg.commerce.csr.common.setPropertyOnItems( disableIds,
    "disabled", true );
  atg.commerce.csr.common.setPropertyOnItems( enableIds,
    "disabled", false );
};

atg.commerce.csr.common.submitPopup = function( pURL, pForm, pFloatingPane )
{
  atgSubmitPopup({url:pURL, form:pForm, popup:pFloatingPane});
};

atg.commerce.csr.common.showPopup = function( pFloatingPane, pURL, pTitle )
{
  if (pFloatingPane && pURL) {
    pFloatingPane.titleBarText.innerHTML = pTitle || "";
    pFloatingPane.setUrl(pURL);
    pFloatingPane.show();
  }
};

atg.commerce.csr.common.hidePopup = function( pFloatingPane )
{
  if (pFloatingPane) {
    pFloatingPane.hide();
  }
};

// Return enclosing Dojo Floating Pane given the ID of any child node
atg.commerce.csr.common.getEnclosingPopup = function ( nodeId )
{
  var node = dojo.dom.getAncestors(
    dojo.byId( nodeId ),
    function ( node ) {
      if ( node.className && node.className.indexOf("dijitDialogPaneContent") == -1 ) {
        if ( node.className.indexOf("dijitDialog") >= 0 ) {
          if ( node.tagName == "DIV" ) {
            return true;
          }
        }
      }
      return false;
    },
    true );
  return dijit.byId( node.id );
};

// Return the form that's the parent of the specified child element
atg.commerce.csr.common.getEnclosingForm = function ( childElementId )
{
  return dojo.dom.getAncestors( document.getElementById(childElementId),
    function ( node ) { return "FORM" == node.tagName; }, true );
};

// Associate data with a popup
atg.commerce.csr.common.setPopupData = function ( popupId, name, data )
{
  var old = atg.commerce.csr.common.getPopupData( popupId, name );
  atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)[name] = data;
  return old;
};

// Get data associated with a popup
atg.commerce.csr.common.getPopupData = function ( popupId, name )
{
  return atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)[name];
};

  //
atg.commerce.csr.common.showPopupWithReturn = function ( args )
{
  var popupPane = dijit.byId( args.popupPaneId );
  popupPane._atg_results = {};
  // This function gets called when the popup is closed
  var closeBuddy = function()
  {
    if ( args.onClose ) {
      args.onClose( atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)._atg_results );
    }
  }

  atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)._atg_args = args;

  // need to replace any previously connected function
  if (popupPane.closeHandle) dojo.disconnect(popupPane.closeHandle);
  popupPane.closeHandle = dojo.connect(popupPane, "hide", dojo.hitch(this, closeBuddy, args));

  popupPane.titleNode.innerHTML = args.title || "";

  popupPane.setHref( args.url );
  popupPane.show();
};

  // Hide a popup shown with showPopupWithReturn()
atg.commerce.csr.common.hidePopupWithReturn = function ( childId, results )
{
  var popup = this.getEnclosingPopup( childId );
  popup._atg_results = results;
  popup.hide();
};

  // Hide or show DOM node and change toggler class
  // hidden node should have "hidden_node" class instead of style="display:none;"
atg.commerce.csr.common.toggle = function(togglerHrefId, divId, openedClass, closedClass) {
  dojo.toggleClass(divId,'hidden_node');
  dojo.toggleClass(togglerHrefId, closedClass);
  dojo.toggleClass(togglerHrefId, openedClass);
};

/*
 *
 * This method will work if and only if the form has
 * persistOrder, successURL and errorURL as ids.
 *
 */
atg.commerce.csr.common.prepareFormToPersistOrder  = function (pParams) {
  var localPersistOrder;
  var localSuccessURL;

  if (pParams && pParams.form) {
  
    if(!(pParams.persistOrder == undefined))
    {
      localPersistOrder = document.getElementById(pParams.form).persistOrder;
      localSuccessURL = document.getElementById(pParams.form).successURL;
      localSuccessURL.value = document.getElementById(pParams.form).successURL.value;
      localPersistOrder.value = true;
    }
  }
};

/*
 *
 * This method disables textbox widget
 * If the string is passed in, this method get the widget. Otherwise uses the
 * passed in widget.
 *
 */
atg.commerce.csr.common.disableTextboxWidget  = function (pWidget/**widgetId or Widget **/) {
  var textboxWidget = null;
  if (typeof pWidget === 'string') {
    textboxWidget = dijit.byId(pWidget);
  } else {
    textboxWidget = pWidget;
  }
  if (textboxWidget) {
    textboxWidget.textbox.disabled="true";
  }
};

/*
 * This method disables ComboBox widgets
 *
 */
atg.commerce.csr.common.disableComboBoxWidget  = function (pWidgetId) {
  var comboBoxWidget = dijit.byId(pWidgetId);
  if (comboBoxWidget) {
    comboBoxWidget.domNode.disabled="true";
  }
};

/// override this method, as we have our own panel stack in DCS-CSR
atg.service.framework.togglePanel = function (panelId) {
  frameworkPanelAction("togglePanel", panelId, ["helpfulPanels","cmcHelpfulPanels"]);
};


//--------------------------------------------------------------
// MultiSite Functions
//--------------------------------------------------------------

// Sets the selected site in the Commerce Context Area (CCA) as well as in the SiteContext
atg.commerce.csr.common.setSite = function(siteId, currentSiteId) {
  dojo.debug("MultiSite | atg.commerce.csr.common.setSite called with siteId = " + siteId + " currentSiteId = " + currentSiteId);
  
 // Submit the form to change the current site
  var changeSiteForm = document.getElementById("atg_commerce_csr_loadExistingSiteForm");
    if (changeSiteForm) {
      if(siteId!=currentSiteId){
        atg.commerce.csr.catalog.clearTreeState();
        atg.commerce.csr.catalog.clearAddProductByIdData();
      }
      changeSiteForm.siteId.value = siteId;
      atgSubmitAction({
        selectTabbedPanels : ["cmcProductCatalogSearchP"],
        form: changeSiteForm
      });
    }
}

// Changes the current site by setting the environment based on the siteId parameter
atg.commerce.csr.common.changeSite = function(siteId, pFormId) {
  dojo.debug("MultiSite | atg.commerce.csr.common.changeSite called with siteId = " + siteId);
  if(!pFormId) {
  var theForm = dojo.byId("atg_commerce_csr_productDetailsForm");
  }
  else {
    var theForm =dojo.byId(pFormId);
  }
  
    atgSubmitAction({
      form: theForm,
      sync: true,
      queryParams: { contentHeader : true, siteId: siteId }
    });
}

// Opens the dialog box to search for a site
atg.commerce.csr.common.searchForSite = function() {
  dojo.debug("MultiSite | atg.commerce.csr.common.searchForSite called");
  //atg.commerce.csr.openPanelStack('cmcMultisiteSelectionPickerPS');

  atgSubmitAction({
    sync:true,
    panelStack:["cmcMultisiteSelectionPickerPS"],
    tab: atg.service.framework.changeTab('commerceTab')
  });}

// Opens the dialog box to search for a catalog
atg.commerce.csr.common.searchForCatalog = function() {
  dojo.debug("MultiSite | atg.commerce.csr.common.searchForCatalog called");
  //atg.commerce.csr.openPanelStack('cmcMoreCatalogsPS');
  
  atgSubmitAction({
    sync:true,
    panelStack:["cmcMoreCatalogsPS"],
    tab: atg.service.framework.changeTab('commerceTab')
  });
}

// Opens the dialog box to search for a price list
atg.commerce.csr.common.searchForPricelist = function() {
  dojo.debug("MultiSite | atg.commerce.csr.common.searchForPricelist called");
  //atg.commerce.csr.openPanelStack('cmcMorePriceListsPS');
  
  atgSubmitAction({
    sync:true,
    panelStack:["cmcMorePriceListsPS"]
  });
}

//this function displays message in the message bar
atg.commerce.csr.common.addMessageInMessagebar = function(pType, pMessage) {
 if (dijit.byId('messageBar')) {
  dijit.byId('messageBar').addMessage({type:pType, summary:pMessage});
 }
}

dojo.provide( "atg.commerce.csr.customer" );

atg.commerce.csr.customer =
{
// Adds new credit
  addNewCredit : function () {
    var addNewCreditForm = dojo.byId('addNewCreditForm');
    if (addNewCreditForm) {
      atgSubmitAction(
      {
        form: addNewCreditForm,
        panelStack: ["globalPanels","customerPanels"]
      });
      atg.commerce.csr.common.hidePopupWithReturn('addNewCreditPopup', {result:'ok'});
    }
  },

  isExistingChecked: function () { return dojo.byId('editCreditCardForm')['/atg/commerce/custsvc/repository/CreditCardFormHandler.createNewAddress'][0].checked; },
  isNewChecked: function () { return dojo.byId('editCreditCardForm')['/atg/commerce/custsvc/repository/CreditCardFormHandler.createNewAddress'][1].checked; },

  existingCreditCardAddressChanged : function()
  {
    var id = dojo.byId('editCreditCardForm_existingAddressList').value;

    if ( id !== "" ) {
      dojo.byId('editCreditCardForm').editCreditCardForm_firstName.value = 
        atg.commerce.csr.customer.addrList[id].first;
      dojo.byId('editCreditCardForm').editCreditCardForm_middleName.value = 
        atg.commerce.csr.customer.addrList[id].middle;
      dojo.byId('editCreditCardForm').editCreditCardForm_lastName.value = 
        atg.commerce.csr.customer.addrList[id].last;
      dojo.byId('editCreditCardForm')['/atg/commerce/custsvc/repository/CreditCardFormHandler.value.billingAddress.REPOSITORYID'].value = 
        dojo.byId('editCreditCardForm_existingAddressList').value;
    }
  },
  
  syncToCustomerCatalog : function(pParams) {
    var theForm = document.getElementById("syncCurrentCustomerCatalog");
    atgSubmitAction({
      form : theForm
    });	  
  },

  syncToCustomerPriceLists : function(pParams) {
    var theForm = document.getElementById("syncCurrentCustomerPriceLists");
    atgSubmitAction({
     form : theForm
    });	  
  }
};

atg.commerce.csr.customer.addrList = [];
// -------------------------------------------------------------------
// EA help array structure for CSC
// -------------------------------------------------------------------
atg.ea.registerCSCHelpArray = function () {
  atg.ea.registerHelpArray({ id: "ea_csc_order_search", type: "inline", helpId: "ea_csc_order_search" });
  atg.ea.registerHelpArray({ id: "ea_csc_product_view", type: "inline", helpId: "ea_csc_product_view" });
  atg.ea.registerHelpArray({ id: "ea_csc_product_item_price", type: "popup", helpId: "ea_csc_product_item_price" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit", type: "popup", helpId: "ea_csc_order_submit" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit_footer", type: "popup", helpId: "ea_csc_order_submit_footer" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit_create_schedule", type: "popup", helpId: "ea_csc_order_submit_create_schedule" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit_create_schedule_footer", type: "popup", helpId: "ea_csc_order_submit_create_schedule_footer" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_create_schedule", type: "popup", helpId: "ea_csc_order_create_schedule" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_create_schedule_footer", type: "popup", helpId: "ea_csc_order_create_schedule_footer" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_days_of_week", type: "popup", helpId: "ea_csc_order_scheduled_days_of_week" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_weeks", type: "popup", helpId: "ea_csc_order_scheduled_weeks" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_dates_in_month", type: "popup", helpId: "ea_csc_order_scheduled_dates_in_month" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_actions", type: "popup", helpId: "ea_csc_order_scheduled_actions" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_status_failed", type: "popup", helpId: "ea_csc_order_scheduled_status_failed" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_copy", type: "popup", helpId: "ea_csc_order_copy" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_view_cancel", type: "popup", helpId: "ea_csc_order_view_cancel" });
  atg.ea.registerHelpArray({ id: "ea_csc_purchased_isReturnable", type: "popup", helpId: "ea_csc_purchased_isReturnable" });
  atg.ea.registerHelpArray({ id: "ea_csc_instore_pickup_available", type: "popup", helpId: "ea_csc_instore_pickup_available" });
  atg.ea.registerHelpArray({ id: "ea_csc_instore_pickup_billing_logic", type: "popup", helpId: "ea_csc_instore_pickup_billing_logic" });
};

//console.debug("EA | atg.ea.registerCSCHelpArray called");

dojo.addOnLoad(atg.ea.registerCSCHelpArray);
// -------------------------------------------------------------------
// EA help content definitions for CSC
// -------------------------------------------------------------------
atg.ea.registerCSCHelpContent = function () {
  atg.ea.registerHelpContent({ "id":"ea_csc_order_search", "excerpt":getResource("ea.csc.helpContent.ea_csc_order_search"), "content":"" });
  atg.ea.registerHelpContent({ "id":"ea_csc_product_view", "excerpt":getResource("ea.csc.helpContent.ea_csc_product_view"), "content":"" });
  atg.ea.registerHelpContent({ "id":"ea_csc_product_item_price", "content":getResource("ea.csc.helpContent.ea_csc_product_item_price") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit", "content":getResource("ea.csc.helpContent.ea_csc_order_submit") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit_footer", "content":getResource("ea.csc.helpContent.ea_csc_order_submit_footer") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit_create_schedule", "content":getResource("ea.csc.helpContent.ea_csc_order_submit_create_schedule") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit_create_schedule_footer", "content":getResource("ea.csc.helpContent.ea_csc_order_submit_create_schedule_footer") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_create_schedule", "content":getResource("ea.csc.helpContent.ea_csc_order_create_schedule") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_create_schedule_footer", "content":getResource("ea.csc.helpContent.ea_csc_order_create_schedule_footer") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_days_of_week", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_days_of_week") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_weeks", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_weeks") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_dates_in_month", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_dates_in_month") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_actions", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_actions") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_status_failed", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_status_failed") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_copy", "content":getResource("ea.csc.helpContent.ea_csc_order_copy") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_view_cancel", "content":getResource("ea.csc.helpContent.ea_csc_order_view_cancel") });
  atg.ea.registerHelpContent({ "id":"ea_csc_purchased_isReturnable", "content":getResource("ea.csc.helpContent.ea_csc_purchased_isReturnable") });
  atg.ea.registerHelpContent({ "id":"ea_csc_instore_pickup_available", "content":getResource("ea.csc.helpContent.ea_csc_instore_pickup_available") }); 
  atg.ea.registerHelpContent({ "id":"ea_csc_instore_pickup_billing_logic", "content":getResource("ea.csc.helpContent.ea_csc_instore_pickup_billing_logic") });
};

//console.debug("EA | atg.ea.registerCSCHelpContent called");

dojo.addOnLoad(atg.ea.registerCSCHelpContent);

// -------------------------------------------------------------------
// EA tooltips for CSC
// -------------------------------------------------------------------
atg.ea.registerCSCTooltips = function () {
  atg.ea.registerTooltip("orderLink", getResource("ea.csc.tooltip.orderLink"));
  atg.ea.registerTooltip("orderSave", getResource("ea.csc.tooltip.orderSave"));
  atg.ea.registerTooltip("orderCancel", getResource("ea.csc.tooltip.orderCancel"));
  atg.ea.registerTooltip("submitOrderButton", getResource("ea.csc.tooltip.submitOrderButton"));
  atg.ea.registerTooltip("submitCreateScheduleButton", getResource("ea.csc.tooltip.submitCreateScheduleButton"));
  atg.ea.registerTooltip("createScheduleButton", getResource("ea.csc.tooltip.createScheduleButton"));
};

dojo.subscribe("UpdateGlobalContext", null, function() { 
//  console.debug("EA | atg.ea.registerCSCTooltips called");
  atg.ea.registerCSCTooltips();
});
dojo.provide("atg.commerce.csr.catalog.endeca.search");

atg.commerce.csr.catalog.endeca.search =
{
  //Category Tree functionality
  showHideMainCategories: function (rootCategoriesUrl) {
    if (this._isNotLoadedMainCategories()) {
      this._loadAndShowMainCategories(rootCategoriesUrl);
    } else {
      if (this._isVisibleMainCategories()) {
        this._hideMainCategories();
      } else {
        this._showMainCategories();
      }
    }
  },

  _isNotLoadedMainCategories: function () {
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    if (categoriesObject.categoriesLoaded != true) return true;
    return false;
  },

  _loadAndShowMainCategories: function (rootCategoriesUrl) {
    this._showMainCategories();
    dojo.xhrGet({
      url: rootCategoriesUrl,
      content: {
        _windowid: window.windowId
      },
      encoding: "utf-8",
      handle: function(response, ioArgs) {
        if (!(response instanceof Error)) {
          var value = document.getElementById('atg_commerce_csr_catalog_endeca_categories');
          value.innerHTML = response;
          value.categoriesLoaded = true;
        } else {
          document.getElementById('atg_commerce_csr_catalog_endeca_categories').innerHTML = "An error occured...";
        }
      },
      mimetype: "text/html"
    });
  },

  _isVisibleMainCategories: function () {
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    return categoriesObject.style.display == 'block';
  },

  _hideMainCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    subCategoriesObject.style.display = "none";
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    categoriesObject.style.display = "none";
    $('body').unbind('keyup', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
    $('body').unbind('click', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
  },

  _showMainCategories: function () {
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    categoriesObject.style.display = "block";
    $('body').bind('click', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
    $('body').bind('keyup', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
  },

  showHideSubCategories: function (selection, rootCategoryId, subCategoriesUrl) {
    if (this._isNotLoadedSubCategoryFlyOut(selection)) {
      this._loadAndShowSubCategoryFlyOut(selection, rootCategoryId, subCategoriesUrl);
    } else {
      this._fillSubCategoriesContent(selection);
      this._showSubCategories();
    }
  },

  _isNotLoadedSubCategoryFlyOut: function (selection) {
    return document.getElementById("atg_commerce_csr_catalog_endeca_category" + selection) == null;
  },
  
  _findSubcategoriesCount: function (collection) {
    for (var i = 0; i < collection.length; i++) {
      var levelString = collection[i].getElementsByTagName("a")[0].className;
      var level = parseInt(levelString.substring(5), 10);
      collection[i].level = level;
    }
    for (i = 0; i < collection.length-1; i++) {
      var count = 0;
      for (var j = i + 1; (j < collection.length) && (collection[i].level < collection[j].level); j++) count++;
      collection[i].subCategoryCount = count;
    }
  },

  _convertHTML: function(containerId, maxColumnCount, minRowsInColumn, nonBreakableSubCount) {
    var container = document.getElementById(containerId);
    var collection = container.getElementsByTagName("div");
    atg.commerce.csr.catalog.endeca.search._findSubcategoriesCount(collection);
    
    var rowsInColumn = minRowsInColumn;
    var itemCount = collection.length;
    var columnCount = Math.ceil(itemCount / minRowsInColumn);
    if (columnCount > maxColumnCount) {
      rowsInColumn = Math.ceil(itemCount / maxColumnCount);
      columnCount = maxColumnCount;
    }
    var columnIndex = 0;
    var rowIndex = 0;
    columnElement = document.createElement("TD");
    columnElement.id = "column"+columnIndex;
    columnElement.className = "column"+columnIndex;
    var colLength = collection.length;
    var nonBreakableCount=0;
    var nonBreakableItemBefore=false;
    var rowElement = document.createElement("TR");
    for (var i = 0; i < colLength; i++) {        
      //1) when rowIndex > rowsInColumn;
      //2) when category has less than 5 subcategories and rowIndex + 1+subCategoryCount > rowsInColumn
      var isNotLastColumn = columnIndex < maxColumnCount-1;
      var rowIndexExceeds = rowIndex >= rowsInColumn;
      var haveNonBreakableCat = (collection[0].subCategoryCount > 0) && (collection[0].subCategoryCount <= nonBreakableSubCount);
      var haveNonBreakableCatOnBorder = rowIndex + 1 + collection[0].subCategoryCount > rowsInColumn;
      var rest_of_items = colLength - i;
      var free_space_in_columns_with_error = (maxColumnCount - 1 - columnIndex) * (rowsInColumn -  nonBreakableSubCount - 1);
      var haveEnoughSpaceInNextCol = rest_of_items <= free_space_in_columns_with_error;
      if (haveNonBreakableCatOnBorder && isNotLastColumn && haveNonBreakableCat && (nonBreakableCount == 0)) {
        nonBreakableCount = collection[0].subCategoryCount+1;
      }
      if (  //if end of the row and item breakable or non-breakable but we have already non-breakable item at the border...
        (isNotLastColumn && rowIndexExceeds && ((nonBreakableCount == 0) || nonBreakableItemBefore)) 
        // if non-breakable item at the border and enough space in columns left...
        ||(isNotLastColumn && haveNonBreakableCatOnBorder && haveNonBreakableCat && haveEnoughSpaceInNextCol && (rest_of_items > rowsInColumn-rowIndex)) 
        //if last non-breakable item at the border and free columns left...
        ||(isNotLastColumn && haveNonBreakableCatOnBorder && haveNonBreakableCat && (rest_of_items == nonBreakableCount))) { 
        // ...start a new column
        //container.appendChild(columnElement);
        rowElement.appendChild(columnElement);
        columnIndex++;
        rowIndex=0;
        columnElement = document.createElement("TD");
        /*columnElement.id = "column"+columnIndex;*/
        columnElement.className = "column"+columnIndex;
      } 
      nonBreakableItemBefore = (nonBreakableCount == 1);  //avoid to prohibit new row when non-breakable items goes one by one on a border. 
      if (nonBreakableCount > 0) nonBreakableCount--;
      columnElement.appendChild(collection[0]);
      rowIndex++;
    }
    rowElement.appendChild(columnElement); //last row
    var tableElement = document.createElement("TABLE");
    tableElement.appendChild(rowElement);
    container.appendChild(tableElement);
  },
  

  _loadAndShowSubCategoryFlyOut: function (selection, rootCategoryId, subCategoriesUrl) {
    this._showSubCategories();
    dojo.xhrGet({
      url: subCategoriesUrl,
      content: {
        _windowid: window.windowId,
        rootCategoryId: rootCategoryId,
        categoryNumber: selection
      },
      encoding: "utf-8",
      handle: function(response, ioArgs) {
        if (!(response instanceof Error)) {
          document.getElementById("atg_commerce_csr_catalog_endeca_subcategoryFlyOutContentCache").innerHTML += response;
          atg.commerce.csr.catalog.endeca.search._convertHTML("atg_commerce_csr_catalog_endeca_category" + selection, 3, 15, 5);
          atg.commerce.csr.catalog.endeca.search._fillSubCategoriesContent(selection);
        } else {
          document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories").innerHTML = "An error occured..."; /* TODO: error message */
        }
      },
      mimetype: "text/html"
    });
  },

  _isVisibleSubCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    return subCategoriesObject.style.display == 'block';
  },

  _hideSubCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    subCategoriesObject.style.display = "none";
  },

  _fillSubCategoriesContent: function (selection) {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    var subCategoriesContent = document.getElementById("atg_commerce_csr_catalog_endeca_category" + selection);
    subCategoriesObject.innerHTML = subCategoriesContent.innerHTML;
    var numberResults = this._getNumberResults(selection);
  },

  _showSubCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    subCategoriesObject.style.display = "block";
  },

  _getNumberResults: function (selection) {
    var subCategoriesContent = document.getElementById("atg_commerce_csr_catalog_endeca_category"+selection);
    var inputObject = subCategoriesContent.getElementsByTagName("input")[0];
    return inputObject.value;
  },

  //Category Tree functionality end

  //Autocomplete functionality
  submitSearchTermRequest: function (searchTermURL)
  {
    //replace the search term input with user typed value
    var searchTerm = dojo.byId("searchTermInput").value;      
    var searchURL=searchTermURL.replace("SEARCHTERMINPUT",escape(searchTerm));
    if ($('#searchTermInput').catcomplete) $('#searchTermInput').catcomplete("destroy");
    atgSubmitAction({url: searchURL});
  },
  setupAutoComplete: function (jsonUrl, contentCollectionTemplate, autoSuggestMinLength)
  {
    $(function() {
      $.widget( "custom.catcomplete", $.ui.autocomplete, {
        _renderMenu: function( ul, items ) {
          var self = this, currentCategory = "";
          $.each( items, function( index, item ) {
            if ( item.category != currentCategory ) {
              ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
              currentCategory = item.category;
            }
            self._renderItem( ul, item );
          });
        }
      });

      $( "#searchTermInput" ).catcomplete({
        source: function( request, response ) {
          //replace the search term input with user typed value
          var contentCollectionURI = contentCollectionTemplate.replace("SEARCHTERMINPUT", escape(request.term));
          $.ajax({
            url: jsonUrl,
            dataType: "json",
            data: {
              _windowid: window.windowId,
              contentCollection: contentCollectionURI
            },
            error: function(jqXHR, textStatus, errorThrown) {
              alert('AJAX Error: ' + jqXHR.status);
            },
            success: function( data ) {
              var list = [];
              $(data.dimensionSearchResults.dimensionSearchGroups).each(function(i, dimension) {
                $(dimension.dimensionSearchValues).each(function(j, result) {
                  var label = [];
                  $(result.ancestors).each(function(j, ancestor) {
                    label.push(ancestor.label);
                  });
                  label.push(result.label);
                  list.push({ label: label.join("  >  "), value: result.label, term: request.term, category: dimension.displayName, contentURL: result.contentURL });
                });
              });

              response(list);
            }
          }).done(function( msg ) {
            //We will need to clear out the search box. This could easily be done here
          });
        },
        minLength: autoSuggestMinLength,
        select: function( event, ui ) {
        },
        open: function() {
          $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
        },
        close: function() {
          $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
        },
        appendTo: "#catalog-nav"
      })
      .data("catcomplete")._renderItem = function(ul, item) {
        // highlight only first occurence in words regardless of words or search term capitalization
        var words = item.label.split(" ");
        for (var i = 0; i < words.length; i++) {
          if (words[i].toLowerCase().indexOf(item.term.toLowerCase()) === 0) {
            words[i] = "<span class='highlight'>" + words[i].substr(0, item.term.length) + "</span>" + words[i].substring(item.term.length);
          }
        }
        var lbl = words.join(" ");
        var $link = $("<a>").html(lbl).click(function () {
          atgSubmitAction({url: item.contentURL});
        });
        return $("<li></li>")
        .data("item.autocomplete", item)
        .append($link)
        .appendTo(ul);
      }; 
    });
  }
  //Autocomplete functionality end

}
// -------------------------------------------------------------------
// Keyboard shortcut map for Commerce Service Center
// -------------------------------------------------------------------
atg.keyboard.registerCSCShortcuts = function () {

  atg.keyboard.registerShortcut(
  "ALT+7", {
    shortcut: "ALT + 7",
    name: getResource("keyboard.service.commerceTab.name"),
    description: getResource("keyboard.service.commerceTab.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/service/keyboardShortcut/commerceTab",
    notify: true
   });
	
  atg.keyboard.registerShortcut(
  "CTRL+ALT+SHIFT+S", {
    shortcut: "CTRL + ALT + SHIFT + S",
    name: getResource("keyboard.csc.searchOrder.name"),
    description: getResource("keyboard.csc.searchOrder.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/searchForOrder",
    notify: true
  });

  atg.keyboard.registerShortcut(
  "ALT+SHIFT+F2", {
    shortcut: "ALT + SHIFT + F2",
    name: getResource("keyboard.csc.productCatalog.name"),
    description: getResource("keyboard.csc.productCatalog.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/productCatalog",
    notify: true
  });

  //Changing shortcut from ALT+SHIFT+S to ALT+SHIFT+G due to IE8 Key reservations  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+G", {
    shortcut: "ALT + SHIFT + G",
    name: getResource("keyboard.csc.shipping.name"),
    description: getResource("keyboard.csc.shipping.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/shipping",
    notify: true
  });

  //Changing shortcut from ALT+SHIFT+B to ALT+SHIFT+I due to IE8 key reservations
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+I", {
    shortcut: "ALT + SHIFT + I",
    name: getResource("keyboard.csc.billing.name"),
    description: getResource("keyboard.csc.billing.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/billing",
    notify: true
  });
  
  
};

dojo.addOnLoad(atg.keyboard.registerCSCShortcuts);// -------------------------------------------------------------------
// Keyboard navigation topics for Commerce Service Center
// -------------------------------------------------------------------
atg.keyboard.registerCSCTopics = function () {
	
  dojo.subscribe("/atg/service/keyboardShortcut/commerceTab", null, function() { 
    atg.commerce.csr.openPanelStackWithTab('cmcShoppingCartPS','commerceTab');
	});
	
  
  dojo.subscribe("/atg/csc/keyboardShortcut/createNewOrder", null, function() { 
    atg.commerce.csr.createOrder(); 
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/searchForOrder", null, function() { 
    atg.commerce.csr.openPanelStackWithTab('cmcOrderSearchPS','commerceTab');
  });

  dojo.subscribe("/atg/csc/keyboardShortcut/productCatalog", null, function() { 
    atg.commerce.csr.openPanelStackWithTabbedPanel('cmcCatalogPS','cmcProductCatalogSearchP','commerceTab');
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shoppingCart", null, function() { 
    var _node = dojo.byId("keyboardShortcutShoppingCart");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shipping", null, function() { 
    var _node = dojo.byId("keyboardShortcutShipping");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/billing", null, function() { 
    var _node = dojo.byId("keyboardShortcutBilling");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/completeOrder", null, function() { 
    var _node = dojo.byId("Complete Order");
    if (_node != null) {
      atg.commerce.csr.order.finish.submitOrder('atg_commerce_csr_finishOrderSubmitForm');
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shipToMultiple", null, function() { 
    var _node = dojo.byId("atg_commerce_csr_shipToMultipleAddresses");
    if (_node != null) {
      addressGrid.render();
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shipToOneAddress", null, function() { 
    var _node = dojo.byId("singleShipping");
    if (_node != null) {
      atgNavigate({panelStack:'cmcShippingAddressPS', queryParams:{'mode':'singleShipping'}}); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/saveOrder", null, function() { 
    var _node = dojo.byId("orderSave");
    if (_node != null) {
      atg.commerce.csr.commitOrder('globalCommitOrderForm');
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/orderDetails", null, function() { 
    var _node = dojo.byId("orderLinkAnchor");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/cancelOrder", null, function() { 
    var _node = dojo.byId("orderCancel");
    if (_node != null) {
      _node.onclick(); 
    }
  });

  dojo.subscribe("/atg/csc/keyboardShortcut/createReturnExchange", null, function() { 
    var _node = dojo.byId("createReturnExchange");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/createAppeasementProcess", null, function() { 
	    var _node = dojo.byId("createAppeasementProcess");
	    if (_node != null) {
	      _node.onclick(); 
	    }
	  });

  dojo.subscribe("/atg/service/keyboardShortcut/customerDetails", null, function() { 
    viewCurrentCustomer('commerceTab');
  });
  
}

dojo.addOnLoad(atg.keyboard.registerCSCTopics);dojo.provide( "atg.commerce.csr.order.appeasement" );

/**
 * Creates a new, transient appeasement and opens up the Appeasements UI screen
 */
atg.commerce.csr.order.appeasement.initiateAppeasementProcess = function (pParams){
  var form  = document.getElementById("createAppeasement");
  if(pParams.orderId !== undefined && pParams.orderId !== null)
    form.orderId.value=pParams.orderId;
  atgSubmitAction({
    form:form
  });
};


/**
 * Applies the values input, reason codes and additional notes to the 
 * refund types for the appeasement
 */
atg.commerce.csr.order.appeasement.applyAppeasementRefundValues = function () {
  var appeasementTypeSelect = document.getElementById("atg_commerce_csr_appeasement_appeasementType");
  var typeSelection = appeasementTypeSelect.options[appeasementTypeSelect.selectedIndex].value;
  var appeasementReasonCodeSelect = document.getElementById("atg_commerce_csr_appeasement_appeasementReasonCode");
  var codeSelection = appeasementReasonCodeSelect.options[appeasementReasonCodeSelect.selectedIndex].value;

  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementType").value=typeSelection;
  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementAmount").value=dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value;
  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementReasonCode").value=codeSelection;
  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementNotes").value=dojo.byId("atg_commerce_csr_appeasement_appeasementsCommentsId").value;

  var theForm = dojo.byId("atg_commerce_csr_updateAppeasement");
      atgSubmitAction( {
      form:theForm,
      panelStack: ["globalPanels", "cmcAppeasementsPS"]});
};
    

/**
 * Cancels the appeasement in progress and returns Agent to the Existing Orders screen
 */
atg.commerce.csr.order.appeasement.cancelAppeasement = function () {
 var form  = document.getElementById('cancelAppeasement');

 var deferred = atgSubmitAction({form:form});
 deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};


/**
 * This function sets up the appeasement UI, hiding the appropriate input area based on 
 * the discount type selected (amount or percent). This function takes the radio button
 * that was selected as the param. 
 */
atg.commerce.csr.order.appeasement.setAppeasementValues = function (el){

   console.log("In setAppeasementValue " + el.value);
   if (el.value == "selectAmountOff") {
     console.log("Hiding percent text box");
     dojo.byId("atg_commerce_csr_appeasement_appeasementAmountInputDiv").style.display = 'inline-block';
     dojo.byId("atg_commerce_csr_appeasement_appeasementPercentInputDiv").style.display = 'none';
   }
   else {
     console.log("Hiding amount text box");
     dojo.byId("atg_commerce_csr_appeasement_appeasementAmountInputDiv").style.display = 'none';
     dojo.byId("atg_commerce_csr_appeasement_appeasementPercentInputDiv").style.display = 'inline-block';
   }
   dojo.byId("atg_commerce_csr_appeasement_amountOrPercentageSelectedValue").value = el.value;
 };
 
 
 /**
  * This function is responsible for displaying the calculated discount amount. 
  * It performs certain sanity checks on the inputted data, including whether all required 
  * fields are populated. If data validation fails it disables the apply button and blanks 
  * the total discount amount. If data validation passes (including calculating the 
  * percentage amount), then the formatted total discount amount is displayed and the 
  * apply button enabled.
  */
atg.commerce.csr.order.appeasement.displayAppeasementAmount = function(){
    var currencyCode = document.getElementById("atg_commerce_csr_order_appeasements_activeCurrencyCode").value;
     
    var spanTotal = dojo.byId("atg_commerce_csr_appeasement_total");
    var balance;
    var result = 0;
    var disable = true;
    var validAmount = false;

    // Set which balance we are using. Shipping or Items.
    if (dojo.byId("atg_commerce_csr_appeasement_appeasementType").value === "shipping"){
      balance = dojo.byId("shippingBalance").value;
    }
    else {
      balance = dojo.byId("itemBalance").value;
    }
   
    // If the discount type is percent off then we have some calculations to do. 
    // We calculate the discount amount on the client side based on the percentage of
    // the balance.
    if(dojo.byId("atg_commerce_csr_appeasement_amountOrPercentageSelectedValue").value === "selectPercentageOff"){
      var percentOff = dojo.byId("atg_commerce_csr_appeasement_appeasementPercentValue").value;
      console.log("Precent off " + percentOff);
     
      // Validate the percentage
      if(!isNaN(percentOff) && percentOff <= 100 && percentOff > 0){
        result = balance / 100 * percentOff;
      }
     
      // If a valid result was calculated the we need to format that
      // into a Currency value for display.
      if (result > 0) {
        console.log("Calculated percent off " + result);
        validAmount = true;
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = atg.commerce.csr.order.billing.roundAmount(result);
        
        //display appeasement total on screen
        var formattedAmount = atg.commerce.csr.order.billing.formatAmount(result, container.currencyCode);
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = formattedAmount;
        } else {
          spanTotal.innerText = formattedAmount;
        }
           
      } 
      else {
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = 0;
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = "";
        } else {
          spanTotal.innerText = "";
        }
        
      }
    }
    else{
      // If an discount amount was entered then the calculations are easier. 
      // We just need to make sure that our balance does not end up in negative figures.
      // Again any valid result needs to formatted for display as a currency value.
      var amountValue = dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue").value;
      var amountOff = atg.commerce.csr.order.billing.parseAmount (amountValue);
      
      if(!isNaN(amountOff) && (amountOff <= balance) && (amountOff > 0)){
        var formattedAmount = atg.commerce.csr.order.billing.formatAmount(amountOff, container.currencyCode);
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = formattedAmount;
        } else {
          spanTotal.innerText = formattedAmount;
        }
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = atg.commerce.csr.order.billing.roundAmount(amountOff);
        validAmount = true;
        console.log("Valid amountoff  " + amountOff);
      }
      else {
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = 0;
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = "";
        } else {
          spanTotal.innerText = "";
        }
      }
    }
   
    // Validate all fields so we can enable/disable the apply button
    var appeasementReasonCodeSelect = document.getElementById("atg_commerce_csr_appeasement_appeasementReasonCode");
    var codeSelection = appeasementReasonCodeSelect.options[appeasementReasonCodeSelect.selectedIndex].value;
   
    console.log("The Reason Code selection is " + codeSelection + " valid amount " + validAmount);
   
    if (validAmount && dojo.string.trim(codeSelection).length > 0) {
      console.log("Enabling the apply appeasements button");
      disable = false;
    }

   dojo.byId("applyAppeasementValuesButton").disabled = disable;
   
 };
 
 /**
  * This function navigates the Agent to the appeasement summary page 
  */
atg.commerce.csr.order.appeasement.openAppeasementSummary = function() {
  console.debug("Calling atg.commerce.csr.appeasements.openAppeasementsPage");
  var form  = document.getElementById('csrApplyAppeasements');
    atgSubmitAction({form:form});
  
};
/**
 * Send an appeasement confirmation message.
 */
atg.commerce.csr.order.appeasement.sendConfirmationMessage = function (){
  atgSubmitAction({
    form:document.getElementById("csrSendAppeasementConfirmationMessage")
  });
};
atg.commerce.csr.order.appeasement.openConfirmAppeasement = function() {
  console.debug("Calling atg.commerce.csr.order.appeasement.openConfirmAppeasement");
  atg.commerce.csr.openPanelStack('cmcConfirmAppeasementPS');
};
    
atg.commerce.csr.order.appeasement.submitAppeasement = function() {
  console.debug("Calling atg.commerce.csr.order.appeasement.submitAppeasement");
  var form  = document.getElementById('csrSubmitAppeasement');
  atgSubmitAction({form:form});
};    

atg.commerce.csr.order.appeasement.selectAppeasementHistory = function (pAppeasementId){
  atgSubmitAction(
      {
        panels:["cmcAppeasementHistoryP"],
        form:dojo.byId('transformForm'),
        queryParams: { "historyAppeasementId" : pAppeasementId }
      }
  );
};dojo.provide( "atg.commerce.csr.order.billing" );

dojo.require("dojox.i18n.currency");
dojo.require("dojox.i18n.number");
dojo.require("dojo.date");
dojo.require("dojox.validate._base");
dojo.require("dojox.validate.creditCard");

atg.commerce.csr.order.billing.container = null;

atg.commerce.csr.order.billing.creditCardTypeDataContainer = null;

atg.commerce.csr.order.billing.applyPaymentGroups = function (pParams){
  var form  = document.getElementById("csrBillingForm");
  
  if (container && container.availablePaymentMethods) {
    for (id in container.availablePaymentMethods) {
      var paymentMethod = container.availablePaymentMethods[id];
      if (paymentMethod.paymentGroupType == 'inStorePayment') {
        var paymentInput = dijit.byId(paymentMethod.paymentGroupId);
        var paymentCheckbox = dojo.byId(paymentMethod.paymentGroupId + "_checkbox");
        var paymentRelationshipType = dojo.byId(paymentMethod.paymentGroupId + "_relationshipType");
        if (paymentInput && paymentCheckbox && !paymentCheckbox.checked) {
          paymentInput.setValue(0);
        }
        if (paymentInput && paymentRelationshipType && paymentCheckbox && !paymentCheckbox.checked) {
          paymentRelationshipType.value = 'ORDERAMOUNT';
        }
      }
    }
  }
  
  atg.commerce.csr.common.enableDisable([{form: "csrBillingForm", name: "csrHandleApplyPaymentGroups"}],
      [{form: "csrBillingForm", name: "csrPaymentGroupsPreserveUserInputOnServerSide"}]);
  atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.billing.savePaymentGroups = function (pParams){
  var form  = document.getElementById("csrBillingForm");
  atg.commerce.csr.common.enableDisable([{form: "csrBillingForm", name: "csrHandleApplyPaymentGroups"}],
      [{form: "csrBillingForm", name: "csrPaymentGroupsPreserveUserInputOnServerSide"}]);
  atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
  atgSubmitAction({
    panelStack:["globalPanels"],
    form:form});
};

atg.commerce.csr.order.billing.claimClaimables = function (){
  atgSubmitAction({form:dojo.byId("csrBillingClaimableForm")});
};

atg.commerce.csr.order.billing.editCreditCard = function(pURL){
  atg.commerce.csr.common.submitPopup(pURL, document.getElementById("csrBillingEditCreditCard"), dijit.byId("editPaymentOptionFloatingPane"));
};

atg.commerce.csr.order.billing.addCreditCard = function (){
  atgSubmitAction({form:dojo.byId("csrBillingAddCreditCard"),panelStack: ["globalPanels"]});
};

atg.commerce.csr.order.billing.renderBillingPage = function (){
  atgNavigate({panelStack:'cmcBillingPS'});
};

/**
 * This method initializes the payment container
 *
 */
atg.commerce.csr.order.billing.initializePaymentContainer = function(pOrderTotal, pCurrencyCode, pParams){
  container = new atg.commerce.csr.order.billing.CSRPaymentContainer();
  container.initialize(pOrderTotal, pCurrencyCode, pParams);
};

/**
 * This method adds the payment method to the container.
 *
 * @param paymentGroupId : pPaymentGroupId
 * @param amount : pAmount
 * @param maxAllowedAmount : pMaxAllowedAmount -- optional
 *
 */
atg.commerce.csr.order.billing.addPaymentMethod = function (pParams){
  dojo.debug("addPaymentMethod: " + pParams.paymentGroupId + " amount " + pParams.amount);
  //create javascript object and add to the container
  var methodsInstance = new atg.commerce.csr.order.billing.AvailablePaymentMethod (pParams);
  container.addAvailablePaymentMethod(methodsInstance);
};// end of addPaymentMethod

/**
 * This method initializes the credit card type data container
 *
 */
atg.commerce.csr.order.billing.initializeCreditCardTypeDataContainer = function(){
  creditCardTypeDataContainer = new atg.commerce.csr.order.billing.CSRCreditCardTypeDataContainer();
  creditCardTypeDataContainer.initialize();
};

/**
 * This method adds the credit card type to the container.
 */
atg.commerce.csr.order.billing.addCreditCardTypeData = function (pCardType, pCode){
  //create javascript object and add to the container
  var methodsInstance = new atg.commerce.csr.order.billing.CreditCardTypeData (pCardType,pCode);
  creditCardTypeDataContainer.addCreditCardTypeData(methodsInstance);
};// end of addCreditCardTypeData

/**
 * This method fires the event with balance amount.
 *
 */
atg.commerce.csr.order.billing.firePaymentBalanceDojoEvent = function (){
  container.firePaymentBalanceDojoEvent();
};// end of firePaymentBalanceDojoEvent

/**
 * This method listens for the balance amount event and if the amount is zero,
 * this it disable the next button. Also displays the balance amount.
 */
atg.commerce.csr.order.billing.paymentBalanceEventListener  = function (pEvent) {
  if ( !pEvent) {
    return;
  }
  var formattedBalanceAmount;
  var balanceAmount = pEvent.balance;
  if ((balanceAmount * 1) === 0 ) {
    this.disableCheckoutButtons(false);
  } else {
    this.disableCheckoutButtons(true);
  }
  balanceDivtag = document.getElementById('displayCSRCustomerPaymentBalance');
  if (balanceDivtag) {
    formattedBalanceAmount = this.formatAmount(balanceAmount, container.currencyCode);
    if (typeof formattedBalanceAmount !== 'undefined') {
      balanceDivtag.innerHTML=formattedBalanceAmount;
    }
  }
};

/**
 * This method disables the next button in the billing page and returns page.
 */
atg.commerce.csr.order.billing.disableCheckoutButtons = function (pFlag) {
  var nextButton = dijit.byId('checkoutFooterNextButton');
  if (nextButton) {
    pFlag ? nextButton.disableButton() : nextButton.enableButton();
  }
  var saveButton = dijit.byId('checkoutFooterSaveButton');
  if (saveButton) {
    pFlag ? saveButton.disableButton() : saveButton.enableButton();
  }
};

/**
 * This method is called from the credit card, store credit and gift certificate page. When the users changes the
 * amount in any of the above payment types, then the payment balance is recomputed.
 *
 */
atg.commerce.csr.order.billing.recalculatePaymentBalance  = function (pParams) {

  //on key press, get the payment group name and amount value.
  //deduct the differences.
  if (!pParams.pmtWidget) {
    dojo.debug("The payment widget is not available.");
    return;
  }
  paymentGroupId = pParams.pmtWidget.id;

  var paymentMethod = container.getAvailablePaymentMethodByKey(paymentGroupId);
  if(!paymentMethod ){return;}

  var currentCurrencyWidget = pParams.pmtWidget;

  var newAmount = currentCurrencyWidget.getValue()*1;
  var oldAmount = paymentMethod.amount*1;
  var balanceAmount = container.balance*1;
  var zeroAmount = 0.0 *1;

  var validOpResultHolder = null;

  if (isNaN(newAmount)) {
    var errormessage = getResource('csc.billing.invalidAmount');
    currentCurrencyWidget.invalidMessage=errormessage;
    atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
    return false;
  }

  if (this.isMaxAmountDefined(paymentMethod)) {

    //If the new amount is equal to or greater than the max allowed amount, then the
    //amount is considered as maximum amount reached. If the newAmount === maxAllowedAmount,
    //we should not display error message.

    if (this.isMaxAmountReached(paymentMethod, newAmount)) {
      var remainingAmount = this.calculateRemainingAmount (paymentMethod, newAmount);
      if (typeof remainingAmount !== 'undefined' && ((remainingAmount*1)===zeroAmount) ) {
        paymentMethod.amount = newAmount;
        if (newAmount >= oldAmount) {
          container.balance = this.roundAmount(balanceAmount + (newAmount - oldAmount));
        } else {
          container.balance = this.roundAmount(balanceAmount + (oldAmount - newAmount));
        }
        //after this make sure that we have the correct amount, by executing the
        //reconsileBalanceAmount().
      } else {
        currentCurrencyWidget.state = "Error";
        currentCurrencyWidget._setStateClass();
        currentCurrencyWidget.displayMessage(getResource('csc.billing.invalidMaximumLimit'));
        currentCurrencyWidget.setValue(oldAmount);
        currentCurrencyWidget.validate(false);
        //after this make sure that we have the correct amount, by executing the
        //reconsileBalanceAmount().
      }
    } else {
      paymentMethod.amount = newAmount;
      if (newAmount >= oldAmount) {
        container.balance = this.roundAmount(balanceAmount + (newAmount - oldAmount));
      } else {
        container.balance = this.roundAmount(balanceAmount + (oldAmount - newAmount));
      }
    }
  } else {
    //User is replacing the old amount with new amount. Thus add the old amount back to the balance and deduct the new
    //amount.

    //if the amount is zero, it is not going through this loop.
    //thus adding zero check.

    if (oldAmount || (oldAmount === 0)) {
      balanceAmount = this.roundAmount(balanceAmount + oldAmount);
    }

    //if the amount is zero, it is not going through this loop.
    //thus adding zero check.
    if (newAmount || (newAmount === 0)) {
      balanceAmount = this.roundAmount(balanceAmount - newAmount);
      paymentMethod.amount = newAmount;
    }
    container.balance = balanceAmount;
  }
  this.reconsileBalanceAmount();
  this.firePaymentBalanceDojoEvent();
  return true;
};

/**
 * This method is to make sure that the order amount and user entered amount is not out of sync.
 *
 */
atg.commerce.csr.order.billing.reconsileBalanceAmount = function ()  {

  var allPayments = container.availablePaymentMethods;
  if (!allPayments) {
    container.balance = container.amountDue;
    return;
  }
  var paymentTotal = 0;
  var i = 0;
  for(i = 0; i < allPayments.length; i++){
    dojo.debug("before adding amount for paymentGroup" + allPayments[i].paymentGroupId + " amount :" + paymentTotal);
    paymentTotal = this.roundAmount((paymentTotal*1) + (allPayments[i].amount * 1));
    dojo.debug("after adding amount for paymentGroup" + allPayments[i].paymentGroupId + " amount :" + paymentTotal);
  }

  var actualBalance = this.roundAmount((container.amountDue * 1) - (paymentTotal * 1));
  dojo.debug("Actual balance is ::" + actualBalance);
  dojo.debug("Calculated balance is ::" + container.balance);

  if (actualBalance == container.balance) {
    dojo.debug("There is no problem with the calculation");
  } else {
    dojo.debug("There is a difference with the calculation. Resetting actual amount.");
    container.balance = actualBalance;
  }
};

/**
 * This method is called from the credit card, store credit and gift certificate page. When the users prefers to
 * apply the remaining amount to a payment group, then this method is called and then the payment balance is recomputed.
 *
 */
atg.commerce.csr.order.billing.applyRemainder  = function (pParams) {

  if (!this.csrBillingFormValidate()) {
    dojo.debug("The payment form contains errors.");
    var errormessage = getResource('csc.billing.form.error');
    atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
    return;
  }
  
  if (!pParams.pmtWidget) {
    dojo.debug("The payment widget is not available.");
    return;
  }
  var paymentGroupId = pParams.pmtWidget.id;

  var paymentMethod = container.getAvailablePaymentMethodByKey(paymentGroupId);
  if(!paymentMethod){
    return;
  }

  var currentCurrencyWidget = pParams.pmtWidget;

  if (this.isZeroBalance()) {
    var errormessage = getResource('csc.billing.zeroBalance');
    currentCurrencyWidget.invalidMessage=errormessage;
    atg.commerce.csr.common.addMessageInMessagebar ("warning", errormessage);
    this.firePaymentBalanceDojoEvent();
    //here we do not want to set the widget is invalid, because this is just an warning.
    return false;
  }

  var balanceAmount = container.balance*1;
  var currentAmount = paymentMethod.amount*1;
  var sumAmount = this.roundAmount(currentAmount + balanceAmount);
  var newAmount = sumAmount*1;
  var zeroAmount = 0.0 *1;
  var possibleMaxAmount = 0.0*1;

  if (this.isMaxAmountDefined(paymentMethod)) {
    if (this.isMaxAmountReached(paymentMethod, currentAmount)) {
      if (balanceAmount >= zeroAmount) {
        currentCurrencyWidget.state = "Error";
        currentCurrencyWidget._setStateClass();
        currentCurrencyWidget.displayMessage(getResource('csc.billing.maxAmountReached'));
        currentCurrencyWidget.setValue(currentAmount);
        currentCurrencyWidget.validate(false);
        //if the maximum amount is already reached, do not assign more.
        return false;
      } else {
        //if the balance is negative, then we need to reduce the balance to the maximum possible
        //amount.
        if ((balanceAmount * -1) >= currentAmount) {
          currentCurrencyWidget.setValue(zeroAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = zeroAmount;
          container.balance = this.roundAmount(balanceAmount + currentAmount);
        } else {
          possibleMaxAmount = this.roundAmount(balanceAmount + currentAmount);
          currentCurrencyWidget.setValue(possibleMaxAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = possibleMaxAmount;
          container.balance = zeroAmount;
        }
      }
    } else {
      var remainingAmount = this.calculateRemainingAmount (paymentMethod, currentAmount);
      if (remainingAmount && !((remainingAmount *1)  === 0)) {
        //remaining amount > balance, then sum the payment method amount and the balance,
        //that will be less than the maximum allowed amount.
        if (remainingAmount*1  >= balanceAmount*1) {
          currentCurrencyWidget.setValue(newAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = newAmount;
          container.balance = 0.00;
        } else {
          possibleMaxAmount = this.roundAmount((currentAmount*1) + (remainingAmount*1));
          currentCurrencyWidget.setValue(possibleMaxAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = possibleMaxAmount;
          container.balance = this.roundAmount((container.balance*1) - (remainingAmount*1));
        }
      }
    }
  } else {
    if ((((newAmount * 1) === 0) || ((newAmount * 1) > 0))) {
      if (this.isValidAmount(newAmount, container.currencyCode)) {
        currentCurrencyWidget.setValue(newAmount);
        currentCurrencyWidget.validate(false);
        paymentMethod.amount = newAmount;
        container.balance = 0.00;
      }
    } else {
      var errormessage = getResource('csc.billing.negativeAmount');
      currentCurrencyWidget.invalidMessage=errormessage;
      atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
      //here we do not want to set the widget is invalid, because this is just an warning.
    }
  }
  this.reconsileBalanceAmount();
  this.firePaymentBalanceDojoEvent();
};

/**
 * This method is responsible for handling instore pickup shipping group checkbox checking/unckecking
 *
 */
atg.commerce.csr.order.billing.checkInStorePaymentCheckbox = function(checkbox, inputNode, textNode, amount, fullAmount) {
  if (checkbox.checked) {
    document.getElementById(textNode).innerHTML = fullAmount;
    var paymentMethod = container.getAvailablePaymentMethodByKey(inputNode);
    if (parseInt(amount)) {
      document.getElementById(inputNode).value = amount;
    } else {
      document.getElementById(inputNode).value = container.balance;
    }
    if (parseInt(paymentMethod.amount)) {
      paymentMethod.initialAmount = paymentMethod.amount;
    } else {
      if (!parseInt(paymentMethod.initialAmount) && !parseInt(paymentMethod.amount)) {
        paymentMethod.amount = container.balance;
      } else {
        paymentMethod.amount = paymentMethod.initialAmount;
      }
    }
  } else {
    document.getElementById(inputNode).value = 0;
    document.getElementById(textNode).innerHTML = 0;
    var paymentMethod = container.getAvailablePaymentMethodByKey(inputNode);
    paymentMethod.amount = "0.0";
  }
  atg.commerce.csr.order.billing.reconsileBalanceAmount();
  this.firePaymentBalanceDojoEvent();
};

/**
 *
 * This method is to identify whether the maximum amount is defined and this helps to allocate
 * amount properly.
 *
 */
atg.commerce.csr.order.billing.isMaxAmountDefined = function (pPaymentMethod) {

  if (!pPaymentMethod) {
    return false;
  }
  var maxAllowedAmount = pPaymentMethod.maxAllowedAmount;

  //if the maxAllowedAmount is not set, the application does not need to check the limit.
  if (!maxAllowedAmount) {
    return false;
  }

  //if the maxAllowedAmount is Infinity, the application does not need to check the limit.
  if (maxAllowedAmount === 'Infinity') {
    return false;
  }

  if (typeof maxAllowedAmount != 'undefined'  && ((maxAllowedAmount * 1) >= 0*1)) {
    return true;
  }

  return false;

};


/**
 *
 * Each payment method has an maximum amount. If the maximum allowed amount is set and if the computed
 * amount is greater than the allowed amount, this method return true. Otherwise false.
 *
 */
atg.commerce.csr.order.billing.isMaxAmountReached = function (pPaymentMethod, pAmount) {

  if (this.isMaxAmountDefined (pPaymentMethod)) {
    var maxAllowedAmount = pPaymentMethod.maxAllowedAmount;
    if ((maxAllowedAmount * 1) <= (pAmount * 1)) {
      return true;
    }
  }
  return false;
};

/**
 * Each payment method has an maximum amount. If the maximum allowed amount is set and if the computed
 * amount is greater than the allowed amount, this method return true. Otherwise false.
 *
 */
atg.commerce.csr.order.billing.calculateRemainingAmount = function (pPaymentMethod, pAmount) {
  var maxAllowedAmount;
  var zeroAmount = 0.0 * 1;
  //This provides an additional check. We are making sure that we are not working with undefined
  //or with infinity.


  if (!atg.commerce.csr.order.billing.isMaxAmountReached(pPaymentMethod, pAmount)) {
    maxAllowedAmount = pPaymentMethod.maxAllowedAmount;
    if ((maxAllowedAmount*1) === (pAmount * 1)) {
      return zeroAmount;
    } else {
      return this.roundAmount((maxAllowedAmount*1) - (pAmount * 1));
    }
  } else {
    if (atg.commerce.csr.order.billing.isMaxAmountDefined(pPaymentMethod)) {
      maxAllowedAmount = pPaymentMethod.maxAllowedAmount;
      if ((maxAllowedAmount*1) === (pAmount * 1)) {
        return zeroAmount;
      }
    }
  }
};


atg.commerce.csr.order.billing.existingCreditCardAddressSelectedRule = function (arguments1, arguments2, arguments3) {
  return atg.commerce.csr.order.billing.selectionRule (arguments1, arguments2, 'true');
};

atg.commerce.csr.order.billing.newCreditCardAddressSelectedRule = function (arguments1, arguments2) {
  return atg.commerce.csr.order.billing.selectionRule (arguments1, arguments2, 'false');
};

atg.commerce.csr.order.billing.selectionRule = function (arguments1, arguments2, arguments3) {
  var form = document.getElementById(arguments1);
  var radio = form.elements [arguments2];
  if(!radio){return false;}

  var checkedValue;
  //find the radio value checked

  //this means there is only one radio button in the
  if (radio.length === undefined ) {
    checkedValue = radio.value;
  } else {
    for(i = 0; i < radio.length; i++){
      if(radio[i].checked){
        checkedValue = radio[i].value;
        break;
      }
    }// end of for loop
  }

  if (checkedValue === arguments3) {
    return true;
  } else {
    return false;
  }
};

atg.commerce.csr.order.billing.notifyAddNewCreditCardValidators = function () {

};

atg.commerce.csr.order.billing.notifyEditCreditCardValidators = function () {
};

/**
 *  First this method checks to see if the amount is valid. Sometimes, the user may not enter the data with
 *  appropriate comma separator between the number. If the user does not enter the valid separator, this method
 *  formats and parses again. As a result this method call, this method returns the result of the validation operation.
 */
atg.commerce.csr.order.billing.isValidAmount = function (pAmount, pCurrencyCode, pFlags/*optional*/) {

  if (typeof pAmount === 'undefined') {
    return false;
  }
  if (isNaN(pAmount)) {
    result = this.parseAmount(pAmount, pFlags);
    if (isNaN(result)) {
      return false;
    } else {
      return true;
    }
  } else {
    return true;
  }
};

/**
 * This method returns true, if the balance is zero. Otherwise false
 */
atg.commerce.csr.order.billing.isZeroBalance = function () {
  if ((container.balance * 1) === 0 ) {
    return true;
  }
  return false;
};

/**
 * This method parses the amount.
 */
atg.commerce.csr.order.billing.parseAmount = function (pAmount, pFlags/*optional*/) {
  var result;

  if (typeof pAmount === 'undefined') {
    return;
  }

  if (typeof pFlags == 'undefined') {
    pFlags = {
        places : ('places' in container) ? container.places : 2,
        fractional : true,
        locale : container.locale,
        currency : container.currencyCode,
        symbol : container.currencySymbol
    };
  }
  result = dojo.currency.parse (pAmount, pFlags);
  return result;
};

/**
 * This method formats the given amount.
 */
atg.commerce.csr.order.billing.formatAmount = function (pAmount, pCurrencyCode, pRemoveCurrencySymbol, pFlags/*optional*/) {
  if (typeof pFlags == 'undefined') {
    pFlags = {
        places : ('places' in container) ? container.places : 2,
        round : true,
        locale : container.locale,
        currency : container.currencyCode,
        symbol : container.currencySymbol
    };
  }

  if (typeof pAmount === 'undefined') {
    return;
  }

  if (typeof pCurrencyCode === 'undefined' || pCurrencyCode === '') {
    return;
  }

  return dojo.currency.format(pAmount, pFlags);
};

/**
 *
 * There is a possibility that a credit card can be expired. In case if the credit card is expired,
 * the amount and cvv fields should be disabled.
 *
 */
atg.commerce.csr.order.billing.disableExpiredCreditCardControls = function (pParams) {

  var paymentWidgetId = null;
  var cvvWidgetId = null;
  var paymentWidget = null;
  var cvvWidget = null;
  var currentAmount = 0.0;
  var paymentMethod = null;

  if (pParams.paymentWidgetId) {
    paymentWidgetId = pParams.paymentWidgetId;
    paymentWidget = dijit.byId(paymentWidgetId);
    if (paymentWidget) {
      atg.commerce.csr.common.disableTextboxWidget(paymentWidget);
      currentAmount = paymentWidget.getValue();
      if ((currentAmount * 1) > 0) {
        container.balance =  (container.balance *1) + (currentAmount * 1);
        paymentMethod = container.getAvailablePaymentMethodByKey(paymentWidgetId);
        if (paymentMethod) {
          paymentMethod.amount = 0.0;
        }
        this.reconsileBalanceAmount();
        this.firePaymentBalanceDojoEvent();
      }
      paymentWidget.setValue('0.0');
    }
  }

  if (pParams.cvv) {
    cvvWidgetId = pParams.cvv;
    cvvWidget = dijit.byId(cvvWidgetId);
    if (cvvWidget) {
      atg.commerce.csr.common.disableTextboxWidget(cvvWidget);
    }
  }
};

atg.commerce.csr.order.billing.saveUserInput = function (pParams) {
  dojo.debug("entering saveUserInput()");
  var form  = document.getElementById("csrBillingForm");
  atg.commerce.csr.common.enableDisable([{form: "csrBillingForm", name: "csrPaymentGroupsPreserveUserInputOnServerSide"}],
      [{form: "csrBillingForm", name: "csrHandleApplyPaymentGroups"}]);
  var d = atgSubmitAction({form:form, handleAs: 'json'});
  var result = d.addCallback(function(result){ return result; });
  var value;
  if (result)  {
    value = result.error;
    if (!value) {
      dojo.debug("There is no error in saving user data.");
      return true;
    }
  }
  dojo.debug("leaving saveUserInput()");
  return false;
};

atg.commerce.csr.order.billing.isValidCreditCardMonth = function (pMonthWidget, pYearWidget) {

  if (!pMonthWidget) {
    return false;
  }

  var month = pMonthWidget.getValue();
  var monthNumberIns = new Number (month);

  if (month === '' || !((typeof monthNumberIns == 'number') || (monthNumberIns instanceof Number))) {
    dojo.debug("Supplied month is not a number.");
    pMonthWidget.invalidMessage=getResource('csc.billing.invalidMonth');
    return false;
  }
  
  // if the year widget is not valid, then we do not need to test
  // month and year combination. Year is invalid anyway.
  // by returning true, this function says that month widget is not an issue
  if (!this.isValidCreditCardYear(pYearWidget)) {
    return true;
  }
  
  //if the month and year combination is invalid, there is only one possible wrong answer, that is the month
  // is not valid
  if (!this.isValidCreditCardExpDate(pMonthWidget, pYearWidget)) {
    pMonthWidget.invalidMessage=getResource('csc.billing.invalidMonth');
    return false;
  }
  
  return true;
};

atg.commerce.csr.order.billing.isValidCreditCardYear = function (pYearWidget) {

  if (!pYearWidget) {
    return false;
  }

  var year = pYearWidget.getValue();
  var yearNumberIns = new Number (year);

  if (year === '' || !((typeof yearNumberIns == 'number') || (yearNumberIns instanceof Number))) {
    dojo.debug("Supplied year is not a number.");
    pYearWidget.invalidMessage=getResource('csc.billing.invalidYear');
    return false;
  }
  return true;
};

atg.commerce.csr.order.billing.isValidCreditCardExpDate = function (pMonthWidget, pYearWidget) {

  var month = pMonthWidget.getValue();
  var year = pYearWidget.getValue();

  var monthNumberIns = new Number (month);
  var yearNumberIns = new Number (year);

  dojo.debug("month and year is valid numbers.");
  var today = new Date();
  var jsMonth = monthNumberIns - 1;
  var expiryDate = new Date(yearNumberIns, jsMonth);  
  var daysInMonth = dojo.date.getDaysInMonth (expiryDate);
  //month is deducted by 1 to accommodate the difference between the month drop down and
  // to satisfy the javascript condition.
  expiryDate = new Date(yearNumberIns, jsMonth, daysInMonth);
  if (dojo.date.compare (today, expiryDate,"day") > 0) {
    dojo.debug("You can't choose the non-current date.");
    return false;
  } else {
    dojo.debug("User selected a future date.");
    return true;
  }
};

/**
 *
 * This method is used primarily in edit credit card pages. The edit credit card pages usually have a masked
 * original credit card. If the credit card number is changed, then the credit card should be validated to make sure
 * the entered number follows certain guidelines such as mod check.<b>
 * If the masked credit card number is modified, then the validation route performs the normal check and based on the
 * routine it returns true/false.
 *
 * @param creditCardType -- pCardTypeWidget -- The credit card type widget should be passed in -- Required
 * @param creditCardNumber -- pCardNumberWidget -- The credit card number widget should be passed in -- Required
 * @param originalMaskedCreditCardNumber -- pOriginalMaskedCreditCardNumber -- The original masked credit card number should be passed in -- Required
 *
 */
atg.commerce.csr.order.billing.isValidCreditCardNumberInEditContext = function (pParams) {

  if (!pParams.originalMaskedCreditCardNumber) {
    return false;
  }

  if (!pParams.creditCardType) {
    return false;
  }

  if (!pParams.creditCardNumber) {
    return false;
  }

  var currentCCNumber = pParams.creditCardNumber.getValue();
  if (pParams.originalMaskedCreditCardNumber === currentCCNumber) {
    return true;
  } else {
    return this.isValidCreditCardNumber (pParams.creditCardType, pParams.creditCardNumber);
  }
};

/**
 *
 * This method gets current values from the credit card type and credit card number widgets and
 * using dojo.validate.isValidCreditCard() to validate the credit card.
 *
 */
atg.commerce.csr.order.billing.isValidCreditCardNumber = function (pCardTypeWidget, pCardNumberWidget) {
  if (!pCardTypeWidget) {
    return false;
  }

  if (!pCardNumberWidget) {
    return false;
  }
  var ccType = pCardTypeWidget.getValue();
  var ccNumber = pCardNumberWidget.getValue();

  if (ccType === '') {
    dojo.debug("Supplied credit card type is not valid.");
    pCardTypeWidget.promptMessage="Please select a valid credit card type.";
    return false;
  }
  if (ccNumber === '') {
    dojo.debug("Supplied card number is not valid.");
    pCardNumberWidget.invalidMessage=getResource('csc.billing.invalidCreditCardNumber');
    return false;
  }
  var cardTypeData = creditCardTypeDataContainer.getCreditCardTypeDataByKey(ccType);
  if (!cardTypeData) {
    dojo.debug("Supplied credit card type is not valid.");
    pCardTypeWidget.invalidMessage=getResource('csc.billing.invalidCreditCardType');
    return false;
  }

  var code = cardTypeData.code;
  if (!cardTypeData) {
    dojo.debug("Supplied credit card type is not valid.");
    pCardTypeWidget.invalidMessage=getResource('csc.billing.invalidCreditCardType');
    return false;
  }

  if (code=='PL' || dojox.validate.isValidCreditCard(ccNumber, code)) {
    dojo.debug("This is a valid credit card number.");
    return true;
  } else {
    dojo.debug("Please provide a valid credit card number.");
    pCardNumberWidget.invalidMessage=getResource('csc.billing.invalidCreditCardNumber');
    return false;
  }
};

/**
 *
 * The AvailablePaymentMethod instance is created for each payment groups  or refund methods.
 * @param paymentGroupId -- required
 * @param amount  -- required
 * @param maxAllowedAmount -- optional
 *
 */
atg.commerce.csr.order.billing.AvailablePaymentMethod = function (pParams) {
  this.paymentGroupId=pParams.paymentGroupId;
  this.paymentGroupType=pParams.paymentGroupType;
  this.amount=pParams.amount;
  this.initialAmount=pParams.initialAmount;
  this.maxAllowedAmount=pParams.maxAllowedAmount;
};

atg.commerce.csr.order.billing.CSRPaymentContainer = function (pBalance,pAmountDue,pCurrencyCode, pParams) {
  this.balance=pBalance;
  this.availablePaymentMethods=[];
  this.amountDue=pAmountDue;
  this.currencyCode=pCurrencyCode;
  if (pParams != null) {
    this.locale=pParams.locale;
    this.currencySymbol=pParams.currencySymbol;
    this.places=pParams.places;
  }

  this.getAvailablePaymentMethodByKey = function(pKey) {
    var i=0;
    if (!this.availablePaymentMethods) {
      return null;
    }
    if (this.availablePaymentMethods.length === undefined ) {
      if (this.availablePaymentMethods.paymentGroupId == pKey) {
        return this.availablePaymentMethods;
      } else {
        return null;
      }
    } else {
      for (i = 0; i < this.availablePaymentMethods.length; i++) {
        if (this.availablePaymentMethods[i].paymentGroupId == pKey) {
          return this.availablePaymentMethods[i];
        }
      }
      return null;
    }
  };//end of getAvailablePaymentMethodByKey

  this.addAvailablePaymentMethod = function (pAvailablePaymentMethod) {
    //This will add an element to the array
    var length = this.availablePaymentMethods.length;
    this.availablePaymentMethods[length] = pAvailablePaymentMethod;

    var currentPaymentMethodAmount = pAvailablePaymentMethod.amount;
    var balanceAmount;
    if (currentPaymentMethodAmount > 0) {
      balanceAmount = this.balance;
      this.balance = atg.commerce.csr.order.billing.roundAmount ((balanceAmount * 1)  - (currentPaymentMethodAmount * 1));
    }
  };//end of addAvailablePaymentMethod

  this.deleteAvailablePaymentMethodByKey = function (pKey) {
    //TODO: We do not need this function for now. Add this later if we require.
    //This will remove an element from the array
  };//end of deleteAvailablePaymentMethodByKey

  this.initialize = function (pOrderTotal, pCurrencyCode, pParams) {
    //This will remove an element from the array
    this.availablePaymentMethods = [];
    this.amountDue=pOrderTotal;
    this.balance=pOrderTotal;
    this.currencyCode=pCurrencyCode;
    this.locale=pParams.locale;
    this.currencySymbol=pParams.currencySymbol;
    this.places=pParams.places;
  };// end of initialize

  this.firePaymentBalanceDojoEvent = function () {
    dojo.publish( "/atg/commerce/csr/order/PaymentBalance",
        [{
          event: "PaymentBalance",
          balance: this.balance
        }]);
  };//end of deleteAvailablePaymentMethodByKey
};

/**
 *
 * This class contains all available credit card type data.
 * The card type data has type and code properties.
 *
 */
atg.commerce.csr.order.billing.CSRCreditCardTypeDataContainer = function () {
  this.creditCardTypeData=[];
  this.initialize = function () {
    this.creditCardTypeData=[];
  };// end of initialize

  this.getCreditCardTypeDataByKey = function(pKey) {
    for (var i = 0; i < this.creditCardTypeData.length; i++) {
      if (this.creditCardTypeData[i].cardType == pKey) {
        return this.creditCardTypeData[i];
      }
    }//end of for
  };//end of getAvailablePaymentMethodByKey

  this.addCreditCardTypeData = function (pCreditCardTypeData) {
    //This will add an element to the array
    var length = this.creditCardTypeData.length;
    this.creditCardTypeData[length] = pCreditCardTypeData;
  };//end of addCreditCardTypeData
};

atg.commerce.csr.order.billing.CreditCardTypeData = function (pCardType, pCode) {
  this.cardType=pCardType;
  this.code=pCode;
};

/**
 *    This object is used to hold the result data for the currency validation operation.
 *  Sometimes, the user may not enter the data with appropriate comma separator between the number.
 *  After the validation operation, this object will contain the result.
 */
atg.commerce.csr.order.billing.CurrencyValidationResultHolder = function (pValid, pFormatted, pAmount) {
  this.valid = pValid;
  this.formatted=pFormatted;
  this.amount=pAmount;
};

/**
 * This method is called during the page initial rendering. If there is a balance and if there are
 * some payment types, the payment is assigned against to the payment types.
 *
 */
atg.commerce.csr.order.billing.assignBalance = function () {

  var allPayments = container.availablePaymentMethods;
  if (!allPayments) {
    dojo.debug(" There is no payment method. Thus leave the method.");
    return;
  }

  if ((container.balance * 1) === 0) {
    dojo.debug(" The balance is zero. No need to assign the balance to the payment types. Thus leave the method.");
    return;
  }
  var i = 0;
  var paymentId;
  var amount;
  var possibleMaximumAmount;
  var maxAllowedAmount;
  var balanceAmount = container.balance;
  var paymentWidget;

  for(i = 0; i < allPayments.length; i++){
    paymentId = allPayments[i].paymentGroupId;
    amount = allPayments[i].amount;
    maxAllowedAmount = allPayments[i].maxAllowedAmount;
    paymentWidget = dijit.byId(paymentId);
    dojo.debug("Widget Id" + paymentId + " amount :" + amount + " maxAllowedAmount :" + maxAllowedAmount);

    dojo.debug("Looping through a paymentGroup with payment group Id ::" + paymentId);
    dojo.debug("Locale value is ::" + container.locale);

    if ((container.balance * 1) === 0) {
      dojo.debug(" The balance is zero. No need to assign the balance to the payment types. Thus leave the method.");
      return;
    }

    if ((container.balance *1) > 0) {
      //if there max amount specified in the payment method, the first condition will be satisfied,
      //otherwise the second condition will be satisfied.
      if (paymentWidget && maxAllowedAmount && maxAllowedAmount !== 'Infinity') {
        possibleMaximumAmount = this.roundAmount ((maxAllowedAmount * 1) - (amount*1));
        if ((possibleMaximumAmount*1) >= (container.balance*1)) {
          allPayments[i].amount = container.balance*1;
          container.balance = 0.0;
        } else {
          allPayments[i].amount = possibleMaximumAmount*1;
          container.balance = this.roundAmount((container.balance*1) - (possibleMaximumAmount * 1));
        }
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      } else if (paymentWidget && ((typeof maxAllowedAmount === 'undefined') || (maxAllowedAmount === 'Infinity'))) {
        allPayments[i].amount = this.roundAmount((allPayments[i].amount *1) + container.balance);
        container.balance = 0.0;
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      }
    } else {
      //amount is always positive. In order to compare against negative number, multiplying by -1.
      if ((container.balance *-1) >= (amount*1)) {
        allPayments[i].amount =  0.0 ;
        container.balance = this.roundAmount((container.balance*1) + allPayments[i].amount);
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      } else {
        allPayments[i].amount = this.roundAmount((allPayments[i].amount * 1) + (container.balance*1));
        container.balance = 0.0;
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      }
    }
  }
};


/**
 *
 * This method does not alter the cvv value, all it does is to verify the entered
 * values are integer values.
 *
 */
atg.commerce.csr.order.billing.isValidCVV = function (pWidget) {

  if (!pWidget) {
    return false;
  }
  var format = "????";
  var flags = {format:format};

  var cvvValue = pWidget.getValue();
  return dojox.validate.isNumberFormat (cvvValue, flags);
};

/**
 *
 * This function is used to validate the CSC billing form or available payment options page.
 *
 */
atg.commerce.csr.order.billing.csrBillingFormValidate = function () {

  var formvalid = true;

  //This checks to make sure all the cvv elements are valid.
  var allPayments = container.availablePaymentMethods;
  if (!allPayments) {
    dojo.debug(" There is no payment method. Thus leave the method.");
    return true;
  }

  var paymentGroupId = null;
  var cvvElementName = null;
  var cvvWidget = null;
  var paymentWidget = null;
  //need to loop through all CVV to mark the invalid characters.

  for(i = 0; i < allPayments.length; i++){
    paymentGroupId = allPayments[i].paymentGroupId;
    paymentGroupType = allPayments[i].paymentGroupType;
    dojo.debug("Looping through a paymentGroup with payment group Id ::" + paymentGroupId);
    cvvElementName = paymentGroupId+"CVV";
    dojo.debug("CVV element name is ::" + cvvElementName);
    cvvWidget = dijit.byId(cvvElementName);
    paymentWidget = dijit.byId(paymentGroupId);

    if (paymentGroupId && cvvWidget ) {
      if (!atg.commerce.csr.order.billing.isValidCVV(cvvWidget)) {
        var errormessage = getResource('csc.billing.invalidCVVNumber');
        cvvWidget.invalidMessage=errormessage;
        atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
        formvalid = false;
        break;
      }
    }
    if (paymentGroupId && paymentWidget ) {
      if (!paymentWidget.isValid() && paymentGroupType != "inStorePayment") {
        formvalid = false;
        break;
      }
    }
    if (allPayments.length == 1 && paymentGroupId && paymentGroupType == "inStorePayment" && !dojo.byId(paymentGroupId + "_checkbox").checked) {
      formvalid = false;
      break;
    }
  }

  if (formvalid) {
    if (atg.commerce.csr.order.billing.isZeroBalance()) {
      //the form is valid and buttons needs to be enabled.
      atg.commerce.csr.order.billing.disableCheckoutButtons(false);
    }
  } else {
    //the form is not valid and buttons needs to be disabled.
    atg.commerce.csr.order.billing.disableCheckoutButtons(true);
  }
  
  return formvalid;

};

/**
 * This method rounds a number to the places value defined in the container and if not present defaults to two decimal places.
 */
atg.commerce.csr.order.billing.roundAmount = function (pAmount) {
  dojo.debug("The original amount is ::" + pAmount);
  var result = dojo.number.round(pAmount, ('places' in container) ? container.places : 2);
  dojo.debug("The rounded original amount is ::" + result);
  return result;
};
dojo.provide( "atg.commerce.csr.order.confirm" );


/**
 * Send an order confirmation message.
 */
atg.commerce.csr.order.confirm.sendConfirmationMessage = function (){

   atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_sendConfirmationMessageForm")
    });    
};

/**
 * Saves a new customer profile.
 */
atg.commerce.csr.order.confirm.saveCustomerProfile = function (createUserOkFormat, createUserFailureFormat){
  var theForm = dojo.byId("atg_commerce_csr_customerCreateForm");
  var firstName = theForm["atg_commerce_csr_confirm_fName"].value;
  var lastName = theForm["atg_commerce_csr_confirm_lastName"].value;

  theForm["atg.successMessage"].value = dojo.string.substitute(createUserOkFormat, [firstName, lastName]);
  theForm["atg.failureMessage"].value = dojo.string.substitute(createUserFailureFormat, [firstName, lastName]);
  
  atgSubmitAction({
    form:theForm,
    panelStack: ["globalPanels"]
  });       
}

/**
 * Navigate to the Product Catalog panel.
 */
atg.commerce.csr.order.confirm.renderProductCatalogPanel = function (){
   atgNavigate({panelStack:'cmcCatalogPS'});
};
dojo.provide( "atg.commerce.csr.order.finish" );

/**
 * Submit the order
 *
 * @param theForm the form that gets submitted. Different forms
 *              are submitted, depending on the state of the order.
 */
atg.commerce.csr.order.finish.submitOrder = function (theForm){
  atgSubmitAction({
    form:theForm,
    panelStack: ["globalPanels"]
    });
};

atg.commerce.csr.order.finish.submitAndScehduleOrder = function (theForm){
  atgSubmitAction({
    form:theForm,
    queryParams: { "contentHeader" : true, "cancelScheduleProcess" : "reviewSubmitAndSchedule" },
    panelStack: ["globalPanels"]
    });
};
atg.commerce.csr.order.finish.scheduleOrder = function (theForm){
  atgSubmitAction({
    form:theForm,
    queryParams: { "contentHeader" : true, "cancelPS" : "cmcCompleteOrderPS", "cancelScheduleProcess" : "reviewAndSchedule" },
    panelStack: ["globalPanels"]
    });
};
/**
 * Submit an exchange replacement order.
 *
 */
atg.commerce.csr.order.finish.submitExchange = function (){
  atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_submitExchangeForm"),
    panelStack: ["globalPanels"]
    });
};

/**
 * Cancel the current order.
 */
atg.commerce.csr.order.finish.cancelOrder = function (){

   atg.commerce.csr.common.enableDisable([{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelOrderHandler'}],
                                         [{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelExchangeOrderHandler'}]);

   atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_finishOrderCancelForm"),
    panelStack: ["globalPanels"],
    sync: true
   });
   this.hideCancelOrderPrompt();
};


/**
 * Cancel the exhange order.
 */
atg.commerce.csr.order.finish.cancelExchangeOrder = function (pParams){

   var cancelOrderForm = document.getElementById("atg_commerce_csr_finishOrderCancelForm");
   atg.commerce.csr.common.enableDisable([{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelExchangeOrderHandler'}],
                                         [{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelOrderHandler'}]);
   var deferred = atgSubmitAction({
                  form:cancelOrderForm,
                  panelStack: ["globalPanels"]
                  });
    var checked = atg.commerce.csr.common.getCheckedItem(cancelOrderForm.desiredOption);
    if ( checked !== "" ) {
       checked = checked.value;
    }
    if (checked === 'cancelExchangeOnly') {
      deferred.addCallback(function() {atg.progress.update('cmcRefundTypePS');}); 
    } else {
      deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');}); 
    }
    this.hideCancelOrderPrompt();
};


atg.commerce.csr.order.finish.navActionCancelOrder = function (pPopupURL){
atg.commerce.csr.common.showPopupWithReturn({
                popupPaneId: 'cancelOrderPopup',
                 url: pPopupURL,
                onClose: function( args ) {  } })
};


/**
 * Cancel the exhange order.
 */
atg.commerce.csr.order.finish.resizeCancelOrderWindow = function (){
    var cancelOrderWindow = dijit.byId("cancelOrderPopup");
    if (cancelOrderWindow) {
      cancelOrderWindow.resizeTo('550','250');
    }
};

/**
 * Persist the current order.
 */
atg.commerce.csr.order.finish.saveOrder = function (){
  atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_finishOrderSaveForm"),
    panelStack: ["globalPanels"]
    });
};

/**
 * Adds a new order note. Make this action synchronised so
 * that the order update process is complete before the UI
 * is refreshed. Othwerwise, there is no indication that the
 * new note was actually added i.e. the UI may refresh before
 * the async order update completes.
 */ 
atg.commerce.csr.order.finish.createNewOrderNote = function () {
  var addNewOrderNoteForm = document.getElementById('atg_commerce_csr_order_note_addNewOrderNoteForm');
  if (addNewOrderNoteForm) {    
      atgSubmitAction(
      {
        form: addNewOrderNoteForm,
        sync: true
      });
      atg.commerce.csr.common.hidePopupWithReturn('addOrderNotePopup', {result:'ok'});
    }
};

atg.commerce.csr.order.finish.editExistingOrder = function( formId, orderId )
{
  var theForm = document.getElementById(formId);
  theForm.orderId.value = orderId;
  atgSubmitAction({
    form : theForm,
    queryParams : {init:true}
  });
};

atg.commerce.csr.order.finish.hideCancelOrderPrompt = function ()
{
    var cancelOrderWindow = dijit.byId("cancelOrderPopup");

    if (cancelOrderWindow) {
      atg.commerce.csr.common.hidePopup (cancelOrderWindow);
    }
};
dojo.provide("atg.commerce.csr.order.gift");

/**
 * atg.commerce.csr.order.gift.giftlistSelect
 * 
 * Select a giftlist on the customer management page and display it to the agent
 * 
 * @param {String}
 *          pGiftlistId the gift list Id.
 */
atg.commerce.csr.order.gift.giftlistSelect = function (giftlistId) {
  var theForm = dojo
      .byId("atg_commerce_csr_customer_gift_showSelectedGiftlist");
  theForm.giftlistId.value = giftlistId;

  atgSubmitAction( {
    form : theForm,
    panels : [ "cmcGiftlistsViewP" ],
    queryParams : { "giftlistId" : giftlistId }
  });
};

/**
 * atg.commerce.csr.order.gift.giftlistBuyFrom
 * 
 * This function will land an agent on the Commerce Tab and the view gift list tab, displaying
 * the gift list associated with the giftlistId parameter passed in.
 * 
 * @param {String}
 *          pGiftlistId the gift list Id.
 */
atg.commerce.csr.order.gift.giftlistBuyFrom = function (giftlistId) {
  var theForm = dojo.byId("atg_commerce_csr_buyFromGiftlist");
  theForm.purchaseGiftlistId.value = giftlistId;

  atgSubmitAction( {
    form : theForm,
    panels : [ "cmcGiftlistViewPurchaseModeP" ],
    panelStack : [ "cmcGiftlistSearchPS", "globalPanels" ],
    selectTabbedPanels : ["cmcGiftlistViewPurchaseModeP"],
    queryParams : { "giftlistId" : giftlistId },
    tab : atg.service.framework.changeTab('commerceTab')
  });
};

/**
 * atg.commerce.csr.order.gift.giftlistDelete
 * 
 * Deletes a customers gift list
 * 
 * @param {String}
 *          pGiftlistId the gift list Id.
 * @param {String}
 *          pPopupTitle the resource for the popup title.
 */
atg.commerce.csr.order.gift.giftlistDelete = function (pGiftlistId, pPopupTitle) {
  var theForm = dojo.byId("deleteGiftlist");
  theForm.giftlistId.value = pGiftlistId;

  atg.commerce.csr.common.showPopupWithReturn( {
    popupPaneId : 'deleteGiftListPopup',
    title : pPopupTitle,
    url : '/DCS-CSR/include/gift/giftlistDeleteConfirm.jsp?_windowid='
        + window.windowId + '&giftlistId=' + pGiftlistId,
    onClose : function(args) {
      if (args.result == 'delete') {
        atgSubmitAction( {
          panels : [ 'cmcGiftlistsViewP' ],
          panelStack : [ "globalPanels" ]
        });
      }
    }
  });
};

/**
 * atg.commerce.csr.order.gift.removeItemFromGiftlist
 * 
 * Remove an item from a customers gift list
 * 
 * @param {String}
 *          pFormId the form Id.
 * @param {String}
 *          pGiftItemId the giftItemId.
 * @param {String}
 *          pGiftlistId the gift list Id.
 */
atg.commerce.csr.order.gift.removeItemFromGiftlist = function (pFormId, pGiftItemId, pGiftlistId) {
  var theForm = dojo.byId(pFormId);
  if (theForm) {
    theForm.giftItemId.value = pGiftItemId;
    theForm.giftlistId.value = pGiftlistId;

    atgSubmitAction( {
      form : theForm,
      panels : [ 'cmcGiftlistsViewP' ],
      panelStack : [ "globalPanels" ],
      queryParams : { "giftlistId" : pGiftlistId }
    });
  }
};

/**
 * atg.commerce.csr.order.gift.addWishlistItemToOrder
 * 
 * Add a wish list item to the current order
 * 
 * @param {String}
 *          pFormId the form Id.
 * @param {String}
 *          pGiftlistId the gift list Id.
 * @param {String}
 *          pConfirmMessage the confirmation message displayed to the agent.
 */
atg.commerce.csr.order.gift.addWishlistItemToOrder = function (pFormId, pCatalogRefId, pProductId,
    pGiftlistId, pGiftlistItemId, pConfirmMessage, pSiteId) {
	
  var theForm = dojo.byId(pFormId);
  if (theForm) {
    theForm.catalogRefId.value = pCatalogRefId;
    theForm.productId.value = pProductId;
    if (pSiteId) {
      theForm.siteId.value = pSiteId;
    }
    theForm["atg.successMessage"].value = pConfirmMessage;
    var deferred = atgSubmitAction( {
      form : theForm,
      queryParams : {
        "giftlistId" : pGiftlistId
      }
    });
    deferred.addCallback(function() {atg.progress.update('cmcCatalogPS');}); // poke the order summary panel to update
    
  }
};

/**
 * atg.commerce.csr.order.gift.addGiftlistItemsToOrder
 * 
 * Add gift items to the current order
 * 
 * @param {String}
 *          pFormId the form Id.
 * @param {String}
 *          pGiftlistId the gift list Id.
 * @param {String}
 *          pConfirmMessage the confirmation message displayed to the agent.
 */
atg.commerce.csr.order.gift.addGiftlistItemsToOrder = function (pFormId, pGiftlistId, pConfirmMessage) {
  var theForm = dojo.byId(pFormId);
  if (theForm) {
    theForm["atg.successMessage"].value = pConfirmMessage;
    atgSubmitAction( {
        form : theForm,  
        panels: ["cmcGiftlistViewPurchaseModeP"],  
        panelStack: ["cmcGiftlistSearchPS"], 
        selectTabbedPanels: ["cmcGiftlistViewPurchaseModeP"] 
    });
  }
};

/**
 * atg.commerce.csr.order.gift.addItemsToGiftlist
 * 
 * Add Items for the gift list
 * 
 * @param {String}
 *          pFormId the form Id.
 * @param {String}
 *          pProductId the product Id.
 * @param {String}
 *          pGiftlistId the gift list Id.
 */
atg.commerce.csr.order.gift.addItemsToGiftlist = function (pFormId, pProductId, pGiftlistId) {
  var theForm = dojo.byId(pFormId);
  var skuList = dojo.query(".quantity-input");
  var skuArray = [];
  var giftlistSelect = document.getElementById("giftlistSelect");
  if (!pGiftlistId) {
    var giftlistId = giftlistSelect.options[giftlistSelect.selectedIndex].value;
  } else {
    var giftlistId = pGiftlistId;
  }

  dojo.byId(pFormId+"_productId").value = pProductId;
  dojo.byId(pFormId+"_selectedGiftlistId").value = giftlistId;

  // iterate over every sku
  for ( var x = 0; x < skuList.length; x++) {
	  if ((typeof(skuList[x].value) != 'undefined') && (skuList[x].value != "")) {
      var value = dojo.number.parse(skuList[x].value);
      dojo.debug("Parsed giftlist value = " + value);
      if (value > 0) {
        input = document.createElement("input");
        input.setAttribute("type", "hidden");
        input.setAttribute("name", skuList[x].id);
        input.setAttribute("value", value);
        // Remove any previously entered values before adding
        // new gift list values.
        for (var i = 0; i < theForm.childNodes.length; i++) {
          var child = theForm.childNodes[i];
          if (child.name && child.name == input.name) {
            theForm.removeChild(child);
          }
        }
        theForm.appendChild(input);
        // add skuId to the skuArray
        skuArray.push(skuList[x].id);
      }
	  }
  }

  atgSubmitAction( {
    form : theForm,
    panelStack : [ "cmcCatalogPS", "globalPanels" ],
    selectTabbedPanels: ["cmcProductViewP"],
    listParams : {
	  catalogRefIds : skuArray
    },
    formHandler : "/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler"
  });
};

/**
 * atg.commerce.csr.order.gift.addItemsToWishlist
 * 
 * Add items to the wish list
 *
 * @param {String}
 *          pFormId the form Id.
 * @param {String}
 *          pProductId the product Id.
 * @param {String}
 *          pGiftlistId the gift list Id.
 */
atg.commerce.csr.order.gift.addItemsToWishlist = function (pFormId, pProductId, pGiftlistId) {
  var theForm = dojo.byId(pFormId);
  var skuList = dojo.query(".quantity-input");
  var skuArray = [];
  dojo.byId(pFormId+"_productId").value = pProductId;
  dojo.byId(pFormId+"_selectedGiftlistId").value = pGiftlistId;
  // iterate over every sku
  for ( var x = 0; x < skuList.length; x++) {
	  if((typeof(skuList[x].value) != 'undefined') && (skuList[x].value != "")) {
      var value = dojo.number.parse(skuList[x].value);
      dojo.debug("Parsed giftlist value = " + value);
      if (value > 0) {
        input = document.createElement("input");
        input.setAttribute("type", "hidden");
        input.setAttribute("name", skuList[x].id);
        input.setAttribute("value", value);
        // Remove any previously entered values before adding
        // new wish list values.
        for (var i = 0; i < theForm.childNodes.length; i++) {
          var child = theForm.childNodes[i];
          if (child.name && child.name == input.name) {
            theForm.removeChild(child);
          }
        }
        theForm.appendChild(input);
        // add skuId to the skuArray
        skuArray.push(skuList[x].id);
      }
	  }
  }
  atgSubmitAction({
    form : theForm,
    panelStack : [ "cmcCatalogPS", "globalPanels" ],
    selectTabbedPanels: ["cmcProductViewP"], 
    listParams : {
	  catalogRefIds : skuArray
    },
    formHandler : "/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler"
  });
};    
    
/**
 * atg.commerce.csr.order.gift.updateGiftlistItems
 * 
 * Update the gift items in a gift list
 * 
 * @param {String}
 *          pGiftlistId the gift list Id.
 */
atg.commerce.csr.order.gift.updateGiftlistItems = function (pGiftlistId) {
  var theForm = dojo.byId("atg_commerce_csr_updateGiftlist");
  var skuList = dojo.query(".quantity-input");
  var skuArray = [];
  dojo.byId('giftlistId').value = pGiftlistId;

  // iterate over every sku
  for ( var x = 0; x < skuList.length; x++) {
    if ((typeof(skuList[x].value) != 'undefined') && (skuList[x].value != "")) {
      var value = dojo.number.parse(skuList[x].value);
      dojo.debug("Parsed giftlist value = " + value);
      if (value > 0) {
        input = document.createElement("input");
        input.setAttribute("type", "hidden");
        input.setAttribute("name", skuList[x].id);
        input.setAttribute("value", value);
        // Remove any previously entered values before adding
        // new gift list values.
        for (var i = 0; i < theForm.childNodes.length; i++) {
          var child = theForm.childNodes[i];
          if (child.name && child.name == input.name) {
            theForm.removeChild(child);
          }
        }
        theForm.appendChild(input);
        //add skuId to the skuArray
        skuArray.push(skuList[x].id);
      }
	  }
  }
  atgSubmitAction( {
    form : theForm,
    listParams : {
      catalogRefIds : skuArray
    },
    formHandler : "/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler",
    panels : [ 'cmcGiftlistsViewP' ],
    panelStack : [ "globalPanels" ],
    queryParams : {
      "giftlistId" : pGiftlistId
    }
  });
};


/**
 * atg.commerce.csr.order.gift.disableExistingAddress
 *
 * disables the existing address drop down (If no existing addesses the text is greyed out)
 * @param {String} pHasExistingAddresses if the customer has existing addresses
 */
atg.commerce.csr.order.gift.disableExistingAddress = function(pFormId,pHasExistingAddresses) {
  if(pHasExistingAddresses == 'false') { 
    dojo.byId(pFormId+"_existingAddressList").disabled = true;
    dojo.byId(pFormId+"_addressRadioSelection_existing").disabled = true;
    dojo.byId(pFormId+"_addressRadioSelection_existing_text").style.color = '#aaaaaa';
  }
  if(pHasExistingAddresses && !dojo.byId(pFormId+"_addressRadioSelection_existing").checked) { 
    dojo.byId(pFormId+"_existingAddressList").disabled = true;
    dojo.byId(pFormId+"_addressRadioSelection_existing_text").style.color = '#333333';
  }
  if(pHasExistingAddresses && dojo.byId(pFormId+"_addressRadioSelection_existing").checked) { 
    dojo.byId(pFormId+"_existingAddressList").disabled = false;
    dojo.byId(pFormId+"_addressRadioSelection_existing_text").style.color = '#333333';
  }
};

/**
 * atg.commerce.csr.order.gift.validateEventType
 *
 * Validates the eventType dropdown
 * @param {String} pEventTypeValue event type value
 * @returns true if the pEventTypeValue is valid, false if it is not valid.
 */
atg.commerce.csr.order.gift.validateEventType = function (pEventTypeValue) {
  if(pEventTypeValue < 0) { return false; }
  return true;
};

/**
 * atg.commerce.csr.order.gift.validateGiftlistEventDate
 * 
 * Validates the eventDate
 * 
 * @param {String}
 *          pEventDateValue event date value
 * @returns true if the pEventDateValue is valid, false if it is not valid.
 */
atg.commerce.csr.order.gift.validateGiftlistEventDate = function(eventDateString,dateFormat) {
  var eventDate = dojo.date.locale.parse(eventDateString, {datePattern: dateFormat, selector: "date"});
  if(eventDateString=="" || !eventDate){
   return false;
  }
  return true;
};

/**
 * atg.commerce.csr.order.gift.isSearchFormEmpty
 * 
 * Checks if the gift list search form is empty
 * 
 * @param {String}
 *          pFormId the form Id.
 * @param {String}
 *          pDateDefault default date string
 * @param {String}
 *          pTrimInputs remove white space from inputs
 * @returns true if the gift list search form is empty otherwise false
 */
atg.commerce.csr.order.gift.isSearchFormEmpty = function (pFormId, pDateDefault, pTrimInputs ) {
  var elements = dojo.query("input", pFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type == "text" || type == "textarea" || type == "password") {
  var itemValue = pTrimInputs ? dojo.string.trim(item.value) : item.value;
  if (itemValue != '' && itemValue != pDateDefault) {return false;}
}
else if (type == "checkbox" || type == "radio") {
      if (item.checked == true) {return false;}
    }
  };
  var elements = dojo.query("select", pFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type.match("select") == "select") {
  if (item.value != '') {return false;}
    }
  };
  return true;
};

//Changes the current site by setting the environment based on the siteId parameter
atg.commerce.csr.order.gift.changeSiteContext = function(siteId, pFormId) {
  dojo.debug("MultiSite | atg.commerce.csr.common.changeSite called with siteId = " + siteId);
  if(!pFormId) {
  var theForm = dojo.byId("atg_commerce_csr_productDetailsForm");
  }
  else {
    var theForm =dojo.byId(pFormId);
  }
  
    atgSubmitAction({
      selectTabbedPanels: ["cmcGiftlistsViewP"],
      panelStack : ["cmcCatalogPS", "globalPanels"],
      form: theForm,
      sync: true,
      queryParams: { contentHeader : true, siteId: siteId }
    });
};dojo.provide( "atg.commerce.csr.gwp" );

atg.commerce.csr.gwp.submitGWPGiftSelectionForm = function(params) {
  dojo.debug("GWP | Submitting Gift Selection form");
  var theForm = atg.commerce.csr.gwp.getGWPGiftSelectionForm();
  
  atgSubmitAction({
    form: theForm
  });
  
};

atg.commerce.csr.gwp.getGWPGiftSelectionForm = function() {
  dojo.debug("GWP | Getting Gift Selection form");
  var theForm = dojo.byId("gwpMakeGiftSelection");
  
  return theForm;
};

atg.commerce.csr.gwp.setGWPGiftSelectionFormInputValues = function(formInput) {
  dojo.debug("GWP | Setting Form Input Values");
  var theForm = atg.commerce.csr.gwp.getGWPGiftSelectionForm();
  atgBindFormValues(theForm,formInput)  ;
  return theForm;
};
dojo.provide( "atg.commerce.csr.order" );

atg.commerce.csr.order.loadExistingOrder = function(orderId, orderstate )
{
  dojo.debug('atg.commerce.csr.order.loadExistingOrder: orderstate is ' + orderstate);
  dojo.debug('atg.commerce.csr.order.loadExistingOrder: orderId is ' + orderId);
  var theForm;
  if(orderstate == 'TEMPLATE' && atg.commerce.csr.order.scheduled.isScheduledOrders == 'true')
    theForm = dojo.byId("atg_commerce_csr_loadExistingScheduledOrderForm");
  else
    theForm = dojo.byId("atg_commerce_csr_loadExistingOrderForm");

  theForm.orderId.value = orderId;
  atgSubmitAction({
    form : theForm,
    queryParams: { "contentHeader" : true }
  });
};

atg.commerce.csr.order.orderHistoryLoadOrder = function(orderId)
{
  var theForm = dojo.byId("atg_commerce_csr_loadExistingOrderForm");
  theForm.orderId.value = orderId;
  atgSubmitAction({
    form : theForm,
    panelStack: ["globalPanels", "cmcExistingOrderPS"],
    queryParams: { "contentHeader" : true },
    tab: atg.service.framework.changeTab('commerceTab')
  });
};


atg.commerce.csr.order.viewExistingOrder = function(orderId,orderstate)
{
  //dojo.debug('atg.commerce.csr.order.viewExistingOrder: orderstate is ' + orderstate);
  //dojo.debug('atg.commerce.csr.order.viewExistingOrder: atg.commerce.csr.order.scheduled.isScheduledOrders is ' + atg.commerce.csr.order.scheduled.isScheduledOrders);

  if(orderstate == 'TEMPLATE' && atg.commerce.csr.order.scheduled.isScheduledOrders == 'true')
  {
    var theForm = dojo.byId("atg_commerce_csr_viewScheduledOrderForm");
    theForm.viewOrderId.value = orderId;
    atgSubmitAction({
      form : theForm
    });

  }
  else
  {
    var theForm = dojo.byId("atg_commerce_csr_viewExistingOrderForm");
    theForm.viewOrderId.value = orderId;
    atgSubmitAction({
      form : theForm
    });
  }
};

atg.commerce.csr.order.findByIdOrder = function(orderId)
{
  if(isEmpty(orderId))
    return;
  var theForm = document.getElementById("atg_commerce_csr_globalFindOrderByIdForm");
  if (theForm) {
    theForm.viewOrderId.value = orderId;
    atgSubmitAction({
      form: theForm
    });
  }
}



// Shows the product details for a cross sell that appears in the product details page
atg.commerce.csr.order.selectCrossSellLink = function(_productId, _panelStack) {
  dojo.debug("MultiSite | Selecting cross-sell link");
  var theForm = dojo.byId("atg_commerce_csr_productDetailsForm");

  atgSubmitAction({
    form: theForm,
    queryParams: { contentHeader : true, productId : _productId }
  });
}

// Shows edit line item popup and updates Cross Sells pane with the selected sku
atg.commerce.csr.order.selectCrossSellSku = function(productId, skuId, rowNumber, displayName, newSiteId, currentSiteId)
{
  var editLineItemUrl = document.getElementById("productEditLineItem").value;
  var editLineItemHandler = function(args) {

	 //See CSC-169126 for more details:
	 //The site specific values are not supported yet. When the site specific values are supported,
	 //until then the site context is not set for the popup page.
	  
    // if sku is selected and Ok is pressed
    if ( args.result == 'ok' && args.sku != '') {
      var selectCrossSellSkuLinkContainer = document.getElementById("selectCrossSellSkuLinkContainer" + rowNumber);
      if (selectCrossSellSkuLinkContainer) {
        //update row with selected sku id
        selectCrossSellSkuLinkContainer.innerHTML = '';
        var selectCrossSellSkuLinkAnchor = document.createElement("a");
        selectCrossSellSkuLinkAnchor.href = "#";
        selectCrossSellSkuLinkAnchor.innerHTML = args.sku;
        selectCrossSellSkuLinkAnchor.onclick = function(args) {
          atg.commerce.csr.order.selectCrossSellSku(window.atg_commerce_csr_order_editLineItemHandler_productId,
                  window.atg_commerce_csr_order_editLineItemHandler_skuId,
                  window.atg_commerce_csr_order_editLineItemHandler_rowNumber,
                  window.atg_commerce_csr_order_editLineItemHandler_displayName);
        }
        selectCrossSellSkuLinkContainer.appendChild(selectCrossSellSkuLinkAnchor);
        var skuIdItem = document.getElementById("skuId" + rowNumber);
        if (skuIdItem) {
          skuIdItem.value = args.sku;
        }
        // enable qty field
        if (document.getElementById("qty" + rowNumber)) {
          var qtyField = document.getElementById("qty" + rowNumber);
          if (qtyField) {
            qtyField.disabled = false;
          }
        } else if (document.getElementById("fractqty" + rowNumber)) {
          var qtyField = document.getElementById("fractqty" + rowNumber);
          if (qtyField) {
            qtyField.disabled = false;
          }
        }
        // update info of the selected SKU
        var readSkuInfoURL = document.getElementById("readSkuInfoURL");
        if (readSkuInfoURL) {
          dojo.xhrGet({
            url: readSkuInfoURL.value + "&skuId" + "=" + args.sku + "&productId" + "=" + productId,
            encoding: "utf-8",
            load: function(data) {
              var skuInfo = atg.commerce.csr.catalog.createObjectFromJSON(data);
              if (skuInfo != null) {
                statusItem = document.getElementById("status" + rowNumber);
                if (statusItem) {
                  statusItem.innerHTML = skuInfo.status;
                }
                priceItem = document.getElementById("price" + rowNumber);
                if (priceItem) {
                  priceItem.innerHTML = skuInfo.price;
                }
              }
            },
            error: function(error) {
              atg.commerce.csr.catalog.showDojoIoBindError(error);
            },
            mimetype: "text/plain"
          });
        }
      }
    }
  }
  window.atg_commerce_csr_order_editLineItemHandler_skuId=skuId;
  window.atg_commerce_csr_order_editLineItemHandler_productId=productId;
  window.atg_commerce_csr_order_editLineItemHandler_rowNumber=rowNumber;
  window.atg_commerce_csr_order_editLineItemHandler_displayName=displayName;
  if (skuId == '') {
    var skuIdItem = document.getElementById("skuId" + window.atg_commerce_csr_order_editLineItemHandler_rowNumber);
    if (skuIdItem) {
      skuId = skuIdItem.value;
    }
  }

  atg.commerce.csr.common.showPopupWithReturn({
    popupPaneId: 'editLineItemPopup',
    title: displayName,
    url: '' + editLineItemUrl.replace("SKUIDPLACEHOLDER", skuId) + productId,
    onClose: editLineItemHandler
  });

};

// Adds selected Cross Sell items to shopping cart
atg.commerce.csr.order.addCrossSellsToCart = function () {

  var index = 0;
  
  // iterate through the items
  while (document.getElementById("qty" + index) || document.getElementById("fractqty" + index)) {
    if (document.getElementById("qty" + index)) {
      var qtyToAdd = document.getElementById("qty" + index);
      if (qtyToAdd && !qtyToAdd.value.match(/^[+-]?\d+(\.\d+)?$/)) {
        qtyToAdd.value = 0;
      }
      // in IE 11 there appears to be a delay when a control has been
      // disabled until the hidden qty field gets updated by dojo
      // NumberTextBox. Manually setting it here to work around that.
      var namedQtyToAdd = document.getElementsByName("qty" + index);
      if (namedQtyToAdd) {
        var arrayLength = namedQtyToAdd.length;
        for (var i = 0; i < arrayLength; i++) {
          if (namedQtyToAdd[i].value != qtyToAdd.value) {
            namedQtyToAdd[i].value = qtyToAdd.value;
          }
        }
      }
      namedQtyToAdd[1].value = qtyToAdd.value;
    } else {
      var qtyToAdd = document.getElementById("fractqty" + index);
      if (qtyToAdd && !qtyToAdd.value.match(/^[+-]?\d+(\.\d+)?$/)) {
        qtyToAdd.value = 0;
      }
      // in IE 11 there appears to be a delay when a control has been
      // disabled until the hidden qty field gets updated by dojo
      // NumberTextBox. Manually setting it here to work around that.
      var namedQtyToAdd = document.getElementsByName("fractqty" + index);
      if (namedQtyToAdd) {
        var arrayLength = namedQtyToAdd.length;
        for (var i = 0; i < arrayLength; i++) {
          if (namedQtyToAdd[i].value != qtyToAdd.value) {
            namedQtyToAdd[i].value = qtyToAdd.value;
          }
        }
      }
      namedQtyToAdd[1].value = qtyToAdd.value;
    }
    index = index + 1;
  }
  // submit form
  var addCrossSellsToCartForm = document.getElementById("addCrossSellsToCartForm");
  if (addCrossSellsToCartForm) {
    addCrossSellsToCartForm.addItemCount.value = index;
    atgSubmitAction({
      panelStack:"cmcShoppingCartPS",
      form:addCrossSellsToCartForm
    });
  }
};

atg.commerce.csr.order.copy = function(pOrderId)
{
  var theForm = dojo.byId("atg_commerce_csr_copyOrder");
  theForm.orderId.value = pOrderId;
  atgSubmitAction({
    form : theForm,
    queryParams: { "contentHeader" : true }
  });

};

atg.commerce.csr.order.skuBrowserAction = function(panels, productid) {
  atgSubmitAction({
    panelStack: [panels,"globalPanels"],
    queryParams: { "productId" : productid },
    form: atg.commerce.csr.common.getEnclosingForm('skuBrowserAction')
  });
};

atg.commerce.csr.order.crossSellItems = function(relatedId, skuId, confirmMsg, panels) {
  document.getElementById('addCrossSellProductId').value = relatedId;
  document.getElementById('addCrossSellSkuId').value = skuId;
  document.getElementById('addCrossSellForm')['atg.successMessage'].value = confirmMsg;
  atgSubmitAction({
    panelStack: [panels,"globalPanels"],
    form: document.getElementById('addCrossSellForm')
  });
};


atg.commerce.csr.order.returnToCustomerInformationPage = function() {
  console.debug("Calling atg.commerce.csr.order.returnToCustomerInformationPage");
  viewCurrentCustomer('customersTab');
};

atg.commerce.csr.order.returnToOrderSearchPage = function() {
  console.debug("Calling atg.commerce.csr.order.returnToOrderSearchPage");
  atg.commerce.csr.openPanelStack('cmcOrderSearchPS');
};

// This function launches instore pickup popup for selected item
atg.commerce.csr.catalog.pickupInStoreAction = function (title, popupUrl, productId) {
  for (itemKey in window.skuArray) {
    if (!window.skuArray[itemKey] && dojo.byId(itemKey).value) {
      var value = dojo.number.parse(dojo.byId(itemKey).value);
      dojo.debug("Parsed pickup in store value = " + value);
      if (value > 0) {
        skuId = itemKey;
        quantity = value;
        break;
      }
    }
  }

  atg.commerce.csr.common.showPopupWithReturn({
    popupPaneId: 'pickupLocationsPopup',
    title: title,
    url: popupUrl + '&productId=' + productId + '&skuId=' + skuId + '&quantity=' + quantity
  });
};

// This function is used to retrieve and show found stores for instore pickup popup
atg.commerce.csr.catalog.pickupInStoreSearchStores = function (productId, skuId, quantity, allItems) {
  if (!productId && !skuId && !quantity) {
    var form = dojo.byId('shippingInStorePickupSearchForm');
    var inStorePickupSearchContainer = dojo.byId('shippingInStorePickupSearchContainer');
    if (inStorePickupSearchContainer) {
      form.countryCode.value = dojo.byId('shippingInStorePickupCountry').value;
      form.postalCode.value = dojo.byId('shippingInStorePickupPostalCode').value;
      if (dojo.byId('shippingInStorePickupState')) {
        form.state.value = dojo.byId('shippingInStorePickupState').value;
      }
      var inStorePickupPostalCity = dojo.byId('shippingInStorePickupPostalCity');
      if (inStorePickupPostalCity && inStorePickupPostalCity.value != '' && inStorePickupPostalCity.value != 'City') {
        form.city.value = inStorePickupPostalCity.value;
      }
      
      if(form['geoLocatorServiceProviderEmpty']){
        if(form['geoLocatorServiceProviderEmpty'].value == '-1'){
          form.distance.value = -1;          
        }
        else{
          form.distance.value = form['geoLocatorServiceProviderEmpty'].value;
        } 
      }
      else{
        form.distance.value = dojo.byId('shippingInStorePickupProximity').value;
      }  
    }
  } else {
    var form = dojo.byId('inStorePickupSearchForm');
    var inStorePickupSearchContainer = dojo.byId('inStorePickupSearchContainer');
    if (inStorePickupSearchContainer) {
      form.countryCode.value = dojo.byId('inStorePickupCountry').value;
      form.postalCode.value = dojo.byId('inStorePickupPostalCode').value;
      if (dojo.byId('inStorePickupState')) {        
        form.state.value = dojo.byId('inStorePickupState').value;
      }
      var inStorePickupPostalCity = dojo.byId('inStorePickupPostalCity');
      if (inStorePickupPostalCity && inStorePickupPostalCity.value != '' && inStorePickupPostalCity.value != 'City') {
        form.city.value = inStorePickupPostalCity.value;
      }
      
      if(form['geoLocatorServiceProviderEmpty']){
        if(form['geoLocatorServiceProviderEmpty'].value == '-1'){
          form.distance.value = -1;
        }
        else{
          form.distance.value = form['geoLocatorServiceProviderEmpty'].value;
        } 
      }
      else{
        form.distance.value = dojo.byId('inStorePickupProximity').value;
      }   
    }
  }
  
  if (allItems) {
    if (form['allItemsSuccessURLHidden']) {
      form['successURL'].value = form['allItemsSuccessURLHidden'].value;
    }
  } else {
    if (form['successURLHidden']) {
      form['successURL'].value = form['successURLHidden'].value;
    }
  }
  if (!productId && !skuId && !quantity) {
    dojo.xhrPost({
      form: form,
      url: atg.commerce.csr.getContextRoot() + "/include/catalog/shippingLocationsSearchResults.jsp?_windowid=" + window.windowId,
      encoding: "utf-8",
      handle: function(response, ioArgs) {
        if (document.getElementById('storesSearchResults')) {
          document.getElementById('storesSearchResults').innerHTML = response;
        }
      },
      mimetype: "text/html"
    });
  } else {
    dojo.xhrPost({
      form: form,
      url: atg.commerce.csr.getContextRoot() + "/include/catalog/pickupLocationsResults.jsp?_windowid=" + window.windowId,
      queryParams:{
        "productId":productId,
        "skuId":skuId,
        "quantity":quantity,
        "allItems":allItems
      },
      encoding: "utf-8",
      preventCache: true,
      handle: function(response, ioArgs) {
        if (document.getElementById('inStorePickupResults')) {
          document.getElementById('inStorePickupResults').innerHTML = response;
        }
      },
      mimetype: "text/html"
    });
  }
};        

// This function changes state drop-down values according to country change
atg.commerce.csr.catalog.pickupInStoreCountryChange = function (url, shipping) {
  if (shipping) {
    var states = dijit.byId("shippingInStorePickupState");
    var countries = dijit.byId("shippingInStorePickupCountry");
  } else {
    var states = dijit.byId("inStorePickupState");
    var countries = dijit.byId("inStorePickupCountry");
  }
  var stateUrl = url;
  stateUrl += countries.getValue();
  console.log(stateUrl);
  stateStore = new dojo.data.ItemFileReadStore({url:stateUrl});
  states.store = stateStore;
  states.setValue("");
  if (shipping) {
    dojo.byId("shippingInStorePickupState").value = "";
  } else {
    dojo.byId("inStorePickupState").value = "";
  }
};

// This function adds item to cart for instore pickup
atg.commerce.csr.catalog.pickupInStoreAddToCart = function (locationId, message, infoMessage, stockLevel) {
  var form = document.getElementById("inStorePickupForm");
  if (form) {
    if (infoMessage && stockLevel) {
      form.quantity.value = stockLevel;
    }
  
    form.locationId.value = locationId;
    atgSubmitAction({
      panelStack: ["cmcCatalogPS", "globalPanels"],
      panels: ["orderSummaryPanel"],
      form: form,
      sync: true
    });
    atg.commerce.csr.common.hidePopupWithReturn('inStorePickupForm', {result:'cancel'});
    dojo.forEach(
      dojo.query('.atg_commerce_csr_coreProductViewData input[type="text"]'),
      function(node){
        node.value = '';
      }
    );
    dojo.byId('pickupInStoreAction').disabled = 'disabled';
    
    var confirmationMessage = {};
    confirmationMessage.type = "confirmation";
    confirmationMessage.summary = message;

    dijit.byId('messageBar').messages.push(confirmationMessage);
    if (infoMessage && stockLevel) {
      var message2 = {};
      message2.type = "information";
      message2.summary = infoMessage;
      dijit.byId('messageBar').messages.push(message2);
      dijit.byId('messageBar').refresh(2);
    } else {
      dijit.byId('messageBar').refresh(1);
    }

  }
};

// Submits a form to create instore pickup shipping group
atg.commerce.csr.catalog.createInStorePickupShippingGroupForm = function (locationId) {
  var form = document.getElementById("createInStorePickupShippingGroupForm");
  if (form) {
    document.getElementById("locationId").value = locationId;
    atgSubmitAction({
      panelStack: ["globalPanels"],
      form: form,
      sync: true
    });
    atgNavigate({ panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});
  }
  
};

//Checks whether pickup instore button should be enabled/disabled
atg.commerce.csr.catalog.checkPickupInStoreButton = function (skuArray) {
  var enableButton = false;
  var pickupInStoreButton = dojo.byId('pickupInStoreAction');
  var pickupInStoreLabel = dojo.byId('pickupInStoreLabel');

  for (itemKey in skuArray) {
    if (dojo.byId(itemKey).value && dojo.byId(itemKey).value > 0) {
      if (!enableButton) {
        enableButton = itemKey;
      } else {
        enableButton = false;
        break;
      }
    }
  }
  
  if (enableButton && !skuArray[enableButton]) {
    enableButton = true;
  } else {
    enableButton = false;
  }
  
  if (enableButton) {
    pickupInStoreButton.disabled = false;
    pickupInStoreLabel.className = "hidden";
  } else {
    pickupInStoreButton.disabled = true;
    pickupInStoreLabel.className = "visible";
  }
};dojo.provide("atg.commerce.csr.promotion");
atg.commerce.csr.promotion.openPromotionsBrowser = function(gridFunction) {
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').closeButtonNode.onclick = atg.commerce.csr.promotion.revertWallet
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').show();
  var deferred = atgSubmitAction({
    showLoadingCurtain:false,
    sync:true,
    form:dojo.byId("promotionUpdateForm")
  });
  gridFunction();
  var options = dojo.byId("promotionSearchForm")["/atg/commerce/custsvc/promotion/PromotionSearch.site"].options;
  options.length = 0;
  for (var i = 0; i < atg.commerce.csr.promotion.sites.length; i++) {
    var site = atg.commerce.csr.promotion.sites[i];
    options[i] = new Option(site.name, site.value, i == 0 ? true : false, false)
  }
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
};
atg.commerce.csr.promotion.checkPromotion = function (promotionId, isChecked){
  if (isChecked) {
		dojo.query("." + promotionId).forEach(function(node, index, arr){
      node.checked = true;
      node.setAttribute("checked", true);
		});
    var formElement  = dojo.byId("promotionExcludeForm");
    formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value = promotionId;
    atgSubmitAction({
      showLoadingCurtain:false,
      form:formElement
    });
  }
  else {
		dojo.query("." + promotionId).forEach(function(node, index, arr){
      node.checked = false;
      node.removeAttribute("checked");
		});
    var formElement  = dojo.byId("promotionIncludeForm");
    formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value = promotionId;
    atgSubmitAction({
      showLoadingCurtain:false,
      form:formElement
    });
  }
};
atg.commerce.csr.promotion.grantPromotion = function (promotionId, gridFunction){
  var formElement  = dojo.byId("promotionGrantForm");
  formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value = promotionId;
  atgSubmitAction({
    showLoadingCurtain:false,
    sync:true, // we have to wait until this is done before rendering the grid to avoid stale data in the grid
    form:formElement
  });
  dijit.byId('atg_commerce_csr_promotionsTabContainer').selectChild(dijit.byId('atg_commerce_csr_availablePromotions'));
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
  gridFunction();
};
atg.commerce.csr.promotion.removePromotion = function (stateId, gridFunction){
  var formElement  = dojo.byId("promotionRemoveForm");
  formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.stateId"].value = stateId;
  atgSubmitAction({
    showLoadingCurtain:false,
    sync:true, // we have to wait until this is done before rendering the grid to avoid stale data in the grid
    form:formElement
  });
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
  gridFunction();
};
atg.commerce.csr.promotion.revertWallet = function () {
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').hide();
  dijit.byId('atg_commerce_csr_promotionsTabContainer').selectChild(dijit.byId('atg_commerce_csr_availablePromotions'));
  atgSubmitAction({
    panelStack: ["globalPanels", "cmcShoppingCartPS"],
    form:dojo.byId("revertPromotionWalletForm")
  });
};
atg.commerce.csr.promotion.saveWallet = function () {
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').hide();
  atgSubmitAction({
    panelStack: ["globalPanels", "cmcShoppingCartPS"],
    form:dojo.byId("savePromotionWalletForm")
  });
};
atg.commerce.csr.promotion.update = function (gridFunction, button) {
	button.disabled = true;
	var deferred = atgSubmitAction({
    showLoadingCurtain:false,
    sync:true,
    form:dojo.byId("promotionUpdateForm")
  });
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
	gridFunction();
  button.disabled = false;
};
atg.commerce.csr.promotion.search = function (gridFunction, gridInstanceId) {
	this.searchGridInstanceId = gridInstanceId;
  gridFunction({o: atg.commerce.csr.promotion, p: "searchModel"});
};
atg.commerce.csr.promotion.searchModelAllChange = function () {
  var gridInstance = null;
	if (this.searchGridInstanceId) {
		gridInstance = eval(this.searchGridInstanceId);
  }
  if (gridInstance && gridInstance.dataModel && gridInstance.dataModel.count > 0) {
    dojo.byId('promotionInstructions').style.display='inline';
  }
  else {
    dojo.byId('promotionInstructions').style.display='none';
  }
};
dojo.provide( "atg.commerce.csr.order.returns" );
atg.commerce.csr.order.returns.selectReturnRequest = function (pReturnRequestId){
  atgSubmitAction(
      {
        panels:["cmcReturnsHistoryP"],
        form:dojo.byId('transformForm'),
        queryParams: { "historyReturnRequestId" : pReturnRequestId }
      }
  );
};

atg.commerce.csr.order.returns.selectOriginatingOrder = function (pOriginatingOrderId){
  atgSubmitAction(
      {
        panels:["cmcReturnsHistoryP"],
        form:dojo.byId('transformForm'),
        queryParams: { "originatingOrderId" : pOriginatingOrderId }
      }
  );
};
atg.commerce.csr.order.returns.resetRefundValues = function (pParams){
  var form  = document.getElementById("resetRefundValues");
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.modifyRefundValues = function (pParams){
  var form  = document.getElementById("modifyRefundValuesForm");
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.initiateReturnProcess = function (pParams){
  var form  = document.getElementById("csrCreateReturnRequest");
  if(pParams.orderId !== undefined && pParams.orderId !== null)
    form.orderId.value=pParams.orderId;
  atgSubmitAction({
    form:form
  });
};

atg.commerce.csr.order.returns.selectReturnItems = function (pParams){
  var form  = document.getElementById("csrSelectReturnItems");
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.startReturnProcess = function (pParams) {
  var form  = document.getElementById('csrSelectReturnItems');
  form.processName.value = 'Return';
  var successurl = form.startReturnSuccessURL.value;
  form.selectItemsSuccessURL.value = successurl;
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.startExchangeProcess = function (pParams) {
  var form  = document.getElementById('csrSelectReturnItems');
  form.processName.value = 'Exchange';
  var successurl = form.startExchangeSuccessURL.value;
  form.selectItemsSuccessURL.value = successurl;
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.cancelReturnRequest = function (pParams) {
  var form  = document.getElementById('cancelReturnRequest');

  var deferred = atgSubmitAction({form:form});
  deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};

atg.commerce.csr.order.returns.applyRefunds = function (pParams) {
  var form  = document.getElementById('csrApplyRefunds');
  atg.commerce.csr.common.enableDisable({form:'csrApplyRefunds', name:'handleApplyRefunds'},{form:'csrApplyRefunds', name:'handleCancelReturnRequest'});
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.submitReturn = function (pParams){
  var form  = document.getElementById('atg_commerce_csr_submitReturnForm');
  atg.commerce.csr.common.enableDisable('handleSubmitReturnRequest','handleCancelReturnRequest');
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.cancelReturnRequestInRefundPage = function (pParams) {
  var form  = document.getElementById('csrApplyRefunds');
  atg.commerce.csr.common.enableDisable({form:'csrApplyRefunds', name:'handleCancelReturnRequest'},{form:'csrApplyRefunds', name:'handleApplyRefunds'});
  var deferred = atgSubmitAction({form:form});
  deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};

atg.commerce.csr.order.returns.cancelReturnRequestInCompletePage = function (pParams) {
  var form  = document.getElementById('atg_commerce_csr_submitReturnForm');
  atg.commerce.csr.common.enableDisable({form:'atg_commerce_csr_submitReturnForm', name:'handleCancelReturnRequest'},{form:'atg_commerce_csr_submitReturnForm', name:'handleSubmitReturnRequest'});
  var deferred = atgSubmitAction({form:form});
  deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};

atg.commerce.csr.order.returns.editCreditCard = function(pURL){
  atg.commerce.csr.common.submitPopup(pURL, document.getElementById("csrEditCreditCard"), dijit.byId("editPaymentOptionFloatingPane"));
};

/**
 * This method enables/disables the start return process and start exchange process
 * buttons.
 * This is used in the return items selection page.
 *
 */
atg.commerce.csr.order.returns.disableReturnProcessButtons = function (pFlag) {
  var startReturn = dijit.byId('StartReturnProcess');
  if (startReturn) {
    pFlag ? startReturn.disableButton() : startReturn.enableButton();
  }
  var exchangeButton = dijit.byId('StartExchangeProcess');
  if (exchangeButton) {
    pFlag ? exchangeButton.disableButton() : exchangeButton.enableButton();
  }
};
dojo.provide( "atg.commerce.csr.order.scheduled" );

  atg.commerce.csr.order.scheduled.populateData = function (pTableData){
  console.debug("atg.commerce.csr.order.scheduled.populateData started");
    var rowData;
    var l;
    var cells;
    var cellData;
    var table=dojo.byId('schedulesTable');
    for (var x=0; x < pTableData.length; x++) 
    {
      //console.debug("pTableData  is " + pTableData);
      //console.debug("pTableData  length is " + pTableData.length);
      rowData=pTableData[x];
      //console.debug("row data is " + rowData);
      //console.debug("rowData  length is " + rowData.length);
      for (var y=0; y<rowData.length; y++) 
      {
        //console.debug("y is " + y);
        //console.debug("x is " + x);
         cellData = rowData[y];
        //console.debug("cellData is " + cellData);
        cells = table.rows[x+1].cells;
        cells[y].innerHTML=cellData;
      }
    }
  };

atg.commerce.csr.order.scheduled.viewScheduleErrors = function (pURL,title) {
  console.debug("viewScheduleErrors.pURL: " + pURL);
  console.debug("viewScheduleErrors.pURL: testing");
  var paneid = "viewScheduleErrors";
      atg.commerce.csr.common.showPopupWithReturn({
        popupPaneId: paneid,
        title: title,
        url: pURL,
        onClose: function( args ) {  }
      });
};
/**
 * Submits the form for creating a new schedule
 *
 * @param theForm the form that gets submitted. Different forms
 *              are submitted, depending on the state of the order.
 */
atg.commerce.csr.order.scheduled.createSchedule = function (pFormId){

  var theForm = dojo.byId(pFormId);
  atg.commerce.csr.order.scheduled.setFormFromScheduleWidget(theForm);
  atgSubmitAction({
    form:theForm,
    panelStack:["globalPanels"]
  });
};

/** 
* Submits the form for updating a schedule
*/
atg.commerce.csr.order.scheduled.updateSchedule = function (pFormId){
   var theForm = dojo.byId(pFormId);
   atg.commerce.csr.order.scheduled.setFormFromScheduleWidget(theForm);
     atgSubmitAction({
       form:theForm,
       panelStack:["globalPanels"]
     });
};

/**
* This function populates the form properties from the values provide by the
* schedule widget
*/
atg.commerce.csr.order.scheduled.setFormFromScheduleWidget = function (pForm)
{
  var scheduleType = dijit.byId("cscDateRange").scheduleType();
  pForm.scheduleType.value = scheduleType;
  console.debug("setFormFromScheduleWidget scheduleType: " + scheduleType);

  var daysOption = dijit.byId("cscDateRange").daysOption();
  pForm.daysOption.value = daysOption;
  console.debug("setFormFromScheduleWidget daysOption: " + daysOption);

  var occurrencesOption = dijit.byId("cscDateRange").weeksOption();
  pForm.occurrencesOption.value =  occurrencesOption;
  console.debug("setFormFromScheduleWidget occurrencesOption: " + occurrencesOption);

  var monthsOption =   dijit.byId("cscDateRange").monthsOption();
  pForm.monthsOption.value = monthsOption;
  console.debug("setFormFromScheduleWidget monthsOption: " + monthsOption);
  
  var intervalOption =   dijit.byId("cscDateRange").intervalOption();
  pForm.intervalOption.value = intervalOption;
  console.debug("setFormFromScheduleWidget intervalOption: " + intervalOption);
  
  var selectedInterval = dijit.byId("cscDateRange").interval();
  pForm.selectedInterval.value = selectedInterval;
  console.debug("setFormFromScheduleWidget selectedInterval: " + selectedInterval);

  var selectedDays = dijit.byId("cscDateRange").days();
  var days = selectedDays.join(',');
  pForm.selectedDays.value = days; 
  console.debug("setFormFromScheduleWidget selectedDays: " + days);

  var selectedMonths = dijit.byId("cscDateRange").months();
  var months = selectedMonths.join(',');
  pForm.selectedMonths.value = months; 
  console.debug("setFormFromScheduleWidget selectedMonths: " + months);

  var selectedOccurrences = dijit.byId("cscDateRange").occurrences();
  var occurrences = selectedOccurrences.join(',');
  pForm.selectedOccurrences.value = occurrences; 
  console.debug("setFormFromScheduleWidget selectedOccurrences: " + occurrences);

  var selectedDates = dijit.byId("cscDateRange").dates();
  var dates = selectedDates.join(',');
  pForm.selectedDates.value = dates; 
  console.debug("setFormFromScheduleWidget selectedDates: " + dates);
};


atg.commerce.csr.order.scheduled.cancelCreate = function (pForm){
  var theForm = dojo.byId(pForm);
  atgSubmitAction({
    form:theForm
  });
};

atg.commerce.csr.order.scheduled.cancelUpdate = function (pForm){
  var theForm = dojo.byId(pForm);
  atgSubmitAction({
    form:theForm
  });
};
atg.commerce.csr.order.scheduled.loadOrderForAddSchedule = function(orderId)
{
  var theForm = dojo.byId("atg_commerce_csr_loadScheduledOrderForScheduleAdd");
  theForm.orderId.value = orderId;
    atgSubmitAction({
      form : theForm,
      queryParams : {cancelScheduleProcess:"createNewSchedule"}
    });
};    
atg.commerce.csr.order.scheduled.loadOrderForChangeSchedule = function(orderId,scheduleId)
{
  var theForm = dojo.byId("atg_commerce_csr_loadScheduledOrderForScheduleChange");
  theForm.orderId.value = orderId;
    atgSubmitAction({
      form : theForm,
      queryParams : {cancelScheduleProcess:"cancelUpdateSchedule",scheduledOrderId:scheduleId}
    });
    
};

//deprecated: no longer used by CSC. Don't removed this function for backward compatibility
atg.commerce.csr.order.scheduled.activateSchedule = function(orderId,scheduleId)
{
  var theForm = dojo.byId("atg_commerce_csr_scheduled_activateSchedule");
  theForm.orderId.value = orderId;
  theForm.scheduledOrderId.value=scheduleId;
    atgSubmitAction({
      form : theForm
    });
    
};

//deprecated: no longer used by CSC. Don't removed this function for backward compatibility
atg.commerce.csr.order.scheduled.deactivateSchedule = function(orderId,scheduleId)
{
  var theForm = dojo.byId("atg_commerce_csr_scheduled_deactivateSchedule");
  theForm.orderId.value = orderId;
  theForm.scheduledOrderId.value=scheduleId;
    atgSubmitAction({
      form : theForm
    });
    
};

atg.commerce.csr.order.scheduled.submitNow = function(pOrderId)
{
  var theForm = dojo.byId("atg_commerce_csr_scheduled_duplicateAndSubmit");
  theForm.orderId.value = pOrderId;
    atgSubmitAction({
      form : theForm
    });
    
};

/**
* executed when the create schedule form is loaded 
*/
atg.commerce.csr.order.scheduled.loadCreateForm = function(pFormId)
{
  console.debug("atg.commerce.csr.order.scheduled.loadCreateForm");
  var widget = new atg.csc.dateRangePicker({}, dojo.byId("cscDateRange"));
  var theForm = dojo.byId(pFormId);
  atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm(theForm);
  widget.render();
  console.debug("atg.commerce.csr.order.scheduled.loadCreateForm DONE");

};


/**
* executed when the update schedule form is loaded 
*/
atg.commerce.csr.order.scheduled.loadUpdateForm = function(pFormId)
{
  var widget = new atg.csc.dateRangePicker({}, dojo.byId("cscDateRange"));
  var theForm = dojo.byId(pFormId);
  atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm(theForm);
  widget.render();

};

/**
* This function sets the schedule widget values from the form properties 
*/
atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm = function(pForm)
{
  var scheduleType = pForm.scheduleType.value;
  console.debug("setScheduleWidgetFromForm scheduleTypeInput: " + scheduleType);
  dijit.byId("cscDateRange").scheduleType(scheduleType);

  var daysOption = pForm.daysOption.value;
  console.debug("setScheduleWidgetFromForm daysOption: " + daysOption);
  dijit.byId("cscDateRange").daysOption(daysOption);

  var occurrencesOption = pForm.occurrencesOption.value;
  console.debug("setScheduleWidgetFromForm occurrencesOption: " + occurrencesOption);
  dijit.byId("cscDateRange").weeksOption(occurrencesOption);

  var monthsOption = pForm.monthsOption.value;
  console.debug("setScheduleWidgetFromForm monthsOption: " + monthsOption);
  dijit.byId("cscDateRange").monthsOption(monthsOption);
  
  var intervalOption = pForm.intervalOption.value;
  console.debug("setScheduleWidgetFromForm intervalOption: " + intervalOption);
  dijit.byId("cscDateRange").intervalOption(intervalOption);
  
  var selectedInterval = pForm.selectedInterval.value;
  console.debug("setScheduleWidgetFromForm selectedInterval: " + selectedInterval);
  dijit.byId("cscDateRange").interval(selectedInterval);
  
  var selectedDays = pForm.selectedDays.value;
  var days = selectedDays.split(",");
  console.debug("setScheduleWidgetFromForm days: " + days);
  dijit.byId("cscDateRange").days(days);
   
  var selectedOccurrences = pForm.selectedOccurrences.value;
  var occurrences = selectedOccurrences.split(",");
  console.debug("setScheduleWidgetFromForm occurrences: " + occurrences);
  dijit.byId("cscDateRange").occurrences(occurrences);
  
  var selectedMonths = pForm.selectedMonths.value;
  var months = selectedMonths.split(",");
  console.debug("setScheduleWidgetFromForm months: " + months);
  dijit.byId("cscDateRange").months(months);

  var selectedDates = pForm.selectedDates.value;
  var dates = selectedDates.split(",");
  console.debug("setScheduleWidgetFromForm dates: " + dates);
  dijit.byId("cscDateRange").dates(dates);

};
    dojo.provide( "atg.commerce.csr.order.shipping" );

    atg.commerce.csr.order.shipping.applySingleShippingGroup  = function (pParams){
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atgSubmitAction({form:dojo.byId("singleShippingAddressForm")});
    };

    atg.commerce.csr.order.shipping.saveApplySingleShippingGroup  = function (pParams){
      var form  = dojo.byId("singleShippingAddressForm");
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atgSubmitAction({
      panelStack: ["globalPanels"],
      form:dojo.byId("singleShippingAddressForm")
      });
    };
    atg.commerce.csr.order.shipping.applyMultipleShippingGroup = function (pParams){
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atg.commerce.csr.common.enableDisable('csrHandleApplyShippingGroups',
                                            'csrPreserveUserInputOnServerSide');
      atgSubmitAction({form:dojo.byId("csrMultipleShippingAddressForm")});
    };

    atg.commerce.csr.order.shipping.applySelectShippingMethods = function (pParams){
      dojo.debug("entering applySelectShippingMethods()");
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atgSubmitAction({form:dojo.byId("csrSelectShippingMethods")});
    };

    atg.commerce.csr.order.shipping.addShippingAddress = function (){
      atgSubmitAction({form:dojo.byId("csrAddShippingAddress"), sync: true});
      atgNavigate({panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});
    };

    atg.commerce.csr.order.shipping.addElectronicAddress = function (){
      atgSubmitAction({form:dojo.byId("csrAddElectronicAddress"), sync: true});
      atgNavigate({panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});
    };

    atg.commerce.csr.order.shipping.editShippingAddress = function(pURL){
      atg.commerce.csr.common.submitPopup(pURL, dojo.byId("csrEditShippingAddressForm"), dijit.byId("csrEditAddressFloatingPane"));
    };

    atg.commerce.csr.order.shipping.renderShippingPage = function (pMode){
      atgNavigate({panelStack:'cmcShippingAddressPS', queryParams : { mode: pMode}});
    };

    atg.commerce.csr.order.shipping.splitQtyPrompt = function (pURL, pTitle) {
      var splitQtyWindow = "csrMultipleShippingFloatingPane";
      if (splitQtyWindow) {
          atg.commerce.csr.common.showPopupWithReturn({
                                                        popupPaneId: splitQtyWindow,
                                                        title: pTitle || "",
                                                        url: pURL
                                                      });
        }
    };

    atg.commerce.csr.order.shipping.cancelSplitQtyPrompt = function (pMode) {
      var splitQtyWindow = dijit.byId("csrMultipleShippingFloatingPane");

      if (splitQtyWindow) {
        atg.commerce.csr.common.hidePopup (splitQtyWindow);
        var deferred = atgSubmitAction({
          form:"transformForm",
          panelStack:'cmcShippingAddressPS',
          queryParams : { 'select' : 'multiple' }
        });
        deferred.addCallback(function() {
          //if the agent is working with the split shipping groups, that means 
          //they are working with the multiple shipping. Just in case if the multiple
          //shipping area is not open, this open up the multi shipping area.
          var multishippingdivid = dijit.byId("atg_commerce_csr_shipToMultipleAddresses");
          if (multishippingdivid && !multishippingdivid.open) {
            multishippingdivid.toggle();
          }
          }); 
      }
    };

    atg.commerce.csr.order.shipping.splitShippingGroupQty = function(pURL){
      atg.commerce.csr.common.submitPopup(pURL, dojo.byId("csrSplitShippingGroupQty"), dijit.byId("csrMultipleShippingFloatingPane"));
    };

    atg.commerce.csr.order.shipping.newShippingGroupSelectedRule = function () {
      var radio = dojo.byId('singleShippingAddressForm').singleShippingShipToAddressNickname;
      if(!radio){return false;}

      var checkedValue;
      //find the radio value checked

      //this means there is only one radio button in the
      if (radio.length === undefined ) {
        checkedValue = radio.value;
      } else {
        for(var i = 0; i < radio.length; i++){
          if(radio[i].checked){
            checkedValue = radio[i].value;
            break;
          }
        }// end of for loop
      }

      if (checkedValue == 'atg_nsg_nickname') {
         return true;
      } else {
         return false;
      }
    };

    atg.commerce.csr.order.shipping.notifySingleShippingValidators = function () {
    };

    atg.commerce.csr.order.shipping.notifyEditShippingAddressValidators = function () {
    };

    atg.commerce.csr.order.shipping.notifyAddShippingAddressValidators = function () {
    };

dojo.provide( "atg.commerce.csr.pricing.priceLists" );
dojo.require("dojo.date.locale");

atg.commerce.csr.pricing.priceLists =
{

// Sets pricelist to user
selectPriceList : function (priceListFormName, priceListId) {
  if (priceListFormName == "setPriceListForm") {
    var form = document.getElementById("setPriceListForm");
    if (form) form.priceListId.value = priceListId;
  } else {
    var form = document.getElementById("setSalePriceListForm");
    if (form) form.salePriceListId.value = priceListId;
  }
  if (form) {
    atgSubmitAction({
      form: form,
      selectTabbedPanels : ["cmcProductCatalogSearchP"],
      sync: true
    });
  }
},

// Searches for price lists
searchForPriceLists:function(dateFormat){
    var incorrectStartDate = dojo.date.locale.format(dojo.date.add(new Date(),"year", 19), {datePattern: dateFormat, selector: "date"});
    var incorrectEndDate = dojo.date.locale.format(new Date(-1, 00, 01), {datePattern: dateFormat, selector: "date"});

    var startDate = document.getElementById('morePriceListsStartDateInput').value;
    var endDate = document.getElementById('morePriceListsEndDateInput').value;
	
    if (startDate != "" && startDate != dateFormat) {
      if (!dojo.date.locale.parse(startDate, {datePattern: dateFormat, selector: "date"})) {
        document.getElementById('morePriceListsStartDate').value = incorrectStartDate;
      } else {
        document.getElementById('morePriceListsStartDate').value = startDate;
      }
    } else {
      document.getElementById('morePriceListsStartDate').value = "";
    }
    if (endDate != "" && endDate != dateFormat) {
      if (!dojo.date.locale.parse(endDate, {datePattern: dateFormat, selector: "date"})) {
        document.getElementById('morePriceListsEndDate').value = incorrectEndDate;
      } else {
        var tempDateInc = dojo.date.add(dojo.date.locale.parse(endDate, {datePattern: dateFormat, selector: "date"}),"day", 1); 
        document.getElementById('morePriceListsEndDate').value = dojo.date.locale.format(tempDateInc, {datePattern: dateFormat, selector: "date"});
      }
    } else {
      document.getElementById('morePriceListsEndDate').value = "";
    }
    
    atg.commerce.csr.pricing.priceLists.morePriceLists.searchRefreshGrid();
}

};
dojo.provide("framework.FrameworkLink");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare(
  // widget name and class
  "framework.FrameworkLink",

  // superclass
  [dijit._Widget,dijit._Templated],

  // properties and methods
  {
    // parameters
    panelStack: "",

    // settings
    templateString: "<span><a dojoAttachPoint='containerNode' dojoAttachEvent='onclick: onClick'></a></span>",

    // callbacks
    onClick: function(evt) {
      if (this.panelStack) atgSubmitAction({panelStack:this.panelStack,form:dojo.byId('transformForm')});
    }
  }
);


/*
  Date Range Picker Widget v0.2
  
  Created by Sykes,rduyckin on 2008-01-10.
  Copyright (c) 2006 Media~Hive Inc.. All rights reserved.

***************************************************************************/


dojo.provide("atg.csc.dateRangePicker");

dojo.declare(
  "atg.csc.dateRangePicker",
  [dijit._Widget, dijit._Templated, dijit._Container],
  {
    widgetsInTemplate: true,
    ppData: '',
        templatePath: atg.commerce.csr.getContextRoot() + "/script/widget/templates/dateRangePicker.jsp",
    //templatePath: "../dijit/templates/dateRangePicker.jsp",        
    parentForm: {},
    
    _scheduleType: "Interval",        // 'Interval' | 'Calendar'
    _intervalOption: "days",            // 'days' | 'weeks'
    _interval: 1,                   // 1+   sinlge numeric value  
    _daysOption: "allDays",              // 'selectedDays' | 'selectedDates' | 'allDays'
    _weeksOption: "allOccurrences",              // 'allOccurrences'| 'selectedOccurrences' 
    _occurrences: new Array(),                      // 1-5   5 meaning the last week
    _days: new Array(),                       // 1-7  multi select   comma delim list i.e 1,2,3,4,5,6,7
    _dates: new Array(),                       // 1-31  multi select  comma delim list i.e 1,2,3,4,5,6...
    _monthsOption: "allMonths",      // 'allMonths' | 'selectedMonths'
    _months: new Array(),                     // 0-11 multi select   comma delim list i.e 0,1,2,3,4,5,6...
    _daysSelectHandle: '',
    _datesSelectHandle: '',
    _monthsSelectHandle: '',
    _weeksSelectHandle: '',            
    
    postCreate: function(){
      this._daysSelectHandle = dojo.subscribe("atg/csc/scheduleorder/daysSelect", this ,"onWeekDays" );
      this._datesSelectHandle = dojo.subscribe("atg/csc/scheduleorder/datesSelect", this ,"onDates" );            
      this._monthsSelectHandle = dojo.subscribe("atg/csc/scheduleorder/monthsSelect", this ,"onMonths" );                        
      this._weeksSelectHandle = dojo.subscribe("atg/csc/scheduleorder/weeksSelect", this ,"onOccurrences" );
      this.inherited("postCreate", arguments);
      dojo.byId('atg_dateRangePicker_intervalTab').style.position= "static";
      dojo.byId('atg_dateRangePicker_calendarTab').style.position= "static";        
    },
    
    destroy: function(){
      dojo.unsubscribe(this._daysSelectHandle );
      dojo.unsubscribe(this._datesSelectHandle );            
      dojo.unsubscribe(this._monthsSelectHandle );                        
      dojo.unsubscribe(this._weeksSelectHandle);
    },
        
    toggleHandler: function(e){
        console.debug("toggleHandler: " + e)
    },
        
    scheduleType: function(newValue){            

            if(newValue != null){                
            console.debug("SET scheduleType",newValue );
                this._scheduleType = newValue;    
            }else{
            //console.debug("GET scheduleType",this._scheduleType + ":" + dijit.byId("intervalTab").selected );  
            if (dijit.byId("atg_dateRangePicker_intervalTab").selected){
                return "Interval";
            }else{
                return "Calendar";
            }
                return this._scheduleType;
            }            
        },

    intervalOption: function(newValue){            
            if(newValue != null){                
            console.debug("SET intervalOption",newValue );
                this._intervalOption = newValue;    
            }else{
            //console.debug("GET intervalType",this._intervalType );                
                return this._intervalOption;
            }            
        },

    interval: function(newValue){            
            if(newValue != null){                
            console.debug("SET interval",newValue );
                this._interval = newValue;    
            }else{
            //console.debug("GET interval",this._interval );                
                return this._interval;
            }            
        },

    daysOption: function(newValue){            
            if(newValue != null){                
            console.debug("SET daysOption", newValue );
                this._daysOption = newValue;    
            }else{
            //console.debug("GET daysOption", this._daysOption );                
                return this._daysOption;
            }            
        },

    weeksOption: function(newValue){            
            if(newValue != null){                
            console.debug("SET weeksOption", newValue );
                this._weeksOption = newValue;    
            }else{
            //console.debug("GET weeksOption", this._weeksOption );                
                return this._weeksOption;
            }            
        },

        
        // ARRAY        
        occurrences: function(newValue){            
            if(newValue != null){                                
            console.debug("SET occurrences", newValue );
                this._occurrences = newValue;    
            }else{        
                return this.parseIntArray(this._occurrences);
            }            
        },

        // ARRAY    
    days: function(newValue){            
            if(newValue != null){                
            console.debug("SET days", newValue );
                this._days = newValue;    
            }else{
            //console.debug("GET days", this._days );                
                return this.parseIntArray(this._days);
            }            
        },

        // ARRAY
        dates: function(newValue){            
            if(newValue != null){                
            console.debug("SET dates",newValue );
                this._dates = newValue;    
            }else{
            //console.debug("GET dates",this._dates + ":" + this._dates.length );                
            //console.debug("GET cleaned dates",this.parseIntArray(this._dates) + ":" + this._dates.length)
                return this.parseIntArray(this._dates);
            }            
        },
    
    monthsOption: function(newValue){            

            if(newValue != null){                
            console.debug("SET monthsOption",newValue );
                this._monthsOption = newValue;    
            }else{
           // console.debug("GET monthsOption",this._monthsOption );                
                return this._monthsOption;
            }            
        },

    // ARRAY    
    months: function(newValue){            
      if(newValue != null){                
        console.debug("SET months",newValue );
        this._months = newValue;    
      }else{
        // console.debug("GET months",this._months );                
        return this.parseIntArray(this._months);
      }           
    },
    
    parseIntArray: function(items){
      //make sure the values are numeric       
      // check for an empty array
      var outputArray = new Array();
      var item = 0;
      for(i = 0 ;i < items.length; i++){
        item = items[i] + "";        
        //remove NaN elements
        if((! isNaN(item) && (item != "") )){
          outputArray.push(parseInt(item));
        }
      }

      return outputArray;
    },    
    
    onSelectChild: function(obj){
      console.debug("onSelectChild");
      console.debug(obj.id);
      this.selectedTab = obj.id;
      
      //dojo.byId("atg_schedOrder_interval").checked = true;
      
      if(this.selectedTab == "intervalTab"){
          dojo.byId("atg_schedOrder_interval").checked = true;
          this.scheduleType('Interval');
      }else{
           dojo.byId("atg_schedOrder_calendar").checked = true;
          this.scheduleType('Calendar');           
      }       
      
      
    },
    
    onSelectChange: function(e){
      console.debug("TARGET:" + e.target.value );
      // find all the expanded areas and 
      var radioGroupName = e.target.name;
            
      dojo.query('.expandedContent').forEach(function(thisItem){
        
        var radioGroup = dojo.query('input[name=' + radioGroupName + ']', thisItem.parentNode);
      
        if(radioGroup.length!=0){
          // Hide the expanded area
          thisItem.style.display = "none";  
          // Reset the selections in that area (is this actually desirable?)
          // TODO: this is specific to multi-selects, need to make it work with full UI.    
          dojo.query('select', thisItem).forEach(function(select){
            select.selectedIndex = -1;
          })
        }  
      });
            
      // set the values
      switch (e.target.value){
          case 'everyDay':
              this.daysOption('allDays');
              break;
          case 'selectDay':
              this.daysOption('selectedDays');
              break;
          case 'selectDate':
              this.daysOption('selectedDates');
              break;
          case 'everyMonth':
              this.monthsOption('allMonths');              
              break;
          case 'selectMonth':
              this.monthsOption('selectedMonths');
              break;
          default:
              break;
      }
                

      // todo: reset the select box of now closed areas
      
      // find sibling expanded  area for the radio that was just clicked and 
      // expand the exapnded area for that event
      
      dojo.query('.expandedContent', e.target.parentNode.parentNode).forEach(function(thisItem){
        thisItem.style.display = "block";        
      });
      
      
    },
    
    render: function(e){
        console.debug("dateRangePicker.render()" + this.scheduleType());
        // schedule type
        if(this._scheduleType == 'Interval'){
                dijit.byId("atg_dateRangePicker_intervalTab").selected = true;
                dijit.byId("atg_dateRangePicker_calendarTab").selected = false;
                dijit.byId("atg_dateRangePickerTabs").selectChild(dijit.byId("atg_dateRangePicker_intervalTab"));                
            this.selectedTab = "atg_schedOrder_interval";            
        }else{
                dijit.byId("atg_dateRangePicker_intervalTab").selected = false;
                dijit.byId("atg_dateRangePicker_calendarTab").selected = true;
                dijit.byId("atg_dateRangePickerTabs").selectChild(dijit.byId("atg_dateRangePicker_calendarTab"));
            this.selectedTab = "atg_schedOrder_calendar";            
        }
        // interval
        console.debug("render: update interval.value")
        this.intervalValue.value = this.interval();
        //interval type
            var options = this.intervalSelect.options;
            for(i = 0 ; i<options.length ; i++){
            //console.debug("option:" + options[i].value + ":" + this.intervalOption());
                if(options[i].value == this.intervalOption()){
                    options[i].selected = true;
                }
            }

            
            //  days option
            if(this.daysOption() == "allDays"){
                this.everyDayOption.checked=true;
                this.selectDaysPanel.style.display = "none";
                this.selectDatesPanel.style.display = "none";
                console.debug("allDays");
            }else if(this.daysOption() == "selectedDays"){
                this.selectDaysOption.checked=true;
                this.selectDaysPanel.style.display = "block";
                this.selectDatesPanel.style.display = "none";
                console.debug("selectedDays");               
                 
            }else if(this.daysOption() == "selectedDates"){
                this.selectDatesOption.checked=true;
                this.selectDaysPanel.style.display = "none";
                this.selectDatesPanel.style.display = "block";
                console.debug("selectedDates");                
            }
            
            // occurrences option
            
            if(this.weeksOption() == "allOccurrences"){
               this.allOccurrencesOption.checked = true;
               this.selectedOccurrencesOption.checked = false;
               this.weeksSelectPanel.style.display = "none";
            }else{
            
                this.allOccurrencesOption.checked = false;
               this.weeksSelectPanel.style.display = "block";
                this.selectedOccurrencesOption.checked = true;
            }

             // months option             
             if(this.monthsOption() == "allMonths"){
                  this.allMonthsOption.checked = true;
                  this.selectedMonthsOption.checked = false;
                  console.debug("hiding monthsSelect");
                  this.monthsSelectPanel.style.display = "none";
              }else{
                  this.allMonthsOption.checked = false;
                  this.selectedMonthsOption.checked = true;
                console.debug("showing monthsSelect");
                  this.monthsSelectPanel.style.display = "block";
              }
              

            // find all of the toggles and render any that have valid values set
            var registry = dijit.registry;

            for(var id in registry._hash){
                var item = registry._hash[id];
               if(item.declaredClass =="atg.csc.toggleLink"){
                   var togglesArray = new Array();

                   switch(item.type){
                       case "month":
                        var togglesArray = this.months();
                        break;
                        
                       case "day":
                        var togglesArray = this.days();
                        break;
                        
                       case "week":
                        var togglesArray = this.occurrences();
                        break;
                        
                       case "date":
                        var togglesArray = this.dates();
                        break;

                       default:
                        break;
                   }
                   for(j = 0 ; j<togglesArray.length ; j++){
                      if(togglesArray[j] == item.value){
                          item.show();
                      }
                    }
                   
                   
               }
           }

    },
    
    onIntervalOption: function(e){
      console.debug("Interval Option:" + e.target.value);
      options = this.intervalSelect;

      for (i = 0 ;i<options.length;i++){
        if(options[i].selected == true){
          this.intervalOption(options[i].value);
        }
      }
      console.debug("intervalOption:" + this.intervalOption());         
    },
    
    onOccurrences: function(item){
      console.debug("Occurrence:" + item.value);
      if(item.toggleState == 1){
        this._occurrences[this._occurrences.length] = item.value;
      }else{
        this._occurrences = this.occurrences();
        //ie6 workaround        
        var occurrencePosition = this.getItemIndex(this.occurrences(),item.value);
        this._occurrences.splice(occurrencePosition,1);
      }
    },

    onWeekDays: function(item){      
      console.debug("onWeekDays: " + item.label + " : " + this.days() );
      if(item.toggleState == 1){       
        this._days[this._days.length] = item.value;
      }else{       
        this._days = this.days();        
        //ie6 workaround        
        var weekdayPosition = this.getItemIndex(this.days(),item.value);
        this._days.splice(weekdayPosition,1);
      }
      console.debug("days array: " + this.days());
    },
    
    onDay: function(e){
        console.debug("WeekDay:" + e.target.id);
    },
    
    onDates: function(item){
      console.debug("Day:" + item.value);
      if(item.toggleState == 1){
        this._dates[this._dates.length] = item.value;
      }else{
        this._dates = this.dates();
        //ie6 workaround        
        var datePosition = this.getItemIndex(this.dates(),item.value);
        this._dates.splice(datePosition,1);
      }
      console.debug("dates:" + this.dates().join(","));
    },
    
    onMonths: function(item){
      console.debug("month:" + item.value);
      if(item.toggleState == 1){
        this._months[this._months.length] = item.value;
      }else{
        this._months = this.months();
        //ie6 workaround        
        var monthPosition = this.getItemIndex(this.months(),item.value);
        this._months.splice(monthPosition,1);
      }
      console.debug("months:" + this.months().join(","));
    },
    
        
    onWeekSelect: function(e){
        var radioGroupName = e.target.name;
        this.weeksOption('selectedOccurrences');            
        dojo.query('.atg_dateRangePicker_weeksSelect', e.target.parentNode.parentNode).forEach(function(thisItem){
        thisItem.style.display = "block";        
      });
    },

    onAllWeeksSelect: function(e){
      var radioGroupName = e.target.name;
      dojo.byId("weekListing").style.display = "none" ;

      this.weeksOption('allOccurrences');
    },
    
    onScheduleType: function(e){
        
      if(dojo.byId("atg_schedOrder_interval").checked){
          this.scheduleType("Interval");
      }else{
          this.scheduleType("Calendar");
      }
    },
    
    onInterval: function(e){
        console.debug(this.intervalValue.value);
        // only allow numeric 
        var tempValue = this.intervalValue.value;
        var newValue = "";
        for(i = 0 ;i < tempValue.length ;i++){
          if(! isNaN(tempValue.charAt(i))){
            newValue += tempValue.charAt(i);
          }          
        }
        this.intervalValue.value = newValue;
        this.interval(tempValue);
    },
    
    getItemIndex: function(list, obj){      
        for(var i=0; i<list.length; i++){
          if(list[i] == obj){
            return i;
          }
        }
        return -1;
    },
    
    hijackForm: function(){
      
      this.parentForm = this.getParentOfType(this.domNode, ["FORM"]);
      
      this.formSubmitButton = dojo.query('input[type="submit"]', this.parentForm)['0'];
      
      dojo.connect(this.formSubmitButton, "onclick", this, "submitClicked");
      
      
    },
    
    
    submitClicked: function(e){
      
      //console.debug(this.parentForm);
      //e.preventDefault();
      
    },
    
    
    // Utility Functions
    /* from the Dojo 1.0 editor, no idea why it's not in core */
    
    isTag: function(/*DomNode*/node, /*Array*/tags){
      if(node && node.tagName){
        var _nlc = node.tagName.toLowerCase();
        for(var i=0; i<tags.length; i++){
          var _tlc = String(tags[i]).toLowerCase();
          if(_nlc == _tlc){
            return _tlc;
          }
        }
      }
      return "";
    },
    
    getParentOfType: function(/*DomNode*/node, /*Array*/tags){
      while(node){
        if(this.isTag(node, tags).length){
          return node;
        }
        node = node.parentNode;
      }
      return null;
    },
    
    
    sanitySaver: ''
});
dojo.provide("atg.csc.toggleLink");

dojo.declare(
  "atg.csc.toggleLink",
  [dijit._Widget, dijit._Templated, dijit._Container],
  {
    widgetsInTemplate: true,
    ppData: '',
        templatePath: atg.commerce.csr.getContextRoot() + "/script/widget/templates/toggleLink.jsp",
    //templatePath: "../dijit/templates/toggleLink.jsp",        
    parentForm: {},
        
    toggleState:0, // 0 | 1
    value: 0,    
    label: "",
    type: "",
    eventTopic: "",

        postCreate: function(){
          this.linkPoint.innerHTML = this.label;
          this.inherited("postCreate", arguments);  
        },
        show: function(){
            if(this.toggleState == 0){
                this.toggleState = 1;
               // this.linkContainer.style = "background-color:pink;"
                this.linkContainer.className = "atg_commerce_csr_selectedItem";

            }else{
                this.toggleState = 0;
                this.linkContainer.className = "atg_commerce_csr_nonSelectedItem";                
            }
            
        },
        selectLink: function(e){

           this.show();
           dojo.publish("atg/csc/scheduleorder/" + this.eventTopic ,[this]);
           //parent.toggleHandler();
           //console.debug("css:" + this.linkPoint.className  );    
           console.debug("ToggleLink:" + this.label + ":" +  this.value + ":" + this.toggleState);
        },
    // Utility Functions
    /* from the Dojo 1.0 editor, no idea why it's not in core */
    
    isTag: function(/*DomNode*/node, /*Array*/tags){
      if(node && node.tagName){
        var _nlc = node.tagName.toLowerCase();
        for(var i=0; i<tags.length; i++){
          var _tlc = String(tags[i]).toLowerCase();
          if(_nlc == _tlc){
            return _tlc;
          }
        }
      }
      return "";
    },
    
    getParentOfType: function(/*DomNode*/node, /*Array*/tags){
      while(node){
        if(this.isTag(node, tags).length){
          return node;
        }
        node = node.parentNode;
      }
      return null;
    },
    
    
    sanitySaver: ''
});
