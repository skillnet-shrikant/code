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
};