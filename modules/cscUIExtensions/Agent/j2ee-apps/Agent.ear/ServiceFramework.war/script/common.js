
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

/*
//
//  Misc
//
//

//  This function will do a replace all of  "strReplace" in "str", when it finds "strFind"
//  this function does not use  regular expressions
*/
function  ReplaceAll(str, strFind, strReplace)
{
  var returnStr = str;
  var strFindUpper = strFind.toUpperCase();
  var start = returnStr.toUpperCase().indexOf(strFindUpper);

  while (start >= 0)
  {
    returnStr = returnStr.substring(0, start) + strReplace +  returnStr.substring(start + strFind.length, returnStr.length);
    start = returnStr.toUpperCase().indexOf(strFindUpper, start + strReplace.length);
  }
  return returnStr;
}

//   Editor
//
var l_bHandleAltGRKey = false;
var l_bInitGRKeyVar = false;

function  IsAltKeyDown(objEvent)
{
  //  On  the french keyboard,  the right alt key is  known as  the "Alt+GR" key.  It's purpose is much differnt than the left  Alt Key.
  //  So  lets handle this properly,  and do it only  for the french  language  (as determined  by  the web server, it is conceivable that  we
  //  could do  this by determining the language  of  the web browser, but  this seems like a better approach).  It should be noted that
  //  "altLeft" is not part of of the Javascript spec, it is something specific to Microsoft's IE browsers.
  //
  //  Cache this for  speed
  //
  if  (!l_bInitGRKeyVar)
  {
    l_bHandleAltGRKey = "false" == "true";
    l_bInitGRKeyVar = true;
  }

  if  (l_bHandleAltGRKey)
  {
    if  (objEvent.altLeft)
    {
      return true;
    }
    else
    {
      if  (objEvent.altKey)
      {
        return false;
      }
    }
    return false;
  }
  else {
    return objEvent.altKey;
  }
}

function  IsAltGRKeyDown(objEvent)
{
  //  On  the french keyboard,  the right alt key is  known as  the "Alt+GR" key.  It's purpose is much different than the left Alt Key.
  //  So  lets handle this properly,  and do it only  for the french  language  (as determined  by  the web server, it is conceivable that  we
  //  could do  this by determining the language  of  the web browser, but  this seems like a better approach).
  //
  //  The way to know whether or  not the Alt+GR  key is down is  down is when the Ctrl and Alt keys are  down at the same time.  This seems
  //  odd, but  that is how the Alt+GR is interpreted
  //
  //  Cache this for  speed
  //
  if  (!l_bInitGRKeyVar)
  {
    l_bHandleAltGRKey = "false" == "true";
    l_bInitGRKeyVar = true;
  }

  if  (l_bHandleAltGRKey)
  {
    return (objEvent.altKey &&  objEvent.ctrlKey);
  }
  else
  {
    return false;
  }
}

function  getStatementText(objWindow)
{
  //  When dealing with a document that has "designmode"  set to "on", in Netscape, if the  innerHTML of that document
  //  was empty, it will now be a "<BR>" with a carriage  return and linefeed on the  end.  In  this case only, we want
  //  to  return an empty string
  //
  //  For IE, the end of the document could very well be  a "<p>&nbsp;</p>" if  there is  nothing in the  cell
  //
  var objBody = objWindow.document.body;
  if  (!objBody || objBody.innerHTML == "") {
    return "";
  }
  else if ((objBody.innerHTML.length == 6 &&
    objBody.innerHTML.slice(0,  4).toLowerCase() == "<br>"  &&
    objBody.innerHTML.charCodeAt(4) == 13 &&
    objBody.innerHTML.charCodeAt(5) == 10)  ||
    (objBody.innerHTML.toLowerCase()  ==  "<p>&nbsp;</p>")){
    return "";
  }
  else
  {
    var str = objBody.innerHTML;

    //  Lets see  if  the statement is wrapped with paragraph tags, if so, lets remove them
    //
    if  ((objBody.childNodes.length == 1) &&
      (objBody.firstChild)  &&  (objBody.firstChild.tagName) &&
      (objBody.firstChild.tagName.toUpperCase() ==  "P")) {
      str = objBody.firstChild.innerHTML;
    }
    return str;
  }
}
          
function GetEvent(event)
{
  return (event) ? event : window.event;
}

function HidePopups()
{
}

function hideCurtain(curtainId) {
  if((curtainId != null) && (curtainId != "")) {
    var curtainObject = dojo.byId(curtainId);
    if (curtainObject != null) {
      curtainObject.style.display = "none";
    }
  }
}

function showCurtain(curtainId) {
  if((curtainId != null) && (curtainId != "")) {
    var curtainObject = dojo.byId(curtainId);
    if (curtainObject != null) {
      var windowWidth = document.body.offsetWidth;
      var windowHeight = document.body.offsetHeight;
      var scrollWidth = document.body.scrollWidth;
      var scrollHeight = document.body.scrollHeight;
      
      if(scrollWidth > windowWidth) {
        curtainObject.style.width = scrollWidth;
      } else {
        curtainObject.style.width = windowWidth;
      }
      if(scrollHeight > windowHeight) {
        curtainObject.style.height = scrollHeight;
      } else {
        curtainObject.style.height = windowHeight;
      }
      curtainObject.style.display = "block";
    }
  }
}

// Generic function for displayed a centered dialog with the ability to specify the dialog parameters
// Sample usage: showDialog("myfile.jsp","My Title",640,480,true,false,true);
function showDialog(url, name, dialogWidth, dialogHeight, isStatus, isScrollbars, isResizable) {
  if ((isStatus == null) || (isStatus != true)) {
    isStatus = "no";
  } else {
    isStatus = "yes";
  }
  
  if ((isScrollbars == null) || (isScrollbars != true)) {
    isScrollbars = "no";
  } else {
    isScrollbars = "yes";
  }
  
  if ((isResizable == null) || (isResizable != true)) {
    isResizable = "no";
  } else {
    isResizable = "yes";
  }

  // Offset values to take window decoration into consideration
  dialogWidth += 32;
  dialogHeight += 96;
  leftPosition = (screen.width - dialogWidth) / 2;
  topPosition = (screen.height - dialogHeight) / 2;

  var dialogWindow = window.open (url, name,
    'width=' + dialogWidth + ', height=' + dialogHeight + ', ' +
    'left=' + leftPosition + ', top=' + topPosition + ', ' +
    'location=no, menubar=no, ' +
    'status=' + isStatus + ', toolbar=no, scrollbars=' + isScrollbars + ', resizable=' + isResizable);
  
  // Just in case width and height are ignored
  dialogWindow.resizeTo(dialogWidth, dialogHeight);
  // Just in case left and top are ignored
  dialogWindow.moveTo(leftPosition, topPosition);
  dialogWindow.focus();
}

function toggle(id, imgid) {
  var ele = document.getElementById(id);
  if (ele == null) { return;}
  var elt = ele.style;
  var imgElt = document.getElementById(imgid);
  if (imgElt == null) {return;}
  if (elt.display == "none") {
    elt.display = "block";
    imgElt.src = getResource("imgArrowDown");
  }
  else {
    elt.display = "none";
    imgElt.src = getResource("imgArrowRight");
  }
}

function toggleRowGroup(id, imgid) {
  var ele = document.getElementById(id);
  if (ele == null) { return;}
  var elt = ele.style;
  var imgElt = document.getElementById(imgid);
  if (imgElt == null) {return;}
  if (elt.display == "none") {
    elt.display = "table-row-group";
    imgElt.src = getResource("imgArrowDown");
  }
  else {
    elt.display = "none";
    imgElt.src = getResource("imgArrowRight");
  }
}

function show(id) {
  var elem = dojo.byId(id);
  if (elem == null) {return;}
  var disp = dojo.style(elem, 'display');
  if (disp == '' || disp == "none") {
    dojo.style(elem, 'display', 'block');
  }
}
function hide(id) {
  var elem = dojo.byId(id);
  if (elem == null) {return;}
  if (dojo.style(elem, 'display') != 'none') {
    dojo.style(elem, 'display', 'none');
  }
}
function toggleShowing(id) {
  var elem = dojo.byId(id);
  var disp = dojo.style(elem, 'display');
  if (disp == '' || disp == "none") {
    dojo.style(elem, 'display', 'block');
  } 
  else 
  {
    dojo.style(elem, 'display', 'none');
  }
}
function isShowing(id) {
  var elem = dojo.byId(id);
  if (dojo.style(elem, "display") != "none") {
    return true;
  } 
  else 
  {
    return false;
  }
}

function dispAssignedAgent() {
  var w = document.frmLeftContent.sAssignedAgent.selectedIndex;
  var selected_text = document.frmLeftContent.sAssignedAgent.options[w].text;
  document.getElementById('aaa').innerHTML=selected_text;
}
function getPageOffsetLeft(el){
  var x;
  x=el.offsetLeft;
  if (el.offsetParent!=null) {
    x+=getPageOffsetLeft(el.offsetParent);
  }
  return x;
}
function getPageOffsetTop(el){
  var y;
  y=el.offsetTop;
  if (el.offsetParent!=null){
    y+=getPageOffsetTop(el.offsetParent);
  }
  return y;
}
function divSetVisible(divId)
{
 var divRef = document.getElementById(divId);
 if (divRef == null) {return;}
 var iframeRef = document.getElementById('divShim');
 if(divRef.style.display == "none")
 {
  divRef.style.display = "block";
  divRef.style.visibility = "visible";
  if (dojo.isIE)
  {
    iframeRef.style.width = divRef.offsetWidth+"px";
    iframeRef.style.height = divRef.offsetHeight+"px";
    iframeRef.style.top = getPageOffsetTop(divRef)+"px";
    iframeRef.style.left = getPageOffsetLeft(divRef)+"px";
    iframeRef.style.zIndex = divRef.style.zIndex - 1;
    iframeRef.style.display = "block";
    iframeRef.style.visibility = "visible";
  }
 }
}

function divSetHide(divId)
{
 var divRef = document.getElementById(divId);
 if (divRef == null) {return;}
 var iframeRef = document.getElementById('divShim');
 if(divRef.style.display == "block")
 {
  divRef.style.display = "none";
  divRef.style.visibility = "hidden";
  if (dojo.isIE)
  {
    iframeRef.style.display = "none";
    iframeRef.style.visibility = "hidden";
  }
 }
}

function loadFragments(forms)  {
  if (forms.length > 0) {
    for (var i = 0; i < forms.length; i++)  {
      dojo.byId(forms[i]).submit();
    }
  }
}

function resizeTransactionFragmentContainer(iframeWindow)
{
  if (iframeWindow.document.height)
  {
    iframeWindow.frameElement.style.height = (iframeWindow.document.height + 10) + "px";
  }
  else
  {
    iframeWindow.frameElement.style.height = (iframeWindow.document.body.scrollHeight + 10) +"px";
  }
}

function getInnerText(element)
{
  if (document.all) {
    return element.innerText;
  }
  else {
    return element.innerHTML.replace(/<br>/gi,"\n").replace(/&nbsp\;/g, " ").replace(/<[^>]+>/g,"");
  }
}

/** 
 * This is defined in /service/Agent and is knowledge specific
 * It's used/called by /script/framework.js 
 * 
 */
function saveState()
{
	return;
}