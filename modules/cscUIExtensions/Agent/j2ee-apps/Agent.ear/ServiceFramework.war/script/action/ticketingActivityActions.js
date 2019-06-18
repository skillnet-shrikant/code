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
