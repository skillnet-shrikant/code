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

//*************************************************************************
//
// response.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// Defines client-side response behavior
//
//*************************************************************************
var g_abandonedSession;
var g_textBefore;

//-------------------------------------------------------
// Functions required for Response Panel Functionality
//-------------------------------------------------------

//-------------------------------------------------------
//  INITIALISATION FUNCTIONS
//-------------------------------------------------------
/**
 * Initialise the response panel. This method should be called by PPR once the response panel has been loaded.
 * This method then performs any additional setup required, including initialising the rich editor and setting
 * the initial state of the editable adresses.
 */
function ResponseInitPanel()
{

  //subscribe to the ticket change topic
  dojo.subscribe("/agent/ticketChange", function(){ResponseSaveStateOnUnload()});

    // Get initial body content for editor, and set on hidden form field (this will be updated
  // on each subsequent editor blur event, but need to set it now in case we never modify and blur the editor)

  var msgBodyContent = unescapeXml(dojo.byId("responseTempHTMLContent_div").innerHTML);

  //***********************************************************************
  // Create new editor object
  //
  var respondEditor = new FCKeditor("RespondEditor");
  respondEditor.BasePath = window.fckEditorBasePath;
  respondEditor.Config["CustomConfigurationsPath"] = window.contextPath + '/include/response/editor/editorConfig.js?' + ( new Date() * 1 );
  respondEditor.ToolbarSet = "Respond";
  respondEditor.Height = 300;
  respondEditor.Value = msgBodyContent;
  dojo.byId("richTextEditorContent").innerHTML = respondEditor.CreateHtml();

  //dojo.subscribe("/agent/panelUnloaded", function(){ResponseSaveStateOnUnload();});
  dijit.byId("contentColumn").onUnloadDeferred.addCallback(function(){ResponseSaveStateOnUnload();});
  g_abandonedSession = false;

  //subscribe to newCallEvent's as part of a fix to bug 149744
  dojo.subscribe("startNewCallEvent", ResponseSaveStateOnUnload);
  dojo.subscribe("endAndStartNewCallEvent", ResponseSaveStateOnUnload);
  
  //disable the spellchecker if language isn't supported
  if(!languageSupported)
  {
	  setTimeout(function () 
	    {
		  FCKeditorAPI.GetInstance("RespondEditor").EditorWindow.parent.FCKToolbarItems.LoadedItems["Spell"].Disable();
		}, 2000);
  }
}

function ResponseAbandonedSession()
{
  g_abandonedSession = true;
}

//-------------------------------------------------------
//  EVENT HANDLING FUNCTIONS
//-------------------------------------------------------

function ResponseUpdateAllFormFields()
{
  // Update any hidden form fields with current state.
  var theElement = document.getElementById("outMessageForm");
  if (theElement != null) {
    theElement.htmlBody.value = ResponseGetStrippedValue();
    theElement.textBody.value = ResponseGetStrippedValue().replace( /<[^<|>]+?>/gi,'' );
  }
}

/**
 * Save the current state of the Response panel. This should invoke an action to submit the message form
 * and expects no response to be returned.
 * This should be called by the onblur event of each form field on the
 * compose panel enabling the user to move between tabs without losing any state information that has
 * been created during the composition of an outbound message.
 *
 * NOTE: The eventSource parameter is not used at present, but may be implemented in future to enable a 'delta'
 * submit of the form instead of submitting the entire form each time the user moves off of any element.
 *
 * @param eventSource the DOM element from which the onblur event originated
 */
function ResponseSaveState(eventSource)
{
  if (!g_abandonedSession)
  {
    ResponseUpdateAllFormFields();
    saveEmail();
  }
  g_abandonedSession = false;

}


function ResponseSaveStateOnUnload()
{
  if (!g_abandonedSession)
  {
    ResponseUpdateAllFormFields();
    ResponseSaveState();
  }
  g_abandonedSession = false;
}

/**
 * Insert email template from container div into the body of the rich editor field
 * @param containerId the DOM id of the container whose content should be inserted
 */
function ResponseInsertTemplate(containerId)
{
  //ResponseReplaceEditorContent(containerId);
  ResponseInsertAtCaret(containerId);
}

/**
 * Replace the body of the rich editor component with new content
 * @param containerId the DOM id of the container whose content should be inserted
 */
function ResponseReplaceEditorContent(containerId)
{
  var element = document.getElementById(containerId);
  if (element && element != null)
  {
    var content = unescapeXml(element.innerHTML);
    FCKeditorAPI.GetInstance('RespondEditor').SetHTML(content);
  }
}

/**
 * Insert content into the rich editor at the caret position, overwriting any selected text.
 * The content to be inserted is takend from the container div passed in
 * c:out tag
 * @param containerId the DOM id of the container whose content should be inserted
 */
function ResponseInsertAtCaret(containerId)
{
  var element = document.getElementById(containerId);
  if (dojo.isIE < 8 && dojo.isIE != 0){
    //this condition was included to fix bug KNLDG-168108
    // IE7 generates error when DOM method "insertBefore" invokes with parameter which wasn't created at the same document as element which invoks method
    // e.g. A. insertBefore(B) - A and B should be elements of one document is it is not IE7 generates error
    //unfortunaly FCKEditor doesn't check this condition which causes error because FCKEditor is loaded as iframe
    var fckEditorDocument = document.getElementById("RespondEditor___Frame").contentWindow.document;
    var fckEdinorContent = fckEditorDocument.getElementsByTagName("iframe")[0].contentWindow.document;
    if (element.ownerDocument != fckEdinorContent){
      var tmp = fckEdinorContent.createElement(element.tagName);
      tmp.innerHTML = element.innerHTML;
      element = tmp;
    }
  }
  FCKeditorAPI.GetInstance('RespondEditor').InsertElement(element);
}

function ResponseModifySolutionLinks(id, prefix)
{
  var solutionText    = document.getElementById(id);
  var solutionLinks   = solutionText.getElementsByTagName("a");
  for (var x = 0; x < solutionLinks.length; x++) {
    if (solutionLinks[x].name == "solutionLink") {
      if (prefix.indexOf(".jsp") == -1) {
        if (prefix.charAt(prefix.length - 1) != "/")
          prefix = prefix + "/";

        solutionLinks[x].href = prefix + "main.jsp?t=solutionTab&solutionId=" + solutionLinks[x].id;
      }
      else {
        solutionLinks[x].href = prefix + "?t=solutionTab&solutionId=" + solutionLinks[x].id;
      }
    }
  }
  return solutionText.innerHTML;
}

/**
 * Check that a value has been set before trying to insert a template.  If it
 * has then go ahead and get it.
 * @param containerId the DOM id of the drop down containing the templates
 */
function ResponseGetTemplateAndInsert(containerId)
{
  var element = document.getElementById(containerId);
  if (element && element != null)
  {
    if (element.selectedIndex != 0)
    {
      // Invoke the action
      insertTemplate();
    }
  }
}


/**
 * Toggle a DOM element between display style 'none' or 'block'
 * @param id the DOM id of the element to toggle
 */
function ResponseToggleDisplay(id)
{
  ResponseToggleDisplayTo(id,"table-row-group");
}

/**
 * Toggle a DOM element between display style 'none' and a specified display style
 * @param id the DOM id of the element to toggle
 * @param displayStyle the style to set to when item is being displayed
 */
function ResponseToggleDisplayTo(id, displayStyle)
{
  var element = document.getElementById(id);

  if (element && element != null)
  {
    if (element.style.display == displayStyle)
    {
      element.style.display = "none";
    }
    else
    {
      element.style.display = displayStyle;
    }
  }
}

/**
 * Set the addresses area into a readonly or editable state.
 * @param state true for editable addresses, false for readonly addresses
 */
function ResponseSetAddressesEditable(state)
{
  var editableAddresses = document.getElementById(editableAddresses_div);
  var readonlyAddresses = document.getElementById(readOnlyAddresses_div);

  if (editableAddresses && editableAddresses != null && readonlyAddresses && readonlyAddresses != null)
  {
    if (state)
    {
      editableAddresses.style.display = "table-row-group";
      readonlyAddresses.style.display = "none";
    }
    else
    {
      editableAddresses.style.display = "none";
      readonlyAddresses.style.display = "table-row-group";
    }
  }
}

/**
 * Toggle the state of the addresses area between editable and readonly
 */
function ResponseToggleAddressesEditable()
{
  ResponseToggleDisplay('editableAddresses_div');
  ResponseToggleDisplay('readOnlyAddresses_div');
}

/**
 * Handle the find more solutions/documents link
 */
function ResponseFindMoreSolutions()
{
  // Set up the search
  //__ppr_findMoreSolutions.synchronizeTransaction = true;
  //__ppr_findMoreSolutions.transact();
  findMoreSolutions();

  // Switch to the find tab
  //__ppr_researchTab.transact();
  researchTab();
}

//-------------------------------------------------------
//  UTILITY FUNCTIONS
//-------------------------------------------------------
/**
 * Call the caf:validation function to validate the page. This will insert any client side errors into the page.
 * Should return true if all is OK, in which case the action should execute. Will return false if validation
 * fails, thus preventing the action from continuing.
 */
function ResponseIsMessageValid()
{
  var validationResult = validateResponseEmailPage();
  return validationResult;
}

/**
 * Unescape data that has been XML escaped by a c:out tag
 * This unescapes all characters that are defined in the JSTL1.1 spec to be escaped (Section 4.2)
 * @param str The string to unescape
 * @return The unescaped string
 */
function unescapeXml(str){
  return str.replace(/&gt;/gm, ">")
             .replace(/&lt;/gm, "<")
             .replace(/&amp;/gm, "&")
             .replace(/&apos;/gm, "'")
             .replace(/&#039;/gm, "'")
             .replace(/&quot;/gm, "\"")
             .replace(/&#034;/gm, "\"");
}

/**
 * Add an Agent Specified Attachment to the Response Message.
 */
function ResponseAddAgentAttachment()
{
  var windowId = document.getElementById("uploadWindowId");
  windowId.value = window.windowId;

  var element = document.getElementById("atg_arm_uploadAttachment");
  if (element && element != null)
  {
    // Only submit the form if a file has been chosen
    var fileElement = document.getElementById("uploadedAttachment");
    if (fileElement != null && fileElement.value != "")
    {


      var theForm = dojo.byId("agentAttachmentForm");

      theForm.encoding="multipart/form-data";

      element.click();

      showCurtain("mainCurtain");
      divSetVisible("uploadingPrompt_div");
    }
  }
}

/**
 * Refresh the attachment panel on the Outbound Message after
 * an Agent Specified Attachment has been added.
 */
function ResponseCompleteAttachmentRefresh()
{
  divSetHide("uploadingPrompt_div");
  hideCurtain("mainCurtain");

  // Clear the filename and description text fields
  var formElement = document.getElementById("agentAttachmentForm");
  if (formElement != null)
  {
    formElement.reset();
  }

  //__ppr_attachmentRefresh.transact();
  attachmentRefresh();
}

/**
 * Display any attachment errors.
 */
function ResponseAttachmentErrorsRefresh(validationErrors)
{
  divSetHide("uploadingPrompt_div");
  hideCurtain("mainCurtain");

  var errorsDiv = dojo.byId("responseErrorMessages");

  errorsDiv.innerHTML = validationErrors;

  ResponseCompleteAttachmentRefresh();
}

/**
 * Replace the value of the attachment id stored in the hidden field when
 * the user changes it.
 * @param attachmentId The id of the system attachment to add, or a comma seperated list of attachment IDs
 * if there is more than one to add.
 */
function ResponseAddSystemAttachment(attachmentId)
{
  var element = document.getElementById("addSystemAttachmentForm").attachmentId;

  if (element && element != null)
  {
    element.value = attachmentId;
    //__ppr_addSystemAttachment.transact();
    addSystemAttachment();
  }
}

/**
 * Removes the specified attachment from the outbound message
 * @param   attachmentId  The id of the system attachment to remove
 */
function ResponseRemoveAttachment(attachmentId)
{
  var element = document.getElementById("removeAttachmentForm").attachmentId;

  ResponseToggleAddAttachmentButtonsFor(attachmentId);

  if (element && element != null)
  {
    element.value = attachmentId;
    //__ppr_removeAttachment.transact();
    removeAttachment();
  }
}

/**
 * Adds an attachment from the content browser to the outbound message.
 * @param   attachmentId  The id of the system attachment to add.
 */
function ResponseAddAttachment(attachmentId)
{
  var element = document.getElementById("addSystemAttachmentForm").attachmentId;

  // Toggle the attach buttons
  ResponseToggleAddAttachmentButtonsFor(attachmentId);

  if (element && element != null)
  {
    element.value = attachmentId;

    // Attach the attachment
    //__ppr_addSystemAttachment.transact();
    addSystemAttachment();
  }
}

/**
 * Given an attachment id this function will toggle between the 'attach' buttons
 * and the 'already attached' message in the details area of the content browser
 * @param   attachmentId  The id of the attachment.
 */
function ResponseToggleAddAttachmentButtonsFor(attachmentId)
{
  // Toggle the attach buttons
  var buttonsId = "contentBrowserAttachment"+attachmentId;
  ResponseToggleDisplay(buttonsId);


  // Toggle the 'already attached' message
  var messageId = "contentBrowserAttachmentAlreadyAttached"+attachmentId;
  ResponseToggleDisplay(messageId);
}

/**
 * Calls the insertSolution action to insert the specified solution
 * in a response.
 * @param solutionId  The id of the solution to insert.
 */
function ResponseInsertSolution(solution)
{
  var element = document.getElementById("insertSolutionForm").solutionId;

  if (element && element != null)
  {
    element.value = solution;
    //__ppr_insertSolution.transact();
    insertSolution();
  }
}

/**
 * Inserts a link into the response allowing the specified recommeded answer
 * to be viewed in SelfService
 * @param url  The url to insert
 */
function ResponseInsertLink(url)
{
  if (url == null || url == "")
  {
    dijit.byId('messageBar').addMessage({type:'error', summary:getResource('response.error.no.site.selected')});
  }
  FCKeditorAPI.GetInstance('RespondEditor').InsertHtml(unescapeXml("<a href='" + url + "' >" + url + "</a>"));
}

/**
 * Strips the path off the agent attachment file name and adds
 * the remaining filename to the Display Text element.
 */
function ReponseSetAgentAttachmentDisplayName()
{
  var element = document.getElementById("uploadedAttachment");
  var filename = "";
  var separator = "";

  if (element && element != null)
  {
    filename = element.value;
    if (filename != null)
    {
      if (filename.charAt(0) == "/")
      {
        separator = "/";
      }
      else
      {
        separator = "\\";
      }
      filename = filename.substring(filename.lastIndexOf(separator) + 1);
    }

    element = document.getElementById("attachmentDisplayName");
    if (element && element != null)
    {
      element.value = filename;
    }
  }
}


/* Workaround for bug 116835/116546 - this function is a copy of that generated by the
caf:validationTrigger tag that is now not evaluated correctly by action
This function has been captured using Fiddler and pasted in here unmodified. */
function validateResponseEmailPage() {
  atgValidation_initValidationObjects();

  var inputValue = null;
  var validationResult = null;
  var validationInputObject = document.getElementById("outMessageForm").to;
  if (validationInputObject != null) {
    inputValue = atgValidation_escapeQuotes(validationInputObject.value);
    validationResult = eval("atgValidation_validateRequiredField('" + inputValue + "',false)");
    if (validationResult != validationCode.SUCCESS) {
      dijit.byId('messageBar').addMessage({type:'error', summary:validationCode.ERROR_EMPTY_STRING + ": To"});
      isSuccess = false;
    }
  }
  
  validationInputObject = document.getElementById("outMessageForm").htmlBody;  
  if (validationInputObject != null && validationInputObject.value != null) {
    inputValue = atgValidation_escapeQuotes(validationInputObject.value);
    validationResult = eval("atgValidation_validateRequiredField('" + inputValue + "',false)");    
    if (validationResult != validationCode.SUCCESS) {
      dijit.byId('messageBar').addMessage({type:'error', summary:validationCode.ERROR_EMPTY_STRING + ": Message Body"});
      isSuccess = false;
    }
  }
  return isSuccess;
}
/* End Workaround */

function nodeSelectedCallback(path, info)
{
  if(!window.path)
   window.path=new Object();
  window.path = path;
  if(!window.info)
    window.info=new Object();
  window.info = info;
  //alert('node selected: id=' + info.id + ' type=' + info.type);
  var element = document.getElementById("contentBrowserContentDetailsForm").contentBrowserId;
  if (element && element != null && info)
    element.value = info.id;
  element = document.getElementById("contentBrowserContentDetailsForm").contentBrowserType;
  if (element && element != null && info)
    element.value = info.type;
  //__ppr_contentBrowserContentDetails.transact();
  contentBrowserContentDetails();
}

function nodeSelectOnOPageReturn()
{
  nodeSelectedCallback(window.path, window.info);
}

/**
 * Replaces the innerHtml of targetId with the innerHtml of sourceId
 * @param targetId  The id of the target container
 * @param sourceId  The id of the source container
 */
function ResponseReplaceContentWith(targetId, sourceId)
{
  var targetElement = document.getElementById(targetId);
  var sourceElement = document.getElementById(sourceId);
  if (targetElement != null && sourceElement != null)
  {
    targetElement.innerHTML = sourceElement.innerHTML;
  }
}

/**
 * Hides the container with the given id
 * @param elementId The id of the container to hide.
 */
function ResponseHideContainer(elementId)
{
  var element = document.getElementById(elementId);
  if (element != null)
  {
    element.style.display = "none";
  }
}
function ResponseMarkupSpellingErrors() {
  var spellCheckerDiv      = document.getElementById("spellCheckDiv");
  var regexp;

  if (spellCheckerDiv.innerHTML.length < 3) {
    alert(getResource("editor.spellcheck.none"));
    return;
  }
  var currentValue = ResponseGetStrippedValue();
  for (var x = 0; x < spellCheckerDiv.childNodes.length; x++) {
    if (spellCheckerDiv.childNodes[x].getElementsByTagName) {
      var aMisspelledWords     = spellCheckerDiv.childNodes[x].getElementsByTagName("div");
      for (var i = 0; i < aMisspelledWords.length; i++) {
        var errorNode          = "<span class='spellerror'>" + aMisspelledWords[i].innerHTML + "</span>";
        var spellingMarkup     = aMisspelledWords[i].innerHTML.replace("\\", "\\\\").replace("?", "\\?").replace("[","\\[").replace("^","\\^");
        spellingMarkup         = spellingMarkup.replace("$", "\\$").replace(".","\\.").replace("|", "\\|").replace("*", "\\*").replace("+","\\+");
        spellingMarkup         = spellingMarkup.replace("(", "\\(").replace(")","\\)");
        regexp       = new RegExp("(?![^<]+>)\\b" + spellingMarkup + "\\b(?![^<]+>)", "g");
        currentValue = currentValue.replace(regexp, errorNode);
      }
    }
  }
  FCKeditorAPI.GetInstance('RespondEditor').SetHTML(currentValue);
}

function ResponseGetStrippedValue() {
  var aSpans       = FCKeditorAPI.GetInstance('RespondEditor').EditorDocument.getElementsByTagName("span");

  for (var i = aSpans.length; i > 0; i--)
  {
    if (aSpans[i-1].className.toUpperCase() == "SPELLERROR")
    {
      if (document.all)
      {
        aSpans[i-1].outerHTML = aSpans[i-1].innerHTML;
      }
      else
      {
        var outerRange = FCKeditorAPI.GetInstance('RespondEditor').EditorDocument.createRange();
        outerRange.setStartBefore(aSpans[i-1]);
        var fragment   = outerRange.createContextualFragment(aSpans[i-1].innerHTML);
        aSpans[i-1].parentNode.replaceChild(fragment, aSpans[i-1]);
      }
    }
  }
  return FCKeditorAPI.GetInstance('RespondEditor').GetHTML();
}
