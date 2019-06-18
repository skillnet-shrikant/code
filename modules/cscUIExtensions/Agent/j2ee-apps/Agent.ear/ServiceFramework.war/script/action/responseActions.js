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
}