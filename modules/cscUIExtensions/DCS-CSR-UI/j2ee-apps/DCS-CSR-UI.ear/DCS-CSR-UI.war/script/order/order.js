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
};