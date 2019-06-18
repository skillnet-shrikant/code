function getTreeTable(treeTableId) {
  //console.debug("getTreeTable");
  var treeTable = null;
  for (var i = 0; i < window.treeTables.length; i++) {
    if (window.treeTables[i].treeTableId == treeTableId) {
      treeTable = window.treeTables[i];
      break;
    }
  }
  return treeTable;
}
function TreeTable(treeTableId) {
  //console.debug("TreeTable");
  this.partialPageRenderer = null;
  this.controlBars = new Array();
  this.selectedItem = null;
  this.stateSavingMethod = "client";
  this.treeTableId = treeTableId;
}
function getAttributeValue(element, name) {
  //console.debug("getAttributeValue");
  var value = "";
  try {
    value = element.attributes.getNamedItem(name).nodeValue;
  }
  catch (ex) {
  }
  return value;
}
function setAttributeValue(element, name, value) {
  //console.debug("setAttributeValue");
  try {
    element.attributes.getNamedItem(name).nodeValue = value;
  }
  catch (ex) {
  }
}
function TreeTableAddForm(treeTableId, name, value) {
  //console.debug("TreeTableAddForm");
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var partialPageRenderer = treeTable.partialPageRenderer;
  if (!partialPageRenderer)
    return;

  partialPageRenderer.addForm(name, value, true);
}
function TreeTableEnd(treeTableId) {
  //console.debug("TreeTableEnd");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonLast = document.getElementById(treeTableId + controlBarId + "ControlBarlast");
    if (pagingButtonLast) {
      TreeTableFireClickEvent(pagingButtonLast);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTableEnter(selectedItem) {
  //console.debug("TreeTableEnter");
  var isHandled = false;
  var spanTags = selectedItem.getElementsByTagName("span");
  for (var i = 0; i < spanTags.length; i++) {
    var spanTag = spanTags[i];
    if (spanTag.id) {
      var idComponents = spanTag.id.split(":");
      if (!idComponents || idComponents.length != 3)
        continue;
    
      var type = idComponents[1];
      if (type == "expandButton") {
        TreeTableFireClickEvent(spanTag);
        break;
      }
    }
  }
  return isHandled;
}
function TreeTableExecuteOperation(operation, treeTableId, parameters) {
  //console.debug("TreeTableExecuteOperation");
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var partialPageRenderer = treeTable.partialPageRenderer;
  var action = treeTable.action;
  if (!partialPageRenderer && !action)
    return;

  if (partialPageRenderer) {
    var protocol = partialPageRenderer.getProtocol();
    if (protocol == "formhandlers") { // form handlers
      partialPageRenderer.addForm("operation", operation, true);
    }
    else { // struts
      partialPageRenderer.addForm("method", operation, true);
    }

    // Populate form pairs for invoked method
    //
    partialPageRenderer.addForm("treeTableId", treeTableId, true);
    if (parameters) {
      for (var i = 0; i < parameters.length; i++) {
        if (parameters[i].hasValue) {
          partialPageRenderer.addForm(parameters[i].name, parameters[i].value, true);
        }
        else {
          partialPageRenderer.addForm(parameters[i].name, eval("document.getElementById('" + parameters[i].elementId + "')." + parameters[i].elementProperty + ";"), true);
        }
      }
    }

    if (treeTable.stateSavingMethod == "client") {
      partialPageRenderer.addForm("state", document.getElementById(treeTableId + "State").innerHTML, true);
    }
    // Submit form pairs via HTTP POST
    //
    partialPageRenderer.transact();
  }
  else if (action) {
    // new style
    var state = "";
    if (document.getElementById(treeTableId + "State")) {
      state = document.getElementById(treeTableId + "State").innerHTML;
      action(operation, parameters,state);
    } else {
      action(operation, parameters);
    }
  }
}
function TreeTableFilterSelectChange(component, e) {
  //console.debug("TreeTableFilterSelectChange");
  if (component.options) {
    for (var i = 0; i < component.options.length; i++) {
      if (component.options[i].selected) {
        TreeTableTransact("filter", component.options[i].value);
        break;
      }
    }
  }
}
function TreeTableFireClickEvent(element) {
  //console.debug("TreeTableFireClickEvent");
  if (element == null)
    return;

  var isIe = false;
  if (document.all) { // IE
    isIe = true;
  }

  if (isIe) {
    element.fireEvent("onclick");
  }
  else {
    var event = document.createEvent("MouseEvents");
    event.initMouseEvent("click", true, true, this.defaultView, 0, element.screenX + 1, element.screenY + 1, 1, 1, false, false, false, false, 0, null);
    element.dispatchEvent(event);
  }
}
function TreeTableGetFirstControlBarId(treeTableId) {
  //console.debug("TreeTableGetFirstControlBarId");
  var controlBarId = null;
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  if (treeTable.controlBars.length > 0) {
    controlBarId = treeTable.controlBars[0];
  }
  return controlBarId;
}
function TreeTableHome(treeTableId) {
  //console.debug("TreeTableHome");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonFirst = document.getElementById(treeTableId + controlBarId + "ControlBarfirst");
    if (pagingButtonFirst) {
      TreeTableFireClickEvent(pagingButtonFirst);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTableKeydown(treeTableId, e) {
  //console.debug("TreeTableKeydown");
  var isIe = false;
  var isHandled = false;
  if (document.all) { // IE
    e = event;
    isIe = true;
  }

  var keyCode = e.keyCode;
  var body = document.getElementById(treeTableId + "Body");
	if(!body) {
		try {
			document.detachEvent('onkeydown', eval("OnKeydown" + treeTableId) ); // doesn't work often?
    } catch(e) { /*alert('OnKeydown' + treeTableId + ' error: ' + e.message); */}

    return;
	}
	
  var selectedItem = null;

  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var selectedItemId = treeTable.selectedItem;
  if (selectedItemId) {
    selectedItem = document.getElementById(selectedItemId);
  }
  switch (keyCode) {
  case 13: // enter
    if (selectedItem) {
      isHandled = TreeTableEnter(selectedItem);
    }
    break;
  case 33: // page up
    isHandled = TreeTablePageUp(treeTableId);
    break;
  case 34: // page down
    isHandled = TreeTablePageDown(treeTableId);
    break;
  case 35: // end
    if (e.ctrlKey) {
      isHandled = TreeTableEnd(treeTableId);
    }
    else {
      isHandled = TreeTableSelectLastItem(body);
    }
    break;
  case 36: // home
    if (e.ctrlKey) {
      isHandled = TreeTableHome(treeTableId);
    }
    else {
      isHandled = TreeTableSelectFirstItem(body);
    }
    break;
  case 38: // up arrow
    if (selectedItem) {
      isHandled = TreeTableSelectPreviousItem(selectedItem);
    }
    else {
      isHandled = TreeTableSelectLastItem(body);
    }
    break;
  case 40: // down arrow
    if (selectedItem) {
      isHandled = TreeTableSelectNextItem(selectedItem);
    }
    else {
      isHandled = TreeTableSelectFirstItem(body);
    }
    break;
  default:
    break;
  }
  if (isHandled) {
    if (isIe) { // IE
      e.returnValue = false;
      e.cancelBubble = true;
    }
    else {
      e.preventDefault();
      e.stopPropagation();
    }
  }
}
function TreeTablePageDown(treeTableId) {
  //console.debug("TreeTablePageDown");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonNext = document.getElementById(treeTableId + controlBarId + "ControlBarnext");
    if (pagingButtonNext) {
      TreeTableFireClickEvent(pagingButtonNext);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTablePageUp(treeTableId) {
  //console.debug("TreeTablePageUp");
  var isHandled = false;
  var controlBarId = TreeTableGetFirstControlBarId(treeTableId);
  if (controlBarId) {
    var pagingButtonPrevious = document.getElementById(treeTableId + controlBarId + "ControlBarprevious");
    if (pagingButtonPrevious) {
      TreeTableFireClickEvent(pagingButtonPrevious);
      isHandled = true;
    }
  }
  return isHandled;
}
function TreeTableRefresh(treeTableId) {
  //console.debug("TreeTableRefresh");
  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  TreeTableExecuteOperation("refresh", treeTableId, null);
}
function TreeTableSelectChangeEventHandler(action, operation, component) {
  //console.debug("TreeTableSelectChangeEventHandler");
  if (component.options) {
    for (var i = 0; i < component.options.length; i++) {
      if (component.options[i].selected) {
        TreeTableTransactEventHandler(action, operation, component.options[i].value);
        break;
      }
    }
  }
}
function TreeTableSelectFirstItem(tbody) {
  //console.debug("TreeTableSelectFirstItem");
  var isHandled = false;
  if(!tbody)
    return isHandled;
  var firstItem = tbody.firstChild;
  while (firstItem && !firstItem.tagName && firstItem.tagName != "table") {
    firstItem = firstItem.nextSibling;
  }
  if (firstItem) {
    TreeTableFireClickEvent(firstItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSelectLastItem(tbody) {
  //console.debug("TreeTableSelectLastItem");
  var isHandled = false;
  if(!tbody)
    return isHandled;  
  var lastItem = tbody.lastChild;
  while (lastItem && !lastItem.tagName && lastItem.tagName != "table") {
    lastItem = lastItem.previousSibling;
  }
  if (lastItem) {
    TreeTableFireClickEvent(lastItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSelectNextItem(item) {
  //console.debug("TreeTableSelectNextItem");
  var isHandled = false;
  var nextItem = item.nextSibling;
  while (nextItem && !nextItem.tagName && nextItem.tagName != "table") {
    nextItem = nextItem.nextSibling;
  }
  if (nextItem) {
    TreeTableFireClickEvent(nextItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSelectPreviousItem(item) {
  //console.debug("TreeTableSelectPreviousItem");
  var isHandled = false;
  var previousItem = item.previousSibling;
  while (previousItem && !previousItem.tagName && previousItem.tagName != "table") {
    previousItem = previousItem.previousSibling;
  }
  if (previousItem) {
    TreeTableFireClickEvent(previousItem);
    isHandled = true;
  }
  return isHandled;
}
function TreeTableSortSelectChange(component, e) {
  //console.debug("TreeTableSortSelectChange");
  if (component.options) {
    for (var i = 0; i < component.options.length; i++) {
      if (component.options[i].selected) {
        TreeTableTransact("sort", component.options[i].value, null);
        break;
      }
    }
  }
}
function TreeTableTransact(operation, id) {
  //console.debug("TreeTableTransact");
  var idComponents = id.split(":");
  if (!idComponents || idComponents.length != 3)
    return;

  var treeTableId = idComponents[0];
  var parameters = idComponents[2];

  var treeTable = getTreeTable(treeTableId);
  if (!treeTable)
    return;

  var partialPageRenderer = treeTable.partialPageRenderer;
  var action = treeTable.action;
  if (!partialPageRenderer && !action)
    return;

  if (partialPageRenderer)
  {
    var protocol = partialPageRenderer.getProtocol();
    if (protocol == "formhandlers") { // form handlers
      partialPageRenderer.addForm("operation", operation, true);
    }
    else { // struts
      partialPageRenderer.addForm("method", operation, true);
    }

    // Populate form pairs for invoked method
    //
    partialPageRenderer.addForm("parameters", parameters, true);
    partialPageRenderer.addForm("treeTableId", treeTableId, true);

    if (treeTable.stateSavingMethod == "client") {
      partialPageRenderer.addForm("state", document.getElementById(treeTableId + "State").innerHTML, true);
    }

    // Submit form pairs via HTTP POST
    //
    partialPageRenderer.transact();
  }
  else if (action){
    if (action.length < 3) {
      alert("Your action " + action + " is missing its (operation, parameters, state) parameters.  Please go add them and set them into the matching values in your form");
    }
    var state;
    if (treeTable.stateSavingMethod == "client") {
      state = document.getElementById(treeTableId + "State").innerHTML;
    }
    action(operation, parameters, state);
  }
}
function TreeTableTransactEventHandler(actionString, operation, id) {
  //console.debug("TreeTableTransactEventHandler");
  var idComponents = id.split(":");
  if (!idComponents || idComponents.length != 3)
    return;

  var partialPageRenderer = window["__ppr_" + actionString];
  var action = window[actionString];
  
  if (partialPageRenderer){
    var protocol = partialPageRenderer.getProtocol();
    if (protocol == "formhandlers") { // form handlers
      partialPageRenderer.addForm("operation", operation, true);
    }
    else { // struts
      partialPageRenderer.addForm("method", operation, true);
    }

    // Populate form pairs for invoked method
    //
    var parameters = idComponents[2];
    partialPageRenderer.addForm("parameters", parameters, true);

    var treeTableId = idComponents[0];
    partialPageRenderer.addForm("treeTableId", treeTableId, true);

    // Submit form pairs via HTTP POST
    //
    partialPageRenderer.transact();
  }
  else if (action){
    if (action.length < 2) {
      alert("Your action " + action + " is missing its (operation, parameters) parameters.  Please go add them and set them into the matching values in your form");
    }
    action(operation, parameters);
  }
}
function atgSetupTreeTable(theForm, operation, parameters, state) {
  //console.debug("atgSetupTreeTable");
  if (operation) {
    theForm.operation.value = operation;
  } else {
    theForm.operation.value = "refresh";
  }
  if (parameters){
    theForm.parameters.value = parameters;
  }
  if (state) {
    theForm.state.value= state;
  }
}
function TreeTableCancelEvent(event){
  //console.debug("TreeTableCancelEvent");
	if (!event) event = window.event;
	event.cancelBubble = true;
	if (event.stopPropagation) event.stopPropagation();
	if (event.preventDefault) event.preventDefault();
  return false;
}
function treeTableHandleOnLoad(idValue){
  //console.debug("treeTableHandleOnLoad -- " + idValue);
  var headTagId = idValue + "HeadTag";
  var head = document.getElementById(headTagId);
  var bodyTagId = idValue + "BodyTag";
  var body = document.getElementById(bodyTagId);
  if (head && body) { head.style.width = body.offsetWidth; }
}
function treeTableHandleOnResize(idValue){
  //console.debug("treeTableHandleOnResize -- " + idValue);
  var headTagId = idValue + "HeadTag";
  var bodyTagId = idValue + "BodyTag";
  var head = document.getElementById(headTagId); 
  var body = document.getElementById(bodyTagId); 
  if (head && body) { 
    head.style.width = body.offsetWidth; 
  }
}
if (!window.treeTables) window.treeTables = new Array();
