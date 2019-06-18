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
