/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2009 Art Technology Group, Inc.
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
// editor.js File
//
// This page defines the Editor object.  The Editor object provides very
// basic editing functioanlity and is responsible for exposing this 
// functionality for extension.  Although this object is a fully capable
// stand alone object, it's designed to be extended.
//
// Here is the information you need to make this work:
//
// 1: Include this file into the page where you want to add the editor.
//
// 2: Create the editor with a call to "new Editor()".
//
// 3: After adding the editor to the page, call the "init()" method
//    on the editor to initialize the editor
//
//*************************************************************************

/**************************************************************************
 **
 **
 **  Editor Object
 **
 **
 **  This object is implemented as a "DIV" element and
 **  employes an iFrame element set with "designMode" to on
 **  This allows for the editing of the object.  Definition
 **  of object properties should be done before the editor
 **  is added to the document and EditorInit() is called.
 **
 *************************************************************************/
 
var Editor = function (initialTemplate, onLoadHandler)
{
  console.debug("Editor constructor");
  //***********************************************************************
  // Create Editor
  //
  this.editor                               = document.createElement("div");
  
  //***********************************************************************
  // Initialize Editor properties
  //
  this.editor.id                            = "";
  this.editor.width                         = "100%";
  this.editor.className                     = "";
  
  //***********************************************************************
  // Initialize Editor reference properties
  //
  this.editor.editFrame                     = "";
  this.editor.currentRange                  = "";
  this.editor.editDocument                  = "";
  this.editor.modified                      = false;
  this.editor.readOnly                      = false;
  this.editor.initialContent                = "";
  
  //***********************************************************************
  // Initialize Editor methods
  /**************************************************************************
   **  EditorInit
   **  This function sets the event handlers on IE and sets the initial value
   **  of the edit document if it was provided.
   *************************************************************************/
  this.editor.init                          = function () 
  {
    console.debug("EditorInit");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editWindow                            = editor.editFrame.contentWindow;
    var editDocument                          = editWindow.document;
    
    //***********************************************************************
    // Get Body
    //  
    var editBody                              = editDocument.body;
    editBody.id                               = editor.id;
    editor.editDocument                       = editDocument;
    //***********************************************************************
    // Set editor refrences
    //
    editWindow.editor                         = editor;
    editDocument.editor                       = editor;
    editBody.editor                           = editor;
    //***********************************************************************
    // We need to seperate the initialization sequence between IE and mozilla
    // due to the fact that they need to call different event handlers and have
    // them registered in a specific sequence.
    //  
    if (document.all)
    {
      dojo.connect(editDocument, "oncontextmenu", editor, editor.handleContextMenu);
      dojo.connect(editDocument, "onclick", editor, editor.handleClick);
      dojo.connect(editDocument, "ondblclick", editor, editor.handleDblClick);
      dojo.connect(editDocument, "onkeyup", editor, editor.handleKeyUp);
      dojo.connect(editDocument, "onkeydown", editor, editor.handleKeyDown);
      dojo.connect(editDocument, "onkeypress", editor, editor.handleKeyPress);
      dojo.connect(editDocument, "onmouseup", editor, editor.handleMouseUp);
      dojo.connect(editDocument, "onmousedown", editor, editor.handleMouseDown);
      dojo.connect(editDocument, "onmousemove", editor, editor.handleMouseMove);
      dojo.connect(editBody, "onfocus", editor, editor.handleFocus);
      dojo.connect(editBody, "onblur", editor, editor.handleBlur);
      dojo.connect(editBody, "onpaste", editor, editor.handlePaste);
      dojo.connect(editBody, "ondrop", editor, editor.handleDrop);
      editBody.contentEditable = "true";
    }
    else
    {    
      editDocument.addEventListener("contextmenu", editor.handleContextMenu, true);
      editDocument.addEventListener("click",       editor.handleClick,       true);
      editDocument.addEventListener("dblclick",    editor.handleDblClick,    true);
      editDocument.addEventListener("dragdrop",    editor.handleDrop,        true);
      editDocument.addEventListener("keyup",       editor.handleKeyUp,       true);
      editDocument.addEventListener("keydown",     editor.handleKeyDown,     true);
      editDocument.addEventListener("keypress",    editor.handleKeyPress,     true);
      editDocument.addEventListener("mouseup",     editor.handleMouseUp,     true);
      editDocument.addEventListener("mousedown",   editor.handleMouseDown,   true);
      editDocument.addEventListener("mousemove",   editor.handleMouseMove,   true);
      editDocument.addEventListener("blur",        editor.handleBlur,        true);
      editDocument.addEventListener("focus",       editor.handleFocus,       true);
      setTimeout( function() { editDocument.designMode = "on";}, 1000 );
    }
  
    //***********************************************************************
    // Set initial value
    //
    if (editor.initialContent != "")
      editBody.innerHTML                      = editor.initialContent;
  };

  this.editor.destroy = function () {
    console.debug("EditorDestroy");
    // undo what was done in init
    var editor                                = this;
    var editWindow                            = editor.editFrame.contentWindow;
    var editDocument                          = editWindow.document;
  
    var editBody                              = editDocument.body;
    editBody.id                               = editor.id;
    editor.editDocument                       = editDocument;
  
    editWindow.editor                         = undefined;
    editDocument.editor                       = undefined;
    editBody.editor                           = undefined;

    // undo what was done in the constructor
    if (document.all)
    {
      dojo.disconnect(editDocument, "oncontextmenu", editor, editor.handleContextMenu);
      dojo.disconnect(editDocument, "onclick", editor, editor.handleClick);
      dojo.disconnect(editDocument, "ondblclick", editor, editor.handleDblClick);
      dojo.disconnect(editDocument, "onkeyup", editor, editor.handleKeyUp);
      dojo.disconnect(editDocument, "onkeydown", editor, editor.handleKeyDown);
      dojo.disconnect(editDocument, "onkeypress", editor, editor.handleKeyPress);
      dojo.disconnect(editDocument, "onmouseup", editor, editor.handleMouseUp);
      dojo.disconnect(editDocument, "onmousedown", editor, editor.handleMouseDown);
      dojo.disconnect(editDocument, "onmousemove", editor, editor.handleMouseMove);
      dojo.disconnect(editBody, "onfocus", editor, editor.handleFocus);
      dojo.disconnect(editBody, "onblur", editor, editor.handleBlur);
      dojo.disconnect(editBody, "onpaste", editor, editor.handlePaste);
      dojo.disconnect(editBody, "ondrop", editor, editor.handleDrop);
      editBody.contentEditable = "false";
    }
    else
    {    
      // do nothing, we couldn't use dojo.connect
    }   
    if (editor.editFrame) {
      editor.editFrame.editor.innerHTML = '';
      editor.editFrame.editor = undefined;
      if (editor.editFrame.parentNode) {
        dojo.clean(editor.editFrame.parentNode.removeChild(editor.editFrame));
      }
    }

    this.editor = undefined;
  };
  /**************************************************************************
   **
   **
   **  EditorSetText
   **  
   **  This function sets the innerText of the editor.
   **
   *************************************************************************/
  this.editor.setText = function (text)
  {
    console.debug("EditorSetText");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;
  
    editSrcWindow.document.body.innerHTML     = escape(text);
    editor.editorModify(null, editor);
  };
  
  /**************************************************************************
 **
 **
 **  EditorGetText
 **  
 **  This function returns the inner text of the editor
 **
 *************************************************************************/
  this.editor.getText = function ()
  {
    console.debug("EditorGetText");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;
  
    if (document.all)
      return editSrcWindow.document.body.innerText;
    else
      return editSrcWindow.document.body.innerHTML.replace(/<br>/gi,"\n").replace(/&nbsp\;/g, " ").replace(/<[^>]+>/g,"").replace(/&amp\;/g, "&");    
  };
  
  /**************************************************************************
 **
 **
 **  EditorSetHTML
 **  
 **  This function sets the innerHTML of the editor.
 **
 *************************************************************************/
  this.editor.setHTML = function (html)
  {
    console.debug("EditorSetHTML");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;
  
    if (!editor.editDocument.body) {
      editor.initialContent = html;
    } else {
      editSrcWindow.document.body.innerHTML     = html;
      editor.editorModify(null, editor);
    }
  };
  
  /**************************************************************************
 **
 **
 **  EditorGetHTML
 **  
 **  This function returns the HTML content of the editor.
 **
 *************************************************************************/
  this.editor.getHTML = function ()
  {
    console.debug("EditorGetHTML");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
    var editSrcWindow                         = editor.editFrame.contentWindow;    
  
    return editSrcWindow.document.body.innerHTML.replace(/<p>/gi,"<br>").replace(/<\/p>/gi,"");
  };
  
  /**************************************************************************
 **
 **
 **  EditorReplaceRange
 **  
 **  This function replaces the supplied range with the supplied node
 **
 *************************************************************************/
  this.editor.replaceRange = function (range, newNode)
  {
    console.debug("EditorReplaceRange");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
  
    if (document.all)
    {
      try
      { 
        range.select();
        range.pasteHTML(newNode.outerHTML);
      }
      catch(e)
      {
        return 0;
      }
      return range;
    }
    else
    {
      var extract = range.extractContents();
      range.insertNode(newNode);
      return range;
    }
  };
  
  /**************************************************************************
 **
 **
 **  EditorGetRange
 **  
 **  This function returns the currently selected range object
 **
 *************************************************************************/
  this.editor.getRange = function ()
  {
    console.debug("EditorGetRange");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
  
    if (document.all)
    {
      editor.editFrame.contentWindow.document.body.focus();
      return editor.editFrame.contentWindow.document.selection.createRange();
    }
    else
    {
      var selection = editor.editFrame.contentWindow.getSelection();
      if (!selection || selection.rangeCount == 0)
        return;
      return selection.getRangeAt(selection.rangeCount - 1).cloneRange();
    }
  };
  
  /**************************************************************************
 **
 **
 **  EditorSetSelectionStyle
 **  
 **  This function sets the supplied style as the style of the current
 **  range.
 **
 *************************************************************************/
  this.editor.setSelectionStyle = function (className)
  {
    console.debug("EditorSetSelectionStyle");
    //***********************************************************************
    // Get Editor
    //
    var editor                                = this;
  
    // We want to remove all of the formatting, but preserve carriage returns if we can.  So
    // lets replace the existing break tags (and paragraph tags) and save them for later.
    // With this replace, replace spaces with non-breaking spaces, otherwise they disappear
    //
    var span   = editor.editFrame.contentWindow.document.createElement("span");
    var range  = null;

    if (document.all)
    {
      range          = editor.getRange();
      span.innerHTML = range.htmlText;
    }
    else
    {
      var selection = editor.editFrame.contentWindow.getSelection();
      if (selection.rangeCount > 0) 
      {
        range               = selection.getRangeAt(0);
        var clonedSelection = range.cloneContents();
        clonedSelection     = span.appendChild(clonedSelection);
      }
    }
  
    span.innerHTML = editor.stripExtraHTML(span.innerHTML);
    span.className = className;

    var cells        = span.getElementsByTagName("td");
    for (var i = 0; i < cells.length; i++)
    {
      cells[i].className = className;
    }

    editor.replaceRange(range, span);
  };
  
  /**************************************************************************
 **
 **
 **  EditorValidateContent
 **  
 **  This function validates and corrects the supplied HTML
 **
 *************************************************************************/
  this.editor.validateContent = function (source)
  {
    console.debug("EditorValidateContent");
    var editor                      = this;
    var childMap                    = new Array();
    var replacementMap              = new Array();
    var replacementPrefix           = "//ATGCHILDREPLACEMENT//";
   
    if (source == "")
      return "";
   
    var testDiv                     = document.createElement("div");
    testDiv.style.display           = "none";
    testDiv                         = editor.appendChild(testDiv);
   
    testDiv.innerHTML               = source;
   
    var children                    = testDiv.childNodes;
   
    for (var i = children.length; i > 0; i--)
    {
      if (children[i-1].nodeType == 1)
      {
        if (children[i-1].innerHTML != "")
        {
          var container                   = document.createElement("div");
          var clone                       = children[i-1].cloneNode(true);
          clone.innerHTML                 = editor.validateContent(clone.innerHTML);
          clone                           = container.appendChild(clone);
          childMap[childMap.length]       = container.innerHTML;
          testDiv.innerHTML               = testDiv.innerHTML.replace(container.innerHTML, replacementPrefix + (childMap.length -1));
        }
      }
    }
    testDiv.innerHTML                   = editor.stripExtraHTML(testDiv.innerHTML);
   
    for (var x = 0; x < childMap.length; x++)
    {
      testDiv.innerHTML = testDiv.innerHTML.replace(replacementPrefix + x, childMap[x]);    
    }  
    
    var stringReturn                            = testDiv.innerHTML; 
    editor.removeChild(testDiv);
    return stringReturn;
  };

/**************************************************************************
 **
 **
 **  EditorStripExtraHTML
 **  
 **  This function validates and corrects the supplied HTML
 **
 *************************************************************************/
  this.editor.stripExtraHTML = function (html)
  {
    console.debug("EditorStripExtraHTML");
    html = html.replace(/<a/g,         "pksi-openanchor").replace(/<A/g,         "pksi-openanchor");
    html = html.replace(/<\/a>/g,       "pksi-closeanchor").replace(/<\/A>/g,       "pksi-closeanchor");
    html = html.replace(/<img/g,         "pksi-openimage").replace(/<IMG/g,         "pksi-openimage");
    html = html.replace(/<p>/g,         "pksi-openpara").replace(/<P>/g,         "pksi-openpara");
    html = html.replace(/<ul>/g,        "pksi-openulist").replace(/<UL>/g,        "pksi-openulist");
    html = html.replace(/<ol>/g,        "pksi-openolist").replace(/<OL>/g,        "pksi-openolist");
    html = html.replace(/<li>/g,        "pksi-openlitem").replace(/<LI>/g,        "pksi-openlitem");
    html = html.replace(/<\/ul>/g,      "pksi-closeulist").replace(/<\/UL>/g,      "pksi-closeulist");
    html = html.replace(/<\/ol>/g,      "pksi-closeolist").replace(/<\/OL>/g,      "pksi-closeolist");
    html = html.replace(/<\/li>/g,      "pksi-closelitem").replace(/<\/LI>/g,      "pksi-closelitem");
    html = html.replace(/<\/p>/g,       "pksi-closepara").replace(/<\/P>/g,       "pksi-closepara");
    html = html.replace(/<br>/g,        "pksi-breaktag").replace(/<BR>/g,        "pksi-breaktag");
    html = html.replace(/<td/g,         "pksi-opencelltag").replace(/<TD/g,         "pksi-opencelltag");
    html = html.replace(/<\/td>/g,      "pksi-closecelltag").replace(/<\/TD>/g,      "pksi-closecelltag");
    html = html.replace(/<tr/g,         "pksi-openrowtag").replace(/<TR/g,         "pksi-openrowtag");
    html = html.replace(/<\/tr>/g,      "pksi-closerowtag").replace(/<\/TR>/g,      "pksi-closerowtag");
    html = html.replace(/<table/g,      "pksi-opentabletag").replace(/<TABLE/g,      "pksi-opentabletag");
    html = html.replace(/<\/table>/g,   "pksi-closetabletag").replace(/<\/TABLE>/g,   "pksi-closetabletag");

    html = html.replace(/<\S[^>]*>/g,   "");

    html = html.replace(/pksi-openanchor/g,      "<a");
    html = html.replace(/pksi-openimage/g,      "<img");
    html = html.replace(/pksi-closeanchor/g,     "</a>");
    html = html.replace(/pksi-openpara/g,        "<p>");
    html = html.replace(/pksi-closepara/g,       "</p>");
    html = html.replace(/pksi-openulist/g,        "<ul>");
    html = html.replace(/pksi-closeulist/g,       "</ul>");
    html = html.replace(/pksi-openolist/g,        "<ol>");
    html = html.replace(/pksi-closeolist/g,       "</ol>");
    html = html.replace(/pksi-openlitem/g,        "<li>");
    html = html.replace(/pksi-closelitem/g,       "</li>");
    html = html.replace(/pksi-breaktag/g,        "<br>");
    html = html.replace(/pksi-opencelltag/g,     "<td");
    html = html.replace(/pksi-closecelltag/g,    "</td>");
    html = html.replace(/pksi-openrowtag/g,      "<tr");
    html = html.replace(/pksi-closerowtag/g,     "</tr>");
    html = html.replace(/pksi-opentabletag/g,    "<table");
    html = html.replace(/pksi-closetabletag/g,   "</table>");
  
    return html;
  };

/**************************************************************************
 **
 **
 **  EditorSetCSS
 **  
 **  This function sets stylesheet source for the editor
 **
 *************************************************************************/
  this.editor.setCSS = function (cssSource)
  {
    console.debug("EditorSetCSS");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    var cssLink               = editor.editDocument.createElement("link");
    cssLink.rel               = "stylesheet";
    cssLink.type              = "text/css";
    cssLink.href              = cssSource;
  
    var head                  = editor.editDocument.getElementsByTagName("head");
    cssLink                   = head[0].appendChild(cssLink);
  };
/**************************************************************************
 **
 **
 **  EditorInsertText
 **  
 **  This function inserts text at the caret position
 **
 *************************************************************************/
  this.editor.insertText = function (text)
  {
    console.debug("EditorInsertText");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    //***********************************************************************
    // Set replacement node
    //
    var range                 = editor.getRange();
  
    if (document.all)
      range.text              = text;
    else
    {
      var insertNode            = editor.editDocument.createTextNode(text);
      editor.replaceRange(range, insertNode);
    }
  };
/**************************************************************************
 **
 **
 **  EditorInsertHTML
 **  
 **  This function inserts text at the caret position
 **
 *************************************************************************/
  this.editor.insertHTML = function (html)
  {
    console.debug("EditorInsertHTML");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    //***********************************************************************
    // Set replacement node
    //
    var insertNode            = editor.editDocument.createElement("div");
    insertNode.innerHTML      = html;
  
    var range                 = editor.getRange();
  
    editor.replaceRange(range, insertNode);
  };
  
  /**************************************************************************
 **
 **
 **  EditorFocus
 **  
 **  This function sets focus to the editor
 **
 *************************************************************************/
  this.editor.focus = function ()
  {
    console.debug("EditorFocus");
    //***********************************************************************
    // Get Editor
    //
    var editor                = this;
  
    //***********************************************************************
    // focus
    //
    if (editor.editDocument.body.focus)
      editor.editDocument.body.focus();
  };
  
  //***********************************************************************
  // Initialize Editor event handling 
  // We need these and the integration calls to make sure we can pass
  // the correct event object to the integration
  //
  this.editor.handleKeyDown                 = function (event)
  {
    console.debug("EditorHandleKeyDown");
    var editor                                = (this.editor) ? this.editor : this;
    editor.modified                           = true;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);
  
    if (editor)  
    {
      editor.editorModify(event, editor);
      return editor.editorKeyDown(event, editor);
    }
  };
  this.editor.handleKeyPress                = function (event)
  {
    console.debug("EditorHandleKeyPress");
    var editor                                = (this.editor) ? this.editor : this;
    editor.modified                           = true;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorKeyPress(event, editor);
  };
  this.editor.handleClick                   = function (event)
  {
    console.debug("EditorHandleClick");
    var editor                                = (this.editor) ? this.editor : this;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    // Sometimes IE goes haywire on load.  Let's make sure the
    // body is active
    //
    if (document.all)
    {
      var test = editor.getRange();
      test.select();
	}
  };
  this.editor.handleDblClick                = function (event)
  {
    console.debug("EditorHandleDblClick");
    var editor                                = (this.editor) ? this.editor : this;
  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (document.all && editor)
    {
      try
      {
  		var test = editor.getRange();
  		test.expand("word");
  		test.select();
      }
      catch(e)
      {}
    }
  };
  this.editor.handleContextMenu             = function (event)
  {
    console.debug("EditorHandleContextMenu");
    var editor                                = (this.editor) ? this.editor : this;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event); 
    
    if (editor)  
      return editor.editorContextMenu(event, editor);
  };
  this.editor.handleKeyUp                   = function (event)
  {
    console.debug("EditorHandleKeyUp");
    var editor                                = (this.editor) ? this.editor : this;
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);
    
    if (editor)  
    {
      editor.editorModify(event, editor);
      return editor.editorKeyUp(event, editor);
    }
  };
  this.editor.handleBlur                    = function (event)
  {
    console.debug("EditorHandleBlur");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)  
      return editor.editorBlur(event, editor);
  };
  this.editor.handleFocus                   = function (event)
  {
    console.debug("EditorHandleFocus");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorFocus(event, editor);
  };
  this.editor.handleMouseMove               = function (event)
  {
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorMouseMove(event, editor);
  };
  this.editor.handleMouseDown               = function (event)
  {
    console.debug("EditorHandleMouseDown");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)  
      return editor.editorMouseDown(event, editor);
  };
  this.editor.handleMouseUp                 = function (event)
  {
    console.debug("EditorHandleMouseUp");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);   
    
    if (editor)  
      return editor.editorMouseUp(event, editor);
  };
  this.editor.handleDrop                    = function (event)
  {
    console.debug("EditorHandleDrop");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)  
      return editor.editorDrop(event, editor);
  };
  this.editor.handlePaste                   = function (event)
  {
    console.debug("EditorHandlePaste");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);  
    
    if (editor)
    {  
      editor.editorModify(event, editor);
      return editor.editorPaste(event, editor);
    }
  };
  this.editor.handleResize                  = function (event)
  {
    console.debug("EditorHandleResize");
    var editor                                = (this.editor) ? this.editor : this;  
    if (!event)
      event                                   = editor.editFrame.contentWindow.event;
  
    if (!editor.editFrame)
      editor                                  = getEditor(event);
  
    if (editor)  
      return editor.editorResize(event, editor);
  };
  
  //***********************************************************************
  // Initialize Editor integration calls
  //
  this.editor.editorClick                   = new Function();
  this.editor.editorContextMenu             = new Function();
  this.editor.editorKeyDown                 = new Function();
  this.editor.editorKeyUp                   = new Function();
  this.editor.editorKeyPress                = new Function();
  this.editor.editorBlur                    = new Function();
  this.editor.editorFocus                   = new Function();
  this.editor.editorModify                  = new Function();
  this.editor.editorMouseMove               = new Function();
  this.editor.editorMouseDown               = new Function();
  this.editor.editorMouseUp                 = new Function();
  this.editor.editorPaste                   = new Function();
  this.editor.editorDrop                    = new Function();
  this.editor.editorResize                  = new Function();
  
  //***********************************************************************
  // Initialize Editor element structure
  // 
  var editIframe                            = document.createElement("iframe");
  editIframe.src                            = initialTemplate;
  
  if (editIframe.addEventListener) {
    editIframe.addEventListener("load", onLoadHandler, false)
  }
  else if (editIframe.attachEvent) {
    editIframe.detachEvent("onload", onLoadHandler)
    editIframe.attachEvent("onload", onLoadHandler)
  }
  
  this.editor.editFrame                     = this.editor.appendChild(editIframe);
  this.editor.editFrame.editor              = this.editor;
  this.editor.editFrame.height              = "200";
  this.editor.editFrame.width               = "100%";
  this.editor.editFrame.border              = "0";  
  this.editor.editFrame.frameBorder         = "0";  
  
  return this.editor;                                     
}

function getEditor(event)
{
  console.debug("getEditor");
  if (event) {
    if (event.target) {
      if (event.target.body) {
        return document.getElementById(event.target.body.id);
      } else {
        return document.getElementById(event.target.ownerDocument.body.id);
      }
    }
  }   
}
