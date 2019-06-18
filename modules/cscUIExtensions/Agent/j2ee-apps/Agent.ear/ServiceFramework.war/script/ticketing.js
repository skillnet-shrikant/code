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
