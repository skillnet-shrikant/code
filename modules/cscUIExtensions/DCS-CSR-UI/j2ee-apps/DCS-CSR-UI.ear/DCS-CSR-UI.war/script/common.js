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

