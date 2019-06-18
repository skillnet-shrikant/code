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
