// -------------------------------------------------------------------
// Imports
// -------------------------------------------------------------------
dojo.provide("atg.ea");

// -------------------------------------------------------------------
// EA initialization
// -------------------------------------------------------------------
atg.helpFields = new Array();
atg.helpContent={ 
  "helpItem": new Array()
};

// -------------------------------------------------------------------
// EA help array registration
// -------------------------------------------------------------------
atg.ea.registerHelpArray = function (_helpArrayObject) {
  //console.debug("EA | Registering help array for object: " + _helpArrayObject.id);
  atg.helpFields.push(_helpArrayObject);
}

// -------------------------------------------------------------------
// EA help content registration
// -------------------------------------------------------------------
atg.ea.registerHelpContent = function (_helpContentObject) {
  //console.debug("EA | Registering help content for object: " + _helpContentObject.id);
  atg.helpContent.helpItem.push(_helpContentObject);
};


// -------------------------------------------------------------------
// EA tooltip registration
// -------------------------------------------------------------------
atg.ea.registerTooltip = function (_tooltipId, _tooltipText) {
  //console.debug("EA | Registering tooltip for object with id = " + _tooltipId);
  tooltipElement = dojo.byId(_tooltipId);
  if (tooltipElement != null) {
    tooltipElement.title = _tooltipText;
  }
};
