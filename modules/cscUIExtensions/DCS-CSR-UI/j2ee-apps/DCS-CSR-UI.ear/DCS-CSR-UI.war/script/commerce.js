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
