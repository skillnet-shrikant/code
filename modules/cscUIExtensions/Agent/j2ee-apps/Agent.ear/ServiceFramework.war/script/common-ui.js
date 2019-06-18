// This should add an input tag to a form dynamically
//
function addNewInput(strName, strType, strValue, objForm, objDocument)
{
   var objInput       = objDocument.createElement("input");
   objInput.id        = strName;
   objInput.name      = strName;
   objInput.type      = strType;
   objInput.value     = strValue;
   objInput = objForm.appendChild(objInput);
}

// THis will remove HTML from a given string
//
function stripHTML(oldString)
{
  return oldString.replace(/(<([^>]+)>)/ig, "");
}

// Encodes text as HTML
function escapeHTML(text) {
  var textNode  = document.createTextNode(text);
  var div       = document.createElement('div');
  div.appendChild(textNode);
  return div.innerHTML;
};

// This function will do a replace all of "strReplace" in "str", when it finds "strFind"
// this function does not use regular expressions
//
function getElt () {
    var name = getElt.arguments[getElt.arguments.length-1];
    var element = document.getElementById(name);
    return element;
}

function replaceAll(str, strFind, strReplace)
{
  var returnStr = str;
  var strFindUpper = strFind.toUpperCase()
  var start = returnStr.toUpperCase().indexOf(strFindUpper);

  while (start >= 0)
  {
    returnStr = returnStr.substring(0, start) + strReplace + returnStr.substring(start + strFind.length, returnStr.length);
    start = returnStr.toUpperCase().indexOf(strFindUpper, start + strReplace.length);
  }
  return returnStr;
}

function trim(str)
{
   var strTrim = trimRight(str)
   return trimLeft(strTrim);
}

function trimRight(str)
{
   var retStr = str;
   var i = str.length - 1;
   while (i >= 0)
   {
      if (str.substring(i, i + 1) == " ")
         retStr = str.substring(0, i);
      else
         break;
      i--;
   }
   return retStr;
}

function trimLeft(str)
{
   var retStr = str;
   var i = 0;
   while (i < str.length)
   {
      if (str.substring(i, i + 1) == " ")
         retStr = str.substring(i + 1, str.length);
      else
         break;
      i++;
   }
   return retStr;
}

  /**
   *
   * This method is used only if you have already known content. If you are not making
   * a server trip to get the floating pane content, then you can use this method to load
   * the floating pane content.
   * This method takes the original div content and sticks it to the floating pane and removes the
   * original div conent.
   *
   */
  showPopupWithContent = function (pOriginalContentDivId, pFloatingPane/*Floating pane Id*/, args ) {
     var originalDiv = document.getElementById ('pOriginalDivId');
     var confirmWindow = dijit.byId(pFloatingPane);

    // This function gets called when the popup is closed
    function closeBuddy ()
    {
        if ( args.onClose ) {
          args.onClose( getEnclosingPopup(pFloatingPane)._atg_results );
        }
    }

    getEnclosingPopup(pFloatingPane)._atg_args = args;
    
    if (confirmWindow.connectHandle) dojo.disconnect(confirmWindow.connectHandle);
    confirmWindow.connectHandle = dojo.connect( confirmWindow, "hide", closeBuddy );

     confirmWindow.setContent (originalDiv);
     originalDiv.innerHTML="";
     confirmWindow.show();
  };

// Return enclosing Dojo Dialog given the ID of any child node
getEnclosingPopup = function ( nodeId )
{
  var startAtNode;
  if (dijit.byId(nodeId)) {
    startAtNode = dijit.byId(nodeId).domNode;
  }
  else if (dojo.byId(nodeId)) {
    startAtNode = dojo.byId(nodeId);
  }
  var foundNode = dojo.dom.getAncestors(
    startAtNode,
    function ( node ) {
      if ( node.className && node.className.indexOf("dijitDialogPaneContent") == -1 ) {
        if ( node.className.indexOf("dijitDialog") >= 0 ) {
          if ( node.tagName == "DIV" ) {
            return true;
          }
        }
      }
      return false;
    },
    true );
  return dijit.byId(foundNode.id);
};

showPopupWithResults = function ( args )
{
  var popupPane = dijit.byId( args.popupPaneId );
  
  // Initing popup results
  popupPane._atg_results = {};

  // This function gets called when the popup is closed
  var closeBuddy = function (args)
  {
    if ( args.onClose ) {
      args.onClose( getEnclosingPopup(args.popupPaneId)._atg_results );
    }
  }

  this.getEnclosingPopup(args.popupPaneId)._atg_args = args;

  // need to replace any previously connected function
  if (popupPane.closeHandle) dojo.disconnect(popupPane.closeHandle);
  popupPane.closeHandle = dojo.connect(popupPane, "hide", dojo.hitch(this, closeBuddy, args));
  popupPane.titleNode.innerHTML = args.title || "";

  popupPane.setHref( args.url );
  popupPane.show();
};

hidePopupWithResults = function ( childId, results )
{
  setTimeout(function () {
    var popup = this.getEnclosingPopup( childId );
    popup._atg_results = results;
    popup.hide();
  }, 50);
};

/** 
 * Escapes XML in provided string
 */
function escapeXML(string) 
{
	return string.replace(/\"/g,'&quot;').replace(/\'/g,'&#39;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/\(/g,'&#40;').replace(/\)/g,'&#41;');
}