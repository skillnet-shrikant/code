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
