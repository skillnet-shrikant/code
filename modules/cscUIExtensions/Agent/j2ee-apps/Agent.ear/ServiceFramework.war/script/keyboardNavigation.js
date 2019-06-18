// -------------------------------------------------------------------
// Imports
// -------------------------------------------------------------------
dojo.provide("atg.keyboard");

dojo.require("dojox.collections.SortedList");

// -------------------------------------------------------------------
// Constants
// -------------------------------------------------------------------
atg.keyboard._keyCtrl = "CTRL";
atg.keyboard._keyAlt = "ALT";
atg.keyboard._keyShift = "SHIFT";
atg.keyboard._keySeparator = "+";
atg.keyboard._classTopLevelIdentifier = "atg_keyboard_top_level_identifier";
atg.keyboard._classPanelIdentifier = "atg_keyboard_panel_identifier";
atg.keyboard._panelTitleIdentifier = "panel";
atg.keyboard._minimizeIdentifier = "icon_minimize";
atg.keyboard._restoreIdentifier = "icon_maximize";
atg.keyboard._dockIdentifier = "icon_arrow";
atg.keyboard._undockIdentifier = "icon_arrow";


atg.keyboard._tabPressed = false;
atg.keyboard._showNotificationWindow = true;
atg.keyboard._highlightAndFadePanels = true;
atg.keyboard._highlightAndFadeNodes = true;

// -------------------------------------------------------------------
// Keyboard navigation help window
// -------------------------------------------------------------------
atg.keyboard.keyboardNavShortcuts = [];
atg.keyboard.keyboardNavHelpWindowContents = [];
atg.keyboard.keyboardNavHelpWindow = null;
atg.keyboard.keyboardNavHelpWindowDiv = null;
atg.keyboard.keyboardNavNotificationWindow = null;
atg.keyboard.keyboardNavCurrentNode = "";
  
// -------------------------------------------------------------------
// Keyboard navigation windows
// -------------------------------------------------------------------
atg.keyboard.keyboardNavHelpMessage = null;
atg.keyboard.keyboardNavNotificationMessage = null;

// -------------------------------------------------------------------
// Keycode special character lookup for IE
// -------------------------------------------------------------------
atg.keyboard.lookupKeyCodeIE = {
  "112": "F1",
  "113": "F2",
  "114": "F3",
  "115": "F4",
  "116": "F5",
  "117": "F6",
  "118": "F7",
  "119": "F8",
  "120": "F9",
  "121": "F10",
  "122": "F11",
  "123": "F12",
  "189": "-",
  "187": "=",
  "219": "[",
  "221": "]",
  "220": "\\",
  "186": ";",
  "222": "'",
  "188": ",",
  "190": ".",
  "191": "/",
  "192": "`",
  "37": "LEFT",
  "37": "UP",
  "37": "RIGHT",
  "37": "DOWN",
  "8": "BACKSPACE"
};

// -------------------------------------------------------------------
// Keycode special character lookup for Mozilla
// -------------------------------------------------------------------
atg.keyboard.lookupKeyCodeMozilla = {
  "112": "F1",
  "113": "F2",
  "114": "F3",
  "115": "F4",
  "116": "F5",
  "117": "F6",
  "118": "F7",
  "119": "F8",
  "120": "F9",
  "121": "F10",
  "122": "F11",
  "123": "F12",
  "109": "-",
  "61": "=",
  "219": "[",
  "221": "]",
  "220": "\\",
  "59": ";",
  "222": "'",
  "188": ",",
  "190": ".",
  "191": "/",
  "192": "`",
  "37": "LEFT",
  "37": "UP",
  "37": "RIGHT",
  "37": "DOWN",
  "8": "BACKSPACE"
};

// -------------------------------------------------------------------
// Keyboard navigation initialization
// -------------------------------------------------------------------
atg.keyboard.init = function () {
  console.debug("Keyboard Nav | Keyboard shortcut initialization function called");
  atg.keyboard.keyboardNavHelpWindow = dijit.byId("keyboardNavHelpObject");
  atg.keyboard.keyboardNavHelpWindowDiv = dijit.byId("keyboardNavHelpObjectDiv");
  atg.keyboard.keyboardNavNotificationWindow = dijit.byId("keyboardNavNotificationObject");
  
  // Set up the event listener for keyboard navigation on the document object
  if(dojo.isIE || dojo.isSafari) {
    dojo.connect(dojo.doc, "onkeyup", atg.keyboard.activateKeyboardShortcut);
  }
  else {
    dojo.connect(dojo.doc, "onkeypress", atg.keyboard.activateKeyboardShortcut);
  }
  
  // Set up the event listener for tab highlighting
  dojo.connect(dojo.doc, "onfocus", atg.keyboard.activateTabHighlight);
  
  // Loop through all the UI elements that support tab highlighting and attach listeners
  // Comment out as we're assuming all items support tab highlighting unless explicitly set to -1
  /*
  var _uiListeners = dojo.query(".atg_navigationHighlight");
  console.debug(_uiListeners.length + " Navigation Highlight Listeners Found");
  for (_count=0; _count<_uiListeners.length; _count++){
    dojo.connect(_uiListeners[_count],"onfocus", atg.keyboard, "highlightAndFade"); 
  }
  */
  
};

// -------------------------------------------------------------------
// Keyboard shortcut event controller
// -------------------------------------------------------------------
atg.keyboard.activateKeyboardShortcut = function (e) {
  var _keyboardShortcutKey;
  var _keyboardShortcutObjects = [];
  var _topic;
  var _action;
  
  if (atg.keyboard.isTabPressed(e) == true) {
    atg.keyboard.setTab();
  }
  else 
    if (atg.keyboard.isModifierPressed(e) == true) {
    atg.keyboard.keyboardNavCurrentNode = e.target;

    _keyboardShortcutKey = atg.keyboard.getShortcutKeyPressed(e);
    console.debug("Keyboard Nav | Shortcut key pressed: " + _keyboardShortcutKey);
    
    _keyboardShortcutObjects = atg.keyboard.getKeyboardNavShortcuts(_keyboardShortcutKey);
    if ((_keyboardShortcutObjects != null) && (_keyboardShortcutObjects.length > 0)) {
      
      if (atg.keyboard._showNotificationWindow == true) {
        // Always use the first object within the array to post to the notification window
        atg.keyboard.setNotificationWindowMessage(_keyboardShortcutObjects[0]);
      }

      // Extract the object data and call the respective topic and action
      for(var _count = 0; _count < _keyboardShortcutObjects.length; _count++) { 
        _topic = _keyboardShortcutObjects[_count].topic;
        _action = _keyboardShortcutObjects[_count].action;
        
        if ((_action != null) && (typeof _action == "function")) {
          console.debug("Keyboard Nav | Calling JavaScript action: " + _action);
          _action();
        }
        
        if ((_topic != null) && (_topic.length > 0)) {
          console.debug("Keyboard Nav | Publishing topic: " + _topic);
          dojo.publish(_topic);
        }
      }
    } 
  }
  
  /*
  e.cancelBubble = true;
  e.returnValue = false;

  if (e.stopPropagation) {
    e.stopPropagation();
    e.preventDefault();
  }
  */
};

// -------------------------------------------------------------------
// Tab highlighting event controller
// -------------------------------------------------------------------
atg.keyboard.activateTabHighlight = function (e) {
  atg.keyboard.keyboardNavCurrentNode = e.target;
  
  if (atg.keyboard.getTab() == true) {
    console.debug ("Detecting tab pressed - highlighting node");
    atg.keyboard.highlightAndFade(atg.keyboard.keyboardNavCurrentNode);
  }
};

// -------------------------------------------------------------------
// Sets the value of tabPressed temporarily for highlighting purposes
// -------------------------------------------------------------------
atg.keyboard.setTab = function () {
  atg.keyboard._tabPressed = true;
  setTimeout(function(){
     atg.keyboard._tabPressed = false;
  },500);
};

// -------------------------------------------------------------------
// Return value of tabPressed
// -------------------------------------------------------------------
atg.keyboard.getTab = function () {
  return atg.keyboard._tabPressed;
};

// -------------------------------------------------------------------
// Keyboard shortcut object registration
// -------------------------------------------------------------------
atg.keyboard.registerShortcut = function (_shortcutKey, _shortcutObject) {
  if ((atg.keyboard.keyboardNavShortcuts.length == 0) || (atg.keyboard.keyboardNavShortcuts[_shortcutKey] == null)) {
    //console.debug("Keyboard Nav | Creating new array for shortcut key " + _shortcutKey);
    atg.keyboard.keyboardNavShortcuts[_shortcutKey] = [];
  }
  
  if (_shortcutObject != null) {
    atg.keyboard.keyboardNavShortcuts[_shortcutKey].push(_shortcutObject);
  }
};

// -------------------------------------------------------------------
// Utility method to determine whether the user is tabbing in the UI
// -------------------------------------------------------------------
atg.keyboard.isTabPressed = function (e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var e  = e || e.event;

  if ((e.ctrlKey == true) || (e.altKey == true)) {
    return false;
  }
  
  if (e.keyCode == 9) {
    return true;
  }

  return false;
};


// -------------------------------------------------------------------
// Utility method to determine whether the user pressed the Enter key
// -------------------------------------------------------------------
atg.keyboard.isEnterPressed = function (e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var e  = e || e.event;
  if ((e.ctrlKey == true) || (e.altKey == true)) {
    return false;
  }
  
  if (e.keyCode == 13) {
    return true;
  }

  return false;
};


// -------------------------------------------------------------------
// Utility method to ensure that at least one modifier key is pressed
// -------------------------------------------------------------------
atg.keyboard.isModifierPressed = function (e) {
  if ((e.ctrlKey == true) || (e.altKey == true) || (e.shiftKey == true)) {
    console.debug("Keyboard Nav | Modifier key pressed - looking up keyboard shortcut");
    return true;
  }
  return false;
};

// -------------------------------------------------------------------
// Utility method to figure out key modifier and shortcut key pressed
// -------------------------------------------------------------------
atg.keyboard.getShortcutKeyPressed = function (e) {
  var _keyPressed = "";
  var _keyLookup;

  console.debug("Keyboard Nav | e.keyCode = " + e.keyCode + ", e.keyChar = " + e.keyChar + ", e.key = " + e.key + ", e.charCode = " + e.charCode);
  if (e.ctrlKey == true) {
    _keyPressed = atg.keyboard._keyCtrl + atg.keyboard._keySeparator;
  }
  if (e.altKey == true) {
    _keyPressed += atg.keyboard._keyAlt + atg.keyboard._keySeparator;
  }
  if (e.shiftKey == true) {
    _keyPressed += atg.keyboard._keyShift + atg.keyboard._keySeparator;
  }
 
  if(dojo.isIE || dojo.isSafari) {
    if (e.keyCode != null) {
      if (e.keyCode > 90) {
        _keyLookup = atg.keyboard.lookupKeyCodeIE[e.keyCode];
        if (_keyLookup == null) {
          _keyPressed += e.keyCode;
        }
        else {
          _keyPressed += _keyLookup;
        }
      }
      else {
        _keyPressed += String.fromCharCode(e.keyCode);
      }
    }
  }
  else {
    _keyLookup = atg.keyboard.lookupKeyCodeMozilla[e.keyCode];
    //_keyLookup = atg.keyboard.lookupKeyCodeMozilla[e.keyChar];
    if (_keyLookup == null) {
      if (e.keyChar != null) {
        _keyPressed += e.keyChar;
      }
      else {
        _keyPressed += e.key;
      }
    }
    else {
      _keyPressed += _keyLookup;
    }
  }
  
  _keyPressed = _keyPressed.toUpperCase();
  return _keyPressed;
};
  
// -------------------------------------------------------------------
// Return an array of zero or more key lookup objects
// -------------------------------------------------------------------
atg.keyboard.getKeyboardNavShortcuts = function (_lookupKey) {
  return atg.keyboard.keyboardNavShortcuts[_lookupKey];
};

// -------------------------------------------------------------------
// Keyboard navigation popup message notifier
// -------------------------------------------------------------------
atg.keyboard.setNotificationWindowMessage = function (_keyboardShortcutObject) {
  var _shortcut = _keyboardShortcutObject.shortcut;  
  var _name = _keyboardShortcutObject.name;
  var _description = _keyboardShortcutObject.description;
  var _area = _keyboardShortcutObject.area;
  var _notify = _keyboardShortcutObject.notify;
  
  if (_notify == true) {
    console.debug("Keyboard Nav | Sending '" + _shortcut + "' event to notification window");
    atg.keyboard.keyboardNavNotificationMessage = "<span class='atg_keyboard_notificationMessageHeading'>" + _shortcut + "</span><br /><span class='atg_keyboard_notificationMessageSubHeading'>" + _name + "</span>";
    //dojo.publish("keyboardNavNotificationTopic", [{ message: atg.keyboard.keyboardNavNotificationMessage, type: "fatal", duration: 500 }]);
    dojo.publish("keyboardNavNotificationTopic", [atg.keyboard.keyboardNavNotificationMessage]);
  }
};

// -------------------------------------------------------------------
// Display keyboard navigation help window
// -------------------------------------------------------------------
atg.keyboard.showKeyboardShortcutHelpWindow = function () {
  atg.keyboard.getHelpWindowContents();
  dijit.byId("keyboardNavHelpObject").show();
};

// -------------------------------------------------------------------
// Retrieve ordered list of keyboard nav shortcuts for help window
// -------------------------------------------------------------------
atg.keyboard.getHelpWindowContents = function () {
  console.debug("Keyboard Nav | Loading shortcut key help dialog");
  
  var _cssRow;
  var _rowCount = 0;
  
  var _helpWindowContents = '<table class="atg_keyboard_helpDialog" cellspacing="0" cellpadding="0" border="0"><tr class="atg_keyboard_helpDialogHeadings"><th>'+getResource("keyboard.popup.shortcut")+'</th><th></th><th>'+getResource("keyboard.popup.name")+'</th><th></th><th>'+getResource("keyboard.popup.description")+'</th><th></th><th>'+getResource("keyboard.popup.area")+'</th></tr>';
  
  // We will comment out the routine to display shortcuts exactly as they are defined in the JS files and instead use
  // a simple sorting algorithm to display shortcuts in alphabetical order, based on the shortcut name
  /*
  var _shortcutArray = atg.keyboard.keyboardNavShortcuts;
  for(var _countPrimary in atg.keyboard.keyboardNavShortcuts) {
    for(var _countSecondary = 0; _countSecondary < atg.keyboard.keyboardNavShortcuts[_countPrimary].length; _countSecondary++) { 
      var _shortcutContents = atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary];
  
      _cssRow = "atg_keyboard_helpDialogShadingOn";
      if(_rowCount % 2 == 0) { 
        _cssRow = "atg_keyboard_helpDialogShadingOff";
      }
  
      _helpWindowContents += '<tr class="' + _cssRow + '"><td nowrap>' + _shortcutContents.shortcut + '<td class="atg_keyboard_tableSpacer"></td><td>' + _shortcutContents.name + '<td class="atg_keyboard_tableSpacer"></td><td>' + _shortcutContents.description + '<td class="atg_keyboard_tableSpacer"></td><td nowrap>' + _shortcutContents.area + '</td></tr>';
      _rowCount ++;
    }
  }
  */

  var _shortcutArrayByName = atg.keyboard.getKeyboardShortcutsByName();
  var _sortedArray;
  var _sortedArrayContents;
  var _cssRow;
  var _rowCount = 0;
  var _helpWindowContents = '<table class="atg_keyboard_helpDialog" cellspacing="0" cellpadding="0" border="0"><tr class="atg_keyboard_helpDialogHeadings"><th>'+getResource("keyboard.popup.shortcut")+'</th><th></th><th>'+getResource("keyboard.popup.name")+'</th><th></th><th>'+getResource("keyboard.popup.description")+'</th><th></th><th>'+getResource("keyboard.popup.area")+'</th></tr>';
  
  for (var _countPrimary in _shortcutArrayByName) {
    _sortedArray = new dojox.collections.SortedList();
    
    for(var _countSecondary = 0; _countSecondary < _shortcutArrayByName[_countPrimary].length; _countSecondary++) { 
      _sortedArray.add(_shortcutArrayByName[_countPrimary][_countSecondary].shortcut, _shortcutArrayByName[_countPrimary][_countSecondary]);
    }
    
    for (var _i = 0; _i < _sortedArray.count; _i++) {
      _sortedArrayContents = _sortedArray.getByIndex(_i);
      _cssRow = "atg_keyboard_helpDialogShadingOn";
      if(_rowCount % 2 == 0) { 
        _cssRow = "atg_keyboard_helpDialogShadingOff";
      }
      _helpWindowContents += '<tr class="' + _cssRow + '"><td nowrap>' + _sortedArrayContents.shortcut + '<td class="atg_keyboard_tableSpacer"></td><td>' + _sortedArrayContents.name + '<td class="atg_keyboard_tableSpacer"></td><td>' + _sortedArrayContents.description + '<td class="atg_keyboard_tableSpacer"></td><td nowrap>' + _sortedArrayContents.area + '</td></tr>';
      _rowCount ++;
    }
  }

  
  // need to use destroyNode to prevent IE memory leak
  var helpWindow = dojo.byId("atg_keyboard_keyboardNavHelpWindowDiv");
  while(helpWindow.hasChildNodes()){ dojo._destroyElement(helpWindow.firstChild); }
  helpWindow.innerHTML = _helpWindowContents;
};

// -------------------------------------------------------------------
// Keyboard shortcut event controller
// -------------------------------------------------------------------
atg.keyboard.activateHelpDialogShortcut = function (e) {
  if (e.keyChar == 27) {
    console.debug("Keyboard Nav | Escape key pressed - closing shortcut key help dialog");
    dojo.publish("CloseHelpDialog");
  }
};


// -------------------------------------------------------------------
// Rearrange the navigation shortcuts to be grouped by functional area
// -------------------------------------------------------------------
atg.keyboard.getKeyboardShortcutsByFunctionalArea = function () {
  var _shortcutArrayByArea = [];
  var _shortcutArrayByAreaSorted = [];
  var _sortedArrayContents;
  var _area;
  var _sortedArray = new dojox.collections.SortedList();
  
  for(var _countPrimary in atg.keyboard.keyboardNavShortcuts) {
    for(var _countSecondary = 0; _countSecondary < atg.keyboard.keyboardNavShortcuts[_countPrimary].length; _countSecondary++) { 
      _area = atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary].area;
      
      if (_shortcutArrayByArea[_area] == null) {
        console.debug("Keyboard Nav | Adding " + _area + " category to the window shortcut array");
        _shortcutArrayByArea[_area] = [];
      }
      
      _shortcutArrayByArea[_area].push(atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary]);
    }
  }
  
  for(var _countList in _shortcutArrayByArea) {
    if (_shortcutArrayByArea[_countList].length > 0) {
      _sortedArray.add(_shortcutArrayByArea[_countList][0].area, _shortcutArrayByArea[_countList]);   
    }
  }

  for (var _i = 0; _i < _sortedArray.count; _i++) {
    _sortedArrayContents = _sortedArray.getByIndex(_i);
    _shortcutArrayByAreaSorted.push(_sortedArrayContents);
  }
 
  return _shortcutArrayByAreaSorted;
};


// -------------------------------------------------------------------
// Rearrange the navigation shortcuts to be grouped by name
// -------------------------------------------------------------------
atg.keyboard.getKeyboardShortcutsByName = function () {
  var _shortcutArrayByName = [];
  var _shortcutArrayByNameSorted = [];
  var _sortedArrayContents;
  var _name;
  var _sortedArray = new dojox.collections.SortedList();
  
  for(var _countPrimary in atg.keyboard.keyboardNavShortcuts) {
    for(var _countSecondary = 0; _countSecondary < atg.keyboard.keyboardNavShortcuts[_countPrimary].length; _countSecondary++) { 
      _name = atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary].name;
      
      if (_shortcutArrayByName[_name] == null) {
        console.debug("Keyboard Nav | Adding " + _name + " category to the window shortcut array");
        _shortcutArrayByName[_name] = [];
      }
      
      _shortcutArrayByName[_name].push(atg.keyboard.keyboardNavShortcuts[_countPrimary][_countSecondary]);
    }
  }
  
  for(var _countList in _shortcutArrayByName) {
    if (_shortcutArrayByName[_countList].length > 0) {
      _sortedArray.add(_shortcutArrayByName[_countList][0].name, _shortcutArrayByName[_countList]);   
    }
  }

  for (var _i = 0; _i < _sortedArray.count; _i++) {
    _sortedArrayContents = _sortedArray.getByIndex(_i);
    _shortcutArrayByNameSorted.push(_sortedArrayContents);
  }
 
  return _shortcutArrayByNameSorted;
};


// -------------------------------------------------------------------
// Return the currently-selected top level node
// -------------------------------------------------------------------
atg.keyboard.getCurrentIdentifier = function (_className) {
  var node = atg.keyboard.keyboardNavCurrentNode; 
  
  while(node){
    
    /* ***
    console.debug("Keyboard Nav | Class name is: " + dojo.getClass(node));
    if (dojo.getClass(node).match(_className) != null) {
      return node;
    }
    */
    
    console.debug("Keyboard Nav | Class name is: " + node.className);
    if (node.className != null) {
      if (node.className.match(_className) != null) {
        return node;
      }
    }
    node = node.parentNode;
  }
  return null;
};

// -------------------------------------------------------------------
// Retrieving top level navigation objects
// -------------------------------------------------------------------
atg.keyboard.getTopLevelIdentifiers = function () {
  return dojo.query("." + atg.keyboard._classTopLevelIdentifier);
};

// -------------------------------------------------------------------
// Retrieving panel navigation objects
// -------------------------------------------------------------------
atg.keyboard.getPanelIdentifiers = function () {
  return dojo.query("." + atg.keyboard._classPanelIdentifier);
};

// -------------------------------------------------------------------
// Navigate to next top level object
// -------------------------------------------------------------------
atg.keyboard.navigateToNextTopLevelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to next top level identifier");
  var _currentTopLevelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classTopLevelIdentifier);
  var _allTopLevelIdentifiers = atg.keyboard.getTopLevelIdentifiers();
  var _nextTopLevelIdentifier;

  console.debug("Keyboard Nav | _allTopLevelIdentifiers = " + _allTopLevelIdentifiers);

  if ((_allTopLevelIdentifiers != null) && (_allTopLevelIdentifiers.length > 0)) {

    if (_currentTopLevelIdentifier == null) {
      _nextTopLevelIdentifier = _allTopLevelIdentifiers[0];
    }
    else {
      for (var _i=0; _i<_allTopLevelIdentifiers.length; _i++) {
        if (_currentTopLevelIdentifier == _allTopLevelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          if (_i == _allTopLevelIdentifiers.length - 1) {
            console.debug("Keyboard Nav | Wrapping around to first node");
            _nextTopLevelIdentifier = _allTopLevelIdentifiers[0];
          }
          else {
            _nextTopLevelIdentifier = _allTopLevelIdentifiers[_i+1];
          }
        }
      }
    }
    
    _nextTopLevelIdentifier.focus();
    atg.keyboard.fireEventTab(_nextTopLevelIdentifier);
    atg.keyboard.highlightAndFadePanel(_nextTopLevelIdentifier);
  }
};

// -------------------------------------------------------------------
// Navigate to previous top level object
// -------------------------------------------------------------------
atg.keyboard.navigateToPreviousTopLevelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to previous top level identifier");
  var _currentTopLevelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classTopLevelIdentifier);
  var _allTopLevelIdentifiers = atg.keyboard.getTopLevelIdentifiers();
  var _previousTopLevelIdentifier;

  if ((_allTopLevelIdentifiers != null) && (_allTopLevelIdentifiers.length > 0)) {

    if (_currentTopLevelIdentifier == null) {
      _previousTopLevelIdentifier = _allTopLevelIdentifiers[_allTopLevelIdentifiers.length-1];
    }
    else {
      for (var _i=0; _i<_allTopLevelIdentifiers.length; _i++) {
        if (_currentTopLevelIdentifier == _allTopLevelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          
          if (_currentTopLevelIdentifier == _allTopLevelIdentifiers[0]) {
            console.debug("Keyboard Nav | Wrapping around to last node");
            _previousTopLevelIdentifier = _allTopLevelIdentifiers[_allTopLevelIdentifiers.length-1];
          }
          else {
            _previousTopLevelIdentifier = _allTopLevelIdentifiers[_i-1];
          }
        }
      }
    }
    
    _previousTopLevelIdentifier.focus();
    atg.keyboard.fireEventTab(_previousTopLevelIdentifier);
    atg.keyboard.highlightAndFadePanel(_previousTopLevelIdentifier);
  }
};

// -------------------------------------------------------------------
// Navigate to next panel object
// -------------------------------------------------------------------
atg.keyboard.navigateToNextPanelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to next panel");
  var _currentPanelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classPanelIdentifier);
  var _allPanelIdentifiers = atg.keyboard.getPanelIdentifiers();
  var _nextPanelIdentifier;

  console.debug("Keyboard Nav | Number of panels in rotation: " + _allPanelIdentifiers.length);
  console.debug("Keyboard Nav | Current panel is: " + _currentPanelIdentifier);
  
  if ((_allPanelIdentifiers != null) && (_allPanelIdentifiers.length > 0)) {

    if (_currentPanelIdentifier == null) {
      _nextPanelIdentifier = _allPanelIdentifiers[0];
    }
    else {
      
      for (var _i=0; _i<_allPanelIdentifiers.length; _i++) {
        if (_currentPanelIdentifier == _allPanelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          if (_i == _allPanelIdentifiers.length - 1) {
            console.debug("Keyboard Nav | Wrapping around to first node");
            _nextPanelIdentifier = _allPanelIdentifiers[0];
          }
          else {
            _nextPanelIdentifier = _allPanelIdentifiers[_i+1];
          }
        }
      }
    }
  
    _nextPanelIdentifier.focus();
    atg.keyboard.fireEventTab(_nextPanelIdentifier);
    atg.keyboard.highlightAndFadePanel(_nextPanelIdentifier);
  }
};

// -------------------------------------------------------------------
// Navigate to previous panel object
// -------------------------------------------------------------------
atg.keyboard.navigateToPreviousPanelIdentifier = function () {
  console.debug("Keyboard Nav | Navigating to previous panel identifier");
  var _currentPanelIdentifier = atg.keyboard.getCurrentIdentifier(atg.keyboard._classPanelIdentifier);
  var _allPanelIdentifiers = atg.keyboard.getPanelIdentifiers();
  var _previousPanelIdentifier;

  if ((_allPanelIdentifiers != null) && (_allPanelIdentifiers.length > 0)) {
    if (_currentPanelIdentifier == null) {
      _previousPanelIdentifier = _allPanelIdentifiers[_allPanelIdentifiers.length-1];
    }
    else {
      for (var _i=0; _i<_allPanelIdentifiers.length; _i++) {
        if (_currentPanelIdentifier == _allPanelIdentifiers[_i]) {
          console.debug("Keyboard Nav | Node match: (" + _i + ")");
          
          if (_currentPanelIdentifier == _allPanelIdentifiers[0]) {
            console.debug("Keyboard Nav | Wrapping around to last node");
            _previousPanelIdentifier = _allPanelIdentifiers[_allPanelIdentifiers.length-1];
          }
          else {
            _previousPanelIdentifier = _allPanelIdentifiers[_i-1];
          }
        }
      }
    }
    
    _previousPanelIdentifier.focus();
    atg.keyboard.fireEventTab(_previousPanelIdentifier);
    atg.keyboard.highlightAndFadePanel(_previousPanelIdentifier);
  }
};

// -------------------------------------------------------------------
// Explicitly fire tab to position cursor on first usable entity
// -------------------------------------------------------------------
atg.keyboard.fireEventTab = function(_selectedNode) {
  /*
  if ((_selectedNode != null) && (_selectedNode.style != null)) {
    console.debug("Keyboard Nav | Firing Tab Event");

    if (window.KeyEvent) {
      console.debug("Keyboard Nav | window.KeyEvent is true - calling KeyEvents");
      var evObj = document.createEvent('KeyEvents');
      evObj.initKeyEvent ('keypress', true, true, window, false, false, false, false, 112, 112);
    } 
    else {
      console.debug("Keyboard Nav | window.KeyEvent is false - calling UIEvents");
      var evObj = document.createEvent('UIEvents');
      evObj.initUIEvent ('keypress', true, true, window, 1);
      evObj.keyCode = 13;
    }
    _selectedNode.dispatchEvent(evObj);
  }
  */
};

// -------------------------------------------------------------------
// Highlight and fade specified panel during panel navigation
// -------------------------------------------------------------------
atg.keyboard.highlightAndFadePanel = function(_selectedNode) {
  if (atg.keyboard._highlightAndFadePanels == true) {
    if ((_selectedNode != null) && (_selectedNode.style != null)) {
      dojo.addClass(_selectedNode, "atg_keyboard_panelHighlight");
      setTimeout(function(){
         dojo.removeClass(_selectedNode, "atg_keyboard_panelHighlight");
      },800);
    }
  }
};

// -------------------------------------------------------------------
// Highlight and fade DOM node for visual effect while tabbing
// -------------------------------------------------------------------
atg.keyboard.highlightAndFade = function(_selectedNode) {
  if (atg.keyboard._highlightAndFadeNodes == true) {
    if ((_selectedNode != null) && (_selectedNode.style != null)) {
      dojo.addClass(_selectedNode, "atg_keyboard_notificationHighlight");
      setTimeout(function(){
         dojo.removeClass(_selectedNode, "atg_keyboard_notificationHighlight");
      },600);
    }
  }
};


/*
 * This could be an element or a widget - whatever it is it must be connect-able to a dojo event
 */
atg.keyboard.findInputElement = function(input) {
  var inputElement = null;
  
  // TODO what can be used for isNode?
  if (false /*dojo.isNode(input)*/) {
    inputElement = input;
  }
  else if (dojo.isString(input)) {
    inputElement = dijit.byId(input);
    if (!inputElement) {
      inputElement = dojo.byId(input);
    }
  }
  else if (dojo.isObject(input)) {
    // TODO widgetType doesn't exist any more
    if (input instanceof dijit._Widget) { // input is a widget
      inputElement = input;
    }
    else if (input.form && input.name) {
      inputElement = dojo.byId(input.form)[input.name];
    }
  }
  return inputElement;
};
/*
 * Same as findInputElement, but allows a form to be specified as the action element.
 * If a form is specified, the form is submitted by calling submit() on the form
 * when the action occurs.
 */
atg.keyboard.findActionElement = function(obj) {
  var actionElement = atg.keyboard.findInputElement(obj);
  if (!actionElement && dojo.isObject(obj) && obj.form) {
    // Action element is a form element to submit
    actionElement = dojo.byId(obj.form);
  }
  return actionElement;
};
/*
 * Associates every element in a form with the default enter key activation behavior.
 * Unlike the registerDefaultEnterKey function, this function can only associate elements
 * (not widgets). However, the target element on which the action CAN be a widget (or element).
 */
atg.keyboard.registerFormDefaultEnterKey = function(_formId, actionRef, actionFunc /*optional*/, actionParams /*optional*/) {
  console.debug("Keyboard Nav | Keyboard Nav | Default enter key registered for form id " + _formId + " and action point " + actionRef);
  var theForm = dojo.byId(_formId);
  var i;
  var _defaultInputObject;
  var keyboardEvents = [];
  if (theForm) {
    for (i = 0; i < theForm.elements.length; i++) {
      _defaultInputObject = theForm.elements[i];
      var actionElement = atg.keyboard.findActionElement(actionRef);
      function handleFormDefaultEnterKey(e) {
        atg.keyboard.activateDefaultEnterKey(actionElement, actionFunc, actionParams, e);
      }
      if(dojo.isIE || dojo.isSafari) {
        keyboardEvents.push(dojo.connect(_defaultInputObject, "onkeyup", handleFormDefaultEnterKey));
      }
      else {
        keyboardEvents.push(dojo.connect(_defaultInputObject, "onkeypress", handleFormDefaultEnterKey));
      }
    }
    theForm.atgKeyboardEvents = keyboardEvents;
  }
};
atg.keyboard.unRegisterFormDefaultEnterKey = function(_formId) {
  var theForm = dojo.byId(_formId);
  if (theForm) {
    var keyboardEvents = theForm.atgKeyboardEvents;
    if (keyboardEvents) {
      console.debug("Keyboard Nav | Default enter key unregistered for form id " + _formId);
      dojo.forEach(keyboardEvents, dojo.disconnect);
      theForm.atgKeyboardEvents = null;
    }
  }
};
/**
 * Associates an element or widget
 * The following are valid for the input reference:
 * 1) Any node (use this if the dojo widget won't support a direct event connection)
 * 2) A widget ID string (dojo handles connection between widget and event - widget must support this)
 * 3) A DOM ID string
 * 4) A dojo widget - see note above
 * 5) An object specifying a form ID - name pair ({form:'myFormId',name:'myElementName'})
 * 
 * Valid inputs for the action reference:
 * 1) Anything that is valid for the input element plus:
 * 2) An object specifying a form by ID ({form:'myFormId'})
 * 
 * Valid inputs for the optional action function:
 * 1) empty/null (default behavior performed on the element)
 * 2) A string corresponding to a function on the submit element
 * 3) Not implemented at this time: A function reference to apply in the context of the submit element
 * 
 * Valid inputs for the optional action parameters
 * 1) empty/null (no parameters are passed in)
 * 2) Array of parameters to pass into the action function
 * 
 */
atg.keyboard.registerDefaultEnterKey = function(inputRef, actionRef, actionFunc /*optional*/, actionParams /*optional*/) {
  console.debug("Keyboard Nav | Default enter key registered for input point " + inputRef + " and action point " + actionRef);
  // this could be an element or a widget - whatever it is it must be connect-able to a dojo event
  var inputElement = atg.keyboard.findInputElement(inputRef);
  var actionElement = atg.keyboard.findActionElement(actionRef);
  if(inputElement&&actionElement){
    var keyboardEvent = null;
    function handleDefaultEnterKey(e) {
      atg.keyboard.activateDefaultEnterKey(actionElement, actionFunc, actionParams, e);
    }
    if(dojo.isIE || dojo.isSafari) {
      keyboardEvent = dojo.connect(inputElement, "onkeyup", handleDefaultEnterKey);
    }else {
      keyboardEvent = dojo.connect(inputElement, "onkeypress", handleDefaultEnterKey);
    }
    inputElement.atgKeyboardEvent = keyboardEvent;
  }
};

atg.keyboard.unRegisterDefaultEnterKey = function(inputRef) {
  console.debug("Keyboard Nav | Default enter key unregistered for input point " + inputRef);
  // this could be an element or a widget - whatever it is it must be connect-able to a dojo event
  var inputElement = atg.keyboard.findInputElement(inputRef);
  if(inputElement){
    var keyboardEvent = inputElement.atgKeyboardEvent;
    if(keyboardEvent){
      dojo.disconnect(keyboardEvent);
    }
    inputElement.atgKeyboardEvent = null;
  }
};
/**
 * Activates the submit button onclick or (if no onclick) submits a form if the enter key is pressed
 * Valid parameter:
 * 1) Button
 * 2) Input type=submit
 * 3) Widget
 * 4) Any other node inside a form
 * 5) A form node
 */
atg.keyboard.activateDefaultEnterKey = function(actionElement, actionFunc, actionParams, e) {
  if (!actionElement) return;

  if (atg.keyboard.isEnterPressed(e) == true) {
    console.debug("Keyboard Nav | activateDefaultEnterKey called for ", actionElement, actionFunc, actionParams, e);
    if (atg.keyboard.fireDefaultAction(actionElement, actionFunc, actionParams)) {
      e.cancelBubble = true;
      e.returnValue = false;
    
      if (e.stopPropagation) {
        e.stopPropagation();
        e.preventDefault();
      }
    }
  }
};
/**
 * Fires a default action, such as onclick or submitting a form, based on a passed in element or widget
 * Returns true if an action was performed to handle the event (that is, a form was submitted or a element click was called)
 * Returns false if no form could be identified to submit or no onclick event could be called (that is, if nothing happened)
 */
atg.keyboard.fireDefaultAction = function(element, func, params) {
  var isHandled = false;
  if (element instanceof dijit._Widget) {
    if (func && dojo.isFunction(element[func])) {
      console.debug("Keyboard Nav | Activating default enter key by calling function ", 
        func, " on widget ", element);
      params ? element[func](params) : element[func]();
      isHandled = true;
    }
    else {
      console.debug("Keyboard Nav | Unable to activate default enter key: Function ", func, " not valid on widget ", element);
    }
  }
  else {
    if (!element.nodeType || element.nodeType != 1) { // 1 == ELEMENT_NODE
      console.debug("Keyboard Nav | Unable to activate default enter key: Node ", element, " is not an element node");
      isHandled = false;
    }
    else {
      var tagName = String(element.tagName).toLowerCase();
      if (tagName == "form") {
        console.debug("Keyboard Nav | Activating default enter key by submitting form ", element);
        element.submit();
        isHandled = true;
      }
      else if (tagName == "button" || tagName == "input") {
        if (element.disabled != true) {
          if (!func) func = "onclick";
          if (dojo.isFunction(element[func])) {
            console.debug("Keyboard Nav | Activating default enter key by calling function ", 
              func, " on element ", element);
            params ? element[func](params) : element[func]();
            isHandled = true;
          }
          else {
            console.debug("Keyboard Nav | Unable to activate default enter key: Function ", func, " not valid on element ", element);
          }
        }
        else {
          console.debug("Keyboard Nav | Unable to activate default enter key: Element ", element, " is disabled");
        }
      }
      else {
        console.debug("Keyboard Nav | Unable to activate default enter key: Unknown tag name ", tagName);
      }
    }
  }
  return isHandled;
};

// -------------------------------------------------------------------
// Prevents automatic form submission from a given input control
// -------------------------------------------------------------------
atg.keyboard.blockDefaultEnterKey = function(_defaultInputId) {
  var _submitTrigger = dojo.byId(_defaultInputId);
  
  if(dojo.isIE || dojo.isSafari) {
    dojo.connect(_submitTrigger, "onkeyup", atg.keyboard.preventDefaultEnterKey);
  }
  else {
    dojo.connect(_submitTrigger, "onkeypress", atg.keyboard.preventDefaultEnterKey);
  }
};

// -------------------------------------------------------------------
// Prevents the submit button onclick if the enter key is pressed
// -------------------------------------------------------------------
atg.keyboard.preventDefaultEnterKey = function(e) {
  if (atg.keyboard.isEnterPressed(e) == true) {
    console.debug("Keyboard Nav | Preventing default enter key");
    
    e.cancelBubble = true;
    e.returnValue = false;
  
    if (e.stopPropagation) {
      e.stopPropagation();
      e.preventDefault();
    }
  }
  return true;
};


// -------------------------------------------------------------------
// Determines the current panel and then calls the dock/undock feature, if applicable
// -------------------------------------------------------------------
atg.keyboard.dockUndockCurrentPanel = function() {
  console.debug("Keyboard Nav | Calling atg.keyboard.dockUndockCurrentPanel");
  var _currentPanel = atg.keyboard.getCurrentPanel();
  
  if (_currentPanel != null) {
    // Get panelHeader
    var _navigateNodes = _currentPanel.childNodes[0].childNodes[0];
    console.debug("Keyboard Nav | 1 - Current navigating node is: " + _navigateNodes);
    console.debug("Keyboard Nav | 1 - Class is: " + _navigateNodes.className);
    
    if (_navigateNodes != null) {
      // Get panelIcons
      var _navigateNodes = _navigateNodes.childNodes[1];
      console.debug("Keyboard Nav | 2 - Current navigating node is: " + _navigateNodes);
      console.debug("Keyboard Nav | 2 - Class is: " + _navigateNodes.className);
      
      if (_navigateNodes != null) {
        // Get dock/undock image
        var _navigateNodes = _navigateNodes.childNodes[0].childNodes[0].childNodes[0];
        console.debug("Keyboard Nav | 3 - Current navigating node is: " + _navigateNodes);
        console.debug("Keyboard Nav | 3 - Class is: " + _navigateNodes.className);
        
        if (_navigateNodes != null) {
          //Calling onclick
          console.debug("Keyboard Nav | Calling node onclick: ");
          _navigateNodes.onclick();
        }
      }
    }
    
  }
}


// -------------------------------------------------------------------
// Determines the current panel and then calls the minimize/restore feature, if applicable
// -------------------------------------------------------------------
atg.keyboard.minimizeRestoreCurrentPanel = function() {
  console.debug("Keyboard Nav | Calling atg.keyboard.minimizeRestoreCurrentPanel");
  var _currentPanel = atg.keyboard.getCurrentPanel();

  if (_currentPanel != null) {
    // Get panelHeader
    var _navigateNodes = _currentPanel.childNodes[0].childNodes[0];
    console.debug("Keyboard Nav | 1 - Current navigating node is: " + _navigateNodes);
    console.debug("Keyboard Nav | 1 - Class is: " + _navigateNodes.className);
    
    if (_navigateNodes != null) {
      // Get panelIcons
      var _navigateNodes = _navigateNodes.childNodes[1];
      console.debug("Keyboard Nav | 2 - Current navigating node is: " + _navigateNodes);
      console.debug("Keyboard Nav | 2 - Class is: " + _navigateNodes.className);
      
      if (_navigateNodes != null) {
        // Get minimize/restore image
        var _navigateNodes = _navigateNodes.childNodes[0].childNodes[1].childNodes[0];
        console.debug("Keyboard Nav | 3 - Current navigating node is: " + _navigateNodes);
        console.debug("Keyboard Nav | 3 - Class is: " + _navigateNodes.className);
        
        if (_navigateNodes != null) {
          //Calling onclick
          console.debug("Keyboard Nav | Calling node onclick: ");
          _navigateNodes.onclick();
        }
      }
    }

  }
}

// -------------------------------------------------------------------
// Retrieves the panel Id for the current panel, if available. If no 
// panel is set to current, the first available panel will be returned, 
// or null if no panels exist on the page.
// -------------------------------------------------------------------
atg.keyboard.getCurrentPanel = function() {
  console.debug("Keyboard Nav | Calling atg.keyboard.getCurrentPanel");
  console.debug("Keyboard Nav | Current Node = " + atg.keyboard.keyboardNavCurrentNode);
  _currentNode = atg.keyboard.keyboardNavCurrentNode;
  _parentNode = _currentNode;
  
  while ((_parentNode != null) && (_parentNode.className != atg.keyboard._panelTitleIdentifier)) {
    _parentNode = _parentNode.parentNode;
   console.debug("Keyboard Nav | _parentNode = " + _parentNode.className); 
  }
  
  if (_parentNode != null) {
    console.debug("Keyboard Nav | Found node for panel."); 
    return _parentNode;
  }
  
  return null;
 
}