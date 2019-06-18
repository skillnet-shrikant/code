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
