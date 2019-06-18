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
