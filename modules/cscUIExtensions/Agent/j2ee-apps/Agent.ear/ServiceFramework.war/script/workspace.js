function showPreferences(){
var theForm = document.getElementById("transformForm"); 
  atgSubmitAction({
    form: theForm,
    nextSteps: "userPreferencesNextSteps",
    panels: ["nextStepsPanel"],
    panelStack: ["preferencesPanels","helpfulPanels"],
    sync: true
  }).addCallback(currPrefHolder.saveCurrentPreferences);
  //currPrefHolder - object implemented in userPreferencesAction.js
}

function insertHyperlink() {
  dojo.debug("insertHyperlink called");
  var theForm = document.getElementById("insertHyperlinkForm");

  atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/InsertFormHandler",
    form: theForm
  });
}

 /* Copyright (C) 1999-2007  Art Technology Group, Inc. */
/*************************************************************************
// customerActions.js
// Defines client-side action events for the customer tab
*************************************************************************/
dojo.getObject("atg.service.customer", true);

function editCustomer(successMessageFormat, failureMessage){
  /* successMessageFormat uses customer info that input user,
   * failureMessage uses customer profile info
   */
 var theForm = dojo.byId("customerCreateForm");
 var firstName = theForm["firstName"].value;
 var lastName = theForm["lastName"].value;

 theForm["atg.successMessage"].value = dojo.string.substitute(successMessageFormat, [firstName, lastName]);
 theForm["atg.failureMessage"].value = failureMessage;

atgSubmitAction({
  form: theForm,
  formHandler: "/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler",
  panelStack: ["customerPanels","globalPanels"]
  });
}

function viewCustomer(theFormId){
var theForm = dojo.byId(theFormId);
//assumes the customerId field in the form has been set
//assumes the viewMode field has been set or "" is OK.
atgSubmitAction({
  form: theForm,
  panelStack: ["customerPanels"],
  tab: atg.service.framework.changeTab('customersTab')
});
}

function validateForCreate(){
 var theForm = dojo.byId("customerCreateForm");
 if (theForm.saveOnUpdate.checked)
   return true;
 else
   return false;
};

function createAccountResetRequiredFields() {
	 var theForm = dojo.byId("customerCreateForm");
	 if (theForm.saveOnUpdate.checked)
	 { // validate email address only when saveOnUpdate is checked
	   dijit.byId("cpEmail").validator = dojox.validate.isEmailAddress;
	 }
	 else 
	 { // stub validator function, we dont need to validate when saveOnUpdate is unchecked
	   dijit.byId("cpEmail").validator = function() { return true; };
	 }
	 dijit.byId("cpEmail").validate();
};

function createCustomer(successMessageFormat, failureMessageFormat){
	 var theForm = dojo.byId("customerCreateForm");
	 var firstName = theForm["cpFirstName"].value;
	 var lastName = theForm["cpLastName"].value;

	 theForm["atg.successMessage"].value = dojo.string.substitute(successMessageFormat, [firstName, lastName]);
	 theForm["atg.failureMessage"].value = dojo.string.substitute(failureMessageFormat, [firstName, lastName]);

	 if (theForm.saveOnUpdate.checked)
	 {
	  atgSubmitAction({
	    form: theForm
	    });
	  }
	  else
	  {
	  	/* this is called when you are just updating the values in the new profile, such as the name but haven't checked the box
	  	 * to persist the profile to the repository. we always want to stay on the same page, regardless of success or error.
	  	 */
    theForm["successURL"].value = window.contextPath + "/framework.jsp?_windowid=" + window.windowId;
    theForm["errorURL"].value = window.contextPath + "/framework.jsp?_windowid=" + window.windowId;
    theForm["password"].value ="";
	  atgSubmitAction({
	    form: theForm,
	    panelStack: ["customerAccountPanels","globalPanels"]
	    });
	  }	
}

function createCustomerInEnvironment(theFormId){
  var theForm = dojo.byId(theFormId);
  atgSubmitAction({
    form: theForm,
    panels: ["nextStepsPanel","sideViewRecentTicketsPanel","ticketDetailErrorPanel","ticketCustomerInformationPanel",
    "ticketSummaryPanel","ticketActivityPanel"],
    nextSteps: "ticketViewNextSteps",
    panelStack: ["ticketPanels","globalPanels"],
    tab: atg.service.framework.changeTab('ticketsTab')
  });
}

function resetCustomerPassword() {
  atgSubmitAction({
    form: dojo.byId("resetPasswordForm"),
    nextSteps: "customerAccountNextSteps",
    panels: ["nextStepsPanel"],
    panelStack: "customerPanels",
    tab: atg.service.framework.changeTab('customersTab')
  });
}

function emailNewPassword(confirmationMessage) {
  atgSubmitAction({
    form: dojo.byId('customerResetPasswordForm'),
    panelStack: 'globalPanels',
    sync: true
    
  });
  dijit.byId('messageBar').addMessage({type:'confirmation', summary:confirmationMessage});
}

function linkCustomerNoSwitch(theId, panelStacks) {
var theForm = dojo.byId("linkCustomerForm");
theForm.linkCustomerId.value = theId;
atgSubmitAction({
  form: theForm,
  panelStack: panelStacks,
  queryParams: {contentHeader: true}
});
}


function customerFilterSearch(){
atgSubmitAction({
  form: dojo.byId("customerSearchFilterForm"),
  nextSteps: "customerSearchNextSteps",
  dynamicIncludes: ["include/customerSearchResults.jsp"]
});
}

function customerSearch(){
atgSubmitAction({
  form: dojo.byId("customerSearchForm"),
  nextSteps: "customerSearchNextSteps",
  panels: ["nextStepsPanel","customerSearchResultsPanel"],
  dynamicIncludes: ["include/customerSearchResults.jsp"]
});
}

function customerSearchPaging(parameters){
var theForm = dojo.byId("customerSearchPagingForm");
theForm.parameters.value=parameters;
atgSubmitAction({
  form: theForm,
  nextSteps: "customerSearchNextSteps",
  dynamicIncludes: "include/customerSearchResults.jsp"
});
}

function backToCustomerSearch(){
atgSubmitAction({
  form: dojo.byId("backToCustomerSearchForm"),
  panels: ["nextStepsPanel"],
  nextSteps: "customerSearchNextSteps",
  panelStack: "customerSearchPanels"
});
}

syncToCustomer = function()
{
  atgSubmitAction({
    form: dojo.byId("syncCurrentCustomer"),
    panelStack: ["globalPanels"]
  });
};

function viewCurrentCustomer(tabName){
  var theForm = dojo.byId("globalViewCustomerForm");
  theForm.customerId.value = atg.service.ticketing.activeCustomerId;

  if(atg.service.ticketing.isActiveCustomerTransient)
  {
    atgSubmitAction({
      form: theForm,
      panelStack: ["customerAccountPanels"],
      tab: atg.service.framework.changeTab(tabName)
    });
  }
  else
  {
    atgSubmitAction({
      form: theForm,
      panelStack: ["customerPanels"],
      tab: atg.service.framework.changeTab(tabName)
    });
  }
}

viewCustomerSelect = function(customerId)
{
  var formElement  = dojo.byId("viewCustomerSelectForm");
  formElement["/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.inputParameters.changeProfileId"].value = customerId;
  atgSubmitAction({
    form: formElement,
    panels: ["sideViewRecentTicketsPanel"],
    panelStack: ["customerPanels","globalPanels"]
  });
};

function createNewCustomer(tabId){
  var tab;

  // Ensure that the customer selection popup is closed before
  // switching to the customer creation tab.
  if(dojo.byId("atg_commerce_csr_catalog_customerSelectionPopup")) {
    atg.commerce.csr.common.hidePopupWithReturn('atg_commerce_csr_catalog_customerSelectionPopup');
  }

  if(tabId == null)
  {
    tab = atg.service.framework.currentTab;
  }
  else
  {
    tab = tabId;
  }
  if(atg.service.ticketing.isActiveCustomerTransient)
  {
    showCreateCustomerPanel(tabId);
  }
  else
  {
    atgSubmitAction({
      form: dojo.byId("createNewCustomer"),
      panelStack: ["customerAccountPanels","globalPanels"],
      tab: atg.service.framework.changeTab(tab)

    });
  }
}

function showCreateCustomerPanel(tabId){
  var tab;
    if(tabId == null)
    {
      tab = atg.service.framework.currentTab;
    }
    else
    {
      tab = tabId;
    }

  var theForm = dojo.byId("showCurrentCustomer");
  theForm.customerId.value=atg.service.ticketing.activeCustomerId;
    atgSubmitAction({
    form: theForm,
    panelStack: ["customerAccountPanels"],
    tab: atg.service.framework.changeTab(tab)
  });
}

function showCustomerSearch(){
  atgSubmitAction({
  form:dojo.byId('transformForm'),
  nextSteps: "customerSearchNextSteps",
  panelStack: "customerSearchPanels",
  tab: atg.service.framework.changeTab('customersTab')
  });
};

function viewCustomerFromSearch(profileId){
  var theForm = dojo.byId("showCurrentCustomer");
  theForm.customerId.value=profileId;
    atgSubmitAction({
    form: theForm,
    panelStack: ["customerPanels"]
  });
}

// toggles truncated note comment to show full comment
function toggleNoteComment(togglerHrefId, noteCommentDivId, noteFullCommentDivId, openedClass, closedClass)
{
  toggleShowing(dojo.byId(noteFullCommentDivId));
  toggleShowing(dojo.byId(noteCommentDivId));
  dojo.toggleClass(togglerHrefId, closedClass);
  dojo.toggleClass(togglerHrefId, openedClass);
};

// adds new customer note
function createNewCustomerNote() {
  var addNewCustomerNoteForm = dojo.byId('addNewCustomerNoteForm');
  if (addNewCustomerNoteForm) {
      atgSubmitAction(
      {
        form: addNewCustomerNoteForm,
        panels: ["customerInformationPanel"]
      });
      hidePopupWithResults('addCustomerNotePopup', {result:'ok'});
  }
}
dojo.require("dojo.parser");
var atgLoadingCount = 0;
var atgSubmitDebug = true;

function atgShowLoadingIcon() {
  var showStart = new Date();
  console.debug("atgLoadingCount before = " + atgLoadingCount);
  if (dijit.byId("atgLoadingDialogWidget")) {
    atgLoadingCount++;
    console.debug("atgLoadingCount after = " + atgLoadingCount);
    
    if (atgLoadingCount >= 1) {
      dojo.byId("opaqueBackground").style.display="block";
    }
    
    if (atgLoadingCount === 1) {
      console.debug("Showing loading icon");
      if ((dijit.byId("pageLoadingNotificationObject") != null)) {
      	dijit.byId("pageLoadingNotificationObject").style.display="block";
      }
      if (dojo.byId("pageLoadingNotificationContainer") != null) {
        dojo.byId("pageLoadingNotificationContainer").style.display="block";
      }
      dijit.byId("pageLoadingNotificationObject").setContent(atgPageLoadingNotificationMessage,"message",0);
      dijit.byId("pageLoadingNotificationObject").show();
    }
  }
  var showEnd = new Date();
  if (atgSubmitDebug) { console.debug("Showing the loading mask took " + (showEnd - showStart));}
}

function atgHideLoadingIcon() {
  var hideStart = new Date();
  console.debug("atgLoadingCount before = " + atgLoadingCount);
  if (dijit.byId("atgLoadingDialogWidget")) {
    if (atgLoadingCount >0) {
      atgLoadingCount--;
    }
    console.debug("atgLoadingCount after = " + atgLoadingCount);
    
    if (atgLoadingCount === 0) {
      console.debug("Hiding loading icon");
      dojo.byId("opaqueBackground").style.display="none";
      if (dijit.byId("pageLoadingNotificationObject") && dijit.byId("pageLoadingNotificationObject").fadeAnim) {
        dijit.byId("pageLoadingNotificationObject").fadeAnim.play();
      }
      if ((dijit.byId("pageLoadingNotificationObject") != null)) {
      	dijit.byId("pageLoadingNotificationObject").style.display="none";
      }
      if (dojo.byId("pageLoadingNotificationContainer") != null) {
        dojo.byId("pageLoadingNotificationContainer").style.display="none";
      }
    }
  }
  var hideEnd = new Date();
  if (atgSubmitDebug) { console.debug("Hiding the loading mask took " + (hideEnd - hideStart));}
}

function atgBindLists(theForm, theLists, formhandlerName, contentMap){
  if (atgSubmitDebug) { console.debug("Binding a map of lists");}
  var listParamValue;
  var formProperties = theLists;
  for (formProperty in formProperties){
    var plainPrefix=formhandlerName + formProperty;
    if (atgSubmitDebug) { console.debug("plain prefix " + plainPrefix);}
    contentMap[plainPrefix] = [];
    for (var i = 0; i < formProperties[formProperty].length; i++){
      listParamValue = formProperties[formProperty][i];
      if (atgSubmitDebug) { console.debug("value " + listParamValue);}
      contentMap[plainPrefix].push(listParamValue);
    }
  }
}
//function testBindLists(){
//  atgSubmitAction({
//    form: document.getElementById("testListsForm"),
//    listParams: {testList: ["one", "two", "three"]},
//    formHandler: "/atg/svc/ui/formhandlers/TicketingFormHandler"
//  });
//}
function atgBindMaps(theForm, formhandlerName, theMaps)
{
  if (atgSubmitDebug) { console.debug("Binding a map of map");}
  // {formProperty: {key: value}}
  var mapParam;
  var mapParamDarg;
  var mapParamValue;
  var formProperties = theMaps;
  for (formProperty in formProperties){
    var plainPrefix=formhandlerName + formProperty;
    if (atgSubmitDebug) { console.debug("plain prefix " + plainPrefix);}
    var fhPrefix="_D:"+plainPrefix;
    if (atgSubmitDebug) { console.debug("formhandler prefix " + fhPrefix);}
    var mapItemsArray = [];
    for (mapEntry in formProperties[formProperty]){
      mapParam = mapEntry + "=" + formProperties[formProperty][mapEntry];
      if (atgSubmitDebug) { console.debug("setting map key=value " + mapParam);}
      mapItemsArray.push(mapParam);
    }
    theForm[plainPrefix].value = mapItemsArray.join();
  }
}
//function testBindMaps(){
//  atgSubmitAction({
//    form: document.getElementById("testMapsForm"),
//    mapParams: {testMap: {key1: "one", key2: "two", key3:"three"}},
//    formHandler: "/atg/svc/ui/formhandlers/TicketingFormHandler"
//  });
//}

function discardElement(theElement) {
  if (dojo.isIE) {
    var garbage = dojo.byId('IELeakGarbageBin');
    if (!garbage) {
      garbage = document.createElement('div');
      garbage.id = 'IELeakGarbageBin';
      garbage.style.display = "none";
      document.body.appendChild(garbage);
    }
    garbage.appendChild(theElement);
  }
}
function clearElements() {
  if (dojo.isIE) {
    var garbage = dojo.byId('IELeakGarbageBin');
    if (garbage) {garbage.innerHTML = '';}
  }
}

// This function will take the provided map of form property names to values and set
// the given form's properties.
//
function atgBindFormValues(pForm, pFormValues)
{
  for (var i in pFormValues) {
    pForm[i].value = pFormValues[i];
    if(atgSubmitDebug) console.debug("setting form input: " + i + " to value: " + pFormValues[i]);
  }
}



// url: string, e.g "/framework.jsp", defaults to framework.jsp
// form: Javascript form object, use document.getElementById("theFormId") or document.forms["theFormName"]
// formId: form id to use. can be specified instead of form. dojo.byId will be used to get access for the form object.
// formInputValues: map of form element names to values. Each form element name will have its value set before submission. (i.e. form[name].value = formInputValues[name])
// tab: name of the new tab, usually from FrameworkChangeTab("theTabName"), if any
// nextSteps: the string name of the next steps to use, if any
// panelStack: the panel stack to switch to, if any
// panels: array of strings naming the panels to refresh e.g. ["panel1","panel2"]
// formHandler: can be used instead of paramsMapName if your extra parameters map is formHandler.parameterMap
// paramsMapName: fully qualified name of the bean map property that will get the extraParams (formhandler path plus map parameter name)
// extraParams: parameters that will be set into a map property, usually parameterMap on FrameworkBaseFormHandler
// listParams: a Map of arrays.  The keys are the property names, the array the values {one: [a,b,c], two: [x,y,z]}
// mapParams: a Map of Maps.  The "outer" keys are the property names, the "inner" keys are the map keys {property: {key: value, key2: value}}
// queryParams: query parameters which will be added as-is to the request URL
// sync: send the request synchronously
// preventCache: prevent IE from caching (we shouldn't normally need to set this since the JSP will set no-cache)
// showLoadingCurtain: set to false to prevent the "Loading" curtain from showing up.  Defaults to true.
// selectTabbedPanels: array of panel identifiers to set to the selected state. Applies to panels that are in a row of tabbed panels.
// dynamicIncludes: Deprecated
// otherContext: Deprecated
// treeTables: Deprecated. Used for the deprecated tree-table component.
function atgSubmitAction(params){
  if(atgSubmitDebug) console.debug("atgSubmitAction started - ServiceFramework");
  var submitStart = new Date();
  
  // The FCK Editor has a bug in which the rich text editor crashes Internet Explorer when focus is
  // placed inside the editor and atgSubmitAction is called. To correct this issue, we use the publish/subscribe
  // feature to send out a notification that this function is being called - and we can then reset the page
  // focus before the panel is unloaded.
  dojo.publish("atgSubmitAction");
  
  var form;
  if (params.form !== undefined && params.form !== null){
    form= params.form;
  }
  if (form == null && params.formId !== undefined && params.formId !== null){
    form = dojo.byId(params.formId);
  }
  if(atgSubmitDebug)
  {
    if(form == null)
      console.debug("Calling atgSubmitAction without a form or formId ");
    else
      console.debug("Calling atgSubmitAction with form " + form.id);
  }
  if (params.showLoadingCurtain !== false)
  {
    atgShowLoadingIcon();
  } else {
    atgLoadingCount++; // prevent the callback from decrementing the loading count below zero
  }
  if (params.url === undefined || params.url === null || params.url === '') {
    params.url = window.contextPath + "/framework.jsp" + window.sessionid;
  }

  if(atgSubmitDebug)
    console.debug("atgSubmitAction using params.url - " + params.url);

  if (params.mimeType === undefined || params.mimeType === null || params.mimeType === '') {
    params.mimeType = "text/html";
  }
  if (params.formHandler === undefined || params.formHandler === null || params.formHandler === '') {
    params.formHandler = "/atg/svc/ui/formhandlers/FrameworkBaseFormHandler";
  }
  if (params.paramsMapName === undefined || params.paramsMapName === null || params.paramsMapName === '') {
    params.paramsMapName = params.formHandler + ".parameterMap";
  }
  if (params.sync === undefined || params.sync === null || params.sync === '') {params.sync = false;}
  if (params.preventCache === undefined || params.preventCache === null || params.preventCache === '') {params.preventCache = false;}
  var contentMap = {};
  if (params.queryParams !== undefined && params.queryParams !== null) {contentMap = params.queryParams;}

  if (atgSubmitDebug)
      console.debug("atgSubmitAction: params.useRedirect = " + params.useRedirect);

  //if (params.useRedirect == undefined || params.useRedirect)
  //{
  //  contentMap["atg.formHandlerUseForwards"] = false;
  //}
  //else if (!params.useRedirect) {
  //  contentMap["atg.formHandlerUseForwards"] = true;
  //}


  //if (params.useRedirect != undefined && !params.useRedirect) {
  if (params.useRedirect == undefined || !params.useRedirect) {
      console.debug("atgSubmitAction: setting atg.formHandlerUseForwards = true");
      contentMap["atg.formHandlerUseForwards"] = true;
  }
  else {
      console.debug("atgSubmitAction: NOT setting atg.formHandlerUseForwards");
  }

  contentMap._isppr = true;
  contentMap._windowid = window.windowId;
  if (window.requestid) { contentMap._requestid = window.requestid;}
  if (params.tab) { contentMap.t = params.tab;}
  if (params.cell) { contentMap.c = params.cell;}
  if (params.nextSteps) { contentMap.ns = params.nextSteps;}
  if (params.panelStack) { contentMap.ps = params.panelStack;}
  if (params.panels) { contentMap.p = params.panels;}
  if (params.selectTabbedPanels) { contentMap.selectTabbedPanelIds = params.selectTabbedPanels;}
  if (params.dynamicIncludes) {contentMap.di = params.dynamicIncludes;}
  if (params.otherContext) {contentMap.ctx = params.otherContext;}
  if (params.treeTables) {contentMap.tt = params.treeTables;}
  if (!params.formHandler.charAt(params.formHandler.length) != ".") {
    params.formHandler = params.formHandler + ".";
  }
  if (params.extraParams){
    var plainPrefix=params.paramsMapName;
    var fhPrefix="_D:"+plainPrefix;
    var localExtraParams = params.extraParams;
    var mapItemsArray = [];
    var parameterMapParamValue;
    for (extraKey in localExtraParams){
      parameterMapParamValue = extraKey + "=" + localExtraParams[extraKey];
      if (atgSubmitDebug) { console.debug("adding map param " + parameterMapParamValue);}
      mapItemsArray.push(parameterMapParamValue);
    }
    form[plainPrefix].value = mapItemsArray.join();
    form[fhPrefix].value = " ";
  }
  if (params.listParams){
    atgBindLists(form,params.listParams, params.formHandler, contentMap);
  }
  if (params.mapParams){
    atgBindMaps(form, params.formHandler, params.mapParams);
  }
  if (params.formInputValues !== undefined && params.formInputValues !== null){
  if(atgSubmitDebug) console.debug("formInputValues provided");
    atgBindFormValues(form, params.formInputValues);
  }
  else
  {
  if(atgSubmitDebug) console.debug("no formInputValues provided");
  }
  var deferred = dojo.xhrPost({
    url: params.url,
    mimetype: params.mimeType,
    handleAs: params.handleAs,
    content: contentMap,
    encoding: "utf-8",
    timeout: atgXhrTimeout,
    handle: function(response, ioArgs){
      atgDojoHandleResponse(response, ioArgs);
      return response;
    },
    form: form,
    sync: params.sync,
    preventCache: params.preventCache
  });
  deferred.addBoth(function(response) {
    var mb = dijit.byId("messageBar");
    if (mb) {
      mb.retrieveMessages();
    }
    return response;
  });
  var submitEnd = new Date();
  if (atgSubmitDebug) { console.debug("Submitting form took " + (submitEnd - submitStart) + "ms");}
  return deferred;
}
function atgChangeTab(newTab,nextSteps,panelStack,panels,extraParams)
{
  atgSubmitAction({url: window.contextPath + "/framework.jsp",
    form: document.getElementById("tabsForm"),
    tab: newTab,
    nextSteps: nextSteps,
    panelStack: panelStack,
    panels: panels,
    paramsMapName: "/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.parameterMap",
    extraParams: extraParams
  });
}
function atgSubmitTreeTable(treeTableForm,formHandler,contentParams)
{
  var contentMap = contentParams;
  atgSubmitAction({
    form: treeTableForm,
    formHandler: formHandler,
    extraParams: contentParams
  });
}

function atgDojoHandleError(redirectToErrorUrl, type, error, http, kwArgs){
  atgHideLoadingIcon();

  // Exception handling to error URL
  if (redirectToErrorUrl) {
    if (is.gecko) {
      redirectToErrorUrl = window.contextPath+"\/"+redirectToErrorUrl+"?"+window.windowIdParamName+"="+window.windowId;
    } else {
      redirectToErrorUrl = window.contextPath + "\/" + redirectToErrorUrl;
    }
    if (window.location.pathname) {
      window.location.pathname = redirectToErrorUrl;
    }
    else if (document.location.pathname) {
      document.location.pathname = redirectToErrorUrl;
    }
  }
}
var _ppr_elementSeparator = "<701cf83a4e9f>";
function atgDojoHandleResponse(response, ioArgs){
  if (atgSubmitDebug) {console.debug("Callback atgDojoHandleResponse was called");}
  var startDate = new Date();
  if (response instanceof Error) {
    atgHideLoadingIcon();
    if (response.dojoType == "cancel") {
      alert("Connection with the server was canceled");
    }
    else if (response.dojoType == "timeout") {
      alert("Connection with the server timed out");
    }
    else {
      alert("An error occurred when talking to the server. " + response);
      console.error(response);
    }
    return;
  }
  else if (ioArgs.handleAs == 'json' ) {
    atgHideLoadingIcon();
    if (atgSubmitDebug) {console.debug("The returned response is json data.");}
  } else {
    if (response instanceof Error) {response = response;} // error returns a JS exception where normal load returns the response text
    // Refresh target elements and invoke target scripts via JavaScript
    //var startSplit = new Date();
    if (response && ioArgs.handleAs == 'text') {
      var nameValuePairs = response.split(_ppr_elementSeparator);
      atgHideLoadingIcon();
    }
    else if (response) {
      atgHideLoadingIcon();
      alert("Server responded with something other than a string. " + response.toSource());
      return;
    }
    else {
      atgHideLoadingIcon();
      console.debug("An empty response body was received from the server");
      return;
    }
    //var endSplit = new Date();
    //console.debug("Splitting the response took " + (endSplit - startSplit));
    var counter = 0;
    while (counter < nameValuePairs.length - 1) {
      counter++;
      var name = nameValuePairs[counter++];
      var mode = nameValuePairs[counter++];
      var value = nameValuePairs[counter++];
      if (!name || name === "") {
        console.debug("Bad response from server, no name on returned data. Trying the rest of the response");
        continue;
      }

      if (!value) {
        console.debug("Value of this response-part is empty, so nothing to do.  Skipping.")
      }

      if (name == "sessioninvalid") {
        // Value contains redirect URL for invalid session
        window.sessioninvalid = true;
        window.location = value;
        return 1;
      }
      else if (name == "javascript") {
        var startJS = new Date();
        if (djConfig.isDebug){
          eval(value); // allow errors to propagate to aid debugging
        }else{
          try {
            eval(value);
          }
          catch (ex) {
            alert("Error when evaluating returned javascript: " + ex.name + " message : " + ex.message + "\n"
              + " fileName: " + ex.fileName + " line: " + ex.lineNumber + "\n"
              + value);
            //console.debug("Error when evaluating returned JavaScript name: " + ex.name + " message: " + ex.message);
          }
        }
        var endJS = new Date();
        if (atgSubmitDebug) { console.debug("Rendering Javascript took " + (endJS - startJS));}
      }
      else
      {
        var element = dojo.byId(name);
        if (element)
        {
          if (element.value)
          {
            if (mode == "overwrite")
            {
              element.value = value;
            }
            else
            { // append
              var temp = element.value;
              temp += value;
              element.value = temp;
            }
          }
          else
          {
            var widget = dijit.byId(name);
            if (widget && widget["setContent"]){ // Dojo contentPane
              var startContent = new Date();
              //console.debug("Calling setContent on the contentPane widget named " + name);
              try {
                value = value.replace(/\\u/g, "&#92;u");
                widget.setContent(value);
              } catch(ex) {
                alert("Error when calling setContent for widget" + name +
                    " with returned HTML: " + ex.name + " message : " + ex.message + "\n"
                     + " fileName: " + ex.fileName + " line: " + ex.lineNumber + "\n");
                console.debug("Returned content with exception was: " + value);
              }
              var endContent = new Date();
              if (atgSubmitDebug) { console.debug("Calling setContent on " + name + " took " + (endContent - startContent));}
            }
            else { // regular div
              var startInner = new Date();
              //console.debug("Setting innerHtml on the element");
              var theChild;
              while(element.firstChild){
                if(dojo["event"]){
                  try{
                    dojo.clean(element.firstChild);
                  }catch(e){}
                }
                theChild = element.removeChild(element.firstChild);
                if (dojo.isIE) {discardElement(theChild);}
              }
              if (dojo.isIE) {element.innerHTML = ''; clearElements(); } // clear out IE's pseudo references
              element.innerHTML = value;
              // now create any widgets that are in the returned html
              dojo.parser.parse(element);
              var endInner = new Date();
              if (atgSubmitDebug) { console.debug("Rendering new inner html for " + name + " took " + (endInner - startInner));}
              var startScripts = new Date();
              var scripts = element.getElementsByTagName("script");
              for (var i = 0; i < scripts.length; i++) {
                if (djConfig.isDebug){
                  eval(scripts[i].innerHTML); // allow errors to propagate, to help debugging
                } else {
                  try {
                    eval(scripts[i].innerHTML);
                  }
                  catch (ex) { // JavaScript errors are of interest
                    alert("Error when evaluating returned javascript in HTML: " + ex.name + " message : " + ex.message + "\n"
                     + " fileName: " + ex.fileName + " line: " + ex.lineNumber + "\n"
                     + scripts[i].innerHTML);
                  }
                }
              }
              var endScripts = new Date();
              if (atgSubmitDebug) { console.debug("Handling scripts for " + name + " took " + (endScripts - startScripts));}
              if (atgSubmitDebug) { console.debug("Rendering innerHtml and scripts for " + name + " took " + (endScripts - startInner));}
            }
          }
        }
        else if (name == 'errorPanelContent') {
          // Let mb handle this case when it can
          var mb = dijit.byId("messageBar");
          if (!mb) {
            alert(value);  // it was an error, but we can't display it so be blunt about it
          }
        }
        else {
          // Element not found with name
          if (atgSubmitDebug) {console.debug("no element found with id " + name);}
        }
      }
    }
  }
  var endDate = new Date();
  if (atgSubmitDebug) {console.debug("Rendering the response took: " + (endDate - startDate));}
  /*Fix for CSC-168369, that corrects for an IE7 redraw issue with the grids*/
  if(dojo.isIE && dojo.isIE < 8){
    // set the scroll bar to the top of the contentColumn
    dojo.byId("contentColumn").scrollTop = 0;
    // 
    dijit.byId("column1").layout();
  }
  return response;
}

function atgSubmitPopup(params){
  var submitStart = new Date();
  if (params.form === undefined || params.form === null){
    alert("Action didn't pass a form in.  Params were " + params);
  }
  if (params.showLoadingCurtain !== false)
  {
    atgShowLoadingIcon();
  } else {
    atgLoadingCount++; // prevent the callback from decrementing the loading count below zero
  }
  if (params.url === undefined || params.url === null || params.url === '') {
    alert("You must pass in a URL");
  }
  if (params.popup === undefined || params.popup === null || params.popup === '') {
    alert("You must pass in Dialog widget reference");
  }
  if (params.preventCache === undefined || params.preventCache === null || params.preventCache === '') {
    params.preventCache = false;
  }

  var contentMap = {};
  if (params.queryParams !== undefined && params.queryParams !== null) {contentMap = params.queryParams;}
  contentMap["atg.formHandlerUseForwards"] = false;
  contentMap._windowid = window.windowId;
  if (window.requestid) { contentMap._requestid = window.requestid;}

  var deferred = dojo.xhrPost({
    url: params.url,
    content: contentMap,
    encoding: "utf-8",
    timeout: atgXhrTimeout,
    form: params.form,
    sync: false,
    preventCache: params.preventCache,
    handle: function(response, ioArgs){
        atgHideLoadingIcon();
        if (response instanceof Error) {
          if (response.dojoType == "cancel") {
            alert("Connection with the server was canceled");
          }
          else if (response.dojoType == "timeout") {
            alert("Connection with the server timed out");
          }
          else {
            console.log("An error has occurred handling a popup, " + response);
            alert('An error has occurred ' + response);
          }
        } else {
          atgDojoPopupHandleResponse(response, ioArgs, params.popup);
        }
        return response;
    }
  });
  var submitEnd = new Date();
  if (atgSubmitDebug) {console.debug("Submitting form took " + (submitEnd - submitStart) + "ms");}
  return deferred;
}

function atgDojoPopupHandleResponse(response, ioArgs, popup) {
  //popup.innerHTML = data;
  if (atgSubmitDebug) {console.debug("in atgDojoPopupHandleResponse with popup " + popup.id);}
  popup.setContent(response);
  atgHideLoadingIcon();
  return response;
}

function atgNavigate(params){
  if (params.form === undefined || params.form === null){
    params.form = dojo.byId("transformForm");
  }
  atgSubmitAction(params);
}
function frameworkCellAction(formId, cellId) {
	var theForm = document.getElementById(formId);
    atgSubmitAction({
    form: theForm,
	 	cell: cellId,
	 	sync: true
  });
}
function frameworkPanelAction(formId, panelId, panelStackId, nextStepsId, nextStepsPanelId) {
	var theForm = document.getElementById(formId);
	if (nextStepsId) {	  
    atgSubmitAction({
      form: theForm,
      nextSteps: nextStepsId,
      panels: [panelId,nextStepsPanelId],
      panelStack: panelStackId
    });
	} else {
    atgSubmitAction({
      form: theForm,
      panels: [panelId],
      panelStack: panelStackId
    });
	}
}
function frameworkMultiPanelAction(formId, panelIdArray) {
	var theForm = document.getElementById(formId);
  atgSubmitAction({
    form: theForm,
    panels: panelIdArray
  });
}
function frameworkPanelStackAction(formId, panelStackId) {
	var theForm = document.getElementById(formId);
  atgSubmitAction({
    form: theForm,
    panelStack: panelStackId
  });
}
function startNewCall(){
  console.debug("publishing startNewCallEvent");
  dojo.publish("startNewCallEvent");
  var theForm = document.getElementById("startCallForm");
  atgSubmitAction({form: theForm});
}
function endCurrentCall(){
  var theForm = document.getElementById("endCallForm");
  atgSubmitAction({
    form: theForm});
}
function endAndStartNewCall(){
  console.debug("publishing endAndStartNewCallEvent");
  dojo.publish("endAndStartNewCallEvent");
  var theForm = document.getElementById("endAndStartCallForm");
  atgSubmitAction({
    form: theForm  });
}
function respondEscalateTicket(){
var theForm = document.getElementById("escalateTicketForm");
theForm.reasonCode.value = window.reasonCode;
theForm.noteText.value = window.comment;
theForm.share.value = window.share;
theForm.escalationLevel.value = window.escalationLevel;
theForm.group.value = window.group;
atgSubmitAction({
  form: theForm,
  panels: ["nextStepsPanel","ticketActivityPanel"],
  nextSteps: "communicateNextSteps",
  panelStack: ["respondPanels","globalPanels"]
});
}

function respondCloseTicket(theFormId){
var theForm = document.getElementById(theFormId);
theForm.reasonCode.value = window.reasonCode;
theForm.ticketId.value = window.ticketId;
theForm.noteText.value = window.comment;
theForm.share.value = window.share;
atgSubmitAction({
  form: theForm,
  panels: ["nextStepsPanel","ticketActivityPanel"],
  nextSteps: "communicateNextSteps",
  panelStack: ["respondPanels","globalPanels"]
});
}

function respondReleaseTicket(){
  var theForm = document.getElementById("releaseTicketForm");
  theForm.reasonCode.value=window.reasonCode;
  theForm.noteText.value=window.comment;
  theForm.share.value=window.share;
  atgSubmitAction({
    form: theForm,
    panels: ["nextStepsPanel","ticketActivityPanel"],
    nextSteps: "communicateNextSteps",
    panelStack: ["respondPanels","globalPanels"]
  });
}

function respondDeferTicket(){
  var theForm = document.getElementById("deferTicketForm");
  theForm.reasonCode.value = window.reasonCode;
  theForm.noteText.value = window.comment;
  theForm.share.value = window.share;
  theForm.date.value = window.date;
  theForm.retain.value = window.retain;
  atgSubmitAction({
    form: theForm,
    panels: ["nextStepsPanel","ticketActivityPanel"],
    nextSteps: "communicateNextSteps",
    panelStack: ["respondPanels","globalPanels"]
  });
}

function respondReassignTicket(){
var theForm = document.getElementById("assignTicketToAgentForm");
theForm.reasonCode.value = window.reasonCode;
theForm.noteText.value= window.comment;
theForm.share.value=window.share;
theForm.reassignAgent.value=window.agent;
atgSubmitAction({
  form: theForm,
  panels: ["nextStepsPanel","ticketActivityPanel"],
  nextSteps: "communicateNextSteps",
  panelStack: ["respondPanels","globalPanels"]
  });
}

function respondSendTicket(){
  var theForm = document.getElementById('sendTicketForm');
  theForm.reasonCode.value=window.reasonCode;
  theForm.sendToGroup.value = window.sendToGroup;
  theForm.noteText.value=window.comment;
  theForm.share.value=window.share;
  atgSubmitAction({
    form: theForm,
    panels: ["nextStepsPanel","ticketActivityPanel"],
    nextSteps: "communicateNextSteps",
    panelStack: ["respondPanels","globalPanels"]
  });
}
var responseReloadExpected = false;

// Send email action
function sendEmail() {
  console.debug("sendEmail called");

  dojo.byId("htmlBody").value = FCKeditorAPI.GetInstance('RespondEditor').GetHTML(true);
  
  //BUGS-FIXED: 13514533 - IE8 AND 9 CRASHING IN CSC IN FCKEDITOR WHEN MAILING 
  //we changing focus to subject field to make sure focus is not on message text field. 
  // if message field is focused, ie8-9 can crash on submit action. 
  //this workaround can be removed when this issue will be not reproducible.
  document.getElementById("subject").focus();
 
  ResponseSaveState();

  var theForm = document.getElementById("sendEmailForm");
  copyEmailForm( {target: theForm} );

  // Don't save message after sending.
  responseReloadExpected = true;

  return atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler",
    form: theForm,
    nextSteps: "communicateNextSteps",
    panelStack: ["respondPanels","globalPanels"],
	  panels: ["nextStepsPanel", "ticketActivityPanel"],
	  sync: true
  });
}


// Send email and close ticket action
function sendEmailClose() {
  console.debug("sendEmailClose called");

  dojo.byId("htmlBody").value = FCKeditorAPI.GetInstance('RespondEditor').GetHTML(true);
  
  //BUGS-FIXED: 13514533 - IE8 AND 9 CRASHING IN CSC IN FCKEDITOR WHEN MAILING 
  //we changing focus to subject field to make sure focus is not on message text field. 
  // if message field is focused, ie8-9 can crash on submit action. 
  //this workaround can be removed when this issue will be not reproducible.
  document.getElementById("subject").focus();
  ResponseUpdateAllFormFields();

  // Don't save message after sending.
  responseReloadExpected = true;

  var theForm = document.getElementById("sendEmailCloseForm");
  copyEmailForm( {target: theForm} );

  return atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/SendAndCloseTicketFormHandler",
    form: theForm,
    nextSteps: "communicateNextSteps",
	panelStack: ["respondPanels","globalPanels"],
	panels: ["nextStepsPanel", "sideViewRecentTicketsPanel"]
  });
}

// Discard email action
function discardEmail() {
  console.debug("discardEmail called");

  ResponseAbandonedSession();

  atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler",
    form: document.getElementById("discardEmailForm"),
    nextSteps: "communicateNextSteps",
	panelStack: "respondPanels",
	panels: ["nextStepsPanel"],
	sync:true
  });
}


// Save email action
function saveEmail() {
  console.debug("saveEmail called");

  if (responseReloadExpected)
  {
    responseReloadExpected = false;
    return false;
  }

  var theForm = document.getElementById("saveEmailForm");
  if (theForm == null) {
    return false;
  }
  
  copyEmailForm( {target: theForm} );

  // Add the text only flag
  var text_only = document.getElementById("outMessageForm").textOnly;
  if ( text_only ) {
    theForm.textOnly.checked = text_only.checked;
  }

  return atgSubmitAction({
    form: theForm,
    sync:false
  })

}

// Called from "Insert Template" link
function insertTemplate() {
  console.debug("insertTemplate called");

  // Find the email template name
  var email_template = document.getElementById("emailTemplate");
  if ( email_template )
  {
	    dojo.xhrGet({
	        url:window.contextPath + "/include/response/template.jsp",
	        content:{templateName: email_template.value},
	        timeout: atgXhrTimeout,
	        load:function(response, ioArgs){
	          FCKeditorAPI.GetInstance('RespondEditor').SetHTML(response);
	        }
	      });
  }
}

function changeChannel(){
	  console.debug("changeChannel called");
	  var theForm = document.getElementById("changeChannelForm");
	  copyEmailForm( {target: theForm} );

	  dojo.xhrPost({
	    url: window.contextPath + "/include/response/responseErrors.jsp",
	    encoding: "utf-8",
	    content:{_windowid: window.windowId, _isppr:true},
	    timeout: atgXhrTimeout,
	    form: theForm,
	    load: function(response, ioArgs)
	    {
	        changeChannelUI(theForm.channelId.value);
	    },
	    sync: true
	  });
}

function responseToggleShow(toToggle, displayStyle)
{
  for(var i = 0; i < toToggle.length; i++)
  {
    dojo.byId(toToggle[i]).style.display = displayStyle;
  }
}

// Refresh attachments area
function attachmentRefresh() {
  console.debug("attachmentRefresh called");

  var theForm = document.getElementById("attachmentRefreshForm");
  atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler",
    form: theForm,
    nextSteps: "communicateNextSteps",
	panels: ["nextStepsPanel"],
	sync: true
  });
}

// Action called to Add Attachment from Content Browser.
function addSystemAttachment(){
  console.debug("addSystemAttachment called");

  var theForm = document.getElementById("addSystemAttachmentForm");
  copyEmailForm( {target: theForm} );

  atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler",
    form: theForm,
    nextSteps: "communicateNextSteps",
    panels: ["nextStepsPanel"],
    sync: true
  });
}

// Action to remove an attachment from the outbound message
function removeAttachment() {
  console.debug("removeAttachment called");

  atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler",
    form: document.getElementById("removeAttachmentForm"),
    nextSteps: "communicateNextSteps",
    panels: ["nextStepsPanel"],
    sync: true
  });
}

// Content Browser Content Details
function contentBrowserContentDetails() {
  console.debug("contentBrowserContentDetails called");

  atgSubmitAction({
    formHandler: "/atg/arm/ui/content/ContentBrowserFormHandler",
    form: document.getElementById("contentBrowserContentDetailsForm"),
    sync: true
  });
}

//
//  Must supply at least a target form to copy to.  Default source form is the
//  'outMessageForm'.
//
//  params is a map and can contain the following -
//    source: Form to copy from, defaults to 'outMessageForm'
//    target: Form to copy to
//    list: Array of elements to copy, defaults to ["htmlBody", "textBody", "subject", "to", "cc", "bcc", "textOnly", "channelId"]
//
function copyEmailForm(params) {
  console.debug("copyEmailForm called");
  if (typeof params.source == undefined || params.source == null) {
    params.source = document.getElementById("outMessageForm");
    
    //In the event that this function is called out of scope (such as after a panel unload), 
    //provide a way to gracefully exit.
    if (params.source == null) {
      return false;
    }
  }
  if (typeof params.list == undefined || params.list == null) {
    params.list = ["htmlBody", "textBody", "subject", "to", "cc", "bcc", "textOnly", "channelId", "ticketId"];
  }

  for (var formElement=0; formElement<params.list.length; formElement++) {
    //console.debug("copyEmailForm ["+params.list[formElement]+"]");
    if ( params.source.elements[params.list[formElement]] &&
         params.target.elements[params.list[formElement]] ) {
      //console.debug("copyEmailForm ["+params.source.elements[params.list[formElement]].value+"]");
      params.target.elements[params.list[formElement]].value = params.source.elements[params.list[formElement]].value;
    }
  }
}function viewRecentTickets() {
  var theForm = document.getElementById("viewRecentTicketsForm");
  if (theForm) {
	  theForm.type.value = window.ticketing.ticketAccessType;
	  atgSubmitAction({
	    form: theForm,
	    panels: ["sideViewRecentTicketsPanel"]
	  });
	}
}

function ticketsTab(){
atgChangeTab(atg.service.framework.changeTab('ticketsTab'),"ticketSearchNextSteps","ticketSearchPanels",["nextStepsPanel"]);
}

function viewTicket(theFormId) {
var theForm = document.getElementById(theFormId);
//assumes the ticketId field in the form has been set
atgSubmitAction({
  form: theForm,
  nextSteps: "ticketViewNextSteps",
  panelStack: ["ticketPanels"],
  tab: atg.service.framework.changeTab('ticketsTab')
});
}

function globalViewTicket(theTicketId) {
    var theForm = document.getElementById('globalViewTicketForm');
    theForm.ticketId.value = theTicketId;
    viewTicket('globalViewTicketForm');
}

function createTicket(theFormId){
  var theForm = document.getElementById(theFormId);
  atgSubmitAction({form: theForm});
  }


function globalSaveTicket(theFormId){
	   saveTicket(theFormId);
	   
	   /*
	   var theForm = document.getElementById(theFormId);
	   // If we are on the ticket tab, we need to emulate the saveTicket next step
	   //
	   var tab = document.getElementById("ticketsTab");
	   if (tab.className != "" && tab.className == "current") {
	     saveTicket(theFormId);
	     return;
	   }
	   atgSubmitAction({
	     form: theForm,
	     panels: ["sideViewRecentTicketsPanel","ticketDetailErrorPanel","ticketCustomerInformationPanel",
	     "ticketSummaryPanel","ticketActivityPanel", "nextStepsPanel"],
	     panelStack: ["globalPanels"]
	   });
	   */
	 }

	 function saveTicket(theFormId){
	   		 
	 var theForm = document.getElementById(theFormId);

	 var theTicketForm = document.getElementById("ticketForm") ;
	 if(theTicketForm == null){
	 atgSubmitAction({
	   form: theForm,
	   nextSteps: "ticketViewNextSteps",
	   panelStack: ["ticketPanels","globalPanels"],
	   tab: atg.service.framework.changeTab('ticketsTab')
	 });
	 }else{
		 atgSubmitAction({
			    form: theForm,
			    panels: ["nextStepsPanel","ticketSummaryPanel","sideViewRecentTicketsPanel"],
			    panelStack: ["globalPanels"],
			    nextSteps: "ticketViewNextSteps",
			    formHandler: "/atg/svc/ui/formhandlers/TicketingFormHandler",
			    extraParams: {
			      //status: theTicketForm.statusSelect.value,
			      priority: theTicketForm.prioritySelect.value,
			      ticketQueue: theTicketForm.ticketQueueSelect.value,
			      description: theTicketForm.description.value,
			      customerDetails_firstName: theTicketForm.firstName.value,
			      customerDetails_lastName: theTicketForm.lastName.value,
			      customerDetails_phone: theTicketForm.phone.value,
			      customerDetails_email: theTicketForm.email.value,
			      customerDetails_address: theTicketForm.address.value,
			      customerDetails_city: theTicketForm.city.value,
			      customerDetails_state: theTicketForm.state.value,
			      customerDetails_country: theTicketForm.country.value,
			      customerDetails_postalCode: theTicketForm.postalCode.value
			    }
			  });
			}  	       
	 }

function saveTicketForm() {
  atgSubmitAction({
    panels: ["nextStepsPanel","sideViewRecentTicketsPanel","ticketDetailErrorPanel","ticketCustomerInformationPanel",
    "ticketSummaryPanel","ticketActivityPanel"],
    nextSteps: "ticketViewNextSteps",
    panelStack: ["ticketPanels","globalPanels"],
    tab: atg.service.framework.changeTab('ticketsTab'),
    form:"saveTicketForm"
  });
}
function saveTicketCaptureClaimSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "captureClaimSteps",
  panels: ["nextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"]
});
}

function saveTicketCaptureBeforeSaveNextSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "captureBeforeSaveNextSteps",
  panels: ["nextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"]
});
}

function saveTicketCommunicateNextSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "communicateNextSteps",
  panels: ["nextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["respondPanels","globalPanels"]
});
}

function saveTicketReportsNextSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "reportsNextSteps",
  panels: ["nextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"]
});
}

function saveTicketSearchNextSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "searchNextSteps",
  panels: ["nextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"]
});
}

function saveTicketTasksNextSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "tasksNextSteps",
  panels: ["nextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"]
});
}

function saveTicketViewDocumentNextSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "viewDocumentNextSteps",
  panels: ["nextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"]
});
}

function saveTicketViewSolutionNextSteps(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  nextSteps: "viewSolutionNextSteps",
  panels: ["knowledgeNextStepsPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"]
});
}

function escalateTicket(){
var theForm = document.getElementById("escalateTicketForm");
theForm.reasonCode.value = window.reasonCode;
theForm.noteText.value = window.comment;
theForm.share.value = window.share;
theForm.escalationLevel.value = window.escalationLevel;
theForm.group.value = window.group;
atgSubmitAction({
  form: theForm,
  nextSteps: "ticketViewNextSteps",
  panelStack: ["ticketPanels","globalPanels"],
  tab: atg.service.framework.changeTab('ticketsTab')
});
}

function closeTicket(theFormId){
var theForm = document.getElementById(theFormId);
theForm.reasonCode.value = window.reasonCode;
theForm.ticketId.value = window.ticketId;
theForm.noteText.value = window.comment;
theForm.share.value = window.share;
atgSubmitAction({
  form: theForm,
  nextSteps: "ticketViewNextSteps",
  panelStack: ["ticketPanels","globalPanels"],
  tab: atg.service.framework.changeTab('ticketsTab')
});
}

function releaseTicket(){
  var theForm = document.getElementById("releaseTicketForm");
  theForm.reasonCode.value=window.reasonCode;
  theForm.noteText.value=window.comment;
  theForm.share.value=window.share;
  atgSubmitAction({
    form: theForm,
    nextSteps: "ticketViewNextSteps",
    panelStack: ["ticketPanels","globalPanels"],
    tab: atg.service.framework.changeTab('ticketsTab')
  });
}

function deferTicket(){
  var theForm = document.getElementById("deferTicketForm");
  theForm.reasonCode.value = window.reasonCode;
  theForm.noteText.value = window.comment;
  theForm.share.value = window.share;
  theForm.date.value = window.date;
  theForm.retain.value = window.retain;
  atgSubmitAction({
    form: theForm,
    nextSteps: "ticketViewNextSteps",
    panelStack: ["ticketPanels","globalPanels"],
    tab: atg.service.framework.changeTab('ticketsTab')
  });
}

function reassignTicket(){
var theForm = document.getElementById("assignTicketToAgentForm");
theForm.reasonCode.value = window.reasonCode;
theForm.noteText.value= window.comment;
theForm.share.value=window.share;
theForm.reassignAgent.value=window.agent;
atgSubmitAction({
  form: theForm,
  nextSteps: "ticketViewNextSteps",
  panelStack: ["ticketPanels","globalPanels"],
  tab: atg.service.framework.changeTab('ticketsTab')
  });
}

function sendTicket(){
  var theForm = document.getElementById('sendTicketForm');
  theForm.reasonCode.value=window.reasonCode;
  theForm.sendToGroup.value = window.sendToGroup;
  theForm.noteText.value=window.comment;
  theForm.share.value=window.share;
  atgSubmitAction({
    form: theForm,
    nextSteps: "ticketViewNextSteps",
    panelStack: ["ticketPanels","globalPanels"],
    tab: atg.service.framework.changeTab('ticketsTab')
  });
}

function workTicket(theFormId, ticketId){
var theForm = document.getElementById(theFormId);
theForm.ticketId.value=ticketId;
window.ticketId=ticketId;
atgSubmitAction({
  form: theForm,
  nextSteps: "ticketViewNextSteps",
  panelStack: ["ticketPanels","globalPanels"],
  tab: atg.service.framework.changeTab('ticketsTab')
  });
}

function workActiveTicket(theFormId,ticketId){
  var theForm = document.getElementById(theFormId);
  theForm.ticketId.value=ticketId;

  if(atg.service.ticketing.isActiveTicketTransient)
    theForm.doDispositionPrompt.value="true";
  else
    theForm.doDispositionPrompt.value="false";
  window.ticketId=ticketId;

  //Determine whether we are on the respond tab, so that we may add it to the panel stack
  if (dojo.byId("respondPanels_communicateLinkedSolutionsPanel_1") != null){
    //publish the ticket change topic
    dojo.publish("/agent/ticketChange");
    dojo.destroy("/agent/ticketChange");
    atgSubmitAction({
      form: theForm,
      panels: ["nextStepsPanel","sideViewRecentTicketsPanel","ticketCustomerInformationPanel",
        "ticketSummaryPanel","ticketActivityPanel"],
      panelStack: ["respondPanels","globalPanels"]
    });
  } else {
    atgSubmitAction({
      form: theForm,
      nextSteps: "ticketViewNextSteps",
      panelStack: ["ticketPanels","globalPanels"],
      tab: atg.service.framework.changeTab('ticketsTab')
    });
  }

}

function ticketHistoryWorkTicket(theFormId,ticketId){
  var theForm = dojo.byId(theFormId);
  theForm.ticketId.value = ticketId;
  atgSubmitAction({
    form : theForm,
    nextSteps: "ticketViewNextSteps",
    panelStack: ["ticketPanels","globalPanels"],
    tab: atg.service.framework.changeTab('ticketsTab')
  });
};


function ticketHistoryViewTicket(theFormId,ticketId){
  var theForm = document.getElementById(theFormId);
  theForm.ticketId.value=ticketId;
  window.ticketId=ticketId;
  atgSubmitAction({
    form: theForm,
    nextSteps: "ticketViewNextSteps",
    panelStack: ["ticketPanels","globalPanels"],
    tab: atg.service.framework.changeTab('ticketsTab')
  });
}


function reopenTicket(theFormId,ticketId){
var theForm = document.getElementById(theFormId);
theForm.ticketId.value=ticketId;
atgSubmitAction({
  form: theForm,
  nextSteps: "ticketViewNextSteps",
  panelStack: ["ticketPanels","globalPanels"],
  tab: atg.service.framework.changeTab('ticketsTab')
});
}

function backToTicketSearch(){
var theForm=document.getElementById("backToTicketSearchForm");
atgSubmitAction({
  form: theForm,
  panels: ["nextStepsPanel"],
  panelStack: "ticketSearchPanels",
  nextSteps: "ticketSearchNextSteps",
  tab: atg.service.framework.changeTab('ticketsTab')
});
}

function beginEditTicket(){
atgSubmitAction({
  form: document.getElementById("beginEditTicketForm"),
  panels: ["nextStepsPanel","ticketSummaryPanel"],
  nextSteps: "ticketViewNextSteps"
});
}

function endEditTicket() {
  var theTicketForm = document.getElementById("ticketForm");
  var description = theTicketForm.description.value;
  var editForm = document.getElementById("endEditTicketForm");
  editForm.description.value =  escapeXML(description);
atgSubmitAction({
  form: editForm,
  panels: ["nextStepsPanel","ticketSummaryPanel","sideViewRecentTicketsPanel"],
  panelStack: ["globalPanels"],
  nextSteps: "ticketViewNextSteps",
  formHandler: "/atg/svc/ui/formhandlers/TicketingFormHandler",
  extraParams: {
    //status: theTicketForm.statusSelect.value,
    priority: theTicketForm.prioritySelect.value,
    ticketQueue: theTicketForm.ticketQueueSelect.value,
    customerDetails_firstName: theTicketForm.firstName.value,
    customerDetails_lastName: theTicketForm.lastName.value,
    customerDetails_phone: theTicketForm.phone.value,
    customerDetails_email: theTicketForm.email.value,
    customerDetails_address: theTicketForm.address.value,
    customerDetails_city: theTicketForm.city.value,
    customerDetails_state: theTicketForm.state.value,
    customerDetails_country: theTicketForm.country.value,
    customerDetails_postalCode: theTicketForm.postalCode.value
  }
});
}

function addTicketNote(formId, isViewTicketNote){
var theForm = document.getElementById(formId);
theForm.noteText.value=window.noteText;
theForm.share.value=window.isPublic;
theForm.viewTicketNote.value=isViewTicketNote;
atgSubmitAction({
  form: theForm,
  nextSteps: "ticketViewNextSteps",
  panelStack: ["ticketPanels","globalPanels"]
  });
}

function globalAddTicketNote(theFormId){
var theForm = document.getElementById(theFormId);
theForm.noteText.value=window.noteText;
theForm.share.value=window.isPublic;
theForm.noteType.value=window.noteType;
theForm.inbound.value=window.inbound;
atgSubmitAction({
  form: theForm,
  panels: ["ticketActivityPanel"],
  panelStack: ["globalPanels"]
});
}

function associateTicket() {
var theForm = document.getElementById('associateTicketForm');
theForm.ticketId.value=window.ticketId;
atgSubmitAction({
  form: theForm,
  panels: ["nextStepsPanel"],
  nextSteps: "ticketViewNextSteps",
  panelStack: "ticketPanels"
  });
}

function mergeTicket(){
var theForm = document.getElementById('mergeTicketForm');
theForm.ticketId.value=window.ticketId;
atgSubmitAction({
  form: theForm,
  panels: ["nextStepsPanel","ticketCustomerInformationPanel","ticketSummaryPanel","ticketActivityPanel"],
  nextSteps: "ticketViewNextSteps",
  panelStack: "ticketPanels"
});
}

function quickSearchTicket() {
  var quickSearchForm                       = document.getElementById("ticketSearchForm");
  quickSearchForm["parameterMap.id"].value  = document.getElementById("quickViewTicketTextEntry").value;
  atgSubmitAction({
    nextSteps: "ticketSearchNextSteps",
    panels: ["nextStepsPanel"],
    dynamicIncludes: "include/ticketSearchResults.jsp",
    form: quickSearchForm
  });
}

function advancedSearchTicket(){
var theForm = document.getElementById('advsearchform');
atgSubmitAction({
  form: theForm,
  formHandler: "/atg/svc/ui/formhandlers/TicketSearchFormHandler",
  nextSteps: "ticketSearchNextSteps",
  panels: ["nextStepsPanel"],
  dynamicIncludes: "include/ticketSearchResults.jsp",
  extraParams: {
    subStatus_subStatusName: theForm.statusSelect.value,
    owningAgentId: theForm.agentSelect.value,
    ticketQueue_id: theForm.ticketQueueSelect.value,
    escalationLevel: theForm.escalationSelect.value,
    customerDetails_firstName: theForm.firstName.value,
    customerDetails_lastName: theForm.lastName.value,
    customerDetails_phone: theForm.phone.value,
    customerDetails_email: theForm.email.value,
    description: theForm.description.value,
    dates_byCreatedDate: theForm.byCreatedDate.checked,
    dates_byLastModified: theForm.byLastModified.checked,
    dates_pastOrFromTo: document.pastOrFromTo,
    dates_past: theForm.past.value,
    dates_fromDate: theForm.fromDate.value,
    dates_toDate: theForm.toDate.value,
    dates_pastOrFromTo2: document.pastOrFromTo2,
    dates_past2: theForm.past2.value,
    dates_modifiedFrom: theForm.modifiedFrom.value,
    dates_modifiedTo: theForm.modifiedTo.value
  }
  });
}

function ticketSearchTreeTableAction(operation,parameters,state){
  var theForm = document.getElementById("ticketSearchResultsTableForm");
  atgSetupTreeTable(theForm, operation, parameters, state);
  atgSubmitAction({
    form: theForm,
    //panels: ["ticketSearchResultsPanel"],
    dynamicIncludes: "include/ticketSearchResults.jsp"
  });
}

function agentSearchTicket(operation,parameters,state,isPolling) {
/*  var theForm = document.getElementById("refreshForm");
  if (theForm){
    atgSetupTreeTable(theForm, operation, parameters, state);
    atgSubmitAction({
      form: theForm,
      dynamicIncludes: "include/agentTicketsSearchResults.jsp",
      showLoadingCurtain: (isPolling ? false : true)
    });
  }
*/
}

function activeTicketsSearch(operation,parameters,state,isPolling){
/*  var theForm = document.getElementById("activeTicketsSearchForm");
  // this action may be called when the form is not present
  // as it is called during automated polling.
  if (theForm){
    atgSetupTreeTable(theForm, operation, parameters, state);
    atgSubmitAction({
      form: theForm,
      dynamicIncludes: "panels/ticketing/activeTicketsSearchResults.jsp",
      showLoadingCurtain: (isPolling ? false : true)
    });
  }
*/
}

function agentSearchAllTickets(operation,parameters,state) {
  var theForm = document.getElementById("searchAllTicketForm");
  atgSetupTreeTable(theForm, operation, parameters, state);
  atgSubmitAction({
    dynamicIncludes: "/include/agentAllTicketsSearchResults.jsp",
    form: theForm,
    panels: ["ticketAllTicketPanel"]
  });
}

function printTicket(theFormId){
var theForm = document.getElementById(theFormId);
atgSubmitAction({
  form: theForm,
  dynamicIncludes: ["include/ticketing/printTicket.jsp"]
});
}

// Function to display active tickets in a popup
function showActiveTicketsPopup() {
  showPopupWithResults({
                  popupPaneId: "activeTicketsPopup",
                  title: getResource("popup.activeTickets.title"),
                  url: "/agent/panels/activeTicketsMainPanel.jsp"
                  });
}

dojo.addOnLoad(function () {
  if (!dijit.byId("activeTicketsPopup")) {
    new dojox.Dialog({ id: "activeTicketsPopup",
                       cacheContent: "false",
                       executeScripts: "true",
                       scriptHasHooks: "true",
                       duration: 100});
  }
});
function ticketHistoryList(operation, parameters,state){
  var theForm = document.getElementById("ticketHistoryListForm");
  atgSetupTreeTable(theForm, operation, parameters, state);
  if (document.getElementById('searchForm')) {theForm.id.value = document.getElementById('searchForm').idfield.value;}
  if (document.filter) {theForm.status.value=document.filter.ticketStatus.value;}
  atgSubmitAction({
    form: theForm,
    formHandler: "/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler",
    dynamicIncludes: ["include/ticketHistoryResults.jsp"],
    panels: ["ticketMyTicketPanel"]
  });
}

function ticketActivityDetail(theFormId){
  atgSubmitAction({
    form: document.getElementById(theFormId),
    formHandler: "/atg/svc/ui/formhandlers/ActivityDetailFormHandler"
  });
}

function mainTicketActivityList(operation, parameters,state){
  var theForm = document.getElementById("mainTicketActivityListForm");
  atgSetupTreeTable(theForm, operation, parameters, state);
  if (document.getElementById('mainActivityFilterForm')) {
    if (document.getElementById('mainActivityFilterForm').mainActivityTypeInput) {
      theForm.activityType.value = document.getElementById('mainActivityFilterForm').mainActivityTypeInput.value;
    }
  }
  atgSubmitAction({
    form: theForm,
    formHandler: "/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler",
    dynamicIncludes: ["include/mainTicketActivityListResults.jsp"]
  });
}

function ticketActivityList(operation, parameters,state){
  var theForm = document.getElementById("altTicketActivityListForm");
  atgSetupTreeTable(theForm, operation, parameters, state);
  if (document.getElementById('altActivityFilterForm')){
    if (document.getElementById('altActivityFilterForm').altActivityTypeInput) {
      theForm.activityType.value=document.getElementById('altActivityFilterForm').altActivityTypeInput.value;
    }
  }
  atgSubmitAction({
    form: theForm,
    formHandler: "/atg/svc/ui/formhandlers/TicketActivityListFormHandler",
    dynamicIncludes:["include/ticketActivityListResults.jsp"]
  });
}
function restoreGeneralPanel(){
var theForm = document.getElementById("restoreGeneralPanelForm");
atgSubmitAction({
  form: theForm
});
}

function saveUserPasswordAction(){
  var theForm = document.getElementById("saveUserPasswordForm");
  theForm.newPassword.value=getOption('passwordNew');
  theForm.oldPassword.value=getOption('password');
  theForm.confirmPassword.value=getOption('passwordConfirm');
  atgSubmitAction({
    form: theForm,
    url: window.contextPath + "/include/passwordChanged.jsp"
  });
  theForm.newPassword.value="";
}

var currPrefHolder = new currentPreferencesHolder();

function currentPreferencesHolder(){
  //hot solution classes is't listed
  //it is a bit redundant to store 2 array of properties names. Some properties are a bit different (one has prefix 'hid' another hasn't).
  //Prefix could be removed with regex but we have property with name differs from form property name.
  var propertyNames = 
    new Array('hidTrylogOut','hidAgentUserDefaultHomeTab');
  var correspondFormProps = 
    new Array('TrylogOut','AgentUserDefaultHomeTab');
  var _holder = new Object;
  this.saveCurrentPreferences = 
    function(){
      var len = propertyNames.length;
      for (var i = 0; i < len; ++i){
        _holder[propertyNames[i]] = getOption(propertyNames[i]);
      }
    };
  this.arePreferencesChanged = 
    function(lastProperties){
      var isSame = true;
      var len = propertyNames.length;
      for (var i = 0; (i < len) && isSame; ++i){
        isSame = 
          ((_holder[propertyNames[i]] == null && lastProperties[correspondFormProps[i]].value == "") ||
           (_holder[propertyNames[i]] == lastProperties[correspondFormProps[i]].value));
      }
      return !isSame;
    };
};

function saveUserPreferences(theFormId){
  var theForm = document.getElementById(theFormId);
  theForm.TrylogOut.value=getOption('hidTrylogOut');
  theForm.AgentUserDefaultHomeTab.value=getOption('hidAgentUserDefaultHomeTab');

  if (currPrefHolder.arePreferencesChanged(theForm)){
    atgShowLoadingIcon();
    atgSubmitAction({
      form: theForm,
      nextSteps: "userPreferencesNextSteps",
      panels: ["nextStepsPanel"],
      panelStack: ["preferencesPanels","helpfulPanels"],
      showLoadingCurtain: false,
      sync: true
    }).addCallback(currPrefHolder.saveCurrentPreferences);
    atgHideLoadingIcon();
  }
}

function radioAllClassesOnClick() {
  //Enable button 'OK' when All classes selected
  var theOkButton = document.getElementById("okSolutionClasses");
  var theRadioAllClasses = document.getElementById('allSolutionClasses');
  if (theOkButton && theRadioAllClasses && theRadioAllClasses.checked) theOkButton.disabled = false;
}

/*
  Custom Dojo Fixes that depend on other Agent application javascript customizations.
  If the Dojo fix can apply directly to the core level dojo, it belongs in the WebUI dojo-fixes.js

*/

/* 
--------------------------------------------------------------------
This variable determines whether to output all the Agent Dojo Fixes 
debug statements to Firebug. It is extremely useful when trying to debug 
grid issues, but may overwhelm the console in cases where these
debug statements are not necessary
-------------------------------------------------------------------- 
*/

var isAgentDojoFixesDebug = false;

consoleDebugAgentDojoFixes = function(_debugContents) {
  if (isAgentDojoFixesDebug) {
    console.debug(_debugContents);
  }
}


/* 
--------------------------------------------------------------------
Grid AutoHeight Functionality 
-------------------------------------------------------------------- 
*/


/* 
--------------------------------------------------------------------
Grid Rows
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.rows");
dojo.extend(dojox.grid.rows, {
   defaultRowHeight: 1, // lines 
   overRow: -2, 

   // metrics 
   getHeight: function(inRowIndex){ 
		return ''; 
   }, 
  

   getDefaultHeightPx: function(){ 
     consoleDebugAgentDojoFixes("Grid Rows: getDefaultHeightPx()");
     // summmary: 
     // retrieves the default row height 
     // returns: int, default row height 
    
      consoleDebugAgentDojoFixes("Grid Rows: getDefaultHeightPx() | grid.contentPixelToEmRatio = (): " + this.grid.contentPixelToEmRatio);
    
     // If the contentPixelToEmRatio value is null, assume that the value is 32
     if (this.grid.contentPixelToEmRatio == null) {
       return 32;
     }
    
     return Math.round(this.defaultRowHeight * this.linesToEms * this.grid.contentPixelToEmRatio); 
   }

});

/* 
--------------------------------------------------------------------
Grid View
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.view");
dojo.extend(dojox.GridView, {
	resizeHeight: function(){
	  consoleDebugAgentDojoFixes("Grid View: resizeHeight(): " + this.domNode.clientHeight);
		if(!this.grid._autoHeight){
			var h = this.domNode.clientHeight;
			consoleDebugAgentDojoFixes("Grid View: resizeHeight() | clientHeight = " + h);
			consoleDebugAgentDojoFixes("Grid View: resizeHeight() | scrollbar width = " + dojox.grid.getScrollbarWidth());
			
			if(!this.hasScrollbar()){ // no scrollbar is rendered
				h -= dojox.grid.getScrollbarWidth();
			}
			dojox.grid.setStyleHeightPx(this.scrollboxNode, h);
		}
	},
	
	
	renderRow: function(inRowIndex){
	  consoleDebugAgentDojoFixes("Grid View: renderRow()");
		var rowNode = this.createRowNode(inRowIndex);
		this.buildRow(inRowIndex, rowNode);
		this.grid.edit.restore(this, inRowIndex);
		return rowNode;
	},
	
	updateRow: function(inRowIndex){
	  consoleDebugAgentDojoFixes("Grid View: updateRow()");
		var rowNode = this.getRowNode(inRowIndex);
		if(rowNode){
			rowNode.style.height = '';
			this.buildRow(inRowIndex, rowNode);
		}
		return rowNode;
	}
	
});


/* 
--------------------------------------------------------------------
Grid Views
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.views");
dojo.extend(dojox.grid.views, {

	updateRow: function(inRowIndex){
		consoleDebugAgentDojoFixes("Grid Views: updateRow()");
		consoleDebugAgentDojoFixes("Grid Views: view = " + this.views[i]);
		for(var i=0, v; v=this.views[i]; i++){
			v.updateRow(inRowIndex);
		}
		this.renormalizeRow(inRowIndex);
		
	}
});


/* 
--------------------------------------------------------------------
Grid Scroller
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.scroller");
dojo.extend(dojox.grid.scroller, {
	
	defaultRowHeight: 45,
	averageRowHeight: 45, // the average height of a row, preset to the default
	// rendering implementation
	renderPage: function(inPageIndex){
		var nodes = [];
		consoleDebugAgentDojoFixes("Grid Scroller: colCount = " + this.colCount);
		for(var i=0; i<this.colCount; i++){
			nodes[i] = this.pageNodes[i][inPageIndex];
		}

    consoleDebugAgentDojoFixes("Grid Scroller: rowsPerPage = " + this.rowsPerPage);
    consoleDebugAgentDojoFixes("Grid Scroller: rowCount = " + this.rowCount);
    consoleDebugAgentDojoFixes("Grid Scroller: inPageIndex*this.rowsPerPage = " + inPageIndex*this.rowsPerPage);
		for(var i=0, j=inPageIndex*this.rowsPerPage; (i<this.rowsPerPage)&&(j<this.rowCount); i++, j++){
			this.renderRow(j, nodes);
		}
		
	},
	
	calculateAverageRowHeight: function(){
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight()");
		// Calculate the average row height and update the defaults (row and page). 
		if (!((this.page == 0) && (this.pageTop == 0))) {
		  this.needPage(this.page, this.pageTop); 
		}
    var rowsOnPage = 0;    
    if(this.page < this.pageCount - 1 || (this.rowCount % this.rowsPerPage) == 0 ){
      rowsOnPage = this.rowsPerPage;
    }else{
      rowsOnPage = (this.rowCount % this.rowsPerPage);
    }
		
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | rowsOnPage = " + rowsOnPage);
		var pageHeight = this.getPageHeight(this.page);
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | pageHeight = " + pageHeight);
		pageHeight += 15;
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | adding 15 to pageHeight");
		this.averageRowHeight = (pageHeight > 0 && rowsOnPage > 0) ? (pageHeight / rowsOnPage) : 0; 
		consoleDebugAgentDojoFixes("Grid Scroller: averageRowHeight = " + this.averageRowHeight);
		
		// Insert if statement to test for valid Scroller
		if (dojox.grid._Scroller) {
		  consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | scroller detected");
		  this.defaultRowHeight = this.averageRowHeight || dojox.grid._Scroller.prototype.defaultRowHeight; 
		  this.defaultPageHeight = this.defaultRowHeight * this.rowsPerPage;
		  consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | defaultRowHeight = " + this.defaultRowHeight + ", defaultPageHeight = " + this.defaultPageHeight);
		}
	}
	
});


/* 
--------------------------------------------------------------------
Virtual Grid
--------------------------------------------------------------------
*/

dojo.require("dojox.grid.VirtualGrid");
dojo.extend(dojox.VirtualGrid, {
	
	showHorizontalScrollbar: false,    // Set to true in order to always show the horizontal scrollbar at the bottom of the grid
	autoHeight: "8",                   // number | boolean  Set to the max desired rows to show at a time or true to grow to fit container

	_perRowPaddingAdjustment: 6,       // Per row Adjustment to correct auto measurement inaccuracies
	_scrollPaddingAdjustment: 55,      // total amount of padding to calibrate the full scroll properly
	_heightPaddingAdjustment: 16,      // needed to account for horizontal scroll measurement calibration
	
  // sizing
  resize: function(){
    consoleDebugAgentDojoFixes("Virtual Grid: resize() | autoheight = " + this.autoHeight);
    // summary:
    //    Update the grid's rendering dimensions and resize it
    
    // if we have set up everything except the DOM, we cannot resize
    if(!this.domNode || !this.domNode.parentNode){
      return;
    }
    
    if(!this.domNode.parentNode || this.domNode.parentNode.nodeType != 1){
      return;
    }
    
    // useful measurement
    var padBorder = dojo._getPadBorderExtents(this.domNode);

    if ((this.model != null) && (this.model.count != null)) {
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | testing model :" + this.model);
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | model.count :" + this.model.count);
      // Whether or not to hide the horizontal scrollbar
      if(this.model.count > 0){
        if((typeof(this.scroller.contentNodes) != undefined) && (this.scroller.contentNodes != null)){
          this.scroller.contentNodes[0].parentNode.style.overflowX="hidden";
        }
      }
    
      // if the autoHeight is set to true, then grow as big as it needs to be
      if( (typeof this.autoHeight != "number") && this.autoHeight && this.model.count > 0){
        this.autoHeight = this.model.count;
        consoleDebugAgentDojoFixes("Virtual Grid: resize() | calculated autoHeight :" + this.autoHeight);
      }  
    }
    
    if( (this.autoHeight == 'true' || this.autoHeight == true )&&  this.domNode.parentNode.style.height != ''){
      this.autoHeight = false;
      this._autoHeight = false;
    }
  
    if(this.autoHeight == 'true' || this.autoHeight == true){
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | autoHeight set to true");
      console.info("Virtual Grid: resize() | autoHeight set to true, testing for height");
      if (this.domNode.clientHeight <= padBorder.h) {
        console.info("Virtual Grid: resize() | switching autoHeight from true to false");
        // If the height of the container is smaller than the calculated height,
        // set the height based on the container
        t = 0;
        this.scroller.calculateAverageRowHeight();
        this.domNode.style.height = 'auto';
        this.viewsNode.style.height = '';
      
        t += (this.scroller.averageRowHeight * this.autoHeight); 
        // add a padding buffer for scrollbars
        t += this._scrollPaddingAdjustment;
        this.domNode.style.height = t + "px";
      }
      else {
        // grid height mode selection 
        console.info("Virtual Grid: resize() | keeping autoHeight set to true");
        this.domNode.style.height = 'auto';
        this.viewsNode.style.height = '';
      }
    }
    else if(typeof this.autoHeight == "number" ){ 
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | autoHeight set to a number");
       t =  this.views.measureHeader();
      // add the padding amount adjustment
      t += this._heightPaddingAdjustment;
      this.scroller.calculateAverageRowHeight();
      this.domNode.style.height = 'auto';
      this.viewsNode.style.height = '';
      // Check to see if there are enough rows to render the full requested height      
      if ((this.model) && (this.autoHeight > this.model.count)){
        t += ((this.scroller.averageRowHeight + this._perRowPaddingAdjustment) * this.model.count);          
      }
      else{
        t += ((this.scroller.averageRowHeight + this._perRowPaddingAdjustment) * this.autoHeight);   
      }
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | final height :" + t + "px");
      // set the final height
      this.domNode.style.height = t + "px";
    }
    else if(this.flex > 0){
    }
    else if(this.domNode.clientHeight <= padBorder.h){
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | this.domNode.clientHeight less then padBorder.h");
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | padBorder.h = " + padBorder.h);
      if(this.domNode.parentNode == document.body){
        this.domNode.style.height = this.defaultHeight;
      }else{
        this.fitTo = "parent";
      }
    }
    else {
      consoleDebugAgentDojoFixes("Virtual Grid: autoHeight is not a number and not set to true");
      t = 0;
      this.scroller.calculateAverageRowHeight();
      this.domNode.style.height = 'auto';
      this.viewsNode.style.height = '';
    
      t += (this.scroller.averageRowHeight * this.autoHeight); 
      // add a padding buffer for scrollbars
      t += this._scrollPaddingAdjustment;
       this.domNode.style.height = t + "px";
    }
    
    if(this.fitTo == "parent"){
      var h = dojo._getContentBox(this.domNode.parentNode).h;
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | fitTo = parent, height = " + h);
      dojo.marginBox(this.domNode, { h: Math.max(0, h) });
    }
    // header height
    var t = this.views.measureHeader();
    consoleDebugAgentDojoFixes("Virtual Grid: resize() | header height = " + t);
    this.headerNode.style.height = t + 'px';
    // content extent
    var l = 1;
    h = (this._autoHeight ? -1 : Math.max(this.domNode.clientHeight - t, 0) || 0);
    if(this.autoWidth){
      // grid width set to total width
      this.domNode.style.width = this.views.arrange(l, 0, 0, h) + 'px';
    }else{
      // views fit to our clientWidth
      var w = this.domNode.clientWidth || (this.domNode.offsetWidth - padBorder.w);
      this.views.arrange(l, 0, w, h);
    }

    // virtual scroller height
    this.scroller.windowHeight = h; 
    this.scroller.defaultRowHeight = this.rows.getDefaultHeightPx() + 1;
    this.postresize();
  },

	_setAutoHeightAttr: function(ah, skipRender){
	  consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr()");
	  consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | type of autoHeight is " + typeof ah);

		if(typeof ah == "string"){
			if(!ah || ah == "false"){
				ah = false;
			}
			else if (ah == "true" || ah == true){
				ah = true;
			}
			else{
				ah = window.parseInt(ah, 10);
				if(isNaN(ah)){
					ah = false;
				}
				// Autoheight must be at least 1, if it's a number.  If it's
				// less than 0, we'll take that to mean "all" rows (same as 
				// autoHeight=true - if it is equal to zero, we'll take that
				// to mean autoHeight=false
				if(ah < 0){
					ah = true;
				}
				else if (ah === 0){
					ah = false;
				}
			}
		}

		consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | autoHeight = " + this.autoHeight);
		this.autoHeight = ah;
		if(typeof ah == "boolean"){
			this._autoHeight = ah;
		}
		else if(typeof ah == "number"){			
			if((ah >= this.rowCount)){
				this.rowCount = ah;
				this._autoHeight = false;				
			}
			else{
				this._autoHeight = false;
			}
			
			if (this.model && this.model.data) {
  			consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | model.data = " + this.model.data);
  			consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | model.data.length = " + this.model.data.length);
  			if(this.rowCount > this.model.data.length){
  				this.rowCount = this.model.data.length;
  				this._autoHeight = true;
  			}
  		}
			
			//use the autoHeight number as the max
			this.render();
		}
		else{
			this._autoHeight = false;
		}
		if(this._started && !skipRender){
			this.resize();
		}

	},

	resizeHeight: function(){
	  consoleDebugAgentDojoFixes("Virtual Grid: resizeHeight()");
		var t = this.views.measureHeader();
		this.headerNode.style.height = t + 'px';
		consoleDebugAgentDojoFixes("Virtual Grid: resizeHeight() | headerNode.style.height = " + this.headerNode.style.height);
		// content extent
		var h = (this.autoHeight ? -1 : Math.max(this.domNode.clientHeight - t, 0) || 0);
		consoleDebugAgentDojoFixes("Virtual Grid: h = " + h);
		//this.views.arrange(0, 0, 0, h);
		this.views.onEach('setSize', [0, h]);
		this.views.onEach('resizeHeight');
		this.scroller.windowHeight = h; 
	},
  postrender: function(){
    consoleDebugAgentDojoFixes("Virtual Grid: postrender()");
		this.postresize();
		this.focus.initFocusView();
		
		/*Fix for CSC-168369, that corrects for an IE7 redraw issue with the grids*/
    if(dojo.isIE && dojo.isIE < 8){
      // Position the content column off viewport
      dojo.byId("contentColumn").style.position = "absolute";
      dojo.byId("contentColumn").style.left = "0px";
      // set the scrollbar to the bottom
      // this line is the actual fix for the IE rendering bug
      dojo.byId("contentColumn").scrollTop = dojo.byId("contentColumn").scrollHeight;
    }
		
	},

	postMixInProperties: function(){
	  consoleDebugAgentDojoFixes("Virtual Grid: postMixInProperties()");
		// Call this to update our autoheight to start out
		this._setAutoHeightAttr(this.autoHeight, true);
  },
	
  destroy: function(){
    // verify that DOM node exists before attempting to access
    if (this.domNode) {
      this.domNode.onReveal = null;
      this.domNode.onSizeChange = null;
    }
    if (this.edit) this.edit.destroy();
    if (this.views) this.views.destroyViews();
    if (this.inherited && dojo.isFunction(this.inherited)) this.inherited("destroy", arguments);
  },

  loadContent: function() {
    // set in JSP to refresh grid content
  },
  postCreate: function(){
		this.inherited("postCreate", arguments);
		if (this.loadContent) this.loadContent();
  }
});

/* 
--------------------------------------------------------------------
End Grid autoheight overrides
--------------------------------------------------------------------
*/


/* 
--------------------------------------------------------------------
Menu update to support icon URLs
-------------------------------------------------------------------- 
*/

dojo.require("dijit.Menu");
dojo.extend(dijit.MenuItem, {
	// Make 3 columns 
	// icon, label, and expand arrow (BiDi-dependent) indicating sub-menu
	templateString:
		 '<tr class="dijitReset dijitMenuItem"'
		+'dojoAttachEvent="onmouseenter:_onHover,onmouseleave:_onUnhover,ondijitclick:_onClick">'
		+'<td class="dijitReset"><div class="dijitMenuItemIcon ${iconClass}" dojoAttachPoint="iconNode"></div></td>'
		+'<td tabIndex="-1" class="dijitReset dijitMenuItemLabel" dojoAttachPoint="containerNode" waiRole="menuitem"></td>'
		+'<td class="dijitReset" dojoAttachPoint="arrowCell">'
			+'<div class="dijitMenuExpand" dojoAttachPoint="expand" style="display:none">'
			+'<span class="dijitInline dijitArrowNode dijitMenuExpandInner">+</span>'
			+'</div>'
		+'</td>'
		+'</tr>',

	// label: String
	//	menu text
	label: '',

	// iconClass: String
	//	class to apply to div in button to make it display an icon
	iconClass: "",

	// iconURL: String
	//	URL to path and file of icon to be displayed in the case when CSS is not feasible
	iconURL: "",
	
	// iconHover: String
	//	Hover text to be used as an ALT tag over the icon
	iconHover: "",

	// disabled: Boolean
	//  if true, the menu item is disabled
	//  if false, the menu item is enabled
	disabled: false,

	postCreate: function(){
		if (this.iconURL) {
		  this.iconNode.innerHTML='<img src="'+this.iconURL+'" border="0" title="'+this.iconHover+'">';
		}
		
		dojo.setSelectable(this.domNode, false);
		this.setDisabled(this.disabled);
		if(this.label){
			this.containerNode.innerHTML=this.label;
		}
	},

	_onHover: function(){
		// summary: callback when mouse is moved onto menu item
		this.getParent().onItemHover(this);
	},

	_onUnhover: function(){
		// summary: callback when mouse is moved off of menu item
		// if we are unhovering the currently selected item
		// then unselect it
		this.getParent().onItemUnhover(this);
	},

	_onClick: function(evt){
		this.getParent().onItemClick(this);
		dojo.stopEvent(evt);
	},

	onClick: function() {
		// summary
		//	User defined function to handle clicks
	},

	focus: function(){
		dojo.addClass(this.domNode, 'dijitMenuItemHover');
		try{
			dijit.focus(this.containerNode);
		}catch(e){
			// this throws on IE (at least) in some scenarios
		}
	},

	_blur: function(){
		dojo.removeClass(this.domNode, 'dijitMenuItemHover');
	},

	setDisabled: function(/*Boolean*/ value){
		// summary: enable or disable this menu item
		this.disabled = value;
		dojo[value ? "addClass" : "removeClass"](this.domNode, 'dijitMenuItemDisabled');
		dijit.setWaiState(this.containerNode, 'disabled', value ? 'true' : 'false');
	}
});

/* 
--------------------------------------------------------------------
End of Menu update to support icon URLs
-------------------------------------------------------------------- 
*/


/* 
--------------------------------------------------------------------
Toaster update to suppress the functionality that hides the Toaster
when clicked directly
-------------------------------------------------------------------- 
*/
dojo.require("dojox.widget.Toaster");
dojo.extend(dojox.widget.Toaster, {
  
  // We are overriding the templateString to omit the onSelect event, thereby preventing the toaster from being closed manually
  templateString: '<div dojoAttachPoint="clipNode"><div dojoAttachPoint="containerNode"><div dojoAttachPoint="contentNode"></div></div></div>',
  
  setContent: function(/*String*/message, /*String*/messageType, /*int?*/duration){
			// summary
			//		sets and displays the given message and show duration
			// message:
			//		the message
			// messageType:
			//		type of message; possible values in messageTypes enumeration ("message", "warning", "error", "fatal")
			// duration:
			//		duration in milliseconds to display message before removing it. Widget has default value.
			duration = duration||this.duration;
			// sync animations so there are no ghosted fades and such
			if(this.slideAnim){
				if(this.slideAnim.status() != "playing"){
					this.slideAnim.stop();
				}
				if(this.slideAnim.status() == "playing" || (this.fadeAnim && this.fadeAnim.status() == "playing")){
					setTimeout(dojo.hitch(this, function(){
						this.setContent(message, messageType);
					}), 50);
					return;
				}
			}

			var capitalize = function(word){
				return word.substring(0,1).toUpperCase() + word.substring(1);
			};

			// determine type of content and apply appropriately
			for(var type in this.messageTypes){
				dojo.removeClass(this.containerNode, "dijitToaster" + capitalize(this.messageTypes[type]));
			}

			dojo.style(this.containerNode, "opacity", 1);

			if(message && this.isVisible){
				//We want to deliberately override the default Dojo behavior and only support a single instance of the widget.
				//Instead of appending the additional content to the widget, we will simply overwrite the old message bar
				//with the new one.
				
				//message = this.contentNode.innerHTML + this.separator + message;
			}
			this.contentNode.innerHTML = message;

			dojo.addClass(this.containerNode, "dijitToaster" + capitalize(messageType || this.defaultType));

			// now do funky animation of widget appearing from
			// bottom right of page and up
			this.show();
			var nodeSize = dojo.marginBox(this.containerNode);
			
			if(this.isVisible){
				this._placeClip();
			}else{
				var style = this.containerNode.style;
				var pd = this.positionDirection;
				// sets up initial position of container node and slide-out direction
				if(pd.indexOf("-up") >= 0){
					style.left=0+"px";
					style.top=nodeSize.h + 10 + "px";
				}else if(pd.indexOf("-left") >= 0){
					style.left=nodeSize.w + 10 +"px";
					style.top=0+"px";
				}else if(pd.indexOf("-right") >= 0){
					style.left = 0 - nodeSize.w - 10 + "px";
					style.top = 0+"px";
				}else if(pd.indexOf("-down") >= 0){
					style.left = 0+"px";
					style.top = 0 - nodeSize.h - 10 + "px";
				}else{
					throw new Error(this.id + ".positionDirection is invalid: " + pd);
				}

				this.slideAnim = dojo.fx.slideTo({
					node: this.containerNode,
					top: 0, left: 0,
					duration: 450});
				dojo.connect(this.slideAnim, "onEnd", this, function(nodes, anim){
						//we build the fadeAnim here so we dont have to duplicate it later
						// can't do a fadeHide because we're fading the
						// inner node rather than the clipping node
						this.fadeAnim = dojo.fadeOut({
							node: this.containerNode,
							duration: 1000});
						dojo.connect(this.fadeAnim, "onEnd", this, function(evt){
							this.isVisible = false;
							this.hide();
						});
						//if duration == 0 we keep the message displayed until clicked
						//TODO: fix so that if a duration > 0 is displayed when a duration==0 is appended to it, the fadeOut is canceled
						if(duration>0){
							setTimeout(dojo.hitch(this, function(evt){
								// we must hide the iframe in order to fade
								// TODO: figure out how to fade with a BackgroundIframe
								if(this.bgIframe && this.bgIframe.iframe){
									this.bgIframe.iframe.style.display="none";
								}
								this.fadeAnim.play();
							}), duration);
						}else{
							dojo.connect(this, 'onSelect', this, function(evt){
								this.fadeAnim.play();
							});
						}
						this.isVisible = true;
					});
				this.slideAnim.play();
			}
		}

});


/* 
--------------------------------------------------------------------
Split Container - Added Typeof test instead of just () since 0 returned erroneous result, bug in Dojo codeline
--------------------------------------------------------------------
*/
dojo.extend(dijit.layout.SplitContainer, {

	beginSizing: function(e, i){
		var children = this.getChildren();
		this.paneBefore = children[i];
		this.paneAfter = children[i+1];

		this.isSizing = true;
		this.sizingSplitter = this.sizers[i];

		if(!this.cover){
			this.cover = dojo.doc.createElement('div');
			this.domNode.appendChild(this.cover);
			var s = this.cover.style;
			s.position = 'absolute';
			s.zIndex = 1;
			s.top = 0;
			s.left = 0;
			s.width = "100%";
			s.height = "100%";
		}else{
			this.cover.style.zIndex = 1;
		}
		this.sizingSplitter.style.zIndex = 2000;

		// TODO: REVISIT - we want MARGIN_BOX and core hasn't exposed that yet (but can't we use it anyway if we pay attention? we do elsewhere.)
		this.originPos = dojo.coords(children[0].domNode, true);
		if(this.isHorizontal){
			var client = (typeof e.layerX === 'undefined') ?  e.offsetX : e.layerX;
			var screen = e.pageX;
			this.originPos = this.originPos.x;
		}else{
			var client = (typeof e.layerY === 'undefined') ? e.offsetY : e.layerY;
			var screen = e.pageY;
			this.originPos = this.originPos.y;
		}
		this.startPoint = this.lastPoint = screen;
		this.screenToClientOffset = screen - client;
		
	
		this.dragOffset = this.lastPoint - this.paneBefore.sizeActual - this.originPos - this.paneBefore.position;

		if(!this.activeSizing){
			this._showSizingLine();
		}

		//					
		// attach mouse events
		//
		this._connects = [];
		this._connects.push(dojo.connect(document.documentElement, "onmousemove", this, "changeSizing"));
		this._connects.push(dojo.connect(document.documentElement, "onmouseup", this, "endSizing"));

		dojo.stopEvent(e);
	}
	
});

/* 
--------------------------------------------------------------------
Tool-tip fix for weird flixering when the tooltip contents is rolledover in IE7+
--------------------------------------------------------------------
*/
dojo.extend(dijit._MasterTooltip, {

_onShow: function(){
	if(dojo.isIE){
		// the arrow won't show up on a node w/an opacity filter
		// this.domNode.style.filter="";
	}
}

});


/* 
--------------------------------------------------------------------
Menu button update to for active parent menu buttons when the menu is opened
-------------------------------------------------------------------- 
*/

dojo.require("dijit.form._FormWidget");
dojo.require("dijit._Container");

dojo.extend(dijit.form.DropDownButton, {

	_openDropDown: function(){
		var dropDown = this.dropDown;
		var oldWidth=dropDown.domNode.style.width;
		var self = this;

		dijit.popup.open({
			parent: this,
			popup: dropDown,
			around: this.domNode,
			orient: this.isLeftToRight() ? {'BL':'TL', 'BR':'TR', 'TL':'BL', 'TR':'BR'}
				: {'BR':'TR', 'BL':'TL', 'TR':'BR', 'TL':'BL'},
			onExecute: function(){
				self._closeDropDown(true);
			},
			onCancel: function(){
				self._closeDropDown(true);
			},
			onClose: function(){
				dropDown.domNode.style.width = oldWidth;
				self.popupStateNode.removeAttribute("popupActive");
				dojo.removeClass(self.titleNode.parentNode, "popupActive");
				this._opened = false;
			}
		});
		if(this.domNode.offsetWidth > dropDown.domNode.offsetWidth){
			var adjustNode = null;
			if(!this.isLeftToRight()){
				adjustNode = dropDown.domNode.parentNode;
				var oldRight = adjustNode.offsetLeft + adjustNode.offsetWidth;
			}
			// make menu at least as wide as the button
			dojo.marginBox(dropDown.domNode, {w: this.domNode.offsetWidth});
			if(adjustNode){
				adjustNode.style.left = oldRight - this.domNode.offsetWidth + "px";
			}
		}
		this.popupStateNode.setAttribute("popupActive", "true");
		dojo.addClass(this.titleNode.parentNode, "popupActive");
		
		this._opened=true;
		if(dropDown.focus){
			dropDown.focus();
		}
		// TODO: set this.checked and call setStateClass(), to affect button look while drop down is shown
	}  

});


/* 
--------------------------------------------------------------------
Re-add 'name' attribute to text field in a FilteringSelect (CSC-159770)
-------------------------------------------------------------------- 
*/

dojo.registerModulePath("atg.widget", "/WebUI/dijit");
dojo.require('atg.widget.form.FilteringSelect');

dojo.extend(atg.widget.form.FilteringSelect, {
    
    postCreate: function(){
        this.inherited('postCreate', arguments);
        this.textbox.name = this.textbox.id;
        this.valueNode.id = this.valueNode.name;
    }
    
});

/*
--------------------------------------------------------------------
When DateTextBox text input is empty and not focused, than it displays the correct date format in gray. 
-------------------------------------------------------------------- 
*/

dojo.require("dijit.form.DateTextBox");
dojo.extend(dijit.form.DateTextBox, {
		
		_setInputValue: function(/* String */ value) {
			dojo.byId(this.id).value = value
		},
		
		_isDatePatternInInput: function() {
			return (dojo.byId(this.id).value == this.constraints.datePattern);
		},
		
		_setupPlaceholderIfNeeded: function(/*Boolean*/ isInitialization) {
			if(this._isConstrainedByDatePattern) {
				var isEmptyInput = !dojo.byId(this.id).value;
				var isInputDisabledOrItIsInitialization =(!this.disabled || isInitialization );
				if(isEmptyInput && isInputDisabledOrItIsInitialization) {
					dojo.style(this.id,"color", "gray");
					this._setInputValue(this.constraints.datePattern);
					
					// TODO: When dojo version > 1.0, check if it is possible to remove this
					if(this.disabled){ // Looks weird, but otherwise disabled input will look like enabled input
						this.setDisabled(true);
					}
				}
			}
		},
		
		_removePlaceholderIfNeeded: function() {
			if(this._isConstrainedByDatePattern) {
				if(this._isDatePatternInInput() && !this.disabled) {
					dojo.style(this.id,"color", "black");
					this._setInputValue('');
				}
			}
		},
		
		// Had to override _onBlur and _onFocus instead of connection by dojo.connect(Overriding gives more controll)
		_onBlur: function() {
			this.inherited('_onBlur', arguments);
			this._setupPlaceholderIfNeeded();
		},
		
		_onFocus: function() {
			this._removePlaceholderIfNeeded();
			this.inherited('_onFocus', arguments);
		},
		
		isValid: function() {
			if (this._isDatePatternInInput()) {
				return true;
			} else {
				return this.inherited('isValid', arguments);
			}
		},
		
		// Use this method to get value. Do not call dojo.byId($id).value directly
		getValue: function() {
			if (this._isDatePatternInInput()) {
				return '';
			} else {
				return this.inherited('getValue', arguments);
			}
		},
		
		// TODO: Uncomment when used dojo version will be >= 1.2
		/*
		_valueChanged: function() {
			if(this._isDatePatternInInput) {
				dojo.style(this.id,"color", "gray");
			} else {
				dojo.style(this.id,"color", "black");
			}
		},
		*/
		
		postCreate: function(){
			this.inherited('postCreate', arguments);
			this._isConstrainedByDatePattern = false;
			if(this.constraints.datePattern) {
				this._isConstrainedByDatePattern = true;
				var context = dijit.byId(this.id);
				// TODO: Replace with commented lines below, when used dojo version will be >= 1.2
				setTimeout(dojo.hitch(context, "_setupPlaceholderIfNeeded", "true"), 100);
				/* context.watch("value", _valueChanged);
				this._setupPlaceholderIfNeeded(); */
			}
		}
});


/* 
--------------------------------------------------------------------
IE8 compatibility fix
-------------------------------------------------------------------- 
*/

dojo.require("dijit.layout.ContentPane");
dojo.extend(dijit.layout.ContentPane, {

  excludeFromTabs: false,
  _setContent: function(cont){
    this.destroyDescendants();

    // FORM Tag switcherooo
    if(dojo.isIE){
      var replacePattern = new RegExp("(<form([^>])*>)", "gmi");
      var matchPattern = new RegExp("(<form)([^>])*action=([^>])*>", "gmi");
      cont = cont.replace(replacePattern, function($0)
      {
        if ($0 != "")
          if ($0.match(matchPattern))
            return $0.replace(/<FORM/i, "<DIV")
          else
            return $0.replace(/<FORM/i, "<DIV action=\"#\"")
       }).replace(/<\/FORM>/gi, "</DIV>");
    }

    try{
      var node = this.containerNode || this.domNode;
      while(node.firstChild){
        dojo._destroyElement(node.firstChild);
      }
      if(typeof cont == "string"){
        // dijit.ContentPane does only minimal fixes,
        // No pathAdjustments, script retrieval, style clean etc
        // some of these should be available in the dojox.layout.ContentPane
        if(this.extractContent){
          match = cont.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
          if(match){ cont = match[1]; }
        }
        node.innerHTML = cont;
      }else{
        // domNode or NodeList
        if(cont.nodeType){ // domNode (htmlNode 1 or textNode 3)
          node.appendChild(cont);
        }else{// nodelist or array such as dojo.Nodelist
          dojo.forEach(cont, function(n){
            node.appendChild(n.cloneNode(true));
          });
        }
      }

      if(dojo.isIE){
        var divTags = node.getElementsByTagName('DIV');
        for(var k = 0; k<divTags.length; k++){
          var thisDiv = divTags[k];
          if(thisDiv.getAttribute('action')){
            newFormNode = atg.formManager.createForm();
            newFormNode.innerHTML = thisDiv.innerHTML;

            // copy over all attributes and styles from the oDIV to the recycled form node
           for (var attrName in  thisDiv.attributes)
              if (thisDiv.attributes[attrName] &&
                  thisDiv.attributes[attrName].nodeValue)
                newFormNode.setAttribute(thisDiv.attributes[attrName].nodeName, thisDiv.attributes[attrName].nodeValue);
            for (var currentStyle in thisDiv.style){
              if (currentStyle != "font")
                  newFormNode.style[currentStyle] = thisDiv.style[currentStyle];
            }
            newFormNode.style.cssText = thisDiv.style.cssText;
            thisDiv.replaceNode(newFormNode);
            thisDiv.outerHTML = "";
            thisDiv.removeNode();
            k--;
          }
        }
      }

    }catch(e){
      // check if a domfault occurs when we are appending this.errorMessage
      // like for instance if domNode is a UL and we try append a DIV
      var errMess = this.onContentError(e);
      try{
        node.innerHTML = errMess;
      }catch(e){
        console.error('Fatal '+this.id+' could not change content due to '+e.message, e);
      }
    }
  }

});

/* 
--------------------------------------------------------------------
Bug fix 16023367
-------------------------------------------------------------------- 
*/

if(dojo.isIE || dojo.isOpera){
  dojo.byId = function(id, doc){
    if(dojo.isString(id)){
      var _d = doc || dojo.doc;
      var te = _d.getElementById(id);
      // attributes.id.value is better than just id in case the
      // user has a name=id inside a form
      if(te && te.attributes.id.value == id){
        return te;
      }else{
        var eles = _d.all[id];
        if(!eles){ return; }
        if(!eles.length){ return eles; }
        // if more than 1, choose first with the correct id
        var i=0;
        while((te=eles[i++])){
          //Bug fix 16023367, null value check for te.attributes.id has been added
          if(te.attributes.id && te.attributes.id.value == id){ return te; }
        }
      }
    }else{
      return id; // DomNode
    }
  }
}

/**
 * Override dojox email validation as the dojo implementation is too retrictive.
 * The purpose of this is to be make the validation simple and minimally 
 * restrictive. Please resist the temptation to "improve" this regular 
 * expression, as it is considered "good enough".
 */
dojo.require("dojox.validate.web");
dojox.validate.isEmailAddress = function (value) {
  return /.+@.+/.test(value);
}

/*
  The default behaiour listens to keypress event, which is a non standard event.
  Keys such ask Alt, Ctrl, ESC don't trigger a keypress event in some browser.
  Must listen on keyup.
*/
dojo.require("dojox.Dialog");
dojo.extend(dojox.Dialog, {
  _onKey: function(/*Event*/ evt){
    // summary: handles the keyboard events for accessibility reasons
    if(evt.keyCode){
      var node = evt.target;
      // see if we are shift-tabbing from titleBar
      if(node == this.titleBar && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
        if(this._lastFocusItem){
          this._lastFocusItem.focus(); // send focus to last item in dialog if known
        }
        dojo.stopEvent(evt);
      }else{

        // see if the key is for the dialog
        while (node) {
          if (node == this.domNode || dojo.hasClass(node, "dijitDialog")) {
            if (evt.keyCode == dojo.keys.ESCAPE) {
              this.hide();
            } else {
              return; // just let it go
            }
          }
          node = node.parentNode;
        }

        // this key is for the disabled document window
        if(evt.keyCode != dojo.keys.TAB){ // allow tabbing into the dialog for a11y
          dojo.stopEvent(evt);
        // opera will not tab to a div
        }else if (!dojo.isOpera){
          try{
            this.titleBar.focus();
          }catch(e){/*squelch*/}
        }
      }
    }
  },

  show: function(){
    // summary: display the dialog

    // first time we show the dialog, there's some initialization stuff to do
    if(!this._alreadyInitialized){
      this._setup();
      this._alreadyInitialized=true;
    }

    if(this._fadeOut.status() == "playing"){
      this._fadeOut.stop();
    }

    this._modalconnects.push(dojo.connect(window, "onscroll", this, "layout"));
    // Listen on keyup instead of keypress, as keypress os not as widely supported.
    this._modalconnects.push(dojo.connect(document.documentElement, "onkeyup", this, "_onKey"));

    // IE doesn't bubble onblur events - use ondeactivate instead
    var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
    this._modalconnects.push(dojo.connect(this.containerNode, ev, this, "_findLastFocus"));

    dojo.style(this.domNode, "opacity", 0);
    this.domNode.style.display="block";
    this.open = true;
    this._loadCheck(); // lazy load trigger

    this._position();

    if (djConfig.usesApplets) {
      // add class to body to hide the applet
      dojo.addClass(document.body, "appletKiller");
      dojo.query("iframe").forEach(

        function(eachIframe) {
          if(eachIframe.contentDocument){
            // Firefox, Opera
            doc = eachIframe.contentDocument;
          }else if(eachIframe.contentWindow){
            // Internet Explorer
            doc = eachIframe.contentWindow.document;
          }else if(eachIframe.document){
            // Others?
            doc = eachIframe.document;
          }
          if (doc.body) {
            dojo.addClass(doc.body, "appletKiller");
          }
        }
      );
    }

    this._fadeIn.play();

    try {
      this._savedFocus = dijit.getFocus(this);
    }
    catch (e) {
      // On IE7 this is a bogus error caused by creating ranges
      // on text in a DIV that is display:none - apparently getFocus does
      // somewhere internally - when this happens just reset the property
      // gracefully without a major page blow-up - the dialog will just
      // behave as though there was no prior focus - we can live with this
      this._savedFocus = null;
      delete this._savedFocus;
    }

    // set timeout to allow the browser to render dialog
    setTimeout(dojo.hitch(this, function(){
      dijit.focus(this.titleBar);
    }), 50);
  }
});
// This should add an input tag to a form dynamically
//
function addNewInput(strName, strType, strValue, objForm, objDocument)
{
   var objInput       = objDocument.createElement("input");
   objInput.id        = strName;
   objInput.name      = strName;
   objInput.type      = strType;
   objInput.value     = strValue;
   objInput = objForm.appendChild(objInput);
}

// THis will remove HTML from a given string
//
function stripHTML(oldString)
{
  return oldString.replace(/(<([^>]+)>)/ig, "");
}

// Encodes text as HTML
function escapeHTML(text) {
  var textNode  = document.createTextNode(text);
  var div       = document.createElement('div');
  div.appendChild(textNode);
  return div.innerHTML;
};

// This function will do a replace all of "strReplace" in "str", when it finds "strFind"
// this function does not use regular expressions
//
function getElt () {
    var name = getElt.arguments[getElt.arguments.length-1];
    var element = document.getElementById(name);
    return element;
}

function replaceAll(str, strFind, strReplace)
{
  var returnStr = str;
  var strFindUpper = strFind.toUpperCase()
  var start = returnStr.toUpperCase().indexOf(strFindUpper);

  while (start >= 0)
  {
    returnStr = returnStr.substring(0, start) + strReplace + returnStr.substring(start + strFind.length, returnStr.length);
    start = returnStr.toUpperCase().indexOf(strFindUpper, start + strReplace.length);
  }
  return returnStr;
}

function trim(str)
{
   var strTrim = trimRight(str)
   return trimLeft(strTrim);
}

function trimRight(str)
{
   var retStr = str;
   var i = str.length - 1;
   while (i >= 0)
   {
      if (str.substring(i, i + 1) == " ")
         retStr = str.substring(0, i);
      else
         break;
      i--;
   }
   return retStr;
}

function trimLeft(str)
{
   var retStr = str;
   var i = 0;
   while (i < str.length)
   {
      if (str.substring(i, i + 1) == " ")
         retStr = str.substring(i + 1, str.length);
      else
         break;
      i++;
   }
   return retStr;
}

  /**
   *
   * This method is used only if you have already known content. If you are not making
   * a server trip to get the floating pane content, then you can use this method to load
   * the floating pane content.
   * This method takes the original div content and sticks it to the floating pane and removes the
   * original div conent.
   *
   */
  showPopupWithContent = function (pOriginalContentDivId, pFloatingPane/*Floating pane Id*/, args ) {
     var originalDiv = document.getElementById ('pOriginalDivId');
     var confirmWindow = dijit.byId(pFloatingPane);

    // This function gets called when the popup is closed
    function closeBuddy ()
    {
        if ( args.onClose ) {
          args.onClose( getEnclosingPopup(pFloatingPane)._atg_results );
        }
    }

    getEnclosingPopup(pFloatingPane)._atg_args = args;
    
    if (confirmWindow.connectHandle) dojo.disconnect(confirmWindow.connectHandle);
    confirmWindow.connectHandle = dojo.connect( confirmWindow, "hide", closeBuddy );

     confirmWindow.setContent (originalDiv);
     originalDiv.innerHTML="";
     confirmWindow.show();
  };

// Return enclosing Dojo Dialog given the ID of any child node
getEnclosingPopup = function ( nodeId )
{
  var startAtNode;
  if (dijit.byId(nodeId)) {
    startAtNode = dijit.byId(nodeId).domNode;
  }
  else if (dojo.byId(nodeId)) {
    startAtNode = dojo.byId(nodeId);
  }
  var foundNode = dojo.dom.getAncestors(
    startAtNode,
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
  return dijit.byId(foundNode.id);
};

showPopupWithResults = function ( args )
{
  var popupPane = dijit.byId( args.popupPaneId );
  
  // Initing popup results
  popupPane._atg_results = {};

  // This function gets called when the popup is closed
  var closeBuddy = function (args)
  {
    if ( args.onClose ) {
      args.onClose( getEnclosingPopup(args.popupPaneId)._atg_results );
    }
  }

  this.getEnclosingPopup(args.popupPaneId)._atg_args = args;

  // need to replace any previously connected function
  if (popupPane.closeHandle) dojo.disconnect(popupPane.closeHandle);
  popupPane.closeHandle = dojo.connect(popupPane, "hide", dojo.hitch(this, closeBuddy, args));
  popupPane.titleNode.innerHTML = args.title || "";

  popupPane.setHref( args.url );
  popupPane.show();
};

hidePopupWithResults = function ( childId, results )
{
  setTimeout(function () {
    var popup = this.getEnclosingPopup( childId );
    popup._atg_results = results;
    popup.hide();
  }, 50);
};

/** 
 * Escapes XML in provided string
 */
function escapeXML(string) 
{
	return string.replace(/\"/g,'&quot;').replace(/\'/g,'&#39;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/\(/g,'&#40;').replace(/\)/g,'&#41;');
}
 /* Copyright (C) 1999-2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */

/*
//
//  Misc
//
//

//  This function will do a replace all of  "strReplace" in "str", when it finds "strFind"
//  this function does not use  regular expressions
*/
function  ReplaceAll(str, strFind, strReplace)
{
  var returnStr = str;
  var strFindUpper = strFind.toUpperCase();
  var start = returnStr.toUpperCase().indexOf(strFindUpper);

  while (start >= 0)
  {
    returnStr = returnStr.substring(0, start) + strReplace +  returnStr.substring(start + strFind.length, returnStr.length);
    start = returnStr.toUpperCase().indexOf(strFindUpper, start + strReplace.length);
  }
  return returnStr;
}

//   Editor
//
var l_bHandleAltGRKey = false;
var l_bInitGRKeyVar = false;

function  IsAltKeyDown(objEvent)
{
  //  On  the french keyboard,  the right alt key is  known as  the "Alt+GR" key.  It's purpose is much differnt than the left  Alt Key.
  //  So  lets handle this properly,  and do it only  for the french  language  (as determined  by  the web server, it is conceivable that  we
  //  could do  this by determining the language  of  the web browser, but  this seems like a better approach).  It should be noted that
  //  "altLeft" is not part of of the Javascript spec, it is something specific to Microsoft's IE browsers.
  //
  //  Cache this for  speed
  //
  if  (!l_bInitGRKeyVar)
  {
    l_bHandleAltGRKey = "false" == "true";
    l_bInitGRKeyVar = true;
  }

  if  (l_bHandleAltGRKey)
  {
    if  (objEvent.altLeft)
    {
      return true;
    }
    else
    {
      if  (objEvent.altKey)
      {
        return false;
      }
    }
    return false;
  }
  else {
    return objEvent.altKey;
  }
}

function  IsAltGRKeyDown(objEvent)
{
  //  On  the french keyboard,  the right alt key is  known as  the "Alt+GR" key.  It's purpose is much different than the left Alt Key.
  //  So  lets handle this properly,  and do it only  for the french  language  (as determined  by  the web server, it is conceivable that  we
  //  could do  this by determining the language  of  the web browser, but  this seems like a better approach).
  //
  //  The way to know whether or  not the Alt+GR  key is down is  down is when the Ctrl and Alt keys are  down at the same time.  This seems
  //  odd, but  that is how the Alt+GR is interpreted
  //
  //  Cache this for  speed
  //
  if  (!l_bInitGRKeyVar)
  {
    l_bHandleAltGRKey = "false" == "true";
    l_bInitGRKeyVar = true;
  }

  if  (l_bHandleAltGRKey)
  {
    return (objEvent.altKey &&  objEvent.ctrlKey);
  }
  else
  {
    return false;
  }
}

function  getStatementText(objWindow)
{
  //  When dealing with a document that has "designmode"  set to "on", in Netscape, if the  innerHTML of that document
  //  was empty, it will now be a "<BR>" with a carriage  return and linefeed on the  end.  In  this case only, we want
  //  to  return an empty string
  //
  //  For IE, the end of the document could very well be  a "<p>&nbsp;</p>" if  there is  nothing in the  cell
  //
  var objBody = objWindow.document.body;
  if  (!objBody || objBody.innerHTML == "") {
    return "";
  }
  else if ((objBody.innerHTML.length == 6 &&
    objBody.innerHTML.slice(0,  4).toLowerCase() == "<br>"  &&
    objBody.innerHTML.charCodeAt(4) == 13 &&
    objBody.innerHTML.charCodeAt(5) == 10)  ||
    (objBody.innerHTML.toLowerCase()  ==  "<p>&nbsp;</p>")){
    return "";
  }
  else
  {
    var str = objBody.innerHTML;

    //  Lets see  if  the statement is wrapped with paragraph tags, if so, lets remove them
    //
    if  ((objBody.childNodes.length == 1) &&
      (objBody.firstChild)  &&  (objBody.firstChild.tagName) &&
      (objBody.firstChild.tagName.toUpperCase() ==  "P")) {
      str = objBody.firstChild.innerHTML;
    }
    return str;
  }
}
          
function GetEvent(event)
{
  return (event) ? event : window.event;
}

function HidePopups()
{
}

function hideCurtain(curtainId) {
  if((curtainId != null) && (curtainId != "")) {
    var curtainObject = dojo.byId(curtainId);
    if (curtainObject != null) {
      curtainObject.style.display = "none";
    }
  }
}

function showCurtain(curtainId) {
  if((curtainId != null) && (curtainId != "")) {
    var curtainObject = dojo.byId(curtainId);
    if (curtainObject != null) {
      var windowWidth = document.body.offsetWidth;
      var windowHeight = document.body.offsetHeight;
      var scrollWidth = document.body.scrollWidth;
      var scrollHeight = document.body.scrollHeight;
      
      if(scrollWidth > windowWidth) {
        curtainObject.style.width = scrollWidth;
      } else {
        curtainObject.style.width = windowWidth;
      }
      if(scrollHeight > windowHeight) {
        curtainObject.style.height = scrollHeight;
      } else {
        curtainObject.style.height = windowHeight;
      }
      curtainObject.style.display = "block";
    }
  }
}

// Generic function for displayed a centered dialog with the ability to specify the dialog parameters
// Sample usage: showDialog("myfile.jsp","My Title",640,480,true,false,true);
function showDialog(url, name, dialogWidth, dialogHeight, isStatus, isScrollbars, isResizable) {
  if ((isStatus == null) || (isStatus != true)) {
    isStatus = "no";
  } else {
    isStatus = "yes";
  }
  
  if ((isScrollbars == null) || (isScrollbars != true)) {
    isScrollbars = "no";
  } else {
    isScrollbars = "yes";
  }
  
  if ((isResizable == null) || (isResizable != true)) {
    isResizable = "no";
  } else {
    isResizable = "yes";
  }

  // Offset values to take window decoration into consideration
  dialogWidth += 32;
  dialogHeight += 96;
  leftPosition = (screen.width - dialogWidth) / 2;
  topPosition = (screen.height - dialogHeight) / 2;

  var dialogWindow = window.open (url, name,
    'width=' + dialogWidth + ', height=' + dialogHeight + ', ' +
    'left=' + leftPosition + ', top=' + topPosition + ', ' +
    'location=no, menubar=no, ' +
    'status=' + isStatus + ', toolbar=no, scrollbars=' + isScrollbars + ', resizable=' + isResizable);
  
  // Just in case width and height are ignored
  dialogWindow.resizeTo(dialogWidth, dialogHeight);
  // Just in case left and top are ignored
  dialogWindow.moveTo(leftPosition, topPosition);
  dialogWindow.focus();
}

function toggle(id, imgid) {
  var ele = document.getElementById(id);
  if (ele == null) { return;}
  var elt = ele.style;
  var imgElt = document.getElementById(imgid);
  if (imgElt == null) {return;}
  if (elt.display == "none") {
    elt.display = "block";
    imgElt.src = getResource("imgArrowDown");
  }
  else {
    elt.display = "none";
    imgElt.src = getResource("imgArrowRight");
  }
}

function toggleRowGroup(id, imgid) {
  var ele = document.getElementById(id);
  if (ele == null) { return;}
  var elt = ele.style;
  var imgElt = document.getElementById(imgid);
  if (imgElt == null) {return;}
  if (elt.display == "none") {
    elt.display = "table-row-group";
    imgElt.src = getResource("imgArrowDown");
  }
  else {
    elt.display = "none";
    imgElt.src = getResource("imgArrowRight");
  }
}

function show(id) {
  var elem = dojo.byId(id);
  if (elem == null) {return;}
  var disp = dojo.style(elem, 'display');
  if (disp == '' || disp == "none") {
    dojo.style(elem, 'display', 'block');
  }
}
function hide(id) {
  var elem = dojo.byId(id);
  if (elem == null) {return;}
  if (dojo.style(elem, 'display') != 'none') {
    dojo.style(elem, 'display', 'none');
  }
}
function toggleShowing(id) {
  var elem = dojo.byId(id);
  var disp = dojo.style(elem, 'display');
  if (disp == '' || disp == "none") {
    dojo.style(elem, 'display', 'block');
  } 
  else 
  {
    dojo.style(elem, 'display', 'none');
  }
}
function isShowing(id) {
  var elem = dojo.byId(id);
  if (dojo.style(elem, "display") != "none") {
    return true;
  } 
  else 
  {
    return false;
  }
}

function dispAssignedAgent() {
  var w = document.frmLeftContent.sAssignedAgent.selectedIndex;
  var selected_text = document.frmLeftContent.sAssignedAgent.options[w].text;
  document.getElementById('aaa').innerHTML=selected_text;
}
function getPageOffsetLeft(el){
  var x;
  x=el.offsetLeft;
  if (el.offsetParent!=null) {
    x+=getPageOffsetLeft(el.offsetParent);
  }
  return x;
}
function getPageOffsetTop(el){
  var y;
  y=el.offsetTop;
  if (el.offsetParent!=null){
    y+=getPageOffsetTop(el.offsetParent);
  }
  return y;
}
function divSetVisible(divId)
{
 var divRef = document.getElementById(divId);
 if (divRef == null) {return;}
 var iframeRef = document.getElementById('divShim');
 if(divRef.style.display == "none")
 {
  divRef.style.display = "block";
  divRef.style.visibility = "visible";
  if (dojo.isIE)
  {
    iframeRef.style.width = divRef.offsetWidth+"px";
    iframeRef.style.height = divRef.offsetHeight+"px";
    iframeRef.style.top = getPageOffsetTop(divRef)+"px";
    iframeRef.style.left = getPageOffsetLeft(divRef)+"px";
    iframeRef.style.zIndex = divRef.style.zIndex - 1;
    iframeRef.style.display = "block";
    iframeRef.style.visibility = "visible";
  }
 }
}

function divSetHide(divId)
{
 var divRef = document.getElementById(divId);
 if (divRef == null) {return;}
 var iframeRef = document.getElementById('divShim');
 if(divRef.style.display == "block")
 {
  divRef.style.display = "none";
  divRef.style.visibility = "hidden";
  if (dojo.isIE)
  {
    iframeRef.style.display = "none";
    iframeRef.style.visibility = "hidden";
  }
 }
}

function loadFragments(forms)  {
  if (forms.length > 0) {
    for (var i = 0; i < forms.length; i++)  {
      dojo.byId(forms[i]).submit();
    }
  }
}

function resizeTransactionFragmentContainer(iframeWindow)
{
  if (iframeWindow.document.height)
  {
    iframeWindow.frameElement.style.height = (iframeWindow.document.height + 10) + "px";
  }
  else
  {
    iframeWindow.frameElement.style.height = (iframeWindow.document.body.scrollHeight + 10) +"px";
  }
}

function getInnerText(element)
{
  if (document.all) {
    return element.innerText;
  }
  else {
    return element.innerHTML.replace(/<br>/gi,"\n").replace(/&nbsp\;/g, " ").replace(/<[^>]+>/g,"");
  }
}

/** 
 * This is defined in /service/Agent and is knowledge specific
 * It's used/called by /script/framework.js 
 * 
 */
function saveState()
{
	return;
} /* Copyright (C) 1999-2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */

/*************************************************************************
//
// cssFramework.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// Defines client-side framework behavior
//
*************************************************************************/
function frameworkSetColumnStyles()
{
  var columnContainer            = dojo.byId("columns");
  var researchColumn             = dojo.byId("column2");
  var sidebarColumn              = dojo.byId("column3");
  var contentColumn              = dojo.byId("contentColumn");
  var column1Widget = dijit.byId("column1");
  var column2Widget = dijit.byId("column2");
  var column3Widget = dijit.byId("column3");

  var needRelayout = false;

  if (!columnContainer){
    return;
  }

  if (atg.service.framework.showResearch && atg.service.framework.researchColumnAccess) 
  {
    if (dojo.style(column2Widget.domNode, "display") == "none") {
      dojo.style(column2Widget.domNode, "display", "block");
      needRelayout = true;
    }
    if (atg.service.framework.isResearchBarOpen)
    {
      // research bar is open, remove any styles that mark it closed
      if (dojo.hasClass(columnContainer, "close_BC")) {
        dojo.removeClass(columnContainer, "close_BC");
        needRelayout = true;
      }
      else if (dojo.hasClass(columnContainer, "close_B")) {
        dojo.removeClass(columnContainer, "close_B");
        needRelayout = true;
      }
      // now check if the side bar is open
      if (atg.service.framework.isSideBarOpen)
      {
        // research bar is open, side bar is open.  We've already removed close_BC and close_B, so remove close_C
        if (dojo.hasClass(columnContainer, "close_C")) {
          dojo.removeClass(columnContainer, "close_C");
          needRelayout = true;
        }
      }
      else
      {
        // sidebar is closed.  That means that we need to set add close_C
        dojo.addClass(columnContainer, "close_C");
        needRelayout = true;
      }
    }
    else
    {
      // the research bar is closed
      if (atg.service.framework.isSideBarOpen)
      {
        // research closed, side open = close_B.  Remove close_BC
        if (dojo.hasClass(columnContainer, "close_BC")) {
          dojo.removeClass(columnContainer, "close_BC");
          dojo.addClass(columnContainer, "close_B");
          needRelayout = true;
        }
        if (!dojo.hasClass(columnContainer, "close_B")) {
          dojo.addClass(columnContainer, "close_B");
          needRelayout = true;
        }
      }
      else
      {
        // research closed, sidebar closed = close_BC
        if (dojo.hasClass(columnContainer, "close_C")) {
          dojo.removeClass(columnContainer, "close_C");
          dojo.addClass(columnContainer, "close_BC");
          needRelayout = true;
        }
        if (!dojo.hasClass(columnContainer, "close_BC")) {
          dojo.addClass(columnContainer, "close_BC");
          needRelayout = true;
        }
      }
    }
  }
  else
  {
    if (dojo.style(column2Widget.domNode, "display") != "none") {
      dojo.style(column2Widget.domNode, "display", "none");
      needRelayout = true;
    }
    if (atg.service.framework.isSideBarOpen)
    {
      if (dojo.hasClass(columnContainer, "close_C")) {
        dojo.removeClass(columnContainer, "close_C");
        needRelayout = true;
      }
    }
    else
    {
      if (!dojo.hasClass(columnContainer, "close_C")) {
        dojo.addClass(columnContainer, "close_C");
        needRelayout = true;
      }
    }
  }
  if (needRelayout) {
    cssSetFrameworkHeight();
  }
}

function frameworkCloseSidebar()
{
  if (atg.service.framework.isSideBarOpen)
  {
    atg.service.framework.toggleSidebar();
  }
}

function frameworkOpenSidebar()
{
  if (!atg.service.framework.isSideBarOpen)
  {
    atg.service.framework.toggleSidebar();
  }
}

function frameworkAddEvent( obj, type, fn )
{
  dojo.connect(obj, "on" + type, fn);
}

// get an events trigger cross browser //

function getEventSrc(event) {
 // get a reference to the IE/windows event object
 var e = (event) ? event : window.event;

 // DOM-compliant name of event source property
 if (e.target) {
   return e.target;
 }
 // IE/windows name of event source property
 else if (e.srcElement){
   return e.srcElement;
 }
}

function cssSetFrameworkHeight()
{
  var startCFH = new Date();

  dijit.byId('wholeWindow').resize();
  if(dijit.byId('contributePanelLayout')){
    dijit.byId('contributePanelLayout').resize();
  }

  var endCFH = new Date();
  console.debug("cssSetFrameworkHeight took " + (endCFH - startCFH));
}
// -------------------------------------------------------------------
// Imports
// -------------------------------------------------------------------
dojo.provide("atg.ea");

// -------------------------------------------------------------------
// EA initialization
// -------------------------------------------------------------------
atg.helpFields = new Array();
atg.helpContent={ 
  "helpItem": new Array()
};

// -------------------------------------------------------------------
// EA help array registration
// -------------------------------------------------------------------
atg.ea.registerHelpArray = function (_helpArrayObject) {
  //console.debug("EA | Registering help array for object: " + _helpArrayObject.id);
  atg.helpFields.push(_helpArrayObject);
}

// -------------------------------------------------------------------
// EA help content registration
// -------------------------------------------------------------------
atg.ea.registerHelpContent = function (_helpContentObject) {
  //console.debug("EA | Registering help content for object: " + _helpContentObject.id);
  atg.helpContent.helpItem.push(_helpContentObject);
};


// -------------------------------------------------------------------
// EA tooltip registration
// -------------------------------------------------------------------
atg.ea.registerTooltip = function (_tooltipId, _tooltipText) {
  //console.debug("EA | Registering tooltip for object with id = " + _tooltipId);
  tooltipElement = dojo.byId(_tooltipId);
  if (tooltipElement != null) {
    tooltipElement.title = _tooltipText;
  }
};
// -------------------------------------------------------------------
// EA help array structure for Service
// -------------------------------------------------------------------
atg.ea.registerServiceHelpArray = function () {
  atg.ea.registerHelpArray({ id: "ea_service_customer_search", type: "inline", helpId: "ea_service_customer_search" });
  atg.ea.registerHelpArray({ id: "atg_arm_contentBrowserTitle", type: "popup", helpId: "atg_arm_contentBrowserTitle" });
  atg.ea.registerHelpArray({ id: "atg_arm_linkedDocumentsTitle", type: "popup", helpId: "atg_arm_linkedDocumentsTitle" });
  atg.ea.registerHelpArray({ id: "atg_arm_addAttachment", type: "popup", helpId: "atg_arm_addAttachment" });
};

//console.debug("EA | atg.ea.registerServiceHelpArray called");

dojo.addOnLoad(atg.ea.registerServiceHelpArray);
// -------------------------------------------------------------------
// EA help content definitions for Service
// -------------------------------------------------------------------
atg.ea.registerServiceHelpContent = function () {
  atg.ea.registerHelpContent({ "id":"ea_service_customer_search", "excerpt":getResource("ea.service.helpContent.ea_service_customer_search"), "content":"" });
  atg.ea.registerHelpContent({ "id":"atg_arm_contentBrowserTitle", "content":getResource("ea.service.helpContent.atg_arm_contentBrowserTitle") });
  atg.ea.registerHelpContent({ "id":"atg_arm_linkedDocumentsTitle", "content":getResource("ea.service.helpContent.atg_arm_linkedDocumentsTitle") });
  atg.ea.registerHelpContent({ "id":"atg_arm_addAttachment", "content":getResource("ea.service.helpContent.atg_arm_addAttachment") });
};

//console.debug("EA | atg.ea.registerServiceHelpContent called");

dojo.addOnLoad(atg.ea.registerServiceHelpContent);

// -------------------------------------------------------------------
// EA tooltips for Service
// -------------------------------------------------------------------
atg.ea.registerServiceTooltips = function () {
  atg.ea.registerTooltip("ticketSave", getResource("ea.service.tooltip.ticketSave"));
  atg.ea.registerTooltip("customerLink", getResource("ea.service.tooltip.customerLink"));
};

dojo.subscribe("UpdateGlobalContext", null, function() { 
  //console.debug("EA | atg.ea.registerServiceTooltips called");
  atg.ea.registerServiceTooltips();
});
/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */

//*************************************************************************
//
// editor.js File
//
// This page defines the Editor object.  The Editor object provides very
// basic editing functioanlity and is responsible for exposing this 
// functionality for extension.  Although this object is a fully capable
// stand alone object, it's designed to be extended.
//
// Here is the information you need to make this work:
//
// 1: Include this file into the page where you want to add the editor.
//
// 2: Create the editor with a call to "new Editor()".
//
// 3: After adding the editor to the page, call the "init()" method
//    on the editor to initialize the editor
//
//*************************************************************************

/**************************************************************************
 **
 **
 **  Editor Object
 **
 **
 **  This object is implemented as a "DIV" element and
 **  employes an iFrame element set with "designMode" to on
 **  This allows for the editing of the object.  Definition
 **  of object properties should be done before the editor
 **  is added to the document and EditorInit() is called.
 **
 *************************************************************************/
 
var Editor = function (initialTemplate, onLoadHandler)
{
  console.debug("Editor constructor");
  //***********************************************************************
  // Create Editor
  //
  this.editor                               = document.createElement("div");
  
  //***********************************************************************
  // Initialize Editor properties
  //
  this.editor.id                            = "";
  this.editor.width                         = "100%";
  this.editor.className                     = "";
  
  //***********************************************************************
  // Initialize Editor reference properties
  //
  this.editor.editFrame                     = "";
  this.editor.currentRange                  = "";
  this.editor.editDocument                  = "";
  this.editor.modified                      = false;
  this.editor.readOnly                      = false;
  this.editor.initialContent                = "";
  
  //***********************************************************************
  // Initialize Editor methods
  /**************************************************************************
   **  EditorInit
   **  This function sets the event handlers on IE and sets the initial value
   **  of the edit document if it was provided.
   *************************************************************************/
  this.editor.init                          = function () 
  {
    console.debug("EditorInit");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editWindow                            = editor.editFrame.contentWindow;
    var editDocument                          = editWindow.document;
    
    //***********************************************************************
    // Get Body
    //  
    var editBody                              = editDocument.body;
    editBody.id                               = editor.id;
    editor.editDocument                       = editDocument;
    //***********************************************************************
    // Set editor refrences
    //
    editWindow.editor                         = editor;
    editDocument.editor                       = editor;
    editBody.editor                           = editor;
    //***********************************************************************
    // We need to seperate the initialization sequence between IE and mozilla
    // due to the fact that they need to call different event handlers and have
    // them registered in a specific sequence.
    //  
    if (document.all)
    {
      dojo.connect(editDocument, "oncontextmenu", editor, editor.handleContextMenu);
      dojo.connect(editDocument, "onclick", editor, editor.handleClick);
      dojo.connect(editDocument, "ondblclick", editor, editor.handleDblClick);
      dojo.connect(editDocument, "onkeyup", editor, editor.handleKeyUp);
      dojo.connect(editDocument, "onkeydown", editor, editor.handleKeyDown);
      dojo.connect(editDocument, "onkeypress", editor, editor.handleKeyPress);
      dojo.connect(editDocument, "onmouseup", editor, editor.handleMouseUp);
      dojo.connect(editDocument, "onmousedown", editor, editor.handleMouseDown);
      dojo.connect(editDocument, "onmousemove", editor, editor.handleMouseMove);
      dojo.connect(editBody, "onfocus", editor, editor.handleFocus);
      dojo.connect(editBody, "onblur", editor, editor.handleBlur);
      dojo.connect(editBody, "onpaste", editor, editor.handlePaste);
      dojo.connect(editBody, "ondrop", editor, editor.handleDrop);
      editBody.contentEditable = "true";
    }
    else
    {    
      editDocument.addEventListener("contextmenu", editor.handleContextMenu, true);
      editDocument.addEventListener("click",       editor.handleClick,       true);
      editDocument.addEventListener("dblclick",    editor.handleDblClick,    true);
      editDocument.addEventListener("dragdrop",    editor.handleDrop,        true);
      editDocument.addEventListener("keyup",       editor.handleKeyUp,       true);
      editDocument.addEventListener("keydown",     editor.handleKeyDown,     true);
      editDocument.addEventListener("keypress",    editor.handleKeyPress,     true);
      editDocument.addEventListener("mouseup",     editor.handleMouseUp,     true);
      editDocument.addEventListener("mousedown",   editor.handleMouseDown,   true);
      editDocument.addEventListener("mousemove",   editor.handleMouseMove,   true);
      editDocument.addEventListener("blur",        editor.handleBlur,        true);
      editDocument.addEventListener("focus",       editor.handleFocus,       true);
      setTimeout( function() { editDocument.designMode = "on";}, 1000 );
    }
  
    //***********************************************************************
    // Set initial value
    //
    if (editor.initialContent != "")
      editBody.innerHTML                      = editor.initialContent;
  };

  this.editor.destroy = function () {
    console.debug("EditorDestroy");
    // undo what was done in init
    var editor                                = this;
    var editWindow                            = editor.editFrame.contentWindow;
    var editDocument                          = editWindow.document;
  
    var editBody                              = editDocument.body;
    editBody.id                               = editor.id;
    editor.editDocument                       = editDocument;
  
    editWindow.editor                         = undefined;
    editDocument.editor                       = undefined;
    editBody.editor                           = undefined;

    // undo what was done in the constructor
    if (document.all)
    {
      dojo.disconnect(editDocument, "oncontextmenu", editor, editor.handleContextMenu);
      dojo.disconnect(editDocument, "onclick", editor, editor.handleClick);
      dojo.disconnect(editDocument, "ondblclick", editor, editor.handleDblClick);
      dojo.disconnect(editDocument, "onkeyup", editor, editor.handleKeyUp);
      dojo.disconnect(editDocument, "onkeydown", editor, editor.handleKeyDown);
      dojo.disconnect(editDocument, "onkeypress", editor, editor.handleKeyPress);
      dojo.disconnect(editDocument, "onmouseup", editor, editor.handleMouseUp);
      dojo.disconnect(editDocument, "onmousedown", editor, editor.handleMouseDown);
      dojo.disconnect(editDocument, "onmousemove", editor, editor.handleMouseMove);
      dojo.disconnect(editBody, "onfocus", editor, editor.handleFocus);
      dojo.disconnect(editBody, "onblur", editor, editor.handleBlur);
      dojo.disconnect(editBody, "onpaste", editor, editor.handlePaste);
      dojo.disconnect(editBody, "ondrop", editor, editor.handleDrop);
      editBody.contentEditable = "false";
    }
    else
    {    
      // do nothing, we couldn't use dojo.connect
    }   
    if (editor.editFrame) {
      editor.editFrame.editor.innerHTML = '';
      editor.editFrame.editor = undefined;
      if (editor.editFrame.parentNode) {
        dojo.clean(editor.editFrame.parentNode.removeChild(editor.editFrame));
      }
    }

    this.editor = undefined;
  };
  /**************************************************************************
   **
   **
   **  EditorSetText
   **  
   **  This function sets the innerText of the editor.
   **
   *************************************************************************/
  this.editor.setText = function (text)
  {
    console.debug("EditorSetText");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;
  
    editSrcWindow.document.body.innerHTML     = escape(text);
    editor.editorModify(null, editor);
  };
  
  /**************************************************************************
 **
 **
 **  EditorGetText
 **  
 **  This function returns the inner text of the editor
 **
 *************************************************************************/
  this.editor.getText = function ()
  {
    console.debug("EditorGetText");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;
  
    if (document.all)
      return editSrcWindow.document.body.innerText;
    else
      return editSrcWindow.document.body.innerHTML.replace(/<br>/gi,"\n").replace(/&nbsp\;/g, " ").replace(/<[^>]+>/g,"").replace(/&amp\;/g, "&");    
  };
  
  /**************************************************************************
 **
 **
 **  EditorSetHTML
 **  
 **  This function sets the innerHTML of the editor.
 **
 *************************************************************************/
  this.editor.setHTML = function (html)
  {
    console.debug("EditorSetHTML");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;
  
    if (!editor.editDocument.body) {
      editor.initialContent = html;
    } else {
      editSrcWindow.document.body.innerHTML     = html;
      editor.editorModify(null, editor);
    }
  };
  
  /**************************************************************************
 **
 **
 **  EditorGetHTML
 **  
 **  This function returns the HTML content of the editor.
 **
 *************************************************************************/
  this.editor.getHTML = function ()
  {
    console.debug("EditorGetHTML");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;    
  
    return editSrcWindow.document.body.innerHTML.replace(/<p>/gi,"<br>").replace(/<\/p>/gi,"");
  };
  
  /**************************************************************************
 **
 **
 **  EditorReplaceRange
 **  
 **  This function replaces the supplied range with the supplied node
 **
 *************************************************************************/
  this.editor.replaceRange = function (range, newNode)
  {
    console.debug("EditorReplaceRange");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
  
    if (document.all)
    {
      try
      { 
        range.select();
        range.pasteHTML(newNode.outerHTML);
      }
      catch(e)
      {
        return 0;
      }
      return range;
    }
    else
    {
      var extract = range.extractContents();
      range.insertNode(newNode);
      return range;
    }
  };
  
  /**************************************************************************
 **
 **
 **  EditorGetRange
 **  
 **  This function returns the currently selected range object
 **
 *************************************************************************/
  this.editor.getRange = function ()
  {
    console.debug("EditorGetRange");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
  
    if (document.all)
    {
      editor.editFrame.contentWindow.document.body.focus();
      return editor.editFrame.contentWindow.document.selection.createRange();
    }
    else
    {
      var selection = editor.editFrame.contentWindow.getSelection();
      if (!selection || selection.rangeCount == 0)
        return;
      return selection.getRangeAt(selection.rangeCount - 1).cloneRange();
    }
  };
  
  /**************************************************************************
 **
 **
 **  EditorSetSelectionStyle
 **  
 **  This function sets the supplied style as the style of the current
 **  range.
 **
 *************************************************************************/
  this.editor.setSelectionStyle = function (className)
  {
    console.debug("EditorSetSelectionStyle");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
  
    // We want to remove all of the formatting, but preserve carriage returns if we can.  So
    // lets replace the existing break tags (and paragraph tags) and save them for later.
    // With this replace, replace spaces with non-breaking spaces, otherwise they disappear
    //
    var span   = editor.editFrame.contentWindow.document.createElement("span");
    var range  = null;

    if (document.all)
    {
      range          = editor.getRange();
      span.innerHTML = range.htmlText;
    }
    else
    {
      var selection = editor.editFrame.contentWindow.getSelection();
      if (selection.rangeCount > 0) 
      {
        range               = selection.getRangeAt(0);
        var clonedSelection = range.cloneContents();
        clonedSelection     = span.appendChild(clonedSelection);
      }
    }
  
    span.innerHTML = editor.stripExtraHTML(span.innerHTML);
    span.className = className;

    var cells        = span.getElementsByTagName("td");
    for (var i = 0; i < cells.length; i++)
    {
      cells[i].className = className;
    }

    editor.replaceRange(range, span);
  };
  
  /**************************************************************************
 **
 **
 **  EditorValidateContent
 **  
 **  This function validates and corrects the supplied HTML
 **
 *************************************************************************/
  this.editor.validateContent = function (source)
  {
    console.debug("EditorValidateContent");
    var editor                      = this;
    var childMap                    = new Array();
    var replacementMap              = new Array();
    var replacementPrefix           = "//ATGCHILDREPLACEMENT//";
   
    if (source == "")
      return "";
   
    var testDiv                     = document.createElement("div");
    testDiv.style.display           = "none";
    testDiv                         = editor.appendChild(testDiv);
   
    testDiv.innerHTML               = source;
   
    var children                    = testDiv.childNodes;
   
    for (var i = children.length; i > 0; i--)
    {
      if (children[i-1].nodeType == 1)
      {
        if (children[i-1].innerHTML != "")
        {
          var container                   = document.createElement("div");
          var clone                       = children[i-1].cloneNode(true);
          clone.innerHTML                 = editor.validateContent(clone.innerHTML);
          clone                           = container.appendChild(clone);
          childMap[childMap.length]       = container.innerHTML;
          testDiv.innerHTML               = testDiv.innerHTML.replace(container.innerHTML, replacementPrefix + (childMap.length -1));
        }
      }
    }
    testDiv.innerHTML                   = editor.stripExtraHTML(testDiv.innerHTML);
   
    for (var x = 0; x < childMap.length; x++)
    {
      testDiv.innerHTML = testDiv.innerHTML.replace(replacementPrefix + x, childMap[x]);    
    }  
    
    var stringReturn                            = testDiv.innerHTML; 
    editor.removeChild(testDiv);
    return stringReturn;
  };

/**************************************************************************
 **
 **
 **  EditorStripExtraHTML
 **  
 **  This function validates and corrects the supplied HTML
 **
 *************************************************************************/
  this.editor.stripExtraHTML = function (html)
  {
    console.debug("EditorStripExtraHTML");
    html = html.replace(/<a/g,         "pksi-openanchor").replace(/<A/g,         "pksi-openanchor");
    html = html.replace(/<\/a>/g,       "pksi-closeanchor").replace(/<\/A>/g,       "pksi-closeanchor");
    html = html.replace(/<img/g,         "pksi-openimage").replace(/<IMG/g,         "pksi-openimage");
    html = html.replace(/<p>/g,         "pksi-openpara").replace(/<P>/g,         "pksi-openpara");
    html = html.replace(/<ul>/g,        "pksi-openulist").replace(/<UL>/g,        "pksi-openulist");
    html = html.replace(/<ol>/g,        "pksi-openolist").replace(/<OL>/g,        "pksi-openolist");
    html = html.replace(/<li>/g,        "pksi-openlitem").replace(/<LI>/g,        "pksi-openlitem");
    html = html.replace(/<\/ul>/g,      "pksi-closeulist").replace(/<\/UL>/g,      "pksi-closeulist");
    html = html.replace(/<\/ol>/g,      "pksi-closeolist").replace(/<\/OL>/g,      "pksi-closeolist");
    html = html.replace(/<\/li>/g,      "pksi-closelitem").replace(/<\/LI>/g,      "pksi-closelitem");
    html = html.replace(/<\/p>/g,       "pksi-closepara").replace(/<\/P>/g,       "pksi-closepara");
    html = html.replace(/<br>/g,        "pksi-breaktag").replace(/<BR>/g,        "pksi-breaktag");
    html = html.replace(/<td/g,         "pksi-opencelltag").replace(/<TD/g,         "pksi-opencelltag");
    html = html.replace(/<\/td>/g,      "pksi-closecelltag").replace(/<\/TD>/g,      "pksi-closecelltag");
    html = html.replace(/<tr/g,         "pksi-openrowtag").replace(/<TR/g,         "pksi-openrowtag");
    html = html.replace(/<\/tr>/g,      "pksi-closerowtag").replace(/<\/TR>/g,      "pksi-closerowtag");
    html = html.replace(/<table/g,      "pksi-opentabletag").replace(/<TABLE/g,      "pksi-opentabletag");
    html = html.replace(/<\/table>/g,   "pksi-closetabletag").replace(/<\/TABLE>/g,   "pksi-closetabletag");

    html = html.replace(/<\S[^>]*>/g,   "");

    html = html.replace(/pksi-openanchor/g,      "<a");
    html = html.replace(/pksi-openimage/g,      "<img");
    html = html.replace(/pksi-closeanchor/g,     "</a>");
    html = html.replace(/pksi-openpara/g,        "<p>");
    html = html.replace(/pksi-closepara/g,       "</p>");
    html = html.replace(/pksi-openulist/g,        "<ul>");
    html = html.replace(/pksi-closeulist/g,       "</ul>");
    html = html.replace(/pksi-openolist/g,        "<ol>");
    html = html.replace(/pksi-closeolist/g,       "</ol>");
    html = html.replace(/pksi-openlitem/g,        "<li>");
    html = html.replace(/pksi-closelitem/g,       "</li>");
    html = html.replace(/pksi-breaktag/g,        "<br>");
    html = html.replace(/pksi-opencelltag/g,     "<td");
    html = html.replace(/pksi-closecelltag/g,    "</td>");
    html = html.replace(/pksi-openrowtag/g,      "<tr");
    html = html.replace(/pksi-closerowtag/g,     "</tr>");
    html = html.replace(/pksi-opentabletag/g,    "<table");
    html = html.replace(/pksi-closetabletag/g,   "</table>");
  
    return html;
  };

/**************************************************************************
 **
 **
 **  EditorSetCSS
 **  
 **  This function sets stylesheet source for the editor
 **
 *************************************************************************/
  this.editor.setCSS = function (cssSource)
  {
    console.debug("EditorSetCSS");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    var cssLink               = editor.editDocument.createElement("link");
    cssLink.rel               = "stylesheet";
    cssLink.type              = "text/css";
    cssLink.href              = cssSource;
  
    var head                  = editor.editDocument.getElementsByTagName("head");
    cssLink                   = head[0].appendChild(cssLink);
  };
/**************************************************************************
 **
 **
 **  EditorInsertText
 **  
 **  This function inserts text at the caret position
 **
 *************************************************************************/
  this.editor.insertText = function (text)
  {
    console.debug("EditorInsertText");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    //***********************************************************************
    // Set replacement node
    //
    var range                 = editor.getRange();
  
    if (document.all)
      range.text              = text;
    else
    {
      var insertNode            = editor.editDocument.createTextNode(text);
      editor.replaceRange(range, insertNode);
    }
  };
/**************************************************************************
 **
 **
 **  EditorInsertHTML
 **  
 **  This function inserts text at the caret position
 **
 *************************************************************************/
  this.editor.insertHTML = function (html)
  {
    console.debug("EditorInsertHTML");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    //***********************************************************************
    // Set replacement node
    //
    var insertNode            = editor.editDocument.createElement("div");
    insertNode.innerHTML      = html;
  
    var range                 = editor.getRange();
  
    editor.replaceRange(range, insertNode);
  };
  
  /**************************************************************************
 **
 **
 **  EditorFocus
 **  
 **  This function sets focus to the editor
 **
 *************************************************************************/
  this.editor.focus = function ()
  {
    console.debug("EditorFocus");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    //***********************************************************************
    // focus
    //
    if (editor.editDocument.body.focus)
      editor.editDocument.body.focus();
  };
  
  //***********************************************************************
  // Initialize Editor event handling 
  // We need these and the integration calls to make sure we can pass
  // the correct event object to the integration
  //
  this.editor.handleKeyDown                 = function (event)
  {
    console.debug("EditorHandleKeyDown");
    var editor                                = (this.editor) ? this.editor : this;
    editor.modified                           = true;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);
  
    if (editor)  
    {
      editor.editorModify(event, editor);
      return editor.editorKeyDown(event, editor);
    }
  };
  this.editor.handleKeyPress                = function (event)
  {
    console.debug("EditorHandleKeyPress");
    var editor                                = (this.editor) ? this.editor : this;
    editor.modified                           = true;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorKeyPress(event, editor);
  };
  this.editor.handleClick                   = function (event)
  {
    console.debug("EditorHandleClick");
    var editor                                = (this.editor) ? this.editor : this;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    // Sometimes IE goes haywire on load.  Let's make sure the
    // body is active
    //
    if (document.all)
    {
      var test = editor.getRange();
      test.select();
	}
  };
  this.editor.handleDblClick                = function (event)
  {
    console.debug("EditorHandleDblClick");
    var editor                                = (this.editor) ? this.editor : this;
  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (document.all && editor)
    {
      try
      {
  		var test = editor.getRange();
  		test.expand("word");
  		test.select();
      }
      catch(e)
      {}
    }
  };
  this.editor.handleContextMenu             = function (event)
  {
    console.debug("EditorHandleContextMenu");
    var editor                                = (this.editor) ? this.editor : this;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event); 
    
    if (editor)  
      return editor.editorContextMenu(event, editor);
  };
  this.editor.handleKeyUp                   = function (event)
  {
    console.debug("EditorHandleKeyUp");
    var editor                                = (this.editor) ? this.editor : this;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);
    
    if (editor)  
    {
      editor.editorModify(event, editor);
      return editor.editorKeyUp(event, editor);
    }
  };
  this.editor.handleBlur                    = function (event)
  {
    console.debug("EditorHandleBlur");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)  
      return editor.editorBlur(event, editor);
  };
  this.editor.handleFocus                   = function (event)
  {
    console.debug("EditorHandleFocus");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorFocus(event, editor);
  };
  this.editor.handleMouseMove               = function (event)
  {
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorMouseMove(event, editor);
  };
  this.editor.handleMouseDown               = function (event)
  {
    console.debug("EditorHandleMouseDown");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)  
      return editor.editorMouseDown(event, editor);
  };
  this.editor.handleMouseUp                 = function (event)
  {
    console.debug("EditorHandleMouseUp");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorMouseUp(event, editor);
  };
  this.editor.handleDrop                    = function (event)
  {
    console.debug("EditorHandleDrop");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)  
      return editor.editorDrop(event, editor);
  };
  this.editor.handlePaste                   = function (event)
  {
    console.debug("EditorHandlePaste");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)
    {  
      editor.editorModify(event, editor);
      return editor.editorPaste(event, editor);
    }
  };
  this.editor.handleResize                  = function (event)
  {
    console.debug("EditorHandleResize");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);
  
    if (editor)  
      return editor.editorResize(event, editor);
  };
  
  //***********************************************************************
  // Initialize Editor integration calls
  //
  this.editor.editorClick                   = new Function();
  this.editor.editorContextMenu             = new Function();
  this.editor.editorKeyDown                 = new Function();
  this.editor.editorKeyUp                   = new Function();
  this.editor.editorKeyPress                = new Function();
  this.editor.editorBlur                    = new Function();
  this.editor.editorFocus                   = new Function();
  this.editor.editorModify                  = new Function();
  this.editor.editorMouseMove               = new Function();
  this.editor.editorMouseDown               = new Function();
  this.editor.editorMouseUp                 = new Function();
  this.editor.editorPaste                   = new Function();
  this.editor.editorDrop                    = new Function();
  this.editor.editorResize                  = new Function();
  
  //***********************************************************************
  // Initialize Editor element structure
  // 
  var editIframe                            = document.createElement("iframe");
  editIframe.src                            = initialTemplate;
  
  if (editIframe.addEventListener) {
    editIframe.addEventListener("load", onLoadHandler, false)
  }
  else if (editIframe.attachEvent) {
    editIframe.detachEvent("onload", onLoadHandler)
    editIframe.attachEvent("onload", onLoadHandler)
  }
  
  this.editor.editFrame                     = this.editor.appendChild(editIframe);
  this.editor.editFrame.editor              = this.editor;
  this.editor.editFrame.height              = "200";
  this.editor.editFrame.width               = "100%";
  this.editor.editFrame.border              = "0";  
  this.editor.editFrame.frameBorder         = "0";  
  
  return this.editor;                                     
}

function getEditor(event)
{
  console.debug("getEditor");
  if (event) {
    if (event.target) {
      if (event.target.body) {
        return document.getElementById(event.target.body.id);
      } else {
        return document.getElementById(event.target.ownerDocument.body.id);
      }
    }
  }   
}
 /* Copyright (C) 1999-2009 Art Technology Group, Inc.
 */
/*************************************************************************
// environment.js File
// Defines client-side environment behavior
*************************************************************************/
// dojo.declare doesn't work in this file in IE for some bizarre reason
dojo.getObject("atg.service.environment", true);

atg.service.environment.showChangePrompt = function(confirmPromptUrl, repostParams) {
  if (!confirmPromptUrl) {
    console.debug("No confirmPromptURL specified for environment change: Unable to render pane contents.");
    return;
  }

  for (key in repostParams) { // don't submit duplicate forms
  	console.debug("showChangePrompt repostParams Key " + key);
  }



  var pane = atg.service.ticketing.createFloatingPane({
    id: "envChangePane",
    titleBarDisplay:false,
    cacheContent:false,
    executeScripts:true,
    scriptHasHooks:true,
    adjustPaths:false,
    extractContent:false,
    href: window.contextPath + "/" + confirmPromptUrl + "?_windowid="+window.windowId
  }, 430, 470);
  pane.containerNode.className = "atg_svc_Ticket_Disposition_Client";
  pane.repostParams = repostParams;
  pane.onLoad = function () {
    dojo.byId("warningsOk").focus();
  };
  pane.show();
}

atg.service.environment.acceptChangePrompt = function() {
  var theForm = dojo.byId("envChangeForm");
  var repostParams = dijit.byId("envChangePane").repostParams;
  for (key in repostParams) { // don't submit duplicate forms
    if (theForm[key]) {
      repostParams[key] = null;
      delete repostParams[key];
    }
  }
  atgSubmitAction({
    form: theForm,
    queryParams : repostParams
  });  
  dijit.byId("envChangePane").hide();
  dijit.byId("envChangePane").destroy();
}

atg.service.environment.cancelChangePrompt = function() {
  dijit.byId("envChangePane").hide();
  dijit.byId("envChangePane").destroy();
  atg.service.reloadResult();
}
function showStackTrace() {
  var stackTraceObject = dojo.byId("errorDetails");
  var imgArrowClosed = dojo.byId("imgErrorDetailArrowClosed");
  var imgArrowDown = dojo.byId("imgErrorDetailArrowDown");

  if (stackTraceObject != null)
  {
    if (isShowing(stackTraceObject))
    {
      show(imgArrowClosed);
      hide(imgArrowDown);
      hide(stackTraceObject);
    }
    else
    {
      hide(imgArrowClosed);
      show(imgArrowDown);
      show(stackTraceObject);
    }
  }
}
function hideErrorPanel() {
  var errorPanelContent = dojo.byId("errorPanelContent");
  if (errorPanelContent)
    errorPanelContent.innerHTML = "";

  var errorPanel = dojo.byId("errorPanel");
  if (errorPanel)
    hide(errorPanel);
}
function hideErrorDetails(id) {
  var errorPanel = dojo.byId(id);
  hide(errorPanel);
}/*************************************************************************
//
// event.js File
//
// (C) Copyright 1997-2004 Primus Knowledge Solutions, Inc.
// All rights reserved.
//
*************************************************************************/

var g_objCurrentEvent = new EventContainer();

function EventContainer()
{
  // Initialize Properties
  //
  this.event       = "";
  this.xCoordinate  = "";
  this.yCoordinate  = "";
  this.windowObject = "";

  // Initialize Methods
  //
  this.saveEvent    = EventContainerSaveEvent;
}

function EventContainerSaveEvent(e, xPos, yPos, nLeftOffset, nTopOffset)
{
  this.event = (e) ? e : window.event;

  g_objCurrentEvent.xCoordinate = xPos + nLeftOffset;
  g_objCurrentEvent.yCoordinate = yPos + nTopOffset;

  g_objCurrentEvent.windowObject   = this;
  g_objCurrentEvent.event          = this.event;   
}
dojo.provide("atg.service.form");

atg.service.form.isFormEmpty = function (theFormId, trimInputs) {
  //console.debug("isFormEmpty called");
  var elements = dojo.query("input", theFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type == "text" || type == "textarea" || type == "password") {
      //console.debug("found a text input value of " + item.value);
      var itemValue = trimInputs ? dojo.string.trim(item.value) : item.value;
      if (itemValue != '') {return false;}
    }
    else if (type == "checkbox" || type == "radio") {
      //console.debug("found a check/radio input value of " + item.checked);
      if (item.checked == true) {return false;}
    }
  };
  var elements = dojo.query("select", theFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type.match("select") == "select") {
      //console.debug("found a select input value of " + item.value);
      if (item.value != '') {return false;}
    }
  };
  var elements = dojo.query("textarea", theFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type.match("textarea") == "textarea") {
      //console.debug("found a textarea input value of " + item.value);
      if (item.value != '') {return false;}
    }
  };
  //console.debug("found all empty fields, returning true");
  return true;
};

atg.service.form.watchInputs = function (theFormId, theFunction) {
  var theForm = dojo.byId(theFormId);
  var atgWatchEvents = [];
  var elements = dojo.query("input", theFormId);
  elements.forEach(function (item,index,array) {
    var type=item.type;
    if (type == "text" || type == "textarea" || type == "password") {
      atgWatchEvents.push(dojo.connect(item, "onkeyup", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onchange", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
    else if (type == "checkbox") {
      atgWatchEvents.push(dojo.connect(item, "onclick", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
    else if (type == "radio") {
      atgWatchEvents.push(dojo.connect(item, "onclick", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
  });
  elements = dojo.query("select", theFormId);
  elements.forEach(function (item,index,array) {
    var type=item.type;
    if (type.match("select") == "select") {
      atgWatchEvents.push(dojo.connect(item, "onchange", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
  });
  elements = dojo.query("textarea", theFormId);
  elements.forEach(function (item,index,array) {
    var type=item.type;
    if (type.match("textarea") == "textarea") {
      atgWatchEvents.push(dojo.connect(item, "onkeyup", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onchange", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
  });
  theForm.atgWatchEvents = atgWatchEvents;
};

atg.service.form.unWatchInputs = function (theFormId) {
  var theForm = dojo.byId(theFormId);
  if (theForm) {
    var watchedInputs = theForm.atgWatchEvents;
    if (watchedInputs) {
      console.debug("removing the watching of the form elements")
      dojo.forEach(watchedInputs, dojo.disconnect);
      theForm.atgWatchEvents = null;
    }
  }
};

// checks maxlenght attribute for textarea
atg.service.form.checkMaxLength = function (obj){
  var mlength=obj.getAttribute ? parseInt(obj.getAttribute("maxlength")) : "";
  if (obj.getAttribute && obj.value.length > mlength){
    obj.value=obj.value.substring(0, mlength);
    obj.scrollTop = obj.scrollHeight;
    return false;
  }
  return true;
};/* Copyright (C) 1999-2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */

/*************************************************************************
 *
 * framework.js File
 * 
 * (C) Copyright 1997-2010 ATG, Inc.
 * All rights reserved.
 * 
 * Defines client-side framework behavior
 *
 *************************************************************************/
dojo.provide("atg.service.framework");

function computeTab(op, ticketId) {
  if (op) {
    if (op == 'viewTicket') {
      document.getElementById('globalViewTicketForm').ticketId.value = ticketId;
      viewTicket('globalViewTicketForm');
    }
    else if (op == 'workTicket') {
      workTicket("workTicketNoSwitchForm", ticketId);
    }
    else if (op == 'reassignTicket') {
      document.getElementById('globalViewTicketForm').ticketId.value = ticketId;
      viewTicket('globalViewTicketForm');
      /* TODO: needs to call javascript for select agent popup which does not exist yet */
    }
    else if (op == 'escalateTicket') {
      document.getElementById('globalViewTicketForm').ticketId.value = ticketId;
      viewTicket('globalViewTicketForm');
      /* TODO: needs to call javascript for escalate popup which does not exist yet */
    }
    else if (op == 'sendTicketToGroup') {
      document.getElementById('globalViewTicketForm').ticketId.value = ticketId;
      viewTicket('globalViewTicketForm');
      /* TODO: needs to call javascript for send to group popup which does not exist yet */
    }
    else if (op == 'deferTicket') {
      document.getElementById('globalViewTicketForm').ticketId.value = ticketId;
      viewTicket('globalViewTicketForm');
      /* TODO: needs to call javascript for defer popup which does not exist yet */
    }
    else if (op == 'releaseTicket') {
      document.getElementById('globalViewTicketForm').ticketId.value = ticketId;
      viewTicket('globalViewTicketForm');
      /* TODO: needs to call javascript for release popup which does not exist yet */
    }
    else if (op == 'initialSearch') {
      initialSearch();
    }
    else {
      // do nothing
    }
  }
}

function unloadPanel(panelId) {
  var panelContainer = dojo.byId(panelId);
  if (panelContainer) { 
    if (panelContainer.panelunload) eval(panelContainer.panelunload + "()"); 
  }
};

atg.service.framework.bindListener = function() {
  console.debug("heard bind, resetting session timer");
  if (atg.service.framework.sessionExpiryTimer) {
    atg.service.framework.myLastRequestTime = new Date().getTime().toString();
    console.debug("setting last request cookie to " + atg.service.framework.myLastRequestTime);
    dojo.cookie("atg.allwindows.lastrequest", atg.service.framework.myLastRequestTime, null, null, null, null);
    // reset the timer, since we've reset the session timer on the server
    clearTimeout(atg.service.framework.sessionExpiryTimer);
    atg.service.framework.sessionExpiryTimer = setTimeout(atg.service.framework.sessionExpiryPromptFunction, atg.service.framework.sessionTimeout);
    if (atg.service.framework.sessionDisplayLoginTimer) {
      clearTimeout(atg.service.framework.sessionDisplayLoginTimer);
      atg.service.framework.sessionDisplayLoginTimer = null;
    }
  }
};

atg.service.framework.sessionExpired = function () {
  console.debug("session is now timed out, logging out");
  dijit.byId('atgSessionTimeoutDialog').hide();
  dojo.byId("logout").doWarnings.value='false';
  dojo.byId("logout").doPrompt.value='false';
  atg.service.logout();
};

atg.service.framework.sessionExpiryPromptFunction = function() {
  // only run if this was the last window to communicate with the server
  var allWindowsLastRequestTime = dojo.cookie("atg.allwindows.lastrequest");
  console.debug("last request cookie is " + allWindowsLastRequestTime + " my request time is " + atg.service.framework.myLastRequestTime);
  if (allWindowsLastRequestTime !== "" && allWindowsLastRequestTime === atg.service.framework.myLastRequestTime) {
    console.debug("showing the warning dialog");
    atg.service.framework.sessionDisplayLoginTimer = setTimeout(atg.service.framework.sessionExpired, atg.service.framework.preSessionExpiryWarningSeconds * 1000);
    dijit.byId("atgSessionTimeoutDialog").lifetime = atg.service.framework.preSessionExpiryWarningSeconds * 1000;
    dijit.byId("atgSessionTimeoutDialog").show();
  }
  else {
    console.debug("times don't match so won't show the dialog in this window " + windowId);
  }
};

atg.service.framework.isResearchBarOpen = false;
atg.service.framework.isSideBarOpen = true;
atg.service.framework.showResearch = false;
atg.service.framework.cancelEvent = function(event) {
  dojo.stopEvent(event);
  return false;
};
atg.service.framework.getClientX = function (event) {
  var x = 0;
  var e;
  e = (event) ? event : window.event;
  x = e.clientX;
  return x;
};
atg.service.framework.getClientY = function (event) {
  var y = 0;
  var e = (event) ? event : window.event;
  y = e.clientY;
  return y;
};

/** Public API - Do not change interface! **/
atg.service.framework.changeTab = function (tabId) {
  var bTest = saveState();
  
  var tab = dojo.byId(tabId);
  if (!tab) {
    return tabId;
  }

  // Already on this tab? If yes, return immediately
  if (dojo.hasClass(tab, "current")){
    return tabId;
  }

  var tabset = atg.service.framework.findAncestorByTagName(tab, "ul", false);
  if (!tabset){
    return tabId;
  }
    
  if (tabset) {
    var tabs = tabset.getElementsByTagName("li");
    for (var i = 0; i < tabs.length; i++) { //>
      dojo.removeClass(tabs[i], "current");
    }
  }
  dojo.addClass(tab,"current");

  // Show research column?
  atg.service.framework.showResearch = false;
  if ((tabId == "contributeTab") && atg.service.framework.researchColumnAccess) {
    atg.service.framework.showResearch = true;
  }

  return tabId;
};

/** Public API - Do not change interface! **/
atg.service.framework.toggleSidebar = function () {
  frameworkCellAction("toggleCell", "sidebarColumn");
  if (atg.service.framework.isSideBarOpen) {
    atg.service.framework.isSideBarOpen = false;
  } else {
    atg.service.framework.isSideBarOpen = true;
  }
  frameworkSetColumnStyles();
};

/** Public API - Do not change interface! **/
atg.service.framework.toggleResearch = function () {
  frameworkCellAction("toggleCell", "researchColumn");
  if ((atg.service.framework.showResearch && atg.service.framework.isResearchBarOpen) && (!window.createSolutionFromDocument)) {
    changeResearchColumnPanels();
  }
  if (!atg.service.framework.isResearchBarOpen) {
    atg.service.framework.showResearch = true;

    // required cleanup before opening research column if we had
    // some search activities within research column while it was open

    var searchText=window.searchText;
    if(searchText) {
      searchText.value="";
      searchText.mode="";
      var srchTxtElmInResearch = document.getElementById("QuickFindSearchTextID");
      if(srchTxtElmInResearch){
        srchTxtElmInResearch.value="";
      }
    }
  }
};

atg.service.framework.findAncestorByTagName = function (currentElement, ancestorTagName, includeSelf) {
  var ancestor = null;
  if (includeSelf) {
    ancestor = dojo.html.getParentByType(currentElement, ancestorTagName);
  }
  if (!ancestor) {
    var parent = currentElement.parentNode;
    ancestor = dojo.html.getParentByType(parent, ancestorTagName);
  }
  return ancestor;
};

/** Public API - Do not change interface! **/
atg.service.framework.selectTabbedPanel = function (panelId, nextStepsId, nextStepsPanelId) {
	// Make sure we save state of displayed panel
  //
  var bTest = saveState();	
  if (nextStepsPanelId) {
	  atgSubmitAction({
	    form: document.getElementById("selectTabbedPanel"),
	    nextSteps: nextStepsId,
	    panels: [nextStepsPanelId],
	    selectTabbedPanels: [panelId]
	  });
  }
  else {
	  atgSubmitAction({
	    form: document.getElementById("selectTabbedPanel"),
	    selectTabbedPanels: [panelId]
	  });
  }
	
	var splitContainer = dijit.byId("contentWrapper");
	if (splitContainer.getChildren() && splitContainer.getChildren()[1]) 
	{ // splitter is valid
	  splitContainer.layout();
	}
};
/** Public API - Do not change interface! **/
atg.service.framework.togglePanel = function (panelId) {
	frameworkPanelAction("togglePanel", panelId, "helpfulPanels");
};
/** Public API - Do not change interface! **/
atg.service.framework.togglePanelsToTabs = function(panelId, panelStackId) {
	frameworkPanelAction("togglePanelsToTabs", panelId, panelStackId);
};
/** Public API - Do not change interface! **/
atg.service.framework.togglePanelContent = function (panelId) {	
  //Remove functionality to expand and contract the split panel
	/*
  
  var theForm = document.getElementById("togglePanelContent");
  atgSubmitAction({
    form: theForm,
    showLoadingCurtain: false,
    panels: [panelId],
    panelStack: ""
  });
	
	if(panelId == "customerMainPanel") {		
	  var splitContainer = dijit.byId("contentWrapper");
	  if (splitContainer.getChildren() && splitContainer.getChildren()[1]) 
	  { // splitter is valid
      //if it's expanded, colapse it
      if(atg.service.framework.globalTicketsPanelExpanded)
      {
        //make the main content panel min size the size of the portal - the size of the ticket panel
        splitContainer.getChildren()[1].sizeMin = splitContainer.paneHeight;
        splitContainer.getChildren()[1].sizeShare = splitContainer.paneHeight;
        atg.service.framework.globalTicketsPanelExpanded = false;
      }
      else
      {
        splitContainer.getChildren()[1].sizeMin = splitContainer.paneHeight - 350;
        splitContainer.getChildren()[0].sizeShare = 345;
        splitContainer.getChildren()[1].sizeShare = splitContainer.paneHeight - 350;
        atg.service.framework.globalTicketsPanelExpanded = true;
      }  
	  }
	  splitContainer.layout();
	} 
	*/
};

atg.service.framework.togglePanelContentNoRefresh = function (panelId) { 	
  var theForm = document.getElementById("togglePanelContent");
	atgSubmitAction({
    form: theForm,
    showLoadingCurtain: false,
    panels: [panelId],
    panelStack: "",
    url: window.contextPath + "/framework.jsp?showHideContent=true"
  }).addCallback(function() {dojo.publish("/togglePanelContent", [panelId]);});
}

dojo.subscribe("/agent/globalPanelLoaded", function(panelLoadParams){atg.service.framework.initResizeContainer(panelLoadParams);});

atg.service.framework.globalTicketsPanelExpanded = false;

//worksout the height of the first child in the split container, then works out the size the scroll bar 
//needs to be
atg.service.framework.resizeGlobalScrollPanel = function()
{
  var splitContainer = dijit.byId("contentWrapper");
  
  var newHeight = splitContainer.getChildren()[0].height - 5;

  //if it's on the activities pane, the resultBox will be there
  if(dojo.byId("resultBoxTopPane"))
  {
    //resize the result box
    dojo.byId("resultBoxTopPane").style.height = newHeight + "px";
  }
  //otherwise resize the customer panel
  else if(dojo.byId("mainTicketCustomerPanel"))
  {
    dojo.byId("mainTicketCustomerPanel").style.height = newHeight + "px";
  }
};


//initialise the panel load parameter
atg.service.framework.initResizeContainer = function(panelLoadParams)
{
  atg.service.framework.globalTicketsPanelExpanded = panelLoadParams.isOpen;
};

atg.service.framework.initSlider = function()
{

  var splitContainer = dijit.byId("contentWrapper");
  if (splitContainer && splitContainer.getChildren() && splitContainer.getChildren()[1]) { // splitter is valid
    splitContainer.getChildren()[0].sizeMin = 0;
    splitContainer.getChildren()[0].sizeShare = 0;
  
    splitContainer.sizers[0].style.zIndex = "1";
    
    if(!atg.service.framework.globalTicketsPanelExpanded)
    {
      //make the main content panel min size the size of the portal - the size of the ticket panel
      splitContainer.getChildren()[1].sizeMin = splitContainer.paneHeight;
      splitContainer.getChildren()[1].sizeShare = splitContainer.paneHeight;
    }
    else
    {
      splitContainer.getChildren()[1].sizeMin = splitContainer.paneHeight - 350;
      splitContainer.getChildren()[0].sizeShare = 345;
      splitContainer.getChildren()[1].sizeShare = splitContainer.paneHeight - 350;
    }
  
    splitContainer.layout();
    dojo.connect(window, "onresize", setMainContentMinHeight);
  }
  
};

function setMainContentMinHeight()
{
  var splitContainer = dijit.byId("contentWrapper");
  if (splitContainer.getChildren() && splitContainer.getChildren()[1]) { // splitter is valid
    
    if(!atg.service.framework.globalTicketsPanelExpanded)
    {
      //make the main content panel min size the size of the portal - the size of the ticket panel
      splitContainer.getChildren()[1].sizeMin = splitContainer.paneHeight;
      splitContainer.getChildren()[1].sizeShare = splitContainer.paneHeight;
      splitContainer.layout();
    }
    else
    {
      splitContainer.getChildren()[1].sizeMin = splitContainer.paneHeight - 350;
      splitContainer.getChildren()[1].sizeShare = splitContainer.paneHeight - 350;
      splitContainer._moveSlider(splitContainer.sizers[0], "350", splitContainer.sizerWidth);
    }
  }
}

/** Public API - Do not change interface! **/
atg.service.framework.startCall = function ()
{
  startNewCall();
};
/** Public API - Do not change interface! **/
atg.service.framework.endCall = function ()
{
  endCurrentCall();
};
/** Public API - Do not change interface! **/
atg.service.framework.endAndStartCall = function ()
{
  endAndStartNewCall();
};

// Needed for validation of email addresses 
dojo.require("dojox.validate.web");dojo.provide("atg.data");
dojo.require('dojox.grid._data.model');

dojo.declare("atg.data.FormhandlerData", dojox.grid.data.Dynamic, { 
	constructor: function(inFields, inUrl) {
		this.server = inUrl;
		this.status = null;
		this.sortField = '';
		this.sortIndex = 0;
    this.formId = null;
    this.formCurrentPageField = "currentResultPageNum";
    this.formSortPropertyField = "sortProperty";
    this.formSortDirectionField = "sortDirection";
	},
	clearData: function() {
		this.cache = [ ];
		this.status = null;
		this.inherited(arguments);
	},
	setStatus: function(inStatus) {
		this.status = inStatus;
	},
	canModify: function() {
		return (!this.status);
	},
	// server send / receive
	send: function(inAsync, inParams, inCallbacks) {
	  if (this.formId !== null && this.formId !== "") {
        console.debug("Grid send called with form " + this.formId);
        if (this.table) {
          console.debug("setting sort fields on the form");
          this.setFormSortProperty(this.sortField, this.sortDesc);
        }
        if (inParams.offset) {
          this.setCurrentPageNumber(inParams.offset);
        }
        else {
          this.setCurrentPageNumber(0);
        }
        var contentMap = {};
        contentMap["atg.formHandlerUseForwards"] = true;
        contentMap._windowid = window.windowId;
        if (window.requestid) { contentMap._requestid = window.requestid;}
  
        var deferred = 
        dojo.xhrPost({
          content: contentMap,
          form: dojo.byId(this.formId),
          url: this.server,
          encoding: "utf-8",
          timeout: atgXhrTimeout,
          handleAs: "json",
          sync: !inAsync
        });
        deferred.addCallbacks(dojo.hitch(this, "receive", inCallbacks), dojo.hitch(this, "receiveError", inCallbacks));
        var mb = dijit.byId("messageBar");
        if (mb) {
          deferred.addCallback(function() { dijit.byId("messageBar").retrieveMessages();});
        }
      }
      else {
        console.debug("skipping send because the needed properties aren't set up yet");
      }
      return deferred;
	},
	_callback: function(cb, eb, data) {
		try{ cb && cb(data); } 
		catch(e){ eb && eb(data, e); }
	},
	receive: function(inCallbacks, inData) {
		inCallbacks && this._callback(inCallbacks.callback, inCallbacks.errback, inData);
	},
	receiveError: function(inCallbacks, inErr) {
		this._callback(inCallbacks.errback, null, inErr)
	},
	encodeRow: function(inParams, inRow) {
		for (var i=0, l=inRow.length; i < l; i++) {
			inParams['_' + i] = (inRow[i] ? inRow[i] : '');
		}
	},
	fetchRowCount: function(inCallbacks) {
		this.send(true, { command: 'count' }, inCallbacks );
	},
	requestRows: function(inRowIndex, inCount)	{
		var params = { 
			orderby: this.sortField, 
			desc: (this.sortDesc ? "true" : ''),
			offset: inRowIndex, 
			limit: inCount
		};
		this.send(true, params, {callback: dojo.hitch(this, this.rows, inRowIndex)});
	},
	// sorting
	canSort: function (inSortIndex) { 
	  var field = this.fields.get(Math.abs(inSortIndex) - 1);
	  if (field.canSort) {
	    return field.canSort;
	  }
	  else {
	    return false;
	  }
	},
	sort: function(inSortIndex) {
		this.sortField = this.fields.get(Math.abs(inSortIndex) - 1).name;
		this.sortDesc = (inSortIndex < 0);
		if (this.formId) {
		  var form = dojo.byId(this.formId);
		  if (form) {
		    if (form[this.formSortPropertyField]) {
		      form[this.formSortPropertyField].value = this.fields.get(Math.abs(inSortIndex) - 1).property;
		      form[this.formSortDirectionField].value = (inSortIndex < 0) ? "desc" : "asc";
		    }
		  }
		}
		this.clearData();
	},
	setCurrentPageNumber: function(inRowIndex) {
	  console.debug("need row index " + inRowIndex);
	  var currentPage = Math.floor(inRowIndex / this.rowsPerPage);
	  console.debug("asking for page " + currentPage);
	  var form = dojo.byId(this.formId);
    if (form) {
      if (form[this.formCurrentPageField]){
	      form[this.formCurrentPageField].value = currentPage;
	    }
	  }
	},
	// server callbacks (called with this == model)
	update: function(inRowIndex, inData) {
		if (inData.error) {
			this.updateError(inData);
		}
		else {
			this.setStatus(null);
			var d = (inData&&inData[0]);
			if (d) {
				this.setRow(d, inRowIndex);
			}
		}
	},
	updateError: function(inRowIndex) {
		this.setStatus(null);
		this.change(inRowIndex);
		alert('Update error. Please refresh.');
	},
	rows: function (inRowIndex, inData) {
    for (var i=0, l=inData.results.length; i<l; i++) {
      var value = inData.results[i];
      this.setRow(value, inRowIndex + i);
    }
	}
	
});dojo.getObject("atg.service", true);

atg.service.openWindow = function() {
  window.isLogout=false;
  initTab();
}
//Code added for a fix to the bug 141105
atg.service.logout = function() {
  window.isLogout=true;
  atgSubmitAction({
    form: dojo.byId('logout'),
    queryParams: {sessioninvalid: true, ppr: true}
  });
  dijit.byId("logoutBox").hide();
  return false;
}
//Code added for a fix to the bug 141105
atg.service.showLogout = function() {
  var ifrm = dojo.byId("ifrmSolution");
  if(ifrm) {
    ifrm.style.display = 'none';
  }
  if (window.confirmLogout) {
    dijit.byId("logoutBox").show();
    dojo.byId("logoutYes").focus();
  }
  else {
    atg.service.logout();
  }
}
atg.service.showUtilities = function()
{
  var util = getElt("utilDD");
  if (util.style.display == "none")
  { 
    divSetVisible("utilDD");
  }
  else
  {
    divSetHide("utilDD");
  }
}

atg.service.popup = function (url, name, width, height) {
  atg_popupSettings="toolbar=no,location=no,directories=no,"+
  "status=no,menubar=no,scrollbars=yes,"+
  "resizable=no,width="+width+",height="+height;
  atg_myNewWindow=window.open(url,name,atg_popupSettings);
}

atg.service.reloadResult = function() {
  dijit.byId("logoutBox").hide();
  var ifrm = dojo.byId("ifrmSolution");
  if(ifrm) {
    ifrm.style.display = "block";
  }
  if (dojo.isMozilla && ifrm) {
    ifrm.src = ifrm.src;
  }
}

var tid, time, action;
atg.service.setInactivityTimer = function(time, action) {
  window.time = time;
  window.action = action;
  if (tid) {clearTimeout(tid);}
  if (document.layers) {document.captureEvents(Event.MOUSEMOVE | Event.KEYUP);}
  document.onmousemove = document.onkeyup = function (evt) {
    atg.service.resetInactivityTimer();
    return true;
  };
  action += '; atg.service.clearEvents();';
  tid = setTimeout(action, time);
}
atg.service.resetInactivityTimer = function()
{
  if (tid) {clearTimeout(tid);}
  tid = setTimeout(action, time);
}
atg.service.clearEvents = function() {
  if (document.layers) {document.releaseEvents(Event.MOUSEMOVE | Event.KEYUP);}
  document.onmousemove = document.onkeyup = null;
}
function showDebugConsole() {

}
var atgLoadingDialog;
function atgInitLoadingDialog(e){
  atgLoadingDialog = dijit.byId("atgLoadingDialogWidget");
}


function atgSetupHelp(locationSet)
{
  dojo.forEach(locationSet, function(helpItem) {
   //dojo.removeClass(dojo.byId(helpItem.id), "hideHelp");
	 //dojo.addClass(dojo.byId(helpItem.id), "showHelp");
   if (dojo.hasClass(dojo.byId(helpItem.id), "popupHelp"))
   {
     atgSetupPopup(helpItem);
   }
   else if (dojo.hasClass(dojo.byId(helpItem.id), "inlineHelp")) {
     atgSetupInline(helpItem);
   }
 }, true);
}
function atgSetupPopup(helpItem)
{
  var theHref = helpItem.url;
  var newImage = document.createElement("img");
  newImage.src = "images/icon_help_authoring.gif";
  newImage.id = helpItem.id + "HelpImg";
  dojo.place(newImage, dojo.byId(helpItem.id), "after");
  var widget = new dijit.Tooltip({connectId: helpItem.id + "HelpImg", href: theHref});
}
function atgSetupInline(helpItem)
{
	var sibDiv = dojo.byId(helpItem.id);
  var newImage = document.createElement("img");
  newImage.src = "images/icon_help_agent.gif";
  newImage.id = helpItem.id + "HelpImg";
  dojo.place(newImage, sibDiv, "after");
	var widget = new dojox.layout.ContentPane({href: helpItem.url, isHidden: true, parseOnLoad: true});
	hide(widget.domNode, false);
	dojo.place(widget.domNode, dojo.byId(newImage.id), "after");
	console.debug("setting onclick on " + sibDiv.firstChild);
	dojo.connect(sibDiv.firstChild, "onclick", function() {
	  console.debug("toggling");
	  toggleShowing(widget.domNode);
	});
}// -------------------------------------------------------------------
// Imports
// -------------------------------------------------------------------
dojo.provide("atg.keyboard");

dojo.require("dojox.collections.SortedList");

// -------------------------------------------------------------------
// Constants
// -------------------------------------------------------------------
atg.keyboard._keyCtrl = "CTRL";
atg.keyboard._keyAlt = "ALT";
atg.keyboard._keyShift = "SHIFT";
atg.keyboard._keySeparator = "+";
atg.keyboard._classTopLevelIdentifier = "atg_keyboard_top_level_identifier";
atg.keyboard._classPanelIdentifier = "atg_keyboard_panel_identifier";
atg.keyboard._panelTitleIdentifier = "panel";
atg.keyboard._minimizeIdentifier = "icon_minimize";
atg.keyboard._restoreIdentifier = "icon_maximize";
atg.keyboard._dockIdentifier = "icon_arrow";
atg.keyboard._undockIdentifier = "icon_arrow";


atg.keyboard._tabPressed = false;
atg.keyboard._showNotificationWindow = true;
atg.keyboard._highlightAndFadePanels = true;
atg.keyboard._highlightAndFadeNodes = true;

// -------------------------------------------------------------------
// Keyboard navigation help window
// -------------------------------------------------------------------
atg.keyboard.keyboardNavShortcuts = [];
atg.keyboard.keyboardNavHelpWindowContents = [];
atg.keyboard.keyboardNavHelpWindow = null;
atg.keyboard.keyboardNavHelpWindowDiv = null;
atg.keyboard.keyboardNavNotificationWindow = null;
atg.keyboard.keyboardNavCurrentNode = "";
  
// -------------------------------------------------------------------
// Keyboard navigation windows
// -------------------------------------------------------------------
atg.keyboard.keyboardNavHelpMessage = null;
atg.keyboard.keyboardNavNotificationMessage = null;

// -------------------------------------------------------------------
// Keycode special character lookup for IE
// -------------------------------------------------------------------
atg.keyboard.lookupKeyCodeIE = {
  "112": "F1",
  "113": "F2",
  "114": "F3",
  "115": "F4",
  "116": "F5",
  "117": "F6",
  "118": "F7",
  "119": "F8",
  "120": "F9",
  "121": "F10",
  "122": "F11",
  "123": "F12",
  "189": "-",
  "187": "=",
  "219": "[",
  "221": "]",
  "220": "\\",
  "186": ";",
  "222": "'",
  "188": ",",
  "190": ".",
  "191": "/",
  "192": "`",
  "37": "LEFT",
  "37": "UP",
  "37": "RIGHT",
  "37": "DOWN",
  "8": "BACKSPACE"
};

// -------------------------------------------------------------------
// Keycode special character lookup for Mozilla
// -------------------------------------------------------------------
atg.keyboard.lookupKeyCodeMozilla = {
  "112": "F1",
  "113": "F2",
  "114": "F3",
  "115": "F4",
  "116": "F5",
  "117": "F6",
  "118": "F7",
  "119": "F8",
  "120": "F9",
  "121": "F10",
  "122": "F11",
  "123": "F12",
  "109": "-",
  "61": "=",
  "219": "[",
  "221": "]",
  "220": "\\",
  "59": ";",
  "222": "'",
  "188": ",",
  "190": ".",
  "191": "/",
  "192": "`",
  "37": "LEFT",
  "37": "UP",
  "37": "RIGHT",
  "37": "DOWN",
  "8": "BACKSPACE"
};

// -------------------------------------------------------------------
// Keyboard navigation initialization
// -------------------------------------------------------------------
atg.keyboard.init = function () {
  console.debug("Keyboard Nav | Keyboard shortcut initialization function called");
  atg.keyboard.keyboardNavHelpWindow = dijit.byId("keyboardNavHelpObject");
  atg.keyboard.keyboardNavHelpWindowDiv = dijit.byId("keyboardNavHelpObjectDiv");
  atg.keyboard.keyboardNavNotificationWindow = dijit.byId("keyboardNavNotificationObject");
  
  // Set up the event listener for keyboard navigation on the document object
  if(dojo.isIE || dojo.isSafari) {
    dojo.connect(dojo.doc, "onkeyup", atg.keyboard.activateKeyboardShortcut);
  }
  else {
    dojo.connect(dojo.doc, "onkeypress", atg.keyboard.activateKeyboardShortcut);
  }
  
  // Set up the event listener for tab highlighting
  dojo.connect(dojo.doc, "onfocus", atg.keyboard.activateTabHighlight);
  
  // Loop through all the UI elements that support tab highlighting and attach listeners
  // Comment out as we're assuming all items support tab highlighting unless explicitly set to -1
  /*
  var _uiListeners = dojo.query(".atg_navigationHighlight");
  console.debug(_uiListeners.length + " Navigation Highlight Listeners Found");
  for (_count=0; _count<_uiListeners.length; _count++){
    dojo.connect(_uiListeners[_count],"onfocus", atg.keyboard, "highlightAndFade"); 
  }
  */
  
};

// -------------------------------------------------------------------
// Keyboard shortcut event controller
// -------------------------------------------------------------------
atg.keyboard.activateKeyboardShortcut = function (e) {
  var _keyboardShortcutKey;
  var _keyboardShortcutObjects = [];
  var _topic;
  var _action;
  
  if (atg.keyboard.isTabPressed(e) == true) {
    atg.keyboard.setTab();
  }
  else 
    if (atg.keyboard.isModifierPressed(e) == true) {
    atg.keyboard.keyboardNavCurrentNode = e.target;

    _keyboardShortcutKey = atg.keyboard.getShortcutKeyPressed(e);
    console.debug("Keyboard Nav | Shortcut key pressed: " + _keyboardShortcutKey);
    
    _keyboardShortcutObjects = atg.keyboard.getKeyboardNavShortcuts(_keyboardShortcutKey);
    if ((_keyboardShortcutObjects != null) && (_keyboardShortcutObjects.length > 0)) {
      
      if (atg.keyboard._showNotificationWindow == true) {
        // Always use the first object within the array to post to the notification window
        atg.keyboard.setNotificationWindowMessage(_keyboardShortcutObjects[0]);
      }

      // Extract the object data and call the respective topic and action
      for(var _count = 0; _count < _keyboardShortcutObjects.length; _count++) { 
        _topic = _keyboardShortcutObjects[_count].topic;
        _action = _keyboardShortcutObjects[_count].action;
        
        if ((_action != null) && (typeof _action == "function")) {
          console.debug("Keyboard Nav | Calling JavaScript action: " + _action);
          _action();
        }
        
        if ((_topic != null) && (_topic.length > 0)) {
          console.debug("Keyboard Nav | Publishing topic: " + _topic);
          dojo.publish(_topic);
        }
      }
    } 
  }
  
  /*
  e.cancelBubble = true;
  e.returnValue = false;

  if (e.stopPropagation) {
    e.stopPropagation();
    e.preventDefault();
  }
  */
};

// -------------------------------------------------------------------
// Tab highlighting event controller
// -------------------------------------------------------------------
atg.keyboard.activateTabHighlight = function (e) {
  atg.keyboard.keyboardNavCurrentNode = e.target;
  
  if (atg.keyboard.getTab() == true) {
    console.debug ("Detecting tab pressed - highlighting node");
    atg.keyboard.highlightAndFade(atg.keyboard.keyboardNavCurrentNode);
  }
};

// -------------------------------------------------------------------
// Sets the value of tabPressed temporarily for highlighting purposes
// -------------------------------------------------------------------
atg.keyboard.setTab = function () {
  atg.keyboard._tabPressed = true;
  setTimeout(function(){
     atg.keyboard._tabPressed = false;
  },500);
};

// -------------------------------------------------------------------
// Return value of tabPressed
// -------------------------------------------------------------------
atg.keyboard.getTab = function () {
  return atg.keyboard._tabPressed;
};

// -------------------------------------------------------------------
// Keyboard shortcut object registration
// -------------------------------------------------------------------
atg.keyboard.registerShortcut = function (_shortcutKey, _shortcutObject) {
  if ((atg.keyboard.keyboardNavShortcuts.length == 0) || (atg.keyboard.keyboardNavShortcuts[_shortcutKey] == null)) {
    //console.debug("Keyboard Nav | Creating new array for shortcut key " + _shortcutKey);
    atg.keyboard.keyboardNavShortcuts[_shortcutKey] = [];
  }
  
  if (_shortcutObject != null) {
    atg.keyboard.keyboardNavShortcuts[_shortcutKey].push(_shortcutObject);
  }
};

// -------------------------------------------------------------------
// Utility method to determine whether the user is tabbing in the UI
// -------------------------------------------------------------------
atg.keyboard.isTabPressed = function (e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var e  = e || e.event;

  if ((e.ctrlKey == true) || (e.altKey == true)) {
    return false;
  }
  
  if (e.keyCode == 9) {
    return true;
  }

  return false;
};


// -------------------------------------------------------------------
// Utility method to determine whether the user pressed the Enter key
// -------------------------------------------------------------------
atg.keyboard.isEnterPressed = function (e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var e  = e || e.event;
  if ((e.ctrlKey == true) || (e.altKey == true)) {
    return false;
  }
  
  if (e.keyCode == 13) {
    return true;
  }

  return false;
};


// -------------------------------------------------------------------
// Utility method to ensure that at least one modifier key is pressed
// -------------------------------------------------------------------
atg.keyboard.isModifierPressed = function (e) {
  if ((e.ctrlKey == true) || (e.altKey == true) || (e.shiftKey == true)) {
    console.debug("Keyboard Nav | Modifier key pressed - looking up keyboard shortcut");
    return true;
  }
  return false;
};

// -------------------------------------------------------------------
// Utility method to figure out key modifier and shortcut key pressed
// -------------------------------------------------------------------
atg.keyboard.getShortcutKeyPressed = function (e) {
  var _keyPressed = "";
  var _keyLookup;

  console.debug("Keyboard Nav | e.keyCode = " + e.keyCode + ", e.keyChar = " + e.keyChar + ", e.key = " + e.key + ", e.charCode = " + e.charCode);
  if (e.ctrlKey == true) {
    _keyPressed = atg.keyboard._keyCtrl + atg.keyboard._keySeparator;
  }
  if (e.altKey == true) {
    _keyPressed += atg.keyboard._keyAlt + atg.keyboard._keySeparator;
  }
  if (e.shiftKey == true) {
    _keyPressed += atg.keyboard._keyShift + atg.keyboard._keySeparator;
  }
 
  if(dojo.isIE || dojo.isSafari) {
    if (e.keyCode != null) {
      if (e.keyCode > 90) {
        _keyLookup = atg.keyboard.lookupKeyCodeIE[e.keyCode];
        if (_keyLookup == null) {
          _keyPressed += e.keyCode;
        }
        else {
          _keyPressed += _keyLookup;
        }
      }
      else {
        _keyPressed += String.fromCharCode(e.keyCode);
      }
    }
  }
  else {
    _keyLookup = atg.keyboard.lookupKeyCodeMozilla[e.keyCode];
    //_keyLookup = atg.keyboard.lookupKeyCodeMozilla[e.keyChar];
    if (_keyLookup == null) {
      if (e.keyChar != null) {
        _keyPressed += e.keyChar;
      }
      else {
        _keyPressed += e.key;
      }
    }
    else {
      _keyPressed += _keyLookup;
    }
  }
  
  _keyPressed = _keyPressed.toUpperCase();
  return _keyPressed;
};
  
// -------------------------------------------------------------------
// Return an array of zero or more key lookup objects
// -------------------------------------------------------------------
atg.keyboard.getKeyboardNavShortcuts = function (_lookupKey) {
  return atg.keyboard.keyboardNavShortcuts[_lookupKey];
};

// -------------------------------------------------------------------
// Keyboard navigation popup message notifier
// -------------------------------------------------------------------
atg.keyboard.setNotificationWindowMessage = function (_keyboardShortcutObject) {
  var _shortcut = _keyboardShortcutObject.shortcut;  
  var _name = _keyboardShortcutObject.name;
  var _description = _keyboardShortcutObject.description;
  var _area = _keyboardShortcutObject.area;
  var _notify = _keyboardShortcutObject.notify;
  
  if (_notify == true) {
    console.debug("Keyboard Nav | Sending '" + _shortcut + "' event to notification window");
    atg.keyboard.keyboardNavNotificationMessage = "<span class='atg_keyboard_notificationMessageHeading'>" + _shortcut + "</span><br /><span class='atg_keyboard_notificationMessageSubHeading'>" + _name + "</span>";
    //dojo.publish("keyboardNavNotificationTopic", [{ message: atg.keyboard.keyboardNavNotificationMessage, type: "fatal", duration: 500 }]);
    dojo.publish("keyboardNavNotificationTopic", [atg.keyboard.keyboardNavNotificationMessage]);
  }
};

// -------------------------------------------------------------------
// Display keyboard navigation help window
// -------------------------------------------------------------------
atg.keyboard.showKeyboardShortcutHelpWindow = function () {
  atg.keyboard.getHelpWindowContents();
  dijit.byId("keyboardNavHelpObject").show();
};

// -------------------------------------------------------------------
// Retrieve ordered list of keyboard nav shortcuts for help window
// -------------------------------------------------------------------
atg.keyboard.getHelpWindowContents = function () {
  console.debug("Keyboard Nav | Loading shortcut key help dialog");
  
  var _cssRow;
  var _rowCount = 0;
  
  var _helpWindowContents = '<table class="atg_keyboard_helpDialog" cellspacing="0" cellpadding="0" border="0"><tr class="atg_keyboard_helpDialogHeadings"><th>'+getResource("keyboard.popup.shortcut")+'</th><th></th><th>'+getResource("keyboard.popup.name")+'</th><th></th><th>'+getResource("keyboard.popup.description")+'</th><th></th><th>'+getResource("keyboard.popup.area")+'</th></tr>';
  
  // We will comment out the routine to display shortcuts exactly as they are defined in the JS files and instead use
  // a simple sorting algorithm to display shortcuts in alphabetical order, based on the shortcut name
  /*
  var _shortcutArray = atg.keyboard.keyboardNavShortcuts;
  for(var _countPrimary in atg.keyboard.keyboardNavShortcuts) {
    for(var _countSecondary = 0; _countSecondary < atg.keyboard.keyboardNavShortcuts[_countPrimary].length; _countSecondary++) { 
      var _shortcutContents = atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary];
  
      _cssRow = "atg_keyboard_helpDialogShadingOn";
      if(_rowCount % 2 == 0) { 
        _cssRow = "atg_keyboard_helpDialogShadingOff";
      }
  
      _helpWindowContents += '<tr class="' + _cssRow + '"><td nowrap>' + _shortcutContents.shortcut + '<td class="atg_keyboard_tableSpacer"></td><td>' + _shortcutContents.name + '<td class="atg_keyboard_tableSpacer"></td><td>' + _shortcutContents.description + '<td class="atg_keyboard_tableSpacer"></td><td nowrap>' + _shortcutContents.area + '</td></tr>';
      _rowCount ++;
    }
  }
  */

  var _shortcutArrayByName = atg.keyboard.getKeyboardShortcutsByName();
  var _sortedArray;
  var _sortedArrayContents;
  var _cssRow;
  var _rowCount = 0;
  var _helpWindowContents = '<table class="atg_keyboard_helpDialog" cellspacing="0" cellpadding="0" border="0"><tr class="atg_keyboard_helpDialogHeadings"><th>'+getResource("keyboard.popup.shortcut")+'</th><th></th><th>'+getResource("keyboard.popup.name")+'</th><th></th><th>'+getResource("keyboard.popup.description")+'</th><th></th><th>'+getResource("keyboard.popup.area")+'</th></tr>';
  
  for (var _countPrimary in _shortcutArrayByName) {
    _sortedArray = new dojox.collections.SortedList();
    
    for(var _countSecondary = 0; _countSecondary < _shortcutArrayByName[_countPrimary].length; _countSecondary++) { 
      _sortedArray.add(_shortcutArrayByName[_countPrimary][_countSecondary].shortcut, _shortcutArrayByName[_countPrimary][_countSecondary]);
    }
    
    for (var _i = 0; _i < _sortedArray.count; _i++) {
      _sortedArrayContents = _sortedArray.getByIndex(_i);
      _cssRow = "atg_keyboard_helpDialogShadingOn";
      if(_rowCount % 2 == 0) { 
        _cssRow = "atg_keyboard_helpDialogShadingOff";
      }
      _helpWindowContents += '<tr class="' + _cssRow + '"><td nowrap>' + _sortedArrayContents.shortcut + '<td class="atg_keyboard_tableSpacer"></td><td>' + _sortedArrayContents.name + '<td class="atg_keyboard_tableSpacer"></td><td>' + _sortedArrayContents.description + '<td class="atg_keyboard_tableSpacer"></td><td nowrap>' + _sortedArrayContents.area + '</td></tr>';
      _rowCount ++;
    }
  }

  
  // need to use destroyNode to prevent IE memory leak
  var helpWindow = dojo.byId("atg_keyboard_keyboardNavHelpWindowDiv");
  while(helpWindow.hasChildNodes()){ dojo._destroyElement(helpWindow.firstChild); }
  helpWindow.innerHTML = _helpWindowContents;
};

// -------------------------------------------------------------------
// Keyboard shortcut event controller
// -------------------------------------------------------------------
atg.keyboard.activateHelpDialogShortcut = function (e) {
  if (e.keyChar == 27) {
    console.debug("Keyboard Nav | Escape key pressed - closing shortcut key help dialog");
    dojo.publish("CloseHelpDialog");
  }
};


// -------------------------------------------------------------------
// Rearrange the navigation shortcuts to be grouped by functional area
// -------------------------------------------------------------------
atg.keyboard.getKeyboardShortcutsByFunctionalArea = function () {
  var _shortcutArrayByArea = [];
  var _shortcutArrayByAreaSorted = [];
  var _sortedArrayContents;
  var _area;
  var _sortedArray = new dojox.collections.SortedList();
  
  for(var _countPrimary in atg.keyboard.keyboardNavShortcuts) {
    for(var _countSecondary = 0; _countSecondary < atg.keyboard.keyboardNavShortcuts[_countPrimary].length; _countSecondary++) { 
      _area = atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary].area;
      
      if (_shortcutArrayByArea[_area] == null) {
        console.debug("Keyboard Nav | Adding " + _area + " category to the window shortcut array");
        _shortcutArrayByArea[_area] = [];
      }
      
      _shortcutArrayByArea[_area].push(atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary]);
    }
  }
  
  for(var _countList in _shortcutArrayByArea) {
    if (_shortcutArrayByArea[_countList].length > 0) {
      _sortedArray.add(_shortcutArrayByArea[_countList][0].area, _shortcutArrayByArea[_countList]);   
    }
  }

  for (var _i = 0; _i < _sortedArray.count; _i++) {
    _sortedArrayContents = _sortedArray.getByIndex(_i);
    _shortcutArrayByAreaSorted.push(_sortedArrayContents);
  }
 
  return _shortcutArrayByAreaSorted;
};


// -------------------------------------------------------------------
// Rearrange the navigation shortcuts to be grouped by name
// -------------------------------------------------------------------
atg.keyboard.getKeyboardShortcutsByName = function () {
  var _shortcutArrayByName = [];
  var _shortcutArrayByNameSorted = [];
  var _sortedArrayContents;
  var _name;
  var _sortedArray = new dojox.collections.SortedList();
  
  for(var _countPrimary in atg.keyboard.keyboardNavShortcuts) {
    for(var _countSecondary = 0; _countSecondary < atg.keyboard.keyboardNavShortcuts[_countPrimary].length; _countSecondary++) { 
      _name = atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary].name;
      
      if (_shortcutArrayByName[_name] == null) {
        console.debug("Keyboard Nav | Adding " + _name + " category to the window shortcut array");
        _shortcutArrayByName[_name] = [];
      }
      
      _shortcutArrayByName[_name].push(atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary]);
    }
  }
  
  for(var _countList in _shortcutArrayByName) {
    if (_shortcutArrayByName[_countList].length > 0) {
      _sortedArray.add(_shortcutArrayByName[_countList][0].name, _shortcutArrayByName[_countList]);   
    }
  }

  for (var _i = 0; _i < _sortedArray.count; _i++) {
    _sortedArrayContents = _sortedArray.getByIndex(_i);
    _shortcutArrayByNameSorted.push(_sortedArrayContents);
  }
 
  return _shortcutArrayByNameSorted;
};


// -------------------------------------------------------------------
// Return the currently-selected top level node
// -------------------------------------------------------------------
atg.keyboard.getCurrentIdentifier = function (_className) {
  var node = atg.keyboard.keyboardNavCurrentNode; 
  
  while(node){
    
    /* ***
    console.debug("Keyboard Nav | Class name is: " + dojo.getClass(node));
    if (dojo.getClass(node).match(_className) != null) {
      return node;
    }
    */
    
    console.debug("Keyboard Nav | Class name is: " + node.className);
    if (node.className != null) {
      if (node.className.match(_className) != null) {
        return node;
      }
    }
    node = node.parentNode;
  }
  return null;
};

// -------------------------------------------------------------------
// Retrieving top level navigation objects
// -------------------------------------------------------------------
atg.keyboard.getTopLevelIdentifiers = function () {
  return dojo.query("." + atg.keyboard._classTopLevelIdentifier);
};

// -------------------------------------------------------------------
// Retrieving panel navigation objects
// -------------------------------------------------------------------
atg.keyboard.getPanelIdentifiers = function () {
  return dojo.query("." + atg.keyboard._classPanelIdentifier);
};

// -------------------------------------------------------------------
// Navigate to next top level object
// -------------------------------------------------------------------
atg.keyboard.navigateToNextTopLevelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to next top level identifier");
  var _currentTopLevelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classTopLevelIdentifier);
  var _allTopLevelIdentifiers = atg.keyboard.getTopLevelIdentifiers();
  var _nextTopLevelIdentifier;

  console.debug("Keyboard Nav | _allTopLevelIdentifiers = " + _allTopLevelIdentifiers);

  if ((_allTopLevelIdentifiers != null) && (_allTopLevelIdentifiers.length > 0)) {

    if (_currentTopLevelIdentifier == null) {
      _nextTopLevelIdentifier = _allTopLevelIdentifiers[0];
    }
    else {
      for (var _i=0; _i<_allTopLevelIdentifiers.length; _i++) {
        if (_currentTopLevelIdentifier == _allTopLevelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          if (_i == _allTopLevelIdentifiers.length - 1) {
            console.debug("Keyboard Nav | Wrapping around to first node");
            _nextTopLevelIdentifier = _allTopLevelIdentifiers[0];
          }
          else {
            _nextTopLevelIdentifier = _allTopLevelIdentifiers[_i+1];
          }
        }
      }
    }
    
    _nextTopLevelIdentifier.focus();
    atg.keyboard.fireEventTab(_nextTopLevelIdentifier);
    atg.keyboard.highlightAndFadePanel(_nextTopLevelIdentifier);
  }
};

// -------------------------------------------------------------------
// Navigate to previous top level object
// -------------------------------------------------------------------
atg.keyboard.navigateToPreviousTopLevelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to previous top level identifier");
  var _currentTopLevelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classTopLevelIdentifier);
  var _allTopLevelIdentifiers = atg.keyboard.getTopLevelIdentifiers();
  var _previousTopLevelIdentifier;

  if ((_allTopLevelIdentifiers != null) && (_allTopLevelIdentifiers.length > 0)) {

    if (_currentTopLevelIdentifier == null) {
      _previousTopLevelIdentifier = _allTopLevelIdentifiers[_allTopLevelIdentifiers.length-1];
    }
    else {
      for (var _i=0; _i<_allTopLevelIdentifiers.length; _i++) {
        if (_currentTopLevelIdentifier == _allTopLevelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          
          if (_currentTopLevelIdentifier == _allTopLevelIdentifiers[0]) {
            console.debug("Keyboard Nav | Wrapping around to last node");
            _previousTopLevelIdentifier = _allTopLevelIdentifiers[_allTopLevelIdentifiers.length-1];
          }
          else {
            _previousTopLevelIdentifier = _allTopLevelIdentifiers[_i-1];
          }
        }
      }
    }
    
    _previousTopLevelIdentifier.focus();
    atg.keyboard.fireEventTab(_previousTopLevelIdentifier);
    atg.keyboard.highlightAndFadePanel(_previousTopLevelIdentifier);
  }
};

// -------------------------------------------------------------------
// Navigate to next panel object
// -------------------------------------------------------------------
atg.keyboard.navigateToNextPanelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to next panel");
  var _currentPanelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classPanelIdentifier);
  var _allPanelIdentifiers = atg.keyboard.getPanelIdentifiers();
  var _nextPanelIdentifier;

  console.debug("Keyboard Nav | Number of panels in rotation: " + _allPanelIdentifiers.length);
  console.debug("Keyboard Nav | Current panel is: " + _currentPanelIdentifier);
  
  if ((_allPanelIdentifiers != null) && (_allPanelIdentifiers.length > 0)) {

    if (_currentPanelIdentifier == null) {
      _nextPanelIdentifier = _allPanelIdentifiers[0];
    }
    else {
      
      for (var _i=0; _i<_allPanelIdentifiers.length; _i++) {
        if (_currentPanelIdentifier == _allPanelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          if (_i == _allPanelIdentifiers.length - 1) {
            console.debug("Keyboard Nav | Wrapping around to first node");
            _nextPanelIdentifier = _allPanelIdentifiers[0];
          }
          else {
            _nextPanelIdentifier = _allPanelIdentifiers[_i+1];
          }
        }
      }
    }
  
    _nextPanelIdentifier.focus();
    atg.keyboard.fireEventTab(_nextPanelIdentifier);
    atg.keyboard.highlightAndFadePanel(_nextPanelIdentifier);
  }
};

// -------------------------------------------------------------------
// Navigate to previous panel object
// -------------------------------------------------------------------
atg.keyboard.navigateToPreviousPanelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to previous panel identifier");
  var _currentPanelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classPanelIdentifier);
  var _allPanelIdentifiers = atg.keyboard.getPanelIdentifiers();
  var _previousPanelIdentifier;

  if ((_allPanelIdentifiers != null) && (_allPanelIdentifiers.length > 0)) {
    if (_currentPanelIdentifier == null) {
      _previousPanelIdentifier = _allPanelIdentifiers[_allPanelIdentifiers.length-1];
    }
    else {
      for (var _i=0; _i<_allPanelIdentifiers.length; _i++) {
        if (_currentPanelIdentifier == _allPanelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          
          if (_currentPanelIdentifier == _allPanelIdentifiers[0]) {
            console.debug("Keyboard Nav | Wrapping around to last node");
            _previousPanelIdentifier = _allPanelIdentifiers[_allPanelIdentifiers.length-1];
          }
          else {
            _previousPanelIdentifier = _allPanelIdentifiers[_i-1];
          }
        }
      }
    }
    
    _previousPanelIdentifier.focus();
    atg.keyboard.fireEventTab(_previousPanelIdentifier);
    atg.keyboard.highlightAndFadePanel(_previousPanelIdentifier);
  }
};

// -------------------------------------------------------------------
// Explicitly fire tab to position cursor on first usable entity
// -------------------------------------------------------------------
atg.keyboard.fireEventTab = function(_selectedNode) {
  /*
  if ((_selectedNode != null) && (_selectedNode.style != null)) {
    console.debug("Keyboard Nav | Firing Tab Event");

    if (window.KeyEvent) {
      console.debug("Keyboard Nav | window.KeyEvent is true - calling KeyEvents");
      var evObj = document.createEvent('KeyEvents');
      evObj.initKeyEvent ('keypress', true, true, window, false, false, false, false, 112, 112);
    } 
    else {
      console.debug("Keyboard Nav | window.KeyEvent is false - calling UIEvents");
      var evObj = document.createEvent('UIEvents');
      evObj.initUIEvent ('keypress', true, true, window, 1);
      evObj.keyCode = 13;
    }
    _selectedNode.dispatchEvent(evObj);
  }
  */
};

// -------------------------------------------------------------------
// Highlight and fade specified panel during panel navigation
// -------------------------------------------------------------------
atg.keyboard.highlightAndFadePanel = function(_selectedNode) {
  if (atg.keyboard._highlightAndFadePanels == true) {
    if ((_selectedNode != null) && (_selectedNode.style != null)) {
      dojo.addClass(_selectedNode, "atg_keyboard_panelHighlight");
      setTimeout(function(){
         dojo.removeClass(_selectedNode, "atg_keyboard_panelHighlight");
      },800);
    }
  }
};

// -------------------------------------------------------------------
// Highlight and fade DOM node for visual effect while tabbing
// -------------------------------------------------------------------
atg.keyboard.highlightAndFade = function(_selectedNode) {
  if (atg.keyboard._highlightAndFadeNodes == true) {
    if ((_selectedNode != null) && (_selectedNode.style != null)) {
      dojo.addClass(_selectedNode, "atg_keyboard_notificationHighlight");
      setTimeout(function(){
         dojo.removeClass(_selectedNode, "atg_keyboard_notificationHighlight");
      },600);
    }
  }
};


/*
 * This could be an element or a widget - whatever it is it must be connect-able to a dojo event
 */
atg.keyboard.findInputElement = function(input) {
  var inputElement = null;
  
  // TODO what can be used for isNode?
  if (false /*dojo.isNode(input)*/) {
    inputElement = input;
  }
  else if (dojo.isString(input)) {
    inputElement = dijit.byId(input);
    if (!inputElement) {
      inputElement = dojo.byId(input);
    }
  }
  else if (dojo.isObject(input)) {
    // TODO widgetType doesn't exist any more
    if (input instanceof dijit._Widget) { // input is a widget
      inputElement = input;
    }
    else if (input.form && input.name) {
      inputElement = dojo.byId(input.form)[input.name];
    }
  }
  return inputElement;
};
/*
 * Same as findInputElement, but allows a form to be specified as the action element.
 * If a form is specified, the form is submitted by calling submit() on the form
 * when the action occurs.
 */
atg.keyboard.findActionElement = function(obj) {
  var actionElement = atg.keyboard.findInputElement(obj);
  if (!actionElement && dojo.isObject(obj) && obj.form) {
    // Action element is a form element to submit
    actionElement = dojo.byId(obj.form);
  }
  return actionElement;
};
/*
 * Associates every element in a form with the default enter key activation behavior.
 * Unlike the registerDefaultEnterKey function, this function can only associate elements
 * (not widgets). However, the target element on which the action CAN be a widget (or element).
 */
atg.keyboard.registerFormDefaultEnterKey = function(_formId, actionRef, actionFunc /*optional*/, actionParams /*optional*/) {
  console.debug("Keyboard Nav | Keyboard Nav | Default enter key registered for form id " + _formId + " and action point " + actionRef);
  var theForm = dojo.byId(_formId);
  var i;
  var _defaultInputObject;
  var keyboardEvents = [];
  if (theForm) {
    for (i = 0; i < theForm.elements.length; i++) {
      _defaultInputObject = theForm.elements[i];
      var actionElement = atg.keyboard.findActionElement(actionRef);
      function handleFormDefaultEnterKey(e) {
        atg.keyboard.activateDefaultEnterKey(actionElement, actionFunc, actionParams, e);
      }
      if(dojo.isIE || dojo.isSafari) {
        keyboardEvents.push(dojo.connect(_defaultInputObject, "onkeyup", handleFormDefaultEnterKey));
      }
      else {
        keyboardEvents.push(dojo.connect(_defaultInputObject, "onkeypress", handleFormDefaultEnterKey));
      }
    }
    theForm.atgKeyboardEvents = keyboardEvents;
  }
};
atg.keyboard.unRegisterFormDefaultEnterKey = function(_formId) {
  var theForm = dojo.byId(_formId);
  if (theForm) {
    var keyboardEvents = theForm.atgKeyboardEvents;
    if (keyboardEvents) {
      console.debug("Keyboard Nav | Default enter key unregistered for form id " + _formId);
      dojo.forEach(keyboardEvents, dojo.disconnect);
      theForm.atgKeyboardEvents = null;
    }
  }
};
/**
 * Associates an element or widget
 * The following are valid for the input reference:
 * 1) Any node (use this if the dojo widget won't support a direct event connection)
 * 2) A widget ID string (dojo handles connection between widget and event - widget must support this)
 * 3) A DOM ID string
 * 4) A dojo widget - see note above
 * 5) An object specifying a form ID - name pair ({form:'myFormId',name:'myElementName'})
 * 
 * Valid inputs for the action reference:
 * 1) Anything that is valid for the input element plus:
 * 2) An object specifying a form by ID ({form:'myFormId'})
 * 
 * Valid inputs for the optional action function:
 * 1) empty/null (default behavior performed on the element)
 * 2) A string corresponding to a function on the submit element
 * 3) Not implemented at this time: A function reference to apply in the context of the submit element
 * 
 * Valid inputs for the optional action parameters
 * 1) empty/null (no parameters are passed in)
 * 2) Array of parameters to pass into the action function
 * 
 */
atg.keyboard.registerDefaultEnterKey = function(inputRef, actionRef, actionFunc /*optional*/, actionParams /*optional*/) {
  console.debug("Keyboard Nav | Default enter key registered for input point " + inputRef + " and action point " + actionRef);
  // this could be an element or a widget - whatever it is it must be connect-able to a dojo event
  var inputElement = atg.keyboard.findInputElement(inputRef);
  var actionElement = atg.keyboard.findActionElement(actionRef);
  if(inputElement&&actionElement){
    var keyboardEvent = null;
    function handleDefaultEnterKey(e) {
      atg.keyboard.activateDefaultEnterKey(actionElement, actionFunc, actionParams, e);
    }
    if(dojo.isIE || dojo.isSafari) {
      keyboardEvent = dojo.connect(inputElement, "onkeyup", handleDefaultEnterKey);
    }else {
      keyboardEvent = dojo.connect(inputElement, "onkeypress", handleDefaultEnterKey);
    }
    inputElement.atgKeyboardEvent = keyboardEvent;
  }
};

atg.keyboard.unRegisterDefaultEnterKey = function(inputRef) {
  console.debug("Keyboard Nav | Default enter key unregistered for input point " + inputRef);
  // this could be an element or a widget - whatever it is it must be connect-able to a dojo event
  var inputElement = atg.keyboard.findInputElement(inputRef);
  if(inputElement){
    var keyboardEvent = inputElement.atgKeyboardEvent;
    if(keyboardEvent){
      dojo.disconnect(keyboardEvent);
    }
    inputElement.atgKeyboardEvent = null;
  }
};
/**
 * Activates the submit button onclick or (if no onclick) submits a form if the enter key is pressed
 * Valid parameter:
 * 1) Button
 * 2) Input type=submit
 * 3) Widget
 * 4) Any other node inside a form
 * 5) A form node
 */
atg.keyboard.activateDefaultEnterKey = function(actionElement, actionFunc, actionParams, e) {
  if (!actionElement) return;

  if (atg.keyboard.isEnterPressed(e) == true) {
    console.debug("Keyboard Nav | activateDefaultEnterKey called for ", actionElement, actionFunc, actionParams, e);
    if (atg.keyboard.fireDefaultAction(actionElement, actionFunc, actionParams)) {
      e.cancelBubble = true;
      e.returnValue = false;
    
      if (e.stopPropagation) {
        e.stopPropagation();
        e.preventDefault();
      }
    }
  }
};
/**
 * Fires a default action, such as onclick or submitting a form, based on a passed in element or widget
 * Returns true if an action was performed to handle the event (that is, a form was submitted or a element click was called)
 * Returns false if no form could be identified to submit or no onclick event could be called (that is, if nothing happened)
 */
atg.keyboard.fireDefaultAction = function(element, func, params) {
  var isHandled = false;
  if (element instanceof dijit._Widget) {
    if (func && dojo.isFunction(element[func])) {
      console.debug("Keyboard Nav | Activating default enter key by calling function ", 
        func, " on widget ", element);
      params ? element[func](params) : element[func]();
      isHandled = true;
    }
    else {
      console.debug("Keyboard Nav | Unable to activate default enter key: Function ", func, " not valid on widget ", element);
    }
  }
  else {
    if (!element.nodeType || element.nodeType != 1) { // 1 == ELEMENT_NODE
      console.debug("Keyboard Nav | Unable to activate default enter key: Node ", element, " is not an element node");
      isHandled = false;
    }
    else {
      var tagName = String(element.tagName).toLowerCase();
      if (tagName == "form") {
        console.debug("Keyboard Nav | Activating default enter key by submitting form ", element);
        element.submit();
        isHandled = true;
      }
      else if (tagName == "button" || tagName == "input") {
        if (element.disabled != true) {
          if (!func) func = "onclick";
          if (dojo.isFunction(element[func])) {
            console.debug("Keyboard Nav | Activating default enter key by calling function ", 
              func, " on element ", element);
            params ? element[func](params) : element[func]();
            isHandled = true;
          }
          else {
            console.debug("Keyboard Nav | Unable to activate default enter key: Function ", func, " not valid on element ", element);
          }
        }
        else {
          console.debug("Keyboard Nav | Unable to activate default enter key: Element ", element, " is disabled");
        }
      }
      else {
        console.debug("Keyboard Nav | Unable to activate default enter key: Unknown tag name ", tagName);
      }
    }
  }
  return isHandled;
};

// -------------------------------------------------------------------
// Prevents automatic form submission from a given input control
// -------------------------------------------------------------------
atg.keyboard.blockDefaultEnterKey = function(_defaultInputId) {
  var _submitTrigger = dojo.byId(_defaultInputId);
  
  if(dojo.isIE || dojo.isSafari) {
    dojo.connect(_submitTrigger, "onkeyup", atg.keyboard.preventDefaultEnterKey);
  }
  else {
    dojo.connect(_submitTrigger, "onkeypress", atg.keyboard.preventDefaultEnterKey);
  }
};

// -------------------------------------------------------------------
// Prevents the submit button onclick if the enter key is pressed
// -------------------------------------------------------------------
atg.keyboard.preventDefaultEnterKey = function(e) {
  if (atg.keyboard.isEnterPressed(e) == true) {
    console.debug("Keyboard Nav | Preventing default enter key");
    
    e.cancelBubble = true;
    e.returnValue = false;
  
    if (e.stopPropagation) {
      e.stopPropagation();
      e.preventDefault();
    }
  }
  return true;
};


// -------------------------------------------------------------------
// Determines the current panel and then calls the dock/undock feature, if applicable
// -------------------------------------------------------------------
atg.keyboard.dockUndockCurrentPanel = function() {
  console.debug("Keyboard Nav | Calling atg.keyboard.dockUndockCurrentPanel");
  var _currentPanel = atg.keyboard.getCurrentPanel();
  
  if (_currentPanel != null) {
    // Get panelHeader
    var _navigateNodes = _currentPanel.childNodes[0].childNodes[0];
    console.debug("Keyboard Nav | 1 - Current navigating node is: " + _navigateNodes);
    console.debug("Keyboard Nav | 1 - Class is: " + _navigateNodes.className);
    
    if (_navigateNodes != null) {
      // Get panelIcons
      var _navigateNodes = _navigateNodes.childNodes[1];
      console.debug("Keyboard Nav | 2 - Current navigating node is: " + _navigateNodes);
      console.debug("Keyboard Nav | 2 - Class is: " + _navigateNodes.className);
      
      if (_navigateNodes != null) {
        // Get dock/undock image
        var _navigateNodes = _navigateNodes.childNodes[0].childNodes[0].childNodes[0];
        console.debug("Keyboard Nav | 3 - Current navigating node is: " + _navigateNodes);
        console.debug("Keyboard Nav | 3 - Class is: " + _navigateNodes.className);
        
        if (_navigateNodes != null) {
          //Calling onclick
          console.debug("Keyboard Nav | Calling node onclick: ");
          _navigateNodes.onclick();
        }
      }
    }
    
  }
}


// -------------------------------------------------------------------
// Determines the current panel and then calls the minimize/restore feature, if applicable
// -------------------------------------------------------------------
atg.keyboard.minimizeRestoreCurrentPanel = function() {
  console.debug("Keyboard Nav | Calling atg.keyboard.minimizeRestoreCurrentPanel");
  var _currentPanel = atg.keyboard.getCurrentPanel();

  if (_currentPanel != null) {
    // Get panelHeader
    var _navigateNodes = _currentPanel.childNodes[0].childNodes[0];
    console.debug("Keyboard Nav | 1 - Current navigating node is: " + _navigateNodes);
    console.debug("Keyboard Nav | 1 - Class is: " + _navigateNodes.className);
    
    if (_navigateNodes != null) {
      // Get panelIcons
      var _navigateNodes = _navigateNodes.childNodes[1];
      console.debug("Keyboard Nav | 2 - Current navigating node is: " + _navigateNodes);
      console.debug("Keyboard Nav | 2 - Class is: " + _navigateNodes.className);
      
      if (_navigateNodes != null) {
        // Get minimize/restore image
        var _navigateNodes = _navigateNodes.childNodes[0].childNodes[1].childNodes[0];
        console.debug("Keyboard Nav | 3 - Current navigating node is: " + _navigateNodes);
        console.debug("Keyboard Nav | 3 - Class is: " + _navigateNodes.className);
        
        if (_navigateNodes != null) {
          //Calling onclick
          console.debug("Keyboard Nav | Calling node onclick: ");
          _navigateNodes.onclick();
        }
      }
    }

  }
}

// -------------------------------------------------------------------
// Retrieves the panel Id for the current panel, if available. If no 
// panel is set to current, the first available panel will be returned, 
// or null if no panels exist on the page.
// -------------------------------------------------------------------
atg.keyboard.getCurrentPanel = function() {
  console.debug("Keyboard Nav | Calling atg.keyboard.getCurrentPanel");
  console.debug("Keyboard Nav | Current Node = " + atg.keyboard.keyboardNavCurrentNode);
  _currentNode = atg.keyboard.keyboardNavCurrentNode;
  _parentNode = _currentNode;
  
  while ((_parentNode != null) && (_parentNode.className != atg.keyboard._panelTitleIdentifier)) {
    _parentNode = _parentNode.parentNode;
   console.debug("Keyboard Nav | _parentNode = " + _parentNode.className); 
  }
  
  if (_parentNode != null) {
    console.debug("Keyboard Nav | Found node for panel."); 
    return _parentNode;
  }
  
  return null;
 
}// -------------------------------------------------------------------
// Keyboard shortcut map for Service
// -------------------------------------------------------------------
atg.keyboard.registerServiceShortcuts = function () {

  atg.keyboard.registerShortcut(
  "ALT+F1", {
    shortcut: "ALT + F1",
    name: getResource("keyboard.service.help.name"),
    description: getResource("keyboard.service.help.description"),
    area: getResource("keyboard.area.workspace"),
    action: function () {atg.keyboard.showKeyboardShortcutHelpWindow();},
    notify: false
  });

  atg.keyboard.registerShortcut(
  "CTRL+ALT+U", {
    shortcut: "CTRL + ALT + U",
    name: getResource("keyboard.service.minimizeUtilities.name"),
    description: getResource("keyboard.service.minimizeUtilities.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/minimizeMaximizeUtilities",
    notify: true
  });

  atg.keyboard.registerShortcut(
  "CTRL+SHIFT+L", {
    shortcut: "CTRL + SHIFT + L",
    name: getResource("keyboard.service.openFirebug.name"),
    description: getResource("keyboard.service.openFirebug.name"),
    area: getResource("keyboard.area.workspace"),
    action: function () {dojo.toggleConsole();},
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+F11", {
    shortcut: "ALT + F11",
    name: getResource("keyboard.service.startCall.name"),
    description: getResource("keyboard.service.startCall.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/startCall",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+F12", {
    shortcut: "ALT + F12",
    name: getResource("keyboard.service.endCall.name"),
    description: getResource("keyboard.service.endCall.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/endCall",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+F12", {
    shortcut: "ALT + SHIFT + F12",
    name: getResource("keyboard.service.endCallStartNew.name"),
    description: getResource("keyboard.service.endCallStartNew.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/endCallStartNew",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+N", {
    shortcut: "ALT + SHIFT + N",
    name: getResource("keyboard.service.addNote.name"),
    description: getResource("keyboard.service.addNote.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/addNote",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "CTRL+ALT+SHIFT+N", {
    shortcut: "CTRL + ALT + SHIFT + N",
    name: getResource("keyboard.service.addCallNote.name"),
    description: getResource("keyboard.service.addCallNote.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/addCallNote",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+K", {
    shortcut: "ALT + SHIFT + K",
    name: getResource("keyboard.service.ticketSearch.name"),
    description: getResource("keyboard.service.ticketSearch.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/backToTicketSearch",
    notify: true
  });
  
  //Changed ALT+SHIFT+C to ALT+SHIFT+U due to IE8 incompatability  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+U", {
    shortcut: "ALT + SHIFT + U",
    name: getResource("keyboard.service.searchProfile.name"),
    description: getResource("keyboard.service.searchProfile.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/searchForProfile",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+3", {
    shortcut: "ALT + 3",
    name: getResource("keyboard.service.respondTab.name"),
    description: getResource("keyboard.service.respondTab.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/respondTab",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+5", {
    shortcut: "ALT + 5",
    name: getResource("keyboard.service.ticketsTab.name"),
    description: getResource("keyboard.service.ticketsTab.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/ticketsTab",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "CTRL+ALT+1", {
    shortcut: "CTRL + ALT + 1",
    name: getResource("keyboard.service.activeTickets.name"),
    description: getResource("keyboard.service.activeTickets.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/activeTickets",
    notify: true
  });

  atg.keyboard.registerShortcut(
  "ALT+6", {
    shortcut: "ALT + 6",
    name: getResource("keyboard.service.customersTab.name"),
    description: getResource("keyboard.service.customersTab.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/customersTab",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "CTRL+ALT+SHIFT+C", {
    shortcut: "CTRL + ALT + SHIFT + C",
    name: getResource("keyboard.service.newProfle.name"),
    description: getResource("keyboard.service.newProfle.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/createNewCustomer",
    notify: true
  });
  
};

dojo.addOnLoad(atg.keyboard.registerServiceShortcuts);// -------------------------------------------------------------------
// Keyboard navigation topics for Service
// -------------------------------------------------------------------
atg.keyboard.registerServiceTopics = function () {
  
  dojo.subscribe("/atg/service/keyboardShortcut/respondTab", null, function() { 
    atgChangeTab(atg.service.framework.changeTab('respondTab'),'communicateNextSteps','respondPanels',['nextStepsPanel']);
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/minimizeMaximizeUtilities", null, function() { 
    atg.service.framework.toggleSidebar();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/ticketsTab", null, function() { 
    atgChangeTab(atg.service.framework.changeTab('ticketsTab'),'ticketSearchNextSteps','ticketSearchPanels',['nextStepsPanel']);
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/customersTab", null, function() { 
    viewCurrentCustomer('customersTab');
  });

  dojo.subscribe("/atg/service/keyboardShortcut/activeTickets", null, function() { 
    showActiveTicketsPopup();
    return false;
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/ticketDetails", null, function() { 
  });

  dojo.subscribe("/atg/service/keyboardShortcut/addNote", null, function() { 
    atg.service.ticketing.addNotePrompt();atg.service.framework.cancelEvent(event); 
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/addCallNote", null, function() { 
    atg.service.ticketing.addCallActivityPrompt();atg.service.framework.cancelEvent(event);
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/startCall", null, function() { 
    atg.service.framework.startCall();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/endCall", null, function() { 
    atg.service.framework.endCall();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/endCallStartNew", null, function() { 
    atg.service.framework.endAndStartCall();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/maxMinGlobalContextArea", null, function() { 
  });

  dojo.subscribe("/atg/service/keyboardShortcut/customerInformationPanel", null, function() { 
    viewCurrentCustomer('customersTab');
    return false;
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/createNewCustomer", null, function() { 
    createNewCustomer();
    return false;
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/searchForProfile", null, function() { 
    showCustomerSearch();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/maxMinUtilitiesPane", null, function() { 
  });

  dojo.subscribe("/atg/service/keyboardShortcut/backToTicketSearch", null, function() { 
    backToTicketSearch();
    return false;
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/dockUndockCurrentPanel", null, function() { 
  });

  dojo.subscribe("/atg/csc/keyboardShortcut/minResCurrentPanel", null, function() { 
  });

}

dojo.addOnLoad(atg.keyboard.registerServiceTopics);
function showPopup(url, width, height) {
  var dummyDiv = document.createElement("div");
  dummyDiv.style.position = "absolute";
  document.body.appendChild(dummyDiv);
  dummyDiv.style.width = width;
  dummyDiv.style.height = height;
  dummyDiv.style.top = "100px";
  dummyDiv.style.left = "100px";
  
  var widget = new dojox.Dialog( {href: "/agent/commerceassist/popup.jsp?url=" + encodeURI(url), windowsStates: ["normal"], displayCloseAction: true, title: "Popup", extractContent: true, refreshOnShow: true, duration: 100}, dummyDiv);
  //widget.show();
}
dojo.provide("atg.progress");
// array of panels to update for progress
// apps should add their own panels to this array
atg.progress.panels = [];
atg.progress.update = function (newState, newProcess, params){
  if (params === undefined || params === null) {
    params = {};
  }
  if (params.queryParams === undefined || params.queryParams === null) {
    params.queryParams = {};
  }

  if (params.form === undefined || params.form === null){
    params.form = dojo.byId("transformForm");
  }
  
  if (newState) { params.queryParams.state = newState; }
  if (newProcess) { params.queryParams.process = newProcess;}
  if (dojo.isArray(atg.progress.panels)) {
	  params.panels = atg.progress.panels;
  }
  params.queryParams = {panel: newState};
  params.showLoadingCurtain = false;
  return atgSubmitAction(params);
};/* Copyright (C) 1999-2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */

//*************************************************************************
//
// response.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// Defines client-side response behavior
//
//*************************************************************************
var g_abandonedSession;
var g_textBefore;

//-------------------------------------------------------
// Functions required for Response Panel Functionality
//-------------------------------------------------------

//-------------------------------------------------------
//  INITIALISATION FUNCTIONS
//-------------------------------------------------------
/**
 * Initialise the response panel. This method should be called by PPR once the response panel has been loaded.
 * This method then performs any additional setup required, including initialising the rich editor and setting
 * the initial state of the editable adresses.
 */
function ResponseInitPanel()
{

  //subscribe to the ticket change topic
  dojo.subscribe("/agent/ticketChange", function(){ResponseSaveStateOnUnload()});

    // Get initial body content for editor, and set on hidden form field (this will be updated
  // on each subsequent editor blur event, but need to set it now in case we never modify and blur the editor)

  var msgBodyContent = unescapeXml(dojo.byId("responseTempHTMLContent_div").innerHTML);

  //***********************************************************************
  // Create new editor object
  //
  var respondEditor = new FCKeditor("RespondEditor");
  respondEditor.BasePath = window.fckEditorBasePath;
  respondEditor.Config["CustomConfigurationsPath"] = window.contextPath + '/include/response/editor/editorConfig.js?' + ( new Date() * 1 );
  respondEditor.ToolbarSet = "Respond";
  respondEditor.Height = 300;
  respondEditor.Value = msgBodyContent;
  dojo.byId("richTextEditorContent").innerHTML = respondEditor.CreateHtml();

  //dojo.subscribe("/agent/panelUnloaded", function(){ResponseSaveStateOnUnload();});
  dijit.byId("contentColumn").onUnloadDeferred.addCallback(function(){ResponseSaveStateOnUnload();});
  g_abandonedSession = false;

  //subscribe to newCallEvent's as part of a fix to bug 149744
  dojo.subscribe("startNewCallEvent", ResponseSaveStateOnUnload);
  dojo.subscribe("endAndStartNewCallEvent", ResponseSaveStateOnUnload);
  
  //disable the spellchecker if language isn't supported
  if(!languageSupported)
  {
	  setTimeout(function () 
	    {
		  FCKeditorAPI.GetInstance("RespondEditor").EditorWindow.parent.FCKToolbarItems.LoadedItems["Spell"].Disable();
		}, 2000);
  }
}

function ResponseAbandonedSession()
{
  g_abandonedSession = true;
}

//-------------------------------------------------------
//  EVENT HANDLING FUNCTIONS
//-------------------------------------------------------

function ResponseUpdateAllFormFields()
{
  // Update any hidden form fields with current state.
  var theElement = document.getElementById("outMessageForm");
  if (theElement != null) {
    theElement.htmlBody.value = ResponseGetStrippedValue();
    theElement.textBody.value = ResponseGetStrippedValue().replace( /<[^<|>]+?>/gi,'' );
  }
}

/**
 * Save the current state of the Response panel. This should invoke an action to submit the message form
 * and expects no response to be returned.
 * This should be called by the onblur event of each form field on the
 * compose panel enabling the user to move between tabs without losing any state information that has
 * been created during the composition of an outbound message.
 *
 * NOTE: The eventSource parameter is not used at present, but may be implemented in future to enable a 'delta'
 * submit of the form instead of submitting the entire form each time the user moves off of any element.
 *
 * @param eventSource the DOM element from which the onblur event originated
 */
function ResponseSaveState(eventSource)
{
  if (!g_abandonedSession)
  {
    ResponseUpdateAllFormFields();
    saveEmail();
  }
  g_abandonedSession = false;

}


function ResponseSaveStateOnUnload()
{
  if (!g_abandonedSession)
  {
    ResponseUpdateAllFormFields();
    ResponseSaveState();
  }
  g_abandonedSession = false;
}

/**
 * Insert email template from container div into the body of the rich editor field
 * @param containerId the DOM id of the container whose content should be inserted
 */
function ResponseInsertTemplate(containerId)
{
  //ResponseReplaceEditorContent(containerId);
  ResponseInsertAtCaret(containerId);
}

/**
 * Replace the body of the rich editor component with new content
 * @param containerId the DOM id of the container whose content should be inserted
 */
function ResponseReplaceEditorContent(containerId)
{
  var element = document.getElementById(containerId);
  if (element && element != null)
  {
    var content = unescapeXml(element.innerHTML);
    FCKeditorAPI.GetInstance('RespondEditor').SetHTML(content);
  }
}

/**
 * Insert content into the rich editor at the caret position, overwriting any selected text.
 * The content to be inserted is takend from the container div passed in
 * c:out tag
 * @param containerId the DOM id of the container whose content should be inserted
 */
function ResponseInsertAtCaret(containerId)
{
  var element = document.getElementById(containerId);
  if (dojo.isIE < 8 && dojo.isIE != 0){
    //this condition was included to fix bug KNLDG-168108
    // IE7 generates error when DOM method "insertBefore" invokes with parameter which wasn't created at the same document as element which invoks method
    // e.g. A. insertBefore(B) - A and B should be elements of one document is it is not IE7 generates error
    //unfortunaly FCKEditor doesn't check this condition which causes error because FCKEditor is loaded as iframe
    var fckEditorDocument = document.getElementById("RespondEditor___Frame").contentWindow.document;
    var fckEdinorContent = fckEditorDocument.getElementsByTagName("iframe")[0].contentWindow.document;
    if (element.ownerDocument != fckEdinorContent){
      var tmp = fckEdinorContent.createElement(element.tagName);
      tmp.innerHTML = element.innerHTML;
      element = tmp;
    }
  }
  FCKeditorAPI.GetInstance('RespondEditor').InsertElement(element);
}

function ResponseModifySolutionLinks(id, prefix)
{
  var solutionText    = document.getElementById(id);
  var solutionLinks   = solutionText.getElementsByTagName("a");
  for (var x = 0; x < solutionLinks.length; x++) {
    if (solutionLinks[x].name == "solutionLink") {
      if (prefix.indexOf(".jsp") == -1) {
        if (prefix.charAt(prefix.length - 1) != "/")
          prefix = prefix + "/";

        solutionLinks[x].href = prefix + "main.jsp?t=solutionTab&solutionId=" + solutionLinks[x].id;
      }
      else {
        solutionLinks[x].href = prefix + "?t=solutionTab&solutionId=" + solutionLinks[x].id;
      }
    }
  }
  return solutionText.innerHTML;
}

/**
 * Check that a value has been set before trying to insert a template.  If it
 * has then go ahead and get it.
 * @param containerId the DOM id of the drop down containing the templates
 */
function ResponseGetTemplateAndInsert(containerId)
{
  var element = document.getElementById(containerId);
  if (element && element != null)
  {
    if (element.selectedIndex != 0)
    {
      // Invoke the action
      insertTemplate();
    }
  }
}


/**
 * Toggle a DOM element between display style 'none' or 'block'
 * @param id the DOM id of the element to toggle
 */
function ResponseToggleDisplay(id)
{
  ResponseToggleDisplayTo(id,"table-row-group");
}

/**
 * Toggle a DOM element between display style 'none' and a specified display style
 * @param id the DOM id of the element to toggle
 * @param displayStyle the style to set to when item is being displayed
 */
function ResponseToggleDisplayTo(id, displayStyle)
{
  var element = document.getElementById(id);

  if (element && element != null)
  {
    if (element.style.display == displayStyle)
    {
      element.style.display = "none";
    }
    else
    {
      element.style.display = displayStyle;
    }
  }
}

/**
 * Set the addresses area into a readonly or editable state.
 * @param state true for editable addresses, false for readonly addresses
 */
function ResponseSetAddressesEditable(state)
{
  var editableAddresses = document.getElementById(editableAddresses_div);
  var readonlyAddresses = document.getElementById(readOnlyAddresses_div);

  if (editableAddresses && editableAddresses != null && readonlyAddresses && readonlyAddresses != null)
  {
    if (state)
    {
      editableAddresses.style.display = "table-row-group";
      readonlyAddresses.style.display = "none";
    }
    else
    {
      editableAddresses.style.display = "none";
      readonlyAddresses.style.display = "table-row-group";
    }
  }
}

/**
 * Toggle the state of the addresses area between editable and readonly
 */
function ResponseToggleAddressesEditable()
{
  ResponseToggleDisplay('editableAddresses_div');
  ResponseToggleDisplay('readOnlyAddresses_div');
}

/**
 * Handle the find more solutions/documents link
 */
function ResponseFindMoreSolutions()
{
  // Set up the search
  //__ppr_findMoreSolutions.synchronizeTransaction = true;
  //__ppr_findMoreSolutions.transact();
  findMoreSolutions();

  // Switch to the find tab
  //__ppr_researchTab.transact();
  researchTab();
}

//-------------------------------------------------------
//  UTILITY FUNCTIONS
//-------------------------------------------------------
/**
 * Call the caf:validation function to validate the page. This will insert any client side errors into the page.
 * Should return true if all is OK, in which case the action should execute. Will return false if validation
 * fails, thus preventing the action from continuing.
 */
function ResponseIsMessageValid()
{
  var validationResult = validateResponseEmailPage();
  return validationResult;
}

/**
 * Unescape data that has been XML escaped by a c:out tag
 * This unescapes all characters that are defined in the JSTL1.1 spec to be escaped (Section 4.2)
 * @param str The string to unescape
 * @return The unescaped string
 */
function unescapeXml(str){
  return str.replace(/&gt;/gm, ">")
             .replace(/&lt;/gm, "<")
             .replace(/&amp;/gm, "&")
             .replace(/&apos;/gm, "'")
             .replace(/&#039;/gm, "'")
             .replace(/&quot;/gm, "\"")
             .replace(/&#034;/gm, "\"");
}

/**
 * Add an Agent Specified Attachment to the Response Message.
 */
function ResponseAddAgentAttachment()
{
  var windowId = document.getElementById("uploadWindowId");
  windowId.value = window.windowId;

  var element = document.getElementById("atg_arm_uploadAttachment");
  if (element && element != null)
  {
    // Only submit the form if a file has been chosen
    var fileElement = document.getElementById("uploadedAttachment");
    if (fileElement != null && fileElement.value != "")
    {


      var theForm = dojo.byId("agentAttachmentForm");

      theForm.encoding="multipart/form-data";

      element.click();

      showCurtain("mainCurtain");
      divSetVisible("uploadingPrompt_div");
    }
  }
}

/**
 * Refresh the attachment panel on the Outbound Message after
 * an Agent Specified Attachment has been added.
 */
function ResponseCompleteAttachmentRefresh()
{
  divSetHide("uploadingPrompt_div");
  hideCurtain("mainCurtain");

  // Clear the filename and description text fields
  var formElement = document.getElementById("agentAttachmentForm");
  if (formElement != null)
  {
    formElement.reset();
  }

  //__ppr_attachmentRefresh.transact();
  attachmentRefresh();
}

/**
 * Display any attachment errors.
 */
function ResponseAttachmentErrorsRefresh(validationErrors)
{
  divSetHide("uploadingPrompt_div");
  hideCurtain("mainCurtain");

  var errorsDiv = dojo.byId("responseErrorMessages");

  errorsDiv.innerHTML = validationErrors;

  ResponseCompleteAttachmentRefresh();
}

/**
 * Replace the value of the attachment id stored in the hidden field when
 * the user changes it.
 * @param attachmentId The id of the system attachment to add, or a comma seperated list of attachment IDs
 * if there is more than one to add.
 */
function ResponseAddSystemAttachment(attachmentId)
{
  var element = document.getElementById("addSystemAttachmentForm").attachmentId;

  if (element && element != null)
  {
    element.value = attachmentId;
    //__ppr_addSystemAttachment.transact();
    addSystemAttachment();
  }
}

/**
 * Removes the specified attachment from the outbound message
 * @param   attachmentId  The id of the system attachment to remove
 */
function ResponseRemoveAttachment(attachmentId)
{
  var element = document.getElementById("removeAttachmentForm").attachmentId;

  ResponseToggleAddAttachmentButtonsFor(attachmentId);

  if (element && element != null)
  {
    element.value = attachmentId;
    //__ppr_removeAttachment.transact();
    removeAttachment();
  }
}

/**
 * Adds an attachment from the content browser to the outbound message.
 * @param   attachmentId  The id of the system attachment to add.
 */
function ResponseAddAttachment(attachmentId)
{
  var element = document.getElementById("addSystemAttachmentForm").attachmentId;

  // Toggle the attach buttons
  ResponseToggleAddAttachmentButtonsFor(attachmentId);

  if (element && element != null)
  {
    element.value = attachmentId;

    // Attach the attachment
    //__ppr_addSystemAttachment.transact();
    addSystemAttachment();
  }
}

/**
 * Given an attachment id this function will toggle between the 'attach' buttons
 * and the 'already attached' message in the details area of the content browser
 * @param   attachmentId  The id of the attachment.
 */
function ResponseToggleAddAttachmentButtonsFor(attachmentId)
{
  // Toggle the attach buttons
  var buttonsId = "contentBrowserAttachment"+attachmentId;
  ResponseToggleDisplay(buttonsId);


  // Toggle the 'already attached' message
  var messageId = "contentBrowserAttachmentAlreadyAttached"+attachmentId;
  ResponseToggleDisplay(messageId);
}

/**
 * Calls the insertSolution action to insert the specified solution
 * in a response.
 * @param solutionId  The id of the solution to insert.
 */
function ResponseInsertSolution(solution)
{
  var element = document.getElementById("insertSolutionForm").solutionId;

  if (element && element != null)
  {
    element.value = solution;
    //__ppr_insertSolution.transact();
    insertSolution();
  }
}

/**
 * Inserts a link into the response allowing the specified recommeded answer
 * to be viewed in SelfService
 * @param url  The url to insert
 */
function ResponseInsertLink(url)
{
  if (url == null || url == "")
  {
    dijit.byId('messageBar').addMessage({type:'error', summary:getResource('response.error.no.site.selected')});
  }
  FCKeditorAPI.GetInstance('RespondEditor').InsertHtml(unescapeXml("<a href='" + url + "' >" + url + "</a>"));
}

/**
 * Strips the path off the agent attachment file name and adds
 * the remaining filename to the Display Text element.
 */
function ReponseSetAgentAttachmentDisplayName()
{
  var element = document.getElementById("uploadedAttachment");
  var filename = "";
  var separator = "";

  if (element && element != null)
  {
    filename = element.value;
    if (filename != null)
    {
      if (filename.charAt(0) == "/")
      {
        separator = "/";
      }
      else
      {
        separator = "\\";
      }
      filename = filename.substring(filename.lastIndexOf(separator) + 1);
    }

    element = document.getElementById("attachmentDisplayName");
    if (element && element != null)
    {
      element.value = filename;
    }
  }
}


/* Workaround for bug 116835/116546 - this function is a copy of that generated by the
caf:validationTrigger tag that is now not evaluated correctly by action
This function has been captured using Fiddler and pasted in here unmodified. */
function validateResponseEmailPage() {
  atgValidation_initValidationObjects();

  var inputValue = null;
  var validationResult = null;
  var validationInputObject = document.getElementById("outMessageForm").to;
  if (validationInputObject != null) {
    inputValue = atgValidation_escapeQuotes(validationInputObject.value);
    validationResult = eval("atgValidation_validateRequiredField('" + inputValue + "',false)");
    if (validationResult != validationCode.SUCCESS) {
      dijit.byId('messageBar').addMessage({type:'error', summary:validationCode.ERROR_EMPTY_STRING + ": To"});
      isSuccess = false;
    }
  }
  
  validationInputObject = document.getElementById("outMessageForm").htmlBody;  
  if (validationInputObject != null && validationInputObject.value != null) {
    inputValue = atgValidation_escapeQuotes(validationInputObject.value);
    validationResult = eval("atgValidation_validateRequiredField('" + inputValue + "',false)");    
    if (validationResult != validationCode.SUCCESS) {
      dijit.byId('messageBar').addMessage({type:'error', summary:validationCode.ERROR_EMPTY_STRING + ": Message Body"});
      isSuccess = false;
    }
  }
  return isSuccess;
}
/* End Workaround */

function nodeSelectedCallback(path, info)
{
  if(!window.path)
   window.path=new Object();
  window.path = path;
  if(!window.info)
    window.info=new Object();
  window.info = info;
  //alert('node selected: id=' + info.id + ' type=' + info.type);
  var element = document.getElementById("contentBrowserContentDetailsForm").contentBrowserId;
  if (element && element != null && info)
    element.value = info.id;
  element = document.getElementById("contentBrowserContentDetailsForm").contentBrowserType;
  if (element && element != null && info)
    element.value = info.type;
  //__ppr_contentBrowserContentDetails.transact();
  contentBrowserContentDetails();
}

function nodeSelectOnOPageReturn()
{
  nodeSelectedCallback(window.path, window.info);
}

/**
 * Replaces the innerHtml of targetId with the innerHtml of sourceId
 * @param targetId  The id of the target container
 * @param sourceId  The id of the source container
 */
function ResponseReplaceContentWith(targetId, sourceId)
{
  var targetElement = document.getElementById(targetId);
  var sourceElement = document.getElementById(sourceId);
  if (targetElement != null && sourceElement != null)
  {
    targetElement.innerHTML = sourceElement.innerHTML;
  }
}

/**
 * Hides the container with the given id
 * @param elementId The id of the container to hide.
 */
function ResponseHideContainer(elementId)
{
  var element = document.getElementById(elementId);
  if (element != null)
  {
    element.style.display = "none";
  }
}
function ResponseMarkupSpellingErrors() {
  var spellCheckerDiv      = document.getElementById("spellCheckDiv");
  var regexp;

  if (spellCheckerDiv.innerHTML.length < 3) {
    alert(getResource("editor.spellcheck.none"));
    return;
  }
  var currentValue = ResponseGetStrippedValue();
  for (var x = 0; x < spellCheckerDiv.childNodes.length; x++) {
    if (spellCheckerDiv.childNodes[x].getElementsByTagName) {
      var aMisspelledWords     = spellCheckerDiv.childNodes[x].getElementsByTagName("div");
      for (var i = 0; i < aMisspelledWords.length; i++) {
        var errorNode          = "<span class='spellerror'>" + aMisspelledWords[i].innerHTML + "</span>";
        var spellingMarkup     = aMisspelledWords[i].innerHTML.replace("\\", "\\\\").replace("?", "\\?").replace("[","\\[").replace("^","\\^");
        spellingMarkup         = spellingMarkup.replace("$", "\\$").replace(".","\\.").replace("|", "\\|").replace("*", "\\*").replace("+","\\+");
        spellingMarkup         = spellingMarkup.replace("(", "\\(").replace(")","\\)");
        regexp       = new RegExp("(?![^<]+>)\\b" + spellingMarkup + "\\b(?![^<]+>)", "g");
        currentValue = currentValue.replace(regexp, errorNode);
      }
    }
  }
  FCKeditorAPI.GetInstance('RespondEditor').SetHTML(currentValue);
}

function ResponseGetStrippedValue() {
  var aSpans       = FCKeditorAPI.GetInstance('RespondEditor').EditorDocument.getElementsByTagName("span");

  for (var i = aSpans.length; i > 0; i--)
  {
    if (aSpans[i-1].className.toUpperCase() == "SPELLERROR")
    {
      if (document.all)
      {
        aSpans[i-1].outerHTML = aSpans[i-1].innerHTML;
      }
      else
      {
        var outerRange = FCKeditorAPI.GetInstance('RespondEditor').EditorDocument.createRange();
        outerRange.setStartBefore(aSpans[i-1]);
        var fragment   = outerRange.createContextualFragment(aSpans[i-1].innerHTML);
        aSpans[i-1].parentNode.replaceChild(fragment, aSpans[i-1]);
      }
    }
  }
  return FCKeditorAPI.GetInstance('RespondEditor').GetHTML();
}
 /* Copyright (C) 1999-2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */
/*************************************************************************
//
// Service.Framework sidePanel.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// Defines client-side side panel behavior
//
*************************************************************************/

//##########################################################################
// 
// Methods for Ticketing Panel
//
//##########################################################################
function openByIdTicket(idValue){
  if(isEmpty(idValue))
    return;
  document.getElementById('globalViewTicketForm').ticketId.value=idValue;
  viewTicket('globalViewTicketForm');
}      
function openTicketTab(){
  var tab = document.getElementById("ticketsTab");
  if (!tab)
    return;
  // If not Already on Find tab? then switch to ticketTab
  if (!(tab.className && tab.className == "current"))
  {
    ticketsTab();
  }
}
function openCustomerInfo(idValue){
  if(isEmpty(idValue))
    return;
  document.getElementById('globalViewCustomerForm').customerId.value=idValue;      
  viewCustomer('globalViewCustomerForm');
}
function openWorkedTickets(){
  window.ticketing = new Object();
  window.ticketing.ticketAccessType="worked";     
  viewRecentTickets();
}
function openViewedTickets(){
  window.ticketing = new Object();
  window.ticketing.ticketAccessType="viewed";  
  viewRecentTickets();
}

//##########################################################################
// 
// Utility Functions
//
//##########################################################################
function isEmpty(strVal){
  if (strVal==null || strVal =="" || strVal.length==0)
    return true;
  strVal=trim(strVal);
  if (strVal.length==0)
    return true;
  else
    return false;
}
function trim(strVal) {
  // removing leading spaces
  while (strVal.substring(0,1) == ' ') {
    strVal = strVal.substring(1,strVal.length);
  }
  // removing trailing spaces
  while (strVal.substring(strVal.length-1,strVal.length) == ' ') {
    strVal = strVal.substring(0,strVal.length-1);
  }
  return strVal;
}
function replaceEscapeCharacters(oriStr){
  var escStr = oriStr.replace(/\\\\'/g, "'");
  escStr = escStr.replace(/\\\\\"/g, "\"");
  return escStr;
}

function eventTicketFind(e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var ev  = e || e.event;
  if (ev != null) {
    if (ev.keyCode == 13) {
        openByIdTicket(document.getElementById('OPTBID').OPBIDTicketText.value);
        dojo.stopEvent(ev);
        return false;
    }
  }
}

function eventOrderFind(e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var ev  = e || e.event;
  if (ev != null) {
    if (ev.keyCode == 13) {
        atg.commerce.csr.order.findByIdOrder(escape(document.getElementById('OPBIDOrder').OPBIDOrderText.value));
        dojo.stopEvent(ev);
        return false;
    }
  }
}
 /* Copyright (C) 1999-2009 Art Technology Group, Inc.
 */
/*************************************************************************
// ticketing.js File
// Defines client-side ticketing behavior
*************************************************************************/
dojo.getObject("atg.service.ticketing", true);

atg.service.ticketing.createFloatingPane = function(params, width, height, parent)
{
  params.duration = 1;
  var fp = new dojox.Dialog(params);
  //dojo.marginBox(fp.domNode, {w: width});
  //dojo.marginBox(fp.domNode, {h: height});
  if (parent) {
    parent.appendChild(fp.domNode);  
  }
  else {
    dojo.body().appendChild(fp.domNode);
  }
  return fp;
}
atg.service.ticketing.workViewingTicket = function (theFormId,ticketId)
{
  workTicket(theFormId, ticketId);
}
atg.service.ticketing.createNewTicket = function (theFormId)
{
  createTicket(theFormId);
}


atg.service.ticketing.reopenViewingTicket = function (theFormId,ticketId)
{
  reopenTicket(theFormId,ticketId);
}
atg.service.ticketing.addNewTicketNote = function(noteText, isPublic)
{
  window.noteText = noteText;
  window.isPublic = isPublic;
  addTicketNote('addTicketNoteForm',false);
}
atg.service.ticketing.addViewTicketNote = function(noteText, isPublic, formId)
{
  window.noteText = noteText;
  window.isPublic = isPublic;
  addTicketNote(formId, true);
}
atg.service.ticketing.archiveTicketPrompt = function () {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/archivePrompt.jsp?_windowid="+window.windowId);
  pane.onLoad = function() {
    pane._position();    
    // This sets the focus the first time the archive ticket prompt is loaded
    dojo.byId("archiveOk").focus();
  };
  //pane.resizeTo(410, 440);
  pane.show();
  // This sets the focus each subsequent time the assign ticket prompt is loaded
  if (dojo.byId("archiveOk")) {
    dojo.byId("archiveOk").focus();
  }  
}
atg.service.ticketing.cancelArchivePrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.assignTicketToAgentPrompt = function(theAction) {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/assignTicketToAgentPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.assign-ticket-to-agent.title");
  pane.onLoad = function() {
    dojo.byId("assignTicketToAgentShare").checked=false;
    dojo.byId("assignTicketToAgentNote").value="";
    var okbutton = dojo.byId("assignAgentOk");
    var actionToTake = function () {
      var theForm = dojo.byId("assignTicketToAgentForm");
      window.agent=theForm.agentSelect.value;
      window.reasonCode=theForm.assignTicketToAgentReasonCode.value;
      window.comment=theForm.assignTicketToAgentNote.value;
      window.share=theForm.assignTicketToAgentShare.checked;
      theAction();
      atg.service.ticketing.cancelAssignTicketToAgentPrompt();
      return false;
    };
    okbutton.onclick=actionToTake;
    pane._position();    
    dojo.byId("assignTicketToAgentForm").agentSelect.focus();
  };
  //pane.resizeTo(410, 330);
  pane.show();

  // This sets the focus each subsequent time the assign ticket prompt is loaded
  if (dojo.byId("assignTicketToAgentForm")) {
    dojo.byId("assignTicketToAgentForm").agentSelect.focus();
  }
}
atg.service.ticketing.cancelAssignTicketToAgentPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}

atg.service.ticketing.closeTicketPrompt = function(theAction) {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/closeTicketPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.close-ticket.title");
  pane.onLoad = function() {
    dojo.byId("closeTicketShare").checked=false;
    dojo.byId("closeTicketNote").value="";
    var okbutton = dojo.byId("closeOk");
    var actionToTake = function ()
    {
      var theForm = dojo.byId("closeTicketForm");
      window.reasonCode=theForm.closeReasonCode.value;
      window.comment=theForm.closeTicketNote.value;
      window.share=theForm.closeTicketShare.checked;
      theAction();
      atg.service.ticketing.cancelCloseTicketPrompt();
      return false;
    };
    okbutton.onclick=actionToTake;
    pane._position();    
    // This sets the focus the first time the close ticket prompt is loaded
    dojo.byId("closeTicketForm").closeReasonCode.focus();
  };
  //pane.resizeTo(415, 300);
  pane.show();
  // This sets the focus each subsequent time the close ticket prompt is loaded
  if (dojo.byId("closeTicketForm")) {
    dojo.byId("closeTicketForm").closeReasonCode.focus();
  }
}
atg.service.ticketing.cancelCloseTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.deferTicketFromPopup = function() {
  var theForm = dojo.byId("deferTicketForm");
  window.date=theForm.deferUntil.value;
  window.retain=theForm.deferTicketRetain.checked;
  window.reasonCode=theForm.deferReasonCode.value;
  window.comment=theForm.deferTicketNote.value;
  window.share=theForm.deferTicketShare.checked;
  atg.service.ticketing.deferTicketFunc();
  atg.service.ticketing.cancelDeferTicketPrompt();
  return false;
}
atg.service.ticketing.deferTicketPrompt = function(theAction) {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/deferTicketPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.defer-ticket.title");
  pane.onLoad = function() {
    initializeDeferPane(theAction);    
    pane._position();    
  };
  //pane.resizeTo(450, 390);
  pane.show();
  // This sets the focus each subsequent time the defer ticket prompt is loaded
  if (dojo.byId("deferTicketForm")) {
    dojo.byId("deferTicketForm").deferUntil.focus();
  }
}
atg.service.ticketing.cancelDeferTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}

atg.service.ticketing.escalateTicketPrompt = function (theAction) {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/escalateTicketPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.escalate-ticket.title");
  pane.onLoad = function() {
    dojo.byId("escalateTicketShare").checked=false;
    dojo.byId("escalateTicketNote").value="";
    var okbutton = dojo.byId("escalateTicketOk");
    var actionToTake = function ()
    {
      var theForm = document.getElementById("escalateTicketForm");
      window.escalationLevel=theForm.escalatePromptSelect.value;
      window.group=theForm.escalateGroupSelect.value;
      window.reasonCode=theForm.escalateReasonCode.value;
      window.comment=theForm.escalateTicketNote.value;
      window.share=theForm.escalateTicketShare.checked;
      theAction();
      atg.service.ticketing.cancelEscalateTicketPrompt();
      return false;
    };
    okbutton.onclick=actionToTake;    
    pane._position();    
    // This sets the focus the first time the escalate ticket prompt is loaded
    dojo.byId("escalateTicketForm").escalateGroupSelect.focus();
  };
  //pane.resizeTo(500, 380);
  pane.show();
  // This sets the focus each subsequent time the escalate ticket prompt is loaded
  if (dojo.byId("escalateTicketForm")) {
    dojo.byId("escalateTicketForm").escalateGroupSelect.focus();
  }
}
atg.service.ticketing.cancelEscalateTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}

atg.service.ticketing.releaseTicketPrompt = function (theAction) {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/releaseTicketPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.release-ticket.title");
  pane.onLoad = function() {
    dojo.byId("releaseTicketShare").checked=false;
    dojo.byId("releaseTicketNote").value="";
    var okbutton = dojo.byId("releaseTicketOk");
    var actionToTake = function ()
    {
      var theForm = document.getElementById("releaseTicketForm");
      window.reasonCode=theForm.releaseReasonCode.value;
      window.comment=theForm.releaseTicketNote.value;
      window.share=theForm.releaseTicketShare.checked;
      theAction();
      atg.service.ticketing.cancelReleaseTicketPrompt();
      return false;
    };
    okbutton.onclick=actionToTake;
    pane._position();    
    // This sets the focus the first time the release ticket prompt is loaded
    dojo.byId("releaseTicketForm").releaseReasonCode.focus();
  };
  //pane.resizeTo(425, 300);
  pane.show();
  // This sets the focus each subsequent time the release ticket prompt is loaded
  if (dojo.byId("releaseTicketForm")) {
    dojo.byId("releaseTicketForm").releaseReasonCode.focus();
  }
}
atg.service.ticketing.cancelReleaseTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.sendTicketPrompt = function(theAction) {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/sendTicketToGroupPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.send-to-group.title");
  pane.onLoad = function() {
    dojo.byId("sendTicketShare").checked=false;
    dojo.byId("sendTicketNote").value="";
    var okbutton = dojo.byId("sendTicketOk");
    var actionToTake = function ()
    {
      var theForm = document.getElementById("sendTicketForm");
      window.sendToGroup=theForm.groupSelect.value;
      window.reasonCode=theForm.sendTicketReasonCode.value;
      window.comment=theForm.sendTicketNote.value;
      window.share=theForm.sendTicketShare.checked;
      theAction();
      atg.service.ticketing.cancelSendTicketPrompt();
      return false;
    };
    okbutton.onclick=actionToTake;
    pane._position();    
    // This sets the focus the first time the send ticket prompt is loaded
    dojo.byId("sendTicketForm").groupSelect.focus();
  };
  //pane.resizeTo(410, 330);
  pane.show();
  // This sets the focus each subsequent time the send ticket prompt is loaded
  if (dojo.byId("sendTicketForm")) {
    dojo.byId("sendTicketForm").groupSelect.focus();
  }
}
atg.service.ticketing.cancelSendTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.addNotePrompt = function() {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/addNotePrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.enter-note.title");
  pane.onLoad = function() {
    dojo.byId("addNoteShare").checked=false;
    dojo.byId("addNoteNote").value="";
    pane._position();    
    // This sets the focus the first time the add note prompt is loaded
    dojo.byId("addNoteOk").focus();
  };
  //pane.resizeTo(410, 440);
  pane.show();
  // This sets the focus each subsequent time the add note prompt is loaded
  if (dojo.byId("addNoteOk")) {
    dojo.byId("addNoteOk").focus();
  }
}
atg.service.ticketing.cancelAddNotePrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.addCallActivityPrompt = function() {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/addCallActivityPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.add-call-note.title");
  pane.onLoad = function() {
    dojo.byId("addCallActivityShare").checked=false;
    dojo.byId("addCallActivityNote").value="";
    dojo.byId("noteDirection").checked=true;
    pane._position();    
    // This sets the focus the first time the add call activity prompt is loaded
    dojo.byId("addCallActivityOk").focus();
  };
  //pane.resizeTo(410, 440);
  pane.show();
  // This sets the focus each subsequent time the add call activity prompt is loaded
  if (dojo.byId("addCallActivityOk")) {
    dojo.byId("addCallActivityOk").focus();
  }
}
atg.service.ticketing.cancelAddCallActivityPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.associateTicketPrompt = function() {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/associateTicketPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.associate-ticket.title");
  pane.onLoad = function() {
    dojo.byId("associateTicketId").value='';
    pane._position();    
    // This sets the focus the first time the associate ticket prompt is loaded
    dojo.byId("associateTicketId").focus();
  };
  //pane.resizeTo(410, 440);
  pane.show();
  // This sets the focus each subsequent time the associate ticket prompt is loaded
  if (dojo.byId("associateTicketId")) {
    dojo.byId("associateTicketId").focus();
  }
}
atg.service.ticketing.cancelAssociateTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.mergeTicketPrompt = function() {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/mergeTicketPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.close-ticket.title");
  pane.onLoad = function() {
    dojo.byId("mergeTicketId").value='';
    pane._position();    
    // This sets the focus the first time the merge ticket prompt is loaded
    dojo.byId("mergeTicketOk").focus();
  };
  //pane.resizeTo(410, 440);
  pane.show();
  // This sets the focus each subsequent time the merge ticket prompt is loaded
  if (dojo.byId("mergeTicketOk")) {
    dojo.byId("mergeTicketOk").focus();
  }
}
atg.service.ticketing.cancelMergeTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
atg.service.ticketing.closeAsDuplicateTicketPrompt = function() {
  var pane = dijit.byId("ticketPromptPane");
  pane.setHref(window.contextPath + "/include/ticketing/closeAsDuplicateTicketPrompt.jsp?_windowid="+window.windowId);
  pane.titleNode.innerHTML=getResource("popup.close-as-duplicate-ticket.title");
  pane.onLoad = function() {
    dojo.byId("closeAsDuplicateTicketId").value='';
    pane._position();
    // This sets the focus the first time the close as duplicate ticket prompt is loaded
    dojo.byId("closeAsDuplicateTicketId").focus();
  };
  //pane.resizeTo(410, 440);
  pane.show();
  // This sets the focus each subsequent time the close as duplicate ticket prompt is loaded
  if (dojo.byId("closeAsDuplicateTicketId")) {
    dojo.byId("closeAsDuplicateTicketId").focus();
  }
}
atg.service.ticketing.cancelCloseAsDuplicateTicketPrompt = function() {
  dijit.byId("ticketPromptPane").hide();
}
// Functions used to conditionalize the various prompts
atg.service.ticketing.escalateTicketFunc = function() { escalateTicket(); }
atg.service.ticketing.closeTicketFunc = function() { closeTicket('closeTicketForm'); }
atg.service.ticketing.releaseTicketFunc = function() { releaseTicket(); }
atg.service.ticketing.deferTicketFunc = function() { deferTicket(); }
atg.service.ticketing.assignTicketFunc = function() { reassignTicket(); }
atg.service.ticketing.sendTicketFunc = function() { sendTicket(); }

atg.service.ticketing.respondEscalateTicketFunc = function() { respondEscalateTicket(); }
atg.service.ticketing.respondCloseTicketFunc = function() { respondCloseTicket('closeTicketForm'); }
atg.service.ticketing.respondReleaseTicketFunc = function() { respondReleaseTicket(); }
atg.service.ticketing.respondDeferTicketFunc = function() { respondDeferTicket(); }
atg.service.ticketing.respondAssignTicketFunc = function() { respondReassignTicket(); }
atg.service.ticketing.respondSendTicketFunc = function() { respondSendTicket(); }

atg.service.ticketing.getMemberValue = function(){
 var theForm = document.getElementById("customerCreateForm");
 alert("Checking customerCreateForm: " + theForm);
 alert("Checking theForm.member: " + theForm.member);
 for (var i=0; i < theForm.member.length; i++){ //>
  if (theForm.member[i].checked){
   document.member = theForm.member[i].value;
  }
 }
}
atg.service.ticketing.getReceiveEmailValue = function(){
 var theForm = document.getElementById("customerCreateForm");
 for (var i=0; i < theForm.receiveEmail.length; i++){ //>
  if (theForm.receiveEmail[i].checked){
   document.receiveEmail = theForm.receiveEmail[i].value;
  }
 }
}
atg.service.ticketing.getPushableValue = function(){
 var theForm = document.getElementById("ticketForm");
 for (var i=0; i < theForm.pushable.length; i++){ //>
  if (theForm.pushable[i].checked){
   document.pushable = theForm.pushable[i].value;
  }
 }
}
atg.service.ticketing.getPastOrFromToValue = function(){
  var theForm = document.getElementById("ticketSearchForm");
  for (var i=0; i < theForm.pastOrFromTo.length; i++){
    if (theForm.pastOrFromTo[i].checked){
      document.pastOrFromTo = theForm.pastOrFromTo[i].value;
    }
  }
}
atg.service.ticketing.getPastOrFromTo2Value = function(){
  var theForm = document.getElementById("ticketSearchForm");
  for (var i=0; i < theForm.pastOrFromTo2.length; i++){
    if (theForm.pastOrFromTo2[i].checked){
      document.pastOrFromTo2 = theForm.pastOrFromTo2[i].value;
  }
 }
}

atg.service.ticketing.resetPassword = function()
{
 resetCustomerPassword();
}
atg.service.ticketing.isEmpty = function(strVal){
 if (strVal===null || strVal ==="" || strVal.length===0){
  return true;
 }
 strVal=trim(strVal);
 if (strVal.length===0){
  return true;
 }
 else{
  return false;
 }
}
atg.service.ticketing.validate_email = function(emailAddress) {
 if (!atg.service.ticketing.isEmpty(emailAddress)) {
  if (emailAddress.indexOf("@") == -1 || emailAddress.indexOf(".") == -1 ) {
   return false;
  } else {
   return true;
  }
 } else {
  return false;
 }
}
atg.service.ticketing.validate_form = function(){
 var theForm = document.getElementById("customerCreateForm");
 if (atg.service.ticketing.isEmpty(theForm.firstName.value)
  || atg.service.ticketing.isEmpty(theForm.lastName.value)
  || atg.service.ticketing.isEmpty(theForm.login.value)
  || atg.service.ticketing.isEmpty(theForm.email.value)) {
   alert("Please fill in all required fields.");
   return false;
 }
 if (atg.service.ticketing.validate_email(theForm.email.value)===false){
  alert("Please enter a valid email address.");
  theForm.email.focus();
  return false;
 }
}
atg.service.ticketing.validate_edit_form = function(){
 var theForm = document.getElementById("customerCreateForm");
 if (atg.service.ticketing.isEmpty(theForm.firstName.value)
  || atg.service.ticketing.isEmpty(theForm.lastName.value)
  || atg.service.ticketing.isEmpty(theForm.login.value)) {
   alert("Please fill in all required fields.");
   return false;
 }
 if ( !atg.service.ticketing.isEmpty(theForm.email.value)){
  if (atg.service.ticketing.validate_email(theForm.email.value)===false){
   alert("Please enter a valid email address.");
   theForm.email.focus();
   return false;
  }
 }
}
atg.service.ticketing.handleEnterSubmission = function(action, evt) {
  var keyCode = evt.which ? evt.which : evt.keyCode;
  if (keyCode == 13) {
    action();
    return false;
  }
  else
  {
    return true;
  }
}
atg.service.ticketing.associateTicketFromPopupIfValueSet = function(){
  if (document.getElementById('associateTicketId').value==''){
    return false;
  }
  window.ticketId=document.getElementById('associateTicketId').value;
  associateTicket();
  atg.service.ticketing.cancelAssociateTicketPrompt(); 
  return false;
}
atg.service.ticketing.closeTicketAsDuplicateFromPopupIfValueSet = function(){
  window.reasonCode='Duplicate';
  window.ticketId=document.getElementById('closeAsDuplicateTicketId').value;
  window.noteText='';
  closeTicket('closeAsDuplicateTicketForm');
  atg.service.ticketing.cancelCloseAsDuplicateTicketPrompt();
  return false;
}


 atg.service.ticketing.init_ticketSearchForm  = function (){
   atg.service.ticketing.checkFirstRadioButton("pastOrFromTo");
   atg.service.ticketing.checkFirstRadioButton("pastOrFromTo2");
   atg.service.ticketing.selectFirstSelectValue("past");
   atg.service.ticketing.selectFirstSelectValue("past2");
 }

 atg.service.ticketing.checkFirstRadioButton = function (radio_name){
   var els = document.getElementsByName(radio_name);
   var checked = false;
   for (var el in els){
     if (el.checked){
       checked = true;
       break;
     }
   }
   if (!checked){
     els[0].checked = true;
   }
 }

 atg.service.ticketing.selectFirstSelectValue = function (select_name){
   var el = document.getElementsByName(select_name);
   if (el[0].selectedIndex == 0)
   el[0].selectedIndex = 1 ;
 }

 atg.service.ticketing.updateCheckBox = function(checkbox_id){
   var el=document.getElementById(checkbox_id);
   el.checked=true;
 }
//*************************************************************************
//
// topicTree.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// This page defines the topic tree object. This object is just a base
// for the topic tree.  All display is done in the topicLoad.jsp page.  
//
//*************************************************************************

function TopicTree(id, div)
{
	if (div != null && div != "undefined") {
		this.topicTree = div;
	}
	else {
		this.topicTree = document.createElement("div");
	}
  
  // Set tree properties
  //
  this.topicTree.id                    = id;
  this.topicTree.form                  = null;
  this.topicTree.locale                = "";
  this.topicTree.selectAction          = new Function();
  this.topicTree.openAction            = new Function();
  this.topicTree.closeAction           = new Function();
  this.topicTree.loadAction            = new Function();
  
  
  // Set Tree methods
  //  
  this.topicTree.openBranch            = topicTreeOpenBranch;
  this.topicTree.selectBranch          = topicTreeSelectBranch;
  this.topicTree.closeBranch           = topicTreeCloseBranch;
  this.topicTree.load                  = topicTreeLoad;
  
  return this.topicTree;
}

function topicTreeOpenBranch(topicId, insertId, treeId, closedIcon, openIcon)
{
  var tree                      = document.getElementById(treeId);
  
  // Change the tree branch icon
  //
  var openIconImg               = document.getElementById(openIcon);
  openIconImg.style.display     = "";
  var closedIconImg             = document.getElementById(closedIcon);
  closedIconImg.style.display   = "none";
  
  // Are the children allready loaded?
  // If so, just show them, if not, reload
  //
  var insertArea            = document.getElementById(insertId);
  if (insertArea.style.display != "none")
    tree.load(topicId, insertId);
  else
    insertArea.style.display = "";
    
  // Call integration function
  //
  tree.openAction(topicId);
}

function topicTreeSelectBranch(topicId, topicName, treeId)
{
  var tree                      = document.getElementById(treeId);
  tree.selectAction(topicId, topicName, treeId); 
}

function topicTreeCloseBranch(displayId, treeId, closedIcon, openIcon)
{
  var tree                  = document.getElementById(treeId);
  var displayArea           = document.getElementById(displayId);
  
  
  // Change the tree branch icon
  //
  var closedIconImg            = document.getElementById(closedIcon);
  closedIconImg.style.display  = "";
  var openIconImg              = document.getElementById(openIcon);
  openIconImg.style.display    = "none";
  
  displayArea.style.display = "none";
}

function topicTreeLoad(topicId, targetId)
{
  var tree                  = this;
  var theForm               = tree.form;
  
  if (theForm.id && (theForm.innerHTML == ""))
  {
    theForm = document.getElementById(theForm.id);
  }
  
  if (theForm)
  {
    theForm.targetId.value = targetId;
    if (topicId != "") theForm.topicId.value = topicId;
    theForm.treeId.value = tree.id;
		theForm["parameterMap.topicsLocale"].value = tree.locale;
	  atgSubmitAction({
	  	form: theForm
	  });
  }
}function getTreeTable(treeTableId) {
  //console.debug("getTreeTable");
  var treeTable = null;
  for (var i = 0; i < window.treeTables.length; i++) {
    if (window.treeTables[i].treeTableId == treeTableId) {
      treeTable = window.treeTables[i];
      break;
    }
  }
  return treeTable;
}
function TreeTable(treeTableId) {
  //console.debug("TreeTable");
  this.partialPageRenderer = null;
  this.controlBars = new Array();
  this.selectedItem = null;
  this.stateSavingMethod = "client";
  this.treeTableId = treeTableId;
}
function getAttributeValue(element, name) {
  //console.debug("getAttributeValue");
  var value = "";
  try {
    value = element.attributes.getNamedItem(name).nodeValue;
  }
  catch (ex) {
  }
  return value;
}
function setAttributeValue(element, name, value) {
  //console.debug("setAttributeValue");
  try {
    element.attributes.getNamedItem(name).nodeValue = value;
  }
  catch (ex) {
  }
}
function TreeTableAddForm(treeTableId, name, value) {
  //console.debug("TreeTableAddForm");
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var partialPageRenderer = treeTable.partialPageRenderer;
  if (!partialPageRenderer)
    return;

  partialPageRenderer.addForm(name, value, true);
}
function TreeTableEnd(treeTableId) {
  //console.debug("TreeTableEnd");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonLast = document.getElementById(treeTableId + controlBarId + "ControlBarlast");
    if (pagingButtonLast) {
      TreeTableFireClickEvent(pagingButtonLast);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTableEnter(selectedItem) {
  //console.debug("TreeTableEnter");
  var isHandled = false;
  var spanTags = selectedItem.getElementsByTagName("span");
  for (var i = 0; i < spanTags.length; i++) {
    var spanTag = spanTags[i];
    if (spanTag.id) {
      var idComponents = spanTag.id.split(":");
      if (!idComponents || idComponents.length != 3)
        continue;
    
      var type = idComponents[1];
      if (type == "expandButton") {
        TreeTableFireClickEvent(spanTag);
        break;
      }
    }
  }
  return isHandled;
}
function TreeTableExecuteOperation(operation, treeTableId, parameters) {
  //console.debug("TreeTableExecuteOperation");
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var partialPageRenderer = treeTable.partialPageRenderer;
  var action = treeTable.action;
  if (!partialPageRenderer && !action)
    return;

  if (partialPageRenderer) {
    var protocol = partialPageRenderer.getProtocol();
    if (protocol == "formhandlers") { // form handlers
      partialPageRenderer.addForm("operation", operation, true);
    }
    else { // struts
      partialPageRenderer.addForm("method", operation, true);
    }

    // Populate form pairs for invoked method
    //
    partialPageRenderer.addForm("treeTableId", treeTableId, true);
    if (parameters) {
      for (var i = 0; i < parameters.length; i++) {
        if (parameters[i].hasValue) {
          partialPageRenderer.addForm(parameters[i].name, parameters[i].value, true);
        }
        else {
          partialPageRenderer.addForm(parameters[i].name, eval("document.getElementById('" + parameters[i].elementId + "')." + parameters[i].elementProperty + ";"), true);
        }
      }
    }

    if (treeTable.stateSavingMethod == "client") {
      partialPageRenderer.addForm("state", document.getElementById(treeTableId + "State").innerHTML, true);
    }
    // Submit form pairs via HTTP POST
    //
    partialPageRenderer.transact();
  }
  else if (action) {
    // new style
    var state = "";
    if (document.getElementById(treeTableId + "State")) {
      state = document.getElementById(treeTableId + "State").innerHTML;
      action(operation, parameters,state);
    } else {
      action(operation, parameters);
    }
  }
}
function TreeTableFilterSelectChange(component, e) {
  //console.debug("TreeTableFilterSelectChange");
  if (component.options) {
    for (var i = 0; i < component.options.length; i++) {
      if (component.options[i].selected) {
        TreeTableTransact("filter", component.options[i].value);
        break;
      }
    }
  }
}
function TreeTableFireClickEvent(element) {
  //console.debug("TreeTableFireClickEvent");
  if (element == null)
    return;

  var isIe = false;
  if (document.all) { // IE
    isIe = true;
  }

  if (isIe) {
    element.fireEvent("onclick");
  }
  else {
    var event = document.createEvent("MouseEvents");
    event.initMouseEvent("click", true, true, this.defaultView, 0, element.screenX + 1, element.screenY + 1, 1, 1, false, false, false, false, 0, null);
    element.dispatchEvent(event);
  }
}
function TreeTableGetFirstControlBarId(treeTableId) {
  //console.debug("TreeTableGetFirstControlBarId");
  var controlBarId = null;
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  if (treeTable.controlBars.length > 0) {
    controlBarId = treeTable.controlBars[0];
  }
  return controlBarId;
}
function TreeTableHome(treeTableId) {
  //console.debug("TreeTableHome");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonFirst = document.getElementById(treeTableId + controlBarId + "ControlBarfirst");
    if (pagingButtonFirst) {
      TreeTableFireClickEvent(pagingButtonFirst);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTableKeydown(treeTableId, e) {
  //console.debug("TreeTableKeydown");
  var isIe = false;
  var isHandled = false;
  if (document.all) { // IE
    e = event;
    isIe = true;
  }

  var keyCode = e.keyCode;
  var body = document.getElementById(treeTableId + "Body");
	if(!body) {
		try {
			document.detachEvent('onkeydown', eval("OnKeydown" + treeTableId) ); // doesn't work often?
    } catch(e) { /*alert('OnKeydown' + treeTableId + ' error: ' + e.message); */}

    return;
	}
	
  var selectedItem = null;

  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var selectedItemId = treeTable.selectedItem;
  if (selectedItemId) {
    selectedItem = document.getElementById(selectedItemId);
  }
  switch (keyCode) {
  case 13: // enter
    if (selectedItem) {
      isHandled = TreeTableEnter(selectedItem);
    }
    break;
  case 33: // page up
    isHandled = TreeTablePageUp(treeTableId);
    break;
  case 34: // page down
    isHandled = TreeTablePageDown(treeTableId);
    break;
  case 35: // end
    if (e.ctrlKey) {
      isHandled = TreeTableEnd(treeTableId);
    }
    else {
      isHandled = TreeTableSelectLastItem(body);
    }
    break;
  case 36: // home
    if (e.ctrlKey) {
      isHandled = TreeTableHome(treeTableId);
    }
    else {
      isHandled = TreeTableSelectFirstItem(body);
    }
    break;
  case 38: // up arrow
    if (selectedItem) {
      isHandled = TreeTableSelectPreviousItem(selectedItem);
    }
    else {
      isHandled = TreeTableSelectLastItem(body);
    }
    break;
  case 40: // down arrow
    if (selectedItem) {
      isHandled = TreeTableSelectNextItem(selectedItem);
    }
    else {
      isHandled = TreeTableSelectFirstItem(body);
    }
    break;
  default:
    break;
  }
  if (isHandled) {
    if (isIe) { // IE
      e.returnValue = false;
      e.cancelBubble = true;
    }
    else {
      e.preventDefault();
      e.stopPropagation();
    }
  }
}
function TreeTablePageDown(treeTableId) {
  //console.debug("TreeTablePageDown");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonNext = document.getElementById(treeTableId + controlBarId + "ControlBarnext");
    if (pagingButtonNext) {
      TreeTableFireClickEvent(pagingButtonNext);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTablePageUp(treeTableId) {
  //console.debug("TreeTablePageUp");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonPrevious = document.getElementById(treeTableId + controlBarId + "ControlBarprevious");
    if (pagingButtonPrevious) {
      TreeTableFireClickEvent(pagingButtonPrevious);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTableRefresh(treeTableId) {
  //console.debug("TreeTableRefresh");
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  TreeTableExecuteOperation("refresh", treeTableId, null);
}
function TreeTableSelectChangeEventHandler(action, operation, component) {
  //console.debug("TreeTableSelectChangeEventHandler");
  if (component.options) {
    for (var i = 0; i < component.options.length; i++) {
      if (component.options[i].selected) {
        TreeTableTransactEventHandler(action, operation, component.options[i].value);
        break;
      }
    }
  }
}
function TreeTableSelectFirstItem(tbody) {
  //console.debug("TreeTableSelectFirstItem");
  var isHandled = false;
  if(!tbody)
    return isHandled;
  var firstItem = tbody.firstChild;
  while (firstItem && !firstItem.tagName && firstItem.tagName != "table") {
    firstItem = firstItem.nextSibling;
  }
  if (firstItem) {
    TreeTableFireClickEvent(firstItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSelectLastItem(tbody) {
  //console.debug("TreeTableSelectLastItem");
  var isHandled = false;
  if(!tbody)
    return isHandled;  
  var lastItem = tbody.lastChild;
  while (lastItem && !lastItem.tagName && lastItem.tagName != "table") {
    lastItem = lastItem.previousSibling;
  }
  if (lastItem) {
    TreeTableFireClickEvent(lastItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSelectNextItem(item) {
  //console.debug("TreeTableSelectNextItem");
  var isHandled = false;
  var nextItem = item.nextSibling;
  while (nextItem && !nextItem.tagName && nextItem.tagName != "table") {
    nextItem = nextItem.nextSibling;
  }
  if (nextItem) {
    TreeTableFireClickEvent(nextItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSelectPreviousItem(item) {
  //console.debug("TreeTableSelectPreviousItem");
  var isHandled = false;
  var previousItem = item.previousSibling;
  while (previousItem && !previousItem.tagName && previousItem.tagName != "table") {
    previousItem = previousItem.previousSibling;
  }
  if (previousItem) {
    TreeTableFireClickEvent(previousItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSortSelectChange(component, e) {
  //console.debug("TreeTableSortSelectChange");
  if (component.options) {
    for (var i = 0; i < component.options.length; i++) {
      if (component.options[i].selected) {
        TreeTableTransact("sort", component.options[i].value, null);
        break;
      }
    }
  }
}
function TreeTableTransact(operation, id) {
  //console.debug("TreeTableTransact");
  var idComponents = id.split(":");
  if (!idComponents || idComponents.length != 3)
    return;

  var treeTableId = idComponents[0];
  var parameters = idComponents[2];

  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var partialPageRenderer = treeTable.partialPageRenderer;
  var action = treeTable.action;
  if (!partialPageRenderer && !action)
    return;

  if (partialPageRenderer)
  {
    var protocol = partialPageRenderer.getProtocol();
    if (protocol == "formhandlers") { // form handlers
      partialPageRenderer.addForm("operation", operation, true);
    }
    else { // struts
      partialPageRenderer.addForm("method", operation, true);
    }

    // Populate form pairs for invoked method
    //
    partialPageRenderer.addForm("parameters", parameters, true);
    partialPageRenderer.addForm("treeTableId", treeTableId, true);

    if (treeTable.stateSavingMethod == "client") {
      partialPageRenderer.addForm("state", document.getElementById(treeTableId + "State").innerHTML, true);
    }

    // Submit form pairs via HTTP POST
    //
    partialPageRenderer.transact();
  }
  else if (action){
    if (action.length < 3) {
      alert("Your action " + action + " is missing its (operation, parameters, state) parameters.  Please go add them and set them into the matching values in your form");
    }
    var state;
    if (treeTable.stateSavingMethod == "client") {
      state = document.getElementById(treeTableId + "State").innerHTML;
    }
    action(operation, parameters, state);
  }
}
function TreeTableTransactEventHandler(actionString, operation, id) {
  //console.debug("TreeTableTransactEventHandler");
  var idComponents = id.split(":");
  if (!idComponents || idComponents.length != 3)
    return;

  var partialPageRenderer = window["__ppr_" + actionString];
  var action = window[actionString];
  
  if (partialPageRenderer){
    var protocol = partialPageRenderer.getProtocol();
    if (protocol == "formhandlers") { // form handlers
      partialPageRenderer.addForm("operation", operation, true);
    }
    else { // struts
      partialPageRenderer.addForm("method", operation, true);
    }

    // Populate form pairs for invoked method
    //
    var parameters = idComponents[2];
    partialPageRenderer.addForm("parameters", parameters, true);

    var treeTableId = idComponents[0];
    partialPageRenderer.addForm("treeTableId", treeTableId, true);

    // Submit form pairs via HTTP POST
    //
    partialPageRenderer.transact();
  }
  else if (action){
    if (action.length < 2) {
      alert("Your action " + action + " is missing its (operation, parameters) parameters.  Please go add them and set them into the matching values in your form");
    }
    action(operation, parameters);
  }
}
function atgSetupTreeTable(theForm, operation, parameters, state) {
  //console.debug("atgSetupTreeTable");
  if (operation) {
    theForm.operation.value = operation;
  } else {
    theForm.operation.value = "refresh";
  }
  if (parameters){
    theForm.parameters.value = parameters;
  }
  if (state) {
    theForm.state.value= state;
  }
}
function TreeTableCancelEvent(event){
  //console.debug("TreeTableCancelEvent");
	if (!event) event = window.event;
	event.cancelBubble = true;
	if (event.stopPropagation) event.stopPropagation();
	if (event.preventDefault) event.preventDefault();
  return false;
}
function treeTableHandleOnLoad(idValue){
  //console.debug("treeTableHandleOnLoad -- " + idValue);
  var headTagId = idValue + "HeadTag";
  var head = document.getElementById(headTagId);
  var bodyTagId = idValue + "BodyTag";
  var body = document.getElementById(bodyTagId);
  if (head && body) { head.style.width = body.offsetWidth; }
}
function treeTableHandleOnResize(idValue){
  //console.debug("treeTableHandleOnResize -- " + idValue);
  var headTagId = idValue + "HeadTag";
  var bodyTagId = idValue + "BodyTag";
  var head = document.getElementById(headTagId); 
  var body = document.getElementById(bodyTagId); 
  if (head && body) { 
    head.style.width = body.offsetWidth; 
  }
}
if (!window.treeTables) window.treeTables = new Array();
function okDiv(divObject_id) {
  hideDiv(divObject_id);
}

function hideDiv(divObject_id) {
  var theDiv = document.getElementById(divObject_id);
  var imgClicked = document.getElementById("img" + divObject_id);
  if (theDiv) {
    theDiv.style.display = "none";
    imgClicked.src = "image/icons/icon_closeNavItem.gif";
  } else {
    alert('Div element not found: ' + divObject_id);
  }
}

function toggleDivs(divObject_id) {
  var theDiv = document.getElementById(divObject_id);
  var imgClicked = document.getElementById("img" + divObject_id);
  if (theDiv) {
    var theStyle = theDiv.style.display;
    if (theStyle == "none") {
      theDiv.style.display = "block";
      if (imgClicked) {
        imgClicked.src = "image/icons/icon_openNavItem.gif";
      } else {
        alert("image not found: " + "img" + divObject_id);
      }
    } else {
      theDiv.style.display = "none";
      imgClicked.src = "image/icons/icon_closeNavItem.gif";
    }
  } else {
    alert('Div element not found: ' + divObject_id);
  }
}

function expandDiv(divObject_id) {
  var theDiv = document.getElementById(divObject_id);
  if (theDiv) {
    var theStyle = theDiv.style.display;
    theDiv.style.display = "block";
    var imgClicked = document.getElementById("img" + divObject_id);
    imgClicked.src = "image/icons/icon_openNavItem.gif";
  } else {
    alert('Div element not found: ' + divObject_id);
  }
}

function setOption(id, value) {
  document.getElementById(id).value = value;
}

function getOption(id) {
  var theElement = document.getElementById(id);
  if (theElement) {
    return theElement.value;
  }
  return "";
}

function setOptionIndex(optionId) {
  var elSelSrc = document.getElementById('sel' + optionId);
  var i;
  for (i = elSelSrc.options.length - 1; i >= 0; i--) {
    if (elSelSrc.options[i].selected) {
      document.getElementById('hid' + optionId).value = elSelSrc.options[i].value;
    }
  }
}

//Check password and save if all ok (password's length not zero, password contains at least non space symbol,
//password and confirm field values are same.
//Returns true, if verification passed and password chenges sent to form handler.
function saveUserPassword()
{
  var passNewElem = document.getElementById('passwordNew');
  var passConfirmElem = document.getElementById('passwordConfirm');
  var password = document.getElementById('password');

  if (!passNewElem || !passConfirmElem) {
    return false;
  }

  var passNew = passNewElem.value;
  var passConfirm = passConfirmElem.value;

  if ((passNew == "") && (passConfirm == ""))
    return true;

  //Password which contains only space(s) symbols - don't save.
  if (passNew.match(/^(\s)*$/))
  {
    var allSpacesErrorMessage = getResource("personalization.password.allspaceserror");
    dijit.byId('messageBar').addMessage( {type:"error", summary:allSpacesErrorMessage});
    return false;
  }

  //Password which contains no symbols or not confirmed with confirm value - don't save.
  if (passNew == '' || passConfirm == '' || passNew != passConfirm ) {

    var confirmErrorMessage = getResource("personalization.password.confirmerror");
    dijit.byId('messageBar').addMessage( {type:"error", summary:confirmErrorMessage });

    return false;
  }

  saveUserPasswordAction();
  return true;
}

function saveWindowPreferences() {
  var mTryLogout = document.getElementById("hidTrylogOut");

  if (mTryLogout) {
    window.confirmLogout = (mTryLogout.value == "true");
  }
  else {
    window.confirmLogout = false;
  }

}

function prefGeneralPanelOk() {
  restoreGeneralPanel();
  showPreferences();
}

function isOptionDisabled(value, message) {
  if (value == 'true') {
    alert(message);
    return false;
  } else {
    showPreferences();
    return true;
  }
}

function warnClient(message, disabled, disabledMessage) {
  var contributeTab = document.getElementById("contributeTab");
  if (contributeTab && contributeTab.className && contributeTab.className == "current")
  {
    var cancelCallback = function() {};
    var okCallback = function() {
      isOptionDisabled(disabled, disabledMessage);
    };
    dojoConfirmDialog('',message,okCallback,cancelCallback);
  } else {
    isOptionDisabled(disabled, disabledMessage);
  }
}


dojo.provide("atg.data.grid");

/*
 * Manages a virtual scrolling grid instance
 */
dojo.declare("atg.data.grid.VirtualGridInstance", null, {
  /*
   * Constructs a data model for the grid instance
   *
   * inFormId - the ID of the search form for the grid results
   * inUrl - the URL to get the JSON data
   * inRowsPerPage - number of rows to fetch per server request
   */
  constructor: function(params) {
    this.formId = params.formId;
    this.gridWidgetId = params.gridWidgetId;
    this.progressNodeId = params.progressNodeId;
    this.url = params.url;
    this.rowsPerPage = params.rowsPerPage;
    this.dataModel = params.dataModel;

    // Create data model and attach progress notification handlers
    if (this.dataModel) {
      this.dataModel.endProgress = dojo.hitch(this, "endProgressMessage");
      this.dataModel.startProgress = dojo.hitch(this, "startProgressMessage");
      if (params.pageBaseOffset) this.dataModel.pageBaseOffset = params.pageBaseOffset; // defaults to 0 if not set
      if (params.currentPageElementName) { this.dataModel.currentPageElementName = params.currentPageElementName;}
    }
    this.messages = params.messages;
    this.structure = params.structure;
    
    this.hitchGridMethods();
  },
  /*
   * Re-attaches methods in the grid model instance (which survives page changes) to the widget
   * (which is destroyed during page changes).
   */
  hitchGridInstanceToWidget: function() {
    var grid = this.getGridWidget();
    if (!grid) return;
    grid.setModel(this.dataModel);
    this.hitchGridMethods();
  },
  hitchGridMethods: function() {
    var grid = this.getGridWidget();
    if (!grid) return;
    grid.canSort = dojo.hitch(grid, this.canSort);
    grid.setSortIndex = dojo.hitch(grid, this.setSortIndex, this);
  },
  // sorting needs to be on the grid so we can look at the cell info
  canSort: function(inSortInfo){
    var c = this.getCell(this.getSortIndex(inSortInfo));
    return (c && c.defaultSort && 
       (c.defaultSort.indexOf("asc") == 0 || c.defaultSort.indexOf("desc") == 0)) ? true : false;
  },
  getSortIndex: function(){
    
  },
	setSortIndex: function(inGridInstance, inIndex, inAsc){
		// override base method to read from configuration - 
		// since not all the fields that we could sort on are visible, we
		// have to map the column header that was clicked on to a field in the data model.
		var si = inIndex + 1;
    var c = this.getCell(this.getSortIndex(si));
    if (c.originalFieldIndex === undefined) { // reset only first time
      c.originalFieldIndex = c.fieldIndex;
    }
    var sortField = "";
    if (c && c.defaultSort && c.sortField) { // maybe override the sort field
      sortField = c.sortField;
    }
    else if (c && c.defaultSort && c.field) {
      sortField = c.field;
    }
    if (sortField) {
      for (var i = 0 ; i < inGridInstance.dataModel.fields.values.length; i++) {
        if (inGridInstance.dataModel.fields.values[i] && inGridInstance.dataModel.fields.values[i].key && 
            inGridInstance.dataModel.fields.values[i].key == sortField) {
          console.debug("VIRTUAL GRID INSTANCE - resetting sortIndex from: ", inIndex, ", to: ", i);
          c.fieldIndex = i;
          break;
        }        
      }
    }
		if(inAsc != undefined){
			si *= (inAsc ? 1 : -1);
		} 
		else if (this.getSortIndex() == inIndex){
			si = -this.sortInfo;
		}
		else { // read configuration by default
      if (c && c.defaultSort && c.defaultSort.indexOf("desc") == 0) {
        si = si * -1;
      }
		}
		this.setSortInfo(si);
	},
  /*
   * Get reference to the data model
   */
  getDataModel: function() {
    return this.dataModel;
  },
  /*
   * Set the grid structure
   */
  setStructure: function(inStructure) {
    this.structure = inStructure;
  },
  setFields: function(inFields) {
    this.dataModel.fields.set(inFields);
  },
  /*
   * Set the current page index form input name
   */
  setCurrentPageElementName: function(inCurrentPageElementName) {
    if (this.dataModel) {this.dataModel.currentPageElementName = inCurrentPageElementName;}
  },
  /*
   * Set the page-base offset for the grid model:
   * 0 for zero-based paging, 1 for one-based paging, etc.
   */
  setPageBaseOffset: function(inPageBaseOffset) {
    if (this.dataModel) this.dataModel.pageBaseOffset = inPageBaseOffset;
  },
  /*
   * Set the sort direction form input name
   */
  setSortDirectionElementName: function(inSortDirectionElementName) {
    this.sortDirectionElementName = inSortDirectionElementName;
  },
  /*
   * Set the sort field form input name
   */
  setSortFieldElementName: function(inSortFieldElementName) {
    this.sortFieldElementName = inSortFieldElementName;
  },
  /*
   * Set the grid widget ID and set up sorting for the instance
   */
  setGridWidgetId: function(inGridWidgetId) {
    this.gridWidgetId = inGridWidgetId;
    this.hitchGridInstanceToWidget();
  },
  /*
   * Sets the array of sortable columns
   */
  setSortableColumns: function(inSortableColumns) {
    this.sortableColumns = inSortableColumns;
  },
  /*
   * Get reference to the grid widget
   */
  getGridWidget: function() {
    if (!this.gridWidgetId) return null;
    return dijit.byId(this.gridWidgetId);
  },
  /*
   * Set the progress node DOM ID
   */
  setProgressNodeId: function(inProgessNodeId) {
    this.progressNodeId = inProgessNodeId;
  },
  /*
   * Get reference to the progress node
   */
  getProgressNode: function() {
    if (!this.progressNodeId) return null;
    return dojo.byId(this.progressNodeId);
  },
  /*
   * Return the data for a cell from the model
   *
   * inProperty - name of the cell in the returned JSON object for the row
   * inRowIndex - index of the row
   */
  getCellData: function(inProperty, inRowIndex) {
    var row;
    var data = "";
    if (this.dataModel) {
      row = this.dataModel.getRow(inRowIndex);
      if (row) {
        data = row[inProperty];
      } 
    }
    return data;
  },
  /*
   * Return the data for a cell by URL
   *
   * inProperty - name of the cell in the grid structure for which to return data
   * inUrl - URL to request
   * inQueryParams - query parameters to add to the URL
   * inRowIndex - index of the row
   */
  getCellDataByGet: function(inProperty, inUrl, inQueryParams, inRowIndex) {
    var data = "";
    if (this.dataModel) {
      data = this.dataModel.getCellByGet(inProperty, inUrl, inQueryParams, inRowIndex);
    }
    this.executeScripts(data, inUrl, true); // eval script blocks in returned cell data
    return data;
  },
  /*
   * Return the data for a cell by form submission
   * Create the row detail by posting a form to get the details
   *
   * inProperty - name of the cell in the grid structure for which to return data
   * inFormId - identifier to look up DOM node of the form to submit
   * inFormData - map of functions or values to submit with form keyed by input,
   *   functions are invoked to obtain the value to submit
   * inSuccessUrl - success URL to render the form result
   * inRowIndex - index of the row
   */
  getCellDataByPost: function(inProperty, inFormId, inFormData, inSuccessUrl, inRowIndex) {
    var data = "...";
    if (!this.dataModel) return data;
    var formNode = dojo.byId(inFormId);
    for (var element in inFormData) {
      var value = inFormData[element];
      if (dojo.isFunction(value)) {
        value = value(inRowIndex);
      }
      formNode[element].value = value;
    }
    data = this.dataModel.getCellByPost(inProperty, formNode, inSuccessUrl, inRowIndex)
    this.executeScripts(data, inSuccessUrl, true); // eval script blocks in returned cell data
    return data;
  },
  /*
   * Create a link for a row based on the passed in template and replacement parameters.
   *   For example, the view link and select link can both be created via this function.
   *   Multiple replacement parameters can be passed in the inReplacementParams argument.
   *   Replacement parameters are keyed by the pattern to replace in the template string.
   *   The template string is processed for each replacement parameter in sequence.
   *   The replacement parameter value can be a function or a value - if a function,
   *   the function will be invoked with the row index to obtain a value for replacement.
   *
   * inTemplate - a URL with patterns to replace that correspond to keys in the inReplacementParams
   * inReplacementParams - a map keyed to patterns in the inTemplate which are replaced by the corresponding values
   * inRowIndex - index of the row
   */
  createLink: function(inTemplate, inReplacementParams, inRowIndex) {
    var link = inTemplate;
    for (var pattern in inReplacementParams) {
      var value = inReplacementParams[pattern];
      if (dojo.isFunction(value)) {
        value = value(inRowIndex);
      }

      pattern = new RegExp(pattern,"gi"); // global replace
      link = link.replace(pattern, value);
    }
    return link;
  },
  /*
   * Creates html for the cell that toggles the details
   *
   * inImagePath - web path to the image directory
   * inImages - map containing image file names keyed to the following keys: "open" and "closed"
   * inGridInstance - global variable name for the grid instance (due to event handler)
   * inRowIndex - index of the row
   */
  createToggler: function(inImagePath, inImages, inGridInstance, inRowIndex) {
    var image = this.isShowRowDetails(inRowIndex) ? inImages["open"] : inImages["closed"];
    var show = this.isShowRowDetails(inRowIndex) ? 'false' : 'true';
    return '<img src="' + inImagePath + image + '" onclick="' + inGridInstance + '.toggleDetail(' + inRowIndex + ', ' + show + ')">';
  },
  /*
   * Creates html for the cell that toggles the details
   *
   * inImagePath - web path to the image directory
   * inImages - map containing image file names keyed to the following keys: "open" and "closed"
   * inGridInstance - global variable name for the grid instance (due to event handler)
   * inRowIndex - index of the row
   */
   createHoverToggler: function(inImagePath, inImages, inGridInstance, inProperty, inFormId, inFormData, inSuccessUrl, inRowIndex){
     var image = this.isShowRowDetails(inRowIndex) ? inImages["open"] : inImages["closed"];
     var show = this.isShowRowDetails(inRowIndex) ? 'false' : 'true';
     var params = "";
     for (var element in inFormData) {
       var value = inFormData[element];
       if (dojo.isFunction(value)) {
         value = value(inRowIndex);
       }
       params += element + ":\'" + value + "\'";
     }
     return '<img width="16" height="14" id="gridCell' + inGridInstance + inRowIndex + '" src="' + inImagePath + image + '" onmouseover="' + inGridInstance + '.toggleHoverDetail(\'' + inGridInstance + inRowIndex  + '\', ' + inRowIndex + ', ' + show + ',\'' + inProperty + '\',\'' + inFormId + '\',{' + params + '} , \'' + inSuccessUrl + '\');" onMouseOut="' + inGridInstance + '.toggleHoverDetail(\'' + inGridInstance + inRowIndex  + '\');">';
   },

   /*
    * Loads the row details into a hovering content pane
    *
    * cellId - id of DOM node need to be hovered to show tooltip
    * inRowIndex - index of the row
    * inShow - flag indicating whether details should be shown or hidden
    * inProperty - name of the cell in the grid structure for which to return data
    * inFormId - identifier to look up DOM node of the form to submit
    * inFormData - map of functions or values to submit with form keyed by input,
    *   functions are invoked to obtain the value to submit
    * inSuccessUrl - success URL to render the form result
   */
   toggleHoverDetail: function(cellId, inRowIndex, inShow, inProperty, inFormId, inFormData, inSuccessUrl) {
     if(arguments.length > 1){
       console.debug("toggleHoverDetail:" + inFormData);
       var data = this.getCellDataByPost(inProperty, inFormId, inFormData, inSuccessUrl, inRowIndex);
       dijit.showTooltip(  data   , dojo.byId("gridCell" + cellId));
     }else{
       dijit.hideTooltip(dojo.byId("gridCell" + cellId));
     }
   },
  /*
   * Changes the toggle detail state
   *
   * inRowIndex - index of the row
   * inShow - flag indicating whether details should be shown or hidden
   */
  toggleDetail: function(inRowIndex, inShow) {
    var grid = this.getGridWidget();
    if (!grid) return;
    this.setShowRowDetails(inRowIndex, inShow);
    grid.updateRow(inRowIndex);
  },
  /*
   * Runs the script blocks in the passed in data when current thread finishes
   *
   * inData - html data that may have script blocks to evaluate
   * inUrl - URL from which the html data was downloaded (required for error reporting purposes only)
   * inWaitForCurrentScriptBlock - controls whether the script blocks are eval'd immediately
   *   or when the current block is finished. If the the scripts depend on HTML that is
   *   being rendered by the currently running script block, wait should be 'true', so
   *   that the required DOM is in existence for the script. If the currently running script
   *   block depends on script blocks in the HTML, wait should be 'false' (but doing this as
   *   a matter of design is not recommended).
   */
  executeScripts: function(inData, inUrl, inWaitForCurrentScriptBlock) {
    var element = document.createElement("div");
    element.innerHTML = inData;
    var scripts = element.getElementsByTagName("script");
    for (var i = 0; i < scripts.length; i++) {
      try {
        inWaitForCurrentScriptBlock ? setTimeout(scripts[i].innerHTML, 1) : eval(scripts[i].innerHTML);
      }
      catch (e) {
        console.debug("Error ", e.message, " running eval() on script in ", inUrl, ": ", e);
      }
    }
  },
  /*
   * Returns the visibility of specified row detail
   *
   * inRowIndex - index of the row
   */
  isShowRowDetails: function(inRowIndex) {
    var isVisible = false;
    if (this.dataModel) {
      isVisible = this.dataModel.isShowDetails[inRowIndex];
    }
    return isVisible;
  },
  /*
   * Sets the visibility of the details for a row specified by index:
   *
   * inRowIndex - index of the row
   * inVisibility - sets the visibility of the row details, true for visible
   */
  setShowRowDetails: function(inRowIndex, inVisibility) {
    this.dataModel.isShowDetails[inRowIndex] = inVisibility;
  },
  /*
   * Callback for data model to update search progress
   *   when a search is starting
   */
  startProgressMessage: function() {
    var node = this.getProgressNode();
    if (!node) return;
    node.innerHTML = this.messages.inProgress;
  },
  /*
   * Set progress and status messages
   */
  setMessages: function(inMessages) {
    this.messages = inMessages;
  },
  /*
   * Callback for data model to update search progress
   *   when a search is ending
   */
  endProgressMessage: function() {
    var node = this.getProgressNode();
    if (!node) return; // progress node optional - not necessarily an error
    if (this.dataModel.lastServerError != null) {
      node.innerHTML = this.dataModel.lastServerError.message;
    }
    else if (this.dataModel.count === 0 || this.dataModel.count == -1) {
      node.innerHTML = this.messages.noResultsFound;
    }
    else {
      var matchingStr = this.messages.resultsFound;
      node.innerHTML = matchingStr.replace(/\{0\}/g, this.dataModel.count);
    }
  },
  /*
   * Sets the details in a row to visible or hidden by setting a visibility flag
   *   on the detail sub row (e.g. the sub row with index=1)
   *
   * inRowIndex - index of the row
   * inSubRows - array of the rows in the grid structure, a grid with details will usually
   *   have 2 sub rows: one for the main row and another below it for the details
   */
  handleDetailVisibility: function(inRowIndex, inSubRows) {
    if (inSubRows && inSubRows[1]) {
      inSubRows[1].hidden = !this.dataModel.isShowDetails[inRowIndex];
    }  
  },
  /*
   * Initializes structure (if needed) and renders contents
   */
  render: function() {
    // provide html for the Detail cell in the master grid    
    // Setup main grid structure 
    var grid = this.getGridWidget();
    if (!grid) {
      console.debug("Cannot find grid widget for VirtualGridInstance");
      return;
    }
    if (!this.structure) {
      console.debug("No structure specified for VirtualGridInstance", this.gridWidgetId);
      return;
    }
    if (!grid.structure) { // Will occur when coming from another page due to re-creation of widget on page
      console.debug("grid didn't have a structure, setting one")
      for (var i = 0; i < this.structure.length; i++) {
        if (!this.structure[i].onBeforeRow) {
          // initially hide detail sub-row
          this.structure[i].onBeforeRow = dojo.hitch(this, "handleDetailVisibility");
        }
      }
      grid.setStructure(this.structure);
      grid.setModel(this.dataModel);
    }
    // occurs on submit, when widget already exists but there is new data
    else if (!atg.service.form.isFormEmpty(this.dataModel.formId)) {
      console.debug("refreshing the grid");
      grid.refresh();
      grid.update();
    }

  }
});

dojo.require("dojox.grid._grid.cell");
dojo.extend(dojox.grid.cell, {
  get: function(inRowIndex) { 
		return this.grid.model.getDatum(inRowIndex, this.originalFieldIndex == undefined ? this.fieldIndex : this.originalFieldIndex);
  }
});
dojo.provide("atg.data");

/*
 * Client-side model for dojo VirtualGrid with SearchFormHandler
 *   SearchFormHandler returns JSON object with following API:
 * 
 *   - resultLength: total number of results
 *   - currentPage: 1-based index for current page
 *   - results: array of results with format corresponding to the grid structure.
 * 
 * The model supports virtual paging which is managed via
 *   the dojo scroller widget (a part of VirtualGrid) and 
 *   results in callbacks for the rendering of cell data
 *   as new rows become visible through the user's scrolling.
 *   When cell data is requested, the rows cache is checked first,
 *   if the row is not found a send is submitted for the row.
 */
dojo.declare("atg.data.VirtualGridData", dojox.grid.data.Dynamic, { 
  constructor: function(inFormId, inUrl, inRowsPerPage) {
    this.formId = inFormId;
    this.server = inUrl;
    this.rowsPerPage = inRowsPerPage;

    this.lastServerError = null;
    this.currentPage = 1; // paging is one-based due to SearchFormHandler
    this.isShowDetails = []; // array of true-false flags for whether details are visible
    this.isCheckRowCount = false;
    
    this.sortField = '';
    this.sortIndex = 0;
    this.sortPropertyElementName = "sortProperty";
    this.sortDirectionElementName = "sortDirection";

    // callbacks for progress message
    this.startProgress = null;
    this.endProgress = null;

    // form input for paging
    this.currentPageElementName = "currentPage";
    this.pageBaseOffset = 0; // 0 for 0-based paging, 1 for 1-based paging, etc.
  },
  /*
   * Clears the results ONLY without bouncing the grid structure
   */
  clearData: function() {
    this.currentPage = this.pageBaseOffset;
    this.lastServerError = null;
    this.count = 0;
    this.isShowDetails = []
    this.isCheckRowCount = false;
    this.cache = [ ];
    this.inherited(arguments);
  },
  /*
   * Performs an HTTP GET to retrieve row details then caches the returned
   *   value in the result cache.
   * 
   * inAsync - flag to indicate whether the call is asynchronous or blocking
   * inUrl - URL to HTTP GET
   * inParams - query parameters appended to the URL
   * inProperty - the property in the result object in which to put the returned value
   * inRowIndex - the index of the row that is being fetched
   * inCount - the number of rows to retrieve
   */
  doGet: function(inAsync, inUrl, inParams, inProperty, inRowIndex, inCount) {
	  if (inUrl === null && inUrl === "") {
      console.debug("VirtualGridData unable to GET: null or empty URL");
	    return null; // no deferred object
	  }
    console.debug("VirtualGridData sending GET: " + this.formId);
    var deferred = 
		dojo.xhrGet({
      url: inUrl,
      mimetype: "text/html",
      encoding: "utf-8",
      timeout: atgXhrTimeout,
      handleAs: "text",
      content: inParams,
      sync: !inAsync
    });
    deferred.addCallbacks(dojo.hitch(this, "receiveGet", inProperty, inRowIndex, inCount),
                          dojo.hitch(this, "receiveError", null /* no extra callbacks */));
    return this.checkMessages(deferred);
  },
  /*
   * Submits a form to retrieve a single page of JSON-encoded results.
   *   The form submitted, the successURL to render the results and the number of results 
   *   to render per page are set in the constructor.
   * 
   * inAsync - flag to indicate whether the call is asynchronous or blocking
   * inRowIndex - the index of the row that is being fetched
   * inCount - the number of rows to retrieve
   */
	doJson: function(inAsync, inRowIndex, inCount) {
	  if (this.formId === null && this.formId === "") {
      console.debug("VirtualGridData unable to retrieve JSON: null or empty form ID");
	    return null; // no deferred object
	  }
    console.debug("VirtualGridData requesting data using: " + this.formId);
	  this.startProgressIndicator();
    var contentMap = {"atg.formHandlerUseForwards": true,
                      "_windowid": window.windowId};
    if (window.requestid) { contentMap["_requestid"] = window.requestid; }
    var deferred =
    dojo.xhrPost({
      content: contentMap,
      form: dojo.byId(this.formId),
      url: this.server,
      encoding: "utf-8",
      timeout: atgXhrTimeout,
      handleAs: "json",
      sync: !inAsync
    });
    deferred.addCallbacks(dojo.hitch(this, "receiveJson", inRowIndex, inCount),
                          dojo.hitch(this, "receiveError"));
    return this.checkMessages(deferred);
	},
  /*
   * Performs an HTTP POST using a form on the grid page to retrieve row details 
   *   then caches the returned value in the result cache.
   * 
   * inAsync - flag to indicate whether the call is asynchronous or blocking
   * inForm - the DOM form node from which to post data to the server
   * inUrl - the success URL to render the form handler results
   * inProperty - the property in the result object in which to put the returned value
   * inRowIndex - the index of the row that is being fetched
   * inCount - the number of rows to retrieve
   */
  doPost: function(inAsync, inForm, inUrl, inProperty, inRowIndex, inCount) {
	  if (inForm === null && inForm === "") {
      console.debug("VirtualGridData unable to POST: null or empty form");
	    return null; // no deferred object
	  }
    console.debug("VirtualGridData sending POST: " + inForm.formId);
    var contentMap = {"atg.formHandlerUseForwards": true,
                      "_windowid": window.windowId};
    if (window.requestid) { contentMap["_requestid"] = window.requestid; }
    var deferred =
    dojo.xhrPost({
      content: contentMap,
      form: inForm,
      url: inUrl,
      encoding: "utf-8",
      timeout: atgXhrTimeout,
      handleAs: "text",
      sync: !inAsync
    });
    deferred.addCallbacks(dojo.hitch(this, "receivePost", inProperty, inRowIndex, inCount),
                          dojo.hitch(this, "receiveError"));
    return this.checkMessages(deferred);
  },
  getRowCount: function() {
    if (this.isCheckRowCount === false) {
      this.fetchRowCount(); // gets the count from the server synchronously
      this.isCheckRowCount = true;
    }
    return this.count;
  },
  /*
   * Returns the total number of results available by making a server
   *   request for the first page of data. The server paging model must be 
   *   capable of supplying a number for the row count even when only a subset 
   *   of the results are actually returned.
   */
  fetchRowCount: function() {
    console.debug("in fetchRowCount");
    dojo.byId(this.formId)[this.currentPageElementName].value = this.pageBaseOffset;
    this.doJson(false, 0);
    this.pages[0] = true;
    return this.count;
  },
  requestRows: function(inRowIndex, inCount)  {
  	console.debug("in requestRows this.isCheckRowCount " + this.isCheckRowCount, this.count);
  	if (this.isCheckRowCount === true && this.count == 0)
  		return;

  	console.debug("in requestRows for row " + inRowIndex);
  	// are first and last indices cached? 
  	// if yes, this page is already in the cache - skip request
    if (this.getRow(inRowIndex + i) === undefined && 
    		this.getRow(inRowIndex + inCount - 1) === undefined) {
	    var currentPage = Math.floor(inRowIndex / this.rowsPerPage) + this.pageBaseOffset;
	    dojo.byId(this.formId)[this.currentPageElementName].value = currentPage;
	    console.debug("set current page to " + currentPage);
	    this.doJson(true, inRowIndex, inCount);
    }
    else {
    	this.rowsProvided(inRowIndex, inCount);
    }	
  },
  /*
   * Returns data for cell via HTTP GET
   * 
   * inProperty - row property into which to insert returned data
   * inUrl - URL to GET
   * inParams - query params for the URL
   * inRowIndex - index of the row in the grid
   */
  getCellByGet: function(inProperty, inUrl, inParams, inRowIndex) {
    var row = this.getRow(inRowIndex);
    if(row && !row[inProperty]) {
      this.doGet(false, inUrl, inParams, inProperty, inRowIndex, 1);
      row = this.getRow(inRowIndex);
    }
    return (row && row[inProperty]) ? row[inProperty] : "...";
  },
  /*
   * Returns data for cell via HTTP POST
   * 
   * inProperty - row property into which to insert returned data
   * inUrl - form action
   * inParams - forms to post
   * inRowIndex - index of the row in the grid
   */
  getCellByPost: function(inProperty, inForm, inUrl, inRowIndex) {
    var row = this.getRow(inRowIndex);
    if(row && !row[inProperty]) {
      this.doPost(false, inForm, inUrl, inProperty, inRowIndex, 1);
      row = this.getRow(inRowIndex);
    }
    return (row && row[inProperty]) ? row[inProperty] : "...";
  },
  /*
   * Method to receive data from a GET call, it is not recommended
   *   to call this method directly
   */
  receiveGet: function(inProperty, inRowIndex, inCount, inData) {
    var row = this.getRow(inRowIndex);
    row[inProperty] = inData;
    this.setRow(row, inRowIndex);
    this.rowsProvided(inRowIndex, inCount);
    return inData;
  },
  /*
   * Method to receive data from a POST call, it is not recommended
   *   to call this method directly
   */
  receivePost: function(inProperty, inRowIndex, inCount, inData) {
    var row = this.getRow(inRowIndex);
    row[inProperty] = inData;
    this.setRow(row, inRowIndex);
    this.rowsProvided(inRowIndex, inCount);
    return inData;
  },
  /*
   * Method to receive JSON data
   */
  receiveJson: function(inRowIndex, inCount, inData) {
    this.currentPage = (inData.currentPage || typeof inData.currentPage == "number") ? inData.currentPage : 1; // paging on server is one-based
    if (inData.results && inData.results.length) {
      this._setupFields(inData.results[0]);
      if (this.isCheckRowCount === false) {
        this.setRowCount(inData.resultLength ? inData.resultLength : 0);
        this.isCheckRowCount = true;
      }
      // grid rows on client are zero-based
      for (var i = 0, length = inData.results.length; i < length; i++) {
        this.setRow(inData.results[i], inRowIndex + i);
      }
    }
    this.rowsProvided(inRowIndex, inCount);
	  this.endProgressIndicator();
		return inData;
  },
  /*
   * Method to handle a server error
   */
	receiveError: function(inErr) {
	  this.lastServerError = inErr;
	  this.endProgressIndicator();
		return inErr;
	},
  _setupFields: function(dataItem){
    // abort if we already have setup fields
    if(this.fields._setup){
      return;
    }
    //console.debug("setting up fields", m);
    var fields = [];
    for(var fieldName in dataItem){
      var newField = { key: fieldName };
      fields.push(newField);
    }
    console.debug("new fields:", fields);
    this.fields.set(fields);
    this.fields._setup = true;
    this.notify("FieldsChange");
  },
	/* 
	 * Gets the data for a given cell 
	 */
  getDatum: function(inRowIndex, inFieldIndex) {
    var row = this.getRow(inRowIndex);
    if (row) {
      var field = this.fields.get(inFieldIndex);
      //console.debug("in getDatum for row " + inRowIndex + " field index " + inFieldIndex + " field " + field.key);
      var propertyName = field.key;
      var row = this.getRow(inRowIndex);
      return row[propertyName]; 
    }
    else {
      return this.fields.get(inFieldIndex).na;
    }
  },
  sort: function(inSortIndex) {
    this.sortField = this.fields.get(Math.abs(inSortIndex) - 1).name;
    this.sortDesc = (inSortIndex < 0);
    if (this.formId) {
      var form = dojo.byId(this.formId);
      if (form) {
        if (form[this.sortPropertyElementName]) {
          form[this.sortPropertyElementName].value = this.fields.get(Math.abs(inSortIndex) - 1).key;
          form[this.sortDirectionElementName].value = (inSortIndex < 0) ? "desc" : "asc";
        }
      }
    }
    this.clearData();
  },
  setFormSortProperty: function (/*int*/columnIndex, /*bool*/sortDesc)  {
    var theForm = dojo.byId(this.formId);
    if (columnIndex != -1) {
      theForm[this.sortPropertyElementName].value = this.fields.get(columnIndex).key;
      theForm[this.sortDirectionElementName].value = sortDesc ? "desc" : "asc";
    }
    else
    {
      theForm[this.sortPropertyElementName].value = "id";
      theForm[this.sortDirectionElementName].value = "asc";
    }
  },
  /*
   * Starts the in-progress message
   */
	startProgressIndicator: function() {
    if (dojo.isFunction(this.startProgress)) {
      this.startProgress();
    }	  
	},
	/*
	 * Ends the in-progress message and displays the number of results returned 
	 *   or the message for no results.
	 */
	endProgressIndicator: function() {
	  if (dojo.isFunction(this.endProgress)) {
	    this.endProgress();
	  }
	},
	/*
	 * Calls the message bar
	 */
	checkMessages: function(inDeferred) {
    var mb = dijit.byId("messageBar");
    if (mb) {
      inDeferred.addCallback(function() { dijit.byId("messageBar").retrieveMessages();});
    }
    return inDeferred;
	}
});
