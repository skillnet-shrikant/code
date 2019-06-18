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
